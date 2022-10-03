package org.htmlparser.parserapplications.filterbuilder;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragGestureEvent;
import java.lang.reflect.Method;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.StringWriter;
import javax.swing.JPopupMenu;
import java.awt.event.MouseEvent;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.FileReader;
import org.htmlparser.util.NodeIterator;
import java.beans.PropertyVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.JTree;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeList;
import javax.swing.JInternalFrame;
import org.htmlparser.util.ParserException;
import org.htmlparser.Parser;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.awt.Frame;
import java.awt.FileDialog;
import javax.swing.JOptionPane;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import org.htmlparser.NodeFilter;
import java.io.IOException;
import org.htmlparser.beans.FilterBean;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.Icon;
import java.net.MalformedURLException;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import javax.swing.JSplitPane;
import org.htmlparser.parserapplications.filterbuilder.layouts.NullLayoutManager;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.JDesktopPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.util.Vector;
import java.awt.Point;
import java.net.URL;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragGestureListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

public class FilterBuilder extends JFrame implements WindowListener, ActionListener, MouseListener, MouseMotionListener, DragGestureListener, DragSourceListener, DropTargetListener, ClipboardOwner
{
    static final String TITLE = "HTML Parser FilterBuilder";
    static final URL mDocumentBase;
    static String mHomeDir;
    protected Point mBasePoint;
    protected Vector mSelection;
    protected boolean mMoved;
    protected DropTarget mDropTarget;
    protected DragSource mDragSource;
    protected Component mCurrentComponent;
    protected JPanel mMainPanel;
    protected JScrollPane mMainScroller;
    protected JTextField mURLField;
    protected JDesktopPane mOutput;
    
    public FilterBuilder() {
        this.mMainPanel = new JPanel();
        this.mDropTarget = new DropTarget(this.mMainPanel, this);
        this.mDragSource = new DragSource();
        final JMenuBar menubar = new JMenuBar();
        final JToolBar toolbar = new JToolBar();
        toolbar.setAlignmentY(0.222222f);
        JMenu menu = new JMenu();
        menu.setText("File");
        menu.setActionCommand("File");
        menu.setMnemonic(70);
        this.makeMenuButton("New", "Create a new document", "New", 78, KeyStroke.getKeyStroke(78, 2), toolbar, menu);
        this.makeMenuButton("Open", "Open an existing document", "Open...", 79, KeyStroke.getKeyStroke(79, 2), toolbar, menu);
        this.makeMenuButton("Save", "Save the active document", "Save...", 83, KeyStroke.getKeyStroke(83, 2), toolbar, menu);
        this.makeMenuButton("SaveAs", "Save the active document", "Save As...", 65, KeyStroke.getKeyStroke(65, 2), null, menu);
        menu.add(new JSeparator());
        this.makeMenuButton("Exit", "Exit the program", "Exit", 69, KeyStroke.getKeyStroke(69, 2), null, menu);
        menubar.add(menu);
        toolbar.add(new JToolBar.Separator());
        menu = new JMenu();
        menu.setText("Edit");
        menu.setActionCommand("Edit");
        menu.setMnemonic(69);
        this.makeMenuButton("Cut", "Cut the selection and put it on the Clipboard", "Cut", 84, KeyStroke.getKeyStroke(88, 2), toolbar, menu);
        this.makeMenuButton("Copy", "Copy the selection and put it on the Clipboard", "Copy", 67, KeyStroke.getKeyStroke(67, 2), toolbar, menu);
        this.makeMenuButton("Paste", "Insert Clipboard contents", "Paste", 80, KeyStroke.getKeyStroke(86, 2), toolbar, menu);
        this.makeMenuButton("Delete", "Delete the selection", "Delete", 68, KeyStroke.getKeyStroke(127, 0), toolbar, menu);
        menubar.add(menu);
        menu = new JMenu();
        menu.setText("Filter");
        menu.setActionCommand("Filter");
        menu.setMnemonic(70);
        menubar.add(menu);
        toolbar.add(new JToolBar.Separator());
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.AndFilterWrapper");
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.OrFilterWrapper");
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.NotFilterWrapper");
        menu.addSeparator();
        toolbar.add(new JToolBar.Separator());
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.StringFilterWrapper");
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.RegexFilterWrapper");
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.TagNameFilterWrapper");
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.NodeClassFilterWrapper");
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.HasAttributeFilterWrapper");
        menu.addSeparator();
        toolbar.add(new JToolBar.Separator());
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.HasParentFilterWrapper");
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.HasChildFilterWrapper");
        this.addFilter(menu, toolbar, "org.htmlparser.parserapplications.filterbuilder.wrappers.HasSiblingFilterWrapper");
        menu.addSeparator();
        toolbar.add(new JToolBar.Separator());
        menu = new JMenu();
        menu.setText("Operation");
        menu.setActionCommand("Operation");
        menu.setMnemonic(114);
        JMenuItem item = new JMenuItem();
        item.setText("Expand");
        item.setActionCommand("expandAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem();
        item.setText("Collapse");
        item.setActionCommand("collapseAction");
        item.addActionListener(this);
        menu.add(item);
        menu.addSeparator();
        item = new JMenuItem();
        item.setText("Expand All");
        item.setActionCommand("expandAllAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem();
        item.setText("Collapse All");
        item.setActionCommand("collapseAllAction");
        item.addActionListener(this);
        menu.add(item);
        menu.addSeparator();
        item = new JMenuItem("Fetch Page");
        item.setActionCommand("fetchAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Execute Filter");
        item.setActionCommand("executeAction");
        item.addActionListener(this);
        menu.add(item);
        menubar.add(menu);
        menu = new JMenu();
        menu.setText("Help");
        menu.setActionCommand("Help");
        menu.setMnemonic(72);
        item = new JMenuItem("Filtering");
        item.setActionCommand("filteringAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Instructions");
        item.setActionCommand("instructionsAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Tutorial");
        item.setActionCommand("tutorialAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Hints");
        item.setActionCommand("hintsAction");
        item.addActionListener(this);
        menu.add(item);
        this.makeMenuButton("About", "Display program information, version number and copyright", "About", 66, KeyStroke.getKeyStroke(72, 2), toolbar, menu);
        menubar.add(menu);
        this.setJMenuBar(menubar);
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(0, 0, 0));
        panel.add(toolbar);
        this.getContentPane().setLayout(new BorderLayout(0, 0));
        this.getContentPane().add("North", panel);
        (this.mURLField = new JTextField()).setToolTipText("Enter the URL to view");
        this.mURLField.setText("http://sourceforge.org/projects/htmlparser");
        this.getContentPane().add("South", this.mURLField);
        this.setTitle("HTML Parser FilterBuilder");
        this.setDefaultCloseOperation(0);
        this.setSize(640, 480);
        this.setVisible(false);
        this.mMainPanel.setLayout(new NullLayoutManager());
        this.mMainScroller = new JScrollPane(this.mMainPanel, 20, 30);
        final JSplitPane split = new JSplitPane();
        final JScrollPane pane = new JScrollPane();
        pane.setViewportView(this.mMainScroller);
        split.setLeftComponent(pane);
        split.setRightComponent(this.mOutput = new JDesktopPane());
        this.getContentPane().add("Center", split);
        this.setVisible(true);
        split.setDividerLocation(0.5);
        this.setVisible(false);
        this.addWindowListener(this);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("images/program16.gif"));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.mSelection = new Vector();
    }
    
    public FilterBuilder(final String title) {
        this();
        this.setTitle(title);
    }
    
    protected void makeMenuButton(final String name, final String description, final String text, final int mnemonic, final KeyStroke key, final JToolBar toolbar, final JMenu menu) {
        final String command = name.toLowerCase();
        ImageIcon icon;
        try {
            icon = new ImageIcon(this.getURL("images/" + command + ".gif"));
        }
        catch (final MalformedURLException error) {
            icon = null;
        }
        final JMenuItem item = new JMenuItem();
        item.setText(text);
        item.setActionCommand(command + "Action");
        item.setAccelerator(key);
        item.setMnemonic(mnemonic);
        item.setIcon(icon);
        item.addActionListener(this);
        menu.add(item);
        if (null != toolbar) {
            final JButton button = new JButton();
            button.setDefaultCapable(false);
            button.setToolTipText(description);
            button.setMnemonic(mnemonic);
            button.setActionCommand(command + "Action");
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setIcon(icon);
            button.addActionListener(this);
            toolbar.add(button);
        }
    }
    
    protected URL getURL(final String spec) throws MalformedURLException {
        URL ret;
        if (null == (ret = this.getClass().getResource(spec))) {
            if (null != FilterBuilder.mDocumentBase && -1 == spec.indexOf("//")) {
                ret = new URL(FilterBuilder.mDocumentBase, spec);
            }
            else {
                ret = new URL(spec);
            }
        }
        return ret;
    }
    
    public JButton makeFilterButton(final String class_name) {
        final JButton ret = new JButton();
        final Filter filter = Filter.instantiate(class_name);
        if (null != filter) {
            ret.setName(class_name);
            ret.setToolTipText(filter.getDescription());
            ret.setMargin(new Insets(0, 0, 0, 0));
            ret.setIcon(filter.getIcon());
            this.mDragSource.createDefaultDragGestureRecognizer(ret, 2, this);
            ret.setActionCommand("filterAction");
            ret.addActionListener(this);
        }
        return ret;
    }
    
    public void addFilter(final JMenu menu, final JToolBar toolbar, final String class_name) {
        final Filter filter = Filter.instantiate(class_name);
        if (null != filter) {
            final String name = filter.getNodeFilter().getClass().getName();
            final String description = filter.getDescription();
            final Icon icon = filter.getIcon();
            final String text = name.substring(name.lastIndexOf(46) + 1);
            final JMenuItem item = new JMenuItem();
            item.setName(class_name);
            item.setText(text);
            item.setActionCommand("filterAction");
            item.setToolTipText(description);
            item.setIcon(icon);
            item.addActionListener(this);
            menu.add(item);
            toolbar.add(this.makeFilterButton(class_name));
        }
    }
    
    protected void insertFilters(final Filter[] filters, final Point point, final SubFilterList list) {
        if (null == list) {
            for (int i = 0; i < filters.length; ++i) {
                filters[i].setLocation(point);
                this.mMainPanel.add(filters[i]);
                final Dimension dimension = filters[i].getPreferredSize();
                point.y += dimension.height;
            }
        }
        else {
            for (int i = 0; i < filters.length; ++i) {
                list.addFilter(filters[i]);
            }
        }
        this.setupDropTargets(filters);
        this.setupMouseListeners(filters);
        this.relayout();
    }
    
    protected void setBasePoint(final Point point) {
        this.mBasePoint = point;
    }
    
    protected Point getBasePoint() {
        return this.mBasePoint;
    }
    
    protected SubFilterList getEnclosing(Component component) {
        do {
            component = component.getParent();
        } while (null != component && !(component instanceof SubFilterList));
        return (SubFilterList)component;
    }
    
    protected SubFilterList getEnclosed(final Component component) {
        if (component instanceof Container) {
            final Component[] list = ((Container)component).getComponents();
            for (int i = 0; i < list.length; ++i) {
                if (list[i] instanceof SubFilterList) {
                    return (SubFilterList)list[i];
                }
            }
        }
        return null;
    }
    
    protected void makeProgram(final String name, final StringBuffer out, final FilterBean bean) {
        final Filter[] filters = (Filter[])bean.getFilters();
        final int[] context = { 0, 0, 0 };
        Filter.spaces(out, context[0]);
        out.append("// Generated by FilterBuilder. http://htmlparser.org");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("// ");
        try {
            out.append(Filter.deconstitute(filters));
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        Filter.newline(out);
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("import org.htmlparser.*;");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("import org.htmlparser.filters.*;");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("import org.htmlparser.beans.*;");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("import org.htmlparser.util.*;");
        Filter.newline(out);
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("public class ");
        out.append(name);
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("{");
        context[0] = 4;
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("public static void main (String args[])");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("{");
        Filter.newline(out);
        context[0] = 8;
        final String[] names = new String[filters.length];
        for (int i = 0; i < names.length; ++i) {
            names[i] = filters[i].toJavaCode(out, context);
        }
        final String array = "array" + context[2]++;
        Filter.spaces(out, context[0]);
        out.append("NodeFilter[] ");
        out.append(array);
        out.append(" = new NodeFilter[");
        out.append(filters.length);
        out.append("];");
        Filter.newline(out);
        for (int i = 0; i < filters.length; ++i) {
            Filter.spaces(out, context[0]);
            out.append(array);
            out.append("[");
            out.append(i);
            out.append("] = ");
            out.append(names[i]);
            out.append(";");
            Filter.newline(out);
        }
        Filter.spaces(out, context[0]);
        out.append("FilterBean bean = new FilterBean ();");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("bean.setFilters (");
        out.append(array);
        out.append(");");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("if (0 != args.length)");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("{");
        Filter.newline(out);
        Filter.spaces(out, context[0] = 12);
        out.append("bean.setURL (args[0]);");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("System.out.println (bean.getNodes ().toHtml ());");
        Filter.newline(out);
        Filter.spaces(out, context[0] = 8);
        out.append("}");
        Filter.newline(out);
        Filter.spaces(out, context[0]);
        out.append("else");
        Filter.newline(out);
        Filter.spaces(out, context[0] = 12);
        out.append("System.out.println (\"Usage: java -classpath .:htmlparser.jar ");
        out.append(name);
        out.append(" <url>\");");
        Filter.newline(out);
        Filter.spaces(out, context[0] = 4);
        out.append("}");
        Filter.newline(out);
        Filter.spaces(out, context[0] = 0);
        out.append("}");
        Filter.newline(out);
    }
    
    protected String classFromFile(String file) {
        final String filesep = System.getProperty("file.separator");
        int index = file.lastIndexOf(filesep);
        if (-1 != index) {
            file = file.substring(index + filesep.length());
        }
        index = file.indexOf(46);
        if (-1 != index) {
            file = file.substring(0, index);
        }
        return file;
    }
    
    public void save(final String name) {
        final String ok = "OK";
        final Filter[] selections = this.getFilters();
        if (0 != selections.length) {
            final FilterBean bean = new FilterBean();
            bean.setURL(this.mURLField.getText());
            bean.setFilters(selections);
            final StringBuffer buffer = new StringBuffer();
            this.makeProgram(this.classFromFile(name), buffer, bean);
            try {
                final PrintWriter out = new PrintWriter(new FileWriter(name), true);
                try {
                    out.write(buffer.toString());
                    out.flush();
                }
                finally {
                    out.close();
                }
            }
            catch (final IOException ioe) {
                ioe.printStackTrace();
            }
        }
        else {
            JOptionPane.showOptionDialog(this.mMainPanel, "No filters to save.", "Oops", -1, 0, null, new String[] { ok }, ok);
        }
    }
    
    protected void newAction() {
        this.mMainPanel.removeAll();
        this.mSelection.clear();
        this.relayout();
    }
    
    protected void openAction() {
        final FileDialog dialog = new FileDialog(this);
        dialog.setMode(0);
        dialog.setTitle("Open");
        dialog.setDirectory(FilterBuilder.mHomeDir);
        dialog.setVisible(true);
        if (null != dialog.getFile()) {
            FilterBuilder.mHomeDir = dialog.getDirectory();
            final File file = new File(FilterBuilder.mHomeDir + dialog.getFile());
            this.open(file.getAbsolutePath());
            this.setTitle("HTML Parser FilterBuilder - " + file.getAbsolutePath());
        }
    }
    
    protected void saveAction() {
        final String title = this.getTitle();
        final int index = title.indexOf(" - ");
        File file;
        if (-1 != index) {
            file = new File(title.substring(index + 3));
        }
        else {
            final FileDialog dialog = new FileDialog(this);
            dialog.setMode(1);
            dialog.setTitle("Save");
            dialog.setDirectory(FilterBuilder.mHomeDir);
            dialog.setVisible(true);
            if (null != dialog.getFile()) {
                FilterBuilder.mHomeDir = dialog.getDirectory();
                file = new File(FilterBuilder.mHomeDir + dialog.getFile());
                this.setTitle("HTML Parser FilterBuilder - " + file.getAbsolutePath());
            }
            else {
                file = null;
            }
        }
        if (null != file) {
            this.save(file.getAbsolutePath());
        }
    }
    
    protected void saveasAction() {
        this.setTitle("HTML Parser FilterBuilder");
        this.saveAction();
    }
    
    protected void exitAction() {
        this.exitApplication();
    }
    
    protected void cutAction() {
        final String string = this.serializeSelection();
        final StringSelection contents = new StringSelection(string);
        final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(contents, this);
        this.deleteSelection();
        this.relayout();
    }
    
    protected void copyAction() {
        final String string = this.serializeSelection();
        final StringSelection contents = new StringSelection(string);
        final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(contents, this);
    }
    
    protected void pasteAction() {
        final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        final Transferable content = cb.getContents(this);
        if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                final String string = (String)content.getTransferData(DataFlavor.stringFlavor);
                final Filter[] filters = Filter.reconstitute(string, new Parser(this.mURLField.getText()));
                final SubFilterList list;
                if (this.isSingleSelection() && null != (list = this.getEnclosed(this.getSelection()[0]))) {
                    for (int i = 0; i < filters.length; ++i) {
                        list.addFilter(filters[i]);
                    }
                }
                else {
                    final Point point = new Point(0, 0);
                    for (int i = 0; i < filters.length; ++i) {
                        filters[i].setLocation(point);
                        this.mMainPanel.add(filters[i]);
                        final Point point2 = point;
                        point2.y += filters[i].getPreferredSize().height;
                    }
                }
                this.setupMouseListeners(filters);
                this.setupDropTargets(filters);
                this.relayout();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void deleteAction() {
        this.deleteSelection();
        this.relayout();
    }
    
    protected void filterAction() {
        final String cls = this.mCurrentComponent.getName();
        Filter filter = Filter.instantiate(cls);
        try {
            filter = Filter.wrap(filter.getNodeFilter(), new Parser(this.mURLField.getText()));
        }
        catch (final ParserException pe) {
            pe.printStackTrace();
        }
        final SubFilterList list;
        if (this.isSingleSelection() && null != (list = this.getEnclosed(this.getSelection()[0]))) {
            this.insertFilters(new Filter[] { filter }, null, list);
        }
        else {
            final Point point = new Point(50, 50);
            this.insertFilters(new Filter[] { filter }, point, null);
        }
    }
    
    protected void fetchAction() {
        final JInternalFrame frame = new JInternalFrame(this.mURLField.getText());
        frame.setClosable(true);
        frame.setResizable(true);
        final Dimension dimension = this.mOutput.getSize();
        frame.setBounds(0, 0, dimension.width, dimension.height);
        final NodeList list = new NodeList();
        try {
            final Parser parser = new Parser(this.mURLField.getText());
            try {
                final NodeIterator iterator = parser.elements();
                while (iterator.hasMoreNodes()) {
                    list.add(iterator.nextNode());
                }
            }
            catch (final EncodingChangeException ece) {
                list.removeAll();
                parser.reset();
                final NodeIterator iterator2 = parser.elements();
                while (iterator2.hasMoreNodes()) {
                    list.add(iterator2.nextNode());
                }
            }
        }
        catch (final ParserException pe) {
            pe.printStackTrace();
        }
        final JTree tree = new JTree(new HtmlTreeModel(list));
        tree.setRootVisible(false);
        tree.setCellRenderer(new HtmlTreeCellRenderer());
        final JScrollPane treeView = new JScrollPane(tree);
        frame.setContentPane(new JScrollPane(treeView, 20, 30));
        this.mOutput.add(frame, new Integer(1));
        try {
            frame.setSelected(true);
        }
        catch (final PropertyVetoException pve) {
            pve.printStackTrace();
        }
        frame.show();
    }
    
    protected void executeAction() {
        Filter[] selections = this.getSelection();
        if (0 == selections.length) {
            selections = this.getFilters();
        }
        if (0 != selections.length) {
            final FilterBean bean = new FilterBean();
            bean.setURL(this.mURLField.getText());
            bean.setFilters(selections);
            final JInternalFrame frame = new JInternalFrame(bean.getURL());
            frame.setClosable(true);
            frame.setResizable(true);
            final Dimension dimension = this.mOutput.getSize();
            frame.setBounds(0, 0, dimension.width, dimension.height / 2);
            final JTree tree = new JTree(new HtmlTreeModel(bean.getNodes()));
            tree.setRootVisible(false);
            tree.setCellRenderer(new HtmlTreeCellRenderer());
            final JScrollPane treeView = new JScrollPane(tree);
            frame.setContentPane(new JScrollPane(treeView, 20, 30));
            this.mOutput.add(frame, new Integer(2));
            try {
                frame.setSelected(true);
            }
            catch (final PropertyVetoException pve) {
                pve.printStackTrace();
            }
            frame.show();
        }
    }
    
    protected void instructionsAction() {
        final String instructions = "<html>Enter the target URL in the text box at the bottom of the window.<br>Choose 'Fetch Page' from the Operations menu to see the whole page.<br>Pick filters from the Filter menu or drag them from the toolbar.<br>Filters such as And, Or, Not, HasParent, HasChild and HasSibling contain other filters:<br><ul><li>drag new filters into their blank areas at the bottom</li><li>cut an existing filter and paste into a selected filter</li></ul>Build the filter incrementally, choosing 'Execute Filter' to test the selected filter.<br>Save creates a .java file that runs the top level filter.<br>Right click on a filter displays a pop-up menu.<br>Double click on a blue item in the result pane expands the tree.</html>";
        final String close = "Close";
        JOptionPane.showOptionDialog(this.mMainPanel, instructions, "FilterBuilder Instructons", -1, 1, null, new String[] { close }, close);
    }
    
    protected void filteringAction() {
        final String instructions = "<html>The HTML Parser filter subsystem extracts items from a web page,<br>corresponding to the use-case 'I want this little piece of information from http://yadda'.<br>The web page is considered a heirarchical tree of nodes. Usually the root node is &lt;html&gt;,<br>intermediate level nodes are &lt;div&gt; and &lt;table&gt; for example,<br>and leaf nodes are things like text or &lt;img&gt;.<br>Any node that isn't the root node has a 'parent' node.<br>Leaf nodes, by definition, have no 'children'.<br>A filter is a Java class that answers the simple question:<br><pre>Is this node acceptable? True or false.</pre><br>Some filters know the answer just by looking at the node,<br>while others must ask other filters, sometimes looking up or down the node heirarchy.<br><b>The FilterBuilder is a program for making other programs that use filters.</b><br>By combining different types of filters, specific nodes can be isolated from the<br>target web page.<br>The results are usually passed on to another part of the users program<br>that does something useful with them.<br>The filters available include:<br><ul><li>AndFilter - The main 'combining' filter, answers <code>true</code> only if<br>all it's subfilters (predicates) are <code>true</code>.</li><li>OrFilter - A 'combining' filter that answers <code>true</code> if<br>any of it's subfilters (predicates) are <code>true</code>.</li><li>NotFilter - A 'reversing' filter that answers <code>true</code> if<br>it's subfilter (predicate) is <code>false</code>.</li><li>StringFilter - A 'leaf' filter that answers <code>true</code> if<br>the node is text and it contains a certain sequence of characters.<br>It can be made case insensitive, but in this case a 'locale' must be<br>supplied as a context for upper-case conversion.</li><li>RegexFilter - A 'leaf' filter that answers <code>true</code> if<br>the node is text and it contains a certain pattern (regular expression).<br>Regular expressions are descibed in the java.util.regex.Pattern class documentation.</li><li>TagNameFilter - A filter that answers <code>true</code> if<br>the node is a tag and it has a certain name,i.e. &lt;div&gt; would match the name <code>DIV</code>.</li><li>NodeClassFilter - A filter that answers <code>true</code> if<br>the node is a certain tag class. Not recommended, use TagNameFilter instead.</li><li>HasAttributeFilter - A filter that answers <code>true</code> if<br>the node is a tag and it has a certain attribute,<br>i.e. &lt;script language=javascript&gt; would match the attribute <code>LANGUAGE</code>.<br>It can be further restricted to have a certain attribute value as well,<br>i.e. 'javascript' in this example.</li><li>HasParentFilter - A filter that answers <code>true</code> if<br>the node is a child of a node that is acceptable to a certain filter.<br>This can be made recursive, which means the acceptable parent can be<br>further up the heirarchy than just the immediate parent node.</li><li>HasChildFilter - A filter that answers <code>true</code> if<br>the node is a parent of a node that is acceptable to a certain filter.<br>This can be made recursive, which means the acceptable child can be<br>further down the heirarchy than just the immediate children nodes.</li><li>HasSiblingFilter - A filter that answers <code>true</code> if<br>the node is a sibling (they have a common parent) of a node that is<br>acceptable to a certain filter.</li></ul></html>";
        final String close = "Close";
        JOptionPane.showOptionDialog(this.mMainPanel, instructions, "FilterBuilder Instructons", -1, 1, null, new String[] { close }, close);
    }
    
    protected void tutorialAction() {
        final String instructions = "<html>To get the title text from a page:<br><ul><li>Choose 'New' from the File menu.</li><li>Choose 'AndFilter' from the Filter menu.</li><li>Select the And filter so it is highlighted.</li><li>Choose 'HasParent' from the Filter menu.</li><li>Toggle the 'Recursive' checkbox on in the HasParent filter.</li><li>Select the HasParent filter so it is highlighted.</li><li>Choose 'TagName' from the Filter menu.<br><i>Alternatively, you can drag the TagName filter (icon Hello-BOB)<br>from the toolbar and drop inside the HasParent filter</i></li><li>Choose 'TITLE' from the TagName combo-box.</li><li>Select the And filter and choose 'Execute Filter' from the<br>Operations menu to test it.</li><li>If there is unwanted non-text nodes in the result<br>select the And filter and choose 'RegexFilter' from the Filter menu.</li><li>Test it again, as above.</li><li>Choose 'Save' from the File menu and enter a filename like GetTitle.java</li><li>Compile the java file and run it.</li></ul></html>";
        final String close = "Close";
        JOptionPane.showOptionDialog(this.mMainPanel, instructions, "FilterBuilder Tutorial", -1, 1, null, new String[] { close }, close);
    }
    
    protected void hintsAction() {
        final String instructions = "<html>Hints:<br><ul><li>There is no undo yet, so save often.</li><li>Recursive HasParent and HasChild filters can be costly.</li><li>RegexFilter is more expensive than StringFilter.</li><li>The order of predicates in And and Or filters matters for performance,<br>put cheap tests first.</li><li>The same node may show up more than once in the results,<br>and at more than one nesting depth, depending on the filter used.</li><li>Typing in a tag name in the TagName filter is not recommended,<br>since extraneous characters can be added. Use an item from the list instead.</li></ul></html>";
        final String close = "Close";
        JOptionPane.showOptionDialog(this.mMainPanel, instructions, "FilterBuilder Hints", -1, 1, null, new String[] { close }, close);
    }
    
    protected void aboutAction() {
        final String close = "Close";
        JOptionPane.showOptionDialog(this.mMainPanel, "<html><center><font color=black>The HTML Parser <font color=blue><b>FilterBuilder</b></font><br><i>by Derrick Oswald</i>&nbsp;&nbsp;<b>DerrickOswald@users.sourceforge.net</b><br>http://htmlparser.org<br><br><font size=-2>Copyright &copy; 2005</font></center></html>", "About FilterBuilder", -1, 1, null, new String[] { close }, close);
    }
    
    public void expandAction() {
        this.setExpanded(this.getSelection(), true, false);
    }
    
    public void collapseAction() {
        this.setExpanded(this.getSelection(), false, false);
    }
    
    public void expandAllAction() {
        this.setExpanded(this.getSelection(), true, true);
    }
    
    public void collapseAllAction() {
        this.setExpanded(this.getSelection(), false, true);
    }
    
    public void setupMouseListeners(final Filter[] filters) {
        for (int i = 0; i < filters.length; ++i) {
            filters[i].addMouseListener(this);
            filters[i].addMouseMotionListener(this);
            final SubFilterList list = this.getEnclosed(filters[i]);
            if (null != list) {
                this.setupMouseListeners(list.getFilters());
            }
        }
    }
    
    public void setupDropTargets(final Filter[] filters) {
        for (int i = 0; i < filters.length; ++i) {
            final SubFilterList list = this.getEnclosed(filters[i]);
            if (null != list) {
                final Component[] components = list.getDropTargets();
                for (int j = 0; j < components.length; ++j) {
                    new DropTarget(components[j], this);
                }
                this.setupDropTargets(list.getFilters());
            }
        }
    }
    
    public void setExpanded(final Filter[] filters, final boolean expanded, final boolean recursive) {
        for (int i = 0; i < filters.length; ++i) {
            final SubFilterList list;
            if (recursive && null != (list = this.getEnclosed(filters[i]))) {
                this.setExpanded(list.getFilters(), expanded, recursive);
            }
            filters[i].setExpanded(expanded);
        }
    }
    
    public Filter[] getFilters() {
        final Component[] components = this.mMainPanel.getComponents();
        final Filter[] ret = new Filter[components.length];
        System.arraycopy(components, 0, ret, 0, components.length);
        return ret;
    }
    
    public void relayout() {
        this.mMainPanel.invalidate();
        this.mMainScroller.invalidate();
        this.mMainScroller.validate();
        this.mMainScroller.repaint();
    }
    
    public void open(final String name) {
        try {
            final LineNumberReader reader = new LineNumberReader(new FileReader(name));
            String line;
            while (null != (line = reader.readLine())) {
                if (line.startsWith("// [")) {
                    line = line.substring(3);
                    try {
                        final Filter[] filters = Filter.reconstitute(line, new Parser(this.mURLField.getText()));
                        this.mMainPanel.removeAll();
                        final Point point = new Point();
                        for (int i = 0; i < filters.length; ++i) {
                            final Dimension dimension = filters[i].getPreferredSize();
                            this.mMainPanel.add(filters[i]);
                            filters[i].setLocation(point);
                            final Point point2 = point;
                            point2.y += dimension.height;
                        }
                        this.setupMouseListeners(filters);
                        this.setupDropTargets(filters);
                        this.relayout();
                    }
                    catch (final ParserException pe) {
                        pe.printStackTrace();
                    }
                    break;
                }
            }
            reader.close();
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    void exitApplication() {
        this.setVisible(false);
        this.dispose();
        System.exit(0);
    }
    
    public void showContextMenu(final MouseEvent event) {
        final JPopupMenu menu = new JPopupMenu();
        menu.setName("Popup");
        JMenuItem item = new JMenuItem("Expand");
        item.setActionCommand("expandAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Collapse");
        item.setActionCommand("collapseAction");
        item.addActionListener(this);
        menu.add(item);
        menu.addSeparator();
        item = new JMenuItem("Expand All");
        item.setActionCommand("expandAllAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("CollapseAll");
        item.setActionCommand("collapseAllAction");
        item.addActionListener(this);
        menu.add(item);
        menu.addSeparator();
        item = new JMenuItem("Cut");
        item.setActionCommand("cutAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Copy");
        item.setActionCommand("copyAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Paste");
        item.setActionCommand("pasteAction");
        item.addActionListener(this);
        menu.add(item);
        item = new JMenuItem("Delete");
        item.setActionCommand("deleteAction");
        item.addActionListener(this);
        menu.add(item);
        menu.addSeparator();
        item = new JMenuItem("Execute Filter");
        item.setActionCommand("executeAction");
        item.addActionListener(this);
        menu.add(item);
        menu.show(event.getComponent(), event.getX(), event.getY());
    }
    
    protected void addSelection(final Filter filter) {
        if (!this.selectionContains(filter)) {
            this.mSelection.addElement(filter);
        }
        filter.setSelected(true);
        this.mMoved = false;
    }
    
    protected void removeSelection(final Filter filter) {
        this.mSelection.removeElement(filter);
        filter.setSelected(false);
    }
    
    protected void selectSelection(final boolean select) {
        for (int count = this.mSelection.size(), i = 0; i < count; ++i) {
            final Filter filter = this.mSelection.elementAt(i);
            filter.setSelected(select);
        }
    }
    
    protected void clearSelection() {
        this.selectSelection(false);
        this.mSelection.removeAllElements();
    }
    
    protected void moveSelection(final Point translation) {
        for (int count = this.mSelection.size(), i = 0; i < count; ++i) {
            final Filter filter = this.mSelection.elementAt(i);
            final Point point = filter.getLocation();
            point.translate(translation.x, translation.y);
            synchronized (filter.getTreeLock()) {
                filter.setLocation(point.x, point.y);
            }
        }
        this.mMoved = true;
    }
    
    protected boolean selectionContains(final Filter filter) {
        return this.mSelection.contains(filter);
    }
    
    protected Filter lastSelected() {
        Filter ret = null;
        if (0 < this.mSelection.size()) {
            ret = this.mSelection.lastElement();
        }
        return ret;
    }
    
    protected Filter[] getSelection() {
        final Filter[] ret = new Filter[this.mSelection.size()];
        this.mSelection.copyInto(ret);
        return ret;
    }
    
    public String serializeSelection() {
        final Filter[] filters = this.getSelection();
        final StringWriter writer = new StringWriter(200);
        final PrintWriter out = new PrintWriter(writer, false);
        try {
            out.println(Filter.deconstitute(filters));
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            out.close();
        }
        return writer.getBuffer().toString();
    }
    
    public void deleteSelection() {
        final Filter[] filters = this.getSelection();
        for (int i = 0; i < filters.length; ++i) {
            final SubFilterList list = this.getEnclosing(filters[i]);
            if (null != list) {
                list.removeFilter(filters[i]);
            }
            else {
                this.mMainPanel.remove(filters[i]);
            }
        }
        this.mSelection.clear();
    }
    
    public boolean isSingleSelection() {
        return 1 == this.mSelection.size();
    }
    
    public void mouseClicked(final MouseEvent event) {
        final Object component = event.getSource();
        if (component instanceof Filter) {
            final Filter filter = (Filter)component;
            final int modifiers = event.getModifiers();
            final boolean contained = this.selectionContains(filter);
            if (0x0 != (modifiers & 0x1)) {
                final SubFilterList list = this.getEnclosed(filter);
                Filter[] filters;
                if (null != list) {
                    filters = list.getFilters();
                }
                else {
                    filters = this.getFilters();
                }
                final Filter last = this.lastSelected();
                if (null == last) {
                    this.addSelection(filter);
                }
                else {
                    int current = -1;
                    int recent = -1;
                    for (int i = 0; i < filters.length; ++i) {
                        if (filters[i] == filter) {
                            current = i;
                        }
                        if (filters[i] == last) {
                            recent = i;
                        }
                    }
                    if (current != -1 && recent != -1) {
                        for (int i = Math.min(current, recent); i <= Math.max(current, recent); ++i) {
                            this.addSelection(filters[i]);
                        }
                    }
                }
            }
            else if (0x0 != (modifiers & 0x2)) {
                if (contained) {
                    this.removeSelection(filter);
                }
                else {
                    this.addSelection(filter);
                }
            }
            else if (0x0 != (modifiers & 0x4)) {
                if (!this.selectionContains(filter)) {
                    this.clearSelection();
                    this.addSelection(filter);
                }
                this.showContextMenu(event);
            }
            else {
                this.clearSelection();
                this.addSelection(filter);
            }
        }
        else {
            this.clearSelection();
        }
    }
    
    public void mouseReleased(final MouseEvent event) {
    }
    
    public void mouseEntered(final MouseEvent event) {
    }
    
    public void mouseExited(final MouseEvent event) {
    }
    
    public void mousePressed(final MouseEvent event) {
        final Object component = event.getSource();
        if (component instanceof Filter) {
            final Point newpoint = event.getPoint();
            final Point upperleft = ((Component)component).getLocation();
            newpoint.translate(upperleft.x, upperleft.y);
            this.setBasePoint(newpoint);
        }
        else {
            this.setBasePoint(null);
        }
    }
    
    public synchronized void mouseDragged(final MouseEvent event) {
        final Object component = event.getSource();
        if (component instanceof Filter) {
            final Filter filter = (Filter)component;
            if (this.selectionContains(filter)) {
                if (null == this.getEnclosing(filter)) {
                    try {
                        final Point base = this.getBasePoint();
                        if (null != base) {
                            final Point newpoint = event.getPoint();
                            final Point upperleft = filter.getLocation();
                            newpoint.translate(upperleft.x, upperleft.y);
                            final Point translation = new Point(newpoint.x - base.x, newpoint.y - base.y);
                            this.setBasePoint(newpoint);
                            this.moveSelection(translation);
                        }
                    }
                    catch (final Exception e) {}
                }
            }
            else {
                this.mouseClicked(event);
            }
        }
    }
    
    public void mouseMoved(final MouseEvent event) {
    }
    
    public void windowOpened(final WindowEvent event) {
    }
    
    public void windowClosing(final WindowEvent event) {
        if (event.getSource() == this) {
            this.exitApplication();
        }
    }
    
    public void windowClosed(final WindowEvent event) {
    }
    
    public void windowIconified(final WindowEvent event) {
    }
    
    public void windowDeiconified(final WindowEvent event) {
    }
    
    public void windowActivated(final WindowEvent event) {
    }
    
    public void windowDeactivated(final WindowEvent event) {
    }
    
    public void actionPerformed(final ActionEvent event) {
        final Object object = event.getSource();
        String action;
        if (object instanceof JButton) {
            action = ((JButton)object).getActionCommand();
        }
        else if (object instanceof JMenuItem) {
            action = ((JMenuItem)object).getActionCommand();
        }
        else {
            action = null;
        }
        if (object instanceof Component) {
            this.mCurrentComponent = (Component)object;
        }
        if (null != action) {
            try {
                final Method method = this.getClass().getDeclaredMethod(action, (Class<?>[])new Class[0]);
                method.invoke(this, new Object[0]);
            }
            catch (final NoSuchMethodException nsme) {
                System.out.println("no " + action + " method found");
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void lostOwnership(final Clipboard clipboard, final Transferable contents) {
        System.out.println("lost clipboard ownership");
    }
    
    public void dragGestureRecognized(final DragGestureEvent event) {
        final Component component = event.getComponent();
        try {
            final String cls = component.getName();
            if (null != cls) {
                final Filter filter = Filter.instantiate(cls);
                final StringSelection text = new StringSelection(Filter.deconstitute(new Filter[] { filter }));
                this.mDragSource.startDrag(event, DragSource.DefaultMoveDrop, text, this);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void dragDropEnd(final DragSourceDropEvent event) {
        if (event.getDropSuccess()) {}
    }
    
    public void dragEnter(final DragSourceDragEvent event) {
    }
    
    public void dragExit(final DragSourceEvent event) {
    }
    
    public void dragOver(final DragSourceDragEvent event) {
    }
    
    public void dropActionChanged(final DragSourceDragEvent event) {
    }
    
    public void dragEnter(final DropTargetDragEvent event) {
        final DropTargetContext context = event.getDropTargetContext();
        Component component;
        for (component = context.getComponent(); null != component && !(component instanceof SubFilterList) && component != this.mMainPanel; component = component.getParent()) {}
        SubFilterList list;
        if (component instanceof SubFilterList) {
            list = (SubFilterList)component;
        }
        else {
            list = null;
        }
        if (null != list) {
            if (!list.canAccept()) {
                event.rejectDrag();
            }
            else {
                list.setSelected(true);
            }
        }
    }
    
    public void dragExit(final DropTargetEvent event) {
        final DropTargetContext context = event.getDropTargetContext();
        Component component;
        for (component = context.getComponent(); null != component && !(component instanceof SubFilterList) && component != this.mMainPanel; component = component.getParent()) {}
        SubFilterList list;
        if (component instanceof SubFilterList) {
            list = (SubFilterList)component;
        }
        else {
            list = null;
        }
        if (null != list) {
            list.setSelected(false);
        }
    }
    
    public void dragOver(final DropTargetDragEvent event) {
    }
    
    public void drop(final DropTargetDropEvent event) {
        final DropTargetContext context = event.getDropTargetContext();
        Component component;
        for (component = context.getComponent(); null != component && !(component instanceof SubFilterList) && component != this.mMainPanel; component = component.getParent()) {}
        SubFilterList list;
        if (component instanceof SubFilterList) {
            list = (SubFilterList)component;
        }
        else {
            list = null;
        }
        try {
            boolean accept = false;
            final Transferable transferable = event.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                accept = true;
                event.acceptDrop(2);
                final String s = (String)transferable.getTransferData(DataFlavor.stringFlavor);
                final Point point = event.getLocation();
                try {
                    final Filter[] filters = Filter.reconstitute(s, new Parser(this.mURLField.getText()));
                    if (0 < filters.length) {
                        this.insertFilters(filters, point, list);
                    }
                    if (null != list) {
                        list.setSelected(false);
                    }
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
                context.dropComplete(accept);
            }
            else {
                event.rejectDrop();
            }
        }
        catch (final IOException exception) {
            exception.printStackTrace();
            System.err.println("Exception" + exception.getMessage());
            event.rejectDrop();
        }
        catch (final UnsupportedFlavorException ufException) {
            ufException.printStackTrace();
            System.err.println("Exception" + ufException.getMessage());
            event.rejectDrop();
        }
    }
    
    public void dropActionChanged(final DropTargetDragEvent event) {
    }
    
    public static void main(final String[] args) {
        try {
            final FilterBuilder builder = new FilterBuilder();
            if (0 != args.length) {
                builder.mURLField.setText(args[0]);
            }
            builder.setVisible(true);
        }
        catch (final Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
    static {
        final String p = System.getProperty("user.dir");
        try {
            final char ps = System.getProperty("file.separator").charAt(0);
            if ('/' != ps) {
                p.replace(ps, '/');
            }
        }
        catch (final StringIndexOutOfBoundsException ex) {}
        URL base;
        try {
            base = new URL("file:///" + p + "/");
        }
        catch (final MalformedURLException murle) {
            base = null;
        }
        mDocumentBase = base;
        final String dir = System.getProperty("user.home") + System.getProperty("file.separator") + ".htmlparser";
        final File file = new File(dir);
        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException("cannot create directory " + file.getAbsolutePath());
        }
        FilterBuilder.mHomeDir = file.getAbsolutePath();
    }
}
