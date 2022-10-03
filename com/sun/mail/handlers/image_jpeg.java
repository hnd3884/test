package com.sun.mail.handlers;

import java.awt.Image;
import javax.activation.ActivationDataFlavor;

public class image_jpeg extends image_gif
{
    private static ActivationDataFlavor[] myDF;
    
    @Override
    protected ActivationDataFlavor[] getDataFlavors() {
        return image_jpeg.myDF;
    }
    
    static {
        image_jpeg.myDF = new ActivationDataFlavor[] { new ActivationDataFlavor(Image.class, "image/jpeg", "JPEG Image") };
    }
}
