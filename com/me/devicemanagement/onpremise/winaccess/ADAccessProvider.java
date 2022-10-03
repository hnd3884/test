package com.me.devicemanagement.onpremise.winaccess;

import com.adventnet.sym.winaccess.ADReportsHandler;
import com.me.devicemanagement.framework.winaccess.DomainInfo;
import java.util.Iterator;
import com.me.devicemanagement.framework.winaccess.ADObjectsConvertor;
import com.me.devicemanagement.onpremise.webclient.admin.UserController;
import java.util.TreeMap;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Properties;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.api.ADAccessAPI;

public class ADAccessProvider extends com.me.devicemanagement.framework.winaccess.ADAccessProvider implements ADAccessAPI
{
    private static Logger logger;
    private static ADAccessProvider adAccessProvider;
    private static String sourceClass;
    
    public static synchronized ADAccessProvider getInstance() {
        if (ADAccessProvider.adAccessProvider == null) {
            ADAccessProvider.adAccessProvider = new ADAccessProvider();
        }
        return ADAccessProvider.adAccessProvider;
    }
    
    public String getNetBIOSName(final String strDomain, final String strDCName, final String strUserName, final String strPasswd, final boolean isSSL, final int portNo) throws SyMException {
        final String sourceMethod = "getNetBIOSName";
        String strLdapPath = "LDAP://" + strDCName + "/CN=Configuration,DC=";
        strLdapPath += strDomain.replace(".", ",DC=");
        final String strBindUserName = strDomain + "\\" + strUserName;
        final String strFilter = "(&(objectcategory=Crossref)(dnsRoot=" + strDomain + ")(netBIOSName=*))";
        final List lisAttributes = new ArrayList();
        lisAttributes.add("nETBIOSName");
        final List lisNetBIOSName = this.nativeGetADObjects(strDCName, strLdapPath, strBindUserName, strPasswd, lisAttributes, strFilter, 2, isSSL, portNo);
        if (lisNetBIOSName.size() > 0) {
            SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Fetched NetBIOS name : " + lisNetBIOSName.get(0));
            final Properties prop = lisNetBIOSName.get(0);
            return prop.getProperty("nETBIOSName");
        }
        throw new SyMException(1001, (Throwable)new Exception("NetBIOS name could not be fetched for given domain : " + strDomain));
    }
    
    public TreeMap getADDomainNamesForLoginPage() {
        final UserController userController = new UserController();
        return UserController.getADDomainNamesForLoginPage();
    }
    
    public List getGeneralComputerAttrList() throws Exception {
        return new ADObjectsConvertor().getGeneralComputerAttrList();
    }
    
    public boolean isUserMemberOfAnyGroup(final String arg_netBIOSName, final String domainUserName, final String emailAddress, final String domainPassword, final List<String> distinguishedNames, final List<String> guids, final Long customerID) throws SyMException {
        Boolean isUserMemberOfGroup = false;
        for (final String distinguishedName : distinguishedNames) {
            isUserMemberOfGroup |= this.isUserMemberOfGroup(arg_netBIOSName, domainUserName, domainPassword, distinguishedName, customerID);
        }
        return isUserMemberOfGroup;
    }
    
    public boolean isUserMemberOfGroup(final String arg_netBIOSName, final String domainUserName, final String domainPassword, final String distinguishedName, final long customerID) throws SyMException {
        final String sourceMethod = "getADObjectsFromNative";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function!!");
        final DomainInfo domainObj = new DomainInfo(arg_netBIOSName);
        if (domainObj.validateDomainInfoObject()) {
            SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Given domain info object contains required data for AD binding!!");
        }
        if (domainUserName != null && domainPassword != null) {
            domainObj.strUserName = domainUserName;
            domainObj.strPassword = domainPassword;
        }
        final List<String> listAttributes = new ArrayList<String>();
        listAttributes.add("name");
        final String strFilter = this.isADUserMemberOfGroupSearchFilter(domainUserName, distinguishedName);
        final String strLdapPath = domainObj.getLdapPath();
        final String strBindUserName = domainObj.getBindFormatUserName();
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "LdapPath : {0}", new Object[] { strLdapPath });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "UserName : {0}", new Object[] { strBindUserName });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Filter used : {0}", new Object[] { strFilter });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD Attributes list : {0}", new Object[] { listAttributes });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Calling native function!!");
        final List lisADObjects = this.nativeGetADObjects(domainObj.strDCName, strLdapPath, strBindUserName, domainObj.strPassword, listAttributes, strFilter, 2, domainObj.isSSL, domainObj.portNo);
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Returned from native function!!");
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Fetched data : {0}", new Object[] { lisADObjects });
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Resource Type  : 2");
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "No. of retrieved AD objects : " + lisADObjects.size());
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function!!");
        return !lisADObjects.isEmpty();
    }
    
    private String isADUserMemberOfGroupSearchFilter(final String userName, final String distinguishedName) {
        return "(&(objectCategory=person)(objectClass=user)(sAMAccountName=" + userName + ")(memberof:1.2.840.113556.1.4.1941:=" + distinguishedName + "))";
    }
    
    public native int validateADsBindingwitherrcode(final String p0, final String p1, final String p2, final String p3, final String p4, final boolean p5, final int p6) throws SyMException;
    
    public native List nativeGetADObjects(final String p0, final String p1, final String p2, final String p3, final List p4, final String p5, final int p6, final boolean p7, final int p8) throws SyMException;
    
    public native boolean nativeIsValidADObjectName(final String p0, final String p1, final String p2, final String p3, final String p4, final boolean p5, final int p6) throws SyMException;
    
    public native Properties nativeGetThisDCInfo(final String p0) throws SyMException;
    
    public native boolean validateADsBinding(final String p0, final String p1, final String p2, final String p3, final boolean p4, final int p5) throws SyMException;
    
    public native List nativeGetModifiedADObjects(final String p0, final String p1, final String p2, final String p3, final List p4, final String p5, final int p6, final long p7, final boolean p8, final int p9) throws SyMException;
    
    public native String getDomainGUID(final String p0, final String p1, final String p2, final String p3, final boolean p4, final int p5) throws SyMException;
    
    public synchronized int fetchBulkADdata(final String strNetBIOSName, final String strDCName, final String strLdapPath, final String strBindUserName, final String strPassword, final int nResType, final List lisAttributes, final String strFilter, final int nSearchLevel, final long nModifiedTime, final int fetchCount, final boolean isDCUserFetch, final String callbackClass, final boolean isSSL, final int portNo) throws Exception {
        return ADReportsHandler.getInstance().fetchBulkADdata(strNetBIOSName, strDCName, strLdapPath, strBindUserName, strPassword, nResType, lisAttributes, strFilter, nSearchLevel, nModifiedTime, fetchCount, isDCUserFetch, callbackClass, isSSL, portNo);
    }
    
    static {
        ADAccessProvider.logger = Logger.getLogger("SoMLogger");
        ADAccessProvider.adAccessProvider = null;
        ADAccessProvider.sourceClass = "ADAccessProvider";
    }
}
