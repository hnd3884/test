package com.me.mdm.onpremise.api.metainfo;

import com.me.ems.onpremise.uac.core.UserManagementUtil;
import org.json.JSONObject;
import com.me.mdm.api.metainfo.UserMetaImpl;

public class MDMPUserMetaImpl extends UserMetaImpl
{
    public JSONObject getUserMeta() {
        final JSONObject result = super.getUserMeta();
        if (result != null) {
            result.put("isPasswordChangeRequired", new UserManagementUtil().isPasswordChangeRequired());
        }
        return result;
    }
}
