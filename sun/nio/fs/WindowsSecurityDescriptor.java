package sun.nio.fs;

import java.nio.file.attribute.FileAttribute;
import java.util.Set;
import java.nio.file.attribute.AclEntryPermission;
import java.util.EnumSet;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.UserPrincipal;
import java.util.Iterator;
import java.io.IOException;
import java.nio.file.ProviderMismatchException;
import java.util.Collection;
import java.util.ArrayList;
import java.nio.file.attribute.AclEntry;
import java.util.List;
import sun.misc.Unsafe;

class WindowsSecurityDescriptor
{
    private static final Unsafe unsafe;
    private static final short SIZEOF_ACL = 8;
    private static final short SIZEOF_ACCESS_ALLOWED_ACE = 12;
    private static final short SIZEOF_ACCESS_DENIED_ACE = 12;
    private static final short SIZEOF_SECURITY_DESCRIPTOR = 20;
    private static final short OFFSETOF_TYPE = 0;
    private static final short OFFSETOF_FLAGS = 1;
    private static final short OFFSETOF_ACCESS_MASK = 4;
    private static final short OFFSETOF_SID = 8;
    private static final WindowsSecurityDescriptor NULL_DESCRIPTOR;
    private final List<Long> sidList;
    private final NativeBuffer aclBuffer;
    private final NativeBuffer sdBuffer;
    
    private WindowsSecurityDescriptor() {
        this.sidList = null;
        this.aclBuffer = null;
        this.sdBuffer = null;
    }
    
    private WindowsSecurityDescriptor(final List<AclEntry> list) throws IOException {
        boolean b = false;
        final ArrayList list2 = new ArrayList((Collection<? extends E>)list);
        this.sidList = new ArrayList<Long>(list2.size());
        try {
            int n = 8;
            final Iterator iterator = list2.iterator();
            while (iterator.hasNext()) {
                final UserPrincipal principal = ((AclEntry)iterator.next()).principal();
                if (!(principal instanceof WindowsUserPrincipals.User)) {
                    throw new ProviderMismatchException();
                }
                final String sidString = ((WindowsUserPrincipals.User)principal).sidString();
                try {
                    final long convertStringSidToSid = WindowsNativeDispatcher.ConvertStringSidToSid(sidString);
                    this.sidList.add(convertStringSidToSid);
                    n += WindowsNativeDispatcher.GetLengthSid(convertStringSidToSid) + Math.max(12, 12);
                }
                catch (final WindowsException ex) {
                    throw new IOException("Failed to get SID for " + principal.getName() + ": " + ex.errorString());
                }
            }
            this.aclBuffer = NativeBuffers.getNativeBuffer(n);
            this.sdBuffer = NativeBuffers.getNativeBuffer(20);
            WindowsNativeDispatcher.InitializeAcl(this.aclBuffer.address(), n);
            for (int i = 0; i < list2.size(); ++i) {
                final AclEntry aclEntry = (AclEntry)list2.get(i);
                final long longValue = this.sidList.get(i);
                try {
                    encode(aclEntry, longValue, this.aclBuffer.address());
                }
                catch (final WindowsException ex2) {
                    throw new IOException("Failed to encode ACE: " + ex2.errorString());
                }
            }
            WindowsNativeDispatcher.InitializeSecurityDescriptor(this.sdBuffer.address());
            WindowsNativeDispatcher.SetSecurityDescriptorDacl(this.sdBuffer.address(), this.aclBuffer.address());
            b = true;
        }
        catch (final WindowsException ex3) {
            throw new IOException(ex3.getMessage());
        }
        finally {
            if (!b) {
                this.release();
            }
        }
    }
    
    void release() {
        if (this.sdBuffer != null) {
            this.sdBuffer.release();
        }
        if (this.aclBuffer != null) {
            this.aclBuffer.release();
        }
        if (this.sidList != null) {
            final Iterator<Long> iterator = this.sidList.iterator();
            while (iterator.hasNext()) {
                WindowsNativeDispatcher.LocalFree(iterator.next());
            }
        }
    }
    
    long address() {
        return (this.sdBuffer == null) ? 0L : this.sdBuffer.address();
    }
    
    private static AclEntry decode(final long n) throws IOException {
        final byte byte1 = WindowsSecurityDescriptor.unsafe.getByte(n + 0L);
        if (byte1 != 0 && byte1 != 1) {
            return null;
        }
        AclEntryType type;
        if (byte1 == 0) {
            type = AclEntryType.ALLOW;
        }
        else {
            type = AclEntryType.DENY;
        }
        final byte byte2 = WindowsSecurityDescriptor.unsafe.getByte(n + 1L);
        final EnumSet<AclEntryFlag> none = EnumSet.noneOf(AclEntryFlag.class);
        if ((byte2 & 0x1) != 0x0) {
            none.add(AclEntryFlag.FILE_INHERIT);
        }
        if ((byte2 & 0x2) != 0x0) {
            none.add(AclEntryFlag.DIRECTORY_INHERIT);
        }
        if ((byte2 & 0x4) != 0x0) {
            none.add(AclEntryFlag.NO_PROPAGATE_INHERIT);
        }
        if ((byte2 & 0x8) != 0x0) {
            none.add(AclEntryFlag.INHERIT_ONLY);
        }
        final int int1 = WindowsSecurityDescriptor.unsafe.getInt(n + 4L);
        final EnumSet<AclEntryPermission> none2 = EnumSet.noneOf(AclEntryPermission.class);
        if ((int1 & 0x1) > 0) {
            none2.add(AclEntryPermission.READ_DATA);
        }
        if ((int1 & 0x2) > 0) {
            none2.add(AclEntryPermission.WRITE_DATA);
        }
        if ((int1 & 0x4) > 0) {
            none2.add(AclEntryPermission.APPEND_DATA);
        }
        if ((int1 & 0x8) > 0) {
            none2.add(AclEntryPermission.READ_NAMED_ATTRS);
        }
        if ((int1 & 0x10) > 0) {
            none2.add(AclEntryPermission.WRITE_NAMED_ATTRS);
        }
        if ((int1 & 0x20) > 0) {
            none2.add(AclEntryPermission.EXECUTE);
        }
        if ((int1 & 0x40) > 0) {
            none2.add(AclEntryPermission.DELETE_CHILD);
        }
        if ((int1 & 0x80) > 0) {
            none2.add(AclEntryPermission.READ_ATTRIBUTES);
        }
        if ((int1 & 0x100) > 0) {
            none2.add(AclEntryPermission.WRITE_ATTRIBUTES);
        }
        if ((int1 & 0x10000) > 0) {
            none2.add(AclEntryPermission.DELETE);
        }
        if ((int1 & 0x20000) > 0) {
            none2.add(AclEntryPermission.READ_ACL);
        }
        if ((int1 & 0x40000) > 0) {
            none2.add(AclEntryPermission.WRITE_ACL);
        }
        if ((int1 & 0x80000) > 0) {
            none2.add(AclEntryPermission.WRITE_OWNER);
        }
        if ((int1 & 0x100000) > 0) {
            none2.add(AclEntryPermission.SYNCHRONIZE);
        }
        return AclEntry.newBuilder().setType(type).setPrincipal(WindowsUserPrincipals.fromSid(n + 8L)).setFlags(none).setPermissions(none2).build();
    }
    
    private static void encode(final AclEntry aclEntry, final long n, final long n2) throws WindowsException {
        if (aclEntry.type() != AclEntryType.ALLOW && aclEntry.type() != AclEntryType.DENY) {
            return;
        }
        final boolean b = aclEntry.type() == AclEntryType.ALLOW;
        final Set<AclEntryPermission> permissions = aclEntry.permissions();
        int n3 = 0;
        if (permissions.contains(AclEntryPermission.READ_DATA)) {
            n3 |= 0x1;
        }
        if (permissions.contains(AclEntryPermission.WRITE_DATA)) {
            n3 |= 0x2;
        }
        if (permissions.contains(AclEntryPermission.APPEND_DATA)) {
            n3 |= 0x4;
        }
        if (permissions.contains(AclEntryPermission.READ_NAMED_ATTRS)) {
            n3 |= 0x8;
        }
        if (permissions.contains(AclEntryPermission.WRITE_NAMED_ATTRS)) {
            n3 |= 0x10;
        }
        if (permissions.contains(AclEntryPermission.EXECUTE)) {
            n3 |= 0x20;
        }
        if (permissions.contains(AclEntryPermission.DELETE_CHILD)) {
            n3 |= 0x40;
        }
        if (permissions.contains(AclEntryPermission.READ_ATTRIBUTES)) {
            n3 |= 0x80;
        }
        if (permissions.contains(AclEntryPermission.WRITE_ATTRIBUTES)) {
            n3 |= 0x100;
        }
        if (permissions.contains(AclEntryPermission.DELETE)) {
            n3 |= 0x10000;
        }
        if (permissions.contains(AclEntryPermission.READ_ACL)) {
            n3 |= 0x20000;
        }
        if (permissions.contains(AclEntryPermission.WRITE_ACL)) {
            n3 |= 0x40000;
        }
        if (permissions.contains(AclEntryPermission.WRITE_OWNER)) {
            n3 |= 0x80000;
        }
        if (permissions.contains(AclEntryPermission.SYNCHRONIZE)) {
            n3 |= 0x100000;
        }
        final Set<AclEntryFlag> flags = aclEntry.flags();
        int n4 = 0;
        if (flags.contains(AclEntryFlag.FILE_INHERIT)) {
            n4 = (byte)(n4 | 0x1);
        }
        if (flags.contains(AclEntryFlag.DIRECTORY_INHERIT)) {
            n4 = (byte)(n4 | 0x2);
        }
        if (flags.contains(AclEntryFlag.NO_PROPAGATE_INHERIT)) {
            n4 = (byte)(n4 | 0x4);
        }
        if (flags.contains(AclEntryFlag.INHERIT_ONLY)) {
            n4 = (byte)(n4 | 0x8);
        }
        if (b) {
            WindowsNativeDispatcher.AddAccessAllowedAceEx(n2, n4, n3, n);
        }
        else {
            WindowsNativeDispatcher.AddAccessDeniedAceEx(n2, n4, n3, n);
        }
    }
    
    static WindowsSecurityDescriptor create(final List<AclEntry> list) throws IOException {
        return new WindowsSecurityDescriptor(list);
    }
    
    static WindowsSecurityDescriptor fromAttribute(final FileAttribute<?>... array) throws IOException {
        WindowsSecurityDescriptor null_DESCRIPTOR = WindowsSecurityDescriptor.NULL_DESCRIPTOR;
        for (final FileAttribute<?> fileAttribute : array) {
            if (null_DESCRIPTOR != WindowsSecurityDescriptor.NULL_DESCRIPTOR) {
                null_DESCRIPTOR.release();
            }
            if (fileAttribute == null) {
                throw new NullPointerException();
            }
            if (!fileAttribute.name().equals("acl:acl")) {
                throw new UnsupportedOperationException("'" + fileAttribute.name() + "' not supported as initial attribute");
            }
            null_DESCRIPTOR = new WindowsSecurityDescriptor(fileAttribute.value());
        }
        return null_DESCRIPTOR;
    }
    
    static List<AclEntry> getAcl(final long n) throws IOException {
        final long getSecurityDescriptorDacl = WindowsNativeDispatcher.GetSecurityDescriptorDacl(n);
        int aceCount;
        if (getSecurityDescriptorDacl == 0L) {
            aceCount = 0;
        }
        else {
            aceCount = WindowsNativeDispatcher.GetAclInformation(getSecurityDescriptorDacl).aceCount();
        }
        final ArrayList list = new ArrayList<AclEntry>(aceCount);
        for (int i = 0; i < aceCount; ++i) {
            final AclEntry decode = decode(WindowsNativeDispatcher.GetAce(getSecurityDescriptorDacl, i));
            if (decode != null) {
                list.add(decode);
            }
        }
        return (List<AclEntry>)list;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        NULL_DESCRIPTOR = new WindowsSecurityDescriptor();
    }
}
