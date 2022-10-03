package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.JdbcParameter;

public class Skip
{
    private Long rowCount;
    private JdbcParameter jdbcParameter;
    private String variable;
    
    public Long getRowCount() {
        return this.rowCount;
    }
    
    public void setRowCount(final Long rowCount) {
        this.rowCount = rowCount;
    }
    
    public JdbcParameter getJdbcParameter() {
        return this.jdbcParameter;
    }
    
    public void setJdbcParameter(final JdbcParameter jdbcParameter) {
        this.jdbcParameter = jdbcParameter;
    }
    
    public String getVariable() {
        return this.variable;
    }
    
    public void setVariable(final String variable) {
        this.variable = variable;
    }
    
    @Override
    public String toString() {
        String result = "SKIP ";
        if (this.rowCount != null) {
            result += this.rowCount;
        }
        else if (this.jdbcParameter != null) {
            result += this.jdbcParameter.toString();
        }
        else if (this.variable != null) {
            result += this.variable;
        }
        return result;
    }
}
