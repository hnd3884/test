package javax.swing;

import java.util.Arrays;
import java.util.List;
import java.io.Serializable;

public class SpinnerListModel extends AbstractSpinnerModel implements Serializable
{
    private List list;
    private int index;
    
    public SpinnerListModel(final List<?> list) {
        if (list == null || list.size() == 0) {
            throw new IllegalArgumentException("SpinnerListModel(List) expects non-null non-empty List");
        }
        this.list = list;
        this.index = 0;
    }
    
    public SpinnerListModel(final Object[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("SpinnerListModel(Object[]) expects non-null non-empty Object[]");
        }
        this.list = Arrays.asList(array);
        this.index = 0;
    }
    
    public SpinnerListModel() {
        this(new Object[] { "empty" });
    }
    
    public List<?> getList() {
        return this.list;
    }
    
    public void setList(final List<?> list) {
        if (list == null || list.size() == 0) {
            throw new IllegalArgumentException("invalid list");
        }
        if (!list.equals(this.list)) {
            this.list = list;
            this.index = 0;
            this.fireStateChanged();
        }
    }
    
    @Override
    public Object getValue() {
        return this.list.get(this.index);
    }
    
    @Override
    public void setValue(final Object o) {
        final int index = this.list.indexOf(o);
        if (index == -1) {
            throw new IllegalArgumentException("invalid sequence element");
        }
        if (index != this.index) {
            this.index = index;
            this.fireStateChanged();
        }
    }
    
    @Override
    public Object getNextValue() {
        return (this.index >= this.list.size() - 1) ? null : this.list.get(this.index + 1);
    }
    
    @Override
    public Object getPreviousValue() {
        return (this.index <= 0) ? null : this.list.get(this.index - 1);
    }
    
    Object findNextMatch(final String s) {
        final int size = this.list.size();
        if (size == 0) {
            return null;
        }
        int i = this.index;
        do {
            final Object value = this.list.get(i);
            final String string = value.toString();
            if (string != null && string.startsWith(s)) {
                return value;
            }
            i = (i + 1) % size;
        } while (i != this.index);
        return null;
    }
}
