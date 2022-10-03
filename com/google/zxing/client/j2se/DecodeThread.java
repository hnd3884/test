package com.google.zxing.client.j2se;

import java.awt.image.RenderedImage;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.BitArray;
import com.google.zxing.Reader;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.LuminanceSource;
import java.awt.image.BufferedImage;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.common.HybridBinarizer;
import java.io.FileNotFoundException;
import javax.imageio.ImageIO;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.io.FileOutputStream;
import com.google.zxing.Result;
import java.net.URI;
import java.io.IOException;
import java.io.File;

final class DecodeThread extends Thread
{
    private int successful;
    private final Config config;
    private final Inputs inputs;
    
    DecodeThread(final Config config, final Inputs inputs) {
        this.config = config;
        this.inputs = inputs;
    }
    
    @Override
    public void run() {
        while (true) {
            final String input = this.inputs.getNextInput();
            if (input == null) {
                break;
            }
            final File inputFile = new File(input);
            if (inputFile.exists()) {
                try {
                    if (this.config.isMulti()) {
                        final Result[] results = this.decodeMulti(inputFile.toURI(), this.config.getHints());
                        if (results == null) {
                            continue;
                        }
                        ++this.successful;
                        if (!this.config.isDumpResults()) {
                            continue;
                        }
                        dumpResultMulti(inputFile, results);
                    }
                    else {
                        final Result result = this.decode(inputFile.toURI(), this.config.getHints());
                        if (result == null) {
                            continue;
                        }
                        ++this.successful;
                        if (!this.config.isDumpResults()) {
                            continue;
                        }
                        dumpResult(inputFile, result);
                    }
                }
                catch (final IOException e) {}
            }
            else {
                try {
                    if (this.decode(new URI(input), this.config.getHints()) == null) {
                        continue;
                    }
                    ++this.successful;
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    public int getSuccessful() {
        return this.successful;
    }
    
    private static void dumpResult(final File input, final Result result) throws IOException {
        String name = input.getCanonicalPath();
        final int pos = name.lastIndexOf(46);
        if (pos > 0) {
            name = name.substring(0, pos);
        }
        final File dump = new File(name + ".txt");
        writeStringToFile(result.getText(), dump);
    }
    
    private static void dumpResultMulti(final File input, final Result[] results) throws IOException {
        String name = input.getCanonicalPath();
        final int pos = name.lastIndexOf(46);
        if (pos > 0) {
            name = name.substring(0, pos);
        }
        final File dump = new File(name + ".txt");
        writeResultsToFile(results, dump);
    }
    
    private static void writeStringToFile(final String value, final File file) throws IOException {
        final Writer out = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF8"));
        try {
            out.write(value);
        }
        finally {
            out.close();
        }
    }
    
    private static void writeResultsToFile(final Result[] results, final File file) throws IOException {
        final String newline = System.getProperty("line.separator");
        final Writer out = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF8"));
        try {
            for (final Result result : results) {
                out.write(result.getText());
                out.write(newline);
            }
        }
        finally {
            out.close();
        }
    }
    
    private Result decode(final URI uri, final Map<DecodeHintType, ?> hints) throws IOException {
        BufferedImage image;
        try {
            image = ImageIO.read(uri.toURL());
        }
        catch (final IllegalArgumentException iae) {
            throw new FileNotFoundException("Resource not found: " + uri);
        }
        if (image == null) {
            System.err.println(uri.toString() + ": Could not load image");
            return null;
        }
        try {
            LuminanceSource source;
            if (this.config.getCrop() == null) {
                source = new BufferedImageLuminanceSource(image);
            }
            else {
                final int[] crop = this.config.getCrop();
                source = new BufferedImageLuminanceSource(image, crop[0], crop[1], crop[2], crop[3]);
            }
            final BinaryBitmap bitmap = new BinaryBitmap((Binarizer)new HybridBinarizer(source));
            if (this.config.isDumpBlackPoint()) {
                dumpBlackPoint(uri, image, bitmap);
            }
            final Result result = new MultiFormatReader().decode(bitmap, (Map)hints);
            if (this.config.isBrief()) {
                System.out.println(uri.toString() + ": Success");
            }
            else {
                final ParsedResult parsedResult = ResultParser.parseResult(result);
                System.out.println(uri.toString() + " (format: " + result.getBarcodeFormat() + ", type: " + parsedResult.getType() + "):\nRaw result:\n" + result.getText() + "\nParsed result:\n" + parsedResult.getDisplayResult());
                System.out.println("Found " + result.getResultPoints().length + " result points.");
                for (int i = 0; i < result.getResultPoints().length; ++i) {
                    final ResultPoint rp = result.getResultPoints()[i];
                    System.out.println("  Point " + i + ": (" + rp.getX() + ',' + rp.getY() + ')');
                }
            }
            return result;
        }
        catch (final NotFoundException nfe) {
            System.out.println(uri.toString() + ": No barcode found");
            return null;
        }
    }
    
    private Result[] decodeMulti(final URI uri, final Map<DecodeHintType, ?> hints) throws IOException {
        BufferedImage image;
        try {
            image = ImageIO.read(uri.toURL());
        }
        catch (final IllegalArgumentException iae) {
            throw new FileNotFoundException("Resource not found: " + uri);
        }
        if (image == null) {
            System.err.println(uri.toString() + ": Could not load image");
            return null;
        }
        try {
            LuminanceSource source;
            if (this.config.getCrop() == null) {
                source = new BufferedImageLuminanceSource(image);
            }
            else {
                final int[] crop = this.config.getCrop();
                source = new BufferedImageLuminanceSource(image, crop[0], crop[1], crop[2], crop[3]);
            }
            final BinaryBitmap bitmap = new BinaryBitmap((Binarizer)new HybridBinarizer(source));
            if (this.config.isDumpBlackPoint()) {
                dumpBlackPoint(uri, image, bitmap);
            }
            final MultiFormatReader multiFormatReader = new MultiFormatReader();
            final GenericMultipleBarcodeReader reader = new GenericMultipleBarcodeReader((Reader)multiFormatReader);
            final Result[] results = reader.decodeMultiple(bitmap, (Map)hints);
            if (this.config.isBrief()) {
                System.out.println(uri.toString() + ": Success");
            }
            else {
                for (final Result result : results) {
                    final ParsedResult parsedResult = ResultParser.parseResult(result);
                    System.out.println(uri.toString() + " (format: " + result.getBarcodeFormat() + ", type: " + parsedResult.getType() + "):\nRaw result:\n" + result.getText() + "\nParsed result:\n" + parsedResult.getDisplayResult());
                    System.out.println("Found " + result.getResultPoints().length + " result points.");
                    for (int i = 0; i < result.getResultPoints().length; ++i) {
                        final ResultPoint rp = result.getResultPoints()[i];
                        System.out.println("  Point " + i + ": (" + rp.getX() + ',' + rp.getY() + ')');
                    }
                }
            }
            return results;
        }
        catch (final NotFoundException nfe) {
            System.out.println(uri.toString() + ": No barcode found");
            return null;
        }
    }
    
    private static void dumpBlackPoint(final URI uri, final BufferedImage image, final BinaryBitmap bitmap) {
        final String inputName = uri.getPath();
        if (inputName.contains(".mono.png")) {
            return;
        }
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final int stride = width * 3;
        final int[] pixels = new int[stride * height];
        final int[] argb = new int[width];
        for (int y = 0; y < height; ++y) {
            image.getRGB(0, y, width, 1, argb, 0, width);
            System.arraycopy(argb, 0, pixels, y * stride, width);
        }
        BitArray row = new BitArray(width);
        for (int y2 = 0; y2 < height; ++y2) {
            try {
                row = bitmap.getBlackRow(y2, row);
            }
            catch (final NotFoundException nfe) {
                final int offset = y2 * stride + width;
                for (int x = 0; x < width; ++x) {
                    pixels[offset + x] = -65536;
                }
                continue;
            }
            final int offset2 = y2 * stride + width;
            for (int x2 = 0; x2 < width; ++x2) {
                if (row.get(x2)) {
                    pixels[offset2 + x2] = -16777216;
                }
                else {
                    pixels[offset2 + x2] = -1;
                }
            }
        }
        try {
            for (int y2 = 0; y2 < height; ++y2) {
                final BitMatrix matrix = bitmap.getBlackMatrix();
                final int offset = y2 * stride + width * 2;
                for (int x = 0; x < width; ++x) {
                    if (matrix.get(x, y2)) {
                        pixels[offset + x] = -16777216;
                    }
                    else {
                        pixels[offset + x] = -1;
                    }
                }
            }
        }
        catch (final NotFoundException ex) {}
        writeResultImage(stride, height, pixels, uri, inputName, ".mono.png");
    }
    
    private static void writeResultImage(final int stride, final int height, final int[] pixels, final URI uri, final String inputName, final String suffix) {
        final BufferedImage result = new BufferedImage(stride, height, 2);
        result.setRGB(0, 0, stride, height, pixels, 0, stride);
        String resultName = inputName;
        if ("http".equals(uri.getScheme())) {
            final int pos = resultName.lastIndexOf(47);
            if (pos > 0) {
                resultName = '.' + resultName.substring(pos);
            }
        }
        final int pos = resultName.lastIndexOf(46);
        if (pos > 0) {
            resultName = resultName.substring(0, pos);
        }
        resultName += suffix;
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(resultName);
            if (!ImageIO.write(result, "png", outStream)) {
                System.err.println("Could not encode an image to " + resultName);
            }
        }
        catch (final FileNotFoundException e) {
            System.err.println("Could not create " + resultName);
        }
        catch (final IOException e2) {
            System.err.println("Could not write to " + resultName);
        }
        finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            }
            catch (final IOException ex) {}
        }
    }
}
