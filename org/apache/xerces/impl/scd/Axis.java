package org.apache.xerces.impl.scd;

final class Axis
{
    public static final String[] AXIS_TYPES;
    public static final short SCHEMA_ATTRIBUTE = 0;
    public static final short SCHEMA_ELEMENT = 1;
    public static final short TYPE = 2;
    public static final short ATTRIBUTE_GROUP = 3;
    public static final short GROUP = 4;
    public static final short IDENTITY_CONSTRAINT = 5;
    public static final short ASSERTION = 6;
    public static final short ALTERNATIVE = 7;
    public static final short NOTATION = 8;
    public static final short MODEL = 9;
    public static final short ANY_ATTRIBUTE = 10;
    public static final short ANY = 11;
    public static final short FACET = 12;
    public static final short SCOPE = 13;
    public static final short CONTEXT = 14;
    public static final short SUBSTITUTION_GROUP = 15;
    public static final short BASE_TYPE = 16;
    public static final short ITEM_TYPE = 17;
    public static final short MEMBER_TYPE = 18;
    public static final short PRIMITIVE_TYPE = 19;
    public static final short KEY = 20;
    public static final short ANNOTATION = 21;
    public static final short COMPONENT = 22;
    public static final short CURRENT_COMPONENT = 23;
    public static final short ATTRIBUTE_USE = 24;
    public static final short PARTICLE = 25;
    public static final short EXTENSION_AXIS = 26;
    public static final short SPECIAL_COMPONENT = 27;
    private static final short UNKNOWN_AXIS = -1;
    public static final short NO_AXIS = 100;
    
    private Axis() {
    }
    
    public static String axisToString(final short n) {
        if (n >= 0 && n < Axis.AXIS_TYPES.length) {
            return Axis.AXIS_TYPES[n];
        }
        return null;
    }
    
    public static short qnameToAxis(final String s) {
        for (short n = 0; n < Axis.AXIS_TYPES.length; ++n) {
            if (Axis.AXIS_TYPES[n].equals(s)) {
                return n;
            }
        }
        return -1;
    }
    
    static {
        AXIS_TYPES = new String[] { "schemaAttribute", "schemaElement", "type", "attributeGroup", "group", "identityConstraint", "assertion", "alternative", "notation", "model", "anyAttribute", "any", "facet", "scope", "context", "substitutionGroup", "baseType", "itemType", "memberType", "primitiveType", "key", "annotation", "component", "currentComponent", "attributeUse", "particle", null };
    }
}
