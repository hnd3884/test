package com.me.idps.core.util;

import java.util.Hashtable;
import java.text.MessageFormat;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import java.util.Iterator;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Range;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Properties;

public class ADSyncDataHandler
{
    private static ADSyncDataHandler adSyncDataHandler;
    
    public static ADSyncDataHandler getInstance() {
        if (ADSyncDataHandler.adSyncDataHandler == null) {
            ADSyncDataHandler.adSyncDataHandler = new ADSyncDataHandler();
        }
        return ADSyncDataHandler.adSyncDataHandler;
    }
    
    public Properties getDirUserProps(final Long customerID, final String domainName, final String userName) {
        return this.getDirUserProps(customerID, domainName, userName, null, null);
    }
    
    public Properties getDirUserProps(final Long customerID, final String domainName, final String userName, final Integer[] allowedStatus, final Integer[] notAllowedStatus) {
        try {
            Criteria resCri = new Criteria(Column.getColumn("Resource", "NAME"), (Object)userName, 0, false).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0, false)).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false));
            if (allowedStatus != null && allowedStatus.length > 0) {
                resCri = resCri.and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)allowedStatus, 8)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0));
            }
            if (notAllowedStatus != null && notAllowedStatus.length > 0) {
                resCri = resCri.and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)notAllowedStatus, 9)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0));
            }
            final Properties dirObjProps = DirectoryUtil.getInstance().getObjectAttributes(resCri);
            if (dirObjProps != null && !dirObjProps.isEmpty()) {
                final Properties props = new Properties();
                final String email = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(106L));
                final String mobile = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(114L));
                final String lastName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(108L));
                final String firstName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(109L));
                final String resName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(2L));
                final String middleName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(110L));
                final String upn = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(112L));
                final String displayName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(111L));
                final String samAccountName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(107L));
                if (!IdpsUtil.isStringEmpty(resName)) {
                    ((Hashtable<String, String>)props).put("NAME", resName);
                }
                if (IdpsUtil.getInstance().isValidEmail(email)) {
                    ((Hashtable<String, String>)props).put("EMAIL_ADDRESS", email);
                }
                ((Hashtable<String, String>)props).put("USER_PRINCIPAL_NAME", IdpsUtil.isStringEmpty(upn) ? "" : upn);
                ((Hashtable<String, String>)props).put("PHONE_NUMBER", IdpsUtil.isStringEmpty(mobile) ? "" : mobile);
                ((Hashtable<String, String>)props).put("LAST_NAME", IdpsUtil.isStringEmpty(lastName) ? "" : lastName);
                ((Hashtable<String, String>)props).put("FIRST_NAME", IdpsUtil.isStringEmpty(firstName) ? "" : firstName);
                ((Hashtable<String, String>)props).put("MIDDLE_NAME", IdpsUtil.isStringEmpty(middleName) ? "" : middleName);
                ((Hashtable<String, String>)props).put("DISPLAY_NAME", IdpsUtil.isStringEmpty(displayName) ? "" : displayName);
                ((Hashtable<String, String>)props).put("SAM_ACCOUNT_NAME", IdpsUtil.isStringEmpty(samAccountName) ? "" : samAccountName);
                return props;
            }
        }
        catch (final DataAccessException ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, (Throwable)ex);
        }
        return new Properties();
    }
    
    private String extractValueForDO(final DataObject dObj, final Long resourceID, final Long attrID) throws DataAccessException {
        final Row dirObjAttrValRow = dObj.getRow("DirObjRegStrVal", new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)attrID, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)resourceID, 0)));
        if (dirObjAttrValRow != null) {
            return String.valueOf(dirObjAttrValRow.get("VALUE"));
        }
        return "";
    }
    
    public List<Properties> getDirUserListForSuggest(final Long customerID, final String userSearchHint, final int range) {
        return this.getDirObjListForSuggest(customerID, 2, userSearchHint, new Range(0, range));
    }
    
    public List<Properties> getDirObjListForSuggest(final Long customerID, final int resType, final String searchHint, final Range range) {
        return this.getDirObjListForSuggest(customerID, resType, searchHint, null, range);
    }
    
    public List<Properties> getDirObjListForSuggest(final Long customerID, final int resType, final String searchHint, final Long domainID, final Range range) {
        final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria resTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)resType, 0);
        Criteria cri = custCri.and(resTypeCri).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)new Integer[] { 1, 3 }, 8));
        if (!IdpsUtil.isStringEmpty(searchHint)) {
            final Column dirObjValCol = Column.getColumn("DirObjRegStrVal", "VALUE");
            Criteria userSearchCri;
            if (searchHint.contains("%")) {
                userSearchCri = new Criteria(dirObjValCol, (Object)searchHint.replace("%", "*"), 2, false);
            }
            else {
                userSearchCri = new Criteria(dirObjValCol, (Object)searchHint, 10, false);
            }
            cri = cri.and(userSearchCri);
        }
        if (domainID != null) {
            cri = cri.and(new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)domainID, 0));
        }
        final SelectQuery selectQuery = this.getDirObjListForSuggest(cri, range);
        final List<Long> resIDs = this.getDirObjListForSuggest(selectQuery);
        return this.getDirObjListForSuggest(resIDs, resType);
    }
    
    public SelectQuery getDirObjListForSuggest(final Criteria cri, final Range range) {
        final SelectQuery query = DirectoryQueryutil.getInstance().getDirObjAttrQuery(cri);
        query.addJoin(new Join("DirObjRegStrVal", "DirResRel", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
        query.addJoin(new Join("DirResRel", "DirObjRegIntVal", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
        query.addJoin(new Join("DirResRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addSelectColumn(Column.getColumn("DirResRel", "RESOURCE_ID"));
        query.setGroupByClause(new GroupByClause(query.getSelectColumns()));
        query.addSortColumn(new SortColumn(Column.getColumn("DirResRel", "RESOURCE_ID"), true));
        query.setRange(range);
        return query;
    }
    
    public List<Long> getDirObjListForSuggest(final SelectQuery query) {
        final List<Long> resIDs = new ArrayList<Long>();
        for (final JSONObject jsonObject : IdpsUtil.executeSelectQuery(query)) {
            try {
                resIDs.add(Long.valueOf(String.valueOf(jsonObject.get((Object)"RESOURCE_ID"))));
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.WARNING, null, ex);
            }
        }
        return resIDs;
    }
    
    public List<Properties> getDirObjListForSuggest(final List<Long> resID, final int resType) {
        final List<Properties> dirObjs = new ArrayList<Properties>();
        try {
            final SelectQuery query = DirectoryQueryutil.getInstance().getDirObjAttrQuery(new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)resID.toArray(new Long[resID.size()]), 8));
            query.addJoin(new Join("DirObjRegStrVal", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("DirResRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID", "user_search_RESOURCE.RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME", "user_search_RESOURCE.DOMAIN_NETBIOS_NAME"));
            query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "VALUE", "user_search_DIROBJREGSTRVAL.VALUE"));
            query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "OBJ_ID", "user_search_DIROBJREGSTRVAL.OBJ_ID"));
            query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ATTR_ID", "user_search_DIROBJREGSTRVAL.ATTR_ID"));
            query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ADDED_AT", "user_search_DIROBJREGSTRVAL.ADDED_AT"));
            query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID", "user_search_DIROBJREGSTRVAL.RESOURCE_ID"));
            final DataObject dObj = IdpsUtil.getPersistenceLite().get(query);
            if (dObj != null && !dObj.isEmpty()) {
                final Iterator itr = dObj.getRows("Resource");
                while (itr != null && itr.hasNext()) {
                    final Row resRow = itr.next();
                    final Long resourceID = (Long)resRow.get("RESOURCE_ID");
                    String displayDomainName;
                    final String userDomainName = displayDomainName = (String)resRow.get("DOMAIN_NETBIOS_NAME");
                    if (userDomainName.equalsIgnoreCase("Zoho Directory")) {
                        displayDomainName = this.extractValueForDO(dObj, resourceID, 116L);
                        if (IdpsUtil.isStringEmpty(displayDomainName)) {
                            displayDomainName = userDomainName;
                        }
                    }
                    final String resourceName = this.extractValueForDO(dObj, resourceID, 2L);
                    JSONObject json = new JSONObject();
                    json.put((Object)"NAME", (Object)resourceName);
                    json.put((Object)"DOMAIN_NETBIOS_NAME", (Object)userDomainName);
                    json.put((Object)"DISPLAY_DOMAIN_NETBIOS_NAME", (Object)displayDomainName);
                    if (resType == 2) {
                        final String email = this.extractValueForDO(dObj, resourceID, 106L);
                        final String phoneNumber = this.extractValueForDO(dObj, resourceID, 114L);
                        final String displayName = this.extractValueForDO(dObj, resourceID, 111L);
                        json.put((Object)"DISPLAY_NAME", (Object)displayName);
                        json.put((Object)"PHONE_NUMBER", (Object)(IdpsUtil.isStringEmpty(phoneNumber) ? "" : phoneNumber));
                        json.put((Object)"EMAIL_ADDRESS", (Object)(IdpsUtil.getInstance().isValidEmail(email) ? email : ""));
                        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                        dirProdImplRequest.args = new Object[] { resourceID, json };
                        dirProdImplRequest.eventType = IdpEventConstants.PROCESS_USER_IDF_DETAILS;
                        json = (JSONObject)DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
                    }
                    final Properties dataProperty = new Properties();
                    ((Hashtable<String, Long>)dataProperty).put("dataId", resourceID);
                    ((Hashtable<String, String>)dataProperty).put("dataValue", json.toString());
                    dirObjs.add(dataProperty);
                }
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
        return dirObjs;
    }
    
    public Properties getDirUserProps(final Long userId, final Long customerId) {
        try {
            final Properties dirObjProps = DirectoryUtil.getInstance().getObjectAttributes(new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)userId, 0));
            if (dirObjProps != null && !dirObjProps.isEmpty()) {
                final Properties props = new Properties();
                final String email = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(106L));
                final String mobile = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(114L));
                final String lastName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(108L));
                final String firstName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(109L));
                final String resName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(2L));
                final String middleName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(110L));
                final String upn = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(112L));
                final String displayName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(111L));
                final String samAccountName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(107L));
                String domainName = dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(116L));
                if (IdpsUtil.isStringEmpty(domainName)) {
                    domainName = (String)DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)userId, "DOMAIN_NETBIOS_NAME");
                }
                ((Hashtable<String, Long>)props).put("CUSTOMER_ID", customerId);
                if (!IdpsUtil.isStringEmpty(resName)) {
                    ((Hashtable<String, String>)props).put("NAME", resName);
                }
                ((Hashtable<String, String>)props).put("USER_PRINCIPAL_NAME", IdpsUtil.isStringEmpty(upn) ? "" : upn);
                ((Hashtable<String, String>)props).put("PHONE_NUMBER", IdpsUtil.isStringEmpty(mobile) ? "" : mobile);
                ((Hashtable<String, String>)props).put("LAST_NAME", IdpsUtil.isStringEmpty(lastName) ? "" : lastName);
                ((Hashtable<String, String>)props).put("FIRST_NAME", IdpsUtil.isStringEmpty(firstName) ? "" : firstName);
                if (IdpsUtil.getInstance().isValidEmail(email)) {
                    ((Hashtable<String, String>)props).put("EMAIL_ADDRESS", email);
                }
                ((Hashtable<String, String>)props).put("MIDDLE_NAME", IdpsUtil.isStringEmpty(middleName) ? "" : middleName);
                ((Hashtable<String, String>)props).put("DISPLAY_NAME", IdpsUtil.isStringEmpty(displayName) ? "" : displayName);
                ((Hashtable<String, String>)props).put("SAM_ACCOUNT_NAME", IdpsUtil.isStringEmpty(samAccountName) ? "" : samAccountName);
                ((Hashtable<String, String>)props).put("DOMAIN_NETBIOS_NAME", IdpsUtil.isStringEmpty(domainName) ? "" : domainName);
                return props;
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, MessageFormat.format("Exception while fetching directory user properties for user id : {0}", userId), e);
        }
        return null;
    }
    
    static {
        ADSyncDataHandler.adSyncDataHandler = null;
    }
}
