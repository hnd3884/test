package sun.awt.image;

import java.util.List;
import java.awt.Image;

public interface MultiResolutionImage
{
    Image getResolutionVariant(final int p0, final int p1);
    
    List<Image> getResolutionVariants();
}
