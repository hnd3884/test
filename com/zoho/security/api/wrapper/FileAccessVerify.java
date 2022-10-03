package com.zoho.security.api.wrapper;

import java.io.IOException;
import java.io.File;
import com.adventnet.iam.security.SecurityUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class FileAccessVerify
{
    private final Pattern allowDirectoriesPattern;
    private final Pattern blockDirectoriesPattern;
    private static String home;
    private static String temp;
    private static String default_allow_directories;
    private static String default_block_directories;
    private static final Logger LOGGER;
    
    public FileAccessVerify() {
        this.allowDirectoriesPattern = Pattern.compile(escapeBackslash(FileAccessVerify.default_allow_directories));
        this.blockDirectoriesPattern = Pattern.compile(escapeBackslash(FileAccessVerify.default_block_directories));
        FileAccessVerify.LOGGER.log(Level.INFO, "Loading default allow: {0} and block: {1} directory configurations", new String[] { FileAccessVerify.default_allow_directories, FileAccessVerify.default_block_directories });
    }
    
    public FileAccessVerify(final String allowDirectories, final String blockDirectories) {
        if (SecurityUtil.isValid(allowDirectories)) {
            this.allowDirectoriesPattern = Pattern.compile(escapeBackslash(allowDirectories));
        }
        else {
            FileAccessVerify.LOGGER.log(Level.WARNING, "Loading default allow: {0} directory configurations", FileAccessVerify.default_allow_directories);
            this.allowDirectoriesPattern = Pattern.compile(escapeBackslash(FileAccessVerify.default_allow_directories));
        }
        if (SecurityUtil.isValid(blockDirectories)) {
            this.blockDirectoriesPattern = Pattern.compile(escapeBackslash(blockDirectories));
        }
        else {
            FileAccessVerify.LOGGER.log(Level.WARNING, "Loading default block: {0} direcotry configurations", FileAccessVerify.default_block_directories);
            this.blockDirectoriesPattern = Pattern.compile(escapeBackslash(FileAccessVerify.default_block_directories));
        }
    }
    
    public static String escapeBackslash(final String path) {
        if (!path.contains("\\")) {
            return path;
        }
        final StringBuilder builder = new StringBuilder();
        final char[] charArray = path.toCharArray();
        boolean escapeBackslash = false;
        for (int i = 0; i < charArray.length; ++i) {
            final char currentChar = charArray[i];
            if (escapeBackslash) {
                if (currentChar != '\\') {
                    builder.append('\\');
                }
                escapeBackslash = false;
            }
            else if (currentChar == '\\') {
                escapeBackslash = true;
            }
            builder.append(currentChar);
        }
        if (escapeBackslash) {
            builder.append("\\");
        }
        return builder.toString();
    }
    
    public String getAccessVerifiedPath(final String filePath) throws IOException {
        return this.getAccessVerifiedPath(new File(filePath));
    }
    
    public String getAccessVerifiedPath(final File file) throws IOException {
        final String path = file.getCanonicalPath();
        final String fileReadingDirectory = new File(path).getParent();
        if (!fileReadingDirectory.startsWith(FileAccessVerify.temp) && !fileReadingDirectory.startsWith(FileAccessVerify.home)) {
            throw new FileAccessDeniedException("Given path : " + path + " is outside of home and temporary directory");
        }
        if ((SecurityUtil.isValid(this.blockDirectoriesPattern) && SecurityUtil.matchPattern(fileReadingDirectory, this.blockDirectoriesPattern)) || !SecurityUtil.isValid(this.allowDirectoriesPattern) || !SecurityUtil.matchPattern(fileReadingDirectory, this.allowDirectoriesPattern)) {
            throw new FileAccessDeniedException("Given path : " + path + " not in the listed whitelist directories or in the listed blacklist directories");
        }
        return path;
    }
    
    public Pattern getAllowRegexPatternDirectory() {
        return this.allowDirectoriesPattern;
    }
    
    public Pattern getBlockRegexPatternDirectory() {
        return this.blockDirectoriesPattern;
    }
    
    static {
        LOGGER = Logger.getLogger(FileAccessVerify.class.getName());
        try {
            FileAccessVerify.home = new File(System.getProperty("user.home")).getCanonicalPath();
            FileAccessVerify.temp = new File(System.getProperty("java.io.tmpdir")).getCanonicalPath();
            FileAccessVerify.default_allow_directories = FileAccessVerify.home + "|" + FileAccessVerify.temp;
            FileAccessVerify.default_block_directories = new File(File.separator).getCanonicalPath();
        }
        catch (final IOException e) {
            FileAccessVerify.LOGGER.log(Level.SEVERE, "IOException occurred while loading default allow and disallow directories. ErrorMsg: {0}", new String[] { e.getMessage() });
        }
    }
}
