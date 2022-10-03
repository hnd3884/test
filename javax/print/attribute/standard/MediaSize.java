package javax.print.attribute.standard;

import java.util.Vector;
import java.util.HashMap;
import javax.print.attribute.Attribute;
import javax.print.attribute.Size2DSyntax;

public class MediaSize extends Size2DSyntax implements Attribute
{
    private static final long serialVersionUID = -1967958664615414771L;
    private MediaSizeName mediaName;
    private static HashMap mediaMap;
    private static Vector sizeVector;
    
    public MediaSize(final float n, final float n2, final int n3) {
        super(n, n2, n3);
        if (n > n2) {
            throw new IllegalArgumentException("X dimension > Y dimension");
        }
        MediaSize.sizeVector.add(this);
    }
    
    public MediaSize(final int n, final int n2, final int n3) {
        super(n, n2, n3);
        if (n > n2) {
            throw new IllegalArgumentException("X dimension > Y dimension");
        }
        MediaSize.sizeVector.add(this);
    }
    
    public MediaSize(final float n, final float n2, final int n3, final MediaSizeName mediaName) {
        super(n, n2, n3);
        if (n > n2) {
            throw new IllegalArgumentException("X dimension > Y dimension");
        }
        if (mediaName != null && MediaSize.mediaMap.get(mediaName) == null) {
            this.mediaName = mediaName;
            MediaSize.mediaMap.put(this.mediaName, this);
        }
        MediaSize.sizeVector.add(this);
    }
    
    public MediaSize(final int n, final int n2, final int n3, final MediaSizeName mediaName) {
        super(n, n2, n3);
        if (n > n2) {
            throw new IllegalArgumentException("X dimension > Y dimension");
        }
        if (mediaName != null && MediaSize.mediaMap.get(mediaName) == null) {
            this.mediaName = mediaName;
            MediaSize.mediaMap.put(this.mediaName, this);
        }
        MediaSize.sizeVector.add(this);
    }
    
    public MediaSizeName getMediaSizeName() {
        return this.mediaName;
    }
    
    public static MediaSize getMediaSizeForName(final MediaSizeName mediaSizeName) {
        return MediaSize.mediaMap.get(mediaSizeName);
    }
    
    public static MediaSizeName findMedia(final float n, final float n2, final int n3) {
        MediaSize a4 = ISO.A4;
        if (n <= 0.0f || n2 <= 0.0f || n3 < 1) {
            throw new IllegalArgumentException("args must be +ve values");
        }
        double n4 = n * n + n2 * n2;
        for (int i = 0; i < MediaSize.sizeVector.size(); ++i) {
            final MediaSize mediaSize = MediaSize.sizeVector.elementAt(i);
            final float[] size = mediaSize.getSize(n3);
            if (n == size[0] && n2 == size[1]) {
                a4 = mediaSize;
                break;
            }
            final float n5 = n - size[0];
            final float n6 = n2 - size[1];
            final double n7 = n5 * n5 + n6 * n6;
            if (n7 < n4) {
                n4 = n7;
                a4 = mediaSize;
            }
        }
        return a4.getMediaSizeName();
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof MediaSize;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return MediaSize.class;
    }
    
    @Override
    public final String getName() {
        return "media-size";
    }
    
    static {
        MediaSize.mediaMap = new HashMap(100, 10.0f);
        MediaSize.sizeVector = new Vector(100, 10);
        final MediaSize a4 = ISO.A4;
        final MediaSize b5 = JIS.B5;
        final MediaSize letter = NA.LETTER;
        final MediaSize c = Engineering.C;
        final MediaSize executive = Other.EXECUTIVE;
    }
    
    public static final class ISO
    {
        public static final MediaSize A0;
        public static final MediaSize A1;
        public static final MediaSize A2;
        public static final MediaSize A3;
        public static final MediaSize A4;
        public static final MediaSize A5;
        public static final MediaSize A6;
        public static final MediaSize A7;
        public static final MediaSize A8;
        public static final MediaSize A9;
        public static final MediaSize A10;
        public static final MediaSize B0;
        public static final MediaSize B1;
        public static final MediaSize B2;
        public static final MediaSize B3;
        public static final MediaSize B4;
        public static final MediaSize B5;
        public static final MediaSize B6;
        public static final MediaSize B7;
        public static final MediaSize B8;
        public static final MediaSize B9;
        public static final MediaSize B10;
        public static final MediaSize C3;
        public static final MediaSize C4;
        public static final MediaSize C5;
        public static final MediaSize C6;
        public static final MediaSize DESIGNATED_LONG;
        
        private ISO() {
        }
        
        static {
            A0 = new MediaSize(841, 1189, 1000, MediaSizeName.ISO_A0);
            A1 = new MediaSize(594, 841, 1000, MediaSizeName.ISO_A1);
            A2 = new MediaSize(420, 594, 1000, MediaSizeName.ISO_A2);
            A3 = new MediaSize(297, 420, 1000, MediaSizeName.ISO_A3);
            A4 = new MediaSize(210, 297, 1000, MediaSizeName.ISO_A4);
            A5 = new MediaSize(148, 210, 1000, MediaSizeName.ISO_A5);
            A6 = new MediaSize(105, 148, 1000, MediaSizeName.ISO_A6);
            A7 = new MediaSize(74, 105, 1000, MediaSizeName.ISO_A7);
            A8 = new MediaSize(52, 74, 1000, MediaSizeName.ISO_A8);
            A9 = new MediaSize(37, 52, 1000, MediaSizeName.ISO_A9);
            A10 = new MediaSize(26, 37, 1000, MediaSizeName.ISO_A10);
            B0 = new MediaSize(1000, 1414, 1000, MediaSizeName.ISO_B0);
            B1 = new MediaSize(707, 1000, 1000, MediaSizeName.ISO_B1);
            B2 = new MediaSize(500, 707, 1000, MediaSizeName.ISO_B2);
            B3 = new MediaSize(353, 500, 1000, MediaSizeName.ISO_B3);
            B4 = new MediaSize(250, 353, 1000, MediaSizeName.ISO_B4);
            B5 = new MediaSize(176, 250, 1000, MediaSizeName.ISO_B5);
            B6 = new MediaSize(125, 176, 1000, MediaSizeName.ISO_B6);
            B7 = new MediaSize(88, 125, 1000, MediaSizeName.ISO_B7);
            B8 = new MediaSize(62, 88, 1000, MediaSizeName.ISO_B8);
            B9 = new MediaSize(44, 62, 1000, MediaSizeName.ISO_B9);
            B10 = new MediaSize(31, 44, 1000, MediaSizeName.ISO_B10);
            C3 = new MediaSize(324, 458, 1000, MediaSizeName.ISO_C3);
            C4 = new MediaSize(229, 324, 1000, MediaSizeName.ISO_C4);
            C5 = new MediaSize(162, 229, 1000, MediaSizeName.ISO_C5);
            C6 = new MediaSize(114, 162, 1000, MediaSizeName.ISO_C6);
            DESIGNATED_LONG = new MediaSize(110, 220, 1000, MediaSizeName.ISO_DESIGNATED_LONG);
        }
    }
    
    public static final class JIS
    {
        public static final MediaSize B0;
        public static final MediaSize B1;
        public static final MediaSize B2;
        public static final MediaSize B3;
        public static final MediaSize B4;
        public static final MediaSize B5;
        public static final MediaSize B6;
        public static final MediaSize B7;
        public static final MediaSize B8;
        public static final MediaSize B9;
        public static final MediaSize B10;
        public static final MediaSize CHOU_1;
        public static final MediaSize CHOU_2;
        public static final MediaSize CHOU_3;
        public static final MediaSize CHOU_4;
        public static final MediaSize CHOU_30;
        public static final MediaSize CHOU_40;
        public static final MediaSize KAKU_0;
        public static final MediaSize KAKU_1;
        public static final MediaSize KAKU_2;
        public static final MediaSize KAKU_3;
        public static final MediaSize KAKU_4;
        public static final MediaSize KAKU_5;
        public static final MediaSize KAKU_6;
        public static final MediaSize KAKU_7;
        public static final MediaSize KAKU_8;
        public static final MediaSize KAKU_20;
        public static final MediaSize KAKU_A4;
        public static final MediaSize YOU_1;
        public static final MediaSize YOU_2;
        public static final MediaSize YOU_3;
        public static final MediaSize YOU_4;
        public static final MediaSize YOU_5;
        public static final MediaSize YOU_6;
        public static final MediaSize YOU_7;
        
        private JIS() {
        }
        
        static {
            B0 = new MediaSize(1030, 1456, 1000, MediaSizeName.JIS_B0);
            B1 = new MediaSize(728, 1030, 1000, MediaSizeName.JIS_B1);
            B2 = new MediaSize(515, 728, 1000, MediaSizeName.JIS_B2);
            B3 = new MediaSize(364, 515, 1000, MediaSizeName.JIS_B3);
            B4 = new MediaSize(257, 364, 1000, MediaSizeName.JIS_B4);
            B5 = new MediaSize(182, 257, 1000, MediaSizeName.JIS_B5);
            B6 = new MediaSize(128, 182, 1000, MediaSizeName.JIS_B6);
            B7 = new MediaSize(91, 128, 1000, MediaSizeName.JIS_B7);
            B8 = new MediaSize(64, 91, 1000, MediaSizeName.JIS_B8);
            B9 = new MediaSize(45, 64, 1000, MediaSizeName.JIS_B9);
            B10 = new MediaSize(32, 45, 1000, MediaSizeName.JIS_B10);
            CHOU_1 = new MediaSize(142, 332, 1000);
            CHOU_2 = new MediaSize(119, 277, 1000);
            CHOU_3 = new MediaSize(120, 235, 1000);
            CHOU_4 = new MediaSize(90, 205, 1000);
            CHOU_30 = new MediaSize(92, 235, 1000);
            CHOU_40 = new MediaSize(90, 225, 1000);
            KAKU_0 = new MediaSize(287, 382, 1000);
            KAKU_1 = new MediaSize(270, 382, 1000);
            KAKU_2 = new MediaSize(240, 332, 1000);
            KAKU_3 = new MediaSize(216, 277, 1000);
            KAKU_4 = new MediaSize(197, 267, 1000);
            KAKU_5 = new MediaSize(190, 240, 1000);
            KAKU_6 = new MediaSize(162, 229, 1000);
            KAKU_7 = new MediaSize(142, 205, 1000);
            KAKU_8 = new MediaSize(119, 197, 1000);
            KAKU_20 = new MediaSize(229, 324, 1000);
            KAKU_A4 = new MediaSize(228, 312, 1000);
            YOU_1 = new MediaSize(120, 176, 1000);
            YOU_2 = new MediaSize(114, 162, 1000);
            YOU_3 = new MediaSize(98, 148, 1000);
            YOU_4 = new MediaSize(105, 235, 1000);
            YOU_5 = new MediaSize(95, 217, 1000);
            YOU_6 = new MediaSize(98, 190, 1000);
            YOU_7 = new MediaSize(92, 165, 1000);
        }
    }
    
    public static final class NA
    {
        public static final MediaSize LETTER;
        public static final MediaSize LEGAL;
        public static final MediaSize NA_5X7;
        public static final MediaSize NA_8X10;
        public static final MediaSize NA_NUMBER_9_ENVELOPE;
        public static final MediaSize NA_NUMBER_10_ENVELOPE;
        public static final MediaSize NA_NUMBER_11_ENVELOPE;
        public static final MediaSize NA_NUMBER_12_ENVELOPE;
        public static final MediaSize NA_NUMBER_14_ENVELOPE;
        public static final MediaSize NA_6X9_ENVELOPE;
        public static final MediaSize NA_7X9_ENVELOPE;
        public static final MediaSize NA_9x11_ENVELOPE;
        public static final MediaSize NA_9x12_ENVELOPE;
        public static final MediaSize NA_10x13_ENVELOPE;
        public static final MediaSize NA_10x14_ENVELOPE;
        public static final MediaSize NA_10X15_ENVELOPE;
        
        private NA() {
        }
        
        static {
            LETTER = new MediaSize(8.5f, 11.0f, 25400, MediaSizeName.NA_LETTER);
            LEGAL = new MediaSize(8.5f, 14.0f, 25400, MediaSizeName.NA_LEGAL);
            NA_5X7 = new MediaSize(5, 7, 25400, MediaSizeName.NA_5X7);
            NA_8X10 = new MediaSize(8, 10, 25400, MediaSizeName.NA_8X10);
            NA_NUMBER_9_ENVELOPE = new MediaSize(3.875f, 8.875f, 25400, MediaSizeName.NA_NUMBER_9_ENVELOPE);
            NA_NUMBER_10_ENVELOPE = new MediaSize(4.125f, 9.5f, 25400, MediaSizeName.NA_NUMBER_10_ENVELOPE);
            NA_NUMBER_11_ENVELOPE = new MediaSize(4.5f, 10.375f, 25400, MediaSizeName.NA_NUMBER_11_ENVELOPE);
            NA_NUMBER_12_ENVELOPE = new MediaSize(4.75f, 11.0f, 25400, MediaSizeName.NA_NUMBER_12_ENVELOPE);
            NA_NUMBER_14_ENVELOPE = new MediaSize(5.0f, 11.5f, 25400, MediaSizeName.NA_NUMBER_14_ENVELOPE);
            NA_6X9_ENVELOPE = new MediaSize(6.0f, 9.0f, 25400, MediaSizeName.NA_6X9_ENVELOPE);
            NA_7X9_ENVELOPE = new MediaSize(7.0f, 9.0f, 25400, MediaSizeName.NA_7X9_ENVELOPE);
            NA_9x11_ENVELOPE = new MediaSize(9.0f, 11.0f, 25400, MediaSizeName.NA_9X11_ENVELOPE);
            NA_9x12_ENVELOPE = new MediaSize(9.0f, 12.0f, 25400, MediaSizeName.NA_9X12_ENVELOPE);
            NA_10x13_ENVELOPE = new MediaSize(10.0f, 13.0f, 25400, MediaSizeName.NA_10X13_ENVELOPE);
            NA_10x14_ENVELOPE = new MediaSize(10.0f, 14.0f, 25400, MediaSizeName.NA_10X14_ENVELOPE);
            NA_10X15_ENVELOPE = new MediaSize(10.0f, 15.0f, 25400, MediaSizeName.NA_10X15_ENVELOPE);
        }
    }
    
    public static final class Engineering
    {
        public static final MediaSize A;
        public static final MediaSize B;
        public static final MediaSize C;
        public static final MediaSize D;
        public static final MediaSize E;
        
        private Engineering() {
        }
        
        static {
            A = new MediaSize(8.5f, 11.0f, 25400, MediaSizeName.A);
            B = new MediaSize(11.0f, 17.0f, 25400, MediaSizeName.B);
            C = new MediaSize(17.0f, 22.0f, 25400, MediaSizeName.C);
            D = new MediaSize(22.0f, 34.0f, 25400, MediaSizeName.D);
            E = new MediaSize(34.0f, 44.0f, 25400, MediaSizeName.E);
        }
    }
    
    public static final class Other
    {
        public static final MediaSize EXECUTIVE;
        public static final MediaSize LEDGER;
        public static final MediaSize TABLOID;
        public static final MediaSize INVOICE;
        public static final MediaSize FOLIO;
        public static final MediaSize QUARTO;
        public static final MediaSize ITALY_ENVELOPE;
        public static final MediaSize MONARCH_ENVELOPE;
        public static final MediaSize PERSONAL_ENVELOPE;
        public static final MediaSize JAPANESE_POSTCARD;
        public static final MediaSize JAPANESE_DOUBLE_POSTCARD;
        
        private Other() {
        }
        
        static {
            EXECUTIVE = new MediaSize(7.25f, 10.5f, 25400, MediaSizeName.EXECUTIVE);
            LEDGER = new MediaSize(11.0f, 17.0f, 25400, MediaSizeName.LEDGER);
            TABLOID = new MediaSize(11.0f, 17.0f, 25400, MediaSizeName.TABLOID);
            INVOICE = new MediaSize(5.5f, 8.5f, 25400, MediaSizeName.INVOICE);
            FOLIO = new MediaSize(8.5f, 13.0f, 25400, MediaSizeName.FOLIO);
            QUARTO = new MediaSize(8.5f, 10.83f, 25400, MediaSizeName.QUARTO);
            ITALY_ENVELOPE = new MediaSize(110, 230, 1000, MediaSizeName.ITALY_ENVELOPE);
            MONARCH_ENVELOPE = new MediaSize(3.87f, 7.5f, 25400, MediaSizeName.MONARCH_ENVELOPE);
            PERSONAL_ENVELOPE = new MediaSize(3.625f, 6.5f, 25400, MediaSizeName.PERSONAL_ENVELOPE);
            JAPANESE_POSTCARD = new MediaSize(100, 148, 1000, MediaSizeName.JAPANESE_POSTCARD);
            JAPANESE_DOUBLE_POSTCARD = new MediaSize(148, 200, 1000, MediaSizeName.JAPANESE_DOUBLE_POSTCARD);
        }
    }
}
