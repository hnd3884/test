package org.apache.tika.mime;

class Magic implements Clause, Comparable<Magic>
{
    private final MimeType type;
    private final int priority;
    private final Clause clause;
    private final String string;
    
    Magic(final MimeType type, final int priority, final Clause clause) {
        this.type = type;
        this.priority = priority;
        this.clause = clause;
        this.string = "[" + priority + "/" + clause + "]";
    }
    
    MimeType getType() {
        return this.type;
    }
    
    int getPriority() {
        return this.priority;
    }
    
    @Override
    public boolean eval(final byte[] data) {
        return this.clause.eval(data);
    }
    
    @Override
    public int size() {
        return this.clause.size();
    }
    
    @Override
    public String toString() {
        return this.string;
    }
    
    @Override
    public int compareTo(final Magic o) {
        int diff = o.priority - this.priority;
        if (diff == 0) {
            diff = o.size() - this.size();
        }
        if (diff == 0) {
            diff = o.type.compareTo(this.type);
        }
        if (diff == 0) {
            diff = o.string.compareTo(this.string);
        }
        return diff;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Magic) {
            final Magic that = (Magic)o;
            return this.type.equals(that.type) && this.string.equals(that.string);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.type.hashCode() ^ this.string.hashCode();
    }
}
