package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.insert.InsertClause;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import java.util.List;
import java.util.StringTokenizer;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import java.util.Map;
import com.adventnet.swissqlapi.sql.statement.select.IntoStatement;
import java.util.HashMap;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.SetOperatorClause;
import com.adventnet.swissqlapi.sql.statement.select.FetchClause;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.select.WithStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectNames;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class CreateQueryStatement implements SwisSQLStatement
{
    private String createOrReplace;
    private String createString;
    private String tableOrView;
    private String as;
    private String force;
    private String onConditionForANSI;
    private String temporary;
    private TableObject tableObject;
    private SelectQueryStatement selectQueryStatement;
    private String withCheckOption;
    private String openBrace;
    private String closedBrace;
    private Vector columnNames;
    private String onCondition;
    private String organisationHeap;
    private String diskAttribute;
    private String oraclePhysicalChar;
    private String materialized;
    private UserObjectContext context;
    private String constraint;
    private String constraintName;
    private CreateIndexClause createIndexClause;
    private Vector createIndexVector;
    private boolean createIndexClauseBooleanValue;
    private String typeString;
    private String typeIdentifier;
    private CommentClass commentObject;
    private String indexString;
    private CreateSequenceStatement createSequence;
    private CreateSynonymStatement createSynonym;
    private boolean isTenroxRequirement;
    private String onConditionString;
    private String quotedIdentifierCondition;
    private Vector physicalAttributesVector;
    private String ignoreOrReplace;
    private String selectStatementString;
    private DatabaseObject databaseObject;
    private DatatypeMapping mapping;
    private String withReadOnly;
    private boolean includeDrop;
    private String dropString;
    public static String commentWhenConstraintNameTruncated;
    private ModifiedObjectNames modifiedObjects;
    private String withSchemaBinding;
    private String indexedViewStmt;
    private String lock;
    private String lockData;
    private String createSequenceStr;
    private String createSynonymStr;
    private String triggerForIdentity;
    private String mysqlCommentTableOption;
    private String distributeOnRandomClause;
    private FunctionCalls startWithFunction;
    Vector computedColumnsVector;
    private InsertQueryStatement insertQueryStatement;
    private String externalTable;
    private String externalDirectory;
    private String externalDelimiter;
    private String startWith;
    private String nextString;
    private String viewMetaAttribute;
    private SelectColumn selectColumnInNextClause;
    private CreateQueryStatement computedColView;
    private String onConditionForTeradata;
    private String noLogConditionForTeradata;
    private boolean comment_flag__for_create_as_select_in_timesten;
    private WithStatement withStatement;
    private String openBraceForSelectQuery;
    private String closeBraceForSelectQuery;
    private ArrayList ttUniqueIndexforUniqueCons;
    
    public void setOpenBraceForSelectQuery(final String openBrace) {
        this.openBraceForSelectQuery = openBrace;
    }
    
    public void setCloseBraceForSelectQuery(final String closeBrace) {
        this.closeBraceForSelectQuery = closeBrace;
    }
    
    public void setInsertQueryStatement(final InsertQueryStatement iqs) {
        this.insertQueryStatement = iqs;
    }
    
    public InsertQueryStatement getInsertQueryStatement() {
        return this.insertQueryStatement;
    }
    
    public CreateQueryStatement() {
        this.context = null;
        this.isTenroxRequirement = false;
        this.includeDrop = false;
        this.modifiedObjects = null;
        this.computedColumnsVector = new Vector();
        this.insertQueryStatement = null;
        this.selectColumnInNextClause = new SelectColumn();
        this.computedColView = null;
        this.comment_flag__for_create_as_select_in_timesten = false;
        this.withStatement = null;
        this.ttUniqueIndexforUniqueCons = new ArrayList();
    }
    
    public void setDropOption(final boolean b) {
        this.includeDrop = b;
    }
    
    public void setDropString(final String str) {
        this.dropString = str;
    }
    
    public void setDatabaseObject(final DatabaseObject object) {
        this.databaseObject = object;
    }
    
    public void setCreate(final String createString) {
        this.createString = createString;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
        this.commentObject = commentObject;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentObject;
    }
    
    public void setCreateOrReplace(final String createOrReplace) {
        this.createOrReplace = createOrReplace;
    }
    
    public void setMaterialized(final String materialized) {
        this.materialized = materialized;
    }
    
    public void setTableOrView(final String tableOrView) {
        this.tableOrView = tableOrView;
    }
    
    public void setAs(final String as) {
        this.as = as;
    }
    
    public void setForce(final String force) {
        this.force = force;
    }
    
    public void setTemp(final String temporary) {
        this.temporary = temporary;
    }
    
    public void setOnCondition(final String onCondition) {
        this.onCondition = onCondition;
    }
    
    public void setHeap(final String organisationHeap) {
        this.organisationHeap = organisationHeap;
    }
    
    public void setDiskAttributes(final String diskAttribute) {
        this.diskAttribute = diskAttribute;
    }
    
    public void setOpenBraces(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    public void setPhysicalCharacteristics(final String oraclePhysicalChar) {
        this.oraclePhysicalChar = oraclePhysicalChar;
    }
    
    public void setClosedBraces(final String closedBrace) {
        this.closedBrace = closedBrace;
    }
    
    public void setTableObject(final TableObject tableObject) {
        if (tableObject != null) {
            tableObject.setObjectContext(this.context);
        }
        this.tableObject = tableObject;
    }
    
    public void setSelectQueryStatement(final SelectQueryStatement selectQueryStatement) {
        this.selectQueryStatement = selectQueryStatement;
    }
    
    public void setWithCheckOption(final String withCheckOption) {
        this.withCheckOption = withCheckOption;
    }
    
    public void setColumnNames(final Vector columnNames) {
        this.columnNames = columnNames;
    }
    
    public void setCreateIndexClause(final CreateIndexClause createIndexClause) {
        this.createIndexClause = createIndexClause;
    }
    
    public void setCreateIndexVector(final Vector createIndexVector) {
        this.createIndexVector = createIndexVector;
    }
    
    public void setCreateIndexClauseBooleanValue(final boolean createIndexClauseBooleanValue) {
        this.createIndexClauseBooleanValue = createIndexClauseBooleanValue;
    }
    
    public void setTypeString(final String typeString) {
        this.typeString = typeString;
    }
    
    public void setTypeIdentifier(final String typeIdentifier) {
        this.typeIdentifier = typeIdentifier;
    }
    
    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }
    
    public void setConstraintName(final String constraintName) {
        this.constraintName = constraintName;
    }
    
    public void setCreateSequenceStatement(final CreateSequenceStatement createSequence) {
        this.createSequence = createSequence;
    }
    
    public void setCreateSequenceString(final String createSequenceStr) {
        this.createSequenceStr = createSequenceStr;
    }
    
    public void setCreateSynonymStatement(final CreateSynonymStatement createSynonym) {
        this.createSynonym = createSynonym;
    }
    
    public void setCreateSynonymString(final String createSynonymStr) {
        this.createSynonymStr = createSynonymStr;
    }
    
    public void setOnForQuotedIdentifier(final String onConditionString) {
        this.onConditionString = onConditionString;
    }
    
    public void setQuotedIdentifierCondition(final String quotedIdentifierCondition) {
        this.quotedIdentifierCondition = quotedIdentifierCondition;
    }
    
    public void setIndexString(final String indexString) {
        this.indexString = indexString;
    }
    
    public void setPhysicalAttributesVector(final Vector physicalAttributesVector) {
        this.physicalAttributesVector = physicalAttributesVector;
    }
    
    public void setIgnoreOrReplace(final String ignoreOrReplace) {
        this.ignoreOrReplace = ignoreOrReplace;
    }
    
    public void setSelectStatementString(final String selectStatementString) {
        this.selectStatementString = selectStatementString;
    }
    
    public void setDatatypeMapping(final DatatypeMapping mapping) {
        this.mapping = mapping;
    }
    
    public void setWithReadOnly(final String withReadOnly) {
        this.withReadOnly = withReadOnly;
    }
    
    public void setModifiedObject(final ModifiedObjectNames modifiedObjects) {
        this.modifiedObjects = modifiedObjects;
    }
    
    private void setTTUniqueIndicesForUniqCons(final ArrayList ttUniqueIndexforUniqueCons) {
        this.ttUniqueIndexforUniqueCons = ttUniqueIndexforUniqueCons;
    }
    
    public void setLock(final String lock) {
        this.lock = lock;
    }
    
    public void setLockData(final String lockData) {
        this.lockData = lockData;
    }
    
    public void setTriggerForIdentity(final String triggerForIdentity) {
        this.triggerForIdentity = triggerForIdentity;
    }
    
    public void setMysqlCommentTableOption(final String mysqlCommentTableOption) {
        this.mysqlCommentTableOption = mysqlCommentTableOption;
    }
    
    public void setDistributeOnRandomClause(final String distOnRandomClause) {
        this.distributeOnRandomClause = distOnRandomClause;
    }
    
    public void setExternalTable(final String externalTable) {
        this.externalTable = externalTable;
    }
    
    public void setExternalDefaultDirectory(final String directory) {
        this.externalDirectory = directory;
    }
    
    public void setExternalDelimiter(final String delimiter) {
        this.externalDelimiter = delimiter;
    }
    
    public void setComputedColumnView(final CreateQueryStatement computedColView) {
        this.computedColView = computedColView;
    }
    
    public void setStartWith(final String startWith) {
        this.startWith = startWith;
    }
    
    public void setStartWithFunction(final FunctionCalls fc) {
        this.startWithFunction = fc;
    }
    
    public void setNextString(final String next) {
        this.nextString = next;
    }
    
    public void setSelectColumnInNextClause(final SelectColumn sc) {
        this.selectColumnInNextClause = sc;
    }
    
    public void setViewMetaAttribute(final String viewMetaAttribute) {
        this.viewMetaAttribute = viewMetaAttribute;
    }
    
    public void setWithStatement(final WithStatement withStmt) {
        this.withStatement = withStmt;
    }
    
    public DatabaseObject getDatabaseObject() {
        return this.databaseObject;
    }
    
    public String getCreate() {
        return this.createString;
    }
    
    public String getCreateOrReplace() {
        return this.createOrReplace;
    }
    
    public String getTableOrView() {
        return this.tableOrView;
    }
    
    public String getAs() {
        return this.as;
    }
    
    public String getForce() {
        return this.force;
    }
    
    public String getTemp() {
        return this.temporary;
    }
    
    public String getOnCondition() {
        return this.onCondition;
    }
    
    public String getHeap() {
        return this.organisationHeap;
    }
    
    public String getDiskAttributes() {
        return this.diskAttribute;
    }
    
    public String getPhysicalCharacteristics() {
        return this.oraclePhysicalChar;
    }
    
    public TableObject getTableObject() {
        return this.tableObject;
    }
    
    public SelectQueryStatement getSelectQueryStatement() {
        return this.selectQueryStatement;
    }
    
    public String getWithCheckOption() {
        return this.withCheckOption;
    }
    
    public Vector getColumnNames() {
        return this.columnNames;
    }
    
    public String getConstraint() {
        return this.constraint;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    public CreateIndexClause getCreateIndexClause() {
        return this.createIndexClause;
    }
    
    public Vector getCreateIndexVector() {
        return this.createIndexVector;
    }
    
    public boolean getCreateIndexClauseBooleanValue() {
        return this.createIndexClauseBooleanValue;
    }
    
    public String getTypeString() {
        return this.typeString;
    }
    
    public String getTypeIdentifier() {
        return this.typeIdentifier;
    }
    
    public CreateSequenceStatement getCreateSequence() {
        return this.createSequence;
    }
    
    public String getCreateSequenceString() {
        return this.createSequenceStr;
    }
    
    public CreateSynonymStatement getCreateSynonym() {
        return this.createSynonym;
    }
    
    public String getCreateSynonymString() {
        return this.createSynonymStr;
    }
    
    public Vector getPhysicalAttributesVector() {
        return this.physicalAttributesVector;
    }
    
    public String getIgnoreOrReplace() {
        return this.ignoreOrReplace;
    }
    
    public String getSelectStatementString() {
        return this.selectStatementString;
    }
    
    public String getWithReadOnly() {
        return this.withReadOnly;
    }
    
    public ModifiedObjectNames getModifiedObject() {
        return this.modifiedObjects;
    }
    
    public String getLock() {
        return this.lock;
    }
    
    public String getLockData() {
        return this.lockData;
    }
    
    public String getDistributeOnRandomClause() {
        return this.distributeOnRandomClause;
    }
    
    public String getExternalTable() {
        return this.externalTable;
    }
    
    public String getExternalDefaultDirectory() {
        return this.externalDirectory;
    }
    
    public String getExternalDelimiter() {
        return this.externalDelimiter;
    }
    
    public String getMaterialized() {
        return this.materialized;
    }
    
    public String getStartWith() {
        return this.startWith;
    }
    
    public FunctionCalls getStartWithFunction() {
        return this.startWithFunction;
    }
    
    public String getNextString() {
        return this.nextString;
    }
    
    public SelectColumn getSelectColumnInNextClause() {
        return this.selectColumnInNextClause;
    }
    
    public String getViewMetaAttribute() {
        return this.viewMetaAttribute;
    }
    
    public WithStatement getWithStatement() {
        return this.withStatement;
    }
    
    @Override
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    public void computedColumns(final CreateQueryStatement cqs) {
        final CreateQueryStatement createStmt = new CreateQueryStatement();
        final SelectQueryStatement selectQuery = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        final SelectColumn selectColumn = new SelectColumn();
        final TableObject viewTableObject = new TableObject();
        final CreateColumn viewCreateColumn = new CreateColumn();
        selectColumn.setColumnExpression(cqs.getColumnNames());
        if (this.computedColumnsVector.size() > 0) {
            createStmt.setCreate("CREATE");
            createStmt.setTableOrView("VIEW");
            final String viewName = cqs.getTableObject() + "_" + "VIEW";
            viewTableObject.setTableName(viewName);
            createStmt.setAs("AS");
            createStmt.setTableObject(viewTableObject);
            selectStmt.setSelectClause("select");
            final Vector viewSelectColumnList = new Vector();
            for (int k = 0; k < cqs.getColumnNames().size(); ++k) {
                final CreateColumn computedCreateColumn = cqs.getColumnNames().get(k);
                final SelectColumn computedSelectColumn = new SelectColumn();
                final TableColumn computedTableColumn = new TableColumn();
                final Vector computedSelectColumnExpression = new Vector();
                final String computedSelectColumnName = computedCreateColumn.getColumnName();
                computedTableColumn.setColumnName(computedSelectColumnName);
                computedSelectColumnExpression.add(computedTableColumn);
                if (computedSelectColumnExpression.size() != 0) {
                    computedSelectColumn.setEndsWith(",");
                }
                computedSelectColumn.setColumnExpression(computedSelectColumnExpression);
                viewSelectColumnList.add(computedSelectColumn);
            }
            for (int k = 0; k < this.computedColumnsVector.size(); ++k) {
                final CreateColumn computedCreateColumn = this.computedColumnsVector.get(k);
                final SelectColumn computedSelectColumn = computedCreateColumn.getComputedColumnExpression();
                computedSelectColumn.setIsAS("as");
                computedSelectColumn.setAliasName(computedCreateColumn.getColumnName());
                if (this.computedColumnsVector.size() != k + 1) {
                    computedSelectColumn.setEndsWith(",");
                }
                viewSelectColumnList.add(computedSelectColumn);
            }
            selectStmt.setSelectItemList(viewSelectColumnList);
            selectQuery.setSelectStatement(selectStmt);
            final FromClause fromClauseObj = new FromClause();
            final FromTable fromTableObj = new FromTable();
            fromTableObj.setTableName(this.getTableObject().getTableName());
            final Vector fromItemList = new Vector();
            fromItemList.add(fromTableObj);
            fromClauseObj.setFromItemList(fromItemList);
            fromClauseObj.setFromClause("FROM");
            selectQuery.setFromClause(fromClauseObj);
        }
        createStmt.setSelectQueryStatement(selectQuery);
        cqs.setComputedColumnView(this.computedColView = createStmt);
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixCreate().toString();
    }
    
    public CreateQueryStatement toInformixCreate() throws ConvertException {
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        final CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setCreateOrReplace(null);
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setMaterialized(null);
        cqs.setWithReadOnly(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        cqs.setViewMetaAttribute(null);
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    changeCreateColumn.toInformixString();
                }
            }
        }
        if (cqs.getTableObject() != null) {
            final TableObject cqsTableObject = cqs.getTableObject();
            String ownerName = cqsTableObject.getOwner();
            String userName = cqsTableObject.getUser();
            String tableName = cqsTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            cqsTableObject.setOwner(ownerName);
            cqsTableObject.setUser(userName);
            cqsTableObject.setTableName(tableName);
            cqsTableObject.toInformix();
        }
        cqs.setTemp(null);
        cqs.setForce(null);
        cqs.setHeap(null);
        cqs.setPhysicalCharacteristics(null);
        cqs.setDiskAttributes(null);
        cqs.setOnCondition(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        if (this.getSelectQueryStatement() != null) {
            cqs.setSelectQueryStatement(this.selectQueryStatement.toInformixSelect());
        }
        if (this.getWithStatement() != null) {
            cqs.setWithStatement((WithStatement)this.withStatement.toInformix());
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toInformix());
        }
        if (cqs.getCreateIndexVector() != null) {
            final Vector temp_Vector = new Vector();
            for (int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(i);
                final CreateIndexClause informixIndexClause = createIndexClauseObject2.toInformix();
                temp_Vector.add(informixIndexClause);
            }
            cqs.setCreateIndexVector(temp_Vector);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toInformix();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toInformixString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        cqs.setPhysicalAttributesVector(null);
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        return cqs;
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSICreate().toString();
    }
    
    public CreateQueryStatement toANSICreate() throws ConvertException {
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        final CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setCreateOrReplace(null);
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setMaterialized(null);
        cqs.setViewMetaAttribute(null);
        cqs.setWithReadOnly(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    changeCreateColumn.toANSIString();
                    if (changeCreateColumn.getComputedColumnExpression() != null) {
                        this.computedColumnsVector.add(columnNamesVector.get(i));
                        columnNamesVector.removeElementAt(i);
                        --i;
                    }
                }
            }
        }
        if (cqs.getTemp() != null) {
            final String tempCQS = cqs.getTemp();
            if (tempCQS.equalsIgnoreCase("TEMP") || tempCQS.equalsIgnoreCase("TEMPORARY")) {
                cqs.setTemp("LOCAL TEMPORARY");
            }
        }
        if (cqs.getTableObject() != null) {
            final TableObject cqsTableObject = cqs.getTableObject();
            String ownerName = cqsTableObject.getOwner();
            String userName = cqsTableObject.getUser();
            String tableName = cqsTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            cqsTableObject.setOwner(ownerName);
            cqsTableObject.setUser(userName);
            cqsTableObject.setTableName(tableName);
            cqsTableObject.toANSISQL();
        }
        if (cqs.getOnCondition() != null) {
            cqs.onConditionForANSI = cqs.getOnCondition();
            cqs.setOnCondition(null);
        }
        cqs.setForce(null);
        cqs.setHeap(null);
        cqs.setDiskAttributes(null);
        cqs.setPhysicalCharacteristics(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        if (this.getSelectQueryStatement() != null) {
            cqs.setSelectQueryStatement(this.selectQueryStatement.toANSISelect());
        }
        if (this.getWithStatement() != null) {
            cqs.setWithStatement((WithStatement)this.withStatement.toANSISQL());
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toANSI());
        }
        if (cqs.getCreateIndexVector() != null) {
            String indexString = "";
            final Vector temp_Vector = new Vector();
            for (int j = 0; j < cqs.getCreateIndexVector().size(); ++j) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(j);
                if (!this.existsPKasIndex(createIndexClauseObject2, cqs)) {
                    createIndexClauseObject2.setIndexOrKey("CREATE INDEX");
                    createIndexClauseObject2.setOn("ON");
                    createIndexClauseObject2.setTableOrView(this.tableObject);
                    final CreateIndexClause ansiIndexClause = createIndexClauseObject2.toANSI();
                    indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
                }
            }
            cqs.setIndexString(indexString);
            cqs.setCreateIndexVector(null);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toANSI();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toANSIString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        cqs.setPhysicalAttributesVector(null);
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        if (this.computedColumnsVector.size() >= 1) {
            this.computedColumns(cqs);
        }
        return cqs;
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2Create().toString();
    }
    
    public CreateQueryStatement toDB2Create() throws ConvertException {
        if (SwisSQLAPI.truncateTableCount > 99) {
            SwisSQLAPI.truncateTableCount = 0;
        }
        if (SwisSQLAPI.truncateIndexCount > 99) {
            SwisSQLAPI.truncateIndexCount = 0;
        }
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        final CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setCreateOrReplace(null);
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setMaterialized(null);
        cqs.setViewMetaAttribute(null);
        cqs.setWithReadOnly(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            final Vector storeAllColumnNamesVector = new Vector();
            final TableObject to = cqs.getTableObject();
            if (to != null) {
                final String tableName = to.getTableName().toLowerCase().trim();
                if (SwisSQLOptions.addRowidColumnForAllDB2Tables && tableName != null) {
                    try {
                        final FileOutputStream fos = new FileOutputStream("conf/TablesHavingRowIdColumns.conf", true);
                        final OutputStreamWriter osw = new OutputStreamWriter(fos);
                        final PrintWriter pw = new PrintWriter(osw);
                        pw.println(tableName);
                        final CreateColumn cc = new CreateColumn();
                        cc.setColumnName("RowId");
                        final CharacterClass charClass = new CharacterClass();
                        charClass.setDatatypeName("VARCHAR");
                        charClass.setOpenBrace("(");
                        charClass.setClosedBrace(")");
                        charClass.setSize("13");
                        charClass.setBinary("FOR BIT DATA");
                        cc.setDatatype(charClass);
                        columnNamesVector.add(0, cc);
                        if (!InsertQueryStatement.tablesWithRowIDColumnsList.contains(tableName)) {
                            InsertQueryStatement.tablesWithRowIDColumnsList.add(tableName);
                        }
                        pw.close();
                        osw.close();
                        fos.close();
                    }
                    catch (final Exception ex) {}
                }
            }
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    storeAllColumnNamesVector.add(changeCreateColumn.getColumnName());
                    final Vector constraintVector = changeCreateColumn.getConstraintClause();
                    boolean isTableLevelConstraintRemoved = false;
                    isTableLevelConstraintRemoved = this.removeCheckConstrWithDetFunctions(constraintVector, changeCreateColumn, columnNamesVector, i);
                    if (changeCreateColumn.getColumnName() == null && constraintVector != null) {
                        final ConstraintClause db2ConstraintClause = constraintVector.get(0);
                        final ConstraintType db2ConstraintType = db2ConstraintClause.getConstraintType();
                        if (db2ConstraintType != null) {
                            if (db2ConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                final PrimaryOrUniqueConstraintClause db2PrimaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)db2ConstraintType;
                                if (db2PrimaryOrUniqueConstraintClause.getConstraintColumnNames() != null) {
                                    final Vector constraintColumnNamesVector = db2PrimaryOrUniqueConstraintClause.getConstraintColumnNames();
                                    for (int i_count = 0; i_count < constraintColumnNamesVector.size(); ++i_count) {
                                        final String constraintColumnName = db2PrimaryOrUniqueConstraintClause.getConstraintColumnNames().get(i_count);
                                        if (storeAllColumnNamesVector.contains(constraintColumnName) || storeAllColumnNamesVector.contains("[" + constraintColumnName + "]") || storeAllColumnNamesVector.contains("`" + constraintColumnName + "`")) {
                                            int indexOfColumnName = storeAllColumnNamesVector.indexOf(constraintColumnName);
                                            if (indexOfColumnName == -1) {
                                                indexOfColumnName = storeAllColumnNamesVector.indexOf("[" + constraintColumnName + "]");
                                            }
                                            if (indexOfColumnName == -1) {
                                                indexOfColumnName = storeAllColumnNamesVector.indexOf("`" + constraintColumnName + "`");
                                            }
                                            final CreateColumn createColumnWithConstraint = columnNamesVector.get(indexOfColumnName);
                                            createColumnWithConstraint.setNullStatus("NOT NULL");
                                            changeCreateColumn.setNotNullSetFromCreateQueryStatement(true);
                                            changeCreateColumn.toDB2String();
                                        }
                                    }
                                }
                            }
                            else {
                                changeCreateColumn.toDB2String();
                            }
                        }
                    }
                    else {
                        changeCreateColumn.toDB2String();
                    }
                    if (isTableLevelConstraintRemoved) {
                        --i;
                    }
                }
            }
        }
        if (cqs.getTableObject() != null) {
            final TableObject cqsTableObject = cqs.getTableObject();
            String ownerName = cqsTableObject.getOwner();
            String userName = cqsTableObject.getUser();
            String tableName = cqsTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            cqsTableObject.setOwner(ownerName);
            cqsTableObject.setUser(userName);
            cqsTableObject.setTableName(tableName);
            cqsTableObject.toDB2();
        }
        cqs.setTemp(null);
        cqs.setForce(null);
        cqs.setHeap(null);
        cqs.setPhysicalCharacteristics(null);
        cqs.setDiskAttributes(null);
        cqs.setOnCondition(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        if (this.getSelectQueryStatement() != null) {
            final SelectQueryStatement db2_sqs = this.selectQueryStatement.toDB2Select();
            if (this.tableOrView.trim().equalsIgnoreCase("VIEW")) {
                final FromClause fc = db2_sqs.getFromClause();
                fc.setFetchClauseFromSQS(null);
                if (db2_sqs.getSetOperatorClause() != null) {
                    final SetOperatorClause soc = db2_sqs.getSetOperatorClause();
                    final SelectQueryStatement subSQS = soc.getSelectQueryStatement();
                    this.removeFetchClause(subSQS);
                    this.checkForColumnNames(db2_sqs, subSQS);
                }
            }
            cqs.setSelectQueryStatement(db2_sqs);
        }
        if (this.getWithStatement() != null) {
            cqs.setWithStatement((WithStatement)this.withStatement.toDB2());
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toDB2());
        }
        if (cqs.getCreateIndexVector() != null) {
            String indexString = "";
            final Vector temp_Vector = new Vector();
            for (int j = 0; j < cqs.getCreateIndexVector().size(); ++j) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(j);
                if (!this.existsPKasIndex(createIndexClauseObject2, cqs)) {
                    createIndexClauseObject2.setIndexOrKey("CREATE INDEX");
                    createIndexClauseObject2.setOn("ON");
                    createIndexClauseObject2.setTableOrView(this.tableObject);
                    final CreateIndexClause ansiIndexClause = createIndexClauseObject2.toDB2();
                    indexString = indexString + "\n@\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
                }
            }
            cqs.setIndexString(indexString);
            cqs.setCreateIndexVector(null);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toDB2();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toDB2String();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        cqs.setPhysicalAttributesVector(null);
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        return cqs;
    }
    
    private void removeFetchClause(final SelectQueryStatement sqs) {
        if (sqs != null) {
            final SetOperatorClause soc = sqs.getSetOperatorClause();
            if (soc != null) {
                final SelectQueryStatement subSQS = soc.getSelectQueryStatement();
                this.removeFetchClause(subSQS);
            }
            final FromClause fc = sqs.getFromClause();
            fc.setFetchClauseFromSQS(null);
        }
    }
    
    private void checkForColumnNames(final SelectQueryStatement fullSQS, final SelectQueryStatement subSQS) {
        if (subSQS != null) {
            final SelectStatement fullSS = fullSQS.getSelectStatement();
            final SelectStatement subSS = subSQS.getSelectStatement();
            final Vector fullSItem = fullSS.getSelectItemList();
            final Vector subSItem = subSS.getSelectItemList();
            for (int i = 0; i < fullSItem.size(); ++i) {
                final Object fullObj = fullSItem.get(i);
                final Object subObj = subSItem.get(i);
                if (fullObj instanceof SelectColumn && subObj instanceof SelectColumn) {
                    final SelectColumn fullSC = (SelectColumn)fullObj;
                    final SelectColumn subSC = (SelectColumn)subObj;
                    final Vector fullColExpr = fullSC.getColumnExpression();
                    final Vector subColExpr = subSC.getColumnExpression();
                    final String fullAlias = fullSC.getAliasName();
                    final String subAlias = subSC.getAliasName();
                    if (fullAlias != null && subAlias == null) {
                        subSC.setAliasName(fullAlias);
                    }
                    else if (fullAlias == null && subAlias != null) {
                        fullSC.setAliasName(subAlias);
                    }
                    else if (fullAlias != null && subAlias != null) {
                        if (!fullAlias.equalsIgnoreCase(subAlias)) {
                            subSC.setAliasName(fullAlias);
                        }
                    }
                    else if (fullColExpr.size() == 1 && subColExpr.size() == 1) {
                        final Object fullObj2 = fullColExpr.get(0);
                        final Object subObj2 = subColExpr.get(0);
                        TableColumn fullTC = null;
                        TableColumn subTC = null;
                        if (fullObj2 instanceof TableColumn && subObj2 instanceof TableColumn) {
                            fullTC = (TableColumn)fullObj2;
                            subTC = (TableColumn)subObj2;
                            if (!fullTC.getColumnName().equalsIgnoreCase(subTC.getColumnName())) {
                                fullSC.setAliasName(fullTC.getColumnName());
                                subSC.setAliasName(fullTC.getColumnName());
                            }
                        }
                        else if (fullObj2 instanceof TableColumn) {
                            fullTC = (TableColumn)fullObj2;
                            subSC.setAliasName(fullTC.getColumnName());
                        }
                        else if (subObj2 instanceof TableColumn) {
                            subTC = (TableColumn)subObj2;
                            fullSC.setAliasName(subTC.getColumnName());
                        }
                    }
                }
            }
        }
    }
    
    private CheckConstraintClause isCheckConstraint(final ConstraintClause cclause) {
        final ConstraintType ctype = cclause.getConstraintType();
        if (ctype != null && ctype instanceof CheckConstraintClause) {
            return (CheckConstraintClause)ctype;
        }
        return null;
    }
    
    private String getCheckConstraintClauseName(final Vector v) {
        for (int i = 0; i < v.size(); ++i) {
            final ConstraintClause cclause = v.elementAt(i);
            final CheckConstraintClause ccc = this.isCheckConstraint(cclause);
            if (ccc != null) {
                final WhereExpression we = ccc.getWhereExpression();
                final Vector witemV = we.getWhereItems();
                if (witemV != null) {
                    return this.leftWhereColumn(witemV.get(0));
                }
            }
        }
        return "";
    }
    
    private String leftWhereColumn(final Object obj) {
        if (obj instanceof WhereExpression) {
            final WhereExpression we = (WhereExpression)obj;
            final Vector wis = we.getWhereItems();
            if (wis != null) {
                return this.leftWhereColumn(wis.get(0));
            }
        }
        else if (obj instanceof WhereItem) {
            final WhereItem wi = (WhereItem)obj;
            return wi.getLeftWhereExp().toString();
        }
        return "";
    }
    
    private boolean containsInVector(String str, final Vector v) {
        if (str == null || str.equalsIgnoreCase("") || v == null || v.isEmpty()) {
            return false;
        }
        str = str.trim();
        if (str.startsWith("\"") || str.startsWith("'")) {
            str = str.substring(1, str.length() - 1);
        }
        for (int i = 0; i < v.size(); ++i) {
            String s = v.elementAt(i);
            s = s.trim();
            if (s.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }
    
    public void setDeltekIdentityString(final String tab_col, final CreateColumn changeCreateColumn) {
        final String identity = SwisSQLAPI.identityMapping.get(tab_col).toString();
        if (identity != null && changeCreateColumn != null) {
            changeCreateColumn.setIdentity(identity);
        }
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServerCreate().toString();
    }
    
    public CreateQueryStatement toMSSQLServerCreate() throws ConvertException {
        final CreateQueryStatement cqs = this.copyObjectValues();
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        cqs.setForce(null);
        cqs.setDropOption(this.includeDrop);
        cqs.setDropString(this.dropString);
        cqs.setPhysicalCharacteristics(null);
        cqs.setCreateOrReplace(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        cqs.setViewMetaAttribute(this.viewMetaAttribute);
        cqs.setWithReadOnly(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        if (cqs.getOnCondition() != null && !cqs.getOnCondition().startsWith("ON DEFAULT")) {
            cqs.setOnCondition(null);
        }
        if (cqs.materialized != null) {
            cqs.withSchemaBinding = "WITH SCHEMABINDING";
        }
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            final Vector vec = new Vector();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    if (changeCreateColumn != null && changeCreateColumn.getColumnName() != null) {
                        final String tab_col = this.tableObject.getTableName().trim() + "." + changeCreateColumn.getColumnName().trim();
                        if (SwisSQLAPI.identityMapping.containsKey(tab_col)) {
                            this.setDeltekIdentityString(tab_col, changeCreateColumn);
                        }
                    }
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        if (SwisSQLOptions.changeDatatype_For_Deltek && changeCreateColumn.getColumnName().toUpperCase().trim().endsWith("_DT") && changeCreateColumn.getDatatype() instanceof DateClass) {
                            final DateClass datatype = (DateClass)changeCreateColumn.getDatatype();
                            if (datatype.getDatatypeName().trim().equalsIgnoreCase("DATE")) {
                                datatype.setDatatypeName("SMALLDATETIME");
                            }
                        }
                        else if (SwisSQLOptions.changeDatatype_For_Deltek && changeCreateColumn.getColumnName().toUpperCase().trim().endsWith("_DTT") && changeCreateColumn.getDatatype() instanceof DateClass) {
                            final DateClass datatype = (DateClass)changeCreateColumn.getDatatype();
                            if (datatype.getDatatypeName().trim().equalsIgnoreCase("DATE")) {
                                datatype.setDatatypeName("DATETIME");
                            }
                        }
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    final String current_col_name = changeCreateColumn.getColumnName();
                    if (changeCreateColumn.getDatatype() != null && (changeCreateColumn.getDatatype().toString().trim().equalsIgnoreCase("CLOB") || changeCreateColumn.getDatatype().toString().trim().equalsIgnoreCase("BLOB"))) {
                        vec.addElement(current_col_name);
                        final Vector consVr = changeCreateColumn.getConstraintClause();
                        if (consVr != null) {
                            for (int j = 0; j < consVr.size(); ++j) {
                                final Object check = consVr.elementAt(j);
                                if (check instanceof ConstraintClause && this.isCheckConstraint((ConstraintClause)check) != null) {
                                    consVr.removeElementAt(j);
                                }
                            }
                        }
                    }
                    if (SwisSQLOptions.EnableDeltekSpecificConversions && changeCreateColumn.getColumnName() != null && changeCreateColumn.getColumnName().trim().equalsIgnoreCase("ID_COL")) {
                        changeCreateColumn.setIdentity("IDENTITY");
                        if (changeCreateColumn.getDatatype() != null && changeCreateColumn.getDatatype().getDatatypeName().trim().equalsIgnoreCase("DECIMAL")) {
                            changeCreateColumn.getDatatype().setDatatypeName("NUMERIC");
                        }
                    }
                    changeCreateColumn.toMSSQLServerString();
                }
            }
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                final CreateColumn changeCreateColumn2 = columnNamesVector.get(i);
                if (changeCreateColumn2 != null && changeCreateColumn2.getColumnName() == null) {
                    final Vector constraintVector = changeCreateColumn2.getConstraintClause();
                    final String current_check_clause_name = this.getCheckConstraintClauseName(constraintVector);
                    if (constraintVector != null && this.containsInVector(current_check_clause_name, vec)) {
                        columnNamesVector.remove(changeCreateColumn2);
                        --i;
                    }
                }
            }
            if (cqs.getDiskAttributes() != null) {
                final Vector ColumnNamesVector = cqs.getColumnNames();
                for (int k = 0; k < columnNamesVector.size(); ++k) {
                    if (columnNamesVector.get(k) instanceof CreateColumn) {
                        final CreateColumn changeCreateColumn3 = columnNamesVector.get(k);
                        final Vector getConstraintVector = changeCreateColumn3.getConstraintClause();
                        if (getConstraintVector != null) {
                            for (int j = 0; j < getConstraintVector.size(); ++j) {
                                final ConstraintClause toSQLServerConstraintClause = getConstraintVector.get(j);
                                final ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                                if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                    final PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                                    changeCreateColumn3.setNotNullSetFromCreateQueryStatement(true);
                                    String fillfactor = cqs.getDiskAttributes();
                                    String tempFillfactor = "";
                                    if (fillfactor != null) {
                                        tempFillfactor = fillfactor;
                                        tempFillfactor = tempFillfactor.substring(0, 7);
                                        tempFillfactor = fillfactor.toUpperCase();
                                        int fillIntValue = 0;
                                        if (tempFillfactor.startsWith("PCTFREE")) {
                                            fillfactor = fillfactor.substring(8);
                                        }
                                        else if (tempFillfactor.startsWith("PCTUSED")) {
                                            fillfactor = fillfactor.substring(8);
                                            fillIntValue = Integer.parseInt(fillfactor);
                                            fillIntValue = 100 - fillIntValue;
                                            fillfactor = "" + fillIntValue;
                                        }
                                        pcc.setWith("WITH");
                                        final HashMap diskAttr = new HashMap();
                                        diskAttr.put("FILLFACTOR", new String(fillIntValue + ""));
                                        pcc.setDiskAttr(diskAttr);
                                        cqs.setHeap(null);
                                        cqs.setDiskAttributes(null);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (cqs.getTableObject() != null) {
            final TableObject orgTableObject = cqs.getTableObject();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            String tableName = orgTableObject.getTableName();
            if (this.tableOrView != null && this.includeDrop) {
                if (this.tableOrView.equalsIgnoreCase("TABLE")) {
                    this.dropString = "--SwisSQL DROP SCRIPTS\nIF EXISTS ( SELECT name from sysobjects where name='" + tableName + "' AND type='U')\n DROP TABLE " + tableName + "\nGO\n";
                }
                else {
                    this.dropString = "--SwisSQL DROP SCRIPTS\nIF EXISTS ( SELECT name from sysobjects where name='" + tableName + "' AND type='V')\n DROP VIEW " + tableName + "\nGO\n";
                }
                cqs.setDropString(this.dropString);
            }
            if (ownerName != null && ownerName.startsWith("`") && ownerName.endsWith("`")) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && userName.startsWith("`") && userName.endsWith("`")) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && tableName.startsWith("`") && tableName.endsWith("`")) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (cqs.getTemp() != null && cqs.getTemp().equalsIgnoreCase("GLOBAL TEMPORARY")) {
                tableName = "##" + tableName;
            }
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            orgTableObject.setTableName(tableName);
            orgTableObject.toMSSQLServer();
        }
        cqs.setForce(null);
        cqs.setTemp(null);
        if (this.getSelectQueryStatement() != null && cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("TABLE")) {
            cqs.setCreate(null);
            cqs.setAs(null);
            final SelectQueryStatement sqs = cqs.getSelectQueryStatement();
            final IntoStatement is = new IntoStatement();
            is.setIntoClause("INTO");
            is.setTableOrFileName(cqs.getTableObject().toString());
            sqs.setIntoStatement(is);
            cqs.setTableObject(null);
            cqs.setTableObject(null);
            cqs.setTableOrView(null);
            cqs.setSelectQueryStatement(sqs.toMSSQLServerSelect());
        }
        else {
            final SelectQueryStatement sqs = cqs.getSelectQueryStatement();
            if (sqs != null) {
                final FromClause fc = sqs.getFromClause();
                if (fc != null) {
                    final Vector fromItems = fc.getFromItemList();
                    for (int k = 0; k < fromItems.size(); ++k) {
                        Object obj = fromItems.get(k);
                        if (obj instanceof FromTable) {
                            final FromTable ft = (FromTable)obj;
                            obj = ft.getTableName();
                            if (obj instanceof String) {
                                String fromTableName = (String)obj;
                                final int index = fromTableName.indexOf(".");
                                if (index != -1 && fromTableName.toLowerCase().indexOf("dbo.") == -1) {
                                    fromTableName = fromTableName.substring(index + 1, fromTableName.length());
                                }
                                if (this.materialized != null) {
                                    ft.setTableName("dbo." + fromTableName);
                                }
                                else {
                                    ft.setTableName(fromTableName);
                                }
                            }
                        }
                    }
                }
                cqs.setSelectQueryStatement(sqs.toMSSQLServerSelect());
            }
        }
        if (this.getWithStatement() != null) {
            cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toMSSQLServer());
            cqs.setWithStatement(null);
        }
        if (cqs.getTypeIdentifier() != null) {
            final String tempTypeIdentifier = cqs.getTypeIdentifier();
            if (cqs.getTypeString() != null && cqs.getTypeString().equalsIgnoreCase("comment")) {
                cqs.setMysqlCommentTableOption(this.mysqlCommentTableOption = "/*" + cqs.getTypeString() + tempTypeIdentifier + "*/");
            }
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toMSSQLServer());
        }
        if (cqs.getCreateIndexVector() != null) {
            String indexString = "";
            final Vector temp_Vector = new Vector();
            for (int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(i);
                if (!this.existsPKasIndex(createIndexClauseObject2, cqs)) {
                    createIndexClauseObject2.setIndexOrKey("CREATE INDEX");
                    createIndexClauseObject2.setOn("ON");
                    createIndexClauseObject2.setTableOrView(this.tableObject);
                    final CreateIndexClause ansiIndexClause = createIndexClauseObject2.toMSSQLServer();
                    indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
                }
            }
            cqs.setIndexString(indexString);
            cqs.setCreateIndexVector(null);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toMSSQLServer();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toMSSQLServerString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        cqs.setPhysicalAttributesVector(null);
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        if (this.materialized != null) {
            final String viewName = cqs.getTableObject().getTableName();
            final SelectQueryStatement sqs2 = cqs.getSelectQueryStatement();
            ArrayList columnList = null;
            String columnName = "";
            if (sqs2 != null) {
                final FromClause matlFC = sqs2.getFromClause();
                if (matlFC != null) {
                    final Vector matlFromItems = matlFC.getFromItemList();
                    for (int n = 0; n < matlFromItems.size(); ++n) {
                        Object obj2 = matlFromItems.get(n);
                        if (obj2 instanceof FromTable) {
                            final FromTable matlFromTable = (FromTable)obj2;
                            obj2 = matlFromTable.getTableName();
                            if (obj2 instanceof String) {
                                String matlTableName = (String)obj2;
                                final int matlIndex = matlTableName.lastIndexOf(".");
                                if (matlIndex != -1) {
                                    matlTableName = matlTableName.substring(matlIndex + 1, matlTableName.length());
                                }
                                if (SwisSQLAPI.primaryKeyMetaData.get(matlTableName.toUpperCase()) != null) {
                                    columnList = SwisSQLAPI.primaryKeyMetaData.get(matlTableName.toUpperCase());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (sqs2 != null && columnList == null) {
                final SelectStatement ss = sqs2.getSelectStatement();
                if (ss != null) {
                    final Vector selectItems = ss.getSelectItemList();
                    for (int l = 0; l < selectItems.size(); ++l) {
                        Object obj2 = selectItems.get(l);
                        if (obj2 instanceof SelectColumn) {
                            final Vector colExpr = ((SelectColumn)obj2).getColumnExpression();
                            if (colExpr.size() == 1) {
                                obj2 = colExpr.get(0);
                                if (obj2 instanceof TableColumn) {
                                    columnName = ((TableColumn)obj2).getColumnName();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (columnList != null) {
                for (int m = 0; m < columnList.size(); ++m) {
                    if (!columnName.equals("")) {
                        columnName = columnName + "," + columnList.get(m);
                    }
                    else {
                        columnName = columnList.get(0);
                    }
                }
            }
            cqs.indexedViewStmt = "CREATE UNIQUE CLUSTERED INDEX " + viewName + "index_ADV ON " + viewName + "(" + columnName + ")";
            cqs.setMaterialized(null);
        }
        return cqs;
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseCreate().toString();
    }
    
    public CreateQueryStatement toSybaseCreate() throws ConvertException {
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        final CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setTemp(null);
        cqs.setForce(null);
        cqs.setPhysicalCharacteristics(null);
        cqs.setCreateOrReplace(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setMaterialized(null);
        cqs.setViewMetaAttribute(null);
        cqs.setWithReadOnly(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        if (cqs.getOnCondition() != null && !cqs.getOnCondition().startsWith("ON DEFAULT")) {
            cqs.setOnCondition(null);
        }
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    changeCreateColumn.toSybaseString();
                }
            }
            this.removeUniqueKeyConstrOnPKExists(columnNamesVector);
            if (cqs.getDiskAttributes() != null) {
                final Vector ColumnNamesVector = cqs.getColumnNames();
                for (int j = 0; j < columnNamesVector.size(); ++j) {
                    if (columnNamesVector.get(j) instanceof CreateColumn) {
                        final CreateColumn changeCreateColumn2 = columnNamesVector.get(j);
                        final Vector getConstraintVector = changeCreateColumn2.getConstraintClause();
                        if (getConstraintVector != null) {
                            for (int k = 0; k < getConstraintVector.size(); ++k) {
                                final ConstraintClause toSQLServerConstraintClause = getConstraintVector.get(k);
                                final ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                                if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                    final PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                                    changeCreateColumn2.setNotNullSetFromCreateQueryStatement(true);
                                    String fillfactor = cqs.getDiskAttributes();
                                    String tempFillfactor = "";
                                    if (fillfactor != null) {
                                        tempFillfactor = fillfactor;
                                        tempFillfactor = tempFillfactor.substring(0, 7);
                                        tempFillfactor = fillfactor.toUpperCase();
                                        int fillIntValue = 0;
                                        if (tempFillfactor.startsWith("PCTFREE")) {
                                            fillfactor = fillfactor.substring(8);
                                        }
                                        else if (tempFillfactor.startsWith("PCTUSED")) {
                                            fillfactor = fillfactor.substring(8);
                                            fillIntValue = Integer.parseInt(fillfactor);
                                            fillIntValue = 100 - fillIntValue;
                                            fillfactor = "" + fillIntValue;
                                        }
                                        pcc.setWith("WITH");
                                        final HashMap diskAttr = new HashMap();
                                        diskAttr.put("FILLFACTOR", new String(fillIntValue + ""));
                                        pcc.setDiskAttr(diskAttr);
                                        cqs.setHeap(null);
                                        cqs.setDiskAttributes(null);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (cqs.getTableObject() != null) {
            final TableObject orgTableObject = cqs.getTableObject();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            String tableName = orgTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            orgTableObject.setOwner(ownerName);
            orgTableObject.setUser(userName);
            orgTableObject.setTableName(tableName);
            orgTableObject.toSybase();
        }
        if (cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("VIEW")) {
            cqs.getTableObject().setDot(".");
        }
        cqs.setForce(null);
        if (this.getSelectQueryStatement() != null) {
            cqs.setSelectQueryStatement(this.selectQueryStatement.toSybaseSelect());
        }
        if (this.getWithStatement() != null) {
            cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toSybase());
            cqs.setWithStatement(null);
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toSybase());
        }
        if (cqs.getCreateIndexVector() != null) {
            String indexString = "";
            final Vector temp_Vector = new Vector();
            for (int j = 0; j < cqs.getCreateIndexVector().size(); ++j) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(j);
                if (!this.existsPKasIndex(createIndexClauseObject2, cqs)) {
                    createIndexClauseObject2.setIndexOrKey("CREATE INDEX");
                    createIndexClauseObject2.setOn("ON");
                    createIndexClauseObject2.setTableOrView(this.tableObject);
                    final CreateIndexClause ansiIndexClause = createIndexClauseObject2.toSybase();
                    indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
                }
            }
            cqs.setIndexString(indexString);
            cqs.setCreateIndexVector(null);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toSybase();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toSybaseString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        if (cqs.getPhysicalAttributesVector() != null) {
            final Vector tempVector = new Vector();
            for (int i = 0; i < cqs.getPhysicalAttributesVector().size(); ++i) {
                final PhysicalAttributesClause tempPhysicalAttributesClause = cqs.getPhysicalAttributesVector().get(i);
                tempPhysicalAttributesClause.setFillFactor(null);
                tempPhysicalAttributesClause.setFillFactorValue(null);
                tempVector.add(tempPhysicalAttributesClause.toSybase());
            }
            cqs.setPhysicalAttributesVector(tempVector);
        }
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        return cqs;
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        final CreateQueryStatement cqs = this.toMySQLCreate();
        if (cqs.getDatabaseObject() == null) {
            return cqs.toString();
        }
        if (cqs.getDatabaseObject().getSize() != null) {
            return "CREATE DATABASE " + cqs.getDatabaseObject().getName() + " SIZE '" + cqs.getDatabaseObject().getSize();
        }
        return "CREATE DATABASE " + cqs.getDatabaseObject().getName();
    }
    
    public CreateQueryStatement toMySQLCreate() throws ConvertException {
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        final CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setForce(null);
        cqs.setHeap(null);
        cqs.setDiskAttributes(null);
        cqs.setPhysicalCharacteristics(null);
        cqs.setCreateOrReplace(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setMaterialized(null);
        cqs.setViewMetaAttribute(null);
        cqs.setWithReadOnly(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        if (cqs.getDatabaseObject() != null) {
            return cqs;
        }
        if (cqs.getTemp() != null) {
            final String tempCQS = cqs.getTemp();
            if (tempCQS.equalsIgnoreCase("GLOBAL TEMPORARY") || tempCQS.equalsIgnoreCase("LOCAL TEMPORARY") || tempCQS.equalsIgnoreCase("TEMP")) {
                cqs.setTemp("TEMPORARY");
            }
        }
        if (cqs.getTableObject() != null) {
            final TableObject cqsTableObject = cqs.getTableObject();
            String ownerName = cqsTableObject.getOwner();
            String userName = cqsTableObject.getUser();
            String tableName = cqsTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("\"") && ownerName.endsWith("\"")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "`" + ownerName + "`";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("\"") && userName.endsWith("\"")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "`" + userName + "`";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("\"") && tableName.endsWith("\"")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "`" + tableName + "`";
                }
            }
            cqsTableObject.setOwner(ownerName);
            cqsTableObject.setUser(userName);
            cqsTableObject.setTableName(tableName);
            cqsTableObject.toMySQL();
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            final CreateIndexClause mySQLIndexClause = createIndexClauseObject.toMySQL();
            cqs.setCreateIndexClause(mySQLIndexClause);
        }
        if (cqs.getCreateIndexVector() != null) {
            final Vector tempVector = new Vector();
            for (int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(i);
                tempVector.add(createIndexClauseObject2.toMySQL());
            }
            cqs.setCreateIndexVector(tempVector);
        }
        if (cqs.getOnCondition() != null) {
            cqs.setOnCondition(null);
        }
        final Map map = new HashMap();
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            for (int j = 0; j < columnNamesVector.size(); ++j) {
                final Object column = columnNamesVector.get(j);
                if (column instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = (CreateColumn)column;
                    final Datatype dt = changeCreateColumn.getDatatype();
                    String sizeStr = null;
                    int size = 0;
                    if (dt instanceof CharacterClass) {
                        final String datatypeName = dt.getDatatypeName();
                        if (datatypeName.equalsIgnoreCase("VARCHAR") || datatypeName.equalsIgnoreCase("CHAR") || datatypeName.equalsIgnoreCase("NCHAR") || datatypeName.equalsIgnoreCase("NVARCHAR") || datatypeName.equalsIgnoreCase("TEXT")) {
                            sizeStr = dt.getSize();
                            if (sizeStr != null) {
                                try {
                                    size = Integer.parseInt(sizeStr);
                                }
                                catch (final NumberFormatException ex) {}
                            }
                        }
                    }
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    changeCreateColumn.toMySQLString();
                    String columnName = changeCreateColumn.getColumnName();
                    if (columnName != null) {
                        final Datatype dt2 = changeCreateColumn.getDatatype();
                        if (dt2 instanceof CharacterClass) {
                            final String datatypeName2 = dt2.getDatatypeName();
                            if (datatypeName2.equalsIgnoreCase("TEXT") && size > 255) {
                                columnName = changeCreateColumn.getColumnName();
                                map.put(columnName, sizeStr);
                            }
                        }
                    }
                    if (columnName == null) {
                        final Vector constraintList = changeCreateColumn.getConstraintClause();
                        if (constraintList != null) {
                            for (int z = constraintList.size(), k = 0; k < z; ++k) {
                                final Object o = constraintList.get(k);
                                if (o instanceof ConstraintClause) {
                                    final ConstraintClause clause = (ConstraintClause)o;
                                    final ConstraintType type = clause.getConstraintType();
                                    if (type instanceof PrimaryOrUniqueConstraintClause) {
                                        final PrimaryOrUniqueConstraintClause pk = (PrimaryOrUniqueConstraintClause)type;
                                        final Vector constraintColumnNames = pk.getConstraintColumnNames();
                                        for (int g = constraintColumnNames.size(), l = 0; l < g; ++l) {
                                            final String ccn = constraintColumnNames.get(l);
                                            final String siz = map.get(ccn);
                                            if (siz != null) {
                                                pk.addToColumnNameVsSize(ccn, siz);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.getSelectQueryStatement() != null) {
            cqs.setSelectQueryStatement(this.selectQueryStatement.toMySQLSelect());
        }
        if (this.getWithStatement() != null) {
            cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toMySQL());
            cqs.setWithStatement(null);
        }
        if (cqs.getTypeString() != null) {
            cqs.getTypeString();
        }
        if (cqs.getTypeIdentifier() != null) {
            cqs.getTypeIdentifier();
        }
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toMySQL();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toMySQLString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        cqs.setPhysicalAttributesVector(null);
        return cqs;
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        final CreateQueryStatement cqs = this.toOracleCreate();
        if (cqs.getDatabaseObject() == null) {
            return cqs.toString();
        }
        if (cqs.getDatabaseObject().getSize() != null) {
            return "CREATE TABLESPACE " + cqs.getDatabaseObject().getName() + " LOGGING DATAFILE '" + cqs.getDatabaseObject().getName() + ".dbf' SIZE " + cqs.getDatabaseObject().getSize() + "M RESUSE AUTOEXTEND ON MAXSIZE UNLIMITED";
        }
        return "CREATE TABLESPACE " + cqs.getDatabaseObject().getName() + " LOGGING DATAFILE '" + cqs.getDatabaseObject().getName() + ".dbf' SIZE " + "5M RESUSE AUTOEXTEND ON MAXSIZE UNLIMITED";
    }
    
    public CreateQueryStatement toOracleCreate() throws ConvertException {
        String[] keywords = null;
        this.modifiedObjects = new ModifiedObjectNames();
        ModifiedObjectAttr modifiedAttr = new ModifiedObjectAttr();
        String origTableName = null;
        if (SwisSQLUtils.getKeywords(1) != null) {
            keywords = SwisSQLUtils.getKeywords(1);
        }
        if (SwisSQLAPI.truncateTableCount > 99) {
            SwisSQLAPI.truncateTableCount = 0;
        }
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        final CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setDropOption(this.includeDrop);
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        if (cqs.getTemp() != null) {
            cqs.setTemp("GLOBAL TEMPORARY");
        }
        if (cqs.getTableObject() != null) {
            final StringBuffer tableStringBuffer = new StringBuffer();
            final TableObject orgTableObject = cqs.getTableObject();
            String table_name = orgTableObject.getTableName();
            String ownerName = orgTableObject.getOwner();
            String userName = orgTableObject.getUser();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (table_name != null && ((table_name.startsWith("[") && table_name.endsWith("]")) || (table_name.startsWith("`") && table_name.endsWith("`")))) {
                table_name = table_name.substring(1, table_name.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || table_name.indexOf(32) != -1) {
                    table_name = "\"" + table_name + "\"";
                }
            }
            origTableName = table_name;
            table_name = CustomizeUtil.objectNamesToQuotedIdentifier(table_name, keywords, modifiedAttr, 1);
            modifiedAttr.setOriginalName(origTableName);
            if (table_name != null && !table_name.equals(origTableName)) {
                modifiedAttr.setModifiedName(table_name);
                this.modifiedObjects.addModifiedObjectName(modifiedAttr);
            }
            if (SwisSQLAPI.truncateTableNameForOracle && table_name != null && table_name.length() > 30) {
                modifiedAttr = new ModifiedObjectAttr();
                modifiedAttr.setOriginalName(origTableName);
                if (table_name.indexOf("\"") != -1) {
                    table_name = table_name.substring(0, 23) + "_ADV" + SwisSQLAPI.truncateTableCount + "\"";
                    modifiedAttr.setModifiedName(table_name);
                    ++SwisSQLAPI.truncateTableCount;
                }
                else {
                    table_name = table_name.substring(0, 24) + "_ADV" + SwisSQLAPI.truncateTableCount;
                    modifiedAttr.setModifiedName(table_name);
                    ++SwisSQLAPI.truncateTableCount;
                }
                SwisSQLUtils.setObjectNameForMapping(origTableName, table_name);
                modifiedAttr.setModifiedType(1);
                this.modifiedObjects.addModifiedObjectName(modifiedAttr);
            }
            orgTableObject.setTableName(table_name);
            if (ownerName != null && (ownerName.equalsIgnoreCase("dbo") || ownerName.equalsIgnoreCase("[dbo]"))) {
                orgTableObject.setOwner(null);
            }
            orgTableObject.setOwner(ownerName);
            if (userName != null && (userName.equalsIgnoreCase("dbo") || userName.equalsIgnoreCase("[dbo]"))) {
                orgTableObject.setUser(null);
            }
            else {
                orgTableObject.setUser(userName);
            }
            if (this.isTenroxRequirement) {
                orgTableObject.setUser("PUSER");
            }
            final StringTokenizer st = new StringTokenizer(table_name, ".");
            int count = 0;
            final Vector tokenVector = new Vector();
            while (st.hasMoreTokens()) {
                tokenVector.add(st.nextToken());
                ++count;
            }
            for (int i_count = 0; i_count < tokenVector.size(); ++i_count) {
                String oracleTableName = tokenVector.get(i_count);
                if (oracleTableName != null && oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) {
                    oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
                    if (SwisSQLOptions.retainQuotedIdentifierForOracle || oracleTableName.indexOf(32) != -1) {
                        oracleTableName = "\"" + oracleTableName + "\"";
                    }
                }
                if (i_count > 0) {
                    tableStringBuffer.append(".");
                }
                tableStringBuffer.append(oracleTableName);
            }
            orgTableObject.setTableName(tableStringBuffer.toString());
            orgTableObject.toOracle();
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            final TableObject oldTableObject = createIndexClauseObject.getIndexName();
            final CreateIndexClause oracleCreateIndexClause = createIndexClauseObject.toOracle();
            if (oldTableObject != null) {
                final String origIndexName = oldTableObject.getTableName();
                modifiedAttr = new ModifiedObjectAttr();
                final String changedIndexName = CustomizeUtil.objectNamesToQuotedIdentifier(origIndexName, keywords, modifiedAttr, 1);
                modifiedAttr.setOriginalName(origIndexName);
                if (!origIndexName.equals(changedIndexName) && origIndexName.length() < 31) {
                    oldTableObject.setTableName(changedIndexName);
                    modifiedAttr.setModifiedName(changedIndexName);
                    this.modifiedObjects.addModifiedObjectName(modifiedAttr);
                }
                if (origIndexName.length() > 30) {
                    oracleCreateIndexClause.toString();
                    final String tmp = oracleCreateIndexClause.getIndexName().getTableName();
                    modifiedAttr.setModifiedName(tmp);
                    modifiedAttr.setModifiedType(1);
                    this.modifiedObjects.addModifiedObjectName(modifiedAttr);
                }
            }
            cqs.setCreateIndexClause(oracleCreateIndexClause);
        }
        if (cqs.getCreateIndexVector() != null) {
            String indexString = "";
            final Vector temp_Vector = new Vector();
            for (int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(i);
                if (!this.existsPKasIndex(createIndexClauseObject2, cqs) && !this.isDatatypeBlobOrClob(createIndexClauseObject2, cqs)) {
                    createIndexClauseObject2.setIndexOrKey("CREATE INDEX");
                    createIndexClauseObject2.setOn("ON");
                    createIndexClauseObject2.setTableOrView(this.tableObject);
                    final CreateIndexClause ansiIndexClause = createIndexClauseObject2.toOracle();
                    indexString = indexString + "\n/\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
                }
            }
            cqs.setIndexString(indexString);
            cqs.setCreateIndexVector(null);
        }
        if (cqs.getOnCondition() != null && cqs.getOnCondition().startsWith("ON DEFAULT")) {
            cqs.setOnCondition(null);
        }
        if (cqs.getConstraint() != null) {
            cqs.getConstraint();
        }
        if (cqs.getConstraintName() != null) {
            cqs.getConstraintName();
        }
        final ArrayList columnList = new ArrayList();
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            int countLong = 0;
            for (int j = 0; j < columnNamesVector.size(); ++j) {
                if (columnNamesVector.get(j) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(j);
                    final String origColName = changeCreateColumn.getColumnName();
                    if (origColName != null) {
                        modifiedAttr = new ModifiedObjectAttr();
                        changeCreateColumn.setColumnName(CustomizeUtil.objectNamesToQuotedIdentifier(changeCreateColumn.getColumnName(), keywords, modifiedAttr, 1));
                        final String modifiedColName = changeCreateColumn.getColumnName();
                        if (!origColName.equals(modifiedColName)) {
                            modifiedAttr.setOriginalName(origColName);
                            modifiedAttr.setModifiedName(modifiedColName);
                            modifiedAttr.setTableName(origTableName);
                            this.modifiedObjects.addModifiedColumns(modifiedAttr);
                        }
                    }
                    if (cqs.getTableObject() != null) {
                        final TableObject orgTableObject2 = cqs.getTableObject();
                        final String tableName = orgTableObject2.getTableName();
                        final String originalTableName = orgTableObject2.getOrigTableName();
                        String name = tableName;
                        if (this.context != null) {
                            if (originalTableName != null && originalTableName.startsWith("#")) {
                                name = this.context.getEquivalent(originalTableName).toString();
                            }
                            else {
                                name = this.context.getEquivalent(tableName).toString();
                            }
                        }
                        changeCreateColumn.setTableNameFromCreateQueryStmt(name);
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    if (changeCreateColumn.getDatatype() != null) {
                        final Datatype changeCreateColumnDatatype = changeCreateColumn.getDatatype();
                        if (changeCreateColumnDatatype instanceof CharacterClass) {
                            final CharacterClass charClass = (CharacterClass)changeCreateColumnDatatype;
                            final String datatypeName = charClass.getDatatypeName();
                            if (datatypeName.equalsIgnoreCase("LONG") && ++countLong > 1) {
                                charClass.setDatatypeName("TEXT");
                                charClass.setOpenBrace(null);
                                charClass.setClosedBrace(null);
                                charClass.setSize(null);
                            }
                        }
                    }
                    final Vector getConstraintVector = changeCreateColumn.getConstraintClause();
                    boolean isTableLevelConstraintRemoved = false;
                    isTableLevelConstraintRemoved = this.removeCheckConstrWithDetFunctions(getConstraintVector, changeCreateColumn, columnNamesVector, j);
                    changeCreateColumn.toOracleString();
                    if (changeCreateColumn.getColumnName() != null) {
                        columnList.add(changeCreateColumn.getColumnName());
                    }
                    if (changeCreateColumn.getCreateSequenceString() != null) {
                        cqs.setCreateSequenceString(changeCreateColumn.getCreateSequenceString());
                    }
                    final Vector changedConstraintVector = changeCreateColumn.getConstraintClause();
                    if (getConstraintVector != null) {
                        for (int k = 0; k < getConstraintVector.size(); ++k) {
                            final ConstraintClause toOracleConstraintClause = getConstraintVector.get(k);
                            final String origConstrName = toOracleConstraintClause.getConstraintName();
                            modifiedAttr = new ModifiedObjectAttr();
                            modifiedAttr.setOriginalName(origConstrName);
                            if (origConstrName != null && origConstrName.startsWith("\"") && !origConstrName.endsWith("\"")) {
                                toOracleConstraintClause.setConstraintName(origConstrName + "\"");
                                modifiedAttr.setModifiedType(1);
                                modifiedAttr.setModifiedName(origConstrName + "\"");
                                this.modifiedObjects.addModifiedConstraints(modifiedAttr);
                            }
                            toOracleConstraintClause.setConstraintName(CustomizeUtil.objectNamesToQuotedIdentifier(toOracleConstraintClause.getConstraintName(), keywords, modifiedAttr, 1));
                            final String modifiedConstrName = toOracleConstraintClause.getConstraintName();
                            if (origConstrName != null && !origConstrName.equals(modifiedConstrName)) {
                                modifiedAttr.setModifiedName(modifiedConstrName);
                                this.modifiedObjects.addModifiedConstraints(modifiedAttr);
                            }
                            if (toOracleConstraintClause != null) {
                                final ConstraintType changeConstraintType = toOracleConstraintClause.getConstraintType();
                                if (changeConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                    final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)changeConstraintType;
                                    primaryOrUniqueConstraintClause.setClustered(null);
                                    changeCreateColumn.setNotNullSetFromCreateQueryStatement(true);
                                    final Vector colNames = primaryOrUniqueConstraintClause.getConstraintColumnNames();
                                    this.columnNamesToQuotedIdentifier(colNames, keywords, origTableName);
                                    if (primaryOrUniqueConstraintClause.getWith() != null) {
                                        final Object fillfactor = primaryOrUniqueConstraintClause.getDiskAttr().get("FILLFACTOR");
                                        if (fillfactor != null) {
                                            final String percent = fillfactor.toString();
                                            cqs.setHeap("ORGANIZATION HEAP ");
                                            try {
                                                final int pcnt = Integer.parseInt(percent);
                                                if (pcnt <= 60) {
                                                    cqs.setDiskAttributes("PCTFREE " + pcnt);
                                                }
                                                else {
                                                    cqs.setDiskAttributes("PCTFREE " + pcnt + ",\n\tPCTUSED " + (100 - pcnt));
                                                }
                                            }
                                            catch (final Exception e) {
                                                cqs.setDiskAttributes("PCTFREE " + percent);
                                            }
                                        }
                                        primaryOrUniqueConstraintClause.setWith(null);
                                        primaryOrUniqueConstraintClause.setDiskAttr(null);
                                    }
                                }
                                else if (changeConstraintType instanceof ForeignConstraintClause) {
                                    final ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)changeConstraintType;
                                    final TableObject fkTableObject = foreignConstraintClause.getTableName();
                                    modifiedAttr = new ModifiedObjectAttr();
                                    if (fkTableObject != null) {
                                        final String fkTableName = fkTableObject.getTableName();
                                        if (fkTableName != null) {
                                            final String modifiedFKTableName = CustomizeUtil.objectNamesToQuotedIdentifier(fkTableName, keywords, modifiedAttr, 1);
                                            if (!modifiedFKTableName.equals(fkTableName)) {
                                                fkTableObject.setTableName(modifiedFKTableName);
                                                modifiedAttr.setOriginalName(fkTableName);
                                                modifiedAttr.setModifiedName(modifiedFKTableName);
                                                this.modifiedObjects.addModifiedObjectName(modifiedAttr);
                                            }
                                        }
                                    }
                                    final Vector colNames2 = foreignConstraintClause.getConstraintColumnNames();
                                    this.columnNamesToQuotedIdentifier(colNames2, keywords, origTableName);
                                    final Vector fkColNames = foreignConstraintClause.getReferenceTableColumnNames();
                                    this.columnNamesToQuotedIdentifier(fkColNames, keywords, origTableName);
                                }
                                cqs.setTriggerForIdentity(toOracleConstraintClause.getTriggerForIdentity());
                            }
                        }
                    }
                    if (isTableLevelConstraintRemoved) {
                        --j;
                    }
                }
            }
            this.removeUniqueKeyConstrOnPKExists(columnNamesVector);
            final HashMap truncatedNames = SwisSQLUtils.truncateNames(columnList, 30);
            for (int l = 0; l < columnNamesVector.size(); ++l) {
                if (columnNamesVector.get(l) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn2 = columnNamesVector.get(l);
                    final String oracleColName = changeCreateColumn2.getColumnName();
                    final String truncatedName = truncatedNames.get(oracleColName);
                    if (truncatedName != null) {
                        changeCreateColumn2.setColumnName(truncatedName);
                    }
                    final Vector constraintVector = changeCreateColumn2.getConstraintClause();
                    if (constraintVector != null) {
                        for (int k = 0; k < constraintVector.size(); ++k) {
                            final ConstraintClause toOracleConstraintClause = constraintVector.get(k);
                            if (toOracleConstraintClause != null) {
                                final ConstraintType changeConstraintType2 = toOracleConstraintClause.getConstraintType();
                                if (changeConstraintType2 instanceof PrimaryOrUniqueConstraintClause) {
                                    final PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause2 = (PrimaryOrUniqueConstraintClause)changeConstraintType2;
                                    final Vector colNames3 = primaryOrUniqueConstraintClause2.getConstraintColumnNames();
                                    if (colNames3 != null) {
                                        for (int m = 0; m < colNames3.size(); ++m) {
                                            final String colName = colNames3.get(m);
                                            final String truncatedColName = truncatedNames.get(colName);
                                            if (truncatedColName != null) {
                                                colNames3.setElementAt(truncatedColName, m);
                                            }
                                        }
                                    }
                                }
                                else if (changeConstraintType2 instanceof ForeignConstraintClause) {
                                    final ForeignConstraintClause foreignConstraintClause2 = (ForeignConstraintClause)changeConstraintType2;
                                    final Vector colNames3 = foreignConstraintClause2.getConstraintColumnNames();
                                    if (colNames3 != null) {
                                        for (int m = 0; m < colNames3.size(); ++m) {
                                            final String colName = colNames3.get(m);
                                            final String truncatedColName = truncatedNames.get(colName);
                                            if (truncatedColName != null) {
                                                colNames3.setElementAt(truncatedColName, m);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.getSelectQueryStatement() != null) {
            cqs.setSelectQueryStatement(this.selectQueryStatement.toOracleSelect());
        }
        if (this.getWithStatement() != null) {
            cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toOracle());
            cqs.setWithStatement(null);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getPhysicalCharacteristics() != null) {
            cqs.getPhysicalCharacteristics();
        }
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toOracle();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toOracleString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        if (cqs.getPhysicalAttributesVector() != null) {
            final Vector temp_Vector = new Vector();
            for (int i = 0; i < cqs.getPhysicalAttributesVector().size(); ++i) {
                final PhysicalAttributesClause temp_PhysicalAttributesClause = cqs.getPhysicalAttributesVector().get(i);
                temp_Vector.add(temp_PhysicalAttributesClause.toOracle());
            }
            cqs.setPhysicalAttributesVector(temp_Vector);
        }
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        String name2 = "";
        if (this.tableObject != null) {
            this.tableObject.setObjectContext(this.context);
            name2 = this.tableObject.toString();
        }
        if (this.tableOrView != null) {
            if (this.tableOrView.equalsIgnoreCase("TABLE")) {
                this.dropString = "BEGIN \nEXECUTE IMMEDIATE 'DROP " + this.tableOrView + " " + name2 + " CASCADE CONSTRAINTS';\nEXCEPTION WHEN OTHERS THEN NULL;\nEND;\n/\n\n";
            }
            else {
                this.dropString = "BEGIN \nEXECUTE IMMEDIATE 'DROP " + this.tableOrView + " " + name2 + "';\nEXCEPTION WHEN OTHERS THEN NULL;\nEND;\n/\n\n";
            }
        }
        else if (this.createIndexClause != null) {
            if (this.createIndexClause.getIndexName() != null) {
                this.createIndexClause.getIndexName().setObjectContext(this.context);
                name2 = this.createIndexClause.getIndexName().toString();
            }
            this.dropString = "BEGIN \nEXECUTE IMMEDIATE 'DROP INDEX " + name2 + "';\nEXCEPTION WHEN OTHERS THEN NULL;\nEND;\n/\n\n";
        }
        cqs.setDropString(this.dropString);
        cqs.setModifiedObject(this.modifiedObjects);
        return cqs;
    }
    
    private void columnNamesToQuotedIdentifier(final Vector colNames, final String[] keywords, final String origTableName) {
        if (colNames != null) {
            for (int k = 0; k < colNames.size(); ++k) {
                final Object obj = colNames.get(k);
                if (obj instanceof String) {
                    final String origCol = (String)obj;
                    if (origCol != null) {
                        final ModifiedObjectAttr modifiedAttr = new ModifiedObjectAttr();
                        final String modifiedCol = CustomizeUtil.objectNamesToQuotedIdentifier(origCol, keywords, modifiedAttr, 1);
                        colNames.setElementAt(modifiedCol, k);
                        if (!modifiedCol.equals(origCol)) {
                            modifiedAttr.setTableName(origTableName);
                            modifiedAttr.setOriginalName(origCol);
                            modifiedAttr.setModifiedName(modifiedCol);
                            this.modifiedObjects.addModifiedColumns(modifiedAttr);
                        }
                    }
                }
            }
        }
    }
    
    private boolean removeCheckConstrWithDetFunctions(final Vector getConstraintVector, final CreateColumn changeCreateColumn, final Vector columnNamesVector, final int i) {
        if (getConstraintVector != null) {
            for (int j = 0; j < getConstraintVector.size(); ++j) {
                final ConstraintClause toConstraintClause = getConstraintVector.get(j);
                if (toConstraintClause != null && toConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
                    final CheckConstraintClause ccc = (CheckConstraintClause)toConstraintClause.getConstraintType();
                    boolean remove = false;
                    final WhereExpression we = ccc.getWhereExpression();
                    final Vector v = we.getWhereItems();
                    for (int size = v.size(), k = 0; k < size; ++k) {
                        final Object val = v.get(k);
                        if (val instanceof WhereItem) {
                            final WhereItem wi = (WhereItem)val;
                            final WhereColumn wcl = wi.getLeftWhereExp();
                            final Vector col = wcl.getColumnExpression();
                            if (col != null) {
                                for (int h = 0; h < col.size(); ++h) {
                                    final Object obj = col.get(h);
                                    if (obj instanceof FunctionCalls) {
                                        final FunctionCalls fc = (FunctionCalls)obj;
                                        final TableColumn tc = fc.getFunctionName();
                                        if (tc != null) {
                                            final String name = tc.getColumnName();
                                            if (name.equalsIgnoreCase("getdate")) {
                                                remove = true;
                                            }
                                        }
                                    }
                                }
                            }
                            final WhereColumn wcr = wi.getRightWhereExp();
                            if (wcr != null) {
                                final Vector colr = wcr.getColumnExpression();
                                if (colr != null) {
                                    for (int h2 = 0; h2 < colr.size(); ++h2) {
                                        final Object obj2 = colr.get(h2);
                                        if (obj2 instanceof FunctionCalls) {
                                            final FunctionCalls fc2 = (FunctionCalls)obj2;
                                            final TableColumn tc2 = fc2.getFunctionName();
                                            if (tc2 != null) {
                                                final String name2 = tc2.getColumnName();
                                                if (name2.equalsIgnoreCase("getdate")) {
                                                    remove = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (remove) {
                        if (changeCreateColumn.getColumnName() == null) {
                            columnNamesVector.remove(i);
                            return true;
                        }
                        getConstraintVector.remove(j);
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQLCreate().toString();
    }
    
    public CreateQueryStatement toPostgreSQLCreate() throws ConvertException {
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        final CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setForce(null);
        cqs.setHeap(null);
        cqs.setPhysicalCharacteristics(null);
        cqs.setCreateOrReplace(null);
        cqs.setDiskAttributes(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setMaterialized(null);
        cqs.setViewMetaAttribute(null);
        cqs.setWithReadOnly(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        if (cqs.getTemp() != null) {
            final String tempCQS = cqs.getTemp();
            if (tempCQS.equalsIgnoreCase("GLOBAL TEMPORARY") || tempCQS.equalsIgnoreCase("LOCAL TEMPORARY")) {
                cqs.setTemp("TEMPORARY");
            }
        }
        if (cqs.getTableObject() != null) {
            final TableObject cqsTableObject = cqs.getTableObject();
            String ownerName = cqsTableObject.getOwner();
            String userName = cqsTableObject.getUser();
            String tableName = cqsTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            cqsTableObject.setOwner(ownerName);
            cqsTableObject.setUser(userName);
            cqsTableObject.setTableName(tableName);
            cqsTableObject.toPostgreSQL();
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toPostgreSQL());
        }
        if (cqs.getCreateIndexVector() != null) {
            String indexString = "";
            final Vector temp_Vector = new Vector();
            for (int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(i);
                if (!this.existsPKasIndex(createIndexClauseObject2, cqs)) {
                    createIndexClauseObject2.setIndexOrKey("CREATE INDEX");
                    createIndexClauseObject2.setOn("ON");
                    createIndexClauseObject2.setTableOrView(this.tableObject);
                    final CreateIndexClause ansiIndexClause = createIndexClauseObject2.toPostgreSQL();
                    indexString = indexString + "\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString();
                }
            }
            cqs.setIndexString("\n" + indexString);
            cqs.setCreateIndexVector(null);
        }
        if (cqs.getOnCondition() != null && cqs.getOnCondition().startsWith("ON DEFAULT")) {
            cqs.setOnCondition(null);
        }
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            for (int j = 0; j < columnNamesVector.size(); ++j) {
                if (columnNamesVector.get(j) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(j);
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    changeCreateColumn.toPostgreSQLString();
                }
            }
        }
        if (this.getSelectQueryStatement() != null) {
            cqs.setSelectQueryStatement(this.selectQueryStatement.toPostgreSQLSelect());
        }
        if (this.getWithStatement() != null) {
            cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toPostgreSQL());
            cqs.setWithStatement(null);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toPostgreSQL();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toPostgreSQLString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        cqs.setPhysicalAttributesVector(null);
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        return cqs;
    }
    
    private void removeUniqueKeyConstrOnPKExists(final Vector columnNamesVector) {
        final ArrayList list = new ArrayList();
        for (int j = 0; j < columnNamesVector.size(); ++j) {
            if (columnNamesVector.get(j) instanceof CreateColumn) {
                final CreateColumn changeCreateColumn = columnNamesVector.get(j);
                final Vector constrClause = changeCreateColumn.getConstraintClause();
                if (constrClause != null) {
                    for (int k = 0; k < constrClause.size(); ++k) {
                        final ConstraintClause constraintClause = constrClause.get(k);
                        if (constraintClause != null) {
                            final ConstraintType constrType = constraintClause.getConstraintType();
                            if (constrType != null && constrType instanceof PrimaryOrUniqueConstraintClause) {
                                final PrimaryOrUniqueConstraintClause tempPrimaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)constrType;
                                if (tempPrimaryOrUniqueConstraintClause != null) {
                                    final Vector ukpkColNames = tempPrimaryOrUniqueConstraintClause.getConstraintColumnNames();
                                    final String ukpkConstraintName = tempPrimaryOrUniqueConstraintClause.getConstraintName();
                                    if (ukpkColNames != null && ukpkConstraintName != null && (ukpkConstraintName.trim().equalsIgnoreCase("unique key") || ukpkConstraintName.trim().equalsIgnoreCase("unique")) && !list.isEmpty() && ukpkColNames.size() == list.size()) {
                                        for (int l = 0; l < ukpkColNames.size(); ++l) {
                                            if (list.get(l).toString().equalsIgnoreCase(ukpkColNames.get(l).toString()) && l == ukpkColNames.size() - 1) {
                                                columnNamesVector.remove(j);
                                                --j;
                                            }
                                        }
                                    }
                                    if (ukpkColNames != null && ukpkConstraintName != null && ukpkConstraintName.trim().equalsIgnoreCase("primary key")) {
                                        for (int l = 0; l < ukpkColNames.size(); ++l) {
                                            list.add(ukpkColNames.get(l).toString().toLowerCase());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenCreate().toString();
    }
    
    public CreateQueryStatement toTimesTenCreate() throws ConvertException {
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setSelectQueryStatement(null);
        cqs.setHeap(null);
        cqs.setTemp(null);
        cqs.setForce(null);
        cqs.setPhysicalCharacteristics(null);
        cqs.setCreateOrReplace(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setViewMetaAttribute(null);
        cqs.setWithReadOnly(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        if (cqs.getDatabaseObject() != null) {
            throw new ConvertException("\n\nCREATE DATABASE syntax is not supported in TimesTen 5.1.21\n");
        }
        if (cqs.getTemp() != null) {
            throw new ConvertException("\n\nSession specific Temporary Tables are not supported in TimesTen 5.1.21\n");
        }
        if (cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("view") && cqs.materialized == null) {
            throw new ConvertException("\n\nCREATE VIEW is not supported in TimesTen 5.1.21\n");
        }
        if (cqs.getOnCondition() != null) {
            cqs.setOnCondition(null);
        }
        if (cqs.getDiskAttributes() != null) {
            cqs.setDiskAttributes(null);
        }
        if (cqs.getTableObject() != null) {
            final TableObject orgTableObject = cqs.getTableObject();
            String ownerName = orgTableObject.getOwner();
            String tableName = orgTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            if (tableName != null && tableName.startsWith("#")) {
                throw new ConvertException("\n\nSession specific Temporary Tables are not supported in TimesTen 5.1.21\n");
            }
            orgTableObject.setOwner(ownerName);
            orgTableObject.setTableName(tableName);
            orgTableObject.toTimesTen();
        }
        int indexCount = 0;
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    changeCreateColumn.setColumnName(CustomizeUtil.objectNamesToQuotedIdentifier(changeCreateColumn.getColumnName(), SwisSQLUtils.getKeywords(10), null, 10));
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    final Vector constraintVector = changeCreateColumn.getConstraintClause();
                    boolean columnRemoved = false;
                    if (constraintVector != null) {
                        for (int j = 0; j < constraintVector.size(); ++j) {
                            final ConstraintClause toTimesTenConstraintClause = constraintVector.get(j);
                            final ConstraintType toTimesTenConstraintType = toTimesTenConstraintClause.getConstraintType();
                            if (toTimesTenConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                                final PrimaryOrUniqueConstraintClause pkClause = (PrimaryOrUniqueConstraintClause)toTimesTenConstraintType;
                                final String constraintType = pkClause.getConstraintName();
                                if (constraintType.toLowerCase().indexOf("unique") != -1) {
                                    final CreateIndexClause createIndexClause = new CreateIndexClause();
                                    createIndexClause.setIndexOrKey("CREATE UNIQUE INDEX");
                                    final TableObject indexObject = new TableObject();
                                    String indexName = cqs.getTableObject().getTableName();
                                    if (indexName.length() > 22) {
                                        indexName = indexName.substring(0, 22);
                                    }
                                    indexObject.setTableName(indexName + "_index_" + ++indexCount);
                                    createIndexClause.setIndexName(indexObject);
                                    createIndexClause.setOn("ON");
                                    final Vector consColNames = pkClause.getConstraintColumnNames();
                                    final ArrayList indexColumns = new ArrayList();
                                    if (consColNames != null && changeCreateColumn.getColumnName() == null) {
                                        for (int k = 0; k < consColNames.size(); ++k) {
                                            final String consColName = consColNames.get(k);
                                            indexColumns.add(this.createIndexColumn(consColName));
                                        }
                                    }
                                    else if (changeCreateColumn.getColumnName() != null) {
                                        indexColumns.add(this.createIndexColumn(changeCreateColumn.getColumnName()));
                                    }
                                    createIndexClause.setIndexColumns(indexColumns);
                                    createIndexClause.setTableOrView(cqs.getTableObject());
                                    createIndexClause.setClosedBraces(")");
                                    createIndexClause.setOpenBraces("(");
                                    final CreateIndexClause indexClause = createIndexClause.toTimesTen();
                                    this.ttUniqueIndexforUniqueCons.add(indexClause);
                                    if (changeCreateColumn.getColumnName() == null) {
                                        columnNamesVector.remove(i--);
                                        columnRemoved = true;
                                    }
                                    else {
                                        constraintVector.remove(j--);
                                    }
                                }
                            }
                            else if (toTimesTenConstraintType instanceof CheckConstraintClause) {
                                if (CreateQueryStatement.commentWhenConstraintNameTruncated.trim().length() > 0 && CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("CHECK Constraint") == -1) {
                                    CreateQueryStatement.commentWhenConstraintNameTruncated += "\n/* SwisSQL Message : CHECK Constraint is not supported in TimesTen 5.1.21 */";
                                }
                                else if (CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("CHECK Constraint") == -1) {
                                    CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : CHECK Constraint is not supported in TimesTen 5.1.21 */";
                                }
                                if (changeCreateColumn.getColumnName() == null) {
                                    columnNamesVector.remove(i--);
                                    columnRemoved = true;
                                }
                                else {
                                    constraintVector.remove(j--);
                                }
                            }
                            else if (toTimesTenConstraintType instanceof ForeignConstraintClause) {
                                final ForeignConstraintClause tempForeignConstraintClause = (ForeignConstraintClause)toTimesTenConstraintType;
                                if (tempForeignConstraintClause.getConstraintName() == null && tempForeignConstraintClause.getReference() != null && changeCreateColumn.getColumnName() != null) {
                                    if (CreateQueryStatement.commentWhenConstraintNameTruncated.trim().length() > 0 && CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("Referential Integrity Constraint") == -1) {
                                        CreateQueryStatement.commentWhenConstraintNameTruncated += "\n/* SwisSQL Message : Column-level Referential Integrity Constraint is not supported in TimesTen 5.1.21. Constraint definition moved at the level of the table. */";
                                    }
                                    else if (CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("Referential Integrity Constraint") == -1) {
                                        CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : Column-level Referential Integrity Constraint is not supported in TimesTen 5.1.21. Constraint definition moved at the level of the table. */";
                                    }
                                    if (toTimesTenConstraintClause.getConstraintName() == null) {
                                        toTimesTenConstraintClause.setConstraint("CONSTRAINT");
                                        String consName = "_CT_" + ++SwisSQLAPI.truncateConstraintCount;
                                        if (changeCreateColumn.getColumnName().length() > 25) {
                                            consName = changeCreateColumn.getColumnName().substring(0, 25) + consName;
                                        }
                                        else {
                                            consName = changeCreateColumn.getColumnName() + consName;
                                        }
                                        toTimesTenConstraintClause.setConstraintName(consName);
                                    }
                                    tempForeignConstraintClause.setConstraintName("FOREIGN KEY");
                                    final Vector consColNames2 = new Vector();
                                    consColNames2.add(changeCreateColumn.getColumnName());
                                    tempForeignConstraintClause.setConstraintColumnNames(consColNames2);
                                    tempForeignConstraintClause.setOpenBrace("(");
                                    tempForeignConstraintClause.setClosedBrace(")");
                                    if (tempForeignConstraintClause.getReferenceTableColumnNames() == null) {
                                        tempForeignConstraintClause.setReferenceOpenBrace("(");
                                        tempForeignConstraintClause.setReferenceClosedBrace(")");
                                        final TableObject refTable = tempForeignConstraintClause.getTableName();
                                        final Object obj = CastingUtil.getValueIgnoreCase(SwisSQLAPI.primaryKeyMetaData, refTable.getTableName());
                                        if (obj != null) {
                                            final ArrayList colList = (ArrayList)obj;
                                            final Vector refCols = new Vector();
                                            for (int l = 0; l < colList.size(); ++l) {
                                                refCols.add(colList.get(l));
                                            }
                                            tempForeignConstraintClause.setReferenceTableColumnNames(refCols);
                                        }
                                        else if (CreateQueryStatement.commentWhenConstraintNameTruncated.trim().length() > 0 && CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("Metadata") == -1) {
                                            CreateQueryStatement.commentWhenConstraintNameTruncated += "\n/* SwisSQL Message : Metadata of the source database required for accurate conversion */";
                                        }
                                        else if (CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("Metadata") == -1) {
                                            CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : Metadata of the source database required for accurate conversion */";
                                        }
                                    }
                                    final CreateColumn ccForRefConstr = new CreateColumn();
                                    final Vector newConstrVector = new Vector();
                                    newConstrVector.add(toTimesTenConstraintClause);
                                    ccForRefConstr.setConstraintClause(newConstrVector);
                                    columnNamesVector.add(ccForRefConstr);
                                    constraintVector.remove(j--);
                                }
                            }
                        }
                    }
                    cqs.setTTUniqueIndicesForUniqCons(this.ttUniqueIndexforUniqueCons);
                    if (!columnRemoved) {
                        changeCreateColumn.toTimesTenString();
                    }
                }
            }
        }
        cqs.setForce(null);
        if (this.getSelectQueryStatement() != null && this.getTableOrView().equalsIgnoreCase("VIEW")) {
            cqs.setSelectQueryStatement(this.getSelectQueryStatement());
        }
        if (this.getWithStatement() != null && this.getTableOrView().equalsIgnoreCase("VIEW")) {
            cqs.setWithStatement((WithStatement)this.withStatement.toTimesTen());
        }
        if (this.getSelectQueryStatement() != null && this.getTableOrView().equalsIgnoreCase("TABLE")) {
            final TableObject orgTableObject2 = cqs.getTableObject();
            if (orgTableObject2 != null) {
                final String tableName = orgTableObject2.getTableName();
                cqs = SwisSQLUtils.constructCQS(tableName, this.getSelectQueryStatement(), null);
                final Vector selectColVector = cqs.getColumnNames();
                final Vector createColVector = this.getColumnNames();
                for (int m = 0; m < selectColVector.size(); ++m) {
                    if (createColVector != null && createColVector.get(m) != null && selectColVector != null && selectColVector.get(m) != null) {
                        final String colname = createColVector.get(m).getColumnName();
                        selectColVector.get(m).setColumnName(colname);
                    }
                }
                cqs = cqs.toTimesTenCreate();
                final InsertQueryStatement iqs = new InsertQueryStatement();
                final InsertClause ic = new InsertClause();
                ic.setInsert("INSERT");
                final OptionalSpecifier optionalSpecifier = new OptionalSpecifier();
                optionalSpecifier.setInto("INTO");
                ic.setOptionalSpecifier(optionalSpecifier);
                final TableExpression texpr = new TableExpression();
                final ArrayList newList = new ArrayList();
                final TableClause tc = new TableClause();
                final TableObject to = new TableObject();
                to.setTableName(tableName);
                tc.setTableObject(to);
                newList.add(tc);
                texpr.setTableClauseList(newList);
                ic.setTableExpression(texpr);
                iqs.setInsertClause(ic);
                cqs.setInsertQueryStatement(iqs);
                this.comment_flag__for_create_as_select_in_timesten = true;
                cqs.setSelectQueryStatement(this.getSelectQueryStatement());
            }
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toTimesTen());
        }
        if (cqs.getCreateIndexVector() != null) {
            String indexString = "";
            final Vector temp_Vector = new Vector();
            for (int i2 = 0; i2 < cqs.getCreateIndexVector().size(); ++i2) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(i2);
                if (!this.existsPKasIndex(createIndexClauseObject2, cqs)) {
                    createIndexClauseObject2.setIndexOrKey("CREATE INDEX");
                    createIndexClauseObject2.setOn("ON");
                    createIndexClauseObject2.setTableOrView(this.tableObject);
                    final CreateIndexClause indexClause2 = createIndexClauseObject2.toTimesTen();
                    indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + indexClause2.toString().trim();
                }
            }
            cqs.setIndexString(indexString);
            cqs.setCreateIndexVector(null);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toTimesTen();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toTimesTenString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        cqs.setPhysicalAttributesVector(null);
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        return cqs;
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaCreate().toString();
    }
    
    public CreateQueryStatement toNetezzaCreate() throws ConvertException {
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        final CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setViewMetaAttribute(null);
        cqs.setWithReadOnly(null);
        cqs.setWithCheckOption(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setStartWith(this.startWith);
        cqs.setStartWithFunction(this.startWithFunction);
        cqs.setNextString(this.nextString);
        cqs.setSelectColumnInNextClause(this.selectColumnInNextClause);
        if (cqs.getColumnNames() != null) {
            int lobDatatypeCount = 0;
            final Vector columnNamesVector = cqs.getColumnNames();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    final Vector constraintVector = changeCreateColumn.getConstraintClause();
                    boolean columnRemoved = false;
                    if (constraintVector != null) {
                        for (int j = 0; j < constraintVector.size(); ++j) {
                            final ConstraintClause toTimesTenConstraintClause = constraintVector.get(j);
                            final ConstraintType toTimesTenConstraintType = toTimesTenConstraintClause.getConstraintType();
                            if (toTimesTenConstraintType instanceof CheckConstraintClause) {
                                if (CreateQueryStatement.commentWhenConstraintNameTruncated.trim().length() > 0 && CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("CHECK Constraint") == -1) {
                                    CreateQueryStatement.commentWhenConstraintNameTruncated += "\n/* SwisSQL Message : CHECK Constraint is not supported in Netezza */";
                                }
                                else if (CreateQueryStatement.commentWhenConstraintNameTruncated.indexOf("CHECK Constraint") == -1) {
                                    CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : CHECK Constraint is not supported in Netezza */";
                                }
                                if (changeCreateColumn.getColumnName() == null) {
                                    columnNamesVector.remove(i--);
                                    columnRemoved = true;
                                }
                                else {
                                    constraintVector.remove(j--);
                                }
                            }
                        }
                    }
                    changeCreateColumn.toNetezzaString();
                    if (changeCreateColumn.getDatatype() != null) {
                        final String dataTypeSize = changeCreateColumn.getDatatype().getSize();
                        if (dataTypeSize != null) {
                            try {
                                final int datatype_size = Integer.parseInt(dataTypeSize);
                                lobDatatypeCount += datatype_size;
                            }
                            catch (final NumberFormatException ex) {}
                        }
                    }
                }
            }
            if (lobDatatypeCount >= 64000) {
                SwisSQLUtils.swissqlMessageList.add("The tuple size of the tables is more than 64k. Manual intervention required.");
            }
        }
        if (cqs.getTemp() != null) {
            final String tempCQS = cqs.getTemp();
            cqs.setTemp("TEMPORARY");
        }
        if (cqs.getTableObject() != null) {
            final TableObject cqsTableObject = cqs.getTableObject();
            String ownerName = cqsTableObject.getOwner();
            String userName = cqsTableObject.getUser();
            String tableName = cqsTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(11), null, 11);
            cqsTableObject.setOwner(ownerName);
            cqsTableObject.setUser(userName);
            cqsTableObject.setTableName(tableName);
            cqsTableObject.toNetezza();
        }
        if (cqs.getOnCondition() != null) {
            cqs.setOnCondition(null);
        }
        cqs.setForce(null);
        cqs.setHeap(null);
        cqs.setDiskAttributes(null);
        cqs.setPhysicalCharacteristics(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        if (cqs.getSelectQueryStatement() != null) {
            if (cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("VIEW")) {
                if (cqs.getColumnNames() != null) {
                    cqs.setColumnNames(null);
                    cqs.setOpenBraces(null);
                    cqs.setClosedBraces(null);
                }
                if (cqs.getMaterialized() != null) {
                    cqs.getSelectQueryStatement().setWhereExpression(null);
                }
            }
            cqs.setSelectQueryStatement(this.selectQueryStatement.toNetezzaSelect());
        }
        if (this.getWithStatement() != null) {
            cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toNetezza());
            cqs.setWithStatement(null);
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toNetezza());
        }
        if (cqs.getCreateIndexVector() != null) {
            String indexString = "";
            final Vector temp_Vector = new Vector();
            for (int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(i);
                if (!this.existsPKasIndex(createIndexClauseObject2, cqs)) {
                    createIndexClauseObject2.setIndexOrKey("CREATE INDEX");
                    createIndexClauseObject2.setOn("ON");
                    createIndexClauseObject2.setTableOrView(this.tableObject);
                    final CreateIndexClause netezzaIndexClause = createIndexClauseObject2.toNetezza();
                    indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + netezzaIndexClause.toString().trim();
                }
            }
            cqs.setIndexString(indexString);
            cqs.setCreateIndexVector(null);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toNetezza();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toNetezzaString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        cqs.setPhysicalAttributesVector(null);
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        if (cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("TABLE")) {
            cqs.setDistributeOnRandomClause("DISTRIBUTE ON RANDOM");
        }
        if (cqs.getExternalTable() != null) {
            cqs.setDistributeOnRandomClause(null);
        }
        return cqs;
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataCreate().toString();
    }
    
    public CreateQueryStatement toTeradataCreate() throws ConvertException {
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
        final CreateQueryStatement cqs = this.copyObjectValues();
        cqs.setCreateOrReplace(null);
        cqs.setOnForQuotedIdentifier(null);
        cqs.setQuotedIdentifierCondition(null);
        cqs.setMaterialized(null);
        cqs.setViewMetaAttribute(null);
        cqs.setWithReadOnly(null);
        cqs.setLock(null);
        cqs.setLockData(null);
        cqs.setExternalTable(null);
        cqs.setExternalDefaultDirectory(null);
        cqs.setExternalDelimiter(null);
        if (cqs.getCommentClass() != null) {
            cqs.getCommentClass().setSQLDialect(12);
        }
        if (cqs.getColumnNames() != null) {
            final Vector columnNamesVector = cqs.getColumnNames();
            for (int i = 0; i < columnNamesVector.size(); ++i) {
                if (columnNamesVector.get(i) instanceof CreateColumn) {
                    final CreateColumn changeCreateColumn = columnNamesVector.get(i);
                    if (cqs.getTableObject() != null) {
                        changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                        changeCreateColumn.setDatatypeMapping(this.mapping);
                    }
                    changeCreateColumn.toTeradataString();
                    if (cqs.getTemp() != null && SwisSQLOptions.convertTemporaryTableToPermanentTable && changeCreateColumn.getDatatype() instanceof CharacterClass && changeCreateColumn.getDatatype().getDatatypeName() != null && (changeCreateColumn.getDatatype().getDatatypeName().equalsIgnoreCase("char") || changeCreateColumn.getDatatype().getDatatypeName().equalsIgnoreCase("varchar"))) {
                        ((CharacterClass)changeCreateColumn.getDatatype()).setCaseSpecificPhrase("CASESPECIFIC");
                    }
                    if (changeCreateColumn.getComputedColumnExpression() != null) {
                        this.computedColumnsVector.add(columnNamesVector.get(i));
                        columnNamesVector.removeElementAt(i);
                        --i;
                    }
                }
            }
        }
        if (cqs.getTableObject() != null) {
            final TableObject cqsTableObject = cqs.getTableObject();
            String ownerName = cqsTableObject.getOwner();
            String userName = cqsTableObject.getUser();
            String tableName = cqsTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
                ownerName = CustomizeUtil.objectNamesToQuotedIdentifier(ownerName, SwisSQLUtils.getKeywords("teradata"), null, -1);
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
                userName = CustomizeUtil.objectNamesToQuotedIdentifier(userName, SwisSQLUtils.getKeywords("teradata"), null, -1);
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
                tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords("teradata"), null, -1);
            }
            cqsTableObject.setOwner(ownerName);
            cqsTableObject.setUser(userName);
            cqsTableObject.setTableName(tableName);
            cqsTableObject.toTeradata();
        }
        if (cqs.getTemp() != null) {
            if (SwisSQLOptions.convertTemporaryTableToPermanentTable || SwisSQLOptions.oracleTempTableToTDConversionFormat == 2) {
                cqs.setTemp(null);
                cqs.setOnCondition(null);
                if (this.getSelectQueryStatement() != null) {
                    cqs.onConditionForTeradata = "WITH DATA";
                }
            }
            else if (SwisSQLOptions.oracleTempTableToTDConversionFormat == 0) {
                final String tempCQS = cqs.getTemp();
                if (tempCQS.equalsIgnoreCase("TEMP") || tempCQS.equalsIgnoreCase("TEMPORARY") || tempCQS.equalsIgnoreCase("GLOBAL TEMPORARY")) {
                    cqs.setTemp("GLOBAL TEMPORARY");
                    cqs.noLogConditionForTeradata = ", NO LOG";
                    cqs.onConditionForTeradata = "WITH DATA";
                    final InsertQueryStatement iqs = new InsertQueryStatement();
                    final InsertClause ic = new InsertClause();
                    ic.setInsert("INSERT");
                    final OptionalSpecifier optionalSpecifier = new OptionalSpecifier();
                    optionalSpecifier.setInto("INTO");
                    ic.setOptionalSpecifier(optionalSpecifier);
                    final TableExpression texpr = new TableExpression();
                    final ArrayList newList = new ArrayList();
                    final TableClause tc = new TableClause();
                    tc.setTableObject(cqs.getTableObject());
                    newList.add(tc);
                    texpr.setTableClauseList(newList);
                    ic.setTableExpression(texpr);
                    iqs.setInsertClause(ic);
                    cqs.setInsertQueryStatement(iqs);
                }
            }
            else {
                final String tempCQS = cqs.getTemp();
                if (tempCQS.equalsIgnoreCase("TEMP") || tempCQS.equalsIgnoreCase("TEMPORARY") || tempCQS.equalsIgnoreCase("GLOBAL TEMPORARY")) {
                    cqs.setTemp("VOLATILE");
                    cqs.noLogConditionForTeradata = ", NO LOG";
                }
            }
        }
        if (cqs.getOnCondition() != null) {
            if (this.getSelectQueryStatement() != null) {
                if (SwisSQLOptions.oracleTempTableToTDConversionFormat == 0) {
                    cqs.onConditionForTeradata = "WITH NO DATA\n" + cqs.getOnCondition();
                }
                else {
                    cqs.onConditionForTeradata = "WITH DATA\n" + cqs.getOnCondition();
                }
                cqs.setOnCondition(null);
            }
            else {
                cqs.onConditionForTeradata = cqs.getOnCondition();
                cqs.setOnCondition(null);
            }
        }
        cqs.setForce(null);
        cqs.setHeap(null);
        cqs.setDiskAttributes(null);
        cqs.setPhysicalCharacteristics(null);
        cqs.setConstraint(null);
        cqs.setConstraintName(null);
        if (this.getSelectQueryStatement() != null) {
            final SelectQueryStatement teradataSQS = this.selectQueryStatement.toTeradataSelect();
            cqs.setOpenBraceForSelectQuery("(");
            cqs.setSelectQueryStatement(teradataSQS);
            cqs.setCloseBraceForSelectQuery(")");
            if (cqs.getInsertQueryStatement() != null && cqs.getInsertQueryStatement().getSubQuery() == null) {
                cqs.getInsertQueryStatement().setSubQuery(teradataSQS);
            }
        }
        if (cqs.getCreateIndexClause() != null) {
            final CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toTeradata());
        }
        if (cqs.getCreateIndexVector() != null) {
            String indexString = "";
            final Vector temp_Vector = new Vector();
            for (int j = 0; j < cqs.getCreateIndexVector().size(); ++j) {
                final CreateIndexClause createIndexClauseObject2 = cqs.getCreateIndexVector().get(j);
                if (!this.existsPKasIndex(createIndexClauseObject2, cqs)) {
                    createIndexClauseObject2.setIndexOrKey("CREATE INDEX");
                    createIndexClauseObject2.setOn("ON");
                    createIndexClauseObject2.setTableOrView(this.tableObject);
                    final CreateIndexClause TeradataIndexClause = createIndexClauseObject2.toTeradata();
                    indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + TeradataIndexClause.toString().trim();
                }
            }
            cqs.setIndexString(indexString);
            cqs.setCreateIndexVector(null);
        }
        cqs.setTypeIdentifier(null);
        cqs.setTypeString(null);
        if (cqs.getCreateSequence() != null) {
            final CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            final CreateSequenceStatement createSequenceObj = tempCreateSequence.toTeradata();
            cqs.setCreateSequenceStatement(createSequenceObj);
        }
        if (cqs.getCreateSynonym() != null) {
            final CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
                tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }
            final String createSynonymObj = tempCreateSynonym.toTeradataString();
            cqs.setCreateSynonymString(createSynonymObj);
        }
        cqs.setPhysicalAttributesVector(null);
        cqs.setIgnoreOrReplace(null);
        cqs.setSelectStatementString(null);
        if (this.computedColumnsVector.size() >= 1) {
            this.computedColumns(cqs);
        }
        return cqs;
    }
    
    private IndexColumn createIndexColumn(final String consColName) {
        final IndexColumn indexCol = new IndexColumn();
        final SelectColumn sc = new SelectColumn();
        final Vector colExpr = new Vector();
        final TableColumn tc = new TableColumn();
        tc.setColumnName(consColName);
        colExpr.add(tc);
        sc.setColumnExpression(colExpr);
        indexCol.setIndexColumnName(sc);
        return indexCol;
    }
    
    public CreateQueryStatement copyObjectValues() {
        final Vector createColumnVector = new Vector();
        final CreateQueryStatement dupCreateQueryStatement = new CreateQueryStatement();
        dupCreateQueryStatement.setCreate(this.getCreate());
        dupCreateQueryStatement.setObjectContext(this.context);
        dupCreateQueryStatement.setCommentClass(this.commentObject);
        dupCreateQueryStatement.setCreateOrReplace(this.getCreateOrReplace());
        dupCreateQueryStatement.setTableOrView(this.getTableOrView());
        dupCreateQueryStatement.setAs(this.getAs());
        dupCreateQueryStatement.setForce(this.getForce());
        dupCreateQueryStatement.setTemp(this.getTemp());
        dupCreateQueryStatement.setOnCondition(this.getOnCondition());
        dupCreateQueryStatement.setHeap(this.getHeap());
        dupCreateQueryStatement.setDiskAttributes(this.getDiskAttributes());
        dupCreateQueryStatement.setTableObject(this.getTableObject());
        dupCreateQueryStatement.setSelectQueryStatement(this.getSelectQueryStatement());
        dupCreateQueryStatement.setWithCheckOption(this.getWithCheckOption());
        dupCreateQueryStatement.setClosedBraces(this.closedBrace);
        dupCreateQueryStatement.setOpenBraces(this.openBrace);
        dupCreateQueryStatement.setPhysicalCharacteristics(this.oraclePhysicalChar);
        dupCreateQueryStatement.setConstraint(this.getConstraint());
        dupCreateQueryStatement.setConstraintName(this.getConstraintName());
        dupCreateQueryStatement.setDatabaseObject(this.getDatabaseObject());
        dupCreateQueryStatement.setExternalTable(this.getExternalTable());
        dupCreateQueryStatement.setExternalDefaultDirectory(this.getExternalDefaultDirectory());
        dupCreateQueryStatement.setExternalDelimiter(this.getExternalDelimiter());
        dupCreateQueryStatement.setCreateIndexClause(this.getCreateIndexClause());
        dupCreateQueryStatement.setCreateIndexClauseBooleanValue(this.getCreateIndexClauseBooleanValue());
        dupCreateQueryStatement.setCreateIndexVector(this.getCreateIndexVector());
        dupCreateQueryStatement.setTypeString(this.typeString);
        dupCreateQueryStatement.setTypeIdentifier(this.typeIdentifier);
        dupCreateQueryStatement.setCreateSequenceStatement(this.getCreateSequence());
        dupCreateQueryStatement.setCreateSynonymStatement(this.getCreateSynonym());
        dupCreateQueryStatement.setOnForQuotedIdentifier(this.onConditionString);
        dupCreateQueryStatement.setQuotedIdentifierCondition(this.quotedIdentifierCondition);
        dupCreateQueryStatement.setPhysicalAttributesVector(this.getPhysicalAttributesVector());
        dupCreateQueryStatement.setIgnoreOrReplace(this.getIgnoreOrReplace());
        dupCreateQueryStatement.setSelectStatementString(this.getSelectStatementString());
        dupCreateQueryStatement.setMaterialized(this.materialized);
        dupCreateQueryStatement.setViewMetaAttribute(this.viewMetaAttribute);
        dupCreateQueryStatement.setWithReadOnly(this.withReadOnly);
        dupCreateQueryStatement.setLock(this.lock);
        dupCreateQueryStatement.setLockData(this.lockData);
        if (this.columnNames != null) {
            for (int i = 0; i < this.columnNames.size(); ++i) {
                if (this.columnNames.get(i) instanceof CreateColumn) {
                    final CreateColumn orgCreateColumn = this.columnNames.get(i);
                    if (orgCreateColumn != null) {
                        final CreateColumn newCreateColumn = orgCreateColumn.copyObjectValues();
                        createColumnVector.add(newCreateColumn);
                    }
                }
            }
        }
        dupCreateQueryStatement.setColumnNames(createColumnVector);
        return dupCreateQueryStatement;
    }
    
    private String singleQueryIntoMultipleQueriesForPLSQL() {
        return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!SwisSQLUtils.swissqlMessageList.isEmpty()) {
            sb.append("/* SwisSQL Messages :\n");
            for (int i = 0; i < SwisSQLUtils.swissqlMessageList.size(); ++i) {
                sb.append(SwisSQLUtils.swissqlMessageList.get(i).toString() + "\n");
            }
            sb.append("*/\n");
            SwisSQLUtils.swissqlMessageList.clear();
        }
        if (this.commentObject != null) {
            sb.append(this.commentObject.toString() + "\n");
        }
        if (this.createSynonymStr != null) {
            sb.append(this.createSynonymStr);
            return sb.toString();
        }
        if (this.includeDrop && this.dropString != null) {
            sb.append(this.dropString);
        }
        if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
            final String sls = this.singleQueryIntoMultipleQueriesForPLSQL();
            if (sls.indexOf("CREATE  SEQUENCE") != -1 && this.includeDrop) {
                String val = sls.substring(sls.indexOf("CREATE  SEQUENCE") + 18);
                val = val.substring(0, val.indexOf("\n"));
                final String ds = "BEGIN \nEXECUTE IMMEDIATE 'DROP SEQUENCE " + val + "';\nEXCEPTION WHEN OTHERS THEN NULL;\nEND;\n/\n\n";
                sb.append(ds);
            }
            sb.append(sls);
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
        }
        if (CreateQueryStatement.commentWhenConstraintNameTruncated != null && !CreateQueryStatement.commentWhenConstraintNameTruncated.equals("")) {
            sb.append("\n" + CreateQueryStatement.commentWhenConstraintNameTruncated + "\n\n");
        }
        if (this.createString != null) {
            sb.append(this.createString.toUpperCase() + " ");
        }
        if (this.createOrReplace != null) {
            sb.append(this.createOrReplace + " ");
        }
        if (this.force != null) {
            sb.append(this.force + " ");
        }
        if (this.temporary != null) {
            sb.append(this.temporary + " ");
        }
        if (this.materialized != null) {
            sb.append(this.materialized.toUpperCase() + " ");
        }
        if (this.externalTable != null) {
            sb.append(this.externalTable.toUpperCase() + " ");
        }
        if (this.tableOrView != null) {
            sb.append(this.tableOrView.toUpperCase() + " ");
        }
        if (this.tableObject != null) {
            this.tableObject.setObjectContext(this.context);
            sb.append(this.tableObject.toString() + " ");
            if (this.noLogConditionForTeradata != null) {
                sb.append(this.noLogConditionForTeradata + "\n");
            }
            else {
                sb.append(" \n");
            }
        }
        if (this.startWith != null && this.startWithFunction != null) {
            sb.append(" " + this.startWith + " ");
            sb.append(this.startWithFunction.toString() + "\n");
        }
        if (this.nextString != null && this.selectColumnInNextClause != null) {
            sb.append("  " + this.nextString + " ");
            sb.append(this.selectColumnInNextClause.toString() + "\n");
        }
        if (this.withSchemaBinding != null) {
            sb.append(" " + this.withSchemaBinding + " ");
        }
        if (this.onConditionForANSI != null) {
            sb.append(this.onConditionForANSI + " \n\t");
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.constraint != null) {
            sb.append("\n\t" + this.constraint.toUpperCase());
        }
        if (this.constraintName != null) {
            if (this.context != null) {
                final String temp = this.context.getEquivalent(this.constraintName).toString();
                sb.append(" " + temp);
            }
            else {
                sb.append(" " + this.constraintName);
            }
        }
        if (this.columnNames != null) {
            for (int i = 0; i < this.columnNames.size(); ++i) {
                if (this.columnNames.get(i) instanceof CreateColumn) {
                    final CreateColumn createColumn = this.columnNames.get(i);
                    createColumn.setObjectContext(this.context);
                    if (i == 0) {
                        sb.append("\n\t" + createColumn.toString());
                    }
                    else {
                        sb.append(", \n\t" + createColumn.toString());
                    }
                }
            }
        }
        if (this.createIndexClause != null) {
            this.createIndexClause.setObjectContext(this.context);
            sb.append(this.createIndexClause.toString());
        }
        if (this.createIndexVector != null) {
            for (int i = 0; i < this.createIndexVector.size(); ++i) {
                final CreateIndexClause tempCreateIndexClause = this.createIndexVector.get(i);
                if (this.createIndexClauseBooleanValue) {
                    sb.append(",\n\t");
                }
                tempCreateIndexClause.setObjectContext(this.context);
                sb.append(tempCreateIndexClause.toString().trim());
            }
        }
        if (this.mysqlCommentTableOption != null) {
            sb.append("\n" + this.mysqlCommentTableOption + "\n");
        }
        if (this.closedBrace != null) {
            sb.append("\n" + this.closedBrace + "\n");
        }
        if (this.onCondition != null) {
            sb.append("\t" + this.onCondition + " ");
        }
        if (this.organisationHeap != null) {
            sb.append("\n\t" + this.organisationHeap + " ");
        }
        if (this.onConditionString != null) {
            sb.append("\n\t" + this.onConditionString + " ");
        }
        if (this.quotedIdentifierCondition != null) {
            sb.append(this.quotedIdentifierCondition + " ");
        }
        if (this.oraclePhysicalChar != null) {
            sb.append("\n\t" + this.oraclePhysicalChar + " ");
        }
        if (this.diskAttribute != null) {
            sb.append("\n\t" + this.diskAttribute + " ");
        }
        if (this.materialized == null && this.viewMetaAttribute != null) {
            sb.append(this.viewMetaAttribute + " \n");
        }
        if (this.as != null) {
            sb.append(this.as + " \n");
        }
        if (this.selectQueryStatement != null) {
            if (this.context != null) {
                this.selectQueryStatement.setObjectContext(this.context);
            }
            if (this.openBraceForSelectQuery != null) {
                sb.append(this.openBraceForSelectQuery + " ");
            }
            sb.append(this.selectQueryStatement.toString() + " ");
            if (this.closeBraceForSelectQuery != null) {
                sb.append(this.closeBraceForSelectQuery + " ");
            }
        }
        if (this.withStatement != null) {
            sb.append(this.withStatement.toString() + " ");
        }
        if (this.withReadOnly != null) {
            sb.append(" " + this.withReadOnly);
        }
        if (this.withCheckOption != null) {
            sb.append("\n\t" + this.withCheckOption);
        }
        if (this.typeString != null) {
            sb.append("\t" + this.typeString.toUpperCase());
        }
        if (this.typeIdentifier != null) {
            sb.append(this.typeIdentifier + "\n");
        }
        if (this.ignoreOrReplace != null) {
            sb.append("\t" + this.ignoreOrReplace + " ");
        }
        if (this.selectStatementString != null) {
            sb.append(" " + this.selectStatementString + "\n");
        }
        if (this.createSequence != null) {
            this.createSequence.setObjectContext(this.context);
            sb.append(this.createSequence.toString());
        }
        if (this.physicalAttributesVector != null) {
            for (int i = 0; i < this.physicalAttributesVector.size(); ++i) {
                sb.append("\n\t" + this.physicalAttributesVector.get(i).toString());
            }
        }
        if (this.indexString != null) {
            String str = sb.toString();
            str = str.trim();
            sb.replace(0, sb.length(), str);
            sb.append(this.indexString);
        }
        if (this.ttUniqueIndexforUniqueCons.size() > 0) {
            sb.append(";\n\n/* SwisSQL Message : UNIQUE constraint is not supported in TimesTen 5.1.21. Query split into multiple queries. */\n");
            for (int i = 0; i < this.ttUniqueIndexforUniqueCons.size(); ++i) {
                final String source = this.ttUniqueIndexforUniqueCons.get(i).toString().trim();
                final StringBuffer sb2 = new StringBuffer(source);
                sb2.replace(source.length() - 1, source.length() - 1, "");
                sb.append("\n" + sb2.toString() + ";\n");
            }
        }
        if (this.indexedViewStmt != null) {
            sb.append("\nGO\n/* SwisSQL Message: Compiling the below \" CREATE UNIQUE CLUSTERED INDEX \" statement might generate compilation error. Hence execute the below \" ALTER DATABASE \" statement by specifying the database name */ \n --ALTER DATABASE <dbname> SET ARITHABORT ON \n --GO\n");
            sb.append("\n" + this.indexedViewStmt + "\n");
        }
        if (this.lock != null) {
            sb.append(" " + this.lock.toUpperCase() + " ");
        }
        if (this.lockData != null) {
            sb.append(this.lockData.toUpperCase());
        }
        if (this.comment_flag__for_create_as_select_in_timesten) {
            sb.append(";\n\n/* SwisSQL Message : 'CREATE TABLE AS SELECT' is not supported in TimesTen 5.1.21. Query split into multiple queries. */\n");
        }
        if (this.triggerForIdentity != null) {
            sb.append("\n\n/\n\n" + this.triggerForIdentity + "\n\t");
        }
        if (this.distributeOnRandomClause != null) {
            sb.append(" " + this.distributeOnRandomClause);
        }
        if (this.onConditionForTeradata != null) {
            sb.append(" " + this.onConditionForTeradata);
        }
        if (this.externalDirectory != null && this.externalDelimiter != null) {
            sb.append(" USING (dataobject('" + this.externalDirectory + "')" + " " + "DELIMITER " + "'" + this.externalDelimiter + "')  ");
        }
        else if (this.externalDirectory != null && this.externalDelimiter == null) {
            sb.append(" USING (dataobject('" + this.externalDirectory + "')" + ") ");
        }
        else if (this.externalDirectory == null && this.externalDelimiter != null) {
            sb.append(" USING (DELIMITER '" + this.externalDelimiter + "')  ");
        }
        if (this.computedColView != null) {
            sb.append(";\n" + this.computedColView.toString());
        }
        if (this.getInsertQueryStatement() != null) {
            sb = new StringBuffer(sb.toString().trim());
            sb.append(";\n\n" + this.getInsertQueryStatement().toString() + " ");
        }
        return sb.toString();
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return this.context;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    private String removeApstorphe(String str) {
        if (str.indexOf("`") != -1 || str.indexOf("'") != -1) {
            str = str.substring(1, str.length() - 1);
            return str;
        }
        return str;
    }
    
    private boolean existsPKasIndex(final CreateIndexClause createIndexClauseObject, final CreateQueryStatement cqs) {
        final Vector pkColumns = new Vector();
        final Vector colVector = cqs.getColumnNames();
        if (colVector != null) {
            for (int k = 0; k < colVector.size(); ++k) {
                Object obj = colVector.get(k);
                if (obj instanceof CreateColumn) {
                    final CreateColumn createCol = colVector.get(k);
                    final Vector constrClauseVec = createCol.getConstraintClause();
                    if (constrClauseVec != null && !constrClauseVec.isEmpty()) {
                        final ConstraintType ct = constrClauseVec.get(0).getConstraintType();
                        if (ct != null && ct instanceof PrimaryOrUniqueConstraintClause) {
                            final Vector constrColNames = ((PrimaryOrUniqueConstraintClause)ct).getConstraintColumnNames();
                            if (constrColNames != null) {
                                obj = constrColNames.get(0);
                                if (obj != null) {
                                    pkColumns.add(this.removeApstorphe((String)obj));
                                }
                            }
                        }
                    }
                }
            }
        }
        final ArrayList col = createIndexClauseObject.getIndexColumns();
        for (int j = 0; j < col.size(); ++j) {
            Object obj2 = col.get(j);
            if (obj2 instanceof IndexColumn) {
                final Vector vec = ((IndexColumn)obj2).getIndexColumnName().getColumnExpression();
                for (int l = 0; l < vec.size(); ++l) {
                    obj2 = vec.get(l);
                    if (obj2 instanceof TableColumn) {
                        final String str = this.removeApstorphe(((TableColumn)obj2).getColumnName());
                        if (pkColumns.contains(str)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isDatatypeBlobOrClob(final CreateIndexClause createIndexClauseObject, final CreateQueryStatement cqs) {
        String indexColumnName = "";
        final ArrayList col = createIndexClauseObject.getIndexColumns();
        for (int j = 0; j < col.size(); ++j) {
            final Object obj = col.get(j);
            if (obj instanceof IndexColumn) {
                indexColumnName = ((IndexColumn)obj).getIndexColumnName().toString();
                indexColumnName = this.removeApstorphe(indexColumnName);
            }
        }
        final Vector colVector = cqs.getColumnNames();
        if (colVector != null) {
            for (int k = 0; k < colVector.size(); ++k) {
                final Object obj2 = colVector.get(k);
                if (obj2 instanceof CreateColumn) {
                    final CreateColumn createCol = colVector.get(k);
                    String createColumnName = createCol.getColumnName();
                    if (createColumnName != null) {
                        createColumnName = this.removeApstorphe(createColumnName);
                        if (indexColumnName.equalsIgnoreCase(createColumnName)) {
                            final String dType = createCol.getDatatype().getDatatypeName();
                            if (dType != null && (dType.trim().equalsIgnoreCase("BLOB") || dType.trim().equalsIgnoreCase("CLOB") || dType.trim().equalsIgnoreCase("TEXT") || dType.trim().equalsIgnoreCase("MEDIUMTEXT") || dType.trim().equalsIgnoreCase("LONGTEXT"))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    static {
        CreateQueryStatement.commentWhenConstraintNameTruncated = "";
    }
}
