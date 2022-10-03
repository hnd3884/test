package com.adventnet.telnet.telnetwindow;

import java.awt.event.AdjustmentEvent;
import de.mud.terminal.VDUBuffer;
import java.awt.Font;
import javax.swing.JScrollBar;
import de.mud.terminal.SwingTerminal;
import java.awt.event.AdjustmentListener;
import de.mud.terminal.vt320;

public class Vt extends vt320 implements AdjustmentListener
{
    TelnetClient client;
    SwingTerminal sterminal;
    JScrollBar scrollBar;
    
    public Vt(final int n) {
        this.sterminal = new SwingTerminal((VDUBuffer)this, new Font("Monospaced", 0, n));
    }
    
    public void write(final byte[] array) {
        try {
            if (this.client == null) {
                return;
            }
            this.client.write(array);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void setTelnetClient(final TelnetClient client) {
        this.client = client;
    }
    
    public SwingTerminal getSwingTerminal() {
        return this.sterminal;
    }
    
    public void adjustmentValueChanged(final AdjustmentEvent adjustmentEvent) {
        if (this.getWindowBase() != adjustmentEvent.getValue()) {
            this.setWindowBase(adjustmentEvent.getValue());
        }
    }
    
    public void setScrollbar(final JScrollBar scrollBar) {
        (this.scrollBar = scrollBar).addAdjustmentListener(this);
        this.scrollBar.setValues(this.windowBase, this.height, 0, this.maxBufSize);
    }
    
    public void setWindowBase(final int windowBase) {
        super.setWindowBase(windowBase);
        this.handleBufferEvent();
    }
    
    public synchronized void insertLine(final int n, final int n2, final boolean b) {
        super.insertLine(n, n2, b);
        this.handleBufferEvent();
        if (this.scrollBar == null) {
            return;
        }
        this.scrollBar.setValue(this.getWindowBase());
    }
    
    public void setBufferSize(final int bufferSize) {
        final boolean b = bufferSize < this.maxBufSize && this.height < this.maxBufSize;
        super.setBufferSize(bufferSize);
        if (b) {
            this.handleBufferEvent();
        }
    }
    
    public void setScreenSize(final int n, final int n2) {
        super.setScreenSize(n, n2);
        this.handleBufferEvent();
    }
    
    private void handleBufferEvent() {
        if (this.scrollBar == null) {
            return;
        }
        this.scrollBar.setValues(this.windowBase, this.height, 0, this.height + this.screenBase);
    }
}
