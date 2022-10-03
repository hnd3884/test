package org.omg.CORBA;

public abstract class ServerRequest
{
    @Deprecated
    public String op_name() {
        return this.operation();
    }
    
    public String operation() {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public void params(final NVList list) {
        this.arguments(list);
    }
    
    public void arguments(final NVList list) {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public void result(final Any any) {
        this.set_result(any);
    }
    
    public void set_result(final Any any) {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public void except(final Any any) {
        this.set_exception(any);
    }
    
    public void set_exception(final Any any) {
        throw new NO_IMPLEMENT();
    }
    
    public abstract Context ctx();
}
