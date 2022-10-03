package org.apache.naming;

import javax.naming.RefAddr;
import javax.naming.StringRefAddr;

public class EjbRef extends AbstractRef
{
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.EjbFactory";
    public static final String TYPE = "type";
    public static final String REMOTE = "remote";
    public static final String LINK = "link";
    
    public EjbRef(final String ejbType, final String home, final String remote, final String link) {
        this(ejbType, home, remote, link, null, null);
    }
    
    public EjbRef(final String ejbType, final String home, final String remote, final String link, final String factory, final String factoryLocation) {
        super(home, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (ejbType != null) {
            refAddr = new StringRefAddr("type", ejbType);
            this.add(refAddr);
        }
        if (remote != null) {
            refAddr = new StringRefAddr("remote", remote);
            this.add(refAddr);
        }
        if (link != null) {
            refAddr = new StringRefAddr("link", link);
            this.add(refAddr);
        }
    }
    
    @Override
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.EjbFactory";
    }
}
