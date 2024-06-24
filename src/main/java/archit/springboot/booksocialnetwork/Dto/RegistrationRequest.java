package archit.springboot.booksocialnetwork.Dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "First name is required")
    @NotBlank(message = "No blank spaces should be there")
    private String firstName;
    @NotEmpty(message = "Last name is required")
    @NotBlank(message = "No blank spaces should be there")
    private String lastName;
    @NotBlank(message = "No blank spaces should be there")
    @Email(message = "Email is not well-formed")
    private String email;
    @NotBlank(message = "No blank spaces should be there")
    @NotEmpty(message = "Password name is required")
    @Size(min = 8,message = "Password should be at least 8 characters")
    private String password;
}

