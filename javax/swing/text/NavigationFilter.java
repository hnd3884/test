package javax.swing.text;

public class NavigationFilter
{
    public void setDot(final FilterBypass filterBypass, final int n, final Position.Bias bias) {
        filterBypass.setDot(n, bias);
    }
    
    public void moveDot(final FilterBypass filterBypass, final int n, final Position.Bias bias) {
        filterBypass.moveDot(n, bias);
    }
    
    public int getNextVisualPositionFrom(final JTextComponent textComponent, final int n, final Position.Bias bias, final int n2, final Position.Bias[] array) throws BadLocationException {
        return textComponent.getUI().getNextVisualPositionFrom(textComponent, n, bias, n2, array);
    }
    
    public abstract static class FilterBypass
    {
        public abstract Caret getCaret();
        
        public abstract void setDot(final int p0, final Position.Bias p1);
        
        public abstract void moveDot(final int p0, final Position.Bias p1);
    }
}
