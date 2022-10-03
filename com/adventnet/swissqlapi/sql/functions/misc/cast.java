package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import java.util.Hashtable;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.create.QuotedIdentifierDatatype;
import com.adventnet.swissqlapi.sql.functions.date.dateadd;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
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

public class cast extends FunctionCalls
{
    @Override
    public void toOracle(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionName.getColumnName().equalsIgnoreCase("cast")) {
            this.functionName.setColumnName("CAST");
            final Vector arguments = new Vector();
            SelectColumn sc = null;
            boolean isDate = false;
            boolean isChar = false;
            boolean isCharVarch = false;
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    final SelectColumn selCol = this.functionArguments.elementAt(i_count);
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
                        }
                        else if (obj instanceof FunctionCalls) {
                            final FunctionCalls fc = (FunctionCalls)obj;
                            final TableColumn fnTc = fc.getFunctionName();
                            if (fnTc != null) {
                                final String fnName = fnTc.getColumnName();
                                if (fnName.equalsIgnoreCase("getdate")) {
                                    isDate = true;
                                }
                                else if (fnName.equalsIgnoreCase("right") || fnName.equalsIgnoreCase("SUBSTR")) {
                                    isCharVarch = true;
                                }
                            }
                        }
                    }
                    arguments.addElement(this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs));
                    sc = this.functionArguments.elementAt(i_count).toOracleSelect(to_sqs, from_sqs);
                }
                else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                    final Datatype datatype = this.functionArguments.elementAt(i_count);
                    DatatypeMapping mapping = null;
                    if (from_sqs != null) {
                        mapping = from_sqs.getDatatypeMapping();
                    }
                    if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                        boolean money = false;
                        if (datatype instanceof NumericClass) {
                            final String type2 = datatype.getDatatypeName();
                            if (type2.equalsIgnoreCase("money") || type2.equalsIgnoreCase("smallmoney")) {
                                money = true;
                            }
                        }
                        else if (datatype instanceof BinClass) {
                            final String type2 = datatype.getDatatypeName();
                            if (type2.equalsIgnoreCase("varbinary")) {
                                this.functionName.setColumnName("RAWTOHEX");
                                this.setFunctionArguments(arguments);
                                this.setAsDatatype(null);
                                return;
                            }
                        }
                        datatype.toOracleString();
                        if (SwisSQLOptions.fromSQLServer) {
                            boolean isSetExpr = false;
                            if (from_sqs != null && from_sqs.getSelectStatement().getSelectItemList() != null && from_sqs.getSelectStatement().getSelectItemList().size() == 1 && from_sqs.getSelectStatement().getSelectItemList().get(0) instanceof SelectColumn && from_sqs.getFromClause() == null && (from_sqs.getSelectStatement().getSelectItemList().get(0).getColumnExpression().size() < 3 || !from_sqs.getSelectStatement().getSelectItemList().get(0).getColumnExpression().get(1).toString().equals("="))) {
                                isSetExpr = true;
                            }
                            if (isSetExpr) {
                                if (datatype instanceof BinClass) {
                                    final BinClass bc = (BinClass)datatype;
                                    final String type = bc.getDatatypeName();
                                    final String size = bc.getSize();
                                    if (type.equalsIgnoreCase("raw") || type.equalsIgnoreCase("number")) {
                                        bc.setSize(null);
                                        bc.setOpenBrace(null);
                                        bc.setClosedBrace(null);
                                    }
                                }
                                else if (money) {
                                    final NumericClass nc = (NumericClass)datatype;
                                    nc.setOpenBrace(null);
                                    nc.setClosedBrace(null);
                                    nc.setPrecision(null);
                                    nc.setScale(null);
                                }
                            }
                        }
                    }
                    if (SwisSQLOptions.PLSQL) {
                        if (datatype instanceof NumericClass) {
                            final NumericClass plsqlNumericClass = (NumericClass)datatype;
                            plsqlNumericClass.setOpenBrace(null);
                            plsqlNumericClass.setPrecision(null);
                            plsqlNumericClass.setScale(null);
                            plsqlNumericClass.setClosedBrace(null);
                        }
                        else if (datatype instanceof BinClass) {
                            final BinClass plsqlBinClass = (BinClass)datatype;
                            plsqlBinClass.setClosedBrace(null);
                            plsqlBinClass.setSize(null);
                            plsqlBinClass.setOpenBrace(null);
                        }
                        else if (datatype instanceof CharacterClass) {
                            final CharacterClass plsqlCharacterClass = (CharacterClass)datatype;
                            plsqlCharacterClass.setClosedBrace(null);
                            plsqlCharacterClass.setSize(null);
                            plsqlCharacterClass.setOpenBrace(null);
                        }
                        else if (datatype instanceof DateClass) {
                            final DateClass plsqlDateClass = (DateClass)datatype;
                            plsqlDateClass.setClosedBrace(null);
                            plsqlDateClass.setSize(null);
                            plsqlDateClass.setOpenBrace(null);
                        }
                        arguments.addElement(datatype);
                    }
                    else if (datatype instanceof DateClass) {
                        this.functionName.setColumnName("TO_DATE");
                        this.setAsDatatype(null);
                        this.convertTheDateFunctionWithDateFormatDefinition(this.functionArguments, arguments);
                        final DateClass plsqlDateClass = (DateClass)datatype;
                        plsqlDateClass.setClosedBrace(null);
                        plsqlDateClass.setSize(null);
                        plsqlDateClass.setOpenBrace(null);
                    }
                    else {
                        arguments.addElement(datatype);
                    }
                    if (SwisSQLOptions.fromSQLServer && datatype instanceof NumericClass) {
                        final NumericClass plsqlNumericClass = (NumericClass)datatype;
                        if (isDate) {
                            this.functionName.setColumnName("");
                            this.setOpenBracesForFunctionNameRequired(false);
                            this.setAsDatatype(null);
                            final String str = arguments.get(0).toString();
                            arguments.clear();
                            arguments.add(str + " - TO_DATE('01-JAN-1900')");
                        }
                    }
                    if (isChar && (datatype instanceof NumericClass || datatype instanceof BinClass)) {
                        final Object obj = arguments.get(0);
                        final FunctionCalls fc = new FunctionCalls();
                        final TableColumn tc2 = new TableColumn();
                        tc2.setColumnName("TRIM");
                        fc.setFunctionName(tc2);
                        final Vector fnArgs = new Vector();
                        fnArgs.add(obj);
                        fc.setFunctionArguments(fnArgs);
                        arguments.setElementAt(fc, 0);
                    }
                    if (isCharVarch && datatype instanceof CharacterClass) {
                        final String dataType2 = datatype.getDatatypeName();
                        if (dataType2.equalsIgnoreCase("nchar") || dataType2.equalsIgnoreCase("nvarchar2") || dataType2.equalsIgnoreCase("varchar2") || dataType2.equalsIgnoreCase("char") || dataType2.equalsIgnoreCase("varchar")) {
                            this.functionName.setColumnName("");
                            this.setAsDatatype(null);
                            this.setOpenBracesForFunctionNameRequired(false);
                            arguments.remove(arguments.size() - 1);
                        }
                    }
                }
                else {
                    final Object obj2 = this.functionArguments.elementAt(i_count);
                    if (obj2 != null && obj2.toString().equalsIgnoreCase("uniqueidentifier")) {
                        arguments.addElement("CHAR(36)");
                    }
                    else if (obj2 != null && obj2.toString().equalsIgnoreCase("sql_variant")) {
                        arguments.addElement("SYS.ANYDATA");
                    }
                    else {
                        arguments.addElement(this.functionArguments.elementAt(i_count));
                    }
                }
            }
            this.setFunctionArguments(arguments);
        }
        if (this.functionName.getColumnName().equalsIgnoreCase("decimal") || this.functionName.getColumnName().equalsIgnoreCase("dec")) {
            this.functionName.setColumnName("CAST");
            final Vector arguments = new Vector();
            arguments.addElement(this.functionArguments.get(0));
            this.setAsDatatype("AS");
            final NumericClass plsqlNumericClass2 = new NumericClass();
            plsqlNumericClass2.setDatatypeName("NUMBER");
            if (this.functionArguments.size() > 1) {
                final SelectColumn sc2 = this.functionArguments.get(1);
                final SelectColumn sc3 = this.functionArguments.get(2);
                final Vector p = sc2.getColumnExpression();
                final Vector s = sc3.getColumnExpression();
                final String prec = p.get(0);
                final String scal = s.get(0);
                plsqlNumericClass2.setPrecision(prec);
                plsqlNumericClass2.setScale(scal);
                plsqlNumericClass2.setOpenBrace("(");
                plsqlNumericClass2.setClosedBrace(")");
            }
            arguments.addElement(plsqlNumericClass2);
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toMSSQLServer(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        SelectColumn sc = null;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs));
                sc = this.functionArguments.elementAt(i_count).toMSSQLServerSelect(to_sqs, from_sqs);
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                DatatypeMapping mapping = null;
                if (from_sqs != null) {
                    mapping = from_sqs.getDatatypeMapping();
                }
                if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                    datatype.toMSSQLServerString();
                }
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toSybase(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CONVERT");
        this.setAsDatatype(null);
        final Vector arguments = new Vector();
        SelectColumn sc = null;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs));
                sc = this.functionArguments.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs);
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                DatatypeMapping mapping = null;
                if (from_sqs != null) {
                    mapping = from_sqs.getDatatypeMapping();
                }
                if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                    datatype.toSybaseString();
                }
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        final Vector swapArguments = new Vector();
        if (arguments.size() > 1) {
            swapArguments.add(arguments.get(1));
            swapArguments.add(arguments.get(0));
        }
        this.setFunctionArguments(swapArguments);
    }
    
    @Override
    public void toDB2(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        SelectColumn sc = null;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs));
                sc = this.functionArguments.elementAt(i_count).toDB2Select(to_sqs, from_sqs);
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                DatatypeMapping mapping = null;
                if (from_sqs != null) {
                    mapping = from_sqs.getDatatypeMapping();
                }
                if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                    if (datatype instanceof CharacterClass && datatype.getDatatypeName().equalsIgnoreCase("VARCHAR")) {
                        if (datatype.getSize() == null) {
                            datatype.setSize("100");
                            datatype.setOpenBrace("(");
                            datatype.setClosedBrace(")");
                        }
                        final Object o = arguments.get(0);
                        if (o instanceof SelectColumn) {
                            final SelectColumn sc2 = (SelectColumn)o;
                            final Vector v = sc2.getColumnExpression();
                            for (int size = v.size(), k = 0; k < size; ++k) {
                                final Object o2 = v.elementAt(k);
                                if (o2 instanceof TableColumn) {
                                    final TableColumn tc = (TableColumn)o2;
                                    final String str = "CHAR(" + o2 + ")";
                                    v.setElementAt(str, k);
                                }
                            }
                        }
                    }
                    datatype.toDB2String();
                }
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toPostgreSQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.functionArguments.elementAt(1) instanceof BinClass) {
            final StringBuffer cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                cast.append(this.functionArguments.elementAt(0).toPostgreSQLSelect(to_sqs, from_sqs).toString());
            }
            else {
                cast.append(this.functionArguments.elementAt(0).toString());
            }
            this.functionName.setColumnName("" + (Object)cast);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
            final StringBuffer cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                cast.append(this.functionArguments.elementAt(0).toPostgreSQLSelect(to_sqs, from_sqs).toString());
            }
            else {
                cast.append(this.functionArguments.elementAt(0).toString());
            }
            this.functionName.setColumnName("CAST(" + (Object)cast + " as TEXT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionArguments.elementAt(1).toString().trim().equalsIgnoreCase("signed")) {
            final StringBuffer cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                cast.append(this.functionArguments.elementAt(0).toPostgreSQLSelect(to_sqs, from_sqs).toString());
            }
            else {
                cast.append(this.functionArguments.elementAt(0).toString());
            }
            this.functionName.setColumnName("cast(" + (Object)cast + " as BIGINT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionArguments.elementAt(1) instanceof DateClass) {
            final StringBuffer cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                this.handleStringLiteralForDateTime(from_sqs, 0, false);
                cast.append(this.functionArguments.elementAt(0).toPostgreSQLSelect(to_sqs, from_sqs).toString());
            }
            else {
                cast.append(this.functionArguments.elementAt(0).toString());
            }
            String dataType = "DATE";
            if (this.functionArguments.elementAt(1).toString().replaceAll(" ", "").equalsIgnoreCase("DATETIME")) {
                dataType = "TIMESTAMP";
            }
            this.functionName.setColumnName("CAST(" + (Object)cast + " AS " + dataType + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else {
            this.functionName.setColumnName("CAST");
            final Vector arguments = new Vector();
            SelectColumn sc = null;
            for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
                if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
                    sc = this.functionArguments.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs);
                }
                else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                    final Datatype datatype = this.functionArguments.elementAt(i_count);
                    DatatypeMapping mapping = null;
                    if (from_sqs != null) {
                        mapping = from_sqs.getDatatypeMapping();
                    }
                    if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                        datatype.toPostgreSQLString();
                    }
                    arguments.addElement(datatype);
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i_count));
                }
            }
            this.setFunctionArguments(arguments);
        }
    }
    
    @Override
    public void toMySQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
    }
    
    @Override
    public void toANSISQL(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        SelectColumn sc = null;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                cast.functionArgsInSingleQuotesToDouble = false;
                arguments.addElement(this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs));
                sc = this.functionArguments.elementAt(i_count).toANSISelect(to_sqs, from_sqs);
                cast.functionArgsInSingleQuotesToDouble = true;
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                DatatypeMapping mapping = null;
                if (from_sqs != null) {
                    mapping = from_sqs.getDatatypeMapping();
                }
                if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                    datatype.toANSIString();
                }
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toInformix(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("");
        final Vector arguments = new Vector();
        this.setAsDatatype("");
        SelectColumn sc = null;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toInformixSelect(to_sqs, from_sqs));
                sc = this.functionArguments.elementAt(i_count).toInformixSelect(to_sqs, from_sqs);
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                DatatypeMapping mapping = null;
                if (from_sqs != null) {
                    mapping = from_sqs.getDatatypeMapping();
                }
                if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                    datatype.toInformixString();
                }
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        arguments.add(1, "::");
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toNetezza(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        SelectColumn sc = null;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs));
                sc = this.functionArguments.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs);
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                DatatypeMapping mapping = null;
                if (from_sqs != null) {
                    mapping = from_sqs.getDatatypeMapping();
                }
                if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                    datatype.toNetezzaString();
                }
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    @Override
    public void toTeradata(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        SelectColumn sc = null;
        for (int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                sc = this.functionArguments.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs);
                if (sc.getTheCoreSelectItem().trim().equals("''")) {
                    arguments.addElement("NULL");
                }
                else {
                    arguments.addElement(sc);
                }
            }
            else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                final Datatype datatype = this.functionArguments.elementAt(i_count);
                DatatypeMapping mapping = null;
                if (from_sqs != null) {
                    mapping = from_sqs.getDatatypeMapping();
                }
                if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                    datatype.toTeradataString();
                }
                if (datatype instanceof NumericClass) {
                    final NumericClass nc = (NumericClass)datatype;
                    if (nc.getPrecision() == null && nc.getScale() == null) {
                        nc.setOpenBrace("(");
                        nc.setPrecision("38");
                        nc.setScale("16");
                        nc.setClosedBrace(")");
                    }
                }
                else if (datatype instanceof CharacterClass && SwisSQLOptions.castCharDatatypeAsCaseSpecific) {
                    ((CharacterClass)datatype).setCaseSpecificPhrase("CASESPECIFIC");
                }
                else if (datatype instanceof DateClass && datatype.getDatatypeName().equalsIgnoreCase("date")) {
                    datatype.setDatatypeName("TIMESTAMP");
                    datatype.setSize("0");
                    datatype.setOpenBrace("(");
                    datatype.setClosedBrace(")");
                }
                arguments.addElement(datatype);
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i_count));
            }
        }
        this.setFunctionArguments(arguments);
    }
    
    public void convertTheDateFunctionWithDateFormatDefinition(final Vector functionArguments, final Vector arguments) {
        for (int i = 0; i < functionArguments.size(); ++i) {
            if (functionArguments.get(0) instanceof SelectColumn) {
                final SelectColumn sc = functionArguments.get(0);
                final Vector columnExpression = sc.getColumnExpression();
                if (columnExpression != null) {
                    for (int index = 0; index < columnExpression.size(); ++index) {
                        if (columnExpression.get(index) instanceof String) {
                            String str = columnExpression.get(index);
                            final dateadd dateAddObj = new dateadd();
                            str = dateAddObj.dateFormatConversion(str);
                            if (str.trim().endsWith(")")) {
                                str = str.substring(0, str.length() - 1);
                            }
                            columnExpression.set(index, str);
                            sc.setColumnExpression(columnExpression);
                            arguments.set(0, sc);
                            return;
                        }
                    }
                }
            }
        }
    }
    
    private boolean mapDatatype(final Datatype dataType, final DatatypeMapping mapping, final SelectColumn sc, final SelectQueryStatement from_sqs) {
        if (dataType != null) {
            String datatypeName = dataType.getDatatypeName();
            if (datatypeName != null) {
                if (SwisSQLAPI.objectContext != null) {
                    String fromColName = null;
                    if (sc != null) {
                        final Vector colExp = sc.getColumnExpression();
                        if (colExp != null) {
                            for (int i = 0; i < colExp.size(); ++i) {
                                if (colExp.get(i) instanceof TableColumn) {
                                    fromColName = colExp.get(i).getColumnName();
                                }
                            }
                        }
                    }
                    if (from_sqs == null || (from_sqs != null && from_sqs.getFromClause() == null)) {
                        final Object val = SwisSQLAPI.objectContext.getMappedDatatype(null, null, datatypeName);
                        if (val != null) {
                            final String newDatatypeName = (String)val;
                            if (newDatatypeName != null) {
                                if (newDatatypeName.indexOf("(") != -1) {
                                    dataType.setDatatypeName(newDatatypeName.substring(0, newDatatypeName.indexOf("(")));
                                    dataType.setOpenBrace("(");
                                    dataType.setClosedBrace(")");
                                    dataType.setSize(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                                    if (dataType instanceof QuotedIdentifierDatatype) {
                                        ((QuotedIdentifierDatatype)dataType).setPrecision(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                                    }
                                }
                                else {
                                    dataType.setDatatypeName(newDatatypeName);
                                }
                                return true;
                            }
                        }
                    }
                    if (fromColName != null && from_sqs != null) {
                        final FromClause fromClause = from_sqs.getFromClause();
                        if (fromClause != null) {
                            final Vector fromItems = fromClause.getFromItemList();
                            if (fromItems != null) {
                                for (int j = 0; j < fromItems.size(); ++j) {
                                    if (fromItems.get(j) instanceof FromTable) {
                                        final Object fromItemObj = fromItems.get(j).getTableName();
                                        if (fromItemObj != null) {
                                            final Object val2 = SwisSQLAPI.objectContext.getMappedDatatype(fromItemObj.toString().toLowerCase(), fromColName, datatypeName);
                                            if (val2 != null) {
                                                final String newDatatypeName2 = (String)val2;
                                                if (newDatatypeName2 != null) {
                                                    if (newDatatypeName2.indexOf("(") != -1) {
                                                        dataType.setDatatypeName(newDatatypeName2.substring(0, newDatatypeName2.indexOf("(")));
                                                        dataType.setOpenBrace("(");
                                                        dataType.setClosedBrace(")");
                                                        dataType.setSize(newDatatypeName2.substring(newDatatypeName2.indexOf("(") + 1, newDatatypeName2.indexOf(")")));
                                                        if (dataType instanceof QuotedIdentifierDatatype) {
                                                            ((QuotedIdentifierDatatype)dataType).setPrecision(newDatatypeName2.substring(newDatatypeName2.indexOf("(") + 1, newDatatypeName2.indexOf(")")));
                                                        }
                                                    }
                                                    else {
                                                        dataType.setDatatypeName(newDatatypeName2);
                                                    }
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (mapping != null) {
                    final Hashtable tableSpecificMapping = mapping.getTableSpecificDatatypeMapping();
                    if (tableSpecificMapping != null) {
                        String fromColName2 = null;
                        if (sc != null) {
                            final Vector colExp2 = sc.getColumnExpression();
                            if (colExp2 != null) {
                                for (int j = 0; j < colExp2.size(); ++j) {
                                    if (colExp2.get(j) instanceof TableColumn) {
                                        fromColName2 = colExp2.get(j).getColumnName();
                                    }
                                }
                            }
                        }
                        if (fromColName2 != null && from_sqs != null) {
                            final FromClause fromClause2 = from_sqs.getFromClause();
                            if (fromClause2 != null) {
                                final Vector fromItems2 = fromClause2.getFromItemList();
                                if (fromItems2 != null) {
                                    for (int k = 0; k < fromItems2.size(); ++k) {
                                        if (fromItems2.get(k) instanceof FromTable && tableSpecificMapping.containsKey(fromItems2.get(k).getTableName().toString().toLowerCase())) {
                                            final Hashtable column = tableSpecificMapping.get(fromItems2.get(k).getTableName().toString().toLowerCase());
                                            if (column != null) {
                                                final String newDatatypeName2 = column.get(fromColName2.toLowerCase());
                                                if (newDatatypeName2 != null) {
                                                    if (newDatatypeName2.indexOf("(") != -1) {
                                                        dataType.setDatatypeName(newDatatypeName2.substring(0, newDatatypeName2.indexOf("(")));
                                                        dataType.setOpenBrace("(");
                                                        dataType.setClosedBrace(")");
                                                        dataType.setSize(newDatatypeName2.substring(newDatatypeName2.indexOf("(") + 1, newDatatypeName2.indexOf(")")));
                                                        if (dataType instanceof QuotedIdentifierDatatype) {
                                                            ((QuotedIdentifierDatatype)dataType).setPrecision(newDatatypeName2.substring(newDatatypeName2.indexOf("(") + 1, newDatatypeName2.indexOf(")")));
                                                        }
                                                    }
                                                    else {
                                                        dataType.setDatatypeName(newDatatypeName2);
                                                    }
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    final Hashtable globalMapping = mapping.getGlobalDatatypeMapping();
                    if (globalMapping != null) {
                        final String origDatatypeName = datatypeName;
                        if (dataType.getOpenBrace() != null) {
                            datatypeName = datatypeName + "(" + dataType.getSize() + ")";
                        }
                        if (globalMapping.containsKey(datatypeName.toLowerCase())) {
                            final String newDatatypeName3 = globalMapping.get(datatypeName.toLowerCase());
                            if (newDatatypeName3.indexOf("(") != -1) {
                                dataType.setDatatypeName(newDatatypeName3.substring(0, newDatatypeName3.indexOf("(")));
                                dataType.setOpenBrace("(");
                                dataType.setClosedBrace(")");
                                dataType.setSize(newDatatypeName3.substring(newDatatypeName3.indexOf("(") + 1, newDatatypeName3.indexOf(")")));
                                if (dataType instanceof QuotedIdentifierDatatype) {
                                    ((QuotedIdentifierDatatype)dataType).setPrecision(newDatatypeName3.substring(newDatatypeName3.indexOf("(") + 1, newDatatypeName3.indexOf(")")));
                                }
                            }
                            else if (datatypeName.indexOf("(") != -1) {
                                dataType.setDatatypeName(newDatatypeName3);
                                dataType.setOpenBrace(null);
                                dataType.setClosedBrace(null);
                                dataType.setSize(null);
                            }
                            else {
                                dataType.setDatatypeName(newDatatypeName3);
                            }
                            return true;
                        }
                        if (globalMapping.containsKey(origDatatypeName.toLowerCase())) {
                            final String newDatatypeName3 = globalMapping.get(origDatatypeName.toLowerCase());
                            dataType.setDatatypeName(newDatatypeName3);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void toVectorWise(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final Vector arguments = new Vector();
        if (this.functionArguments.elementAt(1) instanceof BinClass) {
            final StringBuffer cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                cast.append(this.functionArguments.elementAt(0).toVectorWiseSelect(to_sqs, from_sqs).toString());
            }
            else {
                cast.append(this.functionArguments.elementAt(0).toString());
            }
            this.functionName.setColumnName("" + (Object)cast);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionArguments.elementAt(1) instanceof DateClass) {
            final StringBuffer cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                this.handleStringLiteralForDateTime(from_sqs, 0, false);
                cast.append(this.functionArguments.elementAt(0).toVectorWiseSelect(to_sqs, from_sqs).toString());
            }
            else {
                cast.append(this.functionArguments.elementAt(0).toString());
            }
            String dataType = "DATE";
            if (this.functionArguments.elementAt(1).toString().replaceAll(" ", "").equalsIgnoreCase("DATETIME")) {
                dataType = "TIMESTAMP";
            }
            this.functionName.setColumnName("CAST(" + (Object)cast + " AS " + dataType + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
            final StringBuffer cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                cast.append(this.functionArguments.elementAt(0).toVectorWiseSelect(to_sqs, from_sqs).toString());
            }
            else {
                cast.append(this.functionArguments.elementAt(0).toString());
            }
            this.functionName.setColumnName("CAST(" + (Object)cast + " as VARCHAR)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionArguments.elementAt(1).toString().trim().equalsIgnoreCase("signed")) {
            final StringBuffer cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
                cast.append(this.functionArguments.elementAt(0).toVectorWiseSelect(to_sqs, from_sqs).toString());
            }
            else {
                cast.append(this.functionArguments.elementAt(0).toString());
            }
            this.functionName.setColumnName("cast(" + (Object)cast + " as BIGINT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else if (this.functionArguments.elementAt(1).toString().replaceAll(" ", "").equalsIgnoreCase("decimal")) {
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i));
                }
            }
            this.functionName.setColumnName("cast(" + arguments.get(0) + " as DECIMAL(38,0))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
        }
        else {
            this.functionName.setColumnName("CAST");
            for (int i = 0; i < this.functionArguments.size(); ++i) {
                if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                    arguments.addElement(this.functionArguments.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs));
                }
                else {
                    arguments.addElement(this.functionArguments.elementAt(i));
                }
            }
            this.setFunctionArguments(arguments);
        }
    }
    
    public void toMysql(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.functionName.setColumnName("CAST");
        final Vector arguments = new Vector();
        for (int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                arguments.addElement(this.functionArguments.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                arguments.addElement(this.functionArguments.elementAt(i));
            }
        }
        this.setFunctionArguments(arguments);
    }
}
