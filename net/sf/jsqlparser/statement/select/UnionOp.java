package net.sf.jsqlparser.statement.select;

public class UnionOp extends SetOperation
{
    private boolean distinct;
    private boolean all;
    
    public UnionOp() {
        super(SetOperationList.SetOperationType.UNION);
    }
    
    public boolean isAll() {
        return this.all;
    }
    
    public void setAll(final boolean all) {
        this.all = all;
    }
    
    public boolean isDistinct() {
        return this.distinct;
    }
    
    public void setDistinct(final boolean distinct) {
        this.distinct = distinct;
    }
    
    @Override
    public String toString() {
        String allDistinct = "";
        if (this.isAll()) {
            allDistinct = " ALL";
        }
        else if (this.isDistinct()) {
            allDistinct = " DISTINCT";
        }
        return super.toString() + allDistinct;
    }
}
