package com.ascude.multitenancy.demo.entity.request;

import com.ascude.multitenancy.demo.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDictTypeRequest {

    @NotBlank(message = MessageConstants.Dict.DICT_OLD_TYPE_EMPTY)
    private String oldType;

    @NotBlank(message = MessageConstants.Dict.DICT_NEW_TYPE_EMPTY)
    private String newType;
}
