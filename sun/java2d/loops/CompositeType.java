package sun.java2d.loops;

import java.awt.AlphaComposite;
import java.util.HashMap;

public final class CompositeType
{
    private static int unusedUID;
    private static final HashMap<String, Integer> compositeUIDMap;
    public static final String DESC_ANY = "Any CompositeContext";
    public static final String DESC_XOR = "XOR mode";
    public static final String DESC_CLEAR = "Porter-Duff Clear";
    public static final String DESC_SRC = "Porter-Duff Src";
    public static final String DESC_DST = "Porter-Duff Dst";
    public static final String DESC_SRC_OVER = "Porter-Duff Src Over Dst";
    public static final String DESC_DST_OVER = "Porter-Duff Dst Over Src";
    public static final String DESC_SRC_IN = "Porter-Duff Src In Dst";
    public static final String DESC_DST_IN = "Porter-Duff Dst In Src";
    public static final String DESC_SRC_OUT = "Porter-Duff Src HeldOutBy Dst";
    public static final String DESC_DST_OUT = "Porter-Duff Dst HeldOutBy Src";
    public static final String DESC_SRC_ATOP = "Porter-Duff Src Atop Dst";
    public static final String DESC_DST_ATOP = "Porter-Duff Dst Atop Src";
    public static final String DESC_ALPHA_XOR = "Porter-Duff Xor";
    public static final String DESC_SRC_NO_EA = "Porter-Duff Src, No Extra Alpha";
    public static final String DESC_SRC_OVER_NO_EA = "Porter-Duff SrcOverDst, No Extra Alpha";
    public static final String DESC_ANY_ALPHA = "Any AlphaComposite Rule";
    public static final CompositeType Any;
    public static final CompositeType General;
    public static final CompositeType AnyAlpha;
    public static final CompositeType Xor;
    public static final CompositeType Clear;
    public static final CompositeType Src;
    public static final CompositeType Dst;
    public static final CompositeType SrcOver;
    public static final CompositeType DstOver;
    public static final CompositeType SrcIn;
    public static final CompositeType DstIn;
    public static final CompositeType SrcOut;
    public static final CompositeType DstOut;
    public static final CompositeType SrcAtop;
    public static final CompositeType DstAtop;
    public static final CompositeType AlphaXor;
    public static final CompositeType SrcNoEa;
    public static final CompositeType SrcOverNoEa;
    public static final CompositeType OpaqueSrcOverNoEa;
    private int uniqueID;
    private String desc;
    private CompositeType next;
    
    public CompositeType deriveSubType(final String s) {
        return new CompositeType(this, s);
    }
    
    public static CompositeType forAlphaComposite(final AlphaComposite alphaComposite) {
        switch (alphaComposite.getRule()) {
            case 1: {
                return CompositeType.Clear;
            }
            case 2: {
                if (alphaComposite.getAlpha() >= 1.0f) {
                    return CompositeType.SrcNoEa;
                }
                return CompositeType.Src;
            }
            case 9: {
                return CompositeType.Dst;
            }
            case 3: {
                if (alphaComposite.getAlpha() >= 1.0f) {
                    return CompositeType.SrcOverNoEa;
                }
                return CompositeType.SrcOver;
            }
            case 4: {
                return CompositeType.DstOver;
            }
            case 5: {
                return CompositeType.SrcIn;
            }
            case 6: {
                return CompositeType.DstIn;
            }
            case 7: {
                return CompositeType.SrcOut;
            }
            case 8: {
                return CompositeType.DstOut;
            }
            case 10: {
                return CompositeType.SrcAtop;
            }
            case 11: {
                return CompositeType.DstAtop;
            }
            case 12: {
                return CompositeType.AlphaXor;
            }
            default: {
                throw new InternalError("Unrecognized alpha rule");
            }
        }
    }
    
    private CompositeType(final CompositeType next, final String desc) {
        this.next = next;
        this.desc = desc;
        this.uniqueID = makeUniqueID(desc);
    }
    
    public static final synchronized int makeUniqueID(final String s) {
        Integer value = CompositeType.compositeUIDMap.get(s);
        if (value == null) {
            if (CompositeType.unusedUID > 255) {
                throw new InternalError("composite type id overflow");
            }
            value = CompositeType.unusedUID++;
            CompositeType.compositeUIDMap.put(s, value);
        }
        return value;
    }
    
    public int getUniqueID() {
        return this.uniqueID;
    }
    
    public String getDescriptor() {
        return this.desc;
    }
    
    public CompositeType getSuperType() {
        return this.next;
    }
    
    @Override
    public int hashCode() {
        return this.desc.hashCode();
    }
    
    public boolean isDerivedFrom(final CompositeType compositeType) {
        CompositeType next = this;
        while (next.desc != compositeType.desc) {
            next = next.next;
            if (next == null) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof CompositeType && ((CompositeType)o).uniqueID == this.uniqueID;
    }
    
    @Override
    public String toString() {
        return this.desc;
    }
    
    static {
        CompositeType.unusedUID = 1;
        compositeUIDMap = new HashMap<String, Integer>(100);
        Any = new CompositeType(null, "Any CompositeContext");
        General = CompositeType.Any;
        AnyAlpha = CompositeType.General.deriveSubType("Any AlphaComposite Rule");
        Xor = CompositeType.General.deriveSubType("XOR mode");
        Clear = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Clear");
        Src = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Src");
        Dst = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Dst");
        SrcOver = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Src Over Dst");
        DstOver = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Dst Over Src");
        SrcIn = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Src In Dst");
        DstIn = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Dst In Src");
        SrcOut = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Src HeldOutBy Dst");
        DstOut = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Dst HeldOutBy Src");
        SrcAtop = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Src Atop Dst");
        DstAtop = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Dst Atop Src");
        AlphaXor = CompositeType.AnyAlpha.deriveSubType("Porter-Duff Xor");
        SrcNoEa = CompositeType.Src.deriveSubType("Porter-Duff Src, No Extra Alpha");
        SrcOverNoEa = CompositeType.SrcOver.deriveSubType("Porter-Duff SrcOverDst, No Extra Alpha");
        OpaqueSrcOverNoEa = CompositeType.SrcOverNoEa.deriveSubType("Porter-Duff Src").deriveSubType("Porter-Duff Src, No Extra Alpha");
    }
}
