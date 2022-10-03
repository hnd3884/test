package javax.swing.plaf.multi;

import javax.swing.UIDefaults;

class MultiUIDefaults extends UIDefaults
{
    MultiUIDefaults(final int n, final float n2) {
        super(n, n2);
    }
    
    @Override
    protected void getUIError(final String s) {
        System.err.println("Multiplexing LAF:  " + s);
    }
}
