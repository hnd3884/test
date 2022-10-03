package javax.imageio.metadata;

import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;

public interface IIOMetadataFormat
{
    public static final int CHILD_POLICY_EMPTY = 0;
    public static final int CHILD_POLICY_ALL = 1;
    public static final int CHILD_POLICY_SOME = 2;
    public static final int CHILD_POLICY_CHOICE = 3;
    public static final int CHILD_POLICY_SEQUENCE = 4;
    public static final int CHILD_POLICY_REPEAT = 5;
    public static final int CHILD_POLICY_MAX = 5;
    public static final int VALUE_NONE = 0;
    public static final int VALUE_ARBITRARY = 1;
    public static final int VALUE_RANGE = 2;
    public static final int VALUE_RANGE_MIN_INCLUSIVE_MASK = 4;
    public static final int VALUE_RANGE_MAX_INCLUSIVE_MASK = 8;
    public static final int VALUE_RANGE_MIN_INCLUSIVE = 6;
    public static final int VALUE_RANGE_MAX_INCLUSIVE = 10;
    public static final int VALUE_RANGE_MIN_MAX_INCLUSIVE = 14;
    public static final int VALUE_ENUMERATION = 16;
    public static final int VALUE_LIST = 32;
    public static final int DATATYPE_STRING = 0;
    public static final int DATATYPE_BOOLEAN = 1;
    public static final int DATATYPE_INTEGER = 2;
    public static final int DATATYPE_FLOAT = 3;
    public static final int DATATYPE_DOUBLE = 4;
    
    String getRootName();
    
    boolean canNodeAppear(final String p0, final ImageTypeSpecifier p1);
    
    int getElementMinChildren(final String p0);
    
    int getElementMaxChildren(final String p0);
    
    String getElementDescription(final String p0, final Locale p1);
    
    int getChildPolicy(final String p0);
    
    String[] getChildNames(final String p0);
    
    String[] getAttributeNames(final String p0);
    
    int getAttributeValueType(final String p0, final String p1);
    
    int getAttributeDataType(final String p0, final String p1);
    
    boolean isAttributeRequired(final String p0, final String p1);
    
    String getAttributeDefaultValue(final String p0, final String p1);
    
    String[] getAttributeEnumerations(final String p0, final String p1);
    
    String getAttributeMinValue(final String p0, final String p1);
    
    String getAttributeMaxValue(final String p0, final String p1);
    
    int getAttributeListMinLength(final String p0, final String p1);
    
    int getAttributeListMaxLength(final String p0, final String p1);
    
    String getAttributeDescription(final String p0, final String p1, final Locale p2);
    
    int getObjectValueType(final String p0);
    
    Class<?> getObjectClass(final String p0);
    
    Object getObjectDefaultValue(final String p0);
    
    Object[] getObjectEnumerations(final String p0);
    
    Comparable<?> getObjectMinValue(final String p0);
    
    Comparable<?> getObjectMaxValue(final String p0);
    
    int getObjectArrayMinLength(final String p0);
    
    int getObjectArrayMaxLength(final String p0);
}
