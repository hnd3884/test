package javax.swing.plaf.metal;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.filechooser.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Arrays;
import sun.awt.shell.ShellFolder;
import java.util.Vector;
import javax.swing.AbstractListModel;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.DefaultListCellRenderer;
import java.awt.event.MouseAdapter;
import javax.swing.plaf.basic.BasicDirectoryModel;
import java.awt.event.MouseListener;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import java.awt.ComponentOrientation;
import java.io.File;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import sun.swing.SwingUtilities2;
import java.util.Locale;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.UIManager;
import javax.swing.Box;
import javax.swing.ListCellRenderer;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import java.awt.Component;
import java.awt.Container;
import javax.swing.BoxLayout;
import java.beans.PropertyChangeListener;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import sun.swing.FilePane;
import javax.swing.JTextField;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicFileChooserUI;

public class MetalFileChooserUI extends BasicFileChooserUI
{
    private JLabel lookInLabel;
    private JComboBox directoryComboBox;
    private DirectoryComboBoxModel directoryComboBoxModel;
    private Action directoryComboBoxAction;
    private FilterComboBoxModel filterComboBoxModel;
    private JTextField fileNameTextField;
    private FilePane filePane;
    private JToggleButton listViewButton;
    private JToggleButton detailsViewButton;
    private JButton approveButton;
    private JButton cancelButton;
    private JPanel buttonPanel;
    private JPanel bottomPanel;
    private JComboBox filterComboBox;
    private static final Dimension hstrut5;
    private static final Dimension hstrut11;
    private static final Dimension vstrut5;
    private static final Insets shrinkwrap;
    private static int PREF_WIDTH;
    private static int PREF_HEIGHT;
    private static Dimension PREF_SIZE;
    private static int MIN_WIDTH;
    private static int MIN_HEIGHT;
    private static int LIST_PREF_WIDTH;
    private static int LIST_PREF_HEIGHT;
    private static Dimension LIST_PREF_SIZE;
    private int lookInLabelMnemonic;
    private String lookInLabelText;
    private String saveInLabelText;
    private int fileNameLabelMnemonic;
    private String fileNameLabelText;
    private int folderNameLabelMnemonic;
    private String folderNameLabelText;
    private int filesOfTypeLabelMnemonic;
    private String filesOfTypeLabelText;
    private String upFolderToolTipText;
    private String upFolderAccessibleName;
    private String homeFolderToolTipText;
    private String homeFolderAccessibleName;
    private String newFolderToolTipText;
    private String newFolderAccessibleName;
    private String listViewButtonToolTipText;
    private String listViewButtonAccessibleName;
    private String detailsViewButtonToolTipText;
    private String detailsViewButtonAccessibleName;
    private AlignedLabel fileNameLabel;
    static final int space = 10;
    
    private void populateFileNameLabel() {
        if (this.getFileChooser().getFileSelectionMode() == 1) {
            this.fileNameLabel.setText(this.folderNameLabelText);
            this.fileNameLabel.setDisplayedMnemonic(this.folderNameLabelMnemonic);
        }
        else {
            this.fileNameLabel.setText(this.fileNameLabelText);
            this.fileNameLabel.setDisplayedMnemonic(this.fileNameLabelMnemonic);
        }
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalFileChooserUI((JFileChooser)component);
    }
    
    public MetalFileChooserUI(final JFileChooser fileChooser) {
        super(fileChooser);
        this.directoryComboBoxAction = new DirectoryComboBoxAction();
        this.lookInLabelMnemonic = 0;
        this.lookInLabelText = null;
        this.saveInLabelText = null;
        this.fileNameLabelMnemonic = 0;
        this.fileNameLabelText = null;
        this.folderNameLabelMnemonic = 0;
        this.folderNameLabelText = null;
        this.filesOfTypeLabelMnemonic = 0;
        this.filesOfTypeLabelText = null;
        this.upFolderToolTipText = null;
        this.upFolderAccessibleName = null;
        this.homeFolderToolTipText = null;
        this.homeFolderAccessibleName = null;
        this.newFolderToolTipText = null;
        this.newFolderAccessibleName = null;
        this.listViewButtonToolTipText = null;
        this.listViewButtonAccessibleName = null;
        this.detailsViewButtonToolTipText = null;
        this.detailsViewButtonAccessibleName = null;
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
    }
    
    @Override
    public void uninstallComponents(final JFileChooser fileChooser) {
        fileChooser.removeAll();
        this.bottomPanel = null;
        this.buttonPanel = null;
    }
    
    @Override
    public void installComponents(final JFileChooser fileChooser) {
        final FileSystemView fileSystemView = fileChooser.getFileSystemView();
        fileChooser.setBorder(new EmptyBorder(12, 12, 11, 11));
        fileChooser.setLayout(new BorderLayout(0, 11));
        fileChooser.addPropertyChangeListener(this.filePane = new FilePane(new MetalFileChooserUIAccessor()));
        final JPanel panel = new JPanel(new BorderLayout(11, 0));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, 2));
        panel.add(panel2, "After");
        fileChooser.add(panel, "North");
        (this.lookInLabel = new JLabel(this.lookInLabelText)).setDisplayedMnemonic(this.lookInLabelMnemonic);
        panel.add(this.lookInLabel, "Before");
        (this.directoryComboBox = new JComboBox() {
            @Override
            public Dimension getPreferredSize() {
                final Dimension preferredSize = super.getPreferredSize();
                preferredSize.width = 150;
                return preferredSize;
            }
        }).putClientProperty("AccessibleDescription", this.lookInLabelText);
        this.directoryComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        this.lookInLabel.setLabelFor(this.directoryComboBox);
        this.directoryComboBoxModel = this.createDirectoryComboBoxModel(fileChooser);
        this.directoryComboBox.setModel(this.directoryComboBoxModel);
        this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
        this.directoryComboBox.setRenderer(this.createDirectoryComboBoxRenderer(fileChooser));
        this.directoryComboBox.setAlignmentX(0.0f);
        this.directoryComboBox.setAlignmentY(0.0f);
        this.directoryComboBox.setMaximumRowCount(8);
        panel.add(this.directoryComboBox, "Center");
        final JButton button = new JButton(this.getChangeToParentDirectoryAction());
        button.setText(null);
        button.setIcon(this.upFolderIcon);
        button.setToolTipText(this.upFolderToolTipText);
        button.putClientProperty("AccessibleName", this.upFolderAccessibleName);
        button.setAlignmentX(0.0f);
        button.setAlignmentY(0.5f);
        button.setMargin(MetalFileChooserUI.shrinkwrap);
        panel2.add(button);
        panel2.add(Box.createRigidArea(MetalFileChooserUI.hstrut5));
        fileSystemView.getHomeDirectory();
        final String homeFolderToolTipText = this.homeFolderToolTipText;
        JButton button2 = new JButton(this.homeFolderIcon);
        button2.setToolTipText(homeFolderToolTipText);
        button2.putClientProperty("AccessibleName", this.homeFolderAccessibleName);
        button2.setAlignmentX(0.0f);
        button2.setAlignmentY(0.5f);
        button2.setMargin(MetalFileChooserUI.shrinkwrap);
        button2.addActionListener(this.getGoHomeAction());
        panel2.add(button2);
        panel2.add(Box.createRigidArea(MetalFileChooserUI.hstrut5));
        if (!UIManager.getBoolean("FileChooser.readOnly")) {
            button2 = new JButton(this.filePane.getNewFolderAction());
            button2.setText(null);
            button2.setIcon(this.newFolderIcon);
            button2.setToolTipText(this.newFolderToolTipText);
            button2.putClientProperty("AccessibleName", this.newFolderAccessibleName);
            button2.setAlignmentX(0.0f);
            button2.setAlignmentY(0.5f);
            button2.setMargin(MetalFileChooserUI.shrinkwrap);
        }
        panel2.add(button2);
        panel2.add(Box.createRigidArea(MetalFileChooserUI.hstrut5));
        final ButtonGroup buttonGroup = new ButtonGroup();
        (this.listViewButton = new JToggleButton(this.listViewIcon)).setToolTipText(this.listViewButtonToolTipText);
        this.listViewButton.putClientProperty("AccessibleName", this.listViewButtonAccessibleName);
        this.listViewButton.setSelected(true);
        this.listViewButton.setAlignmentX(0.0f);
        this.listViewButton.setAlignmentY(0.5f);
        this.listViewButton.setMargin(MetalFileChooserUI.shrinkwrap);
        this.listViewButton.addActionListener(this.filePane.getViewTypeAction(0));
        panel2.add(this.listViewButton);
        buttonGroup.add(this.listViewButton);
        (this.detailsViewButton = new JToggleButton(this.detailsViewIcon)).setToolTipText(this.detailsViewButtonToolTipText);
        this.detailsViewButton.putClientProperty("AccessibleName", this.detailsViewButtonAccessibleName);
        this.detailsViewButton.setAlignmentX(0.0f);
        this.detailsViewButton.setAlignmentY(0.5f);
        this.detailsViewButton.setMargin(MetalFileChooserUI.shrinkwrap);
        this.detailsViewButton.addActionListener(this.filePane.getViewTypeAction(1));
        panel2.add(this.detailsViewButton);
        buttonGroup.add(this.detailsViewButton);
        this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                if ("viewType".equals(propertyChangeEvent.getPropertyName())) {
                    switch (MetalFileChooserUI.this.filePane.getViewType()) {
                        case 0: {
                            MetalFileChooserUI.this.listViewButton.setSelected(true);
                            break;
                        }
                        case 1: {
                            MetalFileChooserUI.this.detailsViewButton.setSelected(true);
                            break;
                        }
                    }
                }
            }
        });
        fileChooser.add(this.getAccessoryPanel(), "After");
        final JComponent accessory = fileChooser.getAccessory();
        if (accessory != null) {
            this.getAccessoryPanel().add(accessory);
        }
        this.filePane.setPreferredSize(MetalFileChooserUI.LIST_PREF_SIZE);
        fileChooser.add(this.filePane, "Center");
        final JPanel bottomPanel = this.getBottomPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, 1));
        fileChooser.add(bottomPanel, "South");
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, 2));
        bottomPanel.add(panel3);
        bottomPanel.add(Box.createRigidArea(MetalFileChooserUI.vstrut5));
        this.fileNameLabel = new AlignedLabel();
        this.populateFileNameLabel();
        panel3.add(this.fileNameLabel);
        panel3.add(this.fileNameTextField = new JTextField(35) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(32767, super.getPreferredSize().height);
            }
        });
        this.fileNameLabel.setLabelFor(this.fileNameTextField);
        this.fileNameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent focusEvent) {
                if (!MetalFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
                    MetalFileChooserUI.this.filePane.clearSelection();
                }
            }
        });
        if (fileChooser.isMultiSelectionEnabled()) {
            this.setFileName(this.fileNameString(fileChooser.getSelectedFiles()));
        }
        else {
            this.setFileName(this.fileNameString(fileChooser.getSelectedFile()));
        }
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BoxLayout(panel4, 2));
        bottomPanel.add(panel4);
        final AlignedLabel alignedLabel = new AlignedLabel(this.filesOfTypeLabelText);
        alignedLabel.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
        panel4.add(alignedLabel);
        fileChooser.addPropertyChangeListener(this.filterComboBoxModel = this.createFilterComboBoxModel());
        (this.filterComboBox = new JComboBox((ComboBoxModel<E>)this.filterComboBoxModel)).putClientProperty("AccessibleDescription", this.filesOfTypeLabelText);
        alignedLabel.setLabelFor(this.filterComboBox);
        this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
        panel4.add(this.filterComboBox);
        this.getButtonPanel().setLayout(new ButtonAreaLayout());
        (this.approveButton = new JButton(this.getApproveButtonText(fileChooser))).addActionListener(this.getApproveSelectionAction());
        this.approveButton.setToolTipText(this.getApproveButtonToolTipText(fileChooser));
        this.getButtonPanel().add(this.approveButton);
        (this.cancelButton = new JButton(this.cancelButtonText)).setToolTipText(this.cancelButtonToolTipText);
        this.cancelButton.addActionListener(this.getCancelSelectionAction());
        this.getButtonPanel().add(this.cancelButton);
        if (fileChooser.getControlButtonsAreShown()) {
            this.addControlButtons();
        }
        groupLabels(new AlignedLabel[] { this.fileNameLabel, alignedLabel });
    }
    
    protected JPanel getButtonPanel() {
        if (this.buttonPanel == null) {
            this.buttonPanel = new JPanel();
        }
        return this.buttonPanel;
    }
    
    protected JPanel getBottomPanel() {
        if (this.bottomPanel == null) {
            this.bottomPanel = new JPanel();
        }
        return this.bottomPanel;
    }
    
    @Override
    protected void installStrings(final JFileChooser fileChooser) {
        super.installStrings(fileChooser);
        final Locale locale = fileChooser.getLocale();
        this.lookInLabelMnemonic = this.getMnemonic("FileChooser.lookInLabelMnemonic", locale);
        this.lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", locale);
        this.saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", locale);
        this.fileNameLabelMnemonic = this.getMnemonic("FileChooser.fileNameLabelMnemonic", locale);
        this.fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", locale);
        this.folderNameLabelMnemonic = this.getMnemonic("FileChooser.folderNameLabelMnemonic", locale);
        this.folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", locale);
        this.filesOfTypeLabelMnemonic = this.getMnemonic("FileChooser.filesOfTypeLabelMnemonic", locale);
        this.filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", locale);
        this.upFolderToolTipText = UIManager.getString("FileChooser.upFolderToolTipText", locale);
        this.upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName", locale);
        this.homeFolderToolTipText = UIManager.getString("FileChooser.homeFolderToolTipText", locale);
        this.homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName", locale);
        this.newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", locale);
        this.newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", locale);
        this.listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText", locale);
        this.listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName", locale);
        this.detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText", locale);
        this.detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName", locale);
    }
    
    private Integer getMnemonic(final String s, final Locale locale) {
        return SwingUtilities2.getUIDefaultsInt(s, locale);
    }
    
    @Override
    protected void installListeners(final JFileChooser fileChooser) {
        super.installListeners(fileChooser);
        SwingUtilities.replaceUIActionMap(fileChooser, this.getActionMap());
    }
    
    protected ActionMap getActionMap() {
        return this.createActionMap();
    }
    
    protected ActionMap createActionMap() {
        final ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
        FilePane.addActionsToMap(actionMapUIResource, this.filePane.getActions());
        return actionMapUIResource;
    }
    
    protected JPanel createList(final JFileChooser fileChooser) {
        return this.filePane.createList();
    }
    
    protected JPanel createDetailsView(final JFileChooser fileChooser) {
        return this.filePane.createDetailsView();
    }
    
    @Override
    public ListSelectionListener createListSelectionListener(final JFileChooser fileChooser) {
        return super.createListSelectionListener(fileChooser);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        component.removePropertyChangeListener(this.filterComboBoxModel);
        component.removePropertyChangeListener(this.filePane);
        this.cancelButton.removeActionListener(this.getCancelSelectionAction());
        this.approveButton.removeActionListener(this.getApproveSelectionAction());
        this.fileNameTextField.removeActionListener(this.getApproveSelectionAction());
        if (this.filePane != null) {
            this.filePane.uninstallUI();
            this.filePane = null;
        }
        super.uninstallUI(component);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final int width = MetalFileChooserUI.PREF_SIZE.width;
        final Dimension preferredLayoutSize = component.getLayout().preferredLayoutSize(component);
        if (preferredLayoutSize != null) {
            return new Dimension((preferredLayoutSize.width < width) ? width : preferredLayoutSize.width, (preferredLayoutSize.height < MetalFileChooserUI.PREF_SIZE.height) ? MetalFileChooserUI.PREF_SIZE.height : preferredLayoutSize.height);
        }
        return new Dimension(width, MetalFileChooserUI.PREF_SIZE.height);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return new Dimension(MetalFileChooserUI.MIN_WIDTH, MetalFileChooserUI.MIN_HEIGHT);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    private String fileNameString(final File file) {
        if (file == null) {
            return null;
        }
        final JFileChooser fileChooser = this.getFileChooser();
        if ((fileChooser.isDirectorySelectionEnabled() && !fileChooser.isFileSelectionEnabled()) || (fileChooser.isDirectorySelectionEnabled() && fileChooser.isFileSelectionEnabled() && fileChooser.getFileSystemView().isFileSystemRoot(file))) {
            return file.getPath();
        }
        return file.getName();
    }
    
    private String fileNameString(final File[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int n = 0; array != null && n < array.length; ++n) {
            if (n > 0) {
                sb.append(" ");
            }
            if (array.length > 1) {
                sb.append("\"");
            }
            sb.append(this.fileNameString(array[n]));
            if (array.length > 1) {
                sb.append("\"");
            }
        }
        return sb.toString();
    }
    
    private void doSelectedFileChanged(final PropertyChangeEvent propertyChangeEvent) {
        final File file = (File)propertyChangeEvent.getNewValue();
        final JFileChooser fileChooser = this.getFileChooser();
        if (file != null && ((fileChooser.isFileSelectionEnabled() && !file.isDirectory()) || (file.isDirectory() && fileChooser.isDirectorySelectionEnabled()))) {
            this.setFileName(this.fileNameString(file));
        }
    }
    
    private void doSelectedFilesChanged(final PropertyChangeEvent propertyChangeEvent) {
        final File[] array = (File[])propertyChangeEvent.getNewValue();
        final JFileChooser fileChooser = this.getFileChooser();
        if (array != null && array.length > 0 && (array.length > 1 || fileChooser.isDirectorySelectionEnabled() || !array[0].isDirectory())) {
            this.setFileName(this.fileNameString(array));
        }
    }
    
    private void doDirectoryChanged(final PropertyChangeEvent propertyChangeEvent) {
        final JFileChooser fileChooser = this.getFileChooser();
        final FileSystemView fileSystemView = fileChooser.getFileSystemView();
        this.clearIconCache();
        final File currentDirectory = fileChooser.getCurrentDirectory();
        if (currentDirectory != null) {
            this.directoryComboBoxModel.addItem(currentDirectory);
            if (fileChooser.isDirectorySelectionEnabled() && !fileChooser.isFileSelectionEnabled()) {
                if (fileSystemView.isFileSystem(currentDirectory)) {
                    this.setFileName(currentDirectory.getPath());
                }
                else {
                    this.setFileName(null);
                }
            }
        }
    }
    
    private void doFilterChanged(final PropertyChangeEvent propertyChangeEvent) {
        this.clearIconCache();
    }
    
    private void doFileSelectionModeChanged(final PropertyChangeEvent propertyChangeEvent) {
        if (this.fileNameLabel != null) {
            this.populateFileNameLabel();
        }
        this.clearIconCache();
        final JFileChooser fileChooser = this.getFileChooser();
        final File currentDirectory = fileChooser.getCurrentDirectory();
        if (currentDirectory != null && fileChooser.isDirectorySelectionEnabled() && !fileChooser.isFileSelectionEnabled() && fileChooser.getFileSystemView().isFileSystem(currentDirectory)) {
            this.setFileName(currentDirectory.getPath());
        }
        else {
            this.setFileName(null);
        }
    }
    
    private void doAccessoryChanged(final PropertyChangeEvent propertyChangeEvent) {
        if (this.getAccessoryPanel() != null) {
            if (propertyChangeEvent.getOldValue() != null) {
                this.getAccessoryPanel().remove((Component)propertyChangeEvent.getOldValue());
            }
            final JComponent component = (JComponent)propertyChangeEvent.getNewValue();
            if (component != null) {
                this.getAccessoryPanel().add(component, "Center");
            }
        }
    }
    
    private void doApproveButtonTextChanged(final PropertyChangeEvent propertyChangeEvent) {
        final JFileChooser fileChooser = this.getFileChooser();
        this.approveButton.setText(this.getApproveButtonText(fileChooser));
        this.approveButton.setToolTipText(this.getApproveButtonToolTipText(fileChooser));
    }
    
    private void doDialogTypeChanged(final PropertyChangeEvent propertyChangeEvent) {
        final JFileChooser fileChooser = this.getFileChooser();
        this.approveButton.setText(this.getApproveButtonText(fileChooser));
        this.approveButton.setToolTipText(this.getApproveButtonToolTipText(fileChooser));
        if (fileChooser.getDialogType() == 1) {
            this.lookInLabel.setText(this.saveInLabelText);
        }
        else {
            this.lookInLabel.setText(this.lookInLabelText);
        }
    }
    
    private void doApproveButtonMnemonicChanged(final PropertyChangeEvent propertyChangeEvent) {
    }
    
    private void doControlButtonsChanged(final PropertyChangeEvent propertyChangeEvent) {
        if (this.getFileChooser().getControlButtonsAreShown()) {
            this.addControlButtons();
        }
        else {
            this.removeControlButtons();
        }
    }
    
    @Override
    public PropertyChangeListener createPropertyChangeListener(final JFileChooser fileChooser) {
        return new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                final String propertyName = propertyChangeEvent.getPropertyName();
                if (propertyName.equals("SelectedFileChangedProperty")) {
                    MetalFileChooserUI.this.doSelectedFileChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("SelectedFilesChangedProperty")) {
                    MetalFileChooserUI.this.doSelectedFilesChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("directoryChanged")) {
                    MetalFileChooserUI.this.doDirectoryChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("fileFilterChanged")) {
                    MetalFileChooserUI.this.doFilterChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("fileSelectionChanged")) {
                    MetalFileChooserUI.this.doFileSelectionModeChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("AccessoryChangedProperty")) {
                    MetalFileChooserUI.this.doAccessoryChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("ApproveButtonTextChangedProperty") || propertyName.equals("ApproveButtonToolTipTextChangedProperty")) {
                    MetalFileChooserUI.this.doApproveButtonTextChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("DialogTypeChangedProperty")) {
                    MetalFileChooserUI.this.doDialogTypeChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("ApproveButtonMnemonicChangedProperty")) {
                    MetalFileChooserUI.this.doApproveButtonMnemonicChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("ControlButtonsAreShownChangedProperty")) {
                    MetalFileChooserUI.this.doControlButtonsChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("componentOrientation")) {
                    final ComponentOrientation componentOrientation = (ComponentOrientation)propertyChangeEvent.getNewValue();
                    final JFileChooser fileChooser = (JFileChooser)propertyChangeEvent.getSource();
                    if (componentOrientation != propertyChangeEvent.getOldValue()) {
                        fileChooser.applyComponentOrientation(componentOrientation);
                    }
                }
                else if (propertyName == "FileChooser.useShellFolder") {
                    MetalFileChooserUI.this.doDirectoryChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("ancestor") && propertyChangeEvent.getOldValue() == null && propertyChangeEvent.getNewValue() != null) {
                    MetalFileChooserUI.this.fileNameTextField.selectAll();
                    MetalFileChooserUI.this.fileNameTextField.requestFocus();
                }
            }
        };
    }
    
    protected void removeControlButtons() {
        this.getBottomPanel().remove(this.getButtonPanel());
    }
    
    protected void addControlButtons() {
        this.getBottomPanel().add(this.getButtonPanel());
    }
    
    @Override
    public void ensureFileIsVisible(final JFileChooser fileChooser, final File file) {
        this.filePane.ensureFileIsVisible(fileChooser, file);
    }
    
    @Override
    public void rescanCurrentDirectory(final JFileChooser fileChooser) {
        this.filePane.rescanCurrentDirectory();
    }
    
    @Override
    public String getFileName() {
        if (this.fileNameTextField != null) {
            return this.fileNameTextField.getText();
        }
        return null;
    }
    
    @Override
    public void setFileName(final String text) {
        if (this.fileNameTextField != null) {
            this.fileNameTextField.setText(text);
        }
    }
    
    @Override
    protected void setDirectorySelected(final boolean directorySelected) {
        super.setDirectorySelected(directorySelected);
        final JFileChooser fileChooser = this.getFileChooser();
        if (directorySelected) {
            if (this.approveButton != null) {
                this.approveButton.setText(this.directoryOpenButtonText);
                this.approveButton.setToolTipText(this.directoryOpenButtonToolTipText);
            }
        }
        else if (this.approveButton != null) {
            this.approveButton.setText(this.getApproveButtonText(fileChooser));
            this.approveButton.setToolTipText(this.getApproveButtonToolTipText(fileChooser));
        }
    }
    
    @Override
    public String getDirectoryName() {
        return null;
    }
    
    @Override
    public void setDirectoryName(final String s) {
    }
    
    protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(final JFileChooser fileChooser) {
        return new DirectoryComboBoxRenderer();
    }
    
    protected DirectoryComboBoxModel createDirectoryComboBoxModel(final JFileChooser fileChooser) {
        return new DirectoryComboBoxModel();
    }
    
    protected FilterComboBoxRenderer createFilterComboBoxRenderer() {
        return new FilterComboBoxRenderer();
    }
    
    protected FilterComboBoxModel createFilterComboBoxModel() {
        return new FilterComboBoxModel();
    }
    
    public void valueChanged(final ListSelectionEvent listSelectionEvent) {
        final File selectedFile = this.getFileChooser().getSelectedFile();
        if (!listSelectionEvent.getValueIsAdjusting() && selectedFile != null && !this.getFileChooser().isTraversable(selectedFile)) {
            this.setFileName(this.fileNameString(selectedFile));
        }
    }
    
    @Override
    protected JButton getApproveButton(final JFileChooser fileChooser) {
        return this.approveButton;
    }
    
    private static void groupLabels(final AlignedLabel[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i].group = array;
        }
    }
    
    static {
        hstrut5 = new Dimension(5, 1);
        hstrut11 = new Dimension(11, 1);
        vstrut5 = new Dimension(1, 5);
        shrinkwrap = new Insets(0, 0, 0, 0);
        MetalFileChooserUI.PREF_WIDTH = 500;
        MetalFileChooserUI.PREF_HEIGHT = 326;
        MetalFileChooserUI.PREF_SIZE = new Dimension(MetalFileChooserUI.PREF_WIDTH, MetalFileChooserUI.PREF_HEIGHT);
        MetalFileChooserUI.MIN_WIDTH = 500;
        MetalFileChooserUI.MIN_HEIGHT = 326;
        MetalFileChooserUI.LIST_PREF_WIDTH = 405;
        MetalFileChooserUI.LIST_PREF_HEIGHT = 135;
        MetalFileChooserUI.LIST_PREF_SIZE = new Dimension(MetalFileChooserUI.LIST_PREF_WIDTH, MetalFileChooserUI.LIST_PREF_HEIGHT);
    }
    
    private class MetalFileChooserUIAccessor implements FilePane.FileChooserUIAccessor
    {
        @Override
        public JFileChooser getFileChooser() {
            return MetalFileChooserUI.this.getFileChooser();
        }
        
        @Override
        public BasicDirectoryModel getModel() {
            return MetalFileChooserUI.this.getModel();
        }
        
        @Override
        public JPanel createList() {
            return MetalFileChooserUI.this.createList(this.getFileChooser());
        }
        
        @Override
        public JPanel createDetailsView() {
            return MetalFileChooserUI.this.createDetailsView(this.getFileChooser());
        }
        
        @Override
        public boolean isDirectorySelected() {
            return BasicFileChooserUI.this.isDirectorySelected();
        }
        
        @Override
        public File getDirectory() {
            return BasicFileChooserUI.this.getDirectory();
        }
        
        @Override
        public Action getChangeToParentDirectoryAction() {
            return MetalFileChooserUI.this.getChangeToParentDirectoryAction();
        }
        
        @Override
        public Action getApproveSelectionAction() {
            return MetalFileChooserUI.this.getApproveSelectionAction();
        }
        
        @Override
        public Action getNewFolderAction() {
            return MetalFileChooserUI.this.getNewFolderAction();
        }
        
        @Override
        public MouseListener createDoubleClickListener(final JList list) {
            return BasicFileChooserUI.this.createDoubleClickListener(this.getFileChooser(), list);
        }
        
        @Override
        public ListSelectionListener createListSelectionListener() {
            return MetalFileChooserUI.this.createListSelectionListener(this.getFileChooser());
        }
    }
    
    protected class SingleClickListener extends MouseAdapter
    {
        public SingleClickListener(final JList list) {
        }
    }
    
    protected class FileRenderer extends DefaultListCellRenderer
    {
    }
    
    class DirectoryComboBoxRenderer extends DefaultListCellRenderer
    {
        IndentIcon ii;
        
        DirectoryComboBoxRenderer() {
            this.ii = new IndentIcon();
        }
        
        @Override
        public Component getListCellRendererComponent(final JList list, final Object o, final int n, final boolean b, final boolean b2) {
            super.getListCellRendererComponent(list, o, n, b, b2);
            if (o == null) {
                this.setText("");
                return this;
            }
            final File file = (File)o;
            this.setText(MetalFileChooserUI.this.getFileChooser().getName(file));
            this.ii.icon = MetalFileChooserUI.this.getFileChooser().getIcon(file);
            this.ii.depth = MetalFileChooserUI.this.directoryComboBoxModel.getDepth(n);
            this.setIcon(this.ii);
            return this;
        }
    }
    
    class IndentIcon implements Icon
    {
        Icon icon;
        int depth;
        
        IndentIcon() {
            this.icon = null;
            this.depth = 0;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (component.getComponentOrientation().isLeftToRight()) {
                this.icon.paintIcon(component, graphics, n + this.depth * 10, n2);
            }
            else {
                this.icon.paintIcon(component, graphics, n, n2);
            }
        }
        
        @Override
        public int getIconWidth() {
            return this.icon.getIconWidth() + this.depth * 10;
        }
        
        @Override
        public int getIconHeight() {
            return this.icon.getIconHeight();
        }
    }
    
    protected class DirectoryComboBoxModel extends AbstractListModel<Object> implements ComboBoxModel<Object>
    {
        Vector<File> directories;
        int[] depths;
        File selectedDirectory;
        JFileChooser chooser;
        FileSystemView fsv;
        
        public DirectoryComboBoxModel() {
            this.directories = new Vector<File>();
            this.depths = null;
            this.selectedDirectory = null;
            this.chooser = MetalFileChooserUI.this.getFileChooser();
            this.fsv = this.chooser.getFileSystemView();
            final File currentDirectory = MetalFileChooserUI.this.getFileChooser().getCurrentDirectory();
            if (currentDirectory != null) {
                this.addItem(currentDirectory);
            }
        }
        
        private void addItem(final File file) {
            if (file == null) {
                return;
            }
            final boolean usesShellFolder = FilePane.usesShellFolder(this.chooser);
            this.directories.clear();
            this.directories.addAll(Arrays.asList(usesShellFolder ? ((File[])ShellFolder.get("fileChooserComboBoxFolders")) : this.fsv.getRoots()));
            File normalizedFile;
            try {
                normalizedFile = ShellFolder.getNormalizedFile(file);
            }
            catch (final IOException ex) {
                normalizedFile = file;
            }
            try {
                File parentFile;
                final ShellFolder selectedItem = (ShellFolder)(parentFile = (usesShellFolder ? ShellFolder.getShellFolder(normalizedFile) : normalizedFile));
                final Vector<File> vector = new Vector<File>(10);
                do {
                    vector.addElement(parentFile);
                } while ((parentFile = parentFile.getParentFile()) != null);
                for (int size = vector.size(), i = 0; i < size; ++i) {
                    final File file2 = vector.get(i);
                    if (this.directories.contains(file2)) {
                        final int index = this.directories.indexOf(file2);
                        for (int j = i - 1; j >= 0; --j) {
                            this.directories.insertElementAt(vector.get(j), index + i - j);
                        }
                        break;
                    }
                }
                this.calculateDepths();
                this.setSelectedItem(selectedItem);
            }
            catch (final FileNotFoundException ex2) {
                this.calculateDepths();
            }
        }
        
        private void calculateDepths() {
            this.depths = new int[this.directories.size()];
            for (int i = 0; i < this.depths.length; ++i) {
                final File parentFile = this.directories.get(i).getParentFile();
                this.depths[i] = 0;
                if (parentFile != null) {
                    for (int j = i - 1; j >= 0; --j) {
                        if (parentFile.equals(this.directories.get(j))) {
                            this.depths[i] = this.depths[j] + 1;
                            break;
                        }
                    }
                }
            }
        }
        
        public int getDepth(final int n) {
            return (this.depths != null && n >= 0 && n < this.depths.length) ? this.depths[n] : 0;
        }
        
        @Override
        public void setSelectedItem(final Object o) {
            this.selectedDirectory = (File)o;
            this.fireContentsChanged(this, -1, -1);
        }
        
        @Override
        public Object getSelectedItem() {
            return this.selectedDirectory;
        }
        
        @Override
        public int getSize() {
            return this.directories.size();
        }
        
        @Override
        public Object getElementAt(final int n) {
            return this.directories.elementAt(n);
        }
    }
    
    public class FilterComboBoxRenderer extends DefaultListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(final JList list, final Object o, final int n, final boolean b, final boolean b2) {
            super.getListCellRendererComponent(list, o, n, b, b2);
            if (o != null && o instanceof FileFilter) {
                this.setText(((FileFilter)o).getDescription());
            }
            return this;
        }
    }
    
    protected class FilterComboBoxModel extends AbstractListModel<Object> implements ComboBoxModel<Object>, PropertyChangeListener
    {
        protected FileFilter[] filters;
        
        protected FilterComboBoxModel() {
            this.filters = MetalFileChooserUI.this.getFileChooser().getChoosableFileFilters();
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "ChoosableFileFilterChangedProperty") {
                this.filters = (FileFilter[])propertyChangeEvent.getNewValue();
                this.fireContentsChanged(this, -1, -1);
            }
            else if (propertyName == "fileFilterChanged") {
                this.fireContentsChanged(this, -1, -1);
            }
        }
        
        @Override
        public void setSelectedItem(final Object o) {
            if (o != null) {
                MetalFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)o);
                this.fireContentsChanged(this, -1, -1);
            }
        }
        
        @Override
        public Object getSelectedItem() {
            final FileFilter fileFilter = MetalFileChooserUI.this.getFileChooser().getFileFilter();
            boolean b = false;
            if (fileFilter != null) {
                final FileFilter[] filters = this.filters;
                for (int length = filters.length, i = 0; i < length; ++i) {
                    if (filters[i] == fileFilter) {
                        b = true;
                    }
                }
                if (!b) {
                    MetalFileChooserUI.this.getFileChooser().addChoosableFileFilter(fileFilter);
                }
            }
            return MetalFileChooserUI.this.getFileChooser().getFileFilter();
        }
        
        @Override
        public int getSize() {
            if (this.filters != null) {
                return this.filters.length;
            }
            return 0;
        }
        
        @Override
        public Object getElementAt(final int n) {
            if (n > this.getSize() - 1) {
                return MetalFileChooserUI.this.getFileChooser().getFileFilter();
            }
            if (this.filters != null) {
                return this.filters[n];
            }
            return null;
        }
    }
    
    protected class DirectoryComboBoxAction extends AbstractAction
    {
        protected DirectoryComboBoxAction() {
            super("DirectoryComboBoxAction");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            MetalFileChooserUI.this.directoryComboBox.hidePopup();
            final File currentDirectory = (File)MetalFileChooserUI.this.directoryComboBox.getSelectedItem();
            if (!MetalFileChooserUI.this.getFileChooser().getCurrentDirectory().equals(currentDirectory)) {
                MetalFileChooserUI.this.getFileChooser().setCurrentDirectory(currentDirectory);
            }
        }
    }
    
    private static class ButtonAreaLayout implements LayoutManager
    {
        private int hGap;
        private int topMargin;
        
        private ButtonAreaLayout() {
            this.hGap = 5;
            this.topMargin = 17;
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final Component[] components = container.getComponents();
            if (components != null && components.length > 0) {
                final int length = components.length;
                final Dimension[] array = new Dimension[length];
                final Insets insets = container.getInsets();
                final int n = insets.top + this.topMargin;
                int max = 0;
                for (int i = 0; i < length; ++i) {
                    array[i] = components[i].getPreferredSize();
                    max = Math.max(max, array[i].width);
                }
                int left;
                int n2;
                if (container.getComponentOrientation().isLeftToRight()) {
                    left = container.getSize().width - insets.left - max;
                    n2 = this.hGap + max;
                }
                else {
                    left = insets.left;
                    n2 = -(this.hGap + max);
                }
                for (int j = length - 1; j >= 0; --j) {
                    components[j].setBounds(left, n, max, array[j].height);
                    left -= n2;
                }
            }
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            if (container != null) {
                final Component[] components = container.getComponents();
                if (components != null && components.length > 0) {
                    final int length = components.length;
                    int max = 0;
                    final Insets insets = container.getInsets();
                    final int n = this.topMargin + insets.top + insets.bottom;
                    final int n2 = insets.left + insets.right;
                    int max2 = 0;
                    for (int i = 0; i < length; ++i) {
                        final Dimension preferredSize = components[i].getPreferredSize();
                        max = Math.max(max, preferredSize.height);
                        max2 = Math.max(max2, preferredSize.width);
                    }
                    return new Dimension(n2 + length * max2 + (length - 1) * this.hGap, n + max);
                }
            }
            return new Dimension(0, 0);
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return this.minimumLayoutSize(container);
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
    }
    
    private class AlignedLabel extends JLabel
    {
        private AlignedLabel[] group;
        private int maxWidth;
        
        AlignedLabel() {
            this.maxWidth = 0;
            this.setAlignmentX(0.0f);
        }
        
        AlignedLabel(final String s) {
            super(s);
            this.maxWidth = 0;
            this.setAlignmentX(0.0f);
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(this.getMaxWidth() + 11, super.getPreferredSize().height);
        }
        
        private int getMaxWidth() {
            if (this.maxWidth == 0 && this.group != null) {
                int max = 0;
                for (int i = 0; i < this.group.length; ++i) {
                    max = Math.max(this.group[i].getSuperPreferredWidth(), max);
                }
                for (int j = 0; j < this.group.length; ++j) {
                    this.group[j].maxWidth = max;
                }
            }
            return this.maxWidth;
        }
        
        private int getSuperPreferredWidth() {
            return super.getPreferredSize().width;
        }
    }
}
