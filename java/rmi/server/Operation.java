package java.rmi.server;

@Deprecated
public class Operation
{
    private String operation;
    
    @Deprecated
    public Operation(final String operation) {
        this.operation = operation;
    }
    
    @Deprecated
    public String getOperation() {
        return this.operation;
    }
    
    @Deprecated
    @Override
    public String toString() {
        return this.operation;
    }
}
