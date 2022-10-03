package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.functions.misc.ifnull;
import com.adventnet.swissqlapi.sql.functions.misc.cast;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.SwisSQLAPI;
import java.util.Collection;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import java.util.Vector;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class WhereItem
{
    private String openBraces;
    private String closeBraces;
    private WhereColumn leftWhereExp;
    private WhereColumn rightWhereExp;
    private String LeftJoin;
    private String RightJoin;
    private SelectQueryStatement rightWhereSubQuery;
    private WhereColumn rightWhereSubQueryExp;
    private String beginOperator;
    private UserObjectContext context;
    private boolean movedToFromClause;
    private boolean isContainsFunction;
    private boolean isNullSafeEqualsOperator;
    private String operator1;
    private String operator;
    private String operator2;
    private String operator3;
    private RownumClause rownumClause;
    private ArrayList fromTableList;
    private String stmtTableName;
    private WhereExpression lnnvlWhereExp;
    private CaseStatement casestmtFromLNNVLClause;
    private WhereItem teradataSysCalendarWI;
    private boolean is_Case_Expression;
    private Vector regExp;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    
    public WhereItem() {
        this.context = null;
        this.movedToFromClause = false;
        this.isContainsFunction = false;
        this.isNullSafeEqualsOperator = false;
        this.is_Case_Expression = false;
        this.openBraces = new String("");
        this.closeBraces = new String("");
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setLeftWhereExp(final WhereColumn lwe) {
        this.leftWhereExp = lwe;
        if (this.leftWhereExp != null) {
            this.leftWhereExp.setLHSExpr(true);
        }
    }
    
    public void setMovedToFromClause(final boolean b) {
        this.movedToFromClause = b;
    }
    
    public void setOpenBrace(final String s_ob) {
        this.openBraces = s_ob;
    }
    
    public void setCloseBrace(final String s_cb) {
        this.closeBraces = s_cb;
    }
    
    public void setRightWhereExp(final WhereColumn rwe) {
        this.rightWhereExp = rwe;
    }
    
    public void setOperator(final String opr) {
        this.operator = opr;
    }
    
    public void setOperator1(final String opr) {
        this.operator1 = opr;
    }
    
    public void setOperator2(final String opr) {
        this.operator2 = opr;
    }
    
    public void setOperator3(final String s_opr) {
        this.operator3 = s_opr;
    }
    
    public void setBeginOperator(final String begin) {
        this.beginOperator = begin;
    }
    
    public void setRightWhereSubQuery(final SelectQueryStatement qs) {
        this.rightWhereSubQuery = qs;
    }
    
    public void setRightWhereSubQueryExp(final WhereColumn wc) {
        this.rightWhereSubQueryExp = wc;
    }
    
    public void setFromTableList(final ArrayList fromTableList) {
        this.fromTableList = fromTableList;
    }
    
    public void setRightJoin(final String s_rj) {
        this.RightJoin = s_rj;
    }
    
    public void setLeftJoin(final String s_lj) {
        this.LeftJoin = s_lj;
    }
    
    public void setStmtTableName(final String stmtTableName) {
        this.stmtTableName = stmtTableName;
    }
    
    public void setWhereExpForLNNVL(final WhereExpression we) {
        this.lnnvlWhereExp = we;
    }
    
    public void setCaseStatementForLNNVLFunc(final CaseStatement cs) {
        this.casestmtFromLNNVLClause = cs;
    }
    
    public void setTeradataSysCalendarWhereItem(final WhereItem tdWI) {
        this.teradataSysCalendarWI = tdWI;
    }
    
    public void setCaseExpressionBool(final boolean boolVal) {
        this.is_Case_Expression = boolVal;
    }
    
    public void setRegExp(final Vector exp) {
        this.regExp = exp;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass commentObj) {
        this.commentObjAfterToken = commentObj;
    }
    
    public CaseStatement getCaseStatementForLNNVLFunc() {
        return this.casestmtFromLNNVLClause;
    }
    
    public WhereExpression getWhereExpForLNNVL() {
        return this.lnnvlWhereExp;
    }
    
    public String getRightJoin() {
        return this.RightJoin;
    }
    
    public String getLeftJoin() {
        return this.LeftJoin;
    }
    
    public String getBeginOperator() {
        return this.beginOperator;
    }
    
    public WhereColumn getLeftWhereExp() {
        return this.leftWhereExp;
    }
    
    public WhereColumn getRightWhereExp() {
        return this.rightWhereExp;
    }
    
    public SelectQueryStatement getRightWhereSubQuery() {
        return this.rightWhereSubQuery;
    }
    
    public WhereColumn getRightWhereSubQueryExp() {
        return this.rightWhereSubQueryExp;
    }
    
    public String getOperator() {
        return this.operator;
    }
    
    public String getOperator1() {
        return this.operator1;
    }
    
    public String getOperator2() {
        return this.operator2;
    }
    
    public String getOperator3() {
        return this.operator3;
    }
    
    public void setRownumClause(final RownumClause rc) {
        this.rownumClause = rc;
    }
    
    public String getOpenBrace() {
        return this.openBraces;
    }
    
    public String getCloseBrace() {
        return this.closeBraces;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public CommentClass getCommentClassAfterToken() {
        return this.commentObjAfterToken;
    }
    
    public void removeBraces() {
        if (this.openBraces != null && this.openBraces.length() > 0) {
            this.openBraces = this.openBraces.substring(1);
            if (this.closeBraces != null && this.closeBraces.length() > 0) {
                this.closeBraces = this.closeBraces.substring(1);
            }
        }
    }
    
    public Vector getRegExp() {
        return this.regExp;
    }
    
    public RownumClause getRownumClause() {
        return this.rownumClause;
    }
    
    public boolean getMovedToFromClause() {
        return this.movedToFromClause;
    }
    
    public WhereItem getTeradataSysCalendarWhereItem() {
        return this.teradataSysCalendarWI;
    }
    
    public boolean isNullSafeEqualsOperator() {
        return this.isNullSafeEqualsOperator;
    }
    
    public WhereItem toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        if (this.commentObj != null) {
            wi.setCommentClass(this.commentObj);
        }
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toMySQLSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toMySQLSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toMySQLSelect());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toMySQLSelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("^=")) {
                wi.setOperator("<>");
            }
            else if (this.operator.equalsIgnoreCase("!>")) {
                wi.setOperator("<=");
            }
            else if (this.operator.equalsIgnoreCase("!<")) {
                wi.setOperator(">=");
            }
            else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure..Expressions can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null) {
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_nsce = new Vector();
                final Vector v_sce = wc.getColumnExpression();
                if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                    v_nsce.addElement("(");
                    for (int i = 0; i < v_sce.size(); ++i) {
                        v_nsce.addElement(v_sce.elementAt(i));
                    }
                    v_nsce.addElement(")");
                    wc.setColumnExpression(v_nsce);
                }
                wi.setOperator(this.operator);
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            final WhereColumn r_wc = wi.getRightWhereExp();
            final WhereColumn l_wc = wi.getLeftWhereExp();
            if (r_wc != null && r_wc.getColumnExpression().size() != 1) {
                final Vector v_nlsce = new Vector();
                final Vector v_nrsce = new Vector();
                final Vector v_lsce = l_wc.getColumnExpression();
                final Vector v_rsce = r_wc.getColumnExpression();
                final Vector v_removedcomma_right_column_exp = new Vector();
                final Vector v_removedcomma_left_column_exp = new Vector();
                for (int j = 0; j < v_lsce.size(); ++j) {
                    if (v_lsce.elementAt(j) instanceof String) {
                        final String s_ce2 = v_lsce.elementAt(j);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(j));
                        }
                    }
                    else {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(j));
                    }
                }
                for (int j = 0; j < v_rsce.size(); ++j) {
                    if (v_rsce.elementAt(j) instanceof String) {
                        final String s_ce2 = v_rsce.elementAt(j);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(j));
                        }
                    }
                    else {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(j));
                    }
                }
                v_nlsce.addElement(v_removedcomma_left_column_exp.elementAt(0));
                v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
                l_wc.setColumnExpression(v_nlsce);
                r_wc.setColumnExpression(v_nrsce);
                for (int j = 1; j < v_removedcomma_right_column_exp.size(); ++j) {
                    final WhereItem n_wi = new WhereItem();
                    final WhereColumn l_nwc = new WhereColumn();
                    final WhereColumn r_nwc = new WhereColumn();
                    final Vector v_lnsc = new Vector();
                    final Vector v_rnsc = new Vector();
                    v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                    v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(j));
                    l_nwc.setColumnExpression(v_lnsc);
                    r_nwc.setColumnExpression(v_rnsc);
                    n_wi.setLeftWhereExp(l_nwc);
                    n_wi.setRightWhereExp(r_nwc);
                    n_wi.setOperator(this.operator);
                    if (j == v_lsce.size() - 1) {
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                    else {
                        if (this.operator2.equalsIgnoreCase("ALL")) {
                            from_sqs.getWhereExpression().addOperator("AND");
                        }
                        else {
                            from_sqs.getWhereExpression().addOperator("OR");
                        }
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                }
            }
        }
        if (this.commentObjAfterToken != null) {
            wi.setCommentClassAfterToken(this.commentObjAfterToken);
        }
        if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        if (this.LeftJoin != null) {
            wi.setLeftJoin(null);
        }
        if (this.RightJoin != null) {
            wi.setRightJoin(null);
        }
        return wi;
    }
    
    public boolean convertRownumToLimitClause(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.leftWhereExp != null) {
            final Vector v_ce = this.leftWhereExp.getColumnExpression();
            if (v_ce == null || v_ce.size() != 1 || !(v_ce.elementAt(0) instanceof TableColumn)) {
                return false;
            }
            final TableColumn tc = v_ce.elementAt(0);
            if (!tc.getColumnName().equalsIgnoreCase("ROWNUM")) {
                return false;
            }
        }
        if (this.operator == null || !this.operator.equals("<")) {
            return false;
        }
        if (this.rightWhereExp == null) {
            return false;
        }
        if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
        }
        final LimitClause lc = new LimitClause();
        lc.setLimitClause("LIMIT");
        lc.setLimitValue(this.rightWhereExp.toMySQLSelect(to_sqs, from_sqs).toString());
        to_sqs.setLimitClause(lc);
        return true;
    }
    
    public WhereItem toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        wi.setFromTableList(this.fromTableList);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        if (this.leftWhereExp != null) {
            this.leftWhereExp.setStmtTableName(this.stmtTableName);
            this.leftWhereExp.setFromTableList(this.fromTableList);
            wi.setLeftWhereExp(this.leftWhereExp.toDB2Select(to_sqs, from_sqs));
        }
        if (this.rightWhereExp != null) {
            if (this.leftWhereExp != null) {
                this.rightWhereExp.setTargetDataType(this.leftWhereExp.getSourceDataType());
            }
            this.rightWhereExp.setStmtTableName(this.stmtTableName);
            this.rightWhereExp.setFromTableList(this.fromTableList);
            wi.setRightWhereExp(this.rightWhereExp.toDB2Select(to_sqs, from_sqs));
        }
        if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toDB2Select());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toDB2Select(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            final WhereColumn r_wc = wi.getRightWhereExp();
            final WhereColumn l_wc = wi.getLeftWhereExp();
            if (r_wc.getColumnExpression() != null && r_wc.getColumnExpression().size() != 1) {
                final Vector v_nlsce = new Vector();
                final Vector v_nrsce = new Vector();
                final Vector v_lsce = l_wc.getColumnExpression();
                final Vector v_rsce = r_wc.getColumnExpression();
                final Vector v_removedcomma_right_column_exp = new Vector();
                final Vector v_removedcomma_left_column_exp = new Vector();
                for (int i = 0; i < v_lsce.size(); ++i) {
                    if (v_lsce.elementAt(i) instanceof String) {
                        final String s_ce2 = v_lsce.elementAt(i);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                        }
                    }
                    else {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(i));
                    }
                }
                for (int i = 0; i < v_rsce.size(); ++i) {
                    if (v_rsce.elementAt(i) instanceof String) {
                        final String s_ce2 = v_rsce.elementAt(i);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                        }
                    }
                    else {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(i));
                    }
                }
                v_nlsce.addElement(v_removedcomma_left_column_exp.elementAt(0));
                v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
                l_wc.setColumnExpression(v_nlsce);
                r_wc.setColumnExpression(v_nrsce);
                for (int i = 1; i < v_removedcomma_right_column_exp.size(); ++i) {
                    final WhereItem n_wi = new WhereItem();
                    final WhereColumn l_nwc = new WhereColumn();
                    final WhereColumn r_nwc = new WhereColumn();
                    final Vector v_lnsc = new Vector();
                    final Vector v_rnsc = new Vector();
                    v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                    v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(i));
                    l_nwc.setColumnExpression(v_lnsc);
                    r_nwc.setColumnExpression(v_rnsc);
                    n_wi.setLeftWhereExp(l_nwc);
                    n_wi.setRightWhereExp(r_nwc);
                    n_wi.setOperator(this.operator);
                    if (i == v_lsce.size() - 1) {
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                    else {
                        if (this.operator2.equalsIgnoreCase("ALL")) {
                            from_sqs.getWhereExpression().addOperator("AND");
                        }
                        else {
                            from_sqs.getWhereExpression().addOperator("OR");
                        }
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                }
            }
        }
        if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        if (this.LeftJoin != null) {
            wi.setLeftJoin(null);
        }
        if (this.RightJoin != null) {
            wi.setRightJoin(null);
        }
        boolean change_rightwhere_expression = false;
        String leftside_function_name = "";
        final WhereColumn lwcol = wi.getLeftWhereExp();
        if (lwcol != null) {
            Object obj = null;
            final Vector lwcolexp = lwcol.getColumnExpression();
            if (lwcolexp != null) {
                obj = lwcolexp.get(0);
            }
            if (obj != null && obj instanceof FunctionCalls) {
                final Vector args = ((FunctionCalls)obj).getFunctionArguments();
                final String functionname = ((FunctionCalls)obj).getFunctionNameAsAString();
                if (functionname != null) {
                    if (functionname.equalsIgnoreCase("TIMESTAMP")) {
                        leftside_function_name = "TIMESTAMP";
                        change_rightwhere_expression = true;
                    }
                    else if (functionname.equalsIgnoreCase("DATE")) {
                        leftside_function_name = "DATE";
                        change_rightwhere_expression = true;
                    }
                }
            }
        }
        if (change_rightwhere_expression) {
            final WhereColumn rwcol = wi.getRightWhereExp();
            if (rwcol != null) {
                Object objr = null;
                final Vector rwcolexp = rwcol.getColumnExpression();
                if (rwcolexp != null) {
                    objr = rwcolexp.get(0);
                }
                if (objr != null && objr instanceof FunctionCalls) {
                    final FunctionCalls currentFnCall = (FunctionCalls)objr;
                    final Vector args2 = currentFnCall.getFunctionArguments();
                    final String functionname2 = currentFnCall.getFunctionNameAsAString();
                    if (functionname2 != null && (functionname2.equalsIgnoreCase("CHAR") || functionname2.equalsIgnoreCase("SUBSTR"))) {
                        final Vector newFunctionArgumentsV = new Vector();
                        newFunctionArgumentsV.addElement(currentFnCall);
                        newFunctionArgumentsV.addElement("'00.00.00'");
                        final FunctionCalls newFnCall = new FunctionCalls();
                        final TableColumn tc = new TableColumn();
                        tc.setColumnName("TIMESTAMP");
                        newFnCall.setFunctionName(tc);
                        newFnCall.setFunctionArguments(newFunctionArgumentsV);
                        final Vector newColExpV = new Vector();
                        newColExpV.addElement(newFnCall);
                        rwcol.setColumnExpression(newColExpV);
                        wi.setRightWhereExp(rwcol);
                    }
                }
            }
        }
        final WhereColumn db2LWE = wi.getLeftWhereExp();
        String leftDataType = null;
        if (db2LWE != null) {
            final Vector colExpr = db2LWE.getColumnExpression();
            if (colExpr != null) {
                for (int j = 0; j < colExpr.size(); ++j) {
                    final Object obj2 = colExpr.get(j);
                    if (obj2 instanceof TableColumn) {
                        leftDataType = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)obj2);
                    }
                }
            }
        }
        final WhereColumn db2RWE = wi.getRightWhereExp();
        if (leftDataType != null && (leftDataType.indexOf("int") != -1 || leftDataType.indexOf("num") != -1) && db2RWE != null && db2RWE.getColumnExpression() != null && db2RWE.toString().trim().startsWith("'")) {
            final Vector rColExpr = db2RWE.getColumnExpression();
            if (rColExpr != null && rColExpr.size() == 1) {
                rColExpr.setElementAt(rColExpr.get(0).toString().trim().substring(1, rColExpr.get(0).toString().trim().length() - 1), 0);
            }
        }
        return wi;
    }
    
    public WhereItem toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toANSISelect(to_sqs, from_sqs));
        }
        if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toANSISelect(to_sqs, from_sqs));
        }
        if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toANSISelect());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toANSISelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("^=") || this.operator.equalsIgnoreCase("!=")) {
                wi.setOperator("<>");
            }
            else if (this.operator.equalsIgnoreCase("!>")) {
                wi.setOperator("<=");
            }
            else if (this.operator.equalsIgnoreCase("!<")) {
                wi.setOperator(">=");
            }
            else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null) {
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_nsce = new Vector();
                final Vector v_sce = wc.getColumnExpression();
                if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                    v_nsce.addElement("(");
                    for (int i = 0; i < v_sce.size(); ++i) {
                        v_nsce.addElement(v_sce.elementAt(i));
                    }
                    v_nsce.addElement(")");
                    wc.setColumnExpression(v_nsce);
                }
                wi.setOperator(this.operator);
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        if (this.LeftJoin != null) {
            wi.setLeftJoin(null);
        }
        if (this.RightJoin != null) {
            wi.setRightJoin(null);
        }
        return wi;
    }
    
    public WhereItem toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        if (this.rownumClause != null && !this.is_Case_Expression) {
            to_sqs.setRownumClause(this.rownumClause);
            return null;
        }
        wi.setRownumClause(this.rownumClause);
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toTeradataSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereExp != null) {
            WhereColumn rwe = this.rightWhereExp;
            rwe = rwe.toTeradataSelect(to_sqs, from_sqs);
            wi.setRightWhereExp(rwe);
        }
        if (this.rightWhereSubQuery != null) {
            SelectQueryStatement subQuery = this.rightWhereSubQuery.toTeradataSelect();
            if (subQuery.getWithStatement() != null) {
                SelectQueryStatement.getListOfWithStatements().add(subQuery.getWithStatement());
                final SelectQueryStatement tempSubQuery = subQuery.getWithStatement().getWithSQS();
                subQuery.getWithStatement().setWithSQS(null);
                subQuery = tempSubQuery;
            }
            wi.setRightWhereSubQuery(subQuery);
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toTeradataSelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("^=") || this.operator.equalsIgnoreCase("!=")) {
                wi.setOperator("<>");
            }
            else if (this.operator.equalsIgnoreCase("!>")) {
                wi.setOperator("<=");
            }
            else if (this.operator.equalsIgnoreCase("!<")) {
                wi.setOperator(">=");
            }
            else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null) {
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_nsce = new Vector();
                Vector v_sce = wc.getColumnExpression();
                if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                    v_nsce.addElement("(");
                    for (int i = 0; i < v_sce.size(); ++i) {
                        v_nsce.addElement(v_sce.elementAt(i));
                    }
                    v_nsce.addElement(")");
                    wc.setColumnExpression(v_nsce);
                }
                final Vector newInItems = new Vector();
                final WhereExpression orWhereExp = new WhereExpression();
                final Vector orWhereItems = new Vector();
                v_sce = wc.getColumnExpression();
                final Vector orWhereOperators = new Vector();
                final Vector nonWhereColumnExpr = new Vector();
                for (int k = 0; k < v_sce.size(); ++k) {
                    final Object obj = v_sce.get(k);
                    if (obj instanceof WhereColumn) {
                        final WhereColumn inWhereCol = (WhereColumn)obj;
                        if (inWhereCol.getColumnExpression().get(0) instanceof String) {
                            newInItems.add(inWhereCol);
                            newInItems.add(",");
                        }
                        else {
                            final WhereItem inWhereItem = new WhereItem();
                            inWhereItem.setLeftWhereExp(wi.getLeftWhereExp());
                            inWhereItem.setRightWhereExp(inWhereCol);
                            if (this.operator.equalsIgnoreCase("IN")) {
                                inWhereItem.setOperator("=");
                            }
                            else {
                                inWhereItem.setOperator("!=");
                            }
                            orWhereItems.add(inWhereItem);
                            orWhereOperators.add("OR");
                        }
                    }
                    else if (!(obj instanceof String)) {
                        if (obj != null && obj.toString().startsWith("/*") && obj.toString().endsWith("*/")) {
                            nonWhereColumnExpr.add(obj);
                        }
                        else {
                            nonWhereColumnExpr.add("/*" + obj + "*/");
                        }
                    }
                }
                if (newInItems.size() > 0) {
                    final WhereItem newInWhereItem = new WhereItem();
                    newInWhereItem.setLeftWhereExp(wi.getLeftWhereExp());
                    final WhereColumn newInWhereRightColumn = new WhereColumn();
                    final Vector newInWhereRightColumnExpr = new Vector();
                    newInWhereRightColumnExpr.add("(");
                    newInWhereRightColumnExpr.addAll(newInItems.subList(0, newInItems.size() - 1));
                    newInWhereRightColumnExpr.add(")");
                    newInWhereRightColumn.setColumnExpression(newInWhereRightColumnExpr);
                    newInWhereItem.setRightWhereExp(newInWhereRightColumn);
                    newInWhereItem.setOperator(this.operator);
                    orWhereItems.add(newInWhereItem);
                    orWhereOperators.add("OR");
                }
                if (orWhereItems.size() > 0) {
                    orWhereExp.setWhereItem(orWhereItems);
                    if (orWhereOperators.size() > 0) {
                        orWhereExp.setOperator(new Vector(orWhereOperators.subList(0, orWhereOperators.size() - 1)));
                    }
                    final WhereColumn newInLeftCol = new WhereColumn();
                    final Vector newInLeftColExp = new Vector();
                    newInLeftColExp.add(orWhereExp);
                    newInLeftColExp.addAll(nonWhereColumnExpr);
                    newInLeftCol.setColumnExpression(newInLeftColExp);
                    if (orWhereItems.size() > 1) {
                        newInLeftCol.setOpenBrace("(");
                        newInLeftCol.setCloseBrace(")");
                    }
                    wi.setLeftWhereExp(newInLeftCol);
                    wi.setRightWhereExp(null);
                    wi.setOperator(null);
                }
                else {
                    wi.setOperator(this.operator);
                }
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        if (this.LeftJoin != null) {
            wi.setLeftJoin(null);
        }
        if (this.RightJoin != null) {
            wi.setRightJoin(null);
        }
        return wi;
    }
    
    public WhereItem toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(false);
        this.modifyWhereItemsForEqualAndNotEqualsOperationWithEmptyStringOnRHS();
        if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setReportsMeta(from_sqs != null && from_sqs.getReportsMeta());
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toPostgreSQLSelect());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toPostgreSQLSelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            this.modifyWhereExpForLikeOperator(wi, this.operator, "TEXT");
            if (this.operator.equalsIgnoreCase("^=")) {
                wi.setOperator("<>");
            }
            else if (this.operator.equalsIgnoreCase("!>")) {
                wi.setOperator("<=");
            }
            else if (this.operator.equalsIgnoreCase("!<")) {
                wi.setOperator(">=");
            }
            else if (this.operator.equalsIgnoreCase("<=>")) {
                if (from_sqs != null && from_sqs.isAmazonRedShift()) {
                    wi.setOperator("=");
                    wi.isNullSafeEqualsOperator = true;
                }
                else {
                    wi.setOperator("IS NOT DISTINCT FROM");
                }
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null) {
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_nsce = new Vector();
                final Vector v_sce = wc.getColumnExpression();
                if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                    v_nsce.addElement("(");
                    for (int i = 0; i < v_sce.size(); ++i) {
                        v_nsce.addElement(v_sce.elementAt(i));
                    }
                    v_nsce.addElement(")");
                    wc.setColumnExpression(v_nsce);
                }
                wi.setOperator(this.operator);
            }
            else if (this.operator.trim().equalsIgnoreCase("LNNVl") && this.lnnvlWhereExp != null) {
                final WhereExpression we1 = this.lnnvlWhereExp.toPostgreSQLSelect(to_sqs, from_sqs);
                this.convertLNNVLtoStatement(we1);
                wi.setCaseStatementForLNNVLFunc(this.casestmtFromLNNVLClause);
            }
            else if (this.operator.trim().equalsIgnoreCase("REGEXP_LIKE") && this.regExp != null) {
                wi.setOperator("REGEXP_LIKE");
                final Vector newExp = new Vector();
                for (int j = 0; j < this.regExp.size(); ++j) {
                    if (this.regExp.get(j) instanceof SelectColumn) {
                        final SelectColumn sc1 = this.regExp.get(j);
                        newExp.add(sc1.toPostgreSQLSelect(to_sqs, from_sqs));
                    }
                    else {
                        newExp.add(this.regExp.get(j));
                    }
                }
                wi.setRegExp(newExp);
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            final WhereColumn r_wc = wi.getRightWhereExp();
            final WhereColumn l_wc = wi.getLeftWhereExp();
            if (r_wc.getColumnExpression() != null && r_wc.getColumnExpression().size() != 1) {
                final Vector v_nlsce = new Vector();
                final Vector v_nrsce = new Vector();
                final Vector v_lsce = l_wc.getColumnExpression();
                final Vector v_rsce = r_wc.getColumnExpression();
                final Vector v_removedcomma_right_column_exp = new Vector();
                final Vector v_removedcomma_left_column_exp = new Vector();
                for (int k = 0; k < v_lsce.size(); ++k) {
                    if (v_lsce.elementAt(k) instanceof String) {
                        final String s_ce = v_lsce.elementAt(k);
                        if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                            v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(k));
                        }
                    }
                    else {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(k));
                    }
                }
                for (int k = 0; k < v_rsce.size(); ++k) {
                    if (v_rsce.elementAt(k) instanceof String) {
                        final String s_ce = v_rsce.elementAt(k);
                        if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                            v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(k));
                        }
                    }
                    else {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(k));
                    }
                }
                v_nlsce.addElement(v_removedcomma_left_column_exp.elementAt(0));
                v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
                l_wc.setColumnExpression(v_nlsce);
                r_wc.setColumnExpression(v_nrsce);
                for (int k = 1; k < v_removedcomma_right_column_exp.size(); ++k) {
                    final WhereItem n_wi = new WhereItem();
                    final WhereColumn l_nwc = new WhereColumn();
                    final WhereColumn r_nwc = new WhereColumn();
                    final Vector v_lnsc = new Vector();
                    final Vector v_rnsc = new Vector();
                    v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                    v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(k));
                    l_nwc.setColumnExpression(v_lnsc);
                    r_nwc.setColumnExpression(v_rnsc);
                    n_wi.setLeftWhereExp(l_nwc);
                    n_wi.setRightWhereExp(r_nwc);
                    n_wi.setOperator(this.operator);
                    if (k == v_lsce.size() - 1) {
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                    else {
                        if (this.operator2.equalsIgnoreCase("ALL")) {
                            from_sqs.getWhereExpression().addOperator("AND");
                        }
                        else {
                            from_sqs.getWhereExpression().addOperator("OR");
                        }
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                }
            }
        }
        if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        if (this.LeftJoin != null) {
            wi.setLeftJoin(null);
        }
        if (this.RightJoin != null) {
            wi.setRightJoin(null);
        }
        return wi;
    }
    
    public WhereItem toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        boolean noChangeRequired = false;
        final WhereItem wi = new WhereItem();
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toMSSQLServerSelect(to_sqs, from_sqs));
            final Vector leftColumnExpression = this.leftWhereExp.getColumnExpression();
            if (leftColumnExpression != null && this.containsColumnVariable(leftColumnExpression, false)) {
                noChangeRequired = true;
            }
        }
        if (this.rightWhereExp != null && !this.isContainsFunction) {
            wi.setRightWhereExp(this.rightWhereExp.toMSSQLServerSelect(to_sqs, from_sqs));
            final Vector rightColumnExpression = this.rightWhereExp.getColumnExpression();
            if (rightColumnExpression != null && this.containsColumnVariable(rightColumnExpression, false)) {
                noChangeRequired = true;
            }
        }
        if (!SwisSQLAPI.MSSQLSERVER_THETA) {
            if (this.LeftJoin != null) {
                wi.setLeftJoin(null);
            }
            if (this.RightJoin != null) {
                wi.setLeftJoin(null);
            }
        }
        else if (!noChangeRequired) {
            if (this.LeftJoin != null) {
                if (this.LeftJoin.equalsIgnoreCase("+")) {
                    wi.setLeftJoin("*");
                }
                else {
                    wi.setLeftJoin(this.LeftJoin);
                }
            }
            if (this.RightJoin != null) {
                if (this.RightJoin.equalsIgnoreCase("+")) {
                    wi.setRightJoin("*");
                }
                else {
                    wi.setRightJoin(this.RightJoin);
                }
            }
        }
        if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toMSSQLServerSelect());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toMSSQLServerSelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null && !this.isContainsFunction) {
            if (this.operator.equalsIgnoreCase("^=")) {
                wi.setOperator("<>");
            }
            else if (this.operator.equalsIgnoreCase("<=>")) {
                wi.setOperator("=");
                wi.isNullSafeEqualsOperator = true;
            }
            else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else if ((this.operator.equalsIgnoreCase("BETWEEN") || this.operator.equalsIgnoreCase("NOT BETWEEN")) && wi.getRightWhereExp() != null) {
                final WhereColumn rightSideColumn = wi.getRightWhereExp();
                final Vector rightExpr = rightSideColumn.getColumnExpression();
                if (rightExpr != null) {
                    final Vector newRightExpr = new Vector();
                    for (int i = 0; i < rightExpr.size(); ++i) {
                        if (rightExpr.get(i) instanceof String) {
                            final String rightString = rightExpr.get(i);
                            if (!rightString.equalsIgnoreCase("(")) {
                                if (!rightString.equalsIgnoreCase(")")) {
                                    newRightExpr.add(rightString);
                                }
                            }
                        }
                        else {
                            newRightExpr.add(rightExpr.get(i));
                        }
                    }
                    rightSideColumn.setColumnExpression(newRightExpr);
                }
                wi.setOperator(this.operator);
            }
            else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
                final WhereColumn r_wc = wi.getRightWhereExp();
                final WhereColumn l_wc = wi.getLeftWhereExp();
                final WhereColumn wc2 = wi.getRightWhereExp();
                final Vector v_nsce = new Vector();
                final Vector v_sce = wc2.getColumnExpression();
                if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                    v_nsce.addElement("(");
                    for (int j = 0; j < v_sce.size(); ++j) {
                        v_nsce.addElement(v_sce.elementAt(j));
                    }
                    v_nsce.addElement(")");
                    wc2.setColumnExpression(v_nsce);
                }
                wi.setOperator(this.operator);
            }
            else if ((this.operator.equalsIgnoreCase("IN") || this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereSubQuery() != null) {
                String f_tablename = null;
                if (from_sqs != null) {
                    FromTable ft = null;
                    if (from_sqs.getFromClause() != null && from_sqs.getFromClause().getFromItemList().elementAt(0) instanceof FromTable) {
                        ft = from_sqs.getFromClause().getFromItemList().elementAt(0);
                        if (ft.getAliasName() != null) {
                            f_tablename = ft.getAliasName();
                        }
                        else if (ft.getTableName() instanceof String) {
                            f_tablename = ft.getTableName().toString();
                            int pos = f_tablename.indexOf(".");
                            if (pos == -1) {
                                pos = f_tablename.indexOf("..");
                            }
                            f_tablename = f_tablename.substring(pos + 1);
                        }
                        else {
                            f_tablename = ft.getTableName().toString();
                        }
                    }
                }
                final WhereColumn l_wc = wi.getLeftWhereExp();
                if (l_wc.getColumnExpression() != null && l_wc.getColumnExpression().size() != 1 && l_wc.getColumnExpression().contains(",")) {
                    final Vector v_removedcomma_left_column_exp = new Vector();
                    this.removeCommaBracesFromExpressionList(l_wc, v_removedcomma_left_column_exp, f_tablename);
                    wi.setLeftWhereExp(null);
                    if (this.operator.equalsIgnoreCase("IN")) {
                        wi.setOperator("EXISTS");
                    }
                    else if (this.operator.equalsIgnoreCase("NOT IN")) {
                        wi.setOperator("NOT EXISTS");
                    }
                    final SelectQueryStatement sqs = wi.getRightWhereSubQuery();
                    String f_tablename_1 = null;
                    final FromTable ft_1 = sqs.getFromClause().getFromItemList().elementAt(0);
                    if (ft_1.getAliasName() == null) {}
                    if (ft_1.getAliasName() != null) {
                        f_tablename_1 = ft_1.getAliasName();
                    }
                    else {
                        f_tablename_1 = ft_1.getTableName().toString();
                    }
                    final Vector v_sitl = sqs.getSelectStatement().getSelectItemList();
                    for (int k = 0; k < v_sitl.size(); ++k) {
                        final WhereItem wi_1 = new WhereItem();
                        final WhereColumn l_wc_1 = new WhereColumn();
                        final WhereColumn r_wc_1 = new WhereColumn();
                        final Vector v_lce = new Vector();
                        final Vector v_rce = new Vector();
                        final SelectColumn sc = v_sitl.elementAt(k);
                        final Vector v_ce2 = sc.getColumnExpression();
                        String where_column_name = null;
                        if (v_ce2.elementAt(0) instanceof String) {
                            where_column_name = v_ce2.elementAt(0);
                        }
                        else if (v_ce2.elementAt(0) instanceof TableColumn) {
                            final TableColumn tc = v_ce2.elementAt(0);
                            if (tc.getTableName() == null) {
                                tc.setTableName(f_tablename_1);
                            }
                            where_column_name = tc.toString();
                        }
                        else if (v_ce2.elementAt(0) instanceof SelectColumn) {
                            final SelectColumn selectColumnInsideSelectColumn = v_ce2.elementAt(0);
                            final Vector newColumnExp = selectColumnInsideSelectColumn.getColumnExpression();
                            if (newColumnExp.elementAt(0) instanceof String) {
                                where_column_name = newColumnExp.elementAt(0);
                            }
                            else if (newColumnExp.elementAt(0) instanceof TableColumn) {
                                final TableColumn tableColumn = newColumnExp.elementAt(0);
                                if (tableColumn.getTableName() == null) {
                                    tableColumn.setTableName(f_tablename_1);
                                }
                                where_column_name = tableColumn.toString();
                            }
                        }
                        v_lce.addElement(where_column_name);
                        l_wc_1.setColumnExpression(v_lce);
                        v_rce.addElement(v_removedcomma_left_column_exp.elementAt(k));
                        r_wc_1.setColumnExpression(v_rce);
                        wi_1.setOperator("=");
                        wi_1.setLeftWhereExp(l_wc_1);
                        wi_1.setRightWhereExp(r_wc_1);
                        if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().equalsIgnoreCase("")) {
                            sqs.getWhereExpression().addWhereItem(wi_1);
                            sqs.getWhereExpression().addOperator("AND");
                        }
                        else {
                            final WhereExpression we = new WhereExpression();
                            we.addWhereItem(wi_1);
                            sqs.setWhereExpression(we);
                        }
                    }
                    wi.setRightWhereSubQuery(sqs);
                }
                else {
                    wi.setOperator(this.operator);
                }
            }
            else if (this.getOperator().trim().equals("=") || this.getOperator().trim().equals("!=") || this.getOperator().trim().equals("<>")) {
                final Vector rightColumnExpression = this.rightWhereExp.getColumnExpression();
                if (rightColumnExpression != null && rightColumnExpression.size() == 1) {
                    final Object obj = rightColumnExpression.get(0);
                    if (obj instanceof String && obj.toString().trim().equalsIgnoreCase("null")) {
                        String op = this.getOperator();
                        if (op != null && op.equals("=")) {
                            op = " IS ";
                        }
                        if (op != null && (op.equals("!=") || op.equals("<>"))) {
                            op = " IS NOT ";
                        }
                        wi.setOperator(op);
                    }
                    else {
                        wi.setOperator(this.operator);
                    }
                }
                else {
                    wi.setOperator(this.operator);
                }
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            final WhereColumn r_wc = wi.getRightWhereExp();
            final WhereColumn l_wc = wi.getLeftWhereExp();
            if (r_wc.getColumnExpression() != null && r_wc.getColumnExpression().size() != 1) {
                final Vector v_nlsce = new Vector();
                final Vector v_nrsce = new Vector();
                final Vector v_lsce = l_wc.getColumnExpression();
                final Vector v_rsce = r_wc.getColumnExpression();
                final Vector v_removedcomma_right_column_exp = new Vector();
                final Vector v_removedcomma_left_column_exp2 = new Vector();
                for (int l = 0; l < v_lsce.size(); ++l) {
                    if (v_lsce.elementAt(l) instanceof String) {
                        final String s_ce2 = v_lsce.elementAt(l);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_left_column_exp2.addElement(v_lsce.elementAt(l));
                        }
                    }
                    else {
                        v_removedcomma_left_column_exp2.addElement(v_lsce.elementAt(l));
                    }
                }
                for (int l = 0; l < v_rsce.size(); ++l) {
                    if (v_rsce.elementAt(l) instanceof String) {
                        final String s_ce2 = v_rsce.elementAt(l);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(l));
                        }
                    }
                    else {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(l));
                    }
                }
                v_nlsce.addElement(v_removedcomma_left_column_exp2.elementAt(0));
                v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
                l_wc.setColumnExpression(v_nlsce);
                r_wc.setColumnExpression(v_nrsce);
                for (int l = 1; l < v_removedcomma_right_column_exp.size(); ++l) {
                    final WhereItem n_wi = new WhereItem();
                    final WhereColumn l_nwc = new WhereColumn();
                    final WhereColumn r_nwc = new WhereColumn();
                    final Vector v_lnsc = new Vector();
                    final Vector v_rnsc = new Vector();
                    v_lnsc.addElement(v_removedcomma_left_column_exp2.elementAt(0));
                    v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(l));
                    l_nwc.setColumnExpression(v_lnsc);
                    r_nwc.setColumnExpression(v_rnsc);
                    n_wi.setLeftWhereExp(l_nwc);
                    n_wi.setRightWhereExp(r_nwc);
                    n_wi.setOperator(this.operator);
                    if (l == v_lsce.size() - 1) {
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                    else {
                        if (this.operator2.equalsIgnoreCase("ALL")) {
                            from_sqs.getWhereExpression().addOperator("AND");
                        }
                        else {
                            from_sqs.getWhereExpression().addOperator("OR");
                        }
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                }
            }
        }
        if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        if (this.isContainsFunction && (this.getOperator().trim().equals("=") || this.getOperator().trim().equals("<"))) {
            wi.setOperator1("NOT");
        }
        return wi;
    }
    
    public WhereItem toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        boolean noChangeRequired = false;
        final WhereItem wi = new WhereItem();
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        if (this.leftWhereExp != null) {
            this.leftWhereExp.setObjectContext(this.context);
            final Vector colExp = this.leftWhereExp.getColumnExpression();
            if (colExp != null) {
                for (int i = 0; i < colExp.size(); ++i) {
                    if (colExp.elementAt(i) instanceof TableColumn) {
                        colExp.elementAt(i).setObjectContext(this.context);
                    }
                    else if (colExp.elementAt(i) instanceof SelectColumn) {
                        colExp.elementAt(i).setObjectContext(this.context);
                    }
                    else if (colExp.elementAt(i) instanceof FunctionCalls) {
                        colExp.elementAt(i).setObjectContext(this.context);
                    }
                }
            }
            wi.setObjectContext(this.context);
            wi.setLeftWhereExp(this.leftWhereExp.toSybaseSelect(to_sqs, from_sqs));
            final Vector leftColumnExpression = this.leftWhereExp.getColumnExpression();
            if (leftColumnExpression != null && this.containsColumnVariable(leftColumnExpression, false)) {
                noChangeRequired = true;
            }
        }
        if (this.rightWhereExp != null) {
            this.rightWhereExp.setObjectContext(this.context);
            wi.setObjectContext(this.context);
            final Vector colExp = this.rightWhereExp.getColumnExpression();
            if (colExp != null) {
                for (int i = 0; i < colExp.size(); ++i) {
                    if (colExp.elementAt(i) instanceof TableColumn) {
                        colExp.elementAt(i).setObjectContext(this.context);
                    }
                    else if (colExp.elementAt(i) instanceof SelectColumn) {
                        colExp.elementAt(i).setObjectContext(this.context);
                    }
                    else if (colExp.elementAt(i) instanceof FunctionCalls) {
                        colExp.elementAt(i).setObjectContext(this.context);
                    }
                }
            }
            wi.setRightWhereExp(this.rightWhereExp.toSybaseSelect(to_sqs, from_sqs));
            final Vector rightColumnExpression = this.rightWhereExp.getColumnExpression();
            final int countVarOrConst = 0;
            if (rightColumnExpression != null && this.containsColumnVariable(rightColumnExpression, false)) {
                noChangeRequired = true;
            }
        }
        if (!SwisSQLAPI.MSSQLSERVER_THETA) {
            if (this.LeftJoin != null) {
                wi.setLeftJoin(null);
            }
            if (this.RightJoin != null) {
                wi.setLeftJoin(null);
            }
        }
        else if (!noChangeRequired) {
            if (this.LeftJoin != null) {
                if (this.LeftJoin.equalsIgnoreCase("+")) {
                    wi.setLeftJoin("*");
                }
                else {
                    wi.setLeftJoin(this.LeftJoin);
                }
            }
            if (this.RightJoin != null) {
                if (this.RightJoin.equalsIgnoreCase("+")) {
                    wi.setRightJoin("*");
                }
                else {
                    wi.setRightJoin(this.RightJoin);
                }
            }
        }
        if (this.rightWhereSubQuery != null) {
            this.rightWhereSubQuery.setObjectContext(this.context);
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toSybaseSelect());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toSybaseSelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("^=")) {
                wi.setOperator("<>");
            }
            else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if ((this.operator.equalsIgnoreCase("BETWEEN") || this.operator.equalsIgnoreCase("NOT BETWEEN")) && wi.getRightWhereExp() != null) {
                final WhereColumn rightSideColumn = wi.getRightWhereExp();
                final Vector rightExpr = rightSideColumn.getColumnExpression();
                if (rightExpr != null) {
                    final Vector newRightExpr = new Vector();
                    for (int j = 0; j < rightExpr.size(); ++j) {
                        if (rightExpr.get(j) instanceof String) {
                            final String rightString = rightExpr.get(j);
                            if (!rightString.equalsIgnoreCase("(")) {
                                if (!rightString.equalsIgnoreCase(")")) {
                                    newRightExpr.add(rightString);
                                }
                            }
                        }
                        else {
                            newRightExpr.add(rightExpr.get(j));
                        }
                    }
                    rightSideColumn.setColumnExpression(newRightExpr);
                }
                wi.setOperator(this.operator);
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
                final WhereColumn r_wc = wi.getRightWhereExp();
                final WhereColumn l_wc = wi.getLeftWhereExp();
                final WhereColumn wc2 = wi.getRightWhereExp();
                final Vector v_nsce = new Vector();
                final Vector v_sce = wc2.getColumnExpression();
                if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                    v_nsce.addElement("(");
                    for (int k = 0; k < v_sce.size(); ++k) {
                        v_nsce.addElement(v_sce.elementAt(k));
                    }
                    v_nsce.addElement(")");
                    wc2.setColumnExpression(v_nsce);
                }
                wi.setOperator(this.operator);
            }
            else if ((this.operator.equalsIgnoreCase("IN") || this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereSubQuery() != null) {
                String f_tablename = null;
                if (from_sqs != null) {
                    FromTable ft = null;
                    if (from_sqs.getFromClause().getFromItemList().elementAt(0) instanceof FromTable) {
                        ft = from_sqs.getFromClause().getFromItemList().elementAt(0);
                        if (ft.getAliasName() != null) {
                            f_tablename = ft.getAliasName();
                        }
                        else if (ft.getTableName() instanceof String) {
                            f_tablename = ft.getTableName().toString();
                            int pos = f_tablename.indexOf(".");
                            if (pos == -1) {
                                pos = f_tablename.indexOf("..");
                            }
                            f_tablename = f_tablename.substring(pos + 1);
                        }
                        else {
                            f_tablename = ft.getTableName().toString();
                        }
                    }
                }
                final WhereColumn l_wc = wi.getLeftWhereExp();
                if (l_wc.getColumnExpression() != null && l_wc.getColumnExpression().size() != 1 && l_wc.getColumnExpression().contains(",")) {
                    final Vector v_removedcomma_left_column_exp = new Vector();
                    this.removeCommaBracesFromExpressionList(l_wc, v_removedcomma_left_column_exp, f_tablename);
                    wi.setLeftWhereExp(null);
                    if (this.operator.equalsIgnoreCase("IN")) {
                        wi.setOperator("EXISTS");
                    }
                    else if (this.operator.equalsIgnoreCase("NOT IN")) {
                        wi.setOperator("NOT EXISTS");
                    }
                    final SelectQueryStatement sqs = wi.getRightWhereSubQuery();
                    String f_tablename_1 = null;
                    final FromTable ft_1 = sqs.getFromClause().getFromItemList().elementAt(0);
                    if (ft_1.getAliasName() == null) {}
                    if (ft_1.getAliasName() != null) {
                        f_tablename_1 = ft_1.getAliasName();
                    }
                    else {
                        f_tablename_1 = ft_1.getTableName().toString();
                    }
                    final Vector v_sitl = sqs.getSelectStatement().getSelectItemList();
                    for (int l = 0; l < v_sitl.size(); ++l) {
                        final WhereItem wi_1 = new WhereItem();
                        wi_1.setObjectContext(this.context);
                        final WhereColumn l_wc_1 = new WhereColumn();
                        final WhereColumn r_wc_1 = new WhereColumn();
                        final Vector v_lce = new Vector();
                        final Vector v_rce = new Vector();
                        final SelectColumn sc = v_sitl.elementAt(l);
                        final Vector v_ce2 = sc.getColumnExpression();
                        String where_column_name = null;
                        if (v_ce2.elementAt(0) instanceof String) {
                            where_column_name = v_ce2.elementAt(0);
                        }
                        else if (v_ce2.elementAt(0) instanceof TableColumn) {
                            final TableColumn tc = v_ce2.elementAt(0);
                            if (tc.getTableName() == null) {
                                tc.setTableName(f_tablename_1);
                            }
                            where_column_name = tc.toString();
                        }
                        else if (v_ce2.elementAt(0) instanceof SelectColumn) {
                            final SelectColumn selectColumnInsideSelectColumn = v_ce2.elementAt(0);
                            final Vector newColumnExp = selectColumnInsideSelectColumn.getColumnExpression();
                            if (newColumnExp.elementAt(0) instanceof String) {
                                where_column_name = newColumnExp.elementAt(0);
                            }
                            else if (newColumnExp.elementAt(0) instanceof TableColumn) {
                                final TableColumn tableColumn = newColumnExp.elementAt(0);
                                if (tableColumn.getTableName() == null) {
                                    tableColumn.setTableName(f_tablename_1);
                                }
                                where_column_name = tableColumn.toString();
                            }
                        }
                        v_lce.addElement(where_column_name);
                        l_wc_1.setColumnExpression(v_lce);
                        v_rce.addElement(v_removedcomma_left_column_exp.elementAt(l));
                        r_wc_1.setColumnExpression(v_rce);
                        wi_1.setOperator("=");
                        wi_1.setLeftWhereExp(l_wc_1);
                        wi_1.setRightWhereExp(r_wc_1);
                        if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().equalsIgnoreCase("")) {
                            sqs.getWhereExpression().addWhereItem(wi_1);
                            sqs.getWhereExpression().addOperator("AND");
                        }
                        else {
                            final WhereExpression we = new WhereExpression();
                            we.addWhereItem(wi_1);
                            sqs.setWhereExpression(we);
                        }
                    }
                    final Vector tempVector = new Vector();
                    tempVector.add("*");
                    sqs.getSelectStatement().setSelectItemList(tempVector);
                    wi.setRightWhereSubQuery(sqs);
                }
                else {
                    wi.setOperator(this.operator);
                }
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            final WhereColumn r_wc = wi.getRightWhereExp();
            final WhereColumn l_wc = wi.getLeftWhereExp();
            if (r_wc.getColumnExpression() != null && r_wc.getColumnExpression().size() != 1) {
                final Vector v_nlsce = new Vector();
                final Vector v_nrsce = new Vector();
                final Vector v_lsce = l_wc.getColumnExpression();
                final Vector v_rsce = r_wc.getColumnExpression();
                final Vector v_removedcomma_right_column_exp = new Vector();
                final Vector v_removedcomma_left_column_exp2 = new Vector();
                for (int m = 0; m < v_lsce.size(); ++m) {
                    if (v_lsce.elementAt(m) instanceof String) {
                        final String s_ce2 = v_lsce.elementAt(m);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_left_column_exp2.addElement(v_lsce.elementAt(m));
                        }
                    }
                    else {
                        v_removedcomma_left_column_exp2.addElement(v_lsce.elementAt(m));
                    }
                }
                for (int m = 0; m < v_rsce.size(); ++m) {
                    if (v_rsce.elementAt(m) instanceof String) {
                        final String s_ce2 = v_rsce.elementAt(m);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(m));
                        }
                    }
                    else {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(m));
                    }
                }
                v_nlsce.addElement(v_removedcomma_left_column_exp2.elementAt(0));
                v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
                l_wc.setColumnExpression(v_nlsce);
                r_wc.setColumnExpression(v_nrsce);
                for (int m = 1; m < v_removedcomma_right_column_exp.size(); ++m) {
                    final WhereItem n_wi = new WhereItem();
                    final WhereColumn l_nwc = new WhereColumn();
                    final WhereColumn r_nwc = new WhereColumn();
                    final Vector v_lnsc = new Vector();
                    final Vector v_rnsc = new Vector();
                    v_lnsc.addElement(v_removedcomma_left_column_exp2.elementAt(0));
                    v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(m));
                    l_nwc.setColumnExpression(v_lnsc);
                    r_nwc.setColumnExpression(v_rnsc);
                    n_wi.setLeftWhereExp(l_nwc);
                    n_wi.setRightWhereExp(r_nwc);
                    n_wi.setOperator(this.operator);
                    if (m == v_lsce.size() - 1) {
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                    else {
                        if (this.operator2.equalsIgnoreCase("ALL")) {
                            from_sqs.getWhereExpression().addOperator("AND");
                        }
                        else {
                            from_sqs.getWhereExpression().addOperator("OR");
                        }
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                }
            }
        }
        if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        return wi;
    }
    
    public void removeCommaBracesFromExpressionList(final WhereColumn wc, final Vector v_nce, final String table_name) {
        final Vector v_ce = wc.getColumnExpression();
        if (v_ce != null) {
            for (int i = 0; i < v_ce.size(); ++i) {
                if (v_ce.elementAt(i) instanceof String) {
                    final String s_ce = v_ce.elementAt(i);
                    if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                        v_nce.addElement(v_ce.elementAt(i));
                    }
                }
                else if (v_ce.elementAt(i) instanceof TableColumn) {
                    final TableColumn tc = v_ce.elementAt(i);
                    if (tc.getTableName() == null) {
                        tc.setTableName(table_name);
                    }
                    v_nce.addElement(tc);
                }
                else if (v_ce.elementAt(i) instanceof WhereColumn) {
                    this.removeCommaBracesFromExpressionList(v_ce.elementAt(i), v_nce, table_name);
                }
            }
        }
    }
    
    public WhereItem toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        if (SwisSQLAPI.convert_OracleThetaJOIN_To_ANSIJOIN) {
            wi.setOpenBrace(this.openBraces);
            wi.setCloseBrace(this.closeBraces);
            if (this.beginOperator != null) {
                wi.setBeginOperator(this.beginOperator);
            }
            if (this.operator1 != null) {
                wi.setOperator1(this.operator1);
            }
            if (this.leftWhereExp != null) {
                wi.setLeftWhereExp(this.leftWhereExp.toOracleSelect(to_sqs, from_sqs));
            }
            if (this.rightWhereExp != null) {
                wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
            }
            if (this.rightWhereSubQuery != null) {
                wi.setRightWhereSubQuery(this.rightWhereSubQuery.toOracleSelect());
                if (this.rightWhereSubQueryExp != null) {
                    wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toOracleSelect(to_sqs, from_sqs));
                }
            }
            if (this.operator != null) {
                if (this.operator.equalsIgnoreCase("^=") || this.operator.equalsIgnoreCase("!=")) {
                    wi.setOperator("<>");
                }
                else if (this.operator.equalsIgnoreCase("!>")) {
                    wi.setOperator("<=");
                }
                else if (this.operator.equalsIgnoreCase("!<")) {
                    wi.setOperator(">=");
                }
                else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                    wi.setOperator("LIKE");
                    if (wi.getRightWhereSubQuery() != null) {
                        throw new ConvertException("Conversion failure.. Subquery can't be converted");
                    }
                    final WhereColumn wc = wi.getRightWhereExp();
                    final Vector v_ce = wc.getColumnExpression();
                    final Vector v_nce = new Vector();
                    if (v_ce != null) {
                        if (v_ce.size() != 1) {
                            throw new ConvertException("Conversion failure.. Expressions can't be converted");
                        }
                        String s_ce = v_ce.elementAt(0).toString();
                        if (s_ce.charAt(0) == '\'') {
                            s_ce = s_ce.replace('\'', ' ').trim();
                            s_ce = "'%" + s_ce + "%'";
                        }
                        else if (s_ce.charAt(0) == '\"') {
                            s_ce = s_ce.replace('\"', ' ').trim();
                            s_ce = "\"%" + s_ce + "%\"";
                        }
                        else {
                            s_ce = "'%" + s_ce + "%'";
                        }
                        v_nce.addElement(s_ce);
                        wc.setColumnExpression(v_nce);
                    }
                    wi.setRightWhereExp(wc);
                }
                else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                    if (this.operator.equalsIgnoreCase("MATCHES")) {
                        wi.setOperator("LIKE");
                    }
                    else {
                        wi.setOperator("NOT LIKE");
                    }
                    final WhereColumn whereColumn = wi.getRightWhereExp();
                    final Vector columnExpression = whereColumn.getColumnExpression();
                    final Vector newColumnExpression = new Vector();
                    if (columnExpression != null) {
                        String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                        if (ColumnExpressionAsString.indexOf("*") != -1) {
                            ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                        }
                        if (ColumnExpressionAsString.indexOf("_") != -1) {
                            ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                        }
                        newColumnExpression.add(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(newColumnExpression);
                    }
                }
                else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null) {
                    final WhereColumn wc = wi.getRightWhereExp();
                    final Vector v_nsce = new Vector();
                    final Vector v_sce = wc.getColumnExpression();
                    if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");
                        for (int i = 0; i < v_sce.size(); ++i) {
                            v_nsce.addElement(v_sce.elementAt(i));
                        }
                        v_nsce.addElement(")");
                        wc.setColumnExpression(v_nsce);
                    }
                    wi.setOperator(this.operator);
                }
                else {
                    wi.setOperator(this.operator);
                }
            }
            if (this.operator2 != null) {
                wi.setOperator2(this.operator2);
            }
            if (this.operator3 != null) {
                wi.setOperator3(this.operator3);
            }
            if (this.LeftJoin != null) {
                wi.setLeftJoin(null);
            }
            if (this.RightJoin != null) {
                wi.setRightJoin(null);
            }
        }
        else {
            wi.setOpenBrace(this.openBraces);
            wi.setCloseBrace(this.closeBraces);
            if (this.rownumClause != null) {
                if (from_sqs != null) {
                    final FromClause fc = from_sqs.getFromClause();
                    if (fc != null) {
                        final FromTable ft = MetadataInfoUtil.getTableOfColumn(from_sqs, "rownum");
                        if (ft != null) {
                            this.rownumClause.setRownumClause(CustomizeUtil.objectNamesToQuotedIdentifier(this.rownumClause.getRownumClause(), SwisSQLUtils.getKeywords(1), null, 1));
                        }
                    }
                }
                wi.setRownumClause(this.rownumClause);
                return wi;
            }
            if (this.beginOperator != null) {
                wi.setBeginOperator(this.beginOperator);
            }
            if (this.operator1 != null) {
                wi.setOperator1(this.operator1);
            }
            if (this.leftWhereExp != null) {
                wi.setObjectContext(this.context);
                wi.setLeftWhereExp(this.leftWhereExp.toOracleSelect(to_sqs, from_sqs));
            }
            if (this.rightWhereExp != null) {
                wi.setObjectContext(this.context);
                final Vector rightColExpr = this.rightWhereExp.getColumnExpression();
                if (rightColExpr != null && rightColExpr.size() == 1) {
                    if (SwisSQLOptions.convertNullStringToOracleISNULL && rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().equalsIgnoreCase("''")) {
                        final String condition = rightColExpr.get(0).toString().trim();
                        wi.setRightWhereExp(null);
                        if (this.operator.trim().equals("=")) {
                            this.operator = "IS NULL";
                        }
                        else if (this.operator.trim().equals("<>") || this.operator.trim().equals("!=")) {
                            this.operator = "IS NOT NULL";
                        }
                    }
                    else if (rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().equalsIgnoreCase("null")) {
                        wi.setRightWhereExp(null);
                        if (this.operator.trim().equalsIgnoreCase("=")) {
                            this.operator = "IS NULL";
                        }
                        else if (this.operator.trim().equalsIgnoreCase("!=") || this.operator.trim().equalsIgnoreCase("<>")) {
                            this.operator = "IS NOT NULL";
                        }
                    }
                    else if (rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().startsWith("'")) {
                        final WhereColumn lwc = wi.getLeftWhereExp();
                        boolean added = false;
                        if (lwc != null) {
                            final Object obj = lwc.getColumnExpression().get(0);
                            if (obj instanceof TableColumn) {
                                final String datatype = MetadataInfoUtil.getDatatypeName(to_sqs, (TableColumn)obj);
                                if (datatype != null) {
                                    final String value = rightColExpr.get(0).toString().trim();
                                    if (datatype.toLowerCase().endsWith("datetime")) {
                                        final String format = SwisSQLUtils.getDateFormat(value, 1);
                                        if (format != null) {
                                            final FunctionCalls fc2 = new FunctionCalls();
                                            final TableColumn tc = new TableColumn();
                                            tc.setColumnName("TO_DATE");
                                            final Vector fnArgs = new Vector();
                                            if (format.startsWith("'1900")) {
                                                fnArgs.add(format);
                                                fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                                            }
                                            else {
                                                fnArgs.add(value);
                                                fnArgs.add(format);
                                            }
                                            fc2.setFunctionName(tc);
                                            fc2.setFunctionArguments(fnArgs);
                                            rightColExpr.setElementAt(fc2, 0);
                                            wi.setRightWhereExp(this.rightWhereExp);
                                            added = true;
                                        }
                                    }
                                }
                            }
                        }
                        if (!added) {
                            wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
                        }
                    }
                    else {
                        wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
                    }
                }
                else if (rightColExpr != null && rightColExpr.size() > 1 && this.operator.trim().equalsIgnoreCase("IN")) {
                    if (rightColExpr.get(1) instanceof WhereColumn) {
                        final WhereColumn tempWC = rightColExpr.get(1);
                        final Vector colExp = tempWC.getColumnExpression();
                        if (colExp != null && colExp.get(0) instanceof String) {
                            final String value2 = colExp.get(0).toString().trim();
                            if (value2.equals("''") && SwisSQLOptions.convertNullStringToOracleISNULL) {
                                colExp.set(0, "NULL");
                            }
                        }
                    }
                    wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
                }
                else {
                    wi.setRightWhereExp(this.rightWhereExp.toOracleSelect(to_sqs, from_sqs));
                }
            }
            if (from_sqs != null && from_sqs.getFromClause() != null) {
                final Vector containsFromItemList = from_sqs.getFromClause().getFromItemList();
                final Vector outerFromItemNamesList = from_sqs.getFromClause().getOuterFromTableNames(containsFromItemList);
                boolean isJoinSet = false;
                if (outerFromItemNamesList != null && wi.getLeftWhereExp() != null && wi.getRightWhereExp() != null) {
                    final WhereColumn leftWhereColumn = wi.getLeftWhereExp();
                    final WhereColumn rightWhereColumn = wi.getRightWhereExp();
                    if (leftWhereColumn.getColumnExpression() != null) {
                        final Vector columnExpression2 = leftWhereColumn.getColumnExpression();
                        String tableOrAliasName = new String();
                        for (int j = 0; j < columnExpression2.size(); ++j) {
                            if (leftWhereColumn.getColumnExpression().get(j) instanceof TableColumn) {
                                final TableColumn tableColumn = leftWhereColumn.getColumnExpression().get(j);
                                tableOrAliasName = tableColumn.getTableName();
                            }
                            else if (leftWhereColumn.getColumnExpression().get(j) instanceof String) {
                                final String tableAliasInWhere = leftWhereColumn.getColumnExpression().get(j);
                                if (tableAliasInWhere.indexOf(".") != -1) {
                                    tableOrAliasName = tableAliasInWhere.substring(0, tableAliasInWhere.indexOf("."));
                                }
                            }
                            if (outerFromItemNamesList.contains(tableOrAliasName)) {
                                String tableOrAliasNameRight = new String();
                                if (rightWhereColumn.getColumnExpression().get(j) instanceof TableColumn) {
                                    final TableColumn tableColumn2 = rightWhereColumn.getColumnExpression().get(j);
                                    tableOrAliasNameRight = tableColumn2.getTableName();
                                }
                                else if (rightWhereColumn.getColumnExpression().get(j) instanceof String) {
                                    final String tableAliasInWhere2 = rightWhereColumn.getColumnExpression().get(j);
                                    if (tableAliasInWhere2.indexOf(".") != -1) {
                                        tableOrAliasNameRight = tableAliasInWhere2.substring(0, tableAliasInWhere2.indexOf("."));
                                    }
                                }
                                final int indexLeft = outerFromItemNamesList.indexOf(tableOrAliasName);
                                final int indexRight = outerFromItemNamesList.indexOf(tableOrAliasNameRight);
                                if (indexLeft > indexRight) {
                                    wi.setRightJoin("+");
                                    isJoinSet = true;
                                }
                            }
                        }
                    }
                    if (rightWhereColumn.getColumnExpression() != null) {
                        String tableOrAliasName2 = new String();
                        for (int k = 0; k < rightWhereColumn.getColumnExpression().size(); ++k) {
                            if (rightWhereColumn.getColumnExpression().get(k) instanceof TableColumn) {
                                final TableColumn tableColumn3 = rightWhereColumn.getColumnExpression().get(k);
                                tableOrAliasName2 = tableColumn3.getTableName();
                            }
                            else if (rightWhereColumn.getColumnExpression().get(k) instanceof String) {
                                final String tableAliasInWhere3 = rightWhereColumn.getColumnExpression().get(k);
                                if (tableAliasInWhere3.indexOf(".") != -1) {
                                    tableOrAliasName2 = tableAliasInWhere3.substring(0, tableAliasInWhere3.indexOf("."));
                                }
                            }
                            if (outerFromItemNamesList.contains(tableOrAliasName2) && !isJoinSet) {
                                wi.setLeftJoin("+");
                            }
                        }
                    }
                }
            }
            if (this.LeftJoin != null) {
                if (this.LeftJoin.equalsIgnoreCase("*")) {
                    wi.setLeftJoin("+");
                }
                else {
                    wi.setLeftJoin(this.LeftJoin);
                }
            }
            if (this.RightJoin != null) {
                if (this.RightJoin.equalsIgnoreCase("*")) {
                    wi.setRightJoin("+");
                }
                else {
                    wi.setRightJoin(this.RightJoin);
                }
            }
            if (this.rightWhereSubQuery != null) {
                wi.setRightWhereSubQuery(this.rightWhereSubQuery.toOracleSelect());
                if (this.rightWhereSubQueryExp != null) {
                    wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toOracleSelect(to_sqs, from_sqs));
                }
            }
            if (this.operator != null) {
                if (this.operator.equalsIgnoreCase("!>")) {
                    wi.setOperator("<=");
                }
                else if (this.operator.equalsIgnoreCase("!<")) {
                    wi.setOperator(">=");
                }
                else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                    wi.setOperator("LIKE");
                    if (wi.getRightWhereSubQuery() != null) {
                        throw new ConvertException("Conversion failure.. Subquery can't be converted");
                    }
                    final WhereColumn wc = wi.getRightWhereExp();
                    final Vector v_ce = wc.getColumnExpression();
                    final Vector v_nce = new Vector();
                    if (v_ce != null) {
                        if (v_ce.size() != 1) {
                            throw new ConvertException("Conversion failure.. Expression can't be converted");
                        }
                        String s_ce = v_ce.elementAt(0).toString();
                        if (s_ce.charAt(0) == '\'') {
                            s_ce = s_ce.replace('\'', ' ').trim();
                            s_ce = "'%" + s_ce + "%'";
                        }
                        else if (s_ce.charAt(0) == '\"') {
                            s_ce = s_ce.replace('\"', ' ').trim();
                            s_ce = "\"%" + s_ce + "%\"";
                        }
                        else {
                            s_ce = "'%" + s_ce + "%'";
                        }
                        v_nce.addElement(s_ce);
                        wc.setColumnExpression(v_nce);
                    }
                    wi.setRightWhereExp(wc);
                }
                else if ((this.operator.equalsIgnoreCase("BETWEEN") || this.operator.equalsIgnoreCase("NOT BETWEEN")) && wi.getRightWhereExp() != null) {
                    final WhereColumn rightSideColumn = wi.getRightWhereExp();
                    final Vector rightExpr = rightSideColumn.getColumnExpression();
                    if (rightExpr != null) {
                        final Vector newRightExpr = new Vector();
                        for (int i = 0; i < rightExpr.size(); ++i) {
                            if (rightExpr.get(i) instanceof String) {
                                final String rightString = rightExpr.get(i);
                                if (!rightString.equalsIgnoreCase("(")) {
                                    if (!rightString.equalsIgnoreCase(")")) {
                                        newRightExpr.add(rightString);
                                    }
                                }
                            }
                            else {
                                newRightExpr.add(rightExpr.get(i));
                            }
                        }
                        rightSideColumn.setColumnExpression(newRightExpr);
                    }
                    wi.setOperator(this.operator);
                }
                else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                    if (this.operator.equalsIgnoreCase("MATCHES")) {
                        wi.setOperator("LIKE");
                    }
                    else {
                        wi.setOperator("NOT LIKE");
                    }
                    final WhereColumn whereColumn = wi.getRightWhereExp();
                    final Vector columnExpression = whereColumn.getColumnExpression();
                    final Vector newColumnExpression = new Vector();
                    if (columnExpression != null) {
                        String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                        if (ColumnExpressionAsString.indexOf("*") != -1) {
                            ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                        }
                        if (ColumnExpressionAsString.indexOf("_") != -1) {
                            ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                        }
                        newColumnExpression.add(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(newColumnExpression);
                    }
                }
                else {
                    wi.setOperator(this.operator);
                }
            }
            if (this.operator2 != null) {
                wi.setOperator2(this.operator2);
            }
            if (this.operator3 != null) {
                wi.setOperator3(this.operator3);
            }
        }
        return wi;
    }
    
    public WhereItem toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toInformixSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toInformixSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toInformixSelect());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toInformixSelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("^=")) {
                wi.setOperator("<>");
            }
            else if (this.operator.equalsIgnoreCase("!>")) {
                wi.setOperator("<=");
            }
            else if (this.operator.equalsIgnoreCase("!<")) {
                wi.setOperator(">=");
            }
            else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if (this.operator.equalsIgnoreCase("LIKE") || this.operator.equalsIgnoreCase("NOT LIKE")) {
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("[") != -1) {
                        if (this.operator.equalsIgnoreCase("LIKE")) {
                            wi.setOperator("MATCHES");
                        }
                        else {
                            wi.setOperator("NOT MATCHES");
                        }
                        if (ColumnExpressionAsString.indexOf("%") != -1) {
                            ColumnExpressionAsString = ColumnExpressionAsString.replace('%', '*');
                        }
                        if (ColumnExpressionAsString.indexOf("?") != -1) {
                            ColumnExpressionAsString = ColumnExpressionAsString.replace('?', '_');
                        }
                        newColumnExpression.add(ColumnExpressionAsString);
                        whereColumn.setColumnExpression(newColumnExpression);
                    }
                    else {
                        wi.setOperator(this.operator);
                    }
                }
            }
            else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
                final WhereColumn r_wc = wi.getRightWhereExp();
                final WhereColumn l_wc = wi.getLeftWhereExp();
                final WhereColumn wc2 = wi.getRightWhereExp();
                final Vector v_nsce = new Vector();
                final Vector v_sce = wc2.getColumnExpression();
                if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                    v_nsce.addElement("(");
                    for (int i = 0; i < v_sce.size(); ++i) {
                        v_nsce.addElement(v_sce.elementAt(i));
                    }
                    v_nsce.addElement(")");
                    wc2.setColumnExpression(v_nsce);
                }
                wi.setOperator(this.operator);
            }
            else if ((this.operator.equalsIgnoreCase("IN") || this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereSubQuery() != null) {
                String f_tablename = null;
                if (from_sqs != null) {
                    FromTable ft = null;
                    if (from_sqs.getFromClause().getFromItemList().elementAt(0) instanceof FromTable) {
                        ft = from_sqs.getFromClause().getFromItemList().elementAt(0);
                        if (ft.getAliasName() != null) {
                            f_tablename = ft.getAliasName();
                        }
                        else if (ft.getTableName() instanceof String) {
                            f_tablename = ft.getTableName().toString();
                            int pos = f_tablename.indexOf(".");
                            if (pos == -1) {
                                pos = f_tablename.indexOf("..");
                            }
                            f_tablename = f_tablename.substring(pos + 1);
                        }
                        else {
                            f_tablename = ft.getTableName().toString();
                        }
                    }
                }
                final WhereColumn l_wc = wi.getLeftWhereExp();
                if (l_wc.getColumnExpression() != null && l_wc.getColumnExpression().size() != 1 && l_wc.getColumnExpression().contains(",")) {
                    final Vector v_removedcomma_left_column_exp = new Vector();
                    this.removeCommaBracesFromExpressionList(l_wc, v_removedcomma_left_column_exp, f_tablename);
                    wi.setLeftWhereExp(null);
                    if (this.operator.equalsIgnoreCase("IN")) {
                        wi.setOperator("EXISTS");
                    }
                    else if (this.operator.equalsIgnoreCase("NOT IN")) {
                        wi.setOperator("NOT EXISTS");
                    }
                    final SelectQueryStatement sqs = wi.getRightWhereSubQuery();
                    String f_tablename_1 = null;
                    final FromTable ft_1 = sqs.getFromClause().getFromItemList().elementAt(0);
                    if (ft_1.getAliasName() == null) {}
                    if (ft_1.getAliasName() != null) {
                        f_tablename_1 = ft_1.getAliasName();
                    }
                    else {
                        f_tablename_1 = ft_1.getTableName().toString();
                    }
                    final Vector v_sitl = sqs.getSelectStatement().getSelectItemList();
                    for (int j = 0; j < v_sitl.size(); ++j) {
                        final WhereItem wi_1 = new WhereItem();
                        final WhereColumn l_wc_1 = new WhereColumn();
                        final WhereColumn r_wc_1 = new WhereColumn();
                        final Vector v_lce = new Vector();
                        final Vector v_rce = new Vector();
                        final SelectColumn sc = v_sitl.elementAt(j);
                        final Vector v_ce2 = sc.getColumnExpression();
                        String where_column_name = null;
                        if (v_ce2.elementAt(0) instanceof String) {
                            where_column_name = v_ce2.elementAt(0);
                        }
                        else if (v_ce2.elementAt(0) instanceof TableColumn) {
                            final TableColumn tc = v_ce2.elementAt(0);
                            if (tc.getTableName() == null) {
                                tc.setTableName(f_tablename_1);
                            }
                            where_column_name = tc.toString();
                        }
                        else if (v_ce2.elementAt(0) instanceof SelectColumn) {
                            final SelectColumn selectColumnInsideSelectColumn = v_ce2.elementAt(0);
                            final Vector newColumnExp = selectColumnInsideSelectColumn.getColumnExpression();
                            if (newColumnExp.elementAt(0) instanceof String) {
                                where_column_name = newColumnExp.elementAt(0);
                            }
                            else if (newColumnExp.elementAt(0) instanceof TableColumn) {
                                final TableColumn tableColumn = newColumnExp.elementAt(0);
                                if (tableColumn.getTableName() == null) {
                                    tableColumn.setTableName(f_tablename_1);
                                }
                                where_column_name = tableColumn.toString();
                            }
                        }
                        v_lce.addElement(where_column_name);
                        l_wc_1.setColumnExpression(v_lce);
                        v_rce.addElement(v_removedcomma_left_column_exp.elementAt(j));
                        r_wc_1.setColumnExpression(v_rce);
                        wi_1.setOperator("=");
                        wi_1.setLeftWhereExp(l_wc_1);
                        wi_1.setRightWhereExp(r_wc_1);
                        if (sqs.getWhereExpression() != null) {
                            sqs.getWhereExpression().addWhereItem(wi_1);
                            sqs.getWhereExpression().addOperator("AND");
                        }
                        else {
                            final WhereExpression we = new WhereExpression();
                            we.addWhereItem(wi_1);
                            sqs.setWhereExpression(we);
                        }
                    }
                    wi.setRightWhereSubQuery(sqs);
                }
                else {
                    wi.setOperator(this.operator);
                }
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        if (this.LeftJoin != null) {
            wi.setLeftJoin(null);
        }
        if (this.RightJoin != null) {
            wi.setRightJoin(null);
        }
        return wi;
    }
    
    public WhereItem convert(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final int database) throws ConvertException {
        if (database == 8) {
            return this.toANSISelect(to_sqs, from_sqs);
        }
        if (database == 3) {
            return this.toDB2Select(to_sqs, from_sqs);
        }
        if (database == 2) {
            return this.toMSSQLServerSelect(to_sqs, from_sqs);
        }
        if (database == 7) {
            return this.toSybaseSelect(to_sqs, from_sqs);
        }
        if (database == 5) {
            return this.toMySQLSelect(to_sqs, from_sqs);
        }
        if (database == 4) {
            return this.toPostgreSQLSelect(to_sqs, from_sqs);
        }
        if (database == 6) {
            return this.toInformixSelect(to_sqs, from_sqs);
        }
        if (database == 1) {
            return this.toOracleSelect(to_sqs, from_sqs);
        }
        if (database == 11) {
            return this.toNetezzaSelect(to_sqs, from_sqs);
        }
        if (database == 12) {
            return this.toTeradataSelect(to_sqs, from_sqs);
        }
        if (database == 13) {
            return this.toVectorWiseSelect(to_sqs, from_sqs);
        }
        return null;
    }
    
    public WhereItem toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        if (this.leftWhereExp != null) {
            wi.setObjectContext(this.context);
            wi.setLeftWhereExp(this.leftWhereExp.toTimesTenSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereExp != null) {
            wi.setObjectContext(this.context);
            final Vector rightColExpr = this.rightWhereExp.getColumnExpression();
            if (rightColExpr != null && rightColExpr.size() == 1) {
                if (rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().equalsIgnoreCase("null")) {
                    wi.setRightWhereExp(null);
                    if (this.operator.trim().equalsIgnoreCase("=")) {
                        this.operator = "IS NULL";
                    }
                    else if (this.operator.trim().equalsIgnoreCase("!=") || this.operator.trim().equalsIgnoreCase("<>")) {
                        this.operator = "IS NOT NULL";
                    }
                }
                else if (rightColExpr.get(0) instanceof String && rightColExpr.get(0).toString().trim().startsWith("'")) {
                    final WhereColumn lwc = wi.getLeftWhereExp();
                    boolean added = false;
                    if (lwc != null) {
                        final Object obj = lwc.getColumnExpression().get(0);
                        if (obj instanceof TableColumn) {
                            final String datatype = MetadataInfoUtil.getDatatypeName(to_sqs, (TableColumn)obj);
                            if (datatype != null) {
                                String value = rightColExpr.get(0).toString().trim();
                                if (datatype.toLowerCase().indexOf("date") != -1 || datatype.toLowerCase().indexOf("time") != -1) {
                                    String format = SwisSQLUtils.getDateFormat(value, 10);
                                    if (format != null && (format.equals("YYYY-MM-DD") || format.equals("HH24:MI:SS"))) {
                                        if (datatype.toLowerCase().indexOf("datetime") != -1) {
                                            if (format.equals("YYYY-MM-DD")) {
                                                value = value.substring(0, value.length() - 1) + " 00:00:00'";
                                            }
                                            else {
                                                value = "'1900-01-01 " + value.substring(1);
                                            }
                                            rightColExpr.setElementAt(value, 0);
                                            wi.setRightWhereExp(this.rightWhereExp);
                                            added = true;
                                        }
                                        format = null;
                                    }
                                    if (format != null) {
                                        if (format.startsWith("'1900")) {
                                            rightColExpr.setElementAt(format, 0);
                                            wi.setRightWhereExp(this.rightWhereExp);
                                            added = true;
                                        }
                                        else if (format.equals(value)) {
                                            value = value.substring(1, value.length() - 1);
                                            String time = "";
                                            int index = 0;
                                            if ((index = value.indexOf(" ")) != -1) {
                                                time = value.substring(index + 1);
                                                value = value.substring(0, index);
                                            }
                                            final int len = value.length();
                                            if (len == 8) {
                                                value = value.substring(0, 4) + "-" + value.substring(4, 6) + "-" + value.substring(6);
                                            }
                                            else if (len == 6) {
                                                String yearStr = value.substring(0, 2);
                                                final int year = Integer.parseInt(yearStr);
                                                if (year < 50) {
                                                    yearStr = "20" + yearStr;
                                                }
                                                else {
                                                    yearStr = "19" + yearStr;
                                                }
                                                value = yearStr + "-" + value.substring(2, 4) + "-" + value.substring(4);
                                            }
                                            if (datatype.toLowerCase().equalsIgnoreCase("time") && time == "") {
                                                value += " 00:00:00";
                                            }
                                            else if (time != "") {
                                                value = value + " " + time;
                                            }
                                            rightColExpr.setElementAt(value, 0);
                                            wi.setRightWhereExp(this.rightWhereExp);
                                            added = true;
                                        }
                                        else {
                                            final FunctionCalls fc = new FunctionCalls();
                                            final TableColumn tc = new TableColumn();
                                            tc.setColumnName("TO_DATE");
                                            final Vector fnArgs = new Vector();
                                            fnArgs.add(value);
                                            fnArgs.add(format);
                                            fc.setFunctionName(tc);
                                            fc.setFunctionArguments(fnArgs);
                                            rightColExpr.setElementAt(fc, 0);
                                            wi.setRightWhereExp(this.rightWhereExp);
                                            added = true;
                                        }
                                    }
                                }
                                else if (datatype.indexOf("unichar") != -1 || datatype.indexOf("univarchar") != -1 || datatype.indexOf("nchar") != -1 || datatype.indexOf("nvarchar") != -1) {
                                    rightColExpr.setElementAt("N" + value, 0);
                                    wi.setRightWhereExp(this.rightWhereExp);
                                    added = true;
                                }
                            }
                        }
                    }
                    if (!added) {
                        wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
                    }
                }
                else if (rightColExpr.get(0).toString().trim().startsWith("\"") && this.operator.equalsIgnoreCase("like")) {
                    rightColExpr.setElementAt(StringFunctions.replaceAll("'", "\"", rightColExpr.get(0).toString()), 0);
                    wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
                }
                else {
                    wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
                }
            }
            else if (rightColExpr != null && rightColExpr.size() == 3 && rightColExpr.get(1).toString().equalsIgnoreCase("escape") && this.operator.equals("like")) {
                if (rightColExpr.get(0).toString().trim().startsWith("\"")) {
                    rightColExpr.setElementAt(StringFunctions.replaceAll("'", "\"", rightColExpr.get(0).toString()), 0);
                }
                if (rightColExpr.get(2).toString().trim().startsWith("\"")) {
                    rightColExpr.setElementAt(StringFunctions.replaceAll("'", "\"", rightColExpr.get(2).toString()), 2);
                }
                wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
            }
            else {
                wi.setRightWhereExp(this.rightWhereExp.toTimesTenSelect(to_sqs, from_sqs));
            }
        }
        if (from_sqs != null && from_sqs.getFromClause() != null) {
            final Vector containsFromItemList = from_sqs.getFromClause().getFromItemList();
            final Vector outerFromItemNamesList = from_sqs.getFromClause().getOuterFromTableNames(containsFromItemList);
            boolean isJoinSet = false;
            if (outerFromItemNamesList != null && wi.getLeftWhereExp() != null && wi.getRightWhereExp() != null) {
                final WhereColumn leftWhereColumn = wi.getLeftWhereExp();
                final WhereColumn rightWhereColumn = wi.getRightWhereExp();
                if (leftWhereColumn.getColumnExpression() != null) {
                    final Vector columnExpression = leftWhereColumn.getColumnExpression();
                    String tableOrAliasName = new String();
                    for (int i = 0; i < columnExpression.size(); ++i) {
                        if (leftWhereColumn.getColumnExpression().get(i) instanceof TableColumn) {
                            final TableColumn tableColumn = leftWhereColumn.getColumnExpression().get(i);
                            tableOrAliasName = tableColumn.getTableName();
                        }
                        else if (leftWhereColumn.getColumnExpression().get(i) instanceof String) {
                            final String tableAliasInWhere = leftWhereColumn.getColumnExpression().get(i);
                            if (tableAliasInWhere.indexOf(".") != -1) {
                                tableOrAliasName = tableAliasInWhere.substring(0, tableAliasInWhere.indexOf("."));
                            }
                        }
                        if (outerFromItemNamesList.contains(tableOrAliasName)) {
                            String tableOrAliasNameRight = new String();
                            if (rightWhereColumn.getColumnExpression().get(i) instanceof TableColumn) {
                                final TableColumn tableColumn2 = rightWhereColumn.getColumnExpression().get(i);
                                tableOrAliasNameRight = tableColumn2.getTableName();
                            }
                            else if (rightWhereColumn.getColumnExpression().get(i) instanceof String) {
                                final String tableAliasInWhere2 = rightWhereColumn.getColumnExpression().get(i);
                                if (tableAliasInWhere2.indexOf(".") != -1) {
                                    tableOrAliasNameRight = tableAliasInWhere2.substring(0, tableAliasInWhere2.indexOf("."));
                                }
                            }
                            final int indexLeft = outerFromItemNamesList.indexOf(tableOrAliasName);
                            final int indexRight = outerFromItemNamesList.indexOf(tableOrAliasNameRight);
                            if (indexLeft > indexRight) {
                                wi.setRightJoin("+");
                                isJoinSet = true;
                            }
                        }
                    }
                }
                if (rightWhereColumn.getColumnExpression() != null) {
                    String tableOrAliasName2 = new String();
                    for (int j = 0; j < rightWhereColumn.getColumnExpression().size(); ++j) {
                        if (rightWhereColumn.getColumnExpression().get(j) instanceof TableColumn) {
                            final TableColumn tableColumn3 = rightWhereColumn.getColumnExpression().get(j);
                            tableOrAliasName2 = tableColumn3.getTableName();
                        }
                        else if (rightWhereColumn.getColumnExpression().get(j) instanceof String) {
                            final String tableAliasInWhere3 = rightWhereColumn.getColumnExpression().get(j);
                            if (tableAliasInWhere3.indexOf(".") != -1) {
                                tableOrAliasName2 = tableAliasInWhere3.substring(0, tableAliasInWhere3.indexOf("."));
                            }
                        }
                        if (outerFromItemNamesList.contains(tableOrAliasName2) && !isJoinSet) {
                            wi.setLeftJoin("+");
                        }
                    }
                }
            }
        }
        if (this.LeftJoin != null) {
            if (this.LeftJoin.equalsIgnoreCase("*")) {
                wi.setLeftJoin("+");
                final WhereColumn rwc = wi.getRightWhereExp();
                if (rwc != null && rwc.getColumnExpression().get(0) instanceof String) {
                    wi.setRightJoin("+");
                    wi.setLeftJoin(null);
                }
            }
            else {
                wi.setLeftJoin(this.LeftJoin);
            }
        }
        if (this.RightJoin != null) {
            if (this.RightJoin.equalsIgnoreCase("*")) {
                wi.setRightJoin("+");
            }
            else {
                wi.setRightJoin(this.RightJoin);
            }
        }
        if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toTimesTenSelect());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toTimesTenSelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("!>")) {
                wi.setOperator("<=");
            }
            else if (this.operator.equalsIgnoreCase("!<")) {
                wi.setOperator(">=");
            }
            else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expression can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if ((this.operator.equalsIgnoreCase("BETWEEN") || this.operator.equalsIgnoreCase("NOT BETWEEN")) && wi.getRightWhereExp() != null) {
                final WhereColumn rightSideColumn = wi.getRightWhereExp();
                final Vector rightExpr = rightSideColumn.getColumnExpression();
                if (rightExpr != null) {
                    final Vector newRightExpr = new Vector();
                    for (int k = 0; k < rightExpr.size(); ++k) {
                        if (rightExpr.get(k) instanceof String) {
                            final String rightString = rightExpr.get(k);
                            if (!rightString.equalsIgnoreCase("(")) {
                                if (!rightString.equalsIgnoreCase(")")) {
                                    newRightExpr.add(rightString);
                                }
                            }
                        }
                        else {
                            newRightExpr.add(rightExpr.get(k));
                        }
                    }
                    rightSideColumn.setColumnExpression(newRightExpr);
                }
                wi.setOperator(this.operator);
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression2 = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression2 != null) {
                    String ColumnExpressionAsString = columnExpression2.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        return wi;
    }
    
    public WhereItem toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toNetezzaSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toNetezzaSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toNetezzaSelect());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toNetezzaSelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            if (this.operator.equalsIgnoreCase("^=") || this.operator.equalsIgnoreCase("!=")) {
                wi.setOperator("<>");
            }
            else if (this.operator.equalsIgnoreCase("!>")) {
                wi.setOperator("<=");
            }
            else if (this.operator.equalsIgnoreCase("!<")) {
                wi.setOperator(">=");
            }
            else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expressions can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    if (ColumnExpressionAsString.indexOf("_") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('_', '?');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null) {
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_nsce = new Vector();
                final Vector v_sce = wc.getColumnExpression();
                if (v_sce != null && v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                    v_nsce.addElement("(");
                    for (int i = 0; i < v_sce.size(); ++i) {
                        v_nsce.addElement(v_sce.elementAt(i));
                    }
                    v_nsce.addElement(")");
                    wc.setColumnExpression(v_nsce);
                }
                wi.setOperator(this.operator);
            }
            else {
                wi.setOperator(this.operator);
            }
        }
        if (this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        if (this.LeftJoin != null) {
            wi.setLeftJoin(null);
        }
        if (this.RightJoin != null) {
            wi.setRightJoin(null);
        }
        return wi;
    }
    
    @Override
    public String toString() {
        if (this.rownumClause != null) {
            return this.rownumClause.toString();
        }
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.openBraces != null && !this.openBraces.equals("")) {
            sb.append(this.openBraces);
        }
        if (this.beginOperator != null) {
            sb.append(this.beginOperator + " ");
        }
        if (this.operator1 != null) {
            sb.append(this.operator1 + " ");
        }
        if (this.leftWhereExp != null) {
            this.leftWhereExp.setObjectContext(this.context);
            final Vector colExp = this.leftWhereExp.getColumnExpression();
            if (colExp != null) {
                for (int i = 0; i < colExp.size(); ++i) {
                    if (colExp.elementAt(i) instanceof TableColumn) {
                        colExp.elementAt(i).setObjectContext(this.context);
                    }
                    else if (colExp.elementAt(i) instanceof SelectColumn) {
                        colExp.elementAt(i).setObjectContext(this.context);
                    }
                    else if (colExp.elementAt(i) instanceof FunctionCalls) {
                        colExp.elementAt(i).setObjectContext(this.context);
                    }
                }
            }
            sb.append(this.leftWhereExp.toString());
            if (this.RightJoin != null && this.RightJoin.equals("+")) {
                sb.append("(" + this.RightJoin + ")");
            }
            if (this.LeftJoin != null && this.LeftJoin.equals("*")) {
                sb.append(" *");
            }
        }
        if (this.operator != null) {
            if (this.LeftJoin != null && this.LeftJoin.equals("*")) {
                sb.append(this.operator);
            }
            else {
                sb.append(" " + this.operator);
            }
            if (this.RightJoin != null && this.RightJoin.equals("*")) {
                sb.append(this.RightJoin);
            }
        }
        if (this.lnnvlWhereExp != null) {
            sb.append(this.lnnvlWhereExp.toString());
        }
        if (this.regExp != null) {
            for (int j = 0; j < this.regExp.size(); ++j) {
                if (this.regExp.get(j) instanceof SelectColumn) {
                    final SelectColumn sc = this.regExp.get(j);
                    sb.append(sc.toString());
                }
                else {
                    sb.append(this.regExp.get(j).toString());
                }
            }
        }
        if (this.operator2 != null) {
            sb.append(" " + this.operator2.toUpperCase());
        }
        if (this.operator3 != null) {
            sb.append(" " + this.operator3.toUpperCase());
        }
        if (this.rightWhereSubQuery != null) {
            sb.append("\n");
            ++SelectQueryStatement.beautyTabCount;
            for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                sb.append("\t");
            }
            sb.append("(");
            sb.append("\n ");
            this.rightWhereSubQuery.setObjectContext(this.context);
            sb.append(this.rightWhereSubQuery.toString());
            if (this.rightWhereSubQueryExp != null) {
                boolean isLeftJoinAdded = false;
                final Vector colExp2 = this.rightWhereSubQueryExp.getColumnExpression();
                if (colExp2 != null) {
                    for (int k = 0; k < colExp2.size(); ++k) {
                        if (colExp2.elementAt(k) instanceof TableColumn) {
                            colExp2.elementAt(k).setObjectContext(this.context);
                        }
                        if (colExp2.elementAt(k) instanceof TableColumn && !isLeftJoinAdded) {
                            sb.append(" " + colExp2.elementAt(k).toString());
                            if (this.LeftJoin != null && this.LeftJoin.equals("+")) {
                                sb.append(" (" + this.LeftJoin + ")");
                                isLeftJoinAdded = true;
                            }
                        }
                        else {
                            sb.append(" " + colExp2.elementAt(k).toString());
                        }
                        if (this.LeftJoin != null && this.LeftJoin.equals("+") && !isLeftJoinAdded) {
                            sb.append(" (" + this.LeftJoin + ")");
                            isLeftJoinAdded = true;
                        }
                    }
                }
            }
            for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                sb.append("\t");
            }
            sb.append(")");
            --SelectQueryStatement.beautyTabCount;
        }
        else if (this.rightWhereExp != null) {
            this.rightWhereExp.setObjectContext(this.context);
            boolean isLeftJoinAdded = false;
            if (this.rightWhereExp.getCommentClass() != null) {
                sb.append(this.rightWhereExp.getCommentClass().toString());
            }
            if (this.rightWhereExp.getOpenBrace() != null) {
                sb.append(this.rightWhereExp.getOpenBrace());
            }
            final Vector colExp2 = this.rightWhereExp.getColumnExpression();
            if (colExp2 != null) {
                for (int k = 0; k < colExp2.size(); ++k) {
                    if (colExp2.elementAt(k) instanceof TableColumn && !isLeftJoinAdded) {
                        final TableColumn tc = colExp2.elementAt(k);
                        if (tc.getCommentClass() != null) {
                            final CommentClass co = tc.getCommentClass();
                            if (co != null) {
                                sb.append(" " + co.toString().trim());
                            }
                        }
                        tc.setObjectContext(this.context);
                        sb.append(" " + colExp2.elementAt(k).toString());
                        if (this.LeftJoin != null && this.LeftJoin.equals("+")) {
                            sb.append(" (" + this.LeftJoin + ")");
                            isLeftJoinAdded = true;
                        }
                    }
                    else if (colExp2.elementAt(k) instanceof SelectColumn) {
                        colExp2.elementAt(k).setObjectContext(this.context);
                        sb.append(" " + colExp2.elementAt(k).toString());
                    }
                    else if (colExp2.elementAt(k) instanceof FunctionCalls) {
                        colExp2.elementAt(k).setObjectContext(this.context);
                        sb.append(" " + colExp2.elementAt(k).toString());
                    }
                    else if (colExp2.elementAt(k) instanceof WhereColumn) {
                        colExp2.elementAt(k).setObjectContext(this.context);
                        sb.append(" " + colExp2.elementAt(k).toString());
                    }
                    else {
                        sb.append(" " + colExp2.elementAt(k).toString());
                    }
                    if (this.LeftJoin != null && this.LeftJoin.equals("+") && !isLeftJoinAdded) {
                        sb.append(" (" + this.LeftJoin + ")");
                        isLeftJoinAdded = true;
                    }
                }
                if (this.rightWhereExp.getCloseBrace() != null) {
                    sb.append(this.rightWhereExp.getCloseBrace());
                }
                if (this.rightWhereExp.getCommentClassAfterToken() != null) {
                    sb.append(" " + this.rightWhereExp.getCommentClassAfterToken().toString());
                }
            }
        }
        if (this.closeBraces != null && !this.closeBraces.equals("")) {
            sb.append(this.closeBraces);
        }
        if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString().trim());
        }
        if (this.casestmtFromLNNVLClause != null) {
            sb.append(this.casestmtFromLNNVLClause.toString());
        }
        final String returnString = sb.toString();
        if (returnString.equals("()")) {
            return null;
        }
        return returnString;
    }
    
    public Object clone() {
        final WhereItem item = new WhereItem();
        item.setRownumClause(this.rownumClause);
        item.setOpenBrace(this.openBraces);
        item.setOperator1(this.operator1);
        item.setLeftWhereExp(this.leftWhereExp);
        item.setOperator(this.operator);
        item.setOperator2(this.operator2);
        item.setOperator3(this.operator3);
        item.setRightWhereSubQuery(this.rightWhereSubQuery);
        item.setRightWhereExp(this.rightWhereExp);
        item.setOpenBrace(this.closeBraces);
        item.setLeftJoin(this.LeftJoin);
        item.setRightJoin(this.RightJoin);
        item.setObjectContext(this.context);
        return item;
    }
    
    public boolean isItAJoinItem() {
        Vector leftColumnExpression = null;
        Vector rightColumnExpression = null;
        final WhereColumn leftItem = this.getLeftWhereExp();
        if (leftItem != null) {
            leftColumnExpression = leftItem.getColumnExpression();
        }
        final WhereColumn rightItem = this.getRightWhereExp();
        if (rightItem != null) {
            rightColumnExpression = rightItem.getColumnExpression();
        }
        return (leftColumnExpression == null || leftColumnExpression.elementAt(0) instanceof TableColumn) && (rightColumnExpression == null || rightColumnExpression.elementAt(0) instanceof TableColumn) && leftColumnExpression != null && rightColumnExpression != null;
    }
    
    public Vector getWhereItemsReplacingInClause() {
        final Vector whereItemsListReplacingInClause = new Vector();
        final String whereItemOperator = this.getOperator();
        final SelectQueryStatement rightWhereSubquery = this.getRightWhereSubQuery();
        final WhereColumn rightWhereExpression = this.getRightWhereExp();
        final WhereColumn leftWhereColumn = this.getLeftWhereExp();
        final WhereColumn rightWhereColumn = this.getRightWhereExp();
        final Vector leftWhereColumnExp = leftWhereColumn.getColumnExpression();
        final Vector rightWhereColumnExp = rightWhereColumn.getColumnExpression();
        final Vector newLeftWhereColumnExp = new Vector();
        final Vector newRightWhereColumnExp = new Vector();
        final Vector removeCommaInRightColumnExp = new Vector();
        final Vector removeCommaInLeftColumnExp = new Vector();
        for (int i = 0; i < leftWhereColumnExp.size(); ++i) {
            if (leftWhereColumnExp.elementAt(i) instanceof String) {
                final String s_ce = leftWhereColumnExp.elementAt(i);
                if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                    removeCommaInLeftColumnExp.addElement(leftWhereColumnExp.elementAt(i));
                }
            }
            else {
                removeCommaInLeftColumnExp.addElement(leftWhereColumnExp.elementAt(i));
            }
        }
        for (int i = 0; i < rightWhereColumnExp.size(); ++i) {
            if (rightWhereColumnExp.elementAt(i) instanceof String) {
                final String s_ce = rightWhereColumnExp.elementAt(i);
                if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                    removeCommaInRightColumnExp.addElement(rightWhereColumnExp.elementAt(i));
                }
            }
            else {
                removeCommaInRightColumnExp.addElement(rightWhereColumnExp.elementAt(i));
            }
        }
        for (int i = 0; i < removeCommaInLeftColumnExp.size(); ++i) {
            final WhereItem newWhereItem = new WhereItem();
            final WhereColumn leftWhereColumnInNewWhereItem = new WhereColumn();
            final WhereColumn rightWhereColumnInNewWhereItem = new WhereColumn();
            final Vector v_lnsc = new Vector();
            final Vector v_rnsc = new Vector();
            v_lnsc.addElement(removeCommaInLeftColumnExp.elementAt(i));
            v_rnsc.addElement(removeCommaInRightColumnExp.elementAt(i));
            leftWhereColumnInNewWhereItem.setColumnExpression(v_lnsc);
            rightWhereColumnInNewWhereItem.setColumnExpression(v_rnsc);
            newWhereItem.setLeftWhereExp(leftWhereColumnInNewWhereItem);
            newWhereItem.setRightWhereExp(rightWhereColumnInNewWhereItem);
            if (this.operator.equalsIgnoreCase("IN")) {
                newWhereItem.setOperator("=");
            }
            else {
                newWhereItem.setOperator("!=");
            }
            whereItemsListReplacingInClause.add(newWhereItem);
        }
        return whereItemsListReplacingInClause;
    }
    
    public Vector getWhereItemsReplacingEqualsClause() {
        final Vector whereItemsListReplacingEqualsClause = new Vector();
        final String whereItemOperator = this.getOperator2();
        final SelectQueryStatement rightWhereSubquery = this.getRightWhereSubQuery();
        final WhereColumn rightWhereExpression = this.getRightWhereExp();
        final WhereColumn leftWhereColumn = this.getLeftWhereExp();
        final WhereColumn rightWhereColumn = this.getRightWhereExp();
        final Vector leftWhereColumnExp = leftWhereColumn.getColumnExpression();
        final Vector rightWhereColumnExp = rightWhereColumn.getColumnExpression();
        final Vector newLeftWhereColumnExp = new Vector();
        final Vector newRightWhereColumnExp = new Vector();
        final Vector removeCommaInRightColumnExp = new Vector();
        final Vector removeCommaInLeftColumnExp = new Vector();
        for (int i = 0; i < leftWhereColumnExp.size(); ++i) {
            if (leftWhereColumnExp.elementAt(i) instanceof String) {
                final String s_ce = leftWhereColumnExp.elementAt(i);
                if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                    removeCommaInLeftColumnExp.addElement(leftWhereColumnExp.elementAt(i));
                }
            }
            else {
                removeCommaInLeftColumnExp.addElement(leftWhereColumnExp.elementAt(i));
            }
        }
        for (int i = 0; i < rightWhereColumnExp.size(); ++i) {
            if (rightWhereColumnExp.elementAt(i) instanceof String) {
                final String s_ce = rightWhereColumnExp.elementAt(i);
                if (!s_ce.equalsIgnoreCase(",") && !s_ce.equalsIgnoreCase("(") && !s_ce.equalsIgnoreCase(")")) {
                    removeCommaInRightColumnExp.addElement(rightWhereColumnExp.elementAt(i));
                }
            }
            else {
                removeCommaInRightColumnExp.addElement(rightWhereColumnExp.elementAt(i));
            }
        }
        for (int i = 0; i < removeCommaInRightColumnExp.size(); ++i) {
            final WhereItem newWhereItem = new WhereItem();
            final WhereColumn leftWhereColumnInNewWhereItem = new WhereColumn();
            final WhereColumn rightWhereColumnInNewWhereItem = new WhereColumn();
            final Vector v_lnsc = new Vector();
            final Vector v_rnsc = new Vector();
            v_lnsc.addElement(removeCommaInLeftColumnExp.elementAt(0));
            v_rnsc.addElement(removeCommaInRightColumnExp.elementAt(i));
            leftWhereColumnInNewWhereItem.setColumnExpression(v_lnsc);
            rightWhereColumnInNewWhereItem.setColumnExpression(v_rnsc);
            newWhereItem.setLeftWhereExp(leftWhereColumnInNewWhereItem);
            newWhereItem.setRightWhereExp(rightWhereColumnInNewWhereItem);
            if (this.operator2.equalsIgnoreCase("ALL") || this.operator2.equalsIgnoreCase("ANY") || this.operator2.equalsIgnoreCase("SOME")) {
                newWhereItem.setOperator(this.getOperator());
            }
            whereItemsListReplacingEqualsClause.add(newWhereItem);
        }
        return whereItemsListReplacingEqualsClause;
    }
    
    private boolean containsColumnVariable(final Vector colExp, boolean bool) {
        if (colExp != null) {
            for (int i = 0; i < colExp.size(); ++i) {
                if (colExp.get(i) instanceof TableColumn) {
                    if (colExp.get(i).toString().startsWith("@")) {
                        bool = true;
                    }
                }
                else if (colExp.get(i) instanceof SelectColumn) {
                    final Vector selColExp = colExp.get(i).getColumnExpression();
                    bool = this.containsColumnVariable(selColExp, bool);
                }
                else if (colExp.get(i) instanceof FunctionCalls) {
                    final FunctionCalls fc1 = colExp.get(i);
                    final Vector FunctionArgs = fc1.getFunctionArguments();
                    final String fc1Name = fc1.getFunctionNameAsAString();
                    if (fc1Name != null && fc1Name.trim().equalsIgnoreCase("CONTAINS")) {
                        this.isContainsFunction = true;
                    }
                    bool = this.containsColumnVariable(FunctionArgs, bool);
                }
                else if (colExp.get(i) instanceof String) {
                    final String str = colExp.get(i);
                    if (str.trim().startsWith("@")) {
                        bool = true;
                    }
                    else if (str.trim().startsWith("'")) {
                        bool = true;
                    }
                    else {
                        try {
                            final int intValue = Integer.parseInt(str);
                            bool = true;
                        }
                        catch (final NumberFormatException ex) {}
                    }
                }
            }
        }
        return bool;
    }
    
    private void convertLNNVLtoStatement(final WhereExpression we) {
        this.casestmtFromLNNVLClause = new CaseStatement();
        this.operator = null;
        final SelectColumn scForThenCondition = new SelectColumn();
        final Vector colExpForWhenCondition = new Vector();
        final TableColumn tc = new TableColumn();
        final Vector thenStmts = new Vector();
        final WhenStatement whenClause = new WhenStatement();
        final SelectColumn scForElseStmt = new SelectColumn();
        final Vector elseStmts = new Vector();
        final TableColumn tcForElseStmts = new TableColumn();
        this.casestmtFromLNNVLClause.setCaseClause("CASE");
        this.casestmtFromLNNVLClause.setElseClause("ELSE");
        this.casestmtFromLNNVLClause.setEndClause("END");
        whenClause.setWhenClause("WHEN");
        whenClause.setWhenCondition(we);
        whenClause.setThenClause("THEN");
        tc.setColumnName("FALSE");
        thenStmts.add(tc);
        scForThenCondition.setColumnExpression(thenStmts);
        whenClause.setThenStatement(scForThenCondition);
        colExpForWhenCondition.add(whenClause);
        tcForElseStmts.setColumnName("TRUE");
        elseStmts.add(tcForElseStmts);
        scForElseStmt.setColumnExpression(elseStmts);
        this.casestmtFromLNNVLClause.setWhenStatementList(colExpForWhenCondition);
        this.casestmtFromLNNVLClause.setElseStatement(scForElseStmt);
    }
    
    public boolean checkForWeekOfDayConversion(final FunctionCalls funcCall) {
        boolean needsWeekOfDayConversion = false;
        final String funcStr = funcCall.toString().toUpperCase();
        if (funcCall.getFunctionName().getColumnName().equalsIgnoreCase("TRUNC") && funcStr.indexOf("MOD") != -1 && funcStr.indexOf("TO_NUMBER") != -1 && funcStr.indexOf("TO_CHAR") != -1 && funcStr.indexOf("TO_DATE") != -1) {
            needsWeekOfDayConversion = true;
        }
        else if (funcCall.getFunctionName().getColumnName().equalsIgnoreCase("MOD") && funcStr.indexOf("TO_CHAR") != -1) {
            needsWeekOfDayConversion = true;
        }
        return needsWeekOfDayConversion;
    }
    
    private void handleJulianOrDayFormatForTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final WhereItem wi, final SelectColumn sc) throws ConvertException {
        final Vector colExp = this.rightWhereExp.getColumnExpression();
        final Vector newColExp = new Vector();
        for (int ci = 0; ci < colExp.size(); ++ci) {
            final Object colObj = colExp.elementAt(ci);
            if (colObj instanceof FunctionCalls) {
                final FunctionCalls wcFunc = (FunctionCalls)colObj;
                if (ci == 0 && wcFunc.getFunctionName().getColumnName().equalsIgnoreCase("trunc") && colExp.size() == 3 && colExp.lastElement().toString().equalsIgnoreCase("1") && colExp.get(colExp.size() - 2).toString().equalsIgnoreCase("+") && this.checkForWeekOfDayConversion(wcFunc)) {
                    newColExp.add(this.generateTeradataSysCalendarConstructs(to_sqs, wi, sc));
                    colExp.remove(colExp.size() - 1);
                    colExp.remove(colExp.size() - 2);
                }
                else {
                    if (!wcFunc.getFunctionName().getColumnName().equalsIgnoreCase("mod") || !this.checkForWeekOfDayConversion(wcFunc)) {
                        throw new ConvertException("Exception occurred in conversion of Oracle 'J' and 'D' date formats to Teradata");
                    }
                    newColExp.add(this.generateTeradataSysCalendarConstructs(to_sqs, wi, sc));
                }
            }
            else if (colObj instanceof TableColumn) {
                newColExp.addElement(((TableColumn)colObj).toTeradataSelect(to_sqs, from_sqs));
            }
            else if (colObj instanceof WhereColumn) {
                newColExp.addElement(((WhereColumn)colObj).toTeradataSelect(to_sqs, from_sqs));
            }
            else if (colObj instanceof CaseStatement) {
                newColExp.addElement(((CaseStatement)colObj).toTeradataSelect(to_sqs, from_sqs));
            }
            else if (colObj instanceof SelectQueryStatement) {
                newColExp.addElement(((SelectQueryStatement)colObj).toTeradataSelect());
            }
            else if (colObj instanceof SelectColumn) {
                newColExp.addElement(((SelectColumn)colObj).toTeradataSelect(to_sqs, from_sqs));
            }
            else if (colObj instanceof String) {
                final String s_ce = (String)colObj;
                if (s_ce.equalsIgnoreCase("**")) {
                    this.rightWhereExp.createPowerFunction(newColExp, colExp, ci);
                }
                else if (s_ce.indexOf(".") != -1 && s_ce.charAt(0) != '\'') {
                    final String[] elements = s_ce.split("\\.");
                    final int esize = elements.length;
                    if (esize > 0) {
                        final StringBuffer newS_ce = new StringBuffer();
                        for (int es = 0; es < esize; ++es) {
                            String elem = elements[es];
                            elem = CustomizeUtil.objectNamesToQuotedIdentifier(elem, SwisSQLUtils.getKeywords("teradata"), null, -1);
                            if (elem.equalsIgnoreCase("dual") || elem.equalsIgnoreCase("sys.dual")) {
                                elem = "\"DUAL\"";
                            }
                            if (es == 0) {
                                newS_ce.append(elem);
                            }
                            else {
                                newS_ce.append("." + elem);
                            }
                        }
                        newColExp.addElement(newS_ce.toString());
                        colExp.setElementAt(newS_ce.toString(), ci);
                    }
                    else {
                        newColExp.addElement(CustomizeUtil.objectNamesToQuotedIdentifier(s_ce, SwisSQLUtils.getKeywords("teradata"), null, -1));
                    }
                }
                else {
                    newColExp.addElement(s_ce);
                }
            }
            else {
                newColExp.addElement(colObj);
            }
        }
        final WhereColumn newWhereCol = new WhereColumn();
        newWhereCol.setColumnExpression(newColExp);
        wi.setRightWhereExp(newWhereCol);
    }
    
    private TableColumn generateTeradataSysCalendarConstructs(final SelectQueryStatement to_sqs, final WhereItem wi, final SelectColumn sc) throws ConvertException {
        final TableColumn tdSysCalColumn = new TableColumn();
        tdSysCalColumn.setColumnName("day_of_week");
        tdSysCalColumn.setTableName("calendar");
        tdSysCalColumn.setOwnerName("sys_calendar");
        final FromTable tdSysCalTable = new FromTable();
        tdSysCalTable.setTableName("sys_calendar.calendar");
        to_sqs.getFromClause().getFromItemList().add(tdSysCalTable);
        final WhereItem tdSysCalWi = new WhereItem();
        final TableColumn tdSysCalWeek = new TableColumn();
        tdSysCalWeek.setColumnName("calendar_date");
        tdSysCalWeek.setTableName("calendar");
        tdSysCalWeek.setOwnerName("sys_calendar");
        final WhereColumn tdlwe = new WhereColumn();
        final Vector tdlweExp = new Vector();
        tdlweExp.add(tdSysCalWeek);
        tdlwe.setColumnExpression(tdlweExp);
        tdSysCalWi.setLeftWhereExp(tdlwe);
        final WhereColumn tdrwe = new WhereColumn();
        final Vector tdrweExp = new Vector();
        tdrweExp.add(sc.toTeradataSelect(null, null));
        tdrwe.setColumnExpression(tdrweExp);
        tdSysCalWi.setRightWhereExp(tdrwe);
        tdSysCalWi.setOperator("=");
        wi.setTeradataSysCalendarWhereItem(tdSysCalWi);
        return tdSysCalColumn;
    }
    
    public void replaceRownumTableColumn(final Object newColumn) throws ConvertException {
        if (this.rownumClause != null) {
            this.rownumClause.setRownumClause(newColumn.toString());
        }
        if (this.leftWhereExp != null) {
            this.leftWhereExp.replaceRownumTableColumn(newColumn);
        }
        if (this.rightWhereExp != null) {
            this.rightWhereExp.replaceRownumTableColumn(newColumn);
        }
    }
    
    public WhereItem toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.movedToFromClause) {
            return null;
        }
        if (this.rownumClause != null) {
            return null;
        }
        final WhereItem wi = new WhereItem();
        wi.setOpenBrace(this.openBraces);
        wi.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            wi.setBeginOperator(this.beginOperator);
        }
        if (this.operator1 != null) {
            wi.setOperator1(this.operator1);
        }
        this.modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(true);
        this.modifyWhereItemsForEqualAndNotEqualsOperationWithEmptyStringOnRHS();
        if (this.leftWhereExp != null) {
            wi.setLeftWhereExp(this.leftWhereExp.toVectorWiseSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereExp != null) {
            wi.setRightWhereExp(this.rightWhereExp.toVectorWiseSelect(to_sqs, from_sqs));
        }
        if (this.rightWhereSubQuery != null) {
            wi.setRightWhereSubQuery(this.rightWhereSubQuery.toVectorWiseSelect());
            if (this.rightWhereSubQueryExp != null) {
                wi.setRightWhereSubQueryExp(this.rightWhereSubQueryExp.toVectorWiseSelect(to_sqs, from_sqs));
            }
        }
        if (this.operator != null) {
            this.modifyWhereExpForLikeOperator(wi, this.operator, "VARCHAR");
            if (this.operator.equalsIgnoreCase("^=")) {
                wi.setOperator("<>");
            }
            else if (this.operator.equalsIgnoreCase("!>")) {
                wi.setOperator("<=");
            }
            else if (this.operator.equalsIgnoreCase("!<")) {
                wi.setOperator(">=");
            }
            else if (this.operator.equalsIgnoreCase("<=>")) {
                if (from_sqs != null && from_sqs.canUseDistinctFromForNullSafeEqualsOperator()) {
                    wi.setOperator("IS NOT DISTINCT FROM");
                }
                else {
                    wi.setOperator("=");
                    wi.isNullSafeEqualsOperator = true;
                }
            }
            else if (this.operator.equalsIgnoreCase("~") | this.operator.equalsIgnoreCase("~*")) {
                wi.setOperator("LIKE");
                if (wi.getRightWhereSubQuery() != null) {
                    throw new ConvertException("Conversion failure.. Subquery can't be converted");
                }
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_ce = wc.getColumnExpression();
                final Vector v_nce = new Vector();
                if (v_ce != null) {
                    if (v_ce.size() != 1) {
                        throw new ConvertException("Conversion failure..Expressions can't be converted");
                    }
                    String s_ce = v_ce.elementAt(0).toString();
                    if (s_ce.charAt(0) == '\'') {
                        s_ce = s_ce.replace('\'', ' ').trim();
                        s_ce = "'%" + s_ce + "%'";
                    }
                    else if (s_ce.charAt(0) == '\"') {
                        s_ce = s_ce.replace('\"', ' ').trim();
                        s_ce = "\"%" + s_ce + "%\"";
                    }
                    else {
                        s_ce = "'%" + s_ce + "%'";
                    }
                    v_nce.addElement(s_ce);
                    wc.setColumnExpression(v_nce);
                }
                wi.setRightWhereExp(wc);
            }
            else if (this.operator.equalsIgnoreCase("MATCHES") || this.operator.equalsIgnoreCase("NOT MATCHES")) {
                if (this.operator.equalsIgnoreCase("MATCHES")) {
                    wi.setOperator("LIKE");
                }
                else {
                    wi.setOperator("NOT LIKE");
                }
                final WhereColumn whereColumn = wi.getRightWhereExp();
                final Vector columnExpression = whereColumn.getColumnExpression();
                final Vector newColumnExpression = new Vector();
                if (columnExpression != null) {
                    String ColumnExpressionAsString = columnExpression.elementAt(0).toString();
                    if (ColumnExpressionAsString.indexOf("*") != -1) {
                        ColumnExpressionAsString = ColumnExpressionAsString.replace('*', '%');
                    }
                    newColumnExpression.add(ColumnExpressionAsString);
                    whereColumn.setColumnExpression(newColumnExpression);
                }
            }
            else if ((this.operator.equalsIgnoreCase("IN") | this.operator.equalsIgnoreCase("NOT IN")) && wi.getRightWhereExp() != null) {
                final WhereColumn wc = wi.getRightWhereExp();
                final Vector v_nsce = new Vector();
                final Vector v_sce = wc.getColumnExpression();
                this.modifyUnionQueryAndReplaceAllFetchAndOrderByInsideINClause(wi);
                if (v_sce != null) {
                    if (v_sce.size() > 0 && !(v_sce.elementAt(0) instanceof String)) {
                        v_nsce.addElement("(");
                        for (int i = 0; i < v_sce.size(); ++i) {
                            v_nsce.addElement(v_sce.elementAt(i));
                        }
                        v_nsce.addElement(")");
                        wc.setColumnExpression(v_nsce);
                    }
                    final boolean canHandleNullsInsideINClause = from_sqs != null && from_sqs.canHandleNullsInsideINClause();
                    final Vector newColExp = this.replaceNULLWithIFNULLFn(wc.getColumnExpression(), canHandleNullsInsideINClause);
                    if (newColExp != null && !newColExp.isEmpty()) {
                        wc.setColumnExpression(newColExp);
                    }
                }
                wi.setOperator(this.operator);
            }
            else {
                wi.setOperator(this.operator);
            }
            this.modifyOperatorForNULL(wi, this.operator);
        }
        if (this.operator2 != null && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
            final WhereColumn r_wc = wi.getRightWhereExp();
            final WhereColumn l_wc = wi.getLeftWhereExp();
            if (r_wc != null && r_wc.getColumnExpression().size() != 1) {
                final Vector v_nlsce = new Vector();
                final Vector v_nrsce = new Vector();
                final Vector v_lsce = l_wc.getColumnExpression();
                final Vector v_rsce = r_wc.getColumnExpression();
                final Vector v_removedcomma_right_column_exp = new Vector();
                final Vector v_removedcomma_left_column_exp = new Vector();
                for (int j = 0; j < v_lsce.size(); ++j) {
                    if (v_lsce.elementAt(j) instanceof String) {
                        final String s_ce2 = v_lsce.elementAt(j);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(j));
                        }
                    }
                    else {
                        v_removedcomma_left_column_exp.addElement(v_lsce.elementAt(j));
                    }
                }
                for (int j = 0; j < v_rsce.size(); ++j) {
                    if (v_rsce.elementAt(j) instanceof String) {
                        final String s_ce2 = v_rsce.elementAt(j);
                        if (!s_ce2.equalsIgnoreCase(",") && !s_ce2.equalsIgnoreCase("(") && !s_ce2.equalsIgnoreCase(")")) {
                            v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(j));
                        }
                    }
                    else {
                        v_removedcomma_right_column_exp.addElement(v_rsce.elementAt(j));
                    }
                }
                v_nlsce.addElement(v_removedcomma_left_column_exp.elementAt(0));
                v_nrsce.addElement(v_removedcomma_right_column_exp.elementAt(0));
                l_wc.setColumnExpression(v_nlsce);
                r_wc.setColumnExpression(v_nrsce);
                for (int j = 1; j < v_removedcomma_right_column_exp.size(); ++j) {
                    final WhereItem n_wi = new WhereItem();
                    final WhereColumn l_nwc = new WhereColumn();
                    final WhereColumn r_nwc = new WhereColumn();
                    final Vector v_lnsc = new Vector();
                    final Vector v_rnsc = new Vector();
                    v_lnsc.addElement(v_removedcomma_left_column_exp.elementAt(0));
                    v_rnsc.addElement(v_removedcomma_right_column_exp.elementAt(j));
                    l_nwc.setColumnExpression(v_lnsc);
                    r_nwc.setColumnExpression(v_rnsc);
                    n_wi.setLeftWhereExp(l_nwc);
                    n_wi.setRightWhereExp(r_nwc);
                    n_wi.setOperator(this.operator);
                    if (j == v_lsce.size() - 1) {
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                    else {
                        if (this.operator2.equalsIgnoreCase("ALL")) {
                            from_sqs.getWhereExpression().addOperator("AND");
                        }
                        else {
                            from_sqs.getWhereExpression().addOperator("OR");
                        }
                        from_sqs.getWhereExpression().addWhereItem(n_wi);
                    }
                }
            }
        }
        if (wi.getRightWhereSubQuery() != null && this.operator2 != null) {
            wi.setOperator2(this.operator2);
        }
        if (this.operator3 != null) {
            wi.setOperator3(this.operator3);
        }
        if (this.LeftJoin != null) {
            wi.setLeftJoin(null);
        }
        if (this.RightJoin != null) {
            wi.setRightJoin(null);
        }
        return wi;
    }
    
    public void modifyWhereItemsForEqualAndNotEqualsOperationWithEmptyStringOnRHS() {
        if (this.operator != null && (this.operator.equals("=") || this.operator.equals("!=") || this.operator.equals("<>")) && this.rightWhereExp != null && this.rightWhereExp instanceof WhereColumn) {
            final Vector rightWhereColExp = this.rightWhereExp.getColumnExpression();
            if (this.isRightWhereExpContainsToken(rightWhereColExp) && rightWhereColExp.get(0) instanceof String && (rightWhereColExp.get(0).toString().replaceAll("\\s", "").equals("''") || rightWhereColExp.get(0).toString().replaceAll("\\s", "").equalsIgnoreCase("'null'")) && this.leftWhereExp != null) {
                final Vector newColumnExp = this.modifyColumnExpressionToCastExp(this.leftWhereExp.getColumnExpression(), "CHAR");
                if (newColumnExp != null) {
                    this.leftWhereExp.setColumnExpression(newColumnExp);
                }
            }
        }
    }
    
    public boolean isRightWhereExpContainsToken(final Vector colExp) {
        boolean isRightWhereExpContainsToken = false;
        try {
            if (colExp != null && (colExp.size() == 1 || (colExp.size() == 2 && ((colExp.get(1) instanceof Token && colExp.get(1).toString().startsWith("/*") && colExp.get(1).toString().endsWith("*/")) || (colExp.get(1) instanceof String && colExp.get(1).toString().isEmpty()))))) {
                isRightWhereExpContainsToken = true;
            }
        }
        catch (final Exception e) {
            isRightWhereExpContainsToken = false;
        }
        return isRightWhereExpContainsToken;
    }
    
    public void modifyWhereItemsForLikeOperatorWithoutConstantOnRHS(final boolean isVectorwise) {
        boolean castBothSides = false;
        final String dataType = "CHAR";
        if (this.operator != null && (this.operator.equalsIgnoreCase("like") || this.operator.equalsIgnoreCase("not like") || this.operator.equalsIgnoreCase("matches") || this.operator.equalsIgnoreCase("not matches"))) {
            String newOperator = "=";
            if (this.operator.equalsIgnoreCase("not like") || this.operator.equalsIgnoreCase("not matches")) {
                newOperator = "!=";
            }
            if (this.rightWhereExp != null && this.rightWhereExp instanceof WhereColumn) {
                final Vector rightWhereColExp = this.rightWhereExp.getColumnExpression();
                if (this.isRightWhereExpContainsToken(rightWhereColExp)) {
                    boolean convertToSubs = false;
                    int positionFound = 0;
                    final Object object = rightWhereColExp.get(0);
                    if (object instanceof TableColumn) {
                        this.operator = newOperator;
                        castBothSides = true;
                    }
                    else if (object instanceof FunctionCalls) {
                        boolean startsWith = false;
                        boolean endsWith = false;
                        final FunctionCalls fc = (FunctionCalls)object;
                        final Vector fnArgs = fc.getFunctionArguments();
                        final String functionName = fc.getFunctionName().getColumnName();
                        if (functionName.equalsIgnoreCase("CONCAT")) {
                            if (fnArgs != null) {
                                for (int i = 0; i < fnArgs.size(); ++i) {
                                    final Object argObj = fnArgs.get(i);
                                    if (argObj instanceof SelectColumn) {
                                        final SelectColumn sc = (SelectColumn)argObj;
                                        final Vector colExp = sc.getColumnExpression();
                                        if (colExp != null && colExp.size() == 1 && colExp.get(0) != null && colExp.get(0) instanceof String && colExp.get(0).toString().contains("%")) {
                                            if (!isVectorwise) {
                                                convertToSubs = true;
                                                break;
                                            }
                                            String str = colExp.get(0).toString();
                                            str = str.replaceAll("'", "");
                                            if (str.startsWith("%") && i == 0) {
                                                startsWith = true;
                                                convertToSubs = true;
                                            }
                                            else if (str.endsWith("%") && i + 1 == fnArgs.size()) {
                                                endsWith = true;
                                                convertToSubs = true;
                                            }
                                            positionFound = i;
                                        }
                                    }
                                }
                                if (convertToSubs) {
                                    if (isVectorwise && this.leftWhereExp != null && this.leftWhereExp instanceof WhereColumn) {
                                        if (startsWith && !endsWith) {
                                            this.modifyLeftWhereExpForLikeExpWithoutConstantPatternOnRHS(fc, "RIGHT");
                                            castBothSides = true;
                                        }
                                        else if (endsWith && !startsWith) {
                                            this.modifyLeftWhereExpForLikeExpWithoutConstantPatternOnRHS(fc, "LEFT");
                                            castBothSides = true;
                                        }
                                        else if (startsWith && endsWith) {
                                            this.modifyLikeExpUsingPositionFunctionForSearchingInBetweenString(fc);
                                        }
                                    }
                                }
                                else {
                                    this.operator = newOperator;
                                    castBothSides = true;
                                }
                            }
                        }
                        else if ((functionName.equalsIgnoreCase("CAST") && fnArgs.size() == 2) || functionName.equalsIgnoreCase("UPPER") || functionName.equalsIgnoreCase("LOWER")) {
                            if (fnArgs.get(0) instanceof SelectColumn) {
                                final SelectColumn sc2 = fnArgs.get(0);
                                final Vector colExp2 = sc2.getColumnExpression();
                                if (colExp2 != null && colExp2.size() == 1 && colExp2.get(0) != null) {
                                    if (colExp2.get(0) instanceof String && !colExp2.get(0).toString().contains("%")) {
                                        this.operator = newOperator;
                                        castBothSides = true;
                                    }
                                    else if (colExp2.get(0) instanceof TableColumn) {
                                        this.operator = newOperator;
                                        castBothSides = true;
                                    }
                                }
                            }
                            else if (fnArgs.get(0) instanceof TableColumn) {
                                this.operator = newOperator;
                                castBothSides = true;
                            }
                        }
                        else {
                            if (isVectorwise) {
                                this.operator = newOperator;
                            }
                            castBothSides = true;
                        }
                    }
                }
            }
        }
        if (castBothSides) {
            if (this.leftWhereExp != null) {
                final Vector newColumnExp = this.modifyColumnExpressionToCastExp(this.leftWhereExp.getColumnExpression(), "CHAR");
                if (newColumnExp != null) {
                    this.leftWhereExp.setColumnExpression(newColumnExp);
                }
            }
            if (this.rightWhereExp != null) {
                final Vector newColumnExp = this.modifyColumnExpressionToCastExp(this.rightWhereExp.getColumnExpression(), "CHAR");
                if (newColumnExp != null) {
                    this.rightWhereExp.setColumnExpression(newColumnExp);
                }
            }
        }
    }
    
    public void modifyLikeExpUsingPositionFunctionForSearchingInBetweenString(final FunctionCalls fc) {
        final FunctionCalls positionFC = new FunctionCalls();
        final TableColumn tc = new TableColumn();
        tc.setColumnName("LOCATE");
        positionFC.setFunctionName(tc);
        final SelectColumn sc2 = new SelectColumn();
        final Vector colExp2 = new Vector();
        final Vector newExp = this.modifyColumnExpressionToCastExp(this.leftWhereExp.getColumnExpression(), "CHAR");
        if (newExp != null) {
            colExp2.addAll(newExp);
        }
        else {
            colExp2.addAll(this.leftWhereExp.getColumnExpression());
        }
        sc2.setColumnExpression(colExp2);
        if (fc != null) {
            final Vector fnArgs = fc.getFunctionArguments();
            if (fnArgs != null) {
                for (int i = 0; i < fnArgs.size(); ++i) {
                    final Object element = fnArgs.get(i);
                    if (element instanceof SelectColumn) {
                        final SelectColumn sc3 = (SelectColumn)element;
                        final Vector colExpr = sc3.getColumnExpression();
                        if (colExpr != null && colExpr.size() == 1 && colExpr.get(0) != null && colExpr.get(0) instanceof String && colExpr.get(0).toString().contains("%")) {
                            final Vector ce = new Vector();
                            ce.add(colExpr.get(0).toString().replaceAll("%", ""));
                            sc3.setColumnExpression(ce);
                        }
                    }
                }
            }
        }
        final SelectColumn sc4 = new SelectColumn();
        final Vector colExp3 = new Vector();
        colExp3.add(fc);
        sc4.setColumnExpression(colExp3);
        final Vector fnArgs2 = new Vector();
        fnArgs2.add(sc4);
        fnArgs2.add(sc2);
        positionFC.setFunctionArguments(fnArgs2);
        final Vector finalExp = new Vector();
        finalExp.add(positionFC);
        this.leftWhereExp.setColumnExpression(finalExp);
        this.operator = ">";
        final Vector rightExp = new Vector();
        rightExp.add("0");
        this.rightWhereExp.setColumnExpression(rightExp);
    }
    
    public void modifyLeftWhereExpForLikeExpWithoutConstantPatternOnRHS(final FunctionCalls fc, final String functionNameToChange) {
        final FunctionCalls rightFC = new FunctionCalls();
        final TableColumn tc = new TableColumn();
        tc.setColumnName(functionNameToChange);
        rightFC.setFunctionName(tc);
        final SelectColumn selCol = new SelectColumn();
        final Vector colExp = new Vector();
        colExp.addAll(this.leftWhereExp.getColumnExpression());
        selCol.setColumnExpression(colExp);
        final FunctionCalls lengthFC = new FunctionCalls();
        final TableColumn tc2 = new TableColumn();
        tc2.setColumnName("LENGTH");
        lengthFC.setFunctionName(tc2);
        if (fc != null) {
            final Vector fnArgs = fc.getFunctionArguments();
            if (fnArgs != null) {
                for (int i = 0; i < fnArgs.size(); ++i) {
                    final Object element = fnArgs.get(i);
                    if (element instanceof SelectColumn) {
                        final SelectColumn sc = (SelectColumn)element;
                        final Vector colExpr = sc.getColumnExpression();
                        if (colExpr != null && colExpr.size() == 1 && colExpr.get(0) != null && colExpr.get(0) instanceof String && colExpr.get(0).toString().contains("%")) {
                            final Vector ce = new Vector();
                            ce.add(colExpr.get(0).toString().replaceAll("%", ""));
                            sc.setColumnExpression(ce);
                        }
                    }
                }
            }
        }
        final Vector colExp2 = new Vector();
        colExp2.add(fc);
        lengthFC.setFunctionArguments(colExp2);
        final SelectColumn selCol2 = new SelectColumn();
        final Vector colExp3 = new Vector();
        colExp3.add(lengthFC);
        selCol2.setColumnExpression(colExp3);
        final Vector colExp4 = new Vector();
        colExp4.add(selCol);
        colExp4.add(selCol2);
        rightFC.setFunctionArguments(colExp4);
        final Vector finalExp = new Vector();
        finalExp.add(rightFC);
        this.leftWhereExp.setColumnExpression(finalExp);
        this.operator = "=";
    }
    
    public boolean checkForCasting(final Vector colExp) {
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
                else {
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
    
    public Vector modifyColumnExpressionToCastExp(final Vector colExp, final String dataType) {
        Vector newColumnExpr = null;
        try {
            if (this.checkForCasting(colExp)) {
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
                newColumnExpr = new Vector();
                newColumnExpr.add(0, castFunction);
            }
        }
        catch (final Exception ex) {}
        return newColumnExpr;
    }
    
    public void modifyWhereExpForLikeOperator(final WhereItem wi, final String operator, final String dataType) {
        if (wi != null && wi.getLeftWhereExp() != null && wi.getLeftWhereExp().getColumnExpression() != null) {
            final WhereColumn whereLeftExp = wi.getLeftWhereExp();
            final Vector whereLeftColExpOld = whereLeftExp.getColumnExpression();
            try {
                if ((operator.equalsIgnoreCase("LIKE") || operator.equalsIgnoreCase("NOT LIKE") || operator.equalsIgnoreCase("MATCHES") || operator.equalsIgnoreCase("NOT MATCHES") || (operator.equalsIgnoreCase("~") | operator.equalsIgnoreCase("~*"))) && this.checkForCasting(whereLeftColExpOld)) {
                    final Vector newColumnExpr = this.modifyColumnExpressionToCastExp(whereLeftColExpOld, dataType);
                    if (newColumnExpr != null) {
                        whereLeftExp.setColumnExpression(newColumnExpr);
                    }
                }
            }
            catch (final Exception e) {
                if (whereLeftColExpOld != null) {
                    whereLeftExp.setColumnExpression(whereLeftColExpOld);
                }
            }
        }
    }
    
    public void modifyOperatorForNULL(final WhereItem wi, final String operator) {
        Vector oldExp = null;
        try {
            if ((operator.equalsIgnoreCase("=") || operator.equalsIgnoreCase("!=") || operator.equalsIgnoreCase("<>")) && wi.getRightWhereExp() != null && wi.getRightWhereExp() instanceof WhereColumn && this.isRightWhereExpContainsToken(wi.getRightWhereExp().getColumnExpression()) && wi.getRightWhereExp().getColumnExpression().get(0) instanceof String && wi.getRightWhereExp().getColumnExpression().get(0).toString().equalsIgnoreCase("null")) {
                oldExp = wi.getRightWhereExp().getColumnExpression();
                wi.getRightWhereExp().setColumnExpression(this.getColumnExpressionForNULL());
            }
        }
        catch (final Exception e) {
            if (oldExp != null) {
                wi.getRightWhereExp().setColumnExpression(oldExp);
            }
        }
    }
    
    public Vector replaceNULLWithIFNULLFn(final Vector colExp, final boolean canHandleNullsInsideINClause) {
        Vector newColExp = null;
        try {
            if (colExp != null && colExp.size() > 0) {
                final Vector removeItems = new Vector();
                for (int i = 0; i < colExp.size(); ++i) {
                    if (colExp.get(i) instanceof WhereColumn) {
                        final WhereColumn wherCol = colExp.get(i);
                        final Vector whereColExp = wherCol.getColumnExpression();
                        try {
                            if (whereColExp != null && whereColExp.size() == 1 && ((whereColExp.get(0) instanceof String && (whereColExp.get(0).toString().equalsIgnoreCase("null") || whereColExp.get(0).toString().equalsIgnoreCase("coalesce(null)"))) || (whereColExp.get(0) instanceof SelectColumn && whereColExp.get(0).getColumnExpression() != null && whereColExp.get(0).getColumnExpression().size() == 1 && (whereColExp.get(0).getColumnExpression().get(0).toString().equalsIgnoreCase("null") || whereColExp.get(0).getColumnExpression().get(0).toString().equalsIgnoreCase("coalesce(null)"))))) {
                                removeItems.add(i);
                            }
                        }
                        catch (final Exception e) {
                            removeItems.clear();
                            break;
                        }
                    }
                }
                try {
                    if (!removeItems.isEmpty()) {
                        if (removeItems.size() == 1) {
                            final int index = Integer.parseInt(removeItems.get(0).toString());
                            final Object exp = colExp.get(index);
                            if (exp instanceof WhereColumn) {
                                ((WhereColumn)exp).setColumnExpression(this.getColumnExpressionForNULL());
                            }
                            else if (exp instanceof String) {
                                colExp.setElementAt("COALESCE(NULL)", index);
                            }
                        }
                        else if (canHandleNullsInsideINClause) {
                            final Vector tempColExp = new Vector();
                            for (int j = 0; j < colExp.size(); ++j) {
                                if (!removeItems.contains(j) && (!(colExp.get(j) instanceof String) || (!colExp.get(j).toString().isEmpty() && !colExp.get(j).toString().equals(",") && !colExp.get(j).toString().equals("(") && !colExp.get(j).toString().equals(")")))) {
                                    tempColExp.add(colExp.get(j));
                                }
                            }
                            if (tempColExp.isEmpty()) {
                                newColExp = new Vector();
                                newColExp.add("(");
                                newColExp.add("COALESCE(NULL)");
                                newColExp.add(")");
                            }
                            else {
                                newColExp = new Vector();
                                newColExp.add("(");
                                for (int k = 0; k < tempColExp.size(); ++k) {
                                    if (k > 0) {
                                        newColExp.add(",");
                                    }
                                    newColExp.add(tempColExp.get(k));
                                }
                                newColExp.add(")");
                            }
                        }
                    }
                }
                catch (final Exception e2) {
                    newColExp = null;
                }
            }
        }
        catch (final Exception e3) {
            newColExp = null;
        }
        return newColExp;
    }
    
    public Vector getColumnExpressionForNULL() throws Exception {
        final ifnull ifnullfunction = new ifnull();
        final TableColumn tc1 = new TableColumn();
        tc1.setColumnName("COALESCE");
        ifnullfunction.setFunctionName(tc1);
        final Vector newFunctionArgs = new Vector();
        final Vector nullExp = new Vector();
        nullExp.add("NULL");
        final SelectColumn sc = new SelectColumn();
        sc.setColumnExpression(nullExp);
        newFunctionArgs.add(0, sc);
        ifnullfunction.setFunctionArguments(newFunctionArgs);
        final Vector newColumnExpr = new Vector();
        newColumnExpr.add(0, ifnullfunction);
        return newColumnExpr;
    }
    
    public void modifyUnionQueryAndReplaceAllFetchAndOrderByInsideINClause(final WhereItem wi) {
        try {
            if (wi.getRightWhereSubQuery() != null && wi.getRightWhereSubQuery() instanceof SelectQueryStatement) {
                if (wi.getRightWhereSubQuery().getSetOperatorClause() != null) {
                    final SelectQueryStatement selectQueryStatement = new SelectQueryStatement();
                    final SelectStatement selectStatement = new SelectStatement();
                    selectStatement.setSelectClause("SELECT");
                    final SelectColumn selectColumn = new SelectColumn();
                    final Vector selectItemList = new Vector();
                    final Vector selectColExp = new Vector();
                    selectColExp.add("*");
                    selectColumn.setColumnExpression(selectColExp);
                    selectItemList.add(selectColumn);
                    selectStatement.setSelectItemList(selectItemList);
                    final Vector fromItemList = new Vector();
                    final FromTable fromTable = new FromTable();
                    fromTable.setTableName(wi.getRightWhereSubQuery());
                    fromTable.setAliasName("T0_ALIAS");
                    fromTable.setIsAS(true);
                    fromItemList.add(fromTable);
                    final FromClause fromClause = new FromClause();
                    fromClause.setFromClause("FROM");
                    fromClause.setFromItemList(fromItemList);
                    selectQueryStatement.setSelectStatement(selectStatement);
                    selectQueryStatement.setFromClause(fromClause);
                    wi.setRightWhereSubQuery(selectQueryStatement);
                }
                final SelectQueryStatement queryStatement = wi.getRightWhereSubQuery();
                queryStatement.setFetchClause(null);
                queryStatement.setLimitClause(null);
                queryStatement.setOrderByStatement(null);
                if (queryStatement.getFromClause() != null && queryStatement.getFromClause().getFromItemList() != null && queryStatement.getFromClause().getFromItemList().size() > 0) {
                    final Vector fromItemList2 = queryStatement.getFromClause().getFromItemList();
                    for (int i = 0; i < fromItemList2.size(); ++i) {
                        final Object fromTable2 = fromItemList2.get(i);
                        if (fromTable2 instanceof FromTable && ((FromTable)fromTable2).getTableName() instanceof SelectQueryStatement) {
                            final SelectQueryStatement qStatement = (SelectQueryStatement)((FromTable)fromTable2).getTableName();
                            qStatement.setFetchClause(null);
                            qStatement.setLimitClause(null);
                            qStatement.setOrderByStatement(null);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {}
    }
}
