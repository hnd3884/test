package org.eclipse.jdt.internal.compiler.util;

import org.eclipse.jdt.internal.compiler.batch.Main;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import org.eclipse.jdt.internal.compiler.ClassFile;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.File;
import org.eclipse.jdt.core.compiler.CharOperation;

public class Util implements SuffixConstants
{
    public static final char C_BOOLEAN = 'Z';
    public static final char C_BYTE = 'B';
    public static final char C_CHAR = 'C';
    public static final char C_DOUBLE = 'D';
    public static final char C_FLOAT = 'F';
    public static final char C_INT = 'I';
    public static final char C_SEMICOLON = ';';
    public static final char C_COLON = ':';
    public static final char C_LONG = 'J';
    public static final char C_SHORT = 'S';
    public static final char C_VOID = 'V';
    public static final char C_TYPE_VARIABLE = 'T';
    public static final char C_STAR = '*';
    public static final char C_EXCEPTION_START = '^';
    public static final char C_EXTENDS = '+';
    public static final char C_SUPER = '-';
    public static final char C_DOT = '.';
    public static final char C_DOLLAR = '$';
    public static final char C_ARRAY = '[';
    public static final char C_RESOLVED = 'L';
    public static final char C_UNRESOLVED = 'Q';
    public static final char C_NAME_END = ';';
    public static final char C_PARAM_START = '(';
    public static final char C_PARAM_END = ')';
    public static final char C_GENERIC_START = '<';
    public static final char C_GENERIC_END = '>';
    public static final char C_CAPTURE = '!';
    private static final int DEFAULT_READING_SIZE = 8192;
    private static final int DEFAULT_WRITING_SIZE = 1024;
    public static final String UTF_8 = "UTF-8";
    public static final String LINE_SEPARATOR;
    public static final String EMPTY_STRING;
    public static final int[] EMPTY_INT_ARRAY;
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
        EMPTY_STRING = new String(CharOperation.NO_CHAR);
        EMPTY_INT_ARRAY = new int[0];
    }
    
    public static String buildAllDirectoriesInto(String outputPath, String relativeFileName) throws IOException {
        final char fileSeparatorChar = File.separatorChar;
        final String fileSeparator = File.separator;
        outputPath = outputPath.replace('/', fileSeparatorChar);
        relativeFileName = relativeFileName.replace('/', fileSeparatorChar);
        final int separatorIndex = relativeFileName.lastIndexOf(fileSeparatorChar);
        String outputDirPath;
        String fileName;
        if (separatorIndex == -1) {
            if (outputPath.endsWith(fileSeparator)) {
                outputDirPath = outputPath.substring(0, outputPath.length() - 1);
                fileName = String.valueOf(outputPath) + relativeFileName;
            }
            else {
                outputDirPath = outputPath;
                fileName = String.valueOf(outputPath) + fileSeparator + relativeFileName;
            }
        }
        else if (outputPath.endsWith(fileSeparator)) {
            outputDirPath = String.valueOf(outputPath) + relativeFileName.substring(0, separatorIndex);
            fileName = String.valueOf(outputPath) + relativeFileName;
        }
        else {
            outputDirPath = String.valueOf(outputPath) + fileSeparator + relativeFileName.substring(0, separatorIndex);
            fileName = String.valueOf(outputPath) + fileSeparator + relativeFileName;
        }
        File f = new File(outputDirPath);
        f.mkdirs();
        if (f.isDirectory()) {
            return fileName;
        }
        if (outputPath.endsWith(fileSeparator)) {
            outputPath = outputPath.substring(0, outputPath.length() - 1);
        }
        f = new File(outputPath);
        boolean checkFileType = false;
        if (f.exists()) {
            checkFileType = true;
        }
        else if (!f.mkdirs()) {
            if (!f.exists()) {
                throw new IOException(Messages.bind(Messages.output_notValidAll, f.getAbsolutePath()));
            }
            checkFileType = true;
        }
        if (checkFileType && !f.isDirectory()) {
            throw new IOException(Messages.bind(Messages.output_isFile, f.getAbsolutePath()));
        }
        final StringBuffer outDir = new StringBuffer(outputPath);
        outDir.append(fileSeparator);
        final StringTokenizer tokenizer = new StringTokenizer(relativeFileName, fileSeparator);
        String token = tokenizer.nextToken();
        while (tokenizer.hasMoreTokens()) {
            f = new File(outDir.append(token).append(fileSeparator).toString());
            checkFileType = false;
            if (f.exists()) {
                checkFileType = true;
            }
            else if (!f.mkdir()) {
                if (!f.exists()) {
                    throw new IOException(Messages.bind(Messages.output_notValid, outDir.substring(outputPath.length() + 1, outDir.length() - 1), outputPath));
                }
                checkFileType = true;
            }
            if (checkFileType && !f.isDirectory()) {
                throw new IOException(Messages.bind(Messages.output_isFile, f.getAbsolutePath()));
            }
            token = tokenizer.nextToken();
        }
        return outDir.append(token).toString();
    }
    
    public static char[] bytesToChar(final byte[] bytes, final String encoding) throws IOException {
        return getInputStreamAsCharArray(new ByteArrayInputStream(bytes), bytes.length, encoding);
    }
    
    public static int computeOuterMostVisibility(TypeDeclaration typeDeclaration, int visibility) {
        while (typeDeclaration != null) {
            switch (typeDeclaration.modifiers & 0x7) {
                case 2: {
                    visibility = 2;
                    break;
                }
                case 0: {
                    if (visibility != 2) {
                        visibility = 0;
                        break;
                    }
                    break;
                }
                case 4: {
                    if (visibility == 1) {
                        visibility = 4;
                        break;
                    }
                    break;
                }
            }
            typeDeclaration = typeDeclaration.enclosingType;
        }
        return visibility;
    }
    
    public static byte[] getFileByteContent(final File file) throws IOException {
        InputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
            return getInputStreamAsByteArray(stream, (int)file.length());
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    public static char[] getFileCharContent(final File file, final String encoding) throws IOException {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return getInputStreamAsCharArray(stream, (int)file.length(), encoding);
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    private static FileOutputStream getFileOutputStream(final boolean generatePackagesStructure, String outputPath, final String relativeFileName) throws IOException {
        if (generatePackagesStructure) {
            return new FileOutputStream(new File(buildAllDirectoriesInto(outputPath, relativeFileName)));
        }
        String fileName = null;
        final char fileSeparatorChar = File.separatorChar;
        final String fileSeparator = File.separator;
        outputPath = outputPath.replace('/', fileSeparatorChar);
        final int indexOfPackageSeparator = relativeFileName.lastIndexOf(fileSeparatorChar);
        if (indexOfPackageSeparator == -1) {
            if (outputPath.endsWith(fileSeparator)) {
                fileName = String.valueOf(outputPath) + relativeFileName;
            }
            else {
                fileName = String.valueOf(outputPath) + fileSeparator + relativeFileName;
            }
        }
        else {
            final int length = relativeFileName.length();
            if (outputPath.endsWith(fileSeparator)) {
                fileName = String.valueOf(outputPath) + relativeFileName.substring(indexOfPackageSeparator + 1, length);
            }
            else {
                fileName = String.valueOf(outputPath) + fileSeparator + relativeFileName.substring(indexOfPackageSeparator + 1, length);
            }
        }
        return new FileOutputStream(new File(fileName));
    }
    
    public static byte[] getInputStreamAsByteArray(final InputStream stream, final int length) throws IOException {
        byte[] contents;
        if (length == -1) {
            contents = new byte[0];
            int contentsLength = 0;
            int amountRead = -1;
            do {
                final int amountRequested = Math.max(stream.available(), 8192);
                if (contentsLength + amountRequested > contents.length) {
                    System.arraycopy(contents, 0, contents = new byte[contentsLength + amountRequested], 0, contentsLength);
                }
                amountRead = stream.read(contents, contentsLength, amountRequested);
                if (amountRead > 0) {
                    contentsLength += amountRead;
                }
            } while (amountRead != -1);
            if (contentsLength < contents.length) {
                System.arraycopy(contents, 0, contents = new byte[contentsLength], 0, contentsLength);
            }
        }
        else {
            contents = new byte[length];
            for (int len = 0, readSize = 0; readSize != -1 && len != length; len += readSize, readSize = stream.read(contents, len, length - len)) {}
        }
        return contents;
    }
    
    public static char[] getInputStreamAsCharArray(final InputStream stream, final int length, final String encoding) throws IOException {
        BufferedReader reader = null;
        try {
            BufferedReader bufferedReader;
            if (encoding == null) {
                final InputStreamReader inputStreamReader;
                bufferedReader = new BufferedReader(inputStreamReader);
                inputStreamReader = new InputStreamReader(stream);
            }
            else {
                final InputStreamReader inputStreamReader2;
                bufferedReader = new BufferedReader(inputStreamReader2);
                inputStreamReader2 = new InputStreamReader(stream, encoding);
            }
            reader = bufferedReader;
        }
        catch (final UnsupportedEncodingException ex) {
            reader = new BufferedReader(new InputStreamReader(stream));
        }
        int totalRead = 0;
        char[] contents;
        if (length == -1) {
            contents = CharOperation.NO_CHAR;
        }
        else {
            contents = new char[length];
        }
        while (true) {
            int amountRequested;
            if (totalRead < length) {
                amountRequested = length - totalRead;
            }
            else {
                final int current = reader.read();
                if (current < 0) {
                    break;
                }
                amountRequested = Math.max(stream.available(), 8192);
                if (totalRead + 1 + amountRequested > contents.length) {
                    System.arraycopy(contents, 0, contents = new char[totalRead + 1 + amountRequested], 0, totalRead);
                }
                contents[totalRead++] = (char)current;
            }
            final int amountRead = reader.read(contents, totalRead, amountRequested);
            if (amountRead < 0) {
                break;
            }
            totalRead += amountRead;
        }
        int start = 0;
        if (totalRead > 0 && "UTF-8".equals(encoding) && contents[0] == '\ufeff') {
            --totalRead;
            start = 1;
        }
        if (totalRead < contents.length) {
            System.arraycopy(contents, start, contents = new char[totalRead], 0, totalRead);
        }
        return contents;
    }
    
    public static String getExceptionSummary(final Throwable exception) {
        final StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        final StringBuffer buffer = stringWriter.getBuffer();
        final StringBuffer exceptionBuffer = new StringBuffer(50);
        exceptionBuffer.append(exception.toString());
        int i = 0;
        int lineSep = 0;
        final int max = buffer.length();
        int line2Start = 0;
    Label_0169:
        while (i < max) {
            switch (buffer.charAt(i)) {
                case '\n':
                case '\r': {
                    if (line2Start > 0) {
                        exceptionBuffer.append(' ').append(buffer.substring(line2Start, i));
                        break Label_0169;
                    }
                    ++lineSep;
                    break;
                }
                case '\t':
                case ' ': {
                    break;
                }
                default: {
                    if (lineSep > 0) {
                        line2Start = i;
                        lineSep = 0;
                        break;
                    }
                    break;
                }
            }
            ++i;
        }
        return exceptionBuffer.toString();
    }
    
    public static int getLineNumber(final int position, final int[] lineEnds, int g, int d) {
        if (lineEnds == null) {
            return 1;
        }
        if (d == -1) {
            return 1;
        }
        int m = g;
        while (g <= d) {
            m = g + (d - g) / 2;
            final int start;
            if (position < (start = lineEnds[m])) {
                d = m - 1;
            }
            else {
                if (position <= start) {
                    return m + 1;
                }
                g = m + 1;
            }
        }
        if (position < lineEnds[m]) {
            return m + 1;
        }
        return m + 2;
    }
    
    public static byte[] getZipEntryByteContent(final ZipEntry ze, final ZipFile zip) throws IOException {
        InputStream stream = null;
        try {
            final InputStream inputStream = zip.getInputStream(ze);
            if (inputStream == null) {
                throw new IOException("Invalid zip entry name : " + ze.getName());
            }
            stream = new BufferedInputStream(inputStream);
            return getInputStreamAsByteArray(stream, (int)ze.getSize());
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    public static int hashCode(final Object[] array) {
        final int prime = 31;
        if (array == null) {
            return 0;
        }
        int result = 1;
        for (int index = 0; index < array.length; ++index) {
            result = prime * result + ((array[index] == null) ? 0 : array[index].hashCode());
        }
        return result;
    }
    
    public static final boolean isPotentialZipArchive(final String name) {
        final int lastDot = name.lastIndexOf(46);
        if (lastDot == -1) {
            return false;
        }
        if (name.lastIndexOf(File.separatorChar) > lastDot) {
            return false;
        }
        final int length = name.length();
        final int extensionLength = length - lastDot - 1;
        if (extensionLength == "java".length()) {
            for (int i = extensionLength - 1; i >= 0; --i) {
                if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != "java".charAt(i)) {
                    break;
                }
                if (i == 0) {
                    return false;
                }
            }
        }
        if (extensionLength == "class".length()) {
            for (int i = extensionLength - 1; i >= 0; --i) {
                if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != "class".charAt(i)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public static final boolean isClassFileName(final char[] name) {
        final int nameLength = (name == null) ? 0 : name.length;
        final int suffixLength = Util.SUFFIX_CLASS.length;
        if (nameLength < suffixLength) {
            return false;
        }
        int i = 0;
        final int offset = nameLength - suffixLength;
        while (i < suffixLength) {
            final char c = name[offset + i];
            if (c != Util.SUFFIX_class[i] && c != Util.SUFFIX_CLASS[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }
    
    public static final boolean isClassFileName(final String name) {
        final int nameLength = (name == null) ? 0 : name.length();
        final int suffixLength = Util.SUFFIX_CLASS.length;
        if (nameLength < suffixLength) {
            return false;
        }
        for (int i = 0; i < suffixLength; ++i) {
            final char c = name.charAt(nameLength - i - 1);
            final int suffixIndex = suffixLength - i - 1;
            if (c != Util.SUFFIX_class[suffixIndex] && c != Util.SUFFIX_CLASS[suffixIndex]) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean isExcluded(char[] path, final char[][] inclusionPatterns, final char[][] exclusionPatterns, final boolean isFolderPath) {
        if (inclusionPatterns == null && exclusionPatterns == null) {
            return false;
        }
        Label_0139: {
            if (inclusionPatterns != null) {
                for (int i = 0, length = inclusionPatterns.length; i < length; ++i) {
                    char[] folderPattern;
                    final char[] pattern = folderPattern = inclusionPatterns[i];
                    if (isFolderPath) {
                        final int lastSlash = CharOperation.lastIndexOf('/', pattern);
                        if (lastSlash != -1 && lastSlash != pattern.length - 1) {
                            final int star = CharOperation.indexOf('*', pattern, lastSlash);
                            if (star == -1 || star >= pattern.length - 1 || pattern[star + 1] != '*') {
                                folderPattern = CharOperation.subarray(pattern, 0, lastSlash);
                            }
                        }
                    }
                    if (CharOperation.pathMatch(folderPattern, path, true, '/')) {
                        break Label_0139;
                    }
                }
                return true;
            }
        }
        if (isFolderPath) {
            path = CharOperation.concat(path, new char[] { '*' }, '/');
        }
        if (exclusionPatterns != null) {
            for (int i = 0, length = exclusionPatterns.length; i < length; ++i) {
                if (CharOperation.pathMatch(exclusionPatterns[i], path, true, '/')) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static final boolean isJavaFileName(final char[] name) {
        final int nameLength = (name == null) ? 0 : name.length;
        final int suffixLength = Util.SUFFIX_JAVA.length;
        if (nameLength < suffixLength) {
            return false;
        }
        int i = 0;
        final int offset = nameLength - suffixLength;
        while (i < suffixLength) {
            final char c = name[offset + i];
            if (c != Util.SUFFIX_java[i] && c != Util.SUFFIX_JAVA[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }
    
    public static final boolean isJavaFileName(final String name) {
        final int nameLength = (name == null) ? 0 : name.length();
        final int suffixLength = Util.SUFFIX_JAVA.length;
        if (nameLength < suffixLength) {
            return false;
        }
        for (int i = 0; i < suffixLength; ++i) {
            final char c = name.charAt(nameLength - i - 1);
            final int suffixIndex = suffixLength - i - 1;
            if (c != Util.SUFFIX_java[suffixIndex] && c != Util.SUFFIX_JAVA[suffixIndex]) {
                return false;
            }
        }
        return true;
    }
    
    public static void reverseQuickSort(final char[][] list, int left, int right) {
        final int original_left = left;
        final int original_right = right;
        final char[] mid = list[left + (right - left) / 2];
        while (true) {
            if (CharOperation.compareTo(list[left], mid) <= 0) {
                while (CharOperation.compareTo(mid, list[right]) > 0) {
                    --right;
                }
                if (left <= right) {
                    final char[] tmp = list[left];
                    list[left] = list[right];
                    list[right] = tmp;
                    ++left;
                    --right;
                }
                if (left > right) {
                    break;
                }
                continue;
            }
            else {
                ++left;
            }
        }
        if (original_left < right) {
            reverseQuickSort(list, original_left, right);
        }
        if (left < original_right) {
            reverseQuickSort(list, left, original_right);
        }
    }
    
    public static void reverseQuickSort(final char[][] list, int left, int right, final int[] result) {
        final int original_left = left;
        final int original_right = right;
        final char[] mid = list[left + (right - left) / 2];
        while (true) {
            if (CharOperation.compareTo(list[left], mid) <= 0) {
                while (CharOperation.compareTo(mid, list[right]) > 0) {
                    --right;
                }
                if (left <= right) {
                    final char[] tmp = list[left];
                    list[left] = list[right];
                    list[right] = tmp;
                    final int temp = result[left];
                    result[left] = result[right];
                    result[right] = temp;
                    ++left;
                    --right;
                }
                if (left > right) {
                    break;
                }
                continue;
            }
            else {
                ++left;
            }
        }
        if (original_left < right) {
            reverseQuickSort(list, original_left, right, result);
        }
        if (left < original_right) {
            reverseQuickSort(list, left, original_right, result);
        }
    }
    
    public static final int searchColumnNumber(final int[] startLineIndexes, final int lineNumber, final int position) {
        switch (lineNumber) {
            case 1: {
                return position + 1;
            }
            case 2: {
                return position - startLineIndexes[0];
            }
            default: {
                final int line = lineNumber - 2;
                final int length = startLineIndexes.length;
                if (line >= length) {
                    return position - startLineIndexes[length - 1];
                }
                return position - startLineIndexes[line];
            }
        }
    }
    
    public static Boolean toBoolean(final boolean bool) {
        if (bool) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    public static String toString(final Object[] objects) {
        return toString(objects, new Displayable() {
            @Override
            public String displayString(final Object o) {
                if (o == null) {
                    return "null";
                }
                return o.toString();
            }
        });
    }
    
    public static String toString(final Object[] objects, final Displayable renderer) {
        if (objects == null) {
            return "";
        }
        final StringBuffer buffer = new StringBuffer(10);
        for (int i = 0; i < objects.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(renderer.displayString(objects[i]));
        }
        return buffer.toString();
    }
    
    public static void writeToDisk(final boolean generatePackagesStructure, final String outputPath, final String relativeFileName, final ClassFile classFile) throws IOException {
        final FileOutputStream file = getFileOutputStream(generatePackagesStructure, outputPath, relativeFileName);
        final BufferedOutputStream output = new BufferedOutputStream(file, 1024);
        try {
            output.write(classFile.header, 0, classFile.headerOffset);
            output.write(classFile.contents, 0, classFile.contentsOffset);
            output.flush();
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            output.close();
        }
        output.close();
    }
    
    public static void recordNestedType(final ClassFile classFile, final TypeBinding typeBinding) {
        if (classFile.visitedTypes == null) {
            classFile.visitedTypes = new HashSet(3);
        }
        else if (classFile.visitedTypes.contains(typeBinding)) {
            return;
        }
        classFile.visitedTypes.add(typeBinding);
        if (typeBinding.isParameterizedType() && (typeBinding.tagBits & 0x800L) != 0x0L) {
            final ParameterizedTypeBinding parameterizedTypeBinding = (ParameterizedTypeBinding)typeBinding;
            final ReferenceBinding genericType = parameterizedTypeBinding.genericType();
            if ((genericType.tagBits & 0x800L) != 0x0L) {
                recordNestedType(classFile, genericType);
            }
            final TypeBinding[] arguments = parameterizedTypeBinding.arguments;
            if (arguments != null) {
                for (int j = 0, max2 = arguments.length; j < max2; ++j) {
                    final TypeBinding argument = arguments[j];
                    if (argument.isWildcard()) {
                        final WildcardBinding wildcardBinding = (WildcardBinding)argument;
                        final TypeBinding bound = wildcardBinding.bound;
                        if (bound != null && (bound.tagBits & 0x800L) != 0x0L) {
                            recordNestedType(classFile, bound);
                        }
                        final ReferenceBinding superclass = wildcardBinding.superclass();
                        if (superclass != null && (superclass.tagBits & 0x800L) != 0x0L) {
                            recordNestedType(classFile, superclass);
                        }
                        final ReferenceBinding[] superInterfaces = wildcardBinding.superInterfaces();
                        if (superInterfaces != null) {
                            for (int k = 0, max3 = superInterfaces.length; k < max3; ++k) {
                                final ReferenceBinding superInterface = superInterfaces[k];
                                if ((superInterface.tagBits & 0x800L) != 0x0L) {
                                    recordNestedType(classFile, superInterface);
                                }
                            }
                        }
                    }
                    else if ((argument.tagBits & 0x800L) != 0x0L) {
                        recordNestedType(classFile, argument);
                    }
                }
            }
        }
        else if (typeBinding.isTypeVariable() && (typeBinding.tagBits & 0x800L) != 0x0L) {
            final TypeVariableBinding typeVariableBinding = (TypeVariableBinding)typeBinding;
            final TypeBinding upperBound = typeVariableBinding.upperBound();
            if (upperBound != null && (upperBound.tagBits & 0x800L) != 0x0L) {
                recordNestedType(classFile, upperBound);
            }
            final TypeBinding[] upperBounds = typeVariableBinding.otherUpperBounds();
            if (upperBounds != null) {
                for (int i = 0, max4 = upperBounds.length; i < max4; ++i) {
                    final TypeBinding otherUpperBound = upperBounds[i];
                    if ((otherUpperBound.tagBits & 0x800L) != 0x0L) {
                        recordNestedType(classFile, otherUpperBound);
                    }
                }
            }
        }
        else if (typeBinding.isNestedType()) {
            TypeBinding enclosingType = typeBinding;
            while (true) {
                while (enclosingType.canBeSeenBy(classFile.referenceBinding.scope)) {
                    enclosingType = enclosingType.enclosingType();
                    if (enclosingType == null) {
                        final boolean onBottomForBug445231 = enclosingType != null;
                        classFile.recordInnerClasses(typeBinding, onBottomForBug445231);
                        return;
                    }
                }
                continue;
            }
        }
    }
    
    public static File getJavaHome() {
        final String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            final File javaHomeFile = new File(javaHome);
            if (javaHomeFile.exists()) {
                return javaHomeFile;
            }
        }
        return null;
    }
    
    public static void collectRunningVMBootclasspath(final List bootclasspaths) {
        for (final String filePath : collectFilesNames()) {
            final FileSystem.Classpath currentClasspath = FileSystem.getClasspath(filePath, null, null, null);
            if (currentClasspath != null) {
                bootclasspaths.add(currentClasspath);
            }
        }
    }
    
    public static List<String> collectFilesNames() {
        final String javaversion = System.getProperty("java.version");
        if (javaversion != null && javaversion.equalsIgnoreCase("1.1.8")) {
            throw new IllegalStateException();
        }
        String bootclasspathProperty = System.getProperty("sun.boot.class.path");
        if (bootclasspathProperty == null || bootclasspathProperty.length() == 0) {
            bootclasspathProperty = System.getProperty("vm.boot.class.path");
            if (bootclasspathProperty == null || bootclasspathProperty.length() == 0) {
                bootclasspathProperty = System.getProperty("org.apache.harmony.boot.class.path");
            }
        }
        final List<String> filePaths = new ArrayList<String>();
        if (bootclasspathProperty != null && bootclasspathProperty.length() != 0) {
            final StringTokenizer tokenizer = new StringTokenizer(bootclasspathProperty, File.pathSeparator);
            while (tokenizer.hasMoreTokens()) {
                filePaths.add(tokenizer.nextToken());
            }
        }
        else {
            final File javaHome = getJavaHome();
            if (javaHome != null) {
                File[] directoriesToCheck = null;
                if (System.getProperty("os.name").startsWith("Mac")) {
                    directoriesToCheck = new File[] { new File(javaHome, "../Classes") };
                }
                else {
                    directoriesToCheck = new File[] { new File(javaHome, "lib") };
                }
                final File[][] systemLibrariesJars = Main.getLibrariesFiles(directoriesToCheck);
                if (systemLibrariesJars != null) {
                    for (int i = 0, max = systemLibrariesJars.length; i < max; ++i) {
                        final File[] current = systemLibrariesJars[i];
                        if (current != null) {
                            for (int j = 0, max2 = current.length; j < max2; ++j) {
                                filePaths.add(current[j].getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
        return filePaths;
    }
    
    public static int getParameterCount(final char[] methodSignature) {
        try {
            int count = 0;
            int i = CharOperation.indexOf('(', methodSignature);
            if (i < 0) {
                throw new IllegalArgumentException();
            }
            ++i;
            while (methodSignature[i] != ')') {
                final int e = scanTypeSignature(methodSignature, i);
                if (e < 0) {
                    throw new IllegalArgumentException();
                }
                i = e + 1;
                ++count;
            }
            return count;
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException();
        }
    }
    
    public static int scanTypeSignature(final char[] string, final int start) {
        if (start >= string.length) {
            throw new IllegalArgumentException();
        }
        final char c = string[start];
        switch (c) {
            case '[': {
                return scanArrayTypeSignature(string, start);
            }
            case 'L':
            case 'Q': {
                return scanClassTypeSignature(string, start);
            }
            case 'T': {
                return scanTypeVariableSignature(string, start);
            }
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'V':
            case 'Z': {
                return scanBaseTypeSignature(string, start);
            }
            case '!': {
                return scanCaptureTypeSignature(string, start);
            }
            case '*':
            case '+':
            case '-': {
                return scanTypeBoundSignature(string, start);
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public static int scanBaseTypeSignature(final char[] string, final int start) {
        if (start >= string.length) {
            throw new IllegalArgumentException();
        }
        final char c = string[start];
        if ("BCDFIJSVZ".indexOf(c) >= 0) {
            return start;
        }
        throw new IllegalArgumentException();
    }
    
    public static int scanArrayTypeSignature(final char[] string, int start) {
        final int length = string.length;
        if (start >= length - 1) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        if (c != '[') {
            throw new IllegalArgumentException();
        }
        for (c = string[++start]; c == '['; c = string[++start]) {
            if (start >= length - 1) {
                throw new IllegalArgumentException();
            }
        }
        return scanTypeSignature(string, start);
    }
    
    public static int scanCaptureTypeSignature(final char[] string, final int start) {
        if (start >= string.length - 1) {
            throw new IllegalArgumentException();
        }
        final char c = string[start];
        if (c != '!') {
            throw new IllegalArgumentException();
        }
        return scanTypeBoundSignature(string, start + 1);
    }
    
    public static int scanTypeVariableSignature(final char[] string, final int start) {
        if (start >= string.length - 2) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        if (c != 'T') {
            throw new IllegalArgumentException();
        }
        final int id = scanIdentifier(string, start + 1);
        c = string[id + 1];
        if (c == ';') {
            return id + 1;
        }
        throw new IllegalArgumentException();
    }
    
    public static int scanIdentifier(final char[] string, final int start) {
        if (start >= string.length) {
            throw new IllegalArgumentException();
        }
        int p = start;
        do {
            final char c = string[p];
            if (c == '<' || c == '>' || c == ':' || c == ';' || c == '.' || c == '/') {
                return p - 1;
            }
        } while (++p != string.length);
        return p - 1;
    }
    
    public static int scanClassTypeSignature(final char[] string, final int start) {
        if (start >= string.length - 2) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        if (c != 'L' && c != 'Q') {
            return -1;
        }
        for (int p = start + 1; p < string.length; ++p) {
            c = string[p];
            if (c == ';') {
                return p;
            }
            if (c == '<') {
                final int e = p = scanTypeArgumentSignatures(string, p);
            }
            else if (c == '.' || c == '/') {
                final int id = p = scanIdentifier(string, p + 1);
            }
        }
        throw new IllegalArgumentException();
    }
    
    public static int scanTypeBoundSignature(final char[] string, int start) {
        if (start >= string.length) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        switch (c) {
            case '*': {
                return start;
            }
            case '+':
            case '-': {
                if (start >= string.length - 2) {
                    throw new IllegalArgumentException();
                }
                c = string[++start];
                switch (c) {
                    case '!': {
                        return scanCaptureTypeSignature(string, start);
                    }
                    case '+':
                    case '-': {
                        return scanTypeBoundSignature(string, start);
                    }
                    case 'L':
                    case 'Q': {
                        return scanClassTypeSignature(string, start);
                    }
                    case 'T': {
                        return scanTypeVariableSignature(string, start);
                    }
                    case '[': {
                        return scanArrayTypeSignature(string, start);
                    }
                    case '*': {
                        return start;
                    }
                    default: {
                        throw new IllegalArgumentException();
                    }
                }
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public static int scanTypeArgumentSignatures(final char[] string, final int start) {
        if (start >= string.length - 1) {
            throw new IllegalArgumentException();
        }
        char c = string[start];
        if (c != '<') {
            throw new IllegalArgumentException();
        }
        int e;
        for (int p = start + 1; p < string.length; p = e + 1) {
            c = string[p];
            if (c == '>') {
                return p;
            }
            e = scanTypeArgumentSignature(string, p);
        }
        throw new IllegalArgumentException();
    }
    
    public static int scanTypeArgumentSignature(final char[] string, final int start) {
        if (start >= string.length) {
            throw new IllegalArgumentException();
        }
        final char c = string[start];
        switch (c) {
            case '*': {
                return start;
            }
            case '+':
            case '-': {
                return scanTypeBoundSignature(string, start);
            }
            default: {
                return scanTypeSignature(string, start);
            }
        }
    }
    
    public static boolean effectivelyEqual(final Object[] one, final Object[] two) {
        if (one == two) {
            return true;
        }
        final int oneLength = (one == null) ? 0 : one.length;
        final int twoLength = (two == null) ? 0 : two.length;
        if (oneLength != twoLength) {
            return false;
        }
        if (oneLength == 0) {
            return true;
        }
        for (int i = 0; i < one.length; ++i) {
            if (one[i] != two[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static void appendEscapedChar(final StringBuffer buffer, final char c, final boolean stringLiteral) {
        switch (c) {
            case '\b': {
                buffer.append("\\b");
                break;
            }
            case '\t': {
                buffer.append("\\t");
                break;
            }
            case '\n': {
                buffer.append("\\n");
                break;
            }
            case '\f': {
                buffer.append("\\f");
                break;
            }
            case '\r': {
                buffer.append("\\r");
                break;
            }
            case '\"': {
                if (stringLiteral) {
                    buffer.append("\\\"");
                    break;
                }
                buffer.append(c);
                break;
            }
            case '\'': {
                if (stringLiteral) {
                    buffer.append(c);
                    break;
                }
                buffer.append("\\'");
                break;
            }
            case '\\': {
                buffer.append("\\\\");
                break;
            }
            default: {
                if (c >= ' ') {
                    buffer.append(c);
                    break;
                }
                if (c >= '\u0010') {
                    buffer.append("\\u00").append(Integer.toHexString(c));
                    break;
                }
                if (c >= '\0') {
                    buffer.append("\\u000").append(Integer.toHexString(c));
                    break;
                }
                buffer.append(c);
                break;
            }
        }
    }
    
    public interface Displayable
    {
        String displayString(final Object p0);
    }
}
