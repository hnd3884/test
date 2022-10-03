package org.apache.xmlbeans.impl.jam.internal;

import java.util.StringTokenizer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;
import java.io.File;

public class DirectoryScanner
{
    private boolean mCaseSensitive;
    private File mRoot;
    private JamLogger mLogger;
    private List mIncludeList;
    private List mExcludeList;
    private String[] mIncludes;
    private String[] mExcludes;
    private Vector mFilesIncluded;
    private Vector mDirsIncluded;
    private boolean mIsDirty;
    private String[] mIncludedFilesCache;
    
    public DirectoryScanner(final File dirToScan, final JamLogger logger) {
        this.mCaseSensitive = true;
        this.mIncludeList = null;
        this.mExcludeList = null;
        this.mIsDirty = false;
        this.mIncludedFilesCache = null;
        if (logger == null) {
            throw new IllegalArgumentException("null logger");
        }
        this.mLogger = logger;
        this.mRoot = dirToScan;
    }
    
    public void include(final String pattern) {
        if (this.mIncludeList == null) {
            this.mIncludeList = new ArrayList();
        }
        this.mIncludeList.add(pattern);
        this.mIsDirty = true;
    }
    
    public void exclude(final String pattern) {
        if (this.mExcludeList == null) {
            this.mExcludeList = new ArrayList();
        }
        this.mExcludeList.add(pattern);
        this.mIsDirty = true;
    }
    
    public String[] getIncludedFiles() throws IOException {
        if (!this.mIsDirty && this.mIncludedFilesCache != null) {
            return this.mIncludedFilesCache;
        }
        if (this.mIncludeList != null) {
            final String[] inc = new String[this.mIncludeList.size()];
            this.mIncludeList.toArray(inc);
            this.setIncludes(inc);
        }
        else {
            this.setIncludes(null);
        }
        if (this.mExcludeList != null) {
            final String[] exc = new String[this.mExcludeList.size()];
            this.mExcludeList.toArray(exc);
            this.setExcludes(exc);
        }
        else {
            this.setExcludes(null);
        }
        this.scan();
        this.mIncludedFilesCache = new String[this.mFilesIncluded.size()];
        this.mFilesIncluded.copyInto(this.mIncludedFilesCache);
        return this.mIncludedFilesCache;
    }
    
    public void setDirty() {
        this.mIsDirty = true;
    }
    
    public File getRoot() {
        return this.mRoot;
    }
    
    private void setIncludes(final String[] includes) {
        if (includes == null) {
            this.mIncludes = null;
        }
        else {
            this.mIncludes = new String[includes.length];
            for (int i = 0; i < includes.length; ++i) {
                String pattern = includes[i].replace('/', File.separatorChar).replace('\\', File.separatorChar);
                if (pattern.endsWith(File.separator)) {
                    pattern += "**";
                }
                this.mIncludes[i] = pattern;
            }
        }
    }
    
    private void setExcludes(final String[] excludes) {
        if (excludes == null) {
            this.mExcludes = null;
        }
        else {
            this.mExcludes = new String[excludes.length];
            for (int i = 0; i < excludes.length; ++i) {
                String pattern = excludes[i].replace('/', File.separatorChar).replace('\\', File.separatorChar);
                if (pattern.endsWith(File.separator)) {
                    pattern += "**";
                }
                this.mExcludes[i] = pattern;
            }
        }
    }
    
    private void scan() throws IllegalStateException, IOException {
        if (this.mIncludes == null) {
            (this.mIncludes = new String[1])[0] = "**";
        }
        if (this.mExcludes == null) {
            this.mExcludes = new String[0];
        }
        this.mFilesIncluded = new Vector();
        this.mDirsIncluded = new Vector();
        if (this.isIncluded("") && !this.isExcluded("")) {
            this.mDirsIncluded.addElement("");
        }
        this.scandir(this.mRoot, "", true);
    }
    
    private void scandir(final File dir, final String vpath, final boolean fast) throws IOException {
        if (this.mLogger.isVerbose(this)) {
            this.mLogger.verbose("[DirectoryScanner] scanning dir " + dir + " for '" + vpath + "'");
        }
        final String[] newfiles = dir.list();
        if (newfiles == null) {
            throw new IOException("IO error scanning directory " + dir.getAbsolutePath());
        }
        for (int i = 0; i < newfiles.length; ++i) {
            final String name = vpath + newfiles[i];
            final File file = new File(dir, newfiles[i]);
            if (file.isDirectory()) {
                if (this.isIncluded(name) && !this.isExcluded(name)) {
                    this.mDirsIncluded.addElement(name);
                    if (this.mLogger.isVerbose(this)) {
                        this.mLogger.verbose("...including dir " + name);
                    }
                    this.scandir(file, name + File.separator, fast);
                }
                else if (this.couldHoldIncluded(name)) {
                    this.scandir(file, name + File.separator, fast);
                }
            }
            else if (file.isFile() && this.isIncluded(name)) {
                if (!this.isExcluded(name)) {
                    this.mFilesIncluded.addElement(name);
                    if (this.mLogger.isVerbose(this)) {
                        this.mLogger.verbose("...including " + name + " under '" + dir);
                    }
                }
                else if (this.mLogger.isVerbose(this)) {
                    this.mLogger.verbose("...EXCLUDING " + name + " under '" + dir);
                }
            }
        }
    }
    
    private boolean isIncluded(final String name) {
        for (int i = 0; i < this.mIncludes.length; ++i) {
            if (matchPath(this.mIncludes[i], name, this.mCaseSensitive)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean couldHoldIncluded(final String name) {
        for (int i = 0; i < this.mIncludes.length; ++i) {
            if (matchPatternStart(this.mIncludes[i], name, this.mCaseSensitive)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isExcluded(final String name) {
        for (int i = 0; i < this.mExcludes.length; ++i) {
            if (matchPath(this.mExcludes[i], name, this.mCaseSensitive)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean matchPatternStart(final String pattern, final String str, final boolean mCaseSensitive) {
        if (str.startsWith(File.separator) != pattern.startsWith(File.separator)) {
            return false;
        }
        final Vector patDirs = tokenizePath(pattern);
        final Vector strDirs = tokenizePath(str);
        int patIdxStart;
        int patIdxEnd;
        int strIdxStart;
        int strIdxEnd;
        for (patIdxStart = 0, patIdxEnd = patDirs.size() - 1, strIdxStart = 0, strIdxEnd = strDirs.size() - 1; patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd; ++patIdxStart, ++strIdxStart) {
            final String patDir = patDirs.elementAt(patIdxStart);
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir, strDirs.elementAt(strIdxStart), mCaseSensitive)) {
                return false;
            }
        }
        return strIdxStart > strIdxEnd || patIdxStart <= patIdxEnd;
    }
    
    private static boolean matchPath(final String pattern, final String str, final boolean mCaseSensitive) {
        if (str.startsWith(File.separator) != pattern.startsWith(File.separator)) {
            return false;
        }
        final Vector patDirs = tokenizePath(pattern);
        final Vector strDirs = tokenizePath(str);
        int patIdxStart;
        int patIdxEnd;
        int strIdxStart;
        int strIdxEnd;
        for (patIdxStart = 0, patIdxEnd = patDirs.size() - 1, strIdxStart = 0, strIdxEnd = strDirs.size() - 1; patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd; ++patIdxStart, ++strIdxStart) {
            final String patDir = patDirs.elementAt(patIdxStart);
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir, strDirs.elementAt(strIdxStart), mCaseSensitive)) {
                return false;
            }
        }
        if (strIdxStart > strIdxEnd) {
            for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                if (!patDirs.elementAt(i).equals("**")) {
                    return false;
                }
            }
            return true;
        }
        if (patIdxStart > patIdxEnd) {
            return false;
        }
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            final String patDir = patDirs.elementAt(patIdxEnd);
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir, strDirs.elementAt(strIdxEnd), mCaseSensitive)) {
                return false;
            }
            --patIdxEnd;
            --strIdxEnd;
        }
        if (strIdxStart > strIdxEnd) {
            for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                if (!patDirs.elementAt(i).equals("**")) {
                    return false;
                }
            }
            return true;
        }
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int j = patIdxStart + 1; j <= patIdxEnd; ++j) {
                if (patDirs.elementAt(j).equals("**")) {
                    patIdxTmp = j;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                ++patIdxStart;
            }
            else {
                final int patLength = patIdxTmp - patIdxStart - 1;
                final int strLength = strIdxEnd - strIdxStart + 1;
                int foundIdx = -1;
                int k = 0;
            Label_0378:
                while (k <= strLength - patLength) {
                    for (int l = 0; l < patLength; ++l) {
                        final String subPat = patDirs.elementAt(patIdxStart + l + 1);
                        final String subStr = strDirs.elementAt(strIdxStart + k + l);
                        if (!match(subPat, subStr, mCaseSensitive)) {
                            ++k;
                            continue Label_0378;
                        }
                    }
                    foundIdx = strIdxStart + k;
                    break;
                }
                if (foundIdx == -1) {
                    return false;
                }
                patIdxStart = patIdxTmp;
                strIdxStart = foundIdx + patLength;
            }
        }
        for (int i = patIdxStart; i <= patIdxEnd; ++i) {
            if (!patDirs.elementAt(i).equals("**")) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean match(final String pattern, final String str, final boolean mCaseSensitive) {
        final char[] patArr = pattern.toCharArray();
        final char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        boolean containsStar = false;
        for (int i = 0; i < patArr.length; ++i) {
            if (patArr[i] == '*') {
                containsStar = true;
                break;
            }
        }
        if (!containsStar) {
            if (patIdxEnd != strIdxEnd) {
                return false;
            }
            for (int i = 0; i <= patIdxEnd; ++i) {
                final char ch = patArr[i];
                if (ch != '?') {
                    if (mCaseSensitive && ch != strArr[i]) {
                        return false;
                    }
                    if (!mCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
        else {
            if (patIdxEnd == 0) {
                return true;
            }
            char ch;
            while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
                if (ch != '?') {
                    if (mCaseSensitive && ch != strArr[strIdxStart]) {
                        return false;
                    }
                    if (!mCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart])) {
                        return false;
                    }
                }
                ++patIdxStart;
                ++strIdxStart;
            }
            if (strIdxStart > strIdxEnd) {
                for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                    if (patArr[i] != '*') {
                        return false;
                    }
                }
                return true;
            }
            while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
                if (ch != '?') {
                    if (mCaseSensitive && ch != strArr[strIdxEnd]) {
                        return false;
                    }
                    if (!mCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxEnd])) {
                        return false;
                    }
                }
                --patIdxEnd;
                --strIdxEnd;
            }
            if (strIdxStart > strIdxEnd) {
                for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                    if (patArr[i] != '*') {
                        return false;
                    }
                }
                return true;
            }
            while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
                int patIdxTmp = -1;
                for (int j = patIdxStart + 1; j <= patIdxEnd; ++j) {
                    if (patArr[j] == '*') {
                        patIdxTmp = j;
                        break;
                    }
                }
                if (patIdxTmp == patIdxStart + 1) {
                    ++patIdxStart;
                }
                else {
                    final int patLength = patIdxTmp - patIdxStart - 1;
                    final int strLength = strIdxEnd - strIdxStart + 1;
                    int foundIdx = -1;
                    int k = 0;
                Label_0465:
                    while (k <= strLength - patLength) {
                        for (int l = 0; l < patLength; ++l) {
                            ch = patArr[patIdxStart + l + 1];
                            if (ch != '?') {
                                if (!mCaseSensitive || ch == strArr[strIdxStart + k + l]) {
                                    if (mCaseSensitive || Character.toUpperCase(ch) == Character.toUpperCase(strArr[strIdxStart + k + l])) {
                                        continue;
                                    }
                                }
                                ++k;
                                continue Label_0465;
                            }
                        }
                        foundIdx = strIdxStart + k;
                        break;
                    }
                    if (foundIdx == -1) {
                        return false;
                    }
                    patIdxStart = patIdxTmp;
                    strIdxStart = foundIdx + patLength;
                }
            }
            for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] != '*') {
                    return false;
                }
            }
            return true;
        }
    }
    
    private static Vector tokenizePath(final String path) {
        final Vector ret = new Vector();
        final StringTokenizer st = new StringTokenizer(path, File.separator);
        while (st.hasMoreTokens()) {
            ret.addElement(st.nextToken());
        }
        return ret;
    }
}
