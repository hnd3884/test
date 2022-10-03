package javax.imageio.metadata;

import java.util.Map;
import com.sun.imageio.plugins.common.StandardMetadataFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

public abstract class IIOMetadataFormatImpl implements IIOMetadataFormat
{
    public static final String standardMetadataFormatName = "javax_imageio_1.0";
    private static IIOMetadataFormat standardFormat;
    private String resourceBaseName;
    private String rootName;
    private HashMap elementMap;
    
    public IIOMetadataFormatImpl(final String s, final int childPolicy) {
        this.resourceBaseName = this.getClass().getName() + "Resources";
        this.elementMap = new HashMap();
        if (s == null) {
            throw new IllegalArgumentException("rootName == null!");
        }
        if (childPolicy < 0 || childPolicy > 5 || childPolicy == 5) {
            throw new IllegalArgumentException("Invalid value for childPolicy!");
        }
        this.rootName = s;
        final Element element = new Element();
        element.elementName = s;
        element.childPolicy = childPolicy;
        this.elementMap.put(s, element);
    }
    
    public IIOMetadataFormatImpl(final String s, final int minChildren, final int maxChildren) {
        this.resourceBaseName = this.getClass().getName() + "Resources";
        this.elementMap = new HashMap();
        if (s == null) {
            throw new IllegalArgumentException("rootName == null!");
        }
        if (minChildren < 0) {
            throw new IllegalArgumentException("minChildren < 0!");
        }
        if (minChildren > maxChildren) {
            throw new IllegalArgumentException("minChildren > maxChildren!");
        }
        final Element element = new Element();
        element.elementName = s;
        element.childPolicy = 5;
        element.minChildren = minChildren;
        element.maxChildren = maxChildren;
        this.rootName = s;
        this.elementMap.put(s, element);
    }
    
    protected void setResourceBaseName(final String resourceBaseName) {
        if (resourceBaseName == null) {
            throw new IllegalArgumentException("resourceBaseName == null!");
        }
        this.resourceBaseName = resourceBaseName;
    }
    
    protected String getResourceBaseName() {
        return this.resourceBaseName;
    }
    
    private Element getElement(final String s, final boolean b) {
        if (b && s == null) {
            throw new IllegalArgumentException("element name is null!");
        }
        final Element element = this.elementMap.get(s);
        if (b && element == null) {
            throw new IllegalArgumentException("No such element: " + s);
        }
        return element;
    }
    
    private Element getElement(final String s) {
        return this.getElement(s, true);
    }
    
    private Attribute getAttribute(final String s, final String s2) {
        final Attribute attribute = this.getElement(s).attrMap.get(s2);
        if (attribute == null) {
            throw new IllegalArgumentException("No such attribute \"" + s2 + "\"!");
        }
        return attribute;
    }
    
    protected void addElement(final String elementName, final String s, final int childPolicy) {
        final Element element = this.getElement(s);
        if (childPolicy < 0 || childPolicy > 5 || childPolicy == 5) {
            throw new IllegalArgumentException("Invalid value for childPolicy!");
        }
        final Element element2 = new Element();
        element2.elementName = elementName;
        element2.childPolicy = childPolicy;
        element.childList.add(elementName);
        element2.parentList.add(s);
        this.elementMap.put(elementName, element2);
    }
    
    protected void addElement(final String elementName, final String s, final int minChildren, final int maxChildren) {
        final Element element = this.getElement(s);
        if (minChildren < 0) {
            throw new IllegalArgumentException("minChildren < 0!");
        }
        if (minChildren > maxChildren) {
            throw new IllegalArgumentException("minChildren > maxChildren!");
        }
        final Element element2 = new Element();
        element2.elementName = elementName;
        element2.childPolicy = 5;
        element2.minChildren = minChildren;
        element2.maxChildren = maxChildren;
        element.childList.add(elementName);
        element2.parentList.add(s);
        this.elementMap.put(elementName, element2);
    }
    
    protected void addChildElement(final String s, final String s2) {
        final Element element = this.getElement(s2);
        final Element element2 = this.getElement(s);
        element.childList.add(s);
        element2.parentList.add(s2);
    }
    
    protected void removeElement(final String s) {
        final Element element = this.getElement(s, false);
        if (element != null) {
            final Iterator iterator = element.parentList.iterator();
            while (iterator.hasNext()) {
                final Element element2 = this.getElement((String)iterator.next(), false);
                if (element2 != null) {
                    element2.childList.remove(s);
                }
            }
            this.elementMap.remove(s);
        }
    }
    
    protected void addAttribute(final String s, final String attrName, final int dataType, final boolean required, final String defaultValue) {
        final Element element = this.getElement(s);
        if (attrName == null) {
            throw new IllegalArgumentException("attrName == null!");
        }
        if (dataType < 0 || dataType > 4) {
            throw new IllegalArgumentException("Invalid value for dataType!");
        }
        final Attribute attribute = new Attribute();
        attribute.attrName = attrName;
        attribute.valueType = 1;
        attribute.dataType = dataType;
        attribute.required = required;
        attribute.defaultValue = defaultValue;
        element.attrList.add(attrName);
        element.attrMap.put(attrName, attribute);
    }
    
    protected void addAttribute(final String s, final String attrName, final int dataType, final boolean required, final String defaultValue, final List<String> enumeratedValues) {
        final Element element = this.getElement(s);
        if (attrName == null) {
            throw new IllegalArgumentException("attrName == null!");
        }
        if (dataType < 0 || dataType > 4) {
            throw new IllegalArgumentException("Invalid value for dataType!");
        }
        if (enumeratedValues == null) {
            throw new IllegalArgumentException("enumeratedValues == null!");
        }
        if (enumeratedValues.size() == 0) {
            throw new IllegalArgumentException("enumeratedValues is empty!");
        }
        for (final Object next : enumeratedValues) {
            if (next == null) {
                throw new IllegalArgumentException("enumeratedValues contains a null!");
            }
            if (!(next instanceof String)) {
                throw new IllegalArgumentException("enumeratedValues contains a non-String value!");
            }
        }
        final Attribute attribute = new Attribute();
        attribute.attrName = attrName;
        attribute.valueType = 16;
        attribute.dataType = dataType;
        attribute.required = required;
        attribute.defaultValue = defaultValue;
        attribute.enumeratedValues = enumeratedValues;
        element.attrList.add(attrName);
        element.attrMap.put(attrName, attribute);
    }
    
    protected void addAttribute(final String s, final String attrName, final int dataType, final boolean required, final String defaultValue, final String minValue, final String maxValue, final boolean b, final boolean b2) {
        final Element element = this.getElement(s);
        if (attrName == null) {
            throw new IllegalArgumentException("attrName == null!");
        }
        if (dataType < 0 || dataType > 4) {
            throw new IllegalArgumentException("Invalid value for dataType!");
        }
        final Attribute attribute = new Attribute();
        attribute.attrName = attrName;
        attribute.valueType = 2;
        if (b) {
            final Attribute attribute2 = attribute;
            attribute2.valueType |= 0x4;
        }
        if (b2) {
            final Attribute attribute3 = attribute;
            attribute3.valueType |= 0x8;
        }
        attribute.dataType = dataType;
        attribute.required = required;
        attribute.defaultValue = defaultValue;
        attribute.minValue = minValue;
        attribute.maxValue = maxValue;
        element.attrList.add(attrName);
        element.attrMap.put(attrName, attribute);
    }
    
    protected void addAttribute(final String s, final String attrName, final int dataType, final boolean required, final int listMinLength, final int listMaxLength) {
        final Element element = this.getElement(s);
        if (attrName == null) {
            throw new IllegalArgumentException("attrName == null!");
        }
        if (dataType < 0 || dataType > 4) {
            throw new IllegalArgumentException("Invalid value for dataType!");
        }
        if (listMinLength < 0 || listMinLength > listMaxLength) {
            throw new IllegalArgumentException("Invalid list bounds!");
        }
        final Attribute attribute = new Attribute();
        attribute.attrName = attrName;
        attribute.valueType = 32;
        attribute.dataType = dataType;
        attribute.required = required;
        attribute.listMinLength = listMinLength;
        attribute.listMaxLength = listMaxLength;
        element.attrList.add(attrName);
        element.attrMap.put(attrName, attribute);
    }
    
    protected void addBooleanAttribute(final String s, final String s2, final boolean b, final boolean b2) {
        final ArrayList list = new ArrayList();
        list.add("TRUE");
        list.add("FALSE");
        String s3 = null;
        if (b) {
            s3 = (b2 ? "TRUE" : "FALSE");
        }
        this.addAttribute(s, s2, 1, true, s3, list);
    }
    
    protected void removeAttribute(final String s, final String s2) {
        final Element element = this.getElement(s);
        element.attrList.remove(s2);
        element.attrMap.remove(s2);
    }
    
    protected <T> void addObjectValue(final String s, final Class<T> classType, final boolean b, final T defaultValue) {
        final Element element = this.getElement(s);
        final ObjectValue objectValue = new ObjectValue();
        objectValue.valueType = 1;
        objectValue.classType = classType;
        objectValue.defaultValue = defaultValue;
        element.objectValue = objectValue;
    }
    
    protected <T> void addObjectValue(final String s, final Class<T> classType, final boolean b, final T defaultValue, final List<? extends T> enumeratedValues) {
        final Element element = this.getElement(s);
        if (enumeratedValues == null) {
            throw new IllegalArgumentException("enumeratedValues == null!");
        }
        if (enumeratedValues.size() == 0) {
            throw new IllegalArgumentException("enumeratedValues is empty!");
        }
        for (final Object next : enumeratedValues) {
            if (next == null) {
                throw new IllegalArgumentException("enumeratedValues contains a null!");
            }
            if (!classType.isInstance(next)) {
                throw new IllegalArgumentException("enumeratedValues contains a value not of class classType!");
            }
        }
        final ObjectValue objectValue = new ObjectValue();
        objectValue.valueType = 16;
        objectValue.classType = classType;
        objectValue.defaultValue = defaultValue;
        objectValue.enumeratedValues = enumeratedValues;
        element.objectValue = objectValue;
    }
    
    protected <T extends Object & Comparable<? super T>> void addObjectValue(final String s, final Class<T> classType, final T defaultValue, final Comparable<? super T> minValue, final Comparable<? super T> maxValue, final boolean b, final boolean b2) {
        final Element element = this.getElement(s);
        final ObjectValue objectValue = new ObjectValue();
        objectValue.valueType = 2;
        if (b) {
            final ObjectValue objectValue2 = objectValue;
            objectValue2.valueType |= 0x4;
        }
        if (b2) {
            final ObjectValue objectValue3 = objectValue;
            objectValue3.valueType |= 0x8;
        }
        objectValue.classType = classType;
        objectValue.defaultValue = defaultValue;
        objectValue.minValue = minValue;
        objectValue.maxValue = maxValue;
        element.objectValue = objectValue;
    }
    
    protected void addObjectValue(final String s, final Class<?> classType, final int arrayMinLength, final int arrayMaxLength) {
        final Element element = this.getElement(s);
        final ObjectValue objectValue = new ObjectValue();
        objectValue.valueType = 32;
        objectValue.classType = classType;
        objectValue.arrayMinLength = arrayMinLength;
        objectValue.arrayMaxLength = arrayMaxLength;
        element.objectValue = objectValue;
    }
    
    protected void removeObjectValue(final String s) {
        this.getElement(s).objectValue = null;
    }
    
    @Override
    public String getRootName() {
        return this.rootName;
    }
    
    @Override
    public abstract boolean canNodeAppear(final String p0, final ImageTypeSpecifier p1);
    
    @Override
    public int getElementMinChildren(final String s) {
        final Element element = this.getElement(s);
        if (element.childPolicy != 5) {
            throw new IllegalArgumentException("Child policy not CHILD_POLICY_REPEAT!");
        }
        return element.minChildren;
    }
    
    @Override
    public int getElementMaxChildren(final String s) {
        final Element element = this.getElement(s);
        if (element.childPolicy != 5) {
            throw new IllegalArgumentException("Child policy not CHILD_POLICY_REPEAT!");
        }
        return element.maxChildren;
    }
    
    private String getResource(final String s, Locale default1) {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        final ClassLoader classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
        ResourceBundle resourceBundle;
        try {
            resourceBundle = ResourceBundle.getBundle(this.resourceBaseName, default1, classLoader);
        }
        catch (final MissingResourceException ex) {
            try {
                resourceBundle = ResourceBundle.getBundle(this.resourceBaseName, default1);
            }
            catch (final MissingResourceException ex2) {
                return null;
            }
        }
        try {
            return resourceBundle.getString(s);
        }
        catch (final MissingResourceException ex3) {
            return null;
        }
    }
    
    @Override
    public String getElementDescription(final String s, final Locale locale) {
        this.getElement(s);
        return this.getResource(s, locale);
    }
    
    @Override
    public int getChildPolicy(final String s) {
        return this.getElement(s).childPolicy;
    }
    
    @Override
    public String[] getChildNames(final String s) {
        final Element element = this.getElement(s);
        if (element.childPolicy == 0) {
            return null;
        }
        return element.childList.toArray(new String[0]);
    }
    
    @Override
    public String[] getAttributeNames(final String s) {
        final List attrList = this.getElement(s).attrList;
        return attrList.toArray(new String[attrList.size()]);
    }
    
    @Override
    public int getAttributeValueType(final String s, final String s2) {
        return this.getAttribute(s, s2).valueType;
    }
    
    @Override
    public int getAttributeDataType(final String s, final String s2) {
        return this.getAttribute(s, s2).dataType;
    }
    
    @Override
    public boolean isAttributeRequired(final String s, final String s2) {
        return this.getAttribute(s, s2).required;
    }
    
    @Override
    public String getAttributeDefaultValue(final String s, final String s2) {
        return this.getAttribute(s, s2).defaultValue;
    }
    
    @Override
    public String[] getAttributeEnumerations(final String s, final String s2) {
        final Attribute attribute = this.getAttribute(s, s2);
        if (attribute.valueType != 16) {
            throw new IllegalArgumentException("Attribute not an enumeration!");
        }
        final List enumeratedValues = attribute.enumeratedValues;
        enumeratedValues.iterator();
        return enumeratedValues.toArray(new String[enumeratedValues.size()]);
    }
    
    @Override
    public String getAttributeMinValue(final String s, final String s2) {
        final Attribute attribute = this.getAttribute(s, s2);
        if (attribute.valueType != 2 && attribute.valueType != 6 && attribute.valueType != 10 && attribute.valueType != 14) {
            throw new IllegalArgumentException("Attribute not a range!");
        }
        return attribute.minValue;
    }
    
    @Override
    public String getAttributeMaxValue(final String s, final String s2) {
        final Attribute attribute = this.getAttribute(s, s2);
        if (attribute.valueType != 2 && attribute.valueType != 6 && attribute.valueType != 10 && attribute.valueType != 14) {
            throw new IllegalArgumentException("Attribute not a range!");
        }
        return attribute.maxValue;
    }
    
    @Override
    public int getAttributeListMinLength(final String s, final String s2) {
        final Attribute attribute = this.getAttribute(s, s2);
        if (attribute.valueType != 32) {
            throw new IllegalArgumentException("Attribute not a list!");
        }
        return attribute.listMinLength;
    }
    
    @Override
    public int getAttributeListMaxLength(final String s, final String s2) {
        final Attribute attribute = this.getAttribute(s, s2);
        if (attribute.valueType != 32) {
            throw new IllegalArgumentException("Attribute not a list!");
        }
        return attribute.listMaxLength;
    }
    
    @Override
    public String getAttributeDescription(final String s, final String s2, final Locale locale) {
        final Element element = this.getElement(s);
        if (s2 == null) {
            throw new IllegalArgumentException("attrName == null!");
        }
        if (element.attrMap.get(s2) == null) {
            throw new IllegalArgumentException("No such attribute!");
        }
        return this.getResource(s + "/" + s2, locale);
    }
    
    private ObjectValue getObjectValue(final String s) {
        final ObjectValue objectValue = this.getElement(s).objectValue;
        if (objectValue == null) {
            throw new IllegalArgumentException("No object within element " + s + "!");
        }
        return objectValue;
    }
    
    @Override
    public int getObjectValueType(final String s) {
        final ObjectValue objectValue = this.getElement(s).objectValue;
        if (objectValue == null) {
            return 0;
        }
        return objectValue.valueType;
    }
    
    @Override
    public Class<?> getObjectClass(final String s) {
        return this.getObjectValue(s).classType;
    }
    
    @Override
    public Object getObjectDefaultValue(final String s) {
        return this.getObjectValue(s).defaultValue;
    }
    
    @Override
    public Object[] getObjectEnumerations(final String s) {
        final ObjectValue objectValue = this.getObjectValue(s);
        if (objectValue.valueType != 16) {
            throw new IllegalArgumentException("Not an enumeration!");
        }
        final List enumeratedValues = objectValue.enumeratedValues;
        return enumeratedValues.toArray(new Object[enumeratedValues.size()]);
    }
    
    @Override
    public Comparable<?> getObjectMinValue(final String s) {
        final ObjectValue objectValue = this.getObjectValue(s);
        if ((objectValue.valueType & 0x2) != 0x2) {
            throw new IllegalArgumentException("Not a range!");
        }
        return objectValue.minValue;
    }
    
    @Override
    public Comparable<?> getObjectMaxValue(final String s) {
        final ObjectValue objectValue = this.getObjectValue(s);
        if ((objectValue.valueType & 0x2) != 0x2) {
            throw new IllegalArgumentException("Not a range!");
        }
        return objectValue.maxValue;
    }
    
    @Override
    public int getObjectArrayMinLength(final String s) {
        final ObjectValue objectValue = this.getObjectValue(s);
        if (objectValue.valueType != 32) {
            throw new IllegalArgumentException("Not a list!");
        }
        return objectValue.arrayMinLength;
    }
    
    @Override
    public int getObjectArrayMaxLength(final String s) {
        final ObjectValue objectValue = this.getObjectValue(s);
        if (objectValue.valueType != 32) {
            throw new IllegalArgumentException("Not a list!");
        }
        return objectValue.arrayMaxLength;
    }
    
    private static synchronized void createStandardFormat() {
        if (IIOMetadataFormatImpl.standardFormat == null) {
            IIOMetadataFormatImpl.standardFormat = new StandardMetadataFormat();
        }
    }
    
    public static IIOMetadataFormat getStandardFormatInstance() {
        createStandardFormat();
        return IIOMetadataFormatImpl.standardFormat;
    }
    
    static {
        IIOMetadataFormatImpl.standardFormat = null;
    }
    
    class Element
    {
        String elementName;
        int childPolicy;
        int minChildren;
        int maxChildren;
        List childList;
        List parentList;
        List attrList;
        Map attrMap;
        ObjectValue objectValue;
        
        Element() {
            this.minChildren = 0;
            this.maxChildren = 0;
            this.childList = new ArrayList();
            this.parentList = new ArrayList();
            this.attrList = new ArrayList();
            this.attrMap = new HashMap();
        }
    }
    
    class Attribute
    {
        String attrName;
        int valueType;
        int dataType;
        boolean required;
        String defaultValue;
        List enumeratedValues;
        String minValue;
        String maxValue;
        int listMinLength;
        int listMaxLength;
        
        Attribute() {
            this.valueType = 1;
            this.defaultValue = null;
        }
    }
    
    class ObjectValue
    {
        int valueType;
        Class classType;
        Object defaultValue;
        List enumeratedValues;
        Comparable minValue;
        Comparable maxValue;
        int arrayMinLength;
        int arrayMaxLength;
        
        ObjectValue() {
            this.valueType = 0;
            this.classType = null;
            this.defaultValue = null;
            this.enumeratedValues = null;
            this.minValue = null;
            this.maxValue = null;
            this.arrayMinLength = 0;
            this.arrayMaxLength = 0;
        }
    }
}
