package sun.swing;

import java.awt.Image;
import javax.swing.plaf.UIResource;
import javax.swing.ImageIcon;

public class ImageIconUIResource extends ImageIcon implements UIResource
{
    public ImageIconUIResource(final byte[] array) {
        super(array);
    }
    
    public ImageIconUIResource(final Image image) {
        super(image);
    }
}
