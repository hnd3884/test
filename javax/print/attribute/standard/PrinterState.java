package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.EnumSyntax;

public final class PrinterState extends EnumSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = -649578618346507718L;
    public static final PrinterState UNKNOWN;
    public static final PrinterState IDLE;
    public static final PrinterState PROCESSING;
    public static final PrinterState STOPPED;
    private static final String[] myStringTable;
    private static final PrinterState[] myEnumValueTable;
    
    protected PrinterState(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return PrinterState.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return PrinterState.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterState.class;
    }
    
    @Override
    public final String getName() {
        return "printer-state";
    }
    
    static {
        UNKNOWN = new PrinterState(0);
        IDLE = new PrinterState(3);
        PROCESSING = new PrinterState(4);
        STOPPED = new PrinterState(5);
        myStringTable = new String[] { "unknown", null, null, "idle", "processing", "stopped" };
        myEnumValueTable = new PrinterState[] { PrinterState.UNKNOWN, null, null, PrinterState.IDLE, PrinterState.PROCESSING, PrinterState.STOPPED };
    }
}
