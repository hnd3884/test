package com.me.devicemanagement.framework.webclient.graphs;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;

public class DCGraphToolTipGenerator
{
    public String generateToolTip(final PieDataset dataset, final Comparable section, final int index) {
        final Comparable label = dataset.getKey(index);
        final String toolTip = label + " (" + dataset.getValue(index) + ") ";
        return this.getPrettyToolTip(toolTip, "", 0);
    }
    
    public String generateToolTip(final CategoryDataset dataset, final int series, final int category) {
        final String label = (String)dataset.getRowKey(series);
        final String toolTip = label + " (" + dataset.getValue(series, category) + ") ";
        return this.getPrettyToolTip(toolTip, "", 0);
    }
    
    private String getPrettyToolTip(final String toolTip, final String caption, final int width) {
        String textAlignOptions = "VAUTO, HAUTO, WRAP";
        if (width > 0) {
            textAlignOptions = "VAUTO, HAUTO, WIDTH, '" + width + "'";
        }
        else if (width < 0) {
            textAlignOptions = "VAUTO, HAUTO";
        }
        return toolTip + "', " + textAlignOptions + ", TIMEOUT, 10000, CAPTION, '" + caption + "', FGCLASS, 'bodytext', BGCLASS, 'tooltiptable', TEXTFONTCLASS, 'bodytext', CAPTIONFONTCLASS, 'bodytext";
    }
}
