package net.sf.jsqlparser.expression;

import java.util.List;

public class MySQLIndexHint
{
    private final String action;
    private final String indexQualifier;
    private final List<String> indexNames;
    
    public MySQLIndexHint(final String action, final String indexQualifier, final List<String> indexNames) {
        this.action = action;
        this.indexQualifier = indexQualifier;
        this.indexNames = indexNames;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(" ").append(this.action).append(" ").append(this.indexQualifier).append(" (");
        for (int i = 0; i < this.indexNames.size(); ++i) {
            if (i > 0) {
                buffer.append(",");
            }
            buffer.append(this.indexNames.get(i));
        }
        buffer.append(")");
        return buffer.toString();
    }
}
