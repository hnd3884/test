package org.jfree.chart.imagemap;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.util.StringUtils;
import java.io.IOException;
import org.jfree.chart.ChartRenderingInfo;
import java.io.PrintWriter;

public class ImageMapUtilities
{
    public static void writeImageMap(final PrintWriter writer, final String name, final ChartRenderingInfo info) throws IOException {
        writeImageMap(writer, name, info, new StandardToolTipTagFragmentGenerator(), new StandardURLTagFragmentGenerator());
    }
    
    public static void writeImageMap(final PrintWriter writer, final String name, final ChartRenderingInfo info, final boolean useOverLibForToolTips) throws IOException {
        ToolTipTagFragmentGenerator toolTipTagFragmentGenerator = null;
        if (useOverLibForToolTips) {
            toolTipTagFragmentGenerator = new OverLIBToolTipTagFragmentGenerator();
        }
        else {
            toolTipTagFragmentGenerator = new StandardToolTipTagFragmentGenerator();
        }
        writeImageMap(writer, name, info, toolTipTagFragmentGenerator, new StandardURLTagFragmentGenerator());
    }
    
    public static void writeImageMap(final PrintWriter writer, final String name, final ChartRenderingInfo info, final ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, final URLTagFragmentGenerator urlTagFragmentGenerator) throws IOException {
        writer.println(getImageMap(name, info, toolTipTagFragmentGenerator, urlTagFragmentGenerator));
    }
    
    public static String getImageMap(final String name, final ChartRenderingInfo info) {
        return getImageMap(name, info, new StandardToolTipTagFragmentGenerator(), new StandardURLTagFragmentGenerator());
    }
    
    public static String getImageMap(final String name, final ChartRenderingInfo info, final ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, final URLTagFragmentGenerator urlTagFragmentGenerator) {
        final StringBuffer sb = new StringBuffer();
        sb.append("<map id=\"" + name + "\" name=\"" + name + "\">");
        sb.append(StringUtils.getLineSeparator());
        final EntityCollection entities = info.getEntityCollection();
        if (entities != null) {
            final int count = entities.getEntityCount();
            for (int i = count - 1; i >= 0; --i) {
                final ChartEntity entity = entities.getEntity(i);
                if (entity.getToolTipText() != null || entity.getURLText() != null) {
                    final String area = entity.getImageMapAreaTag(toolTipTagFragmentGenerator, urlTagFragmentGenerator);
                    if (area.length() > 0) {
                        sb.append(area);
                        sb.append(StringUtils.getLineSeparator());
                    }
                }
            }
        }
        sb.append("</map>");
        return sb.toString();
    }
}
