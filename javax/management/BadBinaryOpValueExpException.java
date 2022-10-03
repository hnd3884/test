package javax.management;

public class BadBinaryOpValueExpException extends Exception
{
    private static final long serialVersionUID = 5068475589449021227L;
    private ValueExp exp;
    
    public BadBinaryOpValueExpException(final ValueExp exp) {
        this.exp = exp;
    }
    
    public ValueExp getExp() {
        return this.exp;
    }
    
    @Override
    public String toString() {
        return "BadBinaryOpValueExpException: " + this.exp;
    }
}
