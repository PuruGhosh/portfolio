package com.portfolio.gateway_security.representation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserDto extends UserDto {
    @NotNull private String username;
    @NotNull private String password;
    private boolean isAdmin;

    public UserDto toUserDto() {
        return UserDto.builder()
                .name(this.getName())
                .email(this.getEmail())
                .roles(this.getRoles())
                .bio(this.getBio())
                .skillTags(this.getSkillTags())
                .projectTags(this.getProjectTags())
                .build();
    }
}
