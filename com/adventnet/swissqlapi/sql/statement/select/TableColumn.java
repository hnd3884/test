package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.DB2DataTypeConverter;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.OverrideToString;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class TableColumn
{
    private String OwnerName;
    private String dot;
    private String TableName;
    private String ColumnName;
    private String tempTableName;
    private String startPosition;
    private String endPosition;
    private int startValue;
    private int endValue;
    private int length;
    private UserObjectContext context;
    private String targetDataType;
    private boolean toDB2;
    private OverrideToString override_to_string;
    private boolean isTenroxRequirement;
    private boolean isFunctionName;
    private String sourceDataType;
    private UpdateQueryStatement fromUQS;
    private DeleteQueryStatement fromDQS;
    private String origTableName;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    private boolean isOuterJoined;
    private String databaseName;
    private String collateClause;
    private String collationName;
    
    public TableColumn() {
        this.dot = ".";
        this.context = null;
        this.toDB2 = false;
        this.isTenroxRequirement = false;
        this.isFunctionName = false;
        this.isOuterJoined = false;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setOwnerName(final String s_on) {
        this.OwnerName = s_on;
    }
    
    public void setDot(final String d) {
        this.dot = d;
    }
    
    public void setTableName(final String s_tn) {
        this.TableName = s_tn;
    }
    
    public void setColumnName(final String s_cn) {
        this.ColumnName = s_cn;
    }
    
    public void setTempTableColumnName(final String s_ttcn) {
        this.tempTableName = s_ttcn;
    }
    
    public void setStartPosition(final String startPosition) {
        this.startPosition = startPosition;
    }
    
    public void setEndPosition(final String endPosition) {
        this.endPosition = endPosition;
    }
    
    public void setFromUQS(final UpdateQueryStatement fromUQS) {
        this.fromUQS = fromUQS;
    }
    
    public void setFromDQS(final DeleteQueryStatement fromDQS) {
        this.fromDQS = fromDQS;
    }
    
    public void registerOverrideToString(final OverrideToString ots) {
        this.override_to_string = ots;
    }
    
    public void setIsFunctionName(final boolean isFunctionName) {
        this.isFunctionName = isFunctionName;
    }
    
    public void setOrigTableName(final String origTableName) {
        this.origTableName = origTableName;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass commentObj) {
        this.commentObjAfterToken = commentObj;
    }
    
    public void setOuterJoin(final boolean val) {
        this.isOuterJoined = val;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public CommentClass getCommentClassAfterToken() {
        return this.commentObjAfterToken;
    }
    
    public String getOrigTableName() {
        return this.origTableName;
    }
    
    public String getDot() {
        return this.dot;
    }
    
    public String getOwnerName() {
        return this.OwnerName;
    }
    
    public String getTableName() {
        return this.TableName;
    }
    
    public String getColumnName() {
        return this.ColumnName;
    }
    
    public String getTempTableColumnName() {
        return this.tempTableName;
    }
    
    public String getStartPosition() {
        return this.startPosition;
    }
    
    public String getEndPosition() {
        return this.endPosition;
    }
    
    public boolean getOuterJoin() {
        return this.isOuterJoined;
    }
    
    public void setToDB2(final boolean toDB2) {
        this.toDB2 = toDB2;
    }
    
    public void setTargetDataType(final String targetDataType) {
        this.targetDataType = targetDataType;
    }
    
    public void setSourceDataType(final String sourceDataType) {
        this.sourceDataType = sourceDataType;
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    public void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }
    
    public String getCollationName() {
        return this.collationName;
    }
    
    public void setCollationName(final String collationName) {
        this.collationName = collationName;
    }
    
    public String getCollateClause() {
        return this.collateClause;
    }
    
    public void setCollateClause(final String collateClause) {
        this.collateClause = collateClause;
    }
    
    public TableColumn toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(this.commentObj);
        if (this.TableName != null && this.TableName.startsWith("`") && this.TableName.endsWith("`")) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.ColumnName != null && this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        if (this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) {
            final String name_only = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (name_only.trim().equals("")) {
                this.ColumnName = "'" + name_only + "'";
            }
        }
        tn.setOwnerName(this.OwnerName);
        tn.registerOverrideToString(this.override_to_string);
        tn.setTableName(this.TableName);
        if ((this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE")) && !SwisSQLOptions.fromSybase && !this.isFunctionName) {
            tn.setColumnName("GETDATE()");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
            tn.setColumnName("NEWID()");
        }
        else if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT DATE") || this.ColumnName.equalsIgnoreCase("CURRENT")) {
            tn.setColumnName("CURRENT_TIMESTAMP");
        }
        else if (this.ColumnName.equalsIgnoreCase("USER") || this.ColumnName.equalsIgnoreCase("CURRENT_USER")) {
            if (SwisSQLOptions.fromSybase) {
                tn.setColumnName("USER");
            }
            else {
                tn.setColumnName("SYSTEM_USER");
            }
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTRING(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        if (from_sqs != null && from_sqs.isMSAzure()) {
            if (this.OwnerName != null) {
                tn.setDot(".");
            }
        }
        else if (SwisSQLOptions.removeDBSchemaQualifier && this.OwnerName != null) {
            tn.setOwnerName(null);
            tn.setDot(".");
        }
        else if (this.OwnerName != null && this.OwnerName.equalsIgnoreCase("dbo")) {
            tn.setDot(".");
        }
        else {
            tn.setDot(new String(".."));
        }
        if (this.ColumnName.startsWith(":")) {
            tn.setColumnName("@" + this.ColumnName.substring(1));
        }
        if (this.collateClause != null && this.collationName != null && !this.collateClause.isEmpty() && !this.collationName.isEmpty()) {
            tn.setCollateClause(this.collateClause);
            tn.setCollationName(this.collationName);
        }
        return tn;
    }
    
    public TableColumn toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(this.commentObj);
        if (this.TableName != null && this.TableName.startsWith("`") && this.TableName.endsWith("`")) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.ColumnName != null && this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        tn.setOwnerName(this.OwnerName);
        tn.registerOverrideToString(this.override_to_string);
        tn.setTableName(this.TableName);
        if ((this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE")) && !this.isFunctionName) {
            tn.setColumnName("GETDATE()");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
            tn.setColumnName("NEWID()");
        }
        else if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT DATE") || this.ColumnName.equalsIgnoreCase("CURRENT")) {
            tn.setColumnName("CURRENT_TIMESTAMP");
        }
        else if (this.ColumnName.equalsIgnoreCase("USER") || this.ColumnName.equalsIgnoreCase("CURRENT_USER") || this.ColumnName.equalsIgnoreCase("SYSTEM_USER")) {
            tn.setColumnName("USER");
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTRING(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        if (this.OwnerName != null && !this.OwnerName.equalsIgnoreCase("dbo") && SwisSQLOptions.fullyQualifiedWithDatabaseName) {
            tn.setDatabaseName(this.OwnerName);
            tn.setOwnerName("dbo");
        }
        else {
            tn.setDot(new String(".."));
        }
        if (this.ColumnName.startsWith(":")) {
            tn.setColumnName("@" + this.ColumnName.substring(1));
        }
        else {
            tn.setObjectContext(this.context);
        }
        return tn;
    }
    
    public TableColumn toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(this.commentObj);
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("`") && this.TableName.endsWith("`")))) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")))) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        if (this.TableName != null && !this.TableName.startsWith("\"") && !this.TableName.endsWith("\"") && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
            this.TableName = "\"" + this.TableName + "\"";
        }
        if (this.ColumnName != null && !this.ColumnName.startsWith("\"") && !this.ColumnName.endsWith("\"") && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
        }
        tn.registerOverrideToString(this.override_to_string);
        tn.setOwnerName(this.OwnerName);
        tn.setTableName(this.TableName);
        if (this.ColumnName.charAt(0) == '\'' && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
            tn.setColumnName(this.ColumnName.replace('\'', '\"'));
        }
        else if ((this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT DATE")) && !this.isFunctionName) {
            tn.setColumnName("CURRENT_DATE");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
            tn.setColumnName("NEWID()");
        }
        else if ((this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) || this.ColumnName.equalsIgnoreCase("CURRENT TIME")) {
            tn.setColumnName("CURRENT_TIME");
        }
        else if (this.ColumnName.equalsIgnoreCase("TIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT TIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) {
            tn.setColumnName("CURRENT_TIMESTAMP");
        }
        else if (this.ColumnName.equalsIgnoreCase("USER") || this.ColumnName.equalsIgnoreCase("SYSTEM_USER")) {
            tn.setColumnName("CURRENT_USER");
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        tn.setDot(new String("."));
        return tn;
    }
    
    public TableColumn toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(this.commentObj);
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("`") && this.TableName.endsWith("`")))) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")))) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")))) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        if (this.OwnerName != null && !this.OwnerName.startsWith("\"") && !this.isFunctionName) {
            this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords("teradata"), null, -1);
        }
        if (this.ColumnName != null && !this.ColumnName.startsWith("\"") && !this.isFunctionName && !this.isSystemFunction(this.ColumnName)) {
            this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords("teradata"), null, -1);
        }
        if (this.TableName != null && !this.TableName.startsWith("\"") && !this.isFunctionName) {
            this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords("teradata"), null, -1);
            if (this.TableName.equalsIgnoreCase("DUAL") || this.TableName.equalsIgnoreCase("SYS.DUAL")) {
                this.TableName = "\"DUAL\"";
            }
        }
        if ((this.ColumnName.equalsIgnoreCase("\"rownum\"") || this.ColumnName.equalsIgnoreCase("rownum")) && to_sqs != null) {
            to_sqs.setRownumColumnPresent(true);
        }
        tn.registerOverrideToString(this.override_to_string);
        tn.setOwnerName(this.OwnerName);
        tn.setTableName(this.TableName);
        if (this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("CURRENT DATE")) {
            tn.setColumnName("CURRENT_DATE");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYSDATE") && !this.isFunctionName) {
            tn.setColumnName("CURRENT_TIMESTAMP(0)");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
            tn.setColumnName("NEWID()");
        }
        else if ((this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) || this.ColumnName.equalsIgnoreCase("CURRENT TIME")) {
            tn.setColumnName("CURRENT_TIME");
        }
        else if (this.ColumnName.equalsIgnoreCase("TIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT TIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) {
            tn.setColumnName("CURRENT_TIMESTAMP(0)");
        }
        else if (this.ColumnName.equalsIgnoreCase("USER") || this.ColumnName.equalsIgnoreCase("SYSTEM_USER")) {
            tn.setColumnName("CURRENT_USER");
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        tn.setDot(new String("."));
        return tn;
    }
    
    public TableColumn toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        this.sourceDataType = MetadataInfoUtil.getDatatypeName(from_sqs, this);
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(this.commentObj);
        tn.setTargetDataType(this.targetDataType);
        tn.setSourceDataType(this.sourceDataType);
        tn.setToDB2(true);
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("`") && this.TableName.endsWith("`")))) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")))) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")))) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        tn.setOwnerName(this.OwnerName);
        tn.registerOverrideToString(this.override_to_string);
        tn.setTableName(this.TableName);
        if (this.ColumnName != null && this.ColumnName.charAt(0) == '\"') {
            if (this.startPosition != null) {
                this.startValue = Integer.parseInt(this.startPosition);
                this.endValue = Integer.parseInt(this.endPosition);
                this.length = this.endValue - this.startValue + 1;
                final String tempColumnName = this.getColumnName();
                tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
                tn.setStartPosition(null);
                tn.setEndPosition(null);
            }
            else {
                tn.setColumnName(this.ColumnName.toUpperCase());
            }
        }
        else if (this.ColumnName != null && (this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE"))) {
            tn.setColumnName("CURRENT DATE");
        }
        else if (this.ColumnName != null && ((this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) || this.ColumnName.equalsIgnoreCase("CURRENT_TIME"))) {
            tn.setColumnName("CURRENT TIME");
        }
        else if (this.ColumnName != null && (this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT_TIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
            tn.setColumnName("CURRENT TIMESTAMP");
        }
        else if (this.ColumnName != null && (this.ColumnName.equalsIgnoreCase("CURRENT_USER") || this.ColumnName.equalsIgnoreCase("SYSTEM_USER"))) {
            tn.setColumnName("USER");
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        tn.setDot(new String("."));
        return tn;
    }
    
    public TableColumn toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(this.commentObj);
        if (this.origTableName == null && this.TableName != null) {
            tn.setOrigTableName(this.TableName);
        }
        boolean quotes = false;
        if (this.TableName != null) {
            if (this.TableName.startsWith("#")) {
                this.TableName = this.TableName.substring(1);
            }
            if (this.TableName.startsWith("@")) {
                this.TableName = "\"" + this.TableName + "\"";
            }
            tn.setOrigTableName(this.TableName);
        }
        if (this.TableName != null && !this.isFunctionName) {
            this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(1), null, 1);
        }
        if (this.ColumnName != null && this.TableName == null && this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) {
            quotes = true;
        }
        if (this.ColumnName != null && !this.isFunctionName) {
            boolean check = true;
            if (this.TableName == null && !this.ColumnName.startsWith("[") && !this.ColumnName.startsWith("\"") && !this.ColumnName.startsWith("`") && (this.ColumnName.equalsIgnoreCase("sysdate") || this.ColumnName.equalsIgnoreCase("rownum") || this.ColumnName.equalsIgnoreCase("user"))) {
                check = false;
                if (from_sqs != null) {
                    final FromClause fc = from_sqs.getFromClause();
                    if (fc != null) {
                        final FromTable ft = MetadataInfoUtil.getTableOfColumn(from_sqs, this);
                        if (ft != null) {
                            check = true;
                        }
                    }
                }
                else if (this.fromUQS != null) {
                    final FromTable ft2 = MetadataInfoUtil.getTableOfColumn(this.fromUQS, this);
                    if (ft2 != null) {
                        check = true;
                    }
                }
                else if (this.fromDQS != null) {
                    final FromTable ft2 = MetadataInfoUtil.getTableOfColumn(this.fromDQS, this);
                    if (ft2 != null) {
                        check = true;
                    }
                }
            }
            if (check) {
                this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(1), null, 1);
            }
        }
        if (this.tempTableName != null) {
            if (this.tempTableName.startsWith("#")) {
                this.tempTableName = this.tempTableName.substring(1);
                String[] splitTempTableName = null;
                final String splitwhere = "\\.";
                splitTempTableName = this.tempTableName.split(splitwhere);
                if (splitTempTableName.length == 1 && splitTempTableName[0].startsWith("@")) {
                    this.tempTableName = "\"" + splitTempTableName[0] + "\"";
                }
                else if (splitTempTableName.length == 2) {
                    this.origTableName = splitTempTableName[splitTempTableName.length - 1];
                    this.OwnerName = splitTempTableName[splitTempTableName.length - 2];
                    if (this.origTableName.startsWith("@")) {
                        this.tempTableName = "\"" + this.origTableName + "\"";
                    }
                }
            }
            tn.setColumnName(this.ColumnName);
            tn.setTempTableColumnName(this.tempTableName);
        }
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("`") && this.TableName.endsWith("`")))) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")))) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")))) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        if (!quotes) {
            if (!this.ColumnName.startsWith("'")) {
                boolean addQuotes = false;
                if (this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) {
                    this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
                    addQuotes = true;
                }
                if (this.ColumnName.length() > 30) {
                    if (this.ColumnName.startsWith("@")) {
                        this.ColumnName = this.ColumnName.substring(0, 31);
                    }
                    else {
                        this.ColumnName = this.ColumnName.substring(0, 30);
                    }
                }
                if (addQuotes) {
                    this.ColumnName = "\"" + this.ColumnName + "\"";
                }
            }
        }
        else {
            boolean truncate = false;
            if (from_sqs != null) {
                final FromClause fc = from_sqs.getFromClause();
                if (fc != null) {
                    final FromTable ft = MetadataInfoUtil.getTableOfColumn(from_sqs, this);
                    if (ft != null) {
                        truncate = true;
                    }
                }
            }
            else if (this.fromUQS != null) {
                final FromTable ft2 = MetadataInfoUtil.getTableOfColumn(this.fromUQS, this);
                if (ft2 != null) {
                    truncate = true;
                }
            }
            else if (this.fromDQS != null) {
                final FromTable ft2 = MetadataInfoUtil.getTableOfColumn(this.fromDQS, this);
                if (ft2 != null) {
                    truncate = true;
                }
            }
            if (truncate) {
                boolean addQuotes2 = false;
                if (this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) {
                    this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
                    addQuotes2 = true;
                }
                if (this.ColumnName.length() > 30) {
                    this.ColumnName = this.ColumnName.substring(0, 30);
                }
                if (addQuotes2) {
                    this.ColumnName = "\"" + this.ColumnName + "\"";
                }
            }
        }
        if (this.OwnerName != null && this.OwnerName.equalsIgnoreCase("DBO") && this.isTenroxRequirement) {
            tn.setOwnerName("PUSER");
        }
        else if (this.OwnerName != null && this.OwnerName.equalsIgnoreCase("DBO")) {
            tn.setOwnerName(null);
        }
        else if (SwisSQLOptions.removeOracleSchemaQualifier) {
            tn.setOwnerName(null);
        }
        else {
            tn.setOwnerName(this.OwnerName);
        }
        tn.registerOverrideToString(this.override_to_string);
        tn.setTableName(this.TableName);
        if (this.ColumnName.charAt(0) == '\"') {
            if (this.startPosition != null) {
                this.startValue = Integer.parseInt(this.startPosition);
                this.endValue = Integer.parseInt(this.endPosition);
                this.length = this.endValue - this.startValue + 1;
                final String tempColumnName = this.getColumnName();
                tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
                tn.setStartPosition(null);
                tn.setEndPosition(null);
            }
            else {
                tn.setColumnName(this.ColumnName);
            }
        }
        else if (from_sqs.isOracleLive() && this.ColumnName.equalsIgnoreCase("DATE_FORMAT")) {
            tn.setColumnName("TO_CHAR");
        }
        else if (from_sqs.isOracleLive() && this.ColumnName.equalsIgnoreCase("datediff")) {
            tn.setColumnName("");
        }
        else if (this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE") || this.ColumnName.equalsIgnoreCase("CURRENT")) {
            tn.setColumnName("SYSDATE");
        }
        else if ((this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) || (this.ColumnName.equalsIgnoreCase("CURRENT_TIME") && !SwisSQLOptions.fromSybase)) {
            tn.setColumnName("TO_CHAR(SYSDATE,'HH:MI:SS')");
        }
        else if (this.ColumnName.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
            tn.setColumnName("systimestamp");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYSTEM_USER") || this.ColumnName.equalsIgnoreCase("CURRENT_USER") || this.ColumnName.equalsIgnoreCase("USER") || this.ColumnName.equalsIgnoreCase("SESSION_USER")) {
            tn.setColumnName("USER");
        }
        else if (this.ColumnName.trim().startsWith("$")) {
            try {
                final String dollarString = this.ColumnName.substring(1);
                final float numericValue = Float.parseFloat(dollarString);
                tn.setColumnName(dollarString);
            }
            catch (final NumberFormatException e) {
                tn.setColumnName(this.ColumnName);
            }
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else if (this.ColumnName.startsWith("@")) {
            tn.setColumnName(":" + this.ColumnName.substring(1));
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        tn.setDot(new String("."));
        if (this.origTableName != null) {
            tn.setOrigTableName(this.origTableName);
        }
        return tn;
    }
    
    public TableColumn toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(this.commentObj);
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("`") && this.TableName.endsWith("`")))) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")))) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")))) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        tn.setOwnerName(this.OwnerName);
        tn.registerOverrideToString(this.override_to_string);
        tn.setTableName(this.TableName);
        if (this.ColumnName.charAt(0) == '\"') {
            if (this.startPosition != null) {
                tn.setStartPosition(this.startPosition);
                tn.setEndPosition(this.endPosition);
                tn.setColumnName(this.ColumnName.toUpperCase());
            }
            else {
                tn.setColumnName(this.ColumnName.toUpperCase());
            }
        }
        else if ((this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE")) && !this.isFunctionName) {
            tn.setColumnName("CURRENT");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYSTEM_USER") || this.ColumnName.equalsIgnoreCase("CURRENT_USER")) {
            tn.setColumnName("USER");
        }
        else if (this.startPosition != null) {
            tn.setStartPosition(this.startPosition);
            tn.setEndPosition(this.endPosition);
            tn.setColumnName(this.ColumnName);
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        tn.setDot(new String("."));
        return tn;
    }
    
    public TableColumn toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(null);
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("`") && this.TableName.endsWith("`")))) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")))) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")))) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        if (this.OwnerName != null) {
            this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(4), null, 4);
        }
        if (this.ColumnName != null) {
            this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(4), null, 4);
        }
        if (this.TableName != null) {
            this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(4), null, 4);
        }
        tn.setOwnerName((from_sqs != null && from_sqs.isAmazonRedShift()) ? this.OwnerName : null);
        tn.setTableName(this.TableName);
        if (this.TableName != null) {
            tn.setTableName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(SelectStatement.changeBackTip(this.TableName), from_sqs != null && from_sqs.getReportsMeta()));
        }
        tn.registerOverrideToString(this.override_to_string);
        if (this.ColumnName.charAt(0) == '\"') {
            if (this.startPosition != null) {
                this.startValue = Integer.parseInt(this.startPosition);
                this.endValue = Integer.parseInt(this.endPosition);
                this.length = this.endValue - this.startValue + 1;
                final String tempColumnName = this.getColumnName();
                tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
                tn.setStartPosition(null);
                tn.setEndPosition(null);
            }
            else if (this.ColumnName != null) {
                tn.setColumnName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(SelectStatement.changeBackTip(this.ColumnName), from_sqs != null && from_sqs.getReportsMeta()));
            }
        }
        else if ((this.ColumnName.equalsIgnoreCase("DATE") && from_sqs != null && from_sqs.getFromClause() == null) || this.ColumnName.equalsIgnoreCase("CURDATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE")) {
            tn.setColumnName("CURRENT_DATE");
        }
        else if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
        }
        else if (this.ColumnName.equalsIgnoreCase("CURTIME") || this.ColumnName.equalsIgnoreCase("CURRENT_TIME") || this.ColumnName.equalsIgnoreCase("SYSTIME")) {
            tn.setColumnName("CURRENT_TIME");
        }
        else if ((this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
            tn.setColumnName("CURRENT_TIMESTAMP");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYSTEM_USER") || this.ColumnName.equalsIgnoreCase("USER")) {
            tn.setColumnName("CURRENT_USER");
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else {
            tn.setColumnName(this.ColumnName);
            if (this.ColumnName != null) {
                tn.setColumnName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(SelectStatement.changeBackTip(this.ColumnName), from_sqs != null && from_sqs.getReportsMeta()));
            }
        }
        tn.setDot(new String("."));
        return tn;
    }
    
    public TableColumn toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(this.commentObj);
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("\"") && this.TableName.endsWith("\""))) && from_sqs != null && from_sqs.getCanAllowBackTipInColumnName()) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            this.TableName = "`" + this.TableName + "`";
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\""))) && from_sqs != null && from_sqs.getCanAllowBackTipInColumnName()) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            this.ColumnName = "`" + this.ColumnName + "`";
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("\"") && this.OwnerName.endsWith("\""))) && from_sqs != null && from_sqs.getCanAllowBackTipInColumnName()) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            this.OwnerName = "`" + this.OwnerName + "`";
        }
        tn.setOwnerName(this.OwnerName);
        tn.setTableName(this.TableName);
        tn.registerOverrideToString(this.override_to_string);
        if (this.ColumnName.charAt(0) == '\"') {
            if (this.startPosition != null) {
                this.startValue = Integer.parseInt(this.startPosition);
                this.endValue = Integer.parseInt(this.endPosition);
                this.length = this.endValue - this.startValue + 1;
                final String tempColumnName = this.getColumnName();
                tn.setColumnName("SUBSTRING(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
                tn.setStartPosition(null);
                tn.setEndPosition(null);
            }
            else {
                tn.setColumnName(this.ColumnName.replace('\"', ' ').trim());
            }
        }
        else if (this.ColumnName.equalsIgnoreCase("DATE")) {
            tn.setColumnName("CURRENT_DATE");
        }
        else if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
        }
        else if ((this.ColumnName.equalsIgnoreCase("TIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
            tn.setColumnName("CURRENT_TIMESTAMP");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYSTEM_USER") || this.ColumnName.equalsIgnoreCase("USER") || this.ColumnName.equalsIgnoreCase("CURRENT_USER")) {
            tn.setColumnName("USER()");
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTRING(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        tn.setDot(new String("."));
        return tn;
    }
    
    public TableColumn toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("`") && this.TableName.endsWith("`")))) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.TableName != null && !this.isFunctionName) {
            this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(10), null, 10);
        }
        if (this.ColumnName != null && !this.isFunctionName && !this.ColumnName.equalsIgnoreCase("user") && (!this.ColumnName.equalsIgnoreCase("sysdate") || !SwisSQLOptions.SOURCE_DB_IS_ORACLE)) {
            this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(10), null, 10);
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")))) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")))) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        if (this.OwnerName != null && this.OwnerName.equalsIgnoreCase("DBO")) {
            tn.setOwnerName(null);
        }
        else {
            tn.setOwnerName(this.OwnerName);
        }
        tn.registerOverrideToString(this.override_to_string);
        boolean isAliasSet = false;
        if (from_sqs != null) {
            final FromClause fc = from_sqs.getFromClause();
            Vector v_fil = new Vector();
            if (fc != null) {
                v_fil = fc.getFromItemList();
            }
            for (int i_count = 0; i_count < v_fil.size(); ++i_count) {
                if (v_fil.elementAt(i_count) instanceof FromTable) {
                    final FromTable ft = v_fil.elementAt(i_count);
                    if (ft.getTableName() instanceof String) {
                        final String s_tn = (String)ft.getTableName();
                        if (s_tn.equalsIgnoreCase(this.TableName) && ft.getAliasName() == null) {
                            isAliasSet = false;
                            break;
                        }
                        if (s_tn.equalsIgnoreCase(this.TableName) && ft.getAliasName() != null) {
                            tn.setTableName(ft.getAliasName());
                            isAliasSet = true;
                        }
                    }
                }
            }
        }
        if (!isAliasSet) {
            tn.setTableName(this.TableName);
        }
        if (this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE") || this.ColumnName.equalsIgnoreCase("CURRENT")) {
            tn.setColumnName("SYSDATE");
        }
        else if ((this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) || (this.ColumnName.equalsIgnoreCase("CURRENT_TIME") && !SwisSQLOptions.fromSybase)) {
            tn.setColumnName("TO_CHAR(SYSDATE,'HH:MI:SS')");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYSTEM_USER") || this.ColumnName.equalsIgnoreCase("CURRENT_USER") || this.ColumnName.equalsIgnoreCase("USER")) {
            tn.setColumnName("USER");
        }
        else if (this.ColumnName.trim().startsWith("$")) {
            try {
                final String dollarString = this.ColumnName.substring(1);
                final float numericValue = Float.parseFloat(dollarString);
                tn.setColumnName(dollarString);
            }
            catch (final NumberFormatException e) {
                tn.setColumnName(this.ColumnName);
            }
        }
        else if (this.ColumnName.startsWith("@")) {
            tn.setColumnName(":" + this.ColumnName.substring(1));
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        tn.setDot(new String("."));
        return tn;
    }
    
    public TableColumn toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        tn.setCommentClass(this.commentObj);
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("`") && this.TableName.endsWith("`")))) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            if (this.TableName.indexOf(32) != -1) {
                this.TableName = "\"" + this.TableName + "\"";
            }
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")))) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            if (this.ColumnName.indexOf(32) != -1) {
                this.ColumnName = "\"" + this.ColumnName + "\"";
            }
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")))) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            if (this.OwnerName.indexOf(32) != -1) {
                this.OwnerName = "\"" + this.OwnerName + "\"";
            }
        }
        if (this.OwnerName != null) {
            this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(11), null, 11);
        }
        if (this.ColumnName != null) {
            this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(11), null, 11);
        }
        if (this.TableName != null) {
            this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(11), null, 11);
        }
        tn.registerOverrideToString(this.override_to_string);
        tn.setOwnerName(this.OwnerName);
        if (SwisSQLOptions.renameTableNameAsSchemName_TableName) {
            if (this.OwnerName != null && !this.OwnerName.startsWith("\"") && !this.TableName.startsWith("\"")) {
                tn.setTableName(this.OwnerName + "_" + this.TableName);
            }
            else {
                tn.setTableName(this.TableName);
            }
        }
        else {
            tn.setTableName(this.TableName);
        }
        if (this.ColumnName.charAt(0) == '\'') {
            tn.setColumnName(this.ColumnName.replace('\'', '\''));
        }
        else if ((this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT DATE")) && !this.isFunctionName) {
            tn.setColumnName("CURRENT_DATE");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
            tn.setColumnName("NEWID()");
        }
        else if ((this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) || this.ColumnName.equalsIgnoreCase("CURRENT TIME")) {
            tn.setColumnName("CURRENT_TIME");
        }
        else if (this.ColumnName.equalsIgnoreCase("TIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT TIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) {
            tn.setColumnName("CURRENT_TIMESTAMP");
        }
        else if (this.ColumnName.equalsIgnoreCase("USER") || this.ColumnName.equalsIgnoreCase("SYSTEM_USER")) {
            tn.setColumnName("CURRENT_USER");
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        tn.setDot(new String("."));
        return tn;
    }
    
    public String getResultString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.override_to_string != null) {
            sb.append(this.override_to_string.toString(this));
        }
        else {
            if (this.tempTableName != null) {
                sb.append(this.tempTableName);
                sb.append(".");
            }
            if (this.databaseName != null) {
                sb.append(this.databaseName);
                sb.append(".");
            }
            if (this.OwnerName != null) {
                sb.append(this.OwnerName);
                sb.append(this.dot);
            }
            if (this.TableName != null) {
                if (this.TableName.equalsIgnoreCase("NEXT VALUE FOR ") || this.TableName.trim().equalsIgnoreCase("NEXTVAL FOR") || this.TableName.trim().equalsIgnoreCase("PREVVAL FOR")) {
                    sb.append(this.TableName);
                }
                else if (this.context != null) {
                    String temp = null;
                    if (this.origTableName != null) {
                        final Object obj = this.context.getEquivalent(this.origTableName);
                        if (obj != null) {
                            temp = obj.toString();
                        }
                    }
                    if ((this.origTableName != null && this.origTableName.equals(temp)) || temp == null) {
                        sb.append(this.TableName + ".");
                    }
                    else {
                        sb.append(temp + ".");
                    }
                }
                else if (FromClause.doNotAddDotInSubquery) {
                    sb.append(this.TableName);
                }
                else {
                    sb.append(this.TableName + ".");
                }
            }
            if (this.context != null && !FromClause.doNotAddDotInSubquery) {
                final String name = this.context.getEquivalent(this.ColumnName).toString();
                sb.append(name);
            }
            else if (this.ColumnName != null) {
                sb.append(this.ColumnName);
                if (this.collateClause != null && this.collationName != null && !this.collateClause.isEmpty() && !this.collationName.isEmpty()) {
                    sb.append(" ");
                    sb.append(this.collateClause);
                    sb.append(" ");
                    sb.append(this.collationName);
                    sb.append(" ");
                }
            }
            if (this.startPosition != null) {
                sb.append("[" + this.startPosition);
            }
            if (this.endPosition != null) {
                sb.append("," + this.endPosition + "]");
            }
        }
        if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString().trim());
        }
        return sb.toString();
    }
    
    public String getSourceDataType() {
        final String str = this.getResultString();
        String sourceDataType = this.sourceDataType;
        if (sourceDataType != null) {
            sourceDataType = DB2DataTypeConverter.convertPLSQLTypeToDB2Type(sourceDataType);
            sourceDataType = CastingUtil.getDataType(sourceDataType);
        }
        else if (sourceDataType == null && SwisSQLAPI.variableDatatypeMapping != null) {
            sourceDataType = CastingUtil.getDataType(SwisSQLAPI.variableDatatypeMapping.get(str));
        }
        return sourceDataType;
    }
    
    @Override
    public String toString() {
        final String str = this.getResultString();
        if (this.toDB2) {
            final String res = CastingUtil.getDB2DataTypeCastedParameter(this.getSourceDataType(), this.targetDataType, str);
            return res;
        }
        return str;
    }
    
    private boolean isSystemFunction(final String columnName) {
        boolean bl = false;
        final String[] sysFuncs = SwisSQLUtils.getSystemFunctions(12);
        for (int k = 0; k < sysFuncs.length; ++k) {
            if (columnName.equalsIgnoreCase(sysFuncs[k])) {
                bl = true;
                break;
            }
        }
        return bl;
    }
    
    public TableColumn toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final TableColumn tn = new TableColumn();
        if (this.TableName != null && ((this.TableName.startsWith("[") && this.TableName.endsWith("]")) || (this.TableName.startsWith("`") && this.TableName.endsWith("`")))) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            this.TableName = "\"" + this.TableName + "\"";
        }
        if (this.ColumnName != null && ((this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]")) || (this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")))) {
            this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
            this.ColumnName = "\"" + this.ColumnName + "\"";
        }
        if (this.OwnerName != null && ((this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]")) || (this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")))) {
            this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
            this.OwnerName = "\"" + this.OwnerName + "\"";
        }
        tn.setOwnerName(this.OwnerName);
        tn.setTableName(this.TableName);
        tn.registerOverrideToString(this.override_to_string);
        if (this.ColumnName.charAt(0) == '`') {
            if (this.startPosition != null) {
                this.startValue = Integer.parseInt(this.startPosition);
                this.endValue = Integer.parseInt(this.endPosition);
                this.length = this.endValue - this.startValue + 1;
                final String tempColumnName = this.getColumnName();
                tn.setColumnName("SUBSTRING(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
                tn.setStartPosition(null);
                tn.setEndPosition(null);
            }
            else {
                tn.setColumnName(this.ColumnName.replace('`', ' ').trim());
            }
        }
        else if (this.ColumnName.equalsIgnoreCase("DATE")) {
            tn.setColumnName("CURRENT_DATE");
        }
        else if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
        }
        else if ((this.ColumnName.equalsIgnoreCase("TIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
            tn.setColumnName("CURRENT_TIMESTAMP");
        }
        else if (this.ColumnName.equalsIgnoreCase("SYSTEM_USER") || this.ColumnName.equalsIgnoreCase("USER") || this.ColumnName.equalsIgnoreCase("CURRENT_USER")) {
            tn.setColumnName("USER()");
        }
        else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            final String tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTRING(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition(null);
            tn.setEndPosition(null);
        }
        else {
            tn.setColumnName(this.ColumnName);
        }
        tn.setDot(new String("."));
        return tn;
    }
}
