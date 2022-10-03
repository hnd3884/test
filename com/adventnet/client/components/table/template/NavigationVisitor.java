package com.adventnet.client.components.table.template;

import java.util.List;
import com.adventnet.iam.xss.IAMEncoder;
import org.htmlparser.Tag;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import com.adventnet.client.view.web.ViewContext;
import org.htmlparser.visitors.NodeVisitor;

public class NavigationVisitor extends NodeVisitor
{
    private ViewContext vc;
    String viewName;
    NavigationConfig navigationConfig;
    
    public NavigationVisitor(final ViewContext vcarg, final NavigationConfig navigationConfig) {
        this.vc = vcarg;
        this.viewName = this.vc.getUniqueId();
        this.navigationConfig = navigationConfig;
    }
    
    public void visitTag(final Tag tag) {
        final String navigationType = this.navigationConfig.getNavigationType();
        final int navigationpageno = this.navigationConfig.getPageNumber();
        final int navigationpagelength = this.navigationConfig.getPageLength();
        final int startLinkIndex = this.navigationConfig.getStartLinkIndex();
        final List rangeList = this.navigationConfig.getRangeList();
        final long totalRecords = this.navigationConfig.getTotalRecords();
        final boolean isNoCount = NavigationConfig.getNocount(this.vc).equals("true");
        final Tag parent = (Tag)tag.getParent();
        if (parent == null) {
            tag.setAttribute("mc:id", this.viewName);
        }
        if (tag.getAttribute("mc:pageLinks") != null) {
            String tagContent = tag.getChildren().toHtml();
            if (!isNoCount) {
                final String nos = tag.getAttribute("mc:pageLinks");
                for (int no = Integer.parseInt(nos), i = startLinkIndex; i <= startLinkIndex + no - 1; ++i) {
                    final long totalPages = totalRecords;
                    final int pageLength = navigationpagelength;
                    if (navigationpageno == i) {
                        tagContent += "&nbsp;";
                        if (i != 0) {
                            tagContent = tagContent + "<span>[" + i + "" + "]</span>";
                        }
                        else {
                            tagContent += "<span>[1]</span>";
                        }
                    }
                    else if (i != 0 && (i - 1) * pageLength < totalPages) {
                        tagContent += "&nbsp;";
                        tagContent = tagContent + "<a href=\"javascript:showRange('" + IAMEncoder.encodeJavaScript(this.viewName) + "','" + (navigationpagelength * (i - 1) + 1) + "', '" + i + "')\">" + i + "</a>";
                    }
                }
                tag.setChildren(FillTable.getNodeList(tagContent));
            }
            else {
                tag.setChildren(FillTable.getNodeList(""));
            }
        }
        if (tag.getAttribute("mc:viewPerPageLinks") != null) {
            final String nos2 = tag.getAttribute("mc:viewPerPageLinks");
            final int no2 = Integer.parseInt(nos2);
            String tagContent2 = tag.getChildren().toHtml();
            final String navigType = navigationType;
            if (navigationType.equals("SELECT") || navigationType.equals("NOCOUNT")) {
                tag.setAttribute("nowrap", "");
                tag.setAttribute("class", "navigatorRangeColumn");
                tagContent2 = tagContent2 + "<select name=\"pageLength\" onchange=\"return showRangeForLength('" + IAMEncoder.encodeJavaScript(this.viewName) + "', this.value)\">";
            }
            if (rangeList != null) {
                for (int size = rangeList.size(), j = 0; j < ((no2 < size) ? no2 : size); ++j) {
                    int previous = 0;
                    if (j > 0) {
                        previous = rangeList.get(j - 1);
                    }
                    final int length = rangeList.get(j);
                    if (navigationType.equals("NORMAL")) {
                        if (previous != 0 && !isNoCount && previous >= totalRecords) {
                            tagContent2 += "&nbsp;";
                            tagContent2 = tagContent2 + "<font color=\"#000000\">" + length + "</font>";
                        }
                        else if (length == navigationpagelength) {
                            tagContent2 += "&nbsp;";
                            tagContent2 = tagContent2 + "<span>[" + length + "]</span>";
                        }
                        else {
                            tagContent2 += "&nbsp;";
                            tagContent2 = tagContent2 + "<a href=\"javascript:showRangeForLength('" + IAMEncoder.encodeJavaScript(this.viewName) + "', '" + length + "')\">" + length + "</a>";
                        }
                    }
                    else if (navigationType.equals("SELECT") || navigationType.equals("NOCOUNT")) {
                        tag.setAttribute("nowrap", "");
                        tag.setAttribute("class", "navigatorRangeColumn");
                        tagContent2 = tagContent2 + "<option value='" + length + "'";
                        if (length == this.navigationConfig.getPageLength()) {
                            tagContent2 += "selected";
                        }
                        tagContent2 = tagContent2 + ">" + length + "</option>";
                    }
                }
            }
            tag.setChildren(FillTable.getNodeList(tagContent2));
        }
        if (tag.getAttribute("mc:selectPageLength") != null) {
            try {
                final int val = Integer.parseInt(tag.getAttribute("mc:selectPageLength"));
                final Object len = rangeList.get(val - 1);
                final String javascriptcall = "return showRangeForLength('" + IAMEncoder.encodeJavaScript(this.viewName) + "'," + IAMEncoder.encodeJavaScript((String)len) + ")";
                tag.setAttribute("onclick", javascriptcall);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
