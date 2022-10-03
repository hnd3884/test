package javax.servlet.jsp.tagext;

public interface TryCatchFinally
{
    void doCatch(final Throwable p0) throws Throwable;
    
    void doFinally();
}
