package com.adventnet.client.components.table.web;

import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.web.WebViewModel;
import java.util.ArrayList;
import com.adventnet.beans.xtable.SortColumn;
import java.util.HashMap;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.view.web.DefaultViewController;

public class CustomTableRetrieverAction extends DefaultViewController
{
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        final TableViewModel viewModel = this.getTableViewModel(viewCtx);
        viewModel.init();
        viewModel.getTableTransformerContext().setRequest(viewCtx.getRequest());
        viewCtx.setViewModel((Object)viewModel);
    }
    
    private TableViewModel getTableViewModel(final ViewContext viewCtx) throws Exception {
        final Object tableModel = this.getTableModel(viewCtx);
        final TableViewModel viewModel = new TableViewModel(tableModel, viewCtx);
        return viewModel;
    }
    
    public Object getTableModel(final ViewContext vctx) throws Exception {
        boolean pageEnd = false;
        final String origView = vctx.getUniqueId();
        final WebViewModel viewM = WebViewAPI.getConfigModel((Object)origView, true);
        final DataObject viewConfig = viewM.getViewConfiguration();
        final Row viewconfig = viewConfig.getRow("ViewConfiguration");
        final Long componentNo = (Long)viewconfig.get(3);
        String vcClassName = null;
        final Row ConfigRow = viewConfig.getRow("ACTableViewConfig");
        vcClassName = (String)ConfigRow.get(18);
        final String defaultSortColumn = (String)ConfigRow.get(13);
        String defaultSortOrder = (String)ConfigRow.get(14);
        defaultSortOrder = ("DESC".equals(defaultSortOrder) ? "D" : "A");
        final CustomTableModelRenderer modelRenderer = (CustomTableModelRenderer)WebClientUtil.createInstance(vcClassName);
        final HashMap<String, Object> tableparams = new HashMap<String, Object>();
        String SC;
        if (vctx.getURLStateParameter("_SB") != null) {
            tableparams.put("sortedColumn", vctx.getURLStateParameter("_SB"));
            SC = (String)vctx.getURLStateParameter("_SB");
        }
        else if (vctx.getStateParameter("_SB") != null) {
            tableparams.put("sortedColumn", vctx.getStateParameter("_SB"));
            SC = (String)vctx.getStateParameter("_SB");
        }
        else if (defaultSortColumn != null) {
            tableparams.put("sortedColumn", defaultSortColumn);
            vctx.setStateOrURLStateParam("_SB", (Object)defaultSortColumn);
            SC = defaultSortColumn;
        }
        else {
            tableparams.put("sortedColumn", "");
            SC = "";
        }
        char SO;
        if (vctx.getURLStateParameter("_SO") != null) {
            tableparams.put("sortOrder", vctx.getURLStateParameter("_SO"));
            SO = ((String)vctx.getURLStateParameter("_SO")).toCharArray()[0];
        }
        else if (vctx.getStateParameter("_SO") != null) {
            tableparams.put("sortOrder", vctx.getStateParameter("_SO"));
            SO = ((String)vctx.getStateParameter("_SO")).toCharArray()[0];
        }
        else if (defaultSortOrder != null) {
            tableparams.put("sortOrder", defaultSortOrder);
            vctx.setStateOrURLStateParam("_SO", (Object)defaultSortOrder);
            SO = defaultSortOrder.toCharArray()[0];
        }
        else {
            tableparams.put("sortOrder", "");
            SO = 'A';
        }
        String SearchValue = null;
        String SearchColumn = null;
        if (vctx.getURLStateParameter("SEARCH_COLUMN") != null && vctx.getURLStateParameter("SEARCH_VALUE") != null) {
            tableparams.put("searchColumns", vctx.getURLStateParameter("SEARCH_COLUMN"));
            tableparams.put("searchValues", vctx.getURLStateParameter("SEARCH_VALUE"));
            SearchValue = (String)vctx.getURLStateParameter("SEARCH_VALUE");
            SearchColumn = (String)vctx.getURLStateParameter("SEARCH_COLUMN");
        }
        else if (vctx.getStateParameter("SEARCH_COLUMN") != null && vctx.getStateParameter("SEARCH_VALUE") != null) {
            tableparams.put("searchColumns", vctx.getStateParameter("SEARCH_COLUMN"));
            tableparams.put("searchValues", vctx.getStateParameter("SEARCH_VALUE"));
            SearchValue = (String)vctx.getStateParameter("SEARCH_VALUE");
            SearchColumn = (String)vctx.getStateParameter("SEARCH_COLUMN");
        }
        boolean pageLengthChanged = false;
        int PL;
        if (vctx.getURLStateParameter("_PL") != null) {
            PL = Integer.parseInt((String)vctx.getURLStateParameter("_PL"));
            if (vctx.getURLStateParameter("OLD_PL") != null && Integer.parseInt((String)vctx.getURLStateParameter("OLD_PL")) != PL) {
                pageLengthChanged = true;
            }
            vctx.setURLStateParameter("OLD_PL", (Object)(PL + ""));
        }
        else if (vctx.getStateParameter("_PL") != null) {
            PL = Integer.parseInt((String)vctx.getStateParameter("_PL"));
            if (vctx.getStateParameter("OLD_PL") != null && Integer.parseInt((String)vctx.getStateParameter("OLD_PL")) != PL) {
                pageLengthChanged = true;
            }
            vctx.setStateOrURLStateParam("OLD_PL", (Object)(PL + ""));
        }
        else {
            PL = 0;
        }
        int PN;
        if (vctx.getURLStateParameter("_PN") != null) {
            PN = Integer.parseInt((String)vctx.getURLStateParameter("_PN"));
        }
        else if (vctx.getStateParameter("_PN") != null) {
            PN = Integer.parseInt((String)vctx.getStateParameter("_PN"));
        }
        else {
            PN = 0;
        }
        long SI;
        long EI;
        int pageLength;
        if (PL != 0 && PN == 1 && pageLengthChanged) {
            SI = 1L;
            EI = PL;
            pageLength = (int)(EI - SI + 1L);
            tableparams.put("fromIndex", SI + "");
            tableparams.put("toIndex", EI + "");
        }
        else {
            if (vctx.getURLStateParameter("_FI") != null) {
                tableparams.put("fromIndex", vctx.getURLStateParameter("_FI"));
                SI = Integer.parseInt((String)vctx.getURLStateParameter("_FI"));
            }
            else if (vctx.getStateParameter("_FI") != null) {
                tableparams.put("fromIndex", vctx.getStateParameter("_FI"));
                SI = Integer.parseInt((String)vctx.getStateParameter("_FI"));
            }
            else {
                tableparams.put("fromIndex", "1");
                SI = 1L;
            }
            if (vctx.getURLStateParameter("_PL") != null) {
                final int length = Integer.parseInt((String)vctx.getURLStateParameter("_PL"));
                final int startindex = Integer.parseInt(tableparams.get("fromIndex"));
                tableparams.put("toIndex", length + startindex - 1 + "");
                EI = length + startindex - 1;
            }
            else if (vctx.getStateParameter("_PL") != null) {
                final int length = Integer.parseInt((String)vctx.getStateParameter("_PL"));
                final int startindex = Integer.parseInt(tableparams.get("fromIndex"));
                tableparams.put("toIndex", length + startindex - 1 + "");
                EI = length + startindex - 1;
            }
            else if (ConfigRow.get(12) != null) {
                final Integer toindex = (Integer)ConfigRow.get(12);
                final int ti = toindex;
                tableparams.put("toIndex", ti + "");
                EI = ti;
            }
            else {
                tableparams.put("toIndex", "10");
                EI = 10L;
            }
            pageLength = (int)(EI - SI + 1L);
            int tot = -1;
            if (vctx.getStateParameter("_TL") != null) {
                tot = Integer.parseInt((String)vctx.getStateParameter("_TL"));
                if (tot != -1 && PL * PN > tot) {
                    EI = tot;
                    pageEnd = true;
                }
                tableparams.put("toIndex", EI + "");
                pageLength = Integer.parseInt((String)vctx.getStateParameter("_PL"));
            }
        }
        vctx.setStateOrURLStateParam("_TI", tableparams.get("toIndex"));
        vctx.setStateOrURLStateParam("_FI", tableparams.get("fromIndex"));
        final CustomTableModel model = modelRenderer.renderModel(tableparams, vctx);
        model.setSortOrder(SO);
        model.setSortedColumn(SC);
        model.setModelSortColumns(new SortColumn[] { new SortColumn(model.getColumnIndex(SC), SO == 'A') });
        vctx.setStateOrURLStateParam("_SB", (Object)SC);
        vctx.setStateOrURLStateParam("SEARCH_COLUMN", (Object)SearchColumn);
        vctx.setStateOrURLStateParam("SEARCH_VALUE", (Object)SearchValue);
        final int TR = model.getTotalRecords();
        int pageNumber = 0;
        final String sortOrder = model.getSortOrder() + "";
        if (pageLength != 0) {
            pageNumber = (int)SI / pageLength + 1;
        }
        vctx.setStateOrURLStateParam("_PN", (Object)(pageNumber + ""));
        vctx.setStateOrURLStateParam("_SO", (Object)(sortOrder + ""));
        vctx.setStateOrURLStateParam("componentName", (Object)WebViewAPI.getUIComponentName(componentNo));
        vctx.setStateOrURLStateParam("_TL", (Object)(TR + ""));
        List<Integer> rangelist = model.getRangeList();
        if (rangelist == null) {
            rangelist = new ArrayList<Integer>(5);
            rangelist.add(0, 10);
            rangelist.add(1, 20);
            rangelist.add(2, 30);
            rangelist.add(3, 40);
            rangelist.add(4, 50);
        }
        final int toindex2 = (int)EI;
        final int pageno = Integer.parseInt((String)vctx.getStateParameter("_PN"));
        final int fromindex = (int)SI;
        int totalPages = 1;
        if (TR - (toindex2 - fromindex + 1) > 0) {
            totalPages = TR / (toindex2 - fromindex + 1);
        }
        if (toindex2 - fromindex + 1 != 0 && TR % (toindex2 - fromindex + 1) != 0) {
            ++totalPages;
        }
        if (pageEnd) {
            totalPages = TR / pageLength;
            if (TR % pageLength != 0) {
                ++totalPages;
            }
        }
        if (toindex2 > TR) {
            vctx.setStateOrURLStateParam("_TI", (Object)(TR + ""));
        }
        vctx.setStateOrURLStateParam("_PL", (Object)(pageLength + ""));
        vctx.setStateOrURLStateParam("rangeList", (Object)rangelist);
        if (pageno - 2 > 0 && pageno + 2 <= totalPages) {
            vctx.setStateOrURLStateParam("endLinkIndex", (Object)(pageno + 2 + ""));
            vctx.setStateOrURLStateParam("startLinkIndex", (Object)(pageno - 2 + ""));
        }
        else if (pageno - 2 <= 0) {
            vctx.setStateOrURLStateParam("endLinkIndex", (Object)(((totalPages < 5) ? totalPages : 5) + ""));
            vctx.setStateOrURLStateParam("startLinkIndex", (Object)"1");
        }
        else if (pageno + 2 > totalPages) {
            vctx.setStateOrURLStateParam("endLinkIndex", (Object)(totalPages + ""));
            vctx.setStateOrURLStateParam("startLinkIndex", (Object)(((totalPages - 5 > 0) ? (totalPages - 5) : 1) + ""));
        }
        model.setEndIndex(EI);
        model.setStartIndex(SI);
        model.setPageLength(pageLength);
        model.setTotalRecords(TR);
        return model;
    }
}
