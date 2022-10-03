package com.adventnet.authentication.radius;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.theorem.radius3.ClientSendException;
import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.RADIUSException;
import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.AttributeList;
import com.theorem.radius3.dictionary.RADIUSDictionary;
import com.theorem.radius3.dictionary.DefaultDictionary;
import com.theorem.radius3.RADIUSClient;
import java.net.InetAddress;
import com.adventnet.ds.query.Criteria;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.authentication.PAM;
import java.util.logging.Logger;
import com.adventnet.authentication.lm.Authenticator;

public class RadiusAuthenticator extends Authenticator
{
    static Logger logger;
    
    @Override
    public boolean authenticate() throws LoginException {
        boolean isSuccess = false;
        final String domain = this.request.getParameter(PAM.DOMAINNAME);
        if (domain != null && domain.equalsIgnoreCase("local")) {
            this.request.getSession().setAttribute("DOMAINNAME", (Object)"LOCAL");
            return super.authenticate();
        }
        try {
            this.accountDO = AuthDBUtil.getAccountDO(this.loginName, this.serviceName, domain);
            if (this.accountDO.containsTable("AaaAccount")) {
                isSuccess = this.radiusAuthenticate(this.loginName, this.password);
                this.request.getSession().setAttribute("DOMAINNAME", (Object)"RADIUS");
            }
            else {
                isSuccess = false;
            }
            if (!isSuccess) {
                throw new LoginException("Invalid loginName/password");
            }
            return isSuccess;
        }
        catch (final Exception e) {
            RadiusAuthenticator.logger.log(Level.FINE, "", e);
            throw new LoginException("Invalid loginName/password");
        }
    }
    
    public boolean radiusAuthenticate(final String name, final String password) {
        boolean flg = false;
        RADIUSClient r = null;
        InetAddress ourIP = null;
        InetAddress clientAddress = null;
        String radiusServer = null;
        String radiusSecret = "";
        final String clientId = "localhost";
        int port = 1812;
        int retries = 1;
        String authType = "PAP";
        try {
            final DataObject dobj = ((Persistence)BeanUtil.lookup("Persistence")).get("AAARadiusConfig", (Criteria)null);
            if (!dobj.containsTable("AAARadiusConfig")) {
                return false;
            }
            final Row radiusRow = dobj.getFirstRow("AAARadiusConfig");
            radiusServer = (String)radiusRow.get("SERVER_NAME");
            port = (int)radiusRow.get("SERVER_PORT");
            authType = (String)radiusRow.get("SERVER_PROTOCOL");
            radiusSecret = (String)radiusRow.get("SERVER_SECRET");
            retries = (int)radiusRow.get("RETRIES");
            final int timeOut = (int)((radiusRow.get("TIMEOUT") != null) ? radiusRow.get("TIMEOUT") : 10000);
            clientAddress = InetAddress.getLocalHost();
            r = new RADIUSClient(radiusServer, port, radiusSecret, timeOut);
            r.addDictionary((RADIUSDictionary)new DefaultDictionary());
            ourIP = InetAddress.getByName(clientAddress.getCanonicalHostName());
        }
        catch (final Exception e) {
            RadiusAuthenticator.logger.log(Level.SEVERE, "Radius failed: ", e);
        }
        r.setDebug(true);
        for (int l = 0; l < retries && !flg; ++l) {
            RadiusAuthenticator.logger.log(Level.FINEST, "\n---------------------------- Authentication (" + authType + ")----------------------------");
            int result = 0;
            try {
                final AttributeList aList = new AttributeList();
                if (clientAddress != null) {
                    aList.addAttribute(4, ourIP);
                }
                if (clientId != null) {
                    aList.addAttribute(32, clientId);
                }
                aList.addAttribute(5, 1);
                if (authType.equals("PAP")) {
                    RadiusAuthenticator.logger.log(Level.FINEST, "User name is ***** alist is=" + aList);
                    result = r.authenticate(name, password, aList);
                    final AttributeList responseList = r.getAttributes();
                    RadiusAuthenticator.logger.log(Level.FINEST, "User name is=*****");
                }
                else if (authType.equals("CHAP")) {
                    aList.addAttribute(1, name);
                    result = r.authenticate(password.getBytes(), aList);
                }
                else if (authType.equals("MSCHAP")) {
                    aList.addAttribute(1, Util.toUTF8(name));
                    try {
                        r.createMSCHAP(password.getBytes(), aList);
                    }
                    catch (final RADIUSException re) {
                        RadiusAuthenticator.logger.log(Level.SEVERE, "Can't access the DES encoding algorithm -:", (Throwable)re);
                    }
                    result = r.authenticate(aList);
                }
                else if (authType.equals("MSCHAP2")) {
                    aList.addAttribute(1, Util.toUTF8(name));
                    try {
                        r.createMSCHAP2(RADIUSClient.getBytes(name), RADIUSClient.getBytes(password), aList);
                    }
                    catch (final RADIUSException re) {
                        RadiusAuthenticator.logger.log(Level.FINEST, "Can't access the DES encoding algorithm -:", (Throwable)re);
                    }
                    result = r.authenticate(aList);
                }
                else {
                    RadiusAuthenticator.logger.log(Level.SEVERE, "Unknown authentication type: " + authType);
                }
                switch (result) {
                    case 3: {
                        RadiusAuthenticator.logger.log(Level.SEVERE, "Failed to authenticate");
                        break;
                    }
                    case 0: {
                        RadiusAuthenticator.logger.log(Level.SEVERE, "Received bad packet: " + r.getErrorString());
                        break;
                    }
                    case 11: {
                        RadiusAuthenticator.logger.log(Level.SEVERE, "Access was challenged. Can't handle this yet.");
                        break;
                    }
                    case 2: {
                        RadiusAuthenticator.logger.log(Level.INFO, "Authenticated");
                        flg = true;
                        break;
                    }
                }
            }
            catch (final ClientReceiveException cre) {
                RadiusAuthenticator.logger.log(Level.SEVERE, "Radius authentication failed: ", (Throwable)cre);
            }
            catch (final ClientSendException cse) {
                RadiusAuthenticator.logger.log(Level.SEVERE, "Radius authentication failed: ", (Throwable)cse);
            }
        }
        return flg;
    }
    
    static {
        RadiusAuthenticator.logger = Logger.getLogger(RadiusAuthenticator.class.getName());
    }
}
