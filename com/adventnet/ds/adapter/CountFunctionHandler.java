package com.adventnet.ds.adapter;

import com.adventnet.ds.query.Column;
import java.util.List;

public class CountFunctionHandler extends AbstractFunctionHandler
{
    @Override
    public void processNextRow(final List rows, final List dataList) {
        final int[] indexes = this.getCountIndexes();
        final int selectSize = this.actualSQ.getSelectColumns().size();
        for (int size = rows.size(), i = 0; i < size; ++i) {
            final List currRow = rows.get(i);
            final int currIndex = 0;
            for (int j = 0; j < selectSize; ++j) {
                final Object value = currRow.get(j);
                if (j == indexes[currIndex]) {
                    if (i == 0) {
                        dataList.set(j, value);
                    }
                    else {
                        dataList.set(j, new Integer(dataList.get(j) + (int)value));
                    }
                }
                else if (i == 0) {
                    dataList.set(j, value);
                }
            }
        }
    }
    
    private int[] getCountIndexes() {
        final List selectCols = this.actualSQ.getSelectColumns();
        final int selSize = selectCols.size();
        final int[] indexes = new int[selSize];
        int currIndex = 0;
        for (int i = 0; i < selSize; ++i) {
            final Column col = selectCols.get(i);
            if (col.getColumn() != null && col.getFunction() == 2) {
                indexes[currIndex] = i;
                ++currIndex;
            }
        }
        return indexes;
    }
}
