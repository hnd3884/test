package org.jfree.chart.labels;

import org.jfree.data.general.PieDataset;

public interface PieToolTipGenerator
{
    String generateToolTip(final PieDataset p0, final Comparable p1);
}
