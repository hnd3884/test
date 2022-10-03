package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;

public class Finishings extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -627840419548391754L;
    public static final Finishings NONE;
    public static final Finishings STAPLE;
    public static final Finishings COVER;
    public static final Finishings BIND;
    public static final Finishings SADDLE_STITCH;
    public static final Finishings EDGE_STITCH;
    public static final Finishings STAPLE_TOP_LEFT;
    public static final Finishings STAPLE_BOTTOM_LEFT;
    public static final Finishings STAPLE_TOP_RIGHT;
    public static final Finishings STAPLE_BOTTOM_RIGHT;
    public static final Finishings EDGE_STITCH_LEFT;
    public static final Finishings EDGE_STITCH_TOP;
    public static final Finishings EDGE_STITCH_RIGHT;
    public static final Finishings EDGE_STITCH_BOTTOM;
    public static final Finishings STAPLE_DUAL_LEFT;
    public static final Finishings STAPLE_DUAL_TOP;
    public static final Finishings STAPLE_DUAL_RIGHT;
    public static final Finishings STAPLE_DUAL_BOTTOM;
    private static final String[] myStringTable;
    private static final Finishings[] myEnumValueTable;
    
    protected Finishings(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return Finishings.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return Finishings.myEnumValueTable.clone();
    }
    
    @Override
    protected int getOffset() {
        return 3;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return Finishings.class;
    }
    
    @Override
    public final String getName() {
        return "finishings";
    }
    
    static {
        NONE = new Finishings(3);
        STAPLE = new Finishings(4);
        COVER = new Finishings(6);
        BIND = new Finishings(7);
        SADDLE_STITCH = new Finishings(8);
        EDGE_STITCH = new Finishings(9);
        STAPLE_TOP_LEFT = new Finishings(20);
        STAPLE_BOTTOM_LEFT = new Finishings(21);
        STAPLE_TOP_RIGHT = new Finishings(22);
        STAPLE_BOTTOM_RIGHT = new Finishings(23);
        EDGE_STITCH_LEFT = new Finishings(24);
        EDGE_STITCH_TOP = new Finishings(25);
        EDGE_STITCH_RIGHT = new Finishings(26);
        EDGE_STITCH_BOTTOM = new Finishings(27);
        STAPLE_DUAL_LEFT = new Finishings(28);
        STAPLE_DUAL_TOP = new Finishings(29);
        STAPLE_DUAL_RIGHT = new Finishings(30);
        STAPLE_DUAL_BOTTOM = new Finishings(31);
        myStringTable = new String[] { "none", "staple", null, "cover", "bind", "saddle-stitch", "edge-stitch", null, null, null, null, null, null, null, null, null, null, "staple-top-left", "staple-bottom-left", "staple-top-right", "staple-bottom-right", "edge-stitch-left", "edge-stitch-top", "edge-stitch-right", "edge-stitch-bottom", "staple-dual-left", "staple-dual-top", "staple-dual-right", "staple-dual-bottom" };
        myEnumValueTable = new Finishings[] { Finishings.NONE, Finishings.STAPLE, null, Finishings.COVER, Finishings.BIND, Finishings.SADDLE_STITCH, Finishings.EDGE_STITCH, null, null, null, null, null, null, null, null, null, null, Finishings.STAPLE_TOP_LEFT, Finishings.STAPLE_BOTTOM_LEFT, Finishings.STAPLE_TOP_RIGHT, Finishings.STAPLE_BOTTOM_RIGHT, Finishings.EDGE_STITCH_LEFT, Finishings.EDGE_STITCH_TOP, Finishings.EDGE_STITCH_RIGHT, Finishings.EDGE_STITCH_BOTTOM, Finishings.STAPLE_DUAL_LEFT, Finishings.STAPLE_DUAL_TOP, Finishings.STAPLE_DUAL_RIGHT, Finishings.STAPLE_DUAL_BOTTOM };
    }
}
