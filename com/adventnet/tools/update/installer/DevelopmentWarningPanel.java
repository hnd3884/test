package com.adventnet.tools.update.installer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.Locale;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Font;
import com.adventnet.tools.update.CommonUtil;
import javax.swing.Icon;
import java.applet.Applet;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Color;
import java.util.logging.Logger;
import javax.swing.JPanel;

public class DevelopmentWarningPanel extends JPanel
{
    private static final Logger LOGGER;
    private static final Color DEFAULT_BACKGROUND_COLOR;
    private static final long serialVersionUID = -4731383341635089045L;
    private boolean initialized;
    JPanel topPanel;
    JPanel buttonPanel;
    JLabel alertIconLabel;
    JLabel alertLabel;
    JLabel contextLabel;
    JTextArea alertDescription;
    JTextArea advancedDescription;
    JButton exitButton;
    JButton advancedButton;
    JButton proceedButton;
    JSeparator jSeparator;
    GridBagConstraints cons;
    static final Insets ICON_INSETS;
    static final Insets LABEL_INSETS;
    static final Insets TEXT_INSETS;
    
    public DevelopmentWarningPanel(final Context context) {
        this.initialized = false;
        this.topPanel = new JPanel();
        this.buttonPanel = new JPanel();
        this.alertIconLabel = new JLabel();
        this.alertLabel = new JLabel();
        this.contextLabel = new JLabel();
        this.alertDescription = new JTextArea();
        this.advancedDescription = new JTextArea();
        this.exitButton = new JButton();
        this.advancedButton = new JButton();
        this.proceedButton = new JButton();
        this.jSeparator = new JSeparator();
        this.cons = new GridBagConstraints();
        this.init(context);
    }
    
    private void init(final Context context) {
        if (this.initialized) {
            return;
        }
        this.initContainer();
        this.initComponents(context);
        final Container container = this;
        container.setLayout(new BorderLayout());
        container.add(this.topPanel);
        this.initActions(context);
        this.initialized = true;
    }
    
    void initComponents(final Context context) {
        this.alertIconLabel.setHorizontalAlignment(2);
        this.alertIconLabel.setFont(UpdateManagerUtil.getFont());
        this.alertIconLabel.setForeground(Color.BLACK);
        this.alertIconLabel.setHorizontalTextPosition(4);
        this.alertIconLabel.setVerticalTextPosition(0);
        this.alertIconLabel.setVerticalAlignment(0);
        this.alertIconLabel.setIcon(Utility.findImage("com/adventnet/tools/update/installer/images/n_warning.png", null, true));
        this.alertLabel.setHorizontalAlignment(2);
        this.alertLabel.setForeground(new Color(229, 164, 47));
        this.alertLabel.setHorizontalTextPosition(4);
        this.alertLabel.setText(CommonUtil.getString(MessageConstants.SELF_SIGNED_WARNING_HEADER).toUpperCase());
        this.alertLabel.setFont(new Font(UpdateManagerUtil.getBoldFont().getFontName(), 1, 14));
        this.contextLabel.setHorizontalAlignment(0);
        this.contextLabel.setForeground(Color.BLACK);
        this.contextLabel.setHorizontalTextPosition(0);
        this.contextLabel.setFont(new Font(UpdateManagerUtil.getBoldFont().getFontName(), 1, 14));
        if (context == Context.CERTIFICATE) {
            this.contextLabel.setText(CommonUtil.getString(MessageConstants.SELF_SIGNED_CERTIFICATE_WARNING_TITLE).toUpperCase());
            this.alertDescription.setText(CommonUtil.getString(MessageConstants.IMPORT_SELF_SIGNED_CERTIFICATE));
        }
        else if (context == Context.PATCH) {
            this.contextLabel.setText(CommonUtil.getString(MessageConstants.SELF_SIGNED_PATCH_WARNING_TITLE).toUpperCase());
            this.alertDescription.setText(CommonUtil.getString(MessageConstants.SELF_SIGNED_WARNING_MESSAGE));
        }
        this.alertDescription.setFont(UpdateManagerUtil.getFont());
        this.alertDescription.setForeground(Color.BLACK);
        this.alertDescription.setBackground(DevelopmentWarningPanel.DEFAULT_BACKGROUND_COLOR);
        this.alertDescription.setLineWrap(true);
        this.alertDescription.setWrapStyleWord(true);
        this.alertDescription.setEditable(false);
        this.advancedDescription.setFont(UpdateManagerUtil.getFont());
        this.advancedDescription.setForeground(Color.BLACK);
        this.advancedDescription.setBackground(DevelopmentWarningPanel.DEFAULT_BACKGROUND_COLOR);
        this.advancedDescription.setLineWrap(true);
        this.advancedDescription.setWrapStyleWord(true);
        this.advancedDescription.setEditable(false);
        this.advancedDescription.setText(CommonUtil.getString(MessageConstants.ACCEPT_RISKS_MESSAGE));
        this.advancedDescription.setVisible(false);
        this.jSeparator.setVisible(false);
        this.exitButton.setFont(new Font("SansSerif", 0, 12));
        this.exitButton.setHorizontalTextPosition(4);
        this.exitButton.setText(CommonUtil.getString(MessageConstants.EXIT) + " (" + CommonUtil.getString(MessageConstants.RECOMMENDED) + ")");
        this.advancedButton.setFont(new Font("SansSerif", 0, 12));
        this.advancedButton.setHorizontalTextPosition(4);
        this.advancedButton.setText(CommonUtil.getString(MessageConstants.ADVANCED));
        this.proceedButton.setFont(new Font("SansSerif", 0, 12));
        this.proceedButton.setHorizontalTextPosition(4);
        this.proceedButton.setText(CommonUtil.getString(MessageConstants.PROCEED));
    }
    
    void initContainer() {
        this.topPanel.setLayout(new GridBagLayout());
        this.setConstraints(0, 0, 2, 1, 0.1, 0.0, 10, 1, DevelopmentWarningPanel.LABEL_INSETS, 0, 0);
        this.topPanel.add(this.contextLabel, this.cons);
        this.setConstraints(0, 1, 1, 2, 0.0, 0.0, 10, 1, DevelopmentWarningPanel.ICON_INSETS, 0, 0);
        this.topPanel.add(this.alertIconLabel, this.cons);
        this.setConstraints(1, 1, 1, 1, 0.0, 0.0, 10, 2, DevelopmentWarningPanel.LABEL_INSETS, 0, 0);
        this.topPanel.add(this.alertLabel, this.cons);
        this.setConstraints(1, 2, 2, 1, 0.1, 0.0, 10, 2, DevelopmentWarningPanel.TEXT_INSETS, 0, 0);
        this.topPanel.add(this.alertDescription, this.cons);
        this.setConstraints(1, 3, 1, 1, 0.1, 0.0, 10, 2, DevelopmentWarningPanel.TEXT_INSETS, 0, 0);
        this.topPanel.add(this.jSeparator, this.cons);
        this.setConstraints(1, 4, 1, 1, 0.1, 0.0, 10, 2, DevelopmentWarningPanel.TEXT_INSETS, 0, 0);
        this.topPanel.add(this.advancedDescription, this.cons);
        this.buttonPanel.setLayout(new FlowLayout(2, 5, 5));
        this.buttonPanel.add(this.exitButton);
        this.buttonPanel.add(this.advancedButton);
        this.setConstraints(0, 5, 0, 1, 0.1, 0.0, 10, 2, DevelopmentWarningPanel.TEXT_INSETS, 0, 0);
        this.topPanel.add(this.buttonPanel, this.cons);
    }
    
    void initActions(final Context context) {
        this.exitButton.addActionListener(new ExitButtonAction(context));
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
    
    static {
        LOGGER = Logger.getLogger(DevelopmentWarningPanel.class.getName());
        DEFAULT_BACKGROUND_COLOR = UIManager.getColor("Panel.background");
        ICON_INSETS = new Insets(5, 15, 5, 5);
        LABEL_INSETS = new Insets(15, 10, 5, 5);
        TEXT_INSETS = new Insets(5, 10, 5, 5);
    }
    
    static class ExitButtonAction implements ActionListener
    {
        private final Context context;
        
        public ExitButtonAction(final Context context) {
            this.context = context;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            try {
                UpdateManagerUtil.audit("Pressed Exit button - self signed " + this.context.name().toLowerCase(Locale.ENGLISH));
                UpdateManagerUtil.audit("Rejecting consent for Self Signed " + this.context.name().toLowerCase(Locale.ENGLISH));
            }
            catch (final IOException e) {
                DevelopmentWarningPanel.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            System.exit(1);
        }
    }
    
    enum Context
    {
        CERTIFICATE, 
        PATCH;
    }
}
