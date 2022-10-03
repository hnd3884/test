package javax.swing.text;

public class DocumentFilter
{
    public void remove(final FilterBypass filterBypass, final int n, final int n2) throws BadLocationException {
        filterBypass.remove(n, n2);
    }
    
    public void insertString(final FilterBypass filterBypass, final int n, final String s, final AttributeSet set) throws BadLocationException {
        filterBypass.insertString(n, s, set);
    }
    
    public void replace(final FilterBypass filterBypass, final int n, final int n2, final String s, final AttributeSet set) throws BadLocationException {
        filterBypass.replace(n, n2, s, set);
    }
    
    public abstract static class FilterBypass
    {
        public abstract Document getDocument();
        
        public abstract void remove(final int p0, final int p1) throws BadLocationException;
        
        public abstract void insertString(final int p0, final String p1, final AttributeSet p2) throws BadLocationException;
        
        public abstract void replace(final int p0, final int p1, final String p2, final AttributeSet p3) throws BadLocationException;
    }
}
