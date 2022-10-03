package java.beans;

public abstract class PersistenceDelegate
{
    public void writeObject(final Object o, final Encoder encoder) {
        final Object value = encoder.get(o);
        if (!this.mutatesTo(o, value)) {
            encoder.remove(o);
            encoder.writeExpression(this.instantiate(o, encoder));
        }
        else {
            this.initialize(o.getClass(), o, value, encoder);
        }
    }
    
    protected boolean mutatesTo(final Object o, final Object o2) {
        return o2 != null && o != null && o.getClass() == o2.getClass();
    }
    
    protected abstract Expression instantiate(final Object p0, final Encoder p1);
    
    protected void initialize(final Class<?> clazz, final Object o, final Object o2, final Encoder encoder) {
        final Class<?> superclass = clazz.getSuperclass();
        encoder.getPersistenceDelegate(superclass).initialize(superclass, o, o2, encoder);
    }
}
