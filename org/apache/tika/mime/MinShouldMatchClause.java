package org.apache.tika.mime;

import java.util.Iterator;
import java.util.List;

class MinShouldMatchClause implements Clause
{
    private final int min;
    private final List<Clause> clauses;
    
    MinShouldMatchClause(final int min, final List<Clause> clauses) {
        if (clauses == null || clauses.size() == 0) {
            throw new IllegalArgumentException("clauses must be not null with size > 0");
        }
        if (min > clauses.size()) {
            throw new IllegalArgumentException("min (" + min + ") cannot be > clauses.size (" + clauses.size() + ")");
        }
        if (min <= 0) {
            throw new IllegalArgumentException("min cannot be <= 0: " + min);
        }
        this.min = min;
        this.clauses = clauses;
    }
    
    @Override
    public boolean eval(final byte[] data) {
        int matches = 0;
        for (final Clause clause : this.clauses) {
            if (clause.eval(data) && ++matches >= this.min) {
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
        return "minShouldMatch (min: " + this.min + ") " + this.clauses;
    }
}
