package com.adventnet.persistence.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.adventnet.persistence.DefaultOperationHandler;
import com.adventnet.persistence.ActionInfo;
import java.util.Collection;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.OperationInfo;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.adventnet.db.persistence.metadata.parser.ParserUtil;
import org.w3c.dom.Element;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.adventnet.persistence.OperationHandler;
import java.util.logging.Logger;

public class Operation
{
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    private static ThreadLocal<OperationHandler> operationHandler;
    private static ThreadLocal handler;
    private static ThreadLocal delQuery;
    private static ThreadLocal updateQuery;
    private static ThreadLocal tableVsPKs;
    private static ThreadLocal sqsLocal;
    private static ThreadLocal criteriasLocal;
    private static ThreadLocal inputCriteriasLocal;
    private static Properties opHandlerProps;
    private static String operationHandlerName;
    private static ThreadLocal messagePropertyHolder;
    private static ThreadLocal<Map> contextInfo;
    
    private Operation() {
    }
    
    public static void setContextInfo(final Object key, final Object value) {
        if (Operation.contextInfo.get() == null) {
            Operation.contextInfo.set(new HashMap());
        }
        Operation.contextInfo.get().put(key, value);
    }
    
    public static Object getContextInfo(final Object key) {
        return (Operation.contextInfo.get() == null) ? null : Operation.contextInfo.get().get(key);
    }
    
    public static Map getAllContextInfo() {
        return Operation.contextInfo.get();
    }
    
    public static void setOperationHandler(final String opHandler) {
        Operation.operationHandlerName = PersistenceInitializer.getConfigurationValue(opHandler);
        if (Operation.operationHandlerName == null) {
            Operation.LOGGER.log(Level.WARNING, "Operation handler class name not specified. Defaulting to com.adventnet.persistence.DefaultOperationHandler");
            Operation.operationHandlerName = "com.adventnet.persistence.DefaultOperationHandler";
        }
        Operation.opHandlerProps = PersistenceInitializer.getConfigurationProps(opHandler);
    }
    
    public static void clear() {
        Operation.handler.set(null);
        Operation.delQuery.set(null);
        Operation.updateQuery.set(null);
        Operation.tableVsPKs.set(null);
        Operation.sqsLocal.set(null);
        Operation.criteriasLocal.set(null);
        Operation.inputCriteriasLocal.set(null);
        Operation.messagePropertyHolder.set(null);
        Operation.contextInfo.set(null);
    }
    
    public static void start() {
        Operation.handler.set(getOperationHandler());
        Operation.tableVsPKs.set(null);
        Operation.sqsLocal.set(new ArrayList());
        Operation.criteriasLocal.set(new ArrayList());
        Operation.inputCriteriasLocal.set(new ArrayList());
        Operation.contextInfo.set(new HashMap());
    }
    
    public static void addRow(final int operation, final Row row) throws DataAccessException {
        final OperationHandler opHandler = Operation.handler.get();
        if (opHandler != null) {
            opHandler.addRow(operation, row);
        }
    }
    
    public static void addRow(final int operation, final Row updRow, final int foreignKeyConstraint) throws DataAccessException {
        final OperationHandler opHandler = Operation.handler.get();
        if (opHandler != null) {
            opHandler.addRow(operation, updRow, foreignKeyConstraint);
        }
    }
    
    public static void resume() {
        final OperationHandler opHandler = Operation.handler.get();
        if (opHandler == null) {
            return;
        }
        opHandler.resume();
    }
    
    public static void suspend() {
        final OperationHandler opHandler = Operation.handler.get();
        if (opHandler == null) {
            return;
        }
        opHandler.suspend();
    }
    
    public static void setDataObject(final WritableDataObject dObj) {
        final OperationHandler opHandler = Operation.handler.get();
        if (opHandler == null) {
            return;
        }
        opHandler.setDataObject(dObj);
    }
    
    public static DataObject getDataObject() throws DataAccessException {
        final OperationHandler opHandler = Operation.handler.get();
        if (opHandler == null) {
            return null;
        }
        return opHandler.getDataObject();
    }
    
    public static Set getPKs(final String tableName) {
        final Map tableVsPKsMap = Operation.tableVsPKs.get();
        if (tableVsPKsMap == null) {
            return null;
        }
        return tableVsPKsMap.get(tableName);
    }
    
    public static void addPK(final String tableName, final List pkValues) {
        Map tableVsPKsMap = Operation.tableVsPKs.get();
        if (tableVsPKsMap == null) {
            tableVsPKsMap = new HashMap();
            Operation.tableVsPKs.set(tableVsPKsMap);
        }
        Set pkSet = tableVsPKsMap.get(tableName);
        if (pkSet == null) {
            pkSet = new HashSet();
            tableVsPKsMap.put(tableName, pkSet);
        }
        pkSet.add(pkValues);
    }
    
    public static void addMessageProperty(final String key, final Object value) throws DataAccessException {
        Map messageProperties = Operation.messagePropertyHolder.get();
        if (messageProperties == null) {
            messageProperties = new HashMap();
            Operation.messagePropertyHolder.set(messageProperties);
        }
        messageProperties.put(key, value);
    }
    
    public static Map getMessageProperties() {
        return Operation.messagePropertyHolder.get();
    }
    
    public static void setMessageProperties(final Map value) {
        Map messageProperties = Operation.messagePropertyHolder.get();
        if (messageProperties == null) {
            messageProperties = value;
        }
        else {
            messageProperties.putAll(value);
        }
        Operation.messagePropertyHolder.set(messageProperties);
    }
    
    private static Properties getProperties(final Element elem) {
        final Properties props = new Properties();
        final NodeList nodes = elem.getChildNodes();
        for (int length = nodes.getLength(), i = 0; i < length; ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    final String tagName = nodeElement.getTagName();
                    final String value = ParserUtil.getTextNodeVal(nodeElement);
                    props.setProperty(tagName, value);
                    break;
                }
            }
        }
        Operation.LOGGER.exiting(Operation.CLASS_NAME, "getProperties", props);
        return props;
    }
    
    public static OperationInfo getOperationInfo() throws DataAccessException {
        final OperationInfo delInfo = new OperationInfo();
        delInfo.setSelectQueries(Operation.sqsLocal.get());
        delInfo.setInputDeleteCriterias(Operation.inputCriteriasLocal.get());
        delInfo.setInputDeleteQuery(Operation.delQuery.get());
        delInfo.setInputUpdateQuery(Operation.updateQuery.get());
        final OperationHandler opHandler = Operation.handler.get();
        if (opHandler != null) {
            delInfo.setBulk(opHandler.isBulk());
            final DataObject dataObject = opHandler.getDataObject();
            delInfo.setDataObject(dataObject);
            List tableNames = opHandler.getTableNames();
            List origTableNames = opHandler.getOrigTableNames();
            if (tableNames != null && tableNames.size() > 0) {
                delInfo.setTableNames(tableNames);
                delInfo.setOrigTableNames(origTableNames);
            }
            else if (dataObject != null) {
                tableNames = dataObject.getTableNames();
                origTableNames = new ArrayList();
                for (int tSize = tableNames.size(), i = 0; i < tSize; ++i) {
                    final String tableName = tableNames.get(i);
                    final String origTableName = ((WritableDataObject)dataObject).getOrigTableName(tableName);
                    origTableNames.add(origTableName);
                }
                delInfo.setTableNames(tableNames);
                delInfo.setOrigTableNames(origTableNames);
            }
            else {
                delInfo.setTableNames(new ArrayList());
            }
            final List bulkTableNames = opHandler.getBulkTableNames();
            delInfo.setBulkTableNames(bulkTableNames);
        }
        return delInfo;
    }
    
    public static void filterDataObject(final DataObject dObj) throws DataAccessException {
        final OperationHandler opHandler = Operation.handler.get();
        if (opHandler != null) {
            opHandler.filterDataObject(dObj);
        }
    }
    
    public static void addDeleteCriteria(final Criteria criteria) {
        final List criteriaList = Operation.criteriasLocal.get();
        if (criteriaList != null) {
            criteriaList.add(criteria);
        }
    }
    
    public static List getDeleteCriteriaList() {
        return Operation.criteriasLocal.get();
    }
    
    public static void clearCriteriaList() {
        final List criteriaList = Operation.criteriasLocal.get();
        if (criteriaList != null) {
            criteriaList.clear();
        }
    }
    
    public static void addSelectQuery(final SelectQuery sq) {
        final List sqList = Operation.sqsLocal.get();
        if (sqList != null) {
            sqList.add(sq);
        }
    }
    
    public static void addInputDeleteCriteria(final Criteria criteria) {
        final List inputCriteriaList = Operation.inputCriteriasLocal.get();
        if (inputCriteriaList != null) {
            inputCriteriaList.add(criteria);
        }
    }
    
    public static List getInputDeleteCriterias() {
        return Operation.inputCriteriasLocal.get();
    }
    
    public static void addDeleteQuery(final DeleteQuery query) {
        Operation.delQuery.set(query);
    }
    
    public static DeleteQuery getDeleteQuery() {
        return Operation.delQuery.get();
    }
    
    public static void setUpdateQuery(final UpdateQuery query) {
        Operation.updateQuery.set(query);
    }
    
    public static UpdateQuery getUpdateQuery() {
        return Operation.updateQuery.get();
    }
    
    public static void clearPKs() {
        Operation.tableVsPKs.set(null);
    }
    
    public static void clearOperationHandler() {
        Operation.operationHandler.set(null);
    }
    
    public static OperationInfo merge(final OperationInfo previousInfo, final OperationInfo opInfo) throws DataAccessException {
        List bulkTableNames = previousInfo.getBulkTableNames();
        List tableNames = previousInfo.getTableNames();
        List originalTableNames = previousInfo.getOrigTableNames();
        List selectQueries = previousInfo.getSelectQueries();
        List inputDeleteCriterias = previousInfo.getInputDeleteCriterias();
        final DeleteQuery delQuery = previousInfo.getInputDeleteQuery();
        final HashSet btnSet = new HashSet(bulkTableNames);
        final HashSet tnSet = new HashSet(tableNames);
        final HashSet otnSet = new HashSet(originalTableNames);
        final HashSet sqSet = new HashSet(selectQueries);
        final HashSet idcSet = new HashSet(inputDeleteCriterias);
        btnSet.addAll(opInfo.getBulkTableNames());
        tnSet.addAll(opInfo.getTableNames());
        otnSet.addAll(opInfo.getOrigTableNames());
        sqSet.addAll(opInfo.getSelectQueries());
        idcSet.addAll(opInfo.getInputDeleteCriterias());
        bulkTableNames = new ArrayList(btnSet);
        tableNames = new ArrayList(tnSet);
        originalTableNames = new ArrayList(otnSet);
        selectQueries = new ArrayList(sqSet);
        inputDeleteCriterias = new ArrayList(idcSet);
        if (previousInfo.getDataObject() == null && opInfo.getDataObject() == null) {
            final OperationInfo returnedOpInfo = new OperationInfo();
            returnedOpInfo.setBulk(true);
            returnedOpInfo.setBulkTableNames(bulkTableNames);
            returnedOpInfo.setTableNames(tableNames);
            returnedOpInfo.setOrigTableNames(originalTableNames);
            returnedOpInfo.setDataObject(null);
            returnedOpInfo.setSelectQueries(selectQueries);
            returnedOpInfo.setInputDeleteCriterias(inputDeleteCriterias);
            returnedOpInfo.setInputDeleteQuery(delQuery);
            return returnedOpInfo;
        }
        final int defaultFKConstraint = -1;
        final OperationHandler opHandler = (Operation.operationHandler.get() != null) ? Operation.operationHandler.get() : getOperationHandler();
        opHandler.setTableNames(tableNames);
        opHandler.setBulkTableNames(bulkTableNames);
        DataObject dObj = previousInfo.getDataObject();
        int cause = previousInfo.getOperation();
        if (dObj != null && Operation.operationHandler.get() == null) {
            final List operations = dObj.getOperations();
            for (int size = operations.size(), i = 0; i < size; ++i) {
                final ActionInfo info = operations.get(i);
                final Row row = info.getValue();
                final int operation = info.getOperation();
                if (operation == 3 || operation == 4) {
                    opHandler.addRow(operation, row, 1);
                }
                else if (cause == 3) {
                    opHandler.addRow(3, row, defaultFKConstraint);
                }
                else {
                    opHandler.addRow(operation, row);
                }
            }
        }
        dObj = opInfo.getDataObject();
        cause = opInfo.getOperation();
        if (dObj != null) {
            final List operations = dObj.getOperations();
            for (int size = operations.size(), i = 0; i < size; ++i) {
                final ActionInfo info = operations.get(i);
                final Row row = info.getValue();
                final int operation = info.getOperation();
                if (operation == 3 || operation == 4) {
                    opHandler.addRow(operation, row, 1);
                }
                else if (cause == 3) {
                    opHandler.addRow(3, row, defaultFKConstraint);
                }
                else {
                    opHandler.addRow(operation, row);
                }
            }
        }
        final OperationInfo returnedOpInfo2 = new OperationInfo();
        returnedOpInfo2.setBulk(opHandler.isBulk());
        returnedOpInfo2.setBulkTableNames(opHandler.getBulkTableNames());
        returnedOpInfo2.setTableNames(opHandler.getTableNames());
        returnedOpInfo2.setOrigTableNames(opHandler.getOrigTableNames());
        returnedOpInfo2.setDataObject(opHandler.getDataObject());
        returnedOpInfo2.setSelectQueries(selectQueries);
        returnedOpInfo2.setInputDeleteCriterias(inputDeleteCriterias);
        returnedOpInfo2.setInputDeleteQuery(delQuery);
        Operation.operationHandler.set(opHandler);
        return returnedOpInfo2;
    }
    
    private static OperationHandler getOperationHandler() {
        if (Operation.operationHandlerName == null) {
            Operation.LOGGER.log(Level.SEVERE, "Operation handler class name not specified.");
            Operation.operationHandlerName = "com.adventnet.persistence.DefaultOperationHandler";
        }
        OperationHandler opHandler = null;
        try {
            final Class clz = Class.forName(Operation.operationHandlerName);
            final Class[] params = { Properties.class };
            final Constructor cons = clz.getConstructor((Class[])params);
            opHandler = cons.newInstance(Operation.opHandlerProps);
        }
        catch (final ClassNotFoundException cnfe) {
            Operation.LOGGER.log(Level.WARNING, "Specified operation handler class not found. Defaulting to com.adventnet.persistence.DefaultOperationHandler", cnfe);
            opHandler = new DefaultOperationHandler();
        }
        catch (final NoSuchMethodException nsme) {
            Operation.LOGGER.log(Level.WARNING, "Specified operation handler class doesn't have constructor accepting Element. Defaulting to com.adventnet.persistence.DefaultOperationHandler", nsme);
            opHandler = new DefaultOperationHandler();
        }
        catch (final InstantiationException ie) {
            Operation.LOGGER.log(Level.WARNING, "Can not instantiate operation handler. Defaulting to com.adventnet.persistence.DefaultOperationHandler", ie);
            opHandler = new DefaultOperationHandler();
        }
        catch (final IllegalAccessException iae) {
            Operation.LOGGER.log(Level.WARNING, "Can not instantiate operation handler. Defaulting to com.adventnet.persistence.DefaultOperationHandler", iae);
            opHandler = new DefaultOperationHandler();
        }
        catch (final InvocationTargetException ite) {
            Operation.LOGGER.log(Level.WARNING, "Can not instantiate operation handler. Defaulting to com.adventnet.persistence.DefaultOperationHandler", ite);
            opHandler = new DefaultOperationHandler();
        }
        return opHandler;
    }
    
    static {
        CLASS_NAME = Operation.class.getName();
        LOGGER = Logger.getLogger(Operation.CLASS_NAME);
        Operation.handler = new ThreadLocal();
        Operation.delQuery = new ThreadLocal();
        Operation.updateQuery = new ThreadLocal();
        Operation.tableVsPKs = new ThreadLocal();
        Operation.sqsLocal = new ThreadLocal();
        Operation.criteriasLocal = new ThreadLocal();
        Operation.inputCriteriasLocal = new ThreadLocal();
        Operation.messagePropertyHolder = new ThreadLocal();
        Operation.contextInfo = new ThreadLocal<Map>();
        Operation.operationHandler = new ThreadLocal<OperationHandler>();
    }
}
