package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import java.util.Vector;
import java.util.Set;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;

public class SetOperatorClause
{
    private String SetClause;
    private SelectQueryStatement query_statement;
    private String OpenBrace;
    private String CloseBrace;
    private WhereExpression whereExpression;
    private boolean checkSetOperator;
    private UserObjectContext context;
    private CommentClass commentObj;
    
    public SetOperatorClause() {
        this.whereExpression = null;
        this.context = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setSetClause(final String s_sc) {
        this.SetClause = s_sc;
    }
    
    public void setSelectQueryStatement(final SelectQueryStatement q_qs) {
        this.query_statement = q_qs;
    }
    
    public void setOpenBrace(final String s_ob) {
        this.OpenBrace = s_ob;
    }
    
    public void setCloseBrace(final String s_cb) {
        this.CloseBrace = s_cb;
    }
    
    public void setCheckSetOperator(final boolean b_cso) {
        this.checkSetOperator = b_cso;
    }
    
    public void setWhereExpression(final WhereExpression we) {
        this.whereExpression = we;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public boolean getCheckSetOperator() {
        return this.checkSetOperator;
    }
    
    public String getSetClause() {
        return this.SetClause;
    }
    
    public SelectQueryStatement getSelectQueryStatement() {
        return this.query_statement;
    }
    
    public String getOpenBrace() {
        return this.OpenBrace;
    }
    
    public String getCloseBrace() {
        return this.CloseBrace;
    }
    
    public SetOperatorClause toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SetOperatorClause soc = new SetOperatorClause();
        soc.setOpenBrace(this.OpenBrace);
        if (this.SetClause.equalsIgnoreCase("MINUS")) {
            soc.setSetClause("EXCEPT");
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        soc.setSelectQueryStatement(this.query_statement.toANSISelect());
        soc.setCloseBrace(this.CloseBrace);
        this.checkSetOperator = true;
        return soc;
    }
    
    public SetOperatorClause toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SetOperatorClause soc = new SetOperatorClause();
        soc.setOpenBrace(this.OpenBrace);
        if (this.SetClause.equalsIgnoreCase("MINUS")) {
            soc.setSetClause("EXCEPT");
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        if (from_sqs != null && this.query_statement != null) {
            this.query_statement.setTopLevel(from_sqs.getTopLevel());
        }
        soc.setSelectQueryStatement(this.query_statement.toTeradataSelect());
        soc.setCloseBrace(this.CloseBrace);
        this.checkSetOperator = true;
        return soc;
    }
    
    public SetOperatorClause toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SetOperatorClause soc = new SetOperatorClause();
        soc.setOpenBrace(this.OpenBrace);
        if (this.SetClause.equalsIgnoreCase("MINUS")) {
            soc.setSetClause("EXCEPT");
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        soc.setSelectQueryStatement(this.query_statement.toDB2Select());
        soc.setCloseBrace(this.CloseBrace);
        this.checkSetOperator = true;
        return soc;
    }
    
    public SetOperatorClause toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SetOperatorClause soc = new SetOperatorClause();
        soc.setOpenBrace(this.OpenBrace);
        if (this.SetClause.equalsIgnoreCase("MINUS") | this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
            soc.setSetClause("EXCEPT");
        }
        else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
            soc.setSetClause("INTERSECT");
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        final boolean fromSQSISNotNull = from_sqs != null;
        if (fromSQSISNotNull) {
            this.query_statement.addAllIndexPositionsForStringLiterals(from_sqs.getIndexPositionsForStringLiterals());
            this.query_statement.addAllIndexPositionsForNULLString(from_sqs.getIndexPositionsForNULLString());
        }
        this.query_statement.setSetOperatorQuery(true);
        if (fromSQSISNotNull) {
            this.query_statement.setAmazonRedShiftFlag(from_sqs.isAmazonRedShift());
            this.query_statement.setMSAzureFlag(from_sqs.isMSAzure());
            this.query_statement.setOracleLiveFlag(from_sqs.isOracleLive());
            this.query_statement.setCanUseIFFunctionForPGCaseWhenExp(from_sqs.canUseIFFunctionForPGCaseWhenExp());
            this.query_statement.setCanUseUDFFunctionsForText(from_sqs.canUseUDFFunctionsForText());
            this.query_statement.setCanUseUDFFunctionsForNumeric(from_sqs.canUseUDFFunctionsForNumeric());
            this.query_statement.setCanUseUDFFunctionsForDateTime(from_sqs.canUseUDFFunctionsForDateTime());
            this.query_statement.setCanHandleStringLiteralsForNumeric(from_sqs.canHandleStringLiteralsForNumeric());
            this.query_statement.setCanHandleStringLiteralsForDateTime(from_sqs.canHandleStringLiteralsForDateTime());
            this.query_statement.setCanHandleNullsInsideINClause(from_sqs.canHandleNullsInsideINClause());
            this.query_statement.setCanCastStringLiteralToText(from_sqs.canCastStringLiteralToText());
            this.query_statement.setRemovalOptionForOrderAndFetchClauses(from_sqs.getRemovalOptionForOrderAndFetchClauses());
            this.query_statement.setCanUseDistinctFromForNullSafeEqualsOperator(from_sqs.canUseDistinctFromForNullSafeEqualsOperator());
            this.query_statement.setCanHandleHavingWithoutGroupBy(from_sqs.canHandleHavingWithoutGroupBy());
            this.query_statement.setCanUseUDFFunctionsForStrToDate(from_sqs.canUseUDFFunctionsForStrToDate());
        }
        final SelectQueryStatement sqs = this.query_statement.toPostgreSQLSelect();
        if (fromSQSISNotNull) {
            sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
            sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
            from_sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
            final Set nullSet = from_sqs.getIndexPositionsForNULLString();
            nullSet.clear();
            from_sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
        }
        soc.setSelectQueryStatement(sqs);
        soc.setCloseBrace(this.CloseBrace);
        this.checkSetOperator = true;
        return soc;
    }
    
    public SetOperatorClause toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        SetOperatorClause soc = new SetOperatorClause();
        final WhereExpression we = new WhereExpression();
        final WhereItem wi = new WhereItem();
        if (from_sqs != null) {
            this.query_statement.setCanHandleFunctionArgumentsCountMismatch(from_sqs.getCanHandleFunctionArugmentsCountMismatch());
            this.query_statement.setValidationHandler(from_sqs.getValidationHandler());
            this.query_statement.setcanAllowLogicalExpInAggFun(from_sqs.getcanAllowLogicalExpInAggFun());
            this.query_statement.setCanAllowBackTipInColumnName(from_sqs.getCanAllowBackTipInColumnName());
            this.query_statement.setCanReplaceDoubleDotsInTableName(from_sqs.getCanReplaceDoubleDotsInTableName());
        }
        final SelectQueryStatement sqs = this.query_statement.toMySQLSelect();
        sqs.setCanHandleFunctionArgumentsCountMismatch(this.query_statement.getCanHandleFunctionArugmentsCountMismatch());
        sqs.setValidationHandler(this.query_statement.getValidationHandler());
        sqs.setcanAllowLogicalExpInAggFun(this.query_statement.getcanAllowLogicalExpInAggFun());
        sqs.setCanAllowBackTipInColumnName(this.query_statement.getCanAllowBackTipInColumnName());
        soc.setSelectQueryStatement(sqs);
        soc.setOpenBrace(this.OpenBrace);
        soc.setCloseBrace(this.CloseBrace);
        if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
            if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
                wi.setOperator("EXISTS");
            }
            else {
                wi.setOperator("NOT EXISTS");
                final Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
                for (int qListSize = query_statementFromTableList.size(), qi = 0; qi < qListSize; ++qi) {
                    final FromTable fromTableObj = query_statementFromTableList.get(qi);
                    if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                        fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
                    }
                }
            }
            if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
                sqs.getWhereExpression().addOperator("AND");
                sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            else {
                sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            wi.setRightWhereSubQuery(sqs);
            we.addWhereItem(wi);
            if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereExpression(we);
            }
            else {
                to_sqs.setWhereExpression(we);
            }
            if (to_sqs.getSelectStatement() != null) {
                to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
            }
            soc = null;
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        this.checkSetOperator = true;
        return soc;
    }
    
    public SetOperatorClause toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SetOperatorClause soc = new SetOperatorClause();
        soc.setCommentClass(this.commentObj);
        soc.setOpenBrace(this.OpenBrace);
        if (this.SetClause.equalsIgnoreCase("EXCEPT") | this.SetClause.equalsIgnoreCase("EXCEPT ALL")) {
            soc.setSetClause("MINUS");
        }
        else if (this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
            soc.setSetClause("INTERSECT");
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        this.query_statement.setObjectContext(this.context);
        soc.setSelectQueryStatement(this.query_statement.toOracleSelect());
        soc.setCloseBrace(this.CloseBrace);
        soc.setObjectContext(this.context);
        this.checkSetOperator = true;
        return soc;
    }
    
    public SetOperatorClause toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        SetOperatorClause soc = new SetOperatorClause();
        final WhereExpression we = new WhereExpression();
        final WhereItem wi = new WhereItem();
        final SelectQueryStatement sqs = this.query_statement.toMSSQLServerSelect();
        soc.setSelectQueryStatement(sqs);
        soc.setOpenBrace(this.OpenBrace);
        soc.setCloseBrace(this.CloseBrace);
        if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
            if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
                wi.setOperator("EXISTS");
            }
            else {
                wi.setOperator("NOT EXISTS");
                final Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
                for (int qListSize = query_statementFromTableList.size(), qi = 0; qi < qListSize; ++qi) {
                    final FromTable fromTableObj = query_statementFromTableList.get(qi);
                    if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                        fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
                    }
                }
            }
            if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
                sqs.getWhereExpression().addOperator("AND");
                sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            else {
                sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            wi.setRightWhereSubQuery(sqs);
            we.addWhereItem(wi);
            if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereExpression(we);
            }
            else {
                to_sqs.setWhereExpression(we);
            }
            if (to_sqs.getSelectStatement() != null) {
                to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
            }
            soc = null;
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        this.checkSetOperator = true;
        return soc;
    }
    
    public SetOperatorClause toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        SetOperatorClause soc = new SetOperatorClause();
        soc.setObjectContext(this.context);
        final WhereExpression we = new WhereExpression();
        final WhereItem wi = new WhereItem();
        final SelectQueryStatement sqs = this.query_statement.toSybaseSelect();
        sqs.setObjectContext(this.context);
        we.setObjectContext(this.context);
        wi.setObjectContext(this.context);
        soc.setSelectQueryStatement(sqs);
        soc.setOpenBrace(this.OpenBrace);
        soc.setCloseBrace(this.CloseBrace);
        if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
            if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
                wi.setOperator("EXISTS");
            }
            else {
                wi.setOperator("NOT EXISTS");
                final Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
                for (int qListSize = query_statementFromTableList.size(), qi = 0; qi < qListSize; ++qi) {
                    final FromTable fromTableObj = query_statementFromTableList.get(qi);
                    if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                        fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
                    }
                }
            }
            if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
                sqs.getWhereExpression().addOperator("AND");
                sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            else {
                sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            wi.setRightWhereSubQuery(sqs);
            we.addWhereItem(wi);
            if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereExpression(we);
            }
            else {
                to_sqs.setWhereExpression(we);
            }
            if (to_sqs.getSelectStatement() != null) {
                to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
            }
            soc = null;
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        this.checkSetOperator = true;
        return soc;
    }
    
    public SetOperatorClause toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        SetOperatorClause soc = new SetOperatorClause();
        final WhereExpression we = new WhereExpression();
        final WhereItem wi = new WhereItem();
        final SelectQueryStatement sqs = this.query_statement.toInformixSelect();
        soc.setSelectQueryStatement(sqs);
        soc.setOpenBrace(this.OpenBrace);
        soc.setCloseBrace(this.CloseBrace);
        if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
            if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
                sqs.getWhereExpression().addOperator("AND");
                sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            else {
                sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
                wi.setOperator("EXISTS");
            }
            else {
                wi.setOperator("NOT EXISTS");
            }
            wi.setRightWhereSubQuery(sqs);
            we.addWhereItem(wi);
            if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereExpression(we);
            }
            else {
                to_sqs.setWhereExpression(we);
            }
            if (to_sqs.getSelectStatement() != null) {
                to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
            }
            soc = null;
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        this.checkSetOperator = true;
        return soc;
    }
    
    public SetOperatorClause toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nUnsupported SQL in TimesTen 5.1.21\n");
    }
    
    public SetOperatorClause toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SetOperatorClause soc = new SetOperatorClause();
        soc.setOpenBrace(this.OpenBrace);
        if (this.SetClause.equalsIgnoreCase("MINUS")) {
            soc.setSetClause("EXCEPT");
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        soc.setSelectQueryStatement(this.query_statement.toNetezzaSelect());
        soc.setCloseBrace(this.CloseBrace);
        this.checkSetOperator = true;
        return soc;
    }
    
    public WhereExpression createWhereExp(final SelectQueryStatement to_sqs, final SelectQueryStatement sqs) throws ConvertException {
        final Vector v_sil = to_sqs.getSelectStatement().getSelectItemList();
        final Vector v_scsil = sqs.getSelectStatement().getSelectItemList();
        Vector v_fil = new Vector();
        Vector v_scfil = new Vector();
        String t_name = null;
        String t_scname = null;
        if (to_sqs.getFromClause() != null) {
            v_fil = to_sqs.getFromClause().getFromItemList();
        }
        if (this.query_statement.getFromClause() != null) {
            v_scfil = sqs.getFromClause().getFromItemList();
        }
        if (v_sil.size() != v_scsil.size()) {
            throw new ConvertException("incorrect number of columns");
        }
        final FromTable ft_i = v_fil.elementAt(0);
        final FromTable ft_sci = v_scfil.elementAt(0);
        if (ft_i.getAliasName() != null) {
            t_name = ft_i.getAliasName();
        }
        else if (ft_i.getTableName() instanceof String) {
            t_name = (String)ft_i.getTableName();
        }
        if (ft_sci.getAliasName() != null) {
            t_scname = ft_sci.getAliasName();
        }
        else if (ft_sci.getTableName() instanceof String) {
            t_scname = (String)ft_sci.getTableName();
        }
        final WhereExpression we = new WhereExpression();
        for (int i_count = 0; i_count < v_sil.size(); ++i_count) {
            final WhereItem wi = new WhereItem();
            final Vector v_lwi = new Vector();
            if (v_sil.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = v_sil.get(i_count);
                if (sc.toString().indexOf(".") != -1) {
                    final WhereColumn wc_new = new WhereColumn();
                    final Vector containsSelectColumn = sc.getColumnExpression();
                    wc_new.setColumnExpression(containsSelectColumn);
                    wi.setLeftWhereExp(wc_new);
                }
                else {
                    final WhereColumn wc_new = this.createColumn(v_sil.elementAt(i_count), t_name);
                    wi.setLeftWhereExp(wc_new);
                }
            }
            if (v_scsil.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = v_scsil.get(i_count);
                if (sc.toString().indexOf(".") != -1) {
                    final WhereColumn wc_new = new WhereColumn();
                    final Vector containsSelectColumn = sc.getColumnExpression();
                    wc_new.setColumnExpression(containsSelectColumn);
                    wi.setRightWhereExp(wc_new);
                }
                else {
                    final WhereColumn wc_new = this.createColumn(v_scsil.elementAt(i_count), t_scname);
                    wi.setRightWhereExp(wc_new);
                }
            }
            wi.setOperator("=");
            if (i_count == v_sil.size() - 1) {
                we.addWhereItem(wi);
            }
            else {
                we.addWhereItem(wi);
                we.addOperator("AND");
            }
        }
        return we;
    }
    
    public WhereColumn createColumn(final SelectColumn sc, final String tn) {
        final Vector v_ce = sc.getColumnExpression();
        final WhereColumn wc_new = new WhereColumn();
        final Vector v_nce = new Vector();
        for (int i_count = 0; i_count < v_ce.size(); ++i_count) {
            if (v_ce.elementAt(i_count) instanceof String) {
                final String st = tn + "." + v_ce.elementAt(i_count);
                v_nce.addElement(st);
            }
            else if (v_ce.elementAt(i_count) instanceof TableColumn) {
                final TableColumn tc = v_ce.elementAt(i_count);
                tc.setTableName(tn);
                v_nce.addElement(tc);
            }
            else if (v_ce.elementAt(i_count) instanceof FunctionCalls) {
                final FunctionCalls fc = v_ce.elementAt(i_count);
                final TableColumn tc2 = fc.getFunctionName();
                if (tc2 != null) {
                    tc2.setTableName(tn);
                }
                v_nce.addElement(fc);
            }
            else {
                v_nce.addElement(this.createColumn(v_ce.elementAt(i_count), tn));
            }
        }
        wc_new.setColumnExpression(v_nce);
        return wc_new;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.whereExpression != null) {
            sb.append("  WHERE  " + this.whereExpression);
        }
        for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
            sb.append("\t");
        }
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        sb.append(this.SetClause.toUpperCase());
        if (this.OpenBrace != null) {
            sb.append(" " + this.OpenBrace);
        }
        sb.append("\n ");
        this.query_statement.setObjectContext(this.context);
        sb.append(this.query_statement.toString());
        if (this.CloseBrace != null) {
            sb.append(this.CloseBrace);
        }
        return sb.toString();
    }
    
    public SetOperatorClause toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        SetOperatorClause soc = new SetOperatorClause();
        final WhereExpression we = new WhereExpression();
        final WhereItem wi = new WhereItem();
        this.query_statement.setSetOperatorQuery(true);
        final boolean isFromSQSNotNull = from_sqs != null;
        if (isFromSQSNotNull) {
            this.query_statement.addAllIndexPositionsForStringLiterals(from_sqs.getIndexPositionsForStringLiterals());
            this.query_statement.addAllIndexPositionsForNULLString(from_sqs.getIndexPositionsForNULLString());
            this.query_statement.setAmazonRedShiftFlag(from_sqs.isAmazonRedShift());
            this.query_statement.setMSAzureFlag(from_sqs.isMSAzure());
            this.query_statement.setOracleLiveFlag(from_sqs.isOracleLive());
            this.query_statement.setCanUseIFFunctionForPGCaseWhenExp(from_sqs.canUseIFFunctionForPGCaseWhenExp());
            this.query_statement.setCanUseUDFFunctionsForText(from_sqs.canUseUDFFunctionsForText());
            this.query_statement.setCanUseUDFFunctionsForNumeric(from_sqs.canUseUDFFunctionsForNumeric());
            this.query_statement.setCanUseUDFFunctionsForDateTime(from_sqs.canUseUDFFunctionsForDateTime());
            this.query_statement.setCanHandleStringLiteralsForNumeric(from_sqs.canHandleStringLiteralsForNumeric());
            this.query_statement.setCanHandleStringLiteralsForDateTime(from_sqs.canHandleStringLiteralsForDateTime());
            this.query_statement.setCanHandleNullsInsideINClause(from_sqs.canHandleNullsInsideINClause());
            this.query_statement.setCanCastStringLiteralToText(from_sqs.canCastStringLiteralToText());
            this.query_statement.setRemovalOptionForOrderAndFetchClauses(from_sqs.getRemovalOptionForOrderAndFetchClauses());
            this.query_statement.setCanUseDistinctFromForNullSafeEqualsOperator(from_sqs.canUseDistinctFromForNullSafeEqualsOperator());
            this.query_statement.setCanHandleHavingWithoutGroupBy(from_sqs.canHandleHavingWithoutGroupBy());
        }
        final SelectQueryStatement sqs = this.query_statement.toVectorWiseSelect();
        if (isFromSQSNotNull) {
            sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
            sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
            final Set nullSet = from_sqs.getIndexPositionsForNULLString();
            nullSet.clear();
            from_sqs.addAllIndexPositionsForStringLiterals(this.query_statement.getIndexPositionsForStringLiterals());
            from_sqs.addAllIndexPositionsForNULLString(this.query_statement.getIndexPositionsForNULLString());
        }
        soc.setSelectQueryStatement(sqs);
        soc.setOpenBrace(this.OpenBrace);
        soc.setCloseBrace(this.CloseBrace);
        if (!this.SetClause.equalsIgnoreCase("UNION") && !this.SetClause.equalsIgnoreCase("UNION ALL")) {
            if (this.SetClause.equalsIgnoreCase("INTERSECT") | this.SetClause.equalsIgnoreCase("INTERSECT ALL")) {
                wi.setOperator("EXISTS");
            }
            else {
                wi.setOperator("NOT EXISTS");
                final Vector query_statementFromTableList = sqs.getFromClause().getFromItemList();
                for (int qListSize = query_statementFromTableList.size(), qi = 0; qi < qListSize; ++qi) {
                    final FromTable fromTableObj = query_statementFromTableList.get(qi);
                    if (fromTableObj.getAliasName() == null || fromTableObj.getAliasName().equalsIgnoreCase(" ")) {
                        fromTableObj.setAliasName("ADV_INNER_ALIAS_" + qi);
                    }
                }
            }
            if (sqs.getWhereExpression() != null && !sqs.getWhereExpression().toString().trim().equals("")) {
                sqs.getWhereExpression().addOperator("AND");
                sqs.getWhereExpression().addWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            else {
                sqs.setWhereExpression(this.createWhereExp(to_sqs, sqs));
            }
            wi.setRightWhereSubQuery(sqs);
            we.addWhereItem(wi);
            if (to_sqs.getWhereExpression() != null && !to_sqs.getWhereExpression().toString().trim().equals("")) {
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereExpression(we);
            }
            else {
                to_sqs.setWhereExpression(we);
            }
            if (to_sqs.getSelectStatement() != null) {
                to_sqs.getSelectStatement().setSelectQualifier("DISTINCT");
            }
            soc = null;
        }
        else {
            soc.setSetClause(this.SetClause);
        }
        this.checkSetOperator = true;
        return soc;
    }
}
