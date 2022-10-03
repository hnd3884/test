package com.adventnet.tools.prevalent;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Properties;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.File;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.net.URL;
import javax.swing.border.BevelBorder;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JPanel;
import java.applet.Applet;
import javax.swing.JFrame;

public final class LUIGeneral extends JFrame
{
    private boolean initialized;
    private Applet applet;
    private static final String[] param;
    private boolean running;
    JPanel Top;
    JSeparator JSeparator1;
    JLabel cardTitleLabel;
    JPanel JPanel2;
    CardPanel cardPanel;
    ImageLabel imageLabel;
    JPanel JPanel1;
    JButton previousButton;
    static JButton nextButton;
    JButton exitButton;
    GridBagConstraints cons;
    Insets inset;
    private String lPath;
    String freeFilePath;
    String evalFilePath;
    private String title;
    private boolean vrtuser;
    private boolean validationOver;
    private boolean regScreencanexit;
    private String contactMesg;
    private Validation valid;
    private boolean checkForRegister;
    private boolean evalStandard;
    private String defaultEvalFilePath;
    
    public static void main(final String[] args) {
    }
    
    @Override
    public void setVisible(final boolean bl) {
        if (bl) {
            this.init();
            this.start();
        }
        else {
            this.stop();
        }
        super.setVisible(bl);
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
    
    public void init() {
        if (this.initialized) {
            return;
        }
        this.setSize(this.getPreferredSize().width + 567, this.getPreferredSize().height + 411);
        this.setTitle("LUIGeneral");
        final Container container = this.getContentPane();
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
        this.agreement(this.lPath);
    }
    
    public String getParameter(final String input) {
        final String value = null;
        return value;
    }
    
    public void setUpProperties() {
        boolean isHide = false;
        try {
            this.Top.setBorder(new EmptyBorder(5, 5, 5, 5));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.Top, ex);
        }
        final URL icon = this.getClass().getResource("/com/adventnet/tools/prevalent/images/adventneticon.png");
        if (icon != null) {
            this.setIconImage(new ImageIcon(icon).getImage());
        }
        this.setSize(new Dimension(600, 450));
        this.setResizable(false);
        this.setTitle(this.title);
        this.centerWindow(this);
        try {
            this.cardTitleLabel.setForeground(new Color(-16777216));
            this.cardTitleLabel.setHorizontalTextPosition(4);
            this.cardTitleLabel.setHorizontalAlignment(0);
            this.cardTitleLabel.setFont(new Font("Serif", 1, 14));
            this.cardTitleLabel.setText("  ");
        }
        catch (final Exception ex2) {
            this.showStatus("Exception while setting properties for bean " + this.cardTitleLabel, ex2);
        }
        try {
            final String[] cardPanelcardAndClassNames_array = { "AgreementPanel=com.adventnet.tools.prevalent.LUIAgreement", "RegisterPanel=com.adventnet.tools.prevalent.LUIRegisterDetails", "FirstScreen=com.adventnet.tools.prevalent.LUIFirstScreen", "FileScreen=com.adventnet.tools.prevalent.LUIFileScreen", "OptionScreen=com.adventnet.tools.prevalent.LUIOptionScreen" };
            this.cardPanel.setCardAndClassNames(cardPanelcardAndClassNames_array);
        }
        catch (final Exception ex2) {
            this.showStatus("Exception while setting properties for bean " + this.cardPanel, ex2);
        }
        try {
            final Indication indicate = Indication.getInstance();
            isHide = indicate.isAgreementHide();
        }
        catch (final Exception e) {
            isHide = false;
        }
        if (isHide) {
            this.cardPanel.showCard("OptionScreen");
        }
        else {
            this.cardPanel.showCard("AgreementPanel");
        }
        this.imageLabel.setImageIcon(Utility.findImage("./com/adventnet/tools/prevalent/images/license.png", this.applet, true));
        try {
            this.previousButton.setFont(new Font("Dialog", 0, 12));
            this.previousButton.setBorder(new BevelBorder(0));
            this.previousButton.setEnabled(false);
            this.previousButton.setText("Back");
            this.previousButton.setMnemonic(66);
        }
        catch (final Exception ex2) {
            this.showStatus("Exception while setting properties for bean " + this.previousButton, ex2);
        }
        this.previousButton.setText(ToolsUtils.getString("Back"));
        try {
            LUIGeneral.nextButton.setFont(new Font("Dialog", 0, 12));
            LUIGeneral.nextButton.setBorder(new BevelBorder(0));
            LUIGeneral.nextButton.setEnabled(isHide);
            LUIGeneral.nextButton.setText("Next");
            LUIGeneral.nextButton.setMnemonic(78);
        }
        catch (final Exception ex2) {
            this.showStatus("Exception while setting properties for bean " + LUIGeneral.nextButton, ex2);
        }
        LUIGeneral.nextButton.setText(ToolsUtils.getString("Next"));
        LUIGeneral.nextButton.getRootPane().setDefaultButton(LUIGeneral.nextButton);
        try {
            this.exitButton.setFont(new Font("SansSerif", 0, 12));
            this.exitButton.setText("Exit");
            this.exitButton.setHorizontalTextPosition(11);
            this.exitButton.setBorder(new BevelBorder(0));
            this.exitButton.setMnemonic('x');
        }
        catch (final Exception ex2) {
            this.showStatus("Exception while setting properties for bean " + this.exitButton, ex2);
        }
        this.imageLabel.setBorder(new BevelBorder(1));
        this.exitButton.setText(ToolsUtils.getString("Exit"));
        this.exitButton.setPreferredSize(new Dimension(this.exitButton.getPreferredSize().width + 60, this.exitButton.getPreferredSize().height + 6));
        LUIGeneral.nextButton.setPreferredSize(new Dimension(LUIGeneral.nextButton.getPreferredSize().width + 54, LUIGeneral.nextButton.getPreferredSize().height + 6));
        this.previousButton.setPreferredSize(new Dimension(this.previousButton.getPreferredSize().width + 52, this.previousButton.getPreferredSize().height + 6));
        this.imageLabel.setPreferredSize(new Dimension(this.imageLabel.getPreferredSize().width + 4, this.imageLabel.getPreferredSize().height + 210));
        this.cardPanel.setPreferredSize(new Dimension(this.cardPanel.getPreferredSize().width + 847, this.cardPanel.getPreferredSize().height + 247));
    }
    
    public void initVariables() {
        this.Top = new JPanel();
        this.JSeparator1 = new JSeparator();
        this.cardTitleLabel = new JLabel();
        this.JPanel2 = new JPanel();
        this.cardPanel = new CardPanel(this.applet);
        this.imageLabel = new ImageLabel(this.applet);
        this.JPanel1 = new JPanel();
        this.previousButton = new JButton();
        LUIGeneral.nextButton = new JButton();
        this.exitButton = new JButton();
    }
    
    public void setUpGUI(final Container container) {
        container.add(this.Top, "Center");
        this.Top.setLayout(new GridBagLayout());
        this.inset = new Insets(5, 5, 5, 5);
        final int x = 0;
        final int y = 2;
        final int width = 1;
        final int height = 1;
        final double wtX = 0.1;
        final double wtY = 0.0;
        final GridBagConstraints cons = this.cons;
        final int anchor = 10;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(x, y, width, height, wtX, wtY, anchor, 2, this.inset, 0, 3);
        this.Top.add(this.JSeparator1, this.cons);
        this.inset = new Insets(5, 5, 5, 0);
        final int x2 = 0;
        final int y2 = 0;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.1;
        final double wtY2 = 0.0;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 2, this.inset, 0, 0);
        this.Top.add(this.cardTitleLabel, this.cons);
        this.inset = new Insets(5, 5, 5, 5);
        final int x3 = 0;
        final int y3 = 1;
        final int width3 = 1;
        final int height3 = 1;
        final double wtX3 = 0.1;
        final double wtY3 = 0.1;
        final GridBagConstraints cons5 = this.cons;
        final int anchor3 = 10;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(x3, y3, width3, height3, wtX3, wtY3, anchor3, 1, this.inset, 0, 0);
        this.Top.add(this.JPanel2, this.cons);
        this.JPanel2.setLayout(new BorderLayout(5, 5));
        this.JPanel2.add(this.cardPanel, "Center");
        this.cardPanel.setLayout(new CardLayout(5, 5));
        this.JPanel2.add(this.imageLabel, "West");
        this.imageLabel.setLayout(new FlowLayout(1, 5, 5));
        this.inset = new Insets(5, 5, 5, 5);
        final int x4 = 0;
        final int y4 = 3;
        final int width4 = 1;
        final int height4 = 1;
        final double wtX4 = 0.1;
        final double wtY4 = 0.0;
        final GridBagConstraints cons7 = this.cons;
        final int anchor4 = 10;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 2, this.inset, 0, 0);
        this.Top.add(this.JPanel1, this.cons);
        this.JPanel1.setLayout(new FlowLayout(2, 5, 5));
        this.JPanel1.add(this.previousButton);
        this.JPanel1.add(LUIGeneral.nextButton);
        this.JPanel1.add(this.exitButton);
    }
    
    public void setUpConnections() {
        final previousButton_cardPanel_conn previousButton_cardPanel_conn1 = new previousButton_cardPanel_conn();
        this.previousButton.addActionListener(previousButton_cardPanel_conn1);
        final JButton1_cardPanel_conn JButton1_cardPanel_conn1 = new JButton1_cardPanel_conn();
        LUIGeneral.nextButton.addActionListener(JButton1_cardPanel_conn1);
        final exitButton_cardPanel_conn exitButton_cardPanel_conn1 = new exitButton_cardPanel_conn();
        this.exitButton.addActionListener(exitButton_cardPanel_conn1);
    }
    
    public static void acceptAgreement(final boolean accept) {
        LUIGeneral.nextButton.setEnabled(accept);
    }
    
    public static void setNextButtonText(final String txt) {
        LUIGeneral.nextButton.setText(ToolsUtils.getString(txt));
    }
    
    private JFrame getFrame() {
        return this;
    }
    
    public void showSecond(final boolean bool) {
        this.checkForRegister = bool;
        this.init();
        final LUIAgreement comp = (LUIAgreement)this.cardPanel.getCard("AgreementPanel");
        final LUIRegisterDetails rDet = (LUIRegisterDetails)this.cardPanel.getCard("RegisterPanel");
        comp.acceptCheckBox.setSelected(true);
        this.previousButton.setEnabled(false);
        this.cardPanel.showCard("FirstScreen");
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                if (LUIGeneral.this.regScreencanexit) {
                    System.exit(0);
                }
                else {
                    LUIGeneral.this.setVisible(false);
                }
            }
        });
    }
    
    public void showStatus(final String message) {
        System.out.println("Internal Error :" + message);
    }
    
    public void showStatus(final String message, final Exception ex) {
        System.out.println("Internal Error :" + message);
        ex.printStackTrace();
    }
    
    public LUIGeneral(final String t, final String path, final Validation validate) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JSeparator1 = null;
        this.cardTitleLabel = null;
        this.JPanel2 = null;
        this.cardPanel = null;
        this.imageLabel = null;
        this.JPanel1 = null;
        this.previousButton = null;
        this.exitButton = null;
        this.cons = new GridBagConstraints();
        this.lPath = "";
        this.freeFilePath = LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "Free.xml";
        this.evalFilePath = LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "StandardEvaluation.xml";
        this.title = null;
        this.vrtuser = false;
        this.validationOver = false;
        this.regScreencanexit = true;
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.valid = null;
        this.checkForRegister = false;
        this.evalStandard = false;
        this.defaultEvalFilePath = null;
        this.title = t;
        this.lPath = path;
        this.valid = validate;
        this.pack();
    }
    
    public LUIGeneral(final String t, final String path, final Validation validate, final String filePath, final boolean policy) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JSeparator1 = null;
        this.cardTitleLabel = null;
        this.JPanel2 = null;
        this.cardPanel = null;
        this.imageLabel = null;
        this.JPanel1 = null;
        this.previousButton = null;
        this.exitButton = null;
        this.cons = new GridBagConstraints();
        this.lPath = "";
        this.freeFilePath = LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "Free.xml";
        this.evalFilePath = LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "StandardEvaluation.xml";
        this.title = null;
        this.vrtuser = false;
        this.validationOver = false;
        this.regScreencanexit = true;
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.valid = null;
        this.checkForRegister = false;
        this.evalStandard = false;
        this.defaultEvalFilePath = null;
        this.title = t;
        this.lPath = path;
        this.valid = validate;
        this.evalStandard = policy;
        this.defaultEvalFilePath = filePath;
        this.pack();
    }
    
    private void licensePreviousAction() {
        final String selectedCard = this.cardPanel.getSelectedCardName();
        if (selectedCard.equals("FirstScreen")) {
            LUIGeneral.nextButton.setText(ToolsUtils.getString("Next"));
            if (this.evalStandard || new File(this.freeFilePath).exists()) {
                this.cardPanel.showCard("OptionScreen");
            }
            else {
                final Component comp = this.cardPanel.getCard("AgreementPanel");
                ((LUIAgreement)comp).acceptCheckBox.setSelected(true);
                this.previousButton.setEnabled(false);
                this.cardPanel.showCard("AgreementPanel");
            }
        }
        else if (selectedCard.equals("RegisterPanel")) {
            LUIGeneral.nextButton.setText(ToolsUtils.getString("Next"));
            this.cardPanel.showCard("FirstScreen");
        }
        else if (selectedCard.equals("FileScreen")) {
            LUIGeneral.nextButton.setText(ToolsUtils.getString("Next"));
            this.cardPanel.showCard("FirstScreen");
        }
        else if (selectedCard.equals("OptionScreen")) {
            LUIGeneral.nextButton.setText(ToolsUtils.getString("Next"));
            final Component comp = this.cardPanel.getCard("AgreementPanel");
            ((LUIAgreement)comp).acceptCheckBox.setSelected(true);
            this.previousButton.setEnabled(false);
            this.cardPanel.showCard("AgreementPanel");
        }
    }
    
    private void licenseNextAction() {
        final String selectedCard = this.cardPanel.getSelectedCardName();
        if (selectedCard.equals("AgreementPanel")) {
            final LUIAgreement aPanel = (LUIAgreement)this.cardPanel.getSelectedCard();
            if (aPanel.acceptCheckBox.isSelected()) {
                this.previousButton.setEnabled(true);
                if (this.evalStandard || new File(this.freeFilePath).exists()) {
                    final LUIOptionScreen opt = (LUIOptionScreen)this.cardPanel.getCard("OptionScreen");
                    if (opt.evalRadioButton.isSelected() || opt.freeRadioButton.isSelected()) {
                        LUIGeneral.nextButton.setText(ToolsUtils.getString("Finish"));
                    }
                    this.cardPanel.showCard("OptionScreen");
                }
                else {
                    this.cardPanel.showCard("FirstScreen");
                }
            }
        }
        else if (selectedCard.equals("FirstScreen")) {
            final LUIFirstScreen first = (LUIFirstScreen)this.cardPanel.getSelectedCard();
            if (first.oldCheckBox.isSelected()) {
                LUIGeneral.nextButton.setText(ToolsUtils.getString("Finish"));
                this.previousButton.setEnabled(true);
                this.cardPanel.showCard("RegisterPanel");
            }
            else {
                final LUIFileScreen fileScreen = (LUIFileScreen)this.cardPanel.getCard("FileScreen");
                final String licenseFile = first.fileTextField.getText().trim();
                fileScreen.userNameComboBox.removeAllItems();
                if (licenseFile.equals("")) {
                    LUtil.showError("ERROR CODE : 544\n", "Enter License File path.", this.contactMesg, "Error", 544);
                    return;
                }
                try {
                    final InputFileParser parser = new InputFileParser(licenseFile);
                    final DataClass data = parser.getDataClass();
                    final ArrayList userList = data.getUserList();
                    final int size = userList.size();
                    for (int i = 0; i < size; ++i) {
                        fileScreen.userNameComboBox.addItem(userList.get(i));
                    }
                    String companyName = null;
                    if (size != -1) {
                        final User user = data.getUserObject(userList.get(0));
                        companyName = user.getCompanyName();
                    }
                    if (companyName != null) {
                        fileScreen.companyNameTextField.setText(companyName);
                    }
                }
                catch (final Exception exp) {
                    LUtil.showError("ERROR CODE : 545\n", "Enter a valid License File path.", this.contactMesg, "Error", 545);
                    return;
                }
                LUIGeneral.nextButton.setText(ToolsUtils.getString("Finish"));
                this.previousButton.setEnabled(true);
                this.cardPanel.showCard("FileScreen");
            }
        }
        else if (selectedCard.equals("RegisterPanel")) {
            final LUIRegisterDetails rDet = (LUIRegisterDetails)this.cardPanel.getSelectedCard();
            final String userName = rDet.userNameTextField.getText().trim();
            final String companyName2 = rDet.companyNameTextField.getText().trim();
            if (userName.equals("")) {
                LUtil.showError("ERROR CODE : 540\n", "Enter User Name", this.contactMesg, "Error", 540);
                return;
            }
            if (companyName2.equals("")) {
                LUtil.showError("ERROR CODE : 541\n", "Enter Company Name", this.contactMesg, "Error", 541);
                return;
            }
            String licenseKey = rDet.keyTextField.getText().trim();
            final StringBuffer buffer = new StringBuffer();
            final StringTokenizer st = new StringTokenizer(licenseKey, "-");
            while (st.hasMoreTokens()) {
                buffer.append(st.nextToken());
            }
            licenseKey = buffer.toString();
            final int length = licenseKey.length();
            if (licenseKey.equals("")) {
                LUtil.showError("ERROR CODE : 542\n", "Enter License Key", this.contactMesg, "Error", 542);
                return;
            }
            if (length != 24 && length != 20 && length != 23) {
                LUtil.showError("ERROR CODE : 543\n", "Enter proper License key", this.contactMesg, "Error", 543);
                return;
            }
            if (this.valid == null) {
                this.valid = Validation.getInstance();
            }
            final boolean success = this.valid.doOldValidation(userName, companyName2, null, "R", licenseKey, true);
            if (success) {
                this.validationOver = true;
                this.setVisible(false);
            }
        }
        else if (selectedCard.equals("FileScreen")) {
            final LUIFirstScreen firstScreen = (LUIFirstScreen)this.cardPanel.getCard("FirstScreen");
            final LUIFileScreen fileScreen = (LUIFileScreen)this.cardPanel.getSelectedCard();
            final String licenseFile = firstScreen.fileTextField.getText().trim();
            final String userName2 = (String)fileScreen.userNameComboBox.getSelectedItem();
            if (this.valid == null) {
                this.valid = Validation.getInstance();
            }
            if (this.checkForRegister) {
                final boolean bool = this.valid.isRegisteredFile(licenseFile, userName2, true);
                if (!bool) {
                    return;
                }
            }
            final boolean success2 = this.valid.doValidation(this.lPath, userName2, licenseFile, true);
            if (success2) {
                this.valid.copyLicenseFile(this.lPath, licenseFile);
                if (this.defaultEvalFilePath != null) {
                    new File(this.defaultEvalFilePath).delete();
                }
                this.validationOver = true;
                this.setVisible(false);
            }
        }
        else if (selectedCard.equals("OptionScreen")) {
            final LUIOptionScreen opt2 = (LUIOptionScreen)this.cardPanel.getSelectedCard();
            if (opt2.registerRadioButton.isSelected()) {
                this.previousButton.setEnabled(true);
                this.cardPanel.showCard("FirstScreen");
            }
            else if (opt2.evalRadioButton.isSelected()) {
                if (!new File(this.evalFilePath).exists()) {
                    LUIGeneral.nextButton.setText("Next");
                    this.previousButton.setEnabled(true);
                    this.cardPanel.showCard("FirstScreen");
                    return;
                }
                if (this.valid == null) {
                    this.valid = Validation.getInstance();
                }
                final boolean success3 = this.valid.doValidation(this.lPath, "Evaluation User", this.defaultEvalFilePath, true);
                if (success3) {
                    this.valid.copyLicenseFile(this.lPath, this.defaultEvalFilePath);
                    new File(this.defaultEvalFilePath).delete();
                    this.validationOver = true;
                    this.setVisible(false);
                }
            }
            else {
                if (this.valid == null) {
                    this.valid = Validation.getInstance();
                }
                final boolean success3 = this.valid.doValidation(this.lPath, "Evaluation User", this.freeFilePath, true);
                if (success3) {
                    this.valid.copyLicenseFile(this.lPath, this.freeFilePath);
                    this.validationOver = true;
                    this.setVisible(false);
                }
            }
        }
    }
    
    private void agreement(final String path) {
        final String filePath = path + "LICENSE_AGREEMENT";
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                final Indication util = Indication.getInstance();
                f = util.getTheFile("LICENSE_AGREEMENT");
            }
            catch (final Exception e) {
                System.out.println("Error while reading license agreement");
            }
        }
        try {
            final DataInputStream lData = new DataInputStream(new FileInputStream(f));
            String dataBuffer;
            while ((dataBuffer = lData.readLine()) != null) {
                final LUIAgreement comp = (LUIAgreement)this.cardPanel.getCard("AgreementPanel");
                comp.agreementTextArea.append(dataBuffer + "\n");
            }
        }
        catch (final Exception e) {
            System.out.println("Unable to read agreement ");
        }
    }
    
    public boolean isVRTUser() {
        return this.vrtuser;
    }
    
    public boolean isValidationOk() {
        return this.validationOver;
    }
    
    public void setValidation(final boolean b) {
        this.validationOver = b;
        this.regScreencanexit = false;
    }
    
    private void centerWindow(final Component comp) {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final Point screenCenter = new Point(dim.width / 2, dim.height / 2);
        int x = screenCenter.x - comp.getSize().width / 2;
        int y = screenCenter.y - comp.getSize().height / 2;
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        comp.setLocation(x, y);
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
    
    public void setProperties(final Properties props) {
    }
    
    public LUIGeneral() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JSeparator1 = null;
        this.cardTitleLabel = null;
        this.JPanel2 = null;
        this.cardPanel = null;
        this.imageLabel = null;
        this.JPanel1 = null;
        this.previousButton = null;
        this.exitButton = null;
        this.cons = new GridBagConstraints();
        this.lPath = "";
        this.freeFilePath = LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "Free.xml";
        this.evalFilePath = LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "StandardEvaluation.xml";
        this.title = null;
        this.vrtuser = false;
        this.validationOver = false;
        this.regScreencanexit = true;
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.valid = null;
        this.checkForRegister = false;
        this.evalStandard = false;
        this.defaultEvalFilePath = null;
        this.pack();
    }
    
    public LUIGeneral(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JSeparator1 = null;
        this.cardTitleLabel = null;
        this.JPanel2 = null;
        this.cardPanel = null;
        this.imageLabel = null;
        this.JPanel1 = null;
        this.previousButton = null;
        this.exitButton = null;
        this.cons = new GridBagConstraints();
        this.lPath = "";
        this.freeFilePath = LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "Free.xml";
        this.evalFilePath = LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "StandardEvaluation.xml";
        this.title = null;
        this.vrtuser = false;
        this.validationOver = false;
        this.regScreencanexit = true;
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.valid = null;
        this.checkForRegister = false;
        this.evalStandard = false;
        this.defaultEvalFilePath = null;
        this.applet = applet;
        this.pack();
    }
    
    public Validation getValidation() {
        return this.valid;
    }
    
    static {
        param = new String[0];
        LUIGeneral.nextButton = null;
    }
    
    class previousButton_cardPanel_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            LUIGeneral.this.licensePreviousAction();
        }
    }
    
    class exitButton_cardPanel_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            if (LUIGeneral.this.regScreencanexit) {
                System.exit(0);
                LUIGeneral.this.validationOver = true;
            }
            else {
                LUIGeneral.this.setVisible(false);
                LUIGeneral.this.validationOver = true;
            }
        }
    }
    
    class JButton1_cardPanel_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            LUIGeneral.this.licenseNextAction();
        }
    }
}
