package sun.print;

import javax.print.attribute.TextSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.SetOfIntegerSyntax;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobPriority;
import javax.print.attribute.standard.JobSheets;
import javax.swing.JTextField;
import javax.print.attribute.standard.Sides;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.MediaTray;
import java.util.Vector;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import java.util.Locale;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Copies;
import javax.swing.event.ChangeEvent;
import javax.print.attribute.standard.SheetCollate;
import javax.swing.SpinnerModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import javax.swing.JFormattedTextField;
import javax.print.attribute.standard.PageRanges;
import java.awt.event.FocusListener;
import javax.print.attribute.standard.PrinterInfo;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterMakeAndModel;
import java.security.Permission;
import javax.swing.event.PopupMenuEvent;
import java.awt.event.ItemEvent;
import java.awt.print.PrinterJob;
import javax.print.attribute.AttributeSet;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.print.ServiceUIFactory;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.io.FilePermission;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.AbstractButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import java.net.URL;
import java.security.AccessController;
import java.util.MissingResourceException;
import java.security.PrivilegedAction;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URI;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.Destination;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.Container;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.GraphicsConfiguration;
import javax.print.DocFlavor;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.PrintService;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.util.ResourceBundle;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

public class ServiceDialog extends JDialog implements ActionListener
{
    public static final int WAITING = 0;
    public static final int APPROVE = 1;
    public static final int CANCEL = 2;
    private static final String strBundle = "sun.print.resources.serviceui";
    private static final Insets panelInsets;
    private static final Insets compInsets;
    private static ResourceBundle messageRB;
    private JTabbedPane tpTabs;
    private JButton btnCancel;
    private JButton btnApprove;
    private PrintService[] services;
    private int defaultServiceIndex;
    private PrintRequestAttributeSet asOriginal;
    private HashPrintRequestAttributeSet asCurrent;
    private PrintService psCurrent;
    private DocFlavor docFlavor;
    private int status;
    private ValidatingFileChooser jfc;
    private GeneralPanel pnlGeneral;
    private PageSetupPanel pnlPageSetup;
    private AppearancePanel pnlAppearance;
    private boolean isAWT;
    static Class _keyEventClazz;
    
    public ServiceDialog(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final PrintService[] array, final int n3, final DocFlavor docFlavor, final PrintRequestAttributeSet set, final Dialog dialog) {
        super(dialog, getMsg("dialog.printtitle"), true, graphicsConfiguration);
        this.isAWT = false;
        this.initPrintDialog(n, n2, array, n3, docFlavor, set);
    }
    
    public ServiceDialog(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final PrintService[] array, final int n3, final DocFlavor docFlavor, final PrintRequestAttributeSet set, final Frame frame) {
        super(frame, getMsg("dialog.printtitle"), true, graphicsConfiguration);
        this.isAWT = false;
        this.initPrintDialog(n, n2, array, n3, docFlavor, set);
    }
    
    void initPrintDialog(final int n, final int n2, final PrintService[] services, final int defaultServiceIndex, final DocFlavor docFlavor, final PrintRequestAttributeSet asOriginal) {
        this.services = services;
        this.defaultServiceIndex = defaultServiceIndex;
        this.asOriginal = asOriginal;
        this.asCurrent = new HashPrintRequestAttributeSet(asOriginal);
        this.psCurrent = services[defaultServiceIndex];
        this.docFlavor = docFlavor;
        if (asOriginal.get(SunPageSelection.class) != null) {
            this.isAWT = true;
        }
        if (asOriginal.get(DialogOnTop.class) != null) {
            this.setAlwaysOnTop(true);
        }
        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        (this.tpTabs = new JTabbedPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
        final String msg = getMsg("tab.general");
        final int vkMnemonic = getVKMnemonic("tab.general");
        this.pnlGeneral = new GeneralPanel();
        this.tpTabs.add(msg, this.pnlGeneral);
        this.tpTabs.setMnemonicAt(0, vkMnemonic);
        final String msg2 = getMsg("tab.pagesetup");
        final int vkMnemonic2 = getVKMnemonic("tab.pagesetup");
        this.pnlPageSetup = new PageSetupPanel();
        this.tpTabs.add(msg2, this.pnlPageSetup);
        this.tpTabs.setMnemonicAt(1, vkMnemonic2);
        final String msg3 = getMsg("tab.appearance");
        final int vkMnemonic3 = getVKMnemonic("tab.appearance");
        this.pnlAppearance = new AppearancePanel();
        this.tpTabs.add(msg3, this.pnlAppearance);
        this.tpTabs.setMnemonicAt(2, vkMnemonic3);
        contentPane.add(this.tpTabs, "Center");
        this.updatePanels();
        final JPanel panel = new JPanel(new FlowLayout(4));
        panel.add(this.btnApprove = createExitButton("button.print", this));
        this.getRootPane().setDefaultButton(this.btnApprove);
        this.handleEscKey(this.btnCancel = createExitButton("button.cancel", this));
        panel.add(this.btnCancel);
        contentPane.add(panel, "South");
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                ServiceDialog.this.dispose(2);
            }
        });
        this.getAccessibleContext().setAccessibleDescription(getMsg("dialog.printtitle"));
        this.setResizable(false);
        this.setLocation(n, n2);
        this.pack();
    }
    
    public ServiceDialog(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final PrintService printService, final DocFlavor docFlavor, final PrintRequestAttributeSet set, final Dialog dialog) {
        super(dialog, getMsg("dialog.pstitle"), true, graphicsConfiguration);
        this.isAWT = false;
        this.initPageDialog(n, n2, printService, docFlavor, set);
    }
    
    public ServiceDialog(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final PrintService printService, final DocFlavor docFlavor, final PrintRequestAttributeSet set, final Frame frame) {
        super(frame, getMsg("dialog.pstitle"), true, graphicsConfiguration);
        this.isAWT = false;
        this.initPageDialog(n, n2, printService, docFlavor, set);
    }
    
    void initPageDialog(final int n, final int n2, final PrintService psCurrent, final DocFlavor docFlavor, final PrintRequestAttributeSet asOriginal) {
        this.psCurrent = psCurrent;
        this.docFlavor = docFlavor;
        this.asOriginal = asOriginal;
        this.asCurrent = new HashPrintRequestAttributeSet(asOriginal);
        if (asOriginal.get(DialogOnTop.class) != null) {
            this.setAlwaysOnTop(true);
        }
        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this.pnlPageSetup = new PageSetupPanel(), "Center");
        this.pnlPageSetup.updateInfo();
        final JPanel panel = new JPanel(new FlowLayout(4));
        panel.add(this.btnApprove = createExitButton("button.ok", this));
        this.getRootPane().setDefaultButton(this.btnApprove);
        this.handleEscKey(this.btnCancel = createExitButton("button.cancel", this));
        panel.add(this.btnCancel);
        contentPane.add(panel, "South");
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                ServiceDialog.this.dispose(2);
            }
        });
        this.getAccessibleContext().setAccessibleDescription(getMsg("dialog.pstitle"));
        this.setResizable(false);
        this.setLocation(n, n2);
        this.pack();
    }
    
    private void handleEscKey(final JButton button) {
        final AbstractAction abstractAction = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                ServiceDialog.this.dispose(2);
            }
        };
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(27, 0);
        final InputMap inputMap = button.getInputMap(2);
        final ActionMap actionMap = button.getActionMap();
        if (inputMap != null && actionMap != null) {
            inputMap.put(keyStroke, "cancel");
            actionMap.put("cancel", abstractAction);
        }
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public PrintRequestAttributeSet getAttributes() {
        if (this.status == 1) {
            return this.asCurrent;
        }
        return this.asOriginal;
    }
    
    public PrintService getPrintService() {
        if (this.status == 1) {
            return this.psCurrent;
        }
        return null;
    }
    
    public void dispose(final int status) {
        this.status = status;
        super.dispose();
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        final Object source = actionEvent.getSource();
        boolean showFileChooser = false;
        if (source == this.btnApprove) {
            showFileChooser = true;
            if (this.pnlGeneral != null) {
                if (this.pnlGeneral.isPrintToFileRequested()) {
                    showFileChooser = this.showFileChooser();
                }
                else {
                    this.asCurrent.remove(Destination.class);
                }
            }
        }
        this.dispose(showFileChooser ? 1 : 2);
    }
    
    private boolean showFileChooser() {
        final Class<Destination> clazz = Destination.class;
        Destination destination = (Destination)this.asCurrent.get(clazz);
        if (destination == null) {
            destination = (Destination)this.asOriginal.get(clazz);
            if (destination == null) {
                destination = (Destination)this.psCurrent.getDefaultAttributeValue(clazz);
                if (destination == null) {
                    try {
                        destination = new Destination(new URI("file:out.prn"));
                    }
                    catch (final URISyntaxException ex) {}
                }
            }
        }
        File selectedFile;
        if (destination != null) {
            try {
                selectedFile = new File(destination.getURI());
            }
            catch (final Exception ex2) {
                selectedFile = new File("out.prn");
            }
        }
        else {
            selectedFile = new File("out.prn");
        }
        final ValidatingFileChooser validatingFileChooser = new ValidatingFileChooser();
        validatingFileChooser.setApproveButtonText(getMsg("button.ok"));
        validatingFileChooser.setDialogTitle(getMsg("dialog.printtofile"));
        validatingFileChooser.setDialogType(1);
        validatingFileChooser.setSelectedFile(selectedFile);
        final int showDialog = validatingFileChooser.showDialog(this, null);
        if (showDialog == 0) {
            final File selectedFile2 = validatingFileChooser.getSelectedFile();
            try {
                this.asCurrent.add(new Destination(selectedFile2.toURI()));
            }
            catch (final Exception ex3) {
                this.asCurrent.remove(clazz);
            }
        }
        else {
            this.asCurrent.remove(clazz);
        }
        return showDialog == 0;
    }
    
    private void updatePanels() {
        this.pnlGeneral.updateInfo();
        this.pnlPageSetup.updateInfo();
        this.pnlAppearance.updateInfo();
    }
    
    public static void initResource() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    ServiceDialog.messageRB = ResourceBundle.getBundle("sun.print.resources.serviceui");
                    return null;
                }
                catch (final MissingResourceException ex) {
                    throw new Error("Fatal: Resource for ServiceUI is missing");
                }
            }
        });
    }
    
    public static String getMsg(final String s) {
        try {
            return removeMnemonics(ServiceDialog.messageRB.getString(s));
        }
        catch (final MissingResourceException ex) {
            throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + s + " key in resource");
        }
    }
    
    private static String removeMnemonics(final String s) {
        final int index = s.indexOf(38);
        final int length = s.length();
        if (index < 0 || index == length - 1) {
            return s;
        }
        final int index2 = s.indexOf(38, index + 1);
        if (index2 == index + 1) {
            if (index2 + 1 == length) {
                return s.substring(0, index + 1);
            }
            return s.substring(0, index + 1) + removeMnemonics(s.substring(index2 + 1));
        }
        else {
            if (index == 0) {
                return removeMnemonics(s.substring(1));
            }
            return s.substring(0, index) + removeMnemonics(s.substring(index + 1));
        }
    }
    
    private static char getMnemonic(final String s) {
        final String replace = ServiceDialog.messageRB.getString(s).replace("&&", "");
        final int index = replace.indexOf(38);
        if (0 <= index && index < replace.length() - 1) {
            return Character.toUpperCase(replace.charAt(index + 1));
        }
        return '\0';
    }
    
    private static int getVKMnemonic(final String s) {
        final String value = String.valueOf(getMnemonic(s));
        if (value == null || value.length() != 1) {
            return 0;
        }
        final String string = "VK_" + value.toUpperCase();
        try {
            if (ServiceDialog._keyEventClazz == null) {
                ServiceDialog._keyEventClazz = Class.forName("java.awt.event.KeyEvent", true, ServiceDialog.class.getClassLoader());
            }
            return ServiceDialog._keyEventClazz.getDeclaredField(string).getInt(null);
        }
        catch (final Exception ex) {
            return 0;
        }
    }
    
    private static URL getImageResource(final String s) {
        final URL url = AccessController.doPrivileged((PrivilegedAction<URL>)new PrivilegedAction() {
            @Override
            public Object run() {
                return ServiceDialog.class.getResource("resources/" + s);
            }
        });
        if (url == null) {
            throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + s + " key in resource");
        }
        return url;
    }
    
    private static JButton createButton(final String s, final ActionListener actionListener) {
        final JButton button = new JButton(getMsg(s));
        button.setMnemonic(getMnemonic(s));
        button.addActionListener(actionListener);
        return button;
    }
    
    private static JButton createExitButton(final String s, final ActionListener actionListener) {
        final String msg = getMsg(s);
        final JButton button = new JButton(msg);
        button.addActionListener(actionListener);
        button.getAccessibleContext().setAccessibleDescription(msg);
        return button;
    }
    
    private static JCheckBox createCheckBox(final String s, final ActionListener actionListener) {
        final JCheckBox checkBox = new JCheckBox(getMsg(s));
        checkBox.setMnemonic(getMnemonic(s));
        checkBox.addActionListener(actionListener);
        return checkBox;
    }
    
    private static JRadioButton createRadioButton(final String s, final ActionListener actionListener) {
        final JRadioButton radioButton = new JRadioButton(getMsg(s));
        radioButton.setMnemonic(getMnemonic(s));
        radioButton.addActionListener(actionListener);
        return radioButton;
    }
    
    public static void showNoPrintService(final GraphicsConfiguration graphicsConfiguration) {
        final Frame frame = new Frame(graphicsConfiguration);
        JOptionPane.showMessageDialog(frame, getMsg("dialog.noprintermsg"));
        frame.dispose();
    }
    
    private static void addToGB(final Component component, final Container container, final GridBagLayout gridBagLayout, final GridBagConstraints gridBagConstraints) {
        gridBagLayout.setConstraints(component, gridBagConstraints);
        container.add(component);
    }
    
    private static void addToBG(final AbstractButton abstractButton, final Container container, final ButtonGroup buttonGroup) {
        buttonGroup.add(abstractButton);
        container.add(abstractButton);
    }
    
    static {
        panelInsets = new Insets(6, 6, 6, 6);
        compInsets = new Insets(3, 6, 3, 6);
        initResource();
        ServiceDialog._keyEventClazz = null;
    }
    
    private class GeneralPanel extends JPanel
    {
        private PrintServicePanel pnlPrintService;
        private PrintRangePanel pnlPrintRange;
        private CopiesPanel pnlCopies;
        
        public GeneralPanel() {
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            gridBagConstraints.fill = 1;
            gridBagConstraints.insets = ServiceDialog.panelInsets;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.gridwidth = 0;
            addToGB(this.pnlPrintService = new PrintServicePanel(), this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = -1;
            addToGB(this.pnlPrintRange = new PrintRangePanel(), this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.pnlCopies = new CopiesPanel(), this, layout, gridBagConstraints);
        }
        
        public boolean isPrintToFileRequested() {
            return this.pnlPrintService.isPrintToFileSelected();
        }
        
        public void updateInfo() {
            this.pnlPrintService.updateInfo();
            this.pnlPrintRange.updateInfo();
            this.pnlCopies.updateInfo();
        }
    }
    
    private class PrintServicePanel extends JPanel implements ActionListener, ItemListener, PopupMenuListener
    {
        private final String strTitle;
        private FilePermission printToFilePermission;
        private JButton btnProperties;
        private JCheckBox cbPrintToFile;
        private JComboBox cbName;
        private JLabel lblType;
        private JLabel lblStatus;
        private JLabel lblInfo;
        private ServiceUIFactory uiFactory;
        private boolean changedService;
        private boolean filePermission;
        
        public PrintServicePanel() {
            this.strTitle = ServiceDialog.getMsg("border.printservice");
            this.changedService = false;
            this.uiFactory = ServiceDialog.this.psCurrent.getServiceUIFactory();
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            final String[] array = new String[ServiceDialog.this.services.length];
            for (int i = 0; i < array.length; ++i) {
                array[i] = ServiceDialog.this.services[i].getName();
            }
            (this.cbName = new JComboBox(array)).setSelectedIndex(ServiceDialog.this.defaultServiceIndex);
            this.cbName.addItemListener(this);
            this.cbName.addPopupMenuListener(this);
            gridBagConstraints.fill = 1;
            gridBagConstraints.insets = ServiceDialog.compInsets;
            gridBagConstraints.weightx = 0.0;
            final JLabel label = new JLabel(ServiceDialog.getMsg("label.psname"), 11);
            label.setDisplayedMnemonic(getMnemonic("label.psname"));
            label.setLabelFor(this.cbName);
            addToGB(label, this, layout, gridBagConstraints);
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = -1;
            addToGB(this.cbName, this, layout, gridBagConstraints);
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridwidth = 0;
            addToGB(this.btnProperties = createButton("button.properties", this), this, layout, gridBagConstraints);
            gridBagConstraints.weighty = 1.0;
            (this.lblStatus = this.addLabel(ServiceDialog.getMsg("label.status"), layout, gridBagConstraints)).setLabelFor(null);
            (this.lblType = this.addLabel(ServiceDialog.getMsg("label.pstype"), layout, gridBagConstraints)).setLabelFor(null);
            gridBagConstraints.gridwidth = 1;
            addToGB(new JLabel(ServiceDialog.getMsg("label.info"), 11), this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = -1;
            (this.lblInfo = new JLabel()).setLabelFor(null);
            addToGB(this.lblInfo, this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.cbPrintToFile = createCheckBox("checkbox.printtofile", this), this, layout, gridBagConstraints);
            this.filePermission = this.allowedToPrintToFile();
        }
        
        public boolean isPrintToFileSelected() {
            return this.cbPrintToFile.isSelected();
        }
        
        private JLabel addLabel(final String s, final GridBagLayout gridBagLayout, final GridBagConstraints gridBagConstraints) {
            gridBagConstraints.gridwidth = 1;
            addToGB(new JLabel(s, 11), this, gridBagLayout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            final JLabel label = new JLabel();
            addToGB(label, this, gridBagLayout, gridBagConstraints);
            return label;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (actionEvent.getSource() == this.btnProperties && this.uiFactory != null) {
                final JDialog dialog = (JDialog)this.uiFactory.getUI(3, "javax.swing.JDialog");
                if (dialog != null) {
                    dialog.show();
                }
                else {
                    DocumentPropertiesUI documentPropertiesUI = null;
                    try {
                        documentPropertiesUI = (DocumentPropertiesUI)this.uiFactory.getUI(199, DocumentPropertiesUI.DOCPROPERTIESCLASSNAME);
                    }
                    catch (final Exception ex) {}
                    if (documentPropertiesUI != null) {
                        final PrinterJobWrapper printerJobWrapper = (PrinterJobWrapper)ServiceDialog.this.asCurrent.get(PrinterJobWrapper.class);
                        if (printerJobWrapper == null) {
                            return;
                        }
                        final PrinterJob printerJob = printerJobWrapper.getPrinterJob();
                        if (printerJob == null) {
                            return;
                        }
                        final PrintRequestAttributeSet showDocumentProperties = documentPropertiesUI.showDocumentProperties(printerJob, ServiceDialog.this, ServiceDialog.this.psCurrent, ServiceDialog.this.asCurrent);
                        if (showDocumentProperties != null) {
                            ServiceDialog.this.asCurrent.addAll(showDocumentProperties);
                            ServiceDialog.this.updatePanels();
                        }
                    }
                }
            }
        }
        
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            if (itemEvent.getStateChange() == 1) {
                final int selectedIndex = this.cbName.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < ServiceDialog.this.services.length && !ServiceDialog.this.services[selectedIndex].equals(ServiceDialog.this.psCurrent)) {
                    ServiceDialog.this.psCurrent = ServiceDialog.this.services[selectedIndex];
                    this.uiFactory = ServiceDialog.this.psCurrent.getServiceUIFactory();
                    this.changedService = true;
                    final Destination destination = (Destination)ServiceDialog.this.asOriginal.get(Destination.class);
                    if ((destination != null || this.isPrintToFileSelected()) && ServiceDialog.this.psCurrent.isAttributeCategorySupported(Destination.class)) {
                        if (destination != null) {
                            ServiceDialog.this.asCurrent.add(destination);
                        }
                        else {
                            Destination destination2 = (Destination)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Destination.class);
                            if (destination2 == null) {
                                try {
                                    destination2 = new Destination(new URI("file:out.prn"));
                                }
                                catch (final URISyntaxException ex) {}
                            }
                            if (destination2 != null) {
                                ServiceDialog.this.asCurrent.add(destination2);
                            }
                        }
                    }
                    else {
                        ServiceDialog.this.asCurrent.remove(Destination.class);
                    }
                }
            }
        }
        
        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent popupMenuEvent) {
            this.changedService = false;
        }
        
        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent popupMenuEvent) {
            if (this.changedService) {
                this.changedService = false;
                ServiceDialog.this.updatePanels();
            }
        }
        
        @Override
        public void popupMenuCanceled(final PopupMenuEvent popupMenuEvent) {
        }
        
        private boolean allowedToPrintToFile() {
            try {
                this.throwPrintToFile();
                return true;
            }
            catch (final SecurityException ex) {
                return false;
            }
        }
        
        private void throwPrintToFile() {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                if (this.printToFilePermission == null) {
                    this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
                }
                securityManager.checkPermission(this.printToFilePermission);
            }
        }
        
        public void updateInfo() {
            final Class<Destination> clazz = Destination.class;
            boolean b = false;
            boolean b2 = false;
            final boolean b3 = this.filePermission && this.allowedToPrintToFile();
            if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
                b = true;
            }
            if (ServiceDialog.this.asCurrent.get(clazz) != null) {
                b2 = true;
            }
            this.cbPrintToFile.setEnabled(b && b3);
            this.cbPrintToFile.setSelected(b2 && b3 && b);
            final PrinterMakeAndModel attribute = ServiceDialog.this.psCurrent.getAttribute(PrinterMakeAndModel.class);
            if (attribute != null) {
                this.lblType.setText(attribute.toString());
            }
            final PrinterIsAcceptingJobs attribute2 = ServiceDialog.this.psCurrent.getAttribute(PrinterIsAcceptingJobs.class);
            if (attribute2 != null) {
                this.lblStatus.setText(ServiceDialog.getMsg(attribute2.toString()));
            }
            final PrinterInfo attribute3 = ServiceDialog.this.psCurrent.getAttribute(PrinterInfo.class);
            if (attribute3 != null) {
                this.lblInfo.setText(attribute3.toString());
            }
            this.btnProperties.setEnabled(this.uiFactory != null);
        }
    }
    
    private class PrintRangePanel extends JPanel implements ActionListener, FocusListener
    {
        private final String strTitle;
        private final PageRanges prAll;
        private JRadioButton rbAll;
        private JRadioButton rbPages;
        private JRadioButton rbSelect;
        private JFormattedTextField tfRangeFrom;
        private JFormattedTextField tfRangeTo;
        private JLabel lblRangeTo;
        private boolean prSupported;
        
        public PrintRangePanel() {
            this.strTitle = ServiceDialog.getMsg("border.printrange");
            this.prAll = new PageRanges(1, Integer.MAX_VALUE);
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            gridBagConstraints.fill = 1;
            gridBagConstraints.insets = ServiceDialog.compInsets;
            gridBagConstraints.gridwidth = 0;
            final ButtonGroup buttonGroup = new ButtonGroup();
            final JPanel panel = new JPanel(new FlowLayout(3));
            (this.rbAll = createRadioButton("radiobutton.rangeall", this)).setSelected(true);
            buttonGroup.add(this.rbAll);
            panel.add(this.rbAll);
            addToGB(panel, this, layout, gridBagConstraints);
            final JPanel panel2 = new JPanel(new FlowLayout(3));
            buttonGroup.add(this.rbPages = createRadioButton("radiobutton.rangepages", this));
            panel2.add(this.rbPages);
            final DecimalFormat decimalFormat = new DecimalFormat("####0");
            decimalFormat.setMinimumFractionDigits(0);
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setMinimumIntegerDigits(0);
            decimalFormat.setMaximumIntegerDigits(5);
            decimalFormat.setParseIntegerOnly(true);
            decimalFormat.setDecimalSeparatorAlwaysShown(false);
            final NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);
            numberFormatter.setMinimum(new Integer(1));
            numberFormatter.setMaximum(new Integer(Integer.MAX_VALUE));
            numberFormatter.setAllowsInvalid(true);
            numberFormatter.setCommitsOnValidEdit(true);
            (this.tfRangeFrom = new JFormattedTextField(numberFormatter)).setColumns(4);
            this.tfRangeFrom.setEnabled(false);
            this.tfRangeFrom.addActionListener(this);
            this.tfRangeFrom.addFocusListener(this);
            this.tfRangeFrom.setFocusLostBehavior(3);
            this.tfRangeFrom.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("radiobutton.rangepages"));
            panel2.add(this.tfRangeFrom);
            (this.lblRangeTo = new JLabel(ServiceDialog.getMsg("label.rangeto"))).setEnabled(false);
            panel2.add(this.lblRangeTo);
            NumberFormatter numberFormatter2;
            try {
                numberFormatter2 = (NumberFormatter)numberFormatter.clone();
            }
            catch (final CloneNotSupportedException ex) {
                numberFormatter2 = new NumberFormatter();
            }
            (this.tfRangeTo = new JFormattedTextField(numberFormatter2)).setColumns(4);
            this.tfRangeTo.setEnabled(false);
            this.tfRangeTo.addFocusListener(this);
            this.tfRangeTo.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rangeto"));
            panel2.add(this.tfRangeTo);
            addToGB(panel2, this, layout, gridBagConstraints);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final Object source = actionEvent.getSource();
            SunPageSelection sunPageSelection = SunPageSelection.ALL;
            this.setupRangeWidgets();
            if (source == this.rbAll) {
                ServiceDialog.this.asCurrent.add(this.prAll);
            }
            else if (source == this.rbSelect) {
                sunPageSelection = SunPageSelection.SELECTION;
            }
            else if (source == this.rbPages || source == this.tfRangeFrom || source == this.tfRangeTo) {
                this.updateRangeAttribute();
                sunPageSelection = SunPageSelection.RANGE;
            }
            if (ServiceDialog.this.isAWT) {
                ServiceDialog.this.asCurrent.add(sunPageSelection);
            }
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            final Object source = focusEvent.getSource();
            if (source == this.tfRangeFrom || source == this.tfRangeTo) {
                this.updateRangeAttribute();
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
        }
        
        private void setupRangeWidgets() {
            final boolean enabled = this.rbPages.isSelected() && this.prSupported;
            this.tfRangeFrom.setEnabled(enabled);
            this.tfRangeTo.setEnabled(enabled);
            this.lblRangeTo.setEnabled(enabled);
        }
        
        private void updateRangeAttribute() {
            final String text = this.tfRangeFrom.getText();
            final String text2 = this.tfRangeTo.getText();
            int int1;
            try {
                int1 = Integer.parseInt(text);
            }
            catch (final NumberFormatException ex) {
                int1 = 1;
            }
            int int2;
            try {
                int2 = Integer.parseInt(text2);
            }
            catch (final NumberFormatException ex2) {
                int2 = int1;
            }
            if (int1 < 1) {
                int1 = 1;
                this.tfRangeFrom.setValue(new Integer(1));
            }
            if (int2 < int1) {
                int2 = int1;
                this.tfRangeTo.setValue(new Integer(int1));
            }
            ServiceDialog.this.asCurrent.add(new PageRanges(int1, int2));
        }
        
        public void updateInfo() {
            final Class<PageRanges> clazz = PageRanges.class;
            this.prSupported = false;
            if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz) || ServiceDialog.this.isAWT) {
                this.prSupported = true;
            }
            SunPageSelection sunPageSelection = SunPageSelection.ALL;
            int n = 1;
            int n2 = 1;
            final PageRanges pageRanges = (PageRanges)ServiceDialog.this.asCurrent.get(clazz);
            if (pageRanges != null && !pageRanges.equals(this.prAll)) {
                sunPageSelection = SunPageSelection.RANGE;
                final int[][] members = pageRanges.getMembers();
                if (members.length > 0 && members[0].length > 1) {
                    n = members[0][0];
                    n2 = members[0][1];
                }
            }
            if (ServiceDialog.this.isAWT) {
                sunPageSelection = (SunPageSelection)ServiceDialog.this.asCurrent.get(SunPageSelection.class);
            }
            if (sunPageSelection == SunPageSelection.ALL) {
                this.rbAll.setSelected(true);
            }
            else if (sunPageSelection != SunPageSelection.SELECTION) {
                this.rbPages.setSelected(true);
            }
            this.tfRangeFrom.setValue(new Integer(n));
            this.tfRangeTo.setValue(new Integer(n2));
            this.rbAll.setEnabled(this.prSupported);
            this.rbPages.setEnabled(this.prSupported);
            this.setupRangeWidgets();
        }
    }
    
    private class CopiesPanel extends JPanel implements ActionListener, ChangeListener
    {
        private final String strTitle;
        private SpinnerNumberModel snModel;
        private JSpinner spinCopies;
        private JLabel lblCopies;
        private JCheckBox cbCollate;
        private boolean scSupported;
        
        public CopiesPanel() {
            this.strTitle = ServiceDialog.getMsg("border.copies");
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets = ServiceDialog.compInsets;
            (this.lblCopies = new JLabel(ServiceDialog.getMsg("label.numcopies"), 11)).setDisplayedMnemonic(getMnemonic("label.numcopies"));
            this.lblCopies.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.numcopies"));
            addToGB(this.lblCopies, this, layout, gridBagConstraints);
            this.snModel = new SpinnerNumberModel(1, 1, 999, 1);
            this.spinCopies = new JSpinner(this.snModel);
            this.lblCopies.setLabelFor(this.spinCopies);
            ((JSpinner.NumberEditor)this.spinCopies.getEditor()).getTextField().setColumns(3);
            this.spinCopies.addChangeListener(this);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.spinCopies, this, layout, gridBagConstraints);
            (this.cbCollate = createCheckBox("checkbox.collate", this)).setEnabled(false);
            addToGB(this.cbCollate, this, layout, gridBagConstraints);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.cbCollate.isSelected()) {
                ServiceDialog.this.asCurrent.add(SheetCollate.COLLATED);
            }
            else {
                ServiceDialog.this.asCurrent.add(SheetCollate.UNCOLLATED);
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            this.updateCollateCB();
            ServiceDialog.this.asCurrent.add(new Copies(this.snModel.getNumber().intValue()));
        }
        
        private void updateCollateCB() {
            final int intValue = this.snModel.getNumber().intValue();
            if (ServiceDialog.this.isAWT) {
                this.cbCollate.setEnabled(true);
            }
            else {
                this.cbCollate.setEnabled(intValue > 1 && this.scSupported);
            }
        }
        
        public void updateInfo() {
            final Class<Copies> clazz = Copies.class;
            final Class<SheetCollate> clazz2 = SheetCollate.class;
            boolean b = false;
            this.scSupported = false;
            if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
                b = true;
            }
            Object o = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, null, null);
            if (o == null) {
                o = new CopiesSupported(1, 999);
            }
            Object o2 = ServiceDialog.this.asCurrent.get(clazz);
            if (o2 == null) {
                o2 = ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
                if (o2 == null) {
                    o2 = new Copies(1);
                }
            }
            this.spinCopies.setEnabled(b);
            this.lblCopies.setEnabled(b);
            final int[][] members = ((SetOfIntegerSyntax)o).getMembers();
            int n;
            int n2;
            if (members.length > 0 && members[0].length > 0) {
                n = members[0][0];
                n2 = members[0][1];
            }
            else {
                n = 1;
                n2 = Integer.MAX_VALUE;
            }
            this.snModel.setMinimum(new Integer(n));
            this.snModel.setMaximum(new Integer(n2));
            int value = ((IntegerSyntax)o2).getValue();
            if (value < n || value > n2) {
                value = n;
            }
            this.snModel.setValue(new Integer(value));
            if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz2)) {
                this.scSupported = true;
            }
            SheetCollate uncollated = (SheetCollate)ServiceDialog.this.asCurrent.get(clazz2);
            if (uncollated == null) {
                uncollated = (SheetCollate)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz2);
                if (uncollated == null) {
                    uncollated = SheetCollate.UNCOLLATED;
                }
            }
            this.cbCollate.setSelected(uncollated == SheetCollate.COLLATED);
            this.updateCollateCB();
        }
    }
    
    private class PageSetupPanel extends JPanel
    {
        private MediaPanel pnlMedia;
        private OrientationPanel pnlOrientation;
        private MarginsPanel pnlMargins;
        
        public PageSetupPanel() {
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            gridBagConstraints.fill = 1;
            gridBagConstraints.insets = ServiceDialog.panelInsets;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.gridwidth = 0;
            addToGB(this.pnlMedia = new MediaPanel(), this, layout, gridBagConstraints);
            this.pnlOrientation = new OrientationPanel();
            gridBagConstraints.gridwidth = -1;
            addToGB(this.pnlOrientation, this, layout, gridBagConstraints);
            this.pnlMargins = new MarginsPanel();
            this.pnlOrientation.addOrientationListener(this.pnlMargins);
            this.pnlMedia.addMediaListener(this.pnlMargins);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.pnlMargins, this, layout, gridBagConstraints);
        }
        
        public void updateInfo() {
            this.pnlMedia.updateInfo();
            this.pnlOrientation.updateInfo();
            this.pnlMargins.updateInfo();
        }
    }
    
    private class MarginsPanel extends JPanel implements ActionListener, FocusListener
    {
        private final String strTitle;
        private JFormattedTextField leftMargin;
        private JFormattedTextField rightMargin;
        private JFormattedTextField topMargin;
        private JFormattedTextField bottomMargin;
        private JLabel lblLeft;
        private JLabel lblRight;
        private JLabel lblTop;
        private JLabel lblBottom;
        private int units;
        private float lmVal;
        private float rmVal;
        private float tmVal;
        private float bmVal;
        private Float lmObj;
        private Float rmObj;
        private Float tmObj;
        private Float bmObj;
        
        public MarginsPanel() {
            this.strTitle = ServiceDialog.getMsg("border.margins");
            this.units = 1000;
            this.lmVal = -1.0f;
            this.rmVal = -1.0f;
            this.tmVal = -1.0f;
            this.bmVal = -1.0f;
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = 2;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 0.0;
            gridBagConstraints.insets = ServiceDialog.compInsets;
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            String s = "label.millimetres";
            final String country = Locale.getDefault().getCountry();
            if (country != null && (country.equals("") || country.equals(Locale.US.getCountry()) || country.equals(Locale.CANADA.getCountry()))) {
                s = "label.inches";
                this.units = 25400;
            }
            final String msg = ServiceDialog.getMsg(s);
            DecimalFormat decimalFormat;
            if (this.units == 1000) {
                decimalFormat = new DecimalFormat("###.##");
                decimalFormat.setMaximumIntegerDigits(3);
            }
            else {
                decimalFormat = new DecimalFormat("##.##");
                decimalFormat.setMaximumIntegerDigits(2);
            }
            decimalFormat.setMinimumFractionDigits(1);
            decimalFormat.setMaximumFractionDigits(2);
            decimalFormat.setMinimumIntegerDigits(1);
            decimalFormat.setParseIntegerOnly(false);
            decimalFormat.setDecimalSeparatorAlwaysShown(true);
            final NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);
            numberFormatter.setMinimum(new Float(0.0f));
            numberFormatter.setMaximum(new Float(999.0f));
            numberFormatter.setAllowsInvalid(true);
            numberFormatter.setCommitsOnValidEdit(true);
            (this.leftMargin = new JFormattedTextField(numberFormatter)).addFocusListener(this);
            this.leftMargin.addActionListener(this);
            this.leftMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.leftmargin"));
            (this.rightMargin = new JFormattedTextField(numberFormatter)).addFocusListener(this);
            this.rightMargin.addActionListener(this);
            this.rightMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rightmargin"));
            (this.topMargin = new JFormattedTextField(numberFormatter)).addFocusListener(this);
            this.topMargin.addActionListener(this);
            this.topMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.topmargin"));
            this.topMargin = new JFormattedTextField(numberFormatter);
            (this.bottomMargin = new JFormattedTextField(numberFormatter)).addFocusListener(this);
            this.bottomMargin.addActionListener(this);
            this.bottomMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.bottommargin"));
            this.topMargin = new JFormattedTextField(numberFormatter);
            gridBagConstraints.gridwidth = -1;
            (this.lblLeft = new JLabel(ServiceDialog.getMsg("label.leftmargin") + " " + msg, 10)).setDisplayedMnemonic(getMnemonic("label.leftmargin"));
            this.lblLeft.setLabelFor(this.leftMargin);
            addToGB(this.lblLeft, this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            (this.lblRight = new JLabel(ServiceDialog.getMsg("label.rightmargin") + " " + msg, 10)).setDisplayedMnemonic(getMnemonic("label.rightmargin"));
            this.lblRight.setLabelFor(this.rightMargin);
            addToGB(this.lblRight, this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = -1;
            addToGB(this.leftMargin, this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.rightMargin, this, layout, gridBagConstraints);
            addToGB(new JPanel(), this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = -1;
            (this.lblTop = new JLabel(ServiceDialog.getMsg("label.topmargin") + " " + msg, 10)).setDisplayedMnemonic(getMnemonic("label.topmargin"));
            this.lblTop.setLabelFor(this.topMargin);
            addToGB(this.lblTop, this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            (this.lblBottom = new JLabel(ServiceDialog.getMsg("label.bottommargin") + " " + msg, 10)).setDisplayedMnemonic(getMnemonic("label.bottommargin"));
            this.lblBottom.setLabelFor(this.bottomMargin);
            addToGB(this.lblBottom, this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = -1;
            addToGB(this.topMargin, this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.bottomMargin, this, layout, gridBagConstraints);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            this.updateMargins(actionEvent.getSource());
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            this.updateMargins(focusEvent.getSource());
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
        }
        
        public void updateMargins(final Object o) {
            if (!(o instanceof JFormattedTextField)) {
                return;
            }
            final JFormattedTextField formattedTextField = (JFormattedTextField)o;
            final Float n = (Float)formattedTextField.getValue();
            if (n == null) {
                return;
            }
            if (formattedTextField == this.leftMargin && n.equals(this.lmObj)) {
                return;
            }
            if (formattedTextField == this.rightMargin && n.equals(this.rmObj)) {
                return;
            }
            if (formattedTextField == this.topMargin && n.equals(this.tmObj)) {
                return;
            }
            if (formattedTextField == this.bottomMargin && n.equals(this.bmObj)) {
                return;
            }
            final Float lmObj = (Float)this.leftMargin.getValue();
            final Float rmObj = (Float)this.rightMargin.getValue();
            final Float tmObj = (Float)this.topMargin.getValue();
            final Float bmObj = (Float)this.bottomMargin.getValue();
            float floatValue = lmObj;
            float floatValue2 = rmObj;
            float floatValue3 = tmObj;
            float floatValue4 = bmObj;
            final Class<OrientationRequested> clazz = OrientationRequested.class;
            OrientationRequested orientationRequested = (OrientationRequested)ServiceDialog.this.asCurrent.get(clazz);
            if (orientationRequested == null) {
                orientationRequested = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
            }
            if (orientationRequested == OrientationRequested.REVERSE_PORTRAIT) {
                final float n2 = floatValue;
                floatValue = floatValue2;
                floatValue2 = n2;
                final float n3 = floatValue3;
                floatValue3 = floatValue4;
                floatValue4 = n3;
            }
            else if (orientationRequested == OrientationRequested.LANDSCAPE) {
                final float n4 = floatValue;
                floatValue = floatValue3;
                floatValue3 = floatValue2;
                floatValue2 = floatValue4;
                floatValue4 = n4;
            }
            else if (orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
                final float n5 = floatValue;
                floatValue = floatValue4;
                floatValue4 = floatValue2;
                floatValue2 = floatValue3;
                floatValue3 = n5;
            }
            final MediaPrintableArea validateMargins;
            if ((validateMargins = this.validateMargins(floatValue, floatValue2, floatValue3, floatValue4)) != null) {
                ServiceDialog.this.asCurrent.add(validateMargins);
                this.lmVal = floatValue;
                this.rmVal = floatValue2;
                this.tmVal = floatValue3;
                this.bmVal = floatValue4;
                this.lmObj = lmObj;
                this.rmObj = rmObj;
                this.tmObj = tmObj;
                this.bmObj = bmObj;
            }
            else {
                if (this.lmObj == null || this.rmObj == null || this.tmObj == null || this.rmObj == null) {
                    return;
                }
                this.leftMargin.setValue(this.lmObj);
                this.rightMargin.setValue(this.rmObj);
                this.topMargin.setValue(this.tmObj);
                this.bottomMargin.setValue(this.bmObj);
            }
        }
        
        private MediaPrintableArea validateMargins(final float n, final float n2, final float n3, final float n4) {
            final Class<MediaPrintableArea> clazz = MediaPrintableArea.class;
            MediaPrintableArea mediaPrintableArea = null;
            Size2DSyntax mediaSizeForName = null;
            Media media = (Media)ServiceDialog.this.asCurrent.get(Media.class);
            if (media == null || !(media instanceof MediaSizeName)) {
                media = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
            }
            if (media != null && media instanceof MediaSizeName) {
                mediaSizeForName = MediaSize.getMediaSizeForName((MediaSizeName)media);
            }
            if (mediaSizeForName == null) {
                mediaSizeForName = new MediaSize(8.5f, 11.0f, 25400);
            }
            if (media != null) {
                final HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
                set.add(media);
                final Object supportedAttributeValues = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, set);
                if (supportedAttributeValues instanceof MediaPrintableArea[] && ((MediaPrintableArea[])supportedAttributeValues).length > 0) {
                    mediaPrintableArea = ((MediaPrintableArea[])supportedAttributeValues)[0];
                }
            }
            if (mediaPrintableArea == null) {
                mediaPrintableArea = new MediaPrintableArea(0.0f, 0.0f, mediaSizeForName.getX(this.units), mediaSizeForName.getY(this.units), this.units);
            }
            final float x = mediaSizeForName.getX(this.units);
            final float y = mediaSizeForName.getY(this.units);
            final float n5 = x - n - n2;
            final float n6 = y - n3 - n4;
            if (n5 <= 0.0f || n6 <= 0.0f || n < 0.0f || n3 < 0.0f || n < mediaPrintableArea.getX(this.units) || n5 > mediaPrintableArea.getWidth(this.units) || n3 < mediaPrintableArea.getY(this.units) || n6 > mediaPrintableArea.getHeight(this.units)) {
                return null;
            }
            return new MediaPrintableArea(n, n3, n5, n6, this.units);
        }
        
        public void updateInfo() {
            if (ServiceDialog.this.isAWT) {
                this.leftMargin.setEnabled(false);
                this.rightMargin.setEnabled(false);
                this.topMargin.setEnabled(false);
                this.bottomMargin.setEnabled(false);
                this.lblLeft.setEnabled(false);
                this.lblRight.setEnabled(false);
                this.lblTop.setEnabled(false);
                this.lblBottom.setEnabled(false);
                return;
            }
            final Class<MediaPrintableArea> clazz = MediaPrintableArea.class;
            Attribute attribute = ServiceDialog.this.asCurrent.get(clazz);
            MediaPrintableArea mediaPrintableArea = null;
            Size2DSyntax mediaSizeForName = null;
            Media media = (Media)ServiceDialog.this.asCurrent.get(Media.class);
            if (media == null || !(media instanceof MediaSizeName)) {
                media = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
            }
            if (media != null && media instanceof MediaSizeName) {
                mediaSizeForName = MediaSize.getMediaSizeForName((MediaSizeName)media);
            }
            if (mediaSizeForName == null) {
                mediaSizeForName = new MediaSize(8.5f, 11.0f, 25400);
            }
            if (media != null) {
                final HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
                set.add(media);
                final Object supportedAttributeValues = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, set);
                if (supportedAttributeValues instanceof MediaPrintableArea[] && ((MediaPrintableArea[])supportedAttributeValues).length > 0) {
                    mediaPrintableArea = ((MediaPrintableArea[])supportedAttributeValues)[0];
                }
                else if (supportedAttributeValues instanceof MediaPrintableArea) {
                    mediaPrintableArea = (MediaPrintableArea)supportedAttributeValues;
                }
            }
            if (mediaPrintableArea == null) {
                mediaPrintableArea = new MediaPrintableArea(0.0f, 0.0f, mediaSizeForName.getX(this.units), mediaSizeForName.getY(this.units), this.units);
            }
            final float x = mediaSizeForName.getX(25400);
            final float y = mediaSizeForName.getY(25400);
            final float n = 5.0f;
            float n2;
            if (x > n) {
                n2 = 1.0f;
            }
            else {
                n2 = x / n;
            }
            float n3;
            if (y > n) {
                n3 = 1.0f;
            }
            else {
                n3 = y / n;
            }
            if (attribute == null) {
                attribute = new MediaPrintableArea(n2, n3, x - 2.0f * n2, y - 2.0f * n3, 25400);
                ServiceDialog.this.asCurrent.add(attribute);
            }
            float x2 = ((MediaPrintableArea)attribute).getX(this.units);
            float y2 = ((MediaPrintableArea)attribute).getY(this.units);
            float width = ((MediaPrintableArea)attribute).getWidth(this.units);
            float height = ((MediaPrintableArea)attribute).getHeight(this.units);
            final float x3 = mediaPrintableArea.getX(this.units);
            final float y3 = mediaPrintableArea.getY(this.units);
            final float width2 = mediaPrintableArea.getWidth(this.units);
            final float height2 = mediaPrintableArea.getHeight(this.units);
            boolean b = false;
            final float x4 = mediaSizeForName.getX(this.units);
            final float y4 = mediaSizeForName.getY(this.units);
            if (this.lmVal >= 0.0f) {
                b = true;
                if (this.lmVal + this.rmVal > x4) {
                    if (width > width2) {
                        width = width2;
                    }
                    x2 = (x4 - width) / 2.0f;
                }
                else {
                    x2 = ((this.lmVal >= x3) ? this.lmVal : x3);
                    width = x4 - x2 - this.rmVal;
                }
                if (this.tmVal + this.bmVal > y4) {
                    if (height > height2) {
                        height = height2;
                    }
                    y2 = (y4 - height) / 2.0f;
                }
                else {
                    y2 = ((this.tmVal >= y3) ? this.tmVal : y3);
                    height = y4 - y2 - this.bmVal;
                }
            }
            if (x2 < x3) {
                b = true;
                x2 = x3;
            }
            if (y2 < y3) {
                b = true;
                y2 = y3;
            }
            if (width > width2) {
                b = true;
                width = width2;
            }
            if (height > height2) {
                b = true;
                height = height2;
            }
            if (x2 + width > x3 + width2 || width <= 0.0f) {
                b = true;
                x2 = x3;
                width = width2;
            }
            if (y2 + height > y3 + height2 || height <= 0.0f) {
                b = true;
                y2 = y3;
                height = height2;
            }
            if (b) {
                ServiceDialog.this.asCurrent.add(new MediaPrintableArea(x2, y2, width, height, this.units));
            }
            this.lmVal = x2;
            this.tmVal = y2;
            this.rmVal = mediaSizeForName.getX(this.units) - x2 - width;
            this.bmVal = mediaSizeForName.getY(this.units) - y2 - height;
            this.lmObj = new Float(this.lmVal);
            this.rmObj = new Float(this.rmVal);
            this.tmObj = new Float(this.tmVal);
            this.bmObj = new Float(this.bmVal);
            final Class<OrientationRequested> clazz2 = OrientationRequested.class;
            OrientationRequested orientationRequested = (OrientationRequested)ServiceDialog.this.asCurrent.get(clazz2);
            if (orientationRequested == null) {
                orientationRequested = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz2);
            }
            if (orientationRequested == OrientationRequested.REVERSE_PORTRAIT) {
                final Float lmObj = this.lmObj;
                this.lmObj = this.rmObj;
                this.rmObj = lmObj;
                final Float tmObj = this.tmObj;
                this.tmObj = this.bmObj;
                this.bmObj = tmObj;
            }
            else if (orientationRequested == OrientationRequested.LANDSCAPE) {
                final Float lmObj2 = this.lmObj;
                this.lmObj = this.bmObj;
                this.bmObj = this.rmObj;
                this.rmObj = this.tmObj;
                this.tmObj = lmObj2;
            }
            else if (orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
                final Float lmObj3 = this.lmObj;
                this.lmObj = this.tmObj;
                this.tmObj = this.rmObj;
                this.rmObj = this.bmObj;
                this.bmObj = lmObj3;
            }
            this.leftMargin.setValue(this.lmObj);
            this.rightMargin.setValue(this.rmObj);
            this.topMargin.setValue(this.tmObj);
            this.bottomMargin.setValue(this.bmObj);
        }
    }
    
    private class MediaPanel extends JPanel implements ItemListener
    {
        private final String strTitle;
        private JLabel lblSize;
        private JLabel lblSource;
        private JComboBox cbSize;
        private JComboBox cbSource;
        private Vector sizes;
        private Vector sources;
        private MarginsPanel pnlMargins;
        
        public MediaPanel() {
            this.strTitle = ServiceDialog.getMsg("border.media");
            this.sizes = new Vector();
            this.sources = new Vector();
            this.pnlMargins = null;
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            this.cbSize = new JComboBox();
            this.cbSource = new JComboBox();
            gridBagConstraints.fill = 1;
            gridBagConstraints.insets = ServiceDialog.compInsets;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.weightx = 0.0;
            (this.lblSize = new JLabel(ServiceDialog.getMsg("label.size"), 11)).setDisplayedMnemonic(getMnemonic("label.size"));
            this.lblSize.setLabelFor(this.cbSize);
            addToGB(this.lblSize, this, layout, gridBagConstraints);
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = 0;
            addToGB(this.cbSize, this, layout, gridBagConstraints);
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridwidth = 1;
            (this.lblSource = new JLabel(ServiceDialog.getMsg("label.source"), 11)).setDisplayedMnemonic(getMnemonic("label.source"));
            this.lblSource.setLabelFor(this.cbSource);
            addToGB(this.lblSource, this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.cbSource, this, layout, gridBagConstraints);
        }
        
        private String getMediaName(final String s) {
            try {
                return ServiceDialog.messageRB.getString(s.replace(' ', '-').replace('#', 'n'));
            }
            catch (final MissingResourceException ex) {
                return s;
            }
        }
        
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            final Object source = itemEvent.getSource();
            if (itemEvent.getStateChange() == 1) {
                if (source == this.cbSize) {
                    final int selectedIndex = this.cbSize.getSelectedIndex();
                    if (selectedIndex >= 0 && selectedIndex < this.sizes.size()) {
                        if (this.cbSource.getItemCount() > 1 && this.cbSource.getSelectedIndex() >= 1) {
                            ServiceDialog.this.asCurrent.add(new SunAlternateMedia(this.sources.get(this.cbSource.getSelectedIndex() - 1)));
                        }
                        ServiceDialog.this.asCurrent.add((Attribute)this.sizes.get(selectedIndex));
                    }
                }
                else if (source == this.cbSource) {
                    final int selectedIndex2 = this.cbSource.getSelectedIndex();
                    if (selectedIndex2 >= 1 && selectedIndex2 < this.sources.size() + 1) {
                        ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
                        final MediaTray mediaTray = this.sources.get(selectedIndex2 - 1);
                        final Media media = (Media)ServiceDialog.this.asCurrent.get(Media.class);
                        if (media == null || media instanceof MediaTray) {
                            ServiceDialog.this.asCurrent.add(mediaTray);
                        }
                        else if (media instanceof MediaSizeName) {
                            final MediaSizeName mediaSizeName = (MediaSizeName)media;
                            final Media media2 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
                            if (media2 instanceof MediaSizeName && media2.equals(mediaSizeName)) {
                                ServiceDialog.this.asCurrent.add(mediaTray);
                            }
                            else {
                                ServiceDialog.this.asCurrent.add(new SunAlternateMedia(mediaTray));
                            }
                        }
                    }
                    else if (selectedIndex2 == 0) {
                        ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
                        if (this.cbSize.getItemCount() > 0) {
                            ServiceDialog.this.asCurrent.add(this.sizes.get(this.cbSize.getSelectedIndex()));
                        }
                    }
                }
                if (this.pnlMargins != null) {
                    this.pnlMargins.updateInfo();
                }
            }
        }
        
        public void addMediaListener(final MarginsPanel pnlMargins) {
            this.pnlMargins = pnlMargins;
        }
        
        public void updateInfo() {
            final Class<Media> clazz = Media.class;
            final Class<SunAlternateMedia> clazz2 = SunAlternateMedia.class;
            boolean enabled = false;
            this.cbSize.removeItemListener(this);
            this.cbSize.removeAllItems();
            this.cbSource.removeItemListener(this);
            this.cbSource.removeAllItems();
            this.cbSource.addItem(this.getMediaName("auto-select"));
            this.sizes.clear();
            this.sources.clear();
            if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
                enabled = true;
                final Object supportedAttributeValues = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
                if (supportedAttributeValues instanceof Media[]) {
                    final Media[] array = (Media[])supportedAttributeValues;
                    for (int i = 0; i < array.length; ++i) {
                        final Media media = array[i];
                        if (media instanceof MediaSizeName) {
                            this.sizes.add(media);
                            this.cbSize.addItem(this.getMediaName(media.toString()));
                        }
                        else if (media instanceof MediaTray) {
                            this.sources.add(media);
                            this.cbSource.addItem(this.getMediaName(media.toString()));
                        }
                    }
                }
            }
            final boolean b = enabled && this.sizes.size() > 0;
            this.lblSize.setEnabled(b);
            this.cbSize.setEnabled(b);
            if (ServiceDialog.this.isAWT) {
                this.cbSource.setEnabled(false);
                this.lblSource.setEnabled(false);
            }
            else {
                this.cbSource.setEnabled(enabled);
            }
            if (enabled) {
                Media media2 = (Media)ServiceDialog.this.asCurrent.get(clazz);
                final Media media3 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
                if (media3 instanceof MediaSizeName) {
                    this.cbSize.setSelectedIndex((this.sizes.size() > 0) ? this.sizes.indexOf(media3) : -1);
                }
                if (media2 == null || !ServiceDialog.this.psCurrent.isAttributeValueSupported(media2, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)) {
                    media2 = media3;
                    if (media2 == null && this.sizes.size() > 0) {
                        media2 = this.sizes.get(0);
                    }
                    if (media2 != null) {
                        ServiceDialog.this.asCurrent.add(media2);
                    }
                }
                if (media2 != null) {
                    if (media2 instanceof MediaSizeName) {
                        this.cbSize.setSelectedIndex(this.sizes.indexOf(media2));
                    }
                    else if (media2 instanceof MediaTray) {
                        this.cbSource.setSelectedIndex(this.sources.indexOf(media2) + 1);
                    }
                }
                else {
                    this.cbSize.setSelectedIndex((this.sizes.size() > 0) ? 0 : -1);
                    this.cbSource.setSelectedIndex(0);
                }
                final SunAlternateMedia sunAlternateMedia = (SunAlternateMedia)ServiceDialog.this.asCurrent.get(clazz2);
                if (sunAlternateMedia != null) {
                    final Media media4 = sunAlternateMedia.getMedia();
                    if (media4 instanceof MediaTray) {
                        this.cbSource.setSelectedIndex(this.sources.indexOf(media4) + 1);
                    }
                }
                final int selectedIndex = this.cbSize.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < this.sizes.size()) {
                    ServiceDialog.this.asCurrent.add((Attribute)this.sizes.get(selectedIndex));
                }
                final int selectedIndex2 = this.cbSource.getSelectedIndex();
                if (selectedIndex2 >= 1 && selectedIndex2 < this.sources.size() + 1) {
                    final MediaTray mediaTray = this.sources.get(selectedIndex2 - 1);
                    if (media2 instanceof MediaTray) {
                        ServiceDialog.this.asCurrent.add(mediaTray);
                    }
                    else {
                        ServiceDialog.this.asCurrent.add(new SunAlternateMedia(mediaTray));
                    }
                }
            }
            this.cbSize.addItemListener(this);
            this.cbSource.addItemListener(this);
        }
    }
    
    private class OrientationPanel extends JPanel implements ActionListener
    {
        private final String strTitle;
        private IconRadioButton rbPortrait;
        private IconRadioButton rbLandscape;
        private IconRadioButton rbRevPortrait;
        private IconRadioButton rbRevLandscape;
        private MarginsPanel pnlMargins;
        
        public OrientationPanel() {
            this.strTitle = ServiceDialog.getMsg("border.orientation");
            this.pnlMargins = null;
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            gridBagConstraints.fill = 1;
            gridBagConstraints.insets = ServiceDialog.compInsets;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.gridwidth = 0;
            final ButtonGroup buttonGroup = new ButtonGroup();
            (this.rbPortrait = new IconRadioButton("radiobutton.portrait", "orientPortrait.png", true, buttonGroup, this)).addActionListener(this);
            addToGB(this.rbPortrait, this, layout, gridBagConstraints);
            (this.rbLandscape = new IconRadioButton("radiobutton.landscape", "orientLandscape.png", false, buttonGroup, this)).addActionListener(this);
            addToGB(this.rbLandscape, this, layout, gridBagConstraints);
            (this.rbRevPortrait = new IconRadioButton("radiobutton.revportrait", "orientRevPortrait.png", false, buttonGroup, this)).addActionListener(this);
            addToGB(this.rbRevPortrait, this, layout, gridBagConstraints);
            (this.rbRevLandscape = new IconRadioButton("radiobutton.revlandscape", "orientRevLandscape.png", false, buttonGroup, this)).addActionListener(this);
            addToGB(this.rbRevLandscape, this, layout, gridBagConstraints);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final Object source = actionEvent.getSource();
            if (this.rbPortrait.isSameAs(source)) {
                ServiceDialog.this.asCurrent.add(OrientationRequested.PORTRAIT);
            }
            else if (this.rbLandscape.isSameAs(source)) {
                ServiceDialog.this.asCurrent.add(OrientationRequested.LANDSCAPE);
            }
            else if (this.rbRevPortrait.isSameAs(source)) {
                ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_PORTRAIT);
            }
            else if (this.rbRevLandscape.isSameAs(source)) {
                ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_LANDSCAPE);
            }
            if (this.pnlMargins != null) {
                this.pnlMargins.updateInfo();
            }
        }
        
        void addOrientationListener(final MarginsPanel pnlMargins) {
            this.pnlMargins = pnlMargins;
        }
        
        public void updateInfo() {
            final Class<OrientationRequested> clazz = OrientationRequested.class;
            boolean enabled = false;
            boolean enabled2 = false;
            boolean enabled3 = false;
            boolean enabled4 = false;
            if (ServiceDialog.this.isAWT) {
                enabled = true;
                enabled2 = true;
            }
            else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
                final Object supportedAttributeValues = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
                if (supportedAttributeValues instanceof OrientationRequested[]) {
                    final OrientationRequested[] array = (OrientationRequested[])supportedAttributeValues;
                    for (int i = 0; i < array.length; ++i) {
                        final OrientationRequested orientationRequested = array[i];
                        if (orientationRequested == OrientationRequested.PORTRAIT) {
                            enabled = true;
                        }
                        else if (orientationRequested == OrientationRequested.LANDSCAPE) {
                            enabled2 = true;
                        }
                        else if (orientationRequested == OrientationRequested.REVERSE_PORTRAIT) {
                            enabled3 = true;
                        }
                        else if (orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
                            enabled4 = true;
                        }
                    }
                }
            }
            this.rbPortrait.setEnabled(enabled);
            this.rbLandscape.setEnabled(enabled2);
            this.rbRevPortrait.setEnabled(enabled3);
            this.rbRevLandscape.setEnabled(enabled4);
            OrientationRequested portrait = (OrientationRequested)ServiceDialog.this.asCurrent.get(clazz);
            if (portrait == null || !ServiceDialog.this.psCurrent.isAttributeValueSupported(portrait, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)) {
                portrait = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
                if (portrait != null && !ServiceDialog.this.psCurrent.isAttributeValueSupported(portrait, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)) {
                    portrait = null;
                    final Object supportedAttributeValues2 = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
                    if (supportedAttributeValues2 instanceof OrientationRequested[]) {
                        final OrientationRequested[] array2 = (OrientationRequested[])supportedAttributeValues2;
                        if (array2.length > 1) {
                            portrait = array2[0];
                        }
                    }
                }
                if (portrait == null) {
                    portrait = OrientationRequested.PORTRAIT;
                }
                ServiceDialog.this.asCurrent.add(portrait);
            }
            if (portrait == OrientationRequested.PORTRAIT) {
                this.rbPortrait.setSelected(true);
            }
            else if (portrait == OrientationRequested.LANDSCAPE) {
                this.rbLandscape.setSelected(true);
            }
            else if (portrait == OrientationRequested.REVERSE_PORTRAIT) {
                this.rbRevPortrait.setSelected(true);
            }
            else {
                this.rbRevLandscape.setSelected(true);
            }
        }
    }
    
    private class AppearancePanel extends JPanel
    {
        private ChromaticityPanel pnlChromaticity;
        private QualityPanel pnlQuality;
        private JobAttributesPanel pnlJobAttributes;
        private SidesPanel pnlSides;
        
        public AppearancePanel() {
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            gridBagConstraints.fill = 1;
            gridBagConstraints.insets = ServiceDialog.panelInsets;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.gridwidth = -1;
            addToGB(this.pnlChromaticity = new ChromaticityPanel(), this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.pnlQuality = new QualityPanel(), this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 1;
            addToGB(this.pnlSides = new SidesPanel(), this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.pnlJobAttributes = new JobAttributesPanel(), this, layout, gridBagConstraints);
        }
        
        public void updateInfo() {
            this.pnlChromaticity.updateInfo();
            this.pnlQuality.updateInfo();
            this.pnlSides.updateInfo();
            this.pnlJobAttributes.updateInfo();
        }
    }
    
    private class ChromaticityPanel extends JPanel implements ActionListener
    {
        private final String strTitle;
        private JRadioButton rbMonochrome;
        private JRadioButton rbColor;
        
        public ChromaticityPanel() {
            this.strTitle = ServiceDialog.getMsg("border.chromaticity");
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            gridBagConstraints.fill = 1;
            gridBagConstraints.gridwidth = 0;
            gridBagConstraints.weighty = 1.0;
            final ButtonGroup buttonGroup = new ButtonGroup();
            (this.rbMonochrome = createRadioButton("radiobutton.monochrome", this)).setSelected(true);
            buttonGroup.add(this.rbMonochrome);
            addToGB(this.rbMonochrome, this, layout, gridBagConstraints);
            buttonGroup.add(this.rbColor = createRadioButton("radiobutton.color", this));
            addToGB(this.rbColor, this, layout, gridBagConstraints);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final Object source = actionEvent.getSource();
            if (source == this.rbMonochrome) {
                ServiceDialog.this.asCurrent.add(Chromaticity.MONOCHROME);
            }
            else if (source == this.rbColor) {
                ServiceDialog.this.asCurrent.add(Chromaticity.COLOR);
            }
        }
        
        public void updateInfo() {
            final Class<Chromaticity> clazz = Chromaticity.class;
            boolean enabled = false;
            boolean enabled2 = false;
            if (ServiceDialog.this.isAWT) {
                enabled = true;
                enabled2 = true;
            }
            else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
                final Object supportedAttributeValues = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
                if (supportedAttributeValues instanceof Chromaticity[]) {
                    final Chromaticity[] array = (Chromaticity[])supportedAttributeValues;
                    for (int i = 0; i < array.length; ++i) {
                        final Chromaticity chromaticity = array[i];
                        if (chromaticity == Chromaticity.MONOCHROME) {
                            enabled = true;
                        }
                        else if (chromaticity == Chromaticity.COLOR) {
                            enabled2 = true;
                        }
                    }
                }
            }
            this.rbMonochrome.setEnabled(enabled);
            this.rbColor.setEnabled(enabled2);
            Chromaticity monochrome = (Chromaticity)ServiceDialog.this.asCurrent.get(clazz);
            if (monochrome == null) {
                monochrome = (Chromaticity)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
                if (monochrome == null) {
                    monochrome = Chromaticity.MONOCHROME;
                }
            }
            if (monochrome == Chromaticity.MONOCHROME) {
                this.rbMonochrome.setSelected(true);
            }
            else {
                this.rbColor.setSelected(true);
            }
        }
    }
    
    private class QualityPanel extends JPanel implements ActionListener
    {
        private final String strTitle;
        private JRadioButton rbDraft;
        private JRadioButton rbNormal;
        private JRadioButton rbHigh;
        
        public QualityPanel() {
            this.strTitle = ServiceDialog.getMsg("border.quality");
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            gridBagConstraints.fill = 1;
            gridBagConstraints.gridwidth = 0;
            gridBagConstraints.weighty = 1.0;
            final ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(this.rbDraft = createRadioButton("radiobutton.draftq", this));
            addToGB(this.rbDraft, this, layout, gridBagConstraints);
            (this.rbNormal = createRadioButton("radiobutton.normalq", this)).setSelected(true);
            buttonGroup.add(this.rbNormal);
            addToGB(this.rbNormal, this, layout, gridBagConstraints);
            buttonGroup.add(this.rbHigh = createRadioButton("radiobutton.highq", this));
            addToGB(this.rbHigh, this, layout, gridBagConstraints);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final Object source = actionEvent.getSource();
            if (source == this.rbDraft) {
                ServiceDialog.this.asCurrent.add(PrintQuality.DRAFT);
            }
            else if (source == this.rbNormal) {
                ServiceDialog.this.asCurrent.add(PrintQuality.NORMAL);
            }
            else if (source == this.rbHigh) {
                ServiceDialog.this.asCurrent.add(PrintQuality.HIGH);
            }
        }
        
        public void updateInfo() {
            final Class<PrintQuality> clazz = PrintQuality.class;
            boolean enabled = false;
            boolean enabled2 = false;
            boolean enabled3 = false;
            if (ServiceDialog.this.isAWT) {
                enabled = true;
                enabled2 = true;
                enabled3 = true;
            }
            else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
                final Object supportedAttributeValues = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
                if (supportedAttributeValues instanceof PrintQuality[]) {
                    final PrintQuality[] array = (PrintQuality[])supportedAttributeValues;
                    for (int i = 0; i < array.length; ++i) {
                        final PrintQuality printQuality = array[i];
                        if (printQuality == PrintQuality.DRAFT) {
                            enabled = true;
                        }
                        else if (printQuality == PrintQuality.NORMAL) {
                            enabled2 = true;
                        }
                        else if (printQuality == PrintQuality.HIGH) {
                            enabled3 = true;
                        }
                    }
                }
            }
            this.rbDraft.setEnabled(enabled);
            this.rbNormal.setEnabled(enabled2);
            this.rbHigh.setEnabled(enabled3);
            PrintQuality normal = (PrintQuality)ServiceDialog.this.asCurrent.get(clazz);
            if (normal == null) {
                normal = (PrintQuality)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
                if (normal == null) {
                    normal = PrintQuality.NORMAL;
                }
            }
            if (normal == PrintQuality.DRAFT) {
                this.rbDraft.setSelected(true);
            }
            else if (normal == PrintQuality.NORMAL) {
                this.rbNormal.setSelected(true);
            }
            else {
                this.rbHigh.setSelected(true);
            }
        }
    }
    
    private class SidesPanel extends JPanel implements ActionListener
    {
        private final String strTitle;
        private IconRadioButton rbOneSide;
        private IconRadioButton rbTumble;
        private IconRadioButton rbDuplex;
        
        public SidesPanel() {
            this.strTitle = ServiceDialog.getMsg("border.sides");
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            gridBagConstraints.fill = 1;
            gridBagConstraints.insets = ServiceDialog.compInsets;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.gridwidth = 0;
            final ButtonGroup buttonGroup = new ButtonGroup();
            (this.rbOneSide = new IconRadioButton("radiobutton.oneside", "oneside.png", true, buttonGroup, this)).addActionListener(this);
            addToGB(this.rbOneSide, this, layout, gridBagConstraints);
            (this.rbTumble = new IconRadioButton("radiobutton.tumble", "tumble.png", false, buttonGroup, this)).addActionListener(this);
            addToGB(this.rbTumble, this, layout, gridBagConstraints);
            (this.rbDuplex = new IconRadioButton("radiobutton.duplex", "duplex.png", false, buttonGroup, this)).addActionListener(this);
            gridBagConstraints.gridwidth = 0;
            addToGB(this.rbDuplex, this, layout, gridBagConstraints);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final Object source = actionEvent.getSource();
            if (this.rbOneSide.isSameAs(source)) {
                ServiceDialog.this.asCurrent.add(Sides.ONE_SIDED);
            }
            else if (this.rbTumble.isSameAs(source)) {
                ServiceDialog.this.asCurrent.add(Sides.TUMBLE);
            }
            else if (this.rbDuplex.isSameAs(source)) {
                ServiceDialog.this.asCurrent.add(Sides.DUPLEX);
            }
        }
        
        public void updateInfo() {
            final Class<Sides> clazz = Sides.class;
            boolean enabled = false;
            boolean enabled2 = false;
            boolean enabled3 = false;
            if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
                final Object supportedAttributeValues = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
                if (supportedAttributeValues instanceof Sides[]) {
                    final Sides[] array = (Sides[])supportedAttributeValues;
                    for (int i = 0; i < array.length; ++i) {
                        final Sides sides = array[i];
                        if (sides == Sides.ONE_SIDED) {
                            enabled = true;
                        }
                        else if (sides == Sides.TUMBLE) {
                            enabled2 = true;
                        }
                        else if (sides == Sides.DUPLEX) {
                            enabled3 = true;
                        }
                    }
                }
            }
            this.rbOneSide.setEnabled(enabled);
            this.rbTumble.setEnabled(enabled2);
            this.rbDuplex.setEnabled(enabled3);
            Sides one_SIDED = (Sides)ServiceDialog.this.asCurrent.get(clazz);
            if (one_SIDED == null) {
                one_SIDED = (Sides)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
                if (one_SIDED == null) {
                    one_SIDED = Sides.ONE_SIDED;
                }
            }
            if (one_SIDED == Sides.ONE_SIDED) {
                this.rbOneSide.setSelected(true);
            }
            else if (one_SIDED == Sides.TUMBLE) {
                this.rbTumble.setSelected(true);
            }
            else {
                this.rbDuplex.setSelected(true);
            }
        }
    }
    
    private class JobAttributesPanel extends JPanel implements ActionListener, ChangeListener, FocusListener
    {
        private final String strTitle;
        private JLabel lblPriority;
        private JLabel lblJobName;
        private JLabel lblUserName;
        private JSpinner spinPriority;
        private SpinnerNumberModel snModel;
        private JCheckBox cbJobSheets;
        private JTextField tfJobName;
        private JTextField tfUserName;
        
        public JobAttributesPanel() {
            this.strTitle = ServiceDialog.getMsg("border.jobattributes");
            final GridBagLayout layout = new GridBagLayout();
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            this.setLayout(layout);
            this.setBorder(BorderFactory.createTitledBorder(this.strTitle));
            gridBagConstraints.fill = 0;
            gridBagConstraints.insets = ServiceDialog.compInsets;
            gridBagConstraints.weighty = 1.0;
            this.cbJobSheets = createCheckBox("checkbox.jobsheets", this);
            gridBagConstraints.anchor = 21;
            addToGB(this.cbJobSheets, this, layout, gridBagConstraints);
            final JPanel panel = new JPanel();
            (this.lblPriority = new JLabel(ServiceDialog.getMsg("label.priority"), 11)).setDisplayedMnemonic(getMnemonic("label.priority"));
            panel.add(this.lblPriority);
            this.snModel = new SpinnerNumberModel(1, 1, 100, 1);
            this.spinPriority = new JSpinner(this.snModel);
            this.lblPriority.setLabelFor(this.spinPriority);
            ((JSpinner.NumberEditor)this.spinPriority.getEditor()).getTextField().setColumns(3);
            this.spinPriority.addChangeListener(this);
            panel.add(this.spinPriority);
            gridBagConstraints.anchor = 22;
            gridBagConstraints.gridwidth = 0;
            panel.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.priority"));
            addToGB(panel, this, layout, gridBagConstraints);
            gridBagConstraints.fill = 2;
            gridBagConstraints.anchor = 10;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridwidth = 1;
            final char access$800 = getMnemonic("label.jobname");
            (this.lblJobName = new JLabel(ServiceDialog.getMsg("label.jobname"), 11)).setDisplayedMnemonic(access$800);
            addToGB(this.lblJobName, this, layout, gridBagConstraints);
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = 0;
            this.tfJobName = new JTextField();
            this.lblJobName.setLabelFor(this.tfJobName);
            this.tfJobName.addFocusListener(this);
            this.tfJobName.setFocusAccelerator(access$800);
            this.tfJobName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.jobname"));
            addToGB(this.tfJobName, this, layout, gridBagConstraints);
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridwidth = 1;
            final char access$801 = getMnemonic("label.username");
            (this.lblUserName = new JLabel(ServiceDialog.getMsg("label.username"), 11)).setDisplayedMnemonic(access$801);
            addToGB(this.lblUserName, this, layout, gridBagConstraints);
            gridBagConstraints.gridwidth = 0;
            this.tfUserName = new JTextField();
            this.lblUserName.setLabelFor(this.tfUserName);
            this.tfUserName.addFocusListener(this);
            this.tfUserName.setFocusAccelerator(access$801);
            this.tfUserName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.username"));
            addToGB(this.tfUserName, this, layout, gridBagConstraints);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.cbJobSheets.isSelected()) {
                ServiceDialog.this.asCurrent.add(JobSheets.STANDARD);
            }
            else {
                ServiceDialog.this.asCurrent.add(JobSheets.NONE);
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            ServiceDialog.this.asCurrent.add(new JobPriority(this.snModel.getNumber().intValue()));
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            final Object source = focusEvent.getSource();
            if (source == this.tfJobName) {
                ServiceDialog.this.asCurrent.add(new JobName(this.tfJobName.getText(), Locale.getDefault()));
            }
            else if (source == this.tfUserName) {
                ServiceDialog.this.asCurrent.add(new RequestingUserName(this.tfUserName.getText(), Locale.getDefault()));
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
        }
        
        public void updateInfo() {
            final Class<JobSheets> clazz = JobSheets.class;
            final Class<JobPriority> clazz2 = JobPriority.class;
            final Class<JobName> clazz3 = JobName.class;
            final Class<RequestingUserName> clazz4 = RequestingUserName.class;
            boolean enabled = false;
            boolean b = false;
            boolean b2 = false;
            boolean b3 = false;
            if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
                enabled = true;
            }
            JobSheets none = (JobSheets)ServiceDialog.this.asCurrent.get(clazz);
            if (none == null) {
                none = (JobSheets)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
                if (none == null) {
                    none = JobSheets.NONE;
                }
            }
            this.cbJobSheets.setSelected(none != JobSheets.NONE);
            this.cbJobSheets.setEnabled(enabled);
            if (!ServiceDialog.this.isAWT && ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz2)) {
                b = true;
            }
            Object o = ServiceDialog.this.asCurrent.get(clazz2);
            if (o == null) {
                o = ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz2);
                if (o == null) {
                    o = new JobPriority(1);
                }
            }
            int value = ((IntegerSyntax)o).getValue();
            if (value < 1 || value > 100) {
                value = 1;
            }
            this.snModel.setValue(new Integer(value));
            this.lblPriority.setEnabled(b);
            this.spinPriority.setEnabled(b);
            if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz3)) {
                b2 = true;
            }
            Object o2 = ServiceDialog.this.asCurrent.get(clazz3);
            if (o2 == null) {
                o2 = ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz3);
                if (o2 == null) {
                    o2 = new JobName("", Locale.getDefault());
                }
            }
            this.tfJobName.setText(((TextSyntax)o2).getValue());
            this.tfJobName.setEnabled(b2);
            this.lblJobName.setEnabled(b2);
            if (!ServiceDialog.this.isAWT && ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz4)) {
                b3 = true;
            }
            Object o3 = ServiceDialog.this.asCurrent.get(clazz4);
            if (o3 == null) {
                o3 = ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz4);
                if (o3 == null) {
                    o3 = new RequestingUserName("", Locale.getDefault());
                }
            }
            this.tfUserName.setText(((TextSyntax)o3).getValue());
            this.tfUserName.setEnabled(b3);
            this.lblUserName.setEnabled(b3);
        }
    }
    
    private class IconRadioButton extends JPanel
    {
        private JRadioButton rb;
        private JLabel lbl;
        
        public IconRadioButton(final String s, final String s2, final boolean selected, final ButtonGroup buttonGroup, final ActionListener actionListener) {
            super(new FlowLayout(3));
            this.add(this.lbl = new JLabel(AccessController.doPrivileged((PrivilegedAction<Icon>)new PrivilegedAction() {
                final /* synthetic */ URL val$imgURL = getImageResource(s2);
                
                @Override
                public Object run() {
                    return new ImageIcon(this.val$imgURL);
                }
            })));
            (this.rb = createRadioButton(s, actionListener)).setSelected(selected);
            addToBG(this.rb, this, buttonGroup);
        }
        
        public void addActionListener(final ActionListener actionListener) {
            this.rb.addActionListener(actionListener);
        }
        
        public boolean isSameAs(final Object o) {
            return this.rb == o;
        }
        
        @Override
        public void setEnabled(final boolean b) {
            this.rb.setEnabled(b);
            this.lbl.setEnabled(b);
        }
        
        public boolean isSelected() {
            return this.rb.isSelected();
        }
        
        public void setSelected(final boolean selected) {
            this.rb.setSelected(selected);
        }
    }
    
    private class ValidatingFileChooser extends JFileChooser
    {
        @Override
        public void approveSelection() {
            final File selectedFile = this.getSelectedFile();
            boolean exists;
            try {
                exists = selectedFile.exists();
            }
            catch (final SecurityException ex) {
                exists = false;
            }
            if (exists && JOptionPane.showConfirmDialog(this, ServiceDialog.getMsg("dialog.overwrite"), ServiceDialog.getMsg("dialog.owtitle"), 0) != 0) {
                return;
            }
            try {
                if (selectedFile.createNewFile()) {
                    selectedFile.delete();
                }
            }
            catch (final IOException ex2) {
                JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + selectedFile, ServiceDialog.getMsg("dialog.owtitle"), 2);
                return;
            }
            catch (final SecurityException ex3) {}
            final File parentFile = selectedFile.getParentFile();
            if ((selectedFile.exists() && (!selectedFile.isFile() || !selectedFile.canWrite())) || (parentFile != null && (!parentFile.exists() || (parentFile.exists() && !parentFile.canWrite())))) {
                JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + selectedFile, ServiceDialog.getMsg("dialog.owtitle"), 2);
                return;
            }
            super.approveSelection();
        }
    }
}
