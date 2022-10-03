package com.me.mdm.server.doc;

import java.util.HashSet;
import com.me.mdm.api.user.UserFacade;
import com.me.mdm.server.customgroup.GroupFacade;
import java.util.Collection;
import com.me.mdm.server.device.DeviceFacade;
import java.util.ArrayList;
import org.json.JSONArray;
import com.me.mdm.api.paging.PagingUtil;
import javax.transaction.SystemException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DocFacade
{
    private Logger logger;
    private DocAPIHandler docAPIHandler;
    
    public DocFacade() {
        this.logger = Logger.getLogger("MDMDocLogger");
        this.docAPIHandler = new DocAPIHandler();
    }
    
    public JSONObject addOrUpdateDoc(final JSONObject apiRequestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        boolean eventSuccess = false;
        boolean isAdd = false;
        try {
            Long docId = APIUtil.getResourceID(apiRequestJSON, "doc_id");
            secLog.put((Object)"DOC_ID", (Object)docId);
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            if (apiRequestJSON.has("msg_body")) {
                final JSONObject bodyJSON = apiRequestJSON.getJSONObject("msg_body");
                bodyJSON.put("customer_id", (Object)customerId);
                bodyJSON.put("user_id", (Object)userId);
                bodyJSON.put("user_name", (Object)userName);
                JSONObject responseJSON = new JSONObject();
                if (docId == -1L) {
                    isAdd = true;
                    responseJSON = this.docAPIHandler.addDoc(bodyJSON);
                    docId = JSONUtil.optLongForUVH(responseJSON, "DOC_ID", Long.valueOf(-1L));
                    secLog.put((Object)"DOC_ID", (Object)docId);
                    bodyJSON.put("doc_id", (Object)docId);
                    bodyJSON.put("STATUS_MSG", (Object)responseJSON.optString("STATUS_MSG", (String)null));
                    responseJSON = this.docAPIHandler.getDoc(bodyJSON);
                    final int docType = responseJSON.optInt("doc_type", -1);
                    final String docExt = DocMgmtDataHandler.getInstance().getDocExtention(docType);
                    secLog.put((Object)"DOC_TYPE", (Object)(docExt.startsWith(".") ? docExt.substring(1) : docExt));
                }
                else {
                    if (!this.docAPIHandler.isDocExists(docId, customerId)) {
                        throw new APIHTTPException("DOC0003", new Object[] { docId });
                    }
                    bodyJSON.put("doc_id", (Object)docId);
                    responseJSON = this.docAPIHandler.updateDoc(bodyJSON);
                }
                eventSuccess = true;
                return responseJSON;
            }
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- addOrUpdateDoc() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- addOrUpdateDoc() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            final String operationName = isAdd ? "ADD_DOC" : "MODIFY_DOC";
            final String remarks = isAdd ? (eventSuccess ? "add-success" : "add-failed") : (eventSuccess ? "update-success" : "update-failed");
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, operationName, secLog);
        }
    }
    
    public JSONObject getDoc(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("user_name", (Object)userName);
            MDMUtil.getUserTransaction().begin();
            final Long docId = APIUtil.getResourceID(apiRequestJSON, "doc_id");
            JSONObject responseJSON;
            if (docId == -1L) {
                final String search = APIUtil.optStringFilter(apiRequestJSON, "search", null);
                final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(apiRequestJSON);
                final String docType = APIUtil.getStringFilter(apiRequestJSON, "doc_type");
                final String tagName = APIUtil.getStringFilter(apiRequestJSON, "tag_name");
                final Long docAddedTime = APIUtil.getLongFilter(apiRequestJSON, "added_time");
                final Integer repositoryType = APIUtil.getIntegerFilter(apiRequestJSON, "repository_type");
                if (!MDMUtil.getInstance().isEmpty(docType)) {
                    requestJSON.put("doc_type", (Object)docType);
                }
                if (!MDMUtil.getInstance().isEmpty(tagName)) {
                    requestJSON.put("tag_name", (Object)tagName);
                }
                if (docAddedTime != -1L) {
                    requestJSON.put("added_time", (Object)docAddedTime);
                }
                if (repositoryType != -1) {
                    requestJSON.put("repository_type", (Object)repositoryType);
                }
                responseJSON = this.docAPIHandler.getAllDocs(requestJSON, search, pagingUtil);
            }
            else {
                if (!this.docAPIHandler.isDocExists(docId, customerId)) {
                    throw new APIHTTPException("DOC0003", new Object[] { docId });
                }
                requestJSON.put("doc_id", (Object)docId);
                responseJSON = this.docAPIHandler.getDoc(requestJSON);
            }
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getDoc() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getDoc() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDocCounts(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Integer docCount = this.docAPIHandler.getDocsCount(customerId);
            final JSONObject responseJSON = new JSONObject();
            if (docCount != null) {
                responseJSON.put("doc_count", (Object)docCount);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getDoc() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getDoc() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteDoc(final JSONObject apiRequestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "delete-failed";
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            JSONObject bodyJSON = new JSONObject();
            final Long docId = APIUtil.getResourceID(apiRequestJSON, "doc_id");
            if (docId == -1L) {
                bodyJSON = apiRequestJSON.getJSONObject("msg_body");
            }
            else {
                bodyJSON.put("docs", (Object)new JSONArray().put((Object)docId));
            }
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_id", (Object)userId);
            bodyJSON.put("user_name", (Object)userName);
            secLog.put((Object)"DOC_IDs", (Object)bodyJSON.optJSONArray("docs"));
            this.docAPIHandler.deleteDoc(bodyJSON);
            remarks = "delete-success";
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- deleteDoc() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- deleteDoc() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DELETE_DOC", secLog);
        }
    }
    
    public void shareOrRemoveDocToDevices(final JSONObject apiRequestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        boolean isAssociate = false;
        boolean isSuccess = false;
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            if (!apiRequestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject bodyJSON = apiRequestJSON.getJSONObject("msg_body");
            final String taskType = String.valueOf(apiRequestJSON.get("task"));
            isAssociate = taskType.equals("association");
            bodyJSON.put("task", (Object)taskType);
            if (bodyJSON.has("policy")) {
                secLog.put((Object)"DOC_POLICY_ID", bodyJSON.get("policy"));
            }
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_id", (Object)userId);
            bodyJSON.put("user_name", (Object)userName);
            final Long docId = APIUtil.getResourceID(apiRequestJSON, "doc_id");
            JSONArray docsJSONArray = new JSONArray();
            if (docId == -1L) {
                docsJSONArray = bodyJSON.getJSONArray("docs");
                this.docAPIHandler.validateDocs(docsJSONArray, customerId);
            }
            else {
                if (!this.docAPIHandler.isDocExists(docId, customerId)) {
                    throw new APIHTTPException("DOC0003", new Object[] { docId });
                }
                docsJSONArray.put((Object)docId);
                bodyJSON.put("docs", (Object)docsJSONArray);
            }
            secLog.put((Object)"DOC_IDs", (Object)docsJSONArray);
            final JSONArray devicesJSONArray = bodyJSON.getJSONArray("devices");
            secLog.put((Object)"DEVICE_IDs", (Object)devicesJSONArray);
            final ArrayList deviceList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(devicesJSONArray);
            new DeviceFacade().validateIfDevicesExists(deviceList, customerId);
            this.docAPIHandler.shareOrRemoveDocToDevices(bodyJSON);
            isSuccess = true;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- shareOrRemoveDocToDevices() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- shareOrRemoveDocToDevices() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            final String operation = isAssociate ? "ASSOCIATE_DOC" : "DISSOCIATE_DOC";
            final String remarks = isAssociate ? (isSuccess ? "associate-success" : "associate-failed") : (isSuccess ? "dissociate-success" : "dissociate-failed");
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, operation, secLog);
        }
    }
    
    public void shareOrRemoveDocToGroups(final JSONObject apiRequestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        boolean isAssociate = false;
        boolean isSuccess = false;
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            if (!apiRequestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject bodyJSON = apiRequestJSON.getJSONObject("msg_body");
            final String taskType = String.valueOf(apiRequestJSON.get("task"));
            isAssociate = taskType.equals("association");
            bodyJSON.put("task", (Object)taskType);
            if (bodyJSON.has("policy")) {
                secLog.put((Object)"DOC_POLICY_ID", bodyJSON.get("policy"));
            }
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_id", (Object)userId);
            bodyJSON.put("user_name", (Object)userName);
            final Long docId = APIUtil.getResourceID(apiRequestJSON, "doc_id");
            JSONArray docsJSONArray = new JSONArray();
            if (docId == -1L) {
                docsJSONArray = bodyJSON.getJSONArray("docs");
                this.docAPIHandler.validateDocs(docsJSONArray, customerId);
            }
            else {
                if (!this.docAPIHandler.isDocExists(docId, customerId)) {
                    throw new APIHTTPException("DOC0003", new Object[] { docId });
                }
                docsJSONArray.put((Object)docId);
                bodyJSON.put("docs", (Object)docsJSONArray);
            }
            secLog.put((Object)"DOC_IDs", (Object)docsJSONArray);
            final JSONArray groupsJSONArray = bodyJSON.getJSONArray("groups");
            secLog.put((Object)"GROUP_IDs", (Object)groupsJSONArray);
            final ArrayList groupList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(groupsJSONArray);
            new GroupFacade().validateGroupsIfExists(groupList, customerId);
            this.docAPIHandler.shareOrRemoveDocToGroups(bodyJSON);
            isSuccess = true;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- shareOrRemoveDocToGroups() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- shareOrRemoveDocToGroups() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            final String operation = isAssociate ? "ASSOCIATE_DOC" : "DISSOCIATE_DOC";
            final String remarks = isAssociate ? (isSuccess ? "associate-success" : "associate-failed") : (isSuccess ? "dissociate-success" : "dissociate-failed");
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, operation, secLog);
        }
    }
    
    public void shareOrRemoveDocToUsers(final JSONObject apiRequestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        boolean isAssociate = false;
        boolean isSuccess = false;
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            if (!apiRequestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject bodyJSON = apiRequestJSON.getJSONObject("msg_body");
            bodyJSON.put("user_id", (Object)userId);
            bodyJSON.put("user_name", (Object)userName);
            bodyJSON.put("customer_id", (Object)customerId);
            final String taskType = String.valueOf(apiRequestJSON.get("task"));
            isAssociate = taskType.equals("association");
            bodyJSON.put("task", (Object)taskType);
            if (bodyJSON.has("policy")) {
                secLog.put((Object)"DOC_POLICY_ID", bodyJSON.get("policy"));
            }
            final Long docId = APIUtil.getResourceID(apiRequestJSON, "doc_id");
            JSONArray docsJSONArray = new JSONArray();
            if (docId == -1L) {
                docsJSONArray = bodyJSON.getJSONArray("docs");
                this.docAPIHandler.validateDocs(docsJSONArray, customerId);
            }
            else {
                if (!this.docAPIHandler.isDocExists(docId, customerId)) {
                    throw new APIHTTPException("DOC0003", new Object[] { docId });
                }
                docsJSONArray.put((Object)docId);
                bodyJSON.put("docs", (Object)docsJSONArray);
            }
            secLog.put((Object)"DOC_IDs", (Object)docsJSONArray);
            final JSONArray usersJSONArray = bodyJSON.getJSONArray("users");
            secLog.put((Object)"USER_IDs", (Object)usersJSONArray);
            final ArrayList userList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(usersJSONArray);
            new UserFacade().validateIfUsersExists(userList, customerId);
            this.docAPIHandler.shareOrRemoveDocToUsers(bodyJSON);
            isSuccess = true;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- shareOrRemoveDocToUsers() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- shareOrRemoveDocToUsers() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            final String operation = isAssociate ? "ASSOCIATE_DOC" : "DISSOCIATE_DOC";
            final String remarks = isAssociate ? (isSuccess ? "associate-success" : "associate-failed") : (isSuccess ? "dissociate-success" : "dissociate-failed");
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, operation, secLog);
        }
    }
    
    public void shareOrRemoveDocToResources(final JSONObject apiRequestJSON) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        boolean isAssociate = false;
        boolean isSuccess = false;
        try {
            if (!apiRequestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final JSONObject bodyJSON = apiRequestJSON.getJSONObject("msg_body");
            bodyJSON.put("customer_id", (Object)customerId);
            bodyJSON.put("user_id", (Object)APIUtil.getUserID(apiRequestJSON));
            bodyJSON.put("user_name", (Object)APIUtil.getUserName(apiRequestJSON));
            final String taskType = String.valueOf(apiRequestJSON.get("task"));
            isAssociate = taskType.equals("association");
            bodyJSON.put("task", (Object)taskType);
            final JSONArray docsJsonArray = bodyJSON.getJSONArray("docs");
            secLog.put((Object)"DOC_IDs", (Object)docsJsonArray);
            this.docAPIHandler.validateDocs(docsJsonArray, customerId);
            if (bodyJSON.has("policy")) {
                secLog.put((Object)"DOC_POLICY_ID", bodyJSON.get("policy"));
            }
            if (bodyJSON.has("users")) {
                final JSONArray usersJsonArray = bodyJSON.getJSONArray("users");
                secLog.put((Object)"USER_IDs", (Object)usersJsonArray);
                new UserFacade().validateIfUsersExists(JSONUtil.getInstance().convertLongJSONArrayTOList(usersJsonArray), customerId);
            }
            if (bodyJSON.has("groups")) {
                final JSONArray groupsJsonArray = bodyJSON.getJSONArray("groups");
                secLog.put((Object)"GROUP_IDs", (Object)groupsJsonArray);
                new GroupFacade().validateIfGroupsExists(JSONUtil.getInstance().convertLongJSONArrayTOList(groupsJsonArray), customerId);
                bodyJSON.put("HARD_REMOVE_DOC", false);
            }
            if (bodyJSON.has("devices")) {
                final JSONArray devicesJsonArray = bodyJSON.getJSONArray("devices");
                secLog.put((Object)"DEVICE_IDs", (Object)devicesJsonArray);
                new DeviceFacade().validateIfDevicesExists(JSONUtil.getInstance().convertLongJSONArrayTOList(devicesJsonArray), customerId);
            }
            this.docAPIHandler.shareOrRemoveDocToResources(bodyJSON);
            isSuccess = true;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- shareOrRemoveDocToResources() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- shareOrRemoveDocToResources() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            final String operation = isAssociate ? "ASSOCIATE_DOC" : "DISSOCIATE_DOC";
            final String remarks = isAssociate ? (isSuccess ? "associate-success" : "associate-failed") : (isSuccess ? "dissociate-success" : "dissociate-failed");
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, operation, secLog);
        }
    }
    
    public JSONObject addTag(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            JSONObject responseJSON = new JSONObject();
            if (apiRequestJSON.has("msg_body")) {
                final JSONObject bodyJSON = apiRequestJSON.getJSONObject("msg_body");
                final String tagName = String.valueOf(bodyJSON.get("tag_name"));
                bodyJSON.put("customer_id", (Object)customerId);
                this.docAPIHandler.addTag(customerId, tagName);
                responseJSON = this.docAPIHandler.getTag(bodyJSON);
                return responseJSON;
            }
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- addTag() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- addTag() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject updateTag(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            JSONObject responseJSON = new JSONObject();
            if (apiRequestJSON.has("msg_body")) {
                JSONObject bodyJSON = apiRequestJSON.getJSONObject("msg_body");
                final String tagName = String.valueOf(bodyJSON.get("tag_name"));
                final Long tagId = APIUtil.getResourceID(apiRequestJSON, "tag_id");
                bodyJSON = new JSONObject();
                bodyJSON.put("customer_id", (Object)customerId);
                bodyJSON.put("tag_id", (Object)tagId);
                this.docAPIHandler.updateTag(tagId, customerId, tagName);
                responseJSON = this.docAPIHandler.getTag(bodyJSON);
                return responseJSON;
            }
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- updateTag() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- updateTag() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteTag(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long tagId = APIUtil.getResourceID(apiRequestJSON, "tag_id");
            this.docAPIHandler.deleteTag(tagId, customerId);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- updateTag() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- updateTag() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getTag(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("user_name", (Object)userName);
            final Long tagId = APIUtil.getResourceID(apiRequestJSON, "tag_id");
            MDMUtil.getUserTransaction().begin();
            if (tagId == -1L) {
                final String tagName = APIUtil.getStringFilter(apiRequestJSON, "tag_name");
                if (!MDMUtil.getInstance().isEmpty(tagName)) {
                    requestJSON.put("tag_name", (Object)tagName);
                    final JSONObject tempJSON = this.docAPIHandler.getTag(requestJSON);
                    responseJSON.put("tags", (Object)new JSONArray().put((Object)tempJSON));
                }
                else {
                    responseJSON = this.docAPIHandler.getTag(requestJSON);
                }
            }
            else {
                requestJSON.put("tag_id", (Object)tagId);
                responseJSON = this.docAPIHandler.getTag(requestJSON);
            }
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getTag() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getTag() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDocToDevicesDistributionStatus(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("user_name", (Object)userName);
            final Long docId = APIUtil.getResourceID(apiRequestJSON, "doc_id");
            if (!this.docAPIHandler.isDocExists(docId, customerId)) {
                throw new APIHTTPException("DOC0003", new Object[] { docId });
            }
            requestJSON.put("doc_id", (Object)docId);
            MDMUtil.getUserTransaction().begin();
            responseJSON = this.docAPIHandler.getDocDevicesDistributionStatus(requestJSON);
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getDocToDevicesDistributionStatus() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getDocToDevicesDistributionStatus() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDocToGroupsDistributionStatus(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("user_name", (Object)userName);
            final Long docId = APIUtil.getResourceID(apiRequestJSON, "doc_id");
            if (!this.docAPIHandler.isDocExists(docId, customerId)) {
                throw new APIHTTPException("DOC0003", new Object[] { docId });
            }
            requestJSON.put("doc_id", (Object)docId);
            MDMUtil.getUserTransaction().begin();
            responseJSON = this.docAPIHandler.getDocGroupDistributionStatus(requestJSON);
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getDocToGroupsDistributionStatus() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getDocToGroupsDistributionStatus() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDocToUsersDistributionStatus(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("user_id", (Object)APIUtil.getUserID(apiRequestJSON));
            requestJSON.put("user_name", (Object)APIUtil.getUserName(apiRequestJSON));
            requestJSON.put("customer_id", (Object)customerId);
            final Long docId = APIUtil.getResourceID(apiRequestJSON, "doc_id");
            if (!this.docAPIHandler.isDocExists(docId, customerId)) {
                throw new APIHTTPException("DOC0003", new Object[] { docId });
            }
            requestJSON.put("doc_id", (Object)docId);
            MDMUtil.getUserTransaction().begin();
            final JSONObject responseJSON = this.docAPIHandler.getDocUserDistributionStatus(requestJSON);
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getDocToGroupsDistributionStatus() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getDocToGroupsDistributionStatus() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDocsForDevice(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("user_name", (Object)userName);
            final Long deviceId = APIUtil.getResourceID(apiRequestJSON, "device_id");
            new DeviceFacade().validateIfDeviceExists(deviceId, customerId);
            requestJSON.put("device_id", (Object)deviceId);
            MDMUtil.getUserTransaction().begin();
            responseJSON = this.docAPIHandler.getDocsForDevices(requestJSON);
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getDocsForDevice() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getDocsForDevice() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDocsForGroup(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final Long userId = APIUtil.getUserID(apiRequestJSON);
            final String userName = APIUtil.getUserName(apiRequestJSON);
            JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("user_name", (Object)userName);
            final Long groupId = APIUtil.getResourceID(apiRequestJSON, "group_id");
            final HashSet groupSet = new HashSet();
            groupSet.add(groupId);
            new GroupFacade().validateIfGroupsExists(groupSet, customerId);
            requestJSON.put("group_id", (Object)groupId);
            MDMUtil.getUserTransaction().begin();
            responseJSON = this.docAPIHandler.getDocsForGroups(requestJSON);
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getDocsForGroup() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getDocsForGroup() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDocsForUser(final JSONObject apiRequestJSON) {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)APIUtil.getUserID(apiRequestJSON));
            requestJSON.put("user_name", (Object)APIUtil.getUserName(apiRequestJSON));
            final Long userId = APIUtil.getResourceID(apiRequestJSON, "user_id");
            final HashSet userSet = new HashSet();
            userSet.add(userId);
            new UserFacade().validateIfUsersExists(userSet, customerId);
            requestJSON.put("user_id", (Object)userId);
            MDMUtil.getUserTransaction().begin();
            final JSONObject responseJSON = this.docAPIHandler.getDocsForUsers(requestJSON);
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getDocsForGroup() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getDocsForGroup() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
