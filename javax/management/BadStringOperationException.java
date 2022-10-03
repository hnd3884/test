package javax.management;

public class BadStringOperationException extends Exception
{
    private static final long serialVersionUID = 7802201238441662100L;
    private String op;
    
    public BadStringOperationException(final String op) {
        this.op = op;
    }
    
    @Override
    public String toString() {
        return "BadStringOperationException: " + this.op;
    }
}
