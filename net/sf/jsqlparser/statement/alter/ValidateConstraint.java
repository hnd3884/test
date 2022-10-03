package net.sf.jsqlparser.statement.alter;

public class ValidateConstraint implements ConstraintState
{
    private boolean not;
    
    public ValidateConstraint(final boolean not) {
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
        return this.not ? "NOVALIDATE" : "VALIDATE";
    }
}
