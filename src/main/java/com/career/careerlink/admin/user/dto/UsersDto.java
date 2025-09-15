package com.career.careerlink.admin.user.dto;

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
