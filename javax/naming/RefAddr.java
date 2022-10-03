package javax.naming;

import java.io.Serializable;

public abstract class RefAddr implements Serializable
{
    protected String addrType;
    private static final long serialVersionUID = -1468165120479154358L;
    
    protected RefAddr(final String addrType) {
        this.addrType = addrType;
    }
    
    public String getType() {
        return this.addrType;
    }
    
    public abstract Object getContent();
    
    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof RefAddr) {
            final RefAddr refAddr = (RefAddr)o;
            if (this.addrType.compareTo(refAddr.addrType) == 0) {
                final Object content = this.getContent();
                final Object content2 = refAddr.getContent();
                if (content == content2) {
                    return true;
                }
                if (content != null) {
                    return content.equals(content2);
                }
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (this.getContent() == null) ? this.addrType.hashCode() : (this.addrType.hashCode() + this.getContent().hashCode());
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Type: " + this.addrType + "\n");
        sb.append("Content: " + this.getContent() + "\n");
        return sb.toString();
    }
}
