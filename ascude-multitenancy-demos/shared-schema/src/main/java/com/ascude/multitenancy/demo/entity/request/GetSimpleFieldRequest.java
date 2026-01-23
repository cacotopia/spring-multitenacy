package com.ascude.multitenancy.demo.entity.request;

import com.ascude.multitenancy.demo.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetSimpleFieldRequest {

    @NotBlank(message = MessageConstants.TableFieldConfig.SCHEMA_NAME_NOT_EMPTY)
    private String schemaName;

    @NotBlank(message = MessageConstants.TableFieldConfig.TABLE_NAME_NOT_EMPTY)
    private String tableName;

}
