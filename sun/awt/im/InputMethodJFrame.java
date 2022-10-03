package sun.awt.im;

import javax.swing.JFrame;

public class InputMethodJFrame extends JFrame implements InputMethodWindow
{
    InputContext inputContext;
    private static final long serialVersionUID = -4705856747771842549L;
    
    public InputMethodJFrame(final String s, final InputContext inputContext) {
        super(s);
        this.inputContext = null;
        if (JFrame.isDefaultLookAndFeelDecorated()) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(0);
        }
        if (inputContext != null) {
            this.inputContext = inputContext;
        }
        this.setFocusableWindowState(false);
    }
    
    @Override
    public void setInputContext(final InputContext inputContext) {
        this.inputContext = inputContext;
    }
    
    @Override
    public java.awt.im.InputContext getInputContext() {
        if (this.inputContext != null) {
            return this.inputContext;
        }
        return super.getInputContext();
    }
}
