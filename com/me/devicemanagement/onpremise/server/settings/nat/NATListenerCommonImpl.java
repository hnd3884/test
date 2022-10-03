package com.me.devicemanagement.onpremise.server.settings.nat;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.util.logging.Logger;

public class NATListenerCommonImpl implements NATListener
{
    private Logger logger;
    
    public NATListenerCommonImpl() {
        this.logger = Logger.getLogger("ServerSettingsLogger");
    }
    
    @Override
    public String isNATUpdateSafe(final NATObject object) {
        String status = "update_safe";
        try {
            final boolean isThirdPartySSLInstalled = SSLCertificateUtil.getInstance().isThirdPartySSLInstalled();
            if (isThirdPartySSLInstalled) {
                final String givenNATAddress = object.givenNATAddress;
                final Boolean isValid = SSLCertificateUtil.getInstance().checkHostNameValidWithSSL(givenNATAddress);
                if (!isValid) {
                    status = SSLCertificateUtil.getInstance().getSSLCertificateHostNames().toString();
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in isNATUpdateSafe() ", e);
        }
        return status;
    }
    
    @Override
    public void natModified(final NATObject object) {
        try {
            if (object.givenNATAddress != null) {
                final Properties webServerProps = new Properties();
                webServerProps.setProperty("server.fqdn", object.givenNATAddress);
                WebServerUtil.storeProperWebServerSettings(webServerProps);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in natChanged() ", e);
        }
    }
    
    @Override
    public HashMap setValuesInNATForm(final HashMap dynaForm) {
        try {
            String natAddress = "";
            final Properties natProps = NATHandler.getNATConfigurationProperties();
            int natPort = 0;
            if (natProps.size() > 0) {
                natAddress = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
                natPort = ((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT");
            }
            final Properties serverInfo = SyMUtil.getDCServerInfo();
            if (natPort == 0) {
                final String port = ((Hashtable<K, Object>)serverInfo).get("HTTPS_PORT").toString();
                natPort = Integer.valueOf(port);
            }
            dynaForm.put("NAT_ADDRESS", natAddress);
            dynaForm.put("NAT_HTTPS_PORT", natPort);
            String secIP = ((Hashtable<K, String>)serverInfo).get("SERVER_SEC_IPADDR");
            if (secIP == null || secIP.equalsIgnoreCase("--") || secIP.trim().length() == 0) {
                secIP = ((Hashtable<K, String>)serverInfo).get("SERVER_FQDN");
            }
            if (natAddress == null || natAddress.equalsIgnoreCase("")) {
                dynaForm.put("NAT_ADDRESS", secIP);
            }
            dynaForm.put("SERVER_ADDRESS", ((Hashtable<K, String>)serverInfo).get("SERVER_MAC_IPADDR"));
            final String httpsPortNo = "" + ((Hashtable<K, Object>)serverInfo).get("HTTPS_PORT");
            dynaForm.put("HTTPS_PORT", new Integer(httpsPortNo));
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "DataAccessException in setValuesInNATForm() ", (Throwable)ex);
        }
        catch (final SyMException ex2) {
            this.logger.log(Level.SEVERE, "SyMException in setValuesInNATForm() ", (Throwable)ex2);
        }
        return dynaForm;
    }
    
    @Override
    public NATObject getNATports() {
        final NATObject obj = new NATObject();
        obj.natPorts = new HashMap();
        final int httpsPort = SyMUtil.getSSLPort();
        obj.natPorts.put("HTTPS_PORT", httpsPort);
        return obj;
    }
}
