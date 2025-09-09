package com.career.careerlink.admin.dto;

import lombok.*;

@Getter
@Setter
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
