package com.me.devicemanagement.onpremise.tools.backuprestore.gui;

import java.util.Locale;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Container;
import javax.swing.GroupLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ProgressPanel extends JPanel
{
    public static final String EMPTY_PANEL = "emptyCard";
    public static final String PROGRESS_PANEL = "progressCard";
    private JButton cancelButton;
    private JPanel emptyPanel;
    private JProgressBar progressBar;
    private JTextField progressMessage;
    private JPanel progressPanel;
    private JSeparator progressSeparator;
    private JLabel serverLabel;
    private JTextField serverLocation;
    private JTextField statusMessage;
    
    public ProgressPanel() {
        this.initComponents();
        this.cancelButton.setVisible(false);
        this.serverLocation.setEditable(false);
        this.progressMessage.setEditable(false);
        this.statusMessage.setEditable(false);
    }
    
    private void initComponents() {
        this.emptyPanel = new JPanel();
        this.progressPanel = new JPanel();
        this.progressSeparator = new JSeparator();
        this.serverLabel = new JLabel();
        this.serverLocation = new JTextField();
        this.statusMessage = new JTextField();
        this.progressMessage = new JTextField();
        this.cancelButton = new JButton();
        this.progressBar = new JProgressBar();
        this.setLayout(new CardLayout());
        this.emptyPanel.setBackground(new Color(248, 248, 244));
        final GroupLayout emptyPanelLayout = new GroupLayout(this.emptyPanel);
        this.emptyPanel.setLayout(emptyPanelLayout);
        emptyPanelLayout.setHorizontalGroup(emptyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 307, 32767));
        emptyPanelLayout.setVerticalGroup(emptyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 166, 32767));
        this.add(this.emptyPanel, "emptyCard");
        this.progressPanel.setBackground(new Color(248, 248, 244));
        this.serverLabel.setText("<html><b>Server :</b></html>");
        this.serverLocation.setBackground(new Color(248, 248, 244));
        this.serverLocation.setBorder(null);
        this.statusMessage.setBackground(new Color(248, 248, 244));
        this.statusMessage.setBorder(null);
        this.progressMessage.setBackground(new Color(248, 248, 244));
        this.progressMessage.setBorder(null);
        this.cancelButton.setText(BackupRestoreUtil.getString("desktopcentral.tools.common.cancel", null));
        final GroupLayout progressPanelLayout = new GroupLayout(this.progressPanel);
        this.progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(progressPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(progressPanelLayout.createSequentialGroup().addContainerGap().addGroup(progressPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.statusMessage, -2, 300, -2).addGroup(GroupLayout.Alignment.TRAILING, progressPanelLayout.createSequentialGroup().addComponent(this.serverLabel).addGap(14, 14, 14).addComponent(this.serverLocation, -1, 315, 32767)).addComponent(this.progressMessage, -1, 297, 32767))).addComponent(this.progressSeparator, GroupLayout.Alignment.TRAILING, -1, 307, 32767).addGroup(progressPanelLayout.createSequentialGroup().addContainerGap().addComponent(this.progressBar, -1, 287, 32767).addContainerGap()).addGroup(GroupLayout.Alignment.TRAILING, progressPanelLayout.createSequentialGroup().addContainerGap(215, 32767).addComponent(this.cancelButton, -2, 82, -2).addContainerGap()));
        progressPanelLayout.setVerticalGroup(progressPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(progressPanelLayout.createSequentialGroup().addComponent(this.progressSeparator, -2, -1, -2).addGap(14, 14, 14).addGroup(progressPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.serverLocation).addComponent(this.serverLabel)).addGap(30, 30, 30).addComponent(this.statusMessage, -2, -1, -2).addComponent(this.progressMessage, -2, -1, -2).addComponent(this.progressBar, -2, 11, -2).addGap(18, 18, 18).addComponent(this.cancelButton).addGap(19, 19, 19)));
        this.add(this.progressPanel, "progressCard");
    }
    
    public void setVisibleProgressMsg(final boolean visible) {
        this.progressMessage.setVisible(visible);
    }
    
    public void showCard(final String cardName) {
        final CardLayout c1 = (CardLayout)this.getLayout();
        c1.show(this, cardName);
    }
    
    public JTextField getServerLocationTextField() {
        return this.serverLocation;
    }
    
    public void setProgress(final int progress) {
        this.progressBar.setValue(progress);
    }
    
    public int getProgress() {
        return this.progressBar.getValue();
    }
    
    public void setResultMessage(final String message) {
    }
    
    public void setProgressMessage(final String progressMessage) {
        this.progressMessage.setText(progressMessage);
    }
    
    public void setServerLocation(final String serverLocation) {
        this.serverLocation.setText(serverLocation);
        this.serverLocation.setToolTipText(serverLocation);
    }
    
    public void setStatusMessage(final String statusMessage) {
        this.statusMessage.setText(statusMessage);
    }
    
    public void setStatusFieldEnabled(final boolean enabled) {
        this.statusMessage.setEnabled(enabled);
    }
    
    public JButton getCancelButton() {
        return this.cancelButton;
    }
    
    public void setCancelButtonVisible(final boolean visible) {
        this.cancelButton.setVisible(visible);
    }
    
    public JProgressBar getProgressBar() {
        return this.progressBar;
    }
    
    public void resetComponents() {
        this.cancelButton.setEnabled(true);
        this.progressBar.setValue(0);
        this.progressMessage.setText("");
        this.serverLocation.setText("");
        this.statusMessage.setText("");
    }
}
