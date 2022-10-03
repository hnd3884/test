package org.htmlparser.parserapplications.filterbuilder;

import org.htmlparser.Node;
import java.awt.Component;
import javax.swing.JTree;
import org.htmlparser.nodes.TextNode;
import java.util.Vector;
import org.htmlparser.util.Translate;
import org.htmlparser.Attribute;
import org.htmlparser.nodes.TagNode;
import javax.swing.Icon;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

public class HtmlTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer
{
    public HtmlTreeCellRenderer() {
        this.setLeafIcon(null);
        this.setClosedIcon(null);
        this.setOpenIcon(null);
    }
    
    public String toHtml(final TagNode tag) {
        int length = 2;
        final Vector attributes = tag.getAttributesEx();
        final int size = attributes.size();
        for (int i = 0; i < size; ++i) {
            final Attribute attribute = attributes.elementAt(i);
            length += attribute.getLength();
        }
        StringBuffer ret = new StringBuffer(length);
        ret.append("<");
        for (int i = 0; i < size; ++i) {
            final Attribute attribute = attributes.elementAt(i);
            attribute.toString(ret);
        }
        ret.append(">");
        final String s = Translate.encode(ret.toString());
        final boolean children = null != tag.getChildren();
        ret = new StringBuffer(s.length() + 13 + (children ? 16 : 0));
        ret.append("<html>");
        if (children) {
            ret.append("<font color=\"blue\">");
        }
        ret.append(s);
        if (children) {
            ret.append("</font>");
        }
        ret.append("</html>");
        return ret.toString();
    }
    
    public String toText(final TextNode node) {
        final int startpos = node.getStartPosition();
        final int endpos = node.getEndPosition();
        final StringBuffer ret = new StringBuffer(endpos - startpos + 20);
        final String s = node.toHtml();
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            switch (c) {
                case '\t': {
                    ret.append("\\t");
                    break;
                }
                case '\n': {
                    ret.append("\\n");
                    break;
                }
                case '\r': {
                    ret.append("\\r");
                    break;
                }
                default: {
                    ret.append(c);
                    break;
                }
            }
            if (77 <= ret.length()) {
                ret.append("...");
                break;
            }
        }
        return ret.toString();
    }
    
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        final Node node = (Node)value;
        if (node instanceof TagNode) {
            this.setText(this.toHtml((TagNode)node));
        }
        else if (node instanceof TextNode) {
            this.setText(this.toText((TextNode)node));
        }
        else {
            this.setText(node.toHtml());
        }
        return this;
    }
}
