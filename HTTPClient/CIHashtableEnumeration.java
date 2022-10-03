package HTTPClient;

import java.util.Enumeration;

final class CIHashtableEnumeration implements Enumeration
{
    Enumeration HTEnum;
    
    public CIHashtableEnumeration(final Enumeration enum) {
        this.HTEnum = enum;
    }
    
    public boolean hasMoreElements() {
        return this.HTEnum.hasMoreElements();
    }
    
    public Object nextElement() {
        final Object tmp = this.HTEnum.nextElement();
        if (tmp instanceof CIString) {
            return ((CIString)tmp).getString();
        }
        return tmp;
    }
}
