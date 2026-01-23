package com.ascude.multitenancy.demo.entity.request;

import com.ascude.multitenancy.demo.util.MessageConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateTableConfigRequest extends BaseTableConfigRequest{

    @NotNull(message = MessageConstants.Validate.VALIDATE_ID_ERROR)
    private Long id;
}
