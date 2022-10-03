package com.me.mdm.server.doc;

import java.util.List;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import com.adventnet.i18n.I18N;
import org.apache.tika.Tika;
import java.net.URLDecoder;
import com.me.mdm.files.upload.FileUploadManager;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.paging.PagingUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.Collection;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.io.InputStream;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;

public class DocAPIHandler
{
    protected JSONObject addDoc(final JSONObject requestJSON) throws Exception {
        InputStream inputStream = null;
        try {
            final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
            final JSONObject inputJSON = this.generateInputJSONForDocUpload(requestJSON);
            inputJSON.put("Task".toUpperCase(), (Object)"ADD_DOC");
            final String filePath = String.valueOf(inputJSON.get("DOCUMENT_FILE_PATH"));
            final org.json.simple.JSONObject simpleResponseJSON = DocMgmt.getInstance().saveDocAndDetails(inputJSON);
            if ((int)simpleResponseJSON.get((Object)"STATUS") != 1) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            inputStream = fileAccessAPI.readFile(filePath);
            fileAccessAPI.writeFile(simpleResponseJSON.get((Object)"DOCUMENT_FILE_PATH") + "", inputStream);
            return JSONUtil.getInstance().convertSimpleJSONtoJSON(simpleResponseJSON);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    protected JSONObject updateDoc(final JSONObject requestJSON) throws Exception {
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        final JSONObject inputJSON = this.generateInputJSONForDocUpload(requestJSON);
        inputJSON.put("Task".toUpperCase(), (Object)"MODIFY_DOC");
        inputJSON.put("DOC_ID", (Object)JSONUtil.optLongForUVH(requestJSON, "doc_id", Long.valueOf(-1L)));
        final org.json.simple.JSONObject simpleResponseJSON = DocMgmt.getInstance().saveDocAndDetails(inputJSON);
        if ((int)simpleResponseJSON.get((Object)"STATUS") != 1) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        if (inputJSON.optBoolean("DOWNLOAD_DOC", false)) {
            final InputStream inputStream = fileAccessAPI.readFile(inputJSON.getString("FILE_PATH"));
            fileAccessAPI.writeFile(simpleResponseJSON.get((Object)"DOCUMENT_FILE_PATH") + "", inputStream);
        }
        return JSONUtil.getInstance().convertSimpleJSONtoJSON(simpleResponseJSON);
    }
    
    public JSONObject getDoc(final JSONObject requestJSON) throws Exception {
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final Long docId = JSONUtil.optLongForUVH(requestJSON, "doc_id", Long.valueOf(-1L));
        final Long[] customerIds = { customerId };
        final org.json.simple.JSONObject tempJSON = DocMgmt.getInstance().getDocDetails(customerIds, docId, true);
        final JSONObject responseJSON = JSONUtil.getInstance().convertSimpleJSONtoJSON(tempJSON);
        final long docSize = responseJSON.optLong("SIZE", -1L);
        final int docType = responseJSON.optInt("DOC_TYPE", -1);
        final String mimeType = responseJSON.optString("MIME_TYPE", "");
        final String url = MDMApiFactoryProvider.getDocsApi().getDocViewUrl(docType, docSize, mimeType, (String)tempJSON.get((Object)"LOCATION_PATH"));
        if (!SyMUtil.isStringEmpty(url)) {
            responseJSON.put("file_url", (Object)url);
        }
        responseJSON.put("STATUS_MSG", (Object)requestJSON.optString("STATUS_MSG", (String)null));
        return this.generateResponseJSONForGetRequest(responseJSON);
    }
    
    public void deleteDoc(final JSONObject requestJSON) throws JSONException, DataAccessException {
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final JSONArray docsJSONArray = requestJSON.getJSONArray("docs");
        this.validateDocs(docsJSONArray, customerId);
        final Long[] customerIds = { customerId };
        final org.json.simple.JSONObject deleteResponse = DocMgmt.getInstance().deleteDoc(customerId, customerIds, new JSONObject().put("DOC_ID", (Collection)JSONUtil.getInstance().convertJSONArrayTOList(docsJSONArray)));
        if ((int)deleteResponse.get((Object)"STATUS") != 1) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject generateResponseJSONForGetRequest(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("doc_id", (Object)JSONUtil.optLongForUVH(requestJSON, "DOC_ID", (Long)null));
        responseJSON.put("doc_name", (Object)requestJSON.optString("DOC_NAME", (String)null));
        Long lastModifiedBy = null;
        final String lastModifiedByName = requestJSON.optString("LAST_MODIFIED_BY", (String)null);
        if (!MDMUtil.isStringEmpty(lastModifiedByName)) {
            lastModifiedBy = DMUserHandler.getUserID(lastModifiedByName);
        }
        responseJSON.put("last_modified_by", (Object)lastModifiedBy);
        responseJSON.put("last_modified_by_name", (Object)lastModifiedByName);
        responseJSON.put("size", requestJSON.optInt("SIZE", -1));
        responseJSON.put("added_time", requestJSON.optLong("ADDED_TIME", -1L));
        responseJSON.put("updated_time", requestJSON.optLong("UPDATED_TIME", -1L));
        responseJSON.put("tags", (Object)requestJSON.optJSONArray("TAG_NAME"));
        responseJSON.put("status_msg", (Object)requestJSON.optString("STATUS_MSG", (String)null));
        responseJSON.put("doc_type", requestJSON.optInt("DOC_TYPE", -1));
        responseJSON.put("status", requestJSON.optInt("STATUS", -1));
        responseJSON.put("repository_type", requestJSON.optInt("REPOSITORY_TYPE", -1));
        responseJSON.put("description", (Object)requestJSON.optString("DESCRIPTION", (String)null));
        responseJSON.put("file_url", (Object)requestJSON.optString("file_url", (String)null));
        final JSONObject distributionJSON = new JSONObject();
        distributionJSON.put("group_count", requestJSON.optInt("SHARED_GROUP_COUNT", 0));
        distributionJSON.put("group_list", (Object)requestJSON.optJSONArray("customGrpList"));
        distributionJSON.put("device_count", requestJSON.optInt("SHARED_DEVICE_COUNT", 0));
        distributionJSON.put("device_list", (Object)requestJSON.optJSONArray("deviceList"));
        distributionJSON.put("user_list", (Object)requestJSON.optJSONArray("userList"));
        distributionJSON.put("user_count", requestJSON.optInt("SHARED_USER_COUNT", 0));
        responseJSON.put("distribution_details", (Object)distributionJSON);
        return responseJSON;
    }
    
    public JSONObject getAllDocs(final JSONObject requestJSON, final String search, final PagingUtil pagingUtil) throws Exception {
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final Long docAddedTime = JSONUtil.optLongForUVH(requestJSON, "added_time", Long.valueOf(-1L));
        final Integer docrepository = requestJSON.optInt("repository_type", -1);
        final String docType = requestJSON.optString("doc_type", (String)null);
        final String tagName = requestJSON.optString("tag_name", (String)null);
        JSONObject pagingJSON = new JSONObject();
        final SelectQuery selectQuery = this.getAllDocsQuery();
        final SelectQuery countQuery = this.getBaseDocsQuery();
        countQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID").distinct().count());
        Criteria criteria = new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        if (docAddedTime > 0L) {
            criteria = criteria.and(new Criteria(Column.getColumn("DocumentDetails", "ADDED_TIME"), (Object)docAddedTime, 4));
        }
        if (docrepository != -1) {
            criteria = criteria.and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)docrepository, 0));
        }
        else {
            criteria = criteria.and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)1, 0));
        }
        if (!MDMUtil.getInstance().isEmpty(docType)) {
            int docTypeConstant = 0;
            try {
                docTypeConstant = Integer.valueOf(docType);
            }
            catch (final Exception ex) {
                docTypeConstant = DocMgmtDataHandler.getInstance().getDocType(docType);
            }
            if (docTypeConstant == 0) {
                throw new APIHTTPException("DOC0001", new Object[] { docType });
            }
            criteria = criteria.and(new Criteria(Column.getColumn("DocumentDetails", "DOC_TYPE"), (Object)docTypeConstant, 0));
        }
        if (!MDMUtil.getInstance().isEmpty(tagName)) {
            criteria = criteria.and(new Criteria(Column.getColumn("DocumentTags", "TAG_NAME"), (Object)tagName, 0, false));
        }
        final Criteria filterCriteria = new Criteria(Column.getColumn("DocumentDetails", "DOC_NAME"), (Object)search, 12, false);
        if (search != null) {
            criteria = ((criteria == null) ? filterCriteria : criteria.and(filterCriteria));
            final Criteria countCriteria = (countQuery.getCriteria() == null) ? filterCriteria : countQuery.getCriteria().and(filterCriteria);
            countQuery.setCriteria(countCriteria);
        }
        selectQuery.setCriteria(criteria);
        final int count = DBUtil.getRecordCount(countQuery);
        if (count != 0) {
            pagingJSON = pagingUtil.getPagingJSON(count);
        }
        selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return this.getDocJSONFromAllDocDO(dataObject, count, pagingJSON);
    }
    
    public Integer getDocsCount(final Long customerId) throws Exception {
        final Criteria customerCriteria = new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deleteRepoCriteria = new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1);
        final Column countColumn = new Column("DocumentDetails", "DOC_ID", "COUNT").count();
        countColumn.setColumnAlias("COUNT");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
        selectQuery.addSelectColumn(countColumn);
        selectQuery.setCriteria(customerCriteria.and(deleteRepoCriteria));
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        Integer docsCount = null;
        while (ds.next()) {
            if (ds.getValue("COUNT") != null) {
                docsCount = (Integer)ds.getValue("COUNT");
            }
            else {
                docsCount = 0;
            }
        }
        return docsCount;
    }
    
    private JSONObject getDocJSONFromAllDocDO(final DataObject dataObject, final int count, final JSONObject pagingJSON) throws JSONException, DataAccessException {
        final JSONObject responseJSON = new JSONObject();
        final JSONArray docsJSONArray = new JSONArray();
        if (dataObject != null && !dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("DocumentDetails");
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            responseJSON.put("metadata", (Object)meta);
            if (pagingJSON != null) {
                responseJSON.put("paging", (Object)pagingJSON);
            }
            while (iterator.hasNext()) {
                final JSONObject docJSON = new JSONObject();
                final Row documentDetailsRow = iterator.next();
                final Long docId = (Long)documentDetailsRow.get("DOC_ID");
                final String docName = (String)documentDetailsRow.get("DOC_NAME");
                final String description = (String)documentDetailsRow.get("DESCRIPTION");
                final Long addedTime = (Long)documentDetailsRow.get("ADDED_TIME");
                final Long updatedTime = (Long)documentDetailsRow.get("UPDATED_TIME");
                final Long addedBy = (Long)documentDetailsRow.get("ADDED_BY");
                Row aaaUserRow = dataObject.getRow("AaaUser", new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)addedBy, 0));
                String addedByName = "--";
                if (aaaUserRow != null) {
                    addedByName = (String)aaaUserRow.get("FIRST_NAME");
                }
                final Long updatedBy = (Long)documentDetailsRow.get("LAST_MODIFIED_BY");
                aaaUserRow = dataObject.getRow("AaaUser", new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)updatedBy, 0));
                String updatedByName = "--";
                if (aaaUserRow != null) {
                    updatedByName = (String)aaaUserRow.get("FIRST_NAME");
                }
                final Long size = (Long)documentDetailsRow.get("SIZE");
                final JSONArray tagsJSONArray = new JSONArray();
                final Iterator tagIterator = dataObject.getRows("DocumentTagRel", new Criteria(Column.getColumn("DocumentTagRel", "DOC_ID"), (Object)docId, 0));
                while (tagIterator.hasNext()) {
                    final Row tagRelRow = tagIterator.next();
                    final Long tagId = (Long)tagRelRow.get("TAG_ID");
                    final Row tagRow = dataObject.getRow("DocumentTags", new Criteria(Column.getColumn("DocumentTags", "TAG_ID"), (Object)tagId, 0));
                    final String tagName = (String)tagRow.get("TAG_NAME");
                    final JSONObject tagJSON = new JSONObject();
                    tagJSON.put("tag_id", (Object)tagId);
                    tagJSON.put("tag_name", (Object)tagName);
                    tagsJSONArray.put((Object)tagJSON);
                }
                docJSON.put("tags", (Object)tagsJSONArray);
                final Row documentSummaryRow = dataObject.getRow("DocumentSummary", new Criteria(Column.getColumn("DocumentSummary", "DOC_ID"), (Object)docId, 0));
                final JSONObject distributionJSON = new JSONObject();
                docJSON.put("size", (Object)size);
                docJSON.put("doc_id", (Object)docId);
                docJSON.put("doc_name", (Object)docName);
                docJSON.put("created_by", (Object)addedBy);
                docJSON.put("added_time", (Object)addedTime);
                docJSON.put("description", (Object)description);
                docJSON.put("updated_time", (Object)updatedTime);
                docJSON.put("last_modified_by", (Object)updatedBy);
                docJSON.put("created_by_name", (Object)addedByName);
                docJSON.put("last_modified_by_name", (Object)updatedByName);
                docJSON.put("distribution_details", (Object)distributionJSON);
                docJSON.put("doc_type", documentDetailsRow.get("DOC_TYPE"));
                docJSON.put("mime_type", documentDetailsRow.get("MIME_TYPE"));
                if (documentSummaryRow != null) {
                    distributionJSON.put("user_count", documentSummaryRow.get("USER_COUNT"));
                    distributionJSON.put("group_count", documentSummaryRow.get("GROUP_COUNT"));
                    distributionJSON.put("device_count", documentSummaryRow.get("DEVICE_COUNT"));
                }
                else {
                    distributionJSON.put("user_count", 0);
                    distributionJSON.put("group_count", 0);
                    distributionJSON.put("device_count", 0);
                }
                docsJSONArray.put((Object)docJSON);
            }
        }
        responseJSON.put("docs", (Object)docsJSONArray);
        return responseJSON;
    }
    
    private SelectQuery getAllDocsQuery() {
        final SelectQuery selectQuery = this.getBaseDocsQuery();
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "ADDED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "ADDED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "UPDATED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "LAST_MODIFIED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "MIME_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "SIZE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DESCRIPTION"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentSummary", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentSummary", "DEVICE_COUNT"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentSummary", "GROUP_COUNT"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentSummary", "USER_COUNT"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentTagRel", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentTagRel", "TAG_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentTags", "TAG_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentTags", "TAG_NAME"));
        return selectQuery;
    }
    
    private SelectQuery getBaseDocsQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
        final Criteria aaaUserModifiedByCriteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)Column.getColumn("DocumentDetails", "LAST_MODIFIED_BY"), 0);
        final Criteria aaaUserAddedByCriteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)Column.getColumn("DocumentDetails", "ADDED_BY"), 0);
        final Join aaaUserJoin = new Join("DocumentDetails", "AaaUser", aaaUserAddedByCriteria.or(aaaUserModifiedByCriteria), 2);
        final Join documentSummaryJoin = new Join("DocumentDetails", "DocumentSummary", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1);
        final Join documentTagRelJoin = new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1);
        final Join documentTagsJoin = new Join("DocumentTagRel", "DocumentTags", new String[] { "TAG_ID" }, new String[] { "TAG_ID" }, 1);
        selectQuery.addJoin(aaaUserJoin);
        selectQuery.addJoin(documentSummaryJoin);
        selectQuery.addJoin(documentTagRelJoin);
        selectQuery.addJoin(documentTagsJoin);
        return selectQuery;
    }
    
    public boolean isDocExists(final Long docId, final Long customerId) throws DataAccessException {
        boolean isDocExists = true;
        final Criteria docCriteria = new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)docId, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deleteRepoCriteria = new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1);
        final DataObject dataObject = MDMUtil.getPersistence().get("DocumentDetails", docCriteria.and(customerCriteria).and(deleteRepoCriteria));
        if (dataObject == null || dataObject.isEmpty()) {
            isDocExists = false;
        }
        return isDocExists;
    }
    
    public void validateDocs(final JSONArray docsJSONArray, final Long customerId) throws DataAccessException {
        final ArrayList givenDocList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(docsJSONArray);
        final Criteria docCriteria = new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)givenDocList.toArray(new Long[givenDocList.size()]), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deleteRepoCriteria = new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1);
        final DataObject dataObject = MDMUtil.getPersistence().get("DocumentDetails", docCriteria.and(customerCriteria).and(deleteRepoCriteria));
        if (dataObject == null || dataObject.isEmpty()) {
            throw new APIHTTPException("DOC0003", new Object[] { docsJSONArray.toString() });
        }
        final Iterator iterator = dataObject.getRows("DocumentDetails");
        final ArrayList validDocList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "DOC_ID");
        givenDocList.removeAll(validDocList);
        if (givenDocList.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (final Object s : givenDocList) {
                stringBuilder.append(s);
                stringBuilder.append(", ");
            }
            throw new APIHTTPException("DOC0003", new Object[] { stringBuilder });
        }
    }
    
    private JSONObject generateInputJSONForDocUpload(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        final Long fileID = Long.parseLong(requestJSON.optString("file_id", "-1"));
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        if (fileID != null && !fileID.equals(-1L)) {
            final JSONObject fileDetailsJSON = FileUploadManager.getFileDetails(fileID, customerId);
            final String filePath = fileDetailsJSON.getString("file_path");
            String fileName = fileDetailsJSON.optString("file_name", "--");
            responseJSON.put("DOWNLOAD_DOC", true);
            responseJSON.put("DOCUMENT_FILE_PATH", (Object)filePath);
            responseJSON.put("FILE_PATH", (Object)filePath);
            if (SyMUtil.isStringEmpty(fileName)) {
                fileName = fileAccessAPI.getFileName(filePath);
            }
            fileName = URLDecoder.decode(fileName, "UTF-8");
            responseJSON.put("FILE-NAME", (Object)fileName);
            responseJSON.put("DOC_NAME", (Object)fileName);
            responseJSON.put("CONTENT-LENGTH", (Object)String.valueOf(fileAccessAPI.getFileSize(filePath)));
            responseJSON.put("MIME_TYPE", (Object)new Tika().detect(fileAccessAPI.getInputStream(filePath)));
        }
        else {
            responseJSON.put("DOWNLOAD_DOC", false);
        }
        responseJSON.put("REPOSITORY_TYPE", requestJSON.optInt("repository_type", 1));
        responseJSON.put("DESCRIPTION", (Object)requestJSON.optString("description"));
        responseJSON.put("CUSTOMER_ID", (Object)JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L)));
        if (requestJSON.has("tags")) {
            responseJSON.put("TAG_NAME", (Collection)JSONUtil.getInstance().convertJSONArrayTOList(requestJSON.getJSONArray("tags")));
        }
        else {
            responseJSON.put("TAG_NAME", (Collection)new ArrayList());
        }
        return responseJSON;
    }
    
    public void shareOrRemoveDocToDevices(final JSONObject requestJSON) throws Exception {
        final JSONObject inputJSON = this.generateDistributionJSON(requestJSON);
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final int status = DocMgmtDataHandler.getInstance().saveDocDeviceAssociation(new Long[] { customerId }, inputJSON);
        if (status != 1) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void shareOrRemoveDocToGroups(final JSONObject requestJSON) throws Exception {
        final JSONObject inputJSON = this.generateDistributionJSON(requestJSON);
        inputJSON.put("HARD_REMOVE_DOC", false);
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final int status = DocMgmtDataHandler.getInstance().saveDocDeviceAssociation(new Long[] { customerId }, inputJSON);
        if (status != 1) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void shareOrRemoveDocToUsers(final JSONObject requestJSON) throws Exception {
        final JSONObject inputJSON = this.generateDistributionJSON(requestJSON);
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final int status = DocMgmtDataHandler.getInstance().saveDocDeviceAssociation(new Long[] { customerId }, inputJSON);
        if (status != 1) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void shareOrRemoveDocToResources(final JSONObject requestJSON) throws Exception {
        final JSONObject inputJSON = this.generateDistributionJSON(requestJSON);
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final int status = DocMgmtDataHandler.getInstance().saveDocDeviceAssociation(new Long[] { customerId }, inputJSON);
        if (status != 1) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject checkAndPopulateInputJSON(final JSONArray jsArray, final JSONObject inputJSON, final String key) throws JSONException {
        if (jsArray != null && jsArray.length() > 0) {
            inputJSON.put(key, (Object)jsArray);
        }
        return inputJSON;
    }
    
    private JSONObject generateDistributionJSON(final JSONObject requestJSON) throws JSONException {
        final JSONObject inputJSON = new JSONObject();
        inputJSON.put("DOC_ID", (Object)this.generateDocJSONArray(requestJSON));
        inputJSON.put("ASSOCIATE", String.valueOf(requestJSON.get("task")).equalsIgnoreCase("association"));
        if (requestJSON.has("HARD_REMOVE_DOC")) {
            inputJSON.put("HARD_REMOVE_DOC", requestJSON.get("HARD_REMOVE_DOC"));
        }
        return inputJSON;
    }
    
    private JSONArray generateDocJSONArray(final JSONObject requestJSON) throws JSONException {
        final JSONArray responseJSONArray = new JSONArray();
        final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
        final JSONArray docsJSONArray = requestJSON.getJSONArray("docs");
        for (int i = 0; i < docsJSONArray.length(); ++i) {
            final Long docId = JSONUtil.optLongForUVH(docsJSONArray, i, -1L);
            JSONObject docIdJSON = new JSONObject();
            docIdJSON.put("DOC_ID", (Object)String.valueOf(docId));
            docIdJSON.put("ASSOCIATED_BY", (Object)String.valueOf(userId));
            docIdJSON = this.checkAndPopulateInputJSON(requestJSON.optJSONArray("users"), docIdJSON, "MANAGED_USER_ID");
            docIdJSON = this.checkAndPopulateInputJSON(requestJSON.optJSONArray("groups"), docIdJSON, "CUSTOMGROUP_ID");
            docIdJSON = this.checkAndPopulateInputJSON(requestJSON.optJSONArray("devices"), docIdJSON, "MANAGEDDEVICE_ID");
            if (requestJSON.has("policy")) {
                final String policyId = String.valueOf(requestJSON.get("policy"));
                if (!MDMUtil.isStringEmpty(policyId)) {
                    docIdJSON.put("DEPLOYMENT_POLICY_ID", (Object)policyId);
                }
            }
            responseJSONArray.put((Object)docIdJSON);
        }
        return responseJSONArray;
    }
    
    public void addTag(final Long customeId, final String tagName) throws Exception {
        final JSONObject tagDetails = new JSONObject();
        tagDetails.put("TAG_NAME", (Object)tagName);
        tagDetails.put("CUSTOMER_ID", (Object)customeId);
        final int status = DocMgmtDataHandler.getInstance().addOrUpdateTag(tagDetails);
        if (status == 90005) {
            throw new APIHTTPException("DOC0004", new Object[] { tagName });
        }
        if (status != 1) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updateTag(final Long tagId, final Long customeId, final String tagName) throws Exception {
        final JSONObject tagDetails = new JSONObject();
        tagDetails.put("TAG_ID", (Object)tagId);
        tagDetails.put("TAG_NAME", (Object)tagName);
        tagDetails.put("CUSTOMER_ID", (Object)customeId);
        final int status = DocMgmtDataHandler.getInstance().addOrUpdateTag(tagDetails);
        if (status == 90005) {
            throw new APIHTTPException("DOC0004", new Object[] { tagName });
        }
        if (status != 1) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteTag(final Long tagId, final Long custId) throws Exception {
        final int status = DocMgmtDataHandler.getInstance().deleteTag(tagId, custId);
        if (status == 90010) {
            throw new APIHTTPException("DOC0005", new Object[] { tagId });
        }
        if (status != 1) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getTag(final JSONObject requestJSON) throws DataAccessException, JSONException {
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        final Long tagId = JSONUtil.optLongForUVH(requestJSON, "tag_id", Long.valueOf(-1L));
        final String tagName = requestJSON.optString("tag_name", (String)null);
        final Criteria customerCriteria = new Criteria(Column.getColumn("DocumentTags", "CUSTOMER_ID"), (Object)customerId, 0);
        JSONObject responseJSON = null;
        if (!MDMUtil.isStringEmpty(tagName)) {
            final Criteria tagNameCriteria = new Criteria(Column.getColumn("DocumentTags", "TAG_NAME"), (Object)tagName, 0);
            responseJSON = this.getTag(customerCriteria.and(tagNameCriteria), true);
            if (responseJSON.length() == 0) {
                throw new APIHTTPException("DOC0005", new Object[] { tagName });
            }
        }
        else if (tagId != -1L) {
            final Criteria tagIdCriteria = new Criteria(Column.getColumn("DocumentTags", "TAG_ID"), (Object)tagId, 0);
            responseJSON = this.getTag(customerCriteria.and(tagIdCriteria), true);
            if (responseJSON.length() == 0) {
                throw new APIHTTPException("DOC0005", new Object[] { tagId });
            }
        }
        else {
            responseJSON = this.getTag(customerCriteria, false);
        }
        return responseJSON;
    }
    
    private JSONObject getTag(final Criteria criteria, final boolean isFirstRow) throws DataAccessException, JSONException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentTags"));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("DocumentTags", "TAG_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentTags", "TAG_NAME"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONObject responseJSON = new JSONObject();
        if (!dataObject.isEmpty()) {
            if (isFirstRow) {
                final Long tagId = (Long)dataObject.getFirstRow("DocumentTags").get("TAG_ID");
                final String tagName = (String)dataObject.getFirstRow("DocumentTags").get("TAG_NAME");
                responseJSON.put("tag_name", (Object)tagName);
                responseJSON.put("tag_id", (Object)tagId);
            }
            else {
                final Iterator iterator = dataObject.getRows("DocumentTags");
                final JSONArray tagsJSONArray = new JSONArray();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final String tagName2 = (String)row.get("TAG_NAME");
                    final Long tagId2 = (Long)row.get("TAG_ID");
                    final JSONObject tempJSON = new JSONObject();
                    tempJSON.put("tag_name", (Object)tagName2);
                    tempJSON.put("tag_id", (Object)tagId2);
                    tagsJSONArray.put((Object)tempJSON);
                }
                responseJSON.put("tags", (Object)tagsJSONArray);
            }
        }
        else if (!isFirstRow) {
            responseJSON.put("tags", (Object)new JSONArray());
        }
        return responseJSON;
    }
    
    public JSONObject getDocDevicesDistributionStatus(final JSONObject requestJSON) throws Exception {
        final Long docId = JSONUtil.optLongForUVH(requestJSON, "doc_id", Long.valueOf(-1L));
        final SelectQuery selectQuery = this.getDevicesDistributionQuery();
        final Criteria docCriteria = new Criteria(Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"), (Object)docId, 0);
        final Criteria associatedCriteria = new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0, false);
        final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        selectQuery.setCriteria(docCriteria.and(associatedCriteria).and(managedDeviceCriteria));
        final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
        final JSONObject responseJSON = new JSONObject();
        final JSONArray devicesJSONArray = new JSONArray();
        for (int i = 0; i < resultJSONArray.size(); ++i) {
            final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
            final JSONObject deviceJSON = new JSONObject();
            final Long deviceId = (Long)tempJSON.get((Object)"RESOURCE_ID");
            final String deviceName = (String)tempJSON.get((Object)"NAME");
            final String osVersion = (String)tempJSON.get((Object)"OS_VERSION");
            final int platformType = (int)tempJSON.get((Object)"PLATFORM_TYPE");
            final String platFormName = MDMUtil.getInstance().getPlatformName(platformType);
            final int status = (int)tempJSON.get((Object)"STATUS_ID");
            final String statusMessage = I18N.getMsg(String.valueOf(tempJSON.get((Object)"LABEL")), new Object[0]);
            final Long associatedBy = (Long)tempJSON.get((Object)"ASSOCIATED_BY");
            final String associatedByName = DMUserHandler.getUserNameFromUserID(associatedBy);
            final Long associatedTime = (Long)tempJSON.get((Object)"ASSOCIATED_AT");
            final Long appliedTime = (Long)tempJSON.get((Object)"AGENT_APPLIED_TIME");
            deviceJSON.put("os_version", (Object)osVersion);
            deviceJSON.put("platform_type", platformType);
            deviceJSON.put("platform", (Object)platFormName);
            deviceJSON.put("status", status);
            deviceJSON.put("status_msg", (Object)statusMessage);
            deviceJSON.put("associated_by", (Object)associatedBy);
            deviceJSON.put("associated_by_name", (Object)associatedByName);
            deviceJSON.put("associated_time", (Object)associatedTime);
            deviceJSON.put("applied_time", (Object)appliedTime);
            deviceJSON.put("device_name", (Object)deviceName);
            deviceJSON.put("device_id", (Object)deviceId);
            devicesJSONArray.put((Object)deviceJSON);
        }
        responseJSON.put("devices", (Object)devicesJSONArray);
        responseJSON.put("doc_id", (Object)docId);
        responseJSON.put("device_count", devicesJSONArray.length());
        return responseJSON;
    }
    
    private SelectQuery getDevicesDistributionQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Join mdDeviceInfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join documentManagedDeviceRelJoin = new Join("ManagedDevice", "DocumentManagedDeviceRel", new String[] { "RESOURCE_ID" }, new String[] { "MANAGEDDEVICE_ID" }, 2);
        final Join documentManageDeviceInfoJoin = new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2);
        final Join configStatusDefnJoin = new Join("DocumentManagedDeviceInfo", "ConfigStatusDefn", new String[] { "STATUS_ID" }, new String[] { "STATUS_ID" }, 2);
        final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addJoin(mdDeviceInfoJoin);
        selectQuery.addJoin(documentManagedDeviceRelJoin);
        selectQuery.addJoin(documentManageDeviceInfoJoin);
        selectQuery.addJoin(configStatusDefnJoin);
        selectQuery.addJoin(resourceJoin);
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATED_AT"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "AGENT_APPLIED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "LABEL"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        return selectQuery;
    }
    
    public JSONObject getDocGroupDistributionStatus(final JSONObject requestJSON) throws Exception {
        final Long docId = JSONUtil.optLongForUVH(requestJSON, "doc_id", Long.valueOf(-1L));
        final SelectQuery selectQuery = this.getGroupsDistributionQuery();
        final Criteria docCriteria = new Criteria(Column.getColumn("DocumentToDeviceGroup", "DOC_ID"), (Object)docId, 0);
        selectQuery.setCriteria(docCriteria);
        final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
        final JSONObject responseJSON = new JSONObject();
        final JSONArray groupsJSONArray = new JSONArray();
        int groupsCount = 0;
        for (int i = 0; i < resultJSONArray.size(); ++i) {
            ++groupsCount;
            final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
            final Long groupId = (Long)tempJSON.get((Object)"RESOURCE_ID");
            final String groupName = (String)tempJSON.get((Object)"NAME");
            final int groupType = (int)tempJSON.get((Object)"GROUP_TYPE");
            final int groupMemberCount = CustomGroupingHandler.getGroupMemberCount(groupId);
            final int status = (int)tempJSON.get((Object)"STATUS_ID");
            final String statusMessage = I18N.getMsg((String)tempJSON.get((Object)"LABEL"), new Object[0]);
            final Long associatedBy = (Long)tempJSON.get((Object)"ASSOCIATED_BY");
            final String associatedByName = DMUserHandler.getUserNameFromUserID(associatedBy);
            final Long associatedTime = (Long)tempJSON.get((Object)"ASSOCIATED_AT");
            final JSONObject groupJSON = new JSONObject();
            groupJSON.put("group_id", (Object)groupId);
            groupJSON.put("group_name", (Object)groupName);
            groupJSON.put("group_type", groupType);
            groupJSON.put("member_count", groupMemberCount);
            groupJSON.put("status", status);
            groupJSON.put("status_msg", (Object)statusMessage);
            groupJSON.put("associated_by", (Object)associatedBy);
            groupJSON.put("associated_by_name", (Object)associatedByName);
            groupJSON.put("associated_time", (Object)associatedTime);
            groupsJSONArray.put((Object)groupJSON);
        }
        responseJSON.put("groups", (Object)groupsJSONArray);
        responseJSON.put("doc_id", (Object)docId);
        responseJSON.put("group_count", groupsCount);
        return responseJSON;
    }
    
    public JSONObject getDocUserDistributionStatus(final JSONObject requestJSON) throws Exception {
        final Long docId = JSONUtil.optLongForUVH(requestJSON, "doc_id", Long.valueOf(-1L));
        final SelectQuery selectQuery = this.getUsersDistributionQuery(new ArrayList<Long>(Arrays.asList(docId)));
        final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
        final JSONArray usersJSONArray = new JSONArray();
        for (int i = 0; i < resultJSONArray.size(); ++i) {
            final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
            final JSONObject userJSON = new JSONObject();
            userJSON.put("user_name", tempJSON.get((Object)"NAME"));
            userJSON.put("status", tempJSON.get((Object)"STATUS_ID"));
            userJSON.put("associated_by_name", tempJSON.get((Object)"FIRST_NAME"));
            userJSON.put("user_id", tempJSON.get((Object)"RESOURCE_ID"));
            userJSON.put("associated_by", tempJSON.get((Object)"ASSOCIATED_BY"));
            userJSON.put("associated_time", tempJSON.get((Object)"ASSOCIATED_AT"));
            userJSON.put("status_msg", (Object)I18N.getMsg((String)tempJSON.get((Object)"LABEL"), new Object[0]));
            usersJSONArray.put((Object)userJSON);
        }
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("doc_id", (Object)docId);
        responseJSON.put("users", (Object)usersJSONArray);
        responseJSON.put("user_count", resultJSONArray.size());
        return responseJSON;
    }
    
    private SelectQuery getGroupsDistributionQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        final Join resourceJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join documentToDeviceGroupJoin = new Join("CustomGroup", "DocumentToDeviceGroup", new String[] { "RESOURCE_ID" }, new String[] { "CUSTOMGROUP_ID" }, 2);
        final Join configStatusDefnJoin = new Join("DocumentToDeviceGroup", "ConfigStatusDefn", new String[] { "STATUS_ID" }, new String[] { "STATUS_ID" }, 2);
        selectQuery.addJoin(resourceJoin);
        selectQuery.addJoin(documentToDeviceGroupJoin);
        selectQuery.addJoin(configStatusDefnJoin);
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_CATEGORY"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "CUSTOMGROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "ASSOCIATED_AT"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "ASSOCIATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "LABEL"));
        return selectQuery;
    }
    
    private SelectQuery getUsersDistributionQuery(final List<Long> docIds) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentToMDMResource"));
        selectQuery.addJoin(new Join("DocumentToMDMResource", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("DocumentToMDMResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("DocumentToMDMResource", "ConfigStatusDefn", new String[] { "STATUS_ID" }, new String[] { "STATUS_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0).and(new Criteria(Column.getColumn("DocumentToMDMResource", "DOC_ID"), (Object)docIds.toArray(new Long[docIds.size()]), 8)));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "LABEL"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToMDMResource", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToMDMResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToMDMResource", "ASSOCIATED_AT"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToMDMResource", "ASSOCIATED_BY"));
        return selectQuery;
    }
    
    public JSONObject getDocsForDevices(final JSONObject requestJSON) throws Exception {
        final SelectQuery selectQuery = this.getDocsForDevicesQuery();
        final Long deviceId = JSONUtil.optLongForUVH(requestJSON, "device_id", Long.valueOf(-1L));
        final Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
        final Criteria associatedCriteria = new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0, false);
        final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria docNotDeletedCri = new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1);
        selectQuery.setCriteria(deviceCriteria.and(associatedCriteria).and(managedDeviceCriteria).and(docNotDeletedCri));
        final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
        final JSONObject responseJSON = new JSONObject();
        final JSONArray docsJSONArray = new JSONArray();
        String deviceName = "";
        for (int i = 0; i < resultJSONArray.size(); ++i) {
            final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
            final JSONObject docsJSON = new JSONObject();
            deviceName = (String)tempJSON.get((Object)"NAME");
            final Long associatedBy = (Long)tempJSON.get((Object)"ASSOCIATED_BY");
            docsJSON.put("associated_by", (Object)associatedBy);
            docsJSON.put("size", (Object)tempJSON.get((Object)"SIZE"));
            docsJSON.put("doc_id", (Object)tempJSON.get((Object)"DOC_ID"));
            docsJSON.put("doc_type", (int)tempJSON.get((Object)"DOC_TYPE"));
            docsJSON.put("status", (int)tempJSON.get((Object)"STATUS_ID"));
            docsJSON.put("description", (Object)tempJSON.get((Object)"DESCRIPTION"));
            docsJSON.put("status_msg", (Object)I18N.getMsg((String)tempJSON.get((Object)"LABEL"), new Object[0]));
            docsJSON.put("associated_by_name", (Object)DMUserHandler.getUserNameFromUserID(associatedBy));
            docsJSON.put("associated_time", (Object)tempJSON.get((Object)"ASSOCIATED_AT"));
            docsJSON.put("applied_time", (Object)tempJSON.get((Object)"AGENT_APPLIED_TIME"));
            docsJSONArray.put((Object)docsJSON);
        }
        responseJSON.put("docs", (Object)docsJSONArray);
        responseJSON.put("device_id", (Object)deviceId);
        responseJSON.put("device_name", (Object)deviceName);
        responseJSON.put("doc_count", resultJSONArray.size());
        return responseJSON;
    }
    
    private SelectQuery getDocsForDevicesQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
        final Join documentToManagedDeviceRel = new Join("DocumentDetails", "DocumentManagedDeviceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2);
        final Join documentManagedDeviceInfo = new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2);
        final Join configStatusDefnJoin = new Join("DocumentManagedDeviceInfo", "ConfigStatusDefn", new String[] { "STATUS_ID" }, new String[] { "STATUS_ID" }, 2);
        final Join managedDeviceJoin = new Join("DocumentManagedDeviceRel", "ManagedDevice", new String[] { "MANAGEDDEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addJoin(documentToManagedDeviceRel);
        selectQuery.addJoin(documentManagedDeviceInfo);
        selectQuery.addJoin(configStatusDefnJoin);
        selectQuery.addJoin(managedDeviceJoin);
        selectQuery.addJoin(resourceJoin);
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "SIZE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DESCRIPTION"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATED_AT"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceInfo", "AGENT_APPLIED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "LABEL"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        return selectQuery;
    }
    
    public JSONObject getDocsForGroups(final JSONObject requestJSON) throws Exception {
        final SelectQuery selectQuery = this.getDocsForGroupsQuery();
        final Long groupId = JSONUtil.optLongForUVH(requestJSON, "group_id", Long.valueOf(-1L));
        final Criteria groupCriteria = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupId, 0);
        selectQuery.setCriteria(groupCriteria.and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1)));
        final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
        final JSONObject responseJSON = new JSONObject();
        final JSONArray docsJSONArray = new JSONArray();
        String groupName = "";
        int groupType = 0;
        int groupCategory = 0;
        for (int i = 0; i < resultJSONArray.size(); ++i) {
            final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
            final JSONObject docsJSON = new JSONObject();
            groupName = (String)tempJSON.get((Object)"NAME");
            groupType = (int)tempJSON.get((Object)"GROUP_TYPE");
            groupCategory = (int)tempJSON.get((Object)"GROUP_CATEGORY");
            final Long associatedBy = (Long)tempJSON.get((Object)"ASSOCIATED_BY");
            docsJSON.put("associated_by", (Object)associatedBy);
            docsJSON.put("size", (Object)tempJSON.get((Object)"SIZE"));
            docsJSON.put("doc_id", (Object)tempJSON.get((Object)"DOC_ID"));
            docsJSON.put("doc_type", (int)tempJSON.get((Object)"DOC_TYPE"));
            docsJSON.put("status", (int)tempJSON.get((Object)"STATUS_ID"));
            docsJSON.put("description", (Object)tempJSON.get((Object)"DESCRIPTION"));
            docsJSON.put("status_msg", (Object)I18N.getMsg((String)tempJSON.get((Object)"LABEL"), new Object[0]));
            docsJSON.put("associated_by_name", (Object)DMUserHandler.getUserNameFromUserID(associatedBy));
            docsJSON.put("associated_time", (Object)tempJSON.get((Object)"ASSOCIATED_AT"));
            docsJSONArray.put((Object)docsJSON);
        }
        responseJSON.put("group_id", (Object)groupId);
        responseJSON.put("docs", (Object)docsJSONArray);
        responseJSON.put("group_type", groupType);
        responseJSON.put("group_name", (Object)groupName);
        responseJSON.put("group_category", groupCategory);
        responseJSON.put("doc_count", resultJSONArray.size());
        return responseJSON;
    }
    
    private SelectQuery getDocsForGroupsQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
        final Join documentToDeviceGroupJoin = new Join("DocumentDetails", "DocumentToDeviceGroup", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2);
        final Join customGroupJoin = new Join("DocumentToDeviceGroup", "CustomGroup", new String[] { "CUSTOMGROUP_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join resourceJoin = new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join configStatusDefnJoin = new Join("DocumentToDeviceGroup", "ConfigStatusDefn", new String[] { "STATUS_ID" }, new String[] { "STATUS_ID" }, 2);
        selectQuery.addJoin(documentToDeviceGroupJoin);
        selectQuery.addJoin(customGroupJoin);
        selectQuery.addJoin(resourceJoin);
        selectQuery.addJoin(configStatusDefnJoin);
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "SIZE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DESCRIPTION"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "CUSTOMGROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "ASSOCIATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToDeviceGroup", "ASSOCIATED_AT"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "LABEL"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "GROUP_CATEGORY"));
        return selectQuery;
    }
    
    public JSONObject getDocsForUsers(final JSONObject requestJSON) throws Exception {
        final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
        final SelectQuery selectQuery = this.getDocsForUsersQuery(new ArrayList<Long>(Arrays.asList(userId)));
        final org.json.simple.JSONArray resultJSONArray = MDMUtil.executeSelectQuery(selectQuery);
        final JSONArray docsJSONArray = new JSONArray();
        String userName = "";
        for (int i = 0; i < resultJSONArray.size(); ++i) {
            final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
            userName = (String)tempJSON.get((Object)"NAME");
            final JSONObject docsJSON = new JSONObject();
            docsJSON.put("user_name", (Object)userName);
            docsJSON.put("size", tempJSON.get((Object)"SIZE"));
            docsJSON.put("doc_id", tempJSON.get((Object)"DOC_ID"));
            docsJSON.put("doc_name", tempJSON.get((Object)"DOC_NAME"));
            docsJSON.put("doc_type", tempJSON.get((Object)"DOC_TYPE"));
            docsJSON.put("status", tempJSON.get((Object)"STATUS_ID"));
            docsJSON.put("mime_type", tempJSON.get((Object)"MIME_TYPE"));
            docsJSON.put("associated_by_name", tempJSON.get((Object)"FIRST_NAME"));
            docsJSON.put("description", tempJSON.get((Object)"DESCRIPTION"));
            docsJSON.put("associated_by", tempJSON.get((Object)"ASSOCIATED_BY"));
            docsJSON.put("associated_time", tempJSON.get((Object)"ASSOCIATED_AT"));
            docsJSON.put("status_msg", (Object)I18N.getMsg((String)tempJSON.get((Object)"LABEL"), new Object[0]));
            docsJSONArray.put((Object)docsJSON);
        }
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("user_id", (Object)userId);
        responseJSON.put("user_name", (Object)userName);
        responseJSON.put("docs", (Object)docsJSONArray);
        responseJSON.put("doc_count", resultJSONArray.size());
        return responseJSON;
    }
    
    private SelectQuery getDocsForUsersQuery(final ArrayList<Long> userIds) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
        selectQuery.addJoin(new Join("DocumentDetails", "DocumentToMDMResource", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
        selectQuery.addJoin(new Join("DocumentToMDMResource", "ConfigStatusDefn", new String[] { "STATUS_ID" }, new String[] { "STATUS_ID" }, 2));
        selectQuery.addJoin(new Join("DocumentToMDMResource", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DocumentToMDMResource", "RESOURCE_ID"), (Object)userIds.toArray(new Long[userIds.size()]), 8).and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1)));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "SIZE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "LABEL"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "MIME_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DESCRIPTION"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigStatusDefn", "STATUS_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToMDMResource", "ASSOCIATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentToMDMResource", "ASSOCIATED_AT"));
        return selectQuery;
    }
}
