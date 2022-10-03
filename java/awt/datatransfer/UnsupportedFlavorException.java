package java.awt.datatransfer;

public class UnsupportedFlavorException extends Exception
{
    private static final long serialVersionUID = 5383814944251665601L;
    
    public UnsupportedFlavorException(final DataFlavor dataFlavor) {
        super((dataFlavor != null) ? dataFlavor.getHumanPresentableName() : null);
    }
}
