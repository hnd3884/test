package sun.font;

import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;

public enum EAttribute
{
    EFAMILY(TextAttribute.FAMILY), 
    EWEIGHT(TextAttribute.WEIGHT), 
    EWIDTH(TextAttribute.WIDTH), 
    EPOSTURE(TextAttribute.POSTURE), 
    ESIZE(TextAttribute.SIZE), 
    ETRANSFORM(TextAttribute.TRANSFORM), 
    ESUPERSCRIPT(TextAttribute.SUPERSCRIPT), 
    EFONT(TextAttribute.FONT), 
    ECHAR_REPLACEMENT(TextAttribute.CHAR_REPLACEMENT), 
    EFOREGROUND(TextAttribute.FOREGROUND), 
    EBACKGROUND(TextAttribute.BACKGROUND), 
    EUNDERLINE(TextAttribute.UNDERLINE), 
    ESTRIKETHROUGH(TextAttribute.STRIKETHROUGH), 
    ERUN_DIRECTION(TextAttribute.RUN_DIRECTION), 
    EBIDI_EMBEDDING(TextAttribute.BIDI_EMBEDDING), 
    EJUSTIFICATION(TextAttribute.JUSTIFICATION), 
    EINPUT_METHOD_HIGHLIGHT(TextAttribute.INPUT_METHOD_HIGHLIGHT), 
    EINPUT_METHOD_UNDERLINE(TextAttribute.INPUT_METHOD_UNDERLINE), 
    ESWAP_COLORS(TextAttribute.SWAP_COLORS), 
    ENUMERIC_SHAPING(TextAttribute.NUMERIC_SHAPING), 
    EKERNING(TextAttribute.KERNING), 
    ELIGATURES(TextAttribute.LIGATURES), 
    ETRACKING(TextAttribute.TRACKING), 
    EBASELINE_TRANSFORM((TextAttribute)null);
    
    final int mask;
    final TextAttribute att;
    static final EAttribute[] atts;
    
    private EAttribute(final TextAttribute att) {
        this.mask = 1 << this.ordinal();
        this.att = att;
    }
    
    public static EAttribute forAttribute(final AttributedCharacterIterator.Attribute attribute) {
        for (final EAttribute eAttribute : EAttribute.atts) {
            if (eAttribute.att == attribute) {
                return eAttribute;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.name().substring(1).toLowerCase();
    }
    
    static {
        atts = EAttribute.class.getEnumConstants();
    }
}
