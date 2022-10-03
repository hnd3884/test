package org.apache.commons.net.ftp;

public class FTPFileFilters
{
    public static final FTPFileFilter ALL;
    public static final FTPFileFilter NON_NULL;
    public static final FTPFileFilter DIRECTORIES;
    
    static {
        ALL = new FTPFileFilter() {
            @Override
            public boolean accept(final FTPFile file) {
                return true;
            }
        };
        NON_NULL = new FTPFileFilter() {
            @Override
            public boolean accept(final FTPFile file) {
                return file != null;
            }
        };
        DIRECTORIES = new FTPFileFilter() {
            @Override
            public boolean accept(final FTPFile file) {
                return file != null && file.isDirectory();
            }
        };
    }
}
