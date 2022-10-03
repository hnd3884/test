package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.InputStream;
import org.eclipse.jdt.internal.compiler.util.Util;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;

public class ClassFileReader extends ClassFileStruct implements IBinaryType
{
    private int accessFlags;
    private char[] classFileName;
    private char[] className;
    private int classNameIndex;
    private int constantPoolCount;
    private AnnotationInfo[] annotations;
    private TypeAnnotationInfo[] typeAnnotations;
    private FieldInfo[] fields;
    private int fieldsCount;
    private InnerClassInfo innerInfo;
    private int innerInfoIndex;
    private InnerClassInfo[] innerInfos;
    private char[][] interfaceNames;
    private int interfacesCount;
    private MethodInfo[] methods;
    private int methodsCount;
    private char[] signature;
    private char[] sourceName;
    private char[] sourceFileName;
    private char[] superclassName;
    private long tagBits;
    private long version;
    private char[] enclosingTypeName;
    private char[][][] missingTypeNames;
    private int enclosingNameAndTypeIndex;
    private char[] enclosingMethod;
    private ExternalAnnotationProvider annotationProvider;
    private BinaryTypeBinding.ExternalAnnotationStatus externalAnnotationStatus;
    
    private static String printTypeModifiers(final int modifiers) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintWriter print = new PrintWriter(out);
        if ((modifiers & 0x1) != 0x0) {
            print.print("public ");
        }
        if ((modifiers & 0x2) != 0x0) {
            print.print("private ");
        }
        if ((modifiers & 0x10) != 0x0) {
            print.print("final ");
        }
        if ((modifiers & 0x20) != 0x0) {
            print.print("super ");
        }
        if ((modifiers & 0x200) != 0x0) {
            print.print("interface ");
        }
        if ((modifiers & 0x400) != 0x0) {
            print.print("abstract ");
        }
        print.flush();
        return out.toString();
    }
    
    public static ClassFileReader read(final File file) throws ClassFormatException, IOException {
        return read(file, false);
    }
    
    public static ClassFileReader read(final File file, final boolean fullyInitialize) throws ClassFormatException, IOException {
        final byte[] classFileBytes = Util.getFileByteContent(file);
        final ClassFileReader classFileReader = new ClassFileReader(classFileBytes, file.getAbsolutePath().toCharArray());
        if (fullyInitialize) {
            classFileReader.initialize();
        }
        return classFileReader;
    }
    
    public static ClassFileReader read(final InputStream stream, final String fileName) throws ClassFormatException, IOException {
        return read(stream, fileName, false);
    }
    
    public static ClassFileReader read(final InputStream stream, final String fileName, final boolean fullyInitialize) throws ClassFormatException, IOException {
        final byte[] classFileBytes = Util.getInputStreamAsByteArray(stream, -1);
        final ClassFileReader classFileReader = new ClassFileReader(classFileBytes, fileName.toCharArray());
        if (fullyInitialize) {
            classFileReader.initialize();
        }
        return classFileReader;
    }
    
    public static ClassFileReader read(final ZipFile zip, final String filename) throws ClassFormatException, IOException {
        return read(zip, filename, false);
    }
    
    public static ClassFileReader read(final ZipFile zip, final String filename, final boolean fullyInitialize) throws ClassFormatException, IOException {
        final ZipEntry ze = zip.getEntry(filename);
        if (ze == null) {
            return null;
        }
        final byte[] classFileBytes = Util.getZipEntryByteContent(ze, zip);
        final ClassFileReader classFileReader = new ClassFileReader(classFileBytes, filename.toCharArray());
        if (fullyInitialize) {
            classFileReader.initialize();
        }
        return classFileReader;
    }
    
    public static ClassFileReader read(final String fileName) throws ClassFormatException, IOException {
        return read(fileName, false);
    }
    
    public static ClassFileReader read(final String fileName, final boolean fullyInitialize) throws ClassFormatException, IOException {
        return read(new File(fileName), fullyInitialize);
    }
    
    public ClassFileReader(final byte[] classFileBytes, final char[] fileName) throws ClassFormatException {
        this(classFileBytes, fileName, false);
    }
    
    public ClassFileReader(final byte[] classFileBytes, final char[] fileName, final boolean fullyInitialize) throws ClassFormatException {
        super(classFileBytes, null, 0);
        this.externalAnnotationStatus = BinaryTypeBinding.ExternalAnnotationStatus.NOT_EEA_CONFIGURED;
        this.classFileName = fileName;
        int readOffset = 10;
        try {
            this.version = ((long)this.u2At(6) << 16) + this.u2At(4);
            this.constantPoolCount = this.u2At(8);
            this.constantPoolOffsets = new int[this.constantPoolCount];
            for (int i = 1; i < this.constantPoolCount; ++i) {
                final int tag = this.u1At(readOffset);
                switch (tag) {
                    case 1: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += this.u2At(readOffset + 1);
                        readOffset += 3;
                        break;
                    }
                    case 3: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 4: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 5: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 9;
                        ++i;
                        break;
                    }
                    case 6: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 9;
                        ++i;
                        break;
                    }
                    case 7: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 3;
                        break;
                    }
                    case 8: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 3;
                        break;
                    }
                    case 9: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 10: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 11: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 12: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 5;
                        break;
                    }
                    case 15: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 4;
                        break;
                    }
                    case 16: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 3;
                        break;
                    }
                    case 18: {
                        this.constantPoolOffsets[i] = readOffset;
                        readOffset += 5;
                        break;
                    }
                }
            }
            this.accessFlags = this.u2At(readOffset);
            readOffset += 2;
            this.classNameIndex = this.u2At(readOffset);
            this.className = this.getConstantClassNameAt(this.classNameIndex);
            readOffset += 2;
            final int superclassNameIndex = this.u2At(readOffset);
            readOffset += 2;
            if (superclassNameIndex != 0) {
                this.superclassName = this.getConstantClassNameAt(superclassNameIndex);
            }
            this.interfacesCount = this.u2At(readOffset);
            readOffset += 2;
            if (this.interfacesCount != 0) {
                this.interfaceNames = new char[this.interfacesCount][];
                for (int j = 0; j < this.interfacesCount; ++j) {
                    this.interfaceNames[j] = this.getConstantClassNameAt(this.u2At(readOffset));
                    readOffset += 2;
                }
            }
            this.fieldsCount = this.u2At(readOffset);
            readOffset += 2;
            if (this.fieldsCount != 0) {
                this.fields = new FieldInfo[this.fieldsCount];
                for (int k = 0; k < this.fieldsCount; ++k) {
                    final FieldInfo field = FieldInfo.createField(this.reference, this.constantPoolOffsets, readOffset);
                    this.fields[k] = field;
                    readOffset += field.sizeInBytes();
                }
            }
            this.methodsCount = this.u2At(readOffset);
            readOffset += 2;
            if (this.methodsCount != 0) {
                this.methods = new MethodInfo[this.methodsCount];
                final boolean isAnnotationType = (this.accessFlags & 0x2000) != 0x0;
                for (int k = 0; k < this.methodsCount; ++k) {
                    this.methods[k] = (isAnnotationType ? AnnotationMethodInfo.createAnnotationMethod(this.reference, this.constantPoolOffsets, readOffset) : MethodInfo.createMethod(this.reference, this.constantPoolOffsets, readOffset));
                    readOffset += this.methods[k].sizeInBytes();
                }
            }
            final int attributesCount = this.u2At(readOffset);
            readOffset += 2;
            for (int k = 0; k < attributesCount; ++k) {
                int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)];
                final char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                if (attributeName.length == 0) {
                    readOffset += (int)(6L + this.u4At(readOffset + 2));
                }
                else {
                    switch (attributeName[0]) {
                        case 'E': {
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.EnclosingMethodName)) {
                                utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.u2At(readOffset + 6)] + 1)];
                                this.enclosingTypeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                this.enclosingNameAndTypeIndex = this.u2At(readOffset + 8);
                                break;
                            }
                            break;
                        }
                        case 'D': {
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName)) {
                                this.accessFlags |= 0x100000;
                                break;
                            }
                            break;
                        }
                        case 'I': {
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.InnerClassName)) {
                                int innerOffset = readOffset + 6;
                                final int number_of_classes = this.u2At(innerOffset);
                                if (number_of_classes == 0) {
                                    break;
                                }
                                innerOffset += 2;
                                this.innerInfos = new InnerClassInfo[number_of_classes];
                                for (int l = 0; l < number_of_classes; ++l) {
                                    this.innerInfos[l] = new InnerClassInfo(this.reference, this.constantPoolOffsets, innerOffset);
                                    if (this.classNameIndex == this.innerInfos[l].innerClassNameIndex) {
                                        this.innerInfo = this.innerInfos[l];
                                        this.innerInfoIndex = l;
                                    }
                                    innerOffset += 8;
                                }
                                if (this.innerInfo == null) {
                                    break;
                                }
                                final char[] enclosingType = this.innerInfo.getEnclosingTypeName();
                                if (enclosingType != null) {
                                    this.enclosingTypeName = enclosingType;
                                    break;
                                }
                                break;
                            }
                            else {
                                if (CharOperation.equals(attributeName, AttributeNamesConstants.InconsistentHierarchy)) {
                                    this.tagBits |= 0x20000L;
                                    break;
                                }
                                break;
                            }
                            break;
                        }
                        case 'S': {
                            if (attributeName.length > 2) {
                                switch (attributeName[1]) {
                                    case 'o': {
                                        if (CharOperation.equals(attributeName, AttributeNamesConstants.SourceName)) {
                                            utf8Offset = this.constantPoolOffsets[this.u2At(readOffset + 6)];
                                            this.sourceFileName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                            break;
                                        }
                                        break;
                                    }
                                    case 'y': {
                                        if (CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) {
                                            this.accessFlags |= 0x1000;
                                            break;
                                        }
                                        break;
                                    }
                                    case 'i': {
                                        if (CharOperation.equals(attributeName, AttributeNamesConstants.SignatureName)) {
                                            utf8Offset = this.constantPoolOffsets[this.u2At(readOffset + 6)];
                                            this.signature = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                            break;
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                            break;
                        }
                        case 'R': {
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
                                this.decodeAnnotations(readOffset, true);
                                break;
                            }
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
                                this.decodeAnnotations(readOffset, false);
                                break;
                            }
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName)) {
                                this.decodeTypeAnnotations(readOffset, true);
                                break;
                            }
                            if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName)) {
                                this.decodeTypeAnnotations(readOffset, false);
                                break;
                            }
                            break;
                        }
                        case 'M': {
                            if (!CharOperation.equals(attributeName, AttributeNamesConstants.MissingTypesName)) {
                                break;
                            }
                            int missingTypeOffset = readOffset + 6;
                            final int numberOfMissingTypes = this.u2At(missingTypeOffset);
                            if (numberOfMissingTypes != 0) {
                                this.missingTypeNames = new char[numberOfMissingTypes][][];
                                missingTypeOffset += 2;
                                for (int l = 0; l < numberOfMissingTypes; ++l) {
                                    utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.u2At(missingTypeOffset)] + 1)];
                                    final char[] missingTypeConstantPoolName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                                    this.missingTypeNames[l] = CharOperation.splitOn('/', missingTypeConstantPoolName);
                                    missingTypeOffset += 2;
                                }
                                break;
                            }
                            break;
                        }
                    }
                    readOffset += (int)(6L + this.u4At(readOffset + 2));
                }
            }
            if (fullyInitialize) {
                this.initialize();
            }
        }
        catch (final ClassFormatException e) {
            throw e;
        }
        catch (final Exception ex) {
            throw new ClassFormatException(21, readOffset);
        }
    }
    
    public ZipFile setExternalAnnotationProvider(final String basePath, final String qualifiedBinaryTypeName, ZipFile zipFile, final ZipFileProducer producer) throws IOException {
        this.externalAnnotationStatus = BinaryTypeBinding.ExternalAnnotationStatus.NO_EEA_FILE;
        final String qualifiedBinaryFileName = String.valueOf(qualifiedBinaryTypeName) + ".eea";
        if (zipFile == null) {
            final File annotationBase = new File(basePath);
            if (annotationBase.isDirectory()) {
                try {
                    final String filePath = String.valueOf(annotationBase.getAbsolutePath()) + '/' + qualifiedBinaryFileName;
                    this.annotationProvider = new ExternalAnnotationProvider(new FileInputStream(filePath), String.valueOf(this.getName()));
                    this.externalAnnotationStatus = BinaryTypeBinding.ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
                }
                catch (final FileNotFoundException ex) {}
                return null;
            }
            if (!annotationBase.exists()) {
                return null;
            }
            zipFile = ((producer != null) ? producer.produce() : new ZipFile(annotationBase));
        }
        final ZipEntry entry = zipFile.getEntry(qualifiedBinaryFileName);
        if (entry != null) {
            this.annotationProvider = new ExternalAnnotationProvider(zipFile.getInputStream(entry), String.valueOf(this.getName()));
            this.externalAnnotationStatus = BinaryTypeBinding.ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
        }
        return zipFile;
    }
    
    public boolean hasAnnotationProvider() {
        return this.annotationProvider != null;
    }
    
    public void markAsFromSource() {
        this.externalAnnotationStatus = BinaryTypeBinding.ExternalAnnotationStatus.FROM_SOURCE;
    }
    
    @Override
    public BinaryTypeBinding.ExternalAnnotationStatus getExternalAnnotationStatus() {
        return this.externalAnnotationStatus;
    }
    
    @Override
    public ITypeAnnotationWalker enrichWithExternalAnnotationsFor(final ITypeAnnotationWalker walker, final Object member, final LookupEnvironment environment) {
        if (walker == ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER && this.annotationProvider != null) {
            if (member == null) {
                return this.annotationProvider.forTypeHeader(environment);
            }
            if (member instanceof IBinaryField) {
                final IBinaryField field = (IBinaryField)member;
                char[] fieldSignature = field.getGenericSignature();
                if (fieldSignature == null) {
                    fieldSignature = field.getTypeName();
                }
                return this.annotationProvider.forField(field.getName(), fieldSignature, environment);
            }
            if (member instanceof IBinaryMethod) {
                final IBinaryMethod method = (IBinaryMethod)member;
                char[] methodSignature = method.getGenericSignature();
                if (methodSignature == null) {
                    methodSignature = method.getMethodDescriptor();
                }
                return this.annotationProvider.forMethod(method.isConstructor() ? TypeConstants.INIT : method.getSelector(), methodSignature, environment);
            }
        }
        return walker;
    }
    
    public int accessFlags() {
        return this.accessFlags;
    }
    
    private void decodeAnnotations(final int offset, final boolean runtimeVisible) {
        final int numberOfAnnotations = this.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            int readOffset = offset + 8;
            AnnotationInfo[] newInfos = null;
            int newInfoCount = 0;
            for (int i = 0; i < numberOfAnnotations; ++i) {
                final AnnotationInfo newInfo = new AnnotationInfo(this.reference, this.constantPoolOffsets, readOffset, runtimeVisible, false);
                readOffset += newInfo.readOffset;
                final long standardTagBits = newInfo.standardAnnotationTagBits;
                if (standardTagBits != 0L) {
                    this.tagBits |= standardTagBits;
                }
                else {
                    if (newInfos == null) {
                        newInfos = new AnnotationInfo[numberOfAnnotations - i];
                    }
                    newInfos[newInfoCount++] = newInfo;
                }
            }
            if (newInfos == null) {
                return;
            }
            if (this.annotations == null) {
                if (newInfoCount != newInfos.length) {
                    System.arraycopy(newInfos, 0, newInfos = new AnnotationInfo[newInfoCount], 0, newInfoCount);
                }
                this.annotations = newInfos;
            }
            else {
                final int length = this.annotations.length;
                final AnnotationInfo[] temp = new AnnotationInfo[length + newInfoCount];
                System.arraycopy(this.annotations, 0, temp, 0, length);
                System.arraycopy(newInfos, 0, temp, length, newInfoCount);
                this.annotations = temp;
            }
        }
    }
    
    private void decodeTypeAnnotations(final int offset, final boolean runtimeVisible) {
        final int numberOfAnnotations = this.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            int readOffset = offset + 8;
            TypeAnnotationInfo[] newInfos = null;
            newInfos = new TypeAnnotationInfo[numberOfAnnotations];
            for (int i = 0; i < numberOfAnnotations; ++i) {
                final TypeAnnotationInfo newInfo = new TypeAnnotationInfo(this.reference, this.constantPoolOffsets, readOffset, runtimeVisible, false);
                readOffset += newInfo.readOffset;
                newInfos[i] = newInfo;
            }
            if (this.typeAnnotations == null) {
                this.typeAnnotations = newInfos;
            }
            else {
                final int length = this.typeAnnotations.length;
                final TypeAnnotationInfo[] temp = new TypeAnnotationInfo[length + numberOfAnnotations];
                System.arraycopy(this.typeAnnotations, 0, temp, 0, length);
                System.arraycopy(newInfos, 0, temp, length, numberOfAnnotations);
                this.typeAnnotations = temp;
            }
        }
    }
    
    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return this.annotations;
    }
    
    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return this.typeAnnotations;
    }
    
    private char[] getConstantClassNameAt(final int constantPoolIndex) {
        final int utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[constantPoolIndex] + 1)];
        return this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
    }
    
    public int[] getConstantPoolOffsets() {
        return this.constantPoolOffsets;
    }
    
    @Override
    public char[] getEnclosingMethod() {
        if (this.enclosingNameAndTypeIndex <= 0) {
            return null;
        }
        if (this.enclosingMethod == null) {
            final StringBuffer buffer = new StringBuffer();
            final int nameAndTypeOffset = this.constantPoolOffsets[this.enclosingNameAndTypeIndex];
            int utf8Offset = this.constantPoolOffsets[this.u2At(nameAndTypeOffset + 1)];
            buffer.append(this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1)));
            utf8Offset = this.constantPoolOffsets[this.u2At(nameAndTypeOffset + 3)];
            buffer.append(this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1)));
            this.enclosingMethod = String.valueOf(buffer).toCharArray();
        }
        return this.enclosingMethod;
    }
    
    @Override
    public char[] getEnclosingTypeName() {
        return this.enclosingTypeName;
    }
    
    @Override
    public IBinaryField[] getFields() {
        return this.fields;
    }
    
    @Override
    public char[] getFileName() {
        return this.classFileName;
    }
    
    @Override
    public char[] getGenericSignature() {
        return this.signature;
    }
    
    public char[] getInnerSourceName() {
        if (this.innerInfo != null) {
            return this.innerInfo.getSourceName();
        }
        return null;
    }
    
    @Override
    public char[][] getInterfaceNames() {
        return this.interfaceNames;
    }
    
    @Override
    public IBinaryNestedType[] getMemberTypes() {
        if (this.innerInfos == null) {
            return null;
        }
        final int length = this.innerInfos.length;
        final int startingIndex = (this.innerInfo != null) ? (this.innerInfoIndex + 1) : 0;
        if (length == startingIndex) {
            return null;
        }
        IBinaryNestedType[] memberTypes = new IBinaryNestedType[length - this.innerInfoIndex];
        int memberTypeIndex = 0;
        for (int i = startingIndex; i < length; ++i) {
            final InnerClassInfo currentInnerInfo = this.innerInfos[i];
            final int outerClassNameIdx = currentInnerInfo.outerClassNameIndex;
            final int innerNameIndex = currentInnerInfo.innerNameIndex;
            if (outerClassNameIdx != 0 && innerNameIndex != 0 && outerClassNameIdx == this.classNameIndex && currentInnerInfo.getSourceName().length != 0) {
                memberTypes[memberTypeIndex++] = currentInnerInfo;
            }
        }
        if (memberTypeIndex == 0) {
            return null;
        }
        if (memberTypeIndex != memberTypes.length) {
            System.arraycopy(memberTypes, 0, memberTypes = new IBinaryNestedType[memberTypeIndex], 0, memberTypeIndex);
        }
        return memberTypes;
    }
    
    @Override
    public IBinaryMethod[] getMethods() {
        return this.methods;
    }
    
    @Override
    public char[][][] getMissingTypeNames() {
        return this.missingTypeNames;
    }
    
    @Override
    public int getModifiers() {
        int modifiers;
        if (this.innerInfo != null) {
            modifiers = (this.innerInfo.getModifiers() | (this.accessFlags & 0x100000) | (this.accessFlags & 0x1000));
        }
        else {
            modifiers = this.accessFlags;
        }
        return modifiers;
    }
    
    @Override
    public char[] getName() {
        return this.className;
    }
    
    @Override
    public char[] getSourceName() {
        if (this.sourceName != null) {
            return this.sourceName;
        }
        char[] name = this.getInnerSourceName();
        if (name == null) {
            name = this.getName();
            int start;
            if (this.isAnonymous()) {
                start = CharOperation.indexOf('$', name, CharOperation.lastIndexOf('/', name) + 1) + 1;
            }
            else {
                start = CharOperation.lastIndexOf('/', name) + 1;
            }
            if (start > 0) {
                final char[] newName = new char[name.length - start];
                System.arraycopy(name, start, newName, 0, newName.length);
                name = newName;
            }
        }
        return this.sourceName = name;
    }
    
    @Override
    public char[] getSuperclassName() {
        return this.superclassName;
    }
    
    @Override
    public long getTagBits() {
        return this.tagBits;
    }
    
    public long getVersion() {
        return this.version;
    }
    
    private boolean hasNonSyntheticFieldChanges(final FieldInfo[] currentFieldInfos, final FieldInfo[] otherFieldInfos) {
        final int length1 = (currentFieldInfos == null) ? 0 : currentFieldInfos.length;
        final int length2 = (otherFieldInfos == null) ? 0 : otherFieldInfos.length;
        int index1 = 0;
        int index2 = 0;
    Label_0134:
        while (index1 < length1) {
            if (index2 >= length2) {
                break;
            }
            while (currentFieldInfos[index1].isSynthetic()) {
                if (++index1 >= length1) {
                    break Label_0134;
                }
            }
            while (otherFieldInfos[index2].isSynthetic()) {
                if (++index2 >= length2) {
                    break Label_0134;
                }
            }
            if (this.hasStructuralFieldChanges(currentFieldInfos[index1++], otherFieldInfos[index2++])) {
                return true;
            }
        }
        while (index1 < length1) {
            if (!currentFieldInfos[index1++].isSynthetic()) {
                return true;
            }
        }
        while (index2 < length2) {
            if (!otherFieldInfos[index2++].isSynthetic()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasNonSyntheticMethodChanges(final MethodInfo[] currentMethodInfos, final MethodInfo[] otherMethodInfos) {
        final int length1 = (currentMethodInfos == null) ? 0 : currentMethodInfos.length;
        final int length2 = (otherMethodInfos == null) ? 0 : otherMethodInfos.length;
        int index1 = 0;
        int index2 = 0;
    Label_0167:
        while (index1 < length1) {
            if (index2 >= length2) {
                break;
            }
            MethodInfo m;
            while ((m = currentMethodInfos[index1]).isSynthetic() || m.isClinit()) {
                if (++index1 >= length1) {
                    break Label_0167;
                }
            }
            while ((m = otherMethodInfos[index2]).isSynthetic() || m.isClinit()) {
                if (++index2 >= length2) {
                    break Label_0167;
                }
            }
            if (this.hasStructuralMethodChanges(currentMethodInfos[index1++], otherMethodInfos[index2++])) {
                return true;
            }
        }
        while (index1 < length1) {
            final MethodInfo m;
            if (!(m = currentMethodInfos[index1++]).isSynthetic() && !m.isClinit()) {
                return true;
            }
        }
        while (index2 < length2) {
            final MethodInfo m;
            if (!(m = otherMethodInfos[index2++]).isSynthetic() && !m.isClinit()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasStructuralChanges(final byte[] newBytes) {
        return this.hasStructuralChanges(newBytes, true, true);
    }
    
    public boolean hasStructuralChanges(final byte[] newBytes, final boolean orderRequired, final boolean excludesSynthetic) {
        try {
            final ClassFileReader newClassFile = new ClassFileReader(newBytes, this.classFileName);
            if (this.getModifiers() != newClassFile.getModifiers()) {
                return true;
            }
            final long OnlyStructuralTagBits = 27162300892971008L;
            if ((this.getTagBits() & OnlyStructuralTagBits) != (newClassFile.getTagBits() & OnlyStructuralTagBits)) {
                return true;
            }
            if (this.hasStructuralAnnotationChanges(this.getAnnotations(), newClassFile.getAnnotations())) {
                return true;
            }
            if (this.version >= 3407872L && this.hasStructuralTypeAnnotationChanges(this.getTypeAnnotations(), newClassFile.getTypeAnnotations())) {
                return true;
            }
            if (!CharOperation.equals(this.getGenericSignature(), newClassFile.getGenericSignature())) {
                return true;
            }
            if (!CharOperation.equals(this.getSuperclassName(), newClassFile.getSuperclassName())) {
                return true;
            }
            final char[][] newInterfacesNames = newClassFile.getInterfaceNames();
            if (this.interfaceNames != newInterfacesNames) {
                final int newInterfacesLength = (newInterfacesNames == null) ? 0 : newInterfacesNames.length;
                if (newInterfacesLength != this.interfacesCount) {
                    return true;
                }
                for (int i = 0, max = this.interfacesCount; i < max; ++i) {
                    if (!CharOperation.equals(this.interfaceNames[i], newInterfacesNames[i])) {
                        return true;
                    }
                }
            }
            final IBinaryNestedType[] currentMemberTypes = this.getMemberTypes();
            final IBinaryNestedType[] otherMemberTypes = newClassFile.getMemberTypes();
            if (currentMemberTypes != otherMemberTypes) {
                final int currentMemberTypeLength = (currentMemberTypes == null) ? 0 : currentMemberTypes.length;
                final int otherMemberTypeLength = (otherMemberTypes == null) ? 0 : otherMemberTypes.length;
                if (currentMemberTypeLength != otherMemberTypeLength) {
                    return true;
                }
                for (int j = 0; j < currentMemberTypeLength; ++j) {
                    if (!CharOperation.equals(currentMemberTypes[j].getName(), otherMemberTypes[j].getName()) || currentMemberTypes[j].getModifiers() != otherMemberTypes[j].getModifiers()) {
                        return true;
                    }
                }
            }
            final FieldInfo[] otherFieldInfos = (FieldInfo[])newClassFile.getFields();
            final int otherFieldInfosLength = (otherFieldInfos == null) ? 0 : otherFieldInfos.length;
            boolean compareFields = true;
            if (this.fieldsCount == otherFieldInfosLength) {
                int k;
                for (k = 0; k < this.fieldsCount && !this.hasStructuralFieldChanges(this.fields[k], otherFieldInfos[k]); ++k) {}
                if ((compareFields = (k != this.fieldsCount)) && !orderRequired && !excludesSynthetic) {
                    return true;
                }
            }
            if (compareFields) {
                if (this.fieldsCount != otherFieldInfosLength && !excludesSynthetic) {
                    return true;
                }
                if (orderRequired) {
                    if (this.fieldsCount != 0) {
                        Arrays.sort(this.fields);
                    }
                    if (otherFieldInfosLength != 0) {
                        Arrays.sort(otherFieldInfos);
                    }
                }
                if (excludesSynthetic) {
                    if (this.hasNonSyntheticFieldChanges(this.fields, otherFieldInfos)) {
                        return true;
                    }
                }
                else {
                    for (int k = 0; k < this.fieldsCount; ++k) {
                        if (this.hasStructuralFieldChanges(this.fields[k], otherFieldInfos[k])) {
                            return true;
                        }
                    }
                }
            }
            final MethodInfo[] otherMethodInfos = (MethodInfo[])newClassFile.getMethods();
            final int otherMethodInfosLength = (otherMethodInfos == null) ? 0 : otherMethodInfos.length;
            boolean compareMethods = true;
            if (this.methodsCount == otherMethodInfosLength) {
                int l;
                for (l = 0; l < this.methodsCount && !this.hasStructuralMethodChanges(this.methods[l], otherMethodInfos[l]); ++l) {}
                if ((compareMethods = (l != this.methodsCount)) && !orderRequired && !excludesSynthetic) {
                    return true;
                }
            }
            if (compareMethods) {
                if (this.methodsCount != otherMethodInfosLength && !excludesSynthetic) {
                    return true;
                }
                if (orderRequired) {
                    if (this.methodsCount != 0) {
                        Arrays.sort(this.methods);
                    }
                    if (otherMethodInfosLength != 0) {
                        Arrays.sort(otherMethodInfos);
                    }
                }
                if (excludesSynthetic) {
                    if (this.hasNonSyntheticMethodChanges(this.methods, otherMethodInfos)) {
                        return true;
                    }
                }
                else {
                    for (int l = 0; l < this.methodsCount; ++l) {
                        if (this.hasStructuralMethodChanges(this.methods[l], otherMethodInfos[l])) {
                            return true;
                        }
                    }
                }
            }
            final char[][][] missingTypes = this.getMissingTypeNames();
            final char[][][] newMissingTypes = newClassFile.getMissingTypeNames();
            if (missingTypes != null) {
                if (newMissingTypes == null) {
                    return true;
                }
                final int length = missingTypes.length;
                if (length != newMissingTypes.length) {
                    return true;
                }
                for (int m = 0; m < length; ++m) {
                    if (!CharOperation.equals(missingTypes[m], newMissingTypes[m])) {
                        return true;
                    }
                }
            }
            else if (newMissingTypes != null) {
                return true;
            }
            return false;
        }
        catch (final ClassFormatException ex) {
            return true;
        }
    }
    
    private boolean hasStructuralAnnotationChanges(final IBinaryAnnotation[] currentAnnotations, final IBinaryAnnotation[] otherAnnotations) {
        if (currentAnnotations == otherAnnotations) {
            return false;
        }
        final int currentAnnotationsLength = (currentAnnotations == null) ? 0 : currentAnnotations.length;
        final int otherAnnotationsLength = (otherAnnotations == null) ? 0 : otherAnnotations.length;
        if (currentAnnotationsLength != otherAnnotationsLength) {
            return true;
        }
        for (int i = 0; i < currentAnnotationsLength; ++i) {
            final Boolean match = this.matchAnnotations(currentAnnotations[i], otherAnnotations[i]);
            if (match != null) {
                return match;
            }
        }
        return false;
    }
    
    private Boolean matchAnnotations(final IBinaryAnnotation currentAnnotation, final IBinaryAnnotation otherAnnotation) {
        if (!CharOperation.equals(currentAnnotation.getTypeName(), otherAnnotation.getTypeName())) {
            return true;
        }
        final IBinaryElementValuePair[] currentPairs = currentAnnotation.getElementValuePairs();
        final IBinaryElementValuePair[] otherPairs = otherAnnotation.getElementValuePairs();
        final int currentPairsLength = (currentPairs == null) ? 0 : currentPairs.length;
        final int otherPairsLength = (otherPairs == null) ? 0 : otherPairs.length;
        if (currentPairsLength != otherPairsLength) {
            return Boolean.TRUE;
        }
        int j = 0;
        while (j < currentPairsLength) {
            if (!CharOperation.equals(currentPairs[j].getName(), otherPairs[j].getName())) {
                return Boolean.TRUE;
            }
            final Object value = currentPairs[j].getValue();
            final Object value2 = otherPairs[j].getValue();
            if (value instanceof Object[]) {
                final Object[] currentValues = (Object[])value;
                if (!(value2 instanceof Object[])) {
                    return Boolean.TRUE;
                }
                final Object[] currentValues2 = (Object[])value2;
                final int length = currentValues.length;
                if (length != currentValues2.length) {
                    return Boolean.TRUE;
                }
                for (int n = 0; n < length; ++n) {
                    if (!currentValues[n].equals(currentValues2[n])) {
                        return Boolean.TRUE;
                    }
                }
                return Boolean.FALSE;
            }
            else {
                if (!value.equals(value2)) {
                    return Boolean.TRUE;
                }
                ++j;
            }
        }
        return null;
    }
    
    private boolean hasStructuralFieldChanges(final FieldInfo currentFieldInfo, final FieldInfo otherFieldInfo) {
        if (!CharOperation.equals(currentFieldInfo.getGenericSignature(), otherFieldInfo.getGenericSignature())) {
            return true;
        }
        if (currentFieldInfo.getModifiers() != otherFieldInfo.getModifiers()) {
            return true;
        }
        if ((currentFieldInfo.getTagBits() & 0x400000000000L) != (otherFieldInfo.getTagBits() & 0x400000000000L)) {
            return true;
        }
        if (this.hasStructuralAnnotationChanges(currentFieldInfo.getAnnotations(), otherFieldInfo.getAnnotations())) {
            return true;
        }
        if (this.version >= 3407872L && this.hasStructuralTypeAnnotationChanges(currentFieldInfo.getTypeAnnotations(), otherFieldInfo.getTypeAnnotations())) {
            return true;
        }
        if (!CharOperation.equals(currentFieldInfo.getName(), otherFieldInfo.getName())) {
            return true;
        }
        if (!CharOperation.equals(currentFieldInfo.getTypeName(), otherFieldInfo.getTypeName())) {
            return true;
        }
        if (currentFieldInfo.hasConstant() != otherFieldInfo.hasConstant()) {
            return true;
        }
        if (currentFieldInfo.hasConstant()) {
            final Constant currentConstant = currentFieldInfo.getConstant();
            final Constant otherConstant = otherFieldInfo.getConstant();
            if (currentConstant.typeID() != otherConstant.typeID()) {
                return true;
            }
            if (!currentConstant.getClass().equals(otherConstant.getClass())) {
                return true;
            }
            switch (currentConstant.typeID()) {
                case 10: {
                    return currentConstant.intValue() != otherConstant.intValue();
                }
                case 3: {
                    return currentConstant.byteValue() != otherConstant.byteValue();
                }
                case 4: {
                    return currentConstant.shortValue() != otherConstant.shortValue();
                }
                case 2: {
                    return currentConstant.charValue() != otherConstant.charValue();
                }
                case 7: {
                    return currentConstant.longValue() != otherConstant.longValue();
                }
                case 9: {
                    return currentConstant.floatValue() != otherConstant.floatValue();
                }
                case 8: {
                    return currentConstant.doubleValue() != otherConstant.doubleValue();
                }
                case 5: {
                    return currentConstant.booleanValue() ^ otherConstant.booleanValue();
                }
                case 11: {
                    return !currentConstant.stringValue().equals(otherConstant.stringValue());
                }
            }
        }
        return false;
    }
    
    private boolean hasStructuralMethodChanges(final MethodInfo currentMethodInfo, final MethodInfo otherMethodInfo) {
        if (!CharOperation.equals(currentMethodInfo.getGenericSignature(), otherMethodInfo.getGenericSignature())) {
            return true;
        }
        if (currentMethodInfo.getModifiers() != otherMethodInfo.getModifiers()) {
            return true;
        }
        if ((currentMethodInfo.getTagBits() & 0x400000000000L) != (otherMethodInfo.getTagBits() & 0x400000000000L)) {
            return true;
        }
        if (this.hasStructuralAnnotationChanges(currentMethodInfo.getAnnotations(), otherMethodInfo.getAnnotations())) {
            return true;
        }
        final int currentAnnotatedParamsCount = currentMethodInfo.getAnnotatedParametersCount();
        final int otherAnnotatedParamsCount = otherMethodInfo.getAnnotatedParametersCount();
        if (currentAnnotatedParamsCount != otherAnnotatedParamsCount) {
            return true;
        }
        for (int i = 0; i < currentAnnotatedParamsCount; ++i) {
            if (this.hasStructuralAnnotationChanges(currentMethodInfo.getParameterAnnotations(i, this.classFileName), otherMethodInfo.getParameterAnnotations(i, this.classFileName))) {
                return true;
            }
        }
        if (this.version >= 3407872L && this.hasStructuralTypeAnnotationChanges(currentMethodInfo.getTypeAnnotations(), otherMethodInfo.getTypeAnnotations())) {
            return true;
        }
        if (!CharOperation.equals(currentMethodInfo.getSelector(), otherMethodInfo.getSelector())) {
            return true;
        }
        if (!CharOperation.equals(currentMethodInfo.getMethodDescriptor(), otherMethodInfo.getMethodDescriptor())) {
            return true;
        }
        if (!CharOperation.equals(currentMethodInfo.getGenericSignature(), otherMethodInfo.getGenericSignature())) {
            return true;
        }
        final char[][] currentThrownExceptions = currentMethodInfo.getExceptionTypeNames();
        final char[][] otherThrownExceptions = otherMethodInfo.getExceptionTypeNames();
        if (currentThrownExceptions != otherThrownExceptions) {
            final int currentThrownExceptionsLength = (currentThrownExceptions == null) ? 0 : currentThrownExceptions.length;
            final int otherThrownExceptionsLength = (otherThrownExceptions == null) ? 0 : otherThrownExceptions.length;
            if (currentThrownExceptionsLength != otherThrownExceptionsLength) {
                return true;
            }
            for (int k = 0; k < currentThrownExceptionsLength; ++k) {
                if (!CharOperation.equals(currentThrownExceptions[k], otherThrownExceptions[k])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean hasStructuralTypeAnnotationChanges(final IBinaryTypeAnnotation[] currentTypeAnnotations, IBinaryTypeAnnotation[] otherTypeAnnotations) {
        if (otherTypeAnnotations != null) {
            final int len = otherTypeAnnotations.length;
            System.arraycopy(otherTypeAnnotations, 0, otherTypeAnnotations = new IBinaryTypeAnnotation[len], 0, len);
        }
        if (currentTypeAnnotations != null) {
            for (final IBinaryTypeAnnotation currentAnnotation : currentTypeAnnotations) {
                Label_0120: {
                    if (this.affectsSignature(currentAnnotation)) {
                        if (otherTypeAnnotations == null) {
                            return true;
                        }
                        for (int i = 0; i < otherTypeAnnotations.length; ++i) {
                            final IBinaryTypeAnnotation otherAnnotation = otherTypeAnnotations[i];
                            if (otherAnnotation != null && this.matchAnnotations(currentAnnotation.getAnnotation(), otherAnnotation.getAnnotation()) == Boolean.TRUE) {
                                otherTypeAnnotations[i] = null;
                                break Label_0120;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        if (otherTypeAnnotations != null) {
            IBinaryTypeAnnotation[] array;
            for (int length2 = (array = otherTypeAnnotations).length, k = 0; k < length2; ++k) {
                final IBinaryTypeAnnotation otherAnnotation2 = array[k];
                if (this.affectsSignature(otherAnnotation2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean affectsSignature(final IBinaryTypeAnnotation typeAnnotation) {
        if (typeAnnotation == null) {
            return false;
        }
        final int targetType = typeAnnotation.getTargetType();
        return targetType < 64 || targetType > 75;
    }
    
    private void initialize() throws ClassFormatException {
        try {
            for (int i = 0, max = this.fieldsCount; i < max; ++i) {
                this.fields[i].initialize();
            }
            for (int i = 0, max = this.methodsCount; i < max; ++i) {
                this.methods[i].initialize();
            }
            if (this.innerInfos != null) {
                for (int i = 0, max = this.innerInfos.length; i < max; ++i) {
                    this.innerInfos[i].initialize();
                }
            }
            if (this.annotations != null) {
                for (int i = 0, max = this.annotations.length; i < max; ++i) {
                    this.annotations[i].initialize();
                }
            }
            this.getEnclosingMethod();
            this.reset();
        }
        catch (final RuntimeException e) {
            final ClassFormatException exception = new ClassFormatException(e, this.classFileName);
            throw exception;
        }
    }
    
    @Override
    public boolean isAnonymous() {
        if (this.innerInfo == null) {
            return false;
        }
        final char[] innerSourceName = this.innerInfo.getSourceName();
        return innerSourceName == null || innerSourceName.length == 0;
    }
    
    @Override
    public boolean isBinaryType() {
        return true;
    }
    
    @Override
    public boolean isLocal() {
        if (this.innerInfo == null) {
            return false;
        }
        if (this.innerInfo.getEnclosingTypeName() != null) {
            return false;
        }
        final char[] innerSourceName = this.innerInfo.getSourceName();
        return innerSourceName != null && innerSourceName.length > 0;
    }
    
    @Override
    public boolean isMember() {
        if (this.innerInfo == null) {
            return false;
        }
        if (this.innerInfo.getEnclosingTypeName() == null) {
            return false;
        }
        final char[] innerSourceName = this.innerInfo.getSourceName();
        return innerSourceName != null && innerSourceName.length > 0;
    }
    
    public boolean isNestedType() {
        return this.innerInfo != null;
    }
    
    @Override
    public char[] sourceFileName() {
        return this.sourceFileName;
    }
    
    @Override
    public String toString() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintWriter print = new PrintWriter(out);
        print.println(String.valueOf(this.getClass().getName()) + "{");
        print.println(" this.className: " + new String(this.getName()));
        print.println(" this.superclassName: " + ((this.getSuperclassName() == null) ? "null" : new String(this.getSuperclassName())));
        print.println(" access_flags: " + printTypeModifiers(this.accessFlags()) + "(" + this.accessFlags() + ")");
        print.flush();
        return out.toString();
    }
    
    public interface ZipFileProducer
    {
        ZipFile produce() throws IOException;
    }
}
