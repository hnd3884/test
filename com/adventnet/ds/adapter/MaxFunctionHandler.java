package com.adventnet.ds.adapter;

import com.adventnet.ds.query.Column;
import java.util.List;
import java.text.Collator;
import com.adventnet.ds.query.SelectQuery;
import java.util.Comparator;

public class MaxFunctionHandler extends AbstractFunctionHandler
{
    Comparator comp;
    
    public MaxFunctionHandler() {
        this.comp = null;
    }
    
    @Override
    public void init(final SelectQuery actualSQ, final SelectQuery sqForDataRetrieval) throws DataSourceException {
        super.init(actualSQ, sqForDataRetrieval);
        this.comp = Collator.getInstance();
    }
    
    @Override
    public void processNextRow(final List rows, final List dataList) {
        final int[] indexes = this.getMaxIndexes();
        final int selectSize = this.actualSQ.getSelectColumns().size();
        for (int size = rows.size(), i = 0; i < size; ++i) {
            final List currRow = rows.get(i);
            int currIndex = 0;
            for (int j = 0; j < selectSize; ++j) {
                final Object value = currRow.get(j);
                if (j == indexes[currIndex]) {
                    ++currIndex;
                    if (i == 0) {
                        dataList.set(j, value);
                    }
                    else {
                        final int compared = dataList.get(j).compareTo(value);
                        if (compared == 0) {
                            dataList.set(j, value);
                        }
                        else if (compared < 0) {
                            dataList.set(j, value);
                        }
                        else if (compared > 0) {
                            dataList.set(j, dataList.get(j));
                        }
                    }
                }
                else if (i == 0 && dataList.get(j) instanceof Column) {
                    dataList.set(j, value);
                }
            }
        }
    }
    
    private int[] getMaxIndexes() {
        final List selectCols = this.actualSQ.getSelectColumns();
        final int selSize = selectCols.size();
        final int[] indexes = new int[selSize];
        int currIndex = 0;
        for (int i = 0; i < selSize; ++i) {
            final Column col = selectCols.get(i);
            if (col.getColumn() != null && col.getFunction() == 4) {
                indexes[currIndex] = i;
                ++currIndex;
            }
        }
        return indexes;
    }
}
