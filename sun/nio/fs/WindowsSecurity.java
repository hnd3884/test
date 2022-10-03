package sun.nio.fs;

class WindowsSecurity
{
    static final long processTokenWithDuplicateAccess;
    static final long processTokenWithQueryAccess;
    
    private WindowsSecurity() {
    }
    
    private static long openProcessToken(final int n) {
        try {
            return WindowsNativeDispatcher.OpenProcessToken(WindowsNativeDispatcher.GetCurrentProcess(), n);
        }
        catch (final WindowsException ex) {
            return 0L;
        }
    }
    
    static Privilege enablePrivilege(final String s) {
        long lookupPrivilegeValue;
        try {
            lookupPrivilegeValue = WindowsNativeDispatcher.LookupPrivilegeValue(s);
        }
        catch (final WindowsException ex) {
            throw new AssertionError((Object)ex);
        }
        long n = 0L;
        try {
            n = WindowsNativeDispatcher.OpenThreadToken(WindowsNativeDispatcher.GetCurrentThread(), 32, false);
            if (n == 0L && WindowsSecurity.processTokenWithDuplicateAccess != 0L) {
                n = WindowsNativeDispatcher.DuplicateTokenEx(WindowsSecurity.processTokenWithDuplicateAccess, 36);
                WindowsNativeDispatcher.SetThreadToken(0L, n);
            }
            if (n != 0L) {
                WindowsNativeDispatcher.AdjustTokenPrivileges(n, lookupPrivilegeValue, 2);
            }
        }
        catch (final WindowsException ex2) {}
        return () -> {
            try {
                if (n2 != 0L) {
                    try {
                        if (b) {
                            WindowsNativeDispatcher.SetThreadToken(0L, 0L);
                        }
                        else if (b2) {
                            WindowsNativeDispatcher.AdjustTokenPrivileges(n2, n3, 0);
                        }
                    }
                    catch (final WindowsException ex3) {
                        throw new AssertionError((Object)ex3);
                    }
                    finally {
                        WindowsNativeDispatcher.CloseHandle(n2);
                    }
                }
            }
            finally {
                WindowsNativeDispatcher.LocalFree(n3);
            }
        };
    }
    
    static boolean checkAccessMask(final long n, final int n2, final int n3, final int n4, final int n5, final int n6) throws WindowsException {
        final int n7 = 8;
        long n8 = WindowsNativeDispatcher.OpenThreadToken(WindowsNativeDispatcher.GetCurrentThread(), n7, false);
        if (n8 == 0L && WindowsSecurity.processTokenWithDuplicateAccess != 0L) {
            n8 = WindowsNativeDispatcher.DuplicateTokenEx(WindowsSecurity.processTokenWithDuplicateAccess, n7);
        }
        boolean accessCheck = false;
        if (n8 != 0L) {
            try {
                accessCheck = WindowsNativeDispatcher.AccessCheck(n8, n, n2, n3, n4, n5, n6);
            }
            finally {
                WindowsNativeDispatcher.CloseHandle(n8);
            }
        }
        return accessCheck;
    }
    
    static {
        processTokenWithDuplicateAccess = openProcessToken(2);
        processTokenWithQueryAccess = openProcessToken(8);
    }
    
    interface Privilege
    {
        void drop();
    }
}
