package javax.swing.plaf.nimbus;

import javax.swing.JViewport;
import javax.swing.JComponent;

class TextAreaNotInScrollPaneState extends State
{
    TextAreaNotInScrollPaneState() {
        super("NotInScrollPane");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return !(component.getParent() instanceof JViewport);
    }
}
