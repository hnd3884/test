package com.me.mdm.directory;

import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import java.util.ArrayList;
import com.me.devicemanagement.framework.winaccess.DomainInfo;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.List;
import java.util.logging.Logger;

public class ADAccessProvider extends com.me.devicemanagement.onpremise.winaccess.ADAccessProvider
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
    
    public List getADUserObjects(final String arg_netBIOSName, final List lisAttributes, final long modifiedTime) throws SyMException {
        final String sourceMethod = "getADObjects";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function!!");
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Calling the generic function!!");
        final List lisADObjects = this.getADUserObjectsFromNative(arg_netBIOSName, lisAttributes, modifiedTime);
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Returned from generic function!!");
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "No. of Objects received from generic function : " + lisADObjects.size());
        final List lisRetObject = this.addResTypeKeyToProperties(lisADObjects, 2);
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "List of type(key) added properties : {0}", new Object[] { lisRetObject });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function!!");
        return lisRetObject;
    }
    
    protected List getADUserObjectsFromNative(final String arg_netBIOSName, List lisAttributes, final long nModifiedTime) throws SyMException {
        final String sourceMethod = "getADObjectsFromNative";
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Entered the function!!");
        final DomainInfo domainObj = new DomainInfo(arg_netBIOSName);
        if (domainObj.validateDomainInfoObject()) {
            SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Given domain info object contains required data for AD binding!!");
        }
        if (lisAttributes == null || lisAttributes.size() == 0) {
            SyMLogger.warning(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD Attribute list is empty. So filling with all attributes of resource type : 2");
            try {
                lisAttributes = this.adAccessUtil.getAttributes(2);
            }
            catch (final SyMException ex) {
                throw ex;
            }
        }
        String strFilter = null;
        if (nModifiedTime == 0L) {
            strFilter = this.adAccessUtil.getADSearchFilter(2);
        }
        else {
            strFilter = this.getADUserSearchFilterWithTime(nModifiedTime);
        }
        final String strLdapPath = domainObj.getLdapPath();
        final String strBindUserName = domainObj.getBindFormatUserName();
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "LdapPath : {0}", new Object[] { strLdapPath });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "UserName : {0}", new Object[] { strBindUserName });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Filter used : {0}", new Object[] { strFilter });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "AD Attributes list : {0}", new Object[] { lisAttributes });
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Calling native function!!");
        List lisADObjects = new ArrayList();
        if (nModifiedTime == 0L) {
            lisADObjects = this.nativeGetADObjects(domainObj.strDCName, strLdapPath, strBindUserName, domainObj.strPassword, lisAttributes, strFilter, 2, domainObj.isSSL, domainObj.portNo);
        }
        else {
            lisADObjects = this.nativeGetModifiedADObjects(domainObj.strDCName, strLdapPath, strBindUserName, domainObj.strPassword, lisAttributes, strFilter, 2, nModifiedTime, domainObj.isSSL, domainObj.portNo);
        }
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Returned from native function!!");
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Fetched data : {0}", new Object[] { lisADObjects });
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Resource Type  : 2");
        SyMLogger.info(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "No. of retrieved AD objects : " + lisADObjects.size());
        SyMLogger.debug(ADAccessProvider.logger, ADAccessProvider.sourceClass, sourceMethod, "Leaving the function!!");
        return lisADObjects;
    }
    
    private String getADUserSearchFilterWithTime(final long time) throws SyMException {
        return "(&(objectCategory=person)(objectClass=user)(whenChanged>=" + WinAccessProvider.getInstance().getGMTString(time) + ".0Z))";
    }
    
    static {
        ADAccessProvider.logger = Logger.getLogger("SoMLogger");
        ADAccessProvider.adAccessProvider = null;
        ADAccessProvider.sourceClass = "ADAccessProvider";
    }
}
