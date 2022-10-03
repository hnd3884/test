package org.apache.tika.mime;

import java.util.Iterator;
import java.util.List;

class OrClause implements Clause
{
    private final List<Clause> clauses;
    
    OrClause(final List<Clause> clauses) {
        this.clauses = clauses;
    }
    
    @Override
    public boolean eval(final byte[] data) {
        for (final Clause clause : this.clauses) {
            if (clause.eval(data)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int size() {
        int size = 0;
        for (final Clause clause : this.clauses) {
            size = Math.max(size, clause.size());
        }
        return size;
    }
    
    @Override
    public String toString() {
        return "or" + this.clauses;
    }
}
