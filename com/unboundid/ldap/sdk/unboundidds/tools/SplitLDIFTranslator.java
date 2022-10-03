package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldif.LDIFException;
import com.unboundid.util.Debug;
import java.util.Map;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.security.MessageDigest;
import com.unboundid.util.ByteStringBuffer;
import java.util.Set;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldif.LDIFReaderEntryTranslator;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
abstract class SplitLDIFTranslator implements LDIFReaderEntryTranslator
{
    private final DN splitBaseDN;
    private final RDN[] splitBaseRDNs;
    private final Set<String> errorSetNames;
    private final ThreadLocal<ByteStringBuffer> ldifBuffers;
    private final ThreadLocal<MessageDigest> messageDigests;
    
    SplitLDIFTranslator(final DN splitBaseDN) {
        this.splitBaseDN = splitBaseDN;
        this.splitBaseRDNs = splitBaseDN.getRDNs();
        this.errorSetNames = Collections.singleton(".errors");
        this.ldifBuffers = new ThreadLocal<ByteStringBuffer>();
        this.messageDigests = new ThreadLocal<MessageDigest>();
    }
    
    final DN getSplitBaseDN() {
        return this.splitBaseDN;
    }
    
    final RDN[] getSplitBaseRDNs() {
        return this.splitBaseRDNs;
    }
    
    final Set<String> getErrorSetNames() {
        return this.errorSetNames;
    }
    
    MessageDigest getMD5() throws NoSuchAlgorithmException {
        MessageDigest md5 = this.messageDigests.get();
        if (md5 == null) {
            md5 = MessageDigest.getInstance("MD5");
            this.messageDigests.set(md5);
        }
        return md5;
    }
    
    SplitLDIFEntry createEntry(final Entry e, final Set<String> sets) {
        return this.createEntry(e, null, sets);
    }
    
    SplitLDIFEntry createEntry(final Entry e, final String comment, final Set<String> sets) {
        ByteStringBuffer buffer = this.ldifBuffers.get();
        if (buffer == null) {
            buffer = new ByteStringBuffer();
            this.ldifBuffers.set(buffer);
        }
        else {
            buffer.clear();
        }
        if (comment != null) {
            buffer.append((CharSequence)"# ");
            buffer.append((CharSequence)comment);
            buffer.append(StaticUtils.EOL_BYTES);
        }
        e.toLDIF(buffer, 0);
        buffer.append(StaticUtils.EOL_BYTES);
        return new SplitLDIFEntry(e, buffer.toByteArray(), sets);
    }
    
    SplitLDIFEntry createFromRDNHash(final Entry e, final DN dn, final Map<Integer, Set<String>> setNames) {
        final RDN[] rdns = dn.getRDNs();
        final int targetRDNIndex = rdns.length - this.splitBaseRDNs.length - 1;
        final byte[] normalizedRDNBytes = StaticUtils.getBytes(rdns[targetRDNIndex].toNormalizedString());
        MessageDigest md5Digest;
        try {
            md5Digest = this.getMD5();
        }
        catch (final Exception ex) {
            Debug.debugException(ex);
            return this.createEntry(e, ToolMessages.ERR_SPLIT_LDIF_TRANSLATOR_CANNOT_GET_MD5.get(StaticUtils.getExceptionMessage(ex)), this.errorSetNames);
        }
        final byte[] md5Bytes = md5Digest.digest(normalizedRDNBytes);
        final int checksum = (md5Bytes[0] & 0x7F) << 24 | (md5Bytes[1] & 0xFF) << 16 | (md5Bytes[2] & 0xFF) << 8 | (md5Bytes[3] & 0xFF);
        final int setNumber = checksum % setNames.size();
        return this.createEntry(e, setNames.get(setNumber));
    }
    
    @Override
    public abstract SplitLDIFEntry translate(final Entry p0, final long p1) throws LDIFException;
}
