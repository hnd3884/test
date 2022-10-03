package net.sf.jsqlparser.schema;

import net.sf.jsqlparser.statement.select.IntoTableVisitor;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.expression.MySQLIndexHint;
import net.sf.jsqlparser.statement.select.Pivot;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

public class Table extends ASTNodeAccessImpl implements FromItem, MultiPartName
{
    private Database database;
    private String schemaName;
    private String name;
    private Alias alias;
    private Pivot pivot;
    private MySQLIndexHint hint;
    
    public Table() {
    }
    
    public Table(final String name) {
        this.name = name;
    }
    
    public Table(final String schemaName, final String name) {
        this.schemaName = schemaName;
        this.name = name;
    }
    
    public Table(final Database database, final String schemaName, final String name) {
        this.database = database;
        this.schemaName = schemaName;
        this.name = name;
    }
    
    public Database getDatabase() {
        return this.database;
    }
    
    public void setDatabase(final Database database) {
        this.database = database;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public void setSchemaName(final String string) {
        this.schemaName = string;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String string) {
        this.name = string;
    }
    
    @Override
    public Alias getAlias() {
        return this.alias;
    }
    
    @Override
    public void setAlias(final Alias alias) {
        this.alias = alias;
    }
    
    @Override
    public String getFullyQualifiedName() {
        String fqn = "";
        if (this.database != null) {
            fqn += this.database.getFullyQualifiedName();
        }
        if (!fqn.isEmpty()) {
            fqn += ".";
        }
        if (this.schemaName != null) {
            fqn += this.schemaName;
        }
        if (!fqn.isEmpty()) {
            fqn += ".";
        }
        if (this.name != null) {
            fqn += this.name;
        }
        return fqn;
    }
    
    @Override
    public void accept(final FromItemVisitor fromItemVisitor) {
        fromItemVisitor.visit(this);
    }
    
    public void accept(final IntoTableVisitor intoTableVisitor) {
        intoTableVisitor.visit(this);
    }
    
    @Override
    public Pivot getPivot() {
        return this.pivot;
    }
    
    @Override
    public void setPivot(final Pivot pivot) {
        this.pivot = pivot;
    }
    
    public MySQLIndexHint getIndexHint() {
        return this.hint;
    }
    
    public void setHint(final MySQLIndexHint hint) {
        this.hint = hint;
    }
    
    @Override
    public String toString() {
        return this.getFullyQualifiedName() + ((this.pivot != null) ? (" " + this.pivot) : "") + ((this.alias != null) ? this.alias.toString() : "") + ((this.hint != null) ? this.hint.toString() : "");
    }
}
