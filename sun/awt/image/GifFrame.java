package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

class GifFrame
{
    private static final boolean verbose = false;
    private static IndexColorModel trans_model;
    static final int DISPOSAL_NONE = 0;
    static final int DISPOSAL_SAVE = 1;
    static final int DISPOSAL_BGCOLOR = 2;
    static final int DISPOSAL_PREVIOUS = 3;
    GifImageDecoder decoder;
    int disposal_method;
    int delay;
    IndexColorModel model;
    int x;
    int y;
    int width;
    int height;
    boolean initialframe;
    
    public GifFrame(final GifImageDecoder decoder, final int disposal_method, final int delay, final boolean initialframe, final IndexColorModel model, final int x, final int y, final int width, final int height) {
        this.decoder = decoder;
        this.disposal_method = disposal_method;
        this.delay = delay;
        this.model = model;
        this.initialframe = initialframe;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    private void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final byte[] array, final int n5, final int n6) {
        this.decoder.setPixels(n, n2, n3, n4, colorModel, array, n5, n6);
    }
    
    public boolean dispose() {
        if (this.decoder.imageComplete(2, false) == 0) {
            return false;
        }
        Label_0038: {
            if (this.delay > 0) {
                try {
                    Thread.sleep(this.delay);
                    break Label_0038;
                }
                catch (final InterruptedException ex) {
                    return false;
                }
            }
            Thread.yield();
        }
        final int global_width = this.decoder.global_width;
        final int global_height = this.decoder.global_height;
        if (this.x < 0) {
            this.width += this.x;
            this.x = 0;
        }
        if (this.x + this.width > global_width) {
            this.width = global_width - this.x;
        }
        if (this.width <= 0) {
            this.disposal_method = 0;
        }
        else {
            if (this.y < 0) {
                this.height += this.y;
                this.y = 0;
            }
            if (this.y + this.height > global_height) {
                this.height = global_height - this.y;
            }
            if (this.height <= 0) {
                this.disposal_method = 0;
            }
        }
        switch (this.disposal_method) {
            case 3: {
                final byte[] saved_image = this.decoder.saved_image;
                final IndexColorModel saved_model = this.decoder.saved_model;
                if (saved_image != null) {
                    this.setPixels(this.x, this.y, this.width, this.height, saved_model, saved_image, this.y * global_width + this.x, global_width);
                    break;
                }
                break;
            }
            case 2: {
                byte b;
                if (this.model.getTransparentPixel() < 0) {
                    this.model = GifFrame.trans_model;
                    if (this.model == null) {
                        this.model = new IndexColorModel(8, 1, new byte[4], 0, true);
                        GifFrame.trans_model = this.model;
                    }
                    b = 0;
                }
                else {
                    b = (byte)this.model.getTransparentPixel();
                }
                final byte[] array = new byte[this.width];
                if (b != 0) {
                    for (int i = 0; i < this.width; ++i) {
                        array[i] = b;
                    }
                }
                if (this.decoder.saved_image != null) {
                    for (int j = 0; j < global_width * global_height; ++j) {
                        this.decoder.saved_image[j] = b;
                    }
                }
                this.setPixels(this.x, this.y, this.width, this.height, this.model, array, 0, 0);
                break;
            }
            case 1: {
                this.decoder.saved_model = this.model;
                break;
            }
        }
        return true;
    }
}
