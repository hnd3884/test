package com.sun.java.swing.plaf.motif;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.ListModel;
import sun.swing.SwingUtilities2;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.ComboBoxModel;
import javax.swing.Box;
import java.awt.Container;
import javax.swing.BoxLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.ListSelectionModel;
import java.awt.ComponentOrientation;
import java.awt.Component;
import javax.swing.JComponent;
import java.io.IOException;
import sun.awt.shell.ShellFolder;
import javax.swing.DefaultListSelectionModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.filechooser.FileFilter;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.io.File;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicFileChooserUI;

public class MotifFileChooserUI extends BasicFileChooserUI
{
    private FilterComboBoxModel filterComboBoxModel;
    protected JList<File> directoryList;
    protected JList<File> fileList;
    protected JTextField pathField;
    protected JComboBox<FileFilter> filterComboBox;
    protected JTextField filenameTextField;
    private static final Dimension hstrut10;
    private static final Dimension vstrut10;
    private static final Insets insets;
    private static Dimension prefListSize;
    private static Dimension WITH_ACCELERATOR_PREF_SIZE;
    private static Dimension PREF_SIZE;
    private static final int MIN_WIDTH = 200;
    private static final int MIN_HEIGHT = 300;
    private static Dimension PREF_ACC_SIZE;
    private static Dimension ZERO_ACC_SIZE;
    private static Dimension MAX_SIZE;
    private static final Insets buttonMargin;
    private JPanel bottomPanel;
    protected JButton approveButton;
    private String enterFolderNameLabelText;
    private int enterFolderNameLabelMnemonic;
    private String enterFileNameLabelText;
    private int enterFileNameLabelMnemonic;
    private String filesLabelText;
    private int filesLabelMnemonic;
    private String foldersLabelText;
    private int foldersLabelMnemonic;
    private String pathLabelText;
    private int pathLabelMnemonic;
    private String filterLabelText;
    private int filterLabelMnemonic;
    private JLabel fileNameLabel;
    
    private void populateFileNameLabel() {
        if (this.getFileChooser().getFileSelectionMode() == 1) {
            this.fileNameLabel.setText(this.enterFolderNameLabelText);
            this.fileNameLabel.setDisplayedMnemonic(this.enterFolderNameLabelMnemonic);
        }
        else {
            this.fileNameLabel.setText(this.enterFileNameLabelText);
            this.fileNameLabel.setDisplayedMnemonic(this.enterFileNameLabelMnemonic);
        }
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
    
    public MotifFileChooserUI(final JFileChooser fileChooser) {
        super(fileChooser);
        this.directoryList = null;
        this.fileList = null;
        this.pathField = null;
        this.filterComboBox = null;
        this.filenameTextField = null;
        this.enterFolderNameLabelText = null;
        this.enterFolderNameLabelMnemonic = 0;
        this.enterFileNameLabelText = null;
        this.enterFileNameLabelMnemonic = 0;
        this.filesLabelText = null;
        this.filesLabelMnemonic = 0;
        this.foldersLabelText = null;
        this.foldersLabelMnemonic = 0;
        this.pathLabelText = null;
        this.pathLabelMnemonic = 0;
        this.filterLabelText = null;
        this.filterLabelMnemonic = 0;
    }
    
    @Override
    public String getFileName() {
        if (this.filenameTextField != null) {
            return this.filenameTextField.getText();
        }
        return null;
    }
    
    @Override
    public void setFileName(final String text) {
        if (this.filenameTextField != null) {
            this.filenameTextField.setText(text);
        }
    }
    
    @Override
    public String getDirectoryName() {
        return this.pathField.getText();
    }
    
    @Override
    public void setDirectoryName(final String text) {
        this.pathField.setText(text);
    }
    
    @Override
    public void ensureFileIsVisible(final JFileChooser fileChooser, final File file) {
    }
    
    @Override
    public void rescanCurrentDirectory(final JFileChooser fileChooser) {
        this.getModel().validateFileCache();
    }
    
    @Override
    public PropertyChangeListener createPropertyChangeListener(final JFileChooser fileChooser) {
        return new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                final String propertyName = propertyChangeEvent.getPropertyName();
                if (propertyName.equals("SelectedFileChangedProperty")) {
                    final File file = (File)propertyChangeEvent.getNewValue();
                    if (file != null) {
                        MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.getFileChooser().getName(file));
                    }
                }
                else if (propertyName.equals("SelectedFilesChangedProperty")) {
                    final File[] array = (File[])propertyChangeEvent.getNewValue();
                    final JFileChooser fileChooser = MotifFileChooserUI.this.getFileChooser();
                    if (array != null && array.length > 0 && (array.length > 1 || fileChooser.isDirectorySelectionEnabled() || !array[0].isDirectory())) {
                        MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.fileNameString(array));
                    }
                }
                else if (propertyName.equals("fileFilterChanged")) {
                    MotifFileChooserUI.this.fileList.clearSelection();
                }
                else if (propertyName.equals("directoryChanged")) {
                    MotifFileChooserUI.this.directoryList.clearSelection();
                    final ListSelectionModel selectionModel = MotifFileChooserUI.this.directoryList.getSelectionModel();
                    if (selectionModel instanceof DefaultListSelectionModel) {
                        ((DefaultListSelectionModel)selectionModel).moveLeadSelectionIndex(0);
                        selectionModel.setAnchorSelectionIndex(0);
                    }
                    MotifFileChooserUI.this.fileList.clearSelection();
                    final ListSelectionModel selectionModel2 = MotifFileChooserUI.this.fileList.getSelectionModel();
                    if (selectionModel2 instanceof DefaultListSelectionModel) {
                        ((DefaultListSelectionModel)selectionModel2).moveLeadSelectionIndex(0);
                        selectionModel2.setAnchorSelectionIndex(0);
                    }
                    if (MotifFileChooserUI.this.getFileChooser().getCurrentDirectory() != null) {
                        try {
                            MotifFileChooserUI.this.setDirectoryName(ShellFolder.getNormalizedFile((File)propertyChangeEvent.getNewValue()).getPath());
                        }
                        catch (final IOException ex) {
                            MotifFileChooserUI.this.setDirectoryName(((File)propertyChangeEvent.getNewValue()).getAbsolutePath());
                        }
                        if (MotifFileChooserUI.this.getFileChooser().getFileSelectionMode() == 1 && !MotifFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
                            MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.getDirectoryName());
                        }
                    }
                }
                else if (propertyName.equals("fileSelectionChanged")) {
                    if (MotifFileChooserUI.this.fileNameLabel != null) {
                        MotifFileChooserUI.this.populateFileNameLabel();
                    }
                    MotifFileChooserUI.this.directoryList.clearSelection();
                }
                else if (propertyName.equals("MultiSelectionEnabledChangedProperty")) {
                    if (MotifFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
                        MotifFileChooserUI.this.fileList.setSelectionMode(2);
                    }
                    else {
                        MotifFileChooserUI.this.fileList.setSelectionMode(0);
                        MotifFileChooserUI.this.fileList.clearSelection();
                        MotifFileChooserUI.this.getFileChooser().setSelectedFiles(null);
                    }
                }
                else if (propertyName.equals("AccessoryChangedProperty")) {
                    if (MotifFileChooserUI.this.getAccessoryPanel() != null) {
                        if (propertyChangeEvent.getOldValue() != null) {
                            MotifFileChooserUI.this.getAccessoryPanel().remove((Component)propertyChangeEvent.getOldValue());
                        }
                        final JComponent component = (JComponent)propertyChangeEvent.getNewValue();
                        if (component != null) {
                            MotifFileChooserUI.this.getAccessoryPanel().add(component, "Center");
                            MotifFileChooserUI.this.getAccessoryPanel().setPreferredSize(MotifFileChooserUI.PREF_ACC_SIZE);
                            MotifFileChooserUI.this.getAccessoryPanel().setMaximumSize(MotifFileChooserUI.MAX_SIZE);
                        }
                        else {
                            MotifFileChooserUI.this.getAccessoryPanel().setPreferredSize(MotifFileChooserUI.ZERO_ACC_SIZE);
                            MotifFileChooserUI.this.getAccessoryPanel().setMaximumSize(MotifFileChooserUI.ZERO_ACC_SIZE);
                        }
                    }
                }
                else if (propertyName.equals("ApproveButtonTextChangedProperty") || propertyName.equals("ApproveButtonToolTipTextChangedProperty") || propertyName.equals("DialogTypeChangedProperty")) {
                    MotifFileChooserUI.this.approveButton.setText(MotifFileChooserUI.this.getApproveButtonText(MotifFileChooserUI.this.getFileChooser()));
                    MotifFileChooserUI.this.approveButton.setToolTipText(MotifFileChooserUI.this.getApproveButtonToolTipText(MotifFileChooserUI.this.getFileChooser()));
                }
                else if (propertyName.equals("ControlButtonsAreShownChangedProperty")) {
                    MotifFileChooserUI.this.doControlButtonsChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("componentOrientation")) {
                    final ComponentOrientation componentOrientation = (ComponentOrientation)propertyChangeEvent.getNewValue();
                    final JFileChooser fileChooser2 = (JFileChooser)propertyChangeEvent.getSource();
                    if (componentOrientation != propertyChangeEvent.getOldValue()) {
                        fileChooser2.applyComponentOrientation(componentOrientation);
                    }
                }
            }
        };
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifFileChooserUI((JFileChooser)component);
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        component.removePropertyChangeListener(this.filterComboBoxModel);
        this.approveButton.removeActionListener(this.getApproveSelectionAction());
        this.filenameTextField.removeActionListener(this.getApproveSelectionAction());
        super.uninstallUI(component);
    }
    
    @Override
    public void installComponents(final JFileChooser fileChooser) {
        fileChooser.setLayout(new BorderLayout(10, 10));
        fileChooser.setAlignmentX(0.5f);
        final JPanel panel = new JPanel() {
            @Override
            public Insets getInsets() {
                return MotifFileChooserUI.insets;
            }
        };
        panel.setInheritsPopupMenu(true);
        this.align(panel);
        panel.setLayout(new BoxLayout(panel, 3));
        fileChooser.add(panel, "Center");
        final JLabel label = new JLabel(this.pathLabelText);
        label.setDisplayedMnemonic(this.pathLabelMnemonic);
        this.align(label);
        panel.add(label);
        final File currentDirectory = fileChooser.getCurrentDirectory();
        String path = null;
        if (currentDirectory != null) {
            path = currentDirectory.getPath();
        }
        (this.pathField = new JTextField(path) {
            @Override
            public Dimension getMaximumSize() {
                final Dimension maximumSize = super.getMaximumSize();
                maximumSize.height = this.getPreferredSize().height;
                return maximumSize;
            }
        }).setInheritsPopupMenu(true);
        label.setLabelFor(this.pathField);
        this.align(this.pathField);
        this.pathField.addActionListener(this.getUpdateAction());
        panel.add(this.pathField);
        panel.add(Box.createRigidArea(MotifFileChooserUI.vstrut10));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, 2));
        this.align(panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, 3));
        this.align(panel3);
        final JLabel label2 = new JLabel(this.filterLabelText);
        label2.setDisplayedMnemonic(this.filterLabelMnemonic);
        this.align(label2);
        panel3.add(label2);
        (this.filterComboBox = new JComboBox<FileFilter>() {
            @Override
            public Dimension getMaximumSize() {
                final Dimension maximumSize = super.getMaximumSize();
                maximumSize.height = this.getPreferredSize().height;
                return maximumSize;
            }
        }).setInheritsPopupMenu(true);
        label2.setLabelFor(this.filterComboBox);
        this.filterComboBoxModel = this.createFilterComboBoxModel();
        this.filterComboBox.setModel(this.filterComboBoxModel);
        this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
        fileChooser.addPropertyChangeListener(this.filterComboBoxModel);
        this.align(this.filterComboBox);
        panel3.add(this.filterComboBox);
        final JLabel label3 = new JLabel(this.foldersLabelText);
        label3.setDisplayedMnemonic(this.foldersLabelMnemonic);
        this.align(label3);
        panel3.add(label3);
        final JScrollPane directoryList = this.createDirectoryList();
        directoryList.getVerticalScrollBar().setFocusable(false);
        directoryList.getHorizontalScrollBar().setFocusable(false);
        directoryList.setInheritsPopupMenu(true);
        label3.setLabelFor(directoryList.getViewport().getView());
        panel3.add(directoryList);
        panel3.setInheritsPopupMenu(true);
        final JPanel panel4 = new JPanel();
        this.align(panel4);
        panel4.setLayout(new BoxLayout(panel4, 3));
        panel4.setInheritsPopupMenu(true);
        final JLabel label4 = new JLabel(this.filesLabelText);
        label4.setDisplayedMnemonic(this.filesLabelMnemonic);
        this.align(label4);
        panel4.add(label4);
        final JScrollPane filesList = this.createFilesList();
        label4.setLabelFor(filesList.getViewport().getView());
        panel4.add(filesList);
        filesList.setInheritsPopupMenu(true);
        panel2.add(panel3);
        panel2.add(Box.createRigidArea(MotifFileChooserUI.hstrut10));
        panel2.add(panel4);
        panel2.setInheritsPopupMenu(true);
        final JPanel accessoryPanel = this.getAccessoryPanel();
        final JComponent accessory = fileChooser.getAccessory();
        if (accessoryPanel != null) {
            if (accessory == null) {
                accessoryPanel.setPreferredSize(MotifFileChooserUI.ZERO_ACC_SIZE);
                accessoryPanel.setMaximumSize(MotifFileChooserUI.ZERO_ACC_SIZE);
            }
            else {
                this.getAccessoryPanel().add(accessory, "Center");
                accessoryPanel.setPreferredSize(MotifFileChooserUI.PREF_ACC_SIZE);
                accessoryPanel.setMaximumSize(MotifFileChooserUI.MAX_SIZE);
            }
            this.align(accessoryPanel);
            panel2.add(accessoryPanel);
            accessoryPanel.setInheritsPopupMenu(true);
        }
        panel.add(panel2);
        panel.add(Box.createRigidArea(MotifFileChooserUI.vstrut10));
        this.fileNameLabel = new JLabel();
        this.populateFileNameLabel();
        this.align(this.fileNameLabel);
        panel.add(this.fileNameLabel);
        (this.filenameTextField = new JTextField() {
            @Override
            public Dimension getMaximumSize() {
                final Dimension maximumSize = super.getMaximumSize();
                maximumSize.height = this.getPreferredSize().height;
                return maximumSize;
            }
        }).setInheritsPopupMenu(true);
        this.fileNameLabel.setLabelFor(this.filenameTextField);
        this.filenameTextField.addActionListener(this.getApproveSelectionAction());
        this.align(this.filenameTextField);
        this.filenameTextField.setAlignmentX(0.0f);
        panel.add(this.filenameTextField);
        (this.bottomPanel = this.getBottomPanel()).add(new JSeparator(), "North");
        final JPanel panel5 = new JPanel();
        this.align(panel5);
        panel5.setLayout(new BoxLayout(panel5, 2));
        panel5.add(Box.createGlue());
        (this.approveButton = new JButton(this.getApproveButtonText(fileChooser)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(MotifFileChooserUI.MAX_SIZE.width, this.getPreferredSize().height);
            }
        }).setMnemonic(this.getApproveButtonMnemonic(fileChooser));
        this.approveButton.setToolTipText(this.getApproveButtonToolTipText(fileChooser));
        this.approveButton.setInheritsPopupMenu(true);
        this.align(this.approveButton);
        this.approveButton.setMargin(MotifFileChooserUI.buttonMargin);
        this.approveButton.addActionListener(this.getApproveSelectionAction());
        panel5.add(this.approveButton);
        panel5.add(Box.createGlue());
        final JButton button = new JButton(this.updateButtonText) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(MotifFileChooserUI.MAX_SIZE.width, this.getPreferredSize().height);
            }
        };
        button.setMnemonic(this.updateButtonMnemonic);
        button.setToolTipText(this.updateButtonToolTipText);
        button.setInheritsPopupMenu(true);
        this.align(button);
        button.setMargin(MotifFileChooserUI.buttonMargin);
        button.addActionListener(this.getUpdateAction());
        panel5.add(button);
        panel5.add(Box.createGlue());
        final JButton button2 = new JButton(this.cancelButtonText) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(MotifFileChooserUI.MAX_SIZE.width, this.getPreferredSize().height);
            }
        };
        button2.setMnemonic(this.cancelButtonMnemonic);
        button2.setToolTipText(this.cancelButtonToolTipText);
        button2.setInheritsPopupMenu(true);
        this.align(button2);
        button2.setMargin(MotifFileChooserUI.buttonMargin);
        button2.addActionListener(this.getCancelSelectionAction());
        panel5.add(button2);
        panel5.add(Box.createGlue());
        final JButton button3 = new JButton(this.helpButtonText) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(MotifFileChooserUI.MAX_SIZE.width, this.getPreferredSize().height);
            }
        };
        button3.setMnemonic(this.helpButtonMnemonic);
        button3.setToolTipText(this.helpButtonToolTipText);
        this.align(button3);
        button3.setMargin(MotifFileChooserUI.buttonMargin);
        button3.setEnabled(false);
        button3.setInheritsPopupMenu(true);
        panel5.add(button3);
        panel5.add(Box.createGlue());
        panel5.setInheritsPopupMenu(true);
        this.bottomPanel.add(panel5, "South");
        this.bottomPanel.setInheritsPopupMenu(true);
        if (fileChooser.getControlButtonsAreShown()) {
            fileChooser.add(this.bottomPanel, "South");
        }
    }
    
    protected JPanel getBottomPanel() {
        if (this.bottomPanel == null) {
            this.bottomPanel = new JPanel(new BorderLayout(0, 4));
        }
        return this.bottomPanel;
    }
    
    private void doControlButtonsChanged(final PropertyChangeEvent propertyChangeEvent) {
        if (this.getFileChooser().getControlButtonsAreShown()) {
            this.getFileChooser().add(this.bottomPanel, "South");
        }
        else {
            this.getFileChooser().remove(this.getBottomPanel());
        }
    }
    
    @Override
    public void uninstallComponents(final JFileChooser fileChooser) {
        fileChooser.removeAll();
        this.bottomPanel = null;
        if (this.filterComboBoxModel != null) {
            fileChooser.removePropertyChangeListener(this.filterComboBoxModel);
        }
    }
    
    @Override
    protected void installStrings(final JFileChooser fileChooser) {
        super.installStrings(fileChooser);
        final Locale locale = fileChooser.getLocale();
        this.enterFolderNameLabelText = UIManager.getString("FileChooser.enterFolderNameLabelText", locale);
        this.enterFolderNameLabelMnemonic = this.getMnemonic("FileChooser.enterFolderNameLabelMnemonic", locale);
        this.enterFileNameLabelText = UIManager.getString("FileChooser.enterFileNameLabelText", locale);
        this.enterFileNameLabelMnemonic = this.getMnemonic("FileChooser.enterFileNameLabelMnemonic", locale);
        this.filesLabelText = UIManager.getString("FileChooser.filesLabelText", locale);
        this.filesLabelMnemonic = this.getMnemonic("FileChooser.filesLabelMnemonic", locale);
        this.foldersLabelText = UIManager.getString("FileChooser.foldersLabelText", locale);
        this.foldersLabelMnemonic = this.getMnemonic("FileChooser.foldersLabelMnemonic", locale);
        this.pathLabelText = UIManager.getString("FileChooser.pathLabelText", locale);
        this.pathLabelMnemonic = this.getMnemonic("FileChooser.pathLabelMnemonic", locale);
        this.filterLabelText = UIManager.getString("FileChooser.filterLabelText", locale);
        this.filterLabelMnemonic = this.getMnemonic("FileChooser.filterLabelMnemonic", locale);
    }
    
    private Integer getMnemonic(final String s, final Locale locale) {
        return SwingUtilities2.getUIDefaultsInt(s, locale);
    }
    
    @Override
    protected void installIcons(final JFileChooser fileChooser) {
    }
    
    @Override
    protected void uninstallIcons(final JFileChooser fileChooser) {
    }
    
    protected JScrollPane createFilesList() {
        this.fileList = new JList<File>();
        if (this.getFileChooser().isMultiSelectionEnabled()) {
            this.fileList.setSelectionMode(2);
        }
        else {
            this.fileList.setSelectionMode(0);
        }
        this.fileList.setModel(new MotifFileListModel());
        this.fileList.getSelectionModel().removeSelectionInterval(0, 0);
        this.fileList.setCellRenderer(new FileCellRenderer());
        this.fileList.addListSelectionListener(this.createListSelectionListener(this.getFileChooser()));
        this.fileList.addMouseListener(this.createDoubleClickListener(this.getFileChooser(), this.fileList));
        this.fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                final JFileChooser fileChooser = MotifFileChooserUI.this.getFileChooser();
                if (SwingUtilities.isLeftMouseButton(mouseEvent) && !fileChooser.isMultiSelectionEnabled()) {
                    final int loc2IndexFileList = SwingUtilities2.loc2IndexFileList(MotifFileChooserUI.this.fileList, mouseEvent.getPoint());
                    if (loc2IndexFileList >= 0) {
                        MotifFileChooserUI.this.setFileName(fileChooser.getName(MotifFileChooserUI.this.fileList.getModel().getElementAt(loc2IndexFileList)));
                    }
                }
            }
        });
        this.align(this.fileList);
        final JScrollPane scrollPane = new JScrollPane(this.fileList);
        scrollPane.setPreferredSize(MotifFileChooserUI.prefListSize);
        scrollPane.setMaximumSize(MotifFileChooserUI.MAX_SIZE);
        this.align(scrollPane);
        this.fileList.setInheritsPopupMenu(true);
        scrollPane.setInheritsPopupMenu(true);
        return scrollPane;
    }
    
    protected JScrollPane createDirectoryList() {
        this.align(this.directoryList = new JList<File>());
        this.directoryList.setCellRenderer(new DirectoryCellRenderer());
        this.directoryList.setModel(new MotifDirectoryListModel());
        this.directoryList.getSelectionModel().removeSelectionInterval(0, 0);
        this.directoryList.addMouseListener(this.createDoubleClickListener(this.getFileChooser(), this.directoryList));
        this.directoryList.addListSelectionListener(this.createListSelectionListener(this.getFileChooser()));
        this.directoryList.setInheritsPopupMenu(true);
        final JScrollPane scrollPane = new JScrollPane(this.directoryList);
        scrollPane.setMaximumSize(MotifFileChooserUI.MAX_SIZE);
        scrollPane.setPreferredSize(MotifFileChooserUI.prefListSize);
        scrollPane.setInheritsPopupMenu(true);
        this.align(scrollPane);
        return scrollPane;
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Dimension dimension = (this.getFileChooser().getAccessory() != null) ? MotifFileChooserUI.WITH_ACCELERATOR_PREF_SIZE : MotifFileChooserUI.PREF_SIZE;
        final Dimension preferredLayoutSize = component.getLayout().preferredLayoutSize(component);
        if (preferredLayoutSize != null) {
            return new Dimension((preferredLayoutSize.width < dimension.width) ? dimension.width : preferredLayoutSize.width, (preferredLayoutSize.height < dimension.height) ? dimension.height : preferredLayoutSize.height);
        }
        return dimension;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return new Dimension(200, 300);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    protected void align(final JComponent component) {
        component.setAlignmentX(0.0f);
        component.setAlignmentY(0.0f);
    }
    
    protected FilterComboBoxModel createFilterComboBoxModel() {
        return new FilterComboBoxModel();
    }
    
    protected FilterComboBoxRenderer createFilterComboBoxRenderer() {
        return new FilterComboBoxRenderer();
    }
    
    @Override
    protected JButton getApproveButton(final JFileChooser fileChooser) {
        return this.approveButton;
    }
    
    static {
        hstrut10 = new Dimension(10, 1);
        vstrut10 = new Dimension(1, 10);
        insets = new Insets(10, 10, 10, 10);
        MotifFileChooserUI.prefListSize = new Dimension(75, 150);
        MotifFileChooserUI.WITH_ACCELERATOR_PREF_SIZE = new Dimension(650, 450);
        MotifFileChooserUI.PREF_SIZE = new Dimension(350, 450);
        MotifFileChooserUI.PREF_ACC_SIZE = new Dimension(10, 10);
        MotifFileChooserUI.ZERO_ACC_SIZE = new Dimension(1, 1);
        MotifFileChooserUI.MAX_SIZE = new Dimension(32767, 32767);
        buttonMargin = new Insets(3, 3, 3, 3);
    }
    
    protected class FileCellRenderer extends DefaultListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(final JList list, final Object o, final int n, final boolean b, final boolean b2) {
            super.getListCellRendererComponent(list, o, n, b, b2);
            this.setText(MotifFileChooserUI.this.getFileChooser().getName((File)o));
            this.setInheritsPopupMenu(true);
            return this;
        }
    }
    
    protected class DirectoryCellRenderer extends DefaultListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(final JList list, final Object o, final int n, final boolean b, final boolean b2) {
            super.getListCellRendererComponent(list, o, n, b, b2);
            this.setText(MotifFileChooserUI.this.getFileChooser().getName((File)o));
            this.setInheritsPopupMenu(true);
            return this;
        }
    }
    
    protected class MotifDirectoryListModel extends AbstractListModel<File> implements ListDataListener
    {
        public MotifDirectoryListModel() {
            MotifFileChooserUI.this.getModel().addListDataListener(this);
        }
        
        @Override
        public int getSize() {
            return MotifFileChooserUI.this.getModel().getDirectories().size();
        }
        
        @Override
        public File getElementAt(final int n) {
            return MotifFileChooserUI.this.getModel().getDirectories().elementAt(n);
        }
        
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
            this.fireIntervalAdded(this, listDataEvent.getIndex0(), listDataEvent.getIndex1());
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
            this.fireIntervalRemoved(this, listDataEvent.getIndex0(), listDataEvent.getIndex1());
        }
        
        public void fireContentsChanged() {
            this.fireContentsChanged(this, 0, MotifFileChooserUI.this.getModel().getDirectories().size() - 1);
        }
        
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
            this.fireContentsChanged();
        }
    }
    
    protected class MotifFileListModel extends AbstractListModel<File> implements ListDataListener
    {
        public MotifFileListModel() {
            MotifFileChooserUI.this.getModel().addListDataListener(this);
        }
        
        @Override
        public int getSize() {
            return MotifFileChooserUI.this.getModel().getFiles().size();
        }
        
        public boolean contains(final Object o) {
            return MotifFileChooserUI.this.getModel().getFiles().contains(o);
        }
        
        public int indexOf(final Object o) {
            return MotifFileChooserUI.this.getModel().getFiles().indexOf(o);
        }
        
        @Override
        public File getElementAt(final int n) {
            return MotifFileChooserUI.this.getModel().getFiles().elementAt(n);
        }
        
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
            this.fireIntervalAdded(this, listDataEvent.getIndex0(), listDataEvent.getIndex1());
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
            this.fireIntervalRemoved(this, listDataEvent.getIndex0(), listDataEvent.getIndex1());
        }
        
        public void fireContentsChanged() {
            this.fireContentsChanged(this, 0, MotifFileChooserUI.this.getModel().getFiles().size() - 1);
        }
        
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
            this.fireContentsChanged();
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
    
    protected class FilterComboBoxModel extends AbstractListModel<FileFilter> implements ComboBoxModel<FileFilter>, PropertyChangeListener
    {
        protected FileFilter[] filters;
        
        protected FilterComboBoxModel() {
            this.filters = MotifFileChooserUI.this.getFileChooser().getChoosableFileFilters();
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName.equals("ChoosableFileFilterChangedProperty")) {
                this.filters = (FileFilter[])propertyChangeEvent.getNewValue();
                this.fireContentsChanged(this, -1, -1);
            }
            else if (propertyName.equals("fileFilterChanged")) {
                this.fireContentsChanged(this, -1, -1);
            }
        }
        
        @Override
        public void setSelectedItem(final Object o) {
            if (o != null) {
                MotifFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)o);
                this.fireContentsChanged(this, -1, -1);
            }
        }
        
        @Override
        public Object getSelectedItem() {
            final FileFilter fileFilter = MotifFileChooserUI.this.getFileChooser().getFileFilter();
            boolean b = false;
            if (fileFilter != null) {
                final FileFilter[] filters = this.filters;
                for (int length = filters.length, i = 0; i < length; ++i) {
                    if (filters[i] == fileFilter) {
                        b = true;
                    }
                }
                if (!b) {
                    MotifFileChooserUI.this.getFileChooser().addChoosableFileFilter(fileFilter);
                }
            }
            return MotifFileChooserUI.this.getFileChooser().getFileFilter();
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
                return MotifFileChooserUI.this.getFileChooser().getFileFilter();
            }
            if (this.filters != null) {
                return this.filters[n];
            }
            return null;
        }
    }
}
