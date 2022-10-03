package sun.awt.im;

import java.awt.Frame;

public class SimpleInputMethodWindow extends Frame implements InputMethodWindow
{
    InputContext inputContext;
    private static final long serialVersionUID = 5093376647036461555L;
    
    public SimpleInputMethodWindow(final String s, final InputContext inputContext) {
        super(s);
        this.inputContext = null;
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
