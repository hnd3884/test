package javax.management.relation;

import javax.management.JMException;

public class RelationException extends JMException
{
    private static final long serialVersionUID = 5434016005679159613L;
    
    public RelationException() {
    }
    
    public RelationException(final String s) {
        super(s);
    }
}
