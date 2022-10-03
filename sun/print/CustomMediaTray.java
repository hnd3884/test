package sun.print;

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import javax.print.attribute.standard.MediaTray;

class CustomMediaTray extends MediaTray
{
    private static ArrayList customStringTable;
    private static ArrayList customEnumTable;
    private String choiceName;
    private static final long serialVersionUID = 1019451298193987013L;
    
    private CustomMediaTray(final int n) {
        super(n);
    }
    
    private static synchronized int nextValue(final String s) {
        CustomMediaTray.customStringTable.add(s);
        return CustomMediaTray.customStringTable.size() - 1;
    }
    
    public CustomMediaTray(final String s, final String choiceName) {
        super(nextValue(s));
        this.choiceName = choiceName;
        CustomMediaTray.customEnumTable.add(this);
    }
    
    public String getChoiceName() {
        return this.choiceName;
    }
    
    public Media[] getSuperEnumTable() {
        return (Media[])super.getEnumValueTable();
    }
    
    @Override
    protected String[] getStringTable() {
        return CustomMediaTray.customStringTable.toArray(new String[CustomMediaTray.customStringTable.size()]);
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return CustomMediaTray.customEnumTable.toArray(new MediaTray[CustomMediaTray.customEnumTable.size()]);
    }
    
    static {
        CustomMediaTray.customStringTable = new ArrayList();
        CustomMediaTray.customEnumTable = new ArrayList();
    }
}
