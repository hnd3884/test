package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class CheckConstraintClause implements ConstraintType
{
    private Vector constraintColumnNames;
    private String constraintName;
    private String openBrace;
    private String closedBrace;
    private UserObjectContext context;
    private String objectName;
    private String stmtTableName;
    WhereExpression whereExpression;
    
    public CheckConstraintClause() {
        this.context = null;
        this.objectName = null;
    }
    
    public void setObjectName(final String name) {
        this.objectName = name;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public String getObjectName() {
        return this.objectName;
    }
    
    public void setConstraintColumnNames(final Vector constraintColumnNames) {
        this.constraintColumnNames = constraintColumnNames;
    }
    
    public void setConstraintName(final String constraintName) {
        this.constraintName = constraintName;
    }
    
    public void setWhereExpression(final WhereExpression whereExpression) {
        this.whereExpression = whereExpression;
    }
    
    public void setOpenBrace(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    public void setClosedBrace(final String closedBrace) {
        this.closedBrace = closedBrace;
    }
    
    public void setStmtTableName(final String stmtTableName) {
        this.stmtTableName = stmtTableName;
    }
    
    public Vector getConstraintColumnNames() {
        return this.constraintColumnNames;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    public WhereExpression getWhereExpression() {
        return this.whereExpression;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.constraintName != null) {
            sb.append(this.constraintName.toUpperCase() + " ");
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.constraintColumnNames != null) {
            for (int i = 0; i < this.constraintColumnNames.size(); ++i) {
                String col = this.constraintColumnNames.get(i).toString();
                if (this.objectName != null && this.context != null) {
                    final String s = this.objectName + "." + col;
                    final String sss = this.context.getEquivalent(s).toString();
                    if (!s.equals(sss)) {
                        col = sss;
                    }
                }
                if (i == 0) {
                    if (this.context != null) {
                        final String temp = this.context.getEquivalent(col).toString();
                        sb.append(temp);
                    }
                    else {
                        sb.append(col);
                    }
                }
                else if (this.context != null) {
                    final String temp = this.context.getEquivalent(col).toString();
                    sb.append(", " + temp);
                }
                else {
                    sb.append(", " + col);
                }
            }
        }
        if (this.whereExpression != null) {
            this.whereExpression.setObjectContext(this.context);
            sb.append(this.whereExpression.toString());
        }
        if (this.closedBrace != null) {
            sb.append(this.closedBrace + " ");
        }
        return sb.toString();
    }
    
    @Override
    public void toDB2String() throws ConvertException {
        this.getWhereExpression().setStmtTableName(this.stmtTableName);
        final WhereExpression newWhereExpression = this.getWhereExpression().toDB2Select(null, null);
        this.setWhereExpression(newWhereExpression);
    }
    
    @Override
    public void toMSSQLServerString() throws ConvertException {
        final WhereExpression newWhereExpression = this.getWhereExpression().toMSSQLServerSelect(null, null);
        this.setWhereExpression(newWhereExpression);
    }
    
    @Override
    public void toSybaseString() throws ConvertException {
        final WhereExpression newWhereExpression = this.getWhereExpression().toSybaseSelect(null, null);
        this.setWhereExpression(newWhereExpression);
    }
    
    @Override
    public void toOracleString() throws ConvertException {
        final WhereExpression newWhereExpression = this.getWhereExpression().toOracleSelect(null, null);
        final Vector whereItems = newWhereExpression.getWhereItems();
        for (int i = 0; i < whereItems.size(); ++i) {
            if (whereItems.elementAt(i) instanceof WhereItem) {
                final WhereItem wi1 = whereItems.elementAt(i);
                if (wi1 != null && wi1.getOperator().equalsIgnoreCase("IN")) {
                    final WhereColumn wc = wi1.getRightWhereExp();
                    if (wc != null) {
                        final Vector v = wc.getColumnExpression();
                        final Vector newVec = new Vector();
                        if (v.contains(",")) {
                            for (int k = 0; k < v.size(); ++k) {
                                String str = v.get(k).toString();
                                str = str.trim();
                                if (str.startsWith("\"") && str.endsWith("\"")) {
                                    str = str.substring(1, str.length() - 1);
                                    str = "'" + str + "'";
                                }
                                newVec.insertElementAt(str, k);
                            }
                        }
                        wc.setColumnExpression(newVec);
                    }
                }
            }
        }
        this.setWhereExpression(newWhereExpression);
    }
    
    @Override
    public void toPostgreSQLString() throws ConvertException {
        final WhereExpression newWhereExpression = this.getWhereExpression().toPostgreSQLSelect(null, null);
        this.setWhereExpression(newWhereExpression);
    }
    
    @Override
    public void toANSIString() throws ConvertException {
        final WhereExpression newWhereExpression = this.getWhereExpression().toANSISelect(null, null);
        this.setWhereExpression(newWhereExpression);
    }
    
    @Override
    public void toMySQLString() throws ConvertException {
        final WhereExpression newWhereExpression = this.getWhereExpression().toMySQLSelect(null, null);
        this.setWhereExpression(newWhereExpression);
    }
    
    @Override
    public void toInformixString() throws ConvertException {
        final WhereExpression newWhereExpression = this.getWhereExpression().toInformixSelect(null, null);
        this.setWhereExpression(newWhereExpression);
    }
    
    @Override
    public void toTimesTenString() throws ConvertException {
    }
    
    @Override
    public void toNetezzaString() throws ConvertException {
    }
    
    @Override
    public void toTeradataString() throws ConvertException {
        final WhereExpression newWhereExpression = this.getWhereExpression().toTeradataSelect(null, null);
        this.setWhereExpression(newWhereExpression);
    }
    
    public ConstraintType copyObjectValues() {
        final CheckConstraintClause dupCheckConstraintClause = new CheckConstraintClause();
        dupCheckConstraintClause.setClosedBrace(this.closedBrace);
        dupCheckConstraintClause.setConstraintColumnNames(this.getConstraintColumnNames());
        dupCheckConstraintClause.setConstraintName(this.getConstraintName());
        dupCheckConstraintClause.setOpenBrace(this.openBrace);
        dupCheckConstraintClause.setWhereExpression(this.getWhereExpression());
        dupCheckConstraintClause.setObjectContext(this.context);
        dupCheckConstraintClause.setObjectName(this.objectName);
        return dupCheckConstraintClause;
    }
}
