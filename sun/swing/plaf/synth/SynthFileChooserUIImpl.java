package sun.swing.plaf.synth;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Arrays;
import sun.awt.shell.ShellFolder;
import java.util.Vector;
import javax.swing.AbstractListModel;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicDirectoryModel;
import java.awt.event.MouseListener;
import javax.swing.JList;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.ActionMap;
import sun.swing.SwingUtilities2;
import java.util.Locale;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JPopupMenu;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.ListCellRenderer;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import java.awt.Component;
import java.awt.Container;
import javax.swing.BoxLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.UIManager;
import java.beans.PropertyChangeEvent;
import javax.swing.JFileChooser;
import java.beans.PropertyChangeListener;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.filechooser.FileFilter;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import sun.swing.FilePane;
import javax.swing.JTextField;
import javax.swing.Action;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class SynthFileChooserUIImpl extends SynthFileChooserUI
{
    private JLabel lookInLabel;
    private JComboBox<File> directoryComboBox;
    private DirectoryComboBoxModel directoryComboBoxModel;
    private Action directoryComboBoxAction;
    private FilterComboBoxModel filterComboBoxModel;
    private JTextField fileNameTextField;
    private FilePane filePane;
    private JToggleButton listViewButton;
    private JToggleButton detailsViewButton;
    private boolean readOnly;
    private JPanel buttonPanel;
    private JPanel bottomPanel;
    private JComboBox<FileFilter> filterComboBox;
    private static final Dimension hstrut5;
    private static final Insets shrinkwrap;
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
    private final PropertyChangeListener modeListener;
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
    
    public SynthFileChooserUIImpl(final JFileChooser fileChooser) {
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
        this.modeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                if (SynthFileChooserUIImpl.this.fileNameLabel != null) {
                    SynthFileChooserUIImpl.this.populateFileNameLabel();
                }
            }
        };
    }
    
    @Override
    protected void installDefaults(final JFileChooser fileChooser) {
        super.installDefaults(fileChooser);
        this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
    }
    
    @Override
    public void installComponents(final JFileChooser fileChooser) {
        super.installComponents(fileChooser);
        this.getContext(fileChooser, 1);
        fileChooser.setLayout(new BorderLayout(0, 11));
        final JPanel panel = new JPanel(new BorderLayout(11, 0));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, 2));
        panel.add(panel2, "After");
        fileChooser.add(panel, "North");
        (this.lookInLabel = new JLabel(this.lookInLabelText)).setDisplayedMnemonic(this.lookInLabelMnemonic);
        panel.add(this.lookInLabel, "Before");
        this.directoryComboBox = new JComboBox<File>();
        this.directoryComboBox.getAccessibleContext().setAccessibleDescription(this.lookInLabelText);
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
        fileChooser.addPropertyChangeListener(this.filePane = new FilePane(new SynthFileChooserUIAccessor()));
        final JPopupMenu componentPopupMenu = this.filePane.getComponentPopupMenu();
        if (componentPopupMenu != null) {
            componentPopupMenu.insert(this.getChangeToParentDirectoryAction(), 0);
            if (File.separatorChar == '/') {
                componentPopupMenu.insert(this.getGoHomeAction(), 1);
            }
        }
        final FileSystemView fileSystemView = fileChooser.getFileSystemView();
        final JButton button = new JButton(this.getChangeToParentDirectoryAction());
        button.setText(null);
        button.setIcon(this.upFolderIcon);
        button.setToolTipText(this.upFolderToolTipText);
        button.getAccessibleContext().setAccessibleName(this.upFolderAccessibleName);
        button.setAlignmentX(0.0f);
        button.setAlignmentY(0.5f);
        button.setMargin(SynthFileChooserUIImpl.shrinkwrap);
        panel2.add(button);
        panel2.add(Box.createRigidArea(SynthFileChooserUIImpl.hstrut5));
        fileSystemView.getHomeDirectory();
        final String homeFolderToolTipText = this.homeFolderToolTipText;
        final JButton button2 = new JButton(this.homeFolderIcon);
        button2.setToolTipText(homeFolderToolTipText);
        button2.getAccessibleContext().setAccessibleName(this.homeFolderAccessibleName);
        button2.setAlignmentX(0.0f);
        button2.setAlignmentY(0.5f);
        button2.setMargin(SynthFileChooserUIImpl.shrinkwrap);
        button2.addActionListener(this.getGoHomeAction());
        panel2.add(button2);
        panel2.add(Box.createRigidArea(SynthFileChooserUIImpl.hstrut5));
        if (!this.readOnly) {
            final JButton button3 = new JButton(this.filePane.getNewFolderAction());
            button3.setText(null);
            button3.setIcon(this.newFolderIcon);
            button3.setToolTipText(this.newFolderToolTipText);
            button3.getAccessibleContext().setAccessibleName(this.newFolderAccessibleName);
            button3.setAlignmentX(0.0f);
            button3.setAlignmentY(0.5f);
            button3.setMargin(SynthFileChooserUIImpl.shrinkwrap);
            panel2.add(button3);
            panel2.add(Box.createRigidArea(SynthFileChooserUIImpl.hstrut5));
        }
        final ButtonGroup buttonGroup = new ButtonGroup();
        (this.listViewButton = new JToggleButton(this.listViewIcon)).setToolTipText(this.listViewButtonToolTipText);
        this.listViewButton.getAccessibleContext().setAccessibleName(this.listViewButtonAccessibleName);
        this.listViewButton.setSelected(true);
        this.listViewButton.setAlignmentX(0.0f);
        this.listViewButton.setAlignmentY(0.5f);
        this.listViewButton.setMargin(SynthFileChooserUIImpl.shrinkwrap);
        this.listViewButton.addActionListener(this.filePane.getViewTypeAction(0));
        panel2.add(this.listViewButton);
        buttonGroup.add(this.listViewButton);
        (this.detailsViewButton = new JToggleButton(this.detailsViewIcon)).setToolTipText(this.detailsViewButtonToolTipText);
        this.detailsViewButton.getAccessibleContext().setAccessibleName(this.detailsViewButtonAccessibleName);
        this.detailsViewButton.setAlignmentX(0.0f);
        this.detailsViewButton.setAlignmentY(0.5f);
        this.detailsViewButton.setMargin(SynthFileChooserUIImpl.shrinkwrap);
        this.detailsViewButton.addActionListener(this.filePane.getViewTypeAction(1));
        panel2.add(this.detailsViewButton);
        buttonGroup.add(this.detailsViewButton);
        this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                if ("viewType".equals(propertyChangeEvent.getPropertyName())) {
                    switch (SynthFileChooserUIImpl.this.filePane.getViewType()) {
                        case 0: {
                            SynthFileChooserUIImpl.this.listViewButton.setSelected(true);
                            break;
                        }
                        case 1: {
                            SynthFileChooserUIImpl.this.detailsViewButton.setSelected(true);
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
        this.filePane.setPreferredSize(SynthFileChooserUIImpl.LIST_PREF_SIZE);
        fileChooser.add(this.filePane, "Center");
        (this.bottomPanel = new JPanel()).setLayout(new BoxLayout(this.bottomPanel, 1));
        fileChooser.add(this.bottomPanel, "South");
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, 2));
        this.bottomPanel.add(panel3);
        this.bottomPanel.add(Box.createRigidArea(new Dimension(1, 5)));
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
                if (!SynthFileChooserUIImpl.this.getFileChooser().isMultiSelectionEnabled()) {
                    SynthFileChooserUIImpl.this.filePane.clearSelection();
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
        this.bottomPanel.add(panel4);
        final AlignedLabel alignedLabel = new AlignedLabel(this.filesOfTypeLabelText);
        alignedLabel.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
        panel4.add(alignedLabel);
        fileChooser.addPropertyChangeListener(this.filterComboBoxModel = this.createFilterComboBoxModel());
        this.filterComboBox = new JComboBox<FileFilter>(this.filterComboBoxModel);
        this.filterComboBox.getAccessibleContext().setAccessibleDescription(this.filesOfTypeLabelText);
        alignedLabel.setLabelFor(this.filterComboBox);
        this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
        panel4.add(this.filterComboBox);
        (this.buttonPanel = new JPanel()).setLayout(new ButtonAreaLayout());
        this.buttonPanel.add(this.getApproveButton(fileChooser));
        this.buttonPanel.add(this.getCancelButton(fileChooser));
        if (fileChooser.getControlButtonsAreShown()) {
            this.addControlButtons();
        }
        groupLabels(new AlignedLabel[] { this.fileNameLabel, alignedLabel });
    }
    
    @Override
    protected void installListeners(final JFileChooser fileChooser) {
        super.installListeners(fileChooser);
        fileChooser.addPropertyChangeListener("fileSelectionChanged", this.modeListener);
    }
    
    @Override
    protected void uninstallListeners(final JFileChooser fileChooser) {
        fileChooser.removePropertyChangeListener("fileSelectionChanged", this.modeListener);
        super.uninstallListeners(fileChooser);
    }
    
    private String fileNameString(final File file) {
        if (file == null) {
            return null;
        }
        final JFileChooser fileChooser = this.getFileChooser();
        if (fileChooser.isDirectorySelectionEnabled() && !fileChooser.isFileSelectionEnabled()) {
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
    
    @Override
    public void uninstallUI(final JComponent component) {
        component.removePropertyChangeListener(this.filterComboBoxModel);
        component.removePropertyChangeListener(this.filePane);
        if (this.filePane != null) {
            this.filePane.uninstallUI();
            this.filePane = null;
        }
        super.uninstallUI(component);
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
    
    private int getMnemonic(final String s, final Locale locale) {
        return SwingUtilities2.getUIDefaultsInt(s, locale);
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
    public void rescanCurrentDirectory(final JFileChooser fileChooser) {
        this.filePane.rescanCurrentDirectory();
    }
    
    @Override
    protected void doSelectedFileChanged(final PropertyChangeEvent propertyChangeEvent) {
        super.doSelectedFileChanged(propertyChangeEvent);
        final File file = (File)propertyChangeEvent.getNewValue();
        final JFileChooser fileChooser = this.getFileChooser();
        if (file != null && ((fileChooser.isFileSelectionEnabled() && !file.isDirectory()) || (file.isDirectory() && fileChooser.isDirectorySelectionEnabled()))) {
            this.setFileName(this.fileNameString(file));
        }
    }
    
    @Override
    protected void doSelectedFilesChanged(final PropertyChangeEvent propertyChangeEvent) {
        super.doSelectedFilesChanged(propertyChangeEvent);
        final File[] array = (File[])propertyChangeEvent.getNewValue();
        final JFileChooser fileChooser = this.getFileChooser();
        if (array != null && array.length > 0 && (array.length > 1 || fileChooser.isDirectorySelectionEnabled() || !array[0].isDirectory())) {
            this.setFileName(this.fileNameString(array));
        }
    }
    
    @Override
    protected void doDirectoryChanged(final PropertyChangeEvent propertyChangeEvent) {
        super.doDirectoryChanged(propertyChangeEvent);
        final JFileChooser fileChooser = this.getFileChooser();
        final FileSystemView fileSystemView = fileChooser.getFileSystemView();
        final File currentDirectory = fileChooser.getCurrentDirectory();
        if (!this.readOnly && currentDirectory != null) {
            this.getNewFolderAction().setEnabled(this.filePane.canWrite(currentDirectory));
        }
        if (currentDirectory != null) {
            final JComponent directoryComboBox = this.getDirectoryComboBox();
            if (directoryComboBox instanceof JComboBox) {
                final ComboBoxModel model = ((JComboBox)directoryComboBox).getModel();
                if (model instanceof DirectoryComboBoxModel) {
                    ((DirectoryComboBoxModel)model).addItem(currentDirectory);
                }
            }
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
    
    @Override
    protected void doFileSelectionModeChanged(final PropertyChangeEvent propertyChangeEvent) {
        super.doFileSelectionModeChanged(propertyChangeEvent);
        final JFileChooser fileChooser = this.getFileChooser();
        final File currentDirectory = fileChooser.getCurrentDirectory();
        if (currentDirectory != null && fileChooser.isDirectorySelectionEnabled() && !fileChooser.isFileSelectionEnabled() && fileChooser.getFileSystemView().isFileSystem(currentDirectory)) {
            this.setFileName(currentDirectory.getPath());
        }
        else {
            this.setFileName(null);
        }
    }
    
    @Override
    protected void doAccessoryChanged(final PropertyChangeEvent propertyChangeEvent) {
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
    
    @Override
    protected void doControlButtonsChanged(final PropertyChangeEvent propertyChangeEvent) {
        super.doControlButtonsChanged(propertyChangeEvent);
        if (this.getFileChooser().getControlButtonsAreShown()) {
            this.addControlButtons();
        }
        else {
            this.removeControlButtons();
        }
    }
    
    protected void addControlButtons() {
        if (this.bottomPanel != null) {
            this.bottomPanel.add(this.buttonPanel);
        }
    }
    
    protected void removeControlButtons() {
        if (this.bottomPanel != null) {
            this.bottomPanel.remove(this.buttonPanel);
        }
    }
    
    @Override
    protected ActionMap createActionMap() {
        final ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
        FilePane.addActionsToMap(actionMapUIResource, this.filePane.getActions());
        actionMapUIResource.put("fileNameCompletion", this.getFileNameCompletionAction());
        return actionMapUIResource;
    }
    
    protected JComponent getDirectoryComboBox() {
        return this.directoryComboBox;
    }
    
    protected Action getDirectoryComboBoxAction() {
        return this.directoryComboBoxAction;
    }
    
    protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(final JFileChooser fileChooser) {
        return new DirectoryComboBoxRenderer((ListCellRenderer)this.directoryComboBox.getRenderer());
    }
    
    protected DirectoryComboBoxModel createDirectoryComboBoxModel(final JFileChooser fileChooser) {
        return new DirectoryComboBoxModel();
    }
    
    protected FilterComboBoxRenderer createFilterComboBoxRenderer() {
        return new FilterComboBoxRenderer((ListCellRenderer)this.filterComboBox.getRenderer());
    }
    
    protected FilterComboBoxModel createFilterComboBoxModel() {
        return new FilterComboBoxModel();
    }
    
    private static void groupLabels(final AlignedLabel[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i].group = array;
        }
    }
    
    static {
        hstrut5 = new Dimension(5, 1);
        shrinkwrap = new Insets(0, 0, 0, 0);
        SynthFileChooserUIImpl.LIST_PREF_SIZE = new Dimension(405, 135);
    }
    
    private class SynthFileChooserUIAccessor implements FilePane.FileChooserUIAccessor
    {
        @Override
        public JFileChooser getFileChooser() {
            return SynthFileChooserUIImpl.this.getFileChooser();
        }
        
        @Override
        public BasicDirectoryModel getModel() {
            return SynthFileChooserUIImpl.this.getModel();
        }
        
        @Override
        public JPanel createList() {
            return null;
        }
        
        @Override
        public JPanel createDetailsView() {
            return null;
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
            return SynthFileChooserUIImpl.this.getChangeToParentDirectoryAction();
        }
        
        @Override
        public Action getApproveSelectionAction() {
            return SynthFileChooserUIImpl.this.getApproveSelectionAction();
        }
        
        @Override
        public Action getNewFolderAction() {
            return SynthFileChooserUIImpl.this.getNewFolderAction();
        }
        
        @Override
        public MouseListener createDoubleClickListener(final JList list) {
            return BasicFileChooserUI.this.createDoubleClickListener(this.getFileChooser(), list);
        }
        
        @Override
        public ListSelectionListener createListSelectionListener() {
            return SynthFileChooserUIImpl.this.createListSelectionListener(this.getFileChooser());
        }
    }
    
    private class DirectoryComboBoxRenderer implements ListCellRenderer<File>
    {
        private ListCellRenderer<? super File> delegate;
        IndentIcon ii;
        
        private DirectoryComboBoxRenderer(final ListCellRenderer<? super File> delegate) {
            this.ii = new IndentIcon();
            this.delegate = delegate;
        }
        
        @Override
        public Component getListCellRendererComponent(final JList<? extends File> list, final File file, final int n, final boolean b, final boolean b2) {
            final Component listCellRendererComponent = this.delegate.getListCellRendererComponent(list, file, n, b, b2);
            assert listCellRendererComponent instanceof JLabel;
            final JLabel label = (JLabel)listCellRendererComponent;
            if (file == null) {
                label.setText("");
                return label;
            }
            label.setText(SynthFileChooserUIImpl.this.getFileChooser().getName(file));
            this.ii.icon = SynthFileChooserUIImpl.this.getFileChooser().getIcon(file);
            this.ii.depth = SynthFileChooserUIImpl.this.directoryComboBoxModel.getDepth(n);
            label.setIcon(this.ii);
            return label;
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
            if (this.icon != null) {
                if (component.getComponentOrientation().isLeftToRight()) {
                    this.icon.paintIcon(component, graphics, n + this.depth * 10, n2);
                }
                else {
                    this.icon.paintIcon(component, graphics, n, n2);
                }
            }
        }
        
        @Override
        public int getIconWidth() {
            return ((this.icon != null) ? this.icon.getIconWidth() : 0) + this.depth * 10;
        }
        
        @Override
        public int getIconHeight() {
            return (this.icon != null) ? this.icon.getIconHeight() : 0;
        }
    }
    
    protected class DirectoryComboBoxModel extends AbstractListModel<File> implements ComboBoxModel<File>
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
            this.chooser = SynthFileChooserUIImpl.this.getFileChooser();
            this.fsv = this.chooser.getFileSystemView();
            final File currentDirectory = SynthFileChooserUIImpl.this.getFileChooser().getCurrentDirectory();
            if (currentDirectory != null) {
                this.addItem(currentDirectory);
            }
        }
        
        public void addItem(final File file) {
            if (file == null) {
                return;
            }
            final boolean usesShellFolder = FilePane.usesShellFolder(this.chooser);
            final int size = this.directories.size();
            this.directories.clear();
            if (size > 0) {
                this.fireIntervalRemoved(this, 0, size);
            }
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
                for (int size2 = vector.size(), i = 0; i < size2; ++i) {
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
        public File getElementAt(final int n) {
            return this.directories.elementAt(n);
        }
    }
    
    protected class DirectoryComboBoxAction extends AbstractAction
    {
        protected DirectoryComboBoxAction() {
            super("DirectoryComboBoxAction");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            SynthFileChooserUIImpl.this.directoryComboBox.hidePopup();
            final JComponent directoryComboBox = SynthFileChooserUIImpl.this.getDirectoryComboBox();
            if (directoryComboBox instanceof JComboBox) {
                SynthFileChooserUIImpl.this.getFileChooser().setCurrentDirectory((File)((JComboBox)directoryComboBox).getSelectedItem());
            }
        }
    }
    
    public class FilterComboBoxRenderer implements ListCellRenderer<FileFilter>
    {
        private ListCellRenderer<? super FileFilter> delegate;
        
        private FilterComboBoxRenderer(final ListCellRenderer<? super FileFilter> delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public Component getListCellRendererComponent(final JList<? extends FileFilter> list, final FileFilter fileFilter, final int n, final boolean b, final boolean b2) {
            final Component listCellRendererComponent = this.delegate.getListCellRendererComponent(list, fileFilter, n, b, b2);
            String description = null;
            if (fileFilter != null) {
                description = fileFilter.getDescription();
            }
            assert listCellRendererComponent instanceof JLabel;
            if (description != null) {
                ((JLabel)listCellRendererComponent).setText(description);
            }
            return listCellRendererComponent;
        }
    }
    
    protected class FilterComboBoxModel extends AbstractListModel<FileFilter> implements ComboBoxModel<FileFilter>, PropertyChangeListener
    {
        protected FileFilter[] filters;
        
        protected FilterComboBoxModel() {
            this.filters = SynthFileChooserUIImpl.this.getFileChooser().getChoosableFileFilters();
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
                SynthFileChooserUIImpl.this.getFileChooser().setFileFilter((FileFilter)o);
                this.fireContentsChanged(this, -1, -1);
            }
        }
        
        @Override
        public Object getSelectedItem() {
            final FileFilter fileFilter = SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
            boolean b = false;
            if (fileFilter != null) {
                final FileFilter[] filters = this.filters;
                for (int length = filters.length, i = 0; i < length; ++i) {
                    if (filters[i] == fileFilter) {
                        b = true;
                    }
                }
                if (!b) {
                    SynthFileChooserUIImpl.this.getFileChooser().addChoosableFileFilter(fileFilter);
                }
            }
            return SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
        }
        
        @Override
        public int getSize() {
            if (this.filters != null) {
                return this.filters.length;
            }
            return 0;
        }
        
        @Override
        public FileFilter getElementAt(final int n) {
            if (n > this.getSize() - 1) {
                return SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
            }
            if (this.filters != null) {
                return this.filters[n];
            }
            return null;
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
