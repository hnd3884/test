package javax.print.attribute.standard;

import javax.print.attribute.EnumSyntax;

public class MediaSizeName extends Media
{
    private static final long serialVersionUID = 2778798329756942747L;
    public static final MediaSizeName ISO_A0;
    public static final MediaSizeName ISO_A1;
    public static final MediaSizeName ISO_A2;
    public static final MediaSizeName ISO_A3;
    public static final MediaSizeName ISO_A4;
    public static final MediaSizeName ISO_A5;
    public static final MediaSizeName ISO_A6;
    public static final MediaSizeName ISO_A7;
    public static final MediaSizeName ISO_A8;
    public static final MediaSizeName ISO_A9;
    public static final MediaSizeName ISO_A10;
    public static final MediaSizeName ISO_B0;
    public static final MediaSizeName ISO_B1;
    public static final MediaSizeName ISO_B2;
    public static final MediaSizeName ISO_B3;
    public static final MediaSizeName ISO_B4;
    public static final MediaSizeName ISO_B5;
    public static final MediaSizeName ISO_B6;
    public static final MediaSizeName ISO_B7;
    public static final MediaSizeName ISO_B8;
    public static final MediaSizeName ISO_B9;
    public static final MediaSizeName ISO_B10;
    public static final MediaSizeName JIS_B0;
    public static final MediaSizeName JIS_B1;
    public static final MediaSizeName JIS_B2;
    public static final MediaSizeName JIS_B3;
    public static final MediaSizeName JIS_B4;
    public static final MediaSizeName JIS_B5;
    public static final MediaSizeName JIS_B6;
    public static final MediaSizeName JIS_B7;
    public static final MediaSizeName JIS_B8;
    public static final MediaSizeName JIS_B9;
    public static final MediaSizeName JIS_B10;
    public static final MediaSizeName ISO_C0;
    public static final MediaSizeName ISO_C1;
    public static final MediaSizeName ISO_C2;
    public static final MediaSizeName ISO_C3;
    public static final MediaSizeName ISO_C4;
    public static final MediaSizeName ISO_C5;
    public static final MediaSizeName ISO_C6;
    public static final MediaSizeName NA_LETTER;
    public static final MediaSizeName NA_LEGAL;
    public static final MediaSizeName EXECUTIVE;
    public static final MediaSizeName LEDGER;
    public static final MediaSizeName TABLOID;
    public static final MediaSizeName INVOICE;
    public static final MediaSizeName FOLIO;
    public static final MediaSizeName QUARTO;
    public static final MediaSizeName JAPANESE_POSTCARD;
    public static final MediaSizeName JAPANESE_DOUBLE_POSTCARD;
    public static final MediaSizeName A;
    public static final MediaSizeName B;
    public static final MediaSizeName C;
    public static final MediaSizeName D;
    public static final MediaSizeName E;
    public static final MediaSizeName ISO_DESIGNATED_LONG;
    public static final MediaSizeName ITALY_ENVELOPE;
    public static final MediaSizeName MONARCH_ENVELOPE;
    public static final MediaSizeName PERSONAL_ENVELOPE;
    public static final MediaSizeName NA_NUMBER_9_ENVELOPE;
    public static final MediaSizeName NA_NUMBER_10_ENVELOPE;
    public static final MediaSizeName NA_NUMBER_11_ENVELOPE;
    public static final MediaSizeName NA_NUMBER_12_ENVELOPE;
    public static final MediaSizeName NA_NUMBER_14_ENVELOPE;
    public static final MediaSizeName NA_6X9_ENVELOPE;
    public static final MediaSizeName NA_7X9_ENVELOPE;
    public static final MediaSizeName NA_9X11_ENVELOPE;
    public static final MediaSizeName NA_9X12_ENVELOPE;
    public static final MediaSizeName NA_10X13_ENVELOPE;
    public static final MediaSizeName NA_10X14_ENVELOPE;
    public static final MediaSizeName NA_10X15_ENVELOPE;
    public static final MediaSizeName NA_5X7;
    public static final MediaSizeName NA_8X10;
    private static final String[] myStringTable;
    private static final MediaSizeName[] myEnumValueTable;
    
    protected MediaSizeName(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return MediaSizeName.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return MediaSizeName.myEnumValueTable.clone();
    }
    
    static {
        ISO_A0 = new MediaSizeName(0);
        ISO_A1 = new MediaSizeName(1);
        ISO_A2 = new MediaSizeName(2);
        ISO_A3 = new MediaSizeName(3);
        ISO_A4 = new MediaSizeName(4);
        ISO_A5 = new MediaSizeName(5);
        ISO_A6 = new MediaSizeName(6);
        ISO_A7 = new MediaSizeName(7);
        ISO_A8 = new MediaSizeName(8);
        ISO_A9 = new MediaSizeName(9);
        ISO_A10 = new MediaSizeName(10);
        ISO_B0 = new MediaSizeName(11);
        ISO_B1 = new MediaSizeName(12);
        ISO_B2 = new MediaSizeName(13);
        ISO_B3 = new MediaSizeName(14);
        ISO_B4 = new MediaSizeName(15);
        ISO_B5 = new MediaSizeName(16);
        ISO_B6 = new MediaSizeName(17);
        ISO_B7 = new MediaSizeName(18);
        ISO_B8 = new MediaSizeName(19);
        ISO_B9 = new MediaSizeName(20);
        ISO_B10 = new MediaSizeName(21);
        JIS_B0 = new MediaSizeName(22);
        JIS_B1 = new MediaSizeName(23);
        JIS_B2 = new MediaSizeName(24);
        JIS_B3 = new MediaSizeName(25);
        JIS_B4 = new MediaSizeName(26);
        JIS_B5 = new MediaSizeName(27);
        JIS_B6 = new MediaSizeName(28);
        JIS_B7 = new MediaSizeName(29);
        JIS_B8 = new MediaSizeName(30);
        JIS_B9 = new MediaSizeName(31);
        JIS_B10 = new MediaSizeName(32);
        ISO_C0 = new MediaSizeName(33);
        ISO_C1 = new MediaSizeName(34);
        ISO_C2 = new MediaSizeName(35);
        ISO_C3 = new MediaSizeName(36);
        ISO_C4 = new MediaSizeName(37);
        ISO_C5 = new MediaSizeName(38);
        ISO_C6 = new MediaSizeName(39);
        NA_LETTER = new MediaSizeName(40);
        NA_LEGAL = new MediaSizeName(41);
        EXECUTIVE = new MediaSizeName(42);
        LEDGER = new MediaSizeName(43);
        TABLOID = new MediaSizeName(44);
        INVOICE = new MediaSizeName(45);
        FOLIO = new MediaSizeName(46);
        QUARTO = new MediaSizeName(47);
        JAPANESE_POSTCARD = new MediaSizeName(48);
        JAPANESE_DOUBLE_POSTCARD = new MediaSizeName(49);
        A = new MediaSizeName(50);
        B = new MediaSizeName(51);
        C = new MediaSizeName(52);
        D = new MediaSizeName(53);
        E = new MediaSizeName(54);
        ISO_DESIGNATED_LONG = new MediaSizeName(55);
        ITALY_ENVELOPE = new MediaSizeName(56);
        MONARCH_ENVELOPE = new MediaSizeName(57);
        PERSONAL_ENVELOPE = new MediaSizeName(58);
        NA_NUMBER_9_ENVELOPE = new MediaSizeName(59);
        NA_NUMBER_10_ENVELOPE = new MediaSizeName(60);
        NA_NUMBER_11_ENVELOPE = new MediaSizeName(61);
        NA_NUMBER_12_ENVELOPE = new MediaSizeName(62);
        NA_NUMBER_14_ENVELOPE = new MediaSizeName(63);
        NA_6X9_ENVELOPE = new MediaSizeName(64);
        NA_7X9_ENVELOPE = new MediaSizeName(65);
        NA_9X11_ENVELOPE = new MediaSizeName(66);
        NA_9X12_ENVELOPE = new MediaSizeName(67);
        NA_10X13_ENVELOPE = new MediaSizeName(68);
        NA_10X14_ENVELOPE = new MediaSizeName(69);
        NA_10X15_ENVELOPE = new MediaSizeName(70);
        NA_5X7 = new MediaSizeName(71);
        NA_8X10 = new MediaSizeName(72);
        myStringTable = new String[] { "iso-a0", "iso-a1", "iso-a2", "iso-a3", "iso-a4", "iso-a5", "iso-a6", "iso-a7", "iso-a8", "iso-a9", "iso-a10", "iso-b0", "iso-b1", "iso-b2", "iso-b3", "iso-b4", "iso-b5", "iso-b6", "iso-b7", "iso-b8", "iso-b9", "iso-b10", "jis-b0", "jis-b1", "jis-b2", "jis-b3", "jis-b4", "jis-b5", "jis-b6", "jis-b7", "jis-b8", "jis-b9", "jis-b10", "iso-c0", "iso-c1", "iso-c2", "iso-c3", "iso-c4", "iso-c5", "iso-c6", "na-letter", "na-legal", "executive", "ledger", "tabloid", "invoice", "folio", "quarto", "japanese-postcard", "oufuko-postcard", "a", "b", "c", "d", "e", "iso-designated-long", "italian-envelope", "monarch-envelope", "personal-envelope", "na-number-9-envelope", "na-number-10-envelope", "na-number-11-envelope", "na-number-12-envelope", "na-number-14-envelope", "na-6x9-envelope", "na-7x9-envelope", "na-9x11-envelope", "na-9x12-envelope", "na-10x13-envelope", "na-10x14-envelope", "na-10x15-envelope", "na-5x7", "na-8x10" };
        myEnumValueTable = new MediaSizeName[] { MediaSizeName.ISO_A0, MediaSizeName.ISO_A1, MediaSizeName.ISO_A2, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_A6, MediaSizeName.ISO_A7, MediaSizeName.ISO_A8, MediaSizeName.ISO_A9, MediaSizeName.ISO_A10, MediaSizeName.ISO_B0, MediaSizeName.ISO_B1, MediaSizeName.ISO_B2, MediaSizeName.ISO_B3, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, MediaSizeName.ISO_B7, MediaSizeName.ISO_B8, MediaSizeName.ISO_B9, MediaSizeName.ISO_B10, MediaSizeName.JIS_B0, MediaSizeName.JIS_B1, MediaSizeName.JIS_B2, MediaSizeName.JIS_B3, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, MediaSizeName.JIS_B6, MediaSizeName.JIS_B7, MediaSizeName.JIS_B8, MediaSizeName.JIS_B9, MediaSizeName.JIS_B10, MediaSizeName.ISO_C0, MediaSizeName.ISO_C1, MediaSizeName.ISO_C2, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, MediaSizeName.ISO_C5, MediaSizeName.ISO_C6, MediaSizeName.NA_LETTER, MediaSizeName.NA_LEGAL, MediaSizeName.EXECUTIVE, MediaSizeName.LEDGER, MediaSizeName.TABLOID, MediaSizeName.INVOICE, MediaSizeName.FOLIO, MediaSizeName.QUARTO, MediaSizeName.JAPANESE_POSTCARD, MediaSizeName.JAPANESE_DOUBLE_POSTCARD, MediaSizeName.A, MediaSizeName.B, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, MediaSizeName.NA_NUMBER_14_ENVELOPE, MediaSizeName.NA_6X9_ENVELOPE, MediaSizeName.NA_7X9_ENVELOPE, MediaSizeName.NA_9X11_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, MediaSizeName.NA_10X13_ENVELOPE, MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.NA_10X15_ENVELOPE, MediaSizeName.NA_5X7, MediaSizeName.NA_8X10 };
    }
}
