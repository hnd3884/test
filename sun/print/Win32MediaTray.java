package sun.print;

import javax.print.attribute.EnumSyntax;
import java.util.Collection;
import java.util.ArrayList;
import javax.print.attribute.standard.MediaTray;

public class Win32MediaTray extends MediaTray
{
    static final Win32MediaTray ENVELOPE_MANUAL;
    static final Win32MediaTray AUTO;
    static final Win32MediaTray TRACTOR;
    static final Win32MediaTray SMALL_FORMAT;
    static final Win32MediaTray LARGE_FORMAT;
    static final Win32MediaTray FORMSOURCE;
    private static ArrayList winStringTable;
    private static ArrayList winEnumTable;
    public int winID;
    private static final String[] myStringTable;
    private static final MediaTray[] myEnumValueTable;
    
    private Win32MediaTray(final int n, final int winID) {
        super(n);
        this.winID = winID;
    }
    
    private static synchronized int nextValue(final String s) {
        Win32MediaTray.winStringTable.add(s);
        return getTraySize() - 1;
    }
    
    protected Win32MediaTray(final int winID, final String s) {
        super(nextValue(s));
        this.winID = winID;
        Win32MediaTray.winEnumTable.add(this);
    }
    
    public int getDMBinID() {
        return this.winID;
    }
    
    protected static int getTraySize() {
        return Win32MediaTray.myStringTable.length + Win32MediaTray.winStringTable.size();
    }
    
    @Override
    protected String[] getStringTable() {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < Win32MediaTray.myStringTable.length; ++i) {
            list.add(Win32MediaTray.myStringTable[i]);
        }
        list.addAll(Win32MediaTray.winStringTable);
        return list.toArray(new String[list.size()]);
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < Win32MediaTray.myEnumValueTable.length; ++i) {
            list.add(Win32MediaTray.myEnumValueTable[i]);
        }
        list.addAll(Win32MediaTray.winEnumTable);
        return list.toArray(new MediaTray[list.size()]);
    }
    
    static {
        ENVELOPE_MANUAL = new Win32MediaTray(0, 6);
        AUTO = new Win32MediaTray(1, 7);
        TRACTOR = new Win32MediaTray(2, 8);
        SMALL_FORMAT = new Win32MediaTray(3, 9);
        LARGE_FORMAT = new Win32MediaTray(4, 10);
        FORMSOURCE = new Win32MediaTray(5, 15);
        Win32MediaTray.winStringTable = new ArrayList();
        Win32MediaTray.winEnumTable = new ArrayList();
        myStringTable = new String[] { "Manual-Envelope", "Automatic-Feeder", "Tractor-Feeder", "Small-Format", "Large-Format", "Form-Source" };
        myEnumValueTable = new MediaTray[] { Win32MediaTray.ENVELOPE_MANUAL, Win32MediaTray.AUTO, Win32MediaTray.TRACTOR, Win32MediaTray.SMALL_FORMAT, Win32MediaTray.LARGE_FORMAT, Win32MediaTray.FORMSOURCE };
    }
}
