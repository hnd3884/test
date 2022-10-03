package com.adventnet.client.components.table.template;

import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

public class SearchButtonClassSettingVisitor extends NodeVisitor
{
    boolean isSearchPresent;
    
    public SearchButtonClassSettingVisitor(final boolean isSearchPresent) {
        this.isSearchPresent = isSearchPresent;
    }
    
    public void visitTag(final Tag tag) {
        if (this.isSearchPresent && tag.getAttribute("table_el") != null && tag.getAttribute("table_el").equals("OSBTN")) {
            String classname = "hide";
            if (tag.getAttribute("mc:onSearchClass") != null) {
                classname = tag.getAttribute("mc:onSearchClass");
            }
            tag.setAttribute("class", classname);
        }
        if (this.isSearchPresent && tag.getAttribute("table_el") != null && tag.getAttribute("table_el").equals("CSBTN")) {
            String classname = "tableSearchCloseButton";
            if (tag.getAttribute("mc:onSearchClass") != null) {
                classname = tag.getAttribute("mc:onSearchClass");
            }
            tag.setAttribute("class", classname);
        }
    }
}
