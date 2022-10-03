package sun.awt.image;

import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.awt.image.IndexColorModel;

public class GifImageDecoder extends ImageDecoder
{
    private static final boolean verbose = false;
    private static final int IMAGESEP = 44;
    private static final int EXBLOCK = 33;
    private static final int EX_GRAPHICS_CONTROL = 249;
    private static final int EX_COMMENT = 254;
    private static final int EX_APPLICATION = 255;
    private static final int TERMINATOR = 59;
    private static final int TRANSPARENCYMASK = 1;
    private static final int INTERLACEMASK = 64;
    private static final int COLORMAPMASK = 128;
    int num_global_colors;
    byte[] global_colormap;
    int trans_pixel;
    IndexColorModel global_model;
    Hashtable props;
    byte[] saved_image;
    IndexColorModel saved_model;
    int global_width;
    int global_height;
    int global_bgpixel;
    GifFrame curframe;
    private static final int normalflags = 30;
    private static final int interlaceflags = 29;
    private short[] prefix;
    private byte[] suffix;
    private byte[] outCode;
    
    public GifImageDecoder(final InputStreamImageSource inputStreamImageSource, final InputStream inputStream) {
        super(inputStreamImageSource, inputStream);
        this.trans_pixel = -1;
        this.props = new Hashtable();
        this.prefix = new short[4096];
        this.suffix = new byte[4096];
        this.outCode = new byte[4097];
    }
    
    private static void error(final String s) throws ImageFormatException {
        throw new ImageFormatException(s);
    }
    
    private int readBytes(final byte[] array, int n, int i) {
        while (i > 0) {
            try {
                final int read = this.input.read(array, n, i);
                if (read >= 0) {
                    n += read;
                    i -= read;
                    continue;
                }
            }
            catch (final IOException ex) {}
            break;
        }
        return i;
    }
    
    private static final int ExtractByte(final byte[] array, final int n) {
        return array[n] & 0xFF;
    }
    
    private static final int ExtractWord(final byte[] array, final int n) {
        return (array[n] & 0xFF) | (array[n + 1] & 0xFF) << 8;
    }
    
    @Override
    public void produceImage() throws IOException, ImageFormatException {
        try {
            this.readHeader();
            int n = 0;
            int n2 = 0;
            int extractWord = -1;
            int n3 = 0;
            int n4 = -1;
            int n5 = 0;
            int n6 = 0;
        Label_0595:
            while (!this.aborted) {
                switch (this.input.read()) {
                    case 33: {
                        final int read;
                        switch (read = this.input.read()) {
                            case 249: {
                                final byte[] array = new byte[6];
                                if (this.readBytes(array, 0, 6) != 0) {
                                    return;
                                }
                                if (array[0] != 4 || array[5] != 0) {
                                    return;
                                }
                                n4 = ExtractWord(array, 2) * 10;
                                if (n4 > 0 && n6 == 0) {
                                    n6 = 1;
                                    ImageFetcher.startingAnimation();
                                }
                                n3 = (array[1] >> 2 & 0x7);
                                if ((array[1] & 0x1) != 0x0) {
                                    this.trans_pixel = ExtractByte(array, 4);
                                    continue;
                                }
                                this.trans_pixel = -1;
                                continue;
                            }
                            default: {
                                int n7 = 0;
                                String string = "";
                                while (true) {
                                    final int read2 = this.input.read();
                                    if (read2 <= 0) {
                                        if (read == 254) {
                                            this.props.put("comment", string);
                                        }
                                        if (n7 != 0 && n6 == 0) {
                                            n6 = 1;
                                            ImageFetcher.startingAnimation();
                                            continue Label_0595;
                                        }
                                        continue Label_0595;
                                    }
                                    else {
                                        final byte[] array2 = new byte[read2];
                                        if (this.readBytes(array2, 0, read2) != 0) {
                                            return;
                                        }
                                        if (read == 254) {
                                            string += new String(array2, 0);
                                        }
                                        else {
                                            if (read != 255) {
                                                continue;
                                            }
                                            if (n7 != 0) {
                                                if (read2 == 3 && array2[0] == 1) {
                                                    if (n5 != 0) {
                                                        ExtractWord(array2, 1);
                                                    }
                                                    else {
                                                        extractWord = ExtractWord(array2, 1);
                                                        n5 = 1;
                                                    }
                                                }
                                                else {
                                                    n7 = 0;
                                                }
                                            }
                                            if (!"NETSCAPE2.0".equals(new String(array2, 0))) {
                                                continue;
                                            }
                                            n7 = 1;
                                        }
                                    }
                                }
                                break;
                            }
                            case -1: {
                                return;
                            }
                        }
                        break;
                    }
                    case 44: {
                        if (n6 == 0) {
                            this.input.mark(0);
                        }
                        try {
                            if (!this.readImage(n == 0, n3, n4)) {
                                return;
                            }
                        }
                        catch (final Exception ex) {
                            return;
                        }
                        ++n2;
                        ++n;
                        continue;
                    }
                    default: {
                        if (n2 == 0) {
                            return;
                        }
                    }
                    case 59: {
                        Label_0583: {
                            if (extractWord != 0) {
                                if (extractWord-- < 0) {
                                    break Label_0583;
                                }
                            }
                            try {
                                if (this.curframe != null) {
                                    this.curframe.dispose();
                                    this.curframe = null;
                                }
                                this.input.reset();
                                this.saved_image = null;
                                this.saved_model = null;
                                n2 = 0;
                                continue;
                            }
                            catch (final IOException ex2) {
                                return;
                            }
                        }
                        this.imageComplete(3, true);
                        return;
                    }
                }
            }
        }
        finally {
            this.close();
        }
    }
    
    private void readHeader() throws IOException, ImageFormatException {
        final byte[] array = new byte[13];
        if (this.readBytes(array, 0, 13) != 0) {
            throw new IOException();
        }
        if (array[0] != 71 || array[1] != 73 || array[2] != 70) {
            error("not a GIF file.");
        }
        this.global_width = ExtractWord(array, 6);
        this.global_height = ExtractWord(array, 8);
        final int extractByte = ExtractByte(array, 10);
        if ((extractByte & 0x80) == 0x0) {
            this.num_global_colors = 2;
            this.global_bgpixel = 0;
            this.global_colormap = new byte[6];
            final byte[] global_colormap = this.global_colormap;
            final int n = 0;
            final byte[] global_colormap2 = this.global_colormap;
            final int n2 = 1;
            final byte[] global_colormap3 = this.global_colormap;
            final int n3 = 2;
            final byte b = 0;
            global_colormap3[n3] = b;
            global_colormap[n] = (global_colormap2[n2] = b);
            final byte[] global_colormap4 = this.global_colormap;
            final int n4 = 3;
            final byte[] global_colormap5 = this.global_colormap;
            final int n5 = 4;
            final byte[] global_colormap6 = this.global_colormap;
            final int n6 = 5;
            final byte b2 = -1;
            global_colormap6[n6] = b2;
            global_colormap4[n4] = (global_colormap5[n5] = b2);
        }
        else {
            this.num_global_colors = 1 << (extractByte & 0x7) + 1;
            this.global_bgpixel = ExtractByte(array, 11);
            if (array[12] != 0) {
                this.props.put("aspectratio", "" + (ExtractByte(array, 12) + 15) / 64.0);
            }
            this.global_colormap = new byte[this.num_global_colors * 3];
            if (this.readBytes(this.global_colormap, 0, this.num_global_colors * 3) != 0) {
                throw new IOException();
            }
        }
        this.input.mark(Integer.MAX_VALUE);
    }
    
    private static native void initIDs();
    
    private native boolean parseImage(final int p0, final int p1, final int p2, final int p3, final boolean p4, final int p5, final byte[] p6, final byte[] p7, final IndexColorModel p8);
    
    private int sendPixels(final int n, int n2, int n3, int n4, final byte[] array, final ColorModel colorModel) {
        if (n2 < 0) {
            n4 += n2;
            n2 = 0;
        }
        if (n2 + n4 > this.global_height) {
            n4 = this.global_height - n2;
        }
        if (n4 <= 0) {
            return 1;
        }
        int n5;
        int n6;
        if (n < 0) {
            n5 = -n;
            n3 += n;
            n6 = 0;
        }
        else {
            n5 = 0;
            n6 = n;
        }
        if (n6 + n3 > this.global_width) {
            n3 = this.global_width - n6;
        }
        if (n3 <= 0) {
            return 1;
        }
        final int n7 = n5 + n3;
        int n8 = n2 * this.global_width + n6;
        final boolean b = this.curframe.disposal_method == 1;
        if (this.trans_pixel >= 0 && !this.curframe.initialframe) {
            if (this.saved_image == null || !colorModel.equals(this.saved_model)) {
                int n9 = -1;
                int n10 = 1;
                for (int i = n5; i < n7; ++i, ++n8) {
                    final byte b2 = array[i];
                    if ((b2 & 0xFF) == this.trans_pixel) {
                        if (n9 >= 0) {
                            n10 = this.setPixels(n + n9, n2, i - n9, 1, colorModel, array, n9, 0);
                            if (n10 == 0) {
                                break;
                            }
                        }
                        n9 = -1;
                    }
                    else {
                        if (n9 < 0) {
                            n9 = i;
                        }
                        if (b) {
                            this.saved_image[n8] = b2;
                        }
                    }
                }
                if (n9 >= 0) {
                    n10 = this.setPixels(n + n9, n2, n7 - n9, 1, colorModel, array, n9, 0);
                }
                return n10;
            }
            for (int j = n5; j < n7; ++j, ++n8) {
                final byte b3 = array[j];
                if ((b3 & 0xFF) == this.trans_pixel) {
                    array[j] = this.saved_image[n8];
                }
                else if (b) {
                    this.saved_image[n8] = b3;
                }
            }
        }
        else if (b) {
            System.arraycopy(array, n5, this.saved_image, n8, n3);
        }
        return this.setPixels(n6, n2, n3, n4, colorModel, array, n5, 0);
    }
    
    private boolean readImage(final boolean b, final int n, final int n2) throws IOException {
        if (this.curframe != null && !this.curframe.dispose()) {
            this.abort();
            return false;
        }
        final byte[] array = new byte[259];
        if (this.readBytes(array, 0, 10) != 0) {
            throw new IOException();
        }
        final int extractWord = ExtractWord(array, 0);
        final int extractWord2 = ExtractWord(array, 2);
        int extractWord3 = ExtractWord(array, 4);
        int extractWord4 = ExtractWord(array, 6);
        if (extractWord3 == 0 && this.global_width != 0) {
            extractWord3 = this.global_width - extractWord;
        }
        if (extractWord4 == 0 && this.global_height != 0) {
            extractWord4 = this.global_height - extractWord2;
        }
        final boolean b2 = (array[8] & 0x40) != 0x0;
        IndexColorModel global_model = this.global_model;
        if ((array[8] & 0x80) != 0x0) {
            int n3 = 1 << (array[8] & 0x7) + 1;
            byte[] grow_colormap = new byte[n3 * 3];
            grow_colormap[0] = array[9];
            if (this.readBytes(grow_colormap, 1, n3 * 3 - 1) != 0) {
                throw new IOException();
            }
            if (this.readBytes(array, 9, 1) != 0) {
                throw new IOException();
            }
            if (this.trans_pixel >= n3) {
                n3 = this.trans_pixel + 1;
                grow_colormap = grow_colormap(grow_colormap, n3);
            }
            global_model = new IndexColorModel(8, n3, grow_colormap, 0, false, this.trans_pixel);
        }
        else if (global_model == null || this.trans_pixel != global_model.getTransparentPixel()) {
            if (this.trans_pixel >= this.num_global_colors) {
                this.num_global_colors = this.trans_pixel + 1;
                this.global_colormap = grow_colormap(this.global_colormap, this.num_global_colors);
            }
            global_model = new IndexColorModel(8, this.num_global_colors, this.global_colormap, 0, false, this.trans_pixel);
            this.global_model = global_model;
        }
        if (b) {
            if (this.global_width == 0) {
                this.global_width = extractWord3;
            }
            if (this.global_height == 0) {
                this.global_height = extractWord4;
            }
            this.setDimensions(this.global_width, this.global_height);
            this.setProperties(this.props);
            this.setColorModel(global_model);
            this.headerComplete();
        }
        if (n == 1 && this.saved_image == null) {
            this.saved_image = new byte[this.global_width * this.global_height];
            if (extractWord4 < this.global_height && global_model != null) {
                final byte b3 = (byte)global_model.getTransparentPixel();
                if (b3 >= 0) {
                    final byte[] array2 = new byte[this.global_width];
                    for (int i = 0; i < this.global_width; ++i) {
                        array2[i] = b3;
                    }
                    this.setPixels(0, 0, this.global_width, extractWord2, global_model, array2, 0, 0);
                    this.setPixels(0, extractWord2 + extractWord4, this.global_width, this.global_height - extractWord4 - extractWord2, global_model, array2, 0, 0);
                }
            }
        }
        this.setHints(b2 ? 29 : 30);
        this.curframe = new GifFrame(this, n, n2, this.curframe == null, global_model, extractWord, extractWord2, extractWord3, extractWord4);
        final byte[] array3 = new byte[extractWord3];
        final int extractByte = ExtractByte(array, 9);
        if (extractByte >= 12) {
            return false;
        }
        final boolean image = this.parseImage(extractWord, extractWord2, extractWord3, extractWord4, b2, extractByte, array, array3, global_model);
        if (!image) {
            this.abort();
        }
        return image;
    }
    
    public static byte[] grow_colormap(final byte[] array, final int n) {
        final byte[] array2 = new byte[n * 3];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    static {
        NativeLibLoader.loadLibraries();
        initIDs();
    }
}
