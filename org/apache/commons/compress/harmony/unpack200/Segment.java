package org.apache.commons.compress.harmony.unpack200;

import java.util.zip.ZipEntry;
import java.util.TimeZone;
import java.util.zip.CRC32;
import java.util.jar.JarEntry;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.util.jar.JarOutputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.InnerClassesAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPMethod;
import java.util.List;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPField;
import java.util.ArrayList;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.SourceFileAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFile;
import java.io.InputStream;
import java.io.PrintWriter;

public class Segment
{
    public static final int LOG_LEVEL_VERBOSE = 2;
    public static final int LOG_LEVEL_STANDARD = 1;
    public static final int LOG_LEVEL_QUIET = 0;
    private SegmentHeader header;
    private CpBands cpBands;
    private AttrDefinitionBands attrDefinitionBands;
    private IcBands icBands;
    private ClassBands classBands;
    private BcBands bcBands;
    private FileBands fileBands;
    private boolean overrideDeflateHint;
    private boolean deflateHint;
    private boolean doPreRead;
    private int logLevel;
    private PrintWriter logStream;
    private byte[][] classFilesContents;
    private boolean[] fileDeflate;
    private boolean[] fileIsClass;
    private InputStream internalBuffer;
    
    private ClassFile buildClassFile(final int classNum) throws Pack200Exception {
        final ClassFile classFile = new ClassFile();
        final int[] major = this.classBands.getClassVersionMajor();
        final int[] minor = this.classBands.getClassVersionMinor();
        if (major != null) {
            classFile.major = major[classNum];
            classFile.minor = minor[classNum];
        }
        else {
            classFile.major = this.header.getDefaultClassMajorVersion();
            classFile.minor = this.header.getDefaultClassMinorVersion();
        }
        final ClassConstantPool cp = classFile.pool;
        final int fullNameIndexInCpClass = this.classBands.getClassThisInts()[classNum];
        final String fullName = this.cpBands.getCpClass()[fullNameIndexInCpClass];
        int i = fullName.lastIndexOf("/") + 1;
        final ArrayList classAttributes = this.classBands.getClassAttributes()[classNum];
        SourceFileAttribute sourceFileAttribute = null;
        for (int index = 0; index < classAttributes.size(); ++index) {
            if (classAttributes.get(index).isSourceFileAttribute()) {
                sourceFileAttribute = classAttributes.get(index);
            }
        }
        if (sourceFileAttribute == null) {
            final AttributeLayout SOURCE_FILE = this.attrDefinitionBands.getAttributeDefinitionMap().getAttributeLayout("SourceFile", 0);
            if (SOURCE_FILE.matches(this.classBands.getRawClassFlags()[classNum])) {
                int firstDollar = -1;
                for (int index2 = 0; index2 < fullName.length(); ++index2) {
                    if (fullName.charAt(index2) <= '$') {
                        firstDollar = index2;
                    }
                }
                String fileName = null;
                if (firstDollar > -1 && i <= firstDollar) {
                    fileName = fullName.substring(i, firstDollar) + ".java";
                }
                else {
                    fileName = fullName.substring(i) + ".java";
                }
                sourceFileAttribute = new SourceFileAttribute(this.cpBands.cpUTF8Value(fileName, false));
                classFile.attributes = new Attribute[] { (Attribute)cp.add(sourceFileAttribute) };
            }
            else {
                classFile.attributes = new Attribute[0];
            }
        }
        else {
            classFile.attributes = new Attribute[] { (Attribute)cp.add(sourceFileAttribute) };
        }
        final ArrayList classAttributesWithoutSourceFileAttribute = new ArrayList(classAttributes.size());
        for (int index3 = 0; index3 < classAttributes.size(); ++index3) {
            final Attribute attrib = classAttributes.get(index3);
            if (!attrib.isSourceFileAttribute()) {
                classAttributesWithoutSourceFileAttribute.add(attrib);
            }
        }
        final Attribute[] originalAttributes = classFile.attributes;
        System.arraycopy(originalAttributes, 0, classFile.attributes = new Attribute[originalAttributes.length + classAttributesWithoutSourceFileAttribute.size()], 0, originalAttributes.length);
        for (int index2 = 0; index2 < classAttributesWithoutSourceFileAttribute.size(); ++index2) {
            final Attribute attrib2 = classAttributesWithoutSourceFileAttribute.get(index2);
            cp.add(attrib2);
            classFile.attributes[originalAttributes.length + index2] = attrib2;
        }
        final ClassFileEntry cfThis = cp.add(this.cpBands.cpClassValue(fullNameIndexInCpClass));
        final ClassFileEntry cfSuper = cp.add(this.cpBands.cpClassValue(this.classBands.getClassSuperInts()[classNum]));
        ClassFileEntry[] cfInterfaces;
        for (cfInterfaces = new ClassFileEntry[this.classBands.getClassInterfacesInts()[classNum].length], i = 0; i < cfInterfaces.length; ++i) {
            cfInterfaces[i] = cp.add(this.cpBands.cpClassValue(this.classBands.getClassInterfacesInts()[classNum][i]));
        }
        ClassFileEntry[] cfFields;
        int descriptorIndex;
        int nameIndex;
        int typeIndex;
        CPUTF8 name;
        CPUTF8 descriptor;
        for (cfFields = new ClassFileEntry[this.classBands.getClassFieldCount()[classNum]], i = 0; i < cfFields.length; ++i) {
            descriptorIndex = this.classBands.getFieldDescrInts()[classNum][i];
            nameIndex = this.cpBands.getCpDescriptorNameInts()[descriptorIndex];
            typeIndex = this.cpBands.getCpDescriptorTypeInts()[descriptorIndex];
            name = this.cpBands.cpUTF8Value(nameIndex);
            descriptor = this.cpBands.cpSignatureValue(typeIndex);
            cfFields[i] = cp.add(new CPField(name, descriptor, this.classBands.getFieldFlags()[classNum][i], this.classBands.getFieldAttributes()[classNum][i]));
        }
        ClassFileEntry[] cfMethods;
        int descriptorIndex2;
        int nameIndex2;
        int typeIndex2;
        CPUTF8 name2;
        CPUTF8 descriptor2;
        for (cfMethods = new ClassFileEntry[this.classBands.getClassMethodCount()[classNum]], i = 0; i < cfMethods.length; ++i) {
            descriptorIndex2 = this.classBands.getMethodDescrInts()[classNum][i];
            nameIndex2 = this.cpBands.getCpDescriptorNameInts()[descriptorIndex2];
            typeIndex2 = this.cpBands.getCpDescriptorTypeInts()[descriptorIndex2];
            name2 = this.cpBands.cpUTF8Value(nameIndex2);
            descriptor2 = this.cpBands.cpSignatureValue(typeIndex2);
            cfMethods[i] = cp.add(new CPMethod(name2, descriptor2, this.classBands.getMethodFlags()[classNum][i], this.classBands.getMethodAttributes()[classNum][i]));
        }
        cp.addNestedEntries();
        boolean addInnerClassesAttr = false;
        final IcTuple[] ic_local = this.getClassBands().getIcLocal()[classNum];
        final boolean ic_local_sent = ic_local != null;
        final InnerClassesAttribute innerClassesAttribute = new InnerClassesAttribute("InnerClasses");
        final IcTuple[] ic_relevant = this.getIcBands().getRelevantIcTuples(fullName, cp);
        final List ic_stored = this.computeIcStored(ic_local, ic_relevant);
        for (int index4 = 0; index4 < ic_stored.size(); ++index4) {
            final IcTuple icStored = ic_stored.get(index4);
            final int innerClassIndex = icStored.thisClassIndex();
            final int outerClassIndex = icStored.outerClassIndex();
            final int simpleClassNameIndex = icStored.simpleClassNameIndex();
            final String innerClassString = icStored.thisClassString();
            final String outerClassString = icStored.outerClassString();
            final String simpleClassName = icStored.simpleClassName();
            CPClass innerClass = null;
            CPUTF8 innerName = null;
            CPClass outerClass = null;
            innerClass = ((innerClassIndex != -1) ? this.cpBands.cpClassValue(innerClassIndex) : this.cpBands.cpClassValue(innerClassString));
            if (!icStored.isAnonymous()) {
                innerName = ((simpleClassNameIndex != -1) ? this.cpBands.cpUTF8Value(simpleClassNameIndex) : this.cpBands.cpUTF8Value(simpleClassName));
            }
            if (icStored.isMember()) {
                outerClass = ((outerClassIndex != -1) ? this.cpBands.cpClassValue(outerClassIndex) : this.cpBands.cpClassValue(outerClassString));
            }
            final int flags = icStored.F;
            innerClassesAttribute.addInnerClassesEntry(innerClass, outerClass, innerName, flags);
            addInnerClassesAttr = true;
        }
        if (ic_local_sent && ic_local.length == 0) {
            addInnerClassesAttr = false;
        }
        if (!ic_local_sent && ic_relevant.length == 0) {
            addInnerClassesAttr = false;
        }
        if (addInnerClassesAttr) {
            final Attribute[] originalAttrs = classFile.attributes;
            final Attribute[] newAttrs = new Attribute[originalAttrs.length + 1];
            for (int index5 = 0; index5 < originalAttrs.length; ++index5) {
                newAttrs[index5] = originalAttrs[index5];
            }
            newAttrs[newAttrs.length - 1] = innerClassesAttribute;
            classFile.attributes = newAttrs;
            cp.addWithNestedEntries(innerClassesAttribute);
        }
        cp.resolve(this);
        classFile.accessFlags = (int)this.classBands.getClassFlags()[classNum];
        classFile.thisClass = cp.indexOf(cfThis);
        classFile.superClass = cp.indexOf(cfSuper);
        classFile.interfaces = new int[cfInterfaces.length];
        for (i = 0; i < cfInterfaces.length; ++i) {
            classFile.interfaces[i] = cp.indexOf(cfInterfaces[i]);
        }
        classFile.fields = cfFields;
        classFile.methods = cfMethods;
        return classFile;
    }
    
    private List computeIcStored(final IcTuple[] ic_local, final IcTuple[] ic_relevant) {
        final List result = new ArrayList(ic_relevant.length);
        final List duplicates = new ArrayList(ic_relevant.length);
        final Set isInResult = new HashSet(ic_relevant.length);
        if (ic_local != null) {
            for (int index = 0; index < ic_local.length; ++index) {
                if (isInResult.add(ic_local[index])) {
                    result.add(ic_local[index]);
                }
            }
        }
        for (int index = 0; index < ic_relevant.length; ++index) {
            if (isInResult.add(ic_relevant[index])) {
                result.add(ic_relevant[index]);
            }
            else {
                duplicates.add(ic_relevant[index]);
            }
        }
        for (int index = 0; index < duplicates.size(); ++index) {
            final IcTuple tuple = duplicates.get(index);
            result.remove(tuple);
        }
        return result;
    }
    
    private void readSegment(final InputStream in) throws IOException, Pack200Exception {
        this.log(2, "-------");
        (this.cpBands = new CpBands(this)).read(in);
        (this.attrDefinitionBands = new AttrDefinitionBands(this)).read(in);
        (this.icBands = new IcBands(this)).read(in);
        (this.classBands = new ClassBands(this)).read(in);
        (this.bcBands = new BcBands(this)).read(in);
        (this.fileBands = new FileBands(this)).read(in);
        this.fileBands.processFileBits();
    }
    
    private void parseSegment() throws IOException, Pack200Exception {
        this.header.unpack();
        this.cpBands.unpack();
        this.attrDefinitionBands.unpack();
        this.icBands.unpack();
        this.classBands.unpack();
        this.bcBands.unpack();
        this.fileBands.unpack();
        int classNum = 0;
        final int numberOfFiles = this.header.getNumberOfFiles();
        final String[] fileName = this.fileBands.getFileName();
        final int[] fileOptions = this.fileBands.getFileOptions();
        final SegmentOptions options = this.header.getOptions();
        this.classFilesContents = new byte[numberOfFiles][];
        this.fileDeflate = new boolean[numberOfFiles];
        this.fileIsClass = new boolean[numberOfFiles];
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        for (int i = 0; i < numberOfFiles; ++i) {
            String name = fileName[i];
            final boolean nameIsEmpty = name == null || name.equals("");
            final boolean isClass = (fileOptions[i] & 0x2) == 0x2 || nameIsEmpty;
            if (isClass && nameIsEmpty) {
                name = this.cpBands.getCpClass()[this.classBands.getClassThisInts()[classNum]] + ".class";
                fileName[i] = name;
            }
            if (!this.overrideDeflateHint) {
                this.fileDeflate[i] = ((fileOptions[i] & 0x1) == 0x1 || options.shouldDeflate());
            }
            else {
                this.fileDeflate[i] = this.deflateHint;
            }
            this.fileIsClass[i] = isClass;
            if (isClass) {
                final ClassFile classFile = this.buildClassFile(classNum);
                classFile.write(dos);
                dos.flush();
                this.classFilesContents[classNum] = bos.toByteArray();
                bos.reset();
                ++classNum;
            }
        }
    }
    
    public void unpack(final InputStream in, final JarOutputStream out) throws IOException, Pack200Exception {
        this.unpackRead(in);
        this.unpackProcess();
        this.unpackWrite(out);
    }
    
    void unpackRead(InputStream in) throws IOException, Pack200Exception {
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        (this.header = new SegmentHeader(this)).read(in);
        final int size = (int)this.header.getArchiveSize() - this.header.getArchiveSizeOffset();
        if (this.doPreRead && this.header.getArchiveSize() != 0L) {
            final byte[] data = new byte[size];
            in.read(data);
            this.internalBuffer = new BufferedInputStream(new ByteArrayInputStream(data));
        }
        else {
            this.readSegment(in);
        }
    }
    
    void unpackProcess() throws IOException, Pack200Exception {
        if (this.internalBuffer != null) {
            this.readSegment(this.internalBuffer);
        }
        this.parseSegment();
    }
    
    void unpackWrite(final JarOutputStream out) throws IOException, Pack200Exception {
        this.writeJar(out);
        if (this.logStream != null) {
            this.logStream.close();
        }
    }
    
    public void writeJar(final JarOutputStream out) throws IOException, Pack200Exception {
        final String[] fileName = this.fileBands.getFileName();
        final int[] fileModtime = this.fileBands.getFileModtime();
        final long[] fileSize = this.fileBands.getFileSize();
        final byte[][] fileBits = this.fileBands.getFileBits();
        int classNum = 0;
        final int numberOfFiles = this.header.getNumberOfFiles();
        final long archiveModtime = this.header.getArchiveModtime();
        for (int i = 0; i < numberOfFiles; ++i) {
            final String name = fileName[i];
            final long modtime = 1000L * (archiveModtime + fileModtime[i]);
            final boolean deflate = this.fileDeflate[i];
            final JarEntry entry = new JarEntry(name);
            if (deflate) {
                entry.setMethod(8);
            }
            else {
                entry.setMethod(0);
                final CRC32 crc = new CRC32();
                if (this.fileIsClass[i]) {
                    crc.update(this.classFilesContents[classNum]);
                    entry.setSize(this.classFilesContents[classNum].length);
                }
                else {
                    crc.update(fileBits[i]);
                    entry.setSize(fileSize[i]);
                }
                entry.setCrc(crc.getValue());
            }
            entry.setTime(modtime - TimeZone.getDefault().getRawOffset());
            out.putNextEntry(entry);
            if (this.fileIsClass[i]) {
                entry.setSize(this.classFilesContents[classNum].length);
                out.write(this.classFilesContents[classNum]);
                ++classNum;
            }
            else {
                entry.setSize(fileSize[i]);
                out.write(fileBits[i]);
            }
        }
    }
    
    public SegmentConstantPool getConstantPool() {
        return this.cpBands.getConstantPool();
    }
    
    public SegmentHeader getSegmentHeader() {
        return this.header;
    }
    
    public void setPreRead(final boolean value) {
        this.doPreRead = value;
    }
    
    protected AttrDefinitionBands getAttrDefinitionBands() {
        return this.attrDefinitionBands;
    }
    
    protected ClassBands getClassBands() {
        return this.classBands;
    }
    
    protected CpBands getCpBands() {
        return this.cpBands;
    }
    
    protected IcBands getIcBands() {
        return this.icBands;
    }
    
    public void setLogLevel(final int logLevel) {
        this.logLevel = logLevel;
    }
    
    public void setLogStream(final OutputStream logStream) {
        this.logStream = new PrintWriter(logStream);
    }
    
    public void log(final int logLevel, final String message) {
        if (this.logLevel >= logLevel) {
            this.logStream.println(message);
        }
    }
    
    public void overrideDeflateHint(final boolean deflateHint) {
        this.overrideDeflateHint = true;
        this.deflateHint = deflateHint;
    }
}
