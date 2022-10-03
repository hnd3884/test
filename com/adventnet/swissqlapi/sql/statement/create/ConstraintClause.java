package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.StringTokenizer;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class ConstraintClause
{
    ConstraintType constraintType;
    private String constraint;
    private String constraintName;
    private String autoIncrement;
    private String columnName;
    private NotNull notNull;
    private String notNullStr;
    private String tableNameFromCQS;
    private String columnNameForSequence;
    private boolean commentForConstraintName;
    private String characterLengthForComment;
    private UserObjectContext context;
    private String triggerForIdentity;
    
    public ConstraintClause() {
        this.commentForConstraintName = false;
        this.context = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setConstraintName(final String constraintName) {
        this.constraintName = constraintName;
    }
    
    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }
    
    public void setNotNull(final NotNull notNull) {
        this.notNull = notNull;
    }
    
    public void setConstraintType(final ConstraintType constraintType) {
        this.constraintType = constraintType;
    }
    
    public void setAutoIncrement(final String autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void setTableNameFromCQS(final String tableNameFromCQS) {
        this.tableNameFromCQS = tableNameFromCQS;
    }
    
    public void setColumnNameForSequence(final String columnNameForSequence) {
        this.columnNameForSequence = columnNameForSequence;
    }
    
    public void setCommentForConstraintName(final boolean commentForConstraintName) {
        this.commentForConstraintName = commentForConstraintName;
    }
    
    public void setCharacterLengthForComment(final String characterLengthForComment) {
        this.characterLengthForComment = characterLengthForComment;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public NotNull getNotNull() {
        return this.notNull;
    }
    
    public String getConstraint() {
        return this.constraint;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    public ConstraintType getConstraintType() {
        return this.constraintType;
    }
    
    public String getAutoIncrement() {
        return this.autoIncrement;
    }
    
    public String getTriggerForIdentity() {
        return this.triggerForIdentity;
    }
    
    public void toDB2String() throws ConvertException {
        this.setAutoIncrement(null);
        if (this.constraintName != null && ((this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) || (this.constraintName.startsWith("`") && this.constraintName.endsWith("`")))) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
                this.constraintName = "\"" + this.constraintName + "\"";
            }
        }
        if (this.constraintName != null) {
            if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
                if (this.constraintName.length() < 9) {
                    this.constraintName = "CONS_NAME" + this.constraintName;
                }
                else if (this.constraintName.length() > 9 && this.constraintName.length() < 14) {
                    this.constraintName = "CONS" + this.constraintName;
                }
                else {
                    this.constraintName = "CON" + this.constraintName;
                }
            }
            this.setConstraintName(this.constraintName);
        }
        if (SwisSQLAPI.truncateTableNameForDB2) {
            if (this.constraintName != null && this.constraintName.length() > 18) {
                if (SwisSQLAPI.truncateConstraintCount > 99) {
                    SwisSQLAPI.truncateConstraintCount = 0;
                }
                this.constraintName = this.constraintName.substring(0, 12) + "_ADV" + SwisSQLAPI.truncateConstraintCount;
                ++SwisSQLAPI.truncateConstraintCount;
                CreateQueryStatement.commentWhenConstraintNameTruncated = " -- SwisSQL Message : Manual Intervention required. The constraint name changed as the length was greater than 18 characters ; ";
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toDB2ConstraintType = this.getConstraintType();
            if (toDB2ConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toDB2ConstraintType;
                primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toDB2ConstraintType instanceof ForeignConstraintClause) {
                final ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toDB2ConstraintType;
                foreignConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toDB2ConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
                this.setConstraint(null);
                this.setConstraintName(null);
                this.setCommentForConstraintName(false);
            }
            toDB2ConstraintType.toDB2String();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                final String tempIdentity = this.notNull.getIdentity();
                final StringBuffer temp_SB = new StringBuffer();
                final StringTokenizer st = new StringTokenizer(tempIdentity, ",");
                final String token1 = st.nextToken();
                temp_SB.append("IDENTITY(START");
                temp_SB.append(" WITH");
                final StringTokenizer stBrace = new StringTokenizer(token1, "(");
                if (stBrace.countTokens() > 1) {
                    stBrace.nextToken();
                    temp_SB.append(" " + stBrace.nextToken());
                }
                else {
                    temp_SB.append(" 1");
                }
                if (st.countTokens() > 0) {
                    final String token2 = st.nextToken();
                    temp_SB.append(" INCREMENT BY");
                    temp_SB.append(" " + token2);
                }
                else {
                    temp_SB.append(" INCREMENT BY");
                    temp_SB.append(" 1");
                }
                this.notNullStr = temp_SB.toString() + " ";
            }
            if (this.notNull.getNullStatus() != null && !this.notNull.getNullStatus().trim().equalsIgnoreCase("NULL")) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toMSSQLServerString() throws ConvertException {
        if (this.constraintName != null) {
            if (this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
                if (this.constraintName.length() < 22) {
                    this.constraintName = "CONS_NAME" + this.constraintName;
                }
                else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
                    this.constraintName = "CONS" + this.constraintName;
                }
                else {
                    this.constraintName = "CON" + this.constraintName;
                }
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toSQLServerConstraintType = this.getConstraintType();
            if (toSQLServerConstraintType instanceof DefaultConstraintClause) {
                final DefaultConstraintClause defaultConstraintClause = (DefaultConstraintClause)toSQLServerConstraintType;
            }
            toSQLServerConstraintType.toMSSQLServerString();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIncrement() == null) {
                String strTemp = this.notNull.getIdentity();
                if (strTemp != null && strTemp.indexOf(",") == -1 && strTemp.indexOf(")") != -1) {
                    strTemp = strTemp.substring(0, strTemp.indexOf(")"));
                    strTemp += ", 1)";
                    this.notNull.setIdentity(strTemp);
                }
            }
            if (this.notNull.getIdentity() != null) {
                this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toSybaseString() throws ConvertException {
        if (this.constraintName != null) {
            if (this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.constraintName != null) {
            if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
                if (this.constraintName.length() < 9) {
                    this.constraintName = "CONS_NAME" + this.constraintName;
                }
                else if (this.constraintName.length() > 9 && this.constraintName.length() < 14) {
                    this.constraintName = "CONS" + this.constraintName;
                }
                else {
                    this.constraintName = "CON" + this.constraintName;
                }
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toSybaseConstraintType = this.getConstraintType();
            if (toSybaseConstraintType instanceof DefaultConstraintClause) {
                final DefaultConstraintClause defaultConstraintClause = (DefaultConstraintClause)toSybaseConstraintType;
                if (this.getColumnName() != null) {
                    this.setConstraint(null);
                    this.setConstraintName(null);
                }
            }
            toSybaseConstraintType.toSybaseString();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                this.notNullStr = "IDENTITY ";
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toOracleString() throws ConvertException {
        this.setAutoIncrement(null);
        if (this.constraintName != null) {
            if ((this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) || (this.constraintName.startsWith("`") && this.constraintName.endsWith("`"))) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            this.setConstraintName(this.constraintName = CustomizeUtil.objectNamesToQuotedIdentifier(this.constraintName, SwisSQLUtils.getKeywords(1), null, 1));
        }
        if (this.constraintName != null && this.constraintName.length() > 30) {
            if (SwisSQLAPI.truncateConstraintCount > 99) {
                SwisSQLAPI.truncateConstraintCount = 0;
            }
            this.constraintName = this.constraintName.substring(0, 24) + "_ADV" + SwisSQLAPI.truncateConstraintCount;
            ++SwisSQLAPI.truncateConstraintCount;
            CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : Manual Intervention required. The constraint name changed as the length was greater than 30 characters. */ ";
            this.setConstraintName(this.constraintName);
        }
        if (this.constraintName != null) {
            if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".") || this.constraintName.startsWith("$")) {
                if (this.constraintName.length() < 22) {
                    this.constraintName = "CONS_NAME" + this.constraintName;
                }
                else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
                    this.constraintName = "CONS" + this.constraintName;
                }
                else {
                    this.constraintName = "CON" + this.constraintName;
                }
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toOracleConstraintType = this.getConstraintType();
            if (toOracleConstraintType instanceof ForeignConstraintClause) {
                final ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toOracleConstraintType;
                foreignConstraintClause.setColumnName(this.getColumnName());
                foreignConstraintClause.setTableNameFromCQS(this.tableNameFromCQS);
            }
            else if (toOracleConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                ((PrimaryOrUniqueConstraintClause)toOracleConstraintType).setTableNameFromCQS(this.tableNameFromCQS);
            }
            else if (toOracleConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
                this.setConstraint(null);
                this.setConstraintName(null);
                this.setCommentForConstraintName(false);
            }
            toOracleConstraintType.toOracleString();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                if (this.columnNameForSequence != null) {
                    final String identity = this.notNull.getIdentity();
                    final CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
                    final TableObject tableObj = new TableObject();
                    createSequenceObj.setSequence("SEQUENCE");
                    String oracleColumnName = this.columnNameForSequence;
                    if ((oracleColumnName.startsWith("[") && oracleColumnName.endsWith("]")) || (oracleColumnName.startsWith("`") && oracleColumnName.endsWith("`"))) {
                        oracleColumnName = oracleColumnName.substring(1, oracleColumnName.length() - 1);
                        if (SwisSQLOptions.retainQuotedIdentifierForOracle || oracleColumnName.indexOf(32) != -1) {
                            oracleColumnName = "\"" + oracleColumnName + "\"";
                        }
                        if (this.tableNameFromCQS != null) {
                            String str = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1) + "_SEQ";
                            final String str2 = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1);
                            final String str3 = this.tableNameFromCQS;
                            if (str.length() > 29) {
                                if (str2.length() > 25) {
                                    str = str2.substring(0, 26) + "_SEQ";
                                }
                                else if (str3.length() > 25) {
                                    str = str3.substring(0, 26) + "_SEQ";
                                }
                                if (str.length() > 27) {
                                    tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                                }
                                else {
                                    tableObj.setTableName("\"" + str + "\"");
                                }
                            }
                            else if (str.length() > 27) {
                                tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_S" + "\"");
                            }
                            else {
                                tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ" + "\"");
                            }
                        }
                        else {
                            tableObj.setTableName(oracleColumnName + "_SEQ");
                        }
                    }
                    else if (this.tableNameFromCQS != null) {
                        String str = this.tableNameFromCQS + oracleColumnName + "_SEQ";
                        final String str2 = this.tableNameFromCQS + oracleColumnName;
                        final String str3 = this.tableNameFromCQS;
                        if (str.length() > 29) {
                            if (str2.length() > 25) {
                                str = str2.substring(0, 26) + "_SEQ";
                            }
                            else if (str3.length() > 25) {
                                str = str3.substring(0, 26) + "_SEQ";
                            }
                            tableObj.setTableName(str);
                        }
                        else {
                            tableObj.setTableName(this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ");
                        }
                    }
                    else {
                        tableObj.setTableName(this.columnName + "_SEQ");
                    }
                    createSequenceObj.setSchemaName(tableObj);
                    if (identity.trim().equalsIgnoreCase("IDENTITY")) {
                        createSequenceObj.setStart("START");
                        createSequenceObj.setWith("WITH");
                        createSequenceObj.setStartValue("1");
                        createSequenceObj.setIncrementString("INCREMENT BY");
                        createSequenceObj.setIncrementValue("1");
                    }
                    else {
                        String tempIdentity = identity.trim().substring(8).trim();
                        tempIdentity = tempIdentity.substring(1, tempIdentity.length() - 1);
                        final StringTokenizer st = new StringTokenizer(tempIdentity, ",");
                        final String token1 = st.nextToken();
                        createSequenceObj.setStart("START");
                        createSequenceObj.setWith("WITH");
                        createSequenceObj.setStartValue(token1);
                        if (st.countTokens() > 0) {
                            final String token2 = st.nextToken();
                            createSequenceObj.setIncrementString("INCREMENT BY");
                            createSequenceObj.setIncrementValue(token2);
                        }
                        else {
                            createSequenceObj.setIncrementString("INCREMENT BY");
                            createSequenceObj.setIncrementValue("1");
                        }
                        if (this.notNull.getMaxValueOrNoMaxValue() != null) {
                            if (this.notNull.getMaxValueOrNoMaxValue().equalsIgnoreCase("NO MAXVALUE")) {
                                createSequenceObj.setMaxValueOrNoMaxValue("NOMAXVALUE");
                            }
                            else {
                                createSequenceObj.setMaxValueOrNoMaxValue(this.notNull.getMaxValueOrNoMaxValue());
                            }
                        }
                        if (this.notNull.getMinValueOrNoMinValue() != null) {
                            if (this.notNull.getMinValueOrNoMinValue().equalsIgnoreCase("NO MINVALUE")) {
                                createSequenceObj.setMinValueOrNoMinValue("NOMINVALUE");
                            }
                            else {
                                createSequenceObj.setMinValueOrNoMinValue(this.notNull.getMinValueOrNoMinValue());
                            }
                        }
                        if (this.notNull.getCycleOrNoCycle() != null) {
                            if (this.notNull.getCycleOrNoCycle().equalsIgnoreCase("NO CYCLE")) {
                                createSequenceObj.setCycleOrNoCycle("NOCYCLE");
                            }
                            else {
                                createSequenceObj.setCycleOrNoCycle(this.notNull.getCycleOrNoCycle());
                            }
                        }
                        if (this.notNull.getOrderOrNoOrder() != null) {
                            if (this.notNull.getOrderOrNoOrder().equalsIgnoreCase("NO ORDER")) {
                                createSequenceObj.setOrderOrNoOrder("NOORDER");
                            }
                            else {
                                createSequenceObj.setOrderOrNoOrder(this.notNull.getOrderOrNoOrder());
                            }
                        }
                        if (this.notNull.getCacheOrNoCache() != null) {
                            if (this.notNull.getCacheOrNoCache().equalsIgnoreCase("NO CACHE")) {
                                createSequenceObj.setCacheOrNoCache("NOCACHE");
                            }
                            else {
                                createSequenceObj.setCacheOrNoCache(this.notNull.getCacheOrNoCache());
                            }
                        }
                        if (SwisSQLOptions.generateTriggerForIdentity) {
                            final StringBuffer sb = new StringBuffer();
                            sb.append("CREATE OR REPLACE TRIGGER ");
                            final String triggerName = createSequenceObj.getSchemaName().getTableName();
                            if (triggerName.endsWith("_SEQ")) {
                                sb.append("TR_" + triggerName.substring(0, triggerName.lastIndexOf("_SEQ")));
                            }
                            if (triggerName.endsWith("_S")) {
                                sb.append("TR_" + triggerName.substring(0, triggerName.lastIndexOf("_S")));
                            }
                            sb.append("\nBEFORE INSERT ON ");
                            sb.append(this.tableNameFromCQS);
                            sb.append(" FOR EACH ROW");
                            sb.append("\nBEGIN\n\tSELECT ");
                            sb.append(triggerName + ".nextval");
                            sb.append(" INTO ");
                            sb.append(":new." + oracleColumnName);
                            sb.append(" FROM dual; ");
                            sb.append("\nEND;\n");
                            this.triggerForIdentity = sb.toString();
                        }
                    }
                    if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
                    }
                    else {
                        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
                    }
                    this.notNullStr = "";
                }
                else {
                    this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
                }
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toPostgreSQLString() throws ConvertException {
        if (this.constraintName != null) {
            if ((this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) || (this.constraintName.startsWith("`") && this.constraintName.endsWith("`"))) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
                if (this.constraintName.length() < 22) {
                    this.constraintName = "CONS_NAME" + this.constraintName;
                }
                else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
                    this.constraintName = "CONS" + this.constraintName;
                }
                else {
                    this.constraintName = "CON" + this.constraintName;
                }
            }
            this.setConstraintName(this.constraintName);
        }
        this.setAutoIncrement(null);
        if (this.getConstraintType() != null) {
            final ConstraintType toPostgreSQLConstraintType = this.getConstraintType();
            if (toPostgreSQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toPostgreSQLConstraintType;
                primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toPostgreSQLConstraintType instanceof ForeignConstraintClause) {
                final ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toPostgreSQLConstraintType;
                foreignConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toPostgreSQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            toPostgreSQLConstraintType.toPostgreSQLString();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                if (this.columnNameForSequence != null) {
                    final String identity = this.notNull.getIdentity();
                    final CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
                    final TableObject tableObj = new TableObject();
                    createSequenceObj.setSequence("SEQUENCE");
                    String postgreSQLColumnName = this.columnNameForSequence;
                    if ((postgreSQLColumnName.startsWith("[") && postgreSQLColumnName.endsWith("]")) || (postgreSQLColumnName.startsWith("`") && postgreSQLColumnName.endsWith("`"))) {
                        postgreSQLColumnName = postgreSQLColumnName.substring(1, postgreSQLColumnName.length() - 1);
                        if (postgreSQLColumnName.indexOf(32) != -1) {
                            postgreSQLColumnName = "\"" + postgreSQLColumnName + "\"";
                        }
                        if (this.tableNameFromCQS != null) {
                            String str = this.tableNameFromCQS + postgreSQLColumnName.substring(1, postgreSQLColumnName.length() - 1) + "_SEQ";
                            final String str2 = this.tableNameFromCQS + postgreSQLColumnName.substring(1, postgreSQLColumnName.length() - 1);
                            final String str3 = this.tableNameFromCQS;
                            if (str.length() > 29) {
                                if (str2.length() > 25) {
                                    str = str2.substring(0, 26) + "_SEQ";
                                }
                                else if (str3.length() > 25) {
                                    str = str3.substring(0, 26) + "_SEQ";
                                }
                                if (str.length() > 27) {
                                    tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                                }
                                else {
                                    tableObj.setTableName("\"" + str + "\"");
                                }
                            }
                            else if (str.length() > 27) {
                                tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgreSQLColumnName + "_S" + "\"");
                            }
                            else {
                                tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgreSQLColumnName + "_SEQ" + "\"");
                            }
                        }
                        else {
                            tableObj.setTableName(postgreSQLColumnName + "_SEQ");
                        }
                    }
                    else if (this.tableNameFromCQS != null) {
                        String str = this.tableNameFromCQS + postgreSQLColumnName + "_SEQ";
                        final String str2 = this.tableNameFromCQS + postgreSQLColumnName;
                        final String str3 = this.tableNameFromCQS;
                        if (str.length() > 29) {
                            if (str2.length() > 25) {
                                str = str2.substring(0, 26) + "_SEQ";
                            }
                            else if (str3.length() > 25) {
                                str = str3.substring(0, 26) + "_SEQ";
                            }
                            tableObj.setTableName(str);
                        }
                        else {
                            tableObj.setTableName(this.tableNameFromCQS + "_" + postgreSQLColumnName + "_SEQ");
                        }
                    }
                    else {
                        tableObj.setTableName(this.columnName + "_SEQ");
                    }
                    createSequenceObj.setSchemaName(tableObj);
                    if (identity.trim().equalsIgnoreCase("IDENTITY")) {
                        createSequenceObj.setStart("START");
                        createSequenceObj.setStartValue("1");
                        createSequenceObj.setIncrementString("INCREMENT ");
                        createSequenceObj.setIncrementValue("1");
                    }
                    else {
                        String tempIdentity = identity.trim().substring(8).trim();
                        tempIdentity = tempIdentity.substring(1, tempIdentity.length() - 1);
                        final StringTokenizer st = new StringTokenizer(tempIdentity, ",");
                        final String token1 = st.nextToken();
                        createSequenceObj.setStart("START");
                        createSequenceObj.setStartValue(token1);
                        if (st.countTokens() > 0) {
                            final String token2 = st.nextToken();
                            createSequenceObj.setIncrementString("INCREMENT ");
                            createSequenceObj.setIncrementValue(token2);
                        }
                        else {
                            createSequenceObj.setIncrementString("INCREMENT ");
                            createSequenceObj.setIncrementValue("1");
                        }
                        if (this.notNull.getMaxValueOrNoMaxValue() != null) {
                            if (this.notNull.getMaxValueOrNoMaxValue().equalsIgnoreCase("NO MAXVALUE")) {
                                createSequenceObj.setMaxValueOrNoMaxValue(null);
                            }
                            else {
                                createSequenceObj.setMaxValueOrNoMaxValue(this.notNull.getMaxValueOrNoMaxValue());
                            }
                        }
                        if (this.notNull.getMinValueOrNoMinValue() != null) {
                            if (this.notNull.getMinValueOrNoMinValue().equalsIgnoreCase("NO MINVALUE")) {
                                createSequenceObj.setMinValueOrNoMinValue(null);
                            }
                            else {
                                createSequenceObj.setMinValueOrNoMinValue(this.notNull.getMinValueOrNoMinValue());
                            }
                        }
                        if (this.notNull.getCycleOrNoCycle() != null) {
                            if (this.notNull.getCycleOrNoCycle().equalsIgnoreCase("NO CYCLE")) {
                                createSequenceObj.setCycleOrNoCycle(null);
                            }
                            else {
                                createSequenceObj.setCycleOrNoCycle(this.notNull.getCycleOrNoCycle());
                            }
                        }
                        if (this.notNull.getOrderOrNoOrder() != null) {
                            createSequenceObj.setOrderOrNoOrder(null);
                        }
                        if (this.notNull.getCacheOrNoCache() != null) {
                            if (this.notNull.getCacheOrNoCache().equalsIgnoreCase("NO CACHE")) {
                                createSequenceObj.setCacheOrNoCache(null);
                            }
                            else {
                                createSequenceObj.setCacheOrNoCache(this.notNull.getCacheOrNoCache());
                            }
                        }
                    }
                    if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
                    }
                    else {
                        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
                    }
                    this.notNullStr = "";
                }
                else {
                    this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
                }
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toANSIString() throws ConvertException {
        this.setAutoIncrement(null);
        if (this.constraintName != null) {
            if ((this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) || (this.constraintName.startsWith("`") && this.constraintName.endsWith("`"))) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
                if (this.constraintName.length() < 22) {
                    this.constraintName = "CONS_NAME" + this.constraintName;
                }
                else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
                    this.constraintName = "CONS" + this.constraintName;
                }
                else {
                    this.constraintName = "CON" + this.constraintName;
                }
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toANSISQLConstraintType = this.getConstraintType();
            if (toANSISQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toANSISQLConstraintType;
                primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toANSISQLConstraintType instanceof ForeignConstraintClause) {
                final ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toANSISQLConstraintType;
                foreignConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toANSISQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            toANSISQLConstraintType.toANSIString();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toMySQLString() throws ConvertException {
        if (this.constraintName != null) {
            if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            else if (!this.constraintName.startsWith("`") && !this.constraintName.endsWith("`")) {
                this.constraintName = "`" + this.constraintName + "`";
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toMySQLConstraintType = this.getConstraintType();
            if (toMySQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toMySQLConstraintType;
                primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toMySQLConstraintType instanceof ForeignConstraintClause) {
                final ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toMySQLConstraintType;
                foreignConstraintClause.setColumnName(this.getColumnName());
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            else if (toMySQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            toMySQLConstraintType.toMySQLString();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toInformixString() throws ConvertException {
        this.setAutoIncrement(null);
        if (this.constraintName != null) {
            if ((this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) || (this.constraintName.startsWith("`") && this.constraintName.endsWith("`"))) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toInformixConstraintType = this.getConstraintType();
            if (toInformixConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toInformixConstraintType;
                primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            else if (toInformixConstraintType instanceof ForeignConstraintClause) {
                final ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toInformixConstraintType;
                foreignConstraintClause.setColumnName(this.getColumnName());
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            else if (toInformixConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            toInformixConstraintType.toInformixString();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toTimesTenString() throws ConvertException {
        if (this.constraintName != null) {
            if (this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            else if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
                this.constraintName = "\"" + this.constraintName + "\"";
            }
            this.constraintName = CustomizeUtil.objectNamesToQuotedIdentifier(this.constraintName, SwisSQLUtils.getKeywords(10), null, 10);
            if (this.constraintName.length() > 30) {
                if (SwisSQLAPI.truncateConstraintCount > 99) {
                    SwisSQLAPI.truncateConstraintCount = 0;
                }
                this.constraintName = this.constraintName.substring(0, 24) + "_ADV" + SwisSQLAPI.truncateConstraintCount;
                ++SwisSQLAPI.truncateConstraintCount;
                CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : Manual Intervention may be required. The constraint name changed as the length was greater than 30 characters. */ ";
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toTimesTenConstraintType = this.getConstraintType();
            if (toTimesTenConstraintType instanceof DefaultConstraintClause) {
                final DefaultConstraintClause defaultConstraintClause = (DefaultConstraintClause)toTimesTenConstraintType;
                if (this.getColumnName() != null) {
                    this.setConstraint(null);
                    this.setConstraintName(null);
                }
            }
            toTimesTenConstraintType.toTimesTenString();
        }
        else if (this.notNull != null) {
            if (this.constraintType == null) {
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                if (this.columnNameForSequence != null) {
                    final String identity = this.notNull.getIdentity();
                    final TableObject tableObj = new TableObject();
                    final CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
                    createSequenceObj.setSequence("SEQUENCE");
                    String columnName = this.columnNameForSequence;
                    if ((columnName.startsWith("[") && columnName.endsWith("]")) || (columnName.startsWith("`") && columnName.endsWith("`"))) {
                        columnName = columnName.substring(1, columnName.length() - 1);
                        if (columnName.indexOf(32) != -1) {
                            columnName = "\"" + columnName + "\"";
                        }
                        if (this.tableNameFromCQS != null) {
                            String str = this.tableNameFromCQS + columnName.substring(1, columnName.length() - 1) + "_SEQ";
                            final String str2 = this.tableNameFromCQS + columnName.substring(1, columnName.length() - 1);
                            final String str3 = this.tableNameFromCQS;
                            if (str.length() > 29) {
                                if (str2.length() > 25) {
                                    str = str2.substring(0, 26) + "_SEQ";
                                }
                                else if (str3.length() > 25) {
                                    str = str3.substring(0, 26) + "_SEQ";
                                }
                                if (str.length() > 27) {
                                    tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                                }
                                else {
                                    tableObj.setTableName("\"" + str + "\"");
                                }
                            }
                            else if (str.length() > 27) {
                                tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + columnName + "_S" + "\"");
                            }
                            else {
                                tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + columnName + "_SEQ" + "\"");
                            }
                        }
                        else {
                            tableObj.setTableName(columnName + "_SEQ");
                        }
                    }
                    else if (this.tableNameFromCQS != null) {
                        String str = this.tableNameFromCQS + columnName + "_SEQ";
                        final String str2 = this.tableNameFromCQS + columnName;
                        final String str3 = this.tableNameFromCQS;
                        if (str.length() > 29) {
                            if (str2.length() > 25) {
                                str = str2.substring(0, 26) + "_SEQ";
                            }
                            else if (str3.length() > 25) {
                                str = str3.substring(0, 26) + "_SEQ";
                            }
                            tableObj.setTableName(str);
                        }
                        else {
                            tableObj.setTableName(this.tableNameFromCQS + "_" + columnName + "_SEQ");
                        }
                    }
                    else {
                        tableObj.setTableName(this.columnName + "_SEQ");
                    }
                    createSequenceObj.setSchemaName(tableObj);
                    if (!identity.trim().equalsIgnoreCase("IDENTITY")) {
                        String tempIdentity = identity.trim().substring(8).trim();
                        tempIdentity = tempIdentity.substring(1, tempIdentity.length() - 1);
                        final StringTokenizer st = new StringTokenizer(tempIdentity, ",");
                        final String token1 = st.nextToken();
                        createSequenceObj.setMinValueOrNoMinValue("MINVALUE " + token1);
                        if (st.countTokens() > 0) {
                            final String token2 = st.nextToken();
                            createSequenceObj.setIncrementString("INCREMENT BY");
                            createSequenceObj.setIncrementValue(token2);
                        }
                        else {
                            createSequenceObj.setIncrementString("INCREMENT BY");
                            createSequenceObj.setIncrementValue("1");
                        }
                    }
                    if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention may be required */\n";
                    }
                    else {
                        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention may be required */\n";
                    }
                    this.notNullStr = "";
                }
                else {
                    this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
                }
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toNetezzaString() throws ConvertException {
        this.setAutoIncrement(null);
        if (this.constraintName != null) {
            if ((this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) || (this.constraintName.startsWith("`") && this.constraintName.endsWith("`"))) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
                if (this.constraintName.length() < 22) {
                    this.constraintName = "CONS_NAME" + this.constraintName;
                }
                else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
                    this.constraintName = "CONS" + this.constraintName;
                }
                else {
                    this.constraintName = "CON" + this.constraintName;
                }
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toNetezzaSQLConstraintType = this.getConstraintType();
            if (toNetezzaSQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toNetezzaSQLConstraintType;
                primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toNetezzaSQLConstraintType instanceof ForeignConstraintClause) {
                final ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toNetezzaSQLConstraintType;
                foreignConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toNetezzaSQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            toNetezzaSQLConstraintType.toNetezzaString();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public void toTeradataString() throws ConvertException {
        this.setAutoIncrement(null);
        if (this.constraintName != null) {
            if ((this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) || (this.constraintName.startsWith("`") && this.constraintName.endsWith("`"))) {
                this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
                if (this.constraintName.indexOf(32) != -1) {
                    this.constraintName = "\"" + this.constraintName + "\"";
                }
            }
            if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
                if (this.constraintName.length() < 22) {
                    this.constraintName = "CONS_NAME" + this.constraintName;
                }
                else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
                    this.constraintName = "CONS" + this.constraintName;
                }
                else {
                    this.constraintName = "CON" + this.constraintName;
                }
            }
            this.setConstraintName(this.constraintName);
        }
        if (this.getConstraintType() != null) {
            final ConstraintType toTeradataSQLConstraintType = this.getConstraintType();
            if (toTeradataSQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toTeradataSQLConstraintType;
                primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toTeradataSQLConstraintType instanceof ForeignConstraintClause) {
                final ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toTeradataSQLConstraintType;
                foreignConstraintClause.setColumnName(this.getColumnName());
            }
            else if (toTeradataSQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
                this.setConstraint(null);
                this.setConstraintName(null);
            }
            toTeradataSQLConstraintType.toTeradataString();
        }
        else if (this.notNull != null) {
            this.notNullStr = "";
            if (this.notNull.getIdentity() != null) {
                this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            }
            if (this.notNull.getNullStatus() != null) {
                this.notNullStr += this.notNull.getNullStatus();
            }
        }
    }
    
    public ConstraintClause copyObjectValues() {
        final ConstraintClause dupConstraintClause = new ConstraintClause();
        dupConstraintClause.setObjectContext(this.context);
        final ConstraintType orgConstraintType = this.getConstraintType();
        if (orgConstraintType != null) {
            ConstraintType newConstraintType = null;
            if (orgConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                final PrimaryOrUniqueConstraintClause orgPrimOrUniqueClause = (PrimaryOrUniqueConstraintClause)orgConstraintType;
                newConstraintType = orgPrimOrUniqueClause.copyObjectValues();
            }
            else if (orgConstraintType instanceof ForeignConstraintClause) {
                final ForeignConstraintClause orgForeignClause = (ForeignConstraintClause)orgConstraintType;
                newConstraintType = orgForeignClause.copyObjectValues();
            }
            else if (orgConstraintType instanceof CheckConstraintClause) {
                final CheckConstraintClause orgCheckClause = (CheckConstraintClause)orgConstraintType;
                newConstraintType = orgCheckClause.copyObjectValues();
            }
            else if (orgConstraintType instanceof DefaultConstraintClause) {
                final DefaultConstraintClause orgDefaultClause = (DefaultConstraintClause)orgConstraintType;
                newConstraintType = orgDefaultClause.copyObjectValues();
            }
            dupConstraintClause.setConstraintType(newConstraintType);
        }
        dupConstraintClause.setConstraintName(this.getConstraintName());
        dupConstraintClause.setAutoIncrement(this.getAutoIncrement());
        dupConstraintClause.setConstraint(this.getConstraint());
        dupConstraintClause.setNotNull(this.getNotNull());
        return dupConstraintClause;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.autoIncrement != null) {
            sb.append(this.autoIncrement + " ");
        }
        if (this.constraint != null) {
            sb.append(this.constraint.toUpperCase() + " ");
        }
        if (this.constraintName != null) {
            if (this.context != null) {
                final String temp = this.context.getEquivalent(this.constraintName).toString();
                sb.append(temp + " ");
            }
            else {
                sb.append(this.constraintName + " ");
            }
        }
        if (this.constraintType != null) {
            if (this.constraintType instanceof PrimaryOrUniqueConstraintClause) {
                ((PrimaryOrUniqueConstraintClause)this.constraintType).setObjectContext(this.context);
                sb.append(((PrimaryOrUniqueConstraintClause)this.constraintType).toString());
            }
            else if (this.constraintType instanceof ForeignConstraintClause) {
                ((ForeignConstraintClause)this.constraintType).setObjectContext(this.context);
                sb.append(((ForeignConstraintClause)this.constraintType).toString());
            }
            else if (this.constraintType instanceof CheckConstraintClause) {
                ((CheckConstraintClause)this.constraintType).setObjectContext(this.context);
                sb.append(((CheckConstraintClause)this.constraintType).toString());
            }
            else if (this.constraintType instanceof DefaultConstraintClause) {
                ((DefaultConstraintClause)this.constraintType).setObjectContext(this.context);
                sb.append(((DefaultConstraintClause)this.constraintType).toString());
            }
        }
        if (this.notNullStr != null) {
            sb.append(this.notNullStr);
        }
        if (this.commentForConstraintName) {
            sb.append(" /*SwisSQL Message : Manual Intervention required. The constraint name changed as the length was greater than " + this.characterLengthForComment + " characters.*/ ");
        }
        return sb.toString();
    }
}
