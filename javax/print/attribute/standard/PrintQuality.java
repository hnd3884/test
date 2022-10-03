package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;

public class PrintQuality extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -3072341285225858365L;
    public static final PrintQuality DRAFT;
    public static final PrintQuality NORMAL;
    public static final PrintQuality HIGH;
    private static final String[] myStringTable;
    private static final PrintQuality[] myEnumValueTable;
    
    protected PrintQuality(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return PrintQuality.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return PrintQuality.myEnumValueTable.clone();
    }
    
    @Override
    protected int getOffset() {
        return 3;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrintQuality.class;
    }
    
    @Override
    public final String getName() {
        return "print-quality";
    }
    
    static {
        DRAFT = new PrintQuality(3);
        NORMAL = new PrintQuality(4);
        HIGH = new PrintQuality(5);
        myStringTable = new String[] { "draft", "normal", "high" };
        myEnumValueTable = new PrintQuality[] { PrintQuality.DRAFT, PrintQuality.NORMAL, PrintQuality.HIGH };
    }
}
