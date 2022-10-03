package sun.tracing;

import com.sun.tracing.Probe;

public abstract class ProbeSkeleton implements Probe
{
    protected Class<?>[] parameters;
    
    protected ProbeSkeleton(final Class<?>[] parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public abstract boolean isEnabled();
    
    public abstract void uncheckedTrigger(final Object[] p0);
    
    private static boolean isAssignable(final Object o, final Class<?> clazz) {
        if (o != null && !clazz.isInstance(o)) {
            if (clazz.isPrimitive()) {
                try {
                    return clazz.isAssignableFrom((Class<?>)o.getClass().getField("TYPE").get(null));
                }
                catch (final Exception ex) {}
            }
            return false;
        }
        return true;
    }
    
    @Override
    public void trigger(final Object... array) {
        if (array.length != this.parameters.length) {
            throw new IllegalArgumentException("Wrong number of arguments");
        }
        for (int i = 0; i < this.parameters.length; ++i) {
            if (!isAssignable(array[i], this.parameters[i])) {
                throw new IllegalArgumentException("Wrong type of argument at position " + i);
            }
        }
        this.uncheckedTrigger(array);
    }
}
