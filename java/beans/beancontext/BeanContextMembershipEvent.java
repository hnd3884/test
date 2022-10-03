package java.beans.beancontext;

import java.util.Iterator;
import java.util.Arrays;
import java.util.Collection;

public class BeanContextMembershipEvent extends BeanContextEvent
{
    private static final long serialVersionUID = 3499346510334590959L;
    protected Collection children;
    
    public BeanContextMembershipEvent(final BeanContext beanContext, final Collection children) {
        super(beanContext);
        if (children == null) {
            throw new NullPointerException("BeanContextMembershipEvent constructor:  changes is null.");
        }
        this.children = children;
    }
    
    public BeanContextMembershipEvent(final BeanContext beanContext, final Object[] array) {
        super(beanContext);
        if (array == null) {
            throw new NullPointerException("BeanContextMembershipEvent:  changes is null.");
        }
        this.children = Arrays.asList(array);
    }
    
    public int size() {
        return this.children.size();
    }
    
    public boolean contains(final Object o) {
        return this.children.contains(o);
    }
    
    public Object[] toArray() {
        return this.children.toArray();
    }
    
    public Iterator iterator() {
        return this.children.iterator();
    }
}
