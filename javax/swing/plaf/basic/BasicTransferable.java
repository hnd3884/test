package javax.swing.plaf.basic;

import java.io.ByteArrayInputStream;
import sun.awt.datatransfer.DataTransferer;
import java.io.IOException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Reader;
import java.awt.datatransfer.DataFlavor;
import javax.swing.plaf.UIResource;
import java.awt.datatransfer.Transferable;

class BasicTransferable implements Transferable, UIResource
{
    protected String plainData;
    protected String htmlData;
    private static DataFlavor[] htmlFlavors;
    private static DataFlavor[] stringFlavors;
    private static DataFlavor[] plainFlavors;
    
    public BasicTransferable(final String plainData, final String htmlData) {
        this.plainData = plainData;
        this.htmlData = htmlData;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        final DataFlavor[] richerFlavors = this.getRicherFlavors();
        final int n = (richerFlavors != null) ? richerFlavors.length : 0;
        final int n2 = this.isHTMLSupported() ? BasicTransferable.htmlFlavors.length : 0;
        final int n3 = this.isPlainSupported() ? BasicTransferable.plainFlavors.length : 0;
        final int n4 = this.isPlainSupported() ? BasicTransferable.stringFlavors.length : 0;
        final DataFlavor[] array = new DataFlavor[n + n2 + n3 + n4];
        int n5 = 0;
        if (n > 0) {
            System.arraycopy(richerFlavors, 0, array, n5, n);
            n5 += n;
        }
        if (n2 > 0) {
            System.arraycopy(BasicTransferable.htmlFlavors, 0, array, n5, n2);
            n5 += n2;
        }
        if (n3 > 0) {
            System.arraycopy(BasicTransferable.plainFlavors, 0, array, n5, n3);
            n5 += n3;
        }
        if (n4 > 0) {
            System.arraycopy(BasicTransferable.stringFlavors, 0, array, n5, n4);
        }
        return array;
    }
    
    @Override
    public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
        final DataFlavor[] transferDataFlavors = this.getTransferDataFlavors();
        for (int i = 0; i < transferDataFlavors.length; ++i) {
            if (transferDataFlavors[i].equals(dataFlavor)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        this.getRicherFlavors();
        if (this.isRicherFlavor(dataFlavor)) {
            return this.getRicherData(dataFlavor);
        }
        if (this.isHTMLFlavor(dataFlavor)) {
            final String htmlData = this.getHTMLData();
            final String s = (htmlData == null) ? "" : htmlData;
            if (String.class.equals(dataFlavor.getRepresentationClass())) {
                return s;
            }
            if (Reader.class.equals(dataFlavor.getRepresentationClass())) {
                return new StringReader(s);
            }
            if (InputStream.class.equals(dataFlavor.getRepresentationClass())) {
                return this.createInputStream(dataFlavor, s);
            }
        }
        else if (this.isPlainFlavor(dataFlavor)) {
            final String plainData = this.getPlainData();
            final String s2 = (plainData == null) ? "" : plainData;
            if (String.class.equals(dataFlavor.getRepresentationClass())) {
                return s2;
            }
            if (Reader.class.equals(dataFlavor.getRepresentationClass())) {
                return new StringReader(s2);
            }
            if (InputStream.class.equals(dataFlavor.getRepresentationClass())) {
                return this.createInputStream(dataFlavor, s2);
            }
        }
        else if (this.isStringFlavor(dataFlavor)) {
            final String plainData2 = this.getPlainData();
            return (plainData2 == null) ? "" : plainData2;
        }
        throw new UnsupportedFlavorException(dataFlavor);
    }
    
    private InputStream createInputStream(final DataFlavor dataFlavor, final String s) throws IOException, UnsupportedFlavorException {
        final String textCharset = DataTransferer.getTextCharset(dataFlavor);
        if (textCharset == null) {
            throw new UnsupportedFlavorException(dataFlavor);
        }
        return new ByteArrayInputStream(s.getBytes(textCharset));
    }
    
    protected boolean isRicherFlavor(final DataFlavor dataFlavor) {
        final DataFlavor[] richerFlavors = this.getRicherFlavors();
        for (int n = (richerFlavors != null) ? richerFlavors.length : 0, i = 0; i < n; ++i) {
            if (richerFlavors[i].equals(dataFlavor)) {
                return true;
            }
        }
        return false;
    }
    
    protected DataFlavor[] getRicherFlavors() {
        return null;
    }
    
    protected Object getRicherData(final DataFlavor dataFlavor) throws UnsupportedFlavorException {
        return null;
    }
    
    protected boolean isHTMLFlavor(final DataFlavor dataFlavor) {
        final DataFlavor[] htmlFlavors = BasicTransferable.htmlFlavors;
        for (int i = 0; i < htmlFlavors.length; ++i) {
            if (htmlFlavors[i].equals(dataFlavor)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isHTMLSupported() {
        return this.htmlData != null;
    }
    
    protected String getHTMLData() {
        return this.htmlData;
    }
    
    protected boolean isPlainFlavor(final DataFlavor dataFlavor) {
        final DataFlavor[] plainFlavors = BasicTransferable.plainFlavors;
        for (int i = 0; i < plainFlavors.length; ++i) {
            if (plainFlavors[i].equals(dataFlavor)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isPlainSupported() {
        return this.plainData != null;
    }
    
    protected String getPlainData() {
        return this.plainData;
    }
    
    protected boolean isStringFlavor(final DataFlavor dataFlavor) {
        final DataFlavor[] stringFlavors = BasicTransferable.stringFlavors;
        for (int i = 0; i < stringFlavors.length; ++i) {
            if (stringFlavors[i].equals(dataFlavor)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        try {
            (BasicTransferable.htmlFlavors = new DataFlavor[3])[0] = new DataFlavor("text/html;class=java.lang.String");
            BasicTransferable.htmlFlavors[1] = new DataFlavor("text/html;class=java.io.Reader");
            BasicTransferable.htmlFlavors[2] = new DataFlavor("text/html;charset=unicode;class=java.io.InputStream");
            (BasicTransferable.plainFlavors = new DataFlavor[3])[0] = new DataFlavor("text/plain;class=java.lang.String");
            BasicTransferable.plainFlavors[1] = new DataFlavor("text/plain;class=java.io.Reader");
            BasicTransferable.plainFlavors[2] = new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream");
            (BasicTransferable.stringFlavors = new DataFlavor[2])[0] = new DataFlavor("application/x-java-jvm-local-objectref;class=java.lang.String");
            BasicTransferable.stringFlavors[1] = DataFlavor.stringFlavor;
        }
        catch (final ClassNotFoundException ex) {
            System.err.println("error initializing javax.swing.plaf.basic.BasicTranserable");
        }
    }
}
