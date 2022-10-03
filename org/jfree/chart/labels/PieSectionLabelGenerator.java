package org.jfree.chart.labels;

import java.text.AttributedString;
import org.jfree.data.general.PieDataset;

public interface PieSectionLabelGenerator
{
    String generateSectionLabel(final PieDataset p0, final Comparable p1);
    
    AttributedString generateAttributedSectionLabel(final PieDataset p0, final Comparable p1);
}
