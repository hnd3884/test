package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.schema.Column;
import java.util.List;

public class PivotXml extends Pivot
{
    private SelectBody inSelect;
    private boolean inAny;
    
    public PivotXml() {
        this.inAny = false;
    }
    
    @Override
    public void accept(final PivotVisitor pivotVisitor) {
        pivotVisitor.visit(this);
    }
    
    public SelectBody getInSelect() {
        return this.inSelect;
    }
    
    public void setInSelect(final SelectBody inSelect) {
        this.inSelect = inSelect;
    }
    
    public boolean isInAny() {
        return this.inAny;
    }
    
    public void setInAny(final boolean inAny) {
        this.inAny = inAny;
    }
    
    @Override
    public String toString() {
        final List<Column> forColumns = this.getForColumns();
        final String in = this.inAny ? "ANY" : ((this.inSelect == null) ? PlainSelect.getStringList(this.getInItems()) : this.inSelect.toString());
        return "PIVOT XML (" + PlainSelect.getStringList(this.getFunctionItems()) + " FOR " + PlainSelect.getStringList(forColumns, true, forColumns != null && forColumns.size() > 1) + " IN (" + in + "))";
    }
}
