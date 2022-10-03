package com.me.devicemanagement.framework.winaccess;

import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class DomainInfo
{
    private static Logger logger;
    private static String sourceClass;
    public String strDomainName;
    public String strNetBIOSName;
    public String strDCName;
    public String strUserName;
    public String strPassword;
    public boolean isSSL;
    public int portNo;
    public Long strCustomerId;
    
    public DomainInfo() {
        this.strDomainName = null;
        this.strNetBIOSName = null;
        this.strDCName = null;
        this.strUserName = null;
        this.strPassword = null;
        this.isSSL = false;
        this.strCustomerId = null;
    }
    
    public DomainInfo(final String arg_netBIOSName) {
        this.strDomainName = null;
        this.strNetBIOSName = arg_netBIOSName;
        this.strDCName = null;
        this.strUserName = null;
        this.strPassword = null;
        this.isSSL = false;
        this.strCustomerId = null;
    }
    
    public DomainInfo(final String arg_netBIOSName, final Long arg_customerId) {
        this.strDomainName = null;
        this.strNetBIOSName = arg_netBIOSName;
        this.strDCName = null;
        this.strUserName = null;
        this.strPassword = null;
        this.strPassword = null;
        this.isSSL = false;
        this.strCustomerId = arg_customerId;
    }
    
    public boolean validateDomainInfoObject() throws SyMException {
        final String sourceMethod = "validateDomainInfoObject";
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "NetBIOS Name : {0}", new Object[] { this.strNetBIOSName });
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Domain Name  : {0}", new Object[] { this.strDomainName });
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "DC Name      : {0}", new Object[] { this.strDCName });
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Username     : {0}", new Object[] { this.strUserName });
        if (this.strNetBIOSName == null) {
            if (this.strNetBIOSName == null) {
                SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "NetBIOS name is null");
            }
            else {
                SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "DC name is null");
            }
            throw new SyMException(1001, new Exception("NetBIOS and DC name cannot be null"));
        }
        boolean bResult = false;
        try {
            bResult = this.setDomainInfoAttributes();
        }
        catch (final SyMException ex1) {
            SyMLogger.error(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Exception occured in setDomainInfoAttributes()", ex1);
            throw ex1;
        }
        return bResult;
    }
    
    private boolean setDomainInfoAttributes() throws SyMException {
        final String sourceMethod = "DomainInfo::setDomainInfoAttributes";
        final DataObject dobj = this.getDomainInfoDataObject();
        if (dobj.isEmpty()) {
            SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Information is not present for given NetBIOS name : " + this.strNetBIOSName);
            throw new SyMException(1014, new Exception("[i18n]dc.exception.info_not_present_for_domain@@@" + this.strNetBIOSName + "[/i18n]"));
        }
        try {
            final Row row = dobj.getRow("ManagedDomain");
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "DomainInfo of Domain : " + this.strNetBIOSName);
            Row credRow = null;
            Criteria domainCri = new Criteria(Column.getColumn("ManagedDomainCredentialRel", "DOMAINRESOURCE_ID"), row.get("RESOURCE_ID"), 0);
            domainCri = domainCri.and(new Criteria(Column.getColumn("ManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0));
            final Row mcredRow = dobj.getRow("ManagedDomainCredentialRel", domainCri);
            if (mcredRow != null) {
                credRow = dobj.getRow("Credential", new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), mcredRow.get("CREDENTIAL_ID"), 0));
            }
            try {
                final Row configRow = dobj.getRow("ManagedDomainConfig", new Criteria(Column.getColumn("ManagedDomainConfig", "RESOURCE_ID"), row.get("RESOURCE_ID"), 0));
                if (configRow != null) {
                    this.isSSL = (boolean)configRow.get("USE_SSL");
                    if (this.isSSL) {
                        this.portNo = (int)configRow.get("PORT_NO");
                    }
                    else {
                        this.isSSL = false;
                        this.portNo = 0;
                    }
                }
                else {
                    this.isSSL = false;
                }
            }
            catch (final Exception ex) {
                this.isSSL = false;
                this.portNo = 0;
            }
            if (row != null) {
                SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Domain Info Object has ManagedDomain Row");
                final Boolean bVal = (Boolean)row.get("HAS_CREDENTIALS");
                if (this.strDomainName == null) {
                    this.strDomainName = (String)row.get("AD_DOMAIN_NAME");
                }
                SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Managed Domain Details AD Name" + row.get("AD_DOMAIN_NAME") + " Has Credentials: " + bVal);
                SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Managed Domain Details DC Name" + row.get("DC_NAME") + " Has ManagedComps: " + row.get("HAS_MANAGED_COMPUTERS"));
                if (bVal.compareTo(Boolean.FALSE) == 0) {
                    SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Credentials not given for domain : " + this.strNetBIOSName);
                    throw new SyMException(1014, "[i18n]dc.exception.credential_not_given_for_domain@@@" + this.strNetBIOSName + "[/i18n]", null);
                }
                if (this.strDCName == null) {
                    this.strDCName = (String)row.get("DC_NAME");
                    if (this.strDCName == null) {
                        SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "DC Name not present in DB!!");
                        throw new SyMException(1014, "[i18n]dc.exception.dc_name_not_present_for_domain@@@" + this.strNetBIOSName + "[/i18n]", null);
                    }
                }
                if (this.strUserName == null) {
                    if (credRow == null) {
                        SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, sourceMethod + "-> Returning dummy password for domain name " + this.strDomainName);
                    }
                    this.strUserName = (String)((credRow != null) ? credRow.get("CRD_USERNAME") : "--");
                }
                SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "DomainInfo- DC Name: " + this.strDCName + " AD Name: " + this.strDomainName + " Has Credentials: " + bVal);
                try {
                    if (this.strPassword == null) {
                        String strCryptPassword = "--";
                        if (credRow != null) {
                            strCryptPassword = (String)credRow.get("CRD_PASSWORD");
                            strCryptPassword = ApiFactoryProvider.getCryptoAPI().decrypt(strCryptPassword, (Integer)credRow.get("CRD_ENC_TYPE"));
                        }
                        if (strCryptPassword == null || strCryptPassword.compareTo("--") == 0 || strCryptPassword.compareTo("-") == 0) {
                            SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Password not given for domain : " + this.strNetBIOSName);
                            throw new SyMException(1014, "[i18n]dc.exception.Pwd_not_given_for_domain@@@" + this.strDomainName + "[/i18n]", null);
                        }
                        this.strPassword = strCryptPassword;
                    }
                }
                catch (final Exception ex2) {
                    SyMLogger.error(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Exception while decrypting the password.", ex2);
                    throw new SyMException(1014, "[i18n]dc.exception.Pwd_not_given_for_domain@@@" + this.strDomainName + "[/i18n]", ex2);
                }
            }
        }
        catch (final SyMException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new SyMException(1014, ex4);
        }
        return true;
    }
    
    private DataObject getDomainInfoDataObject() throws SyMException {
        final String sourceMethod = "getDomainInfoDataObject";
        try {
            final Table baseTable = new Table("Resource");
            final Column col = new Column("Resource", "DOMAIN_NETBIOS_NAME");
            Criteria criteria = new Criteria(col, (Object)this.strNetBIOSName, 0, false);
            if (this.strCustomerId != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.strCustomerId, 0));
            }
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
            query.addJoin(new Join("Resource", "ManagedDomain", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("ManagedDomain", "ManagedDomainCredentialRel", new String[] { "RESOURCE_ID" }, new String[] { "DOMAINRESOURCE_ID" }, 1));
            query.addJoin(new Join("ManagedDomainCredentialRel", "Credential", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 1));
            query.addJoin(new Join("ManagedDomain", "ManagedDomainConfig", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            query.addSelectColumn(new Column((String)null, "*"));
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            return dobj;
        }
        catch (final Exception ex) {
            throw new SyMException(1001, ex);
        }
    }
    
    public String getLdapPath() throws SyMException {
        final String strMethodName = "DomainInfo::getLdapPath";
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "User name : {0} \t Domain Name : {1} \t NetBIOS name : {2}", new Object[] { this.strUserName, this.strDomainName, this.strNetBIOSName });
        String strLdapPath = "LDAP://";
        if (this.strDCName != null && this.strDomainName != null) {
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "Concatinating DC name and domain name(DN format) to create Ldap Path");
            String temp = this.strDomainName.replace(".", ", DC=");
            temp = "DC=" + temp;
            strLdapPath = strLdapPath.concat(this.strDCName);
            strLdapPath = strLdapPath.concat("/");
            strLdapPath = strLdapPath.concat(temp);
        }
        else {
            if (this.strDCName == null) {
                SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "DC name and domain name are null. So returing null for Ldap Path");
                return null;
            }
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "Using only DC name to create Ldap Path");
            strLdapPath = strLdapPath.concat(this.strDCName);
        }
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "Formed Ldap Path : " + strLdapPath);
        return strLdapPath;
    }
    
    public String getLdapPathWithDN(final String strDN) throws SyMException {
        final String strMethodName = "getLdapPathWithDN";
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "User name : {0} \t Domain Name : {1} \t NetBIOS name : {2}", new Object[] { this.strUserName, this.strDomainName, this.strNetBIOSName });
        String strLdapPath = "LDAP://";
        if (this.strDCName != null) {
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "DC name is not null. So concatinating DC name and DN to form Ldap path!!");
            strLdapPath = strLdapPath + this.strDCName + "/" + strDN;
        }
        else {
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "DC name is null. So using only DN to form Ldap path!!");
            strLdapPath += strDN;
        }
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "Formed LDAP path : " + strLdapPath);
        return strLdapPath;
    }
    
    public String getLdapPathWithGUID(final String strGUID) throws SyMException {
        final String strMethodName = "getLdapPathWithGUID";
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "User name : {0} \t Domain Name : {1} \t NetBIOS name : {2}", new Object[] { this.strUserName, this.strDomainName, this.strNetBIOSName });
        String strLdapPath = "LDAP://";
        if (this.strDCName != null) {
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "DC name is not null. So concatinating DC name and DN to form Ldap path!!");
            strLdapPath = strLdapPath + this.strDCName + "/" + "<GUID=" + strGUID + ">";
        }
        else {
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "DC name is null. So using only DN to form Ldap path!!");
            strLdapPath = strLdapPath + "<GUID=" + strGUID + ">";
        }
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "Formed LDAP path : " + strLdapPath);
        return strLdapPath;
    }
    
    public String getBindFormatUserName() throws SyMException {
        final String strMethodName = "getBindFormatUserName";
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "User name : {0} \t Domain Name : {1} \t NetBIOS name : {2}", new Object[] { this.strUserName, this.strDomainName, this.strNetBIOSName });
        if (this.strDomainName != null && this.strUserName != null) {
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "Concating domain and user name for binding with AD!!");
            final String strBindUserName = this.strDomainName + "\\" + this.strUserName;
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "User name for binding : " + strBindUserName);
            return strBindUserName;
        }
        if (this.strNetBIOSName != null && this.strUserName != null) {
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "Concating NetBIOS name and user name for binding with AD!!");
            final String strBindUserName = this.strNetBIOSName + "\\" + this.strUserName;
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "User name for binding : " + strBindUserName);
            return strBindUserName;
        }
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "NetBIOS name and Domain name are null. Using default user name for binding with AD!!");
        SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, strMethodName, "User name for binding : " + this.strUserName);
        return this.strUserName;
    }
    
    public boolean setAndValidateDomainInfoAttributes() throws SyMException {
        final String sourceMethod = "DomainInfo::setDomainInfoAttributesForMSPAdAuth";
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ActiveDirectoryInfo"));
        query.addSelectColumn(new Column("ActiveDirectoryInfo", "*"));
        SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Information  NetBIOS name : " + this.strNetBIOSName);
        final Criteria criteria = new Criteria(Column.getColumn("ActiveDirectoryInfo", "DEFAULTDOMAIN"), (Object)this.strNetBIOSName, 0, false);
        query.setCriteria(criteria);
        try {
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (dobj.isEmpty()) {
                SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Information is not present for given NetBIOS name : " + this.strNetBIOSName);
                throw new SyMException(1014, new Exception("[i18n]dc.exception.info_not_present_for_domain@@@" + this.strNetBIOSName + "[/i18n]"));
            }
            SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "DomainInfo of Domain : " + this.strNetBIOSName);
            final Row domainRow = dobj.getFirstRow("ActiveDirectoryInfo");
            if (domainRow != null) {
                SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Domain Info Object has ManagedDomain Row");
                final Long domainID = (Long)domainRow.get("AD_ID");
                this.isSSL = (boolean)domainRow.get("ISSSL");
                if (this.isSSL) {
                    this.portNo = (int)domainRow.get("port");
                }
                else {
                    this.isSSL = false;
                    this.portNo = 0;
                }
                this.strUserName = (String)domainRow.get("USERNAME");
                String strCryptPassword = (String)domainRow.get("PASSWORD");
                this.strPassword = ApiFactoryProvider.getCryptoAPI().decrypt(strCryptPassword, 8);
                final Boolean bVal = this.strUserName != null && this.strPassword != null;
                if (this.strDomainName == null) {
                    this.strDomainName = SyMUtil.getSyMParameter("AD_AUTH_DOMAIN_MSP_" + domainID);
                }
                SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Managed Domain Details AD Name" + this.strDomainName + " Has Credentials: " + bVal);
                SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Managed Domain Details DC Name" + domainRow.get("SERVERNAME"));
                if (bVal.compareTo(Boolean.FALSE) == 0) {
                    SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Credentials not given for domain : " + this.strNetBIOSName);
                    throw new SyMException(1014, "[i18n]dc.exception.credential_not_given_for_domain@@@" + this.strNetBIOSName + "[/i18n]", null);
                }
                if (this.strDCName == null) {
                    this.strDCName = (String)domainRow.get("SERVERNAME");
                    if (this.strDCName == null) {
                        SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "DC Name not present in DB!!");
                        throw new SyMException(1014, "[i18n]dc.exception.dc_name_not_present_for_domain@@@" + this.strNetBIOSName + "[/i18n]", null);
                    }
                }
                if (this.strUserName == null) {
                    SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, sourceMethod + "-> Returning dummy password for domain name " + this.strDomainName);
                    this.strUserName = "--";
                }
                SyMLogger.debug(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "DomainInfo- DC Name: " + this.strDCName + " AD Name: " + this.strDomainName + " Has Credentials: " + bVal);
                try {
                    if (this.strPassword == null) {
                        strCryptPassword = "--";
                        strCryptPassword = (String)domainRow.get("PASSWORD");
                        strCryptPassword = ApiFactoryProvider.getCryptoAPI().decrypt(strCryptPassword, 8);
                        this.strPassword = strCryptPassword;
                        if (strCryptPassword == null || strCryptPassword.compareTo("--") == 0 || strCryptPassword.compareTo("-") == 0) {
                            SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Password not given for domain : " + this.strNetBIOSName);
                            throw new SyMException(1014, "[i18n]dc.exception.Pwd_not_given_for_domain@@@" + this.strDomainName + "[/i18n]", null);
                        }
                        this.strPassword = strCryptPassword;
                        SyMLogger.warning(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Password  given for domain : " + this.strPassword);
                    }
                }
                catch (final Exception ex) {
                    SyMLogger.error(DomainInfo.logger, DomainInfo.sourceClass, sourceMethod, "Exception while decrypting the password.", ex);
                    throw new SyMException(1014, "[i18n]dc.exception.Pwd_not_given_for_domain@@@" + this.strDomainName + "[/i18n]", ex);
                }
            }
        }
        catch (final SyMException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new SyMException(1014, ex3);
        }
        return true;
    }
    
    static {
        DomainInfo.logger = Logger.getLogger("SoMLogger");
        DomainInfo.sourceClass = "DomainInfo";
    }
}
