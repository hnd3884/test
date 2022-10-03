package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;

public final class SheetCollate extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = 7080587914259873003L;
    public static final SheetCollate UNCOLLATED;
    public static final SheetCollate COLLATED;
    private static final String[] myStringTable;
    private static final SheetCollate[] myEnumValueTable;
    
    protected SheetCollate(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return SheetCollate.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return SheetCollate.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return SheetCollate.class;
    }
    
    @Override
    public final String getName() {
        return "sheet-collate";
    }
    
    static {
        UNCOLLATED = new SheetCollate(0);
        COLLATED = new SheetCollate(1);
        myStringTable = new String[] { "uncollated", "collated" };
        myEnumValueTable = new SheetCollate[] { SheetCollate.UNCOLLATED, SheetCollate.COLLATED };
    }
}
