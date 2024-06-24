package archit.springboot.booksocialnetwork.Service;

import archit.springboot.booksocialnetwork.Dto.AuthenticationRequest;
import archit.springboot.booksocialnetwork.Dto.AuthenticationResponse;
import archit.springboot.booksocialnetwork.Dto.RegistrationRequest;
import archit.springboot.booksocialnetwork.Email.EmailService;
import archit.springboot.booksocialnetwork.Email.EmailTemplate;
import archit.springboot.booksocialnetwork.Entity.Role;
import archit.springboot.booksocialnetwork.Entity.Token;
import archit.springboot.booksocialnetwork.Entity.User;
import archit.springboot.booksocialnetwork.Exceptions.ActivationTokenException;
import archit.springboot.booksocialnetwork.Repository.RoleRepository;
import archit.springboot.booksocialnetwork.Repository.TokenRepository;
import archit.springboot.booksocialnetwork.Repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    @Value("${application.security.mailing.frontend.activation-url}")
    private String activationUrl;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;



    public void register(RegistrationRequest request)  throws MessagingException {
        Role role= roleRepository.findByRoleName("USER").orElseThrow(()->new RuntimeException("Role not found"));
        if(userRepository.findByEmail(request.getEmail()).isEmpty()) {
            User user = User.builder()
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .accountLocked(false)
                    .enabled(false)
                    .roles(List.of(role))
                    .build();
            userRepository.save(user);
            sendValidationEmail(user);
        }
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String token = generateAndSaveActivationToken(user);
        emailService.sendEmail(user.getEmail(),user.getFullName(), EmailTemplate.ACTIVATE_ACCOUNT,
                activationUrl,token,"Activation of Account");
    }

    private String generateAndSaveActivationToken(User user) {
        String activationCode=generateActivationCode(6);
        Token token= Token.builder().token(activationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();
        tokenRepository.save(token);
        return activationCode;
        }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
       var auth= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
        var claims=new HashMap<String,Object>();
        var user=((User)auth.getPrincipal());
        claims.put("fullName",user.fullName());
        return AuthenticationResponse.builder().jwtToken(jwtService.generateToken(claims,user)).build();
    }

    public void activateAccount(String token) throws MessagingException {
        Token token1=tokenRepository.findByToken(token).orElseThrow(()->new ActivationTokenException("Token not found"));
        if(LocalDateTime.now().isAfter(token1.getExpiresAt())){
            sendValidationEmail(token1.getUser());
            throw new RuntimeException("Activation Token has expired and a new token has been sent again");
        }
        else{
            User user=userRepository.findById(token1.getUser().getId()).orElseThrow(()->new UsernameNotFoundException(" user not found"));
            user.setEnabled(true);
            token1.setUser(user);
            token1.setValidatedAt(LocalDateTime.now());
            userRepository.save(user);
            tokenRepository.save(token1);
        }
    }
}
