package org.cv.moa.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SeekerProfileUpdateRequest {
    private String nickname;
    private String position;
    private int careerYear;
    private String previousCompany;
    private String techStacks;
    private String bio;
    private String portfolioUrl;
}
