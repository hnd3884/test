package com.adventnet.client.components.table.web;

import com.adventnet.client.components.table.TableViewState;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;

public class CSRTableTransformerContext extends TableTransformerContext
{
    public CSRTableTransformerContext(final TableViewModel tableViewModel, final ViewContext viewContext) {
        super(tableViewModel, viewContext);
    }
    
    @Override
    public void setSelectCriteria() {
        this.selectCriteria = new HashMap<String, String>();
        this.selectComparators = new HashMap<String, Integer>();
        final TableViewState state = (TableViewState)this.viewContext.getViewState();
        final String[] columnNames = state.getSearchColumns();
        final String[] columnValues = state.getSearchValues();
        final int[] comparators = state.getSearchComparators();
        final int colSize = columnNames.length;
        final int valSize = columnValues.length;
        for (int count = 0; count < colSize; ++count) {
            if (colSize == valSize || count <= valSize - 1) {
                if (columnValues[count].equals("")) {
                    this.selectCriteria.put(columnNames[count], null);
                }
                else {
                    this.selectCriteria.put(columnNames[count], columnValues[count]);
                }
            }
            else {
                this.selectCriteria.put(columnNames[count], null);
            }
            if (comparators.length > 0) {
                this.selectComparators.put(columnNames[count], comparators[count]);
            }
        }
    }
    
    @Override
    public void reset() {
        this.renderedProperties.clear();
        this.searchValue = "";
        this.searchComparator = -999;
        this.isSortEnabled = (boolean)this.configRow.get("SORTENABLED");
        this.isSearchEnabled = (boolean)this.configRow.get("SEARCHENABLED");
        this.configuredColStyle = (String)this.configRow.get("CSSCLASS");
        this.headerCss = (String)this.configRow.get("HEADERCSS");
        if (this.isSearchEnabled) {
            final String propertyName = this.getPropertyName();
            this.searchValue = this.selectCriteria.get(propertyName);
            if (this.selectComparators.containsKey(propertyName)) {
                this.searchComparator = this.selectComparators.get(propertyName);
            }
        }
    }
    
    public int getSearchComparator() {
        return this.searchComparator;
    }
}
