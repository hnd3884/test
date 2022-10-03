package org.jfree.chart.imagemap;

public class StandardToolTipTagFragmentGenerator implements ToolTipTagFragmentGenerator
{
    public String generateToolTipFragment(final String toolTipText) {
        return " title=\"" + toolTipText + "\" alt=\"\"";
    }
}
