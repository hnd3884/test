package javax.swing.text.rtf;

import java.io.IOException;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Dictionary;

class RTFAttributes
{
    static RTFAttribute[] attributes;
    
    static Dictionary<String, RTFAttribute> attributesByKeyword() {
        final Hashtable hashtable = new Hashtable(RTFAttributes.attributes.length);
        for (final RTFAttribute rtfAttribute : RTFAttributes.attributes) {
            hashtable.put(rtfAttribute.rtfName(), rtfAttribute);
        }
        return hashtable;
    }
    
    static {
        final Vector vector = new Vector();
        final int n = 0;
        final int n2 = 1;
        final int n3 = 3;
        final int n4 = 4;
        final Boolean value = true;
        final Boolean value2 = false;
        vector.addElement(new BooleanAttribute(n, StyleConstants.Italic, "i"));
        vector.addElement(new BooleanAttribute(n, StyleConstants.Bold, "b"));
        vector.addElement(new BooleanAttribute(n, StyleConstants.Underline, "ul"));
        vector.addElement(NumericAttribute.NewTwips(n2, StyleConstants.LeftIndent, "li", 0.0f, 0));
        vector.addElement(NumericAttribute.NewTwips(n2, StyleConstants.RightIndent, "ri", 0.0f, 0));
        vector.addElement(NumericAttribute.NewTwips(n2, StyleConstants.FirstLineIndent, "fi", 0.0f, 0));
        vector.addElement(new AssertiveAttribute(n2, StyleConstants.Alignment, "ql", 0));
        vector.addElement(new AssertiveAttribute(n2, StyleConstants.Alignment, "qr", 2));
        vector.addElement(new AssertiveAttribute(n2, StyleConstants.Alignment, "qc", 1));
        vector.addElement(new AssertiveAttribute(n2, StyleConstants.Alignment, "qj", 3));
        vector.addElement(NumericAttribute.NewTwips(n2, StyleConstants.SpaceAbove, "sa", 0));
        vector.addElement(NumericAttribute.NewTwips(n2, StyleConstants.SpaceBelow, "sb", 0));
        vector.addElement(new AssertiveAttribute(n4, "tab_alignment", "tqr", 1));
        vector.addElement(new AssertiveAttribute(n4, "tab_alignment", "tqc", 2));
        vector.addElement(new AssertiveAttribute(n4, "tab_alignment", "tqdec", 4));
        vector.addElement(new AssertiveAttribute(n4, "tab_leader", "tldot", 1));
        vector.addElement(new AssertiveAttribute(n4, "tab_leader", "tlhyph", 2));
        vector.addElement(new AssertiveAttribute(n4, "tab_leader", "tlul", 3));
        vector.addElement(new AssertiveAttribute(n4, "tab_leader", "tlth", 4));
        vector.addElement(new AssertiveAttribute(n4, "tab_leader", "tleq", 5));
        vector.addElement(new BooleanAttribute(n, "caps", "caps"));
        vector.addElement(new BooleanAttribute(n, "outl", "outl"));
        vector.addElement(new BooleanAttribute(n, "scaps", "scaps"));
        vector.addElement(new BooleanAttribute(n, "shad", "shad"));
        vector.addElement(new BooleanAttribute(n, "v", "v"));
        vector.addElement(new BooleanAttribute(n, "strike", "strike"));
        vector.addElement(new BooleanAttribute(n, "deleted", "deleted"));
        vector.addElement(new AssertiveAttribute(n3, "saveformat", "defformat", "RTF"));
        vector.addElement(new AssertiveAttribute(n3, "landscape", "landscape"));
        vector.addElement(NumericAttribute.NewTwips(n3, "paperw", "paperw", 12240));
        vector.addElement(NumericAttribute.NewTwips(n3, "paperh", "paperh", 15840));
        vector.addElement(NumericAttribute.NewTwips(n3, "margl", "margl", 1800));
        vector.addElement(NumericAttribute.NewTwips(n3, "margr", "margr", 1800));
        vector.addElement(NumericAttribute.NewTwips(n3, "margt", "margt", 1440));
        vector.addElement(NumericAttribute.NewTwips(n3, "margb", "margb", 1440));
        vector.addElement(NumericAttribute.NewTwips(n3, "gutter", "gutter", 0));
        vector.addElement(new AssertiveAttribute(n2, "widowctrl", "nowidctlpar", value2));
        vector.addElement(new AssertiveAttribute(n2, "widowctrl", "widctlpar", value));
        vector.addElement(new AssertiveAttribute(n3, "widowctrl", "widowctrl", value));
        final RTFAttribute[] attributes = new RTFAttribute[vector.size()];
        vector.copyInto(attributes);
        RTFAttributes.attributes = attributes;
    }
    
    abstract static class GenericAttribute
    {
        int domain;
        Object swingName;
        String rtfName;
        
        protected GenericAttribute(final int domain, final Object swingName, final String rtfName) {
            this.domain = domain;
            this.swingName = swingName;
            this.rtfName = rtfName;
        }
        
        public int domain() {
            return this.domain;
        }
        
        public Object swingName() {
            return this.swingName;
        }
        
        public String rtfName() {
            return this.rtfName;
        }
        
        abstract boolean set(final MutableAttributeSet p0);
        
        abstract boolean set(final MutableAttributeSet p0, final int p1);
        
        abstract boolean setDefault(final MutableAttributeSet p0);
        
        public boolean write(final AttributeSet set, final RTFGenerator rtfGenerator, final boolean b) throws IOException {
            return this.writeValue(set.getAttribute(this.swingName), rtfGenerator, b);
        }
        
        public boolean writeValue(final Object o, final RTFGenerator rtfGenerator, final boolean b) throws IOException {
            return false;
        }
    }
    
    static class BooleanAttribute extends GenericAttribute implements RTFAttribute
    {
        boolean rtfDefault;
        boolean swingDefault;
        protected static final Boolean True;
        protected static final Boolean False;
        
        public BooleanAttribute(final int n, final Object o, final String s, final boolean swingDefault, final boolean rtfDefault) {
            super(n, o, s);
            this.swingDefault = swingDefault;
            this.rtfDefault = rtfDefault;
        }
        
        public BooleanAttribute(final int n, final Object o, final String s) {
            super(n, o, s);
            this.swingDefault = false;
            this.rtfDefault = false;
        }
        
        @Override
        public boolean set(final MutableAttributeSet set) {
            set.addAttribute(this.swingName, BooleanAttribute.True);
            return true;
        }
        
        @Override
        public boolean set(final MutableAttributeSet set, final int n) {
            set.addAttribute(this.swingName, (n != 0) ? BooleanAttribute.True : BooleanAttribute.False);
            return true;
        }
        
        @Override
        public boolean setDefault(final MutableAttributeSet set) {
            if (this.swingDefault != this.rtfDefault || set.getAttribute(this.swingName) != null) {
                set.addAttribute(this.swingName, this.rtfDefault);
            }
            return true;
        }
        
        @Override
        public boolean writeValue(final Object o, final RTFGenerator rtfGenerator, final boolean b) throws IOException {
            Boolean value;
            if (o == null) {
                value = this.swingDefault;
            }
            else {
                value = (Boolean)o;
            }
            if (b || value != this.rtfDefault) {
                if (value) {
                    rtfGenerator.writeControlWord(this.rtfName);
                }
                else {
                    rtfGenerator.writeControlWord(this.rtfName, 0);
                }
            }
            return true;
        }
        
        static {
            True = true;
            False = false;
        }
    }
    
    static class AssertiveAttribute extends GenericAttribute implements RTFAttribute
    {
        Object swingValue;
        
        public AssertiveAttribute(final int n, final Object o, final String s) {
            super(n, o, s);
            this.swingValue = true;
        }
        
        public AssertiveAttribute(final int n, final Object o, final String s, final Object swingValue) {
            super(n, o, s);
            this.swingValue = swingValue;
        }
        
        public AssertiveAttribute(final int n, final Object o, final String s, final int n2) {
            super(n, o, s);
            this.swingValue = n2;
        }
        
        @Override
        public boolean set(final MutableAttributeSet set) {
            if (this.swingValue == null) {
                set.removeAttribute(this.swingName);
            }
            else {
                set.addAttribute(this.swingName, this.swingValue);
            }
            return true;
        }
        
        @Override
        public boolean set(final MutableAttributeSet set, final int n) {
            return false;
        }
        
        @Override
        public boolean setDefault(final MutableAttributeSet set) {
            set.removeAttribute(this.swingName);
            return true;
        }
        
        @Override
        public boolean writeValue(final Object o, final RTFGenerator rtfGenerator, final boolean b) throws IOException {
            if (o == null) {
                return !b;
            }
            if (o.equals(this.swingValue)) {
                rtfGenerator.writeControlWord(this.rtfName);
                return true;
            }
            return !b;
        }
    }
    
    static class NumericAttribute extends GenericAttribute implements RTFAttribute
    {
        int rtfDefault;
        Number swingDefault;
        float scale;
        
        protected NumericAttribute(final int n, final Object o, final String s) {
            super(n, o, s);
            this.rtfDefault = 0;
            this.swingDefault = null;
            this.scale = 1.0f;
        }
        
        public NumericAttribute(final int n, final Object o, final String s, final int n2, final int n3) {
            this(n, o, s, n2, n3, 1.0f);
        }
        
        public NumericAttribute(final int n, final Object o, final String s, final Number swingDefault, final int rtfDefault, final float scale) {
            super(n, o, s);
            this.swingDefault = swingDefault;
            this.rtfDefault = rtfDefault;
            this.scale = scale;
        }
        
        public static NumericAttribute NewTwips(final int n, final Object o, final String s, final float n2, final int n3) {
            return new NumericAttribute(n, o, s, new Float(n2), n3, 20.0f);
        }
        
        public static NumericAttribute NewTwips(final int n, final Object o, final String s, final int n2) {
            return new NumericAttribute(n, o, s, null, n2, 20.0f);
        }
        
        @Override
        public boolean set(final MutableAttributeSet set) {
            return false;
        }
        
        @Override
        public boolean set(final MutableAttributeSet set, final int n) {
            Number value;
            if (this.scale == 1.0f) {
                value = n;
            }
            else {
                value = new Float(n / this.scale);
            }
            set.addAttribute(this.swingName, value);
            return true;
        }
        
        @Override
        public boolean setDefault(final MutableAttributeSet set) {
            Number swingDefault = (Number)set.getAttribute(this.swingName);
            if (swingDefault == null) {
                swingDefault = this.swingDefault;
            }
            if (swingDefault != null && ((this.scale == 1.0f && swingDefault.intValue() == this.rtfDefault) || Math.round(swingDefault.floatValue() * this.scale) == this.rtfDefault)) {
                return true;
            }
            this.set(set, this.rtfDefault);
            return true;
        }
        
        @Override
        public boolean writeValue(final Object o, final RTFGenerator rtfGenerator, final boolean b) throws IOException {
            Number swingDefault = (Number)o;
            if (swingDefault == null) {
                swingDefault = this.swingDefault;
            }
            if (swingDefault == null) {
                return true;
            }
            final int round = Math.round(swingDefault.floatValue() * this.scale);
            if (b || round != this.rtfDefault) {
                rtfGenerator.writeControlWord(this.rtfName, round);
            }
            return true;
        }
    }
}
