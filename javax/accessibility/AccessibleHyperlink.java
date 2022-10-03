package javax.accessibility;

public abstract class AccessibleHyperlink implements AccessibleAction
{
    public abstract boolean isValid();
    
    @Override
    public abstract int getAccessibleActionCount();
    
    @Override
    public abstract boolean doAccessibleAction(final int p0);
    
    @Override
    public abstract String getAccessibleActionDescription(final int p0);
    
    public abstract Object getAccessibleActionObject(final int p0);
    
    public abstract Object getAccessibleActionAnchor(final int p0);
    
    public abstract int getStartIndex();
    
    public abstract int getEndIndex();
}
