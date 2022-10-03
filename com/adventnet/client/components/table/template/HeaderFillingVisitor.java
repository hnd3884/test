package com.adventnet.client.components.table.template;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.Tag;
import org.htmlparser.visitors.NodeVisitor;

public class HeaderFillingVisitor extends NodeVisitor
{
    int i;
    String viewName;
    FillTable.ColumnProperties headerInfo;
    String sortColumnName;
    String header;
    String sortButtonClassName;
    String menuString;
    
    public HeaderFillingVisitor(final String viewName, final FillTable.ColumnProperties headerInfo, final String sortColumnName, final String header, final String sortButtonClassName, final String menuString) {
        this.i = 0;
        this.viewName = null;
        this.headerInfo = null;
        this.sortColumnName = null;
        this.header = null;
        this.sortButtonClassName = null;
        this.menuString = null;
        this.viewName = viewName;
        this.headerInfo = headerInfo;
        this.sortColumnName = sortColumnName;
        this.header = header;
        this.sortButtonClassName = sortButtonClassName;
        this.menuString = menuString;
    }
    
    public void visitTag(final Tag tag) {
        final String menuString = tag.getAttribute("mc:column");
        if (menuString != null && menuString.equals("DefaultMenu")) {
            final String menuStr = this.menuString;
            tag.setChildren(new NodeList((Node)new TextNode(menuStr)));
        }
        String temp = tag.getAttribute("mc:value");
        if (temp != null) {
            if (temp.equals("true")) {
                try {
                    final String replaceWith = (this.header != null) ? this.header : "";
                    tag.setChildren(new NodeList((Node)new TextNode(replaceWith)));
                    ++this.i;
                }
                catch (final Exception e) {
                    System.out.println("Error in filling header contents ");
                }
            }
            final Tag tag2 = (Tag)tag.getParent();
        }
        else {
            temp = tag.getAttribute("mc:sortButton");
            if (temp != null && temp.equals("true")) {
                String inputClass = this.sortButtonClassName;
                final String ASCClass = tag.getAttribute("mc:sortAscendingClass");
                final String DSCClass = tag.getAttribute("mc:sortDescendingClass");
                if (inputClass.equals("sortButtonASC") && ASCClass != null) {
                    inputClass = ASCClass;
                }
                if (inputClass.equals("sortButtonDESC") && DSCClass != null) {
                    inputClass = DSCClass;
                }
                tag.removeAttribute("class");
                tag.setAttribute("class", inputClass);
            }
        }
        if (tag.getAttribute("mc:columnName") != null) {
            final String columnname = tag.getAttribute("mc:columnName");
            if (columnname.equalsIgnoreCase(this.sortColumnName)) {
                tag.removeAttribute("class");
                String classname = tag.getAttribute("mc:onSortClass");
                final String headerCss = (this.headerInfo == null) ? null : this.headerInfo.headerCss;
                if (classname == null) {
                    classname = "SortedTableHeader";
                }
                if (headerCss != null && headerCss.trim().length() != 0) {
                    classname = headerCss + " " + classname;
                }
                tag.setAttribute("class", classname);
                final NodeList children = tag.getChildren();
                final HasAttributeFilter filter = new HasAttributeFilter();
                filter.setAttributeName("mc:value");
                filter.setAttributeValue("true");
                final NodeList nl = children.extractAllNodesThatMatch((NodeFilter)filter, true);
                final Tag child = (Tag)nl.elementAt(0);
                child.removeAttribute("class");
                child.setAttribute("class", classname);
            }
            else {
                String classname = tag.getAttribute("class");
                tag.removeAttribute("class");
                final String headerCss = (this.headerInfo == null) ? null : this.headerInfo.headerCss;
                if (classname == null) {
                    classname = "TableHeader";
                }
                if (headerCss != null && headerCss.trim().length() != 0) {
                    classname = headerCss + " " + classname;
                }
                tag.setAttribute("class", classname);
            }
        }
    }
}
