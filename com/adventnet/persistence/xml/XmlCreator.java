package com.adventnet.persistence.xml;

import java.util.Collection;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.Locale;
import org.w3c.dom.NodeList;
import java.util.Iterator;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.Row;
import org.w3c.dom.Node;
import javax.xml.parsers.ParserConfigurationException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import org.w3c.dom.DOMException;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

class XmlCreator
{
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    private DataObject data;
    private Document doc;
    private HashMap uvgKeyMap;
    private HashMap rowVsElement;
    private ArrayList groupingTagList;
    private Element rootElement;
    private boolean carryOverGeneratedValues;
    private Map uvhMap;
    private boolean useDVH;
    static DOMHandler domHandler;
    
    XmlCreator() {
        this.uvgKeyMap = new HashMap();
        this.rowVsElement = new HashMap();
        this.groupingTagList = new ArrayList();
        this.uvhMap = null;
        this.useDVH = true;
    }
    
    public boolean isCarryOverGeneratedValues() {
        return this.carryOverGeneratedValues;
    }
    
    public void setCarryOverGeneratedValues(final boolean v) {
        this.carryOverGeneratedValues = v;
    }
    
    public void setUVHMap(final Map m) {
        this.uvhMap = m;
    }
    
    public void setUseDVH(final boolean useDVH) {
        this.useDVH = useDVH;
    }
    
    Element createElement(final DataObject data, final ParentChildrenMap pcm) throws DOMException, DataAccessException, MetaDataException, DynamicValueHandlingException, ParserConfigurationException {
        try {
            XmlCreator.LOGGER.entering(XmlCreator.CLASS_NAME, "createElement", new Object[] { data, pcm });
            this.fillUvgKeyMap(this.data = data);
            this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            this.rootElement = this.doc.createElement(pcm.getElementName());
            XmlCreator.LOGGER.log(Level.FINEST, "Root element:{0}", this.rootElement);
            final ArrayList elements = new ArrayList();
            elements.add(this.rootElement);
            this.handleChildPcms(elements, pcm.getChildPCMs());
            XmlCreator.LOGGER.exiting(XmlCreator.CLASS_NAME, "createElement", this.rootElement);
            return this.rootElement;
        }
        catch (final DOMException ex) {
            ex.printStackTrace();
            throw ex;
        }
        catch (final MetaDataException ex2) {
            ex2.printStackTrace();
            throw ex2;
        }
        catch (final DataAccessException ex3) {
            ex3.printStackTrace();
            throw ex3;
        }
        catch (final DynamicValueHandlingException ex4) {
            ex4.printStackTrace();
            throw ex4;
        }
        catch (final ParserConfigurationException ex5) {
            ex5.printStackTrace();
            throw ex5;
        }
        catch (final Exception ex6) {
            ex6.printStackTrace();
            throw new RuntimeException(ex6);
        }
    }
    
    void handleChildPcms(final ArrayList elements, final List childPcms) throws DOMException, DataAccessException, MetaDataException, DynamicValueHandlingException {
        try {
            XmlCreator.LOGGER.entering(XmlCreator.CLASS_NAME, "handleChildPcms", new Object[] { elements, childPcms });
            if (childPcms == null) {
                return;
            }
            for (final ParentChildrenMap pcm : childPcms) {
                final ArrayList newElements = new ArrayList();
                final String tableName = pcm.getElementName();
                if (pcm.isGroupingTag()) {
                    this.groupingTagList.add(tableName);
                    final Element childElement = XmlCreator.domHandler.createElement(this.doc, tableName);
                    for (final Element parentElement : elements) {
                        final Element newElement = (Element)childElement.cloneNode(false);
                        parentElement.appendChild(newElement);
                        newElements.add(newElement);
                    }
                    this.handleChildPcms(newElements, pcm.getChildPCMs());
                    this.removeElementsIfNoChild(newElements);
                }
                else {
                    final Iterator rowIterator = this.data.getRows(tableName);
                    if (pcm.getUseCaseType() == 1) {
                        final Element rootElement = elements.get(0);
                        final String masterTableName = pcm.getMasterTableName();
                        XmlCreator.LOGGER.log(Level.FINEST, "{0} added to root", tableName);
                        XmlCreator.LOGGER.log(Level.FINEST, "masterTableName:{0}", masterTableName);
                        XmlCreator.LOGGER.log(Level.FINEST, "isSingleFKNonPK:{0}", pcm.isSingleFKNonPK());
                        while (rowIterator.hasNext()) {
                            final Row row = rowIterator.next();
                            final Element childElement2 = this.getElement(row, masterTableName, pcm.isBdfk());
                            rootElement.appendChild(childElement2);
                            newElements.add(childElement2);
                            this.rowVsElement.put(row, childElement2);
                        }
                        this.handleChildPcms(newElements, pcm.getChildPCMs());
                    }
                    else {
                        final String masterTableName2 = pcm.getMasterTableName();
                        Element parentElement = elements.get(0);
                        String parentTableName = parentElement.getTagName();
                        boolean isParentGroupTag = false;
                        String groupTagName = null;
                        XmlCreator.LOGGER.log(Level.FINEST, "ParentElement:{0}", parentElement);
                        XmlCreator.LOGGER.log(Level.FINEST, "Grouping tag list:{0}", this.groupingTagList);
                        XmlCreator.LOGGER.log(Level.FINEST, "rowVsElement:{0}", this.rowVsElement);
                        if (this.groupingTagList.contains(parentElement.getTagName())) {
                            isParentGroupTag = true;
                            groupTagName = parentElement.getTagName();
                            parentTableName = parentElement.getParentNode().getNodeName();
                        }
                        XmlCreator.LOGGER.log(Level.FINEST, "ParentTableName:{0}", parentTableName);
                        while (rowIterator.hasNext()) {
                            final Row childRow = rowIterator.next();
                            XmlCreator.LOGGER.log(Level.FINEST, "child row:{0}", childRow);
                            final ArrayList tableNames = new ArrayList();
                            tableNames.add(childRow.getTableName());
                            if (masterTableName2 != null && !parentTableName.equals(masterTableName2)) {
                                tableNames.add(masterTableName2);
                            }
                            tableNames.add(parentTableName);
                            final DataObject subObject = this.data.getDataObject(PersistenceUtil.sortTables(tableNames), childRow);
                            XmlCreator.LOGGER.log(Level.FINEST, "DataObject:{0}", this.data);
                            XmlCreator.LOGGER.log(Level.FINEST, "ParentTableName:{0}", parentTableName);
                            XmlCreator.LOGGER.log(Level.FINEST, "MasterTableName:{0}", masterTableName2);
                            XmlCreator.LOGGER.log(Level.FINEST, "TableNames:{0}", tableNames);
                            XmlCreator.LOGGER.log(Level.FINEST, "child row:{0}", childRow);
                            XmlCreator.LOGGER.log(Level.FINEST, "subObject:{0}", subObject);
                            Iterator parentRowIterator = subObject.getRows(parentTableName);
                            boolean parentHasRow = parentRowIterator.hasNext();
                            XmlCreator.LOGGER.log(Level.FINEST, "{0} has rows:{1}", new Object[] { parentTableName, parentHasRow });
                            if (!parentHasRow) {
                                parentRowIterator = subObject.getRows(masterTableName2);
                                parentHasRow = parentRowIterator.hasNext();
                                XmlCreator.LOGGER.log(Level.FINEST, "{0} has rows:{1}", new Object[] { masterTableName2, parentHasRow });
                                if (!parentHasRow) {
                                    final Element childElement3 = this.getElement(childRow, masterTableName2, pcm.isBdfk());
                                    this.rootElement.appendChild(childElement3);
                                    newElements.add(childElement3);
                                    this.rowVsElement.put(childRow, childElement3);
                                    continue;
                                }
                            }
                            parentElement = this.rowVsElement.get(parentRowIterator.next());
                            if (parentElement == null) {
                                final Element childElement3 = this.getElement(childRow, masterTableName2, pcm.isBdfk());
                                this.rootElement.appendChild(childElement3);
                                newElements.add(childElement3);
                                this.rowVsElement.put(childRow, childElement3);
                            }
                            else {
                                XmlCreator.LOGGER.log(Level.FINEST, "ParentElement:{0}", parentElement);
                                if (isParentGroupTag) {
                                    XmlCreator.LOGGER.finest("isParentGroupTag:true");
                                    final NodeList nodeList = parentElement.getElementsByTagName(groupTagName);
                                    parentElement = (Element)nodeList.item(0);
                                }
                                XmlCreator.LOGGER.log(Level.FINEST, "ParentElement:{0}", parentElement);
                                final HashMap columnvsValue = this.getColumnListToBeAdded(childRow, masterTableName2, pcm.isSingleFKNonPK());
                                final Element newElement2 = XmlCreator.domHandler.createElement(this.doc, childRow.getTableName());
                                this.setColumnValue(newElement2, columnvsValue, tableName);
                                XmlCreator.LOGGER.log(Level.FINEST, "NewElement:{0}", newElement2);
                                parentElement.appendChild(newElement2);
                                this.rowVsElement.put(childRow, newElement2);
                                newElements.add(newElement2);
                            }
                        }
                        this.handleChildPcms(newElements, pcm.getChildPCMs());
                    }
                }
            }
        }
        catch (final DOMException ex) {
            ex.printStackTrace();
            throw ex;
        }
        catch (final MetaDataException ex2) {
            ex2.printStackTrace();
            throw ex2;
        }
        catch (final DataAccessException ex3) {
            ex3.printStackTrace();
            throw ex3;
        }
        catch (final DynamicValueHandlingException ex4) {
            ex4.printStackTrace();
            throw ex4;
        }
        catch (final Exception ex5) {
            ex5.printStackTrace();
            throw new RuntimeException(ex5);
        }
    }
    
    private void setColumnValue(final Element colEl, final Map columnVsValue, final String tableName) throws MetaDataException {
        final XmlRowTransformer rowProcessInstance = DynamicValueHandlerRepositry.getRowTransformer(tableName);
        if (rowProcessInstance != null) {
            rowProcessInstance.setDisplayNames(tableName, columnVsValue);
        }
        for (final String columnName : columnVsValue.keySet()) {
            final String columnValue = columnVsValue.get(columnName);
            colEl.setAttribute(columnName.toLowerCase(Locale.ENGLISH), columnValue);
        }
    }
    
    private String getValue(final ColumnDefinition colDef, final String colValue) {
        return colValue;
    }
    
    ArrayList getUniqueValueGeneratedKeys(final String tableName) throws MetaDataException {
        try {
            final ArrayList uvgKeys = new ArrayList();
            final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
            for (final ColumnDefinition columnDef : tableDef.getColumnList()) {
                if (columnDef.getUniqueValueGeneration() != null) {
                    uvgKeys.add(columnDef.getColumnName());
                }
            }
            return uvgKeys;
        }
        catch (final MetaDataException ex) {
            ex.printStackTrace();
            throw ex;
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
            throw new RuntimeException(ex2);
        }
    }
    
    Element getElement(final Row row, final String masterTableName, final boolean bdfk) throws DOMException, MetaDataException, DynamicValueHandlingException {
        XmlCreator.LOGGER.entering(XmlCreator.CLASS_NAME, "getElementName", new Object[] { row, masterTableName, bdfk });
        final String tableName = row.getTableName();
        final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
        final List pkColumns = tableDef.getPrimaryKey().getColumnList();
        List columnNames = null;
        try {
            columnNames = (List)((ArrayList)tableDef.getColumnNames()).clone();
        }
        catch (final Exception exc) {
            throw new RuntimeException(exc);
        }
        try {
            final Element element = XmlCreator.domHandler.createElement(this.doc, tableName);
            final HashMap colVsvalue = new HashMap();
            if (this.useDVH) {
                final DynamicValueHandlerUtil util = new DynamicValueHandlerUtil();
                final HashMap dynamicValues = util.getDynamicValues(tableName, columnNames, row, this.data);
                for (final String columnName : dynamicValues.keySet()) {
                    final String columnValue = dynamicValues.get(columnName);
                    if (columnValue == null) {
                        columnNames.remove(columnName);
                    }
                    else {
                        final ColumnDefinition colDef = tableDef.getColumnDefinitionByName(columnName);
                        colVsvalue.put(columnName, this.getValue(colDef, columnValue));
                        columnNames.remove(columnName);
                    }
                }
            }
            final List fkList = tableDef.getForeignKeyList();
            if (fkList != null) {
                for (final ForeignKeyDefinition fkDef : fkList) {
                    boolean isParent = false;
                    if (bdfk && fkDef.getMasterTableName().equals(masterTableName)) {
                        isParent = true;
                        XmlCreator.LOGGER.log(Level.FINEST, "isParent:{0}", isParent);
                        XmlCreator.LOGGER.log(Level.FINEST, "masterTableName:{0}", masterTableName);
                    }
                    final List fkColumnDefs = fkDef.getForeignKeyColumns();
                    for (final ForeignKeyColumnDefinition fkColumnDef : fkColumnDefs) {
                        final String fkColumn = fkColumnDef.getLocalColumnDefinition().getColumnName();
                        XmlCreator.LOGGER.log(Level.FINEST, "ForeignKeyColumn:{0}", fkColumn);
                        final String fkRefColumnName = fkColumnDef.getReferencedColumnDefinition().getColumnName().toLowerCase();
                        final String uvgKeyString = fkDef.getMasterTableName() + ":" + fkRefColumnName + ":" + String.valueOf(row.get(fkColumn));
                        XmlCreator.LOGGER.log(Level.FINEST, "Search string:{0}", uvgKeyString);
                        XmlCreator.LOGGER.log(Level.FINEST, "uvgKeyMap:{0}", this.uvgKeyMap);
                        if (this.uvgKeyMap.containsKey(uvgKeyString) && columnNames.contains(fkColumn)) {
                            XmlCreator.LOGGER.log(Level.FINEST, "uvgKeyMap contains {0}", uvgKeyString);
                            final Object realValue = row.get(fkColumn);
                            final String template = tableName + ":" + fkColumn.toLowerCase();
                            final String key = template + ":" + realValue;
                            String fkColumnValue = template + ":" + String.valueOf(row.get(fkColumn));
                            fkColumnValue = this.uvgKeyMap.get(key);
                            final ColumnDefinition colDef2 = tableDef.getColumnDefinitionByName(fkColumn);
                            colVsvalue.put(colDef2.getColumnName(), this.getValue(colDef2, fkColumnValue));
                            this.updateUvgKeyMap(key, template, fkColumnValue, realValue);
                            XmlCreator.LOGGER.log(Level.FINEST, "{0} refers to unique value generated key. Hence removed from list.", fkColumn);
                            columnNames.remove(fkColumn);
                        }
                        else if (fkColumn.equals("CRID")) {
                            if (pkColumns.contains(fkColumn)) {
                                XmlCreator.LOGGER.log(Level.FINEST, "uvgKeyMap contains {0}", uvgKeyString);
                                final Object realValue = row.get(fkColumn);
                                final String template = tableName + ":" + fkColumn.toLowerCase();
                                final String key = template + ":" + realValue;
                                String fkColumnValue = template + ":" + String.valueOf(row.get(fkColumn));
                                fkColumnValue = this.uvgKeyMap.get(key);
                                final ColumnDefinition colDef2 = tableDef.getColumnDefinitionByName(fkColumn);
                                colVsvalue.put(colDef2.getColumnName(), this.getValue(colDef2, fkColumnValue));
                                this.updateUvgKeyMap(key, template, fkColumnValue, realValue);
                                XmlCreator.LOGGER.log(Level.FINEST, "{0} refers to unique value generated key. Hence removed from list.", fkColumn);
                                columnNames.remove(fkColumn);
                            }
                            else {
                                columnNames.remove(fkColumn);
                            }
                        }
                        else {
                            XmlCreator.LOGGER.log(Level.FINEST, "ForeignKeyColumn:{0}", fkColumn);
                            if (!isParent) {
                                continue;
                            }
                            XmlCreator.LOGGER.log(Level.FINEST, "ForeignKeyColumn:{0} is removed.", fkColumn);
                            columnNames.remove(fkColumn);
                        }
                    }
                }
            }
            final Iterator columnIterator = columnNames.iterator();
            final List uvgKeys = this.getUniqueValueGeneratedKeys(tableName);
            while (columnIterator.hasNext()) {
                final String columnName = columnIterator.next();
                final Object value = row.get(columnName);
                final ColumnDefinition cd = tableDef.getColumnDefinitionByName(columnName);
                final Object defValue = cd.getDefaultValue();
                XmlCreator.LOGGER.log(Level.FINEST, "Default value for column {0} is {1}", new Object[] { columnName, defValue });
                if (value != null) {
                    if (value.equals(defValue)) {
                        continue;
                    }
                    final Object realValue2 = row.get(columnName);
                    final String template2 = tableName + ":" + columnName.toLowerCase();
                    String columnValue2 = String.valueOf(realValue2);
                    final String key2 = template2 + ":" + realValue2;
                    final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
                    if (uvgKeys.contains(columnName)) {
                        columnValue2 = tableName + ":" + columnName.toLowerCase() + ":" + columnValue2;
                        this.updateUvgKeyMap(key2, template2, columnValue2, realValue2);
                        columnValue2 = this.uvgKeyMap.get(key2);
                    }
                    if (uvg != null && uvg.getNameColumn() != null && !this.carryOverGeneratedValues) {
                        continue;
                    }
                    final ColumnDefinition colDef3 = tableDef.getColumnDefinitionByName(columnName);
                    colVsvalue.put(columnName, this.getValue(colDef3, columnValue2));
                }
            }
            this.setColumnValue(element, colVsvalue, tableName);
            return element;
        }
        catch (final DOMException ex) {
            ex.printStackTrace();
            throw ex;
        }
        catch (final MetaDataException ex2) {
            ex2.printStackTrace();
            throw ex2;
        }
        catch (final DynamicValueHandlingException ex3) {
            ex3.printStackTrace();
            throw ex3;
        }
        catch (final Exception ex4) {
            ex4.printStackTrace();
            throw new RuntimeException(ex4);
        }
    }
    
    HashMap getColumnListToBeAdded(final Row row, final String parentTableName, final boolean singleFKNonPK) throws DOMException, MetaDataException, DynamicValueHandlingException {
        XmlCreator.LOGGER.entering(XmlCreator.CLASS_NAME, "getColumnListToBeAdded", new Object[] { row, parentTableName, singleFKNonPK });
        final String tableName = row.getTableName();
        final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
        List columnNames = null;
        try {
            columnNames = (List)((ArrayList)tableDef.getColumnNames()).clone();
        }
        catch (final Exception exc) {
            throw new RuntimeException(exc);
        }
        try {
            final HashMap columnVsValue = new HashMap();
            if (this.useDVH) {
                final DynamicValueHandlerUtil util = new DynamicValueHandlerUtil();
                final HashMap dynamicValues = util.getDynamicValues(tableName, columnNames, row, this.data);
                for (final String columnName : dynamicValues.keySet()) {
                    final String columnValue = dynamicValues.get(columnName);
                    if (columnValue == null) {
                        columnNames.remove(columnName);
                    }
                    else {
                        final ColumnDefinition colDef = tableDef.getColumnDefinitionByName(columnName);
                        columnVsValue.put(columnName, this.getValue(colDef, columnValue));
                        columnNames.remove(columnName);
                    }
                }
            }
            final List pkColumns = tableDef.getPrimaryKey().getColumnList();
            XmlCreator.LOGGER.log(Level.FINEST, "PK Columns for the {0}:{1}", new Object[] { tableName, pkColumns });
            XmlCreator.LOGGER.log(Level.FINEST, "Columns for the {0}:{1}", new Object[] { tableName, columnNames });
            final List fkList = tableDef.getForeignKeyList();
            if (fkList != null) {
                final Iterator fkIterator = fkList.iterator();
                boolean alreadyReferenced = false;
                while (fkIterator.hasNext()) {
                    boolean isParent = false;
                    final ForeignKeyDefinition fkDef = fkIterator.next();
                    if (singleFKNonPK && fkDef.getMasterTableName().equals(parentTableName)) {
                        isParent = true;
                    }
                    final List fkColumnDefs = fkDef.getForeignKeyColumns();
                    for (final ForeignKeyColumnDefinition fkColumnDef : fkColumnDefs) {
                        final String fkColumn = fkColumnDef.getLocalColumnDefinition().getColumnName();
                        XmlCreator.LOGGER.log(Level.FINEST, "ForeignKeyColumn:{0}", fkColumn);
                        if (isParent) {
                            columnNames.remove(fkColumn);
                            columnVsValue.remove(fkColumn);
                        }
                        else if (fkDef.getMasterTableName().equals(parentTableName) && !alreadyReferenced) {
                            XmlCreator.LOGGER.log(Level.FINEST, "{0} refers to the parent table {1}. Hence removed from list.", new Object[] { fkColumn, parentTableName });
                            final String fkRefColumnName = fkColumnDef.getReferencedColumnDefinition().getColumnName().toLowerCase();
                            final String uvgKeyString = fkDef.getMasterTableName() + ":" + fkRefColumnName + ":" + String.valueOf(row.get(fkColumn));
                            XmlCreator.LOGGER.log(Level.FINEST, "Search string:{0}", uvgKeyString);
                            XmlCreator.LOGGER.log(Level.FINEST, "uvgKeyMap:{0}", this.uvgKeyMap);
                            if (this.uvgKeyMap.containsKey(uvgKeyString)) {
                                XmlCreator.LOGGER.log(Level.FINEST, "uvgKeyMap contains {0}", uvgKeyString);
                                final String fkColumnValue = this.uvgKeyMap.get(uvgKeyString);
                                final Object realValue = row.get(fkColumn);
                                final String template = fkDef.getMasterTableName() + ":" + fkColumn.toLowerCase();
                                final String key = template + ":" + realValue;
                                this.updateUvgKeyMap(key, template, fkColumnValue, realValue);
                                XmlCreator.LOGGER.log(Level.FINEST, "{0} refers to unique value generated key. Hence removed from list.", fkColumn);
                            }
                            columnNames.remove(fkColumn);
                            columnVsValue.remove(fkColumn);
                        }
                        else if (fkColumn.equals("CRID")) {
                            XmlCreator.LOGGER.log(Level.FINEST, "Foreign key column is {0}. Hence removed from list.", fkColumn);
                            columnNames.remove(fkColumn);
                            columnVsValue.remove(fkColumn);
                        }
                        else {
                            final String fkRefColumnName = fkColumnDef.getReferencedColumnDefinition().getColumnName().toLowerCase();
                            final String uvgKeyString = fkDef.getMasterTableName() + ":" + fkRefColumnName + ":" + String.valueOf(row.get(fkColumn));
                            XmlCreator.LOGGER.log(Level.FINEST, "Search string:{0}", uvgKeyString);
                            XmlCreator.LOGGER.log(Level.FINEST, "uvgKeyMap:{0}", this.uvgKeyMap);
                            if (!this.uvgKeyMap.containsKey(uvgKeyString) || !columnNames.contains(fkColumn)) {
                                continue;
                            }
                            XmlCreator.LOGGER.log(Level.FINEST, "uvgKeyMap contains {0}", uvgKeyString);
                            final String fkColumnValue = this.uvgKeyMap.get(uvgKeyString);
                            final ColumnDefinition colDef2 = tableDef.getColumnDefinitionByName(fkColumn);
                            columnVsValue.put(fkColumn, this.getValue(colDef2, fkColumnValue));
                            final Object realValue2 = row.get(fkColumn);
                            final String template2 = fkDef.getMasterTableName() + ":" + fkColumn.toLowerCase();
                            final String key2 = template2 + ":" + realValue2;
                            this.updateUvgKeyMap(key2, template2, fkColumnValue, realValue2);
                            XmlCreator.LOGGER.log(Level.FINEST, "{0} refers to unique value generated key. Hence removed from list.", fkColumn);
                            columnNames.remove(fkColumn);
                        }
                    }
                    if (fkDef.getMasterTableName().equals(parentTableName) && !alreadyReferenced) {
                        alreadyReferenced = true;
                    }
                }
            }
            else {
                columnNames.removeAll(pkColumns);
            }
            XmlCreator.LOGGER.log(Level.FINEST, "Remaining columns:{0}", columnNames);
            XmlCreator.LOGGER.log(Level.FINEST, "Row:{0}", row);
            final Iterator columnIterator = columnNames.iterator();
            final List uvgKeys = this.getUniqueValueGeneratedKeys(tableName);
            while (columnIterator.hasNext()) {
                final String columnName2 = columnIterator.next();
                final ColumnDefinition cd = tableDef.getColumnDefinitionByName(columnName2);
                final Object defValue = cd.getDefaultValue();
                XmlCreator.LOGGER.log(Level.FINEST, "Default value for column {0} is {1}", new Object[] { columnName2, defValue });
                final Object value = row.get(columnName2);
                if (value != null) {
                    if (value.equals(defValue)) {
                        continue;
                    }
                    final Object realValue3 = row.get(columnName2);
                    String columnValue2 = String.valueOf(realValue3);
                    final String template3 = tableName + ":" + columnName2.toLowerCase();
                    final String key3 = template3 + ":" + realValue3;
                    if (uvgKeys.contains(columnName2)) {
                        columnValue2 = template3 + ":" + columnValue2;
                        this.updateUvgKeyMap(key3, template3, columnValue2, realValue3);
                        columnValue2 = this.uvgKeyMap.get(key3);
                    }
                    final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
                    if (uvg == null || uvg.getNameColumn() == null) {
                        final ColumnDefinition colDef2 = tableDef.getColumnDefinitionByName(columnName2);
                        columnVsValue.put(columnName2, this.getValue(colDef2, columnValue2));
                    }
                    else {
                        XmlCreator.LOGGER.fine("Column Name :: Parent :: " + columnName2);
                    }
                    XmlCreator.LOGGER.log(Level.FINEST, "columnname Vs value:{0}", columnVsValue);
                }
            }
            XmlCreator.LOGGER.exiting(XmlCreator.CLASS_NAME, "getColumnListToBeAdded", columnVsValue);
            return columnVsValue;
        }
        catch (final DOMException ex) {
            ex.printStackTrace();
            throw ex;
        }
        catch (final MetaDataException ex2) {
            ex2.printStackTrace();
            throw ex2;
        }
        catch (final DynamicValueHandlingException ex3) {
            ex3.printStackTrace();
            throw ex3;
        }
        catch (final Exception ex4) {
            ex4.printStackTrace();
            throw new RuntimeException(ex4);
        }
    }
    
    void removeElementsIfNoChild(final ArrayList list) {
        try {
            for (final Element element : list) {
                element.normalize();
                if (!element.hasChildNodes()) {
                    XmlCreator.LOGGER.log(Level.FINEST, "Element has no childs, hence removed.{0}", element);
                    element.getParentNode().removeChild(element);
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    private void fillUvgKeyMap(final DataObject data) throws Exception {
        final List tableNames = PersistenceUtil.sortTables(data.getTableNames());
        for (final String tableName : tableNames) {
            final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
            for (final ColumnDefinition columnDef : tableDef.getColumnList()) {
                if (columnDef.getUniqueValueGeneration() != null) {
                    final UniqueValueGeneration uvg = columnDef.getUniqueValueGeneration();
                    final String columnName = columnDef.getColumnName();
                    final Iterator rowIterator = data.getRows(tableName);
                    final String template = tableName + ":" + columnName.toLowerCase();
                    while (rowIterator.hasNext()) {
                        String columnValue = tableName + ":" + columnName.toLowerCase() + ":";
                        final Row row = rowIterator.next();
                        final String rowValue = String.valueOf(row.get(columnName));
                        final String key = template + ":" + rowValue;
                        if (uvg.getNameColumn() != null && row.get(uvg.getNameColumn()) != null) {
                            columnValue += row.get(uvg.getNameColumn());
                        }
                        else {
                            columnValue += rowValue;
                        }
                        this.updateUvgKeyMap(key, template, columnValue, rowValue);
                    }
                }
            }
            final List fkList = tableDef.getForeignKeyList();
            if (fkList == null) {
                continue;
            }
            for (final ForeignKeyDefinition fkDef : fkList) {
                final List fkColumnDefs = fkDef.getForeignKeyColumns();
                for (final ForeignKeyColumnDefinition fkColumnDef : fkColumnDefs) {
                    final String fkColumn = fkColumnDef.getLocalColumnDefinition().getColumnName();
                    XmlCreator.LOGGER.log(Level.FINEST, "ForeignKeyColumn:{0}", fkColumn);
                    if (fkColumn.equals("CRID")) {
                        XmlCreator.LOGGER.log(Level.FINEST, "Foreign key column is {0}.", fkColumn);
                    }
                    else {
                        final Iterator rowIterator2 = data.getRows(tableName);
                        while (rowIterator2.hasNext()) {
                            final Row row2 = rowIterator2.next();
                            final String fkRefColumnName = fkColumnDef.getReferencedColumnDefinition().getColumnName().toLowerCase();
                            final String uvgKeyString = fkDef.getMasterTableName() + ":" + fkRefColumnName + ":" + String.valueOf(row2.get(fkColumn));
                            XmlCreator.LOGGER.log(Level.FINEST, "Search string:{0}", uvgKeyString);
                            XmlCreator.LOGGER.log(Level.FINEST, "uvgKeyMap:{0}", this.uvgKeyMap);
                            if (this.uvgKeyMap.containsKey(uvgKeyString)) {
                                XmlCreator.LOGGER.log(Level.FINEST, "uvgKeyMap contains {0}", uvgKeyString);
                                final String fkColumnValue = this.uvgKeyMap.get(uvgKeyString);
                                final Object realValue = row2.get(fkColumn);
                                final String template2 = tableName + ":" + fkColumn.toLowerCase();
                                final String key2 = template2 + ":" + realValue;
                                this.updateUvgKeyMap(key2, template2, fkColumnValue, realValue);
                                XmlCreator.LOGGER.log(Level.FINEST, "{0} refers to unique value generated key.", fkColumn);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void updateUvgKeyMap(final String key, final String template, final String fkColumnValue, final Object realValue) {
        XmlCreator.LOGGER.log(Level.FINEST, "updateUvgKeyMap key = {0}  template = {1}  fkColumnValue = {2}  realValue = {3} ", new Object[] { key, template, fkColumnValue, realValue });
        if (this.carryOverGeneratedValues) {
            this.uvgKeyMap.put(key, String.valueOf(realValue));
        }
        else {
            final String uvhMapKey = template + ":" + String.valueOf(realValue);
            if (this.uvhMap != null && this.uvhMap.get(uvhMapKey) != null) {
                this.uvgKeyMap.put(key, this.uvhMap.get(uvhMapKey));
            }
            else {
                this.uvgKeyMap.put(key, fkColumnValue);
            }
        }
    }
    
    static {
        CLASS_NAME = XmlCreator.class.getName();
        LOGGER = Logger.getLogger(XmlCreator.CLASS_NAME);
        XmlCreator.domHandler = new DOMHandler();
    }
}
