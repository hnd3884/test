package java.awt;

import java.util.Locale;

public final class PageAttributes implements Cloneable
{
    private ColorType color;
    private MediaType media;
    private OrientationRequestedType orientationRequested;
    private OriginType origin;
    private PrintQualityType printQuality;
    private int[] printerResolution;
    
    public PageAttributes() {
        this.setColor(ColorType.MONOCHROME);
        this.setMediaToDefault();
        this.setOrientationRequestedToDefault();
        this.setOrigin(OriginType.PHYSICAL);
        this.setPrintQualityToDefault();
        this.setPrinterResolutionToDefault();
    }
    
    public PageAttributes(final PageAttributes pageAttributes) {
        this.set(pageAttributes);
    }
    
    public PageAttributes(final ColorType color, final MediaType media, final OrientationRequestedType orientationRequested, final OriginType origin, final PrintQualityType printQuality, final int[] printerResolution) {
        this.setColor(color);
        this.setMedia(media);
        this.setOrientationRequested(orientationRequested);
        this.setOrigin(origin);
        this.setPrintQuality(printQuality);
        this.setPrinterResolution(printerResolution);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    public void set(final PageAttributes pageAttributes) {
        this.color = pageAttributes.color;
        this.media = pageAttributes.media;
        this.orientationRequested = pageAttributes.orientationRequested;
        this.origin = pageAttributes.origin;
        this.printQuality = pageAttributes.printQuality;
        this.printerResolution = pageAttributes.printerResolution;
    }
    
    public ColorType getColor() {
        return this.color;
    }
    
    public void setColor(final ColorType color) {
        if (color == null) {
            throw new IllegalArgumentException("Invalid value for attribute color");
        }
        this.color = color;
    }
    
    public MediaType getMedia() {
        return this.media;
    }
    
    public void setMedia(final MediaType media) {
        if (media == null) {
            throw new IllegalArgumentException("Invalid value for attribute media");
        }
        this.media = media;
    }
    
    public void setMediaToDefault() {
        final String country = Locale.getDefault().getCountry();
        if (country != null && (country.equals(Locale.US.getCountry()) || country.equals(Locale.CANADA.getCountry()))) {
            this.setMedia(MediaType.NA_LETTER);
        }
        else {
            this.setMedia(MediaType.ISO_A4);
        }
    }
    
    public OrientationRequestedType getOrientationRequested() {
        return this.orientationRequested;
    }
    
    public void setOrientationRequested(final OrientationRequestedType orientationRequested) {
        if (orientationRequested == null) {
            throw new IllegalArgumentException("Invalid value for attribute orientationRequested");
        }
        this.orientationRequested = orientationRequested;
    }
    
    public void setOrientationRequested(final int n) {
        switch (n) {
            case 3: {
                this.setOrientationRequested(OrientationRequestedType.PORTRAIT);
                break;
            }
            case 4: {
                this.setOrientationRequested(OrientationRequestedType.LANDSCAPE);
                break;
            }
            default: {
                this.setOrientationRequested(null);
                break;
            }
        }
    }
    
    public void setOrientationRequestedToDefault() {
        this.setOrientationRequested(OrientationRequestedType.PORTRAIT);
    }
    
    public OriginType getOrigin() {
        return this.origin;
    }
    
    public void setOrigin(final OriginType origin) {
        if (origin == null) {
            throw new IllegalArgumentException("Invalid value for attribute origin");
        }
        this.origin = origin;
    }
    
    public PrintQualityType getPrintQuality() {
        return this.printQuality;
    }
    
    public void setPrintQuality(final PrintQualityType printQuality) {
        if (printQuality == null) {
            throw new IllegalArgumentException("Invalid value for attribute printQuality");
        }
        this.printQuality = printQuality;
    }
    
    public void setPrintQuality(final int n) {
        switch (n) {
            case 3: {
                this.setPrintQuality(PrintQualityType.DRAFT);
                break;
            }
            case 4: {
                this.setPrintQuality(PrintQualityType.NORMAL);
                break;
            }
            case 5: {
                this.setPrintQuality(PrintQualityType.HIGH);
                break;
            }
            default: {
                this.setPrintQuality(null);
                break;
            }
        }
    }
    
    public void setPrintQualityToDefault() {
        this.setPrintQuality(PrintQualityType.NORMAL);
    }
    
    public int[] getPrinterResolution() {
        return new int[] { this.printerResolution[0], this.printerResolution[1], this.printerResolution[2] };
    }
    
    public void setPrinterResolution(final int[] array) {
        if (array == null || array.length != 3 || array[0] <= 0 || array[1] <= 0 || (array[2] != 3 && array[2] != 4)) {
            throw new IllegalArgumentException("Invalid value for attribute printerResolution");
        }
        this.printerResolution = new int[] { array[0], array[1], array[2] };
    }
    
    public void setPrinterResolution(final int n) {
        this.setPrinterResolution(new int[] { n, n, 3 });
    }
    
    public void setPrinterResolutionToDefault() {
        this.setPrinterResolution(72);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PageAttributes)) {
            return false;
        }
        final PageAttributes pageAttributes = (PageAttributes)o;
        return this.color == pageAttributes.color && this.media == pageAttributes.media && this.orientationRequested == pageAttributes.orientationRequested && this.origin == pageAttributes.origin && this.printQuality == pageAttributes.printQuality && this.printerResolution[0] == pageAttributes.printerResolution[0] && this.printerResolution[1] == pageAttributes.printerResolution[1] && this.printerResolution[2] == pageAttributes.printerResolution[2];
    }
    
    @Override
    public int hashCode() {
        return this.color.hashCode() << 31 ^ this.media.hashCode() << 24 ^ this.orientationRequested.hashCode() << 23 ^ this.origin.hashCode() << 22 ^ this.printQuality.hashCode() << 20 ^ this.printerResolution[2] >> 2 << 19 ^ this.printerResolution[1] << 10 ^ this.printerResolution[0];
    }
    
    @Override
    public String toString() {
        return "color=" + this.getColor() + ",media=" + this.getMedia() + ",orientation-requested=" + this.getOrientationRequested() + ",origin=" + this.getOrigin() + ",print-quality=" + this.getPrintQuality() + ",printer-resolution=[" + this.printerResolution[0] + "," + this.printerResolution[1] + "," + this.printerResolution[2] + "]";
    }
    
    public static final class ColorType extends AttributeValue
    {
        private static final int I_COLOR = 0;
        private static final int I_MONOCHROME = 1;
        private static final String[] NAMES;
        public static final ColorType COLOR;
        public static final ColorType MONOCHROME;
        
        private ColorType(final int n) {
            super(n, ColorType.NAMES);
        }
        
        static {
            NAMES = new String[] { "color", "monochrome" };
            COLOR = new ColorType(0);
            MONOCHROME = new ColorType(1);
        }
    }
    
    public static final class MediaType extends AttributeValue
    {
        private static final int I_ISO_4A0 = 0;
        private static final int I_ISO_2A0 = 1;
        private static final int I_ISO_A0 = 2;
        private static final int I_ISO_A1 = 3;
        private static final int I_ISO_A2 = 4;
        private static final int I_ISO_A3 = 5;
        private static final int I_ISO_A4 = 6;
        private static final int I_ISO_A5 = 7;
        private static final int I_ISO_A6 = 8;
        private static final int I_ISO_A7 = 9;
        private static final int I_ISO_A8 = 10;
        private static final int I_ISO_A9 = 11;
        private static final int I_ISO_A10 = 12;
        private static final int I_ISO_B0 = 13;
        private static final int I_ISO_B1 = 14;
        private static final int I_ISO_B2 = 15;
        private static final int I_ISO_B3 = 16;
        private static final int I_ISO_B4 = 17;
        private static final int I_ISO_B5 = 18;
        private static final int I_ISO_B6 = 19;
        private static final int I_ISO_B7 = 20;
        private static final int I_ISO_B8 = 21;
        private static final int I_ISO_B9 = 22;
        private static final int I_ISO_B10 = 23;
        private static final int I_JIS_B0 = 24;
        private static final int I_JIS_B1 = 25;
        private static final int I_JIS_B2 = 26;
        private static final int I_JIS_B3 = 27;
        private static final int I_JIS_B4 = 28;
        private static final int I_JIS_B5 = 29;
        private static final int I_JIS_B6 = 30;
        private static final int I_JIS_B7 = 31;
        private static final int I_JIS_B8 = 32;
        private static final int I_JIS_B9 = 33;
        private static final int I_JIS_B10 = 34;
        private static final int I_ISO_C0 = 35;
        private static final int I_ISO_C1 = 36;
        private static final int I_ISO_C2 = 37;
        private static final int I_ISO_C3 = 38;
        private static final int I_ISO_C4 = 39;
        private static final int I_ISO_C5 = 40;
        private static final int I_ISO_C6 = 41;
        private static final int I_ISO_C7 = 42;
        private static final int I_ISO_C8 = 43;
        private static final int I_ISO_C9 = 44;
        private static final int I_ISO_C10 = 45;
        private static final int I_ISO_DESIGNATED_LONG = 46;
        private static final int I_EXECUTIVE = 47;
        private static final int I_FOLIO = 48;
        private static final int I_INVOICE = 49;
        private static final int I_LEDGER = 50;
        private static final int I_NA_LETTER = 51;
        private static final int I_NA_LEGAL = 52;
        private static final int I_QUARTO = 53;
        private static final int I_A = 54;
        private static final int I_B = 55;
        private static final int I_C = 56;
        private static final int I_D = 57;
        private static final int I_E = 58;
        private static final int I_NA_10X15_ENVELOPE = 59;
        private static final int I_NA_10X14_ENVELOPE = 60;
        private static final int I_NA_10X13_ENVELOPE = 61;
        private static final int I_NA_9X12_ENVELOPE = 62;
        private static final int I_NA_9X11_ENVELOPE = 63;
        private static final int I_NA_7X9_ENVELOPE = 64;
        private static final int I_NA_6X9_ENVELOPE = 65;
        private static final int I_NA_NUMBER_9_ENVELOPE = 66;
        private static final int I_NA_NUMBER_10_ENVELOPE = 67;
        private static final int I_NA_NUMBER_11_ENVELOPE = 68;
        private static final int I_NA_NUMBER_12_ENVELOPE = 69;
        private static final int I_NA_NUMBER_14_ENVELOPE = 70;
        private static final int I_INVITE_ENVELOPE = 71;
        private static final int I_ITALY_ENVELOPE = 72;
        private static final int I_MONARCH_ENVELOPE = 73;
        private static final int I_PERSONAL_ENVELOPE = 74;
        private static final String[] NAMES;
        public static final MediaType ISO_4A0;
        public static final MediaType ISO_2A0;
        public static final MediaType ISO_A0;
        public static final MediaType ISO_A1;
        public static final MediaType ISO_A2;
        public static final MediaType ISO_A3;
        public static final MediaType ISO_A4;
        public static final MediaType ISO_A5;
        public static final MediaType ISO_A6;
        public static final MediaType ISO_A7;
        public static final MediaType ISO_A8;
        public static final MediaType ISO_A9;
        public static final MediaType ISO_A10;
        public static final MediaType ISO_B0;
        public static final MediaType ISO_B1;
        public static final MediaType ISO_B2;
        public static final MediaType ISO_B3;
        public static final MediaType ISO_B4;
        public static final MediaType ISO_B5;
        public static final MediaType ISO_B6;
        public static final MediaType ISO_B7;
        public static final MediaType ISO_B8;
        public static final MediaType ISO_B9;
        public static final MediaType ISO_B10;
        public static final MediaType JIS_B0;
        public static final MediaType JIS_B1;
        public static final MediaType JIS_B2;
        public static final MediaType JIS_B3;
        public static final MediaType JIS_B4;
        public static final MediaType JIS_B5;
        public static final MediaType JIS_B6;
        public static final MediaType JIS_B7;
        public static final MediaType JIS_B8;
        public static final MediaType JIS_B9;
        public static final MediaType JIS_B10;
        public static final MediaType ISO_C0;
        public static final MediaType ISO_C1;
        public static final MediaType ISO_C2;
        public static final MediaType ISO_C3;
        public static final MediaType ISO_C4;
        public static final MediaType ISO_C5;
        public static final MediaType ISO_C6;
        public static final MediaType ISO_C7;
        public static final MediaType ISO_C8;
        public static final MediaType ISO_C9;
        public static final MediaType ISO_C10;
        public static final MediaType ISO_DESIGNATED_LONG;
        public static final MediaType EXECUTIVE;
        public static final MediaType FOLIO;
        public static final MediaType INVOICE;
        public static final MediaType LEDGER;
        public static final MediaType NA_LETTER;
        public static final MediaType NA_LEGAL;
        public static final MediaType QUARTO;
        public static final MediaType A;
        public static final MediaType B;
        public static final MediaType C;
        public static final MediaType D;
        public static final MediaType E;
        public static final MediaType NA_10X15_ENVELOPE;
        public static final MediaType NA_10X14_ENVELOPE;
        public static final MediaType NA_10X13_ENVELOPE;
        public static final MediaType NA_9X12_ENVELOPE;
        public static final MediaType NA_9X11_ENVELOPE;
        public static final MediaType NA_7X9_ENVELOPE;
        public static final MediaType NA_6X9_ENVELOPE;
        public static final MediaType NA_NUMBER_9_ENVELOPE;
        public static final MediaType NA_NUMBER_10_ENVELOPE;
        public static final MediaType NA_NUMBER_11_ENVELOPE;
        public static final MediaType NA_NUMBER_12_ENVELOPE;
        public static final MediaType NA_NUMBER_14_ENVELOPE;
        public static final MediaType INVITE_ENVELOPE;
        public static final MediaType ITALY_ENVELOPE;
        public static final MediaType MONARCH_ENVELOPE;
        public static final MediaType PERSONAL_ENVELOPE;
        public static final MediaType A0;
        public static final MediaType A1;
        public static final MediaType A2;
        public static final MediaType A3;
        public static final MediaType A4;
        public static final MediaType A5;
        public static final MediaType A6;
        public static final MediaType A7;
        public static final MediaType A8;
        public static final MediaType A9;
        public static final MediaType A10;
        public static final MediaType B0;
        public static final MediaType B1;
        public static final MediaType B2;
        public static final MediaType B3;
        public static final MediaType B4;
        public static final MediaType ISO_B4_ENVELOPE;
        public static final MediaType B5;
        public static final MediaType ISO_B5_ENVELOPE;
        public static final MediaType B6;
        public static final MediaType B7;
        public static final MediaType B8;
        public static final MediaType B9;
        public static final MediaType B10;
        public static final MediaType C0;
        public static final MediaType ISO_C0_ENVELOPE;
        public static final MediaType C1;
        public static final MediaType ISO_C1_ENVELOPE;
        public static final MediaType C2;
        public static final MediaType ISO_C2_ENVELOPE;
        public static final MediaType C3;
        public static final MediaType ISO_C3_ENVELOPE;
        public static final MediaType C4;
        public static final MediaType ISO_C4_ENVELOPE;
        public static final MediaType C5;
        public static final MediaType ISO_C5_ENVELOPE;
        public static final MediaType C6;
        public static final MediaType ISO_C6_ENVELOPE;
        public static final MediaType C7;
        public static final MediaType ISO_C7_ENVELOPE;
        public static final MediaType C8;
        public static final MediaType ISO_C8_ENVELOPE;
        public static final MediaType C9;
        public static final MediaType ISO_C9_ENVELOPE;
        public static final MediaType C10;
        public static final MediaType ISO_C10_ENVELOPE;
        public static final MediaType ISO_DESIGNATED_LONG_ENVELOPE;
        public static final MediaType STATEMENT;
        public static final MediaType TABLOID;
        public static final MediaType LETTER;
        public static final MediaType NOTE;
        public static final MediaType LEGAL;
        public static final MediaType ENV_10X15;
        public static final MediaType ENV_10X14;
        public static final MediaType ENV_10X13;
        public static final MediaType ENV_9X12;
        public static final MediaType ENV_9X11;
        public static final MediaType ENV_7X9;
        public static final MediaType ENV_6X9;
        public static final MediaType ENV_9;
        public static final MediaType ENV_10;
        public static final MediaType ENV_11;
        public static final MediaType ENV_12;
        public static final MediaType ENV_14;
        public static final MediaType ENV_INVITE;
        public static final MediaType ENV_ITALY;
        public static final MediaType ENV_MONARCH;
        public static final MediaType ENV_PERSONAL;
        public static final MediaType INVITE;
        public static final MediaType ITALY;
        public static final MediaType MONARCH;
        public static final MediaType PERSONAL;
        
        private MediaType(final int n) {
            super(n, MediaType.NAMES);
        }
        
        static {
            NAMES = new String[] { "iso-4a0", "iso-2a0", "iso-a0", "iso-a1", "iso-a2", "iso-a3", "iso-a4", "iso-a5", "iso-a6", "iso-a7", "iso-a8", "iso-a9", "iso-a10", "iso-b0", "iso-b1", "iso-b2", "iso-b3", "iso-b4", "iso-b5", "iso-b6", "iso-b7", "iso-b8", "iso-b9", "iso-b10", "jis-b0", "jis-b1", "jis-b2", "jis-b3", "jis-b4", "jis-b5", "jis-b6", "jis-b7", "jis-b8", "jis-b9", "jis-b10", "iso-c0", "iso-c1", "iso-c2", "iso-c3", "iso-c4", "iso-c5", "iso-c6", "iso-c7", "iso-c8", "iso-c9", "iso-c10", "iso-designated-long", "executive", "folio", "invoice", "ledger", "na-letter", "na-legal", "quarto", "a", "b", "c", "d", "e", "na-10x15-envelope", "na-10x14-envelope", "na-10x13-envelope", "na-9x12-envelope", "na-9x11-envelope", "na-7x9-envelope", "na-6x9-envelope", "na-number-9-envelope", "na-number-10-envelope", "na-number-11-envelope", "na-number-12-envelope", "na-number-14-envelope", "invite-envelope", "italy-envelope", "monarch-envelope", "personal-envelope" };
            ISO_4A0 = new MediaType(0);
            ISO_2A0 = new MediaType(1);
            ISO_A0 = new MediaType(2);
            ISO_A1 = new MediaType(3);
            ISO_A2 = new MediaType(4);
            ISO_A3 = new MediaType(5);
            ISO_A4 = new MediaType(6);
            ISO_A5 = new MediaType(7);
            ISO_A6 = new MediaType(8);
            ISO_A7 = new MediaType(9);
            ISO_A8 = new MediaType(10);
            ISO_A9 = new MediaType(11);
            ISO_A10 = new MediaType(12);
            ISO_B0 = new MediaType(13);
            ISO_B1 = new MediaType(14);
            ISO_B2 = new MediaType(15);
            ISO_B3 = new MediaType(16);
            ISO_B4 = new MediaType(17);
            ISO_B5 = new MediaType(18);
            ISO_B6 = new MediaType(19);
            ISO_B7 = new MediaType(20);
            ISO_B8 = new MediaType(21);
            ISO_B9 = new MediaType(22);
            ISO_B10 = new MediaType(23);
            JIS_B0 = new MediaType(24);
            JIS_B1 = new MediaType(25);
            JIS_B2 = new MediaType(26);
            JIS_B3 = new MediaType(27);
            JIS_B4 = new MediaType(28);
            JIS_B5 = new MediaType(29);
            JIS_B6 = new MediaType(30);
            JIS_B7 = new MediaType(31);
            JIS_B8 = new MediaType(32);
            JIS_B9 = new MediaType(33);
            JIS_B10 = new MediaType(34);
            ISO_C0 = new MediaType(35);
            ISO_C1 = new MediaType(36);
            ISO_C2 = new MediaType(37);
            ISO_C3 = new MediaType(38);
            ISO_C4 = new MediaType(39);
            ISO_C5 = new MediaType(40);
            ISO_C6 = new MediaType(41);
            ISO_C7 = new MediaType(42);
            ISO_C8 = new MediaType(43);
            ISO_C9 = new MediaType(44);
            ISO_C10 = new MediaType(45);
            ISO_DESIGNATED_LONG = new MediaType(46);
            EXECUTIVE = new MediaType(47);
            FOLIO = new MediaType(48);
            INVOICE = new MediaType(49);
            LEDGER = new MediaType(50);
            NA_LETTER = new MediaType(51);
            NA_LEGAL = new MediaType(52);
            QUARTO = new MediaType(53);
            A = new MediaType(54);
            B = new MediaType(55);
            C = new MediaType(56);
            D = new MediaType(57);
            E = new MediaType(58);
            NA_10X15_ENVELOPE = new MediaType(59);
            NA_10X14_ENVELOPE = new MediaType(60);
            NA_10X13_ENVELOPE = new MediaType(61);
            NA_9X12_ENVELOPE = new MediaType(62);
            NA_9X11_ENVELOPE = new MediaType(63);
            NA_7X9_ENVELOPE = new MediaType(64);
            NA_6X9_ENVELOPE = new MediaType(65);
            NA_NUMBER_9_ENVELOPE = new MediaType(66);
            NA_NUMBER_10_ENVELOPE = new MediaType(67);
            NA_NUMBER_11_ENVELOPE = new MediaType(68);
            NA_NUMBER_12_ENVELOPE = new MediaType(69);
            NA_NUMBER_14_ENVELOPE = new MediaType(70);
            INVITE_ENVELOPE = new MediaType(71);
            ITALY_ENVELOPE = new MediaType(72);
            MONARCH_ENVELOPE = new MediaType(73);
            PERSONAL_ENVELOPE = new MediaType(74);
            A0 = MediaType.ISO_A0;
            A1 = MediaType.ISO_A1;
            A2 = MediaType.ISO_A2;
            A3 = MediaType.ISO_A3;
            A4 = MediaType.ISO_A4;
            A5 = MediaType.ISO_A5;
            A6 = MediaType.ISO_A6;
            A7 = MediaType.ISO_A7;
            A8 = MediaType.ISO_A8;
            A9 = MediaType.ISO_A9;
            A10 = MediaType.ISO_A10;
            B0 = MediaType.ISO_B0;
            B1 = MediaType.ISO_B1;
            B2 = MediaType.ISO_B2;
            B3 = MediaType.ISO_B3;
            B4 = MediaType.ISO_B4;
            ISO_B4_ENVELOPE = MediaType.ISO_B4;
            B5 = MediaType.ISO_B5;
            ISO_B5_ENVELOPE = MediaType.ISO_B5;
            B6 = MediaType.ISO_B6;
            B7 = MediaType.ISO_B7;
            B8 = MediaType.ISO_B8;
            B9 = MediaType.ISO_B9;
            B10 = MediaType.ISO_B10;
            C0 = MediaType.ISO_C0;
            ISO_C0_ENVELOPE = MediaType.ISO_C0;
            C1 = MediaType.ISO_C1;
            ISO_C1_ENVELOPE = MediaType.ISO_C1;
            C2 = MediaType.ISO_C2;
            ISO_C2_ENVELOPE = MediaType.ISO_C2;
            C3 = MediaType.ISO_C3;
            ISO_C3_ENVELOPE = MediaType.ISO_C3;
            C4 = MediaType.ISO_C4;
            ISO_C4_ENVELOPE = MediaType.ISO_C4;
            C5 = MediaType.ISO_C5;
            ISO_C5_ENVELOPE = MediaType.ISO_C5;
            C6 = MediaType.ISO_C6;
            ISO_C6_ENVELOPE = MediaType.ISO_C6;
            C7 = MediaType.ISO_C7;
            ISO_C7_ENVELOPE = MediaType.ISO_C7;
            C8 = MediaType.ISO_C8;
            ISO_C8_ENVELOPE = MediaType.ISO_C8;
            C9 = MediaType.ISO_C9;
            ISO_C9_ENVELOPE = MediaType.ISO_C9;
            C10 = MediaType.ISO_C10;
            ISO_C10_ENVELOPE = MediaType.ISO_C10;
            ISO_DESIGNATED_LONG_ENVELOPE = MediaType.ISO_DESIGNATED_LONG;
            STATEMENT = MediaType.INVOICE;
            TABLOID = MediaType.LEDGER;
            LETTER = MediaType.NA_LETTER;
            NOTE = MediaType.NA_LETTER;
            LEGAL = MediaType.NA_LEGAL;
            ENV_10X15 = MediaType.NA_10X15_ENVELOPE;
            ENV_10X14 = MediaType.NA_10X14_ENVELOPE;
            ENV_10X13 = MediaType.NA_10X13_ENVELOPE;
            ENV_9X12 = MediaType.NA_9X12_ENVELOPE;
            ENV_9X11 = MediaType.NA_9X11_ENVELOPE;
            ENV_7X9 = MediaType.NA_7X9_ENVELOPE;
            ENV_6X9 = MediaType.NA_6X9_ENVELOPE;
            ENV_9 = MediaType.NA_NUMBER_9_ENVELOPE;
            ENV_10 = MediaType.NA_NUMBER_10_ENVELOPE;
            ENV_11 = MediaType.NA_NUMBER_11_ENVELOPE;
            ENV_12 = MediaType.NA_NUMBER_12_ENVELOPE;
            ENV_14 = MediaType.NA_NUMBER_14_ENVELOPE;
            ENV_INVITE = MediaType.INVITE_ENVELOPE;
            ENV_ITALY = MediaType.ITALY_ENVELOPE;
            ENV_MONARCH = MediaType.MONARCH_ENVELOPE;
            ENV_PERSONAL = MediaType.PERSONAL_ENVELOPE;
            INVITE = MediaType.INVITE_ENVELOPE;
            ITALY = MediaType.ITALY_ENVELOPE;
            MONARCH = MediaType.MONARCH_ENVELOPE;
            PERSONAL = MediaType.PERSONAL_ENVELOPE;
        }
    }
    
    public static final class OrientationRequestedType extends AttributeValue
    {
        private static final int I_PORTRAIT = 0;
        private static final int I_LANDSCAPE = 1;
        private static final String[] NAMES;
        public static final OrientationRequestedType PORTRAIT;
        public static final OrientationRequestedType LANDSCAPE;
        
        private OrientationRequestedType(final int n) {
            super(n, OrientationRequestedType.NAMES);
        }
        
        static {
            NAMES = new String[] { "portrait", "landscape" };
            PORTRAIT = new OrientationRequestedType(0);
            LANDSCAPE = new OrientationRequestedType(1);
        }
    }
    
    public static final class OriginType extends AttributeValue
    {
        private static final int I_PHYSICAL = 0;
        private static final int I_PRINTABLE = 1;
        private static final String[] NAMES;
        public static final OriginType PHYSICAL;
        public static final OriginType PRINTABLE;
        
        private OriginType(final int n) {
            super(n, OriginType.NAMES);
        }
        
        static {
            NAMES = new String[] { "physical", "printable" };
            PHYSICAL = new OriginType(0);
            PRINTABLE = new OriginType(1);
        }
    }
    
    public static final class PrintQualityType extends AttributeValue
    {
        private static final int I_HIGH = 0;
        private static final int I_NORMAL = 1;
        private static final int I_DRAFT = 2;
        private static final String[] NAMES;
        public static final PrintQualityType HIGH;
        public static final PrintQualityType NORMAL;
        public static final PrintQualityType DRAFT;
        
        private PrintQualityType(final int n) {
            super(n, PrintQualityType.NAMES);
        }
        
        static {
            NAMES = new String[] { "high", "normal", "draft" };
            HIGH = new PrintQualityType(0);
            NORMAL = new PrintQualityType(1);
            DRAFT = new PrintQualityType(2);
        }
    }
}
