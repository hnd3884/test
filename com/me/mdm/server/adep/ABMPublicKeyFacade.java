package com.me.mdm.server.adep;

import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ABMPublicKeyFacade
{
    private static ABMPublicKeyFacade facadeObj;
    public static Logger logger;
    
    public static ABMPublicKeyFacade getInstance() {
        if (ABMPublicKeyFacade.facadeObj == null) {
            ABMPublicKeyFacade.facadeObj = new ABMPublicKeyFacade();
        }
        return ABMPublicKeyFacade.facadeObj;
    }
    
    public String getPublicKeyPathForDEPToken(final JSONObject apiRequestJson) throws Exception {
        try {
            final Long customerID = APIUtil.getCustomerID(apiRequestJson);
            final Long tokenId = Long.valueOf(String.valueOf(apiRequestJson.getJSONObject("msg_header").getJSONObject("resource_identifier").get("appledepserver_id")));
            ABMAuthTokenFacade.validateIfDepTokenExists(tokenId, customerID);
            return AppleDEPCertificateHandler.getInstance().getDEPTokenPublicKeyPath(customerID, tokenId);
        }
        catch (final Exception ex) {
            ABMPublicKeyFacade.logger.log(Level.SEVERE, "Exception while getting DEP config file", ex);
            throw ex;
        }
    }
    
    static {
        ABMPublicKeyFacade.facadeObj = null;
        ABMPublicKeyFacade.logger = Logger.getLogger("MDMEnrollment");
    }
}
