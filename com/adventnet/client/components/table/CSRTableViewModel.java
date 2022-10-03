package com.adventnet.client.components.table;

import com.adventnet.client.components.table.web.CSRTableTransformerContext;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.TableViewModel;

public class CSRTableViewModel extends TableViewModel
{
    public CSRTableViewModel(final Object tableModel, final ViewContext viewContext) throws Exception {
        super(tableModel, viewContext);
    }
    
    public CSRTableViewModel(final ViewContext viewContext) throws Exception {
        super(viewContext);
    }
    
    @Override
    public void init() throws Exception {
        super.init();
        this.transformerContext = new CSRTableTransformerContext(this, this.viewContext);
    }
}
