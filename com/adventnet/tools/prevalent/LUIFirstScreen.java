package com.adventnet.tools.prevalent;

import java.awt.event.ItemEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import java.io.Serializable;
import java.awt.event.ActionEvent;
import java.io.File;
import java.awt.Window;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import java.awt.event.ItemListener;
import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.border.EmptyBorder;
import java.awt.Cursor;
import javax.swing.Icon;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.EtchedBorder;
import java.util.Properties;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.applet.Applet;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class LUIFirstScreen extends JPanel implements MouseListener, ActionListener
{
    private boolean initialized;
    private Applet applet;
    private boolean running;
    JPanel Top;
    JPanel JPanel1;
    JTextField fileTextField;
    JButton browseButton;
    JTextArea JLabel1;
    JPanel JPanel2;
    JCheckBox oldCheckBox;
    JPanel JPanel11;
    JLabel clickLabel;
    JLabel idLabel;
    JTextField mIDTextField;
    GridBagConstraints cons;
    Insets inset;
    IDDialog idDialog;
    
    public LUIFirstScreen() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.fileTextField = null;
        this.browseButton = null;
        this.JLabel1 = null;
        this.JPanel2 = null;
        this.oldCheckBox = null;
        this.JPanel11 = null;
        this.clickLabel = null;
        this.idLabel = null;
        this.mIDTextField = null;
        this.cons = new GridBagConstraints();
        this.idDialog = null;
        this.init();
    }
    
    public LUIFirstScreen(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.fileTextField = null;
        this.browseButton = null;
        this.JLabel1 = null;
        this.JPanel2 = null;
        this.oldCheckBox = null;
        this.JPanel11 = null;
        this.clickLabel = null;
        this.idLabel = null;
        this.mIDTextField = null;
        this.cons = new GridBagConstraints();
        this.idDialog = null;
        this.applet = applet;
        this.init();
    }
    
    public void init() {
        if (this.initialized) {
            return;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 569, this.getPreferredSize().height + 320));
        this.setSize(this.getPreferredSize());
        final Container container = this;
        container.setLayout(new BorderLayout());
        try {
            this.initVariables();
            this.setUpGUI(container);
            this.setUpProperties();
            this.setUpConnections();
        }
        catch (final Exception ex) {
            this.showStatus("Error in init method", ex);
        }
        this.initialized = true;
        if (LUtil.isTrialMacBased()) {
            this.setUniqueID();
        }
    }
    
    public String getParameter(final String input) {
        final String value = null;
        return value;
    }
    
    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
    }
    
    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
    }
    
    public void setProperties(final Properties props) {
    }
    
    public void setUpProperties() {
        try {
            this.Top.setBorder(new TitledBorder(new EtchedBorder(0), "Licensee Details", 0, 0, new Font("Dialog", 0, 12), new Color(-16777216)));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.Top, ex);
        }
        try {
            this.Top.setBorder(new TitledBorder(new EtchedBorder(0), ToolsUtils.getString("Licensee Details"), 0, 0, new Font("Dialog", 0, 12), new Color(-16777216)));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.Top, ex);
        }
        try {
            this.fileTextField.setHorizontalAlignment(2);
            this.fileTextField.setFont(new Font("SansSerif", 0, 12));
            this.fileTextField.setBorder(new BevelBorder(1));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.fileTextField, ex);
        }
        try {
            this.browseButton.setText("Browse");
            this.browseButton.setFont(new Font("sansserif", 0, 12));
            this.browseButton.setBorder(new BevelBorder(0));
            this.browseButton.setPreferredSize(new Dimension(31, 21));
            this.browseButton.setMinimumSize(new Dimension(31, 21));
            this.browseButton.setMaximumSize(new Dimension(31, 21));
            this.browseButton.setMnemonic('r');
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.browseButton, ex);
        }
        this.browseButton.setText(ToolsUtils.getString("Browse"));
        try {
            this.JLabel1.setFont(new Font("SansSerif", 0, 12));
            this.JLabel1.setForeground(new Color(-16777216));
            this.JLabel1.setBackground(new Color(-3355444));
            this.JLabel1.setOpaque(false);
            this.JLabel1.setText("Please enter the license file that you have obtained from AdventNet. It can either be an Evaluation user license file or a Registered user license file.");
            this.JLabel1.setWrapStyleWord(true);
            this.JLabel1.setDoubleBuffered(true);
            this.JLabel1.setLineWrap(true);
            this.JLabel1.setEditable(false);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JLabel1, ex);
        }
        this.JLabel1.setText(ToolsUtils.getString("Please enter the license file that you have obtained from AdventNet. It can either be an Evaluation user license file or a Registered user license file."));
        try {
            this.oldCheckBox.setFont(new Font("sansserif", 0, 12));
            this.oldCheckBox.setMinimumSize(new Dimension(10, 10));
            this.oldCheckBox.setText("If you have a license key,select this option and proceed.");
            this.oldCheckBox.setMnemonic('I');
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.oldCheckBox, ex);
        }
        this.oldCheckBox.setText(ToolsUtils.getString("If you have a license key,select this option and proceed."));
        if (LUtil.getBackwardSupport()) {
            this.oldCheckBox.setVisible(true);
        }
        else {
            this.oldCheckBox.setVisible(false);
        }
        try {
            this.clickLabel.setFont(new Font("SansSerif", 0, 12));
            this.clickLabel.setHorizontalTextPosition(4);
            this.clickLabel.setText("");
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.clickLabel, ex);
        }
        if (LUtil.isTrialMacBased()) {
            this.clickLabel.setIcon(Utility.findImage("./com/adventnet/tools/prevalent/images/licensehelp.png", this.applet, true));
            this.clickLabel.setCursor(Cursor.getPredefinedCursor(12));
            this.clickLabel.setForeground(Color.blue);
            this.clickLabel.addMouseListener(this);
        }
        try {
            this.idLabel.setEnabled(true);
            this.idLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
            this.idLabel.setToolTipText("");
            this.idLabel.setBackground(new Color(-3355444));
            this.idLabel.setFont(new Font("sansserif", 0, 12));
            this.idLabel.setForeground(new Color(-16777216));
            this.idLabel.setHorizontalTextPosition(4);
            this.idLabel.setHorizontalAlignment(2);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.idLabel, ex);
        }
        try {
            this.mIDTextField.setHorizontalAlignment(2);
            this.mIDTextField.setBorder(new BevelBorder(1));
            this.mIDTextField.setEnabled(true);
            this.mIDTextField.setEditable(false);
            this.mIDTextField.setBackground(new Color(-1));
            this.mIDTextField.setFont(new Font("sansserif", 0, 12));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.mIDTextField, ex);
        }
        this.JPanel11.setPreferredSize(new Dimension(this.JPanel11.getPreferredSize().width + 218, this.JPanel11.getPreferredSize().height + 22));
        this.JPanel2.setPreferredSize(new Dimension(this.JPanel2.getPreferredSize().width + 254, this.JPanel2.getPreferredSize().height + 22));
        this.JPanel1.setPreferredSize(new Dimension(this.JPanel1.getPreferredSize().width + 35, this.JPanel1.getPreferredSize().height + 197));
        if (LUtil.isTrialMacBased()) {
            this.JPanel11.setVisible(true);
        }
        else {
            this.JPanel11.setVisible(false);
        }
    }
    
    public void initVariables() {
        this.Top = new JPanel();
        this.JPanel1 = new JPanel();
        this.fileTextField = new JTextField();
        this.browseButton = new JButton();
        this.JLabel1 = new JTextArea();
        this.JPanel2 = new JPanel();
        this.oldCheckBox = new JCheckBox();
        this.JPanel11 = new JPanel();
        this.clickLabel = new JLabel();
        this.idLabel = new JLabel();
        this.mIDTextField = new JTextField();
    }
    
    public void setConstraints(final int x, final int y, final int width, final int height, final double wtX, final double wtY, final int anchor, final int fill, final Insets inset, final int padX, final int padY) {
        this.cons.gridx = x;
        this.cons.gridy = y;
        this.cons.gridwidth = width;
        this.cons.gridheight = height;
        this.cons.weightx = wtX;
        this.cons.weighty = wtY;
        this.cons.anchor = anchor;
        this.cons.fill = fill;
        this.cons.insets = inset;
        this.cons.ipadx = padX;
        this.cons.ipady = padY;
    }
    
    public void setUpGUI(final Container container) {
        container.add(this.Top, "Center");
        this.Top.setLayout(new BorderLayout(5, 5));
        this.Top.add(this.JPanel1, "Center");
        this.JPanel1.setLayout(new GridBagLayout());
        this.inset = new Insets(0, 20, 0, 0);
        final int x = 0;
        final int y = 1;
        final int width = 1;
        final int height = 1;
        final double wtX = 0.1;
        final double wtY = 0.1;
        final GridBagConstraints cons = this.cons;
        final int anchor = 10;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(x, y, width, height, wtX, wtY, anchor, 2, this.inset, 0, 0);
        this.JPanel1.add(this.fileTextField, this.cons);
        this.inset = new Insets(0, 5, 0, 0);
        final int x2 = 1;
        final int y2 = 1;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.0;
        final double wtY2 = 0.1;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 2, this.inset, 60, 6);
        this.JPanel1.add(this.browseButton, this.cons);
        this.inset = new Insets(20, 10, 5, 5);
        final int x3 = 0;
        final int y3 = 0;
        final int width3 = 2;
        final int height3 = 1;
        final double wtX3 = 0.1;
        final double wtY3 = 0.0;
        final GridBagConstraints cons5 = this.cons;
        final int anchor3 = 13;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(x3, y3, width3, height3, wtX3, wtY3, anchor3, 2, this.inset, 0, 0);
        this.JPanel1.add(this.JLabel1, this.cons);
        this.Top.add(this.JPanel2, "South");
        this.JPanel2.setLayout(new GridBagLayout());
        this.inset = new Insets(5, 10, 5, 5);
        final int x4 = 0;
        final int y4 = 0;
        final int width4 = 1;
        final int height4 = 1;
        final double wtX4 = 0.1;
        final double wtY4 = 0.1;
        final GridBagConstraints cons7 = this.cons;
        final int anchor4 = 15;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 1, this.inset, 0, 0);
        this.JPanel2.add(this.oldCheckBox, this.cons);
        this.Top.add(this.JPanel11, "North");
        this.JPanel11.setLayout(new GridBagLayout());
        this.inset = new Insets(0, 10, 0, 0);
        final int x5 = 2;
        final int y5 = 0;
        final int width5 = 1;
        final int height5 = 1;
        final double wtX5 = 0.0;
        final double wtY5 = 0.0;
        final GridBagConstraints cons9 = this.cons;
        final int anchor5 = 10;
        final GridBagConstraints cons10 = this.cons;
        this.setConstraints(x5, y5, width5, height5, wtX5, wtY5, anchor5, 0, this.inset, 0, 0);
        this.JPanel11.add(this.clickLabel, this.cons);
        this.inset = new Insets(0, 15, 0, 0);
        final int x6 = 0;
        final int y6 = 0;
        final int width6 = 1;
        final int height6 = 1;
        final double wtX6 = 0.1;
        final double wtY6 = 0.0;
        final GridBagConstraints cons11 = this.cons;
        final int anchor6 = 10;
        final GridBagConstraints cons12 = this.cons;
        this.setConstraints(x6, y6, width6, height6, wtX6, wtY6, anchor6, 2, this.inset, 0, 0);
        this.JPanel11.add(this.idLabel, this.cons);
        this.inset = new Insets(0, 0, 0, 10);
        final int x7 = 1;
        final int y7 = 0;
        final int width7 = 1;
        final int height7 = 1;
        final double wtX7 = 0.0;
        final double wtY7 = 0.0;
        final GridBagConstraints cons13 = this.cons;
        final int anchor7 = 10;
        final GridBagConstraints cons14 = this.cons;
        this.setConstraints(x7, y7, width7, height7, wtX7, wtY7, anchor7, 0, this.inset, 100, 0);
        this.JPanel11.add(this.mIDTextField, this.cons);
    }
    
    public void setUpConnections() {
        final browseButton_fileTextField_conn browseButton_fileTextField_conn1 = new browseButton_fileTextField_conn();
        this.browseButton.addActionListener(browseButton_fileTextField_conn1);
        final oldCheckBox_fileTextField_conn oldCheckBox_fileTextField_conn1 = new oldCheckBox_fileTextField_conn();
        this.oldCheckBox.addItemListener(oldCheckBox_fileTextField_conn1);
    }
    
    public void showStatus(final String message) {
        System.out.println("Internal Error :" + message);
    }
    
    public void showStatus(final String message, final Exception ex) {
        System.out.println("Internal Error :" + message);
        ex.printStackTrace();
    }
    
    private JFrame getFrame() {
        return (JFrame)this.getTopLevelAncestor();
    }
    
    @Override
    public void mouseExited(final MouseEvent evt) {
    }
    
    @Override
    public void mouseReleased(final MouseEvent evt) {
    }
    
    @Override
    public void mouseClicked(final MouseEvent evt) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent evt) {
    }
    
    @Override
    public void mousePressed(final MouseEvent evt) {
        final Point point = evt.getPoint();
        final Point p = ((Component)evt.getSource()).getLocationOnScreen();
        final Point point2 = point;
        point2.x += p.x;
        final Point point3 = point;
        point3.y += p.y;
        this.idDetails(point);
    }
    
    private void idDetails(final Point point) {
        if (this.idDialog == null) {
            final Window win = SwingUtilities.windowForComponent(this);
            (this.idDialog = new IDDialog((JFrame)win)).setActionListener(this);
            final String lpath = LUtil.getDir();
            final File f = LUtil.getTheDetailsFile(this.getClass());
            final String ID = this.idLabel.getText();
            if (!f.exists()) {
                return;
            }
            try {
                final DataInputStream lData = new DataInputStream(new FileInputStream(f));
                final StringBuffer buf = new StringBuffer();
                String urlString = null;
                String dataBuffer;
                while ((dataBuffer = lData.readLine()) != null) {
                    if (dataBuffer.startsWith("http")) {
                        urlString = dataBuffer;
                        break;
                    }
                    buf.append(dataBuffer);
                    buf.append("\n");
                }
                this.idDialog.showHelpMessage(buf.toString(), urlString, point);
            }
            catch (final Exception e) {
                System.out.println("Unable to read Unique ID details ");
                e.printStackTrace();
            }
        }
        else {
            this.idDialog.disappear();
            this.idDialog = null;
        }
    }
    
    private void setUniqueID() {
        final String machineID = LUtil.getUniqueID();
        this.mIDTextField.setText(machineID);
        this.idLabel.setText(ToolsUtils.getString("Your Unique Machine ID is"));
    }
    
    @Override
    public void actionPerformed(final ActionEvent aEvtArg) {
        if (aEvtArg.getActionCommand().equals("DISAPPEARED")) {
            this.idDialog.dispose();
            this.idDialog = null;
        }
    }
    
    class browseButton_fileTextField_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileSelectionMode(0);
            fileChooser.setDialogTitle(ToolsUtils.getString("Select License File"));
            fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
            fileChooser.addChoosableFileFilter(new PatchFilter("xml"));
            final int fileChoosen = fileChooser.showOpenDialog(LUIFirstScreen.this.getFrame());
            if (fileChoosen != 0) {
                return;
            }
            final File selFile = fileChooser.getSelectedFile();
            final String fileAbsolutePath = selFile.getAbsolutePath();
            if (fileAbsolutePath == null) {
                return;
            }
            LUIFirstScreen.this.fileTextField.setText(fileAbsolutePath);
        }
    }
    
    class oldCheckBox_fileTextField_conn implements ItemListener, Serializable
    {
        @Override
        public void itemStateChanged(final ItemEvent arg0) {
            final int i = arg0.getStateChange();
            if (i == 1) {
                LUIFirstScreen.this.fileTextField.setEnabled(false);
                LUIFirstScreen.this.fileTextField.setEditable(false);
                LUIFirstScreen.this.browseButton.setEnabled(false);
            }
            else if (i == 2) {
                LUIFirstScreen.this.fileTextField.setEnabled(true);
                LUIFirstScreen.this.fileTextField.setEditable(true);
                LUIFirstScreen.this.browseButton.setEnabled(true);
            }
        }
    }
}
