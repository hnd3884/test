package com.me.devicemanagement.framework.server.admin;

import java.util.Hashtable;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.UUID;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class CredentialManager
{
    String className;
    public Logger out;
    public static final int MD_ROOT_CRED = 1;
    public static final int DOMAIN_CRED = 301;
    public static final int WORKGROUP_CRED = 302;
    public static final int CRED_ENC_POCO = 8;
    
    public CredentialManager() {
        this.className = CredentialManager.class.getName();
        this.out = Logger.getLogger(this.className);
    }
    
    public Long addOrUpdateCredential(final Properties credProps) {
        final HashMap credDetailMap = this.addOrUpdateCredentialDetails(credProps);
        return (credDetailMap != null) ? credDetailMap.get("CREDENTIAL_ID") : null;
    }
    
    public HashMap addOrUpdateCredentialDetails(final Properties credProps) {
        final HashMap credDetailMap = new HashMap();
        final String credentialName = credProps.getProperty("CredentialName");
        final String userName = credProps.getProperty("UserName");
        final String passWord = credProps.getProperty("PassWord");
        final String credentialType = credProps.getProperty("CredentialType");
        final Long customerId = ((Hashtable<K, Long>)credProps).get("CustomerID");
        Long credId = null;
        String credUUID = null;
        if (credentialName == null || customerId == null) {
            this.out.severe("Credential Name || Customer ID is missing.");
            return credDetailMap;
        }
        try {
            Criteria credNameCri = new Criteria(Column.getColumn("Credential", "CREDENTIAL_NAME"), (Object)credentialName, 0, false);
            credNameCri = credNameCri.and(new Criteria(Column.getColumn("Credential", "CUSTOMER_ID"), (Object)customerId, 0));
            DataObject credDO = SyMUtil.getPersistence().get("Credential", credNameCri);
            if (credDO.isEmpty()) {
                if (userName == null || passWord == null || credentialType == null) {
                    this.out.severe("Mandatory fields were missing to add a credential");
                    return credDetailMap;
                }
                credDO = SyMUtil.getPersistence().constructDataObject();
                final Row credRow = new Row("Credential");
                credRow.set("CREDENTIAL_NAME", (Object)credentialName.trim());
                credRow.set("CREDENTIAL_TYPE", (Object)Integer.parseInt(credentialType));
                credRow.set("CRD_USERNAME", (Object)userName);
                credRow.set("CRD_PASSWORD", (Object)ApiFactoryProvider.getCryptoAPI().encrypt(passWord, 8));
                credRow.set("CRD_ENC_TYPE", (Object)8);
                credRow.set("REMARKS", (Object)credProps.getProperty("Description", "--"));
                credRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                credRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                credRow.set("CUSTOMER_ID", (Object)customerId);
                credRow.set("CREDENTIAL_UUID", (Object)UUID.randomUUID().toString().concat("-").concat(String.valueOf(System.currentTimeMillis())));
                credDO.addRow(credRow);
                SyMUtil.getPersistence().add(credDO);
                credId = (Long)credRow.get("CREDENTIAL_ID");
                CredentialListenerHandler.getInstance().invokeCredentialAddedListeners(credId);
                credUUID = (String)credRow.get("CREDENTIAL_UUID");
            }
            else {
                final Row credRow = credDO.getRow("Credential");
                if (credentialType != null) {
                    credRow.set("CREDENTIAL_TYPE", (Object)Integer.parseInt(credentialType));
                }
                credRow.set("CRD_USERNAME", (userName == null) ? credRow.get("CRD_USERNAME") : userName);
                credRow.set("CRD_PASSWORD", (Object)((passWord == null) ? ApiFactoryProvider.getCryptoAPI().encrypt(ApiFactoryProvider.getCryptoAPI().decrypt(String.valueOf(credRow.get("CRD_PASSWORD")), (Integer)credRow.get("CRD_ENC_TYPE")), 8) : ApiFactoryProvider.getCryptoAPI().encrypt(passWord, 8)));
                credRow.set("CRD_ENC_TYPE", (Object)8);
                final String description = credProps.getProperty("Description");
                credRow.set("REMARKS", (description == null) ? credRow.get("REMARKS") : description);
                credRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                credRow.set("CUSTOMER_ID", (Object)customerId);
                credDO.updateRow(credRow);
                SyMUtil.getPersistence().update(credDO);
                credId = (Long)credRow.get("CREDENTIAL_ID");
                credUUID = (String)credRow.get("CREDENTIAL_UUID");
                CredentialListenerHandler.getInstance().invokeCredentialModifiedListeners(credId);
            }
        }
        catch (final Exception ex) {
            this.out.log(Level.SEVERE, "Exception in add / updating managed credentials table..", ex);
        }
        credDetailMap.put("CREDENTIAL_ID", credId);
        credDetailMap.put("CREDENTIAL_UUID", credUUID);
        return credDetailMap;
    }
    
    public void deleteCredential(final String credentialName) {
        try {
            final Criteria credNameCri = new Criteria(Column.getColumn("Credential", "CREDENTIAL_NAME"), (Object)credentialName, 0);
            SyMUtil.getPersistence().delete(credNameCri);
        }
        catch (final DataAccessException ex) {
            this.out.log(Level.SEVERE, "DataAccessException in get deleteCredential for credentialName " + credentialName + " throws exception", (Throwable)ex);
        }
    }
    
    public void deleteCredential(final Long credentialId) throws DataAccessException {
        try {
            final Criteria credIDCri = new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)credentialId, 0);
            SyMUtil.getPersistence().delete(credIDCri);
        }
        catch (final DataAccessException ex) {
            this.out.log(Level.SEVERE, "DataAccessException in get deleteCredential for credentialName " + credentialId + " throws exception", (Throwable)ex);
            throw ex;
        }
    }
    
    public void deleteCredential(final String credentialName, final Long credentialId) throws DataAccessException {
        try {
            final Criteria credNameCri = new Criteria(Column.getColumn("Credential", "CREDENTIAL_NAME"), (Object)credentialName, 0);
            final Criteria credIDCri = new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)credentialId, 0);
            SyMUtil.getPersistence().delete(credNameCri.and(credIDCri));
        }
        catch (final DataAccessException ex) {
            this.out.log(Level.SEVERE, "DataAccessException in get deleteCredential for credentialName " + credentialId + " throws exception", (Throwable)ex);
            throw ex;
        }
    }
    
    public void hideCredential(final String credentialName) {
        try {
            final Criteria credNameCri = new Criteria(Column.getColumn("Credential", "CREDENTIAL_NAME"), (Object)credentialName, 0);
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Credential");
            updateQuery.setCriteria(credNameCri);
            updateQuery.setUpdateColumn("CRD_STATUS", (Object)0);
            updateQuery.setUpdateColumn("CRD_PASSWORD", (Object)ApiFactoryProvider.getCryptoAPI().encrypt("dummy", 8));
            SyMUtil.getPersistence().update(updateQuery);
        }
        catch (final DataAccessException ex) {
            this.out.log(Level.SEVERE, "DataAccessException in get hideCredential for credentialName " + credentialName + " throws exception", (Throwable)ex);
        }
    }
    
    public DataObject getCredentialDO(final Criteria crit) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Credential"));
        query.addSelectColumn(new Column((String)null, "*"));
        if (crit != null) {
            query.setCriteria(crit);
        }
        return SyMUtil.getPersistence().get(query);
    }
    
    public DataObject getCredentialDOWithDomainName(final Criteria criteria) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Credential"));
        final Join managedDomainJoin = new Join("Credential", "ManagedDomainCredentialRel", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 2);
        final Join resourceJoin = new Join("ManagedDomainCredentialRel", "Resource", new String[] { "DOMAINRESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        query.addJoin(managedDomainJoin);
        query.addJoin(resourceJoin);
        query.addSelectColumn(new Column("Credential", "CREDENTIAL_ID"));
        query.addSelectColumn(new Column("Credential", "CREDENTIAL_UUID"));
        query.addSelectColumn(new Column("Credential", "CREDENTIAL_NAME"));
        query.addSelectColumn(new Column("Credential", "CRD_USERNAME"));
        query.addSelectColumn(new Column("Credential", "CRD_PASSWORD"));
        query.addSelectColumn(new Column("Credential", "CREDENTIAL_TYPE"));
        query.addSelectColumn(new Column("Credential", "REMARKS"));
        query.addSelectColumn(new Column("Credential", "LAST_MODIFIED_TIME"));
        query.addSelectColumn(new Column("Credential", "ADDED_TIME"));
        query.addSelectColumn(new Column("Credential", "CRD_ENC_TYPE"));
        query.addSelectColumn(new Column("ManagedDomainCredentialRel", "DOMAINRESOURCE_ID"));
        query.addSelectColumn(new Column("ManagedDomainCredentialRel", "CREDENTIAL_ID"));
        query.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
        query.addSelectColumn(new Column("Resource", "DOMAIN_NETBIOS_NAME"));
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return SyMUtil.getPersistence().get(query);
    }
    
    public JSONObject getCredetialDetails(final Criteria criteria) throws DataAccessException {
        final DataObject credentialData = this.getCredentialDOWithDomainName(criteria);
        final JSONObject credentialObj = new JSONObject();
        final JSONArray credentialArr = new JSONArray();
        if (credentialData != null && !credentialData.isEmpty()) {
            try {
                final Iterator iterator = credentialData.getRows("Credential");
                while (iterator.hasNext()) {
                    final JSONObject credentialDetails = new JSONObject();
                    final Row credentialRow = iterator.next();
                    final Long credentialId = (Long)credentialRow.get("CREDENTIAL_ID");
                    credentialDetails.put("CREDENTIAL_TYPE".toLowerCase(), credentialRow.get("CREDENTIAL_TYPE"));
                    credentialDetails.put("ADDED_TIME".toLowerCase(), credentialRow.get("ADDED_TIME"));
                    credentialDetails.put("LAST_MODIFIED_TIME".toLowerCase(), credentialRow.get("LAST_MODIFIED_TIME"));
                    credentialDetails.put("CREDENTIAL_NAME".toLowerCase(), credentialRow.get("CREDENTIAL_NAME"));
                    credentialDetails.put("CRD_USERNAME".toLowerCase(), credentialRow.get("CRD_USERNAME"));
                    credentialDetails.put("CRD_PASSWORD".toLowerCase(), credentialRow.get("CRD_PASSWORD"));
                    credentialDetails.put("CRD_ENC_TYPE".toLowerCase(), credentialRow.get("CRD_ENC_TYPE"));
                    credentialDetails.put("CREDENTIAL_ID".toLowerCase(), (Object)credentialId);
                    credentialDetails.put("CREDENTIAL_UUID".toLowerCase(), credentialRow.get("CREDENTIAL_UUID"));
                    final Criteria credentialIdCrit = new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)credentialId, 0);
                    final Row domainRelRow = credentialData.getRow("ManagedDomainCredentialRel", credentialIdCrit);
                    final Long domainResID = (Long)domainRelRow.get("DOMAINRESOURCE_ID");
                    final Criteria domainResourceCrit = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)domainResID, 0);
                    final Row domainResourceRow = credentialData.getRow("Resource", domainResourceCrit);
                    credentialDetails.put("DOMAIN_NETBIOS_NAME".toLowerCase(), domainResourceRow.get("DOMAIN_NETBIOS_NAME"));
                    credentialArr.put((Object)credentialDetails);
                }
                credentialObj.put("Credential".toLowerCase(), (Object)credentialArr);
            }
            catch (final JSONException jsonEx) {
                this.out.log(Level.SEVERE, "Exception in construction JSON Details as JSON", (Throwable)jsonEx);
            }
        }
        return credentialObj;
    }
    
    public Long getCredentialID(final String credName) {
        long credId = 0L;
        try {
            final DataObject credDO = this.getCredentialDO(new Criteria(Column.getColumn("Credential", "CREDENTIAL_NAME"), (Object)credName, 0));
            if (!credDO.isEmpty()) {
                credId = (long)credDO.getFirstValue("Credential", "CREDENTIAL_ID");
            }
            else {
                this.out.log(Level.SEVERE, "Credential ID not obtained for Name " + credName);
            }
        }
        catch (final DataAccessException ex) {
            this.out.log(Level.SEVERE, "DataAccessException in getCredentialID for credentialName" + credName + " throws exception", (Throwable)ex);
        }
        return credId;
    }
    
    public void addOrUpdateUserCredentialRel(final Long credentialID, final Long userID) throws Exception {
        String credentialUUID = "--";
        final DataObject credDO = SyMUtil.getPersistence().get("Credential", new Criteria(Column.getColumn("Credential", "CREDENTIAL_ID"), (Object)credentialID, 0));
        if (credDO != null && !credDO.isEmpty()) {
            credentialUUID = (String)credDO.getFirstValue("Credential", "CREDENTIAL_UUID");
        }
        this.addOrUpdateUserCredentialRel(credentialID, credentialUUID, userID);
    }
    
    public void addOrUpdateUserCredentialRel(final Long credentialID, final String credentialUUID, final Long userID) throws Exception {
        final Row userCredRow = new Row("UserCredentialMapping");
        userCredRow.set("CREDENTIAL_ID", (Object)credentialID);
        userCredRow.set("CREDENTIAL_UUID", (Object)credentialUUID);
        final DataObject userCredDO = SyMUtil.getPersistence().get("UserCredentialMapping", userCredRow);
        if (userCredDO != null && userCredDO.isEmpty()) {
            userCredRow.set("USER_ID", (Object)userID);
            userCredDO.addRow(userCredRow);
            SyMUtil.getPersistence().add(userCredDO);
        }
    }
}
