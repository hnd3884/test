package net.sf.jsqlparser.util;

import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import java.util.Iterator;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import java.util.LinkedList;
import java.util.List;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;

public class AddAliasesVisitor implements SelectVisitor, SelectItemVisitor
{
    private static final String NOT_SUPPORTED_YET = "Not supported yet.";
    private List<String> aliases;
    private boolean firstRun;
    private int counter;
    private String prefix;
    
    public AddAliasesVisitor() {
        this.aliases = new LinkedList<String>();
        this.firstRun = true;
        this.counter = 0;
        this.prefix = "A";
    }
    
    @Override
    public void visit(final PlainSelect plainSelect) {
        this.firstRun = true;
        this.counter = 0;
        this.aliases.clear();
        for (final SelectItem item : plainSelect.getSelectItems()) {
            item.accept(this);
        }
        this.firstRun = false;
        for (final SelectItem item : plainSelect.getSelectItems()) {
            item.accept(this);
        }
    }
    
    @Override
    public void visit(final SetOperationList setOpList) {
        for (final SelectBody select : setOpList.getSelects()) {
            select.accept(this);
        }
    }
    
    @Override
    public void visit(final AllTableColumns allTableColumns) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final SelectExpressionItem selectExpressionItem) {
        if (this.firstRun) {
            if (selectExpressionItem.getAlias() != null) {
                this.aliases.add(selectExpressionItem.getAlias().getName().toUpperCase());
            }
        }
        else if (selectExpressionItem.getAlias() == null) {
            String alias;
            do {
                alias = this.getNextAlias().toUpperCase();
            } while (this.aliases.contains(alias));
            this.aliases.add(alias);
            selectExpressionItem.setAlias(new Alias(alias));
        }
    }
    
    protected String getNextAlias() {
        ++this.counter;
        return this.prefix + this.counter;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    @Override
    public void visit(final WithItem withItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final AllColumns allColumns) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
