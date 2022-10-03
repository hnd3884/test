package java.lang;

public final class Void
{
    public static final Class<Void> TYPE;
    
    private Void() {
    }
    
    static {
        TYPE = Class.getPrimitiveClass("void");
    }
}
