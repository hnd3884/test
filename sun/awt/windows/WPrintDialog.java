package sun.awt.windows;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.Component;
import sun.awt.AWTAccessor;
import java.awt.peer.ComponentPeer;
import java.awt.LayoutManager;
import java.awt.Frame;
import java.awt.print.PrinterJob;
import java.awt.PrintJob;
import java.awt.Dialog;

class WPrintDialog extends Dialog
{
    protected PrintJob job;
    protected PrinterJob pjob;
    private boolean retval;
    
    WPrintDialog(final Frame frame, final PrinterJob pjob) {
        super(frame, true);
        this.retval = false;
        this.pjob = pjob;
        this.setLayout(null);
    }
    
    WPrintDialog(final Dialog dialog, final PrinterJob pjob) {
        super(dialog, "", true);
        this.retval = false;
        this.pjob = pjob;
        this.setLayout(null);
    }
    
    final void setPeer(final ComponentPeer componentPeer) {
        AWTAccessor.getComponentAccessor().setPeer(this, componentPeer);
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            final Container parent = this.getParent();
            if (parent != null && parent.getPeer() == null) {
                parent.addNotify();
            }
            if (this.getPeer() == null) {
                this.setPeer(((WToolkit)Toolkit.getDefaultToolkit()).createWPrintDialog(this));
            }
            super.addNotify();
        }
    }
    
    final void setRetVal(final boolean retval) {
        this.retval = retval;
    }
    
    final boolean getRetVal() {
        return this.retval;
    }
    
    private static native void initIDs();
    
    static {
        initIDs();
    }
}
