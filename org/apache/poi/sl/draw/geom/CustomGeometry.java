package org.apache.poi.sl.draw.geom;

import org.apache.poi.sl.draw.binding.CTGeomRect;
import org.apache.poi.sl.draw.binding.CTPath2DList;
import java.util.Iterator;
import org.apache.poi.sl.draw.binding.CTGeomGuideList;
import org.apache.poi.sl.draw.binding.CTPath2D;
import org.apache.poi.sl.draw.binding.CTGeomGuide;
import java.util.ArrayList;
import org.apache.poi.sl.draw.binding.CTCustomGeometry2D;
import java.util.List;

public class CustomGeometry implements Iterable<Path>
{
    final List<Guide> adjusts;
    final List<Guide> guides;
    final List<Path> paths;
    Path textBounds;
    
    public CustomGeometry(final CTCustomGeometry2D geom) {
        this.adjusts = new ArrayList<Guide>();
        this.guides = new ArrayList<Guide>();
        this.paths = new ArrayList<Path>();
        final CTGeomGuideList avLst = geom.getAvLst();
        if (avLst != null) {
            for (final CTGeomGuide gd : avLst.getGd()) {
                this.adjusts.add(new AdjustValue(gd));
            }
        }
        final CTGeomGuideList gdLst = geom.getGdLst();
        if (gdLst != null) {
            for (final CTGeomGuide gd2 : gdLst.getGd()) {
                this.guides.add(new Guide(gd2));
            }
        }
        final CTPath2DList pathLst = geom.getPathLst();
        if (pathLst != null) {
            for (final CTPath2D spPath : pathLst.getPath()) {
                this.paths.add(new Path(spPath));
            }
        }
        final CTGeomRect rect = geom.getRect();
        if (rect != null) {
            (this.textBounds = new Path()).addCommand(new MoveToCommand(rect.getL(), rect.getT()));
            this.textBounds.addCommand(new LineToCommand(rect.getR(), rect.getT()));
            this.textBounds.addCommand(new LineToCommand(rect.getR(), rect.getB()));
            this.textBounds.addCommand(new LineToCommand(rect.getL(), rect.getB()));
            this.textBounds.addCommand(new ClosePathCommand());
        }
    }
    
    @Override
    public Iterator<Path> iterator() {
        return this.paths.iterator();
    }
    
    public Path getTextBounds() {
        return this.textBounds;
    }
}
