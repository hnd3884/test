package org.jfree.chart.labels;

import org.jfree.data.xy.XYDataset;

public interface XYToolTipGenerator
{
    String generateToolTip(final XYDataset p0, final int p1, final int p2);
}
