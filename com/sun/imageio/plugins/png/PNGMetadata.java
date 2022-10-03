package com.sun.imageio.plugins.png;

import java.util.StringTokenizer;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import java.util.Iterator;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import javax.imageio.ImageTypeSpecifier;
import java.util.ArrayList;
import javax.imageio.metadata.IIOMetadata;

public class PNGMetadata extends IIOMetadata implements Cloneable
{
    public static final String nativeMetadataFormatName = "javax_imageio_png_1.0";
    protected static final String nativeMetadataFormatClassName = "com.sun.imageio.plugins.png.PNGMetadataFormat";
    static final String[] IHDR_colorTypeNames;
    static final int[] IHDR_numChannels;
    static final String[] IHDR_bitDepths;
    static final String[] IHDR_compressionMethodNames;
    static final String[] IHDR_filterMethodNames;
    static final String[] IHDR_interlaceMethodNames;
    static final String[] iCCP_compressionMethodNames;
    static final String[] zTXt_compressionMethodNames;
    public static final int PHYS_UNIT_UNKNOWN = 0;
    public static final int PHYS_UNIT_METER = 1;
    static final String[] unitSpecifierNames;
    static final String[] renderingIntentNames;
    static final String[] colorSpaceTypeNames;
    public boolean IHDR_present;
    public int IHDR_width;
    public int IHDR_height;
    public int IHDR_bitDepth;
    public int IHDR_colorType;
    public int IHDR_compressionMethod;
    public int IHDR_filterMethod;
    public int IHDR_interlaceMethod;
    public boolean PLTE_present;
    public byte[] PLTE_red;
    public byte[] PLTE_green;
    public byte[] PLTE_blue;
    public int[] PLTE_order;
    public boolean bKGD_present;
    public int bKGD_colorType;
    public int bKGD_index;
    public int bKGD_gray;
    public int bKGD_red;
    public int bKGD_green;
    public int bKGD_blue;
    public boolean cHRM_present;
    public int cHRM_whitePointX;
    public int cHRM_whitePointY;
    public int cHRM_redX;
    public int cHRM_redY;
    public int cHRM_greenX;
    public int cHRM_greenY;
    public int cHRM_blueX;
    public int cHRM_blueY;
    public boolean gAMA_present;
    public int gAMA_gamma;
    public boolean hIST_present;
    public char[] hIST_histogram;
    public boolean iCCP_present;
    public String iCCP_profileName;
    public int iCCP_compressionMethod;
    public byte[] iCCP_compressedProfile;
    public ArrayList<String> iTXt_keyword;
    public ArrayList<Boolean> iTXt_compressionFlag;
    public ArrayList<Integer> iTXt_compressionMethod;
    public ArrayList<String> iTXt_languageTag;
    public ArrayList<String> iTXt_translatedKeyword;
    public ArrayList<String> iTXt_text;
    public boolean pHYs_present;
    public int pHYs_pixelsPerUnitXAxis;
    public int pHYs_pixelsPerUnitYAxis;
    public int pHYs_unitSpecifier;
    public boolean sBIT_present;
    public int sBIT_colorType;
    public int sBIT_grayBits;
    public int sBIT_redBits;
    public int sBIT_greenBits;
    public int sBIT_blueBits;
    public int sBIT_alphaBits;
    public boolean sPLT_present;
    public String sPLT_paletteName;
    public int sPLT_sampleDepth;
    public int[] sPLT_red;
    public int[] sPLT_green;
    public int[] sPLT_blue;
    public int[] sPLT_alpha;
    public int[] sPLT_frequency;
    public boolean sRGB_present;
    public int sRGB_renderingIntent;
    public ArrayList<String> tEXt_keyword;
    public ArrayList<String> tEXt_text;
    public boolean tIME_present;
    public int tIME_year;
    public int tIME_month;
    public int tIME_day;
    public int tIME_hour;
    public int tIME_minute;
    public int tIME_second;
    public boolean tRNS_present;
    public int tRNS_colorType;
    public byte[] tRNS_alpha;
    public int tRNS_gray;
    public int tRNS_red;
    public int tRNS_green;
    public int tRNS_blue;
    public ArrayList<String> zTXt_keyword;
    public ArrayList<Integer> zTXt_compressionMethod;
    public ArrayList<String> zTXt_text;
    public ArrayList<String> unknownChunkType;
    public ArrayList<byte[]> unknownChunkData;
    
    public PNGMetadata() {
        super(true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", null, null);
        this.PLTE_order = null;
        this.iTXt_keyword = new ArrayList<String>();
        this.iTXt_compressionFlag = new ArrayList<Boolean>();
        this.iTXt_compressionMethod = new ArrayList<Integer>();
        this.iTXt_languageTag = new ArrayList<String>();
        this.iTXt_translatedKeyword = new ArrayList<String>();
        this.iTXt_text = new ArrayList<String>();
        this.tEXt_keyword = new ArrayList<String>();
        this.tEXt_text = new ArrayList<String>();
        this.zTXt_keyword = new ArrayList<String>();
        this.zTXt_compressionMethod = new ArrayList<Integer>();
        this.zTXt_text = new ArrayList<String>();
        this.unknownChunkType = new ArrayList<String>();
        this.unknownChunkData = new ArrayList<byte[]>();
    }
    
    public PNGMetadata(final IIOMetadata iioMetadata) {
        this.PLTE_order = null;
        this.iTXt_keyword = new ArrayList<String>();
        this.iTXt_compressionFlag = new ArrayList<Boolean>();
        this.iTXt_compressionMethod = new ArrayList<Integer>();
        this.iTXt_languageTag = new ArrayList<String>();
        this.iTXt_translatedKeyword = new ArrayList<String>();
        this.iTXt_text = new ArrayList<String>();
        this.tEXt_keyword = new ArrayList<String>();
        this.tEXt_text = new ArrayList<String>();
        this.zTXt_keyword = new ArrayList<String>();
        this.zTXt_compressionMethod = new ArrayList<Integer>();
        this.zTXt_text = new ArrayList<String>();
        this.unknownChunkType = new ArrayList<String>();
        this.unknownChunkData = new ArrayList<byte[]>();
    }
    
    public void initialize(final ImageTypeSpecifier imageTypeSpecifier, final int n) {
        final ColorModel colorModel = imageTypeSpecifier.getColorModel();
        final int[] sampleSize = imageTypeSpecifier.getSampleModel().getSampleSize();
        int ihdr_bitDepth = sampleSize[0];
        for (int i = 1; i < sampleSize.length; ++i) {
            if (sampleSize[i] > ihdr_bitDepth) {
                ihdr_bitDepth = sampleSize[i];
            }
        }
        if (sampleSize.length > 1 && ihdr_bitDepth < 8) {
            ihdr_bitDepth = 8;
        }
        if (ihdr_bitDepth > 2 && ihdr_bitDepth < 4) {
            ihdr_bitDepth = 4;
        }
        else if (ihdr_bitDepth > 4 && ihdr_bitDepth < 8) {
            ihdr_bitDepth = 8;
        }
        else if (ihdr_bitDepth > 8 && ihdr_bitDepth < 16) {
            ihdr_bitDepth = 16;
        }
        else if (ihdr_bitDepth > 16) {
            throw new RuntimeException("bitDepth > 16!");
        }
        this.IHDR_bitDepth = ihdr_bitDepth;
        if (colorModel instanceof IndexColorModel) {
            final IndexColorModel indexColorModel = (IndexColorModel)colorModel;
            final int mapSize = indexColorModel.getMapSize();
            final byte[] array = new byte[mapSize];
            indexColorModel.getReds(array);
            final byte[] array2 = new byte[mapSize];
            indexColorModel.getGreens(array2);
            final byte[] array3 = new byte[mapSize];
            indexColorModel.getBlues(array3);
            boolean b = false;
            if (!this.IHDR_present || this.IHDR_colorType != 3) {
                b = true;
                final int n2 = 255 / ((1 << this.IHDR_bitDepth) - 1);
                for (int j = 0; j < mapSize; ++j) {
                    final byte b2 = array[j];
                    if (b2 != (byte)(j * n2) || b2 != array2[j] || b2 != array3[j]) {
                        b = false;
                        break;
                    }
                }
            }
            final boolean hasAlpha = colorModel.hasAlpha();
            byte[] array4 = null;
            if (hasAlpha) {
                array4 = new byte[mapSize];
                indexColorModel.getAlphas(array4);
            }
            if (b && hasAlpha && (ihdr_bitDepth == 8 || ihdr_bitDepth == 16)) {
                this.IHDR_colorType = 4;
            }
            else if (b && !hasAlpha) {
                this.IHDR_colorType = 0;
            }
            else {
                this.IHDR_colorType = 3;
                this.PLTE_present = true;
                this.PLTE_order = null;
                this.PLTE_red = array.clone();
                this.PLTE_green = array2.clone();
                this.PLTE_blue = array3.clone();
                if (hasAlpha) {
                    this.tRNS_present = true;
                    this.tRNS_colorType = 3;
                    this.PLTE_order = new int[array4.length];
                    final byte[] array5 = new byte[array4.length];
                    int n3 = 0;
                    for (int k = 0; k < array4.length; ++k) {
                        if (array4[k] != -1) {
                            array5[this.PLTE_order[k] = n3] = array4[k];
                            ++n3;
                        }
                    }
                    final int n4 = n3;
                    for (int l = 0; l < array4.length; ++l) {
                        if (array4[l] == -1) {
                            this.PLTE_order[l] = n3++;
                        }
                    }
                    final byte[] plte_red = this.PLTE_red;
                    final byte[] plte_green = this.PLTE_green;
                    final byte[] plte_blue = this.PLTE_blue;
                    final int length = plte_red.length;
                    this.PLTE_red = new byte[length];
                    this.PLTE_green = new byte[length];
                    this.PLTE_blue = new byte[length];
                    for (int n5 = 0; n5 < length; ++n5) {
                        this.PLTE_red[this.PLTE_order[n5]] = plte_red[n5];
                        this.PLTE_green[this.PLTE_order[n5]] = plte_green[n5];
                        this.PLTE_blue[this.PLTE_order[n5]] = plte_blue[n5];
                    }
                    System.arraycopy(array5, 0, this.tRNS_alpha = new byte[n4], 0, n4);
                }
            }
        }
        else if (n == 1) {
            this.IHDR_colorType = 0;
        }
        else if (n == 2) {
            this.IHDR_colorType = 4;
        }
        else if (n == 3) {
            this.IHDR_colorType = 2;
        }
        else {
            if (n != 4) {
                throw new RuntimeException("Number of bands not 1-4!");
            }
            this.IHDR_colorType = 6;
        }
        this.IHDR_present = true;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    private ArrayList<byte[]> cloneBytesArrayList(final ArrayList<byte[]> list) {
        if (list == null) {
            return null;
        }
        final ArrayList list2 = new ArrayList(list.size());
        for (final byte[] array : list) {
            list2.add((array == null) ? null : ((byte[])array.clone()));
        }
        return list2;
    }
    
    public Object clone() {
        PNGMetadata pngMetadata;
        try {
            pngMetadata = (PNGMetadata)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
        pngMetadata.unknownChunkData = this.cloneBytesArrayList(this.unknownChunkData);
        return pngMetadata;
    }
    
    @Override
    public Node getAsTree(final String s) {
        if (s.equals("javax_imageio_png_1.0")) {
            return this.getNativeTree();
        }
        if (s.equals("javax_imageio_1.0")) {
            return this.getStandardTree();
        }
        throw new IllegalArgumentException("Not a recognized format!");
    }
    
    private Node getNativeTree() {
        IIOMetadataNode iioMetadataNode = null;
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("javax_imageio_png_1.0");
        if (this.IHDR_present) {
            final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("IHDR");
            iioMetadataNode3.setAttribute("width", Integer.toString(this.IHDR_width));
            iioMetadataNode3.setAttribute("height", Integer.toString(this.IHDR_height));
            iioMetadataNode3.setAttribute("bitDepth", Integer.toString(this.IHDR_bitDepth));
            iioMetadataNode3.setAttribute("colorType", PNGMetadata.IHDR_colorTypeNames[this.IHDR_colorType]);
            iioMetadataNode3.setAttribute("compressionMethod", PNGMetadata.IHDR_compressionMethodNames[this.IHDR_compressionMethod]);
            iioMetadataNode3.setAttribute("filterMethod", PNGMetadata.IHDR_filterMethodNames[this.IHDR_filterMethod]);
            iioMetadataNode3.setAttribute("interlaceMethod", PNGMetadata.IHDR_interlaceMethodNames[this.IHDR_interlaceMethod]);
            iioMetadataNode2.appendChild(iioMetadataNode3);
        }
        if (this.PLTE_present) {
            final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("PLTE");
            for (int length = this.PLTE_red.length, i = 0; i < length; ++i) {
                final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("PLTEEntry");
                iioMetadataNode5.setAttribute("index", Integer.toString(i));
                iioMetadataNode5.setAttribute("red", Integer.toString(this.PLTE_red[i] & 0xFF));
                iioMetadataNode5.setAttribute("green", Integer.toString(this.PLTE_green[i] & 0xFF));
                iioMetadataNode5.setAttribute("blue", Integer.toString(this.PLTE_blue[i] & 0xFF));
                iioMetadataNode4.appendChild(iioMetadataNode5);
            }
            iioMetadataNode2.appendChild(iioMetadataNode4);
        }
        if (this.bKGD_present) {
            final IIOMetadataNode iioMetadataNode6 = new IIOMetadataNode("bKGD");
            if (this.bKGD_colorType == 3) {
                iioMetadataNode = new IIOMetadataNode("bKGD_Palette");
                iioMetadataNode.setAttribute("index", Integer.toString(this.bKGD_index));
            }
            else if (this.bKGD_colorType == 0) {
                iioMetadataNode = new IIOMetadataNode("bKGD_Grayscale");
                iioMetadataNode.setAttribute("gray", Integer.toString(this.bKGD_gray));
            }
            else if (this.bKGD_colorType == 2) {
                iioMetadataNode = new IIOMetadataNode("bKGD_RGB");
                iioMetadataNode.setAttribute("red", Integer.toString(this.bKGD_red));
                iioMetadataNode.setAttribute("green", Integer.toString(this.bKGD_green));
                iioMetadataNode.setAttribute("blue", Integer.toString(this.bKGD_blue));
            }
            iioMetadataNode6.appendChild(iioMetadataNode);
            iioMetadataNode2.appendChild(iioMetadataNode6);
        }
        if (this.cHRM_present) {
            final IIOMetadataNode iioMetadataNode7 = new IIOMetadataNode("cHRM");
            iioMetadataNode7.setAttribute("whitePointX", Integer.toString(this.cHRM_whitePointX));
            iioMetadataNode7.setAttribute("whitePointY", Integer.toString(this.cHRM_whitePointY));
            iioMetadataNode7.setAttribute("redX", Integer.toString(this.cHRM_redX));
            iioMetadataNode7.setAttribute("redY", Integer.toString(this.cHRM_redY));
            iioMetadataNode7.setAttribute("greenX", Integer.toString(this.cHRM_greenX));
            iioMetadataNode7.setAttribute("greenY", Integer.toString(this.cHRM_greenY));
            iioMetadataNode7.setAttribute("blueX", Integer.toString(this.cHRM_blueX));
            iioMetadataNode7.setAttribute("blueY", Integer.toString(this.cHRM_blueY));
            iioMetadataNode2.appendChild(iioMetadataNode7);
        }
        if (this.gAMA_present) {
            final IIOMetadataNode iioMetadataNode8 = new IIOMetadataNode("gAMA");
            iioMetadataNode8.setAttribute("value", Integer.toString(this.gAMA_gamma));
            iioMetadataNode2.appendChild(iioMetadataNode8);
        }
        if (this.hIST_present) {
            final IIOMetadataNode iioMetadataNode9 = new IIOMetadataNode("hIST");
            for (int j = 0; j < this.hIST_histogram.length; ++j) {
                final IIOMetadataNode iioMetadataNode10 = new IIOMetadataNode("hISTEntry");
                iioMetadataNode10.setAttribute("index", Integer.toString(j));
                iioMetadataNode10.setAttribute("value", Integer.toString(this.hIST_histogram[j]));
                iioMetadataNode9.appendChild(iioMetadataNode10);
            }
            iioMetadataNode2.appendChild(iioMetadataNode9);
        }
        if (this.iCCP_present) {
            final IIOMetadataNode iioMetadataNode11 = new IIOMetadataNode("iCCP");
            iioMetadataNode11.setAttribute("profileName", this.iCCP_profileName);
            iioMetadataNode11.setAttribute("compressionMethod", PNGMetadata.iCCP_compressionMethodNames[this.iCCP_compressionMethod]);
            byte[] userObject = this.iCCP_compressedProfile;
            if (userObject != null) {
                userObject = userObject.clone();
            }
            iioMetadataNode11.setUserObject(userObject);
            iioMetadataNode2.appendChild(iioMetadataNode11);
        }
        if (this.iTXt_keyword.size() > 0) {
            final IIOMetadataNode iioMetadataNode12 = new IIOMetadataNode("iTXt");
            for (int k = 0; k < this.iTXt_keyword.size(); ++k) {
                final IIOMetadataNode iioMetadataNode13 = new IIOMetadataNode("iTXtEntry");
                iioMetadataNode13.setAttribute("keyword", this.iTXt_keyword.get(k));
                iioMetadataNode13.setAttribute("compressionFlag", ((boolean)this.iTXt_compressionFlag.get(k)) ? "TRUE" : "FALSE");
                iioMetadataNode13.setAttribute("compressionMethod", this.iTXt_compressionMethod.get(k).toString());
                iioMetadataNode13.setAttribute("languageTag", this.iTXt_languageTag.get(k));
                iioMetadataNode13.setAttribute("translatedKeyword", this.iTXt_translatedKeyword.get(k));
                iioMetadataNode13.setAttribute("text", this.iTXt_text.get(k));
                iioMetadataNode12.appendChild(iioMetadataNode13);
            }
            iioMetadataNode2.appendChild(iioMetadataNode12);
        }
        if (this.pHYs_present) {
            final IIOMetadataNode iioMetadataNode14 = new IIOMetadataNode("pHYs");
            iioMetadataNode14.setAttribute("pixelsPerUnitXAxis", Integer.toString(this.pHYs_pixelsPerUnitXAxis));
            iioMetadataNode14.setAttribute("pixelsPerUnitYAxis", Integer.toString(this.pHYs_pixelsPerUnitYAxis));
            iioMetadataNode14.setAttribute("unitSpecifier", PNGMetadata.unitSpecifierNames[this.pHYs_unitSpecifier]);
            iioMetadataNode2.appendChild(iioMetadataNode14);
        }
        if (this.sBIT_present) {
            final IIOMetadataNode iioMetadataNode15 = new IIOMetadataNode("sBIT");
            if (this.sBIT_colorType == 0) {
                iioMetadataNode = new IIOMetadataNode("sBIT_Grayscale");
                iioMetadataNode.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
            }
            else if (this.sBIT_colorType == 4) {
                iioMetadataNode = new IIOMetadataNode("sBIT_GrayAlpha");
                iioMetadataNode.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
                iioMetadataNode.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
            }
            else if (this.sBIT_colorType == 2) {
                iioMetadataNode = new IIOMetadataNode("sBIT_RGB");
                iioMetadataNode.setAttribute("red", Integer.toString(this.sBIT_redBits));
                iioMetadataNode.setAttribute("green", Integer.toString(this.sBIT_greenBits));
                iioMetadataNode.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
            }
            else if (this.sBIT_colorType == 6) {
                iioMetadataNode = new IIOMetadataNode("sBIT_RGBAlpha");
                iioMetadataNode.setAttribute("red", Integer.toString(this.sBIT_redBits));
                iioMetadataNode.setAttribute("green", Integer.toString(this.sBIT_greenBits));
                iioMetadataNode.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
                iioMetadataNode.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
            }
            else if (this.sBIT_colorType == 3) {
                iioMetadataNode = new IIOMetadataNode("sBIT_Palette");
                iioMetadataNode.setAttribute("red", Integer.toString(this.sBIT_redBits));
                iioMetadataNode.setAttribute("green", Integer.toString(this.sBIT_greenBits));
                iioMetadataNode.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
            }
            iioMetadataNode15.appendChild(iioMetadataNode);
            iioMetadataNode2.appendChild(iioMetadataNode15);
        }
        if (this.sPLT_present) {
            final IIOMetadataNode iioMetadataNode16 = new IIOMetadataNode("sPLT");
            iioMetadataNode16.setAttribute("name", this.sPLT_paletteName);
            iioMetadataNode16.setAttribute("sampleDepth", Integer.toString(this.sPLT_sampleDepth));
            for (int length2 = this.sPLT_red.length, l = 0; l < length2; ++l) {
                final IIOMetadataNode iioMetadataNode17 = new IIOMetadataNode("sPLTEntry");
                iioMetadataNode17.setAttribute("index", Integer.toString(l));
                iioMetadataNode17.setAttribute("red", Integer.toString(this.sPLT_red[l]));
                iioMetadataNode17.setAttribute("green", Integer.toString(this.sPLT_green[l]));
                iioMetadataNode17.setAttribute("blue", Integer.toString(this.sPLT_blue[l]));
                iioMetadataNode17.setAttribute("alpha", Integer.toString(this.sPLT_alpha[l]));
                iioMetadataNode17.setAttribute("frequency", Integer.toString(this.sPLT_frequency[l]));
                iioMetadataNode16.appendChild(iioMetadataNode17);
            }
            iioMetadataNode2.appendChild(iioMetadataNode16);
        }
        if (this.sRGB_present) {
            final IIOMetadataNode iioMetadataNode18 = new IIOMetadataNode("sRGB");
            iioMetadataNode18.setAttribute("renderingIntent", PNGMetadata.renderingIntentNames[this.sRGB_renderingIntent]);
            iioMetadataNode2.appendChild(iioMetadataNode18);
        }
        if (this.tEXt_keyword.size() > 0) {
            final IIOMetadataNode iioMetadataNode19 = new IIOMetadataNode("tEXt");
            for (int n = 0; n < this.tEXt_keyword.size(); ++n) {
                final IIOMetadataNode iioMetadataNode20 = new IIOMetadataNode("tEXtEntry");
                iioMetadataNode20.setAttribute("keyword", this.tEXt_keyword.get(n));
                iioMetadataNode20.setAttribute("value", this.tEXt_text.get(n));
                iioMetadataNode19.appendChild(iioMetadataNode20);
            }
            iioMetadataNode2.appendChild(iioMetadataNode19);
        }
        if (this.tIME_present) {
            final IIOMetadataNode iioMetadataNode21 = new IIOMetadataNode("tIME");
            iioMetadataNode21.setAttribute("year", Integer.toString(this.tIME_year));
            iioMetadataNode21.setAttribute("month", Integer.toString(this.tIME_month));
            iioMetadataNode21.setAttribute("day", Integer.toString(this.tIME_day));
            iioMetadataNode21.setAttribute("hour", Integer.toString(this.tIME_hour));
            iioMetadataNode21.setAttribute("minute", Integer.toString(this.tIME_minute));
            iioMetadataNode21.setAttribute("second", Integer.toString(this.tIME_second));
            iioMetadataNode2.appendChild(iioMetadataNode21);
        }
        if (this.tRNS_present) {
            final IIOMetadataNode iioMetadataNode22 = new IIOMetadataNode("tRNS");
            if (this.tRNS_colorType == 3) {
                iioMetadataNode = new IIOMetadataNode("tRNS_Palette");
                for (int n2 = 0; n2 < this.tRNS_alpha.length; ++n2) {
                    final IIOMetadataNode iioMetadataNode23 = new IIOMetadataNode("tRNS_PaletteEntry");
                    iioMetadataNode23.setAttribute("index", Integer.toString(n2));
                    iioMetadataNode23.setAttribute("alpha", Integer.toString(this.tRNS_alpha[n2] & 0xFF));
                    iioMetadataNode.appendChild(iioMetadataNode23);
                }
            }
            else if (this.tRNS_colorType == 0) {
                iioMetadataNode = new IIOMetadataNode("tRNS_Grayscale");
                iioMetadataNode.setAttribute("gray", Integer.toString(this.tRNS_gray));
            }
            else if (this.tRNS_colorType == 2) {
                iioMetadataNode = new IIOMetadataNode("tRNS_RGB");
                iioMetadataNode.setAttribute("red", Integer.toString(this.tRNS_red));
                iioMetadataNode.setAttribute("green", Integer.toString(this.tRNS_green));
                iioMetadataNode.setAttribute("blue", Integer.toString(this.tRNS_blue));
            }
            iioMetadataNode22.appendChild(iioMetadataNode);
            iioMetadataNode2.appendChild(iioMetadataNode22);
        }
        if (this.zTXt_keyword.size() > 0) {
            final IIOMetadataNode iioMetadataNode24 = new IIOMetadataNode("zTXt");
            for (int n3 = 0; n3 < this.zTXt_keyword.size(); ++n3) {
                final IIOMetadataNode iioMetadataNode25 = new IIOMetadataNode("zTXtEntry");
                iioMetadataNode25.setAttribute("keyword", this.zTXt_keyword.get(n3));
                iioMetadataNode25.setAttribute("compressionMethod", PNGMetadata.zTXt_compressionMethodNames[this.zTXt_compressionMethod.get(n3)]);
                iioMetadataNode25.setAttribute("text", this.zTXt_text.get(n3));
                iioMetadataNode24.appendChild(iioMetadataNode25);
            }
            iioMetadataNode2.appendChild(iioMetadataNode24);
        }
        if (this.unknownChunkType.size() > 0) {
            final IIOMetadataNode iioMetadataNode26 = new IIOMetadataNode("UnknownChunks");
            for (int n4 = 0; n4 < this.unknownChunkType.size(); ++n4) {
                final IIOMetadataNode iioMetadataNode27 = new IIOMetadataNode("UnknownChunk");
                iioMetadataNode27.setAttribute("type", this.unknownChunkType.get(n4));
                iioMetadataNode27.setUserObject(this.unknownChunkData.get(n4));
                iioMetadataNode26.appendChild(iioMetadataNode27);
            }
            iioMetadataNode2.appendChild(iioMetadataNode26);
        }
        return iioMetadataNode2;
    }
    
    private int getNumChannels() {
        int n = PNGMetadata.IHDR_numChannels[this.IHDR_colorType];
        if (this.IHDR_colorType == 3 && this.tRNS_present && this.tRNS_colorType == this.IHDR_colorType) {
            n = 4;
        }
        return n;
    }
    
    public IIOMetadataNode getStandardChromaNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Chroma");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
        iioMetadataNode2.setAttribute("name", PNGMetadata.colorSpaceTypeNames[this.IHDR_colorType]);
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("NumChannels");
        iioMetadataNode3.setAttribute("value", Integer.toString(this.getNumChannels()));
        iioMetadataNode.appendChild(iioMetadataNode3);
        if (this.gAMA_present) {
            final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("Gamma");
            iioMetadataNode4.setAttribute("value", Float.toString(this.gAMA_gamma * 1.0E-5f));
            iioMetadataNode.appendChild(iioMetadataNode4);
        }
        final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("BlackIsZero");
        iioMetadataNode5.setAttribute("value", "TRUE");
        iioMetadataNode.appendChild(iioMetadataNode5);
        if (this.PLTE_present) {
            final boolean b = this.tRNS_present && this.tRNS_colorType == 3;
            final IIOMetadataNode iioMetadataNode6 = new IIOMetadataNode("Palette");
            for (int i = 0; i < this.PLTE_red.length; ++i) {
                final IIOMetadataNode iioMetadataNode7 = new IIOMetadataNode("PaletteEntry");
                iioMetadataNode7.setAttribute("index", Integer.toString(i));
                iioMetadataNode7.setAttribute("red", Integer.toString(this.PLTE_red[i] & 0xFF));
                iioMetadataNode7.setAttribute("green", Integer.toString(this.PLTE_green[i] & 0xFF));
                iioMetadataNode7.setAttribute("blue", Integer.toString(this.PLTE_blue[i] & 0xFF));
                if (b) {
                    iioMetadataNode7.setAttribute("alpha", Integer.toString((i < this.tRNS_alpha.length) ? (this.tRNS_alpha[i] & 0xFF) : 255));
                }
                iioMetadataNode6.appendChild(iioMetadataNode7);
            }
            iioMetadataNode.appendChild(iioMetadataNode6);
        }
        if (this.bKGD_present) {
            IIOMetadataNode iioMetadataNode8;
            if (this.bKGD_colorType == 3) {
                iioMetadataNode8 = new IIOMetadataNode("BackgroundIndex");
                iioMetadataNode8.setAttribute("value", Integer.toString(this.bKGD_index));
            }
            else {
                iioMetadataNode8 = new IIOMetadataNode("BackgroundColor");
                int n;
                int bkgd_red;
                int bkgd_green;
                if (this.bKGD_colorType == 0) {
                    bkgd_green = (bkgd_red = (n = this.bKGD_gray));
                }
                else {
                    bkgd_red = this.bKGD_red;
                    bkgd_green = this.bKGD_green;
                    n = this.bKGD_blue;
                }
                iioMetadataNode8.setAttribute("red", Integer.toString(bkgd_red));
                iioMetadataNode8.setAttribute("green", Integer.toString(bkgd_green));
                iioMetadataNode8.setAttribute("blue", Integer.toString(n));
            }
            iioMetadataNode.appendChild(iioMetadataNode8);
        }
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardCompressionNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Compression");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
        iioMetadataNode2.setAttribute("value", "deflate");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("Lossless");
        iioMetadataNode3.setAttribute("value", "TRUE");
        iioMetadataNode.appendChild(iioMetadataNode3);
        final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("NumProgressiveScans");
        iioMetadataNode4.setAttribute("value", (this.IHDR_interlaceMethod == 0) ? "1" : "7");
        iioMetadataNode.appendChild(iioMetadataNode4);
        return iioMetadataNode;
    }
    
    private String repeat(final String s, final int n) {
        if (n == 1) {
            return s;
        }
        final StringBuffer sb = new StringBuffer((s.length() + 1) * n - 1);
        sb.append(s);
        for (int i = 1; i < n; ++i) {
            sb.append(" ");
            sb.append(s);
        }
        return sb.toString();
    }
    
    public IIOMetadataNode getStandardDataNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Data");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("PlanarConfiguration");
        iioMetadataNode2.setAttribute("value", "PixelInterleaved");
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("SampleFormat");
        iioMetadataNode3.setAttribute("value", (this.IHDR_colorType == 3) ? "Index" : "UnsignedIntegral");
        iioMetadataNode.appendChild(iioMetadataNode3);
        final String string = Integer.toString(this.IHDR_bitDepth);
        final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("BitsPerSample");
        iioMetadataNode4.setAttribute("value", this.repeat(string, this.getNumChannels()));
        iioMetadataNode.appendChild(iioMetadataNode4);
        if (this.sBIT_present) {
            final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("SignificantBitsPerSample");
            String s;
            if (this.sBIT_colorType == 0 || this.sBIT_colorType == 4) {
                s = Integer.toString(this.sBIT_grayBits);
            }
            else {
                s = Integer.toString(this.sBIT_redBits) + " " + Integer.toString(this.sBIT_greenBits) + " " + Integer.toString(this.sBIT_blueBits);
            }
            if (this.sBIT_colorType == 4 || this.sBIT_colorType == 6) {
                s = s + " " + Integer.toString(this.sBIT_alphaBits);
            }
            iioMetadataNode5.setAttribute("value", s);
            iioMetadataNode.appendChild(iioMetadataNode5);
        }
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardDimensionNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Dimension");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
        iioMetadataNode2.setAttribute("value", Float.toString(this.pHYs_present ? (this.pHYs_pixelsPerUnitXAxis / (float)this.pHYs_pixelsPerUnitYAxis) : 1.0f));
        iioMetadataNode.appendChild(iioMetadataNode2);
        final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("ImageOrientation");
        iioMetadataNode3.setAttribute("value", "Normal");
        iioMetadataNode.appendChild(iioMetadataNode3);
        if (this.pHYs_present && this.pHYs_unitSpecifier == 1) {
            final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("HorizontalPixelSize");
            iioMetadataNode4.setAttribute("value", Float.toString(1000.0f / this.pHYs_pixelsPerUnitXAxis));
            iioMetadataNode.appendChild(iioMetadataNode4);
            final IIOMetadataNode iioMetadataNode5 = new IIOMetadataNode("VerticalPixelSize");
            iioMetadataNode5.setAttribute("value", Float.toString(1000.0f / this.pHYs_pixelsPerUnitYAxis));
            iioMetadataNode.appendChild(iioMetadataNode5);
        }
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardDocumentNode() {
        if (!this.tIME_present) {
            return null;
        }
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Document");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("ImageModificationTime");
        iioMetadataNode2.setAttribute("year", Integer.toString(this.tIME_year));
        iioMetadataNode2.setAttribute("month", Integer.toString(this.tIME_month));
        iioMetadataNode2.setAttribute("day", Integer.toString(this.tIME_day));
        iioMetadataNode2.setAttribute("hour", Integer.toString(this.tIME_hour));
        iioMetadataNode2.setAttribute("minute", Integer.toString(this.tIME_minute));
        iioMetadataNode2.setAttribute("second", Integer.toString(this.tIME_second));
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardTextNode() {
        if (this.tEXt_keyword.size() + this.iTXt_keyword.size() + this.zTXt_keyword.size() == 0) {
            return null;
        }
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Text");
        for (int i = 0; i < this.tEXt_keyword.size(); ++i) {
            final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("TextEntry");
            iioMetadataNode2.setAttribute("keyword", this.tEXt_keyword.get(i));
            iioMetadataNode2.setAttribute("value", this.tEXt_text.get(i));
            iioMetadataNode2.setAttribute("encoding", "ISO-8859-1");
            iioMetadataNode2.setAttribute("compression", "none");
            iioMetadataNode.appendChild(iioMetadataNode2);
        }
        for (int j = 0; j < this.iTXt_keyword.size(); ++j) {
            final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("TextEntry");
            iioMetadataNode3.setAttribute("keyword", this.iTXt_keyword.get(j));
            iioMetadataNode3.setAttribute("value", this.iTXt_text.get(j));
            iioMetadataNode3.setAttribute("language", this.iTXt_languageTag.get(j));
            if (this.iTXt_compressionFlag.get(j)) {
                iioMetadataNode3.setAttribute("compression", "zip");
            }
            else {
                iioMetadataNode3.setAttribute("compression", "none");
            }
            iioMetadataNode.appendChild(iioMetadataNode3);
        }
        for (int k = 0; k < this.zTXt_keyword.size(); ++k) {
            final IIOMetadataNode iioMetadataNode4 = new IIOMetadataNode("TextEntry");
            iioMetadataNode4.setAttribute("keyword", this.zTXt_keyword.get(k));
            iioMetadataNode4.setAttribute("value", this.zTXt_text.get(k));
            iioMetadataNode4.setAttribute("compression", "zip");
            iioMetadataNode.appendChild(iioMetadataNode4);
        }
        return iioMetadataNode;
    }
    
    public IIOMetadataNode getStandardTransparencyNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Transparency");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("Alpha");
        iioMetadataNode2.setAttribute("value", (this.IHDR_colorType == 6 || this.IHDR_colorType == 4 || (this.IHDR_colorType == 3 && this.tRNS_present && this.tRNS_colorType == this.IHDR_colorType && this.tRNS_alpha != null)) ? "nonpremultipled" : "none");
        iioMetadataNode.appendChild(iioMetadataNode2);
        if (this.tRNS_present) {
            final IIOMetadataNode iioMetadataNode3 = new IIOMetadataNode("TransparentColor");
            if (this.tRNS_colorType == 2) {
                iioMetadataNode3.setAttribute("value", Integer.toString(this.tRNS_red) + " " + Integer.toString(this.tRNS_green) + " " + Integer.toString(this.tRNS_blue));
            }
            else if (this.tRNS_colorType == 0) {
                iioMetadataNode3.setAttribute("value", Integer.toString(this.tRNS_gray));
            }
            iioMetadataNode.appendChild(iioMetadataNode3);
        }
        return iioMetadataNode;
    }
    
    private void fatal(final Node node, final String s) throws IIOInvalidTreeException {
        throw new IIOInvalidTreeException(s, node);
    }
    
    private String getStringAttribute(final Node node, final String s, final String s2, final boolean b) throws IIOInvalidTreeException {
        final Node namedItem = node.getAttributes().getNamedItem(s);
        if (namedItem == null) {
            if (!b) {
                return s2;
            }
            this.fatal(node, "Required attribute " + s + " not present!");
        }
        return namedItem.getNodeValue();
    }
    
    private int getIntAttribute(final Node node, final String s, final int n, final boolean b) throws IIOInvalidTreeException {
        final String stringAttribute = this.getStringAttribute(node, s, null, b);
        if (stringAttribute == null) {
            return n;
        }
        return Integer.parseInt(stringAttribute);
    }
    
    private float getFloatAttribute(final Node node, final String s, final float n, final boolean b) throws IIOInvalidTreeException {
        final String stringAttribute = this.getStringAttribute(node, s, null, b);
        if (stringAttribute == null) {
            return n;
        }
        return Float.parseFloat(stringAttribute);
    }
    
    private int getIntAttribute(final Node node, final String s) throws IIOInvalidTreeException {
        return this.getIntAttribute(node, s, -1, true);
    }
    
    private float getFloatAttribute(final Node node, final String s) throws IIOInvalidTreeException {
        return this.getFloatAttribute(node, s, -1.0f, true);
    }
    
    private boolean getBooleanAttribute(final Node node, final String s, final boolean b, final boolean b2) throws IIOInvalidTreeException {
        final Node namedItem = node.getAttributes().getNamedItem(s);
        if (namedItem == null) {
            if (!b2) {
                return b;
            }
            this.fatal(node, "Required attribute " + s + " not present!");
        }
        final String nodeValue = namedItem.getNodeValue();
        if (nodeValue.equals("TRUE") || nodeValue.equals("true")) {
            return true;
        }
        if (nodeValue.equals("FALSE") || nodeValue.equals("false")) {
            return false;
        }
        this.fatal(node, "Attribute " + s + " must be 'TRUE' or 'FALSE'!");
        return false;
    }
    
    private boolean getBooleanAttribute(final Node node, final String s) throws IIOInvalidTreeException {
        return this.getBooleanAttribute(node, s, false, true);
    }
    
    private int getEnumeratedAttribute(final Node node, final String s, final String[] array, final int n, final boolean b) throws IIOInvalidTreeException {
        final Node namedItem = node.getAttributes().getNamedItem(s);
        if (namedItem == null) {
            if (!b) {
                return n;
            }
            this.fatal(node, "Required attribute " + s + " not present!");
        }
        final String nodeValue = namedItem.getNodeValue();
        for (int i = 0; i < array.length; ++i) {
            if (nodeValue.equals(array[i])) {
                return i;
            }
        }
        this.fatal(node, "Illegal value for attribute " + s + "!");
        return -1;
    }
    
    private int getEnumeratedAttribute(final Node node, final String s, final String[] array) throws IIOInvalidTreeException {
        return this.getEnumeratedAttribute(node, s, array, -1, true);
    }
    
    private String getAttribute(final Node node, final String s, final String s2, final boolean b) throws IIOInvalidTreeException {
        final Node namedItem = node.getAttributes().getNamedItem(s);
        if (namedItem == null) {
            if (!b) {
                return s2;
            }
            this.fatal(node, "Required attribute " + s + " not present!");
        }
        return namedItem.getNodeValue();
    }
    
    private String getAttribute(final Node node, final String s) throws IIOInvalidTreeException {
        return this.getAttribute(node, s, null, true);
    }
    
    @Override
    public void mergeTree(final String s, final Node node) throws IIOInvalidTreeException {
        if (s.equals("javax_imageio_png_1.0")) {
            if (node == null) {
                throw new IllegalArgumentException("root == null!");
            }
            this.mergeNativeTree(node);
        }
        else {
            if (!s.equals("javax_imageio_1.0")) {
                throw new IllegalArgumentException("Not a recognized format!");
            }
            if (node == null) {
                throw new IllegalArgumentException("root == null!");
            }
            this.mergeStandardTree(node);
        }
    }
    
    private void mergeNativeTree(final Node node) throws IIOInvalidTreeException {
        if (!node.getNodeName().equals("javax_imageio_png_1.0")) {
            this.fatal(node, "Root must be javax_imageio_png_1.0");
        }
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            final String nodeName = node2.getNodeName();
            if (nodeName.equals("IHDR")) {
                this.IHDR_width = this.getIntAttribute(node2, "width");
                this.IHDR_height = this.getIntAttribute(node2, "height");
                this.IHDR_bitDepth = Integer.valueOf(PNGMetadata.IHDR_bitDepths[this.getEnumeratedAttribute(node2, "bitDepth", PNGMetadata.IHDR_bitDepths)]);
                this.IHDR_colorType = this.getEnumeratedAttribute(node2, "colorType", PNGMetadata.IHDR_colorTypeNames);
                this.IHDR_compressionMethod = this.getEnumeratedAttribute(node2, "compressionMethod", PNGMetadata.IHDR_compressionMethodNames);
                this.IHDR_filterMethod = this.getEnumeratedAttribute(node2, "filterMethod", PNGMetadata.IHDR_filterMethodNames);
                this.IHDR_interlaceMethod = this.getEnumeratedAttribute(node2, "interlaceMethod", PNGMetadata.IHDR_interlaceMethodNames);
                this.IHDR_present = true;
            }
            else if (nodeName.equals("PLTE")) {
                final byte[] array = new byte[256];
                final byte[] array2 = new byte[256];
                final byte[] array3 = new byte[256];
                int n = -1;
                Node node3 = node2.getFirstChild();
                if (node3 == null) {
                    this.fatal(node2, "Palette has no entries!");
                }
                while (node3 != null) {
                    if (!node3.getNodeName().equals("PLTEEntry")) {
                        this.fatal(node2, "Only a PLTEEntry may be a child of a PLTE!");
                    }
                    final int intAttribute = this.getIntAttribute(node3, "index");
                    if (intAttribute < 0 || intAttribute > 255) {
                        this.fatal(node2, "Bad value for PLTEEntry attribute index!");
                    }
                    if (intAttribute > n) {
                        n = intAttribute;
                    }
                    array[intAttribute] = (byte)this.getIntAttribute(node3, "red");
                    array2[intAttribute] = (byte)this.getIntAttribute(node3, "green");
                    array3[intAttribute] = (byte)this.getIntAttribute(node3, "blue");
                    node3 = node3.getNextSibling();
                }
                final int n2 = n + 1;
                this.PLTE_red = new byte[n2];
                this.PLTE_green = new byte[n2];
                this.PLTE_blue = new byte[n2];
                System.arraycopy(array, 0, this.PLTE_red, 0, n2);
                System.arraycopy(array2, 0, this.PLTE_green, 0, n2);
                System.arraycopy(array3, 0, this.PLTE_blue, 0, n2);
                this.PLTE_present = true;
            }
            else if (nodeName.equals("bKGD")) {
                this.bKGD_present = false;
                final Node firstChild = node2.getFirstChild();
                if (firstChild == null) {
                    this.fatal(node2, "bKGD node has no children!");
                }
                final String nodeName2 = firstChild.getNodeName();
                if (nodeName2.equals("bKGD_Palette")) {
                    this.bKGD_index = this.getIntAttribute(firstChild, "index");
                    this.bKGD_colorType = 3;
                }
                else if (nodeName2.equals("bKGD_Grayscale")) {
                    this.bKGD_gray = this.getIntAttribute(firstChild, "gray");
                    this.bKGD_colorType = 0;
                }
                else if (nodeName2.equals("bKGD_RGB")) {
                    this.bKGD_red = this.getIntAttribute(firstChild, "red");
                    this.bKGD_green = this.getIntAttribute(firstChild, "green");
                    this.bKGD_blue = this.getIntAttribute(firstChild, "blue");
                    this.bKGD_colorType = 2;
                }
                else {
                    this.fatal(node2, "Bad child of a bKGD node!");
                }
                if (firstChild.getNextSibling() != null) {
                    this.fatal(node2, "bKGD node has more than one child!");
                }
                this.bKGD_present = true;
            }
            else if (nodeName.equals("cHRM")) {
                this.cHRM_whitePointX = this.getIntAttribute(node2, "whitePointX");
                this.cHRM_whitePointY = this.getIntAttribute(node2, "whitePointY");
                this.cHRM_redX = this.getIntAttribute(node2, "redX");
                this.cHRM_redY = this.getIntAttribute(node2, "redY");
                this.cHRM_greenX = this.getIntAttribute(node2, "greenX");
                this.cHRM_greenY = this.getIntAttribute(node2, "greenY");
                this.cHRM_blueX = this.getIntAttribute(node2, "blueX");
                this.cHRM_blueY = this.getIntAttribute(node2, "blueY");
                this.cHRM_present = true;
            }
            else if (nodeName.equals("gAMA")) {
                this.gAMA_gamma = this.getIntAttribute(node2, "value");
                this.gAMA_present = true;
            }
            else if (nodeName.equals("hIST")) {
                final char[] array4 = new char[256];
                int n3 = -1;
                Node node4 = node2.getFirstChild();
                if (node4 == null) {
                    this.fatal(node2, "hIST node has no children!");
                }
                while (node4 != null) {
                    if (!node4.getNodeName().equals("hISTEntry")) {
                        this.fatal(node2, "Only a hISTEntry may be a child of a hIST!");
                    }
                    final int intAttribute2 = this.getIntAttribute(node4, "index");
                    if (intAttribute2 < 0 || intAttribute2 > 255) {
                        this.fatal(node2, "Bad value for histEntry attribute index!");
                    }
                    if (intAttribute2 > n3) {
                        n3 = intAttribute2;
                    }
                    array4[intAttribute2] = (char)this.getIntAttribute(node4, "value");
                    node4 = node4.getNextSibling();
                }
                final int n4 = n3 + 1;
                System.arraycopy(array4, 0, this.hIST_histogram = new char[n4], 0, n4);
                this.hIST_present = true;
            }
            else if (nodeName.equals("iCCP")) {
                this.iCCP_profileName = this.getAttribute(node2, "profileName");
                this.iCCP_compressionMethod = this.getEnumeratedAttribute(node2, "compressionMethod", PNGMetadata.iCCP_compressionMethodNames);
                final Object userObject = ((IIOMetadataNode)node2).getUserObject();
                if (userObject == null) {
                    this.fatal(node2, "No ICCP profile present in user object!");
                }
                if (!(userObject instanceof byte[])) {
                    this.fatal(node2, "User object not a byte array!");
                }
                this.iCCP_compressedProfile = ((byte[])userObject).clone();
                this.iCCP_present = true;
            }
            else if (nodeName.equals("iTXt")) {
                for (Node node5 = node2.getFirstChild(); node5 != null; node5 = node5.getNextSibling()) {
                    if (!node5.getNodeName().equals("iTXtEntry")) {
                        this.fatal(node2, "Only an iTXtEntry may be a child of an iTXt!");
                    }
                    final String attribute = this.getAttribute(node5, "keyword");
                    if (this.isValidKeyword(attribute)) {
                        this.iTXt_keyword.add(attribute);
                        this.iTXt_compressionFlag.add(this.getBooleanAttribute(node5, "compressionFlag"));
                        this.iTXt_compressionMethod.add(Integer.valueOf(this.getAttribute(node5, "compressionMethod")));
                        this.iTXt_languageTag.add(this.getAttribute(node5, "languageTag"));
                        this.iTXt_translatedKeyword.add(this.getAttribute(node5, "translatedKeyword"));
                        this.iTXt_text.add(this.getAttribute(node5, "text"));
                    }
                }
            }
            else if (nodeName.equals("pHYs")) {
                this.pHYs_pixelsPerUnitXAxis = this.getIntAttribute(node2, "pixelsPerUnitXAxis");
                this.pHYs_pixelsPerUnitYAxis = this.getIntAttribute(node2, "pixelsPerUnitYAxis");
                this.pHYs_unitSpecifier = this.getEnumeratedAttribute(node2, "unitSpecifier", PNGMetadata.unitSpecifierNames);
                this.pHYs_present = true;
            }
            else if (nodeName.equals("sBIT")) {
                this.sBIT_present = false;
                final Node firstChild2 = node2.getFirstChild();
                if (firstChild2 == null) {
                    this.fatal(node2, "sBIT node has no children!");
                }
                final String nodeName3 = firstChild2.getNodeName();
                if (nodeName3.equals("sBIT_Grayscale")) {
                    this.sBIT_grayBits = this.getIntAttribute(firstChild2, "gray");
                    this.sBIT_colorType = 0;
                }
                else if (nodeName3.equals("sBIT_GrayAlpha")) {
                    this.sBIT_grayBits = this.getIntAttribute(firstChild2, "gray");
                    this.sBIT_alphaBits = this.getIntAttribute(firstChild2, "alpha");
                    this.sBIT_colorType = 4;
                }
                else if (nodeName3.equals("sBIT_RGB")) {
                    this.sBIT_redBits = this.getIntAttribute(firstChild2, "red");
                    this.sBIT_greenBits = this.getIntAttribute(firstChild2, "green");
                    this.sBIT_blueBits = this.getIntAttribute(firstChild2, "blue");
                    this.sBIT_colorType = 2;
                }
                else if (nodeName3.equals("sBIT_RGBAlpha")) {
                    this.sBIT_redBits = this.getIntAttribute(firstChild2, "red");
                    this.sBIT_greenBits = this.getIntAttribute(firstChild2, "green");
                    this.sBIT_blueBits = this.getIntAttribute(firstChild2, "blue");
                    this.sBIT_alphaBits = this.getIntAttribute(firstChild2, "alpha");
                    this.sBIT_colorType = 6;
                }
                else if (nodeName3.equals("sBIT_Palette")) {
                    this.sBIT_redBits = this.getIntAttribute(firstChild2, "red");
                    this.sBIT_greenBits = this.getIntAttribute(firstChild2, "green");
                    this.sBIT_blueBits = this.getIntAttribute(firstChild2, "blue");
                    this.sBIT_colorType = 3;
                }
                else {
                    this.fatal(node2, "Bad child of an sBIT node!");
                }
                if (firstChild2.getNextSibling() != null) {
                    this.fatal(node2, "sBIT node has more than one child!");
                }
                this.sBIT_present = true;
            }
            else if (nodeName.equals("sPLT")) {
                this.sPLT_paletteName = this.getAttribute(node2, "name");
                this.sPLT_sampleDepth = this.getIntAttribute(node2, "sampleDepth");
                final int[] array5 = new int[256];
                final int[] array6 = new int[256];
                final int[] array7 = new int[256];
                final int[] array8 = new int[256];
                final int[] array9 = new int[256];
                int n5 = -1;
                Node node6 = node2.getFirstChild();
                if (node6 == null) {
                    this.fatal(node2, "sPLT node has no children!");
                }
                while (node6 != null) {
                    if (!node6.getNodeName().equals("sPLTEntry")) {
                        this.fatal(node2, "Only an sPLTEntry may be a child of an sPLT!");
                    }
                    final int intAttribute3 = this.getIntAttribute(node6, "index");
                    if (intAttribute3 < 0 || intAttribute3 > 255) {
                        this.fatal(node2, "Bad value for PLTEEntry attribute index!");
                    }
                    if (intAttribute3 > n5) {
                        n5 = intAttribute3;
                    }
                    array5[intAttribute3] = this.getIntAttribute(node6, "red");
                    array6[intAttribute3] = this.getIntAttribute(node6, "green");
                    array7[intAttribute3] = this.getIntAttribute(node6, "blue");
                    array8[intAttribute3] = this.getIntAttribute(node6, "alpha");
                    array9[intAttribute3] = this.getIntAttribute(node6, "frequency");
                    node6 = node6.getNextSibling();
                }
                final int n6 = n5 + 1;
                this.sPLT_red = new int[n6];
                this.sPLT_green = new int[n6];
                this.sPLT_blue = new int[n6];
                this.sPLT_alpha = new int[n6];
                this.sPLT_frequency = new int[n6];
                System.arraycopy(array5, 0, this.sPLT_red, 0, n6);
                System.arraycopy(array6, 0, this.sPLT_green, 0, n6);
                System.arraycopy(array7, 0, this.sPLT_blue, 0, n6);
                System.arraycopy(array8, 0, this.sPLT_alpha, 0, n6);
                System.arraycopy(array9, 0, this.sPLT_frequency, 0, n6);
                this.sPLT_present = true;
            }
            else if (nodeName.equals("sRGB")) {
                this.sRGB_renderingIntent = this.getEnumeratedAttribute(node2, "renderingIntent", PNGMetadata.renderingIntentNames);
                this.sRGB_present = true;
            }
            else if (nodeName.equals("tEXt")) {
                for (Node node7 = node2.getFirstChild(); node7 != null; node7 = node7.getNextSibling()) {
                    if (!node7.getNodeName().equals("tEXtEntry")) {
                        this.fatal(node2, "Only an tEXtEntry may be a child of an tEXt!");
                    }
                    this.tEXt_keyword.add(this.getAttribute(node7, "keyword"));
                    this.tEXt_text.add(this.getAttribute(node7, "value"));
                }
            }
            else if (nodeName.equals("tIME")) {
                this.tIME_year = this.getIntAttribute(node2, "year");
                this.tIME_month = this.getIntAttribute(node2, "month");
                this.tIME_day = this.getIntAttribute(node2, "day");
                this.tIME_hour = this.getIntAttribute(node2, "hour");
                this.tIME_minute = this.getIntAttribute(node2, "minute");
                this.tIME_second = this.getIntAttribute(node2, "second");
                this.tIME_present = true;
            }
            else if (nodeName.equals("tRNS")) {
                this.tRNS_present = false;
                final Node firstChild3 = node2.getFirstChild();
                if (firstChild3 == null) {
                    this.fatal(node2, "tRNS node has no children!");
                }
                final String nodeName4 = firstChild3.getNodeName();
                if (nodeName4.equals("tRNS_Palette")) {
                    final byte[] array10 = new byte[256];
                    int n7 = -1;
                    Node node8 = firstChild3.getFirstChild();
                    if (node8 == null) {
                        this.fatal(node2, "tRNS_Palette node has no children!");
                    }
                    while (node8 != null) {
                        if (!node8.getNodeName().equals("tRNS_PaletteEntry")) {
                            this.fatal(node2, "Only a tRNS_PaletteEntry may be a child of a tRNS_Palette!");
                        }
                        final int intAttribute4 = this.getIntAttribute(node8, "index");
                        if (intAttribute4 < 0 || intAttribute4 > 255) {
                            this.fatal(node2, "Bad value for tRNS_PaletteEntry attribute index!");
                        }
                        if (intAttribute4 > n7) {
                            n7 = intAttribute4;
                        }
                        array10[intAttribute4] = (byte)this.getIntAttribute(node8, "alpha");
                        node8 = node8.getNextSibling();
                    }
                    final int n8 = n7 + 1;
                    this.tRNS_alpha = new byte[n8];
                    this.tRNS_colorType = 3;
                    System.arraycopy(array10, 0, this.tRNS_alpha, 0, n8);
                }
                else if (nodeName4.equals("tRNS_Grayscale")) {
                    this.tRNS_gray = this.getIntAttribute(firstChild3, "gray");
                    this.tRNS_colorType = 0;
                }
                else if (nodeName4.equals("tRNS_RGB")) {
                    this.tRNS_red = this.getIntAttribute(firstChild3, "red");
                    this.tRNS_green = this.getIntAttribute(firstChild3, "green");
                    this.tRNS_blue = this.getIntAttribute(firstChild3, "blue");
                    this.tRNS_colorType = 2;
                }
                else {
                    this.fatal(node2, "Bad child of a tRNS node!");
                }
                if (firstChild3.getNextSibling() != null) {
                    this.fatal(node2, "tRNS node has more than one child!");
                }
                this.tRNS_present = true;
            }
            else if (nodeName.equals("zTXt")) {
                for (Node node9 = node2.getFirstChild(); node9 != null; node9 = node9.getNextSibling()) {
                    if (!node9.getNodeName().equals("zTXtEntry")) {
                        this.fatal(node2, "Only an zTXtEntry may be a child of an zTXt!");
                    }
                    this.zTXt_keyword.add(this.getAttribute(node9, "keyword"));
                    this.zTXt_compressionMethod.add(new Integer(this.getEnumeratedAttribute(node9, "compressionMethod", PNGMetadata.zTXt_compressionMethodNames)));
                    this.zTXt_text.add(this.getAttribute(node9, "text"));
                }
            }
            else if (nodeName.equals("UnknownChunks")) {
                for (Node node10 = node2.getFirstChild(); node10 != null; node10 = node10.getNextSibling()) {
                    if (!node10.getNodeName().equals("UnknownChunk")) {
                        this.fatal(node2, "Only an UnknownChunk may be a child of an UnknownChunks!");
                    }
                    final String attribute2 = this.getAttribute(node10, "type");
                    final Object userObject2 = ((IIOMetadataNode)node10).getUserObject();
                    if (attribute2.length() != 4) {
                        this.fatal(node10, "Chunk type must be 4 characters!");
                    }
                    if (userObject2 == null) {
                        this.fatal(node10, "No chunk data present in user object!");
                    }
                    if (!(userObject2 instanceof byte[])) {
                        this.fatal(node10, "User object not a byte array!");
                    }
                    this.unknownChunkType.add(attribute2);
                    this.unknownChunkData.add(((byte[])userObject2).clone());
                }
            }
            else {
                this.fatal(node2, "Unknown child of root node!");
            }
        }
    }
    
    private boolean isValidKeyword(final String s) {
        final int length = s.length();
        return length >= 1 && length < 80 && !s.startsWith(" ") && !s.endsWith(" ") && !s.contains("  ") && this.isISOLatin(s, false);
    }
    
    private boolean isISOLatin(final String s, final boolean b) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if ((char1 < ' ' || char1 > '\u00ff' || (char1 > '~' && char1 < '¡')) && (!b || char1 != '\u0010')) {
                return false;
            }
        }
        return true;
    }
    
    private void mergeStandardTree(final Node node) throws IIOInvalidTreeException {
        if (!node.getNodeName().equals("javax_imageio_1.0")) {
            this.fatal(node, "Root must be javax_imageio_1.0");
        }
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            final String nodeName = node2.getNodeName();
            if (nodeName.equals("Chroma")) {
                for (Node node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                    final String nodeName2 = node3.getNodeName();
                    if (nodeName2.equals("Gamma")) {
                        final float floatAttribute = this.getFloatAttribute(node3, "value");
                        this.gAMA_present = true;
                        this.gAMA_gamma = (int)(floatAttribute * 100000.0f + 0.5);
                    }
                    else if (nodeName2.equals("Palette")) {
                        final byte[] array = new byte[256];
                        final byte[] array2 = new byte[256];
                        final byte[] array3 = new byte[256];
                        int n = -1;
                        for (Node node4 = node3.getFirstChild(); node4 != null; node4 = node4.getNextSibling()) {
                            final int intAttribute = this.getIntAttribute(node4, "index");
                            if (intAttribute >= 0 && intAttribute <= 255) {
                                array[intAttribute] = (byte)this.getIntAttribute(node4, "red");
                                array2[intAttribute] = (byte)this.getIntAttribute(node4, "green");
                                array3[intAttribute] = (byte)this.getIntAttribute(node4, "blue");
                                if (intAttribute > n) {
                                    n = intAttribute;
                                }
                            }
                        }
                        final int n2 = n + 1;
                        this.PLTE_red = new byte[n2];
                        this.PLTE_green = new byte[n2];
                        this.PLTE_blue = new byte[n2];
                        System.arraycopy(array, 0, this.PLTE_red, 0, n2);
                        System.arraycopy(array2, 0, this.PLTE_green, 0, n2);
                        System.arraycopy(array3, 0, this.PLTE_blue, 0, n2);
                        this.PLTE_present = true;
                    }
                    else if (nodeName2.equals("BackgroundIndex")) {
                        this.bKGD_present = true;
                        this.bKGD_colorType = 3;
                        this.bKGD_index = this.getIntAttribute(node3, "value");
                    }
                    else if (nodeName2.equals("BackgroundColor")) {
                        final int intAttribute2 = this.getIntAttribute(node3, "red");
                        final int intAttribute3 = this.getIntAttribute(node3, "green");
                        final int intAttribute4 = this.getIntAttribute(node3, "blue");
                        if (intAttribute2 == intAttribute3 && intAttribute2 == intAttribute4) {
                            this.bKGD_colorType = 0;
                            this.bKGD_gray = intAttribute2;
                        }
                        else {
                            this.bKGD_red = intAttribute2;
                            this.bKGD_green = intAttribute3;
                            this.bKGD_blue = intAttribute4;
                        }
                        this.bKGD_present = true;
                    }
                }
            }
            else if (nodeName.equals("Compression")) {
                for (Node node5 = node2.getFirstChild(); node5 != null; node5 = node5.getNextSibling()) {
                    if (node5.getNodeName().equals("NumProgressiveScans")) {
                        this.IHDR_interlaceMethod = ((this.getIntAttribute(node5, "value") > 1) ? 1 : 0);
                    }
                }
            }
            else if (nodeName.equals("Data")) {
                for (Node node6 = node2.getFirstChild(); node6 != null; node6 = node6.getNextSibling()) {
                    final String nodeName3 = node6.getNodeName();
                    if (nodeName3.equals("BitsPerSample")) {
                        final StringTokenizer stringTokenizer = new StringTokenizer(this.getAttribute(node6, "value"));
                        int ihdr_bitDepth = -1;
                        while (stringTokenizer.hasMoreTokens()) {
                            final int int1 = Integer.parseInt(stringTokenizer.nextToken());
                            if (int1 > ihdr_bitDepth) {
                                ihdr_bitDepth = int1;
                            }
                        }
                        if (ihdr_bitDepth < 1) {
                            ihdr_bitDepth = 1;
                        }
                        if (ihdr_bitDepth == 3) {
                            ihdr_bitDepth = 4;
                        }
                        if (ihdr_bitDepth > 4 || ihdr_bitDepth < 8) {
                            ihdr_bitDepth = 8;
                        }
                        if (ihdr_bitDepth > 8) {
                            ihdr_bitDepth = 16;
                        }
                        this.IHDR_bitDepth = ihdr_bitDepth;
                    }
                    else if (nodeName3.equals("SignificantBitsPerSample")) {
                        final StringTokenizer stringTokenizer2 = new StringTokenizer(this.getAttribute(node6, "value"));
                        final int countTokens = stringTokenizer2.countTokens();
                        if (countTokens == 1) {
                            this.sBIT_colorType = 0;
                            this.sBIT_grayBits = Integer.parseInt(stringTokenizer2.nextToken());
                        }
                        else if (countTokens == 2) {
                            this.sBIT_colorType = 4;
                            this.sBIT_grayBits = Integer.parseInt(stringTokenizer2.nextToken());
                            this.sBIT_alphaBits = Integer.parseInt(stringTokenizer2.nextToken());
                        }
                        else if (countTokens == 3) {
                            this.sBIT_colorType = 2;
                            this.sBIT_redBits = Integer.parseInt(stringTokenizer2.nextToken());
                            this.sBIT_greenBits = Integer.parseInt(stringTokenizer2.nextToken());
                            this.sBIT_blueBits = Integer.parseInt(stringTokenizer2.nextToken());
                        }
                        else if (countTokens == 4) {
                            this.sBIT_colorType = 6;
                            this.sBIT_redBits = Integer.parseInt(stringTokenizer2.nextToken());
                            this.sBIT_greenBits = Integer.parseInt(stringTokenizer2.nextToken());
                            this.sBIT_blueBits = Integer.parseInt(stringTokenizer2.nextToken());
                            this.sBIT_alphaBits = Integer.parseInt(stringTokenizer2.nextToken());
                        }
                        if (countTokens >= 1 && countTokens <= 4) {
                            this.sBIT_present = true;
                        }
                    }
                }
            }
            else if (nodeName.equals("Dimension")) {
                boolean b = false;
                boolean b2 = false;
                boolean b3 = false;
                float floatAttribute2 = -1.0f;
                float floatAttribute3 = -1.0f;
                float floatAttribute4 = -1.0f;
                for (Node node7 = node2.getFirstChild(); node7 != null; node7 = node7.getNextSibling()) {
                    final String nodeName4 = node7.getNodeName();
                    if (nodeName4.equals("PixelAspectRatio")) {
                        floatAttribute4 = this.getFloatAttribute(node7, "value");
                        b3 = true;
                    }
                    else if (nodeName4.equals("HorizontalPixelSize")) {
                        floatAttribute2 = this.getFloatAttribute(node7, "value");
                        b = true;
                    }
                    else if (nodeName4.equals("VerticalPixelSize")) {
                        floatAttribute3 = this.getFloatAttribute(node7, "value");
                        b2 = true;
                    }
                }
                if (b && b2) {
                    this.pHYs_present = true;
                    this.pHYs_unitSpecifier = 1;
                    this.pHYs_pixelsPerUnitXAxis = (int)(floatAttribute2 * 1000.0f + 0.5f);
                    this.pHYs_pixelsPerUnitYAxis = (int)(floatAttribute3 * 1000.0f + 0.5f);
                }
                else if (b3) {
                    this.pHYs_present = true;
                    this.pHYs_unitSpecifier = 0;
                    int phYs_pixelsPerUnitYAxis;
                    for (phYs_pixelsPerUnitYAxis = 1; phYs_pixelsPerUnitYAxis < 100 && Math.abs((int)(floatAttribute4 * phYs_pixelsPerUnitYAxis) / phYs_pixelsPerUnitYAxis - floatAttribute4) >= 0.001; ++phYs_pixelsPerUnitYAxis) {}
                    this.pHYs_pixelsPerUnitXAxis = (int)(floatAttribute4 * phYs_pixelsPerUnitYAxis);
                    this.pHYs_pixelsPerUnitYAxis = phYs_pixelsPerUnitYAxis;
                }
            }
            else if (nodeName.equals("Document")) {
                for (Node node8 = node2.getFirstChild(); node8 != null; node8 = node8.getNextSibling()) {
                    if (node8.getNodeName().equals("ImageModificationTime")) {
                        this.tIME_present = true;
                        this.tIME_year = this.getIntAttribute(node8, "year");
                        this.tIME_month = this.getIntAttribute(node8, "month");
                        this.tIME_day = this.getIntAttribute(node8, "day");
                        this.tIME_hour = this.getIntAttribute(node8, "hour", 0, false);
                        this.tIME_minute = this.getIntAttribute(node8, "minute", 0, false);
                        this.tIME_second = this.getIntAttribute(node8, "second", 0, false);
                    }
                }
            }
            else if (nodeName.equals("Text")) {
                for (Node node9 = node2.getFirstChild(); node9 != null; node9 = node9.getNextSibling()) {
                    if (node9.getNodeName().equals("TextEntry")) {
                        final String attribute = this.getAttribute(node9, "keyword", "", false);
                        final String attribute2 = this.getAttribute(node9, "value");
                        final String attribute3 = this.getAttribute(node9, "language", "", false);
                        final String attribute4 = this.getAttribute(node9, "compression", "none", false);
                        if (this.isValidKeyword(attribute)) {
                            if (this.isISOLatin(attribute2, true)) {
                                if (attribute4.equals("zip")) {
                                    this.zTXt_keyword.add(attribute);
                                    this.zTXt_text.add(attribute2);
                                    this.zTXt_compressionMethod.add(0);
                                }
                                else {
                                    this.tEXt_keyword.add(attribute);
                                    this.tEXt_text.add(attribute2);
                                }
                            }
                            else {
                                this.iTXt_keyword.add(attribute);
                                this.iTXt_compressionFlag.add(attribute4.equals("zip"));
                                this.iTXt_compressionMethod.add(0);
                                this.iTXt_languageTag.add(attribute3);
                                this.iTXt_translatedKeyword.add(attribute);
                                this.iTXt_text.add(attribute2);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void reset() {
        this.IHDR_present = false;
        this.PLTE_present = false;
        this.bKGD_present = false;
        this.cHRM_present = false;
        this.gAMA_present = false;
        this.hIST_present = false;
        this.iCCP_present = false;
        this.iTXt_keyword = new ArrayList<String>();
        this.iTXt_compressionFlag = new ArrayList<Boolean>();
        this.iTXt_compressionMethod = new ArrayList<Integer>();
        this.iTXt_languageTag = new ArrayList<String>();
        this.iTXt_translatedKeyword = new ArrayList<String>();
        this.iTXt_text = new ArrayList<String>();
        this.pHYs_present = false;
        this.sBIT_present = false;
        this.sPLT_present = false;
        this.sRGB_present = false;
        this.tEXt_keyword = new ArrayList<String>();
        this.tEXt_text = new ArrayList<String>();
        this.tIME_present = false;
        this.tRNS_present = false;
        this.zTXt_keyword = new ArrayList<String>();
        this.zTXt_compressionMethod = new ArrayList<Integer>();
        this.zTXt_text = new ArrayList<String>();
        this.unknownChunkType = new ArrayList<String>();
        this.unknownChunkData = new ArrayList<byte[]>();
    }
    
    static {
        IHDR_colorTypeNames = new String[] { "Grayscale", null, "RGB", "Palette", "GrayAlpha", null, "RGBAlpha" };
        IHDR_numChannels = new int[] { 1, 0, 3, 3, 2, 0, 4 };
        IHDR_bitDepths = new String[] { "1", "2", "4", "8", "16" };
        IHDR_compressionMethodNames = new String[] { "deflate" };
        IHDR_filterMethodNames = new String[] { "adaptive" };
        IHDR_interlaceMethodNames = new String[] { "none", "adam7" };
        iCCP_compressionMethodNames = new String[] { "deflate" };
        zTXt_compressionMethodNames = new String[] { "deflate" };
        unitSpecifierNames = new String[] { "unknown", "meter" };
        renderingIntentNames = new String[] { "Perceptual", "Relative colorimetric", "Saturation", "Absolute colorimetric" };
        colorSpaceTypeNames = new String[] { "GRAY", null, "RGB", "RGB", "GRAY", null, "RGB" };
    }
}
