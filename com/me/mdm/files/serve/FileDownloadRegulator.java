package com.me.mdm.files.serve;

import com.me.mdm.app.AppDeviceAuthorizer;
import com.me.mdm.doc.DocDeviceAuthorizer;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.admin.AbstractAuthenticationKeyHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.factory.FileDownloadRegulatorAPI;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import com.me.mdm.agent.servlets.DeviceRequestServlet;

class FileDownloadRegulator extends DeviceRequestServlet
{
    private static void getFileDeviceList(final HashMap<String, List<AsyncContextAuthorizer>> innerValue, final List<String> udidList, final List<Long> fileIDList, final List<Long> loginIdList) {
        final Iterator innerItr = innerValue.keySet().iterator();
        while (innerItr != null && innerItr.hasNext()) {
            final String innerKey = innerItr.next();
            final String[] splitStr = innerKey.split("\\|");
            if (splitStr[0].equalsIgnoreCase("APIKEY")) {
                loginIdList.add(Long.valueOf(splitStr[1]));
                fileIDList.add(Long.valueOf(splitStr[2]));
            }
            else {
                fileIDList.add(Long.valueOf(splitStr[0]));
                udidList.add(splitStr[1]);
            }
        }
    }
    
    private static List<Long> makeUniqueList(final List<Long> list) {
        return new ArrayList<Long>(new HashSet<Long>(list));
    }
    
    private static void authorize(final HashMap<String, HashMap<String, List<AsyncContextAuthorizer>>> authRrequests) throws Exception {
        final List<String> udidList = new ArrayList<String>();
        final List<Long> fileIDList = new ArrayList<Long>();
        final List<Long> loginIdList = new ArrayList<Long>();
        final Iterator itr = authRrequests.keySet().iterator();
        while (itr != null && itr.hasNext()) {
            final String module = itr.next();
            final FileDownloadRegulatorAPI fdra = getFileDownloadRegulatorModuleImpl(module);
            final HashMap<String, List<AsyncContextAuthorizer>> innerValue = authRrequests.get(module);
            getFileDeviceList(innerValue, udidList, fileIDList, loginIdList);
            if (!udidList.isEmpty()) {
                final DataObject dobj = MDMUtil.getPersistenceLite().get(fdra.getAgentAuthorizationQuery((List)udidList, (List)fileIDList));
                if (dobj != null && !dobj.isEmpty()) {
                    final String s = module;
                    switch (s) {
                        case "docrepository": {
                            final Iterator authorizerItr = dobj.getRows("ManagedDevice");
                            while (authorizerItr != null && authorizerItr.hasNext()) {
                                final Row managedDeviceRow = authorizerItr.next();
                                final String authUDID = (String)managedDeviceRow.get("UDID");
                                final Iterator authDevItr = dobj.getRows("DocumentManagedDeviceRel", new Criteria(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), managedDeviceRow.get("RESOURCE_ID"), 0));
                                while (authDevItr != null && authDevItr.hasNext()) {
                                    final Row docMDrelRow = authDevItr.next();
                                    final Long docID = (Long)docMDrelRow.get("DOC_ID");
                                    final String authInnerKey = docID + "|" + authUDID;
                                    if (innerValue.containsKey(authInnerKey)) {
                                        final List<AsyncContextAuthorizer> mapContexts = innerValue.get(authInnerKey);
                                        for (final AsyncContextAuthorizer asyncContextAuthorizer : mapContexts) {
                                            asyncContextAuthorizer.authorize(authUDID, String.valueOf(docID));
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case "apprepository": {
                            final Iterator iterator = dobj.getRows("ManagedDevice");
                            while (iterator != null && iterator.hasNext()) {
                                final Row managedDevice = iterator.next();
                                final String udid = (String)managedDevice.get("UDID");
                                final Long resourceId = (Long)managedDevice.get("RESOURCE_ID");
                                final Iterator authDevItr2 = dobj.getRows("MdAppToCollection", new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0), new Join("MdAppToCollection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                                while (authDevItr2 != null && authDevItr2.hasNext()) {
                                    final Row appMDRElRow = authDevItr2.next();
                                    final Long appID = (Long)appMDRElRow.get("APP_ID");
                                    final String authInnerKey2 = appID + "|" + udid;
                                    if (innerValue.containsKey(authInnerKey2)) {
                                        final List<AsyncContextAuthorizer> mapContexts2 = innerValue.get(authInnerKey2);
                                        for (final AsyncContextAuthorizer asyncContextAuthorizer2 : mapContexts2) {
                                            asyncContextAuthorizer2.authorize(udid, String.valueOf(appID));
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            if (!loginIdList.isEmpty()) {
                final String s2 = module;
                switch (s2) {
                    case "docrepository": {
                        DataObject dobj2;
                        try {
                            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                            dobj2 = SyMUtil.getPersistenceLite().get(fdra.getLoginUserAuthorizationQuery((List)fileIDList));
                        }
                        catch (final Exception ex) {
                            throw ex;
                        }
                        finally {
                            CustomerInfoThreadLocal.setSkipCustomerFilter("false");
                        }
                        if (dobj2 == null || dobj2.isEmpty() || !dobj2.containsTable("AaaLogin") || !dobj2.containsTable("DocumentDetails")) {
                            continue;
                        }
                        if (CustomerInfoUtil.getInstance().isMSP()) {
                            final Iterator iterator = innerValue.keySet().iterator();
                            while (iterator != null && iterator.hasNext()) {
                                final String key = iterator.next();
                                final String[] splitStr = key.split("\\|");
                                Long docID2 = null;
                                if (splitStr[0].equalsIgnoreCase("APIKEY")) {
                                    docID2 = Long.valueOf(splitStr[2]);
                                }
                                else {
                                    docID2 = Long.valueOf(splitStr[0]);
                                }
                                final List<Long> custIDs = makeUniqueList(DBUtil.getColumnValuesAsList(dobj2.getRows("DocumentDetails", new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)docID2, 0)), "CUSTOMER_ID"));
                                final List<Long> userIDs = makeUniqueList(DBUtil.getColumnValuesAsList(dobj2.getRows("LoginUserCustomerMapping", new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)custIDs.toArray(), 8)), "DC_USER_ID"));
                                final List<Long> uniqueApprovedLoginIDs = makeUniqueList(DBUtil.getColumnValuesAsList(dobj2.getRows("AaaLogin", new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userIDs.toArray(), 8)), "LOGIN_ID"));
                                final List<AsyncContextAuthorizer> mapContexts = innerValue.get(key);
                                for (final AsyncContextAuthorizer asyncContextAuthorizer : mapContexts) {
                                    final String fileId = asyncContextAuthorizer.fileIDhint;
                                    final Row docRow = dobj2.getRow("DocumentDetails", new Criteria(new Column("DocumentDetails", "DOC_ID"), (Object)fileId, 0));
                                    final String mime = (String)docRow.get("MIME_TYPE");
                                    asyncContextAuthorizer.authorize(uniqueApprovedLoginIDs, mime);
                                }
                            }
                            continue;
                        }
                        final List<Long> uniqueApprovedLoginIDs2 = makeUniqueList(DBUtil.getColumnValuesAsList(dobj2.getRows("AaaLogin"), "LOGIN_ID"));
                        final Iterator iterator2 = innerValue.keySet().iterator();
                        while (iterator2 != null && iterator2.hasNext()) {
                            final String key2 = iterator2.next();
                            final List<AsyncContextAuthorizer> mapContexts3 = innerValue.get(key2);
                            for (final AsyncContextAuthorizer asyncContextAuthorizer3 : mapContexts3) {
                                final String fileId2 = asyncContextAuthorizer3.fileIDhint;
                                final Row docRow2 = dobj2.getRow("DocumentDetails", new Criteria(new Column("DocumentDetails", "DOC_ID"), (Object)fileId2, 0));
                                final String mime2 = (String)docRow2.get("MIME_TYPE");
                                asyncContextAuthorizer3.authorize(uniqueApprovedLoginIDs2, mime2);
                            }
                        }
                        continue;
                    }
                    case "apprepository": {
                        DataObject dobj2;
                        try {
                            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                            dobj2 = SyMUtil.getPersistenceLite().get(fdra.getLoginUserAuthorizationQuery((List)fileIDList));
                        }
                        catch (final Exception ex) {
                            throw ex;
                        }
                        finally {
                            CustomerInfoThreadLocal.setSkipCustomerFilter("false");
                        }
                        if (CustomerInfoUtil.getInstance().isMSP()) {
                            final Iterator iterator = innerValue.keySet().iterator();
                            while (iterator != null && iterator.hasNext()) {
                                final String key = iterator.next();
                                final String[] splitStr = key.split("\\|");
                                Long appID2 = null;
                                if (splitStr[0].equalsIgnoreCase("APIKEY")) {
                                    appID2 = Long.valueOf(splitStr[2]);
                                }
                                else {
                                    appID2 = Long.valueOf(splitStr[0]);
                                }
                                final List<Long> custIDs = makeUniqueList(DBUtil.getColumnValuesAsList(dobj2.getRows("MdAppDetails", new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appID2, 0)), "CUSTOMER_ID"));
                                final List<Long> userIDs = makeUniqueList(DBUtil.getColumnValuesAsList(dobj2.getRows("LoginUserCustomerMapping", new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)custIDs.toArray(), 8)), "DC_USER_ID"));
                                final List<Long> uniqueApprovedLoginIDs = makeUniqueList(DBUtil.getColumnValuesAsList(dobj2.getRows("AaaLogin", new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userIDs.toArray(), 8)), "LOGIN_ID"));
                                final List<AsyncContextAuthorizer> mapContexts = innerValue.get(key);
                                for (final AsyncContextAuthorizer asyncContextAuthorizer : mapContexts) {
                                    asyncContextAuthorizer.authorize(uniqueApprovedLoginIDs, String.valueOf(appID2));
                                }
                            }
                            continue;
                        }
                        final List<Long> uniqueApprovedLoginIDs2 = makeUniqueList(DBUtil.getColumnValuesAsList(dobj2.getRows("AaaLogin"), "LOGIN_ID"));
                        final Iterator iterator2 = innerValue.keySet().iterator();
                        while (iterator2 != null && iterator2.hasNext()) {
                            final String key2 = iterator2.next();
                            final List<AsyncContextAuthorizer> mapContexts3 = innerValue.get(key2);
                            for (final AsyncContextAuthorizer asyncContextAuthorizer3 : mapContexts3) {
                                asyncContextAuthorizer3.authorize(uniqueApprovedLoginIDs2, null);
                            }
                        }
                        continue;
                    }
                }
            }
        }
    }
    
    static void authorizeAndClose(final List<AsyncContextAuthorizer> asyncContextsBatch) throws Exception {
        final HashMap<String, HashMap<String, List<AsyncContextAuthorizer>>> authRrequests = new HashMap<String, HashMap<String, List<AsyncContextAuthorizer>>>();
        for (final AsyncContextAuthorizer asyncContextAuthorizer : asyncContextsBatch) {
            try {
                final Long loginId = asyncContextAuthorizer.loginId;
                final String module = asyncContextAuthorizer.module;
                final String fileID = asyncContextAuthorizer.fileIDhint;
                final String udidOfDevice = asyncContextAuthorizer.deviceUDID;
                HashMap<String, List<AsyncContextAuthorizer>> value = null;
                if (authRrequests.containsKey(module)) {
                    value = authRrequests.get(module);
                }
                else {
                    value = new HashMap<String, List<AsyncContextAuthorizer>>();
                }
                String innerKey;
                if (SyMUtil.isStringValid(udidOfDevice)) {
                    innerKey = fileID + "|" + udidOfDevice;
                }
                else {
                    if (loginId == null) {
                        asyncContextAuthorizer.complete();
                        continue;
                    }
                    innerKey = "APIKEY|" + loginId + "|" + fileID;
                }
                List<AsyncContextAuthorizer> innerValue = null;
                if (value.containsKey(innerKey)) {
                    innerValue = value.get(innerKey);
                }
                else {
                    innerValue = new ArrayList<AsyncContextAuthorizer>();
                }
                innerValue.add(asyncContextAuthorizer);
                value.put(innerKey, innerValue);
                authRrequests.put(module, value);
            }
            catch (final Exception ex) {
                SyMLogger.log("FileServletLog", Level.WARNING, (String)null, (Throwable)ex);
            }
        }
        authorize(authRrequests);
        close(asyncContextsBatch);
    }
    
    private static void authenticate(final HashMap<String, List<AsyncContextAuthorizer>> authErequests) throws DataAccessException {
        final List<String> udidList = new ArrayList<String>();
        final List<String> tokenList = new ArrayList<String>();
        final Iterator itr = authErequests.keySet().iterator();
        while (itr != null && itr.hasNext()) {
            final String udidKey = itr.next();
            final String[] splitStr = udidKey.split("\\|");
            tokenList.add(splitStr[0]);
            udidList.add(splitStr[1]);
        }
        if (!udidList.isEmpty() && !tokenList.isEmpty()) {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceToken"));
            query.addJoin(new Join("DeviceToken", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.setCriteria(new Criteria(Column.getColumn("DeviceToken", "TOKEN_ENCRYPTED"), (Object)tokenList.toArray(new String[tokenList.size()]), 8).and(new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udidList.toArray(new String[udidList.size()]), 8)));
            query.addSelectColumn(Column.getColumn("DeviceToken", "TOKEN_ENCRYPTED"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("DeviceToken", "ENROLLMENT_REQUEST_ID"));
            query.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
            query.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
            final DataObject dobj = MDMUtil.getPersistenceLite().get(query);
            if (dobj != null && !dobj.isEmpty()) {
                final Iterator iterator = dobj.getRows("DeviceToken");
                while (iterator != null && iterator.hasNext()) {
                    final Row deviceTokenRow = iterator.next();
                    final Row enrollmentRequestTODeviceRow = dobj.getRow("EnrollmentRequestToDevice", new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), deviceTokenRow.get("ENROLLMENT_REQUEST_ID"), 0));
                    final Row managedDeviceRow = dobj.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), enrollmentRequestTODeviceRow.get("MANAGED_DEVICE_ID"), 0));
                    final String deviceToken = (String)deviceTokenRow.get("TOKEN_ENCRYPTED");
                    final String deviceUDID = (String)managedDeviceRow.get("UDID");
                    final String key = deviceToken + "|" + deviceUDID;
                    final List<AsyncContextAuthorizer> authenticateRequests = authErequests.get(key);
                    if (authenticateRequests != null && !authenticateRequests.isEmpty()) {
                        for (final AsyncContextAuthorizer asyncContextAuthorizer : authenticateRequests) {
                            asyncContextAuthorizer.authenticate(deviceUDID, deviceToken);
                        }
                    }
                }
            }
        }
    }
    
    static List<AsyncContextAuthorizer> authenticate(final List<AsyncContextAuthorizer> asyncContextsBatch) throws DataAccessException {
        final HashMap<String, List<AsyncContextAuthorizer>> authErequests = new HashMap<String, List<AsyncContextAuthorizer>>();
        for (final AsyncContextAuthorizer asyncContextAuthorizer : asyncContextsBatch) {
            try {
                final String deviceToken = asyncContextAuthorizer.deviceToken;
                final String udidOfDevice = asyncContextAuthorizer.deviceUDID;
                final String fileId = asyncContextAuthorizer.fileIDhint;
                String key;
                if (!SyMUtil.isStringEmpty(deviceToken) && !SyMUtil.isStringEmpty(udidOfDevice)) {
                    key = deviceToken + "|" + udidOfDevice;
                }
                else {
                    key = "APIKEY|" + fileId;
                }
                List<AsyncContextAuthorizer> value = null;
                if (authErequests.containsKey(key)) {
                    value = authErequests.get(key);
                }
                if (value == null) {
                    value = new ArrayList<AsyncContextAuthorizer>();
                }
                value.add(asyncContextAuthorizer);
                authErequests.put(key, value);
            }
            catch (final Exception ex) {
                SyMLogger.log("FileServletLog", Level.WARNING, (String)null, (Throwable)ex);
            }
        }
        authenticate(authErequests);
        final List<AsyncContextAuthorizer> authenticatedAsyncContext = new ArrayList<AsyncContextAuthorizer>();
        final Iterator itr = authErequests.values().iterator();
        while (itr != null && itr.hasNext()) {
            final List<AsyncContextAuthorizer> authContext = itr.next();
            for (final AsyncContextAuthorizer asyncContextAuthorizer2 : authContext) {
                if (asyncContextAuthorizer2.isAuthenticated()) {
                    authenticatedAsyncContext.add(asyncContextAuthorizer2);
                }
                else {
                    asyncContextAuthorizer2.complete();
                }
            }
        }
        return authenticatedAsyncContext;
    }
    
    static void AAA(List<AsyncContextAuthorizer> asyncContextsBatch) throws Exception {
        asyncContextsBatch = initalize(asyncContextsBatch);
        authorizeAndClose(authenticate(asyncContextsBatch));
    }
    
    private static List<AsyncContextAuthorizer> initalize(final List<AsyncContextAuthorizer> asyncContextsBatch) {
        final List<AsyncContextAuthorizer> initializedAsyncContexts = new ArrayList<AsyncContextAuthorizer>();
        for (final AsyncContextAuthorizer asyncContextAuthorizer : asyncContextsBatch) {
            try {
                final AsyncContextAuthorizer initializedAsyncContextAuthorizer = asyncContextAuthorizer.initalize();
                if (initializedAsyncContextAuthorizer == null) {
                    continue;
                }
                initializedAsyncContexts.add(initializedAsyncContextAuthorizer);
            }
            catch (final Exception ex) {
                SyMLogger.log("FileServletLog", Level.SEVERE, "init " + ex.getMessage());
                try {
                    asyncContextAuthorizer.complete(400);
                }
                catch (final Exception ex2) {
                    SyMLogger.log("FileServletLog", Level.SEVERE, (String)null, (Throwable)ex2);
                }
            }
        }
        return initializedAsyncContexts;
    }
    
    static Long getLoginIDForValidAuthToken(final String authToken, final String host) {
        Long loginID = null;
        try {
            if (host.contains("8020") || host.contains("8383")) {
                final DataObject authDO = AbstractAuthenticationKeyHandler.getInstance().authenticateAPIKey(authToken, Integer.valueOf(Integer.parseInt("101")));
                if (authDO != null && !authDO.isEmpty()) {
                    final Row authRow = authDO.getRow("APIKeyDetails");
                    loginID = (Long)authRow.get("LOGIN_ID");
                }
            }
            else {
                final JSONObject properties = new JSONObject();
                properties.put("API_KEY", (Object)authToken);
                final JSONObject userDetails = APIKeyUtil.getNewInstance().getUserDetails(properties);
                if (String.valueOf(userDetails.get("status")).equals("success")) {
                    final Long userId = userDetails.getLong("USER_ID");
                    loginID = DMUserHandler.getLoginIdForUserId(userId);
                }
            }
        }
        catch (final Exception ex1) {
            SyMLogger.log("FileServletLog", Level.SEVERE, "exception while validating authToken", (Throwable)ex1);
        }
        return loginID;
    }
    
    static FileDownloadRegulatorAPI getFileDownloadRegulatorModuleImpl(final String module) throws Exception {
        if (module != null && module.contains("docrepository")) {
            return (FileDownloadRegulatorAPI)DocDeviceAuthorizer.getInstance();
        }
        if (module != null && module.contains("apprepository")) {
            return (FileDownloadRegulatorAPI)AppDeviceAuthorizer.getInstance();
        }
        throw new Exception(String.valueOf(module) + "has no authorizer");
    }
    
    static void close(final List<AsyncContextAuthorizer> asyncContextsBatch) {
        for (final AsyncContextAuthorizer asyncContextAuthorizer : asyncContextsBatch) {
            asyncContextAuthorizer.complete();
        }
    }
}
