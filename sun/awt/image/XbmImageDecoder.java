package sun.awt.image;

import java.io.IOException;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class XbmImageDecoder extends ImageDecoder
{
    private static byte[] XbmColormap;
    private static int XbmHints;
    
    public XbmImageDecoder(final InputStreamImageSource inputStreamImageSource, final InputStream inputStream) {
        super(inputStreamImageSource, inputStream);
        if (!(this.input instanceof BufferedInputStream)) {
            this.input = new BufferedInputStream(this.input, 80);
        }
    }
    
    private static void error(final String s) throws ImageFormatException {
        throw new ImageFormatException(s);
    }
    
    @Override
    public void produceImage() throws IOException, ImageFormatException {
        final char[] array = new char[80];
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 1;
        byte[] array2 = null;
        ColorModel colorModel = null;
        int read;
        while (!this.aborted && (read = this.input.read()) != -1) {
            if ((97 <= read && read <= 122) || (65 <= read && read <= 90) || (48 <= read && read <= 57) || read == 35 || read == 95) {
                if (n >= 78) {
                    continue;
                }
                array[n++] = (char)read;
            }
            else {
                if (n <= 0) {
                    continue;
                }
                final int n8 = n;
                n = 0;
                if (n7 != 0) {
                    if (n8 != 7 || array[0] != '#' || array[1] != 'd' || array[2] != 'e' || array[3] != 'f' || array[4] != 'i' || array[5] != 'n' || array[6] != 'e') {
                        error("Not an XBM file");
                    }
                    n7 = 0;
                }
                if (array[n8 - 1] == 'h') {
                    n2 = 1;
                }
                else if (array[n8 - 1] == 't' && n8 > 1 && array[n8 - 2] == 'h') {
                    n2 = 2;
                }
                else if (n8 > 2 && n2 < 0 && array[0] == '0' && array[1] == 'x') {
                    int n9 = 0;
                    for (int i = 2; i < n8; ++i) {
                        final char c = array[i];
                        int n10;
                        if ('0' <= c && c <= '9') {
                            n10 = c - '0';
                        }
                        else if ('A' <= c && c <= 'Z') {
                            n10 = c - 'A' + 10;
                        }
                        else if ('a' <= c && c <= 'z') {
                            n10 = c - 'a' + 10;
                        }
                        else {
                            n10 = 0;
                        }
                        n9 = n9 * 16 + n10;
                    }
                    for (int j = 1; j <= 128; j <<= 1) {
                        if (n5 < n4) {
                            if ((n9 & j) != 0x0) {
                                array2[n5] = 1;
                            }
                            else {
                                array2[n5] = 0;
                            }
                        }
                        ++n5;
                    }
                    if (n5 < n4) {
                        continue;
                    }
                    if (this.setPixels(0, n6, n4, 1, colorModel, array2, 0, n4) <= 0) {
                        return;
                    }
                    n5 = 0;
                    if (n6++ >= n3) {
                        break;
                    }
                    continue;
                }
                else {
                    int n11 = 0;
                    for (int k = 0; k < n8; ++k) {
                        final char c2;
                        if ('0' > (c2 = array[k]) || c2 > '9') {
                            n11 = -1;
                            break;
                        }
                        n11 = n11 * 10 + c2 - 48;
                    }
                    if (n11 <= 0 || n2 <= 0) {
                        continue;
                    }
                    if (n2 == 1) {
                        n4 = n11;
                    }
                    else {
                        n3 = n11;
                    }
                    if (n4 == 0 || n3 == 0) {
                        n2 = 0;
                    }
                    else {
                        colorModel = new IndexColorModel(8, 2, XbmImageDecoder.XbmColormap, 0, false, 0);
                        this.setDimensions(n4, n3);
                        this.setColorModel(colorModel);
                        this.setHints(XbmImageDecoder.XbmHints);
                        this.headerComplete();
                        array2 = new byte[n4];
                        n2 = -1;
                    }
                }
            }
        }
        this.input.close();
        this.imageComplete(3, true);
    }
    
    static {
        XbmImageDecoder.XbmColormap = new byte[] { -1, -1, -1, 0, 0, 0 };
        XbmImageDecoder.XbmHints = 30;
    }
}
