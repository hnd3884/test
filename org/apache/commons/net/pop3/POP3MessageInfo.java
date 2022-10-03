package org.apache.commons.net.pop3;

public final class POP3MessageInfo
{
    public int number;
    public int size;
    public String identifier;
    
    public POP3MessageInfo() {
        this(0, null, 0);
    }
    
    public POP3MessageInfo(final int num, final int octets) {
        this(num, null, octets);
    }
    
    public POP3MessageInfo(final int num, final String uid) {
        this(num, uid, -1);
    }
    
    private POP3MessageInfo(final int num, final String uid, final int size) {
        this.number = num;
        this.size = size;
        this.identifier = uid;
    }
    
    @Override
    public String toString() {
        return "Number: " + this.number + ". Size: " + this.size + ". Id: " + this.identifier;
    }
}
