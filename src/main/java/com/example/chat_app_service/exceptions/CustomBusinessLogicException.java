package com.example.chat_app_service.exceptions;

import com.example.chat_app_service.response.ResponseStatusEnum;

public class CustomBusinessLogicException  extends ApplicationException{

    private static final long serialVersionUID = -1605187590106478545L;

    public CustomBusinessLogicException(ResponseStatusEnum responseStatusEnum) {
        super(responseStatusEnum);
    }
}
