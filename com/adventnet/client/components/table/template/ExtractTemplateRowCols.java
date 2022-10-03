package com.adventnet.client.components.table.template;

import org.htmlparser.Tag;
import java.util.HashMap;
import java.util.Map;
import org.htmlparser.visitors.NodeVisitor;

public class ExtractTemplateRowCols extends NodeVisitor
{
    int i;
    int j;
    int count;
    String viewName;
    int noofpre;
    int noofcols;
    int noofpost;
    Map columnOrderInTemplate;
    Map preColumnsInTemplate;
    Map templateHtmlRowCols;
    
    public ExtractTemplateRowCols(final String viewName, final int noOfColumnsInTemplate, final int noOfPreColumnsInTemplate, final int noOfPostColumnsInTemplate, final Map columnOrderInTemplate) {
        this.j = 1;
        this.count = 1;
        this.noofpre = 0;
        this.noofcols = 0;
        this.noofpost = 0;
        this.columnOrderInTemplate = null;
        this.preColumnsInTemplate = new HashMap();
        this.templateHtmlRowCols = new HashMap();
        this.viewName = viewName;
        this.j = 1;
        this.i = 1;
        this.count = 1;
        this.noofcols = noOfColumnsInTemplate;
        this.noofpre = noOfPreColumnsInTemplate;
        this.noofpost = noOfPostColumnsInTemplate;
        this.columnOrderInTemplate = columnOrderInTemplate;
    }
    
    public void visitTag(final Tag tag) {
        final Tag parent = (Tag)tag.getParent();
        if (parent != null) {
            final String attributeString = parent.toHtml().substring(0, parent.toHtml().indexOf(">"));
            final int ind = attributeString.indexOf("mc:row=\"reference\"");
            if (ind != -1) {
                if (this.count <= this.noofpre) {
                    this.preColumnsInTemplate.put(this.i, tag.toHtml());
                    ++this.i;
                }
                else if (this.count > this.noofpre && this.count <= this.noofcols) {
                    tag.setAttribute("mc:temp", "" + this.j);
                    final String ColumnName = this.columnOrderInTemplate.get(this.viewName + "_" + this.j);
                    this.templateHtmlRowCols.put(ColumnName, tag.toHtml());
                    ++this.j;
                }
                ++this.count;
            }
        }
    }
    
    public Map getPreColumnsInTemplate() {
        return this.preColumnsInTemplate;
    }
    
    public Map getTemplateHtmlRowCols() {
        return this.templateHtmlRowCols;
    }
}
