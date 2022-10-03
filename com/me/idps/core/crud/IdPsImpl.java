package com.me.idps.core.crud;

import java.util.Hashtable;
import com.me.idps.core.oauth.OauthException;
import org.json.JSONException;
import com.me.idps.core.service.azure.AzureOauthLoginImpl;
import com.adventnet.i18n.I18N;
import javax.transaction.TransactionManager;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.admin.DomainHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Properties;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.api.IdPsAPI;

public class IdPsImpl implements IdPsAPI
{
    public void handleDomainDeletionInDM(final String domainName, final Long customerID) throws DataAccessException {
        final Properties domainProperties = DMDomainDataHandler.getInstance().getDomainProp(domainName, customerID, new ArrayList<Integer>(Arrays.asList(1, 2)));
        final int domainType = ((Hashtable<K, Integer>)domainProperties).get("CLIENT_ID");
        final String adDomainName = domainProperties.getProperty("AD_DOMAIN_NAME");
        final HashMap deleteDomainDetails = new HashMap();
        deleteDomainDetails.put("CLIENT_ID", domainType);
        deleteDomainDetails.put("CUSTOMER_ID", customerID);
        deleteDomainDetails.put("DOMAINNAME", domainName);
        deleteDomainDetails.put("AD_DOMAIN_NAME", adDomainName);
        DomainDataPopulator.getInstance().deleteDomain(deleteDomainDetails);
    }
    
    public String addOrUpdateDMMDrel(final Long resourceID, final HashMap domainDetails) {
        String errorMsg = null;
        final TransactionManager tm = SyMUtil.getUserTransaction();
        try {
            final Long dmDomainID = DomainDataPopulator.getInstance().addOrUpdateDomainViewRecord(domainDetails);
            tm.begin();
            if (dmDomainID != null) {
                final boolean res = DMDomainDataHandler.getInstance().addDMMDRel(dmDomainID, resourceID, 2);
                if (res) {
                    try {
                        tm.commit();
                    }
                    catch (final RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException ex) {
                        errorMsg = DomainHandler.getInstance().getErrorMessageForErrorID(80006);
                        IDPSlogger.SOM.log(Level.SEVERE, "exception occured. trying to rolling back", ex);
                        try {
                            tm.rollback();
                        }
                        catch (final IllegalStateException | SecurityException | SystemException ex2) {
                            IDPSlogger.SOM.log(Level.SEVERE, "exception occured while trying to rolling back. aborting", ex2);
                        }
                    }
                }
                else {
                    IDPSlogger.SOM.log(Level.SEVERE, "setting state to rollback");
                    tm.setRollbackOnly();
                }
            }
            else {
                IDPSlogger.SOM.log(Level.SEVERE, "setting status to rollback");
                tm.setRollbackOnly();
            }
        }
        catch (final Exception ex3) {
            try {
                tm.setRollbackOnly();
            }
            catch (final IllegalStateException | SystemException ex4) {
                IDPSlogger.SOM.log(Level.SEVERE, null, ex4);
            }
            IDPSlogger.SOM.log(Level.SEVERE, "exception occured", ex3);
            errorMsg = DomainHandler.getInstance().getErrorMessageForErrorID(80006);
            try {
                if (tm.getStatus() == 1) {
                    IDPSlogger.SOM.log(Level.SEVERE, "rolling back");
                    tm.rollback();
                }
            }
            catch (final SystemException ex5) {
                IDPSlogger.SOM.log(Level.SEVERE, "excption occured while getting transaction status", (Throwable)ex5);
            }
        }
        finally {
            try {
                if (tm.getStatus() == 1) {
                    IDPSlogger.SOM.log(Level.SEVERE, "rolling back");
                    tm.rollback();
                }
            }
            catch (final SystemException ex6) {
                IDPSlogger.SOM.log(Level.SEVERE, "excption occured while getting transaction status", (Throwable)ex6);
            }
        }
        return errorMsg;
    }
    
    public int deleteDomainCredentials(final List domainCredentials) {
        return DMDomainDataHandler.getInstance().deleteDomainCredentials(domainCredentials);
    }
    
    private Properties checkForSameDomainExistingWithDomainProps(final boolean isDomainDetailsAvailableInManagedDomain, final Properties existingDomainProps, final Long customerID, final String domainName, final String adDomainName, final int toBeAddedDomainNetworkType, final String action, final boolean loop) throws Exception {
        int existingDomainClientID = -1;
        if (existingDomainProps != null && existingDomainProps.containsKey("CLIENT_ID")) {
            existingDomainClientID = ((Hashtable<K, Integer>)existingDomainProps).get("CLIENT_ID");
        }
        IDPSlogger.SOM.log(Level.INFO, "customerID:{0},ad domain name:{1}, domainName:{2},existing Client ID:{3},inputClientID:{4},already available in ManagedDomain:{5},action:{6}", new Object[] { customerID, adDomainName, domainName, existingDomainClientID, toBeAddedDomainNetworkType, isDomainDetailsAvailableInManagedDomain, action });
        String i18N = null;
        if (isDomainDetailsAvailableInManagedDomain) {
            i18N = I18N.getMsg("desktopcentral.webclient.admin.som.addDomain.Domain_name_already_exists", new Object[0]);
        }
        if (!action.equals("add")) {
            if (existingDomainClientID != toBeAddedDomainNetworkType && toBeAddedDomainNetworkType != 1) {
                if (existingDomainClientID == -1) {
                    i18N = I18N.getMsg("dc.mdm.ad.no_domain_edit", new Object[0]);
                    IDPSlogger.SOM.log(Level.INFO, "domain was clicked to edit, but by now that domain was deleted : {0} or the domain to be modified is completely different and is like adding a new domain", i18N);
                }
                if ((action.equals("edit") || action.equals("editFromMainPage") || action.equals("showComputerAndEditCred")) && existingDomainClientID == 1 && toBeAddedDomainNetworkType == 2 && isDomainDetailsAvailableInManagedDomain) {
                    i18N = null;
                }
                else if ((action.equals("edit") || action.equals("editFromMainPage") || action.equals("showComputer") || action.equals("showComputerAndEditCred")) && existingDomainClientID == -1 && toBeAddedDomainNetworkType == 2) {
                    i18N = null;
                }
            }
            else {
                i18N = null;
            }
        }
        else if (action.equals("add") && existingDomainClientID == -1 && toBeAddedDomainNetworkType == 2 && isDomainDetailsAvailableInManagedDomain) {
            i18N = null;
        }
        final Properties resProperties = new Properties();
        ((Hashtable<String, Integer>)resProperties).put("EXISTING_DOMAIN_CLIENT_ID", existingDomainClientID);
        ((Hashtable<String, Boolean>)resProperties).put("IS_DOMAIN_EXISTING_IN_MANAGEDDOMAIN", isDomainDetailsAvailableInManagedDomain);
        if (i18N != null) {
            ((Hashtable<String, String>)resProperties).put("ERROR_MESSAGE", i18N);
        }
        return resProperties;
    }
    
    public Properties checkForSameDomainExisting(final Long customerID, final String domainName, final String adDomainName, final int toBeAddedDomainNetworkType, final String action) throws Exception {
        boolean isDomainDetailsAvailableInManagedDomain = false;
        if (domainName != null) {
            isDomainDetailsAvailableInManagedDomain = DomainHandler.getInstance().isDomainDetailsAvailableInDB(domainName, customerID);
            final Properties existingDomainProps = DMDomainDataHandler.getInstance().getDomainProp(domainName, customerID, new ArrayList<Integer>(Arrays.asList(1, 2)));
            final Properties domainCheckRes = this.checkForSameDomainExistingWithDomainProps(isDomainDetailsAvailableInManagedDomain, existingDomainProps, customerID, domainName, adDomainName, toBeAddedDomainNetworkType, action, true);
            final String i18N = domainCheckRes.getProperty("ERROR_MESSAGE");
            if (i18N != null) {
                return domainCheckRes;
            }
        }
        if (adDomainName != null) {
            final Properties existingADdomainProps = DMDomainDataHandler.getInstance().getAdDomainProps(adDomainName, customerID, new ArrayList<Integer>(Arrays.asList(1, 2)));
            final Properties adDomainCheckRes = this.checkForSameDomainExistingWithDomainProps(isDomainDetailsAvailableInManagedDomain, existingADdomainProps, customerID, domainName, adDomainName, toBeAddedDomainNetworkType, action, true);
            return adDomainCheckRes;
        }
        final Properties res = new Properties();
        ((Hashtable<String, Boolean>)res).put("IS_DOMAIN_EXISTING_IN_MANAGEDDOMAIN", false);
        return res;
    }
    
    public Properties getAzureUserDetails(final String azureCode) throws JSONException, OauthException {
        final AzureOauthLoginImpl azureOauthLoginImpl = new AzureOauthLoginImpl();
        final AzureOauthLoginImpl.AzureLoginDetails ald = azureOauthLoginImpl.getUserDetails(azureCode);
        if (ald != null) {
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("upn", ald.getUserPrincipalName());
            ((Hashtable<String, String>)properties).put("NAME", ald.getDomainName());
            return properties;
        }
        return null;
    }
}
