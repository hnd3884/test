package sun.print;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;

class ImagePrinter implements Printable
{
    BufferedImage image;
    
    ImagePrinter(final InputStream inputStream) {
        try {
            this.image = ImageIO.read(inputStream);
        }
        catch (final Exception ex) {}
    }
    
    ImagePrinter(final URL url) {
        try {
            this.image = ImageIO.read(url);
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public int print(final Graphics graphics, final PageFormat pageFormat, final int n) {
        if (n > 0 || this.image == null) {
            return 1;
        }
        ((Graphics2D)graphics).translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        final int width = this.image.getWidth(null);
        final int height = this.image.getHeight(null);
        final int n2 = (int)pageFormat.getImageableWidth();
        final int n3 = (int)pageFormat.getImageableHeight();
        int n4 = width;
        int n5 = height;
        if (n4 > n2) {
            n5 *= (int)(n2 / (float)n4);
            n4 = n2;
        }
        if (n5 > n3) {
            n4 *= (int)(n3 / (float)n5);
            n5 = n3;
        }
        final int n6 = (n2 - n4) / 2;
        final int n7 = (n3 - n5) / 2;
        graphics.drawImage(this.image, n6, n7, n6 + n4, n7 + n5, 0, 0, width, height, null);
        return 0;
    }
}
