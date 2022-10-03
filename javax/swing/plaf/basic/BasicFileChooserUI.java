package javax.swing.plaf.basic;

import java.awt.datatransfer.DataFlavor;
import javax.swing.JTable;
import java.awt.datatransfer.Transferable;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.awt.Toolkit;
import java.util.regex.PatternSyntaxException;
import java.util.Arrays;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import java.awt.event.MouseAdapter;
import javax.swing.filechooser.FileSystemView;
import java.util.ArrayList;
import javax.swing.event.ListSelectionEvent;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import javax.swing.filechooser.FileView;
import sun.swing.SwingUtilities2;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseListener;
import javax.swing.JList;
import javax.swing.JButton;
import java.util.Locale;
import javax.swing.LookAndFeel;
import javax.swing.plaf.UIResource;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.ActionMap;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.plaf.FileChooserUI;

public class BasicFileChooserUI extends FileChooserUI
{
    protected Icon directoryIcon;
    protected Icon fileIcon;
    protected Icon computerIcon;
    protected Icon hardDriveIcon;
    protected Icon floppyDriveIcon;
    protected Icon newFolderIcon;
    protected Icon upFolderIcon;
    protected Icon homeFolderIcon;
    protected Icon listViewIcon;
    protected Icon detailsViewIcon;
    protected Icon viewMenuIcon;
    protected int saveButtonMnemonic;
    protected int openButtonMnemonic;
    protected int cancelButtonMnemonic;
    protected int updateButtonMnemonic;
    protected int helpButtonMnemonic;
    protected int directoryOpenButtonMnemonic;
    protected String saveButtonText;
    protected String openButtonText;
    protected String cancelButtonText;
    protected String updateButtonText;
    protected String helpButtonText;
    protected String directoryOpenButtonText;
    private String openDialogTitleText;
    private String saveDialogTitleText;
    protected String saveButtonToolTipText;
    protected String openButtonToolTipText;
    protected String cancelButtonToolTipText;
    protected String updateButtonToolTipText;
    protected String helpButtonToolTipText;
    protected String directoryOpenButtonToolTipText;
    private Action approveSelectionAction;
    private Action cancelSelectionAction;
    private Action updateAction;
    private Action newFolderAction;
    private Action goHomeAction;
    private Action changeToParentDirectoryAction;
    private String newFolderErrorSeparator;
    private String newFolderErrorText;
    private String newFolderParentDoesntExistTitleText;
    private String newFolderParentDoesntExistText;
    private String fileDescriptionText;
    private String directoryDescriptionText;
    private JFileChooser filechooser;
    private boolean directorySelected;
    private File directory;
    private PropertyChangeListener propertyChangeListener;
    private AcceptAllFileFilter acceptAllFileFilter;
    private FileFilter actualFileFilter;
    private GlobFilter globFilter;
    private BasicDirectoryModel model;
    private BasicFileView fileView;
    private boolean usesSingleFilePane;
    private boolean readOnly;
    private JPanel accessoryPanel;
    private Handler handler;
    private static final TransferHandler defaultTransferHandler;
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicFileChooserUI((JFileChooser)component);
    }
    
    public BasicFileChooserUI(final JFileChooser fileChooser) {
        this.directoryIcon = null;
        this.fileIcon = null;
        this.computerIcon = null;
        this.hardDriveIcon = null;
        this.floppyDriveIcon = null;
        this.newFolderIcon = null;
        this.upFolderIcon = null;
        this.homeFolderIcon = null;
        this.listViewIcon = null;
        this.detailsViewIcon = null;
        this.viewMenuIcon = null;
        this.saveButtonMnemonic = 0;
        this.openButtonMnemonic = 0;
        this.cancelButtonMnemonic = 0;
        this.updateButtonMnemonic = 0;
        this.helpButtonMnemonic = 0;
        this.directoryOpenButtonMnemonic = 0;
        this.saveButtonText = null;
        this.openButtonText = null;
        this.cancelButtonText = null;
        this.updateButtonText = null;
        this.helpButtonText = null;
        this.directoryOpenButtonText = null;
        this.openDialogTitleText = null;
        this.saveDialogTitleText = null;
        this.saveButtonToolTipText = null;
        this.openButtonToolTipText = null;
        this.cancelButtonToolTipText = null;
        this.updateButtonToolTipText = null;
        this.helpButtonToolTipText = null;
        this.directoryOpenButtonToolTipText = null;
        this.approveSelectionAction = new ApproveSelectionAction();
        this.cancelSelectionAction = new CancelSelectionAction();
        this.updateAction = new UpdateAction();
        this.goHomeAction = new GoHomeAction();
        this.changeToParentDirectoryAction = new ChangeToParentDirectoryAction();
        this.newFolderErrorSeparator = null;
        this.newFolderErrorText = null;
        this.newFolderParentDoesntExistTitleText = null;
        this.newFolderParentDoesntExistText = null;
        this.fileDescriptionText = null;
        this.directoryDescriptionText = null;
        this.filechooser = null;
        this.directorySelected = false;
        this.directory = null;
        this.propertyChangeListener = null;
        this.acceptAllFileFilter = new AcceptAllFileFilter();
        this.actualFileFilter = null;
        this.globFilter = null;
        this.model = null;
        this.fileView = new BasicFileView();
        this.accessoryPanel = null;
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.accessoryPanel = new JPanel(new BorderLayout());
        this.filechooser = (JFileChooser)component;
        this.createModel();
        this.clearIconCache();
        this.installDefaults(this.filechooser);
        this.installComponents(this.filechooser);
        this.installListeners(this.filechooser);
        this.filechooser.applyComponentOrientation(this.filechooser.getComponentOrientation());
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallListeners(this.filechooser);
        this.uninstallComponents(this.filechooser);
        this.uninstallDefaults(this.filechooser);
        if (this.accessoryPanel != null) {
            this.accessoryPanel.removeAll();
        }
        this.accessoryPanel = null;
        this.getFileChooser().removeAll();
        this.handler = null;
    }
    
    public void installComponents(final JFileChooser fileChooser) {
    }
    
    public void uninstallComponents(final JFileChooser fileChooser) {
    }
    
    protected void installListeners(final JFileChooser fileChooser) {
        this.propertyChangeListener = this.createPropertyChangeListener(fileChooser);
        if (this.propertyChangeListener != null) {
            fileChooser.addPropertyChangeListener(this.propertyChangeListener);
        }
        fileChooser.addPropertyChangeListener(this.getModel());
        SwingUtilities.replaceUIInputMap(fileChooser, 1, this.getInputMap(1));
        SwingUtilities.replaceUIActionMap(fileChooser, this.getActionMap());
    }
    
    InputMap getInputMap(final int n) {
        if (n == 1) {
            return (InputMap)DefaultLookup.get(this.getFileChooser(), this, "FileChooser.ancestorInputMap");
        }
        return null;
    }
    
    ActionMap getActionMap() {
        return this.createActionMap();
    }
    
    ActionMap createActionMap() {
        final ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
        final UIAction uiAction = new UIAction("refresh") {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                BasicFileChooserUI.this.getFileChooser().rescanCurrentDirectory();
            }
        };
        actionMapUIResource.put("approveSelection", this.getApproveSelectionAction());
        actionMapUIResource.put("cancelSelection", this.getCancelSelectionAction());
        actionMapUIResource.put("refresh", uiAction);
        actionMapUIResource.put("Go Up", this.getChangeToParentDirectoryAction());
        return actionMapUIResource;
    }
    
    protected void uninstallListeners(final JFileChooser fileChooser) {
        if (this.propertyChangeListener != null) {
            fileChooser.removePropertyChangeListener(this.propertyChangeListener);
        }
        fileChooser.removePropertyChangeListener(this.getModel());
        SwingUtilities.replaceUIInputMap(fileChooser, 1, null);
        SwingUtilities.replaceUIActionMap(fileChooser, null);
    }
    
    protected void installDefaults(final JFileChooser fileChooser) {
        this.installIcons(fileChooser);
        this.installStrings(fileChooser);
        this.usesSingleFilePane = UIManager.getBoolean("FileChooser.usesSingleFilePane");
        this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
        final TransferHandler transferHandler = fileChooser.getTransferHandler();
        if (transferHandler == null || transferHandler instanceof UIResource) {
            fileChooser.setTransferHandler(BasicFileChooserUI.defaultTransferHandler);
        }
        LookAndFeel.installProperty(fileChooser, "opaque", Boolean.FALSE);
    }
    
    protected void installIcons(final JFileChooser fileChooser) {
        this.directoryIcon = UIManager.getIcon("FileView.directoryIcon");
        this.fileIcon = UIManager.getIcon("FileView.fileIcon");
        this.computerIcon = UIManager.getIcon("FileView.computerIcon");
        this.hardDriveIcon = UIManager.getIcon("FileView.hardDriveIcon");
        this.floppyDriveIcon = UIManager.getIcon("FileView.floppyDriveIcon");
        this.newFolderIcon = UIManager.getIcon("FileChooser.newFolderIcon");
        this.upFolderIcon = UIManager.getIcon("FileChooser.upFolderIcon");
        this.homeFolderIcon = UIManager.getIcon("FileChooser.homeFolderIcon");
        this.detailsViewIcon = UIManager.getIcon("FileChooser.detailsViewIcon");
        this.listViewIcon = UIManager.getIcon("FileChooser.listViewIcon");
        this.viewMenuIcon = UIManager.getIcon("FileChooser.viewMenuIcon");
    }
    
    protected void installStrings(final JFileChooser fileChooser) {
        final Locale locale = fileChooser.getLocale();
        this.newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText", locale);
        this.newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator", locale);
        this.newFolderParentDoesntExistTitleText = UIManager.getString("FileChooser.newFolderParentDoesntExistTitleText", locale);
        this.newFolderParentDoesntExistText = UIManager.getString("FileChooser.newFolderParentDoesntExistText", locale);
        this.fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText", locale);
        this.directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText", locale);
        this.saveButtonText = UIManager.getString("FileChooser.saveButtonText", locale);
        this.openButtonText = UIManager.getString("FileChooser.openButtonText", locale);
        this.saveDialogTitleText = UIManager.getString("FileChooser.saveDialogTitleText", locale);
        this.openDialogTitleText = UIManager.getString("FileChooser.openDialogTitleText", locale);
        this.cancelButtonText = UIManager.getString("FileChooser.cancelButtonText", locale);
        this.updateButtonText = UIManager.getString("FileChooser.updateButtonText", locale);
        this.helpButtonText = UIManager.getString("FileChooser.helpButtonText", locale);
        this.directoryOpenButtonText = UIManager.getString("FileChooser.directoryOpenButtonText", locale);
        this.saveButtonMnemonic = this.getMnemonic("FileChooser.saveButtonMnemonic", locale);
        this.openButtonMnemonic = this.getMnemonic("FileChooser.openButtonMnemonic", locale);
        this.cancelButtonMnemonic = this.getMnemonic("FileChooser.cancelButtonMnemonic", locale);
        this.updateButtonMnemonic = this.getMnemonic("FileChooser.updateButtonMnemonic", locale);
        this.helpButtonMnemonic = this.getMnemonic("FileChooser.helpButtonMnemonic", locale);
        this.directoryOpenButtonMnemonic = this.getMnemonic("FileChooser.directoryOpenButtonMnemonic", locale);
        this.saveButtonToolTipText = UIManager.getString("FileChooser.saveButtonToolTipText", locale);
        this.openButtonToolTipText = UIManager.getString("FileChooser.openButtonToolTipText", locale);
        this.cancelButtonToolTipText = UIManager.getString("FileChooser.cancelButtonToolTipText", locale);
        this.updateButtonToolTipText = UIManager.getString("FileChooser.updateButtonToolTipText", locale);
        this.helpButtonToolTipText = UIManager.getString("FileChooser.helpButtonToolTipText", locale);
        this.directoryOpenButtonToolTipText = UIManager.getString("FileChooser.directoryOpenButtonToolTipText", locale);
    }
    
    protected void uninstallDefaults(final JFileChooser fileChooser) {
        this.uninstallIcons(fileChooser);
        this.uninstallStrings(fileChooser);
        if (fileChooser.getTransferHandler() instanceof UIResource) {
            fileChooser.setTransferHandler(null);
        }
    }
    
    protected void uninstallIcons(final JFileChooser fileChooser) {
        this.directoryIcon = null;
        this.fileIcon = null;
        this.computerIcon = null;
        this.hardDriveIcon = null;
        this.floppyDriveIcon = null;
        this.newFolderIcon = null;
        this.upFolderIcon = null;
        this.homeFolderIcon = null;
        this.detailsViewIcon = null;
        this.listViewIcon = null;
        this.viewMenuIcon = null;
    }
    
    protected void uninstallStrings(final JFileChooser fileChooser) {
        this.saveButtonText = null;
        this.openButtonText = null;
        this.cancelButtonText = null;
        this.updateButtonText = null;
        this.helpButtonText = null;
        this.directoryOpenButtonText = null;
        this.saveButtonToolTipText = null;
        this.openButtonToolTipText = null;
        this.cancelButtonToolTipText = null;
        this.updateButtonToolTipText = null;
        this.helpButtonToolTipText = null;
        this.directoryOpenButtonToolTipText = null;
    }
    
    protected void createModel() {
        if (this.model != null) {
            this.model.invalidateFileCache();
        }
        this.model = new BasicDirectoryModel(this.getFileChooser());
    }
    
    public BasicDirectoryModel getModel() {
        return this.model;
    }
    
    public PropertyChangeListener createPropertyChangeListener(final JFileChooser fileChooser) {
        return null;
    }
    
    public String getFileName() {
        return null;
    }
    
    public String getDirectoryName() {
        return null;
    }
    
    public void setFileName(final String s) {
    }
    
    public void setDirectoryName(final String s) {
    }
    
    @Override
    public void rescanCurrentDirectory(final JFileChooser fileChooser) {
    }
    
    @Override
    public void ensureFileIsVisible(final JFileChooser fileChooser, final File file) {
    }
    
    public JFileChooser getFileChooser() {
        return this.filechooser;
    }
    
    public JPanel getAccessoryPanel() {
        return this.accessoryPanel;
    }
    
    protected JButton getApproveButton(final JFileChooser fileChooser) {
        return null;
    }
    
    @Override
    public JButton getDefaultButton(final JFileChooser fileChooser) {
        return this.getApproveButton(fileChooser);
    }
    
    public String getApproveButtonToolTipText(final JFileChooser fileChooser) {
        final String approveButtonToolTipText = fileChooser.getApproveButtonToolTipText();
        if (approveButtonToolTipText != null) {
            return approveButtonToolTipText;
        }
        if (fileChooser.getDialogType() == 0) {
            return this.openButtonToolTipText;
        }
        if (fileChooser.getDialogType() == 1) {
            return this.saveButtonToolTipText;
        }
        return null;
    }
    
    public void clearIconCache() {
        this.fileView.clearIconCache();
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected MouseListener createDoubleClickListener(final JFileChooser fileChooser, final JList list) {
        return new Handler(list);
    }
    
    public ListSelectionListener createListSelectionListener(final JFileChooser fileChooser) {
        return this.getHandler();
    }
    
    protected boolean isDirectorySelected() {
        return this.directorySelected;
    }
    
    protected void setDirectorySelected(final boolean directorySelected) {
        this.directorySelected = directorySelected;
    }
    
    protected File getDirectory() {
        return this.directory;
    }
    
    protected void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    private int getMnemonic(final String s, final Locale locale) {
        return SwingUtilities2.getUIDefaultsInt(s, locale);
    }
    
    @Override
    public FileFilter getAcceptAllFileFilter(final JFileChooser fileChooser) {
        return this.acceptAllFileFilter;
    }
    
    @Override
    public FileView getFileView(final JFileChooser fileChooser) {
        return this.fileView;
    }
    
    @Override
    public String getDialogTitle(final JFileChooser fileChooser) {
        final String dialogTitle = fileChooser.getDialogTitle();
        if (dialogTitle != null) {
            return dialogTitle;
        }
        if (fileChooser.getDialogType() == 0) {
            return this.openDialogTitleText;
        }
        if (fileChooser.getDialogType() == 1) {
            return this.saveDialogTitleText;
        }
        return this.getApproveButtonText(fileChooser);
    }
    
    public int getApproveButtonMnemonic(final JFileChooser fileChooser) {
        final int approveButtonMnemonic = fileChooser.getApproveButtonMnemonic();
        if (approveButtonMnemonic > 0) {
            return approveButtonMnemonic;
        }
        if (fileChooser.getDialogType() == 0) {
            return this.openButtonMnemonic;
        }
        if (fileChooser.getDialogType() == 1) {
            return this.saveButtonMnemonic;
        }
        return approveButtonMnemonic;
    }
    
    @Override
    public String getApproveButtonText(final JFileChooser fileChooser) {
        final String approveButtonText = fileChooser.getApproveButtonText();
        if (approveButtonText != null) {
            return approveButtonText;
        }
        if (fileChooser.getDialogType() == 0) {
            return this.openButtonText;
        }
        if (fileChooser.getDialogType() == 1) {
            return this.saveButtonText;
        }
        return null;
    }
    
    public Action getNewFolderAction() {
        if (this.newFolderAction == null) {
            this.newFolderAction = new NewFolderAction();
            if (this.readOnly) {
                this.newFolderAction.setEnabled(false);
            }
        }
        return this.newFolderAction;
    }
    
    public Action getGoHomeAction() {
        return this.goHomeAction;
    }
    
    public Action getChangeToParentDirectoryAction() {
        return this.changeToParentDirectoryAction;
    }
    
    public Action getApproveSelectionAction() {
        return this.approveSelectionAction;
    }
    
    public Action getCancelSelectionAction() {
        return this.cancelSelectionAction;
    }
    
    public Action getUpdateAction() {
        return this.updateAction;
    }
    
    private void resetGlobFilter() {
        if (this.actualFileFilter != null) {
            final JFileChooser fileChooser = this.getFileChooser();
            final FileFilter fileFilter = fileChooser.getFileFilter();
            if (fileFilter != null && fileFilter.equals(this.globFilter)) {
                fileChooser.setFileFilter(this.actualFileFilter);
                fileChooser.removeChoosableFileFilter(this.globFilter);
            }
            this.actualFileFilter = null;
        }
    }
    
    private static boolean isGlobPattern(final String s) {
        return (File.separatorChar == '\\' && (s.indexOf(42) >= 0 || s.indexOf(63) >= 0)) || (File.separatorChar == '/' && (s.indexOf(42) >= 0 || s.indexOf(63) >= 0 || s.indexOf(91) >= 0));
    }
    
    private void changeDirectory(File currentDirectory) {
        final JFileChooser fileChooser = this.getFileChooser();
        if (currentDirectory != null && FilePane.usesShellFolder(fileChooser)) {
            try {
                final ShellFolder shellFolder = ShellFolder.getShellFolder(currentDirectory);
                if (shellFolder.isLink()) {
                    final ShellFolder linkLocation = shellFolder.getLinkLocation();
                    if (linkLocation != null) {
                        if (!fileChooser.isTraversable(linkLocation)) {
                            return;
                        }
                        currentDirectory = linkLocation;
                    }
                    else {
                        currentDirectory = shellFolder;
                    }
                }
            }
            catch (final FileNotFoundException ex) {
                return;
            }
        }
        fileChooser.setCurrentDirectory(currentDirectory);
        if (fileChooser.getFileSelectionMode() == 2 && fileChooser.getFileSystemView().isFileSystem(currentDirectory)) {
            this.setFileName(currentDirectory.getAbsolutePath());
        }
    }
    
    static {
        defaultTransferHandler = new FileTransferHandler();
    }
    
    private class Handler implements MouseListener, ListSelectionListener
    {
        JList list;
        
        Handler() {
        }
        
        Handler(final JList list) {
            this.list = list;
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            if (this.list != null && SwingUtilities.isLeftMouseButton(mouseEvent) && mouseEvent.getClickCount() % 2 == 0) {
                final int loc2IndexFileList = SwingUtilities2.loc2IndexFileList(this.list, mouseEvent.getPoint());
                if (loc2IndexFileList >= 0) {
                    File normalizedFile = this.list.getModel().getElementAt(loc2IndexFileList);
                    try {
                        normalizedFile = ShellFolder.getNormalizedFile(normalizedFile);
                    }
                    catch (final IOException ex) {}
                    if (BasicFileChooserUI.this.getFileChooser().isTraversable(normalizedFile)) {
                        this.list.clearSelection();
                        BasicFileChooserUI.this.changeDirectory(normalizedFile);
                    }
                    else {
                        BasicFileChooserUI.this.getFileChooser().approveSelection();
                    }
                }
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            if (this.list != null) {
                final TransferHandler transferHandler = BasicFileChooserUI.this.getFileChooser().getTransferHandler();
                if (transferHandler != this.list.getTransferHandler()) {
                    this.list.setTransferHandler(transferHandler);
                }
                if (BasicFileChooserUI.this.getFileChooser().getDragEnabled() != this.list.getDragEnabled()) {
                    this.list.setDragEnabled(BasicFileChooserUI.this.getFileChooser().getDragEnabled());
                }
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                final JFileChooser fileChooser = BasicFileChooserUI.this.getFileChooser();
                final FileSystemView fileSystemView = fileChooser.getFileSystemView();
                final JList list = (JList)listSelectionEvent.getSource();
                final int fileSelectionMode = fileChooser.getFileSelectionMode();
                final boolean b = BasicFileChooserUI.this.usesSingleFilePane && fileSelectionMode == 0;
                if (fileChooser.isMultiSelectionEnabled()) {
                    File[] selectedFiles = null;
                    final Object[] selectedValues = list.getSelectedValues();
                    if (selectedValues != null) {
                        if (selectedValues.length == 1 && ((File)selectedValues[0]).isDirectory() && fileChooser.isTraversable((File)selectedValues[0]) && (b || !fileSystemView.isFileSystem((File)selectedValues[0]))) {
                            BasicFileChooserUI.this.setDirectorySelected(true);
                            BasicFileChooserUI.this.setDirectory((File)selectedValues[0]);
                        }
                        else {
                            final ArrayList list2 = new ArrayList<File>(selectedValues.length);
                            final Object[] array = selectedValues;
                            for (int length = array.length, i = 0; i < length; ++i) {
                                final File file = (File)array[i];
                                final boolean directory = file.isDirectory();
                                if ((fileChooser.isFileSelectionEnabled() && !directory) || (fileChooser.isDirectorySelectionEnabled() && fileSystemView.isFileSystem(file) && directory)) {
                                    list2.add(file);
                                }
                            }
                            if (list2.size() > 0) {
                                selectedFiles = list2.toArray(new File[list2.size()]);
                            }
                            BasicFileChooserUI.this.setDirectorySelected(false);
                        }
                    }
                    fileChooser.setSelectedFiles(selectedFiles);
                }
                else {
                    final File file2 = list.getSelectedValue();
                    if (file2 != null && file2.isDirectory() && fileChooser.isTraversable(file2) && (b || !fileSystemView.isFileSystem(file2))) {
                        BasicFileChooserUI.this.setDirectorySelected(true);
                        BasicFileChooserUI.this.setDirectory(file2);
                        if (BasicFileChooserUI.this.usesSingleFilePane) {
                            fileChooser.setSelectedFile(null);
                        }
                    }
                    else {
                        BasicFileChooserUI.this.setDirectorySelected(false);
                        if (file2 != null) {
                            fileChooser.setSelectedFile(file2);
                        }
                    }
                }
            }
        }
    }
    
    protected class DoubleClickListener extends MouseAdapter
    {
        Handler handler;
        
        public DoubleClickListener(final JList list) {
            this.handler = new Handler(list);
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            this.handler.mouseEntered(mouseEvent);
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            this.handler.mouseClicked(mouseEvent);
        }
    }
    
    protected class SelectionListener implements ListSelectionListener
    {
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
            BasicFileChooserUI.this.getHandler().valueChanged(listSelectionEvent);
        }
    }
    
    protected class NewFolderAction extends AbstractAction
    {
        protected NewFolderAction() {
            super("New Folder");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicFileChooserUI.this.readOnly) {
                return;
            }
            final JFileChooser fileChooser = BasicFileChooserUI.this.getFileChooser();
            final File currentDirectory = fileChooser.getCurrentDirectory();
            if (!currentDirectory.exists()) {
                JOptionPane.showMessageDialog(fileChooser, BasicFileChooserUI.this.newFolderParentDoesntExistText, BasicFileChooserUI.this.newFolderParentDoesntExistTitleText, 2);
                return;
            }
            try {
                final File newFolder = fileChooser.getFileSystemView().createNewFolder(currentDirectory);
                if (fileChooser.isMultiSelectionEnabled()) {
                    fileChooser.setSelectedFiles(new File[] { newFolder });
                }
                else {
                    fileChooser.setSelectedFile(newFolder);
                }
            }
            catch (final IOException ex) {
                JOptionPane.showMessageDialog(fileChooser, BasicFileChooserUI.this.newFolderErrorText + BasicFileChooserUI.this.newFolderErrorSeparator + ex, BasicFileChooserUI.this.newFolderErrorText, 0);
                return;
            }
            fileChooser.rescanCurrentDirectory();
        }
    }
    
    protected class GoHomeAction extends AbstractAction
    {
        protected GoHomeAction() {
            super("Go Home");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicFileChooserUI.this.changeDirectory(BasicFileChooserUI.this.getFileChooser().getFileSystemView().getHomeDirectory());
        }
    }
    
    protected class ChangeToParentDirectoryAction extends AbstractAction
    {
        protected ChangeToParentDirectoryAction() {
            super("Go Up");
            this.putValue("ActionCommandKey", "Go Up");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicFileChooserUI.this.getFileChooser().changeToParentDirectory();
        }
    }
    
    protected class ApproveSelectionAction extends AbstractAction
    {
        protected ApproveSelectionAction() {
            super("approveSelection");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicFileChooserUI.this.isDirectorySelected()) {
                File file = BasicFileChooserUI.this.getDirectory();
                if (file != null) {
                    try {
                        file = ShellFolder.getNormalizedFile(file);
                    }
                    catch (final IOException ex) {}
                    BasicFileChooserUI.this.changeDirectory(file);
                    return;
                }
            }
            final JFileChooser fileChooser = BasicFileChooserUI.this.getFileChooser();
            String s = BasicFileChooserUI.this.getFileName();
            final FileSystemView fileSystemView = fileChooser.getFileSystemView();
            final File currentDirectory = fileChooser.getCurrentDirectory();
            if (s != null) {
                int n;
                for (n = s.length() - 1; n >= 0 && s.charAt(n) <= ' '; --n) {}
                s = s.substring(0, n + 1);
            }
            if (s == null || s.length() == 0) {
                BasicFileChooserUI.this.resetGlobFilter();
                return;
            }
            File selectedFile = null;
            File[] array = null;
            if (File.separatorChar == '/') {
                if (s.startsWith("~/")) {
                    s = System.getProperty("user.home") + s.substring(1);
                }
                else if (s.equals("~")) {
                    s = System.getProperty("user.home");
                }
            }
            if (fileChooser.isMultiSelectionEnabled() && s.length() > 1 && s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"') {
                final ArrayList list = new ArrayList();
                final String[] split = s.substring(1, s.length() - 1).split("\" \"");
                Arrays.sort(split);
                Object[] files = null;
                int n2 = 0;
                for (final String s2 : split) {
                    Object fileObject = fileSystemView.createFileObject(s2);
                    if (!((File)fileObject).isAbsolute()) {
                        if (files == null) {
                            files = fileSystemView.getFiles(currentDirectory, false);
                            Arrays.sort(files);
                        }
                        for (int j = 0; j < files.length; ++j) {
                            final int n3 = (n2 + j) % files.length;
                            if (((File)files[n3]).getName().equals(s2)) {
                                fileObject = files[n3];
                                n2 = n3 + 1;
                                break;
                            }
                        }
                    }
                    list.add(fileObject);
                }
                if (!list.isEmpty()) {
                    array = (File[])list.toArray(new File[list.size()]);
                }
                BasicFileChooserUI.this.resetGlobFilter();
            }
            else {
                selectedFile = fileSystemView.createFileObject(s);
                if (!selectedFile.isAbsolute()) {
                    selectedFile = fileSystemView.getChild(currentDirectory, s);
                }
                final FileFilter fileFilter = fileChooser.getFileFilter();
                if (!selectedFile.exists() && isGlobPattern(s)) {
                    BasicFileChooserUI.this.changeDirectory(selectedFile.getParentFile());
                    if (BasicFileChooserUI.this.globFilter == null) {
                        BasicFileChooserUI.this.globFilter = new GlobFilter();
                    }
                    try {
                        BasicFileChooserUI.this.globFilter.setPattern(selectedFile.getName());
                        if (!(fileFilter instanceof GlobFilter)) {
                            BasicFileChooserUI.this.actualFileFilter = fileFilter;
                        }
                        fileChooser.setFileFilter(null);
                        fileChooser.setFileFilter(BasicFileChooserUI.this.globFilter);
                        return;
                    }
                    catch (final PatternSyntaxException ex2) {}
                }
                BasicFileChooserUI.this.resetGlobFilter();
                final boolean b = selectedFile != null && selectedFile.isDirectory();
                final boolean b2 = selectedFile != null && fileChooser.isTraversable(selectedFile);
                final boolean directorySelectionEnabled = fileChooser.isDirectorySelectionEnabled();
                final boolean fileSelectionEnabled = fileChooser.isFileSelectionEnabled();
                final boolean b3 = actionEvent != null && (actionEvent.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0x0;
                if (b && b2 && (b3 || !directorySelectionEnabled)) {
                    BasicFileChooserUI.this.changeDirectory(selectedFile);
                    return;
                }
                if ((b || !fileSelectionEnabled) && (!b || !directorySelectionEnabled) && (!directorySelectionEnabled || selectedFile.exists())) {
                    selectedFile = null;
                }
            }
            if (array != null || selectedFile != null) {
                if (array != null || fileChooser.isMultiSelectionEnabled()) {
                    if (array == null) {
                        array = new File[] { selectedFile };
                    }
                    fileChooser.setSelectedFiles(array);
                    fileChooser.setSelectedFiles(array);
                }
                else {
                    fileChooser.setSelectedFile(selectedFile);
                }
                fileChooser.approveSelection();
            }
            else {
                if (fileChooser.isMultiSelectionEnabled()) {
                    fileChooser.setSelectedFiles(null);
                }
                else {
                    fileChooser.setSelectedFile(null);
                }
                fileChooser.cancelSelection();
            }
        }
    }
    
    class GlobFilter extends FileFilter
    {
        Pattern pattern;
        String globPattern;
        
        public void setPattern(final String globPattern) {
            final char[] charArray = globPattern.toCharArray();
            final char[] array = new char[charArray.length * 2];
            final boolean b = File.separatorChar == '\\';
            int n = 0;
            int n2 = 0;
            this.globPattern = globPattern;
            if (b) {
                int length = charArray.length;
                if (globPattern.endsWith("*.*")) {
                    length -= 2;
                }
                for (int i = 0; i < length; ++i) {
                    switch (charArray[i]) {
                        case '*': {
                            array[n2++] = '.';
                            array[n2++] = '*';
                            break;
                        }
                        case '?': {
                            array[n2++] = '.';
                            break;
                        }
                        case '\\': {
                            array[n2++] = '\\';
                            array[n2++] = '\\';
                            break;
                        }
                        default: {
                            if ("+()^$.{}[]".indexOf(charArray[i]) >= 0) {
                                array[n2++] = '\\';
                            }
                            array[n2++] = charArray[i];
                            break;
                        }
                    }
                }
            }
            else {
                for (int j = 0; j < charArray.length; ++j) {
                    switch (charArray[j]) {
                        case '*': {
                            if (n == 0) {
                                array[n2++] = '.';
                            }
                            array[n2++] = '*';
                            break;
                        }
                        case '?': {
                            array[n2++] = ((n != 0) ? '?' : '.');
                            break;
                        }
                        case '[': {
                            n = 1;
                            array[n2++] = charArray[j];
                            if (j < charArray.length - 1) {
                                switch (charArray[j + 1]) {
                                    case '!':
                                    case '^': {
                                        array[n2++] = '^';
                                        ++j;
                                        break;
                                    }
                                    case ']': {
                                        array[n2++] = charArray[++j];
                                        break;
                                    }
                                }
                                break;
                            }
                            break;
                        }
                        case ']': {
                            array[n2++] = charArray[j];
                            n = 0;
                            break;
                        }
                        case '\\': {
                            if (j == 0 && charArray.length > 1 && charArray[1] == '~') {
                                array[n2++] = charArray[++j];
                                break;
                            }
                            array[n2++] = '\\';
                            if (j < charArray.length - 1 && "*?[]".indexOf(charArray[j + 1]) >= 0) {
                                array[n2++] = charArray[++j];
                                break;
                            }
                            array[n2++] = '\\';
                            break;
                        }
                        default: {
                            if (!Character.isLetterOrDigit(charArray[j])) {
                                array[n2++] = '\\';
                            }
                            array[n2++] = charArray[j];
                            break;
                        }
                    }
                }
            }
            this.pattern = Pattern.compile(new String(array, 0, n2), 2);
        }
        
        @Override
        public boolean accept(final File file) {
            return file != null && (file.isDirectory() || this.pattern.matcher(file.getName()).matches());
        }
        
        @Override
        public String getDescription() {
            return this.globPattern;
        }
    }
    
    protected class CancelSelectionAction extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicFileChooserUI.this.getFileChooser().cancelSelection();
        }
    }
    
    protected class UpdateAction extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JFileChooser fileChooser = BasicFileChooserUI.this.getFileChooser();
            fileChooser.setCurrentDirectory(fileChooser.getFileSystemView().createFileObject(BasicFileChooserUI.this.getDirectoryName()));
            fileChooser.rescanCurrentDirectory();
        }
    }
    
    protected class AcceptAllFileFilter extends FileFilter
    {
        public AcceptAllFileFilter() {
        }
        
        @Override
        public boolean accept(final File file) {
            return true;
        }
        
        @Override
        public String getDescription() {
            return UIManager.getString("FileChooser.acceptAllFileFilterText");
        }
    }
    
    protected class BasicFileView extends FileView
    {
        protected Hashtable<File, Icon> iconCache;
        
        public BasicFileView() {
            this.iconCache = new Hashtable<File, Icon>();
        }
        
        public void clearIconCache() {
            this.iconCache = new Hashtable<File, Icon>();
        }
        
        @Override
        public String getName(final File file) {
            String systemDisplayName = null;
            if (file != null) {
                systemDisplayName = BasicFileChooserUI.this.getFileChooser().getFileSystemView().getSystemDisplayName(file);
            }
            return systemDisplayName;
        }
        
        @Override
        public String getDescription(final File file) {
            return file.getName();
        }
        
        @Override
        public String getTypeDescription(final File file) {
            String s = BasicFileChooserUI.this.getFileChooser().getFileSystemView().getSystemTypeDescription(file);
            if (s == null) {
                if (file.isDirectory()) {
                    s = BasicFileChooserUI.this.directoryDescriptionText;
                }
                else {
                    s = BasicFileChooserUI.this.fileDescriptionText;
                }
            }
            return s;
        }
        
        public Icon getCachedIcon(final File file) {
            return this.iconCache.get(file);
        }
        
        public void cacheIcon(final File file, final Icon icon) {
            if (file == null || icon == null) {
                return;
            }
            this.iconCache.put(file, icon);
        }
        
        @Override
        public Icon getIcon(final File file) {
            final Icon cachedIcon = this.getCachedIcon(file);
            if (cachedIcon != null) {
                return cachedIcon;
            }
            Icon icon = BasicFileChooserUI.this.fileIcon;
            if (file != null) {
                final FileSystemView fileSystemView = BasicFileChooserUI.this.getFileChooser().getFileSystemView();
                if (fileSystemView.isFloppyDrive(file)) {
                    icon = BasicFileChooserUI.this.floppyDriveIcon;
                }
                else if (fileSystemView.isDrive(file)) {
                    icon = BasicFileChooserUI.this.hardDriveIcon;
                }
                else if (fileSystemView.isComputerNode(file)) {
                    icon = BasicFileChooserUI.this.computerIcon;
                }
                else if (file.isDirectory()) {
                    icon = BasicFileChooserUI.this.directoryIcon;
                }
            }
            this.cacheIcon(file, icon);
            return icon;
        }
        
        public Boolean isHidden(final File file) {
            final String name = file.getName();
            if (name != null && name.charAt(0) == '.') {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }
    
    static class FileTransferHandler extends TransferHandler implements UIResource
    {
        @Override
        protected Transferable createTransferable(final JComponent component) {
            Object[] selectedValues = null;
            if (component instanceof JList) {
                selectedValues = ((JList)component).getSelectedValues();
            }
            else if (component instanceof JTable) {
                final JTable table = (JTable)component;
                final int[] selectedRows = table.getSelectedRows();
                if (selectedRows != null) {
                    selectedValues = new Object[selectedRows.length];
                    for (int i = 0; i < selectedRows.length; ++i) {
                        selectedValues[i] = table.getValueAt(selectedRows[i], 0);
                    }
                }
            }
            if (selectedValues == null || selectedValues.length == 0) {
                return null;
            }
            final StringBuffer sb = new StringBuffer();
            final StringBuffer sb2 = new StringBuffer();
            sb2.append("<html>\n<body>\n<ul>\n");
            for (final Object o : selectedValues) {
                final String s = (o == null) ? "" : o.toString();
                sb.append(s + "\n");
                sb2.append("  <li>" + s + "\n");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb2.append("</ul>\n</body>\n</html>");
            return new FileTransferable(sb.toString(), sb2.toString(), selectedValues);
        }
        
        @Override
        public int getSourceActions(final JComponent component) {
            return 1;
        }
        
        static class FileTransferable extends BasicTransferable
        {
            Object[] fileData;
            
            FileTransferable(final String s, final String s2, final Object[] fileData) {
                super(s, s2);
                this.fileData = fileData;
            }
            
            @Override
            protected DataFlavor[] getRicherFlavors() {
                return new DataFlavor[] { DataFlavor.javaFileListFlavor };
            }
            
            @Override
            protected Object getRicherData(final DataFlavor dataFlavor) {
                if (DataFlavor.javaFileListFlavor.equals(dataFlavor)) {
                    final ArrayList list = new ArrayList();
                    final Object[] fileData = this.fileData;
                    for (int length = fileData.length, i = 0; i < length; ++i) {
                        list.add(fileData[i]);
                    }
                    return list;
                }
                return null;
            }
        }
    }
}
