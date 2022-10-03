package javax.tools;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public final class DiagnosticCollector<S> implements DiagnosticListener<S>
{
    private List<Diagnostic<? extends S>> diagnostics;
    
    public DiagnosticCollector() {
        this.diagnostics = Collections.synchronizedList(new ArrayList<Diagnostic<? extends S>>());
    }
    
    @Override
    public void report(final Diagnostic<? extends S> diagnostic) {
        diagnostic.getClass();
        this.diagnostics.add(diagnostic);
    }
    
    public List<Diagnostic<? extends S>> getDiagnostics() {
        return Collections.unmodifiableList((List<? extends Diagnostic<? extends S>>)this.diagnostics);
    }
}
