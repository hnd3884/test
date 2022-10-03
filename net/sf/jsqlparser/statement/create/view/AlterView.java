package net.sf.jsqlparser.statement.create.view;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.StatementVisitor;
import java.util.List;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class AlterView implements Statement
{
    private Table view;
    private SelectBody selectBody;
    private List<String> columnNames;
    
    public AlterView() {
        this.columnNames = null;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public Table getView() {
        return this.view;
    }
    
    public void setView(final Table view) {
        this.view = view;
    }
    
    public SelectBody getSelectBody() {
        return this.selectBody;
    }
    
    public void setSelectBody(final SelectBody selectBody) {
        this.selectBody = selectBody;
    }
    
    public List<String> getColumnNames() {
        return this.columnNames;
    }
    
    public void setColumnNames(final List<String> columnNames) {
        this.columnNames = columnNames;
    }
    
    @Override
    public String toString() {
        final StringBuilder sql = new StringBuilder("ALTER ");
        sql.append("VIEW ");
        sql.append(this.view);
        if (this.columnNames != null) {
            sql.append(PlainSelect.getStringList(this.columnNames, true, true));
        }
        sql.append(" AS ").append(this.selectBody);
        return sql.toString();
    }
}
