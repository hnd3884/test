package com.me.devicemanagement.onpremise.server.settings.nat;

import java.util.Hashtable;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class NATHandler
{
    private static List<NATListener> natChangeListenerList;
    private static NATHandler natHandler;
    private Logger logger;
    private static Logger mstCreationLogger;
    public static final String NAT_UPDATE_SAFE = "update_safe";
    public static final String NAT_UPDATE_NOT_SAFE = "update_not_safe";
    
    public NATHandler() {
        this.logger = Logger.getLogger("ServerSettingsLogger");
    }
    
    public static NATHandler getInstance() {
        if (NATHandler.natHandler == null) {
            NATHandler.natHandler = new NATHandler();
        }
        return NATHandler.natHandler;
    }
    
    public void addNATListener(final NATListener natListener) {
        this.logger.log(Level.INFO, "addNATListener() called : {0}", natListener.getClass().getName());
        NATHandler.natChangeListenerList.add(natListener);
    }
    
    public void removeNATListener(final NATListener natListener) {
        this.logger.log(Level.INFO, "removeNATListener() called : {0}", natListener.getClass().getName());
        NATHandler.natChangeListenerList.remove(natListener);
    }
    
    public void invokeNATChangedListeners(final NATObject obj) {
        final int l = NATHandler.natChangeListenerList.size();
        this.logger.log(Level.INFO, "invokeNATListeners() called : {0}", NATHandler.natChangeListenerList.toString());
        for (int s = 0; s < l; ++s) {
            final NATListener listener = NATHandler.natChangeListenerList.get(s);
            listener.natModified(obj);
        }
    }
    
    public String checkIsNATUpdateSafe(final NATObject obj) {
        String isSafe = "update_safe";
        for (final NATListener listener : NATHandler.natChangeListenerList) {
            isSafe = listener.isNATUpdateSafe(obj);
            if (!isSafe.equalsIgnoreCase("update_safe")) {
                break;
            }
        }
        this.logger.log(Level.INFO, "checkIsNATUpdateSafe() called : {0} ; isSafe = {1}", new Object[] { NATHandler.natChangeListenerList.toString(), String.valueOf(isSafe) });
        return isSafe;
    }
    
    public HashMap setNATvaluesInForm(HashMap dynaForm) {
        for (final NATListener listener : NATHandler.natChangeListenerList) {
            dynaForm = listener.setValuesInNATForm(dynaForm);
        }
        this.logger.log(Level.INFO, "setNATvaluesInForm() called..");
        return dynaForm;
    }
    
    public HashMap getPortsMap() {
        final HashMap ports = new HashMap();
        for (final NATListener listener : NATHandler.natChangeListenerList) {
            final NATObject obj = listener.getNATports();
            if (obj.natPorts != null && !obj.natPorts.isEmpty()) {
                ports.putAll(obj.natPorts);
            }
        }
        this.logger.log(Level.INFO, "getPortsMap() called.. ports : {0} ", ports.toString());
        return ports;
    }
    
    public NATObject addOrUpdateNATSettings(final Map dynaFormMap) throws Exception {
        final String natAddress = dynaFormMap.get("NAT_ADDRESS");
        Integer port = dynaFormMap.get("NAT_HTTPS_PORT");
        this.logger.log(Level.INFO, "addOrUpdateNATSettings() called.. DynaMap : {0}", dynaFormMap.toString());
        this.logger.log(Level.INFO, "addOrUpdateNATSettings() port value for NAT_HTTPS_PORT from dynamform is  ", port);
        final Properties serverInfo = SyMUtil.getDCServerInfo();
        if (port == 0) {
            final String httpPort = ((Hashtable<K, Object>)serverInfo).get("HTTPS_PORT").toString();
            this.logger.log(Level.INFO, "addOrUpdateNATSettings() : NAT_HTTPS_PORT is zero going get server started port which is  : " + httpPort);
            port = Integer.valueOf(httpPort);
            this.logger.log(Level.INFO, "addOrUpdateNATSettings() : NAT_HTTPS_PORT now is  " + port);
        }
        final Integer rdsPort = dynaFormMap.get("NAT_RDS_HTTPS_PORT");
        final Integer fileTransferPort = dynaFormMap.get("NAT_FT_HTTPS_PORT");
        final Integer nsPort = dynaFormMap.get("NAT_NS_PORT");
        final Integer chatPort = dynaFormMap.get("NAT_CHAT_PORT");
        final DataObject data = SyMUtil.getPersistence().get("DCServerNATInfo", (Criteria)null);
        this.logger.log(Level.INFO, "addOrUpdateNATSettings() called.. DynaMap : {0}", dynaFormMap.toString());
        final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        boolean isNeedAgentRegenerate = false;
        if (!data.isEmpty()) {
            final Row natRow = data.getRow("DCServerNATInfo");
            final String oldIP = (String)natRow.get("NAT_ADDRESS");
            final Integer oldPort = (Integer)natRow.get("NAT_HTTPS_PORT");
            if (!oldIP.equalsIgnoreCase(natAddress) || oldPort != (int)port) {
                isNeedAgentRegenerate = true;
            }
            natRow.set("NAT_ADDRESS", (Object)natAddress);
            natRow.set("NAT_HTTPS_PORT", (Object)port);
            natRow.set("NAT_RDS_HTTPS_PORT", (Object)rdsPort);
            natRow.set("NAT_FT_HTTPS_PORT", (Object)fileTransferPort);
            natRow.set("NAT_NS_PORT", (Object)nsPort);
            natRow.set("NAT_CHAT_PORT", (Object)chatPort);
            data.updateRow(natRow);
            SyMUtil.getPersistence().update(data);
            this.logger.log(Level.INFO, "Nat Modified from {0} to {1}", new Object[] { oldIP + ":" + oldPort, natAddress + ":" + port });
            DCEventLogUtil.getInstance().addEvent(121, userName, (HashMap)null, "dc.mdm.actionlog.settings.nat_modified", (Object)(oldIP + ":" + oldPort + "@@@" + natAddress + ":" + port), true);
        }
        else {
            final Row natRow = new Row("DCServerNATInfo");
            natRow.set("NAT_ADDRESS", (Object)natAddress);
            natRow.set("NAT_HTTPS_PORT", (Object)port);
            natRow.set("NAT_RDS_HTTPS_PORT", (Object)rdsPort);
            natRow.set("NAT_FT_HTTPS_PORT", (Object)fileTransferPort);
            natRow.set("NAT_NS_PORT", (Object)nsPort);
            natRow.set("NAT_CHAT_PORT", (Object)chatPort);
            data.addRow(natRow);
            SyMUtil.getPersistence().add(data);
            this.logger.log(Level.INFO, "Nat Saved as {0}", natAddress + ":" + port);
            DCEventLogUtil.getInstance().addEvent(121, userName, (HashMap)null, "dc.mdm.actionlog.settings.nat_saved", (Object)(natAddress + ":" + port), true);
            isNeedAgentRegenerate = true;
        }
        final String givenNATAddress = dynaFormMap.get("NAT_ADDRESS");
        final NATObject obj = new NATObject();
        obj.givenNATAddress = givenNATAddress;
        obj.isRegenerateRequired = isNeedAgentRegenerate;
        obj.isSecondaryMismatch = this.isSecondaryIPMismatchWithNatAddress(obj.givenNATAddress);
        return obj;
    }
    
    public static Properties getNATConfigurationProperties() throws DataAccessException {
        final Properties natProps = new Properties();
        final DataObject data = SyMUtil.getPersistence().get("DCServerNATInfo", (Criteria)null);
        if (!data.isEmpty()) {
            final Row natRow = data.getRow("DCServerNATInfo");
            if (natRow != null) {
                for (final String columnName : natRow.getColumns()) {
                    ((Hashtable<String, Object>)natProps).put(columnName, natRow.get(columnName));
                }
            }
        }
        return natProps;
    }
    
    public static void updateNATSettingsHttpsPort(final Integer httpsPort) throws Exception {
        if (!CustomerInfoUtil.getInstance().isMSP()) {
            final DataObject natDO = SyMUtil.getPersistence().get("DCServerNATInfo", (Criteria)null);
            if (!natDO.isEmpty()) {
                final Row natRow = natDO.getRow("DCServerNATInfo");
                natRow.set("NAT_HTTPS_PORT", (Object)httpsPort);
                natDO.updateRow(natRow);
                SyMUtil.getPersistence().update(natDO);
            }
        }
    }
    
    public static void updateNATSettingsPort(final String portName, final Integer port) throws Exception {
        if (!CustomerInfoUtil.getInstance().isMSP()) {
            final DataObject natDO = SyMUtil.getPersistence().get("DCServerNATInfo", (Criteria)null);
            if (!natDO.isEmpty()) {
                final Row natRow = natDO.getRow("DCServerNATInfo");
                natRow.set(portName, (Object)port);
                natDO.updateRow(natRow);
                SyMUtil.getPersistence().update(natDO);
            }
        }
    }
    
    public NATObject saveNATsettings(final Map dynaFormMap) {
        String status = "success";
        NATObject natObj = new NATObject();
        try {
            natObj = getInstance().addOrUpdateNATSettings(dynaFormMap);
            boolean needCertificateRegenerate = false;
            final Boolean isThridParty = SSLCertificateUtil.getInstance().isThirdPartySSLInstalled();
            if (!isThridParty) {
                final Boolean isValid = SSLCertificateUtil.getInstance().checkHostNameValidWithSSL(natObj.givenNATAddress);
                if (!isValid) {
                    needCertificateRegenerate = true;
                }
                if (natObj.isRegenerateRequired && needCertificateRegenerate) {
                    status = "restart";
                }
            }
            if (needCertificateRegenerate) {
                final boolean isCertificateGenerated = com.me.devicemanagement.onpremise.server.certificate.SSLCertificateUtil.getInstance().checkAndGenerateServerCertificate(natObj.givenNATAddress);
                if (isCertificateGenerated) {
                    MessageProvider.getInstance().unhideMessage("REQUIRED_SERVICE_RESTART");
                    SyMUtil.updateSyMParameter("SERVICE_RESTARTED", "false");
                }
            }
        }
        catch (final Exception e) {
            status = "failed";
            this.logger.log(Level.WARNING, "Exception while saving NAT Settings", e);
        }
        natObj.statusOnSavingNATdetails = status;
        return natObj;
    }
    
    public boolean isSecondaryIPMismatchWithNatAddress(final String natAddress) throws Exception {
        final Properties dcserverinfo = SyMUtil.getDCServerInfo();
        final String secondaryIP = dcserverinfo.getProperty("SERVER_SEC_IPADDR");
        return !natAddress.equalsIgnoreCase(secondaryIP);
    }
    
    public boolean isSecondaryPublic() throws Exception {
        final DataObject dataObject = SyMUtil.getDCServerInfoDO();
        final Row serverInfoRow = dataObject.getRow("DCServerInfo");
        final String serverMacIpAddr = serverInfoRow.get("SERVER_MAC_IPADDR") + "";
        final String serverSecIpAddr = serverInfoRow.get("SERVER_SEC_IPADDR") + "";
        return !serverMacIpAddr.equals("") && !serverSecIpAddr.equals("") && !serverSecIpAddr.equals("--") && !serverMacIpAddr.equalsIgnoreCase(serverSecIpAddr);
    }
    
    static {
        NATHandler.natChangeListenerList = new ArrayList<NATListener>();
        NATHandler.natHandler = null;
        NATHandler.mstCreationLogger = Logger.getLogger("MSTCreationLogger");
    }
}
