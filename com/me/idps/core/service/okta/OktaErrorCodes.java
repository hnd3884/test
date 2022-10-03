package com.me.idps.core.service.okta;

import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.api.IdpsAPIException;

public class OktaErrorCodes
{
    public static final String E0000001 = "E0000001";
    public static final String E0000004 = "E0000004";
    public static final String E0000005 = "E0000005";
    public static final String E0000006 = "E0000006";
    public static final String E0000009 = "E0000009";
    public static final String E0000011 = "E0000011";
    public static final String E0000064 = "E0000064";
    public static final String E0000055 = "E0000055";
    public static final String E0000015 = "E0000015";
    
    public IdpsAPIException getOktaErrorCodes(final String s) {
        switch (s) {
            case "E0000001": {
                IDPSlogger.SOM.log(Level.SEVERE, "Okta API validation failed.");
                return new IdpsAPIException("AD008");
            }
            case "E0000004": {
                IDPSlogger.SOM.log(Level.SEVERE, "Okta Authentication failed.");
                return new IdpsAPIException("AD008");
            }
            case "E0000005": {
                IDPSlogger.SOM.log(Level.SEVERE, "Okta Invalid session.");
                return new IdpsAPIException("AD008");
            }
            case "E0000006": {
                IDPSlogger.SOM.log(Level.SEVERE, "Okta You do not have permission to perform the requested action.");
                return new IdpsAPIException("COM0013");
            }
            case "E0000009": {
                IDPSlogger.SOM.log(Level.SEVERE, "Okta Internal Server Error.");
                return new IdpsAPIException("COM0004");
            }
            case "E0000011": {
                IDPSlogger.SOM.log(Level.SEVERE, "Okta Invalid token provided");
                return new IdpsAPIException("AD008");
            }
            case "E0000064": {
                IDPSlogger.SOM.log(Level.SEVERE, "Okta Password is expired and must be changed.");
                return new IdpsAPIException("AD008");
            }
            case "E0000055": {
                IDPSlogger.SOM.log(Level.SEVERE, "Okta Duplicate group");
                return new IdpsAPIException("COM0014");
            }
            case "E0000015": {
                IDPSlogger.SOM.log(Level.SEVERE, "Okta You do not have permission to access the feature you are requesting.");
                return new IdpsAPIException("COM0013");
            }
            default: {
                return new IdpsAPIException("COM0014");
            }
        }
    }
}
