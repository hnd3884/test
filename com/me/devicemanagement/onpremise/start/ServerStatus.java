package com.me.devicemanagement.onpremise.start;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.onpremise.start.util.InstallUtil;
import java.util.Properties;
import java.util.logging.Level;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.util.logging.Logger;
import javax.swing.JDialog;

public class ServerStatus extends JDialog implements InvokeClass
{
    private int webPort;
    private static final Logger LOGGER;
    private JButton jButton1;
    private JCheckBox jCheckBox1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JSeparator jSeparator1;
    private JTextField jTextField1;
    private JTextField jTextField2;
    private JTextField jTextField3;
    private JLabel titleImageLabel;
    
    public ServerStatus() {
        this(new JFrame(), false);
        ServerStatus.LOGGER.log(Level.INFO, "Init ServerStatus");
    }
    
    public ServerStatus(final Frame parent, final boolean modal) {
        super(parent, modal);
        this.webPort = 8080;
        this.initComponents();
    }
    
    @Override
    public void executeProgram(final Properties additionalParams, final String[] args) {
        ServerStatus.LOGGER.log(Level.INFO, "SetVisible ServerStatus");
        final int webPort = InstallUtil.getWebServerPort();
        final String serverAddr = this.getServerName() + ":" + new Integer(webPort).toString();
        this.jTextField3.setText(serverAddr);
        try {
            final String[] command = { "cscript", "status.vbs" };
            Process p = null;
            String commandOutput = "";
            final ProcessBuilder builder = new ProcessBuilder(command);
            p = builder.start();
            final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str = null;
            while ((str = br.readLine()) != null) {
                commandOutput = str;
            }
            p.waitFor();
            ServerStatus.LOGGER.log(Level.INFO, "The status is ::::" + commandOutput);
            if (commandOutput.equalsIgnoreCase("Auto")) {
                this.jCheckBox1.setSelected(true);
            }
            else {
                this.jCheckBox1.setSelected(false);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception occurred while trying to find server status", e);
        }
        this.setVisible(true);
    }
    
    private String getServerIP() {
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        }
        catch (final UnknownHostException uke) {
            return null;
        }
    }
    
    private String getServerName() {
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        }
        catch (final UnknownHostException uke) {
            return null;
        }
    }
    
    private void initComponents() {
        this.titleImageLabel = new JLabel();
        this.jPanel1 = new JPanel();
        this.jLabel1 = new JLabel();
        this.jTextField1 = new JTextField();
        this.jLabel2 = new JLabel();
        this.jTextField2 = new JTextField();
        this.jLabel3 = new JLabel();
        this.jLabel4 = new JLabel();
        this.jLabel5 = new JLabel();
        this.jTextField3 = new JTextField();
        this.jCheckBox1 = new JCheckBox();
        this.jLabel6 = new JLabel();
        this.jPanel2 = new JPanel();
        this.jSeparator1 = new JSeparator();
        this.jButton1 = new JButton();
        this.setDefaultCloseOperation(2);
        this.setTitle("AdventNet ManageEngine Opsym 4");
        this.titleImageLabel.setHorizontalAlignment(0);
        this.titleImageLabel.setIcon(new ImageIcon("..\\images\\title.jpg"));
        this.titleImageLabel.setHorizontalTextPosition(0);
        this.titleImageLabel.setIconTextGap(0);
        this.titleImageLabel.setPreferredSize(new Dimension(0, 60));
        this.getContentPane().add(this.titleImageLabel, "North");
        this.jPanel1.setLayout(new GridBagLayout());
        this.jLabel1.setHorizontalAlignment(4);
        this.jLabel1.setText("Server Name");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jLabel1, gridBagConstraints);
        this.jTextField1.setEditable(false);
        this.jTextField1.setText(this.getServerName());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jTextField1, gridBagConstraints);
        this.jLabel2.setHorizontalAlignment(4);
        this.jLabel2.setText("Server IpAddress");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jLabel2, gridBagConstraints);
        this.jTextField2.setEditable(false);
        this.jTextField2.setText(this.getServerIP());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jTextField2, gridBagConstraints);
        this.jLabel3.setHorizontalAlignment(4);
        this.jLabel3.setText("Server Status");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jLabel3, gridBagConstraints);
        this.jLabel4.setForeground(new Color(0, 150, 0));
        this.jLabel4.setText("Running");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jLabel4, gridBagConstraints);
        this.jLabel5.setHorizontalAlignment(4);
        this.jLabel5.setText("Client ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jLabel5, gridBagConstraints);
        this.jTextField3.setEditable(false);
        this.jTextField3.setText("jTextField3");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jTextField3, gridBagConstraints);
        this.jCheckBox1.setSelected(true);
        this.jCheckBox1.setToolTipText("Check the checkbox if you want to Start Opsym at windows startup");
        this.jCheckBox1.setHorizontalAlignment(4);
        this.jCheckBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerStatus.this.jCheckBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jCheckBox1, gridBagConstraints);
        this.jLabel6.setText("Start Opsym automatically at windows startup");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel1.add(this.jLabel6, gridBagConstraints);
        this.getContentPane().add(this.jPanel1, "Center");
        this.jPanel2.setLayout(new GridBagLayout());
        this.jPanel2.setPreferredSize(new Dimension(10, 50));
        this.jSeparator1.setOpaque(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        this.jPanel2.add(this.jSeparator1, gridBagConstraints);
        this.jButton1.setText("Close");
        this.jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ServerStatus.this.jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = 3;
        gridBagConstraints.anchor = 13;
        gridBagConstraints.insets = new Insets(5, 5, 0, 20);
        this.jPanel2.add(this.jButton1, gridBagConstraints);
        this.getContentPane().add(this.jPanel2, "South");
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screenSize.width - 455) / 2, (screenSize.height - 327) / 2, 455, 327);
    }
    
    private void jCheckBox1ActionPerformed(final ActionEvent evt) {
        try {
            if (this.jCheckBox1.isSelected()) {
                ServerStatus.LOGGER.log(Level.INFO, "Checkbox is checked");
                final String[] command = { "cscript", "auto.vbs" };
                final ProcessBuilder builder = new ProcessBuilder(command);
                builder.start();
            }
            else {
                ServerStatus.LOGGER.log(Level.INFO, "Checkbox is unchecked");
                final String[] command = { "cscript", "man.vbs" };
                final ProcessBuilder builder = new ProcessBuilder(command);
                builder.start();
            }
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception occured while trying to change Opsym server startup type ", e);
        }
    }
    
    private void jButton1ActionPerformed(final ActionEvent evt) {
        this.setVisible(false);
    }
    
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerStatus(new JFrame(), true).setVisible(true);
            }
        });
    }
    
    static {
        LOGGER = Logger.getLogger(ServerStatus.class.getName());
    }
}
