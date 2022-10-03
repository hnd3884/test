package eu.medsea.mimeutil.detector;

import java.io.BufferedInputStream;
import eu.medsea.mimeutil.MimeUtil;
import java.io.FileInputStream;
import eu.medsea.mimeutil.MimeType;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashSet;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimerTask;
import eu.medsea.mimeutil.MimeException;
import java.util.Date;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.Timer;
import java.nio.ByteBuffer;
import eu.medsea.mimeutil.MimeUtil2;

public class OpendesktopMimeDetector extends MimeDetector
{
    private static final MimeUtil2.MimeLogger log;
    private static String mimeCacheFile;
    private static String internalMimeCacheFile;
    private ByteBuffer content;
    private Timer timer;
    
    public OpendesktopMimeDetector(final String mimeCacheFile) {
        this.init(mimeCacheFile);
    }
    
    public OpendesktopMimeDetector() {
        this.init(OpendesktopMimeDetector.mimeCacheFile);
    }
    
    private void init(final String mimeCacheFile) {
        String cacheFile = mimeCacheFile;
        if (!new File(cacheFile).exists()) {
            cacheFile = OpendesktopMimeDetector.internalMimeCacheFile;
        }
        FileChannel rCh = null;
        try {
            RandomAccessFile raf = null;
            raf = new RandomAccessFile(cacheFile, "r");
            rCh = raf.getChannel();
            this.content = rCh.map(FileChannel.MapMode.READ_ONLY, 0L, rCh.size());
            this.initMimeTypes();
            if (OpendesktopMimeDetector.log.isDebugEnabled()) {
                OpendesktopMimeDetector.log.debug("Registering a FileWatcher for [" + cacheFile + "]");
            }
            final TimerTask task = new FileWatcher(new File(cacheFile)) {
                @Override
                protected void onChange(final File file) {
                    OpendesktopMimeDetector.this.initMimeTypes();
                }
            };
            (this.timer = new Timer()).schedule(task, new Date(), 10000L);
        }
        catch (final Exception e) {
            throw new MimeException(e);
        }
        finally {
            if (rCh != null) {
                try {
                    rCh.close();
                }
                catch (final Exception e2) {
                    OpendesktopMimeDetector.log.error(e2.getLocalizedMessage(), e2);
                }
            }
        }
    }
    
    @Override
    public void delete() {
        this.timer.cancel();
    }
    
    @Override
    public String getDescription() {
        return "Resolve mime types for files and streams using the Opendesktop shared mime.cache file. Version [" + this.getMajorVersion() + "." + this.getMinorVersion() + "].";
    }
    
    public Collection getMimeTypesFileName(final String fileName) {
        Collection mimeTypes = new ArrayList();
        this.lookupMimeTypesForGlobFileName(fileName, mimeTypes);
        if (!mimeTypes.isEmpty()) {
            mimeTypes = this.normalizeWeightedMimeList(mimeTypes);
        }
        return mimeTypes;
    }
    
    public Collection getMimeTypesURL(final URL url) {
        final Collection mimeTypes = this.getMimeTypesFileName(url.getPath());
        return this._getMimeTypes(mimeTypes, this.getInputStream(url));
    }
    
    public Collection getMimeTypesFile(final File file) throws UnsupportedOperationException {
        final Collection mimeTypes = this.getMimeTypesFileName(file.getName());
        if (!file.exists()) {
            return mimeTypes;
        }
        return this._getMimeTypes(mimeTypes, this.getInputStream(file));
    }
    
    public Collection getMimeTypesInputStream(final InputStream in) throws UnsupportedOperationException {
        return this.lookupMimeTypesForMagicData(in);
    }
    
    public Collection getMimeTypesByteArray(final byte[] data) throws UnsupportedOperationException {
        return this.lookupMagicData(data);
    }
    
    public String dump() {
        return "{MAJOR_VERSION=" + this.getMajorVersion() + " MINOR_VERSION=" + this.getMinorVersion() + " ALIAS_LIST_OFFSET=" + this.getAliasListOffset() + " PARENT_LIST_OFFSET=" + this.getParentListOffset() + " LITERAL_LIST_OFFSET=" + this.getLiteralListOffset() + " REVERSE_SUFFIX_TREE_OFFSET=" + this.getReverseSuffixTreeOffset() + " GLOB_LIST_OFFSET=" + this.getGlobListOffset() + " MAGIC_LIST_OFFSET=" + this.getMagicListOffset() + " NAMESPACE_LIST_OFFSET=" + this.getNameSpaceListOffset() + " ICONS_LIST_OFFSET=" + this.getIconListOffset() + " GENERIC_ICONS_LIST_OFFSET=" + this.getGenericIconListOffset() + "}";
    }
    
    private Collection lookupMimeTypesForMagicData(final InputStream in) {
        int offset = 0;
        final int len = this.getMaxExtents();
        final byte[] data = new byte[len];
        in.mark(len);
        try {
            int bytesRead;
            for (int restBytesToRead = len; restBytesToRead > 0; restBytesToRead -= bytesRead) {
                bytesRead = in.read(data, offset, restBytesToRead);
                if (bytesRead < 0) {
                    break;
                }
                offset += bytesRead;
            }
        }
        catch (final IOException ioe) {
            throw new MimeException(ioe);
        }
        finally {
            try {
                in.reset();
            }
            catch (final Exception e) {
                throw new MimeException(e);
            }
        }
        return this.lookupMagicData(data);
    }
    
    private Collection lookupMagicData(final byte[] data) {
        final Collection mimeTypes = new ArrayList();
        final int listOffset = this.getMagicListOffset();
        final int numEntries = this.content.getInt(listOffset);
        final int offset = this.content.getInt(listOffset + 8);
        for (int i = 0; i < numEntries; ++i) {
            final String mimeType = this.compareToMagicData(offset + 16 * i, data);
            if (mimeType != null) {
                mimeTypes.add(mimeType);
            }
            else {
                final String nonMatch = this.getMimeType(this.content.getInt(offset + 16 * i + 4));
                mimeTypes.remove(nonMatch);
            }
        }
        return mimeTypes;
    }
    
    private String compareToMagicData(final int offset, final byte[] data) {
        final int mimeOffset = this.content.getInt(offset + 4);
        final int numMatches = this.content.getInt(offset + 8);
        final int matchletOffset = this.content.getInt(offset + 12);
        for (int i = 0; i < numMatches; ++i) {
            if (this.matchletMagicCompare(matchletOffset + i * 32, data)) {
                return this.getMimeType(mimeOffset);
            }
        }
        return null;
    }
    
    private boolean matchletMagicCompare(final int offset, final byte[] data) {
        final int rangeStart = this.content.getInt(offset);
        final int rangeLength = this.content.getInt(offset + 4);
        final int dataLength = this.content.getInt(offset + 12);
        final int dataOffset = this.content.getInt(offset + 16);
        final int maskOffset = this.content.getInt(offset + 20);
        for (int i = rangeStart; i <= rangeStart + rangeLength; ++i) {
            boolean validMatch = true;
            if (i + dataLength > data.length) {
                return false;
            }
            if (maskOffset != 0) {
                for (int j = 0; j < dataLength; ++j) {
                    if ((this.content.get(dataOffset + j) & this.content.get(maskOffset + j)) != (data[j + i] & this.content.get(maskOffset + j))) {
                        validMatch = false;
                        break;
                    }
                }
            }
            else {
                for (int j = 0; j < dataLength; ++j) {
                    if (this.content.get(dataOffset + j) != data[j + i]) {
                        validMatch = false;
                        break;
                    }
                }
            }
            if (validMatch) {
                return true;
            }
        }
        return false;
    }
    
    private void lookupGlobLiteral(final String fileName, final Collection mimeTypes) {
        final int listOffset = this.getLiteralListOffset();
        final int numEntries = this.content.getInt(listOffset);
        int min = 0;
        int max = numEntries - 1;
        while (max >= min) {
            final int mid = (min + max) / 2;
            final String literal = this.getString(this.content.getInt(listOffset + 4 + 12 * mid));
            final int cmp = literal.compareTo(fileName);
            if (cmp < 0) {
                min = mid + 1;
            }
            else {
                if (cmp <= 0) {
                    final String mimeType = this.getMimeType(this.content.getInt(listOffset + 4 + 12 * mid + 4));
                    final int weight = this.content.getInt(listOffset + 4 + 12 * mid + 8);
                    mimeTypes.add(new WeightedMimeType(mimeType, literal, weight));
                    return;
                }
                max = mid - 1;
            }
        }
    }
    
    private void lookupGlobFileNameMatch(final String fileName, final Collection mimeTypes) {
        final int listOffset = this.getGlobListOffset();
        for (int numEntries = this.content.getInt(listOffset), i = 0; i < numEntries; ++i) {
            final int offset = this.content.getInt(listOffset + 4 + 12 * i);
            final int mimeTypeOffset = this.content.getInt(listOffset + 4 + 12 * i + 4);
            final int weight = this.content.getInt(listOffset + 4 + 12 * i + 8);
            final String pattern = this.getString(offset, true);
            final String mimeType = this.getMimeType(mimeTypeOffset);
            if (fileName.matches(pattern)) {
                mimeTypes.add(new WeightedMimeType(mimeType, pattern, weight));
            }
        }
    }
    
    private Collection normalizeWeightedMimeList(final Collection weightedMimeTypes) {
        final Collection mimeTypes = new LinkedHashSet();
        Collections.sort((List<Object>)weightedMimeTypes, new Comparator() {
            @Override
            public int compare(final Object obj1, final Object obj2) {
                return ((WeightedMimeType)obj1).weight - ((WeightedMimeType)obj2).weight;
            }
        });
        int weight = 0;
        int patternLen = 0;
        for (final WeightedMimeType mw : weightedMimeTypes) {
            if (weight < mw.weight) {
                weight = mw.weight;
            }
            if (weight >= mw.weight) {
                if (mw.pattern.length() > patternLen) {
                    patternLen = mw.pattern.length();
                }
                mimeTypes.add(mw);
            }
        }
        for (final WeightedMimeType mw : weightedMimeTypes) {
            if (mw.pattern.length() < patternLen) {
                mimeTypes.remove(mw);
            }
        }
        final Collection _mimeTypes = new HashSet();
        final Iterator it2 = mimeTypes.iterator();
        while (it2.hasNext()) {
            _mimeTypes.add(it2.next().toString());
        }
        return _mimeTypes;
    }
    
    private void lookupMimeTypesForGlobFileName(final String fileName, final Collection mimeTypes) {
        if (fileName == null) {
            return;
        }
        this.lookupGlobLiteral(fileName, mimeTypes);
        if (!mimeTypes.isEmpty()) {
            return;
        }
        final int len = fileName.length();
        this.lookupGlobSuffix(fileName, false, len, mimeTypes);
        if (mimeTypes.isEmpty()) {
            this.lookupGlobSuffix(fileName, true, len, mimeTypes);
        }
        if (mimeTypes.isEmpty()) {
            this.lookupGlobFileNameMatch(fileName, mimeTypes);
        }
    }
    
    private void lookupGlobSuffix(final String fileName, final boolean ignoreCase, final int len, final Collection mimeTypes) {
        final int listOffset = this.getReverseSuffixTreeOffset();
        final int numEntries = this.content.getInt(listOffset);
        final int offset = this.content.getInt(listOffset + 4);
        this.lookupGlobNodeSuffix(fileName, numEntries, offset, ignoreCase, len, mimeTypes, new StringBuffer());
    }
    
    private void lookupGlobNodeSuffix(final String fileName, final int numEntries, final int offset, final boolean ignoreCase, int len, final Collection mimeTypes, final StringBuffer pattern) {
        final char character = ignoreCase ? fileName.toLowerCase().charAt(len - 1) : fileName.charAt(len - 1);
        if (character == '\0') {
            return;
        }
        int min = 0;
        int max = numEntries - 1;
        while (max >= min && len >= 0) {
            final int mid = (min + max) / 2;
            char matchChar = (char)this.content.getInt(offset + 12 * mid);
            if (matchChar < character) {
                min = mid + 1;
            }
            else {
                if (matchChar <= character) {
                    --len;
                    final int numChildren = this.content.getInt(offset + 12 * mid + 4);
                    final int childOffset = this.content.getInt(offset + 12 * mid + 8);
                    if (len > 0) {
                        pattern.append(matchChar);
                        this.lookupGlobNodeSuffix(fileName, numChildren, childOffset, ignoreCase, len, mimeTypes, pattern);
                    }
                    if (mimeTypes.isEmpty()) {
                        for (int i = 0; i < numChildren; ++i) {
                            matchChar = (char)this.content.getInt(childOffset + 12 * i);
                            if (matchChar != '\0') {
                                break;
                            }
                            final int mimeOffset = this.content.getInt(childOffset + 12 * i + 4);
                            final int weight = this.content.getInt(childOffset + 12 * i + 8);
                            mimeTypes.add(new WeightedMimeType(this.getMimeType(mimeOffset), pattern.toString(), weight));
                        }
                    }
                    return;
                }
                max = mid - 1;
            }
        }
    }
    
    private int getMaxExtents() {
        return this.content.getInt(this.getMagicListOffset() + 4);
    }
    
    private String aliasLookup(final String alias) {
        final int aliasListOffset = this.getAliasListOffset();
        int min = 0;
        int max = this.content.getInt(aliasListOffset) - 1;
        while (max >= min) {
            final int mid = (min + max) / 2;
            final int aliasOffset = this.content.getInt(aliasListOffset + 4 + mid * 8);
            final int mimeOffset = this.content.getInt(aliasListOffset + 4 + mid * 8 + 4);
            final int cmp = this.getMimeType(aliasOffset).compareTo(alias);
            if (cmp < 0) {
                min = mid + 1;
            }
            else {
                if (cmp <= 0) {
                    return this.getMimeType(mimeOffset);
                }
                max = mid - 1;
            }
        }
        return null;
    }
    
    private String unaliasMimeType(final String mimeType) {
        final String lookup = this.aliasLookup(mimeType);
        return (lookup == null) ? mimeType : lookup;
    }
    
    private boolean isMimeTypeSubclass(final String mimeType, final String subClass) {
        final String umimeType = this.unaliasMimeType(mimeType);
        final String usubClass = this.unaliasMimeType(subClass);
        final MimeType _mimeType = new MimeType(umimeType);
        final MimeType _subClass = new MimeType(usubClass);
        if (umimeType.compareTo(usubClass) == 0) {
            return true;
        }
        if (this.isSuperType(usubClass) && _mimeType.getMediaType().equals(_subClass.getMediaType())) {
            return true;
        }
        if (usubClass.equals("text/plain") && _mimeType.getMediaType().equals("text")) {
            return true;
        }
        if (usubClass.equals("application/octet-stream")) {
            return true;
        }
        final int parentListOffset = this.getParentListOffset();
        final int numParents = this.content.getInt(parentListOffset);
        int min = 0;
        int max = numParents - 1;
        while (max >= min) {
            final int med = (min + max) / 2;
            int offset = this.content.getInt(parentListOffset + 4 + 8 * med);
            final String parentMime = this.getMimeType(offset);
            final int cmp = parentMime.compareTo(umimeType);
            if (cmp < 0) {
                min = med + 1;
            }
            else {
                if (cmp <= 0) {
                    offset = this.content.getInt(parentListOffset + 4 + 8 * med + 4);
                    for (int _numParents = this.content.getInt(offset), i = 0; i < _numParents; ++i) {
                        final int parentOffset = this.content.getInt(offset + 4 + 4 * i);
                        if (this.isMimeTypeSubclass(this.getMimeType(parentOffset), usubClass)) {
                            return true;
                        }
                    }
                    break;
                }
                max = med - 1;
            }
        }
        return false;
    }
    
    private boolean isSuperType(final String mimeType) {
        final String type = mimeType.substring(mimeType.length() - 2);
        return type.equals("/*");
    }
    
    private int getGenericIconListOffset() {
        return this.content.getInt(36);
    }
    
    private int getIconListOffset() {
        return this.content.getInt(32);
    }
    
    private int getNameSpaceListOffset() {
        return this.content.getInt(28);
    }
    
    private int getMagicListOffset() {
        return this.content.getInt(24);
    }
    
    private int getGlobListOffset() {
        return this.content.getInt(20);
    }
    
    private int getReverseSuffixTreeOffset() {
        return this.content.getInt(16);
    }
    
    private int getLiteralListOffset() {
        return this.content.getInt(12);
    }
    
    private int getParentListOffset() {
        return this.content.getInt(8);
    }
    
    private int getAliasListOffset() {
        return this.content.getInt(4);
    }
    
    private short getMinorVersion() {
        return this.content.getShort(2);
    }
    
    private short getMajorVersion() {
        return this.content.getShort(0);
    }
    
    private String getMimeType(final int offset) {
        return this.getString(offset);
    }
    
    private String getString(final int offset) {
        return this.getString(offset, false);
    }
    
    private String getString(final int offset, final boolean regularExpression) {
        final int position = this.content.position();
        this.content.position(offset);
        final StringBuffer buf = new StringBuffer();
        char c = '\0';
        while ((c = (char)this.content.get()) != '\0') {
            if (regularExpression) {
                switch (c) {
                    case '.': {
                        buf.append("\\");
                        break;
                    }
                    case '*':
                    case '+':
                    case '?': {
                        buf.append(".");
                        break;
                    }
                }
            }
            buf.append(c);
        }
        this.content.position(position + 4);
        if (regularExpression) {
            buf.insert(0, '^');
            buf.append('$');
        }
        return buf.toString();
    }
    
    private InputStream getInputStream(final File file) {
        try {
            return new FileInputStream(file);
        }
        catch (final Exception e) {
            OpendesktopMimeDetector.log.error("Error getting InputStream for file [" + file.getAbsolutePath() + "]", e);
            return null;
        }
    }
    
    private InputStream getInputStream(final URL url) {
        try {
            return MimeUtil.getInputStreamForURL(url);
        }
        catch (final Exception e) {
            throw new MimeException("Error getting InputStream for URL [" + url.getPath() + "]", e);
        }
    }
    
    private Collection _getMimeTypes(final Collection mimeTypes, InputStream in) {
        try {
            if (mimeTypes.isEmpty() || mimeTypes.size() > 1) {
                final Collection _mimeTypes = this.getMimeTypesInputStream(in = new BufferedInputStream(in));
                if (!_mimeTypes.isEmpty()) {
                    if (mimeTypes.isEmpty()) {
                        return _mimeTypes;
                    }
                    for (final String mimeType : mimeTypes) {
                        if (_mimeTypes.contains(mimeType)) {
                            mimeTypes.add(mimeType);
                        }
                        for (final String _mimeType : _mimeTypes) {
                            if (this.isMimeTypeSubclass(mimeType, _mimeType)) {
                                mimeTypes.add(mimeType);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            throw new MimeException(e);
        }
        finally {
            MimeDetector.closeStream(in);
        }
        return mimeTypes;
    }
    
    private void initMimeTypes() {
        final int listOffset = this.getAliasListOffset();
        for (int numAliases = this.content.getInt(listOffset), i = 0; i < numAliases; ++i) {
            MimeUtil.addKnownMimeType(this.getString(this.content.getInt(listOffset + 4 + i * 8)));
            MimeUtil.addKnownMimeType(this.getString(this.content.getInt(listOffset + 8 + i * 8)));
        }
    }
    
    static {
        log = new MimeUtil2.MimeLogger(OpendesktopMimeDetector.class.getName());
        OpendesktopMimeDetector.mimeCacheFile = "/usr/share/mime/mime.cache";
        OpendesktopMimeDetector.internalMimeCacheFile = "src/main/resources/mime.cache";
    }
    
    class WeightedMimeType extends MimeType
    {
        private static final long serialVersionUID = 1L;
        String pattern;
        int weight;
        
        WeightedMimeType(final String mimeType, final String pattern, final int weight) {
            super(mimeType);
            this.pattern = pattern;
            this.weight = weight;
        }
    }
}
