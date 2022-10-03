package org.jfree.chart.labels;

import org.jfree.data.category.CategoryDataset;

public interface CategoryItemLabelGenerator
{
    String generateRowLabel(final CategoryDataset p0, final int p1);
    
    String generateColumnLabel(final CategoryDataset p0, final int p1);
    
    String generateLabel(final CategoryDataset p0, final int p1, final int p2);
}
