package com.me.idps.op;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.util.IdpsUtil;
import java.util.Properties;
import java.util.logging.Logger;

public class DmDomainTask
{
    private static Logger logger;
    
    public void executeTask(final Properties props) {
        final String duplicateManagedDomainOnServerStartup = IdpsUtil.getSyMParameter("duplicateManagedDomainOnServerStartup");
        if (duplicateManagedDomainOnServerStartup != null && duplicateManagedDomainOnServerStartup.equalsIgnoreCase("true")) {
            final SelectQuery mangedDomainQuery = SyMUtil.formSelectQuery("ManagedDomain", new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)5, 0).and(new Criteria(Column.getColumn("ManagedDomainCredentialRel", "IS_ROOT"), (Object)1, 0)), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("Resource", "RESOURCE_ID"), Column.getColumn("Resource", "CUSTOMER_ID"), Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), Column.getColumn("Credential", "CRD_PASSWORD"), Column.getColumn("Credential", "CRD_USERNAME"), Column.getColumn("Credential", "CRD_ENC_TYPE"), Column.getColumn("Credential", "CREDENTIAL_ID"), Column.getColumn("ManagedDomain", "DC_NAME"), Column.getColumn("ManagedDomain", "DNS_SUFFIX"), Column.getColumn("ManagedDomain", "RESOURCE_ID"), Column.getColumn("ManagedDomain", "IS_AD_DOMAIN"), Column.getColumn("ManagedDomain", "AD_DOMAIN_NAME"), Column.getColumn("ManagedDomainCredentialRel", "CREDENTIAL_ID"), Column.getColumn("ManagedDomainCredentialRel", "DOMAINRESOURCE_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("ManagedDomain", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2), new Join("ManagedDomain", "ManagedDomainCredentialRel", new String[] { "RESOURCE_ID" }, new String[] { "DOMAINRESOURCE_ID" }, 2), new Join("ManagedDomainCredentialRel", "Credential", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 2))), (Criteria)null);
            DmDomainTask.logger.log(Level.INFO, "query formed to extract details from managed domain data modelling system to duplicate them into dmdomain data modelling system {0}", mangedDomainQuery);
            try {
                final DataObject dObj = SyMUtil.getPersistence().get(mangedDomainQuery);
                if (!dObj.isEmpty()) {
                    final Iterator mdItr = dObj.getRows("ManagedDomain");
                    if (mdItr != null) {
                        while (mdItr.hasNext()) {
                            final Row mdRow = mdItr.next();
                            final Long mdResouceID = (Long)mdRow.get("RESOURCE_ID");
                            final Row resRow = dObj.getRow("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)mdResouceID, 0));
                            final Row mdCredRelRow = dObj.getRow("ManagedDomainCredentialRel", new Criteria(Column.getColumn("ManagedDomainCredentialRel", "DOMAINRESOURCE_ID"), (Object)mdResouceID, 0));
                            final Long credentialID = (Long)mdCredRelRow.get("CREDENTIAL_ID");
                            final Row credRow = dObj.getRow("Credential", new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)credentialID, 0));
                            final Integer encType = (Integer)credRow.get("CRD_ENC_TYPE");
                            final String isADdomain = String.valueOf(mdRow.get("IS_AD_DOMAIN"));
                            final Integer networkType = isADdomain.equalsIgnoreCase(String.valueOf(true)) ? 2 : 1;
                            final HashMap domainDetails = new HashMap();
                            domainDetails.put("IS_AD_DOMAIN", isADdomain);
                            domainDetails.put("NETWORK_TYPE", networkType);
                            domainDetails.put("DC_NAME", mdRow.get("DC_NAME"));
                            domainDetails.put("CUSTOMER_ID", resRow.get("CUSTOMER_ID"));
                            domainDetails.put("DNS_NAME", mdRow.get("DNS_SUFFIX"));
                            domainDetails.put("USERNAME", credRow.get("CRD_USERNAME"));
                            domainDetails.put("DOMAINNAME", resRow.get("DOMAIN_NETBIOS_NAME"));
                            domainDetails.put("AD_DOMAIN_NAME", mdRow.get("AD_DOMAIN_NAME"));
                            DmDomainTask.logger.log(Level.INFO, "duplicating the following managed domain details {0}", domainDetails.toString());
                            domainDetails.put("PASSWORD", ApiFactoryProvider.getCryptoAPI().decrypt((String)credRow.get("CRD_PASSWORD"), encType));
                            ApiFactoryProvider.getIdPsAPI().addOrUpdateDMMDrel(mdResouceID, domainDetails);
                        }
                    }
                }
                IdpsUtil.updateSyMParameter("duplicateManagedDomainOnServerStartup", "false");
            }
            catch (final Exception ex) {
                DmDomainTask.logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    static {
        DmDomainTask.logger = Logger.getLogger("SoMLogger");
    }
}
