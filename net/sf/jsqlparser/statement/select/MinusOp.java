package net.sf.jsqlparser.statement.select;

public class MinusOp extends SetOperation
{
    public MinusOp() {
        super(SetOperationList.SetOperationType.MINUS);
    }
}
