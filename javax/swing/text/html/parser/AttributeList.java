package javax.swing.text.html.parser;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.io.Serializable;

public final class AttributeList implements DTDConstants, Serializable
{
    public String name;
    public int type;
    public Vector<?> values;
    public int modifier;
    public String value;
    public AttributeList next;
    static Hashtable<Object, Object> attributeTypes;
    
    AttributeList() {
    }
    
    public AttributeList(final String name) {
        this.name = name;
    }
    
    public AttributeList(final String name, final int type, final int modifier, final String value, final Vector<?> values, final AttributeList next) {
        this.name = name;
        this.type = type;
        this.modifier = modifier;
        this.value = value;
        this.values = values;
        this.next = next;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getModifier() {
        return this.modifier;
    }
    
    public Enumeration<?> getValues() {
        return (this.values != null) ? this.values.elements() : null;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public AttributeList getNext() {
        return this.next;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static void defineAttributeType(final String s, final int n) {
        final Integer value = n;
        AttributeList.attributeTypes.put(s, value);
        AttributeList.attributeTypes.put(value, s);
    }
    
    public static int name2type(final String s) {
        final Integer n = AttributeList.attributeTypes.get(s);
        return (n == null) ? 1 : n;
    }
    
    public static String type2name(final int n) {
        return AttributeList.attributeTypes.get(n);
    }
    
    static {
        AttributeList.attributeTypes = new Hashtable<Object, Object>();
        defineAttributeType("CDATA", 1);
        defineAttributeType("ENTITY", 2);
        defineAttributeType("ENTITIES", 3);
        defineAttributeType("ID", 4);
        defineAttributeType("IDREF", 5);
        defineAttributeType("IDREFS", 6);
        defineAttributeType("NAME", 7);
        defineAttributeType("NAMES", 8);
        defineAttributeType("NMTOKEN", 9);
        defineAttributeType("NMTOKENS", 10);
        defineAttributeType("NOTATION", 11);
        defineAttributeType("NUMBER", 12);
        defineAttributeType("NUMBERS", 13);
        defineAttributeType("NUTOKEN", 14);
        defineAttributeType("NUTOKENS", 15);
        AttributeList.attributeTypes.put("fixed", 1);
        AttributeList.attributeTypes.put("required", 2);
        AttributeList.attributeTypes.put("current", 3);
        AttributeList.attributeTypes.put("conref", 4);
        AttributeList.attributeTypes.put("implied", 5);
    }
}
