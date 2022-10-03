package javax.management;

public class MBeanRegistrationException extends MBeanException
{
    private static final long serialVersionUID = 4482382455277067805L;
    
    public MBeanRegistrationException(final Exception ex) {
        super(ex);
    }
    
    public MBeanRegistrationException(final Exception ex, final String s) {
        super(ex, s);
    }
}
