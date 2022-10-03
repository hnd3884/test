package javax.naming;

public class LinkRef extends Reference
{
    static final String linkClassName;
    static final String linkAddrType = "LinkAddress";
    private static final long serialVersionUID = -5386290613498931298L;
    
    public LinkRef(final Name name) {
        super(LinkRef.linkClassName, new StringRefAddr("LinkAddress", name.toString()));
    }
    
    public LinkRef(final String s) {
        super(LinkRef.linkClassName, new StringRefAddr("LinkAddress", s));
    }
    
    public String getLinkName() throws NamingException {
        if (this.className != null && this.className.equals(LinkRef.linkClassName)) {
            final RefAddr value = this.get("LinkAddress");
            if (value != null && value instanceof StringRefAddr) {
                return (String)((StringRefAddr)value).getContent();
            }
        }
        throw new MalformedLinkException();
    }
    
    static {
        linkClassName = LinkRef.class.getName();
    }
}
