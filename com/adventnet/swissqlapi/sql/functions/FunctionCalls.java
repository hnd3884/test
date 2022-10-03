package com.adventnet.swissqlapi.sql.functions;

import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.functions.misc.cast;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.OrderItem;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.select.GroupByStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.HavingStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.functions.misc.nvl;
import com.adventnet.swissqlapi.sql.functions.misc.decode;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.WindowingClause;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.OverrideToString;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;

public class FunctionCalls
{
    private static String trimmedFnName;
    protected TableColumn functionName;
    protected String argumentQualifier;
    protected String trailingString;
    protected String fromStringInFunction;
    protected String forStringInFunction;
    protected String lengthString;
    protected String asDatatype;
    protected String using;
    protected String inString;
    protected String separatorString;
    protected String over;
    protected OrderByStatement obs;
    protected Vector functionArguments;
    public static boolean charToIntName;
    protected String CaseString;
    protected boolean decodeConvertedToCaseStatement;
    protected UserObjectContext context;
    protected String toDateString;
    protected String toDateSymbolValue;
    protected String divisionBy31;
    private boolean openBracesForFunctionNameRequired;
    private String wrapper;
    private OverrideToString override_to_string;
    private String partitionBy;
    private QueryPartitionClause partitionByClause;
    private boolean inArithmeticExpr;
    private String targetDataType;
    private CommentClass commentObj;
    private String adventNetMessage;
    private WindowingClause windowClause;
    private String dateArithmetic;
    protected String keep;
    protected String denseRank;
    protected String last;
    protected String first;
    public static boolean functionArgsInSingleQuotesToDouble;
    protected SelectColumn atTimeZoneRegion;
    private String withinGroup;
    protected CaseStatement caseStatement;
    private String usingClause;
    private boolean stripComma;
    protected boolean castToTextInsideIf;
    private boolean outerJoin;
    private static final int[] WEEKDAY_MAP;
    private static final int[] WEEKSTARTDAY_WEEKDAY_MAP;
    
    public FunctionCalls() {
        this.decodeConvertedToCaseStatement = false;
        this.context = null;
        this.openBracesForFunctionNameRequired = true;
        this.wrapper = null;
        this.adventNetMessage = null;
        this.keep = null;
        this.denseRank = null;
        this.last = null;
        this.first = null;
        this.atTimeZoneRegion = null;
        this.usingClause = null;
        this.stripComma = false;
        this.castToTextInsideIf = true;
        this.outerJoin = false;
        this.functionName = new TableColumn();
        this.functionArguments = new Vector();
    }
    
    public boolean castToTextInsideIf() {
        return this.castToTextInsideIf;
    }
    
    public void setCastToTextInsideIf(final boolean yes) {
        this.castToTextInsideIf = yes;
    }
    
    public void setDateArithmetic(final String dateArithmetic) {
        this.dateArithmetic = dateArithmetic;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setOver(final String over) {
        this.over = over;
    }
    
    public void setWithinGroup(final String withinGroup) {
        this.withinGroup = withinGroup;
    }
    
    public void setOrderBy(final OrderByStatement obs) {
        this.obs = obs;
    }
    
    public void setPartitionBy(final String partitionBy) {
        this.partitionBy = partitionBy;
    }
    
    public void setFunctionName(final TableColumn tc_fn) {
        this.functionName = tc_fn;
    }
    
    public void setArgumentQualifier(final String s_aq) {
        this.argumentQualifier = s_aq;
    }
    
    public void setFunctionArguments(final Vector v_fa) {
        this.functionArguments = v_fa;
    }
    
    public void setTrailingString(final String trailingString) {
        this.trailingString = trailingString;
    }
    
    public void setFromInTrim(final String fromStringInFunction) {
        this.fromStringInFunction = fromStringInFunction;
    }
    
    public void setForLength(final String forStringInFunction) {
        this.forStringInFunction = forStringInFunction;
    }
    
    public void setLengthString(final String lengthString) {
        this.lengthString = lengthString;
    }
    
    public void setAsDatatype(final String asDatatype) {
        this.asDatatype = asDatatype;
    }
    
    public void setUsing(final String using) {
        this.using = using;
    }
    
    public void setInString(final String in) {
        this.inString = in;
    }
    
    public void setSeparatorString(final String separatorString) {
        this.separatorString = separatorString;
    }
    
    public void setDivisionBy31(final String divisionBy31) {
        this.divisionBy31 = divisionBy31;
    }
    
    public void setTargetDataType(final String targetDataType) {
        this.targetDataType = targetDataType;
    }
    
    public void setOpenBracesForFunctionNameRequired(final boolean openBracesForFunctionNameRequired) {
        this.openBracesForFunctionNameRequired = openBracesForFunctionNameRequired;
    }
    
    public void setToDateExpression(final String toDateString) {
        this.toDateString = toDateString;
    }
    
    public void setToDateSymbolValue(final String toDateSymbolValue) {
        this.toDateSymbolValue = toDateSymbolValue;
    }
    
    public void setPartitionByClause(final QueryPartitionClause pbc) {
        this.partitionByClause = pbc;
    }
    
    public void registerOverrideToString(final OverrideToString ots) {
        this.override_to_string = ots;
    }
    
    public void setWrapper(final String wrapper) {
        this.wrapper = wrapper;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setAdventNetMessageString(final String message) {
        this.adventNetMessage = message;
    }
    
    public void setWindowingClause(final WindowingClause windowClause) {
        this.windowClause = windowClause;
    }
    
    public void setLast(final String last) {
        this.last = last;
    }
    
    public void setFirst(final String first) {
        this.first = first;
    }
    
    public void setKeep(final String keep) {
        this.keep = keep;
    }
    
    public void setDenseRank(final String denseRank) {
        this.denseRank = denseRank;
    }
    
    public void setAtTimeZoneRegion(final SelectColumn atTZR) {
        this.atTimeZoneRegion = atTZR;
    }
    
    public void setUsingClause(final String option) {
        this.usingClause = option;
    }
    
    public void setStripComma(final boolean stripComma) {
        this.stripComma = stripComma;
    }
    
    public String getDateArithmetic() {
        return this.dateArithmetic;
    }
    
    public TableColumn getFunctionName() {
        return this.functionName;
    }
    
    public String getWrapper() {
        return this.wrapper;
    }
    
    public String getArgumentQualifier() {
        return this.argumentQualifier;
    }
    
    public Vector getFunctionArguments() {
        return this.functionArguments;
    }
    
    public String getFunctionNameAsAString() {
        if (this.functionName != null) {
            return this.functionName.getColumnName();
        }
        return null;
    }
    
    public String getTrailingString() {
        return this.trailingString;
    }
    
    public String getFromInTrim() {
        return this.fromStringInFunction;
    }
    
    public String getForLength() {
        return this.forStringInFunction;
    }
    
    public String getLengthString() {
        return this.lengthString;
    }
    
    public String getAsDatatype() {
        return this.asDatatype;
    }
    
    public String getUsing() {
        return this.using;
    }
    
    public String getInString() {
        return this.inString;
    }
    
    public String getSeparatorString() {
        return this.separatorString;
    }
    
    public String getDivisionBy31() {
        return this.divisionBy31;
    }
    
    public String getToDateExpression() {
        return this.toDateString;
    }
    
    public String getToDateSymbolValue() {
        return this.toDateSymbolValue;
    }
    
    public String getOver() {
        return this.over;
    }
    
    public String getWithinGroup() {
        return this.withinGroup;
    }
    
    public OrderByStatement getOrderBy() {
        return this.obs;
    }
    
    public String getPartitionBy() {
        return this.partitionBy;
    }
    
    public QueryPartitionClause getPartitionByClause() {
        return this.partitionByClause;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public String getAdventNetMessageString() {
        return this.adventNetMessage;
    }
    
    public WindowingClause getWindowingClause() {
        return this.windowClause;
    }
    
    public String getKeep() {
        return this.keep;
    }
    
    public String getDenseRank() {
        return this.denseRank;
    }
    
    public String getLast() {
        return this.last;
    }
    
    public String getFirst() {
        return this.first;
    }
    
    public SelectColumn getAtTimeZoneRegion() {
        return this.atTimeZoneRegion;
    }
    
    public String getUsingClause() {
        return this.usingClause;
    }
    
    public boolean getOpenBracesForFunctionNameRequired() {
        return this.openBracesForFunctionNameRequired;
    }
    
    public void setInArithmeticExpression(final boolean inArithmeticExpr) {
        this.inArithmeticExpr = inArithmeticExpr;
    }
    
    public boolean isStripComma() {
        return this.stripComma;
    }
    
    public FunctionCalls toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FunctionCalls functioncall = null;
        if (this.functionName != null) {
            functioncall = getNewInstance(this.functionName.getColumnName());
        }
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                    newFunctioncall.setArgumentQualifier("DISTINCT");
                }
                else {
                    newFunctioncall.setArgumentQualifier(this.argumentQualifier);
                }
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        newFunctionArguments.add(this.functionArguments.get(i).toTeradataSelect(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toTeradataString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            if (this.obs != null) {
                newFunctioncall.setOrderBy(this.obs.toTeradataSelect(null, null));
            }
            newFunctioncall.setOpenBracesForFunctionNameRequired(this.openBracesForFunctionNameRequired);
            newFunctioncall.setKeep(this.keep);
            newFunctioncall.setDenseRank(this.denseRank);
            newFunctioncall.setFirst(this.first);
            newFunctioncall.setLast(this.last);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setWithinGroup(this.withinGroup);
            if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
                from_sqs.setOlapFunctionPresent(true);
            }
            if (this.partitionByClause != null) {
                newFunctioncall.setPartitionByClause(this.partitionByClause.toTeradataSelect(to_sqs, from_sqs));
            }
            if (this.windowClause != null) {
                newFunctioncall.setWindowingClause(this.windowClause.toTeradata(to_sqs, from_sqs));
            }
            if (this.atTimeZoneRegion != null) {
                functioncall.setAtTimeZoneRegion(this.atTimeZoneRegion);
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            return newFunctioncall;
        }
        if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                functioncall.setArgumentQualifier("DISTINCT");
            }
            else {
                functioncall.setArgumentQualifier(this.argumentQualifier);
            }
        }
        functioncall.setTrailingString(this.trailingString);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toTeradataSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        if (this.obs != null) {
            functioncall.setOrderBy(this.obs.toTeradataSelect(to_sqs, from_sqs));
        }
        functioncall.setOpenBracesForFunctionNameRequired(this.openBracesForFunctionNameRequired);
        functioncall.setKeep(this.keep);
        functioncall.setDenseRank(this.denseRank);
        functioncall.setFirst(this.first);
        functioncall.setLast(this.last);
        functioncall.setOver(this.over);
        functioncall.setWithinGroup(this.withinGroup);
        if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
        }
        if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toTeradataSelect(to_sqs, from_sqs));
        }
        if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toTeradata(to_sqs, from_sqs));
        }
        if (this.atTimeZoneRegion != null) {
            functioncall.setAtTimeZoneRegion(this.atTimeZoneRegion);
        }
        functioncall.toTeradata(to_sqs, from_sqs);
        if (functioncall.getKeep() != null && functioncall.getDenseRank() != null) {
            this.handleKeepDenseRank(to_sqs, from_sqs, functioncall);
        }
        functioncall.setCommentClass(this.commentObj);
        return functioncall;
    }
    
    public FunctionCalls toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.CaseString != null && this.functionName == null) {
            return this;
        }
        FunctionCalls functioncall = null;
        if (this.functionName != null) {
            functioncall = getNewInstance(this.functionName.getColumnName());
        }
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                    newFunctioncall.setArgumentQualifier("DISTINCT");
                }
                else {
                    newFunctioncall.setArgumentQualifier(this.argumentQualifier);
                }
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        newFunctionArguments.add(this.functionArguments.get(i).toANSISelect(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toANSIString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            if (from_sqs != null) {
                if (from_sqs.getOrderByStatement() != null) {
                    if (this.obs != null) {
                        from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                        this.obs.setOrderItemList(null);
                        this.obs.setOrderClause(null);
                    }
                }
                else {
                    from_sqs.setOrderByStatement(this.obs);
                }
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            return newFunctioncall;
        }
        if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                functioncall.setArgumentQualifier("DISTINCT");
            }
            else {
                functioncall.setArgumentQualifier(this.argumentQualifier);
            }
        }
        functioncall.setTrailingString(this.trailingString);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toANSISelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        functioncall.setOrderBy(this.obs);
        functioncall.setOver(this.over);
        if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toANSISelect(to_sqs, from_sqs));
        }
        if (from_sqs != null) {
            if (from_sqs.getOrderByStatement() != null) {
                if (this.obs != null) {
                    from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                    this.obs.setOrderItemList(null);
                    this.obs.setOrderClause(null);
                }
            }
            else {
                from_sqs.setOrderByStatement(this.obs);
            }
        }
        functioncall.toANSISQL(to_sqs, from_sqs);
        functioncall.setCommentClass(this.commentObj);
        return functioncall;
    }
    
    public FunctionCalls toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FunctionCalls functioncall = null;
        if (this.functionName != null) {
            functioncall = getNewInstance(this.functionName.getColumnName(), false, false, true, false, false);
            final String funcName = this.functionName.toString();
            if (SwisSQLOptions.fromSybase && (funcName.equalsIgnoreCase("LEFT") || funcName.equalsIgnoreCase("LTRIM") || funcName.equalsIgnoreCase("REPLICATE") || funcName.equalsIgnoreCase("RIGHT") || funcName.equalsIgnoreCase("RTRIM") || funcName.equalsIgnoreCase("SPACE") || funcName.equalsIgnoreCase("SUBSTRING"))) {
                functioncall.setWrapper("NULLIF");
            }
        }
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                    newFunctioncall.setArgumentQualifier("DISTINCT");
                }
                else {
                    newFunctioncall.setArgumentQualifier(this.argumentQualifier);
                }
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        newFunctionArguments.add(this.functionArguments.get(i).toMSSQLServerSelect(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toMSSQLServerString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setPartitionByClause(this.partitionByClause);
            newFunctioncall.setOrderBy(this.obs);
            try {
                if (newFunctioncall.getFunctionName() != null) {
                    final TableColumn functionName = newFunctioncall.getFunctionName();
                    if (functionName.getTableName() == null && functionName.getColumnName() != null && !functionName.getColumnName().equals("") && !this.isSQLServerSystemFunction(functionName.getColumnName().toLowerCase()) && from_sqs != null && from_sqs.isMSAzure()) {
                        final String ownerName = SwisSQLAPI.objectsOwnerName.get(new Integer(2));
                        if (ownerName != null) {
                            functionName.setTableName(ownerName);
                        }
                        else {
                            functionName.setTableName("DBO");
                        }
                        functionName.setDot(".");
                    }
                }
            }
            catch (final Exception ex) {}
            return newFunctioncall;
        }
        if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                functioncall.setArgumentQualifier("DISTINCT");
            }
            else {
                functioncall.setArgumentQualifier(this.argumentQualifier);
            }
        }
        functioncall.setTrailingString(this.trailingString);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toMSSQLServerSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        functioncall.setOrderBy(this.obs);
        functioncall.setOver(this.over);
        if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toMSSQLServerSelect(to_sqs, from_sqs));
        }
        functioncall.toMSSQLServer(to_sqs, from_sqs);
        functioncall.setCommentClass(this.commentObj);
        return functioncall;
    }
    
    public FunctionCalls toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FunctionCalls functioncall = null;
        if (this.functionName != null) {
            functioncall = getNewInstance(this.functionName.getColumnName());
        }
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                    newFunctioncall.setArgumentQualifier("DISTINCT");
                }
                else {
                    newFunctioncall.setArgumentQualifier(this.argumentQualifier);
                }
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            newFunctioncall.setObjectContext(this.context);
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        this.functionArguments.get(i).setObjectContext(this.context);
                        newFunctionArguments.add(this.functionArguments.get(i).toSybaseSelect(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toSybaseString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            if (from_sqs != null) {
                if (from_sqs.getOrderByStatement() != null) {
                    if (this.obs != null) {
                        from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                        this.obs.setOrderItemList(null);
                        this.obs.setOrderClause(null);
                    }
                }
                else {
                    from_sqs.setOrderByStatement(this.obs);
                }
            }
            try {
                if (newFunctioncall.getFunctionName() != null) {
                    final TableColumn functionName = newFunctioncall.getFunctionName();
                    if (functionName.getTableName() == null && functionName.getColumnName() != null && !functionName.getColumnName().equals("") && !this.isSybaseSystemFunction(functionName.getColumnName().toLowerCase())) {
                        functionName.setTableName("DBO");
                        functionName.setDot(".");
                        if (this.context != null) {
                            functionName.setColumnName(this.context.getEquivalent(functionName.getColumnName()).toString());
                        }
                    }
                }
            }
            catch (final Exception ex) {}
            return newFunctioncall;
        }
        if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                functioncall.setArgumentQualifier("DISTINCT");
            }
            else {
                functioncall.setArgumentQualifier(this.argumentQualifier);
            }
        }
        functioncall.setTrailingString(this.trailingString);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setFromInTrim(this.fromStringInFunction);
        for (int l = 0; l < this.functionArguments.size(); ++l) {
            if (this.functionArguments.get(l) instanceof SelectColumn) {
                this.functionArguments.get(l).setObjectContext(this.context);
            }
        }
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toSybaseSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setObjectContext(this.context);
        functioncall.setDateArithmetic(this.dateArithmetic);
        if (from_sqs != null) {
            if (from_sqs.getOrderByStatement() != null) {
                if (this.obs != null) {
                    from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                    this.obs.setOrderItemList(null);
                    this.obs.setOrderClause(null);
                }
            }
            else {
                from_sqs.setOrderByStatement(this.obs);
            }
        }
        functioncall.toSybase(to_sqs, from_sqs);
        functioncall.setCommentClass(this.commentObj);
        return functioncall;
    }
    
    public FunctionCalls toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName());
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                    newFunctioncall.setArgumentQualifier("DISTINCT");
                }
                else {
                    newFunctioncall.setArgumentQualifier(this.argumentQualifier);
                }
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        final String tDataType = CastingUtil.getParameterDataType(this.functionName.getColumnName(), i);
                        this.functionArguments.get(i).setTargetDataType(tDataType);
                        newFunctionArguments.add(this.functionArguments.get(i).toDB2Select(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toDB2String();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            newFunctioncall.setFunctionName(this.functionName);
            if (this.functionName.getColumnName().equalsIgnoreCase("identity") && this.functionName.getTableName() == null) {
                this.functionName.setColumnName("ROW_NUMBER() OVER");
                newFunctioncall.setFunctionArguments(new Vector());
            }
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            if (from_sqs != null) {
                if (from_sqs.getOrderByStatement() != null) {
                    if (this.obs != null) {
                        newFunctioncall.setOrderBy(this.obs);
                        newFunctioncall.setOver(this.over);
                        newFunctioncall.setPartitionByClause(this.partitionByClause);
                    }
                }
                else {
                    newFunctioncall.setOrderBy(this.obs);
                    newFunctioncall.setOver(this.over);
                    newFunctioncall.setPartitionByClause(this.partitionByClause);
                }
            }
            return newFunctioncall;
        }
        if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                functioncall.setArgumentQualifier("DISTINCT");
            }
            else {
                functioncall.setArgumentQualifier(this.argumentQualifier);
            }
        }
        functioncall.setTrailingString(this.trailingString);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toDB2Select(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        if (this.partitionByClause != null) {
            functioncall.setOver(this.over);
            functioncall.setPartitionByClause(this.partitionByClause.toDB2Select(to_sqs, from_sqs));
        }
        if (from_sqs != null) {
            if (from_sqs.getOrderByStatement() != null) {
                if (this.obs != null) {
                    from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                    this.obs.setOrderItemList(null);
                    this.obs.setOrderClause(null);
                }
            }
            else {
                from_sqs.setOrderByStatement(this.obs);
            }
        }
        functioncall.setOpenBracesForFunctionNameRequired(this.openBracesForFunctionNameRequired);
        if (functioncall instanceof decode || functioncall instanceof nvl) {
            functioncall.setTargetDataType(this.targetDataType);
            functioncall.setInArithmeticExpression(this.inArithmeticExpr);
        }
        functioncall.toDB2(to_sqs, from_sqs);
        functioncall.setCommentClass(this.commentObj);
        return functioncall;
    }
    
    public FunctionCalls toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName(), false, false, false, false, true);
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        newFunctionArguments.add(this.functionArguments.get(i).toOracleSelect(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toOracleString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            if (this.functionName != null) {
                final String tempTableName = this.functionName.getTableName();
                String colName = this.functionName.getColumnName();
                if (this.functionName.getColumnName().startsWith("[") && this.functionName.getColumnName().endsWith("]")) {
                    this.functionName.setColumnName(colName.substring(1, colName.length() - 1));
                }
                if (tempTableName == null && colName != null && colName.equalsIgnoreCase("quotename")) {
                    this.functionName.setColumnName("");
                    newFunctioncall.setOpenBracesForFunctionNameRequired(false);
                    final Object obj = newFunctioncall.getFunctionArguments().get(0);
                    final Vector fnArgs = new Vector();
                    fnArgs.add(obj);
                    newFunctioncall.setFunctionArguments(fnArgs);
                }
                if (tempTableName != null && (tempTableName.equalsIgnoreCase("dbo") || tempTableName.equalsIgnoreCase("[dbo]"))) {
                    if (colName.length() > 30) {
                        colName = colName.substring(0, 30);
                    }
                    colName = CustomizeUtil.objectNamesToQuotedIdentifier(colName, SwisSQLUtils.getKeywords(1), null, 1);
                    this.functionName.setColumnName(colName);
                    this.functionName.setTableName(null);
                }
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setOrderBy(this.obs);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setPartitionByClause(this.partitionByClause);
            newFunctioncall.setObjectContext(this.context);
            newFunctioncall.setCommentClass(this.commentObj);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            return newFunctioncall;
        }
        functioncall.setArgumentQualifier(this.argumentQualifier);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setTrailingString(this.trailingString);
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toOracleSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        functioncall.setOrderBy(this.obs);
        functioncall.setOver(this.over);
        if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toOracleSelect(to_sqs, from_sqs));
        }
        functioncall.setObjectContext(this.context);
        functioncall.toOracle(to_sqs, from_sqs);
        functioncall.setCommentClass(this.commentObj);
        if (from_sqs.isOracleLive() && functioncall.getFunctionName().toString().equalsIgnoreCase("TO_CHAR") && this.getFunctionArguments().size() == 2) {
            final SelectColumn selectColumn = new SelectColumn();
            final Vector v = new Vector();
            final Vector timeStamp = new Vector();
            final SelectColumn selectColumn2 = new SelectColumn();
            final Vector v2 = new Vector();
            final FunctionCalls inner_call = new FunctionCalls();
            final TableColumn inner_tc = new TableColumn();
            final String date_format = functioncall.getFunctionArguments().get(1).toString();
            final List invalid_formats = Arrays.asList("%D", "%e", "%U", "%u", "%v", "%V", "%X", "%x");
            for (final Object invalid_format : invalid_formats) {
                if (date_format.contains((CharSequence)invalid_format)) {
                    throw new ConvertException("Cannot use function in Oracle DB");
                }
            }
            String changed_format = date_format.replace("%b", "FMMON");
            changed_format = changed_format.replace("%a", "FMDY");
            changed_format = changed_format.replace("%c", "FMMM");
            changed_format = changed_format.replace("%d", "FMDD");
            changed_format = changed_format.replace("%f", "FMFF");
            changed_format = changed_format.replace("%H", "FMHH24");
            changed_format = changed_format.replace("%h", "FMHH");
            changed_format = changed_format.replace("%I", "FMHH");
            changed_format = changed_format.replace("%i", "FMMI");
            changed_format = changed_format.replace("%j", "FMDDD");
            changed_format = changed_format.replace("%k", "FMHH24");
            changed_format = changed_format.replace("%l", "FMHH");
            changed_format = changed_format.replace("%M", "FMMONTH");
            changed_format = changed_format.replace("%m", "FMMM");
            changed_format = changed_format.replace("%p", "FMAM");
            changed_format = changed_format.replace("%r", "FMHH12:MI:SS AM");
            changed_format = changed_format.replace("%s", "FMSS");
            changed_format = changed_format.replace("%S", "FMSS");
            changed_format = changed_format.replace("%T", "FMHH24:MI:SS");
            changed_format = changed_format.replace("%w", "FMD");
            changed_format = changed_format.replace("%W", "FMDAY");
            changed_format = changed_format.replace("%Y", "FMYYYY");
            changed_format = changed_format.replace("%y", "FMYY");
            v.add(0, changed_format);
            selectColumn.setColumnExpression(v);
            inner_tc.setColumnName("TO_TIMESTAMP");
            inner_call.setFunctionName(inner_tc);
            timeStamp.add(0, functioncall.getFunctionArguments().get(0));
            inner_call.setFunctionArguments(timeStamp);
            v2.add(inner_call);
            selectColumn2.setColumnExpression(v2);
            functioncall.getFunctionArguments().set(0, selectColumn2);
            functioncall.getFunctionArguments().set(1, selectColumn);
        }
        return functioncall;
    }
    
    public FunctionCalls toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName(), false, true, false, false, false);
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            newFunctioncall.setCastToTextInsideIf(this.castToTextInsideIf);
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                    newFunctioncall.setArgumentQualifier("DISTINCT");
                }
                else {
                    newFunctioncall.setArgumentQualifier(this.argumentQualifier);
                }
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                this.handlePositionFunctionArguments(this.functionName.getColumnName(), this.functionArguments, "TEXT");
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        newFunctionArguments.add(this.functionArguments.get(i).toPostgreSQLSelect(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toPostgreSQLString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setWithinGroup(this.withinGroup);
            if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
                from_sqs.setOlapFunctionPresent(true);
            }
            if (this.partitionByClause != null) {
                newFunctioncall.setPartitionByClause(this.partitionByClause.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            if (this.windowClause != null) {
                newFunctioncall.setWindowingClause(this.windowClause.toPostgreSQL(to_sqs, from_sqs));
            }
            if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
                newFunctioncall.setOrderBy(this.obs);
            }
            else if (from_sqs != null) {
                if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
                    functioncall.obs = this.obs;
                }
                else if (from_sqs.getOrderByStatement() != null) {
                    if (this.obs != null) {
                        from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                        this.obs.setOrderItemList(null);
                        this.obs.setOrderClause(null);
                    }
                }
                else {
                    from_sqs.setOrderByStatement(this.obs);
                }
            }
            return newFunctioncall;
        }
        functioncall.setCastToTextInsideIf(this.castToTextInsideIf);
        if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                functioncall.setArgumentQualifier("DISTINCT");
            }
            else {
                functioncall.setArgumentQualifier(this.argumentQualifier);
            }
        }
        functioncall.setTrailingString(this.trailingString);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toPostgreSQLSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        functioncall.setSeparatorString(this.separatorString);
        functioncall.setOver(this.over);
        functioncall.setWithinGroup(this.withinGroup);
        if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
        }
        if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toPostgreSQL(to_sqs, from_sqs));
        }
        if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs);
        }
        else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
                functioncall.obs = this.obs;
            }
            else if (from_sqs.getOrderByStatement() != null) {
                if (this.obs != null) {
                    from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                    this.obs.setOrderItemList(null);
                    this.obs.setOrderClause(null);
                }
            }
            else {
                from_sqs.setOrderByStatement(this.obs);
            }
        }
        functioncall.toPostgreSQL(to_sqs, from_sqs);
        functioncall.setCommentClass(this.commentObj);
        return functioncall;
    }
    
    public FunctionCalls toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.validateFunctionArgumentCount(from_sqs);
        if ((this.CaseString != null && this.functionName == null) || this.decodeConvertedToCaseStatement) {
            return this;
        }
        final FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName(), true);
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                    newFunctioncall.setArgumentQualifier("DISTINCT");
                }
                else {
                    newFunctioncall.setArgumentQualifier(this.argumentQualifier);
                }
            }
            newFunctioncall.setInString(this.getInString());
            newFunctioncall.setSeparatorString(this.getSeparatorString());
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.getSeparatorString() != null) {
                newFunctioncall.setOrderBy(this.obs);
            }
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        String str = null;
                        final SelectColumn sc1 = this.functionArguments.get(i);
                        final Vector colExp = sc1.getColumnExpression();
                        if (colExp.size() == 1 && colExp.get(0) instanceof TableColumn) {
                            str = colExp.get(0).getColumnName().trim().toLowerCase();
                        }
                        if (this.functionName.getColumnName().trim().equalsIgnoreCase("GET_FORMAT") && str != null && (str.equalsIgnoreCase("date") || str.equalsIgnoreCase("time"))) {
                            newFunctionArguments.add(this.functionArguments.get(i));
                        }
                        else {
                            newFunctionArguments.add(this.functionArguments.get(i).toMySQLSelect(to_sqs, from_sqs));
                        }
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toMySQLString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setWithinGroup(this.withinGroup);
            if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
                from_sqs.setOlapFunctionPresent(true);
            }
            if (this.partitionByClause != null) {
                newFunctioncall.setPartitionByClause(this.partitionByClause.toMySQLSelect(to_sqs, from_sqs));
            }
            if (this.windowClause != null) {
                newFunctioncall.setWindowingClause(this.windowClause.toMySQL(to_sqs, from_sqs));
            }
            if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
                newFunctioncall.setOrderBy(this.obs);
            }
            else if (from_sqs != null && this.separatorString == null) {
                if (from_sqs.getOrderByStatement() != null) {
                    if (this.obs != null) {
                        from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                        this.obs.setOrderItemList(null);
                        this.obs.setOrderClause(null);
                    }
                }
                else {
                    from_sqs.setOrderByStatement(this.obs);
                }
            }
            return newFunctioncall;
        }
        if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                functioncall.setArgumentQualifier("DISTINCT");
            }
            else {
                functioncall.setArgumentQualifier(this.argumentQualifier);
            }
        }
        functioncall.setTrailingString(this.trailingString);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toMySQLSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        functioncall.setUsing(this.getUsing());
        functioncall.setSeparatorString(this.getSeparatorString());
        functioncall.setInString(this.getInString());
        functioncall.setOver(this.over);
        functioncall.setWithinGroup(this.withinGroup);
        if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
        }
        if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toMySQLSelect(to_sqs, from_sqs));
        }
        if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toMySQL(to_sqs, from_sqs));
        }
        if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs);
        }
        else if (from_sqs != null) {
            if (from_sqs.getOrderByStatement() != null) {
                if (this.obs != null) {
                    from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                    this.obs.setOrderItemList(null);
                    this.obs.setOrderClause(null);
                }
            }
            else {
                from_sqs.setOrderByStatement(this.obs);
            }
        }
        functioncall.toMySQL(to_sqs, from_sqs);
        functioncall.setCommentClass(this.commentObj);
        return functioncall;
    }
    
    public void validateFunctionArgumentCount(final SelectQueryStatement from_sqs) throws ConvertException {
        if (from_sqs != null && from_sqs.getCanHandleFunctionArugmentsCountMismatch()) {
            if (from_sqs.getValidationHandler() == null || from_sqs.getValidationHandler().getWhiteListedFunctions() == null) {
                return;
            }
            final Object[] fnDetails = from_sqs.getValidationHandler().getWhiteListedFunctions().get(this.functionName.getColumnName().toUpperCase());
            if (fnDetails != null) {
                final int min = (int)fnDetails[1];
                final int max = (int)fnDetails[2];
                if ((min != -1 && min > this.functionArguments.size()) || (max != -1 && max < this.functionArguments.size())) {
                    throw new ConvertException("Function Arguments Count Mismatch for " + this.functionName.getColumnName(), "ARGUMENT_COUNT_MISMATCH", new Object[] { this.functionName.getColumnName().toUpperCase() });
                }
            }
            else if (from_sqs.getValidationHandler().getBlackListedFunctions() == null || from_sqs.getValidationHandler().getBlackListedFunctions().contains(this.functionName.getColumnName().toUpperCase())) {
                throw new ConvertException("UNSUPPORTED_MYSQL_FN " + this.functionName.getColumnName(), "UNSUPPORTED_MYSQL_FN", new Object[] { this.functionName.getColumnName().toUpperCase() });
            }
        }
    }
    
    public FunctionCalls toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if ((this.CaseString != null && this.functionName == null) || this.decodeConvertedToCaseStatement) {
            return this;
        }
        final FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName(), true, false, false, false, false);
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                    newFunctioncall.setArgumentQualifier("DISTINCT");
                }
                else {
                    newFunctioncall.setArgumentQualifier(this.argumentQualifier);
                }
            }
            newFunctioncall.setInString(this.getInString());
            newFunctioncall.setSeparatorString(this.getSeparatorString());
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.getSeparatorString() != null) {
                newFunctioncall.setOrderBy(this.obs);
            }
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                this.handlePositionFunctionArguments(this.functionName.getColumnName(), this.functionArguments, "CHAR");
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        String str = null;
                        final SelectColumn sc1 = this.functionArguments.get(i);
                        final Vector colExp = sc1.getColumnExpression();
                        if (colExp.size() == 1 && colExp.get(0) instanceof TableColumn) {
                            str = colExp.get(0).getColumnName().trim().toLowerCase();
                        }
                        if (this.functionName.getColumnName().trim().equalsIgnoreCase("GET_FORMAT") && str != null && (str.equalsIgnoreCase("date") || str.equalsIgnoreCase("time"))) {
                            newFunctionArguments.add(this.functionArguments.get(i));
                        }
                        else {
                            newFunctionArguments.add(this.functionArguments.get(i).toVectorWiseSelect(to_sqs, from_sqs));
                        }
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toMySQLString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setWithinGroup(this.withinGroup);
            if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
                from_sqs.setOlapFunctionPresent(true);
            }
            if (this.partitionByClause != null) {
                newFunctioncall.setPartitionByClause(this.partitionByClause.toVectorWiseSelect(to_sqs, from_sqs));
            }
            if (this.windowClause != null) {
                newFunctioncall.setWindowingClause(this.windowClause.toVectorWise(to_sqs, from_sqs));
            }
            if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
                newFunctioncall.setOrderBy(this.obs);
            }
            else if (from_sqs != null && this.separatorString == null) {
                if (from_sqs.getOrderByStatement() != null) {
                    if (this.obs != null) {
                        from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                        this.obs.setOrderItemList(null);
                        this.obs.setOrderClause(null);
                    }
                }
                else {
                    from_sqs.setOrderByStatement(this.obs);
                }
            }
            return newFunctioncall;
        }
        if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                functioncall.setArgumentQualifier("DISTINCT");
            }
            else {
                functioncall.setArgumentQualifier(this.argumentQualifier);
            }
        }
        functioncall.setTrailingString(this.trailingString);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toVectorWiseSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        functioncall.setUsing(this.getUsing());
        functioncall.setSeparatorString(this.getSeparatorString());
        functioncall.setInString(this.getInString());
        functioncall.setOver(this.over);
        functioncall.setWithinGroup(this.withinGroup);
        if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
        }
        if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toVectorWiseSelect(to_sqs, from_sqs));
        }
        if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toVectorWise(to_sqs, from_sqs));
        }
        if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs);
        }
        else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
                functioncall.obs = this.obs;
            }
            else if (from_sqs.getOrderByStatement() != null) {
                if (this.obs != null) {
                    from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                    this.obs.setOrderItemList(null);
                    this.obs.setOrderClause(null);
                }
            }
            else {
                from_sqs.setOrderByStatement(this.obs);
            }
        }
        functioncall.toVectorWise(to_sqs, from_sqs);
        return functioncall;
    }
    
    public FunctionCalls toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName());
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (from_sqs != null) {
                if (from_sqs.getOrderByStatement() != null) {
                    if (this.obs != null) {
                        from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                        this.obs.setOrderItemList(null);
                        this.obs.setOrderClause(null);
                    }
                }
                else {
                    from_sqs.setOrderByStatement(this.obs);
                }
            }
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        newFunctionArguments.add(this.functionArguments.get(i).toInformixSelect(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toInformixString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            return newFunctioncall;
        }
        functioncall.setArgumentQualifier(this.argumentQualifier);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setTrailingString(this.trailingString);
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toInformixSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        if (from_sqs != null) {
            if (from_sqs.getOrderByStatement() != null) {
                if (this.obs != null) {
                    from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                    this.obs.setOrderItemList(null);
                    this.obs.setOrderClause(null);
                }
            }
            else {
                from_sqs.setOrderByStatement(this.obs);
            }
        }
        functioncall.toInformix(to_sqs, from_sqs);
        functioncall.setCommentClass(this.commentObj);
        return functioncall;
    }
    
    public FunctionCalls toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.getFunctionName() != null) {
            final String func_name = this.getFunctionName().toString();
            if (func_name != null && func_name.equalsIgnoreCase("SOUNDEX")) {
                throw new ConvertException("\nThe Function \"" + func_name + "\" is not supported in TimesTen 5.1.21\n");
            }
        }
        final FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName());
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        newFunctionArguments.add(this.functionArguments.get(i).toTimesTenSelect(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toTimesTenString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            if (this.functionName != null) {
                final String tempTableName = this.functionName.getTableName();
                final String colName = this.functionName.getColumnName();
                if (this.functionName.getColumnName().startsWith("[") && this.functionName.getColumnName().endsWith("]")) {
                    this.functionName.setColumnName(colName.substring(1, colName.length() - 1));
                }
                if (tempTableName == null) {
                    final String fnName = this.functionName.getColumnName();
                    if (SwisSQLOptions.fromSybase) {
                        throw new ConvertException("\nThe Built-in function " + fnName.toUpperCase() + " is not supported in TimesTen 5.1.21\n");
                    }
                }
                if (tempTableName != null && (tempTableName.equalsIgnoreCase("dbo") || tempTableName.equalsIgnoreCase("[dbo]"))) {
                    this.functionName.setTableName(null);
                }
            }
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setOrderBy(this.obs);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setPartitionByClause(this.partitionByClause);
            newFunctioncall.setObjectContext(this.context);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            return newFunctioncall;
        }
        functioncall.setArgumentQualifier(this.argumentQualifier);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setTrailingString(this.trailingString);
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toTimesTenSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        functioncall.setOrderBy(this.obs);
        functioncall.setOver(this.over);
        functioncall.setObjectContext(this.context);
        functioncall.toTimesTen(to_sqs, from_sqs);
        return functioncall;
    }
    
    public FunctionCalls toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName());
        if (functioncall == null) {
            final FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                    newFunctioncall.setArgumentQualifier("DISTINCT");
                }
                else {
                    newFunctioncall.setArgumentQualifier(this.argumentQualifier);
                }
            }
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.functionArguments != null) {
                final Vector newFunctionArguments = new Vector();
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (this.functionArguments.get(i) instanceof SelectColumn) {
                        newFunctionArguments.add(this.functionArguments.get(i).toNetezzaSelect(to_sqs, from_sqs));
                    }
                    if (this.functionArguments.get(i) instanceof Datatype) {
                        final Datatype newDatatype = this.functionArguments.get(i);
                        newDatatype.toNetezzaString();
                        newFunctionArguments.add(newDatatype);
                    }
                    if (this.functionArguments.get(i) instanceof String) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Integer) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                    if (this.functionArguments.get(i) instanceof Double) {
                        newFunctionArguments.add(this.functionArguments.get(i));
                    }
                }
                newFunctioncall.setFunctionArguments(newFunctionArguments);
            }
            newFunctioncall.setFunctionName(SwisSQLUtils.getMappedFunctionName(SwisSQLAPI.targetDBMappedFunctionNames, this.functionName));
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            if (this.obs != null) {
                newFunctioncall.setOrderBy(this.obs.toNetezzaSelect(null, null));
            }
            newFunctioncall.setKeep(this.keep);
            newFunctioncall.setDenseRank(this.denseRank);
            newFunctioncall.setFirst(this.first);
            newFunctioncall.setLast(this.last);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setPartitionByClause(this.partitionByClause);
            newFunctioncall.setWindowingClause(this.windowClause);
            newFunctioncall.setObjectContext(this.context);
            newFunctioncall.setCommentClass(this.commentObj);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            return newFunctioncall;
        }
        if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                functioncall.setArgumentQualifier("DISTINCT");
            }
            else {
                functioncall.setArgumentQualifier(this.argumentQualifier);
            }
        }
        functioncall.setTrailingString(this.trailingString);
        functioncall.setAsDatatype(this.getAsDatatype());
        functioncall.setFromInTrim(this.fromStringInFunction);
        functioncall.setFunctionArguments(this.functionArguments);
        this.functionName.setIsFunctionName(true);
        functioncall.setFunctionName(this.functionName.toNetezzaSelect(to_sqs, from_sqs));
        functioncall.setForLength(this.forStringInFunction);
        functioncall.setLengthString(this.lengthString);
        functioncall.setDateArithmetic(this.dateArithmetic);
        functioncall.setKeep(this.keep);
        functioncall.setDenseRank(this.denseRank);
        functioncall.setFirst(this.first);
        functioncall.setLast(this.last);
        if (this.obs != null) {
            functioncall.setOrderBy(this.obs.toNetezzaSelect(to_sqs, from_sqs));
        }
        functioncall.setOver(this.over);
        if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toNetezzaSelect(to_sqs, from_sqs));
        }
        if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toNetezza(to_sqs, from_sqs));
        }
        functioncall.setObjectContext(this.context);
        functioncall.setCommentClass(this.commentObj);
        functioncall.toNetezza(to_sqs, from_sqs);
        functioncall.setFunctionName(SwisSQLUtils.getMappedFunctionName(SwisSQLAPI.targetDBMappedFunctionNames, this.functionName));
        return functioncall;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.toDateString != null) {
            sb.append("(");
        }
        if (this.override_to_string != null) {
            sb.append(this.override_to_string.toString(this));
        }
        else {
            if (this.functionName != null) {
                sb.append(this.functionName.toString());
                if (this.openBracesForFunctionNameRequired) {
                    sb.append("(");
                }
            }
            if (this.argumentQualifier != null) {
                if (this.argumentQualifier.indexOf("'") == -1) {
                    sb.append(this.argumentQualifier.toUpperCase() + " ");
                }
                else {
                    sb.append(this.argumentQualifier + " ");
                }
            }
            if (this.trailingString != null) {
                sb.append(this.trailingString + " ");
            }
            if (this.fromStringInFunction != null) {
                sb.append(this.fromStringInFunction.toUpperCase() + " ");
            }
            boolean asDatatypeAdded = false;
            if (this.lengthString != null) {
                for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                    if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                        this.functionArguments.elementAt(i_count).setObjectContext(this.context);
                    }
                    else if (this.functionArguments.elementAt(i_count) instanceof TableColumn) {
                        this.functionArguments.elementAt(i_count).setObjectContext(this.context);
                    }
                    else if (this.functionArguments.elementAt(i_count) instanceof FunctionCalls) {
                        this.functionArguments.elementAt(i_count).setObjectContext(this.context);
                    }
                    if (this.context != null && this.functionArguments.elementAt(i_count) instanceof String) {
                        final String temp = this.context.getEquivalent(this.functionArguments.elementAt(i_count)).toString();
                        sb.append(temp + "");
                    }
                    else {
                        sb.append(this.functionArguments.elementAt(i_count).toString() + "");
                    }
                }
                sb.append(" " + this.forStringInFunction + " ");
                sb.append(this.lengthString);
            }
            else {
                for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                    if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                        this.functionArguments.elementAt(i_count).setObjectContext(this.context);
                    }
                    else if (this.functionArguments.elementAt(i_count) instanceof TableColumn) {
                        this.functionArguments.elementAt(i_count).setObjectContext(this.context);
                    }
                    else if (this.functionArguments.elementAt(i_count) instanceof FunctionCalls) {
                        this.functionArguments.elementAt(i_count).setObjectContext(this.context);
                    }
                    if (i_count == this.functionArguments.size() - 1) {
                        if (!asDatatypeAdded && this.asDatatype != null) {
                            sb.append(this.asDatatype + " ");
                            asDatatypeAdded = true;
                        }
                        if (this.context != null && this.functionArguments.elementAt(i_count) instanceof String) {
                            final String temp = this.context.getEquivalent(this.functionArguments.elementAt(i_count)).toString().trim();
                            sb.append(temp);
                        }
                        else {
                            sb.append(this.functionArguments.elementAt(i_count).toString().trim());
                        }
                    }
                    else if (this.asDatatype != null) {
                        if (!asDatatypeAdded && this.functionArguments.elementAt(i_count) instanceof Datatype) {
                            sb.append(" " + this.asDatatype + " " + this.functionArguments.elementAt(i_count).toString() + " ");
                            asDatatypeAdded = true;
                        }
                        else {
                            sb.append(this.functionArguments.elementAt(i_count).toString() + " ");
                        }
                    }
                    else if (this.using != null) {
                        sb.append(this.functionArguments.elementAt(i_count).toString() + " " + this.using + " ");
                    }
                    else if (this.inString != null) {
                        sb.append(this.functionArguments.elementAt(i_count).toString() + " " + this.inString + " ");
                    }
                    else if (this.separatorString != null) {
                        sb.append(this.functionArguments.elementAt(i_count).toString() + " ");
                        if (this.obs != null) {
                            sb.append(this.obs.toString() + " ");
                        }
                        sb.append(this.separatorString + " ");
                    }
                    else if (this.context != null && this.functionArguments.elementAt(i_count) instanceof String) {
                        final String temp = this.context.getEquivalent(this.functionArguments.elementAt(i_count)).toString().trim();
                        if (!this.stripComma) {
                            sb.append(temp + ",");
                        }
                        sb.append(" ");
                    }
                    else {
                        sb.append(this.functionArguments.elementAt(i_count).toString().trim());
                        if (!this.stripComma) {
                            sb.append(",");
                        }
                        sb.append(" ");
                    }
                }
            }
            if (this.openBracesForFunctionNameRequired) {
                sb.append(")");
            }
            if (this.getAdventNetMessageString() != null) {
                sb.append("/*" + this.getAdventNetMessageString() + "*/");
            }
            if (this.toDateString != null) {
                sb.append(" + ( " + this.toDateString + " * ");
            }
            if (this.toDateSymbolValue != null) {
                sb.append(this.toDateSymbolValue + " ) ");
            }
            if (this.dateArithmetic != null) {
                sb.append(this.dateArithmetic);
            }
            if (this.keep != null) {
                sb.append(" " + this.keep + " ");
                if (this.denseRank != null) {
                    sb.append("( " + this.denseRank);
                }
                if (this.last != null) {
                    sb.append(" " + this.last + " ");
                }
                if (this.first != null) {
                    sb.append(" " + this.first + " ");
                }
                if (this.obs != null) {
                    sb.append(this.obs.toString() + " )");
                }
            }
            if (this.over != null) {
                sb.append(" " + this.over);
            }
            if (this.obs != null && this.keep == null && this.separatorString == null) {
                if (this.partitionByClause != null) {
                    if (this.windowClause != null) {
                        sb.append("(" + this.partitionByClause.toString() + " " + this.obs.toString() + this.windowClause.toString() + ")");
                    }
                    else {
                        sb.append("(" + this.partitionByClause.toString() + " " + this.obs.toString() + ")");
                    }
                }
                else if (this.windowClause != null) {
                    sb.append("(" + this.obs.toString() + this.windowClause.toString() + ")");
                }
                else {
                    sb.append("(" + this.obs.toString() + ")");
                }
            }
            else if (this.partitionByClause != null) {
                sb.append(" (" + this.partitionByClause.toString() + ")");
            }
            else if (this.over != null) {
                if (this.windowClause != null) {
                    sb.append("(" + this.windowClause.toString() + ")");
                }
                else {
                    sb.append("( )");
                }
            }
            if (this.divisionBy31 != null) {
                sb.append(" " + this.divisionBy31);
            }
        }
        if (this.toDateString != null) {
            sb.append(")");
        }
        if (this.atTimeZoneRegion != null) {
            sb.append(" AT TIME ZONE " + this.atTimeZoneRegion);
        }
        if (this.wrapper != null) {
            return this.wrapper + "(" + sb.toString() + " , '')";
        }
        return sb.toString();
    }
    
    public static FunctionCalls getNewInstance(final String functionName) {
        return getNewInstance(functionName, false, false, false, false, false);
    }
    
    public static FunctionCalls getNewInstance(final String functionName, final boolean isMySql) {
        return getNewInstance(functionName, false, false, false, isMySql, false);
    }
    
    public static FunctionCalls getNewInstance(final String functionName, final boolean isVectorWise, final boolean isPostgreSql, final boolean isMsAzure, final boolean isMySql, final boolean isOracle) {
        try {
            final String className = getClassName(functionName, isVectorWise, isPostgreSql, isMsAzure, isMySql, isOracle);
            final Class newClass = Class.forName("com.adventnet.swissqlapi.sql.functions." + className);
            return newClass.newInstance();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private static String getClassName(final String fname) {
        return getClassName(fname, false, false, false, false, false);
    }
    
    private static String getClassName(final String fname, final boolean isVectorWise, final boolean isPostgreSql, final boolean isMsAzure, final boolean isMySql, final boolean isOracle) {
        final String trimFnName = fname.trim();
        if (isOracle) {
            if (trimFnName.equalsIgnoreCase("quarter")) {
                return new String("date.quarter");
            }
            if (trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("addtime") || trimFnName.equalsIgnoreCase("subdate") || trimFnName.equalsIgnoreCase("subtime") || trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub")) {
                return new String("date.addToDate");
            }
            if (trimFnName.equalsIgnoreCase("DAYOFYEAR") || trimFnName.equalsIgnoreCase("minute") || trimFnName.equalsIgnoreCase("from_unixtime") || trimFnName.equalsIgnoreCase("dayname") || trimFnName.equalsIgnoreCase("week")) {
                return new String("date.datepart");
            }
            if (trimFnName.equalsIgnoreCase("makedate")) {
                return new String("date.MakeDate");
            }
            if (trimFnName.equalsIgnoreCase("insert")) {
                return new String("string.insert");
            }
            if (trimFnName.equalsIgnoreCase("binary")) {
                return new String("misc.Binary");
            }
        }
        if (trimFnName.equalsIgnoreCase("substring") && SwisSQLOptions.SOURCE_DB_IS_ORACLE) {
            return null;
        }
        if (trimFnName.equalsIgnoreCase("substr") || trimFnName.equalsIgnoreCase("substring") || trimFnName.equalsIgnoreCase("substrb")) {
            return new String("string.substring");
        }
        if (isVectorWise && trimFnName.equalsIgnoreCase("mid")) {
            return new String("string.substring");
        }
        if (trimFnName.equalsIgnoreCase("space")) {
            return new String("string.space");
        }
        if (trimFnName.equalsIgnoreCase("datename")) {
            return new String("string.datename");
        }
        if (trimFnName.equalsIgnoreCase("len") || trimFnName.equalsIgnoreCase("length") || trimFnName.equalsIgnoreCase("char_length") || trimFnName.equalsIgnoreCase("character_length") || trimFnName.equalsIgnoreCase("datalength") || trimFnName.equalsIgnoreCase("lengthb") || trimFnName.equalsIgnoreCase("octet_length")) {
            return new String("string.length");
        }
        if (trimFnName.equalsIgnoreCase("instr") || trimFnName.equalsIgnoreCase("strpos") || trimFnName.equalsIgnoreCase("posstr") || trimFnName.equalsIgnoreCase("patindex") || trimFnName.equalsIgnoreCase("locate") || trimFnName.equalsIgnoreCase("instrb")) {
            return new String("string.instr");
        }
        if (trimFnName.equalsIgnoreCase("charindex") && !SwisSQLOptions.SOURCE_DB_IS_ORACLE) {
            return new String("string.instr");
        }
        if (trimFnName.equalsIgnoreCase("lower") || trimFnName.equalsIgnoreCase("lcase") || trimFnName.equalsIgnoreCase("lowercase")) {
            return new String("string.lower");
        }
        if (trimFnName.equalsIgnoreCase("upper") || trimFnName.equalsIgnoreCase("ucase") || trimFnName.equalsIgnoreCase("uppercase")) {
            return new String("string.upper");
        }
        if (trimFnName.equalsIgnoreCase("stuff")) {
            return new String("string.stuff");
        }
        if (trimFnName.equalsIgnoreCase("write")) {
            return new String("string.Write");
        }
        if (trimFnName.equalsIgnoreCase("replace") || trimFnName.equalsIgnoreCase("str_replace")) {
            return new String("string.replace");
        }
        if (trimFnName.equalsIgnoreCase("reverse")) {
            return new String("string.reverse");
        }
        if (trimFnName.equalsIgnoreCase("lpad")) {
            return new String("string.lpad");
        }
        if (trimFnName.equalsIgnoreCase("trim")) {
            return new String("string.trim");
        }
        if (trimFnName.equalsIgnoreCase("ltrim")) {
            return new String("string.ltrim");
        }
        if (trimFnName.equalsIgnoreCase("rtrim")) {
            return new String("string.rtrim");
        }
        if (trimFnName.equalsIgnoreCase("left")) {
            return new String("string.left");
        }
        if (trimFnName.equalsIgnoreCase("right")) {
            return new String("string.right");
        }
        if (trimFnName.equalsIgnoreCase("repeat") || trimFnName.equalsIgnoreCase("replicate")) {
            return new String("string.repeat");
        }
        if (trimFnName.equalsIgnoreCase("rpad")) {
            return new String("string.rpad");
        }
        if (trimFnName.equalsIgnoreCase("str")) {
            return new String("string.str");
        }
        if (trimFnName.equalsIgnoreCase("concat") || trimFnName.equalsIgnoreCase("concatenate") || trimFnName.equalsIgnoreCase("concat_ws")) {
            return new String("string.concat");
        }
        if (trimFnName.equalsIgnoreCase("concat_ignore_null")) {
            return new String("string.concat_ignore_null");
        }
        if (trimFnName.equalsIgnoreCase("chr") || trimFnName.equalsIgnoreCase("char") || trimFnName.equalsIgnoreCase("nchar")) {
            return new String("string.chr");
        }
        if (trimFnName.equalsIgnoreCase("initcap")) {
            return new String("string.initcap");
        }
        if (trimFnName.equalsIgnoreCase("rawtohex")) {
            return new String("string.rawtohex");
        }
        if (trimFnName.equalsIgnoreCase("indexof") || trimFnName.equalsIgnoreCase("substring_position")) {
            return new String("string.indexof");
        }
        if (trimFnName.equalsIgnoreCase("soundex") && (isVectorWise || isPostgreSql)) {
            return new String("string.soundex");
        }
        if (trimFnName.equalsIgnoreCase("substring_between")) {
            return new String("string.substring_between");
        }
        if (trimFnName.equalsIgnoreCase("substring_count")) {
            return new String("string.substring_count");
        }
        if (trimFnName.equalsIgnoreCase("isstartswith") || trimFnName.equalsIgnoreCase("isendswith") || trimFnName.equalsIgnoreCase("iscontains") || trimFnName.equalsIgnoreCase("isempty") || trimFnName.equalsIgnoreCase("to_string") || trimFnName.equalsIgnoreCase("convert_to_datetime")) {
            return new String("string.strsearch");
        }
        if (trimFnName.equalsIgnoreCase("pi")) {
            return new String("math.pi");
        }
        if (trimFnName.equalsIgnoreCase("pow") || trimFnName.equalsIgnoreCase("power")) {
            return new String("math.power");
        }
        if (trimFnName.equalsIgnoreCase("square")) {
            return new String("math.square");
        }
        if (trimFnName.equalsIgnoreCase("cot")) {
            return new String("math.cot");
        }
        if (trimFnName.equalsIgnoreCase("round")) {
            return new String("math.round");
        }
        if (trimFnName.equalsIgnoreCase("abs")) {
            return new String("math.abs");
        }
        if (trimFnName.equalsIgnoreCase("exp")) {
            return new String("math.exp");
        }
        if (trimFnName.equalsIgnoreCase("log") || trimFnName.equalsIgnoreCase("log10")) {
            return new String("math.log");
        }
        if (trimFnName.equalsIgnoreCase("ln")) {
            return new String("math.ln");
        }
        if (trimFnName.equalsIgnoreCase("sqrt")) {
            return new String("math.sqrt");
        }
        if (trimFnName.equalsIgnoreCase("rand") || trimFnName.equalsIgnoreCase("random")) {
            return new String("math.rand");
        }
        if (trimFnName.equalsIgnoreCase("ceil") || trimFnName.equalsIgnoreCase("ceiling")) {
            return new String("math.ceil");
        }
        if (trimFnName.equalsIgnoreCase("floor")) {
            return new String("math.floor");
        }
        if (trimFnName.equalsIgnoreCase("sin") || trimFnName.equalsIgnoreCase("cos") || trimFnName.equalsIgnoreCase("tan") || trimFnName.equalsIgnoreCase("asin") || trimFnName.equalsIgnoreCase("acos") || trimFnName.equalsIgnoreCase("atan") || trimFnName.equalsIgnoreCase("sign")) {
            return new String("math.trig");
        }
        if (trimFnName.equalsIgnoreCase("sinh") || trimFnName.equalsIgnoreCase("cosh") || trimFnName.equalsIgnoreCase("tanh")) {
            return new String("math.trigh");
        }
        if (trimFnName.equalsIgnoreCase("atan2") || trimFnName.equalsIgnoreCase("atn2")) {
            return new String("math.atan2");
        }
        if (trimFnName.equalsIgnoreCase("mod")) {
            return new String("math.mod");
        }
        if (trimFnName.equalsIgnoreCase("to_number")) {
            return new String("math.tonumber");
        }
        if (trimFnName.equalsIgnoreCase("trunc") || trimFnName.equalsIgnoreCase("truncate") || trimFnName.equalsIgnoreCase("integer") || trimFnName.equalsIgnoreCase("int") || trimFnName.equalsIgnoreCase("decimal") || trimFnName.equalsIgnoreCase("dec") || trimFnName.equalsIgnoreCase("date_trunc")) {
            return new String("math.trunc");
        }
        if (trimFnName.equalsIgnoreCase("smallint")) {
            return new String("math.smallint");
        }
        if (trimFnName.equalsIgnoreCase("bigint")) {
            return new String("math.bigint");
        }
        if (trimFnName.equalsIgnoreCase("degrees")) {
            return new String("math.degrees");
        }
        if (trimFnName.equalsIgnoreCase("radians")) {
            return new String("math.radians");
        }
        if (trimFnName.equalsIgnoreCase("getdate") || trimFnName.equalsIgnoreCase("getutcdate") || trimFnName.equalsIgnoreCase("now") || trimFnName.equalsIgnoreCase("sysdate")) {
            return new String("date.getdate");
        }
        if (trimFnName.equalsIgnoreCase("to_char")) {
            return new String("date.tochar");
        }
        if (trimFnName.equalsIgnoreCase("to_date") || trimFnName.equalsIgnoreCase("to_timestamp")) {
            return new String("date.todate");
        }
        if (trimFnName.equalsIgnoreCase("add_months")) {
            return new String("date.addmonths");
        }
        if (trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub")) {
            return new String("date.dateadd");
        }
        if (trimFnName.equalsIgnoreCase("dateadd") && !SwisSQLOptions.SOURCE_DB_IS_ORACLE) {
            return new String("date.dateadd");
        }
        if (isPostgreSql && trimFnName.equalsIgnoreCase("unix_timestamp")) {
            return new String("date.unixTimestamp");
        }
        if (trimFnName.equalsIgnoreCase("datediff") && !SwisSQLOptions.SOURCE_DB_IS_ORACLE) {
            return new String("date.datediff");
        }
        if (trimFnName.equalsIgnoreCase("months_between")) {
            return new String("date.months_between");
        }
        if (trimFnName.equalsIgnoreCase("datepart")) {
            return new String("date.datepart");
        }
        if (trimFnName.equalsIgnoreCase("monthname") || trimFnName.equalsIgnoreCase("dayofweek") || trimFnName.equalsIgnoreCase("julian_day") || trimFnName.equalsIgnoreCase("week_iso") || trimFnName.equalsIgnoreCase("days")) {
            return new String("date.datepart");
        }
        if (trimFnName.equalsIgnoreCase("hour")) {
            return new String("date.datepart");
        }
        if (trimFnName.equalsIgnoreCase("month") || trimFnName.equalsIgnoreCase("monthnum")) {
            return new String("date.month");
        }
        if (trimFnName.equalsIgnoreCase("day")) {
            return new String("date.day");
        }
        if (trimFnName.equalsIgnoreCase("dayofquarter")) {
            return new String("date.dayofquarter");
        }
        if (trimFnName.equalsIgnoreCase("addyear") || trimFnName.equalsIgnoreCase("addmonth") || trimFnName.equalsIgnoreCase("addweek") || trimFnName.equalsIgnoreCase("addquarter") || trimFnName.equalsIgnoreCase("addhour") || trimFnName.equalsIgnoreCase("addminute") || trimFnName.equalsIgnoreCase("addsecond")) {
            return new String("date.addToDate");
        }
        if (trimFnName.equalsIgnoreCase("year")) {
            return new String("date.year");
        }
        if (trimFnName.equalsIgnoreCase("start_day")) {
            return new String("date.start_day");
        }
        if (trimFnName.equalsIgnoreCase("end_day")) {
            return new String("date.end_day");
        }
        if (trimFnName.equalsIgnoreCase("last_day")) {
            return new String("date.last_day");
        }
        if (trimFnName.equalsIgnoreCase("date")) {
            return new String("date.date");
        }
        if (trimFnName.equalsIgnoreCase("next_day")) {
            return new String("date.next_day");
        }
        if (trimFnName.equalsIgnoreCase("isprevious_nyear") || trimFnName.equalsIgnoreCase("isprevious_nmonth") || trimFnName.equalsIgnoreCase("isprevious_nquarter") || trimFnName.equalsIgnoreCase("ispreviousweek") || trimFnName.equalsIgnoreCase("isprevious_nday") || trimFnName.equalsIgnoreCase("yesterday") || trimFnName.equalsIgnoreCase("previous_nday") || trimFnName.equalsIgnoreCase("previous_nmonth")) {
            return new String("date.previousDate");
        }
        if (trimFnName.equalsIgnoreCase("islast_nyear") || trimFnName.equalsIgnoreCase("islast_nmonth") || trimFnName.equalsIgnoreCase("islast_nquarter") || trimFnName.equalsIgnoreCase("islast_nday") || trimFnName.equalsIgnoreCase("last_nday") || trimFnName.equalsIgnoreCase("last_nmonth")) {
            return new String("date.lastDate");
        }
        if (trimFnName.equalsIgnoreCase("isnext_nyear") || trimFnName.equalsIgnoreCase("isnext_nmonth") || trimFnName.equalsIgnoreCase("isnext_nquarter") || trimFnName.equalsIgnoreCase("isnextweek") || trimFnName.equalsIgnoreCase("isnext_nday") || trimFnName.equalsIgnoreCase("tomorrow") || trimFnName.equalsIgnoreCase("next_nday") || trimFnName.equalsIgnoreCase("next_nmonth") || trimFnName.equalsIgnoreCase("next_weekday")) {
            return new String("date.nextDate");
        }
        if (trimFnName.equalsIgnoreCase("iscurrentyear") || trimFnName.equalsIgnoreCase("iscurrentmonth") || trimFnName.equalsIgnoreCase("iscurrentquarter") || trimFnName.equalsIgnoreCase("iscurrentweek") || trimFnName.equalsIgnoreCase("today")) {
            return new String("date.currentDate");
        }
        if (trimFnName.equalsIgnoreCase("business_completion_day") || trimFnName.equalsIgnoreCase("business_days") || trimFnName.equalsIgnoreCase("business_hours")) {
            return new String("date.businessDate");
        }
        if (trimFnName.equalsIgnoreCase("start_datetime")) {
            return new String("date.start_datetime");
        }
        if (isMySql && (trimFnName.equalsIgnoreCase("quartername") || trimFnName.equalsIgnoreCase("quarternum") || trimFnName.equalsIgnoreCase("quarter"))) {
            return new String("date.quarter");
        }
        if (trimFnName.equalsIgnoreCase("extract")) {
            return new String("date.extract");
        }
        if (!isMsAzure && trimFnName.equalsIgnoreCase("second")) {
            return new String("math.trunc");
        }
        if (trimFnName.equalsIgnoreCase("date_format") || (isVectorWise && trimFnName.equalsIgnoreCase("TIME_FORMAT"))) {
            return new String("date.dateformat");
        }
        if (trimFnName.equalsIgnoreCase("from_tz")) {
            return new String("date.FromTZ");
        }
        if (trimFnName.equalsIgnoreCase("to_timestamp_tz")) {
            return new String("date.ToTimestampTZ");
        }
        if (trimFnName.equalsIgnoreCase("NUMTODSINTERVAL")) {
            return new String("date.NumToDSInterval");
        }
        if (trimFnName.equalsIgnoreCase("NumToYMInterval")) {
            return new String("date.NumToYMInterval");
        }
        if (trimFnName.equalsIgnoreCase("sysdatetime") || trimFnName.equalsIgnoreCase("SYSUTCDATETIME")) {
            return new String("date.SysDateTime");
        }
        if (trimFnName.equalsIgnoreCase("SYSDATETIMEOFFSET")) {
            return new String("date.SysDateTimeOffset");
        }
        if (trimFnName.equalsIgnoreCase("first_date_current_week")) {
            return new String("date.FirstDateCurrentWeek");
        }
        if (trimFnName.equalsIgnoreCase("weekofmonth")) {
            return new String("date.WeekOfMonth");
        }
        if (trimFnName.equalsIgnoreCase("absquarter")) {
            return new String("date.AbsoluteQuarter");
        }
        if (trimFnName.equalsIgnoreCase("absmonth")) {
            return new String("date.AbsoluteMonth");
        }
        if (trimFnName.equalsIgnoreCase("absweek")) {
            return new String("date.AbsoluteWeek");
        }
        if (isMySql && trimFnName.equalsIgnoreCase("weekofyear")) {
            return new String("date.week");
        }
        if (trimFnName.equalsIgnoreCase("group_first") || trimFnName.equalsIgnoreCase("group_last")) {
            return new String("string.groupFirstLast");
        }
        if (trimFnName.equalsIgnoreCase("to_integer") || trimFnName.equalsIgnoreCase("to_decimal") || trimFnName.equalsIgnoreCase("to_currency") || trimFnName.equalsIgnoreCase("to_percentage")) {
            return new String("string.DecInt");
        }
        if (isVectorWise && (trimFnName.equalsIgnoreCase("minute") || trimFnName.equalsIgnoreCase("quarter") || trimFnName.equalsIgnoreCase("dayofmonth") || trimFnName.equalsIgnoreCase("dayofyear"))) {
            return new String("date.extract");
        }
        if (isVectorWise && (trimFnName.equalsIgnoreCase("from_unixtime") || trimFnName.equalsIgnoreCase("from_days") || trimFnName.equalsIgnoreCase("to_days") || trimFnName.equalsIgnoreCase("dayname"))) {
            return new String("date.FromTZ");
        }
        if (isVectorWise && trimFnName.equalsIgnoreCase("microsecond")) {
            return new String("date.datepart");
        }
        if (isVectorWise && (trimFnName.equalsIgnoreCase("time_to_sec") || trimFnName.equalsIgnoreCase("sec_to_time") || trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("subdate") || trimFnName.equalsIgnoreCase("timediff") || trimFnName.equalsIgnoreCase("weekday"))) {
            return new String("date.TimeToSec");
        }
        if (isVectorWise && trimFnName.equalsIgnoreCase("binary")) {
            return new String("misc.Binary");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("hex") || trimFnName.equalsIgnoreCase("unhex"))) {
            return new String("misc.hex");
        }
        if (isVectorWise && (trimFnName.equalsIgnoreCase("makedate") || trimFnName.equalsIgnoreCase("period_add") || trimFnName.equalsIgnoreCase("maketime") || trimFnName.equalsIgnoreCase("period_diff"))) {
            return new String("date.MakeDate");
        }
        if (isVectorWise && (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("yearweek") || trimFnName.equalsIgnoreCase("addtime") || trimFnName.equalsIgnoreCase("subtime") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("convert_tz") || trimFnName.equalsIgnoreCase("convert_timezone"))) {
            return new String("date.week");
        }
        if (isVectorWise && (trimFnName.equalsIgnoreCase("curdate") || trimFnName.equalsIgnoreCase("currentdate") || trimFnName.equalsIgnoreCase("current_date") || trimFnName.equalsIgnoreCase("cur_date"))) {
            return new String("date.getdate");
        }
        if (isVectorWise && trimFnName.equalsIgnoreCase("format")) {
            return new String("misc.format");
        }
        if (isVectorWise && trimFnName.equalsIgnoreCase("STR_TO_DATE")) {
            return new String("date.StrToDate");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub") || trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("subdate"))) {
            return new String("date.dateadd");
        }
        if (isPostgreSql && trimFnName.equalsIgnoreCase("mid")) {
            return new String("misc.hex");
        }
        if ((isOracle || isPostgreSql || isMsAzure) && trimFnName.equalsIgnoreCase("log2")) {
            return new String("math.log");
        }
        if (isPostgreSql && trimFnName.equalsIgnoreCase("convert_tz")) {
            return new String("date.convertTz");
        }
        if (trimFnName.equalsIgnoreCase("timestampdiff") || trimFnName.equalsIgnoreCase("dateandtimediff") || trimFnName.equalsIgnoreCase("age_years") || trimFnName.equalsIgnoreCase("age_months") || trimFnName.equalsIgnoreCase("days_between")) {
            return new String("date.timestampDiff");
        }
        if ((isPostgreSql || isVectorWise || isMySql) && trimFnName.equalsIgnoreCase("TIMESTAMPADD")) {
            return new String("date.timestampAdd");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("ADDTIME") || trimFnName.equalsIgnoreCase("SUBTIME"))) {
            return new String("date.timeAddSub");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("curdate") || trimFnName.equalsIgnoreCase("currentdate") || trimFnName.equalsIgnoreCase("current_date"))) {
            return new String("date.getdate");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("current_time") || trimFnName.equalsIgnoreCase("curtime"))) {
            return new String("date.gettime");
        }
        if (isVectorWise && (trimFnName.equalsIgnoreCase("current_time") || trimFnName.equalsIgnoreCase("curtime"))) {
            return new String("date.gettime");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("weekday") || trimFnName.equalsIgnoreCase("minute") || trimFnName.equalsIgnoreCase("microsecond") || trimFnName.equalsIgnoreCase("quarter") || trimFnName.equalsIgnoreCase("dayname") || trimFnName.equalsIgnoreCase("dayofmonth") || trimFnName.equalsIgnoreCase("dayofyear"))) {
            return new String("date.extract");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("makedate") || trimFnName.equalsIgnoreCase("period_add") || trimFnName.equalsIgnoreCase("period_diff") || trimFnName.equalsIgnoreCase("maketime"))) {
            return new String("date.MakeDate");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("timestamp") || trimFnName.equalsIgnoreCase("timediff"))) {
            return new String("date.TimeToSec");
        }
        if ((isPostgreSql || isMySql) && trimFnName.equalsIgnoreCase("SEC_TO_TIME")) {
            return new String("date.SecToTime");
        }
        if ((isVectorWise || isPostgreSql) && trimFnName.equalsIgnoreCase("localtimestamp")) {
            return new String("date.localtimestamp");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("STR_TO_DATE") || trimFnName.equalsIgnoreCase("TIME_FORMAT"))) {
            return new String("date.dateformat");
        }
        if (trimFnName.equalsIgnoreCase("time")) {
            return new String("date.time");
        }
        if ((isVectorWise || isPostgreSql) && trimFnName.equalsIgnoreCase("utc_date")) {
            return new String("date.utcDate");
        }
        if ((isVectorWise || isPostgreSql) && trimFnName.equalsIgnoreCase("utc_timestamp")) {
            return new String("date.utcTimestamp");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("yearweek"))) {
            return new String("date.week");
        }
        if ((isVectorWise || isPostgreSql) && trimFnName.equalsIgnoreCase("current_timestamp")) {
            return new String("date.currentTimestamp");
        }
        if (isPostgreSql && (trimFnName.equalsIgnoreCase("FROM_UNIXTIME") || trimFnName.equalsIgnoreCase("from_days") || trimFnName.equalsIgnoreCase("to_days"))) {
            return new String("date.FromTZ");
        }
        if (isPostgreSql && trimFnName.equalsIgnoreCase("time_to_sec")) {
            return new String("date.TimeToSec");
        }
        if (isPostgreSql && trimFnName.equalsIgnoreCase("binary")) {
            return new String("misc.Binary");
        }
        if (isPostgreSql && trimFnName.equalsIgnoreCase("format")) {
            return new String("misc.format");
        }
        if (isPostgreSql && trimFnName.equalsIgnoreCase("conv")) {
            return new String("misc.conv");
        }
        if ((isVectorWise || isPostgreSql || isMsAzure || isOracle) && trimFnName.equalsIgnoreCase("strcmp")) {
            return new String("string.strcmp");
        }
        if ((isVectorWise || isPostgreSql) && trimFnName.equalsIgnoreCase("insert")) {
            return new String("string.insert");
        }
        if (isMsAzure) {
            if (trimFnName.equalsIgnoreCase("mid")) {
                return new String("string.substring");
            }
            if (trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub") || trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("subdate") || trimFnName.equalsIgnoreCase("datediff")) {
                return new String("date.dateadd");
            }
            if (trimFnName.equalsIgnoreCase("minute") || trimFnName.equalsIgnoreCase("quarter") || trimFnName.equalsIgnoreCase("dayname") || trimFnName.equalsIgnoreCase("weekday") || trimFnName.equalsIgnoreCase("dayofyear")) {
                return new String("date.extract");
            }
            if (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("yearweek") || trimFnName.equalsIgnoreCase("convert_tz")) {
                return new String("date.week");
            }
            if (trimFnName.equalsIgnoreCase("date_diff")) {
                return new String("date.datediff");
            }
            if (trimFnName.equalsIgnoreCase("STR_TO_DATE")) {
                return new String("date.dateformat");
            }
            if (trimFnName.equalsIgnoreCase("ADDTIME") || trimFnName.equalsIgnoreCase("SUBTIME") || trimFnName.equalsIgnoreCase("FROM_UNIXTIME")) {
                return new String("date.timeAddSub");
            }
            if (trimFnName.equalsIgnoreCase("makedate")) {
                return new String("date.MakeDate");
            }
            if (trimFnName.equalsIgnoreCase("ifnull")) {
                return new String("misc.ifnull");
            }
            if (trimFnName.equalsIgnoreCase("insert")) {
                return new String("string.insert");
            }
            if (trimFnName.equalsIgnoreCase("second")) {
                return new String("date.datepart");
            }
        }
        if (fname.trim().equalsIgnoreCase("TO_DSINTERVAL")) {
            return new String("date.ToDSInterval");
        }
        if (fname.trim().equalsIgnoreCase("TO_YMINTERVAL")) {
            return new String("date.ToYMInterval");
        }
        if (trimFnName.equalsIgnoreCase("ascii") || trimFnName.equalsIgnoreCase("unicode") || (isPostgreSql && trimFnName.equalsIgnoreCase("ord"))) {
            return new String("misc.ascii");
        }
        if (trimFnName.equalsIgnoreCase("contains")) {
            return new String("misc.contains");
        }
        if (trimFnName.equalsIgnoreCase("bitand")) {
            return new String("misc.bitand");
        }
        if (trimFnName.equalsIgnoreCase("bitor")) {
            return new String("misc.bitor");
        }
        if (trimFnName.equalsIgnoreCase("user")) {
            return new String("misc.user");
        }
        if (trimFnName.equalsIgnoreCase("userenv")) {
            return new String("misc.userenv");
        }
        if (trimFnName.equalsIgnoreCase("suser_sname") || trimFnName.equalsIgnoreCase("suser_name")) {
            return new String("misc.suser_sname");
        }
        if (trimFnName.equalsIgnoreCase("suser_sid") || trimFnName.equalsIgnoreCase("suser_id") || trimFnName.equalsIgnoreCase("user_id")) {
            return new String("misc.suser_sid");
        }
        if (trimFnName.equalsIgnoreCase("decode")) {
            return new String("misc.decode");
        }
        if (trimFnName.equalsIgnoreCase("isnull")) {
            return new String("misc.isnull");
        }
        if (trimFnName.equalsIgnoreCase("nullif")) {
            return new String("misc.nullif");
        }
        if (trimFnName.equalsIgnoreCase("nvl2")) {
            return new String("misc.nvl2");
        }
        if (trimFnName.equalsIgnoreCase("nvl") || trimFnName.equalsIgnoreCase("coalesce")) {
            return new String("misc.nvl");
        }
        if (trimFnName.equalsIgnoreCase("translate")) {
            return new String("misc.translate");
        }
        if (trimFnName.equalsIgnoreCase("convert")) {
            return new String("misc.convert");
        }
        if (trimFnName.equalsIgnoreCase("cast")) {
            return new String("misc.cast");
        }
        if (trimFnName.equalsIgnoreCase("sys_guid") || trimFnName.equalsIgnoreCase("newid")) {
            return new String("misc.sysguid");
        }
        if (trimFnName.equalsIgnoreCase("greatest") || trimFnName.equalsIgnoreCase("findmaxvalue")) {
            return new String("misc.greatest");
        }
        if (trimFnName.equalsIgnoreCase("least") || trimFnName.equalsIgnoreCase("findminvalue")) {
            return new String("misc.least");
        }
        if (trimFnName.equalsIgnoreCase("object_id")) {
            return new String("misc.object_id");
        }
        if (trimFnName.equalsIgnoreCase("serverproperty")) {
            return new String("misc.serverproperty");
        }
        if (trimFnName.equalsIgnoreCase("db_id") || trimFnName.equalsIgnoreCase("db_name") || trimFnName.equalsIgnoreCase("host_id") || trimFnName.equalsIgnoreCase("host_name")) {
            return new String("misc.sys_context");
        }
        if (trimFnName.equalsIgnoreCase("is_srvrolemember")) {
            return new String("misc.is_srvrolemember");
        }
        if (trimFnName.equalsIgnoreCase("if")) {
            return new String("misc.iffunction");
        }
        if (trimFnName.equalsIgnoreCase("if_case")) {
            return new String("misc.ifmatches");
        }
        if ((isPostgreSql || isVectorWise || isOracle) && trimFnName.equalsIgnoreCase("ifnull")) {
            return new String("misc.ifnull");
        }
        if (trimFnName.equalsIgnoreCase("hextoraw")) {
            return new String("misc.hextoraw");
        }
        if (trimFnName.equalsIgnoreCase("grouping_id") || fname.trim().equalsIgnoreCase("group_id")) {
            return new String("misc.grouping_id");
        }
        if (trimFnName.equalsIgnoreCase("hashbytes")) {
            return new String("misc.Hashbytes");
        }
        if (trimFnName.equalsIgnoreCase("sum")) {
            return new String("aggregate.sum");
        }
        if (trimFnName.equalsIgnoreCase("sumif")) {
            return new String("aggregate.aggregateIf");
        }
        if (trimFnName.equalsIgnoreCase("avg")) {
            return new String("aggregate.avg");
        }
        if (trimFnName.equalsIgnoreCase("avgif")) {
            return new String("aggregate.aggregateIf");
        }
        if (trimFnName.equalsIgnoreCase("count")) {
            return new String("aggregate.count");
        }
        if (trimFnName.equalsIgnoreCase("countif")) {
            return new String("aggregate.aggregateIf");
        }
        if (trimFnName.equalsIgnoreCase("count_wb")) {
            return new String("aggregate.aggregateIf");
        }
        if (trimFnName.equalsIgnoreCase("distinctcount")) {
            return new String("aggregate.aggregateIf");
        }
        if (trimFnName.equalsIgnoreCase("ytd")) {
            return new String("aggregate.ytd");
        }
        if (trimFnName.equalsIgnoreCase("qtd")) {
            return new String("aggregate.qtd");
        }
        if (trimFnName.equalsIgnoreCase("mtd")) {
            return new String("aggregate.mtd");
        }
        if (trimFnName.equalsIgnoreCase("max")) {
            return new String("aggregate.max");
        }
        if (trimFnName.equalsIgnoreCase("min")) {
            return new String("aggregate.min");
        }
        if (trimFnName.equalsIgnoreCase("stddev") || trimFnName.equalsIgnoreCase("stdev") || trimFnName.equalsIgnoreCase("std") || trimFnName.equalsIgnoreCase("stddev_pop") || trimFnName.equalsIgnoreCase("stddev_samp")) {
            return new String("aggregate.stddeviation");
        }
        if (trimFnName.equalsIgnoreCase("variance") || trimFnName.equalsIgnoreCase("var") || trimFnName.equalsIgnoreCase("varp") || trimFnName.equalsIgnoreCase("var_pop") || trimFnName.equalsIgnoreCase("var_samp") || trimFnName.equalsIgnoreCase("corr") || trimFnName.equalsIgnoreCase("covar_pop")) {
            return new String("aggregate.variance");
        }
        if (trimFnName.equalsIgnoreCase("regr_intercept") || trimFnName.equalsIgnoreCase("regr_r2") || trimFnName.equalsIgnoreCase("regr_slope") || trimFnName.equalsIgnoreCase("regr_avgx") || trimFnName.equalsIgnoreCase("regr_avgy")) {
            return new String("aggregate.regression");
        }
        if ((trimFnName.equalsIgnoreCase("group_concat") || trimFnName.equalsIgnoreCase("substring_index")) && isVectorWise) {
            return new String("string.repeat");
        }
        if ((isMySql || isPostgreSql || isVectorWise) && (trimFnName.equalsIgnoreCase("mode") || trimFnName.equalsIgnoreCase("stats_mode"))) {
            return new String("aggregate.mode");
        }
        if ((isMySql || isPostgreSql || isVectorWise) && trimFnName.equalsIgnoreCase("median")) {
            return new String("aggregate.median");
        }
        if ((isMySql || isPostgreSql || isVectorWise) && trimFnName.equalsIgnoreCase("mean")) {
            return new String("aggregate.mean");
        }
        if ((isMySql || isPostgreSql || isVectorWise || isOracle) && (trimFnName.equalsIgnoreCase("percentile") || trimFnName.equalsIgnoreCase("percentile_cont"))) {
            return new String("aggregate.percentile");
        }
        if (isPostgreSql && trimFnName.equalsIgnoreCase("group_concat")) {
            return new String("string.groupConcat");
        }
        if (isPostgreSql && trimFnName.equalsIgnoreCase("substring_index")) {
            return new String("string.substringIndex");
        }
        if ((isPostgreSql && (trimFnName.equalsIgnoreCase("elt") || trimFnName.equalsIgnoreCase("field") || trimFnName.equalsIgnoreCase("find_in_set") || trimFnName.equalsIgnoreCase("make_set"))) || (isVectorWise && (trimFnName.equalsIgnoreCase("elt") || trimFnName.equalsIgnoreCase("field")))) {
            return new String("string.set");
        }
        if (trimFnName.equalsIgnoreCase("first_value")) {
            return new String("analytic.FirstValue");
        }
        if (trimFnName.equalsIgnoreCase("last_value")) {
            return new String("analytic.LastValue");
        }
        if (trimFnName.equalsIgnoreCase("dense_rank")) {
            return new String("analytic.DenseRank");
        }
        if (trimFnName.equalsIgnoreCase("lead")) {
            return new String("analytic.Lead");
        }
        if (trimFnName.equalsIgnoreCase("lag")) {
            return new String("analytic.Lag");
        }
        if (trimFnName.equalsIgnoreCase("ntile")) {
            return new String("analytic.Ntile");
        }
        if (trimFnName.equalsIgnoreCase("Ratio_To_Report")) {
            return new String("analytic.Ratio_To_Report");
        }
        if (trimFnName.equalsIgnoreCase("cume_dist")) {
            return new String("analytic.Cume_Dist");
        }
        if (trimFnName.equalsIgnoreCase("rank") || trimFnName.equalsIgnoreCase("percent_rank")) {
            return new String("analytic.Rank");
        }
        if (trimFnName.equalsIgnoreCase("row_number")) {
            return new String("analytic.RowNumber");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fyear")) {
            return new String("reports.fiscalYear");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fyeardt")) {
            return new String("reports.fiscalYear");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fyearintrval")) {
            return new String("reports.fiscalYear");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fquarter")) {
            return new String("reports.fiscalQuarter");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fquarterdt")) {
            return new String("reports.fiscalQuarter");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fquarterintrval")) {
            return new String("reports.fiscalQuarter");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fquarteryear")) {
            return new String("reports.fiscalQuarterYear");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fquarteryeardt")) {
            return new String("reports.fiscalQuarterYear");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fquarteryearintrval")) {
            return new String("reports.fiscalQuarterYear");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweek")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweekdtNwkstrtday")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweekintrvalNwkstrtday")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweekdt")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweekintrval")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweekyear")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweekyeardtNwkstrtday")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweekyearintrvalNwkstrtday")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweekyeardt")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_fweekyearintrval")) {
            return new String("reports.fiscalWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
            return new String("reports.startWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
            return new String("reports.startWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_WeekYearIntrvalNwkStrtDay")) {
            return new String("reports.startWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_WeekIntrvalNwkStrtDay")) {
            return new String("reports.startWeek");
        }
        if (trimFnName.equalsIgnoreCase("ZR_date_trunc")) {
            return new String("reports.dateTrunc");
        }
        if (trimFnName.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
            return new String("date.previousDate");
        }
        if (trimFnName.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
            return new String("date.previousDate");
        }
        if (trimFnName.equalsIgnoreCase("ZR_ISLASTMONTH")) {
            return new String("date.lastDate");
        }
        if (trimFnName.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
            return new String("date.lastDate");
        }
        if (trimFnName.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
            return new String("date.nextDate");
        }
        if (trimFnName.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
            return new String("date.nextDate");
        }
        if (trimFnName.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
            return new String("date.businessDate");
        }
        if (trimFnName.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
            return new String("date.businessDate");
        }
        if (trimFnName.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
            return new String("date.businessDate");
        }
        if (trimFnName.equalsIgnoreCase("ZR_TEXTBETWEEN")) {
            return new String("string.substring_between");
        }
        return null;
    }
    
    public void toOracle() throws ConvertException {
    }
    
    public void toMSSQLServer() throws ConvertException {
    }
    
    public void toSybase() throws ConvertException {
    }
    
    public void toDB2() throws ConvertException {
    }
    
    public void toPostgreSQL() throws ConvertException {
    }
    
    public void toMySQL() throws ConvertException {
    }
    
    public void toANSISQL() throws ConvertException {
    }
    
    public void toInformix() throws ConvertException {
    }
    
    public void toTimesTen() throws ConvertException {
    }
    
    public void toNetezza() throws ConvertException {
    }
    
    public void toTeradata() throws ConvertException {
    }
    
    public void toVectorWise() throws ConvertException {
    }
    
    public void toOracle(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toMSSQLServer(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toSybase(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toDB2(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toPostgreSQL(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toMySQL(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toANSISQL(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toInformix(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toTimesTen(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toNetezza(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toTeradata(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public void toVectorWise(final SelectQueryStatement to, final SelectQueryStatement from) throws ConvertException {
    }
    
    public boolean getOuterJoin() {
        return this.outerJoin;
    }
    
    public void setOuterJoin(final boolean oj) {
        this.outerJoin = oj;
    }
    
    private boolean isSQLServerSystemFunction(final String functionname) {
        final String[] arr = SwisSQLUtils.getSystemFunctions(2);
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i].equalsIgnoreCase(functionname)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSybaseSystemFunction(final String functionname) {
        final String[] arr = SwisSQLUtils.getSystemFunctions(7);
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i].equalsIgnoreCase(functionname)) {
                return true;
            }
        }
        return false;
    }
    
    public FromTable createTeradataDerivedTable(final SelectQueryStatement to_sqs, final FunctionCalls fnCall, final SelectColumn functionArgument, final String alias) throws ConvertException {
        return null;
    }
    
    private void handleKeepDenseRank(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final FunctionCalls aggrFn) throws ConvertException {
        boolean createNewDerivedTable = false;
        String alias = from_sqs.getFromClause().getLastElement().getAliasName();
        String idx = "" + from_sqs.getOlapDerivedTables().size();
        final String keepDenseRank = "keepdenserank" + aggrFn.getFunctionName() + this.getOrderBy();
        if (this.getPartitionByClause() != null) {
            final String partitionString = "keepdenserank" + this.getPartitionByClause().toString() + aggrFn.getFunctionName() + this.getOrderBy();
            if (!from_sqs.getOlapDerivedTables().containsKey(partitionString)) {
                createNewDerivedTable = true;
            }
        }
        else if (!from_sqs.getOlapDerivedTables().containsKey(keepDenseRank)) {
            createNewDerivedTable = true;
        }
        if (createNewDerivedTable) {
            final SelectQueryStatement derivedTable = new SelectQueryStatement();
            final SelectStatement selectStatement = new SelectStatement();
            selectStatement.setSelectClause("SELECT");
            final Vector selectItemsList = new Vector();
            final Vector newWhereItemList = new Vector();
            final HavingStatement qualifyStmt = new HavingStatement();
            qualifyStmt.setHavingClause("QUALIFY");
            final WhereExpression qualifyExpression = new WhereExpression();
            final Vector qualifyItems = new Vector();
            final GroupByStatement gbs = new GroupByStatement();
            gbs.setGroupClause("GROUP BY");
            final Vector gbsItems = new Vector();
            final QueryPartitionClause qpc = new QueryPartitionClause();
            qpc.setPartitionBy("PARTITION BY");
            final ArrayList qpcSelCols = new ArrayList();
            final Vector tableColumns = from_sqs.getSelectStatement().getSelectItemList();
            for (int tcni = 0; tcni < tableColumns.size(); ++tcni) {
                final SelectColumn tcn = tableColumns.get(tcni);
                final Vector tcnColExp = tcn.getColumnExpression();
                for (int j = 0; j < tcnColExp.size(); ++j) {
                    final Object jObj = tcnColExp.get(j);
                    if (jObj instanceof TableColumn) {
                        SelectColumn newTCN = new SelectColumn();
                        newTCN.setColumnExpression(tcnColExp);
                        newTCN = newTCN.toTeradataSelect(null, null);
                        newTCN.setAliasName("ADV_ALIAS_" + tcni);
                        newTCN.setEndsWith(",");
                        selectItemsList.add(newTCN);
                        final SelectColumn gbsTCN = new SelectColumn();
                        gbsTCN.setColumnExpression(newTCN.getColumnExpression());
                        gbsItems.add(gbsTCN);
                        final SelectColumn qpcTCN = new SelectColumn();
                        qpcTCN.setColumnExpression(newTCN.getColumnExpression());
                        qpcTCN.setEndsWith(",");
                        qpcSelCols.add(qpcTCN);
                        String whereidx = "";
                        if (from_sqs.getOlapDerivedTables().size() > 0) {
                            whereidx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
                        }
                        else {
                            whereidx = "" + from_sqs.getOlapDerivedTables().size();
                        }
                        newWhereItemList.add(this.generateWhereItems(tcn.toTeradataSelect(null, null), alias + whereidx, newTCN.getAliasName()));
                        break;
                    }
                }
            }
            qpcSelCols.get(qpcSelCols.size() - 1).setEndsWith(null);
            qpc.setSelectColumnList(qpcSelCols);
            gbs.setGroupByItemList(gbsItems);
            if (aggrFn.getPartitionByClause() != null) {
                newWhereItemList.clear();
                final ArrayList selColsList = aggrFn.getPartitionByClause().getSelectColumnList();
                for (int k = 0; k < selColsList.size(); ++k) {
                    if (selColsList.get(k) instanceof SelectColumn) {
                        final SelectColumn partSelCol = selColsList.get(k);
                        final SelectColumn newPartSelCol = new SelectColumn();
                        newPartSelCol.setColumnExpression(partSelCol.getColumnExpression());
                        newPartSelCol.setAliasName("partition_" + k);
                        newPartSelCol.setEndsWith(",");
                        newWhereItemList.add(this.generateWhereItems(partSelCol, alias + idx, "partition_" + k));
                        selectItemsList.add(newPartSelCol);
                    }
                }
            }
            else {
                aggrFn.setPartitionByClause(qpc);
            }
            if (from_sqs.getGroupByStatement() == null && to_sqs.getGroupByStatement() == null) {
                to_sqs.setGroupByStatement(gbs);
            }
            final SelectColumn rownumSelCol = new SelectColumn();
            final FunctionCalls rownumFunc = new FunctionCalls();
            final TableColumn rownumFuncName = new TableColumn();
            rownumFuncName.setColumnName("ROW_NUMBER");
            rownumFunc.setFunctionName(rownumFuncName);
            rownumFunc.setFunctionArguments(new Vector());
            if (aggrFn.getPartitionByClause() != null) {
                rownumFunc.setPartitionByClause(aggrFn.getPartitionByClause());
            }
            if (aggrFn.getOrderBy() != null) {
                final OrderByStatement obs = aggrFn.getOrderBy();
                if (aggrFn.getLast() != null) {
                    final Vector orderItemList = obs.getOrderItemList();
                    for (int i_count = 0; i_count < orderItemList.size(); ++i_count) {
                        final OrderItem oi = orderItemList.elementAt(i_count);
                        if (oi != null) {
                            final String orderType = oi.getOrder();
                            if (orderType != null && orderType.equalsIgnoreCase("ASC")) {
                                oi.setOrder("DESC");
                            }
                            if (orderType != null && orderType.equalsIgnoreCase("DESC")) {
                                oi.setOrder("ASC");
                            }
                            else if (orderType == null) {
                                oi.setOrder("DESC");
                            }
                        }
                    }
                }
                rownumFunc.setOrderBy(obs);
            }
            rownumFunc.setOver("OVER");
            final Vector rownumSelColExp = new Vector();
            rownumSelColExp.add(rownumFunc);
            rownumSelCol.setColumnExpression(rownumSelColExp);
            rownumSelCol.setAliasName("rownum_0");
            rownumSelCol.setEndsWith(",");
            selectItemsList.add(rownumSelCol);
            final SelectColumn aggrFnArg = aggrFn.getFunctionArguments().firstElement();
            aggrFnArg.setAliasName("denserank_" + alias + idx);
            aggrFnArg.setEndsWith(",");
            selectItemsList.add(aggrFnArg);
            final SelectColumn newAggrFnArg = new SelectColumn();
            final Vector newAggrFnArgExp = new Vector();
            final TableColumn newAggrFnArgCol = new TableColumn();
            newAggrFnArgCol.setColumnName("denserank_" + alias + idx);
            newAggrFnArgExp.add(newAggrFnArgCol);
            newAggrFnArg.setColumnExpression(newAggrFnArgExp);
            final Vector newFunctionArgs = new Vector();
            newFunctionArgs.add(newAggrFnArg);
            aggrFn.setFunctionArguments(newFunctionArgs);
            selectItemsList.lastElement().setEndsWith(null);
            selectStatement.setSelectItemList(selectItemsList);
            derivedTable.setSelectStatement(selectStatement);
            final WhereItem wi = new WhereItem();
            final WhereColumn lwc = new WhereColumn();
            final Vector lwcColExp = new Vector();
            final TableColumn tc1 = new TableColumn();
            tc1.setColumnName(rownumSelCol.getAliasName());
            lwcColExp.add(tc1);
            final WhereColumn rwc = new WhereColumn();
            final Vector rwcColExp = new Vector();
            rwcColExp.add("1");
            lwc.setColumnExpression(lwcColExp);
            rwc.setColumnExpression(rwcColExp);
            wi.setLeftWhereExp(lwc);
            wi.setRightWhereExp(rwc);
            wi.setOperator("=");
            qualifyExpression.addWhereItem(wi);
            qualifyItems.add(qualifyExpression);
            qualifyStmt.setHavingItems(qualifyItems);
            derivedTable.setHavingStatement(qualifyStmt);
            final FromTable derivedTableFromItem = new FromTable();
            derivedTableFromItem.setTableName(derivedTable);
            derivedTableFromItem.setAliasName(alias + idx);
            derivedTableFromItem.setJoinClause("INNER JOIN ");
            derivedTableFromItem.setOnOrUsingJoin("ON");
            final Vector joinCondition = new Vector();
            final WhereExpression we = new WhereExpression();
            we.setWhereItem(newWhereItemList);
            final Vector operators = new Vector();
            for (int s = 0; s < newWhereItemList.size() - 1; ++s) {
                operators.add("AND");
            }
            we.setOperator(operators);
            joinCondition.add(we);
            derivedTableFromItem.setJoinExpression(joinCondition);
            aggrFn.setKeep(null);
            aggrFn.setFirst(null);
            aggrFn.setLast(null);
            aggrFn.setDenseRank(null);
            aggrFn.setOver(null);
            aggrFn.setPartitionBy(null);
            aggrFn.setPartitionByClause(null);
            aggrFn.setOrderBy(null);
            from_sqs.setOlapFunctionPresent(true);
            if (this.getPartitionByClause() != null) {
                final String partitionString2 = "keepdenserank" + this.getPartitionByClause().toString();
                from_sqs.addOlapDerivedTables(partitionString2 + aggrFn.getFunctionName() + this.getOrderBy(), derivedTableFromItem);
            }
            else {
                from_sqs.addOlapDerivedTables("keepdenserank" + aggrFn.getFunctionName() + this.getOrderBy(), derivedTableFromItem);
            }
        }
        else {
            final SelectColumn newArgSelCol = new SelectColumn();
            final SelectColumn arg = this.getFunctionArguments().get(0);
            newArgSelCol.setColumnExpression(arg.getColumnExpression());
            newArgSelCol.setIgnoreNulls(arg.getIgnoreNulls());
            String partitionString3 = "keepdenserank";
            if (this.getPartitionByClause() != null) {
                partitionString3 = partitionString3 + this.getPartitionByClause().toString() + aggrFn.getFunctionName() + this.getOrderBy();
            }
            else {
                partitionString3 = partitionString3 + aggrFn.getFunctionName() + this.getOrderBy();
            }
            if (from_sqs.getOlapDerivedTables().containsKey(partitionString3)) {
                final FromTable derivedTable2 = from_sqs.getOlapDerivedTables().get(partitionString3);
                final Vector existingSelectItems = ((SelectQueryStatement)derivedTable2.getTableName()).getSelectStatement().getSelectItemList();
                final int siz = existingSelectItems.size();
                newArgSelCol.setAliasName("denserank_" + alias + siz);
                final SelectColumn selCol = existingSelectItems.get(siz - 1);
                if (selCol.getEndsWith() == null) {
                    selCol.setEndsWith(",");
                }
                existingSelectItems.add(newArgSelCol);
                final SelectColumn newAggrFnArg2 = new SelectColumn();
                final Vector newAggrFnArgExp2 = new Vector();
                final TableColumn newAggrFnArgCol2 = new TableColumn();
                newAggrFnArgCol2.setColumnName("denserank_" + alias + siz);
                newAggrFnArgExp2.add(newAggrFnArgCol2);
                newAggrFnArg2.setColumnExpression(newAggrFnArgExp2);
                final Vector newFunctionArgs2 = new Vector();
                newFunctionArgs2.add(newAggrFnArg2);
                aggrFn.setFunctionArguments(newFunctionArgs2);
                alias = derivedTable2.getAliasName();
                idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
            }
            aggrFn.setKeep(null);
            aggrFn.setFirst(null);
            aggrFn.setLast(null);
            aggrFn.setDenseRank(null);
            aggrFn.setOver(null);
            aggrFn.setPartitionBy(null);
            aggrFn.setPartitionByClause(null);
            aggrFn.setOrderBy(null);
            from_sqs.setOlapFunctionPresent(true);
        }
    }
    
    private WhereItem generateWhereItems(final SelectColumn selCol, final String derivedTableAlias, final String derivedTableColumn) throws ConvertException {
        final WhereItem wi = new WhereItem();
        final WhereColumn lwc = new WhereColumn();
        final Vector lwcColExp = new Vector();
        if (selCol.getEndsWith() != null) {
            selCol.setEndsWith(null);
        }
        if (selCol.getAliasName() != null) {
            lwcColExp.add(selCol.getAliasName());
        }
        else {
            lwcColExp.add(selCol);
        }
        final WhereColumn rwc = new WhereColumn();
        final Vector rwcColExp = new Vector();
        if (selCol != null) {
            final TableColumn rsc = new TableColumn();
            rsc.setTableName(derivedTableAlias);
            rsc.setColumnName(derivedTableColumn);
            rsc.setDot(".");
            rwcColExp.add(rsc);
        }
        lwc.setColumnExpression(lwcColExp);
        rwc.setColumnExpression(rwcColExp);
        wi.setLeftWhereExp(lwc);
        wi.setRightWhereExp(rwc);
        wi.setOperator("=");
        return wi;
    }
    
    public static boolean checkForCasting(final Vector colExp) {
        boolean needsCasting = true;
        try {
            if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof FunctionCalls) {
                final FunctionCalls fc = colExp.get(0);
                final String functionNameStr = (fc.getFunctionNameAsAString() != null) ? fc.getFunctionNameAsAString().trim().toUpperCase() : "";
                if (!functionNameStr.isEmpty() && functionNameStr.equalsIgnoreCase("CAST") && fc.getFunctionArguments().size() == 2 && fc.getFunctionArguments().get(1) instanceof CharacterClass) {
                    needsCasting = false;
                }
                else if (fc.getFunctionArguments() != null && fc.getFunctionArguments().isEmpty() && functionNameStr.startsWith("CAST(") && (functionNameStr.endsWith("CHAR)") || functionNameStr.endsWith("VARCHAR)") || functionNameStr.endsWith("TEXT)"))) {
                    needsCasting = false;
                }
                else if (!functionNameStr.startsWith("CHAR_LENGTH")) {
                    final Vector list = StringFunctions.getStringFunctionsListForCasting();
                    for (int i = 0; i < list.size(); ++i) {
                        final String functionNameString = list.get(i).toString();
                        if (functionNameStr.startsWith(functionNameString)) {
                            needsCasting = false;
                            break;
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {}
        return needsCasting;
    }
    
    public static Vector castToCharClass(final Vector colExp, final String dataType) {
        Vector newColumnExpr = null;
        if (colExp != null) {
            try {
                if (checkForCasting(colExp)) {
                    final FunctionCalls castFunction = castToCharFunctionCall(colExp, dataType);
                    if (castFunction != null) {
                        newColumnExpr = new Vector();
                        newColumnExpr.add(0, castFunction);
                    }
                }
            }
            catch (final Exception e) {
                newColumnExpr = null;
            }
        }
        return newColumnExpr;
    }
    
    public static FunctionCalls castToCharFunctionCall(final String argument) {
        final Vector colExp = new Vector();
        colExp.add(argument);
        return castToCharFunctionCall(colExp, "CHAR");
    }
    
    public static FunctionCalls castToCharFunctionCall(final Vector colExp, final String dataType) {
        try {
            final cast castFunction = new cast();
            final TableColumn tc1 = new TableColumn();
            final CharacterClass charClass = new CharacterClass();
            tc1.setColumnName("CAST");
            castFunction.setFunctionName(tc1);
            castFunction.setAsDatatype("AS");
            charClass.setDatatypeName(dataType);
            final Vector newFunctionArgs = new Vector();
            final SelectColumn sc = new SelectColumn();
            sc.setColumnExpression(colExp);
            newFunctionArgs.add(0, sc);
            newFunctionArgs.add(1, charClass);
            castFunction.setFunctionArguments(newFunctionArgs);
            return castFunction;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static Vector castToNumericClass(final Vector colExp, final String dataType) {
        Vector newColumnExpr = null;
        if (colExp != null) {
            try {
                final cast castFunction = new cast();
                final TableColumn tc1 = new TableColumn();
                final NumericClass numClass = new NumericClass();
                tc1.setColumnName("CAST");
                castFunction.setFunctionName(tc1);
                castFunction.setAsDatatype("AS");
                numClass.setDatatypeName(dataType);
                final Vector newFunctionArgs = new Vector();
                final SelectColumn sc = new SelectColumn();
                sc.setColumnExpression(colExp);
                newFunctionArgs.add(0, sc);
                newFunctionArgs.add(1, numClass);
                castFunction.setFunctionArguments(newFunctionArgs);
                newColumnExpr = new Vector();
                newColumnExpr.add(0, castFunction);
            }
            catch (final Exception e) {
                newColumnExpr = null;
            }
        }
        return newColumnExpr;
    }
    
    public void handlePositionFunctionArguments(final String functionName, final Vector newFunctionArguments, final String dataType) {
        if (functionName != null && functionName.equalsIgnoreCase("POSITION") && newFunctionArguments != null && newFunctionArguments.size() == 1 && newFunctionArguments.get(0) instanceof SelectColumn && newFunctionArguments.get(0).getColumnExpression().size() == 1 && newFunctionArguments.get(0).getColumnExpression().get(0) instanceof WhereItem) {
            final WhereItem wi = newFunctionArguments.get(0).getColumnExpression().get(0);
            final WhereColumn leftWhereCol = wi.getLeftWhereExp();
            WhereColumn rightWhereCol = wi.getRightWhereExp();
            if (rightWhereCol != null && leftWhereCol != null && rightWhereCol.getColumnExpression() != null && leftWhereCol.getColumnExpression() != null) {
                final Vector leftWhrColExp = castToCharClass(leftWhereCol.getColumnExpression(), dataType);
                if (rightWhereCol.getColumnExpression().size() == 3 && rightWhereCol.getColumnExpression().get(0) instanceof String && rightWhereCol.getColumnExpression().get(1) instanceof WhereColumn && rightWhereCol.getColumnExpression().get(2) instanceof String && rightWhereCol.getColumnExpression().get(0).toString().equals("(") && rightWhereCol.getColumnExpression().get(2).toString().equals(")")) {
                    final WhereColumn rightWhrCol = rightWhereCol = rightWhereCol.getColumnExpression().get(1);
                }
                final Vector rightWhrColExp = castToCharClass(rightWhereCol.getColumnExpression(), dataType);
                if (leftWhrColExp != null) {
                    leftWhereCol.setColumnExpression(leftWhrColExp);
                }
                if (rightWhrColExp != null) {
                    rightWhereCol.setColumnExpression(rightWhrColExp);
                }
            }
        }
    }
    
    public boolean needsCastingForStringLiterals() {
        boolean needsCasting = false;
        try {
            for (int j = 0; j < this.functionArguments.size(); ++j) {
                if (this.functionArguments.elementAt(j) instanceof SelectColumn) {
                    final SelectColumn sc = this.functionArguments.elementAt(j);
                    if (sc.needsCastingForStringLiterals()) {
                        needsCasting = true;
                        break;
                    }
                }
            }
        }
        catch (final Exception ex) {}
        return needsCasting;
    }
    
    public String handleStringLiteralForDateTime(String dateTimeString, final SelectQueryStatement from_sqs) {
        final boolean canHandleStringLiteralsForDateTime = from_sqs != null && from_sqs.canHandleStringLiteralsForDateTime();
        dateTimeString = StringFunctions.convertToAnsiDateLiteral(dateTimeString, canHandleStringLiteralsForDateTime);
        return dateTimeString;
    }
    
    public void handleStringLiteralForDateTime(final SelectQueryStatement from_sqs, final int elementPosition, final boolean castingNeeded) {
        try {
            final boolean canHandleStringLiteralsForDateTime = from_sqs != null && from_sqs.canHandleStringLiteralsForDateTime();
            if ((canHandleStringLiteralsForDateTime || castingNeeded) && this.functionArguments != null && this.functionArguments.size() > elementPosition && this.functionArguments.elementAt(elementPosition).getColumnExpression() != null && this.functionArguments.elementAt(elementPosition).getColumnExpression().size() == 1 && this.functionArguments.elementAt(elementPosition).getColumnExpression().get(0) instanceof String) {
                String dateTimeString = this.functionArguments.elementAt(elementPosition).getColumnExpression().get(0).toString();
                dateTimeString = StringFunctions.convertToAnsiDateLiteral(dateTimeString, canHandleStringLiteralsForDateTime);
                if (castingNeeded) {
                    dateTimeString = "CAST(" + dateTimeString + " AS TIMESTAMP)";
                }
                this.functionArguments.elementAt(elementPosition).getColumnExpression().set(0, dateTimeString);
            }
        }
        catch (final Exception ex) {}
    }
    
    public void handleStringLiteralForDate(final SelectQueryStatement from_sqs, final int elementPosition, final boolean castingNeeded) {
        try {
            final boolean canHandleStringLiteralsForDateTime = from_sqs != null && from_sqs.canHandleStringLiteralsForDateTime();
            if ((canHandleStringLiteralsForDateTime || castingNeeded) && this.functionArguments != null && this.functionArguments.size() > elementPosition && this.functionArguments.elementAt(elementPosition).getColumnExpression() != null && this.functionArguments.elementAt(elementPosition).getColumnExpression().size() == 1 && this.functionArguments.elementAt(elementPosition).getColumnExpression().get(0) instanceof String) {
                String dateTimeString = this.functionArguments.elementAt(elementPosition).getColumnExpression().get(0).toString();
                dateTimeString = StringFunctions.convertToAnsiDateLiteral(dateTimeString, canHandleStringLiteralsForDateTime);
                if (castingNeeded) {
                    dateTimeString = "CAST(" + dateTimeString + " AS DATE)";
                }
                this.functionArguments.elementAt(elementPosition).getColumnExpression().set(0, dateTimeString);
            }
        }
        catch (final Exception ex) {}
    }
    
    public String handleStringLiteralForTime(final String timeString, final SelectQueryStatement from_sqs) {
        return this.handleStringLiteralForTime(timeString, from_sqs, false);
    }
    
    public String handleStringLiteralForTime(String timeString, final SelectQueryStatement from_sqs, final boolean forToChar) {
        final boolean canHandleStringLiteralsForTime = from_sqs != null && from_sqs.canHandleStringLiteralsForDateTime();
        timeString = StringFunctions.convertToAnsiTimeLiteral(timeString, canHandleStringLiteralsForTime, forToChar);
        return timeString;
    }
    
    public void handleStringLiteralForTime(final SelectQueryStatement from_sqs, final int elementPosition, final boolean castingNeeded) {
        this.handleStringLiteralForTime(from_sqs, elementPosition, castingNeeded, false);
    }
    
    public void handleStringLiteralForTime(final SelectQueryStatement from_sqs, final int elementPosition, final boolean castingNeeded, final boolean forToChar) {
        try {
            final boolean canHandleStringLiteralsForDateTime = from_sqs != null && from_sqs.canHandleStringLiteralsForDateTime();
            if ((canHandleStringLiteralsForDateTime || castingNeeded) && this.functionArguments != null && this.functionArguments.size() > elementPosition && this.functionArguments.elementAt(elementPosition).getColumnExpression() != null && this.functionArguments.elementAt(elementPosition).getColumnExpression().size() == 1 && this.functionArguments.elementAt(elementPosition).getColumnExpression().get(0) instanceof String) {
                String timeString = this.functionArguments.elementAt(elementPosition).getColumnExpression().get(0).toString();
                timeString = StringFunctions.convertToAnsiTimeLiteral(timeString, canHandleStringLiteralsForDateTime, forToChar);
                if (castingNeeded) {
                    timeString = "CAST(" + timeString + " AS TIME)";
                }
                this.functionArguments.elementAt(elementPosition).getColumnExpression().set(0, timeString);
            }
        }
        catch (final Exception ex) {}
    }
    
    public static int getWeekStartDayValue(final int startMonth, final int startWeekDay) {
        int weekStartDay = 1;
        if (startMonth > 1) {
            weekStartDay = FunctionCalls.WEEKDAY_MAP[startWeekDay];
        }
        else {
            weekStartDay = FunctionCalls.WEEKSTARTDAY_WEEKDAY_MAP[startWeekDay];
        }
        return weekStartDay;
    }
    
    public void validateExcludeWeekendAsCharArray(String weekendPattern, final String fnStr) throws ConvertException {
        if (weekendPattern.length() == 1 || weekendPattern.contains(",")) {
            weekendPattern = weekendPattern.replaceAll(",", "");
            final char[] sortedWeekendPattern = weekendPattern.toCharArray();
            for (int index = 0; index < sortedWeekendPattern.length; ++index) {
                final int weekValue = sortedWeekendPattern[index] - '0';
                if (weekValue <= 0 || weekValue >= 8) {
                    throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separated by comma" });
                }
            }
            return;
        }
        throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separated by comma" });
    }
    
    public void validateExcludeWeekendAsString(final String weekendPattern, final String fnStr) throws ConvertException {
        for (int index = 0; index < 7; ++index) {
            if (weekendPattern.charAt(index) != '1' && weekendPattern.charAt(index) != '0') {
                throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "EXCLUDE_WEEKENDS", "Provide values as a seven digit string with 0's and 1's" });
            }
        }
    }
    
    public void validateWorkStartAndEndTime(final String timeArg, final String paramName, final String fnStr, final int wst) throws ConvertException {
        if (!timeArg.contains(":")) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, paramName.toUpperCase(), "Provide value in HH:mm:ss format only. WORK_START_TIME of the day should be less than WORK_END_TIME of the day" });
        }
        final String[] arrayStart = timeArg.split(":");
        if (arrayStart.length != 3) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, paramName.toUpperCase(), "Provide value in HH:mm:ss format only. WORK_START_TIME of the day should be less than WORK_END_TIME of the day" });
        }
        final int[] a = new int[3];
        for (int i = 0; i < 3; ++i) {
            try {
                a[i] = Integer.parseInt(arrayStart[i]);
            }
            catch (final Exception e) {
                throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, paramName.toUpperCase(), "Provide only numeric values in HH:mm:ss format" });
            }
        }
        final int workTime = a[0] * 3600 + a[1] * 60 + a[2];
        if (workTime < 0 || workTime > 86400 || workTime < wst) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, paramName.toUpperCase(), "Provide value in HH:mm:ss format only. WORK_START_TIME of the day should be less than WORK_END_TIME of the day" });
        }
    }
    
    public void validateStringLength(final String str_len, final String fnStr) throws ConvertException {
        int len;
        try {
            len = Integer.valueOf(str_len);
        }
        catch (final Exception e) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "STRING_LEN", "Provide only numeric values between 1 to 255" });
        }
        if (len < 1 || len > 255) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "STRING_LEN", "Provide values between 1 to 255" });
        }
    }
    
    public void validateIsWholeValue(final String absoluteValue, final String fnStr) throws ConvertException {
        if (!absoluteValue.equalsIgnoreCase("1") && !absoluteValue.equalsIgnoreCase("0")) {
            throw new ConvertException("Invalid Argument Value for Function MONTHS_BETWEEN", "INVALID_ARGUMENT_VALUE", new Object[] { "MONTHS_BETWEEN", "ISWHOLE_VALUE", "Provide values 0 or 1" });
        }
    }
    
    public void validateFiscalStartMonth(final String fiscalStartMonth_str, final String fnStr) throws ConvertException {
        int fiscalStartMonth;
        try {
            fiscalStartMonth = Integer.parseInt(fiscalStartMonth_str);
        }
        catch (final Exception e) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "FISCAL_START_MONTH", "Provide only numeric values between 1 to 12" });
        }
        if (fiscalStartMonth < 1 || fiscalStartMonth > 12) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "FISCAL_START_MONTH", "Provide values between 1 to 12" });
        }
    }
    
    public void validateWeekMode(final String weekMode, final String fnStr) throws ConvertException {
        if (!weekMode.equalsIgnoreCase("1") && !weekMode.equalsIgnoreCase("2")) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "WEEK_MODE", "Provide values 0 or 1" });
        }
    }
    
    public void validateWeek_Start_Day(final String weekStartDay_str, final String fnStr) throws ConvertException {
        int weekStartDay;
        try {
            weekStartDay = Integer.parseInt(weekStartDay_str);
        }
        catch (final Exception e) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "WEEK_START_DAY", "Provide only numeric values between 1 to 7" });
        }
        if (weekStartDay < 1 || weekStartDay > 7) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "WEEK_START_DAY", "Provide values between 1 to 7" });
        }
    }
    
    public void validateWeekDay(final String weekDay_str, final String fnStr) throws ConvertException {
        int weekDay;
        try {
            weekDay = Integer.parseInt(weekDay_str);
        }
        catch (final Exception e) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "WEEKDAY", "Provide only numeric values between 1 to 7" });
        }
        if (weekDay < 1 || weekDay > 7) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "WEEKDAY", "Provide values between 1 to 7" });
        }
    }
    
    public void validateWeek_StartDay(final String weekStartDay_str, final String fnStr) throws ConvertException {
        if (weekStartDay_str.equalsIgnoreCase("1") && weekStartDay_str.equalsIgnoreCase("0")) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "WEEK_STARTDAY", "Provide values 0 or 1" });
        }
    }
    
    public void validatePercentileRange(final String range_str, final String fnStr) throws ConvertException {
        int range;
        try {
            range = Integer.parseInt(range_str);
        }
        catch (final Exception e) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "RANGE", "Provide only numeric values between 0 to 100" });
        }
        if (range < 0 || range > 100) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[] { fnStr, "RANGE", "Provide values between 0 to 100" });
        }
    }
    
    public void validateAggFunArgsType(final Vector arguments, final String fnStr) throws ConvertException {
        if (arguments.elementAt(0) instanceof SelectColumn) {
            final SelectColumn sc = arguments.elementAt(0);
            final Vector vc = sc.getColumnExpression();
            if (vc.elementAt(0) instanceof WhereExpression || vc.elementAt(0) instanceof WhereItem) {
                throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_EXPRESSION", new Object[] { fnStr, "Provide a valid numeric column or expression or value as argument for the function" });
            }
        }
    }
    
    static {
        FunctionCalls.charToIntName = false;
        FunctionCalls.functionArgsInSingleQuotesToDouble = true;
        WEEKDAY_MAP = new int[8];
        WEEKSTARTDAY_WEEKDAY_MAP = new int[8];
        FunctionCalls.WEEKDAY_MAP[1] = 1;
        FunctionCalls.WEEKDAY_MAP[7] = 2;
        FunctionCalls.WEEKDAY_MAP[6] = 3;
        FunctionCalls.WEEKDAY_MAP[5] = 4;
        FunctionCalls.WEEKDAY_MAP[4] = 5;
        FunctionCalls.WEEKDAY_MAP[3] = 6;
        FunctionCalls.WEEKDAY_MAP[2] = 0;
        FunctionCalls.WEEKSTARTDAY_WEEKDAY_MAP[1] = 4;
        FunctionCalls.WEEKSTARTDAY_WEEKDAY_MAP[7] = 5;
        FunctionCalls.WEEKSTARTDAY_WEEKDAY_MAP[6] = 6;
        FunctionCalls.WEEKSTARTDAY_WEEKDAY_MAP[5] = 0;
        FunctionCalls.WEEKSTARTDAY_WEEKDAY_MAP[4] = 1;
        FunctionCalls.WEEKSTARTDAY_WEEKDAY_MAP[3] = 2;
        FunctionCalls.WEEKSTARTDAY_WEEKDAY_MAP[2] = 3;
    }
}
