package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.EnumSyntax;

public final class PresentationDirection extends EnumSyntax implements PrintJobAttribute, PrintRequestAttribute
{
    private static final long serialVersionUID = 8294728067230931780L;
    public static final PresentationDirection TOBOTTOM_TORIGHT;
    public static final PresentationDirection TOBOTTOM_TOLEFT;
    public static final PresentationDirection TOTOP_TORIGHT;
    public static final PresentationDirection TOTOP_TOLEFT;
    public static final PresentationDirection TORIGHT_TOBOTTOM;
    public static final PresentationDirection TORIGHT_TOTOP;
    public static final PresentationDirection TOLEFT_TOBOTTOM;
    public static final PresentationDirection TOLEFT_TOTOP;
    private static final String[] myStringTable;
    private static final PresentationDirection[] myEnumValueTable;
    
    private PresentationDirection(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return PresentationDirection.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return PresentationDirection.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PresentationDirection.class;
    }
    
    @Override
    public final String getName() {
        return "presentation-direction";
    }
    
    static {
        TOBOTTOM_TORIGHT = new PresentationDirection(0);
        TOBOTTOM_TOLEFT = new PresentationDirection(1);
        TOTOP_TORIGHT = new PresentationDirection(2);
        TOTOP_TOLEFT = new PresentationDirection(3);
        TORIGHT_TOBOTTOM = new PresentationDirection(4);
        TORIGHT_TOTOP = new PresentationDirection(5);
        TOLEFT_TOBOTTOM = new PresentationDirection(6);
        TOLEFT_TOTOP = new PresentationDirection(7);
        myStringTable = new String[] { "tobottom-toright", "tobottom-toleft", "totop-toright", "totop-toleft", "toright-tobottom", "toright-totop", "toleft-tobottom", "toleft-totop" };
        myEnumValueTable = new PresentationDirection[] { PresentationDirection.TOBOTTOM_TORIGHT, PresentationDirection.TOBOTTOM_TOLEFT, PresentationDirection.TOTOP_TORIGHT, PresentationDirection.TOTOP_TOLEFT, PresentationDirection.TORIGHT_TOBOTTOM, PresentationDirection.TORIGHT_TOTOP, PresentationDirection.TOLEFT_TOBOTTOM, PresentationDirection.TOLEFT_TOTOP };
    }
}
