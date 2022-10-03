package org.apache.naming;

public class TransactionRef extends AbstractRef
{
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.TransactionFactory";
    
    public TransactionRef() {
        this(null, (String)null);
    }
    
    public TransactionRef(final String factory, final String factoryLocation) {
        super("javax.transaction.UserTransaction", factory, factoryLocation);
    }
    
    @Override
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.TransactionFactory";
    }
}
