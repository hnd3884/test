package org.apache.tika.mime;

import java.util.Arrays;

class AndClause implements Clause
{
    private final Clause[] clauses;
    
    AndClause(final Clause... clauses) {
        this.clauses = clauses;
    }
    
    @Override
    public boolean eval(final byte[] data) {
        for (final Clause clause : this.clauses) {
            if (!clause.eval(data)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int size() {
        int size = 0;
        for (final Clause clause : this.clauses) {
            size += clause.size();
        }
        return size;
    }
    
    @Override
    public String toString() {
        return "and" + Arrays.toString(this.clauses);
    }
}
