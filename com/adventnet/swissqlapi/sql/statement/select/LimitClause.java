package com.adventnet.swissqlapi.sql.statement.select;

import java.util.Iterator;
import java.util.Set;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import java.util.Map;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.SwisSQLAPI;
import java.util.Hashtable;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.sql.statement.CommentClass;

public class LimitClause
{
    public String limitClause;
    public String limitValue;
    public String offsetClause;
    public String offsetStart;
    public String RowOnlyClause;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    
    public void setLimitClause(final String s_lc) {
        this.limitClause = s_lc;
    }
    
    public void setLimitValue(final String s_lv) {
        this.limitValue = s_lv;
    }
    
    public void setOffSetClause(final String s_osc) {
        this.offsetClause = s_osc;
    }
    
    public void setOffSetStart(final String s_os) {
        this.offsetStart = s_os;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass commentObj) {
        this.commentObjAfterToken = commentObj;
    }
    
    public void setRowOnlyClause(final String s_roc) {
        this.RowOnlyClause = s_roc;
    }
    
    public String getLimitValue() {
        return this.limitValue;
    }
    
    public String getOffSetClause() {
        return this.offsetClause;
    }
    
    public String getOffSetStart() {
        return this.offsetStart;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public CommentClass getCommentClassAfterToken() {
        return this.commentObjAfterToken;
    }
    
    public String getRowOnlyClause() {
        return this.RowOnlyClause;
    }
    
    public void addCommentClassAfterToken(Token commentObj) {
        if (this.commentObjAfterToken != null) {
            final ArrayList specialTokenList = this.commentObjAfterToken.getSpecialToken();
            final int lastIndex = specialTokenList.size();
            while (commentObj.specialToken != null) {
                specialTokenList.add(lastIndex, commentObj.specialToken.image);
                commentObj = commentObj.specialToken;
            }
        }
        else if (commentObj != null && commentObj.specialToken != null) {
            final ArrayList<String> specialTokenList2 = new ArrayList<String>();
            while (commentObj.specialToken != null) {
                specialTokenList2.add(0, commentObj.specialToken.image);
                commentObj = commentObj.specialToken;
            }
            final CommentClass commentObjToBeInserted = new CommentClass();
            commentObjToBeInserted.setSpecialToken(specialTokenList2);
            this.commentObjAfterToken = commentObjToBeInserted;
        }
    }
    
    public LimitClause toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final LimitClause lc = new LimitClause();
        if (this.limitValue != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            if (this.offsetStart != null) {
                lc.setLimitClause(this.limitClause);
                lc.setLimitValue(this.offsetStart);
                lc.setOffSetStart(this.limitValue);
            }
            else {
                lc.setLimitValue(this.limitValue);
                lc.setOffSetStart(this.offsetStart);
                lc.setLimitClause(this.limitClause);
            }
        }
        return lc;
    }
    
    public LimitClause toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final LimitClause lc = new LimitClause();
        if (this.limitValue != null) {
            if (this.offsetStart != null) {
                lc.setLimitClause(this.limitClause);
                lc.setLimitValue(this.limitValue);
                lc.setOffSetClause("OFFSET");
                lc.setOffSetStart(this.offsetStart);
            }
            else {
                lc.setLimitValue(this.limitValue);
                lc.setOffSetStart(this.offsetStart);
                lc.setLimitClause(this.limitClause);
            }
        }
        return lc;
    }
    
    public LimitClause toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (from_sqs != null && from_sqs.isMSAzure()) {
            final FetchClause fc = new FetchClause();
            if (this.limitValue != null && !this.limitValue.equalsIgnoreCase("ALL")) {
                if (to_sqs.getOrderByStatement() == null) {
                    final OrderByStatement obs = new OrderByStatement();
                    final OrderItem oi = new OrderItem();
                    final SelectColumn sc = new SelectColumn();
                    oi.setOrder("ASC");
                    obs.setOrderClause("ORDER BY ");
                    final Vector vc = new Vector();
                    vc.add("1");
                    sc.setColumnExpression(vc);
                    oi.setOrderSpecifier(sc);
                    final Vector oItems = new Vector();
                    oItems.add(oi);
                    obs.setOrderItemList(oItems);
                    to_sqs.setOrderByStatement(obs);
                }
                String offSet = " 0 ROWS";
                if (this.offsetStart != null) {
                    offSet = this.offsetStart + " ROWS ";
                }
                fc.setFetchFirstClause("FETCH NEXT");
                fc.setRowOnlyClause("ROWS ONLY");
                fc.setFetchCount(this.limitValue);
                fc.setFetchOffSetCount(offSet);
                to_sqs.setFetchClause(fc);
            }
        }
        else if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue));
            if (this.offsetStart != null) {
                int offset = 0;
                try {
                    offset = Integer.parseInt(this.offsetStart);
                }
                catch (final Exception ex) {}
                if (offset > 0) {
                    final SelectQueryStatement sqs = new SelectQueryStatement();
                    sqs.setDatatypeMapping(to_sqs.getDatatypeMapping());
                    sqs.setFromClause(to_sqs.getFromClause());
                    sqs.setGroupByStatement(to_sqs.getGroupByStatement());
                    sqs.setHavingStatement(to_sqs.getHavingStatement());
                    sqs.setIntoStatement(to_sqs.getIntoStatement());
                    sqs.setOrderByStatement(to_sqs.getOrderByStatement());
                    sqs.setSetOperatorClause(to_sqs.getSetOperatorClause());
                    sqs.setForUpdateStatement(to_sqs.getForUpdateStatement());
                    if (to_sqs.getWhereExpression() != null) {
                        sqs.setWhereExpression(this.getClonedWhereExpression(to_sqs.getWhereExpression()));
                    }
                    final SelectStatement toSS = to_sqs.getSelectStatement();
                    final Vector toSelItems = toSS.getSelectItemList();
                    final SelectStatement ss = new SelectStatement();
                    ss.setSelectClause("SELECT");
                    ss.setSelectRowSpecifier("TOP");
                    ss.setSelectRowCount(Integer.parseInt(this.offsetStart));
                    sqs.setSelectStatement(ss);
                    final Vector subSelItems = new Vector();
                    ss.setSelectItemList(subSelItems);
                    final SelectColumn subSC = new SelectColumn();
                    final WhereItem wi = new WhereItem();
                    final WhereColumn lwc = new WhereColumn();
                    final Object obj = toSelItems.get(0);
                    boolean isStar = false;
                    if (toSelItems.size() == 1 && obj instanceof SelectColumn) {
                        final Vector colExpr = ((SelectColumn)obj).getColumnExpression();
                        if (colExpr.size() == 1) {
                            final Object exprObj = colExpr.get(0);
                            if (exprObj instanceof String && exprObj.toString().equals("*")) {
                                isStar = true;
                            }
                        }
                        else if (colExpr.size() == 2) {
                            final Object exprObj = colExpr.get(1);
                            if (exprObj instanceof String && exprObj.toString().equals(".*")) {
                                isStar = true;
                            }
                        }
                    }
                    if (isStar) {
                        final FromClause fc2 = sqs.getFromClause();
                        if (fc2 != null) {
                            final Vector fromItems = fc2.getFromItemList();
                            for (int i = 0; i < fromItems.size(); ++i) {
                                final Object fromItemObj = fromItems.get(i);
                                if (fromItemObj instanceof FromTable) {
                                    final Object tblObj = ((FromTable)fromItemObj).getTableName();
                                    String alias = ((FromTable)fromItemObj).getAliasName();
                                    if (tblObj instanceof String) {
                                        String tableName = tblObj.toString();
                                        if (alias == null) {
                                            alias = tableName;
                                        }
                                        final int index = tableName.indexOf(".");
                                        if (index != -1) {
                                            tableName = tableName.substring(index + 1, tableName.length());
                                        }
                                        final Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.dataTypesFromMetaDataHT, tableName);
                                        if (colDatatypeTable == null) {
                                            to_sqs.setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                                            break;
                                        }
                                        final Set keys = colDatatypeTable.keySet();
                                        final Iterator it = keys.iterator();
                                        final Vector lwcColExpr = new Vector();
                                        lwcColExpr.add(alias + "." + it.next().toString());
                                        subSC.setColumnExpression(lwcColExpr);
                                        subSelItems.add(subSC);
                                        lwc.setColumnExpression(lwcColExpr);
                                        wi.setLeftWhereExp(lwc);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else if (obj instanceof SelectColumn) {
                        final Vector lwcColExpr2 = ((SelectColumn)obj).getColumnExpression();
                        subSC.setColumnExpression(lwcColExpr2);
                        subSC.setAliasName(((SelectColumn)obj).getAliasName());
                        subSelItems.add(subSC);
                        lwc.setColumnExpression(lwcColExpr2);
                        wi.setLeftWhereExp(lwc);
                    }
                    boolean hasAggregateFunctions = false;
                    if (lwc != null && lwc.getColumnExpression() != null) {
                        final Vector wce = lwc.getColumnExpression();
                        for (int k = 0; k < wce.size(); ++k) {
                            final Object ob = wce.get(k);
                            if (ob instanceof FunctionCalls) {
                                final String fnName = ((FunctionCalls)ob).getFunctionNameAsAString();
                                if (fnName != null && (fnName.equalsIgnoreCase("SUM") || fnName.equalsIgnoreCase("AVG") || fnName.equalsIgnoreCase("MIN") || fnName.equalsIgnoreCase("MAX") || fnName.equalsIgnoreCase("STDDEV") || fnName.equalsIgnoreCase("COUNT") || fnName.equalsIgnoreCase("VARIANCE") || fnName.equalsIgnoreCase("STD") || fnName.equalsIgnoreCase("STDDEV_POP") || fnName.equalsIgnoreCase("STDDEV_SAMP") || fnName.equalsIgnoreCase("VAR_POP") || fnName.equalsIgnoreCase("VAR_SAMP"))) {
                                    hasAggregateFunctions = true;
                                    break;
                                }
                            }
                        }
                    }
                    wi.setOperator("NOT IN");
                    wi.setRightWhereSubQuery(sqs);
                    if (hasAggregateFunctions) {
                        if (to_sqs.getHavingStatement() != null) {
                            final HavingStatement havingSt = to_sqs.getHavingStatement();
                            final Vector havingItems = havingSt.getHavingItems();
                            if (havingItems.size() == 1) {
                                final WhereExpression havExp = havingItems.get(0);
                                if (havExp != null) {
                                    Vector operators = havExp.getOperator();
                                    if (operators != null && operators.size() > 0) {
                                        operators.add("AND");
                                    }
                                    else {
                                        operators = new Vector();
                                        operators.add("AND");
                                        havExp.setOperator(operators);
                                    }
                                    havExp.getWhereItems().add(wi);
                                }
                            }
                        }
                        else {
                            final WhereExpression newWE = new WhereExpression();
                            final Vector wis = new Vector();
                            wis.add(wi);
                            newWE.setWhereItem(wis);
                            final Vector hItems = new Vector();
                            hItems.add(newWE);
                            final HavingStatement having = new HavingStatement();
                            having.setHavingClause("HAVING");
                            having.setHavingItems(hItems);
                            to_sqs.setHavingStatement(having);
                        }
                    }
                    else {
                        final WhereExpression toWE = to_sqs.getWhereExpression();
                        if (toWE != null) {
                            Vector operators2 = toWE.getOperator();
                            if (operators2 != null && operators2.size() > 0) {
                                operators2.add("AND");
                            }
                            else {
                                operators2 = new Vector();
                                operators2.add("AND");
                                toWE.setOperator(operators2);
                            }
                            toWE.getWhereItems().add(wi);
                        }
                        else {
                            final WhereExpression newWE2 = new WhereExpression();
                            final Vector wis2 = new Vector();
                            wis2.add(wi);
                            newWE2.setWhereItem(wis2);
                            to_sqs.setWhereExpression(newWE2);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public LimitClause toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue));
        }
        return null;
    }
    
    public LimitClause toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            to_sqs.getSelectStatement().setInformixRowSpecifier("FIRST");
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue));
        }
        return null;
    }
    
    public LimitClause toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FetchClause fc = new FetchClause();
        if (to_sqs.getFetchClause() != null) {
            throw new ConvertException();
        }
        if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            fc.setFetchFirstClause("FETCH FIRST");
            fc.setFetchCount(this.limitValue);
            fc.setRowOnlyClause("ROWS ONLY");
            to_sqs.setFetchClause(fc);
        }
        return null;
    }
    
    public LimitClause toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.offsetStart != null && this.offsetStart.equalsIgnoreCase("ALL")) {
            throw new ConvertException("Invalid 'OFFSET' value");
        }
        LimitClause lc = new LimitClause();
        if (this.limitClause != null && this.offsetStart == null && !this.limitValue.equalsIgnoreCase("ALL")) {
            final WhereExpression f_we = from_sqs.getWhereExpression();
            final WhereItem wi = new WhereItem();
            Vector v_temp = new Vector();
            WhereColumn wc_temp = new WhereColumn();
            v_temp.addElement("ROWNUM");
            wc_temp.setColumnExpression(v_temp);
            wi.setLeftWhereExp(wc_temp);
            wi.setOperator("<");
            v_temp = new Vector();
            wc_temp = new WhereColumn();
            v_temp.addElement(Integer.toString(Integer.parseInt(this.limitValue) + 1));
            wc_temp.setColumnExpression(v_temp);
            wi.setRightWhereExp(wc_temp);
            if (f_we != null && f_we.getCheckWhere()) {
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereItem(wi);
            }
            else if (f_we != null) {
                to_sqs.setWhereExpression(f_we.toOracleSelect(to_sqs, from_sqs));
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereItem(wi);
            }
            else {
                final WhereExpression we = new WhereExpression();
                we.addWhereItem(wi);
                if (to_sqs != null && to_sqs.getWhereExpression() != null) {
                    to_sqs.getWhereExpression().addOperator("AND");
                    to_sqs.getWhereExpression().addWhereExpression(we);
                }
                else {
                    to_sqs.setWhereExpression(we);
                }
            }
        }
        else if (this.limitClause != null && this.offsetStart != null && this.limitValue.equalsIgnoreCase("ALL")) {
            final WhereItem wi2 = new WhereItem();
            final WhereColumn wc = new WhereColumn();
            final Vector v_sc = new Vector();
            final SelectQueryStatement sqs_i = new SelectQueryStatement();
            final SelectStatement ss_i = new SelectStatement();
            final FromClause fc = new FromClause();
            final TableColumn tc = new TableColumn();
            final WhereExpression we2 = new WhereExpression();
            final FromClause t_fc = to_sqs.getFromClause();
            final WhereExpression f_we2 = from_sqs.getWhereExpression();
            v_sc.addElement("ROWID");
            wc.setColumnExpression(v_sc);
            wi2.setLeftWhereExp(wc);
            wi2.setOperator("NOT IN");
            wi2.setRightWhereSubQuery(sqs_i);
            ss_i.setSelectClause("select");
            final Vector v_tc = new Vector();
            v_tc.addElement("ROWID");
            final SelectColumn sc_new = new SelectColumn();
            sc_new.setColumnExpression(v_tc);
            final Vector vec_tc = new Vector();
            vec_tc.addElement(sc_new);
            ss_i.setSelectItemList(vec_tc);
            fc.setFromClause(t_fc.getFromClause());
            fc.setFromItemList(t_fc.getFromItemList());
            final WhereItem wi_sc = new WhereItem();
            Vector v_temp2 = new Vector();
            WhereColumn wc_temp2 = new WhereColumn();
            v_temp2.addElement("ROWNUM");
            wc_temp2.setColumnExpression(v_temp2);
            wi_sc.setLeftWhereExp(wc_temp2);
            wi_sc.setOperator("<");
            v_temp2 = new Vector();
            wc_temp2 = new WhereColumn();
            v_temp2.addElement(Integer.toString(Integer.parseInt(this.offsetStart) + 1));
            wc_temp2.setColumnExpression(v_temp2);
            wi_sc.setRightWhereExp(wc_temp2);
            sqs_i.setSelectStatement(ss_i);
            sqs_i.setFromClause(fc);
            we2.addWhereItem(wi_sc);
            sqs_i.setWhereExpression(we2);
            if (f_we2 != null && f_we2.getCheckWhere()) {
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereItem(wi2);
            }
            else if (f_we2 != null) {
                to_sqs.setWhereExpression(f_we2.toOracleSelect(to_sqs, from_sqs));
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereItem(wi2);
            }
            else {
                final WhereExpression we_temp = new WhereExpression();
                we_temp.addWhereItem(wi2);
                if (to_sqs != null && to_sqs.getWhereExpression() != null) {
                    to_sqs.getWhereExpression().addOperator("AND");
                    to_sqs.getWhereExpression().addWhereExpression(we_temp);
                }
                else {
                    to_sqs.setWhereExpression(we_temp);
                }
            }
        }
        else if (this.limitClause != null && this.offsetStart != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            final WhereItem wi2 = new WhereItem();
            final WhereColumn wc = new WhereColumn();
            final Vector v_sc = new Vector();
            final SelectQueryStatement sqs_i = new SelectQueryStatement();
            final SelectStatement ss_i = new SelectStatement();
            final FromClause fc = new FromClause();
            final TableColumn tc = new TableColumn();
            final WhereExpression we2 = new WhereExpression();
            final FromClause t_fc = to_sqs.getFromClause();
            v_sc.addElement("ROWID");
            wc.setColumnExpression(v_sc);
            wi2.setLeftWhereExp(wc);
            wi2.setOperator("IN");
            wi2.setRightWhereSubQuery(sqs_i);
            ss_i.setSelectClause("select");
            final Vector v_tc2 = new Vector();
            v_tc2.addElement("ROWID");
            final SelectColumn sc_new2 = new SelectColumn();
            sc_new2.setColumnExpression(v_tc2);
            final Vector vec_tc2 = new Vector();
            vec_tc2.addElement(sc_new2);
            ss_i.setSelectItemList(vec_tc2);
            fc.setFromClause(t_fc.getFromClause());
            fc.setFromItemList(t_fc.getFromItemList());
            final WhereItem wi_sc2 = new WhereItem();
            Vector v_temp3 = new Vector();
            WhereColumn wc_temp3 = new WhereColumn();
            v_temp3.addElement("ROWNUM");
            wc_temp3.setColumnExpression(v_temp3);
            wi_sc2.setLeftWhereExp(wc_temp3);
            wi_sc2.setOperator("<");
            v_temp3 = new Vector();
            wc_temp3 = new WhereColumn();
            v_temp3.addElement(Integer.toString(Integer.parseInt(this.limitValue) + Integer.parseInt(this.offsetStart) + 1));
            wc_temp3.setColumnExpression(v_temp3);
            wi_sc2.setRightWhereExp(wc_temp3);
            sqs_i.setSelectStatement(ss_i);
            sqs_i.setFromClause(fc);
            we2.addWhereItem(wi_sc2);
            sqs_i.setWhereExpression(we2);
            final WhereItem wi_new = new WhereItem();
            final WhereExpression we_new = new WhereExpression();
            final WhereExpression f_we3 = from_sqs.getWhereExpression();
            Vector vec_temp = new Vector();
            WhereColumn wc_tc = new WhereColumn();
            vec_temp.addElement("ROWNUM");
            wc_tc.setColumnExpression(vec_temp);
            wi_new.setLeftWhereExp(wc_tc);
            wi_new.setOperator("<");
            vec_temp = new Vector();
            wc_tc = new WhereColumn();
            vec_temp.addElement(Integer.toString(Integer.parseInt(this.offsetStart) + 1));
            wc_tc.setColumnExpression(vec_temp);
            wi_new.setRightWhereExp(wc_tc);
            final SelectQueryStatement sqs_new = new SelectQueryStatement();
            sqs_new.setSelectStatement(ss_i);
            sqs_new.setFromClause(fc);
            we_new.addWhereItem(wi_new);
            sqs_new.setWhereExpression(we_new);
            final SetOperatorClause soc = new SetOperatorClause();
            soc.setSetClause("minus");
            soc.setSelectQueryStatement(sqs_new);
            sqs_i.setSetOperatorClause(soc);
            if (f_we3 != null && f_we3.getCheckWhere()) {
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereItem(wi2);
            }
            else if (f_we3 != null) {
                to_sqs.setWhereExpression(f_we3.toOracleSelect(to_sqs, from_sqs));
                to_sqs.getWhereExpression().addOperator("AND");
                to_sqs.getWhereExpression().addWhereItem(wi2);
            }
            else {
                final WhereExpression we_temp2 = new WhereExpression();
                we_temp2.addWhereItem(wi2);
                if (to_sqs != null && to_sqs.getWhereExpression() != null) {
                    to_sqs.getWhereExpression().addOperator("AND");
                    to_sqs.getWhereExpression().addWhereExpression(we_temp2);
                }
                else {
                    to_sqs.setWhereExpression(we_temp2);
                }
            }
        }
        lc = null;
        return lc;
    }
    
    private WhereExpression getClonedWhereExpression(final WhereExpression whereExpression) {
        final WhereExpression clonedWhereExpression = new WhereExpression();
        final Vector whereItemList = new Vector();
        final Vector clonedWhereItems = new Vector();
        clonedWhereExpression.setOperator((Vector)whereExpression.getOperator().clone());
        final Vector whereItems = whereExpression.getWhereItems();
        for (int i = 0; i < whereItems.size(); ++i) {
            if (whereItems.elementAt(i) instanceof WhereItem) {
                final WhereItem whereItem = (WhereItem)whereItems.elementAt(i).clone();
                whereItemList.addElement(whereItem);
            }
            else if (whereItems.elementAt(i) instanceof WhereExpression) {
                whereItemList.addElement(this.getClonedWhereExpression(whereItems.elementAt(i)));
            }
        }
        clonedWhereExpression.setWhereItem(whereItemList);
        return clonedWhereExpression;
    }
    
    public LimitClause toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.limitClause != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            to_sqs.getSelectStatement().setSelectRowSpecifier("FIRST");
            to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(this.limitValue));
        }
        return null;
    }
    
    public LimitClause toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final LimitClause lc = new LimitClause();
        if (this.limitValue != null) {
            if (this.offsetStart != null) {
                lc.setLimitClause(this.limitClause);
                lc.setLimitValue(this.offsetStart);
                lc.setOffSetStart(this.limitValue);
            }
            else {
                lc.setLimitValue(this.limitValue);
                lc.setOffSetStart(this.offsetStart);
                lc.setLimitClause(this.limitClause);
            }
        }
        return lc;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString() + " ");
        }
        if (this.limitClause != null) {
            sb.append(this.limitClause.toUpperCase());
        }
        if (this.limitValue != null) {
            sb.append(" " + this.limitValue.toUpperCase());
        }
        if (this.offsetClause != null) {
            sb.append(" " + this.offsetClause.toUpperCase());
        }
        if (this.offsetStart != null) {
            if (this.offsetClause == null) {
                sb.append("," + this.offsetStart.toUpperCase());
            }
            else {
                sb.append(" " + this.offsetStart.toUpperCase());
            }
        }
        if (this.RowOnlyClause != null) {
            sb.append(" " + this.RowOnlyClause.toUpperCase());
        }
        if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString());
        }
        return sb.toString();
    }
    
    public LimitClause toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FetchClause fc = new FetchClause();
        if (this.limitValue != null && !this.limitValue.equalsIgnoreCase("ALL")) {
            if (this.offsetStart != null) {
                fc.setFetchFirstClause("FETCH NEXT");
                fc.setRowOnlyClause("ROWS ONLY");
                fc.setFetchCount(this.limitValue);
                fc.setFetchOffSetCount(this.offsetStart);
                to_sqs.setFetchClause(fc);
            }
            else {
                fc.setFetchFirstClause("FETCH FIRST");
                fc.setFetchCount(this.limitValue);
                fc.setRowOnlyClause("ROWS ONLY");
                to_sqs.setFetchClause(fc);
            }
        }
        return null;
    }
}
