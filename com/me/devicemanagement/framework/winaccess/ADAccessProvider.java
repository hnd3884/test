package com.me.devicemanagement.framework.winaccess;

import java.util.logging.Level;
import java.util.Map;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class ADAccessProvider extends ADAccessUtil
{
    private static Logger logger;
    private static ADAccessProvider adAccessProvider;
    private static String sourceClass;
    protected ADAccessUtil adAccessUtil;
    
    public static synchronized ADAccessProvider getInstance() {
        if (ADAccessProvider.adAccessProvider == null) {
            ADAccessProvider.adAccessProvider = new ADAccessProvider();
        }
        return ADAccessProvider.adAccessProvider;
    }
    
    protected ADAccessProvider() {
        this.adAccessUtil = new ADAccessUtil();
    }
    
    public int validatePasswordWithErrorCode(final String strNetBIOSName, final String strDomainName, final String strDCName, final String userName, final String password, final boolean isSSL, final int portNo) throws SyMException {
        final String sourceMethod = "validatePasswordWithErrorCode";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered into function!!");
        if (strNetBIOSName == null && strDomainName == null) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Domain name and NetBIOS name are null!!");
            throw new SyMException(1002, "Domain Name and NetBIOS name cannot be null!!", null);
        }
        if (userName == null) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "User name is null");
            throw new SyMException(1002, "User name cannot be null!!", null);
        }
        if (strDCName == null) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "DC name is null");
            throw new SyMException(1002, "DC name cannot be null!!", null);
        }
        if (isSSL && (portNo <= 0 || portNo > 65535)) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Port No is Invalid");
            throw new SyMException(1002, "Port no is invalid", null);
        }
        int result = 1;
        String strLdapPath = null;
        try {
            strLdapPath = "LDAP://" + strDCName;
            if (strDomainName != null) {
                SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD Domain Name is NOT NULL. AD DOMAIN name : " + strDomainName);
                strLdapPath += "/DC=";
                strLdapPath += strDomainName.replace(".", ",DC=");
                result = ApiFactoryProvider.getADAccessAPI().validateADsBindingwitherrcode(strDCName, strLdapPath, strDomainName + "\\" + userName, password, strDomainName, isSSL, portNo);
                if (result == 0) {
                    SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, strDomainName + " is present. Its credentials are valid.");
                }
            }
            else {
                SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "NetBIOS Name is NOT NULL. NetBIOS name : " + strNetBIOSName);
                result = ApiFactoryProvider.getADAccessAPI().validateADsBindingwitherrcode(strDCName, strLdapPath, strNetBIOSName + "\\" + userName, password, strNetBIOSName, isSSL, portNo);
                if (result == 0) {
                    SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, strNetBIOSName + " is present. Its credentials are valid.");
                }
            }
        }
        catch (final SyMException ex) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Exception in validating ADsBinding. Details -> Path: " + strLdapPath + " Domain : " + strDomainName + " User name : " + userName);
            SyMLogger.error(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Exception occured while validating ADsBinding!!", ex);
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "res" + result);
        }
        SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Exiting the function!! returning validation result as " + result);
        return result;
    }
    
    public boolean isValidADObjectName(final String arg_netBIOSName, final String strName, final int nResType) throws SyMException {
        final String sourceMethod = "isValidADObjectName";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function!!");
        final DomainInfo domainObj = new DomainInfo(arg_netBIOSName);
        try {
            if (domainObj.validateDomainInfoObject()) {
                SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Given domain info object contains required data for AD binding!!");
            }
        }
        catch (final Exception ex) {
            throw new SyMException(1001, ex);
        }
        final String strFilter = this.adAccessUtil.getADSearchFilterWithName(nResType, strName);
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Filter used : " + strFilter);
        final String strLdapPath = domainObj.getLdapPath();
        final String strBindUserName = domainObj.getBindFormatUserName();
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Calling native function!!");
        final boolean result = ApiFactoryProvider.getADAccessAPI().nativeIsValidADObjectName(domainObj.strDCName, strLdapPath, strBindUserName, domainObj.strPassword, strFilter, domainObj.isSSL, domainObj.portNo);
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Native function returned!!");
        if (result) {
            SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Given object " + strName + " is present in AD");
            return result;
        }
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Given object " + strName + " is NOT in AD");
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function!!");
        return result;
    }
    
    public Properties getThisADObjectByName(final String arg_netBIOSName, final String strADObjectName, final int nResourceType, final List lisAttributes) throws SyMException {
        return this.getThisADObjectByName(arg_netBIOSName, null, null, strADObjectName, nResourceType, lisAttributes);
    }
    
    public Properties getThisADObjectByName(final String arg_netBIOSName, final String domainUserName, final String domainPassword, final String strADObjectName, final int nResourceType, List lisAttributes) throws SyMException {
        final String sourceMethod = "getThisADObjectByName";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function!!");
        if (arg_netBIOSName == null) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "NetBIOS name sent is null");
            throw new SyMException(1001, new Exception("NetBIOS name cannot be null"));
        }
        final DomainInfo domainObj = new DomainInfo(arg_netBIOSName);
        if (domainObj.validateDomainInfoObject()) {
            SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Given domain info object contains required data for AD binding!!");
        }
        if (domainUserName != null && domainPassword != null) {
            domainObj.strUserName = domainUserName;
            domainObj.strPassword = domainPassword;
        }
        final String strFilter = this.adAccessUtil.getADSearchFilterWithName(nResourceType, strADObjectName);
        if (strFilter == null) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD object filter not present for given resource type : " + nResourceType);
            throw new SyMException(1001, new Exception("AD Object Search Filter is null"));
        }
        final String strLdapPath = domainObj.getLdapPath();
        final String strBindUserName = domainObj.getBindFormatUserName();
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "LdapPath : {0}", new Object[] { strLdapPath });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "UserName : {0}", new Object[] { strBindUserName });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Filter used : {0}", new Object[] { strFilter });
        if (lisAttributes == null || lisAttributes.size() == 0) {
            lisAttributes = this.adAccessUtil.getAttributes(nResourceType);
        }
        int nSearchLevel = 2;
        if (nResourceType == 4) {
            nSearchLevel = 3;
        }
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Native function called!!");
        List lisFetchedData = new ArrayList();
        try {
            lisFetchedData = ApiFactoryProvider.getADAccessAPI().nativeGetADObjects(domainObj.strDCName, strLdapPath, strBindUserName, domainObj.strPassword, lisAttributes, strFilter, nSearchLevel, domainObj.isSSL, domainObj.portNo);
        }
        catch (final SyMException ex) {
            SyMLogger.error(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Exception while fetching data from native!!", ex);
            throw ex;
        }
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Native function returned!!");
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Resource Type : {0}", new Object[] { nResourceType });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "List of fetched This data  : {0}", new Object[] { lisFetchedData });
        if (lisFetchedData.size() <= 0) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Required data not present in AD!!");
            return null;
        }
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function!!");
        final Properties propRet = lisFetchedData.get(0);
        if (nResourceType == 3) {
            try {
                final Properties propDCInfo = ApiFactoryProvider.getADAccessAPI().nativeGetThisDCInfo(strADObjectName);
                propRet.putAll(propDCInfo);
            }
            catch (final Exception ex2) {
                SyMLogger.error(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Exception occured!!", ex2);
                throw new SyMException(1001, ex2);
            }
        }
        return propRet;
    }
    
    public boolean validatePassword(final String strNetBIOSName, final String strDomainName, final String strDCName, final String userName, final String password, final boolean isSSL, final int portNo) throws SyMException {
        final String sourceMethod = "validatePassword";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered into function!!");
        if (strNetBIOSName == null && strDomainName == null) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Domain name and NetBIOS name are null!!");
            throw new SyMException(1002, "Domain Name and NetBIOS name cannot be null!!", null);
        }
        if (userName == null) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "User name is null");
            throw new SyMException(1002, "User name cannot be null!!", null);
        }
        if (strDCName == null) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "DC name is null");
            throw new SyMException(1002, "DC name cannot be null!!", null);
        }
        boolean result = false;
        String strLdapPath = null;
        try {
            strLdapPath = "LDAP://" + strDCName;
            if (strDomainName != null) {
                SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD Domain Name is NOT NULL. AD DOMAIN name : " + strDomainName);
                strLdapPath += "/DC=";
                strLdapPath += strDomainName.replace(".", ",DC=");
                result = ApiFactoryProvider.getADAccessAPI().validateADsBinding(strDCName, strLdapPath, strDomainName + "\\" + userName, password, isSSL, portNo);
                if (result) {
                    SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, strDomainName + " is present. Its credentials are valid.");
                }
            }
            else {
                SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "NetBIOS Name is NOT NULL. NetBIOS name : " + strNetBIOSName);
                result = ApiFactoryProvider.getADAccessAPI().validateADsBinding(strDCName, strLdapPath, strNetBIOSName + "\\" + userName, password, isSSL, portNo);
                if (result) {
                    SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, strNetBIOSName + " is present. Its credentials are valid.");
                }
            }
        }
        catch (final SyMException ex) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Exception in validating ADsBinding. Details -> Path: " + strLdapPath + " Domain : " + strDomainName + " User name : " + userName);
            SyMLogger.error(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Exception occured while validating ADsBinding!!", ex);
            throw ex;
        }
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Exiting the function!!");
        return result;
    }
    
    public List getADObjects(final String arg_netBIOSName, final int childrenResType, final String strParentGUID, List lisAttributes, final boolean isExcludeSubTree) throws SyMException {
        if (arg_netBIOSName == null) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, "getADObjects", "NetBIOS name sent is null");
            throw new SyMException(1001, new Exception("NetBIOS name cannot be null"));
        }
        boolean isChangeNeeded = false;
        if (lisAttributes == null || lisAttributes.size() == 0) {
            lisAttributes = this.adAccessUtil.getAttributes(childrenResType);
        }
        if (childrenResType == 5) {
            final List temp = this.adAccessUtil.getAttributes(childrenResType);
            if (temp.size() > lisAttributes.size() && lisAttributes.contains("name") && !lisAttributes.contains("distinguishedName")) {
                lisAttributes.add("distinguishedName");
                lisAttributes.add("domainName");
                isChangeNeeded = true;
            }
        }
        else if (childrenResType == 2) {
            final List temp = this.adAccessUtil.getAttributes(childrenResType);
            if (temp.size() > lisAttributes.size() && lisAttributes.contains("name") && !lisAttributes.contains("sAMAccountName")) {
                lisAttributes.add("sAMAccountName");
                isChangeNeeded = true;
            }
        }
        List val = new ArrayList();
        if (isExcludeSubTree) {
            val = this.getADObjects(arg_netBIOSName, childrenResType, strParentGUID, lisAttributes, 1);
        }
        else {
            val = this.getADObjects(arg_netBIOSName, childrenResType, strParentGUID, lisAttributes, 2);
        }
        if (isChangeNeeded) {
            if (childrenResType == 5) {
                val = this.manipulatNameAttr(val);
            }
            else if (childrenResType == 2) {
                val = this.manipulatUserNameAttr(val);
            }
        }
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, "getADObjects", "Value to be returned : {0}", new Object[] { val });
        return val;
    }
    
    protected List manipulatNameAttr(final List lisValue) throws SyMException {
        final String sourceMethod = "manipulatNameAttr";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function.");
        final int size = lisValue.size();
        final List retList = new ArrayList();
        final String domainAttr = "domainName";
        final String nameAttr = "name";
        final String dnAttr = "distinguishedName";
        for (int index = 0; index < size; ++index) {
            final Properties propValue = lisValue.get(index);
            final String strDomainName = propValue.getProperty(domainAttr);
            propValue.setProperty(nameAttr, strDomainName);
            propValue.remove(domainAttr);
            propValue.remove(dnAttr);
            retList.add(propValue);
        }
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function.");
        return retList;
    }
    
    protected List manipulatUserNameAttr(final List lisValue) throws SyMException {
        final String sourceMethod = "manipulatUserNameAttr";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function.");
        final int size = lisValue.size();
        final List retList = new ArrayList();
        final String userNameAttr = "sAMAccountName";
        final String nameAttr = "name";
        for (int index = 0; index < size; ++index) {
            final Properties propValue = lisValue.get(index);
            final String strUserName = propValue.getProperty(userNameAttr);
            propValue.setProperty(nameAttr, strUserName);
            propValue.remove(userNameAttr);
            retList.add(propValue);
        }
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function.");
        return retList;
    }
    
    public List getADObjects(final String arg_netBIOSName, final int childrenResType, final String strGUID, final List lisAttributes, final int nSearchLevel) throws SyMException {
        final String sourceMethod = "getADObjects";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function!!");
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Calling the generic function!!");
        final List lisADObjects = this.getADObjectsFromNative(arg_netBIOSName, childrenResType, strGUID, lisAttributes, nSearchLevel, 0L);
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Returned from generic function!!");
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "No. of Objects received from generic function : " + lisADObjects.size());
        final List lisRetObject = this.addResTypeKeyToProperties(lisADObjects, childrenResType);
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "List of type(key) added properties : {0}", new Object[] { lisRetObject });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function!!");
        return lisRetObject;
    }
    
    protected List addResTypeKeyToProperties(final List lisADObjects, final int nResType) throws SyMException {
        final String sourceMethod = "addResTypeKeyToProperties";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function!!");
        final int nListSize = lisADObjects.size();
        final Integer objResType = new Integer(nResType);
        final String strResType = objResType.toString();
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Size of data received : {0}", new Object[] { nListSize });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Going to add type property with value : {0}", new Object[] { strResType });
        final List lisRetADObjects = new ArrayList();
        for (int index = 0; index < nListSize; ++index) {
            final Properties propADObject = lisADObjects.get(index);
            propADObject.setProperty("type", strResType);
            lisRetADObjects.add(propADObject);
        }
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Size of data to be sent : {0}", new Object[] { lisRetADObjects.size() });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Converted data : {0}", new Object[] { lisRetADObjects });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function!!");
        return lisRetADObjects;
    }
    
    public List searchADObject(final String netBIOSName, final int nResType, final String startsWithName, final String strGUID, List lisAttributes, final int nSearchAllLevel, final int adSearchType) throws SyMException {
        final String sourceMethod = "getADObjectWithPrefix";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function!!");
        final DomainInfo domainObj = new DomainInfo(netBIOSName);
        domainObj.validateDomainInfoObject();
        if (lisAttributes == null || lisAttributes.size() == 0) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD Attribute list is empty. So filling with all attributes of resource type : " + nResType);
            try {
                lisAttributes = this.adAccessUtil.getAttributes(nResType);
            }
            catch (final SyMException ex) {
                throw ex;
            }
        }
        String strFilter = "";
        if (adSearchType == 100) {
            strFilter = this.adAccessUtil.getEndsWithADSearchFilter(nResType, startsWithName);
        }
        else if (adSearchType == 200) {
            strFilter = this.adAccessUtil.getStartsWithADSearchFilter(nResType, startsWithName);
        }
        else if (adSearchType == 300) {
            strFilter = this.adAccessUtil.getContainsADSearchFilter(nResType, startsWithName);
        }
        else {
            strFilter = this.adAccessUtil.getADSearchFilterWithName(nResType, startsWithName);
        }
        String strLdapPath = null;
        if (strGUID != null) {
            strLdapPath = domainObj.getLdapPathWithGUID(strGUID);
        }
        else if (nResType == 4 && domainObj.strDomainName != null) {
            if (domainObj.strDCName != null) {
                strLdapPath = "LDAP://" + domainObj.strDCName + "/CN=Configuration,DC=";
            }
            else {
                strLdapPath = "LDAP://CN=Configuration,DC=";
            }
            strLdapPath += domainObj.strDomainName.replace(".", ",DC=");
        }
        else {
            strLdapPath = domainObj.getLdapPath();
        }
        final String strBindUserName = domainObj.getBindFormatUserName();
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "LdapPath : {0}", new Object[] { strLdapPath });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "UserName : {0}", new Object[] { strBindUserName });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Filter used : {0}", new Object[] { strFilter });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD Attributes list : {0}", new Object[] { lisAttributes });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Calling native function!!");
        List lisADObjects = new ArrayList();
        lisADObjects = ApiFactoryProvider.getADAccessAPI().nativeGetADObjects(domainObj.strDCName, strLdapPath, strBindUserName, domainObj.strPassword, lisAttributes, strFilter, nSearchAllLevel, domainObj.isSSL, domainObj.portNo);
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Returned from native function!!");
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Fetched data : {0}", new Object[] { lisADObjects });
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Resource Type  : " + nResType);
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "No. of retrieved AD objects : " + lisADObjects.size());
        final List lisRetObject = this.addResTypeKeyToProperties(lisADObjects, nResType);
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "List of type(key) added properties : {0}", new Object[] { lisRetObject });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function!!");
        return lisRetObject;
    }
    
    protected List getADObjectsFromNative(final String arg_netBIOSName, final int nResType, final String strGUID, List lisAttributes, final int nSearchAllLevel, final long nModifiedTime) throws SyMException {
        final String sourceMethod = "getADObjectsFromNative";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function!!");
        final DomainInfo domainObj = new DomainInfo(arg_netBIOSName);
        if (domainObj.validateDomainInfoObject()) {
            SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Given domain info object contains required data for AD binding!!");
        }
        if (lisAttributes == null || lisAttributes.size() == 0) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD Attribute list is empty. So filling with all attributes of resource type : " + nResType);
            try {
                lisAttributes = this.adAccessUtil.getAttributes(nResType);
            }
            catch (final SyMException ex) {
                throw ex;
            }
        }
        String strFilter = null;
        if (nModifiedTime == 0L) {
            strFilter = this.adAccessUtil.getADSearchFilter(nResType);
        }
        else {
            strFilter = this.adAccessUtil.getADSearchFilterWithTime(nResType, nModifiedTime);
        }
        String strLdapPath = null;
        if (strGUID != null) {
            strLdapPath = domainObj.getLdapPathWithGUID(strGUID);
        }
        else if (nResType == 4 && domainObj.strDomainName != null) {
            if (domainObj.strDCName != null) {
                strLdapPath = "LDAP://" + domainObj.strDCName + "/CN=Configuration,DC=";
            }
            else {
                strLdapPath = "LDAP://CN=Configuration,DC=";
            }
            strLdapPath += domainObj.strDomainName.replace(".", ",DC=");
        }
        else {
            strLdapPath = domainObj.getLdapPath();
        }
        final String strBindUserName = domainObj.getBindFormatUserName();
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "LdapPath : {0}", new Object[] { strLdapPath });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "UserName : {0}", new Object[] { strBindUserName });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Filter used : {0}", new Object[] { strFilter });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD Attributes list : {0}", new Object[] { lisAttributes });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Calling native function!!");
        List lisADObjects = new ArrayList();
        if (nModifiedTime == 0L) {
            lisADObjects = ApiFactoryProvider.getADAccessAPI().nativeGetADObjects(domainObj.strDCName, strLdapPath, strBindUserName, domainObj.strPassword, lisAttributes, strFilter, nSearchAllLevel, domainObj.isSSL, domainObj.portNo);
        }
        else {
            lisADObjects = ApiFactoryProvider.getADAccessAPI().nativeGetModifiedADObjects(domainObj.strDCName, strLdapPath, strBindUserName, domainObj.strPassword, lisAttributes, strFilter, nSearchAllLevel, nModifiedTime, domainObj.isSSL, domainObj.portNo);
        }
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Returned from native function!!");
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Fetched data : {0}", new Object[] { lisADObjects });
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Resource Type  : " + nResType);
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "No. of retrieved AD objects : " + lisADObjects.size());
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function!!");
        return lisADObjects;
    }
    
    public List searchAndFetchADObject(final String domainName, final Integer nResType, final List attributeList, final String filterChar) throws SyMException {
        List resourceList = null;
        try {
            ADAccessProvider.logger.log(Level.INFO, "domainName : " + domainName);
            ADAccessProvider.logger.log(Level.INFO, "resourceType : " + nResType);
            ADAccessProvider.logger.log(Level.INFO, "attributeList : " + attributeList);
            if (filterChar == null || filterChar.equalsIgnoreCase("All")) {
                resourceList = this.getADObjects(domainName, nResType, null, attributeList, false);
            }
            else if (filterChar.length() == 1) {
                resourceList = this.searchADObject(domainName, nResType, filterChar, null, attributeList, 2, 200);
            }
            else {
                resourceList = this.searchADObject(domainName, nResType, filterChar, null, attributeList, 2, 300);
            }
            ADAccessProvider.logger.log(Level.FINEST, "Available list obtained from AD : " + resourceList);
        }
        catch (final SyMException e) {
            ADAccessProvider.logger.log(Level.WARNING, "Exception occoured in getAvailabeListFromAD....", e);
            throw e;
        }
        return resourceList;
    }
    
    public String getDomainGUID(final String strNetBIOSName, final String strDomainName, final String strDCName, final String strUserName, final String strPasswd, final boolean isSSL, final int portNo) throws SyMException {
        ADAccessProvider.logger.log(Level.FINEST, "Entered the function!!::getDomainGUID");
        if (strNetBIOSName == null && strDomainName == null) {
            ADAccessProvider.logger.warning("Domain name and NetBIOS name cannot be null!!");
            throw new SyMException(1002, new Exception("Domain name and NetBIOS name cannot be null!!"));
        }
        if (strDCName == null) {
            ADAccessProvider.logger.warning("Domain Controller name cannot be null!!");
            throw new SyMException(1002, new Exception("Domain Controller name cannot be null!!"));
        }
        if (strUserName == null) {
            ADAccessProvider.logger.warning("User name cannot be null!!");
            throw new SyMException(1002, new Exception("User name cannot be null!!"));
        }
        if (strDomainName == null) {
            ADAccessProvider.logger.finest("NetBIOS name is NOT NULL. NetBIOS name : " + strNetBIOSName);
            final String strLdapPath = "LDAP://" + strDCName;
            ADAccessProvider.logger.info("Ldap Path : " + strLdapPath);
            final String strGUID = ApiFactoryProvider.getADAccessAPI().getDomainGUID(strDCName, strLdapPath, strNetBIOSName + "\\" + strUserName, strPasswd, isSSL, portNo);
            ADAccessProvider.logger.finest("Retrieved GUID : " + strGUID);
            return strGUID;
        }
        ADAccessProvider.logger.finest("Domain name is NOT NULL. Domain name : " + strDomainName);
        final String strDN = "DC=" + strDomainName.replace(".", ",DC=");
        final String strLdapPath2 = "LDAP://" + strDCName + "/" + strDN;
        try {
            ADAccessProvider.logger.finest("Ldap Path : " + strLdapPath2);
            final String strGUID2 = ApiFactoryProvider.getADAccessAPI().getDomainGUID(strDCName, strLdapPath2, strNetBIOSName + "\\" + strUserName, strPasswd, isSSL, portNo);
            ADAccessProvider.logger.finest("Retrieved GUID : " + strGUID2);
            return strGUID2;
        }
        catch (final SyMException ex) {
            ADAccessProvider.logger.log(Level.SEVERE, "Exception occured while accessing native to verify whether Domain Exists or not!!", ex);
            throw ex;
        }
    }
    
    public List searchAndFetchADObject(final String domainName, final String strGUID, final Integer nResType, final List attributeList, final String filterChar, final boolean isExcludeSubTree) throws SyMException {
        List resourceList = null;
        try {
            ADAccessProvider.logger.log(Level.INFO, "domainName : " + domainName);
            ADAccessProvider.logger.log(Level.INFO, "resourceType : " + nResType);
            ADAccessProvider.logger.log(Level.INFO, "attributeList : " + attributeList);
            if (filterChar == null || filterChar.equalsIgnoreCase("All")) {
                resourceList = this.getADObjects(domainName, nResType, strGUID, attributeList, false);
            }
            else if (filterChar.length() == 1) {
                resourceList = this.searchADObject(domainName, nResType, filterChar, strGUID, attributeList, isExcludeSubTree ? 1 : 2, 200);
            }
            else {
                resourceList = this.searchADObject(domainName, nResType, filterChar, strGUID, attributeList, isExcludeSubTree ? 1 : 2, 300);
            }
            ADAccessProvider.logger.log(Level.FINEST, "Available list obtained from AD : " + resourceList);
        }
        catch (final SyMException e) {
            ADAccessProvider.logger.log(Level.WARNING, "Exception occoured in getAvailabeListFromAD....", e);
            throw e;
        }
        return resourceList;
    }
    
    public List getUserDetails(final String domainName, final String userName) {
        List resourceList = null;
        final List attributeList = new ArrayList();
        attributeList.add("objectGUID");
        attributeList.add("name");
        attributeList.add("mail");
        attributeList.add("userPrincipalName");
        attributeList.add("sAMAccountName");
        try {
            resourceList = getInstance().searchAndFetchADObject(domainName, 2, attributeList, userName);
        }
        catch (final SyMException ex) {
            ADAccessProvider.logger.log(Level.SEVERE, "Exception in search and fetch ad object..", ex);
        }
        return resourceList;
    }
    
    static {
        ADAccessProvider.logger = Logger.getLogger("SoMLogger");
        ADAccessProvider.adAccessProvider = null;
        ADAccessProvider.sourceClass = "ADAccessProvider";
    }
}
