package javax.management;

public class JMException extends Exception
{
    private static final long serialVersionUID = 350520924977331825L;
    
    public JMException() {
    }
    
    public JMException(final String s) {
        super(s);
    }
}