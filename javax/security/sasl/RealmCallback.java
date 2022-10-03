package javax.security.sasl;

import javax.security.auth.callback.TextInputCallback;

public class RealmCallback extends TextInputCallback
{
    private static final long serialVersionUID = -4342673378785456908L;
    
    public RealmCallback(final String s) {
        super(s);
    }
    
    public RealmCallback(final String s, final String s2) {
        super(s, s2);
    }
}
