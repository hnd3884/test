package com.me.mdm.server.enrollment.api.service;

import org.json.JSONObject;
import com.me.mdm.server.enrollment.api.model.LicensePercentNotificationUpdateModel;
import com.me.mdm.core.enrollment.settings.DeviceLicensePercentSettingsHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.mdm.server.enrollment.api.model.LicensePercentNotificationSettingsModel;
import com.me.mdm.api.model.BaseAPIModel;
import java.util.logging.Logger;

public class LicenseNotificationSettingsService
{
    private static Logger logger;
    
    public LicensePercentNotificationSettingsModel getLicensePercentNotificationSettings(final BaseAPIModel baseAPIModel) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        return (LicensePercentNotificationSettingsModel)mapper.readValue(DeviceLicensePercentSettingsHandler.getInstance().getDeviceLicensePercentSettingsWithEmail(baseAPIModel.getCustomerId(), baseAPIModel.getUserId()).toString(), (Class)LicensePercentNotificationSettingsModel.class);
    }
    
    public void updateLicensePercentNotificationSettings(final LicensePercentNotificationUpdateModel licensePercentNotificationUpdate) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final JSONObject requestJSON = new JSONObject(objectMapper.writeValueAsString((Object)licensePercentNotificationUpdate));
        DeviceLicensePercentSettingsHandler.getInstance().addOrUpdateDeviceLicensePercent(requestJSON);
    }
    
    public void deleteLicensePercentNotificationSettings(final BaseAPIModel baseAPIModel) throws Exception {
        DeviceLicensePercentSettingsHandler.getInstance().deleteDeviceLicensePercent(baseAPIModel.getCustomerId(), baseAPIModel.getUserId());
    }
    
    static {
        LicenseNotificationSettingsService.logger = Logger.getLogger("MDMEnrollment");
    }
}
