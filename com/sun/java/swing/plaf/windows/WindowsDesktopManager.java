package com.sun.java.swing.plaf.windows;

import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import java.lang.ref.WeakReference;
import javax.swing.plaf.UIResource;
import java.io.Serializable;
import javax.swing.DefaultDesktopManager;

public class WindowsDesktopManager extends DefaultDesktopManager implements Serializable, UIResource
{
    private WeakReference<JInternalFrame> currentFrameRef;
    
    @Override
    public void activateFrame(final JInternalFrame internalFrame) {
        final JInternalFrame internalFrame2 = (this.currentFrameRef != null) ? this.currentFrameRef.get() : null;
        try {
            super.activateFrame(internalFrame);
            if (internalFrame2 != null && internalFrame != internalFrame2) {
                if (internalFrame2.isMaximum() && internalFrame.getClientProperty("JInternalFrame.frameType") != "optionDialog" && !internalFrame2.isIcon()) {
                    internalFrame2.setMaximum(false);
                    if (internalFrame.isMaximizable()) {
                        if (!internalFrame.isMaximum()) {
                            internalFrame.setMaximum(true);
                        }
                        else if (internalFrame.isMaximum() && internalFrame.isIcon()) {
                            internalFrame.setIcon(false);
                        }
                        else {
                            internalFrame.setMaximum(false);
                        }
                    }
                }
                if (internalFrame2.isSelected()) {
                    internalFrame2.setSelected(false);
                }
            }
            if (!internalFrame.isSelected()) {
                internalFrame.setSelected(true);
            }
        }
        catch (final PropertyVetoException ex) {}
        if (internalFrame != internalFrame2) {
            this.currentFrameRef = new WeakReference<JInternalFrame>(internalFrame);
        }
    }
}
