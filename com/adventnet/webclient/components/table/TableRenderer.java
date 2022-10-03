package com.adventnet.webclient.components.table;

import java.util.Properties;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.adventnet.webclient.util.ValueRetriever;

public abstract class TableRenderer
{
    public ValueRetriever retriever;
    
    public TableRenderer() {
        this.retriever = null;
    }
    
    public final void setValueRetriever(final ValueRetriever retriever) {
        this.retriever = retriever;
    }
    
    public abstract Properties renderCell(final TableNavigatorModel p0, final int p1, final int p2, final ViewColumn p3);
    
    public abstract Properties renderHeader(final ViewColumn p0);
}
