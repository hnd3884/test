package com.adventnet.client.components.table.template;

import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.nodes.TextNode;
import com.adventnet.i18n.I18N;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.HasAttributeFilter;
import com.adventnet.iam.xss.IAMEncoder;
import java.util.logging.Level;
import com.adventnet.client.components.filter.web.FilterModel;
import org.htmlparser.Tag;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import org.htmlparser.visitors.NodeVisitor;

public class CheckAndPopulateFilterComboVisitor extends NodeVisitor
{
    static final Logger OUT;
    private ViewContext vc;
    
    public CheckAndPopulateFilterComboVisitor(final ViewContext vcarg) {
        this.vc = vcarg;
    }
    
    public void visitTag(final Tag tag) {
        if (tag.getAttribute("mc:row") != null && tag.getAttribute("mc:row").equals("filter")) {
            final boolean isEditable = (boolean)this.vc.getRequest().getAttribute("ISEDIT");
            final FilterModel fm = (FilterModel)this.vc.getTransientState("FILTERMODEL");
            final String controllerViewName = (String)this.vc.getRequest().getAttribute("controllerViewName");
            boolean edit = false;
            boolean delete = false;
            try {
                delete = fm.isDeleteable();
                edit = fm.isEditable();
            }
            catch (final Exception e) {
                CheckAndPopulateFilterComboVisitor.OUT.log(Level.INFO, "Enable Filter in configuration , Or remove filter row from template", e);
            }
            final String uniqId = this.vc.getUniqueId();
            final Long listId = fm.getListId();
            final String selFilter = fm.getSelectedFilter();
            String tagContent = tag.getChildren().toHtml();
            String script = "<script> var uniqId='" + IAMEncoder.encodeJavaScript(uniqId) + "'; var listId='" + listId + "'; var selFilter='" + IAMEncoder.encodeJavaScript(selFilter) + "'; controllerViewName='" + IAMEncoder.encodeJavaScript(controllerViewName) + "';";
            if (edit) {
                script += "document.getElementById('edit').disabled=false;";
            }
            if (delete) {
                script += "document.getElementById('delete').disabled=false;";
            }
            script += "</script>";
            tagContent = script + tagContent;
            if (isEditable) {
                tag.setChildren(FillTable.getNodeList(tagContent));
            }
            else {
                final HasAttributeFilter comboFilter = new HasAttributeFilter();
                comboFilter.setAttributeName("mc:column");
                comboFilter.setAttributeValue("FilterComboBox");
                final NodeList combo = FillTable.getNodeList(tagContent).extractAllNodesThatMatch((NodeFilter)comboFilter, true);
                tag.setChildren(combo);
            }
        }
        if (tag.getAttribute("mc:column") != null && tag.getAttribute("mc:column").equals("FilterComboBox")) {
            FilterModel fm2 = (FilterModel)this.vc.getTransientState("FILTERMODEL");
            if (this.vc.getTransientState("FILTERMODEL") != null) {
                try {
                    String child = "";
                    child = child + "<select name=\"selectedfilter\" onChange=\"selectFilter('" + IAMEncoder.encodeJavaScript(this.vc.getUniqueId()) + "',this.value);\">";
                    while (fm2.next()) {
                        if (fm2.isNewGroup()) {
                            final String groupTitle = fm2.getGroupTitle();
                            if (groupTitle != null) {
                                child = child + "<optgroup label=\"" + IAMEncoder.encodeHTMLAttribute(I18N.getMsg(groupTitle, new Object[0])) + "\"  class=\"select\" style=\"border:none\">";
                            }
                        }
                        child = child + "<option value=\"" + IAMEncoder.encodeHTMLAttribute(I18N.getMsg(fm2.getFilterName(), new Object[0])) + "\" " + (fm2.isSelected() ? "selected" : "") + ">" + IAMEncoder.encodeHTML(I18N.getMsg(fm2.getFilterTitle(), new Object[0])) + "</option>";
                    }
                    final Object tmp_fm = this.vc.getTransientState("EXTFILTERMODEL");
                    boolean evaextfilter = false;
                    if (tmp_fm != null) {
                        fm2 = (FilterModel)tmp_fm;
                        evaextfilter = true;
                    }
                    if (fm2 != null && evaextfilter) {
                        while (fm2.next()) {
                            if (fm2.isNewGroup()) {
                                final String groupTitle2 = fm2.getGroupTitle();
                                if (groupTitle2 != null) {
                                    child = child + "<optgroup label=\"" + IAMEncoder.encodeHTMLAttribute(I18N.getMsg(groupTitle2, new Object[0])) + "\"  class=\"select\" style=\"border:none\">";
                                }
                            }
                            child = child + "<option value=\"" + IAMEncoder.encodeHTMLAttribute(I18N.getMsg(fm2.getFilterName(), new Object[0])) + "\" " + (fm2.isSelected() ? "selected" : "") + ">" + IAMEncoder.encodeHTMLAttribute(I18N.getMsg(fm2.getFilterTitle(), new Object[0])) + "</option>";
                        }
                    }
                    child += "</select>";
                    tag.setChildren(new NodeList((Node)new TextNode(child)));
                }
                catch (final Exception e2) {
                    CheckAndPopulateFilterComboVisitor.OUT.log(Level.INFO, "Error while generating Filter combo box", e2);
                    e2.printStackTrace();
                }
            }
        }
    }
    
    static {
        OUT = Logger.getLogger(CheckAndPopulateFilterComboVisitor.class.getName());
    }
}
