package com.me.devicemanagement.onpremise.webclient.authentication;

import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.authentication.util.AuthDBUtil;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.winaccess.ADAccessProvider;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class ConfirmPasswordAction
{
    Logger log;
    
    public ConfirmPasswordAction() {
        this.log = Logger.getLogger(ConfirmPasswordAction.class.getName());
    }
    
    private DataObject getDomainInfoDataObject(final String strNetBIOSName) throws Exception {
        final Table baseTable = new Table("Resource");
        final Column col = new Column("Resource", "DOMAIN_NETBIOS_NAME");
        final Criteria criteria = new Criteria(col, (Object)strNetBIOSName, 0, false);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
        query.addJoin(new Join("Resource", "ManagedDomain", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedDomain", "ManagedDomainConfig", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addSelectColumn(new Column((String)null, "*"));
        query.setCriteria(criteria);
        final DataObject dobj = SyMUtil.getPersistence().get(query);
        return dobj;
    }
    
    public Boolean validateADUser(final String loginName, final String domainName, final String password) {
        Boolean isValid = Boolean.FALSE;
        try {
            final DataObject domainDo = this.getDomainInfoDataObject(domainName);
            if (!domainDo.isEmpty()) {
                final Row domainRow = domainDo.getFirstRow("ManagedDomain");
                final String adDomainName = (String)domainRow.get("AD_DOMAIN_NAME");
                final String dcName = (String)domainRow.get("DC_NAME");
                Boolean isSSL = false;
                int portNo = 0;
                if (domainDo.containsTable("ManagedDomainConfig")) {
                    final Row domainConfRow = domainDo.getFirstRow("ManagedDomainConfig");
                    if (domainConfRow != null) {
                        isSSL = (Boolean)domainConfRow.get("USE_SSL");
                        portNo = (int)domainConfRow.get("PORT_NO");
                    }
                }
                final int errorcode = ADAccessProvider.getInstance().validatePasswordWithErrorCode(domainName, adDomainName, dcName, loginName, password, (boolean)isSSL, portNo);
                if (errorcode == 0) {
                    isValid = Boolean.TRUE;
                }
            }
        }
        catch (final Exception ex) {
            this.log.log(Level.SEVERE, "Exception while validating domain " + domainName, ex);
        }
        return isValid;
    }
    
    public Boolean validateDCUser(final String loginName, final String password) {
        Boolean isValid = Boolean.FALSE;
        try {
            final String serviceName = SYMClientUtil.getServiceName(loginName);
            final DataObject accountDO = AuthDBUtil.getAccountDO(loginName, serviceName);
            final Row passRow = accountDO.getFirstRow("AaaPassword");
            final String salt = (String)passRow.get("SALT");
            final String algorithm = (String)passRow.get("ALGORITHM");
            final String oldPassword = (String)passRow.get("PASSWORD");
            final String newPassword = AuthUtil.getEncryptedPassword(password, salt, algorithm);
            if (oldPassword.equals(newPassword)) {
                isValid = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            this.log.log(Level.SEVERE, "Exception while validating DC user", ex);
        }
        return isValid;
    }
}
