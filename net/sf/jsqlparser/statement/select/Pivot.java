package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.schema.Column;
import java.util.List;

public class Pivot
{
    private List<FunctionItem> functionItems;
    private List<Column> forColumns;
    private List<SelectExpressionItem> singleInItems;
    private List<ExpressionListItem> multiInItems;
    
    public void accept(final PivotVisitor pivotVisitor) {
        pivotVisitor.visit(this);
    }
    
    public List<SelectExpressionItem> getSingleInItems() {
        return this.singleInItems;
    }
    
    public void setSingleInItems(final List<SelectExpressionItem> singleInItems) {
        this.singleInItems = singleInItems;
    }
    
    public List<ExpressionListItem> getMultiInItems() {
        return this.multiInItems;
    }
    
    public void setMultiInItems(final List<ExpressionListItem> multiInItems) {
        this.multiInItems = multiInItems;
    }
    
    public List<FunctionItem> getFunctionItems() {
        return this.functionItems;
    }
    
    public void setFunctionItems(final List<FunctionItem> functionItems) {
        this.functionItems = functionItems;
    }
    
    public List<Column> getForColumns() {
        return this.forColumns;
    }
    
    public void setForColumns(final List<Column> forColumns) {
        this.forColumns = forColumns;
    }
    
    public List<?> getInItems() {
        return (List<?>)((this.singleInItems == null) ? this.multiInItems : this.singleInItems);
    }
    
    @Override
    public String toString() {
        return "PIVOT (" + PlainSelect.getStringList(this.functionItems) + " FOR " + PlainSelect.getStringList(this.forColumns, true, this.forColumns != null && this.forColumns.size() > 1) + " IN " + PlainSelect.getStringList(this.getInItems(), true, true) + ")";
    }
}
