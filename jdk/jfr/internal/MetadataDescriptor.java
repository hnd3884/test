package jdk.jfr.internal;

import java.util.TimeZone;
import java.util.Locale;
import java.io.DataOutput;
import java.io.DataInput;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import jdk.jfr.EventType;
import java.util.List;

public final class MetadataDescriptor
{
    static final String ATTRIBUTE_ID = "id";
    static final String ATTRIBUTE_SIMPLE_TYPE = "simpleType";
    static final String ATTRIBUTE_GMT_OFFSET = "gmtOffset";
    static final String ATTRIBUTE_LOCALE = "locale";
    static final String ELEMENT_TYPE = "class";
    static final String ELEMENT_SETTING = "setting";
    static final String ELEMENT_ANNOTATION = "annotation";
    static final String ELEMENT_FIELD = "field";
    static final String ATTRIBUTE_SUPER_TYPE = "superType";
    static final String ATTRIBUTE_TYPE_ID = "class";
    static final String ATTRIBUTE_DIMENSION = "dimension";
    static final String ATTRIBUTE_NAME = "name";
    static final String ATTRIBUTE_CONSTANT_POOL = "constantPool";
    static final String ATTRIBUTE_DEFAULT_VALUE = "defaultValue";
    final List<EventType> eventTypes;
    final Collection<Type> types;
    long gmtOffset;
    String locale;
    Element root;
    
    MetadataDescriptor() {
        this.eventTypes = new ArrayList<EventType>();
        this.types = new ArrayList<Type>();
    }
    
    private static void prettyPrintXML(final Appendable appendable, final String s, final Element element) throws IOException {
        appendable.append(s + "<" + element.name);
        for (final Attribute attribute : element.attributes) {
            appendable.append(" ").append(attribute.name).append("=\"").append(attribute.value).append("\"");
        }
        if (element.elements.size() == 0) {
            appendable.append("/");
        }
        appendable.append(">\n");
        final Iterator<Element> iterator2 = element.elements.iterator();
        while (iterator2.hasNext()) {
            prettyPrintXML(appendable, s + "  ", iterator2.next());
        }
        if (element.elements.size() != 0) {
            appendable.append(s).append("</").append(element.name).append(">\n");
        }
    }
    
    public Collection<Type> getTypes() {
        return this.types;
    }
    
    public List<EventType> getEventTypes() {
        return this.eventTypes;
    }
    
    public int getGMTOffset() {
        return (int)this.gmtOffset;
    }
    
    public String getLocale() {
        return this.locale;
    }
    
    public static MetadataDescriptor read(final DataInput dataInput) throws IOException {
        return new MetadataReader(dataInput).getDescriptor();
    }
    
    static void write(final List<Type> list, final DataOutput dataOutput) throws IOException {
        final MetadataDescriptor metadataDescriptor = new MetadataDescriptor();
        metadataDescriptor.locale = Locale.getDefault().toString();
        metadataDescriptor.gmtOffset = TimeZone.getDefault().getRawOffset();
        metadataDescriptor.types.addAll(list);
        new MetadataWriter(metadataDescriptor).writeBinary(dataOutput);
    }
    
    @Override
    public String toString() {
        return this.root.toString();
    }
    
    static final class Attribute
    {
        final String name;
        final String value;
        
        private Attribute(final String name, final String value) {
            this.name = name;
            this.value = value;
        }
    }
    
    static final class Element
    {
        final String name;
        final List<Element> elements;
        final List<Attribute> attributes;
        
        Element(final String name) {
            this.elements = new ArrayList<Element>();
            this.attributes = new ArrayList<Attribute>();
            this.name = name;
        }
        
        long longValue(final String s) {
            final String attribute = this.attribute(s);
            if (attribute != null) {
                return Long.parseLong(attribute);
            }
            throw new IllegalArgumentException(s);
        }
        
        String attribute(final String s) {
            for (final Attribute attribute : this.attributes) {
                if (attribute.name.equals(s)) {
                    return attribute.value;
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            try {
                prettyPrintXML(sb, "", this);
            }
            catch (final IOException ex) {}
            return sb.toString();
        }
        
        long attribute(final String s, final long n) {
            final String attribute = this.attribute(s);
            if (attribute == null) {
                return n;
            }
            return Long.parseLong(attribute);
        }
        
        String attribute(final String s, final String s2) {
            final String attribute = this.attribute(s);
            if (attribute == null) {
                return s2;
            }
            return attribute;
        }
        
        List<Element> elements(final String... array) {
            final ArrayList list = new ArrayList();
            for (final String s : array) {
                for (final Element element : this.elements) {
                    if (element.name.equals(s)) {
                        list.add(element);
                    }
                }
            }
            return list;
        }
        
        void add(final Element element) {
            this.elements.add(element);
        }
        
        void addAttribute(final String s, final Object o) {
            this.attributes.add(new Attribute(s, String.valueOf(o)));
        }
        
        Element newChild(final String s) {
            final Element element = new Element(s);
            this.elements.add(element);
            return element;
        }
        
        public void addArrayAttribute(final Element element, final String s, final Object o) {
            final String name;
            final String s2 = name = o.getClass().getComponentType().getName();
            switch (name) {
                case "int": {
                    final int[] array = (int[])o;
                    for (int i = 0; i < array.length; ++i) {
                        this.addAttribute(s + "-" + i, array[i]);
                    }
                    break;
                }
                case "long": {
                    final long[] array2 = (long[])o;
                    for (int j = 0; j < array2.length; ++j) {
                        this.addAttribute(s + "-" + j, array2[j]);
                    }
                    break;
                }
                case "float": {
                    final float[] array3 = (float[])o;
                    for (int k = 0; k < array3.length; ++k) {
                        this.addAttribute(s + "-" + k, array3[k]);
                    }
                    break;
                }
                case "double": {
                    final double[] array4 = (double[])o;
                    for (int l = 0; l < array4.length; ++l) {
                        this.addAttribute(s + "-" + l, array4[l]);
                    }
                    break;
                }
                case "short": {
                    final short[] array5 = (short[])o;
                    for (int n2 = 0; n2 < array5.length; ++n2) {
                        this.addAttribute(s + "-" + n2, array5[n2]);
                    }
                    break;
                }
                case "char": {
                    final char[] array6 = (char[])o;
                    for (int n3 = 0; n3 < array6.length; ++n3) {
                        this.addAttribute(s + "-" + n3, array6[n3]);
                    }
                    break;
                }
                case "byte": {
                    final byte[] array7 = (byte[])o;
                    for (int n4 = 0; n4 < array7.length; ++n4) {
                        this.addAttribute(s + "-" + n4, array7[n4]);
                    }
                    break;
                }
                case "boolean": {
                    final boolean[] array8 = (boolean[])o;
                    for (int n5 = 0; n5 < array8.length; ++n5) {
                        this.addAttribute(s + "-" + n5, array8[n5]);
                    }
                    break;
                }
                case "java.lang.String": {
                    final String[] array9 = (String[])o;
                    for (int n6 = 0; n6 < array9.length; ++n6) {
                        this.addAttribute(s + "-" + n6, array9[n6]);
                    }
                    break;
                }
                default: {
                    throw new InternalError("Array type of " + s2 + " is not supported");
                }
            }
        }
    }
}
