package javax.swing.text;

import java.io.Writer;
import java.io.Reader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.Action;
import javax.swing.JEditorPane;
import java.io.Serializable;

public abstract class EditorKit implements Cloneable, Serializable
{
    public Object clone() {
        Object clone;
        try {
            clone = super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            clone = null;
        }
        return clone;
    }
    
    public void install(final JEditorPane editorPane) {
    }
    
    public void deinstall(final JEditorPane editorPane) {
    }
    
    public abstract String getContentType();
    
    public abstract ViewFactory getViewFactory();
    
    public abstract Action[] getActions();
    
    public abstract Caret createCaret();
    
    public abstract Document createDefaultDocument();
    
    public abstract void read(final InputStream p0, final Document p1, final int p2) throws IOException, BadLocationException;
    
    public abstract void write(final OutputStream p0, final Document p1, final int p2, final int p3) throws IOException, BadLocationException;
    
    public abstract void read(final Reader p0, final Document p1, final int p2) throws IOException, BadLocationException;
    
    public abstract void write(final Writer p0, final Document p1, final int p2, final int p3) throws IOException, BadLocationException;
}
