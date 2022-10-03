package org.jfree.chart.title;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import java.awt.Shape;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.block.EntityBlockParams;
import org.jfree.chart.block.BlockResult;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.block.Arrangement;
import org.jfree.chart.block.BlockContainer;

public class LegendItemBlockContainer extends BlockContainer
{
    private int dataset;
    private int series;
    
    public LegendItemBlockContainer(final Arrangement arrangement, final int dataset, final int series) {
        super(arrangement);
        this.dataset = dataset;
        this.series = series;
    }
    
    public int getDatasetIndex() {
        return this.dataset;
    }
    
    public int getSeriesIndex() {
        return this.series;
    }
    
    public Object draw(final Graphics2D g2, final Rectangle2D area, final Object params) {
        super.draw(g2, area, null);
        EntityBlockParams ebp = null;
        final BlockResult r = new BlockResult();
        if (params instanceof EntityBlockParams) {
            ebp = (EntityBlockParams)params;
            if (ebp.getGenerateEntities()) {
                final EntityCollection ec = new StandardEntityCollection();
                final LegendItemEntity entity = new LegendItemEntity((Shape)area.clone());
                entity.setSeriesIndex(this.series);
                ec.add(entity);
                r.setEntityCollection(ec);
            }
        }
        return r;
    }
}
