package com.adventnet.client.components.table.template;

import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

public class ClassSettingRowVisitor extends NodeVisitor
{
    boolean isEvenRow;
    int position;
    
    public ClassSettingRowVisitor(final int position) {
        this.position = position;
        this.isEvenRow = (position % 2 == 0);
    }
    
    public void visitTag(final Tag tag) {
        String evenclass = "evenrow";
        String oddclass = "oddrow";
        final Tag parent = (Tag)tag.getParent();
        if (tag.getAttribute("mc:row") != null && tag.getAttribute("mc:row").equals("reference")) {
            tag.setAttribute("rowidx", "" + (this.position - 1));
        }
        if (tag.getAttribute("name") != null && tag.getAttribute("name").equals("rowSelection")) {
            tag.setAttribute("value", "" + (this.position - 1));
        }
        if (parent != null) {
            try {
                if (parent.getAttribute("mc:row") != null && parent.getAttribute("mc:row").equals("reference")) {
                    String temp = "";
                    if (tag.getAttribute("mc:evenClass") != null) {
                        evenclass = tag.getAttribute("mc:evenClass");
                    }
                    if (tag.getAttribute("mc:oddClass") != null) {
                        oddclass = tag.getAttribute("mc:oddClass");
                    }
                    temp = "";
                    if (tag.getAttribute("class") != null) {
                        temp = " " + tag.getAttribute("class");
                    }
                    if (this.isEvenRow) {
                        tag.setAttribute("class", evenclass + temp);
                    }
                    else {
                        tag.setAttribute("class", oddclass + temp);
                    }
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
