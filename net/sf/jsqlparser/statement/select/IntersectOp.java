package net.sf.jsqlparser.statement.select;

public class IntersectOp extends SetOperation
{
    public IntersectOp() {
        super(SetOperationList.SetOperationType.INTERSECT);
    }
}
