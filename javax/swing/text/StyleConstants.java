package javax.swing.text;

import java.awt.Color;
import javax.swing.Icon;
import java.awt.Component;

public class StyleConstants
{
    public static final String ComponentElementName = "component";
    public static final String IconElementName = "icon";
    public static final Object NameAttribute;
    public static final Object ResolveAttribute;
    public static final Object ModelAttribute;
    public static final Object BidiLevel;
    public static final Object FontFamily;
    public static final Object Family;
    public static final Object FontSize;
    public static final Object Size;
    public static final Object Bold;
    public static final Object Italic;
    public static final Object Underline;
    public static final Object StrikeThrough;
    public static final Object Superscript;
    public static final Object Subscript;
    public static final Object Foreground;
    public static final Object Background;
    public static final Object ComponentAttribute;
    public static final Object IconAttribute;
    public static final Object ComposedTextAttribute;
    public static final Object FirstLineIndent;
    public static final Object LeftIndent;
    public static final Object RightIndent;
    public static final Object LineSpacing;
    public static final Object SpaceAbove;
    public static final Object SpaceBelow;
    public static final Object Alignment;
    public static final Object TabSet;
    public static final Object Orientation;
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;
    public static final int ALIGN_JUSTIFIED = 3;
    static Object[] keys;
    private String representation;
    
    @Override
    public String toString() {
        return this.representation;
    }
    
    public static int getBidiLevel(final AttributeSet set) {
        final Integer n = (Integer)set.getAttribute(StyleConstants.BidiLevel);
        if (n != null) {
            return n;
        }
        return 0;
    }
    
    public static void setBidiLevel(final MutableAttributeSet set, final int n) {
        set.addAttribute(StyleConstants.BidiLevel, n);
    }
    
    public static Component getComponent(final AttributeSet set) {
        return (Component)set.getAttribute(StyleConstants.ComponentAttribute);
    }
    
    public static void setComponent(final MutableAttributeSet set, final Component component) {
        set.addAttribute("$ename", "component");
        set.addAttribute(StyleConstants.ComponentAttribute, component);
    }
    
    public static Icon getIcon(final AttributeSet set) {
        return (Icon)set.getAttribute(StyleConstants.IconAttribute);
    }
    
    public static void setIcon(final MutableAttributeSet set, final Icon icon) {
        set.addAttribute("$ename", "icon");
        set.addAttribute(StyleConstants.IconAttribute, icon);
    }
    
    public static String getFontFamily(final AttributeSet set) {
        String s = (String)set.getAttribute(StyleConstants.FontFamily);
        if (s == null) {
            s = "Monospaced";
        }
        return s;
    }
    
    public static void setFontFamily(final MutableAttributeSet set, final String s) {
        set.addAttribute(StyleConstants.FontFamily, s);
    }
    
    public static int getFontSize(final AttributeSet set) {
        final Integer n = (Integer)set.getAttribute(StyleConstants.FontSize);
        if (n != null) {
            return n;
        }
        return 12;
    }
    
    public static void setFontSize(final MutableAttributeSet set, final int n) {
        set.addAttribute(StyleConstants.FontSize, n);
    }
    
    public static boolean isBold(final AttributeSet set) {
        final Boolean b = (Boolean)set.getAttribute(StyleConstants.Bold);
        return b != null && b;
    }
    
    public static void setBold(final MutableAttributeSet set, final boolean b) {
        set.addAttribute(StyleConstants.Bold, b);
    }
    
    public static boolean isItalic(final AttributeSet set) {
        final Boolean b = (Boolean)set.getAttribute(StyleConstants.Italic);
        return b != null && b;
    }
    
    public static void setItalic(final MutableAttributeSet set, final boolean b) {
        set.addAttribute(StyleConstants.Italic, b);
    }
    
    public static boolean isUnderline(final AttributeSet set) {
        final Boolean b = (Boolean)set.getAttribute(StyleConstants.Underline);
        return b != null && b;
    }
    
    public static boolean isStrikeThrough(final AttributeSet set) {
        final Boolean b = (Boolean)set.getAttribute(StyleConstants.StrikeThrough);
        return b != null && b;
    }
    
    public static boolean isSuperscript(final AttributeSet set) {
        final Boolean b = (Boolean)set.getAttribute(StyleConstants.Superscript);
        return b != null && b;
    }
    
    public static boolean isSubscript(final AttributeSet set) {
        final Boolean b = (Boolean)set.getAttribute(StyleConstants.Subscript);
        return b != null && b;
    }
    
    public static void setUnderline(final MutableAttributeSet set, final boolean b) {
        set.addAttribute(StyleConstants.Underline, b);
    }
    
    public static void setStrikeThrough(final MutableAttributeSet set, final boolean b) {
        set.addAttribute(StyleConstants.StrikeThrough, b);
    }
    
    public static void setSuperscript(final MutableAttributeSet set, final boolean b) {
        set.addAttribute(StyleConstants.Superscript, b);
    }
    
    public static void setSubscript(final MutableAttributeSet set, final boolean b) {
        set.addAttribute(StyleConstants.Subscript, b);
    }
    
    public static Color getForeground(final AttributeSet set) {
        Color black = (Color)set.getAttribute(StyleConstants.Foreground);
        if (black == null) {
            black = Color.black;
        }
        return black;
    }
    
    public static void setForeground(final MutableAttributeSet set, final Color color) {
        set.addAttribute(StyleConstants.Foreground, color);
    }
    
    public static Color getBackground(final AttributeSet set) {
        Color black = (Color)set.getAttribute(StyleConstants.Background);
        if (black == null) {
            black = Color.black;
        }
        return black;
    }
    
    public static void setBackground(final MutableAttributeSet set, final Color color) {
        set.addAttribute(StyleConstants.Background, color);
    }
    
    public static float getFirstLineIndent(final AttributeSet set) {
        final Float n = (Float)set.getAttribute(StyleConstants.FirstLineIndent);
        if (n != null) {
            return n;
        }
        return 0.0f;
    }
    
    public static void setFirstLineIndent(final MutableAttributeSet set, final float n) {
        set.addAttribute(StyleConstants.FirstLineIndent, new Float(n));
    }
    
    public static float getRightIndent(final AttributeSet set) {
        final Float n = (Float)set.getAttribute(StyleConstants.RightIndent);
        if (n != null) {
            return n;
        }
        return 0.0f;
    }
    
    public static void setRightIndent(final MutableAttributeSet set, final float n) {
        set.addAttribute(StyleConstants.RightIndent, new Float(n));
    }
    
    public static float getLeftIndent(final AttributeSet set) {
        final Float n = (Float)set.getAttribute(StyleConstants.LeftIndent);
        if (n != null) {
            return n;
        }
        return 0.0f;
    }
    
    public static void setLeftIndent(final MutableAttributeSet set, final float n) {
        set.addAttribute(StyleConstants.LeftIndent, new Float(n));
    }
    
    public static float getLineSpacing(final AttributeSet set) {
        final Float n = (Float)set.getAttribute(StyleConstants.LineSpacing);
        if (n != null) {
            return n;
        }
        return 0.0f;
    }
    
    public static void setLineSpacing(final MutableAttributeSet set, final float n) {
        set.addAttribute(StyleConstants.LineSpacing, new Float(n));
    }
    
    public static float getSpaceAbove(final AttributeSet set) {
        final Float n = (Float)set.getAttribute(StyleConstants.SpaceAbove);
        if (n != null) {
            return n;
        }
        return 0.0f;
    }
    
    public static void setSpaceAbove(final MutableAttributeSet set, final float n) {
        set.addAttribute(StyleConstants.SpaceAbove, new Float(n));
    }
    
    public static float getSpaceBelow(final AttributeSet set) {
        final Float n = (Float)set.getAttribute(StyleConstants.SpaceBelow);
        if (n != null) {
            return n;
        }
        return 0.0f;
    }
    
    public static void setSpaceBelow(final MutableAttributeSet set, final float n) {
        set.addAttribute(StyleConstants.SpaceBelow, new Float(n));
    }
    
    public static int getAlignment(final AttributeSet set) {
        final Integer n = (Integer)set.getAttribute(StyleConstants.Alignment);
        if (n != null) {
            return n;
        }
        return 0;
    }
    
    public static void setAlignment(final MutableAttributeSet set, final int n) {
        set.addAttribute(StyleConstants.Alignment, n);
    }
    
    public static TabSet getTabSet(final AttributeSet set) {
        return (TabSet)set.getAttribute(StyleConstants.TabSet);
    }
    
    public static void setTabSet(final MutableAttributeSet set, final TabSet set2) {
        set.addAttribute(StyleConstants.TabSet, set2);
    }
    
    StyleConstants(final String representation) {
        this.representation = representation;
    }
    
    static {
        NameAttribute = new StyleConstants("name");
        ResolveAttribute = new StyleConstants("resolver");
        ModelAttribute = new StyleConstants("model");
        BidiLevel = new CharacterConstants("bidiLevel");
        FontFamily = new FontConstants("family");
        Family = StyleConstants.FontFamily;
        FontSize = new FontConstants("size");
        Size = StyleConstants.FontSize;
        Bold = new FontConstants("bold");
        Italic = new FontConstants("italic");
        Underline = new CharacterConstants("underline");
        StrikeThrough = new CharacterConstants("strikethrough");
        Superscript = new CharacterConstants("superscript");
        Subscript = new CharacterConstants("subscript");
        Foreground = new ColorConstants("foreground");
        Background = new ColorConstants("background");
        ComponentAttribute = new CharacterConstants("component");
        IconAttribute = new CharacterConstants("icon");
        ComposedTextAttribute = new StyleConstants("composed text");
        FirstLineIndent = new ParagraphConstants("FirstLineIndent");
        LeftIndent = new ParagraphConstants("LeftIndent");
        RightIndent = new ParagraphConstants("RightIndent");
        LineSpacing = new ParagraphConstants("LineSpacing");
        SpaceAbove = new ParagraphConstants("SpaceAbove");
        SpaceBelow = new ParagraphConstants("SpaceBelow");
        Alignment = new ParagraphConstants("Alignment");
        TabSet = new ParagraphConstants("TabSet");
        Orientation = new ParagraphConstants("Orientation");
        StyleConstants.keys = new Object[] { StyleConstants.NameAttribute, StyleConstants.ResolveAttribute, StyleConstants.BidiLevel, StyleConstants.FontFamily, StyleConstants.FontSize, StyleConstants.Bold, StyleConstants.Italic, StyleConstants.Underline, StyleConstants.StrikeThrough, StyleConstants.Superscript, StyleConstants.Subscript, StyleConstants.Foreground, StyleConstants.Background, StyleConstants.ComponentAttribute, StyleConstants.IconAttribute, StyleConstants.FirstLineIndent, StyleConstants.LeftIndent, StyleConstants.RightIndent, StyleConstants.LineSpacing, StyleConstants.SpaceAbove, StyleConstants.SpaceBelow, StyleConstants.Alignment, StyleConstants.TabSet, StyleConstants.Orientation, StyleConstants.ModelAttribute, StyleConstants.ComposedTextAttribute };
    }
    
    public static class ParagraphConstants extends StyleConstants implements AttributeSet.ParagraphAttribute
    {
        private ParagraphConstants(final String s) {
            super(s);
        }
    }
    
    public static class CharacterConstants extends StyleConstants implements AttributeSet.CharacterAttribute
    {
        private CharacterConstants(final String s) {
            super(s);
        }
    }
    
    public static class ColorConstants extends StyleConstants implements AttributeSet.ColorAttribute, AttributeSet.CharacterAttribute
    {
        private ColorConstants(final String s) {
            super(s);
        }
    }
    
    public static class FontConstants extends StyleConstants implements AttributeSet.FontAttribute, AttributeSet.CharacterAttribute
    {
        private FontConstants(final String s) {
            super(s);
        }
    }
}
