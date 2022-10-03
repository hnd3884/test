package com.me.ems.onpremise.uac.core;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class TwoFactorAuthenticationUtil
{
    private static Logger logger;
    
    public static String getTwoFactorAuthType() throws Exception {
        String authType = "disabled";
        final Criteria twoFactorParamsCri = new Criteria(Column.getColumn("UserMgmtParams", "PARAMS_NAME"), (Object)"authType", 0);
        final DataObject twoFactorParamDO = SyMUtil.getPersistence().get("UserMgmtParams", twoFactorParamsCri);
        if (!twoFactorParamDO.isEmpty()) {
            final Row twoFactorParamRow = twoFactorParamDO.getFirstRow("UserMgmtParams");
            authType = (String)twoFactorParamRow.get("PARAMS_VALUE");
            TwoFactorAuthenticationUtil.logger.log(Level.FINE, "Enabled TwoFactor Type:" + authType);
        }
        return authType;
    }
    
    public static boolean isTwoFactorEnabledGlobaly() throws Exception {
        boolean isTwoFactorEnabled = false;
        final String authType = getTwoFactorAuthType();
        if (authType.equalsIgnoreCase("mail") || authType.equalsIgnoreCase("googleApp")) {
            isTwoFactorEnabled = true;
        }
        return isTwoFactorEnabled;
    }
    
    static {
        TwoFactorAuthenticationUtil.logger = Logger.getLogger("UserManagementLogger");
    }
}
