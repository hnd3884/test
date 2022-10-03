package com.sun.java.swing.plaf.windows;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Arrays;
import sun.awt.shell.ShellFolder;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.JList;
import javax.swing.filechooser.FileView;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import java.awt.ComponentOrientation;
import javax.swing.filechooser.FileSystemView;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.ActionMap;
import sun.swing.SwingUtilities2;
import java.util.Locale;
import java.awt.Graphics;
import javax.swing.plaf.InsetsUIResource;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.Container;
import javax.swing.BoxLayout;
import java.beans.PropertyChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.Icon;
import javax.swing.Action;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ButtonGroup;
import javax.swing.UIManager;
import javax.swing.ListCellRenderer;
import javax.swing.ComboBoxModel;
import javax.swing.Box;
import java.awt.Component;
import javax.swing.JToolBar;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.beans.PropertyChangeListener;
import javax.swing.JFileChooser;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.filechooser.FileFilter;
import javax.swing.JButton;
import sun.swing.WindowsPlacesBar;
import sun.swing.FilePane;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicFileChooserUI;

public class WindowsFileChooserUI extends BasicFileChooserUI
{
    private JPanel centerPanel;
    private JLabel lookInLabel;
    private JComboBox<File> directoryComboBox;
    private DirectoryComboBoxModel directoryComboBoxModel;
    private ActionListener directoryComboBoxAction;
    private FilterComboBoxModel filterComboBoxModel;
    private JTextField filenameTextField;
    private FilePane filePane;
    private WindowsPlacesBar placesBar;
    private JButton approveButton;
    private JButton cancelButton;
    private JPanel buttonPanel;
    private JPanel bottomPanel;
    private JComboBox<FileFilter> filterComboBox;
    private static final Dimension hstrut10;
    private static final Dimension vstrut4;
    private static final Dimension vstrut6;
    private static final Dimension vstrut8;
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
    private String newFolderToolTipText;
    private String newFolderAccessibleName;
    private String viewMenuButtonToolTipText;
    private String viewMenuButtonAccessibleName;
    private BasicFileView fileView;
    private JLabel fileNameLabel;
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
        return new WindowsFileChooserUI((JFileChooser)component);
    }
    
    public WindowsFileChooserUI(final JFileChooser fileChooser) {
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
        this.newFolderToolTipText = null;
        this.newFolderAccessibleName = null;
        this.viewMenuButtonToolTipText = null;
        this.viewMenuButtonAccessibleName = null;
        this.fileView = new WindowsFileView();
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
    }
    
    @Override
    public void uninstallComponents(final JFileChooser fileChooser) {
        fileChooser.removeAll();
    }
    
    @Override
    public void installComponents(final JFileChooser fileChooser) {
        fileChooser.addPropertyChangeListener(this.filePane = new FilePane(new WindowsFileChooserUIAccessor()));
        fileChooser.getFileSystemView();
        fileChooser.setBorder(new EmptyBorder(4, 10, 10, 10));
        fileChooser.setLayout(new BorderLayout(8, 8));
        this.updateUseShellFolder();
        final JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        fileChooser.add(toolBar, "North");
        (this.lookInLabel = new JLabel(this.lookInLabelText, 11) {
            @Override
            public Dimension getPreferredSize() {
                return this.getMinimumSize();
            }
            
            @Override
            public Dimension getMinimumSize() {
                final Dimension preferredSize = super.getPreferredSize();
                if (WindowsFileChooserUI.this.placesBar != null) {
                    preferredSize.width = Math.max(preferredSize.width, WindowsFileChooserUI.this.placesBar.getWidth());
                }
                return preferredSize;
            }
        }).setDisplayedMnemonic(this.lookInLabelMnemonic);
        this.lookInLabel.setAlignmentX(0.0f);
        this.lookInLabel.setAlignmentY(0.5f);
        toolBar.add(this.lookInLabel);
        toolBar.add(Box.createRigidArea(new Dimension(8, 0)));
        (this.directoryComboBox = new JComboBox<File>() {
            @Override
            public Dimension getMinimumSize() {
                final Dimension minimumSize = super.getMinimumSize();
                minimumSize.width = 60;
                return minimumSize;
            }
            
            @Override
            public Dimension getPreferredSize() {
                final Dimension preferredSize = super.getPreferredSize();
                preferredSize.width = 150;
                return preferredSize;
            }
        }).putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
        this.lookInLabel.setLabelFor(this.directoryComboBox);
        this.directoryComboBoxModel = this.createDirectoryComboBoxModel(fileChooser);
        this.directoryComboBox.setModel(this.directoryComboBoxModel);
        this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
        this.directoryComboBox.setRenderer(this.createDirectoryComboBoxRenderer(fileChooser));
        this.directoryComboBox.setAlignmentX(0.0f);
        this.directoryComboBox.setAlignmentY(0.5f);
        this.directoryComboBox.setMaximumRowCount(8);
        toolBar.add(this.directoryComboBox);
        toolBar.add(Box.createRigidArea(WindowsFileChooserUI.hstrut10));
        toolBar.add(createToolButton(this.getChangeToParentDirectoryAction(), this.upFolderIcon, this.upFolderToolTipText, this.upFolderAccessibleName));
        if (!UIManager.getBoolean("FileChooser.readOnly")) {
            toolBar.add(createToolButton(this.filePane.getNewFolderAction(), this.newFolderIcon, this.newFolderToolTipText, this.newFolderAccessibleName));
        }
        final ButtonGroup buttonGroup = new ButtonGroup();
        final JPopupMenu popupMenu = new JPopupMenu();
        final JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem(this.filePane.getViewTypeAction(0));
        radioButtonMenuItem.setSelected(this.filePane.getViewType() == 0);
        popupMenu.add(radioButtonMenuItem);
        buttonGroup.add(radioButtonMenuItem);
        final JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem(this.filePane.getViewTypeAction(1));
        radioButtonMenuItem2.setSelected(this.filePane.getViewType() == 1);
        popupMenu.add(radioButtonMenuItem2);
        buttonGroup.add(radioButtonMenuItem2);
        final BufferedImage bufferedImage = new BufferedImage(this.viewMenuIcon.getIconWidth() + 7, this.viewMenuIcon.getIconHeight(), 2);
        final Graphics graphics = bufferedImage.getGraphics();
        this.viewMenuIcon.paintIcon(this.filePane, graphics, 0, 0);
        final int n = bufferedImage.getWidth() - 5;
        final int n2 = bufferedImage.getHeight() / 2 - 1;
        graphics.setColor(Color.BLACK);
        graphics.fillPolygon(new int[] { n, n + 5, n + 2 }, new int[] { n2, n2, n2 + 3 }, 3);
        final JButton toolButton = createToolButton(null, new ImageIcon(bufferedImage), this.viewMenuButtonToolTipText, this.viewMenuButtonAccessibleName);
        toolButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent mouseEvent) {
                if (SwingUtilities.isLeftMouseButton(mouseEvent) && !toolButton.isSelected()) {
                    toolButton.setSelected(true);
                    popupMenu.show(toolButton, 0, toolButton.getHeight());
                }
            }
        });
        toolButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 32 && toolButton.getModel().isRollover()) {
                    toolButton.setSelected(true);
                    popupMenu.show(toolButton, 0, toolButton.getHeight());
                }
            }
        });
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent popupMenuEvent) {
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent popupMenuEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        toolButton.setSelected(false);
                    }
                });
            }
            
            @Override
            public void popupMenuCanceled(final PopupMenuEvent popupMenuEvent) {
            }
        });
        toolBar.add(toolButton);
        toolBar.add(Box.createRigidArea(new Dimension(80, 0)));
        this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                if ("viewType".equals(propertyChangeEvent.getPropertyName())) {
                    switch (WindowsFileChooserUI.this.filePane.getViewType()) {
                        case 0: {
                            radioButtonMenuItem.setSelected(true);
                            break;
                        }
                        case 1: {
                            radioButtonMenuItem2.setSelected(true);
                            break;
                        }
                    }
                }
            }
        });
        (this.centerPanel = new JPanel(new BorderLayout())).add(this.getAccessoryPanel(), "After");
        final JComponent accessory = fileChooser.getAccessory();
        if (accessory != null) {
            this.getAccessoryPanel().add(accessory);
        }
        this.filePane.setPreferredSize(WindowsFileChooserUI.LIST_PREF_SIZE);
        this.centerPanel.add(this.filePane, "Center");
        fileChooser.add(this.centerPanel, "Center");
        this.getBottomPanel().setLayout(new BoxLayout(this.getBottomPanel(), 2));
        this.centerPanel.add(this.getBottomPanel(), "South");
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, 3));
        panel.add(Box.createRigidArea(WindowsFileChooserUI.vstrut4));
        this.fileNameLabel = new JLabel();
        this.populateFileNameLabel();
        this.fileNameLabel.setAlignmentY(0.0f);
        panel.add(this.fileNameLabel);
        panel.add(Box.createRigidArea(new Dimension(1, 12)));
        final JLabel label = new JLabel(this.filesOfTypeLabelText);
        label.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
        panel.add(label);
        this.getBottomPanel().add(panel);
        this.getBottomPanel().add(Box.createRigidArea(new Dimension(15, 0)));
        final JPanel panel2 = new JPanel();
        panel2.add(Box.createRigidArea(WindowsFileChooserUI.vstrut8));
        panel2.setLayout(new BoxLayout(panel2, 1));
        this.filenameTextField = new JTextField(35) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(32767, super.getPreferredSize().height);
            }
        };
        this.fileNameLabel.setLabelFor(this.filenameTextField);
        this.filenameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent focusEvent) {
                if (!WindowsFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
                    WindowsFileChooserUI.this.filePane.clearSelection();
                }
            }
        });
        if (fileChooser.isMultiSelectionEnabled()) {
            this.setFileName(this.fileNameString(fileChooser.getSelectedFiles()));
        }
        else {
            this.setFileName(this.fileNameString(fileChooser.getSelectedFile()));
        }
        panel2.add(this.filenameTextField);
        panel2.add(Box.createRigidArea(WindowsFileChooserUI.vstrut8));
        fileChooser.addPropertyChangeListener(this.filterComboBoxModel = this.createFilterComboBoxModel());
        label.setLabelFor(this.filterComboBox = new JComboBox<FileFilter>(this.filterComboBoxModel));
        this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
        panel2.add(this.filterComboBox);
        this.getBottomPanel().add(panel2);
        this.getBottomPanel().add(Box.createRigidArea(new Dimension(30, 0)));
        this.getButtonPanel().setLayout(new BoxLayout(this.getButtonPanel(), 1));
        this.approveButton = new JButton(this.getApproveButtonText(fileChooser)) {
            @Override
            public Dimension getMaximumSize() {
                return (WindowsFileChooserUI.this.approveButton.getPreferredSize().width > WindowsFileChooserUI.this.cancelButton.getPreferredSize().width) ? WindowsFileChooserUI.this.approveButton.getPreferredSize() : WindowsFileChooserUI.this.cancelButton.getPreferredSize();
            }
        };
        final Insets margin = this.approveButton.getMargin();
        final InsetsUIResource insetsUIResource = new InsetsUIResource(margin.top, margin.left + 5, margin.bottom, margin.right + 5);
        this.approveButton.setMargin(insetsUIResource);
        this.approveButton.setMnemonic(this.getApproveButtonMnemonic(fileChooser));
        this.approveButton.addActionListener(this.getApproveSelectionAction());
        this.approveButton.setToolTipText(this.getApproveButtonToolTipText(fileChooser));
        this.getButtonPanel().add(Box.createRigidArea(WindowsFileChooserUI.vstrut6));
        this.getButtonPanel().add(this.approveButton);
        this.getButtonPanel().add(Box.createRigidArea(WindowsFileChooserUI.vstrut4));
        (this.cancelButton = new JButton(this.cancelButtonText) {
            @Override
            public Dimension getMaximumSize() {
                return (WindowsFileChooserUI.this.approveButton.getPreferredSize().width > WindowsFileChooserUI.this.cancelButton.getPreferredSize().width) ? WindowsFileChooserUI.this.approveButton.getPreferredSize() : WindowsFileChooserUI.this.cancelButton.getPreferredSize();
            }
        }).setMargin(insetsUIResource);
        this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
        this.cancelButton.addActionListener(this.getCancelSelectionAction());
        this.getButtonPanel().add(this.cancelButton);
        if (fileChooser.getControlButtonsAreShown()) {
            this.addControlButtons();
        }
    }
    
    private void updateUseShellFolder() {
        final JFileChooser fileChooser = this.getFileChooser();
        if (FilePane.usesShellFolder(fileChooser)) {
            if (this.placesBar == null && !UIManager.getBoolean("FileChooser.noPlacesBar")) {
                fileChooser.add(this.placesBar = new WindowsPlacesBar(fileChooser, XPStyle.getXP() != null), "Before");
                fileChooser.addPropertyChangeListener(this.placesBar);
            }
        }
        else if (this.placesBar != null) {
            fileChooser.remove(this.placesBar);
            fileChooser.removePropertyChangeListener(this.placesBar);
            this.placesBar = null;
        }
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
        this.newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", locale);
        this.newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", locale);
        this.viewMenuButtonToolTipText = UIManager.getString("FileChooser.viewMenuButtonToolTipText", locale);
        this.viewMenuButtonAccessibleName = UIManager.getString("FileChooser.viewMenuButtonAccessibleName", locale);
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
        if (this.placesBar != null) {
            component.removePropertyChangeListener(this.placesBar);
        }
        this.cancelButton.removeActionListener(this.getCancelSelectionAction());
        this.approveButton.removeActionListener(this.getApproveSelectionAction());
        this.filenameTextField.removeActionListener(this.getApproveSelectionAction());
        if (this.filePane != null) {
            this.filePane.uninstallUI();
            this.filePane = null;
        }
        super.uninstallUI(component);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final int width = WindowsFileChooserUI.PREF_SIZE.width;
        final Dimension preferredLayoutSize = component.getLayout().preferredLayoutSize(component);
        if (preferredLayoutSize != null) {
            return new Dimension((preferredLayoutSize.width < width) ? width : preferredLayoutSize.width, (preferredLayoutSize.height < WindowsFileChooserUI.PREF_SIZE.height) ? WindowsFileChooserUI.PREF_SIZE.height : preferredLayoutSize.height);
        }
        return new Dimension(width, WindowsFileChooserUI.PREF_SIZE.height);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return new Dimension(WindowsFileChooserUI.MIN_WIDTH, WindowsFileChooserUI.MIN_HEIGHT);
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
        this.approveButton.setMnemonic(this.getApproveButtonMnemonic(fileChooser));
    }
    
    private void doDialogTypeChanged(final PropertyChangeEvent propertyChangeEvent) {
        final JFileChooser fileChooser = this.getFileChooser();
        this.approveButton.setText(this.getApproveButtonText(fileChooser));
        this.approveButton.setToolTipText(this.getApproveButtonToolTipText(fileChooser));
        this.approveButton.setMnemonic(this.getApproveButtonMnemonic(fileChooser));
        if (fileChooser.getDialogType() == 1) {
            this.lookInLabel.setText(this.saveInLabelText);
        }
        else {
            this.lookInLabel.setText(this.lookInLabelText);
        }
    }
    
    private void doApproveButtonMnemonicChanged(final PropertyChangeEvent propertyChangeEvent) {
        this.approveButton.setMnemonic(this.getApproveButtonMnemonic(this.getFileChooser()));
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
                    WindowsFileChooserUI.this.doSelectedFileChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("SelectedFilesChangedProperty")) {
                    WindowsFileChooserUI.this.doSelectedFilesChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("directoryChanged")) {
                    WindowsFileChooserUI.this.doDirectoryChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("fileFilterChanged")) {
                    WindowsFileChooserUI.this.doFilterChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("fileSelectionChanged")) {
                    WindowsFileChooserUI.this.doFileSelectionModeChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("AccessoryChangedProperty")) {
                    WindowsFileChooserUI.this.doAccessoryChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("ApproveButtonTextChangedProperty") || propertyName.equals("ApproveButtonToolTipTextChangedProperty")) {
                    WindowsFileChooserUI.this.doApproveButtonTextChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("DialogTypeChangedProperty")) {
                    WindowsFileChooserUI.this.doDialogTypeChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("ApproveButtonMnemonicChangedProperty")) {
                    WindowsFileChooserUI.this.doApproveButtonMnemonicChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("ControlButtonsAreShownChangedProperty")) {
                    WindowsFileChooserUI.this.doControlButtonsChanged(propertyChangeEvent);
                }
                else if (propertyName == "FileChooser.useShellFolder") {
                    WindowsFileChooserUI.this.updateUseShellFolder();
                    WindowsFileChooserUI.this.doDirectoryChanged(propertyChangeEvent);
                }
                else if (propertyName.equals("componentOrientation")) {
                    final ComponentOrientation componentOrientation = (ComponentOrientation)propertyChangeEvent.getNewValue();
                    final JFileChooser fileChooser = (JFileChooser)propertyChangeEvent.getSource();
                    if (componentOrientation != propertyChangeEvent.getOldValue()) {
                        fileChooser.applyComponentOrientation(componentOrientation);
                    }
                }
                else if (propertyName.equals("ancestor") && propertyChangeEvent.getOldValue() == null && propertyChangeEvent.getNewValue() != null) {
                    WindowsFileChooserUI.this.filenameTextField.selectAll();
                    WindowsFileChooserUI.this.filenameTextField.requestFocus();
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
    protected void setDirectorySelected(final boolean directorySelected) {
        super.setDirectorySelected(directorySelected);
        final JFileChooser fileChooser = this.getFileChooser();
        if (directorySelected) {
            this.approveButton.setText(this.directoryOpenButtonText);
            this.approveButton.setToolTipText(this.directoryOpenButtonToolTipText);
            this.approveButton.setMnemonic(this.directoryOpenButtonMnemonic);
        }
        else {
            this.approveButton.setText(this.getApproveButtonText(fileChooser));
            this.approveButton.setToolTipText(this.getApproveButtonToolTipText(fileChooser));
            this.approveButton.setMnemonic(this.getApproveButtonMnemonic(fileChooser));
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
    
    private static JButton createToolButton(final Action action, final Icon icon, final String toolTipText, final String s) {
        final JButton button = new JButton(action);
        button.setText(null);
        button.setIcon(icon);
        button.setToolTipText(toolTipText);
        button.setRequestFocusEnabled(false);
        button.putClientProperty("AccessibleName", s);
        button.putClientProperty(WindowsLookAndFeel.HI_RES_DISABLED_ICON_CLIENT_KEY, Boolean.TRUE);
        button.setAlignmentX(0.0f);
        button.setAlignmentY(0.5f);
        button.setMargin(WindowsFileChooserUI.shrinkwrap);
        button.setFocusPainted(false);
        button.setModel(new DefaultButtonModel() {
            @Override
            public void setPressed(final boolean pressed) {
                if (!pressed || this.isRollover()) {
                    super.setPressed(pressed);
                }
            }
            
            @Override
            public void setRollover(final boolean rollover) {
                if (rollover && !this.isRollover()) {
                    for (final Component component : button.getParent().getComponents()) {
                        if (component instanceof JButton && component != button) {
                            ((JButton)component).getModel().setRollover(false);
                        }
                    }
                }
                super.setRollover(rollover);
            }
            
            @Override
            public void setSelected(final boolean selected) {
                super.setSelected(selected);
                if (selected) {
                    this.stateMask |= 0x5;
                }
                else {
                    this.stateMask &= 0xFFFFFFFA;
                }
            }
        });
        button.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent focusEvent) {
                button.getModel().setRollover(true);
            }
            
            @Override
            public void focusLost(final FocusEvent focusEvent) {
                button.getModel().setRollover(false);
            }
        });
        return button;
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
    
    @Override
    public FileView getFileView(final JFileChooser fileChooser) {
        return this.fileView;
    }
    
    static {
        hstrut10 = new Dimension(10, 1);
        vstrut4 = new Dimension(1, 4);
        vstrut6 = new Dimension(1, 6);
        vstrut8 = new Dimension(1, 8);
        shrinkwrap = new Insets(0, 0, 0, 0);
        WindowsFileChooserUI.PREF_WIDTH = 425;
        WindowsFileChooserUI.PREF_HEIGHT = 245;
        WindowsFileChooserUI.PREF_SIZE = new Dimension(WindowsFileChooserUI.PREF_WIDTH, WindowsFileChooserUI.PREF_HEIGHT);
        WindowsFileChooserUI.MIN_WIDTH = 425;
        WindowsFileChooserUI.MIN_HEIGHT = 245;
        WindowsFileChooserUI.LIST_PREF_WIDTH = 444;
        WindowsFileChooserUI.LIST_PREF_HEIGHT = 138;
        WindowsFileChooserUI.LIST_PREF_SIZE = new Dimension(WindowsFileChooserUI.LIST_PREF_WIDTH, WindowsFileChooserUI.LIST_PREF_HEIGHT);
    }
    
    private class WindowsFileChooserUIAccessor implements FilePane.FileChooserUIAccessor
    {
        @Override
        public JFileChooser getFileChooser() {
            return WindowsFileChooserUI.this.getFileChooser();
        }
        
        @Override
        public BasicDirectoryModel getModel() {
            return WindowsFileChooserUI.this.getModel();
        }
        
        @Override
        public JPanel createList() {
            return WindowsFileChooserUI.this.createList(this.getFileChooser());
        }
        
        @Override
        public JPanel createDetailsView() {
            return WindowsFileChooserUI.this.createDetailsView(this.getFileChooser());
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
            return WindowsFileChooserUI.this.getChangeToParentDirectoryAction();
        }
        
        @Override
        public Action getApproveSelectionAction() {
            return WindowsFileChooserUI.this.getApproveSelectionAction();
        }
        
        @Override
        public Action getNewFolderAction() {
            return WindowsFileChooserUI.this.getNewFolderAction();
        }
        
        @Override
        public MouseListener createDoubleClickListener(final JList list) {
            return BasicFileChooserUI.this.createDoubleClickListener(this.getFileChooser(), list);
        }
        
        @Override
        public ListSelectionListener createListSelectionListener() {
            return WindowsFileChooserUI.this.createListSelectionListener(this.getFileChooser());
        }
    }
    
    protected class WindowsNewFolderAction extends NewFolderAction
    {
    }
    
    protected class SingleClickListener extends MouseAdapter
    {
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
            this.setText(WindowsFileChooserUI.this.getFileChooser().getName(file));
            this.ii.icon = WindowsFileChooserUI.this.getFileChooser().getIcon(file);
            this.ii.depth = WindowsFileChooserUI.this.directoryComboBoxModel.getDepth(n);
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
            this.chooser = WindowsFileChooserUI.this.getFileChooser();
            this.fsv = this.chooser.getFileSystemView();
            final File currentDirectory = WindowsFileChooserUI.this.getFileChooser().getCurrentDirectory();
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
            File canonicalFile;
            try {
                canonicalFile = file.getCanonicalFile();
            }
            catch (final IOException ex) {
                canonicalFile = file;
            }
            try {
                File parentFile;
                final ShellFolder selectedItem = (ShellFolder)(parentFile = (usesShellFolder ? ShellFolder.getShellFolder(canonicalFile) : canonicalFile));
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
        public File getElementAt(final int n) {
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
    
    protected class FilterComboBoxModel extends AbstractListModel<FileFilter> implements ComboBoxModel<FileFilter>, PropertyChangeListener
    {
        protected FileFilter[] filters;
        
        protected FilterComboBoxModel() {
            this.filters = WindowsFileChooserUI.this.getFileChooser().getChoosableFileFilters();
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
                WindowsFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)o);
                this.fireContentsChanged(this, -1, -1);
            }
        }
        
        @Override
        public Object getSelectedItem() {
            final FileFilter fileFilter = WindowsFileChooserUI.this.getFileChooser().getFileFilter();
            boolean b = false;
            if (fileFilter != null) {
                final FileFilter[] filters = this.filters;
                for (int length = filters.length, i = 0; i < length; ++i) {
                    if (filters[i] == fileFilter) {
                        b = true;
                    }
                }
                if (!b) {
                    WindowsFileChooserUI.this.getFileChooser().addChoosableFileFilter(fileFilter);
                }
            }
            return WindowsFileChooserUI.this.getFileChooser().getFileFilter();
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
                return WindowsFileChooserUI.this.getFileChooser().getFileFilter();
            }
            if (this.filters != null) {
                return this.filters[n];
            }
            return null;
        }
    }
    
    protected class DirectoryComboBoxAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            WindowsFileChooserUI.this.getFileChooser().setCurrentDirectory((File)WindowsFileChooserUI.this.directoryComboBox.getSelectedItem());
        }
    }
    
    protected class WindowsFileView extends BasicFileView
    {
        @Override
        public Icon getIcon(final File file) {
            Icon icon = this.getCachedIcon(file);
            if (icon != null) {
                return icon;
            }
            if (file != null) {
                icon = WindowsFileChooserUI.this.getFileChooser().getFileSystemView().getSystemIcon(file);
            }
            if (icon == null) {
                icon = super.getIcon(file);
            }
            this.cacheIcon(file, icon);
            return icon;
        }
    }
}
