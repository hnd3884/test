package com.adventnet.db.persistence.metadata;

import java.util.Iterator;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import org.w3c.dom.Text;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NodeList;

public class ElementTransformer
{
    static List<String> getNames(final NodeList nodes, final String tagName) {
        final List<String> Names = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                final Element nodeElement = (Element)node;
                if (nodeElement.getTagName().equals(tagName)) {
                    final NamedNodeMap attris = nodeElement.getAttributes();
                    final Attr nameattr = (Attr)attris.getNamedItem("name");
                    final String columnname = nameattr.getValue();
                    Names.add(columnname);
                }
            }
        }
        return Names;
    }
    
    static String getTagValue(final NodeList nodes, final String tagName) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)node;
                if (element.getTagName().equals(tagName)) {
                    final NodeList innerNodes = element.getChildNodes();
                    for (int j = 0; j < innerNodes.getLength(); ++j) {
                        final Node innerNode = innerNodes.item(j);
                        if (innerNode.getNodeType() == 3) {
                            return ((Text)innerNode).getData();
                        }
                    }
                }
            }
        }
        return null;
    }
    
    static List<String> getValuesList(final NodeList nodes) {
        final List<String> values = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element element = (Element)node;
                    if (element.getTagName().equals("value")) {
                        final NodeList innernodes = element.getChildNodes();
                        for (int j = 0; j < innernodes.getLength(); ++j) {
                            final Node innernode = innernodes.item(j);
                            if (innernode.getNodeType() == 3) {
                                values.add(((Text)innernode).getData());
                            }
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return values;
    }
    
    static List<String> getColumns(final NodeList nodes, final String tagName) {
        final List<String> Columns = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element element = (Element)node;
                    if (element.getTagName().equals(tagName)) {
                        final NodeList innernodes = element.getChildNodes();
                        for (int j = 0; j < innernodes.getLength(); ++j) {
                            final Node innernode = innernodes.item(j);
                            if (innernode.getNodeType() == 3) {
                                Columns.add(((Text)innernode).getData());
                            }
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return Columns;
    }
    
    static Element getElement(final NodeList nodes, final String elementName) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)node;
                if (element.getTagName().equals(elementName)) {
                    return element;
                }
            }
        }
        return null;
    }
    
    static Element getElementByName(final NodeList nodes, final String elementName, final String name) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                final Element element = (Element)node;
                final String tagName = element.getTagName();
                if (tagName.equals(elementName)) {
                    final NamedNodeMap attris = element.getAttributes();
                    final Attr nameattr = (Attr)attris.getNamedItem("name");
                    final String Name = nameattr.getValue();
                    if (Name.equals(name)) {
                        return element;
                    }
                }
            }
        }
        return null;
    }
    
    static List<ForeignKeyColumnDefinition> getFKColumns(final NodeList nodes) {
        final List<ForeignKeyColumnDefinition> fkColumns = new ArrayList<ForeignKeyColumnDefinition>();
        for (int j = 0; j < nodes.getLength(); ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    final String tagName = nodeElement.getTagName();
                    if (tagName.equals("fk-column")) {
                        final NodeList nodelist = nodeElement.getChildNodes();
                        final ForeignKeyColumnDefinition fkcd = new ForeignKeyColumnDefinition();
                        final ColumnDefinition localColumnDefinition = new ColumnDefinition();
                        final ColumnDefinition referenceColumnDefinition = new ColumnDefinition();
                        for (int i = 0; i < nodelist.getLength(); ++i) {
                            final Node nod = nodelist.item(i);
                            if (nod.getNodeType() == 1) {
                                final Element element = (Element)nod;
                                if (element.getTagName().equals("fk-local-column")) {
                                    final NodeList innernodes = element.getChildNodes();
                                    for (int k = 0; k < innernodes.getLength(); ++k) {
                                        final Node innernode = innernodes.item(k);
                                        if (innernode.getNodeType() == 3) {
                                            localColumnDefinition.setColumnName(((Text)innernode).getData());
                                        }
                                    }
                                }
                                else if (element.getTagName().equals("fk-reference-column")) {
                                    final NodeList innernodes = element.getChildNodes();
                                    for (int k = 0; k < innernodes.getLength(); ++k) {
                                        final Node innernode = innernodes.item(k);
                                        if (innernode.getNodeType() == 3) {
                                            referenceColumnDefinition.setColumnName(((Text)innernode).getData());
                                        }
                                    }
                                }
                            }
                        }
                        fkcd.setLocalColumnDefinition(localColumnDefinition);
                        fkcd.setReferencedColumnDefinition(referenceColumnDefinition);
                        fkColumns.add(fkcd);
                        break;
                    }
                    break;
                }
            }
        }
        return fkColumns;
    }
    
    public static DataDictionary getDataDictionary(final URL ddUrl) throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        builderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        builderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        final DocumentBuilder builder = builderFactory.newDocumentBuilder();
        InputStream urlStream = null;
        try {
            urlStream = ddUrl.openStream();
            final Document document = builder.parse(urlStream);
            return getDataDictionary(document.getDocumentElement());
        }
        finally {
            if (urlStream != null) {
                urlStream.close();
            }
        }
    }
    
    public static DataDictionary getDataDictionary(final Element dataDictionaryElement) {
        final String ddName = dataDictionaryElement.getAttribute("name");
        final DataDictionary dd = new DataDictionary(ddName);
        if (dataDictionaryElement.getAttribute("template-meta-handler") != null && dataDictionaryElement.getAttribute("template-meta-handler").length() > 0) {
            dd.setTemplateMetaHandler(dataDictionaryElement.getAttribute("template-meta-handler"));
        }
        if (dataDictionaryElement.getAttribute("dc-type") != null && dataDictionaryElement.getAttribute("dc-type").length() > 0) {
            dd.setDynamicColumnType(dataDictionaryElement.getAttribute("dc-type"));
        }
        dd.setDescription(getTagValue(dataDictionaryElement.getChildNodes(), "description"));
        for (final String name : getNames(dataDictionaryElement.getChildNodes(), "table")) {
            final TableDefinition td = getTableDefinition(getElementByName(dataDictionaryElement.getChildNodes(), "table", name));
            dd.addTableDefinition(td);
        }
        return dd;
    }
    
    public static TableDefinition getTableDefinition(final Element tableElement) {
        final String tableName = tableElement.getAttribute("name");
        final String moduleName = ((Element)tableElement.getParentNode()).getAttribute("name");
        boolean createTable = true;
        if (tableElement.getAttribute("createtable") != null && tableElement.getAttribute("createtable").length() > 0) {
            createTable = Boolean.valueOf(tableElement.getAttribute("createtable"));
        }
        final boolean isSystem = false;
        final boolean template = Boolean.valueOf(tableElement.getAttribute("template"));
        final TableDefinition td = new TableDefinition(isSystem, createTable);
        td.setModuleName(moduleName);
        td.setTableName(tableName);
        if (tableElement.getAttribute("display-name") != null && tableElement.getAttribute("display-name").length() > 0) {
            td.setDisplayName(tableElement.getAttribute("display-name"));
        }
        td.setTemplate(template);
        final String dcType = tableElement.getAttribute("dc-type");
        if (!dcType.isEmpty()) {
            td.setDynamicColumnType(dcType);
        }
        td.setDescription(getTagValue(tableElement.getChildNodes(), "description"));
        final Element cols = getElement(tableElement.getChildNodes(), "columns");
        for (final String name : getNames(cols.getChildNodes(), "column")) {
            final ColumnDefinition column = getColumnDefinition(getElementByName(cols.getChildNodes(), "column", name));
            td.addColumnDefinition(column);
            if (column.isUnique()) {
                final UniqueKeyDefinition ukd = new UniqueKeyDefinition(true);
                final List ukList = td.getUniqueKeys();
                final int ukNo = (ukList == null) ? 0 : ukList.size();
                ukd.setName(td.getTableName() + "_UK" + String.valueOf(ukNo));
                ukd.addColumn(column.getColumnName());
                td.addUniqueKey(ukd);
            }
        }
        final PrimaryKeyDefinition pk = getPrimaryKeyDefinition(getElement(tableElement.getChildNodes(), "primary-key"));
        td.setPrimaryKey(pk);
        final Element fks = getElement(tableElement.getChildNodes(), "foreign-keys");
        if (fks != null) {
            for (final String name2 : getNames(fks.getChildNodes(), "foreign-key")) {
                final ForeignKeyDefinition fk = getForeignKeyDefinition(getElementByName(fks.getChildNodes(), "foreign-key", name2));
                final List<ForeignKeyColumnDefinition> fkCols = fk.getForeignKeyColumns();
                for (final ForeignKeyColumnDefinition fkCol : fkCols) {
                    fkCol.setLocalColumnDefinition(td.getColumnDefinitionByName(fkCol.getLocalColumnDefinition().getColumnName()));
                }
                td.addForeignKey(fk);
            }
        }
        final Element uks = getElement(tableElement.getChildNodes(), "unique-keys");
        if (uks != null) {
            for (final String name3 : getNames(uks.getChildNodes(), "unique-key")) {
                final UniqueKeyDefinition uk = getUniqueKeyDefinition(getElementByName(uks.getChildNodes(), "unique-key", name3));
                td.addUniqueKey(uk);
            }
        }
        final List<String> uksToBeRemoved = new ArrayList<String>();
        List<UniqueKeyDefinition> ukds = td.getUniqueKeys();
        if (ukds != null) {
            for (final UniqueKeyDefinition ukDef : ukds) {
                if (pk.getColumnList().equals(ukDef.getColumns())) {
                    uksToBeRemoved.add(ukDef.getName());
                }
            }
        }
        if (!uksToBeRemoved.isEmpty()) {
            for (final String ukName : uksToBeRemoved) {
                td.removeUniqueKey(ukName);
            }
        }
        final Element idxs = getElement(tableElement.getChildNodes(), "indexes");
        if (idxs != null) {
            for (final String name4 : getNames(idxs.getChildNodes(), "index")) {
                final IndexDefinition idx = getIndexDefinition(getElementByName(idxs.getChildNodes(), "index", name4), td);
                boolean addThisIndex = true;
                if (pk.getColumnList().equals(idx.getColumns())) {
                    addThisIndex = false;
                }
                ukds = td.getUniqueKeys();
                if (ukds != null) {
                    for (final UniqueKeyDefinition ukDef2 : ukds) {
                        if (ukDef2.getColumns().equals(idx.getColumns())) {
                            addThisIndex = false;
                            break;
                        }
                    }
                }
                final List<ForeignKeyDefinition> fkDefs = td.getForeignKeyList();
                for (final ForeignKeyDefinition fkDef : fkDefs) {
                    if (fkDef.getFkColumns().equals(idx.getColumns())) {
                        addThisIndex = false;
                        break;
                    }
                }
                final List<IndexDefinition> indexes = td.getIndexes();
                if (indexes != null) {
                    for (final IndexDefinition idxDef : indexes) {
                        if (idxDef.getColumns().equals(idx.getColumns())) {
                            addThisIndex = false;
                            break;
                        }
                    }
                }
                if (addThisIndex) {
                    td.addIndex(idx);
                }
            }
        }
        return td;
    }
    
    public static ColumnDefinition getColumnDefinition(final Element columnElement) {
        final ColumnDefinition colDef = new ColumnDefinition();
        colDef.setColumnName(columnElement.getAttribute("name"));
        if (columnElement.getAttribute("display-name") != null && columnElement.getAttribute("display-name").length() > 0) {
            colDef.setDisplayName(columnElement.getAttribute("display-name"));
        }
        colDef.setDescription(getTagValue(columnElement.getChildNodes(), "description"));
        colDef.setDataType(getTagValue(columnElement.getChildNodes(), "data-type"));
        final String maxSize = getTagValue(columnElement.getChildNodes(), "max-size");
        if (maxSize != null) {
            colDef.setMaxLength(Integer.parseInt(maxSize));
        }
        final String precision = getTagValue(columnElement.getChildNodes(), "precision");
        if (precision != null) {
            colDef.setPrecision(Integer.parseInt(precision));
        }
        try {
            final String defaultValue = getTagValue(columnElement.getChildNodes(), "default-value");
            if (defaultValue != null) {
                colDef.setDefaultValue(defaultValue);
            }
        }
        catch (final MetaDataException e) {
            throw new IllegalArgumentException(e);
        }
        final String nullable = getTagValue(columnElement.getChildNodes(), "nullable");
        if (nullable != null) {
            final boolean isNullable = Boolean.valueOf(nullable);
            colDef.setNullable(isNullable);
        }
        final boolean isUnique = Boolean.valueOf(getTagValue(columnElement.getChildNodes(), "unique"));
        colDef.setUnique(isUnique);
        final Element avElement = getElement(columnElement.getChildNodes(), "allowed-values");
        if (avElement != null) {
            colDef.setAllowedValues(getAllowedValues(avElement));
        }
        final Element uvgElement = getElement(columnElement.getChildNodes(), "uniquevalue-generation");
        if (uvgElement != null) {
            colDef.setUniqueValueGeneration(getUniqueValueGeneration(uvgElement));
        }
        return colDef;
    }
    
    public static PrimaryKeyDefinition getPrimaryKeyDefinition(final Element pkElement) {
        final PrimaryKeyDefinition pkDef = new PrimaryKeyDefinition();
        pkDef.setName(pkElement.getAttribute("name"));
        for (final String pkCol : getColumns(pkElement.getChildNodes(), "primary-key-column")) {
            pkDef.addColumnName(pkCol);
        }
        return pkDef;
    }
    
    public static ForeignKeyDefinition getForeignKeyDefinition(final Element fkElement) {
        final ForeignKeyDefinition fkDef = new ForeignKeyDefinition();
        fkDef.setName(fkElement.getAttribute("name"));
        fkDef.setMasterTableName(fkElement.getAttribute("reference-table-name"));
        final boolean isBidirectional = Boolean.valueOf(fkElement.getAttribute("isbidirectional"));
        fkDef.setBidirectional(isBidirectional);
        fkDef.setDescription(getTagValue(fkElement.getChildNodes(), "description"));
        final String constraint = getTagValue(fkElement.getChildNodes(), "fk-constraints");
        if (constraint != null) {
            if (constraint.equalsIgnoreCase("ON-DELETE-CASCADE")) {
                fkDef.setConstraints(1);
            }
            else if (constraint.equalsIgnoreCase("ON-DELETE-SET-DEFAULT")) {
                fkDef.setConstraints(3);
            }
            else if (constraint.equalsIgnoreCase("ON-DELETE-SET-NULL")) {
                fkDef.setConstraints(2);
            }
            else if (constraint.equalsIgnoreCase("ON-DELETE-RESTRICT")) {
                fkDef.setConstraints(0);
            }
        }
        final List<ForeignKeyColumnDefinition> fkcds = getFKColumns(getElement(fkElement.getChildNodes(), "fk-columns").getChildNodes());
        for (final ForeignKeyColumnDefinition fkcd : fkcds) {
            fkDef.addForeignKeyColumns(fkcd);
        }
        return fkDef;
    }
    
    public static UniqueKeyDefinition getUniqueKeyDefinition(final Element ukElement) {
        final UniqueKeyDefinition ukDef = new UniqueKeyDefinition();
        ukDef.setName(ukElement.getAttribute("name"));
        for (final String ukCol : getColumns(ukElement.getChildNodes(), "unique-key-column")) {
            ukDef.addColumn(ukCol);
        }
        return ukDef;
    }
    
    @Deprecated
    public static IndexDefinition getIndexDefinition(final Element idxElement) {
        return getIndexDefinition(idxElement, null);
    }
    
    public static IndexDefinition getIndexDefinition(final Element idxElement, final TableDefinition td) {
        final IndexDefinition idxDef = new IndexDefinition();
        idxDef.setName(idxElement.getAttribute("name"));
        for (int i = 0; i < idxElement.getChildNodes().getLength(); ++i) {
            String columnName = null;
            int size = -1;
            Boolean isAscending = true;
            Boolean isNullsFirst = null;
            final Node node = idxElement.getChildNodes().item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element element = (Element)node;
                    if (element.getTagName().equals("index-column")) {
                        final NodeList innernodes = element.getChildNodes();
                        for (int j = 0; j < innernodes.getLength(); ++j) {
                            final Node innernode = innernodes.item(j);
                            if (innernode.getNodeType() == 3) {
                                columnName = ((Text)innernode).getData();
                            }
                        }
                        if (element.hasAttribute("size")) {
                            size = Integer.parseInt(element.getAttribute("size"));
                        }
                        if (element.hasAttribute("isAscending")) {
                            isAscending = Boolean.valueOf(element.getAttribute("isAscending"));
                        }
                        if (element.hasAttribute("isNullsFirst")) {
                            isNullsFirst = Boolean.valueOf(element.getAttribute("isNullsFirst"));
                        }
                        IndexColumnDefinition idxColDef;
                        if (td != null) {
                            idxColDef = new IndexColumnDefinition(td.getColumnDefinitionByName(columnName), size, isAscending, isNullsFirst);
                        }
                        else {
                            final ColumnDefinition cd = new ColumnDefinition();
                            cd.setColumnName(columnName);
                            idxColDef = new IndexColumnDefinition(cd, size, isAscending, isNullsFirst);
                        }
                        idxDef.addIndexColumnDefinition(idxColDef);
                        break;
                    }
                    break;
                }
            }
        }
        return idxDef;
    }
    
    private static AllowedValues getAllowedValues(final Element allowedValuesElement) {
        final AllowedValues allowedValues = new AllowedValues();
        String tagValue = null;
        tagValue = getTagValue(allowedValuesElement.getChildNodes(), "from");
        if (null != tagValue) {
            allowedValues.setFromVal(tagValue);
        }
        tagValue = getTagValue(allowedValuesElement.getChildNodes(), "to");
        if (null != tagValue) {
            allowedValues.setToVal(tagValue);
        }
        final String pattern = getTagValue(allowedValuesElement.getChildNodes(), "pattern");
        if (pattern != null) {
            allowedValues.setPattern(pattern);
        }
        for (final String value : getValuesList(allowedValuesElement.getChildNodes())) {
            allowedValues.addValue(value);
        }
        return allowedValues;
    }
    
    private static UniqueValueGeneration getUniqueValueGeneration(final Element uniqueValuegeneratorElement) {
        final UniqueValueGeneration uvg = new UniqueValueGeneration();
        uvg.setGeneratorName(getTagValue(uniqueValuegeneratorElement.getChildNodes(), "generator-name"));
        uvg.setNameColumn(getTagValue(uniqueValuegeneratorElement.getChildNodes(), "name-column"));
        uvg.setGeneratorClass(getTagValue(uniqueValuegeneratorElement.getChildNodes(), "generator-class"));
        uvg.setInstanceSpecificSequenceGenerator(Boolean.parseBoolean(getTagValue(uniqueValuegeneratorElement.getChildNodes(), "instancespecific-seqgen")));
        final boolean isTemplateTable = Boolean.valueOf(((Element)uniqueValuegeneratorElement.getParentNode().getParentNode().getParentNode()).getAttribute("template"));
        if (isTemplateTable) {
            uvg.setGeneratorType(2);
        }
        return uvg;
    }
}
