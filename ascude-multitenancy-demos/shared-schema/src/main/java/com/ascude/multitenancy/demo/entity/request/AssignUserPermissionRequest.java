package com.ascude.multitenancy.demo.entity.request;

import com.ascude.multitenancy.demo.util.MessageConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignUserPermissionRequest {

    @NotNull(message = MessageConstants.User.ASSIGN_USER_ID_EMPTY)
    private Long userId;

    private List<Long> permissionIds;
}
