package org.apache.xmlbeans.impl.tool;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.util.HexBin;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import java.math.BigInteger;
import org.apache.xmlbeans.QNameSet;
import javax.xml.namespace.QName;
import java.util.Enumeration;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileFilter;
import java.io.File;
import org.apache.xmlbeans.XmlOptions;
import java.io.DataInputStream;
import java.io.PrintStream;

public class XsbDumper
{
    private String _indent;
    private PrintStream _out;
    public static final int DATA_BABE = -629491010;
    public static final int MAJOR_VERSION = 2;
    public static final int MINOR_VERSION = 24;
    public static final int FILETYPE_SCHEMAINDEX = 1;
    public static final int FILETYPE_SCHEMATYPE = 2;
    public static final int FILETYPE_SCHEMAELEMENT = 3;
    public static final int FILETYPE_SCHEMAATTRIBUTE = 4;
    public static final int FILETYPE_SCHEMAPOINTER = 5;
    public static final int FILETYPE_SCHEMAMODELGROUP = 6;
    public static final int FILETYPE_SCHEMAATTRIBUTEGROUP = 7;
    public static final int FLAG_PART_SKIPPABLE = 1;
    public static final int FLAG_PART_FIXED = 4;
    public static final int FLAG_PART_NILLABLE = 8;
    public static final int FLAG_PART_BLOCKEXT = 16;
    public static final int FLAG_PART_BLOCKREST = 32;
    public static final int FLAG_PART_BLOCKSUBST = 64;
    public static final int FLAG_PART_ABSTRACT = 128;
    public static final int FLAG_PART_FINALEXT = 256;
    public static final int FLAG_PART_FINALREST = 512;
    public static final int FLAG_PROP_ISATTR = 1;
    public static final int FLAG_PROP_JAVASINGLETON = 2;
    public static final int FLAG_PROP_JAVAOPTIONAL = 4;
    public static final int FLAG_PROP_JAVAARRAY = 8;
    public static final int FIELD_NONE = 0;
    public static final int FIELD_GLOBAL = 1;
    public static final int FIELD_LOCALATTR = 2;
    public static final int FIELD_LOCALELT = 3;
    static final int FLAG_SIMPLE_TYPE = 1;
    static final int FLAG_DOCUMENT_TYPE = 2;
    static final int FLAG_ORDERED = 4;
    static final int FLAG_BOUNDED = 8;
    static final int FLAG_FINITE = 16;
    static final int FLAG_NUMERIC = 32;
    static final int FLAG_STRINGENUM = 64;
    static final int FLAG_UNION_OF_LISTS = 128;
    static final int FLAG_HAS_PATTERN = 256;
    static final int FLAG_ORDER_SENSITIVE = 512;
    static final int FLAG_TOTAL_ORDER = 1024;
    static final int FLAG_COMPILED = 2048;
    static final int FLAG_BLOCK_EXT = 4096;
    static final int FLAG_BLOCK_REST = 8192;
    static final int FLAG_FINAL_EXT = 16384;
    static final int FLAG_FINAL_REST = 32768;
    static final int FLAG_FINAL_UNION = 65536;
    static final int FLAG_FINAL_LIST = 131072;
    static final int FLAG_ABSTRACT = 262144;
    static final int FLAG_ATTRIBUTE_TYPE = 524288;
    DataInputStream _input;
    StringPool _stringPool;
    private static final XmlOptions prettyOptions;
    static final byte[] SINGLE_ZERO_BYTE;
    private int _majorver;
    private int _minorver;
    private int _releaseno;
    
    public static void printUsage() {
        System.out.println("Prints the contents of an XSB file in human-readable form.");
        System.out.println("An XSB file contains schema meta information needed to ");
        System.out.println("perform tasks such as binding and validation.");
        System.out.println("Usage: dumpxsb myfile.xsb");
        System.out.println("    myfile.xsb - Path to an XSB file.");
        System.out.println();
    }
    
    public static void main(final String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            dump(new File(args[i]), true);
        }
    }
    
    private static void dump(final File file, final boolean force) {
        if (file.isDirectory()) {
            final File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(final File file) {
                    return file.isDirectory() || (file.isFile() && file.getName().endsWith(".xsb"));
                }
            });
            for (int i = 0; i < files.length; ++i) {
                dump(files[i], false);
            }
        }
        else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
            dumpZip(file);
        }
        else {
            if (!force) {
                if (!file.getName().endsWith(".xsb")) {
                    return;
                }
            }
            try {
                System.out.println(file.toString());
                dump(new FileInputStream(file), "  ");
                System.out.println();
            }
            catch (final FileNotFoundException e) {
                System.out.println(e.toString());
            }
        }
    }
    
    public static void dumpZip(final File file) {
        try {
            final ZipFile zipFile = new ZipFile(file);
            final Enumeration e = zipFile.entries();
            while (e.hasMoreElements()) {
                final ZipEntry entry = e.nextElement();
                if (entry.getName().endsWith(".xsb")) {
                    System.out.println(entry.getName());
                    dump(zipFile.getInputStream(entry), "  ");
                    System.out.println();
                }
            }
        }
        catch (final IOException e2) {
            System.out.println(e2.toString());
        }
    }
    
    public static void dump(final InputStream input) {
        dump(input, "", System.out);
    }
    
    public static void dump(final InputStream input, final String indent) {
        dump(input, indent, System.out);
    }
    
    public static void dump(final InputStream input, final String indent, final PrintStream output) {
        final XsbDumper dumper = new XsbDumper(input, indent, output);
        dumper.dumpAll();
    }
    
    private XsbDumper(final InputStream stream, final String indent, final PrintStream ostream) {
        this._input = new DataInputStream(stream);
        this._indent = indent;
        this._out = ostream;
    }
    
    void flush() {
        this._out.flush();
    }
    
    void emit(final String str) {
        this._out.println(this._indent + str);
        this.flush();
    }
    
    void emit() {
        this._out.println();
        this.flush();
    }
    
    void error(final Exception e) {
        this._out.println(e.toString());
        this.flush();
        final IllegalStateException e2 = new IllegalStateException(e.getMessage());
        e2.initCause(e);
        throw e2;
    }
    
    void error(final String str) {
        this._out.println(str);
        this.flush();
        final IllegalStateException e2 = new IllegalStateException(str);
        throw e2;
    }
    
    void indent() {
        this._indent += "  ";
    }
    
    void outdent() {
        this._indent = this._indent.substring(0, this._indent.length() - 2);
    }
    
    static String filetypeString(final int code) {
        switch (code) {
            case 1: {
                return "FILETYPE_SCHEMAINDEX";
            }
            case 2: {
                return "FILETYPE_SCHEMATYPE";
            }
            case 3: {
                return "FILETYPE_SCHEMAELEMENT";
            }
            case 4: {
                return "FILETYPE_SCHEMAATTRIBUTE";
            }
            case 5: {
                return "FILETYPE_SCHEMAPOINTER";
            }
            case 6: {
                return "FILETYPE_SCHEMAMODELGROUP";
            }
            case 7: {
                return "FILETYPE_SCHEMAATTRIBUTEGROUP";
            }
            default: {
                return "Unknown FILETYPE (" + code + ")";
            }
        }
    }
    
    static String particleflagsString(final int flags) {
        final StringBuffer result = new StringBuffer();
        if ((flags & 0x1) != 0x0) {
            result.append("FLAG_PART_SKIPPABLE | ");
        }
        if ((flags & 0x4) != 0x0) {
            result.append("FLAG_PART_FIXED | ");
        }
        if ((flags & 0x8) != 0x0) {
            result.append("FLAG_PART_NILLABLE | ");
        }
        if ((flags & 0x10) != 0x0) {
            result.append("FLAG_PART_BLOCKEXT | ");
        }
        if ((flags & 0x20) != 0x0) {
            result.append("FLAG_PART_BLOCKREST | ");
        }
        if ((flags & 0x40) != 0x0) {
            result.append("FLAG_PART_BLOCKSUBST | ");
        }
        if ((flags & 0x80) != 0x0) {
            result.append("FLAG_PART_ABSTRACT | ");
        }
        if ((flags & 0x100) != 0x0) {
            result.append("FLAG_PART_FINALEXT | ");
        }
        if ((flags & 0x200) != 0x0) {
            result.append("FLAG_PART_FINALREST | ");
        }
        if (result.length() == 0) {
            result.append("0 | ");
        }
        return result.substring(0, result.length() - 3);
    }
    
    static String propertyflagsString(final int flags) {
        final StringBuffer result = new StringBuffer();
        if ((flags & 0x1) != 0x0) {
            result.append("FLAG_PROP_ISATTR | ");
        }
        if ((flags & 0x2) != 0x0) {
            result.append("FLAG_PROP_JAVASINGLETON | ");
        }
        if ((flags & 0x4) != 0x0) {
            result.append("FLAG_PROP_JAVAOPTIONAL | ");
        }
        if ((flags & 0x8) != 0x0) {
            result.append("FLAG_PROP_JAVAARRAY | ");
        }
        if (result.length() == 0) {
            result.append("0 | ");
        }
        return result.substring(0, result.length() - 3);
    }
    
    static String containerfieldTypeString(final int code) {
        switch (code) {
            case 0: {
                return "FIELD_NONE";
            }
            case 1: {
                return "FIELD_GLOBAL";
            }
            case 2: {
                return "FIELD_LOCALATTR";
            }
            case 3: {
                return "FIELD_LOCALELT";
            }
            default: {
                return "Unknown container field type (" + code + ")";
            }
        }
    }
    
    static String typeflagsString(final int flags) {
        final StringBuffer result = new StringBuffer();
        if ((flags & 0x1) != 0x0) {
            result.append("FLAG_SIMPLE_TYPE | ");
        }
        if ((flags & 0x2) != 0x0) {
            result.append("FLAG_DOCUMENT_TYPE | ");
        }
        if ((flags & 0x80000) != 0x0) {
            result.append("FLAG_ATTRIBUTE_TYPE | ");
        }
        if ((flags & 0x4) != 0x0) {
            result.append("FLAG_ORDERED | ");
        }
        if ((flags & 0x8) != 0x0) {
            result.append("FLAG_BOUNDED | ");
        }
        if ((flags & 0x10) != 0x0) {
            result.append("FLAG_FINITE | ");
        }
        if ((flags & 0x20) != 0x0) {
            result.append("FLAG_NUMERIC | ");
        }
        if ((flags & 0x40) != 0x0) {
            result.append("FLAG_STRINGENUM | ");
        }
        if ((flags & 0x80) != 0x0) {
            result.append("FLAG_UNION_OF_LISTS | ");
        }
        if ((flags & 0x100) != 0x0) {
            result.append("FLAG_HAS_PATTERN | ");
        }
        if ((flags & 0x400) != 0x0) {
            result.append("FLAG_TOTAL_ORDER | ");
        }
        if ((flags & 0x800) != 0x0) {
            result.append("FLAG_COMPILED | ");
        }
        if ((flags & 0x1000) != 0x0) {
            result.append("FLAG_BLOCK_EXT | ");
        }
        if ((flags & 0x2000) != 0x0) {
            result.append("FLAG_BLOCK_REST | ");
        }
        if ((flags & 0x4000) != 0x0) {
            result.append("FLAG_FINAL_EXT | ");
        }
        if ((flags & 0x8000) != 0x0) {
            result.append("FLAG_FINAL_REST | ");
        }
        if ((flags & 0x10000) != 0x0) {
            result.append("FLAG_FINAL_UNION | ");
        }
        if ((flags & 0x20000) != 0x0) {
            result.append("FLAG_FINAL_LIST | ");
        }
        if ((flags & 0x40000) != 0x0) {
            result.append("FLAG_ABSTRACT | ");
        }
        if (result.length() == 0) {
            result.append("0 | ");
        }
        return result.substring(0, result.length() - 3);
    }
    
    void dumpAll() {
        final int filetype = this.dumpHeader();
        switch (filetype) {
            case 1: {
                this.dumpIndexData();
                return;
            }
            case 2: {
                this.dumpTypeFileData();
                break;
            }
            case 3: {
                this.dumpParticleData(true);
                break;
            }
            case 4: {
                this.dumpAttributeData(true);
                break;
            }
            case 5: {
                this.dumpPointerData();
                break;
            }
            case 6: {
                this.dumpModelGroupData();
                break;
            }
            case 7: {
                this.dumpAttributeGroupData();
                break;
            }
        }
        this.readEnd();
    }
    
    static String hex32String(final int i) {
        return Integer.toHexString(i);
    }
    
    protected int dumpHeader() {
        final int magic = this.readInt();
        this.emit("Magic cookie: " + hex32String(magic));
        if (magic != -629491010) {
            this.emit("Wrong magic cookie.");
            return 0;
        }
        this._majorver = this.readShort();
        this._minorver = this.readShort();
        if (this.atLeast(2, 18, 0)) {
            this._releaseno = this.readShort();
        }
        this.emit("Major version: " + this._majorver);
        this.emit("Minor version: " + this._minorver);
        this.emit("Release number: " + this._releaseno);
        if (this._majorver != 2 || this._minorver > 24) {
            this.emit("Incompatible version.");
            return 0;
        }
        final int actualfiletype = this.readShort();
        this.emit("Filetype: " + filetypeString(actualfiletype));
        (this._stringPool = new StringPool()).readFrom(this._input);
        return actualfiletype;
    }
    
    void dumpPointerData() {
        this.emit("Type system: " + this.readString());
    }
    
    protected void dumpIndexData() {
        final int size = this.readShort();
        this.emit("Handle pool (" + size + "):");
        this.indent();
        for (int i = 0; i < size; ++i) {
            final String handle = this.readString();
            final int code = this.readShort();
            this.emit(handle + " (" + filetypeString(code) + ")");
        }
        this.outdent();
        this.dumpQNameMap("Global elements");
        this.dumpQNameMap("Global attributes");
        this.dumpQNameMap("Model groups");
        this.dumpQNameMap("Attribute groups");
        this.dumpQNameMap("Identity constraints");
        this.dumpQNameMap("Global types");
        this.dumpQNameMap("Document types");
        this.dumpQNameMap("Attribute types");
        this.dumpClassnameIndex("All types by classname");
        this.dumpStringArray("Defined namespaces");
        if (this.atLeast(2, 15, 0)) {
            this.dumpQNameMap("Redefined global types");
            this.dumpQNameMap("Redfined model groups");
            this.dumpQNameMap("Redfined attribute groups");
        }
        if (this.atLeast(2, 19, 0)) {
            this.dumpAnnotations();
        }
        this.readEnd();
    }
    
    int readShort() {
        try {
            return this._input.readUnsignedShort();
        }
        catch (final IOException e) {
            this.error(e);
            return 0;
        }
    }
    
    int readInt() {
        try {
            return this._input.readInt();
        }
        catch (final IOException e) {
            this.error(e);
            return 0;
        }
    }
    
    String readString() {
        return this._stringPool.stringForCode(this.readShort());
    }
    
    QName readQName() {
        final String namespace = this.readString();
        final String localname = this.readString();
        if (localname == null) {
            return null;
        }
        return new QName(namespace, localname);
    }
    
    String readHandle() {
        return this.readString();
    }
    
    String readType() {
        return this.readHandle();
    }
    
    static String qnameString(final QName qname) {
        if (qname == null) {
            return "(null)";
        }
        if (qname.getNamespaceURI() != null) {
            return qname.getLocalPart() + "@" + qname.getNamespaceURI();
        }
        return qname.getLocalPart();
    }
    
    static String qnameSetString(final QNameSet set) {
        return set.toString();
    }
    
    void dumpQNameMap(final String fieldname) {
        final int size = this.readShort();
        this.emit(fieldname + " (" + size + "):");
        this.indent();
        for (int i = 0; i < size; ++i) {
            this.emit(qnameString(this.readQName()) + " = " + this.readHandle());
        }
        this.outdent();
    }
    
    void dumpTypeArray(final String fieldname) {
        final int size = this.readShort();
        this.emit(fieldname + " (" + size + "):");
        this.indent();
        for (int i = 0; i < size; ++i) {
            this.emit(i + " = " + this.readType());
        }
        this.outdent();
    }
    
    void dumpClassnameIndex(final String fieldname) {
        final int size = this.readShort();
        this.emit(fieldname + " (" + size + "):");
        this.indent();
        for (int i = 0; i < size; ++i) {
            this.emit(this.readString() + " = " + this.readType());
        }
        this.outdent();
    }
    
    void dumpStringArray(final String fieldname) {
        final int size = this.readShort();
        this.emit(fieldname + " (" + size + "):");
        this.indent();
        for (int i = 0; i < size; ++i) {
            this.emit(this.readString());
        }
        this.outdent();
    }
    
    void readEnd() {
        try {
            this._input.close();
        }
        catch (final IOException ex) {}
        this._input = null;
        this._stringPool = null;
    }
    
    static String particleTypeString(final int spt) {
        switch (spt) {
            case 1: {
                return "ALL";
            }
            case 2: {
                return "CHOICE";
            }
            case 4: {
                return "ELEMENT";
            }
            case 3: {
                return "SEQUENCE";
            }
            case 5: {
                return "WILDCARD";
            }
            default: {
                return "Unknown particle type (" + spt + ")";
            }
        }
    }
    
    static String bigIntegerString(final BigInteger bigint) {
        if (bigint == null) {
            return "(null)";
        }
        return bigint.toString();
    }
    
    static String wcprocessString(final int code) {
        switch (code) {
            case 1: {
                return "STRICT";
            }
            case 3: {
                return "SKIP";
            }
            case 2: {
                return "LAX";
            }
            case 0: {
                return "NOT_WILDCARD";
            }
            default: {
                return "Unknown process type (" + code + ")";
            }
        }
    }
    
    void dumpAnnotation() {
        if (!this.atLeast(2, 19, 0)) {
            return;
        }
        int n = this.readInt();
        if (n == -1) {
            return;
        }
        this.emit("Annotation");
        boolean empty = true;
        this.indent();
        if (n > 0) {
            this.emit("Attributes (" + n + "):");
            this.indent();
            for (int i = 0; i < n; ++i) {
                if (this.atLeast(2, 24, 0)) {
                    this.emit("Name: " + qnameString(this.readQName()) + ", Value: " + this.readString() + ", ValueURI: " + this.readString());
                }
                else {
                    this.emit("Name: " + qnameString(this.readQName()) + ", Value: " + this.readString());
                }
            }
            this.outdent();
            empty = false;
        }
        n = this.readInt();
        if (n > 0) {
            this.emit("Documentation elements (" + n + "):");
            this.indent();
            for (int i = 0; i < n; ++i) {
                this.emit(this.readString());
            }
            this.outdent();
            empty = false;
        }
        n = this.readInt();
        if (n > 0) {
            this.emit("Appinfo elements (" + n + "):");
            this.indent();
            for (int i = 0; i < n; ++i) {
                this.emit(this.readString());
            }
            this.outdent();
            empty = false;
        }
        if (empty) {
            this.emit("<empty>");
        }
        this.outdent();
    }
    
    void dumpAnnotations() {
        final int n = this.readInt();
        if (n > 0) {
            this.emit("Top-level annotations (" + n + "):");
            this.indent();
            for (int i = 0; i < n; ++i) {
                this.dumpAnnotation();
            }
            this.outdent();
        }
    }
    
    void dumpParticleData(final boolean global) {
        final int particleType = this.readShort();
        this.emit(particleTypeString(particleType) + ":");
        this.indent();
        final int particleFlags = this.readShort();
        this.emit("Flags: " + particleflagsString(particleFlags));
        this.emit("MinOccurs: " + bigIntegerString(this.readBigInteger()));
        this.emit("MaxOccurs: " + bigIntegerString(this.readBigInteger()));
        this.emit("Transition: " + qnameSetString(this.readQNameSet()));
        switch (particleType) {
            case 5: {
                this.emit("Wildcard set: " + qnameSetString(this.readQNameSet()));
                this.emit("Wildcard process: " + wcprocessString(this.readShort()));
                break;
            }
            case 4: {
                this.emit("Name: " + qnameString(this.readQName()));
                this.emit("Type: " + this.readType());
                this.emit("Default: " + this.readString());
                if (this.atLeast(2, 16, 0)) {
                    this.emit("Default value: " + this.readXmlValueObject());
                }
                this.emit("WsdlArrayType: " + this.SOAPArrayTypeString(this.readSOAPArrayType()));
                this.dumpAnnotation();
                if (global) {
                    if (this.atLeast(2, 17, 0)) {
                        this.emit("Substitution group ref: " + this.readHandle());
                    }
                    final int substGroupCount = this.readShort();
                    this.emit("Substitution group members (" + substGroupCount + ")");
                    this.indent();
                    for (int i = 0; i < substGroupCount; ++i) {
                        this.emit(qnameString(this.readQName()));
                    }
                    this.outdent();
                }
                final int count = this.readShort();
                this.emit("Identity constraints (" + count + "):");
                this.indent();
                for (int i = 0; i < count; ++i) {
                    this.emit(this.readHandle());
                }
                this.outdent();
                if (global) {
                    this.emit("Filename: " + this.readString());
                    break;
                }
                break;
            }
            case 1:
            case 2:
            case 3: {
                this.dumpParticleArray("Particle children");
                break;
            }
            default: {
                this.error("Unrecognized schema particle type");
                break;
            }
        }
        this.outdent();
    }
    
    void dumpParticleArray(final String fieldname) {
        final int count = this.readShort();
        this.emit(fieldname + "(" + count + "):");
        this.indent();
        for (int i = 0; i < count; ++i) {
            this.dumpParticleData(false);
        }
        this.outdent();
    }
    
    static String complexVarietyString(final int code) {
        switch (code) {
            case 1: {
                return "EMPTY_CONTENT";
            }
            case 2: {
                return "SIMPLE_CONTENT";
            }
            case 3: {
                return "ELEMENT_CONTENT";
            }
            case 4: {
                return "MIXED_CONTENT";
            }
            default: {
                return "Unknown complex variety (" + code + ")";
            }
        }
    }
    
    static String simpleVarietyString(final int code) {
        switch (code) {
            case 1: {
                return "ATOMIC";
            }
            case 3: {
                return "LIST";
            }
            case 2: {
                return "UNION";
            }
            default: {
                return "Unknown simple variety (" + code + ")";
            }
        }
    }
    
    String facetCodeString(final int code) {
        switch (code) {
            case 0: {
                return "FACET_LENGTH";
            }
            case 1: {
                return "FACET_MIN_LENGTH";
            }
            case 2: {
                return "FACET_MAX_LENGTH";
            }
            case 3: {
                return "FACET_MIN_EXCLUSIVE";
            }
            case 4: {
                return "FACET_MIN_INCLUSIVE";
            }
            case 5: {
                return "FACET_MAX_INCLUSIVE";
            }
            case 6: {
                return "FACET_MAX_EXCLUSIVE";
            }
            case 7: {
                return "FACET_TOTAL_DIGITS";
            }
            case 8: {
                return "FACET_FRACTION_DIGITS";
            }
            default: {
                return "Unknown facet code (" + code + ")";
            }
        }
    }
    
    String whitespaceCodeString(final int code) {
        switch (code) {
            case 3: {
                return "WS_COLLAPSE";
            }
            case 1: {
                return "WS_PRESERVE";
            }
            case 2: {
                return "WS_REPLACE";
            }
            case 0: {
                return "WS_UNSPECIFIED";
            }
            default: {
                return "Unknown whitespace code (" + code + ")";
            }
        }
    }
    
    String derivationTypeString(final int code) {
        switch (code) {
            case 0: {
                return "DT_NOT_DERIVED";
            }
            case 1: {
                return "DT_RESTRICTION";
            }
            case 2: {
                return "DT_EXTENSION";
            }
            default: {
                return "Unknown derivation code (" + code + ")";
            }
        }
    }
    
    void dumpTypeFileData() {
        this.emit("Name: " + qnameString(this.readQName()));
        this.emit("Outer type: " + this.readType());
        this.emit("Depth: " + this.readShort());
        this.emit("Base type: " + this.readType());
        this.emit("Derivation type: " + this.derivationTypeString(this.readShort()));
        this.dumpAnnotation();
        this.emit("Container field:");
        this.indent();
        final int containerfieldtype = this.readShort();
        this.emit("Reftype: " + containerfieldTypeString(containerfieldtype));
        switch (containerfieldtype) {
            case 1: {
                this.emit("Handle: " + this.readHandle());
                break;
            }
            case 2: {
                this.emit("Index: " + this.readShort());
                break;
            }
            case 3: {
                this.emit("Index: " + this.readShort());
                break;
            }
        }
        this.outdent();
        this.emit("Java class name: " + this.readString());
        this.emit("Java impl class name: " + this.readString());
        this.dumpTypeArray("Anonymous types");
        this.emit("Anonymous union member ordinal: " + this.readShort());
        final int flags = this.readInt();
        this.emit("Flags: " + typeflagsString(flags));
        final boolean isComplexType = (flags & 0x1) == 0x0;
        int complexVariety = 0;
        if (isComplexType) {
            complexVariety = this.readShort();
            this.emit("Complex variety: " + complexVarietyString(complexVariety));
            if (this.atLeast(2, 23, 0)) {
                this.emit("Content based on type: " + this.readType());
            }
            final int attrCount = this.readShort();
            this.emit("Attribute model (" + attrCount + "):");
            this.indent();
            for (int i = 0; i < attrCount; ++i) {
                this.dumpAttributeData(false);
            }
            this.emit("Wildcard set: " + qnameSetString(this.readQNameSet()));
            this.emit("Wildcard process: " + wcprocessString(this.readShort()));
            this.outdent();
            final int attrPropCount = this.readShort();
            this.emit("Attribute properties (" + attrPropCount + "):");
            this.indent();
            for (int j = 0; j < attrPropCount; ++j) {
                this.dumpPropertyData();
            }
            this.outdent();
            if (complexVariety == 3 || complexVariety == 4) {
                this.emit("IsAll: " + this.readShort());
                this.dumpParticleArray("Content model");
                final int elemPropCount = this.readShort();
                this.emit("Element properties (" + elemPropCount + "):");
                this.indent();
                for (int k = 0; k < elemPropCount; ++k) {
                    this.dumpPropertyData();
                }
                this.outdent();
            }
        }
        if (!isComplexType || complexVariety == 2) {
            final int simpleVariety = this.readShort();
            this.emit("Simple type variety: " + simpleVarietyString(simpleVariety));
            final boolean isStringEnum = (flags & 0x40) != 0x0;
            final int facetCount = this.readShort();
            this.emit("Facets (" + facetCount + "):");
            this.indent();
            for (int k = 0; k < facetCount; ++k) {
                this.emit(this.facetCodeString(this.readShort()));
                this.emit("Value: " + this.readXmlValueObject());
                this.emit("Fixed: " + this.readShort());
            }
            this.outdent();
            this.emit("Whitespace rule: " + this.whitespaceCodeString(this.readShort()));
            final int patternCount = this.readShort();
            this.emit("Patterns (" + patternCount + "):");
            this.indent();
            for (int l = 0; l < patternCount; ++l) {
                this.emit(this.readString());
            }
            this.outdent();
            final int enumCount = this.readShort();
            this.emit("Enumeration values (" + enumCount + "):");
            this.indent();
            for (int m = 0; m < enumCount; ++m) {
                this.emit(this.readXmlValueObject());
            }
            this.outdent();
            this.emit("Base enum type: " + this.readType());
            if (isStringEnum) {
                final int seCount = this.readShort();
                this.emit("String enum entries (" + seCount + "):");
                this.indent();
                for (int i2 = 0; i2 < seCount; ++i2) {
                    this.emit("\"" + this.readString() + "\" -> " + this.readShort() + " = " + this.readString());
                }
                this.outdent();
            }
            switch (simpleVariety) {
                case 1: {
                    this.emit("Primitive type: " + this.readType());
                    this.emit("Decimal size: " + this.readInt());
                    break;
                }
                case 3: {
                    this.emit("List item type: " + this.readType());
                    break;
                }
                case 2: {
                    this.dumpTypeArray("Union members");
                    break;
                }
                default: {
                    this.error("Unknown simple type variety");
                    break;
                }
            }
        }
        this.emit("Filename: " + this.readString());
    }
    
    static String attruseCodeString(final int code) {
        switch (code) {
            case 2: {
                return "OPTIONAL";
            }
            case 3: {
                return "REQUIRED";
            }
            case 1: {
                return "PROHIBITED";
            }
            default: {
                return "Unknown use code (" + code + ")";
            }
        }
    }
    
    void dumpAttributeData(final boolean global) {
        this.emit("Name: " + qnameString(this.readQName()));
        this.emit("Type: " + this.readType());
        this.emit("Use: " + attruseCodeString(this.readShort()));
        this.emit("Default: " + this.readString());
        if (this.atLeast(2, 16, 0)) {
            this.emit("Default value: " + this.readXmlValueObject());
        }
        this.emit("Fixed: " + this.readShort());
        this.emit("WsdlArrayType: " + this.SOAPArrayTypeString(this.readSOAPArrayType()));
        this.dumpAnnotation();
        if (global) {
            this.emit("Filename: " + this.readString());
        }
    }
    
    void dumpXml() {
        final String xml = this.readString();
        try {
            this.emit(XmlObject.Factory.parse(xml).xmlText(XsbDumper.prettyOptions));
        }
        catch (final XmlException x) {
            this.emit("!!!!!! BAD XML !!!!!");
            this.emit(xml);
        }
    }
    
    void dumpModelGroupData() {
        this.emit("Name: " + qnameString(this.readQName()));
        this.emit("Target namespace: " + this.readString());
        this.emit("Chameleon: " + this.readShort());
        if (this.atLeast(2, 22, 0)) {
            this.emit("Element form default: " + this.readString());
        }
        if (this.atLeast(2, 22, 0)) {
            this.emit("Attribute form default: " + this.readString());
        }
        if (this.atLeast(2, 15, 0)) {
            this.emit("Redefine: " + this.readShort());
        }
        this.emit("Model Group Xml: ");
        this.dumpXml();
        this.dumpAnnotation();
        if (this.atLeast(2, 21, 0)) {
            this.emit("Filename: " + this.readString());
        }
    }
    
    void dumpAttributeGroupData() {
        this.emit("Name: " + qnameString(this.readQName()));
        this.emit("Target namespace: " + this.readString());
        this.emit("Chameleon: " + this.readShort());
        if (this.atLeast(2, 22, 0)) {
            this.emit("Form default: " + this.readString());
        }
        if (this.atLeast(2, 15, 0)) {
            this.emit("Redefine: " + this.readShort());
        }
        this.emit("Attribute Group Xml: ");
        this.dumpXml();
        this.dumpAnnotation();
        if (this.atLeast(2, 21, 0)) {
            this.emit("Filename: " + this.readString());
        }
    }
    
    static String alwaysString(final int code) {
        switch (code) {
            case 2: {
                return "CONSISTENTLY";
            }
            case 0: {
                return "NEVER";
            }
            case 1: {
                return "VARIABLE";
            }
            default: {
                return "Unknown frequency code (" + code + ")";
            }
        }
    }
    
    static String jtcString(final int code) {
        switch (code) {
            case 0: {
                return "XML_OBJECT";
            }
            case 1: {
                return "JAVA_BOOLEAN";
            }
            case 2: {
                return "JAVA_FLOAT";
            }
            case 3: {
                return "JAVA_DOUBLE";
            }
            case 4: {
                return "JAVA_BYTE";
            }
            case 5: {
                return "JAVA_SHORT";
            }
            case 6: {
                return "JAVA_INT";
            }
            case 7: {
                return "JAVA_LONG";
            }
            case 8: {
                return "JAVA_BIG_DECIMAL";
            }
            case 9: {
                return "JAVA_BIG_INTEGER";
            }
            case 10: {
                return "JAVA_STRING";
            }
            case 11: {
                return "JAVA_BYTE_ARRAY";
            }
            case 12: {
                return "JAVA_GDATE";
            }
            case 13: {
                return "JAVA_GDURATION";
            }
            case 14: {
                return "JAVA_DATE";
            }
            case 15: {
                return "JAVA_QNAME";
            }
            case 17: {
                return "JAVA_CALENDAR";
            }
            case 16: {
                return "JAVA_LIST";
            }
            case 18: {
                return "JAVA_ENUM";
            }
            case 19: {
                return "JAVA_OBJECT";
            }
            default: {
                return "Unknown java type code (" + code + ")";
            }
        }
    }
    
    void dumpPropertyData() {
        this.emit("Property");
        this.indent();
        this.emit("Name: " + qnameString(this.readQName()));
        this.emit("Type: " + this.readType());
        final int propflags = this.readShort();
        this.emit("Flags: " + propertyflagsString(propflags));
        this.emit("Container type: " + this.readType());
        this.emit("Min occurances: " + bigIntegerString(this.readBigInteger()));
        this.emit("Max occurances: " + bigIntegerString(this.readBigInteger()));
        this.emit("Nillable: " + alwaysString(this.readShort()));
        this.emit("Default: " + alwaysString(this.readShort()));
        this.emit("Fixed: " + alwaysString(this.readShort()));
        this.emit("Default text: " + this.readString());
        this.emit("Java prop name: " + this.readString());
        this.emit("Java type code: " + jtcString(this.readShort()));
        this.emit("Type for java signature: " + this.readType());
        if (this.atMost(2, 19, 0)) {
            this.emit("Java setter delimiter: " + qnameSetString(this.readQNameSet()));
        }
        if (this.atLeast(2, 16, 0)) {
            this.emit("Default value: " + this.readXmlValueObject());
        }
        if ((propflags & 0x1) == 0x0 && this.atLeast(2, 17, 0)) {
            final int size = this.readShort();
            this.emit("Accepted substitutions (" + size + "):");
            for (int i = 0; i < size; ++i) {
                this.emit("  Accepted name " + this.readQName());
            }
        }
        this.outdent();
    }
    
    String readXmlValueObject() {
        final String type = this.readType();
        if (type == null) {
            return "null";
        }
        final int btc = this.readShort();
        String value = null;
        switch (btc) {
            default: {
                assert false;
            }
            case 0: {
                value = "nil";
                break;
            }
            case 2:
            case 3:
            case 6:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21: {
                value = this.readString();
                break;
            }
            case 4:
            case 5: {
                value = new String(HexBin.encode(this.readByteArray()));
                if (value.length() > 19) {
                    value = (Object)value.subSequence(0, 16) + "...";
                    break;
                }
                break;
            }
            case 7:
            case 8: {
                value = QNameHelper.pretty(this.readQName());
                break;
            }
            case 9:
            case 10: {
                value = Double.toString(this.readDouble());
                break;
            }
        }
        return value + " (" + type + ": " + btc + ")";
    }
    
    double readDouble() {
        try {
            return this._input.readDouble();
        }
        catch (final IOException e) {
            this.error(e);
            return 0.0;
        }
    }
    
    String SOAPArrayTypeString(final SOAPArrayType t) {
        if (t == null) {
            return "null";
        }
        return QNameHelper.pretty(t.getQName()) + t.soap11DimensionString();
    }
    
    SOAPArrayType readSOAPArrayType() {
        final QName qName = this.readQName();
        final String dimensions = this.readString();
        if (qName == null) {
            return null;
        }
        return new SOAPArrayType(qName, dimensions);
    }
    
    QNameSet readQNameSet() {
        final int flag = this.readShort();
        final Set uriSet = new HashSet();
        for (int uriCount = this.readShort(), i = 0; i < uriCount; ++i) {
            uriSet.add(this.readString());
        }
        final Set qnameSet1 = new HashSet();
        for (int qncount1 = this.readShort(), j = 0; j < qncount1; ++j) {
            qnameSet1.add(this.readQName());
        }
        final Set qnameSet2 = new HashSet();
        for (int qncount2 = this.readShort(), k = 0; k < qncount2; ++k) {
            qnameSet2.add(this.readQName());
        }
        if (flag == 1) {
            return QNameSet.forSets(uriSet, null, qnameSet1, qnameSet2);
        }
        return QNameSet.forSets(null, uriSet, qnameSet2, qnameSet1);
    }
    
    byte[] readByteArray() {
        try {
            final int len = this._input.readShort();
            final byte[] result = new byte[len];
            this._input.readFully(result);
            return result;
        }
        catch (final IOException e) {
            this.error(e);
            return null;
        }
    }
    
    BigInteger readBigInteger() {
        final byte[] result = this.readByteArray();
        if (result.length == 0) {
            return null;
        }
        if (result.length == 1 && result[0] == 0) {
            return BigInteger.ZERO;
        }
        if (result.length == 1 && result[0] == 1) {
            return BigInteger.ONE;
        }
        return new BigInteger(result);
    }
    
    protected boolean atLeast(final int majorver, final int minorver, final int releaseno) {
        return this._majorver > majorver || (this._majorver >= majorver && (this._minorver > minorver || (this._minorver >= minorver && this._releaseno >= releaseno)));
    }
    
    protected boolean atMost(final int majorver, final int minorver, final int releaseno) {
        return this._majorver <= majorver && (this._majorver < majorver || (this._minorver <= minorver && (this._minorver < minorver || this._releaseno <= releaseno)));
    }
    
    static {
        prettyOptions = new XmlOptions().setSavePrettyPrint();
        SINGLE_ZERO_BYTE = new byte[] { 0 };
    }
    
    class StringPool
    {
        private List intsToStrings;
        private Map stringsToInts;
        
        StringPool() {
            this.intsToStrings = new ArrayList();
            this.stringsToInts = new HashMap();
            this.intsToStrings.add(null);
        }
        
        String stringForCode(final int code) {
            if (code == 0) {
                return null;
            }
            return this.intsToStrings.get(code);
        }
        
        int codeForString(final String str) {
            if (str == null) {
                return 0;
            }
            Integer result = this.stringsToInts.get(str);
            if (result == null) {
                result = new Integer(this.intsToStrings.size());
                this.intsToStrings.add(str);
                this.stringsToInts.put(str, result);
            }
            return result;
        }
        
        void readFrom(final DataInputStream input) {
            if (this.intsToStrings.size() != 1 || this.stringsToInts.size() != 0) {
                throw new IllegalStateException();
            }
            try {
                final int size = input.readShort();
                XsbDumper.this.emit("String pool (" + size + "):");
                XsbDumper.this.indent();
                for (int i = 1; i < size; ++i) {
                    final String str = input.readUTF();
                    final int code = this.codeForString(str);
                    if (code != i) {
                        throw new IllegalStateException();
                    }
                    XsbDumper.this.emit(code + " = \"" + str + "\"");
                }
                XsbDumper.this.outdent();
            }
            catch (final IOException e) {
                XsbDumper.this.emit(e.toString());
            }
        }
    }
}
