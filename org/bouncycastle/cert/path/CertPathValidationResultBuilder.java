package org.bouncycastle.cert.path;

import org.bouncycastle.util.Integers;
import java.util.ArrayList;
import java.util.List;

class CertPathValidationResultBuilder
{
    private final CertPathValidationContext context;
    private final List<Integer> certIndexes;
    private final List<Integer> ruleIndexes;
    private final List<CertPathValidationException> exceptions;
    
    CertPathValidationResultBuilder(final CertPathValidationContext context) {
        this.certIndexes = new ArrayList<Integer>();
        this.ruleIndexes = new ArrayList<Integer>();
        this.exceptions = new ArrayList<CertPathValidationException>();
        this.context = context;
    }
    
    public CertPathValidationResult build() {
        if (this.exceptions.isEmpty()) {
            return new CertPathValidationResult(this.context);
        }
        return new CertPathValidationResult(this.context, this.toInts(this.certIndexes), this.toInts(this.ruleIndexes), this.exceptions.toArray(new CertPathValidationException[this.exceptions.size()]));
    }
    
    public void addException(final int n, final int n2, final CertPathValidationException ex) {
        this.certIndexes.add(Integers.valueOf(n));
        this.ruleIndexes.add(Integers.valueOf(n2));
        this.exceptions.add(ex);
    }
    
    private int[] toInts(final List<Integer> list) {
        final int[] array = new int[list.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (int)list.get(i);
        }
        return array;
    }
}
