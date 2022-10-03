package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;
import com.lowagie.text.pdf.PatternColor;
import com.lowagie.text.pdf.ShadingColor;
import com.lowagie.text.pdf.SpotColor;
import com.lowagie.text.pdf.PdfXConformanceException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.interfaces.PdfXConformance;

public class PdfXConformanceImp implements PdfXConformance
{
    public static final int PDFXKEY_COLOR = 1;
    public static final int PDFXKEY_CMYK = 2;
    public static final int PDFXKEY_RGB = 3;
    public static final int PDFXKEY_FONT = 4;
    public static final int PDFXKEY_IMAGE = 5;
    public static final int PDFXKEY_GSTATE = 6;
    public static final int PDFXKEY_LAYER = 7;
    protected int pdfxConformance;
    
    public PdfXConformanceImp() {
        this.pdfxConformance = 0;
    }
    
    @Override
    public void setPDFXConformance(final int pdfxConformance) {
        this.pdfxConformance = pdfxConformance;
    }
    
    @Override
    public int getPDFXConformance() {
        return this.pdfxConformance;
    }
    
    @Override
    public boolean isPdfX() {
        return this.pdfxConformance != 0;
    }
    
    public boolean isPdfX1A2001() {
        return this.pdfxConformance == 1;
    }
    
    public boolean isPdfX32002() {
        return this.pdfxConformance == 2;
    }
    
    public boolean isPdfA1() {
        return this.pdfxConformance == 3 || this.pdfxConformance == 4;
    }
    
    public boolean isPdfA1A() {
        return this.pdfxConformance == 3;
    }
    
    public void completeInfoDictionary(final PdfDictionary info) {
        if (this.isPdfX() && !this.isPdfA1()) {
            if (info.get(PdfName.GTS_PDFXVERSION) == null) {
                if (this.isPdfX1A2001()) {
                    info.put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-1:2001"));
                    info.put(new PdfName("GTS_PDFXConformance"), new PdfString("PDF/X-1a:2001"));
                }
                else if (this.isPdfX32002()) {
                    info.put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-3:2002"));
                }
            }
            if (info.get(PdfName.TITLE) == null) {
                info.put(PdfName.TITLE, new PdfString("Pdf document"));
            }
            if (info.get(PdfName.CREATOR) == null) {
                info.put(PdfName.CREATOR, new PdfString("Unknown"));
            }
            if (info.get(PdfName.TRAPPED) == null) {
                info.put(PdfName.TRAPPED, new PdfName("False"));
            }
        }
    }
    
    public void completeExtraCatalog(final PdfDictionary extraCatalog) {
        if (this.isPdfX() && !this.isPdfA1() && extraCatalog.get(PdfName.OUTPUTINTENTS) == null) {
            final PdfDictionary out = new PdfDictionary(PdfName.OUTPUTINTENT);
            out.put(PdfName.OUTPUTCONDITION, new PdfString("SWOP CGATS TR 001-1995"));
            out.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString("CGATS TR 001"));
            out.put(PdfName.REGISTRYNAME, new PdfString("http://www.color.org"));
            out.put(PdfName.INFO, new PdfString(""));
            out.put(PdfName.S, PdfName.GTS_PDFX);
            extraCatalog.put(PdfName.OUTPUTINTENTS, new PdfArray(out));
        }
    }
    
    public static void checkPDFXConformance(final PdfWriter writer, final int key, final Object obj1) {
        if (writer == null || !writer.isPdfX()) {
            return;
        }
        final int conf = writer.getPDFXConformance();
        switch (key) {
            case 1: {
                switch (conf) {
                    case 1: {
                        if (obj1 instanceof ExtendedColor) {
                            final ExtendedColor ec = (ExtendedColor)obj1;
                            switch (ec.getType()) {
                                case 1:
                                case 2: {
                                    return;
                                }
                                case 0: {
                                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.rgb.is.not.allowed"));
                                }
                                case 3: {
                                    final SpotColor sc = (SpotColor)ec;
                                    checkPDFXConformance(writer, 1, sc.getPdfSpotColor().getAlternativeCS());
                                    break;
                                }
                                case 5: {
                                    final ShadingColor xc = (ShadingColor)ec;
                                    checkPDFXConformance(writer, 1, xc.getPdfShadingPattern().getShading().getColorSpace());
                                    break;
                                }
                                case 4: {
                                    final PatternColor pc = (PatternColor)ec;
                                    checkPDFXConformance(writer, 1, pc.getPainter().getDefaultColor());
                                    break;
                                }
                            }
                            break;
                        }
                        if (obj1 instanceof Color) {
                            throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.rgb.is.not.allowed"));
                        }
                        break;
                    }
                }
            }
            case 3: {
                if (conf == 1) {
                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.rgb.is.not.allowed"));
                }
                break;
            }
            case 4: {
                if (!((BaseFont)obj1).isEmbedded()) {
                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage("all.the.fonts.must.be.embedded.this.one.isn.t.1", ((BaseFont)obj1).getPostscriptFontName()));
                }
                break;
            }
            case 5: {
                final PdfImage image = (PdfImage)obj1;
                if (image.get(PdfName.SMASK) != null) {
                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage("the.smask.key.is.not.allowed.in.images"));
                }
                switch (conf) {
                    case 1: {
                        final PdfObject cs = image.get(PdfName.COLORSPACE);
                        if (cs == null) {
                            return;
                        }
                        if (cs.isName()) {
                            if (PdfName.DEVICERGB.equals(cs)) {
                                throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.rgb.is.not.allowed"));
                            }
                            break;
                        }
                        else {
                            if (cs.isArray() && PdfName.CALRGB.equals(((PdfArray)cs).getPdfObject(0))) {
                                throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.calrgb.is.not.allowed"));
                            }
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 6: {
                final PdfDictionary gs = (PdfDictionary)obj1;
                PdfObject obj2 = gs.get(PdfName.BM);
                if (obj2 != null && !PdfGState.BM_NORMAL.equals(obj2) && !PdfGState.BM_COMPATIBLE.equals(obj2)) {
                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage("blend.mode.1.not.allowed", obj2.toString()));
                }
                obj2 = gs.get(PdfName.CA);
                double v = 0.0;
                if (obj2 != null && (v = ((PdfNumber)obj2).doubleValue()) != 1.0) {
                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage("transparency.is.not.allowed.ca.eq.1", String.valueOf(v)));
                }
                obj2 = gs.get(PdfName.ca);
                v = 0.0;
                if (obj2 != null && (v = ((PdfNumber)obj2).doubleValue()) != 1.0) {
                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage("transparency.is.not.allowed.ca.eq.1", String.valueOf(v)));
                }
                break;
            }
            case 7: {
                throw new PdfXConformanceException(MessageLocalization.getComposedMessage("layers.are.not.allowed"));
            }
        }
    }
}
