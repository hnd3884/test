package com.adventnet.client.components.table.template;

import org.htmlparser.Tag;
import java.util.HashMap;
import java.util.Map;
import org.htmlparser.visitors.NodeVisitor;

public class ExtractTemplateSearchRowCols extends NodeVisitor
{
    int j;
    int i;
    int k;
    int count;
    String viewName;
    boolean isSearchPresent;
    int noofpre;
    int noofcols;
    int noofpost;
    Map columnOrderInTemplate;
    Map templateHtmlSearchRowCols;
    Map preColumnsInTemplate;
    Map postColumnsInTemplate;
    
    public ExtractTemplateSearchRowCols(final String viewName, final boolean isSearchPresent, final int noOfColumnsInTemplate, final int noOfPreColumnsInTemplate, final int noOfPostColumnsInTemplate, final Map columnOrderInTemplate) {
        this.j = 1;
        this.i = 1;
        this.k = 1;
        this.count = 1;
        this.noofpre = 0;
        this.noofcols = 0;
        this.noofpost = 0;
        this.columnOrderInTemplate = null;
        this.templateHtmlSearchRowCols = new HashMap();
        this.preColumnsInTemplate = new HashMap();
        this.postColumnsInTemplate = new HashMap();
        this.viewName = viewName;
        this.isSearchPresent = isSearchPresent;
        this.j = 1;
        this.k = 1;
        this.i = 1;
        this.count = 1;
        this.noofcols = noOfColumnsInTemplate;
        this.noofpre = noOfPreColumnsInTemplate;
        this.noofpost = noOfPostColumnsInTemplate;
        this.columnOrderInTemplate = columnOrderInTemplate;
    }
    
    public void visitTag(final Tag tag) {
        final Tag parent = (Tag)tag.getParent();
        if (tag.getAttribute("mc:row") != null && tag.getAttribute("mc:row").equals("searchRow") && this.isSearchPresent) {
            String onsearchclass = "searchRow";
            if (tag.getAttribute("mc:onSearchClass") != null) {
                onsearchclass = tag.getAttribute("mc:onSearchClass");
            }
            tag.setAttribute("class", onsearchclass);
        }
        if (parent != null) {
            final String attributeString = parent.toHtml().substring(0, parent.toHtml().indexOf(">"));
            final int ind = attributeString.indexOf("mc:row=\"searchRow\"");
            if (ind != -1) {
                if (this.count <= this.noofpre) {
                    this.preColumnsInTemplate.put(this.k, tag.toHtml());
                    ++this.k;
                }
                else if (this.count > this.noofpre && this.count <= this.noofcols - this.noofpost) {
                    final String columnName = this.columnOrderInTemplate.get(this.viewName + "_" + this.j);
                    String temp = tag.toHtml();
                    if (temp.indexOf("<input") != -1) {
                        final int startIndex = temp.indexOf("<input");
                        final String temp2 = temp.substring(startIndex, temp.length());
                        if (temp2.indexOf("type=\"text\"") != -1) {
                            final int startindex = temp.indexOf("type=\"text\"");
                            temp = temp.substring(0, startindex - 1) + " name=\"" + columnName + "\" " + temp.substring(startindex, temp.length());
                        }
                    }
                    this.templateHtmlSearchRowCols.put(columnName, temp);
                    tag.setAttribute("mc:tempSearchRow", "1");
                    ++this.j;
                }
                else {
                    this.postColumnsInTemplate.put(this.i, tag.toHtml());
                    ++this.i;
                }
                ++this.count;
            }
        }
    }
    
    public Map getTemplateHtmlSearchRowCols() {
        return this.templateHtmlSearchRowCols;
    }
    
    public Map getPreColumnsInTemplate() {
        return this.preColumnsInTemplate;
    }
    
    public Map getPostColumnsInTemplate() {
        return this.postColumnsInTemplate;
    }
}
