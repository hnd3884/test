package com.adventnet.swissqlapi.sql.statement.select;

import java.util.Set;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class SelectStatement
{
    private String selectClause;
    private String reports;
    private String selectQualifier;
    private Vector distinctList;
    private String selectRowSpecifier;
    private String ifxSelectRowSpecifier;
    private String percentSpecifier;
    private int selectRowCount;
    private String selectSpecialQualifier;
    private Vector selectItemList;
    private String rowcountVariable;
    private String openBraceForSelectInInsert;
    private String xmlString;
    private String endBracesForXMLString;
    private UserObjectContext context;
    private ArrayList insertValList;
    private HintClause hintClause;
    private String openBraceForRowCount;
    private String closedBraceForRowCount;
    private boolean isMainQueryStatement;
    private CommentClass commentObj;
    
    public SelectStatement() {
        this.context = null;
        this.insertValList = null;
        this.isMainQueryStatement = false;
    }
    
    public void setSpecialCase(final String rep) {
        this.reports = rep;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setSelectClause(final String s) {
        this.selectClause = s;
    }
    
    public void setSelectItemList(final Vector v) {
        this.selectItemList = v;
    }
    
    public void setSelectQualifier(final String s_sq) {
        this.selectQualifier = s_sq;
    }
    
    public void setSelectRowSpecifier(final String s_srs) {
        this.selectRowSpecifier = s_srs;
    }
    
    public void setInformixRowSpecifier(final String s_srs) {
        this.ifxSelectRowSpecifier = s_srs;
    }
    
    public void setSelectRowCount(final int i_rc) {
        this.selectRowCount = i_rc;
    }
    
    public void setSelectRowCountVariable(final String rowcountVariable) {
        this.rowcountVariable = rowcountVariable;
    }
    
    public void setSelectSpecialQualifier(final String s_ssq) {
        this.selectSpecialQualifier = s_ssq;
    }
    
    public void setDistinctList(final Vector v_dl) {
        this.distinctList = v_dl;
    }
    
    public void setOpenBraceForSelectInInsertQuery(final String openBraceForSelectInInsert) {
        this.openBraceForSelectInInsert = openBraceForSelectInInsert;
    }
    
    public void setPercentSpecifier(final String s_ps) {
        this.percentSpecifier = s_ps;
    }
    
    public void setXMLString(final String xmlString) {
        this.xmlString = xmlString;
    }
    
    public void setXMLEndTag(final String endBracesForXMLString) {
        this.endBracesForXMLString = endBracesForXMLString;
    }
    
    public void setInsertValList(final ArrayList insertValList) {
        this.insertValList = insertValList;
    }
    
    public void setHintClause(final HintClause hintClause) {
        this.hintClause = hintClause;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setOpenBraceForRowCount(final String openBraceForRowCount) {
        this.openBraceForRowCount = openBraceForRowCount;
    }
    
    public void setClosedBraceForRowCount(final String closedBraceForRowCount) {
        this.closedBraceForRowCount = closedBraceForRowCount;
    }
    
    public void setIsMainQueryStatement(final boolean stmt) {
        this.isMainQueryStatement = stmt;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public String getSelectClause() {
        return this.selectClause;
    }
    
    public Vector getSelectItemList() {
        return this.selectItemList;
    }
    
    public String getSelectQualifier() {
        return this.selectQualifier;
    }
    
    public String getSelectRowSpecifier() {
        return this.selectRowSpecifier;
    }
    
    public String getInformixRowSpecifier() {
        return this.ifxSelectRowSpecifier;
    }
    
    public int getSelectRowCount() {
        return this.selectRowCount;
    }
    
    public String getSelectRowCountVariable() {
        return this.rowcountVariable;
    }
    
    public String getSelectSpecialQualifier() {
        return this.selectSpecialQualifier;
    }
    
    public Vector getDistinctList() {
        return this.distinctList;
    }
    
    public String getPercentSpecifier() {
        return this.percentSpecifier;
    }
    
    public HintClause getHintClause() {
        return this.hintClause;
    }
    
    public String getOpenBraceForRowCount() {
        return this.openBraceForRowCount;
    }
    
    public String getClosedBraceForRowCount() {
        return this.closedBraceForRowCount;
    }
    
    public boolean getIsMainQueryStatement() {
        return this.isMainQueryStatement;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.openBraceForSelectInInsert != null) {
            sb.append(this.openBraceForSelectInInsert);
        }
        sb.append(this.selectClause.toUpperCase());
        if (this.reports != null) {
            sb.append(" " + this.reports.toUpperCase());
        }
        if (this.hintClause != null) {
            sb.append(" " + this.hintClause);
        }
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.ifxSelectRowSpecifier != null) {
            sb.append(" " + this.ifxSelectRowSpecifier.toUpperCase() + " " + this.selectRowCount);
        }
        if (this.selectQualifier != null) {
            sb.append(" " + this.selectQualifier.toUpperCase());
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                sb.append("(");
                for (int i_count = 0; i_count < this.distinctList.size(); ++i_count) {
                    if (i_count == this.distinctList.size() - 1) {
                        sb.append(this.distinctList.elementAt(i_count).toString());
                    }
                    else {
                        sb.append(this.distinctList.elementAt(i_count).toString() + ",");
                    }
                }
                sb.append(")");
            }
        }
        if (this.selectRowSpecifier != null) {
            if (this.rowcountVariable != null) {
                sb.append(" " + this.selectRowSpecifier.toUpperCase() + " (" + this.rowcountVariable + ")");
            }
            else {
                sb.append(" " + this.selectRowSpecifier.toUpperCase());
                if (this.openBraceForRowCount != null) {
                    sb.append(this.openBraceForRowCount + this.selectRowCount + this.closedBraceForRowCount);
                }
                else {
                    sb.append(" " + this.selectRowCount);
                }
            }
            if (this.percentSpecifier != null) {
                sb.append(" " + this.percentSpecifier.toUpperCase());
            }
        }
        if (this.selectSpecialQualifier != null) {
            sb.append(" " + this.selectSpecialQualifier.toUpperCase());
        }
        if (this.xmlString != null) {
            sb.append(" " + this.xmlString);
        }
        final int size = this.selectItemList.size();
        SelectQueryStatement.beautyTabCount += 2;
        for (int i = 0; i < size; ++i) {
            if (size > 1) {
                sb.append("\n");
                for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                    sb.append("\t");
                }
            }
            if (this.selectItemList.elementAt(i) instanceof SelectColumn) {
                this.selectItemList.elementAt(i).setObjectContext(this.context);
            }
            sb.append(" " + this.selectItemList.elementAt(i).toString());
        }
        if (this.endBracesForXMLString != null) {
            sb.append(this.endBracesForXMLString);
        }
        SelectQueryStatement.beautyTabCount -= 2;
        return sb.toString();
    }
    
    public SelectStatement toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        if (this.selectQualifier != null) {
            final GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
            final HavingStatement hs = from_sqs.getHavingStatement();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (gbs_gb != null) {
                    to_sqs.setGroupByStatement(gbs_gb.toANSISelect(to_sqs, from_sqs));
                    this.addGroupByItems(to_sqs, from_sqs);
                }
                else {
                    to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
                }
                if (hs != null) {
                    to_sqs.setHavingStatement(hs.toANSISelect(to_sqs, from_sqs));
                    this.addHavingItem(to_sqs);
                }
                else {
                    to_sqs.setHavingStatement(this.createHavingStatement());
                }
                this.setSelectItemList(t_ss, from_sqs, to_sqs);
            }
            else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
                t_ss.setSelectQualifier("DISTINCT");
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null) {
            throw new ConvertException();
        }
        if (this.selectSpecialQualifier != null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("/* " + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
        }
        if ((this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) || this.selectQualifier == null) {
            final Vector v_sil = new Vector();
            for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
                if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
                }
                else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
            }
            t_ss.setSelectItemList(v_sil);
        }
        return t_ss;
    }
    
    public SelectStatement toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        t_ss.setHintClause(this.hintClause);
        if (this.commentObj != null) {
            String commentStr = this.commentObj.toString();
            final String commId = "%SSTD%";
            if (commentStr.indexOf(commId) != -1) {
                commentStr = commentStr.replaceAll("/\\*", "").replaceAll(commId, "").replaceAll("\\*/", "");
            }
            to_sqs.setTeradataComment(commentStr);
        }
        if (this.selectQualifier != null) {
            final GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
            final HavingStatement hs = from_sqs.getHavingStatement();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (gbs_gb != null) {
                    to_sqs.setGroupByStatement(gbs_gb.toTeradataSelect(to_sqs, from_sqs));
                    this.addGroupByItems(to_sqs, from_sqs);
                }
                else {
                    to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
                }
                if (hs != null) {
                    to_sqs.setHavingStatement(hs.toTeradataSelect(to_sqs, from_sqs));
                    this.addHavingItem(to_sqs);
                }
                else {
                    to_sqs.setHavingStatement(this.createHavingStatement());
                }
                this.setSelectItemList(t_ss, from_sqs, to_sqs);
            }
            else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
                t_ss.setSelectQualifier("DISTINCT");
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null) {
            throw new ConvertException();
        }
        if (this.selectSpecialQualifier != null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("/* " + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
        }
        if ((this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) || this.selectQualifier == null) {
            final Vector v_sil = new Vector();
            for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
                if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc1 = this.selectItemList.elementAt(i_count);
                    if (from_sqs != null && !from_sqs.getTopLevel()) {
                        if (sc1.getColumnExpression().size() == 1 && !(sc1.getColumnExpression().get(0) instanceof TableColumn) && sc1.getAliasName() == null) {
                            String aliasForExpr = sc1.getTheCoreSelectItem().trim();
                            if (sc1.getColumnExpression().get(0) instanceof SelectColumn && sc1.getColumnExpression().get(0).getColumnExpression().size() == 1) {
                                if (!(sc1.getColumnExpression().get(0).getColumnExpression().get(0) instanceof TableColumn)) {
                                    aliasForExpr = sc1.getColumnExpression().get(0).toString();
                                }
                                else {
                                    aliasForExpr = "";
                                }
                            }
                            if (aliasForExpr.lastIndexOf(",") != -1) {
                                aliasForExpr = aliasForExpr.substring(0, aliasForExpr.lastIndexOf(",")).trim();
                            }
                            if (aliasForExpr.indexOf("*/") != -1) {
                                aliasForExpr = aliasForExpr.substring(aliasForExpr.indexOf("*/") + 2).trim();
                            }
                            boolean isNum = false;
                            try {
                                Double.parseDouble(aliasForExpr);
                                isNum = true;
                            }
                            catch (final NumberFormatException nfe) {
                                isNum = false;
                            }
                            if (!isNum && !aliasForExpr.toLowerCase().startsWith("case") && aliasForExpr.indexOf(".") != -1 && aliasForExpr.indexOf(".") == aliasForExpr.lastIndexOf(".") && aliasForExpr.indexOf("(") == -1) {
                                aliasForExpr = aliasForExpr.substring(aliasForExpr.lastIndexOf(".") + 1);
                            }
                            if (aliasForExpr.indexOf("/*") != -1) {
                                if (aliasForExpr.indexOf("/*") == 0 && aliasForExpr.indexOf("*/") < aliasForExpr.length() - 1) {
                                    aliasForExpr = aliasForExpr.substring(aliasForExpr.indexOf("*/") + 1).trim();
                                }
                                else {
                                    aliasForExpr = aliasForExpr.substring(0, aliasForExpr.indexOf("/*")).trim();
                                }
                            }
                            if (!aliasForExpr.equalsIgnoreCase("*") && !aliasForExpr.startsWith("*") && !aliasForExpr.endsWith("*")) {
                                if (aliasForExpr.length() > 30) {
                                    aliasForExpr = aliasForExpr.substring(0, 29);
                                }
                                if (aliasForExpr.length() > 0) {
                                    sc1.setAliasForExpression("\"" + aliasForExpr.replaceAll("\n", " ").replaceAll("\t", " ") + "\"");
                                }
                            }
                        }
                        if (sc1.getAliasName() == null && sc1.getAliasForExpression() != null) {
                            sc1.setAliasName(sc1.getAliasForExpression());
                        }
                    }
                    v_sil.addElement(sc1.toTeradataSelect(to_sqs, from_sqs));
                }
                else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
            }
            t_ss.setSelectItemList(v_sil);
        }
        return t_ss;
    }
    
    public SelectStatement toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setOpenBraceForSelectInInsertQuery(this.openBraceForSelectInInsert);
        t_ss.setSelectClause(this.selectClause);
        if (this.selectQualifier != null) {
            final GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
            final HavingStatement hs = from_sqs.getHavingStatement();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (gbs_gb != null) {
                    to_sqs.setGroupByStatement(gbs_gb.toDB2Select(to_sqs, from_sqs));
                    this.addGroupByItems(to_sqs, from_sqs);
                }
                else {
                    to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
                }
                if (hs != null) {
                    to_sqs.setHavingStatement(hs.toDB2Select(to_sqs, from_sqs));
                    this.addHavingItem(to_sqs);
                }
                else {
                    to_sqs.setHavingStatement(this.createHavingStatement());
                }
                this.setSelectItemList(t_ss, from_sqs, to_sqs);
            }
            else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
                t_ss.setSelectQualifier("DISTINCT");
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) {
            if (this.percentSpecifier != null) {
                throw new ConvertException();
            }
            final FetchClause fc = new FetchClause();
            if (from_sqs.getFetchClause() != null) {
                throw new ConvertException();
            }
            fc.setFetchFirstClause("FETCH FIRST");
            fc.setFetchCount("" + this.selectRowCount);
            fc.setRowOnlyClause("ROWS ONLY");
            to_sqs.setFetchClause(fc);
        }
        if ((this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) || this.selectQualifier == null) {
            final Vector v_sil = new Vector();
            int count = 0;
            for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
                if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.selectItemList.elementAt(i_count);
                    if (this.insertValList != null) {
                        sc.setCorrespondingTableColumn(this.insertValList.get(count));
                    }
                    final Vector v_ce = sc.getColumnExpression();
                    for (int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                        if (v_ce.elementAt(ii_count) instanceof String) {
                            String s_ce = v_ce.elementAt(ii_count);
                            if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                                final FromClause fc2 = from_sqs.getFromClause();
                                final Vector v_fil = fc2.getFromItemList();
                                if (v_fil.size() > 1) {
                                    for (int countNum = 0; countNum < v_fil.size(); ++countNum) {
                                        if (v_fil.elementAt(countNum) instanceof FromTable) {
                                            final FromTable ft = v_fil.elementAt(countNum);
                                            if (ft.getAliasName() == null) {
                                                final Object o_tn = ft.getTableName();
                                                if (!(o_tn instanceof String)) {
                                                    throw new ConvertException();
                                                }
                                                s_ce = (String)o_tn + ".*";
                                            }
                                            else {
                                                s_ce = ft.getAliasName() + ".*";
                                            }
                                        }
                                        else if (v_fil.elementAt(countNum) instanceof FromClause) {
                                            final Vector newFromItemList = v_fil.get(countNum).getFromItemList();
                                            this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFromItemList, ii_count);
                                        }
                                        if (countNum == 0) {
                                            v_ce.setElementAt(s_ce, ii_count);
                                        }
                                        else {
                                            v_ce.add(",");
                                            v_ce.add(s_ce);
                                        }
                                    }
                                }
                                else if (v_fil.elementAt(0) instanceof FromTable) {
                                    final FromTable ft2 = v_fil.elementAt(0);
                                    if (ft2.getAliasName() == null) {
                                        final Object o_tn2 = ft2.getTableName();
                                        if (!(o_tn2 instanceof String)) {
                                            throw new ConvertException();
                                        }
                                        s_ce = (String)o_tn2 + ".*";
                                    }
                                    else {
                                        s_ce = ft2.getAliasName() + ".*";
                                    }
                                    v_ce.setElementAt(s_ce, ii_count);
                                }
                                else if (v_fil.elementAt(0) instanceof FromClause) {
                                    final Vector newFCVector = v_fil.get(0).getFromItemList();
                                    this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector, ii_count);
                                }
                            }
                        }
                    }
                    sc.setColumnExpression(v_ce);
                    v_sil.addElement(this.selectItemList.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
                }
                else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
                ++count;
            }
            t_ss.setSelectItemList(v_sil);
        }
        return t_ss;
    }
    
    public SelectStatement toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        if (this.reports != null) {
            t_ss.setSpecialCase(this.reports);
        }
        if (this.selectQualifier != null) {
            final GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
            final HavingStatement hs = from_sqs.getHavingStatement();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (gbs_gb != null) {
                    to_sqs.setGroupByStatement(gbs_gb.toMySQLSelect(to_sqs, from_sqs));
                    this.addGroupByItems(to_sqs, from_sqs);
                }
                else {
                    to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
                }
                if (hs != null) {
                    to_sqs.setHavingStatement(hs.toMySQLSelect(to_sqs, from_sqs));
                    this.addHavingItem(to_sqs);
                }
                else {
                    to_sqs.setHavingStatement(this.createHavingStatement());
                }
                this.setSelectItemList(t_ss, from_sqs, to_sqs);
            }
            else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
                t_ss.setSelectQualifier("DISTINCT");
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null) {
            if (this.percentSpecifier != null) {
                throw new ConvertException();
            }
            final LimitClause lc = new LimitClause();
            if (from_sqs.getLimitClause() != null) {
                throw new ConvertException();
            }
            lc.setLimitClause("LIMIT");
            lc.setLimitValue("" + this.selectRowCount);
            to_sqs.setLimitClause(lc);
        }
        if (this.selectSpecialQualifier != null) {
            t_ss.setSelectSpecialQualifier(this.selectSpecialQualifier);
        }
        if ((this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) || this.selectQualifier == null) {
            final Vector v_sil = new Vector();
            for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
                if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.selectItemList.elementAt(i_count);
                    if (this.isMainQueryStatement && !this.hasStarInSelectColumn(sc)) {
                        if (sc.getAliasName() == null) {
                            String orginalAliasName = sc.toString();
                            orginalAliasName = orginalAliasName.replaceAll("\"", "");
                            orginalAliasName = orginalAliasName.replaceAll("'", "");
                            orginalAliasName = orginalAliasName.replaceAll("`", "");
                            if (orginalAliasName.endsWith(",")) {
                                orginalAliasName = orginalAliasName.substring(0, orginalAliasName.length() - 1);
                            }
                            orginalAliasName = "`" + orginalAliasName + "`";
                            sc.setAliasName(orginalAliasName);
                            sc.setIsAS("as");
                        }
                        else if (sc.getAliasName() != null && sc.getIsAS() == null) {
                            sc.setIsAS("as");
                        }
                    }
                    final Vector v_ce = sc.getColumnExpression();
                    for (int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                        if (v_ce.elementAt(ii_count) instanceof String) {
                            String s_ce = v_ce.elementAt(ii_count);
                            if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                                final FromClause fc = from_sqs.getFromClause();
                                final Vector v_fil = fc.getFromItemList();
                                if (v_fil.size() <= 1) {
                                    final FromTable ft = v_fil.elementAt(0);
                                    if (ft.getAliasName() == null) {
                                        final Object o_tn = ft.getTableName();
                                        if (!(o_tn instanceof String)) {
                                            throw new ConvertException();
                                        }
                                    }
                                    else {
                                        s_ce = ft.getAliasName() + ".*";
                                    }
                                    v_ce.setElementAt(s_ce, ii_count);
                                }
                            }
                        }
                    }
                    sc.setColumnExpression(v_ce);
                    v_sil.addElement(this.selectItemList.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
            }
            t_ss.setSelectItemList(v_sil);
        }
        return t_ss;
    }
    
    public HavingStatement createHavingStatement() {
        final HavingStatement hs = new HavingStatement();
        final Vector v_hi = new Vector();
        final Vector v_fa = new Vector();
        final SelectColumn sc = new SelectColumn();
        final SelectColumn sc_new = new SelectColumn();
        final SelectColumn sc_temp = new SelectColumn();
        final Vector v_new = new Vector();
        final Vector v_fi = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_fa = new Vector();
        v_hi.addElement("(");
        tc.setColumnName("COUNT");
        fc.setFunctionName(tc);
        vec_fa.addElement("*");
        sc_new.setColumnExpression(vec_fa);
        v_fa.addElement(sc_new);
        fc.setFunctionArguments(v_fa);
        v_fi.addElement(fc);
        sc.setColumnExpression(v_fi);
        v_hi.addElement(sc);
        v_hi.addElement("<");
        v_new.addElement("2");
        sc_temp.setColumnExpression(v_new);
        v_hi.addElement(sc_temp);
        v_hi.addElement(")");
        hs.setHavingClause("HAVING");
        hs.setHavingItems(v_hi);
        return hs;
    }
    
    public void addHavingItem(final SelectQueryStatement to_sqs) {
        final HavingStatement hs = to_sqs.getHavingStatement();
        final Vector v_to_hi = hs.getHavingItems();
        final Vector v_hi = new Vector();
        for (int i_count = 0; i_count < v_to_hi.size(); ++i_count) {
            v_hi.addElement(v_to_hi.elementAt(i_count));
        }
        v_hi.addElement("AND");
        final SelectColumn sc = new SelectColumn();
        final SelectColumn sc_new = new SelectColumn();
        final SelectColumn sc_temp = new SelectColumn();
        final Vector v_new = new Vector();
        final Vector v_fi = new Vector();
        final Vector v_fa = new Vector();
        final TableColumn tc = new TableColumn();
        final FunctionCalls fc = new FunctionCalls();
        final Vector vec_fa = new Vector();
        v_hi.addElement("(");
        tc.setColumnName("COUNT");
        fc.setFunctionName(tc);
        vec_fa.addElement("*");
        sc_new.setColumnExpression(vec_fa);
        v_fa.addElement(sc);
        fc.setFunctionArguments(v_fa);
        v_fi.addElement(fc);
        sc.setColumnExpression(v_fi);
        v_hi.addElement(sc);
        v_hi.addElement("<");
        v_new.addElement("2");
        sc_temp.setColumnExpression(v_new);
        v_hi.addElement(sc_temp);
        v_hi.addElement(")");
        hs.setHavingItems(v_hi);
    }
    
    public SelectStatement toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        if (this.selectQualifier != null) {
            if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
                t_ss.setSelectQualifier("DISTINCT");
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
            final Vector v_dl = new Vector();
            if (this.distinctList != null) {
                for (int i_count = 0; i_count < this.distinctList.size(); ++i_count) {
                    v_dl.addElement(this.distinctList.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
            }
            t_ss.setDistinctList(v_dl);
        }
        if (this.selectRowSpecifier != null) {
            if (this.percentSpecifier != null) {
                throw new ConvertException();
            }
            final LimitClause lc = new LimitClause();
            if (from_sqs.getLimitClause() != null) {
                throw new ConvertException();
            }
            lc.setLimitClause("LIMIT");
            lc.setLimitValue("" + this.selectRowCount);
            to_sqs.setLimitClause(lc);
        }
        if (this.selectSpecialQualifier != null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("/* " + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
        }
        if (this.selectItemList != null) {
            final Vector v_sil = new Vector();
            for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
                if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.selectItemList.elementAt(i_count);
                    sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
                    if (from_sqs != null) {
                        if (from_sqs.isSelectWithoutSetClause()) {
                            sc.convertSelectColumnToNumericDataType("SIGNED");
                        }
                        else {
                            sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                        }
                    }
                    final Vector v_ce = sc.getColumnExpression();
                    for (int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                        if (v_ce.elementAt(ii_count) instanceof String) {
                            String s_ce = v_ce.elementAt(ii_count);
                            if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                                final FromClause fc = from_sqs.getFromClause();
                                final Vector v_fil = fc.getFromItemList();
                                if (v_fil.size() <= 1) {
                                    final FromTable ft = v_fil.elementAt(0);
                                    if (ft.getAliasName() == null) {
                                        final Object o_tn = ft.getTableName();
                                        if (!(o_tn instanceof String)) {
                                            throw new ConvertException();
                                        }
                                    }
                                    else {
                                        s_ce = ft.getAliasName() + ".*";
                                    }
                                    v_ce.setElementAt(changeBackTip(s_ce), ii_count);
                                }
                            }
                        }
                        else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                            final TableColumn tc = v_ce.elementAt(ii_count);
                            tc.setColumnName(checkandRemoveDoubleQuoteForPostgresIdentifier(changeBackTip(tc.getColumnName()), from_sqs != null && from_sqs.getReportsMeta()));
                            tc.setTableName(checkandRemoveDoubleQuoteForPostgresIdentifier(changeBackTip(tc.getTableName()), from_sqs != null && from_sqs.getReportsMeta()));
                        }
                    }
                    sc.setColumnExpression(v_ce);
                    if (to_sqs != null) {
                        to_sqs.setCurrentIndexPosition(i_count);
                    }
                    v_sil.addElement(this.selectItemList.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
            }
            if (to_sqs != null) {
                to_sqs.setCurrentIndexPosition(-1);
            }
            t_ss.setSelectItemList(v_sil);
        }
        return t_ss;
    }
    
    public SelectStatement toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        t_ss.setCommentClass(this.commentObj);
        if (this.selectQualifier != null) {
            final GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
            final HavingStatement hs = from_sqs.getHavingStatement();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (gbs_gb != null) {
                    to_sqs.setGroupByStatement(gbs_gb.toMSSQLServerSelect(to_sqs, from_sqs));
                    this.addGroupByItems(to_sqs, from_sqs);
                }
                else {
                    to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
                }
                if (hs != null) {
                    to_sqs.setHavingStatement(hs.toMSSQLServerSelect(to_sqs, from_sqs));
                    this.addHavingItem(to_sqs);
                }
                else {
                    to_sqs.setHavingStatement(this.createHavingStatement());
                }
                this.setSelectItemList(t_ss, from_sqs, to_sqs);
            }
            else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
                t_ss.setSelectQualifier("DISTINCT");
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) {
            if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("FIRST")) {
                t_ss.setSelectRowSpecifier("TOP");
            }
            else if (this.ifxSelectRowSpecifier != null && this.ifxSelectRowSpecifier.equalsIgnoreCase("FIRST")) {
                t_ss.setSelectRowSpecifier("TOP");
                this.ifxSelectRowSpecifier = null;
            }
            else {
                t_ss.setSelectRowSpecifier(this.selectRowSpecifier);
                t_ss.setOpenBraceForRowCount(this.openBraceForRowCount);
                t_ss.setClosedBraceForRowCount(this.closedBraceForRowCount);
                t_ss.setSelectRowCountVariable(this.rowcountVariable);
            }
            t_ss.setSelectRowCount(this.selectRowCount);
        }
        if (this.selectSpecialQualifier != null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("/* " + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
        }
        if ((this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) || this.selectQualifier == null) {
            final Vector v_sil = new Vector();
            for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
                if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
                }
                else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
            }
            t_ss.setSelectItemList(v_sil);
        }
        t_ss.setPercentSpecifier(this.percentSpecifier);
        return t_ss;
    }
    
    public SelectStatement toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        if (this.selectQualifier != null) {
            final GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
            final HavingStatement hs = from_sqs.getHavingStatement();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (gbs_gb != null) {
                    to_sqs.setGroupByStatement(gbs_gb.toSybaseSelect(to_sqs, from_sqs));
                    this.addGroupByItems(to_sqs, from_sqs);
                }
                else {
                    to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
                }
                if (hs != null) {
                    to_sqs.setHavingStatement(hs.toSybaseSelect(to_sqs, from_sqs));
                    this.addHavingItem(to_sqs);
                }
                else {
                    to_sqs.setHavingStatement(this.createHavingStatement());
                }
                this.setSelectItemList(t_ss, from_sqs, to_sqs);
            }
            else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
                t_ss.setSelectQualifier("DISTINCT");
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) {
            if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("FIRST")) {
                t_ss.setSelectRowSpecifier("TOP");
            }
            else if (this.ifxSelectRowSpecifier != null && this.ifxSelectRowSpecifier.equalsIgnoreCase("FIRST")) {
                t_ss.setSelectRowSpecifier("TOP");
                this.ifxSelectRowSpecifier = null;
            }
            else if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("TOP")) {
                t_ss.setSelectRowSpecifier(null);
                to_sqs.setSybaseTopRowCount(this.selectRowCount);
            }
            else {
                t_ss.setSelectRowSpecifier(this.selectRowSpecifier);
            }
            t_ss.setSelectRowCount(this.selectRowCount);
        }
        if (this.selectSpecialQualifier != null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("/* " + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
        }
        for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                this.selectItemList.elementAt(i_count).setObjectContext(this.context);
            }
        }
        if ((this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) || this.selectQualifier == null) {
            final Vector v_sil = new Vector();
            final Vector aliasNames = new Vector();
            int aliasCount = 1;
            for (int i_count2 = 0; i_count2 < this.selectItemList.size(); ++i_count2) {
                if (this.selectItemList.elementAt(i_count2) instanceof SelectColumn) {
                    final SelectColumn sc = this.selectItemList.elementAt(i_count2);
                    sc.setObjectContext(this.context);
                    if (sc.getAliasName() != null) {
                        if (aliasNames != null && aliasNames.contains(sc.getAliasName())) {
                            sc.setAliasName(sc.getAliasName() + aliasCount);
                            ++aliasCount;
                        }
                        else {
                            aliasNames.add(sc.getAliasName());
                        }
                    }
                    v_sil.addElement(this.selectItemList.elementAt(i_count2).toSybaseSelect(to_sqs, from_sqs));
                }
                else if (this.selectItemList.elementAt(i_count2) instanceof WhereColumn) {
                    this.selectItemList.elementAt(i_count2).setObjectContext(this.context);
                    v_sil.addElement(this.selectItemList.elementAt(i_count2));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count2));
                }
            }
            t_ss.setSelectItemList(v_sil);
        }
        t_ss.setPercentSpecifier(this.percentSpecifier);
        t_ss.setObjectContext(this.context);
        return t_ss;
    }
    
    public void setSelectItemList(final SelectStatement ss, final SelectQueryStatement from_sqs, final SelectQueryStatement to_sqs) throws ConvertException {
        final SelectStatement from_ss = from_sqs.getSelectStatement();
        final Vector v_sil = from_ss.getSelectItemList();
        final Vector v_new_sil = new Vector();
        for (int i_count = 0; i_count < v_sil.size(); ++i_count) {
            final SelectColumn sc = new SelectColumn();
            final Vector v_fi = new Vector();
            final TableColumn tc = new TableColumn();
            final FunctionCalls fc = new FunctionCalls();
            final Vector vec_af = new Vector();
            tc.setColumnName("MAX");
            fc.setFunctionName(tc);
            if (v_sil.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc_vi = v_sil.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs);
                sc_vi.setEndsWith(null);
                sc_vi.setAliasName(null);
                sc_vi.setIsAS(null);
                v_fi.addElement(sc_vi);
                if (sc_vi.getColumnExpression().elementAt(0) instanceof TableColumn) {
                    sc.setAliasName(sc_vi.getColumnExpression().elementAt(0).getColumnName());
                }
            }
            else {
                v_fi.addElement(v_sil.elementAt(i_count));
            }
            fc.setFunctionArguments(v_fi);
            vec_af.addElement(fc);
            sc.setColumnExpression(vec_af);
            if (i_count != v_sil.size() - 1) {
                sc.setEndsWith(",");
            }
            v_new_sil.addElement(sc);
        }
        ss.setSelectItemList(v_new_sil);
    }
    
    public GroupByStatement createGroupByStatement(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final GroupByStatement gbs = new GroupByStatement();
        final Vector v_gbil = new Vector();
        gbs.setGroupClause("GROUP BY");
        for (int i_count = 0; i_count < this.distinctList.size(); ++i_count) {
            v_gbil.addElement(this.distinctList.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
        }
        gbs.setGroupByItemList(v_gbil);
        return gbs;
    }
    
    public void addGroupByItems(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final GroupByStatement gbs = to_sqs.getGroupByStatement();
        final Vector v_gbil = gbs.getGroupByItemList();
        final Vector v_new_gbil = new Vector();
        for (int i_count = 0; i_count < v_gbil.size(); ++i_count) {
            v_new_gbil.addElement(v_gbil.elementAt(i_count));
        }
        for (int i_count = 0; i_count < this.distinctList.size(); ++i_count) {
            v_new_gbil.addElement(this.distinctList.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
        }
        gbs.setGroupByItemList(v_new_gbil);
    }
    
    public SelectStatement toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        t_ss.setHintClause(this.hintClause);
        t_ss.setCommentClass(this.commentObj);
        if (this.selectQualifier != null) {
            final WhereExpression f_we = from_sqs.getWhereExpression();
            final WhereExpression t_we = new WhereExpression();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (f_we != null) {
                    to_sqs.setWhereExpression(f_we.toOracleSelect(to_sqs, from_sqs));
                    to_sqs.getWhereExpression().addOperator("AND");
                    to_sqs.getWhereExpression().addWhereExpression(this.createQuery(from_sqs.getFromClause(), to_sqs, from_sqs));
                }
                else {
                    to_sqs.setWhereExpression(this.createQuery(from_sqs.getFromClause(), to_sqs, from_sqs));
                }
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if ((this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) && ((this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("TOP")) || (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("FIRST")) || (this.ifxSelectRowSpecifier != null && this.ifxSelectRowSpecifier.equalsIgnoreCase("FIRST")))) {
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
            if (this.percentSpecifier != null) {
                String s_sqs;
                if (this.rowcountVariable != null && this.selectRowCount == 0) {
                    s_sqs = "select count(*)*(" + this.rowcountVariable + "/100) + 1 " + from_sqs.getFromClause().toString();
                }
                else {
                    s_sqs = "select count(*)*(" + this.selectRowCount + "/100) + 1 " + from_sqs.getFromClause().toString();
                }
                final SwisSQLAPI swissqlapi = new SwisSQLAPI(s_sqs);
                try {
                    s_sqs = swissqlapi.convert(1);
                    s_sqs = "(" + s_sqs + ")";
                }
                catch (final ParseException pe) {
                    throw new ConvertException(" Could not parse the converted from clause " + s_sqs);
                }
                catch (final ConvertException ce) {
                    throw ce;
                }
                v_temp.addElement(s_sqs);
            }
            else if (this.rowcountVariable != null) {
                v_temp.addElement(this.rowcountVariable + " + 1");
            }
            else {
                v_temp.addElement(Integer.toString(this.selectRowCount + 1));
            }
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
        if (this.selectSpecialQualifier != null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("/* +" + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
        }
        final Vector v_sil = new Vector();
        for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn originalsc = this.selectItemList.elementAt(i_count);
                final SelectColumn sc;
                final SelectColumn oracleSelectColumn = sc = this.selectItemList.elementAt(i_count).toOracleSelect(to_sqs, from_sqs);
                final Vector v_ce = sc.getColumnExpression();
                for (int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                    if (v_ce.elementAt(ii_count) instanceof String) {
                        String s_ce = v_ce.elementAt(ii_count);
                        if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                            final FromClause fc = from_sqs.getFromClause();
                            final Vector v_fil = fc.getFromItemList();
                            if (v_fil.size() > 1) {
                                for (int countNum = 0; countNum < v_fil.size(); ++countNum) {
                                    if (v_fil.elementAt(countNum) instanceof FromTable) {
                                        final FromTable ft = v_fil.elementAt(countNum);
                                        if (ft.getAliasName() == null) {
                                            final Object o_tn = ft.getTableName();
                                            if (!(o_tn instanceof String)) {
                                                throw new ConvertException();
                                            }
                                            String tableName = (String)o_tn;
                                            if (tableName.toLowerCase().startsWith("dbo.")) {
                                                tableName = tableName.substring(4);
                                            }
                                            else if (tableName.toLowerCase().startsWith("[dbo].")) {
                                                tableName = tableName.substring(6);
                                            }
                                            s_ce = tableName + ".*";
                                        }
                                        else {
                                            s_ce = ft.getAliasName() + ".*";
                                        }
                                    }
                                    else if (v_fil.elementAt(countNum) instanceof FromClause) {
                                        final Vector newFromItemList = v_fil.get(countNum).getFromItemList();
                                        this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFromItemList, ii_count);
                                    }
                                    if (countNum == 0) {
                                        v_ce.setElementAt(s_ce, ii_count);
                                    }
                                    else {
                                        v_ce.add(",");
                                        v_ce.add(s_ce);
                                    }
                                }
                            }
                            else if (v_fil.elementAt(0) instanceof FromTable) {
                                final FromTable ft2 = v_fil.elementAt(0);
                                if (ft2.getAliasName() == null) {
                                    final Object o_tn2 = ft2.getTableName();
                                    if (!(o_tn2 instanceof String)) {
                                        throw new ConvertException();
                                    }
                                    String tableName2 = (String)o_tn2;
                                    if (tableName2.toLowerCase().startsWith("dbo.")) {
                                        tableName2 = tableName2.substring(4);
                                    }
                                    else if (tableName2.toLowerCase().startsWith("[dbo].")) {
                                        tableName2 = tableName2.substring(6);
                                    }
                                    s_ce = tableName2 + ".*";
                                }
                                else {
                                    s_ce = ft2.getAliasName() + ".*";
                                }
                                v_ce.setElementAt(s_ce, ii_count);
                            }
                            else if (v_fil.elementAt(0) instanceof FromClause) {
                                final Vector newFCVector = v_fil.get(0).getFromItemList();
                                this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector, ii_count);
                            }
                        }
                    }
                }
                sc.setColumnExpression(v_ce);
                v_sil.addElement(sc);
            }
            else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                v_sil.addElement(this.selectItemList.elementAt(i_count));
            }
            else if (this.selectItemList.elementAt(i_count) instanceof SelectQueryStatement) {
                final SelectQueryStatement ss = this.selectItemList.elementAt(i_count);
                if (ss.getWhereExpression() == null) {
                    final WhereExpression we2 = new WhereExpression();
                    ss.setWhereExpression(we2);
                }
                else {
                    ss.getWhereExpression().addOperator("AND");
                }
                final WhereColumn col1 = new WhereColumn();
                final Vector col1List = new Vector();
                col1List.add("ROWNUM");
                col1.setColumnExpression(col1List);
                final WhereColumn col2 = new WhereColumn();
                final Vector col2List = new Vector();
                col2List.add("1");
                col2.setColumnExpression(col2List);
                final WhereItem it = new WhereItem();
                it.setLeftWhereExp(col1);
                it.setRightWhereExp(col2);
                it.setOperator("=");
                ss.getWhereExpression().addWhereItem(it);
                v_sil.addElement(ss);
            }
            else {
                v_sil.addElement(this.selectItemList.elementAt(i_count));
            }
        }
        t_ss.setSelectItemList(v_sil);
        t_ss.setObjectContext(this.context);
        return t_ss;
    }
    
    private void changeTheSelectColumnWithStarToTableNameStar(final Vector v_ce, final Vector v_fil, final int num) throws ConvertException {
        for (int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
            if (v_ce.elementAt(ii_count) instanceof String) {
                String s_ce = v_ce.elementAt(ii_count);
                if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                    if (v_fil.size() > 1) {
                        for (int countNum = 0; countNum < v_fil.size(); ++countNum) {
                            if (v_fil.elementAt(countNum) instanceof FromTable) {
                                final FromTable ft = v_fil.elementAt(countNum);
                                if (ft.getAliasName() == null) {
                                    final Object o_tn = ft.getTableName();
                                    if (!(o_tn instanceof String)) {
                                        throw new ConvertException();
                                    }
                                    s_ce = (String)o_tn + ".*";
                                }
                                else {
                                    s_ce = ft.getAliasName() + ".*";
                                }
                            }
                            else if (v_fil.elementAt(countNum) instanceof FromClause) {
                                final Vector newFCVector = v_fil.get(countNum).getFromItemList();
                                this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector, num);
                            }
                            if (countNum == 0) {
                                v_ce.setElementAt(s_ce, num);
                            }
                            else {
                                if (countNum > 0) {
                                    v_ce.add(",");
                                }
                                v_ce.add(s_ce);
                            }
                        }
                    }
                    else if (v_fil.elementAt(0) instanceof FromTable) {
                        final FromTable ft2 = v_fil.elementAt(0);
                        if (ft2.getAliasName() == null) {
                            final Object o_tn2 = ft2.getTableName();
                            if (!(o_tn2 instanceof String)) {
                                throw new ConvertException();
                            }
                            s_ce = (String)o_tn2 + ".*";
                        }
                        else {
                            s_ce = ft2.getAliasName() + ".*";
                        }
                        v_ce.setElementAt(s_ce, ii_count);
                    }
                    else if (v_fil.elementAt(0) instanceof FromClause) {
                        final Vector newFCVector2 = v_fil.get(0).getFromItemList();
                        this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector2, ii_count);
                    }
                }
            }
        }
    }
    
    public SelectStatement toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setCommentClass(this.commentObj);
        t_ss.setSelectClause(this.selectClause);
        if (this.selectQualifier != null) {
            final GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
            final HavingStatement hs = from_sqs.getHavingStatement();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (gbs_gb != null) {
                    to_sqs.setGroupByStatement(gbs_gb.toInformixSelect(to_sqs, from_sqs));
                    this.addGroupByItems(to_sqs, from_sqs);
                }
                else {
                    to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
                }
                if (hs != null) {
                    to_sqs.setHavingStatement(hs.toMySQLSelect(to_sqs, from_sqs));
                    this.addHavingItem(to_sqs);
                }
                else {
                    to_sqs.setHavingStatement(this.createHavingStatement());
                }
                this.setSelectItemList(t_ss, from_sqs, to_sqs);
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null) {
            if (this.selectRowSpecifier.equalsIgnoreCase("TOP")) {
                t_ss.setInformixRowSpecifier("FIRST");
                t_ss.setSelectRowSpecifier(null);
            }
            else {
                t_ss.setSelectRowSpecifier(this.selectRowSpecifier);
            }
            t_ss.setSelectRowCount(this.selectRowCount);
        }
        if (this.selectSpecialQualifier != null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("/* " + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
        }
        if ((this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) || this.selectQualifier == null) {
            final Vector v_sil = new Vector();
            for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
                if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
                }
                else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
            }
            t_ss.setSelectItemList(v_sil);
        }
        if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("FIRST")) {
            t_ss.setSelectRowSpecifier(null);
            t_ss.setInformixRowSpecifier("FIRST");
        }
        return t_ss;
    }
    
    public SelectStatement toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        t_ss.setOpenBraceForSelectInInsertQuery(this.openBraceForSelectInInsert);
        if (this.selectQualifier != null) {
            final WhereExpression f_we = from_sqs.getWhereExpression();
            final WhereExpression t_we = new WhereExpression();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (f_we != null) {
                    to_sqs.setWhereExpression(f_we.toTimesTenSelect(to_sqs, from_sqs));
                    to_sqs.getWhereExpression().addOperator("AND");
                    to_sqs.getWhereExpression().addWhereExpression(this.createQuery(from_sqs.getFromClause(), to_sqs, from_sqs));
                }
                else {
                    to_sqs.setWhereExpression(this.createQuery(from_sqs.getFromClause(), to_sqs, from_sqs));
                }
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) {
            if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("TOP")) {
                t_ss.setSelectRowSpecifier("FIRST");
            }
            else {
                t_ss.setSelectRowSpecifier(this.selectRowSpecifier);
            }
            t_ss.setSelectRowCount(this.selectRowCount);
        }
        if (this.selectSpecialQualifier != null) {}
        final Vector v_sil = new Vector();
        for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn originalsc = this.selectItemList.elementAt(i_count);
                final SelectColumn sc;
                final SelectColumn timesTenSelectColumn = sc = this.selectItemList.elementAt(i_count).toTimesTenSelect(to_sqs, from_sqs);
                final Vector v_ce = sc.getColumnExpression();
                for (int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                    if (v_ce.elementAt(ii_count) instanceof String) {
                        String s_ce = v_ce.elementAt(ii_count);
                        if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                            final FromClause fc = from_sqs.getFromClause();
                            final Vector v_fil = fc.getFromItemList();
                            if (v_fil.size() > 1) {
                                for (int countNum = 0; countNum < v_fil.size(); ++countNum) {
                                    if (v_fil.elementAt(countNum) instanceof FromTable) {
                                        final FromTable ft = v_fil.elementAt(countNum);
                                        if (ft.getAliasName() == null) {
                                            final Object o_tn = ft.getTableName();
                                            if (!(o_tn instanceof String)) {
                                                throw new ConvertException();
                                            }
                                            String tableName = (String)o_tn;
                                            if (tableName.toLowerCase().startsWith("dbo.")) {
                                                tableName = tableName.substring(4);
                                            }
                                            else if (tableName.toLowerCase().startsWith("[dbo].")) {
                                                tableName = tableName.substring(6);
                                            }
                                            s_ce = tableName + ".*";
                                        }
                                        else {
                                            s_ce = ft.getAliasName() + ".*";
                                        }
                                    }
                                    else if (v_fil.elementAt(countNum) instanceof FromClause) {
                                        final Vector newFromItemList = v_fil.get(countNum).getFromItemList();
                                        this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFromItemList, ii_count);
                                    }
                                    if (countNum == 0) {
                                        v_ce.setElementAt(s_ce, ii_count);
                                    }
                                    else {
                                        v_ce.add(",");
                                        v_ce.add(s_ce);
                                    }
                                }
                            }
                            else if (v_fil.elementAt(0) instanceof FromTable) {
                                final FromTable ft2 = v_fil.elementAt(0);
                                if (ft2.getAliasName() == null) {
                                    final Object o_tn2 = ft2.getTableName();
                                    if (!(o_tn2 instanceof String)) {
                                        throw new ConvertException();
                                    }
                                    String tableName2 = (String)o_tn2;
                                    if (tableName2.toLowerCase().startsWith("dbo.")) {
                                        tableName2 = tableName2.substring(4);
                                    }
                                    else if (tableName2.toLowerCase().startsWith("[dbo].")) {
                                        tableName2 = tableName2.substring(6);
                                    }
                                    s_ce = tableName2 + ".*";
                                }
                                else {
                                    s_ce = ft2.getAliasName() + ".*";
                                }
                                v_ce.setElementAt(s_ce, ii_count);
                            }
                            else if (v_fil.elementAt(0) instanceof FromClause) {
                                final Vector newFCVector = v_fil.get(0).getFromItemList();
                                this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector, ii_count);
                            }
                        }
                    }
                }
                sc.setColumnExpression(v_ce);
                v_sil.addElement(sc);
            }
            else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                v_sil.addElement(this.selectItemList.elementAt(i_count));
            }
            else if (this.selectItemList.elementAt(i_count) instanceof SelectQueryStatement) {
                final SelectQueryStatement selectQueryStatement = this.selectItemList.elementAt(i_count);
            }
            else {
                v_sil.addElement(this.selectItemList.elementAt(i_count));
            }
        }
        t_ss.setSelectItemList(v_sil);
        t_ss.setObjectContext(this.context);
        return t_ss;
    }
    
    public SelectStatement toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        t_ss.setHintClause(this.hintClause);
        if (this.selectQualifier != null) {
            final GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
            final HavingStatement hs = from_sqs.getHavingStatement();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (gbs_gb != null) {
                    to_sqs.setGroupByStatement(gbs_gb.toNetezzaSelect(to_sqs, from_sqs));
                    this.addGroupByItems(to_sqs, from_sqs);
                }
                else {
                    to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
                }
                if (hs != null) {
                    to_sqs.setHavingStatement(hs.toNetezzaSelect(to_sqs, from_sqs));
                    this.addHavingItem(to_sqs);
                }
                else {
                    to_sqs.setHavingStatement(this.createHavingStatement());
                }
                this.setSelectItemList(t_ss, from_sqs, to_sqs);
            }
            else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
                t_ss.setSelectQualifier("DISTINCT");
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null) {
            if (this.percentSpecifier != null) {
                throw new ConvertException();
            }
            final LimitClause lc = new LimitClause();
            if (from_sqs.getLimitClause() != null) {
                throw new ConvertException();
            }
            lc.setLimitClause("LIMIT");
            lc.setLimitValue("" + this.selectRowCount);
            to_sqs.setLimitClause(lc);
        }
        if (this.selectSpecialQualifier != null) {
            final StringBuffer sb = new StringBuffer();
            sb.append("/* " + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
        }
        if ((this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) || this.selectQualifier == null) {
            final Vector v_sil = new Vector();
            for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
                if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
                }
                else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
            }
            t_ss.setSelectItemList(v_sil);
        }
        return t_ss;
    }
    
    WhereExpression createQuery(final FromClause t_fc, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereItem wi = new WhereItem();
        final WhereColumn wc = new WhereColumn();
        Vector v_sc = new Vector();
        final SelectQueryStatement sqs_i = new SelectQueryStatement();
        final SelectStatement ss_i = new SelectStatement();
        final FunctionCalls fc = new FunctionCalls();
        final FromClause fcl = new FromClause();
        final GroupByStatement gbs = new GroupByStatement();
        final TableColumn tc = new TableColumn();
        final WhereExpression we = new WhereExpression();
        v_sc.addElement("ROWID");
        wc.setColumnExpression(v_sc);
        wi.setLeftWhereExp(wc);
        wi.setOperator("IN");
        tc.setColumnName("MIN");
        fc.setFunctionName(tc);
        SelectColumn sc = new SelectColumn();
        v_sc = new Vector();
        v_sc.addElement("ROWID");
        sc.setColumnExpression(v_sc);
        v_sc = new Vector();
        v_sc.addElement(sc);
        fc.setFunctionArguments(v_sc);
        sc = new SelectColumn();
        v_sc = new Vector();
        v_sc.addElement(fc);
        sc.setColumnExpression(v_sc);
        v_sc = new Vector();
        v_sc.addElement(sc);
        ss_i.setSelectClause("select");
        ss_i.setSelectItemList(v_sc);
        fcl.setFromClause(t_fc.getFromClause());
        final Vector v_fil = t_fc.getFromItemList();
        final Vector v_nfil = new Vector();
        for (int i_count = 0; i_count < v_fil.size(); ++i_count) {
            final FromTable fc_n = v_fil.elementAt(i_count);
            fc_n.setIsAS(false);
            v_nfil.addElement(fc_n);
        }
        fcl.setFromItemList(v_nfil);
        gbs.setGroupClause("group by");
        gbs.setGroupByItemList(this.distinctList);
        sqs_i.setSelectStatement(ss_i);
        sqs_i.setFromClause(fcl);
        sqs_i.setGroupByStatement(gbs);
        wi.setRightWhereSubQuery(sqs_i.toOracleSelect());
        we.addWhereItem(wi);
        return we;
    }
    
    public static String changeBackTip(final String obj) {
        if (obj == null) {
            return obj;
        }
        final String obj2 = obj.replace('`', '\"');
        return obj2;
    }
    
    public static String checkandRemoveDoubleQuoteForPostgresIdentifier(String obj) {
        if (obj != null) {
            obj = obj.toLowerCase();
        }
        return obj;
    }
    
    public static String checkandRemoveDoubleQuoteForPostgresIdentifier(String obj, final boolean noNeedToLower) {
        if (obj != null) {
            if (noNeedToLower) {
                if (obj.startsWith("\"T_") || obj.startsWith("\"C_")) {
                    obj = obj.toLowerCase();
                }
            }
            else {
                obj = obj.toLowerCase();
            }
        }
        return obj;
    }
    
    public SelectStatement toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement t_ss = new SelectStatement();
        t_ss.setSelectClause(this.selectClause);
        if (this.selectQualifier != null) {
            final GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
            final HavingStatement hs = from_sqs.getHavingStatement();
            if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
                if (gbs_gb != null) {
                    to_sqs.setGroupByStatement(gbs_gb.toVectorWiseSelect(to_sqs, from_sqs));
                    this.addGroupByItems(to_sqs, from_sqs);
                }
                else {
                    to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
                }
                if (hs != null) {
                    to_sqs.setHavingStatement(hs.toVectorWiseSelect(to_sqs, from_sqs));
                    this.addHavingItem(to_sqs);
                }
                else {
                    to_sqs.setHavingStatement(this.createHavingStatement());
                }
                this.setSelectItemList(t_ss, from_sqs, to_sqs);
            }
            else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
                t_ss.setSelectQualifier("DISTINCT");
            }
            else {
                t_ss.setSelectQualifier(this.selectQualifier);
            }
        }
        if (this.selectRowSpecifier != null) {
            if (this.percentSpecifier != null) {
                throw new ConvertException();
            }
            final LimitClause lc = new LimitClause();
            if (from_sqs.getLimitClause() != null) {
                throw new ConvertException();
            }
            lc.setLimitClause("LIMIT");
            lc.setLimitValue("" + this.selectRowCount);
            to_sqs.setLimitClause(lc);
        }
        if (this.selectSpecialQualifier != null) {
            t_ss.setSelectSpecialQualifier(this.selectSpecialQualifier);
        }
        if ((this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) || this.selectQualifier == null) {
            final Vector v_sil = new Vector();
            for (int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
                if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.selectItemList.elementAt(i_count);
                    sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
                    if (from_sqs != null) {
                        if (from_sqs.isSelectWithoutSetClause()) {
                            sc.convertSelectColumnToNumericDataType("SIGNED");
                        }
                        else {
                            sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                        }
                    }
                    final Vector v_ce = sc.getColumnExpression();
                    for (int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                        if (v_ce.elementAt(ii_count) instanceof String) {
                            String s_ce = v_ce.elementAt(ii_count);
                            if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                                final FromClause fc = from_sqs.getFromClause();
                                final Vector v_fil = fc.getFromItemList();
                                if (v_fil.size() <= 1) {
                                    final FromTable ft = v_fil.elementAt(0);
                                    if (ft.getAliasName() == null) {
                                        final Object o_tn = ft.getTableName();
                                        if (!(o_tn instanceof String)) {
                                            throw new ConvertException();
                                        }
                                    }
                                    else {
                                        s_ce = ft.getAliasName() + ".*";
                                    }
                                    v_ce.setElementAt(changeBackTip(s_ce), ii_count);
                                }
                            }
                        }
                        else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                            final TableColumn tc = v_ce.elementAt(ii_count);
                            tc.setColumnName(changeBackTip(tc.getColumnName()));
                        }
                    }
                    sc.setColumnExpression(v_ce);
                    v_sil.addElement(this.selectItemList.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    v_sil.addElement(this.selectItemList.elementAt(i_count));
                }
            }
            t_ss.setSelectItemList(v_sil);
        }
        return t_ss;
    }
    
    public Set getIndexPositionsSetForStringLiterals(final SelectQueryStatement from_sqs) {
        if (from_sqs != null) {
            return from_sqs.getIndexPositionsForStringLiterals();
        }
        return null;
    }
    
    public Set getIndexPositionsSetForNULLString(final SelectQueryStatement from_sqs) {
        if (from_sqs != null) {
            return from_sqs.getIndexPositionsForNULLString();
        }
        return null;
    }
    
    public boolean hasStarInSelectColumn(final SelectColumn sc) {
        final Vector vc = sc.getColumnExpression();
        return (vc.size() == 1 && vc.get(0).toString().trim().equals("*")) || (vc.size() == 2 && vc.get(1).toString().trim().equals(".*"));
    }
}
