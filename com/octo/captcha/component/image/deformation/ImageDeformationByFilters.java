package com.octo.captcha.component.image.deformation;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;
import com.octo.captcha.component.image.utils.ToolkitFactory;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;

public class ImageDeformationByFilters implements ImageDeformation
{
    private ImageFilter[] filters;
    
    public ImageDeformationByFilters(final ImageFilter[] filters) {
        this.filters = filters;
    }
    
    public BufferedImage deformImage(final BufferedImage bufferedImage) {
        if (this.filters != null && this.filters.length > 0) {
            final BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
            Image image = null;
            for (int i = 0; i < this.filters.length; ++i) {
                image = ToolkitFactory.getToolkit().createImage(new FilteredImageSource(bufferedImage.getSource(), this.filters[i]));
            }
            bufferedImage2.getGraphics().drawImage(image, 0, 0, null);
            bufferedImage2.getGraphics().dispose();
            return bufferedImage2;
        }
        return bufferedImage;
    }
}
