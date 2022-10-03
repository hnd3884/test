package net.sf.jsqlparser.util;

import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import java.util.Iterator;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.BinaryExpression;
import java.util.LinkedList;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import java.util.List;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;

public abstract class ConnectExpressionsVisitor implements SelectVisitor, SelectItemVisitor
{
    private String alias;
    private final List<SelectExpressionItem> itemsExpr;
    
    public ConnectExpressionsVisitor() {
        this.alias = "expr";
        this.itemsExpr = new LinkedList<SelectExpressionItem>();
    }
    
    public ConnectExpressionsVisitor(final String alias) {
        this.alias = "expr";
        this.itemsExpr = new LinkedList<SelectExpressionItem>();
        this.alias = alias;
    }
    
    protected abstract BinaryExpression createBinaryExpression();
    
    @Override
    public void visit(final PlainSelect plainSelect) {
        for (final SelectItem item : plainSelect.getSelectItems()) {
            item.accept(this);
        }
        if (this.itemsExpr.size() > 1) {
            BinaryExpression binExpr = this.createBinaryExpression();
            binExpr.setLeftExpression(this.itemsExpr.get(0).getExpression());
            for (int i = 1; i < this.itemsExpr.size() - 1; ++i) {
                binExpr.setRightExpression(this.itemsExpr.get(i).getExpression());
                final BinaryExpression binExpr2 = this.createBinaryExpression();
                binExpr2.setLeftExpression(binExpr);
                binExpr = binExpr2;
            }
            binExpr.setRightExpression(this.itemsExpr.get(this.itemsExpr.size() - 1).getExpression());
            final SelectExpressionItem sei = new SelectExpressionItem();
            sei.setExpression(binExpr);
            plainSelect.getSelectItems().clear();
            plainSelect.getSelectItems().add(sei);
        }
        plainSelect.getSelectItems().get(0).setAlias(new Alias(this.alias));
    }
    
    @Override
    public void visit(final SetOperationList setOpList) {
        for (final SelectBody select : setOpList.getSelects()) {
            select.accept(this);
        }
    }
    
    @Override
    public void visit(final WithItem withItem) {
    }
    
    @Override
    public void visit(final AllTableColumns allTableColumns) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final AllColumns allColumns) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final SelectExpressionItem selectExpressionItem) {
        this.itemsExpr.add(selectExpressionItem);
    }
}
