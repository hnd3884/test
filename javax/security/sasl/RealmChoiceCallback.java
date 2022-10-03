package javax.security.sasl;

import javax.security.auth.callback.ChoiceCallback;

public class RealmChoiceCallback extends ChoiceCallback
{
    private static final long serialVersionUID = -8588141348846281332L;
    
    public RealmChoiceCallback(final String s, final String[] array, final int n, final boolean b) {
        super(s, array, n, b);
    }
}
