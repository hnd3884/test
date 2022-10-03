package com.adventnet.db.persistence.metadata.parser;

import com.zoho.conf.tree.ConfTreeBuilder;
import com.zoho.conf.tree.ConfTree;
import java.io.File;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.List;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import java.util.logging.Logger;

public class ParserUtil
{
    private static final Logger OUT;
    
    private ParserUtil() {
    }
    
    public static String getAttribute(final Element el, final String attName) {
        final NamedNodeMap attrs = el.getAttributes();
        final Attr nameAttr = (Attr)attrs.getNamedItem("name");
        if (nameAttr != null) {
            return nameAttr.getValue();
        }
        return null;
    }
    
    public static String getTextValue(final Element el, final String tagName) {
        final NodeList list = el.getElementsByTagName(tagName);
        if (list.getLength() != 0) {
            final Element element = (Element)list.item(0);
            final String t = getTextNodeVal(element);
            return t;
        }
        return null;
    }
    
    public static String getTextNodeVal(final Element element) {
        String nodeVal = null;
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 3) {
                final Text tNode = (Text)node;
                nodeVal = tNode.getData();
            }
        }
        return nodeVal;
    }
    
    static boolean getTextNodeValAsBoolean(final Element element) {
        final String textNodeStr = getTextNodeVal(element);
        return textNodeStr != null && textNodeStr.trim().equalsIgnoreCase("true");
    }
    
    static int getTextNodeValAsInt(final Element element) {
        final String textNodeStr = getTextNodeVal(element);
        if (textNodeStr == null) {
            return 0;
        }
        return Integer.parseInt(textNodeStr.trim());
    }
    
    public static void validateFK(final TableDefinition parent, final TableDefinition child, final ForeignKeyDefinition fkdef, final List<ColumnDefinition> newColumns) {
        if (!child.isTemplate() && parent.isTemplate()) {
            throw new IllegalArgumentException("A non-template(child) table cannot refer a template(parent) table. Hence this FK " + fkdef + " is not valid");
        }
        final String fkName = fkdef.getName();
        if (fkName == null) {
            throw new IllegalArgumentException("The Name of the Foreignkey Definition in the table " + child.getTableName() + " should not be null");
        }
        if (fkName.length() > 60) {
            throw new IllegalArgumentException("The FKname \"" + fkName + "\" size \"" + fkName.length() + "\" should not exceed " + 60 + " in table \"" + child.getTableName() + "\".");
        }
        final List<ForeignKeyColumnDefinition> fkcols = fkdef.getForeignKeyColumns();
        if (fkcols.isEmpty()) {
            throw new IllegalArgumentException("Foreign key \"" + fkdef.getName() + "\" of table \"" + parent.getTableName() + "\" doesn't contain foreign-key columns.");
        }
        ColumnDefinition parentColumn = null;
        ColumnDefinition childColumn = null;
        for (final ForeignKeyColumnDefinition fkcol : fkcols) {
            parentColumn = parent.getColumnDefinitionByName(fkcol.getReferencedColumnDefinition().getColumnName());
            childColumn = child.getColumnDefinitionByName(fkcol.getLocalColumnDefinition().getColumnName());
            if (parentColumn == null) {
                throw new IllegalArgumentException("Parent Column \"" + parent.getTableName() + "." + fkcol.getReferencedColumnDefinition().getColumnName() + "\" defined in the foreign-key \"" + fkName + "\" doesn't exists.");
            }
            if (childColumn == null && newColumns != null) {
                for (final ColumnDefinition cd : newColumns) {
                    if (cd.getColumnName().equals(fkcol.getLocalColumnDefinition().getColumnName())) {
                        childColumn = cd;
                        break;
                    }
                }
            }
            if (childColumn == null) {
                throw new IllegalArgumentException("Child Column \"" + child.getTableName() + "." + fkcol.getLocalColumnDefinition().getColumnName() + "\" defined in the foreign-key \"" + fkName + "\" doesn't exists.");
            }
            if (!childColumn.getDataType().equals(parentColumn.getDataType())) {
                if (!DataTypeUtil.isUDT(parentColumn.getDataType())) {
                    throw new IllegalArgumentException("The Parent Column \"" + parentColumn.getTableName() + "." + parentColumn.getColumnName() + "\" dataType \"" + parentColumn.getDataType() + "\" and the child column \"" + childColumn.getTableName() + "." + childColumn.getColumnName() + "\" dataType \"" + childColumn.getDataType() + "\" are not same in the foreign-key \"" + fkName + "\".");
                }
                if (!DataTypeManager.getDataTypeDefinition(parentColumn.getDataType()).getMeta().isReferenceable()) {
                    throw new IllegalArgumentException("Parent is not referenceable! for the data type [" + parentColumn.getDataType() + "]");
                }
                if (null == DataTypeManager.getDataTypeDefinition(parentColumn.getDataType()).getMeta().referenceableTypes() || !DataTypeManager.getDataTypeDefinition(parentColumn.getDataType()).getMeta().referenceableTypes().contains(childColumn.getDataType())) {
                    throw new IllegalArgumentException("Child data type :" + childColumn.getDataType() + " cannot be refenced for parent data type : " + parentColumn.getDataType());
                }
            }
            if (childColumn.getDataType().equals("BLOB") || childColumn.getDataType().equals("SBLOB") || childColumn.getDataType().equals("SCHAR")) {
                throw new IllegalArgumentException("\"" + childColumn.getDataType() + "\" dataType column \"" + childColumn.getTableName() + "." + childColumn.getColumnName() + "\" cannot participate in foreign-key \"" + fkName + "\".");
            }
            if (!childColumn.getDataType().equals("CHAR") && !childColumn.getDataType().equals("NCHAR")) {
                continue;
            }
            if (childColumn.getMaxLength() < 0 || childColumn.getMaxLength() > 255) {
                throw new IllegalArgumentException("CHAR column \"" + childColumn.getTableName() + "." + childColumn.getColumnName() + "\" with max-size " + childColumn.getMaxLength() + " greater than 255 or lesser than 0 cannot participate in foreign-key \"" + fkName + "\".");
            }
            if (childColumn.getMaxLength() != parentColumn.getMaxLength()) {
                throw new IllegalArgumentException("The Parent Column \"" + parentColumn.getTableName() + "." + parentColumn.getColumnName() + "\" max-size \"" + parentColumn.getMaxLength() + "\" and the child column '" + childColumn.getTableName() + "." + childColumn.getColumnName() + "\" max-size \"" + childColumn.getMaxLength() + "\" are not same in the foreign-key \"" + fkName + "\".");
            }
        }
        final List<String> fkrefCols = fkdef.getFkRefColumns();
        boolean unique = parent.getPrimaryKey().getColumnList().equals(fkrefCols);
        if (!unique && parent.getUniqueKeys() != null) {
            for (final UniqueKeyDefinition ukdef : parent.getUniqueKeys()) {
                if (unique = ukdef.getColumns().equals(fkrefCols)) {
                    break;
                }
            }
        }
        if (!unique) {
            throw new IllegalArgumentException("The Parent columns " + fkrefCols + " referred in the foreign-key \"" + fkName + "\" of table \"" + child.getTableName() + "\" were not unique.");
        }
    }
    
    public static void validatePrimaryKey(final TableDefinition td, final PrimaryKeyDefinition pkDef, final List<ColumnDefinition> newColumns) {
        validateKeyDetails(td, pkDef.getName(), pkDef.getColumnList(), newColumns);
    }
    
    public static void validateIndexDefinition(final TableDefinition td, final IndexDefinition idxDef, final List<ColumnDefinition> newColumns) {
        validateKeyDetails(td, idxDef.getName(), idxDef.getColumns(), newColumns);
    }
    
    public static void validateUniqueKey(final TableDefinition td, final UniqueKeyDefinition ukDef, final List<ColumnDefinition> newColumns) {
        validateKeyDetails(td, ukDef.getName(), ukDef.getColumns(), newColumns);
    }
    
    private static void validateKeyDetails(final TableDefinition td, final String name, final List<String> columnNames, final List<ColumnDefinition> newColumns) {
        final String tableName = td.getTableName();
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("The constraintName cannot be null for table \"" + tableName + "\".");
        }
        if (name.length() > 60) {
            throw new IllegalArgumentException("The constraintName :: \"" + name + "\" of tableName :: \"" + tableName + "\" has [" + name.length() + "] characters but it should not exceed " + 60 + ".");
        }
        if (columnNames.isEmpty()) {
            throw new IllegalArgumentException("The constraint with name " + name + " cannot be created without columns.");
        }
        for (final String columnName : columnNames) {
            ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
            if (cd == null && newColumns != null) {
                for (final ColumnDefinition colDef : newColumns) {
                    if (colDef.getColumnName().equals(columnName)) {
                        cd = colDef;
                        break;
                    }
                }
            }
            if (cd == null) {
                throw new IllegalArgumentException("UnKnown Column Name specified \"" + columnName + "\" in constraint \"" + name + "\" for table \"" + tableName + "\".");
            }
            final String dataType = cd.getDataType();
            if (dataType.equals("BLOB") || dataType.equals("SBLOB") || dataType.equals("SCHAR")) {
                throw new IllegalArgumentException("BLOB/TEXT dataType column \"" + tableName + "\".\"" + columnName + "\" cannot participate in constraint \"" + name + "\".");
            }
            if ((dataType.equals("CHAR") || dataType.equals("NCHAR")) && cd.getMaxLength() > 255) {
                throw new IllegalArgumentException("CHAR Column \"" + tableName + "\".\"" + cd.getColumnName() + "\" with max-size greater than 255 cannot be participate in  constraint \"" + name + "\".");
            }
        }
    }
    
    public static ConfTree parseExtendedDDConfTree(final File file) {
        if (file.exists()) {
            try {
                return ((ConfTreeBuilder)ConfTreeBuilder.confTree().fromConfFile(file.getPath())).build();
            }
            catch (final Exception e) {
                ParserUtil.OUT.severe("Error occurred while constructing conftree from extended-dd.conf, Hence extended values will be skipped");
                return null;
            }
        }
        ParserUtil.OUT.info("file " + file.getPath() + " does not exist.. hence returning null");
        return null;
    }
    
    static {
        OUT = Logger.getLogger(ParserUtil.class.getName());
    }
}
