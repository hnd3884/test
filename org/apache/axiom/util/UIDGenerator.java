package org.apache.axiom.util;

import java.util.Random;
import java.net.URISyntaxException;
import java.net.URI;
import java.security.SecureRandom;

public final class UIDGenerator
{
    private static final long startTimeXorOperand;
    private static final long threadIdXorOperand;
    private static final long seqXorOperand;
    private static final SecureRandom secureRandom;
    private static final UUIDCache[] uuidCaches;
    private static final ThreadLocal triplet;
    
    private UIDGenerator() {
    }
    
    private static void writeReverseLongHex(final long value, final StringBuilder buffer) {
        for (int i = 0; i < 16; ++i) {
            final int n = (int)(value >> 4 * i) & 0xF;
            writeNibble(n, buffer);
        }
    }
    
    private static void writeNibble(final int n, final StringBuilder buffer) {
        buffer.append((char)((n < 10) ? (48 + n) : (97 + n - 10)));
    }
    
    private static void generateHex(final StringBuilder buffer) {
        final long[] values = UIDGenerator.triplet.get();
        writeReverseLongHex(values[2]++ ^ UIDGenerator.seqXorOperand, buffer);
        writeReverseLongHex(values[1], buffer);
        writeReverseLongHex(values[0], buffer);
    }
    
    public static String generateContentId() {
        final StringBuilder buffer = new StringBuilder();
        generateHex(buffer);
        buffer.append("@apache.org");
        return buffer.toString();
    }
    
    public static String generateMimeBoundary() {
        final StringBuilder buffer = new StringBuilder("MIMEBoundary_");
        generateHex(buffer);
        return buffer.toString();
    }
    
    public static String generateUID() {
        final StringBuilder buffer = new StringBuilder(48);
        generateHex(buffer);
        return buffer.toString();
    }
    
    public static String generateURNString() {
        final StringBuilder urn = new StringBuilder(45);
        urn.append("urn:uuid:");
        final UUIDCache cache = UIDGenerator.uuidCaches[(int)Thread.currentThread().getId() & 0xF];
        synchronized (cache) {
            int position = cache.position;
            byte[] randomBytes = cache.randomBytes;
            boolean fill;
            if (randomBytes == null) {
                randomBytes = (cache.randomBytes = new byte[4096]);
                fill = true;
            }
            else if (position == 4096) {
                position = 0;
                fill = true;
            }
            else {
                fill = false;
            }
            if (fill) {
                UIDGenerator.secureRandom.nextBytes(cache.randomBytes);
            }
            writeHex(randomBytes[position], urn);
            writeHex(randomBytes[position + 1], urn);
            writeHex(randomBytes[position + 2], urn);
            writeHex(randomBytes[position + 3], urn);
            urn.append('-');
            writeHex(randomBytes[position + 4], urn);
            writeHex(randomBytes[position + 5], urn);
            urn.append('-');
            writeHex((byte)((randomBytes[position + 6] & 0xF) | 0x40), urn);
            writeHex(randomBytes[position + 7], urn);
            urn.append('-');
            writeHex((byte)((randomBytes[position + 8] & 0x3F) | 0x80), urn);
            writeHex(randomBytes[position + 9], urn);
            urn.append('-');
            writeHex(randomBytes[position + 10], urn);
            writeHex(randomBytes[position + 11], urn);
            writeHex(randomBytes[position + 12], urn);
            writeHex(randomBytes[position + 13], urn);
            writeHex(randomBytes[position + 14], urn);
            writeHex(randomBytes[position + 15], urn);
            cache.position = position + 16;
        }
        return urn.toString();
    }
    
    private static void writeHex(final byte b, final StringBuilder buffer) {
        writeNibble(b >> 4 & 0xF, buffer);
        writeNibble(b & 0xF, buffer);
    }
    
    public static URI generateURN() {
        try {
            return new URI(generateURNString());
        }
        catch (final URISyntaxException ex) {
            throw new Error(ex);
        }
    }
    
    static {
        secureRandom = new SecureRandom();
        final Random rand = new Random();
        threadIdXorOperand = rand.nextLong();
        startTimeXorOperand = rand.nextLong();
        seqXorOperand = rand.nextLong();
        uuidCaches = new UUIDCache[16];
        for (int i = 0; i < 16; ++i) {
            UIDGenerator.uuidCaches[i] = new UUIDCache();
        }
        triplet = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                final long[] values = { Thread.currentThread().getId() ^ UIDGenerator.threadIdXorOperand, System.currentTimeMillis() ^ UIDGenerator.startTimeXorOperand, 0L };
                return values;
            }
        };
    }
}
