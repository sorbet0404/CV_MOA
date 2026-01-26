package org.cv.moa.domain.user.dto;

import lombok.Data;

@Data
public class CompanyInfoUpdateRequest {
    private String companyName;
    private String industry;
    private Double rating;
    private String companyDesc;
    private String companyPros;
    private String companyCons;
    private String companyLogoUrl;
}
