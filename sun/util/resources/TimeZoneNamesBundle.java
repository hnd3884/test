package sun.util.resources;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class TimeZoneNamesBundle extends OpenListResourceBundle
{
    public Object handleGetObject(final String s) {
        final String[] array = (String[])super.handleGetObject(s);
        if (Objects.isNull(array)) {
            return null;
        }
        final int length = array.length;
        final String[] array2 = new String[7];
        array2[0] = s;
        System.arraycopy(array, 0, array2, 1, length);
        return array2;
    }
    
    @Override
    protected <K, V> Map<K, V> createMap(final int n) {
        return new LinkedHashMap<K, V>(n);
    }
    
    @Override
    protected <E> Set<E> createSet() {
        return new LinkedHashSet<E>();
    }
    
    @Override
    protected abstract Object[][] getContents();
}
