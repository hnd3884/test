package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class initcap extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("INITCAP");
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
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        final SelectColumn arg = new SelectColumn();
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg = new Vector();
        final Vector colExpArg2 = new Vector();
        final FunctionCalls length = new FunctionCalls();
        final FunctionCalls substringForLcase = new FunctionCalls();
        final FunctionCalls substringForUcase = new FunctionCalls();
        final FunctionCalls lowerCase = new FunctionCalls();
        final FunctionCalls upperCase = new FunctionCalls();
        final TableColumn lengthFunction = new TableColumn();
        final TableColumn substrFunctionForLcase = new TableColumn();
        final TableColumn substrFunctionForUcase = new TableColumn();
        final TableColumn lcaseFunction = new TableColumn();
        final TableColumn ucaseFunction = new TableColumn();
        lengthFunction.setColumnName("DATALENGTH");
        length.setFunctionName(lengthFunction);
        substrFunctionForLcase.setColumnName("SUBSTRING");
        substringForLcase.setFunctionName(substrFunctionForLcase);
        substringForUcase.setFunctionName(substrFunctionForLcase);
        lcaseFunction.setColumnName("LOWER");
        ucaseFunction.setColumnName("UPPER");
        lowerCase.setFunctionName(lcaseFunction);
        upperCase.setFunctionName(ucaseFunction);
        final Vector lengthArgument = new Vector();
        final Vector substrArgumentForLcase = new Vector();
        final Vector substrArgumentForUcase = new Vector();
        final Vector lcaseArgument = new Vector();
        final Vector ucaseArgument = new Vector();
        final Vector dummyArgument = new Vector();
        lengthArgument.addElement(this.functionArguments.get(0));
        length.setFunctionArguments(lengthArgument);
        colExpArg.addElement(length);
        colExpArg.addElement("-1");
        arg.setColumnExpression(colExpArg);
        substrArgumentForLcase.addElement(this.functionArguments.get(0));
        substrArgumentForLcase.addElement("2");
        substrArgumentForLcase.addElement(arg);
        substringForLcase.setFunctionArguments(substrArgumentForLcase);
        lcaseArgument.addElement(substringForLcase);
        lowerCase.setFunctionArguments(lcaseArgument);
        substrArgumentForUcase.addElement(this.functionArguments.get(0));
        substrArgumentForUcase.addElement("1");
        substrArgumentForUcase.addElement("1");
        substringForUcase.setFunctionArguments(substrArgumentForUcase);
        ucaseArgument.addElement(substringForUcase);
        upperCase.setFunctionArguments(ucaseArgument);
        colExpArg2.addElement(upperCase);
        colExpArg2.addElement("+");
        colExpArg2.addElement(lowerCase);
        arg2.setColumnExpression(colExpArg2);
        dummyArgument.addElement(arg2);
        this.setFunctionArguments(dummyArgument);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        final SelectColumn arg = new SelectColumn();
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg = new Vector();
        final Vector colExpArg2 = new Vector();
        final FunctionCalls length = new FunctionCalls();
        final FunctionCalls substringForLcase = new FunctionCalls();
        final FunctionCalls substringForUcase = new FunctionCalls();
        final FunctionCalls lowerCase = new FunctionCalls();
        final FunctionCalls upperCase = new FunctionCalls();
        final TableColumn lengthFunction = new TableColumn();
        final TableColumn substrFunctionForLcase = new TableColumn();
        final TableColumn substrFunctionForUcase = new TableColumn();
        final TableColumn lcaseFunction = new TableColumn();
        final TableColumn ucaseFunction = new TableColumn();
        lengthFunction.setColumnName("DATALENGTH");
        length.setFunctionName(lengthFunction);
        substrFunctionForLcase.setColumnName("SUBSTRING");
        substringForLcase.setFunctionName(substrFunctionForLcase);
        substringForUcase.setFunctionName(substrFunctionForLcase);
        lcaseFunction.setColumnName("LOWER");
        ucaseFunction.setColumnName("UPPER");
        lowerCase.setFunctionName(lcaseFunction);
        upperCase.setFunctionName(ucaseFunction);
        final Vector lengthArgument = new Vector();
        final Vector substrArgumentForLcase = new Vector();
        final Vector substrArgumentForUcase = new Vector();
        final Vector lcaseArgument = new Vector();
        final Vector ucaseArgument = new Vector();
        final Vector dummyArgument = new Vector();
        lengthArgument.addElement(this.functionArguments.get(0));
        length.setFunctionArguments(lengthArgument);
        colExpArg.addElement(length);
        colExpArg.addElement("-1");
        arg.setColumnExpression(colExpArg);
        substrArgumentForLcase.addElement(this.functionArguments.get(0));
        substrArgumentForLcase.addElement("2");
        substrArgumentForLcase.addElement(arg);
        substringForLcase.setFunctionArguments(substrArgumentForLcase);
        lcaseArgument.addElement(substringForLcase);
        lowerCase.setFunctionArguments(lcaseArgument);
        substrArgumentForUcase.addElement(this.functionArguments.get(0));
        substrArgumentForUcase.addElement("1");
        substrArgumentForUcase.addElement("1");
        substringForUcase.setFunctionArguments(substrArgumentForUcase);
        ucaseArgument.addElement(substringForUcase);
        upperCase.setFunctionArguments(ucaseArgument);
        colExpArg2.addElement(upperCase);
        colExpArg2.addElement("+");
        colExpArg2.addElement(lowerCase);
        arg2.setColumnExpression(colExpArg2);
        dummyArgument.addElement(arg2);
        this.setFunctionArguments(dummyArgument);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONCAT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final SelectColumn arg = new SelectColumn();
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg = new Vector();
        final Vector colExpArg2 = new Vector();
        final FunctionCalls length = new FunctionCalls();
        final FunctionCalls substringForLcase = new FunctionCalls();
        final FunctionCalls substringForUcase = new FunctionCalls();
        final FunctionCalls lowerCase = new FunctionCalls();
        final FunctionCalls upperCase = new FunctionCalls();
        final TableColumn lengthFunction = new TableColumn();
        final TableColumn substrFunctionForLcase = new TableColumn();
        final TableColumn substrFunctionForUcase = new TableColumn();
        final TableColumn lcaseFunction = new TableColumn();
        final TableColumn ucaseFunction = new TableColumn();
        lengthFunction.setColumnName("LENGTH");
        length.setFunctionName(lengthFunction);
        substrFunctionForLcase.setColumnName("SUBSTR");
        substringForLcase.setFunctionName(substrFunctionForLcase);
        substringForUcase.setFunctionName(substrFunctionForLcase);
        lcaseFunction.setColumnName("LCASE");
        ucaseFunction.setColumnName("UCASE");
        lowerCase.setFunctionName(lcaseFunction);
        upperCase.setFunctionName(ucaseFunction);
        final Vector lengthArgument = new Vector();
        final Vector substrArgumentForLcase = new Vector();
        final Vector substrArgumentForUcase = new Vector();
        final Vector lcaseArgument = new Vector();
        final Vector ucaseArgument = new Vector();
        lengthArgument.addElement(this.functionArguments.get(0));
        length.setFunctionArguments(lengthArgument);
        colExpArg.addElement(length);
        colExpArg.addElement("-1");
        arg.setColumnExpression(colExpArg);
        substrArgumentForLcase.addElement(this.functionArguments.get(0));
        substrArgumentForLcase.addElement("2");
        substrArgumentForLcase.addElement(arg);
        substringForLcase.setFunctionArguments(substrArgumentForLcase);
        lcaseArgument.addElement(substringForLcase);
        lowerCase.setFunctionArguments(lcaseArgument);
        substrArgumentForUcase.addElement(this.functionArguments.get(0));
        substrArgumentForUcase.addElement("1");
        substrArgumentForUcase.addElement("1");
        substringForUcase.setFunctionArguments(substrArgumentForUcase);
        ucaseArgument.addElement(substringForUcase);
        upperCase.setFunctionArguments(ucaseArgument);
        colExpArg2.addElement(upperCase);
        colExpArg2.addElement(lowerCase);
        this.setFunctionArguments(colExpArg2);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("INITCAP");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONCAT");
        final Vector arguments1 = new Vector();
        final Vector arguments2 = new Vector();
        final Vector arguments3 = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments1.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                arguments2.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                arguments3.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments1.addElement(this.functionArguments.elementAt(i_count));
                arguments2.addElement(this.functionArguments.elementAt(i_count));
                arguments3.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Vector concatArguments = new Vector();
        final SelectColumn sc_upperCase = new SelectColumn();
        final FunctionCalls fn_upperCase = new FunctionCalls();
        final TableColumn tb_upperCase = new TableColumn();
        tb_upperCase.setColumnName("UPPER");
        fn_upperCase.setFunctionName(tb_upperCase);
        final Vector vc_upperCaseIn = new Vector();
        final Vector vc_upperCaseOut = new Vector();
        final SelectColumn sc_subStrFirstLetter = new SelectColumn();
        final FunctionCalls fn_subStrFirstLetter = new FunctionCalls();
        final TableColumn tb_subStrFirstLetter = new TableColumn();
        tb_subStrFirstLetter.setColumnName("SUBSTRING");
        fn_subStrFirstLetter.setFunctionName(tb_subStrFirstLetter);
        final Vector vc_subStrFirstLetterIn = new Vector();
        final Vector vc_subStrFirstLetterOut = new Vector();
        vc_subStrFirstLetterIn.addElement(arguments1.get(0));
        vc_subStrFirstLetterIn.addElement("1");
        vc_subStrFirstLetterIn.addElement("1");
        fn_subStrFirstLetter.setFunctionArguments(vc_subStrFirstLetterIn);
        vc_subStrFirstLetterOut.addElement(fn_subStrFirstLetter);
        sc_subStrFirstLetter.setColumnExpression(vc_subStrFirstLetterOut);
        vc_upperCaseIn.addElement(sc_subStrFirstLetter);
        fn_upperCase.setFunctionArguments(vc_upperCaseIn);
        vc_upperCaseOut.addElement(fn_upperCase);
        sc_upperCase.setColumnExpression(vc_upperCaseOut);
        final SelectColumn sc_lowerCase = new SelectColumn();
        final FunctionCalls fn_lowerCase = new FunctionCalls();
        final TableColumn tb_lowerCase = new TableColumn();
        tb_lowerCase.setColumnName("LOWER");
        fn_lowerCase.setFunctionName(tb_lowerCase);
        final Vector vc_lowerCaseIn = new Vector();
        final Vector vc_lowerCaseOut = new Vector();
        final SelectColumn sc_subStrRemainingLetters = new SelectColumn();
        final FunctionCalls fn_subStrRemainingLetters = new FunctionCalls();
        final TableColumn tb_subStrRemainingLetters = new TableColumn();
        tb_subStrRemainingLetters.setColumnName("SUBSTRING");
        fn_subStrRemainingLetters.setFunctionName(tb_subStrRemainingLetters);
        final Vector vc_subStrRemainingLettersIn = new Vector();
        final Vector vc_subStrRemainingLettersOut = new Vector();
        vc_subStrRemainingLettersIn.addElement(arguments2.get(0));
        vc_subStrRemainingLettersIn.addElement("2");
        final SelectColumn sc_endIndexForRemainingLetters = new SelectColumn();
        final Vector vc_endIndexForRemainingLetters = new Vector();
        final SelectColumn sc_lenArgForLowerCase = new SelectColumn();
        final FunctionCalls fn_lenArgForLowerCase = new FunctionCalls();
        final TableColumn tb_lenArgForLowerCase = new TableColumn();
        tb_lenArgForLowerCase.setColumnName("LENGTH");
        fn_lenArgForLowerCase.setFunctionName(tb_lenArgForLowerCase);
        final Vector vc_lenArgForLowerCaseIn = new Vector();
        final Vector vc_lenArgForLowerCaseOut = new Vector();
        vc_lenArgForLowerCaseIn.addElement(arguments3.get(0));
        fn_lenArgForLowerCase.setFunctionArguments(vc_lenArgForLowerCaseIn);
        vc_lenArgForLowerCaseOut.addElement(fn_lenArgForLowerCase);
        sc_lenArgForLowerCase.setColumnExpression(vc_lenArgForLowerCaseOut);
        vc_endIndexForRemainingLetters.addElement(sc_lenArgForLowerCase);
        vc_endIndexForRemainingLetters.addElement("-");
        vc_endIndexForRemainingLetters.addElement("1");
        sc_endIndexForRemainingLetters.setOpenBrace("(");
        sc_endIndexForRemainingLetters.setCloseBrace(")");
        sc_endIndexForRemainingLetters.setColumnExpression(vc_endIndexForRemainingLetters);
        vc_subStrRemainingLettersIn.addElement(sc_endIndexForRemainingLetters);
        fn_subStrRemainingLetters.setFunctionArguments(vc_subStrRemainingLettersIn);
        vc_subStrRemainingLettersOut.addElement(fn_subStrRemainingLetters);
        sc_subStrRemainingLetters.setColumnExpression(vc_subStrRemainingLettersOut);
        vc_lowerCaseIn.addElement(sc_subStrRemainingLetters);
        fn_lowerCase.setFunctionArguments(vc_lowerCaseIn);
        vc_lowerCaseOut.addElement(fn_lowerCase);
        sc_lowerCase.setColumnExpression(vc_lowerCaseOut);
        concatArguments.addElement(sc_upperCase);
        concatArguments.addElement(sc_lowerCase);
        this.setFunctionArguments(concatArguments);
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
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
        final FunctionCalls length = new FunctionCalls();
        final TableColumn lengthFunction = new TableColumn();
        lengthFunction.setColumnName("CHARACTER_LENGTH");
        length.setFunctionName(lengthFunction);
        final Vector lengthArgument = new Vector();
        lengthArgument.addElement(this.functionArguments.get(0));
        length.setFunctionArguments(lengthArgument);
        final SelectColumn arg = new SelectColumn();
        final Vector colExpArg = new Vector();
        colExpArg.addElement(length);
        colExpArg.addElement("-1");
        arg.setColumnExpression(colExpArg);
        final FunctionCalls substringForLcase = new FunctionCalls();
        final TableColumn substrFunctionForLcase = new TableColumn();
        substrFunctionForLcase.setColumnName("SUBSTRING");
        substringForLcase.setFunctionName(substrFunctionForLcase);
        final Vector substrArgumentForLcase = new Vector();
        substrArgumentForLcase.addElement(this.functionArguments.get(0));
        substrArgumentForLcase.addElement(" FROM ");
        substrArgumentForLcase.addElement("2");
        substrArgumentForLcase.addElement(" FOR ");
        substrArgumentForLcase.addElement(arg);
        substringForLcase.setFunctionArguments(substrArgumentForLcase);
        substringForLcase.setStripComma(true);
        final FunctionCalls substringForUcase = new FunctionCalls();
        substringForUcase.setFunctionName(substrFunctionForLcase);
        final Vector substrArgumentForUcase = new Vector();
        substrArgumentForUcase.addElement(this.functionArguments.get(0));
        substrArgumentForUcase.addElement(" FROM ");
        substrArgumentForUcase.addElement("1");
        substrArgumentForUcase.addElement(" FOR ");
        substrArgumentForUcase.addElement("1");
        substringForUcase.setFunctionArguments(substrArgumentForUcase);
        substringForUcase.setStripComma(true);
        final FunctionCalls lowerCase = new FunctionCalls();
        final TableColumn lcaseFunction = new TableColumn();
        lcaseFunction.setColumnName("LOWER");
        lowerCase.setFunctionName(lcaseFunction);
        final Vector lcaseArgument = new Vector();
        lcaseArgument.addElement(substringForLcase);
        lowerCase.setFunctionArguments(lcaseArgument);
        final FunctionCalls upperCase = new FunctionCalls();
        final TableColumn ucaseFunction = new TableColumn();
        ucaseFunction.setColumnName("UPPER");
        upperCase.setFunctionName(ucaseFunction);
        final Vector ucaseArgument = new Vector();
        ucaseArgument.addElement(substringForUcase);
        upperCase.setFunctionArguments(ucaseArgument);
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg2 = new Vector();
        colExpArg2.addElement(upperCase);
        colExpArg2.addElement("||");
        colExpArg2.addElement(lowerCase);
        arg2.setColumnExpression(colExpArg2);
        final Vector dummyArgument = new Vector();
        dummyArgument.addElement(arg2);
        this.setFunctionArguments(dummyArgument);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
        final SelectColumn arg = new SelectColumn();
        final SelectColumn arg2 = new SelectColumn();
        final Vector colExpArg = new Vector();
        final Vector colExpArg2 = new Vector();
        final FunctionCalls length = new FunctionCalls();
        final FunctionCalls substringForLcase = new FunctionCalls();
        final FunctionCalls substringForUcase = new FunctionCalls();
        final FunctionCalls lowerCase = new FunctionCalls();
        final FunctionCalls upperCase = new FunctionCalls();
        final TableColumn lengthFunction = new TableColumn();
        final TableColumn substrFunctionForLcase = new TableColumn();
        final TableColumn substrFunctionForUcase = new TableColumn();
        final TableColumn lcaseFunction = new TableColumn();
        final TableColumn ucaseFunction = new TableColumn();
        lengthFunction.setColumnName("CHARACTER_LENGTH");
        length.setFunctionName(lengthFunction);
        substrFunctionForLcase.setColumnName("SUBSTR");
        substringForLcase.setFunctionName(substrFunctionForLcase);
        substringForUcase.setFunctionName(substrFunctionForLcase);
        lcaseFunction.setColumnName("LOWER");
        ucaseFunction.setColumnName("UPPER");
        lowerCase.setFunctionName(lcaseFunction);
        upperCase.setFunctionName(ucaseFunction);
        final Vector lengthArgument = new Vector();
        final Vector substrArgumentForLcase = new Vector();
        final Vector substrArgumentForUcase = new Vector();
        final Vector lcaseArgument = new Vector();
        final Vector ucaseArgument = new Vector();
        final Vector dummyArgument = new Vector();
        lengthArgument.addElement(this.functionArguments.get(0));
        length.setFunctionArguments(lengthArgument);
        colExpArg.addElement(length);
        colExpArg.addElement("-1");
        arg.setColumnExpression(colExpArg);
        substrArgumentForLcase.addElement(this.functionArguments.get(0));
        substrArgumentForLcase.addElement("2");
        substrArgumentForLcase.addElement(arg);
        substringForLcase.setFunctionArguments(substrArgumentForLcase);
        lcaseArgument.addElement(substringForLcase);
        lowerCase.setFunctionArguments(lcaseArgument);
        substrArgumentForUcase.addElement(this.functionArguments.get(0));
        substrArgumentForUcase.addElement("1");
        substrArgumentForUcase.addElement("1");
        substringForUcase.setFunctionArguments(substrArgumentForUcase);
        ucaseArgument.addElement(substringForUcase);
        upperCase.setFunctionArguments(ucaseArgument);
        colExpArg2.addElement(upperCase);
        colExpArg2.addElement("||");
        colExpArg2.addElement(lowerCase);
        arg2.setColumnExpression(colExpArg2);
        dummyArgument.addElement(arg2);
        this.setFunctionArguments(dummyArgument);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("INITCAP");
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
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("INITCAP");
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
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("Initcap");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
}
