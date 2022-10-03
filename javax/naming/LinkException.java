package javax.naming;

public class LinkException extends NamingException
{
    protected Name linkResolvedName;
    protected Object linkResolvedObj;
    protected Name linkRemainingName;
    protected String linkExplanation;
    private static final long serialVersionUID = -7967662604076777712L;
    
    public LinkException(final String s) {
        super(s);
        this.linkResolvedName = null;
        this.linkResolvedObj = null;
        this.linkRemainingName = null;
        this.linkExplanation = null;
    }
    
    public LinkException() {
        this.linkResolvedName = null;
        this.linkResolvedObj = null;
        this.linkRemainingName = null;
        this.linkExplanation = null;
    }
    
    public Name getLinkResolvedName() {
        return this.linkResolvedName;
    }
    
    public Name getLinkRemainingName() {
        return this.linkRemainingName;
    }
    
    public Object getLinkResolvedObj() {
        return this.linkResolvedObj;
    }
    
    public String getLinkExplanation() {
        return this.linkExplanation;
    }
    
    public void setLinkExplanation(final String linkExplanation) {
        this.linkExplanation = linkExplanation;
    }
    
    public void setLinkResolvedName(final Name name) {
        if (name != null) {
            this.linkResolvedName = (Name)name.clone();
        }
        else {
            this.linkResolvedName = null;
        }
    }
    
    public void setLinkRemainingName(final Name name) {
        if (name != null) {
            this.linkRemainingName = (Name)name.clone();
        }
        else {
            this.linkRemainingName = null;
        }
    }
    
    public void setLinkResolvedObj(final Object linkResolvedObj) {
        this.linkResolvedObj = linkResolvedObj;
    }
    
    @Override
    public String toString() {
        return super.toString() + "; Link Remaining Name: '" + this.linkRemainingName + "'";
    }
    
    @Override
    public String toString(final boolean b) {
        if (!b || this.linkResolvedObj == null) {
            return this.toString();
        }
        return this.toString() + "; Link Resolved Object: " + this.linkResolvedObj;
    }
}
