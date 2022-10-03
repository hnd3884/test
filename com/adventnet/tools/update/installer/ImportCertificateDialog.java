package com.adventnet.tools.update.installer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;
import com.zoho.tools.CertificateUtil;
import java.io.File;
import java.awt.FlowLayout;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.logging.Level;
import java.net.URI;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import javax.swing.JEditorPane;
import java.awt.GridBagLayout;
import java.awt.Component;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import java.applet.Applet;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.awt.Font;
import java.awt.Container;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import com.adventnet.tools.update.CommonUtil;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.util.logging.Logger;
import javax.swing.JDialog;

public class ImportCertificateDialog extends JDialog
{
    private static final Logger LOGGER;
    private static final Color DEFAULT_BACKGROUND_COLOR;
    private boolean initialized;
    JDialog currentComponent;
    DevelopmentWarningPanel developmentWarningPanel;
    JPanel importCertPanel;
    JPanel importCertBottomPanel;
    JLabel importCertImage;
    JLabel importCertLabel;
    JTextArea importCertInfoText;
    JButton importCertButton;
    JButton browseCert;
    JButton backButton;
    JTextField importCertPath;
    GridBagConstraints cons;
    static final Insets PANEL_INSETS;
    static final Insets INPUT_INSETS;
    static final Insets ICON_INSETS;
    static final Insets LABEL_INSETS;
    static final Insets TEXT_INSETS;
    
    public ImportCertificateDialog() {
        this.initialized = false;
        this.developmentWarningPanel = new DevelopmentWarningPanel(DevelopmentWarningPanel.Context.CERTIFICATE);
        this.importCertPanel = new JPanel();
        this.importCertBottomPanel = new JPanel();
        this.importCertImage = new JLabel();
        this.importCertLabel = new JLabel();
        this.importCertInfoText = new JTextArea();
        this.importCertButton = new JButton();
        this.browseCert = new JButton();
        this.backButton = new JButton();
        this.importCertPath = new JTextField();
        this.cons = new GridBagConstraints();
        (this.currentComponent = this).pack();
    }
    
    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            this.init();
        }
        super.setVisible(visible);
    }
    
    private void init() {
        if (this.initialized) {
            return;
        }
        this.setSize(this.getPreferredSize().width + 525, this.getPreferredSize().height + 240);
        this.setMinimumSize(new Dimension(this.getPreferredSize().width + 525, this.getPreferredSize().height + 240));
        Assorted.positionTheWindow(this, "Center");
        this.setLocation(Assorted.screenCenter.x - this.getSize().width / 2, Assorted.screenCenter.y - this.getSize().height / 2 - 140);
        this.setResizable(true);
        this.setTitle(CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE));
        final Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        this.initComponents();
        this.initContainer(container);
        this.initActions();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                ImportCertificateDialog.this.setVisible(false);
                ImportCertificateDialog.this.dispose();
            }
        });
        this.setModal(true);
        this.initialized = true;
    }
    
    void initComponents() {
        this.importCertPath.setHorizontalAlignment(2);
        this.importCertPath.setFont(new Font("SansSerif", 0, 12));
        this.importCertPath.setMinimumSize(new Dimension(4, 24));
        this.importCertPath.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        this.importCertPath.setPreferredSize(new Dimension(4, 24));
        this.browseCert.setFont(new Font("SansSerif", 0, 12));
        this.browseCert.setHorizontalTextPosition(4);
        this.browseCert.setText(CommonUtil.getString("Browse"));
        this.browseCert.setMnemonic('b');
        this.importCertImage.setHorizontalAlignment(2);
        this.importCertImage.setFont(UpdateManagerUtil.getFont());
        this.importCertImage.setForeground(Color.BLACK);
        this.importCertImage.setHorizontalTextPosition(4);
        this.importCertImage.setVerticalTextPosition(0);
        this.importCertImage.setVerticalAlignment(0);
        this.importCertImage.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/import.png", null, true));
        this.importCertLabel.setHorizontalAlignment(2);
        this.importCertLabel.setForeground(Color.BLACK);
        this.importCertLabel.setHorizontalTextPosition(4);
        this.importCertLabel.setText(CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE).toUpperCase());
        this.importCertLabel.setFont(UpdateManagerUtil.getBoldFont());
        this.importCertInfoText.setFont(UpdateManagerUtil.getFont());
        this.importCertInfoText.setForeground(Color.BLACK);
        this.importCertInfoText.setBackground(ImportCertificateDialog.DEFAULT_BACKGROUND_COLOR);
        this.importCertInfoText.setLineWrap(true);
        this.importCertInfoText.setWrapStyleWord(true);
        this.importCertInfoText.setEditable(false);
        this.importCertInfoText.setText(CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE_DESCRIPTION));
        this.importCertButton.setFont(new Font("SansSerif", 0, 12));
        this.importCertButton.setHorizontalTextPosition(4);
        this.importCertButton.setText(CommonUtil.getString(MessageConstants.IMPORT));
        this.importCertButton.setEnabled(false);
        this.backButton.setFont(new Font("SansSerif", 0, 12));
        this.backButton.setHorizontalTextPosition(4);
        this.backButton.setText(CommonUtil.getString(MessageConstants.BACK));
    }
    
    void initActions() {
        this.browseCert.addActionListener(new BrowseCertificateButtonAction());
        this.importCertPath.addKeyListener(new ImportCertificatePathAction());
        this.importCertButton.addActionListener(new ImportCertificateButtonAction());
        this.backButton.addActionListener(new BackButtonAction());
        this.developmentWarningPanel.advancedButton.addActionListener(new AdvancedButtonAction());
        this.developmentWarningPanel.proceedButton.addActionListener(new ProceedButtonAction());
    }
    
    void initContainer(final Container container) {
        container.add(this.importCertPanel, "Center");
        this.importCertPanel.setLayout(new GridBagLayout());
        this.setConstraints(0, 0, 1, 2, 0.0, 0.0, 10, 1, ImportCertificateDialog.ICON_INSETS, 0, 0);
        this.importCertPanel.add(this.importCertImage, this.cons);
        this.setConstraints(1, 0, 1, 1, 0.1, 0.0, 10, 2, ImportCertificateDialog.LABEL_INSETS, 0, 0);
        this.importCertPanel.add(this.importCertLabel, this.cons);
        this.setConstraints(1, 1, 2, 1, 0.0, 0.0, 10, 2, ImportCertificateDialog.LABEL_INSETS, 0, 0);
        if (this.importCertInfoText.getText().contains("${cert.url")) {
            final String desc = this.importCertInfoText.getText();
            String placeHolder = desc.substring(desc.indexOf("${cert.url"));
            placeHolder = placeHolder.substring(0, placeHolder.indexOf("}") + 1);
            String alias = CommonUtil.getString(MessageConstants.HERE);
            if (placeHolder.contains("[") && placeHolder.contains("]}")) {
                alias = placeHolder.substring(placeHolder.indexOf("[") + 1);
                alias = alias.substring(0, alias.indexOf("]}"));
            }
            final String html = "<a href=\"https://www.manageengine.com/certificate/ppmsigner_publickey.crt\">" + alias + "</a>";
            final String descPrefix = desc.substring(0, desc.indexOf("${cert.url")).replaceAll("<.*?>", "");
            final String descSuffix = desc.substring(desc.indexOf("]}") + 2).replaceAll("<.*?>", "");
            final JEditorPane importCertInfoHTML = new JEditorPane() {
                @Override
                public String getToolTipText(final MouseEvent evt) {
                    String text = null;
                    if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        final int pos = this.viewToModel(evt.getPoint());
                        if (pos >= 0) {
                            final HTMLDocument hdoc = (HTMLDocument)this.getDocument();
                            final Element e = hdoc.getCharacterElement(pos);
                            final AttributeSet a = e.getAttributes();
                            final SimpleAttributeSet value = (SimpleAttributeSet)a.getAttribute(HTML.Tag.A);
                            if (value != null) {
                                text = CommonUtil.getString(MessageConstants.CLICK_TO_COPY);
                            }
                        }
                    }
                    return text;
                }
            };
            importCertInfoHTML.setContentType("text/html");
            importCertInfoHTML.setText(descPrefix + html + descSuffix);
            importCertInfoHTML.setOpaque(false);
            importCertInfoHTML.setFont(UpdateManagerUtil.getFont());
            final String bodyRule = "body { font-family: " + UpdateManagerUtil.getFont().getFamily() + "; " + "font-size: " + UpdateManagerUtil.getFont().getSize() + "pt; }";
            ((HTMLDocument)importCertInfoHTML.getDocument()).getStyleSheet().addRule(bodyRule);
            importCertInfoHTML.setEditable(false);
            importCertInfoHTML.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(final HyperlinkEvent hyperlinkEvent) {
                    if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                            final Desktop desktop = Desktop.getDesktop();
                            try {
                                final URI uri = new URI("https://www.manageengine.com/certificate/ppmsigner_publickey.crt");
                                desktop.browse(uri);
                                return;
                            }
                            catch (final IOException | URISyntaxException ex) {
                                ImportCertificateDialog.LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                                ImportCertificateDialog.LOGGER.log(Level.WARNING, "Browse action failed. Hence using Copy Action");
                            }
                        }
                        final StringSelection stringSelection = new StringSelection("https://www.manageengine.com/certificate/ppmsigner_publickey.crt");
                        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                        String message = CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE_URL_COPIED_TO_CLIPBOARD);
                        if (message.contains("${cert.url}")) {
                            message = message.replace("${cert.url}", "https://www.manageengine.com/certificate/ppmsigner_publickey.crt");
                        }
                        UMOptionPane.showInformationDialog(ImportCertificateDialog.this.importCertPanel, message);
                    }
                }
            });
            ToolTipManager.sharedInstance().registerComponent(importCertInfoHTML);
            this.importCertPanel.add(importCertInfoHTML, this.cons);
        }
        else {
            this.importCertPanel.add(this.importCertInfoText, this.cons);
        }
        this.setConstraints(1, 2, 1, 1, 0.1, 0.0, 10, 2, ImportCertificateDialog.INPUT_INSETS, 0, 0);
        this.importCertPanel.add(this.importCertPath, this.cons);
        this.setConstraints(2, 2, 1, 1, 0.0, 0.0, 10, 2, ImportCertificateDialog.TEXT_INSETS, 0, 0);
        this.importCertPanel.add(this.browseCert, this.cons);
        this.setConstraints(0, 3, 0, 1, 0.0, 0.0, 10, 2, ImportCertificateDialog.PANEL_INSETS, 0, 0);
        this.importCertPanel.add(this.importCertBottomPanel, this.cons);
        this.setConstraints(0, 4, 0, 0, 0.0, 0.0, 10, 2, ImportCertificateDialog.PANEL_INSETS, 0, 0);
        this.importCertPanel.add(this.developmentWarningPanel, this.cons);
        this.developmentWarningPanel.setVisible(false);
        this.importCertBottomPanel.setLayout(new FlowLayout(2, 5, 5));
        this.importCertBottomPanel.add(this.backButton);
        this.importCertBottomPanel.add(this.importCertButton);
    }
    
    private void importCertificate(final String importCertFile) {
        try {
            final String password = UpdateManager.getInstanceConfig().getKeyStorePassword();
            CertificateUtil.importCertificate(importCertFile, UpdateManagerUtil.getHomeDirectory() + File.separator + "conf", password);
            final String alias = CertificateUtil.getAlias(importCertFile, UpdateManagerUtil.getHomeDirectory() + File.separator + "conf", password);
            ImportCertificateDialog.LOGGER.log(Level.INFO, "Certificate imported with alias {0}.", alias);
            UpdateManagerUtil.audit("Certificate imported with alias " + alias);
            UMOptionPane.showInformationDialog(this.currentComponent, CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE_SUCCESS));
            this.dispose();
        }
        catch (final Exception e) {
            ImportCertificateDialog.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            UMOptionPane.showErroDialog(this.currentComponent, CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE_FAILED));
        }
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
        LOGGER = Logger.getLogger(ImportCertificateDialog.class.getName());
        DEFAULT_BACKGROUND_COLOR = UIManager.getColor("Panel.background");
        PANEL_INSETS = new Insets(20, 5, 10, 5);
        INPUT_INSETS = new Insets(5, 4, 5, 5);
        ICON_INSETS = new Insets(5, 15, 5, 5);
        LABEL_INSETS = new Insets(15, 10, 5, 15);
        TEXT_INSETS = new Insets(5, 10, 5, 5);
    }
    
    class BrowseCertificateButtonAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            ImportCertificateDialog.this.browseCert.setEnabled(false);
            ImportCertificateDialog.this.setCursor(new Cursor(3));
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileSelectionMode(0);
            fileChooser.setDialogTitle(CommonUtil.getString(MessageConstants.SELECT_FILE));
            fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
            fileChooser.addChoosableFileFilter(new CertificateFilter());
            final int fileChosen = fileChooser.showOpenDialog(ImportCertificateDialog.this.currentComponent);
            ImportCertificateDialog.this.browseCert.setEnabled(true);
            if (fileChosen != 0) {
                ImportCertificateDialog.this.setCursor(new Cursor(0));
                return;
            }
            final File selFile = fileChooser.getSelectedFile();
            if (!selFile.exists()) {
                JOptionPane.showMessageDialog(ImportCertificateDialog.this.currentComponent, CommonUtil.getString(MessageConstants.FILE_NOT_EXISTS), CommonUtil.getString(MessageConstants.ERROR), 2);
                ImportCertificateDialog.this.setCursor(new Cursor(0));
            }
            ImportCertificateDialog.this.importCertPath.setText(selFile.getAbsolutePath().trim());
            ImportCertificateDialog.this.setCursor(new Cursor(0));
            ImportCertificateDialog.this.importCertButton.setEnabled(true);
            ImportCertificateDialog.this.importCertButton.requestFocus();
        }
    }
    
    class ImportCertificatePathAction extends KeyAdapter
    {
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
            final String certFile = ImportCertificateDialog.this.importCertPath.getText().trim();
            ImportCertificateDialog.this.importCertButton.setEnabled(!certFile.equals(""));
        }
    }
    
    class ImportCertificateButtonAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            ImportCertificateDialog.this.setCursor(new Cursor(3));
            final String importCertFile = ImportCertificateDialog.this.importCertPath.getText();
            if (!new File(importCertFile).exists()) {
                UMOptionPane.showErroDialog(ImportCertificateDialog.this.currentComponent, CommonUtil.getString(MessageConstants.CERTIFICATE_NOT_EXIST));
            }
            else {
                ImportCertificateDialog.this.importCertButton.setEnabled(false);
                ImportCertificateDialog.this.importCertPath.setEnabled(false);
                ImportCertificateDialog.this.browseCert.setEnabled(false);
                try {
                    try {
                        final String password = UpdateManager.getInstanceConfig().getKeyStorePassword();
                        if (CertificateUtil.isCertificateExists(importCertFile, UpdateManagerUtil.getHomeDirectory() + File.separator + "conf", password)) {
                            final String alias = CertificateUtil.getAlias(importCertFile, UpdateManagerUtil.getHomeDirectory() + File.separator + "conf", password);
                            ImportCertificateDialog.LOGGER.log(Level.INFO, "Certificate already exists with alias {0}.", alias);
                            UpdateManagerUtil.audit("Certificate already exists with alias " + alias);
                            UMOptionPane.showInformationDialog(ImportCertificateDialog.this.currentComponent, CommonUtil.getString(MessageConstants.CERTIFICATE_EXISTS));
                            ImportCertificateDialog.this.setCursor(new Cursor(0));
                            ImportCertificateDialog.this.dispose();
                            return;
                        }
                    }
                    catch (final Exception e) {
                        ImportCertificateDialog.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                    if (CertificateUtil.isSelfSigned(importCertFile)) {
                        UpdateManagerUtil.audit("Attempting to get consent for Self Signed certificate");
                        final Dimension dimension = new Dimension(ImportCertificateDialog.this.importCertPanel.getSize().width, ImportCertificateDialog.this.importCertPanel.getSize().height + 270);
                        ImportCertificateDialog.this.setSize(dimension);
                        ImportCertificateDialog.this.setMinimumSize(dimension);
                        ImportCertificateDialog.this.importCertPanel.getComponent(6).setVisible(true);
                        ImportCertificateDialog.this.importCertButton.setEnabled(false);
                        ImportCertificateDialog.this.backButton.setEnabled(false);
                    }
                    else {
                        ImportCertificateDialog.this.importCertificate(importCertFile);
                    }
                }
                catch (final Exception e) {
                    ImportCertificateDialog.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    UMOptionPane.showErroDialog(ImportCertificateDialog.this.currentComponent, CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE_FAILED));
                }
            }
            ImportCertificateDialog.this.setCursor(new Cursor(0));
        }
    }
    
    class AdvancedButtonAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (!ImportCertificateDialog.this.developmentWarningPanel.advancedDescription.isVisible()) {
                try {
                    UpdateManagerUtil.audit("Pressed Advanced button for L1 consent - self signed certificate");
                }
                catch (final IOException e) {
                    ImportCertificateDialog.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
                final Dimension dimension = new Dimension(ImportCertificateDialog.this.importCertPanel.getSize().width, ImportCertificateDialog.this.importCertPanel.getSize().height + 70);
                ImportCertificateDialog.this.setSize(dimension);
                ImportCertificateDialog.this.setMinimumSize(dimension);
                ImportCertificateDialog.this.developmentWarningPanel.jSeparator.setVisible(true);
                ImportCertificateDialog.this.developmentWarningPanel.advancedDescription.setVisible(true);
                ImportCertificateDialog.this.developmentWarningPanel.buttonPanel.remove(1);
                ImportCertificateDialog.this.developmentWarningPanel.buttonPanel.add(ImportCertificateDialog.this.developmentWarningPanel.proceedButton);
            }
        }
    }
    
    class BackButtonAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            ImportCertificateDialog.this.dispose();
        }
    }
    
    class ProceedButtonAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            try {
                UpdateManagerUtil.audit("Pressed Proceed button for L2 consent - self signed certificate");
                UpdateManagerUtil.audit("Consent obtained for Self Signed certificate");
            }
            catch (final IOException e) {
                ImportCertificateDialog.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            ImportCertificateDialog.this.setCursor(new Cursor(3));
            final String importCertFile = ImportCertificateDialog.this.importCertPath.getText();
            if (!new File(importCertFile).exists()) {
                UMOptionPane.showErroDialog(ImportCertificateDialog.this.currentComponent, CommonUtil.getString(MessageConstants.CERTIFICATE_NOT_EXIST));
            }
            else {
                ImportCertificateDialog.this.importCertificate(importCertFile);
            }
            ImportCertificateDialog.this.setCursor(new Cursor(0));
        }
    }
}
