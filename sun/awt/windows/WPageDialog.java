package sun.awt.windows;

import java.awt.Container;
import java.awt.peer.ComponentPeer;
import java.awt.Toolkit;
import java.awt.Dialog;
import java.awt.print.PrinterJob;
import java.awt.Frame;
import java.awt.print.Printable;
import java.awt.print.PageFormat;

final class WPageDialog extends WPrintDialog
{
    PageFormat page;
    Printable painter;
    
    WPageDialog(final Frame frame, final PrinterJob printerJob, final PageFormat page, final Printable painter) {
        super(frame, printerJob);
        this.page = page;
        this.painter = painter;
    }
    
    WPageDialog(final Dialog dialog, final PrinterJob printerJob, final PageFormat page, final Printable painter) {
        super(dialog, printerJob);
        this.page = page;
        this.painter = painter;
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            final Container parent = this.getParent();
            if (parent != null && parent.getPeer() == null) {
                parent.addNotify();
            }
            if (this.getPeer() == null) {
                this.setPeer(((WToolkit)Toolkit.getDefaultToolkit()).createWPageDialog(this));
            }
            super.addNotify();
        }
    }
    
    private static native void initIDs();
    
    static {
        initIDs();
    }
}
