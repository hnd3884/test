package com.adventnet.tools.update.installer;

import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Vector;
import java.awt.Toolkit;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FilenameFilter;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public final class PureUtils
{
    private PureUtils() {
    }
    
    public static int atoi(final String val) throws NumberFormatException {
        int sign = 1;
        int i;
        for (i = 0; i < val.length() && Character.isSpaceChar(val.charAt(i)); ++i) {}
        if (i == val.length()) {
            throw new NumberFormatException(" No Digits ");
        }
        if (val.charAt(i) == '-' || val.charAt(i) == '+') {
            if (val.charAt(i) == '-') {
                sign = -1;
            }
            ++i;
        }
        int sum = 0;
        while (i < val.length() && Character.isDigit(val.charAt(i))) {
            sum = 10 * sum + (val.charAt(i) - '0');
            ++i;
        }
        while (i < val.length() && Character.isSpaceChar(val.charAt(i))) {
            ++i;
        }
        if (i == val.length()) {
            return sum * sign;
        }
        throw new NumberFormatException(" Space in Between ");
    }
    
    public static String replaceStringBySpecifiedString(final String origString, final String patternArg, final String insertStringArg) {
        return replaceStringBySpecifiedString(origString, patternArg, insertStringArg, 0);
    }
    
    public static String replaceStringBySpecifiedString(final String origString, final String patternArg, final String insertStringArg, final int fromArg) {
        int from = fromArg;
        int to = fromArg;
        final int step = patternArg.length();
        if (from < 0 || from > origString.length() - step || step == 0) {
            return origString;
        }
        final StringBuffer processedString = new StringBuffer(origString.length());
        if (from > 0) {
            processedString.append(origString.substring(0, from));
        }
        while (true) {
            to = origString.indexOf(patternArg, from);
            if (to < 0) {
                break;
            }
            processedString.append(origString.substring(from, to));
            processedString.append(insertStringArg);
            from = to + step;
        }
        processedString.append(origString.substring(from));
        return processedString.toString();
    }
    
    public static String getCorrectErrorMsg(final String msgArg, final Throwable throwableArg) {
        return getCorrectErrorMsg(msgArg, throwableArg, null);
    }
    
    public static String getCorrectErrorMsg(final String msgArg, Throwable throwableArg, final String fromPosArg) {
        if (throwableArg instanceof InvocationTargetException && ((InvocationTargetException)throwableArg).getTargetException() != null) {
            throwableArg = ((InvocationTargetException)throwableArg).getTargetException();
        }
        String completeMsg = '\n' + msgArg + "\nException Details :\n   Type : " + throwableArg.getClass().getName();
        if (throwableArg.getMessage() != null) {
            completeMsg = completeMsg + "\n   Message : " + throwableArg.getMessage();
        }
        if (fromPosArg != null) {
            final CustomOutputStream cusOut = new CustomOutputStream();
            final PrintWriter pw = new PrintWriter(cusOut);
            throwableArg.printStackTrace(pw);
            pw.flush();
            pw.close();
            String stackStr = cusOut.getString();
            int startIndex = 0;
            String prefix = throwableArg.getClass().getName() + ": ";
            if (throwableArg.getMessage() != null) {
                prefix += throwableArg.getMessage();
            }
            if (stackStr.startsWith(prefix)) {
                startIndex = prefix.length();
            }
            int index = stackStr.indexOf(fromPosArg);
            if (index > -1) {
                index = stackStr.indexOf(10, index);
                if (index < 0) {
                    index = stackStr.indexOf(fromPosArg);
                }
                if (startIndex >= index) {
                    startIndex = 0;
                }
                stackStr = stackStr.substring(startIndex, index);
            }
            completeMsg = completeMsg + "\n   Partial StackTrace : \n\t" + stackStr + '\n';
        }
        return completeMsg;
    }
    
    public static String getRelativePath(final String withRespectToArg, final String fileNameArg, final String attachIfRelativeArg) {
        final int end = (withRespectToArg.length() < fileNameArg.length()) ? withRespectToArg.length() : fileNameArg.length();
        int i;
        if (System.getProperty("os.name").startsWith("Window")) {
            for (i = 0; i < end; ++i) {
                if (Character.toLowerCase(withRespectToArg.charAt(i)) != Character.toLowerCase(fileNameArg.charAt(i))) {
                    break;
                }
            }
        }
        else {
            for (i = 0; i < end; ++i) {
                if (withRespectToArg.charAt(i) != fileNameArg.charAt(i)) {
                    break;
                }
            }
        }
        int numOfDotDotSep = 0;
        if (i < withRespectToArg.length()) {
            if (i != fileNameArg.length()) {
                ++numOfDotDotSep;
            }
            int j = i;
            while ((j = withRespectToArg.indexOf(File.separator, j) + 1) > 0) {
                ++numOfDotDotSep;
            }
        }
        final StringBuffer relativeStr = new StringBuffer();
        if (attachIfRelativeArg != null) {
            relativeStr.append(attachIfRelativeArg);
        }
        for (int k = 0; k < numOfDotDotSep; ++k) {
            relativeStr.append(File.separator + "..");
        }
        if (i < fileNameArg.length()) {
            int k = i;
            if (!fileNameArg.startsWith(File.separator, i)) {
                k = fileNameArg.lastIndexOf(File.separator, i);
            }
            else if (i < withRespectToArg.length()) {
                k = fileNameArg.lastIndexOf(File.separator, i - 1);
            }
            if (k < 0) {
                return fileNameArg;
            }
            relativeStr.append(fileNameArg.substring(k));
        }
        return relativeStr.toString();
    }
    
    public static String[] getInnerClasses(String classFileArg) {
        if (classFileArg == null) {
            return new String[0];
        }
        final int index = classFileArg.lastIndexOf(".class");
        if (index < 1) {
            return new String[0];
        }
        classFileArg = classFileArg.substring(0, index);
        final File classFile = new File(classFileArg);
        final String classDir = classFile.getParent();
        final String className = classFile.getName();
        return getInnerClasses(className, classDir, true);
    }
    
    public static String[] getInnerClasses(final String classNameArg, final String dirNameArg, final boolean fullPathArg) {
        if (classNameArg == null || dirNameArg == null) {
            return new String[0];
        }
        final File directory = new File(dirNameArg);
        if (!directory.exists() || !directory.isDirectory()) {
            return new String[0];
        }
        final String cls = classNameArg + '$';
        final FilenameFilter innerClassFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.startsWith(cls) && name.endsWith(".class");
            }
        };
        final String[] fileList = directory.list(innerClassFilter);
        if (fileList != null) {
            final String directoryPath = directory.getAbsolutePath();
            if (fullPathArg) {
                for (int i = 0; i < fileList.length; ++i) {
                    fileList[i] = directoryPath + File.separatorChar + fileList[i];
                }
            }
            return fileList;
        }
        return new String[0];
    }
    
    public static String getContentsAsString(final String fileNameArg) throws IOException {
        RandomAccessFile rda = null;
        try {
            rda = new RandomAccessFile(fileNameArg, "r");
            final byte[] bt = new byte[(int)rda.length()];
            rda.readFully(bt);
            return new String(bt);
        }
        finally {
            if (rda != null) {
                try {
                    rda.close();
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    public static void generateSrcFile(final String sourceFileNameArg, final String initialSourceArg, final boolean createParentDirsArg) throws ProcessedException {
        if (createParentDirsArg) {
            final File file = new File(new File(sourceFileNameArg).getParent());
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        generateSrcFile(sourceFileNameArg, initialSourceArg);
    }
    
    public static void generateSrcFile(final String sourceFileNameArg, final String initialSourceArg) throws ProcessedException {
        PrintWriter out = null;
        final boolean returnValue = true;
        try {
            final FileWriter fout = new FileWriter(sourceFileNameArg);
            out = new PrintWriter(fout);
            out.println(initialSourceArg);
            out.close();
        }
        catch (final Exception ex) {
            throw new ProcessedException("Couldn't create file " + sourceFileNameArg, ex);
        }
    }
    
    public static void generateBeep() {
        Toolkit.getDefaultToolkit().beep();
    }
    
    public static String getUniqueName(final String screenPrefixArg, final Vector childVec) {
        final Enumeration enum1 = childVec.elements();
        int j = 0;
        int k = 0;
        int i = 0;
        while (enum1.hasMoreElements()) {
            k = getLastNumeral(enum1.nextElement().toString(), screenPrefixArg);
            if (j < k) {
                j = k;
            }
            ++i;
        }
        ++j;
        return screenPrefixArg + j;
    }
    
    public static String generateUniqueName(final String screenPrefixArg, final Vector childVec) {
        final Enumeration enum1 = childVec.elements();
        int i = 0;
        while (enum1.hasMoreElements()) {
            if (enum1.nextElement().toString().equals(screenPrefixArg)) {
                return getUniqueName(screenPrefixArg, childVec);
            }
            ++i;
        }
        return screenPrefixArg;
    }
    
    private static int getLastNumeral(final String in, final String screenPrefixArg) {
        if (in.startsWith(screenPrefixArg)) {
            final String str = in.substring(screenPrefixArg.length());
            try {
                final int i = Integer.valueOf(str);
                return i;
            }
            catch (final Exception ex) {
                return 0;
            }
        }
        return 0;
    }
    
    public static String getUniqueNewFileName(final String dirNameArg, final String filePrefixArg, final String fileSuffixArg) {
        final File directory = new File(dirNameArg);
        final String[] fileArr = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String fileNameArg) {
                return fileNameArg.startsWith(filePrefixArg) && fileNameArg.endsWith(fileSuffixArg);
            }
        });
        int num = 1;
        boolean present;
        String newFileName;
        do {
            present = false;
            newFileName = filePrefixArg + num++ + fileSuffixArg;
            for (int i = 0; i < fileArr.length; ++i) {
                if (newFileName.equals(fileArr[i])) {
                    present = true;
                    break;
                }
            }
        } while (present);
        return newFileName;
    }
    
    public static void copyFile(final String sourceFileArg, final String destinationFileArg, final boolean createParentDirsArg) throws FileNotFoundException, IOException {
        if (createParentDirsArg) {
            final File parent = new File(destinationFileArg).getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IOException(" Unable to create parent directories for " + destinationFileArg);
            }
        }
        copyFile(sourceFileArg, destinationFileArg);
    }
    
    public static void copyFile(String sourceFileArg, String destinationFileArg) throws FileNotFoundException, IOException {
        RandomAccessFile sourceRAF = null;
        RandomAccessFile destinationRAF = null;
        final File destinationFile = new File(destinationFileArg);
        try {
            try {
                sourceFileArg = new File(sourceFileArg).getCanonicalPath();
                destinationFileArg = destinationFile.getCanonicalPath();
            }
            catch (final IOException ioe) {
                sourceFileArg = new File(sourceFileArg).getAbsolutePath();
                destinationFileArg = destinationFile.getAbsolutePath();
            }
            if (sourceFileArg.equals(destinationFileArg)) {
                return;
            }
            if (destinationFile.exists()) {
                destinationFile.delete();
            }
            sourceRAF = new RandomAccessFile(sourceFileArg, "r");
            destinationRAF = new RandomAccessFile(destinationFileArg, "rw");
            final byte[] byteArr = new byte[(int)sourceRAF.length()];
            sourceRAF.readFully(byteArr);
            destinationRAF.write(byteArr);
        }
        finally {
            try {
                if (sourceRAF != null) {
                    sourceRAF.close();
                }
                if (destinationRAF != null) {
                    destinationRAF.close();
                }
            }
            catch (final IOException ex) {}
        }
    }
    
    public static String moveFile(final String fileName, final String moveToFileName) {
        final File file = new File(fileName);
        final File moveToFile = new File(moveToFileName);
        String message = null;
        if (!file.exists()) {
            return null;
        }
        if (moveToFile.exists()) {
            if (!moveToFile.delete()) {
                message = "Error: Unable to delete the file " + moveToFile.getAbsolutePath();
            }
        }
        else {
            final String parent = moveToFile.getParent();
            if (parent != null) {
                final File parentFile = new File(parent);
                if (!parentFile.isDirectory() && !parentFile.mkdirs()) {
                    message = "Error: Unable to create directory " + parentFile.getAbsolutePath();
                }
            }
        }
        if (!file.renameTo(moveToFile)) {
            message = ((message != null) ? (message + "\n") : "") + "Error: Unable to move file " + file.getAbsolutePath() + " to " + moveToFile.getAbsolutePath();
        }
        return message;
    }
    
    public static boolean isWindowsPlatform() {
        final String os = System.getProperty("os.name");
        return os != null && os.startsWith("Windows");
    }
    
    public static String makeUpper(final String nameArg) {
        final char[] value = nameArg.toCharArray();
        value[0] = Character.toUpperCase(value[0]);
        return new String(value);
    }
}
