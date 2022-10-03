package javax.print;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DocFlavor implements Serializable, Cloneable
{
    private static final long serialVersionUID = -4512080796965449721L;
    public static final String hostEncoding;
    private transient MimeType myMimeType;
    private String myClassName;
    private transient String myStringValue;
    
    public DocFlavor(final String s, final String myClassName) {
        this.myStringValue = null;
        if (myClassName == null) {
            throw new NullPointerException();
        }
        this.myMimeType = new MimeType(s);
        this.myClassName = myClassName;
    }
    
    public String getMimeType() {
        return this.myMimeType.getMimeType();
    }
    
    public String getMediaType() {
        return this.myMimeType.getMediaType();
    }
    
    public String getMediaSubtype() {
        return this.myMimeType.getMediaSubtype();
    }
    
    public String getParameter(final String s) {
        return this.myMimeType.getParameterMap().get(s.toLowerCase());
    }
    
    public String getRepresentationClassName() {
        return this.myClassName;
    }
    
    @Override
    public String toString() {
        return this.getStringValue();
    }
    
    @Override
    public int hashCode() {
        return this.getStringValue().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof DocFlavor && this.getStringValue().equals(((DocFlavor)o).getStringValue());
    }
    
    private String getStringValue() {
        if (this.myStringValue == null) {
            this.myStringValue = this.myMimeType + "; class=\"" + this.myClassName + "\"";
        }
        return this.myStringValue;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.myMimeType.getMimeType());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.myMimeType = new MimeType((String)objectInputStream.readObject());
    }
    
    static {
        hostEncoding = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("file.encoding"));
    }
    
    public static class BYTE_ARRAY extends DocFlavor
    {
        private static final long serialVersionUID = -9065578006593857475L;
        public static final BYTE_ARRAY TEXT_PLAIN_HOST;
        public static final BYTE_ARRAY TEXT_PLAIN_UTF_8;
        public static final BYTE_ARRAY TEXT_PLAIN_UTF_16;
        public static final BYTE_ARRAY TEXT_PLAIN_UTF_16BE;
        public static final BYTE_ARRAY TEXT_PLAIN_UTF_16LE;
        public static final BYTE_ARRAY TEXT_PLAIN_US_ASCII;
        public static final BYTE_ARRAY TEXT_HTML_HOST;
        public static final BYTE_ARRAY TEXT_HTML_UTF_8;
        public static final BYTE_ARRAY TEXT_HTML_UTF_16;
        public static final BYTE_ARRAY TEXT_HTML_UTF_16BE;
        public static final BYTE_ARRAY TEXT_HTML_UTF_16LE;
        public static final BYTE_ARRAY TEXT_HTML_US_ASCII;
        public static final BYTE_ARRAY PDF;
        public static final BYTE_ARRAY POSTSCRIPT;
        public static final BYTE_ARRAY PCL;
        public static final BYTE_ARRAY GIF;
        public static final BYTE_ARRAY JPEG;
        public static final BYTE_ARRAY PNG;
        public static final BYTE_ARRAY AUTOSENSE;
        
        public BYTE_ARRAY(final String s) {
            super(s, "[B");
        }
        
        static {
            TEXT_PLAIN_HOST = new BYTE_ARRAY("text/plain; charset=" + BYTE_ARRAY.hostEncoding);
            TEXT_PLAIN_UTF_8 = new BYTE_ARRAY("text/plain; charset=utf-8");
            TEXT_PLAIN_UTF_16 = new BYTE_ARRAY("text/plain; charset=utf-16");
            TEXT_PLAIN_UTF_16BE = new BYTE_ARRAY("text/plain; charset=utf-16be");
            TEXT_PLAIN_UTF_16LE = new BYTE_ARRAY("text/plain; charset=utf-16le");
            TEXT_PLAIN_US_ASCII = new BYTE_ARRAY("text/plain; charset=us-ascii");
            TEXT_HTML_HOST = new BYTE_ARRAY("text/html; charset=" + BYTE_ARRAY.hostEncoding);
            TEXT_HTML_UTF_8 = new BYTE_ARRAY("text/html; charset=utf-8");
            TEXT_HTML_UTF_16 = new BYTE_ARRAY("text/html; charset=utf-16");
            TEXT_HTML_UTF_16BE = new BYTE_ARRAY("text/html; charset=utf-16be");
            TEXT_HTML_UTF_16LE = new BYTE_ARRAY("text/html; charset=utf-16le");
            TEXT_HTML_US_ASCII = new BYTE_ARRAY("text/html; charset=us-ascii");
            PDF = new BYTE_ARRAY("application/pdf");
            POSTSCRIPT = new BYTE_ARRAY("application/postscript");
            PCL = new BYTE_ARRAY("application/vnd.hp-PCL");
            GIF = new BYTE_ARRAY("image/gif");
            JPEG = new BYTE_ARRAY("image/jpeg");
            PNG = new BYTE_ARRAY("image/png");
            AUTOSENSE = new BYTE_ARRAY("application/octet-stream");
        }
    }
    
    public static class INPUT_STREAM extends DocFlavor
    {
        private static final long serialVersionUID = -7045842700749194127L;
        public static final INPUT_STREAM TEXT_PLAIN_HOST;
        public static final INPUT_STREAM TEXT_PLAIN_UTF_8;
        public static final INPUT_STREAM TEXT_PLAIN_UTF_16;
        public static final INPUT_STREAM TEXT_PLAIN_UTF_16BE;
        public static final INPUT_STREAM TEXT_PLAIN_UTF_16LE;
        public static final INPUT_STREAM TEXT_PLAIN_US_ASCII;
        public static final INPUT_STREAM TEXT_HTML_HOST;
        public static final INPUT_STREAM TEXT_HTML_UTF_8;
        public static final INPUT_STREAM TEXT_HTML_UTF_16;
        public static final INPUT_STREAM TEXT_HTML_UTF_16BE;
        public static final INPUT_STREAM TEXT_HTML_UTF_16LE;
        public static final INPUT_STREAM TEXT_HTML_US_ASCII;
        public static final INPUT_STREAM PDF;
        public static final INPUT_STREAM POSTSCRIPT;
        public static final INPUT_STREAM PCL;
        public static final INPUT_STREAM GIF;
        public static final INPUT_STREAM JPEG;
        public static final INPUT_STREAM PNG;
        public static final INPUT_STREAM AUTOSENSE;
        
        public INPUT_STREAM(final String s) {
            super(s, "java.io.InputStream");
        }
        
        static {
            TEXT_PLAIN_HOST = new INPUT_STREAM("text/plain; charset=" + INPUT_STREAM.hostEncoding);
            TEXT_PLAIN_UTF_8 = new INPUT_STREAM("text/plain; charset=utf-8");
            TEXT_PLAIN_UTF_16 = new INPUT_STREAM("text/plain; charset=utf-16");
            TEXT_PLAIN_UTF_16BE = new INPUT_STREAM("text/plain; charset=utf-16be");
            TEXT_PLAIN_UTF_16LE = new INPUT_STREAM("text/plain; charset=utf-16le");
            TEXT_PLAIN_US_ASCII = new INPUT_STREAM("text/plain; charset=us-ascii");
            TEXT_HTML_HOST = new INPUT_STREAM("text/html; charset=" + INPUT_STREAM.hostEncoding);
            TEXT_HTML_UTF_8 = new INPUT_STREAM("text/html; charset=utf-8");
            TEXT_HTML_UTF_16 = new INPUT_STREAM("text/html; charset=utf-16");
            TEXT_HTML_UTF_16BE = new INPUT_STREAM("text/html; charset=utf-16be");
            TEXT_HTML_UTF_16LE = new INPUT_STREAM("text/html; charset=utf-16le");
            TEXT_HTML_US_ASCII = new INPUT_STREAM("text/html; charset=us-ascii");
            PDF = new INPUT_STREAM("application/pdf");
            POSTSCRIPT = new INPUT_STREAM("application/postscript");
            PCL = new INPUT_STREAM("application/vnd.hp-PCL");
            GIF = new INPUT_STREAM("image/gif");
            JPEG = new INPUT_STREAM("image/jpeg");
            PNG = new INPUT_STREAM("image/png");
            AUTOSENSE = new INPUT_STREAM("application/octet-stream");
        }
    }
    
    public static class URL extends DocFlavor
    {
        public static final URL TEXT_PLAIN_HOST;
        public static final URL TEXT_PLAIN_UTF_8;
        public static final URL TEXT_PLAIN_UTF_16;
        public static final URL TEXT_PLAIN_UTF_16BE;
        public static final URL TEXT_PLAIN_UTF_16LE;
        public static final URL TEXT_PLAIN_US_ASCII;
        public static final URL TEXT_HTML_HOST;
        public static final URL TEXT_HTML_UTF_8;
        public static final URL TEXT_HTML_UTF_16;
        public static final URL TEXT_HTML_UTF_16BE;
        public static final URL TEXT_HTML_UTF_16LE;
        public static final URL TEXT_HTML_US_ASCII;
        public static final URL PDF;
        public static final URL POSTSCRIPT;
        public static final URL PCL;
        public static final URL GIF;
        public static final URL JPEG;
        public static final URL PNG;
        public static final URL AUTOSENSE;
        
        public URL(final String s) {
            super(s, "java.net.URL");
        }
        
        static {
            TEXT_PLAIN_HOST = new URL("text/plain; charset=" + URL.hostEncoding);
            TEXT_PLAIN_UTF_8 = new URL("text/plain; charset=utf-8");
            TEXT_PLAIN_UTF_16 = new URL("text/plain; charset=utf-16");
            TEXT_PLAIN_UTF_16BE = new URL("text/plain; charset=utf-16be");
            TEXT_PLAIN_UTF_16LE = new URL("text/plain; charset=utf-16le");
            TEXT_PLAIN_US_ASCII = new URL("text/plain; charset=us-ascii");
            TEXT_HTML_HOST = new URL("text/html; charset=" + URL.hostEncoding);
            TEXT_HTML_UTF_8 = new URL("text/html; charset=utf-8");
            TEXT_HTML_UTF_16 = new URL("text/html; charset=utf-16");
            TEXT_HTML_UTF_16BE = new URL("text/html; charset=utf-16be");
            TEXT_HTML_UTF_16LE = new URL("text/html; charset=utf-16le");
            TEXT_HTML_US_ASCII = new URL("text/html; charset=us-ascii");
            PDF = new URL("application/pdf");
            POSTSCRIPT = new URL("application/postscript");
            PCL = new URL("application/vnd.hp-PCL");
            GIF = new URL("image/gif");
            JPEG = new URL("image/jpeg");
            PNG = new URL("image/png");
            AUTOSENSE = new URL("application/octet-stream");
        }
    }
    
    public static class CHAR_ARRAY extends DocFlavor
    {
        private static final long serialVersionUID = -8720590903724405128L;
        public static final CHAR_ARRAY TEXT_PLAIN;
        public static final CHAR_ARRAY TEXT_HTML;
        
        public CHAR_ARRAY(final String s) {
            super(s, "[C");
        }
        
        static {
            TEXT_PLAIN = new CHAR_ARRAY("text/plain; charset=utf-16");
            TEXT_HTML = new CHAR_ARRAY("text/html; charset=utf-16");
        }
    }
    
    public static class STRING extends DocFlavor
    {
        private static final long serialVersionUID = 4414407504887034035L;
        public static final STRING TEXT_PLAIN;
        public static final STRING TEXT_HTML;
        
        public STRING(final String s) {
            super(s, "java.lang.String");
        }
        
        static {
            TEXT_PLAIN = new STRING("text/plain; charset=utf-16");
            TEXT_HTML = new STRING("text/html; charset=utf-16");
        }
    }
    
    public static class READER extends DocFlavor
    {
        private static final long serialVersionUID = 7100295812579351567L;
        public static final READER TEXT_PLAIN;
        public static final READER TEXT_HTML;
        
        public READER(final String s) {
            super(s, "java.io.Reader");
        }
        
        static {
            TEXT_PLAIN = new READER("text/plain; charset=utf-16");
            TEXT_HTML = new READER("text/html; charset=utf-16");
        }
    }
    
    public static class SERVICE_FORMATTED extends DocFlavor
    {
        private static final long serialVersionUID = 6181337766266637256L;
        public static final SERVICE_FORMATTED RENDERABLE_IMAGE;
        public static final SERVICE_FORMATTED PRINTABLE;
        public static final SERVICE_FORMATTED PAGEABLE;
        
        public SERVICE_FORMATTED(final String s) {
            super("application/x-java-jvm-local-objectref", s);
        }
        
        static {
            RENDERABLE_IMAGE = new SERVICE_FORMATTED("java.awt.image.renderable.RenderableImage");
            PRINTABLE = new SERVICE_FORMATTED("java.awt.print.Printable");
            PAGEABLE = new SERVICE_FORMATTED("java.awt.print.Pageable");
        }
    }
}
