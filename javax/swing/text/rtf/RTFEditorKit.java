package javax.swing.text.rtf;

import java.io.Writer;
import java.io.Reader;
import java.io.OutputStream;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import javax.swing.text.StyledDocument;
import javax.swing.text.Document;
import java.io.InputStream;
import javax.swing.text.StyledEditorKit;

public class RTFEditorKit extends StyledEditorKit
{
    @Override
    public String getContentType() {
        return "text/rtf";
    }
    
    @Override
    public void read(final InputStream inputStream, final Document document, final int n) throws IOException, BadLocationException {
        if (document instanceof StyledDocument) {
            final RTFReader rtfReader = new RTFReader((StyledDocument)document);
            rtfReader.readFromStream(inputStream);
            rtfReader.close();
        }
        else {
            super.read(inputStream, document, n);
        }
    }
    
    @Override
    public void write(final OutputStream outputStream, final Document document, final int n, final int n2) throws IOException, BadLocationException {
        RTFGenerator.writeDocument(document, outputStream);
    }
    
    @Override
    public void read(final Reader reader, final Document document, final int n) throws IOException, BadLocationException {
        if (document instanceof StyledDocument) {
            final RTFReader rtfReader = new RTFReader((StyledDocument)document);
            rtfReader.readFromReader(reader);
            rtfReader.close();
        }
        else {
            super.read(reader, document, n);
        }
    }
    
    @Override
    public void write(final Writer writer, final Document document, final int n, final int n2) throws IOException, BadLocationException {
        throw new IOException("RTF is an 8-bit format");
    }
}
