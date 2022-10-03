package com.me.devicemanagement.framework.winaccess;

import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.api.WinAccessAPI;
import java.util.logging.Logger;

public class WinAccessProvider
{
    private static WinAccessProvider winAccessProvider;
    protected String strCurrentDomainNetBIOSName;
    public static Logger agentLogger;
    private static WinAccessAPI winaccessapi;
    public static final String DM_WINACCESS_CLASS = "DM_WINACCESS_CLASS";
    
    public WinAccessProvider() {
        this.strCurrentDomainNetBIOSName = null;
    }
    
    public static WinAccessProvider getInstance() {
        if (WinAccessProvider.winAccessProvider == null) {
            WinAccessProvider.winAccessProvider = new WinAccessProvider();
        }
        return WinAccessProvider.winAccessProvider;
    }
    
    public boolean isFirewallEnabledInDCServer(final long nPortNo) throws SyMException {
        return this.isFirewallEnabledInDCServer(nPortNo, "TCP");
    }
    
    public boolean isFirewallEnabledInDCServer(final long nPortNo, final String protocol) throws SyMException {
        if (!CustomerInfoUtil.isSAS) {
            try {
                final String strNetBIOSName = this.getCurrentNetBIOSName();
                WinAccessProvider.agentLogger.log(Level.INFO, "NetBios Name : " + strNetBIOSName);
                if (strNetBIOSName != null) {
                    final Properties credProps = this.getCredentialsForDomain(strNetBIOSName);
                    final String strUserName = credProps.getProperty("USER_NAME");
                    final String strPasswd = credProps.getProperty("PASSWORD");
                    if (strUserName != null && strPasswd != null) {
                        return getWinAccessAPI().nativeIsFirewallEnabledInDCServer(strNetBIOSName + "\\" + strUserName, strPasswd, strNetBIOSName, nPortNo, protocol);
                    }
                }
                return getWinAccessAPI().nativeIsFirewallEnabledInDCServer(null, null, null, nPortNo, protocol);
            }
            catch (final Exception ex) {
                WinAccessProvider.agentLogger.log(Level.INFO, "Exception occured in isFirewallEnabledInDCServer : " + ex);
                WinAccessProvider.agentLogger.log(Level.INFO, "WinAccessProvider::isFirewallEnabledInDCServer : returnin false becoz of exception.");
            }
        }
        return false;
    }
    
    public boolean openFirewallPort(final long nPortNo) throws SyMException {
        return this.openFirewallPort(nPortNo, "TCP");
    }
    
    public boolean openFirewallPort(final long nPortNo, final String protocol) throws SyMException {
        try {
            final String strNetBIOSName = this.getCurrentNetBIOSName();
            if (strNetBIOSName != null) {
                final Properties credProps = this.getCredentialsForDomain(strNetBIOSName);
                final String strUserName = credProps.getProperty("USER_NAME");
                final String strPasswd = credProps.getProperty("PASSWORD");
                if (strUserName != null && strPasswd != null) {
                    return getWinAccessAPI().nativeOpenFirewallPort(strNetBIOSName + "\\" + strUserName, strPasswd, strNetBIOSName, nPortNo, protocol);
                }
            }
            return getWinAccessAPI().nativeOpenFirewallPort(null, null, null, nPortNo, protocol);
        }
        catch (final Exception ex) {
            WinAccessProvider.agentLogger.log(Level.INFO, "Exception occured in isFirewallEnabledInDCServer : " + ex);
            throw new SyMException(1001, ex);
        }
    }
    
    public String getCurrentNetBIOSName() throws SyMException {
        return getWinAccessAPI().nativeGetCurrentNetBIOSName();
    }
    
    public String replaceSpcCharForFilter(final String filter) {
        String temp = filter;
        temp = temp.replace("\\", "\\5c");
        temp = temp.replace("*", "\\2a");
        temp = temp.replace("(", "\\28");
        temp = temp.replace(")", "\\29");
        return temp;
    }
    
    public boolean validatePassword(final String strNetBIOSName, final String strDomainName, final String strDCName, final String strUserName, final String strPasswd, final boolean isSSL, final int portNo) throws SyMException {
        return ADAccessProvider.getInstance().validatePassword(strNetBIOSName, strDomainName, strDCName, strUserName, strPasswd, isSSL, portNo);
    }
    
    public String getGMTString(final long time) {
        final Date date = new Date(time);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return dateFormat.format(date);
    }
    
    public Properties getCredentialsForDomain(final String strNetBIOSName) throws DataAccessException {
        final Properties props = new Properties();
        final Column col = new Column("Resource", "DOMAIN_NETBIOS_NAME");
        final Criteria criteria = new Criteria(col, (Object)strNetBIOSName, 0, false);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        query.addJoin(new Join("Resource", "ManagedDomain", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedDomain", "ManagedDomainCredentialRel", new String[] { "RESOURCE_ID" }, new String[] { "DOMAINRESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedDomainCredentialRel", "Credential", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 2));
        query.addSelectColumn(new Column((String)null, "*"));
        query.setCriteria(criteria.and(new Criteria(Column.getColumn("ManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0)));
        final DataObject dobj = SyMUtil.getPersistence().get(query);
        if (!dobj.isEmpty()) {
            final Row credRow = dobj.getRow("Credential");
            if (credRow != null) {
                final String strUserName = (String)credRow.get("CRD_USERNAME");
                String strPasswd = "";
                String tempPasswd = (String)credRow.get("CRD_PASSWORD");
                WinAccessProvider.agentLogger.log(Level.INFO, "Domain netbios name : " + strNetBIOSName);
                WinAccessProvider.agentLogger.log(Level.INFO, "UserName length retrieved : " + strUserName.length());
                WinAccessProvider.agentLogger.log(Level.INFO, "Password length retrieved : " + tempPasswd.length());
                WinAccessProvider.agentLogger.log(Level.INFO, "Encryption Type : " + credRow.get("CRD_ENC_TYPE"));
                tempPasswd = ApiFactoryProvider.getCryptoAPI().decrypt(tempPasswd, (Integer)credRow.get("CRD_ENC_TYPE"));
                if (tempPasswd.compareTo("--") != 0) {
                    strPasswd = tempPasswd;
                }
                props.setProperty("USER_NAME", strUserName);
                props.setProperty("PASSWORD", strPasswd);
            }
        }
        else {
            props.setProperty("USER_NAME", "--");
            props.setProperty("PASSWORD", "--");
            WinAccessProvider.agentLogger.log(Level.INFO, "getCredentialsForDomain -> Returning dummy password for domain name " + strNetBIOSName);
        }
        return props;
    }
    
    public static WinAccessAPI getWinAccessAPI() {
        try {
            if (WinAccessProvider.winaccessapi == null) {
                final String classname = ProductClassLoader.getSingleImplProductClass("DM_WINACCESS_CLASS");
                WinAccessProvider.winaccessapi = (WinAccessAPI)Class.forName(classname).newInstance();
            }
            return WinAccessProvider.winaccessapi;
        }
        catch (final Exception e) {
            WinAccessProvider.agentLogger.log(Level.SEVERE, "Exception while trying to get class value for DM_WINACCESS_CLASS", e);
            return null;
        }
    }
    
    public static DomainInfo getDomainLdapStatus(final String netBIOSName) {
        WinAccessProvider.agentLogger.log(Level.INFO, "WinAccessProvider : getDomainLdapStatus called!! Received NetBiosName:: " + netBIOSName);
        if (netBIOSName != null) {
            try {
                final DomainInfo domainInfo = new DomainInfo(netBIOSName);
                if (domainInfo.validateDomainInfoObject()) {
                    WinAccessProvider.agentLogger.log(Level.INFO, "WinAccessProvider : getDomainLdapStatus VALID DOMAIN OBJECT FOUND !!" + domainInfo.strDCName);
                    return domainInfo;
                }
                WinAccessProvider.agentLogger.log(Level.INFO, "WinAccessProvider : getDomainLdapStatus No exception occurred and domain Info is not valid :: returning null");
                return null;
            }
            catch (final Exception ex) {
                WinAccessProvider.agentLogger.log(Level.INFO, "WinAccessProvider : getDomainLdapStatus Exception occurred!! " + ex);
                return null;
            }
        }
        return null;
    }
    
    static {
        WinAccessProvider.winAccessProvider = null;
        WinAccessProvider.agentLogger = Logger.getLogger("AgentInstallerLogger");
        WinAccessProvider.winaccessapi = null;
    }
}
