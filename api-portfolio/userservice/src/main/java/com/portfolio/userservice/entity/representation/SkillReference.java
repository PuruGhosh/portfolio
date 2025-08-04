package com.portfolio.userservice.entity.representation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillReference {
    UUID skillId;
    Competency competency;
}
