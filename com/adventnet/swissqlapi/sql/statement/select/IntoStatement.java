package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.insert.InsertClause;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateSequenceStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.ArrayList;

public class IntoStatement
{
    private String IntoClause;
    private String TableQualifier;
    private String TableKeyword;
    private String FileQualifier;
    private String TableOrFileName;
    private ArrayList vArrayList;
    private UserObjectContext context;
    private CommentClass commentObj;
    private String fieldsTerminatedByString;
    private String optionallyEnclosed;
    private String linesTerminated;
    
    public IntoStatement() {
        this.vArrayList = new ArrayList();
        this.context = null;
        this.fieldsTerminatedByString = null;
        this.optionallyEnclosed = null;
        this.linesTerminated = null;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setIntoClause(final String s_ic) {
        this.IntoClause = s_ic;
    }
    
    public void setTableQualifier(final String s_tq) {
        this.TableQualifier = s_tq;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setTableKeyword(final String s_tk) {
        this.TableKeyword = s_tk;
    }
    
    public void setFileQualifier(final String s_fq) {
        this.FileQualifier = s_fq;
    }
    
    public void setTableOrFileName(final String s_tofn) {
        this.TableOrFileName = s_tofn;
    }
    
    public void addVarray(final String vArray) {
        this.vArrayList.add(vArray);
    }
    
    public void setVarray(final ArrayList vArray) {
        this.vArrayList = vArray;
    }
    
    public void setFieldsTerminated(final String fields) {
        this.fieldsTerminatedByString = fields;
    }
    
    public void setOptionallyEnclosed(final String optionallyEnclosedBy) {
        this.optionallyEnclosed = optionallyEnclosedBy;
    }
    
    public void setLinesTerminated(final String linesTerminatedBy) {
        this.linesTerminated = linesTerminatedBy;
    }
    
    public String getFieldsTerminated() {
        return this.fieldsTerminatedByString;
    }
    
    public String getOptionallyEnclosed() {
        return this.optionallyEnclosed;
    }
    
    public String getLinesTerminated() {
        return this.linesTerminated;
    }
    
    public String getTableOrFileName() {
        return this.TableOrFileName;
    }
    
    public ArrayList getVarray() {
        return this.vArrayList;
    }
    
    public String getTableQualifier() {
        return this.TableQualifier;
    }
    
    public String getFileQualifier() {
        return this.FileQualifier;
    }
    
    public String getTableKeyword() {
        return this.TableKeyword;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public IntoStatement toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final IntoStatement is = new IntoStatement();
        is.setIntoClause(this.IntoClause);
        is.setTableQualifier(this.TableQualifier);
        is.setTableKeyword(this.TableKeyword);
        is.setTableOrFileName(this.TableOrFileName);
        if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
        }
        return is;
    }
    
    public IntoStatement toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final IntoStatement is = new IntoStatement();
        is.setIntoClause(this.IntoClause);
        is.setTableQualifier(this.TableQualifier);
        is.setTableKeyword(this.TableKeyword);
        is.setTableOrFileName(this.TableOrFileName);
        if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
        }
        return is;
    }
    
    public IntoStatement toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        IntoStatement is = new IntoStatement();
        if (this.TableKeyword != null || this.FileQualifier == null) {
            to_sqs.setCreateStatement("CREATE TABLE " + this.TableOrFileName + " AS");
            is = null;
        }
        else {
            if (this.FileQualifier == null || !this.FileQualifier.trim().equalsIgnoreCase("OUTFILE")) {
                throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
            }
            is.setIntoClause("INTO");
            is.setFileQualifier(this.getFileQualifier());
            is.setTableOrFileName(this.getTableOrFileName());
            if (this.fieldsTerminatedByString != null) {
                is.setFieldsTerminated(this.fieldsTerminatedByString);
            }
            if (this.linesTerminated != null) {
                is.setLinesTerminated(this.linesTerminated);
            }
            if (this.optionallyEnclosed != null) {
                is.setOptionallyEnclosed(this.optionallyEnclosed);
            }
        }
        return is;
    }
    
    public IntoStatement toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        IntoStatement is = new IntoStatement();
        String origTableName = null;
        if (this.TableOrFileName != null) {
            origTableName = this.TableOrFileName;
            this.TableOrFileName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableOrFileName, SwisSQLUtils.getKeywords(1), null, 1);
        }
        if (SwisSQLOptions.PLSQL) {
            is.setIntoClause(this.IntoClause);
            is.setTableOrFileName(this.TableOrFileName);
        }
        else {
            if (this.TableKeyword != null | this.FileQualifier == null) {
                if (from_sqs.getSequenceForIdentityFn() != null) {
                    final CreateSequenceStatement createSeq = from_sqs.getSequenceForIdentityFn();
                    createSeq.setSequence("CREATE SEQUENCE ");
                    final TableObject tobj = new TableObject();
                    tobj.setTableName(this.TableOrFileName + "_SEQ");
                    createSeq.setSchemaName(tobj);
                    to_sqs.setSequenceForIdentityFn(createSeq);
                }
                else {
                    String temp = this.TableOrFileName;
                    if (this.context != null && origTableName != null) {
                        temp = (String)this.context.getEquivalent(origTableName);
                        if (temp == null || (temp != null && temp.equals(origTableName))) {
                            temp = this.TableOrFileName;
                        }
                    }
                    to_sqs.setCreateStatement("CREATE GLOBAL TEMPORARY TABLE " + temp + " AS");
                }
            }
            else if (this.FileQualifier != null) {
                throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
            }
            is = null;
        }
        return is;
    }
    
    public IntoStatement toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        IntoStatement is = new IntoStatement();
        if (this.TableKeyword != null || this.FileQualifier == null) {
            to_sqs.setCreateStatement("CREATE TABLE " + this.TableOrFileName + " AS(");
            to_sqs.setDefinitionOnly(") DEFINITION ONLY;");
            final InsertQueryStatement iqs = new InsertQueryStatement();
            final InsertClause ic = new InsertClause();
            final OptionalSpecifier optionalSp = new OptionalSpecifier();
            final TableClause tc = new TableClause();
            final TableExpression tableExp = new TableExpression();
            ic.setInsert("INSERT");
            optionalSp.setInto("INTO");
            final TableObject tableObj = new TableObject();
            tableObj.setTableName(this.TableOrFileName);
            final ArrayList tableExpList = new ArrayList();
            tc.setTableObject(tableObj);
            tableExpList.add(tc);
            tableExp.setTableClauseList(tableExpList);
            ic.setOptionalSpecifier(optionalSp);
            ic.setTableExpression(tableExp);
            iqs.setInsertClause(ic);
            final SelectQueryStatement sqs = new SelectQueryStatement();
            sqs.setIntoStatement(null);
            SelectStatement ss = new SelectStatement();
            ss = from_sqs.getSelectStatement();
            ss.setOpenBraceForSelectInInsertQuery("(");
            sqs.setSelectStatement(ss);
            sqs.setFromClause(from_sqs.getFromClause());
            sqs.setGroupByStatement(from_sqs.getGroupByStatement());
            sqs.setHavingStatement(from_sqs.getHavingStatement());
            sqs.setOrderByStatement(from_sqs.getOrderByStatement());
            sqs.setLimitClause(from_sqs.getLimitClause());
            sqs.setSetOperatorClause(from_sqs.getSetOperatorClause());
            sqs.setWhereExpression(from_sqs.getWhereExpression());
            iqs.setSubQuery(sqs.toDB2Select());
            to_sqs.setInsertQueryStatement(iqs);
        }
        else if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
        }
        is = null;
        return is;
    }
    
    public IntoStatement toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final IntoStatement is = new IntoStatement();
        is.setCommentClass(this.commentObj);
        if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
        }
        is.setIntoClause(this.IntoClause);
        if (this.TableOrFileName != null && this.TableOrFileName.startsWith(":")) {
            is.setTableOrFileName("@" + this.TableOrFileName.substring(1));
        }
        else {
            is.setTableOrFileName(this.TableOrFileName);
        }
        if (this.vArrayList != null && this.vArrayList.size() > 0) {
            final ArrayList newVarray = new ArrayList();
            for (int i = 0; i < this.vArrayList.size(); ++i) {
                newVarray.add("@" + this.vArrayList.get(i).toString().substring(1));
            }
            is.setVarray(newVarray);
        }
        return is;
    }
    
    public IntoStatement toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final IntoStatement is = new IntoStatement();
        if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
        }
        is.setIntoClause(this.IntoClause);
        if (this.TableOrFileName != null && this.TableOrFileName.startsWith(":")) {
            is.setTableOrFileName("@" + this.TableOrFileName.substring(1));
        }
        else {
            is.setTableOrFileName(this.TableOrFileName);
        }
        if (this.vArrayList != null && this.vArrayList.size() > 0) {
            final ArrayList newVarray = new ArrayList();
            for (int i = 0; i < this.vArrayList.size(); ++i) {
                newVarray.add("@" + this.vArrayList.get(i).toString().substring(1));
            }
            is.setVarray(newVarray);
        }
        return is;
    }
    
    public IntoStatement toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        IntoStatement is = new IntoStatement();
        if (this.TableKeyword != null || this.FileQualifier == null) {
            final CreateQueryStatement cqs = SwisSQLUtils.constructCQS(this.TableOrFileName, from_sqs, to_sqs);
            to_sqs.setCreateStatement(cqs.toTimesTenString());
            to_sqs.setSelectStatement(null);
            to_sqs.setFromClause(null);
            to_sqs.setGroupByStatement(null);
            to_sqs.setHavingStatement(null);
            to_sqs.setOrderByStatement(null);
            to_sqs.setLimitClause(null);
            to_sqs.setSetOperatorClause(null);
            to_sqs.setWhereExpression(null);
            final InsertQueryStatement iqs = new InsertQueryStatement();
            final InsertClause ic = new InsertClause();
            final OptionalSpecifier optionalSp = new OptionalSpecifier();
            final TableClause tc = new TableClause();
            final TableExpression tableExp = new TableExpression();
            ic.setInsert("INSERT");
            optionalSp.setInto("INTO");
            final TableObject tableObj = new TableObject();
            tableObj.setTableName(this.TableOrFileName);
            final ArrayList tableExpList = new ArrayList();
            tc.setTableObject(tableObj);
            tableExpList.add(tc);
            tableExp.setTableClauseList(tableExpList);
            ic.setOptionalSpecifier(optionalSp);
            ic.setTableExpression(tableExp);
            iqs.setInsertClause(ic);
            final SelectQueryStatement sqs = new SelectQueryStatement();
            sqs.setIntoStatement(null);
            SelectStatement ss = new SelectStatement();
            ss = from_sqs.getSelectStatement();
            final Vector sourceSItems = ss.getSelectItemList();
            boolean isAliasExists = false;
            for (int k = 0; k < sourceSItems.size(); ++k) {
                final Object sourceObj = sourceSItems.get(k);
                if (sourceObj instanceof SelectColumn && ((SelectColumn)sourceObj).getAliasName() != null) {
                    isAliasExists = true;
                    break;
                }
            }
            if (!isAliasExists) {
                final Vector newSelItems = new Vector();
                final Vector colNames = cqs.getColumnNames();
                for (int i = 0; i < colNames.size(); ++i) {
                    final TableColumn tCol = new TableColumn();
                    tCol.setColumnName(colNames.get(i).getColumnName());
                    final SelectColumn sCol = new SelectColumn();
                    final Vector colExpr = new Vector();
                    colExpr.add(tCol);
                    sCol.setColumnExpression(colExpr);
                    if (i != colNames.size() - 1) {
                        sCol.setEndsWith(",");
                    }
                    newSelItems.add(sCol);
                }
                ss.setSelectItemList(newSelItems);
            }
            ss.setOpenBraceForSelectInInsertQuery("(");
            sqs.setSelectStatement(ss);
            sqs.setFromClause(from_sqs.getFromClause());
            sqs.setGroupByStatement(from_sqs.getGroupByStatement());
            sqs.setHavingStatement(from_sqs.getHavingStatement());
            sqs.setOrderByStatement(from_sqs.getOrderByStatement());
            sqs.setLimitClause(from_sqs.getLimitClause());
            sqs.setSetOperatorClause(from_sqs.getSetOperatorClause());
            sqs.setWhereExpression(from_sqs.getWhereExpression());
            iqs.setSubQuery(sqs.toTimesTenSelect());
            to_sqs.setInsertQueryStatement(iqs);
        }
        else if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
        }
        is = null;
        return is;
    }
    
    public IntoStatement toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final IntoStatement is = new IntoStatement();
        is.setIntoClause(this.IntoClause);
        is.setTableQualifier(this.TableQualifier);
        is.setTableKeyword(this.TableKeyword);
        is.setTableOrFileName(this.TableOrFileName);
        if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
        }
        return is;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString());
            sb.append("\n");
        }
        sb.append(this.IntoClause.toUpperCase());
        if (this.TableQualifier != null) {
            sb.append(" " + this.TableQualifier.toUpperCase());
        }
        else if (this.FileQualifier != null) {
            sb.append(" " + this.FileQualifier.toUpperCase());
        }
        if (this.TableKeyword != null) {
            sb.append(" " + this.TableKeyword.toUpperCase());
        }
        if (this.vArrayList.size() == 0) {
            if (this.context != null) {
                sb.append(" " + this.context.getEquivalent(this.TableOrFileName));
            }
            else {
                sb.append(" " + this.TableOrFileName);
            }
        }
        if (this.fieldsTerminatedByString != null) {
            sb.append(" " + this.fieldsTerminatedByString);
        }
        if (this.optionallyEnclosed != null) {
            sb.append(" " + this.optionallyEnclosed);
        }
        if (this.linesTerminated != null) {
            sb.append(" " + this.linesTerminated + " ");
        }
        else {
            for (int i = 0; i < this.vArrayList.size(); ++i) {
                if (i != this.vArrayList.size() - 1) {
                    sb.append(" " + this.vArrayList.get(i).toString() + ",");
                }
                else {
                    sb.append(" " + this.vArrayList.get(i).toString());
                }
            }
        }
        return sb.toString();
    }
    
    public IntoStatement toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        IntoStatement is = new IntoStatement();
        if (this.TableKeyword != null || this.FileQualifier == null) {
            to_sqs.setCreateStatement("CREATE TABLE " + this.TableOrFileName + " AS");
            is = null;
        }
        else {
            if (this.FileQualifier == null || !this.FileQualifier.trim().equalsIgnoreCase("OUTFILE")) {
                throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
            }
            is.setIntoClause("INTO");
            is.setFileQualifier(this.getFileQualifier());
            is.setTableOrFileName(this.getTableOrFileName());
            if (this.fieldsTerminatedByString != null) {
                is.setFieldsTerminated(this.fieldsTerminatedByString);
            }
            if (this.linesTerminated != null) {
                is.setLinesTerminated(this.linesTerminated);
            }
            if (this.optionallyEnclosed != null) {
                is.setOptionallyEnclosed(this.optionallyEnclosed);
            }
        }
        return is;
    }
}
