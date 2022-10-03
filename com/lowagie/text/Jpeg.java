package com.lowagie.text;

import java.awt.color.ICC_Profile;
import java.nio.charset.StandardCharsets;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

public class Jpeg extends Image
{
    public static final int NOT_A_MARKER = -1;
    public static final int VALID_MARKER = 0;
    public static final int[] VALID_MARKERS;
    public static final int UNSUPPORTED_MARKER = 1;
    public static final int[] UNSUPPORTED_MARKERS;
    public static final int NOPARAM_MARKER = 2;
    public static final int[] NOPARAM_MARKERS;
    public static final int M_APP0 = 224;
    public static final int M_APP2 = 226;
    public static final int M_APPE = 238;
    public static final byte[] JFIF_ID;
    private byte[][] icc;
    
    Jpeg(final Image image) {
        super(image);
    }
    
    public Jpeg(final URL url) throws BadElementException, IOException {
        super(url);
        this.processParameters();
    }
    
    public Jpeg(final byte[] img) throws BadElementException, IOException {
        super((URL)null);
        this.rawData = img;
        this.originalData = img;
        this.processParameters();
    }
    
    public Jpeg(final byte[] img, final float width, final float height) throws BadElementException, IOException {
        this(img);
        this.scaledWidth = width;
        this.scaledHeight = height;
    }
    
    private static final int getShort(final InputStream is) throws IOException {
        return (is.read() << 8) + is.read();
    }
    
    private static final int marker(final int marker) {
        for (int i = 0; i < Jpeg.VALID_MARKERS.length; ++i) {
            if (marker == Jpeg.VALID_MARKERS[i]) {
                return 0;
            }
        }
        for (int i = 0; i < Jpeg.NOPARAM_MARKERS.length; ++i) {
            if (marker == Jpeg.NOPARAM_MARKERS[i]) {
                return 2;
            }
        }
        for (int i = 0; i < Jpeg.UNSUPPORTED_MARKERS.length; ++i) {
            if (marker == Jpeg.UNSUPPORTED_MARKERS[i]) {
                return 1;
            }
        }
        return -1;
    }
    
    private void processParameters() throws BadElementException, IOException {
        this.type = 32;
        this.originalType = 1;
        InputStream is = null;
        try {
            String errorID;
            if (this.rawData == null) {
                is = this.url.openStream();
                errorID = this.url.toString();
            }
            else {
                is = new ByteArrayInputStream(this.rawData);
                errorID = "Byte array";
            }
            if (is.read() != 255 || is.read() != 216) {
                throw new BadElementException(MessageLocalization.getComposedMessage("1.is.not.a.valid.jpeg.file", errorID));
            }
            boolean firstPass = true;
            while (true) {
                final int v = is.read();
                if (v < 0) {
                    throw new IOException(MessageLocalization.getComposedMessage("premature.eof.while.reading.jpg"));
                }
                if (v != 255) {
                    continue;
                }
                final int marker = is.read();
                if (firstPass && marker == 224) {
                    firstPass = false;
                    final int len = getShort(is);
                    if (len < 16) {
                        Utilities.skip(is, len - 2);
                    }
                    else {
                        final byte[] bcomp = new byte[Jpeg.JFIF_ID.length];
                        final int r = is.read(bcomp);
                        if (r != bcomp.length) {
                            throw new BadElementException(MessageLocalization.getComposedMessage("1.corrupted.jfif.marker", errorID));
                        }
                        boolean found = true;
                        for (int k = 0; k < bcomp.length; ++k) {
                            if (bcomp[k] != Jpeg.JFIF_ID[k]) {
                                found = false;
                                break;
                            }
                        }
                        if (!found) {
                            Utilities.skip(is, len - 2 - bcomp.length);
                        }
                        else {
                            Utilities.skip(is, 2);
                            final int units = is.read();
                            final int dx = getShort(is);
                            final int dy = getShort(is);
                            if (units == 1) {
                                this.dpiX = dx;
                                this.dpiY = dy;
                            }
                            else if (units == 2) {
                                this.dpiX = (int)(dx * 2.54f + 0.5f);
                                this.dpiY = (int)(dy * 2.54f + 0.5f);
                            }
                            Utilities.skip(is, len - 2 - bcomp.length - 7);
                        }
                    }
                }
                else if (marker == 238) {
                    final int len = getShort(is) - 2;
                    final byte[] byteappe = new byte[len];
                    for (int i = 0; i < len; ++i) {
                        byteappe[i] = (byte)is.read();
                    }
                    if (byteappe.length < 12) {
                        continue;
                    }
                    final String appe = new String(byteappe, 0, 5, StandardCharsets.ISO_8859_1);
                    if (!appe.equals("Adobe")) {
                        continue;
                    }
                    this.invert = true;
                }
                else if (marker == 226) {
                    final int len = getShort(is) - 2;
                    final byte[] byteapp2 = new byte[len];
                    for (int i = 0; i < len; ++i) {
                        byteapp2[i] = (byte)is.read();
                    }
                    if (byteapp2.length < 14) {
                        continue;
                    }
                    final String app2 = new String(byteapp2, 0, 11, StandardCharsets.ISO_8859_1);
                    if (!app2.equals("ICC_PROFILE")) {
                        continue;
                    }
                    int order = byteapp2[12] & 0xFF;
                    int count = byteapp2[13] & 0xFF;
                    if (order < 1) {
                        order = 1;
                    }
                    if (count < 1) {
                        count = 1;
                    }
                    if (this.icc == null) {
                        this.icc = new byte[count][];
                    }
                    this.icc[order - 1] = byteapp2;
                }
                else {
                    firstPass = false;
                    final int markertype = marker(marker);
                    if (markertype == 0) {
                        Utilities.skip(is, 2);
                        if (is.read() != 8) {
                            throw new BadElementException(MessageLocalization.getComposedMessage("1.must.have.8.bits.per.component", errorID));
                        }
                        this.setTop(this.scaledHeight = (float)getShort(is));
                        this.setRight(this.scaledWidth = (float)getShort(is));
                        this.colorspace = is.read();
                        this.bpc = 8;
                        break;
                    }
                    else {
                        if (markertype == 1) {
                            throw new BadElementException(MessageLocalization.getComposedMessage("1.unsupported.jpeg.marker.2", errorID, String.valueOf(marker)));
                        }
                        if (markertype == 2) {
                            continue;
                        }
                        Utilities.skip(is, getShort(is) - 2);
                    }
                }
            }
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
        if (this.icc != null) {
            int total = 0;
            for (int j = 0; j < this.icc.length; ++j) {
                if (this.icc[j] == null) {
                    this.icc = null;
                    return;
                }
                total += this.icc[j].length - 14;
            }
            final byte[] ficc = new byte[total];
            total = 0;
            for (int l = 0; l < this.icc.length; ++l) {
                System.arraycopy(this.icc[l], 14, ficc, total, this.icc[l].length - 14);
                total += this.icc[l].length - 14;
            }
            try {
                final ICC_Profile icc_prof = ICC_Profile.getInstance(ficc);
                this.tagICC(icc_prof);
            }
            catch (final IllegalArgumentException ex) {}
            this.icc = null;
        }
    }
    
    static {
        VALID_MARKERS = new int[] { 192, 193, 194 };
        UNSUPPORTED_MARKERS = new int[] { 195, 197, 198, 199, 200, 201, 202, 203, 205, 206, 207 };
        NOPARAM_MARKERS = new int[] { 208, 209, 210, 211, 212, 213, 214, 215, 216, 1 };
        JFIF_ID = new byte[] { 74, 70, 73, 70, 0 };
    }
}
