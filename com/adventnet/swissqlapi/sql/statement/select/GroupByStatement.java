package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class GroupByStatement
{
    private String GroupClause;
    private boolean ALLOption;
    private Vector GroupByItemList;
    private String GroupingSetClause;
    private String WithOption;
    private boolean CheckGroupByStatement;
    private String openBraces;
    private String closedBraces;
    private UserObjectContext context;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    private HintClause hintClause;
    private String descOption;
    
    public GroupByStatement() {
        this.context = null;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setGroupClause(final String s_gc) {
        this.GroupClause = s_gc;
    }
    
    public void setGroupingSetClause(final String s_gsc) {
        this.GroupingSetClause = s_gsc;
    }
    
    public void setGroupByItemList(final Vector v_gbil) {
        this.GroupByItemList = v_gbil;
    }
    
    public void setCheckGroupByStatement(final boolean b_cgbs) {
        this.CheckGroupByStatement = b_cgbs;
    }
    
    public void setALLOption(final boolean b_ao) {
        this.ALLOption = b_ao;
    }
    
    public void setWithOption(final String s_wo) {
        this.WithOption = s_wo;
    }
    
    public void setOpenBraces(final String openBraces) {
        this.openBraces = openBraces;
    }
    
    public void setClosedBraces(final String closedBraces) {
        this.closedBraces = closedBraces;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass commentObj) {
        this.commentObjAfterToken = commentObj;
    }
    
    public void setHintClause(final HintClause hintClause) {
        this.hintClause = hintClause;
    }
    
    public void setDescOption(final String desc) {
        this.descOption = desc;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public boolean getCheckGroupByStatement() {
        return this.CheckGroupByStatement;
    }
    
    public String getGroupClause() {
        return this.GroupClause;
    }
    
    public Vector getGroupByItemList() {
        return this.GroupByItemList;
    }
    
    public String getGroupingSetClause() {
        return this.GroupingSetClause;
    }
    
    public boolean getALLOption() {
        return this.ALLOption;
    }
    
    public String getWithOption() {
        return this.WithOption;
    }
    
    public String getOpenBraces() {
        return this.openBraces;
    }
    
    public String getClosedBraces() {
        return this.closedBraces;
    }
    
    public HintClause getHintClause() {
        return this.hintClause;
    }
    
    public String getDescOption() {
        return this.descOption;
    }
    
    public GroupByStatement toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        final Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            if (this.ALLOption) {
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        v_gil.addElement(sc.toANSISelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
                SetOperatorClause soc = null;
                if (from_sqs.getSetOperatorClause() != null) {
                    soc = from_sqs.getSetOperatorClause();
                }
                if (from_sqs.getWhereExpression() != null) {
                    final SetOperatorClause n_soc = this.createSetOperatorClause(null, from_sqs, to_sqs, 8);
                    SelectQueryStatement t_sqs = to_sqs;
                    t_sqs.setSetOperatorClause(n_soc);
                    for (SetOperatorClause t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                        t_sqs = t_soc.getSelectQueryStatement();
                    }
                    if (soc != null) {
                        t_sqs.setSetOperatorClause(soc.toANSISelect(to_sqs, from_sqs));
                    }
                }
                else if (soc != null) {
                    to_sqs.setSetOperatorClause(soc.toANSISelect(to_sqs, from_sqs));
                }
            }
            else {
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        v_gil.addElement(sc.toANSISelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
        }
        else {
            SetOperatorClause soc = null;
            if (this.GroupByItemList.elementAt(0).size() != 0) {
                final Vector singleGroupingSet = this.GroupByItemList.elementAt(0);
                for (int i_count2 = 0; i_count2 < singleGroupingSet.size(); ++i_count2) {
                    if (singleGroupingSet.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = singleGroupingSet.elementAt(i_count2);
                        v_gil.addElement(sc2.toANSISelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
            else {
                gbs = null;
            }
            this.makeNonGroupedSelectItemsNull(this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 8);
            if (from_sqs.getSetOperatorClause() != null) {
                soc = from_sqs.getSetOperatorClause();
            }
            for (int i_count3 = 1; i_count3 < this.GroupByItemList.size(); ++i_count3) {
                final Vector v_item_list = this.GroupByItemList.elementAt(i_count3);
                final SetOperatorClause n_soc2 = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 8);
                SelectQueryStatement t_sqs2 = to_sqs;
                for (SetOperatorClause t_soc2 = t_sqs2.getSetOperatorClause(); t_soc2 != null; t_soc2 = t_sqs2.getSetOperatorClause()) {
                    t_sqs2 = t_soc2.getSelectQueryStatement();
                }
                t_sqs2.setSetOperatorClause(n_soc2);
            }
            SelectQueryStatement t_sqs3 = to_sqs;
            for (SetOperatorClause t_soc3 = t_sqs3.getSetOperatorClause(); t_soc3 != null; t_soc3 = t_sqs3.getSetOperatorClause()) {
                t_sqs3 = t_soc3.getSelectQueryStatement();
            }
            if (soc != null) {
                t_sqs3.setSetOperatorClause(soc.toANSISelect(to_sqs, from_sqs));
            }
        }
        return gbs;
    }
    
    public GroupByStatement toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            if (this.WithOption != null) {
                final Vector v_gl = new Vector();
                final SelectColumn sc_glsc = new SelectColumn();
                final Vector v_glsc = new Vector();
                final FunctionCalls fc = new FunctionCalls();
                final TableColumn tc = new TableColumn();
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        v_gil.addElement(sc.toTeradataSelect(to_sqs, from_sqs));
                    }
                }
                tc.setColumnName(this.WithOption);
                fc.setFunctionName(tc);
                fc.setFunctionArguments(v_gil);
                v_glsc.addElement(fc);
                sc_glsc.setColumnExpression(v_glsc);
                v_gl.addElement(sc_glsc);
                gbs.setGroupByItemList(v_gl);
            }
            else if (this.ALLOption) {
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                        v_gil.addElement(sc2.toTeradataSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
                SetOperatorClause soc = null;
                if (from_sqs.getSetOperatorClause() != null) {
                    soc = from_sqs.getSetOperatorClause();
                }
                if (from_sqs.getWhereExpression() != null) {
                    final SetOperatorClause n_soc = this.createSetOperatorClause(null, from_sqs, to_sqs, 12);
                    SelectQueryStatement t_sqs = to_sqs;
                    t_sqs.setSetOperatorClause(n_soc);
                    for (SetOperatorClause t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                        t_sqs = t_soc.getSelectQueryStatement();
                    }
                    if (soc != null) {
                        t_sqs.setSetOperatorClause(soc.toTeradataSelect(to_sqs, from_sqs));
                    }
                }
                else if (soc != null) {
                    to_sqs.setSetOperatorClause(soc.toTeradataSelect(to_sqs, from_sqs));
                }
            }
            else {
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        boolean isNum = false;
                        boolean removeNumCol = false;
                        final SelectColumn sc3 = this.GroupByItemList.elementAt(i_count2).toTeradataSelect(to_sqs, from_sqs);
                        if (sc3.getColumnExpression().size() == 1) {
                            final String scStr = sc3.getColumnExpression().get(0).toString().trim();
                            try {
                                Double.parseDouble(scStr);
                                isNum = true;
                            }
                            catch (final NumberFormatException ex) {}
                            boolean aliasPresent = false;
                            if (to_sqs != null && from_sqs != null && to_sqs.getSelectStatement() != null && from_sqs.getSelectStatement().getSelectItemList().size() == to_sqs.getSelectStatement().getSelectItemList().size()) {
                                int sci = 0;
                                while (sci < from_sqs.getSelectStatement().getSelectItemList().size()) {
                                    final String al = from_sqs.getSelectStatement().getSelectItemList().get(sci).getTheCoreSelectItem().trim();
                                    if (al != null && al.equalsIgnoreCase(scStr)) {
                                        aliasPresent = true;
                                        final String aliasName = to_sqs.getSelectStatement().getSelectItemList().get(sci).getAliasName();
                                        if (isNum) {
                                            removeNumCol = true;
                                            break;
                                        }
                                        sc3.getColumnExpression().setElementAt(to_sqs.getSelectStatement().getSelectItemList().get(sci).getTheCoreSelectItem(), 0);
                                        break;
                                    }
                                    else {
                                        final String aliasName = to_sqs.getSelectStatement().getSelectItemList().get(sci).getAliasName();
                                        if (aliasName != null && aliasName.equalsIgnoreCase(scStr)) {
                                            sc3.getColumnExpression().setElementAt(to_sqs.getSelectStatement().getSelectItemList().get(sci).getTheCoreSelectItem(), 0);
                                        }
                                        ++sci;
                                    }
                                }
                            }
                        }
                        if (!removeNumCol) {
                            v_gil.addElement(sc3);
                        }
                    }
                    else if (this.GroupByItemList.elementAt(i_count2) instanceof Vector) {
                        final Vector groupByVector = this.GroupByItemList.elementAt(i_count2);
                        for (int j_count = 0; j_count < groupByVector.size(); ++j_count) {
                            if (groupByVector.elementAt(j_count) instanceof SelectColumn) {
                                boolean isNum2 = false;
                                boolean removeNumCol2 = false;
                                final SelectColumn sc4 = groupByVector.elementAt(j_count).toTeradataSelect(to_sqs, from_sqs);
                                if (sc4.getColumnExpression().size() == 1) {
                                    final String scStr2 = sc4.getColumnExpression().get(0).toString().trim();
                                    try {
                                        Double.parseDouble(scStr2);
                                        isNum2 = true;
                                    }
                                    catch (final NumberFormatException ex2) {}
                                    boolean aliasPresent2 = false;
                                    if (to_sqs != null && from_sqs != null && to_sqs.getSelectStatement() != null && from_sqs.getSelectStatement().getSelectItemList().size() == to_sqs.getSelectStatement().getSelectItemList().size()) {
                                        int sci2 = 0;
                                        while (sci2 < from_sqs.getSelectStatement().getSelectItemList().size()) {
                                            final String al2 = from_sqs.getSelectStatement().getSelectItemList().get(sci2).getTheCoreSelectItem().trim();
                                            if (al2 != null && al2.equalsIgnoreCase(scStr2)) {
                                                aliasPresent2 = true;
                                                final String aliasName2 = to_sqs.getSelectStatement().getSelectItemList().get(sci2).getAliasName();
                                                if (isNum2) {
                                                    removeNumCol2 = true;
                                                    break;
                                                }
                                                sc4.getColumnExpression().setElementAt(to_sqs.getSelectStatement().getSelectItemList().get(sci2).getTheCoreSelectItem(), 0);
                                                break;
                                            }
                                            else {
                                                final String aliasName2 = to_sqs.getSelectStatement().getSelectItemList().get(sci2).getAliasName();
                                                if (aliasName2 != null && aliasName2.equalsIgnoreCase(scStr2)) {
                                                    sc4.getColumnExpression().setElementAt(to_sqs.getSelectStatement().getSelectItemList().get(sci2).getTheCoreSelectItem(), 0);
                                                }
                                                ++sci2;
                                            }
                                        }
                                    }
                                }
                                if (!removeNumCol2) {
                                    v_gil.addElement(sc4);
                                }
                            }
                        }
                    }
                    else if (this.GroupByItemList.elementAt(i_count2) instanceof FunctionCalls) {
                        final FunctionCalls cubeRollUpFunc = this.GroupByItemList.elementAt(i_count2);
                        final Vector newFuncArgs = new Vector();
                        for (int fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                            final Object fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                            if (fnArg instanceof SelectColumn) {
                                newFuncArgs.addElement(((SelectColumn)fnArg).toTeradataSelect(to_sqs, from_sqs));
                            }
                            else if (fnArg instanceof Vector) {
                                final Vector fnArgVec = (Vector)fnArg;
                                final SelectColumn newSelectColumn = new SelectColumn();
                                final Vector newVector = new Vector();
                                for (int vi = 0; vi < fnArgVec.size(); ++vi) {
                                    final SelectColumn fnArgCol = fnArgVec.elementAt(vi).toTeradataSelect(to_sqs, from_sqs);
                                    if (vi != fnArgVec.size() - 1) {
                                        fnArgCol.setEndsWith(",");
                                    }
                                    newVector.add(fnArgCol);
                                }
                                newSelectColumn.setColumnExpression(newVector);
                                newSelectColumn.setOpenBrace("(");
                                newSelectColumn.setCloseBrace(")");
                                newFuncArgs.addElement(newSelectColumn);
                            }
                        }
                        cubeRollUpFunc.setFunctionArguments(newFuncArgs);
                        v_gil.addElement(cubeRollUpFunc);
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
        }
        else {
            gbs.setGroupingSetClause(this.GroupingSetClause);
            gbs.setOpenBraces(this.openBraces);
            gbs.setClosedBraces(this.closedBraces);
            for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                if (this.GroupByItemList.elementAt(i_count2) instanceof Vector) {
                    final Vector v_item_list = this.GroupByItemList.elementAt(i_count2);
                    v_gil = new Vector();
                    for (int i_icount = 0; i_icount < v_item_list.size(); ++i_icount) {
                        if (v_item_list.elementAt(i_icount) instanceof SelectColumn) {
                            v_gil.addElement(v_item_list.elementAt(i_icount).toTeradataSelect(to_sqs, from_sqs));
                        }
                    }
                    v_tgil.addElement(v_gil);
                }
                else if (this.GroupByItemList.elementAt(i_count2) instanceof FunctionCalls) {
                    final FunctionCalls cubeRollUpFunc = this.GroupByItemList.elementAt(i_count2);
                    final Vector newFuncArgs = new Vector();
                    for (int fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                        final Object fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                        if (fnArg instanceof SelectColumn) {
                            newFuncArgs.addElement(((SelectColumn)fnArg).toTeradataSelect(to_sqs, from_sqs));
                        }
                        else if (fnArg instanceof Vector) {
                            final Vector fnArgVec = (Vector)fnArg;
                            final SelectColumn newSelectColumn = new SelectColumn();
                            final Vector newVector = new Vector();
                            for (int vi = 0; vi < fnArgVec.size(); ++vi) {
                                final SelectColumn fnArgCol = fnArgVec.elementAt(vi).toTeradataSelect(to_sqs, from_sqs);
                                if (vi != fnArgVec.size() - 1) {
                                    fnArgCol.setEndsWith(",");
                                }
                                newVector.add(fnArgCol);
                            }
                            newSelectColumn.setColumnExpression(newVector);
                            newSelectColumn.setOpenBrace("(");
                            newSelectColumn.setCloseBrace(")");
                            newFuncArgs.addElement(newSelectColumn);
                        }
                    }
                    cubeRollUpFunc.setFunctionArguments(newFuncArgs);
                    v_gil.addElement(cubeRollUpFunc);
                }
                else {
                    final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                    v_tgil.addElement(sc2.toTeradataSelect(to_sqs, from_sqs));
                }
            }
            gbs.setGroupByItemList(v_tgil);
        }
        if (gbs.getGroupByItemList() == null || gbs.getGroupByItemList().isEmpty()) {
            return null;
        }
        return gbs;
    }
    
    public GroupByStatement toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            if (this.WithOption != null) {
                final Vector v_gl = new Vector();
                final SelectColumn sc_glsc = new SelectColumn();
                final Vector v_glsc = new Vector();
                final FunctionCalls fc = new FunctionCalls();
                final TableColumn tc = new TableColumn();
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        v_gil.addElement(sc.toDB2Select(to_sqs, from_sqs));
                    }
                }
                tc.setColumnName(this.WithOption);
                fc.setFunctionName(tc);
                fc.setFunctionArguments(v_gil);
                v_glsc.addElement(fc);
                sc_glsc.setColumnExpression(v_glsc);
                v_gl.addElement(sc_glsc);
                gbs.setGroupByItemList(v_gl);
            }
            else if (this.ALLOption) {
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                        this.covertAilasToTableName(sc2, from_sqs);
                        v_gil.addElement(sc2.toDB2Select(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
                SetOperatorClause soc = null;
                if (from_sqs.getSetOperatorClause() != null) {
                    soc = from_sqs.getSetOperatorClause();
                }
                if (from_sqs.getWhereExpression() != null) {
                    final SetOperatorClause n_soc = this.createSetOperatorClause(null, from_sqs, to_sqs, 3);
                    SelectQueryStatement t_sqs = to_sqs;
                    t_sqs.setSetOperatorClause(n_soc);
                    for (SetOperatorClause t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                        t_sqs = t_soc.getSelectQueryStatement();
                    }
                    if (soc != null) {
                        t_sqs.setSetOperatorClause(soc.toDB2Select(to_sqs, from_sqs));
                    }
                }
                else if (soc != null) {
                    to_sqs.setSetOperatorClause(soc.toDB2Select(to_sqs, from_sqs));
                }
            }
            else {
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                        this.covertAilasToTableName(sc2, from_sqs);
                        v_gil.addElement(sc2.toDB2Select(to_sqs, from_sqs));
                    }
                    else if (this.GroupByItemList.elementAt(i_count2) instanceof FunctionCalls) {
                        final FunctionCalls cubeRollUpFunc = this.GroupByItemList.elementAt(i_count2);
                        final Vector newFuncArgs = new Vector();
                        for (int fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                            final Object fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                            if (fnArg instanceof SelectColumn) {
                                newFuncArgs.addElement(((SelectColumn)fnArg).toDB2Select(to_sqs, from_sqs));
                            }
                            else if (fnArg instanceof Vector) {
                                final Vector fnArgVec = (Vector)fnArg;
                                final SelectColumn newSelectColumn = new SelectColumn();
                                final Vector newVector = new Vector();
                                for (int vi = 0; vi < fnArgVec.size(); ++vi) {
                                    final SelectColumn fnArgCol = fnArgVec.elementAt(vi).toDB2Select(to_sqs, from_sqs);
                                    if (vi != fnArgVec.size() - 1) {
                                        fnArgCol.setEndsWith(",");
                                    }
                                    newVector.add(fnArgCol);
                                }
                                newSelectColumn.setColumnExpression(newVector);
                                newSelectColumn.setOpenBrace("(");
                                newSelectColumn.setCloseBrace(")");
                                newFuncArgs.addElement(newSelectColumn);
                            }
                        }
                        cubeRollUpFunc.setFunctionArguments(newFuncArgs);
                        v_gil.addElement(cubeRollUpFunc);
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
        }
        else {
            gbs.setGroupingSetClause(this.GroupingSetClause);
            gbs.setOpenBraces(this.openBraces);
            gbs.setClosedBraces(this.closedBraces);
            for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                final Vector v_item_list = this.GroupByItemList.elementAt(i_count2);
                v_gil = new Vector();
                for (int i_icount = 0; i_icount < v_item_list.size(); ++i_icount) {
                    if (v_item_list.elementAt(i_icount) instanceof SelectColumn) {
                        v_gil.addElement(v_item_list.elementAt(i_icount).toDB2Select(to_sqs, from_sqs));
                    }
                }
                v_tgil.addElement(v_gil);
            }
            gbs.setGroupByItemList(v_tgil);
        }
        return gbs;
    }
    
    public GroupByStatement toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final GroupByStatement gbs = new GroupByStatement();
        gbs.setCommentClassAfterToken(this.commentObjAfterToken);
        gbs.setGroupClause(this.GroupClause);
        final Vector v_gbl = new Vector();
        if (this.GroupingSetClause == null) {
            for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                    v_gbl.addElement(this.GroupByItemList.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.GroupByItemList.elementAt(i_count);
                    String s_fn = new String();
                    if (fc.getFunctionName() != null) {
                        final TableColumn c = fc.getFunctionName();
                        s_fn = fc.getFunctionName().getColumnName();
                    }
                    if ((s_fn != null && s_fn.equalsIgnoreCase("cube")) || s_fn.equalsIgnoreCase("rollup")) {
                        gbs.setWithOption(s_fn);
                        final Vector v_fa = fc.getFunctionArguments();
                        for (int cnt = 0; cnt < v_fa.size(); ++cnt) {
                            v_gbl.addElement(v_fa.elementAt(cnt).toMySQLSelect(to_sqs, from_sqs));
                        }
                    }
                    else {
                        v_gbl.addElement(fc.toMSSQLServerSelect(to_sqs, from_sqs));
                    }
                }
            }
        }
        if (this.descOption != null) {
            gbs.setDescOption(this.descOption);
        }
        gbs.setGroupByItemList(v_gbl);
        if (this.GroupingSetClause != null) {
            throw new ConvertException();
        }
        if (this.ALLOption) {
            throw new ConvertException();
        }
        if (this.WithOption != null) {
            gbs.setWithOption(this.WithOption);
        }
        return gbs;
    }
    
    public GroupByStatement toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        if (this.ALLOption) {
            gbs.setALLOption(this.ALLOption);
        }
        if (this.WithOption != null) {
            gbs.setWithOption(this.WithOption);
        }
        final Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                    final Vector v_ce = sc.getColumnExpression();
                    this.covertAilasToTableName(sc, from_sqs);
                    if (v_ce.elementAt(0) instanceof FunctionCalls) {
                        final FunctionCalls fc = v_ce.elementAt(0);
                        String s_fn = new String();
                        if (fc.getFunctionName() != null) {
                            final TableColumn c = fc.getFunctionName();
                            s_fn = fc.getFunctionName().getColumnName();
                        }
                        if ((s_fn != null && s_fn.equalsIgnoreCase("cube")) || s_fn.equalsIgnoreCase("rollup")) {
                            gbs.setWithOption(s_fn);
                            final Vector v_fa = fc.getFunctionArguments();
                            for (int cnt = 0; cnt < v_fa.size(); ++cnt) {
                                v_gil.addElement(v_fa.elementAt(cnt).toMSSQLServerSelect(to_sqs, from_sqs));
                            }
                        }
                        else {
                            v_gil.addElement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
                        }
                    }
                    else if (v_ce.elementAt(0) instanceof TableColumn) {
                        SwisSQLUtils.checkAndReplaceGroupByItem(sc, from_sqs);
                        v_gil.addElement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
                    }
                    else {
                        v_gil.addElement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
                    }
                }
                else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                    final FunctionCalls fc2 = this.GroupByItemList.elementAt(i_count);
                    String s_fn2 = new String();
                    if (fc2.getFunctionName() != null) {
                        final TableColumn c2 = fc2.getFunctionName();
                        s_fn2 = fc2.getFunctionName().getColumnName();
                    }
                    if ((s_fn2 != null && s_fn2.equalsIgnoreCase("cube")) || s_fn2.equalsIgnoreCase("rollup")) {
                        gbs.setWithOption(s_fn2);
                        final Vector v_fa2 = fc2.getFunctionArguments();
                        for (int cnt2 = 0; cnt2 < v_fa2.size(); ++cnt2) {
                            v_gil.addElement(v_fa2.elementAt(cnt2).toMSSQLServerSelect(to_sqs, from_sqs));
                        }
                    }
                    else {
                        v_gil.addElement(fc2.toMSSQLServerSelect(to_sqs, from_sqs));
                    }
                }
            }
            this.processGroupByArguments(v_gil, v_gil, 0, false);
            gbs.setGroupByItemList(v_gil);
        }
        else {
            SetOperatorClause soc = null;
            if (this.GroupByItemList.elementAt(0).size() != 0) {
                final Vector singleGroupingSet = this.GroupByItemList.elementAt(0);
                for (int i_count2 = 0; i_count2 < singleGroupingSet.size(); ++i_count2) {
                    if (singleGroupingSet.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = singleGroupingSet.elementAt(i_count2);
                        v_gil.addElement(sc2.toMSSQLServerSelect(to_sqs, from_sqs));
                    }
                }
                this.processGroupByArguments(v_gil, v_gil, 0, false);
                gbs.setGroupByItemList(v_gil);
            }
            else {
                gbs = null;
            }
            this.makeNonGroupedSelectItemsNull(this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 2);
            if (from_sqs.getSetOperatorClause() != null) {
                soc = from_sqs.getSetOperatorClause();
            }
            for (int i_count3 = 1; i_count3 < this.GroupByItemList.size(); ++i_count3) {
                final Vector v_item_list = this.GroupByItemList.elementAt(i_count3);
                final SetOperatorClause n_soc = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 2);
                SelectQueryStatement t_sqs = to_sqs;
                for (SetOperatorClause t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                    t_sqs = t_soc.getSelectQueryStatement();
                }
                t_sqs.setSetOperatorClause(n_soc);
            }
            SelectQueryStatement t_sqs2 = to_sqs;
            for (SetOperatorClause t_soc2 = t_sqs2.getSetOperatorClause(); t_soc2 != null; t_soc2 = t_sqs2.getSetOperatorClause()) {
                t_sqs2 = t_soc2.getSelectQueryStatement();
            }
            if (soc != null) {
                t_sqs2.setSetOperatorClause(soc.toMSSQLServerSelect(to_sqs, from_sqs));
            }
        }
        if (gbs != null) {
            gbs.setCheckGroupByStatement(true);
        }
        return gbs;
    }
    
    public GroupByStatement toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        gbs.setObjectContext(this.context);
        if (this.ALLOption) {
            gbs.setALLOption(this.ALLOption);
        }
        if (this.WithOption != null) {
            gbs.setWithOption(this.WithOption);
        }
        final Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            boolean rollupAdded = false;
            for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                    final Vector v_ce = sc.getColumnExpression();
                    if (v_ce.elementAt(0) instanceof FunctionCalls) {
                        final FunctionCalls fc = v_ce.elementAt(0);
                        final String s_fn = fc.getFunctionName().getColumnName();
                        if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                            final Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                            final Vector newSelectItemList = new Vector();
                            if (selectItemList != null) {
                                for (int ii = 0; ii < selectItemList.size(); ++ii) {
                                    if (selectItemList.get(ii) instanceof SelectColumn) {
                                        final SelectColumn orgSC = selectItemList.get(ii);
                                        final Vector columnExpression = orgSC.getColumnExpression();
                                        final Vector newColumnExp = new Vector();
                                        final SelectColumn newSC = new SelectColumn();
                                        this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                        newSC.setColumnExpression(newColumnExp);
                                        newSC.setIsAS(orgSC.getIsAS());
                                        newSC.setAliasName(orgSC.getAliasName());
                                        newSC.setObjectContext(this.context);
                                        if (selectItemList.size() - 1 > ii) {
                                            newSC.setEndsWith(",");
                                        }
                                        newSelectItemList.add(newSC);
                                    }
                                }
                            }
                            this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 7);
                            to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                            rollupAdded = true;
                            if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                                final Vector newArguments = new Vector();
                                for (int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                    if (j > 0) {
                                        newArguments.add(", ");
                                    }
                                    newArguments.add(fc.getFunctionArguments().get(j));
                                }
                                sc.setColumnExpression(newArguments);
                                v_gil.addElement(sc.toSybaseSelect(to_sqs, from_sqs));
                            }
                            if (!rollupAdded) {
                                gbs.setWithOption(s_fn);
                                final Vector v_fa = fc.getFunctionArguments();
                                for (int cnt = 0; cnt < v_fa.size(); ++cnt) {
                                    v_gil.addElement(v_fa.elementAt(cnt).toSybaseSelect(to_sqs, from_sqs));
                                }
                            }
                        }
                        else {
                            v_gil.addElement(fc.toSybaseSelect(to_sqs, from_sqs));
                        }
                        if (!rollupAdded && !s_fn.equalsIgnoreCase("TRUNC") && !s_fn.equalsIgnoreCase("DECODE") && !s_fn.equalsIgnoreCase("FLOOR")) {
                            gbs.setWithOption(s_fn);
                            final Vector v_fa2 = fc.getFunctionArguments();
                            for (int cnt2 = 0; cnt2 < v_fa2.size(); ++cnt2) {
                                v_gil.addElement(v_fa2.elementAt(cnt2).toSybaseSelect(to_sqs, from_sqs));
                            }
                        }
                    }
                    else if (v_ce.elementAt(0) instanceof TableColumn) {
                        this.covertAilasToTableName(sc, from_sqs);
                        v_gil.addElement(sc.toSybaseSelect(to_sqs, from_sqs));
                    }
                    else {
                        v_gil.addElement(sc.toSybaseSelect(to_sqs, from_sqs));
                    }
                }
                else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                    final FunctionCalls fc2 = this.GroupByItemList.elementAt(i_count);
                    final String s_fn2 = fc2.getFunctionName().getColumnName();
                    if (s_fn2.equalsIgnoreCase("cube") || s_fn2.equalsIgnoreCase("rollup")) {
                        final Vector selectItemList2 = to_sqs.getSelectStatement().getSelectItemList();
                        final Vector newSelectItemList2 = new Vector();
                        if (selectItemList2 != null) {
                            for (int ii2 = 0; ii2 < selectItemList2.size(); ++ii2) {
                                if (selectItemList2.get(ii2) instanceof SelectColumn) {
                                    final SelectColumn orgSC2 = selectItemList2.get(ii2);
                                    final Vector columnExpression2 = orgSC2.getColumnExpression();
                                    final Vector newColumnExp2 = new Vector();
                                    final SelectColumn newSC2 = new SelectColumn();
                                    this.copyFromOneSCToAnother(columnExpression2, newColumnExp2);
                                    newSC2.setColumnExpression(newColumnExp2);
                                    newSC2.setIsAS(orgSC2.getIsAS());
                                    newSC2.setAliasName(orgSC2.getAliasName());
                                    newSC2.setObjectContext(this.context);
                                    if (selectItemList2.size() - 1 > ii2) {
                                        newSC2.setEndsWith(",");
                                    }
                                    newSelectItemList2.add(newSC2);
                                }
                            }
                        }
                        this.processCubeAndRollupConversion(fc2, from_sqs, to_sqs, 7);
                        to_sqs.getSelectStatement().setSelectItemList(newSelectItemList2);
                        rollupAdded = true;
                        if (fc2.getFunctionArguments() != null && fc2.getFunctionArguments().size() > 0) {
                            final Vector newArguments2 = new Vector();
                            for (int i = 0; i < fc2.getFunctionArguments().size(); ++i) {
                                if (i > 0) {
                                    newArguments2.add(", ");
                                }
                                newArguments2.add(fc2.getFunctionArguments().get(i));
                            }
                            final SelectColumn cubeRollupSelectColumn = new SelectColumn();
                            cubeRollupSelectColumn.setColumnExpression(newArguments2);
                            v_gil.addElement(cubeRollupSelectColumn.toSybaseSelect(to_sqs, from_sqs));
                        }
                        if (!rollupAdded) {
                            gbs.setWithOption(s_fn2);
                            final Vector v_fa2 = fc2.getFunctionArguments();
                            for (int cnt2 = 0; cnt2 < v_fa2.size(); ++cnt2) {
                                v_gil.addElement(v_fa2.elementAt(cnt2).toSybaseSelect(to_sqs, from_sqs));
                            }
                        }
                    }
                    else {
                        v_gil.addElement(fc2.toSybaseSelect(to_sqs, from_sqs));
                    }
                    if (!rollupAdded && !s_fn2.equalsIgnoreCase("TRUNC") && !s_fn2.equalsIgnoreCase("DECODE") && !s_fn2.equalsIgnoreCase("FLOOR")) {
                        gbs.setWithOption(s_fn2);
                        final Vector v_fa3 = fc2.getFunctionArguments();
                        for (int cnt3 = 0; cnt3 < v_fa3.size(); ++cnt3) {
                            v_gil.addElement(v_fa3.elementAt(cnt3).toSybaseSelect(to_sqs, from_sqs));
                        }
                    }
                }
            }
            this.processGroupByArguments(v_gil, v_gil, 0, false);
            gbs.setGroupByItemList(v_gil);
        }
        else {
            SetOperatorClause soc = null;
            if (this.GroupByItemList.elementAt(0).size() != 0) {
                final Vector singleGroupingSet = this.GroupByItemList.elementAt(0);
                for (int i_count2 = 0; i_count2 < singleGroupingSet.size(); ++i_count2) {
                    if (singleGroupingSet.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = singleGroupingSet.elementAt(i_count2);
                        v_gil.addElement(sc2.toSybaseSelect(to_sqs, from_sqs));
                    }
                }
                this.processGroupByArguments(v_gil, v_gil, 0, false);
                gbs.setGroupByItemList(v_gil);
            }
            else {
                gbs = null;
            }
            this.makeNonGroupedSelectItemsNull(this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 7);
            if (from_sqs.getSetOperatorClause() != null) {
                soc = from_sqs.getSetOperatorClause();
            }
            for (int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
                final Vector v_item_list = this.GroupByItemList.elementAt(i_count);
                final SetOperatorClause n_soc = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 7);
                SelectQueryStatement t_sqs = to_sqs;
                for (SetOperatorClause t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                    t_sqs = t_soc.getSelectQueryStatement();
                }
                t_sqs.setSetOperatorClause(n_soc);
            }
            SelectQueryStatement t_sqs2 = to_sqs;
            for (SetOperatorClause t_soc2 = t_sqs2.getSetOperatorClause(); t_soc2 != null; t_soc2 = t_sqs2.getSetOperatorClause()) {
                t_sqs2 = t_soc2.getSelectQueryStatement();
            }
            if (soc != null) {
                t_sqs2.setSetOperatorClause(soc.toSybaseSelect(to_sqs, from_sqs));
            }
        }
        if (gbs != null) {
            gbs.setCheckGroupByStatement(true);
        }
        return gbs;
    }
    
    public GroupByStatement toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        final Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            if (this.ALLOption) {
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        v_gil.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
                SetOperatorClause soc = null;
                if (from_sqs.getSetOperatorClause() != null) {
                    soc = from_sqs.getSetOperatorClause();
                }
                if (from_sqs.getWhereExpression() != null) {
                    final SetOperatorClause n_soc = this.createSetOperatorClause(null, from_sqs, to_sqs, 4);
                    SelectQueryStatement t_sqs = to_sqs;
                    t_sqs.setSetOperatorClause(n_soc);
                    for (SetOperatorClause t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                        t_sqs = t_soc.getSelectQueryStatement();
                    }
                    if (soc != null) {
                        t_sqs.setSetOperatorClause(soc.toPostgreSQLSelect(to_sqs, from_sqs));
                    }
                }
                else if (soc != null) {
                    to_sqs.setSetOperatorClause(soc.toPostgreSQLSelect(to_sqs, from_sqs));
                }
            }
            else {
                boolean rollupAdded = false;
                final SetOperatorClause soc2 = null;
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    rollupAdded = false;
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                        final Vector columnExpressionVector = sc2.getColumnExpression();
                        for (int i = 0; i < columnExpressionVector.size(); ++i) {
                            if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                                final FunctionCalls fc = columnExpressionVector.elementAt(i);
                                final String s_fn = fc.getFunctionName().getColumnName();
                                if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                                    final Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                                    final Vector newSelectItemList = new Vector();
                                    if (selectItemList != null) {
                                        for (int ii = 0; ii < selectItemList.size(); ++ii) {
                                            if (selectItemList.get(i) instanceof SelectColumn) {
                                                final SelectColumn orgSC = selectItemList.get(ii);
                                                final Vector columnExpression = orgSC.getColumnExpression();
                                                final Vector newColumnExp = new Vector();
                                                final SelectColumn newSC = new SelectColumn();
                                                this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                                newSC.setColumnExpression(newColumnExp);
                                                newSC.setIsAS(orgSC.getIsAS());
                                                newSC.setAliasName(orgSC.getAliasName());
                                                if (selectItemList.size() - 1 > ii) {
                                                    newSC.setEndsWith(",");
                                                }
                                                newSelectItemList.add(newSC);
                                            }
                                        }
                                    }
                                    this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 4);
                                    to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                                    rollupAdded = true;
                                    if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                                        final Vector newArguments = new Vector();
                                        for (int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                            if (j > 0) {
                                                newArguments.add(", ");
                                            }
                                            newArguments.add(fc.getFunctionArguments().get(j));
                                        }
                                        sc2.setColumnExpression(newArguments);
                                        v_gil.addElement(sc2.toPostgreSQLSelect(to_sqs, from_sqs));
                                    }
                                }
                            }
                        }
                        if (!rollupAdded) {
                            v_gil.addElement(sc2.toPostgreSQLSelect(to_sqs, from_sqs));
                        }
                    }
                    else if (this.GroupByItemList.elementAt(i_count2) instanceof FunctionCalls) {
                        final FunctionCalls fc2 = this.GroupByItemList.elementAt(i_count2);
                        final String s_fn2 = fc2.getFunctionName().getColumnName();
                        if (s_fn2.equalsIgnoreCase("cube") || s_fn2.equalsIgnoreCase("rollup")) {
                            final Vector selectItemList2 = to_sqs.getSelectStatement().getSelectItemList();
                            final Vector newSelectItemList2 = new Vector();
                            if (selectItemList2 != null) {
                                for (int k = 0; k < selectItemList2.size(); ++k) {
                                    if (selectItemList2.get(k) instanceof SelectColumn) {
                                        final SelectColumn orgSC2 = selectItemList2.get(k);
                                        final Vector columnExpression2 = orgSC2.getColumnExpression();
                                        final Vector newColumnExp2 = new Vector();
                                        final SelectColumn newSC2 = new SelectColumn();
                                        this.copyFromOneSCToAnother(columnExpression2, newColumnExp2);
                                        newSC2.setColumnExpression(newColumnExp2);
                                        newSC2.setIsAS(orgSC2.getIsAS());
                                        newSC2.setAliasName(orgSC2.getAliasName());
                                        newSelectItemList2.add(newSC2);
                                        if (selectItemList2.size() - 1 > k) {
                                            newSC2.setEndsWith(",");
                                        }
                                    }
                                }
                            }
                            this.processCubeAndRollupConversion(fc2, from_sqs, to_sqs, 4);
                            to_sqs.getSelectStatement().setSelectItemList(newSelectItemList2);
                        }
                        else {
                            v_gil.addElement(fc2.toPostgreSQLSelect(to_sqs, from_sqs));
                        }
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
        }
        else {
            SetOperatorClause soc = null;
            if (this.GroupByItemList.elementAt(0).size() != 0) {
                final Vector singleGroupingSet = this.GroupByItemList.elementAt(0);
                for (int i_count2 = 0; i_count2 < singleGroupingSet.size(); ++i_count2) {
                    if (singleGroupingSet.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = singleGroupingSet.elementAt(i_count2);
                        v_gil.addElement(sc2.toPostgreSQLSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
            else {
                gbs = null;
            }
            this.makeNonGroupedSelectItemsNull(this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 4);
            if (from_sqs.getSetOperatorClause() != null) {
                soc = from_sqs.getSetOperatorClause();
            }
            for (int i_count3 = 1; i_count3 < this.GroupByItemList.size(); ++i_count3) {
                final Vector v_item_list = this.GroupByItemList.elementAt(i_count3);
                final SetOperatorClause n_soc2 = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 4);
                SelectQueryStatement t_sqs2 = to_sqs;
                for (SetOperatorClause t_soc2 = t_sqs2.getSetOperatorClause(); t_soc2 != null; t_soc2 = t_sqs2.getSetOperatorClause()) {
                    t_sqs2 = t_soc2.getSelectQueryStatement();
                }
                t_sqs2.setSetOperatorClause(n_soc2);
            }
            SelectQueryStatement t_sqs3 = to_sqs;
            for (SetOperatorClause t_soc3 = t_sqs3.getSetOperatorClause(); t_soc3 != null; t_soc3 = t_sqs3.getSetOperatorClause()) {
                t_sqs3 = t_soc3.getSelectQueryStatement();
            }
            if (soc != null) {
                t_sqs3.setSetOperatorClause(soc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
        }
        if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
            final FunctionCalls fc3 = new FunctionCalls();
            final TableColumn tc = new TableColumn();
            tc.setColumnName(this.WithOption.toUpperCase());
            fc3.setFunctionName(tc);
            fc3.setFunctionArguments(v_gil);
            final Vector v_gblw = new Vector();
            v_gblw.add(fc3);
            gbs.setGroupByItemList(v_gblw);
        }
        return gbs;
    }
    
    public GroupByStatement toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        final Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            if (this.ALLOption) {
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        v_gil.addElement(sc.toInformixSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
                SetOperatorClause soc = null;
                if (from_sqs.getSetOperatorClause() != null) {
                    soc = from_sqs.getSetOperatorClause();
                }
                if (from_sqs.getWhereExpression() != null) {
                    final SetOperatorClause n_soc = this.createSetOperatorClause(null, from_sqs, to_sqs, 6);
                    SelectQueryStatement t_sqs = to_sqs;
                    t_sqs.setSetOperatorClause(n_soc);
                    for (SetOperatorClause t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                        t_sqs = t_soc.getSelectQueryStatement();
                    }
                    if (soc != null) {
                        t_sqs.setSetOperatorClause(soc.toInformixSelect(to_sqs, from_sqs));
                    }
                }
                else if (soc != null) {
                    to_sqs.setSetOperatorClause(soc.toInformixSelect(to_sqs, from_sqs));
                }
            }
            else {
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        v_gil.addElement(sc.toInformixSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
        }
        else {
            SetOperatorClause soc = null;
            if (this.GroupByItemList.elementAt(0).size() != 0) {
                final Vector singleGroupingSet = this.GroupByItemList.elementAt(0);
                for (int i_count2 = 0; i_count2 < singleGroupingSet.size(); ++i_count2) {
                    if (singleGroupingSet.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = singleGroupingSet.elementAt(i_count2);
                        this.covertAilasToTableName(sc2, from_sqs);
                        v_gil.addElement(sc2.toInformixSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
            else {
                gbs = null;
            }
            this.makeNonGroupedSelectItemsNull(this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 6);
            if (from_sqs.getSetOperatorClause() != null) {
                soc = from_sqs.getSetOperatorClause();
            }
            for (int i_count3 = 1; i_count3 < this.GroupByItemList.size(); ++i_count3) {
                final Vector v_item_list = this.GroupByItemList.elementAt(i_count3);
                final SetOperatorClause n_soc2 = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 6);
                SelectQueryStatement t_sqs2 = to_sqs;
                for (SetOperatorClause t_soc2 = t_sqs2.getSetOperatorClause(); t_soc2 != null; t_soc2 = t_sqs2.getSetOperatorClause()) {
                    t_sqs2 = t_soc2.getSelectQueryStatement();
                }
                t_sqs2.setSetOperatorClause(n_soc2);
            }
            SelectQueryStatement t_sqs3 = to_sqs;
            for (SetOperatorClause t_soc3 = t_sqs3.getSetOperatorClause(); t_soc3 != null; t_soc3 = t_sqs3.getSetOperatorClause()) {
                t_sqs3 = t_soc3.getSelectQueryStatement();
            }
            if (soc != null) {
                t_sqs3.setSetOperatorClause(soc.toInformixSelect(to_sqs, from_sqs));
            }
        }
        return gbs;
    }
    
    public GroupByStatement toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final GroupByStatement gbs = new GroupByStatement();
        gbs.setCommentClass(this.commentObj);
        gbs.setGroupClause(this.GroupClause);
        gbs.setHintClause(this.hintClause);
        Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            if (this.WithOption != null) {
                final Vector v_gl = new Vector();
                final SelectColumn sc_glsc = new SelectColumn();
                final Vector v_glsc = new Vector();
                final FunctionCalls fc = new FunctionCalls();
                final TableColumn tc = new TableColumn();
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        v_gil.addElement(sc.toOracleSelect(to_sqs, from_sqs));
                    }
                }
                tc.setColumnName(this.WithOption);
                fc.setFunctionName(tc);
                fc.setFunctionArguments(v_gil);
                v_glsc.addElement(fc);
                sc_glsc.setColumnExpression(v_glsc);
                v_gl.addElement(sc_glsc);
                gbs.setGroupByItemList(v_gl);
            }
            else if (this.ALLOption) {
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                        this.covertAilasToTableName(sc2, from_sqs);
                        v_gil.addElement(sc2.toOracleSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
                SetOperatorClause soc = null;
                if (from_sqs.getSetOperatorClause() != null) {
                    soc = from_sqs.getSetOperatorClause();
                }
                if (from_sqs.getWhereExpression() != null) {
                    final SetOperatorClause n_soc = this.createSetOperatorClause(null, from_sqs, to_sqs, 1);
                    SelectQueryStatement t_sqs = to_sqs;
                    t_sqs.setSetOperatorClause(n_soc);
                    for (SetOperatorClause t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                        t_sqs = t_soc.getSelectQueryStatement();
                    }
                    if (soc != null) {
                        t_sqs.setSetOperatorClause(soc.toOracleSelect(to_sqs, from_sqs));
                    }
                }
                else if (soc != null) {
                    to_sqs.setSetOperatorClause(soc.toOracleSelect(to_sqs, from_sqs));
                }
            }
            else {
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                        this.covertAilasToTableName(sc2, from_sqs);
                        v_gil.addElement(sc2.toOracleSelect(to_sqs, from_sqs));
                    }
                    else if (this.GroupByItemList.elementAt(i_count2) instanceof FunctionCalls) {
                        final FunctionCalls cubeRollUpFunc = this.GroupByItemList.elementAt(i_count2);
                        final Vector newFuncArgs = new Vector();
                        for (int fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                            final Object fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                            if (fnArg instanceof SelectColumn) {
                                newFuncArgs.addElement(((SelectColumn)fnArg).toOracleSelect(to_sqs, from_sqs));
                            }
                            else if (fnArg instanceof Vector) {
                                final Vector fnArgVec = (Vector)fnArg;
                                final SelectColumn newSelectColumn = new SelectColumn();
                                final Vector newVector = new Vector();
                                for (int vi = 0; vi < fnArgVec.size(); ++vi) {
                                    final SelectColumn fnArgCol = fnArgVec.elementAt(vi).toOracleSelect(to_sqs, from_sqs);
                                    if (vi != fnArgVec.size() - 1) {
                                        fnArgCol.setEndsWith(",");
                                    }
                                    newVector.add(fnArgCol);
                                }
                                newSelectColumn.setColumnExpression(newVector);
                                newSelectColumn.setOpenBrace("(");
                                newSelectColumn.setCloseBrace(")");
                                newFuncArgs.addElement(newSelectColumn);
                            }
                        }
                        cubeRollUpFunc.setFunctionArguments(newFuncArgs);
                        v_gil.addElement(cubeRollUpFunc);
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
        }
        else {
            gbs.setGroupingSetClause(this.GroupingSetClause);
            gbs.setOpenBraces(this.openBraces);
            gbs.setClosedBraces(this.closedBraces);
            for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                if (this.GroupByItemList.elementAt(i_count2) instanceof Vector) {
                    final Vector v_item_list = this.GroupByItemList.elementAt(i_count2);
                    v_gil = new Vector();
                    for (int i_icount = 0; i_icount < v_item_list.size(); ++i_icount) {
                        if (v_item_list.elementAt(i_icount) instanceof SelectColumn) {
                            v_gil.addElement(v_item_list.elementAt(i_icount).toOracleSelect(to_sqs, from_sqs));
                        }
                    }
                    v_tgil.addElement(v_gil);
                }
                else if (this.GroupByItemList.elementAt(i_count2) instanceof FunctionCalls) {
                    final FunctionCalls cubeRollUpFunc = this.GroupByItemList.elementAt(i_count2);
                    final Vector newFuncArgs = new Vector();
                    for (int fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                        final Object fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                        if (fnArg instanceof SelectColumn) {
                            newFuncArgs.addElement(((SelectColumn)fnArg).toOracleSelect(to_sqs, from_sqs));
                        }
                        else if (fnArg instanceof Vector) {
                            final Vector fnArgVec = (Vector)fnArg;
                            final SelectColumn newSelectColumn = new SelectColumn();
                            final Vector newVector = new Vector();
                            for (int vi = 0; vi < fnArgVec.size(); ++vi) {
                                final SelectColumn fnArgCol = fnArgVec.elementAt(vi).toOracleSelect(to_sqs, from_sqs);
                                if (vi != fnArgVec.size() - 1) {
                                    fnArgCol.setEndsWith(",");
                                }
                                newVector.add(fnArgCol);
                            }
                            newSelectColumn.setColumnExpression(newVector);
                            newSelectColumn.setOpenBrace("(");
                            newSelectColumn.setCloseBrace(")");
                            newFuncArgs.addElement(newSelectColumn);
                        }
                    }
                    cubeRollUpFunc.setFunctionArguments(newFuncArgs);
                    v_gil.addElement(cubeRollUpFunc);
                }
                else {
                    final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                    v_tgil.addElement(sc2.toOracleSelect(to_sqs, from_sqs));
                }
            }
            gbs.setGroupByItemList(v_tgil);
        }
        if (gbs != null) {
            gbs.setObjectContext(this.context);
        }
        return gbs;
    }
    
    public GroupByStatement toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        final Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            if (this.WithOption != null) {
                final Vector v_gl = new Vector();
                final SelectColumn sc_glsc = new SelectColumn();
                final Vector v_glsc = new Vector();
                final FunctionCalls fc = new FunctionCalls();
                final TableColumn tc = new TableColumn();
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        v_gil.addElement(sc.toTimesTenSelect(to_sqs, from_sqs));
                    }
                }
                tc.setColumnName(this.WithOption);
                fc.setFunctionName(tc);
                fc.setFunctionArguments(v_gil);
                v_glsc.addElement(fc);
                sc_glsc.setColumnExpression(v_glsc);
                v_gl.addElement(sc_glsc);
                gbs.setGroupByItemList(v_gl);
            }
            else if (this.ALLOption) {
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                        this.covertAilasToTableName(sc2, from_sqs);
                        v_gil.addElement(sc2.toTimesTenSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
            else {
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                        this.covertAilasToTableName(sc2, from_sqs);
                        v_gil.addElement(sc2.toTimesTenSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
        }
        else {
            if (this.GroupByItemList.elementAt(0).size() != 0) {
                final Vector singleGroupingSet = this.GroupByItemList.elementAt(0);
                for (int i_count3 = 0; i_count3 < singleGroupingSet.size(); ++i_count3) {
                    if (singleGroupingSet.elementAt(i_count3) instanceof SelectColumn) {
                        final SelectColumn sc3 = singleGroupingSet.elementAt(i_count3);
                        this.covertAilasToTableName(sc3, from_sqs);
                        v_gil.addElement(sc3.toTimesTenSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
            else {
                gbs = null;
            }
            this.makeNonGroupedSelectItemsNull(this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 10);
        }
        if (gbs != null) {
            gbs.setObjectContext(this.context);
        }
        return gbs;
    }
    
    public GroupByStatement toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        final Vector v_gil = new Vector();
        final Vector v_tgil = new Vector();
        if (this.GroupingSetClause == null) {
            if (this.ALLOption) {
                for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                    if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                        v_gil.addElement(sc.toNetezzaSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
                SetOperatorClause soc = null;
                if (from_sqs.getSetOperatorClause() != null) {
                    soc = from_sqs.getSetOperatorClause();
                }
                if (from_sqs.getWhereExpression() != null) {
                    final SetOperatorClause n_soc = this.createSetOperatorClause(null, from_sqs, to_sqs, 11);
                    SelectQueryStatement t_sqs = to_sqs;
                    t_sqs.setSetOperatorClause(n_soc);
                    for (SetOperatorClause t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                        t_sqs = t_soc.getSelectQueryStatement();
                    }
                    if (soc != null) {
                        t_sqs.setSetOperatorClause(soc.toNetezzaSelect(to_sqs, from_sqs));
                    }
                }
                else if (soc != null) {
                    to_sqs.setSetOperatorClause(soc.toNetezzaSelect(to_sqs, from_sqs));
                }
            }
            else {
                boolean rollupAdded = false;
                final SetOperatorClause soc2 = null;
                for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                    if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = this.GroupByItemList.elementAt(i_count2);
                        final Vector columnExpressionVector = sc2.getColumnExpression();
                        int i = 0;
                        while (i < columnExpressionVector.size()) {
                            if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                                final FunctionCalls fc = columnExpressionVector.elementAt(i);
                                final String s_fn = fc.getFunctionName().getColumnName();
                                if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                                    final Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                                    final Vector newSelectItemList = new Vector();
                                    if (selectItemList != null) {
                                        for (int ii = 0; ii < selectItemList.size(); ++ii) {
                                            if (selectItemList.get(ii) instanceof SelectColumn) {
                                                final SelectColumn orgSC = selectItemList.get(ii);
                                                final Vector columnExpression = orgSC.getColumnExpression();
                                                final Vector newColumnExp = new Vector();
                                                final SelectColumn newSC = new SelectColumn();
                                                this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                                newSC.setColumnExpression(newColumnExp);
                                                newSC.setIsAS(orgSC.getIsAS());
                                                newSC.setAliasName(orgSC.getAliasName());
                                                if (selectItemList.size() - 1 > ii) {
                                                    newSC.setEndsWith(",");
                                                }
                                                newSelectItemList.add(newSC);
                                            }
                                        }
                                    }
                                    this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 11);
                                    to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                                    rollupAdded = true;
                                    if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                                        final Vector newArguments = new Vector();
                                        for (int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                            if (j > 0) {
                                                newArguments.add(", ");
                                            }
                                            newArguments.add(fc.getFunctionArguments().get(j));
                                        }
                                        sc2.setColumnExpression(newArguments);
                                        v_gil.addElement(sc2.toNetezzaSelect(to_sqs, from_sqs));
                                    }
                                }
                                else {
                                    v_gil.addElement(fc.toNetezzaSelect(to_sqs, from_sqs));
                                    rollupAdded = true;
                                }
                                ++i;
                            }
                            else {
                                if (columnExpressionVector.elementAt(0) instanceof TableColumn) {
                                    this.covertAilasToTableName(sc2, from_sqs);
                                    v_gil.addElement(sc2.toNetezzaSelect(to_sqs, from_sqs));
                                    rollupAdded = true;
                                    break;
                                }
                                v_gil.addElement(sc2.toNetezzaSelect(to_sqs, from_sqs));
                                rollupAdded = true;
                                break;
                            }
                        }
                        if (!rollupAdded) {
                            v_gil.addElement(sc2.toNetezzaSelect(to_sqs, from_sqs));
                        }
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
        }
        else {
            SetOperatorClause soc = null;
            if (this.GroupByItemList.elementAt(0).size() != 0) {
                final Vector singleGroupingSet = this.GroupByItemList.elementAt(0);
                for (int i_count2 = 0; i_count2 < singleGroupingSet.size(); ++i_count2) {
                    if (singleGroupingSet.elementAt(i_count2) instanceof SelectColumn) {
                        final SelectColumn sc2 = singleGroupingSet.elementAt(i_count2);
                        v_gil.addElement(sc2.toNetezzaSelect(to_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(v_gil);
            }
            else {
                gbs = null;
            }
            this.makeNonGroupedSelectItemsNull(this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 11);
            if (from_sqs.getSetOperatorClause() != null) {
                soc = from_sqs.getSetOperatorClause();
            }
            for (int i_count3 = 1; i_count3 < this.GroupByItemList.size(); ++i_count3) {
                final Vector v_item_list = this.GroupByItemList.elementAt(i_count3);
                final SetOperatorClause n_soc2 = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 11);
                SelectQueryStatement t_sqs2 = to_sqs;
                for (SetOperatorClause t_soc2 = t_sqs2.getSetOperatorClause(); t_soc2 != null; t_soc2 = t_sqs2.getSetOperatorClause()) {
                    t_sqs2 = t_soc2.getSelectQueryStatement();
                }
                t_sqs2.setSetOperatorClause(n_soc2);
            }
            SelectQueryStatement t_sqs3 = to_sqs;
            for (SetOperatorClause t_soc3 = t_sqs3.getSetOperatorClause(); t_soc3 != null; t_soc3 = t_sqs3.getSetOperatorClause()) {
                t_sqs3 = t_soc3.getSelectQueryStatement();
            }
            if (soc != null) {
                t_sqs3.setSetOperatorClause(soc.toNetezzaSelect(to_sqs, from_sqs));
            }
        }
        return gbs;
    }
    
    private void makeNonGroupedSelectItemsNull(final Vector singleGroupingSet, final SelectQueryStatement from_sqs, final SelectQueryStatement to_sqs, final int database) throws ConvertException {
        final Vector selectItemList = this.createNewSelectItemListIfStar(from_sqs, to_sqs);
        Vector v_sil = null;
        if (selectItemList == null) {
            v_sil = from_sqs.getSelectStatement().getSelectItemList();
        }
        else {
            v_sil = selectItemList;
        }
        for (int i_count = 0; i_count < v_sil.size(); ++i_count) {
            if (v_sil.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn selectItemSC = v_sil.elementAt(i_count);
                final SelectColumn to_sql_selectItemSC = to_sqs.getSelectStatement().getSelectItemList().elementAt(i_count);
                if (selectItemSC.isAggregateFunction()) {
                    if (singleGroupingSet == null) {
                        if (database == 3 || database == 6) {
                            final Vector v = new Vector();
                            v.addElement(" CAST(NULL AS INT)");
                            to_sql_selectItemSC.setColumnExpression(v);
                        }
                        else {
                            final Vector v = new Vector();
                            v.addElement("0");
                            v.addElement("*");
                            v.addElement("NULL");
                            to_sql_selectItemSC.setColumnExpression(v);
                        }
                    }
                }
                else {
                    boolean areThere = true;
                    if (singleGroupingSet != null) {
                        areThere = this.checkIfSelectItemIsPresentInSingleGroupingSet(singleGroupingSet, selectItemSC);
                    }
                    if (!areThere) {
                        final Vector v2 = new Vector();
                        boolean castNullAsInt = false;
                        castNullAsInt = this.checkWhetherToCastNullAsInt(selectItemSC);
                        if (castNullAsInt && database == 4) {
                            v2.addElement("CAST(NULL AS INT)");
                        }
                        else {
                            v2.addElement("NULL");
                        }
                        to_sql_selectItemSC.setColumnExpression(v2);
                        if (selectItemSC.getAliasName() == null) {
                            if (database != 4) {
                                to_sql_selectItemSC.setAliasName(selectItemSC.getTheCoreSelectItem());
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean checkIfSelectItemIsPresentInSingleGroupingSet(final Vector singleGroupingSet, final SelectColumn selectItemSC) {
        for (int i = 0; i < singleGroupingSet.size(); ++i) {
            final SelectColumn groupingSetSC = singleGroupingSet.elementAt(i);
            if (this.checkSelectColumnEuality(groupingSetSC, selectItemSC)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkSelectColumnEuality(final SelectColumn groupingSetSC, final SelectColumn selectItemSC) {
        final String groupingSetCoreSelectItem = groupingSetSC.getTheCoreSelectItem();
        final String selectItemCoreSelectItem = selectItemSC.getTheCoreSelectItem();
        return selectItemCoreSelectItem.equalsIgnoreCase(groupingSetCoreSelectItem);
    }
    
    private Vector createNewSelectItemListIfStar(final SelectQueryStatement from_sqs, final SelectQueryStatement to_sqs) throws ConvertException {
        final Vector selectItemList = from_sqs.getSelectStatement().getSelectItemList();
        boolean starPresent = false;
        for (int i = 0; i < selectItemList.size(); ++i) {
            final SelectColumn selectItemSC = selectItemList.get(i);
            if (selectItemSC.toString().equals("* ") || selectItemSC.toString().indexOf(".*") != -1) {
                starPresent = true;
            }
        }
        if (!starPresent) {
            return null;
        }
        final Vector copyGroupByListInSelectClause = new Vector();
        final Vector comparisonGroupByList = new Vector();
        for (int j = 0; j < this.GroupByItemList.size(); ++j) {
            if (this.GroupByItemList.elementAt(j) instanceof Vector) {
                final Vector groupByListVector = this.GroupByItemList.get(j);
                for (int k = 0; k < groupByListVector.size(); ++k) {
                    final SelectColumn groupBySCToBeAdded = groupByListVector.get(k);
                    if (!comparisonGroupByList.contains(groupBySCToBeAdded.toString().toUpperCase().trim())) {
                        comparisonGroupByList.add(groupBySCToBeAdded.toString().toUpperCase().trim());
                        copyGroupByListInSelectClause.add(groupBySCToBeAdded);
                    }
                }
            }
            else if (this.GroupByItemList.elementAt(j) instanceof SelectColumn) {
                final SelectColumn groupBySCToBeAdded2 = this.GroupByItemList.get(j);
                if (!comparisonGroupByList.contains(groupBySCToBeAdded2.toString().toUpperCase().trim())) {
                    comparisonGroupByList.add(groupBySCToBeAdded2.toString().toUpperCase().trim());
                    copyGroupByListInSelectClause.add(groupBySCToBeAdded2);
                }
            }
        }
        final Vector newSelectItemListFromGroupingSet = new Vector();
        for (int l = 0; l < copyGroupByListInSelectClause.size(); ++l) {
            final SelectColumn sc = copyGroupByListInSelectClause.get(l);
            final SelectColumn toSQS_sc = sc.toANSISelect(to_sqs, from_sqs);
            if (l != copyGroupByListInSelectClause.size() - 1) {
                toSQS_sc.setEndsWith(",");
            }
            newSelectItemListFromGroupingSet.add(toSQS_sc);
        }
        final SelectStatement toSQSSelectStatement = to_sqs.getSelectStatement();
        final Vector v = toSQSSelectStatement.getSelectItemList();
        toSQSSelectStatement.setSelectItemList(newSelectItemListFromGroupingSet);
        return copyGroupByListInSelectClause;
    }
    
    public SetOperatorClause createSetOperatorClause(final Vector v_glist, final SelectQueryStatement from_sqs, final SelectQueryStatement to_sqs, final int database) throws ConvertException {
        final SetOperatorClause soc = new SetOperatorClause();
        GroupByStatement gbs = new GroupByStatement();
        final SelectQueryStatement set_sqs = new SelectQueryStatement();
        if (database == 1) {
            if (from_sqs.getSelectStatement() != null) {
                set_sqs.setSelectStatement(from_sqs.getSelectStatement().toOracleSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getFromClause() != null) {
                final FromClause fc = new FromClause();
                set_sqs.setFromClause(fc);
                set_sqs.setFromClause(from_sqs.getFromClause().toOracleSelect(set_sqs, from_sqs));
            }
            if (set_sqs.getWhereExpression() == null) {
                if (from_sqs.getWhereExpression() != null) {
                    set_sqs.setWhereExpression(from_sqs.getWhereExpression().toOracleSelect(set_sqs, from_sqs));
                }
            }
            else {
                set_sqs.getWhereExpression().addOperator("AND");
                set_sqs.getWhereExpression().addWhereExpression(from_sqs.getWhereExpression().toOracleSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHavingStatement() != null) {
                set_sqs.setHavingStatement(from_sqs.getHavingStatement().toOracleSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getIntoStatement() != null) {
                set_sqs.setIntoStatement(from_sqs.getIntoStatement().toOracleSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getForUpdateStatement() != null) {
                set_sqs.setForUpdateStatement(from_sqs.getForUpdateStatement().toOracleSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHierarchicalQueryClause() != null) {
                set_sqs.setHierarchicalQueryClause(from_sqs.getHierarchicalQueryClause().toOracleSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getLimitClause() != null) {
                set_sqs.setLimitClause(from_sqs.getLimitClause().toOracleSelect(set_sqs, from_sqs));
            }
            final Vector convertedSingleGroupingList = new Vector();
            if (v_glist != null && v_glist.size() != 0) {
                for (int i_count = 0; i_count < v_glist.size(); ++i_count) {
                    if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = v_glist.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        convertedSingleGroupingList.addElement(sc.toOracleSelect(set_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(convertedSingleGroupingList);
                gbs.setGroupClause("GROUP BY");
                gbs.setGroupingSetClause(null);
            }
            else {
                gbs = null;
            }
        }
        else if (database == 2 && v_glist != null) {
            if (from_sqs.getSelectStatement() != null) {
                set_sqs.setSelectStatement(from_sqs.getSelectStatement().toMSSQLServerSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getFromClause() != null) {
                set_sqs.setFromClause(from_sqs.getFromClause().toMSSQLServerSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getWhereExpression() != null) {
                set_sqs.setWhereExpression(from_sqs.getWhereExpression().toMSSQLServerSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHavingStatement() != null) {
                set_sqs.setHavingStatement(from_sqs.getHavingStatement().toMSSQLServerSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getIntoStatement() != null) {
                set_sqs.setIntoStatement(from_sqs.getIntoStatement().toMSSQLServerSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getForUpdateStatement() != null) {
                set_sqs.setForUpdateStatement(from_sqs.getForUpdateStatement().toMSSQLServerSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHierarchicalQueryClause() != null) {
                set_sqs.setHierarchicalQueryClause(from_sqs.getHierarchicalQueryClause().toMSSQLServerSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getLimitClause() != null) {
                set_sqs.setLimitClause(from_sqs.getLimitClause().toMSSQLServerSelect(set_sqs, from_sqs));
            }
            final Vector convertedSingleGroupingList = new Vector();
            if (v_glist != null && v_glist.size() != 0) {
                for (int i_count = 0; i_count < v_glist.size(); ++i_count) {
                    if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = v_glist.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        convertedSingleGroupingList.addElement(sc.toMSSQLServerSelect(set_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(convertedSingleGroupingList);
                gbs.setGroupClause("GROUP BY");
                gbs.setGroupingSetClause(null);
            }
            else {
                gbs = null;
            }
        }
        else if (database == 7 && v_glist != null) {
            if (from_sqs.getSelectStatement() != null) {
                set_sqs.setSelectStatement(from_sqs.getSelectStatement().toSybaseSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getFromClause() != null) {
                set_sqs.setFromClause(from_sqs.getFromClause().toSybaseSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getWhereExpression() != null) {
                set_sqs.setWhereExpression(from_sqs.getWhereExpression().toSybaseSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHavingStatement() != null) {
                set_sqs.setHavingStatement(from_sqs.getHavingStatement().toSybaseSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getIntoStatement() != null) {
                set_sqs.setIntoStatement(from_sqs.getIntoStatement().toSybaseSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getForUpdateStatement() != null) {
                set_sqs.setForUpdateStatement(from_sqs.getForUpdateStatement().toSybaseSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHierarchicalQueryClause() != null) {
                set_sqs.setHierarchicalQueryClause(from_sqs.getHierarchicalQueryClause().toSybaseSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getLimitClause() != null) {
                set_sqs.setLimitClause(from_sqs.getLimitClause().toSybaseSelect(set_sqs, from_sqs));
            }
            final Vector convertedSingleGroupingList = new Vector();
            if (v_glist != null && v_glist.size() != 0) {
                for (int i_count = 0; i_count < v_glist.size(); ++i_count) {
                    if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = v_glist.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        convertedSingleGroupingList.addElement(sc.toSybaseSelect(set_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(convertedSingleGroupingList);
                gbs.setGroupClause("GROUP BY");
                gbs.setGroupingSetClause(null);
            }
            else {
                gbs = null;
            }
        }
        else if (database == 3 && v_glist == null) {
            if (from_sqs.getSelectStatement() != null) {
                set_sqs.setSelectStatement(from_sqs.getSelectStatement().toDB2Select(set_sqs, from_sqs));
            }
            if (from_sqs.getFromClause() != null) {
                set_sqs.setFromClause(from_sqs.getFromClause().toDB2Select(set_sqs, from_sqs));
            }
            if (from_sqs.getWhereExpression() != null) {
                set_sqs.setWhereExpression(from_sqs.getWhereExpression().toDB2Select(set_sqs, from_sqs));
            }
            if (from_sqs.getHavingStatement() != null) {
                set_sqs.setHavingStatement(from_sqs.getHavingStatement().toDB2Select(set_sqs, from_sqs));
            }
            if (from_sqs.getLimitClause() != null) {
                set_sqs.setLimitClause(from_sqs.getLimitClause().toDB2Select(set_sqs, from_sqs));
            }
        }
        else if (database == 4) {
            if (from_sqs.getSelectStatement() != null) {
                set_sqs.setSelectStatement(from_sqs.getSelectStatement().toPostgreSQLSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getFromClause() != null) {
                set_sqs.setFromClause(from_sqs.getFromClause().toPostgreSQLSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getWhereExpression() != null) {
                set_sqs.setWhereExpression(from_sqs.getWhereExpression().toPostgreSQLSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHavingStatement() != null) {
                set_sqs.setHavingStatement(from_sqs.getHavingStatement().toPostgreSQLSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getIntoStatement() != null) {
                set_sqs.setIntoStatement(from_sqs.getIntoStatement().toPostgreSQLSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getLimitClause() != null) {
                set_sqs.setLimitClause(from_sqs.getLimitClause().toPostgreSQLSelect(set_sqs, from_sqs));
            }
            final Vector convertedSingleGroupingList = new Vector();
            if (v_glist != null && v_glist.size() != 0) {
                for (int i_count = 0; i_count < v_glist.size(); ++i_count) {
                    if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = v_glist.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        convertedSingleGroupingList.addElement(sc.toPostgreSQLSelect(set_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(convertedSingleGroupingList);
                gbs.setGroupClause("GROUP BY");
                gbs.setGroupingSetClause(null);
            }
            else {
                gbs = null;
            }
        }
        else if (database == 8) {
            if (from_sqs.getSelectStatement() != null) {
                set_sqs.setSelectStatement(from_sqs.getSelectStatement().toANSISelect(set_sqs, from_sqs));
            }
            if (from_sqs.getFromClause() != null) {
                set_sqs.setFromClause(from_sqs.getFromClause().toANSISelect(set_sqs, from_sqs));
            }
            if (from_sqs.getWhereExpression() != null) {
                set_sqs.setWhereExpression(from_sqs.getWhereExpression().toANSISelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHavingStatement() != null) {
                set_sqs.setHavingStatement(from_sqs.getHavingStatement().toANSISelect(set_sqs, from_sqs));
            }
            final Vector convertedSingleGroupingList = new Vector();
            if (v_glist != null && v_glist.size() != 0) {
                for (int i_count = 0; i_count < v_glist.size(); ++i_count) {
                    if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = v_glist.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        convertedSingleGroupingList.addElement(sc.toANSISelect(set_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(convertedSingleGroupingList);
                gbs.setGroupClause("GROUP BY");
                gbs.setGroupingSetClause(null);
            }
            else {
                gbs = null;
            }
        }
        else if (database == 6) {
            if (from_sqs.getSelectStatement() != null) {
                set_sqs.setSelectStatement(from_sqs.getSelectStatement().toInformixSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getFromClause() != null) {
                set_sqs.setFromClause(from_sqs.getFromClause().toInformixSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getWhereExpression() != null) {
                set_sqs.setWhereExpression(from_sqs.getWhereExpression().toInformixSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHavingStatement() != null) {
                set_sqs.setHavingStatement(from_sqs.getHavingStatement().toInformixSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getIntoStatement() != null) {
                set_sqs.setIntoStatement(from_sqs.getIntoStatement().toInformixSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getLimitClause() != null) {
                set_sqs.setLimitClause(from_sqs.getLimitClause().toInformixSelect(set_sqs, from_sqs));
            }
            final Vector convertedSingleGroupingList = new Vector();
            if (v_glist != null && v_glist.size() != 0) {
                for (int i_count = 0; i_count < v_glist.size(); ++i_count) {
                    if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = v_glist.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        convertedSingleGroupingList.addElement(sc.toInformixSelect(set_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(convertedSingleGroupingList);
                gbs.setGroupClause("GROUP BY");
                gbs.setGroupingSetClause(null);
            }
            else {
                gbs = null;
            }
        }
        else if (database == 11) {
            if (from_sqs.getSelectStatement() != null) {
                set_sqs.setSelectStatement(from_sqs.getSelectStatement().toNetezzaSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getFromClause() != null) {
                set_sqs.setFromClause(from_sqs.getFromClause().toNetezzaSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getWhereExpression() != null) {
                set_sqs.setWhereExpression(from_sqs.getWhereExpression().toNetezzaSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHavingStatement() != null) {
                set_sqs.setHavingStatement(from_sqs.getHavingStatement().toNetezzaSelect(set_sqs, from_sqs));
            }
            final Vector convertedSingleGroupingList = new Vector();
            if (v_glist != null && v_glist.size() != 0) {
                for (int i_count = 0; i_count < v_glist.size(); ++i_count) {
                    if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = v_glist.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        convertedSingleGroupingList.addElement(sc.toNetezzaSelect(set_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(convertedSingleGroupingList);
                gbs.setGroupClause("GROUP BY");
                gbs.setGroupingSetClause(null);
            }
            else {
                gbs = null;
            }
        }
        else if (database == 12) {
            if (from_sqs.getSelectStatement() != null) {
                set_sqs.setSelectStatement(from_sqs.getSelectStatement().toTeradataSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getFromClause() != null) {
                set_sqs.setFromClause(from_sqs.getFromClause().toTeradataSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getWhereExpression() != null) {
                set_sqs.setWhereExpression(from_sqs.getWhereExpression().toTeradataSelect(set_sqs, from_sqs));
            }
            if (from_sqs.getHavingStatement() != null) {
                set_sqs.setHavingStatement(from_sqs.getHavingStatement().toTeradataSelect(set_sqs, from_sqs));
            }
            final Vector convertedSingleGroupingList = new Vector();
            if (v_glist != null && v_glist.size() != 0) {
                for (int i_count = 0; i_count < v_glist.size(); ++i_count) {
                    if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc = v_glist.elementAt(i_count);
                        this.covertAilasToTableName(sc, from_sqs);
                        convertedSingleGroupingList.addElement(sc.toTeradataSelect(set_sqs, from_sqs));
                    }
                }
                gbs.setGroupByItemList(convertedSingleGroupingList);
                gbs.setGroupClause("GROUP BY");
                gbs.setGroupingSetClause(null);
            }
            else {
                gbs = null;
            }
        }
        this.makeNonGroupedSelectItemsNull(v_glist, from_sqs, set_sqs, database);
        final Vector convertedSingleGroupingList = new Vector();
        if (v_glist == null) {
            set_sqs.setWhereExpression(this.changeOperator(set_sqs.getWhereExpression()));
            for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.GroupByItemList.elementAt(i_count);
                    this.covertAilasToTableName(sc, from_sqs);
                    convertedSingleGroupingList.addElement(sc.toOracleSelect(set_sqs, from_sqs));
                }
            }
            gbs = new GroupByStatement();
            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause(null);
        }
        set_sqs.setGroupByStatement(gbs);
        soc.setSetClause("UNION ALL");
        soc.setSelectQueryStatement(set_sqs);
        return soc;
    }
    
    public WhereExpression changeOperator(final WhereExpression we) {
        if (we == null) {
            return null;
        }
        final Vector v_wi = we.getWhereItem();
        for (int i_count = 0; i_count < v_wi.size(); ++i_count) {
            if (v_wi.elementAt(i_count) instanceof WhereItem) {
                final WhereItem wi = v_wi.elementAt(i_count);
                wi.setBeginOperator("NOT ");
            }
            else if (v_wi.elementAt(i_count) instanceof WhereExpression) {
                this.changeOperator(v_wi.elementAt(i_count));
            }
        }
        return we;
    }
    
    public void covertAilasToTableName(final SelectColumn sc, final SelectQueryStatement from_sqs) {
        final Vector v_ce = sc.getColumnExpression();
        final SelectStatement ss = from_sqs.getSelectStatement();
        final Vector v_sil = ss.getSelectItemList();
        Vector v_nce = new Vector();
        if (v_ce.size() == 1) {
            if (v_ce.elementAt(0) instanceof TableColumn) {
                final TableColumn tc = v_ce.elementAt(0);
                final String colName = tc.getColumnName();
                if (tc.getTableName() == null) {
                    final FromTable ft = MetadataInfoUtil.getTableOfColumn(from_sqs, colName);
                    if (ft != null) {
                        return;
                    }
                }
                for (int i_count = 0; i_count < v_sil.size(); ++i_count) {
                    if (v_sil.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn t_sc = v_sil.elementAt(i_count);
                        if (t_sc.getAliasName() != null && t_sc.getAliasName().replace('\"', ' ').trim().equalsIgnoreCase(v_ce.elementAt(0).getColumnName())) {
                            v_nce = t_sc.getColumnExpression();
                            sc.setColumnExpression(v_nce);
                            break;
                        }
                    }
                }
            }
            else if ((from_sqs.isOracleLive() || from_sqs.isMSAzure()) && v_ce.elementAt(0) instanceof String) {
                sc.convertOrdinalNumberToColumn(from_sqs, v_ce);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
            sb.append("\t");
        }
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.GroupClause != null) {
            sb.append(this.GroupClause.toUpperCase());
        }
        if (this.ALLOption) {
            sb.append(" ALL");
        }
        if (this.hintClause != null) {
            sb.append(this.hintClause);
        }
        if (this.GroupingSetClause == null) {
            ++SelectQueryStatement.beautyTabCount;
            for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                    this.GroupByItemList.elementAt(i_count).setObjectContext(this.context);
                }
                if (i_count == this.GroupByItemList.size() - 1) {
                    sb.append("  " + this.GroupByItemList.elementAt(i_count).toString());
                }
                else {
                    sb.append(" " + this.GroupByItemList.elementAt(i_count).toString() + ",");
                    sb.append("\n");
                    for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                        sb.append("\t");
                    }
                }
            }
            --SelectQueryStatement.beautyTabCount;
        }
        else {
            final Vector groupingSetItems = new Vector();
            for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                if (this.GroupByItemList.elementAt(i_count2) instanceof Vector) {
                    groupingSetItems.add(this.GroupByItemList.elementAt(i_count2));
                }
            }
            sb.append(" " + this.GroupingSetClause.toUpperCase() + "(");
            for (int i_count2 = 0; i_count2 < groupingSetItems.size(); ++i_count2) {
                if (groupingSetItems.elementAt(i_count2) instanceof Vector) {
                    final Vector v_item_list = groupingSetItems.elementAt(i_count2);
                    if (this.openBraces != null) {
                        sb.append(this.openBraces);
                    }
                    for (int i_icount = 0; i_icount < v_item_list.size(); ++i_icount) {
                        if (v_item_list.elementAt(i_icount) instanceof SelectColumn) {
                            v_item_list.elementAt(i_icount).setObjectContext(this.context);
                        }
                        if (i_icount == v_item_list.size() - 1) {
                            sb.append(" " + v_item_list.elementAt(i_icount).toString());
                        }
                        else {
                            sb.append(" " + v_item_list.elementAt(i_icount).toString() + ",");
                        }
                    }
                    if (this.closedBraces != null) {
                        sb.append(this.closedBraces);
                    }
                    if (i_count2 != groupingSetItems.size() - 1) {
                        sb.append(",");
                    }
                }
            }
            sb.append(")");
            ++SelectQueryStatement.beautyTabCount;
            for (int i_count2 = 0; i_count2 < this.GroupByItemList.size(); ++i_count2) {
                if (this.GroupByItemList.elementAt(i_count2) instanceof SelectColumn) {
                    this.GroupByItemList.elementAt(i_count2).setObjectContext(this.context);
                    if (i_count2 == this.GroupByItemList.size() - 1) {
                        sb.append(" , " + this.GroupByItemList.elementAt(i_count2).toString());
                    }
                    else {
                        sb.append(", " + this.GroupByItemList.elementAt(i_count2).toString() + "");
                        sb.append("\n");
                        for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                            sb.append("\t");
                        }
                    }
                }
            }
            --SelectQueryStatement.beautyTabCount;
        }
        if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString().trim());
        }
        if (this.descOption != null) {
            sb.append(" " + this.descOption.toUpperCase() + " ");
        }
        if (this.WithOption != null) {
            sb.append(" WITH " + this.WithOption.toUpperCase());
        }
        return sb.toString();
    }
    
    private void processGroupByArguments(final Vector colExp, final Vector orgExp, int j, final boolean bool) {
        if (colExp != null) {
            for (int i = 0; i < colExp.size(); ++i) {
                if (!bool) {
                    j = i;
                }
                if (colExp.get(i) instanceof TableColumn) {
                    if (colExp.get(i).getColumnName().startsWith("@")) {
                        orgExp.removeElementAt(j);
                    }
                }
                else if (colExp.get(i) instanceof String) {
                    if (colExp.get(i).trim().startsWith("@")) {
                        orgExp.removeElementAt(j);
                    }
                    else if (colExp.get(i).trim().startsWith("'") && colExp.get(i).trim().endsWith("'")) {
                        orgExp.removeElementAt(j);
                    }
                }
                else if (colExp.get(i) instanceof SelectColumn) {
                    final Vector selColExp = colExp.get(i).getColumnExpression();
                    this.processGroupByArguments(selColExp, orgExp, j, true);
                }
                else if (colExp.get(i) instanceof FunctionCalls) {
                    final Vector FunctionArgs = colExp.get(i).getFunctionArguments();
                    if (FunctionArgs != null && FunctionArgs.size() == 1) {
                        this.processGroupByArguments(FunctionArgs, orgExp, j, true);
                    }
                }
            }
        }
    }
    
    public void processCubeAndRollupConversion(final FunctionCalls cubeOrRollup, final SelectQueryStatement from_sqs, final SelectQueryStatement to_sqs, final int convertInt) throws ConvertException {
        if (cubeOrRollup.getFunctionName().getTableName() == null && cubeOrRollup.getFunctionName().getOwnerName() == null) {
            if (cubeOrRollup.getFunctionName().getColumnName().equalsIgnoreCase("CUBE")) {
                cubeOrRollup.getFunctionArguments();
            }
            else if (cubeOrRollup.getFunctionName().getColumnName().equalsIgnoreCase("ROLLUP")) {
                final Vector functionArguments = cubeOrRollup.getFunctionArguments();
                this.processingElementsForRollup(functionArguments, from_sqs, to_sqs, convertInt);
                if (cubeOrRollup.getFunctionName() != null) {
                    cubeOrRollup.setFunctionName(null);
                }
            }
        }
    }
    
    public void processingElementsForRollup(final Vector functionArguments, final SelectQueryStatement from_sqs, final SelectQueryStatement to_sqs, final int convertInt) throws ConvertException {
        SetOperatorClause soc = null;
        for (int i = functionArguments.size() - 1; i >= 0; --i) {
            final Vector newArgsForCubeProcessing = new Vector();
            for (int j = 0; j < i; ++j) {
                newArgsForCubeProcessing.add(functionArguments.get(j));
            }
            this.makeNonGroupedSelectItemsNull(newArgsForCubeProcessing, from_sqs, to_sqs, convertInt);
            if (from_sqs.getSetOperatorClause() != null) {
                soc = from_sqs.getSetOperatorClause();
            }
            final Vector v_item_list = newArgsForCubeProcessing;
            SetOperatorClause n_soc = null;
            if (convertInt == 4) {
                n_soc = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 4);
            }
            else if (convertInt == 7) {
                n_soc = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 7);
            }
            else if (convertInt == 11) {
                n_soc = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 11);
            }
            SelectQueryStatement t_sqs;
            SetOperatorClause t_soc;
            for (t_sqs = to_sqs, t_soc = t_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                t_sqs = t_soc.getSelectQueryStatement();
            }
            t_sqs.setSetOperatorClause(n_soc);
            while (t_soc != null) {
                t_sqs = t_soc.getSelectQueryStatement();
                t_soc = t_sqs.getSetOperatorClause();
            }
            if (soc != null) {
                if (convertInt == 4) {
                    t_sqs.setSetOperatorClause(soc.toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else if (convertInt == 7) {
                    t_sqs.setSetOperatorClause(soc.toSybaseSelect(to_sqs, from_sqs));
                }
                else if (convertInt == 11) {
                    t_sqs.setSetOperatorClause(soc.toNetezzaSelect(to_sqs, from_sqs));
                }
            }
        }
    }
    
    private void copyFromOneSCToAnother(final Vector orgColumnExpression, final Vector cloneColumnExpression) {
        if (orgColumnExpression != null) {
            for (int i = 0; i < orgColumnExpression.size(); ++i) {
                if (orgColumnExpression.get(i) instanceof String) {
                    cloneColumnExpression.add(orgColumnExpression.get(i));
                }
                else if (orgColumnExpression.get(i) instanceof TableColumn) {
                    final TableColumn orgTC = orgColumnExpression.get(i);
                    final TableColumn cloneTC = new TableColumn();
                    cloneTC.setOwnerName(orgTC.getOwnerName());
                    cloneTC.setTableName(orgTC.getTableName());
                    cloneTC.setColumnName(orgTC.getColumnName());
                    cloneTC.setDot(orgTC.getDot());
                    cloneColumnExpression.add(cloneTC);
                }
                else if (orgColumnExpression.get(i) instanceof FunctionCalls) {
                    final FunctionCalls orgFC = orgColumnExpression.get(i);
                    final FunctionCalls newFC = new FunctionCalls();
                    final TableColumn orgTC2 = orgFC.getFunctionName();
                    final TableColumn cloneTC2 = new TableColumn();
                    if (orgFC != null && orgTC2 != null) {
                        cloneTC2.setOwnerName(orgTC2.getOwnerName());
                        cloneTC2.setTableName(orgTC2.getTableName());
                        cloneTC2.setColumnName(orgTC2.getColumnName());
                        cloneTC2.setDot(orgTC2.getDot());
                        newFC.setFunctionName(cloneTC2);
                        newFC.setArgumentQualifier(orgFC.getArgumentQualifier());
                        newFC.setAsDatatype(orgFC.getAsDatatype());
                        newFC.setForLength(orgFC.getForLength());
                        newFC.setFromInTrim(orgFC.getFromInTrim());
                        newFC.setLengthString(orgFC.getLengthString());
                    }
                    else if (orgFC.getArgumentQualifier() == null && orgFC.getFunctionArguments() == null) {
                        cloneTC2.setColumnName(orgFC.toString());
                        newFC.setFunctionName(cloneTC2);
                        newFC.setOpenBracesForFunctionNameRequired(false);
                        newFC.setAsDatatype(orgFC.getAsDatatype());
                        newFC.setForLength(orgFC.getForLength());
                        newFC.setFromInTrim(orgFC.getFromInTrim());
                        newFC.setLengthString(orgFC.getLengthString());
                    }
                    final Vector newFunctionArgs = new Vector();
                    final Vector orgFunctionArgs = orgFC.getFunctionArguments();
                    this.copyFromOneSCToAnother(orgFunctionArgs, newFunctionArgs);
                    newFC.setFunctionArguments(newFunctionArgs);
                    cloneColumnExpression.add(newFC);
                }
                else if (orgColumnExpression.get(i) instanceof SelectColumn) {
                    final SelectColumn cloneSC = new SelectColumn();
                    final SelectColumn orgSC = orgColumnExpression.get(i);
                    cloneSC.setAliasName(orgSC.getAliasName());
                    cloneSC.setIsAS(orgSC.getIsAS());
                    final Vector newExpression = new Vector();
                    final Vector orgExpression = orgSC.getColumnExpression();
                    this.copyFromOneSCToAnother(orgExpression, newExpression);
                    cloneSC.setColumnExpression(newExpression);
                    cloneColumnExpression.add(cloneSC);
                }
            }
        }
    }
    
    private boolean checkWhetherToCastNullAsInt(final SelectColumn selectItemSC) {
        final Vector selectItemSCColExpr = selectItemSC.getColumnExpression();
        for (int iCol = 0; iCol < selectItemSCColExpr.size(); ++iCol) {
            if (selectItemSCColExpr.get(iCol) instanceof FunctionCalls) {
                final FunctionCalls selectItemSCColFC = selectItemSCColExpr.get(iCol);
                if (selectItemSCColFC.getFunctionNameAsAString().equalsIgnoreCase("decode")) {
                    final String selectItemSCColFCLastArg = selectItemSCColFC.getFunctionArguments().lastElement().toString();
                    try {
                        Integer.parseInt(selectItemSCColFCLastArg);
                        return true;
                    }
                    catch (final NumberFormatException nfe) {
                        return false;
                    }
                }
            }
            else if (selectItemSCColExpr.get(iCol) instanceof SelectColumn) {
                return this.checkWhetherToCastNullAsInt(selectItemSCColExpr.get(iCol));
            }
        }
        return false;
    }
    
    public GroupByStatement toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final GroupByStatement gbs = new GroupByStatement();
        gbs.setGroupClause(this.GroupClause);
        final Vector v_gbl = new Vector();
        if (this.GroupingSetClause == null) {
            for (int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn temp = this.GroupByItemList.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs);
                    if (!this.isVectorContains(v_gbl, temp.toString())) {
                        v_gbl.addElement(temp);
                    }
                }
                else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                    final FunctionCalls fc = this.GroupByItemList.elementAt(i_count);
                    String s_fn = new String();
                    if (fc.getFunctionName() != null) {
                        final TableColumn c = fc.getFunctionName();
                        s_fn = fc.getFunctionName().getColumnName();
                    }
                    if ((s_fn != null && s_fn.equalsIgnoreCase("cube")) || s_fn.equalsIgnoreCase("rollup")) {
                        gbs.setWithOption(s_fn);
                        final Vector v_fa = fc.getFunctionArguments();
                        for (int cnt = 0; cnt < v_fa.size(); ++cnt) {
                            v_gbl.addElement(v_fa.elementAt(cnt).toVectorWiseSelect(to_sqs, from_sqs));
                        }
                    }
                    else {
                        v_gbl.addElement(fc.toVectorWiseSelect(to_sqs, from_sqs));
                    }
                }
            }
        }
        if (this.descOption != null) {
            gbs.setDescOption(this.descOption);
        }
        gbs.setGroupByItemList(v_gbl);
        if (this.GroupingSetClause != null) {
            throw new ConvertException();
        }
        if (this.ALLOption) {
            throw new ConvertException();
        }
        if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
            final FunctionCalls fc2 = new FunctionCalls();
            final TableColumn tc = new TableColumn();
            tc.setColumnName(this.WithOption.toUpperCase());
            fc2.setFunctionName(tc);
            fc2.setFunctionArguments(v_gbl);
            final Vector v_gblw = new Vector();
            v_gblw.add(fc2);
            gbs.setGroupByItemList(v_gblw);
        }
        return gbs;
    }
    
    public boolean isVectorContains(final Vector obj, final String groupby) {
        if (obj == null) {
            return false;
        }
        for (int i = 0; i < obj.size(); ++i) {
            if (obj.get(i) instanceof SelectColumn) {
                final String temp = obj.get(i).toString().trim();
                if (temp.equalsIgnoreCase(groupby)) {
                    return true;
                }
            }
        }
        return false;
    }
}
