package net.sf.jsqlparser.statement.merge;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class Merge implements Statement
{
    private Table table;
    private Table usingTable;
    private SubSelect usingSelect;
    private Alias usingAlias;
    private Expression onCondition;
    private MergeInsert mergeInsert;
    private MergeUpdate mergeUpdate;
    private boolean insertFirst;
    
    public Merge() {
        this.insertFirst = false;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table name) {
        this.table = name;
    }
    
    public Table getUsingTable() {
        return this.usingTable;
    }
    
    public void setUsingTable(final Table usingTable) {
        this.usingTable = usingTable;
    }
    
    public SubSelect getUsingSelect() {
        return this.usingSelect;
    }
    
    public void setUsingSelect(final SubSelect usingSelect) {
        this.usingSelect = usingSelect;
        if (this.usingSelect != null) {
            this.usingSelect.setUseBrackets(false);
        }
    }
    
    public Alias getUsingAlias() {
        return this.usingAlias;
    }
    
    public void setUsingAlias(final Alias usingAlias) {
        this.usingAlias = usingAlias;
    }
    
    public Expression getOnCondition() {
        return this.onCondition;
    }
    
    public void setOnCondition(final Expression onCondition) {
        this.onCondition = onCondition;
    }
    
    public MergeInsert getMergeInsert() {
        return this.mergeInsert;
    }
    
    public void setMergeInsert(final MergeInsert insert) {
        this.mergeInsert = insert;
    }
    
    public MergeUpdate getMergeUpdate() {
        return this.mergeUpdate;
    }
    
    public void setMergeUpdate(final MergeUpdate mergeUpdate) {
        this.mergeUpdate = mergeUpdate;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public boolean isInsertFirst() {
        return this.insertFirst;
    }
    
    public void setInsertFirst(final boolean insertFirst) {
        this.insertFirst = insertFirst;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("MERGE INTO ");
        b.append(this.table);
        b.append(" USING ");
        if (this.usingTable != null) {
            b.append(this.usingTable.toString());
        }
        else if (this.usingSelect != null) {
            b.append("(").append(this.usingSelect.toString()).append(")");
        }
        if (this.usingAlias != null) {
            b.append(this.usingAlias.toString());
        }
        b.append(" ON (");
        b.append(this.onCondition);
        b.append(")");
        if (this.insertFirst && this.mergeInsert != null) {
            b.append(this.mergeInsert.toString());
        }
        if (this.mergeUpdate != null) {
            b.append(this.mergeUpdate.toString());
        }
        if (!this.insertFirst && this.mergeInsert != null) {
            b.append(this.mergeInsert.toString());
        }
        return b.toString();
    }
}
