package com.me.devicemanagement.onpremise.start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Component;
import java.util.Properties;
import java.awt.Frame;
import java.util.logging.Level;
import javax.swing.UIManager;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.util.logging.Logger;
import javax.swing.JDialog;

public class ShowShutdownStatus extends JDialog
{
    private boolean isTerminated;
    private static final Logger LOGGER;
    private JLabel jLabel1;
    private JProgressBar jProgressBar1;
    
    public ShowShutdownStatus() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception occured while trying to show shutdown status", e);
        }
        ShowShutdownStatus.LOGGER.log(Level.INFO, "Init ShowShutdownStatus");
    }
    
    public ShowShutdownStatus(final Frame parent, final boolean modal, final Properties additionalParams) {
        super(parent, modal);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception occured while trying to show shutdown status", e);
        }
        this.initComponents();
        final Thread showShutDownStatusThread = new Thread("ShowShutDownStatusThread") {
            @Override
            public void run() {
                while (!ShowShutdownStatus.this.isTerminated) {
                    ShowShutdownStatus.LOGGER.log(Level.INFO, "isTerminated " + ShowShutdownStatus.this.isTerminated);
                    try {
                        Thread.sleep(200L);
                    }
                    catch (final Exception ex) {}
                    ShowShutdownStatus.this.jLabel1.setText(ShowShutdownStatus.this.jLabel1.getText() + ".");
                    ShowShutdownStatus.this.jProgressBar1.setValue(ShowShutdownStatus.this.jProgressBar1.getValue() + 3);
                }
                ShowShutdownStatus.LOGGER.log(Level.INFO, "isTerminated 3 " + ShowShutdownStatus.this.isTerminated);
            }
        };
        showShutDownStatusThread.start();
        this.setVisible(true);
        ProductTrayIcon.cleanTray();
    }
    
    public void terminated() {
        ShowShutdownStatus.LOGGER.log(Level.INFO, "isTerminated 1 " + this.isTerminated);
        this.isTerminated = true;
        ShowShutdownStatus.LOGGER.log(Level.INFO, "isTerminated 2 " + this.isTerminated);
        this.setVisible(false);
        this.dispose();
    }
    
    private void initComponents() {
        this.jProgressBar1 = new JProgressBar();
        this.jLabel1 = new JLabel();
        this.setDefaultCloseOperation(2);
        this.setTitle("Shuting down..");
        this.getContentPane().add(this.jProgressBar1, "Center");
        this.jLabel1.setText("Shuting down...");
        this.jLabel1.setPreferredSize(new Dimension(34, 25));
        this.getContentPane().add(this.jLabel1, "North");
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screenSize.width - 300) / 2, (screenSize.height - 80) / 2, 300, 80);
    }
    
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ShowShutdownStatus(new JFrame(), true, null).setVisible(true);
            }
        });
    }
    
    static {
        LOGGER = Logger.getLogger(ShowShutdownStatus.class.getName());
    }
    
    class NewOkCancelDialog extends JDialog
    {
        public static final boolean RET_CANCEL = false;
        public static final boolean RET_OK = true;
        private JPanel buttonPanel;
        private JButton cancelButton;
        private JLabel jLabel1;
        private JButton okButton;
        private boolean returnStatus;
        
        public NewOkCancelDialog(final Frame parent, final boolean modal) {
            super(parent, modal);
            this.returnStatus = false;
            this.initComponents();
        }
        
        public boolean getReturnStatus() {
            return this.returnStatus;
        }
        
        private void initComponents() {
            this.buttonPanel = new JPanel();
            this.okButton = new JButton();
            this.cancelButton = new JButton();
            this.jLabel1 = new JLabel();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent evt) {
                    NewOkCancelDialog.this.closeDialog(evt);
                }
            });
            this.buttonPanel.setLayout(new FlowLayout(2));
            this.okButton.setText("Yes");
            this.okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent evt) {
                    NewOkCancelDialog.this.okButtonActionPerformed(evt);
                }
            });
            this.buttonPanel.add(this.okButton);
            this.cancelButton.setText("No");
            this.cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent evt) {
                    NewOkCancelDialog.this.cancelButtonActionPerformed(evt);
                }
            });
            this.buttonPanel.add(this.cancelButton);
            this.getContentPane().add(this.buttonPanel, "South");
            this.jLabel1.setText("Do you really want to shutdown DesktopCentral?");
            this.getContentPane().add(this.jLabel1, "Center");
            this.pack();
        }
        
        private void okButtonActionPerformed(final ActionEvent evt) {
            this.doClose(true);
        }
        
        private void cancelButtonActionPerformed(final ActionEvent evt) {
            this.doClose(false);
        }
        
        private void closeDialog(final WindowEvent evt) {
            this.doClose(false);
        }
        
        private void doClose(final boolean retStatus) {
            this.returnStatus = retStatus;
            this.setVisible(false);
            this.dispose();
        }
    }
}
