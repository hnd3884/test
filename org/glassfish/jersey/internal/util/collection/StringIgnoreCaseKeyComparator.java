package org.glassfish.jersey.internal.util.collection;

public class StringIgnoreCaseKeyComparator implements KeyComparator<String>
{
    private static final long serialVersionUID = 9106900325469360723L;
    public static final StringIgnoreCaseKeyComparator SINGLETON;
    
    @Override
    public int hash(final String k) {
        return k.toLowerCase().hashCode();
    }
    
    @Override
    public boolean equals(final String x, final String y) {
        return x.equalsIgnoreCase(y);
    }
    
    static {
        SINGLETON = new StringIgnoreCaseKeyComparator();
    }
}
