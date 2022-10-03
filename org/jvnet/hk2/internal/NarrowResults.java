package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Injectee;
import java.util.LinkedList;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.util.List;

public class NarrowResults
{
    private List<ActiveDescriptor<?>> unnarrowedResults;
    private final List<ActiveDescriptor<?>> goodResults;
    private final List<ErrorResults> errors;
    
    public NarrowResults() {
        this.goodResults = new LinkedList<ActiveDescriptor<?>>();
        this.errors = new LinkedList<ErrorResults>();
    }
    
    void addGoodResult(final ActiveDescriptor<?> result) {
        this.goodResults.add(result);
    }
    
    void addError(final ActiveDescriptor<?> fail, final Injectee injectee, final MultiException me) {
        this.errors.add(new ErrorResults(fail, injectee, me));
    }
    
    List<ActiveDescriptor<?>> getResults() {
        return this.goodResults;
    }
    
    List<ErrorResults> getErrors() {
        return this.errors;
    }
    
    void setUnnarrowedResults(final List<ActiveDescriptor<?>> unnarrowed) {
        this.unnarrowedResults = unnarrowed;
    }
    
    ActiveDescriptor<?> removeUnnarrowedResult() {
        if (this.unnarrowedResults == null || this.unnarrowedResults.isEmpty()) {
            return null;
        }
        return this.unnarrowedResults.remove(0);
    }
    
    @Override
    public String toString() {
        return "NarrowResults(goodResultsSize=" + this.goodResults.size() + ",errorsSize=" + this.errors.size() + "," + System.identityHashCode(this) + ")";
    }
}
