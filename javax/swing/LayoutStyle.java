package javax.swing;

import java.awt.Container;
import sun.awt.AppContext;

public abstract class LayoutStyle
{
    public static void setInstance(final LayoutStyle layoutStyle) {
        synchronized (LayoutStyle.class) {
            if (layoutStyle == null) {
                AppContext.getAppContext().remove(LayoutStyle.class);
            }
            else {
                AppContext.getAppContext().put(LayoutStyle.class, layoutStyle);
            }
        }
    }
    
    public static LayoutStyle getInstance() {
        final LayoutStyle layoutStyle;
        synchronized (LayoutStyle.class) {
            layoutStyle = (LayoutStyle)AppContext.getAppContext().get(LayoutStyle.class);
        }
        if (layoutStyle == null) {
            return UIManager.getLookAndFeel().getLayoutStyle();
        }
        return layoutStyle;
    }
    
    public abstract int getPreferredGap(final JComponent p0, final JComponent p1, final ComponentPlacement p2, final int p3, final Container p4);
    
    public abstract int getContainerGap(final JComponent p0, final int p1, final Container p2);
    
    public enum ComponentPlacement
    {
        RELATED, 
        UNRELATED, 
        INDENT;
    }
}
