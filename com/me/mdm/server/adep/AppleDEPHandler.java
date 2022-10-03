package com.me.mdm.server.adep;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.DBUtil;

public class AppleDEPHandler
{
    Long tokenId;
    Long customerId;
    
    public AppleDEPHandler(final Long tokenID) {
        this.tokenId = tokenID;
        this.customerId = getCustomerIdToToken(tokenID);
    }
    
    public AppleDEPHandler(final Long tokenID, final Long customerId) {
        this.tokenId = tokenID;
        this.customerId = customerId;
    }
    
    private static Long getCustomerIdToToken(final Long tokenID) {
        try {
            return (Long)DBUtil.getValueFromDB("DEPTokenDetails", "DEP_TOKEN_ID", (Object)tokenID, "CUSTOMER_ID");
        }
        catch (final Exception ex) {
            Logger.getLogger(AppleDEPHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
