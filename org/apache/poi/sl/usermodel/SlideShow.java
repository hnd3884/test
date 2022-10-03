package org.apache.poi.sl.usermodel;

import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.extractor.POITextExtractor;
import java.io.OutputStream;
import java.io.File;
import java.io.InputStream;
import java.awt.Dimension;
import java.util.List;
import java.io.IOException;
import java.io.Closeable;

public interface SlideShow<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends Closeable
{
    Slide<S, P> createSlide() throws IOException;
    
    List<? extends Slide<S, P>> getSlides();
    
    MasterSheet<S, P> createMasterSheet() throws IOException;
    
    List<? extends MasterSheet<S, P>> getSlideMasters();
    
    Dimension getPageSize();
    
    void setPageSize(final Dimension p0);
    
    List<? extends PictureData> getPictureData();
    
    PictureData addPicture(final byte[] p0, final PictureData.PictureType p1) throws IOException;
    
    PictureData addPicture(final InputStream p0, final PictureData.PictureType p1) throws IOException;
    
    PictureData addPicture(final File p0, final PictureData.PictureType p1) throws IOException;
    
    PictureData findPictureData(final byte[] p0);
    
    void write(final OutputStream p0) throws IOException;
    
    POITextExtractor getMetadataTextExtractor();
    
    Object getPersistDocument();
    
    FontInfo addFont(final InputStream p0) throws IOException;
    
    List<? extends FontInfo> getFonts();
}
