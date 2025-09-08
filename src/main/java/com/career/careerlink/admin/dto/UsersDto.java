package com.career.careerlink.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersDto {
    private String userName;
    private String loginId;
    private String email;
    private String role;
    private String userPk;
    private String userStatus;

}
