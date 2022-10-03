package eu.medsea.mimeutil.detector;

import eu.medsea.util.StringUtil;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Vector;
import java.util.List;
import eu.medsea.mimeutil.MimeType;
import java.util.ArrayList;

class MagicMimeEntry
{
    public static final int STRING_TYPE = 1;
    public static final int BELONG_TYPE = 2;
    public static final int SHORT_TYPE = 3;
    public static final int LELONG_TYPE = 4;
    public static final int BESHORT_TYPE = 5;
    public static final int LESHORT_TYPE = 6;
    public static final int BYTE_TYPE = 7;
    public static final int UNKNOWN_TYPE = 20;
    private ArrayList subEntries;
    private int checkBytesFrom;
    private int type;
    private String typeStr;
    private String content;
    private long contentNumber;
    private MimeType mimeType;
    private String mimeEnc;
    private MagicMimeEntry parent;
    private MagicMimeEntryOperation operation;
    boolean isBetween;
    
    public MagicMimeEntry(final ArrayList entries) throws InvalidMagicMimeEntryException {
        this(0, null, entries);
    }
    
    private MagicMimeEntry(final int level, final MagicMimeEntry parent, final ArrayList entries) throws InvalidMagicMimeEntryException {
        this.subEntries = new ArrayList();
        this.operation = MagicMimeEntryOperation.EQUALS;
        if (entries == null || entries.size() == 0) {
            return;
        }
        if ((this.parent = parent) != null) {
            parent.subEntries.add(this);
        }
        try {
            this.addEntry(entries.get(0));
        }
        catch (final Exception e) {
            throw new InvalidMagicMimeEntryException(entries, e);
        }
        entries.remove(0);
        while (entries.size() > 0) {
            final int thisLevel = this.howManyGreaterThans(entries.get(0));
            if (thisLevel <= level) {
                break;
            }
            new MagicMimeEntry(thisLevel, this, entries);
        }
    }
    
    @Override
    public String toString() {
        return "MimeMagicType: " + this.checkBytesFrom + ", " + this.type + ", " + this.content + ", " + this.mimeType + ", " + this.mimeEnc;
    }
    
    public void traverseAndPrint(final String tabs) {
        System.out.println(tabs + this.toString());
        for (int len = this.subEntries.size(), i = 0; i < len; ++i) {
            final MagicMimeEntry me = this.subEntries.get(i);
            me.traverseAndPrint(tabs + "\t");
        }
    }
    
    private int howManyGreaterThans(final String aLine) {
        if (aLine == null) {
            return -1;
        }
        int i = 0;
        for (int len = aLine.length(); i < len && aLine.charAt(i) == '>'; ++i) {}
        return i;
    }
    
    void addEntry(final String aLine) throws InvalidMagicMimeEntryException {
        final String trimmed = aLine.replaceAll("[\\\\][ ]", "<##>").replaceAll("^>*", "").replaceAll("\\s+", "\t").replaceAll("[\t]{2,}", "\t").replaceAll("<##>", "\\\\ ");
        String[] tokens = trimmed.split("\t");
        final Vector v = new Vector();
        for (int i = 0; i < tokens.length; ++i) {
            if (!"".equals(tokens[i])) {
                v.add(tokens[i]);
            }
        }
        tokens = new String[v.size()];
        tokens = v.toArray(tokens);
        if (tokens.length > 0) {
            final String tok = tokens[0].trim();
            try {
                if (tok.startsWith("0x")) {
                    this.checkBytesFrom = Integer.parseInt(tok.substring(2), 16);
                }
                else {
                    this.checkBytesFrom = Integer.parseInt(tok);
                }
            }
            catch (final NumberFormatException e) {
                throw new InvalidMagicMimeEntryException(Collections.singletonList(this), e);
            }
        }
        if (tokens.length > 1) {
            this.typeStr = tokens[1].trim();
            this.type = this.getType(this.typeStr);
        }
        if (tokens.length > 2) {
            this.content = this.ltrim(tokens[2]);
            switch (this.type) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    this.operation = MagicMimeEntryOperation.getOperationForNumberField(this.content);
                    break;
                }
                default: {
                    this.operation = MagicMimeEntryOperation.getOperationForStringField(this.content);
                    break;
                }
            }
            if (this.content.length() > 0 && this.content.charAt(0) == this.operation.getOperationID()) {
                this.content = this.content.substring(1);
            }
            this.content = stringWithEscapeSubstitutions(this.content);
        }
        else {
            this.content = "";
        }
        if (tokens.length > 3) {
            this.mimeType = new MimeType(tokens[3].trim());
        }
        if (tokens.length > 4) {
            this.mimeEnc = tokens[4].trim();
        }
        this.initContentNumber();
    }
    
    private void initContentNumber() {
        this.contentNumber = 0L;
        if (this.content.length() == 0) {
            return;
        }
        switch (this.type) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                if (this.content.startsWith("0x")) {
                    this.contentNumber = Long.parseLong(this.content.substring(2).trim(), 16);
                    break;
                }
                if (this.content.startsWith("0")) {
                    this.contentNumber = Long.parseLong(this.content.trim(), 8);
                    break;
                }
                this.contentNumber = Long.parseLong(this.content.trim());
                break;
            }
        }
    }
    
    private String ltrim(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != ' ') {
                return s.substring(i);
            }
        }
        return s;
    }
    
    private int getType(final String tok) {
        if (tok.startsWith("string")) {
            return 1;
        }
        if (tok.startsWith("belong")) {
            return 2;
        }
        if (tok.equals("short")) {
            return 3;
        }
        if (tok.startsWith("lelong")) {
            return 4;
        }
        if (tok.startsWith("beshort")) {
            return 5;
        }
        if (tok.startsWith("leshort")) {
            return 6;
        }
        if (tok.equals("byte") || tok.startsWith("ubyte")) {
            return 7;
        }
        return 20;
    }
    
    public int getCheckBytesFrom() {
        return this.checkBytesFrom;
    }
    
    public int getType() {
        return this.type;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public MimeType getMimeType() {
        return this.mimeType;
    }
    
    MagicMimeEntry getMatch(final InputStream in) throws IOException {
        final int bytesToRead = this.getInputStreamMarkLength();
        in.mark(bytesToRead);
        try {
            final byte[] content = new byte[bytesToRead];
            int offset = 0;
            int bytesRead;
            for (int restBytesToRead = bytesToRead; restBytesToRead > 0; restBytesToRead -= bytesRead) {
                bytesRead = in.read(content, offset, restBytesToRead);
                if (bytesRead < 0) {
                    break;
                }
                offset += bytesRead;
            }
            return this.getMatch(content);
        }
        finally {
            in.reset();
        }
    }
    
    MagicMimeEntry getMatch(final byte[] content) throws IOException {
        final ByteBuffer buf = this.readBuffer(content);
        if (buf == null) {
            return null;
        }
        buf.position(0);
        final boolean matches = this.match(buf);
        if (matches) {
            final int subLen = this.subEntries.size();
            final MimeType mimeType = this.getMimeType();
            if (subLen > 0) {
                for (int k = 0; k < subLen; ++k) {
                    final MagicMimeEntry me = this.subEntries.get(k);
                    final MagicMimeEntry matchingEntry = me.getMatch(content);
                    if (matchingEntry != null) {
                        return matchingEntry;
                    }
                }
                if (mimeType != null) {
                    return this;
                }
            }
            else if (mimeType != null) {
                return this;
            }
        }
        return null;
    }
    
    MagicMimeEntry getMatch(final RandomAccessFile raf) throws IOException {
        final ByteBuffer buf = this.readBuffer(raf);
        if (buf == null) {
            return null;
        }
        final boolean matches = this.match(buf);
        if (matches) {
            final MimeType mimeType = this.getMimeType();
            if (this.subEntries.size() > 0) {
                for (int i = 0; i < this.subEntries.size(); ++i) {
                    final MagicMimeEntry me = this.subEntries.get(i);
                    final MagicMimeEntry matchingEntry = me.getMatch(raf);
                    if (matchingEntry != null) {
                        return matchingEntry;
                    }
                }
                if (mimeType != null) {
                    return this;
                }
            }
            else if (mimeType != null) {
                return this;
            }
        }
        return null;
    }
    
    private ByteBuffer readBuffer(final byte[] content) throws IOException {
        final int startPos = this.getCheckBytesFrom();
        if (content == null || startPos > content.length) {
            return null;
        }
        ByteBuffer buf = null;
        try {
            switch (this.getType()) {
                case 1: {
                    int len = 0;
                    final int index = this.typeStr.indexOf(">");
                    if (index != -1) {
                        len = Integer.parseInt(this.typeStr.substring(index + 1, this.typeStr.length() - 1));
                        this.isBetween = true;
                    }
                    else {
                        len = this.getContent().length();
                    }
                    buf = ByteBuffer.allocate(len);
                    buf.put(content, startPos, len);
                    break;
                }
                case 3:
                case 5:
                case 6: {
                    buf = ByteBuffer.allocate(2);
                    buf.put(content, startPos, 2);
                    break;
                }
                case 2:
                case 4: {
                    buf = ByteBuffer.allocate(4);
                    buf.put(content, startPos, 4);
                    break;
                }
                case 7: {
                    buf = ByteBuffer.allocate(1);
                    buf.put(content, startPos, 1);
                    break;
                }
                default: {
                    buf = null;
                    break;
                }
            }
        }
        catch (final IndexOutOfBoundsException iobe) {
            return null;
        }
        return buf;
    }
    
    private ByteBuffer readBuffer(final RandomAccessFile raf) throws IOException {
        final int startPos = this.getCheckBytesFrom();
        if (startPos > raf.length()) {
            return null;
        }
        raf.seek(startPos);
        ByteBuffer buf = null;
        switch (this.getType()) {
            case 1: {
                int len = 0;
                final int index = this.typeStr.indexOf(">");
                if (index != -1) {
                    len = Integer.parseInt(this.typeStr.substring(index + 1, this.typeStr.length() - 1));
                    this.isBetween = true;
                }
                else {
                    len = this.getContent().length();
                }
                buf = ByteBuffer.allocate(len);
                raf.read(buf.array(), 0, len);
                break;
            }
            case 3:
            case 5:
            case 6: {
                buf = ByteBuffer.allocate(2);
                raf.read(buf.array(), 0, 2);
                break;
            }
            case 2:
            case 4: {
                buf = ByteBuffer.allocate(4);
                raf.read(buf.array(), 0, 4);
                break;
            }
            case 7: {
                buf = ByteBuffer.allocate(1);
                raf.read(buf.array(), 0, 1);
                break;
            }
            default: {
                buf = null;
                break;
            }
        }
        return buf;
    }
    
    private int getInputStreamMarkLength() {
        int len = this._getInputStreamMarkLength();
        for (final MagicMimeEntry subEntry : this.subEntries) {
            final int subLen = subEntry.getInputStreamMarkLength();
            if (len < subLen) {
                len = subLen;
            }
        }
        return len;
    }
    
    private int _getInputStreamMarkLength() {
        switch (this.getType()) {
            case 1: {
                int len = 0;
                final int index = this.typeStr.indexOf(">");
                if (index != -1) {
                    len = Integer.parseInt(this.typeStr.substring(index + 1, this.typeStr.length() - 1));
                    this.isBetween = true;
                }
                else if (this.getContent() != null) {
                    len = this.getContent().length();
                }
                return this.getCheckBytesFrom() + len + 1;
            }
            case 3:
            case 5:
            case 6: {
                return this.getCheckBytesFrom() + 2;
            }
            case 2:
            case 4: {
                return this.getCheckBytesFrom() + 4;
            }
            case 7: {
                return this.getCheckBytesFrom() + 1;
            }
            default: {
                return 0;
            }
        }
    }
    
    private boolean match(final ByteBuffer buf) throws IOException {
        boolean matches = true;
        ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        switch (this.getType()) {
            case 1: {
                matches = this.matchString(buf);
                break;
            }
            case 3: {
                matches = this.matchShort(buf, byteOrder);
                break;
            }
            case 5:
            case 6: {
                if (this.getType() == 6) {
                    byteOrder = ByteOrder.LITTLE_ENDIAN;
                }
                matches = this.matchShort(buf, byteOrder);
                break;
            }
            case 2:
            case 4: {
                if (this.getType() == 4) {
                    byteOrder = ByteOrder.LITTLE_ENDIAN;
                }
                matches = this.matchLong(buf, byteOrder);
                break;
            }
            case 7: {
                matches = this.matchByte(buf);
                break;
            }
            default: {
                matches = false;
                break;
            }
        }
        return matches;
    }
    
    private boolean matchString(final ByteBuffer bbuf) throws IOException {
        if (this.isBetween) {
            final String buffer = new String(bbuf.array());
            return StringUtil.contains(buffer, this.getContent());
        }
        if (this.operation.equals(MagicMimeEntryOperation.EQUALS)) {
            for (int read = this.getContent().length(), j = 0; j < read; ++j) {
                if ((bbuf.get(j) & 0xFF) != this.getContent().charAt(j)) {
                    return false;
                }
            }
            return true;
        }
        if (this.operation.equals(MagicMimeEntryOperation.NOT_EQUALS)) {
            for (int read = this.getContent().length(), j = 0; j < read; ++j) {
                if ((bbuf.get(j) & 0xFF) != this.getContent().charAt(j)) {
                    return true;
                }
            }
            return false;
        }
        if (this.operation.equals(MagicMimeEntryOperation.GREATER_THAN)) {
            final String buffer = new String(bbuf.array());
            return buffer.compareTo(this.getContent()) > 0;
        }
        if (this.operation.equals(MagicMimeEntryOperation.LESS_THAN)) {
            final String buffer = new String(bbuf.array());
            return buffer.compareTo(this.getContent()) < 0;
        }
        return false;
    }
    
    private long getMask(final String maskString) {
        final String[] tokens = maskString.split("&");
        if (tokens.length < 2) {
            return 4294967295L;
        }
        if (tokens[1].startsWith("0x")) {
            return Long.parseLong(tokens[1].substring(2).trim(), 16);
        }
        if (tokens[1].startsWith("0")) {
            return Long.parseLong(tokens[1], 8);
        }
        return Long.parseLong(tokens[1]);
    }
    
    private boolean matchByte(final ByteBuffer bbuf) throws IOException {
        final short found = (short)(bbuf.get(0) & 0xFF & (short)this.getMask(this.typeStr));
        if (this.operation.equals(MagicMimeEntryOperation.EQUALS)) {
            return found == this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.NOT_EQUALS)) {
            return found != this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.GREATER_THAN)) {
            return found > this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.LESS_THAN)) {
            return found < this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.AND)) {
            final boolean result = ((long)found & this.contentNumber) == this.contentNumber;
            return result;
        }
        if (this.operation.equals(MagicMimeEntryOperation.ANY)) {
            return true;
        }
        if (this.operation.equals(MagicMimeEntryOperation.CLEAR)) {
            final long maskedFound = (long)found & this.contentNumber;
            final boolean result2 = (maskedFound ^ this.contentNumber) == 0x0L;
            return result2;
        }
        if (this.operation.equals(MagicMimeEntryOperation.NEGATED)) {
            final int negatedFound = ~found;
            return negatedFound == this.contentNumber;
        }
        return false;
    }
    
    private boolean matchShort(final ByteBuffer bbuf, final ByteOrder bo) throws IOException {
        bbuf.order(bo);
        final int found = bbuf.getShort() & 0xFFFF & (int)this.getMask(this.typeStr);
        if (this.operation.equals(MagicMimeEntryOperation.EQUALS)) {
            return found == this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.NOT_EQUALS)) {
            return found != this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.GREATER_THAN)) {
            return found > this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.LESS_THAN)) {
            return found < this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.AND)) {
            final boolean result = ((long)found & this.contentNumber) == this.contentNumber;
            return result;
        }
        if (this.operation.equals(MagicMimeEntryOperation.ANY)) {
            return true;
        }
        if (this.operation.equals(MagicMimeEntryOperation.CLEAR)) {
            final long maskedFound = (long)found & this.contentNumber;
            final boolean result2 = (maskedFound ^ this.contentNumber) == 0x0L;
            return result2;
        }
        if (this.operation.equals(MagicMimeEntryOperation.NEGATED)) {
            final int negatedFound = ~found;
            return negatedFound == this.contentNumber;
        }
        return false;
    }
    
    private boolean matchLong(final ByteBuffer bbuf, final ByteOrder bo) throws IOException {
        bbuf.order(bo);
        final long found = (long)bbuf.getInt() & 0xFFFFFFFFL & this.getMask(this.typeStr);
        if (this.operation.equals(MagicMimeEntryOperation.EQUALS)) {
            return found == this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.NOT_EQUALS)) {
            return found != this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.GREATER_THAN)) {
            return found > this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.LESS_THAN)) {
            return found < this.contentNumber;
        }
        if (this.operation.equals(MagicMimeEntryOperation.AND)) {
            final boolean result = (found & this.contentNumber) == this.contentNumber;
            return result;
        }
        if (this.operation.equals(MagicMimeEntryOperation.ANY)) {
            return true;
        }
        if (this.operation.equals(MagicMimeEntryOperation.CLEAR)) {
            final long maskedFound = found & this.contentNumber;
            final boolean result2 = (maskedFound ^ this.contentNumber) == 0x0L;
            return result2;
        }
        if (this.operation.equals(MagicMimeEntryOperation.NEGATED)) {
            final long negatedFound = ~found;
            return negatedFound == this.contentNumber;
        }
        return false;
    }
    
    private static String stringWithEscapeSubstitutions(final String s) {
        final StringBuffer ret = new StringBuffer();
        for (int len = s.length(), indx = 0; indx < len; ++indx) {
            final int c = s.charAt(indx);
            if (c == 10) {
                break;
            }
            if (c == 92) {
                if (++indx >= len) {
                    ret.append((char)c);
                    break;
                }
                int cn = s.charAt(indx);
                if (cn == 92) {
                    ret.append('\\');
                }
                else if (cn == 32) {
                    ret.append(' ');
                }
                else if (cn == 116) {
                    ret.append('\t');
                }
                else if (cn == 110) {
                    ret.append('\n');
                }
                else if (cn == 114) {
                    ret.append('\r');
                }
                else if (cn == 120) {
                    indx += 2;
                    if (indx >= len) {
                        ret.append((char)c);
                        ret.append((char)cn);
                        break;
                    }
                    final String hexDigits = s.substring(indx - 1, indx + 1);
                    int hexEncodedValue;
                    try {
                        hexEncodedValue = Integer.parseInt(hexDigits, 16);
                    }
                    catch (final NumberFormatException x) {
                        ret.append((char)c);
                        ret.append(hexDigits);
                        break;
                    }
                    ret.append((char)hexEncodedValue);
                }
                else if (cn >= 48 && cn <= 55) {
                    int escape = cn - 48;
                    if (++indx >= len) {
                        ret.append((char)escape);
                        break;
                    }
                    cn = s.charAt(indx);
                    if (cn >= 48 && cn <= 55) {
                        escape <<= 3;
                        escape |= cn - 48;
                        if (++indx >= len) {
                            ret.append((char)escape);
                            break;
                        }
                        cn = s.charAt(indx);
                        if (cn >= 48 && cn <= 55) {
                            escape <<= 3;
                            escape |= cn - 48;
                        }
                        else {
                            --indx;
                        }
                    }
                    else {
                        --indx;
                    }
                    ret.append((char)escape);
                }
                else {
                    ret.append((char)cn);
                }
            }
            else {
                ret.append((char)c);
            }
        }
        return new String(ret);
    }
    
    public boolean containsMimeType(final String mimeType) {
        if (this.mimeType != null && this.mimeType.equals(mimeType)) {
            return true;
        }
        for (final MagicMimeEntry subEntry : this.subEntries) {
            if (subEntry.containsMimeType(mimeType)) {
                return true;
            }
        }
        return false;
    }
    
    public MagicMimeEntry getParent() {
        return this.parent;
    }
    
    public List getSubEntries() {
        return Collections.unmodifiableList((List<?>)this.subEntries);
    }
}
