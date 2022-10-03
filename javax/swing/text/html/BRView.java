package javax.swing.text.html;

import javax.swing.text.Element;

class BRView extends InlineView
{
    public BRView(final Element element) {
        super(element);
    }
    
    @Override
    public int getBreakWeight(final int n, final float n2, final float n3) {
        if (n == 0) {
            return 3000;
        }
        return super.getBreakWeight(n, n2, n3);
    }
}
