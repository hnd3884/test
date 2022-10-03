package com.me.idps.core.crud;

import java.util.Hashtable;
import com.me.idps.core.oauth.OauthUtil;
import org.json.JSONObject;
import com.me.idps.core.oauth.OauthIdThreadLocal;
import java.util.Set;
import com.me.idps.core.util.DirectoryUtil;
import java.util.Properties;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.service.azure.AzureADAccessProvider;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;

class DMDomainListenerImpl implements DMDomainListener
{
    @Override
    public void domainsAdded(final DomainEvent[] domainEventArr) {
        for (final DomainEvent event : domainEventArr) {
            try {
                this.addOauthRef(event);
                this.syncAllDomains(domainEventArr);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void domainsPreDelete(final DomainEvent[] domainEventArr) {
        for (final DomainEvent event : domainEventArr) {
            try {
                final Long dmDomainID = ((Hashtable<K, Long>)event.domainProperties).get("DOMAIN_ID");
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "FETCH_STATUS", 941);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void domainsDeleted(final DomainEvent[] domainEventArr) {
        for (final DomainEvent event : domainEventArr) {
            try {
                final Integer clientID = event.clientID;
                final Long customerID = ((Hashtable<K, Long>)event.domainProperties).get("CUSTOMER_ID");
                if (clientID != null && clientID == 3) {
                    AzureADAccessProvider.getInstance().hideOrShowOauthMessage(customerID);
                }
                IdpsFactoryProvider.getIdpsProdEnvAPI().checkAndStopADSyncSchduler(((Hashtable<K, Long>)event.domainProperties).get("CUSTOMER_ID"));
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void domainsUpdated(final DomainEvent[] domainEventArr) {
        for (final DomainEvent event : domainEventArr) {
            this.addOauthRef(event);
        }
        this.syncAllDomains(domainEventArr);
    }
    
    private Properties formatDMdomainprops(final Properties dmDomainProps, final String key, final Object value) {
        Object propsVal = null;
        if (dmDomainProps.containsKey(key)) {
            propsVal = ((Hashtable<K, Object>)dmDomainProps).get(key);
        }
        if (value != null) {
            if (propsVal == null) {
                ((Hashtable<String, Object>)dmDomainProps).put(key, value);
            }
            else if (propsVal != null && !value.equals(propsVal)) {
                ((Hashtable<String, Object>)dmDomainProps).put(key, value);
            }
        }
        return dmDomainProps;
    }
    
    private void syncAllDomains(final DomainEvent[] domainEventArr) {
        for (final DomainEvent event : domainEventArr) {
            try {
                if (event.clientID != 1) {
                    Properties dmDomainProps = event.domainProperties;
                    dmDomainProps.remove("CRD_USERNAME");
                    dmDomainProps.remove("CRD_PASSWORD");
                    dmDomainProps = this.formatDMdomainprops(dmDomainProps, "DOMAIN_ID", event.domainID);
                    dmDomainProps = this.formatDMdomainprops(dmDomainProps, "CLIENT_ID", event.clientID);
                    dmDomainProps = this.formatDMdomainprops(dmDomainProps, "CUSTOMER_ID", event.customerID);
                    final Set<Integer> defaultSyncObjTypes = IdpsFactoryProvider.getIdpsAccessAPI(event.clientID).getDefaultSyncObjectTypes();
                    DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateDirectorySyncSettings(dmDomainProps, defaultSyncObjTypes, true);
                    DirectoryUtil.getInstance().syncDomain(dmDomainProps, true);
                }
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in queuing {0} for sync", new Object[] { event.domainProperties });
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void addOauthRef(final DomainEvent event) {
        final Long oauthId = OauthIdThreadLocal.getOauthId();
        OauthIdThreadLocal.clearOauthId();
        try {
            final Long domainID = event.domainID;
            if (oauthId != null && event.clientID == 3) {
                final JSONObject oauth = new JSONObject();
                oauth.put("DOMAIN_ID", (Object)domainID);
                oauth.put("OAUTH_TOKEN_ID", (Object)oauthId);
                oauth.put("STATUS", 1);
                OauthUtil.getInstance().registerOauth(oauth);
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
}
