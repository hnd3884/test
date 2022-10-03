package com.adventnet.ds.adapter;

import com.adventnet.ds.query.Column;
import java.util.List;

public class SumFunctionHandler extends AbstractFunctionHandler
{
    @Override
    public void processNextRow(final List rows, final List dataList) {
        final int[] indexes = this.getSumIndexes();
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
                        final Object value2 = dataList.get(j);
                        if (value2 instanceof Integer) {
                            dataList.set(j, new Integer((int)value2 + (int)value));
                        }
                        else if (value2 instanceof Double) {
                            dataList.set(j, new Double((double)value2 + (double)value));
                        }
                        else if (value2 instanceof Float) {
                            dataList.set(j, new Float((float)value2 + (float)value));
                        }
                        else if (value2 instanceof Long) {
                            dataList.set(j, new Long((long)value2 + (long)value));
                        }
                    }
                }
                else if (i == 0) {
                    dataList.set(j, value);
                }
            }
        }
    }
    
    private int[] getSumIndexes() {
        final List selectCols = this.actualSQ.getSelectColumns();
        final int selSize = selectCols.size();
        final int[] indexes = new int[selSize];
        int currIndex = 0;
        for (int i = 0; i < selSize; ++i) {
            final Column col = selectCols.get(i);
            if (col.getColumn() != null && col.getFunction() == 5) {
                indexes[currIndex] = i;
                ++currIndex;
            }
        }
        return indexes;
    }
}
