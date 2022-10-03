package com.adventnet.tools.prevalent;

import javax.swing.JTextArea;
import java.awt.Window;
import java.awt.Dialog;
import java.awt.Frame;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;
import java.awt.Component;

public class OptionDialogInformer implements OptionDialogConstants
{
    private Component comp;
    private String errMsg;
    private String detMsg;
    public OptionDialog dlg;
    public String dlgTitle;
    private int dialogType;
    private int dialogOption;
    private JTextComponent componentInside;
    private boolean detailsNeeded;
    private ImageIcon errorIcon;
    private ImageIcon warnIcon;
    private ImageIcon informIcon;
    
    public OptionDialogInformer() {
        this.errMsg = null;
        this.detMsg = null;
        this.dialogType = 0;
        this.detailsNeeded = true;
        this.errorIcon = null;
        this.warnIcon = null;
        this.informIcon = null;
        this.dlgTitle = new String("");
    }
    
    public void setDetailsNeeded(final boolean arg) {
        this.detailsNeeded = arg;
        if (this.dlg != null) {
            this.dlg.setDetailsNeeded(arg);
        }
    }
    
    public boolean getDetailsNeeded() {
        return this.detailsNeeded;
    }
    
    public void setUserComponent(final JTextComponent component) {
        this.componentInside = component;
        if (this.dlg != null) {
            this.dlg.setTextComponent(component);
        }
    }
    
    public JTextComponent getUserComponent() {
        return this.componentInside;
    }
    
    public void setDialogOption(final int arg) {
        this.dialogOption = arg;
        if (this.dlg != null) {
            this.dlg.setOption(arg);
        }
    }
    
    public int getDialogOption() {
        return this.dialogOption;
    }
    
    public Component getComponent() {
        return this.comp;
    }
    
    public void setComponent(final Component compArg) {
        this.comp = compArg;
    }
    
    public void setDialogTitle(final String title) {
        this.dlgTitle = title;
        if (this.dlg != null) {
            this.dlg.setTitle(title);
        }
    }
    
    public String getDialogTitle() {
        return this.dlgTitle;
    }
    
    private void initialize(final boolean modal) {
        Window win = null;
        if (this.comp != null) {
            win = SwingUtilities.windowForComponent(this.comp);
        }
        if (win instanceof Frame) {
            this.dlg = new OptionDialog((Frame)win, this.dlgTitle, modal);
        }
        else if (win instanceof Dialog) {
            this.dlg = new OptionDialog((Dialog)win, this.dlgTitle, modal);
        }
        else {
            this.dlg = new OptionDialog((Frame)null, this.dlgTitle, modal);
        }
        this.dlg.setDetailsNeeded(this.detailsNeeded);
        this.dlg.setTextComponent(this.componentInside);
        this.dlg.setOption(this.dialogOption);
        this.dlg.setTitle(this.dlgTitle);
        this.dlg.setMessage(this.errMsg);
        this.dlg.setDetailedMessage(this.detMsg);
        if (this.errorIcon != null) {
            this.dlg.setErrorIcon(this.errorIcon);
        }
        if (this.warnIcon != null) {
            this.dlg.setWarnIcon(this.warnIcon);
        }
        if (this.informIcon != null) {
            this.dlg.setInformIcon(this.informIcon);
        }
        this.dlg.setDialogType(this.dialogType);
    }
    
    public void showOptionDialog() {
        this.initialize(true);
        this.dlg.setLocationRelativeTo(this.comp);
        this.dlg.setVisible(true);
    }
    
    public int getClickedState() {
        if (this.dlg != null) {
            return this.dlg.getClickedState();
        }
        return -1;
    }
    
    public void showOKDetailsOptionDialog(final Window owner, final String title, final String message, final JTextComponent comp) {
        this.dlgTitle = title;
        this.errMsg = message;
        this.dialogOption = 0;
        this.dialogType = 1;
        this.setComponent(owner);
        this.initialize(false);
        this.dlg.setTitle(title);
        this.dlg.setMessage(message);
        this.dlg.setDetailedMessage(comp.getText());
        this.dlg.setTextComponent(comp);
        this.dlg.setLocationRelativeTo(owner);
        this.dlg.setVisible(true);
    }
    
    public int showOptionDialog(final Window modalcomp, final String title, final String message, final String detailsInside) {
        final JTextArea area = new JTextArea();
        area.setEditable(false);
        return this.showOptionDialog(modalcomp, title, 2, 1, message, detailsInside, null, area);
    }
    
    public int showOptionDialog(final Window win, final String title, final String message, final ImageIcon icon, final JTextComponent component) {
        return this.showOptionDialog(win, title, 2, 1, message, component.getText(), icon, component);
    }
    
    public int showOptionDialog(final Window win, final String title, final int option, final int dialogType, final String message, final JTextComponent component) {
        return this.showOptionDialog(win, title, option, dialogType, message, component.getText(), null, component);
    }
    
    public int showOptionDialog(final Window win, final String title, final int option, final String message, final String detailsInside, final ImageIcon imgIcon) {
        final JTextArea area = new JTextArea();
        area.setEditable(false);
        return this.showOptionDialog(win, title, option, 1, message, detailsInside, imgIcon, area);
    }
    
    public int showOptionDialog(final Window win, final String title, final int option, final int dialogType, final String message, final String detailsInside) {
        final JTextArea area = new JTextArea();
        area.setEditable(false);
        return this.showOptionDialog(win, title, option, dialogType, message, detailsInside, null, area);
    }
    
    public int showOptionDialog(final Window win, final String title, final int option, final String message, final ImageIcon imgIcon, final JTextComponent comp) {
        return this.showOptionDialog(win, title, option, 1, message, comp.getText(), imgIcon, comp);
    }
    
    private int showOptionDialog(final Window win, final String title, final int option, final int dialogType, final String message, final String detailsInside, final ImageIcon imgIcon, final JTextComponent component) {
        OptionDialog dialog = null;
        if (win == null) {
            return -1;
        }
        if (win instanceof Dialog) {
            dialog = new OptionDialog((Dialog)win, title, true);
        }
        else if (win instanceof Frame) {
            dialog = new OptionDialog((Frame)win, title, true);
        }
        else {
            dialog = new OptionDialog();
        }
        if (imgIcon != null) {
            dialog.setLableIcon(imgIcon);
        }
        else {
            dialog.setDialogType(dialogType);
        }
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setTextComponent(component);
        if (detailsInside == null) {
            dialog.setDetailsNeeded(false);
        }
        else {
            dialog.setDetailedMessage(detailsInside);
        }
        dialog.setOption(option);
        dialog.setLocationRelativeTo(win);
        dialog.setVisible(true);
        return dialog.getClickedState();
    }
    
    public void disposeDialog() {
        this.dlg.setVisible(false);
        this.dlg.dispose();
    }
    
    public void setMessage(final String err) {
        this.errMsg = err;
        if (this.dlg != null) {
            this.dlg.setMessage(this.errMsg);
        }
    }
    
    public String getMessage() {
        return this.errMsg;
    }
    
    public void setDetailedMessage(final String msg) {
        this.detMsg = msg;
        if (this.dlg != null) {
            this.dlg.setDetailedMessage(msg);
        }
    }
    
    public String getDetailedMessage() {
        return this.detMsg;
    }
    
    public void setDialogType(final int i) {
        this.dialogType = i;
        if (this.dlg != null) {
            this.dlg.setDialogType(this.dialogType);
        }
    }
    
    public int getDialogType() {
        return this.dialogType;
    }
    
    public ImageIcon getErrorIcon() {
        if (this.dlg != null) {
            return this.dlg.getErrorIcon();
        }
        return null;
    }
    
    public void setErrorIcon(final ImageIcon errorIconArg) {
        this.errorIcon = errorIconArg;
        if (this.dlg != null && errorIconArg != null) {
            this.dlg.setErrorIcon(errorIconArg);
        }
    }
    
    public ImageIcon getWarnIcon() {
        if (this.dlg != null) {
            return this.dlg.getWarnIcon();
        }
        return null;
    }
    
    public void setWarnIcon(final ImageIcon warnIconArg) {
        this.warnIcon = warnIconArg;
        if (this.dlg != null && warnIconArg != null) {
            this.dlg.setWarnIcon(warnIconArg);
        }
    }
    
    public ImageIcon getInformIcon() {
        if (this.dlg != null) {
            return this.dlg.getInformIcon();
        }
        return null;
    }
    
    public void setInformIcon(final ImageIcon informIconArg) {
        this.informIcon = informIconArg;
        if (this.dlg != null && informIconArg != null) {
            this.dlg.setInformIcon(informIconArg);
        }
    }
    
    @Override
    public String toString() {
        String dlgType = null;
        if (this.dialogType == 0) {
            dlgType = "OK_DETAILS";
        }
        else if (this.dialogType == 1) {
            dlgType = "OK_CANCEL_DETAILS";
        }
        else if (this.dialogType == 2) {
            dlgType = "YES_NO_DETAILS";
        }
        else if (this.dialogType == 3) {
            dlgType = "YES_NO_CANCEL_DETAILS";
        }
        return "Error Icon=" + this.errorIcon + ",Warning Icon=" + this.warnIcon + ",Inform Icon=" + this.informIcon + ",Message=" + this.errMsg + ",Detailed Message=" + this.errMsg + ",Dialog Type=" + dlgType;
    }
}
