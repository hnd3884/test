package javax.naming;

public class Binding extends NameClassPair
{
    private Object boundObj;
    private static final long serialVersionUID = 8839217842691845890L;
    
    public Binding(final String s, final Object boundObj) {
        super(s, null);
        this.boundObj = boundObj;
    }
    
    public Binding(final String s, final Object boundObj, final boolean b) {
        super(s, null, b);
        this.boundObj = boundObj;
    }
    
    public Binding(final String s, final String s2, final Object boundObj) {
        super(s, s2);
        this.boundObj = boundObj;
    }
    
    public Binding(final String s, final String s2, final Object boundObj, final boolean b) {
        super(s, s2, b);
        this.boundObj = boundObj;
    }
    
    @Override
    public String getClassName() {
        final String className = super.getClassName();
        if (className != null) {
            return className;
        }
        if (this.boundObj != null) {
            return this.boundObj.getClass().getName();
        }
        return null;
    }
    
    public Object getObject() {
        return this.boundObj;
    }
    
    public void setObject(final Object boundObj) {
        this.boundObj = boundObj;
    }
    
    @Override
    public String toString() {
        return super.toString() + ":" + this.getObject();
    }
}
