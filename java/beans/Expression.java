package java.beans;

public class Expression extends Statement
{
    private static Object unbound;
    private Object value;
    
    @ConstructorProperties({ "target", "methodName", "arguments" })
    public Expression(final Object o, final String s, final Object[] array) {
        super(o, s, array);
        this.value = Expression.unbound;
    }
    
    public Expression(final Object value, final Object o, final String s, final Object[] array) {
        this(o, s, array);
        this.setValue(value);
    }
    
    @Override
    public void execute() throws Exception {
        this.setValue(this.invoke());
    }
    
    public Object getValue() throws Exception {
        if (this.value == Expression.unbound) {
            this.setValue(this.invoke());
        }
        return this.value;
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    @Override
    String instanceName(final Object o) {
        return (o == Expression.unbound) ? "<unbound>" : super.instanceName(o);
    }
    
    @Override
    public String toString() {
        return this.instanceName(this.value) + "=" + super.toString();
    }
    
    static {
        Expression.unbound = new Object();
    }
}
