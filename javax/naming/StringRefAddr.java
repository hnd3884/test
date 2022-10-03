package javax.naming;

public class StringRefAddr extends RefAddr
{
    private String contents;
    private static final long serialVersionUID = -8913762495138505527L;
    
    public StringRefAddr(final String s, final String contents) {
        super(s);
        this.contents = contents;
    }
    
    @Override
    public Object getContent() {
        return this.contents;
    }
}
