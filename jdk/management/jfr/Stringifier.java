package jdk.management.jfr;

final class Stringifier
{
    private final StringBuilder sb;
    private boolean first;
    
    Stringifier() {
        this.sb = new StringBuilder();
        this.first = true;
    }
    
    public void add(final String s, final Object o) {
        if (this.first) {
            this.first = false;
        }
        else {
            this.sb.append(" ");
        }
        final boolean b = o instanceof String;
        this.sb.append(s).append("=");
        if (o == null) {
            this.sb.append("null");
        }
        else {
            if (b) {
                this.sb.append("\"");
            }
            this.sb.append(o);
            if (b) {
                this.sb.append("\"");
            }
        }
    }
    
    @Override
    public String toString() {
        return this.sb.toString();
    }
}
