package com.me.mdm.server.doc;

import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.File;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import org.json.simple.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.logging.Logger;

public class DocMgmt
{
    private static DocMgmt docMgmt;
    public static final Logger logger;
    
    public static DocMgmt getInstance() {
        if (DocMgmt.docMgmt == null) {
            DocMgmt.docMgmt = new DocMgmt();
        }
        return DocMgmt.docMgmt;
    }
    
    public void logEvent(final int eventID, final Long resourceID, final String remarks, final Object remarksArgs, final Long customerId) {
        try {
            MDMEventLogHandler.getInstance().MDMEventLogEntry(eventID, resourceID, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), remarks, remarksArgs, customerId);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private JSONObject generateResponse(final Integer statusCode, final Object[] status_msg_args) {
        final String status_msg = DocMgmtDataHandler.getInstance().getMsgForCode(statusCode, status_msg_args);
        final JSONObject docSaveResponse = new JSONObject();
        docSaveResponse.put((Object)"STATUS", (Object)statusCode);
        docSaveResponse.put((Object)"STATUS_MSG", (Object)status_msg);
        return docSaveResponse;
    }
    
    public JSONObject generateResponse(final Integer statusCode) {
        return this.generateResponse(statusCode, null);
    }
    
    public JSONObject getDocDetails(final Long[] customerID, final Long docID, final boolean locationPath) {
        try {
            final JSONObject docDetails = DocMgmtDataHandler.getInstance().getDocDetails(customerID, docID, locationPath);
            docDetails.putAll((Map)this.generateResponse(1));
            return docDetails;
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
            return this.generateResponse(90001);
        }
    }
    
    public JSONObject saveDocAndDetails(org.json.JSONObject docDetails) {
        try {
            final int docRepoType = docDetails.getInt("REPOSITORY_TYPE");
            if (docRepoType == 1 || docRepoType == 3) {
                docDetails = DocMgmtDataHandler.getInstance().addOrUpdateServerDoc(docDetails);
            }
            final int status = docDetails.getInt("STATUS");
            final JSONObject docSaveResponse = this.generateResponse(status);
            if (status == 1) {
                docSaveResponse.put((Object)"DOC_ID", (Object)docDetails.getLong("DOC_ID"));
                docSaveResponse.put((Object)"DOC_TYPE", (Object)docDetails.getInt("DOC_TYPE"));
                docSaveResponse.put((Object)"DOC_NAME", (Object)String.valueOf(docDetails.get("DOC_NAME")));
                docSaveResponse.put((Object)"ADDED_TIME", (Object)docDetails.getLong("ADDED_TIME"));
                if (docDetails.has("STATUS_MSG")) {
                    docSaveResponse.put((Object)"STATUS_MSG", (Object)String.valueOf(docDetails.get("STATUS_MSG")));
                }
                if (docDetails.has("DOCUMENT_FILE_PATH")) {
                    docSaveResponse.put((Object)"DOCUMENT_FILE_PATH", (Object)(docDetails.get("DOCUMENT_FILE_PATH") + ""));
                }
            }
            return docSaveResponse;
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, "exception occurred in saving the uploaded document file", ex);
            return this.generateResponse(90001);
        }
    }
    
    public JSONObject deleteDoc(final Long requestCustomerID, final Long[] customerIDs, final org.json.JSONObject docs) {
        int status = 90001;
        try {
            status = DocMgmtDataHandler.getInstance().markDocForDeletion(docs, requestCustomerID, customerIDs);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return this.generateResponse(status);
    }
    
    public JSONObject saveDocDeviceAssociation(final Long[] customerID, final org.json.JSONObject docToResourceDetails) {
        int status = 90001;
        try {
            status = DocMgmtDataHandler.getInstance().saveDocDeviceAssociation(customerID, docToResourceDetails);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
        return this.generateResponse(status);
    }
    
    public void docScheduledTask() {
        DocMgmtDataHandler.getInstance().cleanUpDocRepo();
        DocMgmtDataHandler.getInstance().checkAndOpenDocDistMsg();
        DocMgmtDataHandler.getInstance().handleMissedDocAck();
        final String key = SyMUtil.getSyMParameter("ADD_POLICY_UI_USER_PARAM");
        final Boolean boolKey = Boolean.valueOf(key);
        if (boolKey) {
            this.updateUserParamForUI();
        }
    }
    
    public void calculateCheckSumForFiles(final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ServerDocDetails"));
        selectQuery.addJoin(new Join("ServerDocDetails", "DocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
        final Criteria customerCriteria = new Criteria(new Column("DocumentDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria emptyCheckSumCriteria = new Criteria(new Column("ServerDocDetails", "CHECKSUM"), (Object)null, 0);
        selectQuery.setCriteria(customerCriteria.and(emptyCheckSumCriteria));
        selectQuery.addSelectColumn(new Column("ServerDocDetails", "*"));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
                final Iterator<Row> iterator = dataObject.getRows("ServerDocDetails");
                while (iterator.hasNext()) {
                    final Row serverDocRow = iterator.next();
                    final String location = (String)serverDocRow.get("LOCATION_PATH");
                    String docLocationPath = location.replace("/", File.separator);
                    CustomerInfoUtil.getInstance();
                    if (CustomerInfoUtil.isSAS()) {
                        docLocationPath = DocMgmtConstants.OLD_DOC_BASE_DIRECTORY + docLocationPath;
                    }
                    else {
                        docLocationPath = DocMgmtConstants.DOC_BASE_DIRECTORY + docLocationPath;
                    }
                    if (fileAccessAPI.isFileExists(docLocationPath)) {
                        final String fileCheckSum = ChecksumProvider.getInstance().GetSHA256CheckSum(docLocationPath);
                        serverDocRow.set("CHECKSUM", (Object)fileCheckSum);
                        dataObject.updateRow(serverDocRow);
                    }
                }
                MDMUtil.getPersistence().update(dataObject);
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DocumentManagedDeviceRel");
                updateQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
                updateQuery.setUpdateColumn("MODIFIED_TIME", (Object)System.currentTimeMillis());
                updateQuery.setCriteria(new Criteria(new Column("DocumentDetails", "CUSTOMER_ID"), (Object)customerId, 0));
                MDMUtil.getPersistence().update(updateQuery);
            }
        }
        catch (final DataAccessException e) {
            DocMgmt.logger.log(Level.WARNING, "Could not update checksum for content mgmt files", (Throwable)e);
            throw e;
        }
    }
    
    private void updateUserParamForUI() {
        try {
            final List userList = SyMUtil.getUsers();
            final String paramName = "POLICY_UI_PARAM";
            final String paramValue = "true";
            final Column userIdCol = Column.getColumn("UserParams", "USER_ACCOUNT_ID");
            Criteria criteria = new Criteria(userIdCol, (Object)userList.toArray(), 8);
            final Column paramNameCol = Column.getColumn("UserParams", "PARAM_NAME");
            final Criteria paramCri = new Criteria(paramNameCol, (Object)paramName, 0, false);
            criteria = criteria.and(paramCri);
            final DataObject resultDO = MDMUtil.getPersistenceLite().get("UserParams", criteria);
            for (final Long userId : userList) {
                final Row row = resultDO.getRow("UserParams", new Criteria(Column.getColumn("UserParams", "USER_ACCOUNT_ID"), (Object)userId, 0));
                if (row != null) {
                    row.set("PARAM_VALUE", (Object)paramValue);
                    resultDO.updateRow(row);
                }
                else {
                    final Row userParamRow = new Row("UserParams");
                    userParamRow.set("USER_ACCOUNT_ID", (Object)userId);
                    userParamRow.set("PARAM_NAME", (Object)paramName);
                    userParamRow.set("PARAM_VALUE", (Object)paramValue);
                    resultDO.addRow(userParamRow);
                }
            }
            MDMUtil.getPersistence().update(resultDO);
            SyMUtil.updateSyMParameter("ADD_POLICY_UI_USER_PARAM", "false");
        }
        catch (final SyMException | DataAccessException e) {
            DocMgmt.logger.log(Level.SEVERE, null, e);
        }
    }
    
    static {
        DocMgmt.docMgmt = null;
        logger = Logger.getLogger("MDMDocLogger");
    }
}
