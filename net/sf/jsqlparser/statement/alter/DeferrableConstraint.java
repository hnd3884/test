package net.sf.jsqlparser.statement.alter;

public class DeferrableConstraint implements ConstraintState
{
    private boolean not;
    
    public DeferrableConstraint(final boolean not) {
        this.not = not;
    }
    
    public boolean isNot() {
        return this.not;
    }
    
    public void setNot(final boolean not) {
        this.not = not;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        if (this.not) {
            b.append("NOT ");
        }
        b.append("DEFERRABLE");
        return b.toString();
    }
}
