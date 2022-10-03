package net.sf.jsqlparser.statement.select;

public abstract class SetOperation
{
    private SetOperationList.SetOperationType type;
    
    public SetOperation(final SetOperationList.SetOperationType type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return this.type.name();
    }
}
