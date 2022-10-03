package javax.print.attribute.standard;

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.Attribute;

public class MediaName extends Media implements Attribute
{
    private static final long serialVersionUID = 4653117714524155448L;
    public static final MediaName NA_LETTER_WHITE;
    public static final MediaName NA_LETTER_TRANSPARENT;
    public static final MediaName ISO_A4_WHITE;
    public static final MediaName ISO_A4_TRANSPARENT;
    private static final String[] myStringTable;
    private static final MediaName[] myEnumValueTable;
    
    protected MediaName(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return MediaName.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return MediaName.myEnumValueTable.clone();
    }
    
    static {
        NA_LETTER_WHITE = new MediaName(0);
        NA_LETTER_TRANSPARENT = new MediaName(1);
        ISO_A4_WHITE = new MediaName(2);
        ISO_A4_TRANSPARENT = new MediaName(3);
        myStringTable = new String[] { "na-letter-white", "na-letter-transparent", "iso-a4-white", "iso-a4-transparent" };
        myEnumValueTable = new MediaName[] { MediaName.NA_LETTER_WHITE, MediaName.NA_LETTER_TRANSPARENT, MediaName.ISO_A4_WHITE, MediaName.ISO_A4_TRANSPARENT };
    }
}
