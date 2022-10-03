package com.me.mdm.server.enrollment.approval;

import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.HashSet;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Iterator;
import java.util.List;

public class EnrollmentApprovalHandler
{
    private static List<EnrollmentApprover> approverList;
    private static EnrollmentApprovalHandler enrollmentApprovalHandler;
    
    public void addEnrollmentApprover(final EnrollmentApprover approver) {
        EnrollmentApprovalHandler.approverList.add(approver);
    }
    
    private EnrollmentApprovalHandler() {
    }
    
    public static EnrollmentApprovalHandler getInstance() {
        if (EnrollmentApprovalHandler.enrollmentApprovalHandler == null) {
            EnrollmentApprovalHandler.enrollmentApprovalHandler = new EnrollmentApprovalHandler();
        }
        return EnrollmentApprovalHandler.enrollmentApprovalHandler;
    }
    
    public static void approveEnrollmentRequest(final EnrollmentRequest request) throws SyMException {
        final Iterator<EnrollmentApprover> iterator = EnrollmentApprovalHandler.approverList.iterator();
        while (iterator.hasNext()) {
            iterator.next().allowEnrollment(request);
        }
    }
    
    public JSONObject getCriteria(final int resourcePropertyType) {
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final long enrollmentSettingsID = EnrollmentSettingsHandler.getInstance().addBaseEnrollmentSettings(customerID);
            final DataObject dobj = this.getApprovalCriteriaDO(resourcePropertyType, enrollmentSettingsID);
            if (!dobj.isEmpty()) {
                final Row criteriaRow = dobj.getRow("ApprovalCriteria");
                final JSONObject json = new JSONObject();
                json.put("RESOURCE_PROPERTY_TYPE", criteriaRow.get("RESOURCE_PROPERTY_TYPE"));
                json.put("INCLUDE_RESOURCE", criteriaRow.get("INCLUDE_RESOURCE"));
                final JSONArray jsonArray = new JSONArray();
                final Iterator<Row> iterator = dobj.getRows("ApprovalCriteriaResource");
                while (iterator.hasNext()) {
                    final Row criteriaResource = iterator.next();
                    final JSONObject resourceJSON = new JSONObject();
                    resourceJSON.put("RESOURCE_NAME", criteriaResource.get("RESOURCE_NAME"));
                    final Row adResource = dobj.getRow("ApprovalCriteriaADResource", new Criteria(Column.getColumn("ApprovalCriteriaADResource", "RESOURCE_PROPERTY_ID"), criteriaResource.get("RESOURCE_PROPERTY_ID"), 0));
                    if (adResource != null) {
                        resourceJSON.put("DOMAIN_NETBIOS_NAME", dobj.getValue("DMDomain", "NAME", new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), adResource.get("DMDOMAIN_ID"), 0)));
                        resourceJSON.put("DIRECTORY_IDENTIFIER", adResource.get("DIRECTORY_IDENTIFIER"));
                        final Row opadResource = dobj.getRow("ApprovalCriteriaOPADResource", new Criteria(Column.getColumn("ApprovalCriteriaOPADResource", "RESOURCE_PROPERTY_ID"), criteriaResource.get("RESOURCE_PROPERTY_ID"), 0));
                        if (opadResource != null) {
                            resourceJSON.put("DN", opadResource.get("DN"));
                        }
                    }
                    jsonArray.put((Object)resourceJSON);
                }
                json.put("RESOURCE_LIST", (Object)jsonArray);
                return json;
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentApprovalHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private DataObject getApprovalCriteriaDO(final Integer resourcePropertyType, final long enrollmentSettingsID) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ApprovalCriteria"));
        query.addJoin(new Join("ApprovalCriteria", "ApprovalCriteriaResource", new String[] { "CRITERIA_ID" }, new String[] { "CRITERIA_ID" }, 2));
        query.addJoin(new Join("ApprovalCriteriaResource", "ApprovalCriteriaADResource", new String[] { "RESOURCE_PROPERTY_ID" }, new String[] { "RESOURCE_PROPERTY_ID" }, 1));
        query.addJoin(new Join("ApprovalCriteriaADResource", "ApprovalCriteriaOPADResource", new String[] { "RESOURCE_PROPERTY_ID" }, new String[] { "RESOURCE_PROPERTY_ID" }, 1));
        query.addJoin(new Join("ApprovalCriteriaADResource", "DMManagedDomain", new String[] { "DMDOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 1));
        query.addJoin(new Join("DMManagedDomain", "DMDomain", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 1));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        Criteria criteria = new Criteria(Column.getColumn("ApprovalCriteria", "ENROLLMENT_SETTINGS_ID"), (Object)enrollmentSettingsID, 0);
        if (resourcePropertyType != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("ApprovalCriteria", "RESOURCE_PROPERTY_TYPE"), (Object)resourcePropertyType, 0));
        }
        query.setCriteria(criteria);
        return MDMUtil.getPersistence().get(query);
    }
    
    public void addOrUpdateApprovalCriteria(final JSONObject criteriaDetails) {
        try {
            final int resourcePropertyType = criteriaDetails.getInt("RESOURCE_PROPERTY_TYPE");
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final long enrollmentSettingsID = EnrollmentSettingsHandler.getInstance().addBaseEnrollmentSettings(customerID);
            DataObject dobj = this.getApprovalCriteriaDO(resourcePropertyType, enrollmentSettingsID);
            dobj = (DataObject)(dobj.isEmpty() ? new WritableDataObject() : dobj);
            final long criteriaID = this.addOrUpdateApprovalCriteria(dobj, enrollmentSettingsID, criteriaDetails);
            this.addOrUpdateResourcesForCriteria(dobj, criteriaID, criteriaDetails.getJSONArray("RESOURCE_LIST"));
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentApprovalHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private long addOrUpdateApprovalCriteria(final DataObject dobj, final Long enrollSettingsID, final JSONObject jsonobject) throws Exception {
        final Iterator<Row> iterator = dobj.getRows("ApprovalCriteria");
        if (iterator.hasNext()) {
            final Row row = iterator.next();
            row.set("INCLUDE_RESOURCE", (Object)jsonobject.getBoolean("INCLUDE_RESOURCE"));
            dobj.updateRow(row);
        }
        else {
            final Row row = new Row("ApprovalCriteria");
            row.set("ENROLLMENT_SETTINGS_ID", (Object)enrollSettingsID);
            row.set("INCLUDE_RESOURCE", (Object)jsonobject.getBoolean("INCLUDE_RESOURCE"));
            row.set("RESOURCE_PROPERTY_TYPE", (Object)jsonobject.getInt("RESOURCE_PROPERTY_TYPE"));
            dobj.addRow(row);
        }
        MDMUtil.getPersistence().update(dobj);
        return (long)dobj.getValue("ApprovalCriteria", "CRITERIA_ID", (Criteria)null);
    }
    
    private void addOrUpdateResourcesForCriteria(final DataObject dobj, final long criteriaID, final JSONArray jsonArray) throws Exception {
        final HashSet<String> domainSet = new HashSet<String>();
        for (int jsonIndex = 0; jsonIndex < jsonArray.length(); ++jsonIndex) {
            domainSet.add(String.valueOf(jsonArray.getJSONObject(jsonIndex).get("DOMAIN_NETBIOS_NAME")));
        }
        final DataObject domainDO = MDMUtil.getPersistence().get("DMDomain", new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)domainSet.toArray(new String[domainSet.size()]), 8, (boolean)Boolean.FALSE));
        dobj.deleteRows("ApprovalCriteriaResource", new Criteria(Column.getColumn("ApprovalCriteriaResource", "CRITERIA_ID"), (Object)criteriaID, 0));
        MDMUtil.getPersistence().update(dobj);
        for (int jsonIndex2 = 0; jsonIndex2 < jsonArray.length(); ++jsonIndex2) {
            final Row row = new Row("ApprovalCriteriaResource");
            row.set("RESOURCE_NAME", (Object)String.valueOf(jsonArray.getJSONObject(jsonIndex2).get("RESOURCE_NAME")));
            row.set("CRITERIA_ID", (Object)criteriaID);
            dobj.addRow(row);
            final Row adRow = new Row("ApprovalCriteriaADResource");
            adRow.set("RESOURCE_PROPERTY_ID", row.get("RESOURCE_PROPERTY_ID"));
            adRow.set("DIRECTORY_IDENTIFIER", (Object)String.valueOf(jsonArray.getJSONObject(jsonIndex2).get("DIRECTORY_IDENTIFIER")));
            adRow.set("DMDOMAIN_ID", domainDO.getValue("DMDomain", "DOMAIN_ID", new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)String.valueOf(jsonArray.getJSONObject(jsonIndex2).get("DOMAIN_NETBIOS_NAME")), 0, false)));
            dobj.addRow(adRow);
            final int clientId = (int)domainDO.getValue("DMDomain", "CLIENT_ID", new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)String.valueOf(jsonArray.getJSONObject(jsonIndex2).get("DOMAIN_NETBIOS_NAME")), 0, false));
            if ((clientId == 2 || clientId == 4) && !MDMStringUtils.isEmpty(jsonArray.getJSONObject(jsonIndex2).optString("DN"))) {
                final Row opadRow = new Row("ApprovalCriteriaOPADResource");
                opadRow.set("RESOURCE_PROPERTY_ID", row.get("RESOURCE_PROPERTY_ID"));
                opadRow.set("DN", (Object)jsonArray.getJSONObject(jsonIndex2).optString("DN"));
                dobj.addRow(opadRow);
            }
        }
        MDMUtil.getPersistence().update(dobj);
    }
    
    public void clearApprovalCriteria() {
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final long enrollmentSettingsID = EnrollmentSettingsHandler.getInstance().addBaseEnrollmentSettings(customerID);
            final DataObject dobj = this.getApprovalCriteriaDO(null, enrollmentSettingsID);
            if (!dobj.isEmpty()) {
                dobj.deleteRows("ApprovalCriteria", (Criteria)null);
                MDMUtil.getPersistence().update(dobj);
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(EnrollmentApprovalHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    static {
        EnrollmentApprovalHandler.approverList = new ArrayList<EnrollmentApprover>();
        EnrollmentApprovalHandler.enrollmentApprovalHandler = null;
    }
}
