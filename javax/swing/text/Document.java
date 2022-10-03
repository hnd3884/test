package javax.swing.text;

import javax.swing.event.UndoableEditListener;
import javax.swing.event.DocumentListener;

public interface Document
{
    public static final String StreamDescriptionProperty = "stream";
    public static final String TitleProperty = "title";
    
    int getLength();
    
    void addDocumentListener(final DocumentListener p0);
    
    void removeDocumentListener(final DocumentListener p0);
    
    void addUndoableEditListener(final UndoableEditListener p0);
    
    void removeUndoableEditListener(final UndoableEditListener p0);
    
    Object getProperty(final Object p0);
    
    void putProperty(final Object p0, final Object p1);
    
    void remove(final int p0, final int p1) throws BadLocationException;
    
    void insertString(final int p0, final String p1, final AttributeSet p2) throws BadLocationException;
    
    String getText(final int p0, final int p1) throws BadLocationException;
    
    void getText(final int p0, final int p1, final Segment p2) throws BadLocationException;
    
    Position getStartPosition();
    
    Position getEndPosition();
    
    Position createPosition(final int p0) throws BadLocationException;
    
    Element[] getRootElements();
    
    Element getDefaultRootElement();
    
    void render(final Runnable p0);
}
