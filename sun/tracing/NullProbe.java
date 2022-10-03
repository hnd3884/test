package sun.tracing;

class NullProbe extends ProbeSkeleton
{
    public NullProbe(final Class<?>[] array) {
        super(array);
    }
    
    @Override
    public boolean isEnabled() {
        return false;
    }
    
    @Override
    public void uncheckedTrigger(final Object[] array) {
    }
}
