package com.adventnet.ds.adapter;

import com.adventnet.ds.query.SortColumn;
import java.util.List;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;

public class DistinctFunctionHandler extends AbstractFunctionHandler
{
    @Override
    public void init(final SelectQuery actualSQ, final SelectQuery sqForDataRetrieval) throws DataSourceException {
        super.init(actualSQ, sqForDataRetrieval);
        final List selectCols = sqForDataRetrieval.getSelectColumns();
        final int selSize = selectCols.size();
        if (selSize != 1) {
            throw new DataSourceException("When distinct is selected, there should be only one column selected, either DISTINCT(<col_name>) or COUNT(DISTINCT(<col_name>))");
        }
        final Column col = selectCols.get(0);
        if (col.getFunction() == 2 && col.getColumn().getFunction() == 1) {
            throw new DataSourceException("COUNT(DISTINCT) is not supported as of now");
        }
        if (col.getFunction() == 1) {
            final boolean exist = this.doesColExistInSort(col.getColumn(), sqForDataRetrieval.getSortColumns());
            if (!exist) {
                sqForDataRetrieval.addSortColumn(new SortColumn(col.getColumn(), true));
            }
        }
    }
    
    private boolean doesColExistInSort(final Column col, final List sortCols) {
        for (int sortSize = sortCols.size(), i = 0; i < sortSize; ++i) {
            final SortColumn sort = sortCols.get(i);
            if (sort.getColumn().equals(col)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void processNextRow(final List rows, final List dataList) {
        final List list = rows.get(0);
        for (int size = list.size(), i = 0; i < size; ++i) {
            dataList.set(i, list.get(i));
        }
    }
}
