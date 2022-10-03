package javax.management.openmbean;

import java.util.Iterator;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;

public class CompositeType extends OpenType<CompositeData>
{
    static final long serialVersionUID = -5366242454346948798L;
    private TreeMap<String, String> nameToDescription;
    private TreeMap<String, OpenType<?>> nameToType;
    private transient Integer myHashCode;
    private transient String myToString;
    private transient Set<String> myNamesSet;
    
    public CompositeType(final String s, final String s2, final String[] array, final String[] array2, final OpenType<?>[] array3) throws OpenDataException {
        super(CompositeData.class.getName(), s, s2, false);
        this.myHashCode = null;
        this.myToString = null;
        this.myNamesSet = null;
        checkForNullElement(array, "itemNames");
        checkForNullElement(array2, "itemDescriptions");
        checkForNullElement(array3, "itemTypes");
        checkForEmptyString(array, "itemNames");
        checkForEmptyString(array2, "itemDescriptions");
        if (array.length != array2.length || array.length != array3.length) {
            throw new IllegalArgumentException("Array arguments itemNames[], itemDescriptions[] and itemTypes[] should be of same length (got " + array.length + ", " + array2.length + " and " + array3.length + ").");
        }
        this.nameToDescription = new TreeMap<String, String>();
        this.nameToType = new TreeMap<String, OpenType<?>>();
        for (int i = 0; i < array.length; ++i) {
            final String trim = array[i].trim();
            if (this.nameToDescription.containsKey(trim)) {
                throw new OpenDataException("Argument's element itemNames[" + i + "]=\"" + array[i] + "\" duplicates a previous item names.");
            }
            this.nameToDescription.put(trim, array2[i].trim());
            this.nameToType.put(trim, array3[i]);
        }
    }
    
    private static void checkForNullElement(final Object[] array, final String s) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Argument " + s + "[] cannot be null or empty.");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new IllegalArgumentException("Argument's element " + s + "[" + i + "] cannot be null.");
            }
        }
    }
    
    private static void checkForEmptyString(final String[] array, final String s) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].trim().equals("")) {
                throw new IllegalArgumentException("Argument's element " + s + "[" + i + "] cannot be an empty string.");
            }
        }
    }
    
    public boolean containsKey(final String s) {
        return s != null && this.nameToDescription.containsKey(s);
    }
    
    public String getDescription(final String s) {
        if (s == null) {
            return null;
        }
        return this.nameToDescription.get(s);
    }
    
    public OpenType<?> getType(final String s) {
        if (s == null) {
            return null;
        }
        return this.nameToType.get(s);
    }
    
    public Set<String> keySet() {
        if (this.myNamesSet == null) {
            this.myNamesSet = Collections.unmodifiableSet((Set<? extends String>)this.nameToDescription.keySet());
        }
        return this.myNamesSet;
    }
    
    @Override
    public boolean isValue(final Object o) {
        return o instanceof CompositeData && this.isAssignableFrom(((CompositeData)o).getCompositeType());
    }
    
    @Override
    boolean isAssignableFrom(final OpenType<?> openType) {
        if (!(openType instanceof CompositeType)) {
            return false;
        }
        final CompositeType compositeType = (CompositeType)openType;
        if (!compositeType.getTypeName().equals(this.getTypeName())) {
            return false;
        }
        for (final String s : this.keySet()) {
            final OpenType<?> type = compositeType.getType(s);
            final OpenType<?> type2 = this.getType(s);
            if (type == null || !type2.isAssignableFrom(type)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        CompositeType compositeType;
        try {
            compositeType = (CompositeType)o;
        }
        catch (final ClassCastException ex) {
            return false;
        }
        return this.getTypeName().equals(compositeType.getTypeName()) && this.nameToType.equals(compositeType.nameToType);
    }
    
    @Override
    public int hashCode() {
        if (this.myHashCode == null) {
            int n = 0 + this.getTypeName().hashCode();
            for (final String s : this.nameToDescription.keySet()) {
                n = n + s.hashCode() + this.nameToType.get(s).hashCode();
            }
            this.myHashCode = n;
        }
        return this.myHashCode;
    }
    
    @Override
    public String toString() {
        if (this.myToString == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getName());
            sb.append("(name=");
            sb.append(this.getTypeName());
            sb.append(",items=(");
            int n = 0;
            for (final String s : this.nameToType.keySet()) {
                if (n > 0) {
                    sb.append(",");
                }
                sb.append("(itemName=");
                sb.append(s);
                sb.append(",itemType=");
                sb.append(this.nameToType.get(s).toString() + ")");
                ++n;
            }
            sb.append("))");
            this.myToString = sb.toString();
        }
        return this.myToString;
    }
}
