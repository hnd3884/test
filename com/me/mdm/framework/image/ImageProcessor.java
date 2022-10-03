package com.me.mdm.framework.image;

import java.awt.Color;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import com.dd.plist.NSData;
import java.awt.Graphics;
import org.json.JSONObject;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.util.logging.Level;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class ImageProcessor
{
    private static Logger logger;
    protected static final String IMAGE_SCALE_VALUE = "ScaleValue";
    protected static final String IMAGE_CROP_AXIS = "CropAxis";
    
    public BufferedImage ScaleImage(final BufferedImage inputImage, final double deviceWidth, final double deviceHeight) {
        BufferedImage finalImage = null;
        try {
            ImageProcessor.logger.log(Level.FINE, "Scaling the image to the device width:{0} and device height:{1}", new Object[] { deviceWidth, deviceHeight });
            int startingX = 0;
            int startingY = 0;
            final double imageWidth = inputImage.getWidth();
            final double imageHeight = inputImage.getHeight();
            final JSONObject imageObject = this.computeImageScale(imageWidth, imageHeight, deviceWidth, deviceHeight);
            final double scaleValue = imageObject.getDouble("ScaleValue");
            final double cropAxis = imageObject.getDouble("CropAxis");
            final double scaledWidth = Math.ceil(scaleValue * imageWidth);
            final double scaledHeight = Math.ceil(scaleValue * imageHeight);
            final BufferedImage image = new BufferedImage((int)scaledWidth, (int)scaledHeight, 1);
            final Graphics imageGraphics = image.getGraphics();
            imageGraphics.drawImage(inputImage, 0, 0, (int)scaledWidth, (int)scaledHeight, null);
            imageGraphics.dispose();
            ImageProcessor.logger.log(Level.FINE, "Crop Axis {0} and scale value {1}", new Object[] { cropAxis, scaleValue });
            if (cropAxis == 1.0) {
                final double extraWidth = Math.abs(scaledWidth - deviceWidth);
                startingX += (int)(extraWidth / 2.0);
            }
            else if (cropAxis == 2.0) {
                final double extraHeight = Math.abs(scaledHeight - deviceHeight);
                startingY += (int)(extraHeight / 2.0);
            }
            finalImage = image.getSubimage(startingX, startingY, (int)deviceWidth, (int)deviceHeight);
            ImageProcessor.logger.log(Level.FINE, "Scaled the image");
        }
        catch (final Exception e) {
            ImageProcessor.logger.log(Level.SEVERE, "Exception while scaling the image", e);
        }
        return finalImage;
    }
    
    protected JSONObject computeImageScale(final double imageWidth, final double imageHeight, final double deviceWidth, final double deviceHeight) {
        final JSONObject finalObject = new JSONObject();
        try {
            ImageProcessor.logger.log(Level.FINE, "Computing the scale value");
            final double actualImageRatio = imageWidth / imageHeight;
            final double neededImageRatio = deviceWidth / deviceHeight;
            double scaleValue = 0.0;
            int cropAxis = 0;
            if (actualImageRatio > neededImageRatio) {
                ImageProcessor.logger.log(Level.FINE, "Width is greater so cropping the X axis");
                scaleValue = deviceHeight / imageHeight;
                cropAxis = 1;
            }
            else if (actualImageRatio < neededImageRatio) {
                ImageProcessor.logger.log(Level.FINE, "Height is greater so cropping the Y axis");
                scaleValue = deviceWidth / imageWidth;
                cropAxis = 2;
            }
            else if (actualImageRatio == neededImageRatio) {
                ImageProcessor.logger.log(Level.FINE, "Both are in equal ratio so cutting the both");
                scaleValue = deviceWidth / imageWidth;
            }
            finalObject.put("ScaleValue", scaleValue);
            finalObject.put("CropAxis", cropAxis);
        }
        catch (final Exception e) {
            ImageProcessor.logger.log(Level.SEVERE, "Exception while computing Image Scale", e);
        }
        return finalObject;
    }
    
    public BufferedImage ScaleImage(final NSData imageData, final double deviceWidth, final double deviceHeight) {
        ByteArrayInputStream imageInputStream = null;
        try {
            final byte[] imageByes = imageData.bytes();
            imageInputStream = new ByteArrayInputStream(imageByes);
            final BufferedImage inputImage = ImageIO.read(imageInputStream);
            return this.ScaleImage(inputImage, deviceWidth, deviceHeight);
        }
        catch (final Exception e) {
            ImageProcessor.logger.log(Level.SEVERE, "Exception while scaling the image", e);
            try {
                if (imageInputStream != null) {
                    imageInputStream.close();
                }
            }
            catch (final Exception e) {
                ImageProcessor.logger.log(Level.SEVERE, "Exception while closing the output stream", e);
            }
        }
        finally {
            try {
                if (imageInputStream != null) {
                    imageInputStream.close();
                }
            }
            catch (final Exception e2) {
                ImageProcessor.logger.log(Level.SEVERE, "Exception while closing the output stream", e2);
            }
        }
        return null;
    }
    
    public NSData getScaleImageData(final NSData imageData, final double deviceWidth, final double deviceHeight, final String finalImageFormat) {
        ByteArrayOutputStream imageOutput = null;
        NSData finalImageData = null;
        try {
            final BufferedImage finalImage = this.ScaleImage(imageData, deviceWidth, deviceHeight);
            imageOutput = new ByteArrayOutputStream();
            ImageIO.write(finalImage, finalImageFormat, imageOutput);
            final byte[] finalImageBytes = imageOutput.toByteArray();
            finalImageData = new NSData(finalImageBytes);
        }
        catch (final Exception e) {
            ImageProcessor.logger.log(Level.SEVERE, "Exception while scaling image data", e);
            try {
                if (imageOutput != null) {
                    imageOutput.close();
                }
            }
            catch (final Exception e) {
                ImageProcessor.logger.log(Level.SEVERE, "Exception while closing output stream", e);
            }
        }
        finally {
            try {
                if (imageOutput != null) {
                    imageOutput.close();
                }
            }
            catch (final Exception e2) {
                ImageProcessor.logger.log(Level.SEVERE, "Exception while closing output stream", e2);
            }
        }
        return finalImageData;
    }
    
    public BufferedImage drawCustomImage(final double deviceWidth, final double deviceHeight, final String colour) {
        BufferedImage image = null;
        try {
            ImageProcessor.logger.log(Level.FINE, "Drawing the custom image with image width:{0} and image height{1} and colour{2}", new Object[] { deviceWidth, deviceHeight, colour });
            image = new BufferedImage((int)deviceWidth, (int)deviceHeight, 1);
            final Graphics graphics = image.getGraphics();
            graphics.setColor(new Color(Integer.valueOf(colour.substring(1, 3), 16), Integer.valueOf(colour.substring(3, 5), 16), Integer.valueOf(colour.substring(5, 7), 16)));
            graphics.setPaintMode();
            graphics.fillRect(0, 0, (int)deviceWidth, (int)deviceHeight);
            graphics.dispose();
            ImageProcessor.logger.log(Level.FINE, "Finished the custom image");
        }
        catch (final Exception e) {
            ImageProcessor.logger.log(Level.SEVERE, "Exception in drawing custom image", e);
        }
        return image;
    }
    
    public NSData drawCustomImageData(final double deviceWidth, final double deviceHeight, final String colour) {
        return this.drawCustomImageData(deviceWidth, deviceHeight, colour, "jpg");
    }
    
    public NSData drawCustomImageData(final double deviceWidth, final double deviceHeight, final String colour, final String finalImageFormat) {
        BufferedImage image = null;
        ByteArrayOutputStream imageStream = null;
        NSData finalImageData = null;
        try {
            image = this.drawCustomImage(deviceWidth, deviceHeight, colour);
            imageStream = new ByteArrayOutputStream();
            ImageIO.write(image, finalImageFormat, imageStream);
            final byte[] bytes = imageStream.toByteArray();
            finalImageData = new NSData(bytes);
        }
        catch (final Exception e) {
            ImageProcessor.logger.log(Level.SEVERE, "Exception while drawing custom colour Data Image", e);
            try {
                if (imageStream != null) {
                    imageStream.close();
                }
            }
            catch (final Exception e) {
                ImageProcessor.logger.log(Level.SEVERE, "Exception while closing the output stream", e);
            }
        }
        finally {
            try {
                if (imageStream != null) {
                    imageStream.close();
                }
            }
            catch (final Exception e2) {
                ImageProcessor.logger.log(Level.SEVERE, "Exception while closing the output stream", e2);
            }
        }
        return finalImageData;
    }
    
    static {
        ImageProcessor.logger = Logger.getLogger("MDMConfigLogger");
    }
}
