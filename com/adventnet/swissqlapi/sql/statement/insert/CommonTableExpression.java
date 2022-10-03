package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;

public class CommonTableExpression
{
    String with;
    TableObject viewName;
    ArrayList columnList;
    String as;
    SelectQueryStatement sqs;
    
    public CommonTableExpression() {
        this.columnList = new ArrayList();
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setViewName(final TableObject viewName) {
        this.viewName = viewName;
    }
    
    public void setColumnList(final ArrayList columnList) {
        this.columnList = columnList;
    }
    
    public void setAs(final String as) {
        this.as = as;
    }
    
    public void setSelectQueryStatement(final SelectQueryStatement sqs) {
        this.sqs = sqs;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public TableObject getViewName() {
        return this.viewName;
    }
    
    public ArrayList getColumnList() {
        return this.columnList;
    }
    
    public String getAs() {
        return this.as;
    }
    
    public SelectQueryStatement getSelectQueryStatement() {
        return this.sqs;
    }
    
    public CommonTableExpression toOracle(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        final CommonTableExpression commonTableExpr = this.copyObjectValues();
        final TableObject srcViewName = commonTableExpr.getViewName();
        srcViewName.toOracle();
        final ArrayList columnList = commonTableExpr.getColumnList();
        if (!columnList.isEmpty()) {
            for (int i = 0; i < columnList.size(); ++i) {
                String columnName = columnList.get(i);
                if (!columnName.trim().equals(",") && !columnName.trim().equals("(") && !columnName.trim().equals(")")) {
                    columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(1), null, 1);
                }
                if ((columnName.startsWith("[") && columnName.endsWith("]")) || (columnName.startsWith("`") && columnName.endsWith("`"))) {
                    columnName = columnName.substring(1, columnName.length() - 1);
                }
                if (!columnName.startsWith("\"") && (SwisSQLOptions.retainQuotedIdentifierForOracle || columnName.indexOf(32) != -1)) {
                    columnName = "\"" + columnName + "\"";
                }
            }
        }
        final SelectQueryStatement srcSQS = commonTableExpr.getSelectQueryStatement();
        if (srcSQS != null) {
            commonTableExpr.setSelectQueryStatement(srcSQS.toOracleSelect());
        }
        return commonTableExpr;
    }
    
    public CommonTableExpression toMSSQLServer(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        return this;
    }
    
    public CommonTableExpression toSybase(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        return this;
    }
    
    public CommonTableExpression toDB2(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        return this;
    }
    
    public CommonTableExpression toMySQL(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        return this;
    }
    
    public CommonTableExpression toInformix(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        return this;
    }
    
    public CommonTableExpression toPostgreSQL(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        return this;
    }
    
    public CommonTableExpression toANSISQL(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        final CommonTableExpression commonTableExpr = this.copyObjectValues();
        final TableObject srcViewName = commonTableExpr.getViewName();
        srcViewName.toANSISQL();
        final ArrayList columnList = commonTableExpr.getColumnList();
        if (!columnList.isEmpty()) {
            for (int i = 0; i < columnList.size(); ++i) {
                String columnName = columnList.get(i);
                if (!columnName.trim().equals(",") && !columnName.trim().equals("(") && !columnName.trim().equals(")")) {
                    columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(8), null, 8);
                }
                if ((columnName.startsWith("[") && columnName.endsWith("]")) || (columnName.startsWith("`") && columnName.endsWith("`"))) {
                    columnName = columnName.substring(1, columnName.length() - 1);
                }
                if (!columnName.startsWith("\"") && columnName.indexOf(32) != -1) {
                    columnName = "\"" + columnName + "\"";
                }
            }
        }
        final SelectQueryStatement srcSQS = commonTableExpr.getSelectQueryStatement();
        if (srcSQS != null) {
            commonTableExpr.setSelectQueryStatement(srcSQS.toANSISelect());
        }
        return commonTableExpr;
    }
    
    public CommonTableExpression toTeradata(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        final CommonTableExpression commonTableExpr = this.copyObjectValues();
        final TableObject srcViewName = commonTableExpr.getViewName();
        srcViewName.toTeradata();
        final ArrayList columnList = commonTableExpr.getColumnList();
        if (!columnList.isEmpty()) {
            for (int i = 0; i < columnList.size(); ++i) {
                String columnName = columnList.get(i);
                if (!columnName.trim().equals(",") && !columnName.trim().equals("(") && !columnName.trim().equals(")")) {
                    columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(12), null, 12);
                }
                if ((columnName.startsWith("[") && columnName.endsWith("]")) || (columnName.startsWith("`") && columnName.endsWith("`"))) {
                    columnName = columnName.substring(1, columnName.length() - 1);
                }
                if (!columnName.startsWith("\"") && columnName.indexOf(32) != -1) {
                    columnName = "\"" + columnName + "\"";
                }
            }
        }
        final SelectQueryStatement srcSQS = commonTableExpr.getSelectQueryStatement();
        if (srcSQS != null) {
            commonTableExpr.setSelectQueryStatement(srcSQS.toTeradataSelect());
        }
        return commonTableExpr;
    }
    
    public CommonTableExpression toTimesTen(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        return this;
    }
    
    public CommonTableExpression toNetezza(final SwisSQLStatement fromSQL, final SwisSQLStatement toSQL) throws ConvertException {
        final CommonTableExpression commonTableExpr = this.copyObjectValues();
        final TableObject srcViewName = commonTableExpr.getViewName();
        srcViewName.toNetezza();
        final ArrayList columnList = commonTableExpr.getColumnList();
        if (!columnList.isEmpty()) {
            for (int i = 0; i < columnList.size(); ++i) {
                String columnName = columnList.get(i);
                if (!columnName.trim().equals(",") && !columnName.trim().equals("(") && !columnName.trim().equals(")")) {
                    columnName = CustomizeUtil.objectNamesToQuotedIdentifier(columnName, SwisSQLUtils.getKeywords(11), null, 11);
                }
                if ((columnName.startsWith("[") && columnName.endsWith("]")) || (columnName.startsWith("`") && columnName.endsWith("`"))) {
                    columnName = columnName.substring(1, columnName.length() - 1);
                }
                if (!columnName.startsWith("\"") && columnName.indexOf(32) != -1) {
                    columnName = "\"" + columnName + "\"";
                }
            }
        }
        final SelectQueryStatement srcSQS = commonTableExpr.getSelectQueryStatement();
        if (srcSQS != null) {
            commonTableExpr.setSelectQueryStatement(srcSQS.toNetezzaSelect());
        }
        return commonTableExpr;
    }
    
    private CommonTableExpression copyObjectValues() {
        final CommonTableExpression commonTableExpr = new CommonTableExpression();
        commonTableExpr.setWith(this.getWith());
        commonTableExpr.setViewName(this.getViewName());
        commonTableExpr.setColumnList(this.getColumnList());
        commonTableExpr.setAs(this.getAs());
        commonTableExpr.setSelectQueryStatement(this.getSelectQueryStatement());
        return commonTableExpr;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.with != null) {
            sb.append("WITH ");
        }
        if (this.viewName != null) {
            sb.append(this.viewName);
        }
        if (!this.columnList.isEmpty()) {
            for (int i = 0; i < this.columnList.size(); ++i) {
                sb.append(this.columnList.get(i));
            }
        }
        if (this.as != null) {
            sb.append(" AS ");
        }
        if (this.sqs != null) {
            sb.append("(" + this.sqs.toString().trim() + ")");
        }
        return sb.toString();
    }
}
