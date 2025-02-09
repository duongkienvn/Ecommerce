package com.project.shopapp.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING
            , pattern = "yyyy-MM-dd HH:mm:ss"
            , timezone = "Asia/Ho_Chi_Minh")
    @JsonProperty("created_at")
    private Date created_at;

    @JsonFormat(shape = JsonFormat.Shape.STRING
            , pattern = "yyyy-MM-dd HH:mm:ss"
            , timezone = "Asia/Ho_Chi_Minh")
    @JsonProperty("updated_at")
    private Date updated_at;
}
