package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class nvl extends FunctionCalls
{
    private String targetDataType;
    private boolean inArithmeticExpr;
    
    public nvl() {
        this.inArithmeticExpr = false;
    }
    
    @Override
    public void setInArithmeticExpression(final boolean inArithmeticExpr) {
        this.inArithmeticExpr = inArithmeticExpr;
    }
    
    @Override
    public void setTargetDataType(final String targetDataType) {
        this.targetDataType = targetDataType;
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("NVL");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        int noOfArguments = this.functionArguments.size();
        if (noOfArguments > 2) {
            final FunctionCalls[] nvlFunctions = new FunctionCalls[noOfArguments - 1];
            final Vector[] argList = new Vector[noOfArguments - 1];
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("NVL");
            final int argc = noOfArguments;
            for (int i = 0; i < argc - 1; ++i) {
                argList[i] = new Vector();
                nvlFunctions[i] = new FunctionCalls();
                if (i == 0) {
                    argList[i].add(this.functionArguments.remove(noOfArguments - 2));
                    --noOfArguments;
                    argList[i].add(this.functionArguments.remove(noOfArguments - 1));
                    --noOfArguments;
                }
                else {
                    argList[i].add(this.functionArguments.remove(noOfArguments - 1));
                    --noOfArguments;
                    argList[i].add(nvlFunctions[i - 1]);
                }
                nvlFunctions[i].setFunctionArguments(argList[i]);
                nvlFunctions[i].setFunctionName(innerFunction);
            }
            this.setFunctionArguments(argList[argc - 2]);
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("NVL")) {
            this.functionName.setColumnName("ISNULL");
        }
        else {
            this.functionName.setColumnName("COALESCE");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("NVL")) {
            this.functionName.setColumnName("ISNULL");
        }
        else {
            this.functionName.setColumnName("COALESCE");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                this.functionArguments.elementAt(i_count).setTargetDataType(this.targetDataType);
                this.functionArguments.elementAt(i_count).setInArithmeticExpression(this.inArithmeticExpr);
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final boolean needsCasting = this.needsCastingForStringLiterals() || (to_sqs != null && to_sqs.canCastAllToTextColumns());
        boolean coalesce = true;
        if (!needsCasting && this.functionArguments.size() <= 10) {
            FunctionCalls fc = this.convertToIFFunctionCall(to_sqs, from_sqs);
            if (fc != null) {
                fc.toPostgreSQL(to_sqs, from_sqs);
                this.functionName.setColumnName("IF");
                this.setFunctionArguments(fc.getFunctionArguments());
                fc = null;
                coalesce = false;
            }
            else {
                coalesce = true;
            }
        }
        if (coalesce) {
            this.functionName.setColumnName("COALESCE");
            final Vector arguments = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn sc = this.functionArguments.elementAt(i_count);
                    sc.convertSelectColumnToTextDataType(needsCasting);
                    arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            this.setFunctionArguments(arguments);
            if (to_sqs != null) {
                to_sqs.addCurrentIndexToCoalesceFunctionList();
            }
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("NVL");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        int noOfArguments = this.functionArguments.size();
        if (noOfArguments > 2) {
            final FunctionCalls[] nvlFunctions = new FunctionCalls[noOfArguments - 1];
            final Vector[] argList = new Vector[noOfArguments - 1];
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("NVL");
            final int argc = noOfArguments;
            for (int i = 0; i < argc - 1; ++i) {
                argList[i] = new Vector();
                nvlFunctions[i] = new FunctionCalls();
                if (i == 0) {
                    argList[i].add(this.functionArguments.remove(noOfArguments - 2));
                    --noOfArguments;
                    argList[i].add(this.functionArguments.remove(noOfArguments - 1));
                    --noOfArguments;
                }
                else {
                    argList[i].add(this.functionArguments.remove(noOfArguments - 1));
                    --noOfArguments;
                    argList[i].add(nvlFunctions[i - 1]);
                }
                nvlFunctions[i].setFunctionArguments(argList[i]);
                nvlFunctions[i].setFunctionName(innerFunction);
            }
            this.setFunctionArguments(argList[argc - 2]);
        }
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("NVL");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTimesTenSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        int noOfArguments = this.functionArguments.size();
        if (noOfArguments > 2) {
            final FunctionCalls[] nvlFunctions = new FunctionCalls[noOfArguments - 1];
            final Vector[] argList = new Vector[noOfArguments - 1];
            final TableColumn innerFunction = new TableColumn();
            innerFunction.setOwnerName(this.functionName.getOwnerName());
            innerFunction.setTableName(this.functionName.getTableName());
            innerFunction.setColumnName("NVL");
            final int argc = noOfArguments;
            for (int i = 0; i < argc - 1; ++i) {
                argList[i] = new Vector();
                nvlFunctions[i] = new FunctionCalls();
                if (i == 0) {
                    argList[i].add(this.functionArguments.remove(noOfArguments - 2));
                    --noOfArguments;
                    argList[i].add(this.functionArguments.remove(noOfArguments - 1));
                    --noOfArguments;
                }
                else {
                    argList[i].add(this.functionArguments.remove(noOfArguments - 1));
                    --noOfArguments;
                    argList[i].add(nvlFunctions[i - 1]);
                }
                nvlFunctions[i].setFunctionArguments(argList[i]);
                nvlFunctions[i].setFunctionName(innerFunction);
            }
            this.setFunctionArguments(argList[argc - 2]);
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("nvl")) {
            this.functionName.setColumnName("NVL");
        }
        else if (this.functionName.getColumnName().equalsIgnoreCase("coalesce")) {
            this.functionName.setColumnName("COALESCE");
        }
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        boolean isDateArg = false;
        for (int j = 0; j < this.functionArguments.size(); ++j) {
            if (this.functionArguments.elementAt(j) instanceof SelectColumn) {
                if (this.functionArguments.elementAt(j).getColumnExpression().get(0) instanceof FunctionCalls) {
                    final FunctionCalls dateFunc = this.functionArguments.elementAt(j).getColumnExpression().get(0);
                    if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
                        isDateArg = true;
                    }
                }
                else if (this.functionArguments.elementAt(j).getColumnExpression().get(0) instanceof TableColumn) {
                    final TableColumn dateFunc2 = this.functionArguments.elementAt(j).getColumnExpression().get(0);
                    if (SwisSQLUtils.getFunctionReturnType(dateFunc2.getColumnName(), null).equalsIgnoreCase("date")) {
                        isDateArg = true;
                    }
                }
            }
            else if (this.functionArguments.elementAt(j) instanceof FunctionCalls) {
                final FunctionCalls dateFunc = this.functionArguments.elementAt(j);
                if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
                    isDateArg = true;
                }
            }
            else if (this.functionArguments.elementAt(j) instanceof String) {}
        }
        if (isDateArg) {
            final Vector newArguments = new Vector();
            for (int k = 0; k < arguments.size(); ++k) {
                final FunctionCalls castTimestamp = new FunctionCalls();
                final TableColumn castTcn = new TableColumn();
                castTcn.setColumnName("CAST");
                castTimestamp.setFunctionName(castTcn);
                castTimestamp.setAsDatatype("AS");
                final DateClass castDatatype = new DateClass();
                castDatatype.setDatatypeName("TIMESTAMP");
                castDatatype.setOpenBrace("(");
                castDatatype.setSize("0");
                castDatatype.setClosedBrace(")");
                final Vector castTimestampArgs = new Vector();
                castTimestampArgs.add(arguments.get(k));
                castTimestampArgs.add(castDatatype);
                castTimestamp.setFunctionArguments(castTimestampArgs);
                newArguments.add(castTimestamp);
            }
            this.setFunctionArguments(newArguments);
        }
        else {
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("COALESCE");
        final boolean needsCasting = this.needsCastingForStringLiterals();
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.elementAt(i_count);
                sc.convertSelectColumnToTextDataType(needsCasting);
                arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    public FunctionCalls getFunctionCall() {
        final FunctionCalls fc = new FunctionCalls();
        final TableColumn tc = new TableColumn();
        tc.setColumnName("IF");
        fc.setFunctionName(tc);
        return fc;
    }
    
    public FunctionCalls convertToIFFunctionCall(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        FunctionCalls fcFinal = null;
        try {
            if (from_sqs != null && from_sqs.canUseIFFunctionForPGCaseWhenExp() && !from_sqs.isAmazonRedShift()) {
                final Vector fcList = new Vector();
                FunctionCalls fc;
                fcFinal = (fc = this.getFunctionCall());
                FunctionCalls reference = null;
                for (int i = 0; i < this.functionArguments.size(); ++i) {
                    if (!(this.functionArguments.elementAt(i) instanceof SelectColumn)) {
                        fcFinal = null;
                        break;
                    }
                    final SelectColumn sc = this.functionArguments.elementAt(i);
                    if (i != 0) {
                        fc = reference;
                    }
                    final SelectColumn sc2 = new SelectColumn();
                    final Vector colExp = new Vector();
                    final WhereItem wi = new WhereItem();
                    final WhereColumn wc = new WhereColumn();
                    wc.setColumnExpression(sc.getColumnExpression());
                    wi.setLeftWhereExp(wc);
                    wi.setRightWhereExp(null);
                    wi.setOperator("IS NOT NULL");
                    colExp.add(wi);
                    sc2.setColumnExpression(colExp);
                    final SelectColumn sc3 = sc;
                    final SelectColumn sc4 = new SelectColumn();
                    final Vector colExp2 = new Vector();
                    if (i + 1 < this.functionArguments.size()) {
                        reference = this.getFunctionCall();
                        colExp2.add(reference);
                    }
                    else {
                        colExp2.add("NULL");
                    }
                    sc4.setColumnExpression(colExp2);
                    final Vector fnArgs = new Vector();
                    fnArgs.add(sc2);
                    fnArgs.add(sc3);
                    fnArgs.add(sc4);
                    fc.setFunctionArguments(fnArgs);
                    fcList.add(fc);
                }
            }
        }
        catch (final Exception e) {
            fcFinal = null;
        }
        return fcFinal;
    }
}
