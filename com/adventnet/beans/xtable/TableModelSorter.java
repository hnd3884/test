package com.adventnet.beans.xtable;

import java.util.Locale;
import javax.swing.table.TableModel;

public interface TableModelSorter
{
    TableModel sortView(final TableModel p0, final SortColumn[] p1, final Locale p2);
    
    TableModel sortModel(final TableModel p0, final SortColumn[] p1, final Locale p2);
}
