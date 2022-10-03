package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.create.BinClass;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;

public class convert extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        this.setAsDatatype("AS");
        final Vector arguments = new Vector();
        TableColumn tableColumn = null;
        String columnDataType = null;
        boolean isDate = false;
        boolean isChar = false;
        boolean isCharVarch = false;
        boolean isBinary = false;
        if (this.functionArguments.size() == 2) {
            Object argObj = this.functionArguments.get(1);
            if (argObj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)argObj;
                final Vector colExpr = selCol.getColumnExpression();
                if (colExpr.size() == 1) {
                    final Object obj = colExpr.get(0);
                    if (obj instanceof TableColumn) {
                        final TableColumn tc = (TableColumn)obj;
                        final String colName = tc.getColumnName();
                        String type = null;
                        if (from_sqs != null && from_sqs.getFromClause() != null) {
                            type = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                        }
                        if (type != null && type.toLowerCase().indexOf("date") != -1) {
                            isDate = true;
                        }
                        else if (type == null && SwisSQLAPI.variableDatatypeMapping != null && ((from_sqs != null && from_sqs.getFromClause() == null) || from_sqs == null) && SwisSQLAPI.variableDatatypeMapping.containsKey(colName)) {
                            final String dataType = SwisSQLAPI.variableDatatypeMapping.get(colName);
                            if (dataType.toLowerCase().indexOf("date") != -1) {
                                isDate = true;
                            }
                            else if (dataType.toLowerCase().startsWith("char") || dataType.toLowerCase().startsWith("nchar")) {
                                isChar = true;
                            }
                            else if (dataType.toLowerCase().startsWith("binary") || dataType.toLowerCase().startsWith("varbinary")) {
                                isBinary = true;
                            }
                            if (dataType.toLowerCase().startsWith("char") || dataType.toLowerCase().startsWith("varchar")) {
                                isCharVarch = true;
                            }
                        }
                        if (type != null && (type.toLowerCase().startsWith("char") || type.toLowerCase().startsWith("nchar"))) {
                            isChar = true;
                        }
                        if (type != null && (type.toLowerCase().startsWith("char") || type.toLowerCase().startsWith("varchar"))) {
                            isCharVarch = true;
                        }
                        if (type != null && (type.toLowerCase().startsWith("binary") || type.toLowerCase().startsWith("varbinary"))) {
                            isBinary = true;
                        }
                    }
                    else if (obj instanceof FunctionCalls) {
                        final FunctionCalls fc = (FunctionCalls)obj;
                        final TableColumn fnTc = fc.getFunctionName();
                        if (fnTc != null) {
                            final String fnName = fnTc.getColumnName();
                            if (fnName.equalsIgnoreCase("getdate")) {
                                isDate = true;
                            }
                        }
                    }
                    else if (obj instanceof SelectColumn) {
                        final SelectColumn sc = (SelectColumn)obj;
                        final Vector columExpr = sc.getColumnExpression();
                        if (columExpr.size() == 1) {
                            final Object columOjb = columExpr.get(0);
                            if (columOjb instanceof TableColumn) {
                                final TableColumn tc2 = (TableColumn)columOjb;
                                if (tc2.getColumnName().equalsIgnoreCase("sysdate")) {
                                    isDate = true;
                                }
                            }
                        }
                    }
                }
            }
            if (isDate) {
                final Object obj2 = this.functionArguments.get(0);
                if (obj2 instanceof Datatype) {
                    final Datatype type2 = (Datatype)obj2;
                    if (type2 instanceof NumericClass) {
                        if (argObj instanceof SelectColumn) {
                            argObj = ((SelectColumn)argObj).toOracleSelect(to_sqs, from_sqs);
                        }
                        this.functionName.setColumnName("");
                        this.setOpenBracesForFunctionNameRequired(false);
                        this.setAsDatatype(null);
                        arguments.add(argObj + " - TO_DATE('01-JAN-1900')");
                        this.setFunctionArguments(arguments);
                        return;
                    }
                }
            }
        }
        boolean targetTypeBinOrNum = false;
        boolean targetTypeNCharacter = false;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                final SelectColumn scl = this.functionArguments.elementAt(i_count);
                final Vector vcl = scl.getColumnExpression();
                if (vcl != null) {
                    for (int jj = 0; jj < vcl.size(); ++jj) {
                        final Object oo = vcl.get(jj);
                        if (oo instanceof TableColumn) {
                            tableColumn = (TableColumn)oo;
                        }
                    }
                }
                arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
                if (isChar && targetTypeBinOrNum) {
                    final Object obj3 = arguments.lastElement();
                    final FunctionCalls fc2 = new FunctionCalls();
                    final TableColumn tc2 = new TableColumn();
                    tc2.setColumnName("TRIM");
                    fc2.setFunctionName(tc2);
                    final Vector fnArgs = new Vector();
                    fnArgs.add(obj3);
                    fc2.setFunctionArguments(fnArgs);
                    arguments.setElementAt(fc2, arguments.size() - 1);
                }
                else if (isCharVarch && targetTypeNCharacter) {
                    this.functionName.setColumnName("");
                    this.setAsDatatype(null);
                    this.setOpenBracesForFunctionNameRequired(false);
                    arguments.remove(0);
                    this.setFunctionArguments(arguments);
                    return;
                }
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                boolean binWithoutSize = false;
                if (datatype instanceof BinClass) {
                    final BinClass bc = (BinClass)datatype;
                    final String type = bc.getDatatypeName();
                    if ((type.equalsIgnoreCase("binary") || type.equalsIgnoreCase("varbinary")) && bc.getSize() == null) {
                        binWithoutSize = true;
                    }
                }
                datatype.toOracleString();
                if (SwisSQLOptions.fromSQLServer) {
                    boolean isSetExpr = false;
                    if (from_sqs != null && from_sqs.getSelectStatement().getSelectItemList() != null && from_sqs.getSelectStatement().getSelectItemList().size() == 1 && from_sqs.getSelectStatement().getSelectItemList().get(0) instanceof SelectColumn && from_sqs.getFromClause() == null && (from_sqs.getSelectStatement().getSelectItemList().get(0).getColumnExpression().size() < 3 || !from_sqs.getSelectStatement().getSelectItemList().get(0).getColumnExpression().get(1).toString().equals("="))) {
                        isSetExpr = true;
                    }
                    if (datatype instanceof BinClass) {
                        final BinClass bc2 = (BinClass)datatype;
                        final String type3 = bc2.getDatatypeName();
                        final String size = bc2.getSize();
                        if (type3.equalsIgnoreCase("raw")) {
                            if (binWithoutSize && !isSetExpr) {
                                bc2.setSize("30");
                                bc2.setOpenBrace("(");
                                bc2.setClosedBrace(")");
                            }
                            else if (isSetExpr) {
                                bc2.setSize(null);
                                bc2.setOpenBrace(null);
                                bc2.setClosedBrace(null);
                            }
                        }
                    }
                    else if (datatype instanceof CharacterClass) {
                        final CharacterClass cc = (CharacterClass)datatype;
                        final String type3 = cc.getDatatypeName();
                        final String size = cc.getSize();
                        if (type3.equalsIgnoreCase("nchar") || type3.equalsIgnoreCase("nvarchar2")) {
                            if (size == null && !isSetExpr) {
                                cc.setSize("30");
                                cc.setOpenBrace("(");
                                cc.setClosedBrace(")");
                            }
                            else if (isSetExpr) {
                                cc.setSize(null);
                                cc.setOpenBrace(null);
                                cc.setClosedBrace(null);
                            }
                        }
                    }
                }
                if (datatype instanceof BinClass || datatype instanceof NumericClass) {
                    targetTypeBinOrNum = true;
                }
                else if (datatype instanceof CharacterClass) {
                    final String dataType2 = datatype.getDatatypeName();
                    if (dataType2.equalsIgnoreCase("nchar") || dataType2.equalsIgnoreCase("nvarchar2")) {
                        targetTypeNCharacter = true;
                    }
                }
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionArguments.get(0) instanceof Datatype && (this.functionArguments.size() != 2 || this.functionArguments.get(1).toString().indexOf("'") == -1 || (!this.functionArguments.get(0).toString().trim().toUpperCase().startsWith("CHAR") && !this.functionArguments.get(0).toString().trim().toUpperCase().startsWith("VARCHAR")))) {
            final Datatype dt = this.functionArguments.get(0);
            if (dt.toString().trim().toUpperCase().startsWith("CHAR") || dt.toString().trim().toUpperCase().startsWith("VARCHAR")) {
                if (tableColumn != null) {
                    columnDataType = MetadataInfoUtil.getDatatypeName(from_sqs, tableColumn);
                }
                if (columnDataType != null && columnDataType.toLowerCase().startsWith("uniqueidentifier")) {
                    final Vector newFunctionArgument = new Vector();
                    newFunctionArgument.add(arguments.get(1));
                    newFunctionArgument.add(arguments.get(0));
                    this.setFunctionArguments(newFunctionArgument);
                }
                else {
                    final Vector newFunctionArgument = new Vector();
                    this.functionName.setColumnName("TO_CHAR");
                    this.asDatatype = null;
                    newFunctionArgument.add(arguments.get(1));
                    if (this.functionArguments.size() > 2) {
                        if (this.functionArguments.get(2) instanceof SelectColumn) {
                            final SelectColumn fcSelectColumn = this.functionArguments.get(2);
                            final Vector fcColumnExpression = fcSelectColumn.getColumnExpression();
                            if (fcColumnExpression != null && fcColumnExpression.size() == 1) {
                                if (fcColumnExpression.get(0) instanceof String) {
                                    final String numericString = fcColumnExpression.get(0);
                                    try {
                                        final int i = Integer.parseInt(numericString);
                                        if (i == 100) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'Mon DD YYYY HH:MIAM'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 101) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'MM/DD/YYYY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 102) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YYYY.MM.DD'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 103) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD/MM/YYYY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 104) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD.MM.YYYY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 105) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD-MM-YYYY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 106) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD Mon YYYY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 107) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'MON DD, YYYY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 108) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'HH24:MI:SS'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 109) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'Mon DD YYYY HH:MI:SSAM'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 110) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'MM-DD-YYYY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 111) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YYYY/MM/DD'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 112) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YYYYMMDD'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 113) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD Mon YYYY HH24:MI:SS'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 114) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'HH24:MI:SS'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 120) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YYYY-MM-DD HH24:MI:SS'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 121) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YYYY-MM-DD HH24:MI:SS'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 0) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'Mon DD YYYY HH:MIAM'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 1) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'MM/DD/YY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 2) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YY.MM.DD'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 3) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD/MM/YY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 4) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD.MM.YY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 5) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD-MM-YY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 6) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD Mon YY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 7) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'Mon DD, YY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 8) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'HH24:MI:SS'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 9) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'Mon DD YYYY HH:MI:SSAM'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 10) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'MM-DD-YY'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 11) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YY/MM/DD'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 12) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YYMMDD'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 13) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'DD MON YYYY HH24:MI:SS'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 14) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'HH24:MI:SS'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 20) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YYYY MM DD HH24:MI:SS'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else if (i == 21) {
                                            final SelectColumn newSC = new SelectColumn();
                                            final Vector newColExp = new Vector();
                                            newColExp.add("'YYYY MM DD HH24:MI:SS.FF3'");
                                            newSC.setColumnExpression(newColExp);
                                            newFunctionArgument.add(newSC);
                                        }
                                        else {
                                            newFunctionArgument.add(this.functionArguments.get(2));
                                        }
                                    }
                                    catch (final NumberFormatException nfe) {
                                        newFunctionArgument.add(this.functionArguments.get(2));
                                    }
                                }
                                else {
                                    newFunctionArgument.add(this.functionArguments.get(2));
                                }
                            }
                            else {
                                newFunctionArgument.add(this.functionArguments.get(2));
                            }
                        }
                    }
                    else if (isDate) {
                        final SelectColumn newSC2 = new SelectColumn();
                        final Vector newColExp2 = new Vector();
                        newColExp2.add("'Mon DD YYYY HH:MIAM'");
                        newSC2.setColumnExpression(newColExp2);
                        newFunctionArgument.add(newSC2);
                    }
                    else if (isBinary) {
                        this.functionName.setColumnName("CAST");
                        this.asDatatype = "AS";
                        newFunctionArgument.add(arguments.get(0));
                        if (SwisSQLOptions.fromSQLServer) {
                            boolean isSetExpr2 = false;
                            if (from_sqs != null && from_sqs.getSelectStatement().getSelectItemList() != null && from_sqs.getSelectStatement().getSelectItemList().size() == 1 && from_sqs.getSelectStatement().getSelectItemList().get(0) instanceof SelectColumn && from_sqs.getFromClause() == null && (from_sqs.getSelectStatement().getSelectItemList().get(0).getColumnExpression().size() < 3 || !from_sqs.getSelectStatement().getSelectItemList().get(0).getColumnExpression().get(1).toString().equals("="))) {
                                isSetExpr2 = true;
                            }
                            final CharacterClass cc2 = arguments.get(0);
                            final String size2 = cc2.getSize();
                            if (size2 == null && !isSetExpr2) {
                                cc2.setSize("30");
                                cc2.setOpenBrace("(");
                                cc2.setClosedBrace(")");
                            }
                            else if (isSetExpr2) {
                                cc2.setSize(null);
                                cc2.setOpenBrace(null);
                                cc2.setClosedBrace(null);
                            }
                        }
                    }
                    this.setFunctionArguments(newFunctionArgument);
                }
            }
            else if (dt.toString().trim().equalsIgnoreCase("DATE") || dt.toString().trim().equalsIgnoreCase("DATETIME")) {
                final Vector newFunctionArgument = new Vector();
                this.functionName.setColumnName("TO_DATE");
                this.asDatatype = null;
                newFunctionArgument.add(arguments.get(1));
                if (this.functionArguments.size() > 2) {
                    if (this.functionArguments.get(2) instanceof SelectColumn) {
                        final SelectColumn fcSelectColumn = this.functionArguments.get(2);
                        final Vector fcColumnExpression = fcSelectColumn.getColumnExpression();
                        if (fcColumnExpression != null && fcColumnExpression.size() == 1) {
                            if (fcColumnExpression.get(0) instanceof String) {
                                final String numericString = fcColumnExpression.get(0);
                                try {
                                    final int i = Integer.parseInt(numericString);
                                    if (i == 103) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD/MM/YYYY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 102) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YYYY.MM.DD'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 121 || i == 120) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YYYY-MM-DD HH24:MI:SS'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 111) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YYYY/MM/DD'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 101) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'MM DD YYYY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 104) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD.MM.YYYY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 105) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD-MM-YYYY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 106) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD MON YYYY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 107) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'MON DD, YYYY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 108 || i == 8) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'HH24:MI:SS'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 110) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'MM-DD-YYYY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 112) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YYYYMMDD'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 113) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD MON YYYY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 14 || i == 114) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'HH24:MI:SS'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 120 || i == 121) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YYYY-MM-DD HH24:MI:SS'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 1) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'MM/DD/YY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 2) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YYY/MM/DD'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 3) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD/MM/YY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 4) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD.MM.YY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 5) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD-MM-YY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 6) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD-MON YY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 7) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'MON DD, YY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 9 || i == 109) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'Mon DD YYYY HH:MI:SSAM'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 10) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'MM-DD-YY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 11) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YY/MM/DD'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 12) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YYMMDD'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 13) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'DD MON YYYY'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 0 || i == 100) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'Mon DD YYYY HH:MIAM'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 20) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YYYY MM DD HH24:MI:SS'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else if (i == 21) {
                                        final SelectColumn newSC = new SelectColumn();
                                        final Vector newColExp = new Vector();
                                        newColExp.add("'YYYY MM DD HH24:MI:SS.FF3'");
                                        newSC.setColumnExpression(newColExp);
                                        newFunctionArgument.add(newSC);
                                    }
                                    else {
                                        newFunctionArgument.add(this.functionArguments.get(2));
                                    }
                                }
                                catch (final NumberFormatException nfe) {
                                    newFunctionArgument.add(this.functionArguments.get(2));
                                }
                            }
                            else {
                                newFunctionArgument.add(this.functionArguments.get(2));
                            }
                        }
                        else {
                            newFunctionArgument.add(this.functionArguments.get(2));
                        }
                    }
                }
                else if (this.functionArguments.size() == 2) {
                    if (this.functionArguments.get(1).toString().toUpperCase().indexOf("'MM'") != -1 && this.functionArguments.get(1).toString().toUpperCase().indexOf("'YYYY'") != -1) {
                        newFunctionArgument.add("'YYYY-MM-DD'");
                    }
                    if (this.functionArguments.get(1).toString().startsWith("'")) {
                        final String format = SwisSQLUtils.getDateFormat(this.functionArguments.get(1).toString().trim(), 1);
                        if (format != null) {
                            if (format.startsWith("'1900")) {
                                newFunctionArgument.setElementAt(format, 0);
                                newFunctionArgument.add("'YYYY-MM-DD HH24:MI:SS'");
                            }
                            else {
                                if (format.indexOf(".FF") != -1 || format.indexOf(":FF") != -1) {
                                    this.functionName.setColumnName("TO_TIMESTAMP");
                                }
                                newFunctionArgument.add(format);
                            }
                        }
                    }
                    if (this.functionArguments.get(1) instanceof SelectColumn) {
                        final SelectColumn scTemp = this.functionArguments.get(1);
                        final Vector newColExp2 = scTemp.getColumnExpression();
                        if (newColExp2 != null && newColExp2.size() == 1 && newColExp2.get(0) instanceof FunctionCalls) {
                            final FunctionCalls tempFC = newColExp2.get(0);
                            final String innerFunctionName = tempFC.getFunctionName().getColumnName();
                            if (innerFunctionName != null && innerFunctionName.trim().equalsIgnoreCase("TO_CHAR")) {
                                final Vector tempArgs = tempFC.getFunctionArguments();
                                if (tempArgs != null && tempArgs.size() == 2 && tempArgs.get(1) instanceof SelectColumn) {
                                    final SelectColumn toBeAdded = tempArgs.get(1);
                                    newFunctionArgument.add(toBeAdded);
                                }
                            }
                        }
                    }
                }
                this.setFunctionArguments(newFunctionArgument);
            }
            else if (dt.toString().toUpperCase().trim().startsWith("DECIMAL") || dt.toString().toUpperCase().trim().startsWith("NUMERIC") || dt.toString().toUpperCase().trim().startsWith("NUMBER")) {
                this.functionName.setColumnName("TO_NUMBER");
                final Vector newFunctionArgument = new Vector();
                this.asDatatype = null;
                newFunctionArgument.add(arguments.get(1));
                this.setFunctionArguments(newFunctionArgument);
            }
            else {
                final Vector swapArguments = new Vector();
                swapArguments.add(0, arguments.get(1));
                swapArguments.add(1, arguments.get(0));
                this.setFunctionArguments(swapArguments);
            }
        }
        else if (this.functionArguments.get(0) instanceof SelectColumn) {
            final SelectColumn sc2 = this.functionArguments.get(0);
            if (sc2.getColumnExpression() != null && sc2.getColumnExpression().size() > 0 && sc2.getColumnExpression().get(0) instanceof FunctionCalls) {
                final FunctionCalls fc3 = sc2.getColumnExpression().get(0);
                final TableColumn tc = fc3.getFunctionName();
                if ((tc != null && tc.getColumnName().equalsIgnoreCase("CHAR")) || tc.getColumnName().equalsIgnoreCase("CHR") || tc.getColumnName().equalsIgnoreCase("VARCHAR")) {
                    final Vector newFunctionArgument2 = new Vector();
                    this.functionName.setColumnName("TO_CHAR");
                    this.asDatatype = null;
                    newFunctionArgument2.add(arguments.get(1));
                    if (this.functionArguments.size() > 2 && this.functionArguments.get(2) instanceof SelectColumn) {
                        final SelectColumn fcSelectColumn2 = this.functionArguments.get(2);
                        final Vector fcColumnExpression2 = fcSelectColumn2.getColumnExpression();
                        if (fcColumnExpression2 != null && fcColumnExpression2.size() == 1) {
                            if (fcColumnExpression2.get(0) instanceof String) {
                                final String numericString2 = fcColumnExpression2.get(0);
                                try {
                                    final int j = Integer.parseInt(numericString2);
                                    if (j == 103) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD/MM/YYYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 102) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'YYYY.MM.DD'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 121 || j == 120) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'YYYY-MM-DD'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 111) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'YYYY/MM/DD'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 101) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD MON YYYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 104) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD.MM.YYYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 105) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD-MM-YYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 106) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'YYYY-MM-DD'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 107) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'MON DD, YYYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 108) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'HH:MI:SS'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 109) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'MON DD YYYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 110) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'MM-DD-YYYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 112) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'YYYY MM DD'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 113) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD MON YYYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 2) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'YYY/MM/DD'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 3) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD/MM/YY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 4) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD.MM.YY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 5) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD-MM-YY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 6) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD-MON YY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 7) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'MON DD, YY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 9) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'MON DD YYYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 10) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'MM-DD-YY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 11) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'YY/MM/DD'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 12) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'YYMMDD'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else if (j == 13) {
                                        final SelectColumn newSC3 = new SelectColumn();
                                        final Vector newColExp3 = new Vector();
                                        newColExp3.add("'DD MON YYYY'");
                                        newSC3.setColumnExpression(newColExp3);
                                        newFunctionArgument2.add(newSC3);
                                    }
                                    else {
                                        newFunctionArgument2.add(this.functionArguments.get(2));
                                    }
                                }
                                catch (final NumberFormatException nfe2) {
                                    newFunctionArgument2.add(this.functionArguments.get(2));
                                }
                            }
                            else {
                                newFunctionArgument2.add(this.functionArguments.get(2));
                            }
                        }
                        else {
                            newFunctionArgument2.add(this.functionArguments.get(2));
                        }
                    }
                    this.setFunctionArguments(newFunctionArgument2);
                }
                else if (tc.getColumnName().trim().equalsIgnoreCase("DECIMAL") || tc.getColumnName().trim().equalsIgnoreCase("NUMERIC")) {
                    this.functionName.setColumnName("TO_NUMBER");
                    final Vector newFunctionArgument2 = new Vector();
                    this.asDatatype = null;
                    newFunctionArgument2.add(arguments.get(1));
                    this.setFunctionArguments(newFunctionArgument2);
                }
            }
            else {
                final Vector swapArguments = new Vector();
                swapArguments.add(0, arguments.get(1));
                if (arguments.get(0).toString().equalsIgnoreCase("sql_variant")) {
                    swapArguments.add(1, "SYS.ANYDATA");
                }
                else if (arguments.get(0).toString().equalsIgnoreCase("uniqueidentifier")) {
                    swapArguments.add(1, "CHAR(36)");
                }
                else {
                    swapArguments.add(1, arguments.get(0));
                }
                this.setFunctionArguments(swapArguments);
            }
        }
        else if (this.functionArguments.size() == 2 && this.functionArguments.get(1).toString().indexOf("'") != -1 && (this.functionArguments.get(0).toString().trim().toUpperCase().startsWith("CHAR") || this.functionArguments.get(0).toString().trim().toUpperCase().startsWith("VARCHAR"))) {
            if (arguments.get(0).toString().indexOf("(") != -1) {
                final String value = arguments.get(1).toString().substring(1, arguments.get(1).toString().length() - 1);
                final int size3 = Integer.parseInt(arguments.get(0).getSize());
                if (value.length() > size3) {
                    this.functionName.setColumnName("SUBSTR");
                    this.setAsDatatype(null);
                    this.functionArguments.clear();
                    this.functionArguments.add(arguments.get(1));
                    this.functionArguments.add("1");
                    this.functionArguments.add(new Integer(size3));
                }
                else if (value.length() <= size3) {
                    this.functionName.setColumnName(arguments.get(1).toString());
                    final Vector dummyArgs = new Vector();
                    this.setFunctionArguments(dummyArgs);
                    this.setAsDatatype(null);
                    this.setOpenBracesForFunctionNameRequired(false);
                }
            }
            else {
                this.functionName.setColumnName(arguments.get(1).toString());
                final Vector dummyArgs2 = new Vector();
                this.setFunctionArguments(dummyArgs2);
                this.setAsDatatype(null);
                this.setOpenBracesForFunctionNameRequired(false);
            }
        }
        else {
            final Vector swapArguments2 = new Vector();
            swapArguments2.add(0, arguments.get(1));
            swapArguments2.add(1, arguments.get(0));
            this.setFunctionArguments(swapArguments2);
        }
        if (SwisSQLOptions.removeFormatForOracleToCharFunction && this.functionName.getColumnName().equalsIgnoreCase("to_char") && this.functionArguments.size() > 1) {
            this.functionArguments.removeElementAt(1);
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONVERT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                datatype.toMSSQLServerString();
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (from_sqs.isMSAzure()) {
            String arg1 = arguments.get(1).toString();
            if (arg1.equalsIgnoreCase("SIGNED") || arg1.equalsIgnoreCase("UNSIGNED")) {
                arg1 = "BIGINT";
            }
            arguments.set(1, arguments.get(0));
            arguments.set(0, arg1);
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONVERT");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                datatype.toSybaseString();
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        this.setAsDatatype("AS");
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                datatype.toDB2String();
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Vector swapArguments = new Vector();
        swapArguments.add(0, arguments.get(1));
        swapArguments.add(1, arguments.get(0));
        this.setFunctionArguments(swapArguments);
        if (this.functionArguments.size() == 2) {
            final Object obj1 = this.functionArguments.get(1);
            if (obj1 instanceof CharacterClass) {
                final CharacterClass cc = (CharacterClass)obj1;
                if (cc.getDatatypeName().equalsIgnoreCase("varchar")) {
                    final Object obj2 = this.functionArguments.get(0);
                    if (obj2 instanceof SelectColumn) {
                        final Vector colExpr = ((SelectColumn)obj2).getColumnExpression();
                        if (colExpr.size() == 1 && colExpr.get(0) instanceof FunctionCalls) {
                            final FunctionCalls fc = colExpr.get(0);
                            final TableColumn tc = fc.getFunctionName();
                            if (tc != null && this.isIntegerRetFunction(tc.getColumnName())) {
                                String size = cc.getSize();
                                if (size == null) {
                                    size = "30";
                                }
                                colExpr.setElementAt("RTRIM(CAST(" + colExpr.get(0) + " AS CHAR(" + size + ")))", 0);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean isIntegerRetFunction(final String fnName) {
        return fnName.equalsIgnoreCase("day") || fnName.equalsIgnoreCase("month") || fnName.equalsIgnoreCase("year") || fnName.equalsIgnoreCase("quarter") || fnName.equalsIgnoreCase("week") || fnName.equalsIgnoreCase("dayofyear") || fnName.equalsIgnoreCase("dayofweek") || fnName.equalsIgnoreCase("hour") || fnName.equalsIgnoreCase("minute") || fnName.equalsIgnoreCase("second");
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        String qry = "";
        final boolean isAmazonRedshift = from_sqs != null && from_sqs.isAmazonRedShift();
        final boolean canUseUDFFunction = from_sqs != null && !isAmazonRedshift && from_sqs.canUseUDFFunctionsForNumeric();
        if (arguments.size() == 2) {
            String numericStr = "(" + arguments.get(0).toString() + ")";
            if (canUseUDFFunction) {
                numericStr = "TONUMERIC_UDF(" + numericStr + ")";
            }
            else {
                numericStr = "TONUMERIC_UDF((" + numericStr + ")::text)";
            }
            if (arguments.get(1).toString().equalsIgnoreCase("CURRENT_DATE") || arguments.get(1).toString().equalsIgnoreCase("DATE")) {
                qry = " cast(" + arguments.get(0) + " as DATE) ";
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("DATETIME")) {
                qry = " cast(" + arguments.get(0) + " as TIMESTAMP) ";
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("CURRENT_TIME") || arguments.get(1).toString().equalsIgnoreCase("TIME")) {
                qry = " cast(" + arguments.get(0) + " as TIME) ";
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("CHAR") || arguments.get(1).toString().startsWith("CHR")) {
                qry = " cast(" + arguments.get(0) + " as TEXT) ";
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("SIGNED")) {
                if (isAmazonRedshift) {
                    qry = " cast(" + arguments.get(0) + " as BIGINT) ";
                }
                else {
                    qry = " cast(" + numericStr + " as BIGINT) ";
                }
            }
            else if (arguments.get(1).toString().replaceAll("\\s+", "").equalsIgnoreCase("SIGNEDASINTEGER")) {
                if (isAmazonRedshift) {
                    qry = " cast(" + arguments.get(0) + " as BIGINT) ";
                }
                else {
                    qry = " cast(" + numericStr + " as BIGINT) ";
                }
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("DECIMAL") || arguments.get(1).toString().toUpperCase().startsWith("DECIMAL")) {
                if (isAmazonRedshift) {
                    qry = "CAST(" + arguments.get(0).toString() + "AS " + arguments.get(1).toString() + ")";
                }
                else {
                    qry = "CAST(" + numericStr + " AS " + arguments.get(1).toString() + ")";
                }
            }
            else {
                if (!arguments.get(1).toString().equalsIgnoreCase("\"BINARY\"")) {
                    throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported. \n Please ensure that the correct number of arguments are passed\n");
                }
                qry = arguments.get(0) + "";
            }
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final int size = this.functionArguments.size();
        boolean isDateConversion = false;
        String funName = null;
        SelectColumn selCol = null;
        if (size > 1 && this.functionArguments.get(1) instanceof SelectColumn) {
            selCol = this.functionArguments.get(1);
        }
        funName = this.getFunctionNameFromSelectColumn(selCol);
        if (funName != null && funName.trim().equalsIgnoreCase("GetDate")) {
            isDateConversion = true;
        }
        if (isDateConversion) {
            boolean isCharNeeded = false;
            this.functionName.setColumnName("CONVERT");
            final Vector arguments = new Vector();
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs));
                }
                else if (this.functionArguments.elementAt(i_count) instanceof CharacterClass) {
                    isCharNeeded = true;
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            if (size == 3) {
                arguments.clear();
                arguments.addElement("CURDATE() ");
            }
            if (isCharNeeded) {
                arguments.addElement("CHAR");
            }
            this.setFunctionArguments(arguments);
        }
        else {
            boolean setArg = true;
            final Vector arguments = new Vector();
            final Vector datatypeVec = new Vector();
            final Vector expressionVec = new Vector();
            for (int i_count2 = 0; i_count2 < size; ++i_count2) {
                if (this.functionArguments.elementAt(i_count2) instanceof SelectColumn) {
                    SelectColumn sc = new SelectColumn();
                    sc = this.functionArguments.elementAt(i_count2).toMySQLSelect(to_sqs, from_sqs);
                    arguments.addElement(sc);
                    expressionVec.addElement(sc);
                }
                else if (this.functionArguments.elementAt(i_count2) instanceof Datatype) {
                    final Datatype datatype = this.functionArguments.elementAt(i_count2);
                    datatype.toMySQLString();
                    arguments.addElement(datatype);
                    datatypeVec.addElement(datatype);
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count2));
                    expressionVec.addElement(this.functionArguments.elementAt(i_count2));
                }
            }
            if (size == 2 && this.getUsing() == null) {
                final Vector newArguments = new Vector();
                if (datatypeVec.size() == 1 && expressionVec.size() == 1) {
                    newArguments.add(expressionVec.get(0));
                    final Datatype targDatatype = datatypeVec.get(0);
                    if (targDatatype.getDatatypeName().equalsIgnoreCase("VARCHAR") || targDatatype.getDatatypeName().equalsIgnoreCase("CHAR") || targDatatype.getDatatypeName().equalsIgnoreCase("TEXT") || targDatatype.getDatatypeName().equalsIgnoreCase("LONGTEXT")) {
                        if (targDatatype instanceof CharacterClass) {
                            final CharacterClass charDatatype = (CharacterClass)targDatatype;
                            charDatatype.setNational(null);
                            charDatatype.setBinary(null);
                        }
                        targDatatype.setDatatypeName("CHAR");
                        newArguments.add(targDatatype);
                        this.setFunctionArguments(newArguments);
                        setArg = false;
                    }
                }
            }
            if (setArg) {
                this.setFunctionArguments(arguments);
            }
        }
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        this.setAsDatatype("AS");
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                datatype.toANSIString();
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Vector swapArguments = new Vector();
        swapArguments.add(0, arguments.get(1));
        swapArguments.add(1, arguments.get(0));
        this.setFunctionArguments(swapArguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        this.setAsDatatype("AS");
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs));
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                datatype.toTeradataString();
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (arguments.get(1) instanceof SelectColumn && arguments.get(1).toString().toUpperCase().indexOf("US7ASCII") != -1) {
            this.functionName.setColumnName("TRANSLATE");
            this.setAsDatatype("USING");
            arguments.setElementAt("UNICODE_TO_LATIN", 1);
            this.setFunctionArguments(arguments);
        }
        else {
            final Vector swapArguments = new Vector();
            swapArguments.add(0, arguments.get(1));
            swapArguments.add(1, arguments.get(0));
            this.setFunctionArguments(swapArguments);
        }
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        this.setAsDatatype("");
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                datatype.toInformixString();
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Vector swapArguments = new Vector();
        swapArguments.add(0, arguments.get(1));
        swapArguments.add("::");
        swapArguments.add(2, arguments.get(0));
        this.setFunctionArguments(swapArguments);
    }
    
    @Override
    public void toTimesTen(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionArguments.size() == 2) {
            if (this.functionArguments.elementAt(0) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(0);
                final String type = datatype.getDatatypeName();
                if (type.toLowerCase().indexOf("char") != -1) {
                    this.functionName.setColumnName("TO_CHAR");
                    arguments.add(this.functionArguments.elementAt(1).toTimesTenSelect(to_sqs, from_sqs));
                    this.setFunctionArguments(arguments);
                }
                else {
                    if (type.toLowerCase().indexOf("date") == -1 && type.toLowerCase().indexOf("time") == -1) {
                        throw new ConvertException("\nThe function CONVERT is not supported in TimesTen 5.1.21\n");
                    }
                    this.functionName.setColumnName("TO_DATE");
                    arguments.add(this.functionArguments.elementAt(1).toTimesTenSelect(to_sqs, from_sqs));
                    if (this.functionArguments.elementAt(1).toString().trim().startsWith("'")) {
                        String literalValue = this.functionArguments.elementAt(1).toString().trim();
                        String format = SwisSQLUtils.getDateFormat(literalValue, 10);
                        if (format != null && (format.equals("YYYY-MM-DD") || format.equals("HH24:MI:SS"))) {
                            if (type.toLowerCase().indexOf("datetime") != -1) {
                                if (format.equals("YYYY-MM-DD")) {
                                    literalValue = literalValue.substring(0, literalValue.length() - 1) + " 00:00:00'";
                                }
                                else {
                                    literalValue = "'1900-01-01 " + literalValue.substring(1);
                                }
                                arguments.get(0).getColumnExpression().setElementAt(literalValue, 0);
                            }
                            format = null;
                        }
                        if (format != null) {
                            if (format.startsWith("'1900")) {
                                arguments.get(0).getColumnExpression().setElementAt(format, 0);
                            }
                            else if (format.equals(literalValue)) {
                                literalValue = literalValue.substring(1, literalValue.length() - 1);
                                final int len = literalValue.length();
                                if (len == 8) {
                                    literalValue = literalValue.substring(0, 4) + "-" + literalValue.substring(4, 6) + "-" + literalValue.substring(6);
                                }
                                else if (len == 6) {
                                    String yearStr = literalValue.substring(0, 2);
                                    final int year = Integer.parseInt(yearStr);
                                    if (year < 50) {
                                        yearStr = "20" + yearStr;
                                    }
                                    else {
                                        yearStr = "19" + yearStr;
                                    }
                                    literalValue = yearStr + "-" + literalValue.substring(2, 4) + "-" + literalValue.substring(4);
                                }
                                arguments.get(0).getColumnExpression().setElementAt("'" + literalValue + "'", 0);
                            }
                            else {
                                arguments.add(format);
                            }
                        }
                    }
                    this.setFunctionArguments(arguments);
                }
            }
        }
        else if (this.functionArguments.size() == 3) {
            this.functionName.setColumnName("TO_DATE");
            arguments.add(this.functionArguments.elementAt(1).toTimesTenSelect(to_sqs, from_sqs));
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        this.setAsDatatype("AS");
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                datatype.toNetezzaString();
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Vector swapArguments = new Vector();
        swapArguments.add(0, arguments.get(1));
        swapArguments.add(1, arguments.get(0));
        this.setFunctionArguments(swapArguments);
    }
    
    private String getFunctionNameFromSelectColumn(final SelectColumn selCol) {
        String funName = null;
        if (selCol != null) {
            final Vector colExp = selCol.getColumnExpression();
            if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof FunctionCalls) {
                final FunctionCalls funCall = colExp.get(0);
                final TableColumn tabCol = funCall.getFunctionName();
                if (tabCol != null) {
                    funName = tabCol.getColumnName();
                }
            }
        }
        return funName;
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        if (this.functionArguments.size() == 2) {
            if (arguments.get(1).toString().equalsIgnoreCase("CHAR") || arguments.get(1).toString().toUpperCase().startsWith("CHAR") || arguments.get(1).toString().toUpperCase().startsWith("CHR")) {
                this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS VARCHAR)");
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("BINARY")) {
                this.functionName.setColumnName(arguments.get(0).toString());
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("DATE")) {
                this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS DATE)");
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("DATETIME")) {
                this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)");
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("TIME")) {
                this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS TIME)");
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("SIGNED") || arguments.get(1).toString().equalsIgnoreCase("SIGNED INTEGER") || arguments.get(1).toString().equalsIgnoreCase("INTEGER")) {
                this.functionName.setColumnName("IF(" + arguments.get(0).toString() + " IS INTEGER, CAST(" + arguments.get(0).toString() + " AS BIGINT),0)");
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else if (arguments.get(1).toString().equalsIgnoreCase("DECIMAL") || arguments.get(1).toString().toUpperCase().startsWith("DECIMAL")) {
                String secArg = arguments.get(1).toString();
                if (this.functionArguments.get(1).getColumnExpression().get(0) != null && !(this.functionArguments.get(1).getColumnExpression().get(0) instanceof FunctionCalls)) {
                    secArg = "DECIMAL(38,0)";
                }
                this.functionName.setColumnName("IF(" + arguments.get(0).toString() + " IS DECIMAL, CAST(" + arguments.get(0).toString() + " AS " + secArg + "),0)");
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            else {
                if (!arguments.get(1).toString().toUpperCase().startsWith("TRUNCATE")) {
                    throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
                }
                final String secArg = arguments.get(1).toString().toUpperCase().replaceAll("TRUNCATE", "DECIMAL");
                this.functionName.setColumnName("IF(" + arguments.get(0).toString() + " IS DECIMAL, CAST(" + arguments.get(0).toString() + " AS " + secArg + "),0)");
                this.setOpenBracesForFunctionNameRequired(false);
                this.functionArguments = new Vector();
            }
            return;
        }
        throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
    }
}
