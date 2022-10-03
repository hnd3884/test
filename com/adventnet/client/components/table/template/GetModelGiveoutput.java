package com.adventnet.client.components.table.template;

import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;
import com.adventnet.client.components.table.web.RowTag;
import javax.swing.table.TableModel;
import javax.servlet.jsp.PageContext;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.TableViewModel;
import java.util.logging.Logger;

public class GetModelGiveoutput
{
    private static final Logger OUT;
    
    public static String produceFilledHtmlOutput(final TableViewModel viewModel, final ViewContext vc, final PageContext pageContext, final String templateViewName, final boolean returnFilter) throws Exception {
        final String viewname = vc.getUniqueId();
        final TableModel tablemodel = (TableModel)viewModel.getTableModel();
        String templateTableHtml = "";
        String tableScriptAndEnding = "";
        final RowTag rowtag = new RowTag();
        rowtag.setViewContext(vc);
        if (returnFilter) {
            return FillTable.getFilledRowHtml(viewModel, templateViewName, true);
        }
        try {
            templateTableHtml = FillTable.getFilledRowHtml(viewModel, templateViewName, false);
            tableScriptAndEnding = rowtag.generateJS(tablemodel, viewname);
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Parser Error from filltable class");
        }
        return templateTableHtml + tableScriptAndEnding;
    }
    
    public static String getFilledTabHtml(final ViewContext vc) {
        final String templateString = FillTable.tabhtmlmap.get(vc.getUniqueId());
        final NodeList tab = FillTable.getNodeList(templateString);
        final TabVisitor tabVisitor = new TabVisitor(vc);
        try {
            tab.visitAllNodesWith((NodeVisitor)tabVisitor);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return tab.toHtml();
    }
    
    static {
        OUT = Logger.getLogger(GetModelGiveoutput.class.getName());
    }
}
