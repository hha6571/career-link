package com.career.careerlink.common.controller;

import com.career.careerlink.common.response.ResponseResult;
import com.career.careerlink.common.response.ResponseResultHeader;
import com.career.careerlink.common.response.ServiceResult;
import org.springframework.http.ResponseEntity;

public class BaseController {

    protected ResponseEntity<ResponseResult> result(ServiceResult serviceResult) {
        return ResponseEntity.ok(
                ResponseResult.builder()
                        .header(ResponseResultHeader.builder()
                                .result(serviceResult.isResult())
                                .message(serviceResult.getMessage())
                                .build())
                        .body(serviceResult.getBody())
                        .pagination(serviceResult.getPagination())
                        .build()
        );
    }
}

