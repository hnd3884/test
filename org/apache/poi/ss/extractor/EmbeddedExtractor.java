package org.apache.poi.ss.extractor;

import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.Ole10NativeException;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.hpsf.ClassIDPredefined;
import org.apache.poi.util.POILogFactory;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.Entry;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.ObjectData;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.ShapeContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Picture;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.poi.util.POILogger;

public class EmbeddedExtractor implements Iterable<EmbeddedExtractor>
{
    private static final POILogger LOG;
    private static final int MAX_RECORD_LENGTH = 1000000;
    private static final String CONTENT_TYPE_BYTES = "binary/octet-stream";
    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final String CONTENT_TYPE_DOC = "application/msword";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
    
    @Override
    public Iterator<EmbeddedExtractor> iterator() {
        final EmbeddedExtractor[] ee = { new Ole10Extractor(), new PdfExtractor(), new BiffExtractor(), new OOXMLExtractor(), new FsExtractor() };
        return Arrays.asList(ee).iterator();
    }
    
    public EmbeddedData extractOne(final DirectoryNode src) throws IOException {
        for (final EmbeddedExtractor ee : this) {
            if (ee.canExtract(src)) {
                return ee.extract(src);
            }
        }
        return null;
    }
    
    public EmbeddedData extractOne(final Picture src) throws IOException {
        for (final EmbeddedExtractor ee : this) {
            if (ee.canExtract(src)) {
                return ee.extract(src);
            }
        }
        return null;
    }
    
    public List<EmbeddedData> extractAll(final Sheet sheet) throws IOException {
        final Drawing<?> patriarch = sheet.getDrawingPatriarch();
        if (null == patriarch) {
            return Collections.emptyList();
        }
        final List<EmbeddedData> embeddings = new ArrayList<EmbeddedData>();
        this.extractAll(patriarch, embeddings);
        return embeddings;
    }
    
    protected void extractAll(final ShapeContainer<?> parent, final List<EmbeddedData> embeddings) throws IOException {
        for (final Shape shape : parent) {
            EmbeddedData data = null;
            if (shape instanceof ObjectData) {
                final ObjectData od = (ObjectData)shape;
                try {
                    if (od.hasDirectoryEntry()) {
                        data = this.extractOne((DirectoryNode)od.getDirectory());
                    }
                    else {
                        data = new EmbeddedData(od.getFileName(), od.getObjectData(), od.getContentType());
                    }
                }
                catch (final Exception e) {
                    EmbeddedExtractor.LOG.log(5, "Entry not found / readable - ignoring OLE embedding", e);
                }
            }
            else if (shape instanceof Picture) {
                data = this.extractOne((Picture)shape);
            }
            else if (shape instanceof ShapeContainer) {
                this.extractAll((ShapeContainer<?>)shape, embeddings);
            }
            if (data == null) {
                continue;
            }
            data.setShape(shape);
            String filename = data.getFilename();
            final String extension = (filename == null || filename.lastIndexOf(46) == -1) ? ".bin" : filename.substring(filename.lastIndexOf(46));
            if (filename == null || filename.isEmpty() || filename.startsWith("MBD") || filename.startsWith("Root Entry")) {
                filename = shape.getShapeName();
                if (filename != null) {
                    filename += extension;
                }
            }
            if (filename == null || filename.isEmpty()) {
                filename = "picture_" + embeddings.size() + extension;
            }
            filename = filename.trim();
            data.setFilename(filename);
            embeddings.add(data);
        }
    }
    
    public boolean canExtract(final DirectoryNode source) {
        return false;
    }
    
    public boolean canExtract(final Picture source) {
        return false;
    }
    
    protected EmbeddedData extract(final DirectoryNode dn) throws IOException {
        assert this.canExtract(dn);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(20000);
        try (final POIFSFileSystem dest = new POIFSFileSystem()) {
            copyNodes(dn, dest.getRoot());
            dest.writeFilesystem(bos);
        }
        return new EmbeddedData(dn.getName(), bos.toByteArray(), "binary/octet-stream");
    }
    
    protected EmbeddedData extract(final Picture source) throws IOException {
        return null;
    }
    
    protected static void copyNodes(final DirectoryNode src, final DirectoryNode dest) throws IOException {
        for (final Entry e : src) {
            if (e instanceof DirectoryNode) {
                final DirectoryNode srcDir = (DirectoryNode)e;
                final DirectoryNode destDir = (DirectoryNode)dest.createDirectory(srcDir.getName());
                destDir.setStorageClsid(srcDir.getStorageClsid());
                copyNodes(srcDir, destDir);
            }
            else {
                try (final InputStream is = src.createDocumentInputStream(e)) {
                    dest.createDocument(e.getName(), is);
                }
            }
        }
    }
    
    private static int indexOf(final byte[] data, final int offset, final byte[] pattern) {
        final int[] failure = computeFailure(pattern);
        int j = 0;
        if (data.length == 0) {
            return -1;
        }
        for (int i = offset; i < data.length; ++i) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                ++j;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }
    
    private static int[] computeFailure(final byte[] pattern) {
        final int[] failure = new int[pattern.length];
        int j = 0;
        for (int i = 1; i < pattern.length; ++i) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                ++j;
            }
            failure[i] = j;
        }
        return failure;
    }
    
    static {
        LOG = POILogFactory.getLogger(EmbeddedExtractor.class);
    }
    
    public static class Ole10Extractor extends EmbeddedExtractor
    {
        @Override
        public boolean canExtract(final DirectoryNode dn) {
            final ClassID clsId = dn.getStorageClsid();
            return ClassIDPredefined.lookup(clsId) == ClassIDPredefined.OLE_V1_PACKAGE;
        }
        
        public EmbeddedData extract(final DirectoryNode dn) throws IOException {
            try {
                final Ole10Native ole10 = Ole10Native.createFromEmbeddedOleObject(dn);
                return new EmbeddedData(ole10.getFileName(), ole10.getDataBuffer(), "binary/octet-stream");
            }
            catch (final Ole10NativeException e) {
                throw new IOException(e);
            }
        }
    }
    
    static class PdfExtractor extends EmbeddedExtractor
    {
        @Override
        public boolean canExtract(final DirectoryNode dn) {
            final ClassID clsId = dn.getStorageClsid();
            return ClassIDPredefined.PDF.equals(clsId) || dn.hasEntry("CONTENTS");
        }
        
        public EmbeddedData extract(final DirectoryNode dn) throws IOException {
            try (final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 final InputStream is = dn.createDocumentInputStream("CONTENTS")) {
                IOUtils.copy(is, bos);
                return new EmbeddedData(dn.getName() + ".pdf", bos.toByteArray(), "application/pdf");
            }
        }
        
        @Override
        public boolean canExtract(final Picture source) {
            final PictureData pd = source.getPictureData();
            return pd != null && pd.getPictureType() == 2;
        }
        
        @Override
        protected EmbeddedData extract(final Picture source) throws IOException {
            final PictureData pd = source.getPictureData();
            if (pd == null || pd.getPictureType() != 2) {
                return null;
            }
            final byte[] pictureBytes = pd.getData();
            final int idxStart = indexOf(pictureBytes, 0, "%PDF-".getBytes(LocaleUtil.CHARSET_1252));
            if (idxStart == -1) {
                return null;
            }
            final int idxEnd = indexOf(pictureBytes, idxStart, "%%EOF".getBytes(LocaleUtil.CHARSET_1252));
            if (idxEnd == -1) {
                return null;
            }
            final int pictureBytesLen = idxEnd - idxStart + 6;
            final byte[] pdfBytes = IOUtils.safelyAllocate(pictureBytesLen, 1000000);
            System.arraycopy(pictureBytes, idxStart, pdfBytes, 0, pictureBytesLen);
            String filename = source.getShapeName().trim();
            if (!StringUtil.endsWithIgnoreCase(filename, ".pdf")) {
                filename += ".pdf";
            }
            return new EmbeddedData(filename, pdfBytes, "application/pdf");
        }
    }
    
    static class OOXMLExtractor extends EmbeddedExtractor
    {
        @Override
        public boolean canExtract(final DirectoryNode dn) {
            return dn.hasEntry("package");
        }
        
        public EmbeddedData extract(final DirectoryNode dn) throws IOException {
            final ClassIDPredefined clsId = ClassIDPredefined.lookup(dn.getStorageClsid());
            String contentType = null;
            String ext = null;
            if (clsId != null) {
                contentType = clsId.getContentType();
                ext = clsId.getFileExtension();
            }
            if (contentType == null || ext == null) {
                contentType = "application/zip";
                ext = ".zip";
            }
            final DocumentInputStream dis = dn.createDocumentInputStream("package");
            final byte[] data = IOUtils.toByteArray(dis);
            dis.close();
            return new EmbeddedData(dn.getName() + ext, data, contentType);
        }
    }
    
    static class BiffExtractor extends EmbeddedExtractor
    {
        @Override
        public boolean canExtract(final DirectoryNode dn) {
            return this.canExtractExcel(dn) || this.canExtractWord(dn);
        }
        
        protected boolean canExtractExcel(final DirectoryNode dn) {
            final ClassIDPredefined clsId = ClassIDPredefined.lookup(dn.getStorageClsid());
            return ClassIDPredefined.EXCEL_V7 == clsId || ClassIDPredefined.EXCEL_V8 == clsId || dn.hasEntry("Workbook");
        }
        
        protected boolean canExtractWord(final DirectoryNode dn) {
            final ClassIDPredefined clsId = ClassIDPredefined.lookup(dn.getStorageClsid());
            return ClassIDPredefined.WORD_V7 == clsId || ClassIDPredefined.WORD_V8 == clsId || dn.hasEntry("WordDocument");
        }
        
        public EmbeddedData extract(final DirectoryNode dn) throws IOException {
            final EmbeddedData ed = super.extract(dn);
            if (this.canExtractExcel(dn)) {
                ed.setFilename(dn.getName() + ".xls");
                ed.setContentType("application/vnd.ms-excel");
            }
            else if (this.canExtractWord(dn)) {
                ed.setFilename(dn.getName() + ".doc");
                ed.setContentType("application/msword");
            }
            return ed;
        }
    }
    
    static class FsExtractor extends EmbeddedExtractor
    {
        @Override
        public boolean canExtract(final DirectoryNode dn) {
            return true;
        }
        
        public EmbeddedData extract(final DirectoryNode dn) throws IOException {
            final EmbeddedData ed = super.extract(dn);
            ed.setFilename(dn.getName() + ".ole");
            return ed;
        }
    }
}
