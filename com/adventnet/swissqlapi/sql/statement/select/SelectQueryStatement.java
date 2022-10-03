package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import java.util.List;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.insert.InsertClause;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.insert.CommonTableExpression;
import java.util.Collection;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.drop.DropStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.util.FunctionValidateHandler;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Hashtable;
import com.adventnet.swissqlapi.sql.statement.create.CreateSequenceStatement;
import java.util.ArrayList;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalHintClause;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class SelectQueryStatement implements SwisSQLStatement
{
    private UserObjectContext objectContext;
    private String create_statement;
    private SelectStatement select_statement;
    private FromClause from_clause;
    private WhereExpression whereExpression;
    private OrderByStatement order_by_statement;
    private GroupByStatement group_by_statement;
    private HavingStatement having_statement;
    private IntoStatement into_statement;
    private SetOperatorClause set_operator_clause;
    private ForUpdateStatement for_update_statement;
    private HierarchicalQueryClause hierarchical_query_clause;
    private LimitClause limit_clause;
    private FetchClause fetch_clause;
    private OptionalHintClause optionalHintClause;
    private ProcessSelectQueryForHierarchicalClause processSelectQueryForStartWithConnectBy;
    private HintClause hintClause;
    private boolean reportsMeta;
    private int SQLDialect;
    private String openBrace;
    private String closeBrace;
    private XMLStatement xmls;
    private InsertQueryStatement iqs;
    private String definitionOnly;
    private Vector computeByVector;
    private boolean commentForCompute;
    private boolean commentForOrderByStatement;
    private String generalComments;
    private String multipleQuery;
    private String atIsolation;
    private String isolationReadLevel;
    public static int beautyTabCount;
    public static String singleQueryConvertedToMultipleQueryList;
    private SelectQueryStatement subQuery;
    private DatatypeMapping mapping;
    private ArrayList lockTableStatements;
    private ArrayList lockTableList;
    private ArrayList insertValList;
    private String withString;
    private String isolationLevel;
    private CreateSequenceStatement sequenceForIdentotyFn;
    private WithStatement withStatement;
    private ArrayList createForSubQuery;
    private ArrayList insertForSubQuery;
    private ArrayList dropSttForSubQuery;
    private int sybaseTopRowCount;
    public boolean hasSubQuery;
    public boolean isStartWith;
    public String subQuery1;
    public String funcName;
    private Hashtable startWithConnectByHashtable;
    private String aliasForSubQuery;
    private boolean isOlapFunctionPresent;
    private LinkedHashMap olapDerivedTables;
    private RownumClause rownumClause;
    private boolean isSetOperatorQuery;
    private HavingStatement qualifyStatement;
    private String teradataComment;
    private boolean isRownumColumnPresent;
    private Vector tableColumnList;
    private static Vector listOfWithStatements;
    private boolean topLevel;
    private boolean converted;
    private boolean sumFunctionWithPartition;
    private LinkedHashMap sumDerivedTables;
    private LinkedHashMap sumfunc_SelectColumn_Alias_pair;
    private boolean isAmazonRedShift;
    private boolean isMSAzure;
    private boolean isOracleLive;
    private boolean canUseIfForPGCaseWhenExp;
    private boolean canUseUDFFunctionsForNumeric;
    private boolean canUseUDFFunctionsForText;
    private boolean canUseUDFFunctionsForDateTime;
    private boolean canHandleStringLiteralsForNumeric;
    private boolean canHandleStringLiteralsForDateTime;
    private boolean canHandleNullsInsideINClause;
    private boolean canCastStringLiteralToText;
    private int removalOptionForOrderAndFetchClauses;
    private boolean canUseDistinctFromForNullSafeEqualsOperator;
    private Set indexPositionsOfStringLiteralsSet;
    private Set indexPositionsOfNULLStringsSet;
    private int currentIndexPosition;
    private Set<Integer> coalesceFunctionIndexPositionSet;
    private Set<Integer> ifFunctionIndexPositionSet;
    private boolean canCastAllToTextColumns;
    private Map<String, String> aliasVsSelectColExpMap;
    private boolean canHandleHavingWithoutGroupBy;
    private boolean canUseUDFFunctionsForStrToDate;
    private boolean canHandleFunctionArgumentsCountMismatch;
    private FunctionValidateHandler validationHandler;
    private boolean canAllowLogicalExpInAggFun;
    private boolean canAllowBackTipInColumnName;
    private boolean canReplaceDoubleDotsInTableName;
    private CommentClass commentObject;
    private LinkedList list;
    
    public SelectQueryStatement() {
        this.objectContext = null;
        this.processSelectQueryForStartWithConnectBy = null;
        this.reportsMeta = false;
        this.openBrace = null;
        this.closeBrace = null;
        this.iqs = null;
        this.commentForCompute = false;
        this.commentForOrderByStatement = false;
        this.subQuery = null;
        this.lockTableStatements = new ArrayList();
        this.lockTableList = new ArrayList();
        this.insertValList = null;
        this.withString = null;
        this.isolationLevel = null;
        this.sequenceForIdentotyFn = null;
        this.withStatement = null;
        this.createForSubQuery = new ArrayList();
        this.insertForSubQuery = new ArrayList();
        this.dropSttForSubQuery = new ArrayList();
        this.sybaseTopRowCount = -1;
        this.hasSubQuery = false;
        this.isStartWith = false;
        this.subQuery1 = null;
        this.funcName = null;
        this.startWithConnectByHashtable = null;
        this.aliasForSubQuery = null;
        this.isOlapFunctionPresent = false;
        this.olapDerivedTables = new LinkedHashMap();
        this.rownumClause = null;
        this.isSetOperatorQuery = false;
        this.qualifyStatement = null;
        this.tableColumnList = new Vector();
        this.topLevel = false;
        this.converted = false;
        this.sumFunctionWithPartition = false;
        this.sumDerivedTables = new LinkedHashMap();
        this.sumfunc_SelectColumn_Alias_pair = new LinkedHashMap();
        this.isAmazonRedShift = false;
        this.isMSAzure = true;
        this.isOracleLive = false;
        this.canUseIfForPGCaseWhenExp = false;
        this.canUseUDFFunctionsForNumeric = false;
        this.canUseUDFFunctionsForText = false;
        this.canUseUDFFunctionsForDateTime = false;
        this.canHandleStringLiteralsForNumeric = false;
        this.canHandleStringLiteralsForDateTime = false;
        this.canHandleNullsInsideINClause = false;
        this.canCastStringLiteralToText = false;
        this.removalOptionForOrderAndFetchClauses = -1;
        this.canUseDistinctFromForNullSafeEqualsOperator = false;
        this.indexPositionsOfStringLiteralsSet = new HashSet();
        this.indexPositionsOfNULLStringsSet = new HashSet();
        this.currentIndexPosition = -1;
        this.coalesceFunctionIndexPositionSet = new HashSet<Integer>();
        this.ifFunctionIndexPositionSet = new HashSet<Integer>();
        this.canCastAllToTextColumns = false;
        this.aliasVsSelectColExpMap = new HashMap<String, String>();
        this.canHandleHavingWithoutGroupBy = false;
        this.canUseUDFFunctionsForStrToDate = false;
        this.canHandleFunctionArgumentsCountMismatch = false;
        this.validationHandler = null;
        this.canAllowLogicalExpInAggFun = false;
        this.canAllowBackTipInColumnName = true;
        this.canReplaceDoubleDotsInTableName = true;
        this.list = null;
        this.SQLDialect = 5;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentObject) {
        this.commentObject = commentObject;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentObject;
    }
    
    public void setReportsMeta(final boolean reportsMeta) {
        this.reportsMeta = reportsMeta;
    }
    
    public boolean getReportsMeta() {
        return this.reportsMeta;
    }
    
    public void setTeradataComment(final String comment) {
        this.teradataComment = comment;
    }
    
    public void setSQLDialect(final int i_sqldialect) {
        this.SQLDialect = i_sqldialect;
    }
    
    public void setCreateStatement(final String s_cs) {
        this.create_statement = s_cs;
    }
    
    public void setSelectStatement(final SelectStatement s) {
        this.select_statement = s;
    }
    
    public void setFromClause(final FromClause fc) {
        this.from_clause = fc;
    }
    
    public void setWhereExpression(final WhereExpression we) {
        this.whereExpression = we;
    }
    
    public void setOrderByStatement(final OrderByStatement ordst_os) {
        this.order_by_statement = ordst_os;
    }
    
    public void setGroupByStatement(final GroupByStatement gpst_gbs) {
        this.group_by_statement = gpst_gbs;
    }
    
    public void setHavingStatement(final HavingStatement havstat_hs) {
        this.having_statement = havstat_hs;
    }
    
    public void setIntoStatement(final IntoStatement is_sis) {
        this.into_statement = is_sis;
    }
    
    public void setXMLStatements(final XMLStatement xmls) {
        this.xmls = xmls;
    }
    
    public XMLStatement getXMLStatements() {
        return this.xmls;
    }
    
    public void setSetOperatorClause(final SetOperatorClause sop_so) {
        this.set_operator_clause = sop_so;
    }
    
    public void setForUpdateStatement(final ForUpdateStatement fus_fu) {
        this.for_update_statement = fus_fu;
    }
    
    public void setHierarchicalQueryClause(final HierarchicalQueryClause hqc_hq) {
        this.hierarchical_query_clause = hqc_hq;
    }
    
    public void setLimitClause(final LimitClause lc_l) {
        this.limit_clause = lc_l;
    }
    
    public void setFetchClause(final FetchClause fc_f) {
        this.fetch_clause = fc_f;
    }
    
    public void setOpenBrace(final String str) {
        this.openBrace = str;
    }
    
    public void setCloseBrace(final String str) {
        this.closeBrace = str;
    }
    
    public void setOptionalHintClause(final OptionalHintClause optionalHintClauseObj) {
        this.optionalHintClause = optionalHintClauseObj;
    }
    
    public void setDefinitionOnly(final String definitionOnly) {
        this.definitionOnly = definitionOnly;
    }
    
    public void setInsertQueryStatement(final InsertQueryStatement iqs) {
        this.iqs = iqs;
    }
    
    public void setComputeByStatements(final Vector computeByVector) {
        this.computeByVector = computeByVector;
    }
    
    public void setCommentForCompute(final boolean commentForCompute) {
        this.commentForCompute = commentForCompute;
    }
    
    public void setCommentForOrderByStatement(final boolean commentForOrderByStatement) {
        this.commentForOrderByStatement = commentForOrderByStatement;
    }
    
    public void setSubQuery(final SelectQueryStatement sqs) {
        this.subQuery = sqs;
    }
    
    public void setAliasForSubQuery(final String s) {
        this.aliasForSubQuery = s;
    }
    
    public void setDatatypeMapping(final DatatypeMapping mapping) {
        this.mapping = mapping;
    }
    
    public void setLockTableStatements(final ArrayList lockTableStatements) {
        this.lockTableStatements = lockTableStatements;
    }
    
    public void addLockTableList(final String lockTableStt) {
        this.lockTableList.add(lockTableStt);
    }
    
    public void setGeneralComments(final String generalComments) {
        this.generalComments = generalComments;
    }
    
    public void setMultipleQuery(final String multipleQuery) {
        this.multipleQuery = multipleQuery;
    }
    
    public void addCreateForSubQuery(final CreateQueryStatement cqs) {
        this.createForSubQuery.add(cqs);
    }
    
    public void addInsertForSubQuery(final InsertQueryStatement iqs) {
        this.insertForSubQuery.add(iqs);
    }
    
    public void addDropSttForSubQuery(final DropStatement dropStt) {
        this.dropSttForSubQuery.add(dropStt);
    }
    
    public void setAtIsolation(final String atIsolation) {
        this.atIsolation = atIsolation;
    }
    
    public void setIsolationReadLevel(final String isolationReadLevel) {
        this.isolationReadLevel = isolationReadLevel;
    }
    
    public void setOlapFunctionPresent(final boolean present) {
        this.isOlapFunctionPresent = present;
    }
    
    public void addOlapDerivedTables(final String partitionKey, final FromTable ft) {
        this.olapDerivedTables.put(partitionKey, ft);
    }
    
    public void setRownumClause(final RownumClause rownumClause) {
        this.rownumClause = rownumClause;
    }
    
    public void setRownumColumnPresent(final boolean yes) {
        this.isRownumColumnPresent = yes;
    }
    
    public void setQualifyStatement(final HavingStatement qualifyStatement) {
        this.qualifyStatement = qualifyStatement;
    }
    
    public void setSetOperatorQuery(final boolean yes) {
        this.isSetOperatorQuery = yes;
    }
    
    public boolean isSetOperatorSelectQueryStatement() {
        return this.isSetOperatorQuery;
    }
    
    public void setSybaseTopRowCount(final int i) {
        this.sybaseTopRowCount = i;
    }
    
    public void setHintClause(final HintClause hintClause) {
        this.hintClause = hintClause;
    }
    
    public void setConverted(final boolean queryConverted) {
        this.converted = queryConverted;
    }
    
    public void setAmazonRedShiftFlag(final boolean isAmazonRedShift) {
        this.isAmazonRedShift = isAmazonRedShift;
    }
    
    public void setMSAzureFlag(final boolean isMSAzure) {
        this.isMSAzure = isMSAzure;
    }
    
    public void setOracleLiveFlag(final boolean isOracleLive) {
        this.isOracleLive = isOracleLive;
    }
    
    public void setCanUseIFFunctionForPGCaseWhenExp(final boolean canUseIFFunction) {
        this.canUseIfForPGCaseWhenExp = canUseIFFunction;
    }
    
    public boolean canUseIFFunctionForPGCaseWhenExp() {
        return this.canUseIfForPGCaseWhenExp;
    }
    
    public void setRemovalOptionForOrderAndFetchClauses(final int optionValue) {
        this.removalOptionForOrderAndFetchClauses = optionValue;
    }
    
    public int getRemovalOptionForOrderAndFetchClauses() {
        return this.removalOptionForOrderAndFetchClauses;
    }
    
    public void setCanUseUDFFunctionsForNumeric(final boolean canUseUDFFunction) {
        this.canUseUDFFunctionsForNumeric = canUseUDFFunction;
    }
    
    public void setCanUseUDFFunctionsForText(final boolean canUseUDFFunction) {
        this.canUseUDFFunctionsForText = canUseUDFFunction;
    }
    
    public void setCanUseUDFFunctionsForDateTime(final boolean canUseUDFFunction) {
        this.canUseUDFFunctionsForDateTime = canUseUDFFunction;
    }
    
    public void setCanHandleStringLiteralsForNumeric(final boolean canHandleStringLiteralsForNumeric) {
        this.canHandleStringLiteralsForNumeric = canHandleStringLiteralsForNumeric;
    }
    
    public void setCanHandleStringLiteralsForDateTime(final boolean canHandleStringLiteralsForDateTime) {
        this.canHandleStringLiteralsForDateTime = canHandleStringLiteralsForDateTime;
    }
    
    public void setCanHandleNullsInsideINClause(final boolean canHandleNullsInsideINClause) {
        this.canHandleNullsInsideINClause = canHandleNullsInsideINClause;
    }
    
    public void setCanCastStringLiteralToText(final boolean canCastStringLiteralToText) {
        this.canCastStringLiteralToText = canCastStringLiteralToText;
    }
    
    public void setCanUseDistinctFromForNullSafeEqualsOperator(final boolean canUseDistinctFromForNullSafeEqualsOperator) {
        this.canUseDistinctFromForNullSafeEqualsOperator = canUseDistinctFromForNullSafeEqualsOperator;
    }
    
    public void setCanUseUDFFunctionsForStrToDate(final boolean canUseUDFFunctionsForStrToDate) {
        this.canUseUDFFunctionsForStrToDate = canUseUDFFunctionsForStrToDate;
    }
    
    public boolean canUseUDFFunctionsForStrToDate() {
        return this.canUseUDFFunctionsForStrToDate;
    }
    
    public void setCurrentIndexPosition(final int indexPosition) {
        this.currentIndexPosition = indexPosition;
    }
    
    public void addCurrentIndexToCoalesceFunctionList() {
        if (this.currentIndexPosition != -1) {
            this.coalesceFunctionIndexPositionSet.add(this.currentIndexPosition);
        }
    }
    
    public void setCanCastAllToTextColumns(final boolean flag) {
        this.canCastAllToTextColumns = flag;
    }
    
    public void setCanAllowBackTipInColumnName(final boolean canAllowBackTip) {
        this.canAllowBackTipInColumnName = canAllowBackTip;
    }
    
    public void setCanReplaceDoubleDotsInTableName(final boolean canReplaceDoubleDots) {
        this.canReplaceDoubleDotsInTableName = canReplaceDoubleDots;
    }
    
    public boolean canCastAllToTextColumns() {
        return this.canCastAllToTextColumns;
    }
    
    public void addCurrentIndexToIfFunctionList() {
        if (this.currentIndexPosition != -1) {
            this.ifFunctionIndexPositionSet.add(this.currentIndexPosition);
        }
    }
    
    public int getCurrentIndexPosition() {
        return this.currentIndexPosition;
    }
    
    public Set<Integer> getCoalesceFunctionIndexList() {
        return this.coalesceFunctionIndexPositionSet;
    }
    
    public Set<Integer> getIfFunctionListIndexList() {
        return this.ifFunctionIndexPositionSet;
    }
    
    public boolean canUseDistinctFromForNullSafeEqualsOperator() {
        return this.canUseDistinctFromForNullSafeEqualsOperator;
    }
    
    public boolean canUseUDFFunctionsForNumeric() {
        return this.canUseUDFFunctionsForNumeric;
    }
    
    public boolean canUseUDFFunctionsForText() {
        return this.canUseUDFFunctionsForText;
    }
    
    public boolean canUseUDFFunctionsForDateTime() {
        return this.canUseUDFFunctionsForDateTime;
    }
    
    public boolean canHandleStringLiteralsForNumeric() {
        return this.canHandleStringLiteralsForNumeric;
    }
    
    public boolean canHandleStringLiteralsForDateTime() {
        return this.canHandleStringLiteralsForDateTime;
    }
    
    public boolean canHandleNullsInsideINClause() {
        return this.canHandleNullsInsideINClause;
    }
    
    public boolean canCastStringLiteralToText() {
        return this.canCastStringLiteralToText;
    }
    
    public boolean canHandleHavingWithoutGroupBy() {
        return this.canHandleHavingWithoutGroupBy;
    }
    
    public void setCanHandleHavingWithoutGroupBy(final boolean canHandleHavingWithoutGroupBy) {
        this.canHandleHavingWithoutGroupBy = canHandleHavingWithoutGroupBy;
    }
    
    public void setCanHandleFunctionArgumentsCountMismatch(final boolean canHandleFnArgCount) {
        this.canHandleFunctionArgumentsCountMismatch = canHandleFnArgCount;
    }
    
    public void setValidationHandler(final FunctionValidateHandler handler) {
        this.validationHandler = handler;
    }
    
    public void setcanAllowLogicalExpInAggFun(final boolean canAllowLogicalExpInAgg) {
        this.canAllowLogicalExpInAggFun = canAllowLogicalExpInAgg;
    }
    
    public Map<String, String> getAliasVsSelectColExpMap() {
        return this.aliasVsSelectColExpMap;
    }
    
    public boolean isAmazonRedShift() {
        return this.isAmazonRedShift;
    }
    
    public boolean isMSAzure() {
        return this.isMSAzure;
    }
    
    public boolean isOracleLive() {
        return this.isOracleLive;
    }
    
    public HintClause getHintClause() {
        return this.hintClause;
    }
    
    public int getSybaseTopRowCount() {
        return this.sybaseTopRowCount;
    }
    
    public ArrayList getLockTableList() {
        return this.lockTableList;
    }
    
    public String getCreateStatement() {
        return this.create_statement;
    }
    
    public SelectStatement getSelectStatement() {
        return this.select_statement;
    }
    
    public FromClause getFromClause() {
        return this.from_clause;
    }
    
    public WhereExpression getWhereExpression() {
        return this.whereExpression;
    }
    
    public OrderByStatement getOrderByStatement() {
        return this.order_by_statement;
    }
    
    public GroupByStatement getGroupByStatement() {
        return this.group_by_statement;
    }
    
    public HavingStatement getHavingStatement() {
        return this.having_statement;
    }
    
    public IntoStatement getIntoStatement() {
        return this.into_statement;
    }
    
    public SetOperatorClause getSetOperatorClause() {
        return this.set_operator_clause;
    }
    
    public ForUpdateStatement getForUpdateStatement() {
        return this.for_update_statement;
    }
    
    public HierarchicalQueryClause getHierarchicalQueryClause() {
        return this.hierarchical_query_clause;
    }
    
    public LimitClause getLimitClause() {
        return this.limit_clause;
    }
    
    public FetchClause getFetchClause() {
        return this.fetch_clause;
    }
    
    public int getSQLDialect() {
        return this.SQLDialect;
    }
    
    public SelectQueryStatement getSubQuery() {
        return this.subQuery;
    }
    
    public String getAliasForSubQuery() {
        return this.aliasForSubQuery;
    }
    
    public DatatypeMapping getDatatypeMapping() {
        return this.mapping;
    }
    
    public String getAtIsolation() {
        return this.atIsolation;
    }
    
    public String getIsolationReadLevel() {
        return this.isolationReadLevel;
    }
    
    public boolean isOlapFunctionPresent() {
        return this.isOlapFunctionPresent;
    }
    
    public RownumClause getRownumClause() {
        return this.rownumClause;
    }
    
    public HashMap getOlapDerivedTables() {
        return this.olapDerivedTables;
    }
    
    public HavingStatement getQualifyStatement() {
        return this.qualifyStatement;
    }
    
    public boolean getCanHandleFunctionArugmentsCountMismatch() {
        return this.canHandleFunctionArgumentsCountMismatch;
    }
    
    public FunctionValidateHandler getValidationHandler() {
        return this.validationHandler;
    }
    
    public boolean getCanAllowBackTipInColumnName() {
        return this.canAllowBackTipInColumnName;
    }
    
    public boolean getcanAllowLogicalExpInAggFun() {
        return this.canAllowLogicalExpInAggFun;
    }
    
    public boolean getCanReplaceDoubleDotsInTableName() {
        return this.canReplaceDoubleDotsInTableName;
    }
    
    public boolean isRownumColumnPresent() {
        return this.isRownumColumnPresent;
    }
    
    public void setInsertValList(final ArrayList insertValList) {
        this.insertValList = insertValList;
    }
    
    public void setWithString(final String w) {
        this.withString = w;
    }
    
    public void setIsolationLevel(final String il) {
        this.isolationLevel = il;
    }
    
    public String getIsolationLevel() {
        return this.isolationLevel;
    }
    
    public void setSequenceForIdentityFn(final CreateSequenceStatement sequenceForIdentotyFn) {
        this.sequenceForIdentotyFn = sequenceForIdentotyFn;
    }
    
    public CreateSequenceStatement getSequenceForIdentityFn() {
        return this.sequenceForIdentotyFn;
    }
    
    public void setWithStatement(final WithStatement withStatement) {
        this.withStatement = withStatement;
    }
    
    public void setTopLevel(final boolean top) {
        this.topLevel = top;
    }
    
    public boolean getTopLevel() {
        return this.topLevel;
    }
    
    public WithStatement getWithStatement() {
        return this.withStatement;
    }
    
    public static Vector getListOfWithStatements() {
        return SelectQueryStatement.listOfWithStatements;
    }
    
    public void setStartWithConnectByHashtable(final ProcessSelectQueryForHierarchicalClause hierarchicalClause) {
        this.processSelectQueryForStartWithConnectBy = hierarchicalClause;
    }
    
    public void addTableColumnToTableColumnList(final TableColumn tcn) {
        this.tableColumnList.add(tcn);
    }
    
    public Vector getTableColumnList() {
        return this.tableColumnList;
    }
    
    public boolean isConverted() {
        return this.converted;
    }
    
    public void addSumDerivedTables(final String key, final FromTable ft) {
        this.sumDerivedTables.put(key, ft);
    }
    
    public void setSumFunctionWithPartitionAvailable(final boolean setMe) {
        this.sumFunctionWithPartition = setMe;
    }
    
    public void addSumSelectColumnAlias(final String key, final String alias) {
        this.sumfunc_SelectColumn_Alias_pair.put(key, alias);
    }
    
    public HashMap getSumDerivedTables() {
        return this.sumDerivedTables;
    }
    
    public boolean isSumFunctionWithPartitionAvailable() {
        return this.sumFunctionWithPartition;
    }
    
    public HashMap getSumSelectColumn() {
        return this.sumfunc_SelectColumn_Alias_pair;
    }
    
    private void replaceTableAliasInTableColumn(final WhereColumn wc, final String alias) throws ConvertException {
        final Vector colExp = wc.getColumnExpression();
        for (int k = 0; k < colExp.size(); ++k) {
            final Object obj = colExp.get(k);
            if (obj instanceof TableColumn) {
                final TableColumn tcnMod = (TableColumn)obj;
                final TableColumn newTCN = tcnMod.toTeradataSelect(null, null);
                if (tcnMod.getColumnName().equalsIgnoreCase("level") || tcnMod.getColumnName().equalsIgnoreCase("\"level\"")) {
                    newTCN.setTableName("PARENT1");
                }
                else {
                    newTCN.setTableName(alias);
                }
                colExp.setElementAt(newTCN, k);
            }
            else if (obj instanceof FunctionCalls) {
                final Vector funcArgs = ((FunctionCalls)obj).getFunctionArguments();
                for (int j = 0; j < funcArgs.size(); ++j) {
                    if (funcArgs.get(j) instanceof SelectColumn) {
                        this.replaceTableAliasInTableColumn(funcArgs.get(j), alias);
                    }
                }
            }
            else if (obj instanceof SelectColumn) {
                this.replaceTableAliasInTableColumn((SelectColumn)obj, alias);
            }
            else if (obj instanceof WhereColumn) {
                this.replaceTableAliasInTableColumn((WhereColumn)obj, alias);
            }
            else if (!(obj instanceof CaseStatement)) {
                if (!(obj instanceof SelectQueryStatement)) {
                    if (obj instanceof String) {}
                }
            }
        }
    }
    
    private void replaceTableAliasInTableColumn(final SelectColumn sc, final String alias) throws ConvertException {
        final Vector colExp = sc.getColumnExpression();
        for (int k = 0; k < colExp.size(); ++k) {
            final Object obj = colExp.get(k);
            if (obj instanceof TableColumn) {
                final TableColumn tcnMod = (TableColumn)obj;
                final TableColumn newTCN = tcnMod.toTeradataSelect(null, null);
                if (tcnMod.getColumnName().equalsIgnoreCase("level") || tcnMod.getColumnName().equalsIgnoreCase("\"level\"")) {
                    newTCN.setTableName("PARENT1");
                }
                else {
                    newTCN.setTableName(alias);
                }
                colExp.setElementAt(newTCN, k);
            }
            else if (obj instanceof FunctionCalls) {
                final Vector funcArgs = ((FunctionCalls)obj).getFunctionArguments();
                for (int j = 0; j < funcArgs.size(); ++j) {
                    if (funcArgs.get(j) instanceof SelectColumn) {
                        this.replaceTableAliasInTableColumn(funcArgs.get(j), alias);
                    }
                }
            }
            else if (obj instanceof SelectColumn) {
                this.replaceTableAliasInTableColumn((SelectColumn)obj, alias);
            }
            else if (!(obj instanceof CaseStatement)) {
                if (!(obj instanceof SelectQueryStatement)) {
                    if (obj instanceof String) {}
                }
            }
        }
    }
    
    private void replaceKeywordInTableColumn(final SelectColumn sc) throws ConvertException {
        final Vector colExp = sc.getColumnExpression();
        for (int k = 0; k < colExp.size(); ++k) {
            final Object obj = colExp.get(k);
            if (obj instanceof TableColumn) {
                final TableColumn tcnMod = (TableColumn)obj;
                final TableColumn newTCN = tcnMod.toTeradataSelect(null, null);
                if (tcnMod.getColumnName().equalsIgnoreCase("level") || tcnMod.getColumnName().equalsIgnoreCase("\"level\"")) {
                    newTCN.setColumnName("1");
                    newTCN.setTableName(null);
                }
                colExp.setElementAt(newTCN, k);
            }
            else if (obj instanceof FunctionCalls) {
                final Vector funcArgs = ((FunctionCalls)obj).getFunctionArguments();
                for (int j = 0; j < funcArgs.size(); ++j) {
                    if (funcArgs.get(j) instanceof SelectColumn) {
                        this.replaceKeywordInTableColumn(funcArgs.get(j));
                    }
                }
            }
            else if (obj instanceof SelectColumn) {
                this.replaceKeywordInTableColumn((SelectColumn)obj);
            }
            else if (!(obj instanceof CaseStatement)) {
                if (!(obj instanceof SelectQueryStatement)) {
                    if (obj instanceof String) {}
                }
            }
        }
    }
    
    private void replaceTableAliasInWhereExpression(final WhereExpression whereExp) throws ConvertException {
        final Vector recursiveWhereItems = whereExp.getWhereItems();
        for (int wiSize = recursiveWhereItems.size(), wi = 0; wi < wiSize; ++wi) {
            final Object obj = recursiveWhereItems.get(wi);
            if (obj instanceof WhereItem) {
                final WhereItem whereItem = (WhereItem)obj;
                final WhereColumn lft = whereItem.getLeftWhereExp();
                final WhereColumn rft = whereItem.getRightWhereExp();
                if (whereItem.getOperator3() != null && whereItem.getOperator3().equalsIgnoreCase("PRIOR")) {
                    whereItem.setOperator3(null);
                    this.replaceTableAliasInTableColumn(rft, "PARENT1");
                    this.replaceTableAliasInTableColumn(lft, "CHILD");
                }
                else if (whereItem.getOperator1() != null && whereItem.getOperator1().equalsIgnoreCase("PRIOR")) {
                    whereItem.setOperator1(null);
                    this.replaceTableAliasInTableColumn(lft, "PARENT1");
                    this.replaceTableAliasInTableColumn(rft, "CHILD");
                }
            }
            else if (obj instanceof WhereExpression) {
                this.replaceTableAliasInWhereExpression((WhereExpression)obj);
            }
        }
    }
    
    public WithStatement convertHierarchical_queryToWithStatement(final SelectQueryStatement to_sqs) throws ConvertException {
        final WhereExpression startWithCond = this.hierarchical_query_clause.getStartWithCondition().toTeradataSelect(to_sqs, this);
        final WhereExpression connectByCond = this.hierarchical_query_clause.getConnectByCondition().toTeradataSelect(to_sqs, this);
        final Vector origSelectItems = to_sqs.getSelectStatement().getSelectItemList();
        final SelectQueryStatement seedStmt = new SelectQueryStatement();
        final SelectStatement seedSelect = new SelectStatement();
        seedSelect.setSelectClause("SELECT");
        final Vector seedSelectItemsBase = new Vector();
        final Vector selectItemsForWithSQS = new Vector();
        final Vector seedSelectItems = new Vector();
        final Vector tableColumnsAddedToSeedStmt = new Vector();
        for (int k = 0; k < to_sqs.getTableColumnList().size(); ++k) {
            final TableColumn tcn = to_sqs.getTableColumnList().get(k);
            final String tcnStr = tcn.toString();
            if (!tableColumnsAddedToSeedStmt.contains(tcnStr)) {
                final SelectColumn newSelectColumn = new SelectColumn();
                newSelectColumn.setAliasName(tcn.getColumnName());
                newSelectColumn.setEndsWith(",");
                final Vector newColumnExpression = new Vector();
                if (tcn.getColumnName().equalsIgnoreCase("LEVEL") || tcn.getColumnName().equalsIgnoreCase("\"LEVEL\"")) {
                    newColumnExpression.add("1");
                }
                else {
                    newColumnExpression.add(tcn);
                }
                newSelectColumn.setColumnExpression(newColumnExpression);
                seedSelectItems.add(newSelectColumn);
                seedSelectItemsBase.add(newSelectColumn);
                tableColumnsAddedToSeedStmt.add(tcnStr);
            }
        }
        for (int origSelectItemsSize = origSelectItems.size(), oi = 0; oi < origSelectItemsSize; ++oi) {
            final SelectColumn sc = origSelectItems.get(oi);
            if (sc.getEndsWith() == null) {
                sc.setEndsWith(",");
            }
            final SelectColumn newSelectColumn = new SelectColumn();
            if (sc.getAliasName() != null) {
                newSelectColumn.setAliasName(sc.getAliasName());
            }
            else if (sc.getColumnExpression().size() == 1 && sc.getColumnExpression().firstElement() instanceof TableColumn) {
                newSelectColumn.setAliasName(sc.getColumnExpression().firstElement().getColumnName());
            }
            else {
                newSelectColumn.setAliasName("ADV_ALIAS_" + oi);
            }
            newSelectColumn.setEndsWith(",");
            sc.setEndsWith(null);
            final Vector newColumnExpression = new Vector();
            String scStr = "";
            for (int colExpi = 0; colExpi < sc.getColumnExpression().size(); ++colExpi) {
                scStr = scStr + sc.getColumnExpression().get(colExpi).toString() + " ";
            }
            if (scStr.indexOf("\"level\"") != -1) {
                scStr = scStr.replaceAll("\"level\"", "1");
            }
            else if (scStr.indexOf("\"LEVEL\"") != -1) {
                scStr = scStr.replaceAll("\"LEVEL\"", "1");
            }
            else if (scStr.indexOf("level") != -1) {
                scStr = scStr.replaceAll("level", "1");
            }
            else if (scStr.indexOf("LEVEL") != -1) {
                scStr = scStr.replaceAll("LEVEL", "1");
            }
            newColumnExpression.add(scStr);
            newSelectColumn.setColumnExpression(newColumnExpression);
            if (sc.getAliasName() == null) {
                sc.setAliasName(newSelectColumn.getAliasName());
            }
            if (sc.getColumnExpression().size() > 1 || (sc.getColumnExpression().size() == 1 && !(sc.getColumnExpression().firstElement() instanceof TableColumn))) {
                seedSelectItems.add(newSelectColumn);
                seedSelectItemsBase.add(sc);
            }
            selectItemsForWithSQS.add(sc);
        }
        seedSelectItems.lastElement().setEndsWith(null);
        seedSelect.setSelectItemList(seedSelectItems);
        seedStmt.setSelectStatement(seedSelect);
        final FromClause seedFC = new FromClause();
        seedFC.setFromClause("FROM");
        final Vector seedFromItems = new Vector();
        seedFromItems.add(to_sqs.getFromClause());
        seedStmt.setFromClause(to_sqs.getFromClause());
        seedStmt.setWhereExpression(startWithCond);
        final SelectQueryStatement recursiveStatement = new SelectQueryStatement();
        final SelectStatement recursiveSelect = new SelectStatement();
        recursiveSelect.setSelectClause("SELECT");
        final Vector recursiveSelectItems = new Vector();
        for (int seedSelectItemsSize = seedSelectItemsBase.size(), oi2 = 0; oi2 < seedSelectItemsSize; ++oi2) {
            final SelectColumn sc2 = seedSelectItemsBase.get(oi2);
            final SelectColumn newSelectColumn2 = new SelectColumn();
            newSelectColumn2.setAliasName(sc2.getAliasName());
            newSelectColumn2.setEndsWith(",");
            final Vector newColumnExpression2 = new Vector();
            if (sc2.getAliasName() != null && (sc2.getAliasName().equalsIgnoreCase("LEVEL") || sc2.getAliasName().equalsIgnoreCase("\"LEVEL\""))) {
                newColumnExpression2.add("1 + " + sc2.getAliasName());
            }
            else {
                newColumnExpression2.addAll(sc2.getColumnExpression());
            }
            newSelectColumn2.setColumnExpression(newColumnExpression2);
            this.replaceTableAliasInTableColumn(newSelectColumn2, "CHILD");
            recursiveSelectItems.add(newSelectColumn2);
        }
        recursiveSelectItems.lastElement().setEndsWith(null);
        recursiveSelect.setSelectItemList(recursiveSelectItems);
        recursiveStatement.setSelectStatement(recursiveSelect);
        final FromClause recursiveFromClause = new FromClause();
        recursiveFromClause.setFromClause("FROM");
        final Vector recursiveFromItems = new Vector();
        final FromTable parentTable = new FromTable();
        parentTable.setTableName("ADV_RECURSIVE");
        parentTable.setAliasName("PARENT1");
        recursiveFromItems.add(parentTable);
        final FromTable childTable = new FromTable();
        final FromClause to_sqsFC = to_sqs.getFromClause();
        if (to_sqsFC.getFromItemList().size() > 1) {
            throw new ConvertException("Hierarchical clause referring to multiple tables is not supported. \nPlease use Oracle's Subquery factoring clause for such queries. ");
        }
        final FromTable origFT = to_sqsFC.getFromItemList().firstElement();
        childTable.setTableName(origFT.getTableName());
        childTable.setAliasName("CHILD");
        recursiveFromItems.add(childTable);
        recursiveFromClause.setFromItemList(recursiveFromItems);
        recursiveStatement.setFromClause(recursiveFromClause);
        final WhereExpression recursiveWhereExp = connectByCond;
        this.replaceTableAliasInWhereExpression(recursiveWhereExp);
        recursiveStatement.setWhereExpression(recursiveWhereExp);
        final SetOperatorClause withSOC = new SetOperatorClause();
        withSOC.setSelectQueryStatement(recursiveStatement);
        withSOC.setSetClause("UNION ALL");
        seedStmt.setSetOperatorClause(withSOC);
        final WithStatement ws = new WithStatement();
        ws.setWith("WITH");
        final CommonTableExpression cte = new CommonTableExpression();
        final TableObject tableObject = new TableObject();
        tableObject.setTableName("ADV_RECURSIVE");
        cte.setViewName(tableObject);
        final ArrayList columnNamesList = new ArrayList();
        columnNamesList.add("(");
        for (int si = 0; si < seedSelectItems.size(); ++si) {
            final SelectColumn seedSC = seedSelectItems.get(si);
            columnNamesList.add(seedSC.getAliasName());
            if (si != seedSelectItems.size() - 1) {
                columnNamesList.add(",");
            }
        }
        columnNamesList.add(")");
        cte.setColumnList(columnNamesList);
        cte.setAs("AS");
        cte.setSelectQueryStatement(seedStmt);
        final Vector cteList = new Vector();
        cteList.add(cte);
        ws.setCommonTableExpressionList(cteList);
        final SelectQueryStatement withSQS = new SelectQueryStatement();
        final SelectStatement withSelect = new SelectStatement();
        withSelect.setSelectClause("SELECT");
        final Vector withSelectItems = new Vector();
        for (int e = 0; e < selectItemsForWithSQS.size(); ++e) {
            final String obj = selectItemsForWithSQS.get(e).getAliasName();
            if (!obj.equalsIgnoreCase(",") && !obj.equalsIgnoreCase("(") && !obj.equalsIgnoreCase(")")) {
                final SelectColumn sc3 = new SelectColumn();
                final Vector v = new Vector();
                v.add(obj);
                sc3.setColumnExpression(v);
                sc3.setEndsWith(",");
                withSelectItems.add(sc3);
            }
        }
        withSelectItems.lastElement().setEndsWith(null);
        withSelect.setSelectItemList(withSelectItems);
        withSQS.setSelectStatement(withSelect);
        final FromClause withFC = new FromClause();
        withFC.setFromClause("FROM");
        final Vector withFCItems = new Vector();
        final FromTable withFT = new FromTable();
        withFT.setTableName("ADV_RECURSIVE");
        withFCItems.add(withFT);
        withFC.setFromItemList(withFCItems);
        withSQS.setFromClause(withFC);
        ws.setWithSQS(withSQS);
        return ws;
    }
    
    public WithStatement convertHierarchical_queryToWithStatement() {
        String tableName = null;
        String columnName = null;
        final FromClause fromclause = this.getFromClause();
        final Vector tableItems = fromclause.getFromItemList();
        if (tableItems != null && tableItems.size() > 0) {
            final FromTable fromTableName = tableItems.get(0);
            tableName = fromTableName.getTableName().toString();
        }
        final SelectStatement selectStatement = this.getSelectStatement();
        final Vector selectItemList = selectStatement.getSelectItemList();
        final ArrayList newSelectItemsList = new ArrayList();
        for (int i = 0; i < selectItemList.size(); ++i) {
            columnName = selectItemList.get(i).toString();
            newSelectItemsList.add(columnName);
        }
        final HierarchicalQueryClause hierarchyClause = this.getHierarchicalQueryClause();
        final String startWithCondition = hierarchyClause.getStartWithCondition().toString();
        final WhereExpression connectByCondition = hierarchyClause.getConnectByCondition();
        final Vector connectByConditionVector = connectByCondition.getWhereItem();
        final WhereItem connectByConditionChoose = connectByConditionVector.get(0);
        String selectClause1 = " ";
        for (int j = 0; j < newSelectItemsList.size(); ++j) {
            final Object obj_1 = newSelectItemsList.get(j);
            if (obj_1.toString().trim().equalsIgnoreCase("LEVEL")) {
                selectClause1 += "1";
            }
            else if (obj_1.toString().trim().equalsIgnoreCase("LEVEL,")) {
                selectClause1 += "1,";
            }
            else {
                selectClause1 = selectClause1 + " PARENT." + newSelectItemsList.get(j);
            }
        }
        final String whereClause1 = " PARENT." + startWithCondition;
        String selectClause2 = "";
        for (int k = 0; k < newSelectItemsList.size(); ++k) {
            final Object obj_2 = newSelectItemsList.get(k);
            if (obj_2.toString().trim().equalsIgnoreCase("LEVEL")) {
                selectClause2 += "LEVEL+1";
            }
            else if (obj_2.toString().trim().equalsIgnoreCase("LEVEL,")) {
                selectClause2 += "LEVEL+1,";
            }
            else {
                selectClause2 = selectClause2 + "CHILD." + newSelectItemsList.get(k);
            }
        }
        String whereClause2 = " CHILD.";
        final String leftWhere = connectByConditionChoose.getLeftWhereExp().toString();
        final String rightWhere = connectByConditionChoose.getRightWhereExp().toString();
        if (connectByConditionChoose.getOperator3() != null && connectByConditionChoose.getOperator3().toString().equalsIgnoreCase("PRIOR")) {
            whereClause2 = whereClause2 + leftWhere + " =" + " PARENT." + rightWhere;
        }
        if (connectByConditionChoose.getOperator1() != null) {
            if (connectByConditionChoose.getOperator1().toString().equalsIgnoreCase("PRIOR")) {
                whereClause2 = whereClause2 + rightWhere + " =" + " PARENT." + leftWhere;
            }
            final WithStatement ws = new WithStatement();
            final CommonTableExpression cte = new CommonTableExpression();
            final ArrayList columnNamesList = new ArrayList();
            final SetOperatorClause soc = new SetOperatorClause();
            final WhereExpression we_1 = new WhereExpression();
            final WhereItem wi_1 = new WhereItem();
            final WhereColumn wc_1 = new WhereColumn();
            final WhereExpression we_2 = new WhereExpression();
            final WhereItem wi_2 = new WhereItem();
            final Vector whereItems_1 = new Vector();
            final Vector whereItems_2 = new Vector();
            ws.setWith("WITH");
            final TableObject tableObject = new TableObject();
            tableObject.setTableName("ADV_RECURSIVE");
            cte.setViewName(tableObject);
            columnNamesList.add("(");
            final Vector temp = this.getSelectStatement().getSelectItemList();
            for (int l = 0; l < temp.size(); ++l) {
                columnNamesList.add(temp.get(l));
            }
            columnNamesList.add(")");
            cte.setColumnList(columnNamesList);
            cte.setAs("As");
            final SelectQueryStatement sqs = new SelectQueryStatement();
            final Vector v1 = new Vector();
            final Vector fromItems_1 = new Vector();
            final Vector fromItems_2 = new Vector();
            final SelectStatement ss = new SelectStatement();
            final FromClause fc = new FromClause();
            v1.add(selectClause1);
            ss.setSelectItemList(v1);
            ss.setSelectClause("SELECT");
            sqs.setSelectStatement(ss);
            fc.setFromClause("FROM");
            final FromTable ft1 = new FromTable();
            ft1.setTableName(tableName);
            ft1.setAliasName("PARENT");
            fromItems_1.add(ft1);
            fc.setFromItemList(fromItems_1);
            whereItems_1.add(whereClause1);
            we_1.setWhereItem(whereItems_1);
            sqs.setFromClause(fc);
            sqs.setWhereExpression(we_1);
            soc.setSetClause("UNION ALL");
            final SelectQueryStatement sqs2 = new SelectQueryStatement();
            final SelectStatement ss2 = new SelectStatement();
            final FromClause fc2 = new FromClause();
            final FromTable ft2 = new FromTable();
            final FromTable ft3 = new FromTable();
            ss2.setSelectClause("SELECT");
            final Vector v2 = new Vector();
            v2.add(selectClause2);
            ss2.setSelectItemList(v2);
            sqs2.setSelectStatement(ss2);
            ft2.setTableName("ADV_RECURSIVE");
            ft2.setAliasName("PARENT");
            fromItems_2.add(ft2);
            ft3.setTableName(tableName);
            ft3.setAliasName("CHILD");
            fromItems_2.add(ft3);
            fc2.setFromClause("FROM");
            fc2.setFromItemList(fromItems_2);
            sqs2.setFromClause(fc2);
            whereItems_2.add(whereClause2);
            we_2.setWhereItem(whereItems_2);
            sqs2.setWhereExpression(we_2);
            soc.setSelectQueryStatement(sqs2);
            sqs.setSetOperatorClause(soc);
            final SelectQueryStatement with_sqs = new SelectQueryStatement();
            final FromClause fc3 = new FromClause();
            final Vector fromItems_3 = new Vector();
            fc3.setFromClause("FROM");
            final FromTable ft4 = new FromTable();
            ft4.setTableName("ADV_RECURSIVE");
            with_sqs.setSelectStatement(this.getSelectStatement());
            cte.setSelectQueryStatement(sqs);
            final Vector cteVector = new Vector();
            cteVector.add(cte);
            ws.setCommonTableExpressionList(cteVector);
            fromItems_3.add(ft4);
            fc3.setFromItemList(fromItems_3);
            with_sqs.setFromClause(fc3);
            final WhereExpression we = this.getWhereExpression();
            with_sqs.setWhereExpression(this.getWhereExpression());
            with_sqs.setOrderByStatement(this.getOrderByStatement());
            with_sqs.setIntoStatement(this.getIntoStatement());
            ws.setWithSQS(with_sqs);
            return ws;
        }
        return null;
    }
    
    public ProcessSelectQueryForHierarchicalClause handleStartWithConnectByClause() {
        final Hashtable hashTable = new Hashtable();
        hashTable.put("$name$", "Anon");
        final SelectStatement tsqlSelectStatement = this.getSelectStatement();
        final Vector selectItems = tsqlSelectStatement.getSelectItemList();
        if (selectItems.size() == 1) {
            String columnName = null;
            String tableReference = null;
            String startWithAliasName = null;
            final SelectColumn selectColumn = selectItems.get(0);
            final Vector expression = selectColumn.getColumnExpression();
            if (expression.get(0) instanceof TableColumn) {
                final TableColumn tableColumn = expression.get(0);
                columnName = tableColumn.getColumnName();
                tableReference = tableColumn.getTableName();
                if (tableReference != null && !tableReference.trim().equals("")) {
                    tableReference = tableReference.toLowerCase();
                }
                if (selectColumn.getAliasName() != null && !selectColumn.getAliasName().trim().equals("")) {
                    startWithAliasName = selectColumn.getAliasName();
                }
                else {
                    startWithAliasName = columnName;
                }
            }
            if (columnName != null) {
                hashTable.put("STARTWITH_DISTINCT_COLUMNAME", columnName);
            }
            if (tableReference != null) {
                hashTable.put("STARTWITH_DISTINCT_TABREF", tableReference);
            }
            if (startWithAliasName != null) {
                hashTable.put("STARTWITH_DISTINCT_ALIASNAME", startWithAliasName);
            }
            final FromClause startwithtsqlFromClause = this.getFromClause();
            final Vector startWithFromItems = startwithtsqlFromClause.getFromItemList();
            final ArrayList startWithTableList = new ArrayList();
            final ArrayList startWithAliasList = new ArrayList();
            if (startWithFromItems != null) {
                for (int i = 0; i < startWithFromItems.size(); ++i) {
                    final FromTable startWithtsqlFromTable = startWithFromItems.get(i);
                    Object startWithTableName = startWithtsqlFromTable.getTableName();
                    if (startWithTableName instanceof String) {
                        if (((String)startWithTableName).indexOf(46) != -1) {
                            startWithTableName = ((String)startWithTableName).substring(((String)startWithTableName).lastIndexOf(46) + 1);
                        }
                        startWithTableList.add(startWithTableName);
                        if (startWithtsqlFromTable.getAliasName() != null && !startWithtsqlFromTable.getAliasName().trim().equals("")) {
                            startWithAliasList.add(startWithtsqlFromTable.getAliasName().toLowerCase());
                        }
                        else {
                            startWithAliasList.add(((String)startWithTableName).toLowerCase());
                        }
                    }
                }
                hashTable.put("STARTWITH_TABLE_LIST", startWithTableList);
                hashTable.put("STARTWITH_TABLE_ALIAS_LIST", startWithAliasList);
            }
        }
        final ProcessSelectQueryForHierarchicalClause processSelectQueryForHierarchicalClause = new ProcessSelectQueryForHierarchicalClause();
        hashTable.put("ORIGINALSWISSQLSTMT", this);
        processSelectQueryForHierarchicalClause.processSelectQueryWhenTreeIsEncountered(this, hashTable);
        this.startWithConnectByHashtable = processSelectQueryForHierarchicalClause.getStartWithConnectByHashtable();
        this.hasSubQuery = processSelectQueryForHierarchicalClause.hasSubQuery;
        this.subQuery1 = processSelectQueryForHierarchicalClause.subQuery;
        this.funcName = processSelectQueryForHierarchicalClause.funcName;
        return processSelectQueryForHierarchicalClause;
    }
    
    public boolean checkForPriorinHierarchyClause() {
        final HierarchicalQueryClause hierarchyClause = this.getHierarchicalQueryClause();
        final String startWithCondition = hierarchyClause.getStartWithCondition().toString();
        final WhereExpression connectByCondition = hierarchyClause.getConnectByCondition();
        final Vector connectByConditionVector = connectByCondition.getWhereItem();
        final WhereItem connectByConditionChoose = connectByConditionVector.get(0);
        return connectByConditionChoose.getOperator1() != null || connectByConditionChoose.getOperator2() != null || connectByConditionChoose.getOperator3() != null;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (!SwisSQLUtils.swissqlMessageList.isEmpty()) {
            sb.append("/* SwisSQL Messages :\n");
            for (int i = 0; i < SwisSQLUtils.swissqlMessageList.size(); ++i) {
                sb.append(SwisSQLUtils.swissqlMessageList.get(i).toString() + "\n");
            }
            sb.append("*/\n");
            SwisSQLUtils.swissqlMessageList.clear();
        }
        if (this.commentObject != null) {
            final String commentStr = this.commentObject.toString().trim();
            sb.append(commentStr + "\n");
        }
        if (this.teradataComment != null) {
            sb.append(this.teradataComment + "\n");
        }
        if (this.hintClause != null) {
            sb.append(" " + this.hintClause);
        }
        if (this.topLevel && getListOfWithStatements().size() > 0) {
            for (int wiSize = getListOfWithStatements().size(), wi = 0; wi < wiSize; ++wi) {
                sb.append(getListOfWithStatements().get(wi).toString() + "\n");
                if (wi != wiSize - 1) {
                    sb.append(",");
                }
            }
        }
        else if (this.withStatement != null) {
            return this.withStatement.toString();
        }
        if (this.processSelectQueryForStartWithConnectBy != null && this.processSelectQueryForStartWithConnectBy.startWithConnectByHash != null) {
            this.startWithConnectByHashtable = this.processSelectQueryForStartWithConnectBy.startWithConnectByHash;
            final String tempFuncName = this.processSelectQueryForStartWithConnectBy.funcName;
            final String tempAnonFuncName = this.processSelectQueryForStartWithConnectBy.anonFunName;
            if (this.startWithConnectByHashtable.get(tempFuncName) != null) {
                sb.append(this.startWithConnectByHashtable.get(tempFuncName).toString());
                if (this.startWithConnectByHashtable.get(tempAnonFuncName) != null) {
                    sb.append(this.startWithConnectByHashtable.get(tempAnonFuncName).toString());
                }
            }
        }
        if (this.getSybaseTopRowCount() != -1) {
            sb.append("SET ROWCOUNT " + this.getSybaseTopRowCount() + " \n");
        }
        for (int k = 0; k < this.lockTableList.size(); ++k) {
            sb.append(this.lockTableList.get(k).toString() + ";\n");
        }
        for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
            sb.append("\t");
        }
        if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
            sb.append(this.singleQueryIntoMultipleQueriesForPLSQL());
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.sequenceForIdentotyFn != null) {
            try {
                sb.append(this.sequenceForIdentotyFn.toOracle().toString() + " \n/\n");
            }
            catch (final Exception ex) {}
        }
        if (this.create_statement != null && this.select_statement == null) {
            sb.append(this.create_statement.toString().trim() + ";");
        }
        else if (this.create_statement != null) {
            sb.append(this.create_statement + " ");
        }
        if (this.createForSubQuery.size() > 0) {
            for (int i = 0; i < this.createForSubQuery.size(); ++i) {
                sb.append(this.createForSubQuery.get(i).toString().trim() + ";\n\n");
            }
        }
        if (this.insertForSubQuery.size() > 0) {
            for (int i = 0; i < this.insertForSubQuery.size(); ++i) {
                sb.append(this.insertForSubQuery.get(i).toString().trim() + ";\n\n");
            }
        }
        if (this.select_statement != null) {
            this.select_statement.setObjectContext(this.objectContext);
            sb.append(this.select_statement.toString());
        }
        if (this.into_statement != null) {
            this.into_statement.setObjectContext(this.objectContext);
            sb.append(" " + this.into_statement.toString());
        }
        sb.append("\n");
        if (this.from_clause != null && this.subQuery != null) {
            sb.append(" FROM\n");
        }
        else if (this.from_clause != null) {
            this.from_clause.setObjectContext(this.objectContext);
            sb.append(this.from_clause.toString() + " \n");
        }
        if (this.subQuery != null) {
            sb.append("(" + this.subQuery.toString() + ")");
        }
        if (this.aliasForSubQuery != null) {
            sb.append(" " + this.aliasForSubQuery + " ");
        }
        if (this.whereExpression != null) {
            final StringBuffer string_buffer = new StringBuffer();
            for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                sb.append("\t");
            }
            if (this.whereExpression.getConcatenation() != null) {
                string_buffer.append("+ ");
            }
            if (this.whereExpression.getCommentClass() != null) {
                string_buffer.append(this.whereExpression.getCommentClass().toString().trim() + " ");
            }
            string_buffer.append("WHERE");
            string_buffer.append("\t");
            this.whereExpression.setObjectContext(this.objectContext);
            if (this.whereExpression.toString() != null && this.whereExpression.toString().indexOf("AND") == 0) {
                string_buffer.append(StringFunctions.replaceFirst(" ", "AND", this.whereExpression.toString()));
            }
            else {
                string_buffer.append(" " + this.whereExpression.toString());
            }
            if (!string_buffer.toString().trim().equalsIgnoreCase("WHERE")) {
                sb.append(string_buffer.toString());
                sb.append("\n");
            }
        }
        if (this.hierarchical_query_clause != null) {
            sb.append(this.hierarchical_query_clause.toString() + " \n");
        }
        if (this.group_by_statement != null) {
            if (this.group_by_statement.getDescOption() != null && this.group_by_statement.getDescOption().equalsIgnoreCase("desc")) {
                this.group_by_statement.setDescOption(null);
            }
            this.group_by_statement.setObjectContext(this.objectContext);
            sb.append(this.group_by_statement.toString() + " \n");
        }
        if (this.having_statement != null) {
            this.having_statement.setObjectContext(this.objectContext);
            sb.append(this.having_statement.toString() + " \n");
        }
        if (this.qualifyStatement != null) {
            sb.append(this.qualifyStatement.toString() + " \n");
        }
        if (this.closeBrace != null) {
            sb.append(this.closeBrace);
        }
        if (this.set_operator_clause != null) {
            this.set_operator_clause.setObjectContext(this.objectContext);
            sb.append(this.set_operator_clause.toString() + " \n");
        }
        if (this.order_by_statement != null && this.subQuery == null && !this.canRemoveOrderByClause()) {
            if (this.commentForOrderByStatement) {
                for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                    sb.append("\t");
                }
                sb.append("/*SwisSQLAPI Message : Manual intervention required\n");
                for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                    sb.append("\t");
                }
            }
            this.order_by_statement.setObjectContext(this.objectContext);
            sb.append(this.order_by_statement.toString() + " \n");
            if (this.commentForOrderByStatement) {
                sb.append("*/\n");
            }
        }
        if (this.xmls != null) {
            sb.append(this.xmls.toString() + " \n");
        }
        if (this.computeByVector != null) {
            if (this.commentForCompute) {
                for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                    sb.append("\t");
                }
                sb.append("/*SwisSQLAPI Message : Manual intervention required\n");
                for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                    sb.append("\t");
                }
            }
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                for (int l = 0; l < SelectQueryStatement.beautyTabCount; ++l) {
                    sb.append("\t");
                }
                sb.append(this.computeByVector.get(i).toString() + "\n");
            }
            if (this.commentForCompute) {
                sb.append("*/\n");
            }
        }
        if (this.for_update_statement != null) {
            sb.append(this.for_update_statement.toString() + " \n");
        }
        if (this.limit_clause != null && !this.canRemoveLimitAndFetchClause()) {
            sb.append(this.limit_clause.toString() + " \n");
        }
        if (this.optionalHintClause != null) {
            sb.append(this.optionalHintClause.toString() + "\n");
        }
        if (this.fetch_clause != null && !this.canRemoveLimitAndFetchClause()) {
            sb.append(this.fetch_clause.toString() + " \n");
        }
        if (this.atIsolation != null) {
            sb.append(this.atIsolation + " READ " + this.isolationReadLevel.toUpperCase() + "\n");
        }
        if (this.definitionOnly != null) {
            sb.append(this.definitionOnly);
        }
        if (this.generalComments != null) {
            sb.append("\n" + this.generalComments + "\n");
        }
        if (this.iqs != null) {
            for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                sb.append("\t");
            }
            sb.append("\n" + this.iqs.toString().trim() + ")");
        }
        for (int n = 0; n < this.lockTableStatements.size(); ++n) {
            sb.append(this.lockTableStatements.get(n).toString() + "\n");
        }
        if (this.dropSttForSubQuery.size() > 0) {
            final StringBuffer sb2 = new StringBuffer();
            sb2.append(sb.toString().trim() + ";\n\n");
            for (int j = 0; j < this.dropSttForSubQuery.size(); ++j) {
                sb2.append(this.dropSttForSubQuery.get(j).toString().trim() + ";\n\n");
            }
            return sb2.toString();
        }
        if (this.multipleQuery != null) {
            final StringBuffer sb2 = new StringBuffer();
            sb2.append(sb.toString().trim() + ";\n");
            sb2.append(this.multipleQuery);
            return sb2.toString();
        }
        if (this.getSybaseTopRowCount() != -1) {
            sb.append("SET ROWCOUNT 0");
        }
        if (this.withString != null) {
            sb.append(this.withString + " ");
        }
        if (this.isolationLevel != null) {
            sb.append(this.isolationLevel);
        }
        if (this.commentObject != null && this.SQLDialect == 10) {
            sb.append(" " + this.commentObject);
        }
        final String returnString = sb.toString();
        return returnString;
    }
    
    @Override
    public String removeIndent(String s_ri) {
        s_ri = s_ri.replace('\n', ' ');
        s_ri = s_ri.replace('\t', ' ');
        return s_ri;
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixSelect().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServerSelect().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseSelect().toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracleSelect().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2Select().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQLSelect().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMySQLSelect().toString();
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSISelect().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataSelect().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenSelect().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaSelect().toString();
    }
    
    public SelectQueryStatement toANSISelect() throws ConvertException {
        final SelectQueryStatement sqs_ansi_sql = new SelectQueryStatement();
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(8);
        }
        sqs_ansi_sql.setCommentClass(this.commentObject);
        sqs_ansi_sql.setSelectStatement(this.select_statement.toANSISelect(sqs_ansi_sql, this));
        if (this.from_clause != null) {
            sqs_ansi_sql.setFromClause(this.from_clause.toANSISelect(sqs_ansi_sql, this));
        }
        if (this.whereExpression != null) {
            sqs_ansi_sql.setWhereExpression(this.whereExpression.toANSISelect(sqs_ansi_sql, this));
        }
        if (this.order_by_statement != null) {
            sqs_ansi_sql.setOrderByStatement(this.order_by_statement.toANSISelect(sqs_ansi_sql, this));
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                computeByStatementVector.add(this.computeByVector.get(i).toANSISelect(sqs_ansi_sql, this));
            }
            sqs_ansi_sql.setComputeByStatements(computeByStatementVector);
            sqs_ansi_sql.setCommentForCompute(true);
        }
        if (sqs_ansi_sql.getGroupByStatement() == null && this.group_by_statement != null) {
            sqs_ansi_sql.setGroupByStatement(this.group_by_statement.toANSISelect(sqs_ansi_sql, this));
        }
        if (this.set_operator_clause != null) {
            sqs_ansi_sql.setSetOperatorClause(this.set_operator_clause.toANSISelect(sqs_ansi_sql, this));
        }
        if (this.having_statement != null) {
            sqs_ansi_sql.setHavingStatement(this.having_statement.toANSISelect(sqs_ansi_sql, this));
        }
        if (this.into_statement != null) {
            throw new ConvertException();
        }
        if (this.hierarchical_query_clause != null) {
            throw new ConvertException();
        }
        if (this.limit_clause != null) {
            throw new ConvertException();
        }
        if (this.fetch_clause != null) {
            throw new ConvertException();
        }
        if (this.for_update_statement != null) {
            throw new ConvertException();
        }
        sqs_ansi_sql.setOptionalHintClause(null);
        return sqs_ansi_sql;
    }
    
    public SelectQueryStatement toTeradataSelect() throws ConvertException {
        SelectQueryStatement sqs_Teradata_sql = new SelectQueryStatement();
        sqs_Teradata_sql.setTopLevel(this.topLevel);
        sqs_Teradata_sql.setHintClause(this.hintClause);
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(12);
        }
        sqs_Teradata_sql.setCommentClass(this.commentObject);
        sqs_Teradata_sql.setSelectStatement(this.select_statement.toTeradataSelect(sqs_Teradata_sql, this));
        if (this.from_clause != null) {
            sqs_Teradata_sql.setFromClause(this.from_clause.toTeradataSelect(sqs_Teradata_sql, this));
        }
        if (this.whereExpression != null) {
            sqs_Teradata_sql.setWhereExpression(this.whereExpression.toTeradataSelect(sqs_Teradata_sql, this));
        }
        if (this.order_by_statement != null) {
            sqs_Teradata_sql.setOrderByStatement(this.order_by_statement.toTeradataSelect(sqs_Teradata_sql, this));
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                computeByStatementVector.add(this.computeByVector.get(i).toTeradataSelect(sqs_Teradata_sql, this));
            }
            sqs_Teradata_sql.setComputeByStatements(computeByStatementVector);
            sqs_Teradata_sql.setCommentForCompute(true);
        }
        if (sqs_Teradata_sql.getGroupByStatement() == null && this.group_by_statement != null) {
            sqs_Teradata_sql.setGroupByStatement(this.group_by_statement.toTeradataSelect(sqs_Teradata_sql, this));
        }
        if (this.set_operator_clause != null) {
            sqs_Teradata_sql.setSetOperatorClause(this.set_operator_clause.toTeradataSelect(sqs_Teradata_sql, this));
        }
        if (this.having_statement != null) {
            sqs_Teradata_sql.setHavingStatement(this.having_statement.toTeradataSelect(sqs_Teradata_sql, this));
        }
        if (this.isOlapFunctionPresent && this.olapDerivedTables != null && this.olapDerivedTables.size() > 0) {
            final Vector derivedTables = new Vector();
            final FromClause newFromClause = new FromClause();
            newFromClause.setFromClause("FROM");
            final Vector newFromItems = new Vector();
            newFromItems.addAll(sqs_Teradata_sql.getFromClause().getFromItemList());
            newFromClause.setFromItemList(newFromItems);
            WhereExpression newWhereExp = null;
            if (sqs_Teradata_sql.getWhereExpression() != null && sqs_Teradata_sql.getWhereExpression().hasNonNullWhereItem()) {
                newWhereExp = new WhereExpression();
                final Vector newWhereExpItems = new Vector(sqs_Teradata_sql.getWhereExpression().getWhereItem());
                final Vector newWhereExpOps = new Vector(sqs_Teradata_sql.getWhereExpression().getOperator());
                newWhereExp.setWhereItem(newWhereExpItems);
                newWhereExp.setOperator(newWhereExpOps);
            }
            final Object[] olapTables = this.olapDerivedTables.values().toArray();
            for (int j = 0; j < olapTables.length; ++j) {
                final FromTable dt = (FromTable)olapTables[j];
                ((SelectQueryStatement)dt.getTableName()).setFromClause(newFromClause);
                if (newWhereExp != null) {
                    if (((SelectQueryStatement)dt.getTableName()).getWhereExpression() == null) {
                        ((SelectQueryStatement)dt.getTableName()).setWhereExpression(newWhereExp);
                    }
                    else {
                        if (!((SelectQueryStatement)dt.getTableName()).getWhereExpression().getWhereItems().isEmpty()) {
                            ((SelectQueryStatement)dt.getTableName()).getWhereExpression().addOperator("AND");
                        }
                        ((SelectQueryStatement)dt.getTableName()).getWhereExpression().addWhereExpression(newWhereExp);
                    }
                }
                final String dtAliasName = this.getFromClause().getLastElement().getAliasName();
                if (dtAliasName != null && dtAliasName.startsWith("\"")) {
                    dt.setAliasName(dtAliasName.substring(1, dtAliasName.length() - 1) + j);
                }
                else {
                    dt.setAliasName(dtAliasName + j);
                }
                if (sqs_Teradata_sql.getWhereExpression() != null && !sqs_Teradata_sql.getWhereExpression().isThetaJoinPresent()) {
                    dt.setJoinClause(null);
                    dt.setOnOrUsingJoin(null);
                    if (!sqs_Teradata_sql.getWhereExpression().getWhereItems().isEmpty()) {
                        sqs_Teradata_sql.getWhereExpression().addOperator("AND");
                    }
                    sqs_Teradata_sql.getWhereExpression().addWhereExpression(dt.getJoinExpression().firstElement());
                    dt.setJoinExpression(null);
                }
                derivedTables.add(dt);
            }
            sqs_Teradata_sql.getFromClause().getFromItemList().addAll(derivedTables);
        }
        if (sqs_Teradata_sql.getRownumClause() != null || sqs_Teradata_sql.isRownumColumnPresent()) {
            this.handleRownumConversion(sqs_Teradata_sql);
        }
        if (this.into_statement != null) {
            throw new ConvertException();
        }
        if (this.hierarchical_query_clause != null) {
            (this.withStatement = this.convertHierarchical_queryToWithStatement(sqs_Teradata_sql)).setWith("WITH RECURSIVE ");
            sqs_Teradata_sql.setWithStatement(this.withStatement);
            if (this.withStatement == null && this.hierarchical_query_clause.getStartWithCondition() != null) {
                sqs_Teradata_sql.setWhereExpression(this.hierarchical_query_clause.getStartWithCondition());
            }
        }
        if (this.limit_clause != null) {
            throw new ConvertException();
        }
        if (this.fetch_clause != null) {
            throw new ConvertException();
        }
        if (this.for_update_statement != null) {
            throw new ConvertException();
        }
        sqs_Teradata_sql.setOptionalHintClause(null);
        final boolean crossJoinFound = this.isCrossJoinAvailable(sqs_Teradata_sql);
        if (this.isOlapFunctionPresent && crossJoinFound) {
            final String tableName = "ADVENTNET_SWISSQL1";
            SelectStatement newSelectStatement = new SelectStatement();
            FromClause fc = new FromClause();
            final Vector selectItemsForFromTable = new Vector();
            selectItemsForFromTable.addAll(sqs_Teradata_sql.getSelectStatement().getSelectItemList());
            fc = this.createFromClauseForDerivedTable(selectItemsForFromTable, sqs_Teradata_sql, tableName);
            newSelectStatement = this.createSelectStatementForDerivedTable(this.getSelectStatement(), tableName);
            sqs_Teradata_sql = new SelectQueryStatement();
            sqs_Teradata_sql.setSelectStatement(newSelectStatement);
            sqs_Teradata_sql.setFromClause(fc);
        }
        if (this.sumFunctionWithPartition) {
            final Vector selectItems = new Vector(sqs_Teradata_sql.getSelectStatement().getSelectItemList());
            final Vector derivedTables2 = new Vector();
            final FromClause newFromClause2 = new FromClause();
            newFromClause2.setFromClause("FROM");
            final Vector newFromItems2 = new Vector();
            newFromItems2.addAll(sqs_Teradata_sql.getFromClause().getFromItemList());
            newFromClause2.setFromItemList(newFromItems2);
            WhereExpression newWhereExp2 = null;
            if (sqs_Teradata_sql.getWhereExpression() != null && sqs_Teradata_sql.getWhereExpression().hasNonNullWhereItem()) {
                newWhereExp2 = new WhereExpression();
                final Vector newWhereExpItems2 = new Vector(sqs_Teradata_sql.getWhereExpression().getWhereItem());
                final Vector newWhereExpOps2 = new Vector(sqs_Teradata_sql.getWhereExpression().getOperator());
                newWhereExp2.setWhereItem(newWhereExpItems2);
                newWhereExp2.setOperator(newWhereExpOps2);
            }
            final Object[] sumTables = this.sumDerivedTables.values().toArray();
            for (int k = 0; k < sumTables.length; ++k) {
                final FromTable dt2 = (FromTable)sumTables[k];
                ((SelectQueryStatement)dt2.getTableName()).setFromClause(newFromClause2);
                if (newWhereExp2 != null) {
                    if (((SelectQueryStatement)dt2.getTableName()).getWhereExpression() == null) {
                        ((SelectQueryStatement)dt2.getTableName()).setWhereExpression(newWhereExp2);
                    }
                    else {
                        if (!((SelectQueryStatement)dt2.getTableName()).getWhereExpression().getWhereItems().isEmpty()) {
                            ((SelectQueryStatement)dt2.getTableName()).getWhereExpression().addOperator("AND");
                        }
                        ((SelectQueryStatement)dt2.getTableName()).getWhereExpression().addWhereExpression(newWhereExp2);
                    }
                }
                if (sqs_Teradata_sql.getWhereExpression() != null && !sqs_Teradata_sql.getWhereExpression().isThetaJoinPresent()) {
                    if (!sqs_Teradata_sql.getWhereExpression().getWhereItems().isEmpty()) {
                        sqs_Teradata_sql.getWhereExpression().addOperator("AND");
                    }
                    sqs_Teradata_sql.getWhereExpression().addWhereExpression(dt2.getJoinExpression().firstElement());
                }
                derivedTables2.add(dt2);
            }
            final String orgnlTblAliasName = "orgnl";
            final FromClause fc2 = new FromClause();
            fc2.setFromClause("FROM");
            final Vector vc = new Vector();
            vc.addAll(derivedTables2);
            vc.add(0, this.convertedSQStoDerivedTable(selectItems, newFromClause2, newWhereExp2, orgnlTblAliasName));
            fc2.setFromItemList(vc);
            final SelectStatement ss = new SelectStatement();
            ss.setSelectClause("SELECT");
            final Vector vSelectItem = new Vector(this.getSelectStatement().getSelectItemList());
            final Vector vSelectItems_modified = new Vector(this.modifySumMethodFunctionsInSelectItems(vSelectItem, orgnlTblAliasName, this.getSumSelectColumn(), this.getSumDerivedTables()));
            ss.setSelectItemList(vSelectItems_modified);
            sqs_Teradata_sql = new SelectQueryStatement();
            sqs_Teradata_sql.setSelectStatement(ss);
            sqs_Teradata_sql.setFromClause(fc2);
        }
        if (this.getSelectStatement().getSelectItemList().size() != sqs_Teradata_sql.getSelectStatement().getSelectItemList().size()) {
            System.out.println("Issue in convesion size of select items does not match");
            throw new ConvertException("Issue in convesion size of select items does not match");
        }
        sqs_Teradata_sql.setConverted(true);
        return sqs_Teradata_sql;
    }
    
    public FromTable convertedSQStoDerivedTable(Vector selectItems, final FromClause fc, final WhereExpression we, final String aliasName) {
        final SelectQueryStatement sqsAsFromTable = new SelectQueryStatement();
        final SelectStatement ss = new SelectStatement();
        ss.setSelectClause("SELECT");
        selectItems = new Vector(this.removeSumFunctionWithPartitionByClause(selectItems));
        final SelectColumn sc = (SelectColumn)selectItems.lastElement();
        sc.setEndsWith(null);
        selectItems.setElementAt(sc, selectItems.size() - 1);
        ss.setSelectItemList(selectItems);
        sqsAsFromTable.setSelectStatement(ss);
        sqsAsFromTable.setFromClause(fc);
        sqsAsFromTable.setWhereExpression(we);
        final FromTable ft = new FromTable();
        ft.setTableName(sqsAsFromTable);
        ft.setAliasName(aliasName);
        return ft;
    }
    
    public Vector removeSumFunctionWithPartitionByClause(final Vector sItems) {
        final Vector sItem1 = new Vector(sItems);
        for (int i = 0, size = sItem1.size(); i < size; ++i) {
            if (sItem1.get(i) instanceof SelectColumn) {
                Vector vsc = new Vector();
                final SelectColumn sc = sItem1.get(i);
                if (this.checkForSumFunctionWithPartitionClause(sc.getColumnExpression())) {
                    sItem1.removeElementAt(i);
                    --i;
                    --size;
                }
                else {
                    vsc.addAll(sc.getColumnExpression());
                    vsc = this.removeSumFunctionWithPartitionByClause(vsc);
                    sc.setColumnExpression(vsc);
                    sItem1.setElementAt(sc, i);
                }
            }
            else if (sItem1.get(i) instanceof TableColumn) {
                final TableColumn tc = sItem1.get(i);
                sItem1.setElementAt(tc, i);
            }
            else if (sItem1.get(i) instanceof FunctionCalls) {
                final FunctionCalls fc = sItem1.get(i);
                if (!fc.getFunctionName().getColumnName().equalsIgnoreCase("SUM") || fc.getPartitionByClause() == null) {
                    Vector funArgs = new Vector(fc.getFunctionArguments());
                    funArgs = this.removeSumFunctionWithPartitionByClause(funArgs);
                    fc.setFunctionArguments(funArgs);
                    sItem1.setElementAt(fc, i);
                }
            }
        }
        return sItem1;
    }
    
    public boolean checkForSumFunctionWithPartitionClause(final Vector vc1) {
        boolean chk = false;
        final Vector vc2 = new Vector(vc1);
        for (int i = 0; i < vc2.size(); ++i) {
            if (vc2.get(i) instanceof FunctionCalls) {
                final FunctionCalls fc = vc2.get(i);
                chk = ((fc.getFunctionName().getColumnName().equalsIgnoreCase("SUM") && fc.getPartitionByClause() != null && fc.getArgumentQualifier() != null && fc.getArgumentQualifier().equalsIgnoreCase("DISTINCT")) || (chk | this.checkForSumFunctionWithPartitionClause(fc.getFunctionArguments())));
            }
            if (vc2.get(i) instanceof SelectColumn) {
                chk |= this.checkForSumFunctionWithPartitionClause(vc2.get(i).getColumnExpression());
            }
        }
        return chk;
    }
    
    public Vector modifySumMethodFunctionsInSelectItems(final Vector vSelectItem1, final String aliasName, final HashMap aliasNameSelectColumnPair, final HashMap derivedTables) throws ConvertException {
        final Vector vSelectItem2 = new Vector(vSelectItem1);
        for (int i = 0, size = vSelectItem2.size(); i < size; ++i) {
            if (vSelectItem2.get(i) instanceof SelectColumn) {
                Vector vsc = new Vector();
                final SelectColumn sc = vSelectItem2.get(i);
                vsc.addAll(sc.getColumnExpression());
                vsc = this.modifySumMethodFunctionsInSelectItems(vsc, aliasName, aliasNameSelectColumnPair, derivedTables);
                sc.setColumnExpression(vsc);
                vSelectItem2.setElementAt(sc.toTeradataSelect(null, null), i);
            }
            else if (vSelectItem2.get(i) instanceof TableColumn) {
                final TableColumn tc = vSelectItem2.get(i);
                tc.setTableName(aliasName);
                vSelectItem2.setElementAt(tc.toTeradataSelect(null, null), i);
            }
            else if (vSelectItem2.get(i) instanceof FunctionCalls) {
                final FunctionCalls fc = vSelectItem2.get(i);
                if (fc.getFunctionName().getColumnName().equalsIgnoreCase("SUM") && fc.getArgumentQualifier() != null && fc.getPartitionByClause() != null && fc.getFunctionArguments() != null) {
                    final String gpc = fc.getPartitionByClause().toString();
                    final String fcStr = fc.getFunctionArguments().get(0).toString();
                    final String newColName = aliasNameSelectColumnPair.get(gpc + fcStr).toString();
                    final TableColumn tc2 = new TableColumn();
                    final FromTable ft = derivedTables.get(gpc);
                    tc2.setTableName(ft.getAliasName());
                    final Vector vc = ((SelectQueryStatement)ft.getTableName()).getSelectStatement().getSelectItemList();
                    tc2.setColumnName(newColName);
                    vSelectItem2.setElementAt(tc2.toTeradataSelect(null, null), i);
                }
                else {
                    Vector funArgs = new Vector(fc.getFunctionArguments());
                    funArgs = this.modifySumMethodFunctionsInSelectItems(funArgs, aliasName, aliasNameSelectColumnPair, derivedTables);
                    fc.setFunctionArguments(funArgs);
                    vSelectItem2.setElementAt(fc.toTeradataSelect(null, null), i);
                }
            }
        }
        return vSelectItem2;
    }
    
    public SelectQueryStatement toMSSQLServerSelect() throws ConvertException {
        boolean wrapSelectQuery = false;
        Vector wrapperSelectCol = null;
        final SelectQueryStatement sqs_ms_sql = new SelectQueryStatement();
        sqs_ms_sql.setOpenBrace(this.openBrace);
        sqs_ms_sql.setCloseBrace(this.closeBrace);
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(2);
        }
        sqs_ms_sql.setCommentClass(this.commentObject);
        sqs_ms_sql.setSelectStatement(this.select_statement.toMSSQLServerSelect(sqs_ms_sql, this));
        if (this.from_clause != null) {
            sqs_ms_sql.setFromClause(this.from_clause.toMSSQLServerSelect(sqs_ms_sql, this));
        }
        if (this.whereExpression != null) {
            sqs_ms_sql.setWhereExpression(this.whereExpression.toMSSQLServerSelect(sqs_ms_sql, this));
        }
        if (this.order_by_statement != null) {
            final int selectColCount = sqs_ms_sql.getSelectStatement().getSelectItemList().size();
            wrapperSelectCol = sqs_ms_sql.getSelectStatement().getSelectItemList();
            sqs_ms_sql.setOrderByStatement(this.order_by_statement.toMSSQLServerSelect(sqs_ms_sql, this));
            final int newSelectColCount = sqs_ms_sql.getSelectStatement().getSelectItemList().size();
            if (newSelectColCount > selectColCount) {
                wrapSelectQuery = true;
            }
        }
        if (this.xmls != null) {
            sqs_ms_sql.setXMLStatements(this.xmls);
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                computeByStatementVector.add(this.computeByVector.get(i).toMSSQLServerSelect(sqs_ms_sql, this));
            }
            sqs_ms_sql.setComputeByStatements(computeByStatementVector);
        }
        if (sqs_ms_sql.getGroupByStatement() == null && this.group_by_statement != null) {
            sqs_ms_sql.setGroupByStatement(this.group_by_statement.toMSSQLServerSelect(sqs_ms_sql, this));
            if (sqs_ms_sql.getGroupByStatement() != null) {
                this.handleGroupByStatement(sqs_ms_sql, sqs_ms_sql.getGroupByStatement());
            }
        }
        if (this.set_operator_clause != null) {
            sqs_ms_sql.setSetOperatorClause(this.set_operator_clause.toMSSQLServerSelect(sqs_ms_sql, this));
        }
        if (this.having_statement != null) {
            sqs_ms_sql.setHavingStatement(this.having_statement.toMSSQLServerSelect(sqs_ms_sql, this));
        }
        if (this.into_statement != null) {
            sqs_ms_sql.setIntoStatement(this.into_statement.toMSSQLServerSelect(sqs_ms_sql, this));
        }
        if (this.hierarchical_query_clause != null) {
            final boolean isPrior = this.checkForPriorinHierarchyClause();
            if (!SwisSQLOptions.convertHierarchicalClausetoSQL2005 && isPrior) {
                sqs_ms_sql.setStartWithConnectByHashtable(this.processSelectQueryForStartWithConnectBy = this.handleStartWithConnectByClause());
                sqs_ms_sql.setFromClause(this.from_clause.toMSSQLServerSelect(sqs_ms_sql, this));
            }
            else {
                sqs_ms_sql.setWithStatement(this.withStatement = this.convertHierarchical_queryToWithStatement());
                if (this.withStatement == null && this.hierarchical_query_clause.getStartWithCondition() != null) {
                    sqs_ms_sql.setWhereExpression(this.hierarchical_query_clause.getStartWithCondition());
                }
            }
        }
        if (this.limit_clause != null) {
            sqs_ms_sql.setLimitClause(this.limit_clause.toMSSQLServerSelect(sqs_ms_sql, this));
        }
        if (this.optionalHintClause != null) {
            sqs_ms_sql.setOptionalHintClause(this.optionalHintClause);
        }
        if (this.fetch_clause != null) {
            sqs_ms_sql.setFetchClause(this.fetch_clause.toMSSQLServerSelect(sqs_ms_sql, this));
        }
        if (this.for_update_statement != null && FunctionCalls.charToIntName) {
            sqs_ms_sql.setForUpdateStatement(this.for_update_statement.toMSSQLServerSelect(sqs_ms_sql, this));
        }
        if (wrapSelectQuery) {
            final SelectQueryStatement wrapperQuery = new SelectQueryStatement();
            final SelectStatement wrapperStmt = new SelectStatement();
            wrapperStmt.setSelectClause("SELECT");
            if (wrapperSelectCol != null) {
                wrapperStmt.setSelectItemList(wrapperSelectCol);
            }
            final FromTable wrapperSubQuery = new FromTable();
            wrapperSubQuery.setTableName(sqs_ms_sql);
            wrapperQuery.setSelectStatement(wrapperStmt);
            wrapperSubQuery.setAliasName("alias");
            final FromClause wrapperFromClause = new FromClause();
            wrapperFromClause.addFromItem(wrapperSubQuery);
            wrapperFromClause.setFromClause("FROM");
            wrapperQuery.setFromClause(wrapperFromClause);
            wrapperQuery.setOrderByStatement(sqs_ms_sql.getOrderByStatement());
            sqs_ms_sql.setOrderByStatement(null);
            return wrapperQuery;
        }
        return sqs_ms_sql;
    }
    
    public SelectQueryStatement toSybaseSelect() throws ConvertException {
        final SelectQueryStatement sqs_ms_sql = new SelectQueryStatement();
        sqs_ms_sql.setObjectContext(this.objectContext);
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(7);
        }
        sqs_ms_sql.setCommentClass(this.commentObject);
        this.select_statement.setObjectContext(this.objectContext);
        sqs_ms_sql.setSelectStatement(this.select_statement.toSybaseSelect(sqs_ms_sql, this));
        if (this.from_clause != null) {
            sqs_ms_sql.setFromClause(this.from_clause.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.whereExpression != null) {
            this.whereExpression.setObjectContext(this.objectContext);
            sqs_ms_sql.setWhereExpression(this.whereExpression.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.order_by_statement != null) {
            sqs_ms_sql.setOrderByStatement(this.order_by_statement.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                computeByStatementVector.add(this.computeByVector.get(i).toSybaseSelect(sqs_ms_sql, this));
            }
            sqs_ms_sql.setComputeByStatements(computeByStatementVector);
            sqs_ms_sql.setCommentForCompute(true);
        }
        if (sqs_ms_sql.getGroupByStatement() == null && this.group_by_statement != null) {
            sqs_ms_sql.setGroupByStatement(this.group_by_statement.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.set_operator_clause != null) {
            sqs_ms_sql.setSetOperatorClause(this.set_operator_clause.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.having_statement != null) {
            sqs_ms_sql.setHavingStatement(this.having_statement.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.into_statement != null) {
            sqs_ms_sql.setIntoStatement(this.into_statement.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.hierarchical_query_clause != null) {
            sqs_ms_sql.setHierarchicalQueryClause(this.hierarchical_query_clause.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.limit_clause != null) {
            sqs_ms_sql.setLimitClause(this.limit_clause.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.fetch_clause != null) {
            sqs_ms_sql.setFetchClause(this.fetch_clause.toSybaseSelect(sqs_ms_sql, this));
        }
        if (this.for_update_statement != null) {
            if (SwisSQLOptions.convertOracleForUpdateToSybaseLockTable) {
                if (this.for_update_statement.getForUpdateQualifier() != null && this.for_update_statement.getForUpdateQualifier().toLowerCase().indexOf("of") != -1) {
                    final ArrayList addedTables = new ArrayList();
                    final Vector updateTableNames = this.for_update_statement.getForUpdateTableName();
                    for (int l = 0; l < updateTableNames.size(); ++l) {
                        final Object currObj = updateTableNames.get(l);
                        if (currObj instanceof TableColumn) {
                            final String currTableName = ((TableColumn)currObj).getTableName();
                            if (currTableName != null && !addedTables.contains(currTableName)) {
                                String lockTableStatement;
                                if (this.objectContext != null) {
                                    lockTableStatement = "LOCK TABLE " + this.objectContext.getEquivalent(currTableName) + " IN SHARE MODE";
                                }
                                else {
                                    lockTableStatement = "LOCK TABLE " + currTableName + " IN SHARE MODE";
                                }
                                if (this.for_update_statement.getNoWaitQualifier() != null && this.for_update_statement.getNoWaitQualifier().toLowerCase().indexOf("nowait") != -1) {
                                    lockTableStatement += " NOWAIT";
                                }
                                this.lockTableStatements.add(lockTableStatement);
                                addedTables.add(currTableName);
                            }
                            else if (currTableName == null) {
                                final FromClause fromClause = sqs_ms_sql.getFromClause();
                                if (fromClause != null) {
                                    final Vector fromItems = fromClause.getFromItemList();
                                    if (fromItems.size() == 1) {
                                        if (fromItems.get(0) instanceof FromTable) {
                                            final Object newObj = fromItems.get(0).getTableName();
                                            if (newObj instanceof String && !addedTables.contains(newObj.toString())) {
                                                String lockTableStatement;
                                                if (this.objectContext != null) {
                                                    lockTableStatement = "LOCK TABLE " + this.objectContext.getEquivalent(newObj.toString()) + " IN SHARE MODE";
                                                }
                                                else {
                                                    lockTableStatement = "LOCK TABLE " + newObj.toString() + " IN SHARE MODE";
                                                }
                                                if (this.for_update_statement.getNoWaitQualifier() != null && this.for_update_statement.getNoWaitQualifier().toLowerCase().indexOf("nowait") != -1) {
                                                    lockTableStatement += " NOWAIT";
                                                }
                                                this.lockTableStatements.add(lockTableStatement);
                                                addedTables.add(newObj.toString());
                                            }
                                        }
                                    }
                                    else {
                                        final FromTable currFromTable = MetadataInfoUtil.getTableOfColumn(sqs_ms_sql, (TableColumn)currObj);
                                        if (currFromTable != null) {
                                            final Object fromTableObj = currFromTable.getTableName();
                                            if (fromTableObj instanceof String && !addedTables.contains(fromTableObj.toString())) {
                                                String lockTableStatement;
                                                if (this.objectContext != null) {
                                                    lockTableStatement = "LOCK TABLE " + this.objectContext.getEquivalent(fromTableObj.toString()) + " IN SHARE MODE";
                                                }
                                                else {
                                                    lockTableStatement = "LOCK TABLE " + fromTableObj.toString() + " IN SHARE MODE";
                                                }
                                                if (this.for_update_statement.getNoWaitQualifier() != null && this.for_update_statement.getNoWaitQualifier().toLowerCase().indexOf("nowait") != -1) {
                                                    lockTableStatement += " NOWAIT";
                                                }
                                                this.lockTableStatements.add(lockTableStatement);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    final FromClause fromClause2 = sqs_ms_sql.getFromClause();
                    if (fromClause2 != null) {
                        final Vector fromItems2 = fromClause2.getFromItemList();
                        for (int l = 0; l < fromItems2.size(); ++l) {
                            Object currObj = fromItems2.get(l);
                            if (currObj instanceof FromTable) {
                                currObj = ((FromTable)currObj).getTableName();
                                if (currObj instanceof String) {
                                    String lockTableStatement;
                                    if (this.objectContext != null) {
                                        lockTableStatement = "LOCK TABLE " + this.objectContext.getEquivalent(currObj.toString()) + " IN SHARE MODE";
                                    }
                                    else {
                                        lockTableStatement = "LOCK TABLE " + currObj.toString() + " IN SHARE MODE";
                                    }
                                    if (this.for_update_statement.getNoWaitQualifier() != null && this.for_update_statement.getNoWaitQualifier().toLowerCase().indexOf("nowait") != -1) {
                                        lockTableStatement += " NOWAIT";
                                    }
                                    this.lockTableStatements.add(lockTableStatement);
                                }
                            }
                        }
                    }
                }
                sqs_ms_sql.setLockTableStatements(this.lockTableStatements);
                sqs_ms_sql.setForUpdateStatement(null);
            }
            else {
                sqs_ms_sql.setForUpdateStatement(null);
            }
        }
        sqs_ms_sql.setOptionalHintClause(null);
        sqs_ms_sql.setAtIsolation(this.atIsolation);
        sqs_ms_sql.setIsolationReadLevel(this.isolationReadLevel);
        return sqs_ms_sql;
    }
    
    public SelectQueryStatement toDB2Select() throws ConvertException {
        final SelectQueryStatement sqs_db2_sql = new SelectQueryStatement();
        this.select_statement.setInsertValList(this.insertValList);
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(3);
        }
        sqs_db2_sql.setCommentClass(this.commentObject);
        sqs_db2_sql.setSelectStatement(this.select_statement.toDB2Select(sqs_db2_sql, this));
        final SelectQueryStatement sqs_db2_Hierarchial_Query = new SelectQueryStatement();
        if (this.from_clause != null) {
            sqs_db2_sql.setFromClause(this.from_clause.toDB2Select(sqs_db2_sql, this));
        }
        else {
            final FromClause fc = new FromClause();
            final FromTable ft = new FromTable();
            final Vector fil = new Vector();
            final FetchClause local_fetch_clause = new FetchClause();
            fc.setFromClause("FROM");
            ft.setTableName("SYSIBM.SYSDUMMY1");
            fil.addElement(ft);
            fc.setFromItemList(fil);
            local_fetch_clause.setFetchFirstClause("FETCH FIRST");
            local_fetch_clause.setFetchCount("1");
            local_fetch_clause.setRowOnlyClause("ROW ONLY");
            fc.setFetchClauseFromSQS(local_fetch_clause);
            sqs_db2_sql.setFromClause(fc);
        }
        if (this.whereExpression != null) {
            sqs_db2_sql.setWhereExpression(this.whereExpression.toDB2Select(sqs_db2_sql, this));
        }
        if (this.order_by_statement != null) {
            final SelectStatement ss = sqs_db2_sql.getSelectStatement();
            boolean orderBySetNull = false;
            if (ss != null) {
                final Vector selectItems = ss.getSelectItemList();
                if (selectItems != null) {
                    for (int i = 0; i < selectItems.size(); ++i) {
                        Object obj = selectItems.get(i);
                        if (obj instanceof SelectColumn) {
                            final SelectColumn sc = (SelectColumn)obj;
                            final Vector colExpr = sc.getColumnExpression();
                            if (colExpr != null) {
                                for (int j = 0; j < colExpr.size(); ++j) {
                                    obj = colExpr.get(j);
                                    if (obj instanceof FunctionCalls) {
                                        final FunctionCalls fn = (FunctionCalls)obj;
                                        final TableColumn tc = fn.getFunctionName();
                                        if (tc != null) {
                                            final String colName = tc.getColumnName();
                                            if ((colName.equalsIgnoreCase("max") || colName.equalsIgnoreCase("min") || colName.equalsIgnoreCase("count") || colName.equalsIgnoreCase("sum") || colName.equalsIgnoreCase("avg")) && this.group_by_statement == null) {
                                                orderBySetNull = true;
                                                sqs_db2_sql.setOrderByStatement(null);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!orderBySetNull) {
                sqs_db2_sql.setOrderByStatement(this.order_by_statement.toDB2Select(sqs_db2_sql, this));
            }
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int k = 0; k < this.computeByVector.size(); ++k) {
                computeByStatementVector.add(this.computeByVector.get(k).toDB2Select(sqs_db2_sql, this));
            }
            sqs_db2_sql.setComputeByStatements(computeByStatementVector);
            sqs_db2_sql.setCommentForCompute(true);
        }
        if (sqs_db2_sql.getGroupByStatement() == null && this.group_by_statement != null) {
            sqs_db2_sql.setGroupByStatement(this.group_by_statement.toDB2Select(sqs_db2_sql, this));
            if (sqs_db2_sql.getGroupByStatement() != null && sqs_db2_sql.getGroupByStatement().getGroupingSetClause() == null) {
                this.handleGroupByStatement(sqs_db2_sql, sqs_db2_sql.getGroupByStatement());
            }
        }
        if (this.set_operator_clause != null) {
            sqs_db2_sql.setSetOperatorClause(this.set_operator_clause.toDB2Select(sqs_db2_sql, this));
        }
        if (this.having_statement != null) {
            sqs_db2_sql.setHavingStatement(this.having_statement.toDB2Select(sqs_db2_sql, this));
        }
        if (this.into_statement != null) {
            sqs_db2_sql.setIntoStatement(this.into_statement.toDB2Select(sqs_db2_sql, this));
        }
        if (this.hierarchical_query_clause != null) {
            sqs_db2_sql.setWithStatement(this.withStatement = this.convertHierarchical_queryToWithStatement());
            if (this.withStatement == null && this.hierarchical_query_clause.getStartWithCondition() != null) {
                sqs_db2_sql.setWhereExpression(this.hierarchical_query_clause.getStartWithCondition());
            }
        }
        if (this.limit_clause != null) {
            sqs_db2_sql.setLimitClause(this.limit_clause.toDB2Select(sqs_db2_sql, this));
        }
        if (this.fetch_clause != null) {
            sqs_db2_sql.setFetchClause(this.fetch_clause.toDB2Select(sqs_db2_sql, this));
        }
        if (this.for_update_statement != null) {
            sqs_db2_sql.setForUpdateStatement(this.for_update_statement.toDB2Select(sqs_db2_sql, this));
        }
        sqs_db2_sql.setOptionalHintClause(null);
        if (this.definitionOnly != null) {
            sqs_db2_sql.setDefinitionOnly(this.definitionOnly);
        }
        if (this.iqs != null) {
            sqs_db2_sql.setInsertQueryStatement(this.iqs);
        }
        if (this.withString != null) {
            sqs_db2_sql.setWithString(this.withString);
        }
        if (this.isolationLevel != null) {
            sqs_db2_sql.setIsolationLevel(this.isolationLevel);
        }
        return sqs_db2_sql;
    }
    
    public SelectQueryStatement toPostgreSQLSelect() throws ConvertException {
        final SelectQueryStatement sqs_postgre_sql = new SelectQueryStatement();
        sqs_postgre_sql.setSetOperatorQuery(this.isSetOperatorQuery);
        sqs_postgre_sql.setCanHandleHavingWithoutGroupBy(this.canHandleHavingWithoutGroupBy);
        sqs_postgre_sql.setRemovalOptionForOrderAndFetchClauses(this.removalOptionForOrderAndFetchClauses);
        sqs_postgre_sql.setSelectStatement(this.select_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
        if (this.from_clause != null) {
            sqs_postgre_sql.setFromClause(this.from_clause.toPostgreSQLSelect(sqs_postgre_sql, this));
        }
        if (this.whereExpression != null) {
            sqs_postgre_sql.setWhereExpression(this.whereExpression.toPostgreSQLSelect(sqs_postgre_sql, this));
        }
        if (this.order_by_statement != null) {
            sqs_postgre_sql.setOrderByStatement(this.order_by_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                computeByStatementVector.add(this.computeByVector.get(i).toPostgreSQLSelect(sqs_postgre_sql, this));
            }
            sqs_postgre_sql.setComputeByStatements(computeByStatementVector);
            sqs_postgre_sql.setCommentForCompute(true);
        }
        if (sqs_postgre_sql.getGroupByStatement() == null && this.group_by_statement != null) {
            sqs_postgre_sql.setGroupByStatement(this.group_by_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
        }
        if (this.set_operator_clause != null) {
            sqs_postgre_sql.setSetOperatorClause(this.set_operator_clause.toPostgreSQLSelect(sqs_postgre_sql, this));
            this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_postgre_sql, "TEXT");
        }
        if (this.having_statement != null) {
            sqs_postgre_sql.setHavingStatement(this.having_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
            this.handleHavingClauseWithoutGroupByClauseQueries(sqs_postgre_sql);
        }
        if (this.into_statement != null) {
            sqs_postgre_sql.setIntoStatement(this.into_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
        }
        if (this.hierarchical_query_clause != null) {
            throw new ConvertException();
        }
        if (this.limit_clause != null) {
            sqs_postgre_sql.setLimitClause(this.limit_clause.toPostgreSQLSelect(sqs_postgre_sql, this));
        }
        if (this.fetch_clause != null) {
            sqs_postgre_sql.setFetchClause(this.fetch_clause.toPostgreSQLSelect(sqs_postgre_sql, this));
        }
        sqs_postgre_sql.setOptionalHintClause(null);
        return sqs_postgre_sql;
    }
    
    public SelectQueryStatement toMySQLSelect() throws ConvertException {
        final SelectQueryStatement sqs_mysql_sql = new SelectQueryStatement();
        sqs_mysql_sql.setCanHandleFunctionArgumentsCountMismatch(this.getCanHandleFunctionArugmentsCountMismatch());
        sqs_mysql_sql.setValidationHandler(this.getValidationHandler());
        sqs_mysql_sql.setcanAllowLogicalExpInAggFun(this.getcanAllowLogicalExpInAggFun());
        sqs_mysql_sql.setCanAllowBackTipInColumnName(this.getCanAllowBackTipInColumnName());
        sqs_mysql_sql.setCanReplaceDoubleDotsInTableName(this.getCanReplaceDoubleDotsInTableName());
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(5);
        }
        sqs_mysql_sql.setCommentClass(this.commentObject);
        sqs_mysql_sql.setSelectStatement(this.select_statement.toMySQLSelect(sqs_mysql_sql, this));
        if (this.from_clause != null) {
            sqs_mysql_sql.setFromClause(this.from_clause.toMySQLSelect(sqs_mysql_sql, this));
        }
        if (this.whereExpression != null) {
            sqs_mysql_sql.setWhereExpression(this.whereExpression.toMySQLSelect(sqs_mysql_sql, this));
        }
        if (this.order_by_statement != null) {
            sqs_mysql_sql.setOrderByStatement(this.order_by_statement.toMySQLSelect(sqs_mysql_sql, this));
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                computeByStatementVector.add(this.computeByVector.get(i).toPostgreSQLSelect(sqs_mysql_sql, this));
            }
            sqs_mysql_sql.setComputeByStatements(computeByStatementVector);
            sqs_mysql_sql.setCommentForCompute(true);
        }
        if (sqs_mysql_sql.getGroupByStatement() == null && this.group_by_statement != null) {
            sqs_mysql_sql.setGroupByStatement(this.group_by_statement.toMySQLSelect(sqs_mysql_sql, this));
        }
        if (this.set_operator_clause != null) {
            sqs_mysql_sql.setSetOperatorClause(this.set_operator_clause.toMySQLSelect(sqs_mysql_sql, this));
        }
        if (this.having_statement != null) {
            sqs_mysql_sql.setHavingStatement(this.having_statement.toMySQLSelect(sqs_mysql_sql, this));
        }
        if (this.into_statement != null) {
            sqs_mysql_sql.setIntoStatement(this.into_statement.toMySQLSelect(sqs_mysql_sql, this));
        }
        if (this.hierarchical_query_clause != null) {
            throw new ConvertException();
        }
        if (this.limit_clause != null) {
            sqs_mysql_sql.setLimitClause(this.limit_clause.toMySQLSelect(sqs_mysql_sql, this));
        }
        if (this.fetch_clause != null) {
            sqs_mysql_sql.setFetchClause(this.fetch_clause.toMySQLSelect(sqs_mysql_sql, this));
        }
        sqs_mysql_sql.setOptionalHintClause(null);
        return sqs_mysql_sql;
    }
    
    public SelectQueryStatement toOracleSelect() throws ConvertException {
        final SelectQueryStatement sqs_oracle_sql = new SelectQueryStatement();
        sqs_oracle_sql.setObjectContext(this.objectContext);
        boolean topWithOrderBy = false;
        String rowSpecifier = null;
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(1);
        }
        sqs_oracle_sql.setCommentClass(this.commentObject);
        final SelectQueryStatement newSQS = this;
        final XMLStatement newXmls = this.xmls;
        boolean xmlForest = false;
        String xmlString = null;
        String tableName = null;
        FromClause frcl = this.getFromClause();
        Vector fromIList = null;
        if (frcl != null) {
            fromIList = frcl.getFromItemList();
            if (fromIList != null) {
                for (int i = 0; i < fromIList.size(); ++i) {
                    try {
                        final Object tObj = fromIList.get(i);
                        if (tObj instanceof FromTable) {
                            tableName = ((FromTable)tObj).getTableName().toString();
                        }
                        else if (tObj instanceof String) {
                            tableName = tObj.toString();
                        }
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        final SelectStatement ssForId = this.getSelectStatement();
        final Vector siList = ssForId.getSelectItemList();
        final CreateSequenceStatement createSeq = new CreateSequenceStatement();
        if (siList != null) {
            for (int j = 0; j < siList.size(); ++j) {
                final Object o = siList.get(j);
                if (o instanceof SelectColumn) {
                    final SelectColumn sclForId = (SelectColumn)o;
                    final Vector vecForId = sclForId.getColumnExpression();
                    if (vecForId != null) {
                        for (int k = 0; k < vecForId.size(); ++k) {
                            if (vecForId.get(k) instanceof FunctionCalls && vecForId.get(k).getFunctionName() != null) {
                                final TableColumn tcForId = vecForId.get(k).getFunctionName();
                                final Vector args = vecForId.get(k).getFunctionArguments();
                                if (tcForId.getColumnName() != null && tcForId.getColumnName().equalsIgnoreCase("IDENTITY")) {
                                    if (args != null) {
                                        if (args.size() > 0) {
                                            createSeq.setDataType(args.get(0));
                                            createSeq.setAs("AS");
                                        }
                                        if (args.size() > 1) {
                                            createSeq.setStartValue(args.get(1) + "");
                                            createSeq.setStart("START");
                                            createSeq.setWith("WITH");
                                        }
                                        if (args.size() > 2) {
                                            createSeq.setIncrementValue(args.get(2) + "");
                                            createSeq.setIncrementString("INCREMENT BY");
                                        }
                                        this.setSequenceForIdentityFn(createSeq);
                                    }
                                    final String alname = sclForId.getAliasName();
                                    tcForId.setColumnName(alname);
                                    sclForId.setIsAS(null);
                                    sclForId.setAliasName(null);
                                    vecForId.setElementAt(tcForId, k);
                                }
                            }
                        }
                    }
                    sclForId.setColumnExpression(vecForId);
                }
            }
        }
        SetOperatorClause sopc = this.getSetOperatorClause();
        while (tableName == null && sopc != null) {
            final SelectQueryStatement tempSQS = sopc.getSelectQueryStatement();
            if (tempSQS != null) {
                sopc = tempSQS.getSetOperatorClause();
                frcl = tempSQS.getFromClause();
                if (frcl == null) {
                    continue;
                }
                fromIList = frcl.getFromItemList();
                if (fromIList == null) {
                    continue;
                }
                for (int l = 0; l < fromIList.size(); ++l) {
                    try {
                        final Object tObj2 = fromIList.get(l);
                        if (tObj2 instanceof FromTable) {
                            tableName = ((FromTable)tObj2).getTableName().toString();
                        }
                        else if (tObj2 instanceof String) {
                            tableName = tObj2.toString();
                        }
                    }
                    catch (final Exception ex2) {}
                }
            }
        }
        if (newXmls != null && newXmls.getXMLType().equalsIgnoreCase("EXPLICIT")) {
            int maxDepth = 1;
            final SelectStatement newSS = this.getSelectStatement();
            Vector selectList = newSS.getSelectItemList();
            if (selectList == null) {
                selectList = new Vector();
            }
            for (int m = 0; m < selectList.size(); ++m) {
                int depth = maxDepth;
                final Object o2 = selectList.get(m);
                if (o2 instanceof SelectColumn) {
                    final String aliasName = ((SelectColumn)o2).getAliasName();
                    if (aliasName.indexOf("!") > 0) {
                        try {
                            depth = Integer.parseInt(aliasName.substring(aliasName.indexOf("!") + 1, aliasName.lastIndexOf("!")));
                        }
                        catch (final Exception e) {
                            e.printStackTrace();
                        }
                        maxDepth = ((depth > maxDepth) ? depth : maxDepth);
                    }
                }
            }
            Vector newSelectItemList = this.getSelectStatement().getSelectItemList();
            if (newSelectItemList == null) {
                newSelectItemList = new Vector();
            }
            for (int i2 = 1; i2 < newSelectItemList.size(); ++i2) {
                final Object o2 = newSelectItemList.get(i2);
                if (o2 instanceof SelectColumn) {
                    String clName = null;
                    final Object subO = ((SelectColumn)o2).getColumnExpression().elementAt(0);
                    if (subO instanceof String) {
                        clName = subO.toString();
                    }
                    else if (subO instanceof TableColumn) {
                        clName = ((TableColumn)subO).getColumnName();
                    }
                    if (clName != null && clName.equalsIgnoreCase("NULL")) {
                        SelectQueryStatement newTempSQS = this;
                        while (newSQS.getSetOperatorClause() != null) {
                            newTempSQS = newTempSQS.getSetOperatorClause().getSelectQueryStatement();
                            if (newTempSQS != null) {
                                final Vector tempSelectItem = newTempSQS.getSelectStatement().getSelectItemList();
                                final Object oo = tempSelectItem.get(i2);
                                if (!(oo instanceof SelectColumn)) {
                                    continue;
                                }
                                String clName2 = null;
                                final Object subOo = ((SelectColumn)oo).getColumnExpression().elementAt(0);
                                if (subO instanceof String) {
                                    clName2 = subOo.toString();
                                }
                                else if (subO instanceof TableColumn) {
                                    clName2 = ((TableColumn)subOo).getColumnName();
                                }
                                if (clName2 != null && !clName2.equalsIgnoreCase("NULL")) {
                                    final Object newOb = newSelectItemList.remove(i2);
                                    ((SelectColumn)oo).setAliasName(((SelectColumn)newOb).getAliasName());
                                    newSelectItemList.add(i2, oo);
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                }
            }
            if (newSelectItemList.size() > 1) {
                for (int index = 0; index < 2; ++index) {
                    final Object o2 = newSelectItemList.get(0);
                    if (o2 instanceof SelectColumn) {
                        final String aliasName = ((SelectColumn)o2).getAliasName();
                        if (aliasName.equalsIgnoreCase("TAG")) {
                            newSelectItemList.remove(0);
                        }
                        if (aliasName.equalsIgnoreCase("PARENT")) {
                            final SelectColumn sc = newSelectItemList.remove(0);
                            final Object tempO = sc.getColumnExpression().elementAt(0);
                            xmlForest = ((tempO instanceof String && tempO.toString().equalsIgnoreCase("NULL")) || (tempO instanceof TableColumn && ((TableColumn)tempO).getColumnName().equalsIgnoreCase("NULL")));
                        }
                    }
                }
            }
            StringBuffer xmlStringBuf = new StringBuffer();
            if (xmlForest) {
                xmlStringBuf.append("XMLFOREST(");
                for (int i3 = 0; i3 < newSelectItemList.size(); ++i3) {
                    final SelectColumn tempSc = newSelectItemList.get(i3);
                    String alName = tempSc.getAliasName();
                    String clName3 = null;
                    final Object clObj = tempSc.getColumnExpression().elementAt(0);
                    if (clObj != null) {
                        if (clObj instanceof String) {
                            clName3 = clObj.toString();
                        }
                        else if (clObj instanceof TableColumn) {
                            clName3 = ((TableColumn)clObj).getColumnName();
                        }
                    }
                    if (alName != null) {
                        if (alName.startsWith("[")) {
                            alName = alName.substring(1);
                        }
                        if (alName.endsWith("]")) {
                            alName = alName.substring(0, alName.length() - 1);
                        }
                    }
                    if (alName != null) {
                        alName = alName.substring(alName.lastIndexOf("!") + 1, alName.length());
                    }
                    if (clName3 != null) {
                        if (clName3.startsWith("[")) {
                            clName3 = clName3.substring(1);
                        }
                        if (clName3.endsWith("]")) {
                            clName3 = clName3.substring(0, clName3.length() - 1);
                        }
                    }
                    if (clName3 != null && alName != null) {
                        xmlStringBuf.append(clName3 + " AS " + alName + ",");
                    }
                    else if (clName3 != null) {
                        xmlStringBuf.append(clName3 + ",");
                    }
                }
                xmlString = xmlStringBuf.toString();
                xmlString = xmlString.substring(0, xmlString.length() - 1);
                xmlString += ")";
            }
            else {
                xmlStringBuf.append("XMLELEMENT(");
                for (int depth2 = 1; depth2 <= maxDepth; ++depth2) {
                    if (depth2 > 1) {
                        xmlStringBuf.append(", XMLELEMENT(");
                    }
                    boolean tagAdded = false;
                    int currentDepth = 1;
                    for (int i4 = 0; i4 < newSelectItemList.size(); ++i4) {
                        final Object o3 = selectList.get(i4);
                        if (o3 instanceof SelectColumn) {
                            String alName2 = ((SelectColumn)o3).getAliasName();
                            if (alName2 != null && alName2.indexOf("!") > 0) {
                                try {
                                    currentDepth = Integer.parseInt(alName2.substring(alName2.indexOf("!") + 1, alName2.lastIndexOf("!")));
                                }
                                catch (final Exception e2) {
                                    e2.printStackTrace();
                                }
                            }
                            if (currentDepth == depth2) {
                                String clName4 = null;
                                final Object clObj2 = ((SelectColumn)o3).getColumnExpression().elementAt(0);
                                if (clObj2 != null) {
                                    if (clObj2 instanceof String) {
                                        clName4 = clObj2.toString();
                                    }
                                    else if (clObj2 instanceof TableColumn) {
                                        clName4 = ((TableColumn)clObj2).getColumnName();
                                    }
                                    else {
                                        clName4 = clObj2.toString();
                                    }
                                }
                                if (alName2 != null) {
                                    if (alName2.startsWith("[")) {
                                        alName2 = alName2.substring(1);
                                    }
                                    if (alName2.endsWith("]")) {
                                        alName2 = alName2.substring(0, alName2.length() - 1);
                                    }
                                }
                                if (alName2 != null && !tagAdded) {
                                    xmlStringBuf.append("\"" + alName2.substring(0, alName2.indexOf("!")) + "\"");
                                    xmlStringBuf.append(", XMLATTRIBUTES(");
                                    tagAdded = true;
                                }
                                if (alName2 != null) {
                                    alName2 = alName2.substring(alName2.lastIndexOf("!") + 1, alName2.length());
                                }
                                if (clName4 != null && alName2 != null) {
                                    if (clName4.startsWith("[")) {
                                        clName4 = clName4.substring(1);
                                    }
                                    if (clName4.endsWith("]")) {
                                        clName4 = clName4.substring(0, clName4.length() - 1);
                                    }
                                    xmlStringBuf.append(clName4 + " AS " + alName2 + ",");
                                }
                                else if (clName4 != null) {
                                    xmlStringBuf.append(clName4 + ",");
                                }
                            }
                        }
                    }
                    xmlString = xmlStringBuf.toString();
                    xmlString = xmlString.substring(0, xmlString.length() - 1);
                    xmlString += ")";
                    if (depth2 > 1) {
                        xmlString += ")";
                    }
                    xmlStringBuf = new StringBuffer(xmlString);
                }
                xmlString = xmlStringBuf.toString();
                xmlString += ")";
            }
            frcl = new FromClause();
            frcl.setFromClause("FROM");
            final FromTable frt = new FromTable();
            frt.setTableName(tableName);
            fromIList = new Vector();
            fromIList.add(frt);
            frcl.setFromItemList(fromIList);
            this.setFromClause(frcl);
            this.setSetOperatorClause(null);
            if (this.getOrderByStatement() != null) {
                final OrderByStatement obs = this.getOrderByStatement();
                final Vector v = obs.getOrderItemList();
                if (v != null) {
                    for (int i4 = 0; i4 < v.size(); ++i4) {
                        final OrderItem oi = v.get(i4);
                        final SelectColumn os = oi.getOrderSpecifier();
                        if (os != null) {
                            final Object clObj3 = os.getColumnExpression().elementAt(0);
                            if (clObj3 != null) {
                                if (clObj3 instanceof String && clObj3.toString().indexOf("!") > 0) {
                                    String tempS = clObj3.toString();
                                    tempS = tempS.substring(tempS.lastIndexOf("!") + 1, tempS.length());
                                    if (tempS.endsWith("]")) {
                                        tempS = tempS.substring(0, tempS.length() - 1);
                                    }
                                    os.getColumnExpression().remove(0);
                                    os.getColumnExpression().add(tempS);
                                }
                                else if (clObj3 instanceof TableColumn) {
                                    final TableColumn tc = (TableColumn)clObj3;
                                    String tempS2 = tc.getColumnName();
                                    tempS2 = tempS2.substring(tempS2.lastIndexOf("!") + 1, tempS2.length());
                                    if (tempS2.endsWith("]")) {
                                        tempS2 = tempS2.substring(0, tempS2.length() - 1);
                                    }
                                    tc.setColumnName(tempS2);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.getSelectStatement() != null) {
            rowSpecifier = this.getSelectStatement().getSelectRowSpecifier();
            if (rowSpecifier != null && rowSpecifier.trim().equalsIgnoreCase("TOP") && this.getOrderByStatement() != null) {
                topWithOrderBy = true;
                final Vector selectitems = this.select_statement.getSelectItemList();
                int k2 = 1;
                for (int i5 = 0; i5 < selectitems.size(); ++i5) {
                    if (selectitems.get(i5) instanceof SelectColumn) {
                        final SelectColumn tempSelectColumn = selectitems.get(i5);
                        final Vector tempColumnExpression = tempSelectColumn.getColumnExpression();
                        for (int j2 = 0; j2 < tempColumnExpression.size(); ++j2) {
                            if (tempColumnExpression.get(j2) instanceof FunctionCalls && tempSelectColumn.getAliasName() == null) {
                                final FunctionCalls columnsWithFunctions = tempColumnExpression.get(j2);
                                final String aliasName2 = "ADV_ALIAS_" + k2;
                                tempSelectColumn.setAliasName(aliasName2);
                                tempSelectColumn.setIsAS("AS");
                                ++k2;
                            }
                        }
                    }
                }
            }
        }
        sqs_oracle_sql.setSelectStatement(this.select_statement.toOracleSelect(sqs_oracle_sql, this));
        if (this.xmls != null && this.xmls.getXMLType() != null) {
            final String xmlType = this.xmls.getXMLType();
            if (xmlType.equalsIgnoreCase("AUTO") && this.xmls.getElements() != null) {
                final String xmlString2 = "XMLFOREST(";
                sqs_oracle_sql.getSelectStatement().setXMLString(xmlString2);
                sqs_oracle_sql.getSelectStatement().setXMLEndTag(")");
            }
            else if (xmlType.equalsIgnoreCase("AUTO")) {
                String xmlString2 = "XMLELEMENT(";
                final String xmlTable = this.getTableNameForXMLElement(this.from_clause);
                xmlString2 = xmlString2 + "\"" + xmlTable + "\"" + ", XMLATTRIBUTES(";
                sqs_oracle_sql.getSelectStatement().setXMLString(xmlString2);
                sqs_oracle_sql.getSelectStatement().setXMLEndTag("))");
            }
            else if (xmlType.equalsIgnoreCase("RAW")) {
                String xmlString2 = "XMLELEMENT(";
                final String xmlTable = this.getTableNameForXMLElement(this.from_clause);
                xmlString2 = xmlString2 + "\"ROW\"" + ", XMLATTRIBUTES(";
                sqs_oracle_sql.getSelectStatement().setXMLString(xmlString2);
                sqs_oracle_sql.getSelectStatement().setXMLEndTag("))");
            }
            else if (xmlString != null) {
                sqs_oracle_sql.getSelectStatement().setXMLString(xmlString);
                final Vector v2 = new Vector();
                sqs_oracle_sql.getSelectStatement().setSelectItemList(v2);
            }
            else if (xmlType.equalsIgnoreCase("EXPLICIT")) {
                String xmlString2 = "XMLELEMENT(";
                final String xmlTable = this.getTableNameForXMLElement(this.from_clause);
                xmlString2 = xmlString2 + "\"" + xmlTable + "\"" + ", XMLFOREST(";
                sqs_oracle_sql.getSelectStatement().setXMLString(xmlString2);
                sqs_oracle_sql.getSelectStatement().setXMLEndTag("))");
            }
        }
        if (this.from_clause != null) {
            final FromClause fc = new FromClause();
            sqs_oracle_sql.setFromClause(fc);
            this.from_clause.setBaseFromClauseFound(true);
            final FromClause newFC = this.from_clause.toOracleSelect(sqs_oracle_sql, this);
            sqs_oracle_sql.setFromClause(newFC);
            if (newFC != null) {
                final Vector fromItems = newFC.getFromItemList();
                if (fromItems != null) {
                    for (int m = 0; m < fromItems.size(); ++m) {
                        final Object obj = fromItems.get(m);
                        if (obj instanceof FromTable) {
                            final String lockStt = ((FromTable)obj).getLockTableStatement();
                            if (lockStt != null) {
                                sqs_oracle_sql.addLockTableList(lockStt);
                            }
                        }
                    }
                }
            }
        }
        else {
            final FromClause fc = new FromClause();
            final FromTable ft = new FromTable();
            final Vector fil = new Vector();
            fc.setFromClause("FROM");
            ft.setTableName("SYS.DUAL");
            fil.addElement(ft);
            fc.setFromItemList(fil);
            sqs_oracle_sql.setFromClause(fc);
        }
        if (this.withString != null && this.isolationLevel != null) {
            String lockStatement = "LOCK TABLE ";
            final FromClause fc2 = this.from_clause;
            final Vector tableList = fc2.getFromItemList();
            for (int m = 0; m < tableList.size(); ++m) {
                final Object o4 = tableList.get(m);
                if (o4 instanceof FromTable) {
                    final FromTable ft2 = (FromTable)o4;
                    if (ft2.getTableName() instanceof String) {
                        final String s = (String)ft2.getTableName();
                        lockStatement = lockStatement + s + " IN ";
                        if (this.isolationLevel.trim().equalsIgnoreCase("RR") || this.isolationLevel.trim().equalsIgnoreCase("RS")) {
                            lockStatement += "EXCLUSIVE MODE";
                            sqs_oracle_sql.addLockTableList(lockStatement);
                        }
                    }
                }
            }
        }
        if (this.whereExpression != null && !this.whereExpression.getCheckWhere()) {
            if (sqs_oracle_sql.getWhereExpression() == null) {
                sqs_oracle_sql.setWhereExpression(this.whereExpression.toOracleSelect(sqs_oracle_sql, this));
            }
            else if (!topWithOrderBy && (rowSpecifier == null || !rowSpecifier.trim().equalsIgnoreCase("TOP"))) {
                sqs_oracle_sql.getWhereExpression().addOperator("AND");
                this.whereExpression.setCloseBrace(")");
                this.whereExpression.setOpenBrace("(");
                sqs_oracle_sql.getWhereExpression().addWhereExpression(this.whereExpression.toOracleSelect(sqs_oracle_sql, this));
            }
        }
        if (this.order_by_statement != null) {
            sqs_oracle_sql.setOrderByStatement(this.order_by_statement.toOracleSelect(sqs_oracle_sql, this));
        }
        SelectQueryStatement subSQS = null;
        if (topWithOrderBy) {
            final SelectStatement newSS = new SelectStatement();
            subSQS = this.toFormSubQuery(sqs_oracle_sql);
            sqs_oracle_sql.setSubQuery(subSQS);
            final FromClause fc3 = sqs_oracle_sql.getFromClause();
            final Vector fromitems = fc3.getFromItemList();
            if (fromitems.size() == 1 && subSQS != null && fromitems.get(0) instanceof FromTable) {
                final FromTable ft3 = fromitems.get(0);
                sqs_oracle_sql.setAliasForSubQuery(ft3.getTableName().toString());
            }
            newSS.setSelectClause("SELECT");
            final Vector items = sqs_oracle_sql.getSelectStatement().getSelectItemList();
            final Vector outerItems1 = new Vector();
            final Vector innerItems2 = new Vector();
            if (items != null) {
                boolean selectStarPresent = false;
                for (int i4 = 0; i4 < items.size(); ++i4) {
                    final Object o3 = items.get(i4);
                    if (o3 instanceof SelectColumn) {
                        final SelectColumn sc2 = (SelectColumn)o3;
                        final SelectColumn newSc = new SelectColumn();
                        newSc.setObjectContext(sc2.getObjectContext());
                        newSc.setAliasName(sc2.getAliasName());
                        newSc.setIsAS(sc2.getIsAS());
                        newSc.setEndsWith(sc2.getEndsWith());
                        newSc.setOpenBrace(sc2.getOpenBrace());
                        newSc.setCloseBrace(sc2.getCloseBrace());
                        newSc.setInsideDecodeFunction(sc2.getInsideDecodeFunction());
                        newSc.setInArithmeticExpression(sc2.getInArithmeticExpression());
                        newSc.setCorrespondingTableColumn(sc2.getCorrespondingTableColumn());
                        newSc.setTargetDataType(sc2.getTargetDataType());
                        newSc.setSelectColFromUQSSetExpression(sc2.getSelectColFromUQSSetExpression());
                        newSc.setColumnExpression(sc2.getColumnExpression());
                        final Vector vOuter = sc2.getColumnExpression();
                        final Vector vInner = new Vector();
                        final Vector vOuter2 = new Vector();
                        if (vOuter != null) {
                            for (int j3 = 0; j3 < vOuter.size(); ++j3) {
                                final Object oo2 = vOuter.get(j3);
                                if (oo2 instanceof String) {
                                    final String sce = oo2.toString();
                                    if (sce.equalsIgnoreCase("=")) {
                                        if (vOuter.elementAt(j3 - 1) instanceof TableColumn) {
                                            vOuter2.add(vOuter.elementAt(j3 - 1));
                                            vOuter2.add(vOuter.elementAt(j3));
                                            vInner.setElementAt(" ", j3 - 1);
                                        }
                                    }
                                    else if (sce.trim().endsWith(".*")) {
                                        selectStarPresent = true;
                                        vInner.add(j3, vOuter.get(j3));
                                    }
                                    else {
                                        vInner.add(j3, vOuter.get(j3));
                                    }
                                }
                                else {
                                    TableColumn tc2 = null;
                                    if (oo2 instanceof TableColumn) {
                                        tc2 = (TableColumn)oo2;
                                    }
                                    if (sc2.getAliasName() != null && !sc2.getAliasName().trim().equalsIgnoreCase("")) {
                                        final TableColumn newTc = new TableColumn();
                                        newTc.setColumnName(sc2.getAliasName());
                                        sc2.setAliasName(null);
                                        sc2.setIsAS(null);
                                        vOuter2.add(newTc);
                                        sc2.setColumnExpression(vOuter2);
                                    }
                                    else if (tc2 != null && tc2.getTableName() != null) {
                                        final TableColumn newTc = new TableColumn();
                                        newTc.setColumnName(tc2.getColumnName());
                                        vOuter2.add(newTc);
                                        sc2.setColumnExpression(vOuter2);
                                    }
                                    vInner.add(j3, vOuter.get(j3));
                                }
                            }
                            newSc.setColumnExpression(vInner);
                        }
                        innerItems2.add(newSc);
                        outerItems1.add(sc2);
                    }
                }
                if (selectStarPresent) {
                    final SelectColumn outerSC = new SelectColumn();
                    final Vector outerColumnExp = new Vector();
                    outerColumnExp.add("*");
                    outerSC.setColumnExpression(outerColumnExp);
                    outerItems1.clear();
                    outerItems1.add(outerSC);
                }
            }
            newSS.setSelectItemList(outerItems1);
            final SelectStatement ss = subSQS.getSelectStatement();
            ss.setSelectItemList(innerItems2);
            subSQS.setSelectStatement(ss);
            sqs_oracle_sql.setSelectStatement(newSS);
            final WhereExpression subWE = subSQS.getWhereExpression();
            if (subWE != null) {
                final Vector wi = subWE.getWhereItems();
                final WhereExpression we = new WhereExpression();
                final Vector newWI = new Vector();
                if (wi != null) {
                    for (int n = 0; n < wi.size(); ++n) {
                        final Object wItem = wi.get(n);
                        if (wItem instanceof WhereItem) {
                            final WhereColumn wCol = ((WhereItem)wItem).getLeftWhereExp();
                            if (wCol != null) {
                                final Vector colExprr = wCol.getColumnExpression();
                                if (colExprr != null && colExprr.size() > 0 && colExprr.get(0).toString().trim().equalsIgnoreCase("rownum")) {
                                    newWI.add(wItem);
                                    wi.remove(wItem);
                                    final Vector op = subWE.getOperator();
                                    if (op != null && op.size() > 0) {
                                        op.remove(op.size() - 1);
                                    }
                                }
                            }
                        }
                    }
                    we.setWhereItem(newWI);
                    sqs_oracle_sql.setWhereExpression(we);
                }
            }
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i5 = 0; i5 < this.computeByVector.size(); ++i5) {
                computeByStatementVector.add(this.computeByVector.get(i5).toOracleSelect(sqs_oracle_sql, this));
            }
            if (!topWithOrderBy) {
                sqs_oracle_sql.setComputeByStatements(computeByStatementVector);
                sqs_oracle_sql.setCommentForCompute(true);
            }
            else {
                subSQS.setComputeByStatements(computeByStatementVector);
                subSQS.setCommentForCompute(true);
            }
        }
        if (this.group_by_statement != null) {
            if (!topWithOrderBy) {
                sqs_oracle_sql.setGroupByStatement(this.group_by_statement.toOracleSelect(sqs_oracle_sql, this));
                if (sqs_oracle_sql.getGroupByStatement() != null && sqs_oracle_sql.getGroupByStatement().getGroupingSetClause() == null) {
                    this.handleGroupByStatement(sqs_oracle_sql, sqs_oracle_sql.getGroupByStatement());
                }
            }
            else {
                subSQS.setGroupByStatement(this.group_by_statement.toOracleSelect(sqs_oracle_sql, this));
                if (subSQS.getGroupByStatement() != null && subSQS.getGroupByStatement().getGroupingSetClause() == null) {
                    this.handleGroupByStatement(subSQS, subSQS.getGroupByStatement());
                }
            }
        }
        if (this.set_operator_clause != null && !this.set_operator_clause.getCheckSetOperator()) {
            if (!topWithOrderBy) {
                sqs_oracle_sql.setSetOperatorClause(this.set_operator_clause.toOracleSelect(sqs_oracle_sql, this));
            }
            else {
                subSQS.setSetOperatorClause(this.set_operator_clause.toOracleSelect(sqs_oracle_sql, this));
            }
        }
        if (!topWithOrderBy) {
            if (sqs_oracle_sql.getSetOperatorClause() != null && sqs_oracle_sql.getOrderByStatement() != null) {
                this.changeOrderByColForSOC(sqs_oracle_sql);
            }
        }
        else if (subSQS.getSetOperatorClause() != null && subSQS.getOrderByStatement() != null) {
            this.changeOrderByColForSOC(subSQS);
        }
        if (this.having_statement != null) {
            if (!topWithOrderBy) {
                sqs_oracle_sql.setHavingStatement(this.having_statement.toOracleSelect(sqs_oracle_sql, this));
            }
            else {
                subSQS.setHavingStatement(this.having_statement.toOracleSelect(sqs_oracle_sql, this));
            }
        }
        if (this.into_statement != null) {
            if (!topWithOrderBy) {
                if (!SwisSQLOptions.PLSQL) {
                    sqs_oracle_sql.setIntoStatement(this.into_statement.toOracleSelect(sqs_oracle_sql, this));
                }
                else {
                    sqs_oracle_sql.setIntoStatement(this.into_statement);
                }
            }
            else if (!SwisSQLOptions.PLSQL) {
                subSQS.setIntoStatement(this.into_statement.toOracleSelect(sqs_oracle_sql, this));
            }
            else {
                subSQS.setIntoStatement(this.into_statement);
            }
        }
        if (this.for_update_statement != null) {
            final ForUpdateStatement fus = this.for_update_statement;
            if (fus.getForUpdateClause() != null) {
                final String forUpdateClause = fus.getForUpdateClause();
                if (forUpdateClause.equalsIgnoreCase("FOR READ")) {
                    sqs_oracle_sql.setForUpdateStatement(null);
                }
                else {
                    sqs_oracle_sql.setForUpdateStatement(this.for_update_statement.toOracleSelect(sqs_oracle_sql, this));
                }
            }
            else {
                sqs_oracle_sql.setForUpdateStatement(this.for_update_statement.toOracleSelect(sqs_oracle_sql, this));
            }
        }
        if (this.hierarchical_query_clause != null) {
            sqs_oracle_sql.setHierarchicalQueryClause(this.hierarchical_query_clause.toOracleSelect(sqs_oracle_sql, this));
        }
        if (this.limit_clause != null) {
            sqs_oracle_sql.setLimitClause(this.limit_clause.toOracleSelect(sqs_oracle_sql, this));
        }
        if (this.fetch_clause != null) {
            sqs_oracle_sql.setFetchClause(this.fetch_clause.toOracleSelect(sqs_oracle_sql, this));
        }
        sqs_oracle_sql.setOptionalHintClause(null);
        sqs_oracle_sql.setObjectContext(this.objectContext);
        return sqs_oracle_sql;
    }
    
    public SelectQueryStatement toTimesTenSelect() throws ConvertException {
        final SelectQueryStatement sqs_timesten_sql = new SelectQueryStatement();
        sqs_timesten_sql.setObjectContext(this.objectContext);
        sqs_timesten_sql.setCommentClass(this.commentObject);
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(10);
        }
        sqs_timesten_sql.setSQLDialect(10);
        sqs_timesten_sql.setSelectStatement(this.select_statement.toTimesTenSelect(sqs_timesten_sql, this));
        String rowSpecifier = null;
        boolean aggreFnExistsInSelCol = false;
        if (this.getSelectStatement() != null) {
            final SelectStatement ss = this.getSelectStatement();
            rowSpecifier = ss.getSelectRowSpecifier();
            final Vector sItems = ss.getSelectItemList();
            if (sItems != null) {
                for (int i = 0; i < sItems.size(); ++i) {
                    final Object obj = sItems.get(i);
                    if (obj instanceof SelectColumn) {
                        final SelectColumn sc = (SelectColumn)obj;
                        final Vector colExpr = sc.getColumnExpression();
                        if (colExpr != null) {
                            for (int j = 0; j < colExpr.size(); ++j) {
                                final Object subObj = colExpr.get(j);
                                if (subObj instanceof SelectQueryStatement) {
                                    throw new ConvertException("\nSubqueries are not allowed in select columns in TimesTen 5.1.21\n");
                                }
                                if (subObj instanceof FunctionCalls) {
                                    aggreFnExistsInSelCol = this.isAggreFnExists((FunctionCalls)subObj);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.from_clause == null) {
            final FromClause fc = new FromClause();
            final FromTable ft = new FromTable();
            final Vector fil = new Vector();
            fc.setFromClause("FROM");
            ft.setTableName("MONITOR");
            fil.addElement(ft);
            fc.setFromItemList(fil);
            sqs_timesten_sql.setFromClause(fc);
        }
        else {
            final FromClause fc = this.getFromClause();
            final Vector fromItems = fc.getFromItemList();
            int tableCount = 0;
            for (int k = 0; k < fromItems.size(); ++k) {
                if (fromItems.get(k) instanceof FromTable) {
                    final Object sourceTable = fromItems.get(k).getTableName();
                    if (sourceTable instanceof SelectQueryStatement) {
                        final CreateQueryStatement cqs = SwisSQLUtils.constructCQS("ADV_SQSTABLE_" + ++tableCount, (SelectQueryStatement)sourceTable, sqs_timesten_sql).toTimesTenCreate();
                        sqs_timesten_sql.addCreateForSubQuery(cqs);
                        final FromTable ft2 = new FromTable();
                        ft2.setTableName("ADV_SQSTABLE_" + tableCount);
                        fromItems.setElementAt(ft2, k);
                        final InsertQueryStatement iqs = new InsertQueryStatement();
                        final InsertClause ic = new InsertClause();
                        final OptionalSpecifier optionalSp = new OptionalSpecifier();
                        final TableClause tc = new TableClause();
                        final TableExpression tableExp = new TableExpression();
                        ic.setInsert("INSERT");
                        optionalSp.setInto("INTO");
                        final TableObject tableObj = new TableObject();
                        tableObj.setTableName("ADV_SQSTABLE_" + tableCount);
                        final ArrayList tableExpList = new ArrayList();
                        tc.setTableObject(tableObj);
                        tableExpList.add(tc);
                        tableExp.setTableClauseList(tableExpList);
                        ic.setOptionalSpecifier(optionalSp);
                        ic.setTableExpression(tableExp);
                        final SelectQueryStatement subSQS = (SelectQueryStatement)sourceTable;
                        final SelectStatement ss2 = subSQS.getSelectStatement();
                        final Vector sourceSItems = ss2.getSelectItemList();
                        boolean isAliasExists = false;
                        for (int l = 0; l < sourceSItems.size(); ++l) {
                            final Object sourceObj = sourceSItems.get(l);
                            if (sourceObj instanceof SelectColumn && ((SelectColumn)sourceObj).getAliasName() != null) {
                                isAliasExists = true;
                                break;
                            }
                        }
                        if (!isAliasExists) {
                            final Vector newSelItems = new Vector();
                            final Vector colNames = cqs.getColumnNames();
                            for (int m = 0; m < colNames.size(); ++m) {
                                final TableColumn tCol = new TableColumn();
                                tCol.setColumnName(colNames.get(m).getColumnName());
                                final SelectColumn sCol = new SelectColumn();
                                final Vector colExpr2 = new Vector();
                                colExpr2.add(tCol);
                                sCol.setColumnExpression(colExpr2);
                                if (m != colNames.size() - 1) {
                                    sCol.setEndsWith(",");
                                }
                                newSelItems.add(sCol);
                            }
                            ss2.setSelectItemList(newSelItems);
                        }
                        iqs.setSelectQueryStatement(subSQS.toTimesTenSelect());
                        iqs.setInsertClause(ic);
                        sqs_timesten_sql.addInsertForSubQuery(iqs);
                        final DropStatement dropStt = new DropStatement();
                        dropStt.setDrop("DROP");
                        dropStt.setTableOrSequence("TABLE");
                        final Vector tableObjVector = new Vector();
                        final TableObject tabObj = new TableObject();
                        tabObj.setTableName("ADV_SQSTABLE_" + tableCount);
                        tableObjVector.add(tabObj);
                        dropStt.setTableNameVector(tableObjVector);
                        sqs_timesten_sql.addDropSttForSubQuery(dropStt);
                    }
                }
            }
            sqs_timesten_sql.setFromClause(fc.toTimesTenSelect(sqs_timesten_sql, this));
        }
        if (this.whereExpression != null && !this.whereExpression.getCheckWhere()) {
            if (sqs_timesten_sql.getWhereExpression() == null) {
                sqs_timesten_sql.setWhereExpression(this.whereExpression.toTimesTenSelect(sqs_timesten_sql, this));
            }
            else if (rowSpecifier == null || !rowSpecifier.trim().equalsIgnoreCase("FIRST")) {
                sqs_timesten_sql.getWhereExpression().addOperator("AND");
                this.whereExpression.setCloseBrace(")");
                this.whereExpression.setOpenBrace("(");
                sqs_timesten_sql.getWhereExpression().addWhereExpression(this.whereExpression.toTimesTenSelect(sqs_timesten_sql, this));
            }
        }
        if (this.order_by_statement != null) {
            sqs_timesten_sql.setOrderByStatement(this.order_by_statement.toTimesTenSelect(sqs_timesten_sql, this));
        }
        if (this.computeByVector != null) {
            for (int i2 = 0; i2 < this.computeByVector.size(); ++i2) {
                final Object obj2 = this.computeByVector.get(i2);
                if (obj2 instanceof ComputeByStatement) {
                    final ComputeByStatement cbs = (ComputeByStatement)obj2;
                    final Vector fnNames = cbs.getFunctionNameVector();
                    if (fnNames != null) {
                        GroupByStatement newGrpBy = null;
                        final Vector colNames2 = cbs.getTableNameVector();
                        if (colNames2 != null) {
                            newGrpBy = new GroupByStatement();
                            newGrpBy.setGroupClause("GROUP BY");
                            newGrpBy.setGroupByItemList(colNames2);
                            if (this.getGroupByStatement() == null) {
                                sqs_timesten_sql.setGroupByStatement(newGrpBy);
                                if (!this.selectColumnsExistsInGroupBy(sqs_timesten_sql, colNames2)) {
                                    if (aggreFnExistsInSelCol) {
                                        throw new ConvertException("\n In TimesTen, all the items in select-list should also exists in group-by items except for the aggregate functions.\n Unlike other database, Sybase accepts the syntax where in it is not necessary that all the items in\n select-list should exists in group-by (or) compute-by items. Hence on conversion of such Sybase queries to\n TimesTen by adding the left out items from the select-list to group-by will affect the actual result set.\n Another alternative solution is to use the 'SELECT queries(sub queries)' instead of aggregate functions \n in the select-list. But using sub-queries in select-list is not supported in TimesTen 5.1.21\n\n Eg : SELECT col1, col2, max(col3) from test group by col1 // Sybase Query, the column 'col2' is not in group-by items\n\n Alternate Solution : SELECT col1, col2, (SELECT max(col3) from test where col1 = a.col1) from test a\n\n");
                                    }
                                    sqs_timesten_sql.setGroupByStatement(null);
                                }
                            }
                        }
                        this.multipleQuery = "";
                        for (int j = 0; j < fnNames.size(); ++j) {
                            final SelectQueryStatement newSQS = new SelectQueryStatement();
                            final SelectStatement newSS = new SelectStatement();
                            newSS.setSelectClause("SELECT");
                            final SelectColumn newSc = new SelectColumn();
                            final Vector newColExpr = new Vector();
                            newColExpr.add(fnNames.get(j));
                            newSc.setColumnExpression(newColExpr);
                            final Vector sItems2 = new Vector();
                            sItems2.add(newSc);
                            newSS.setSelectItemList(sItems2);
                            newSQS.setSelectStatement(newSS);
                            newSQS.setFromClause(sqs_timesten_sql.getFromClause());
                            newSQS.setWhereExpression(sqs_timesten_sql.getWhereExpression());
                            if (newGrpBy != null) {
                                newSQS.setGroupByStatement(newGrpBy);
                            }
                            this.multipleQuery = this.multipleQuery + "\n" + newSQS.toString().trim() + ";\n";
                        }
                        sqs_timesten_sql.setMultipleQuery(this.multipleQuery);
                    }
                }
            }
            sqs_timesten_sql.setComputeByStatements(null);
        }
        if (this.group_by_statement != null) {
            sqs_timesten_sql.setGroupByStatement(this.group_by_statement.toTimesTenSelect(sqs_timesten_sql, this));
            final GroupByStatement gbs = sqs_timesten_sql.getGroupByStatement();
            if (gbs != null) {
                final Vector groupItems = gbs.getGroupByItemList();
                if (!this.selectColumnsExistsInGroupBy(sqs_timesten_sql, groupItems)) {
                    if (aggreFnExistsInSelCol) {
                        throw new ConvertException("\n In TimesTen, all the items in select-list should also exists in group-by items except for the aggregate functions.\n Unlike other database, Sybase accepts the syntax where in it is not necessary that all the items in\n select-list should exists in group-by (or) compute-by items. Hence on conversion of such Sybase queries to\n TimesTen by adding the left out items from the select-list to group-by will affect the actual result set.\n Another alternative solution is to use the 'SELECT queries(sub queries)' instead of aggregate functions \n in the select-list. But using sub-queries in select-list is not supported in TimesTen 5.1.21\n\n Eg : SELECT col1, col2, max(col3) from test group by col1 // Sybase Query, the column 'col2' is not in group-by items\n\n Alternate Solution : SELECT col1, col2, (SELECT max(col3) from test where col1 = a.col1) from test a\n\n");
                    }
                    sqs_timesten_sql.setGroupByStatement(null);
                }
            }
        }
        if (this.set_operator_clause != null && !this.set_operator_clause.getCheckSetOperator()) {
            sqs_timesten_sql.setSetOperatorClause(this.set_operator_clause.toTimesTenSelect(sqs_timesten_sql, this));
        }
        if (sqs_timesten_sql.getSetOperatorClause() != null && sqs_timesten_sql.getOrderByStatement() != null) {
            this.changeOrderByColForSOC(sqs_timesten_sql);
        }
        if (this.having_statement != null) {
            sqs_timesten_sql.setHavingStatement(this.having_statement.toTimesTenSelect(sqs_timesten_sql, this));
        }
        if (this.into_statement != null) {
            if (!SwisSQLOptions.PLSQL) {
                sqs_timesten_sql.setIntoStatement(this.into_statement.toTimesTenSelect(sqs_timesten_sql, this));
            }
            else {
                sqs_timesten_sql.setIntoStatement(this.into_statement);
            }
        }
        if (this.for_update_statement != null) {
            sqs_timesten_sql.setForUpdateStatement(this.for_update_statement.toTimesTenSelect(sqs_timesten_sql, this));
        }
        if (this.hierarchical_query_clause != null) {
            sqs_timesten_sql.setHierarchicalQueryClause(this.hierarchical_query_clause.toTimesTenSelect(sqs_timesten_sql, this));
        }
        if (this.limit_clause != null) {
            sqs_timesten_sql.setLimitClause(this.limit_clause.toTimesTenSelect(sqs_timesten_sql, this));
        }
        if (this.fetch_clause != null) {
            sqs_timesten_sql.setFetchClause(this.fetch_clause.toTimesTenSelect(sqs_timesten_sql, this));
        }
        if (this.atIsolation != null && this.isolationReadLevel.equalsIgnoreCase("UNCOMMITTED")) {
            final ForUpdateStatement forUpdateStt = new ForUpdateStatement();
            forUpdateStt.setForUpdateClause("FOR UPDATE");
            sqs_timesten_sql.setForUpdateStatement(forUpdateStt);
        }
        sqs_timesten_sql.setOptionalHintClause(null);
        sqs_timesten_sql.setObjectContext(this.objectContext);
        return sqs_timesten_sql;
    }
    
    public SelectQueryStatement toNetezzaSelect() throws ConvertException {
        final SelectQueryStatement sqs_netezza_sql = new SelectQueryStatement();
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(11);
        }
        sqs_netezza_sql.setCommentClass(this.commentObject);
        sqs_netezza_sql.setSelectStatement(this.select_statement.toNetezzaSelect(sqs_netezza_sql, this));
        if (this.from_clause != null) {
            sqs_netezza_sql.setFromClause(this.from_clause.toNetezzaSelect(sqs_netezza_sql, this));
        }
        if (this.whereExpression != null) {
            sqs_netezza_sql.setWhereExpression(this.whereExpression.toNetezzaSelect(sqs_netezza_sql, this));
        }
        if (this.order_by_statement != null) {
            sqs_netezza_sql.setOrderByStatement(this.order_by_statement.toNetezzaSelect(sqs_netezza_sql, this));
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                computeByStatementVector.add(this.computeByVector.get(i).toNetezzaSelect(sqs_netezza_sql, this));
            }
            sqs_netezza_sql.setComputeByStatements(computeByStatementVector);
            sqs_netezza_sql.setCommentForCompute(true);
        }
        if (sqs_netezza_sql.getGroupByStatement() == null && this.group_by_statement != null) {
            sqs_netezza_sql.setGroupByStatement(this.group_by_statement.toNetezzaSelect(sqs_netezza_sql, this));
        }
        if (this.set_operator_clause != null) {
            sqs_netezza_sql.setSetOperatorClause(this.set_operator_clause.toNetezzaSelect(sqs_netezza_sql, this));
        }
        if (this.having_statement != null) {
            sqs_netezza_sql.setHavingStatement(this.having_statement.toNetezzaSelect(sqs_netezza_sql, this));
        }
        if (this.into_statement != null) {
            sqs_netezza_sql.setIntoStatement(this.into_statement.toNetezzaSelect(sqs_netezza_sql, this));
        }
        if (this.hierarchical_query_clause != null) {
            throw new ConvertException();
        }
        if (this.limit_clause != null) {
            sqs_netezza_sql.setLimitClause(this.limit_clause.toNetezzaSelect(sqs_netezza_sql, this));
        }
        if (this.fetch_clause != null) {
            throw new ConvertException();
        }
        if (this.for_update_statement != null) {
            this.for_update_statement = null;
        }
        sqs_netezza_sql.setOptionalHintClause(null);
        return sqs_netezza_sql;
    }
    
    private void handleGroupByStatement(final SelectQueryStatement to_sqs, final GroupByStatement to_group_by_statement) {
        final Vector groupItems = to_group_by_statement.getGroupByItemList();
        if (!this.selectColumnsExistsInGroupBy(to_sqs, groupItems)) {
            final SelectStatement ss = to_sqs.getSelectStatement();
            final Vector selectItems = ss.getSelectItemList();
            int count = 0;
            for (int l = 0; l < selectItems.size(); ++l) {
                final Object sobj = selectItems.get(l);
                if (sobj instanceof SelectColumn) {
                    final SelectColumn scol = (SelectColumn)sobj;
                    final Vector colExprn = scol.getColumnExpression();
                    for (int s = 0; s < colExprn.size(); ++s) {
                        final Object sObj = colExprn.get(s);
                        if (sObj instanceof FunctionCalls) {
                            final FunctionCalls fc = (FunctionCalls)sObj;
                            final TableColumn tfc = fc.getFunctionName();
                            if (tfc != null) {
                                final String fnName = tfc.getColumnName();
                                if (fnName.equalsIgnoreCase("min") || fnName.equalsIgnoreCase("max") || fnName.equalsIgnoreCase("count") || fnName.equalsIgnoreCase("avg") || fnName.equalsIgnoreCase("sum")) {
                                    final SelectQueryStatement newSQS = new SelectQueryStatement();
                                    final SelectStatement newSS = new SelectStatement();
                                    newSS.setSelectClause("SELECT");
                                    final Vector newSelItems = new Vector();
                                    final SelectColumn sCol = new SelectColumn();
                                    final Vector newColExprn = new Vector();
                                    newColExprn.add(sObj);
                                    sCol.setColumnExpression(newColExprn);
                                    newSelItems.add(sCol);
                                    newSS.setSelectItemList(newSelItems);
                                    newSQS.setSelectStatement(newSS);
                                    final FromClause fromClause = to_sqs.getFromClause();
                                    final Vector fromItems = fromClause.getFromItemList();
                                    final FromClause newFC = new FromClause();
                                    newFC.setFromClause("FROM");
                                    final Vector newFromItems = new Vector();
                                    newFC.setFromItemList(newFromItems);
                                    newSQS.setFromClause(newFC);
                                    FromTable ft = null;
                                    TableColumn tcfn = null;
                                    final Vector fnargs = fc.getFunctionArguments();
                                    boolean count_star = false;
                                    if (fnName.equalsIgnoreCase("count") && fnargs != null && fnargs.size() > 0 && fnargs.get(0).toString().trim().equals("*")) {
                                        count_star = true;
                                    }
                                    else {
                                        final Object argobj = fc.getFunctionArguments().get(0);
                                        if (argobj instanceof SelectColumn) {
                                            tcfn = this.getTableColumn((SelectColumn)argobj);
                                        }
                                        else if (argobj instanceof String) {
                                            tcfn = new TableColumn();
                                            tcfn.setColumnName(argobj.toString());
                                        }
                                    }
                                    if (fromItems.size() == 1) {
                                        if (fromItems.get(0) instanceof FromTable) {
                                            ft = fromItems.get(0);
                                        }
                                    }
                                    else if (tcfn != null) {
                                        final String tableName = tcfn.getTableName();
                                        if (tableName != null) {
                                            ft = this.getFromTable(fromItems, tableName);
                                        }
                                        else {
                                            final String columnName = tcfn.getColumnName();
                                            ft = MetadataInfoUtil.getTableOfColumn(to_sqs, columnName);
                                        }
                                    }
                                    if (tcfn != null) {
                                        tcfn.setTableName(null);
                                    }
                                    FromTable newFromTable = new FromTable();
                                    if (ft != null) {
                                        newFromTable.setTableName(ft.getTableName());
                                        newFromItems.add(newFromTable);
                                        newFromTable.setAliasName("adv_alias_" + ++count);
                                    }
                                    else if (count_star) {
                                        for (int f = 0; f < fromItems.size(); ++f) {
                                            final Object frmObj = fromItems.get(f);
                                            if (frmObj instanceof FromTable) {
                                                final FromTable newFrmTable = new FromTable();
                                                newFrmTable.setTableName(((FromTable)frmObj).getTableName());
                                                newFrmTable.setAliasName("adv_alias_" + ++count);
                                                newFromItems.add(newFrmTable);
                                            }
                                        }
                                    }
                                    else {
                                        to_sqs.setGeneralComments("/* SwisSQL Message : Metadata required for accurate conversions */");
                                    }
                                    final WhereExpression we = new WhereExpression();
                                    for (int n = 0; n < groupItems.size(); ++n) {
                                        final Object gObj = groupItems.get(n);
                                        if (gObj instanceof SelectColumn) {
                                            final WhereItem wi = new WhereItem();
                                            final WhereColumn lwc = new WhereColumn();
                                            final Vector newlColExpr = new Vector();
                                            final WhereColumn rwc = new WhereColumn();
                                            final Vector newrColExpr = new Vector();
                                            final Vector gColExpr = ((SelectColumn)gObj).getColumnExpression();
                                            for (int r = 0; r < gColExpr.size(); ++r) {
                                                final Object object = gColExpr.get(r);
                                                if (object instanceof TableColumn) {
                                                    final String gColName = ((TableColumn)object).getColumnName();
                                                    final String gTabName = ((TableColumn)object).getTableName();
                                                    if (ft != null || count_star) {
                                                        if (fromItems.size() > 1) {
                                                            if (gTabName != null) {
                                                                if (count_star) {
                                                                    ft = this.getFromTable(fromItems, gTabName);
                                                                }
                                                                else if (ft != null && ft.getAliasName() != null) {
                                                                    if (!ft.getAliasName().equalsIgnoreCase(gTabName)) {
                                                                        continue;
                                                                    }
                                                                }
                                                                else if (ft != null && !ft.getTableName().toString().equalsIgnoreCase(gTabName)) {
                                                                    continue;
                                                                }
                                                            }
                                                            else {
                                                                final FromTable ftab = MetadataInfoUtil.getTableOfColumn(to_sqs, (TableColumn)object);
                                                                if (ftab != null) {
                                                                    if (count_star) {
                                                                        ft = this.getFromTable(fromItems, ftab.getTableName().toString());
                                                                    }
                                                                    else if (ft != null && !ftab.getTableName().toString().equalsIgnoreCase(ft.getTableName().toString())) {
                                                                        continue;
                                                                    }
                                                                }
                                                                else {
                                                                    to_sqs.setGeneralComments("/* SwisSQL Message : Metadata might be required for accurate conversions. */");
                                                                }
                                                            }
                                                            if (ft != null && count_star) {
                                                                newFromTable = this.getFromTable(newFromItems, ft.getTableName().toString());
                                                            }
                                                        }
                                                        if (ft != null) {
                                                            final TableColumn newrTC = new TableColumn();
                                                            newrTC.setColumnName(gColName);
                                                            if (ft.getAliasName() != null) {
                                                                newrTC.setTableName(ft.getAliasName());
                                                            }
                                                            else {
                                                                newrTC.setTableName(ft.getTableName().toString());
                                                            }
                                                            newrColExpr.add(newrTC);
                                                            final TableColumn newlTC = new TableColumn();
                                                            newlTC.setColumnName(gColName);
                                                            newlTC.setTableName(newFromTable.getAliasName());
                                                            newlColExpr.add(newlTC);
                                                        }
                                                    }
                                                }
                                                else {
                                                    newrColExpr.add(object);
                                                    newlColExpr.add(object);
                                                }
                                            }
                                            if (newlColExpr.size() > 0) {
                                                if (we.getWhereItems().size() > 0) {
                                                    we.addOperator("AND");
                                                }
                                                rwc.setColumnExpression(newrColExpr);
                                                lwc.setColumnExpression(newlColExpr);
                                                wi.setLeftWhereExp(lwc);
                                                wi.setRightWhereExp(rwc);
                                                wi.setOperator("=");
                                                we.addWhereItem(wi);
                                            }
                                        }
                                    }
                                    newSQS.setWhereExpression(we);
                                    newSQS.setOpenBrace("(");
                                    newSQS.setCloseBrace(")");
                                    colExprn.setElementAt(newSQS.removeIndent(newSQS.toString()), s);
                                    if (scol.getAliasName() == null) {
                                        boolean aliasExists = false;
                                        if (colExprn.size() > 2 && colExprn.get(1).toString().trim().equals("=")) {
                                            aliasExists = true;
                                        }
                                        if (!aliasExists) {
                                            if (count_star) {
                                                scol.setAliasName("adv_count_value");
                                            }
                                            else if (tcfn != null) {
                                                scol.setAliasName(fnName.toLowerCase() + "_" + tcfn.getColumnName().toLowerCase());
                                            }
                                        }
                                    }
                                    to_sqs.setGroupByStatement(null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private FromTable getFromTable(final Vector fromItems, final String tableName) {
        for (int n = 0; n < fromItems.size(); ++n) {
            final Object obj = fromItems.get(n);
            if (obj instanceof FromTable) {
                final FromTable frmTab = (FromTable)obj;
                final String aliasName = frmTab.getAliasName();
                if (aliasName != null && aliasName.equalsIgnoreCase(tableName)) {
                    return frmTab;
                }
            }
        }
        for (int n = 0; n < fromItems.size(); ++n) {
            final Object obj = fromItems.get(n);
            if (obj instanceof FromTable) {
                final FromTable frmTab = (FromTable)obj;
                final Object tblObj = frmTab.getTableName();
                if (tblObj instanceof String && tableName.equalsIgnoreCase(tblObj.toString())) {
                    return frmTab;
                }
            }
        }
        return null;
    }
    
    private TableColumn getTableColumn(final SelectColumn sc) {
        final Vector colExpr = sc.getColumnExpression();
        for (int i = 0; i < colExpr.size(); ++i) {
            final Object obj = colExpr.get(i);
            if (obj instanceof TableColumn) {
                return (TableColumn)obj;
            }
            if (obj instanceof SelectColumn) {
                return this.getTableColumn((SelectColumn)obj);
            }
            if (obj instanceof FunctionCalls) {
                final FunctionCalls fc = (FunctionCalls)obj;
                final Vector fnArgs = fc.getFunctionArguments();
                if (fnArgs != null) {
                    for (int j = 0; j < fnArgs.size(); ++j) {
                        final Object argObj = fnArgs.get(j);
                        if (argObj instanceof SelectColumn) {
                            final TableColumn tc = this.getTableColumn((SelectColumn)argObj);
                            if (tc != null) {
                                return tc;
                            }
                        }
                    }
                }
            }
            else if (obj instanceof SelectQueryStatement) {
                final SelectQueryStatement sqs = (SelectQueryStatement)obj;
                final SelectStatement ss = sqs.getSelectStatement();
                final Vector sItems = ss.getSelectItemList();
                for (int k = 0; k < sItems.size(); ++k) {
                    final Object sobj = sItems.get(k);
                    if (sobj instanceof SelectColumn) {
                        final TableColumn tc2 = this.getTableColumn((SelectColumn)sobj);
                        if (tc2 != null) {
                            return tc2;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private boolean selectColumnsExistsInGroupBy(final SelectQueryStatement to_sqs, final Vector groupItems) {
        if (to_sqs.getSelectStatement() != null) {
            final SelectStatement ss = to_sqs.getSelectStatement();
            final Vector sItems = ss.getSelectItemList();
            if (sItems != null) {
                for (int j = 0; j < sItems.size(); ++j) {
                    final Object sobj = sItems.get(j);
                    if (sobj instanceof SelectColumn) {
                        final SelectColumn sc = (SelectColumn)sobj;
                        final Vector colExpr = sc.getColumnExpression();
                        if (colExpr != null && !this.selectColumnExprExistsInGroupBy(colExpr, groupItems)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private boolean selectColumnExprExistsInGroupBy(final Vector selColExpr, final Vector groupItems) {
        for (int i = 0; i < groupItems.size(); ++i) {
            final Object gObj = groupItems.get(i);
            if (gObj instanceof SelectColumn) {
                final SelectColumn sc = (SelectColumn)gObj;
                final Vector gColExpr = sc.getColumnExpression();
                if (gColExpr.size() > 1) {
                    if (selColExpr.size() == gColExpr.size()) {
                        int j;
                        Object selColObj;
                        Object gColObj;
                        TableColumn tcg;
                        TableColumn tcs;
                        for (j = 0, j = 0; j < selColExpr.size(); ++j) {
                            if (j == 0 && selColExpr.size() > 2 && selColExpr.get(1).toString().trim().equals("=")) {
                                j = 1;
                            }
                            else {
                                selColObj = selColExpr.get(j);
                                gColObj = gColExpr.get(j);
                                if (selColObj instanceof TableColumn && gColObj instanceof TableColumn) {
                                    tcg = (TableColumn)selColObj;
                                    tcs = (TableColumn)gColObj;
                                    if (tcg.getTableName() != null && tcs.getTableName() != null) {
                                        if (tcg.getTableName().equalsIgnoreCase(tcs.getTableName()) && !tcg.getColumnName().equalsIgnoreCase(tcs.getColumnName())) {
                                            break;
                                        }
                                    }
                                    else if (!tcg.getColumnName().equalsIgnoreCase(tcs.getColumnName()) && !tcs.getColumnName().trim().startsWith("@") && !tcs.getColumnName().trim().startsWith(":")) {
                                        break;
                                    }
                                }
                                else if (!selColObj.toString().trim().equalsIgnoreCase(gColObj.toString().trim())) {
                                    break;
                                }
                            }
                        }
                        if (j == selColExpr.size()) {
                            return true;
                        }
                    }
                }
                else if (gColExpr.size() == 1 && gColExpr.get(0) instanceof FunctionCalls) {
                    final FunctionCalls fc = gColExpr.get(0);
                    if (selColExpr.size() == 1 && selColExpr.get(0) instanceof FunctionCalls) {
                        final FunctionCalls fc2 = selColExpr.get(0);
                        if (fc2.getFunctionName() != null) {
                            final String fnName1 = fc2.getFunctionName().getColumnName();
                            if (!fnName1.equalsIgnoreCase("min") && !fnName1.equalsIgnoreCase("max") && !fnName1.equalsIgnoreCase("count") && !fnName1.equalsIgnoreCase("avg") && !fnName1.equalsIgnoreCase("sum") && fc.toString().equalsIgnoreCase(fc2.toString())) {
                                return true;
                            }
                        }
                    }
                    else if (selColExpr.size() == 2 && selColExpr.get(1) instanceof FunctionCalls && selColExpr.get(0).toString().trim().equals("")) {
                        final FunctionCalls fc2 = selColExpr.get(1);
                        if (fc2.getFunctionName() != null) {
                            final String fnName1 = fc2.getFunctionName().getColumnName();
                            if (!fnName1.equalsIgnoreCase("min") && !fnName1.equalsIgnoreCase("max") && !fnName1.equalsIgnoreCase("count") && !fnName1.equalsIgnoreCase("avg") && !fnName1.equalsIgnoreCase("sum") && fc.toString().equalsIgnoreCase(fc2.toString())) {
                                return true;
                            }
                        }
                    }
                }
            }
            else if (gObj instanceof TableColumn && selColExpr.size() == 1 && selColExpr.get(0) instanceof TableColumn) {
                final TableColumn tcg2 = (TableColumn)gObj;
                final TableColumn tcs2 = selColExpr.get(0);
                if (tcg2.getTableName() != null && tcs2.getTableName() != null) {
                    if (tcg2.getTableName().equalsIgnoreCase(tcs2.getTableName()) && tcg2.getColumnName().equalsIgnoreCase(tcs2.getColumnName())) {
                        return true;
                    }
                }
                else {
                    if (tcg2.getColumnName().equalsIgnoreCase(tcs2.getColumnName())) {
                        return true;
                    }
                    if (tcs2.getColumnName().trim().startsWith("@") || tcs2.getColumnName().trim().startsWith(":")) {
                        return true;
                    }
                }
            }
            else if (gObj instanceof FunctionCalls) {
                final FunctionCalls gObjFc = (FunctionCalls)gObj;
                return gObjFc.getFunctionName().getColumnName().equalsIgnoreCase("cube") || gObjFc.getFunctionName().getColumnName().equalsIgnoreCase("rollup");
            }
        }
        final ArrayList tcList = new ArrayList();
        this.getTableColumns(selColExpr, tcList);
        if (tcList.size() == 0) {
            return true;
        }
        boolean exists = false;
        for (int k = 0; k < tcList.size(); ++k) {
            final TableColumn tcs2 = tcList.get(k);
            exists = false;
            for (int l = 0; l < groupItems.size(); ++l) {
                final Object obj = groupItems.get(l);
                if (obj instanceof SelectColumn) {
                    final SelectColumn gObj2 = (SelectColumn)obj;
                    final Vector gColExpr2 = gObj2.getColumnExpression();
                    if (gColExpr2.size() == 1) {
                        final Object gColObj2 = gColExpr2.get(0);
                        if (gColObj2 instanceof TableColumn) {
                            final TableColumn tcg3 = (TableColumn)gColObj2;
                            if (tcg3.getTableName() != null && tcs2.getTableName() != null) {
                                if (tcg3.getTableName().equalsIgnoreCase(tcs2.getTableName()) && tcg3.getColumnName().equalsIgnoreCase(tcs2.getColumnName())) {
                                    exists = true;
                                    break;
                                }
                            }
                            else if (tcg3.getColumnName().equalsIgnoreCase(tcs2.getColumnName())) {
                                exists = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (!exists) {
                break;
            }
        }
        return exists;
    }
    
    private void getTableColumns(final Vector expr, final ArrayList tcList) {
        for (int i = 0; i < expr.size(); ++i) {
            if (i == 0 && expr.size() > 2 && expr.get(1).toString().trim().equals("=")) {
                i = 1;
            }
            else {
                final Object obj = expr.get(i);
                if (obj instanceof TableColumn) {
                    if (!tcList.contains(obj)) {
                        final TableColumn tc = (TableColumn)obj;
                        final String colName = tc.getColumnName();
                        if (!colName.startsWith("@") && !colName.startsWith(":")) {
                            tcList.add(tc);
                        }
                    }
                }
                else if (obj instanceof SelectColumn) {
                    this.getTableColumns(((SelectColumn)obj).getColumnExpression(), tcList);
                }
                else if (obj instanceof FunctionCalls) {
                    final FunctionCalls fc = (FunctionCalls)obj;
                    if (fc.getFunctionName() != null) {
                        final String fnName = fc.getFunctionName().getColumnName();
                        if (!fnName.equalsIgnoreCase("min") && !fnName.equalsIgnoreCase("max") && !fnName.equalsIgnoreCase("count") && !fnName.equalsIgnoreCase("avg") && !fnName.equalsIgnoreCase("sum")) {
                            this.getTableColumns(((FunctionCalls)obj).getFunctionArguments(), tcList);
                        }
                    }
                }
                else if (obj instanceof SelectQueryStatement) {
                    this.getTableColumns(((SelectQueryStatement)obj).getSelectStatement().getSelectItemList(), tcList);
                }
            }
        }
    }
    
    private boolean isAggreFnExists(final FunctionCalls fc) {
        final String fnName = fc.getFunctionName().getColumnName();
        if (fnName.equalsIgnoreCase("min") || fnName.equalsIgnoreCase("max") || fnName.equalsIgnoreCase("count") || fnName.equalsIgnoreCase("avg") || fnName.equalsIgnoreCase("sum")) {
            return true;
        }
        final Vector fnArgs = fc.getFunctionArguments();
        if (fnArgs != null) {
            for (int i = 0; i < fnArgs.size(); ++i) {
                Object obj = fnArgs.get(i);
                if (obj instanceof SelectColumn) {
                    final SelectColumn sc = (SelectColumn)obj;
                    final Vector colExpr = sc.getColumnExpression();
                    for (int j = 0; j < colExpr.size(); ++j) {
                        obj = colExpr.get(j);
                        if (obj instanceof FunctionCalls && this.isAggreFnExists((FunctionCalls)obj)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private void changeOrderByColForSOC(final SelectQueryStatement sqs) {
        final OrderByStatement orderBy = sqs.getOrderByStatement();
        final Vector orderItem = orderBy.getOrderItemList();
        for (int i = 0; i < orderItem.size(); ++i) {
            Object obj = orderItem.get(i);
            if (obj instanceof OrderItem) {
                final OrderItem oi = (OrderItem)obj;
                final SelectColumn sc = oi.getOrderSpecifier();
                final Vector colExpr = sc.getColumnExpression();
                for (int j = 0; j < colExpr.size(); ++j) {
                    obj = colExpr.get(j);
                    if (obj instanceof TableColumn) {
                        TableColumn tc = (TableColumn)obj;
                        tc = this.getOrderByColumnName(sqs, tc);
                    }
                }
            }
        }
    }
    
    private TableColumn getOrderByColumnName(final SelectQueryStatement sqs, final TableColumn tc) {
        tc.setTableName(null);
        final String colName = tc.getColumnName();
        final SelectStatement ss = sqs.getSelectStatement();
        final Vector selItem = ss.getSelectItemList();
        if (selItem != null) {
            for (int i = 0; i < selItem.size(); ++i) {
                Object obj = selItem.get(i);
                if (obj instanceof SelectColumn) {
                    final SelectColumn sc = (SelectColumn)obj;
                    final Vector colExpr = sc.getColumnExpression();
                    for (int j = 0; j < colExpr.size(); ++j) {
                        obj = colExpr.get(j);
                        if (obj instanceof TableColumn) {
                            final TableColumn tabCol = (TableColumn)obj;
                            if (tabCol.getColumnName().equalsIgnoreCase(colName)) {
                                final String aliasName = sc.getAliasName();
                                if (aliasName != null) {
                                    tc.setColumnName(aliasName);
                                }
                                return tc;
                            }
                        }
                    }
                }
            }
        }
        return tc;
    }
    
    private SelectQueryStatement toFormSubQuery(final SelectQueryStatement sqs) {
        (this.subQuery = new SelectQueryStatement()).setSelectStatement(sqs.getSelectStatement());
        this.subQuery.setFromClause(sqs.getFromClause());
        this.subQuery.setOrderByStatement(sqs.getOrderByStatement());
        this.subQuery.setWhereExpression(sqs.getWhereExpression());
        return this.subQuery;
    }
    
    public SelectQueryStatement toInformixSelect() throws ConvertException {
        final SelectQueryStatement informixSelectQueryStatement = new SelectQueryStatement();
        if (this.commentObject != null) {
            this.commentObject.setSQLDialect(6);
        }
        informixSelectQueryStatement.setCommentClass(this.commentObject);
        informixSelectQueryStatement.setSelectStatement(this.select_statement.toInformixSelect(informixSelectQueryStatement, this));
        if (this.from_clause != null) {
            final FromClause fc = new FromClause();
            informixSelectQueryStatement.setFromClause(fc);
            informixSelectQueryStatement.setFromClause(this.from_clause.toInformixSelect(informixSelectQueryStatement, this));
        }
        else {
            final FromClause fc = new FromClause();
            final FromTable ft = new FromTable();
            final Vector fil = new Vector();
            final SelectStatement ifxSelectStatement = informixSelectQueryStatement.getSelectStatement();
            ifxSelectStatement.setSelectRowSpecifier(null);
            ifxSelectStatement.setInformixRowSpecifier("FIRST");
            ifxSelectStatement.setSelectRowCount(1);
            fc.setFromClause("FROM");
            ft.setTableName("SYSTABLES");
            fil.addElement(ft);
            fc.setFromItemList(fil);
            informixSelectQueryStatement.setFromClause(fc);
        }
        if (this.whereExpression != null && !this.whereExpression.getCheckWhere()) {
            if (informixSelectQueryStatement.getWhereExpression() == null) {
                informixSelectQueryStatement.setWhereExpression(this.whereExpression.toInformixSelect(informixSelectQueryStatement, this));
            }
            else {
                informixSelectQueryStatement.getWhereExpression().addOperator("AND");
                informixSelectQueryStatement.getWhereExpression().addWhereExpression(this.whereExpression.toInformixSelect(informixSelectQueryStatement, this));
            }
        }
        if (this.order_by_statement != null) {
            informixSelectQueryStatement.setOrderByStatement(this.order_by_statement.toInformixSelect(informixSelectQueryStatement, this));
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                computeByStatementVector.add(this.computeByVector.get(i).toInformixSelect(informixSelectQueryStatement, this));
            }
            informixSelectQueryStatement.setComputeByStatements(computeByStatementVector);
            informixSelectQueryStatement.setCommentForCompute(true);
        }
        if (this.group_by_statement != null) {
            informixSelectQueryStatement.setGroupByStatement(this.group_by_statement.toInformixSelect(informixSelectQueryStatement, this));
        }
        if (this.set_operator_clause != null && !this.set_operator_clause.getCheckSetOperator()) {
            informixSelectQueryStatement.setSetOperatorClause(this.set_operator_clause.toInformixSelect(informixSelectQueryStatement, this));
        }
        if (this.having_statement != null) {
            informixSelectQueryStatement.setHavingStatement(this.having_statement.toInformixSelect(informixSelectQueryStatement, this));
        }
        if (this.into_statement != null) {
            informixSelectQueryStatement.setIntoStatement(this.into_statement.toInformixSelect(informixSelectQueryStatement, this));
        }
        if (this.for_update_statement != null) {
            informixSelectQueryStatement.setForUpdateStatement(this.for_update_statement.toInformixSelect(informixSelectQueryStatement, this));
        }
        if (this.hierarchical_query_clause != null) {
            informixSelectQueryStatement.setHierarchicalQueryClause(this.hierarchical_query_clause.toInformixSelect(informixSelectQueryStatement, this));
        }
        if (this.limit_clause != null) {
            informixSelectQueryStatement.setLimitClause(this.limit_clause.toInformixSelect(informixSelectQueryStatement, this));
        }
        if (this.fetch_clause != null) {
            informixSelectQueryStatement.setFetchClause(this.fetch_clause.toInformixSelect(informixSelectQueryStatement, this));
        }
        informixSelectQueryStatement.setOptionalHintClause(null);
        return informixSelectQueryStatement;
    }
    
    private String singleQueryIntoMultipleQueriesForPLSQL() {
        return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
    }
    
    public void addDummyWhereItem1Equalto1(final WhereExpression we) {
        final WhereItem wi = new WhereItem();
        final Vector colExp = new Vector();
        colExp.add("1");
        final WhereColumn wc = new WhereColumn();
        wc.setColumnExpression(colExp);
        wi.setLeftWhereExp(wc);
        wi.setRightWhereExp(wc);
        wi.setOperator("=");
        we.addWhereItem(wi);
        we.addOperator("AND");
        final Vector whereItems = we.getWhereItems();
        for (int i = 0; i < whereItems.size(); ++i) {
            final Object obj = whereItems.get(i);
            if (obj instanceof WhereExpression) {
                final WhereExpression localWE = (WhereExpression)obj;
                this.addDummyWhereItem1Equalto1(localWE);
            }
        }
    }
    
    public String getTableNameForXMLElement(final FromClause fc) {
        final String tableNameStr = new String();
        if (fc != null) {
            final Vector fromItems = this.from_clause.getFromItemList();
            if (fromItems != null) {
                for (int i = 0; i < fromItems.size(); ++i) {
                    if (fromItems.get(i) instanceof FromTable) {
                        final FromTable ft = fromItems.get(i);
                        if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                            return ft.getTableName().toString();
                        }
                        if (ft.getAliasName() != null && !ft.getAliasName().equals("")) {
                            return ft.getAliasName();
                        }
                    }
                    else if (fromItems.get(i) instanceof FromClause) {
                        final FromClause subFc = fromItems.get(i);
                        final String str = this.getTableNameForXMLElement(subFc);
                        if (!str.equalsIgnoreCase("")) {
                            return str;
                        }
                    }
                }
            }
        }
        return tableNameStr;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return this.objectContext;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
        this.objectContext = obj;
    }
    
    private void handleRownumConversion(final SelectQueryStatement to_sqs) throws ConvertException {
        final FromClause fc = this.getFromClause();
        final Vector fromItems = fc.getFromItemList();
        final Vector combinedOrderItems = new Vector();
        for (int s = 0; s < fromItems.size(); ++s) {
            final Object obj = fromItems.get(s);
            if (obj instanceof FromTable) {
                final FromTable ft = (FromTable)obj;
                final Object tableName = ft.getTableName();
                if (tableName instanceof SelectQueryStatement) {
                    final SelectQueryStatement subquery = (SelectQueryStatement)tableName;
                    if (subquery.getOrderByStatement() != null) {
                        final OrderByStatement obs = subquery.getOrderByStatement().toTeradataSelect(null, null);
                        final Vector orderItemList = obs.getOrderItemList();
                        for (int i_count = 0; i_count < orderItemList.size(); ++i_count) {
                            final OrderItem oi = orderItemList.elementAt(i_count);
                            if (oi != null) {
                                combinedOrderItems.add(oi);
                            }
                        }
                    }
                }
            }
        }
        if (to_sqs.getOrderByStatement() == null && combinedOrderItems.size() == 0) {
            final String qualifyAlias = "rownum";
            final FunctionCalls countOlap = new FunctionCalls();
            final TableColumn countOlapName = new TableColumn();
            countOlapName.setColumnName("COUNT");
            countOlap.setFunctionName(countOlapName);
            final FunctionCalls countOlapCast = new FunctionCalls();
            final TableColumn countOlapCastName = new TableColumn();
            countOlapCastName.setColumnName("CAST");
            countOlapCast.setFunctionName(countOlapCastName);
            final Vector countOlapCastArgs = new Vector();
            countOlapCastArgs.add("1");
            final NumericClass integerType = new NumericClass();
            integerType.setDatatypeName("INTEGER");
            integerType.setOpenBrace(null);
            integerType.setClosedBrace(null);
            countOlapCastArgs.add(integerType);
            countOlapCast.setAsDatatype("AS");
            countOlapCast.setFunctionArguments(countOlapCastArgs);
            final Vector countOlapArgs = new Vector();
            countOlapArgs.add(countOlapCast);
            countOlap.setFunctionArguments(countOlapArgs);
            countOlap.setOver("OVER");
            final WindowingClause countOlapWindow = new WindowingClause();
            countOlapWindow.setRowsOrRange("ROWS");
            countOlapWindow.setUnbounded("UNBOUNDED");
            countOlapWindow.setPreceding("PRECEDING");
            countOlap.setWindowingClause(countOlapWindow);
            final Vector selectItems = to_sqs.getSelectStatement().getSelectItemList();
            final boolean rownumFound = false;
            for (int j = 0; j < selectItems.size(); ++j) {
                final SelectColumn selCol = selectItems.get(j);
                selCol.replaceRownumTableColumn(countOlap);
            }
            if (to_sqs.getRownumClause() != null) {
                final HavingStatement qualify = new HavingStatement();
                qualify.setHavingClause("QUALIFY");
                final Vector qualifyItems = new Vector();
                final WhereExpression we = new WhereExpression();
                final Vector wiVec = new Vector();
                final WhereItem wi = new WhereItem();
                final WhereColumn lwc = new WhereColumn();
                final Vector lwcColExp = new Vector();
                if (rownumFound) {
                    if (qualifyAlias != null) {
                        lwcColExp.add(qualifyAlias);
                    }
                    else {
                        lwcColExp.add("rownum");
                    }
                }
                else {
                    lwcColExp.add(countOlap);
                }
                lwc.setColumnExpression(lwcColExp);
                wi.setLeftWhereExp(lwc);
                final WhereColumn rwc = new WhereColumn();
                final Vector rwcColExp = new Vector();
                rwcColExp.add(to_sqs.getRownumClause().getRownumValue());
                rwc.setColumnExpression(rwcColExp);
                wi.setRightWhereExp(rwc);
                wi.setOperator(to_sqs.getRownumClause().getOperator());
                wiVec.add(wi);
                we.setWhereItem(wiVec);
                qualifyItems.add(we);
                qualify.setHavingItems(qualifyItems);
                to_sqs.setQualifyStatement(qualify);
            }
        }
        else {
            String qualifyAlias = "ROW_NO";
            final SelectColumn rownumSelCol = new SelectColumn();
            final FunctionCalls rownumFunc = new FunctionCalls();
            final TableColumn rownumFuncName = new TableColumn();
            rownumFuncName.setColumnName("COUNT");
            rownumFunc.setFunctionName(rownumFuncName);
            final FunctionCalls countOlapCast2 = new FunctionCalls();
            final TableColumn countOlapCastName2 = new TableColumn();
            countOlapCastName2.setColumnName("CAST");
            countOlapCast2.setFunctionName(countOlapCastName2);
            final Vector countOlapCastArgs2 = new Vector();
            countOlapCastArgs2.add("1");
            final NumericClass integerType2 = new NumericClass();
            integerType2.setDatatypeName("INTEGER");
            integerType2.setOpenBrace(null);
            integerType2.setClosedBrace(null);
            countOlapCastArgs2.add(integerType2);
            countOlapCast2.setAsDatatype("AS");
            countOlapCast2.setFunctionArguments(countOlapCastArgs2);
            final Vector rownumFuncArgs = new Vector();
            final SelectColumn countOlapCastSC = new SelectColumn();
            final Vector countOlapCastSCExpr = new Vector();
            countOlapCastSCExpr.add(countOlapCast2);
            countOlapCastSC.setColumnExpression(countOlapCastSCExpr);
            rownumFuncArgs.add(countOlapCastSC);
            rownumFunc.setFunctionArguments(rownumFuncArgs);
            if (to_sqs.getOrderByStatement() != null) {
                final OrderByStatement obs2 = to_sqs.getOrderByStatement();
                final OrderByStatement newObs = new OrderByStatement();
                final Vector newOrderItemList = new Vector();
                final Vector orderItemList2 = obs2.getOrderItemList();
                for (int i_count2 = 0; i_count2 < orderItemList2.size(); ++i_count2) {
                    final OrderItem oi2 = orderItemList2.elementAt(i_count2);
                    if (oi2 != null) {
                        newOrderItemList.add(oi2);
                    }
                }
                newObs.setOrderItemList(newOrderItemList);
                newObs.setOrderClause("ORDER BY");
                rownumFunc.setOrderBy(newObs);
            }
            if (combinedOrderItems.size() > 0) {
                final OrderByStatement newObs2 = new OrderByStatement();
                newObs2.setOrderItemList(combinedOrderItems);
                newObs2.setOrderClause("ORDER BY");
                rownumFunc.setOrderBy(newObs2);
            }
            rownumFunc.setOver("OVER");
            final WindowingClause countOlapWindow2 = new WindowingClause();
            countOlapWindow2.setRowsOrRange("ROWS");
            countOlapWindow2.setUnbounded("UNBOUNDED");
            countOlapWindow2.setPreceding("PRECEDING");
            rownumFunc.setWindowingClause(countOlapWindow2);
            final Vector rownumSelColExp = new Vector();
            rownumSelColExp.add(rownumFunc);
            rownumSelCol.setColumnExpression(rownumSelColExp);
            final Vector selectItems2 = to_sqs.getSelectStatement().getSelectItemList();
            final boolean rownumFound2 = false;
            for (int i = 0; i < selectItems2.size(); ++i) {
                final SelectColumn selCol2 = selectItems2.get(i);
                selCol2.replaceRownumTableColumn(rownumSelCol);
            }
            if (!rownumFound2) {
                qualifyAlias = null;
            }
            if (to_sqs.getRownumClause() != null) {
                final HavingStatement qualify2 = new HavingStatement();
                qualify2.setHavingClause("QUALIFY");
                final Vector qualifyItems2 = new Vector();
                final WhereExpression we2 = new WhereExpression();
                final Vector wiVec2 = new Vector();
                final WhereItem wi2 = new WhereItem();
                final WhereColumn lwc2 = new WhereColumn();
                final Vector lwcColExp2 = new Vector();
                if (qualifyAlias == null) {
                    lwcColExp2.add(rownumSelCol);
                }
                else {
                    lwcColExp2.add(qualifyAlias);
                }
                lwc2.setColumnExpression(lwcColExp2);
                wi2.setLeftWhereExp(lwc2);
                final WhereColumn rwc2 = new WhereColumn();
                final Vector rwcColExp2 = new Vector();
                rwcColExp2.add(to_sqs.getRownumClause().getRownumValue());
                rwc2.setColumnExpression(rwcColExp2);
                wi2.setRightWhereExp(rwc2);
                wi2.setOperator(to_sqs.getRownumClause().getOperator());
                wiVec2.add(wi2);
                we2.setWhereItem(wiVec2);
                qualifyItems2.add(we2);
                qualify2.setHavingItems(qualifyItems2);
                to_sqs.setQualifyStatement(qualify2);
            }
            final OrderItem rownumOrderItem = new OrderItem();
            final SelectColumn rownumOrderCol = new SelectColumn();
            final Vector rownumOrderColExp = new Vector();
            rownumOrderColExp.add(qualifyAlias);
            rownumOrderCol.setColumnExpression(rownumSelColExp);
            rownumOrderItem.setOrderSpecifier(rownumOrderCol);
            to_sqs.getOrderByStatement();
        }
    }
    
    private void replaceRownumTableColumn(final SelectColumn sc, final Object newColumn) throws ConvertException {
    }
    
    public boolean isUnionClausePresent() {
        boolean unionClausePresent = false;
        if (this.set_operator_clause != null) {
            unionClausePresent = true;
        }
        else if (this.set_operator_clause == null) {
            unionClausePresent = this.isUnionClausePresentInSubQuery(this);
        }
        return unionClausePresent;
    }
    
    public boolean isUnionClausePresentInSubQuery(final SelectQueryStatement subQueryStmt) {
        boolean unionClausePresent = false;
        final Vector fromItems = subQueryStmt.getFromClause().getFromItemList();
        for (int j = 0; j < fromItems.size(); ++j) {
            if (fromItems.get(j) instanceof FromTable) {
                final FromTable ft = fromItems.get(j);
                if (ft.getTableName() instanceof SelectQueryStatement && ((SelectQueryStatement)ft.getTableName()).getSetOperatorClause() != null) {
                    unionClausePresent = true;
                    break;
                }
                if (ft.getTableName() instanceof SelectQueryStatement) {
                    unionClausePresent = this.isUnionClausePresentInSubQuery((SelectQueryStatement)ft.getTableName());
                    if (unionClausePresent) {
                        break;
                    }
                }
                else if (ft.getTableName() instanceof FromClause) {
                    unionClausePresent = this.isUnionClausePresentInFromClause((FromClause)ft.getTableName());
                    if (unionClausePresent) {
                        break;
                    }
                }
            }
        }
        return unionClausePresent;
    }
    
    private boolean isUnionClausePresentInFromClause(final FromClause fc) {
        boolean unionClausePresent = false;
        final Vector fromItems = fc.getFromItemList();
        for (int j = 0; j < fromItems.size(); ++j) {
            if (fromItems.get(j) instanceof FromTable) {
                final FromTable ft = fromItems.get(j);
                if (ft.getTableName() instanceof SelectQueryStatement && ((SelectQueryStatement)ft.getTableName()).getSetOperatorClause() != null) {
                    unionClausePresent = true;
                    break;
                }
                if (ft.getTableName() instanceof SelectQueryStatement) {
                    unionClausePresent = this.isUnionClausePresentInSubQuery((SelectQueryStatement)ft.getTableName());
                    if (unionClausePresent) {
                        break;
                    }
                }
                else if (ft.getTableName() instanceof FromClause) {
                    unionClausePresent = this.isUnionClausePresentInFromClause((FromClause)ft.getTableName());
                    if (unionClausePresent) {
                        break;
                    }
                }
            }
            else if (fromItems.get(j) instanceof FromClause) {
                unionClausePresent = this.isUnionClausePresentInFromClause(fromItems.get(j));
                if (unionClausePresent) {
                    break;
                }
            }
        }
        return unionClausePresent;
    }
    
    public LinkedList getListOfUnionedSelectQueries() {
        if (this.set_operator_clause != null) {
            this.list = new LinkedList();
            this.set_operator_clause.getSelectQueryStatement().setTopLevel(this.topLevel);
            this.list.add(this.recurseAndGetUnionSQLS(this, this.list, new LinkedList(), true));
            return this.list;
        }
        this.list = new LinkedList();
        return this.getListOfUnionedSelectQueriesInFromClause(this.getFromClause(), this.list);
    }
    
    private LinkedList getListOfUnionedSelectQueriesInFromClause(final FromClause fc, final LinkedList list) {
        final Vector fromItems = fc.getFromItemList();
        for (int j = 0; j < fromItems.size(); ++j) {
            if (fromItems.get(j) instanceof FromTable) {
                final FromTable ft = fromItems.get(j);
                if (ft.getTableName() instanceof SelectQueryStatement) {
                    final LinkedList segList = this.recurseAndGetUnionSQLS((SelectQueryStatement)ft.getTableName(), list, new LinkedList(), true);
                    if (!segList.isEmpty()) {
                        list.add(segList);
                    }
                }
                else if (ft.getTableName() instanceof FromClause) {
                    this.getListOfUnionedSelectQueriesInFromClause((FromClause)ft.getTableName(), list);
                }
            }
            else if (fromItems.get(j) instanceof FromClause) {
                this.getListOfUnionedSelectQueriesInFromClause(fromItems.get(j), list);
            }
        }
        return list;
    }
    
    private LinkedList recurseAndGetUnionSQLS(final SelectQueryStatement sqs, final LinkedList list, final LinkedList segmentList, final boolean first) {
        if (sqs.getSetOperatorClause() != null) {
            final SelectQueryStatement ss = new SelectQueryStatement();
            ss.setTopLevel(sqs.getTopLevel());
            ss.setSelectStatement(sqs.getSelectStatement());
            ss.setFromClause(sqs.getFromClause());
            if (sqs.getWhereExpression() != null) {
                ss.setWhereExpression(sqs.getWhereExpression());
            }
            if (sqs.getOrderByStatement() != null) {
                ss.setOrderByStatement(sqs.getOrderByStatement());
            }
            if (sqs.getGroupByStatement() != null) {
                ss.setGroupByStatement(sqs.getGroupByStatement());
            }
            if (sqs.getHavingStatement() != null) {
                ss.setHavingStatement(sqs.getHavingStatement());
            }
            segmentList.add(ss);
            this.recurseAndGetUnionSQLS(sqs.getSetOperatorClause().getSelectQueryStatement(), list, segmentList, false);
            final Vector fromItems = sqs.getFromClause().getFromItemList();
            for (int j = 0; j < fromItems.size(); ++j) {
                if (fromItems.get(j) instanceof FromTable) {
                    final FromTable ft = fromItems.get(j);
                    if (ft.getTableName() instanceof SelectQueryStatement) {
                        final LinkedList segList = this.recurseAndGetUnionSQLS((SelectQueryStatement)ft.getTableName(), list, new LinkedList(), true);
                        if (!segList.isEmpty()) {
                            list.add(segList);
                        }
                    }
                    else if (ft.getTableName() instanceof FromClause) {
                        this.getListOfUnionedSelectQueriesInFromClause((FromClause)ft.getTableName(), list);
                    }
                }
            }
        }
        else {
            if (!first) {
                segmentList.add(sqs);
            }
            final Vector fromItems2 = sqs.getFromClause().getFromItemList();
            for (int i = 0; i < fromItems2.size(); ++i) {
                if (fromItems2.get(i) instanceof FromTable) {
                    final FromTable ft2 = fromItems2.get(i);
                    if (ft2.getTableName() instanceof SelectQueryStatement) {
                        final LinkedList segList2 = this.recurseAndGetUnionSQLS((SelectQueryStatement)ft2.getTableName(), list, new LinkedList(), true);
                        if (!segList2.isEmpty()) {
                            list.add(segList2);
                        }
                    }
                    else if (ft2.getTableName() instanceof FromClause) {
                        this.getListOfUnionedSelectQueriesInFromClause((FromClause)ft2.getTableName(), list);
                    }
                }
            }
        }
        return segmentList;
    }
    
    private SelectStatement createSelectStatementForDerivedTable(final SelectStatement ssold, final String dummyAliasName) throws ConvertException {
        SelectStatement ssnew = new SelectStatement();
        ssnew = ssold.toTeradataSelect(null, null);
        Vector selectItemList = new Vector();
        selectItemList.addAll(ssnew.getSelectItemList());
        selectItemList = this.replaceTableNameInSelectItems(selectItemList, dummyAliasName);
        ssnew.setSelectItemList(selectItemList);
        return ssnew;
    }
    
    private FromClause createFromClauseForDerivedTable(final Vector v, final SelectQueryStatement sqs, final String dummyAlias) throws ConvertException {
        final SelectStatement ss = new SelectStatement();
        ss.setSelectClause("SELECT");
        Vector v2 = new Vector();
        v2.addAll(v);
        v2 = this.removeFunctionsInSelectItemLists(v2);
        ss.setSelectItemList(v2);
        sqs.setSelectStatement(ss);
        final FromClause fc = new FromClause();
        fc.setFromClause("FROM");
        final FromTable ft = new FromTable();
        final Vector fromItem = new Vector();
        ft.setTableName(sqs);
        ft.setAliasName(dummyAlias);
        fromItem.add(ft);
        fc.setFromItemList(fromItem);
        return fc;
    }
    
    private Vector removeFunctionsInSelectItemLists(final Vector vce) throws ConvertException {
        for (int i = 0, size = vce.size(); i < size; ++i) {
            if (vce.get(i) instanceof SelectColumn) {
                final SelectColumn dummySelectColumn = vce.get(i);
                final Vector vse = dummySelectColumn.getColumnExpression();
                if (dummySelectColumn.getAliasName() != null) {
                    dummySelectColumn.setAliasName(null);
                }
                if (vse.get(0) instanceof FunctionCalls) {
                    int tmp = 0;
                    final FunctionCalls funcCall = vse.get(0);
                    final Vector functionCallColumns = funcCall.getFunctionArguments();
                    vce.removeElementAt(i);
                    --size;
                    for (int k = 0; k < functionCallColumns.size(); ++k) {
                        final SelectColumn functionArgsAsSelectColumn = functionCallColumns.get(k);
                        functionArgsAsSelectColumn.setEndsWith(",");
                        vce.add(i + tmp, functionArgsAsSelectColumn);
                        ++tmp;
                        ++size;
                    }
                    if (funcCall.getPartitionByClause() != null && funcCall.getPartitionByClause().getSelectColumnList() != null) {
                        final ArrayList pSelectItems = funcCall.getPartitionByClause().getSelectColumnList();
                        for (int pos = 0, psize = pSelectItems.size(); pos < psize; ++pos) {
                            if (pSelectItems.get(pos) instanceof SelectColumn) {
                                final SelectColumn sc = pSelectItems.get(pos);
                                sc.setEndsWith(",");
                                vce.add(i + tmp, sc);
                                ++tmp;
                                ++size;
                            }
                        }
                    }
                    if (funcCall.getOrderBy() != null && funcCall.getOrderBy().getOrderItemList() != null) {
                        final Vector orderItemList = funcCall.getOrderBy().getOrderItemList();
                        for (int pos = 0, psize = orderItemList.size(); pos < psize; ++pos) {
                            if (orderItemList.get(pos) instanceof OrderItem) {
                                final OrderItem oi = orderItemList.get(pos);
                                if (oi.getOrderSpecifier() instanceof SelectColumn) {
                                    final SelectColumn orderSpecifierAsSelectColumn = oi.getOrderSpecifier();
                                    orderSpecifierAsSelectColumn.setEndsWith(",");
                                    vce.add(i + tmp, orderSpecifierAsSelectColumn);
                                    ++tmp;
                                    ++size;
                                }
                            }
                        }
                    }
                    --i;
                }
                else if (vse.get(0) instanceof String || this.isDuplicate(vse.get(0), vce, i)) {
                    vce.removeElementAt(i);
                    --size;
                    --i;
                }
                else {
                    dummySelectColumn.setColumnExpression(vse);
                    vce.setElementAt(dummySelectColumn, i);
                }
            }
        }
        if (vce.lastElement() instanceof SelectColumn) {
            final SelectColumn dummySelectColumn2 = vce.lastElement();
            dummySelectColumn2.setEndsWith(null);
            vce.setElementAt(dummySelectColumn2, vce.size() - 1);
        }
        return vce;
    }
    
    private boolean isDuplicate(final TableColumn obj, final Vector vce, final int limit) {
        String colStr = "";
        String objStr = obj.getColumnName().trim();
        for (int i = 0; i < limit; ++i) {
            if (vce.get(i) instanceof SelectColumn) {
                final SelectColumn dummy = vce.get(i);
                final Vector colExp = dummy.getColumnExpression();
                if (colExp.get(0) instanceof TableColumn) {
                    colStr = colExp.get(0).getColumnName();
                    if (objStr.indexOf("\"") != -1) {
                        objStr = objStr.replaceAll("\"", "").trim();
                    }
                    else if (objStr.indexOf("'") != -1) {
                        objStr = objStr.replaceAll("'", "").trim();
                    }
                    if (colStr.indexOf("\"") != -1) {
                        colStr = colStr.replaceAll("\"", "").trim();
                    }
                    else if (colStr.indexOf("'") != -1) {
                        colStr = colStr.replaceAll("'", "").trim();
                    }
                    if (objStr.equalsIgnoreCase(colStr)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private Vector replaceTableNameInSelectItems(final Vector newSelectItems1, final String newTableName) throws ConvertException {
        final Vector newSI = new Vector();
        newSI.addAll(newSelectItems1);
        for (int size = newSI.size(), i = 0; i < size; ++i) {
            if (newSI.get(i) instanceof SelectColumn) {
                final SelectColumn sc = newSI.get(i);
                Vector v_ce = new Vector();
                v_ce.addAll(sc.getColumnExpression());
                v_ce = this.replaceTableNameInSelectItems(v_ce, newTableName);
                sc.setColumnExpression(v_ce);
                newSI.setElementAt(sc.toTeradataSelect(null, null), i);
            }
            else if (newSI.elementAt(i) instanceof FunctionCalls) {
                final FunctionCalls fc = newSI.get(i);
                Vector funArgs = new Vector();
                funArgs.addAll(fc.getFunctionArguments());
                funArgs = this.replaceTableNameInSelectItems(funArgs, newTableName);
                if (fc.getPartitionByClause() != null && fc.getPartitionByClause().getSelectColumnList() != null) {
                    final ArrayList pSelectItems = new ArrayList();
                    pSelectItems.addAll(fc.getPartitionByClause().getSelectColumnList());
                    for (int pos = 0, psize = pSelectItems.size(); pos < psize; ++pos) {
                        if (pSelectItems.get(pos) instanceof SelectColumn) {
                            final SelectColumn sc2 = pSelectItems.get(pos);
                            Vector v_ce2 = new Vector();
                            v_ce2.addAll(sc2.getColumnExpression());
                            v_ce2 = this.replaceTableNameInSelectItems(v_ce2, newTableName);
                            sc2.setColumnExpression(v_ce2);
                            pSelectItems.set(pos, sc2.toTeradataSelect(null, null));
                        }
                    }
                }
                if (fc.getOrderBy() != null && fc.getOrderBy().getOrderItemList() != null) {
                    final Vector orderItemList = fc.getOrderBy().getOrderItemList();
                    for (int pos = 0, psize = orderItemList.size(); pos < psize; ++pos) {
                        if (orderItemList.get(pos) instanceof OrderItem) {
                            final OrderItem oi = orderItemList.get(pos);
                            if (oi.getOrderSpecifier() instanceof SelectColumn) {
                                Vector orderSpecifierList = new Vector();
                                orderSpecifierList.add(oi.getOrderSpecifier());
                                orderSpecifierList = this.replaceTableNameInSelectItems(orderSpecifierList, newTableName);
                                oi.setOrderSpecifier(orderSpecifierList.get(0));
                                orderItemList.set(pos, oi);
                            }
                        }
                    }
                }
            }
            else if (newSI.elementAt(i) instanceof TableColumn) {
                final TableColumn tc = newSI.elementAt(i);
                tc.setTableName(newTableName);
                newSI.setElementAt(tc, i);
            }
        }
        return newSI;
    }
    
    private boolean isCrossJoinAvailable(final SelectQueryStatement sqs_Teradata_sql) {
        FromTable ft = new FromTable();
        FromClause fc = new FromClause();
        if (sqs_Teradata_sql != null && sqs_Teradata_sql.getFromClause() != null) {
            fc = sqs_Teradata_sql.getFromClause();
            final Vector fromItemList = fc.getFromItemList();
            for (int size = fromItemList.size(), i = 0; i < size; ++i) {
                if (fromItemList.get(i) instanceof FromTable) {
                    ft = fromItemList.get(i);
                    if (ft.getCrossJoinForPartitionClause() != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void handleTeradataUnionAllConversion(final LinkedList ArrayOfColumns) throws ConvertException {
        try {
            final LinkedList listOfUnionQueries = ArrayOfColumns;
            if (this.list != null && this.list.size() == listOfUnionQueries.size()) {
                for (int k = 0; k < this.list.size(); ++k) {
                    final LinkedList listOfCols = listOfUnionQueries.get(k);
                    final LinkedList listOfSegments = this.list.get(k);
                    if (listOfCols.size() == listOfSegments.size()) {
                        for (int n = 0; n < listOfCols.size(); ++n) {
                            final SelectQueryStatement sqs = listOfSegments.get(n);
                            final Vector v = listOfCols.get(n);
                            final Vector v2 = v.get(0);
                            final Vector selectItem = sqs.getSelectStatement().getSelectItemList();
                            if (v2.size() == selectItem.size()) {
                                for (int j = 0; j < selectItem.size(); ++j) {
                                    final HashMap h = v2.get(j);
                                    final SelectColumn origSelCol = selectItem.get(j);
                                    if (h.get("Update Flag").toString().equalsIgnoreCase("true") && !origSelCol.isTeradataUnionCastingDone()) {
                                        final SelectColumn castCol = new SelectColumn();
                                        if (origSelCol.getAliasName() != null) {
                                            castCol.setAliasName(origSelCol.getAliasName());
                                        }
                                        else if (origSelCol.getAliasForExpression() != null && origSelCol.getAliasForExpression().length() > 0) {
                                            if (origSelCol.getAliasForExpression().startsWith("\"")) {
                                                castCol.setAliasName(origSelCol.getAliasForExpression());
                                            }
                                            else {
                                                castCol.setAliasName("\"" + origSelCol.getAliasForExpression() + "\"");
                                            }
                                        }
                                        else if (origSelCol.getColumnExpression().size() == 1 && !sqs.getTopLevel()) {
                                            String origSCAlias = origSelCol.getColumnExpression().get(0).toString().replaceAll("\n", " ").replaceAll("\t", " ").trim();
                                            if (origSCAlias.indexOf("*/") != -1) {
                                                origSCAlias = origSCAlias.substring(origSCAlias.lastIndexOf("*/") + 1);
                                            }
                                            if (origSCAlias.indexOf(".") != -1 && origSCAlias.indexOf(".") == origSCAlias.lastIndexOf(".")) {
                                                origSCAlias = origSCAlias.substring(origSCAlias.lastIndexOf(".") + 1);
                                            }
                                            if (origSCAlias.length() > 30) {
                                                origSCAlias = origSCAlias.substring(0, 29);
                                            }
                                            if (origSCAlias.length() > 0) {
                                                if (origSCAlias.startsWith("\"") && origSCAlias.length() > 2) {
                                                    castCol.setAliasName(origSCAlias);
                                                }
                                                else {
                                                    castCol.setAliasName("\"" + origSCAlias + "\"");
                                                }
                                            }
                                        }
                                        final Vector castColExp = new Vector();
                                        final FunctionCalls castFunc = new FunctionCalls();
                                        final TableColumn castTCN = new TableColumn();
                                        castTCN.setColumnName("CAST");
                                        castFunc.setFunctionName(castTCN);
                                        castFunc.setFunctionArguments(origSelCol.getColumnExpression());
                                        final String de = h.get("SQL Type").toString();
                                        final String deName = h.get("SQL Type").toString();
                                        castFunc.getFunctionArguments().add(this.getDataTypeForCasting(deName, h.get("Precision").toString(), h.get("Scale").toString()));
                                        castFunc.setAsDatatype("AS");
                                        castColExp.add(castFunc);
                                        castCol.setColumnExpression(castColExp);
                                        if (origSelCol.getEndsWith() != null) {
                                            castCol.setEndsWith(origSelCol.getEndsWith());
                                        }
                                        castCol.setTeradataUnionCastingDone(true);
                                        selectItem.setElementAt(castCol, j);
                                    }
                                    else if (h.get("Update Flag").toString().equalsIgnoreCase("false") && origSelCol.getAliasName() == null && origSelCol.getAliasForExpression() != null) {
                                        if (origSelCol.getAliasForExpression().startsWith("\"")) {
                                            origSelCol.setAliasName(origSelCol.getAliasForExpression());
                                        }
                                        else {
                                            origSelCol.setAliasName("\"" + origSelCol.getAliasForExpression() + "\"");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            final String message = "Exception occurred while converting Union All queries using Prepared statement metadata. \n More details : \n" + e.getMessage();
            final ConvertException ce = new ConvertException(message);
            ce.setStackTrace(e.getStackTrace());
            throw ce;
        }
    }
    
    private Datatype getDataTypeForCasting(final String dataTypeName, final String precision, final String scale) {
        Datatype dt = null;
        if (dataTypeName.equalsIgnoreCase("decimal") || dataTypeName.equalsIgnoreCase("numeric")) {
            dt = new NumericClass();
            dt.setDatatypeName(dataTypeName);
            ((NumericClass)dt).setPrecision(precision.toString());
            dt.setOpenBrace("(");
            dt.setClosedBrace(")");
            if (!scale.toString().equalsIgnoreCase("0")) {
                ((NumericClass)dt).setScale(scale.toString());
            }
        }
        else if (dataTypeName.equalsIgnoreCase("varchar") || dataTypeName.equalsIgnoreCase("char") || dataTypeName.equalsIgnoreCase("varbyte") || dataTypeName.equalsIgnoreCase("byte")) {
            dt = new CharacterClass();
            dt.setDatatypeName(dataTypeName);
            dt.setSize(precision.toString());
            if (SwisSQLOptions.castCharDatatypeAsCaseSpecific) {
                ((CharacterClass)dt).setCaseSpecificPhrase("CASESPECIFIC");
            }
            dt.setOpenBrace("(");
            dt.setClosedBrace(")");
        }
        else if (dataTypeName.equalsIgnoreCase("timestamp")) {
            dt = new DateClass();
            dt.setDatatypeName(dataTypeName);
            dt.setSize("0");
            dt.setOpenBrace("(");
            dt.setClosedBrace(")");
        }
        else if (dataTypeName.equalsIgnoreCase("date")) {
            dt = new DateClass();
            dt.setDatatypeName(dataTypeName);
        }
        else {
            dt = new NumericClass();
            dt.setDatatypeName(dataTypeName);
        }
        return dt;
    }
    
    public SelectQueryStatement toVectorWiseSelect() throws ConvertException {
        final SelectQueryStatement sqs_vw_sql = new SelectQueryStatement();
        sqs_vw_sql.setSetOperatorQuery(this.isSetOperatorQuery);
        sqs_vw_sql.setCanHandleHavingWithoutGroupBy(this.canHandleHavingWithoutGroupBy);
        sqs_vw_sql.setSelectStatement(this.select_statement.toVectorWiseSelect(sqs_vw_sql, this));
        sqs_vw_sql.setRemovalOptionForOrderAndFetchClauses(this.removalOptionForOrderAndFetchClauses);
        if (this.from_clause != null) {
            sqs_vw_sql.setFromClause(this.from_clause.toVectorWiseSelect(sqs_vw_sql, this));
        }
        if (this.whereExpression != null) {
            sqs_vw_sql.setWhereExpression(this.whereExpression.toVectorWiseSelect(sqs_vw_sql, this));
        }
        if (this.order_by_statement != null) {
            sqs_vw_sql.setOrderByStatement(this.order_by_statement.toVectorWiseSelect(sqs_vw_sql, this));
        }
        if (this.computeByVector != null) {
            final Vector computeByStatementVector = new Vector();
            for (int i = 0; i < this.computeByVector.size(); ++i) {
                computeByStatementVector.add(this.computeByVector.get(i).toVectorWiseSelect(sqs_vw_sql, this));
            }
            sqs_vw_sql.setComputeByStatements(computeByStatementVector);
            sqs_vw_sql.setCommentForCompute(true);
        }
        if (sqs_vw_sql.getGroupByStatement() == null && this.group_by_statement != null) {
            sqs_vw_sql.setGroupByStatement(this.group_by_statement.toVectorWiseSelect(sqs_vw_sql, this));
        }
        if (this.set_operator_clause != null) {
            sqs_vw_sql.setSetOperatorClause(this.set_operator_clause.toVectorWiseSelect(sqs_vw_sql, this));
            this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_vw_sql, "VARCHAR");
        }
        if (this.having_statement != null) {
            sqs_vw_sql.setHavingStatement(this.having_statement.toVectorWiseSelect(sqs_vw_sql, this));
            this.handleHavingClauseWithoutGroupByClauseQueries(sqs_vw_sql);
        }
        if (this.into_statement != null) {
            sqs_vw_sql.setIntoStatement(this.into_statement.toVectorWiseSelect(sqs_vw_sql, this));
        }
        if (this.hierarchical_query_clause != null) {
            throw new ConvertException();
        }
        if (this.limit_clause != null) {
            sqs_vw_sql.setLimitClause(this.limit_clause.toVectorWiseSelect(sqs_vw_sql, this));
        }
        sqs_vw_sql.setOptionalHintClause(null);
        return sqs_vw_sql;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        return this.toVectorWiseSelect().toString();
    }
    
    public void addAllIndexPositionsForStringLiterals(final Set indexSet) {
        this.indexPositionsOfStringLiteralsSet.addAll(indexSet);
    }
    
    public void addAllIndexPositionsForStringLiterals(final List indexSet) {
        this.indexPositionsOfStringLiteralsSet.addAll(indexSet);
    }
    
    public void addIndexPostionForStringLiteral(final int index) {
        this.indexPositionsOfStringLiteralsSet.add(index);
    }
    
    public Set getIndexPositionsForStringLiterals() {
        return this.indexPositionsOfStringLiteralsSet;
    }
    
    public boolean hasToConvertToTextDataTypeForStringLiterals(final int selectIndex) {
        return this.indexPositionsOfStringLiteralsSet.contains(selectIndex);
    }
    
    public void addAllIndexPositionsForNULLString(final Set indexSet) {
        this.indexPositionsOfNULLStringsSet.addAll(indexSet);
    }
    
    public void addAllIndexPositionsForNULLString(final List indexSet) {
        this.indexPositionsOfNULLStringsSet.addAll(indexSet);
    }
    
    public void addIndexPostionForNULLString(final int index) {
        this.indexPositionsOfNULLStringsSet.add(index);
    }
    
    public void removeIndexPositionForNULLString(final int index) {
        this.indexPositionsOfNULLStringsSet.remove(index);
    }
    
    public Set getIndexPositionsForNULLString() {
        return this.indexPositionsOfNULLStringsSet;
    }
    
    public boolean hasToConvertToTextDataTypeForNULLString(final int selectIndex) {
        return this.indexPositionsOfNULLStringsSet.contains(selectIndex);
    }
    
    public boolean isSelectWithoutSetClause() {
        boolean selectWithoutSetClauses = false;
        if (this.set_operator_clause == null && !this.isSetOperatorQuery) {
            selectWithoutSetClauses = true;
        }
        return selectWithoutSetClauses;
    }
    
    public boolean isFirstSelectStatementInSetQuery() {
        boolean firstSelectStatement = false;
        if (this.set_operator_clause != null && !this.isSetOperatorQuery) {
            firstSelectStatement = true;
        }
        return firstSelectStatement;
    }
    
    public void replaceSelectItemsForNULLString(final SelectQueryStatement sqs, final String dataType) {
        final Set indexSet = SwisSQLAPI.getNULLIndexPositionsForCasting();
        if (!indexSet.isEmpty() && sqs.isFirstSelectStatementInSetQuery() && sqs.select_statement != null && sqs.select_statement.getSelectItemList() != null) {
            final Vector selectItemList = sqs.select_statement.getSelectItemList();
            for (int i_count = 0; i_count < selectItemList.size(); ++i_count) {
                if (indexSet.contains(i_count) && selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = selectItemList.elementAt(i_count);
                    sc.convertSelectColumnToTextDataType(dataType);
                }
            }
        }
    }
    
    public void replaceSetClauseSelectItemsForStringLiterals(final SelectQueryStatement sqs, final String dataType) {
        final Set indexSet = SwisSQLAPI.getIndexPositionsForCasting();
        if (!indexSet.isEmpty() && sqs.select_statement != null && sqs.select_statement.getSelectItemList() != null) {
            final Vector selectItemList = sqs.select_statement.getSelectItemList();
            for (int i_count = 0; i_count < selectItemList.size(); ++i_count) {
                if (indexSet.contains(i_count) && selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = selectItemList.elementAt(i_count);
                    sc.convertSelectColumnToTextDataType(dataType);
                }
            }
        }
    }
    
    public void replaceSelectItemsForSetQueriesDataTypeMismatchedColumns() {
        this.replaceSelectItemsForStringLiteralsAndNULLString(this, "TEXT");
    }
    
    public void replaceSelectItemsForSetQueriesDataTypeMismatchedColumns(final String dataType) {
        this.replaceSelectItemsForStringLiteralsAndNULLString(this, dataType);
    }
    
    public void replaceSelectItemsForStringLiteralsAndNULLString(final SelectQueryStatement sqs, final String dataType) {
        final Set indexSet = this.getIndexPositionsForStringLiterals();
        final Set indexNULLSet = this.getIndexPositionsForNULLString();
        if ((!indexSet.isEmpty() || (!indexNULLSet.isEmpty() && sqs.isFirstSelectStatementInSetQuery())) && sqs.select_statement != null && sqs.select_statement.getSelectItemList() != null) {
            final Vector selectItemList = sqs.select_statement.getSelectItemList();
            for (int i_count = 0; i_count < selectItemList.size(); ++i_count) {
                final boolean isNullValue = indexNULLSet.contains(i_count);
                final String dataTypeNew = (isNullValue && dataType.equalsIgnoreCase("VARCHAR")) ? "VARCHAR(10)" : dataType;
                if ((indexSet.contains(i_count) || isNullValue) && selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = selectItemList.elementAt(i_count);
                    sc.convertSelectColumnToTextDataType(dataTypeNew);
                }
            }
        }
    }
    
    public boolean canRemoveOrderByClause() {
        boolean removeOrderBy = false;
        if (SwisSQLAPI.canClearLimitAndFetchClauses() || this.removalOptionForOrderAndFetchClauses == 1 || this.removalOptionForOrderAndFetchClauses == 3 || (this.removalOptionForOrderAndFetchClauses == 0 && this.select_statement != null && this.select_statement.getSelectQualifier() != null && this.select_statement.getSelectQualifier().equalsIgnoreCase("DISTINCT"))) {
            removeOrderBy = true;
        }
        return removeOrderBy;
    }
    
    public boolean canRemoveLimitAndFetchClause() {
        boolean removeLimitAndFetch = false;
        if (SwisSQLAPI.canClearLimitAndFetchClauses() || this.removalOptionForOrderAndFetchClauses == 2 || this.removalOptionForOrderAndFetchClauses == 3) {
            removeLimitAndFetch = true;
        }
        return removeLimitAndFetch;
    }
    
    public void removeOrderByStatementAndFetchClausesForPGAndVW() {
        if (this.order_by_statement != null && this.removalOptionForOrderAndFetchClauses >= 0) {
            switch (this.removalOptionForOrderAndFetchClauses) {
                case 0: {
                    if (this.select_statement != null && this.select_statement.getSelectQualifier() != null && this.select_statement.getSelectQualifier().equalsIgnoreCase("DISTINCT")) {
                        this.order_by_statement = null;
                        break;
                    }
                    break;
                }
                case 1: {
                    this.order_by_statement = null;
                    break;
                }
                case 2: {
                    this.limit_clause = null;
                    this.fetch_clause = null;
                    break;
                }
                case 3: {
                    this.order_by_statement = null;
                    this.limit_clause = null;
                    this.fetch_clause = null;
                    break;
                }
            }
        }
    }
    
    public void handleHavingClauseWithoutGroupByClauseQueries(final SelectQueryStatement sqs) {
        if (sqs != null && sqs.canHandleHavingWithoutGroupBy()) {
            final HavingStatement havingSt = sqs.getHavingStatement();
            final Vector havingItems = havingSt.getHavingItems();
            final boolean hasAggFunctions = false;
            if (havingItems != null && !havingItems.isEmpty()) {
                for (int i = 0; i < havingItems.size(); ++i) {
                    if (havingItems.elementAt(i) instanceof WhereExpression) {
                        final WhereExpression we = havingItems.elementAt(i);
                        final Vector wItems = we.getWhereItems();
                        if (wItems != null && !wItems.isEmpty()) {
                            for (int j = 0; j < wItems.size(); ++j) {
                                if (wItems.elementAt(j) instanceof WhereItem) {
                                    final WhereItem wi = wItems.get(j);
                                    final WhereColumn leftExp = wi.getLeftWhereExp();
                                    if (leftExp != null && leftExp.getColumnExpression() != null && leftExp.getColumnExpression().size() == 1) {
                                        if (leftExp.getColumnExpression().get(0) instanceof String) {
                                            final String columnName = leftExp.getColumnExpression().get(0).toString().toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
                                            if (sqs.aliasVsSelectColExpMap.containsKey(columnName)) {
                                                final TableColumn columnNameNew = new TableColumn();
                                                columnNameNew.setColumnName(sqs.aliasVsSelectColExpMap.get(columnName));
                                                leftExp.getColumnExpression().setElementAt(columnNameNew, 0);
                                            }
                                        }
                                        if (leftExp.getColumnExpression().get(0) instanceof TableColumn) {
                                            final String columnName = leftExp.getColumnExpression().get(0).toString().toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
                                            if (sqs.aliasVsSelectColExpMap.containsKey(columnName)) {
                                                final TableColumn columnNameNew = new TableColumn();
                                                columnNameNew.setColumnName(sqs.aliasVsSelectColExpMap.get(columnName));
                                                leftExp.getColumnExpression().setElementAt(columnNameNew, 0);
                                            }
                                        }
                                        else if (leftExp.getColumnExpression().get(0) instanceof FunctionCalls) {
                                            final FunctionCalls fc = leftExp.getColumnExpression().get(0);
                                            this.checkInsideCastFunctionForHavingClauseAliasConversion(fc, sqs.aliasVsSelectColExpMap);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.group_by_statement == null && !hasAggFunctions) {
                if (this.whereExpression == null) {
                    havingSt.setHavingClause("WHERE");
                }
                else {
                    final WhereExpression newExp = new WhereExpression();
                    final WhereExpression wExp = sqs.getWhereExpression();
                    wExp.setOpenBrace("(");
                    wExp.setCloseBrace(")");
                    final Vector vecItems = new Vector();
                    final Vector opItems = new Vector();
                    vecItems.add(wExp);
                    if (havingItems != null && !havingItems.isEmpty()) {
                        for (int k = 0; k < havingItems.size(); ++k) {
                            if (havingItems.elementAt(k) instanceof WhereExpression) {
                                final WhereExpression we2 = havingItems.elementAt(k);
                                we2.setOpenBrace("(");
                                we2.setCloseBrace(")");
                                vecItems.add(we2);
                                opItems.add("AND");
                            }
                        }
                        newExp.setWhereItem(vecItems);
                        newExp.setOperator(opItems);
                        newExp.setOpenBrace("(");
                        newExp.setCloseBrace(")");
                        sqs.setWhereExpression(newExp);
                        sqs.setHavingStatement(null);
                    }
                }
            }
        }
    }
    
    public boolean checkInsideCastFunctionForHavingClauseAliasConversion(final FunctionCalls fc, final Map<String, String> aliasVsSelectColExpMap) {
        boolean hasAggFunctions = false;
        try {
            final String functionName = fc.getFunctionNameAsAString();
            if (functionName != null && !functionName.isEmpty()) {
                if (functionName.equalsIgnoreCase("CAST")) {
                    return this.replaceAliasNameInsideFunction(fc, aliasVsSelectColExpMap, 2, true);
                }
                if (functionName.equalsIgnoreCase("SUM") || functionName.equalsIgnoreCase("AVG") || functionName.equalsIgnoreCase("COUNT") || functionName.equalsIgnoreCase("MIN") || functionName.equalsIgnoreCase("MAX") || functionName.equalsIgnoreCase("STD") || functionName.equalsIgnoreCase("VARIANCE")) {
                    hasAggFunctions = true;
                    this.replaceAliasNameInsideFunction(fc, aliasVsSelectColExpMap, 1, false);
                }
            }
        }
        catch (final StackOverflowError so) {
            hasAggFunctions = false;
        }
        catch (final Exception ex) {}
        hasAggFunctions = false;
        return hasAggFunctions;
    }
    
    public boolean replaceAliasNameInsideFunction(final FunctionCalls fc, final Map<String, String> aliasVsSelectColExpMap, final int argCount, final boolean allowFnCall) {
        try {
            if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() == argCount && fc.getFunctionArguments().get(0) instanceof SelectColumn) {
                final SelectColumn sc = fc.getFunctionArguments().get(0);
                if (sc.getColumnExpression() != null && sc.getColumnExpression().size() == 1) {
                    if (sc.getColumnExpression().get(0) instanceof TableColumn) {
                        final String columnName = sc.getColumnExpression().get(0).toString().toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
                        if (aliasVsSelectColExpMap.containsKey(columnName)) {
                            final TableColumn columnNameNew = new TableColumn();
                            columnNameNew.setColumnName(aliasVsSelectColExpMap.get(columnName));
                            sc.getColumnExpression().setElementAt(columnNameNew, 0);
                        }
                    }
                    else if (allowFnCall && sc.getColumnExpression().get(0) instanceof FunctionCalls) {
                        return this.checkInsideCastFunctionForHavingClauseAliasConversion(sc.getColumnExpression().get(0), aliasVsSelectColExpMap);
                    }
                }
            }
        }
        catch (final StackOverflowError stackOverflowError) {}
        catch (final Exception ex) {}
        return false;
    }
    
    static {
        SelectQueryStatement.beautyTabCount = 0;
        SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
        SelectQueryStatement.listOfWithStatements = new Vector();
    }
}
