package com.zoho.migration;

import java.util.Iterator;
import com.adventnet.persistence.ActionInfo;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;

public class TableLevelConfigurationHandlerUtil
{
    private static Map<String, String> tableLevelConfigurationHandlerMap;
    private static Map<String, TableLevelConfigurationHandler> tableLevelConfigurationHandlerInstanceMap;
    private static final Logger OUT;
    
    public static void setTableLevelConfigurationHandlerMap(final Map<String, String> tableLevelConfigurationHandlerMap) {
        TableLevelConfigurationHandlerUtil.tableLevelConfigurationHandlerMap = tableLevelConfigurationHandlerMap;
        TableLevelConfigurationHandlerUtil.tableLevelConfigurationHandlerInstanceMap = new HashMap<String, TableLevelConfigurationHandler>();
    }
    
    public static void invokeHandlers(final DataObject doWithOperations) throws Exception {
        if (TableLevelConfigurationHandlerUtil.tableLevelConfigurationHandlerMap == null) {
            return;
        }
        invokeHandlersFor(1, doWithOperations);
        invokeHandlersFor(2, doWithOperations);
        invokeHandlersFor(3, doWithOperations);
    }
    
    private static void invokeHandlersFor(final Integer operation, final DataObject doWithOperations) throws Exception {
        Map operInfo = null;
        switch (operation) {
            case 1: {
                operInfo = ((WritableDataObject)doWithOperations).getActionsFor("insert");
                break;
            }
            case 2: {
                operInfo = ((WritableDataObject)doWithOperations).getActionsFor("update");
                break;
            }
            case 3: {
                operInfo = ((WritableDataObject)doWithOperations).getActionsFor("delete");
                break;
            }
        }
        if (operInfo == null) {
            return;
        }
        final Map clonedOperInfo = (Map)((HashMap)operInfo).clone();
        for (final Object tableNameObj : clonedOperInfo.keySet()) {
            final String tableName = tableNameObj.toString();
            final TableLevelConfigurationHandler handler = getHandlerForTable(tableName);
            if (handler == null) {
                TableLevelConfigurationHandlerUtil.OUT.fine("No TableLevelConfigurationHandler is configured for " + tableName);
            }
            else {
                final List<ActionInfo> actionInfoList = (List<ActionInfo>)clonedOperInfo.get(tableName).clone();
                for (final ActionInfo info : actionInfoList) {
                    switch (operation) {
                        case 1: {
                            handler.handleForInsert(info.getValue(), doWithOperations);
                            continue;
                        }
                        case 2: {
                            handler.handleForUpdate(info.getValue(), doWithOperations);
                            continue;
                        }
                        case 3: {
                            handler.handleForDelete(info.getValue(), doWithOperations);
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    private static TableLevelConfigurationHandler getHandlerForTable(final String tableName) throws Exception {
        if (TableLevelConfigurationHandlerUtil.tableLevelConfigurationHandlerInstanceMap.containsKey(tableName)) {
            return TableLevelConfigurationHandlerUtil.tableLevelConfigurationHandlerInstanceMap.get(tableName);
        }
        final String handlerClassName = TableLevelConfigurationHandlerUtil.tableLevelConfigurationHandlerMap.containsKey(tableName) ? TableLevelConfigurationHandlerUtil.tableLevelConfigurationHandlerMap.get(tableName) : null;
        if (handlerClassName != null) {
            final TableLevelConfigurationHandler instance = (TableLevelConfigurationHandler)Thread.currentThread().getContextClassLoader().loadClass(handlerClassName).newInstance();
            TableLevelConfigurationHandlerUtil.tableLevelConfigurationHandlerInstanceMap.put(tableName, instance);
            return instance;
        }
        return null;
    }
    
    static {
        OUT = Logger.getLogger(TableLevelConfigurationHandlerUtil.class.getName());
    }
}
