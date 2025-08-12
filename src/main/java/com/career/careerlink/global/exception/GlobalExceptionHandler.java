package com.career.careerlink.global.exception;

import com.career.careerlink.common.response.ErrorCode;
import com.career.careerlink.common.response.ResponseResult;
import com.career.careerlink.common.response.ResponseResultHeader;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ResponseResult<Void>> write(ErrorCode code, String msg) {
        ResponseResultHeader header = ResponseResultHeader.builder()
                .result(false)
                .message((msg == null || msg.isBlank()) ? code.getMessage() : msg)
                .code(code.name())
                .build();
        ResponseResult<Void> body = ResponseResult.<Void>builder()
                .header(header)
                .build();
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // 커스텀: 원하는 메시지로 내려보낼 때 사용
    @ExceptionHandler(CareerLinkException.class)
    public ResponseEntity<ResponseResult<Void>> handleCareer(CareerLinkException e) {
        return write(e.getCode(), e.getMessage());
    }

    // 404
    @ExceptionHandler({
            EntityNotFoundException.class,
            NoSuchElementException.class,
            EmptyResultDataAccessException.class
    })
    public ResponseEntity<ResponseResult<Void>> handleNotFound(Exception e) {
        return write(ErrorCode.DATA_NOT_FOUND, null);
    }

    // 400
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            jakarta.validation.ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ResponseResult<Void>> handleBadRequest(Exception e) {
        return write(ErrorCode.INVALID_REQUEST, null);
    }

    // 401(에러구분백업용)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseResult<Void>> handleUnauthorized(Exception e) {
        return write(ErrorCode.UNAUTHORIZED, null);
    }

    // 403(에러구분백업용)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseResult<Void>> handleForbidden(Exception e) {
        return write(ErrorCode.FORBIDDEN, null);
    }

    // 409 (키 중복 등)
    @ExceptionHandler({
            DataIntegrityViolationException.class,
            org.hibernate.exception.ConstraintViolationException.class
    })
    public ResponseEntity<ResponseResult<Void>> handleConflict(Exception e) {
        return write(ErrorCode.DUPLICATE_RESOURCE, null);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult<Void>> handleEtc(Exception e) {
        log.error("SYSTEM_ERROR", e);
        return write(ErrorCode.SYSTEM_ERROR, null);
    }
}
