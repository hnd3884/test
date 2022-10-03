package com.me.mdm.server.enrollment.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LicenseLimitAlertEmailListModel
{
    @JsonProperty("email")
    private String email;
    @JsonProperty("user_name")
    private String userName;
}
