package sun.print;

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import javax.print.attribute.standard.MediaSizeName;

class Win32MediaSize extends MediaSizeName
{
    private static ArrayList winStringTable;
    private static ArrayList winEnumTable;
    private static MediaSize[] predefMedia;
    private int dmPaperID;
    
    private Win32MediaSize(final int n) {
        super(n);
    }
    
    private static synchronized int nextValue(final String s) {
        Win32MediaSize.winStringTable.add(s);
        return Win32MediaSize.winStringTable.size() - 1;
    }
    
    public static synchronized Win32MediaSize findMediaName(final String s) {
        final int index = Win32MediaSize.winStringTable.indexOf(s);
        if (index != -1) {
            return (Win32MediaSize)Win32MediaSize.winEnumTable.get(index);
        }
        return null;
    }
    
    public static MediaSize[] getPredefMedia() {
        return Win32MediaSize.predefMedia;
    }
    
    public Win32MediaSize(final String s, final int dmPaperID) {
        super(nextValue(s));
        this.dmPaperID = dmPaperID;
        Win32MediaSize.winEnumTable.add(this);
    }
    
    private MediaSizeName[] getSuperEnumTable() {
        return (MediaSizeName[])super.getEnumValueTable();
    }
    
    int getDMPaper() {
        return this.dmPaperID;
    }
    
    @Override
    protected String[] getStringTable() {
        return Win32MediaSize.winStringTable.toArray(new String[Win32MediaSize.winStringTable.size()]);
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return Win32MediaSize.winEnumTable.toArray(new MediaSizeName[Win32MediaSize.winEnumTable.size()]);
    }
    
    static {
        Win32MediaSize.winStringTable = new ArrayList();
        Win32MediaSize.winEnumTable = new ArrayList();
        final MediaSizeName[] superEnumTable = new Win32MediaSize(-1).getSuperEnumTable();
        if (superEnumTable != null) {
            Win32MediaSize.predefMedia = new MediaSize[superEnumTable.length];
            for (int i = 0; i < superEnumTable.length; ++i) {
                Win32MediaSize.predefMedia[i] = MediaSize.getMediaSizeForName(superEnumTable[i]);
            }
        }
    }
}
