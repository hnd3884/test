package org.jvnet.hk2.internal;

import java.util.LinkedList;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.util.List;

public class ImmediateResults
{
    private final NarrowResults timelessResults;
    private final List<ActiveDescriptor<?>> validatedImmediateResults;
    
    ImmediateResults(final NarrowResults cachedResults) {
        this.validatedImmediateResults = new LinkedList<ActiveDescriptor<?>>();
        if (cachedResults == null) {
            this.timelessResults = new NarrowResults();
        }
        else {
            this.timelessResults = cachedResults;
        }
    }
    
    NarrowResults getTimelessResults() {
        return this.timelessResults;
    }
    
    List<ActiveDescriptor<?>> getImmediateResults() {
        return this.validatedImmediateResults;
    }
    
    void addValidatedResult(final ActiveDescriptor<?> addMe) {
        this.validatedImmediateResults.add(addMe);
    }
    
    @Override
    public String toString() {
        return "ImmediateResults(" + this.timelessResults + "," + this.validatedImmediateResults + "," + System.identityHashCode(this) + ")";
    }
}
