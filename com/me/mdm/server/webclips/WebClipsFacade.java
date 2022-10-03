package com.me.mdm.server.webclips;

import java.util.Collection;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashMap;
import com.me.mdm.files.FileFacade;
import java.io.File;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import org.json.JSONArray;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class WebClipsFacade
{
    private Logger logger;
    private static WebClipsPolicyHandler webClipsPolicyHandler;
    private static final long WEBCLIPSIMAGESIZE = 1048576L;
    private static final String WEBCLIP_FILE_UPLOAD = "WEBCLIPS_FILE_UPLOAD";
    private static final String WEBCLIP_ICON_REMOVED = "IS_ICON_REMOVED";
    
    public WebClipsFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject addWebClipsPolicy(final JSONObject request) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(request);
            final JSONObject requestJSON = this.convertToServerJSON(request.getJSONObject("msg_body"));
            final String webClipName = requestJSON.getString("WEBCLIP_NAME");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipPolicies"));
            selectQuery.addSelectColumn(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"));
            final Criteria webClipLabelCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_NAME"), (Object)webClipName, 0, false);
            final Criteria customerIdCriteria = new Criteria(new Column("WebClipPolicies", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(webClipLabelCriteria.and(customerIdCriteria));
            DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                throw new APIHTTPException("COM0010", new Object[0]);
            }
            dataObject = (DataObject)new WritableDataObject();
            requestJSON.put("CUSTOMER_ID", (Object)customerId);
            final Row webClipPolicyRow = this.addWebClipPolicies(dataObject, requestJSON);
            final String webClipSrc = requestJSON.optString("WEBCLIPS_FILE_UPLOAD");
            if (!MDMStringUtils.isEmpty(webClipSrc)) {
                dataObject.updateRow(webClipPolicyRow);
                MDMUtil.getPersistenceLite().update(dataObject);
            }
            return this.getWebClipsPolicy(webClipPolicyRow);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in adding webclip", e2);
            throw e2;
        }
    }
    
    public Row addWebClipPolicies(final DataObject dataObject, final JSONObject requestJSON) throws Exception {
        final Row webClipPolicyRow = new Row("WebClipPolicies");
        final List excludeList = new ArrayList();
        excludeList.add("WEBCLIP_POLICY_ID");
        excludeList.add("ICON_FILE_NAME");
        this.addWebClipPolicyRow(webClipPolicyRow, requestJSON, excludeList);
        final String webClipSrc = requestJSON.optString("WEBCLIPS_FILE_UPLOAD");
        if (!MDMStringUtils.isEmpty(webClipSrc)) {
            this.validateWebClips(webClipSrc);
        }
        dataObject.addRow(webClipPolicyRow);
        MDMUtil.getPersistenceLite().add(dataObject);
        final Long webClipPolicyId = (Long)webClipPolicyRow.get("WEBCLIP_POLICY_ID");
        if (!MDMStringUtils.isEmpty(webClipSrc)) {
            final String iconFilePath = this.handleIconFileForWebClip(webClipSrc, webClipPolicyId);
            webClipPolicyRow.set("ICON_FILE_NAME", (Object)iconFilePath);
        }
        return webClipPolicyRow;
    }
    
    public JSONObject getWebClipsPolicy(final JSONObject request) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(request);
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final Long webclipPolicyId = APIUtil.getResourceID(request, "webclip_id");
            final boolean inProfile = APIUtil.getBooleanFilter(request, "in_profile");
            final Criteria customerIdCriteria = new Criteria(new Column("WebClipPolicies", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria webClipPolicyCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"), (Object)webclipPolicyId, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipPolicies"));
            selectQuery.addSelectColumn(new Column("WebClipPolicies", "*"));
            selectQuery.setCriteria(customerIdCriteria.and(webClipPolicyCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final Row webClipRow = dataObject.getRow("WebClipPolicies");
            final JSONObject webClipPolicyObject = this.getWebClipsPolicy(webClipRow);
            if (inProfile) {
                webClipPolicyObject.put("profile_ids", (Object)WebClipsFacade.webClipsPolicyHandler.getProfilesForWebClipID(webclipPolicyId));
            }
            return webClipPolicyObject;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getting webClip policy", e2);
            throw e2;
        }
    }
    
    public JSONObject getWebClipPolicies(final JSONObject request) throws Exception {
        final JSONObject webClipsPolicies = new JSONObject();
        final JSONArray webClipsArray = new JSONArray();
        try {
            final Long customerId = APIUtil.getCustomerID(request);
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
            final String search = APIUtil.getStringFilter(request, "search");
            final boolean inProfile = APIUtil.getBooleanFilter(request, "in_profile");
            final String webclipName = APIUtil.getStringFilter(request, "webclip_name");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipPolicies"));
            Criteria customerIdCriteria = new Criteria(new Column("WebClipPolicies", "CUSTOMER_ID"), (Object)customerId, 0);
            if (!MDMStringUtils.isEmpty(search)) {
                final Criteria searchCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_NAME"), (Object)search, 12, false).or(new Criteria(new Column("WebClipPolicies", "WEBCLIP_URL"), (Object)search, 12, false));
                customerIdCriteria = customerIdCriteria.and(searchCriteria);
            }
            else if (!MDMStringUtils.isEmpty(webclipName)) {
                final Criteria searchCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_NAME"), (Object)webclipName, 12, false);
                customerIdCriteria = customerIdCriteria.and(searchCriteria);
            }
            selectQuery.setCriteria(customerIdCriteria);
            final int totalCount = MDMDBUtil.getCount(selectQuery, "WebClipPolicies", "WEBCLIP_POLICY_ID");
            selectQuery.addSelectColumn(new Column("WebClipPolicies", "*"));
            final Range range = new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit());
            selectQuery.setRange(range);
            final SortColumn sortColumn = new SortColumn(new Column("WebClipPolicies", "WEBCLIP_NAME"), true);
            selectQuery.addSortColumn(sortColumn);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator webClipRows = dataObject.getRows("WebClipPolicies");
                JSONObject webClipInProfileList = new JSONObject();
                if (inProfile) {
                    final Iterator checkingWebClipRows = dataObject.getRows("WebClipPolicies");
                    webClipInProfileList = WebClipsFacade.webClipsPolicyHandler.isWebClipInProfile(MDMDBUtil.getColumnValuesAsList(checkingWebClipRows, "WEBCLIP_POLICY_ID"));
                }
                while (webClipRows.hasNext()) {
                    final Row webClipsRow = webClipRows.next();
                    final JSONObject webClipObject = this.getWebClipsPolicy(webClipsRow);
                    if (inProfile) {
                        final Long webClipPolicyId = (Long)webClipsRow.get("WEBCLIP_POLICY_ID");
                        if (webClipInProfileList.has(String.valueOf(webClipPolicyId))) {
                            webClipObject.put("profile_ids", webClipInProfileList.get(String.valueOf(webClipPolicyId)));
                        }
                    }
                    webClipsArray.put((Object)webClipObject);
                }
            }
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", totalCount);
            webClipsPolicies.put("metadata", (Object)meta);
            webClipsPolicies.put("webclippolicies", (Object)webClipsArray);
            if (totalCount != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(totalCount);
                if (pagingJSON != null) {
                    webClipsPolicies.put("paging", (Object)pagingJSON);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in webclips get all", e);
            throw e;
        }
        return webClipsPolicies;
    }
    
    public JSONObject modifyWebClipPolicy(final JSONObject request) throws Exception {
        try {
            if (!APIUtil.getNewInstance().hasUserAllDeviceScopeGroup(request, false)) {
                throw new APIHTTPException("COM0013", new Object[0]);
            }
            final Long customerId = APIUtil.getCustomerID(request);
            final JSONObject requestJSON = this.convertToServerJSON(request.getJSONObject("msg_body"));
            final String webClipName = requestJSON.getString("WEBCLIP_NAME");
            final Long webClipPolicyId = APIUtil.getResourceID(request, "webclip_id");
            final Criteria customerIdCriteria = new Criteria(new Column("WebClipPolicies", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria webClipNameCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_NAME"), (Object)webClipName, 0, false);
            final Criteria webClipIdCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"), (Object)webClipPolicyId, 0);
            final Criteria webClipCriteria = webClipIdCriteria.or(webClipNameCriteria);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipPolicies"));
            selectQuery.addSelectColumn(new Column("WebClipPolicies", "*"));
            selectQuery.setCriteria(webClipCriteria.and(customerIdCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final Row webClipIdRow = dataObject.getRow("WebClipPolicies", webClipIdCriteria);
            if (webClipIdRow == null) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final String existingWebClipName = (String)webClipIdRow.get("WEBCLIP_NAME");
            if (!existingWebClipName.equals(webClipName)) {
                final Row otherWebClipRow = dataObject.getRow("WebClipPolicies", webClipNameCriteria);
                if (otherWebClipRow != null) {
                    final Long otherWebClipID = (Long)otherWebClipRow.get("WEBCLIP_POLICY_ID");
                    this.logger.log(Level.SEVERE, "Another webclips exists{0}", new Object[] { otherWebClipID });
                    throw new APIHTTPException("COM0010", new Object[0]);
                }
            }
            this.modifyWebClipRow(webClipIdRow, requestJSON);
            dataObject.updateRow(webClipIdRow);
            MDMUtil.getPersistenceLite().update(dataObject);
            final boolean reDistribute = requestJSON.optBoolean("REDISTRIBUTE", false);
            final Long userId = APIUtil.getUserID(request);
            final List webClipList = new ArrayList();
            final List reDistributeList = new ArrayList();
            webClipList.add(webClipPolicyId);
            if (reDistribute) {
                reDistributeList.add(webClipPolicyId);
            }
            WebClipsFacade.webClipsPolicyHandler.handleModifyWebClips(reDistributeList, webClipList, customerId, userId);
            return this.getWebClipsPolicy(webClipIdRow);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in modifying the webCLips", ex2);
            throw ex2;
        }
    }
    
    public void deleteWebClipPolicy(final JSONObject request) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(request);
            final Long userId = APIUtil.getUserID(request);
            final Long webClipPolicyId = APIUtil.getResourceID(request, "webclip_id");
            final JSONArray webClipArray = new JSONArray();
            webClipArray.put((Object)webClipPolicyId);
            this.deleteWebClips(webClipArray, customerId, userId);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private boolean validateWebClipsExist(final List webClipIds, final Long customerId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipPolicies"));
        selectQuery.addSelectColumn(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"));
        final Criteria webClipCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"), (Object)webClipIds.toArray(), 8);
        final Criteria customerIdCriteria = new Criteria(new Column("WebClipPolicies", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(webClipCriteria.and(customerIdCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final int size = dataObject.size("WebClipPolicies");
            if (webClipIds.size() == size) {
                return true;
            }
        }
        return false;
    }
    
    private JSONObject getWebClipsPolicy(final Row webClipRow) {
        final JSONObject webClipJSON = new JSONObject();
        final List columns = webClipRow.getColumns();
        for (int i = 0; i < columns.size(); ++i) {
            final String policyColumnName = columns.get(i);
            final Object policyColumnValue = webClipRow.get(policyColumnName);
            if (policyColumnName.equals("ICON_FILE_NAME")) {
                final String webClipFileURL = (String)policyColumnValue;
                if (!MDMStringUtils.isEmpty(webClipFileURL)) {
                    webClipJSON.put(policyColumnName.toLowerCase(), this.getWebClipUrl(webClipFileURL));
                }
            }
            else if (policyColumnValue != null) {
                webClipJSON.put(policyColumnName.toLowerCase(), policyColumnValue);
            }
        }
        return webClipJSON;
    }
    
    public Object getWebClipUrl(String webClipFileURL) {
        if (!MDMStringUtils.isEmpty(webClipFileURL)) {
            if (!webClipFileURL.startsWith("/mdm/webclips/")) {
                webClipFileURL = "/mdm/webclips/" + webClipFileURL;
            }
            return this.constructFileUrl(webClipFileURL);
        }
        return webClipFileURL;
    }
    
    private void addWebClipPolicyRow(final Row webClipPoliciesRow, final JSONObject webClipJSON, final List excludeColumnList) throws Exception {
        final List columns = webClipPoliciesRow.getColumns();
        for (int i = 0; i < columns.size(); ++i) {
            final String policyColumnName = columns.get(i);
            if (!excludeColumnList.contains(policyColumnName) && webClipJSON.has(policyColumnName)) {
                webClipPoliciesRow.set(policyColumnName, webClipJSON.get(policyColumnName));
            }
        }
    }
    
    protected String handleIconFileForWebClip(final String webclipsSrc, final Long iconId) throws Exception {
        try {
            this.validateWebClips(webclipsSrc);
            final String fileName = ApiFactoryProvider.getFileAccessAPI().getFileName(webclipsSrc);
            final boolean fileUploaded = ProfileUtil.getInstance().uploadProfileImageFile(webclipsSrc, ProfileUtil.getProfileWebClipsFolderPath(iconId), fileName);
            if (fileUploaded) {
                String path = ProfileUtil.getProfileWebClipsRelativeFolderPath(iconId) + File.separator + fileName;
                path = path.replaceAll("\\\\", "/");
                return path;
            }
            throw new Exception("Unable to upload WebClips file - " + fileName);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in uploading image for webclips", e2);
            throw e2;
        }
        finally {
            FileFacade.getInstance().deleteFile(webclipsSrc);
        }
    }
    
    private void validateWebClips(final String webclipsSrc) throws Exception {
        if (!APIUtil.isAllowedImageMimeTypeForFile(webclipsSrc)) {
            throw new APIHTTPException("PAY0005", new Object[0]);
        }
        if (ApiFactoryProvider.getFileAccessAPI().getFileSize(webclipsSrc) > 1048576L) {
            throw new APIHTTPException("PAY0001", new Object[0]);
        }
    }
    
    private Object constructFileUrl(Object columnValue) {
        final HashMap hm = new HashMap();
        hm.put("path", columnValue);
        hm.put("IS_SERVER", true);
        hm.put("IS_AUTHTOKEN", false);
        columnValue = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        return columnValue;
    }
    
    public JSONObject modifyBulkWebClips(final JSONObject request) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(request);
            final Long userId = APIUtil.getUserID(request);
            final JSONObject requestJSON = request.getJSONObject("msg_body");
            final JSONArray webClipsArray = requestJSON.getJSONArray("WebClipPolicies".toLowerCase());
            return this.updateBulkWebClips(webClipsArray, customerId, userId);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public void deleteBulkWebClips(final JSONObject request) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(request);
            final Long userId = APIUtil.getUserID(request);
            final JSONObject responseJSON = request.getJSONObject("msg_body");
            final JSONArray webClipPolicyIds = responseJSON.getJSONArray("webclip_policies_ids");
            this.deleteWebClips(webClipPolicyIds, customerId, userId);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw ex2;
        }
    }
    
    private void deleteWebClips(final JSONArray array, final Long customerId, final Long userId) throws Exception {
        final List webClipPolicyIds = JSONUtil.getInstance().convertLongJSONArrayTOList(array);
        if (!this.validateWebClipsExist(webClipPolicyIds, customerId)) {
            throw new APIHTTPException("COM0008", new Object[0]);
        }
        final Long loginId = DMUserHandler.getLoginIdForUserId(userId);
        if (!RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginId, false)) {
            throw new APIHTTPException("COM0013", new Object[0]);
        }
        try {
            WebClipsFacade.webClipsPolicyHandler.handleDeleteWebClips(webClipPolicyIds, customerId, userId);
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("WebClipPolicies");
            final Criteria customerIdCriteria = new Criteria(new Column("WebClipPolicies", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria webClipIdCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"), (Object)webClipPolicyIds.toArray(), 8);
            deleteQuery.setCriteria(customerIdCriteria.and(webClipIdCriteria));
            MDMUtil.getPersistenceLite().delete(deleteQuery);
            WebClipsFacade.webClipsPolicyHandler.deleteWebClip(webClipPolicyIds);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in delete webclips", ex);
            throw ex;
        }
    }
    
    private JSONObject updateBulkWebClips(final JSONArray jsonArray, final Long customerId, final Long userId) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final HashMap webClipDetails = this.getWebClipIdsFromArray(jsonArray);
        final List<Long> webClipIds = webClipDetails.get("WEBCLIP_POLICY_ID");
        final JSONArray failureArray = new JSONArray();
        if (!this.validateWebClipsExist(webClipIds, customerId)) {
            throw new APIHTTPException("COM0008", new Object[0]);
        }
        final Long loginId = DMUserHandler.getLoginIdForUserId(userId);
        if (!RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginId, false)) {
            throw new APIHTTPException("COM0013", new Object[0]);
        }
        final List<String> webClipNames = webClipDetails.get("WEBCLIP_NAME");
        try {
            final List<Long> redistributeList = new ArrayList<Long>();
            final List<Long> successList = new ArrayList<Long>();
            final Criteria customerIdCriteria = new Criteria(new Column("WebClipPolicies", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria webClipIdCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"), (Object)webClipIds.toArray(), 8);
            final Criteria webClipLabelCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_NAME"), (Object)webClipNames.toArray(), 8, false);
            final Criteria webClipCriteria = webClipIdCriteria.or(webClipLabelCriteria);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipPolicies"));
            selectQuery.addSelectColumn(new Column("WebClipPolicies", "*"));
            selectQuery.setCriteria(webClipCriteria.and(customerIdCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                for (int i = 0; i < jsonArray.length(); ++i) {
                    final JSONObject webClipJSON = this.convertToServerJSON(jsonArray.getJSONObject(i));
                    final Long webClipPolicyId = webClipJSON.getLong("WEBCLIP_POLICY_ID");
                    final String newWebClipName = webClipJSON.getString("WEBCLIP_NAME");
                    final Row webClipRow = dataObject.getRow("WebClipPolicies", new Criteria(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"), (Object)webClipPolicyId, 0));
                    final String webClipName = (String)webClipRow.get("WEBCLIP_NAME");
                    if (!webClipName.equalsIgnoreCase(newWebClipName)) {
                        final Criteria existingWebClipCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"), (Object)webClipPolicyId, 1).and(new Column("WebClipPolicies", "WEBCLIP_NAME"), (Object)newWebClipName, 0, false);
                        final Row existingLabel = dataObject.getRow("WebClipPolicies", existingWebClipCriteria);
                        if (existingLabel != null) {
                            final JSONObject failureObject = new JSONObject();
                            failureObject.put("failureReason", new APIHTTPException("COM0010", new Object[0]).toJSONObject().get("error_code"));
                            failureObject.put("webclip_policy_id", (Object)webClipPolicyId);
                            failureArray.put((Object)failureObject);
                            continue;
                        }
                    }
                    try {
                        this.modifyWebClipRow(webClipRow, webClipJSON);
                        dataObject.updateRow(webClipRow);
                        final boolean reDistribute = webClipJSON.optBoolean("REDISTRIBUTE", false);
                        if (reDistribute) {
                            redistributeList.add(webClipPolicyId);
                        }
                        successList.add(webClipPolicyId);
                    }
                    catch (final APIHTTPException ex) {
                        final JSONObject failureObject2 = new JSONObject();
                        failureObject2.put("failureReason", ex.toJSONObject().get("error_code"));
                        failureObject2.put("webclip_policy_id", (Object)webClipPolicyId);
                    }
                    catch (final Exception ex2) {
                        final JSONObject failureObject2 = new JSONObject();
                        failureObject2.put("failureReason", new APIHTTPException("COM0004", new Object[0]).toJSONObject().get("error_code"));
                        failureObject2.put("webclip_policy_id", (Object)webClipPolicyId);
                    }
                }
                MDMUtil.getPersistenceLite().update(dataObject);
                WebClipsFacade.webClipsPolicyHandler.handleModifyWebClips(redistributeList, successList, customerId, userId);
                responseJSON.put("successList", (Collection)successList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in adding webclips", e);
            throw e;
        }
        return responseJSON;
    }
    
    public Row modifyWebClipRow(final Row webClipRow, final JSONObject webClipJSON) throws Exception {
        final List excludeList = new ArrayList();
        excludeList.add("CUSTOMER_ID");
        excludeList.add("WEBCLIP_POLICY_ID");
        excludeList.add("ICON_FILE_NAME");
        this.addWebClipPolicyRow(webClipRow, webClipJSON, excludeList);
        final Long webClipPolicyId = (Long)webClipRow.get("WEBCLIP_POLICY_ID");
        final String webClipSrc = webClipJSON.optString("WEBCLIPS_FILE_UPLOAD");
        final boolean iconRemoved = webClipJSON.optBoolean("IS_ICON_REMOVED", false);
        if (iconRemoved) {
            webClipRow.set("ICON_FILE_NAME", (Object)"");
        }
        else if (!MDMStringUtils.isEmpty(webClipSrc)) {
            final String iconFilePath = this.handleIconFileForWebClip(webClipSrc, webClipPolicyId);
            webClipRow.set("ICON_FILE_NAME", (Object)iconFilePath);
        }
        return webClipRow;
    }
    
    private HashMap getWebClipIdsFromArray(final JSONArray array) {
        final HashMap webClipDetails = new HashMap();
        final List<Long> webClipIds = new ArrayList<Long>();
        final List<String> webClipLabel = new ArrayList<String>();
        for (int i = 0; i < array.length(); ++i) {
            final JSONObject webClipsObject = array.getJSONObject(i);
            webClipIds.add(webClipsObject.getLong("WEBCLIP_POLICY_ID".toLowerCase()));
            webClipLabel.add(webClipsObject.getString("WEBCLIP_NAME".toLowerCase()));
        }
        webClipDetails.put("WEBCLIP_POLICY_ID", webClipIds);
        webClipDetails.put("WEBCLIP_NAME", webClipLabel);
        return webClipDetails;
    }
    
    private JSONObject convertToServerJSON(final JSONObject requestJSON) {
        final JSONObject modifiedJSON = JSONUtil.getInstance().changeJSONKeyCase(requestJSON, 1);
        if (modifiedJSON.has("WEBCLIPS_FILE_UPLOAD")) {
            final String webclipsSrc = String.valueOf(modifiedJSON.get("WEBCLIPS_FILE_UPLOAD"));
            if (!MDMStringUtils.isEmpty(webclipsSrc)) {
                final String filePath = FileFacade.getInstance().getLocalPathForFileID(Long.valueOf(webclipsSrc));
                modifiedJSON.put("WEBCLIPS_FILE_UPLOAD", (Object)filePath);
            }
        }
        return modifiedJSON;
    }
    
    static {
        WebClipsFacade.webClipsPolicyHandler = new WebClipsPolicyHandler();
    }
}
