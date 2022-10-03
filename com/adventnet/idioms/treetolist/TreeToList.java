package com.adventnet.idioms.treetolist;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.ListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public class TreeToList extends JPanel implements ActionListener, TreeSelectionListener, ListSelectionListener
{
    private JButton addButton;
    private JButton removeButton;
    private GridBagConstraints cons;
    private JScrollPane treeScrollPane;
    private JScrollPane listScrollPane;
    private JPanel treePanel;
    private JPanel listPanel;
    private JPanel centerPanel;
    private JTree tree;
    private JList list;
    private TreeToListModel ttlModel;
    private boolean multiSelectionAllowed;
    private JLabel treeLabel;
    private JLabel listLabel;
    
    public TreeToList() {
        this(new JTree());
    }
    
    public TreeToList(final JTree tree) {
        this.addButton = new JButton("Right");
        this.removeButton = new JButton("Left");
        this.cons = null;
        this.treeScrollPane = new JScrollPane();
        this.listScrollPane = new JScrollPane();
        this.treePanel = new JPanel();
        this.listPanel = new JPanel();
        this.centerPanel = new JPanel();
        this.tree = null;
        this.list = null;
        this.ttlModel = null;
        this.multiSelectionAllowed = true;
        this.treeLabel = null;
        this.listLabel = null;
        this.list = new JList();
        this.setTree(tree);
        this.addListeners();
    }
    
    private void addListeners() {
        this.list.addListSelectionListener(this);
        this.addButton.addActionListener(this);
        this.removeButton.addActionListener(this);
    }
    
    private void initialize() {
        this.addButton.setActionCommand("Add");
        this.addButton.setName("TreeToList_Add");
        this.removeButton.setActionCommand("Remove");
        this.removeButton.setName("TreeToList_Remove");
        this.cons = new GridBagConstraints();
        this.removeButton.setEnabled(false);
        this.addButton.setEnabled(false);
        this.tree.addTreeSelectionListener(this);
        this.list.setModel(this.getModel().getListModel());
        this.treePanel.setLayout(new BorderLayout());
        this.listPanel.setLayout(new BorderLayout());
        this.treeLabel = new JLabel("TreeText");
        this.listLabel = new JLabel("ListText");
        this.treePanel.add(this.treeScrollPane, "Center");
        this.treePanel.add(this.treeLabel, "North");
        this.listPanel.add(this.listScrollPane, "Center");
        this.listPanel.add(this.listLabel, "North");
        this.treePanel.setMinimumSize(new Dimension(200, 200));
        this.listPanel.setMinimumSize(new Dimension(200, 200));
        this.centerPanel.setLayout(new GridBagLayout());
        final Insets insets = new Insets(0, 0, 0, 0);
        final int n = 0;
        final int n2 = 0;
        final int n3 = 1;
        final int n4 = 2;
        final double n5 = 0.5;
        final double n6 = 0.2;
        final GridBagConstraints cons = this.cons;
        final int n7 = 11;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(n, n2, n3, n4, n5, n6, n7, 1, insets, 0, 0);
        this.centerPanel.add(this.treePanel, this.cons);
        this.treeScrollPane.getViewport().add(this.tree);
        final Insets insets2 = new Insets(0, 0, 0, 0);
        final int n8 = 2;
        final int n9 = 0;
        final int n10 = 1;
        final int n11 = 2;
        final double n12 = 0.5;
        final double n13 = 0.2;
        final GridBagConstraints cons3 = this.cons;
        final int n14 = 11;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(n8, n9, n10, n11, n12, n13, n14, 1, insets2, 0, 0);
        this.centerPanel.add(this.listPanel, this.cons);
        this.listScrollPane.getViewport().add(this.list);
        final Insets insets3 = new Insets(0, 2, 4, 4);
        final int n15 = 1;
        final int n16 = 0;
        final int n17 = 1;
        final int n18 = 1;
        final double n19 = 0.0;
        final double n20 = 0.5;
        final GridBagConstraints cons5 = this.cons;
        final int n21 = 15;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(n15, n16, n17, n18, n19, n20, n21, 0, insets3, 0, 0);
        this.centerPanel.add(this.addButton, this.cons);
        final Insets insets4 = new Insets(2, 0, 4, 4);
        final int n22 = 1;
        final int n23 = 1;
        final int n24 = 1;
        final int n25 = 1;
        final double n26 = 0.0;
        final double n27 = 0.5;
        final GridBagConstraints cons7 = this.cons;
        final int n28 = 11;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(n22, n23, n24, n25, n26, n27, n28, 0, insets4, 0, 0);
        this.centerPanel.add(this.removeButton, this.cons);
        this.setLayout(new BorderLayout());
        this.add(this.centerPanel, "Center");
    }
    
    private void setConstraints(final int gridx, final int gridy, final int gridwidth, final int gridheight, final double weightx, final double weighty, final int anchor, final int fill, final Insets insets, final int ipadx, final int ipady) {
        this.cons.gridx = gridx;
        this.cons.gridy = gridy;
        this.cons.gridwidth = gridwidth;
        this.cons.gridheight = gridheight;
        this.cons.weightx = weightx;
        this.cons.weighty = weighty;
        this.cons.anchor = anchor;
        this.cons.fill = fill;
        this.cons.insets = insets;
        this.cons.ipadx = ipadx;
        this.cons.ipady = ipady;
    }
    
    public JList getList() {
        return this.list;
    }
    
    public void setTree(final JTree tree) {
        this.tree = tree;
        this.initialize();
        this.repaint();
    }
    
    public JTree getTree() {
        return this.tree;
    }
    
    public void setModel(final TreeToListModel ttlModel) {
        this.ttlModel = ttlModel;
        this.list.setModel(this.ttlModel.getListModel());
    }
    
    public TreeToListModel getModel() {
        if (this.ttlModel == null) {
            this.ttlModel = new DefaultTreeToListModel();
        }
        return this.ttlModel;
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.addButton) {
            if (this.multiSelectionAllowed) {
                final TreePath[] selectionPaths = this.tree.getSelectionPaths();
                if (selectionPaths != null) {
                    for (int i = 0; i < selectionPaths.length; ++i) {
                        this.ttlModel.addOption((TreeNode)selectionPaths[i].getLastPathComponent());
                    }
                }
                this.list.setSelectedIndex(this.ttlModel.getListModel().getSize() - 1);
                this.addButton.setEnabled(false);
                this.removeButton.setEnabled(true);
            }
            else {
                this.ttlModel.addOption((TreeNode)this.tree.getLastSelectedPathComponent());
                this.list.setSelectedIndex(this.ttlModel.getListModel().getSize() - 1);
                this.addButton.setEnabled(false);
                this.removeButton.setEnabled(true);
            }
        }
        else if (actionEvent.getSource() == this.removeButton) {
            final Object[] selectedValues = this.list.getSelectedValues();
            if (selectedValues.length == 1 && this.ttlModel.getListModel().indexOf(selectedValues[0]) != 0) {}
            if (selectedValues != null) {
                for (int j = 0; j < selectedValues.length; ++j) {
                    this.ttlModel.removeOption(selectedValues[j]);
                }
                this.addButton.setEnabled(true);
                this.removeButton.setEnabled(false);
            }
        }
    }
    
    public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
        if (!this.list.getSelectionModel().isSelectionEmpty()) {
            this.list.getSelectionModel().clearSelection();
        }
        final TreePath[] paths = treeSelectionEvent.getPaths();
        for (int i = 0; i < paths.length; ++i) {
            if (paths[i] == null) {
                this.addButton.setEnabled(false);
                return;
            }
            final TreeNode treeNode = (TreeNode)paths[i].getLastPathComponent();
            if (this.ttlModel.isNodeAddable(treeNode)) {
                if (!this.isNodeAdded(treeNode)) {
                    this.addButton.setEnabled(true);
                    this.removeButton.setEnabled(false);
                    return;
                }
                this.list.removeListSelectionListener(this);
                this.list.setSelectedValue(this.ttlModel.getObjectToAdd(treeNode), true);
                this.list.addListSelectionListener(this);
                this.addButton.setEnabled(false);
                this.removeButton.setEnabled(true);
            }
            else {
                this.addButton.setEnabled(false);
                this.removeButton.setEnabled(true);
            }
        }
    }
    
    public void valueChanged(final ListSelectionEvent listSelectionEvent) {
        this.addButton.setEnabled(false);
        this.tree.removeTreeSelectionListener(this);
        if (this.list == null) {
            return;
        }
        if (this.list.getSelectedValue() instanceof TreeNode) {
            final TreeNode treeNode = this.list.getSelectedValue();
            if (treeNode != null) {
                final TreePath selectionPath = new TreePath(this.getPathToRoot(treeNode, 0));
                this.tree.setSelectionPath(selectionPath);
                this.tree.scrollPathToVisible(selectionPath);
                this.removeButton.setEnabled(true);
                this.addButton.setEnabled(false);
            }
            else {
                this.removeButton.setEnabled(false);
                this.addButton.setEnabled(true);
            }
        }
        else {
            this.addButton.setEnabled(false);
            this.removeButton.setEnabled(true);
        }
        this.tree.addTreeSelectionListener(this);
    }
    
    public boolean isNodeAdded(final TreeNode treeNode) {
        return this.ttlModel.getListModel().contains(this.ttlModel.getObjectToAdd(treeNode));
    }
    
    private TreeNode[] getPathToRoot(final TreeNode treeNode, int n) {
        TreeNode[] pathToRoot;
        if (treeNode == null) {
            if (n == 0) {
                return null;
            }
            pathToRoot = new TreeNode[n];
        }
        else {
            ++n;
            pathToRoot = this.getPathToRoot(treeNode.getParent(), n);
            pathToRoot[pathToRoot.length - n] = treeNode;
        }
        return pathToRoot;
    }
    
    public void setTreeLabelText(final String text) {
        this.treeLabel.setText(text);
    }
    
    public String getTreeLabelText() {
        return this.treeLabel.getText();
    }
    
    public void setListLabelText(final String text) {
        this.listLabel.setText(text);
    }
    
    public String getListLabelText() {
        return this.listLabel.getText();
    }
    
    public static void main(final String[] array) {
        final JFrame frame = new JFrame();
        final JTree tree = new JTree();
        final TreeToList list = new TreeToList();
        list.setTree(tree);
        final DefaultTreeToListModel model = new DefaultTreeToListModel();
        model.setListModel(new DefaultListModel());
        list.setModel(model);
        frame.getContentPane().add(list);
        final JButton button = new JButton("Click");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
            }
        });
        frame.getContentPane().add(button, "South");
        frame.setSize(400, 400);
        frame.setVisible(true);
    }
}
