package com.example.chat_app_service.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResponseStatus implements Serializable {

    private static final long serialVersionUID = 1216664062736095390L;

    private String code;

    private String message;

    public GeneralResponseStatus(ResponseStatusEnum responseStatusEnum) {
        this.code = responseStatusEnum.getCode();
        this.message = responseStatusEnum.getMessage();
    }
}

