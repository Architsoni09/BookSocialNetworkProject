package archit.springboot.booksocialnetwork.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.Servers;
import jdk.jfr.Name;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Archit Soni",
                        email = "architsoni2908@gmail.com"
                ),
                description = "Open API documentation for Archit's Spring Boot Project",
                title = "Open API Specification- Archit",
                license = @License(
                        name = "Grave Walker"
                ),
                termsOfService = "Just for learning."
        ),
        servers = {@Server(
                description = "LOCAL ENVIRONMENT",
                url = "http://localhost:8080/api/v1"
        ),
        @Server(
                description = "PRODUCTION ENVIRONMENT",
                url = "http://localhost:8080/ONLY/FOR/LEARNING"
        )
        },
        security = {
                @SecurityRequirement(
                       name= "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authorization Description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
