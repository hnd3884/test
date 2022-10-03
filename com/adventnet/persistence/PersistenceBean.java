package com.adventnet.persistence;

import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.ds.query.AlterTableQuery;
import java.sql.SQLException;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.interceptor.DeletePersistenceRequest;
import java.util.Stack;
import com.adventnet.ds.query.UpdateQuery;
import java.util.HashMap;
import com.adventnet.persistence.interceptor.ModifyPersistenceRequest;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.interceptor.RetrievePersistenceRequest;
import com.adventnet.ds.query.DeleteQuery;
import java.util.Map;
import com.adventnet.persistence.interceptor.TerminalPersistenceRequest;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.interceptor.PersistenceRequest;
import com.adventnet.persistence.interceptor.CreatePersistenceRequest;
import com.adventnet.persistence.internal.Operation;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.persistence.interceptor.PersistenceInterceptor;
import java.util.logging.Logger;
import com.adventnet.mfw.bean.Initializable;

public class PersistenceBean implements Initializable, Persistence, ReadOnlyPersistence
{
    private static final String CLASS_NAME;
    private static final Logger logger;
    private static final String CORE_INTERCEPTOR = "com.adventnet.persistence.interceptor.CorePersistenceInterceptor";
    PersistenceInterceptor startup;
    private static ThreadLocal<PersistenceBean> ref;
    
    public PersistenceBean() {
        this.startup = null;
    }
    
    public void initialize(final DataObject beanDO) throws Exception {
        PersistenceBean.logger.log(Level.FINEST, "BeanDO :: {0}", beanDO);
        PersistenceInterceptor current = null;
        final Iterator i = beanDO.getRows("BeanInterceptor");
        final List<PersistenceInterceptor> interceptors = new ArrayList<PersistenceInterceptor>();
        final List<String> interceptorNames = new ArrayList<String>();
        while (i.hasNext()) {
            final Row interceptorRow = i.next();
            final String interceptorName = (String)interceptorRow.get("CLASSNAME");
            interceptorNames.add(interceptorName);
            final PersistenceInterceptor interceptor = (PersistenceInterceptor)Class.forName(interceptorName).newInstance();
            interceptor.setBeanConfiguration(beanDO);
            interceptors.add(interceptor);
        }
        PersistenceInterceptor interceptor2 = null;
        if (!interceptorNames.contains("com.adventnet.persistence.ejb.interceptor.CorePersistenceInterceptor")) {
            interceptor2 = (PersistenceInterceptor)Class.forName("com.adventnet.persistence.interceptor.CorePersistenceInterceptor").newInstance();
            interceptors.add(interceptor2);
        }
        this.startup = interceptors.get(0);
        current = this.startup;
        for (int j = 1; j < interceptors.size(); ++j) {
            interceptor2 = interceptors.get(j);
            current.setNextInterceptor(interceptor2);
            current = interceptor2;
        }
    }
    
    public DataObject add(DataObject dataObject) throws DataAccessException {
        PersistenceBean.logger.finer("addDataObject request in PersistenceBean delegating through interceptors chain... ");
        if (dataObject != null && !dataObject.isEmpty()) {
            Operation.start();
            try {
                final PersistenceRequest pr = new CreatePersistenceRequest(dataObject);
                dataObject = (DataObject)this.process(pr);
            }
            finally {
                Operation.clear();
            }
        }
        return dataObject;
    }
    
    public DataObject constructDataObject() throws DataAccessException {
        return DataAccess.constructDataObject();
    }
    
    public void delete(final Criteria condition) throws DataAccessException {
        final long requestId = TerminalPersistenceRequest.nextRequestId();
        TerminalPersistenceRequest tpr = new TerminalPersistenceRequest(901, requestId);
        this.process(tpr);
        final Map messageProperties = Operation.getMessageProperties();
        this.delete(condition, messageProperties, true);
        Operation.setMessageProperties(messageProperties);
        tpr = new TerminalPersistenceRequest(902, requestId);
        Operation.start();
        this.process(tpr);
        Operation.clear();
    }
    
    public void delete(final Row row) throws DataAccessException {
        final Criteria criteria = QueryConstructor.formCriteria(row);
        this.delete(criteria);
    }
    
    public int delete(final DeleteQuery query) throws DataAccessException {
        final long requestId = TerminalPersistenceRequest.nextRequestId();
        TerminalPersistenceRequest tpr = new TerminalPersistenceRequest(901, requestId);
        this.process(tpr);
        final Map messageProperties = Operation.getMessageProperties();
        final int noOfRowsDeleted = this.delete(query, messageProperties);
        Operation.setMessageProperties(messageProperties);
        tpr = new TerminalPersistenceRequest(902, requestId);
        Operation.start();
        this.process(tpr);
        Operation.clear();
        return noOfRowsDeleted;
    }
    
    public DataObject get(final String tableName, final Row instance) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableName, instance);
        return (DataObject)this.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final String tableName, final List instances) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableName, instances);
        return (DataObject)this.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final String tableName, final Criteria condition) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableName, condition);
        return (DataObject)this.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final List tableNames, final Criteria condition) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, condition);
        return (DataObject)this.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final List tableNames, final List instances) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, instances);
        return (DataObject)this.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final List tableNames, final Row instance) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, instance);
        return (DataObject)this.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final List tableNames, final List optionalTableNames, final Criteria condition) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, optionalTableNames, condition);
        return (DataObject)this.process(new RetrievePersistenceRequest(sq));
    }
    
    public DataObject get(final SelectQuery selectQuery) throws DataAccessException {
        return (DataObject)this.process(new RetrievePersistenceRequest(selectQuery));
    }
    
    public DataObject getForPersonalities(final List personalities, final Criteria condition) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonalities(personalities, condition);
        return (DataObject)this.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonalities(final List personalities, final List instances) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonalities(personalities, instances);
        return (DataObject)this.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonalities(final List personalities, final Row instance) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonalities(personalities, instance);
        return (DataObject)this.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonalities(final List personalities, final List deepRetrievedPersonalities, final Row instance) throws DataAccessException {
        if (PersonalityConfigurationUtil.areAllPersonalitiesNotIndexed(deepRetrievedPersonalities)) {
            final SelectQuery query = QueryConstructor.getForPersonalities(personalities, deepRetrievedPersonalities, QueryConstructor.formCriteria(instance));
            return (DataObject)this.process(new RetrievePersistenceRequest(query));
        }
        return DataAccess.getForPersonalities(personalities, deepRetrievedPersonalities, instance);
    }
    
    public DataObject getForPersonalities(final List personalities, final List deepRetrievedPersonalities, final List instances) throws DataAccessException {
        if (PersonalityConfigurationUtil.areAllPersonalitiesNotIndexed(deepRetrievedPersonalities)) {
            final SelectQuery query = QueryConstructor.getForPersonalities(personalities, deepRetrievedPersonalities, QueryConstructor.formCriteria(instances));
            return (DataObject)this.process(new RetrievePersistenceRequest(query));
        }
        return DataAccess.getForPersonalities(personalities, deepRetrievedPersonalities, instances);
    }
    
    public DataObject getForPersonalities(final List personalities, final List deepRetrievedPersonalities, final Criteria condition) throws DataAccessException {
        if (PersonalityConfigurationUtil.areAllPersonalitiesNotIndexed(deepRetrievedPersonalities)) {
            final SelectQuery query = QueryConstructor.getForPersonalities(personalities, deepRetrievedPersonalities, condition);
            return (DataObject)this.process(new RetrievePersistenceRequest(query));
        }
        return DataAccess.getForPersonalities(personalities, deepRetrievedPersonalities, condition);
    }
    
    public DataObject getForPersonality(final String personalityName, final Criteria condition) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonality(personalityName, condition);
        return (DataObject)this.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonality(final String personalityName, final List instances) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonality(personalityName, instances);
        return (DataObject)this.process(new RetrievePersistenceRequest(query));
    }
    
    public DataObject getForPersonality(final String personalityName, final Row instance) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonality(personalityName, instance);
        return (DataObject)this.process(new RetrievePersistenceRequest(query));
    }
    
    public List getPersonalities(final Row instance) throws DataAccessException {
        return DataAccess.getPersonalities(instance);
    }
    
    public DataObject getCompleteData(final Row row) throws DataAccessException {
        return DataAccess.getCompleteData(row);
    }
    
    public DataObject getPrimaryKeys(final String tableName, final Criteria condition) throws DataAccessException {
        return DataAccess.getPrimaryKeys(tableName, condition);
    }
    
    public boolean isInstanceOf(final Row instance, final List personalities) throws DataAccessException {
        return DataAccess.isInstanceOf(instance, personalities);
    }
    
    public boolean isInstanceOf(final Row instance, final String personalityName) throws DataAccessException {
        return DataAccess.isInstanceOf(instance, personalityName);
    }
    
    public DataObject update(final DataObject dataObject) throws DataAccessException {
        PersistenceBean.logger.log(Level.FINEST, "Update method invoked with the DataObject:{0}", dataObject);
        final WritableDataObject writeDataObject = (WritableDataObject)dataObject;
        final Map messageProperties = Operation.getMessageProperties();
        final List insertActionsList = writeDataObject.removeActionsFor("insert");
        final List deleteActionsList = writeDataObject.removeActionsFor("delete");
        if (insertActionsList != null) {
            final WritableDataObject newObject = new WritableDataObject();
            final int size = insertActionsList.size();
            Operation.setMessageProperties(messageProperties);
            Operation.start();
            for (int i = 0; i < size; ++i) {
                final ActionInfo info = insertActionsList.get(i);
                final Row thisRow = info.getValue();
                PersistenceBean.logger.log(Level.FINEST, "ActionInfo:{0}", info);
                newObject.addRow(thisRow);
            }
            final PersistenceRequest cpr = new CreatePersistenceRequest(newObject);
            this.process(cpr);
            Operation.clear();
        }
        final HashMap updateInfos = writeDataObject.getActionsFor("update");
        if (updateInfos != null) {
            Operation.setMessageProperties(messageProperties);
            Operation.start();
            final PersistenceRequest mpr = new ModifyPersistenceRequest(dataObject);
            this.process(mpr);
            Operation.clear();
        }
        if (deleteActionsList != null) {
            final int size = deleteActionsList.size();
            final long requestId = TerminalPersistenceRequest.nextRequestId();
            TerminalPersistenceRequest tpr = new TerminalPersistenceRequest(901, requestId);
            this.process(tpr);
            for (int j = size - 1; j >= 0; --j) {
                final ActionInfo info2 = deleteActionsList.get(j);
                if (info2.getCause() != 4) {
                    final Row thisRow2 = info2.getValue();
                    PersistenceBean.logger.log(Level.FINEST, "ActionInfo:{0}", info2);
                    final Criteria criteria = QueryConstructor.formCriteria(thisRow2);
                    this.delete(criteria, messageProperties, true);
                }
            }
            Operation.setMessageProperties(messageProperties);
            tpr = new TerminalPersistenceRequest(902, requestId);
            Operation.start();
            this.process(tpr);
            Operation.clear();
        }
        ((WritableDataObject)dataObject).clearIndices();
        return dataObject;
    }
    
    public int update(final UpdateQuery updateQuery) throws DataAccessException {
        PersistenceBean.logger.entering(PersistenceBean.CLASS_NAME, "update", updateQuery);
        final PersistenceRequest pr = new ModifyPersistenceRequest(null, updateQuery);
        PersistenceBean.logger.exiting(PersistenceBean.CLASS_NAME, "update", updateQuery);
        Operation.start();
        Operation.setUpdateQuery(updateQuery);
        final int noOfRowsUpdated = (int)this.process(pr);
        Operation.clear();
        return noOfRowsUpdated;
    }
    
    public List getDominantPersonalities(final Row instance) throws DataAccessException {
        return DataAccess.getDominantPersonalities(instance);
    }
    
    public DataObject fillGeneratedValues(final DataObject dataObject) throws DataAccessException {
        return DataAccess.fillGeneratedValues(dataObject);
    }
    
    private DataObject delete(Criteria condition, final Map messageProperties, boolean firstTime) throws DataAccessException {
        DataObject returnedDataObject = null;
        while (condition != null) {
            try {
                Operation.start();
                if (firstTime) {
                    DeleteUtil.setBdfkStack(new Stack());
                    Operation.addInputDeleteCriteria(condition);
                    firstTime = false;
                }
                final PersistenceRequest pr = new DeletePersistenceRequest(condition);
                this.process(pr);
                returnedDataObject = Operation.getDataObject();
            }
            finally {
                Operation.clear();
            }
            for (condition = null; condition == null && !DeleteUtil.isBdfkStackEmpty(); condition = DeleteUtil.getCondition(DeleteUtil.popBdfkStack())) {}
        }
        return returnedDataObject;
    }
    
    private int delete(final DeleteQuery query, final Map messageProperties) throws DataAccessException {
        DeleteUtil.setBdfkStack(new Stack());
        int noOfRowsDeleted = 0;
        try {
            Operation.start();
            Operation.addDeleteQuery(query);
            final PersistenceRequest pr = new DeletePersistenceRequest(query);
            noOfRowsDeleted = (int)this.process(pr);
        }
        finally {
            Operation.clear();
        }
        Criteria condition;
        for (condition = null; condition == null && !DeleteUtil.isBdfkStackEmpty(); condition = DeleteUtil.getCondition(DeleteUtil.popBdfkStack())) {}
        if (condition != null) {
            this.delete(condition, messageProperties, false);
        }
        return noOfRowsDeleted;
    }
    
    public void createTable(final String moduleName, final TableDefinition tableDefinition) throws DataAccessException, SQLException {
        try {
            DataAccess.createTable(moduleName, tableDefinition);
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final SQLException sqle) {
            throw sqle;
        }
    }
    
    public void alterTable(final AlterTableQuery alterTableQuery) throws DataAccessException, SQLException {
        try {
            DataAccess.alterTable(alterTableQuery);
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final SQLException sqle) {
            throw sqle;
        }
    }
    
    public void dropTable(final String tableName) throws DataAccessException, SQLException {
        try {
            DataAccess.dropTable(tableName);
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final SQLException sqle) {
            throw sqle;
        }
    }
    
    public void dropTables(final String moduleName) throws DataAccessException, SQLException {
        try {
            DataAccess.dropTables(moduleName);
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final SQLException sqle) {
            throw sqle;
        }
    }
    
    public void createTables(final String moduleName) throws DataAccessException, SQLException {
        try {
            DataAccess.createTables(moduleName);
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final SQLException sqle) {
            throw sqle;
        }
    }
    
    public void createTables(final List tables) throws DataAccessException, SQLException {
        try {
            DataAccess.createTables(tables);
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final SQLException sqle) {
            throw sqle;
        }
    }
    
    private Object process(final PersistenceRequest pr) throws DataAccessException {
        try {
            PersistenceBean.ref.set(this);
            return this.startup.process(pr);
        }
        finally {
            PersistenceBean.ref.set(null);
        }
    }
    
    public static PersistenceBean getRef() {
        return PersistenceBean.ref.get();
    }
    
    public void addDataType(final DataTypeDefinition edtDefinition) throws DataAccessException {
        DataAccess.addDataType(edtDefinition);
    }
    
    static {
        CLASS_NAME = PersistenceBean.class.getName();
        logger = Logger.getLogger(PersistenceBean.CLASS_NAME);
        PersistenceBean.ref = new ThreadLocal<PersistenceBean>();
    }
}
