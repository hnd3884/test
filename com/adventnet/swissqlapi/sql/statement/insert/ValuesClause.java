package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Vector;
import java.util.List;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.ArrayList;

public class ValuesClause
{
    private String values;
    private String default_String;
    private ArrayList valuesList;
    private UserObjectContext context;
    private TableColumn tableColumn;
    private InsertClause insertClause;
    private ArrayList insertValList;
    private CommentClass commentObj;
    private InsertQueryStatement insertQueryStmt;
    
    public ValuesClause() {
        this.context = null;
        this.tableColumn = new TableColumn();
        this.valuesList = new ArrayList();
        this.values = new String();
        this.default_String = new String();
    }
    
    public void setInsertQueryStatement(final InsertQueryStatement iq) {
        this.insertQueryStmt = iq;
    }
    
    public void setValues(final String s) {
        this.values = s;
    }
    
    public void setDefault(final String s) {
        this.default_String = s;
    }
    
    public void setValuesList(final ArrayList v) {
        this.valuesList = v;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setInsertValList(final ArrayList insertValList) {
        this.insertValList = insertValList;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public String getValues() {
        return this.values;
    }
    
    public String getDefault() {
        return this.default_String;
    }
    
    public ArrayList getValuesList() {
        return this.valuesList;
    }
    
    public ValuesClause toOracle() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        dupValuesClause.setCommentClass(this.commentObj);
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        final ArrayList insertStmts = new ArrayList();
        boolean bracesOpen = false;
        final int exValListSize = getExistingValuesList.size();
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                if (sc.getColumnExpression() != null) {
                    final Vector colExp = sc.getColumnExpression();
                    if (colExp.get(0) instanceof TableColumn) {
                        final TableColumn table_Column_Val = colExp.get(0);
                        if (table_Column_Val.getColumnName() != null) {
                            final String column_Name_Var = table_Column_Val.getColumnName();
                            if (column_Name_Var.equalsIgnoreCase("NEXTVAL")) {
                                if (i + 2 < getExistingValuesList.size() && getExistingValuesList.get(i + 1) instanceof SelectColumn) {
                                    final SelectColumn sc2 = getExistingValuesList.get(i + 1);
                                    final Vector columnExpression = sc2.getColumnExpression();
                                    for (int count_Var = 0; count_Var < columnExpression.size(); ++count_Var) {
                                        if (columnExpression.elementAt(count_Var) instanceof TableColumn) {
                                            final TableColumn tc = columnExpression.elementAt(count_Var);
                                            if (tc.getColumnName() != null) {
                                                String column_Name = tc.getColumnName();
                                                if (column_Name.equalsIgnoreCase("FOR")) {
                                                    column_Name = "FOR";
                                                }
                                                if (getExistingValuesList.get(i + 2) instanceof SelectColumn) {
                                                    final SelectColumn sc3 = getExistingValuesList.get(i + 2);
                                                    final Vector columnExpression_Vector = sc3.getColumnExpression();
                                                    for (int count_Var_Index = 0; count_Var_Index < columnExpression_Vector.size(); ++count_Var_Index) {
                                                        if (columnExpression_Vector.elementAt(count_Var_Index) instanceof TableColumn) {
                                                            final TableColumn tc_Temp = columnExpression_Vector.elementAt(count_Var_Index);
                                                            if (tc.getColumnName() != null) {
                                                                final String column_Name_temp = tc_Temp.getColumnName();
                                                                table_Column_Val.setTableName(column_Name_temp);
                                                                table_Column_Val.setColumnName("NEXTVAL");
                                                                ++i;
                                                                ++i;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if (column_Name_Var.equalsIgnoreCase("NEXT") && i + 3 < getExistingValuesList.size() && getExistingValuesList.get(i + 1) instanceof SelectColumn) {
                                final SelectColumn sc2 = getExistingValuesList.get(i + 1);
                                final Vector columnExpression = sc2.getColumnExpression();
                                for (int count_Var = 0; count_Var < columnExpression.size(); ++count_Var) {
                                    if (columnExpression.elementAt(count_Var) instanceof TableColumn) {
                                        final TableColumn tc = columnExpression.elementAt(count_Var);
                                        if (tc.getColumnName() != null) {
                                            final String column_Name = tc.getColumnName();
                                            if (column_Name.equalsIgnoreCase("VALUE") && getExistingValuesList.get(i + 2) instanceof SelectColumn) {
                                                final SelectColumn sc3 = getExistingValuesList.get(i + 2);
                                                final Vector columnExpression_Vector = sc3.getColumnExpression();
                                                for (int count_Var_Index = 0; count_Var_Index < columnExpression_Vector.size(); ++count_Var_Index) {
                                                    if (columnExpression_Vector.elementAt(count_Var_Index) instanceof TableColumn) {
                                                        final TableColumn tc_Temp = columnExpression_Vector.elementAt(count_Var_Index);
                                                        if (tc.getColumnName() != null) {
                                                            final String column_Name_temp = tc_Temp.getColumnName();
                                                            if (column_Name_temp.equalsIgnoreCase("FOR") && getExistingValuesList.get(i + 3) instanceof SelectColumn) {
                                                                final SelectColumn sc4 = getExistingValuesList.get(i + 3);
                                                                final Vector columnExpression_Vector2 = sc4.getColumnExpression();
                                                                for (int count_Var_Index2 = 0; count_Var_Index2 < columnExpression_Vector2.size(); ++count_Var_Index2) {
                                                                    if (columnExpression_Vector2.elementAt(count_Var_Index2) instanceof TableColumn) {
                                                                        final TableColumn tc_Temp2 = columnExpression_Vector2.elementAt(count_Var_Index2);
                                                                        if (tc_Temp2.getColumnName() != null) {
                                                                            final String column_Name_temp2 = tc_Temp2.getColumnName();
                                                                            table_Column_Val.setTableName(column_Name_temp2);
                                                                            table_Column_Val.setColumnName("NEXTVAL");
                                                                            ++i;
                                                                            ++i;
                                                                            ++i;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                final SelectColumn tempSelectColumn = sc.toOracleSelect(null, null);
                String tempString = tempSelectColumn.toString();
                if (tempString.indexOf("\"") != -1) {
                    tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
                    newValuesList.add(tempString);
                }
                else {
                    final String defSC = tempString.trim().toLowerCase();
                    if (!defSC.equals("default")) {
                        newValuesList.add(tempSelectColumn);
                    }
                    else if (InsertClause.isOracleDEFColTruncated) {
                        if (getExistingValuesList.size() != i + 2) {
                            ++i;
                        }
                        else {
                            newValuesList.remove(newValuesList.size() - 1);
                        }
                    }
                    else {
                        newValuesList.add(tempSelectColumn);
                    }
                }
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                newValuesList.add(subQueryInsideValues.toOracleSelect());
            }
            else {
                final Object obj = getExistingValuesList.get(i);
                if (bracesOpen && obj.toString().equalsIgnoreCase("(")) {
                    this.handleMultipleValuesList(insertStmts, getExistingValuesList.subList(i, exValListSize));
                    break;
                }
                if (obj.toString().equalsIgnoreCase("(")) {
                    bracesOpen = true;
                }
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        this.insertQueryStmt.setMultipleValuesInsertStmts(insertStmts);
        InsertClause.isOracleDEFColTruncated = false;
        return dupValuesClause;
    }
    
    public ValuesClause toMSSQLServer() throws ConvertException {
        final ArrayList insertStmts = new ArrayList();
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        boolean bracesOpen = false;
        for (int exValListSize = getExistingValuesList.size(), i = 0; i < exValListSize; ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                final SelectColumn tempSelectColumn = sc.toMSSQLServerSelect(null, null);
                String tempString = tempSelectColumn.toString();
                if (tempString.indexOf("\"") != -1) {
                    tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
                    newValuesList.add(tempString);
                }
                else if (tempString.trim().startsWith("@") && SwisSQLOptions.EnableDeltekSpecificConversions) {
                    tempString = tempString.replaceFirst("@", ":");
                    newValuesList.add(tempString);
                }
                else {
                    newValuesList.add(tempSelectColumn);
                }
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                newValuesList.add(subQueryInsideValues.toMSSQLServerSelect());
            }
            else {
                final Object obj = getExistingValuesList.get(i);
                if (bracesOpen && obj.toString().equalsIgnoreCase("(")) {
                    this.handleMultipleValuesList(insertStmts, getExistingValuesList.subList(i, exValListSize));
                    break;
                }
                if (obj.toString().equalsIgnoreCase("(")) {
                    bracesOpen = true;
                }
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        this.insertQueryStmt.setMultipleValuesInsertStmts(insertStmts);
        return dupValuesClause;
    }
    
    private void handleMultipleValuesList(final ArrayList insertStmts, final List multiValList) throws ConvertException {
        final int multiValListSize = multiValList.size();
        final int firstOpenBraceIndex = multiValList.indexOf("(");
        final int firstCloseBraceIndex = multiValList.indexOf(")");
        final int lastCloseBraceIndex = multiValList.lastIndexOf(")");
        final List firstValuesSet = multiValList.subList(firstOpenBraceIndex, firstCloseBraceIndex + 1);
        final int firstValuesSetSize = firstValuesSet.size();
        final ArrayList newValuesList = new ArrayList();
        for (int i = 0; i < firstValuesSetSize; ++i) {
            if (firstValuesSet.get(i) instanceof SelectColumn) {
                final SelectColumn sc = firstValuesSet.get(i);
                final SelectColumn tempSelectColumn = sc.toMSSQLServerSelect(null, null);
                String tempString = tempSelectColumn.toString();
                if (tempString.indexOf("\"") != -1) {
                    tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
                    newValuesList.add(tempString);
                }
                else {
                    newValuesList.add(tempSelectColumn);
                }
            }
            else {
                newValuesList.add(firstValuesSet.get(i));
            }
        }
        final InsertQueryStatement iq = new InsertQueryStatement();
        iq.setInsertClause(this.insertQueryStmt.getInsertClause());
        final ValuesClause valuesCl = new ValuesClause();
        valuesCl.setValues("VALUES");
        valuesCl.setValuesList(newValuesList);
        iq.setValuesClause(valuesCl);
        insertStmts.add(iq);
        if (firstCloseBraceIndex == lastCloseBraceIndex) {
            return;
        }
        this.handleMultipleValuesList(insertStmts, multiValList.subList(firstCloseBraceIndex + 1, multiValListSize));
    }
    
    public ValuesClause toDB2() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        int count = 0;
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                if (this.insertValList != null && this.insertValList.size() > 0) {
                    sc.setCorrespondingTableColumn(this.insertValList.get(count));
                    ++count;
                }
                if (sc.getColumnExpression() != null) {
                    final Vector columnExpVector = sc.getColumnExpression();
                    if (columnExpVector.get(0) instanceof TableColumn) {
                        final TableColumn tableColumn = columnExpVector.get(0);
                        if (tableColumn.getColumnName().toUpperCase().equalsIgnoreCase("NEXTVAL")) {
                            final String tableName = tableColumn.getTableName();
                            final String columnName = tableColumn.getColumnName();
                            if (tableName != null) {
                                tableColumn.setTableName("NEXT VALUE FOR ");
                                tableColumn.setColumnName(tableName);
                            }
                        }
                    }
                    else if (columnExpVector.get(0) instanceof String) {
                        String str = columnExpVector.get(0);
                        if (str.trim().startsWith("'") && str.indexOf("\n") != -1) {
                            str = StringFunctions.replaceAll(" ", "\n", str);
                            columnExpVector.setElementAt(str, 0);
                        }
                        if (str.trim().startsWith("'") && str.indexOf("\r") != -1) {
                            str = StringFunctions.replaceAll(" ", "\r", str);
                            columnExpVector.setElementAt(str, 0);
                        }
                    }
                }
                if (sc.toString().equalsIgnoreCase("NULL")) {
                    newValuesList.add(sc);
                }
                else {
                    newValuesList.add(sc.toDB2Select(null, null));
                }
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                newValuesList.add(subQueryInsideValues.toDB2Select());
            }
            else {
                if (getExistingValuesList.get(i) instanceof String) {
                    final String checkNullCast = getExistingValuesList.get(i);
                    if (checkNullCast.equalsIgnoreCase("NULL") || checkNullCast.trim().indexOf("CAST( NULL AS") != -1) {
                        ++count;
                    }
                }
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        return dupValuesClause;
    }
    
    public ValuesClause toANSI() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                final SelectColumn tempSelectColumn = sc.toANSISelect(null, null);
                String tempString = tempSelectColumn.toString();
                if (tempString.indexOf("\"") != -1) {
                    tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
                    newValuesList.add(tempString);
                }
                else {
                    newValuesList.add(tempSelectColumn);
                }
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                newValuesList.add(subQueryInsideValues.toANSISelect());
            }
            else {
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        return dupValuesClause;
    }
    
    public ValuesClause toTeradata() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                final SelectColumn tempSelectColumn = sc.toTeradataSelect(null, null);
                final String tempString = tempSelectColumn.toString();
                newValuesList.add(tempSelectColumn);
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                newValuesList.add(subQueryInsideValues.toTeradataSelect());
            }
            else {
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        return dupValuesClause;
    }
    
    public ValuesClause toPostgreSQL() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                newValuesList.add(sc.toPostgreSQLSelect(null, null));
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                newValuesList.add(subQueryInsideValues.toPostgreSQLSelect());
            }
            else {
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        return dupValuesClause;
    }
    
    public ValuesClause toInformix() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                newValuesList.add(sc.toInformixSelect(null, null));
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                newValuesList.add(subQueryInsideValues.toInformixSelect());
            }
            else {
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        return dupValuesClause;
    }
    
    public ValuesClause toMySQL() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                newValuesList.add(sc.toMySQLSelect(null, null));
            }
            else {
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        return dupValuesClause;
    }
    
    public ValuesClause toSybase() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                sc.setObjectContext(this.context);
                final SelectColumn tempSelectColumn = sc.toSybaseSelect(null, null);
                String tempString = tempSelectColumn.toString();
                if (tempString.indexOf("\"") != -1) {
                    tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
                    newValuesList.add(tempString);
                }
                else {
                    newValuesList.add(tempSelectColumn);
                }
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                subQueryInsideValues.setObjectContext(this.context);
                newValuesList.add(subQueryInsideValues.toSybaseSelect());
            }
            else {
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        return dupValuesClause;
    }
    
    public ValuesClause toTimesTen() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        boolean isHexaValue = false;
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                sc.setObjectContext(this.context);
                final SelectColumn tempSelectColumn = sc.toTimesTenSelect(null, null);
                String tempString = tempSelectColumn.toString();
                if (tempString.startsWith("\"") && tempString.endsWith("\"")) {
                    tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
                    newValuesList.add(tempString);
                }
                else if (tempString.trim().equals("0")) {
                    isHexaValue = true;
                    newValuesList.add(tempSelectColumn);
                }
                else if (tempString.trim().toLowerCase().startsWith("x") && isHexaValue && sc.getColumnExpression().size() == 1) {
                    final String hexaValue = newValuesList.get(newValuesList.size() - 1).toString().trim();
                    newValuesList.set(newValuesList.size() - 1, hexaValue + tempString.trim());
                }
                else {
                    newValuesList.add(tempSelectColumn);
                }
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                subQueryInsideValues.setObjectContext(this.context);
                newValuesList.add(subQueryInsideValues.toTimesTenSelect());
            }
            else {
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        return dupValuesClause;
    }
    
    public ValuesClause toNetezza() throws ConvertException {
        final ValuesClause dupValuesClause = this.copyObjectValues();
        final ArrayList newValuesList = new ArrayList();
        final ArrayList getExistingValuesList = dupValuesClause.getValuesList();
        for (int i = 0; i < getExistingValuesList.size(); ++i) {
            if (getExistingValuesList.get(i) instanceof SelectColumn) {
                final SelectColumn sc = getExistingValuesList.get(i);
                final SelectColumn tempSelectColumn = sc.toNetezzaSelect(null, null);
                String tempString = tempSelectColumn.toString();
                if (tempString.indexOf("\"") != -1) {
                    tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
                    newValuesList.add(tempString);
                }
                else {
                    newValuesList.add(tempSelectColumn);
                }
            }
            else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryInsideValues = getExistingValuesList.get(i);
                newValuesList.add(subQueryInsideValues.toNetezzaSelect());
            }
            else {
                newValuesList.add(getExistingValuesList.get(i));
            }
        }
        dupValuesClause.setValuesList(newValuesList);
        return dupValuesClause;
    }
    
    public ValuesClause copyObjectValues() {
        final ValuesClause valuesClause = new ValuesClause();
        valuesClause.setValuesList(this.getValuesList());
        valuesClause.setDefault(this.default_String);
        valuesClause.setValues(this.values);
        valuesClause.setObjectContext(this.context);
        return valuesClause;
    }
    
    public String removeIndent(String s_ri) {
        s_ri = s_ri.replace('\n', ' ');
        s_ri = s_ri.replace('\t', ' ');
        return s_ri;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.default_String != null) {
            sb.append(this.default_String.toUpperCase());
            sb.append(" ");
        }
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.values != null) {
            sb.append(this.values.toUpperCase());
            sb.append(" ");
        }
        if (this.valuesList != null) {
            final int size = this.valuesList.size();
            SelectQueryStatement.beautyTabCount += 2;
            for (int i = 0; i < size; ++i) {
                String isCommaOrOpenBrace = "";
                if (this.valuesList.get(i) instanceof String) {
                    isCommaOrOpenBrace = this.valuesList.get(i);
                }
                if (isCommaOrOpenBrace.equals("(")) {
                    for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                        sb.append("\t");
                    }
                }
                sb.append(this.valuesList.get(i) + " ");
                if (isCommaOrOpenBrace.equals(",")) {
                    sb.append("\n");
                    for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                        sb.append("\t");
                    }
                }
            }
            SelectQueryStatement.beautyTabCount -= 2;
        }
        return sb.toString();
    }
}
