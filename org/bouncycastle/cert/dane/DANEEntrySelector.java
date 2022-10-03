package org.bouncycastle.cert.dane;

import org.bouncycastle.util.Selector;

public class DANEEntrySelector implements Selector
{
    private final String domainName;
    
    DANEEntrySelector(final String domainName) {
        this.domainName = domainName;
    }
    
    public boolean match(final Object o) {
        return ((DANEEntry)o).getDomainName().equals(this.domainName);
    }
    
    public Object clone() {
        return this;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
}
