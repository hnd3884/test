package com.adventnet.beans.treetable;

import java.util.Locale;
import com.adventnet.beans.xtable.SortColumn;

public interface TreeTableModelSorter
{
    void sort(final TreeTableModel p0, final Object p1, final SortColumn[] p2, final Locale p3, final int p4, final int p5);
}
