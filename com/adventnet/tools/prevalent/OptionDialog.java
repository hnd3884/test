package com.adventnet.tools.prevalent;

import javax.swing.Icon;
import java.awt.Insets;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Container;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.WindowListener;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

public class OptionDialog extends JDialog implements ActionListener, WindowListener, OptionDialogConstants
{
    private JButton btnYes;
    private JButton btnNo;
    private JButton btnCancel;
    private JButton btnDetails;
    private JPanel detailsPanel;
    private JScrollPane scroll;
    private Container container;
    private JPanel scrollPanel;
    private JLabel lblicon;
    private JTextArea txtmsg;
    private JTextArea text;
    private JTextComponent textComponent;
    private ImageIcon errorIcon;
    private ImageIcon warnIcon;
    private ImageIcon informIcon;
    private int dialogType;
    private String errMsg;
    private String detMsg;
    private GridBagLayout g;
    private GridBagConstraints ct;
    private JPanel btnPanel;
    private int dialogOption;
    private int clickedState;
    private Dimension dialogSize;
    private int componentSize;
    private boolean detailsNeeded;
    
    public OptionDialog() {
        this((Frame)null, "", false);
    }
    
    public OptionDialog(final Frame owner, final String title, final boolean modal) {
        super(owner, title, modal);
        this.dialogType = 0;
        this.clickedState = -1;
        this.detailsNeeded = true;
        this.doInit();
    }
    
    public OptionDialog(final Dialog owner, final String title, final boolean modal) {
        super(owner, title, modal);
        this.dialogType = 0;
        this.clickedState = -1;
        this.detailsNeeded = true;
        this.doInit();
    }
    
    public OptionDialog(final Dialog owner, final boolean modal, final String title, final String message, final String detailsInside) {
        super(owner, title, modal);
        this.dialogType = 0;
        this.clickedState = -1;
        this.detailsNeeded = true;
        this.doInit();
        this.setMessage(message);
        this.setDetailedMessage(detailsInside);
    }
    
    public OptionDialog(final Dialog owner, final boolean modal, final String title, final String message, final JTextComponent comp) {
        super(owner, title, modal);
        this.dialogType = 0;
        this.clickedState = -1;
        this.detailsNeeded = true;
        this.doInit();
        this.setMessage(message);
        this.setTextComponent(comp);
    }
    
    public OptionDialog(final Dialog owner, final boolean modal, final String title, final int option, final int dialogType, final String message, final String detailsInside) {
        super(owner, title, modal);
        this.dialogType = 0;
        this.clickedState = -1;
        this.detailsNeeded = true;
        this.doInit();
        this.setDialogType(dialogType);
        this.setMessage(message);
        this.setDetailedMessage(detailsInside);
        this.setOption(option);
    }
    
    public OptionDialog(final Frame owner, final boolean modal, final String title, final String message, final String detailsInside) {
        super(owner, title, modal);
        this.dialogType = 0;
        this.clickedState = -1;
        this.detailsNeeded = true;
        this.doInit();
        this.setMessage(message);
        this.setDetailedMessage(detailsInside);
    }
    
    public OptionDialog(final Frame owner, final boolean modal, final String title, final String message, final JTextComponent comp) {
        super(owner, title, modal);
        this.dialogType = 0;
        this.clickedState = -1;
        this.detailsNeeded = true;
        this.doInit();
        this.setMessage(message);
        this.setTextComponent(comp);
    }
    
    public OptionDialog(final Frame owner, final boolean modal, final String title, final int option, final int dialogType, final String message, final String detailsInside) {
        super(owner, title, modal);
        this.dialogType = 0;
        this.clickedState = -1;
        this.detailsNeeded = true;
        this.doInit();
        this.setDialogType(dialogType);
        this.setMessage(message);
        this.setDetailedMessage(detailsInside);
        this.setOption(option);
    }
    
    public void doInit() {
        this.errorIcon = new ImageIcon();
        this.warnIcon = new ImageIcon();
        this.informIcon = new ImageIcon();
        this.setInformIcon(Utility.getTheImage(this.getClass().getResource("/com/adventnet/tools/prevalent/images/info.png")));
        this.setWarnIcon(Utility.getTheImage(this.getClass().getResource("/com/adventnet/tools/prevalent/images/warning.png")));
        this.setErrorIcon(Utility.getTheImage(this.getClass().getResource("/com/adventnet/tools/prevalent/images/error.png")));
        this.dialogSize = new Dimension();
        this.lblicon = new JLabel("");
        this.errMsg = new String();
        this.detMsg = new String();
        (this.btnDetails = new JButton("Details >>")).setFont(new Font("SansSerif", 0, 12));
        (this.btnYes = new JButton("Yes")).setFont(new Font("SansSerif", 0, 12));
        (this.btnCancel = new JButton("Cancel")).setFont(new Font("SansSerif", 0, 12));
        (this.btnNo = new JButton("No")).setFont(new Font("SansSerif", 0, 12));
        (this.text = new JTextArea()).setEditable(false);
        this.text.setOpaque(false);
        this.text.setBackground(this.getBackground());
        this.text.setLineWrap(true);
        this.text.setWrapStyleWord(true);
        (this.txtmsg = new JTextArea()).setEditable(false);
        this.txtmsg.setBackground(this.getBackground());
        this.txtmsg.setOpaque(false);
        this.txtmsg.setPreferredSize(new Dimension(5, 10));
        this.txtmsg.setLineWrap(true);
        this.txtmsg.setWrapStyleWord(true);
        (this.detailsPanel = new JPanel(new BorderLayout())).setOpaque(true);
        this.btnYes.addActionListener(this);
        this.btnDetails.addActionListener(this);
        this.btnCancel.addActionListener(this);
        this.btnNo.addActionListener(this);
        this.scrollPanel = new JPanel(new BorderLayout());
        this.addWindowListener(this);
        this.setDialogSize(new Dimension(462, 135));
        this.setComponentHeight(286);
        this.setResizable(false);
        this.addComp();
        this.setDialogType(1);
        this.setTextComponent(this.text);
        this.setMessage("");
        this.setDetailedMessage("");
        this.setOption(0);
    }
    
    public void setDetailsNeeded(final boolean arg) {
        this.detailsNeeded = arg;
        this.setOption(this.getOption());
    }
    
    public boolean getDetailsNeeded() {
        return this.detailsNeeded;
    }
    
    public void setTextComponent(final JTextComponent textArg) {
        if (textArg != null) {
            this.textComponent = textArg;
            (this.scroll = new JScrollPane(this.getTextComponent())).setVerticalScrollBarPolicy(22);
            this.scrollPanel.add(this.scroll, "Center");
            this.setDetailedMessage(this.getDetailedMessage());
        }
    }
    
    public JTextComponent getTextComponent() {
        return this.textComponent;
    }
    
    public JScrollPane getUserComponent() {
        return this.scroll;
    }
    
    public void setComponentHeight(final int height) {
        this.componentSize = height;
    }
    
    public int getComponentHeight() {
        return this.componentSize;
    }
    
    public void setDialogSize(final Dimension dim) {
        this.setSize(this.dialogSize = dim);
    }
    
    public Dimension getDialogSize() {
        return this.getSize();
    }
    
    @Override
    public void windowClosing(final WindowEvent e) {
        this.btnDetails.setText("Details >>");
        this.container.remove(this.detailsPanel);
        this.container.repaint();
        this.setSize(this.getDialogSize());
        this.validate();
        this.hideDialog(3);
    }
    
    public int getClickedState() {
        return this.clickedState;
    }
    
    @Override
    public void windowOpened(final WindowEvent e) {
    }
    
    @Override
    public void windowActivated(final WindowEvent e) {
    }
    
    @Override
    public void windowIconified(final WindowEvent e) {
    }
    
    @Override
    public void windowDeiconified(final WindowEvent e) {
    }
    
    @Override
    public void windowDeactivated(final WindowEvent e) {
    }
    
    @Override
    public void windowClosed(final WindowEvent e) {
        this.hideDialog(3);
        this.btnDetails.setText("Details >>");
        this.container.remove(this.detailsPanel);
        this.setSize(this.getDialogSize());
        this.validate();
    }
    
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == this.btnDetails) {
            if (this.btnDetails.getText() == "Details >>") {
                this.container.add(this.detailsPanel, "Center");
                this.btnDetails.setText("<< Details");
                this.setSize((int)this.getDialogSize().getWidth(), this.getComponentHeight());
                this.validate();
            }
            else {
                this.btnDetails.setText("Details >>");
                this.container.remove(this.detailsPanel);
                this.setSize((int)this.getDialogSize().getWidth(), (int)this.dialogSize.getHeight());
                this.validate();
            }
        }
        else if (e.getSource() == this.btnYes) {
            this.hideDialog(1);
        }
        else if (e.getSource() == this.btnNo) {
            this.hideDialog(2);
        }
        else if (e.getSource() == this.btnCancel) {
            this.hideDialog(3);
        }
    }
    
    public void addComp() {
        this.g = new GridBagLayout();
        this.ct = new GridBagConstraints();
        (this.btnPanel = new JPanel()).setLayout(this.g);
        this.scrollPanel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, this.getBackground()));
        this.detailsPanel.add(new JSeparator(), "North");
        this.detailsPanel.add(this.scrollPanel, "Center");
        this.container = this.getContentPane();
        this.ct.gridx = 0;
        this.ct.gridy = 0;
        this.ct.gridwidth = 1;
        this.ct.gridheight = 1;
        final GridBagConstraints ct = this.ct;
        final GridBagConstraints ct2 = this.ct;
        ct.fill = 1;
        final GridBagConstraints ct3 = this.ct;
        final GridBagConstraints ct4 = this.ct;
        ct3.anchor = 13;
        this.ct.weightx = 0.2;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 4;
        this.ct.ipadx = 4;
        this.ct.insets = new Insets(30, 10, 0, 10);
        this.g.setConstraints(this.lblicon, this.ct);
        this.btnPanel.add(this.lblicon);
        this.ct.gridx = 1;
        this.ct.gridy = 0;
        final GridBagConstraints ct5 = this.ct;
        final GridBagConstraints ct6 = this.ct;
        ct5.gridwidth = 0;
        final GridBagConstraints ct7 = this.ct;
        final GridBagConstraints ct8 = this.ct;
        ct7.gridheight = -1;
        final GridBagConstraints ct9 = this.ct;
        final GridBagConstraints ct10 = this.ct;
        ct9.fill = 1;
        final GridBagConstraints ct11 = this.ct;
        final GridBagConstraints ct12 = this.ct;
        ct11.anchor = 10;
        this.ct.weightx = 0.8;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 0;
        this.ct.ipady = 0;
        this.ct.insets = new Insets(30, 0, 0, 10);
        this.g.setConstraints(this.txtmsg, this.ct);
        this.btnPanel.add(this.txtmsg);
        this.container.add(this.btnPanel, "North");
    }
    
    public void setOption(final int arg) {
        this.dialogOption = arg;
        this.setUp();
    }
    
    public int getOption() {
        return this.dialogOption;
    }
    
    private void process() {
        final Component[] comp = this.btnPanel.getComponents();
        for (int i = 0; i < comp.length; ++i) {
            if (comp[i] instanceof JButton) {
                this.btnPanel.remove(comp[i]);
            }
        }
        this.btnPanel.validate();
    }
    
    private void setUp() {
        final int state = this.getOption();
        switch (state) {
            case 0: {
                this.process();
                this.yesDetails();
                this.btnYes.setText("OK");
                break;
            }
            case 2: {
                this.process();
                this.yesNoDetails();
                break;
            }
            case 1: {
                this.process();
                this.okCancelDetails();
                break;
            }
            case 3: {
                this.process();
                this.yesNoCancelDetails();
                break;
            }
        }
        if (state > 4) {
            System.err.println(" No Such Option Available in the Optional Dialog ");
        }
    }
    
    private void yesDetails() {
        this.ct.gridx = 2;
        this.ct.gridy = 1;
        this.ct.gridwidth = 1;
        this.ct.gridheight = 1;
        final GridBagConstraints ct = this.ct;
        final GridBagConstraints ct2 = this.ct;
        ct.fill = 0;
        final GridBagConstraints ct3 = this.ct;
        final GridBagConstraints ct4 = this.ct;
        ct3.anchor = 13;
        this.ct.weightx = 0.1;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 0;
        this.ct.ipady = 0;
        this.ct.insets = new Insets(10, 10, 10, 5);
        this.g.setConstraints(this.btnYes, this.ct);
        this.btnPanel.add(this.btnYes);
        if (this.detailsNeeded) {
            this.ct.gridx = 3;
            this.ct.gridy = 1;
            this.ct.gridwidth = 1;
            this.ct.gridheight = 1;
            final GridBagConstraints ct5 = this.ct;
            final GridBagConstraints ct6 = this.ct;
            ct5.fill = 0;
            final GridBagConstraints ct7 = this.ct;
            final GridBagConstraints ct8 = this.ct;
            ct7.anchor = 10;
            this.ct.weightx = 0.0;
            this.ct.weighty = 0.0;
            this.ct.ipadx = 0;
            this.ct.ipady = 0;
            this.ct.insets = new Insets(10, 5, 10, 10);
            this.g.setConstraints(this.btnDetails, this.ct);
            this.btnPanel.add(this.btnDetails);
        }
    }
    
    private void yesNoDetails() {
        this.ct.gridx = 2;
        this.ct.gridy = 1;
        this.ct.gridwidth = 1;
        this.ct.gridheight = 1;
        final GridBagConstraints ct = this.ct;
        final GridBagConstraints ct2 = this.ct;
        ct.fill = 1;
        final GridBagConstraints ct3 = this.ct;
        final GridBagConstraints ct4 = this.ct;
        ct3.anchor = 10;
        this.ct.weightx = 0.1;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 0;
        this.ct.ipady = 0;
        this.ct.insets = new Insets(10, 80, 5, 5);
        this.g.setConstraints(this.btnYes, this.ct);
        this.btnPanel.add(this.btnYes);
        this.btnYes.setText("Yes");
        this.ct.gridx = 3;
        this.ct.gridy = 1;
        this.ct.gridwidth = 1;
        this.ct.gridheight = 1;
        final GridBagConstraints ct5 = this.ct;
        final GridBagConstraints ct6 = this.ct;
        ct5.fill = 1;
        final GridBagConstraints ct7 = this.ct;
        final GridBagConstraints ct8 = this.ct;
        ct7.anchor = 10;
        this.ct.weightx = 0.1;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 0;
        this.ct.ipady = 0;
        this.ct.insets = new Insets(10, 0, 5, 5);
        this.g.setConstraints(this.btnNo, this.ct);
        this.btnPanel.add(this.btnNo);
        if (this.detailsNeeded) {
            this.ct.gridx = 4;
            this.ct.gridy = 1;
            this.ct.gridwidth = 1;
            this.ct.gridheight = 1;
            final GridBagConstraints ct9 = this.ct;
            final GridBagConstraints ct10 = this.ct;
            ct9.fill = 1;
            final GridBagConstraints ct11 = this.ct;
            final GridBagConstraints ct12 = this.ct;
            ct11.anchor = 10;
            this.ct.weightx = 0.1;
            this.ct.weighty = 0.0;
            this.ct.ipadx = 0;
            this.ct.ipady = 0;
            this.g.setConstraints(this.btnDetails, this.ct);
            this.btnPanel.add(this.btnDetails);
        }
    }
    
    private void okCancelDetails() {
        this.ct.gridx = 2;
        this.ct.gridy = 1;
        this.ct.gridwidth = 1;
        this.ct.gridheight = 1;
        final GridBagConstraints ct = this.ct;
        final GridBagConstraints ct2 = this.ct;
        ct.fill = 1;
        final GridBagConstraints ct3 = this.ct;
        final GridBagConstraints ct4 = this.ct;
        ct3.anchor = 10;
        this.ct.weightx = 0.1;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 0;
        this.ct.ipady = 0;
        this.ct.insets = new Insets(10, 80, 5, 5);
        this.g.setConstraints(this.btnYes, this.ct);
        this.btnPanel.add(this.btnYes);
        this.btnYes.setText("OK");
        this.ct.gridx = 3;
        this.ct.gridy = 1;
        this.ct.gridwidth = 1;
        this.ct.gridheight = 1;
        final GridBagConstraints ct5 = this.ct;
        final GridBagConstraints ct6 = this.ct;
        ct5.fill = 1;
        final GridBagConstraints ct7 = this.ct;
        final GridBagConstraints ct8 = this.ct;
        ct7.anchor = 10;
        this.ct.weightx = 0.1;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 0;
        this.ct.ipady = 0;
        this.ct.insets = new Insets(10, 0, 5, 5);
        this.g.setConstraints(this.btnCancel, this.ct);
        this.btnPanel.add(this.btnCancel);
        if (this.detailsNeeded) {
            this.ct.gridx = 4;
            this.ct.gridy = 1;
            this.ct.gridwidth = 1;
            this.ct.gridheight = 1;
            final GridBagConstraints ct9 = this.ct;
            final GridBagConstraints ct10 = this.ct;
            ct9.fill = 1;
            final GridBagConstraints ct11 = this.ct;
            final GridBagConstraints ct12 = this.ct;
            ct11.anchor = 10;
            this.ct.weightx = 0.1;
            this.ct.weighty = 0.0;
            this.ct.ipadx = 0;
            this.ct.ipady = 0;
            this.g.setConstraints(this.btnDetails, this.ct);
            this.btnPanel.add(this.btnDetails);
        }
    }
    
    private void yesNoCancelDetails() {
        this.ct.gridx = 2;
        this.ct.gridy = 1;
        this.ct.gridwidth = 1;
        this.ct.gridheight = 1;
        final GridBagConstraints ct = this.ct;
        final GridBagConstraints ct2 = this.ct;
        ct.fill = 1;
        final GridBagConstraints ct3 = this.ct;
        final GridBagConstraints ct4 = this.ct;
        ct3.anchor = 10;
        this.ct.weightx = 0.1;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 0;
        this.ct.ipady = 0;
        this.ct.insets = new Insets(10, 10, 5, 5);
        this.g.setConstraints(this.btnYes, this.ct);
        this.btnPanel.add(this.btnYes);
        this.btnYes.setText("Yes");
        this.ct.gridx = 3;
        this.ct.gridy = 1;
        this.ct.gridwidth = 1;
        this.ct.gridheight = 1;
        final GridBagConstraints ct5 = this.ct;
        final GridBagConstraints ct6 = this.ct;
        ct5.fill = 1;
        final GridBagConstraints ct7 = this.ct;
        final GridBagConstraints ct8 = this.ct;
        ct7.anchor = 10;
        this.ct.weightx = 0.1;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 0;
        this.ct.ipady = 0;
        this.ct.insets = new Insets(10, 0, 5, 5);
        this.g.setConstraints(this.btnNo, this.ct);
        this.btnPanel.add(this.btnNo);
        this.ct.gridx = 4;
        this.ct.gridy = 1;
        this.ct.gridwidth = 1;
        this.ct.gridheight = 1;
        final GridBagConstraints ct9 = this.ct;
        final GridBagConstraints ct10 = this.ct;
        ct9.fill = 1;
        final GridBagConstraints ct11 = this.ct;
        final GridBagConstraints ct12 = this.ct;
        ct11.anchor = 10;
        this.ct.weightx = 0.1;
        this.ct.weighty = 0.0;
        this.ct.ipadx = 0;
        this.ct.ipady = 0;
        this.g.setConstraints(this.btnCancel, this.ct);
        this.btnPanel.add(this.btnCancel);
        if (this.detailsNeeded) {
            this.ct.gridx = 5;
            this.ct.gridy = 1;
            this.ct.gridwidth = 1;
            this.ct.gridheight = 1;
            final GridBagConstraints ct13 = this.ct;
            final GridBagConstraints ct14 = this.ct;
            ct13.fill = 1;
            final GridBagConstraints ct15 = this.ct;
            final GridBagConstraints ct16 = this.ct;
            ct15.anchor = 10;
            this.ct.weightx = 0.1;
            this.ct.weighty = 0.0;
            this.ct.ipadx = 0;
            this.ct.ipady = 0;
            this.g.setConstraints(this.btnDetails, this.ct);
            this.btnPanel.add(this.btnDetails);
        }
    }
    
    public void setMessage(final String err) {
        this.txtmsg.setText(err);
        this.errMsg = err;
    }
    
    public String getMessage() {
        return this.errMsg;
    }
    
    public void setDetailedMessage(final String det) {
        if (this.textComponent != null) {
            this.detMsg = det;
            this.textComponent.setText(det);
        }
        else {
            this.textComponent = this.text;
        }
    }
    
    public String getDetailedMessage() {
        return this.detMsg;
    }
    
    public void setDialogType(final int i) {
        switch (this.dialogType = i) {
            case 0: {
                this.lblicon.setIcon(this.getWarnIcon());
                break;
            }
            case 1: {
                this.lblicon.setIcon(this.getErrorIcon());
                break;
            }
            case 2: {
                this.lblicon.setIcon(this.getInformIcon());
                break;
            }
            default: {
                this.lblicon.setIcon(this.errorIcon);
                break;
            }
        }
    }
    
    void setLableIcon(final ImageIcon icon) {
        if (this.lblicon != null) {
            this.lblicon.setIcon(icon);
        }
    }
    
    public int getDialogType() {
        return this.dialogType;
    }
    
    public ImageIcon getErrorIcon() {
        return this.errorIcon;
    }
    
    public void setErrorIcon(final ImageIcon errorIconArg) {
        this.errorIcon = errorIconArg;
    }
    
    public ImageIcon getWarnIcon() {
        return this.warnIcon;
    }
    
    public void setWarnIcon(final ImageIcon warnIconArg) {
        this.warnIcon = warnIconArg;
    }
    
    public ImageIcon getInformIcon() {
        return this.informIcon;
    }
    
    public void setInformIcon(final ImageIcon informIconArg) {
        this.informIcon = informIconArg;
    }
    
    private void hideDialog(final int state) {
        this.clickedState = state;
        this.setVisible(false);
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
        return super.toString() + ",Error Icon=" + this.errorIcon + ",Warning Icon=" + this.warnIcon + ",Inform Icon=" + this.informIcon + ",Message=" + this.errMsg + ",Detailed Message=" + this.errMsg + ",Dialog Type=" + dlgType;
    }
}
