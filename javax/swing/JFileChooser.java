package javax.swing;

import javax.accessibility.AccessibleRole;
import java.beans.PropertyChangeEvent;
import java.lang.ref.WeakReference;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.Window;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.accessibility.AccessibleContext;
import java.io.File;
import javax.swing.filechooser.FileSystemView;
import java.beans.PropertyChangeListener;
import javax.swing.filechooser.FileView;
import javax.swing.filechooser.FileFilter;
import java.util.Vector;
import javax.accessibility.Accessible;

public class JFileChooser extends JComponent implements Accessible
{
    private static final String uiClassID = "FileChooserUI";
    public static final int OPEN_DIALOG = 0;
    public static final int SAVE_DIALOG = 1;
    public static final int CUSTOM_DIALOG = 2;
    public static final int CANCEL_OPTION = 1;
    public static final int APPROVE_OPTION = 0;
    public static final int ERROR_OPTION = -1;
    public static final int FILES_ONLY = 0;
    public static final int DIRECTORIES_ONLY = 1;
    public static final int FILES_AND_DIRECTORIES = 2;
    public static final String CANCEL_SELECTION = "CancelSelection";
    public static final String APPROVE_SELECTION = "ApproveSelection";
    public static final String APPROVE_BUTTON_TEXT_CHANGED_PROPERTY = "ApproveButtonTextChangedProperty";
    public static final String APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY = "ApproveButtonToolTipTextChangedProperty";
    public static final String APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY = "ApproveButtonMnemonicChangedProperty";
    public static final String CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY = "ControlButtonsAreShownChangedProperty";
    public static final String DIRECTORY_CHANGED_PROPERTY = "directoryChanged";
    public static final String SELECTED_FILE_CHANGED_PROPERTY = "SelectedFileChangedProperty";
    public static final String SELECTED_FILES_CHANGED_PROPERTY = "SelectedFilesChangedProperty";
    public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";
    public static final String FILE_SYSTEM_VIEW_CHANGED_PROPERTY = "FileSystemViewChanged";
    public static final String FILE_VIEW_CHANGED_PROPERTY = "fileViewChanged";
    public static final String FILE_HIDING_CHANGED_PROPERTY = "FileHidingChanged";
    public static final String FILE_FILTER_CHANGED_PROPERTY = "fileFilterChanged";
    public static final String FILE_SELECTION_MODE_CHANGED_PROPERTY = "fileSelectionChanged";
    public static final String ACCESSORY_CHANGED_PROPERTY = "AccessoryChangedProperty";
    public static final String ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY = "acceptAllFileFilterUsedChanged";
    public static final String DIALOG_TITLE_CHANGED_PROPERTY = "DialogTitleChangedProperty";
    public static final String DIALOG_TYPE_CHANGED_PROPERTY = "DialogTypeChangedProperty";
    public static final String CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY = "ChoosableFileFilterChangedProperty";
    private String dialogTitle;
    private String approveButtonText;
    private String approveButtonToolTipText;
    private int approveButtonMnemonic;
    private Vector<FileFilter> filters;
    private JDialog dialog;
    private int dialogType;
    private int returnValue;
    private JComponent accessory;
    private FileView fileView;
    private boolean controlsShown;
    private boolean useFileHiding;
    private static final String SHOW_HIDDEN_PROP = "awt.file.showHiddenFiles";
    private transient PropertyChangeListener showFilesListener;
    private int fileSelectionMode;
    private boolean multiSelectionEnabled;
    private boolean useAcceptAllFileFilter;
    private boolean dragEnabled;
    private FileFilter fileFilter;
    private FileSystemView fileSystemView;
    private File currentDirectory;
    private File selectedFile;
    private File[] selectedFiles;
    protected AccessibleContext accessibleContext;
    
    public JFileChooser() {
        this((File)null, null);
    }
    
    public JFileChooser(final String s) {
        this(s, null);
    }
    
    public JFileChooser(final File file) {
        this(file, null);
    }
    
    public JFileChooser(final FileSystemView fileSystemView) {
        this((File)null, fileSystemView);
    }
    
    public JFileChooser(final File currentDirectory, final FileSystemView fileSystemView) {
        this.dialogTitle = null;
        this.approveButtonText = null;
        this.approveButtonToolTipText = null;
        this.approveButtonMnemonic = 0;
        this.filters = new Vector<FileFilter>(5);
        this.dialog = null;
        this.dialogType = 0;
        this.returnValue = -1;
        this.accessory = null;
        this.fileView = null;
        this.controlsShown = true;
        this.useFileHiding = true;
        this.showFilesListener = null;
        this.fileSelectionMode = 0;
        this.multiSelectionEnabled = false;
        this.useAcceptAllFileFilter = true;
        this.dragEnabled = false;
        this.fileFilter = null;
        this.fileSystemView = null;
        this.currentDirectory = null;
        this.selectedFile = null;
        this.accessibleContext = null;
        this.setup(fileSystemView);
        this.setCurrentDirectory(currentDirectory);
    }
    
    public JFileChooser(final String s, final FileSystemView fileSystemView) {
        this.dialogTitle = null;
        this.approveButtonText = null;
        this.approveButtonToolTipText = null;
        this.approveButtonMnemonic = 0;
        this.filters = new Vector<FileFilter>(5);
        this.dialog = null;
        this.dialogType = 0;
        this.returnValue = -1;
        this.accessory = null;
        this.fileView = null;
        this.controlsShown = true;
        this.useFileHiding = true;
        this.showFilesListener = null;
        this.fileSelectionMode = 0;
        this.multiSelectionEnabled = false;
        this.useAcceptAllFileFilter = true;
        this.dragEnabled = false;
        this.fileFilter = null;
        this.fileSystemView = null;
        this.currentDirectory = null;
        this.selectedFile = null;
        this.accessibleContext = null;
        this.setup(fileSystemView);
        if (s == null) {
            this.setCurrentDirectory(null);
        }
        else {
            this.setCurrentDirectory(this.fileSystemView.createFileObject(s));
        }
    }
    
    protected void setup(FileSystemView fileSystemView) {
        this.installShowFilesListener();
        this.installHierarchyListener();
        if (fileSystemView == null) {
            fileSystemView = FileSystemView.getFileSystemView();
        }
        this.setFileSystemView(fileSystemView);
        this.updateUI();
        if (this.isAcceptAllFileFilterUsed()) {
            this.setFileFilter(this.getAcceptAllFileFilter());
        }
        this.enableEvents(16L);
    }
    
    private void installHierarchyListener() {
        this.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(final HierarchyEvent hierarchyEvent) {
                if ((hierarchyEvent.getChangeFlags() & 0x1L) == 0x1L) {
                    final JFileChooser this$0 = JFileChooser.this;
                    final JRootPane rootPane = SwingUtilities.getRootPane(this$0);
                    if (rootPane != null) {
                        rootPane.setDefaultButton(this$0.getUI().getDefaultButton(this$0));
                    }
                }
            }
        });
    }
    
    private void installShowFilesListener() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        final Object desktopProperty = defaultToolkit.getDesktopProperty("awt.file.showHiddenFiles");
        if (desktopProperty instanceof Boolean) {
            this.useFileHiding = !(boolean)desktopProperty;
            defaultToolkit.addPropertyChangeListener("awt.file.showHiddenFiles", this.showFilesListener = new WeakPCL(this));
        }
    }
    
    public void setDragEnabled(final boolean dragEnabled) {
        if (dragEnabled && GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        this.dragEnabled = dragEnabled;
    }
    
    public boolean getDragEnabled() {
        return this.dragEnabled;
    }
    
    public File getSelectedFile() {
        return this.selectedFile;
    }
    
    public void setSelectedFile(final File selectedFile) {
        final File selectedFile2 = this.selectedFile;
        this.selectedFile = selectedFile;
        if (this.selectedFile != null) {
            if (selectedFile.isAbsolute() && !this.getFileSystemView().isParent(this.getCurrentDirectory(), this.selectedFile)) {
                this.setCurrentDirectory(this.selectedFile.getParentFile());
            }
            if (!this.isMultiSelectionEnabled() || this.selectedFiles == null || this.selectedFiles.length == 1) {
                this.ensureFileIsVisible(this.selectedFile);
            }
        }
        this.firePropertyChange("SelectedFileChangedProperty", selectedFile2, this.selectedFile);
    }
    
    public File[] getSelectedFiles() {
        if (this.selectedFiles == null) {
            return new File[0];
        }
        return this.selectedFiles.clone();
    }
    
    public void setSelectedFiles(File[] array) {
        final File[] selectedFiles = this.selectedFiles;
        if (array == null || array.length == 0) {
            array = null;
            this.selectedFiles = null;
            this.setSelectedFile(null);
        }
        else {
            this.selectedFiles = array.clone();
            this.setSelectedFile(this.selectedFiles[0]);
        }
        this.firePropertyChange("SelectedFilesChangedProperty", selectedFiles, array);
    }
    
    public File getCurrentDirectory() {
        return this.currentDirectory;
    }
    
    public void setCurrentDirectory(File currentDirectory) {
        final File currentDirectory2 = this.currentDirectory;
        if (currentDirectory != null && !currentDirectory.exists()) {
            currentDirectory = this.currentDirectory;
        }
        if (currentDirectory == null) {
            currentDirectory = this.getFileSystemView().getDefaultDirectory();
        }
        if (this.currentDirectory != null && this.currentDirectory.equals(currentDirectory)) {
            return;
        }
        for (File file = null; !this.isTraversable(currentDirectory) && file != currentDirectory; file = currentDirectory, currentDirectory = this.getFileSystemView().getParentDirectory(currentDirectory)) {}
        this.firePropertyChange("directoryChanged", currentDirectory2, this.currentDirectory = currentDirectory);
    }
    
    public void changeToParentDirectory() {
        this.selectedFile = null;
        this.setCurrentDirectory(this.getFileSystemView().getParentDirectory(this.getCurrentDirectory()));
    }
    
    public void rescanCurrentDirectory() {
        this.getUI().rescanCurrentDirectory(this);
    }
    
    public void ensureFileIsVisible(final File file) {
        this.getUI().ensureFileIsVisible(this, file);
    }
    
    public int showOpenDialog(final Component component) throws HeadlessException {
        this.setDialogType(0);
        return this.showDialog(component, null);
    }
    
    public int showSaveDialog(final Component component) throws HeadlessException {
        this.setDialogType(1);
        return this.showDialog(component, null);
    }
    
    public int showDialog(final Component component, final String approveButtonText) throws HeadlessException {
        if (this.dialog != null) {
            return -1;
        }
        if (approveButtonText != null) {
            this.setApproveButtonText(approveButtonText);
            this.setDialogType(2);
        }
        (this.dialog = this.createDialog(component)).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                JFileChooser.this.returnValue = 1;
            }
        });
        this.returnValue = -1;
        this.rescanCurrentDirectory();
        this.dialog.show();
        this.firePropertyChange("JFileChooserDialogIsClosingProperty", this.dialog, null);
        this.dialog.getContentPane().removeAll();
        this.dialog.dispose();
        this.dialog = null;
        return this.returnValue;
    }
    
    protected JDialog createDialog(final Component locationRelativeTo) throws HeadlessException {
        final String dialogTitle = this.getUI().getDialogTitle(this);
        this.putClientProperty("AccessibleDescription", dialogTitle);
        final Window windowForComponent = JOptionPane.getWindowForComponent(locationRelativeTo);
        JDialog dialog;
        if (windowForComponent instanceof Frame) {
            dialog = new JDialog((Frame)windowForComponent, dialogTitle, true);
        }
        else {
            dialog = new JDialog((Dialog)windowForComponent, dialogTitle, true);
        }
        dialog.setComponentOrientation(this.getComponentOrientation());
        final Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, "Center");
        if (JDialog.isDefaultLookAndFeelDecorated() && UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            dialog.getRootPane().setWindowDecorationStyle(6);
        }
        dialog.pack();
        dialog.setLocationRelativeTo(locationRelativeTo);
        return dialog;
    }
    
    public boolean getControlButtonsAreShown() {
        return this.controlsShown;
    }
    
    public void setControlButtonsAreShown(final boolean controlsShown) {
        if (this.controlsShown == controlsShown) {
            return;
        }
        this.firePropertyChange("ControlButtonsAreShownChangedProperty", this.controlsShown, this.controlsShown = controlsShown);
    }
    
    public int getDialogType() {
        return this.dialogType;
    }
    
    public void setDialogType(final int dialogType) {
        if (this.dialogType == dialogType) {
            return;
        }
        if (dialogType != 0 && dialogType != 1 && dialogType != 2) {
            throw new IllegalArgumentException("Incorrect Dialog Type: " + dialogType);
        }
        final int dialogType2 = this.dialogType;
        if ((this.dialogType = dialogType) == 0 || dialogType == 1) {
            this.setApproveButtonText(null);
        }
        this.firePropertyChange("DialogTypeChangedProperty", dialogType2, dialogType);
    }
    
    public void setDialogTitle(final String s) {
        final String dialogTitle = this.dialogTitle;
        this.dialogTitle = s;
        if (this.dialog != null) {
            this.dialog.setTitle(s);
        }
        this.firePropertyChange("DialogTitleChangedProperty", dialogTitle, s);
    }
    
    public String getDialogTitle() {
        return this.dialogTitle;
    }
    
    public void setApproveButtonToolTipText(final String approveButtonToolTipText) {
        if (this.approveButtonToolTipText == approveButtonToolTipText) {
            return;
        }
        this.firePropertyChange("ApproveButtonToolTipTextChangedProperty", this.approveButtonToolTipText, this.approveButtonToolTipText = approveButtonToolTipText);
    }
    
    public String getApproveButtonToolTipText() {
        return this.approveButtonToolTipText;
    }
    
    public int getApproveButtonMnemonic() {
        return this.approveButtonMnemonic;
    }
    
    public void setApproveButtonMnemonic(final int approveButtonMnemonic) {
        if (this.approveButtonMnemonic == approveButtonMnemonic) {
            return;
        }
        this.firePropertyChange("ApproveButtonMnemonicChangedProperty", this.approveButtonMnemonic, this.approveButtonMnemonic = approveButtonMnemonic);
    }
    
    public void setApproveButtonMnemonic(final char c) {
        int approveButtonMnemonic = c;
        if (approveButtonMnemonic >= 97 && approveButtonMnemonic <= 122) {
            approveButtonMnemonic -= 32;
        }
        this.setApproveButtonMnemonic(approveButtonMnemonic);
    }
    
    public void setApproveButtonText(final String approveButtonText) {
        if (this.approveButtonText == approveButtonText) {
            return;
        }
        this.firePropertyChange("ApproveButtonTextChangedProperty", this.approveButtonText, this.approveButtonText = approveButtonText);
    }
    
    public String getApproveButtonText() {
        return this.approveButtonText;
    }
    
    public FileFilter[] getChoosableFileFilters() {
        final FileFilter[] array = new FileFilter[this.filters.size()];
        this.filters.copyInto(array);
        return array;
    }
    
    public void addChoosableFileFilter(final FileFilter fileFilter) {
        if (fileFilter != null && !this.filters.contains(fileFilter)) {
            final FileFilter[] choosableFileFilters = this.getChoosableFileFilters();
            this.filters.addElement(fileFilter);
            this.firePropertyChange("ChoosableFileFilterChangedProperty", choosableFileFilters, this.getChoosableFileFilters());
            if (this.fileFilter == null && this.filters.size() == 1) {
                this.setFileFilter(fileFilter);
            }
        }
    }
    
    public boolean removeChoosableFileFilter(final FileFilter fileFilter) {
        final int index = this.filters.indexOf(fileFilter);
        if (index >= 0) {
            if (this.getFileFilter() == fileFilter) {
                final FileFilter acceptAllFileFilter = this.getAcceptAllFileFilter();
                if (this.isAcceptAllFileFilterUsed() && acceptAllFileFilter != fileFilter) {
                    this.setFileFilter(acceptAllFileFilter);
                }
                else if (index > 0) {
                    this.setFileFilter(this.filters.get(0));
                }
                else if (this.filters.size() > 1) {
                    this.setFileFilter(this.filters.get(1));
                }
                else {
                    this.setFileFilter(null);
                }
            }
            final FileFilter[] choosableFileFilters = this.getChoosableFileFilters();
            this.filters.removeElement(fileFilter);
            this.firePropertyChange("ChoosableFileFilterChangedProperty", choosableFileFilters, this.getChoosableFileFilters());
            return true;
        }
        return false;
    }
    
    public void resetChoosableFileFilters() {
        final FileFilter[] choosableFileFilters = this.getChoosableFileFilters();
        this.setFileFilter(null);
        this.filters.removeAllElements();
        if (this.isAcceptAllFileFilterUsed()) {
            this.addChoosableFileFilter(this.getAcceptAllFileFilter());
        }
        this.firePropertyChange("ChoosableFileFilterChangedProperty", choosableFileFilters, this.getChoosableFileFilters());
    }
    
    public FileFilter getAcceptAllFileFilter() {
        FileFilter acceptAllFileFilter = null;
        if (this.getUI() != null) {
            acceptAllFileFilter = this.getUI().getAcceptAllFileFilter(this);
        }
        return acceptAllFileFilter;
    }
    
    public boolean isAcceptAllFileFilterUsed() {
        return this.useAcceptAllFileFilter;
    }
    
    public void setAcceptAllFileFilterUsed(final boolean useAcceptAllFileFilter) {
        final boolean useAcceptAllFileFilter2 = this.useAcceptAllFileFilter;
        if (!(this.useAcceptAllFileFilter = useAcceptAllFileFilter)) {
            this.removeChoosableFileFilter(this.getAcceptAllFileFilter());
        }
        else {
            this.removeChoosableFileFilter(this.getAcceptAllFileFilter());
            this.addChoosableFileFilter(this.getAcceptAllFileFilter());
        }
        this.firePropertyChange("acceptAllFileFilterUsedChanged", useAcceptAllFileFilter2, this.useAcceptAllFileFilter);
    }
    
    public JComponent getAccessory() {
        return this.accessory;
    }
    
    public void setAccessory(final JComponent accessory) {
        this.firePropertyChange("AccessoryChangedProperty", this.accessory, this.accessory = accessory);
    }
    
    public void setFileSelectionMode(final int fileSelectionMode) {
        if (this.fileSelectionMode == fileSelectionMode) {
            return;
        }
        if (fileSelectionMode == 0 || fileSelectionMode == 1 || fileSelectionMode == 2) {
            this.firePropertyChange("fileSelectionChanged", this.fileSelectionMode, this.fileSelectionMode = fileSelectionMode);
            return;
        }
        throw new IllegalArgumentException("Incorrect Mode for file selection: " + fileSelectionMode);
    }
    
    public int getFileSelectionMode() {
        return this.fileSelectionMode;
    }
    
    public boolean isFileSelectionEnabled() {
        return this.fileSelectionMode == 0 || this.fileSelectionMode == 2;
    }
    
    public boolean isDirectorySelectionEnabled() {
        return this.fileSelectionMode == 1 || this.fileSelectionMode == 2;
    }
    
    public void setMultiSelectionEnabled(final boolean multiSelectionEnabled) {
        if (this.multiSelectionEnabled == multiSelectionEnabled) {
            return;
        }
        this.firePropertyChange("MultiSelectionEnabledChangedProperty", this.multiSelectionEnabled, this.multiSelectionEnabled = multiSelectionEnabled);
    }
    
    public boolean isMultiSelectionEnabled() {
        return this.multiSelectionEnabled;
    }
    
    public boolean isFileHidingEnabled() {
        return this.useFileHiding;
    }
    
    public void setFileHidingEnabled(final boolean useFileHiding) {
        if (this.showFilesListener != null) {
            Toolkit.getDefaultToolkit().removePropertyChangeListener("awt.file.showHiddenFiles", this.showFilesListener);
            this.showFilesListener = null;
        }
        this.firePropertyChange("FileHidingChanged", this.useFileHiding, this.useFileHiding = useFileHiding);
    }
    
    public void setFileFilter(final FileFilter fileFilter) {
        final FileFilter fileFilter2 = this.fileFilter;
        this.fileFilter = fileFilter;
        if (fileFilter != null) {
            if (this.isMultiSelectionEnabled() && this.selectedFiles != null && this.selectedFiles.length > 0) {
                final Vector vector = new Vector();
                boolean b = false;
                for (final File file : this.selectedFiles) {
                    if (fileFilter.accept(file)) {
                        vector.add(file);
                    }
                    else {
                        b = true;
                    }
                }
                if (b) {
                    this.setSelectedFiles((File[])((vector.size() == 0) ? null : ((File[])vector.toArray(new File[vector.size()]))));
                }
            }
            else if (this.selectedFile != null && !fileFilter.accept(this.selectedFile)) {
                this.setSelectedFile(null);
            }
        }
        this.firePropertyChange("fileFilterChanged", fileFilter2, this.fileFilter);
    }
    
    public FileFilter getFileFilter() {
        return this.fileFilter;
    }
    
    public void setFileView(final FileView fileView) {
        this.firePropertyChange("fileViewChanged", this.fileView, this.fileView = fileView);
    }
    
    public FileView getFileView() {
        return this.fileView;
    }
    
    public String getName(final File file) {
        String s = null;
        if (file != null) {
            if (this.getFileView() != null) {
                s = this.getFileView().getName(file);
            }
            final FileView fileView = this.getUI().getFileView(this);
            if (s == null && fileView != null) {
                s = fileView.getName(file);
            }
        }
        return s;
    }
    
    public String getDescription(final File file) {
        String s = null;
        if (file != null) {
            if (this.getFileView() != null) {
                s = this.getFileView().getDescription(file);
            }
            final FileView fileView = this.getUI().getFileView(this);
            if (s == null && fileView != null) {
                s = fileView.getDescription(file);
            }
        }
        return s;
    }
    
    public String getTypeDescription(final File file) {
        String s = null;
        if (file != null) {
            if (this.getFileView() != null) {
                s = this.getFileView().getTypeDescription(file);
            }
            final FileView fileView = this.getUI().getFileView(this);
            if (s == null && fileView != null) {
                s = fileView.getTypeDescription(file);
            }
        }
        return s;
    }
    
    public Icon getIcon(final File file) {
        Icon icon = null;
        if (file != null) {
            if (this.getFileView() != null) {
                icon = this.getFileView().getIcon(file);
            }
            final FileView fileView = this.getUI().getFileView(this);
            if (icon == null && fileView != null) {
                icon = fileView.getIcon(file);
            }
        }
        return icon;
    }
    
    public boolean isTraversable(final File file) {
        Boolean b = null;
        if (file != null) {
            if (this.getFileView() != null) {
                b = this.getFileView().isTraversable(file);
            }
            final FileView fileView = this.getUI().getFileView(this);
            if (b == null && fileView != null) {
                b = fileView.isTraversable(file);
            }
            if (b == null) {
                b = this.getFileSystemView().isTraversable(file);
            }
        }
        return b != null && b;
    }
    
    public boolean accept(final File file) {
        boolean accept = true;
        if (file != null && this.fileFilter != null) {
            accept = this.fileFilter.accept(file);
        }
        return accept;
    }
    
    public void setFileSystemView(final FileSystemView fileSystemView) {
        this.firePropertyChange("FileSystemViewChanged", this.fileSystemView, this.fileSystemView = fileSystemView);
    }
    
    public FileSystemView getFileSystemView() {
        return this.fileSystemView;
    }
    
    public void approveSelection() {
        this.returnValue = 0;
        if (this.dialog != null) {
            this.dialog.setVisible(false);
        }
        this.fireActionPerformed("ApproveSelection");
    }
    
    public void cancelSelection() {
        this.returnValue = 1;
        if (this.dialog != null) {
            this.dialog.setVisible(false);
        }
        this.fireActionPerformed("CancelSelection");
    }
    
    public void addActionListener(final ActionListener actionListener) {
        this.listenerList.add(ActionListener.class, actionListener);
    }
    
    public void removeActionListener(final ActionListener actionListener) {
        this.listenerList.remove(ActionListener.class, actionListener);
    }
    
    public ActionListener[] getActionListeners() {
        return this.listenerList.getListeners(ActionListener.class);
    }
    
    protected void fireActionPerformed(final String s) {
        final Object[] listenerList = this.listenerList.getListenerList();
        final long mostRecentEventTime = EventQueue.getMostRecentEventTime();
        int n = 0;
        final AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            n = ((InputEvent)currentEvent).getModifiers();
        }
        else if (currentEvent instanceof ActionEvent) {
            n = ((ActionEvent)currentEvent).getModifiers();
        }
        ActionEvent actionEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ActionListener.class) {
                if (actionEvent == null) {
                    actionEvent = new ActionEvent(this, 1001, s, mostRecentEventTime, n);
                }
                ((ActionListener)listenerList[i + 1]).actionPerformed(actionEvent);
            }
        }
    }
    
    @Override
    public void updateUI() {
        if (this.isAcceptAllFileFilterUsed()) {
            this.removeChoosableFileFilter(this.getAcceptAllFileFilter());
        }
        final FileChooserUI ui = (FileChooserUI)UIManager.getUI(this);
        if (this.fileSystemView == null) {
            this.setFileSystemView(FileSystemView.getFileSystemView());
        }
        this.setUI(ui);
        if (this.isAcceptAllFileFilterUsed()) {
            this.addChoosableFileFilter(this.getAcceptAllFileFilter());
        }
    }
    
    @Override
    public String getUIClassID() {
        return "FileChooserUI";
    }
    
    public FileChooserUI getUI() {
        return (FileChooserUI)this.ui;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.installShowFilesListener();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        FileSystemView fileSystemView = null;
        if (this.isAcceptAllFileFilterUsed()) {
            this.removeChoosableFileFilter(this.getAcceptAllFileFilter());
        }
        if (this.fileSystemView.equals(FileSystemView.getFileSystemView())) {
            fileSystemView = this.fileSystemView;
            this.fileSystemView = null;
        }
        objectOutputStream.defaultWriteObject();
        if (fileSystemView != null) {
            this.fileSystemView = fileSystemView;
        }
        if (this.isAcceptAllFileFilterUsed()) {
            this.addChoosableFileFilter(this.getAcceptAllFileFilter());
        }
        if (this.getUIClassID().equals("FileChooserUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        final String s = (this.approveButtonText != null) ? this.approveButtonText : "";
        final String s2 = (this.dialogTitle != null) ? this.dialogTitle : "";
        String s3;
        if (this.dialogType == 0) {
            s3 = "OPEN_DIALOG";
        }
        else if (this.dialogType == 1) {
            s3 = "SAVE_DIALOG";
        }
        else if (this.dialogType == 2) {
            s3 = "CUSTOM_DIALOG";
        }
        else {
            s3 = "";
        }
        String s4;
        if (this.returnValue == 1) {
            s4 = "CANCEL_OPTION";
        }
        else if (this.returnValue == 0) {
            s4 = "APPROVE_OPTION";
        }
        else if (this.returnValue == -1) {
            s4 = "ERROR_OPTION";
        }
        else {
            s4 = "";
        }
        final String s5 = this.useFileHiding ? "true" : "false";
        String s6;
        if (this.fileSelectionMode == 0) {
            s6 = "FILES_ONLY";
        }
        else if (this.fileSelectionMode == 1) {
            s6 = "DIRECTORIES_ONLY";
        }
        else if (this.fileSelectionMode == 2) {
            s6 = "FILES_AND_DIRECTORIES";
        }
        else {
            s6 = "";
        }
        return super.paramString() + ",approveButtonText=" + s + ",currentDirectory=" + ((this.currentDirectory != null) ? this.currentDirectory.toString() : "") + ",dialogTitle=" + s2 + ",dialogType=" + s3 + ",fileSelectionMode=" + s6 + ",returnValue=" + s4 + ",selectedFile=" + ((this.selectedFile != null) ? this.selectedFile.toString() : "") + ",useFileHiding=" + s5;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJFileChooser();
        }
        return this.accessibleContext;
    }
    
    private static class WeakPCL implements PropertyChangeListener
    {
        WeakReference<JFileChooser> jfcRef;
        
        public WeakPCL(final JFileChooser fileChooser) {
            this.jfcRef = new WeakReference<JFileChooser>(fileChooser);
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            assert propertyChangeEvent.getPropertyName().equals("awt.file.showHiddenFiles");
            final JFileChooser fileChooser = this.jfcRef.get();
            if (fileChooser == null) {
                Toolkit.getDefaultToolkit().removePropertyChangeListener("awt.file.showHiddenFiles", this);
            }
            else {
                final boolean access$100 = fileChooser.useFileHiding;
                fileChooser.useFileHiding = !(boolean)propertyChangeEvent.getNewValue();
                fileChooser.firePropertyChange("FileHidingChanged", access$100, fileChooser.useFileHiding);
            }
        }
    }
    
    protected class AccessibleJFileChooser extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.FILE_CHOOSER;
        }
    }
}
