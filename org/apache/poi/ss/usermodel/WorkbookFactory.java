package org.apache.poi.ss.usermodel;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import java.io.FileNotFoundException;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.util.Removal;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;

public abstract class WorkbookFactory
{
    protected static CreateWorkbook0 createHssfFromScratch;
    protected static CreateWorkbook1<DirectoryNode> createHssfByNode;
    protected static CreateWorkbook0 createXssfFromScratch;
    protected static CreateWorkbook1<InputStream> createXssfByStream;
    protected static CreateWorkbook1<Object> createXssfByPackage;
    protected static CreateWorkbook2<File, Boolean> createXssfByFile;
    
    public static Workbook create(final boolean xssf) throws IOException {
        if (xssf) {
            initXssf();
            return WorkbookFactory.createXssfFromScratch.apply();
        }
        initHssf();
        return WorkbookFactory.createHssfFromScratch.apply();
    }
    
    public static Workbook create(final POIFSFileSystem fs) throws IOException {
        return create(fs, null);
    }
    
    private static Workbook create(final POIFSFileSystem fs, final String password) throws IOException {
        return create(fs.getRoot(), password);
    }
    
    public static Workbook create(final DirectoryNode root) throws IOException {
        return create(root, null);
    }
    
    public static Workbook create(final DirectoryNode root, final String password) throws IOException {
        if (root.hasEntry("EncryptedPackage")) {
            InputStream stream = null;
            try {
                stream = DocumentFactoryHelper.getDecryptedStream(root, password);
                initXssf();
                return WorkbookFactory.createXssfByStream.apply(stream);
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
            initHssf();
            return WorkbookFactory.createHssfByNode.apply(root);
        }
        finally {
            if (passwordSet) {
                Biff8EncryptionKey.setCurrentUserPassword(null);
            }
        }
    }
    
    @Deprecated
    @Removal(version = "4.2.0")
    public static Workbook create(final Object pkg) throws IOException {
        initXssf();
        return WorkbookFactory.createXssfByPackage.apply(pkg);
    }
    
    public static Workbook create(final InputStream inp) throws IOException, EncryptedDocumentException {
        return create(inp, null);
    }
    
    public static Workbook create(final InputStream inp, final String password) throws IOException, EncryptedDocumentException {
        final InputStream is = FileMagic.prepareToCheckMagic(inp);
        final FileMagic fm = FileMagic.valueOf(is);
        switch (fm) {
            case OLE2: {
                final POIFSFileSystem fs = new POIFSFileSystem(is);
                return create(fs, password);
            }
            case OOXML: {
                initXssf();
                return WorkbookFactory.createXssfByStream.apply(is);
            }
            default: {
                throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
            }
        }
    }
    
    public static Workbook create(final File file) throws IOException, EncryptedDocumentException {
        return create(file, null);
    }
    
    public static Workbook create(final File file, final String password) throws IOException, EncryptedDocumentException {
        return create(file, password, false);
    }
    
    public static Workbook create(final File file, final String password, final boolean readOnly) throws IOException, EncryptedDocumentException {
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
            initXssf();
            return WorkbookFactory.createXssfByFile.apply(file, readOnly);
        }
        catch (final RuntimeException e2) {
            IOUtils.closeQuietly(fs);
            throw e2;
        }
    }
    
    private static void initXssf() throws IOException {
        if (WorkbookFactory.createXssfFromScratch == null) {
            initFactory("org.apache.poi.xssf.usermodel.XSSFWorkbookFactory", "poi-ooxml-*.jar");
        }
    }
    
    private static void initHssf() throws IOException {
        if (WorkbookFactory.createHssfFromScratch == null) {
            initFactory("org.apache.poi.hssf.usermodel.HSSFWorkbookFactory", "poi-*.jar");
        }
    }
    
    private static void initFactory(final String factoryClass, final String jar) throws IOException {
        try {
            Class.forName(factoryClass, true, WorkbookFactory.class.getClassLoader());
        }
        catch (final ClassNotFoundException e) {
            throw new IOException(factoryClass + " not found - check if " + jar + " is on the classpath.");
        }
    }
    
    protected interface CreateWorkbook2<T, U>
    {
        Workbook apply(final T p0, final U p1) throws IOException;
    }
    
    protected interface CreateWorkbook1<T>
    {
        Workbook apply(final T p0) throws IOException;
    }
    
    protected interface CreateWorkbook0
    {
        Workbook apply() throws IOException;
    }
}
