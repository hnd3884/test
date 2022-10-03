package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;

public class PrinterStateReason extends EnumSyntax implements Attribute
{
    private static final long serialVersionUID = -1623720656201472593L;
    public static final PrinterStateReason OTHER;
    public static final PrinterStateReason MEDIA_NEEDED;
    public static final PrinterStateReason MEDIA_JAM;
    public static final PrinterStateReason MOVING_TO_PAUSED;
    public static final PrinterStateReason PAUSED;
    public static final PrinterStateReason SHUTDOWN;
    public static final PrinterStateReason CONNECTING_TO_DEVICE;
    public static final PrinterStateReason TIMED_OUT;
    public static final PrinterStateReason STOPPING;
    public static final PrinterStateReason STOPPED_PARTLY;
    public static final PrinterStateReason TONER_LOW;
    public static final PrinterStateReason TONER_EMPTY;
    public static final PrinterStateReason SPOOL_AREA_FULL;
    public static final PrinterStateReason COVER_OPEN;
    public static final PrinterStateReason INTERLOCK_OPEN;
    public static final PrinterStateReason DOOR_OPEN;
    public static final PrinterStateReason INPUT_TRAY_MISSING;
    public static final PrinterStateReason MEDIA_LOW;
    public static final PrinterStateReason MEDIA_EMPTY;
    public static final PrinterStateReason OUTPUT_TRAY_MISSING;
    public static final PrinterStateReason OUTPUT_AREA_ALMOST_FULL;
    public static final PrinterStateReason OUTPUT_AREA_FULL;
    public static final PrinterStateReason MARKER_SUPPLY_LOW;
    public static final PrinterStateReason MARKER_SUPPLY_EMPTY;
    public static final PrinterStateReason MARKER_WASTE_ALMOST_FULL;
    public static final PrinterStateReason MARKER_WASTE_FULL;
    public static final PrinterStateReason FUSER_OVER_TEMP;
    public static final PrinterStateReason FUSER_UNDER_TEMP;
    public static final PrinterStateReason OPC_NEAR_EOL;
    public static final PrinterStateReason OPC_LIFE_OVER;
    public static final PrinterStateReason DEVELOPER_LOW;
    public static final PrinterStateReason DEVELOPER_EMPTY;
    public static final PrinterStateReason INTERPRETER_RESOURCE_UNAVAILABLE;
    private static final String[] myStringTable;
    private static final PrinterStateReason[] myEnumValueTable;
    
    protected PrinterStateReason(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return PrinterStateReason.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return PrinterStateReason.myEnumValueTable.clone();
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterStateReason.class;
    }
    
    @Override
    public final String getName() {
        return "printer-state-reason";
    }
    
    static {
        OTHER = new PrinterStateReason(0);
        MEDIA_NEEDED = new PrinterStateReason(1);
        MEDIA_JAM = new PrinterStateReason(2);
        MOVING_TO_PAUSED = new PrinterStateReason(3);
        PAUSED = new PrinterStateReason(4);
        SHUTDOWN = new PrinterStateReason(5);
        CONNECTING_TO_DEVICE = new PrinterStateReason(6);
        TIMED_OUT = new PrinterStateReason(7);
        STOPPING = new PrinterStateReason(8);
        STOPPED_PARTLY = new PrinterStateReason(9);
        TONER_LOW = new PrinterStateReason(10);
        TONER_EMPTY = new PrinterStateReason(11);
        SPOOL_AREA_FULL = new PrinterStateReason(12);
        COVER_OPEN = new PrinterStateReason(13);
        INTERLOCK_OPEN = new PrinterStateReason(14);
        DOOR_OPEN = new PrinterStateReason(15);
        INPUT_TRAY_MISSING = new PrinterStateReason(16);
        MEDIA_LOW = new PrinterStateReason(17);
        MEDIA_EMPTY = new PrinterStateReason(18);
        OUTPUT_TRAY_MISSING = new PrinterStateReason(19);
        OUTPUT_AREA_ALMOST_FULL = new PrinterStateReason(20);
        OUTPUT_AREA_FULL = new PrinterStateReason(21);
        MARKER_SUPPLY_LOW = new PrinterStateReason(22);
        MARKER_SUPPLY_EMPTY = new PrinterStateReason(23);
        MARKER_WASTE_ALMOST_FULL = new PrinterStateReason(24);
        MARKER_WASTE_FULL = new PrinterStateReason(25);
        FUSER_OVER_TEMP = new PrinterStateReason(26);
        FUSER_UNDER_TEMP = new PrinterStateReason(27);
        OPC_NEAR_EOL = new PrinterStateReason(28);
        OPC_LIFE_OVER = new PrinterStateReason(29);
        DEVELOPER_LOW = new PrinterStateReason(30);
        DEVELOPER_EMPTY = new PrinterStateReason(31);
        INTERPRETER_RESOURCE_UNAVAILABLE = new PrinterStateReason(32);
        myStringTable = new String[] { "other", "media-needed", "media-jam", "moving-to-paused", "paused", "shutdown", "connecting-to-device", "timed-out", "stopping", "stopped-partly", "toner-low", "toner-empty", "spool-area-full", "cover-open", "interlock-open", "door-open", "input-tray-missing", "media-low", "media-empty", "output-tray-missing", "output-area-almost-full", "output-area-full", "marker-supply-low", "marker-supply-empty", "marker-waste-almost-full", "marker-waste-full", "fuser-over-temp", "fuser-under-temp", "opc-near-eol", "opc-life-over", "developer-low", "developer-empty", "interpreter-resource-unavailable" };
        myEnumValueTable = new PrinterStateReason[] { PrinterStateReason.OTHER, PrinterStateReason.MEDIA_NEEDED, PrinterStateReason.MEDIA_JAM, PrinterStateReason.MOVING_TO_PAUSED, PrinterStateReason.PAUSED, PrinterStateReason.SHUTDOWN, PrinterStateReason.CONNECTING_TO_DEVICE, PrinterStateReason.TIMED_OUT, PrinterStateReason.STOPPING, PrinterStateReason.STOPPED_PARTLY, PrinterStateReason.TONER_LOW, PrinterStateReason.TONER_EMPTY, PrinterStateReason.SPOOL_AREA_FULL, PrinterStateReason.COVER_OPEN, PrinterStateReason.INTERLOCK_OPEN, PrinterStateReason.DOOR_OPEN, PrinterStateReason.INPUT_TRAY_MISSING, PrinterStateReason.MEDIA_LOW, PrinterStateReason.MEDIA_EMPTY, PrinterStateReason.OUTPUT_TRAY_MISSING, PrinterStateReason.OUTPUT_AREA_ALMOST_FULL, PrinterStateReason.OUTPUT_AREA_FULL, PrinterStateReason.MARKER_SUPPLY_LOW, PrinterStateReason.MARKER_SUPPLY_EMPTY, PrinterStateReason.MARKER_WASTE_ALMOST_FULL, PrinterStateReason.MARKER_WASTE_FULL, PrinterStateReason.FUSER_OVER_TEMP, PrinterStateReason.FUSER_UNDER_TEMP, PrinterStateReason.OPC_NEAR_EOL, PrinterStateReason.OPC_LIFE_OVER, PrinterStateReason.DEVELOPER_LOW, PrinterStateReason.DEVELOPER_EMPTY, PrinterStateReason.INTERPRETER_RESOURCE_UNAVAILABLE };
    }
}
