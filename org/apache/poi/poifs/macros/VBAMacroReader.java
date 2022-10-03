package org.apache.poi.poifs.macros;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.LittleEndian;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import org.apache.poi.util.HexDump;
import java.nio.charset.Charset;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.DocumentNode;
import java.io.ByteArrayInputStream;
import org.apache.poi.util.RLEDecompressingInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import org.apache.poi.util.StringUtil;
import java.util.zip.ZipInputStream;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.FileMagic;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.POILogger;
import java.io.Closeable;

public class VBAMacroReader implements Closeable
{
    private static final POILogger LOGGER;
    private static final int MAX_STRING_LENGTH = 20000;
    protected static final String VBA_PROJECT_OOXML = "vbaProject.bin";
    protected static final String VBA_PROJECT_POIFS = "VBA";
    private POIFSFileSystem fs;
    private static final int STREAMNAME_RESERVED = 50;
    private static final int PROJECT_CONSTANTS_RESERVED = 60;
    private static final int HELP_FILE_PATH_RESERVED = 61;
    private static final int REFERENCE_NAME_RESERVED = 62;
    private static final int DOC_STRING_RESERVED = 64;
    private static final int MODULE_DOCSTRING_RESERVED = 72;
    
    public VBAMacroReader(final InputStream rstream) throws IOException {
        final InputStream is = FileMagic.prepareToCheckMagic(rstream);
        final FileMagic fm = FileMagic.valueOf(is);
        if (fm == FileMagic.OLE2) {
            this.fs = new POIFSFileSystem(is);
        }
        else {
            this.openOOXML(is);
        }
    }
    
    public VBAMacroReader(final File file) throws IOException {
        try {
            this.fs = new POIFSFileSystem(file);
        }
        catch (final OfficeXmlFileException e) {
            this.openOOXML(new FileInputStream(file));
        }
    }
    
    public VBAMacroReader(final POIFSFileSystem fs) {
        this.fs = fs;
    }
    
    private void openOOXML(final InputStream zipFile) throws IOException {
        try (final ZipInputStream zis = new ZipInputStream(zipFile)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (StringUtil.endsWithIgnoreCase(zipEntry.getName(), "vbaProject.bin")) {
                    try {
                        this.fs = new POIFSFileSystem(zis);
                        return;
                    }
                    catch (final IOException e) {
                        zis.close();
                        throw e;
                    }
                    break;
                }
            }
        }
        throw new IllegalArgumentException("No VBA project found");
    }
    
    @Override
    public void close() throws IOException {
        this.fs.close();
        this.fs = null;
    }
    
    public Map<String, Module> readMacroModules() throws IOException {
        final ModuleMap modules = new ModuleMap();
        final Map<String, String> moduleNameMap = new LinkedHashMap<String, String>();
        this.findMacros(this.fs.getRoot(), modules);
        this.findModuleNameMap(this.fs.getRoot(), moduleNameMap, modules);
        this.findProjectProperties(this.fs.getRoot(), moduleNameMap, modules);
        final Map<String, Module> moduleSources = new HashMap<String, Module>();
        for (final Map.Entry<String, ModuleImpl> entry : modules.entrySet()) {
            final ModuleImpl module = entry.getValue();
            module.charset = modules.charset;
            moduleSources.put(entry.getKey(), module);
        }
        return moduleSources;
    }
    
    public Map<String, String> readMacros() throws IOException {
        final Map<String, Module> modules = this.readMacroModules();
        final Map<String, String> moduleSources = new HashMap<String, String>();
        for (final Map.Entry<String, Module> entry : modules.entrySet()) {
            moduleSources.put(entry.getKey(), entry.getValue().getContent());
        }
        return moduleSources;
    }
    
    protected void findMacros(final DirectoryNode dir, final ModuleMap modules) throws IOException {
        if ("VBA".equalsIgnoreCase(dir.getName())) {
            this.readMacros(dir, modules);
        }
        else {
            for (final Entry child : dir) {
                if (child instanceof DirectoryNode) {
                    this.findMacros((DirectoryNode)child, modules);
                }
            }
        }
    }
    
    private static void readModuleMetadataFromDirStream(final RLEDecompressingInputStream in, final String streamName, final ModuleMap modules) throws IOException {
        final int moduleOffset = in.readInt();
        ModuleImpl module = ((HashMap<K, ModuleImpl>)modules).get(streamName);
        if (module == null) {
            module = new ModuleImpl();
            module.offset = moduleOffset;
            modules.put(streamName, module);
        }
        else {
            final InputStream stream = new RLEDecompressingInputStream(new ByteArrayInputStream(module.buf, moduleOffset, module.buf.length - moduleOffset));
            module.read(stream);
            stream.close();
        }
    }
    
    private static void readModuleFromDocumentStream(final DocumentNode documentNode, final String name, final ModuleMap modules) throws IOException {
        ModuleImpl module = ((HashMap<K, ModuleImpl>)modules).get(name);
        if (module == null) {
            module = new ModuleImpl();
            modules.put(name, module);
            try (final InputStream dis = new DocumentInputStream(documentNode)) {
                module.read(dis);
            }
        }
        else if (module.buf == null) {
            if (module.offset == null) {
                throw new IOException("Module offset for '" + name + "' was never read.");
            }
            InputStream decompressed = null;
            InputStream compressed = new DocumentInputStream(documentNode);
            while (true) {
                try {
                    trySkip(compressed, module.offset);
                    decompressed = new RLEDecompressingInputStream(compressed);
                    module.read(decompressed);
                    return;
                }
                catch (final IllegalArgumentException | IllegalStateException ex) {
                    IOUtils.closeQuietly(compressed);
                    IOUtils.closeQuietly(decompressed);
                    final InputStream inputStream;
                    compressed = (inputStream = new DocumentInputStream(documentNode));
                    final byte[] decompressedBytes = findCompressedStreamWBruteForce(inputStream);
                    if (decompressedBytes == null) {
                        return;
                    }
                    module.read(new ByteArrayInputStream(decompressedBytes));
                }
                try {
                    final InputStream inputStream = compressed;
                    final byte[] decompressedBytes = findCompressedStreamWBruteForce(inputStream);
                    continue;
                }
                finally {}
                break;
            }
        }
    }
    
    private static void trySkip(final InputStream in, final long n) throws IOException {
        final long skippedBytes = IOUtils.skipFully(in, n);
        if (skippedBytes == n) {
            return;
        }
        if (skippedBytes < 0L) {
            throw new IOException("Tried skipping " + n + " bytes, but no bytes were skipped. The end of the stream has been reached or the stream is closed.");
        }
        throw new IOException("Tried skipping " + n + " bytes, but only " + skippedBytes + " bytes were skipped. This should never happen with a non-corrupt file.");
    }
    
    protected void readMacros(final DirectoryNode macroDir, final ModuleMap modules) throws IOException {
        for (final String entryName : macroDir.getEntryNames()) {
            if ("dir".equalsIgnoreCase(entryName)) {
                this.processDirStream(macroDir.getEntry(entryName), modules);
                break;
            }
        }
        for (final Entry entry : macroDir) {
            if (!(entry instanceof DocumentNode)) {
                continue;
            }
            final String name = entry.getName();
            final DocumentNode document = (DocumentNode)entry;
            if ("dir".equalsIgnoreCase(name) || StringUtil.startsWithIgnoreCase(name, "__SRP") || StringUtil.startsWithIgnoreCase(name, "_VBA_PROJECT")) {
                continue;
            }
            readModuleFromDocumentStream(document, name, modules);
        }
    }
    
    protected void findProjectProperties(final DirectoryNode node, final Map<String, String> moduleNameMap, final ModuleMap modules) throws IOException {
        for (final Entry entry : node) {
            if ("project".equalsIgnoreCase(entry.getName())) {
                final DocumentNode document = (DocumentNode)entry;
                try (final DocumentInputStream dis = new DocumentInputStream(document)) {
                    this.readProjectProperties(dis, moduleNameMap, modules);
                    return;
                }
            }
            if (entry instanceof DirectoryNode) {
                this.findProjectProperties((DirectoryNode)entry, moduleNameMap, modules);
            }
        }
    }
    
    protected void findModuleNameMap(final DirectoryNode node, final Map<String, String> moduleNameMap, final ModuleMap modules) throws IOException {
        for (final Entry entry : node) {
            if ("projectwm".equalsIgnoreCase(entry.getName())) {
                final DocumentNode document = (DocumentNode)entry;
                try (final DocumentInputStream dis = new DocumentInputStream(document)) {
                    this.readNameMapRecords(dis, moduleNameMap, modules.charset);
                    return;
                }
            }
            if (entry.isDirectoryEntry()) {
                this.findModuleNameMap((DirectoryNode)entry, moduleNameMap, modules);
            }
        }
    }
    
    private void processDirStream(final Entry dir, final ModuleMap modules) throws IOException {
        final DocumentNode dirDocumentNode = (DocumentNode)dir;
        DIR_STATE dirState = DIR_STATE.INFORMATION_RECORD;
        try (final DocumentInputStream dis = new DocumentInputStream(dirDocumentNode)) {
            String streamName = null;
            int recordId = 0;
            try (final RLEDecompressingInputStream in = new RLEDecompressingInputStream(dis)) {
                while (true) {
                    recordId = in.readShort();
                    if (recordId == -1) {
                        break;
                    }
                    final RecordType type = RecordType.lookup(recordId);
                    if (type.equals(RecordType.EOF)) {
                        break;
                    }
                    if (type.equals(RecordType.DIR_STREAM_TERMINATOR)) {
                        break;
                    }
                    switch (type) {
                        case PROJECT_VERSION: {
                            trySkip(in, RecordType.PROJECT_VERSION.getConstantLength());
                            continue;
                        }
                        case PROJECT_CODEPAGE: {
                            in.readInt();
                            final int codepage = in.readShort();
                            modules.charset = Charset.forName(CodePageUtil.codepageToEncoding(codepage, true));
                            continue;
                        }
                        case MODULE_STREAM_NAME: {
                            final ASCIIUnicodeStringPair pair = this.readStringPair(in, modules.charset, 50);
                            streamName = pair.getAscii();
                            continue;
                        }
                        case PROJECT_DOC_STRING: {
                            this.readStringPair(in, modules.charset, 64);
                            continue;
                        }
                        case PROJECT_HELP_FILE_PATH: {
                            this.readStringPair(in, modules.charset, 61);
                            continue;
                        }
                        case PROJECT_CONSTANTS: {
                            this.readStringPair(in, modules.charset, 60);
                            continue;
                        }
                        case REFERENCE_NAME: {
                            if (dirState.equals(DIR_STATE.INFORMATION_RECORD)) {
                                dirState = DIR_STATE.REFERENCES_RECORD;
                            }
                            final ASCIIUnicodeStringPair stringPair = this.readStringPair(in, modules.charset, 62, false);
                            if (stringPair.getPushbackRecordId() == -1) {
                                continue;
                            }
                            if (stringPair.getPushbackRecordId() != RecordType.REFERENCE_REGISTERED.id) {
                                throw new IllegalArgumentException("Unexpected reserved character. Expected " + Integer.toHexString(62) + " or " + Integer.toHexString(RecordType.REFERENCE_REGISTERED.id) + " not: " + Integer.toHexString(stringPair.getPushbackRecordId()));
                            }
                        }
                        case REFERENCE_REGISTERED: {
                            final int recLength = in.readInt();
                            trySkip(in, recLength);
                            continue;
                        }
                        case MODULE_DOC_STRING: {
                            final int modDocStringLength = in.readInt();
                            readString(in, modDocStringLength, modules.charset);
                            final int modDocStringReserved = in.readShort();
                            if (modDocStringReserved != 72) {
                                throw new IOException("Expected x003C after stream name before Unicode stream name, but found: " + Integer.toHexString(modDocStringReserved));
                            }
                            final int unicodeModDocStringLength = in.readInt();
                            this.readUnicodeString(in, unicodeModDocStringLength);
                            continue;
                        }
                        case MODULE_OFFSET: {
                            final int modOffsetSz = in.readInt();
                            readModuleMetadataFromDirStream(in, streamName, modules);
                            continue;
                        }
                        case PROJECT_MODULES: {
                            dirState = DIR_STATE.MODULES_RECORD;
                            in.readInt();
                            in.readShort();
                            continue;
                        }
                        case REFERENCE_CONTROL_A: {
                            final int szTwiddled = in.readInt();
                            trySkip(in, szTwiddled);
                            int nextRecord = in.readShort();
                            if (nextRecord == RecordType.REFERENCE_NAME.id) {
                                this.readStringPair(in, modules.charset, 62);
                                nextRecord = in.readShort();
                            }
                            if (nextRecord != 48) {
                                throw new IOException("Expected 0x30 as Reserved3 in a ReferenceControl record");
                            }
                            final int szExtended = in.readInt();
                            trySkip(in, szExtended);
                            continue;
                        }
                        case MODULE_TERMINATOR: {
                            final int endOfModulesReserved = in.readInt();
                            continue;
                        }
                        default: {
                            if (type.getConstantLength() > -1) {
                                trySkip(in, type.getConstantLength());
                                continue;
                            }
                            final int recordLength = in.readInt();
                            trySkip(in, recordLength);
                            continue;
                        }
                    }
                }
            }
            catch (final IOException e) {
                throw new IOException("Error occurred while reading macros at section id " + recordId + " (" + HexDump.shortToHex(recordId) + ")", e);
            }
        }
    }
    
    private ASCIIUnicodeStringPair readStringPair(final RLEDecompressingInputStream in, final Charset charset, final int reservedByte) throws IOException {
        return this.readStringPair(in, charset, reservedByte, true);
    }
    
    private ASCIIUnicodeStringPair readStringPair(final RLEDecompressingInputStream in, final Charset charset, final int reservedByte, final boolean throwOnUnexpectedReservedByte) throws IOException {
        final int nameLength = in.readInt();
        final String ascii = readString(in, nameLength, charset);
        final int reserved = in.readShort();
        if (reserved == reservedByte) {
            final int unicodeNameRecordLength = in.readInt();
            final String unicode = this.readUnicodeString(in, unicodeNameRecordLength);
            return new ASCIIUnicodeStringPair(ascii, unicode);
        }
        if (throwOnUnexpectedReservedByte) {
            throw new IOException("Expected " + Integer.toHexString(reservedByte) + "after name before Unicode name, but found: " + Integer.toHexString(reserved));
        }
        return new ASCIIUnicodeStringPair(ascii, reserved);
    }
    
    protected void readNameMapRecords(final InputStream is, final Map<String, String> moduleNames, final Charset charset) throws IOException {
        String mbcs = null;
        String unicode = null;
        final int maxNameRecords = 10000;
        int records = 0;
        while (++records < 10000) {
            try {
                int b = IOUtils.readByte(is);
                if (b == 0) {
                    b = IOUtils.readByte(is);
                    if (b == 0) {
                        return;
                    }
                }
                mbcs = readMBCS(b, is, charset, 20000);
            }
            catch (final EOFException e) {
                return;
            }
            try {
                unicode = readUnicode(is, 20000);
            }
            catch (final EOFException e) {
                return;
            }
            if (mbcs.trim().length() > 0 && unicode.trim().length() > 0) {
                moduleNames.put(mbcs, unicode);
            }
        }
        if (records >= 10000) {
            VBAMacroReader.LOGGER.log(5, "Hit max name records to read (10000). Stopped early.");
        }
    }
    
    private static String readUnicode(final InputStream is, final int maxLength) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b0;
        int b2;
        int read;
        for (b0 = IOUtils.readByte(is), b2 = IOUtils.readByte(is), read = 2; b0 + b2 != 0 && read < maxLength; b0 = IOUtils.readByte(is), b2 = IOUtils.readByte(is), read += 2) {
            bos.write(b0);
            bos.write(b2);
        }
        if (read >= maxLength) {
            VBAMacroReader.LOGGER.log(5, "stopped reading unicode name after " + read + " bytes");
        }
        return new String(bos.toByteArray(), StandardCharsets.UTF_16LE);
    }
    
    private static String readMBCS(final int firstByte, final InputStream is, final Charset charset, final int maxLength) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (int len = 0, b = firstByte; b > 0 && len < maxLength; b = IOUtils.readByte(is)) {
            ++len;
            bos.write(b);
        }
        return new String(bos.toByteArray(), charset);
    }
    
    private static String readString(final InputStream stream, final int length, final Charset charset) throws IOException {
        final byte[] buffer = IOUtils.safelyAllocate(length, 20000);
        final int bytesRead = IOUtils.readFully(stream, buffer);
        if (bytesRead != length) {
            throw new IOException("Tried to read: " + length + ", but could only read: " + bytesRead);
        }
        return new String(buffer, 0, length, charset);
    }
    
    protected void readProjectProperties(final DocumentInputStream dis, final Map<String, String> moduleNameMap, final ModuleMap modules) throws IOException {
        final InputStreamReader reader = new InputStreamReader(dis, modules.charset);
        final StringBuilder builder = new StringBuilder();
        final char[] buffer = new char[512];
        int read;
        while ((read = reader.read(buffer)) >= 0) {
            builder.append(buffer, 0, read);
        }
        final String properties = builder.toString();
        for (final String line : properties.split("\r\n|\n\r")) {
            if (!line.startsWith("[")) {
                final String[] tokens = line.split("=");
                if (tokens.length > 1 && tokens[1].length() > 1 && tokens[1].startsWith("\"") && tokens[1].endsWith("\"")) {
                    tokens[1] = tokens[1].substring(1, tokens[1].length() - 1);
                }
                if ("Document".equals(tokens[0]) && tokens.length > 1) {
                    final String mn = tokens[1].substring(0, tokens[1].indexOf("/&H"));
                    final ModuleImpl module = this.getModule(mn, moduleNameMap, modules);
                    if (module != null) {
                        module.moduleType = Module.ModuleType.Document;
                    }
                    else {
                        VBAMacroReader.LOGGER.log(5, "couldn't find module with name: " + mn);
                    }
                }
                else if ("Module".equals(tokens[0]) && tokens.length > 1) {
                    final ModuleImpl module2 = this.getModule(tokens[1], moduleNameMap, modules);
                    if (module2 != null) {
                        module2.moduleType = Module.ModuleType.Module;
                    }
                    else {
                        VBAMacroReader.LOGGER.log(5, "couldn't find module with name: " + tokens[1]);
                    }
                }
                else if ("Class".equals(tokens[0]) && tokens.length > 1) {
                    final ModuleImpl module2 = this.getModule(tokens[1], moduleNameMap, modules);
                    if (module2 != null) {
                        module2.moduleType = Module.ModuleType.Class;
                    }
                    else {
                        VBAMacroReader.LOGGER.log(5, "couldn't find module with name: " + tokens[1]);
                    }
                }
            }
        }
    }
    
    private ModuleImpl getModule(final String moduleName, final Map<String, String> moduleNameMap, final ModuleMap moduleMap) {
        if (moduleNameMap.containsKey(moduleName)) {
            return ((HashMap<K, ModuleImpl>)moduleMap).get(moduleNameMap.get(moduleName));
        }
        return ((HashMap<K, ModuleImpl>)moduleMap).get(moduleName);
    }
    
    private String readUnicodeString(final RLEDecompressingInputStream in, final int unicodeNameRecordLength) throws IOException {
        final byte[] buffer = IOUtils.safelyAllocate(unicodeNameRecordLength, 20000);
        final int bytesRead = IOUtils.readFully(in, buffer);
        if (bytesRead != unicodeNameRecordLength) {
            throw new EOFException();
        }
        return new String(buffer, StringUtil.UTF16LE);
    }
    
    private static byte[] findCompressedStreamWBruteForce(final InputStream is) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        final byte[] compressed = bos.toByteArray();
        byte[] decompressed = null;
        for (int i = 0; i < compressed.length; ++i) {
            if (compressed[i] == 1 && i < compressed.length - 1) {
                final int w = LittleEndian.getUShort(compressed, i + 1);
                if (w > 0) {
                    if ((w & 0x7000) == 0x3000) {
                        decompressed = tryToDecompress(new ByteArrayInputStream(compressed, i, compressed.length - i));
                        if (decompressed != null && decompressed.length > 9) {
                            final int firstX = Math.min(20, decompressed.length);
                            final String start = new String(decompressed, 0, firstX, StringUtil.WIN_1252);
                            if (start.contains("Attribute")) {
                                return decompressed;
                            }
                        }
                    }
                }
            }
        }
        return decompressed;
    }
    
    private static byte[] tryToDecompress(final InputStream is) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(new RLEDecompressingInputStream(is), bos);
        }
        catch (final IllegalArgumentException | IOException | IllegalStateException e) {
            return null;
        }
        return bos.toByteArray();
    }
    
    static {
        LOGGER = POILogFactory.getLogger(VBAMacroReader.class);
    }
    
    protected static class ModuleImpl implements Module
    {
        Integer offset;
        byte[] buf;
        ModuleType moduleType;
        Charset charset;
        
        void read(final InputStream in) throws IOException {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            out.close();
            this.buf = out.toByteArray();
        }
        
        @Override
        public String getContent() {
            return new String(this.buf, this.charset);
        }
        
        @Override
        public ModuleType geModuleType() {
            return this.moduleType;
        }
    }
    
    protected static class ModuleMap extends HashMap<String, ModuleImpl>
    {
        Charset charset;
        
        protected ModuleMap() {
            this.charset = StringUtil.WIN_1252;
        }
    }
    
    private enum RecordType
    {
        MODULE_OFFSET(49), 
        PROJECT_SYS_KIND(1), 
        PROJECT_LCID(2), 
        PROJECT_LCID_INVOKE(20), 
        PROJECT_CODEPAGE(3), 
        PROJECT_NAME(4), 
        PROJECT_DOC_STRING(5), 
        PROJECT_HELP_FILE_PATH(6), 
        PROJECT_HELP_CONTEXT(7, 8), 
        PROJECT_LIB_FLAGS(8), 
        PROJECT_VERSION(9, 10), 
        PROJECT_CONSTANTS(12), 
        PROJECT_MODULES(15), 
        DIR_STREAM_TERMINATOR(16), 
        PROJECT_COOKIE(19), 
        MODULE_NAME(25), 
        MODULE_NAME_UNICODE(71), 
        MODULE_STREAM_NAME(26), 
        MODULE_DOC_STRING(28), 
        MODULE_HELP_CONTEXT(30), 
        MODULE_COOKIE(44), 
        MODULE_TYPE_PROCEDURAL(33, 4), 
        MODULE_TYPE_OTHER(34, 4), 
        MODULE_PRIVATE(40, 4), 
        REFERENCE_NAME(22), 
        REFERENCE_REGISTERED(13), 
        REFERENCE_PROJECT(14), 
        REFERENCE_CONTROL_A(47), 
        REFERENCE_CONTROL_B(51), 
        MODULE_TERMINATOR(43), 
        EOF(-1), 
        UNKNOWN(-2);
        
        private final int VARIABLE_LENGTH = -1;
        private final int id;
        private final int constantLength;
        
        private RecordType(final int id) {
            this.id = id;
            this.constantLength = -1;
        }
        
        private RecordType(final int id, final int constantLength) {
            this.id = id;
            this.constantLength = constantLength;
        }
        
        int getConstantLength() {
            return this.constantLength;
        }
        
        static RecordType lookup(final int id) {
            for (final RecordType type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return RecordType.UNKNOWN;
        }
    }
    
    private enum DIR_STATE
    {
        INFORMATION_RECORD, 
        REFERENCES_RECORD, 
        MODULES_RECORD;
    }
    
    private static class ASCIIUnicodeStringPair
    {
        private final String ascii;
        private final String unicode;
        private final int pushbackRecordId;
        
        ASCIIUnicodeStringPair(final String ascii, final int pushbackRecordId) {
            this.ascii = ascii;
            this.unicode = "";
            this.pushbackRecordId = pushbackRecordId;
        }
        
        ASCIIUnicodeStringPair(final String ascii, final String unicode) {
            this.ascii = ascii;
            this.unicode = unicode;
            this.pushbackRecordId = -1;
        }
        
        private String getAscii() {
            return this.ascii;
        }
        
        private String getUnicode() {
            return this.unicode;
        }
        
        private int getPushbackRecordId() {
            return this.pushbackRecordId;
        }
    }
}
