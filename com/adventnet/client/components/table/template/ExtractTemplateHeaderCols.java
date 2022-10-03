package com.adventnet.client.components.table.template;

import java.util.Map;
import org.htmlparser.Tag;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import org.htmlparser.visitors.NodeVisitor;

public class ExtractTemplateHeaderCols extends NodeVisitor
{
    int i;
    int preCols;
    int postCols;
    int noOfColumnsInTemplate;
    int noOfPreColumnsInTemplate;
    int noOfPostColumnsInTemplate;
    String columnChooserType;
    String columnChooser;
    ViewContext vc;
    String viewName;
    HashMap templateHtmlHeaderCols;
    HashMap columnOrderInTemplate;
    HashMap preColumnsInTemplate;
    HashMap postColumnsInTemplate;
    
    public ExtractTemplateHeaderCols(final ViewContext vc, final String defaultColumnChooserType) {
        this.i = 1;
        this.preCols = 1;
        this.postCols = 1;
        this.noOfColumnsInTemplate = 0;
        this.noOfPreColumnsInTemplate = 0;
        this.noOfPostColumnsInTemplate = 0;
        this.columnChooserType = "";
        this.columnChooser = "";
        this.vc = null;
        this.viewName = null;
        this.templateHtmlHeaderCols = new HashMap();
        this.columnOrderInTemplate = new HashMap();
        this.preColumnsInTemplate = new HashMap();
        this.postColumnsInTemplate = new HashMap();
        this.vc = vc;
        this.viewName = vc.getUniqueId();
        this.columnChooserType = defaultColumnChooserType;
    }
    
    public void visitTag(final Tag tag) {
        if (tag.getAttribute("mc:columnChooserType") != null) {
            final String chooserType = tag.getAttribute("mc:columnChooserType");
            if (!chooserType.equals("DefaultColumnChooserType")) {
                this.columnChooserType = tag.getAttribute("mc:columnChooserType");
            }
            else {
                tag.setAttribute("mc:ColumnChooserType", this.columnChooserType);
            }
            this.columnChooser = tag.toHtml();
        }
        if (tag.getAttribute("mc:row") != null && tag.getAttribute("mc:row").equals("header")) {
            final Tag parent = (Tag)tag.getParent();
            parent.setAttribute("id", this.viewName + "_TABLE");
        }
        final String attrname = tag.getAttribute("mc:columnName");
        final String headerRow = tag.getAttribute("mc:row");
        if (headerRow != null && (headerRow.equals("header") || headerRow.equals("searchRow") || headerRow.equals("reference"))) {
            tag.setAttribute("mc:id", this.viewName);
        }
        if (attrname != null) {
            if (attrname.indexOf("mc:") == -1) {
                ++this.noOfColumnsInTemplate;
                final String temp = tag.toHtml();
                this.templateHtmlHeaderCols.put(this.viewName + "_" + attrname, temp);
                this.columnOrderInTemplate.put(this.viewName + "_" + this.i, attrname);
                tag.setAttribute("mc:temp", "\"" + this.i + "\"");
                ++this.i;
            }
            else {
                ++this.noOfColumnsInTemplate;
                if (this.i == 1) {
                    ++this.noOfPreColumnsInTemplate;
                    this.preColumnsInTemplate.put(this.preCols, tag.toHtml());
                    ++this.preCols;
                }
                else {
                    ++this.noOfPostColumnsInTemplate;
                    this.postColumnsInTemplate.put(this.postCols, tag.toHtml());
                    ++this.postCols;
                }
            }
        }
    }
    
    public String getColumnChooser() {
        return this.columnChooser;
    }
    
    public String getColumnChooserType() {
        return this.columnChooserType;
    }
    
    public int getNoOfColumnsInTemplate() {
        return this.noOfColumnsInTemplate;
    }
    
    public int getNoOfPreColumnsInTemplate() {
        return this.noOfPreColumnsInTemplate;
    }
    
    public int getNoOfPostColumnsInTempate() {
        return this.noOfPostColumnsInTemplate;
    }
    
    public Map getTemplateHtmlHeaderCols() {
        return this.templateHtmlHeaderCols;
    }
    
    public Map getColumnOrderInTemplate() {
        return this.columnOrderInTemplate;
    }
    
    public Map getPreColumnsInTemplate() {
        return this.preColumnsInTemplate;
    }
    
    public Map getPostColumnsInTemplate() {
        return this.postColumnsInTemplate;
    }
}
