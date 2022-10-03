package com.adventnet.model.tree;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import com.adventnet.tree.HierarchyNode;

public class TreeModelData
{
    public static String getString(final HierarchyNode rootNode) {
        if (rootNode == null) {
            return "null-null";
        }
        StringBuffer buf = new StringBuffer();
        final HierarchyNode dummy = null;
        buf = getString(buf, rootNode, dummy);
        return buf.toString();
    }
    
    private static StringBuffer getString(StringBuffer buf, final HierarchyNode hierarchyNode, final HierarchyNode previous) {
        final int level = hierarchyNode.getLevel();
        if (level == 0) {
            buf.append(hierarchyNode.toString()).append("\n");
        }
        else {
            if (previous != null && previous.getLevel() == level && !previous.isLeaf()) {
                append(buf, level, hierarchyNode, false);
            }
            append(buf, level, hierarchyNode, true);
        }
        HierarchyNode previousNode = null;
        final Enumeration en = hierarchyNode.children();
        while (en.hasMoreElements()) {
            final HierarchyNode childNode = en.nextElement();
            buf = getString(buf, childNode, previousNode);
            previousNode = childNode;
        }
        return buf;
    }
    
    private static void append(final StringBuffer buf, final int level, final HierarchyNode rNode, final boolean flag) {
        for (int i = 0; i < level - 1; ++i) {
            HierarchyNode ref = rNode;
            HierarchyNode refp = rNode;
            for (int j = 0; j < level - i; ++j) {
                refp = ref;
                ref = (HierarchyNode)ref.getParent();
            }
            if (getChildAfter(ref, refp) != null) {
                buf.append("|  ");
            }
            else {
                buf.append("   ");
            }
        }
        if (flag) {
            buf.append("|___").append(rNode.toString()).append("\n");
        }
        else {
            buf.append("|").append("\n");
        }
    }
    
    private static HierarchyNode getChildAfter(final HierarchyNode parentNode, final HierarchyNode childNode) {
        if (childNode == null) {
            throw new IllegalArgumentException("argument is null");
        }
        final int index = parentNode.getIndex(childNode);
        if (index == -1) {
            throw new IllegalArgumentException("node is not a child");
        }
        if (index < parentNode.getChildCount() - 1) {
            return (HierarchyNode)parentNode.getChildAt(index + 1);
        }
        return null;
    }
}
