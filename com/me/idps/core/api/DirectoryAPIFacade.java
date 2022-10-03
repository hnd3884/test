package com.me.idps.core.api;

import java.util.Hashtable;
import com.me.idps.core.util.IdpsUtil;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.idps.core.factory.IdpsFactoryProvider;
import org.json.JSONException;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import java.util.Enumeration;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.me.idps.core.util.DirectoryResetHandler;
import java.util.Properties;
import org.json.JSONArray;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.idps.core.crud.DomainDataProvider;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.DirectoryUtil;
import org.json.JSONObject;

public class DirectoryAPIFacade
{
    private static final int CREATE = 1;
    private static final int READ = 2;
    private static final int UPDATE = 3;
    private static final int DELETE = 4;
    private static final int CREATE_SYNC = 5;
    private static final int READ_SYNC = 6;
    private static DirectoryAPIFacade directoryAPIFacade;
    
    private DirectoryAPIFacade() {
    }
    
    public static DirectoryAPIFacade getInstance() {
        if (DirectoryAPIFacade.directoryAPIFacade == null) {
            DirectoryAPIFacade.directoryAPIFacade = new DirectoryAPIFacade();
        }
        return DirectoryAPIFacade.directoryAPIFacade;
    }
    
    public void syncAllDomain(final JSONObject apirequest) {
        try {
            final Long customer_id = IdpsAPIUtil.getCustomerID(apirequest);
            final JSONObject msg_body = apirequest.getJSONObject("msg_body");
            final boolean doFullSync = msg_body.optBoolean("is_full_sync", true);
            DirectoryUtil.getInstance().syncAllDomains(customer_id, doFullSync);
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, null, e);
            throw new IdpsAPIException("COM0004");
        }
    }
    
    public JSONObject getAllDirectory(final JSONObject request) {
        try {
            final JSONObject response = new JSONObject();
            final Long customer_id = IdpsAPIUtil.getCustomerID(request);
            final String filters = IdpsAPIUtil.getStringFilter(request, "domain_type");
            List<Integer> clientArray = null;
            if (filters != null) {
                final String[] domainTypes = filters.split(",");
                clientArray = new ArrayList<Integer>();
                for (int i = 0; i < domainTypes.length; ++i) {
                    clientArray.add(Integer.parseInt(domainTypes[i]));
                }
            }
            List domainArray = new ArrayList();
            if (clientArray != null) {
                final SelectQuery query = DomainDataProvider.getDMManagedDomainQuery(customer_id, null, null, clientArray, false);
                domainArray = DMDomainDataHandler.getInstance().getDomains(query);
            }
            else {
                domainArray = DMDomainDataHandler.getInstance().getAllDMManagedProps(customer_id);
            }
            final boolean isZDOPIntegratedWithApiOrUserIntegration = DirectoryUtil.getInstance().isZDOPIntegratedWithApiOrUserIntegration();
            final JSONArray domain = new JSONArray();
            for (final Properties p : domainArray) {
                final int clientID = Integer.valueOf(String.valueOf(((Hashtable<K, Object>)p).get("CLIENT_ID")));
                if (clientID == 201 && !isZDOPIntegratedWithApiOrUserIntegration) {
                    continue;
                }
                final Enumeration<String> enums = (Enumeration<String>)p.propertyNames();
                final JSONObject curDomain = new JSONObject();
                while (enums.hasMoreElements()) {
                    final String key = enums.nextElement();
                    if (!key.equalsIgnoreCase("CRD_PASSWORD") || !key.equalsIgnoreCase("CREDENTIAL_ID")) {
                        if (key.equalsIgnoreCase("CRD_USERNAME")) {
                            curDomain.put("user_name", ((Hashtable<K, Object>)p).get(key));
                        }
                        else {
                            curDomain.put(key.toLowerCase(), ((Hashtable<K, Object>)p).get(key));
                        }
                    }
                }
                domain.put((Object)curDomain);
            }
            response.put("Domains", (Object)domain);
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error IdpsAPIException Occured in getAllDirectory", e);
            throw e;
        }
        catch (final Exception e2) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error Exception Occured in getAllDirectory", e2);
            throw new IdpsAPIException("COM0004");
        }
        finally {
            try {
                DirectoryResetHandler.getInstance().doLazyResetHandling();
            }
            catch (final Exception e3) {
                IDPSlogger.ERR.log(Level.INFO, null, e3);
            }
        }
    }
    
    private int getObj(final String key, final int client_id) {
        if (key.equalsIgnoreCase("is_group_sync")) {
            return 7;
        }
        if (key.equalsIgnoreCase("is_device_sync") && client_id == 3) {
            return 205;
        }
        throw new IdpsAPIException("COM0014");
    }
    
    private void errorAddorUpdate(final String response) {
        try {
            final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
            dirProdImplRequest.args = new Object[] { response };
            dirProdImplRequest.eventType = IdpEventConstants.ERR_IDP_CONF;
            DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
        }
        catch (final IdpsAPIException e) {
            throw e;
        }
        catch (final Exception e2) {
            IDPSlogger.ERR.log(Level.SEVERE, null, e2);
            throw new IdpsAPIException("COM0004", e2.getMessage());
        }
    }
    
    private HashMap convertPropForOPAD(final HashMap domainProps) {
        domainProps.put("USE_SSL", domainProps.get("use_ssl"));
        domainProps.put("DC_NAME", domainProps.get("dc_name"));
        domainProps.put("DNS_NAME", domainProps.get("dns_name"));
        domainProps.put("PASSWORD", domainProps.get("password"));
        domainProps.put("USERNAME", domainProps.get("user_name"));
        domainProps.put("DOMAINNAME", domainProps.get("domain_name"));
        domainProps.put("AD_DOMAIN_NAME", domainProps.get("ad_domain_name"));
        final Object portNo = domainProps.get("port_no");
        if (portNo != null) {
            domainProps.put("PORT_NO", ((Integer)portNo).toString());
        }
        return domainProps;
    }
    
    private HashMap convertPropForOktaAD(final HashMap domainProps) {
        domainProps.put("PASSWORD", domainProps.get("password"));
        domainProps.put("DOMAINNAME", domainProps.get("domain_name"));
        return domainProps;
    }
    
    private HashMap convertPropFroAzureAD(final HashMap domainProps) {
        domainProps.put("NAME", domainProps.get("domain_name"));
        domainProps.put("AAD_USERNAME", domainProps.get("user_name"));
        domainProps.put("AAD_PASSWORD", domainProps.get("password"));
        return domainProps;
    }
    
    private HashMap convertDirectoryPropsForProcessing(final Properties domainPropsInDb, final JSONObject request, final String action) throws JSONException {
        final String userName = IdpsAPIUtil.getUserName(request);
        final Long customer_id = IdpsAPIUtil.getCustomerID(request);
        final JSONObject msg_body = request.getJSONObject("msg_body");
        HashMap domainProps;
        int client_id;
        if (domainPropsInDb == null) {
            if (!action.equalsIgnoreCase("add")) {
                throw new IdpsAPIException("COM0014");
            }
            if (!msg_body.has("CLIENT_ID".toLowerCase())) {
                throw new IdpsAPIException("COM0014");
            }
            domainProps = new HashMap();
            client_id = msg_body.getInt("CLIENT_ID".toLowerCase());
        }
        else {
            if (!action.equalsIgnoreCase("edit")) {
                throw new IdpsAPIException("COM0014");
            }
            if (domainPropsInDb == null) {
                throw new IdpsAPIException("COM0014");
            }
            if (domainPropsInDb == null || domainPropsInDb.isEmpty()) {
                throw new IdpsAPIException("COM0014");
            }
            domainProps = new HashMap();
            client_id = ((Hashtable<K, Integer>)domainPropsInDb).get("CLIENT_ID");
            domainProps.put("client_id", client_id);
            domainProps.put("use_ssl", ((Hashtable<K, Object>)domainPropsInDb).get("USE_SSL"));
            domainProps.put("domain_name", domainPropsInDb.getProperty("NAME"));
            domainProps.put("NAME", domainPropsInDb.getProperty("NAME"));
            domainProps.put("domainName", domainPropsInDb.getProperty("NAME"));
            domainProps.put("password", domainPropsInDb.getProperty("CRD_PASSWORD"));
            domainProps.put("user_name", domainPropsInDb.getProperty("CRD_USERNAME"));
            domainProps.put("DOMAINNAME", domainPropsInDb.getProperty("NAME"));
            domainProps.put("ad_domain_name", domainPropsInDb.getProperty("AD_DOMAIN_NAME"));
        }
        domainProps.put("user_name", userName);
        domainProps.put("ACTION", action);
        domainProps.put("CUSTOMER_ID", customer_id);
        final Iterator itr = msg_body.keys();
        final Set<Integer> enable = new HashSet<Integer>();
        final Set<Integer> disable = new HashSet<Integer>();
        while (itr.hasNext()) {
            final String key = itr.next();
            if (key.startsWith("is_") && key.endsWith("_sync")) {
                final int obj = this.getObj(key, client_id);
                final boolean enableObj = msg_body.getBoolean(key);
                if (enableObj) {
                    if (disable.contains(obj)) {
                        throw new IdpsAPIException("COM0014");
                    }
                    enable.add(obj);
                }
                else {
                    if (enable.contains(obj)) {
                        throw new IdpsAPIException("COM0014");
                    }
                    disable.add(obj);
                }
            }
            else {
                domainProps.put(key, msg_body.get(key));
            }
        }
        domainProps.put("enable", enable);
        domainProps.put("disable", disable);
        switch (client_id) {
            case 3: {
                domainProps = this.convertPropFroAzureAD(domainProps);
                break;
            }
            case 301: {
                domainProps = this.convertPropForOktaAD(domainProps);
                break;
            }
            case 2: {
                domainProps = this.convertPropForOPAD(domainProps);
                break;
            }
        }
        return domainProps;
    }
    
    private JSONObject processAddOrUpdateRequest(final HashMap domainDetails) throws Exception {
        final JSONObject re = new JSONObject();
        final Set<Integer> enable = domainDetails.get("enable");
        final Long customer_id = domainDetails.get("CUSTOMER_ID");
        final Set<Integer> disable = domainDetails.get("disable");
        final String userName = domainDetails.get("user_name");
        final String action = domainDetails.get("ACTION");
        final String domain_name = domainDetails.get("DOMAINNAME");
        final int client_id = Integer.valueOf(String.valueOf(domainDetails.get("client_id")));
        if (enable.isEmpty() && disable.isEmpty()) {
            final String response = IdpsFactoryProvider.getIdpsAccessAPI(client_id).addOrUpdateAD(domainDetails);
            this.errorAddorUpdate(response);
            if (action.equalsIgnoreCase("add")) {
                DCEventLogUtil.getInstance().addEvent(4001, userName, (HashMap)null, "dir.audit.add", (Object)domain_name, true, customer_id);
            }
            else if (action.equalsIgnoreCase("edit")) {
                DCEventLogUtil.getInstance().addEvent(4001, userName, (HashMap)null, "dir.audit.edit", (Object)domain_name, true, customer_id);
            }
        }
        final SelectQuery domainQuery = DomainDataProvider.getDMManagedDomainQuery(customer_id, domain_name, null, client_id);
        final Properties domainProps = DMDomainDataHandler.getInstance().getDomain(domainQuery);
        if (!enable.isEmpty() || !disable.isEmpty()) {
            if (!enable.isEmpty()) {
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateDirectorySyncSettings(domainProps, enable, true);
            }
            if (!disable.isEmpty()) {
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateDirectorySyncSettings(domainProps, disable, false);
            }
        }
        else {
            re.put("NAME", ((Hashtable<K, Object>)domainProps).get("NAME"));
            re.put("DOMAIN_ID", ((Hashtable<K, Object>)domainProps).get("DOMAIN_ID"));
            re.put("CLIENT_ID", ((Hashtable<K, Object>)domainProps).get("CLIENT_ID"));
        }
        return re;
    }
    
    private JSONObject addOrUpdateDomainDetails(final Properties domainProps, final JSONObject request, final String action) {
        try {
            final HashMap prop = this.convertDirectoryPropsForProcessing(domainProps, request, action);
            final JSONObject response = this.processAddOrUpdateRequest(prop);
            return response;
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error Occured ", e);
            throw e;
        }
        catch (final Exception e2) {
            IDPSlogger.ERR.log(Level.SEVERE, "Un-known Error Occured ", e2);
            throw new IdpsAPIException("COM0004");
        }
    }
    
    public ArrayList<Long> getRelevantCustomerIDs(final Long userID) throws Exception {
        final ArrayList<Long> customerIDs = new ArrayList<Long>();
        final ArrayList<HashMap> customerIDlist = CustomerInfoUtil.getInstance().getCustomerDetailsForUser(userID);
        if (customerIDlist != null && !customerIDlist.isEmpty()) {
            for (int i = 0; i < customerIDlist.size(); ++i) {
                final HashMap customerMap = customerIDlist.get(i);
                final Long customerID = customerMap.get("CUSTOMER_ID");
                if (customerID != null) {
                    customerIDs.add(customerID);
                }
            }
        }
        return customerIDs;
    }
    
    public boolean isUserCustomerRelevant(final Long userID, final Long customerIDfromRequest) throws Exception {
        if (ApiFactoryProvider.getUtilAccessAPI().isMSP()) {
            final ArrayList<Long> customerIDs = this.getRelevantCustomerIDs(userID);
            IDPSlogger.ASYNCH.log(Level.INFO, "in MSP got {0} customers for {1} userID, and customerIDfromRequest is {2}", new Object[] { Arrays.toString(customerIDs.toArray(new Long[customerIDs.size()])), String.valueOf(userID), String.valueOf(customerIDfromRequest) });
            return customerIDs.contains(customerIDfromRequest);
        }
        IDPSlogger.ASYNCH.log(Level.INFO, "in EE allowing {0} customerIDfromRequest for {1} userID", new Object[] { String.valueOf(customerIDfromRequest), String.valueOf(userID) });
        return true;
    }
    
    private Object individualDomainRequestHandler(final JSONObject apirequest, final int requestType) {
        try {
            final Long domain_id = IdpsAPIUtil.getResourceID(apirequest, "director_id");
            if (domain_id != null && !domain_id.equals(-1L)) {
                final Properties domainProps = DMDomainDataHandler.getInstance().getDomainById(domain_id);
                final Long customerID = ((Hashtable<K, Long>)domainProps).get("CUSTOMER_ID");
                final boolean authorized = this.isUserCustomerRelevant(IdpsAPIUtil.getUserID(apirequest), customerID);
                if (!authorized) {
                    throw new IdpsAPIException("COM0013");
                }
                switch (requestType) {
                    case 3: {
                        return this.modifyDomainDetails(domainProps, apirequest);
                    }
                    case 2: {
                        return this.getDirectory(domainProps);
                    }
                    case 4: {
                        this.deleteDomain(domainProps);
                        break;
                    }
                    case 5: {
                        this.syncParticularDomain(domainProps, apirequest);
                        break;
                    }
                    case 6: {
                        return this.getDomainSyncDetails(domainProps);
                    }
                    default: {
                        throw new IdpsAPIException("COM0014");
                    }
                }
            }
            else {
                if (requestType == 1) {
                    return this.addNewADDomain(null, apirequest);
                }
                throw new IdpsAPIException("COM0014");
            }
        }
        catch (final IdpsAPIException ae) {
            throw ae;
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, null, e);
            throw new IdpsAPIException("COM0004");
        }
        return null;
    }
    
    private JSONObject addNewADDomain(final Properties domainProps, final JSONObject request) {
        return this.addOrUpdateDomainDetails(domainProps, request, "add");
    }
    
    public JSONObject addNewADDomain(final JSONObject request) {
        return (JSONObject)this.individualDomainRequestHandler(request, 1);
    }
    
    private JSONObject getDirectory(final Properties domainProps) throws Exception {
        final Long domain_id = ((Hashtable<K, Long>)domainProps).get("DOMAIN_ID");
        final int clientID = ((Hashtable<K, Integer>)domainProps).get("CLIENT_ID");
        if (clientID == -1) {
            IDPSlogger.ERR.log(Level.SEVERE, "Error unknown domain client_id Occured in getDirectory");
            throw new IdpsAPIException("COM0008");
        }
        domainProps.remove("CRD_PASSWORD");
        domainProps.remove("CRD_USERNAME");
        domainProps.remove("CREDENTIAL_ID");
        final JSONObject resp = IdpsUtil.convertPropertiesToJSONObject(domainProps);
        resp.put("user_name", (Object)domainProps.getProperty("CRD_USERNAME"));
        resp.put("FETCH_STATUS", DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(domain_id, "FETCH_STATUS"));
        final JSONObject response = new JSONObject();
        response.put("response", (Object)resp);
        return response;
    }
    
    public JSONObject getDirectory(final JSONObject apirequest) {
        return (JSONObject)this.individualDomainRequestHandler(apirequest, 2);
    }
    
    private JSONObject modifyDomainDetails(final Properties domainProps, final JSONObject request) {
        return this.addOrUpdateDomainDetails(domainProps, request, "edit");
    }
    
    public JSONObject modifyDomainDetails(final JSONObject request) {
        return (JSONObject)this.individualDomainRequestHandler(request, 3);
    }
    
    private void deleteDomain(final Properties domainProps) {
        final int client_id = ((Hashtable<K, Integer>)domainProps).get("CLIENT_ID");
        if (client_id == 201) {
            throw new IdpsAPIException("COM0014");
        }
        try {
            final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
            dirProdImplRequest.dmDomainProps = domainProps;
            dirProdImplRequest.eventType = IdpEventConstants.APPROVE_DOMAIN_DELETION;
            DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
        }
        catch (final IdpsAPIException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in deleting domain", e);
            throw e;
        }
        catch (final Exception e2) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in deleting domain", e2);
            throw new IdpsAPIException("COM0004");
        }
    }
    
    public void deleteDomain(final JSONObject apirequest) {
        this.individualDomainRequestHandler(apirequest, 4);
    }
    
    private void syncParticularDomain(final Properties domainProps, final JSONObject apirequest) throws JSONException {
        final JSONObject msg_body = apirequest.getJSONObject("msg_body");
        final boolean doFullSync = msg_body.optBoolean("is_full_sync", true);
        DirectoryUtil.getInstance().syncDomain(domainProps, doFullSync);
    }
    
    public void syncParticularDomain(final JSONObject apirequest) {
        this.individualDomainRequestHandler(apirequest, 5);
    }
    
    private JSONObject getDomainSyncDetails(final Properties domainProps) {
        JSONObject syncDetails = null;
        final Long domain_id = ((Hashtable<K, Long>)domainProps).get("DOMAIN_ID");
        try {
            syncDetails = DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetails(domain_id);
        }
        catch (final Exception e) {
            throw new IdpsAPIException("COM0004");
        }
        if (syncDetails == null) {
            throw new IdpsAPIException("AD007");
        }
        return syncDetails;
    }
    
    public JSONObject getDomainSyncDetails(final JSONObject apirequest) {
        return (JSONObject)this.individualDomainRequestHandler(apirequest, 6);
    }
    
    static {
        DirectoryAPIFacade.directoryAPIFacade = null;
    }
}
