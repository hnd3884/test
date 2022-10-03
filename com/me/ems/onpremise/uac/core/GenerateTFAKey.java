package com.me.ems.onpremise.uac.core;

import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.adventnet.persistence.StandAlonePersistence;
import java.util.logging.Level;

public class GenerateTFAKey extends DisableTFA
{
    private GenerateTFAKey() {
    }
    
    public static void main(final String[] args) throws Exception {
        try {
            final GenerateTFAKey generateTFADisableKey = new GenerateTFAKey();
            generateTFADisableKey.startServer();
            final String userName = generateTFADisableKey.validateUserForAdminRole();
            if (userName != null) {
                generateTFADisableKey.printTFADisablingKey();
            }
        }
        catch (final Exception exception) {
            DisableTFA.showMsgInConsoleAndLog("\nUnable to create the disabling key.");
            GenerateTFAKey.logger.log(Level.SEVERE, "Exception in main method:- " + exception);
        }
        finally {
            new StandAlonePersistence().stopDB();
        }
        System.exit(0);
    }
    
    private void printTFADisablingKey() throws Exception {
        try {
            DisableTFA.showMsgInConsoleAndLog("\n------------------------------------------------------------------------ ");
            final String key = TFAUtil.getTFADisableKey();
            SecurityUtil.updateSecurityParameter("TFADisablingKey", key);
            DisableTFA.showMsgInConsoleAndLog("key is : " + key);
        }
        catch (final Exception exception) {
            GenerateTFAKey.logger.log(Level.SEVERE, "printTFADisablingKey():- " + exception);
            throw exception;
        }
    }
}
