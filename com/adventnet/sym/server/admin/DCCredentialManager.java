package com.adventnet.sym.server.admin;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.admin.CredentialManager;

public class DCCredentialManager extends CredentialManager
{
    private static Logger logger;
    
    public void updateDomainCredentialStatus() {
        try {
            final SelectQuery domainCredQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Credential"));
            domainCredQuery.addSelectColumn(new Column((String)null, "*"));
            domainCredQuery.addJoin(new Join("Credential", "ManagedDomainCredentialRel", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 2));
            domainCredQuery.addJoin(new Join("Credential", "UserCredentialMapping", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 2));
            domainCredQuery.addJoin(new Join("ManagedDomainCredentialRel", "ManagedDomain", new String[] { "DOMAINRESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            domainCredQuery.addJoin(new Join("ManagedDomain", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            domainCredQuery.addJoin(new Join("ManagedDomain", "ManagedDomainConfig", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            domainCredQuery.setCriteria(new Criteria(Column.getColumn("ManagedDomain", "IS_AD_DOMAIN"), (Object)Boolean.TRUE, 0));
            final DataObject resultDO = SyMUtil.getPersistence().get(domainCredQuery);
            if (!resultDO.isEmpty()) {
                final Iterator it = resultDO.getRows("Resource");
                while (it.hasNext()) {
                    final Row resRow = it.next();
                    final Row mdRow = resultDO.getRow("ManagedDomain", resRow);
                    final Row domainConfigRow = resultDO.getRow("ManagedDomainConfig", resRow);
                    final Iterator domainCredRows = resultDO.getRows("ManagedDomainCredentialRel", mdRow);
                    if (domainCredRows != null) {
                        while (domainCredRows.hasNext()) {
                            boolean verifyRes = false;
                            final Row domainCredRow = domainCredRows.next();
                            Row credRow = null;
                            if (domainCredRow != null) {
                                credRow = resultDO.getRow("Credential", domainCredRow);
                            }
                            final String domainName = (String)resRow.get("DOMAIN_NETBIOS_NAME");
                            final String adDomainName = (String)mdRow.get("AD_DOMAIN_NAME");
                            final String dcName = (String)mdRow.get("DC_NAME");
                            Boolean isSSL = false;
                            int portNo = 0;
                            if (domainConfigRow != null) {
                                isSSL = (Boolean)domainConfigRow.get("USE_SSL");
                                portNo = (int)domainConfigRow.get("PORT_NO");
                            }
                            if (credRow != null) {
                                final String userName = (String)credRow.get("CRD_USERNAME");
                                String password = (String)credRow.get("CRD_PASSWORD");
                                password = ApiFactoryProvider.getCryptoAPI().decrypt(password, (Integer)credRow.get("CRD_ENC_TYPE"));
                                try {
                                    verifyRes = WinAccessProvider.getInstance().validatePassword(domainName, adDomainName, dcName, userName, password, (boolean)isSSL, portNo);
                                }
                                catch (final Exception ex) {
                                    DCCredentialManager.logger.log(Level.SEVERE, "Caught exception while validating AD Domain :" + domainName, ex);
                                }
                                domainCredRow.set("VALIDATION_STATUS", (Object)verifyRes);
                                resultDO.updateRow(domainCredRow);
                            }
                            DCCredentialManager.logger.log(Level.INFO, "Credential ID validatePassword result for domain:{0} is :{1}", new Object[] { domainName, verifyRes });
                        }
                    }
                }
                SyMUtil.getPersistence().update(resultDO);
            }
        }
        catch (final Exception ex2) {
            DCCredentialManager.logger.log(Level.SEVERE, "Caught exception while validating AD Credentials :", ex2);
        }
    }
    
    static {
        DCCredentialManager.logger = Logger.getLogger(DCCredentialManager.class.getName());
    }
}
