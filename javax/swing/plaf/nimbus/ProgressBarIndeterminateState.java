package javax.swing.plaf.nimbus;

import javax.swing.JProgressBar;
import javax.swing.JComponent;

class ProgressBarIndeterminateState extends State
{
    ProgressBarIndeterminateState() {
        super("Indeterminate");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JProgressBar && ((JProgressBar)component).isIndeterminate();
    }
}
