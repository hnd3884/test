package javax.swing.text;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public abstract class TextAction extends AbstractAction
{
    public TextAction(final String s) {
        super(s);
    }
    
    protected final JTextComponent getTextComponent(final ActionEvent actionEvent) {
        if (actionEvent != null) {
            final Object source = actionEvent.getSource();
            if (source instanceof JTextComponent) {
                return (JTextComponent)source;
            }
        }
        return this.getFocusedComponent();
    }
    
    public static final Action[] augmentList(final Action[] array, final Action[] array2) {
        final Hashtable hashtable = new Hashtable();
        for (final Action action : array) {
            final String s = (String)action.getValue("Name");
            hashtable.put((s != null) ? s : "", action);
        }
        for (final Action action2 : array2) {
            final String s2 = (String)action2.getValue("Name");
            hashtable.put((s2 != null) ? s2 : "", action2);
        }
        final Action[] array3 = new Action[hashtable.size()];
        int n = 0;
        final Enumeration elements = hashtable.elements();
        while (elements.hasMoreElements()) {
            array3[n++] = (Action)elements.nextElement();
        }
        return array3;
    }
    
    protected final JTextComponent getFocusedComponent() {
        return JTextComponent.getFocusedComponent();
    }
}
