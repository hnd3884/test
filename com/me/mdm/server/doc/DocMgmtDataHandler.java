package com.me.mdm.server.doc;

import java.util.Hashtable;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Set;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import java.util.HashSet;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.doc.policy.DocPolicyHandler;
import com.me.idps.core.util.DirectoryQueryutil;
import java.util.Properties;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.UpdateQuery;
import java.sql.Connection;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.db.api.RelationalAPI;
import java.text.MessageFormat;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import javax.transaction.TransactionManager;
import javax.transaction.SystemException;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.File;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONException;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Join;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Iterator;
import org.json.simple.JSONObject;
import com.adventnet.persistence.Row;
import org.json.simple.JSONArray;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;

public class DocMgmtDataHandler
{
    private static DocMgmtDataHandler docMgmtDataHandler;
    private static final Integer DOC_TABLE_LOCK;
    
    public static DocMgmtDataHandler getInstance() {
        if (DocMgmtDataHandler.docMgmtDataHandler == null) {
            DocMgmtDataHandler.docMgmtDataHandler = new DocMgmtDataHandler();
        }
        return DocMgmtDataHandler.docMgmtDataHandler;
    }
    
    private String getI18NKeyForCode(final Integer statusCode) {
        switch (statusCode) {
            case 1: {
                return "mdm.content.success";
            }
            case 90007: {
                return "mdm.content.file.added";
            }
            case 90001: {
                return "mdm.content.gen.error";
            }
            case 90002: {
                return "mdm.content.security.error";
            }
            case 90003: {
                return "mdm.content.invalid.file";
            }
            case 90006: {
                return "mdm.content.filename.warning";
            }
            case 90004: {
                return "mdm.content.extention.error";
            }
            case 90008: {
                return "mdm.cyclic.group.distribute";
            }
            case 90009: {
                return "mdm.content.upload.error";
            }
            default: {
                return "";
            }
        }
    }
    
    public String getMsgForCode(final Integer statusCode, final Object[] status_msg_args) {
        final String i18Nkey = this.getI18NKeyForCode(statusCode);
        try {
            if (status_msg_args != null) {
                return I18N.getMsg(i18Nkey, status_msg_args);
            }
            return I18N.getMsg(i18Nkey, new Object[0]);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public int getDocumentCount(final Long customerID) throws Exception {
        return DBUtil.getRecordCount("DocumentDetails", "DOC_ID", new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1)));
    }
    
    public JSONArray getAvailableTags(final Long customerID) throws Exception {
        final JSONArray tagNameList = new JSONArray();
        final Iterator<Row> iterator = DBUtil.getRowsFromDB("DocumentTags", "CUSTOMER_ID", (Object)customerID);
        while (iterator != null && iterator.hasNext()) {
            final Row docTagRow = iterator.next();
            final JSONObject docTag = new JSONObject();
            docTag.put((Object)"TAG_ID", (Object)docTagRow.get("TAG_ID"));
            docTag.put((Object)"TAG_NAME", (Object)docTagRow.get("TAG_NAME"));
            tagNameList.add((Object)docTag);
        }
        return tagNameList;
    }
    
    public org.json.JSONArray getDocsTag(final Long docID) {
        final org.json.JSONArray docTags = new org.json.JSONArray();
        try {
            final DataObject resultDO = SyMUtil.getPersistenceLite().get(SyMUtil.formSelectQuery("DocumentDetails", new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)docID, 0).and(new Criteria(Column.getColumn("DocumentDetails", "UPDATED_TIME"), (Object)Column.getColumn("DocumentTagRel", "MODIFIED_TIME"), 6)), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DocumentTags", "TAG_ID"), Column.getColumn("DocumentTags", "TAG_NAME"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2), new Join("DocumentTagRel", "DocumentTags", new String[] { "TAG_ID" }, new String[] { "TAG_ID" }, 2))), (Criteria)null));
            final Iterator docTagIt = resultDO.getRows("DocumentTags");
            if (docTagIt != null) {
                while (docTagIt.hasNext()) {
                    final Row docTagRow = docTagIt.next();
                    docTags.put(docTagRow.get("TAG_NAME"));
                }
            }
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.INFO, null, ex);
        }
        return docTags;
    }
    
    protected JSONObject getDocDetails(final Long[] customerID, final Long docID, final boolean getLocationPath) throws Exception {
        JSONObject docDetails = null;
        CustomerInfoThreadLocal.setSkipCustomerFilter("true");
        final SelectQuery docDetailsQuery = SyMUtil.formSelectQuery("DocumentDetails", new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)docID, 0).and(new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerID, 8)), new ArrayList((Collection<? extends E>)Arrays.asList(new Column((String)null, "*"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DocumentDetails", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, 2), new Join("DocumentDetails", "ServerDocDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1), new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1), new Join("DocumentDetails", "DocumentToDeviceGroup", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1), new Join("DocumentDetails", "DocumentManagedDeviceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1), new Join("DocumentDetails", "DocumentToMDMResource", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1), new Join("DocumentToDeviceGroup", "Resource", new String[] { "CUSTOMGROUP_ID" }, new String[] { "RESOURCE_ID" }, 1), new Join("DocumentTagRel", "DocumentTags", new String[] { "TAG_ID" }, new String[] { "TAG_ID" }, 1), new Join("DocumentDetails", "DocumentSummary", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1), new Join("DocumentDetails", "DocumentPolicyResourceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1), new Join("DocumentPolicyResourceRel", "DeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 1), new Join("DeploymentPolicy", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 1))), (Criteria)null);
        final DataObject resultDO = SyMUtil.getPersistenceLite().get(docDetailsQuery);
        CustomerInfoThreadLocal.setSkipCustomerFilter("false");
        if (!resultDO.isEmpty()) {
            final Row docRow = resultDO.getFirstRow("DocumentDetails");
            final Long aaaUserID = (Long)docRow.get("LAST_MODIFIED_BY");
            final Row aaaUserRow = resultDO.getRow("AaaUser", new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)aaaUserID, 0));
            final Row docSummmaryRow = resultDO.getRow("DocumentSummary", new Criteria(Column.getColumn("DocumentSummary", "DOC_ID"), (Object)docID, 0));
            docDetails = new JSONObject();
            docDetails.put((Object)"DOC_ID", (Object)docID);
            docDetails.put((Object)"SIZE", (Object)docRow.get("SIZE"));
            docDetails.put((Object)"DOC_NAME", (Object)docRow.get("DOC_NAME"));
            docDetails.put((Object)"DOC_TYPE", (Object)docRow.get("DOC_TYPE"));
            docDetails.put((Object)"MIME_TYPE", (Object)docRow.get("MIME_TYPE"));
            docDetails.put((Object)"DESCRIPTION", (Object)docRow.get("DESCRIPTION"));
            docDetails.put((Object)"REPOSITORY_TYPE", (Object)docRow.get("REPOSITORY_TYPE"));
            docDetails.put((Object)"ADDED_TIME", (Object)docRow.get("ADDED_TIME"));
            docDetails.put((Object)"UPDATED_TIME", (Object)docRow.get("UPDATED_TIME"));
            docDetails.put((Object)"LAST_MODIFIED_BY", (Object)aaaUserRow.get("FIRST_NAME"));
            int userCount = 0;
            int grpCount = 0;
            int deviceCount = 0;
            if (docSummmaryRow != null) {
                userCount = Integer.valueOf(String.valueOf(docSummmaryRow.get("USER_COUNT")));
                grpCount = Integer.valueOf(String.valueOf(docSummmaryRow.get("GROUP_COUNT")));
                deviceCount = Integer.valueOf(String.valueOf(docSummmaryRow.get("DEVICE_COUNT")));
            }
            docDetails.put((Object)"SHARED_USER_COUNT", (Object)userCount);
            docDetails.put((Object)"SHARED_GROUP_COUNT", (Object)grpCount);
            docDetails.put((Object)"SHARED_DEVICE_COUNT", (Object)deviceCount);
            if (getLocationPath) {
                final Row serverDocRow = resultDO.getRow("ServerDocDetails", new Criteria(Column.getColumn("ServerDocDetails", "DOC_ID"), (Object)docID, 0));
                docDetails.put((Object)"LOCATION_PATH", serverDocRow.get("LOCATION_PATH"));
            }
            final Long docUpdatedTime = (Long)docRow.get("UPDATED_TIME");
            final Iterator docTagRelIt = resultDO.getRows("DocumentTagRel", new Criteria(Column.getColumn("DocumentTagRel", "MODIFIED_TIME"), (Object)docUpdatedTime, 4));
            if (docTagRelIt != null) {
                final org.json.JSONArray docTags = new org.json.JSONArray();
                while (docTagRelIt.hasNext()) {
                    final Row docTagRelRow = docTagRelIt.next();
                    final Long tagID = (Long)docTagRelRow.get("TAG_ID");
                    final Row docTagRow = resultDO.getRow("DocumentTags", new Criteria(Column.getColumn("DocumentTags", "TAG_ID"), (Object)tagID, 0));
                    docTags.put(docTagRow.get("TAG_NAME"));
                }
                docDetails.put((Object)"TAG_NAME", (Object)docTags);
            }
            final JSONArray cgDetails = new JSONArray();
            final Iterator cgItr = resultDO.getRows("Resource");
            while (cgItr != null && cgItr.hasNext()) {
                final Row resRow = cgItr.next();
                final Long resID = (Long)resRow.get("RESOURCE_ID");
                final String resName = (String)resRow.get("NAME");
                final JSONObject curCGdetals = new JSONObject();
                curCGdetals.put((Object)"CUSTOM_GP_ID", (Object)String.valueOf(resID));
                curCGdetals.put((Object)"CUSTOM_GP_NAME", (Object)String.valueOf(resName));
                curCGdetals.put((Object)"POLICY", (Object)this.getPolicyDetails(resultDO, docID, resID));
                cgDetails.add((Object)curCGdetals);
            }
            docDetails.put((Object)"customGrpList", (Object)cgDetails);
            final JSONArray deviceDetails = new JSONArray();
            final Iterator deviceItr = resultDO.getRows("DocumentManagedDeviceRel");
            while (deviceItr != null && deviceItr.hasNext()) {
                final Row docMDrelRow = deviceItr.next();
                final Long resID2 = (Long)docMDrelRow.get("MANAGEDDEVICE_ID");
                final JSONObject curDevice = new JSONObject();
                curDevice.put((Object)"DEVICE_ID", (Object)resID2);
                curDevice.put((Object)"POLICY", (Object)this.getPolicyDetails(resultDO, docID, resID2));
                deviceDetails.add((Object)curDevice);
            }
            docDetails.put((Object)"deviceList", (Object)deviceDetails);
            final JSONArray userDetails = new JSONArray();
            final Iterator userItr = resultDO.getRows("DocumentToMDMResource");
            while (userItr != null && userItr.hasNext()) {
                final Row docMDresRow = userItr.next();
                final Long resID3 = (Long)docMDresRow.get("RESOURCE_ID");
                final JSONObject curUserdetals = new JSONObject();
                curUserdetals.put((Object)"USER_ID", (Object)resID3);
                curUserdetals.put((Object)"POLICY", (Object)this.getPolicyDetails(resultDO, docID, resID3));
                userDetails.add((Object)curUserdetals);
            }
            docDetails.put((Object)"userList", (Object)userDetails);
        }
        return docDetails;
    }
    
    private JSONObject getPolicyDetails(final DataObject resultDO, final Long docID, final Long resID) throws DataAccessException {
        final JSONObject docPolicydetals = new JSONObject();
        final Criteria criteria = new Criteria(Column.getColumn("DocumentPolicyResourceRel", "DOC_ID"), (Object)docID, 0).and(new Criteria(Column.getColumn("DocumentPolicyResourceRel", "RESOURCE_ID"), (Object)resID, 0));
        final Row docPolicyResRelRow = resultDO.getRow("DocumentPolicyResourceRel", criteria);
        if (docPolicyResRelRow != null) {
            final Long depPolicyID = (Long)docPolicyResRelRow.get("DEPLOYMENT_POLICY_ID");
            if (depPolicyID != null) {
                docPolicydetals.put((Object)"DEPLOYMENT_POLICY_ID", (Object)depPolicyID);
                final Row depPolicyRow = resultDO.getRow("DeploymentPolicy", new Criteria(Column.getColumn("DeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)depPolicyID, 0));
                if (depPolicyRow != null) {
                    final Long depConfigID = (Long)depPolicyRow.get("DEPLOYMENT_CONFIG_ID");
                    if (depConfigID != null) {
                        final Row depConfigRow = resultDO.getRow("DeploymentConfig", new Criteria(Column.getColumn("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"), (Object)depConfigID, 0));
                        if (depConfigRow != null) {
                            final String depConfigName = (String)depConfigRow.get("DEPLOYMENT_CONFIG_NAME");
                            if (!SyMUtil.isStringEmpty(depConfigName)) {
                                docPolicydetals.put((Object)"DEPLOYMENT_CONFIG_ID", (Object)depConfigID);
                                docPolicydetals.put((Object)"DEPLOYMENT_CONFIG_NAME", (Object)depConfigName);
                            }
                        }
                    }
                }
            }
        }
        return docPolicydetals;
    }
    
    public JSONObject getDocDetails(final Long[] customerID, final Long docID) throws Exception {
        return this.getDocDetails(customerID, docID, false);
    }
    
    private HashMap getDocsNames(final Long[] customerID, final List<Long> docIDs) throws Exception {
        final HashMap docs = new HashMap();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
        final Criteria docIds = new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)docIDs.toArray(), 8);
        final Criteria custIds = new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerID, 8);
        query.addSelectColumn(Column.getColumn("DocumentDetails", "*"));
        query.setCriteria(docIds.and(custIds));
        CustomerInfoThreadLocal.setSkipCustomerFilter("true");
        final DataObject resultDO = SyMUtil.getPersistenceLite().get(query);
        CustomerInfoThreadLocal.setSkipCustomerFilter("false");
        if (!resultDO.isEmpty()) {
            final Iterator rows = resultDO.getRows("DocumentDetails");
            while (rows.hasNext()) {
                final JSONObject j = new JSONObject();
                final Row r = rows.next();
                docs.put(r.get("DOC_ID"), r.get("DOC_NAME"));
            }
        }
        return docs;
    }
    
    public String getDocExtention(final int docType) {
        switch (docType) {
            case 0: {
                return "NA";
            }
            case 1: {
                return ".doc";
            }
            case 2: {
                return ".ppt";
            }
            case 3: {
                return ".xls";
            }
            case 4: {
                return ".pdf";
            }
            case 5: {
                return ".txt";
            }
            case 6: {
                return ".xps";
            }
            case 11: {
                return ".rtf";
            }
            case 7: {
                return ".pptx";
            }
            case 8: {
                return ".xlsx";
            }
            case 9: {
                return ".docx";
            }
            case 10: {
                return ".epub";
            }
            case 12: {
                return ".html";
            }
            case 13: {
                return ".jpeg";
            }
            case 14: {
                return ".jpg";
            }
            case 15: {
                return ".png";
            }
            case 16: {
                return ".gif";
            }
            case 17: {
                return ".bmp";
            }
            case 18: {
                return ".wav";
            }
            case 19: {
                return ".mp3";
            }
            case 20: {
                return ".au";
            }
            case 21: {
                return ".aiff";
            }
            case 22: {
                return ".ogg";
            }
            case 23: {
                return ".mp4";
            }
            case 24: {
                return ".mov";
            }
            case 25: {
                return ".flv";
            }
            case 26: {
                return ".3gp";
            }
            case 27: {
                return ".avi";
            }
            case 28: {
                return ".mkv";
            }
            case 29: {
                return ".ovpn";
            }
            default: {
                return "";
            }
        }
    }
    
    public int getDocType(final String extention) {
        final String lowerCase = extention.toLowerCase();
        switch (lowerCase) {
            case ".doc": {
                return 1;
            }
            case ".rtf": {
                return 11;
            }
            case ".ppt": {
                return 2;
            }
            case ".xls": {
                return 3;
            }
            case ".pdf": {
                return 4;
            }
            case ".txt": {
                return 5;
            }
            case ".xps": {
                return 6;
            }
            case ".pptx": {
                return 7;
            }
            case ".xlsx": {
                return 8;
            }
            case ".docx": {
                return 9;
            }
            case ".epub": {
                return 10;
            }
            case ".html": {
                return 12;
            }
            case ".jpeg": {
                return 13;
            }
            case ".jpg": {
                return 14;
            }
            case ".png": {
                return 15;
            }
            case ".gif": {
                return 16;
            }
            case ".bmp": {
                return 17;
            }
            case ".wav": {
                return 18;
            }
            case ".mp3": {
                return 19;
            }
            case ".au": {
                return 20;
            }
            case ".aiff": {
                return 21;
            }
            case ".ogg": {
                return 22;
            }
            case ".mp4": {
                return 23;
            }
            case ".mov": {
                return 24;
            }
            case ".flv": {
                return 25;
            }
            case ".3gp": {
                return 26;
            }
            case ".avi": {
                return 27;
            }
            case ".mkv": {
                return 28;
            }
            case ".ovpn": {
                return 29;
            }
            default: {
                return 0;
            }
        }
    }
    
    public String getDocImage(final int docType) {
        switch (docType) {
            case 1: {
                return "/images/documentManagementDemo/word.png";
            }
            case 11: {
                return "/images/documentManagementDemo/rtf.png";
            }
            case 2: {
                return "/images/documentManagementDemo/ppt.png";
            }
            case 3: {
                return "/images/documentManagementDemo/xls.png";
            }
            case 4: {
                return "/images/documentManagementDemo/pdf.png";
            }
            case 5: {
                return "/images/documentManagementDemo/txt.png";
            }
            case 6: {
                return "/images/documentManagementDemo/common.png";
            }
            case 7: {
                return "/images/documentManagementDemo/ppt.png";
            }
            case 8: {
                return "/images/documentManagementDemo/xls.png";
            }
            case 9: {
                return "/images/documentManagementDemo/word.png";
            }
            case 10: {
                return "/images/documentManagementDemo/epub.png";
            }
            case 12: {
                return "/images/documentManagementDemo/html.png";
            }
            case 13: {
                return "/images/documentManagementDemo/image.png";
            }
            case 14: {
                return "/images/documentManagementDemo/image.png";
            }
            case 15: {
                return "/images/documentManagementDemo/image.png";
            }
            case 16: {
                return "/images/documentManagementDemo/image.png";
            }
            case 17: {
                return "/images/documentManagementDemo/image.png";
            }
            case 18: {
                return "/images/documentManagementDemo/audio.png";
            }
            case 19: {
                return "/images/documentManagementDemo/audio.png";
            }
            case 20: {
                return "/images/documentManagementDemo/audio.png";
            }
            case 21: {
                return "/images/documentManagementDemo/audio.png";
            }
            case 22: {
                return "/images/documentManagementDemo/audio.png";
            }
            case 23: {
                return "/images/documentManagementDemo/video.png";
            }
            case 24: {
                return "/images/documentManagementDemo/video.png";
            }
            case 25: {
                return "/images/documentManagementDemo/video.png";
            }
            case 26: {
                return "/images/documentManagementDemo/video.png";
            }
            case 27: {
                return "/images/documentManagementDemo/video.png";
            }
            case 28: {
                return "/images/documentManagementDemo/video.png";
            }
            default: {
                return "/images/documentManagementDemo/common.png";
            }
        }
    }
    
    private Row populateRowColumn(final Row row, final String columnName, final org.json.JSONObject jsObject) throws JSONException {
        if (jsObject.has(columnName)) {
            row.set(columnName, jsObject.get(columnName));
        }
        return row;
    }
    
    private Row populateDocDetailsRow(Row docDetailsRow, final org.json.JSONObject docDetails) throws JSONException {
        docDetailsRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
        final boolean downloadDoc = docDetails.getBoolean("DOWNLOAD_DOC");
        if (downloadDoc) {
            docDetailsRow.set("SIZE", (Object)Long.parseLong((String)docDetails.get("CONTENT-LENGTH")));
            docDetailsRow = this.populateRowColumn(docDetailsRow, "DOC_TYPE", docDetails);
            docDetailsRow = this.populateRowColumn(docDetailsRow, "DOC_NAME", docDetails);
        }
        docDetailsRow = this.populateRowColumn(docDetailsRow, "DESCRIPTION", docDetails);
        docDetailsRow = this.populateRowColumn(docDetailsRow, "MIME_TYPE", docDetails);
        docDetailsRow.set("LAST_MODIFIED_BY", (Object)MDMUtil.getInstance().getLoggedInUserID());
        return docDetailsRow;
    }
    
    private org.json.JSONObject addOrUpdateDocDetails(final org.json.JSONObject docDetails) throws JSONException, DataAccessException {
        docDetails.put("STATUS", 90001);
        Row docDetailsRow = null;
        final String task = String.valueOf(docDetails.get("Task".toUpperCase()));
        int eventID = 2101;
        String remarks = "mdm.content.al.add";
        Long docAddedTime = -1L;
        if (task.equals("ADD_DOC")) {
            final DataObject dObj = SyMUtil.getPersistenceLite().constructDataObject();
            docDetailsRow = new Row("DocumentDetails");
            docDetailsRow = this.populateDocDetailsRow(docDetailsRow, docDetails);
            docAddedTime = System.currentTimeMillis();
            docDetailsRow.set("ADDED_TIME", (Object)docAddedTime);
            docDetailsRow = this.populateRowColumn(docDetailsRow, "REPOSITORY_TYPE", docDetails);
            docDetailsRow = this.populateRowColumn(docDetailsRow, "CUSTOMER_ID", docDetails);
            docDetailsRow.set("ADDED_BY", (Object)MDMUtil.getInstance().getLoggedInUserID());
            dObj.addRow(docDetailsRow);
            SyMUtil.getPersistenceLite().add(dObj);
        }
        else if (task.equals("MODIFY_DOC")) {
            eventID = 2102;
            remarks = "mdm.content.al.update";
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentDetails", new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)JSONUtil.optLongForUVH(docDetails, "DOC_ID", (Long)null), 0));
            docDetailsRow = dObj.getFirstRow("DocumentDetails");
            docDetailsRow = this.populateDocDetailsRow(docDetailsRow, docDetails);
            dObj.updateRow(docDetailsRow);
            SyMUtil.getPersistenceLite().update(dObj);
        }
        docDetails.put("ADDED_TIME", (Object)docAddedTime);
        docDetails.put("STATUS", 1);
        docDetails.put("DOC_TYPE", docDetailsRow.get("DOC_TYPE"));
        docDetails.put("DOC_ID", (Object)docDetailsRow.get("DOC_ID"));
        docDetails.put("DOC_NAME", docDetailsRow.get("DOC_NAME"));
        DocMgmt.getInstance().logEvent(eventID, (Long)docDetails.get("DOC_ID"), remarks, String.valueOf(docDetails.get("DOC_NAME")), docDetails.getLong("CUSTOMER_ID"));
        return docDetails;
    }
    
    private org.json.JSONObject getUniqueFileName(final org.json.JSONObject docDetails) throws JSONException, DataAccessException {
        final String docFileName = (String)docDetails.get("FILE-NAME");
        final int extentionIndex = docFileName.lastIndexOf(".");
        String docDisplayName = docFileName.substring(0, extentionIndex);
        final String extention = docFileName.substring(extentionIndex);
        final int docType = this.getDocType(extention);
        if (docType < 1) {
            docDisplayName = docFileName;
        }
        DocMgmt.logger.log(Level.INFO, "mime type detected as : {0} , doc type detected as : {1}", new Object[] { docDetails.optString("MIME_TYPE", "NA"), String.valueOf(docType) });
        final Long customerID = docDetails.getLong("CUSTOMER_ID");
        for (boolean findUniqueName = true; findUniqueName; findUniqueName = true) {
            findUniqueName = false;
            Criteria docIdentifyCriteria = new Criteria(Column.getColumn("DocumentDetails", "DOC_NAME"), (Object)docDisplayName, 0).and(new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerID, 0)).and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)1, 0));
            if (String.valueOf(docDetails.get("TASK")).equalsIgnoreCase("MODIFY_DOC")) {
                docIdentifyCriteria = docIdentifyCriteria.and(new Criteria(Column.getColumn("DocumentDetails", "SIZE"), (Object)0L, 1));
            }
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentDetails", docIdentifyCriteria);
            if (!dObj.isEmpty()) {
                String status_msg = this.getMsgForCode(90006, new String[] { docDisplayName });
                status_msg = status_msg.replace("{0}", docDisplayName);
                docDetails.put("STATUS_MSG", (Object)status_msg);
                final int brStIndex = docDisplayName.lastIndexOf("(");
                final int brClIndex = docDisplayName.lastIndexOf(")");
                if (brStIndex != -1 && brClIndex != -1 && (brClIndex == docDisplayName.length() - 1 || brClIndex == docDisplayName.lastIndexOf(".") - 1)) {
                    final String brVal = docDisplayName.substring(brStIndex + 1, brClIndex);
                    try {
                        Long repCount = Long.valueOf(brVal);
                        ++repCount;
                        docDisplayName = docDisplayName.substring(0, brStIndex) + "(" + String.valueOf(repCount) + ")" + docDisplayName.substring(brClIndex + 1);
                    }
                    catch (final NumberFormatException ex) {
                        final int docDisplayNameExtentionIndex = docDisplayName.lastIndexOf(".");
                        if (docDisplayNameExtentionIndex == -1) {
                            docDisplayName += "(1)";
                        }
                        else {
                            docDisplayName = docDisplayName.substring(0, docDisplayNameExtentionIndex) + "(1)" + docDisplayName.substring(docDisplayNameExtentionIndex);
                        }
                    }
                }
                else {
                    final int docDisplayNameExtentionIndex2 = docDisplayName.lastIndexOf(".");
                    if (docDisplayNameExtentionIndex2 == -1) {
                        docDisplayName += "(1)";
                    }
                    else {
                        docDisplayName = docDisplayName.substring(0, docDisplayNameExtentionIndex2) + "(1)" + docDisplayName.substring(docDisplayNameExtentionIndex2);
                    }
                }
            }
        }
        docDetails.put("DOC_TYPE", docType);
        docDetails.put("DOC_NAME", (Object)docDisplayName);
        docDetails.put("SIZE", Long.parseLong((String)docDetails.get("CONTENT-LENGTH")));
        docDetails.put("STATUS", 1);
        return docDetails;
    }
    
    private org.json.JSONObject addOrUpdateServerDocDetails(org.json.JSONObject docDetails) throws Exception {
        int status = 90001;
        docDetails.put("STATUS", status);
        final boolean downloadDoc = docDetails.getBoolean("DOWNLOAD_DOC");
        if (downloadDoc) {
            docDetails = this.getUniqueFileName(docDetails);
            status = docDetails.getInt("STATUS");
            if (status != 1) {
                return docDetails;
            }
        }
        docDetails = this.addOrUpdateDocDetails(docDetails);
        status = docDetails.getInt("STATUS");
        if (status == 1) {
            docDetails.put("STATUS", 90001);
            final Long docID = docDetails.getLong("DOC_ID");
            final Long customerID = docDetails.getLong("CUSTOMER_ID");
            String docSavePath = null;
            String checkSum = null;
            if (downloadDoc) {
                docSavePath = DocMgmtConstants.DOC_FILE_DIRECTORY + File.separator + customerID + File.separator + String.valueOf(docID) + ".cms";
                String fullDirectoryPath = DocMgmtConstants.DOC_BASE_DIRECTORY + docSavePath;
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    fullDirectoryPath = DocMgmtConstants.OLD_DOC_BASE_DIRECTORY + docSavePath;
                }
                checkSum = ChecksumProvider.getInstance().GetSHA256CheckSum((String)docDetails.get("DOCUMENT_FILE_PATH"));
                final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
                fileAccessAPI.deleteDirectory(fullDirectoryPath);
                docDetails.put("DOCUMENT_FILE_PATH", (Object)fullDirectoryPath);
                docSavePath = docSavePath.replace('\\', '/');
            }
            final DataObject dObj = SyMUtil.getPersistenceLite().get("ServerDocDetails", new Criteria(Column.getColumn("ServerDocDetails", "DOC_ID"), (Object)docID, 0));
            if (dObj.isEmpty()) {
                final Row row = new Row("ServerDocDetails");
                row.set("DOC_ID", (Object)docID);
                row.set("LOCATION_PATH", (Object)docSavePath);
                row.set("CHECKSUM", (Object)checkSum);
                dObj.addRow(row);
                SyMUtil.getPersistenceLite().add(dObj);
            }
            else {
                final Row row = dObj.getFirstRow("ServerDocDetails");
                row.set("CHECKSUM", (Object)checkSum);
                dObj.updateRow(row);
                SyMUtil.getPersistenceLite().update(dObj);
            }
            docDetails.put("STATUS", 1);
        }
        return docDetails;
    }
    
    private void addOrUpdateDocTagRel(final org.json.JSONObject docDetails) throws JSONException, DataAccessException {
        final Long docID = docDetails.getLong("DOC_ID");
        final Long customerID = docDetails.getLong("CUSTOMER_ID");
        final List tagNameList = this.parseIntoList(docDetails.get("TAG_NAME"));
        if (tagNameList != null && !tagNameList.isEmpty()) {
            DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentTags", new Criteria(Column.getColumn("DocumentTags", "TAG_NAME"), (Object)tagNameList.toArray(), 8).and(new Criteria(Column.getColumn("DocumentTags", "CUSTOMER_ID"), (Object)customerID, 0)));
            final org.json.JSONArray tagIDs = new org.json.JSONArray();
            final Iterator it = dObj.getRows("DocumentTags");
            if (it != null) {
                while (it.hasNext()) {
                    final Row docTagRow = it.next();
                    tagIDs.put((Object)docTagRow.get("TAG_ID"));
                }
            }
            for (int i = 0; i < tagIDs.length(); ++i) {
                dObj = SyMUtil.getPersistenceLite().get("DocumentTagRel", new Criteria(Column.getColumn("DocumentTagRel", "TAG_ID"), (Object)tagIDs.getLong(i), 0).and(new Criteria(Column.getColumn("DocumentTagRel", "DOC_ID"), (Object)docID, 0)));
                if (dObj.isEmpty()) {
                    final Row documentTagRelRow = new Row("DocumentTagRel");
                    documentTagRelRow.set("DOC_ID", (Object)docID);
                    documentTagRelRow.set("TAG_ID", (Object)tagIDs.getLong(i));
                    documentTagRelRow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                    dObj.addRow(documentTagRelRow);
                    SyMUtil.getPersistenceLite().add(dObj);
                }
                else {
                    final Row documentTagRelRow = dObj.getFirstRow("DocumentTagRel");
                    documentTagRelRow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                    dObj.updateRow(documentTagRelRow);
                    SyMUtil.getPersistenceLite().update(dObj);
                }
            }
        }
    }
    
    public void updateDocDeviceInfoStatus(final Criteria criteria, final List<Join> joins) throws DataAccessException {
        final SelectQuery docAssociatedDeviceQuery = (SelectQuery)new SelectQueryImpl(new Table("DocumentDetails"));
        docAssociatedDeviceQuery.addSelectColumns((List)new ArrayList(Arrays.asList(Column.getColumn("DocumentDetails", "DOC_ID"), Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"), Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"), Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"), Column.getColumn("DocumentManagedDeviceRel", "MODIFIED_TIME"), Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"))));
        docAssociatedDeviceQuery.addJoin(new Join("DocumentDetails", "DocumentManagedDeviceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
        docAssociatedDeviceQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2));
        if (joins != null && joins.size() > 0) {
            for (final Join join : joins) {
                docAssociatedDeviceQuery.addJoin(join);
            }
        }
        if (criteria != null) {
            docAssociatedDeviceQuery.setCriteria(criteria);
        }
        List<Long> docsList = null;
        final DataObject dObj = SyMUtil.getPersistenceLite().get(docAssociatedDeviceQuery);
        if (dObj != null && !dObj.isEmpty()) {
            final Iterator docMDrelIt = dObj.getRows("DocumentManagedDeviceRel");
            final List<Long> toBeNotifiedDeviceList = new ArrayList<Long>();
            while (docMDrelIt != null && docMDrelIt.hasNext()) {
                final Row docMDrelRow = docMDrelIt.next();
                final Long docMdId = (Long)docMDrelRow.get("DOC_MD_ID");
                final Row docMDInfoRow = dObj.getRow("DocumentManagedDeviceInfo", new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"), (Object)docMdId, 0).and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"), (Object)6, 0)));
                docMDrelRow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                if (docMDInfoRow != null) {
                    docMDInfoRow.set("STATUS_ID", (Object)200);
                    dObj.updateRow(docMDInfoRow);
                    final Long deviceResId = (Long)docMDrelRow.get("MANAGEDDEVICE_ID");
                    toBeNotifiedDeviceList.add(deviceResId);
                }
                dObj.updateRow(docMDrelRow);
            }
            SyMUtil.getPersistenceLite().update(dObj);
            this.wakeUpDevice(toBeNotifiedDeviceList);
            docsList = DBUtil.getColumnValuesAsList(dObj.getRows("DocumentDetails"), "DOC_ID");
        }
        DocSummaryHandler.getInstance().reviseDocSummary(docsList);
    }
    
    public org.json.JSONObject addOrUpdateServerDoc(org.json.JSONObject docDetails) {
        final long timeStamp = System.currentTimeMillis();
        DocMgmt.logger.log(Level.INFO, "entering synchronized block {0} : {1}", new Object[] { timeStamp, docDetails.toString() });
        synchronized (DocMgmtDataHandler.DOC_TABLE_LOCK) {
            DocMgmt.logger.log(Level.INFO, "entered synchronized block at {0},{1} : {2}", new Object[] { timeStamp, System.currentTimeMillis(), docDetails.toString() });
            final TransactionManager tm = SyMUtil.getUserTransaction();
            try {
                tm.begin();
                docDetails = this.addOrUpdateServerDocDetails(docDetails);
                final int status = docDetails.getInt("STATUS");
                if (status == 1) {
                    this.addOrUpdateDocTagRel(docDetails);
                    this.updateDocDeviceInfoStatus(new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)docDetails.getLong("DOC_ID"), 0), null);
                    tm.commit();
                    return docDetails;
                }
                tm.setRollbackOnly();
            }
            catch (final Exception ex) {
                try {
                    tm.setRollbackOnly();
                    docDetails.put("STATUS", 90001);
                }
                catch (final IllegalStateException | SystemException | JSONException ex2) {
                    DocMgmt.logger.log(Level.SEVERE, null, ex2);
                }
                DocMgmt.logger.log(Level.SEVERE, null, ex);
                try {
                    if (tm.getStatus() == 1) {
                        DocMgmt.logger.log(Level.SEVERE, "rolling back adding/updating server doc");
                        tm.rollback();
                    }
                }
                catch (final SystemException ex3) {
                    DocMgmt.logger.log(Level.SEVERE, "excption occured while getting transaction status and rolling back", (Throwable)ex3);
                }
            }
            finally {
                try {
                    if (tm.getStatus() == 1) {
                        DocMgmt.logger.log(Level.SEVERE, "rolling back adding/updating server doc");
                        tm.rollback();
                    }
                }
                catch (final SystemException ex4) {
                    DocMgmt.logger.log(Level.SEVERE, "excption occured while getting transaction status and rolling back", (Throwable)ex4);
                }
            }
            DocMgmt.logger.log(Level.INFO, "exiting synchronized block timestamp : {0} , at : {1}", new Object[] { timeStamp, System.currentTimeMillis() });
        }
        return docDetails;
    }
    
    public void checkAndOpenDocDistMsg() {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1));
            selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID"));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("DocumentDetails", "DOC_ID"), true));
            selectQuery.setRange(new Range(0, 1));
            DataObject dobj = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dobj == null || (dobj != null && dobj.isEmpty())) {
                MessageProvider.getInstance().unhideMessage("MDM_ADD_DOC");
            }
            else {
                MessageProvider.getInstance().hideMessage("MDM_ADD_DOC");
                selectQuery.setCriteria(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0));
                selectQuery.addJoin(new Join("DocumentDetails", "DocumentManagedDeviceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
                selectQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2));
                dobj = MDMUtil.getPersistenceLite().get(selectQuery);
                if (dobj == null || (dobj != null && dobj.isEmpty())) {
                    MessageProvider.getInstance().unhideMessage("MDM_DOC_NOT_DISTRIBUTED");
                }
                else {
                    MessageProvider.getInstance().hideMessage("MDM_DOC_NOT_DISTRIBUTED");
                }
            }
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public int addOrUpdateTag(final org.json.JSONObject tagDetails) throws Exception {
        try {
            final String tagName = (String)tagDetails.get("TAG_NAME");
            final Long customerID = (Long)tagDetails.get("CUSTOMER_ID");
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentTags", new Criteria(Column.getColumn("DocumentTags", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("DocumentTags", "TAG_NAME"), (Object)tagName, 0)));
            if (dObj.isEmpty()) {
                if (tagDetails.has("TAG_ID")) {
                    final Long tagID = (Long)tagDetails.get("TAG_ID");
                    final Criteria idCriteria = new Criteria(Column.getColumn("DocumentTags", "TAG_ID"), (Object)tagID, 0);
                    final Criteria idCustIdCriteria = new Criteria(Column.getColumn("DocumentTags", "CUSTOMER_ID"), (Object)customerID, 0);
                    final DataObject dObjId = SyMUtil.getPersistenceLite().get("DocumentTags", idCriteria.and(idCustIdCriteria));
                    if (!dObjId.isEmpty()) {
                        final Row docTagRow = dObjId.getRow("DocumentTags");
                        docTagRow.set("TAG_NAME", (Object)tagName);
                        dObjId.updateRow(docTagRow);
                        SyMUtil.getPersistenceLite().update(dObjId);
                        getInstance().updateDocDeviceInfoStatus(new Criteria(Column.getColumn("DocumentTagRel", "TAG_ID"), (Object)tagID, 0), new ArrayList<Join>(Arrays.asList(new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2))));
                    }
                }
                else {
                    final Row docTagRow2 = new Row("DocumentTags");
                    docTagRow2.set("TAG_NAME", (Object)tagName);
                    docTagRow2.set("CUSTOMER_ID", (Object)customerID);
                    dObj.addRow(docTagRow2);
                    SyMUtil.getPersistenceLite().add(dObj);
                }
                return 1;
            }
            return 90005;
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    public int deleteTag(final long tagId, final Long custId) throws Exception {
        try {
            final Criteria idCriteria = new Criteria(Column.getColumn("DocumentTags", "TAG_ID"), (Object)tagId, 0);
            final Criteria idCustIdCriteria = new Criteria(Column.getColumn("DocumentTags", "CUSTOMER_ID"), (Object)custId, 0);
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentTags", idCriteria.and(idCustIdCriteria));
            if (!dObj.isEmpty()) {
                getInstance().updateDocDeviceInfoStatus(new Criteria(Column.getColumn("DocumentTagRel", "TAG_ID"), (Object)tagId, 0), new ArrayList<Join>(Arrays.asList(new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2))));
                SyMUtil.getPersistenceLite().delete(new Criteria(Column.getColumn("DocumentTags", "TAG_ID"), (Object)tagId, 0));
                return 1;
            }
            return 90010;
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    private boolean isPathValid(final String docLocationPath) {
        return !SyMUtil.isStringEmpty(docLocationPath) && !docLocationPath.contains("..") && docLocationPath.contains(DocMgmtConstants.DOC_FILE_DIRECTORY) && docLocationPath.endsWith(".cms");
    }
    
    private void actuallyDeleteFile(final Criteria baseCri) throws Exception {
        final SelectQuery toBeDeletedFilesQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ServerDocDetails"));
        toBeDeletedFilesQuery.addJoin(new Join("ServerDocDetails", "DocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
        toBeDeletedFilesQuery.setCriteria(baseCri);
        toBeDeletedFilesQuery.addSelectColumn(Column.getColumn("ServerDocDetails", "DOC_ID"));
        toBeDeletedFilesQuery.addSelectColumn(Column.getColumn("ServerDocDetails", "LOCATION_PATH"));
        final DataObject dobj = MDMUtil.getPersistenceLite().get(toBeDeletedFilesQuery);
        final List<String> toBeDeletedFilePaths = DBUtil.getColumnValuesAsList(dobj.getRows("ServerDocDetails"), "LOCATION_PATH");
        for (String docLocationPath : toBeDeletedFilePaths) {
            docLocationPath = docLocationPath.replace("/", File.separator);
            if (!this.isPathValid(docLocationPath)) {
                throw new Exception(MessageFormat.format("{0} is in a folder which is outside the scope of files allowed to delete by Content Mgmt", String.valueOf(docLocationPath)));
            }
            docLocationPath = DocMgmtConstants.DOC_BASE_DIRECTORY + docLocationPath;
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                docLocationPath = DocMgmtConstants.OLD_DOC_BASE_DIRECTORY + docLocationPath;
            }
            final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
            if (fileAccessAPI.isFileExists(docLocationPath)) {
                fileAccessAPI.deleteFile(docLocationPath);
            }
            else {
                DocMgmt.logger.log(Level.WARNING, "no file exists at {0}", new Object[] { String.valueOf(docLocationPath) });
            }
        }
    }
    
    public int markDocForDeletion(final org.json.JSONObject docs, final Long requestCustomerID, final Long[] customerIDs) throws JSONException {
        int status = 1;
        final Long modifiedTime = System.currentTimeMillis();
        final List<Long> toBeDeletedDocs = this.parseRequestIntoList(docs, "DOC_ID");
        if (!toBeDeletedDocs.isEmpty()) {
            Connection connection = null;
            RelationalAPI relationalAPI = null;
            try {
                this.removeDocPolicyResourceRelByDocDelete(toBeDeletedDocs);
                final Criteria baseCri = new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerIDs, 8).and(new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)toBeDeletedDocs.toArray(new Long[toBeDeletedDocs.size()]), 8));
                this.actuallyDeleteFile(baseCri);
                relationalAPI = RelationalAPI.getInstance();
                connection = relationalAPI.getConnection();
                UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DocumentDetails");
                updateQuery.setCriteria(baseCri.and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1)));
                updateQuery.setUpdateColumn("UPDATED_TIME", (Object)modifiedTime);
                updateQuery.setUpdateColumn("REPOSITORY_TYPE", (Object)0);
                relationalAPI.execute(connection, relationalAPI.getUpdateSQL(updateQuery));
                updateQuery = (UpdateQuery)new UpdateQueryImpl("DocumentManagedDeviceRel");
                updateQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
                updateQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2));
                updateQuery.setCriteria(baseCri.and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0)));
                updateQuery.setUpdateColumn("MODIFIED_TIME", (Object)modifiedTime);
                relationalAPI.execute(connection, relationalAPI.getUpdateSQL(updateQuery));
                updateQuery = (UpdateQuery)new UpdateQueryImpl("DocumentManagedDeviceInfo");
                updateQuery.addJoin(new Join("DocumentManagedDeviceInfo", "DocumentManagedDeviceRel", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2));
                updateQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
                updateQuery.setCriteria(baseCri.and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0)).and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"), (Object)12, 0)));
                updateQuery.setUpdateColumn("ASSOCIATE", (Object)Boolean.FALSE);
                updateQuery.setUpdateColumn("STATUS_ID", (Object)6);
                relationalAPI.execute(connection, relationalAPI.getUpdateSQL(updateQuery));
                final SelectQuery toBeNotifiedDeviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
                toBeNotifiedDeviceQuery.addJoin(new Join("DocumentDetails", "DocumentManagedDeviceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
                toBeNotifiedDeviceQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2));
                toBeNotifiedDeviceQuery.setCriteria(baseCri.and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0)).and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"), (Object)12, 1)));
                toBeNotifiedDeviceQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"));
                toBeNotifiedDeviceQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"));
                final DataObject dobj = MDMUtil.getPersistenceLite().get(toBeNotifiedDeviceQuery);
                final List<Long> toBeNotifiedList = DBUtil.getColumnValuesAsList(dobj.getRows("DocumentManagedDeviceRel"), "MANAGEDDEVICE_ID");
                updateQuery = (UpdateQuery)new UpdateQueryImpl("DocumentManagedDeviceInfo");
                updateQuery.addJoin(new Join("DocumentManagedDeviceInfo", "DocumentManagedDeviceRel", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2));
                updateQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
                updateQuery.setCriteria(baseCri.and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0)).and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"), (Object)12, 1)));
                updateQuery.setUpdateColumn("ASSOCIATE", (Object)Boolean.FALSE);
                updateQuery.setUpdateColumn("STATUS_ID", (Object)12);
                relationalAPI.execute(connection, relationalAPI.getUpdateSQL(updateQuery));
                DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DocumentToDeviceGroup");
                deleteQuery.setCriteria(new Criteria(Column.getColumn("DocumentToDeviceGroup", "DOC_ID"), (Object)toBeDeletedDocs.toArray(new Long[toBeDeletedDocs.size()]), 8));
                relationalAPI.execute(connection, relationalAPI.getDeleteSQL(deleteQuery));
                deleteQuery = (DeleteQuery)new DeleteQueryImpl("DocumentToMDMResource");
                deleteQuery.setCriteria(new Criteria(Column.getColumn("DocumentToMDMResource", "DOC_ID"), (Object)toBeDeletedDocs.toArray(new Long[toBeDeletedDocs.size()]), 8));
                relationalAPI.execute(connection, relationalAPI.getDeleteSQL(deleteQuery));
                final Iterator docIt = MDMUtil.getPersistenceLite().get("DocumentDetails", baseCri).getRows("DocumentDetails");
                while (docIt != null && docIt.hasNext()) {
                    final Row docRow = docIt.next();
                    DocMgmt.getInstance().logEvent(2103, (Long)docRow.get("DOC_ID"), "mdm.content.al.remove", docRow.get("DOC_NAME"), requestCustomerID);
                }
                DocSummaryHandler.getInstance().reviseDocSummary(toBeDeletedDocs);
                this.wakeUpDevice(toBeNotifiedList);
            }
            catch (final Exception ex) {
                status = 90001;
                DocMgmt.logger.log(Level.SEVERE, null, ex);
                try {
                    if (connection != null) {
                        connection.close();
                    }
                }
                catch (final Exception ex) {
                    DocMgmt.logger.log(Level.SEVERE, null, ex);
                }
            }
            finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                }
                catch (final Exception ex2) {
                    DocMgmt.logger.log(Level.SEVERE, null, ex2);
                }
            }
        }
        return status;
    }
    
    public void updateDeviceForPolicyId(final Long policyId) throws DataAccessException {
        final Criteria baseCri = new Criteria(Column.getColumn("DocumentPolicyResourceRel", "DEPLOYMENT_POLICY_ID"), (Object)policyId, 0);
        final Join docPolicyJoin = new Join("DocumentDetails", "DocumentPolicyResourceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2);
        this.updateDocDeviceInfoStatus(baseCri, new ArrayList<Join>(Arrays.asList(docPolicyJoin)));
    }
    
    private void updateStatus(final DataObject dObj, final Long agentLastSyncAt, final Long syncRequstTime, final Long deviceID) {
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "DocDiffUpdate");
        taskInfoMap.put("poolName", "mdmPool");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        final Properties taskProps = new Properties();
        ((Hashtable<String, DataObject>)taskProps).put("DocumentDetails", dObj);
        ((Hashtable<String, Long>)taskProps).put("SYNC_REQUEST_TIME", syncRequstTime);
        ((Hashtable<String, Long>)taskProps).put("MANAGEDDEVICE_ID", deviceID);
        ((Hashtable<String, Long>)taskProps).put("AGENT_APPLIED_TIME", agentLastSyncAt);
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.doc.DocTask", taskInfoMap, taskProps);
    }
    
    protected DataObject getDiff(final Long deviceID, final Long agentLastSyncAt) throws DataAccessException {
        Criteria docDiffCriteria = new Criteria(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), (Object)deviceID, 0);
        if (agentLastSyncAt != -1L) {
            docDiffCriteria = docDiffCriteria.and(new Criteria(Column.getColumn("DocumentManagedDeviceRel", "MODIFIED_TIME"), (Object)agentLastSyncAt, 4).and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"), (Object)6, 1)));
        }
        final SelectQuery docDiffQuery = SyMUtil.formSelectQuery("DocumentDetails", docDiffCriteria, new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DocumentDetails", "SIZE"), Column.getColumn("DocumentDetails", "DOC_ID"), Column.getColumn("DocumentDetails", "DOC_NAME"), Column.getColumn("DocumentDetails", "DOC_TYPE"), Column.getColumn("DocumentDetails", "MIME_TYPE"), Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATED_AT"), Column.getColumn("ServerDocDetails", "DOC_ID"), Column.getColumn("ServerDocDetails", "LOCATION_PATH"), Column.getColumn("ServerDocDetails", "CHECKSUM"), Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"), Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"), Column.getColumn("DocumentPolicyResourceRel", "DEPLOYMENT_POLICY_ID"), Column.getColumn("DocumentPolicyResourceRel", "DOC_ID"), Column.getColumn("DocumentPolicyResourceRel", "RESOURCE_ID"), Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"), Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DocumentDetails", "DocumentManagedDeviceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2), new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2), new Join("DocumentDetails", "ServerDocDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1), new Join("DocumentManagedDeviceRel", "DocumentPolicyResourceRel", new String[] { "DOC_ID", "MANAGEDDEVICE_ID" }, new String[] { "DOC_ID", "RESOURCE_ID" }, 1))), (Criteria)null);
        return SyMUtil.getPersistenceLite().get(docDiffQuery);
    }
    
    private String getAckCacheKey(final Long deviceID, final Long syncRequstTime) {
        return "DocumentManagedDeviceInfo_ACK_" + String.valueOf(deviceID) + "_" + String.valueOf(syncRequstTime);
    }
    
    public boolean processAck(final Long deviceID, final Long syncAckTime, final Long[] docMDids) throws Exception {
        if (docMDids == null || docMDids.length <= 0) {
            return false;
        }
        final String ackKey = this.getAckCacheKey(deviceID, syncAckTime);
        final ArrayList<Long> authroizedDocMDidsInCache = (ArrayList<Long>)ApiFactoryProvider.getCacheAccessAPI().getCache(ackKey, 2);
        ApiFactoryProvider.getCacheAccessAPI().removeCache(ackKey, 2);
        if (authroizedDocMDidsInCache == null) {
            DocMgmt.logger.log(Level.WARNING, "deviceID:{0},syncAckTime:{1},docMDids:{2} timed out", new Object[] { deviceID, syncAckTime, Arrays.toString(docMDids) });
            return false;
        }
        Arrays.sort(docMDids);
        final Long[] authroizedDocMDidsInCacheAr = authroizedDocMDidsInCache.toArray(new Long[authroizedDocMDidsInCache.size()]);
        Arrays.sort(authroizedDocMDidsInCacheAr);
        DocMgmt.logger.log(Level.INFO, "doc md ids requested for ack size : {0}, {1}", new Object[] { docMDids.length, Arrays.toString(docMDids) });
        DocMgmt.logger.log(Level.INFO, "doc md ids authorized in cache for ack size : {0}, {1}", new Object[] { authroizedDocMDidsInCacheAr.length, Arrays.toString(authroizedDocMDidsInCacheAr) });
        final ArrayList<Long> authroizedDocMDids = new ArrayList<Long>();
        for (int i = 0; i < docMDids.length; ++i) {
            final Long curDocMDid = docMDids[i];
            if (authroizedDocMDidsInCache.contains(curDocMDid)) {
                authroizedDocMDids.add(curDocMDid);
                authroizedDocMDidsInCache.remove(curDocMDid);
            }
        }
        DocMgmt.logger.log(Level.INFO, "authorized doc md ids filtered in request from cache size : {0}, {1}", new Object[] { authroizedDocMDids.size(), Arrays.toString(authroizedDocMDids.toArray(new Long[authroizedDocMDids.size()])) });
        final Criteria criteria = new Criteria(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), (Object)deviceID, 0).and(new Criteria(Column.getColumn("DocumentManagedDeviceRel", "MODIFIED_TIME"), (Object)syncAckTime, 7)).and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"), (Object)6, 1)).and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"), (Object)authroizedDocMDids.toArray(new Long[authroizedDocMDids.size()]), 8));
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DocumentManagedDeviceInfo");
        updateQuery.addJoin(new Join("DocumentManagedDeviceInfo", "DocumentManagedDeviceRel", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2));
        updateQuery.setCriteria(criteria);
        updateQuery.setUpdateColumn("AGENT_APPLIED_TIME", (Object)syncAckTime);
        updateQuery.setUpdateColumn("STATUS_ID", (Object)6);
        final int numberOfUpdatedRows = DirectoryQueryutil.getInstance().executeUpdateQuery(updateQuery, false);
        DocMgmt.logger.log(Level.INFO, "device id {0}, syncAckTime {1}, {2} doc meta marked as successfully sent to agent", new Object[] { deviceID, syncAckTime, numberOfUpdatedRows });
        DocSummaryHandler.getInstance().reviseAgentDocSummary(authroizedDocMDids);
        if (!authroizedDocMDidsInCache.isEmpty()) {
            DocMgmt.logger.log(Level.WARNING, "device id {0}, syncAckTime {1}, acknowledgement not received for : {2},{3}", new Object[] { deviceID, syncAckTime, Arrays.toString(authroizedDocMDidsInCache.toArray(new Long[authroizedDocMDidsInCache.size()])), authroizedDocMDidsInCache.size() });
            return false;
        }
        return true;
    }
    
    public org.json.JSONArray processDiffData(final DataObject dObj, final String processType, final Long agentLastSyncAt, final Long syncRequstTime, final Long deviceID, final boolean ackSupported) throws Exception {
        final List<Long> authroizedDocMDidsForAck = new ArrayList<Long>();
        final org.json.JSONArray docTaskArray = new org.json.JSONArray();
        if (!dObj.isEmpty()) {
            final List<Long> docList = new ArrayList<Long>();
            final Iterator docIt = dObj.getRows("DocumentDetails");
            while (docIt != null && docIt.hasNext()) {
                final Row docRow = docIt.next();
                final Long docId = (Long)docRow.get("DOC_ID");
                docList.add(docId);
                final Row serverDocRow = dObj.getRow("ServerDocDetails", new Criteria(Column.getColumn("ServerDocDetails", "DOC_ID"), (Object)docId, 0));
                final Row docMDrelRow = dObj.getRow("DocumentManagedDeviceRel", new Criteria(Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"), (Object)docId, 0));
                final Iterator docPolicyResourceRow = dObj.getRows("DocumentPolicyResourceRel", new Criteria(Column.getColumn("DocumentPolicyResourceRel", "DOC_ID"), (Object)docId, 0));
                final List cmPolicyIds = DBUtil.getColumnValuesAsList(docPolicyResourceRow, "DEPLOYMENT_POLICY_ID");
                final Long docMDid = (Long)docMDrelRow.get("DOC_MD_ID");
                final org.json.JSONObject policyObjWithPrecedence = DocPolicyHandler.getInstance().getCmDeploymentPolicyForPolicyIds(cmPolicyIds);
                final Row docMDinfoRow = dObj.getRow("DocumentManagedDeviceInfo", new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"), (Object)docMDid, 0));
                final int docType = (int)docRow.get("DOC_TYPE");
                String docExtension = this.getDocExtention(docType);
                if (docExtension.equals("NA")) {
                    docExtension = "";
                }
                final String docFileName = docRow.get("DOC_NAME") + docExtension;
                int status = (int)docMDinfoRow.get("STATUS_ID");
                final boolean associate = (boolean)docMDinfoRow.get("ASSOCIATE");
                if (agentLastSyncAt == -1L) {
                    if (!associate) {
                        continue;
                    }
                    status = 12;
                }
                else {
                    if (status != 12 && status != 200) {
                        continue;
                    }
                    if (!associate) {
                        status = 8;
                    }
                }
                if (processType.equals("SyncDocuments")) {
                    final org.json.JSONObject curDocDetails = new org.json.JSONObject();
                    curDocDetails.put("Task", (Object)String.valueOf(status));
                    curDocDetails.put("DOC_ID".toUpperCase(), (Object)String.valueOf(docId));
                    curDocDetails.put("DOC_MD_ID", (Object)String.valueOf(docMDid));
                    authroizedDocMDidsForAck.add(docMDid);
                    if (status != 8) {
                        final String docLocationPath = (String)serverDocRow.get("LOCATION_PATH");
                        curDocDetails.put("DOC_NAME".toUpperCase(), (Object)docFileName);
                        curDocDetails.put("SIZE".toUpperCase(), (Object)String.valueOf(docRow.get("SIZE")));
                        curDocDetails.put("MIME_TYPE", (Object)String.valueOf(docRow.get("MIME_TYPE")));
                        curDocDetails.put("REPOSITORY_TYPE", (Object)String.valueOf(docRow.get("REPOSITORY_TYPE")));
                        curDocDetails.put("DISTRIBUTED_TIME", (Object)String.valueOf(docMDinfoRow.get("ASSOCIATED_AT")));
                        final String checkSum = (String)serverDocRow.get("CHECKSUM");
                        if (!MDMStringUtils.isEmpty(checkSum)) {
                            curDocDetails.put("Checksum", (Object)checkSum);
                        }
                        else {
                            curDocDetails.put("Checksum", (Object)"---");
                        }
                        final HashMap hm = new HashMap();
                        hm.put("IS_SERVER", false);
                        hm.put("IS_AUTHTOKEN", true);
                        hm.put("path", docLocationPath);
                        curDocDetails.put("LOCATION_PATH".toUpperCase(), (Object)(MDMEnrollmentUtil.getInstance().getServerBaseURL() + ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm)));
                        curDocDetails.put("TAG_NAME".toUpperCase(), (Object)this.getDocsTag(docId).toString());
                        curDocDetails.put("DOC_TYPE".toUpperCase(), (Object)String.valueOf(docType));
                        curDocDetails.put("policy", (Object)policyObjWithPrecedence);
                    }
                    docTaskArray.put((Object)curDocDetails);
                }
                else {
                    if (!processType.equals("UPDATE_SYNC_DOC_DIFF")) {
                        continue;
                    }
                    docMDinfoRow.set("AGENT_APPLIED_TIME", (Object)syncRequstTime);
                    docMDinfoRow.set("STATUS_ID", (Object)6);
                    dObj.updateRow(docMDinfoRow);
                }
            }
            if (processType.equals("SyncDocuments")) {
                if (!ackSupported) {
                    DocMgmt.logger.log(Level.INFO, "deviceID : {0} , agentLastSyncAt {1} at {2} marking as success without ack", new Object[] { deviceID, agentLastSyncAt, syncRequstTime });
                    this.updateStatus(dObj, agentLastSyncAt, syncRequstTime, deviceID);
                }
                else {
                    DocMgmt.logger.log(Level.INFO, "deviceID : {0} , agentLastSyncAt {1} at {2}. authorizing {3} for ack to happen within 15 minutes", new Object[] { deviceID, agentLastSyncAt, syncRequstTime, Arrays.toString(authroizedDocMDidsForAck.toArray(new Long[authroizedDocMDidsForAck.size()])) });
                    final String ackKey = this.getAckCacheKey(deviceID, syncRequstTime);
                    ApiFactoryProvider.getCacheAccessAPI().putCache(ackKey, (Object)authroizedDocMDidsForAck, 2, 900);
                }
            }
            if (processType.equals("UPDATE_SYNC_DOC_DIFF")) {
                SyMUtil.getPersistenceLite().update(dObj);
                DocSummaryHandler.getInstance().reviseDocSummary(docList);
            }
        }
        return docTaskArray;
    }
    
    public org.json.JSONObject getDiffAndUpdateStatus(final Long deviceID, final Long agentLastSyncAt, final boolean ackSupported) throws JSONException {
        final Long syncRequstTime = System.currentTimeMillis();
        DocMgmt.logger.log(Level.INFO, "entering : deviceID : {0} , agentLastSyncAt {1} at {2}", new Object[] { deviceID, agentLastSyncAt, syncRequstTime });
        org.json.JSONArray docTaskArray = null;
        try {
            DocMgmt.logger.log(Level.INFO, "getting diff : deviceID : {0} , agentLastSyncAt {1} at {2}", new Object[] { deviceID, agentLastSyncAt, syncRequstTime });
            final DataObject dObj = this.getDiff(deviceID, agentLastSyncAt);
            DocMgmt.logger.log(Level.INFO, "got diff : deviceID : {0} , agentLastSyncAt {1} at {2}, ackSupported : {3}", new Object[] { deviceID, agentLastSyncAt, syncRequstTime, ackSupported });
            docTaskArray = this.processDiffData(dObj, "SyncDocuments", agentLastSyncAt, syncRequstTime, deviceID, ackSupported);
            DocMgmt.logger.log(Level.INFO, "processed diff into response : deviceID : {0} , agentLastSyncAt {1} at {2}, ackSupported : {3}", new Object[] { deviceID, agentLastSyncAt, syncRequstTime, ackSupported });
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
        final org.json.JSONObject docDiff = new org.json.JSONObject();
        docDiff.put("MsgResponseType", (Object)"SyncDocuments");
        if (docTaskArray != null) {
            docDiff.put("ACK_SUPPORTED", true);
            docDiff.put("LastSyncTime", (Object)syncRequstTime);
            docDiff.put("DocumentDetails".toUpperCase(), (Object)docTaskArray);
            docDiff.put("Status", (Object)"Acknowledged");
        }
        else {
            docDiff.put("Status", (Object)"Error");
        }
        DocMgmt.logger.log(Level.INFO, "exiting : deviceID : {0} , agentLastSyncAt {1}", new Object[] { deviceID, agentLastSyncAt });
        return docDiff;
    }
    
    private SelectQuery getDocToGroupBaseQuery(final Long docID) {
        final SelectQuery deviceToDocQuery = SyMUtil.formSelectQuery("DocumentDetails", new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)docID, 0), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DocumentDetails", "DOC_ID"), Column.getColumn("DocumentToDeviceGroup", "DOC_ID"), Column.getColumn("DocumentToDeviceGroup", "CUSTOMGROUP_ID"), Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DocumentDetails", "DocumentToDeviceGroup", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2), new Join("DocumentToDeviceGroup", "CustomGroupMemberRel", new String[] { "CUSTOMGROUP_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2))), (Criteria)null);
        return deviceToDocQuery;
    }
    
    private String getResTypeStr(final int resType) {
        switch (resType) {
            case 2: {
                return "USER";
            }
            case 101: {
                return "CUSTOM_GROUP";
            }
            case 120: {
                return "MOBILE_DEVICE";
            }
            case 121: {
                return "MDM_COMPUTER";
            }
            default: {
                return "";
            }
        }
    }
    
    private boolean isDeviceAssociatedToDocViaAnotherDeviceGroup(final Long deviceResourceID, final Long docID) throws DataAccessException {
        final SelectQuery deviceToDocQuery = this.getDocToGroupBaseQuery(docID);
        deviceToDocQuery.setCriteria(deviceToDocQuery.getCriteria().and(new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)deviceResourceID, 0)));
        final String logMsg = "{0} doc {1} to {2} resource {3} via other device groups";
        final DataObject dObj = SyMUtil.getPersistenceLite().get(deviceToDocQuery);
        if (dObj == null || (dObj != null && dObj.isEmpty())) {
            DocMgmt.logger.log(Level.INFO, logMsg, new String[] { String.valueOf(docID), "not associated", this.getResTypeStr(120), String.valueOf(deviceResourceID) });
            return false;
        }
        DocMgmt.logger.log(Level.INFO, logMsg, new String[] { String.valueOf(docID), "associated", this.getResTypeStr(120), String.valueOf(deviceResourceID) });
        return true;
    }
    
    private boolean isResourceAssociatedToDocViaAnotherUserGroup(final int resType, final Long resID, final Long docID) throws DataAccessException {
        final SelectQuery deviceToResQuery = this.getDocToGroupBaseQuery(docID);
        if (resType == 2) {
            deviceToResQuery.addJoin(new Join("CustomGroupMemberRel", "DocumentToMDMResource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deviceToResQuery.setCriteria(deviceToResQuery.getCriteria().and(new Criteria(Column.getColumn("DocumentToMDMResource", "RESOURCE_ID"), (Object)resID, 0)));
            deviceToResQuery.addSelectColumn(Column.getColumn("DocumentToMDMResource", "DOC_ID"));
            deviceToResQuery.addSelectColumn(Column.getColumn("DocumentToMDMResource", "RESOURCE_ID"));
        }
        else if (resType == 101) {
            deviceToResQuery.addJoin(new Join("CustomGroupMemberRel", "Resource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deviceToResQuery.setCriteria(deviceToResQuery.getCriteria().and(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resID, 0)));
            deviceToResQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        }
        else if (resType == 120 || resType == 121) {
            deviceToResQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedUserToDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            deviceToResQuery.setCriteria(deviceToResQuery.getCriteria().and(new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)resID, 0)));
            deviceToResQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
            deviceToResQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
        }
        final String logMsg = "{0} doc {1} to {2} resource {3} via other user groups";
        final DataObject dObj = SyMUtil.getPersistenceLite().get(deviceToResQuery);
        if (dObj == null || (dObj != null && dObj.isEmpty())) {
            DocMgmt.logger.log(Level.INFO, logMsg, new String[] { String.valueOf(docID), "not associated", this.getResTypeStr(resType), String.valueOf(resID) });
            return false;
        }
        DocMgmt.logger.log(Level.INFO, logMsg, new String[] { String.valueOf(docID), "associated", this.getResTypeStr(resType), String.valueOf(resID) });
        return true;
    }
    
    private boolean isDeviceAssociatedToDocViaAnotherGroup(final Long deviceResourceID, final Long docID) throws DataAccessException {
        return this.isDeviceAssociatedToDocViaAnotherDeviceGroup(deviceResourceID, docID) || this.isResourceAssociatedToDocViaAnotherUserGroup(120, deviceResourceID, docID);
    }
    
    private Long addOrUpdateDocMDrel(final Long docID, final Long deviceResourceID, final long timeStamp) throws DataAccessException {
        final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentManagedDeviceRel", new Criteria(Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"), (Object)docID, 0).and(new Criteria(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), (Object)deviceResourceID, 0)));
        Row docMDrelRow;
        if (dObj.isEmpty()) {
            docMDrelRow = new Row("DocumentManagedDeviceRel");
            docMDrelRow.set("DOC_ID", (Object)docID);
            docMDrelRow.set("MANAGEDDEVICE_ID", (Object)deviceResourceID);
            docMDrelRow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
            dObj.addRow(docMDrelRow);
            SyMUtil.getPersistenceLite().add(dObj);
            DocMgmt.logger.log(Level.INFO, "doc Md rel row added : {0} for {1}", new Object[] { docMDrelRow, timeStamp });
        }
        else {
            docMDrelRow = dObj.getFirstRow("DocumentManagedDeviceRel");
            docMDrelRow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
            dObj.updateRow(docMDrelRow);
            SyMUtil.getPersistenceLite().update(dObj);
            DocMgmt.logger.log(Level.INFO, "doc Md rel row updated : {0} for {1}", new Object[] { docMDrelRow, timeStamp });
        }
        final Long docMDRelID = (Long)docMDrelRow.get("DOC_MD_ID");
        DocMgmt.logger.log(Level.INFO, "doc Md rel id : {0} for {1}", new Object[] { docMDRelID, timeStamp });
        return docMDRelID;
    }
    
    private boolean addOrUpdateDocPolicyResourceRel(final Long customerId, final Long docID, final String docName, final Long resourceId, final String resName, final Long policyId, final String policyName) throws DataAccessException {
        boolean addedOrUpdated = false;
        final SelectQuery docPolicyResRelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentPolicyResourceRel"));
        final Criteria docIdCriteria = new Criteria(Column.getColumn("DocumentPolicyResourceRel", "DOC_ID"), (Object)docID, 0);
        final Criteria resIdCriteria = new Criteria(Column.getColumn("DocumentPolicyResourceRel", "RESOURCE_ID"), (Object)resourceId, 0);
        if (policyId != null) {
            docPolicyResRelQuery.setCriteria(docIdCriteria.and(resIdCriteria));
            docPolicyResRelQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dObj = SyMUtil.getPersistenceLite().get(docPolicyResRelQuery);
            Row docPolicyResRow = null;
            if (dObj.isEmpty()) {
                docPolicyResRow = new Row("DocumentPolicyResourceRel");
                docPolicyResRow.set("DOC_ID", (Object)docID);
                docPolicyResRow.set("RESOURCE_ID", (Object)resourceId);
                docPolicyResRow.set("DEPLOYMENT_POLICY_ID", (Object)policyId);
                docPolicyResRow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                dObj.addRow(docPolicyResRow);
                SyMUtil.getPersistenceLite().add(dObj);
                DocMgmt.logger.log(Level.INFO, "doc res policy rel row added : {0}", new Object[] { docPolicyResRow });
                addedOrUpdated = true;
                final String remarkArgs = docName + "@@@" + resName + "@@@" + StringUtils.join((Object[])new String[] { policyName });
                DocMgmt.getInstance().logEvent(2115, docID, "mdm.content.user.disassociate", remarkArgs, customerId);
            }
            else {
                docPolicyResRow = dObj.getRow("DocumentPolicyResourceRel");
                final Long curPolicyID = (Long)docPolicyResRow.get("DEPLOYMENT_POLICY_ID");
                if (curPolicyID == null || !policyId.equals(curPolicyID)) {
                    addedOrUpdated = true;
                }
                docPolicyResRow.set("DEPLOYMENT_POLICY_ID", (Object)policyId);
                docPolicyResRow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                dObj.updateRow(docPolicyResRow);
                SyMUtil.getPersistenceLite().update(dObj);
                DocMgmt.logger.log(Level.INFO, "doc res policy rel row updated : {0} ", new Object[] { docPolicyResRow });
            }
        }
        return addedOrUpdated;
    }
    
    private void removeDocPolicyResourceRelByDocDelete(final List docIds) throws Exception {
        final DeleteQuery docPolicyResRelQuery = (DeleteQuery)new DeleteQueryImpl("DocumentPolicyResourceRel");
        final Criteria docIdCriteria = new Criteria(Column.getColumn("DocumentPolicyResourceRel", "DOC_ID"), (Object)docIds.toArray(), 8);
        docPolicyResRelQuery.setCriteria(docIdCriteria);
        DirectoryQueryutil.getInstance().executeDeleteQuery(docPolicyResRelQuery, false);
    }
    
    private boolean removeDocPolicyResourceRel(final Long customerId, final Long docId, final String docName, final Long resourceId, final String resName, final Long policyId) throws Exception {
        if (docId == null || resourceId == null) {
            throw new Exception("doc Id and resourceId cannot be null");
        }
        final Criteria docIdCriteria = new Criteria(Column.getColumn("DocumentPolicyResourceRel", "DOC_ID"), (Object)docId, 0);
        final Criteria resIdCriteria = new Criteria(Column.getColumn("DocumentPolicyResourceRel", "RESOURCE_ID"), (Object)resourceId, 0);
        Criteria criteria = docIdCriteria.and(resIdCriteria);
        if (policyId != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("DocumentPolicyResourceRel", "DEPLOYMENT_POLICY_ID"), (Object)policyId, 0));
        }
        final DeleteQuery docPolicyResRelQuery = (DeleteQuery)new DeleteQueryImpl("DocumentPolicyResourceRel");
        docPolicyResRelQuery.setCriteria(criteria);
        final int numOfRowsDeleted = DirectoryQueryutil.getInstance().executeDeleteQuery(docPolicyResRelQuery, false);
        if (numOfRowsDeleted > 0) {
            DocMgmt.logger.log(Level.INFO, "deleting {0} rows from DocumentPolicyResourceRel using criteria {1}", new Object[] { numOfRowsDeleted, criteria.toString() });
            final String remarkArgs = docName + "@@@" + resName + "@@@";
            DocMgmt.getInstance().logEvent(2116, docId, "mdm.content.user.disassociate", remarkArgs, customerId);
            return true;
        }
        return false;
    }
    
    private boolean addOrDeleteDocPolicyResRel(final Long customerId, final Long docId, final String docName, final Long resourceId, final String resName, final Long policyId, final String policyName, final boolean associate) throws Exception {
        if (associate && policyId != null) {
            return this.addOrUpdateDocPolicyResourceRel(customerId, docId, docName, resourceId, resName, policyId, policyName);
        }
        return this.removeDocPolicyResourceRel(customerId, docId, docName, resourceId, resName, policyId);
    }
    
    private boolean addOrUpdateDocMDinfo(final Long customerId, final Long docID, final String docName, final Long deviceResourceID, final String deviceResName, final Long policyId, final String policyName, final boolean associate, final boolean hardRemove, final Long associatedBy) {
        boolean notifyDevice = true;
        final long timeStamp = System.currentTimeMillis();
        DocMgmt.logger.log(Level.INFO, "entered block for add or update docMdInfo timeStamp={0} at {1} : docId={2},deviceResId={3},associate={4},hardRemove={5}", new Object[] { timeStamp, System.currentTimeMillis(), docID, deviceResourceID, associate, hardRemove });
        try {
            final Long docMDid = this.addOrUpdateDocMDrel(docID, deviceResourceID, timeStamp);
            final boolean policyAssociatedOrDisassociatedToDocDevice = this.addOrDeleteDocPolicyResRel(customerId, docID, docName, deviceResourceID, deviceResName, policyId, policyName, associate);
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentManagedDeviceInfo", new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"), (Object)docMDid, 0));
            if (dObj.isEmpty()) {
                final Row docMDinfoRow = new Row("DocumentManagedDeviceInfo");
                docMDinfoRow.set("DOC_MD_ID", (Object)docMDid);
                docMDinfoRow.set("ASSOCIATE", (Object)associate);
                docMDinfoRow.set("AGENT_APPLIED_TIME", (Object)(-1));
                docMDinfoRow.set("ASSOCIATED_AT", (Object)System.currentTimeMillis());
                docMDinfoRow.set("STATUS_ID", (Object)12);
                docMDinfoRow.set("ASSOCIATED_BY", (Object)associatedBy);
                dObj.addRow(docMDinfoRow);
                SyMUtil.getPersistenceLite().add(dObj);
            }
            else {
                final Row docMDinfoRow = dObj.getFirstRow("DocumentManagedDeviceInfo");
                final int curStatusID = (int)docMDinfoRow.get("STATUS_ID");
                final boolean currentAssociateValue = (boolean)docMDinfoRow.get("ASSOCIATE");
                if (!associate && (hardRemove || !getInstance().isDeviceAssociatedToDocViaAnotherGroup(deviceResourceID, docID))) {
                    if (currentAssociateValue) {
                        if (curStatusID == 12) {
                            docMDinfoRow.set("ASSOCIATE", (Object)associate);
                            docMDinfoRow.set("STATUS_ID", (Object)6);
                            notifyDevice = false;
                        }
                        else {
                            docMDinfoRow.set("ASSOCIATE", (Object)associate);
                            docMDinfoRow.set("STATUS_ID", (Object)12);
                        }
                    }
                }
                else if (associate) {
                    if (!currentAssociateValue) {
                        docMDinfoRow.set("ASSOCIATE", (Object)associate);
                        docMDinfoRow.set("ASSOCIATED_AT", (Object)System.currentTimeMillis());
                        docMDinfoRow.set("ASSOCIATED_BY", (Object)associatedBy);
                        if (curStatusID == 6) {
                            docMDinfoRow.set("STATUS_ID", (Object)12);
                        }
                        else if (curStatusID == 12) {
                            final Long agentAppliedTime = (Long)docMDinfoRow.get("AGENT_APPLIED_TIME");
                            if (agentAppliedTime == -1L) {
                                docMDinfoRow.set("STATUS_ID", (Object)12);
                            }
                            else {
                                docMDinfoRow.set("STATUS_ID", (Object)200);
                            }
                        }
                    }
                    else if (curStatusID == 6) {
                        if (policyAssociatedOrDisassociatedToDocDevice) {
                            docMDinfoRow.set("STATUS_ID", (Object)200);
                        }
                        else {
                            notifyDevice = false;
                        }
                    }
                }
                dObj.updateRow(docMDinfoRow);
                SyMUtil.getPersistenceLite().update(dObj);
            }
            final String remarkArgs = docName + "@@@" + deviceResName;
            if (associate) {
                DocMgmt.getInstance().logEvent(2106, docID, "mdm.content.al.associate", remarkArgs, customerId);
            }
            else {
                DocMgmt.getInstance().logEvent(2108, docID, "mdm.content.al.disassociate", remarkArgs, customerId);
            }
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.INFO, ex, () -> "exception occured at saving doc md info timeStamp=" + n);
        }
        DocMgmt.logger.log(Level.INFO, "exiting block for add or update docMdInfo timeStamp={0} at {1} : docId={2},deviceResId={3},associate={4},hardRemove={5}", new Object[] { timeStamp, System.currentTimeMillis(), docID, deviceResourceID, associate, hardRemove });
        return notifyDevice;
    }
    
    private boolean addOrDeleteDocToMDMResRel(final Long docID, final Long resID, final boolean associate, final Long associatedBy) throws DataAccessException {
        final Criteria docToMDMResCriteria = new Criteria(Column.getColumn("DocumentToMDMResource", "DOC_ID"), (Object)docID, 0).and(new Criteria(Column.getColumn("DocumentToMDMResource", "RESOURCE_ID"), (Object)resID, 0));
        if (associate) {
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentToMDMResource", docToMDMResCriteria);
            if (dObj.isEmpty()) {
                final Row docToMDMResRow = new Row("DocumentToMDMResource");
                docToMDMResRow.set("DOC_ID", (Object)docID);
                docToMDMResRow.set("RESOURCE_ID", (Object)resID);
                docToMDMResRow.set("ASSOCIATE", (Object)Boolean.TRUE);
                docToMDMResRow.set("ASSOCIATED_AT", (Object)System.currentTimeMillis());
                docToMDMResRow.set("STATUS_ID", (Object)12);
                docToMDMResRow.set("ASSOCIATED_BY", (Object)associatedBy);
                dObj.addRow(docToMDMResRow);
                SyMUtil.getPersistenceLite().add(dObj);
            }
        }
        else {
            if (this.isResourceAssociatedToDocViaAnotherUserGroup(2, resID, docID)) {
                return false;
            }
            MDMUtil.getPersistenceLite().delete(docToMDMResCriteria);
            ProfileAssociateHandler.getInstance().updateuserProfileSummary();
        }
        return true;
    }
    
    private List saveDocUserAssociation(final Long customerId, final Long docID, final String docName, final Long userResID, final String userName, final Long policyId, final String policyName, final boolean associate, final boolean hardRemove, final Long associatedBy) throws Exception {
        final List toBeNotifiedDeviceList = new ArrayList();
        this.addOrDeleteDocToMDMResRel(docID, userResID, associate, associatedBy);
        this.addOrDeleteDocPolicyResRel(customerId, docID, docName, userResID, userName, policyId, policyName, associate);
        final List<Properties> userDeviceIDs = this.getManagedDevicesListForManagedUsers(userResID);
        final List<Integer> suportedPlatformTypes = this.getSupportedPlatformsList();
        for (int i = 0; userDeviceIDs != null && i < userDeviceIDs.size(); ++i) {
            final Properties userDeviceDetails = userDeviceIDs.get(i);
            final String mdResName = ((Hashtable<K, String>)userDeviceDetails).get("NAME");
            final Long mdResID = ((Hashtable<K, Long>)userDeviceDetails).get("RESOURCE_ID");
            final Integer mdPlatformType = ((Hashtable<K, Integer>)userDeviceDetails).get("PLATFORM_TYPE");
            if (mdPlatformType != null && suportedPlatformTypes.contains(mdPlatformType)) {
                final boolean notifyDevice = this.addOrUpdateDocMDinfo(customerId, docID, docName, mdResID, mdResName, policyId, policyName, associate, hardRemove, associatedBy);
                if (notifyDevice) {
                    toBeNotifiedDeviceList.add(mdResID);
                }
            }
        }
        return toBeNotifiedDeviceList;
    }
    
    private boolean addOrDeleteDocToGroupRel(final Long docID, final Long groupResourceID, final boolean associate, final boolean hardRemove, final Long associatedBy) throws DataAccessException {
        final Criteria docToGroupCriteria = new Criteria(Column.getColumn("DocumentToDeviceGroup", "DOC_ID"), (Object)docID, 0).and(new Criteria(Column.getColumn("DocumentToDeviceGroup", "CUSTOMGROUP_ID"), (Object)groupResourceID, 0));
        if (associate) {
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentToDeviceGroup", docToGroupCriteria);
            if (dObj.isEmpty()) {
                final Row docToDeviceGroupRow = new Row("DocumentToDeviceGroup");
                docToDeviceGroupRow.set("DOC_ID", (Object)docID);
                docToDeviceGroupRow.set("CUSTOMGROUP_ID", (Object)groupResourceID);
                docToDeviceGroupRow.set("STATUS_ID", (Object)12);
                docToDeviceGroupRow.set("ASSOCIATED_AT", (Object)System.currentTimeMillis());
                docToDeviceGroupRow.set("ASSOCIATED_BY", (Object)associatedBy);
                dObj.addRow(docToDeviceGroupRow);
                SyMUtil.getPersistenceLite().add(dObj);
            }
        }
        else {
            if (!hardRemove && this.isResourceAssociatedToDocViaAnotherUserGroup(101, groupResourceID, docID)) {
                return false;
            }
            MDMUtil.getPersistenceLite().delete(docToGroupCriteria);
            ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        }
        return true;
    }
    
    private List saveDocGroupAssociation(final Long customerID, final Long docID, final String docName, final Long groupResourceID, final String groupName, final Long policyId, final String policyName, final boolean associate, final Long associatedBy, final boolean hardRemove) throws Exception {
        final List toBeNotifiedDeviceList = new ArrayList();
        this.addOrDeleteDocToGroupRel(docID, groupResourceID, associate, hardRemove, associatedBy);
        this.addOrDeleteDocPolicyResRel(customerID, docID, docName, groupResourceID, groupName, policyId, policyName, associate);
        final List<Properties> groupMemberDeviceIDs = this.getGroupMembers(groupResourceID);
        final List<Integer> suportedPlatformTypes = this.getSupportedPlatformsList();
        for (int i = 0; groupMemberDeviceIDs != null && i < groupMemberDeviceIDs.size(); ++i) {
            final Properties groupMembers = groupMemberDeviceIDs.get(i);
            final String memberResName = groupMembers.getProperty("NAME");
            final Integer resourceType = ((Hashtable<K, Integer>)groupMembers).get("RESOURCE_TYPE");
            final Long memberResID = ((Hashtable<K, Long>)groupMembers).get("MEMBER_RESOURCE_ID");
            if (resourceType == 101 && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
                this.saveDocGroupAssociation(customerID, docID, docName, memberResID, memberResName, policyId, policyName, associate, associatedBy, hardRemove);
            }
            else if (resourceType == 120 || resourceType == 121) {
                final Integer curPlatformType = ((Hashtable<K, Integer>)groupMembers).get("PLATFORM_TYPE");
                if (curPlatformType != null && suportedPlatformTypes.contains(curPlatformType)) {
                    final boolean notifyDevice = this.addOrUpdateDocMDinfo(customerID, docID, docName, memberResID, memberResName, policyId, policyName, associate, hardRemove, associatedBy);
                    if (notifyDevice) {
                        toBeNotifiedDeviceList.add(memberResID);
                    }
                }
            }
            else if (resourceType == 2) {
                final List curUserNotificationDevList = this.saveDocUserAssociation(customerID, docID, docName, memberResID, memberResName, policyId, policyName, associate, false, associatedBy);
                toBeNotifiedDeviceList.addAll(curUserNotificationDevList);
            }
        }
        return toBeNotifiedDeviceList;
    }
    
    private boolean checkIfUserAuthorized(final Long[] objectIDs, final Long[] customerIDs, final String tableName, final String customerColumnName, final String idColumnName) throws Exception {
        final int objectCount = DBUtil.getRecordCount(tableName, idColumnName, new Criteria(Column.getColumn(tableName, idColumnName), (Object)objectIDs, 8).and(new Criteria(Column.getColumn(tableName, customerColumnName), (Object)customerIDs, 8)));
        if (objectCount == objectIDs.length) {
            return true;
        }
        throw new Exception("un-authorized access");
    }
    
    private String checkIfPolicyAuthorized(final Long policyId, final Long[] customerIds) throws Exception {
        final Join configDeployJoin = new Join("DeploymentPolicy", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Criteria customerCriteria = new Criteria(Column.getColumn("DeploymentConfig", "CUSTOMER_ID"), (Object)customerIds, 8);
        final Criteria policyIdsCriteria = new Criteria(Column.getColumn("DeploymentPolicy", "DEPLOYMENT_POLICY_ID"), (Object)policyId, 0);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentPolicy"));
        sQuery.addJoin(configDeployJoin);
        sQuery.addSelectColumn(Column.getColumn("DeploymentPolicy", "DEPLOYMENT_POLICY_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeploymentPolicy", "DEPLOYMENT_CONFIG_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"));
        sQuery.addSelectColumn(Column.getColumn("DeploymentConfig", "DEPLOYMENT_CONFIG_NAME"));
        sQuery.addSelectColumn(Column.getColumn("DeploymentConfig", "CUSTOMER_ID"));
        sQuery.setCriteria(policyIdsCriteria.and(customerCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(sQuery);
        if (dataObject.isEmpty()) {
            throw new Exception("un-authorized access");
        }
        final Row row = dataObject.getFirstRow("DeploymentPolicy");
        final Long policyIdFromDb = (Long)row.get("DEPLOYMENT_POLICY_ID");
        if (policyId.equals(policyIdFromDb)) {
            final Long configID = (Long)row.get("DEPLOYMENT_CONFIG_ID");
            final Row configRow = dataObject.getRow("DeploymentConfig", new Criteria(Column.getColumn("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"), (Object)configID, 0));
            final String policyName = (String)configRow.get("DEPLOYMENT_CONFIG_NAME");
            return policyName;
        }
        throw new Exception("un-authorized access");
    }
    
    private List parseIntoList(final Object ids) {
        List respList = new ArrayList();
        if (ids != null) {
            if (ids instanceof String) {
                respList = new ArrayList(new HashSet(MDMUtil.getInstance().parseStringForElements((String)ids)));
            }
            else if (ids instanceof ArrayList) {
                respList = new ArrayList(new HashSet((Collection)ids));
            }
            else if (ids instanceof org.json.JSONArray) {
                respList = JSONUtil.getInstance().convertJSONArrayTOList((org.json.JSONArray)ids);
            }
        }
        return respList;
    }
    
    private List<Long> parseRequestIntoList(final org.json.JSONObject docToResourceDetails, final String optkey) {
        final List respList = this.parseIntoList(docToResourceDetails.opt(optkey));
        final List<Long> longList = new ArrayList<Long>();
        for (int j = 0; j < respList.size(); ++j) {
            Long longVal = null;
            try {
                longVal = Long.valueOf(String.valueOf(respList.get(j)));
            }
            catch (final Exception ex) {
                DocMgmt.logger.log(Level.INFO, null, ex);
            }
            if (longVal != null) {
                longList.add(longVal);
            }
        }
        return longList;
    }
    
    public int saveDocDeviceAssociation(final Long[] customerIDs, final org.json.JSONObject docToResourceDetails) throws Exception {
        final long timeStamp = System.currentTimeMillis();
        try {
            DocMgmt.logger.log(Level.INFO, "entered block for saveDocDeviceAssociation. timeStamp : {0}, request details : {1}", new Object[] { timeStamp, docToResourceDetails.toString() });
            final Set<Long> toBeNotifiedDeviceSet = new HashSet<Long>();
            boolean hardRemove = true;
            final boolean associate = docToResourceDetails.getBoolean("ASSOCIATE");
            if (docToResourceDetails.has("HARD_REMOVE_DOC")) {
                hardRemove = docToResourceDetails.getBoolean("HARD_REMOVE_DOC");
            }
            final HashSet<Long> docsIDList = new HashSet<Long>();
            final HashSet<Long> userIDList = new HashSet<Long>();
            final HashSet<Long> groupsIDList = new HashSet<Long>();
            final HashSet<Long> devicesIDList = new HashSet<Long>();
            final org.json.JSONArray docsListStr = docToResourceDetails.getJSONArray("DOC_ID");
            for (int i = 0; i < docsListStr.length(); ++i) {
                final org.json.JSONObject jsonObject = (org.json.JSONObject)docsListStr.get(i);
                final Long docID = Long.valueOf((String)jsonObject.get("DOC_ID"));
                docsIDList.add(docID);
                if (jsonObject.has("MANAGED_USER_ID")) {
                    final List<Long> userId = this.parseRequestIntoList(jsonObject, "MANAGED_USER_ID");
                    userIDList.addAll((Collection<?>)userId);
                }
                if (jsonObject.has("CUSTOMGROUP_ID")) {
                    final List<Long> groupId = this.parseRequestIntoList(jsonObject, "CUSTOMGROUP_ID");
                    groupsIDList.addAll((Collection<?>)groupId);
                }
                if (jsonObject.has("MANAGEDDEVICE_ID")) {
                    final List<Long> deviceId = this.parseRequestIntoList(jsonObject, "MANAGEDDEVICE_ID");
                    devicesIDList.addAll((Collection<?>)deviceId);
                }
            }
            if (associate) {
                try {
                    final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
                    Criteria criteria = new Criteria(Column.getColumn("DocumentDetails", "SIZE"), (Object)0L, 0);
                    criteria = criteria.and(new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)docsIDList.toArray(), 8));
                    selectQuery.setCriteria(criteria);
                    selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID"));
                    final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                    if (!dataObject.isEmpty()) {
                        return 90009;
                    }
                }
                catch (final Exception e) {
                    DocMgmt.logger.log(Level.SEVERE, "Exception while checking upload Error for docs: {0}, Exception:", new Object[] { docsIDList, e });
                }
            }
            final HashMap docs = this.getDocsNames(customerIDs, new ArrayList<Long>(docsIDList));
            final HashMap users = MDMResourceDataProvider.getResourceNames(new ArrayList(userIDList));
            final HashMap groups = MDMResourceDataProvider.getResourceNames(new ArrayList(groupsIDList));
            final HashMap devices = MDMResourceDataProvider.getResourceNames(new ArrayList(devicesIDList));
            if (groupsIDList != null && !groupsIDList.isEmpty() && MDMGroupHandler.getInstance().isInCycle(groupsIDList)) {
                return 90008;
            }
            final List<Long> resourceIDlist = new ArrayList<Long>();
            resourceIDlist.addAll(userIDList);
            resourceIDlist.addAll(groupsIDList);
            resourceIDlist.addAll(devicesIDList);
            this.checkIfUserAuthorized(resourceIDlist.toArray(new Long[resourceIDlist.size()]), customerIDs, "Resource", "CUSTOMER_ID", "RESOURCE_ID");
            this.checkIfUserAuthorized(docsIDList.toArray(new Long[docsIDList.size()]), customerIDs, "DocumentDetails", "CUSTOMER_ID", "DOC_ID");
            final List<Long> docIDList = new ArrayList<Long>();
            for (int j = 0; j < docsListStr.length(); ++j) {
                final org.json.JSONObject jsonObject2 = (org.json.JSONObject)docsListStr.get(j);
                final Long docID2 = Long.valueOf((String)jsonObject2.get("DOC_ID"));
                Long associatedBy = null;
                if (jsonObject2.has("ASSOCIATED_BY")) {
                    associatedBy = Long.valueOf((String)jsonObject2.get("ASSOCIATED_BY"));
                }
                Long policyId = null;
                String policyName = null;
                if (jsonObject2.has("DEPLOYMENT_POLICY_ID")) {
                    policyId = jsonObject2.optLong("DEPLOYMENT_POLICY_ID");
                    if (policyId != null) {
                        policyName = this.checkIfPolicyAuthorized(policyId, customerIDs);
                    }
                }
                if (jsonObject2.has("CUSTOMGROUP_ID")) {
                    final List groupIds = this.parseRequestIntoList(jsonObject2, "CUSTOMGROUP_ID");
                    for (int k = 0; k < groupIds.size(); ++k) {
                        final Long groupResourceID = groupIds.get(k);
                        final List curGroupNotificationDevList = this.saveDocGroupAssociation(customerIDs[0], docID2, docs.get(docID2), groupResourceID, groups.get(groupResourceID), policyId, policyName, associate, associatedBy, hardRemove);
                        toBeNotifiedDeviceSet.addAll(curGroupNotificationDevList);
                    }
                }
                if (jsonObject2.has("MANAGED_USER_ID")) {
                    final List userIds = this.parseRequestIntoList(jsonObject2, "MANAGED_USER_ID");
                    for (int k = 0; k < userIds.size(); ++k) {
                        final Long userResID = userIds.get(k);
                        final List curUserNotificationDevList = this.saveDocUserAssociation(customerIDs[0], docID2, docs.get(docID2), userResID, users.get(userResID), policyId, policyName, associate, true, associatedBy);
                        toBeNotifiedDeviceSet.addAll(curUserNotificationDevList);
                    }
                }
                if (jsonObject2.has("MANAGEDDEVICE_ID")) {
                    final List deviceIds = this.parseRequestIntoList(jsonObject2, "MANAGEDDEVICE_ID");
                    for (int k = 0; k < deviceIds.size(); ++k) {
                        final Long deviceResourceID = deviceIds.get(k);
                        final boolean notifyDevice = this.addOrUpdateDocMDinfo(customerIDs[0], docID2, docs.get(docID2), deviceResourceID, devices.get(deviceResourceID), policyId, policyName, associate, true, associatedBy);
                        if (notifyDevice) {
                            toBeNotifiedDeviceSet.add(deviceResourceID);
                        }
                    }
                }
                docIDList.add(docID2);
            }
            DocSummaryHandler.getInstance().reviseDocSummary(docIDList);
            DocMgmt.logger.log(Level.INFO, "devices to be notified : {0}", toBeNotifiedDeviceSet.toString());
            this.wakeUpDevice(new ArrayList<Long>(toBeNotifiedDeviceSet));
            DocMgmt.logger.log(Level.INFO, "exiting block for saveDocDeviceAssociation. timeStamp : {0}", new Object[] { timeStamp });
            return 1;
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
            return 90001;
        }
    }
    
    public org.json.JSONArray getDocsAssociatedToDevice(final Long deviceResID) throws DataAccessException, JSONException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentManagedDeviceRel"));
        selectQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)true, 0).and(new Criteria(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), (Object)deviceResID, 0)));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATED_BY"));
        final DataObject dObj = SyMUtil.getPersistenceLite().get(selectQuery);
        final Iterator docDeviceIT = dObj.getRows("DocumentManagedDeviceRel");
        org.json.JSONArray docsAssociatedToDevices = new org.json.JSONArray();
        while (docDeviceIT != null && docDeviceIT.hasNext()) {
            final Row docDeviceRow = docDeviceIT.next();
            final Long docID = (Long)docDeviceRow.get("DOC_ID");
            final Long docMDid = (Long)docDeviceRow.get("DOC_MD_ID");
            final Row docDeviceInfoRow = dObj.getRow("DocumentManagedDeviceInfo", new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"), (Object)docMDid, 0));
            final org.json.JSONObject jsObject = new org.json.JSONObject();
            jsObject.put("DOC_ID", (Object)String.valueOf(docID));
            jsObject.put("ASSOCIATED_BY", (Object)String.valueOf(docDeviceInfoRow.get("ASSOCIATED_BY")));
            docsAssociatedToDevices = docsAssociatedToDevices.put((Object)jsObject);
        }
        return docsAssociatedToDevices;
    }
    
    public org.json.JSONArray getDocsAssociatedToUser(final Long userResID) throws DataAccessException, JSONException {
        final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentToMDMResource", new Criteria(Column.getColumn("DocumentToMDMResource", "RESOURCE_ID"), (Object)userResID, 0));
        final Iterator docUserItr = dObj.getRows("DocumentToMDMResource");
        org.json.JSONArray docsAssociatedToUsers = new org.json.JSONArray();
        while (docUserItr != null && docUserItr.hasNext()) {
            final Row docUserRow = docUserItr.next();
            final org.json.JSONObject jsObject = new org.json.JSONObject();
            jsObject.put("DOC_ID", (Object)String.valueOf(docUserRow.get("DOC_ID")));
            jsObject.put("ASSOCIATED_BY", (Object)String.valueOf(docUserRow.get("ASSOCIATED_BY")));
            docsAssociatedToUsers = docsAssociatedToUsers.put((Object)jsObject);
        }
        return docsAssociatedToUsers;
    }
    
    public org.json.JSONArray getDocsAssociatedToDeviceGroup(final Long groupID) throws DataAccessException, JSONException {
        final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentToDeviceGroup", new Criteria(Column.getColumn("DocumentToDeviceGroup", "CUSTOMGROUP_ID"), (Object)groupID, 0));
        final Iterator docGroupIT = dObj.getRows("DocumentToDeviceGroup");
        org.json.JSONArray docsAssociatedToGroups = new org.json.JSONArray();
        while (docGroupIT != null && docGroupIT.hasNext()) {
            final Row docGroupRow = docGroupIT.next();
            final org.json.JSONObject jsObject = new org.json.JSONObject();
            jsObject.put("DOC_ID", (Object)String.valueOf(docGroupRow.get("DOC_ID")));
            jsObject.put("ASSOCIATED_BY", (Object)String.valueOf(docGroupRow.get("ASSOCIATED_BY")));
            docsAssociatedToGroups = docsAssociatedToGroups.put((Object)jsObject);
        }
        return docsAssociatedToGroups;
    }
    
    public List<Long> getDocsList(final Long customerID) throws DataAccessException {
        final DataObject dObj = SyMUtil.getPersistenceLite().get("DocumentDetails", new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1)));
        final Iterator docIdIT = dObj.getRows("DocumentDetails");
        final List<Long> docIDs = new ArrayList<Long>();
        while (docIdIT != null && docIdIT.hasNext()) {
            final Row docRow = docIdIT.next();
            docIDs.add((Long)docRow.get("DOC_ID"));
        }
        return docIDs;
    }
    
    public void cleanUpDocRepo() {
        final Criteria deleteRepoCri = new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 0);
        final Criteria dissAssociatedSuccessFullyCri = new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.FALSE, 0).and(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"), (Object)6, 0));
        final Criteria docUnassociatedCri = new Criteria(Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"), (Object)null, 0);
        try {
            final SelectQuery toBeDeletedDocDeviceQuery = SyMUtil.formSelectQuery("DocumentManagedDeviceRel", dissAssociatedSuccessFullyCri, new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"), Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2))), (Criteria)null);
            final DataObject docDevRelDobj = SyMUtil.getPersistenceLite().get(toBeDeletedDocDeviceQuery);
            if (docDevRelDobj != null && !docDevRelDobj.isEmpty() && docDevRelDobj.containsTable("DocumentManagedDeviceRel")) {
                DocMgmt.logger.log(Level.INFO, "going to delete rows from {0} in the dobj : {1}", new Object[] { "DocumentManagedDeviceRel", docDevRelDobj.toString() });
                docDevRelDobj.deleteRows("DocumentManagedDeviceRel", (Criteria)null);
                SyMUtil.getPersistenceLite().update(docDevRelDobj);
            }
        }
        catch (final DataAccessException ex) {
            DocMgmt.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        try {
            final SelectQuery toBeRemovedDocDeviceQuery = SyMUtil.formSelectQuery("DocumentDetails", deleteRepoCri.and(docUnassociatedCri), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DocumentDetails", "DOC_ID"), Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DocumentDetails", "DocumentManagedDeviceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1))), (Criteria)null);
            final DataObject toBeRemovedDocsDobj = SyMUtil.getPersistenceLite().get(toBeRemovedDocDeviceQuery);
            if (toBeRemovedDocsDobj != null && !toBeRemovedDocsDobj.isEmpty() && toBeRemovedDocsDobj.containsTable("DocumentDetails")) {
                DocMgmt.logger.log(Level.INFO, "going to delete rows from {0} in the dobj : {1}", new Object[] { "DocumentDetails", toBeRemovedDocsDobj.toString() });
                toBeRemovedDocsDobj.deleteRows("DocumentDetails", (Criteria)null);
                SyMUtil.getPersistenceLite().update(toBeRemovedDocsDobj);
            }
        }
        catch (final Exception ex2) {
            DocMgmt.logger.log(Level.SEVERE, null, ex2);
        }
        try {
            final SelectQuery toBeDeletedDocTagRelQuery = SyMUtil.formSelectQuery("DocumentDetails", new Criteria(Column.getColumn("DocumentTagRel", "MODIFIED_TIME"), (Object)Column.getColumn("DocumentDetails", "UPDATED_TIME"), 7), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DocumentTagRel", "DOC_ID"), Column.getColumn("DocumentTagRel", "TAG_ID"), Column.getColumn("DocumentDetails", "DOC_ID"), Column.getColumn("DocumentDetails", "UPDATED_TIME"), Column.getColumn("DocumentTagRel", "MODIFIED_TIME"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2))), (Criteria)null);
            final DataObject docTagRelDobj = SyMUtil.getPersistenceLite().get(toBeDeletedDocTagRelQuery);
            if (docTagRelDobj != null && !docTagRelDobj.isEmpty() && docTagRelDobj.containsTable("DocumentTagRel")) {
                DocMgmt.logger.log(Level.INFO, "going to delete rows from {0} in the dobj : {1}", new Object[] { "DocumentTagRel", docTagRelDobj.toString() });
                docTagRelDobj.deleteRows("DocumentTagRel", (Criteria)null);
                SyMUtil.getPersistenceLite().update(docTagRelDobj);
            }
        }
        catch (final DataAccessException ex) {
            DocMgmt.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        MDMApiFactoryProvider.getDocsCleanupAPI().docCleanUp();
    }
    
    public org.json.JSONObject getDocMgmtMEtrackingInfo() throws Exception {
        final JSONArray metrackResult = MDMUtil.executeSelectQuery(MDMCoreQuery.getInstance().getContentMgmtQuery());
        DocMgmt.logger.log(Level.INFO, "me tracking details query response : {0}", new Object[] { metrackResult.toString() });
        final org.json.JSONObject docTrackingDetails = new org.json.JSONObject();
        for (int i = 0; i < metrackResult.size(); ++i) {
            final JSONObject jsObject = (JSONObject)metrackResult.get(i);
            docTrackingDetails.put("CUSTOMER_ID", jsObject.get((Object)"CUSTOMER_ID"));
            docTrackingDetails.put("TAGS_COUNT", jsObject.get((Object)"TAGS_COUNT"));
            docTrackingDetails.put("MAX_DOC_TAG_COUNT", (jsObject.get((Object)"MAX_DOC_TAG_COUNT") == null) ? Integer.valueOf(0) : jsObject.get((Object)"MAX_DOC_TAG_COUNT"));
            docTrackingDetails.put("MANAGED_DOCS_COUNT", jsObject.get((Object)"MANAGED_DOCS_COUNT"));
            docTrackingDetails.put("UNTAGGED_DOCS_COUNT", jsObject.get((Object)"UNTAGGED_DOCS_COUNT"));
            docTrackingDetails.put("DOC_GROUP_ASSOCIATION_COUNT", jsObject.get((Object)"DOC_GROUP_ASSOCIATION_COUNT"));
            docTrackingDetails.put("DOC_DEVICE_ASSOCIATION_COUNT", jsObject.get((Object)"DOC_DEVICE_ASSOCIATION_COUNT"));
            docTrackingDetails.put("DOC_USER_ASSOCIATION_COUNT", jsObject.get((Object)"DOC_USER_ASSOCIATION_COUNT"));
        }
        DocMgmt.logger.log(Level.INFO, "me tracking details :  method called at : {0}, {1}", new Object[] { DateTimeUtil.dateString(), docTrackingDetails.toString() });
        return docTrackingDetails;
    }
    
    public boolean isContentMgmtForAndroidEnabled(final String udid) {
        try {
            final Row row = DBUtil.getRowFromDB("ManagedDevice", "UDID", (Object)udid);
            if (row != null && (int)row.get("PLATFORM_TYPE") == 2 && (long)row.get("AGENT_VERSION_CODE") > 244L) {
                return true;
            }
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, "Exception in isContentMgmtForAndroidEnabled ", ex);
        }
        return false;
    }
    
    public List<Properties> getGroupMembers(final Long groupId) {
        return this.getGroupMembers(groupId, null);
    }
    
    public List<Properties> getGroupMembers(final Long groupId, Integer resType) {
        ArrayList<Properties> memberIdList = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            query.addJoin(new Join("CustomGroupMemberRel", "Resource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
            if (resType != null) {
                groupIdCri = groupIdCri.and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)resType, 0));
            }
            query.setCriteria(groupIdCri);
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
            query.addSelectColumn(Column.getColumn("Resource", "NAME"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            final DataObject memberDO = SyMUtil.getPersistenceLite().get(query);
            if (!memberDO.isEmpty()) {
                memberIdList = new ArrayList<Properties>();
                final Iterator memberIdItr = memberDO.getRows("CustomGroupMemberRel");
                while (memberIdItr.hasNext()) {
                    final Row memberIdRow = memberIdItr.next();
                    final Long memberId = (Long)memberIdRow.get("MEMBER_RESOURCE_ID");
                    final Row resourceRow = memberDO.getRow("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)memberId, 0));
                    final Properties memberProps = new Properties();
                    ((Hashtable<String, Object>)memberProps).put("NAME", resourceRow.get("NAME"));
                    ((Hashtable<String, Long>)memberProps).put("MEMBER_RESOURCE_ID", memberId);
                    ((Hashtable<String, Object>)memberProps).put("RESOURCE_TYPE", resourceRow.get("RESOURCE_TYPE"));
                    resType = (Integer)resourceRow.get("RESOURCE_TYPE");
                    if (resType != null && (resType == 120 || resType == 121)) {
                        final Row mdRow = memberDO.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)memberId, 0));
                        ((Hashtable<String, Object>)memberProps).put("PLATFORM_TYPE", mdRow.get("PLATFORM_TYPE"));
                    }
                    memberIdList.add(memberProps);
                }
            }
        }
        catch (final Exception e) {
            DocMgmt.logger.log(Level.SEVERE, null, e);
        }
        return memberIdList;
    }
    
    private List<Properties> getManagedDevicesListForManagedUsers(final Long userResID) {
        ArrayList<Properties> deviceIDlistProps = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userResID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            final DataObject dObj = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dObj != null && !dObj.isEmpty()) {
                deviceIDlistProps = new ArrayList<Properties>();
                final Iterator itr = dObj.getRows("ManagedDevice");
                while (itr != null && itr.hasNext()) {
                    final Row mdRow = itr.next();
                    final Long mdResID = (Long)mdRow.get("RESOURCE_ID");
                    final Row resRow = dObj.getRow("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)mdResID, 0));
                    final String resName = (String)resRow.get("NAME");
                    final Integer mdPlatform = (Integer)mdRow.get("PLATFORM_TYPE");
                    final Properties mdProps = new Properties();
                    ((Hashtable<String, String>)mdProps).put("NAME", resName);
                    ((Hashtable<String, Long>)mdProps).put("RESOURCE_ID", mdResID);
                    ((Hashtable<String, Integer>)mdProps).put("PLATFORM_TYPE", mdPlatform);
                    deviceIDlistProps.add(mdProps);
                }
            }
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, "Exception in getting manged devices for user", ex);
        }
        return deviceIDlistProps;
    }
    
    private List<Integer> getSupportedPlatformsList() {
        final ArrayList<Integer> supportedPlatforms = new ArrayList<Integer>();
        supportedPlatforms.add(2);
        supportedPlatforms.add(1);
        supportedPlatforms.add(3);
        return supportedPlatforms;
    }
    
    public JSONObject addDoctoDB(final org.json.JSONObject jsonHeader) throws Exception {
        JSONObject saveDocResponse = new JSONObject();
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        if (!jsonHeader.has("TAG_NAME")) {
            jsonHeader.put("TAG_NAME", (Object)"");
        }
        if (!jsonHeader.has("DESCRIPTION")) {
            jsonHeader.put("DESCRIPTION", (Object)"");
        }
        if (jsonHeader.getBoolean("DOWNLOAD_DOC")) {
            jsonHeader.put("CUSTOMER_ID", (Object)customerId);
            saveDocResponse = DocMgmt.getInstance().saveDocAndDetails(jsonHeader);
        }
        saveDocResponse = JSONUtil.getInstance().convertLongToString(saveDocResponse);
        return saveDocResponse;
    }
    
    public static DocMgmtDataHandler getInstance(final int platformType) {
        if (platformType == 3) {
            return new WindowsDocDataHandler();
        }
        return new DocMgmtDataHandler();
    }
    
    private void wakeUpDevice(final List<Long> resourceIDs) {
        if (resourceIDs.isEmpty()) {
            return;
        }
        final List iOSResourceList = new ArrayList();
        final List androidResourceList = new ArrayList();
        final List windowsResourceList = new ArrayList();
        final List<org.json.JSONObject> deviceProps = ManagedDeviceHandler.getInstance().getPlatformTypeForListOfDeviceIDs(resourceIDs);
        for (int i = 0; i < deviceProps.size(); ++i) {
            try {
                final org.json.JSONObject deviceDetails = deviceProps.get(i);
                final Long resourceId = (Long)deviceDetails.get("RESOURCE_ID");
                final Integer devicePlatform = (Integer)deviceDetails.get("PLATFORM_TYPE");
                if (devicePlatform == 2) {
                    androidResourceList.add(resourceId);
                }
                else if (devicePlatform == 1) {
                    iOSResourceList.add(resourceId);
                }
                else if (devicePlatform == 3) {
                    windowsResourceList.add(resourceId);
                }
                else {
                    DocMgmt.logger.log(Level.WARNING, "cant distribute doc to device of {0} platform type", new Object[] { devicePlatform });
                }
            }
            catch (final Exception ex) {
                DocMgmt.logger.log(Level.SEVERE, null, ex);
            }
        }
        final List collatedDeviceList = new ArrayList();
        if (!iOSResourceList.isEmpty()) {
            collatedDeviceList.addAll(iOSResourceList);
        }
        if (!windowsResourceList.isEmpty()) {
            collatedDeviceList.addAll(windowsResourceList);
        }
        if (!collatedDeviceList.isEmpty()) {
            DeviceCommandRepository.getInstance().addContentSyncCommand(collatedDeviceList, 2);
        }
        if (!androidResourceList.isEmpty()) {
            DeviceCommandRepository.getInstance().addContentSyncCommand(androidResourceList, 1);
        }
        collatedDeviceList.addAll(androidResourceList);
        if (!collatedDeviceList.isEmpty()) {
            try {
                NotificationHandler.getInstance().SendNotification(collatedDeviceList);
            }
            catch (final Exception ex) {
                DocMgmt.logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void handleMissedDocAck() {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("HANDLE_CMS_ACK")) {
            MDMFeatureParamsHandler.updateMDMFeatureParameter("HANDLE_CMS_ACK", false);
            DocMgmt.logger.log(Level.INFO, "starting missed cms ack handling");
            final Column docIDcol = Column.getColumn("DocumentDetails", "DOC_ID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
            selectQuery.addSelectColumn(docIDcol);
            selectQuery.addSortColumn(new SortColumn(docIDcol, true));
            final int batchSize = 100;
            int rangeIterator = 0;
            Range range = new Range(0, batchSize);
            selectQuery.setRange(range);
            Connection connection = null;
            try {
                connection = RelationalAPI.getInstance().getConnection();
                for (JSONArray jsonArray = MDMUtil.executeSelectQuery(connection, selectQuery); jsonArray != null && !jsonArray.isEmpty(); jsonArray = MDMUtil.executeSelectQuery(connection, selectQuery)) {
                    DocMgmt.logger.log(Level.INFO, "updating summary for range : {0}", new Object[] { range.toString() });
                    final List<Long> docIDlist = new ArrayList<Long>();
                    for (int i = 0; i < jsonArray.size(); ++i) {
                        final JSONObject jsObject = (JSONObject)jsonArray.get(i);
                        docIDlist.add((Long)jsObject.get((Object)"DOC_ID"));
                    }
                    DocSummaryHandler.getInstance().reviseDocSummary(docIDlist);
                    rangeIterator += batchSize;
                    range = new Range(rangeIterator, batchSize);
                    selectQuery.setRange(range);
                }
            }
            catch (final Exception ex) {
                DocMgmt.logger.log(Level.SEVERE, null, ex);
                try {
                    if (connection != null) {
                        connection.close();
                    }
                }
                catch (final Exception ex) {
                    DocMgmt.logger.log(Level.SEVERE, null, ex);
                }
            }
            finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                }
                catch (final Exception ex2) {
                    DocMgmt.logger.log(Level.SEVERE, null, ex2);
                }
            }
        }
    }
    
    public String getDocSHA256FileHash(final Long docID) throws Exception {
        String hash = null;
        final Long customerID = (Long)DBUtil.getValueFromDB("DocumentDetails", "DOC_ID", (Object)docID, "CUSTOMER_ID");
        final String docSavePath = DocMgmtConstants.DOC_FILE_DIRECTORY + File.separator + customerID + File.separator + String.valueOf(docID) + ".cms";
        String fullDirectoryPath = DocMgmtConstants.DOC_BASE_DIRECTORY + docSavePath;
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            fullDirectoryPath = DocMgmtConstants.OLD_DOC_BASE_DIRECTORY + docSavePath;
        }
        hash = ChecksumProvider.getInstance().GetSHA256CheckSum(fullDirectoryPath);
        return hash;
    }
    
    static {
        DocMgmtDataHandler.docMgmtDataHandler = null;
        DOC_TABLE_LOCK = new Integer(1);
    }
}
