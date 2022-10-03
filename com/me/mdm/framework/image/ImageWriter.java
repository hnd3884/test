package com.me.mdm.framework.image;

import java.io.OutputStream;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import com.dd.plist.NSData;
import java.util.List;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class ImageWriter
{
    private static Logger logger;
    private Integer imageWidth;
    private Integer imageHeight;
    String fontName;
    int fontStyle;
    Integer heightOffset;
    String imageFormat;
    int fontSize;
    int fontSpacing;
    
    public ImageWriter() {
        this.heightOffset = 0;
        this.fontSize = 0;
        this.fontSpacing = 5;
        this.fontName = "TimesRoman";
        this.fontStyle = 0;
        this.imageFormat = "jpg";
    }
    
    public ImageWriter(final String fontName, final int fontStyle) {
        this.heightOffset = 0;
        this.fontSize = 0;
        this.fontSpacing = 5;
        this.fontName = fontName;
        this.fontStyle = fontStyle;
    }
    
    public void setImageFormat(final String imageFormat) {
        this.imageFormat = imageFormat;
    }
    
    public void setHeightOffset(final int offset) {
        this.heightOffset = offset;
    }
    
    public int getHeightOffset() {
        return this.heightOffset;
    }
    
    public void setFontSpacing(final int fontSpacing) {
        this.fontSpacing = fontSpacing;
    }
    
    public int getFontSpacing() {
        return this.fontSpacing;
    }
    
    public void drawTextWithPercentage(final BufferedImage image, final String text, final String textColour, final int fontPercentage) {
        final int deviceHeight = image.getHeight();
        final double fontSize = deviceHeight / 100 * fontPercentage;
        this.drawText(image, text, textColour, (int)fontSize);
    }
    
    public void drawText(final BufferedImage image, final String text, final String textColour, final int fontSize) {
        try {
            ImageWriter.logger.log(Level.FINE, "Writing the text to the image with text:{0}, textcolour: {1} and fontsize: {2}", new Object[] { text, textColour, fontSize });
            this.fontSize = fontSize;
            final Graphics imageGraphics = image.getGraphics();
            final Font textFont = new Font(this.fontName, this.fontStyle, fontSize);
            imageGraphics.setFont(textFont);
            imageGraphics.setColor(new Color(Integer.valueOf(textColour.substring(1, 3), 16), Integer.valueOf(textColour.substring(3, 5), 16), Integer.valueOf(textColour.substring(5, 7), 16)));
            this.imageWidth = image.getWidth();
            this.imageHeight = image.getHeight();
            final int textWidth = imageGraphics.getFontMetrics().stringWidth(text);
            final int availableSpace = this.imageWidth - textWidth;
            List<String> textList = new ArrayList<String>();
            if (availableSpace < 0) {
                textList = this.getTextListForImage(imageGraphics, text);
            }
            else {
                textList.add(text);
            }
            ImageWriter.logger.log(Level.FINE, "Final Text splited text:{0}", textList.toString());
            for (int i = 0; i < textList.size(); ++i) {
                final String splitedText = textList.get(i);
                final int spiltTextWidth = imageGraphics.getFontMetrics().stringWidth(splitedText);
                final int availableTextSpace = this.imageWidth - spiltTextWidth;
                final int widthOffsetPosition = Math.round((float)(availableTextSpace / 2));
                imageGraphics.drawString(splitedText, widthOffsetPosition, this.heightOffset);
                this.heightOffset += imageGraphics.getFontMetrics().getHeight() + this.fontSpacing;
            }
            ImageWriter.logger.log(Level.INFO, "Wrote the text to the image with height offset:{0}", this.heightOffset);
            imageGraphics.dispose();
        }
        catch (final Exception e) {
            ImageWriter.logger.log(Level.SEVERE, "Exception while drawing text in a image", e);
        }
    }
    
    public NSData drawText(final NSData data, final String text, final String textColour) {
        return this.drawTextWithPercentage(data, text, textColour, 5);
    }
    
    public NSData drawText(final NSData data, final String text, final String textColour, final int fontSize) {
        NSData finalImageData = null;
        ByteArrayInputStream imageStream = null;
        ByteArrayOutputStream imageOutput = null;
        try {
            final byte[] bytes = data.bytes();
            imageStream = new ByteArrayInputStream(bytes);
            final BufferedImage image = ImageIO.read(imageStream);
            this.drawText(image, text, textColour, fontSize);
            imageOutput = new ByteArrayOutputStream();
            ImageIO.write(image, this.imageFormat, imageOutput);
            final byte[] imageBytes = imageOutput.toByteArray();
            finalImageData = new NSData(imageBytes);
        }
        catch (final Exception e) {
            ImageWriter.logger.log(Level.SEVERE, "Exception while drawing the text in the image", e);
            try {
                if (imageStream != null) {
                    imageStream.close();
                }
            }
            catch (final Exception e) {
                ImageWriter.logger.log(Level.SEVERE, "Exception while closing the output stream", e);
            }
        }
        finally {
            try {
                if (imageStream != null) {
                    imageStream.close();
                }
            }
            catch (final Exception e2) {
                ImageWriter.logger.log(Level.SEVERE, "Exception while closing the output stream", e2);
            }
        }
        return finalImageData;
    }
    
    public NSData drawTextWithPercentage(final NSData data, final String text, final String textColour, final int fontPercentage) {
        NSData finalImageData = null;
        ByteArrayInputStream imageStream = null;
        ByteArrayOutputStream imageOutput = null;
        try {
            final byte[] bytes = data.bytes();
            imageStream = new ByteArrayInputStream(bytes);
            final BufferedImage image = ImageIO.read(imageStream);
            this.drawTextWithPercentage(image, text, textColour, fontPercentage);
            imageOutput = new ByteArrayOutputStream();
            ImageIO.write(image, this.imageFormat, imageOutput);
            final byte[] imageBytes = imageOutput.toByteArray();
            finalImageData = new NSData(imageBytes);
        }
        catch (final Exception e) {
            ImageWriter.logger.log(Level.SEVERE, "Exception while drawing text in a image with percentage", e);
            try {
                if (imageStream != null) {
                    imageStream.close();
                }
                if (imageOutput != null) {
                    imageOutput.close();
                }
            }
            catch (final Exception e) {
                ImageWriter.logger.log(Level.SEVERE, "Exception while closing the output stream");
            }
        }
        finally {
            try {
                if (imageStream != null) {
                    imageStream.close();
                }
                if (imageOutput != null) {
                    imageOutput.close();
                }
            }
            catch (final Exception e2) {
                ImageWriter.logger.log(Level.SEVERE, "Exception while closing the output stream");
            }
        }
        return finalImageData;
    }
    
    protected List getTextListForImage(final Graphics imageGraphics, String text) {
        ImageWriter.logger.log(Level.INFO, "Text is long so trying to optimize it. Text width{0} and image width{1}", new Object[] { imageGraphics.getFontMetrics().stringWidth(text), this.imageWidth });
        final int maxTextLength = this.getPerfectTextIndex(imageGraphics, text);
        final List splitedList = this.spiltText(text, " ", maxTextLength);
        if (splitedList.size() == 0) {
            ImageWriter.logger.log(Level.FINE, "Going to shrink the text since it is not available to fit the text");
            text = this.changeFontSize(imageGraphics, text, maxTextLength);
            splitedList.add(text);
        }
        return splitedList;
    }
    
    private String changeFontSize(final Graphics imageGraphics, String text, final int maxLength) {
        Integer tempFontSize = this.fontSize;
        boolean isFiled = false;
        while (tempFontSize >= 1 && tempFontSize * 100.0 / this.imageHeight >= 1.5) {
            final Font textFont = new Font(this.fontName, this.fontStyle, tempFontSize);
            imageGraphics.setFont(textFont);
            final Integer textImageWidth = imageGraphics.getFontMetrics().stringWidth(text) + 10;
            if (textImageWidth <= this.imageWidth) {
                isFiled = true;
                break;
            }
            --tempFontSize;
        }
        if (!isFiled) {
            final Font textFont = new Font(this.fontName, this.fontStyle, this.fontSize);
            imageGraphics.setFont(textFont);
            text = text.substring(0, maxLength - 2) + "..";
        }
        return text;
    }
    
    private int getPerfectTextIndex(final Graphics imageGraphics, final String text) {
        int mid = 0;
        final int start = 0;
        final int end = text.length() - 1;
        try {
            mid = (start + end) / 2;
            boolean changed = false;
            int type = 0;
            while (mid < end) {
                final String subString = text.substring(start, mid);
                final int subStringWidth = imageGraphics.getFontMetrics().stringWidth(subString) + 10;
                final int percentagePropotion = subString.length() * 100 / end;
                if (this.imageWidth == subStringWidth || (percentagePropotion >= 75 && subStringWidth < this.imageWidth) || changed) {
                    return mid;
                }
                if (this.imageWidth > subStringWidth) {
                    ++mid;
                    changed = (type == 2);
                    type = 1;
                }
                else {
                    if (this.imageWidth >= subStringWidth) {
                        continue;
                    }
                    --mid;
                    changed = (type == 1);
                    type = 2;
                }
            }
        }
        catch (final Exception e) {
            ImageWriter.logger.log(Level.SEVERE, "", e);
        }
        return (start + end) / 2;
    }
    
    private List<String> spiltText(final String text, final String sep, final int maxLength) {
        final List<String> textList = new ArrayList<String>();
        int start;
        int index;
        for (start = 0; start + maxLength < text.length(); start = index + sep.length()) {
            index = text.lastIndexOf(sep, start + maxLength);
            if (index < start) {
                return textList;
            }
            textList.add(text.substring(start, index));
        }
        textList.add(text.substring(start));
        return textList;
    }
    
    static {
        ImageWriter.logger = Logger.getLogger("MDMConfigLogger");
    }
}
