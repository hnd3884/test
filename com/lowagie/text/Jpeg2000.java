package com.lowagie.text;

import java.io.ByteArrayInputStream;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.IOException;
import java.net.URL;
import java.io.InputStream;

public class Jpeg2000 extends Image
{
    public static final int JP2_JP = 1783636000;
    public static final int JP2_IHDR = 1768449138;
    public static final int JPIP_JPIP = 1785751920;
    public static final int JP2_FTYP = 1718909296;
    public static final int JP2_JP2H = 1785737832;
    public static final int JP2_COLR = 1668246642;
    public static final int JP2_JP2C = 1785737827;
    public static final int JP2_URL = 1970433056;
    public static final int JP2_DBTL = 1685348972;
    public static final int JP2_BPCC = 1651532643;
    public static final int JP2_JP2 = 1785737760;
    InputStream inp;
    int boxLength;
    int boxType;
    
    Jpeg2000(final Image image) {
        super(image);
    }
    
    public Jpeg2000(final URL url) throws BadElementException, IOException {
        super(url);
        this.processParameters();
    }
    
    public Jpeg2000(final byte[] img) throws BadElementException, IOException {
        super((URL)null);
        this.rawData = img;
        this.originalData = img;
        this.processParameters();
    }
    
    public Jpeg2000(final byte[] img, final float width, final float height) throws BadElementException, IOException {
        this(img);
        this.scaledWidth = width;
        this.scaledHeight = height;
    }
    
    private int cio_read(final int n) throws IOException {
        int v = 0;
        for (int i = n - 1; i >= 0; --i) {
            v += this.inp.read() << (i << 3);
        }
        return v;
    }
    
    public void jp2_read_boxhdr() throws IOException {
        this.boxLength = this.cio_read(4);
        this.boxType = this.cio_read(4);
        if (this.boxLength == 1) {
            if (this.cio_read(4) != 0) {
                throw new IOException(MessageLocalization.getComposedMessage("cannot.handle.box.sizes.higher.than.2.32"));
            }
            this.boxLength = this.cio_read(4);
            if (this.boxLength == 0) {
                throw new IOException(MessageLocalization.getComposedMessage("unsupported.box.size.eq.eq.0"));
            }
        }
        else if (this.boxLength == 0) {
            throw new IOException(MessageLocalization.getComposedMessage("unsupported.box.size.eq.eq.0"));
        }
    }
    
    private void processParameters() throws IOException {
        this.type = 33;
        this.originalType = 8;
        this.inp = null;
        try {
            if (this.rawData == null) {
                this.inp = this.url.openStream();
                final String errorID = this.url.toString();
            }
            else {
                this.inp = new ByteArrayInputStream(this.rawData);
                final String errorID = "Byte array";
            }
            this.boxLength = this.cio_read(4);
            if (this.boxLength == 12) {
                this.boxType = this.cio_read(4);
                if (1783636000 != this.boxType) {
                    throw new IOException(MessageLocalization.getComposedMessage("expected.jp.marker"));
                }
                if (218793738 != this.cio_read(4)) {
                    throw new IOException(MessageLocalization.getComposedMessage("error.with.jp.marker"));
                }
                this.jp2_read_boxhdr();
                if (1718909296 != this.boxType) {
                    throw new IOException(MessageLocalization.getComposedMessage("expected.ftyp.marker"));
                }
                Utilities.skip(this.inp, this.boxLength - 8);
                this.jp2_read_boxhdr();
                do {
                    if (1785737832 != this.boxType) {
                        if (this.boxType == 1785737827) {
                            throw new IOException(MessageLocalization.getComposedMessage("expected.jp2h.marker"));
                        }
                        Utilities.skip(this.inp, this.boxLength - 8);
                        this.jp2_read_boxhdr();
                    }
                } while (1785737832 != this.boxType);
                this.jp2_read_boxhdr();
                if (1768449138 != this.boxType) {
                    throw new IOException(MessageLocalization.getComposedMessage("expected.ihdr.marker"));
                }
                this.setTop(this.scaledHeight = (float)this.cio_read(4));
                this.setRight(this.scaledWidth = (float)this.cio_read(4));
                this.bpc = -1;
            }
            else {
                if (this.boxLength != -11534511) {
                    throw new IOException(MessageLocalization.getComposedMessage("not.a.valid.jpeg2000.file"));
                }
                Utilities.skip(this.inp, 4);
                final int x1 = this.cio_read(4);
                final int y1 = this.cio_read(4);
                final int x2 = this.cio_read(4);
                final int y2 = this.cio_read(4);
                Utilities.skip(this.inp, 16);
                this.colorspace = this.cio_read(2);
                this.bpc = 8;
                this.setTop(this.scaledHeight = (float)(y1 - y2));
                this.setRight(this.scaledWidth = (float)(x1 - x2));
            }
        }
        finally {
            if (this.inp != null) {
                try {
                    this.inp.close();
                }
                catch (final Exception ex) {}
                this.inp = null;
            }
        }
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}
