package sun.print;

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import javax.print.attribute.standard.MediaSizeName;

class CustomMediaSizeName extends MediaSizeName
{
    private static ArrayList customStringTable;
    private static ArrayList customEnumTable;
    private String choiceName;
    private MediaSizeName mediaName;
    private static final long serialVersionUID = 7412807582228043717L;
    
    private CustomMediaSizeName(final int n) {
        super(n);
    }
    
    private static synchronized int nextValue(final String s) {
        CustomMediaSizeName.customStringTable.add(s);
        return CustomMediaSizeName.customStringTable.size() - 1;
    }
    
    public CustomMediaSizeName(final String s) {
        super(nextValue(s));
        CustomMediaSizeName.customEnumTable.add(this);
        this.choiceName = null;
        this.mediaName = null;
    }
    
    public CustomMediaSizeName(final String s, final String choiceName, final float n, final float n2) {
        super(nextValue(s));
        this.choiceName = choiceName;
        CustomMediaSizeName.customEnumTable.add(this);
        this.mediaName = null;
        try {
            this.mediaName = MediaSize.findMedia(n, n2, 25400);
        }
        catch (final IllegalArgumentException ex) {}
        if (this.mediaName != null) {
            final MediaSize mediaSizeForName = MediaSize.getMediaSizeForName(this.mediaName);
            if (mediaSizeForName == null) {
                this.mediaName = null;
            }
            else {
                final float x = mediaSizeForName.getX(25400);
                final float y = mediaSizeForName.getY(25400);
                final float abs = Math.abs(x - n);
                final float abs2 = Math.abs(y - n2);
                if (abs > 0.1 || abs2 > 0.1) {
                    this.mediaName = null;
                }
            }
        }
    }
    
    public String getChoiceName() {
        return this.choiceName;
    }
    
    public MediaSizeName getStandardMedia() {
        return this.mediaName;
    }
    
    public static MediaSizeName findMedia(final Media[] array, final float n, final float n2, final int n3) {
        if (n <= 0.0f || n2 <= 0.0f || n3 < 1) {
            throw new IllegalArgumentException("args must be +ve values");
        }
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("args must have valid array of media");
        }
        int n4 = 0;
        final MediaSizeName[] array2 = new MediaSizeName[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] instanceof MediaSizeName) {
                array2[n4++] = (MediaSizeName)array[i];
            }
        }
        if (n4 == 0) {
            return null;
        }
        int n5 = 0;
        double n6 = n * n + n2 * n2;
        for (int j = 0; j < n4; ++j) {
            final MediaSize mediaSizeForName = MediaSize.getMediaSizeForName(array2[j]);
            if (mediaSizeForName != null) {
                final float[] size = mediaSizeForName.getSize(n3);
                if (n == size[0] && n2 == size[1]) {
                    n5 = j;
                    break;
                }
                final float n7 = n - size[0];
                final float n8 = n2 - size[1];
                final double n9 = n7 * n7 + n8 * n8;
                if (n9 < n6) {
                    n6 = n9;
                    n5 = j;
                }
            }
        }
        return array2[n5];
    }
    
    public Media[] getSuperEnumTable() {
        return (Media[])super.getEnumValueTable();
    }
    
    @Override
    protected String[] getStringTable() {
        return CustomMediaSizeName.customStringTable.toArray(new String[CustomMediaSizeName.customStringTable.size()]);
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return CustomMediaSizeName.customEnumTable.toArray(new MediaSizeName[CustomMediaSizeName.customEnumTable.size()]);
    }
    
    static {
        CustomMediaSizeName.customStringTable = new ArrayList();
        CustomMediaSizeName.customEnumTable = new ArrayList();
    }
}
