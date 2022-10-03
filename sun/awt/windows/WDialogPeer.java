package sun.awt.windows;

import java.awt.SystemColor;
import java.awt.Dimension;
import java.util.Iterator;
import java.awt.Component;
import sun.awt.AWTAccessor;
import java.util.List;
import sun.awt.im.InputMethodManager;
import java.awt.Window;
import java.awt.Dialog;
import java.awt.Color;
import java.awt.peer.DialogPeer;

final class WDialogPeer extends WWindowPeer implements DialogPeer
{
    static final Color defaultBackground;
    boolean needDefaultBackground;
    
    WDialogPeer(final Dialog dialog) {
        super(dialog);
        final String triggerMenuString = InputMethodManager.getInstance().getTriggerMenuString();
        if (triggerMenuString != null) {
            this.pSetIMMOption(triggerMenuString);
        }
    }
    
    native void createAwtDialog(final WComponentPeer p0);
    
    @Override
    void create(final WComponentPeer wComponentPeer) {
        this.preCreate(wComponentPeer);
        this.createAwtDialog(wComponentPeer);
    }
    
    native void showModal();
    
    native void endModal();
    
    @Override
    void initialize() {
        final Dialog dialog = (Dialog)this.target;
        if (this.needDefaultBackground) {
            dialog.setBackground(WDialogPeer.defaultBackground);
        }
        super.initialize();
        if (dialog.getTitle() != null) {
            this.setTitle(dialog.getTitle());
        }
        this.setResizable(dialog.isResizable());
    }
    
    @Override
    protected void realShow() {
        if (((Dialog)this.target).getModalityType() != Dialog.ModalityType.MODELESS) {
            this.showModal();
        }
        else {
            super.realShow();
        }
    }
    
    @Override
    void hide() {
        if (((Dialog)this.target).getModalityType() != Dialog.ModalityType.MODELESS) {
            this.endModal();
        }
        else {
            super.hide();
        }
    }
    
    @Override
    public void blockWindows(final List<Window> list) {
        final Iterator<Window> iterator = list.iterator();
        while (iterator.hasNext()) {
            final WWindowPeer wWindowPeer = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(iterator.next());
            if (wWindowPeer != null) {
                wWindowPeer.setModalBlocked((Dialog)this.target, true);
            }
        }
    }
    
    @Override
    public Dimension getMinimumSize() {
        if (((Dialog)this.target).isUndecorated()) {
            return super.getMinimumSize();
        }
        return new Dimension(WWindowPeer.getSysMinWidth(), WWindowPeer.getSysMinHeight());
    }
    
    @Override
    boolean isTargetUndecorated() {
        return ((Dialog)this.target).isUndecorated();
    }
    
    @Override
    public void reshape(final int n, final int n2, final int n3, final int n4) {
        if (((Dialog)this.target).isUndecorated()) {
            super.reshape(n, n2, n3, n4);
        }
        else {
            this.reshapeFrame(n, n2, n3, n4);
        }
    }
    
    private void setDefaultColor() {
        this.needDefaultBackground = true;
    }
    
    native void pSetIMMOption(final String p0);
    
    void notifyIMMOptionChange() {
        InputMethodManager.getInstance().notifyChangeRequest((Component)this.target);
    }
    
    static {
        defaultBackground = SystemColor.control;
    }
}
