package com.lowagie.text.pdf;

import java.io.InputStream;
import java.io.EOFException;
import java.util.Arrays;
import java.util.Map;
import java.io.IOException;
import java.util.Iterator;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import java.text.SimpleDateFormat;
import com.lowagie.text.error_messages.MessageLocalization;
import java.security.cert.X509Certificate;
import java.util.GregorianCalendar;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.OutputStream;
import java.security.cert.CRL;
import java.security.PrivateKey;
import java.util.Calendar;
import com.lowagie.text.Rectangle;

public class PdfSignatureAppearance
{
    public static final int SignatureRenderDescription = 0;
    public static final int SignatureRenderNameAndDescription = 1;
    public static final int SignatureRenderGraphicAndDescription = 2;
    public static final PdfName SELF_SIGNED;
    public static final PdfName VERISIGN_SIGNED;
    public static final PdfName WINCER_SIGNED;
    public static final int NOT_CERTIFIED = 0;
    public static final int CERTIFIED_NO_CHANGES_ALLOWED = 1;
    public static final int CERTIFIED_FORM_FILLING = 2;
    public static final int CERTIFIED_FORM_FILLING_AND_ANNOTATIONS = 3;
    private static final float TOP_SECTION = 0.3f;
    private static final float MARGIN = 2.0f;
    private Rectangle rect;
    private Rectangle pageRect;
    private final PdfTemplate[] app;
    private PdfTemplate frm;
    private final PdfStamperImp writer;
    private String layer2Text;
    private String reason;
    private String location;
    private Calendar signDate;
    private String provider;
    private int page;
    private String fieldName;
    private PrivateKey privKey;
    private CRL[] crlList;
    private PdfName filter;
    private boolean newField;
    private ByteBuffer sigout;
    private OutputStream originalout;
    private File tempFile;
    private PdfDictionary cryptoDictionary;
    private PdfStamper stamper;
    private boolean preClosed;
    private PdfSigGenericPKCS sigStandard;
    private int[] range;
    private RandomAccessFile raf;
    private byte[] bout;
    private int boutLen;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String digestEncryptionAlgorithm;
    private HashMap exclusionLocations;
    private Certificate[] certChain;
    private int render;
    private Image signatureGraphic;
    public static final String questionMark = "% DSUnknown\nq\n1 G\n1 g\n0.1 0 0 0.1 9 0 cm\n0 J 0 j 4 M []0 d\n1 i \n0 g\n313 292 m\n313 404 325 453 432 529 c\n478 561 504 597 504 645 c\n504 736 440 760 391 760 c\n286 760 271 681 265 626 c\n265 625 l\n100 625 l\n100 828 253 898 381 898 c\n451 898 679 878 679 650 c\n679 555 628 499 538 435 c\n488 399 467 376 467 292 c\n313 292 l\nh\n308 214 170 -164 re\nf\n0.44 G\n1.2 w\n1 1 0.4 rg\n287 318 m\n287 430 299 479 406 555 c\n451 587 478 623 478 671 c\n478 762 414 786 365 786 c\n260 786 245 707 239 652 c\n239 651 l\n74 651 l\n74 854 227 924 355 924 c\n425 924 653 904 653 676 c\n653 581 602 525 512 461 c\n462 425 441 402 441 318 c\n287 318 l\nh\n282 240 170 -164 re\nB\nQ\n";
    private String contact;
    private Font layer2Font;
    private String layer4Text;
    private boolean acro6Layers;
    private int runDirection;
    private SignatureEvent signatureEvent;
    private Image image;
    private float imageScale;
    private int certificationLevel;
    
    PdfSignatureAppearance(final PdfStamperImp writer) {
        this.app = new PdfTemplate[5];
        this.page = 1;
        this.preClosed = false;
        this.render = 0;
        this.signatureGraphic = null;
        this.runDirection = 1;
        this.certificationLevel = 0;
        this.writer = writer;
        this.signDate = new GregorianCalendar();
        this.fieldName = this.getNewSigName();
    }
    
    public int getRender() {
        return this.render;
    }
    
    public void setRender(final int render) {
        this.render = render;
    }
    
    public Image getSignatureGraphic() {
        return this.signatureGraphic;
    }
    
    public void setSignatureGraphic(final Image signatureGraphic) {
        this.signatureGraphic = signatureGraphic;
    }
    
    public void setLayer2Text(final String text) {
        this.layer2Text = text;
    }
    
    public String getLayer2Text() {
        return this.layer2Text;
    }
    
    public void setLayer4Text(final String text) {
        this.layer4Text = text;
    }
    
    public String getLayer4Text() {
        return this.layer4Text;
    }
    
    public Rectangle getRect() {
        return this.rect;
    }
    
    public boolean isInvisible() {
        return this.rect == null || this.rect.getWidth() == 0.0f || this.rect.getHeight() == 0.0f;
    }
    
    public void setCrypto(final PrivateKey privKey, final X509Certificate certificate, final CRL crl, final PdfName filter) {
        this.privKey = privKey;
        if (certificate == null) {
            throw new IllegalArgumentException("Null certificate not allowed");
        }
        (this.certChain = new Certificate[1])[0] = certificate;
        if (crl != null) {
            (this.crlList = new CRL[1])[0] = crl;
        }
        this.filter = filter;
    }
    
    public void setCrypto(final PrivateKey privKey, final Certificate[] certChain, final CRL[] crlList, final PdfName filter) {
        this.privKey = privKey;
        this.certChain = certChain;
        this.crlList = crlList;
        this.filter = filter;
    }
    
    public void setVisibleSignature(final Rectangle pageRect, final int page) {
        this.setVisibleSignature(pageRect, page, this.getNewSigName());
    }
    
    public void setVisibleSignature(final Rectangle pageRect, final int page, final String fieldName) {
        if (fieldName != null) {
            if (fieldName.indexOf(46) >= 0) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("field.names.cannot.contain.a.dot"));
            }
            final AcroFields af = this.writer.getAcroFields();
            final AcroFields.Item item = af.getFieldItem(fieldName);
            if (item != null) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.field.1.already.exists", fieldName));
            }
            this.fieldName = fieldName;
        }
        if (page < 0 || page > this.writer.reader.getNumberOfPages()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.page.number.1", page));
        }
        (this.pageRect = new Rectangle(pageRect)).normalize();
        this.rect = new Rectangle(this.pageRect.getWidth(), this.pageRect.getHeight());
        this.page = page;
        this.newField = true;
    }
    
    public void setVisibleSignature(final String fieldName) {
        final AcroFields af = this.writer.getAcroFields();
        final AcroFields.Item item = af.getFieldItem(fieldName);
        if (item == null) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.field.1.does.not.exist", fieldName));
        }
        final PdfDictionary merged = item.getMerged(0);
        if (!PdfName.SIG.equals(PdfReader.getPdfObject(merged.get(PdfName.FT)))) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.field.1.is.not.a.signature.field", fieldName));
        }
        this.fieldName = fieldName;
        final PdfArray r = merged.getAsArray(PdfName.RECT);
        final float llx = r.getAsNumber(0).floatValue();
        final float lly = r.getAsNumber(1).floatValue();
        final float urx = r.getAsNumber(2).floatValue();
        final float ury = r.getAsNumber(3).floatValue();
        (this.pageRect = new Rectangle(llx, lly, urx, ury)).normalize();
        this.page = item.getPage(0);
        final int rotation = this.writer.reader.getPageRotation(this.page);
        final Rectangle pageSize = this.writer.reader.getPageSizeWithRotation(this.page);
        switch (rotation) {
            case 90: {
                this.pageRect = new Rectangle(this.pageRect.getBottom(), pageSize.getTop() - this.pageRect.getLeft(), this.pageRect.getTop(), pageSize.getTop() - this.pageRect.getRight());
                break;
            }
            case 180: {
                this.pageRect = new Rectangle(pageSize.getRight() - this.pageRect.getLeft(), pageSize.getTop() - this.pageRect.getBottom(), pageSize.getRight() - this.pageRect.getRight(), pageSize.getTop() - this.pageRect.getTop());
                break;
            }
            case 270: {
                this.pageRect = new Rectangle(pageSize.getRight() - this.pageRect.getBottom(), this.pageRect.getLeft(), pageSize.getRight() - this.pageRect.getTop(), this.pageRect.getRight());
                break;
            }
        }
        if (rotation != 0) {
            this.pageRect.normalize();
        }
        this.rect = new Rectangle(this.pageRect.getWidth(), this.pageRect.getHeight());
    }
    
    public PdfTemplate getLayer(final int layer) {
        if (layer < 0 || layer >= this.app.length) {
            return null;
        }
        PdfTemplate t = this.app[layer];
        if (t == null) {
            final PdfTemplate[] app = this.app;
            final PdfTemplate pdfTemplate = new PdfTemplate(this.writer);
            app[layer] = pdfTemplate;
            t = pdfTemplate;
            t.setBoundingBox(this.rect);
            this.writer.addDirectTemplateSimple(t, new PdfName("n" + layer));
        }
        return t;
    }
    
    public PdfTemplate getTopLayer() {
        if (this.frm == null) {
            (this.frm = new PdfTemplate(this.writer)).setBoundingBox(this.rect);
            this.writer.addDirectTemplateSimple(this.frm, new PdfName("FRM"));
        }
        return this.frm;
    }
    
    public PdfTemplate getAppearance() throws DocumentException {
        if (this.isInvisible()) {
            final PdfTemplate t = new PdfTemplate(this.writer);
            t.setBoundingBox(new Rectangle(0.0f, 0.0f));
            this.writer.addDirectTemplateSimple(t, null);
            return t;
        }
        if (this.app[0] == null) {
            final PdfTemplate[] app = this.app;
            final int n2 = 0;
            final PdfTemplate pdfTemplate = new PdfTemplate(this.writer);
            app[n2] = pdfTemplate;
            final PdfTemplate t = pdfTemplate;
            t.setBoundingBox(new Rectangle(100.0f, 100.0f));
            this.writer.addDirectTemplateSimple(t, new PdfName("n0"));
            t.setLiteral("% DSBlank\n");
        }
        if (this.app[1] == null && !this.acro6Layers) {
            final PdfTemplate[] app2 = this.app;
            final int n3 = 1;
            final PdfTemplate pdfTemplate2 = new PdfTemplate(this.writer);
            app2[n3] = pdfTemplate2;
            final PdfTemplate t = pdfTemplate2;
            t.setBoundingBox(new Rectangle(100.0f, 100.0f));
            this.writer.addDirectTemplateSimple(t, new PdfName("n1"));
            t.setLiteral("% DSUnknown\nq\n1 G\n1 g\n0.1 0 0 0.1 9 0 cm\n0 J 0 j 4 M []0 d\n1 i \n0 g\n313 292 m\n313 404 325 453 432 529 c\n478 561 504 597 504 645 c\n504 736 440 760 391 760 c\n286 760 271 681 265 626 c\n265 625 l\n100 625 l\n100 828 253 898 381 898 c\n451 898 679 878 679 650 c\n679 555 628 499 538 435 c\n488 399 467 376 467 292 c\n313 292 l\nh\n308 214 170 -164 re\nf\n0.44 G\n1.2 w\n1 1 0.4 rg\n287 318 m\n287 430 299 479 406 555 c\n451 587 478 623 478 671 c\n478 762 414 786 365 786 c\n260 786 245 707 239 652 c\n239 651 l\n74 651 l\n74 854 227 924 355 924 c\n425 924 653 904 653 676 c\n653 581 602 525 512 461 c\n462 425 441 402 441 318 c\n287 318 l\nh\n282 240 170 -164 re\nB\nQ\n");
        }
        if (this.app[2] == null) {
            String text;
            if (this.layer2Text == null) {
                final StringBuilder buf = new StringBuilder();
                buf.append("Digitally signed by ").append(PdfPKCS7.getSubjectFields((X509Certificate)this.certChain[0]).getField("CN")).append('\n');
                final SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
                buf.append("Date: ").append(sd.format(this.signDate.getTime()));
                if (this.reason != null) {
                    buf.append('\n').append("Reason: ").append(this.reason);
                }
                if (this.location != null) {
                    buf.append('\n').append("Location: ").append(this.location);
                }
                text = buf.toString();
            }
            else {
                text = this.layer2Text;
            }
            final PdfTemplate[] app3 = this.app;
            final int n4 = 2;
            final PdfTemplate pdfTemplate3 = new PdfTemplate(this.writer);
            app3[n4] = pdfTemplate3;
            final PdfTemplate t2 = pdfTemplate3;
            t2.setBoundingBox(this.rect);
            this.writer.addDirectTemplateSimple(t2, new PdfName("n2"));
            if (this.image != null) {
                if (this.imageScale == 0.0f) {
                    t2.addImage(this.image, this.rect.getWidth(), 0.0f, 0.0f, this.rect.getHeight(), 0.0f, 0.0f);
                }
                else {
                    float usableScale = this.imageScale;
                    if (this.imageScale < 0.0f) {
                        usableScale = Math.min(this.rect.getWidth() / this.image.getWidth(), this.rect.getHeight() / this.image.getHeight());
                    }
                    final float w = this.image.getWidth() * usableScale;
                    final float h = this.image.getHeight() * usableScale;
                    final float x = (this.rect.getWidth() - w) / 2.0f;
                    final float y = (this.rect.getHeight() - h) / 2.0f;
                    t2.addImage(this.image, w, 0.0f, 0.0f, h, x, y);
                }
            }
            Font font;
            if (this.layer2Font == null) {
                font = new Font();
            }
            else {
                font = new Font(this.layer2Font);
            }
            float size = font.getSize();
            Rectangle dataRect = null;
            Rectangle signatureRect = null;
            if (this.render == 1 || (this.render == 2 && this.signatureGraphic != null)) {
                signatureRect = new Rectangle(2.0f, 2.0f, this.rect.getWidth() / 2.0f - 2.0f, this.rect.getHeight() - 2.0f);
                dataRect = new Rectangle(this.rect.getWidth() / 2.0f + 1.0f, 2.0f, this.rect.getWidth() - 1.0f, this.rect.getHeight() - 2.0f);
                if (this.rect.getHeight() > this.rect.getWidth()) {
                    signatureRect = new Rectangle(2.0f, this.rect.getHeight() / 2.0f, this.rect.getWidth() - 2.0f, this.rect.getHeight());
                    dataRect = new Rectangle(2.0f, 2.0f, this.rect.getWidth() - 2.0f, this.rect.getHeight() / 2.0f - 2.0f);
                }
            }
            else {
                dataRect = new Rectangle(2.0f, 2.0f, this.rect.getWidth() - 2.0f, this.rect.getHeight() * 0.7f - 2.0f);
            }
            if (this.render == 1) {
                final String signedBy = PdfPKCS7.getSubjectFields((X509Certificate)this.certChain[0]).getField("CN");
                final Rectangle sr2 = new Rectangle(signatureRect.getWidth() - 2.0f, signatureRect.getHeight() - 2.0f);
                final float signedSize = fitText(font, signedBy, sr2, -1.0f, this.runDirection);
                final ColumnText ct2 = new ColumnText(t2);
                ct2.setRunDirection(this.runDirection);
                ct2.setSimpleColumn(new Phrase(signedBy, font), signatureRect.getLeft(), signatureRect.getBottom(), signatureRect.getRight(), signatureRect.getTop(), signedSize, 0);
                ct2.go();
            }
            else if (this.render == 2) {
                final ColumnText ct3 = new ColumnText(t2);
                ct3.setRunDirection(this.runDirection);
                ct3.setSimpleColumn(signatureRect.getLeft(), signatureRect.getBottom(), signatureRect.getRight(), signatureRect.getTop(), 0.0f, 2);
                final Image im = Image.getInstance(this.signatureGraphic);
                im.scaleToFit(signatureRect.getWidth(), signatureRect.getHeight());
                final Paragraph p = new Paragraph();
                float x2 = 0.0f;
                float y2 = -im.getScaledHeight() + 15.0f;
                x2 += (signatureRect.getWidth() - im.getScaledWidth()) / 2.0f;
                y2 -= (signatureRect.getHeight() - im.getScaledHeight()) / 2.0f;
                p.add(new Chunk(im, x2 + (signatureRect.getWidth() - im.getScaledWidth()) / 2.0f, y2, false));
                ct3.addElement(p);
                ct3.go();
            }
            if (size <= 0.0f) {
                final Rectangle sr3 = new Rectangle(dataRect.getWidth(), dataRect.getHeight());
                size = fitText(font, text, sr3, 12.0f, this.runDirection);
            }
            final ColumnText ct4 = new ColumnText(t2);
            ct4.setRunDirection(this.runDirection);
            ct4.setSimpleColumn(new Phrase(text, font), dataRect.getLeft(), dataRect.getBottom(), dataRect.getRight(), dataRect.getTop(), size, 0);
            ct4.go();
        }
        if (this.app[3] == null && !this.acro6Layers) {
            final PdfTemplate[] app4 = this.app;
            final int n5 = 3;
            final PdfTemplate pdfTemplate4 = new PdfTemplate(this.writer);
            app4[n5] = pdfTemplate4;
            final PdfTemplate t = pdfTemplate4;
            t.setBoundingBox(new Rectangle(100.0f, 100.0f));
            this.writer.addDirectTemplateSimple(t, new PdfName("n3"));
            t.setLiteral("% DSBlank\n");
        }
        if (this.app[4] == null && !this.acro6Layers) {
            final PdfTemplate[] app5 = this.app;
            final int n6 = 4;
            final PdfTemplate pdfTemplate5 = new PdfTemplate(this.writer);
            app5[n6] = pdfTemplate5;
            final PdfTemplate t = pdfTemplate5;
            t.setBoundingBox(new Rectangle(0.0f, this.rect.getHeight() * 0.7f, this.rect.getRight(), this.rect.getTop()));
            this.writer.addDirectTemplateSimple(t, new PdfName("n4"));
            Font font2;
            if (this.layer2Font == null) {
                font2 = new Font();
            }
            else {
                font2 = new Font(this.layer2Font);
            }
            float size2 = font2.getSize();
            String text2 = "Signature Not Verified";
            if (this.layer4Text != null) {
                text2 = this.layer4Text;
            }
            final Rectangle sr4 = new Rectangle(this.rect.getWidth() - 4.0f, this.rect.getHeight() * 0.3f - 4.0f);
            size2 = fitText(font2, text2, sr4, 15.0f, this.runDirection);
            final ColumnText ct5 = new ColumnText(t);
            ct5.setRunDirection(this.runDirection);
            ct5.setSimpleColumn(new Phrase(text2, font2), 2.0f, 0.0f, this.rect.getWidth() - 2.0f, this.rect.getHeight() - 2.0f, size2, 0);
            ct5.go();
        }
        final int rotation = this.writer.reader.getPageRotation(this.page);
        Rectangle rotated = new Rectangle(this.rect);
        for (int n = rotation; n > 0; n -= 90) {
            rotated = rotated.rotate();
        }
        if (this.frm == null) {
            (this.frm = new PdfTemplate(this.writer)).setBoundingBox(rotated);
            this.writer.addDirectTemplateSimple(this.frm, new PdfName("FRM"));
            float scale = Math.min(this.rect.getWidth(), this.rect.getHeight()) * 0.9f;
            final float x3 = (this.rect.getWidth() - scale) / 2.0f;
            final float y3 = (this.rect.getHeight() - scale) / 2.0f;
            scale /= 100.0f;
            if (rotation == 90) {
                this.frm.concatCTM(0.0f, 1.0f, -1.0f, 0.0f, this.rect.getHeight(), 0.0f);
            }
            else if (rotation == 180) {
                this.frm.concatCTM(-1.0f, 0.0f, 0.0f, -1.0f, this.rect.getWidth(), this.rect.getHeight());
            }
            else if (rotation == 270) {
                this.frm.concatCTM(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, this.rect.getWidth());
            }
            this.frm.addTemplate(this.app[0], 0.0f, 0.0f);
            if (!this.acro6Layers) {
                this.frm.addTemplate(this.app[1], scale, 0.0f, 0.0f, scale, x3, y3);
            }
            this.frm.addTemplate(this.app[2], 0.0f, 0.0f);
            if (!this.acro6Layers) {
                this.frm.addTemplate(this.app[3], scale, 0.0f, 0.0f, scale, x3, y3);
                this.frm.addTemplate(this.app[4], 0.0f, 0.0f);
            }
        }
        final PdfTemplate napp = new PdfTemplate(this.writer);
        napp.setBoundingBox(rotated);
        this.writer.addDirectTemplateSimple(napp, null);
        napp.addTemplate(this.frm, 0.0f, 0.0f);
        return napp;
    }
    
    public static float fitText(final Font font, final String text, final Rectangle rect, float maxFontSize, final int runDirection) {
        try {
            ColumnText ct = null;
            int status = 0;
            if (maxFontSize <= 0.0f) {
                int cr = 0;
                int lf = 0;
                final char[] t = text.toCharArray();
                for (int k = 0; k < t.length; ++k) {
                    if (t[k] == '\n') {
                        ++lf;
                    }
                    else if (t[k] == '\r') {
                        ++cr;
                    }
                }
                final int minLines = Math.max(cr, lf) + 1;
                maxFontSize = Math.abs(rect.getHeight()) / minLines - 0.001f;
            }
            font.setSize(maxFontSize);
            final Phrase ph = new Phrase(text, font);
            ct = new ColumnText(null);
            ct.setSimpleColumn(ph, rect.getLeft(), rect.getBottom(), rect.getRight(), rect.getTop(), maxFontSize, 0);
            ct.setRunDirection(runDirection);
            status = ct.go(true);
            if ((status & 0x1) != 0x0) {
                return maxFontSize;
            }
            final float precision = 0.1f;
            float min = 0.0f;
            float max = maxFontSize;
            float size = maxFontSize;
            for (int i = 0; i < 50; ++i) {
                size = (min + max) / 2.0f;
                ct = new ColumnText(null);
                font.setSize(size);
                ct.setSimpleColumn(new Phrase(text, font), rect.getLeft(), rect.getBottom(), rect.getRight(), rect.getTop(), size, 0);
                ct.setRunDirection(runDirection);
                status = ct.go(true);
                if ((status & 0x1) != 0x0) {
                    if (max - min < size * precision) {
                        return size;
                    }
                    min = size;
                }
                else {
                    max = size;
                }
            }
            return size;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public void setExternalDigest(final byte[] digest, final byte[] RSAdata, final String digestEncryptionAlgorithm) {
        this.externalDigest = digest;
        this.externalRSAdata = RSAdata;
        this.digestEncryptionAlgorithm = digestEncryptionAlgorithm;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public String getProvider() {
        return this.provider;
    }
    
    public void setProvider(final String provider) {
        this.provider = provider;
    }
    
    public PrivateKey getPrivKey() {
        return this.privKey;
    }
    
    public CRL[] getCrlList() {
        return this.crlList;
    }
    
    public PdfName getFilter() {
        return this.filter;
    }
    
    public boolean isNewField() {
        return this.newField;
    }
    
    public int getPage() {
        return this.page;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public Rectangle getPageRect() {
        return this.pageRect;
    }
    
    public Calendar getSignDate() {
        return this.signDate;
    }
    
    public void setSignDate(final Calendar signDate) {
        this.signDate = signDate;
    }
    
    ByteBuffer getSigout() {
        return this.sigout;
    }
    
    void setSigout(final ByteBuffer sigout) {
        this.sigout = sigout;
    }
    
    OutputStream getOriginalout() {
        return this.originalout;
    }
    
    void setOriginalout(final OutputStream originalout) {
        this.originalout = originalout;
    }
    
    public File getTempFile() {
        return this.tempFile;
    }
    
    void setTempFile(final File tempFile) {
        this.tempFile = tempFile;
    }
    
    public final String getNewSigName() {
        final AcroFields af = this.writer.getAcroFields();
        String name = "Signature";
        int step = 0;
        boolean found = false;
        while (!found) {
            ++step;
            String n1 = name + step;
            if (af.getFieldItem(n1) != null) {
                continue;
            }
            n1 += ".";
            found = true;
            for (final String fn : af.getFields().keySet()) {
                if (fn.startsWith(n1)) {
                    found = false;
                    break;
                }
            }
        }
        name += step;
        return name;
    }
    
    public void preClose() throws IOException, DocumentException {
        this.preClose(null);
    }
    
    public void preClose(final HashMap exclusionSizes) throws IOException, DocumentException {
        if (this.preClosed) {
            throw new DocumentException(MessageLocalization.getComposedMessage("document.already.pre.closed"));
        }
        this.preClosed = true;
        final AcroFields af = this.writer.getAcroFields();
        final String name = this.getFieldName();
        final boolean fieldExists = !this.isInvisible() && !this.isNewField();
        final PdfIndirectReference refSig = this.writer.getPdfIndirectReference();
        this.writer.setSigFlags(3);
        if (fieldExists) {
            final PdfDictionary widget = af.getFieldItem(name).getWidget(0);
            this.writer.markUsed(widget);
            widget.put(PdfName.P, this.writer.getPageReference(this.getPage()));
            widget.put(PdfName.V, refSig);
            final PdfObject obj = PdfReader.getPdfObjectRelease(widget.get(PdfName.F));
            int flags = 0;
            if (obj != null && obj.isNumber()) {
                flags = ((PdfNumber)obj).intValue();
            }
            flags |= 0x80;
            widget.put(PdfName.F, new PdfNumber(flags));
            final PdfDictionary ap = new PdfDictionary();
            ap.put(PdfName.N, this.getAppearance().getIndirectReference());
            widget.put(PdfName.AP, ap);
        }
        else {
            final PdfFormField sigField = PdfFormField.createSignature(this.writer);
            sigField.setFieldName(name);
            sigField.put(PdfName.V, refSig);
            sigField.setFlags(132);
            int pagen = this.getPage();
            if (!this.isInvisible() && pagen == 0) {
                for (int pages = this.writer.reader.getNumberOfPages(), i = 1; i <= pages; ++i) {
                    PdfFormField field = PdfFormField.createEmpty(this.writer);
                    this.page = i;
                    pagen = i;
                    field.setWidget(this.getPageRect(), null);
                    field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, this.getAppearance());
                    field.setPlaceInPage(i);
                    field.setPage(i);
                    field.setFlags(4);
                    sigField.addKid(field);
                    field = null;
                }
            }
            else if (!this.isInvisible()) {
                sigField.setWidget(this.getPageRect(), null);
            }
            else {
                sigField.setWidget(new Rectangle(0.0f, 0.0f), null);
            }
            sigField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, this.getAppearance());
            sigField.setPage(pagen);
            this.writer.addAnnotation(sigField, pagen);
        }
        this.exclusionLocations = new HashMap();
        if (this.cryptoDictionary == null) {
            if (PdfName.ADOBE_PPKLITE.equals(this.getFilter())) {
                this.sigStandard = new PdfSigGenericPKCS.PPKLite(this.getProvider());
            }
            else if (PdfName.ADOBE_PPKMS.equals(this.getFilter())) {
                this.sigStandard = new PdfSigGenericPKCS.PPKMS(this.getProvider());
            }
            else {
                if (!PdfName.VERISIGN_PPKVS.equals(this.getFilter())) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("unknown.filter.1", this.getFilter()));
                }
                this.sigStandard = new PdfSigGenericPKCS.VeriSign(this.getProvider());
            }
            this.sigStandard.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
            if (this.getReason() != null) {
                this.sigStandard.setReason(this.getReason());
            }
            if (this.getLocation() != null) {
                this.sigStandard.setLocation(this.getLocation());
            }
            if (this.getContact() != null) {
                this.sigStandard.setContact(this.getContact());
            }
            this.sigStandard.put(PdfName.M, new PdfDate(this.getSignDate()));
            this.sigStandard.setSignInfo(this.getPrivKey(), this.certChain, this.crlList);
            final PdfString contents = (PdfString)this.sigStandard.get(PdfName.CONTENTS);
            PdfLiteral lit = new PdfLiteral((contents.toString().length() + (PdfName.ADOBE_PPKLITE.equals(this.getFilter()) ? 0 : 64)) * 2 + 2);
            this.exclusionLocations.put(PdfName.CONTENTS, lit);
            this.sigStandard.put(PdfName.CONTENTS, lit);
            lit = new PdfLiteral(80);
            this.exclusionLocations.put(PdfName.BYTERANGE, lit);
            this.sigStandard.put(PdfName.BYTERANGE, lit);
            if (this.certificationLevel > 0) {
                this.addDocMDP(this.sigStandard);
            }
            if (this.signatureEvent != null) {
                this.signatureEvent.getSignatureDictionary(this.sigStandard);
            }
            this.writer.addToBody(this.sigStandard, refSig, false);
        }
        else {
            PdfLiteral lit2 = new PdfLiteral(80);
            this.exclusionLocations.put(PdfName.BYTERANGE, lit2);
            this.cryptoDictionary.put(PdfName.BYTERANGE, lit2);
            for (final Map.Entry entry : exclusionSizes.entrySet()) {
                final PdfName key = entry.getKey();
                final Integer v = entry.getValue();
                lit2 = new PdfLiteral(v);
                this.exclusionLocations.put(key, lit2);
                this.cryptoDictionary.put(key, lit2);
            }
            if (this.certificationLevel > 0) {
                this.addDocMDP(this.cryptoDictionary);
            }
            if (this.signatureEvent != null) {
                this.signatureEvent.getSignatureDictionary(this.cryptoDictionary);
            }
            this.writer.addToBody(this.cryptoDictionary, refSig, false);
        }
        if (this.certificationLevel > 0) {
            final PdfDictionary docmdp = new PdfDictionary();
            docmdp.put(new PdfName("DocMDP"), refSig);
            this.writer.reader.getCatalog().put(new PdfName("Perms"), docmdp);
        }
        this.writer.close(this.stamper.getMoreInfo());
        this.range = new int[this.exclusionLocations.size() * 2];
        final int byteRangePosition = this.exclusionLocations.get(PdfName.BYTERANGE).getPosition();
        this.exclusionLocations.remove(PdfName.BYTERANGE);
        int idx = 1;
        for (final PdfLiteral lit3 : this.exclusionLocations.values()) {
            final int n = lit3.getPosition();
            this.range[idx++] = n;
            this.range[idx++] = lit3.getPosLength() + n;
        }
        Arrays.sort(this.range, 1, this.range.length - 1);
        for (int k = 3; k < this.range.length - 2; k += 2) {
            final int[] range = this.range;
            final int n2 = k;
            range[n2] -= this.range[k - 1];
        }
        if (this.tempFile == null) {
            this.bout = this.sigout.getBuffer();
            this.boutLen = this.sigout.size();
            this.range[this.range.length - 1] = this.boutLen - this.range[this.range.length - 2];
            final ByteBuffer bf = new ByteBuffer();
            bf.append('[');
            for (int j = 0; j < this.range.length; ++j) {
                bf.append(this.range[j]).append(' ');
            }
            bf.append(']');
            System.arraycopy(bf.getBuffer(), 0, this.bout, byteRangePosition, bf.size());
        }
        else {
            try {
                this.raf = new RandomAccessFile(this.tempFile, "rw");
                final int boutL = (int)this.raf.length();
                this.range[this.range.length - 1] = boutL - this.range[this.range.length - 2];
                final ByteBuffer bf2 = new ByteBuffer();
                bf2.append('[');
                for (int l = 0; l < this.range.length; ++l) {
                    bf2.append(this.range[l]).append(' ');
                }
                bf2.append(']');
                this.raf.seek(byteRangePosition);
                this.raf.write(bf2.getBuffer(), 0, bf2.size());
            }
            catch (final IOException e) {
                try {
                    this.raf.close();
                }
                catch (final Exception ex) {}
                try {
                    this.tempFile.delete();
                }
                catch (final Exception ex2) {}
                throw e;
            }
        }
    }
    
    public void close(final PdfDictionary update) throws IOException, DocumentException {
        try {
            if (!this.preClosed) {
                throw new DocumentException(MessageLocalization.getComposedMessage("preclose.must.be.called.first"));
            }
            final ByteBuffer bf = new ByteBuffer();
            for (final PdfName key : update.getKeys()) {
                final PdfObject obj = update.get(key);
                final PdfLiteral lit = this.exclusionLocations.get(key);
                if (lit == null) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.key.1.didn.t.reserve.space.in.preclose", key.toString()));
                }
                bf.reset();
                obj.toPdf(null, bf);
                if (bf.size() > lit.getPosLength()) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.key.1.is.too.big.is.2.reserved.3", key.toString(), String.valueOf(bf.size()), String.valueOf(lit.getPosLength())));
                }
                if (this.tempFile == null) {
                    System.arraycopy(bf.getBuffer(), 0, this.bout, lit.getPosition(), bf.size());
                }
                else {
                    this.raf.seek(lit.getPosition());
                    this.raf.write(bf.getBuffer(), 0, bf.size());
                }
            }
            if (update.size() != this.exclusionLocations.size()) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.update.dictionary.has.less.keys.than.required"));
            }
            if (this.tempFile == null) {
                this.originalout.write(this.bout, 0, this.boutLen);
            }
            else if (this.originalout != null) {
                this.raf.seek(0L);
                int length = (int)this.raf.length();
                final byte[] buf = new byte[8192];
                while (length > 0) {
                    final int r = this.raf.read(buf, 0, Math.min(buf.length, length));
                    if (r < 0) {
                        throw new EOFException(MessageLocalization.getComposedMessage("unexpected.eof"));
                    }
                    this.originalout.write(buf, 0, r);
                    length -= r;
                }
            }
        }
        finally {
            if (this.tempFile != null) {
                try {
                    this.raf.close();
                }
                catch (final Exception ex) {}
                if (this.originalout != null) {
                    try {
                        this.tempFile.delete();
                    }
                    catch (final Exception ex2) {}
                }
            }
            if (this.originalout != null) {
                try {
                    this.originalout.close();
                }
                catch (final Exception ex3) {}
            }
        }
    }
    
    private void addDocMDP(final PdfDictionary crypto) {
        final PdfDictionary reference = new PdfDictionary();
        final PdfDictionary transformParams = new PdfDictionary();
        transformParams.put(PdfName.P, new PdfNumber(this.certificationLevel));
        transformParams.put(PdfName.V, new PdfName("1.2"));
        transformParams.put(PdfName.TYPE, PdfName.TRANSFORMPARAMS);
        reference.put(PdfName.TRANSFORMMETHOD, PdfName.DOCMDP);
        reference.put(PdfName.TYPE, PdfName.SIGREF);
        reference.put(PdfName.TRANSFORMPARAMS, transformParams);
        reference.put(new PdfName("DigestValue"), new PdfString("aa"));
        final PdfArray loc = new PdfArray();
        loc.add(new PdfNumber(0));
        loc.add(new PdfNumber(0));
        reference.put(new PdfName("DigestLocation"), loc);
        reference.put(new PdfName("DigestMethod"), new PdfName("MD5"));
        reference.put(PdfName.DATA, this.writer.reader.getTrailer().get(PdfName.ROOT));
        final PdfArray types = new PdfArray();
        types.add(reference);
        crypto.put(PdfName.REFERENCE, types);
    }
    
    public InputStream getRangeStream() {
        return new RangeStream(this.raf, this.bout, this.range);
    }
    
    public PdfDictionary getCryptoDictionary() {
        return this.cryptoDictionary;
    }
    
    public void setCryptoDictionary(final PdfDictionary cryptoDictionary) {
        this.cryptoDictionary = cryptoDictionary;
    }
    
    public PdfStamper getStamper() {
        return this.stamper;
    }
    
    void setStamper(final PdfStamper stamper) {
        this.stamper = stamper;
    }
    
    public boolean isPreClosed() {
        return this.preClosed;
    }
    
    public PdfSigGenericPKCS getSigStandard() {
        return this.sigStandard;
    }
    
    public String getContact() {
        return this.contact;
    }
    
    public void setContact(final String contact) {
        this.contact = contact;
    }
    
    public Font getLayer2Font() {
        return this.layer2Font;
    }
    
    public void setLayer2Font(final Font layer2Font) {
        this.layer2Font = layer2Font;
    }
    
    public boolean isAcro6Layers() {
        return this.acro6Layers;
    }
    
    public void setAcro6Layers(final boolean acro6Layers) {
        this.acro6Layers = acro6Layers;
    }
    
    public void setRunDirection(final int runDirection) {
        if (runDirection < 0 || runDirection > 3) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.run.direction.1", runDirection));
        }
        this.runDirection = runDirection;
    }
    
    public int getRunDirection() {
        return this.runDirection;
    }
    
    public SignatureEvent getSignatureEvent() {
        return this.signatureEvent;
    }
    
    public void setSignatureEvent(final SignatureEvent signatureEvent) {
        this.signatureEvent = signatureEvent;
    }
    
    public Image getImage() {
        return this.image;
    }
    
    public void setImage(final Image image) {
        this.image = image;
    }
    
    public float getImageScale() {
        return this.imageScale;
    }
    
    public void setImageScale(final float imageScale) {
        this.imageScale = imageScale;
    }
    
    public int getCertificationLevel() {
        return this.certificationLevel;
    }
    
    public void setCertificationLevel(final int certificationLevel) {
        this.certificationLevel = certificationLevel;
    }
    
    static {
        SELF_SIGNED = PdfName.ADOBE_PPKLITE;
        VERISIGN_SIGNED = PdfName.VERISIGN_PPKVS;
        WINCER_SIGNED = PdfName.ADOBE_PPKMS;
    }
    
    private static class RangeStream extends InputStream
    {
        private final byte[] b;
        private final RandomAccessFile raf;
        private final byte[] bout;
        private final int[] range;
        private int rangePosition;
        
        private RangeStream(final RandomAccessFile raf, final byte[] bout, final int[] range) {
            this.b = new byte[1];
            this.rangePosition = 0;
            this.raf = raf;
            this.bout = bout;
            this.range = range;
        }
        
        @Override
        public int read() throws IOException {
            final int n = this.read(this.b);
            if (n != 1) {
                return -1;
            }
            return this.b[0] & 0xFF;
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.rangePosition >= this.range[this.range.length - 2] + this.range[this.range.length - 1]) {
                return -1;
            }
            for (int k = 0; k < this.range.length; k += 2) {
                final int start = this.range[k];
                final int end = start + this.range[k + 1];
                if (this.rangePosition < start) {
                    this.rangePosition = start;
                }
                if (this.rangePosition >= start && this.rangePosition < end) {
                    final int lenf = Math.min(len, end - this.rangePosition);
                    if (this.raf == null) {
                        System.arraycopy(this.bout, this.rangePosition, b, off, lenf);
                    }
                    else {
                        this.raf.seek(this.rangePosition);
                        this.raf.readFully(b, off, lenf);
                    }
                    this.rangePosition += lenf;
                    return lenf;
                }
            }
            return -1;
        }
    }
    
    public interface SignatureEvent
    {
        void getSignatureDictionary(final PdfDictionary p0);
    }
}
