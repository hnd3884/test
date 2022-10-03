package com.me.mdm.framework.qr;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.ByteArrayOutputStream;
import com.google.zxing.BarcodeFormat;
import java.io.OutputStream;
import java.awt.Color;

public class QRCodeGenerator
{
    private static final Color TRANSPARENT_COLOR;
    private static final int DEFAULT_TRIM_SIZE = 15;
    int width;
    int height;
    String imageFormat;
    boolean isFrameNeeded;
    Color bgColour;
    
    public QRCodeGenerator() {
        this.bgColour = QRCodeGenerator.TRANSPARENT_COLOR;
        this.width = 300;
        this.height = 300;
        this.imageFormat = "png";
        this.isFrameNeeded = true;
    }
    
    public QRCodeGenerator(final Color bgColour) {
        this.bgColour = QRCodeGenerator.TRANSPARENT_COLOR;
        this.width = 300;
        this.height = 300;
        this.imageFormat = "png";
        this.isFrameNeeded = true;
        this.bgColour = bgColour;
    }
    
    public QRCodeGenerator(final int width, final int height, final String imageFormat, final boolean isFrameNeeded) {
        this.bgColour = QRCodeGenerator.TRANSPARENT_COLOR;
        this.width = width;
        this.height = height;
        this.imageFormat = imageFormat;
        this.isFrameNeeded = isFrameNeeded;
    }
    
    public QRCodeGenerator(final int width, final int height, final String imageFormat, final boolean isFrameNeeded, final Color bgColour) {
        this.bgColour = QRCodeGenerator.TRANSPARENT_COLOR;
        this.width = width;
        this.height = height;
        this.imageFormat = imageFormat;
        this.isFrameNeeded = isFrameNeeded;
        this.bgColour = bgColour;
    }
    
    public void createQRCode(final String data, final OutputStream out) throws Exception {
        this.createQRCode(data, BarcodeFormat.QR_CODE, out);
    }
    
    public void createQRCode(final String data, final String fileName) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.createQRCode(data, out);
        final byte[] fileBytes = out.toByteArray();
        ApiFactoryProvider.getFileAccessAPI().writeFile(fileName, fileBytes);
    }
    
    private void createQRCode(final String data, final BarcodeFormat barCodeFormat, final OutputStream out) throws WriterException {
        final QRCodeWriter qrWriter = new QRCodeWriter();
        final BitMatrix matrix = qrWriter.encode(data, barCodeFormat, this.width, this.height);
        this.writeImageToStream(matrix, out);
    }
    
    private void writeImageToStream(final BitMatrix matrix, final OutputStream out) {
        try {
            final BufferedImage image = this.getBufferedImage(matrix, this.bgColour.getRGB(), Color.BLACK.getRGB());
            if (!ImageIO.write(image, this.imageFormat, out)) {
                throw new IOException("Could not write an image of format " + this.imageFormat);
            }
        }
        catch (final IOException ex) {
            Logger.getLogger(QRCodeGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private BufferedImage getBufferedImage(final BitMatrix matrix, final int onColor, final int offColor) {
        final int trimmedWidth = this.width - 30 + 1;
        final int trimmedHeight = this.height - 30 + 1;
        final BufferedImage image = new BufferedImage(trimmedWidth, trimmedHeight, 2);
        for (int y = 15; y <= this.height - 15; ++y) {
            for (int x = 15; x <= this.width - 15; ++x) {
                if (this.isFrameNeeded && this.isFramePixel(x, y)) {
                    image.setRGB(x - 15, y - 15, offColor);
                }
                else {
                    image.setRGB(x - 15, y - 15, matrix.get(x, y) ? offColor : onColor);
                }
            }
        }
        return image;
    }
    
    private Boolean isFramePixel(final int x, final int y) {
        final int frameWidth = 5;
        final int frameLength = 25;
        final int rightLimit = this.width - 15;
        final int bottomLimit = this.height - 15;
        return (x < 15 + frameLength && y < 15 + frameWidth) || (x < 15 + frameWidth && y < 15 + frameLength) || (x > rightLimit - frameLength && y > bottomLimit - frameWidth) || (x > rightLimit - frameWidth && y > bottomLimit - frameLength) || (x > rightLimit - frameLength && y < 15 + frameWidth) || (x < 15 + frameWidth && y > bottomLimit - frameLength) || (x < 15 + frameLength && y > bottomLimit - frameWidth) || (y < 15 + frameLength && x > rightLimit - frameWidth);
    }
    
    static {
        TRANSPARENT_COLOR = new Color(255, 255, 255, 0);
    }
}
