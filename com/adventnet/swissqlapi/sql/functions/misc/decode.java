package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import java.util.Map;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class decode extends FunctionCalls
{
    private TableColumn corrTableColumn;
    private String dataType;
    private boolean inArithmeticExpr;
    
    public decode() {
        this.inArithmeticExpr = false;
        this.CaseString = null;
    }
    
    @Override
    public void setInArithmeticExpression(final boolean inArithmeticExpr) {
        this.inArithmeticExpr = inArithmeticExpr;
    }
    
    @Override
    public void setTargetDataType(final String targetDataType) {
        this.dataType = targetDataType;
    }
    
    public CaseStatement getCaseStatement() {
        return this.caseStatement;
    }
    
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        this.CaseString = null;
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final StringBuffer sb = new StringBuffer();
        boolean requiresSearchedCaseStatement = false;
        final ArrayList positionOfNull = new ArrayList();
        SelectQueryStatement.beautyTabCount += 2;
        sb.append("\n");
        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
            sb.append("\t");
        }
        for (int i = 1; i < this.functionArguments.size() - 1; ++i) {
            if (this.functionArguments.get(i) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.get(i);
                final Vector columnList = sc.getColumnExpression();
                if (columnList != null) {
                    if (columnList.size() == 1 && columnList.get(0) instanceof String && columnList.get(0).trim().equalsIgnoreCase("NULL")) {
                        requiresSearchedCaseStatement = true;
                        positionOfNull.add(i + "");
                    }
                    else if (columnList.size() == 1 && columnList.get(0) instanceof String && columnList.get(0).trim().equalsIgnoreCase("''")) {
                        requiresSearchedCaseStatement = true;
                        columnList.set(0, "NULL");
                        positionOfNull.add(i + "");
                    }
                }
            }
        }
        sb.append("CASE ");
        if (!requiresSearchedCaseStatement) {
            sb.append(this.functionArguments.elementAt(0).toANSISelect(to_sqs, from_sqs).toString() + " ");
        }
        ++SelectQueryStatement.beautyTabCount;
        if ((this.functionArguments.size() & 0x1) != 0x0) {
            for (int i = 1; i < this.functionArguments.size(); ++i) {
                sb.append("\n");
                for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                    sb.append("\t");
                }
                sb.append("WHEN ");
                if (requiresSearchedCaseStatement) {
                    sb.append(this.functionArguments.elementAt(0).toANSISelect(to_sqs, from_sqs).toString());
                    if (positionOfNull.contains(i + "")) {
                        sb.append(" IS ");
                    }
                    else {
                        sb.append(" = ");
                    }
                }
                sb.append(this.functionArguments.elementAt(i).toANSISelect(to_sqs, from_sqs).toString() + " ");
                sb.append("THEN ");
                sb.append(this.functionArguments.elementAt(++i).toANSISelect(to_sqs, from_sqs).toString() + " ");
            }
        }
        else {
            int i;
            for (i = 1; i < this.functionArguments.size() - 1; ++i) {
                sb.append("\n");
                for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                    sb.append("\t");
                }
                sb.append("WHEN ");
                if (requiresSearchedCaseStatement) {
                    sb.append(this.functionArguments.elementAt(0).toANSISelect(to_sqs, from_sqs).toString());
                    if (positionOfNull.contains(i + "")) {
                        sb.append(" IS ");
                    }
                    else {
                        sb.append(" = ");
                    }
                }
                sb.append(this.functionArguments.elementAt(i).toANSISelect(to_sqs, from_sqs).toString() + " ");
                sb.append("THEN ");
                sb.append(this.functionArguments.elementAt(++i).toANSISelect(to_sqs, from_sqs).toString() + " ");
            }
            sb.append("\n");
            for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                sb.append("\t");
            }
            sb.append("ELSE ");
            sb.append(this.functionArguments.elementAt(i).toANSISelect(to_sqs, from_sqs).toString() + " ");
        }
        --SelectQueryStatement.beautyTabCount;
        sb.append("\n");
        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
            sb.append("\t");
        }
        sb.append("END");
        SelectQueryStatement.beautyTabCount -= 2;
        this.CaseString = sb.toString();
        this.functionName = null;
        this.argumentQualifier = null;
        this.functionArguments = null;
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final StringBuffer sb = new StringBuffer();
        boolean requiresSearchedCaseStatement = false;
        final ArrayList positionOfNull = new ArrayList();
        SelectQueryStatement.beautyTabCount += 2;
        sb.append("\n");
        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
            sb.append("\t");
        }
        for (int i = 1; i < this.functionArguments.size() - 1; ++i) {
            if (this.functionArguments.get(i) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.get(i);
                final Vector columnList = sc.getColumnExpression();
                if (columnList != null) {
                    if (columnList.size() == 1 && columnList.get(0) instanceof String && columnList.get(0).trim().equalsIgnoreCase("NULL")) {
                        requiresSearchedCaseStatement = true;
                        positionOfNull.add(i + "");
                    }
                    else if (columnList.size() == 1 && columnList.get(0) instanceof String && columnList.get(0).trim().equalsIgnoreCase("''")) {
                        requiresSearchedCaseStatement = true;
                        columnList.set(0, "NULL");
                        positionOfNull.add(i + "");
                    }
                }
            }
        }
        sb.append("CASE ");
        if (!requiresSearchedCaseStatement) {
            sb.append(this.functionArguments.elementAt(0).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
        }
        ++SelectQueryStatement.beautyTabCount;
        if ((this.functionArguments.size() & 0x1) != 0x0) {
            for (int i = 1; i < this.functionArguments.size(); ++i) {
                sb.append("\n");
                for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                    sb.append("\t");
                }
                sb.append("WHEN ");
                if (requiresSearchedCaseStatement) {
                    sb.append(this.functionArguments.elementAt(0).toMSSQLServerSelect(to_sqs, from_sqs).toString());
                    if (positionOfNull.contains(i + "")) {
                        sb.append(" IS ");
                    }
                    else {
                        sb.append(" = ");
                    }
                }
                sb.append(this.functionArguments.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
                sb.append("THEN ");
                sb.append(this.functionArguments.elementAt(++i).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
            }
        }
        else {
            int i;
            for (i = 1; i < this.functionArguments.size() - 1; ++i) {
                sb.append("\n");
                for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                    sb.append("\t");
                }
                sb.append("WHEN ");
                if (requiresSearchedCaseStatement) {
                    sb.append(this.functionArguments.elementAt(0).toMSSQLServerSelect(to_sqs, from_sqs).toString());
                    if (positionOfNull.contains(i + "")) {
                        sb.append(" IS ");
                    }
                    else {
                        sb.append(" = ");
                    }
                }
                sb.append(this.functionArguments.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
                sb.append("THEN ");
                sb.append(this.functionArguments.elementAt(++i).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
            }
            sb.append("\n");
            for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                sb.append("\t");
            }
            sb.append("ELSE ");
            sb.append(this.functionArguments.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
        }
        --SelectQueryStatement.beautyTabCount;
        sb.append("\n");
        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
            sb.append("\t");
        }
        sb.append("END");
        SelectQueryStatement.beautyTabCount -= 2;
        this.CaseString = sb.toString();
        this.functionName = null;
        this.argumentQualifier = null;
        this.functionArguments = null;
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final StringBuffer sb = new StringBuffer();
        SelectQueryStatement.beautyTabCount += 2;
        sb.append("\n");
        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
            sb.append("\t");
        }
        sb.append("CASE ");
        if (!SwisSQLOptions.caseWithEqualForDecode) {
            if (this.context != null) {
                this.functionArguments.elementAt(0).setObjectContext(this.context);
                sb.append(this.context.getEquivalent(this.functionArguments.elementAt(0).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
            }
            else {
                sb.append(this.functionArguments.elementAt(0).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
            }
        }
        ++SelectQueryStatement.beautyTabCount;
        if ((this.functionArguments.size() & 0x1) != 0x0) {
            for (int i = 1; i < this.functionArguments.size(); ++i) {
                sb.append("\n");
                for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                    sb.append("\t");
                }
                sb.append("WHEN ");
                if (SwisSQLOptions.caseWithEqualForDecode) {
                    if (this.context != null) {
                        this.functionArguments.elementAt(0).setObjectContext(this.context);
                        sb.append(this.context.getEquivalent(this.functionArguments.elementAt(0).toSybaseSelect(to_sqs, from_sqs).toString()) + " = ");
                    }
                    else {
                        sb.append(this.functionArguments.elementAt(0).toSybaseSelect(to_sqs, from_sqs).toString() + " = ");
                    }
                }
                if (this.context != null) {
                    this.functionArguments.elementAt(i).setObjectContext(this.context);
                    sb.append(this.context.getEquivalent(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
                }
                else {
                    sb.append(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
                }
                sb.append("THEN ");
                if (this.context != null) {
                    ++i;
                    this.functionArguments.elementAt(i).setObjectContext(this.context);
                    sb.append(this.context.getEquivalent(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
                }
                else {
                    sb.append(this.functionArguments.elementAt(++i).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
                }
            }
        }
        else {
            int i;
            for (i = 1; i < this.functionArguments.size() - 1; ++i) {
                sb.append("\n");
                for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                    sb.append("\t");
                }
                sb.append("WHEN ");
                if (SwisSQLOptions.caseWithEqualForDecode) {
                    if (this.context != null) {
                        this.functionArguments.elementAt(0).setObjectContext(this.context);
                        sb.append(this.context.getEquivalent(this.functionArguments.elementAt(0).toSybaseSelect(to_sqs, from_sqs).toString()) + " = ");
                    }
                    else {
                        sb.append(this.functionArguments.elementAt(0).toSybaseSelect(to_sqs, from_sqs).toString() + " = ");
                    }
                }
                if (this.context != null) {
                    this.functionArguments.elementAt(i).setObjectContext(this.context);
                    sb.append(this.context.getEquivalent(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
                }
                else {
                    sb.append(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
                }
                sb.append("THEN ");
                if (this.context != null) {
                    ++i;
                    this.functionArguments.elementAt(i).setObjectContext(this.context);
                    sb.append(this.context.getEquivalent(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
                }
                else {
                    sb.append(this.functionArguments.elementAt(++i).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
                }
            }
            sb.append("\n");
            for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                sb.append("\t");
            }
            sb.append("ELSE ");
            if (this.context != null) {
                this.functionArguments.elementAt(i).setObjectContext(this.context);
                sb.append(this.context.getEquivalent(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
            }
            else {
                sb.append(this.functionArguments.elementAt(i).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
            }
        }
        --SelectQueryStatement.beautyTabCount;
        sb.append("\n");
        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
            sb.append("\t");
        }
        sb.append("END");
        SelectQueryStatement.beautyTabCount -= 2;
        this.CaseString = sb.toString();
        this.functionName = null;
        this.argumentQualifier = null;
        this.functionArguments = null;
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final StringBuffer sb = new StringBuffer();
        String caseDataType = null;
        boolean requiresSearchedCaseStatement = false;
        final ArrayList positionOfNull = new ArrayList();
        SelectQueryStatement.beautyTabCount += 2;
        sb.append("\n");
        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
            sb.append("\t");
        }
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i).toString().trim().equalsIgnoreCase("NULL")) {
                this.functionArguments.elementAt(i).setInsideDecodeFunction(true);
            }
        }
        for (int i = 1; i < this.functionArguments.size() - 1; ++i) {
            if (this.functionArguments.get(i) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.get(i);
                final Vector columnList = sc.getColumnExpression();
                if (columnList != null) {
                    if (columnList.size() == 1 && columnList.get(0) instanceof String && columnList.get(0).trim().equalsIgnoreCase("NULL")) {
                        requiresSearchedCaseStatement = true;
                        positionOfNull.add(i + "");
                    }
                    else if (columnList.size() == 1 && columnList.get(0) instanceof String && columnList.get(0).trim().equalsIgnoreCase("''")) {
                        requiresSearchedCaseStatement = true;
                        columnList.set(0, " NULL");
                        positionOfNull.add(i + "");
                    }
                }
            }
        }
        sb.append("CASE ");
        if (!requiresSearchedCaseStatement) {
            final String str = this.functionArguments.elementAt(0).toDB2Select(to_sqs, from_sqs).toString();
            if (SwisSQLAPI.variableDatatypeMapping != null) {
                caseDataType = CastingUtil.getDataType(SwisSQLAPI.variableDatatypeMapping.get(str));
            }
            sb.append(str + " ");
        }
        ++SelectQueryStatement.beautyTabCount;
        if ((this.functionArguments.size() & 0x1) != 0x0) {
            for (int i = 1; i < this.functionArguments.size(); ++i) {
                sb.append("\n");
                for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                    sb.append("\t");
                }
                sb.append("WHEN ");
                if (requiresSearchedCaseStatement) {
                    sb.append(this.functionArguments.elementAt(0).toDB2Select(to_sqs, from_sqs).toString());
                    if (positionOfNull.contains(i + "")) {
                        sb.append(" IS ");
                    }
                    else {
                        sb.append(" = ");
                    }
                }
                String str2 = this.functionArguments.elementAt(i).toDB2Select(to_sqs, from_sqs).toString();
                String sourceDataType = null;
                if (SwisSQLAPI.variableDatatypeMapping != null) {
                    sourceDataType = CastingUtil.getDataType((String)CastingUtil.getValueIgnoreCase(SwisSQLAPI.variableDatatypeMapping, str2));
                }
                sb.append(CastingUtil.getDB2DataTypeCastedString(sourceDataType, caseDataType, str2) + " ");
                sb.append("THEN ");
                this.functionArguments.elementAt(++i).setInArithmeticExpression(this.inArithmeticExpr);
                this.functionArguments.elementAt(i).setTargetDataType(this.dataType);
                str2 = this.functionArguments.elementAt(i).toDB2Select(to_sqs, from_sqs).toString();
                sb.append(str2 + " ");
            }
        }
        else {
            int i;
            for (i = 1; i < this.functionArguments.size() - 1; ++i) {
                sb.append("\n");
                for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                    sb.append("\t");
                }
                sb.append("WHEN ");
                if (requiresSearchedCaseStatement) {
                    String sourceDataType2 = null;
                    final String str3 = this.functionArguments.elementAt(0).toDB2Select(to_sqs, from_sqs).toString();
                    if (SwisSQLAPI.variableDatatypeMapping != null) {
                        sourceDataType2 = CastingUtil.getDataType((String)CastingUtil.getValueIgnoreCase(SwisSQLAPI.variableDatatypeMapping, str3));
                    }
                    sb.append(CastingUtil.getDB2DataTypeCastedString(sourceDataType2, caseDataType, str3) + " ");
                    if (positionOfNull.contains(i + "")) {
                        sb.append(" IS ");
                    }
                    else {
                        sb.append(" = ");
                    }
                }
                String sourceDataType2 = null;
                String str3 = this.functionArguments.elementAt(i).toDB2Select(to_sqs, from_sqs).toString();
                if (SwisSQLAPI.variableDatatypeMapping != null) {
                    sourceDataType2 = CastingUtil.getDataType((String)CastingUtil.getValueIgnoreCase(SwisSQLAPI.variableDatatypeMapping, str3));
                }
                sb.append(CastingUtil.getDB2DataTypeCastedString(sourceDataType2, caseDataType, str3) + " ");
                sb.append("THEN ");
                this.functionArguments.elementAt(++i).setInArithmeticExpression(this.inArithmeticExpr);
                this.functionArguments.elementAt(i).setTargetDataType(this.dataType);
                str3 = this.functionArguments.elementAt(i).toDB2Select(to_sqs, from_sqs).toString();
                sb.append(str3 + " ");
            }
            sb.append("\n");
            for (int k = 0; k < SelectQueryStatement.beautyTabCount; ++k) {
                sb.append("\t");
            }
            sb.append("ELSE ");
            this.functionArguments.elementAt(i).setInArithmeticExpression(this.inArithmeticExpr);
            this.functionArguments.elementAt(i).setTargetDataType(this.dataType);
            final String str2 = this.functionArguments.elementAt(i).toDB2Select(to_sqs, from_sqs).toString();
            sb.append(str2 + " ");
        }
        --SelectQueryStatement.beautyTabCount;
        sb.append("\n");
        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
            sb.append("\t");
        }
        sb.append("END");
        SelectQueryStatement.beautyTabCount -= 2;
        this.CaseString = sb.toString();
        this.functionName = null;
        this.argumentQualifier = null;
        this.functionArguments = null;
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final StringBuffer sb = new StringBuffer();
        sb.append("CASE ");
        sb.append(this.functionArguments.elementAt(0).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
        if ((this.functionArguments.size() & 0x1) != 0x0) {
            for (int i = 1; i < this.functionArguments.size(); ++i) {
                sb.append("WHEN ");
                sb.append(this.functionArguments.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
                sb.append("THEN ");
                sb.append(this.functionArguments.elementAt(++i).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
            }
        }
        else {
            int i;
            for (i = 1; i < this.functionArguments.size() - 1; ++i) {
                sb.append("WHEN ");
                sb.append(this.functionArguments.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
                sb.append("THEN ");
                sb.append(this.functionArguments.elementAt(++i).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
            }
            sb.append("ELSE ");
            sb.append(this.functionArguments.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
        }
        sb.append("END");
        this.CaseString = sb.toString();
        this.functionName = null;
        this.argumentQualifier = null;
        this.functionArguments = null;
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.decodeConvertedToCaseStatement = true;
        this.functionName.setColumnName(null);
        Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        if (this.functionArguments.size() > 2) {
            arguments = new Vector();
            final CaseStatement caseStmt = new CaseStatement();
            final WhereExpression caseCondition = new WhereExpression();
            final Vector whereItemsVector = new Vector();
            final WhereItem caseConditionWhereItems = new WhereItem();
            final WhereColumn caseLeftWC = new WhereColumn();
            final Vector caseColExp = new Vector();
            caseStmt.setCaseClause("CASE");
            caseStmt.setElseClause("ELSE");
            caseStmt.setEndClause("END");
            caseColExp.add(this.functionArguments.elementAt(0).toMySQLSelect(to_sqs, from_sqs).toString());
            caseLeftWC.setColumnExpression(caseColExp);
            caseConditionWhereItems.setLeftWhereExp(caseLeftWC);
            whereItemsVector.add(caseConditionWhereItems);
            caseCondition.setWhereItem(whereItemsVector);
            caseStmt.setCaseCondition(caseCondition);
            final Vector whenStmtList = new Vector();
            for (int i = 1; i < this.functionArguments.size() - 1; ++i) {
                final WhenStatement whenStmt = new WhenStatement();
                final WhereExpression whenConditionWE = new WhereExpression();
                final Vector whenConditionVector = new Vector();
                final WhereItem whenWhereItem = new WhereItem();
                final WhereColumn whenLeftWC = new WhereColumn();
                final Vector whenColExp = new Vector();
                final SelectColumn thenStmt = new SelectColumn();
                final Vector thenColExp = new Vector();
                whenStmt.setWhenClause("WHEN");
                whenColExp.add(this.functionArguments.elementAt(i).toMySQLSelect(to_sqs, from_sqs).toString());
                whenStmt.setThenClause("THEN");
                thenColExp.add(this.functionArguments.elementAt(++i).toMySQLSelect(to_sqs, from_sqs).toString());
                whenLeftWC.setColumnExpression(whenColExp);
                whenWhereItem.setLeftWhereExp(whenLeftWC);
                whenConditionVector.add(whenWhereItem);
                whenConditionWE.setWhereItem(whenConditionVector);
                whenStmt.setWhenCondition(whenConditionWE);
                thenStmt.setColumnExpression(thenColExp);
                whenStmt.setThenStatement(thenStmt);
                whenStmtList.add(whenStmt);
            }
            final SelectColumn elseStmt = new SelectColumn();
            final Vector elseColExp = new Vector();
            elseColExp.add(this.functionArguments.elementAt(this.functionArguments.size() - 1).toMySQLSelect(to_sqs, from_sqs).toString() + " ");
            elseStmt.setColumnExpression(elseColExp);
            caseStmt.setElseStatement(elseStmt);
            caseStmt.setWhenStatementList(whenStmtList);
            final SelectColumn argument = new SelectColumn();
            final Vector colExp = new Vector();
            colExp.add(caseStmt);
            argument.setColumnExpression(colExp);
            arguments.add(argument);
            this.setFunctionArguments(arguments);
            this.functionName = null;
            this.argumentQualifier = null;
            this.setOpenBracesForFunctionNameRequired(false);
        }
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nDECODE() function is not supported in TimesTen 5.1.21\n");
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.CaseString = null;
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
        boolean requiresSearchedCaseStatement = false;
        final ArrayList positionOfNull = new ArrayList();
        for (int i = 1; i < this.functionArguments.size() - 1; ++i) {
            if (this.functionArguments.get(i) instanceof SelectColumn) {
                final SelectColumn sc = this.functionArguments.get(i);
                final Vector columnList = sc.getColumnExpression();
                if (columnList != null) {
                    if (columnList.size() == 1 && columnList.get(0) instanceof String && columnList.get(0).trim().equalsIgnoreCase("NULL")) {
                        requiresSearchedCaseStatement = true;
                        positionOfNull.add(i + "");
                    }
                    else if (columnList.size() == 1 && columnList.get(0) instanceof String && columnList.get(0).trim().equalsIgnoreCase("''")) {
                        requiresSearchedCaseStatement = true;
                        columnList.set(0, "NULL");
                        positionOfNull.add(i + "");
                    }
                }
            }
        }
        (this.caseStatement = new CaseStatement()).setCaseClause("CASE");
        if (!requiresSearchedCaseStatement) {
            final WhereItem wi = new WhereItem();
            final WhereColumn wc = new WhereColumn();
            final Vector wcColExp = new Vector();
            wcColExp.add(this.functionArguments.elementAt(0).toTeradataSelect(to_sqs, from_sqs));
            wc.setColumnExpression(wcColExp);
            wi.setLeftWhereExp(wc);
            final WhereExpression we = new WhereExpression();
            we.addWhereItem(wi);
            this.caseStatement.setCaseCondition(we);
        }
        if ((this.functionArguments.size() & 0x1) != 0x0) {
            final Vector whenStmtList = new Vector();
            for (int j = 1; j < this.functionArguments.size(); ++j) {
                final WhenStatement when_statement = new WhenStatement();
                when_statement.setWhenClause("WHEN");
                when_statement.setThenClause("THEN");
                if (requiresSearchedCaseStatement) {
                    final WhereItem wi2 = new WhereItem();
                    final WhereColumn wc2 = new WhereColumn();
                    final Vector wcColExp2 = new Vector();
                    wcColExp2.add(this.functionArguments.elementAt(0).toTeradataSelect(to_sqs, from_sqs));
                    wc2.setColumnExpression(wcColExp2);
                    wi2.setLeftWhereExp(wc2);
                    if (positionOfNull.contains(j + "")) {
                        wi2.setOperator("IS");
                    }
                    else {
                        wi2.setOperator("=");
                    }
                    final WhereColumn rwc = new WhereColumn();
                    final Vector rwcColExp = new Vector();
                    rwcColExp.add(this.functionArguments.elementAt(j).toTeradataSelect(to_sqs, from_sqs));
                    rwc.setColumnExpression(rwcColExp);
                    wi2.setRightWhereExp(rwc);
                    final WhereExpression we2 = new WhereExpression();
                    we2.addWhereItem(wi2);
                    when_statement.setWhenCondition(we2);
                }
                else {
                    final WhereItem wi2 = new WhereItem();
                    final WhereColumn wc2 = new WhereColumn();
                    final Vector wcColExp2 = new Vector();
                    wcColExp2.add(this.functionArguments.elementAt(j).toTeradataSelect(to_sqs, from_sqs));
                    wc2.setColumnExpression(wcColExp2);
                    wi2.setLeftWhereExp(wc2);
                    final WhereExpression we3 = new WhereExpression();
                    we3.addWhereItem(wi2);
                    when_statement.setWhenCondition(we3);
                }
                final SelectColumn thenStmtColumn = this.functionArguments.elementAt(++j);
                when_statement.setThenStatement(thenStmtColumn.toTeradataSelect(to_sqs, from_sqs));
                whenStmtList.add(when_statement);
            }
            this.caseStatement.setWhenStatementList(whenStmtList);
        }
        else {
            final Vector whenStmtList = new Vector();
            int j;
            for (j = 1; j < this.functionArguments.size() - 1; ++j) {
                final WhenStatement when_statement = new WhenStatement();
                when_statement.setWhenClause("WHEN");
                when_statement.setThenClause("THEN");
                if (requiresSearchedCaseStatement) {
                    final WhereItem wi2 = new WhereItem();
                    final WhereColumn wc2 = new WhereColumn();
                    final Vector wcColExp2 = new Vector();
                    wcColExp2.add(this.functionArguments.elementAt(0).toTeradataSelect(to_sqs, from_sqs));
                    wc2.setColumnExpression(wcColExp2);
                    wi2.setLeftWhereExp(wc2);
                    if (positionOfNull.contains(j + "")) {
                        wi2.setOperator("IS");
                    }
                    else {
                        wi2.setOperator("=");
                    }
                    final WhereColumn rwc = new WhereColumn();
                    final Vector rwcColExp = new Vector();
                    rwcColExp.add(this.functionArguments.elementAt(j).toTeradataSelect(to_sqs, from_sqs));
                    rwc.setColumnExpression(rwcColExp);
                    wi2.setRightWhereExp(rwc);
                    final WhereExpression we2 = new WhereExpression();
                    we2.addWhereItem(wi2);
                    when_statement.setWhenCondition(we2);
                }
                else {
                    final WhereItem wi2 = new WhereItem();
                    final WhereColumn wc2 = new WhereColumn();
                    final Vector wcColExp2 = new Vector();
                    wcColExp2.add(this.functionArguments.elementAt(j).toTeradataSelect(to_sqs, from_sqs));
                    wc2.setColumnExpression(wcColExp2);
                    wi2.setLeftWhereExp(wc2);
                    final WhereExpression we3 = new WhereExpression();
                    we3.addWhereItem(wi2);
                    when_statement.setWhenCondition(we3);
                }
                when_statement.setThenStatement(this.functionArguments.elementAt(++j).toTeradataSelect(to_sqs, from_sqs));
                whenStmtList.add(when_statement);
            }
            this.caseStatement.setWhenStatementList(whenStmtList);
            this.caseStatement.setElseClause("ELSE");
            this.caseStatement.setElseStatement(this.functionArguments.elementAt(j).toTeradataSelect(to_sqs, from_sqs));
        }
        boolean isDatePresent = false;
        int dateWhenStmtIdx = -1;
        for (int k = 0; k < this.caseStatement.getWhenClauseList().size(); ++k) {
            final SelectColumn sc2 = this.caseStatement.getWhenClauseList().elementAt(k).getThenStatement();
            for (int n = 0; n < sc2.getColumnExpression().size(); ++n) {
                final Object obj = sc2.getColumnExpression().get(n);
                if (obj instanceof FunctionCalls) {
                    final FunctionCalls fcObj = (FunctionCalls)obj;
                    if (fcObj.getFunctionName() != null) {
                        final String fnName = fcObj.getFunctionName().getColumnName();
                        if (SwisSQLUtils.getFunctionReturnType(fnName, fcObj.getFunctionArguments()).equalsIgnoreCase("date")) {
                            isDatePresent = true;
                            dateWhenStmtIdx = k;
                        }
                    }
                }
            }
        }
        for (int l = 0; l < this.caseStatement.getWhenClauseList().size(); ++l) {
            final WhenStatement convertedWhenStmt = this.caseStatement.getWhenClauseList().elementAt(l);
            if (isDatePresent) {
                final FunctionCalls caseFunc = new FunctionCalls();
                final TableColumn fnName2 = new TableColumn();
                fnName2.setColumnName("CAST");
                caseFunc.setFunctionName(fnName2);
                final Vector fnArgs = new Vector();
                fnArgs.add(convertedWhenStmt.getThenStatement());
                caseFunc.setAsDatatype("AS");
                final DateClass timestamp = new DateClass();
                timestamp.setDatatypeName("TIMESTAMP");
                timestamp.setSize("0");
                timestamp.setOpenBrace("(");
                timestamp.setClosedBrace(")");
                fnArgs.add(timestamp);
                caseFunc.setFunctionArguments(fnArgs);
                final SelectColumn newSelCol = new SelectColumn();
                final Vector colExp = new Vector();
                colExp.add(caseFunc);
                newSelCol.setColumnExpression(colExp);
                convertedWhenStmt.setThenStatement(newSelCol);
            }
        }
        if (this.caseStatement.getElseStatement() != null) {
            final SelectColumn convertedElseStatement = this.caseStatement.getElseStatement();
            if (isDatePresent) {
                final FunctionCalls caseFunc2 = new FunctionCalls();
                final TableColumn fnName3 = new TableColumn();
                fnName3.setColumnName("CAST");
                caseFunc2.setFunctionName(fnName3);
                final Vector fnArgs2 = new Vector();
                fnArgs2.add(convertedElseStatement);
                caseFunc2.setAsDatatype("AS");
                final DateClass timestamp2 = new DateClass();
                timestamp2.setDatatypeName("TIMESTAMP");
                timestamp2.setSize("0");
                timestamp2.setOpenBrace("(");
                timestamp2.setClosedBrace(")");
                fnArgs2.add(timestamp2);
                caseFunc2.setFunctionArguments(fnArgs2);
                final SelectColumn newSelCol2 = new SelectColumn();
                final Vector colExp2 = new Vector();
                colExp2.add(caseFunc2);
                newSelCol2.setColumnExpression(colExp2);
                this.caseStatement.setElseStatement(newSelCol2);
            }
        }
        this.caseStatement.setEndClause("END");
        this.CaseString = null;
        this.functionName = null;
        this.argumentQualifier = null;
        this.functionArguments = null;
    }
    
    @Override
    public String toString() {
        if (this.CaseString != null) {
            return this.CaseString;
        }
        if (this.caseStatement != null) {
            return this.caseStatement.toString();
        }
        if (this.functionName != null) {
            this.functionName.setColumnName("DECODE");
        }
        return super.toString();
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
    }
}
