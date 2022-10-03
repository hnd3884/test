package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import java.util.Map;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.StringTokenizer;
import java.util.Hashtable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.ArrayList;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class CreateColumn
{
    private String columnName;
    private String nullStatus;
    private String onDefault;
    private String defaultValue;
    private String defaultOpenBrace;
    private String defaultClosedBrace;
    private FunctionCalls functionCall;
    private String identity;
    Datatype datatype;
    private String userDefinedDatatype;
    private Vector constraintVector;
    private Vector constraintNullVector;
    private NotNull notNull;
    private String tableNameFromCQS;
    private String startString;
    private String withString;
    private String increment;
    private String byString;
    private String generated;
    private String always;
    private String byForAlways;
    private String defaultForIdentity;
    private String asForIdentity;
    private boolean notNullSetFromCreateQueryStatement;
    private String collate;
    private String collationName;
    private String autoIncrement;
    private boolean booleanOracle;
    private boolean booleanDb2;
    private UserObjectContext context;
    private DatatypeMapping datatypeMapping;
    private boolean isAlterStatement;
    private String datapageStorageLevel;
    private String createSequenceStr;
    private ArrayList defaultExpList;
    private String onUpdateClause;
    Datatype castDatatype;
    private SelectColumn computedColumnExpression;
    private String computedColumnAS;
    private String sparseStr;
    private static Hashtable userdefinedDatatypes;
    private static Hashtable userConfiguredDatatypes;
    
    public CreateColumn() {
        this.notNullSetFromCreateQueryStatement = false;
        this.booleanOracle = false;
        this.booleanDb2 = false;
        this.context = null;
        this.isAlterStatement = false;
        this.onUpdateClause = null;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setDatatype(final Datatype datatype) {
        this.datatype = datatype;
    }
    
    public void setUserDefinedDatatype(final String userDefinedDatatype) {
        this.userDefinedDatatype = userDefinedDatatype;
    }
    
    public void setDefault(final String onDefault) {
        this.onDefault = onDefault;
    }
    
    public void setDefaultOpenBrace(final String defaultOpenBrace) {
        this.defaultOpenBrace = defaultOpenBrace;
    }
    
    public void setDefaultClosedBrace(final String defaultClosedBrace) {
        this.defaultClosedBrace = defaultClosedBrace;
    }
    
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public void setDefaultFunction(final FunctionCalls functionCall) {
        this.functionCall = functionCall;
    }
    
    public void setDefaultExpression(final ArrayList defaultExpList) {
        this.defaultExpList = defaultExpList;
    }
    
    public void setIdentity(final String identity) {
        this.identity = identity;
    }
    
    public void setCastDatatype(final Datatype castDatatype) {
        this.castDatatype = castDatatype;
    }
    
    public void setNotNull(final NotNull notNull) {
        this.notNull = notNull;
        this.identity = notNull.getIdentity();
        this.nullStatus = notNull.getNullStatus();
        this.startString = notNull.getStart();
        this.withString = notNull.getWith();
        this.increment = notNull.getIncrement();
        this.byString = notNull.getBy();
    }
    
    public void setTableNameFromCreateQueryStmt(final String tableNameFromCQS) {
        this.tableNameFromCQS = tableNameFromCQS;
    }
    
    public void setNullStatus(final String nullStatus) {
        this.nullStatus = nullStatus;
    }
    
    public void setNotNullSetFromCreateQueryStatement(final boolean setNotNullValue) {
        this.notNullSetFromCreateQueryStatement = setNotNullValue;
    }
    
    public void setConstraintClause(final Vector constraintVector) {
        this.constraintVector = constraintVector;
    }
    
    public void setCollate(final String collate) {
        this.collate = collate;
    }
    
    public void setCollationName(final String collationName) {
        this.collationName = collationName;
    }
    
    public void setGenerated(final String generated) {
        this.generated = generated;
    }
    
    public void setAlways(final String always) {
        this.always = always;
    }
    
    public void setOnUpdateClause(final String val) {
        this.onUpdateClause = val;
    }
    
    public void setSparseString(final String sparse) {
        this.sparseStr = sparse;
    }
    
    public String getOnUpdateClause() {
        return this.onUpdateClause;
    }
    
    public String getGenerated() {
        return this.generated;
    }
    
    public void setByForAlways(final String byForAlways) {
        this.byForAlways = byForAlways;
    }
    
    public void setDefaultForIdentity(final String defaultForIdentity) {
        this.defaultForIdentity = defaultForIdentity;
    }
    
    public void setIdentityAs(final String asForIdentity) {
        this.asForIdentity = asForIdentity;
    }
    
    public void setAutoIncrement(final String autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
    
    public void setDatatypeMapping(final DatatypeMapping mapping) {
        this.datatypeMapping = mapping;
    }
    
    public void setAlterStatement(final boolean isAlterStatement) {
        this.isAlterStatement = isAlterStatement;
    }
    
    public void setDatapageStorageLevel(final String datapageStorageLevel) {
        this.datapageStorageLevel = datapageStorageLevel;
    }
    
    public void setCreateSequenceString(final String createSequenceStr) {
        this.createSequenceStr = createSequenceStr;
    }
    
    public void setComputedColumnExpression(final SelectColumn computedColExpr) {
        this.computedColumnExpression = computedColExpr;
    }
    
    public void setComputedColumnAS(final String as) {
        this.computedColumnAS = as;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public Datatype getDatatype() {
        return this.datatype;
    }
    
    public String getUserDefinedDatatype() {
        return this.userDefinedDatatype;
    }
    
    public String getDefault() {
        return this.onDefault;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public String getAutoIncrement() {
        return this.autoIncrement;
    }
    
    public FunctionCalls getDefaultFunction() {
        return this.functionCall;
    }
    
    public ArrayList getDefaultExpression() {
        return this.defaultExpList;
    }
    
    public String getIdentity() {
        return this.identity;
    }
    
    public Vector getConstraintClause() {
        return this.constraintVector;
    }
    
    public String getNullStatus() {
        return this.nullStatus;
    }
    
    public String getDatapageStorageLevel() {
        return this.datapageStorageLevel;
    }
    
    public String getCreateSequenceString() {
        return this.createSequenceStr;
    }
    
    public Datatype getCastDatatype() {
        return this.castDatatype;
    }
    
    public SelectColumn getComputedColumnExpression() {
        return this.computedColumnExpression;
    }
    
    public String getComputedColumnAS() {
        return this.computedColumnAS;
    }
    
    public String getSparseString() {
        return this.sparseStr;
    }
    
    public void toDB2String() throws ConvertException {
        this.setDatapageStorageLevel(null);
        if (this.columnName != null && ((this.columnName.startsWith("[") && this.columnName.endsWith("]")) || (this.columnName.startsWith("`") && this.columnName.endsWith("`")))) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
                this.columnName = "\"" + this.columnName + "\"";
            }
        }
        this.constraintNullVector = new Vector();
        if (this.identity != null) {
            final StringBuffer temp_SB = new StringBuffer();
            if (this.identity.trim().equalsIgnoreCase("IDENTITY")) {
                if (this.always == null) {
                    temp_SB.append("GENERATED");
                    temp_SB.append(" BY");
                    temp_SB.append(" DEFAULT");
                    temp_SB.append(" AS ");
                }
                temp_SB.append("IDENTITY(START");
                temp_SB.append(" WITH");
                temp_SB.append(" 1");
                temp_SB.append(" INCREMENT BY");
                temp_SB.append(" 1)");
                this.identity = temp_SB.toString();
            }
            else {
                if (this.always == null) {
                    temp_SB.append("GENERATED");
                    temp_SB.append(" BY");
                    temp_SB.append(" DEFAULT");
                    temp_SB.append(" AS ");
                }
                String tempIdentity = this.identity.trim().substring(8).trim();
                tempIdentity = tempIdentity.substring(1, tempIdentity.length() - 1);
                final StringTokenizer st = new StringTokenizer(tempIdentity, ",");
                final String token1 = st.nextToken();
                temp_SB.append("IDENTITY(START");
                temp_SB.append(" WITH");
                temp_SB.append(" " + token1);
                if (st.countTokens() > 0) {
                    final String token2 = st.nextToken();
                    temp_SB.append(" INCREMENT BY");
                    temp_SB.append(" " + token2 + ")");
                }
                else {
                    temp_SB.append(" INCREMENT BY");
                    temp_SB.append(" 1)");
                }
                this.identity = temp_SB.toString();
            }
        }
        this.setCollate(null);
        this.setCollationName(null);
        if (this.getConstraintClause() != null) {
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toDB2ConstraintClause = changeConstraintVector.get(i);
                    if (toDB2ConstraintClause != null) {
                        if (toDB2ConstraintClause.getConstraintType() != null) {
                            final ConstraintType toDB2ConstraintType = toDB2ConstraintClause.getConstraintType();
                            if (toDB2ConstraintType instanceof PrimaryOrUniqueConstraintClause && (this.getColumnName() != null || toDB2ConstraintClause.getConstraintName() != null) && !this.notNullSetFromCreateQueryStatement) {
                                this.setNullStatus("NOT NULL");
                            }
                        }
                        toDB2ConstraintClause.setColumnName(this.getColumnName());
                        toDB2ConstraintClause.toDB2String();
                        this.constraintNullVector.add(toDB2ConstraintClause);
                    }
                }
            }
        }
        if (this.getNullStatus() != null && !this.getNullStatus().trim().equalsIgnoreCase("NULL") && !this.isAlterStatement) {
            this.constraintNullVector.add(this.getNullStatus());
        }
        if (this.getDatatype() != null) {
            if (!this.mapDatatype(this, this.datatypeMapping)) {
                final Datatype toDB2Datatype = this.getDatatype();
                if (toDB2Datatype instanceof DateClass) {
                    final DateClass dateClass = (DateClass)toDB2Datatype;
                    if (dateClass.getDatatypeName().equalsIgnoreCase("DATE")) {
                        dateClass.setDatatypeName("TIMESTAMP");
                    }
                    dateClass.toDB2String();
                }
                else if (toDB2Datatype instanceof BinClass) {
                    final BinClass binClass = (BinClass)toDB2Datatype;
                    if (binClass.getDatatypeName().trim().equalsIgnoreCase("BOOLEAN")) {
                        this.booleanDb2 = true;
                    }
                    else {
                        binClass.toDB2String();
                    }
                }
                else {
                    toDB2Datatype.toDB2String();
                    if (toDB2Datatype instanceof CharacterClass) {
                        this.enumValuesConvertedToCheckConstraints(toDB2Datatype, this.constraintNullVector);
                    }
                }
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() > 0) {
            if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
                if (CreateColumn.userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
                    this.setUserDefinedDatatype(CreateColumn.userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
                }
                else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
                    this.setUserDefinedDatatype("VARGRAPHIC(36)");
                }
                else if (this.userDefinedDatatype.equalsIgnoreCase("ROWID")) {
                    this.setUserDefinedDatatype("CHAR(16) FOR BIT DATA");
                }
                else if (this.userDefinedDatatype.equalsIgnoreCase("SQL_VARIANT")) {
                    this.setUserDefinedDatatype("VARCHAR(800)");
                }
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
                this.setUserDefinedDatatype("VARGRAPHIC(36)");
            }
            else if (this.userDefinedDatatype.equalsIgnoreCase("ROWID")) {
                this.setUserDefinedDatatype("CHAR(16) FOR BIT DATA");
            }
            else if (this.userDefinedDatatype.equalsIgnoreCase("SQL_VARIANT")) {
                this.setUserDefinedDatatype("VARCHAR(800)");
            }
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnExpression(this.getComputedColumnExpression().toDB2Select(null, null));
        }
        if (this.getDefaultValue() != null) {
            this.setDefaultOpenBrace(null);
            this.setDefaultClosedBrace(null);
            if (this.getDatatype() != null) {
                final Datatype toDB2Datatype = this.getDatatype();
                if (toDB2Datatype instanceof NumericClass && this.getDefaultValue().startsWith("'") && this.getDefaultValue().endsWith("'")) {
                    this.setDefaultValue(this.defaultValue.substring(1, this.defaultValue.length() - 1));
                }
                else if (toDB2Datatype instanceof CharacterClass && this.getDefaultValue().startsWith("\"") && this.getDefaultValue().endsWith("\"")) {
                    this.setDefaultValue("'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'");
                }
                else if (this.getDatatype() instanceof DateClass) {
                    if (this.defaultValue.equals("'0000-00-00 00:00:00'") || this.defaultValue.equals("0000-00-00 00:00:00")) {
                        this.setDefaultValue("'0001-01-01 00:00:00'");
                    }
                    else if (this.defaultValue.equals("'0000-00-00'") || this.defaultValue.equals("0000-00-00")) {
                        this.setDefaultValue("'0001-01-01'");
                    }
                    else if (this.defaultValue.trim().equalsIgnoreCase("SYSDATE")) {
                        this.setDefaultValue("CURRENT TIMESTAMP");
                    }
                }
                else if (toDB2Datatype instanceof BinClass) {
                    final BinClass binClass = (BinClass)toDB2Datatype;
                    if (binClass.getDatatypeName().trim().equalsIgnoreCase("BLOB")) {
                        this.setDefaultValue("BLOB(" + this.defaultValue + ")");
                    }
                }
            }
            else if (this.defaultValue.trim().equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
                this.setDefaultValue("CURRENT DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
                this.setDefaultValue("CURRENT TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                this.setDefaultValue("CURRENT TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER");
            }
        }
        else if (this.getDefaultFunction() != null) {
            if (this.getDefaultFunction().getFunctionName() != null) {
                final String functionColName = this.getDefaultFunction().getFunctionName().getColumnName();
                if (functionColName.equalsIgnoreCase("GETDATE")) {
                    this.setDefaultClosedBrace(null);
                    this.setDefaultOpenBrace(null);
                    this.functionCall.setOpenBracesForFunctionNameRequired(false);
                }
                else if (functionColName.equalsIgnoreCase("EMPTY_CLOB")) {
                    this.setDefaultFunction(null);
                }
            }
            if (this.functionCall != null) {
                this.setDefaultFunction(this.functionCall.toDB2Select(null, null));
            }
        }
        else if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            for (int i = 0; i < this.defaultExpList.size(); ++i) {
                if (this.defaultExpList.get(i) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(i);
                    final SelectColumn db2SC = sc.toDB2Select(null, null);
                    final Vector colExpr = db2SC.getColumnExpression();
                    if (colExpr != null) {
                        for (int n = 0; n < colExpr.size(); ++n) {
                            final Object obj = colExpr.get(n);
                            if (obj instanceof FunctionCalls) {
                                ((FunctionCalls)obj).setOpenBracesForFunctionNameRequired(false);
                            }
                            if (obj instanceof SelectColumn) {
                                final SelectColumn scTemp = (SelectColumn)obj;
                                final Vector vtemp = scTemp.getColumnExpression();
                                if (scTemp.getOpenBrace() != null && scTemp.getCloseBrace() != null && vtemp.size() == 1) {
                                    scTemp.setOpenBrace(null);
                                    scTemp.setCloseBrace(null);
                                }
                            }
                        }
                    }
                    newExpList.add(db2SC);
                }
                else {
                    newExpList.add(this.defaultExpList.get(i));
                }
            }
            this.setDefaultExpression(newExpList);
            this.setDefaultOpenBrace(null);
            this.setDefaultClosedBrace(null);
        }
        if (this.autoIncrement != null) {
            final StringBuffer temp_SB = new StringBuffer();
            temp_SB.append("GENERATED");
            temp_SB.append(" BY");
            temp_SB.append(" DEFAULT");
            temp_SB.append(" AS ");
            temp_SB.append("IDENTITY(START");
            temp_SB.append(" WITH");
            temp_SB.append(" 1");
            temp_SB.append(" INCREMENT BY");
            temp_SB.append(" 1)");
            this.identity = temp_SB.toString();
            this.autoIncrement = null;
        }
        if (this.getCastDatatype() != null) {}
    }
    
    public void toMSSQLServerString() throws ConvertException {
        this.setDatapageStorageLevel(null);
        this.constraintNullVector = new Vector();
        if (this.columnName != null && this.columnName.equalsIgnoreCase("FUNCTION")) {
            this.setColumnName(this.columnName + "_COLUMN");
        }
        this.generated = null;
        this.always = null;
        this.asForIdentity = null;
        this.byForAlways = null;
        this.defaultForIdentity = null;
        this.asForIdentity = null;
        if (this.nullStatus == null && SwisSQLOptions.sybaseNotNullConstraint && SwisSQLOptions.fromSybase && (this.getDatatype() != null || this.getUserDefinedDatatype() != null)) {
            this.nullStatus = "NOT NULL";
        }
        if (this.columnName != null) {
            if (this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
                this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
                if (this.columnName.indexOf(32) != -1) {
                    this.columnName = "\"" + this.columnName + "\"";
                }
            }
            String[] keywords = null;
            if (SwisSQLUtils.getKeywords(2) != null) {
                keywords = SwisSQLUtils.getKeywords(2);
                if (this.columnName.trim().length() > 0) {
                    this.columnName = CustomizeUtil.objectNamesToBracedIdentifier(this.columnName, keywords, null);
                }
            }
        }
        if (this.getConstraintClause() != null) {
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toSQLServerConstraintClause = changeConstraintVector.get(i);
                    if (toSQLServerConstraintClause != null) {
                        if (toSQLServerConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause) {
                            toSQLServerConstraintClause.setColumnName(this.getColumnName());
                            final PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintClause.getConstraintType();
                            boolean ccToBeAdded = false;
                            if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                                this.constraintNullVector.add(",\n\t");
                                final CreateColumn cc = new CreateColumn();
                                if (this.getConstraintClause() != null) {
                                    final Vector primaryConstraintVector = new Vector();
                                    for (int index = 0; index < this.getConstraintClause().size(); ++index) {
                                        if (this.getConstraintClause().get(index) instanceof PrimaryOrUniqueConstraintClause) {
                                            primaryConstraintVector.add(this.getConstraintClause().get(index));
                                            cc.setNullStatus(null);
                                            cc.setDefaultValue(null);
                                            cc.setDefault(null);
                                            cc.toOracleString();
                                            final NotNull notnullObj = new NotNull();
                                            notnullObj.setNullStatus(null);
                                            notnullObj.setIdentity(null);
                                            cc.setNotNull(notnullObj);
                                            ccToBeAdded = true;
                                        }
                                    }
                                    cc.setConstraintClause(primaryConstraintVector);
                                }
                                if (ccToBeAdded) {
                                    this.constraintNullVector.add(cc);
                                }
                            }
                            if (primaryConstraintClause.getConstraintColumnNames() != null && !this.notNullSetFromCreateQueryStatement) {
                                this.setNullStatus("");
                            }
                        }
                        if (toSQLServerConstraintClause.getAutoIncrement() != null) {
                            this.setIdentity("IDENTITY (1,1)");
                            toSQLServerConstraintClause.setAutoIncrement(null);
                        }
                        toSQLServerConstraintClause.setColumnName(this.getColumnName());
                        toSQLServerConstraintClause.toMSSQLServerString();
                        this.constraintNullVector.add(toSQLServerConstraintClause);
                    }
                }
            }
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnAS("AS");
            this.setComputedColumnExpression(this.getComputedColumnExpression().toMSSQLServerSelect(null, null));
        }
        if (this.getNullStatus() != null) {
            this.constraintNullVector.add(this.getNullStatus());
        }
        if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
            final Datatype toMSSQLServerDatatype = this.getDatatype();
            toMSSQLServerDatatype.toMSSQLServerString();
            if (toMSSQLServerDatatype instanceof CharacterClass) {
                this.enumValuesConvertedToCheckConstraints(toMSSQLServerDatatype, this.constraintNullVector);
            }
        }
        if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            final int size = this.defaultExpList.size();
            if (size == 1) {
                if (this.defaultExpList.get(0) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(0);
                    final Vector expList = sc.getColumnExpression();
                    if (expList.size() == 1) {
                        final Object obj = expList.get(0);
                        if (obj instanceof String) {
                            this.setDefaultValue((String)obj);
                        }
                        else if (obj instanceof TableColumn) {
                            final TableColumn tc = (TableColumn)obj;
                            final String colName = tc.getColumnName();
                            if (colName != null) {
                                this.setDefaultValue(colName);
                            }
                        }
                    }
                }
                else {
                    final ArrayList expWithoutSc = this.getDefaultExpression();
                    if (expWithoutSc.size() == 1 && expWithoutSc.get(0) instanceof String) {
                        this.setDefaultValue(expWithoutSc.get(0));
                    }
                }
            }
        }
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.toUpperCase().startsWith("G'") && this.defaultValue.toUpperCase().endsWith("'")) {
                final String temp = this.defaultValue.substring(1);
                this.setDefaultValue(temp);
            }
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("GETDATE()");
            }
            if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
                this.setDefaultValue("NEWID()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE") || this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME") || this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") || this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("SYSTEM_USER");
            }
            else if (this.defaultValue.toUpperCase().startsWith("EMPTY_BLOB") || this.defaultValue.toUpperCase().startsWith("EMPTY_CLOB")) {
                this.setDefaultValue("''");
            }
            else if (this.defaultValue.equals("'0000-00-00 00:00:00'") || this.defaultValue.equals("0000-00-00 00:00:00")) {
                this.setDefaultValue("'1753-01-01 00:00:00'");
            }
            else if (this.defaultValue.equals("\"\"")) {
                this.setDefaultValue("' '");
            }
            else if (this.defaultValue.startsWith("\"") && this.defaultValue.endsWith("\"")) {
                this.setDefaultValue("'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'");
            }
            else if (this.defaultValue.equalsIgnoreCase("true")) {
                this.setDefaultValue("1");
            }
            else if (this.defaultValue.equalsIgnoreCase("false")) {
                this.setDefaultValue("0");
            }
            else if (this.getDefault() != null && this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
            if (this.defaultValue.startsWith("'")) {
                final Datatype type = this.getDatatype();
                if (type != null && type instanceof BinClass) {
                    final String typeName = type.getDatatypeName();
                    if (typeName != null && typeName.equalsIgnoreCase("varbinary")) {
                        this.setDefaultValue("CONVERT(VARBINARY, " + this.defaultValue + ")");
                    }
                }
            }
        }
        else if (this.getDefaultFunction() != null) {
            final String fnName = this.functionCall.getFunctionName().getColumnName();
            if (fnName != null && (fnName.toUpperCase().startsWith("EMPTY_BLOB") || fnName.toUpperCase().startsWith("EMPTY_CLOB"))) {
                this.functionCall.setFunctionName(null);
                final Vector fnArgs = new Vector();
                fnArgs.add("''");
                this.functionCall.setFunctionArguments(fnArgs);
                this.functionCall.setOpenBracesForFunctionNameRequired(false);
            }
            else {
                this.setDefaultFunction(this.functionCall.toMSSQLServerSelect(null, null));
            }
        }
        else if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            for (int i = 0; i < this.defaultExpList.size(); ++i) {
                if (this.defaultExpList.get(i) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(i);
                    newExpList.add(sc.toMSSQLServerSelect(null, null));
                }
                else {
                    newExpList.add(this.defaultExpList.get(i));
                }
            }
            this.setDefaultExpression(newExpList);
        }
        else if (this.getDefault() != null) {
            if (this.getDatatype() != null) {
                if (this.getDatatype() instanceof NumericClass) {
                    this.setDefaultValue("0");
                }
                else if (this.getDatatype() instanceof BinClass) {
                    this.setDefaultValue("''");
                }
                else if (this.getDatatype() instanceof CharacterClass) {
                    this.setDefaultValue("' '");
                }
                else if (this.getDatatype() instanceof DateClass) {
                    this.setDefaultValue("'0001-01-01 00:00:00'");
                }
                else {
                    this.setDefaultValue("NULL");
                }
            }
            else {
                this.setDefaultValue("NULL");
            }
            if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
        if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("ROWID")) {
            this.setUserDefinedDatatype("UNIQUEIDENTIFIER");
        }
        if (this.autoIncrement != null) {
            this.identity = "IDENTITY(1,1)";
            this.autoIncrement = null;
        }
        if (this.getCastDatatype() != null) {
            this.getCastDatatype().toMSSQLServerString();
            this.createCastFunction();
        }
    }
    
    public void toSybaseString() throws ConvertException {
        this.constraintNullVector = new Vector();
        this.generated = null;
        this.always = null;
        this.byForAlways = null;
        this.defaultForIdentity = null;
        if (this.columnName != null && this.columnName.startsWith("`") && this.columnName.endsWith("`")) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
                this.columnName = "\"" + this.columnName + "\"";
            }
        }
        boolean nullStatusExists = false;
        if (this.getConstraintClause() != null) {
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toSybaseConstraintClause = changeConstraintVector.get(i);
                    if (toSybaseConstraintClause != null) {
                        final ConstraintType toSybaseConstraintType = toSybaseConstraintClause.getConstraintType();
                        toSybaseConstraintClause.setColumnName(this.getColumnName());
                        toSybaseConstraintClause.toSybaseString();
                        if (toSybaseConstraintType instanceof DefaultConstraintClause) {
                            this.constraintNullVector.insertElementAt(toSybaseConstraintClause, 0);
                        }
                        else {
                            if (toSybaseConstraintClause.getAutoIncrement() != null) {
                                this.setIdentity("IDENTITY");
                                toSybaseConstraintClause.setAutoIncrement(null);
                            }
                            toSybaseConstraintClause.setColumnName(this.getColumnName());
                            if (this.getIdentity() != null) {
                                this.setIdentity("IDENTITY");
                                if (this.getDatatype() != null) {
                                    final Datatype datatype = this.getDatatype();
                                    final NumericClass numericClass = (NumericClass)datatype;
                                    final String dataTypeName = numericClass.getDatatypeName();
                                    if (dataTypeName.indexOf("NUMERIC") == -1) {
                                        numericClass.setDatatypeName("NUMERIC (5,0)");
                                    }
                                }
                            }
                            if (toSybaseConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                final PrimaryOrUniqueConstraintClause tempPrimaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toSybaseConstraintType;
                                if (tempPrimaryOrUniqueConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                                    this.constraintNullVector.add(",\n\t");
                                    if (!this.notNullSetFromCreateQueryStatement) {
                                        this.setNullStatus("");
                                    }
                                }
                            }
                            if (toSybaseConstraintType instanceof ForeignConstraintClause) {
                                final ForeignConstraintClause tempForeignConstraintClause = (ForeignConstraintClause)toSybaseConstraintType;
                                if (tempForeignConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                                    this.constraintNullVector.add(",\n\t");
                                }
                                else if (tempForeignConstraintClause.getConstraintColumnNames() == null && this.columnName == null) {
                                    tempForeignConstraintClause.setConstraintName(null);
                                }
                            }
                            toSybaseConstraintClause.toSybaseString();
                            if (toSybaseConstraintClause.getNotNull() != null && toSybaseConstraintClause.getNotNull().getNullStatus() != null && !toSybaseConstraintClause.getNotNull().getNullStatus().trim().equals("")) {
                                nullStatusExists = true;
                            }
                            this.constraintNullVector.add(toSybaseConstraintClause);
                        }
                    }
                }
            }
        }
        if (this.onDefault != null && this.onDefault.trim().equalsIgnoreCase("DEFAULT") && this.getDefaultValue() != null && this.getDefaultValue().trim().equalsIgnoreCase("null") && this.notNull == null) {
            final NotNull newNotNull = new NotNull();
            newNotNull.setNullStatus("NULL");
            this.setNotNull(newNotNull);
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnAS("AS");
            this.setComputedColumnExpression(this.getComputedColumnExpression().toSybaseSelect(null, null));
        }
        if (this.getNullStatus() != null) {
            this.constraintNullVector.add(this.getNullStatus());
        }
        else if (!SwisSQLOptions.fromSybase && this.tableNameFromCQS != null && this.columnName != null && this.identity == null && !nullStatusExists) {
            this.tableNameFromCQS = this.removeDelimiter(this.tableNameFromCQS);
            final String colName = this.removeDelimiter(this.columnName);
            final ArrayList tempCols = (ArrayList)CastingUtil.getValueIgnoreCase(SwisSQLAPI.primaryKeyMetaData, this.tableNameFromCQS);
            if (tempCols != null) {
                if (!tempCols.contains(colName) && !tempCols.contains(colName.toLowerCase()) && !tempCols.contains(colName.toUpperCase())) {
                    if (this.notNull == null) {
                        final NotNull newNotNull2 = new NotNull();
                        newNotNull2.setNullStatus("NULL");
                        this.setNotNull(newNotNull2);
                    }
                    else {
                        this.notNull.setNullStatus("NULL");
                    }
                    this.constraintNullVector.add(this.getNullStatus());
                }
            }
            else if (SwisSQLAPI.dataTypesFromMetaDataHT.size() > 0) {
                final Hashtable colTypeHT = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, this.tableNameFromCQS);
                if (colTypeHT != null) {
                    final String type = (String)CastingUtil.getValueIgnoreCase(colTypeHT, colName);
                    if (type != null) {
                        if (this.notNull == null) {
                            final NotNull newNotNull3 = new NotNull();
                            newNotNull3.setNullStatus("NULL");
                            this.setNotNull(newNotNull3);
                        }
                        else {
                            this.notNull.setNullStatus("NULL");
                        }
                        this.constraintNullVector.add(this.getNullStatus());
                    }
                }
            }
        }
        if (this.getDatatype() != null) {
            if (this.identity != null || this.asForIdentity != null) {
                final NumericClass ncl = new NumericClass();
                ncl.setDatatypeName("NUMERIC");
                this.setDatatype(ncl);
                if (this.identity != null && this.identity.indexOf("(") != -1) {
                    this.identity = this.identity.substring(0, this.identity.indexOf("("));
                }
            }
            else if (!this.mapDatatype(this, this.datatypeMapping)) {
                final Datatype toSybaseDatatype = this.getDatatype();
                toSybaseDatatype.toSybaseString();
                if (toSybaseDatatype instanceof CharacterClass) {
                    this.enumValuesConvertedToCheckConstraints(toSybaseDatatype, this.constraintNullVector);
                }
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() > 0) {
            if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
                if (CreateColumn.userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
                    this.setUserDefinedDatatype(CreateColumn.userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
                }
                else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
                    this.setUserDefinedDatatype("CHAR(36)");
                }
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
            this.setUserDefinedDatatype("CHAR(36)");
        }
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("GETDATE()");
            }
            if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
                this.setDefaultValue("NEWID()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE") || this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME") || this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") || this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("GETDATE()");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER");
            }
            else if (this.defaultValue.indexOf("\"") != -1) {
                this.setDefaultValue(this.defaultValue = "'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'");
            }
            else if (this.defaultValue.equalsIgnoreCase("true")) {
                this.setDefaultValue("1");
            }
            else if (this.defaultValue.equalsIgnoreCase("false")) {
                this.setDefaultValue("0");
            }
            else if (this.getDefault() != null && this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
        else if (this.getDefaultFunction() != null) {
            this.setDefaultFunction(this.functionCall.toSybaseSelect(null, null));
        }
        else if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            for (int i = 0; i < this.defaultExpList.size(); ++i) {
                if (this.defaultExpList.get(i) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(i);
                    newExpList.add(sc.toSybaseSelect(null, null));
                }
                else {
                    newExpList.add(this.defaultExpList.get(i));
                }
            }
            this.setDefaultExpression(newExpList);
        }
        else if (this.getDefault() != null) {
            if (this.getDatatype() != null) {
                if (this.getDatatype() instanceof NumericClass) {
                    this.setDefaultValue("0");
                }
                else if (this.getDatatype() instanceof BinClass) {
                    this.setDefaultValue("''");
                }
                else if (this.getDatatype() instanceof CharacterClass) {
                    this.setDefaultValue("' '");
                }
                else if (this.getDatatype() instanceof DateClass) {
                    this.setDefaultValue("'0001-01-01 00:00:00'");
                }
                else if (this.getNullStatus() != null && !this.getNullStatus().trim().equalsIgnoreCase("NOT NULL")) {
                    this.setDefaultValue("NULL");
                }
            }
            else if (this.getNullStatus() != null && !this.getNullStatus().trim().equalsIgnoreCase("NOT NULL")) {
                this.setDefaultValue("NULL");
            }
            if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
        if (this.getCastDatatype() != null) {
            this.getCastDatatype().toSybaseString();
            this.createCastFunction();
        }
        if (this.autoIncrement != null) {
            this.identity = "IDENTITY";
            this.autoIncrement = null;
        }
        this.asForIdentity = null;
    }
    
    public void toOracleString() throws ConvertException {
        this.constraintNullVector = new Vector();
        int indexOfNotNull = 0;
        boolean notNullAddedForPrimaryorUniqueConstraint = true;
        this.setCollate(null);
        this.setDatapageStorageLevel(null);
        this.setCollationName(null);
        this.generated = null;
        this.always = null;
        this.asForIdentity = null;
        this.byForAlways = null;
        this.defaultForIdentity = null;
        this.asForIdentity = null;
        if (this.nullStatus == null && SwisSQLOptions.sybaseNotNullConstraint && SwisSQLOptions.fromSybase && (this.getDatatype() != null || this.getUserDefinedDatatype() != null)) {
            this.nullStatus = "NOT NULL";
        }
        if (this.columnName != null) {
            if ((this.columnName.startsWith("[") && this.columnName.endsWith("]")) || (this.columnName.startsWith("`") && this.columnName.endsWith("`"))) {
                this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.columnName.indexOf(32) != -1) {
                    this.columnName = "\"" + this.columnName + "\"";
                }
            }
            if (this.columnName.equalsIgnoreCase("SIZE")) {
                this.columnName = "SIZE_1";
            }
            this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords(1), null, 1);
        }
        if (this.identity != null) {
            final CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
            final TableObject tableObj = new TableObject();
            createSequenceObj.setSequence("SEQUENCE");
            String oracleColumnName = this.columnName;
            if (oracleColumnName.startsWith("[") || oracleColumnName.startsWith("\"")) {
                oracleColumnName = "\"" + oracleColumnName.substring(1);
                if (oracleColumnName.endsWith("]") || oracleColumnName.endsWith("\"")) {
                    oracleColumnName = oracleColumnName.substring(0, oracleColumnName.length() - 1) + "\"";
                }
                if (this.tableNameFromCQS != null) {
                    String str = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1) + "_SEQ";
                    final String str2 = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1);
                    final String str3 = this.tableNameFromCQS;
                    if (str.length() > 29) {
                        if (str2.length() > 25) {
                            str = str2.substring(0, 26) + "_SEQ";
                            if (str.startsWith("\"")) {
                                str = StringFunctions.replaceAll("", "\"", str);
                                str = "\"" + str + "\"";
                            }
                        }
                        else if (str3.length() > 25) {
                            str = str3.substring(0, 26) + "_SEQ";
                            if (str3.startsWith("\"")) {
                                str = StringFunctions.replaceAll("", "\"", str3);
                                str = "\"" + str + "\"";
                            }
                        }
                        if (str.length() > 27) {
                            tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                            String table_name_str = tableObj.getTableName();
                            if (table_name_str.startsWith("\"")) {
                                table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                                table_name_str = "\"" + table_name_str + "\"";
                                tableObj.setTableName(table_name_str);
                            }
                        }
                        else {
                            tableObj.setTableName("\"" + str + "\"");
                            String table_name_str = tableObj.getTableName();
                            if (table_name_str.startsWith("\"")) {
                                table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                                table_name_str = "\"" + table_name_str + "\"";
                                tableObj.setTableName(table_name_str);
                            }
                        }
                    }
                    else if (str.length() > 27) {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_S" + "\"");
                        String table_name_str = tableObj.getTableName();
                        if (table_name_str.startsWith("\"")) {
                            table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                            table_name_str = "\"" + table_name_str + "\"";
                            tableObj.setTableName(table_name_str);
                        }
                    }
                    else {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ" + "\"");
                        String table_name_str = tableObj.getTableName();
                        if (table_name_str.startsWith("\"")) {
                            table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                            table_name_str = "\"" + table_name_str + "\"";
                            tableObj.setTableName(table_name_str);
                        }
                    }
                }
                else {
                    tableObj.setTableName(oracleColumnName + "_SEQ");
                    String table_name_str2 = tableObj.getTableName();
                    if (table_name_str2.startsWith("\"")) {
                        table_name_str2 = StringFunctions.replaceAll("", "\"", table_name_str2);
                        table_name_str2 = "\"" + table_name_str2 + "\"";
                        tableObj.setTableName(table_name_str2);
                    }
                }
            }
            else if (this.tableNameFromCQS != null) {
                String str = this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ";
                final String str2 = this.tableNameFromCQS + "_" + oracleColumnName;
                final String str3 = this.tableNameFromCQS;
                if (str.length() > 29) {
                    if (str2.length() > 25) {
                        str = str2.substring(0, 26) + "_SEQ";
                    }
                    else if (str3.length() > 25) {
                        str = str3.substring(0, 26) + "_SEQ";
                    }
                    tableObj.setTableName(str);
                    String table_name_str = tableObj.getTableName();
                    if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                    }
                }
                else {
                    tableObj.setTableName(this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ");
                    String table_name_str = tableObj.getTableName();
                    if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                    }
                }
            }
            else {
                tableObj.setTableName(this.columnName + "_SEQ");
                String table_name_str2 = tableObj.getTableName();
                if (table_name_str2.startsWith("\"")) {
                    table_name_str2 = StringFunctions.replaceAll("", "\"", table_name_str2);
                    table_name_str2 = "\"" + table_name_str2 + "\"";
                    tableObj.setTableName(table_name_str2);
                }
            }
            createSequenceObj.setSchemaName(tableObj);
            if (this.identity.trim().equalsIgnoreCase("IDENTITY")) {
                createSequenceObj.setStart("START");
                createSequenceObj.setWith("WITH");
                createSequenceObj.setStartValue("1");
                createSequenceObj.setIncrementString("INCREMENT BY");
                createSequenceObj.setIncrementValue("1");
            }
            else {
                String tempIdentity = this.identity.trim().substring(8).trim();
                tempIdentity = tempIdentity.substring(1, tempIdentity.length() - 1);
                final StringTokenizer st = new StringTokenizer(tempIdentity, ",");
                final String token1 = st.nextToken();
                createSequenceObj.setStart("START");
                createSequenceObj.setWith("WITH");
                createSequenceObj.setStartValue(token1);
                if (Integer.parseInt(token1) == 0) {
                    createSequenceObj.setMinValueOrNoMinValue("MINVALUE 0");
                }
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
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n/" + "\n/\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
            }
            else {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
            }
            this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
            this.setIdentity(null);
        }
        if (this.getConstraintClause() != null) {
            boolean primaryOrUniqueClauseEncountered = false;
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                final Vector defaultConstraintVector = new Vector();
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toOracleConstraintClause = changeConstraintVector.get(i);
                    if (toOracleConstraintClause != null && toOracleConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                        defaultConstraintVector.add(toOracleConstraintClause);
                        changeConstraintVector.remove(i);
                    }
                }
                if (defaultConstraintVector.size() > 0) {
                    for (int index = 0; index < defaultConstraintVector.size(); ++index) {
                        if (index < changeConstraintVector.size()) {
                            changeConstraintVector.add(index, defaultConstraintVector.get(index));
                        }
                        else {
                            changeConstraintVector.add(defaultConstraintVector.get(index));
                        }
                    }
                }
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toOracleConstraintClause = changeConstraintVector.get(i);
                    toOracleConstraintClause.setTableNameFromCQS(this.tableNameFromCQS);
                    toOracleConstraintClause.setColumnNameForSequence(this.columnName);
                    if (toOracleConstraintClause != null) {
                        if (toOracleConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause) {
                            primaryOrUniqueClauseEncountered = true;
                            toOracleConstraintClause.setColumnName(this.getColumnName());
                            final PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toOracleConstraintClause.getConstraintType();
                            boolean ccToBeAdded = false;
                            if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                                this.constraintNullVector.add(",\n\t");
                                final CreateColumn cc = new CreateColumn();
                                if (this.getConstraintClause() != null) {
                                    final Vector primaryConstraintVector = new Vector();
                                    for (int index2 = 0; index2 < this.getConstraintClause().size(); ++index2) {
                                        if (this.getConstraintClause().get(index2) instanceof PrimaryOrUniqueConstraintClause) {
                                            primaryConstraintVector.add(this.getConstraintClause().get(index2));
                                            cc.setNullStatus(null);
                                            cc.setDefaultValue(null);
                                            cc.setDefault(null);
                                            cc.toOracleString();
                                            final NotNull notnullObj = new NotNull();
                                            notnullObj.setNullStatus(null);
                                            notnullObj.setIdentity(null);
                                            cc.setNotNull(notnullObj);
                                            ccToBeAdded = true;
                                        }
                                    }
                                    cc.setConstraintClause(primaryConstraintVector);
                                }
                                if (ccToBeAdded) {
                                    this.constraintNullVector.add(cc);
                                }
                                notNullAddedForPrimaryorUniqueConstraint = false;
                                indexOfNotNull = i;
                            }
                            toOracleConstraintClause.toOracleString();
                            this.constraintNullVector.add(toOracleConstraintClause);
                        }
                        else if (toOracleConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                            final DefaultConstraintClause defaultConstraint = (DefaultConstraintClause)toOracleConstraintClause.getConstraintType();
                            if (defaultConstraint.getDefaultValue() != null && defaultConstraint.getDefaultValue().equalsIgnoreCase("NULL") && SwisSQLOptions.fromSybase && this.nullStatus != null && this.nullStatus.equalsIgnoreCase("NOT NULL")) {
                                this.nullStatus = "NULL";
                            }
                            if (this.columnName != null && defaultConstraint.getConstraintName() != null) {
                                toOracleConstraintClause.setConstraint(null);
                                toOracleConstraintClause.setConstraintName(null);
                                toOracleConstraintClause.toOracleString();
                                this.constraintNullVector.add(toOracleConstraintClause);
                            }
                            else {
                                toOracleConstraintClause.toOracleString();
                                this.constraintNullVector.add(toOracleConstraintClause);
                            }
                        }
                        else if (toOracleConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
                            toOracleConstraintClause.toOracleString();
                            this.constraintNullVector.add(toOracleConstraintClause);
                        }
                        else if (toOracleConstraintClause.getConstraintType() instanceof ForeignConstraintClause) {
                            toOracleConstraintClause.toOracleString();
                            this.constraintNullVector.add(toOracleConstraintClause);
                        }
                        else if (toOracleConstraintClause.getNotNull() != null) {
                            notNullAddedForPrimaryorUniqueConstraint = false;
                            indexOfNotNull = i;
                            toOracleConstraintClause.toOracleString();
                            if (!primaryOrUniqueClauseEncountered) {
                                this.constraintNullVector.add(toOracleConstraintClause);
                            }
                        }
                        else {
                            toOracleConstraintClause.toOracleString();
                            this.constraintNullVector.add(toOracleConstraintClause);
                        }
                    }
                }
            }
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnExpression(this.getComputedColumnExpression().toOracleSelect(null, null));
        }
        if (this.getNullStatus() != null) {
            if (!notNullAddedForPrimaryorUniqueConstraint) {
                this.constraintNullVector.add(indexOfNotNull, this.getNullStatus());
            }
            else if (SwisSQLOptions.fromSybase) {
                if (this.getDefaultValue() != null && this.getDefaultValue().equalsIgnoreCase("NULL")) {
                    this.constraintNullVector.add("NULL");
                }
                else {
                    this.constraintNullVector.add(this.getNullStatus());
                }
            }
            else {
                this.constraintNullVector.add(this.getNullStatus());
            }
        }
        if (this.getDatatype() != null) {
            if (!this.mapDatatype(this, this.datatypeMapping)) {
                final Datatype toOracleDatatype = this.getDatatype();
                if (toOracleDatatype instanceof BinClass) {
                    final BinClass binClass = (BinClass)toOracleDatatype;
                    if (binClass.getDatatypeName().trim().equalsIgnoreCase("BOOLEAN")) {
                        this.booleanOracle = true;
                    }
                }
                if (CreateColumn.userConfiguredDatatypes.isEmpty()) {
                    if (toOracleDatatype instanceof CharacterClass) {
                        final CharacterClass ct = (CharacterClass)toOracleDatatype;
                        toOracleDatatype.toOracleString();
                        if ((toOracleDatatype.getDatatypeName().equalsIgnoreCase("varchar2") || toOracleDatatype.getDatatypeName().equalsIgnoreCase("nvarchar2")) && ct.getSize() == null) {
                            ct.setSize("1");
                            ct.setOpenBrace("(");
                            ct.setClosedBrace(")");
                        }
                        this.enumValuesConvertedToCheckConstraints(toOracleDatatype, this.constraintNullVector);
                    }
                    else {
                        toOracleDatatype.toOracleString();
                    }
                }
                else if (toOracleDatatype instanceof DateClass) {
                    final DateClass dt = (DateClass)toOracleDatatype;
                    String typeString = new String();
                    boolean braceComes = false;
                    final String typeStringWithoutBraces;
                    typeString = (typeStringWithoutBraces = typeString + dt.getDatatypeName().trim());
                    if (dt.getSize() != null && !dt.getSize().trim().equals("")) {
                        typeString = typeString + "(" + dt.getSize().trim() + ")";
                        braceComes = true;
                    }
                    if (CreateColumn.userConfiguredDatatypes.containsKey(typeString.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeString.toLowerCase()).toString();
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            final String dataTypeWithoutLength = typeName.substring(0, typeName.indexOf("("));
                            dt.setDatatypeName(dataTypeWithoutLength);
                            final String length = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                            dt.setSize(length);
                            dt.setOpenBrace("(");
                            dt.setClosedBrace(")");
                        }
                        else {
                            dt.setDatatypeName(typeName);
                            if (!braceComes) {
                                dt.setSize(null);
                                dt.setOpenBrace(null);
                                dt.setClosedBrace(null);
                            }
                        }
                    }
                    else if (CreateColumn.userConfiguredDatatypes.containsKey(typeStringWithoutBraces.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeStringWithoutBraces.toLowerCase()).toString();
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            if (!braceComes) {
                                final String dataTypeWithoutLength = typeName.substring(0, typeName.indexOf("("));
                                dt.setDatatypeName(dataTypeWithoutLength);
                                final String length = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                                dt.setSize(length);
                                dt.setOpenBrace("(");
                                dt.setClosedBrace(")");
                            }
                            else {
                                toOracleDatatype.toOracleString();
                            }
                        }
                        else {
                            dt.setDatatypeName(typeName);
                            if (!braceComes) {
                                dt.setSize(null);
                                dt.setOpenBrace(null);
                                dt.setClosedBrace(null);
                            }
                        }
                    }
                    else {
                        toOracleDatatype.toOracleString();
                    }
                }
                else if (toOracleDatatype instanceof NumericClass) {
                    final NumericClass nt = (NumericClass)toOracleDatatype;
                    String typeString = new String();
                    final String typeStringWithoutBraces2;
                    typeString = (typeStringWithoutBraces2 = typeString + nt.getDatatypeName().trim());
                    boolean braceComes2 = false;
                    if (nt.getPrecision() != null && !nt.getPrecision().trim().equals("")) {
                        if (nt.getScale() != null && !nt.getScale().trim().equals("")) {
                            typeString = typeString + "(" + nt.getPrecision().trim() + "," + nt.getScale() + ")";
                        }
                        else {
                            typeString = typeString + "(" + nt.getPrecision().trim() + ")";
                        }
                        braceComes2 = true;
                    }
                    if (CreateColumn.userConfiguredDatatypes.containsKey(typeString.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeString.toLowerCase()).toString();
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            final String dataTypeWithoutLength = typeName.substring(0, typeName.indexOf("("));
                            nt.setDatatypeName(dataTypeWithoutLength);
                            final String length = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                            if (length.indexOf(",") != -1) {
                                final String precision = length.substring(0, length.indexOf(","));
                                nt.setPrecision(precision);
                                final String scale = length.substring(length.indexOf(",") + 1);
                                nt.setScale(scale);
                                nt.setOpenBrace("(");
                                nt.setClosedBrace(")");
                            }
                            else {
                                nt.setPrecision(length);
                                nt.setOpenBrace("(");
                                nt.setClosedBrace(")");
                            }
                        }
                        else {
                            nt.setDatatypeName(typeName);
                            if (!braceComes2) {
                                nt.setPrecision(null);
                                nt.setScale(null);
                                nt.setOpenBrace(null);
                                nt.setClosedBrace(null);
                            }
                        }
                    }
                    else if (CreateColumn.userConfiguredDatatypes.containsKey(typeStringWithoutBraces2.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeStringWithoutBraces2.toLowerCase()).toString();
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            if (!braceComes2) {
                                final String dataTypeWithoutLength = typeName.substring(0, typeName.indexOf("("));
                                nt.setDatatypeName(dataTypeWithoutLength);
                                final String length = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                                if (length.indexOf(",") != -1) {
                                    final String precision = length.substring(0, length.indexOf(","));
                                    nt.setPrecision(precision);
                                    final String scale = length.substring(length.indexOf(",") + 1);
                                    nt.setScale(scale);
                                    nt.setOpenBrace("(");
                                    nt.setClosedBrace(")");
                                }
                                else {
                                    nt.setPrecision(length);
                                    nt.setOpenBrace("(");
                                    nt.setClosedBrace(")");
                                }
                            }
                            else {
                                toOracleDatatype.toOracleString();
                            }
                        }
                        else {
                            nt.setDatatypeName(typeName);
                            if (!braceComes2) {
                                nt.setPrecision(null);
                                nt.setScale(null);
                                nt.setOpenBrace(null);
                                nt.setClosedBrace(null);
                            }
                        }
                    }
                    else {
                        toOracleDatatype.toOracleString();
                    }
                }
                else if (toOracleDatatype instanceof CharacterClass) {
                    final CharacterClass ct = (CharacterClass)toOracleDatatype;
                    String typeString = new String();
                    typeString += ct.getDatatypeName().trim();
                    boolean braceComes = false;
                    final String typeStringWithoutBraces = typeString;
                    if (ct.getSize() != null && !ct.getSize().trim().equals("")) {
                        typeString = typeString + "(" + ct.getSize().trim() + ")";
                        braceComes = true;
                        if (ct.getSize().equalsIgnoreCase("max")) {
                            braceComes = false;
                        }
                    }
                    if (CreateColumn.userConfiguredDatatypes.containsKey(typeString.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeString.toLowerCase()).toString();
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            final String dataTypeWithoutLength = typeName.substring(0, typeName.indexOf("("));
                            ct.setDatatypeName(dataTypeWithoutLength);
                            final String length = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                            ct.setSize(length);
                            ct.setOpenBrace("(");
                            ct.setClosedBrace(")");
                        }
                        else {
                            ct.setDatatypeName(typeName);
                            if (!braceComes) {
                                ct.setSize(null);
                                ct.setOpenBrace(null);
                                ct.setClosedBrace(null);
                            }
                        }
                    }
                    else if (CreateColumn.userConfiguredDatatypes.containsKey(typeStringWithoutBraces.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeStringWithoutBraces.toLowerCase()).toString();
                        final boolean sizeIsMax = ct.getSize().equalsIgnoreCase("max");
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            if (!braceComes) {
                                final String dataTypeWithoutLength2 = typeName.substring(0, typeName.indexOf("("));
                                ct.setDatatypeName(dataTypeWithoutLength2);
                                final String length2 = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                                ct.setSize(length2);
                                ct.setOpenBrace("(");
                                ct.setClosedBrace(")");
                            }
                            else {
                                toOracleDatatype.toOracleString();
                            }
                        }
                        else {
                            ct.setDatatypeName(typeName);
                            if (!braceComes) {
                                ct.setSize(null);
                                ct.setOpenBrace(null);
                                ct.setClosedBrace(null);
                            }
                        }
                        if (sizeIsMax) {
                            if (typeStringWithoutBraces.equalsIgnoreCase("varchar")) {
                                ct.setSize("4000");
                            }
                            else if (typeStringWithoutBraces.equalsIgnoreCase("nvarchar")) {
                                ct.setSize("2000");
                            }
                            ct.setOpenBrace("(");
                            ct.setClosedBrace(")");
                        }
                    }
                    else {
                        toOracleDatatype.toOracleString();
                        if ((toOracleDatatype.getDatatypeName().equalsIgnoreCase("varchar2") || toOracleDatatype.getDatatypeName().equalsIgnoreCase("nvarchar2")) && ct.getSize() == null) {
                            ct.setSize("1");
                            ct.setOpenBrace("(");
                            ct.setClosedBrace(")");
                        }
                    }
                }
                else if (toOracleDatatype instanceof BinClass) {
                    final BinClass bt = (BinClass)toOracleDatatype;
                    String typeString = new String();
                    typeString += bt.getDatatypeName().trim();
                    boolean braceComes = false;
                    final String typeStringWithoutBraces = typeString;
                    if (bt.getSize() != null && !bt.getSize().trim().equals("")) {
                        typeString = typeString + "(" + bt.getSize().trim() + ")";
                        braceComes = true;
                    }
                    if (CreateColumn.userConfiguredDatatypes.containsKey(typeString.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeString.toLowerCase()).toString();
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            final String dataTypeWithoutLength = typeName.substring(0, typeName.indexOf("("));
                            bt.setDatatypeName(dataTypeWithoutLength);
                            final String length = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                            bt.setSize(length);
                            bt.setOpenBrace("(");
                            bt.setClosedBrace(")");
                        }
                        else {
                            bt.setDatatypeName(typeName);
                            if (!braceComes) {
                                bt.setSize(null);
                                bt.setOpenBrace(null);
                                bt.setClosedBrace(null);
                            }
                        }
                    }
                    else if (CreateColumn.userConfiguredDatatypes.containsKey(typeStringWithoutBraces.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeStringWithoutBraces.toLowerCase()).toString();
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            if (!braceComes) {
                                final String dataTypeWithoutLength = typeName.substring(0, typeName.indexOf("("));
                                bt.setDatatypeName(dataTypeWithoutLength);
                                final String length = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                                bt.setSize(length);
                                bt.setOpenBrace("(");
                                bt.setClosedBrace(")");
                            }
                            else {
                                toOracleDatatype.toOracleString();
                            }
                        }
                        else {
                            bt.setDatatypeName(typeName);
                            if (!braceComes) {
                                bt.setSize(null);
                                bt.setOpenBrace(null);
                                bt.setClosedBrace(null);
                            }
                        }
                    }
                    else {
                        toOracleDatatype.toOracleString();
                    }
                }
                else if (toOracleDatatype instanceof QuotedIdentifierDatatype) {
                    final QuotedIdentifierDatatype nt2 = (QuotedIdentifierDatatype)toOracleDatatype;
                    String typeString = new String();
                    final String typeStringWithoutBraces2;
                    typeString = (typeStringWithoutBraces2 = typeString + nt2.getDatatypeName().trim());
                    boolean braceComes2 = false;
                    if (nt2.getPrecision() != null && !nt2.getPrecision().trim().equals("")) {
                        if (nt2.getScale() != null && !nt2.getScale().trim().equals("")) {
                            typeString = typeString + "(" + nt2.getPrecision().trim() + "," + nt2.getScale() + ")";
                        }
                        else {
                            typeString = typeString + "(" + nt2.getPrecision().trim() + ")";
                        }
                        braceComes2 = true;
                    }
                    if (CreateColumn.userConfiguredDatatypes.containsKey(typeString.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeString.toLowerCase()).toString();
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            final String dataTypeWithoutLength = typeName.substring(0, typeName.indexOf("("));
                            nt2.setDatatypeName(dataTypeWithoutLength);
                            final String length = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                            if (length.indexOf(",") != -1) {
                                final String precision = length.substring(0, length.indexOf(","));
                                nt2.setPrecision(precision);
                                final String scale = length.substring(length.indexOf(",") + 1);
                                nt2.setScale(scale);
                                nt2.setOpenBrace("(");
                                nt2.setClosedBrace(")");
                            }
                            else {
                                nt2.setPrecision(length);
                                nt2.setOpenBrace("(");
                                nt2.setClosedBrace(")");
                            }
                        }
                        else {
                            nt2.setDatatypeName(typeName);
                            if (!braceComes2) {
                                nt2.setPrecision(null);
                                nt2.setScale(null);
                                nt2.setOpenBrace(null);
                                nt2.setClosedBrace(null);
                            }
                        }
                    }
                    else if (CreateColumn.userConfiguredDatatypes.containsKey(typeStringWithoutBraces2.toLowerCase())) {
                        final String typeName = CreateColumn.userConfiguredDatatypes.get(typeStringWithoutBraces2.toLowerCase()).toString();
                        if (typeName.indexOf("(") != -1 && typeName.indexOf(")") != -1 && typeName.indexOf("(") < typeName.indexOf(")")) {
                            if (!braceComes2) {
                                final String dataTypeWithoutLength = typeName.substring(0, typeName.indexOf("("));
                                nt2.setDatatypeName(dataTypeWithoutLength);
                                final String length = typeName.substring(typeName.indexOf("(") + 1, typeName.indexOf(")"));
                                if (length.indexOf(",") != -1) {
                                    final String precision = length.substring(0, length.indexOf(","));
                                    nt2.setPrecision(precision);
                                    final String scale = length.substring(length.indexOf(",") + 1);
                                    nt2.setScale(scale);
                                    nt2.setOpenBrace("(");
                                    nt2.setClosedBrace(")");
                                }
                                else {
                                    nt2.setPrecision(length);
                                    nt2.setOpenBrace("(");
                                    nt2.setClosedBrace(")");
                                }
                            }
                            else {
                                toOracleDatatype.toOracleString();
                            }
                        }
                        else {
                            nt2.setDatatypeName(typeName);
                            if (!braceComes2) {
                                nt2.setPrecision(null);
                                nt2.setScale(null);
                                nt2.setOpenBrace(null);
                                nt2.setClosedBrace(null);
                            }
                        }
                    }
                    else {
                        toOracleDatatype.toOracleString();
                    }
                }
            }
            else if (this.getDatatype() != null && this.getDatatype().getDatatypeName() != null && this.getDatatype().getSize() != null && this.getDatatype().getDatatypeName().startsWith("varchar") && this.getDatatype().getSize().equalsIgnoreCase("max")) {
                this.getDatatype().setSize(null);
                this.getDatatype().setOpenBrace(null);
                this.getDatatype().setClosedBrace(null);
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() > 0) {
            if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
                if (CreateColumn.userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
                    this.setUserDefinedDatatype(CreateColumn.userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
                }
                else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
                    this.setUserDefinedDatatype("CHAR(36)");
                }
                else if (this.userDefinedDatatype.equalsIgnoreCase("SQL_VARIANT")) {
                    this.setUserDefinedDatatype("SYS.ANYDATA");
                }
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
            if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
                this.setUserDefinedDatatype("CHAR(36)");
            }
            else if (this.userDefinedDatatype.equalsIgnoreCase("SQL_VARIANT")) {
                this.setUserDefinedDatatype("SYS.ANYDATA");
            }
        }
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("CURRENT_DATE") || this.defaultValue.equalsIgnoreCase("CURRENT_TIME") || this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP") || this.defaultValue.equalsIgnoreCase("CURRENT DATE") || this.defaultValue.equalsIgnoreCase("CURRENT TIME") || this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") || this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("SYSDATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER");
            }
            else if (this.defaultValue.startsWith("'") || this.defaultValue.startsWith("\"")) {
                if (this.getDatatype() instanceof DateClass) {
                    if (this.defaultValue.startsWith("\"")) {
                        this.defaultValue = "'" + this.defaultValue.substring(1);
                    }
                    if (this.defaultValue.endsWith("\"")) {
                        this.defaultValue = this.defaultValue.substring(0, this.defaultValue.length() - 1) + "'";
                    }
                    if (this.defaultValue.equals("'0000-00-00 00:00:00'") || this.defaultValue.equals("0000-00-00 00:00:00")) {
                        this.setDefaultValue("TO_DATE('0001-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')");
                    }
                    else if (this.defaultValue.equals("'0000-00-00'") || this.defaultValue.equals("0000-00-00")) {
                        this.setDefaultValue("TO_DATE('0001-01-01', 'YYYY-MM-DD')");
                    }
                    else {
                        final String format = SwisSQLUtils.getDateFormat(this.defaultValue, 1);
                        if (format != null) {
                            final FunctionCalls fc = new FunctionCalls();
                            final TableColumn tc = new TableColumn();
                            tc.setColumnName("TO_DATE");
                            final Vector fnArgs = new Vector();
                            if (format.startsWith("'1900")) {
                                fnArgs.add(format);
                                fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                            }
                            else {
                                fnArgs.add(this.defaultValue);
                                fnArgs.add(format);
                            }
                            fc.setFunctionName(tc);
                            fc.setFunctionArguments(fnArgs);
                            this.setDefaultFunction(fc);
                            this.setDefaultValue(null);
                        }
                    }
                }
                else if (this.getDatatype() instanceof CharacterClass) {
                    final CharacterClass cc2 = (CharacterClass)this.getDatatype();
                    final String dtype = cc2.getDatatypeName();
                    if (dtype != null && (dtype.indexOf("char") != -1 || dtype.indexOf("CHAR") != -1) && cc2.getSize() != null) {
                        final int size = Integer.parseInt(cc2.getSize());
                        final int len = this.defaultValue.length() - 2;
                        if (len > size) {
                            this.setDefaultValue(null);
                            this.setDefault(null);
                            this.setDefaultOpenBrace(null);
                            this.setDefaultClosedBrace(null);
                        }
                    }
                }
            }
            if (this.getDefaultValue() != null) {
                final StringTokenizer st2 = new StringTokenizer(this.getDefaultValue(), ".");
                final Vector defaultList = new Vector();
                final StringBuffer sb = new StringBuffer();
                while (st2.hasMoreTokens()) {
                    defaultList.add(st2.nextToken());
                }
                for (int i = 0; i < defaultList.size(); ++i) {
                    String str4 = defaultList.get(i);
                    if (str4.startsWith("[") || str4.startsWith("\"")) {
                        str4 = "'" + str4.substring(1);
                    }
                    if (str4.endsWith("]") || str4.endsWith("\"")) {
                        str4 = str4.substring(0, str4.length() - 1) + "'";
                    }
                    if (i > 0) {
                        sb.append(".");
                    }
                    sb.append(str4);
                }
                if (this.datatype instanceof BinClass) {
                    final BinClass binClass2 = (BinClass)this.datatype;
                    if (binClass2.getDatatypeName() != null && binClass2.getDatatypeName().trim().equalsIgnoreCase("BOOLEAN")) {
                        if (this.getDefaultValue().trim().equalsIgnoreCase("TRUE")) {
                            this.setDefaultValue("1");
                        }
                        else {
                            this.setDefaultValue("0");
                        }
                    }
                    else {
                        this.setDefaultValue(sb.toString());
                    }
                }
                else {
                    this.setDefaultValue(sb.toString());
                }
            }
            if (this.getDefault() != null && this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
            if (SwisSQLOptions.fromSybase && this.getDefaultValue() != null && this.getDefaultValue().equalsIgnoreCase("NULL")) {
                this.nullStatus = "NULL";
            }
        }
        else if (this.getDefaultFunction() != null) {
            this.setDefaultFunction(this.functionCall.toOracleSelect(null, null));
        }
        else if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            for (int j = 0; j < this.defaultExpList.size(); ++j) {
                if (this.defaultExpList.get(j) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(j).toOracleSelect(null, null);
                    if (this.getDatatype() instanceof DateClass && sc.getColumnExpression().size() == 1) {
                        final Object sc_val = sc.getColumnExpression().get(0);
                        if (sc_val instanceof String && sc_val.toString().startsWith("'") && sc_val.toString().length() == 10) {
                            sc.getColumnExpression().setElementAt("to_date(" + sc_val.toString() + ",'YYYYMMDD')", 0);
                        }
                        else if (sc_val instanceof String) {
                            String defVal = sc_val.toString();
                            if (defVal.startsWith("N") && defVal.endsWith("'")) {
                                defVal = defVal.substring(defVal.indexOf("'"));
                            }
                            final String format2 = SwisSQLUtils.getDateFormat(defVal, 1);
                            if (format2 != null) {
                                final FunctionCalls fc2 = new FunctionCalls();
                                final TableColumn tc2 = new TableColumn();
                                if (format2.toLowerCase().indexOf("ff") != -1) {
                                    tc2.setColumnName("TO_TIMESTAMP");
                                }
                                else {
                                    tc2.setColumnName("TO_DATE");
                                }
                                final Vector fnArgs2 = new Vector();
                                if (format2.startsWith("'1900")) {
                                    fnArgs2.add(format2);
                                    fnArgs2.add("'YYYY-MM-DD HH24:MI:SS'");
                                }
                                else {
                                    fnArgs2.add(defVal);
                                    fnArgs2.add(format2);
                                }
                                fc2.setFunctionName(tc2);
                                fc2.setFunctionArguments(fnArgs2);
                                this.setDefaultFunction(fc2);
                                this.setDefaultValue(null);
                            }
                        }
                    }
                    else if (this.getDatatype() instanceof BinClass && sc.getColumnExpression().size() == 1) {
                        final Object sc_val = sc.getColumnExpression().get(0);
                        if (sc_val instanceof String) {
                            final String defVal = sc_val.toString();
                            if (defVal.trim().equalsIgnoreCase("'false'") || defVal.trim().equalsIgnoreCase("'f'")) {
                                sc.getColumnExpression().setElementAt("0", 0);
                            }
                            else if (defVal.trim().equalsIgnoreCase("'true'") || defVal.trim().equalsIgnoreCase("'t'")) {
                                sc.getColumnExpression().setElementAt("1", 0);
                            }
                        }
                    }
                    newExpList.add(sc);
                }
                else {
                    newExpList.add(this.defaultExpList.get(j));
                }
            }
            this.setDefaultExpression(newExpList);
        }
        else if (this.getDefault() != null) {
            if (this.getDatatype() != null) {
                if (this.getDatatype() instanceof NumericClass) {
                    this.setDefaultValue("0");
                }
                else if (this.getDatatype() instanceof BinClass) {
                    this.setDefaultValue("''");
                }
                else if (this.getDatatype() instanceof CharacterClass) {
                    this.setDefaultValue("' '");
                }
                else if (this.getDatatype() instanceof DateClass) {
                    this.setDefaultValue("TO_DATE('0001-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')");
                }
                else {
                    this.setDefaultValue("NULL");
                }
            }
            else {
                this.setDefaultValue("NULL");
            }
            if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
        if (this.autoIncrement != null) {
            final CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
            final TableObject tableObj = new TableObject();
            createSequenceObj.setSequence("SEQUENCE");
            String oracleColumnName = this.columnName;
            if (oracleColumnName.startsWith("[") || oracleColumnName.startsWith("\"")) {
                oracleColumnName = "\"" + oracleColumnName.substring(1);
                if (oracleColumnName.endsWith("]") || oracleColumnName.endsWith("\"")) {
                    oracleColumnName = oracleColumnName.substring(0, oracleColumnName.length() - 1) + "\"";
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
            createSequenceObj.setStart("START");
            createSequenceObj.setWith("WITH");
            createSequenceObj.setStartValue("1");
            createSequenceObj.setIncrementString("INCREMENT BY");
            createSequenceObj.setIncrementValue("1");
            if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
            }
            else {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
            }
            this.setAutoIncrement(null);
        }
        if (this.getCastDatatype() != null) {
            this.getCastDatatype().toOracleString();
            this.createCastFunction();
        }
    }
    
    public void toPostgreSQLString() throws ConvertException {
        int indexOfNotNull = 0;
        boolean notNullAddedForPrimaryorUniqueConstraint = true;
        this.constraintNullVector = new Vector();
        this.setCollate(null);
        this.setCollationName(null);
        this.setDatapageStorageLevel(null);
        this.generated = null;
        this.always = null;
        this.asForIdentity = null;
        this.byForAlways = null;
        this.defaultForIdentity = null;
        this.asForIdentity = null;
        if (this.columnName != null) {
            if ((this.columnName.startsWith("[") && this.columnName.endsWith("]")) || (this.columnName.startsWith("`") && this.columnName.endsWith("`"))) {
                this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
                if (this.columnName.indexOf(32) != -1) {
                    this.columnName = "\"" + this.columnName + "\"";
                }
            }
            this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords(4), null, 4);
        }
        if (this.identity != null) {
            final CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
            final TableObject tableObj = new TableObject();
            createSequenceObj.setSequence("SEQUENCE");
            String postgresqlColumnName = this.columnName;
            if (postgresqlColumnName.startsWith("[") || postgresqlColumnName.startsWith("\"")) {
                postgresqlColumnName = "\"" + postgresqlColumnName.substring(1);
                if (postgresqlColumnName.endsWith("]") || postgresqlColumnName.endsWith("\"")) {
                    postgresqlColumnName = postgresqlColumnName.substring(0, postgresqlColumnName.length() - 1) + "\"";
                }
                if (this.tableNameFromCQS != null) {
                    String str = this.tableNameFromCQS + postgresqlColumnName.substring(1, postgresqlColumnName.length() - 1) + "_SEQ";
                    final String str2 = this.tableNameFromCQS + postgresqlColumnName.substring(1, postgresqlColumnName.length() - 1);
                    final String str3 = this.tableNameFromCQS;
                    if (str.length() > 63) {
                        if (str2.length() > 59) {
                            str = str2.substring(0, 60) + "_SEQ";
                            if (str.startsWith("\"")) {
                                str = StringFunctions.replaceAll("", "\"", str);
                                str = "\"" + str + "\"";
                            }
                        }
                        else if (str3.length() > 59) {
                            str = str3.substring(0, 60) + "_SEQ";
                            if (str3.startsWith("\"")) {
                                str = StringFunctions.replaceAll("", "\"", str3);
                                str = "\"" + str + "\"";
                            }
                        }
                        if (str.length() > 60) {
                            tableObj.setTableName("\"" + str.substring(0, 61) + "\"");
                            String table_name_str = tableObj.getTableName();
                            if (table_name_str.startsWith("\"")) {
                                table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                                table_name_str = "\"" + table_name_str + "\"";
                                tableObj.setTableName(table_name_str);
                            }
                        }
                        else {
                            tableObj.setTableName("\"" + str + "\"");
                            String table_name_str = tableObj.getTableName();
                            if (table_name_str.startsWith("\"")) {
                                table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                                table_name_str = "\"" + table_name_str + "\"";
                                tableObj.setTableName(table_name_str);
                            }
                        }
                    }
                    else if (str.length() > 60) {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgresqlColumnName + "_S" + "\"");
                        String table_name_str = tableObj.getTableName();
                        if (table_name_str.startsWith("\"")) {
                            table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                            table_name_str = "\"" + table_name_str + "\"";
                            tableObj.setTableName(table_name_str);
                        }
                    }
                    else {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ" + "\"");
                        String table_name_str = tableObj.getTableName();
                        if (table_name_str.startsWith("\"")) {
                            table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                            table_name_str = "\"" + table_name_str + "\"";
                            tableObj.setTableName(table_name_str);
                        }
                    }
                }
                else {
                    tableObj.setTableName(postgresqlColumnName + "_SEQ");
                    String table_name_str2 = tableObj.getTableName();
                    if (table_name_str2.startsWith("\"")) {
                        table_name_str2 = StringFunctions.replaceAll("", "\"", table_name_str2);
                        table_name_str2 = "\"" + table_name_str2 + "\"";
                        tableObj.setTableName(table_name_str2);
                    }
                }
            }
            else if (this.tableNameFromCQS != null) {
                String str = this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ";
                final String str2 = this.tableNameFromCQS + "_" + postgresqlColumnName;
                final String str3 = this.tableNameFromCQS;
                if (str.length() > 63) {
                    if (str2.length() > 59) {
                        str = str2.substring(0, 60) + "_SEQ";
                    }
                    else if (str3.length() > 59) {
                        str = str3.substring(0, 60) + "_SEQ";
                    }
                    tableObj.setTableName(str);
                    String table_name_str = tableObj.getTableName();
                    if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                    }
                }
                else {
                    tableObj.setTableName(this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ");
                    String table_name_str = tableObj.getTableName();
                    if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                    }
                }
            }
            else {
                tableObj.setTableName(this.columnName + "_SEQ");
                String table_name_str2 = tableObj.getTableName();
                if (table_name_str2.startsWith("\"")) {
                    table_name_str2 = StringFunctions.replaceAll("", "\"", table_name_str2);
                    table_name_str2 = "\"" + table_name_str2 + "\"";
                    tableObj.setTableName(table_name_str2);
                }
            }
            createSequenceObj.setSchemaName(tableObj);
            if (this.identity.trim().equalsIgnoreCase("IDENTITY")) {
                createSequenceObj.setStart("START");
                createSequenceObj.setStartValue("1");
                createSequenceObj.setIncrementString("INCREMENT ");
                createSequenceObj.setIncrementValue("1");
            }
            else {
                String tempIdentity = this.identity.trim().substring(8).trim();
                tempIdentity = tempIdentity.substring(1, tempIdentity.length() - 1);
                final StringTokenizer st = new StringTokenizer(tempIdentity, ",");
                final String token1 = st.nextToken();
                createSequenceObj.setStart("START");
                createSequenceObj.setStartValue(token1);
                if (Integer.parseInt(token1) == 0) {
                    createSequenceObj.setMinValueOrNoMinValue("MINVALUE 0");
                }
                if (st.countTokens() > 0) {
                    final String token2 = st.nextToken();
                    createSequenceObj.setIncrementString("INCREMENT ");
                    createSequenceObj.setIncrementValue(token2);
                }
                else {
                    createSequenceObj.setIncrementString("INCREMENT ");
                    createSequenceObj.setIncrementValue("1");
                }
            }
            if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n/\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
            }
            else {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
            }
            this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
            this.setIdentity(null);
        }
        if (this.getConstraintClause() != null) {
            boolean primaryOrUniqueClauseEncountered = false;
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                final Vector defaultConstraintVector = new Vector();
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toPostgreSQLConstraintClause = changeConstraintVector.get(i);
                    if (toPostgreSQLConstraintClause != null && toPostgreSQLConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                        defaultConstraintVector.add(toPostgreSQLConstraintClause);
                        changeConstraintVector.remove(i);
                    }
                }
                if (defaultConstraintVector.size() > 0) {
                    for (int index = 0; index < defaultConstraintVector.size(); ++index) {
                        if (index < changeConstraintVector.size()) {
                            changeConstraintVector.add(index, defaultConstraintVector.get(index));
                        }
                        else {
                            changeConstraintVector.add(defaultConstraintVector.get(index));
                        }
                    }
                }
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toPostgreSQLConstraintClause = changeConstraintVector.get(i);
                    toPostgreSQLConstraintClause.setTableNameFromCQS(this.tableNameFromCQS);
                    toPostgreSQLConstraintClause.setColumnNameForSequence(this.columnName);
                    if (toPostgreSQLConstraintClause != null) {
                        if (toPostgreSQLConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause) {
                            primaryOrUniqueClauseEncountered = true;
                            toPostgreSQLConstraintClause.setColumnName(this.getColumnName());
                            final PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toPostgreSQLConstraintClause.getConstraintType();
                            boolean ccToBeAdded = false;
                            if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                                this.constraintNullVector.add(",\n\t");
                                final CreateColumn cc = new CreateColumn();
                                if (this.getConstraintClause() != null) {
                                    final Vector primaryConstraintVector = new Vector();
                                    for (int index2 = 0; index2 < this.getConstraintClause().size(); ++index2) {
                                        if (this.getConstraintClause().get(index2) instanceof PrimaryOrUniqueConstraintClause) {
                                            primaryConstraintVector.add(this.getConstraintClause().get(index2));
                                            cc.setNullStatus(null);
                                            cc.setDefaultValue(null);
                                            cc.setDefault(null);
                                            cc.toPostgreSQLString();
                                            final NotNull notnullObj = new NotNull();
                                            notnullObj.setNullStatus(null);
                                            notnullObj.setIdentity(null);
                                            cc.setNotNull(notnullObj);
                                            ccToBeAdded = true;
                                        }
                                    }
                                    cc.setConstraintClause(primaryConstraintVector);
                                }
                                if (ccToBeAdded) {
                                    this.constraintNullVector.add(cc);
                                }
                                notNullAddedForPrimaryorUniqueConstraint = false;
                                indexOfNotNull = i;
                            }
                            toPostgreSQLConstraintClause.toPostgreSQLString();
                            this.constraintNullVector.add(toPostgreSQLConstraintClause);
                        }
                        else if (toPostgreSQLConstraintClause.getConstraintType() instanceof DefaultConstraintClause) {
                            final DefaultConstraintClause defaultConstraint = (DefaultConstraintClause)toPostgreSQLConstraintClause.getConstraintType();
                            if (defaultConstraint.getDefaultValue() != null && defaultConstraint.getDefaultValue().equalsIgnoreCase("NULL") && SwisSQLOptions.fromSybase && this.nullStatus != null && this.nullStatus.equalsIgnoreCase("NOT NULL")) {
                                this.nullStatus = "NULL";
                            }
                            if (this.columnName != null && defaultConstraint.getConstraintName() != null) {
                                toPostgreSQLConstraintClause.setConstraint(null);
                                toPostgreSQLConstraintClause.setConstraintName(null);
                                toPostgreSQLConstraintClause.toPostgreSQLString();
                                this.constraintNullVector.add(toPostgreSQLConstraintClause);
                            }
                            else {
                                toPostgreSQLConstraintClause.toPostgreSQLString();
                                this.constraintNullVector.add(toPostgreSQLConstraintClause);
                            }
                        }
                        else if (toPostgreSQLConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
                            toPostgreSQLConstraintClause.toPostgreSQLString();
                            this.constraintNullVector.add(toPostgreSQLConstraintClause);
                        }
                        else if (toPostgreSQLConstraintClause.getConstraintType() instanceof ForeignConstraintClause) {
                            toPostgreSQLConstraintClause.toPostgreSQLString();
                            this.constraintNullVector.add(toPostgreSQLConstraintClause);
                        }
                        else if (toPostgreSQLConstraintClause.getNotNull() != null) {
                            notNullAddedForPrimaryorUniqueConstraint = false;
                            indexOfNotNull = i;
                            toPostgreSQLConstraintClause.toPostgreSQLString();
                            if (!primaryOrUniqueClauseEncountered) {
                                this.constraintNullVector.add(toPostgreSQLConstraintClause);
                            }
                        }
                        else {
                            toPostgreSQLConstraintClause.toPostgreSQLString();
                            this.constraintNullVector.add(toPostgreSQLConstraintClause);
                        }
                    }
                }
            }
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnExpression(this.getComputedColumnExpression().toPostgreSQLSelect(null, null));
        }
        if (this.getNullStatus() != null) {
            this.constraintNullVector.add(this.getNullStatus());
        }
        if (this.getDatatype() != null) {
            if (!this.mapDatatype(this, this.datatypeMapping)) {
                final Datatype toPostgreSQLDatatype = this.getDatatype();
                toPostgreSQLDatatype.toPostgreSQLString();
                if (toPostgreSQLDatatype instanceof CharacterClass) {
                    this.enumValuesConvertedToCheckConstraints(toPostgreSQLDatatype, this.constraintNullVector);
                }
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() > 0) {
            if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
                if (CreateColumn.userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
                    this.setUserDefinedDatatype(CreateColumn.userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
                }
                else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
                    this.setUserDefinedDatatype("CHAR(36)");
                }
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
            this.setUserDefinedDatatype("CHAR(36)");
        }
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("USER")) {
                this.setDefaultValue("CURRENT_USER");
            }
            else if (this.defaultValue.startsWith("0x") || this.defaultValue.startsWith("0X")) {
                this.setDefaultValue("'" + this.defaultValue.substring(1, this.defaultValue.length()) + "'");
            }
        }
        else if (this.getDefaultFunction() != null) {
            this.setDefaultFunction(this.functionCall.toPostgreSQLSelect(null, null));
        }
        else if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            for (int j = 0; j < this.defaultExpList.size(); ++j) {
                if (this.defaultExpList.get(j) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(j);
                    newExpList.add(sc.toPostgreSQLSelect(null, null));
                }
                else {
                    newExpList.add(this.defaultExpList.get(j));
                }
            }
            this.setDefaultExpression(newExpList);
        }
        else if (this.getDefault() != null) {
            final String def = this.getDefault();
            if (def.equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
        if (this.autoIncrement != null) {
            final CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
            final TableObject tableObj = new TableObject();
            createSequenceObj.setSequence("SEQUENCE");
            String postgresqlColumnName = this.columnName;
            if (postgresqlColumnName.startsWith("[") || postgresqlColumnName.startsWith("\"")) {
                postgresqlColumnName = "\"" + postgresqlColumnName.substring(1);
                if (postgresqlColumnName.endsWith("]") || postgresqlColumnName.endsWith("\"")) {
                    postgresqlColumnName = postgresqlColumnName.substring(0, postgresqlColumnName.length() - 1) + "\"";
                }
                if (this.tableNameFromCQS != null) {
                    String str = this.tableNameFromCQS + postgresqlColumnName.substring(1, postgresqlColumnName.length() - 1) + "_SEQ";
                    final String str2 = this.tableNameFromCQS + postgresqlColumnName.substring(1, postgresqlColumnName.length() - 1);
                    final String str3 = this.tableNameFromCQS;
                    if (str.length() > 63) {
                        if (str2.length() > 59) {
                            str = str2.substring(0, 60) + "_SEQ";
                        }
                        else if (str3.length() > 59) {
                            str = str3.substring(0, 60) + "_SEQ";
                        }
                        if (str.length() > 60) {
                            tableObj.setTableName("\"" + str.substring(0, 61) + "\"");
                        }
                        else {
                            tableObj.setTableName("\"" + str + "\"");
                        }
                    }
                    else if (str.length() > 60) {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgresqlColumnName + "_S" + "\"");
                    }
                    else {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ" + "\"");
                    }
                }
                else {
                    tableObj.setTableName(postgresqlColumnName + "_SEQ");
                }
            }
            else if (this.tableNameFromCQS != null) {
                String str = this.tableNameFromCQS + postgresqlColumnName + "_SEQ";
                final String str2 = this.tableNameFromCQS + postgresqlColumnName;
                final String str3 = this.tableNameFromCQS;
                if (str.length() > 63) {
                    if (str2.length() > 59) {
                        str = str2.substring(0, 60) + "_SEQ";
                    }
                    else if (str3.length() > 59) {
                        str = str3.substring(0, 60) + "_SEQ";
                    }
                    tableObj.setTableName(str);
                }
                else {
                    tableObj.setTableName(this.tableNameFromCQS + "_" + postgresqlColumnName + "_SEQ");
                }
            }
            else {
                tableObj.setTableName(this.columnName + "_SEQ");
            }
            createSequenceObj.setSchemaName(tableObj);
            createSequenceObj.setStart("START");
            createSequenceObj.setStartValue("1");
            createSequenceObj.setIncrementString("INCREMENT BY");
            createSequenceObj.setIncrementValue("1");
            if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
            }
            else {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
            }
            this.setCreateSequenceString("CREATE " + createSequenceObj.toString());
            this.setAutoIncrement(null);
        }
        if (this.getCastDatatype() != null) {
            this.getCastDatatype().toPostgreSQLString();
            this.setDefaultValue(this.getDefaultValue() + "::" + this.getCastDatatype());
        }
    }
    
    public void toMySQLString() throws ConvertException {
        this.constraintNullVector = new Vector();
        this.setCollate(null);
        this.setCollationName(null);
        this.generated = null;
        this.always = null;
        this.asForIdentity = null;
        this.setDatapageStorageLevel(this.byForAlways = null);
        this.defaultForIdentity = null;
        this.asForIdentity = null;
        if (this.columnName != null) {
            if ((this.columnName.startsWith("[") && this.columnName.endsWith("]")) || (this.columnName.startsWith("\"") && this.columnName.endsWith("\""))) {
                this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
                this.columnName = "`" + this.columnName + "`";
            }
            else if (!this.columnName.startsWith("`") && !this.columnName.endsWith("`")) {
                this.columnName = "`" + this.columnName + "`";
            }
        }
        if (this.getConstraintClause() != null) {
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toMySQLConstraintClause = changeConstraintVector.get(i);
                    if (toMySQLConstraintClause != null) {
                        if (toMySQLConstraintClause.getNotNull() != null) {
                            final NotNull notNullClause = toMySQLConstraintClause.getNotNull();
                            if (notNullClause.getIdentity() != null) {
                                notNullClause.setIdentity(null);
                                this.setAutoIncrement("AUTO_INCREMENT");
                            }
                        }
                        if (this.getIdentity() != null) {
                            toMySQLConstraintClause.setAutoIncrement("AUTO_INCREMENT");
                            this.setIdentity(null);
                            if (this.getDatatype() != null) {
                                final Datatype datatype = this.getDatatype();
                                final NumericClass numericClass = (NumericClass)datatype;
                                final String dataTypeName = numericClass.getDatatypeName();
                                if (dataTypeName != null) {
                                    numericClass.setDatatypeName("INT");
                                    numericClass.setSize(null);
                                    numericClass.setPrecision(null);
                                    numericClass.setScale(null);
                                    numericClass.setOpenBrace(null);
                                    numericClass.setClosedBrace(null);
                                }
                            }
                        }
                        toMySQLConstraintClause.setColumnName(this.getColumnName());
                        if (toMySQLConstraintClause.getConstraintType() instanceof PrimaryOrUniqueConstraintClause) {
                            final PrimaryOrUniqueConstraintClause primaryConstraintClause = (PrimaryOrUniqueConstraintClause)toMySQLConstraintClause.getConstraintType();
                            boolean ccToBeAdded = false;
                            if (primaryConstraintClause.getConstraintColumnNames() != null && this.columnName != null) {
                                this.constraintNullVector.add(",\n\t");
                                final CreateColumn cc = new CreateColumn();
                                if (this.getConstraintClause() != null) {
                                    final Vector primaryConstraintVector = new Vector();
                                    for (int index = 0; index < this.getConstraintClause().size(); ++index) {
                                        if (this.getConstraintClause().get(index) instanceof PrimaryOrUniqueConstraintClause) {
                                            primaryConstraintVector.add(this.getConstraintClause().get(index));
                                            cc.setNullStatus(null);
                                            cc.setDefaultValue(null);
                                            cc.setDefault(null);
                                            cc.toOracleString();
                                            final NotNull notnullObj = new NotNull();
                                            notnullObj.setNullStatus(null);
                                            notnullObj.setIdentity(null);
                                            cc.setNotNull(notnullObj);
                                            ccToBeAdded = true;
                                        }
                                    }
                                    cc.setConstraintClause(primaryConstraintVector);
                                }
                                if (ccToBeAdded) {
                                    this.constraintNullVector.add(cc);
                                }
                            }
                        }
                        toMySQLConstraintClause.toMySQLString();
                        this.constraintNullVector.add(toMySQLConstraintClause);
                    }
                }
            }
        }
        else {
            if (this.identity != null) {
                this.setAutoIncrement("AUTO_INCREMENT");
                if (this.getDatatype() != null) {
                    final Datatype datatype2 = this.getDatatype();
                    if (datatype2 instanceof NumericClass) {
                        final NumericClass numericClass2 = (NumericClass)datatype2;
                        final String dataTypeName2 = numericClass2.getDatatypeName();
                        if (dataTypeName2 != null) {
                            numericClass2.setDatatypeName("INT");
                            numericClass2.setSize(null);
                            numericClass2.setPrecision(null);
                            numericClass2.setScale(null);
                            numericClass2.setOpenBrace(null);
                            numericClass2.setClosedBrace(null);
                        }
                    }
                }
            }
            this.setIdentity(null);
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnExpression(this.getComputedColumnExpression().toMySQLSelect(null, null));
        }
        if (this.getNullStatus() != null) {
            this.constraintNullVector.add(this.getNullStatus());
        }
        if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            final int size = this.defaultExpList.size();
            if (size == 1) {
                if (this.defaultExpList.get(0) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(0);
                    final Vector expList = sc.getColumnExpression();
                    if (expList.size() == 1) {
                        final Object obj = expList.get(0);
                        if (obj instanceof String) {
                            this.setDefaultValue((String)obj);
                        }
                        else if (obj instanceof TableColumn) {
                            final TableColumn tc = (TableColumn)obj;
                            final String colName = tc.getColumnName();
                            if (colName != null) {
                                this.setDefaultValue(colName);
                            }
                        }
                    }
                }
                else {
                    final ArrayList expWithoutSc = this.getDefaultExpression();
                    if (expWithoutSc.size() == 1 && expWithoutSc.get(0) instanceof String) {
                        this.setDefaultValue(expWithoutSc.get(0));
                    }
                }
            }
        }
        if (this.getDatatype() != null) {
            if (!this.mapDatatype(this, this.datatypeMapping)) {
                final Datatype toMySQLDatatype = this.getDatatype();
                if (toMySQLDatatype instanceof CharacterClass) {
                    final CharacterClass cc2 = (CharacterClass)toMySQLDatatype;
                    if (cc2.getDatatypeName() != null && cc2.getDatatypeName().equalsIgnoreCase("LONG")) {
                        this.setDefault(null);
                        this.setDefaultValue(null);
                    }
                }
                if (toMySQLDatatype instanceof DateClass) {
                    final DateClass dc = (DateClass)toMySQLDatatype;
                    if (dc.getDatatypeName() != null && dc.getDatatypeName().equalsIgnoreCase("TIMESTAMP") && this.getDefault() != null && this.getDefaultValue() != null && this.getDefaultValue().trim().equalsIgnoreCase("NULL")) {
                        this.setDefaultValue("0");
                    }
                }
                toMySQLDatatype.toMySQLString();
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() > 0) {
            if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
                if (CreateColumn.userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
                    this.setUserDefinedDatatype(CreateColumn.userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim()));
                }
                else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
                    this.setUserDefinedDatatype("CHAR(36)");
                }
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() <= 0 && !this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) && this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {
            this.setUserDefinedDatatype("CHAR(36)");
        }
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("'CURRENT_DATE'");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("'CURRENT_DATE'");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("'CURRENT_TIME'");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("'CURRENT_DATE'");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER()");
            }
            else if (this.defaultValue.equalsIgnoreCase("true")) {
                this.setDefaultValue("1");
            }
            else if (this.defaultValue.equalsIgnoreCase("false")) {
                this.setDefaultValue("0");
            }
            this.setDefaultOpenBrace(null);
            this.setDefaultClosedBrace(null);
        }
        else if (this.getDefaultFunction() != null) {
            final FunctionCalls fnCall = this.getDefaultFunction();
            final TableColumn tabCol = fnCall.getFunctionName();
            if (tabCol != null && tabCol.getColumnName().equalsIgnoreCase("getdate")) {
                this.setDefaultValue("'CURRENT_DATE'");
                this.setDefaultFunction(null);
            }
            else {
                this.setDefaultFunction(this.functionCall.toMySQLSelect(null, null));
            }
        }
        else if (this.getDefault() != null) {
            this.setDefaultOpenBrace(null);
            this.setDefaultClosedBrace(null);
            this.setDefaultValue("NULL");
            if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
        if (this.getCastDatatype() != null) {}
    }
    
    public void toANSIString() throws ConvertException {
        this.constraintNullVector = new Vector();
        this.setIdentity(null);
        this.setDatapageStorageLevel(null);
        this.setAutoIncrement(null);
        this.setCollate(null);
        this.setCollationName(null);
        this.generated = null;
        this.always = null;
        this.asForIdentity = null;
        this.byForAlways = null;
        this.defaultForIdentity = null;
        this.asForIdentity = null;
        if (this.columnName != null) {
            if ((this.columnName.startsWith("[") && this.columnName.endsWith("]")) || (this.columnName.startsWith("`") && this.columnName.endsWith("`"))) {
                this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
                if (this.columnName.indexOf(32) != -1) {
                    this.columnName = "\"" + this.columnName + "\"";
                }
            }
            else {
                this.columnName = "\"" + this.columnName + "\"";
            }
        }
        if (this.getConstraintClause() != null) {
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toANSISQLConstraintClause = changeConstraintVector.get(i);
                    if (toANSISQLConstraintClause != null) {
                        toANSISQLConstraintClause.setColumnName(this.getColumnName());
                        toANSISQLConstraintClause.toANSIString();
                        this.constraintNullVector.add(toANSISQLConstraintClause);
                    }
                }
            }
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnAS("AS");
            this.setComputedColumnExpression(this.getComputedColumnExpression().toANSISelect(null, null));
        }
        if (this.getNullStatus() != null) {
            this.constraintNullVector.add(this.getNullStatus());
        }
        if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
            final Datatype toANSISQLDatatype = this.getDatatype();
            toANSISQLDatatype.toANSIString();
            if (toANSISQLDatatype instanceof CharacterClass) {
                this.enumValuesConvertedToCheckConstraints(toANSISQLDatatype, this.constraintNullVector);
            }
        }
        if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            final int size = this.defaultExpList.size();
            if (size == 1) {
                if (this.defaultExpList.get(0) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(0);
                    final Vector expList = sc.getColumnExpression();
                    if (expList.size() == 1) {
                        final Object obj = expList.get(0);
                        if (obj instanceof String) {
                            this.setDefaultValue((String)obj);
                        }
                        else if (obj instanceof TableColumn) {
                            final TableColumn tc = (TableColumn)obj;
                            final String colName = tc.getColumnName();
                            if (colName != null) {
                                this.setDefaultValue(colName);
                            }
                        }
                    }
                }
                else {
                    final ArrayList expWithoutSc = this.getDefaultExpression();
                    if (expWithoutSc.size() == 1 && expWithoutSc.get(0) instanceof String) {
                        this.setDefaultValue(expWithoutSc.get(0));
                    }
                }
            }
        }
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("USER")) {
                this.setDefaultValue("CURRENT_USER");
            }
        }
        else if (this.getDefaultFunction() != null) {
            this.setDefaultFunction(this.functionCall.toANSISelect(null, null));
        }
        else if (this.getDefault() != null) {
            this.setDefaultValue("NULL");
            if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
        if (this.getCastDatatype() != null) {
            this.getCastDatatype().toANSIString();
            this.createCastFunction();
        }
    }
    
    public void toInformixString() throws ConvertException {
        this.constraintNullVector = new Vector();
        this.setIdentity(null);
        this.setAutoIncrement(null);
        this.setDatapageStorageLevel(null);
        this.setCollate(null);
        this.setCollationName(null);
        this.generated = null;
        this.always = null;
        this.asForIdentity = null;
        this.byForAlways = null;
        this.defaultForIdentity = null;
        this.asForIdentity = null;
        if (this.columnName != null && ((this.columnName.startsWith("[") && this.columnName.endsWith("]")) || (this.columnName.startsWith("`") && this.columnName.endsWith("`")))) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
                this.columnName = "\"" + this.columnName + "\"";
            }
        }
        if (this.getNullStatus() != null) {
            if (this.getNullStatus().trim().equalsIgnoreCase("NULL")) {
                this.setNullStatus(null);
            }
            else {
                this.constraintNullVector.add(this.getNullStatus());
            }
        }
        if (this.getConstraintClause() != null) {
            final Vector constraintVector = this.getConstraintClause();
            if (constraintVector != null) {
                for (int i = 0; i < constraintVector.size(); ++i) {
                    final ConstraintClause toInformixConstraintClause = constraintVector.get(i);
                    if (toInformixConstraintClause != null) {
                        final ConstraintType toInformixConstraintType = toInformixConstraintClause.getConstraintType();
                        toInformixConstraintClause.setColumnName(this.getColumnName());
                        toInformixConstraintClause.toInformixString();
                        if (toInformixConstraintType instanceof DefaultConstraintClause) {
                            this.constraintNullVector.insertElementAt(toInformixConstraintClause, 0);
                        }
                        else {
                            this.constraintNullVector.add(toInformixConstraintClause);
                        }
                    }
                }
            }
        }
        if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
            final Datatype toInformixDatatype = this.getDatatype();
            toInformixDatatype.toInformixString();
            if (toInformixDatatype instanceof CharacterClass) {
                this.enumValuesConvertedToCheckConstraints(toInformixDatatype, this.constraintNullVector);
            }
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnExpression(this.getComputedColumnExpression().toInformixSelect(null, null));
        }
        if (this.getDefaultValue() != null) {
            this.setDefaultOpenBrace(null);
            this.setDefaultClosedBrace(null);
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("TODAY");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("TODAY");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("TODAY");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("TODAY");
            }
            else if (this.defaultValue.equalsIgnoreCase("SYSTEM_USER") || this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                this.setDefaultValue("USER");
            }
            else if (this.defaultValue.indexOf("'") != -1) {
                this.setDefaultValue(this.defaultValue.replace('\'', '\"'));
            }
        }
        else if (this.getDefaultFunction() != null) {
            this.setDefaultFunction(this.functionCall.toInformixSelect(null, null));
        }
        else if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            for (int i = 0; i < this.defaultExpList.size(); ++i) {
                if (this.defaultExpList.get(i) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(i);
                    newExpList.add(sc.toInformixSelect(null, null));
                }
                else {
                    newExpList.add(this.defaultExpList.get(i));
                }
            }
            this.setDefaultExpression(newExpList);
        }
        else if (this.getDefault() != null) {
            this.setDefaultValue("NULL");
            if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
    }
    
    public void toTimesTenString() throws ConvertException {
        this.constraintNullVector = new Vector();
        this.generated = null;
        this.always = null;
        this.byForAlways = null;
        this.setDatapageStorageLevel(this.defaultForIdentity = null);
        if (this.columnName != null && ((this.columnName.startsWith("[") && this.columnName.endsWith("]")) || (this.columnName.startsWith("`") && this.columnName.endsWith("`")))) {
            this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
            if (this.columnName.indexOf(32) != -1) {
                this.columnName = "\"" + this.columnName + "\"";
            }
        }
        boolean isPKColumn = false;
        if (this.getConstraintClause() != null) {
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toTimesTenConstraintClause = changeConstraintVector.get(i);
                    if (toTimesTenConstraintClause != null) {
                        final ConstraintType toTimesTenConstraintType = toTimesTenConstraintClause.getConstraintType();
                        toTimesTenConstraintClause.setColumnName(this.getColumnName());
                        toTimesTenConstraintClause.toTimesTenString();
                        if (toTimesTenConstraintType instanceof DefaultConstraintClause) {
                            if (CreateQueryStatement.commentWhenConstraintNameTruncated.trim().length() > 0 && CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("DEFAULT Constraint") == -1) {
                                CreateQueryStatement.commentWhenConstraintNameTruncated += "\n/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
                            }
                            else if (CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("DEFAULT Constraint") == -1) {
                                CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
                            }
                        }
                        else {
                            if (toTimesTenConstraintClause.getAutoIncrement() != null) {
                                toTimesTenConstraintClause.setAutoIncrement(null);
                            }
                            toTimesTenConstraintClause.setColumnName(this.getColumnName());
                            if (toTimesTenConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                final PrimaryOrUniqueConstraintClause tempPrimaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toTimesTenConstraintType;
                                if (toTimesTenConstraintClause.getConstraint() != null) {
                                    toTimesTenConstraintClause.setConstraint(null);
                                    toTimesTenConstraintClause.setConstraintName(null);
                                }
                                if (tempPrimaryOrUniqueConstraintClause.getConstraintName().equalsIgnoreCase("PRIMARY KEY")) {
                                    isPKColumn = true;
                                }
                                toTimesTenConstraintClause.toTimesTenString();
                                this.constraintNullVector.add(toTimesTenConstraintClause);
                            }
                            else {
                                toTimesTenConstraintClause.toTimesTenString();
                                this.constraintNullVector.add(toTimesTenConstraintClause);
                            }
                        }
                    }
                }
            }
        }
        if (this.onDefault != null && this.onDefault.trim().equalsIgnoreCase("DEFAULT") && this.getDefaultValue() != null && this.getDefaultValue().trim().equalsIgnoreCase("null") && this.notNull == null) {
            final NotNull newNotNull = new NotNull();
            newNotNull.setNullStatus("NULL");
            this.setNotNull(newNotNull);
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnExpression(this.getComputedColumnExpression().toTimesTenSelect(null, null));
        }
        if (!this.isAlterStatement) {
            if (this.getNullStatus() != null) {
                this.constraintNullVector.add(this.getNullStatus());
            }
            else if (!isPKColumn && this.getColumnName() != null && SwisSQLOptions.fromSybase && SwisSQLOptions.sybaseNotNullConstraint) {
                this.constraintNullVector.add("NOT NULL");
            }
        }
        if (this.getDatatype() != null) {
            if (!this.mapDatatype(this, this.datatypeMapping)) {
                final Datatype toTimesTenDatatype = this.getDatatype();
                toTimesTenDatatype.toTimesTenString();
                toTimesTenDatatype.setDatatypeName(toTimesTenDatatype.getDatatypeName().toUpperCase());
                if (toTimesTenDatatype instanceof CharacterClass) {
                    this.enumValuesConvertedToCheckConstraints(toTimesTenDatatype, this.constraintNullVector);
                }
            }
        }
        else if (this.userDefinedDatatype != null && CreateColumn.userdefinedDatatypes.size() > 0) {
            if (!this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype)) {
                if (CreateColumn.userdefinedDatatypes.containsKey(this.userDefinedDatatype.toLowerCase().trim())) {
                    final String baseType = CreateColumn.userdefinedDatatypes.get(this.userDefinedDatatype.toLowerCase().trim());
                    final Datatype ttType = SwisSQLUtils.constructDatatype(baseType);
                    if (ttType != null) {
                        ttType.toTimesTenString();
                        this.setUserDefinedDatatype(ttType.toString());
                    }
                    else {
                        this.setUserDefinedDatatype(baseType);
                    }
                }
                else if (this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {}
            }
        }
        else if (this.userDefinedDatatype == null || CreateColumn.userdefinedDatatypes.size() > 0 || this.mapUserDatatype(this, this.datatypeMapping, this.userDefinedDatatype) || this.userDefinedDatatype.equalsIgnoreCase("UNIQUEIDENTIFIER")) {}
        if (this.getDefaultFunction() != null || this.getDefaultValue() != null || this.getDefault() != null) {
            if (CreateQueryStatement.commentWhenConstraintNameTruncated.trim().length() > 0 && CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("DEFAULT Constraint") == -1) {
                CreateQueryStatement.commentWhenConstraintNameTruncated += "\n/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
            }
            else if (CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("DEFAULT Constraint") == -1) {
                CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : DEFAULT Constraint is not supported in TimesTen 5.1.21 */";
            }
        }
        if (this.getDefaultValue() != null) {
            this.setDefault(null);
            this.setDefaultValue(null);
            this.setDefaultClosedBrace(null);
            this.setDefaultOpenBrace(null);
        }
        else if (this.getDefaultFunction() != null) {
            this.setDefaultFunction(null);
            this.setDefault(null);
        }
        else if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            for (int i = 0; i < this.defaultExpList.size(); ++i) {
                if (this.defaultExpList.get(i) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(i);
                    newExpList.add(sc.toTimesTenSelect(null, null));
                }
                else {
                    newExpList.add(this.defaultExpList.get(i));
                }
            }
            this.setDefaultExpression(newExpList);
        }
        else if (this.getDefault() != null) {
            this.setDefault(null);
        }
        if (this.identity != null) {
            final Datatype type = this.getDatatype();
            if (type instanceof NumericClass) {
                final NumericClass nc = (NumericClass)type;
                nc.setDatatypeName("INT");
                nc.setSize(null);
                nc.setPrecision(null);
                nc.setScale(null);
                nc.setOpenBrace(null);
                nc.setClosedBrace(null);
            }
            final CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
            final TableObject tableObj = new TableObject();
            createSequenceObj.setSequence("SEQUENCE");
            final String timesTenColumnName = this.columnName;
            this.setTableNameAfterTruncation(timesTenColumnName, tableObj);
            createSequenceObj.setSchemaName(tableObj);
            if (!this.identity.trim().equalsIgnoreCase("IDENTITY")) {
                String tempIdentity = this.identity.trim().substring(8).trim();
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
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
            }
            else {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
            }
            this.setIdentity(null);
        }
        if (this.autoIncrement != null) {
            this.getDatatype().setDatatypeName("INT");
            this.getDatatype().setSize(null);
            this.getDatatype().setOpenBrace(null);
            this.getDatatype().setClosedBrace(null);
            final CreateSequenceStatement createSequenceObj2 = new CreateSequenceStatement();
            final TableObject tableObj2 = new TableObject();
            createSequenceObj2.setSequence("SEQUENCE");
            final String timesTenColumnName2 = this.columnName;
            this.setTableNameAfterTruncation(timesTenColumnName2, tableObj2);
            createSequenceObj2.setSchemaName(tableObj2);
            if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj2.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
            }
            else {
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj2.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. */\n\n";
            }
            this.autoIncrement = null;
        }
        this.asForIdentity = null;
    }
    
    public void toNetezzaString() throws ConvertException {
        this.constraintNullVector = new Vector();
        this.setIdentity(null);
        this.setDatapageStorageLevel(null);
        this.setAutoIncrement(null);
        this.setCollate(null);
        this.setCollationName(null);
        this.generated = null;
        this.always = null;
        this.asForIdentity = null;
        this.byForAlways = null;
        this.defaultForIdentity = null;
        this.asForIdentity = null;
        if (this.columnName != null) {
            if ((this.columnName.startsWith("[") && this.columnName.endsWith("]")) || (this.columnName.startsWith("`") && this.columnName.endsWith("`"))) {
                this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
                if (this.columnName.indexOf(32) != -1) {
                    this.columnName = "\"" + this.columnName + "\"";
                }
            }
            this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords(11), null, 11);
        }
        if (this.getConstraintClause() != null) {
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toNetezzaSQLConstraintClause = changeConstraintVector.get(i);
                    if (toNetezzaSQLConstraintClause != null) {
                        toNetezzaSQLConstraintClause.setColumnName(this.getColumnName());
                        toNetezzaSQLConstraintClause.toNetezzaString();
                        this.constraintNullVector.add(toNetezzaSQLConstraintClause);
                    }
                }
            }
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnAS("AS");
            this.setComputedColumnExpression(this.getComputedColumnExpression().toNetezzaSelect(null, null));
        }
        if (this.getNullStatus() != null) {
            this.constraintNullVector.add(this.getNullStatus());
        }
        if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
            final Datatype toNetezzaSQLDatatype = this.getDatatype();
            toNetezzaSQLDatatype.toNetezzaString();
            if (toNetezzaSQLDatatype instanceof CharacterClass) {
                this.enumValuesConvertedToCheckConstraints(toNetezzaSQLDatatype, this.constraintNullVector);
            }
        }
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("USER")) {
                this.setDefaultValue("CURRENT_USER");
            }
        }
        else if (this.getDefaultFunction() != null) {
            this.setDefaultFunction(this.functionCall.toNetezzaSelect(null, null));
        }
        else if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            for (int i = 0; i < this.defaultExpList.size(); ++i) {
                if (this.defaultExpList.get(i) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(i);
                    newExpList.add(sc.toNetezzaSelect(null, null));
                }
                else {
                    newExpList.add(this.defaultExpList.get(i));
                }
            }
            this.setDefaultExpression(newExpList);
        }
        else if (this.getDefault() != null) {
            this.setDefaultValue("NULL");
            if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
        if (this.getCastDatatype() != null) {
            this.getCastDatatype().toNetezzaString();
            this.createCastFunction();
        }
    }
    
    public void toTeradataString() throws ConvertException {
        this.constraintNullVector = new Vector();
        this.setIdentity(null);
        this.setDatapageStorageLevel(null);
        this.setAutoIncrement(null);
        this.setCollate(null);
        this.setCollationName(null);
        this.generated = null;
        this.always = null;
        this.asForIdentity = null;
        this.byForAlways = null;
        this.defaultForIdentity = null;
        this.asForIdentity = null;
        if (this.columnName != null) {
            if ((this.columnName.startsWith("[") && this.columnName.endsWith("]")) || (this.columnName.startsWith("`") && this.columnName.endsWith("`"))) {
                this.columnName = this.columnName.substring(1, this.columnName.length() - 1);
                if (this.columnName.indexOf(32) != -1) {
                    this.columnName = "\"" + this.columnName + "\"";
                }
            }
            else {
                this.columnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.columnName, SwisSQLUtils.getKeywords("teradata"), null, -1);
            }
        }
        if (this.getConstraintClause() != null) {
            final Vector changeConstraintVector = this.getConstraintClause();
            if (changeConstraintVector != null) {
                for (int i = 0; i < changeConstraintVector.size(); ++i) {
                    final ConstraintClause toTeradataSQLConstraintClause = changeConstraintVector.get(i);
                    if (toTeradataSQLConstraintClause != null) {
                        toTeradataSQLConstraintClause.setColumnName(this.getColumnName());
                        toTeradataSQLConstraintClause.toTeradataString();
                        this.constraintNullVector.add(toTeradataSQLConstraintClause);
                    }
                }
            }
        }
        if (this.getComputedColumnExpression() != null) {
            this.setComputedColumnAS("AS");
            this.setComputedColumnExpression(this.getComputedColumnExpression().toTeradataSelect(null, null));
        }
        if (this.getNullStatus() != null) {
            this.constraintNullVector.add(this.getNullStatus());
        }
        if (this.getDatatype() != null && !this.mapDatatype(this, this.datatypeMapping)) {
            final Datatype toTeradataSQLDatatype = this.getDatatype();
            toTeradataSQLDatatype.toTeradataString();
            if (toTeradataSQLDatatype instanceof CharacterClass) {
                this.enumValuesConvertedToCheckConstraints(toTeradataSQLDatatype, this.constraintNullVector);
            }
        }
        if (this.getDefaultExpression() != null) {
            final ArrayList newExpList = new ArrayList();
            final int size = this.defaultExpList.size();
            if (size == 1) {
                if (this.defaultExpList.get(0) instanceof SelectColumn) {
                    final SelectColumn sc = this.defaultExpList.get(0);
                    final Vector expList = sc.getColumnExpression();
                    if (expList.size() == 1) {
                        final Object obj = expList.get(0);
                        if (obj instanceof String) {
                            this.setDefaultValue((String)obj);
                        }
                        else if (obj instanceof TableColumn) {
                            final TableColumn tc = (TableColumn)obj;
                            final String colName = tc.getColumnName();
                            if (colName != null) {
                                this.setDefaultValue(colName);
                            }
                        }
                    }
                }
                else {
                    final ArrayList expWithoutSc = this.getDefaultExpression();
                    if (expWithoutSc.size() == 1 && expWithoutSc.get(0) instanceof String) {
                        this.setDefaultValue(expWithoutSc.get(0));
                    }
                }
            }
        }
        if (this.getDefaultValue() != null) {
            if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
                this.setDefaultValue("CURRENT_TIME");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
                this.setDefaultValue("CURRENT_TIMESTAMP");
            }
            else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                this.setDefaultValue("CURRENT_DATE");
            }
            else if (this.defaultValue.equalsIgnoreCase("USER")) {
                this.setDefaultValue("CURRENT_USER");
            }
        }
        else if (this.getDefaultFunction() != null) {
            this.setDefaultFunction(this.functionCall.toTeradataSelect(null, null));
        }
        else if (this.getDefault() != null) {
            this.setDefaultValue("NULL");
            if (this.getDefault().equalsIgnoreCase("WITH DEFAULT ")) {
                this.setDefault("DEFAULT ");
            }
        }
        if (this.getCastDatatype() != null) {
            this.getCastDatatype().toTeradataString();
            this.createCastFunction();
        }
    }
    
    private void setTableNameAfterTruncation(String sourceColumnName, final TableObject tableObj) {
        if (sourceColumnName.startsWith("[") || sourceColumnName.startsWith("\"")) {
            sourceColumnName = "\"" + sourceColumnName.substring(1);
            if (sourceColumnName.endsWith("]") || sourceColumnName.endsWith("\"")) {
                sourceColumnName = sourceColumnName.substring(0, sourceColumnName.length() - 1) + "\"";
            }
            if (this.tableNameFromCQS != null) {
                String str = this.tableNameFromCQS + sourceColumnName.substring(1, sourceColumnName.length() - 1) + "_SEQ";
                final String str2 = this.tableNameFromCQS + sourceColumnName.substring(1, sourceColumnName.length() - 1);
                final String str3 = this.tableNameFromCQS;
                if (str.length() > 29) {
                    if (str2.length() > 25) {
                        str = str2.substring(0, 26) + "_SEQ";
                        if (str.startsWith("\"")) {
                            str = StringFunctions.replaceAll("", "\"", str);
                            str = "\"" + str + "\"";
                        }
                    }
                    else if (str3.length() > 25) {
                        str = str3.substring(0, 26) + "_SEQ";
                        if (str3.startsWith("\"")) {
                            str = StringFunctions.replaceAll("", "\"", str3);
                            str = "\"" + str + "\"";
                        }
                    }
                    if (str.length() > 27) {
                        tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                        String table_name_str = tableObj.getTableName();
                        if (table_name_str.startsWith("\"")) {
                            table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                            table_name_str = "\"" + table_name_str + "\"";
                            tableObj.setTableName(table_name_str);
                        }
                    }
                    else {
                        tableObj.setTableName("\"" + str + "\"");
                        String table_name_str = tableObj.getTableName();
                        if (table_name_str.startsWith("\"")) {
                            table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                            table_name_str = "\"" + table_name_str + "\"";
                            tableObj.setTableName(table_name_str);
                        }
                    }
                }
                else if (str.length() > 27) {
                    tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + sourceColumnName + "_S" + "\"");
                    String table_name_str = tableObj.getTableName();
                    if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                    }
                }
                else {
                    tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + sourceColumnName + "_SEQ" + "\"");
                    String table_name_str = tableObj.getTableName();
                    if (table_name_str.startsWith("\"")) {
                        table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                        table_name_str = "\"" + table_name_str + "\"";
                        tableObj.setTableName(table_name_str);
                    }
                }
            }
            else {
                tableObj.setTableName(sourceColumnName + "_SEQ");
                String table_name_str2 = tableObj.getTableName();
                if (table_name_str2.startsWith("\"")) {
                    table_name_str2 = StringFunctions.replaceAll("", "\"", table_name_str2);
                    table_name_str2 = "\"" + table_name_str2 + "\"";
                    tableObj.setTableName(table_name_str2);
                }
            }
        }
        else if (this.tableNameFromCQS != null) {
            String str = this.tableNameFromCQS + "_" + sourceColumnName + "_SEQ";
            final String str2 = this.tableNameFromCQS + "_" + sourceColumnName;
            final String str3 = this.tableNameFromCQS;
            if (str.length() > 29) {
                if (str2.length() > 25) {
                    str = str2.substring(0, 26) + "_SEQ";
                }
                else if (str3.length() > 25) {
                    str = str3.substring(0, 26) + "_SEQ";
                }
                tableObj.setTableName(str);
                String table_name_str = tableObj.getTableName();
                if (table_name_str.startsWith("\"")) {
                    table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                    table_name_str = "\"" + table_name_str + "\"";
                    tableObj.setTableName(table_name_str);
                }
            }
            else {
                tableObj.setTableName(this.tableNameFromCQS + "_" + sourceColumnName + "_SEQ");
                String table_name_str = tableObj.getTableName();
                if (table_name_str.startsWith("\"")) {
                    table_name_str = StringFunctions.replaceAll("", "\"", table_name_str);
                    table_name_str = "\"" + table_name_str + "\"";
                    tableObj.setTableName(table_name_str);
                }
            }
        }
        else {
            tableObj.setTableName(this.columnName + "_SEQ");
            String table_name_str2 = tableObj.getTableName();
            if (table_name_str2.startsWith("\"")) {
                table_name_str2 = StringFunctions.replaceAll("", "\"", table_name_str2);
                table_name_str2 = "\"" + table_name_str2 + "\"";
                tableObj.setTableName(table_name_str2);
            }
        }
    }
    
    public CreateColumn copyObjectValues() {
        final CreateColumn dupCreateColumn = new CreateColumn();
        final Vector orgConstraintVector = this.getConstraintClause();
        if (orgConstraintVector != null) {
            final Vector newConstraintVector = new Vector();
            for (int i = 0; i < orgConstraintVector.size(); ++i) {
                final ConstraintClause orgConstraintClause = orgConstraintVector.get(i);
                if (orgConstraintClause != null) {
                    final ConstraintClause newConstraintClause = orgConstraintClause.copyObjectValues();
                    newConstraintVector.add(newConstraintClause);
                }
            }
            dupCreateColumn.setConstraintClause(newConstraintVector);
        }
        dupCreateColumn.setColumnName(this.getColumnName());
        dupCreateColumn.setObjectContext(this.context);
        final Datatype orgDatatype = this.getDatatype();
        if (orgDatatype != null) {
            Datatype dupDatatype = null;
            if (orgDatatype instanceof NumericClass) {
                final NumericClass numericDatatype = (NumericClass)orgDatatype;
                dupDatatype = numericDatatype.copyObjectValues();
            }
            else if (orgDatatype instanceof CharacterClass) {
                final CharacterClass characterDatatype = (CharacterClass)orgDatatype;
                dupDatatype = characterDatatype.copyObjectValues();
            }
            else if (orgDatatype instanceof DateClass) {
                final DateClass dateDatatype = (DateClass)orgDatatype;
                dupDatatype = dateDatatype.copyObjectValues();
            }
            else if (orgDatatype instanceof BinClass) {
                final BinClass binDatatype = (BinClass)orgDatatype;
                dupDatatype = binDatatype.copyObjectValues();
            }
            else if (orgDatatype instanceof GeometryClass) {
                final GeometryClass geometryDatatype = (GeometryClass)orgDatatype;
                dupDatatype = geometryDatatype.copyObjectValues();
            }
            else if (orgDatatype instanceof QuotedIdentifierDatatype) {
                final QuotedIdentifierDatatype quotedDatatype = (QuotedIdentifierDatatype)orgDatatype;
                dupDatatype = quotedDatatype.copyObjectValues();
            }
            dupCreateColumn.setDatatype(dupDatatype);
        }
        dupCreateColumn.setUserDefinedDatatype(this.userDefinedDatatype);
        dupCreateColumn.setDefault(this.getDefault());
        dupCreateColumn.setDefaultOpenBrace(this.defaultOpenBrace);
        dupCreateColumn.setDefaultValue(this.getDefaultValue());
        dupCreateColumn.setDefaultClosedBrace(this.defaultClosedBrace);
        dupCreateColumn.setIdentity(this.getIdentity());
        dupCreateColumn.setNullStatus(this.getNullStatus());
        dupCreateColumn.setDefaultFunction(this.getDefaultFunction());
        dupCreateColumn.setCollate(this.collate);
        dupCreateColumn.setCollationName(this.collationName);
        dupCreateColumn.setGenerated(this.generated);
        dupCreateColumn.setAlways(this.always);
        dupCreateColumn.setByForAlways(this.byForAlways);
        dupCreateColumn.setDefaultForIdentity(this.defaultForIdentity);
        dupCreateColumn.setIdentityAs(this.asForIdentity);
        dupCreateColumn.setAutoIncrement(this.autoIncrement);
        dupCreateColumn.setDatapageStorageLevel(this.datapageStorageLevel);
        dupCreateColumn.setDefaultExpression(this.defaultExpList);
        dupCreateColumn.setCastDatatype(this.castDatatype);
        dupCreateColumn.setComputedColumnExpression(this.computedColumnExpression);
        return dupCreateColumn;
    }
    
    private boolean mapDatatype(final CreateColumn changeCreateColumn, final DatatypeMapping mapping) {
        final Datatype changeCreateColumnDatatype = changeCreateColumn.getDatatype();
        if (changeCreateColumnDatatype != null) {
            String datatypeName = changeCreateColumnDatatype.getDatatypeName();
            if (datatypeName != null) {
                if (SwisSQLAPI.objectContext != null) {
                    final String tableName = this.tableNameFromCQS;
                    final String columnName = changeCreateColumn.getColumnName();
                    Object val = null;
                    if ((datatypeName.equalsIgnoreCase("varchar") || datatypeName.equalsIgnoreCase("nvarchar")) && changeCreateColumn.getDatatype().getSize() != null && changeCreateColumn.getDatatype().getSize().equalsIgnoreCase("max")) {
                        final String dataTypeNameWithSize = datatypeName.trim() + "(" + changeCreateColumn.getDatatype().getSize() + ")";
                        val = SwisSQLAPI.objectContext.getMappedDatatype(tableName, columnName, dataTypeNameWithSize);
                    }
                    else {
                        val = SwisSQLAPI.objectContext.getMappedDatatype(tableName, columnName, datatypeName);
                    }
                    if (val != null) {
                        final String newDatatypeName = (String)val;
                        if (changeCreateColumn.getDatatype().getSize() != null && changeCreateColumn.getDatatype().getSize().equalsIgnoreCase("max") && (!newDatatypeName.startsWith("varchar") || !newDatatypeName.startsWith("nvarchar"))) {
                            changeCreateColumnDatatype.setOpenBrace(null);
                            changeCreateColumnDatatype.setClosedBrace(null);
                            changeCreateColumnDatatype.setSize(null);
                        }
                        if (newDatatypeName.indexOf("(") != -1) {
                            changeCreateColumnDatatype.setDatatypeName(newDatatypeName.substring(0, newDatatypeName.indexOf("(")));
                            changeCreateColumnDatatype.setOpenBrace("(");
                            changeCreateColumnDatatype.setClosedBrace(")");
                            changeCreateColumnDatatype.setSize(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                            if (changeCreateColumnDatatype instanceof QuotedIdentifierDatatype) {
                                ((QuotedIdentifierDatatype)changeCreateColumnDatatype).setPrecision(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                            }
                        }
                        else {
                            changeCreateColumnDatatype.setDatatypeName(newDatatypeName);
                        }
                        return true;
                    }
                }
                if (mapping != null) {
                    final Hashtable tableSpecificMapping = mapping.getTableSpecificDatatypeMapping();
                    if (tableSpecificMapping != null && this.tableNameFromCQS != null) {
                        boolean contain = tableSpecificMapping.containsKey(this.tableNameFromCQS.toLowerCase());
                        String tableNameWithoutDelimiter = null;
                        if (!contain) {
                            tableNameWithoutDelimiter = this.removeDelimiter(this.tableNameFromCQS.toLowerCase());
                            contain = tableSpecificMapping.containsKey(tableNameWithoutDelimiter);
                        }
                        if (contain) {
                            Hashtable column = tableSpecificMapping.get(this.tableNameFromCQS.toLowerCase());
                            if (column == null) {
                                column = tableSpecificMapping.get(tableNameWithoutDelimiter);
                            }
                            if (column != null) {
                                String newDatatypeName2 = column.get(changeCreateColumn.getColumnName().toLowerCase());
                                if (newDatatypeName2 == null) {
                                    newDatatypeName2 = column.get(this.removeDelimiter(changeCreateColumn.getColumnName().toLowerCase()));
                                }
                                if (newDatatypeName2 != null) {
                                    if (newDatatypeName2.indexOf("(") != -1) {
                                        changeCreateColumnDatatype.setDatatypeName(newDatatypeName2.substring(0, newDatatypeName2.indexOf("(")));
                                        changeCreateColumnDatatype.setOpenBrace("(");
                                        changeCreateColumnDatatype.setClosedBrace(")");
                                        changeCreateColumnDatatype.setSize(newDatatypeName2.substring(newDatatypeName2.indexOf("(") + 1, newDatatypeName2.indexOf(")")));
                                        if (changeCreateColumnDatatype instanceof QuotedIdentifierDatatype) {
                                            ((QuotedIdentifierDatatype)changeCreateColumnDatatype).setPrecision(newDatatypeName2.substring(newDatatypeName2.indexOf("(") + 1, newDatatypeName2.indexOf(")")));
                                        }
                                    }
                                    else {
                                        changeCreateColumnDatatype.setDatatypeName(newDatatypeName2);
                                        if (changeCreateColumnDatatype.getOpenBrace() != null) {
                                            if (changeCreateColumnDatatype instanceof NumericClass) {
                                                final NumericClass nC = (NumericClass)changeCreateColumnDatatype;
                                                if (nC.getPrecision() != null) {
                                                    nC.setPrecision(null);
                                                    if (nC.getScale() != null) {
                                                        nC.setScale(null);
                                                    }
                                                }
                                            }
                                            changeCreateColumnDatatype.setOpenBrace(null);
                                            changeCreateColumnDatatype.setSize(null);
                                            changeCreateColumnDatatype.setClosedBrace(null);
                                        }
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                    final Hashtable globalMapping = mapping.getGlobalDatatypeMapping();
                    if (globalMapping != null) {
                        final String origDatatypeName = datatypeName;
                        if (changeCreateColumnDatatype instanceof NumericClass) {
                            final NumericClass nc = (NumericClass)changeCreateColumnDatatype;
                            if (nc.getPrecision() != null) {
                                datatypeName = datatypeName + "(" + nc.getPrecision();
                                if (nc.getScale() != null) {
                                    datatypeName = datatypeName + "," + nc.getScale();
                                }
                                datatypeName += ")";
                            }
                        }
                        else if (changeCreateColumnDatatype.getOpenBrace() != null) {
                            if (changeCreateColumnDatatype instanceof QuotedIdentifierDatatype) {
                                if (changeCreateColumnDatatype.getSize() != null) {
                                    datatypeName = datatypeName + "(" + changeCreateColumnDatatype.getSize() + ")";
                                }
                                else if (((QuotedIdentifierDatatype)changeCreateColumnDatatype).getPrecision() != null) {
                                    datatypeName = datatypeName + "(" + ((QuotedIdentifierDatatype)changeCreateColumnDatatype).getPrecision() + ")";
                                }
                            }
                            else {
                                datatypeName = datatypeName + "(" + changeCreateColumnDatatype.getSize() + ")";
                            }
                        }
                        CharacterClass cc = null;
                        if (changeCreateColumnDatatype instanceof CharacterClass) {
                            cc = (CharacterClass)changeCreateColumnDatatype;
                            if (cc.getBinary() != null && cc.getBinary().toUpperCase().indexOf("FOR BIT DATA") != -1) {
                                datatypeName += " FOR BIT DATA";
                            }
                        }
                        if (globalMapping.containsKey(datatypeName.toLowerCase())) {
                            final String newDatatypeName2 = globalMapping.get(datatypeName.toLowerCase());
                            if (newDatatypeName2.indexOf("(") != -1) {
                                changeCreateColumnDatatype.setDatatypeName(newDatatypeName2.substring(0, newDatatypeName2.indexOf("(")));
                                changeCreateColumnDatatype.setOpenBrace("(");
                                changeCreateColumnDatatype.setClosedBrace(")");
                                changeCreateColumnDatatype.setSize(newDatatypeName2.substring(newDatatypeName2.indexOf("(") + 1, newDatatypeName2.indexOf(")")));
                                if (changeCreateColumnDatatype instanceof QuotedIdentifierDatatype) {
                                    ((QuotedIdentifierDatatype)changeCreateColumnDatatype).setPrecision(newDatatypeName2.substring(newDatatypeName2.indexOf("(") + 1, newDatatypeName2.indexOf(")")));
                                }
                            }
                            else if (datatypeName.indexOf("(") != -1) {
                                changeCreateColumnDatatype.setDatatypeName(newDatatypeName2);
                                changeCreateColumnDatatype.setOpenBrace(null);
                                changeCreateColumnDatatype.setClosedBrace(null);
                                changeCreateColumnDatatype.setSize(null);
                                if (changeCreateColumnDatatype instanceof NumericClass) {
                                    final NumericClass nc2 = (NumericClass)changeCreateColumnDatatype;
                                    nc2.setPrecision(null);
                                    nc2.setScale(null);
                                }
                            }
                            else {
                                changeCreateColumnDatatype.setDatatypeName(newDatatypeName2);
                            }
                            if (cc != null && cc.getBinary() != null) {
                                cc.setBinary(null);
                            }
                            return true;
                        }
                        if (globalMapping.containsKey(origDatatypeName.toLowerCase())) {
                            String newDatatypeName2 = globalMapping.get(origDatatypeName.toLowerCase());
                            if (newDatatypeName2.indexOf("(") != -1) {
                                final String size = newDatatypeName2.substring(newDatatypeName2.indexOf("(") + 1, newDatatypeName2.indexOf(")"));
                                changeCreateColumnDatatype.setOpenBrace("(");
                                changeCreateColumnDatatype.setClosedBrace(")");
                                changeCreateColumnDatatype.setSize(size);
                                newDatatypeName2 = newDatatypeName2.substring(0, newDatatypeName2.indexOf("("));
                            }
                            changeCreateColumnDatatype.setDatatypeName(newDatatypeName2);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean mapUserDatatype(final CreateColumn changeCreateColumn, final DatatypeMapping mapping, final String datatypeName) {
        if (SwisSQLAPI.objectContext != null) {
            final String tableName = this.tableNameFromCQS;
            final String columnName = changeCreateColumn.getColumnName();
            final Object val = SwisSQLAPI.objectContext.getMappedDatatype(tableName, columnName, datatypeName);
            if (val != null) {
                this.setUserDefinedDatatype((String)val);
                return true;
            }
        }
        if (mapping != null) {
            final Hashtable tableSpecificMapping = mapping.getTableSpecificDatatypeMapping();
            if (tableSpecificMapping != null && this.tableNameFromCQS != null) {
                boolean contain = tableSpecificMapping.containsKey(this.tableNameFromCQS.toLowerCase());
                String tableNameWithoutDelimiter = null;
                if (!contain) {
                    tableNameWithoutDelimiter = this.removeDelimiter(this.tableNameFromCQS.toLowerCase());
                    contain = tableSpecificMapping.containsKey(tableNameWithoutDelimiter);
                }
                if (contain) {
                    Hashtable column = tableSpecificMapping.get(this.tableNameFromCQS.toLowerCase());
                    if (column == null) {
                        column = tableSpecificMapping.get(tableNameWithoutDelimiter);
                    }
                    if (column != null) {
                        String newDatatypeName = column.get(changeCreateColumn.getColumnName().toLowerCase());
                        if (newDatatypeName == null) {
                            newDatatypeName = column.get(this.removeDelimiter(changeCreateColumn.getColumnName().toLowerCase()));
                        }
                        if (newDatatypeName != null) {
                            this.setUserDefinedDatatype(newDatatypeName);
                            return true;
                        }
                    }
                }
            }
            final Hashtable globalMapping = mapping.getGlobalDatatypeMapping();
            if (globalMapping != null && globalMapping.containsKey(datatypeName.toLowerCase())) {
                final String newDatatypeName2 = globalMapping.get(datatypeName.toLowerCase());
                this.setUserDefinedDatatype(newDatatypeName2);
                return true;
            }
        }
        return false;
    }
    
    private String removeDelimiter(final String name) {
        if ((name.startsWith("[") && name.endsWith("]")) || (name.startsWith("\"") && name.endsWith("\"")) || (name.startsWith("`") && name.endsWith("`"))) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.columnName != null) {
            if (this.context != null) {
                final String temp = this.context.getEquivalent(this.columnName).toString();
                sb.append(temp + " ");
            }
            else {
                sb.append(this.columnName + " ");
            }
        }
        if (this.datatype != null) {
            if (this.datatype.toString().trim().equalsIgnoreCase("BOOLEAN")) {
                if (this.booleanDb2) {
                    sb.append("SMALLINT CHECK(" + this.columnName + "=0 or " + this.columnName + "=1) ");
                }
                else if (this.booleanOracle) {
                    if (this.getDefaultValue() != null && this.onDefault != null) {
                        sb.append("NUMBER(1) " + this.onDefault + " " + this.getDefaultValue() + " CHECK(" + this.columnName + "=0 or " + this.columnName + "=1) ");
                    }
                    else {
                        sb.append("NUMBER(1) CHECK(" + this.columnName + "=0 or " + this.columnName + "=1) ");
                    }
                }
                else {
                    sb.append(this.datatype.toString() + " ");
                }
            }
            else {
                sb.append(this.datatype.toString() + " ");
            }
        }
        else if (this.userDefinedDatatype != null) {
            sb.append(this.userDefinedDatatype + " ");
        }
        if (this.computedColumnExpression != null) {
            if (this.computedColumnAS != null) {
                sb.append(this.computedColumnAS + " ");
            }
            sb.append(this.computedColumnExpression.toString() + " ");
        }
        if (this.collate != null) {
            sb.append(this.collate + " ");
        }
        if (this.collationName != null) {
            sb.append(this.collationName + " ");
        }
        if (this.sparseStr != null) {
            sb.append(this.sparseStr + " ");
        }
        if (this.onDefault != null && !this.booleanOracle) {
            sb.append(this.onDefault.toUpperCase() + " ");
        }
        if (this.defaultOpenBrace != null) {
            sb.append(this.defaultOpenBrace + " ");
        }
        if (this.defaultValue != null && !this.booleanOracle) {
            sb.append(this.defaultValue + " ");
        }
        else if (this.getDefaultFunction() != null) {
            this.functionCall.setObjectContext(this.context);
            sb.append(this.functionCall + " ");
        }
        else if (this.getDefaultExpression() != null) {
            for (int i = 0; i < this.defaultExpList.size(); ++i) {
                sb.append(this.defaultExpList.get(i) + " ");
            }
        }
        if (this.defaultClosedBrace != null) {
            sb.append(this.defaultClosedBrace + " ");
        }
        if (this.generated != null) {
            sb.append(" " + this.generated.toUpperCase());
        }
        if (this.always != null) {
            sb.append(" " + this.always.toUpperCase());
        }
        if (this.byForAlways != null) {
            sb.append(" " + this.byForAlways.toUpperCase());
        }
        if (this.defaultForIdentity != null) {
            sb.append(" " + this.defaultForIdentity);
        }
        if (this.asForIdentity != null) {
            sb.append(" " + this.asForIdentity.toUpperCase() + " ");
        }
        if (this.identity != null) {
            sb.append(this.identity + " ");
        }
        if (this.constraintNullVector != null) {
            for (int i = 0; i < this.constraintNullVector.size(); ++i) {
                if (this.constraintNullVector.get(i) instanceof String) {
                    final String str = sb.toString().trim();
                    if (this.constraintNullVector.get(i).trim().toUpperCase().equals("NOT NULL")) {
                        if (str.toUpperCase().indexOf("NOT NULL") == -1) {
                            sb.append(this.constraintNullVector.get(i).toUpperCase() + " ");
                        }
                    }
                    else if (this.constraintNullVector.get(i).equalsIgnoreCase("null") || this.constraintNullVector.get(i).equalsIgnoreCase("not null")) {
                        sb.append(this.constraintNullVector.get(i).toUpperCase() + " ");
                    }
                    else {
                        sb.append(this.constraintNullVector.get(i) + " ");
                    }
                }
                else if (this.constraintNullVector.get(i) instanceof ConstraintClause) {
                    final ConstraintClause constraintClause = this.constraintNullVector.get(i);
                    constraintClause.setObjectContext(this.context);
                    sb.append(constraintClause.toString());
                }
                else if (this.constraintNullVector.get(i) instanceof CreateColumn) {
                    final CreateColumn createCol = this.constraintNullVector.get(i);
                    createCol.setObjectContext(this.context);
                    sb.append(createCol.toString());
                }
            }
        }
        else if (this.constraintVector != null || this.getNullStatus() != null) {
            if (this.constraintVector != null) {
                for (int i = 0; i < this.constraintVector.size(); ++i) {
                    final ConstraintClause constraintClause = this.constraintVector.get(i);
                    constraintClause.setObjectContext(this.context);
                    sb.append(constraintClause.toString());
                }
            }
            if (this.getNullStatus() != null) {
                sb.append(this.nullStatus.toUpperCase() + " ");
            }
        }
        if (this.autoIncrement != null) {
            sb.append(" " + this.autoIncrement.toUpperCase() + " ");
        }
        if (this.getNullStatus() != null && !this.getNullStatus().trim().toUpperCase().equals("NULL")) {
            final String str2 = sb.toString().trim();
            if (str2.toUpperCase().indexOf("NOT NULL") == -1) {
                sb.append(this.nullStatus.toUpperCase() + " ");
            }
        }
        if (this.datapageStorageLevel != null) {
            sb.append(this.datapageStorageLevel.toUpperCase() + " ROW");
        }
        return sb.toString();
    }
    
    public static void addToUserDefinedDataTypes(final String type, final String basetype) {
        if (type != null && basetype != null) {
            CreateColumn.userdefinedDatatypes.put(type, basetype);
            CreateColumn.userConfiguredDatatypes.put(type, basetype);
        }
    }
    
    public static Hashtable getUserDefinedDatatypes() {
        return CreateColumn.userdefinedDatatypes;
    }
    
    public static boolean isTypePresent(final String type) {
        return CreateColumn.userConfiguredDatatypes.containsKey(type);
    }
    
    public static void readUserDefinedDatatypes() {
        try {
            final FileInputStream fis = new FileInputStream("conf/SQLServerUDDBaseTypeEquivalents.conf");
            final InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br;
            String functionString;
            int index;
            StringTokenizer st;
            String udd;
            String oracleBaseDatatype;
            for (br = new BufferedReader(isr), functionString = new String(), functionString = br.readLine(); functionString != null; functionString = br.readLine()) {
                if (!functionString.trim().equals("")) {
                    if ((index = functionString.trim().indexOf("/*")) != -1) {
                        if (index > 0) {
                            functionString = functionString.substring(0, functionString.indexOf("/*"));
                            st = new StringTokenizer(functionString, "=");
                            if (st.countTokens() == 2) {
                                udd = st.nextToken().trim().toLowerCase();
                                oracleBaseDatatype = st.nextToken().trim();
                                CreateColumn.userdefinedDatatypes.put(udd, oracleBaseDatatype);
                            }
                        }
                        while (functionString != null && functionString.indexOf("*/") == -1) {
                            functionString = br.readLine();
                        }
                    }
                    else {
                        if (functionString.trim().indexOf("--") != -1) {
                            functionString = functionString.substring(0, functionString.indexOf("--"));
                        }
                        st = new StringTokenizer(functionString, "=");
                        if (st.countTokens() == 2) {
                            udd = st.nextToken().trim().toLowerCase();
                            oracleBaseDatatype = st.nextToken().trim();
                            CreateColumn.userdefinedDatatypes.put(udd, oracleBaseDatatype);
                        }
                    }
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void enumValuesConvertedToCheckConstraints(final Datatype datatype, final Vector constraintNullVector) {
        final ArrayList enumValues = ((CharacterClass)datatype).getEnumValues();
        if (enumValues != null && !enumValues.isEmpty()) {
            final WhereExpression checkWE = new WhereExpression();
            final WhereColumn lwc = new WhereColumn();
            final Vector lwcV = new Vector();
            lwcV.add(this.columnName);
            lwc.setColumnExpression(lwcV);
            for (int i = 0; i < enumValues.size(); ++i) {
                final WhereItem wi = new WhereItem();
                wi.setLeftWhereExp(lwc);
                final WhereColumn rwc = new WhereColumn();
                final Vector rwcV = new Vector();
                rwcV.add(enumValues.get(i));
                rwc.setColumnExpression(rwcV);
                wi.setRightWhereExp(rwc);
                wi.setOperator("=");
                checkWE.addWhereItem(wi);
                if (i != enumValues.size() - 1) {
                    checkWE.addOperator("OR");
                }
            }
            final Vector constrClaV = this.getConstraintClause();
            boolean isCheckConstrExists = false;
            if (constrClaV != null) {
                for (int j = 0; j < constrClaV.size(); ++j) {
                    final ConstraintClause constrCla = constrClaV.get(j);
                    final ConstraintType constrType = constrCla.getConstraintType();
                    if (constrType != null && constrType instanceof CheckConstraintClause) {
                        isCheckConstrExists = true;
                        final CheckConstraintClause checkConstr = (CheckConstraintClause)constrType;
                        final WhereExpression we = checkConstr.getWhereExpression();
                        if (we != null) {
                            we.addOperator("OR");
                            we.addWhereExpression(checkWE);
                        }
                    }
                }
            }
            if (!isCheckConstrExists) {
                final ConstraintClause constrCla2 = new ConstraintClause();
                final CheckConstraintClause chConstrClause = new CheckConstraintClause();
                chConstrClause.setOpenBrace("(");
                chConstrClause.setClosedBrace(")");
                chConstrClause.setWhereExpression(checkWE);
                chConstrClause.setConstraintName("CHECK");
                constrCla2.setConstraintType(chConstrClause);
                constraintNullVector.addElement(constrCla2);
            }
        }
        ((CharacterClass)datatype).setEnumValues(null);
    }
    
    public static void readUserConfiguredDatatypes() {
        try {
            final FileInputStream fis = new FileInputStream("conf/MSSQLServerToOracleDatatype.conf");
            final InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br;
            String functionString;
            int index;
            StringTokenizer st;
            String udd;
            String oracleBaseDatatype;
            for (br = new BufferedReader(isr), functionString = new String(), functionString = br.readLine(); functionString != null; functionString = br.readLine()) {
                if (!functionString.trim().equals("")) {
                    if ((index = functionString.trim().indexOf("/*")) != -1) {
                        if (index > 0) {
                            functionString = functionString.substring(0, functionString.indexOf("/*"));
                            st = new StringTokenizer(functionString, "=");
                            if (st.countTokens() == 2) {
                                udd = st.nextToken().trim().toLowerCase();
                                oracleBaseDatatype = st.nextToken().trim();
                                CreateColumn.userConfiguredDatatypes.put(udd.toLowerCase(), oracleBaseDatatype);
                            }
                        }
                        while (functionString != null && functionString.indexOf("*/") == -1) {
                            functionString = br.readLine();
                        }
                    }
                    else {
                        if (functionString.trim().indexOf("--") != -1) {
                            functionString = functionString.substring(0, functionString.indexOf("--"));
                        }
                        st = new StringTokenizer(functionString, "=");
                        if (st.countTokens() == 2) {
                            udd = st.nextToken().trim().toLowerCase();
                            oracleBaseDatatype = st.nextToken().trim();
                            CreateColumn.userConfiguredDatatypes.put(udd.toLowerCase(), oracleBaseDatatype);
                        }
                    }
                }
            }
            br.close();
            isr.close();
            fis.close();
        }
        catch (final Exception ex) {}
    }
    
    public void createCastFunction() throws ConvertException {
        final SelectColumn firstArgInCastFunction = new SelectColumn();
        final SelectColumn secondArgInCastFunction = new SelectColumn();
        final Vector functionArguments = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_firstarg = new Vector();
        final Vector vec_secondarg = new Vector();
        tc.setColumnName("CAST");
        fc.setFunctionName(tc);
        vec_firstarg.addElement(this.getDefaultValue());
        vec_firstarg.addElement(" AS ");
        vec_firstarg.addElement(this.getCastDatatype());
        firstArgInCastFunction.setColumnExpression(vec_firstarg);
        functionArguments.addElement(firstArgInCastFunction);
        fc.setFunctionArguments(functionArguments);
        this.setDefaultFunction(fc);
        this.setDefaultValue(null);
    }
    
    static {
        CreateColumn.userdefinedDatatypes = new Hashtable();
        CreateColumn.userConfiguredDatatypes = new Hashtable();
    }
}
