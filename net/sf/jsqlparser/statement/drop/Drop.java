package net.sf.jsqlparser.statement.drop;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.StatementVisitor;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class Drop implements Statement
{
    private String type;
    private Table name;
    private List<String> parameters;
    private boolean ifExists;
    
    public Drop() {
        this.ifExists = false;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public Table getName() {
        return this.name;
    }
    
    public List<String> getParameters() {
        return this.parameters;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setName(final Table string) {
        this.name = string;
    }
    
    public void setParameters(final List<String> list) {
        this.parameters = list;
    }
    
    public void setType(final String string) {
        this.type = string;
    }
    
    public boolean isIfExists() {
        return this.ifExists;
    }
    
    public void setIfExists(final boolean ifExists) {
        this.ifExists = ifExists;
    }
    
    @Override
    public String toString() {
        String sql = "DROP " + this.type + " " + (this.ifExists ? "IF EXISTS " : "") + this.name.toString();
        if (this.parameters != null && !this.parameters.isEmpty()) {
            sql = sql + " " + PlainSelect.getStringList(this.parameters);
        }
        return sql;
    }
}
