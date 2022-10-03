package sun.management;

import javax.management.openmbean.OpenType;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.TabularType;
import java.util.Set;
import java.io.ObjectStreamException;
import java.util.Collection;
import javax.management.openmbean.CompositeType;
import java.io.Serializable;
import javax.management.openmbean.CompositeData;

public abstract class LazyCompositeData implements CompositeData, Serializable
{
    private CompositeData compositeData;
    private static final long serialVersionUID = -2190411934472666714L;
    
    @Override
    public boolean containsKey(final String s) {
        return this.compositeData().containsKey(s);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.compositeData().containsValue(o);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.compositeData().equals(o);
    }
    
    @Override
    public Object get(final String s) {
        return this.compositeData().get(s);
    }
    
    @Override
    public Object[] getAll(final String[] array) {
        return this.compositeData().getAll(array);
    }
    
    @Override
    public CompositeType getCompositeType() {
        return this.compositeData().getCompositeType();
    }
    
    @Override
    public int hashCode() {
        return this.compositeData().hashCode();
    }
    
    @Override
    public String toString() {
        return this.compositeData().toString();
    }
    
    @Override
    public Collection<?> values() {
        return this.compositeData().values();
    }
    
    private synchronized CompositeData compositeData() {
        if (this.compositeData != null) {
            return this.compositeData;
        }
        return this.compositeData = this.getCompositeData();
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return this.compositeData();
    }
    
    protected abstract CompositeData getCompositeData();
    
    static String getString(final CompositeData compositeData, final String s) {
        if (compositeData == null) {
            throw new IllegalArgumentException("Null CompositeData");
        }
        return (String)compositeData.get(s);
    }
    
    static boolean getBoolean(final CompositeData compositeData, final String s) {
        if (compositeData == null) {
            throw new IllegalArgumentException("Null CompositeData");
        }
        return (boolean)compositeData.get(s);
    }
    
    static long getLong(final CompositeData compositeData, final String s) {
        if (compositeData == null) {
            throw new IllegalArgumentException("Null CompositeData");
        }
        return (long)compositeData.get(s);
    }
    
    static int getInt(final CompositeData compositeData, final String s) {
        if (compositeData == null) {
            throw new IllegalArgumentException("Null CompositeData");
        }
        return (int)compositeData.get(s);
    }
    
    protected static boolean isTypeMatched(final CompositeType compositeType, final CompositeType compositeType2) {
        if (compositeType == compositeType2) {
            return true;
        }
        final Set<String> keySet = compositeType.keySet();
        return compositeType2.keySet().containsAll(keySet) && keySet.stream().allMatch(s -> isTypeMatched(compositeType3.getType(s), compositeType4.getType(s)));
    }
    
    protected static boolean isTypeMatched(final TabularType tabularType, final TabularType tabularType2) {
        return tabularType == tabularType2 || (tabularType.getIndexNames().equals(tabularType2.getIndexNames()) && isTypeMatched(tabularType.getRowType(), tabularType2.getRowType()));
    }
    
    protected static boolean isTypeMatched(final ArrayType<?> arrayType, final ArrayType<?> arrayType2) {
        return arrayType == arrayType2 || (arrayType.getDimension() == arrayType2.getDimension() && isTypeMatched(arrayType.getElementOpenType(), arrayType2.getElementOpenType()));
    }
    
    private static boolean isTypeMatched(final OpenType<?> openType, final OpenType<?> openType2) {
        if (openType instanceof CompositeType) {
            if (!(openType2 instanceof CompositeType)) {
                return false;
            }
            if (!isTypeMatched((CompositeType)openType, (CompositeType)openType2)) {
                return false;
            }
        }
        else if (openType instanceof TabularType) {
            if (!(openType2 instanceof TabularType)) {
                return false;
            }
            if (!isTypeMatched((TabularType)openType, (TabularType)openType2)) {
                return false;
            }
        }
        else if (openType instanceof ArrayType) {
            if (!(openType2 instanceof ArrayType)) {
                return false;
            }
            if (!isTypeMatched((ArrayType<?>)openType, (ArrayType<?>)openType2)) {
                return false;
            }
        }
        else if (!openType.equals(openType2)) {
            return false;
        }
        return true;
    }
}
