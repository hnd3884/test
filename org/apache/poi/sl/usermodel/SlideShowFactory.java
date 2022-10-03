package org.apache.poi.sl.usermodel;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import java.io.FileNotFoundException;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;
import java.io.InputStream;

public abstract class SlideShowFactory
{
    protected static CreateSlideShow1<InputStream> createXslfByStream;
    protected static CreateSlideShow2<File, Boolean> createXslfByFile;
    protected static CreateSlideShow1<POIFSFileSystem> createHslfByPoifs;
    protected static CreateSlideShow1<DirectoryNode> createHslfByNode;
    
    public static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> SlideShow<S, P> create(final POIFSFileSystem fs) throws IOException {
        return create(fs, null);
    }
    
    public static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> SlideShow<S, P> create(final POIFSFileSystem fs, final String password) throws IOException {
        return create(fs.getRoot(), password);
    }
    
    public static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> SlideShow<S, P> create(final DirectoryNode root) throws IOException {
        return create(root, null);
    }
    
    public static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> SlideShow<S, P> create(final DirectoryNode root, final String password) throws IOException {
        if (root.hasEntry("EncryptedPackage")) {
            InputStream stream = null;
            try {
                stream = DocumentFactoryHelper.getDecryptedStream(root, password);
                initXslf();
                return (SlideShow<S, P>)SlideShowFactory.createXslfByStream.apply(stream);
            }
            finally {
                IOUtils.closeQuietly(stream);
                root.getFileSystem().close();
            }
        }
        boolean passwordSet = false;
        if (password != null) {
            Biff8EncryptionKey.setCurrentUserPassword(password);
            passwordSet = true;
        }
        try {
            initHslf();
            return (SlideShow<S, P>)SlideShowFactory.createHslfByNode.apply(root);
        }
        finally {
            if (passwordSet) {
                Biff8EncryptionKey.setCurrentUserPassword(null);
            }
        }
    }
    
    public static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> SlideShow<S, P> create(final InputStream inp) throws IOException, EncryptedDocumentException {
        return create(inp, null);
    }
    
    public static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> SlideShow<S, P> create(final InputStream inp, final String password) throws IOException, EncryptedDocumentException {
        final InputStream is = FileMagic.prepareToCheckMagic(inp);
        final FileMagic fm = FileMagic.valueOf(is);
        switch (fm) {
            case OLE2: {
                final POIFSFileSystem fs = new POIFSFileSystem(is);
                return create(fs, password);
            }
            case OOXML: {
                initXslf();
                return (SlideShow<S, P>)SlideShowFactory.createXslfByStream.apply(is);
            }
            default: {
                throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
            }
        }
    }
    
    public static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> SlideShow<S, P> create(final File file) throws IOException, EncryptedDocumentException {
        return create(file, null);
    }
    
    public static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> SlideShow<S, P> create(final File file, final String password) throws IOException, EncryptedDocumentException {
        return create(file, password, false);
    }
    
    public static <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> SlideShow<S, P> create(final File file, final String password, final boolean readOnly) throws IOException, EncryptedDocumentException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        POIFSFileSystem fs = null;
        try {
            fs = new POIFSFileSystem(file, readOnly);
            return create(fs, password);
        }
        catch (final OfficeXmlFileException e) {
            IOUtils.closeQuietly(fs);
            initXslf();
            return (SlideShow<S, P>)SlideShowFactory.createXslfByFile.apply(file, readOnly);
        }
        catch (final RuntimeException e2) {
            IOUtils.closeQuietly(fs);
            throw e2;
        }
    }
    
    private static void initXslf() throws IOException {
        if (SlideShowFactory.createXslfByFile == null) {
            initFactory("org.apache.poi.xslf.usermodel.XSLFSlideShowFactory", "poi-ooxml-*.jar");
        }
    }
    
    private static void initHslf() throws IOException {
        if (SlideShowFactory.createHslfByPoifs == null) {
            initFactory("org.apache.poi.hslf.usermodel.HSLFSlideShowFactory", "poi-scratchpad-*.jar");
        }
    }
    
    private static void initFactory(final String factoryClass, final String jar) throws IOException {
        try {
            Class.forName(factoryClass, true, SlideShowFactory.class.getClassLoader());
        }
        catch (final ClassNotFoundException e) {
            throw new IOException(factoryClass + " not found - check if " + jar + " is on the classpath.");
        }
    }
    
    protected interface CreateSlideShow2<T, U>
    {
        SlideShow<?, ?> apply(final T p0, final U p1) throws IOException;
    }
    
    protected interface CreateSlideShow1<T>
    {
        SlideShow<?, ?> apply(final T p0) throws IOException;
    }
}
