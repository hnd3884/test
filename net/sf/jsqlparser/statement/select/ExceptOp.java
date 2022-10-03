package net.sf.jsqlparser.statement.select;

public class ExceptOp extends SetOperation
{
    public ExceptOp() {
        super(SetOperationList.SetOperationType.EXCEPT);
    }
}
