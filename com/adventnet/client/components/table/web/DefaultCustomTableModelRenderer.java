package com.adventnet.client.components.table.web;

import java.util.List;
import java.util.ArrayList;
import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;

public class DefaultCustomTableModelRenderer implements CustomTableModelRenderer
{
    @Override
    public CustomTableModel renderModel(final HashMap tableParams, final ViewContext vcxt) throws Exception {
        final String[] header = { "DEFAULT", "CUSTOM", "TABLE" };
        final String[][] rows = { { "Extend", "DefaultCustomTableModelRenderer", "and" }, { "override", "renderModel", "method." }, { "override", "renderModel", "method." }, { "override", "renderModel", "method." }, { "override", "renderModel", "method." }, { "override", "renderModel", "method." }, { "override", "renderModel", "method." }, { "override", "renderModel", "method." }, { "override", "renderModel", "method." }, { "Give entry in", "ACTableViewConfig", "have your table" } };
        final CustomTableModel model = new CustomTableModel(rows, header, vcxt);
        model.setTotalRecords(20);
        final ArrayList<Integer> rangelist = new ArrayList<Integer>(5);
        rangelist.add(0, 5);
        rangelist.add(1, 10);
        rangelist.add(2, 15);
        rangelist.add(3, 20);
        rangelist.add(4, 30);
        model.setRangeList(rangelist);
        return model;
    }
}
