package javax.swing.plaf.nimbus;

import javax.swing.JProgressBar;
import javax.swing.JComponent;

class ProgressBarFinishedState extends State
{
    ProgressBarFinishedState() {
        super("Finished");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JProgressBar && ((JProgressBar)component).getPercentComplete() == 1.0;
    }
}
