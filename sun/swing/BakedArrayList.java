package sun.swing;

import java.util.List;
import java.util.ArrayList;

public class BakedArrayList extends ArrayList
{
    private int _hashCode;
    
    public BakedArrayList(final int n) {
        super(n);
    }
    
    public BakedArrayList(final List list) {
        this(list.size());
        for (int i = 0; i < list.size(); ++i) {
            this.add(list.get(i));
        }
        this.cacheHashCode();
    }
    
    public void cacheHashCode() {
        this._hashCode = 1;
        for (int i = this.size() - 1; i >= 0; --i) {
            this._hashCode = 31 * this._hashCode + this.get(i).hashCode();
        }
    }
    
    @Override
    public int hashCode() {
        return this._hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        final BakedArrayList list = (BakedArrayList)o;
        int size = this.size();
        if (list.size() != size) {
            return false;
        }
        while (size-- > 0) {
            if (!this.get(size).equals(list.get(size))) {
                return false;
            }
        }
        return true;
    }
}
