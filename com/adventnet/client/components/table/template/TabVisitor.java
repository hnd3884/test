package com.adventnet.client.components.table.template;

import com.adventnet.client.util.web.WebClientUtil;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.nodes.TextNode;
import com.adventnet.i18n.I18N;
import org.htmlparser.Tag;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.tab.web.TabModel;
import org.htmlparser.visitors.NodeVisitor;

public class TabVisitor extends NodeVisitor
{
    private TabModel model;
    private TabModel.TabIterator ite;
    private ViewContext vc;
    
    public TabVisitor(final ViewContext viewContext) {
        this.model = (TabModel)viewContext.getViewModel();
        this.ite = this.model.getIterator();
        this.vc = viewContext;
    }
    
    public void visitTag(final Tag tag) {
        if (tag.getAttribute("mc:tab") != null) {
            final int tabCount = Integer.parseInt(tag.getAttribute("mc:tab"));
            this.ite.next();
            final String cref = this.ite.getCurrentRefId();
            tag.setAttribute("cref", cref);
            tag.setAttribute("viewname", this.vc.getUniqueId());
        }
        if (tag.getAttribute("mc:value") != null && tag.getAttribute("mc:value").equals("true")) {
            try {
                final String tabHeader = I18N.getMsg(this.ite.getTitle(), new Object[0]);
                tag.setChildren(new NodeList((Node)new TextNode(tabHeader)));
            }
            catch (final Exception e) {
                System.out.println("Error while generating tab header");
            }
        }
        try {
            if (tag.getAttribute("href") != null) {
                final String link = tag.getAttribute("href");
                if (link.equals("javascript:tabSelected()")) {
                    tag.setAttribute("href", this.ite.getTabAction());
                }
            }
            if (tag.getAttribute("onclick") != null || tag.getAttribute("onClick") != null) {
                String link = tag.getAttribute("onclick");
                String javascript;
                if (WebClientUtil.isRestful(this.vc.getRequest())) {
                    javascript = "javascript:location.href=\"" + this.ite.getTabAction() + "\";";
                }
                else {
                    javascript = this.ite.getTabAction();
                }
                if (link == null) {
                    link = tag.getAttribute("onClick");
                }
                if (link.equals("javascript:tabSelected()")) {
                    tag.setAttribute("onClick", javascript);
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
