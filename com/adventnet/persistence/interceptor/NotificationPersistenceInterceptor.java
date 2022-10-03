package com.adventnet.persistence.interceptor;

import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.adapter.mds.DBThreadLocal;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import javax.transaction.SystemException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.ActionInfo;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.PersistenceBean;
import com.adventnet.persistence.DeleteUtil;
import com.adventnet.persistence.internal.Operation;
import java.io.Serializable;
import com.adventnet.mfw.message.Messenger;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.persistence.OperationInfo;
import java.util.logging.Logger;

public class NotificationPersistenceInterceptor implements PersistenceInterceptor
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    private ThreadLocal<OperationInfo> opInfo;
    PersistenceInterceptor nextPI;
    private String dataModelTopicName;
    private String persistenceTopicName;
    private Map<String, Object> beanProps;
    
    public NotificationPersistenceInterceptor() throws DataAccessException {
        this.opInfo = new ThreadLocal<OperationInfo>();
        this.nextPI = null;
        this.dataModelTopicName = "DataModelTopic";
        this.persistenceTopicName = "PersistenceTopic";
        this.beanProps = new HashMap<String, Object>();
    }
    
    @Override
    public void setBeanConfiguration(final DataObject beanDO) throws DataAccessException {
        final String beanName = (String)beanDO.getRow("Bean").get("BEAN_NAME");
        this.beanProps.put("beanname", beanName);
        final Iterator itr = beanDO.getRows("BeanProperties");
        Row row = null;
        while (itr.hasNext()) {
            row = itr.next();
            this.beanProps.put((String)row.get("PROPERTY"), row.get("VALUE"));
        }
        NotificationPersistenceInterceptor.OUT.log(Level.FINE, "bean props:: " + this.beanProps);
    }
    
    @Override
    public String getInterceptorName() {
        return "NotificationPersistenceInterceptor";
    }
    
    @Override
    public Object process(final PersistenceRequest pRequest) throws DataAccessException {
        if (!Messenger.hasListener()) {
            return this.nextPI.process(pRequest);
        }
        boolean sendNotification = true;
        if (pRequest.getOperationType() == 604) {
            final TerminalPersistenceRequest tpr = (TerminalPersistenceRequest)pRequest;
            if (tpr.getTerminalType() == 902) {
                final OperationInfo delInfo = this.opInfo.get();
                if (!this.isEmpty(delInfo)) {
                    this.sendLMNotification("delete", delInfo);
                }
            }
            if (tpr.getTerminalType() == 901) {
                this.opInfo.set(null);
            }
            Operation.clearOperationHandler();
        }
        else if (pRequest.getOperationType() == 602) {
            NotificationPersistenceInterceptor.OUT.log(Level.FINER, "Processing Delete Request");
            final DeletePersistenceRequest request = (DeletePersistenceRequest)pRequest;
            final DeleteQuery query = request.getQuery();
            if (query != null) {
                OperationInfo delInfo2 = (OperationInfo)request.getContextInfo(query);
                if (delInfo2 == null) {
                    delInfo2 = DeleteUtil.getDeleteInfo(query);
                    request.setContextInfo(query, delInfo2);
                }
                if (!this.isEmpty(delInfo2)) {
                    delInfo2.setOperation("DELETE");
                    if (this.opInfo.get() != null) {
                        final OperationInfo previousInfo = this.opInfo.get();
                        final OperationInfo mergedInfo = Operation.merge(previousInfo, delInfo2);
                        mergedInfo.setOperation("DELETE");
                        this.opInfo.set(mergedInfo);
                    }
                    else {
                        this.opInfo.set(delInfo2);
                    }
                }
            }
            else {
                final Criteria criteria = request.getCriteria();
                OperationInfo delInfo3 = (OperationInfo)request.getContextInfo(criteria);
                if (delInfo3 == null) {
                    final String tableName = this.getTableName(criteria);
                    delInfo3 = DeleteUtil.getDeleteInfo(tableName, criteria);
                    request.setContextInfo(criteria, delInfo3);
                }
                if (!this.isEmpty(delInfo3)) {
                    delInfo3.setOperation("DELETE");
                    if (this.opInfo.get() != null) {
                        final OperationInfo previousInfo2 = this.opInfo.get();
                        final OperationInfo mergedInfo2 = Operation.merge(previousInfo2, delInfo3);
                        mergedInfo2.setOperation("DELETE");
                        this.opInfo.set(mergedInfo2);
                    }
                    else {
                        this.opInfo.set(delInfo3);
                    }
                }
            }
        }
        else if (pRequest.getOperationType() == 601) {
            NotificationPersistenceInterceptor.OUT.log(Level.FINER, "Processing Modify Request");
            final PersistenceBean pr = PersistenceBean.getRef();
            final DataObject dob = ((ModifyPersistenceRequest)pRequest).getDataObject();
            NotificationPersistenceInterceptor.OUT.log(Level.FINER, "Modify Request DataObject is {0}", dob);
            if (dob == null) {
                final UpdateQuery updateQuery = ((ModifyPersistenceRequest)pRequest).getUpdateQuery();
                NotificationPersistenceInterceptor.OUT.log(Level.FINER, "Modify Request UpdateQuery {0}", updateQuery);
                final String table = updateQuery.getTableName();
                final Criteria criteria2 = updateQuery.getCriteria();
                final HashMap updateColumns = (HashMap)updateQuery.getUpdateColumns();
                final SelectQuery select = new SelectQueryImpl(Table.getTable(table));
                select.addSelectColumn(Column.getColumn(table, "*"));
                select.setCriteria(criteria2);
                final List<Join> joins = updateQuery.getJoins();
                for (final Join join : joins) {
                    select.addJoin(join);
                }
                NotificationPersistenceInterceptor.OUT.log(Level.FINER, "Constructed query is {0}", select);
                final DataObject dataObject = pr.get(select);
                this.processAndSetDOToOperation(dataObject, table, updateColumns);
                if (dataObject instanceof WritableDataObject) {
                    final Map<String, List<ActionInfo>> updateActions = ((WritableDataObject)dataObject).getActionsFor("update");
                    if (updateActions == null || updateActions.isEmpty()) {
                        NotificationPersistenceInterceptor.OUT.log(Level.FINE, "Nothing has been changed in DB hence nothing is notified - for the UpdateQuery :: [{0}]", updateQuery);
                        sendNotification = false;
                    }
                }
            }
        }
        final Object retObj = this.nextPI.process(pRequest);
        if (sendNotification) {
            this.sendNotification(pRequest);
        }
        return retObj;
    }
    
    private void processAndSetDOToOperation(final DataObject dataObject, final String tableName, final HashMap updateColumns) throws DataAccessException {
        NotificationPersistenceInterceptor.OUT.log(Level.FINER, "In processAndSetDOTooperation DO {0} table {1} columns {2}", new Object[] { dataObject, tableName, updateColumns });
        for (final Column key : updateColumns.keySet()) {
            Object value = updateColumns.get(key);
            if (value instanceof Column) {
                value = "<ComputedValue>";
            }
            dataObject.set(tableName, key.getColumnName(), value);
        }
        NotificationPersistenceInterceptor.OUT.log(Level.FINER, "Setting dataObject as {0}", dataObject);
        Operation.setDataObject((WritableDataObject)dataObject);
    }
    
    private boolean isEmpty(final OperationInfo delInfo) {
        return delInfo == null || (!delInfo.isBulk() && delInfo.getDataObject() == null);
    }
    
    private void sendNotification(final PersistenceRequest pRequest) throws DataAccessException {
        if (pRequest.getOperationType() == 600) {
            this.sendLMNotification("add", Operation.getOperationInfo());
        }
        else if (pRequest.getOperationType() == 601) {
            this.sendLMNotification("update", Operation.getOperationInfo());
        }
    }
    
    private void sendLMNotification(final String cause, final Serializable obj) throws DataAccessException {
        NotificationPersistenceInterceptor.OUT.entering(NotificationPersistenceInterceptor.CLASS_NAME, "sendLMNotification", new Object[] { cause, obj });
        final OperationInfo opInfo = (OperationInfo)obj;
        opInfo.setOperation(cause.toUpperCase());
        if (this.isEmpty(opInfo)) {
            NotificationPersistenceInterceptor.OUT.log(Level.WARNING, "Empty Notification is being notified. Please check it ...");
            return;
        }
        OperationInfo dataModelTopicOpInfo = null;
        if (!cause.equals("delete")) {
            if (opInfo.getDataObject() != null) {
                Operation.filterDataObject(opInfo.getDataObject());
                dataModelTopicOpInfo = Operation.getOperationInfo();
                dataModelTopicOpInfo.setOperation(cause.toUpperCase());
            }
            else {
                NotificationPersistenceInterceptor.OUT.log(Level.WARNING, "Need to check this OperationInfo :: {0}", opInfo);
                dataModelTopicOpInfo = opInfo;
            }
        }
        else {
            dataModelTopicOpInfo = opInfo;
        }
        this.sendNotification(cause, obj, Messenger.Topics.PERSISTENCE_TOPIC.get());
        try {
            if (DataAccess.getTransactionManager().getTransaction() != null) {
                this.sendNotification(cause, opInfo, Messenger.Topics.COMMIT_TOPIC.get());
            }
        }
        catch (final SystemException e) {
            NotificationPersistenceInterceptor.OUT.log(Level.SEVERE, "Exception when sending commit notification - ", (Throwable)e);
        }
        Operation.setDataObject(null);
        this.sendNotification(cause, dataModelTopicOpInfo, Messenger.Topics.DATAMODEL_TOPIC.get());
        Operation.clear();
    }
    
    private void sendNotification(final String cause, final Serializable obj, final String topicName) throws DataAccessException {
        List tableNames = null;
        final StringBuffer tablesBuffer = new StringBuffer();
        final OperationInfo opInfo = (OperationInfo)obj;
        tableNames = opInfo.getOrigTableNames();
        if (opInfo.getDataObject() != null) {
            final WritableDataObject dObj = (WritableDataObject)opInfo.getDataObject();
            dObj.makeImmutable();
        }
        final long time = System.currentTimeMillis();
        if (tableNames != null) {
            for (int tabSize = tableNames.size(), i = 0; i < tabSize; ++i) {
                final String tableName = tableNames.get(i);
                try {
                    final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                    td.setModifiedTime(time);
                }
                catch (final MetaDataException mde) {
                    throw new DataAccessException(mde);
                }
                if (i != 0) {
                    tablesBuffer.append(",");
                }
                tablesBuffer.append("<").append(tableName).append(">");
            }
        }
        Map props = Operation.getMessageProperties();
        if (props == null) {
            props = new HashMap();
        }
        props.put("beaninfo", this.beanProps);
        opInfo.setMessageProperties(props);
        final Map hashMap = DBThreadLocal.get();
        final String datasourceName = (hashMap != null) ? hashMap.get("dbAdapter") : "default";
        opInfo.setDSName(datasourceName);
        try {
            Messenger.publish(topicName, (Object)obj);
        }
        catch (final Exception e) {
            NotificationPersistenceInterceptor.OUT.log(Level.SEVERE, "Exception occured while publishing message to the topic :: [" + topicName + "]", e);
            throw new DataAccessException("Exception occured while publishing message to the topic :: [" + topicName + "]", e);
        }
    }
    
    private String getTableName(Criteria criteria) {
        while (criteria.getLeftCriteria() != null) {
            criteria = criteria.getLeftCriteria();
        }
        return criteria.getColumn().getTableAlias();
    }
    
    @Override
    public void setNextInterceptor(final PersistenceInterceptor nextPI) {
        this.nextPI = nextPI;
    }
    
    @Override
    public void cleanup() {
    }
    
    static {
        CLASS_NAME = NotificationPersistenceInterceptor.class.getName();
        OUT = Logger.getLogger(NotificationPersistenceInterceptor.CLASS_NAME);
    }
}
