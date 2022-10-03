package com.me.mdm.onpremise.server.user;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.simple.JSONObject;
import com.me.mdm.server.user.BaseManagedUserImportTask;

public class ManagedUserImportTask extends BaseManagedUserImportTask
{
    protected void performOperation(final JSONObject jsonObj) throws Exception {
        super.performOperation(jsonObj);
    }
    
    public void validateAndUpdateEmail(final String email, final String newEmail, final boolean isEmailEditEnabled) throws Exception {
        if (isEmailEditEnabled && newEmail != null && !email.equals(newEmail) && !MDMUtil.getInstance().isValidEmail(newEmail)) {
            throw new SyMException(53007, I18N.getMsg("dc.mdm.safe_device_mgmt.invalid_email", new Object[0]), (Throwable)null);
        }
    }
}
