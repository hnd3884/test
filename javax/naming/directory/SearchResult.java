package javax.naming.directory;

import javax.naming.Binding;

public class SearchResult extends Binding
{
    private Attributes attrs;
    private static final long serialVersionUID = -9158063327699723172L;
    
    public SearchResult(final String s, final Object o, final Attributes attrs) {
        super(s, o);
        this.attrs = attrs;
    }
    
    public SearchResult(final String s, final Object o, final Attributes attrs, final boolean b) {
        super(s, o, b);
        this.attrs = attrs;
    }
    
    public SearchResult(final String s, final String s2, final Object o, final Attributes attrs) {
        super(s, s2, o);
        this.attrs = attrs;
    }
    
    public SearchResult(final String s, final String s2, final Object o, final Attributes attrs, final boolean b) {
        super(s, s2, o, b);
        this.attrs = attrs;
    }
    
    public Attributes getAttributes() {
        return this.attrs;
    }
    
    public void setAttributes(final Attributes attrs) {
        this.attrs = attrs;
    }
    
    @Override
    public String toString() {
        return super.toString() + ":" + this.getAttributes();
    }
}
