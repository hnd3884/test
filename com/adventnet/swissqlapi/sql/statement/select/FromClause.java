package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.update.SetClause;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import java.util.Hashtable;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.OpenXML;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class FromClause
{
    public static boolean doNotAddDotInSubquery;
    public String fromClause;
    public Vector fromItemList;
    private String openBraces;
    private String closedBraces;
    private FetchClause fetchClauseFromSQS;
    private UserObjectContext context;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    private PivotClause pivot_clause;
    private String aliasName;
    private String sqlServerapplyType;
    private OpenXML oxml;
    private boolean baseFromClauseFound;
    private String updateColumnName;
    
    public FromClause() {
        this.context = null;
        this.oxml = null;
        this.baseFromClauseFound = false;
        this.updateColumnName = null;
        this.fromClause = new String();
        this.fromItemList = new Vector();
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setFromClause(final String s_fc) {
        this.fromClause = s_fc;
    }
    
    public void setOpenXML(final OpenXML oxml) {
        this.oxml = oxml;
    }
    
    public OpenXML getOpenXML() {
        return this.oxml;
    }
    
    public void setFromItemList(final Vector v_fil) {
        this.fromItemList = v_fil;
        final SelectInvolvedTables tl = SwisSQLAPI.involvedTablesTL.get();
        if (tl.isNeeded) {
            for (int i = 0; i < this.fromItemList.size(); ++i) {
                final Object obj = this.fromItemList.elementAt(i).tableName;
                if (!(obj instanceof SelectQueryStatement)) {
                    final String tableName = obj.toString();
                    tl.involvedTables.add(tableName);
                }
            }
        }
    }
    
    public void setOpenBraces(final String openBraces) {
        this.openBraces = openBraces;
    }
    
    public void setClosedBraces(final String closedBraces) {
        this.closedBraces = closedBraces;
    }
    
    public void setFetchClauseFromSQS(final FetchClause fetchClauseFromSQS) {
        this.fetchClauseFromSQS = fetchClauseFromSQS;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass commentObj) {
        this.commentObjAfterToken = commentObj;
    }
    
    public void setAliasName(final String alias) {
        this.aliasName = alias;
    }
    
    public void setPivotClause(final PivotClause pc) {
        this.pivot_clause = pc;
    }
    
    public String getFromClause() {
        return this.fromClause;
    }
    
    public Vector getFromItemList() {
        return this.fromItemList;
    }
    
    public FetchClause getFetchClauseFromSQS() {
        return this.fetchClauseFromSQS;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public PivotClause getPivotClause() {
        return this.pivot_clause;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
            sb.append("\t");
        }
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim());
            sb.append("\n");
            for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                sb.append("\t");
            }
        }
        if (this.fromClause != null) {
            sb.append(this.fromClause.toUpperCase() + " ");
        }
        ++SelectQueryStatement.beautyTabCount;
        if (this.openBraces != null) {
            sb.append("(");
        }
        if (this.oxml != null) {
            sb.append("(");
            sb.append(this.oxml.toString());
            sb.append(")");
        }
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            try {
                if (i == this.fromItemList.size() - 1 || (this.fromItemList.elementAt(i + 1) instanceof FromTable && (this.fromItemList.elementAt(i + 1).getJoinClause() != null || this.fromItemList.elementAt(i + 1).getJoinClause() != null))) {
                    if (this.fromItemList.elementAt(i + 1).getJoinClause().equalsIgnoreCase("OUTER")) {
                        if (this.fromItemList.elementAt(i) instanceof FromClause) {
                            if (this.openBraces != null) {
                                sb.append("(");
                            }
                            final FromClause newFC = this.fromItemList.elementAt(i);
                            newFC.setObjectContext(this.context);
                            sb.append(newFC.toString() + ",");
                            if (this.closedBraces != null) {
                                sb.append(")");
                            }
                        }
                        else if (this.fromItemList.elementAt(i) instanceof FromTable) {
                            this.fromItemList.elementAt(i).setObjectContext(this.context);
                            sb.append(this.fromItemList.elementAt(i).toString() + ",");
                        }
                        else {
                            sb.append(this.fromItemList.elementAt(i).toString() + ",");
                        }
                        sb.append("\n");
                        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                            sb.append("\t");
                        }
                    }
                    else if (this.fromItemList.elementAt(i) instanceof FromClause) {
                        if (this.openBraces != null) {
                            sb.append("(");
                        }
                        final FromClause newFC = this.fromItemList.elementAt(i);
                        newFC.setObjectContext(this.context);
                        sb.append(newFC.toString());
                        if (this.closedBraces != null) {
                            sb.append(")");
                        }
                    }
                    else if (this.fromItemList.elementAt(i) instanceof FromTable) {
                        this.fromItemList.elementAt(i).setObjectContext(this.context);
                        sb.append(this.fromItemList.elementAt(i).toString());
                    }
                    else if (this.fromItemList.elementAt(i) != null) {
                        sb.append(this.fromItemList.elementAt(i).toString());
                    }
                }
                else {
                    if (this.fromItemList.elementAt(i) instanceof FromClause) {
                        if (this.openBraces != null) {
                            sb.append("(");
                        }
                        final FromClause newFC = this.fromItemList.elementAt(i);
                        newFC.setObjectContext(this.context);
                        sb.append(newFC.toString() + ",");
                        if (this.closedBraces != null) {
                            sb.append(")");
                        }
                    }
                    else if (this.fromItemList.elementAt(i) instanceof FromTable) {
                        this.fromItemList.elementAt(i).setObjectContext(this.context);
                        sb.append(this.fromItemList.elementAt(i).toString() + ",");
                    }
                    else if (this.fromItemList.elementAt(i) != null) {
                        sb.append(this.fromItemList.elementAt(i).toString() + ",");
                    }
                    sb.append("\n");
                    for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                        sb.append("\t");
                    }
                }
            }
            catch (final ArrayIndexOutOfBoundsException e) {
                if (this.fromItemList.elementAt(i) instanceof FromClause) {
                    if (this.openBraces != null) {
                        sb.append("(");
                    }
                    final FromClause newFC2 = this.fromItemList.elementAt(i);
                    newFC2.setObjectContext(this.context);
                    sb.append(newFC2.toString());
                    if (this.closedBraces != null) {
                        sb.append(")");
                    }
                }
                else if (this.fromItemList.elementAt(i) instanceof FromTable) {
                    this.fromItemList.elementAt(i).setObjectContext(this.context);
                    sb.append(this.fromItemList.elementAt(i).toString());
                }
                else {
                    sb.append(this.fromItemList.elementAt(i).toString());
                }
            }
        }
        if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString().trim());
        }
        if (this.closedBraces != null) {
            sb.append(")");
        }
        if (this.fetchClauseFromSQS != null) {
            sb.append(" " + this.fetchClauseFromSQS.toString());
        }
        if (this.pivot_clause != null) {
            sb.append(this.pivot_clause.toString());
        }
        --SelectQueryStatement.beautyTabCount;
        return sb.toString();
    }
    
    public FromTable getLastElement() {
        if (this.fromItemList != null) {
            for (int i = 0; i < this.fromItemList.size(); ++i) {
                if (this.fromItemList.lastElement() instanceof FromTable) {
                    return this.fromItemList.lastElement();
                }
                if (this.fromItemList.lastElement() instanceof FromClause) {
                    final FromClause fc = this.fromItemList.lastElement();
                    fc.getLastElement();
                }
            }
        }
        return null;
    }
    
    public FromTable getFirstElement() {
        if (this.fromItemList != null) {
            if (this.fromItemList.firstElement() instanceof FromTable) {
                return this.fromItemList.firstElement();
            }
            if (this.fromItemList.firstElement() instanceof FromClause) {
                final FromClause fc = this.fromItemList.firstElement();
                fc.getFirstElement();
            }
        }
        return null;
    }
    
    public FromTable getFromTablefromTheVector() {
        if (this.fromItemList != null) {
            for (int i = 0; i < this.fromItemList.size(); ++i) {
                if (this.fromItemList.elementAt(i) instanceof FromTable) {
                    return this.fromItemList.elementAt(i);
                }
                if (this.fromItemList.elementAt(i) instanceof FromClause) {
                    final FromClause fc = this.fromItemList.elementAt(i);
                    fc.getFromTablefromTheVector();
                }
            }
        }
        return null;
    }
    
    public String getAliasName() {
        return this.aliasName;
    }
    
    public Object clone() {
        final Vector newFromItemList = new Vector();
        final FromClause fc = new FromClause();
        fc.setBaseFromClauseFound(this.baseFromClauseFound);
        fc.setClosedBraces(this.closedBraces);
        fc.setCommentClass(this.commentObj);
        fc.setCommentClassAfterToken(this.commentObjAfterToken);
        fc.setFetchClauseFromSQS(this.fetchClauseFromSQS);
        fc.setFromClause(this.fromClause);
        fc.setFromItemList(this.fromItemList);
        fc.setObjectContext(this.context);
        fc.setOpenBraces(this.openBraces);
        fc.setOpenXML(this.oxml);
        final Vector fromItemList = fc.getFromItemList();
        if (fromItemList != null) {
            for (int i = 0; i < fromItemList.size(); ++i) {
                final Object obj = fromItemList.get(i);
                if (obj instanceof FromTable) {
                    final FromTable ft = (FromTable)((FromTable)obj).clone();
                    newFromItemList.add(ft);
                }
            }
            fc.setFromItemList(newFromItemList);
        }
        return fc;
    }
    
    public FromClause toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = new FromClause();
        fc.setCommentClass(this.commentObj);
        fc.setOpenBraces(this.openBraces);
        fc.setClosedBraces(this.closedBraces);
        fc.setFromClause(this.fromClause);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
                v_fil.addElement(this.fromItemList.elementAt(i).toANSISelect(to_sqs, from_sqs));
            }
            else {
                v_fil.addElement(this.fromItemList.elementAt(i).toANSISelect(to_sqs, from_sqs));
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.firstElement() instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
                fc = null;
            }
        }
        if (this.pivot_clause != null && this.getFromItemList().size() == 1 && this.getFromItemList().get(0) instanceof FromTable) {
            final FromTable ft = this.getFromItemList().get(0);
            if (ft.getTableName() instanceof SelectQueryStatement) {
                this.pivot_clause.setSubQuery((SelectQueryStatement)ft.getTableName());
                this.pivot_clause.toANSISelect(to_sqs, from_sqs);
                final SelectQueryStatement sqs = (SelectQueryStatement)ft.getTableName();
                return sqs.getFromClause();
            }
        }
        return fc;
    }
    
    public FromClause toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = new FromClause();
        fc.setCommentClass(this.commentObj);
        fc.setOpenBraces(this.openBraces);
        fc.setClosedBraces(this.closedBraces);
        fc.setFromClause(this.fromClause);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) != null) {
                if (this.fromItemList.elementAt(i) instanceof FromTable) {
                    final FromTable teradataFromTable = this.fromItemList.elementAt(i).toTeradataSelect(to_sqs, from_sqs);
                    if ((this.fromClause == null || this.fromClause.length() == 0) && teradataFromTable.getFromClauseOpenBraces() == null && teradataFromTable.getAliasName() != null && teradataFromTable.getAliasName().startsWith("SwisSQL")) {
                        fc.setAliasName(teradataFromTable.getAliasName());
                        teradataFromTable.setAliasName(null);
                    }
                    v_fil.addElement(teradataFromTable);
                    if (teradataFromTable.getJoinExpression() == null && teradataFromTable.getQueryPartitionClause() != null && i != this.fromItemList.size() - 1) {
                        if (this.fromItemList.elementAt(i + 1) instanceof FromTable) {
                            this.fromItemList.elementAt(i + 1).setCrossJoinForPartitionClause(teradataFromTable.getCrossJoinForPartitionClause());
                            this.fromItemList.elementAt(i + 1).setCrossJoinExpression(teradataFromTable.getCrossJoinExpression());
                        }
                        else if (this.fromItemList.elementAt(i + 1) instanceof FromClause) {
                            this.fromItemList.elementAt(i + 1).getFromItemList().firstElement().setCrossJoinForPartitionClause(teradataFromTable.getCrossJoinForPartitionClause());
                        }
                    }
                    else if (teradataFromTable.getQueryPartitionClause() != null) {
                        v_fil.insertElementAt(teradataFromTable.getCrossJoinForPartitionClause(), v_fil.size() - 1);
                    }
                }
                else {
                    v_fil.addElement(this.fromItemList.elementAt(i).toTeradataSelect(to_sqs, from_sqs));
                }
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.firstElement() instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else {
                ft = this.getFirstElement();
            }
            if (!SwisSQLOptions.isDualTableNameRequired && ft != null && ft.getTableName() instanceof String) {
                final String tabName = (String)ft.getTableName();
                final String ignoreQuotes = tabName.replaceAll("\"", "");
                if (ignoreQuotes.equalsIgnoreCase("DUAL") || ignoreQuotes.equalsIgnoreCase("SYS.DUAL")) {
                    fc = null;
                }
            }
            if (ft != null && ft.getTableName() instanceof String && ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1")) {
                fc = null;
            }
        }
        return fc;
    }
    
    public FromClause toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FromClause fc = new FromClause();
        fc.setCommentClass(this.commentObj);
        fc.setOpenBraces(this.openBraces);
        fc.setClosedBraces(this.closedBraces);
        fc.setFromClause(this.fromClause);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
                v_fil.addElement(this.fromItemList.elementAt(i).toDB2Select(to_sqs, from_sqs));
            }
            else if (this.fromItemList.elementAt(i) instanceof FromClause) {
                v_fil.addElement(this.fromItemList.elementAt(i).toDB2Select(to_sqs, from_sqs));
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.firstElement() instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL"))) {
                ft.setTableName("SYSIBM.SYSDUMMY1");
                final FetchClause fec = new FetchClause();
                fec.setFetchFirstClause("FETCH FIRST");
                fec.setFetchCount("1");
                fec.setRowOnlyClause("ROWS ONLY");
                if (from_sqs.getFetchClause() != null) {
                    throw new ConvertException("Conversion failure..");
                }
                to_sqs.setFetchClause(fec);
            }
            else if (ft != null && ft.getTableName() instanceof String && ((String)ft.getTableName()).equalsIgnoreCase("USER_SEQUENCES")) {
                ft.setTableName("SYSIBM.SYSSEQUENCES");
            }
        }
        if (this.fetchClauseFromSQS != null) {
            fc.setFetchClauseFromSQS(this.fetchClauseFromSQS.toDB2Select(to_sqs, from_sqs));
        }
        if (this.pivot_clause != null && this.getFromItemList().size() == 1 && this.getFromItemList().get(0) instanceof FromTable) {
            final FromTable ft = this.getFromItemList().get(0);
            if (ft.getTableName() instanceof SelectQueryStatement) {
                this.pivot_clause.setSubQuery((SelectQueryStatement)ft.getTableName());
                this.pivot_clause.toDB2Select(to_sqs, from_sqs);
                final SelectQueryStatement sqs = (SelectQueryStatement)ft.getTableName();
                return sqs.getFromClause();
            }
        }
        return fc;
    }
    
    public FromClause toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = new FromClause();
        fc.setCommentClass(this.commentObj);
        fc.setCommentClassAfterToken(this.commentObjAfterToken);
        fc.setFromClause(this.fromClause);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
                v_fil.addElement(this.fromItemList.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else if (this.fromItemList.elementAt(i) instanceof FromClause) {
                v_fil.addElement(this.fromItemList.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.firstElement() instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
                fc = null;
            }
        }
        if (this.pivot_clause != null && this.getFromItemList().size() == 1 && this.getFromItemList().get(0) instanceof FromTable) {
            final FromTable ft = this.getFromItemList().get(0);
            if (ft.getTableName() instanceof SelectQueryStatement) {
                this.pivot_clause.setSubQuery((SelectQueryStatement)ft.getTableName());
                this.pivot_clause.toMySQLSelect(to_sqs, from_sqs);
                final SelectQueryStatement sqs = (SelectQueryStatement)ft.getTableName();
                return sqs.getFromClause();
            }
        }
        return fc;
    }
    
    public FromClause toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = new FromClause();
        fc.setCommentClass(this.commentObj);
        fc.setOpenBraces(this.openBraces);
        fc.setClosedBraces(this.closedBraces);
        fc.setFromClause(this.fromClause);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
                v_fil.addElement(this.fromItemList.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else if (this.fromItemList.elementAt(i) instanceof FromClause) {
                v_fil.addElement(this.fromItemList.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.firstElement() instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else if (v_fil.firstElement() instanceof FromClause) {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
                fc = null;
            }
        }
        if (this.pivot_clause != null) {
            fc.setPivotClause(this.pivot_clause);
        }
        return fc;
    }
    
    public FromClause toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = new FromClause();
        fc.setCommentClass(this.commentObj);
        fc.setOpenBraces(this.openBraces);
        fc.setClosedBraces(this.closedBraces);
        fc.setFromClause(this.fromClause);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
                v_fil.addElement(this.fromItemList.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
            }
            else if (this.fromItemList.elementAt(i) instanceof FromClause) {
                v_fil.addElement(this.fromItemList.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.elementAt(0) instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else if (v_fil.elementAt(0) instanceof FromClause) {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
                fc = null;
            }
        }
        if (this.pivot_clause != null && this.getFromItemList().size() == 1 && this.getFromItemList().get(0) instanceof FromTable) {
            final FromTable ft = this.getFromItemList().get(0);
            if (ft.getTableName() instanceof SelectQueryStatement) {
                this.pivot_clause.setSubQuery((SelectQueryStatement)ft.getTableName());
                this.pivot_clause.toSybaseSelect(to_sqs, from_sqs);
                final SelectQueryStatement sqs = (SelectQueryStatement)ft.getTableName();
                return sqs.getFromClause();
            }
        }
        return fc;
    }
    
    public void setBaseFromClauseFound(final boolean baseFromClauseFound) {
        this.baseFromClauseFound = baseFromClauseFound;
    }
    
    public FromClause toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = null;
        if (this.baseFromClauseFound) {
            fc = to_sqs.getFromClause();
        }
        else {
            fc = this.copyObjectValues();
        }
        fc.setCommentClass(this.commentObj);
        fc.setFromClause(this.fromClause);
        fc.setOpenBraces(null);
        fc.setClosedBraces(null);
        fc.setOpenXML(this.oxml);
        final Vector v_fil = new Vector();
        fc.setFromItemList(v_fil);
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromClause) {
                final FromClause fromCl = this.fromItemList.elementAt(i);
                fromCl.setObjectContext(this.context);
                fromCl.baseFromClauseFound = false;
                v_fil.addElement(this.fromItemList.elementAt(i).toOracleSelect(to_sqs, from_sqs));
            }
            else if (this.fromItemList.elementAt(i) instanceof FromTable) {
                final FromTable ft = this.fromItemList.elementAt(i);
                ft.setObjectContext(this.context);
                final Object obj = ft.getTableName();
                final String join = ft.getJoinClause();
                if (join != null && join.trim().equalsIgnoreCase("apply") && i > 0 && this.fromItemList.elementAt(i - 1) instanceof FromTable) {
                    final FromTable ftPrevious = v_fil.elementAt(i - 1);
                    final String alias = ftPrevious.getAliasName();
                    if ((alias != null && alias.trim().equalsIgnoreCase("cross")) || alias.trim().equalsIgnoreCase("outer")) {
                        ftPrevious.setAliasName(null);
                        ft.setJoinClause(this.sqlServerapplyType = alias.trim() + " " + join.trim());
                    }
                }
                if (obj instanceof String) {
                    String temp = (String)obj;
                    if (temp.indexOf(".") != -1) {
                        temp = temp.substring(temp.lastIndexOf(".") + 1);
                    }
                    ft.setOrigTableName(temp);
                }
                v_fil.addElement(ft.toOracleSelect(to_sqs, from_sqs));
            }
        }
        if (v_fil.size() == 1) {
            FromTable ft2 = null;
            if (v_fil.firstElement() instanceof FromTable) {
                ft2 = v_fil.elementAt(0);
            }
            else {
                ft2 = this.getFirstElement();
            }
            if (ft2 != null && ft2.getTableName() instanceof String && (((String)ft2.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1") || ((String)ft2.getTableName()).equalsIgnoreCase("SYSDUMMY1"))) {
                ft2.setTableName("DUAL");
            }
        }
        fc.setFromItemList(v_fil);
        if (this.pivot_clause != null && this.getFromItemList().size() == 1 && this.getFromItemList().get(0) instanceof FromTable) {
            final FromTable ft2 = this.getFromItemList().get(0);
            if (ft2.getTableName() instanceof SelectQueryStatement) {
                this.pivot_clause.setSubQuery((SelectQueryStatement)ft2.getTableName());
                this.pivot_clause.toOracleSelect(to_sqs, from_sqs);
                final SelectQueryStatement sqs = (SelectQueryStatement)ft2.getTableName();
                return sqs.getFromClause();
            }
        }
        return fc;
    }
    
    public FromClause toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final FromClause fc = new FromClause();
        fc.setCommentClass(this.commentObj);
        fc.setFromClause(this.fromClause);
        fc.setOpenBraces(null);
        fc.setClosedBraces(null);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromClause) {
                v_fil.addElement(this.fromItemList.elementAt(i).toInformixSelect(to_sqs, from_sqs));
            }
            else if (this.fromItemList.elementAt(i) instanceof FromTable) {
                v_fil.addElement(this.fromItemList.elementAt(i).toInformixSelect(to_sqs, from_sqs));
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.elementAt(0) instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else if (v_fil.elementAt(0) instanceof FromClause) {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL"))) {
                final SelectStatement ifxSelectStatement = to_sqs.getSelectStatement();
                ifxSelectStatement.setSelectRowSpecifier(null);
                ifxSelectStatement.setInformixRowSpecifier("FIRST");
                ifxSelectStatement.setSelectRowCount(1);
                ft.setTableName("SYSTABLES");
            }
        }
        if (this.pivot_clause != null && this.getFromItemList().size() == 1 && this.getFromItemList().get(0) instanceof FromTable) {
            final FromTable ft = this.getFromItemList().get(0);
            if (ft.getTableName() instanceof SelectQueryStatement) {
                this.pivot_clause.setSubQuery((SelectQueryStatement)ft.getTableName());
                this.pivot_clause.toInformixSelect(to_sqs, from_sqs);
                final SelectQueryStatement sqs = (SelectQueryStatement)ft.getTableName();
                return sqs.getFromClause();
            }
        }
        return fc;
    }
    
    public FromClause toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = new FromClause();
        fc.setCommentClass(null);
        fc.setOpenBraces(this.openBraces);
        fc.setClosedBraces(this.closedBraces);
        fc.setFromClause(this.fromClause);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
                v_fil.addElement(this.fromItemList.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else if (this.fromItemList.elementAt(i) instanceof FromClause) {
                v_fil.addElement(this.fromItemList.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs));
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.elementAt(0) instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else if (v_fil.elementAt(0) instanceof FromClause) {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL"))) {
                fc = null;
            }
        }
        if (this.pivot_clause != null && this.getFromItemList().size() == 1 && this.getFromItemList().get(0) instanceof FromTable) {
            final FromTable ft = this.getFromItemList().get(0);
            if (ft.getTableName() instanceof SelectQueryStatement) {
                this.pivot_clause.setSubQuery((SelectQueryStatement)ft.getTableName());
                this.pivot_clause.toPostgreSQLSelect(to_sqs, from_sqs);
                final SelectQueryStatement sqs = (SelectQueryStatement)ft.getTableName();
                return sqs.getFromClause();
            }
        }
        return fc;
    }
    
    public FromClause toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = null;
        if (this.baseFromClauseFound) {
            fc = to_sqs.getFromClause();
        }
        else {
            fc = this.copyObjectValues();
        }
        fc.setFromClause(this.fromClause);
        fc.setOpenBraces(null);
        fc.setClosedBraces(null);
        fc.setOpenXML(null);
        final Vector v_fil = new Vector();
        fc.setFromItemList(v_fil);
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromClause) {
                final FromClause fromCl = this.fromItemList.elementAt(i);
                fromCl.setObjectContext(this.context);
                fromCl.baseFromClauseFound = false;
                v_fil.addElement(this.fromItemList.elementAt(i).toTimesTenSelect(to_sqs, from_sqs));
            }
            else if (this.fromItemList.elementAt(i) instanceof FromTable) {
                this.fromItemList.elementAt(i).setObjectContext(this.context);
                v_fil.addElement(this.fromItemList.elementAt(i).toTimesTenSelect(to_sqs, from_sqs));
            }
        }
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.firstElement() instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1") || ((String)ft.getTableName()).equalsIgnoreCase("SYSDUMMY1"))) {
                ft.setTableName("MONITOR");
            }
        }
        fc.setFromItemList(v_fil);
        return fc;
    }
    
    public FromClause toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = new FromClause();
        fc.setCommentClass(this.commentObj);
        fc.setOpenBraces(this.openBraces);
        fc.setClosedBraces(this.closedBraces);
        fc.setFromClause(this.fromClause);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
                v_fil.addElement(this.fromItemList.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                v_fil.addElement(this.fromItemList.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.firstElement() instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
                fc = null;
            }
        }
        if (this.pivot_clause != null && this.getFromItemList().size() == 1 && this.getFromItemList().get(0) instanceof FromTable) {
            final FromTable ft = this.getFromItemList().get(0);
            if (ft.getTableName() instanceof SelectQueryStatement) {
                this.pivot_clause.setSubQuery((SelectQueryStatement)ft.getTableName());
                this.pivot_clause.toNetezzaSelect(to_sqs, from_sqs);
                final SelectQueryStatement sqs = (SelectQueryStatement)ft.getTableName();
                return sqs.getFromClause();
            }
        }
        return fc;
    }
    
    public void changeWhereItem(final WhereExpression whereExpression, final String tableName, String columnName, final String orgTableName) {
        Hashtable columnNameTable = new Hashtable();
        if (SwisSQLAPI.dataTypesFromMetaDataHT != null) {
            if (SwisSQLAPI.dataTypesFromMetaDataHT.containsKey(tableName.trim().toUpperCase())) {
                columnNameTable = SwisSQLAPI.dataTypesFromMetaDataHT.get(tableName.trim().toUpperCase());
            }
            else if (SwisSQLAPI.dataTypesFromMetaDataHT.containsKey(tableName.trim().toLowerCase())) {
                columnNameTable = SwisSQLAPI.dataTypesFromMetaDataHT.get(tableName.trim().toLowerCase());
            }
        }
        if (whereExpression != null) {
            final Vector whereItemList = whereExpression.getWhereItems();
            final String alias = tableName;
            boolean columnNameAdded = false;
            for (int i = 0, size = whereItemList.size(); i < size; ++i) {
                WhereItem whereItem = null;
                if (whereItemList.elementAt(i) instanceof WhereItem) {
                    whereItem = whereItemList.elementAt(i);
                    WhereColumn wc = whereItem.getLeftWhereExp();
                    if (wc != null) {
                        Vector colExp = wc.getColumnExpression();
                        if (colExp != null) {
                            final Object object = colExp.elementAt(0);
                            if (object instanceof TableColumn) {
                                final TableColumn tblCol = (TableColumn)object;
                                if (tableName.equalsIgnoreCase(tblCol.getTableName())) {
                                    tblCol.setTableName(alias);
                                    columnName = tblCol.getColumnName();
                                    if (!columnNameAdded) {
                                        this.updateColumnName = columnName;
                                        columnNameAdded = true;
                                    }
                                }
                                else if ((columnNameTable.containsKey(tblCol.getColumnName().trim().toLowerCase()) || columnNameTable.containsKey(tblCol.getColumnName().trim().toUpperCase())) && !columnNameAdded) {
                                    this.updateColumnName = tblCol.getColumnName();
                                    columnNameAdded = true;
                                }
                            }
                        }
                        Object obj = whereItem.getRightWhereExp();
                        if (obj instanceof WhereColumn) {
                            wc = (WhereColumn)obj;
                            colExp = wc.getColumnExpression();
                            if (colExp != null) {
                                if (colExp.size() != 0) {
                                    obj = colExp.elementAt(0);
                                    if (obj instanceof TableColumn) {
                                        final TableColumn tblCol = (TableColumn)obj;
                                        if (tableName.equalsIgnoreCase(tblCol.getTableName())) {
                                            tblCol.setTableName(alias);
                                            columnName = tblCol.getColumnName();
                                            if (!columnNameAdded) {
                                                this.updateColumnName = columnName;
                                                columnNameAdded = true;
                                            }
                                        }
                                        else if ((columnNameTable.containsKey(tblCol.getColumnName().trim().toLowerCase()) || columnNameTable.containsKey(tblCol.getColumnName().trim().toUpperCase())) && !columnNameAdded) {
                                            this.updateColumnName = tblCol.getColumnName();
                                            columnNameAdded = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (whereItemList.elementAt(i) instanceof WhereExpression) {
                    this.changeWhereItem(whereItemList.elementAt(i), tableName, columnName, orgTableName);
                }
            }
            if (this.updateColumnName == null && columnNameAdded) {
                this.updateColumnName = columnName;
            }
        }
    }
    
    public void convertToSubQuery(final UpdateQueryStatement uqs, final int database, final FromClause fc) throws ConvertException {
        final TableExpression tblExp = uqs.getTableExpression();
        final TableClause tc = tblExp.getTableClauseList().get(0);
        final TableObject tableObject = tc.getTableObject();
        String tableName = tableObject.getTableName();
        String orgTableName = null;
        String alias = tableName.toUpperCase();
        final WhereExpression whereExpression = uqs.getWhereExpression();
        if (fc != null && fc.getFromItemList() != null) {
            final Vector fromItems = fc.getFromItemList();
            for (int i = 0; i < fromItems.size(); ++i) {
                if (fromItems.get(i) instanceof FromTable) {
                    final FromTable ft = fromItems.get(i);
                    if (ft.getTableName() != null && ft.getAliasName() != null) {
                        final Object tableObjectInFrom = ft.getTableName();
                        if (tableObjectInFrom instanceof String) {
                            String tableNameInFrom = tableObject.toString();
                            if (tableNameInFrom.indexOf(46) != -1) {
                                tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                                if (tableNameInFrom.indexOf(46) != -1) {
                                    tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                                }
                            }
                            if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                                orgTableName = tableName;
                                tableName = ft.getAliasName();
                                alias = ft.getAliasName();
                            }
                        }
                    }
                    else if (ft.getTableName() != null) {
                        final Object tableObjectInFrom = ft.getTableName();
                        if (tableObjectInFrom instanceof String) {
                            String tableNameInFrom = tableObjectInFrom.toString();
                            if (tableNameInFrom.indexOf(46) != -1) {
                                tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                                if (tableNameInFrom.indexOf(46) != -1) {
                                    tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                                }
                            }
                            if (tableNameInFrom.trim().equalsIgnoreCase(tableName) && fromItems.size() == 1) {
                                uqs.setFromClause(null);
                                return;
                            }
                            if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                                if (ft != null && ft.getJoinExpression() != null) {
                                    for (int j = 0; j < ft.getJoinExpression().size(); ++j) {
                                        final WhereExpression whereExp = ft.getJoinExpression().get(j);
                                        if (whereExpression != null) {
                                            if (whereExp.getOperator() == null || (whereExp.getOperator() != null && whereExp.getOperator().size() < 1)) {
                                                whereExpression.addOperator("AND");
                                            }
                                            else {
                                                for (int k = 0; k < whereExp.getOperator().size(); ++k) {
                                                    whereExpression.addOperator(whereExp.getOperator().get(k));
                                                }
                                            }
                                            whereExpression.addWhereExpression(ft.getJoinExpression().get(j));
                                        }
                                    }
                                }
                                fromItems.remove(i);
                                alias = tableName.toUpperCase();
                            }
                        }
                    }
                }
                else if (fromItems.get(i) instanceof FromClause) {
                    final FromClause newFC = fromItems.get(i);
                    final Vector newFromItems = newFC.getFromItemList();
                    this.processTheFromTableInsideTheFromItemList(fromItems, newFromItems, tableObject, tableName, orgTableName, alias, uqs, whereExpression);
                }
            }
        }
        final String columnName = new String();
        this.changeWhereItem(whereExpression, tableName, columnName, orgTableName);
        if (this.updateColumnName == null) {
            final SetClause setClause = uqs.getSetClause();
            ArrayList expList = setClause.getExpression();
            if (expList == null) {
                expList = setClause.getSetExpressionList();
            }
            if (expList != null && expList.size() > 0) {
                this.updateColumnName = expList.get(0).toString();
            }
        }
        final WhereExpression newWhereExpression = new WhereExpression();
        final WhereItem newWhereItem = new WhereItem();
        final WhereColumn newWhereColumn = new WhereColumn();
        final Vector colExp = new Vector();
        final TableColumn newTableColumn = new TableColumn();
        newTableColumn.setColumnName(this.updateColumnName);
        if (this.updateColumnName.indexOf(".") == -1) {
            if (orgTableName == null) {
                newTableColumn.setTableName(tableName);
            }
            else {
                newTableColumn.setTableName(orgTableName);
            }
        }
        colExp.addElement(newTableColumn);
        newWhereColumn.setColumnExpression(colExp);
        newWhereItem.setLeftWhereExp(newWhereColumn);
        newWhereItem.setOperator("IN");
        final SelectQueryStatement newSQS = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        selectStmt.setSelectClause("SELECT");
        final Vector columnList = new Vector();
        columnList.addElement(newWhereColumn);
        selectStmt.setSelectItemList(columnList);
        newSQS.setSelectStatement(selectStmt);
        newSQS.setFromClause(this);
        newSQS.setWhereExpression(whereExpression);
        if (database == 1) {
            newWhereItem.setRightWhereSubQuery(newSQS.toOracleSelect());
        }
        else if (database == 2) {
            newWhereItem.setRightWhereSubQuery(newSQS.toMSSQLServerSelect());
        }
        else if (database == 7) {
            newWhereItem.setRightWhereSubQuery(newSQS.toSybaseSelect());
        }
        else if (database == 3) {
            newWhereItem.setRightWhereSubQuery(newSQS.toDB2Select());
        }
        else if (database == 4) {
            newWhereItem.setRightWhereSubQuery(newSQS.toPostgreSQLSelect());
        }
        else if (database == 5) {
            newWhereItem.setRightWhereSubQuery(newSQS.toMySQLSelect());
        }
        else if (database == 8) {
            newWhereItem.setRightWhereSubQuery(newSQS.toANSISelect());
        }
        else if (database == 6) {
            newWhereItem.setRightWhereSubQuery(newSQS.toInformixSelect());
        }
        else if (database == 11) {
            newWhereItem.setRightWhereSubQuery(newSQS.toNetezzaSelect());
        }
        else if (database == 13) {
            newWhereItem.setRightWhereSubQuery(newSQS.toVectorWiseSelect());
        }
        final Vector dum = new Vector();
        dum.addElement(newWhereItem);
        newWhereExpression.setWhereItem(dum);
        uqs.setWhereClause(newWhereExpression);
        uqs.setFromClause(null);
    }
    
    public void addFromItem(final FromTable ft) {
        this.fromItemList.addElement(ft);
    }
    
    public void processTheFromTableInsideTheFromItemList(final Vector originalFromItemList, final Vector fromItems, final TableObject tableObject, String tableName, String orgTableName, String alias, final UpdateQueryStatement uqs, final WhereExpression whereExpression) {
        for (int i = 0; i < fromItems.size(); ++i) {
            if (fromItems.get(i) instanceof FromTable) {
                final FromTable ft = fromItems.get(i);
                if (ft.getTableName() != null && ft.getAliasName() != null) {
                    final Object tableObjectInFrom = ft.getTableName();
                    if (tableObjectInFrom instanceof String) {
                        String tableNameInFrom = tableObject.toString();
                        if (tableNameInFrom.indexOf(46) != -1) {
                            tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                            if (tableNameInFrom.indexOf(46) != -1) {
                                tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                            }
                        }
                        if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                            orgTableName = tableName;
                            tableName = ft.getAliasName();
                            alias = ft.getAliasName();
                        }
                    }
                }
                else if (ft.getTableName() != null) {
                    final Object tableObjectInFrom = ft.getTableName();
                    if (tableObjectInFrom instanceof String) {
                        String tableNameInFrom = tableObjectInFrom.toString();
                        if (tableNameInFrom.indexOf(46) != -1) {
                            tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                            if (tableNameInFrom.indexOf(46) != -1) {
                                tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                            }
                        }
                        if (tableNameInFrom.trim().equalsIgnoreCase(tableName) && fromItems.size() == 1) {
                            uqs.setFromClause(null);
                            return;
                        }
                        if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                            if (ft != null && ft.getJoinExpression() != null) {
                                for (int j = 0; j < ft.getJoinExpression().size(); ++j) {
                                    final WhereExpression whereExp = ft.getJoinExpression().get(j);
                                    if (whereExp.getOperator() == null || (whereExp.getOperator() != null && whereExp.getOperator().size() < 1)) {
                                        whereExpression.addOperator("AND");
                                    }
                                    else {
                                        for (int k = 0; k < whereExp.getOperator().size(); ++k) {
                                            whereExpression.addOperator(whereExp.getOperator().get(k));
                                        }
                                    }
                                    whereExpression.addWhereExpression(ft.getJoinExpression().get(j));
                                }
                            }
                            fromItems.remove(i);
                            alias = tableName.toUpperCase();
                        }
                    }
                }
            }
            else if (fromItems.get(i) instanceof FromClause) {
                final Vector newFromItems = fromItems.get(i).getFromItemList();
                this.processTheFromTableInsideTheFromItemList(originalFromItemList, newFromItems, tableObject, tableName, orgTableName, alias, uqs, whereExpression);
            }
        }
    }
    
    public void convertToSubQuery(final DeleteQueryStatement dqs, final int database, final FromClause fc) throws ConvertException, ParseException {
        final TableExpression tblExp = dqs.getTableExpression();
        final TableClause tc = tblExp.getTableClauseList().get(0);
        final TableObject tableObject = tc.getTableObject();
        String tableName = tableObject.getTableName();
        String orgTableName = null;
        String alias = tableName.toUpperCase();
        final String columnName = new String();
        if (fc != null && fc.getFromItemList() != null) {
            final Vector fromItems = fc.getFromItemList();
            for (int i = 0; i < fromItems.size(); ++i) {
                if (fromItems.get(i) instanceof FromTable) {
                    final FromTable ft = fromItems.get(i);
                    if (ft.getTableName() != null && ft.getAliasName() != null) {
                        final Object tableObjectInFrom = ft.getTableName();
                        if (tableObjectInFrom instanceof String) {
                            String tableNameInFrom = tableObject.toString();
                            if (tableNameInFrom.indexOf(46) != -1) {
                                tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                                if (tableNameInFrom.indexOf(46) != -1) {
                                    tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46) + 1);
                                }
                            }
                            if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                                orgTableName = tableName;
                                tableName = ft.getAliasName();
                                alias = ft.getAliasName();
                            }
                        }
                    }
                    else if (ft.getTableName() != null) {
                        final Object tableObjectInFrom = ft.getTableName();
                        if (tableObjectInFrom instanceof String) {
                            String tableNameInFrom = tableObjectInFrom.toString();
                            if (tableNameInFrom.indexOf(46) != -1) {
                                tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                                if (tableNameInFrom.indexOf(46) != -1) {
                                    tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                                }
                            }
                            if (tableNameInFrom.trim().equalsIgnoreCase(tableName) && fromItems.size() == 1) {
                                dqs.setFromClause(null);
                                return;
                            }
                            if (tableNameInFrom.trim().equalsIgnoreCase("DUAL") && fromItems.size() > 1) {
                                fromItems.remove(i);
                            }
                            else if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {}
                        }
                    }
                }
                else if (fromItems.get(i) instanceof FromClause) {
                    final FromClause newFC = fromItems.get(i);
                    newFC.convertToSubQuery(dqs, database, newFC);
                }
            }
        }
        final WhereExpression whereExpression = dqs.getWhereExpression();
        this.changeWhereItem(whereExpression, tableName, columnName, orgTableName);
        final Vector fromItemVector = this.getFromItemList();
        String columnNameString = new String();
        if (this.updateColumnName == null && fromItemVector != null) {
            for (int j = 0; j < fromItemVector.size(); ++j) {
                if (fromItemVector.elementAt(j) instanceof FromTable) {
                    final FromTable fromTableClause = fromItemVector.get(j);
                    if (fromTableClause.getJoinExpression() != null) {
                        final Vector joinExpVector = fromTableClause.getJoinExpression();
                        for (int count = 0; count < joinExpVector.size(); ++count) {
                            if (joinExpVector.elementAt(count) instanceof WhereExpression) {
                                final WhereExpression whereExp = joinExpVector.elementAt(count);
                                final Vector whereItemsVector = whereExp.getWhereItems();
                                boolean columnAdded = false;
                                for (int index = 0; index < whereItemsVector.size(); ++index) {
                                    WhereItem whereItem = null;
                                    if (whereItemsVector.elementAt(index) instanceof WhereItem) {
                                        whereItem = whereItemsVector.elementAt(index);
                                        WhereColumn wc = whereItem.getLeftWhereExp();
                                        final Vector colExpression = wc.getColumnExpression();
                                        if (colExpression != null) {
                                            final Object object = colExpression.elementAt(0);
                                            if (object instanceof TableColumn) {
                                                final TableColumn tblCol = (TableColumn)object;
                                                if (tableName.equalsIgnoreCase(tblCol.getTableName())) {
                                                    tblCol.setTableName(alias);
                                                    columnNameString = tblCol.getColumnName();
                                                    if (!columnAdded) {
                                                        this.updateColumnName = columnNameString;
                                                        columnAdded = true;
                                                    }
                                                }
                                            }
                                        }
                                        Object obj = whereItem.getRightWhereExp();
                                        if (obj instanceof WhereColumn) {
                                            wc = (WhereColumn)obj;
                                            final Vector colExp = wc.getColumnExpression();
                                            if (colExp != null) {
                                                if (colExp.size() != 0) {
                                                    obj = colExp.elementAt(0);
                                                    if (obj instanceof TableColumn) {
                                                        final TableColumn tblCol = (TableColumn)obj;
                                                        if (tableName.equalsIgnoreCase(tblCol.getTableName())) {
                                                            tblCol.setTableName(alias);
                                                            columnNameString = tblCol.getColumnName();
                                                            if (!columnAdded) {
                                                                this.updateColumnName = columnNameString;
                                                                columnAdded = true;
                                                            }
                                                        }
                                                        else {
                                                            columnNameString = tblCol.getColumnName();
                                                            if (!columnAdded) {
                                                                this.updateColumnName = columnNameString;
                                                                columnAdded = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else if (whereItemsVector.elementAt(index) instanceof WhereExpression) {
                                        this.changeWhereItem(whereItemsVector.elementAt(index), tableName, columnNameString, orgTableName);
                                    }
                                }
                                if (this.updateColumnName == null && columnAdded) {
                                    this.updateColumnName = columnNameString;
                                }
                            }
                        }
                    }
                }
            }
        }
        final WhereExpression newWhereExpression = new WhereExpression();
        final WhereItem newWhereItem = new WhereItem();
        final WhereColumn newWhereColumn = new WhereColumn();
        final Vector colExp2 = new Vector();
        final TableColumn newTableColumn = new TableColumn();
        newTableColumn.setColumnName(this.updateColumnName);
        if (orgTableName != null) {
            newTableColumn.setTableName(orgTableName);
        }
        else {
            newTableColumn.setTableName(tableName);
        }
        if (this.updateColumnName == null) {
            FromClause.doNotAddDotInSubquery = true;
        }
        colExp2.addElement(newTableColumn);
        newWhereColumn.setColumnExpression(colExp2);
        newWhereItem.setLeftWhereExp(newWhereColumn);
        newWhereItem.setOperator("IN");
        final SelectQueryStatement newSQS = new SelectQueryStatement();
        final SelectStatement selectStmt = new SelectStatement();
        selectStmt.setSelectClause("SELECT");
        final Vector columnList = new Vector();
        columnList.addElement(newWhereColumn);
        selectStmt.setSelectItemList(columnList);
        newSQS.setSelectStatement(selectStmt);
        newSQS.setFromClause(this);
        newSQS.setWhereExpression(whereExpression);
        final SwisSQLAPI tempAPI = new SwisSQLAPI();
        tempAPI.setSQLString(newSQS.toString());
        if (database == 1) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(1));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        else if (database == 2) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(2));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        else if (database == 7) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(7));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        else if (database == 3) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(3));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        else if (database == 4) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(4));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        else if (database == 5) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(5));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        else if (database == 8) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(8));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        else if (database == 6) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(6));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        else if (database == 11) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(11));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        else if (database == 13) {
            tempAPI.setSQLString(newSQS.toString());
            tempAPI.setSQLString(tempAPI.convert(13));
            newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
        }
        FromClause.doNotAddDotInSubquery = false;
        final Vector dum = new Vector();
        dum.addElement(newWhereItem);
        newWhereExpression.setWhereItem(dum);
        dqs.setWhereClause(newWhereExpression);
        dqs.setFromClause(null);
    }
    
    public Vector changeOrderForOuter(final Vector containsFromItems) {
        final Vector ChangedVectorOrder = new Vector();
        boolean checkForOuter = false;
        int countOuter = 0;
        for (int i = 0; i < containsFromItems.size(); ++i) {
            if (containsFromItems.elementAt(i) instanceof FromTable) {
                if (containsFromItems.get(i).getOuter() != null || checkForOuter) {
                    if (containsFromItems.get(i).getOuterOpenBrace() != null) {
                        checkForOuter = true;
                    }
                    ChangedVectorOrder.insertElementAt(containsFromItems.get(i), i);
                    ++countOuter;
                    if (containsFromItems.get(i).getOuterClosedBrace() != null) {
                        checkForOuter = false;
                    }
                }
                else if (i < ChangedVectorOrder.size()) {
                    ChangedVectorOrder.insertElementAt(containsFromItems.get(i), i - countOuter);
                }
                else {
                    ChangedVectorOrder.add(containsFromItems.get(i));
                }
            }
            else if (containsFromItems.elementAt(i) instanceof FromClause) {
                final FromClause newFC = containsFromItems.get(i);
                final Vector fromClauseVector = newFC.getFromItemList();
                newFC.changeOrderForOuter(fromClauseVector);
            }
        }
        return ChangedVectorOrder;
    }
    
    public Vector getOuterFromTableNames(final Vector containsFromItems) {
        Vector outerFromItemNamesList = null;
        boolean ifInOuterJoin = false;
        if (this.fromItemList != null) {
            outerFromItemNamesList = new Vector();
            for (int i = 0; i < this.fromItemList.size(); ++i) {
                if (this.fromItemList.elementAt(i) instanceof FromTable) {
                    final FromTable newFromTable = this.fromItemList.get(i);
                    if (newFromTable.getOuter() != null || ifInOuterJoin) {
                        if (newFromTable.getOuterOpenBrace() != null) {
                            ifInOuterJoin = true;
                        }
                        if (newFromTable.getAliasName() != null) {
                            outerFromItemNamesList.add(newFromTable.getAliasName());
                        }
                        else {
                            outerFromItemNamesList.add(newFromTable.getTableName().toString());
                        }
                        if (newFromTable.getOuterClosedBrace() != null) {
                            ifInOuterJoin = false;
                        }
                    }
                }
                else if (this.fromItemList.elementAt(i) instanceof FromClause) {
                    final FromClause newFC = this.fromItemList.get(i);
                    final Vector newFromItemList = newFC.getFromItemList();
                    newFC.getOuterFromTableNames(newFromItemList);
                }
            }
        }
        return outerFromItemNamesList;
    }
    
    public FromClause copyObjectValues() {
        final FromClause fc = new FromClause();
        fc.setFromClause(this.getFromClause());
        final Vector vNew = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            vNew.addElement(this.fromItemList.elementAt(i));
        }
        fc.setFromItemList(vNew);
        fc.setOpenBraces(this.openBraces);
        fc.setClosedBraces(this.closedBraces);
        fc.baseFromClauseFound = this.baseFromClauseFound;
        fc.updateColumnName = this.updateColumnName;
        fc.setFetchClauseFromSQS(this.fetchClauseFromSQS);
        fc.setObjectContext(this.context);
        return fc;
    }
    
    public FromClause toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        FromClause fc = new FromClause();
        fc.setFromClause(this.fromClause);
        final Vector v_fil = new Vector();
        for (int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
                v_fil.addElement(this.fromItemList.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else if (this.fromItemList.elementAt(i) instanceof FromClause) {
                v_fil.addElement(this.fromItemList.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
            }
        }
        fc.setFromItemList(v_fil);
        if (v_fil.size() == 1) {
            FromTable ft = null;
            if (v_fil.firstElement() instanceof FromTable) {
                ft = v_fil.elementAt(0);
            }
            else {
                ft = this.getFirstElement();
            }
            if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
                fc = null;
            }
        }
        if (this.pivot_clause != null && this.getFromItemList().size() == 1 && this.getFromItemList().get(0) instanceof FromTable) {
            final FromTable ft = this.getFromItemList().get(0);
            if (ft.getTableName() instanceof SelectQueryStatement) {
                this.pivot_clause.setSubQuery((SelectQueryStatement)ft.getTableName());
                this.pivot_clause.toVectorWiseSelect(to_sqs, from_sqs);
                final SelectQueryStatement sqs = (SelectQueryStatement)ft.getTableName();
                return sqs.getFromClause();
            }
        }
        return fc;
    }
    
    static {
        FromClause.doNotAddDotInSubquery = false;
    }
}
