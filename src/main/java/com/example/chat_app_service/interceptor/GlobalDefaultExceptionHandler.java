package com.example.chat_app_service.interceptor;

import com.example.chat_app_service.exceptions.CustomBusinessLogicException;
import com.example.chat_app_service.response.GeneralResponse;
import com.example.chat_app_service.response.ResponseFactory;
import com.example.chat_app_service.response.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<GeneralResponse<Object>> defaultExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, ResponseStatusEnum.UNKNOWN_ERROR);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<GeneralResponse<Object>> missingRequestParamHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, ResponseStatusEnum.NOT_ENOUGH_PARAM);
    }

    @ExceptionHandler(value = CustomBusinessLogicException.class)
    @ResponseBody
    public ResponseEntity<GeneralResponse<Object>> businessExceptionHandler(CustomBusinessLogicException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getResponseStatusEnum());
    }
}
