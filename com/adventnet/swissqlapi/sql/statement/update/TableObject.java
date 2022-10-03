package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class TableObject
{
    private String user;
    private String owner;
    private String tableName;
    private String dot;
    private String dotDot;
    private String origTableName;
    private String databaseName;
    private boolean isTenroxRequirement;
    private UserObjectContext context;
    private String tableType;
    private CommentClass commentObj;
    
    public TableObject() {
        this.isTenroxRequirement = false;
        this.context = null;
        this.tableType = "";
    }
    
    public void setUser(final String s) {
        this.user = s;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    public void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public void setTableName(final String s) {
        this.tableName = s;
    }
    
    public void setDot(final String dot) {
        this.dot = dot;
    }
    
    public void setDotDot(final String dotDot) {
        this.dotDot = dotDot;
    }
    
    public void setOrigTableName(final String origTableName) {
        this.origTableName = origTableName;
    }
    
    public String getOrigTableName() {
        return this.origTableName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getDot() {
        return this.dot;
    }
    
    public String getDotDot() {
        return this.dotDot;
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    public String getTableType() {
        return this.tableType;
    }
    
    public void setTableType(final String tableType) {
        this.tableType = tableType;
    }
    
    public void toMySQL() throws ConvertException {
        this.setCommentClass(null);
        this.dot = new String(".");
        if (!this.tableName.startsWith("`") && !this.tableName.endsWith("`")) {
            this.tableName = "`" + this.tableName + "`";
        }
        this.owner = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void toOracle() throws ConvertException {
        if (this.user != null && this.isTenroxRequirement) {
            this.user = "PUSER";
        }
        this.dot = new String(".");
        this.owner = null;
        if (this.user != null) {
            this.user = CustomizeUtil.objectNamesToQuotedIdentifier(this.user, SwisSQLUtils.getKeywords(1), null, 1);
            if (this.dotDot != null && this.user.equalsIgnoreCase("tempdb")) {
                this.user = null;
            }
        }
        this.tableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.tableName, SwisSQLUtils.getKeywords(1), null, 1);
        if (this.tableName.startsWith("#")) {
            this.tableName = this.tableName.substring(1);
            if (this.databaseName != null && this.databaseName.startsWith("@")) {
                this.databaseName = "\"" + this.databaseName + "\"";
            }
        }
        if (this.databaseName != null && !this.databaseName.startsWith("\"") && !this.databaseName.endsWith("\"") && this.databaseName.startsWith("@")) {
            this.databaseName = "\"" + this.databaseName + "\"";
        }
        if (this.tableName != null && this.tableName.startsWith("@")) {
            this.tableName = "\"" + this.tableName + "\"";
        }
        if (this.dotDot != null) {
            if (this.owner != null && this.owner.equalsIgnoreCase("dbo")) {
                this.owner = null;
            }
        }
        else if (this.user != null && this.user.equalsIgnoreCase("dbo")) {
            this.user = null;
        }
    }
    
    public void toMSSQLServer() throws ConvertException {
        this.setCommentClass(null);
        if (SwisSQLOptions.removeExtraDotForDatabaseName) {
            this.dot = new String(".");
        }
        else {
            this.dot = new String("..");
        }
        if (!this.tableName.startsWith("\"") && !this.tableName.startsWith("[")) {
            final String[] keywords = SwisSQLUtils.getKeywords(2);
            if (this.tableName.trim().length() > 0) {
                this.tableName = CustomizeUtil.objectNamesToBracedIdentifier(this.tableName, keywords, null);
            }
        }
        if (SwisSQLOptions.TRUNCATE_ORACLE_SCHEMA_INFORMATION && this.getTableType().equalsIgnoreCase("INDEX")) {
            this.user = null;
            this.owner = null;
        }
    }
    
    public void toSybase() throws ConvertException {
        this.setCommentClass(null);
        this.dot = new String("..");
    }
    
    public void toPostgreSQL() throws ConvertException {
        if (this.user != null) {
            if (this.user.startsWith("[")) {
                this.user = this.user.substring(1, this.user.length() - 1);
            }
            this.user = CustomizeUtil.objectNamesToQuotedIdentifier(this.user, SwisSQLUtils.getKeywords(4), null, 4);
            if (this.dotDot != null && this.user.equalsIgnoreCase("tempdb")) {
                this.user = null;
            }
        }
        if (this.tableName.startsWith("[")) {
            this.tableName = this.tableName.substring(1, this.tableName.length() - 1);
        }
        this.tableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.tableName, SwisSQLUtils.getKeywords(4), null, 4);
        if (this.tableName.startsWith("#")) {
            this.tableName = this.tableName.substring(1);
        }
        if (this.databaseName != null && !this.databaseName.startsWith("\"") && !this.databaseName.endsWith("\"") && this.databaseName.startsWith("@")) {
            this.databaseName = "\"" + this.databaseName + "\"";
        }
        if (this.dotDot != null) {
            if (this.owner != null && this.owner.equalsIgnoreCase("dbo")) {
                this.owner = null;
            }
        }
        else if (this.user != null && this.user.equalsIgnoreCase("dbo")) {
            this.user = null;
        }
    }
    
    public void toDB2() throws ConvertException {
        this.setCommentClass(null);
        this.dot = new String(".");
        this.owner = null;
        final String ownerName = SwisSQLAPI.objectsOwnerName.get(new Integer(3));
        if (ownerName != null) {
            this.user = ownerName;
        }
    }
    
    public void toInformix() throws ConvertException {
        this.dot = new String(".");
        this.owner = null;
        this.setCommentClass(null);
    }
    
    public void toANSISQL() throws ConvertException {
        if (this.user != null && this.user.equalsIgnoreCase("dbo")) {
            this.user = null;
        }
        if (this.tableName != null) {
            this.tableName = "\"" + this.tableName + "\"";
        }
        this.dot = new String(".");
        this.owner = null;
        this.setCommentClass(null);
    }
    
    public void toTeradata() throws ConvertException {
        if (this.user != null && this.user.equalsIgnoreCase("dbo")) {
            this.user = null;
        }
        if (this.tableName != null) {
            this.tableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.tableName, SwisSQLUtils.getKeywords("teradata"), null, -1);
        }
        this.dot = new String(".");
        this.owner = null;
        this.setCommentClass(null);
    }
    
    public void toTimesTen() throws ConvertException {
        this.setCommentClass(null);
        if (this.dotDot != null) {
            this.user = null;
            if (this.owner != null && this.owner.equalsIgnoreCase("dbo")) {
                this.owner = null;
            }
        }
        else if (this.user != null && this.user.equalsIgnoreCase("dbo")) {
            this.user = null;
        }
        this.tableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.tableName, SwisSQLUtils.getKeywords(10), null, 10);
    }
    
    public void toNetezza() throws ConvertException {
        this.dot = new String(".");
        this.setCommentClass(null);
        if (this.user != null) {
            this.user = CustomizeUtil.objectNamesToQuotedIdentifier(this.user, SwisSQLUtils.getKeywords(11), null, 11);
            if (this.dotDot != null && this.user.equalsIgnoreCase("tempdb")) {
                this.user = null;
            }
        }
        this.tableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.tableName, SwisSQLUtils.getKeywords(11), null, 11);
        if (this.databaseName != null && !this.databaseName.startsWith("\"") && !this.databaseName.endsWith("\"") && this.databaseName.startsWith("@")) {
            this.databaseName = "\"" + this.databaseName + "\"";
        }
        if (this.tableName != null && this.tableName.startsWith("@")) {
            this.tableName = "\"" + this.tableName + "\"";
        }
        if (this.dotDot != null) {
            if (this.owner != null && this.owner.equalsIgnoreCase("dbo")) {
                this.owner = null;
            }
        }
        else if (this.user != null && this.user.equalsIgnoreCase("dbo")) {
            this.user = null;
        }
        if (SwisSQLOptions.renameTableNameAsSchemName_TableName && this.user != null && this.tableName != null && this.owner != null && !this.user.startsWith("\"") && !this.tableName.startsWith("\"") && !this.owner.startsWith("\"")) {
            this.tableName = this.owner + "_" + this.tableName;
        }
        else if (SwisSQLOptions.renameTableNameAsSchemName_TableName && this.user != null && this.tableName != null && this.owner == null && !this.user.startsWith("\"") && !this.tableName.startsWith("\"")) {
            this.tableName = this.user + "_" + this.tableName;
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        if (this.commentObj != null) {
            stringbuffer.append(this.commentObj.toString().trim() + " ");
        }
        if (this.user != null) {
            stringbuffer.append(this.user);
            if (this.owner != null) {
                stringbuffer.append(".");
            }
            else {
                stringbuffer.append(this.dot);
            }
        }
        if (this.owner != null) {
            stringbuffer.append(this.owner);
            stringbuffer.append(".");
        }
        if (this.tableName != null) {
            if (this.context != null) {
                String name = null;
                if (this.origTableName != null) {
                    final Object obj = this.context.getEquivalent(this.origTableName);
                    if (obj != null) {
                        name = obj.toString();
                    }
                }
                if ((this.origTableName != null && this.origTableName.equals(name)) || name == null) {
                    stringbuffer.append(this.tableName);
                }
                else {
                    stringbuffer.append(name);
                }
            }
            else {
                stringbuffer.append(this.tableName);
            }
        }
        if (this.databaseName != null) {
            stringbuffer.append(this.databaseName);
        }
        return stringbuffer.toString();
    }
}
