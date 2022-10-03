package sun.security.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ManifestDigester
{
    public static final String MF_MAIN_ATTRS = "Manifest-Main-Attributes";
    private byte[] rawBytes;
    private HashMap<String, Entry> entries;
    
    private boolean findSection(final int n, final Position position) {
        int i = n;
        final int length = this.rawBytes.length;
        int endOfSection = n;
        int n2 = 1;
        position.endOfFirstLine = -1;
        while (i < length) {
            switch (this.rawBytes[i]) {
                case 13: {
                    if (position.endOfFirstLine == -1) {
                        position.endOfFirstLine = i - 1;
                    }
                    if (i < length && this.rawBytes[i + 1] == 10) {
                        ++i;
                    }
                }
                case 10: {
                    if (position.endOfFirstLine == -1) {
                        position.endOfFirstLine = i - 1;
                    }
                    if (n2 != 0 || i == length - 1) {
                        if (i == length - 1) {
                            position.endOfSection = i;
                        }
                        else {
                            position.endOfSection = endOfSection;
                        }
                        position.startOfNext = i + 1;
                        return true;
                    }
                    endOfSection = i;
                    n2 = 1;
                    break;
                }
                default: {
                    n2 = 0;
                    break;
                }
            }
            ++i;
        }
        return false;
    }
    
    public ManifestDigester(final byte[] rawBytes) {
        this.rawBytes = rawBytes;
        this.entries = new HashMap<String, Entry>();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final Position position = new Position();
        if (!this.findSection(0, position)) {
            return;
        }
        this.entries.put("Manifest-Main-Attributes", new Entry().addSection(new Section(0, position.endOfSection + 1, position.startOfNext, this.rawBytes)));
        for (int n = position.startOfNext; this.findSection(n, position); n = position.startOfNext) {
            final int n2 = position.endOfFirstLine - n + 1;
            final int n3 = position.endOfSection - n + 1;
            final int n4 = position.startOfNext - n;
            if (n2 > 6 && this.isNameAttr(rawBytes, n)) {
                final StringBuilder sb = new StringBuilder(n3);
                try {
                    sb.append(new String(rawBytes, n + 6, n2 - 6, "UTF8"));
                    int n5 = n + n2;
                    if (n5 - n < n3) {
                        if (rawBytes[n5] == 13) {
                            n5 += 2;
                        }
                        else {
                            ++n5;
                        }
                    }
                    while (n5 - n < n3 && rawBytes[n5++] == 32) {
                        final int n6 = n5;
                        while (n5 - n < n3 && rawBytes[n5++] != 10) {}
                        if (rawBytes[n5 - 1] != 10) {
                            return;
                        }
                        int n7;
                        if (rawBytes[n5 - 2] == 13) {
                            n7 = n5 - n6 - 2;
                        }
                        else {
                            n7 = n5 - n6 - 1;
                        }
                        sb.append(new String(rawBytes, n6, n7, "UTF8"));
                    }
                    final Entry entry = this.entries.get(sb.toString());
                    if (entry == null) {
                        this.entries.put(sb.toString(), new Entry().addSection(new Section(n, n3, n4, this.rawBytes)));
                    }
                    else {
                        entry.addSection(new Section(n, n3, n4, this.rawBytes));
                    }
                }
                catch (final UnsupportedEncodingException ex) {
                    throw new IllegalStateException("UTF8 not available on platform");
                }
            }
        }
    }
    
    private boolean isNameAttr(final byte[] array, final int n) {
        return (array[n] == 78 || array[n] == 110) && (array[n + 1] == 97 || array[n + 1] == 65) && (array[n + 2] == 109 || array[n + 2] == 77) && (array[n + 3] == 101 || array[n + 3] == 69) && array[n + 4] == 58 && array[n + 5] == 32;
    }
    
    public Entry get(final String s, final boolean oldStyle) {
        final Entry entry = this.entries.get(s);
        if (entry != null) {
            entry.oldStyle = oldStyle;
        }
        return entry;
    }
    
    public byte[] manifestDigest(final MessageDigest messageDigest) {
        messageDigest.reset();
        messageDigest.update(this.rawBytes, 0, this.rawBytes.length);
        return messageDigest.digest();
    }
    
    static class Position
    {
        int endOfFirstLine;
        int endOfSection;
        int startOfNext;
    }
    
    public static class Entry
    {
        private List<Section> sections;
        boolean oldStyle;
        
        public Entry() {
            this.sections = new ArrayList<Section>();
        }
        
        private Entry addSection(final Section section) {
            this.sections.add(section);
            return this;
        }
        
        public byte[] digest(final MessageDigest messageDigest) {
            messageDigest.reset();
            for (final Section section : this.sections) {
                if (this.oldStyle) {
                    doOldStyle(messageDigest, section.rawBytes, section.offset, section.lengthWithBlankLine);
                }
                else {
                    messageDigest.update(section.rawBytes, section.offset, section.lengthWithBlankLine);
                }
            }
            return messageDigest.digest();
        }
        
        public byte[] digestWorkaround(final MessageDigest messageDigest) {
            messageDigest.reset();
            for (final Section section : this.sections) {
                messageDigest.update(section.rawBytes, section.offset, section.length);
            }
            return messageDigest.digest();
        }
    }
    
    private static class Section
    {
        int offset;
        int length;
        int lengthWithBlankLine;
        byte[] rawBytes;
        
        public Section(final int offset, final int length, final int lengthWithBlankLine, final byte[] rawBytes) {
            this.offset = offset;
            this.length = length;
            this.lengthWithBlankLine = lengthWithBlankLine;
            this.rawBytes = rawBytes;
        }
        
        private static void doOldStyle(final MessageDigest messageDigest, final byte[] array, final int n, final int n2) {
            int i = n;
            int n3 = n;
            final int n4 = n + n2;
            int n5 = -1;
            while (i < n4) {
                if (array[i] == 13 && n5 == 32) {
                    messageDigest.update(array, n3, i - n3 - 1);
                    n3 = i;
                }
                n5 = array[i];
                ++i;
            }
            messageDigest.update(array, n3, i - n3);
        }
    }
}
