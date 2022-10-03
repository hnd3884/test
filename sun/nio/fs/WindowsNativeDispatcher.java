package sun.nio.fs;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class WindowsNativeDispatcher
{
    private static final Unsafe unsafe;
    
    private WindowsNativeDispatcher() {
    }
    
    static native long CreateEvent(final boolean p0, final boolean p1) throws WindowsException;
    
    static long CreateFile(final String s, final int n, final int n2, final long n3, final int n4, final int n5) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return CreateFile0(nativeBuffer.address(), n, n2, n3, n4, n5);
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    static long CreateFile(final String s, final int n, final int n2, final int n3, final int n4) throws WindowsException {
        return CreateFile(s, n, n2, 0L, n3, n4);
    }
    
    private static native long CreateFile0(final long p0, final int p1, final int p2, final long p3, final int p4, final int p5) throws WindowsException;
    
    static native void CloseHandle(final long p0);
    
    static void DeleteFile(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            DeleteFile0(nativeBuffer.address());
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native void DeleteFile0(final long p0) throws WindowsException;
    
    static void CreateDirectory(final String s, final long n) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            CreateDirectory0(nativeBuffer.address(), n);
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native void CreateDirectory0(final long p0, final long p1) throws WindowsException;
    
    static void RemoveDirectory(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            RemoveDirectory0(nativeBuffer.address());
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native void RemoveDirectory0(final long p0) throws WindowsException;
    
    static native void DeviceIoControlSetSparse(final long p0) throws WindowsException;
    
    static native void DeviceIoControlGetReparsePoint(final long p0, final long p1, final int p2) throws WindowsException;
    
    static FirstFile FindFirstFile(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            final FirstFile firstFile = new FirstFile();
            FindFirstFile0(nativeBuffer.address(), firstFile);
            return firstFile;
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native void FindFirstFile0(final long p0, final FirstFile p1) throws WindowsException;
    
    static long FindFirstFile(final String s, final long n) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return FindFirstFile1(nativeBuffer.address(), n);
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native long FindFirstFile1(final long p0, final long p1) throws WindowsException;
    
    static native String FindNextFile(final long p0, final long p1) throws WindowsException;
    
    static FirstStream FindFirstStream(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            final FirstStream firstStream = new FirstStream();
            FindFirstStream0(nativeBuffer.address(), firstStream);
            if (firstStream.handle() == -1L) {
                return null;
            }
            return firstStream;
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native void FindFirstStream0(final long p0, final FirstStream p1) throws WindowsException;
    
    static native String FindNextStream(final long p0) throws WindowsException;
    
    static native void FindClose(final long p0) throws WindowsException;
    
    static native void GetFileInformationByHandle(final long p0, final long p1) throws WindowsException;
    
    static void CopyFileEx(final String s, final String s2, final int n, final long n2) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        final NativeBuffer nativeBuffer2 = asNativeBuffer(s2);
        try {
            CopyFileEx0(nativeBuffer.address(), nativeBuffer2.address(), n, n2);
        }
        finally {
            nativeBuffer2.release();
            nativeBuffer.release();
        }
    }
    
    private static native void CopyFileEx0(final long p0, final long p1, final int p2, final long p3) throws WindowsException;
    
    static void MoveFileEx(final String s, final String s2, final int n) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        final NativeBuffer nativeBuffer2 = asNativeBuffer(s2);
        try {
            MoveFileEx0(nativeBuffer.address(), nativeBuffer2.address(), n);
        }
        finally {
            nativeBuffer2.release();
            nativeBuffer.release();
        }
    }
    
    private static native void MoveFileEx0(final long p0, final long p1, final int p2) throws WindowsException;
    
    static int GetFileAttributes(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return GetFileAttributes0(nativeBuffer.address());
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native int GetFileAttributes0(final long p0) throws WindowsException;
    
    static void SetFileAttributes(final String s, final int n) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            SetFileAttributes0(nativeBuffer.address(), n);
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native void SetFileAttributes0(final long p0, final int p1) throws WindowsException;
    
    static void GetFileAttributesEx(final String s, final long n) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            GetFileAttributesEx0(nativeBuffer.address(), n);
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native void GetFileAttributesEx0(final long p0, final long p1) throws WindowsException;
    
    static native void SetFileTime(final long p0, final long p1, final long p2, final long p3) throws WindowsException;
    
    static native void SetEndOfFile(final long p0) throws WindowsException;
    
    static native int GetLogicalDrives() throws WindowsException;
    
    static VolumeInformation GetVolumeInformation(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            final VolumeInformation volumeInformation = new VolumeInformation();
            GetVolumeInformation0(nativeBuffer.address(), volumeInformation);
            return volumeInformation;
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native void GetVolumeInformation0(final long p0, final VolumeInformation p1) throws WindowsException;
    
    static int GetDriveType(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return GetDriveType0(nativeBuffer.address());
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native int GetDriveType0(final long p0) throws WindowsException;
    
    static DiskFreeSpace GetDiskFreeSpaceEx(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            final DiskFreeSpace diskFreeSpace = new DiskFreeSpace();
            GetDiskFreeSpaceEx0(nativeBuffer.address(), diskFreeSpace);
            return diskFreeSpace;
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native void GetDiskFreeSpaceEx0(final long p0, final DiskFreeSpace p1) throws WindowsException;
    
    static String GetVolumePathName(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return GetVolumePathName0(nativeBuffer.address());
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native String GetVolumePathName0(final long p0) throws WindowsException;
    
    static native void InitializeSecurityDescriptor(final long p0) throws WindowsException;
    
    static native void InitializeAcl(final long p0, final int p1) throws WindowsException;
    
    static int GetFileSecurity(final String s, final int n, final long n2, final int n3) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return GetFileSecurity0(nativeBuffer.address(), n, n2, n3);
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native int GetFileSecurity0(final long p0, final int p1, final long p2, final int p3) throws WindowsException;
    
    static void SetFileSecurity(final String s, final int n, final long n2) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            SetFileSecurity0(nativeBuffer.address(), n, n2);
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    static native void SetFileSecurity0(final long p0, final int p1, final long p2) throws WindowsException;
    
    static native long GetSecurityDescriptorOwner(final long p0) throws WindowsException;
    
    static native void SetSecurityDescriptorOwner(final long p0, final long p1) throws WindowsException;
    
    static native long GetSecurityDescriptorDacl(final long p0);
    
    static native void SetSecurityDescriptorDacl(final long p0, final long p1) throws WindowsException;
    
    static AclInformation GetAclInformation(final long n) {
        final AclInformation aclInformation = new AclInformation();
        GetAclInformation0(n, aclInformation);
        return aclInformation;
    }
    
    private static native void GetAclInformation0(final long p0, final AclInformation p1);
    
    static native long GetAce(final long p0, final int p1);
    
    static native void AddAccessAllowedAceEx(final long p0, final int p1, final int p2, final long p3) throws WindowsException;
    
    static native void AddAccessDeniedAceEx(final long p0, final int p1, final int p2, final long p3) throws WindowsException;
    
    static Account LookupAccountSid(final long n) throws WindowsException {
        final Account account = new Account();
        LookupAccountSid0(n, account);
        return account;
    }
    
    private static native void LookupAccountSid0(final long p0, final Account p1) throws WindowsException;
    
    static int LookupAccountName(final String s, final long n, final int n2) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return LookupAccountName0(nativeBuffer.address(), n, n2);
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native int LookupAccountName0(final long p0, final long p1, final int p2) throws WindowsException;
    
    static native int GetLengthSid(final long p0);
    
    static native String ConvertSidToStringSid(final long p0) throws WindowsException;
    
    static long ConvertStringSidToSid(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return ConvertStringSidToSid0(nativeBuffer.address());
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native long ConvertStringSidToSid0(final long p0) throws WindowsException;
    
    static native long GetCurrentProcess();
    
    static native long GetCurrentThread();
    
    static native long OpenProcessToken(final long p0, final int p1) throws WindowsException;
    
    static native long OpenThreadToken(final long p0, final int p1, final boolean p2) throws WindowsException;
    
    static native long DuplicateTokenEx(final long p0, final int p1) throws WindowsException;
    
    static native void SetThreadToken(final long p0, final long p1) throws WindowsException;
    
    static native int GetTokenInformation(final long p0, final int p1, final long p2, final int p3) throws WindowsException;
    
    static native void AdjustTokenPrivileges(final long p0, final long p1, final int p2) throws WindowsException;
    
    static native boolean AccessCheck(final long p0, final long p1, final int p2, final int p3, final int p4, final int p5, final int p6) throws WindowsException;
    
    static long LookupPrivilegeValue(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return LookupPrivilegeValue0(nativeBuffer.address());
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native long LookupPrivilegeValue0(final long p0) throws WindowsException;
    
    static void CreateSymbolicLink(final String s, final String s2, final int n) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        final NativeBuffer nativeBuffer2 = asNativeBuffer(s2);
        try {
            CreateSymbolicLink0(nativeBuffer.address(), nativeBuffer2.address(), n);
        }
        finally {
            nativeBuffer2.release();
            nativeBuffer.release();
        }
    }
    
    private static native void CreateSymbolicLink0(final long p0, final long p1, final int p2) throws WindowsException;
    
    static void CreateHardLink(final String s, final String s2) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        final NativeBuffer nativeBuffer2 = asNativeBuffer(s2);
        try {
            CreateHardLink0(nativeBuffer.address(), nativeBuffer2.address());
        }
        finally {
            nativeBuffer2.release();
            nativeBuffer.release();
        }
    }
    
    private static native void CreateHardLink0(final long p0, final long p1) throws WindowsException;
    
    static String GetFullPathName(final String s) throws WindowsException {
        final NativeBuffer nativeBuffer = asNativeBuffer(s);
        try {
            return GetFullPathName0(nativeBuffer.address());
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    private static native String GetFullPathName0(final long p0) throws WindowsException;
    
    static native String GetFinalPathNameByHandle(final long p0) throws WindowsException;
    
    static native String FormatMessage(final int p0);
    
    static native void LocalFree(final long p0);
    
    static native long CreateIoCompletionPort(final long p0, final long p1, final long p2) throws WindowsException;
    
    static CompletionStatus GetQueuedCompletionStatus(final long n) throws WindowsException {
        final CompletionStatus completionStatus = new CompletionStatus();
        GetQueuedCompletionStatus0(n, completionStatus);
        return completionStatus;
    }
    
    private static native void GetQueuedCompletionStatus0(final long p0, final CompletionStatus p1) throws WindowsException;
    
    static native void PostQueuedCompletionStatus(final long p0, final long p1) throws WindowsException;
    
    static native void ReadDirectoryChangesW(final long p0, final long p1, final int p2, final boolean p3, final int p4, final long p5, final long p6) throws WindowsException;
    
    static native void CancelIo(final long p0) throws WindowsException;
    
    static native int GetOverlappedResult(final long p0, final long p1) throws WindowsException;
    
    static BackupResult BackupRead(final long n, final long n2, final int n3, final boolean b, final long n4) throws WindowsException {
        final BackupResult backupResult = new BackupResult();
        BackupRead0(n, n2, n3, b, n4, backupResult);
        return backupResult;
    }
    
    private static native void BackupRead0(final long p0, final long p1, final int p2, final boolean p3, final long p4, final BackupResult p5) throws WindowsException;
    
    static native void BackupSeek(final long p0, final long p1, final long p2) throws WindowsException;
    
    static NativeBuffer asNativeBuffer(final String owner) throws WindowsException {
        if (owner.length() > 1073741822) {
            throw new WindowsException("String too long to convert to native buffer");
        }
        final int n = owner.length() << 1;
        final int n2 = n + 2;
        NativeBuffer nativeBuffer = NativeBuffers.getNativeBufferFromCache(n2);
        if (nativeBuffer == null) {
            nativeBuffer = NativeBuffers.allocNativeBuffer(n2);
        }
        else if (nativeBuffer.owner() == owner) {
            return nativeBuffer;
        }
        WindowsNativeDispatcher.unsafe.copyMemory(owner.toCharArray(), Unsafe.ARRAY_CHAR_BASE_OFFSET, null, nativeBuffer.address(), n);
        WindowsNativeDispatcher.unsafe.putChar(nativeBuffer.address() + n, '\0');
        nativeBuffer.setOwner(owner);
        return nativeBuffer;
    }
    
    private static native void initIDs();
    
    static {
        unsafe = Unsafe.getUnsafe();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("net");
                System.loadLibrary("nio");
                return null;
            }
        });
        initIDs();
    }
    
    static class FirstFile
    {
        private long handle;
        private String name;
        private int attributes;
        
        private FirstFile() {
        }
        
        public long handle() {
            return this.handle;
        }
        
        public String name() {
            return this.name;
        }
        
        public int attributes() {
            return this.attributes;
        }
    }
    
    static class FirstStream
    {
        private long handle;
        private String name;
        
        private FirstStream() {
        }
        
        public long handle() {
            return this.handle;
        }
        
        public String name() {
            return this.name;
        }
    }
    
    static class VolumeInformation
    {
        private String fileSystemName;
        private String volumeName;
        private int volumeSerialNumber;
        private int flags;
        
        private VolumeInformation() {
        }
        
        public String fileSystemName() {
            return this.fileSystemName;
        }
        
        public String volumeName() {
            return this.volumeName;
        }
        
        public int volumeSerialNumber() {
            return this.volumeSerialNumber;
        }
        
        public int flags() {
            return this.flags;
        }
    }
    
    static class DiskFreeSpace
    {
        private long freeBytesAvailable;
        private long totalNumberOfBytes;
        private long totalNumberOfFreeBytes;
        
        private DiskFreeSpace() {
        }
        
        public long freeBytesAvailable() {
            return this.freeBytesAvailable;
        }
        
        public long totalNumberOfBytes() {
            return this.totalNumberOfBytes;
        }
        
        public long totalNumberOfFreeBytes() {
            return this.totalNumberOfFreeBytes;
        }
    }
    
    static class AclInformation
    {
        private int aceCount;
        
        private AclInformation() {
        }
        
        public int aceCount() {
            return this.aceCount;
        }
    }
    
    static class Account
    {
        private String domain;
        private String name;
        private int use;
        
        private Account() {
        }
        
        public String domain() {
            return this.domain;
        }
        
        public String name() {
            return this.name;
        }
        
        public int use() {
            return this.use;
        }
    }
    
    static class CompletionStatus
    {
        private int error;
        private int bytesTransferred;
        private long completionKey;
        
        private CompletionStatus() {
        }
        
        int error() {
            return this.error;
        }
        
        int bytesTransferred() {
            return this.bytesTransferred;
        }
        
        long completionKey() {
            return this.completionKey;
        }
    }
    
    static class BackupResult
    {
        private int bytesTransferred;
        private long context;
        
        private BackupResult() {
        }
        
        int bytesTransferred() {
            return this.bytesTransferred;
        }
        
        long context() {
            return this.context;
        }
    }
}
