package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Hashtable;

public class CommentTableObject
{
    private String schema;
    private String tableOrView;
    private String column;
    private Hashtable levelTypeAndName;
    private String commentName;
    
    public CommentTableObject() {
        this.schema = null;
        this.tableOrView = null;
        this.column = null;
        this.levelTypeAndName = new Hashtable();
        this.commentName = null;
    }
    
    public CommentTableObject toOracleCommentObject() throws ConvertException {
        final CommentTableObject to_CommentTableObject = new CommentTableObject();
        if (this.schema != null) {
            to_CommentTableObject.setSchema(this.getOracleObject(this.schema));
        }
        if (this.tableOrView != null) {
            to_CommentTableObject.setTableOrView(this.getOracleObject(this.tableOrView));
        }
        if (this.column != null) {
            to_CommentTableObject.setColumn(this.getOracleObject(this.column));
        }
        return to_CommentTableObject;
    }
    
    private String getOracleObject(String objString) {
        objString = CustomizeUtil.objectNamesToQuotedIdentifier(objString, SwisSQLUtils.getKeywords(1), null, 1);
        if (objString.startsWith("[") || objString.startsWith("`")) {
            objString = objString.substring(1, objString.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || objString.indexOf(" ") != -1) {
                objString = "\"" + objString + "\"";
            }
        }
        return objString;
    }
    
    public CommentTableObject toMSSQLServerCommentObject(final String commentType) throws ConvertException {
        final CommentTableObject to_co = new CommentTableObject();
        final boolean schemaExists = false;
        if (commentType.equalsIgnoreCase("TABLE") || commentType.equalsIgnoreCase("COLUMN")) {
            if (this.schema != null) {
                this.schema = this.getMSSQLServerObject(this.schema);
            }
            else {
                this.schema = "DBO";
            }
            to_co.addLevelTypeAndName("@Level0Type", "N'SCHEMA'");
            to_co.addLevelTypeAndName("@Level0Name", "'" + this.schema + "'");
            if (this.column != null) {
                this.column = this.getMSSQLServerObject(this.column);
                to_co.addLevelTypeAndName("@Level2Type", "N'COLUMN'");
                to_co.addLevelTypeAndName("@Level2Name", "'" + this.column + "'");
            }
            if (this.tableOrView != null) {
                this.tableOrView = this.getMSSQLServerObject(this.tableOrView);
                to_co.addLevelTypeAndName("@Level1Type", "N'TABLE'");
                to_co.addLevelTypeAndName("@Level1Name", "'" + this.tableOrView + "'");
            }
            this.commentName = this.generateCommentName(this.schema, this.tableOrView, this.column);
            to_co.addLevelTypeAndName("@NAME", "'" + this.commentName + "'");
            return to_co;
        }
        throw new ConvertException("/* SwisSQL Message : Only Comments on Table and Column Conversion is supported for SQL Server, Other Data types are yet to be supported*/");
    }
    
    private String getMSSQLServerObject(String obj) {
        if (!obj.startsWith("\"") && !obj.startsWith("[")) {
            final String[] keywords = SwisSQLUtils.getKeywords(2);
            if (obj.trim().length() > 0) {
                obj = CustomizeUtil.objectNamesToBracedIdentifier(obj, keywords, null);
            }
        }
        return obj;
    }
    
    public CommentTableObject toSybaseCommentObject() throws ConvertException {
        throw new ConvertException("Not yet supported for Sybase...");
    }
    
    public CommentTableObject toDB2CommentObject() throws ConvertException {
        throw new ConvertException("Not yet supported for DB2...");
    }
    
    public CommentTableObject toPostgreSQLCommentObject() throws ConvertException {
        throw new ConvertException("Not yet supported for PostgreSQL...");
    }
    
    public CommentTableObject toMySQLCommentObject() throws ConvertException {
        throw new ConvertException("Not yet supported for MySQL...");
    }
    
    public CommentTableObject toANSICommentObject() throws ConvertException {
        throw new ConvertException("Not yet supported for ANSI...");
    }
    
    public CommentTableObject toInformixCommentObject() throws ConvertException {
        throw new ConvertException("Not yet supported for Informix...");
    }
    
    public CommentTableObject toTimesTenCommentObject() throws ConvertException {
        throw new ConvertException("Not yet supported for TimesTen...");
    }
    
    public CommentTableObject toNetezzaCommentObject() throws ConvertException {
        throw new ConvertException("Not yet supported for Netezza.");
    }
    
    public CommentTableObject toTeradataCommentObject() throws ConvertException {
        throw new ConvertException("Not yet supported for Teradata...");
    }
    
    public String removeIndent(final String formattedSqlString) {
        return formattedSqlString;
    }
    
    @Override
    public String toString() {
        final StringBuffer commentTableObject = new StringBuffer();
        if (this.schema != null) {
            commentTableObject.append(this.schema);
            commentTableObject.append(".");
        }
        if (this.tableOrView != null) {
            commentTableObject.append(this.tableOrView);
        }
        if (this.column != null) {
            commentTableObject.append(".");
            commentTableObject.append(this.column);
        }
        if (this.levelTypeAndName != null && !this.levelTypeAndName.isEmpty()) {
            final String typeNameString = this.getTypeNameString(this.levelTypeAndName, ",");
            commentTableObject.append(typeNameString);
        }
        return commentTableObject.toString();
    }
    
    private String getTypeNameString(final Hashtable ht, final String separator) {
        final StringBuffer sb = new StringBuffer();
        final String[] key = { "@Name", "@Value", "@Level0Type", "@Level0Name", "@Level1Type", "@Level1Name", "@Level2Type", "@Level2Name" };
        for (int i = 0; i < key.length; ++i) {
            final String value = this.getLevelTypeAndName(key[i]);
            if (value != null) {
                if (i % 2 == 0) {
                    sb.append("\n");
                }
                sb.append(",");
                sb.append(key[i]);
                sb.append("=");
                sb.append(value);
            }
        }
        return "\n" + sb.substring(2);
    }
    
    private String generateCommentName(final String schema, final String table, final String column) {
        final StringBuffer cName = new StringBuffer("SWISSQL_");
        cName.append(table);
        if (column != null) {
            cName.append("_");
            cName.append(column);
        }
        return cName.toString();
    }
    
    public String getSchema() {
        return this.schema;
    }
    
    public void setSchema(final String schema) {
        this.schema = schema;
    }
    
    public String getTableOrView() {
        return this.tableOrView;
    }
    
    public void setTableOrView(final String tableOrView) {
        this.tableOrView = tableOrView;
    }
    
    public String getColumn() {
        return this.column;
    }
    
    public void setColumn(final String column) {
        this.column = column;
    }
    
    public String getLevelTypeAndName(final String type) {
        final String key = type.toUpperCase();
        if (this.levelTypeAndName.containsKey(key)) {
            return this.levelTypeAndName.get(key).toString();
        }
        return null;
    }
    
    public void addLevelTypeAndName(final String type, final String name) {
        this.levelTypeAndName.put(type.toUpperCase(), name);
    }
    
    public String getCommentName() {
        return this.commentName;
    }
    
    public void setCommentName(final String commentName) {
        this.commentName = commentName;
    }
}
