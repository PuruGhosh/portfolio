package com.portfolio.userservice.entity.representation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tags {
    private List<String> skillTags;
    private List<String> projectTags;
}
