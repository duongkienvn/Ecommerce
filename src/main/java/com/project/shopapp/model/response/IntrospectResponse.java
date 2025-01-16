package com.project.shopapp.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class IntrospectResponse {
    private boolean isValid;
}
