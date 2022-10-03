package com.me.mdm.webclient.formbean;

import java.util.UUID;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.config.PayloadProperty;
import org.json.JSONObject;

public class MDMWPEmailFormBean extends MDMDefaultFormBean
{
    @Override
    protected boolean getTransformedFormPropertyValue(final JSONObject multipleConfigForm, final JSONObject dynaFormData, final PayloadProperty payloadProperty) throws Exception {
        if (payloadProperty.name.equals("ACCOUNT_ICON_NAME")) {
            final boolean isAdded = dynaFormData.optBoolean("_IS_CONF_ADDED");
            final String accountIconPathSrc = dynaFormData.optString("ACCOUNT_ICON_PATH");
            if (accountIconPathSrc == null || accountIconPathSrc.equals("")) {
                return isAdded;
            }
            final String fileName = ApiFactoryProvider.getFileAccessAPI().getFileName(accountIconPathSrc);
            final boolean fileUploaded = ProfileUtil.getInstance().uploadProfileImageFile(accountIconPathSrc, ProfileUtil.getProfileEmailAccountIconFolderPath(), fileName);
            if (fileUploaded) {
                payloadProperty.value = fileName;
                return true;
            }
            throw new Exception("Unable to upload Account Icon file - " + fileName);
        }
        else {
            if (!payloadProperty.name.equals("GUID")) {
                return true;
            }
            if (((String)payloadProperty.value).equals("")) {
                payloadProperty.value = UUID.randomUUID().toString();
                return true;
            }
            return false;
        }
    }
}
