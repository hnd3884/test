package java.awt.dnd;

public class InvalidDnDOperationException extends IllegalStateException
{
    private static final long serialVersionUID = -6062568741193956678L;
    private static String dft_msg;
    
    public InvalidDnDOperationException() {
        super(InvalidDnDOperationException.dft_msg);
    }
    
    public InvalidDnDOperationException(final String s) {
        super(s);
    }
    
    static {
        InvalidDnDOperationException.dft_msg = "The operation requested cannot be performed by the DnD system since it is not in the appropriate state";
    }
}
