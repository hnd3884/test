package com.lowagie.text;

import java.lang.reflect.Field;
import com.lowagie.text.error_messages.MessageLocalization;

public class PageSize
{
    public static final Rectangle LETTER;
    public static final Rectangle NOTE;
    public static final Rectangle LEGAL;
    public static final Rectangle TABLOID;
    public static final Rectangle EXECUTIVE;
    public static final Rectangle POSTCARD;
    public static final Rectangle A0;
    public static final Rectangle A1;
    public static final Rectangle A2;
    public static final Rectangle A3;
    public static final Rectangle A4;
    public static final Rectangle A5;
    public static final Rectangle A6;
    public static final Rectangle A7;
    public static final Rectangle A8;
    public static final Rectangle A9;
    public static final Rectangle A10;
    public static final Rectangle B0;
    public static final Rectangle B1;
    public static final Rectangle B2;
    public static final Rectangle B3;
    public static final Rectangle B4;
    public static final Rectangle B5;
    public static final Rectangle B6;
    public static final Rectangle B7;
    public static final Rectangle B8;
    public static final Rectangle B9;
    public static final Rectangle B10;
    public static final Rectangle ARCH_E;
    public static final Rectangle ARCH_D;
    public static final Rectangle ARCH_C;
    public static final Rectangle ARCH_B;
    public static final Rectangle ARCH_A;
    public static final Rectangle FLSA;
    public static final Rectangle FLSE;
    public static final Rectangle HALFLETTER;
    public static final Rectangle _11X17;
    public static final Rectangle ID_1;
    public static final Rectangle ID_2;
    public static final Rectangle ID_3;
    public static final Rectangle LEDGER;
    public static final Rectangle CROWN_QUARTO;
    public static final Rectangle LARGE_CROWN_QUARTO;
    public static final Rectangle DEMY_QUARTO;
    public static final Rectangle ROYAL_QUARTO;
    public static final Rectangle CROWN_OCTAVO;
    public static final Rectangle LARGE_CROWN_OCTAVO;
    public static final Rectangle DEMY_OCTAVO;
    public static final Rectangle ROYAL_OCTAVO;
    public static final Rectangle SMALL_PAPERBACK;
    public static final Rectangle PENGUIN_SMALL_PAPERBACK;
    public static final Rectangle PENGUIN_LARGE_PAPERBACK;
    
    public static Rectangle getRectangle(String name) {
        name = name.trim().toUpperCase();
        final int pos = name.indexOf(32);
        if (pos == -1) {
            try {
                final Field field = PageSize.class.getDeclaredField(name.toUpperCase());
                return (Rectangle)field.get(null);
            }
            catch (final Exception e) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("can.t.find.page.size.1", name));
            }
        }
        try {
            final String width = name.substring(0, pos);
            final String height = name.substring(pos + 1);
            return new Rectangle(Float.parseFloat(width), Float.parseFloat(height));
        }
        catch (final Exception e) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("1.is.not.a.valid.page.size.format.2", name, e.getMessage()));
        }
    }
    
    static {
        LETTER = new RectangleReadOnly(612.0f, 792.0f);
        NOTE = new RectangleReadOnly(540.0f, 720.0f);
        LEGAL = new RectangleReadOnly(612.0f, 1008.0f);
        TABLOID = new RectangleReadOnly(792.0f, 1224.0f);
        EXECUTIVE = new RectangleReadOnly(522.0f, 756.0f);
        POSTCARD = new RectangleReadOnly(283.0f, 416.0f);
        A0 = new RectangleReadOnly(2384.0f, 3370.0f);
        A1 = new RectangleReadOnly(1684.0f, 2384.0f);
        A2 = new RectangleReadOnly(1191.0f, 1684.0f);
        A3 = new RectangleReadOnly(842.0f, 1191.0f);
        A4 = new RectangleReadOnly(595.0f, 842.0f);
        A5 = new RectangleReadOnly(420.0f, 595.0f);
        A6 = new RectangleReadOnly(297.0f, 420.0f);
        A7 = new RectangleReadOnly(210.0f, 297.0f);
        A8 = new RectangleReadOnly(148.0f, 210.0f);
        A9 = new RectangleReadOnly(105.0f, 148.0f);
        A10 = new RectangleReadOnly(73.0f, 105.0f);
        B0 = new RectangleReadOnly(2834.0f, 4008.0f);
        B1 = new RectangleReadOnly(2004.0f, 2834.0f);
        B2 = new RectangleReadOnly(1417.0f, 2004.0f);
        B3 = new RectangleReadOnly(1000.0f, 1417.0f);
        B4 = new RectangleReadOnly(708.0f, 1000.0f);
        B5 = new RectangleReadOnly(498.0f, 708.0f);
        B6 = new RectangleReadOnly(354.0f, 498.0f);
        B7 = new RectangleReadOnly(249.0f, 354.0f);
        B8 = new RectangleReadOnly(175.0f, 249.0f);
        B9 = new RectangleReadOnly(124.0f, 175.0f);
        B10 = new RectangleReadOnly(87.0f, 124.0f);
        ARCH_E = new RectangleReadOnly(2592.0f, 3456.0f);
        ARCH_D = new RectangleReadOnly(1728.0f, 2592.0f);
        ARCH_C = new RectangleReadOnly(1296.0f, 1728.0f);
        ARCH_B = new RectangleReadOnly(864.0f, 1296.0f);
        ARCH_A = new RectangleReadOnly(648.0f, 864.0f);
        FLSA = new RectangleReadOnly(612.0f, 936.0f);
        FLSE = new RectangleReadOnly(648.0f, 936.0f);
        HALFLETTER = new RectangleReadOnly(396.0f, 612.0f);
        _11X17 = new RectangleReadOnly(792.0f, 1224.0f);
        ID_1 = new RectangleReadOnly(242.65f, 153.0f);
        ID_2 = new RectangleReadOnly(297.0f, 210.0f);
        ID_3 = new RectangleReadOnly(354.0f, 249.0f);
        LEDGER = new RectangleReadOnly(1224.0f, 792.0f);
        CROWN_QUARTO = new RectangleReadOnly(535.0f, 697.0f);
        LARGE_CROWN_QUARTO = new RectangleReadOnly(569.0f, 731.0f);
        DEMY_QUARTO = new RectangleReadOnly(620.0f, 782.0f);
        ROYAL_QUARTO = new RectangleReadOnly(671.0f, 884.0f);
        CROWN_OCTAVO = new RectangleReadOnly(348.0f, 527.0f);
        LARGE_CROWN_OCTAVO = new RectangleReadOnly(365.0f, 561.0f);
        DEMY_OCTAVO = new RectangleReadOnly(391.0f, 612.0f);
        ROYAL_OCTAVO = new RectangleReadOnly(442.0f, 663.0f);
        SMALL_PAPERBACK = new RectangleReadOnly(314.0f, 504.0f);
        PENGUIN_SMALL_PAPERBACK = new RectangleReadOnly(314.0f, 513.0f);
        PENGUIN_LARGE_PAPERBACK = new RectangleReadOnly(365.0f, 561.0f);
    }
}
