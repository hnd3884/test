package sun.font;

public class FontScalerException extends Exception
{
    public FontScalerException() {
        super("Font scaler encountered runtime problem.");
    }
    
    public FontScalerException(final String s) {
        super(s);
    }
}
