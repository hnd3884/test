package org.apache.poi.ss.util;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.Units;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.ClientAnchor;
import java.io.ByteArrayInputStream;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.io.InputStream;
import org.apache.poi.util.POILogger;

public class ImageUtils
{
    private static final POILogger logger;
    public static final int PIXEL_DPI = 96;
    
    public static Dimension getImageDimension(final InputStream is, final int type) {
        final Dimension size = new Dimension();
        switch (type) {
            case 5:
            case 6:
            case 7: {
                try (final ImageInputStream iis = ImageIO.createImageInputStream(is)) {
                    final Iterator<ImageReader> i = ImageIO.getImageReaders(iis);
                    if (i.hasNext()) {
                        final ImageReader r = i.next();
                        try {
                            r.setInput(iis);
                            final BufferedImage img = r.read(0);
                            final int[] dpi = getResolution(r);
                            if (dpi[0] == 0) {
                                dpi[0] = 96;
                            }
                            if (dpi[1] == 0) {
                                dpi[1] = 96;
                            }
                            size.width = img.getWidth() * 96 / dpi[0];
                            size.height = img.getHeight() * 96 / dpi[1];
                        }
                        finally {
                            r.dispose();
                        }
                    }
                    else {
                        ImageUtils.logger.log(5, "ImageIO found no images");
                    }
                }
                catch (final IOException e) {
                    ImageUtils.logger.log(5, e);
                }
                break;
            }
            default: {
                ImageUtils.logger.log(5, "Only JPEG, PNG and DIB pictures can be automatically sized");
                break;
            }
        }
        return size;
    }
    
    public static int[] getResolution(final ImageReader r) throws IOException {
        int hdpi = 96;
        int vdpi = 96;
        final double mm2inch = 25.4;
        final Element node = (Element)r.getImageMetadata(0).getAsTree("javax_imageio_1.0");
        NodeList lst = node.getElementsByTagName("HorizontalPixelSize");
        if (lst != null && lst.getLength() == 1) {
            hdpi = (int)(mm2inch / Float.parseFloat(((Element)lst.item(0)).getAttribute("value")));
        }
        lst = node.getElementsByTagName("VerticalPixelSize");
        if (lst != null && lst.getLength() == 1) {
            vdpi = (int)(mm2inch / Float.parseFloat(((Element)lst.item(0)).getAttribute("value")));
        }
        return new int[] { hdpi, vdpi };
    }
    
    public static Dimension setPreferredSize(final Picture picture, final double scaleX, final double scaleY) {
        final ClientAnchor anchor = picture.getClientAnchor();
        final boolean isHSSF = anchor instanceof HSSFClientAnchor;
        final PictureData data = picture.getPictureData();
        final Sheet sheet = picture.getSheet();
        final Dimension imgSize = getImageDimension(new ByteArrayInputStream(data.getData()), data.getPictureType());
        final Dimension anchorSize = getDimensionFromAnchor(picture);
        final double scaledWidth = (scaleX == Double.MAX_VALUE) ? imgSize.getWidth() : (anchorSize.getWidth() / 9525.0 * scaleX);
        final double scaledHeight = (scaleY == Double.MAX_VALUE) ? imgSize.getHeight() : (anchorSize.getHeight() / 9525.0 * scaleY);
        double w = 0.0;
        int col2 = anchor.getCol1();
        int dx2 = 0;
        w = sheet.getColumnWidthInPixels(col2++);
        if (isHSSF) {
            w *= 1.0 - anchor.getDx1() / 1024.0;
        }
        else {
            w -= anchor.getDx1() / 9525.0;
        }
        while (w < scaledWidth) {
            w += sheet.getColumnWidthInPixels(col2++);
        }
        if (w > scaledWidth) {
            final double cw = sheet.getColumnWidthInPixels(--col2);
            final double delta = w - scaledWidth;
            if (isHSSF) {
                dx2 = (int)((cw - delta) / cw * 1024.0);
            }
            else {
                dx2 = (int)((cw - delta) * 9525.0);
            }
            if (dx2 < 0) {
                dx2 = 0;
            }
        }
        anchor.setCol2(col2);
        anchor.setDx2(dx2);
        double h = 0.0;
        int row2 = anchor.getRow1();
        int dy2 = 0;
        h = getRowHeightInPixels(sheet, row2++);
        if (isHSSF) {
            h *= 1.0 - anchor.getDy1() / 256.0;
        }
        else {
            h -= anchor.getDy1() / 9525.0;
        }
        while (h < scaledHeight) {
            h += getRowHeightInPixels(sheet, row2++);
        }
        if (h > scaledHeight) {
            final double ch = getRowHeightInPixels(sheet, --row2);
            final double delta2 = h - scaledHeight;
            if (isHSSF) {
                dy2 = (int)((ch - delta2) / ch * 256.0);
            }
            else {
                dy2 = (int)((ch - delta2) * 9525.0);
            }
            if (dy2 < 0) {
                dy2 = 0;
            }
        }
        anchor.setRow2(row2);
        anchor.setDy2(dy2);
        return new Dimension((int)Math.round(scaledWidth * 9525.0), (int)Math.round(scaledHeight * 9525.0));
    }
    
    public static Dimension getDimensionFromAnchor(final Picture picture) {
        final ClientAnchor anchor = picture.getClientAnchor();
        final boolean isHSSF = anchor instanceof HSSFClientAnchor;
        final Sheet sheet = picture.getSheet();
        double w = 0.0;
        int col2 = anchor.getCol1();
        w = sheet.getColumnWidthInPixels(col2++);
        if (isHSSF) {
            w *= 1.0 - anchor.getDx1() / 1024.0;
        }
        else {
            w -= anchor.getDx1() / 9525.0;
        }
        while (col2 < anchor.getCol2()) {
            w += sheet.getColumnWidthInPixels(col2++);
        }
        if (isHSSF) {
            w += anchor.getDx2() / 1024.0 * sheet.getColumnWidthInPixels(col2);
        }
        else {
            w += anchor.getDx2() / 9525.0;
        }
        double h = 0.0;
        int row2 = anchor.getRow1();
        h = getRowHeightInPixels(sheet, row2++);
        if (isHSSF) {
            h *= 1.0 - anchor.getDy1() / 256.0;
        }
        else {
            h -= anchor.getDy1() / 9525.0;
        }
        while (row2 < anchor.getRow2()) {
            h += getRowHeightInPixels(sheet, row2++);
        }
        if (isHSSF) {
            h += getRowHeightInPixels(sheet, row2) * anchor.getDy2() / 256.0;
        }
        else {
            h += anchor.getDy2() / 9525.0;
        }
        w *= 9525.0;
        h *= 9525.0;
        return new Dimension((int)Math.rint(w), (int)Math.rint(h));
    }
    
    public static double getRowHeightInPixels(final Sheet sheet, final int rowNum) {
        final Row r = sheet.getRow(rowNum);
        final double points = (r == null) ? sheet.getDefaultRowHeightInPoints() : ((double)r.getHeightInPoints());
        return Units.toEMU(points) / 9525.0;
    }
    
    static {
        logger = POILogFactory.getLogger(ImageUtils.class);
    }
}
