package org.apache.poi.ss.format;

import java.util.regex.Matcher;
import java.util.Locale;

public class CellTextFormatter extends CellFormatter
{
    private final int[] textPos;
    private final String desc;
    static final CellFormatter SIMPLE_TEXT;
    
    public CellTextFormatter(final String format) {
        super(format);
        final int[] numPlaces = { 0 };
        this.desc = CellFormatPart.parseFormat(format, CellFormatType.TEXT, (m, part, type, desc) -> {
            if (part.equals("@")) {
                final int n;
                ++numPlaces[n];
                return "\u0000";
            }
            else {
                return null;
            }
        }).toString();
        this.textPos = new int[numPlaces[0]];
        int pos = this.desc.length() - 1;
        for (int i = 0; i < this.textPos.length; ++i) {
            this.textPos[i] = this.desc.lastIndexOf("\u0000", pos);
            pos = this.textPos[i] - 1;
        }
    }
    
    @Override
    public void formatValue(final StringBuffer toAppendTo, final Object obj) {
        final int start = toAppendTo.length();
        String text = obj.toString();
        if (obj instanceof Boolean) {
            text = text.toUpperCase(Locale.ROOT);
        }
        toAppendTo.append(this.desc);
        for (final int textPo : this.textPos) {
            final int pos = start + textPo;
            toAppendTo.replace(pos, pos + 1, text);
        }
    }
    
    @Override
    public void simpleValue(final StringBuffer toAppendTo, final Object value) {
        CellTextFormatter.SIMPLE_TEXT.formatValue(toAppendTo, value);
    }
    
    static {
        SIMPLE_TEXT = new CellTextFormatter("@");
    }
}
