package com.me.mdm.core.ios.adep;

import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;

public class ADEPAuthHandler
{
    private static ADEPAuthHandler adepAuthHandler;
    
    public static ADEPAuthHandler getInstance() {
        try {
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS() && ADEPAuthHandler.adepAuthHandler == null) {
                ADEPAuthHandler.adepAuthHandler = (ADEPAuthHandler)Class.forName("com.me.mdm.onpremise.server.enrollment.authentication.ADEPActiveDirectoryAuthHandler").newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            Logger.getLogger(ADEPAuthHandler.class.getName()).log(Level.SEVERE, "ClassNotFoundException  during Instantiation for ADEPAuthHandler... ", ce);
        }
        catch (final InstantiationException ie) {
            Logger.getLogger(ADEPAuthHandler.class.getName()).log(Level.SEVERE, "InstantiationException During Instantiation  for ADEPAuthHandler...", ie);
        }
        catch (final IllegalAccessException ie2) {
            Logger.getLogger(ADEPAuthHandler.class.getName()).log(Level.SEVERE, "IllegalAccessException During Instantiation  for ADEPAuthHandler...", ie2);
        }
        catch (final Exception ex) {
            Logger.getLogger(ADEPAuthHandler.class.getName()).log(Level.SEVERE, "Exception During Instantiation  for ADEPAuthHandler...", ex);
        }
        return ADEPAuthHandler.adepAuthHandler;
    }
    
    public JSONObject authenticate(final JSONObject msgRequestJSON) {
        return null;
    }
    
    public void getAuthenticatedUserDetails(final JSONObject json) {
    }
    
    static {
        ADEPAuthHandler.adepAuthHandler = null;
    }
}
