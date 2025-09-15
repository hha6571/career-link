package com.career.careerlink.admin.user.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersRequestDto {
    private Integer page;
    private Integer size;
    private String sort;
    private String direction;
    private String keyword;
    private String role;
}
