package sun.security.tools.policytool;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import java.io.FileNotFoundException;
import java.io.File;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.FileDialog;
import java.text.MessageFormat;
import java.awt.Window;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.LinkedList;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JComponent;
import java.awt.Container;
import javax.swing.JLabel;
import sun.security.provider.PolicyParser;
import javax.swing.JList;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JPanel;
import java.awt.event.WindowListener;
import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.KeyStroke;
import javax.swing.JDialog;

class ToolDialog extends JDialog
{
    private static final long serialVersionUID = -372244357011301190L;
    static final KeyStroke escKey;
    public static final int NOACTION = 0;
    public static final int QUIT = 1;
    public static final int NEW = 2;
    public static final int OPEN = 3;
    public static final String ALL_PERM_CLASS = "java.security.AllPermission";
    public static final String FILE_PERM_CLASS = "java.io.FilePermission";
    public static final String X500_PRIN_CLASS = "javax.security.auth.x500.X500Principal";
    public static final String PERM;
    public static final String PRIN_TYPE;
    public static final String PRIN_NAME;
    public static final String PERM_NAME;
    public static final String PERM_ACTIONS;
    public static final int PE_CODEBASE_LABEL = 0;
    public static final int PE_CODEBASE_TEXTFIELD = 1;
    public static final int PE_SIGNEDBY_LABEL = 2;
    public static final int PE_SIGNEDBY_TEXTFIELD = 3;
    public static final int PE_PANEL0 = 4;
    public static final int PE_ADD_PRIN_BUTTON = 0;
    public static final int PE_EDIT_PRIN_BUTTON = 1;
    public static final int PE_REMOVE_PRIN_BUTTON = 2;
    public static final int PE_PRIN_LABEL = 5;
    public static final int PE_PRIN_LIST = 6;
    public static final int PE_PANEL1 = 7;
    public static final int PE_ADD_PERM_BUTTON = 0;
    public static final int PE_EDIT_PERM_BUTTON = 1;
    public static final int PE_REMOVE_PERM_BUTTON = 2;
    public static final int PE_PERM_LIST = 8;
    public static final int PE_PANEL2 = 9;
    public static final int PE_CANCEL_BUTTON = 1;
    public static final int PE_DONE_BUTTON = 0;
    public static final int PRD_DESC_LABEL = 0;
    public static final int PRD_PRIN_CHOICE = 1;
    public static final int PRD_PRIN_TEXTFIELD = 2;
    public static final int PRD_NAME_LABEL = 3;
    public static final int PRD_NAME_TEXTFIELD = 4;
    public static final int PRD_CANCEL_BUTTON = 6;
    public static final int PRD_OK_BUTTON = 5;
    public static final int PD_DESC_LABEL = 0;
    public static final int PD_PERM_CHOICE = 1;
    public static final int PD_PERM_TEXTFIELD = 2;
    public static final int PD_NAME_CHOICE = 3;
    public static final int PD_NAME_TEXTFIELD = 4;
    public static final int PD_ACTIONS_CHOICE = 5;
    public static final int PD_ACTIONS_TEXTFIELD = 6;
    public static final int PD_SIGNEDBY_LABEL = 7;
    public static final int PD_SIGNEDBY_TEXTFIELD = 8;
    public static final int PD_CANCEL_BUTTON = 10;
    public static final int PD_OK_BUTTON = 9;
    public static final int EDIT_KEYSTORE = 0;
    public static final int KSD_NAME_LABEL = 0;
    public static final int KSD_NAME_TEXTFIELD = 1;
    public static final int KSD_TYPE_LABEL = 2;
    public static final int KSD_TYPE_TEXTFIELD = 3;
    public static final int KSD_PROVIDER_LABEL = 4;
    public static final int KSD_PROVIDER_TEXTFIELD = 5;
    public static final int KSD_PWD_URL_LABEL = 6;
    public static final int KSD_PWD_URL_TEXTFIELD = 7;
    public static final int KSD_CANCEL_BUTTON = 9;
    public static final int KSD_OK_BUTTON = 8;
    public static final int USC_LABEL = 0;
    public static final int USC_PANEL = 1;
    public static final int USC_YES_BUTTON = 0;
    public static final int USC_NO_BUTTON = 1;
    public static final int USC_CANCEL_BUTTON = 2;
    public static final int CRPE_LABEL1 = 0;
    public static final int CRPE_LABEL2 = 1;
    public static final int CRPE_PANEL = 2;
    public static final int CRPE_PANEL_OK = 0;
    public static final int CRPE_PANEL_CANCEL = 1;
    private static final int PERMISSION = 0;
    private static final int PERMISSION_NAME = 1;
    private static final int PERMISSION_ACTIONS = 2;
    private static final int PERMISSION_SIGNEDBY = 3;
    private static final int PRINCIPAL_TYPE = 4;
    private static final int PRINCIPAL_NAME = 5;
    static final int TEXTFIELD_HEIGHT;
    public static ArrayList<Perm> PERM_ARRAY;
    public static ArrayList<Prin> PRIN_ARRAY;
    PolicyTool tool;
    ToolWindow tw;
    
    ToolDialog(final String title, final PolicyTool tool, final ToolWindow tw, final boolean b) {
        super(tw, b);
        this.setTitle(title);
        this.tool = tool;
        this.tw = tw;
        this.addWindowListener(new ChildWindowListener(this));
        ((JPanel)this.getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
    }
    
    @Override
    public Component getComponent(final int n) {
        Component component = this.getContentPane().getComponent(n);
        if (component instanceof JScrollPane) {
            component = ((JScrollPane)component).getViewport().getView();
        }
        return component;
    }
    
    static Perm getPerm(final String s, final boolean b) {
        for (int i = 0; i < ToolDialog.PERM_ARRAY.size(); ++i) {
            final Perm perm = ToolDialog.PERM_ARRAY.get(i);
            if (b) {
                if (perm.FULL_CLASS.equals(s)) {
                    return perm;
                }
            }
            else if (perm.CLASS.equals(s)) {
                return perm;
            }
        }
        return null;
    }
    
    static Prin getPrin(final String s, final boolean b) {
        for (int i = 0; i < ToolDialog.PRIN_ARRAY.size(); ++i) {
            final Prin prin = ToolDialog.PRIN_ARRAY.get(i);
            if (b) {
                if (prin.FULL_CLASS.equals(s)) {
                    return prin;
                }
            }
            else if (prin.CLASS.equals(s)) {
                return prin;
            }
        }
        return null;
    }
    
    void displayPolicyEntryDialog(final boolean b) {
        int selectedIndex = 0;
        PolicyEntry[] entry = null;
        final TaggedList list = new TaggedList(3, false);
        list.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Principal.List"));
        list.addMouseListener(new EditPrinButtonListener(this.tool, this.tw, this, b));
        final TaggedList list2 = new TaggedList(10, false);
        list2.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Permission.List"));
        list2.addMouseListener(new EditPermButtonListener(this.tool, this.tw, this, b));
        this.tw.getLocationOnScreen();
        this.setLayout(new GridBagLayout());
        this.setResizable(true);
        if (b) {
            entry = this.tool.getEntry();
            selectedIndex = ((JList)this.tw.getComponent(3)).getSelectedIndex();
            final LinkedList<PolicyParser.PrincipalEntry> principals = entry[selectedIndex].getGrantEntry().principals;
            for (int i = 0; i < principals.size(); ++i) {
                final PolicyParser.PrincipalEntry principalEntry = principals.get(i);
                list.addTaggedItem(PrincipalEntryToUserFriendlyString(principalEntry), principalEntry);
            }
            final Vector<PolicyParser.PermissionEntry> permissionEntries = entry[selectedIndex].getGrantEntry().permissionEntries;
            for (int j = 0; j < permissionEntries.size(); ++j) {
                final PolicyParser.PermissionEntry permissionEntry = permissionEntries.elementAt(j);
                list2.addTaggedItem(PermissionEntryToUserFriendlyString(permissionEntry), permissionEntry);
            }
        }
        final JLabel label = new JLabel();
        this.tw.addNewComponent(this, label, 0, 0, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.R_PADDING);
        final JTextField textField = b ? new JTextField(entry[selectedIndex].getGrantEntry().codeBase) : new JTextField();
        ToolWindow.configureLabelFor(label, textField, "CodeBase.");
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
        textField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Code.Base"));
        this.tw.addNewComponent(this, textField, 1, 1, 0, 1, 1, 1.0, 0.0, 1);
        final JLabel label2 = new JLabel();
        this.tw.addNewComponent(this, label2, 2, 0, 1, 1, 1, 0.0, 0.0, 1, ToolWindow.R_PADDING);
        final JTextField textField2 = b ? new JTextField(entry[selectedIndex].getGrantEntry().signedBy) : new JTextField();
        ToolWindow.configureLabelFor(label2, textField2, "SignedBy.");
        textField2.setPreferredSize(new Dimension(textField2.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
        textField2.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Signed.By."));
        this.tw.addNewComponent(this, textField2, 3, 1, 1, 1, 1, 1.0, 0.0, 1);
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JButton button = new JButton();
        ToolWindow.configureButton(button, "Add.Principal");
        button.addActionListener(new AddPrinButtonListener(this.tool, this.tw, this, b));
        this.tw.addNewComponent(panel, button, 0, 0, 0, 1, 1, 100.0, 0.0, 2);
        final JButton button2 = new JButton();
        ToolWindow.configureButton(button2, "Edit.Principal");
        button2.addActionListener(new EditPrinButtonListener(this.tool, this.tw, this, b));
        this.tw.addNewComponent(panel, button2, 1, 1, 0, 1, 1, 100.0, 0.0, 2);
        final JButton button3 = new JButton();
        ToolWindow.configureButton(button3, "Remove.Principal");
        button3.addActionListener(new RemovePrinButtonListener(this.tool, this.tw, this, b));
        this.tw.addNewComponent(panel, button3, 2, 2, 0, 1, 1, 100.0, 0.0, 2);
        this.tw.addNewComponent(this, panel, 4, 1, 2, 1, 1, 0.0, 0.0, 2, ToolWindow.LITE_BOTTOM_PADDING);
        final JLabel label3 = new JLabel();
        this.tw.addNewComponent(this, label3, 5, 0, 3, 1, 1, 0.0, 0.0, 1, ToolWindow.R_BOTTOM_PADDING);
        final JScrollPane scrollPane = new JScrollPane(list);
        ToolWindow.configureLabelFor(label3, scrollPane, "Principals.");
        this.tw.addNewComponent(this, scrollPane, 6, 1, 3, 3, 1, 0.0, list.getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        final JButton button4 = new JButton();
        ToolWindow.configureButton(button4, ".Add.Permission");
        button4.addActionListener(new AddPermButtonListener(this.tool, this.tw, this, b));
        this.tw.addNewComponent(panel2, button4, 0, 0, 0, 1, 1, 100.0, 0.0, 2);
        final JButton button5 = new JButton();
        ToolWindow.configureButton(button5, ".Edit.Permission");
        button5.addActionListener(new EditPermButtonListener(this.tool, this.tw, this, b));
        this.tw.addNewComponent(panel2, button5, 1, 1, 0, 1, 1, 100.0, 0.0, 2);
        final JButton button6 = new JButton();
        ToolWindow.configureButton(button6, "Remove.Permission");
        button6.addActionListener(new RemovePermButtonListener(this.tool, this.tw, this, b));
        this.tw.addNewComponent(panel2, button6, 2, 2, 0, 1, 1, 100.0, 0.0, 2);
        this.tw.addNewComponent(this, panel2, 7, 0, 4, 2, 1, 0.0, 0.0, 2, ToolWindow.LITE_BOTTOM_PADDING);
        this.tw.addNewComponent(this, new JScrollPane(list2), 8, 0, 5, 3, 1, 0.0, list2.getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        final JButton defaultButton = new JButton(PolicyTool.getMessage("Done"));
        defaultButton.addActionListener(new AddEntryDoneButtonListener(this.tool, this.tw, this, b));
        this.tw.addNewComponent(panel3, defaultButton, 0, 0, 0, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_PADDING);
        final JButton button7 = new JButton(PolicyTool.getMessage("Cancel"));
        final CancelButtonListener cancelButtonListener = new CancelButtonListener(this);
        button7.addActionListener(cancelButtonListener);
        this.tw.addNewComponent(panel3, button7, 1, 1, 0, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_PADDING);
        this.tw.addNewComponent(this, panel3, 9, 0, 6, 2, 1, 0.0, 0.0, 3);
        this.getRootPane().setDefaultButton(defaultButton);
        this.getRootPane().registerKeyboardAction(cancelButtonListener, ToolDialog.escKey, 2);
        this.pack();
        this.setLocationRelativeTo(this.tw);
        this.setVisible(true);
    }
    
    PolicyEntry getPolicyEntryFromDialog() throws InvalidParameterException, MalformedURLException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, CertificateException, IOException, Exception {
        final JTextField textField = (JTextField)this.getComponent(1);
        String s = null;
        if (!textField.getText().trim().equals("")) {
            s = new String(textField.getText().trim());
        }
        final JTextField textField2 = (JTextField)this.getComponent(3);
        String s2 = null;
        if (!textField2.getText().trim().equals("")) {
            s2 = new String(textField2.getText().trim());
        }
        final PolicyParser.GrantEntry grantEntry = new PolicyParser.GrantEntry(s2, s);
        final LinkedList<PolicyParser.PrincipalEntry> principals = new LinkedList<PolicyParser.PrincipalEntry>();
        final TaggedList list = (TaggedList)this.getComponent(6);
        for (int i = 0; i < list.getModel().getSize(); ++i) {
            principals.add((PolicyParser.PrincipalEntry)list.getObject(i));
        }
        grantEntry.principals = principals;
        final Vector<PolicyParser.PermissionEntry> permissionEntries = new Vector<PolicyParser.PermissionEntry>();
        final TaggedList list2 = (TaggedList)this.getComponent(8);
        for (int j = 0; j < list2.getModel().getSize(); ++j) {
            permissionEntries.addElement((PolicyParser.PermissionEntry)list2.getObject(j));
        }
        grantEntry.permissionEntries = permissionEntries;
        return new PolicyEntry(this.tool, grantEntry);
    }
    
    void keyStoreDialog(final int n) {
        this.tw.getLocationOnScreen();
        this.setLayout(new GridBagLayout());
        if (n == 0) {
            final JLabel label = new JLabel();
            this.tw.addNewComponent(this, label, 0, 0, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.R_BOTTOM_PADDING);
            final JTextField textField = new JTextField(this.tool.getKeyStoreName(), 30);
            ToolWindow.configureLabelFor(label, textField, "KeyStore.URL.");
            textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
            textField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.U.R.L."));
            this.tw.addNewComponent(this, textField, 1, 1, 0, 1, 1, 1.0, 0.0, 1, ToolWindow.BOTTOM_PADDING);
            final JLabel label2 = new JLabel();
            this.tw.addNewComponent(this, label2, 2, 0, 1, 1, 1, 0.0, 0.0, 1, ToolWindow.R_BOTTOM_PADDING);
            final JTextField textField2 = new JTextField(this.tool.getKeyStoreType(), 30);
            ToolWindow.configureLabelFor(label2, textField2, "KeyStore.Type.");
            textField2.setPreferredSize(new Dimension(textField2.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
            textField2.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Type."));
            this.tw.addNewComponent(this, textField2, 3, 1, 1, 1, 1, 1.0, 0.0, 1, ToolWindow.BOTTOM_PADDING);
            final JLabel label3 = new JLabel();
            this.tw.addNewComponent(this, label3, 4, 0, 2, 1, 1, 0.0, 0.0, 1, ToolWindow.R_BOTTOM_PADDING);
            final JTextField textField3 = new JTextField(this.tool.getKeyStoreProvider(), 30);
            ToolWindow.configureLabelFor(label3, textField3, "KeyStore.Provider.");
            textField3.setPreferredSize(new Dimension(textField3.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
            textField3.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Provider."));
            this.tw.addNewComponent(this, textField3, 5, 1, 2, 1, 1, 1.0, 0.0, 1, ToolWindow.BOTTOM_PADDING);
            final JLabel label4 = new JLabel();
            this.tw.addNewComponent(this, label4, 6, 0, 3, 1, 1, 0.0, 0.0, 1, ToolWindow.R_BOTTOM_PADDING);
            final JTextField textField4 = new JTextField(this.tool.getKeyStorePwdURL(), 30);
            ToolWindow.configureLabelFor(label4, textField4, "KeyStore.Password.URL.");
            textField4.setPreferredSize(new Dimension(textField4.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
            textField4.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Password.U.R.L."));
            this.tw.addNewComponent(this, textField4, 7, 1, 3, 1, 1, 1.0, 0.0, 1, ToolWindow.BOTTOM_PADDING);
            final JButton defaultButton = new JButton(PolicyTool.getMessage("OK"));
            defaultButton.addActionListener(new ChangeKeyStoreOKButtonListener(this.tool, this.tw, this));
            this.tw.addNewComponent(this, defaultButton, 8, 0, 4, 1, 1, 0.0, 0.0, 3);
            final JButton button = new JButton(PolicyTool.getMessage("Cancel"));
            final CancelButtonListener cancelButtonListener = new CancelButtonListener(this);
            button.addActionListener(cancelButtonListener);
            this.tw.addNewComponent(this, button, 9, 1, 4, 1, 1, 0.0, 0.0, 3);
            this.getRootPane().setDefaultButton(defaultButton);
            this.getRootPane().registerKeyboardAction(cancelButtonListener, ToolDialog.escKey, 2);
        }
        this.pack();
        this.setLocationRelativeTo(this.tw);
        this.setVisible(true);
    }
    
    void displayPrincipalDialog(final boolean b, final boolean b2) {
        PolicyParser.PrincipalEntry principalEntry = null;
        final TaggedList list = (TaggedList)this.getComponent(6);
        final int selectedIndex = list.getSelectedIndex();
        if (b2) {
            principalEntry = (PolicyParser.PrincipalEntry)list.getObject(selectedIndex);
        }
        final ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Principals"), this.tool, this.tw, true);
        toolDialog.addWindowListener(new ChildWindowListener(toolDialog));
        this.getLocationOnScreen();
        toolDialog.setLayout(new GridBagLayout());
        toolDialog.setResizable(true);
        this.tw.addNewComponent(toolDialog, b2 ? new JLabel(PolicyTool.getMessage(".Edit.Principal.")) : new JLabel(PolicyTool.getMessage(".Add.New.Principal.")), 0, 0, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.TOP_BOTTOM_PADDING);
        final JComboBox comboBox = new JComboBox();
        comboBox.addItem(ToolDialog.PRIN_TYPE);
        comboBox.getAccessibleContext().setAccessibleName(ToolDialog.PRIN_TYPE);
        for (int i = 0; i < ToolDialog.PRIN_ARRAY.size(); ++i) {
            comboBox.addItem(ToolDialog.PRIN_ARRAY.get(i).CLASS);
        }
        if (b2) {
            if ("WILDCARD_PRINCIPAL_CLASS".equals(principalEntry.getPrincipalClass())) {
                comboBox.setSelectedItem(ToolDialog.PRIN_TYPE);
            }
            else {
                final Prin prin = getPrin(principalEntry.getPrincipalClass(), true);
                if (prin != null) {
                    comboBox.setSelectedItem(prin.CLASS);
                }
            }
        }
        comboBox.addItemListener(new PrincipalTypeMenuListener(toolDialog));
        this.tw.addNewComponent(toolDialog, comboBox, 1, 0, 1, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_PADDING);
        final JTextField textField = b2 ? new JTextField(principalEntry.getDisplayClass(), 30) : new JTextField(30);
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
        textField.getAccessibleContext().setAccessibleName(ToolDialog.PRIN_TYPE);
        this.tw.addNewComponent(toolDialog, textField, 2, 1, 1, 1, 1, 1.0, 0.0, 1, ToolWindow.LR_PADDING);
        final JLabel label = new JLabel(ToolDialog.PRIN_NAME);
        final JTextField textField2 = b2 ? new JTextField(principalEntry.getDisplayName(), 40) : new JTextField(40);
        textField2.setPreferredSize(new Dimension(textField2.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
        textField2.getAccessibleContext().setAccessibleName(ToolDialog.PRIN_NAME);
        this.tw.addNewComponent(toolDialog, label, 3, 0, 2, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_PADDING);
        this.tw.addNewComponent(toolDialog, textField2, 4, 1, 2, 1, 1, 1.0, 0.0, 1, ToolWindow.LR_PADDING);
        final JButton defaultButton = new JButton(PolicyTool.getMessage("OK"));
        defaultButton.addActionListener(new NewPolicyPrinOKButtonListener(this.tool, this.tw, this, toolDialog, b2));
        this.tw.addNewComponent(toolDialog, defaultButton, 5, 0, 3, 1, 1, 0.0, 0.0, 3, ToolWindow.TOP_BOTTOM_PADDING);
        final JButton button = new JButton(PolicyTool.getMessage("Cancel"));
        final CancelButtonListener cancelButtonListener = new CancelButtonListener(toolDialog);
        button.addActionListener(cancelButtonListener);
        this.tw.addNewComponent(toolDialog, button, 6, 1, 3, 1, 1, 0.0, 0.0, 3, ToolWindow.TOP_BOTTOM_PADDING);
        toolDialog.getRootPane().setDefaultButton(defaultButton);
        toolDialog.getRootPane().registerKeyboardAction(cancelButtonListener, ToolDialog.escKey, 2);
        toolDialog.pack();
        toolDialog.setLocationRelativeTo(this.tw);
        toolDialog.setVisible(true);
    }
    
    void displayPermissionDialog(final boolean b, final boolean b2) {
        PolicyParser.PermissionEntry permissionEntry = null;
        final TaggedList list = (TaggedList)this.getComponent(8);
        final int selectedIndex = list.getSelectedIndex();
        if (b2) {
            permissionEntry = (PolicyParser.PermissionEntry)list.getObject(selectedIndex);
        }
        final ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Permissions"), this.tool, this.tw, true);
        toolDialog.addWindowListener(new ChildWindowListener(toolDialog));
        this.getLocationOnScreen();
        toolDialog.setLayout(new GridBagLayout());
        toolDialog.setResizable(true);
        this.tw.addNewComponent(toolDialog, b2 ? new JLabel(PolicyTool.getMessage(".Edit.Permission.")) : new JLabel(PolicyTool.getMessage(".Add.New.Permission.")), 0, 0, 0, 1, 1, 0.0, 0.0, 1, ToolWindow.TOP_BOTTOM_PADDING);
        final JComboBox<String> comboBox = new JComboBox<String>();
        comboBox.addItem(ToolDialog.PERM);
        comboBox.getAccessibleContext().setAccessibleName(ToolDialog.PERM);
        for (int i = 0; i < ToolDialog.PERM_ARRAY.size(); ++i) {
            comboBox.addItem(ToolDialog.PERM_ARRAY.get(i).CLASS);
        }
        this.tw.addNewComponent(toolDialog, comboBox, 1, 0, 1, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_BOTTOM_PADDING);
        final JTextField textField = b2 ? new JTextField(permissionEntry.permission, 30) : new JTextField(30);
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
        textField.getAccessibleContext().setAccessibleName(ToolDialog.PERM);
        if (b2) {
            final Perm perm = getPerm(permissionEntry.permission, true);
            if (perm != null) {
                comboBox.setSelectedItem(perm.CLASS);
            }
        }
        this.tw.addNewComponent(toolDialog, textField, 2, 1, 1, 1, 1, 1.0, 0.0, 1, ToolWindow.LR_BOTTOM_PADDING);
        comboBox.addItemListener(new PermissionMenuListener(toolDialog));
        final JComboBox comboBox2 = new JComboBox();
        comboBox2.addItem(ToolDialog.PERM_NAME);
        comboBox2.getAccessibleContext().setAccessibleName(ToolDialog.PERM_NAME);
        final JTextField textField2 = b2 ? new JTextField(permissionEntry.name, 40) : new JTextField(40);
        textField2.setPreferredSize(new Dimension(textField2.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
        textField2.getAccessibleContext().setAccessibleName(ToolDialog.PERM_NAME);
        if (b2) {
            this.setPermissionNames(getPerm(permissionEntry.permission, true), comboBox2, textField2);
        }
        this.tw.addNewComponent(toolDialog, comboBox2, 3, 0, 2, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_BOTTOM_PADDING);
        this.tw.addNewComponent(toolDialog, textField2, 4, 1, 2, 1, 1, 1.0, 0.0, 1, ToolWindow.LR_BOTTOM_PADDING);
        comboBox2.addItemListener(new PermissionNameMenuListener(toolDialog));
        final JComboBox comboBox3 = new JComboBox();
        comboBox3.addItem(ToolDialog.PERM_ACTIONS);
        comboBox3.getAccessibleContext().setAccessibleName(ToolDialog.PERM_ACTIONS);
        final JTextField textField3 = b2 ? new JTextField(permissionEntry.action, 40) : new JTextField(40);
        textField3.setPreferredSize(new Dimension(textField3.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
        textField3.getAccessibleContext().setAccessibleName(ToolDialog.PERM_ACTIONS);
        if (b2) {
            this.setPermissionActions(getPerm(permissionEntry.permission, true), comboBox3, textField3);
        }
        this.tw.addNewComponent(toolDialog, comboBox3, 5, 0, 3, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_BOTTOM_PADDING);
        this.tw.addNewComponent(toolDialog, textField3, 6, 1, 3, 1, 1, 1.0, 0.0, 1, ToolWindow.LR_BOTTOM_PADDING);
        comboBox3.addItemListener(new PermissionActionsMenuListener(toolDialog));
        this.tw.addNewComponent(toolDialog, new JLabel(PolicyTool.getMessage("Signed.By.")), 7, 0, 4, 1, 1, 0.0, 0.0, 1, ToolWindow.LR_BOTTOM_PADDING);
        final JTextField textField4 = b2 ? new JTextField(permissionEntry.signedBy, 40) : new JTextField(40);
        textField4.setPreferredSize(new Dimension(textField4.getPreferredSize().width, ToolDialog.TEXTFIELD_HEIGHT));
        textField4.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Signed.By."));
        this.tw.addNewComponent(toolDialog, textField4, 8, 1, 4, 1, 1, 1.0, 0.0, 1, ToolWindow.LR_BOTTOM_PADDING);
        final JButton defaultButton = new JButton(PolicyTool.getMessage("OK"));
        defaultButton.addActionListener(new NewPolicyPermOKButtonListener(this.tool, this.tw, this, toolDialog, b2));
        this.tw.addNewComponent(toolDialog, defaultButton, 9, 0, 5, 1, 1, 0.0, 0.0, 3, ToolWindow.TOP_BOTTOM_PADDING);
        final JButton button = new JButton(PolicyTool.getMessage("Cancel"));
        final CancelButtonListener cancelButtonListener = new CancelButtonListener(toolDialog);
        button.addActionListener(cancelButtonListener);
        this.tw.addNewComponent(toolDialog, button, 10, 1, 5, 1, 1, 0.0, 0.0, 3, ToolWindow.TOP_BOTTOM_PADDING);
        toolDialog.getRootPane().setDefaultButton(defaultButton);
        toolDialog.getRootPane().registerKeyboardAction(cancelButtonListener, ToolDialog.escKey, 2);
        toolDialog.pack();
        toolDialog.setLocationRelativeTo(this.tw);
        toolDialog.setVisible(true);
    }
    
    PolicyParser.PrincipalEntry getPrinFromDialog() throws Exception {
        String s = new String(((JTextField)this.getComponent(2)).getText().trim());
        String s2 = new String(((JTextField)this.getComponent(4)).getText().trim());
        if (s.equals("*")) {
            s = "WILDCARD_PRINCIPAL_CLASS";
        }
        if (s2.equals("*")) {
            s2 = "WILDCARD_PRINCIPAL_NAME";
        }
        if (s.equals("WILDCARD_PRINCIPAL_CLASS") && !s2.equals("WILDCARD_PRINCIPAL_NAME")) {
            throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name"));
        }
        if (s2.equals("")) {
            throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.without.a.Name"));
        }
        if (s.equals("")) {
            s = "PolicyParser.REPLACE_NAME";
            this.tool.warnings.addElement("Warning: Principal name '" + s2 + "' specified without a Principal class.\n\t'" + s2 + "' will be interpreted as a key store alias.\n\tThe final principal class will be " + "javax.security.auth.x500.X500Principal" + ".\n\tThe final principal name will be determined by the following:\n\n\tIf the key store entry identified by '" + s2 + "'\n\tis a key entry, then the principal name will be\n\tthe subject distinguished name from the first\n\tcertificate in the entry's certificate chain.\n\n\tIf the key store entry identified by '" + s2 + "'\n\tis a trusted certificate entry, then the\n\tprincipal name will be the subject distinguished\n\tname from the trusted public key certificate.");
            this.tw.displayStatusDialog(this, "'" + s2 + "' will be interpreted as a key store alias.  View Warning Log for details.");
        }
        return new PolicyParser.PrincipalEntry(s, s2);
    }
    
    PolicyParser.PermissionEntry getPermFromDialog() {
        final String s = new String(((JTextField)this.getComponent(2)).getText().trim());
        final JTextField textField = (JTextField)this.getComponent(4);
        String s2 = null;
        if (!textField.getText().trim().equals("")) {
            s2 = new String(textField.getText().trim());
        }
        if (s.equals("") || (!s.equals("java.security.AllPermission") && s2 == null)) {
            throw new InvalidParameterException(PolicyTool.getMessage("Permission.and.Target.Name.must.have.a.value"));
        }
        if (s.equals("java.io.FilePermission") && s2.lastIndexOf("\\\\") > 0 && this.tw.displayYesNoDialog(this, PolicyTool.getMessage("Warning"), PolicyTool.getMessage("Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes"), PolicyTool.getMessage("Retain"), PolicyTool.getMessage("Edit")) != 'Y') {
            throw new NoDisplayException();
        }
        final JTextField textField2 = (JTextField)this.getComponent(6);
        String s3 = null;
        if (!textField2.getText().trim().equals("")) {
            s3 = new String(textField2.getText().trim());
        }
        final JTextField textField3 = (JTextField)this.getComponent(8);
        String signedBy = null;
        if (!textField3.getText().trim().equals("")) {
            signedBy = new String(textField3.getText().trim());
        }
        final PolicyParser.PermissionEntry permissionEntry = new PolicyParser.PermissionEntry(s, s2, s3);
        if ((permissionEntry.signedBy = signedBy) != null) {
            final String[] signers = this.tool.parseSigners(permissionEntry.signedBy);
            for (int i = 0; i < signers.length; ++i) {
                try {
                    if (this.tool.getPublicKeyAlias(signers[i]) == null) {
                        final MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
                        final Object[] array = { signers[i] };
                        this.tool.warnings.addElement(messageFormat.format(array));
                        this.tw.displayStatusDialog(this, messageFormat.format(array));
                    }
                }
                catch (final Exception ex) {
                    this.tw.displayErrorDialog(this, ex);
                }
            }
        }
        return permissionEntry;
    }
    
    void displayConfirmRemovePolicyEntry() {
        final int selectedIndex = ((JList)this.tw.getComponent(3)).getSelectedIndex();
        final PolicyEntry[] entry = this.tool.getEntry();
        this.tw.getLocationOnScreen();
        this.setLayout(new GridBagLayout());
        this.tw.addNewComponent(this, new JLabel(PolicyTool.getMessage("Remove.this.Policy.Entry.")), 0, 0, 0, 2, 1, 0.0, 0.0, 1, ToolWindow.BOTTOM_PADDING);
        this.tw.addNewComponent(this, new JLabel(entry[selectedIndex].codebaseToString()), 1, 0, 1, 2, 1, 0.0, 0.0, 1);
        this.tw.addNewComponent(this, new JLabel(entry[selectedIndex].principalsToString().trim()), 2, 0, 2, 2, 1, 0.0, 0.0, 1);
        final Vector<PolicyParser.PermissionEntry> permissionEntries = entry[selectedIndex].getGrantEntry().permissionEntries;
        for (int i = 0; i < permissionEntries.size(); ++i) {
            final JLabel label = new JLabel("    " + PermissionEntryToUserFriendlyString(permissionEntries.elementAt(i)));
            if (i == permissionEntries.size() - 1) {
                this.tw.addNewComponent(this, label, 3 + i, 1, 3 + i, 1, 1, 0.0, 0.0, 1, ToolWindow.BOTTOM_PADDING);
            }
            else {
                this.tw.addNewComponent(this, label, 3 + i, 1, 3 + i, 1, 1, 0.0, 0.0, 1);
            }
        }
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JButton defaultButton = new JButton(PolicyTool.getMessage("OK"));
        defaultButton.addActionListener(new ConfirmRemovePolicyEntryOKButtonListener(this.tool, this.tw, this));
        this.tw.addNewComponent(panel, defaultButton, 0, 0, 0, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_PADDING);
        final JButton button = new JButton(PolicyTool.getMessage("Cancel"));
        final CancelButtonListener cancelButtonListener = new CancelButtonListener(this);
        button.addActionListener(cancelButtonListener);
        this.tw.addNewComponent(panel, button, 1, 1, 0, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_PADDING);
        this.tw.addNewComponent(this, panel, 3 + permissionEntries.size(), 0, 3 + permissionEntries.size(), 2, 1, 0.0, 0.0, 3, ToolWindow.TOP_BOTTOM_PADDING);
        this.getRootPane().setDefaultButton(defaultButton);
        this.getRootPane().registerKeyboardAction(cancelButtonListener, ToolDialog.escKey, 2);
        this.pack();
        this.setLocationRelativeTo(this.tw);
        this.setVisible(true);
    }
    
    void displaySaveAsDialog(final int n) {
        final FileDialog fileDialog = new FileDialog(this.tw, PolicyTool.getMessage("Save.As"), 1);
        fileDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                windowEvent.getWindow().setVisible(false);
            }
        });
        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null || fileDialog.getFile().equals("")) {
            return;
        }
        final String path = new File(fileDialog.getDirectory(), fileDialog.getFile()).getPath();
        fileDialog.dispose();
        try {
            this.tool.savePolicy(path);
            this.tw.displayStatusDialog(null, new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename")).format(new Object[] { path }));
            ((JTextField)this.tw.getComponent(1)).setText(path);
            this.tw.setVisible(true);
            this.userSaveContinue(this.tool, this.tw, this, n);
        }
        catch (final FileNotFoundException ex) {
            if (path == null || path.equals("")) {
                this.tw.displayErrorDialog(null, new FileNotFoundException(PolicyTool.getMessage("null.filename")));
            }
            else {
                this.tw.displayErrorDialog(null, ex);
            }
        }
        catch (final Exception ex2) {
            this.tw.displayErrorDialog(null, ex2);
        }
    }
    
    void displayUserSave(final int n) {
        if (this.tool.modified) {
            this.tw.getLocationOnScreen();
            this.setLayout(new GridBagLayout());
            this.tw.addNewComponent(this, new JLabel(PolicyTool.getMessage("Save.changes.")), 0, 0, 0, 3, 1, 0.0, 0.0, 1, ToolWindow.L_TOP_BOTTOM_PADDING);
            final JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            final JButton button = new JButton();
            ToolWindow.configureButton(button, "Yes");
            button.addActionListener(new UserSaveYesButtonListener(this, this.tool, this.tw, n));
            this.tw.addNewComponent(panel, button, 0, 0, 0, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_BOTTOM_PADDING);
            final JButton button2 = new JButton();
            ToolWindow.configureButton(button2, "No");
            button2.addActionListener(new UserSaveNoButtonListener(this, this.tool, this.tw, n));
            this.tw.addNewComponent(panel, button2, 1, 1, 0, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_BOTTOM_PADDING);
            final JButton button3 = new JButton();
            ToolWindow.configureButton(button3, "Cancel");
            final CancelButtonListener cancelButtonListener = new CancelButtonListener(this);
            button3.addActionListener(cancelButtonListener);
            this.tw.addNewComponent(panel, button3, 2, 2, 0, 1, 1, 0.0, 0.0, 3, ToolWindow.LR_BOTTOM_PADDING);
            this.tw.addNewComponent(this, panel, 1, 0, 1, 1, 1, 0.0, 0.0, 1);
            this.getRootPane().registerKeyboardAction(cancelButtonListener, ToolDialog.escKey, 2);
            this.pack();
            this.setLocationRelativeTo(this.tw);
            this.setVisible(true);
        }
        else {
            this.userSaveContinue(this.tool, this.tw, this, n);
        }
    }
    
    void userSaveContinue(final PolicyTool policyTool, final ToolWindow toolWindow, final ToolDialog toolDialog, final int n) {
        switch (n) {
            case 1: {
                toolWindow.setVisible(false);
                toolWindow.dispose();
                System.exit(0);
            }
            case 2: {
                try {
                    policyTool.openPolicy(null);
                }
                catch (final Exception ex) {
                    policyTool.modified = false;
                    toolWindow.displayErrorDialog(null, ex);
                }
                final JList list = new JList(new DefaultListModel<Object>());
                list.setVisibleRowCount(15);
                list.setSelectionMode(0);
                list.addMouseListener(new PolicyListListener(policyTool, toolWindow));
                toolWindow.replacePolicyList(list);
                ((JTextField)toolWindow.getComponent(1)).setText("");
                toolWindow.setVisible(true);
                break;
            }
            case 3: {
                final FileDialog fileDialog = new FileDialog(toolWindow, PolicyTool.getMessage("Open"), 0);
                fileDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(final WindowEvent windowEvent) {
                        windowEvent.getWindow().setVisible(false);
                    }
                });
                fileDialog.setVisible(true);
                if (fileDialog.getFile() == null || fileDialog.getFile().equals("")) {
                    return;
                }
                final String path = new File(fileDialog.getDirectory(), fileDialog.getFile()).getPath();
                try {
                    policyTool.openPolicy(path);
                    final DefaultListModel defaultListModel = new DefaultListModel();
                    final JList list2 = new JList(defaultListModel);
                    list2.setVisibleRowCount(15);
                    list2.setSelectionMode(0);
                    list2.addMouseListener(new PolicyListListener(policyTool, toolWindow));
                    final PolicyEntry[] entry = policyTool.getEntry();
                    if (entry != null) {
                        for (int i = 0; i < entry.length; ++i) {
                            defaultListModel.addElement(entry[i].headerToString());
                        }
                    }
                    toolWindow.replacePolicyList(list2);
                    policyTool.modified = false;
                    ((JTextField)toolWindow.getComponent(1)).setText(path);
                    toolWindow.setVisible(true);
                    if (policyTool.newWarning) {
                        toolWindow.displayStatusDialog(null, PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information."));
                    }
                }
                catch (final Exception ex2) {
                    final JList list3 = new JList((ListModel<E>)new DefaultListModel<Object>());
                    list3.setVisibleRowCount(15);
                    list3.setSelectionMode(0);
                    list3.addMouseListener(new PolicyListListener(policyTool, toolWindow));
                    toolWindow.replacePolicyList(list3);
                    policyTool.setPolicyFileName(null);
                    policyTool.modified = false;
                    ((JTextField)toolWindow.getComponent(1)).setText("");
                    toolWindow.setVisible(true);
                    toolWindow.displayErrorDialog(null, new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString.")).format(new Object[] { path, ex2.toString() }));
                }
                break;
            }
        }
    }
    
    void setPermissionNames(final Perm perm, final JComboBox comboBox, final JTextField textField) {
        comboBox.removeAllItems();
        comboBox.addItem(ToolDialog.PERM_NAME);
        if (perm == null) {
            textField.setEditable(true);
        }
        else if (perm.TARGETS == null) {
            textField.setEditable(false);
        }
        else {
            textField.setEditable(true);
            for (int i = 0; i < perm.TARGETS.length; ++i) {
                comboBox.addItem(perm.TARGETS[i]);
            }
        }
    }
    
    void setPermissionActions(final Perm perm, final JComboBox comboBox, final JTextField textField) {
        comboBox.removeAllItems();
        comboBox.addItem(ToolDialog.PERM_ACTIONS);
        if (perm == null) {
            textField.setEditable(true);
        }
        else if (perm.ACTIONS == null) {
            textField.setEditable(false);
        }
        else {
            textField.setEditable(true);
            for (int i = 0; i < perm.ACTIONS.length; ++i) {
                comboBox.addItem(perm.ACTIONS[i]);
            }
        }
    }
    
    static String PermissionEntryToUserFriendlyString(final PolicyParser.PermissionEntry permissionEntry) {
        String s = permissionEntry.permission;
        if (permissionEntry.name != null) {
            s = s + " " + permissionEntry.name;
        }
        if (permissionEntry.action != null) {
            s = s + ", \"" + permissionEntry.action + "\"";
        }
        if (permissionEntry.signedBy != null) {
            s = s + ", signedBy " + permissionEntry.signedBy;
        }
        return s;
    }
    
    static String PrincipalEntryToUserFriendlyString(final PolicyParser.PrincipalEntry principalEntry) {
        final StringWriter stringWriter = new StringWriter();
        principalEntry.write(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
    
    static {
        escKey = KeyStroke.getKeyStroke(27, 0);
        PERM = PolicyTool.getMessage("Permission.");
        PRIN_TYPE = PolicyTool.getMessage("Principal.Type.");
        PRIN_NAME = PolicyTool.getMessage("Principal.Name.");
        PERM_NAME = PolicyTool.getMessage("Target.Name.");
        PERM_ACTIONS = PolicyTool.getMessage("Actions.");
        TEXTFIELD_HEIGHT = new JComboBox<Object>().getPreferredSize().height;
        (ToolDialog.PERM_ARRAY = new ArrayList<Perm>()).add(new AllPerm());
        ToolDialog.PERM_ARRAY.add(new AudioPerm());
        ToolDialog.PERM_ARRAY.add(new AuthPerm());
        ToolDialog.PERM_ARRAY.add(new AWTPerm());
        ToolDialog.PERM_ARRAY.add(new DelegationPerm());
        ToolDialog.PERM_ARRAY.add(new FilePerm());
        ToolDialog.PERM_ARRAY.add(new URLPerm());
        ToolDialog.PERM_ARRAY.add(new InqSecContextPerm());
        ToolDialog.PERM_ARRAY.add(new LogPerm());
        ToolDialog.PERM_ARRAY.add(new MgmtPerm());
        ToolDialog.PERM_ARRAY.add(new MBeanPerm());
        ToolDialog.PERM_ARRAY.add(new MBeanSvrPerm());
        ToolDialog.PERM_ARRAY.add(new MBeanTrustPerm());
        ToolDialog.PERM_ARRAY.add(new NetPerm());
        ToolDialog.PERM_ARRAY.add(new PrivCredPerm());
        ToolDialog.PERM_ARRAY.add(new PropPerm());
        ToolDialog.PERM_ARRAY.add(new ReflectPerm());
        ToolDialog.PERM_ARRAY.add(new RuntimePerm());
        ToolDialog.PERM_ARRAY.add(new SecurityPerm());
        ToolDialog.PERM_ARRAY.add(new SerialPerm());
        ToolDialog.PERM_ARRAY.add(new ServicePerm());
        ToolDialog.PERM_ARRAY.add(new SocketPerm());
        ToolDialog.PERM_ARRAY.add(new SQLPerm());
        ToolDialog.PERM_ARRAY.add(new SSLPerm());
        ToolDialog.PERM_ARRAY.add(new SubjDelegPerm());
        (ToolDialog.PRIN_ARRAY = new ArrayList<Prin>()).add(new KrbPrin());
        ToolDialog.PRIN_ARRAY.add(new X500Prin());
    }
}
