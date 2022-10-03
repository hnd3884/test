package com.me.mdm.api.reports.integ;

import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.webclient.reports.ReportBIUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONArray;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ModulesAPIRequestHandler extends ApiRequestHandler
{
    private static Logger out;
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONArray modules = this.getAvailableReportsDetails(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            final JSONObject apiResponse = new JSONObject();
            final JSONObject msgResponse = new JSONObject();
            msgResponse.put("modules", (Object)modules);
            apiResponse.put("message_type", (Object)"modules");
            apiResponse.put("message_response", (Object)msgResponse);
            apiResponse.put("status", (Object)"success");
            apiResponse.put("message_version", (Object)"1.0");
            response.put("status", 200);
            response.put("RESPONSE", (Object)apiResponse);
            return response;
        }
        catch (final JSONException e) {
            ModulesAPIRequestHandler.out.log(Level.SEVERE, "Exception in doGet", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray getAvailableReportsDetails(final JSONObject apiRequest) {
        final Long userID = APIUtil.getUserID(apiRequest);
        final String apiVersion = "1.2";
        final JSONArray moduleDetails = ReportBIUtil.getModuleDetailsForUser(userID);
        return getAvailableModulesAndTables(moduleDetails, apiVersion);
    }
    
    private static JSONArray getAvailableModulesAndTables(final JSONArray moduleDetails, final String apiVersion) {
        try {
            for (int i = 0; i < moduleDetails.length(); ++i) {
                final JSONObject currentModule = (JSONObject)moduleDetails.get(i);
                final Long moduleID = currentModule.getLong("moduleID");
                final DataObject tableDetailsDO = ReportBIUtil.getTableDetailsForModule(moduleID, apiVersion);
                final JSONArray availableTables = convertReportTableDetailsDOToJSON(tableDetailsDO);
                currentModule.put("tableDetails", (Object)availableTables);
                moduleDetails.put(i, (Object)currentModule);
            }
        }
        catch (final Exception e) {
            ModulesAPIRequestHandler.out.log(Level.WARNING, "Exception in getting Available Report Modules and Tables for the user", e);
        }
        return moduleDetails;
    }
    
    private static JSONArray convertReportTableDetailsDOToJSON(final DataObject reportQueryDO) {
        final JSONArray tableDetails = new JSONArray();
        try {
            final Iterator iterator = reportQueryDO.getRows("ReportBIQuery");
            while (iterator.hasNext()) {
                final JSONObject reportQueryDetails = new JSONObject();
                final Row reportQueryRow = iterator.next();
                final Long tableID = (Long)reportQueryRow.get("TABLE_ID");
                final String tableName = (String)reportQueryRow.get("TABLE_NAME");
                JSONArray tableAssociation = null;
                reportQueryDetails.put("tableName", (Object)tableName);
                reportQueryDetails.put("tableID", (Object)tableID);
                final Criteria tableAssociationCriteria = new Criteria(new Column("ReportBITableAssociation", "CHILD_TABLE_ID"), (Object)tableID, 0);
                final DataObject tableAssociationDO = ReportBIUtil.getReportBITableDetails("ReportBITableAssociation", tableAssociationCriteria);
                if (tableAssociationDO != null) {
                    tableAssociation = getTableAssociationJSONFromDO(tableAssociationDO);
                }
                reportQueryDetails.put("lookUpDetails", (Object)tableAssociation);
                tableDetails.put((Object)reportQueryDetails);
            }
            return tableDetails;
        }
        catch (final Exception e) {
            ModulesAPIRequestHandler.out.log(Level.WARNING, " Exception while converting DataObject to HashMap", e);
            return null;
        }
    }
    
    private static JSONArray getTableAssociationJSONFromDO(final DataObject tableAssociationDO) {
        final JSONArray tableAssociation = new JSONArray();
        try {
            final Iterator tableAssociationIterator = tableAssociationDO.getRows("ReportBITableAssociation");
            while (tableAssociationIterator.hasNext()) {
                final JSONObject lookUpDetails = new JSONObject();
                final Row tableAssociationRow = tableAssociationIterator.next();
                final Long lookUpTableID = (Long)tableAssociationRow.get("CHILD_TABLE_ID");
                final Long lookUpColumnID = (Long)tableAssociationRow.get("CHILD_COLUMN_ID");
                final Long lookedUpTableID = (Long)tableAssociationRow.get("PARENT_TABLE_ID");
                final Long lookedUpColumnID = (Long)tableAssociationRow.get("PARENT_COLUMN_ID");
                String lookUpColumnName = "";
                String lookedUpColumnName = "";
                final Criteria columnDetailsCrit = new Criteria(new Column("CRColumns", "COLUMN_ID"), (Object)new Long[] { lookUpColumnID, lookedUpColumnID }, 8);
                final DataObject columnDetailsDO = ReportBIUtil.getColumnDetailsForCriteria(columnDetailsCrit);
                if (columnDetailsDO != null) {
                    final Iterator columnDetailsIterator = columnDetailsDO.getRows("CRColumns");
                    while (columnDetailsIterator.hasNext()) {
                        final Row columnDetailsRow = columnDetailsIterator.next();
                        final Long columnID = (Long)columnDetailsRow.get("COLUMN_ID");
                        if (columnID.equals(lookUpColumnID)) {
                            lookUpColumnName = (String)columnDetailsRow.get("COLUMN_NAME_ALIAS");
                        }
                        else {
                            if (!columnID.equals(lookedUpColumnID)) {
                                continue;
                            }
                            lookedUpColumnName = (String)columnDetailsRow.get("COLUMN_NAME_ALIAS");
                        }
                    }
                }
                lookUpDetails.put("lookUpColumnName", (Object)lookUpColumnName);
                lookUpDetails.put("lookUpTableID", (Object)lookUpTableID);
                lookUpDetails.put("lookedUpTableID", (Object)lookedUpTableID);
                lookUpDetails.put("lookedUpColumnName", (Object)lookedUpColumnName);
                tableAssociation.put((Object)lookUpDetails);
            }
        }
        catch (final Exception e) {
            ModulesAPIRequestHandler.out.log(Level.WARNING, " Exception while constructing JSONArray for tableAssociation", e);
        }
        return tableAssociation;
    }
    
    static {
        ModulesAPIRequestHandler.out = Logger.getLogger(ModulesAPIRequestHandler.class.getName());
    }
}
