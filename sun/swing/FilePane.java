package sun.swing;

import javax.swing.TransferHandler;
import java.awt.Point;
import sun.awt.AWTAccessor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import java.util.Date;
import java.awt.Insets;
import java.text.DateFormat;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.DefaultCellEditor;
import java.util.concurrent.Callable;
import java.util.Comparator;
import javax.swing.DefaultRowSorter;
import javax.swing.table.TableRowSorter;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelListener;
import javax.swing.AbstractListModel;
import java.io.FileNotFoundException;
import sun.awt.shell.ShellFolder;
import javax.swing.JMenuItem;
import javax.swing.AbstractButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import java.beans.PropertyChangeEvent;
import javax.swing.DefaultListSelectionModel;
import java.util.Arrays;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JOptionPane;
import java.text.MessageFormat;
import javax.swing.Icon;
import java.awt.ComponentOrientation;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import javax.swing.LookAndFeel;
import java.awt.AWTKeyStroke;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import java.awt.Dimension;
import javax.swing.RowSorter;
import javax.swing.event.TableModelEvent;
import java.awt.AWTEvent;
import javax.swing.table.TableModel;
import sun.awt.shell.ShellFolderColumnInfo;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.JScrollPane;
import java.awt.event.MouseListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.text.Position;
import javax.swing.ActionMap;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.Locale;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import javax.swing.JFileChooser;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import javax.swing.UIManager;
import java.awt.event.KeyAdapter;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.io.File;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import javax.swing.border.Border;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.Cursor;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.Action;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

public class FilePane extends JPanel implements PropertyChangeListener
{
    public static final String ACTION_APPROVE_SELECTION = "approveSelection";
    public static final String ACTION_CANCEL = "cancelSelection";
    public static final String ACTION_EDIT_FILE_NAME = "editFileName";
    public static final String ACTION_REFRESH = "refresh";
    public static final String ACTION_CHANGE_TO_PARENT_DIRECTORY = "Go Up";
    public static final String ACTION_NEW_FOLDER = "New Folder";
    public static final String ACTION_VIEW_LIST = "viewTypeList";
    public static final String ACTION_VIEW_DETAILS = "viewTypeDetails";
    private Action[] actions;
    public static final int VIEWTYPE_LIST = 0;
    public static final int VIEWTYPE_DETAILS = 1;
    private static final int VIEWTYPE_COUNT = 2;
    private int viewType;
    private JPanel[] viewPanels;
    private JPanel currentViewPanel;
    private String[] viewTypeActionNames;
    private String filesListAccessibleName;
    private String filesDetailsAccessibleName;
    private JPopupMenu contextMenu;
    private JMenu viewMenu;
    private String viewMenuLabelText;
    private String refreshActionLabelText;
    private String newFolderActionLabelText;
    private String kiloByteString;
    private String megaByteString;
    private String gigaByteString;
    private String renameErrorTitleText;
    private String renameErrorText;
    private String renameErrorFileExistsText;
    private static final Cursor waitCursor;
    private final KeyListener detailsKeyListener;
    private FocusListener editorFocusListener;
    private static FocusListener repaintListener;
    private boolean smallIconsView;
    private Border listViewBorder;
    private Color listViewBackground;
    private boolean listViewWindowsStyle;
    private boolean readOnly;
    private boolean fullRowSelection;
    private ListSelectionModel listSelectionModel;
    private JList list;
    private JTable detailsTable;
    private static final int COLUMN_FILENAME = 0;
    private File newFolderFile;
    private FileChooserUIAccessor fileChooserUIAccessor;
    private DetailsTableModel detailsTableModel;
    private DetailsTableRowSorter rowSorter;
    private DetailsTableCellEditor tableCellEditor;
    int lastIndex;
    File editFile;
    JTextField editCell;
    protected Action newFolderAction;
    private Handler handler;
    
    public FilePane(final FileChooserUIAccessor fileChooserUIAccessor) {
        super(new BorderLayout());
        this.viewType = -1;
        this.viewPanels = new JPanel[2];
        this.filesListAccessibleName = null;
        this.filesDetailsAccessibleName = null;
        this.detailsKeyListener = new KeyAdapter() {
            private final long timeFactor;
            private final StringBuilder typedString = new StringBuilder();
            private long lastTime = 1000L;
            
            {
                final Long n = (Long)UIManager.get("Table.timeFactor");
                this.timeFactor = ((n != null) ? n : 1000L);
            }
            
            @Override
            public void keyTyped(final KeyEvent keyEvent) {
                final int size = FilePane.this.getModel().getSize();
                if (FilePane.this.detailsTable == null || size == 0 || keyEvent.isAltDown() || keyEvent.isControlDown() || keyEvent.isMetaDown()) {
                    return;
                }
                final InputMap inputMap = FilePane.this.detailsTable.getInputMap(1);
                final KeyStroke keyStrokeForEvent = KeyStroke.getKeyStrokeForEvent(keyEvent);
                if (inputMap != null && inputMap.get(keyStrokeForEvent) != null) {
                    return;
                }
                int leadSelectionIndex = FilePane.this.detailsTable.getSelectionModel().getLeadSelectionIndex();
                if (leadSelectionIndex < 0) {
                    leadSelectionIndex = 0;
                }
                if (leadSelectionIndex >= size) {
                    leadSelectionIndex = size - 1;
                }
                final char keyChar = keyEvent.getKeyChar();
                final long when = keyEvent.getWhen();
                if (when - this.lastTime < this.timeFactor) {
                    if (this.typedString.length() == 1 && this.typedString.charAt(0) == keyChar) {
                        ++leadSelectionIndex;
                    }
                    else {
                        this.typedString.append(keyChar);
                    }
                }
                else {
                    ++leadSelectionIndex;
                    this.typedString.setLength(0);
                    this.typedString.append(keyChar);
                }
                this.lastTime = when;
                if (leadSelectionIndex >= size) {
                    leadSelectionIndex = 0;
                }
                int n = this.getNextMatch(leadSelectionIndex, size - 1);
                if (n < 0 && leadSelectionIndex > 0) {
                    n = this.getNextMatch(0, leadSelectionIndex - 1);
                }
                if (n >= 0) {
                    FilePane.this.detailsTable.getSelectionModel().setSelectionInterval(n, n);
                    FilePane.this.detailsTable.scrollRectToVisible(FilePane.this.detailsTable.getCellRect(n, FilePane.this.detailsTable.convertColumnIndexToView(0), false));
                }
            }
            
            private int getNextMatch(final int n, final int n2) {
                final BasicDirectoryModel model = FilePane.this.getModel();
                final JFileChooser fileChooser = FilePane.this.getFileChooser();
                final DetailsTableRowSorter access$100 = FilePane.this.getRowSorter();
                final String lowerCase = this.typedString.toString().toLowerCase();
                for (int i = n; i <= n2; ++i) {
                    if (fileChooser.getName((File)model.getElementAt(access$100.convertRowIndexToModel(i))).toLowerCase().startsWith(lowerCase)) {
                        return i;
                    }
                }
                return -1;
            }
        };
        this.editorFocusListener = new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent focusEvent) {
                if (!focusEvent.isTemporary()) {
                    FilePane.this.applyEdit();
                }
            }
        };
        this.smallIconsView = false;
        this.fullRowSelection = false;
        this.lastIndex = -1;
        this.editFile = null;
        this.editCell = null;
        this.fileChooserUIAccessor = fileChooserUIAccessor;
        this.installDefaults();
        this.createActionMap();
    }
    
    public void uninstallUI() {
        if (this.getModel() != null) {
            this.getModel().removePropertyChangeListener(this);
        }
    }
    
    protected JFileChooser getFileChooser() {
        return this.fileChooserUIAccessor.getFileChooser();
    }
    
    protected BasicDirectoryModel getModel() {
        return this.fileChooserUIAccessor.getModel();
    }
    
    public int getViewType() {
        return this.viewType;
    }
    
    public void setViewType(final int viewType) {
        if (viewType == this.viewType) {
            return;
        }
        final int viewType2 = this.viewType;
        this.viewType = viewType;
        JPanel panel = null;
        Component component = null;
        switch (viewType) {
            case 0: {
                if (this.viewPanels[viewType] == null) {
                    panel = this.fileChooserUIAccessor.createList();
                    if (panel == null) {
                        panel = this.createList();
                    }
                    this.list = (JList)this.findChildComponent(panel, JList.class);
                    if (this.listSelectionModel == null) {
                        this.listSelectionModel = this.list.getSelectionModel();
                        if (this.detailsTable != null) {
                            this.detailsTable.setSelectionModel(this.listSelectionModel);
                        }
                    }
                    else {
                        this.list.setSelectionModel(this.listSelectionModel);
                    }
                }
                this.list.setLayoutOrientation(1);
                component = this.list;
                break;
            }
            case 1: {
                if (this.viewPanels[viewType] == null) {
                    panel = this.fileChooserUIAccessor.createDetailsView();
                    if (panel == null) {
                        panel = this.createDetailsView();
                    }
                    (this.detailsTable = (JTable)this.findChildComponent(panel, JTable.class)).setRowHeight(Math.max(this.detailsTable.getFont().getSize() + 4, 17));
                    if (this.listSelectionModel != null) {
                        this.detailsTable.setSelectionModel(this.listSelectionModel);
                    }
                }
                component = this.detailsTable;
                break;
            }
        }
        if (panel != null) {
            recursivelySetInheritsPopupMenu(this.viewPanels[viewType] = panel, true);
        }
        boolean b = false;
        if (this.currentViewPanel != null) {
            final Component permanentFocusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            b = (permanentFocusOwner == this.detailsTable || permanentFocusOwner == this.list);
            this.remove(this.currentViewPanel);
        }
        this.add(this.currentViewPanel = this.viewPanels[viewType], "Center");
        if (b && component != null) {
            component.requestFocusInWindow();
        }
        this.revalidate();
        this.repaint();
        this.updateViewMenu();
        this.firePropertyChange("viewType", viewType2, viewType);
    }
    
    public Action getViewTypeAction(final int n) {
        return new ViewTypeAction(n);
    }
    
    private static void recursivelySetInheritsPopupMenu(final Container container, final boolean inheritsPopupMenu) {
        if (container instanceof JComponent) {
            ((JComponent)container).setInheritsPopupMenu(inheritsPopupMenu);
        }
        for (int componentCount = container.getComponentCount(), i = 0; i < componentCount; ++i) {
            recursivelySetInheritsPopupMenu((Container)container.getComponent(i), inheritsPopupMenu);
        }
    }
    
    protected void installDefaults() {
        final Locale locale = this.getFileChooser().getLocale();
        this.listViewBorder = UIManager.getBorder("FileChooser.listViewBorder");
        this.listViewBackground = UIManager.getColor("FileChooser.listViewBackground");
        this.listViewWindowsStyle = UIManager.getBoolean("FileChooser.listViewWindowsStyle");
        this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
        this.viewMenuLabelText = UIManager.getString("FileChooser.viewMenuLabelText", locale);
        this.refreshActionLabelText = UIManager.getString("FileChooser.refreshActionLabelText", locale);
        this.newFolderActionLabelText = UIManager.getString("FileChooser.newFolderActionLabelText", locale);
        (this.viewTypeActionNames = new String[2])[0] = UIManager.getString("FileChooser.listViewActionLabelText", locale);
        this.viewTypeActionNames[1] = UIManager.getString("FileChooser.detailsViewActionLabelText", locale);
        this.kiloByteString = UIManager.getString("FileChooser.fileSizeKiloBytes", locale);
        this.megaByteString = UIManager.getString("FileChooser.fileSizeMegaBytes", locale);
        this.gigaByteString = UIManager.getString("FileChooser.fileSizeGigaBytes", locale);
        this.fullRowSelection = UIManager.getBoolean("FileView.fullRowSelection");
        this.filesListAccessibleName = UIManager.getString("FileChooser.filesListAccessibleName", locale);
        this.filesDetailsAccessibleName = UIManager.getString("FileChooser.filesDetailsAccessibleName", locale);
        this.renameErrorTitleText = UIManager.getString("FileChooser.renameErrorTitleText", locale);
        this.renameErrorText = UIManager.getString("FileChooser.renameErrorText", locale);
        this.renameErrorFileExistsText = UIManager.getString("FileChooser.renameErrorFileExistsText", locale);
    }
    
    public Action[] getActions() {
        if (this.actions == null) {
            final ArrayList list = new ArrayList(8);
            class FilePaneAction extends AbstractAction
            {
                FilePaneAction(final FilePane filePane, final String s) {
                    this(filePane, s, s);
                }
                
                FilePaneAction(final String s, final String s2) {
                    super(s);
                    this.putValue("ActionCommandKey", s2);
                }
                
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    final String s = (String)this.getValue("ActionCommandKey");
                    if (s == "cancelSelection") {
                        if (FilePane.this.editFile != null) {
                            FilePane.this.cancelEdit();
                        }
                        else {
                            FilePane.this.getFileChooser().cancelSelection();
                        }
                    }
                    else if (s == "editFileName") {
                        final JFileChooser fileChooser = FilePane.this.getFileChooser();
                        final int minSelectionIndex = FilePane.this.listSelectionModel.getMinSelectionIndex();
                        if (minSelectionIndex >= 0 && FilePane.this.editFile == null && (!fileChooser.isMultiSelectionEnabled() || fileChooser.getSelectedFiles().length <= 1)) {
                            FilePane.this.editFileName(minSelectionIndex);
                        }
                    }
                    else if (s == "refresh") {
                        FilePane.this.getFileChooser().rescanCurrentDirectory();
                    }
                }
                
                @Override
                public boolean isEnabled() {
                    final String s = (String)this.getValue("ActionCommandKey");
                    if (s == "cancelSelection") {
                        return FilePane.this.getFileChooser().isEnabled();
                    }
                    return s != "editFileName" || (!FilePane.this.readOnly && FilePane.this.getFileChooser().isEnabled());
                }
            }
            list.add(new FilePaneAction("cancelSelection"));
            list.add(new FilePaneAction("editFileName"));
            list.add(new FilePaneAction(this.refreshActionLabelText, "refresh"));
            final Action approveSelectionAction = this.fileChooserUIAccessor.getApproveSelectionAction();
            if (approveSelectionAction != null) {
                list.add(approveSelectionAction);
            }
            final Action changeToParentDirectoryAction = this.fileChooserUIAccessor.getChangeToParentDirectoryAction();
            if (changeToParentDirectoryAction != null) {
                list.add(changeToParentDirectoryAction);
            }
            final Action newFolderAction = this.getNewFolderAction();
            if (newFolderAction != null) {
                list.add(newFolderAction);
            }
            final Action viewTypeAction = this.getViewTypeAction(0);
            if (viewTypeAction != null) {
                list.add(viewTypeAction);
            }
            final Action viewTypeAction2 = this.getViewTypeAction(1);
            if (viewTypeAction2 != null) {
                list.add(viewTypeAction2);
            }
            this.actions = list.toArray(new Action[list.size()]);
        }
        return this.actions;
    }
    
    protected void createActionMap() {
        addActionsToMap(super.getActionMap(), this.getActions());
    }
    
    public static void addActionsToMap(final ActionMap actionMap, final Action[] array) {
        if (actionMap != null && array != null) {
            for (final Action action : array) {
                String s = (String)action.getValue("ActionCommandKey");
                if (s == null) {
                    s = (String)action.getValue("Name");
                }
                actionMap.put(s, action);
            }
        }
    }
    
    private void updateListRowCount(final JList list) {
        if (this.smallIconsView) {
            list.setVisibleRowCount(this.getModel().getSize() / 3);
        }
        else {
            list.setVisibleRowCount(-1);
        }
    }
    
    public JPanel createList() {
        final JPanel panel = new JPanel(new BorderLayout());
        final JFileChooser fileChooser = this.getFileChooser();
        final JList<Object> list = new JList<Object>() {
            @Override
            public int getNextMatch(final String s, final int n, final Position.Bias bias) {
                final ListModel<Object> model = this.getModel();
                final int size = model.getSize();
                if (s == null || n < 0 || n >= size) {
                    throw new IllegalArgumentException();
                }
                final boolean b = bias == Position.Bias.Backward;
                int n2 = n;
                while (true) {
                    if (b) {
                        if (n2 < 0) {
                            break;
                        }
                    }
                    else if (n2 >= size) {
                        break;
                    }
                    if (fileChooser.getName((File)model.getElementAt(n2)).regionMatches(true, 0, s, 0, s.length())) {
                        return n2;
                    }
                    n2 += (b ? -1 : 1);
                }
                return -1;
            }
        };
        list.setCellRenderer(new FileRenderer());
        list.setLayoutOrientation(1);
        list.putClientProperty("List.isFileList", Boolean.TRUE);
        if (this.listViewWindowsStyle) {
            list.addFocusListener(FilePane.repaintListener);
        }
        this.updateListRowCount(list);
        this.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(final ListDataEvent listDataEvent) {
                FilePane.this.updateListRowCount(list);
            }
            
            @Override
            public void intervalRemoved(final ListDataEvent listDataEvent) {
                FilePane.this.updateListRowCount(list);
            }
            
            @Override
            public void contentsChanged(final ListDataEvent listDataEvent) {
                if (FilePane.this.isShowing()) {
                    FilePane.this.clearSelection();
                }
                FilePane.this.updateListRowCount(list);
            }
        });
        this.getModel().addPropertyChangeListener(this);
        if (fileChooser.isMultiSelectionEnabled()) {
            list.setSelectionMode(2);
        }
        else {
            list.setSelectionMode(0);
        }
        list.setModel(new SortableListModel());
        list.addListSelectionListener(this.createListSelectionListener());
        list.addMouseListener(this.getMouseHandler());
        final JScrollPane scrollPane = new JScrollPane(list);
        if (this.listViewBackground != null) {
            list.setBackground(this.listViewBackground);
        }
        if (this.listViewBorder != null) {
            scrollPane.setBorder(this.listViewBorder);
        }
        list.putClientProperty("AccessibleName", this.filesListAccessibleName);
        panel.add(scrollPane, "Center");
        return panel;
    }
    
    private DetailsTableModel getDetailsTableModel() {
        if (this.detailsTableModel == null) {
            this.detailsTableModel = new DetailsTableModel(this.getFileChooser());
        }
        return this.detailsTableModel;
    }
    
    private void updateDetailsColumnModel(final JTable table) {
        if (table != null) {
            final ShellFolderColumnInfo[] columns = this.detailsTableModel.getColumns();
            final DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
            for (int i = 0; i < columns.length; ++i) {
                final ShellFolderColumnInfo shellFolderColumnInfo = columns[i];
                final TableColumn tableColumn = new TableColumn(i);
                String title = shellFolderColumnInfo.getTitle();
                if (title != null && title.startsWith("FileChooser.") && title.endsWith("HeaderText")) {
                    final String string = UIManager.getString(title, table.getLocale());
                    if (string != null) {
                        title = string;
                    }
                }
                tableColumn.setHeaderValue(title);
                final Integer width = shellFolderColumnInfo.getWidth();
                if (width != null) {
                    tableColumn.setPreferredWidth(width);
                }
                columnModel.addColumn(tableColumn);
            }
            if (!this.readOnly && columnModel.getColumnCount() > 0) {
                columnModel.getColumn(0).setCellEditor(this.getDetailsTableCellEditor());
            }
            table.setColumnModel(columnModel);
        }
    }
    
    private DetailsTableRowSorter getRowSorter() {
        if (this.rowSorter == null) {
            this.rowSorter = new DetailsTableRowSorter();
        }
        return this.rowSorter;
    }
    
    private DetailsTableCellEditor getDetailsTableCellEditor() {
        if (this.tableCellEditor == null) {
            this.tableCellEditor = new DetailsTableCellEditor(new JTextField());
        }
        return this.tableCellEditor;
    }
    
    public JPanel createDetailsView() {
        final JFileChooser fileChooser = this.getFileChooser();
        final JPanel panel = new JPanel(new BorderLayout());
        final JTable table = new JTable(this.getDetailsTableModel()) {
            @Override
            protected boolean processKeyBinding(final KeyStroke keyStroke, final KeyEvent keyEvent, final int n, final boolean b) {
                if (keyEvent.getKeyCode() == 27 && this.getCellEditor() == null) {
                    fileChooser.dispatchEvent(keyEvent);
                    return true;
                }
                return super.processKeyBinding(keyStroke, keyEvent, n, b);
            }
            
            @Override
            public void tableChanged(final TableModelEvent tableModelEvent) {
                super.tableChanged(tableModelEvent);
                if (tableModelEvent.getFirstRow() == -1) {
                    FilePane.this.updateDetailsColumnModel(this);
                }
            }
        };
        table.setRowSorter((RowSorter<? extends TableModel>)this.getRowSorter());
        table.setAutoCreateColumnsFromModel(false);
        table.setComponentOrientation(fileChooser.getComponentOrientation());
        table.setAutoResizeMode(0);
        table.setShowGrid(false);
        table.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
        table.addKeyListener(this.detailsKeyListener);
        table.setFont(this.list.getFont());
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setDefaultRenderer(new AlignableTableHeaderRenderer(table.getTableHeader().getDefaultRenderer()));
        table.setDefaultRenderer(Object.class, new DetailsTableCellRenderer(fileChooser));
        table.getColumnModel().getSelectionModel().setSelectionMode(0);
        table.addMouseListener(this.getMouseHandler());
        table.putClientProperty("Table.isFileList", Boolean.TRUE);
        if (this.listViewWindowsStyle) {
            table.addFocusListener(FilePane.repaintListener);
        }
        final ActionMap uiActionMap = SwingUtilities.getUIActionMap(table);
        uiActionMap.remove("selectNextRowCell");
        uiActionMap.remove("selectPreviousRowCell");
        uiActionMap.remove("selectNextColumnCell");
        uiActionMap.remove("selectPreviousColumnCell");
        table.setFocusTraversalKeys(0, null);
        table.setFocusTraversalKeys(1, null);
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setComponentOrientation(fileChooser.getComponentOrientation());
        LookAndFeel.installColors(scrollPane.getViewport(), "Table.background", "Table.foreground");
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent componentEvent) {
                final JScrollPane scrollPane = (JScrollPane)componentEvent.getComponent();
                FilePane.this.fixNameColumnWidth(scrollPane.getViewport().getSize().width);
                scrollPane.removeComponentListener(this);
            }
        });
        scrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent mouseEvent) {
                final JTable table = (JTable)((JScrollPane)mouseEvent.getComponent()).getViewport().getView();
                if (!mouseEvent.isShiftDown() || table.getSelectionModel().getSelectionMode() == 0) {
                    FilePane.this.clearSelection();
                    final TableCellEditor cellEditor = table.getCellEditor();
                    if (cellEditor != null) {
                        cellEditor.stopCellEditing();
                    }
                }
            }
        });
        table.setForeground(this.list.getForeground());
        table.setBackground(this.list.getBackground());
        if (this.listViewBorder != null) {
            scrollPane.setBorder(this.listViewBorder);
        }
        panel.add(scrollPane, "Center");
        this.detailsTableModel.fireTableStructureChanged();
        table.putClientProperty("AccessibleName", this.filesDetailsAccessibleName);
        return panel;
    }
    
    private void fixNameColumnWidth(final int n) {
        final TableColumn column = this.detailsTable.getColumnModel().getColumn(0);
        final int width = this.detailsTable.getPreferredSize().width;
        if (width < n) {
            column.setPreferredWidth(column.getPreferredWidth() + n - width);
        }
    }
    
    public ListSelectionListener createListSelectionListener() {
        return this.fileChooserUIAccessor.createListSelectionListener();
    }
    
    private int getEditIndex() {
        return this.lastIndex;
    }
    
    private void setEditIndex(final int lastIndex) {
        this.lastIndex = lastIndex;
    }
    
    private void resetEditIndex() {
        this.lastIndex = -1;
    }
    
    private void cancelEdit() {
        if (this.editFile != null) {
            this.editFile = null;
            this.list.remove(this.editCell);
            this.repaint();
        }
        else if (this.detailsTable != null && this.detailsTable.isEditing()) {
            this.detailsTable.getCellEditor().cancelCellEditing();
        }
    }
    
    private void editFileName(final int n) {
        final JFileChooser fileChooser = this.getFileChooser();
        final File currentDirectory = fileChooser.getCurrentDirectory();
        if (this.readOnly || !this.canWrite(currentDirectory)) {
            return;
        }
        this.ensureIndexIsVisible(n);
        switch (this.viewType) {
            case 0: {
                this.editFile = (File)this.getModel().getElementAt(this.getRowSorter().convertRowIndexToModel(n));
                final Rectangle cellBounds = this.list.getCellBounds(n, n);
                if (this.editCell == null) {
                    (this.editCell = new JTextField()).setName("Tree.cellEditor");
                    this.editCell.addActionListener(new EditActionListener());
                    this.editCell.addFocusListener(this.editorFocusListener);
                    this.editCell.setNextFocusableComponent(this.list);
                }
                this.list.add(this.editCell);
                this.editCell.setText(fileChooser.getName(this.editFile));
                final ComponentOrientation componentOrientation = this.list.getComponentOrientation();
                this.editCell.setComponentOrientation(componentOrientation);
                final Icon icon = fileChooser.getIcon(this.editFile);
                final int n2 = (icon == null) ? 20 : (icon.getIconWidth() + 4);
                if (componentOrientation.isLeftToRight()) {
                    this.editCell.setBounds(n2 + cellBounds.x, cellBounds.y, cellBounds.width - n2, cellBounds.height);
                }
                else {
                    this.editCell.setBounds(cellBounds.x, cellBounds.y, cellBounds.width - n2, cellBounds.height);
                }
                this.editCell.requestFocus();
                this.editCell.selectAll();
                break;
            }
            case 1: {
                this.detailsTable.editCellAt(n, 0);
                break;
            }
        }
    }
    
    private void applyEdit() {
        if (this.editFile != null && this.editFile.exists()) {
            final JFileChooser fileChooser = this.getFileChooser();
            final String name = fileChooser.getName(this.editFile);
            final String name2 = this.editFile.getName();
            final String trim = this.editCell.getText().trim();
            if (!trim.equals(name)) {
                String string = trim;
                final int length = name2.length();
                final int length2 = name.length();
                if (length > length2 && name2.charAt(length2) == '.') {
                    string = trim + name2.substring(length2);
                }
                final FileSystemView fileSystemView = fileChooser.getFileSystemView();
                final File fileObject = fileSystemView.createFileObject(this.editFile.getParentFile(), string);
                if (fileObject.exists()) {
                    JOptionPane.showMessageDialog(fileChooser, MessageFormat.format(this.renameErrorFileExistsText, name2), this.renameErrorTitleText, 0);
                }
                else if (this.getModel().renameFile(this.editFile, fileObject)) {
                    if (fileSystemView.isParent(fileChooser.getCurrentDirectory(), fileObject)) {
                        if (fileChooser.isMultiSelectionEnabled()) {
                            fileChooser.setSelectedFiles(new File[] { fileObject });
                        }
                        else {
                            fileChooser.setSelectedFile(fileObject);
                        }
                    }
                }
                else {
                    JOptionPane.showMessageDialog(fileChooser, MessageFormat.format(this.renameErrorText, name2), this.renameErrorTitleText, 0);
                }
            }
        }
        if (this.detailsTable != null && this.detailsTable.isEditing()) {
            this.detailsTable.getCellEditor().stopCellEditing();
        }
        this.cancelEdit();
    }
    
    public Action getNewFolderAction() {
        if (!this.readOnly && this.newFolderAction == null) {
            this.newFolderAction = new AbstractAction(this.newFolderActionLabelText) {
                private Action basicNewFolderAction;
                
                {
                    this.putValue("ActionCommandKey", "New Folder");
                    final File currentDirectory = FilePane.this.getFileChooser().getCurrentDirectory();
                    if (currentDirectory != null) {
                        this.setEnabled(FilePane.this.canWrite(currentDirectory));
                    }
                }
                
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    if (this.basicNewFolderAction == null) {
                        this.basicNewFolderAction = FilePane.this.fileChooserUIAccessor.getNewFolderAction();
                    }
                    final JFileChooser fileChooser = FilePane.this.getFileChooser();
                    final File selectedFile = fileChooser.getSelectedFile();
                    this.basicNewFolderAction.actionPerformed(actionEvent);
                    final File selectedFile2 = fileChooser.getSelectedFile();
                    if (selectedFile2 != null && !selectedFile2.equals(selectedFile) && selectedFile2.isDirectory()) {
                        FilePane.this.newFolderFile = selectedFile2;
                    }
                }
            };
        }
        return this.newFolderAction;
    }
    
    void setFileSelected() {
        if (this.getFileChooser().isMultiSelectionEnabled() && !this.isDirectorySelected()) {
            final File[] selectedFiles = this.getFileChooser().getSelectedFiles();
            final Object[] selectedValues = this.list.getSelectedValues();
            this.listSelectionModel.setValueIsAdjusting(true);
            try {
                final int leadSelectionIndex = this.listSelectionModel.getLeadSelectionIndex();
                final int anchorSelectionIndex = this.listSelectionModel.getAnchorSelectionIndex();
                Arrays.sort(selectedFiles);
                Arrays.sort(selectedValues);
                int i = 0;
                int j = 0;
                while (i < selectedFiles.length && j < selectedValues.length) {
                    final int compareTo = selectedFiles[i].compareTo((File)selectedValues[j]);
                    if (compareTo < 0) {
                        this.doSelectFile(selectedFiles[i++]);
                    }
                    else if (compareTo > 0) {
                        this.doDeselectFile(selectedValues[j++]);
                    }
                    else {
                        ++i;
                        ++j;
                    }
                }
                while (i < selectedFiles.length) {
                    this.doSelectFile(selectedFiles[i++]);
                }
                while (j < selectedValues.length) {
                    this.doDeselectFile(selectedValues[j++]);
                }
                if (this.listSelectionModel instanceof DefaultListSelectionModel) {
                    ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(leadSelectionIndex);
                    this.listSelectionModel.setAnchorSelectionIndex(anchorSelectionIndex);
                }
            }
            finally {
                this.listSelectionModel.setValueIsAdjusting(false);
            }
        }
        else {
            final JFileChooser fileChooser = this.getFileChooser();
            File file;
            if (this.isDirectorySelected()) {
                file = this.getDirectory();
            }
            else {
                file = fileChooser.getSelectedFile();
            }
            final int index;
            if (file != null && (index = this.getModel().indexOf(file)) >= 0) {
                final int convertRowIndexToView = this.getRowSorter().convertRowIndexToView(index);
                this.listSelectionModel.setSelectionInterval(convertRowIndexToView, convertRowIndexToView);
                this.ensureIndexIsVisible(convertRowIndexToView);
            }
            else {
                this.clearSelection();
            }
        }
    }
    
    private void doSelectFile(final File file) {
        final int index = this.getModel().indexOf(file);
        if (index >= 0) {
            final int convertRowIndexToView = this.getRowSorter().convertRowIndexToView(index);
            this.listSelectionModel.addSelectionInterval(convertRowIndexToView, convertRowIndexToView);
        }
    }
    
    private void doDeselectFile(final Object o) {
        final int convertRowIndexToView = this.getRowSorter().convertRowIndexToView(this.getModel().indexOf(o));
        this.listSelectionModel.removeSelectionInterval(convertRowIndexToView, convertRowIndexToView);
    }
    
    private void doSelectedFileChanged(final PropertyChangeEvent propertyChangeEvent) {
        this.applyEdit();
        final File file = (File)propertyChangeEvent.getNewValue();
        final JFileChooser fileChooser = this.getFileChooser();
        if (file != null && ((fileChooser.isFileSelectionEnabled() && !file.isDirectory()) || (file.isDirectory() && fileChooser.isDirectorySelectionEnabled()))) {
            this.setFileSelected();
        }
    }
    
    private void doSelectedFilesChanged(final PropertyChangeEvent propertyChangeEvent) {
        this.applyEdit();
        final File[] array = (File[])propertyChangeEvent.getNewValue();
        final JFileChooser fileChooser = this.getFileChooser();
        if (array != null && array.length > 0 && (array.length > 1 || fileChooser.isDirectorySelectionEnabled() || !array[0].isDirectory())) {
            this.setFileSelected();
        }
    }
    
    private void doDirectoryChanged(final PropertyChangeEvent propertyChangeEvent) {
        this.getDetailsTableModel().updateColumnInfo();
        final JFileChooser fileChooser = this.getFileChooser();
        final FileSystemView fileSystemView = fileChooser.getFileSystemView();
        this.applyEdit();
        this.resetEditIndex();
        this.ensureIndexIsVisible(0);
        final File currentDirectory = fileChooser.getCurrentDirectory();
        if (currentDirectory != null) {
            if (!this.readOnly) {
                this.getNewFolderAction().setEnabled(this.canWrite(currentDirectory));
            }
            this.fileChooserUIAccessor.getChangeToParentDirectoryAction().setEnabled(!fileSystemView.isRoot(currentDirectory));
        }
        if (this.list != null) {
            this.list.clearSelection();
        }
    }
    
    private void doFilterChanged(final PropertyChangeEvent propertyChangeEvent) {
        this.applyEdit();
        this.resetEditIndex();
        this.clearSelection();
    }
    
    private void doFileSelectionModeChanged(final PropertyChangeEvent propertyChangeEvent) {
        this.applyEdit();
        this.resetEditIndex();
        this.clearSelection();
    }
    
    private void doMultiSelectionChanged(final PropertyChangeEvent propertyChangeEvent) {
        if (this.getFileChooser().isMultiSelectionEnabled()) {
            this.listSelectionModel.setSelectionMode(2);
        }
        else {
            this.listSelectionModel.setSelectionMode(0);
            this.clearSelection();
            this.getFileChooser().setSelectedFiles(null);
        }
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (this.viewType == -1) {
            this.setViewType(0);
        }
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName.equals("SelectedFileChangedProperty")) {
            this.doSelectedFileChanged(propertyChangeEvent);
        }
        else if (propertyName.equals("SelectedFilesChangedProperty")) {
            this.doSelectedFilesChanged(propertyChangeEvent);
        }
        else if (propertyName.equals("directoryChanged")) {
            this.doDirectoryChanged(propertyChangeEvent);
        }
        else if (propertyName.equals("fileFilterChanged")) {
            this.doFilterChanged(propertyChangeEvent);
        }
        else if (propertyName.equals("fileSelectionChanged")) {
            this.doFileSelectionModeChanged(propertyChangeEvent);
        }
        else if (propertyName.equals("MultiSelectionEnabledChangedProperty")) {
            this.doMultiSelectionChanged(propertyChangeEvent);
        }
        else if (propertyName.equals("CancelSelection")) {
            this.applyEdit();
        }
        else if (propertyName.equals("busy")) {
            this.setCursor(((boolean)propertyChangeEvent.getNewValue()) ? FilePane.waitCursor : null);
        }
        else if (propertyName.equals("componentOrientation")) {
            final ComponentOrientation componentOrientation = (ComponentOrientation)propertyChangeEvent.getNewValue();
            final JFileChooser fileChooser = (JFileChooser)propertyChangeEvent.getSource();
            if (componentOrientation != propertyChangeEvent.getOldValue()) {
                fileChooser.applyComponentOrientation(componentOrientation);
            }
            if (this.detailsTable != null) {
                this.detailsTable.setComponentOrientation(componentOrientation);
                this.detailsTable.getParent().getParent().setComponentOrientation(componentOrientation);
            }
        }
    }
    
    private void ensureIndexIsVisible(final int n) {
        if (n >= 0) {
            if (this.list != null) {
                this.list.ensureIndexIsVisible(n);
            }
            if (this.detailsTable != null) {
                this.detailsTable.scrollRectToVisible(this.detailsTable.getCellRect(n, 0, true));
            }
        }
    }
    
    public void ensureFileIsVisible(final JFileChooser fileChooser, final File file) {
        final int index = this.getModel().indexOf(file);
        if (index >= 0) {
            this.ensureIndexIsVisible(this.getRowSorter().convertRowIndexToView(index));
        }
    }
    
    public void rescanCurrentDirectory() {
        this.getModel().validateFileCache();
    }
    
    public void clearSelection() {
        if (this.listSelectionModel != null) {
            this.listSelectionModel.clearSelection();
            if (this.listSelectionModel instanceof DefaultListSelectionModel) {
                ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(0);
                this.listSelectionModel.setAnchorSelectionIndex(0);
            }
        }
    }
    
    public JMenu getViewMenu() {
        if (this.viewMenu == null) {
            this.viewMenu = new JMenu(this.viewMenuLabelText);
            final ButtonGroup buttonGroup = new ButtonGroup();
            for (int i = 0; i < 2; ++i) {
                final JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem(new ViewTypeAction(i));
                buttonGroup.add(radioButtonMenuItem);
                this.viewMenu.add(radioButtonMenuItem);
            }
            this.updateViewMenu();
        }
        return this.viewMenu;
    }
    
    private void updateViewMenu() {
        if (this.viewMenu != null) {
            for (final Component component : this.viewMenu.getMenuComponents()) {
                if (component instanceof JRadioButtonMenuItem) {
                    final JRadioButtonMenuItem radioButtonMenuItem = (JRadioButtonMenuItem)component;
                    if (((ViewTypeAction)radioButtonMenuItem.getAction()).viewType == this.viewType) {
                        radioButtonMenuItem.setSelected(true);
                    }
                }
            }
        }
    }
    
    @Override
    public JPopupMenu getComponentPopupMenu() {
        final JPopupMenu componentPopupMenu = this.getFileChooser().getComponentPopupMenu();
        if (componentPopupMenu != null) {
            return componentPopupMenu;
        }
        final JMenu viewMenu = this.getViewMenu();
        if (this.contextMenu == null) {
            this.contextMenu = new JPopupMenu();
            if (viewMenu != null) {
                this.contextMenu.add(viewMenu);
                if (this.listViewWindowsStyle) {
                    this.contextMenu.addSeparator();
                }
            }
            final ActionMap actionMap = this.getActionMap();
            final Action value = actionMap.get("refresh");
            final Action value2 = actionMap.get("New Folder");
            if (value != null) {
                this.contextMenu.add(value);
                if (this.listViewWindowsStyle && value2 != null) {
                    this.contextMenu.addSeparator();
                }
            }
            if (value2 != null) {
                this.contextMenu.add(value2);
            }
        }
        if (viewMenu != null) {
            viewMenu.getPopupMenu().setInvoker(viewMenu);
        }
        return this.contextMenu;
    }
    
    protected Handler getMouseHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected boolean isDirectorySelected() {
        return this.fileChooserUIAccessor.isDirectorySelected();
    }
    
    protected File getDirectory() {
        return this.fileChooserUIAccessor.getDirectory();
    }
    
    private Component findChildComponent(final Container container, final Class clazz) {
        for (int componentCount = container.getComponentCount(), i = 0; i < componentCount; ++i) {
            final Component component = container.getComponent(i);
            if (clazz.isInstance(component)) {
                return component;
            }
            if (component instanceof Container) {
                final Component childComponent = this.findChildComponent((Container)component, clazz);
                if (childComponent != null) {
                    return childComponent;
                }
            }
        }
        return null;
    }
    
    public boolean canWrite(final File file) {
        if (!file.exists()) {
            return false;
        }
        try {
            if (file instanceof ShellFolder) {
                return file.canWrite();
            }
            if (usesShellFolder(this.getFileChooser())) {
                try {
                    return ShellFolder.getShellFolder(file).canWrite();
                }
                catch (final FileNotFoundException ex) {
                    return false;
                }
            }
            return file.canWrite();
        }
        catch (final SecurityException ex2) {
            return false;
        }
    }
    
    public static boolean usesShellFolder(final JFileChooser fileChooser) {
        final Boolean b = (Boolean)fileChooser.getClientProperty("FileChooser.useShellFolder");
        return (b == null) ? fileChooser.getFileSystemView().equals(FileSystemView.getFileSystemView()) : b;
    }
    
    static {
        waitCursor = Cursor.getPredefinedCursor(3);
        FilePane.repaintListener = new FocusListener() {
            @Override
            public void focusGained(final FocusEvent focusEvent) {
                this.repaintSelection(focusEvent.getSource());
            }
            
            @Override
            public void focusLost(final FocusEvent focusEvent) {
                this.repaintSelection(focusEvent.getSource());
            }
            
            private void repaintSelection(final Object o) {
                if (o instanceof JList) {
                    this.repaintListSelection((JList)o);
                }
                else if (o instanceof JTable) {
                    this.repaintTableSelection((JTable)o);
                }
            }
            
            private void repaintListSelection(final JList list) {
                for (final int n : list.getSelectedIndices()) {
                    list.repaint(list.getCellBounds(n, n));
                }
            }
            
            private void repaintTableSelection(final JTable table) {
                final int minSelectionIndex = table.getSelectionModel().getMinSelectionIndex();
                final int maxSelectionIndex = table.getSelectionModel().getMaxSelectionIndex();
                if (minSelectionIndex == -1 || maxSelectionIndex == -1) {
                    return;
                }
                final int convertColumnIndexToView = table.convertColumnIndexToView(0);
                table.repaint(table.getCellRect(minSelectionIndex, convertColumnIndexToView, false).union(table.getCellRect(maxSelectionIndex, convertColumnIndexToView, false)));
            }
        };
    }
    
    class ViewTypeAction extends AbstractAction
    {
        private int viewType;
        
        ViewTypeAction(final int viewType) {
            super(FilePane.this.viewTypeActionNames[viewType]);
            String s = null;
            switch (this.viewType = viewType) {
                case 0: {
                    s = "viewTypeList";
                    break;
                }
                case 1: {
                    s = "viewTypeDetails";
                    break;
                }
                default: {
                    s = (String)this.getValue("Name");
                    break;
                }
            }
            this.putValue("ActionCommandKey", s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            FilePane.this.setViewType(this.viewType);
        }
    }
    
    private class SortableListModel extends AbstractListModel<Object> implements TableModelListener, RowSorterListener
    {
        public SortableListModel() {
            FilePane.this.getDetailsTableModel().addTableModelListener(this);
            FilePane.this.getRowSorter().addRowSorterListener(this);
        }
        
        @Override
        public int getSize() {
            return FilePane.this.getModel().getSize();
        }
        
        @Override
        public Object getElementAt(final int n) {
            return FilePane.this.getModel().getElementAt(FilePane.this.getRowSorter().convertRowIndexToModel(n));
        }
        
        @Override
        public void tableChanged(final TableModelEvent tableModelEvent) {
            this.fireContentsChanged(this, 0, this.getSize());
        }
        
        @Override
        public void sorterChanged(final RowSorterEvent rowSorterEvent) {
            this.fireContentsChanged(this, 0, this.getSize());
        }
    }
    
    class DetailsTableModel extends AbstractTableModel implements ListDataListener
    {
        JFileChooser chooser;
        BasicDirectoryModel directoryModel;
        ShellFolderColumnInfo[] columns;
        int[] columnMap;
        
        DetailsTableModel(final JFileChooser chooser) {
            this.chooser = chooser;
            (this.directoryModel = FilePane.this.getModel()).addListDataListener(this);
            this.updateColumnInfo();
        }
        
        void updateColumnInfo() {
            File file = this.chooser.getCurrentDirectory();
            if (file != null && FilePane.usesShellFolder(this.chooser)) {
                try {
                    file = ShellFolder.getShellFolder(file);
                }
                catch (final FileNotFoundException ex) {}
            }
            final ShellFolderColumnInfo[] folderColumns = ShellFolder.getFolderColumns(file);
            final ArrayList list = new ArrayList();
            this.columnMap = new int[folderColumns.length];
            for (int i = 0; i < folderColumns.length; ++i) {
                final ShellFolderColumnInfo shellFolderColumnInfo = folderColumns[i];
                if (shellFolderColumnInfo.isVisible()) {
                    this.columnMap[list.size()] = i;
                    list.add(shellFolderColumnInfo);
                }
            }
            list.toArray(this.columns = new ShellFolderColumnInfo[list.size()]);
            this.columnMap = Arrays.copyOf(this.columnMap, this.columns.length);
            final List<? extends RowSorter.SortKey> list2 = (FilePane.this.rowSorter == null) ? null : FilePane.this.rowSorter.getSortKeys();
            this.fireTableStructureChanged();
            this.restoreSortKeys(list2);
        }
        
        private void restoreSortKeys(List<? extends RowSorter.SortKey> sortKeys) {
            if (sortKeys != null) {
                for (int i = 0; i < sortKeys.size(); ++i) {
                    if (sortKeys.get(i).getColumn() >= this.columns.length) {
                        sortKeys = null;
                        break;
                    }
                }
                if (sortKeys != null) {
                    FilePane.this.rowSorter.setSortKeys(sortKeys);
                }
            }
        }
        
        @Override
        public int getRowCount() {
            return this.directoryModel.getSize();
        }
        
        @Override
        public int getColumnCount() {
            return this.columns.length;
        }
        
        @Override
        public Object getValueAt(final int n, final int n2) {
            return this.getFileColumnValue((File)this.directoryModel.getElementAt(n), n2);
        }
        
        private Object getFileColumnValue(final File file, final int n) {
            return (n == 0) ? file : ShellFolder.getFolderColumnValue(file, this.columnMap[n]);
        }
        
        @Override
        public void setValueAt(final Object o, final int n, final int n2) {
            if (n2 == 0) {
                final JFileChooser fileChooser = FilePane.this.getFileChooser();
                final File file = (File)this.getValueAt(n, n2);
                if (file != null) {
                    final String name = fileChooser.getName(file);
                    final String name2 = file.getName();
                    final String trim = ((String)o).trim();
                    if (!trim.equals(name)) {
                        String string = trim;
                        final int length = name2.length();
                        final int length2 = name.length();
                        if (length > length2 && name2.charAt(length2) == '.') {
                            string = trim + name2.substring(length2);
                        }
                        final FileSystemView fileSystemView = fileChooser.getFileSystemView();
                        final File fileObject = fileSystemView.createFileObject(file.getParentFile(), string);
                        if (fileObject.exists()) {
                            JOptionPane.showMessageDialog(fileChooser, MessageFormat.format(FilePane.this.renameErrorFileExistsText, name2), FilePane.this.renameErrorTitleText, 0);
                        }
                        else if (FilePane.this.getModel().renameFile(file, fileObject)) {
                            if (fileSystemView.isParent(fileChooser.getCurrentDirectory(), fileObject)) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (fileChooser.isMultiSelectionEnabled()) {
                                            fileChooser.setSelectedFiles(new File[] { fileObject });
                                        }
                                        else {
                                            fileChooser.setSelectedFile(fileObject);
                                        }
                                    }
                                });
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(fileChooser, MessageFormat.format(FilePane.this.renameErrorText, name2), FilePane.this.renameErrorTitleText, 0);
                        }
                    }
                }
            }
        }
        
        @Override
        public boolean isCellEditable(final int n, final int n2) {
            final File currentDirectory = FilePane.this.getFileChooser().getCurrentDirectory();
            return !FilePane.this.readOnly && n2 == 0 && FilePane.this.canWrite(currentDirectory);
        }
        
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
            new DelayedSelectionUpdater();
            this.fireTableDataChanged();
        }
        
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
            final int index0 = listDataEvent.getIndex0();
            if (index0 == listDataEvent.getIndex1()) {
                final File file = (File)FilePane.this.getModel().getElementAt(index0);
                if (file.equals(FilePane.this.newFolderFile)) {
                    new DelayedSelectionUpdater(file);
                    FilePane.this.newFolderFile = null;
                }
            }
            this.fireTableRowsInserted(listDataEvent.getIndex0(), listDataEvent.getIndex1());
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
            this.fireTableRowsDeleted(listDataEvent.getIndex0(), listDataEvent.getIndex1());
        }
        
        public ShellFolderColumnInfo[] getColumns() {
            return this.columns;
        }
    }
    
    private class DetailsTableRowSorter extends TableRowSorter<TableModel>
    {
        public DetailsTableRowSorter() {
            this.setModelWrapper((ModelWrapper<M, Integer>)new SorterModelWrapper());
        }
        
        public void updateComparators(final ShellFolderColumnInfo[] array) {
            for (int i = 0; i < array.length; ++i) {
                Comparator comparator = array[i].getComparator();
                if (comparator != null) {
                    comparator = new DirectoriesFirstComparatorWrapper(i, comparator);
                }
                this.setComparator(i, comparator);
            }
        }
        
        @Override
        public void sort() {
            ShellFolder.invoke((Callable<Object>)new Callable<Void>() {
                @Override
                public Void call() {
                    DefaultRowSorter.this.sort();
                    return null;
                }
            });
        }
        
        @Override
        public void modelStructureChanged() {
            super.modelStructureChanged();
            this.updateComparators(FilePane.this.detailsTableModel.getColumns());
        }
        
        private class SorterModelWrapper extends ModelWrapper<TableModel, Integer>
        {
            @Override
            public TableModel getModel() {
                return FilePane.this.getDetailsTableModel();
            }
            
            @Override
            public int getColumnCount() {
                return FilePane.this.getDetailsTableModel().getColumnCount();
            }
            
            @Override
            public int getRowCount() {
                return FilePane.this.getDetailsTableModel().getRowCount();
            }
            
            @Override
            public Object getValueAt(final int n, final int n2) {
                return FilePane.this.getModel().getElementAt(n);
            }
            
            @Override
            public Integer getIdentifier(final int n) {
                return n;
            }
        }
    }
    
    private class DirectoriesFirstComparatorWrapper implements Comparator<File>
    {
        private Comparator comparator;
        private int column;
        
        public DirectoriesFirstComparatorWrapper(final int column, final Comparator comparator) {
            this.column = column;
            this.comparator = comparator;
        }
        
        @Override
        public int compare(final File file, final File file2) {
            if (file != null && file2 != null) {
                final boolean traversable = FilePane.this.getFileChooser().isTraversable(file);
                final boolean traversable2 = FilePane.this.getFileChooser().isTraversable(file2);
                if (traversable && !traversable2) {
                    return -1;
                }
                if (!traversable && traversable2) {
                    return 1;
                }
            }
            if (FilePane.this.detailsTableModel.getColumns()[this.column].isCompareByColumn()) {
                return this.comparator.compare(FilePane.this.getDetailsTableModel().getFileColumnValue(file, this.column), FilePane.this.getDetailsTableModel().getFileColumnValue(file2, this.column));
            }
            return this.comparator.compare(file, file2);
        }
    }
    
    private class DetailsTableCellEditor extends DefaultCellEditor
    {
        private final JTextField tf;
        
        public DetailsTableCellEditor(final JTextField tf) {
            super(tf);
            (this.tf = tf).setName("Table.editor");
            tf.addFocusListener(FilePane.this.editorFocusListener);
        }
        
        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object o, final boolean b, final int n, final int n2) {
            final Component tableCellEditorComponent = super.getTableCellEditorComponent(table, o, b, n, n2);
            if (o instanceof File) {
                this.tf.setText(FilePane.this.getFileChooser().getName((File)o));
                this.tf.selectAll();
            }
            return tableCellEditorComponent;
        }
    }
    
    class DetailsTableCellRenderer extends DefaultTableCellRenderer
    {
        JFileChooser chooser;
        DateFormat df;
        
        DetailsTableCellRenderer(final JFileChooser chooser) {
            this.chooser = chooser;
            this.df = DateFormat.getDateTimeInstance(3, 3, chooser.getLocale());
        }
        
        @Override
        public void setBounds(int n, final int n2, int min, final int n3) {
            if (this.getHorizontalAlignment() == 10 && !FilePane.this.fullRowSelection) {
                min = Math.min(min, this.getPreferredSize().width + 4);
            }
            else {
                n -= 4;
            }
            super.setBounds(n, n2, min, n3);
        }
        
        @Override
        public Insets getInsets(Insets insets) {
            final Insets insets2;
            insets = (insets2 = super.getInsets(insets));
            insets2.left += 4;
            final Insets insets3 = insets;
            insets3.right += 4;
            return insets;
        }
        
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object o, boolean b, final boolean b2, final int n, final int n2) {
            if ((table.convertColumnIndexToModel(n2) != 0 || (FilePane.this.listViewWindowsStyle && !table.isFocusOwner())) && !FilePane.this.fullRowSelection) {
                b = false;
            }
            super.getTableCellRendererComponent(table, o, b, b2, n, n2);
            this.setIcon(null);
            Integer n3 = FilePane.this.detailsTableModel.getColumns()[table.convertColumnIndexToModel(n2)].getAlignment();
            if (n3 == null) {
                n3 = ((o instanceof Number) ? 4 : 10);
            }
            this.setHorizontalAlignment(n3);
            String text;
            if (o == null) {
                text = "";
            }
            else if (o instanceof File) {
                final File file = (File)o;
                text = this.chooser.getName(file);
                this.setIcon(this.chooser.getIcon(file));
            }
            else if (o instanceof Long) {
                final long n4 = (long)o / 1024L;
                if (FilePane.this.listViewWindowsStyle) {
                    text = MessageFormat.format(FilePane.this.kiloByteString, n4 + 1L);
                }
                else if (n4 < 1024L) {
                    text = MessageFormat.format(FilePane.this.kiloByteString, (n4 == 0L) ? 1L : n4);
                }
                else {
                    final long n5 = n4 / 1024L;
                    if (n5 < 1024L) {
                        text = MessageFormat.format(FilePane.this.megaByteString, n5);
                    }
                    else {
                        text = MessageFormat.format(FilePane.this.gigaByteString, n5 / 1024L);
                    }
                }
            }
            else if (o instanceof Date) {
                text = this.df.format((Date)o);
            }
            else {
                text = o.toString();
            }
            this.setText(text);
            return this;
        }
    }
    
    private class AlignableTableHeaderRenderer implements TableCellRenderer
    {
        TableCellRenderer wrappedRenderer;
        
        public AlignableTableHeaderRenderer(final TableCellRenderer wrappedRenderer) {
            this.wrappedRenderer = wrappedRenderer;
        }
        
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
            final Component tableCellRendererComponent = this.wrappedRenderer.getTableCellRendererComponent(table, o, b, b2, n, n2);
            Integer n3 = FilePane.this.detailsTableModel.getColumns()[table.convertColumnIndexToModel(n2)].getAlignment();
            if (n3 == null) {
                n3 = 0;
            }
            if (tableCellRendererComponent instanceof JLabel) {
                ((JLabel)tableCellRendererComponent).setHorizontalAlignment(n3);
            }
            return tableCellRendererComponent;
        }
    }
    
    private class DelayedSelectionUpdater implements Runnable
    {
        File editFile;
        
        DelayedSelectionUpdater(final FilePane filePane) {
            this(filePane, null);
        }
        
        DelayedSelectionUpdater(final File editFile) {
            this.editFile = editFile;
            if (FilePane.this.isShowing()) {
                SwingUtilities.invokeLater(this);
            }
        }
        
        @Override
        public void run() {
            FilePane.this.setFileSelected();
            if (this.editFile != null) {
                FilePane.this.editFileName(FilePane.this.getRowSorter().convertRowIndexToView(FilePane.this.getModel().indexOf(this.editFile)));
                this.editFile = null;
            }
        }
    }
    
    class EditActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            FilePane.this.applyEdit();
        }
    }
    
    protected class FileRenderer extends DefaultListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(final JList list, final Object o, final int n, boolean b, final boolean b2) {
            if (FilePane.this.listViewWindowsStyle && !list.isFocusOwner()) {
                b = false;
            }
            super.getListCellRendererComponent(list, o, n, b, b2);
            final File file = (File)o;
            final String name = FilePane.this.getFileChooser().getName(file);
            this.setText(name);
            this.setFont(list.getFont());
            final Icon icon = FilePane.this.getFileChooser().getIcon(file);
            if (icon != null) {
                this.setIcon(icon);
            }
            else if (FilePane.this.getFileChooser().getFileSystemView().isTraversable(file)) {
                this.setText(name + File.separator);
            }
            return this;
        }
    }
    
    private class Handler implements MouseListener
    {
        private MouseListener doubleClickListener;
        
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            final JComponent component = (JComponent)mouseEvent.getSource();
            int n;
            if (component instanceof JList) {
                n = SwingUtilities2.loc2IndexFileList(FilePane.this.list, mouseEvent.getPoint());
            }
            else {
                if (!(component instanceof JTable)) {
                    return;
                }
                final JTable table = (JTable)component;
                final Point point = mouseEvent.getPoint();
                n = table.rowAtPoint(point);
                if (SwingUtilities2.pointOutsidePrefSize(table, n, table.columnAtPoint(point), point) && !FilePane.this.fullRowSelection) {
                    return;
                }
                if (n >= 0 && FilePane.this.list != null && FilePane.this.listSelectionModel.isSelectedIndex(n)) {
                    final Rectangle cellBounds = FilePane.this.list.getCellBounds(n, n);
                    final MouseEvent mouseEvent2 = new MouseEvent(FilePane.this.list, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), cellBounds.x + 1, cellBounds.y + cellBounds.height / 2, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), mouseEvent.getButton());
                    final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
                    mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
                    mouseEvent = mouseEvent2;
                }
            }
            if (n >= 0 && SwingUtilities.isLeftMouseButton(mouseEvent)) {
                final JFileChooser fileChooser = FilePane.this.getFileChooser();
                if (mouseEvent.getClickCount() == 1 && component instanceof JList) {
                    if ((!fileChooser.isMultiSelectionEnabled() || fileChooser.getSelectedFiles().length <= 1) && n >= 0 && FilePane.this.listSelectionModel.isSelectedIndex(n) && FilePane.this.getEditIndex() == n && FilePane.this.editFile == null) {
                        FilePane.this.editFileName(n);
                    }
                    else if (n >= 0) {
                        FilePane.this.setEditIndex(n);
                    }
                    else {
                        FilePane.this.resetEditIndex();
                    }
                }
                else if (mouseEvent.getClickCount() == 2) {
                    FilePane.this.resetEditIndex();
                }
            }
            if (this.getDoubleClickListener() != null) {
                this.getDoubleClickListener().mouseClicked(mouseEvent);
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            final JComponent component = (JComponent)mouseEvent.getSource();
            if (component instanceof JTable) {
                final JTable table = (JTable)mouseEvent.getSource();
                final TransferHandler transferHandler = FilePane.this.getFileChooser().getTransferHandler();
                if (transferHandler != table.getTransferHandler()) {
                    table.setTransferHandler(transferHandler);
                }
                final boolean dragEnabled = FilePane.this.getFileChooser().getDragEnabled();
                if (dragEnabled != table.getDragEnabled()) {
                    table.setDragEnabled(dragEnabled);
                }
            }
            else if (component instanceof JList && this.getDoubleClickListener() != null) {
                this.getDoubleClickListener().mouseEntered(mouseEvent);
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() instanceof JList && this.getDoubleClickListener() != null) {
                this.getDoubleClickListener().mouseExited(mouseEvent);
            }
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() instanceof JList && this.getDoubleClickListener() != null) {
                this.getDoubleClickListener().mousePressed(mouseEvent);
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() instanceof JList && this.getDoubleClickListener() != null) {
                this.getDoubleClickListener().mouseReleased(mouseEvent);
            }
        }
        
        private MouseListener getDoubleClickListener() {
            if (this.doubleClickListener == null && FilePane.this.list != null) {
                this.doubleClickListener = FilePane.this.fileChooserUIAccessor.createDoubleClickListener(FilePane.this.list);
            }
            return this.doubleClickListener;
        }
    }
    
    public interface FileChooserUIAccessor
    {
        JFileChooser getFileChooser();
        
        BasicDirectoryModel getModel();
        
        JPanel createList();
        
        JPanel createDetailsView();
        
        boolean isDirectorySelected();
        
        File getDirectory();
        
        Action getApproveSelectionAction();
        
        Action getChangeToParentDirectoryAction();
        
        Action getNewFolderAction();
        
        MouseListener createDoubleClickListener(final JList p0);
        
        ListSelectionListener createListSelectionListener();
    }
}
