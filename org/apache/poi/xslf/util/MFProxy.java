package org.apache.poi.xslf.util;

import org.apache.poi.sl.draw.EmbeddedExtractor;
import org.apache.poi.common.usermodel.GenericRecord;
import java.util.Collections;
import java.util.Set;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import org.apache.poi.util.Internal;
import java.io.Closeable;

@Internal
abstract class MFProxy implements Closeable
{
    boolean ignoreParse;
    boolean quite;
    
    void setIgnoreParse(final boolean ignoreParse) {
        this.ignoreParse = ignoreParse;
    }
    
    void setQuite(final boolean quite) {
        this.quite = quite;
    }
    
    abstract void parse(final File p0) throws IOException;
    
    abstract void parse(final InputStream p0) throws IOException;
    
    abstract Dimension2D getSize();
    
    void setSlideNo(final int slideNo) {
    }
    
    abstract String getTitle();
    
    abstract void draw(final Graphics2D p0);
    
    int getSlideCount() {
        return 1;
    }
    
    Set<Integer> slideIndexes(final String range) {
        return Collections.singleton(1);
    }
    
    abstract GenericRecord getRoot();
    
    abstract Iterable<EmbeddedExtractor.EmbeddedPart> getEmbeddings(final int p0);
}
