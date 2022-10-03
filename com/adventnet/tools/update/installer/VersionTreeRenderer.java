package com.adventnet.tools.update.installer;

import java.awt.Color;
import javax.swing.tree.TreeNode;
import javax.swing.Icon;
import java.applet.Applet;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.tree.DefaultTreeCellRenderer;

public class VersionTreeRenderer extends DefaultTreeCellRenderer
{
    private JLabel theLabel;
    
    public VersionTreeRenderer() {
        (this.theLabel = new JLabel()).setOpaque(true);
    }
    
    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        final Object userObject = node.getUserObject();
        if (userObject instanceof String) {
            final String treeName = (String)userObject;
            this.theLabel.setText(treeName);
            if (node.isRoot()) {
                this.theLabel.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/service_pack.png", null, true));
            }
            else if (node.getRoot().getIndex(node) == 0) {
                this.theLabel.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/trash2.png", null, true));
            }
            else {
                this.theLabel.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/trash1.png", null, true));
            }
        }
        if (selected) {
            this.theLabel.setBackground(Color.blue);
            this.theLabel.setForeground(Color.white);
        }
        else {
            this.theLabel.setBackground(tree.getBackground());
            this.theLabel.setForeground(tree.getForeground());
        }
        return this.theLabel;
    }
}
