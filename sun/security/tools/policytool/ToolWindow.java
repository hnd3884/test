package sun.security.tools.policytool;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Point;
import java.awt.event.WindowListener;
import java.awt.GridBagConstraints;
import javax.swing.JDialog;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import java.awt.Window;
import java.text.MessageFormat;
import java.io.FileNotFoundException;
import java.awt.event.MouseListener;
import javax.swing.ListModel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import java.io.File;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import javax.swing.JButton;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JComponent;
import java.awt.Container;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Insets;
import javax.swing.KeyStroke;
import javax.swing.JFrame;

class ToolWindow extends JFrame
{
    private static final long serialVersionUID = 5682568601210376777L;
    static final KeyStroke escKey;
    public static final Insets TOP_PADDING;
    public static final Insets BOTTOM_PADDING;
    public static final Insets LITE_BOTTOM_PADDING;
    public static final Insets LR_PADDING;
    public static final Insets TOP_BOTTOM_PADDING;
    public static final Insets L_TOP_BOTTOM_PADDING;
    public static final Insets LR_TOP_BOTTOM_PADDING;
    public static final Insets LR_BOTTOM_PADDING;
    public static final Insets L_BOTTOM_PADDING;
    public static final Insets R_BOTTOM_PADDING;
    public static final Insets R_PADDING;
    public static final String NEW_POLICY_FILE = "New";
    public static final String OPEN_POLICY_FILE = "Open";
    public static final String SAVE_POLICY_FILE = "Save";
    public static final String SAVE_AS_POLICY_FILE = "Save.As";
    public static final String VIEW_WARNINGS = "View.Warning.Log";
    public static final String QUIT = "Exit";
    public static final String ADD_POLICY_ENTRY = "Add.Policy.Entry";
    public static final String EDIT_POLICY_ENTRY = "Edit.Policy.Entry";
    public static final String REMOVE_POLICY_ENTRY = "Remove.Policy.Entry";
    public static final String EDIT_KEYSTORE = "Edit";
    public static final String ADD_PUBKEY_ALIAS = "Add.Public.Key.Alias";
    public static final String REMOVE_PUBKEY_ALIAS = "Remove.Public.Key.Alias";
    public static final int MW_FILENAME_LABEL = 0;
    public static final int MW_FILENAME_TEXTFIELD = 1;
    public static final int MW_PANEL = 2;
    public static final int MW_ADD_BUTTON = 0;
    public static final int MW_EDIT_BUTTON = 1;
    public static final int MW_REMOVE_BUTTON = 2;
    public static final int MW_POLICY_LIST = 3;
    static final int TEXTFIELD_HEIGHT;
    private PolicyTool tool;
    private int shortCutModifier;
    
    ToolWindow(final PolicyTool tool) {
        this.shortCutModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        this.tool = tool;
    }
    
    @Override
    public Component getComponent(final int n) {
        Component component = this.getContentPane().getComponent(n);
        if (component instanceof JScrollPane) {
            component = ((JScrollPane)component).getViewport().getView();
        }
        return component;
    }
    
    private void initWindow() {
        this.setDefaultCloseOperation(0);
        final JMenuBar jMenuBar = new JMenuBar();
        final JMenu menu = new JMenu();
        configureButton(menu, "File");
        final FileMenuListener fileMenuListener = new FileMenuListener(this.tool, this);
        this.addMenuItem(menu, "New", fileMenuListener, "N");
        this.addMenuItem(menu, "Open", fileMenuListener, "O");
        this.addMenuItem(menu, "Save", fileMenuListener, "S");
        this.addMenuItem(menu, "Save.As", fileMenuListener, null);
        this.addMenuItem(menu, "View.Warning.Log", fileMenuListener, null);
        this.addMenuItem(menu, "Exit", fileMenuListener, null);
        jMenuBar.add(menu);
        final JMenu menu2 = new JMenu();
        configureButton(menu2, "KeyStore");
        this.addMenuItem(menu2, "Edit", new MainWindowListener(this.tool, this), null);
        jMenuBar.add(menu2);
        this.setJMenuBar(jMenuBar);
        ((JPanel)this.getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
        this.addNewComponent(this, new JLabel(PolicyTool.getMessage("Policy.File.")), 0, 0, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_TOP_BOTTOM_PADDING);
        final JTextField textField = new JTextField(50);
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, ToolWindow.TEXTFIELD_HEIGHT));
        textField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Policy.File."));
        textField.setEditable(false);
        this.addNewComponent(this, textField, 1, 1, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_TOP_BOTTOM_PADDING);
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JButton button = new JButton();
        configureButton(button, "Add.Policy.Entry");
        button.addActionListener(new MainWindowListener(this.tool, this));
        this.addNewComponent(panel, button, 0, 0, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_PADDING);
        final JButton button2 = new JButton();
        configureButton(button2, "Edit.Policy.Entry");
        button2.addActionListener(new MainWindowListener(this.tool, this));
        this.addNewComponent(panel, button2, 1, 1, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_PADDING);
        final JButton button3 = new JButton();
        configureButton(button3, "Remove.Policy.Entry");
        button3.addActionListener(new MainWindowListener(this.tool, this));
        this.addNewComponent(panel, button3, 2, 2, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_PADDING);
        this.addNewComponent(this, panel, 2, 0, 2, 2, 1, 0.0, 0.0, 1, ToolWindow.BOTTOM_PADDING);
        String text = this.tool.getPolicyFileName();
        if (text == null) {
            text = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("user.home")) + File.separatorChar + ".java.policy";
        }
        try {
            this.tool.openPolicy(text);
            final DefaultListModel<String> defaultListModel = new DefaultListModel<String>();
            final JList list = new JList((ListModel<Object>)defaultListModel);
            list.setVisibleRowCount(15);
            list.setSelectionMode(0);
            list.addMouseListener(new PolicyListListener(this.tool, this));
            final PolicyEntry[] entry = this.tool.getEntry();
            if (entry != null) {
                for (int i = 0; i < entry.length; ++i) {
                    defaultListModel.addElement(entry[i].headerToString());
                }
            }
            ((JTextField)this.getComponent(1)).setText(text);
            this.initPolicyList(list);
        }
        catch (final FileNotFoundException ex) {
            final JList list2 = new JList(new DefaultListModel<Object>());
            list2.setVisibleRowCount(15);
            list2.setSelectionMode(0);
            list2.addMouseListener(new PolicyListListener(this.tool, this));
            this.initPolicyList(list2);
            this.tool.setPolicyFileName(null);
            this.tool.modified = false;
            this.tool.warnings.addElement(ex.toString());
        }
        catch (final Exception ex2) {
            final JList list3 = new JList(new DefaultListModel<Object>());
            list3.setVisibleRowCount(15);
            list3.setSelectionMode(0);
            list3.addMouseListener(new PolicyListListener(this.tool, this));
            this.initPolicyList(list3);
            this.tool.setPolicyFileName(null);
            this.tool.modified = false;
            this.displayErrorDialog(null, new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString.")).format(new Object[] { text, ex2.toString() }));
        }
    }
    
    private void addMenuItem(final JMenu menu, final String s, final ActionListener actionListener, String message) {
        final JMenuItem menuItem = new JMenuItem();
        configureButton(menuItem, s);
        if (PolicyTool.rb.containsKey(s + ".accelerator")) {
            message = PolicyTool.getMessage(s + ".accelerator");
        }
        if (message != null && !message.isEmpty()) {
            KeyStroke accelerator;
            if (message.length() == 1) {
                accelerator = KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar(message.charAt(0)), this.shortCutModifier);
            }
            else {
                accelerator = KeyStroke.getKeyStroke(message);
            }
            menuItem.setAccelerator(accelerator);
        }
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
    }
    
    static void configureButton(final AbstractButton abstractButton, final String actionCommand) {
        abstractButton.setText(PolicyTool.getMessage(actionCommand));
        abstractButton.setActionCommand(actionCommand);
        final int mnemonicInt = PolicyTool.getMnemonicInt(actionCommand);
        if (mnemonicInt > 0) {
            abstractButton.setMnemonic(mnemonicInt);
            abstractButton.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(actionCommand));
        }
    }
    
    static void configureLabelFor(final JLabel label, final JComponent labelFor, final String s) {
        label.setText(PolicyTool.getMessage(s));
        label.setLabelFor(labelFor);
        final int mnemonicInt = PolicyTool.getMnemonicInt(s);
        if (mnemonicInt > 0) {
            label.setDisplayedMnemonic(mnemonicInt);
            label.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(s));
        }
    }
    
    void addNewComponent(Container container, final JComponent component, final int n, final int gridx, final int gridy, final int gridwidth, final int gridheight, final double weightx, final double weighty, final int fill, final Insets insets) {
        if (container instanceof JFrame) {
            container = ((JFrame)container).getContentPane();
        }
        else if (container instanceof JDialog) {
            container = ((JDialog)container).getContentPane();
        }
        container.add(component, n);
        final GridBagLayout gridBagLayout = (GridBagLayout)container.getLayout();
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        gridBagConstraints.gridwidth = gridwidth;
        gridBagConstraints.gridheight = gridheight;
        gridBagConstraints.weightx = weightx;
        gridBagConstraints.weighty = weighty;
        gridBagConstraints.fill = fill;
        if (insets != null) {
            gridBagConstraints.insets = insets;
        }
        gridBagLayout.setConstraints(component, gridBagConstraints);
    }
    
    void addNewComponent(final Container container, final JComponent component, final int n, final int n2, final int n3, final int n4, final int n5, final double n6, final double n7, final int n8) {
        this.addNewComponent(container, component, n, n2, n3, n4, n5, n6, n7, n8, null);
    }
    
    void initPolicyList(final JList list) {
        this.addNewComponent(this, new JScrollPane(list), 3, 0, 3, 2, 1, 1.0, 1.0, 1);
    }
    
    void replacePolicyList(final JList list) {
        ((JList)this.getComponent(3)).setModel(list.getModel());
    }
    
    void displayToolWindow(final String[] array) {
        this.setTitle(PolicyTool.getMessage("Policy.Tool"));
        this.setResizable(true);
        this.addWindowListener(new ToolWindowListener(this.tool, this));
        this.getContentPane().setLayout(new GridBagLayout());
        this.initWindow();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        if (this.tool.newWarning) {
            this.displayStatusDialog(this, PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information."));
        }
    }
    
    void displayErrorDialog(final Window locationRelativeTo, final String s) {
        final ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Error"), this.tool, this, true);
        final Point point = (locationRelativeTo == null) ? this.getLocationOnScreen() : locationRelativeTo.getLocationOnScreen();
        toolDialog.setLayout(new GridBagLayout());
        this.addNewComponent(toolDialog, new JLabel(s), 0, 0, 0, 1, 1, 0.0, 0.0, 1);
        final JButton defaultButton = new JButton(PolicyTool.getMessage("OK"));
        final ErrorOKButtonListener errorOKButtonListener = new ErrorOKButtonListener(toolDialog);
        defaultButton.addActionListener(errorOKButtonListener);
        this.addNewComponent(toolDialog, defaultButton, 1, 0, 1, 1, 1, 0.0, 0.0, 3);
        toolDialog.getRootPane().setDefaultButton(defaultButton);
        toolDialog.getRootPane().registerKeyboardAction(errorOKButtonListener, ToolWindow.escKey, 2);
        toolDialog.pack();
        toolDialog.setLocationRelativeTo(locationRelativeTo);
        toolDialog.setVisible(true);
    }
    
    void displayErrorDialog(final Window window, final Throwable t) {
        if (t instanceof NoDisplayException) {
            return;
        }
        this.displayErrorDialog(window, t.toString());
    }
    
    void displayStatusDialog(final Window locationRelativeTo, final String s) {
        final ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Status"), this.tool, this, true);
        final Point point = (locationRelativeTo == null) ? this.getLocationOnScreen() : locationRelativeTo.getLocationOnScreen();
        toolDialog.setLayout(new GridBagLayout());
        this.addNewComponent(toolDialog, new JLabel(s), 0, 0, 0, 1, 1, 0.0, 0.0, 1);
        final JButton defaultButton = new JButton(PolicyTool.getMessage("OK"));
        final StatusOKButtonListener statusOKButtonListener = new StatusOKButtonListener(toolDialog);
        defaultButton.addActionListener(statusOKButtonListener);
        this.addNewComponent(toolDialog, defaultButton, 1, 0, 1, 1, 1, 0.0, 0.0, 3);
        toolDialog.getRootPane().setDefaultButton(defaultButton);
        toolDialog.getRootPane().registerKeyboardAction(statusOKButtonListener, ToolWindow.escKey, 2);
        toolDialog.pack();
        toolDialog.setLocationRelativeTo(locationRelativeTo);
        toolDialog.setVisible(true);
    }
    
    void displayWarningLog(final Window locationRelativeTo) {
        final ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Warning"), this.tool, this, true);
        final Point point = (locationRelativeTo == null) ? this.getLocationOnScreen() : locationRelativeTo.getLocationOnScreen();
        toolDialog.setLayout(new GridBagLayout());
        final JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        for (int i = 0; i < this.tool.warnings.size(); ++i) {
            textArea.append(this.tool.warnings.elementAt(i));
            textArea.append(PolicyTool.getMessage("NEWLINE"));
        }
        this.addNewComponent(toolDialog, textArea, 0, 0, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.BOTTOM_PADDING);
        textArea.setFocusable(false);
        final JButton defaultButton = new JButton(PolicyTool.getMessage("OK"));
        final CancelButtonListener cancelButtonListener = new CancelButtonListener(toolDialog);
        defaultButton.addActionListener(cancelButtonListener);
        this.addNewComponent(toolDialog, defaultButton, 1, 0, 1, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_PADDING);
        toolDialog.getRootPane().setDefaultButton(defaultButton);
        toolDialog.getRootPane().registerKeyboardAction(cancelButtonListener, ToolWindow.escKey, 2);
        toolDialog.pack();
        toolDialog.setLocationRelativeTo(locationRelativeTo);
        toolDialog.setVisible(true);
    }
    
    char displayYesNoDialog(final Window locationRelativeTo, final String s, final String s2, final String s3, final String s4) {
        final ToolDialog toolDialog = new ToolDialog(s, this.tool, this, true);
        final Point point = (locationRelativeTo == null) ? this.getLocationOnScreen() : locationRelativeTo.getLocationOnScreen();
        toolDialog.setLayout(new GridBagLayout());
        final JTextArea textArea = new JTextArea(s2, 10, 50);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        this.addNewComponent(toolDialog, new JScrollPane(textArea, 20, 31), 0, 0, 0, 1, 1, 0.0, 0.0, 1);
        textArea.setFocusable(false);
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final StringBuffer sb = new StringBuffer();
        final JButton button = new JButton(s3);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                sb.append('Y');
                toolDialog.setVisible(false);
                toolDialog.dispose();
            }
        });
        this.addNewComponent(panel, button, 0, 0, 0, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_PADDING);
        final JButton button2 = new JButton(s4);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                sb.append('N');
                toolDialog.setVisible(false);
                toolDialog.dispose();
            }
        });
        this.addNewComponent(panel, button2, 1, 1, 0, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_PADDING);
        this.addNewComponent(toolDialog, panel, 1, 0, 1, 1, 1, 0.0, 0.0, 3);
        toolDialog.pack();
        toolDialog.setLocationRelativeTo(locationRelativeTo);
        toolDialog.setVisible(true);
        if (sb.length() > 0) {
            return sb.charAt(0);
        }
        return 'N';
    }
    
    static {
        escKey = KeyStroke.getKeyStroke(27, 0);
        TOP_PADDING = new Insets(25, 0, 0, 0);
        BOTTOM_PADDING = new Insets(0, 0, 25, 0);
        LITE_BOTTOM_PADDING = new Insets(0, 0, 10, 0);
        LR_PADDING = new Insets(0, 10, 0, 10);
        TOP_BOTTOM_PADDING = new Insets(15, 0, 15, 0);
        L_TOP_BOTTOM_PADDING = new Insets(5, 10, 15, 0);
        LR_TOP_BOTTOM_PADDING = new Insets(15, 4, 15, 4);
        LR_BOTTOM_PADDING = new Insets(0, 10, 5, 10);
        L_BOTTOM_PADDING = new Insets(0, 10, 5, 0);
        R_BOTTOM_PADDING = new Insets(0, 0, 25, 5);
        R_PADDING = new Insets(0, 0, 0, 5);
        TEXTFIELD_HEIGHT = new JComboBox<Object>().getPreferredSize().height;
    }
}
