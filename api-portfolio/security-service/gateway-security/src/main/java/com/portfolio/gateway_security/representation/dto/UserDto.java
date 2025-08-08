package com.portfolio.gateway_security.representation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties
public class UserDto {

    private UUID id;
    @NotNull
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    private List<String> roles;
    private String bio;
    private List<String> skillTags;
    private List<String> projectTags;

    public static UserDto getSampleData() {
        return UserDto.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john.doe@example.com")
                .roles(List.of("USER", "ADMIN"))
                .bio("A passionate developer with a love for clean code.")
                .skillTags(List.of("Java", "Spring Boot", "React"))
                .projectTags(List.of("Portfolio", "E-commerce"))
                .build();
    }


}
