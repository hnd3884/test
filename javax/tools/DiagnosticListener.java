package javax.tools;

public interface DiagnosticListener<S>
{
    void report(final Diagnostic<? extends S> p0);
}
