package com.me.mdm.server.enrollment.api.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LicensePercentNotificationSettingsModel
{
    @JsonProperty("min_threshold")
    private int minThreshold;
    @JsonProperty("max_threshold")
    private int maxThreshold;
    @JsonProperty("alert_email_ids")
    private List<LicenseLimitAlertEmailListModel> licenseLimitAlertEmailListModelList;
}
