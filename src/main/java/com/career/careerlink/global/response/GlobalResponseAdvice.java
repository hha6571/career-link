    package com.career.careerlink.global.response;

    import com.career.careerlink.common.response.PaginationInfo;
    import com.career.careerlink.common.response.ResponseResult;
    import com.career.careerlink.common.response.ResponseResultHeader;
    import com.career.careerlink.common.response.SuccessCode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.RequiredArgsConstructor;
    import org.springframework.core.MethodParameter;
    import org.springframework.core.Ordered;
    import org.springframework.core.annotation.Order;
    import org.springframework.core.io.Resource;
    import org.springframework.data.domain.Page;
    import org.springframework.http.*;
    import org.springframework.http.converter.HttpMessageConverter;
    import org.springframework.http.converter.StringHttpMessageConverter;
    import org.springframework.http.server.*;
    import org.springframework.web.bind.annotation.RestControllerAdvice;
    import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
    import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
    import org.springframework.http.HttpMethod;


    @RestControllerAdvice
    @Order(Ordered.LOWEST_PRECEDENCE)
    @RequiredArgsConstructor
    public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

        private final ObjectMapper objectMapper;

        @Override
        public boolean supports(MethodParameter returnType,
                                Class<? extends HttpMessageConverter<?>> converterType) {

            // @SkipWrap 이면 래핑 스킵 (클래스/메서드 둘 다 체크)
            if (returnType.getContainingClass().isAnnotationPresent(SkipWrap.class)) return false;
            if (returnType.hasMethodAnnotation(SkipWrap.class)) return false;

            return true;
        }

        @Override
        public Object beforeBodyWrite(Object body,
                                      MethodParameter returnType,
                                      MediaType selectedContentType,
                                      Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                      ServerHttpRequest request,
                                      ServerHttpResponse response) {

            // 이미 감싼 경우는 그대로 통과
            if (body instanceof ResponseResult) return body;
            if (body instanceof ResponseEntity<?> re && re.getBody() instanceof ResponseResult) return body;

            // 파일/바이너리 류는 스킵
            if (body instanceof Resource
                    || body instanceof StreamingResponseBody
                    || body instanceof byte[]) {
                return body;
            }

            // HTTP 메서드 → SuccessCode 매핑
            HttpMethod method = request.getMethod();
            String m = method.name();

            SuccessCode sc = switch (m) {
                case "POST"          -> SuccessCode.CREATED;
                case "PUT", "PATCH"  -> SuccessCode.UPDATED;
                case "DELETE"        -> SuccessCode.DELETED;
                default              -> SuccessCode.OK;
            };



            // Page<?> → pagination + content 분리
            PaginationInfo pagination = null;
            Object actualBody = body;
            if (body instanceof Page<?> page) {
                pagination = PaginationInfo.builder()
                        .page(page.getNumber()) // 0-based면 유지
                        .size(page.getSize())
                        .totalPages(page.getTotalPages())
                        .totalElements(page.getTotalElements())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build();
                actualBody = page.getContent();
            }


            ResponseResultHeader header = ResponseResultHeader.builder()
                    .result(true)
                    .message(sc.getMessage())
                    .code(sc.name())
                    .build();

            ResponseResult<?> wrapped = ResponseResult.builder()
                    .header(header)
                    .body(actualBody)
                    .pagination(pagination)
                    .build();

            // 컨트롤러가 String을 반환하는 경우(JSON 직렬화 수동 처리)
            if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
                try {
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    return objectMapper.writeValueAsString(wrapped);
                } catch (Exception ignore) {
                    return body; // 실패시 원본 그대로
                }
            }

            // ResponseEntity<T> 를 반환한 경우: status/header는 이미 위에서 세팅했으므로 바디만 교체가 필요할 때
            if (body instanceof ResponseEntity<?> re) {
                return ResponseEntity.status(re.getStatusCode())
                        .headers(re.getHeaders())
                        .body(wrapped);
            }

            return wrapped;
        }
    }
