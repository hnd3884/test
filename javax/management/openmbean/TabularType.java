package javax.management.openmbean;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class TabularType extends OpenType<TabularData>
{
    static final long serialVersionUID = 6554071860220659261L;
    private CompositeType rowType;
    private List<String> indexNames;
    private transient Integer myHashCode;
    private transient String myToString;
    
    public TabularType(final String s, final String s2, final CompositeType rowType, final String[] array) throws OpenDataException {
        super(TabularData.class.getName(), s, s2, false);
        this.myHashCode = null;
        this.myToString = null;
        if (rowType == null) {
            throw new IllegalArgumentException("Argument rowType cannot be null.");
        }
        checkForNullElement(array, "indexNames");
        checkForEmptyString(array, "indexNames");
        for (int i = 0; i < array.length; ++i) {
            if (!rowType.containsKey(array[i])) {
                throw new OpenDataException("Argument's element value indexNames[" + i + "]=\"" + array[i] + "\" is not a valid item name for rowType.");
            }
        }
        this.rowType = rowType;
        final ArrayList list = new ArrayList(array.length + 1);
        for (int j = 0; j < array.length; ++j) {
            list.add(array[j]);
        }
        this.indexNames = (List<String>)Collections.unmodifiableList((List<?>)list);
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
    
    public CompositeType getRowType() {
        return this.rowType;
    }
    
    public List<String> getIndexNames() {
        return this.indexNames;
    }
    
    @Override
    public boolean isValue(final Object o) {
        return o instanceof TabularData && this.isAssignableFrom(((TabularData)o).getTabularType());
    }
    
    @Override
    boolean isAssignableFrom(final OpenType<?> openType) {
        if (!(openType instanceof TabularType)) {
            return false;
        }
        final TabularType tabularType = (TabularType)openType;
        return this.getTypeName().equals(tabularType.getTypeName()) && this.getIndexNames().equals(tabularType.getIndexNames()) && this.getRowType().isAssignableFrom(tabularType.getRowType());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        TabularType tabularType;
        try {
            tabularType = (TabularType)o;
        }
        catch (final ClassCastException ex) {
            return false;
        }
        return this.getTypeName().equals(tabularType.getTypeName()) && this.rowType.equals(tabularType.rowType) && this.indexNames.equals(tabularType.indexNames);
    }
    
    @Override
    public int hashCode() {
        if (this.myHashCode == null) {
            int n = 0 + this.getTypeName().hashCode() + this.rowType.hashCode();
            final Iterator<String> iterator = this.indexNames.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().hashCode();
            }
            this.myHashCode = n;
        }
        return this.myHashCode;
    }
    
    @Override
    public String toString() {
        if (this.myToString == null) {
            final StringBuilder append = new StringBuilder().append(this.getClass().getName()).append("(name=").append(this.getTypeName()).append(",rowType=").append(this.rowType.toString()).append(",indexNames=(");
            String s = "";
            final Iterator<String> iterator = this.indexNames.iterator();
            while (iterator.hasNext()) {
                append.append(s).append(iterator.next());
                s = ",";
            }
            append.append("))");
            this.myToString = append.toString();
        }
        return this.myToString;
    }
}
