package com.adventnet.client.components.table.template;

import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.nodes.TextNode;
import com.adventnet.client.components.table.web.TableDatasetModel;
import com.adventnet.client.components.table.web.TableViewModel;
import org.htmlparser.Tag;
import com.adventnet.client.view.web.ViewContext;
import org.htmlparser.visitors.NodeVisitor;

public class SumSettingVisitor extends NodeVisitor
{
    ViewContext vc;
    
    private SumSettingVisitor() {
    }
    
    public SumSettingVisitor(final ViewContext vc) {
        this.vc = vc;
    }
    
    public void visitTag(final Tag tag) {
        if (!(this.vc.getViewModel() instanceof TableViewModel)) {
            return;
        }
        final TableViewModel viewmodel = (TableViewModel)this.vc.getViewModel();
        if (!(viewmodel.getTableModel() instanceof TableDatasetModel)) {
            return;
        }
        final TableDatasetModel tdsm = (TableDatasetModel)viewmodel.getTableModel();
        if (tag.getAttribute("mc:Tsum") != null) {
            final String sumcol = tag.getAttribute("mc:Tsum");
            if (tdsm.getTotalSumMap().get(sumcol) == null) {
                return;
            }
            final String val = tdsm.getTotalSumMap().get(sumcol).toString();
            tag.setChildren(new NodeList((Node)new TextNode(val)));
        }
        if (tag.getAttribute("mc:Vsum") != null) {
            final String sumcol = tag.getAttribute("mc:Vsum");
            if (tdsm.getViewSumMap().get(sumcol) == null) {
                return;
            }
            final String val = tdsm.getViewSumMap().get(sumcol).toString();
            tag.setChildren(new NodeList((Node)new TextNode(val)));
        }
    }
}
