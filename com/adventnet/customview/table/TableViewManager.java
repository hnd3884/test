package com.adventnet.customview.table;

import com.adventnet.customview.CustomViewException;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;

public class TableViewManager
{
    private static TableModelCoreServiceProvider tableModelCoreServiceProvider;
    
    private TableViewManager() {
    }
    
    public static ViewData getData(final CustomViewRequest cvRequest) throws CustomViewException {
        return TableViewManager.tableModelCoreServiceProvider.process(cvRequest);
    }
    
    static {
        TableViewManager.tableModelCoreServiceProvider = new TableModelCoreServiceProvider();
    }
}
