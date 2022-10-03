package com.sun.imageio.plugins.png;

import javax.imageio.ImageTypeSpecifier;
import java.util.Arrays;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class PNGMetadataFormat extends IIOMetadataFormatImpl
{
    private static IIOMetadataFormat instance;
    private static String VALUE_0;
    private static String VALUE_1;
    private static String VALUE_12;
    private static String VALUE_23;
    private static String VALUE_31;
    private static String VALUE_59;
    private static String VALUE_60;
    private static String VALUE_255;
    private static String VALUE_MAX_16;
    private static String VALUE_MAX_32;
    
    private PNGMetadataFormat() {
        super("javax_imageio_png_1.0", 2);
        this.addElement("IHDR", "javax_imageio_png_1.0", 0);
        this.addAttribute("IHDR", "width", 2, true, null, PNGMetadataFormat.VALUE_1, PNGMetadataFormat.VALUE_MAX_32, true, true);
        this.addAttribute("IHDR", "height", 2, true, null, PNGMetadataFormat.VALUE_1, PNGMetadataFormat.VALUE_MAX_32, true, true);
        this.addAttribute("IHDR", "bitDepth", 2, true, null, Arrays.asList(PNGMetadata.IHDR_bitDepths));
        this.addAttribute("IHDR", "colorType", 0, true, null, Arrays.asList("Grayscale", "RGB", "Palette", "GrayAlpha", "RGBAlpha"));
        this.addAttribute("IHDR", "compressionMethod", 0, true, null, Arrays.asList(PNGMetadata.IHDR_compressionMethodNames));
        this.addAttribute("IHDR", "filterMethod", 0, true, null, Arrays.asList(PNGMetadata.IHDR_filterMethodNames));
        this.addAttribute("IHDR", "interlaceMethod", 0, true, null, Arrays.asList(PNGMetadata.IHDR_interlaceMethodNames));
        this.addElement("PLTE", "javax_imageio_png_1.0", 1, 256);
        this.addElement("PLTEEntry", "PLTE", 0);
        this.addAttribute("PLTEEntry", "index", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("PLTEEntry", "red", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("PLTEEntry", "green", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("PLTEEntry", "blue", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addElement("bKGD", "javax_imageio_png_1.0", 3);
        this.addElement("bKGD_Grayscale", "bKGD", 0);
        this.addAttribute("bKGD_Grayscale", "gray", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addElement("bKGD_RGB", "bKGD", 0);
        this.addAttribute("bKGD_RGB", "red", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("bKGD_RGB", "green", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("bKGD_RGB", "blue", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addElement("bKGD_Palette", "bKGD", 0);
        this.addAttribute("bKGD_Palette", "index", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addElement("cHRM", "javax_imageio_png_1.0", 0);
        this.addAttribute("cHRM", "whitePointX", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("cHRM", "whitePointY", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("cHRM", "redX", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("cHRM", "redY", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("cHRM", "greenX", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("cHRM", "greenY", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("cHRM", "blueX", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("cHRM", "blueY", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addElement("gAMA", "javax_imageio_png_1.0", 0);
        this.addAttribute("gAMA", "value", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_32, true, true);
        this.addElement("hIST", "javax_imageio_png_1.0", 1, 256);
        this.addElement("hISTEntry", "hIST", 0);
        this.addAttribute("hISTEntry", "index", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("hISTEntry", "value", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addElement("iCCP", "javax_imageio_png_1.0", 0);
        this.addAttribute("iCCP", "profileName", 0, true, null);
        this.addAttribute("iCCP", "compressionMethod", 0, true, null, Arrays.asList(PNGMetadata.iCCP_compressionMethodNames));
        this.addObjectValue("iCCP", Byte.TYPE, 0, Integer.MAX_VALUE);
        this.addElement("iTXt", "javax_imageio_png_1.0", 1, Integer.MAX_VALUE);
        this.addElement("iTXtEntry", "iTXt", 0);
        this.addAttribute("iTXtEntry", "keyword", 0, true, null);
        this.addBooleanAttribute("iTXtEntry", "compressionFlag", false, false);
        this.addAttribute("iTXtEntry", "compressionMethod", 0, true, null);
        this.addAttribute("iTXtEntry", "languageTag", 0, true, null);
        this.addAttribute("iTXtEntry", "translatedKeyword", 0, true, null);
        this.addAttribute("iTXtEntry", "text", 0, true, null);
        this.addElement("pHYS", "javax_imageio_png_1.0", 0);
        this.addAttribute("pHYS", "pixelsPerUnitXAxis", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_32, true, true);
        this.addAttribute("pHYS", "pixelsPerUnitYAxis", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_32, true, true);
        this.addAttribute("pHYS", "unitSpecifier", 0, true, null, Arrays.asList(PNGMetadata.unitSpecifierNames));
        this.addElement("sBIT", "javax_imageio_png_1.0", 3);
        this.addElement("sBIT_Grayscale", "sBIT", 0);
        this.addAttribute("sBIT_Grayscale", "gray", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addElement("sBIT_GrayAlpha", "sBIT", 0);
        this.addAttribute("sBIT_GrayAlpha", "gray", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sBIT_GrayAlpha", "alpha", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addElement("sBIT_RGB", "sBIT", 0);
        this.addAttribute("sBIT_RGB", "red", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sBIT_RGB", "green", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sBIT_RGB", "blue", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addElement("sBIT_RGBAlpha", "sBIT", 0);
        this.addAttribute("sBIT_RGBAlpha", "red", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sBIT_RGBAlpha", "green", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sBIT_RGBAlpha", "blue", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sBIT_RGBAlpha", "alpha", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addElement("sBIT_Palette", "sBIT", 0);
        this.addAttribute("sBIT_Palette", "red", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sBIT_Palette", "green", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sBIT_Palette", "blue", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addElement("sPLT", "javax_imageio_png_1.0", 1, 256);
        this.addElement("sPLTEntry", "sPLT", 0);
        this.addAttribute("sPLTEntry", "index", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sPLTEntry", "red", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sPLTEntry", "green", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sPLTEntry", "blue", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("sPLTEntry", "alpha", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addElement("sRGB", "javax_imageio_png_1.0", 0);
        this.addAttribute("sRGB", "renderingIntent", 0, true, null, Arrays.asList(PNGMetadata.renderingIntentNames));
        this.addElement("tEXt", "javax_imageio_png_1.0", 1, Integer.MAX_VALUE);
        this.addElement("tEXtEntry", "tEXt", 0);
        this.addAttribute("tEXtEntry", "keyword", 0, true, null);
        this.addAttribute("tEXtEntry", "value", 0, true, null);
        this.addElement("tIME", "javax_imageio_png_1.0", 0);
        this.addAttribute("tIME", "year", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("tIME", "month", 2, true, null, PNGMetadataFormat.VALUE_1, PNGMetadataFormat.VALUE_12, true, true);
        this.addAttribute("tIME", "day", 2, true, null, PNGMetadataFormat.VALUE_1, PNGMetadataFormat.VALUE_31, true, true);
        this.addAttribute("tIME", "hour", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_23, true, true);
        this.addAttribute("tIME", "minute", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_59, true, true);
        this.addAttribute("tIME", "second", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_60, true, true);
        this.addElement("tRNS", "javax_imageio_png_1.0", 3);
        this.addElement("tRNS_Grayscale", "tRNS", 0);
        this.addAttribute("tRNS_Grayscale", "gray", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addElement("tRNS_RGB", "tRNS", 0);
        this.addAttribute("tRNS_RGB", "red", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("tRNS_RGB", "green", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addAttribute("tRNS_RGB", "blue", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_MAX_16, true, true);
        this.addElement("tRNS_Palette", "tRNS", 0);
        this.addAttribute("tRNS_Palette", "index", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addAttribute("tRNS_Palette", "alpha", 2, true, null, PNGMetadataFormat.VALUE_0, PNGMetadataFormat.VALUE_255, true, true);
        this.addElement("zTXt", "javax_imageio_png_1.0", 1, Integer.MAX_VALUE);
        this.addElement("zTXtEntry", "zTXt", 0);
        this.addAttribute("zTXtEntry", "keyword", 0, true, null);
        this.addAttribute("zTXtEntry", "compressionMethod", 0, true, null, Arrays.asList(PNGMetadata.zTXt_compressionMethodNames));
        this.addAttribute("zTXtEntry", "text", 0, true, null);
        this.addElement("UnknownChunks", "javax_imageio_png_1.0", 1, Integer.MAX_VALUE);
        this.addElement("UnknownChunk", "UnknownChunks", 0);
        this.addAttribute("UnknownChunk", "type", 0, true, null);
        this.addObjectValue("UnknownChunk", Byte.TYPE, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean canNodeAppear(final String s, final ImageTypeSpecifier imageTypeSpecifier) {
        return true;
    }
    
    public static synchronized IIOMetadataFormat getInstance() {
        if (PNGMetadataFormat.instance == null) {
            PNGMetadataFormat.instance = new PNGMetadataFormat();
        }
        return PNGMetadataFormat.instance;
    }
    
    static {
        PNGMetadataFormat.instance = null;
        PNGMetadataFormat.VALUE_0 = "0";
        PNGMetadataFormat.VALUE_1 = "1";
        PNGMetadataFormat.VALUE_12 = "12";
        PNGMetadataFormat.VALUE_23 = "23";
        PNGMetadataFormat.VALUE_31 = "31";
        PNGMetadataFormat.VALUE_59 = "59";
        PNGMetadataFormat.VALUE_60 = "60";
        PNGMetadataFormat.VALUE_255 = "255";
        PNGMetadataFormat.VALUE_MAX_16 = "65535";
        PNGMetadataFormat.VALUE_MAX_32 = "2147483647";
    }
}
