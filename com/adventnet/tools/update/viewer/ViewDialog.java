package com.adventnet.tools.update.viewer;

import java.awt.GridLayout;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Graphics;
import javax.swing.border.AbstractBorder;
import javax.swing.table.TableColumnModel;
import java.awt.Cursor;
import javax.swing.event.HyperlinkEvent;
import javax.swing.JLabel;
import com.adventnet.tools.update.installer.ReadMeWrapper;
import com.adventnet.tools.update.installer.ReadmeUI;
import java.io.FileWriter;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import javax.swing.JList;
import javax.swing.table.TableColumn;
import java.util.Arrays;
import java.awt.Toolkit;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.io.IOException;
import javax.swing.event.HyperlinkListener;
import java.awt.ComponentOrientation;
import java.applet.Applet;
import com.adventnet.tools.update.installer.Utility;
import javax.swing.tree.TreeNode;
import java.util.Collection;
import javax.swing.RootPaneContainer;
import javax.swing.JComponent;
import java.awt.Container;
import java.util.Enumeration;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.event.MouseListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Insets;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Dimension;
import com.adventnet.tools.update.CommonUtil;
import java.awt.Dialog;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.tree.DefaultTreeModel;
import java.net.URL;
import java.util.Hashtable;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Vector;
import java.awt.CardLayout;
import javax.swing.JFrame;
import java.awt.event.MouseAdapter;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.JTable;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JDialog;

public class ViewDialog extends JDialog implements Cursors
{
    private JPanel ViewPanel;
    private JButton btnTreeHeader;
    private JButton cancelBtn;
    private JPanel cardPanel;
    private JEditorPane confEditorPane;
    private JPanel diffPanel;
    private JToolBar diffToolbar;
    private JScrollPane editorPane;
    private JButton helpBtn;
    private JPanel jPanel5;
    private JScrollPane jScrollPane1;
    private JSplitPane jSplitPane1;
    private JTable jTableDisplay;
    private JTree jtreeDiff;
    private JButton saveBtn;
    private JPanel tablePanel;
    private JScrollPane tableScrollPane;
    private JPanel toolBarPanel;
    private JPanel treePanel;
    private int counter;
    private String ppmFile;
    private Font uf;
    public static boolean initTree;
    private ImageIcon categoryIcon;
    private ImageIcon homeIcon;
    private ImageIcon treeLeafIcon;
    private ImageIcon addIcon;
    private ImageIcon modIcon;
    private ImageIcon reintroIcon;
    private ImageIcon excepIcon;
    private ImageIcon helpIcon;
    private ImageIcon saveIcon;
    private ImageIcon cancelIcon;
    private static final MouseAdapter mouseAdapter;
    private JFrame dialog;
    public String homeDir;
    JEditorPane confJEditor;
    CardLayout diffCard;
    public static String prodName;
    public static String prodVersion;
    public static String subProd;
    private String patchFile;
    public String category;
    Vector colvect;
    public String strWarning;
    DefaultMutableTreeNode root;
    DefaultTreeCellRenderer dsrenderer;
    Hashtable diffhash;
    SortTableModel model;
    URL confURL;
    DiffUtility diffutil;
    DefaultTreeModel dtmodel;
    String[] nodes;
    String helpFile;
    String filesep;
    Hashtable mapDisplay;
    JDialog dlg;
    JScrollPane editorScrollPane;
    JFileChooser fc;
    DocumentNodeProps[] nodeProps;
    Hashtable exceptionHash;
    ArrayList sortedList;
    int dividerVal;
    
    public ViewDialog(final JDialog parent, final boolean modal) {
        super(parent, modal);
        this.counter = 0;
        this.ppmFile = null;
        this.uf = null;
        this.dialog = null;
        this.homeDir = null;
        this.confJEditor = null;
        this.diffCard = null;
        this.patchFile = null;
        this.category = null;
        this.strWarning = "The higlighted files will be Replaced,while applying the ppm.Backup the files before applying !";
        this.root = null;
        this.dsrenderer = null;
        this.diffhash = null;
        this.confURL = null;
        this.diffutil = null;
        this.dtmodel = null;
        this.nodes = null;
        this.helpFile = null;
        this.filesep = "";
        this.mapDisplay = null;
        this.dlg = null;
        this.editorScrollPane = null;
        this.nodeProps = null;
        this.exceptionHash = new Hashtable();
        this.dividerVal = 0;
        this.initComponents();
        this.setTitle(CommonUtil.getString("PreView"));
        this.setSize(new Dimension(450, 300));
    }
    
    public ViewDialog(final JFrame jf, final String phome, final String pname, final String pversion, final String subprod, final String patchFile, final DiffUtility du) {
        super(jf, true);
        this.counter = 0;
        this.ppmFile = null;
        this.uf = null;
        this.dialog = null;
        this.homeDir = null;
        this.confJEditor = null;
        this.diffCard = null;
        this.patchFile = null;
        this.category = null;
        this.strWarning = "The higlighted files will be Replaced,while applying the ppm.Backup the files before applying !";
        this.root = null;
        this.dsrenderer = null;
        this.diffhash = null;
        this.confURL = null;
        this.diffutil = null;
        this.dtmodel = null;
        this.nodes = null;
        this.helpFile = null;
        this.filesep = "";
        this.mapDisplay = null;
        this.dlg = null;
        this.editorScrollPane = null;
        this.nodeProps = null;
        this.exceptionHash = new Hashtable();
        this.dividerVal = 0;
        this.setUMFont();
        ViewDialog.prodName = pname;
        this.diffutil = du;
        ViewDialog.prodVersion = pversion;
        this.homeDir = phome;
        this.patchFile = patchFile;
        ViewDialog.subProd = subprod;
        this.initComponents();
        this.postTabbedPane();
        this.dialog = jf;
        this.setTitle(CommonUtil.getString("PreView"));
        this.preInitViewDialog();
        this.pack();
        this.centerTheWindow();
    }
    
    private void initComponents() {
        this.jPanel5 = new JPanel();
        this.diffPanel = new JPanel();
        this.toolBarPanel = new JPanel();
        this.diffToolbar = new JToolBar();
        this.saveBtn = new JButton();
        this.cancelBtn = new JButton();
        this.helpBtn = new JButton();
        this.ViewPanel = new JPanel();
        this.jSplitPane1 = new JSplitPane();
        this.jScrollPane1 = new JScrollPane();
        this.treePanel = new JPanel();
        this.btnTreeHeader = new JButton();
        this.jtreeDiff = new JTree();
        this.cardPanel = new JPanel();
        this.tablePanel = new JPanel();
        this.tableScrollPane = new JScrollPane();
        this.jTableDisplay = new JTable();
        this.editorPane = new JScrollPane();
        this.confEditorPane = new JEditorPane();
        this.getContentPane().setLayout(new BorderLayout(20, 10));
        this.setDefaultCloseOperation(2);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent evt) {
                ViewDialog.this.formComponentResized(evt);
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                ViewDialog.this.closeDialog(evt);
            }
        });
        this.diffPanel.setLayout(new BorderLayout(10, 10));
        this.diffPanel.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
        this.diffPanel.setMinimumSize(new Dimension(500, 350));
        this.toolBarPanel.setLayout(new BorderLayout());
        this.toolBarPanel.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
        this.PostinitDiff();
        this.diffToolbar.setBorder(null);
        this.saveBtn.setIcon(this.saveIcon);
        this.saveBtn.setMnemonic('S');
        this.saveBtn.setToolTipText(CommonUtil.getString("Save"));
        this.saveBtn.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
        this.saveBtn.setMaximumSize(new Dimension(50, 50));
        this.saveBtn.setMinimumSize(new Dimension(50, 50));
        this.saveBtn.setPreferredSize(new Dimension(50, 50));
        this.saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ViewDialog.this.saveBtnActionPerformed(evt);
            }
        });
        this.saveBtn.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(final MouseEvent evt) {
                ViewDialog.this.saveBtnMouseMoved(evt);
            }
        });
        this.diffToolbar.add(this.saveBtn);
        this.cancelBtn.setIcon(this.cancelIcon);
        this.cancelBtn.setToolTipText(CommonUtil.getString("Close"));
        this.cancelBtn.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
        this.cancelBtn.setMaximumSize(new Dimension(50, 50));
        this.cancelBtn.setMinimumSize(new Dimension(50, 50));
        this.cancelBtn.setPreferredSize(new Dimension(50, 50));
        this.cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ViewDialog.this.cancelBtnActionPerformed(evt);
            }
        });
        this.cancelBtn.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(final MouseEvent evt) {
                ViewDialog.this.cancelBtnMouseMoved(evt);
            }
        });
        this.diffToolbar.add(this.cancelBtn);
        this.toolBarPanel.add(this.diffToolbar, "Center");
        this.helpBtn.setIcon(this.helpIcon);
        this.helpBtn.setMnemonic('H');
        this.helpBtn.setToolTipText(CommonUtil.getString("Help"));
        this.helpBtn.setActionCommand("help");
        this.helpBtn.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
        this.helpBtn.setMaximumSize(new Dimension(50, 50));
        this.helpBtn.setMinimumSize(new Dimension(50, 50));
        this.helpBtn.setPreferredSize(new Dimension(50, 50));
        this.diffToolbar.addSeparator();
        this.helpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ViewDialog.this.helpBtnActionPerformed(evt);
            }
        });
        this.helpBtn.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(final MouseEvent evt) {
                ViewDialog.this.helpBtnMouseMoved(evt);
            }
        });
        this.toolBarPanel.add(this.helpBtn, "East");
        this.diffPanel.add(this.toolBarPanel, "North");
        this.ViewPanel.setLayout(new BorderLayout());
        this.ViewPanel.setBackground(new Color(255, 255, 255));
        this.dlg = new JDialog(this, true);
        (this.confJEditor = new JEditorPane()).setEditable(false);
        this.editorScrollPane = new JScrollPane(this.confJEditor);
        this.dlg.getContentPane().add(this.editorScrollPane);
        this.initTreeComponents();
        this.jSplitPane1.setBorder(null);
        this.jSplitPane1.setDividerLocation(205);
        this.jSplitPane1.setDividerSize(0);
        this.treePanel.setLayout(new BorderLayout());
        this.btnTreeHeader.setBackground(new Color(204, 204, 255));
        this.btnTreeHeader.setText(CommonUtil.getString("Groups"));
        this.btnTreeHeader.setHorizontalAlignment(2);
        this.btnTreeHeader.setHorizontalTextPosition(2);
        this.treePanel.add(this.btnTreeHeader, "North");
        this.jtreeDiff.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
        this.jtreeDiff.setFont(this.uf);
        this.jtreeDiff.setAlignmentX(0.1f);
        this.jtreeDiff.setAlignmentY(0.1f);
        this.jtreeDiff.setModel(this.dtmodel);
        this.jtreeDiff.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent evt) {
                ViewDialog.this.jtreeDiffValueChanged(evt);
            }
        });
        this.treePanel.add(this.jtreeDiff, "Center");
        this.jScrollPane1.setViewportView(this.treePanel);
        this.jSplitPane1.setLeftComponent(this.jScrollPane1);
        this.cardPanel.setLayout(new CardLayout());
        this.tablePanel.setLayout(new BorderLayout());
        final BottomPanel bottomPanel = new BottomPanel();
        this.tablePanel.setLayout(new BorderLayout());
        this.tablePanel.add(bottomPanel, "South");
        this.tableScrollPane.setMinimumSize(new Dimension(22, 26));
        this.colvect = new Vector();
        this.model = new SortTableModel(new Vector(), this.getHeaderNames());
        this.jTableDisplay.setModel(this.model);
        this.model.addMouseListenerToHeaderInTable(this.jTableDisplay);
        final TableHeaderRenderer tgRenderer = new TableHeaderRenderer();
        for (int i = 0; i < 4; ++i) {
            this.jTableDisplay.getColumnModel().getColumn(i).setHeaderRenderer(tgRenderer);
        }
        this.jTableDisplay.getTableHeader().addMouseListener(tgRenderer);
        this.jTableDisplay.setAutoCreateColumnsFromModel(false);
        this.jTableDisplay.setAutoResizeMode(4);
        this.tableScrollPane.setViewportView(this.jTableDisplay);
        this.tablePanel.add(this.tableScrollPane, "Center");
        this.cardPanel.add(this.tablePanel, "card4");
        this.editorPane.setHorizontalScrollBarPolicy(32);
        this.editorPane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent evt) {
                ViewDialog.this.editorPaneMouseDragged(evt);
            }
        });
        this.confEditorPane.setAutoscrolls(false);
        this.editorPane.setViewportView(this.confEditorPane);
        this.cardPanel.add(this.editorPane, "confCard");
        this.jSplitPane1.setRightComponent(this.cardPanel);
        this.ViewPanel.add(this.jSplitPane1, "Center");
        this.diffPanel.add(this.ViewPanel, "Center");
        this.getContentPane().add(this.diffPanel, "Center");
        this.pack();
    }
    
    private void editorPaneMouseDragged(final MouseEvent evt) {
    }
    
    private void helpBtnMouseMoved(final MouseEvent evt) {
        this.helpBtn.setBorderPainted(true);
    }
    
    private void cancelBtnMouseMoved(final MouseEvent evt) {
        this.cancelBtn.setBorder(null);
    }
    
    private void saveBtnMouseMoved(final MouseEvent evt) {
        this.saveBtn.setBorder(null);
    }
    
    private void cancelBtnActionPerformed(final ActionEvent evt) {
        this.setVisible(false);
        this.dispose();
    }
    
    private void formComponentResized(final ComponentEvent evt) {
    }
    
    private void saveBtnActionPerformed(final ActionEvent evt) {
        this.saveAll();
    }
    
    private void helpBtnActionPerformed(final ActionEvent evt) {
        this.showHelp("Help", this.helpFile);
    }
    
    private void jtreeDiffValueChanged(final TreeSelectionEvent evt) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.jtreeDiff.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        final String strnode = node.toString();
        if (node.isLeaf()) {
            this.populateFiles(strnode);
            if (this.dividerVal != 0) {
                this.ShowWarning(true);
            }
            else {
                this.ShowWarning(false);
            }
        }
        else {
            this.ShowWarning(false);
            this.clearAll();
        }
    }
    
    private void closeDialog(final WindowEvent evt) {
        this.setVisible(false);
        this.dispose();
    }
    
    public static void main(final String[] args) {
        new ViewDialog(new JDialog(), true).show();
    }
    
    private void setPpm(final String ppm) {
        this.ppmFile = ppm;
    }
    
    public void InitTree() {
        try {
            this.processHierarchy();
            final DefaultMutableTreeNode dfnode = null;
            final String parent = null;
            (this.dsrenderer = new DefaultTreeCellRenderer()).setOpenIcon(this.categoryIcon);
            this.dsrenderer.setClosedIcon(this.homeIcon);
            this.dsrenderer.setLeafIcon(this.treeLeafIcon);
            this.jtreeDiff.setCellRenderer(this.dsrenderer);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void processHierarchy() {
        this.sortedList = new ArrayList();
        this.nodes = this.diffutil.getBaseNodeIDs();
        this.diffhash = new Hashtable();
        this.nodeProps = this.diffutil.getDocumentNodes();
        if (this.nodeProps.length != 0) {
            String nodename = null;
            for (int cnp = 0; cnp < this.nodeProps.length; ++cnp) {
                nodename = this.nodeProps[cnp].getDisplayName();
                if (this.nodeProps[cnp].getID().equalsIgnoreCase("DiffViewerHelp")) {
                    this.helpFile = this.nodeProps[cnp].getDocumentFile().getPath();
                }
                else if (this.nodeProps[cnp].getID().equalsIgnoreCase("ConfChanges")) {
                    this.diffhash.put(nodename, this.nodeProps[cnp]);
                    this.root.add(new DefaultMutableTreeNode(nodename));
                }
            }
        }
        this.mapDisplay = this.diffutil.getDisplayVector(this.nodes);
        final Enumeration e = this.mapDisplay.keys();
        while (e.hasMoreElements()) {
            final String node = e.nextElement().toString();
            final Hashtable temp = this.diffutil.getFileList(node);
            if (!temp.isEmpty()) {
                final Enumeration et = temp.keys();
                while (et.hasMoreElements()) {
                    final String str = et.nextElement().toString().trim();
                    if (this.diffhash.get(str) == null && !str.equalsIgnoreCase("Others")) {
                        this.sortedList.add(str);
                    }
                    this.addToDiffhash(str, temp.get(str), node);
                }
            }
        }
        this.sortList();
        this.root.add(new DefaultMutableTreeNode("Others"));
        this.diffutil.cleanResource();
    }
    
    private void populateFiles(final String p) {
        this.clearAll();
        if (this.diffhash.get(p).getClass().getName().equalsIgnoreCase("com.adventnet.tools.update.viewer.DocumentNodeProps")) {
            final DocumentNodeProps docnode = this.diffhash.get(p);
            ((CardLayout)this.cardPanel.getLayout()).show(this.cardPanel, "confCard");
            this.showHTML(docnode.getDisplayName(), docnode.getDocumentFile().getPath());
            return;
        }
        ((CardLayout)this.cardPanel.getLayout()).show(this.cardPanel, "table");
        this.editorPane.setVisible(false);
        this.tablePanel.setVisible(true);
        final Hashtable temp = this.diffhash.get(p);
        final Enumeration et = temp.keys();
        while (et.hasMoreElements()) {
            final String node = et.nextElement().toString();
            final Vector tempcat = temp.get(node);
            if (!tempcat.isEmpty()) {
                if (node.equalsIgnoreCase("modified")) {
                    this.populateTable(tempcat, this.modIcon, true, p);
                }
                else if (node.equalsIgnoreCase("added")) {
                    this.addIcon.setDescription("added");
                    this.populateTable(tempcat, this.addIcon, false, p);
                }
                else {
                    if (!node.equalsIgnoreCase("reintroduced")) {
                        continue;
                    }
                    this.reintroIcon.setDescription("reintroduced");
                    this.populateTable(tempcat, this.reintroIcon, false, p);
                }
            }
        }
    }
    
    private void populateTable(final Vector cat, final ImageIcon icon, final boolean except, final String p) {
        final int s = cat.size();
        int fsep = 0;
        int fstart = 0;
        int len = 0;
        this.filesep = "/";
        String type = "";
        String fname = null;
        String path = null;
        final String strapp = " File";
        if (except) {
            final Vector tobeColored = this.exceptionHash.get(p);
            int[] arr = null;
            this.dividerVal = 0;
            if (tobeColored != null) {
                this.dividerVal = tobeColored.size();
                arr = new int[tobeColored.size()];
                for (int cc = 0; cc < tobeColored.size(); ++cc) {
                    fname = tobeColored.elementAt(cc);
                    len = fname.length();
                    fstart = fname.lastIndexOf("/");
                    fsep = fname.lastIndexOf(".");
                    if (fsep == -1) {
                        fsep = len;
                        type = "File";
                    }
                    else {
                        type = fname.substring(fsep + 1, len) + strapp;
                    }
                    if (fstart == -1) {
                        fstart = 0;
                        path = this.filesep;
                        fname = fname.substring(fstart);
                    }
                    else {
                        path = fname.substring(0, fstart + 1);
                        fname = fname.substring(fstart + 1);
                    }
                    this.excepIcon.setDescription("exception");
                    final Object[] rowObj = { this.excepIcon, fname, path, type };
                    this.model.addRow(rowObj);
                    arr[cc] = cc;
                }
            }
            for (int rem = 0; rem < cat.size(); ++rem) {
                if (tobeColored == null || !tobeColored.contains(cat.elementAt(rem))) {
                    fname = cat.elementAt(rem);
                    len = fname.length();
                    fstart = fname.lastIndexOf("/");
                    fsep = fname.lastIndexOf(".");
                    if (fsep == -1) {
                        fsep = len;
                        type = "File";
                    }
                    else {
                        type = fname.substring(fsep + 1, len) + strapp;
                    }
                    if (fstart == -1) {
                        fstart = 0;
                        path = this.filesep;
                        fname = fname.substring(fstart);
                    }
                    else {
                        path = fname.substring(0, fstart + 1);
                        fname = fname.substring(fstart + 1);
                    }
                    this.modIcon.setDescription("modified");
                    final Object[] rowObj = { this.modIcon, fname, path, type };
                    this.model.addRow(rowObj);
                }
            }
            return;
        }
        for (int cc2 = 0; cc2 < cat.size(); ++cc2) {
            fname = cat.elementAt(cc2);
            len = fname.length();
            fstart = fname.lastIndexOf("/");
            fsep = fname.lastIndexOf(".");
            if (fsep == -1) {
                fsep = len;
                type = "File";
            }
            else {
                type = fname.substring(fsep + 1, len) + strapp;
            }
            if (fstart == -1) {
                fstart = 0;
                path = this.filesep;
                fname = fname.substring(fstart);
            }
            else {
                path = fname.substring(0, fstart + 1);
                fname = fname.substring(fstart + 1);
            }
            final Object[] rowObj = { icon, fname, path, type };
            this.model.addRow(rowObj);
        }
    }
    
    private boolean isCategory(final String search) {
        for (int count = 0; count < this.nodes.length; ++count) {
            if (this.nodes[count].equals(search)) {
                return true;
            }
        }
        return false;
    }
    
    public static void startWaitCursor(final JComponent component) {
        final RootPaneContainer root = (RootPaneContainer)component.getTopLevelAncestor();
        root.getGlassPane().setCursor(ViewDialog.WAIT_CURSOR);
        root.getGlassPane().addMouseListener(ViewDialog.mouseAdapter);
        root.getGlassPane().setVisible(true);
    }
    
    public static void stopWaitCursor(final JComponent component) {
        final RootPaneContainer root = (RootPaneContainer)component.getTopLevelAncestor();
        root.getGlassPane().setCursor(ViewDialog.DEFAULT_CURSOR);
        root.getGlassPane().removeMouseListener(ViewDialog.mouseAdapter);
        root.getGlassPane().setVisible(false);
    }
    
    private void addToDiffhash(final String key, final Vector valvect, String cat) {
        if (cat.equalsIgnoreCase("ExceptionCases") || cat.equalsIgnoreCase("ModifiedFiles")) {
            if (cat.equalsIgnoreCase("ExceptionCases")) {
                this.exceptionHash.put(key, new Vector(valvect));
            }
            cat = "modified";
        }
        else if (cat.equalsIgnoreCase("NewFiles")) {
            cat = "added";
        }
        else if (cat.equalsIgnoreCase("ReIntroducedFiles")) {
            cat = "reintroduced";
        }
        Hashtable temp = null;
        Vector tempvect = null;
        if (this.diffhash.containsKey(key)) {
            temp = this.diffhash.get(key);
            if (temp.containsKey(cat)) {
                tempvect = temp.get(cat);
                tempvect.addAll(valvect);
                temp.put(key, valvect);
            }
            else {
                temp.put(cat, valvect);
            }
        }
        else {
            temp = new Hashtable();
            temp.put(cat, valvect);
            this.diffhash.put(key, temp);
        }
    }
    
    private void initTreeComponents() {
        this.root = new DefaultMutableTreeNode(CommonUtil.getString("Results"));
        this.dtmodel = new DefaultTreeModel(this.root);
    }
    
    private void postTabbedPane() {
        this.colvect = new Vector();
        this.initCellRenderer();
        ((CardLayout)this.cardPanel.getLayout()).show(this.cardPanel, "table");
    }
    
    private void initCellRenderer() {
        this.setTableColumnRenderer();
    }
    
    private URL createConfURL(final String fileName) {
        final String url = "file:" + this.homeDir + System.getProperty("file.separator") + fileName;
        try {
            this.confURL = new URL(url);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return this.confURL;
    }
    
    private void PostinitDiff() {
        this.categoryIcon = new ImageIcon();
        this.setcategoryIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/categories.png", null, true));
        this.sethomeIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/home.png", null, true));
        this.settreeLeafIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/leaf.png", null, true));
        this.setsaveIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/save.png", null, true));
        this.setcancelIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/exit.png", null, true));
        this.setaddIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/added.png", null, true));
        this.setmodifyIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/overwritten.png", null, true));
        this.setreintroIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/reintroduced.png", null, true));
        this.setexcepIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/exceptions.png", null, true));
        this.sethelpIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/diffhelp.png", null, true));
        final ComponentOrientation co = this.diffToolbar.getComponentOrientation();
    }
    
    private void showHTML(final String title, String fname) {
        try {
            this.tablePanel.setVisible(false);
            fname = this.setHtmlPage(fname);
            final URL url = new URL("file", "localhost", fname);
            this.editorPane.setVisible(true);
            this.confEditorPane.setPage(url);
            this.confEditorPane.setEditable(false);
            this.confEditorPane.addHyperlinkListener(new DiffHyperlinkListener());
            this.editorPane.updateUI();
            this.confEditorPane.updateUI();
        }
        catch (final IOException ex) {}
    }
    
    private void ShowWarning(final boolean show) {
    }
    
    private void preInitViewDialog() {
        stopWaitCursor(this.dialog.getRootPane());
        this.setSize(750, 650);
        startWaitCursor(this.ViewPanel);
        this.root.removeAllChildren();
        this.InitTree();
        this.jtreeDiff.setShowsRootHandles(true);
        this.jtreeDiff.setRootVisible(false);
        this.jtreeDiff.putClientProperty("JTree.lineStyle", "None");
        this.dtmodel.reload();
        stopWaitCursor(this.ViewPanel);
    }
    
    private void setUMFont() {
        this.uf = UpdateManagerUtil.getFont();
    }
    
    private void setaddIcon(final ImageIcon add) {
        this.addIcon = add;
    }
    
    private void setmodifyIcon(final ImageIcon mod) {
        this.modIcon = mod;
    }
    
    private void setreintroIcon(final ImageIcon reintro) {
        this.reintroIcon = reintro;
    }
    
    private void setcategoryIcon(final ImageIcon ci) {
        this.categoryIcon = ci;
    }
    
    private void settreeLeafIcon(final ImageIcon ti) {
        this.treeLeafIcon = ti;
    }
    
    private void sethomeIcon(final ImageIcon hi) {
        this.homeIcon = hi;
    }
    
    private void sethelpIcon(final ImageIcon help) {
        this.helpIcon = help;
    }
    
    private void setsaveIcon(final ImageIcon save) {
        this.saveIcon = save;
    }
    
    private void setcancelIcon(final ImageIcon cancel) {
        this.cancelIcon = cancel;
    }
    
    private void setexcepIcon(final ImageIcon ex) {
        this.excepIcon = ex;
    }
    
    private void showErrorDialog(final String message, final JDialog frame) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(frame, message, "Error", 2);
            }
        };
        SwingUtilities.invokeLater(r);
    }
    
    private void showError(final String message, final JDialog frame) {
        this.showErrorDialog(message, frame);
    }
    
    private void clearAll() {
        this.model.getDataVector().clear();
    }
    
    private void saveAll() {
        final DefaultMutableTreeNode dmt = (DefaultMutableTreeNode)this.jtreeDiff.getLastSelectedPathComponent();
        if (dmt == null || dmt.isRoot()) {
            JOptionPane.showMessageDialog(this, CommonUtil.getString("Select the Category to save"), CommonUtil.getString("PreView"), 1);
            return;
        }
        if (dmt.isLeaf() && !this.saveToFile(dmt.toString())) {
            return;
        }
    }
    
    private void jlstToFile(final Vector fileVect, final StringBuffer sb, final Vector ex) {
        String n = null;
        final String nl = "\n";
        sb.append(nl);
        sb.append("---------------------------------------");
        sb.append(nl);
        for (int c = 0; c < fileVect.size(); ++c) {
            n = fileVect.elementAt(c).toString();
            if (ex == null || !ex.contains(n)) {
                sb.append(n);
                sb.append(nl);
            }
        }
    }
    
    private void centerTheWindow() {
        final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - this.getWidth()) / 2, (d.height - this.getHeight()) / 2);
        this.setVisible(true);
    }
    
    private void sortList() {
        String[] groups = new String[this.sortedList.size()];
        groups = this.sortedList.toArray(groups);
        Arrays.sort(groups);
        this.addToTree(groups);
    }
    
    private void addToTree(final String[] toadd) {
        for (int i = 0; i < toadd.length; ++i) {
            this.root.add(new DefaultMutableTreeNode(toadd[i]));
        }
    }
    
    private void setTableColumnRenderer() {
        final DiffCellRenderer dcr = new DiffCellRenderer();
        for (int col = 0; col < this.jTableDisplay.getColumnCount(); ++col) {
            final TableColumn tc = this.jTableDisplay.getColumnModel().getColumn(col);
            tc.setCellRenderer(dcr);
        }
        this.jTableDisplay.getColumn("Category").setMinWidth(23);
        this.jTableDisplay.getColumn("Category").setMaxWidth(23);
        this.jTableDisplay.getColumn("Type").setMinWidth(90);
        this.jTableDisplay.getColumn("Type").setMaxWidth(100);
    }
    
    private void setSelectionColor(final JList js) {
        js.setSelectionBackground(Color.blue);
        js.setSelectionForeground(Color.white);
    }
    
    private Vector getHeaderNames() {
        this.colvect.addElement("Category");
        this.colvect.addElement("FileName");
        this.colvect.addElement("Path");
        this.colvect.addElement("Type");
        return this.colvect;
    }
    
    private String getFileType(final String file) {
        final StringBuffer temp = new StringBuffer(file);
        return file.substring(file.lastIndexOf("."), file.length());
    }
    
    private boolean saveToFile(final String selnode) {
        if (this.diffhash.get(selnode) instanceof DocumentNodeProps) {
            JOptionPane.showMessageDialog(this, CommonUtil.getString("Can not be saved as text file"), CommonUtil.getString("PreView"), 1);
            return false;
        }
        File file = null;
        final JFileChooser fc = new JFileChooser();
        final TextFilter textFilter = new TextFilter();
        fc.setCurrentDirectory(new File("."));
        fc.setFileFilter(textFilter);
        fc.setSelectedFile(file);
        final int result = fc.showSaveDialog(this);
        if (result == 1) {
            return true;
        }
        if (result == 0) {
            file = fc.getSelectedFile();
            final String filestr = file.getAbsolutePath() + file.getName() + ".txt";
            file.renameTo(new File(filestr));
            if (file.exists()) {
                final int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite", 2, 3);
                if (response == 2) {
                    return false;
                }
            }
            return this.writetoTextFile(file, selnode);
        }
        return false;
    }
    
    private boolean writetoTextFile(File file, final String node) {
        try {
            if (!file.getName().endsWith(".txt")) {
                final String ext = ".txt";
                file = new File(file.getCanonicalPath() + ext);
            }
            final StringBuffer sb = new StringBuffer("");
            final FileWriter fw = new FileWriter(file);
            final String nl = "\n";
            final StringBuffer cat = new StringBuffer();
            final Hashtable temp = this.diffhash.get(node);
            final Vector except = this.exceptionHash.get(node);
            if (except != null && except.size() != 0) {
                sb.append("---------------------------------------\n");
                sb.append(CommonUtil.getString("Overwitten Open Source Files"));
                this.jlstToFile(except, sb, null);
            }
            final Enumeration et = temp.keys();
            while (et.hasMoreElements()) {
                final String filestr = et.nextElement().toString();
                final Vector tempcat = temp.get(filestr);
                if (!filestr.equalsIgnoreCase("added") && !filestr.equalsIgnoreCase("modified") && !filestr.equalsIgnoreCase("reintroduced")) {
                    continue;
                }
                if (except != null && except.size() != 0 && filestr.equalsIgnoreCase("modified")) {
                    final Vector rem = new Vector(tempcat);
                    rem.removeAll(except);
                    if (rem.size() == 0) {
                        continue;
                    }
                    sb.append("---------------------------------------\n");
                    sb.append(this.getLegendName(filestr) + "  Files ");
                    this.jlstToFile(rem, sb, null);
                }
                else {
                    if (tempcat.size() == 0) {
                        continue;
                    }
                    sb.append("---------------------------------------\n");
                    sb.append(this.getLegendName(filestr) + "    Files ");
                    this.jlstToFile(tempcat, sb, except);
                }
            }
            fw.write(sb.toString());
            fw.flush();
            fw.close();
            return true;
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }
    
    public static String getExtension(final File f) {
        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf(46);
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
    
    public String setHtmlPage(final String urlName) {
        String current_urlName = "";
        if (urlName.startsWith("patchtemp")) {
            if (urlName.startsWith("/")) {
                current_urlName = urlName.substring(1);
            }
            else {
                current_urlName = urlName;
            }
        }
        else {
            current_urlName = urlName;
        }
        return current_urlName;
    }
    
    private void showHelp(final String title, final String fname) {
        final String tname = CommonUtil.getString("PreView") + " " + CommonUtil.getString(title);
        final ViewDialog view = this;
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                final ReadmeUI readme = new ReadmeUI();
                final ReadMeWrapper reme = new ReadMeWrapper(view);
                reme.setModal(true);
                readme.setReadmeTitle(tname + " " + CommonUtil.getString("is shown below"));
                reme.setDialogTitle(tname);
                reme.init();
                reme.addReadMePanel(readme);
                reme.setPage(fname);
                reme.showCornered();
            }
        };
        SwingUtilities.invokeLater(r);
    }
    
    private String getLegendName(final String cat) {
        String dname = "";
        if (cat.equalsIgnoreCase("overwritten")) {
            dname = CommonUtil.getString("Overwitten Open Source Files");
        }
        else if (cat.equalsIgnoreCase("modified")) {
            dname = CommonUtil.getString("Modified Files");
        }
        else if (cat.equalsIgnoreCase("added")) {
            dname = CommonUtil.getString("New Files");
        }
        else if (cat.equalsIgnoreCase("reintroduced")) {
            dname = CommonUtil.getString("Re-Introduced Files");
        }
        return dname;
    }
    
    static {
        ViewDialog.initTree = false;
        mouseAdapter = new MouseAdapter() {};
        ViewDialog.prodName = null;
        ViewDialog.prodVersion = null;
        ViewDialog.subProd = null;
    }
    
    class DiffCellRenderer extends JLabel implements TableCellRenderer
    {
        public DiffCellRenderer() {
            this.setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean Selected, final boolean hasFocus, final int row, final int column) {
            this.setFont(ViewDialog.this.uf);
            if (table.getColumnName(column).equalsIgnoreCase("Category") && value instanceof ImageIcon) {
                final ImageIcon img = (ImageIcon)value;
                this.setIcon(img);
                this.setText("");
            }
            else {
                this.setText(value.toString());
                this.setIcon(null);
                this.setBackground(Selected ? Color.blue : Color.white);
                this.setForeground(Selected ? Color.white : Color.black);
            }
            if (row % 2 == 0) {
                this.setBackground(new Color(204, 204, 204));
            }
            this.setBackground(Selected ? Color.blue : Color.white);
            this.setForeground(Selected ? Color.white : Color.black);
            return this;
        }
    }
    
    public class TextFilter extends FileFilter
    {
        @Override
        public boolean accept(final File f) {
            return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
        }
        
        @Override
        public String getDescription() {
            return "Text files (*.txt)";
        }
    }
    
    class DiffHyperlinkListener implements HyperlinkListener
    {
        @Override
        public void hyperlinkUpdate(final HyperlinkEvent e) {
            try {
                if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                    ViewDialog.this.confEditorPane.setCursor(Cursor.getPredefinedCursor(12));
                }
                else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                    ViewDialog.this.confEditorPane.setCursor(Cursor.getPredefinedCursor(0));
                }
                else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    CommonUtil.displayURL(e.getURL().toExternalForm());
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    private class TableHeaderRenderer extends JButton implements TableCellRenderer, MouseListener
    {
        String sortedColumn;
        boolean isAsc;
        
        public TableHeaderRenderer() {
            this.sortedColumn = "";
            this.isAsc = true;
            this.setFont(ViewDialog.this.uf);
            this.setBorder(new TableHeaderBorder());
            this.setSize(this.getWidth(), this.getHeight() - 15);
            this.setHorizontalTextPosition(2);
        }
        
        public String getSortedColumn() {
            return this.sortedColumn;
        }
        
        public boolean isAscending() {
            return this.isAsc;
        }
        
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            final String columnName = table.getColumnName(column);
            if (columnName.equals("Category")) {
                this.setText("");
            }
            else {
                this.setText(value.toString());
            }
            if (!columnName.equals(this.sortedColumn)) {
                this.setIcon(null);
            }
            else if (this.isAsc) {
                this.setIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/ascendarrow.png", null, true));
            }
            else {
                this.setIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/descendarrow.png", null, true));
            }
            return this;
        }
        
        @Override
        public void mouseClicked(final MouseEvent event) {
            final TableColumnModel columnModel = ViewDialog.this.jTableDisplay.getColumnModel();
            final int viewColumn = columnModel.getColumnIndexAtX(event.getX());
            final String identifier = ViewDialog.this.jTableDisplay.getColumnName(viewColumn);
            if (event.getClickCount() == 1) {
                if (!identifier.equals(this.sortedColumn)) {
                    this.sortedColumn = identifier;
                }
                else {
                    this.isAsc = !this.isAsc;
                }
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent event) {
        }
        
        @Override
        public void mouseExited(final MouseEvent event) {
        }
        
        @Override
        public void mousePressed(final MouseEvent event) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent event) {
        }
    }
    
    public static class TableHeaderBorder extends AbstractBorder
    {
        protected Insets editorBorderInsets;
        
        public TableHeaderBorder() {
            this.editorBorderInsets = new Insets(2, 2, 2, 0);
        }
        
        @Override
        public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w, final int h) {
            g.translate(x, y);
            g.setColor(MetalLookAndFeel.getControlDarkShadow());
            g.drawLine(w - 1, 0, w - 1, h - 1);
            g.drawLine(1, h - 1, w - 1, h - 1);
            g.setColor(MetalLookAndFeel.getControlHighlight());
            g.drawLine(0, 0, w - 2, 0);
            g.drawLine(0, 0, 0, h - 2);
            g.translate(-x, -y);
        }
        
        @Override
        public Insets getBorderInsets(final Component c) {
            return this.editorBorderInsets;
        }
    }
    
    private class BottomPanel extends JPanel
    {
        JPanel Top;
        JLabel lblOver;
        JLabel lblMod;
        JLabel lblNew;
        JLabel lblReintro;
        
        public void init() {
            this.setPreferredSize(new Dimension(this.getPreferredSize().width + 432, this.getPreferredSize().height + 60));
            this.setSize(this.getPreferredSize());
            final Container container = this;
            container.setLayout(new BorderLayout());
            this.initVariables();
            this.setUpGUI(container);
            this.setUpProperties();
        }
        
        public void setUpProperties() {
            this.Top.setBorder(new TitledBorder(new EtchedBorder(1), " Legend   ", 0, 0, ViewDialog.this.uf, new Color(-16382458)));
            this.lblOver.setHorizontalAlignment(2);
            this.lblOver.setForeground(new Color(-16777216));
            this.lblOver.setHorizontalTextPosition(4);
            this.lblOver.setFont(ViewDialog.this.uf);
            this.lblOver.setIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/exceptions.png", null, true));
            this.lblOver.setText(CommonUtil.getString("Overwitten Open Source Files"));
            this.lblOver.setMinimumSize(new Dimension(185, 16));
            this.lblMod.setHorizontalAlignment(2);
            this.lblMod.setForeground(new Color(-16777216));
            this.lblMod.setHorizontalTextPosition(4);
            this.lblMod.setFont(ViewDialog.this.uf);
            this.lblMod.setText(CommonUtil.getString("Modified Files"));
            this.lblMod.setIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/overwritten.png", null, true));
            this.lblNew.setHorizontalAlignment(2);
            this.lblNew.setForeground(new Color(-16777216));
            this.lblNew.setHorizontalTextPosition(4);
            this.lblNew.setFont(ViewDialog.this.uf);
            this.lblNew.setText(CommonUtil.getString("New Files"));
            this.lblNew.setIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/added.png", null, true));
            this.lblReintro.setHorizontalAlignment(2);
            this.lblReintro.setForeground(new Color(-16777216));
            this.lblReintro.setHorizontalTextPosition(4);
            this.lblReintro.setFont(ViewDialog.this.uf);
            this.lblReintro.setText(CommonUtil.getString("Re-Introduced Files"));
            this.lblReintro.setIcon(Utility.findImage("com/adventnet/tools/update/viewer/images/reintroduced.png", null, true));
        }
        
        public void initVariables() {
            this.Top = new JPanel();
            this.lblOver = new JLabel();
            this.lblMod = new JLabel();
            this.lblNew = new JLabel();
            this.lblReintro = new JLabel();
        }
        
        public void setUpGUI(final Container container) {
            container.add(this.Top, "Center");
            this.Top.setLayout(new GridLayout(2, 2));
            this.lblReintro.setBounds(215, 40, 200, 25);
            this.Top.add(this.lblOver);
            this.lblMod.setBounds(215, 15, 200, 25);
            this.Top.add(this.lblMod);
            this.lblNew.setBounds(8, 40, 200, 25);
            this.Top.add(this.lblNew);
            this.lblOver.setBounds(8, 15, 200, 25);
            this.Top.add(this.lblReintro);
        }
        
        public BottomPanel() {
            this.Top = null;
            this.lblOver = null;
            this.lblMod = null;
            this.lblNew = null;
            this.lblReintro = null;
            this.init();
        }
    }
}
