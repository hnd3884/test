package com.sun.java.util.jar.pack;

import java.util.Enumeration;
import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.ListIterator;
import java.util.Comparator;
import java.util.Collections;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Collection;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

class Package
{
    int verbose;
    final int magic = -889270259;
    int default_modtime;
    int default_options;
    Version defaultClassVersion;
    final Version minClassVersion;
    final Version maxClassVersion;
    final Version packageVersion;
    Version observedHighestClassVersion;
    ConstantPool.IndexGroup cp;
    public static final Attribute.Layout attrCodeEmpty;
    public static final Attribute.Layout attrBootstrapMethodsEmpty;
    public static final Attribute.Layout attrInnerClassesEmpty;
    public static final Attribute.Layout attrSourceFileSpecial;
    public static final Map<Attribute.Layout, Attribute> attrDefs;
    ArrayList<Class> classes;
    ArrayList<File> files;
    List<InnerClass> allInnerClasses;
    Map<ConstantPool.ClassEntry, InnerClass> allInnerClassesByThis;
    private static final int SLASH_MIN = 46;
    private static final int SLASH_MAX = 47;
    private static final int DOLLAR_MIN = 0;
    private static final int DOLLAR_MAX = 45;
    static final List<Object> noObjects;
    static final List<Class.Field> noFields;
    static final List<Class.Method> noMethods;
    static final List<InnerClass> noInnerClasses;
    
    public Package() {
        final PropMap currentPropMap = Utils.currentPropMap();
        if (currentPropMap != null) {
            this.verbose = currentPropMap.getInteger("com.sun.java.util.jar.pack.verbose");
        }
        this.default_modtime = 0;
        this.default_options = 0;
        this.defaultClassVersion = null;
        this.observedHighestClassVersion = null;
        this.cp = new ConstantPool.IndexGroup();
        this.classes = new ArrayList<Class>();
        this.files = new ArrayList<File>();
        this.allInnerClasses = new ArrayList<InnerClass>();
        this.minClassVersion = Constants.JAVA_MIN_CLASS_VERSION;
        this.maxClassVersion = Constants.JAVA_MAX_CLASS_VERSION;
        this.packageVersion = null;
    }
    
    public Package(final Version version, final Version version2, final Version packageVersion) {
        final PropMap currentPropMap = Utils.currentPropMap();
        if (currentPropMap != null) {
            this.verbose = currentPropMap.getInteger("com.sun.java.util.jar.pack.verbose");
        }
        this.default_modtime = 0;
        this.default_options = 0;
        this.defaultClassVersion = null;
        this.observedHighestClassVersion = null;
        this.cp = new ConstantPool.IndexGroup();
        this.classes = new ArrayList<Class>();
        this.files = new ArrayList<File>();
        this.allInnerClasses = new ArrayList<InnerClass>();
        this.minClassVersion = ((version == null) ? Constants.JAVA_MIN_CLASS_VERSION : version);
        this.maxClassVersion = ((version2 == null) ? Constants.JAVA_MAX_CLASS_VERSION : version2);
        this.packageVersion = packageVersion;
    }
    
    public void reset() {
        this.cp = new ConstantPool.IndexGroup();
        this.classes.clear();
        this.files.clear();
        BandStructure.nextSeqForDebug = 0;
        this.observedHighestClassVersion = null;
    }
    
    Version getDefaultClassVersion() {
        return this.defaultClassVersion;
    }
    
    private void setHighestClassVersion() {
        if (this.observedHighestClassVersion != null) {
            return;
        }
        Version java_MIN_CLASS_VERSION = Constants.JAVA_MIN_CLASS_VERSION;
        final Iterator<Class> iterator = this.classes.iterator();
        while (iterator.hasNext()) {
            final Version version = iterator.next().getVersion();
            if (java_MIN_CLASS_VERSION.lessThan(version)) {
                java_MIN_CLASS_VERSION = version;
            }
        }
        this.observedHighestClassVersion = java_MIN_CLASS_VERSION;
    }
    
    Version getHighestClassVersion() {
        this.setHighestClassVersion();
        return this.observedHighestClassVersion;
    }
    
    public List<Class> getClasses() {
        return this.classes;
    }
    
    void addClass(final Class class1) {
        assert class1.getPackage() == this;
        final boolean add = this.classes.add(class1);
        assert add;
        if (class1.file == null) {
            class1.initFile(null);
        }
        this.addFile(class1.file);
    }
    
    public List<File> getFiles() {
        return this.files;
    }
    
    public List<File> getClassStubs() {
        final ArrayList list = new ArrayList(this.classes.size());
        for (final Class class1 : this.classes) {
            assert class1.file.isClassStub();
            list.add(class1.file);
        }
        return list;
    }
    
    File newStub(final String s) {
        final File file2;
        final File file = file2 = new File(s);
        file2.options |= 0x2;
        file.prepend = null;
        file.append = null;
        return file;
    }
    
    private static String fixupFileName(final String s) {
        final String replace = s.replace(java.io.File.separatorChar, '/');
        if (replace.startsWith("/")) {
            throw new IllegalArgumentException("absolute file name " + replace);
        }
        return replace;
    }
    
    void addFile(final File file) {
        final boolean add = this.files.add(file);
        assert add;
    }
    
    public List<InnerClass> getAllInnerClasses() {
        return this.allInnerClasses;
    }
    
    public void setAllInnerClasses(final Collection<InnerClass> collection) {
        assert collection != this.allInnerClasses;
        this.allInnerClasses.clear();
        this.allInnerClasses.addAll(collection);
        this.allInnerClassesByThis = new HashMap<ConstantPool.ClassEntry, InnerClass>(this.allInnerClasses.size());
        for (final InnerClass innerClass : this.allInnerClasses) {
            final InnerClass put = this.allInnerClassesByThis.put(innerClass.thisClass, innerClass);
            assert put == null;
        }
    }
    
    public InnerClass getGlobalInnerClass(final ConstantPool.Entry entry) {
        assert entry instanceof ConstantPool.ClassEntry;
        return this.allInnerClassesByThis.get(entry);
    }
    
    private static void visitInnerClassRefs(final Collection<InnerClass> collection, final int n, final Collection<ConstantPool.Entry> collection2) {
        if (collection == null) {
            return;
        }
        if (n == 0) {
            collection2.add(getRefString("InnerClasses"));
        }
        if (collection.size() > 0) {
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                ((InnerClass)iterator.next()).visitRefs(n, collection2);
            }
        }
    }
    
    static String[] parseInnerClassName(final String s) {
        final int length = s.length();
        final int n = lastIndexOf(46, 47, s, s.length()) + 1;
        final int lastIndex = lastIndexOf(0, 45, s, s.length());
        if (lastIndex < n) {
            return null;
        }
        String s2;
        String s3;
        int lastIndex2;
        if (isDigitString(s, lastIndex + 1, length)) {
            s2 = s.substring(lastIndex + 1, length);
            s3 = null;
            lastIndex2 = lastIndex;
        }
        else if ((lastIndex2 = lastIndexOf(0, 45, s, lastIndex - 1)) > n && isDigitString(s, lastIndex2 + 1, lastIndex)) {
            s2 = s.substring(lastIndex2 + 1, lastIndex);
            s3 = s.substring(lastIndex + 1, length).intern();
        }
        else {
            lastIndex2 = lastIndex;
            s2 = null;
            s3 = s.substring(lastIndex + 1, length).intern();
        }
        String intern;
        if (s2 == null) {
            intern = s.substring(0, lastIndex2).intern();
        }
        else {
            intern = null;
        }
        return new String[] { intern, s2, s3 };
    }
    
    private static int lastIndexOf(final int n, final int n2, final String s, final int n3) {
        int n4 = n3;
        while (--n4 >= 0) {
            final char char1 = s.charAt(n4);
            if (char1 >= n && char1 <= n2) {
                return n4;
            }
        }
        return -1;
    }
    
    private static boolean isDigitString(final String s, final int n, final int n2) {
        if (n == n2) {
            return false;
        }
        for (int i = n; i < n2; ++i) {
            final char char1 = s.charAt(i);
            if (char1 < '0' || char1 > '9') {
                return false;
            }
        }
        return true;
    }
    
    static String getObviousSourceFile(final String s) {
        final String substring = s.substring(lastIndexOf(46, 47, s, s.length()) + 1);
        int length = substring.length();
        do {
            final int lastIndex = lastIndexOf(0, 45, substring, length - 1);
            if (lastIndex < 0) {
                break;
            }
            length = lastIndex;
        } while (length != 0);
        return substring.substring(0, length) + ".java";
    }
    
    static ConstantPool.Utf8Entry getRefString(final String s) {
        return ConstantPool.getUtf8Entry(s);
    }
    
    static ConstantPool.LiteralEntry getRefLiteral(final Comparable<?> comparable) {
        return ConstantPool.getLiteralEntry(comparable);
    }
    
    void stripAttributeKind(final String s) {
        if (this.verbose > 0) {
            Utils.log.info("Stripping " + s.toLowerCase() + " data and attributes...");
        }
        switch (s) {
            case "Debug": {
                this.strip("SourceFile");
                this.strip("LineNumberTable");
                this.strip("LocalVariableTable");
                this.strip("LocalVariableTypeTable");
                break;
            }
            case "Compile": {
                this.strip("Deprecated");
                this.strip("Synthetic");
                break;
            }
            case "Exceptions": {
                this.strip("Exceptions");
                break;
            }
            case "Constant": {
                this.stripConstantFields();
                break;
            }
        }
    }
    
    public void trimToSize() {
        this.classes.trimToSize();
        final Iterator<Class> iterator = this.classes.iterator();
        while (iterator.hasNext()) {
            iterator.next().trimToSize();
        }
        this.files.trimToSize();
    }
    
    public void strip(final String s) {
        final Iterator<Class> iterator = this.classes.iterator();
        while (iterator.hasNext()) {
            iterator.next().strip(s);
        }
    }
    
    public void stripConstantFields() {
        final Iterator<Class> iterator = this.classes.iterator();
        while (iterator.hasNext()) {
            final Iterator<Class.Field> iterator2 = iterator.next().fields.iterator();
            while (iterator2.hasNext()) {
                final Class.Field field = iterator2.next();
                if (Modifier.isFinal(field.flags) && Modifier.isStatic(field.flags) && field.getAttribute("ConstantValue") != null && !field.getName().startsWith("serial") && this.verbose > 2) {
                    Utils.log.fine(">> Strip " + this + " ConstantValue");
                    iterator2.remove();
                }
            }
        }
    }
    
    protected void visitRefs(final int n, final Collection<ConstantPool.Entry> collection) {
        final Iterator<Class> iterator = this.classes.iterator();
        while (iterator.hasNext()) {
            iterator.next().visitRefs(n, collection);
        }
        if (n != 0) {
            final Iterator<File> iterator2 = this.files.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().visitRefs(n, collection);
            }
            visitInnerClassRefs(this.allInnerClasses, n, collection);
        }
    }
    
    void reorderFiles(final boolean b, final boolean b2) {
        if (!b) {
            Collections.sort(this.classes);
        }
        final List<File> classStubs = this.getClassStubs();
        final Iterator<File> iterator = this.files.iterator();
        while (iterator.hasNext()) {
            final File file = iterator.next();
            if (file.isClassStub() || (b2 && file.isDirectory())) {
                iterator.remove();
            }
        }
        Collections.sort(this.files, new Comparator<File>() {
            @Override
            public int compare(final File file, final File file2) {
                final String nameString = file.nameString;
                final String nameString2 = file2.nameString;
                if (nameString.equals(nameString2)) {
                    return 0;
                }
                if ("META-INF/MANIFEST.MF".equals(nameString)) {
                    return -1;
                }
                if ("META-INF/MANIFEST.MF".equals(nameString2)) {
                    return 1;
                }
                final String substring = nameString.substring(1 + nameString.lastIndexOf(47));
                final String substring2 = nameString2.substring(1 + nameString2.lastIndexOf(47));
                final int compareTo = substring.substring(1 + substring.lastIndexOf(46)).compareTo(substring2.substring(1 + substring2.lastIndexOf(46)));
                if (compareTo != 0) {
                    return compareTo;
                }
                return nameString.compareTo(nameString2);
            }
        });
        this.files.addAll(classStubs);
    }
    
    void trimStubs() {
        final ListIterator<File> listIterator = this.files.listIterator(this.files.size());
        while (listIterator.hasPrevious()) {
            final File file = listIterator.previous();
            if (!file.isTrivialClassStub()) {
                if (this.verbose > 1) {
                    Utils.log.fine("Keeping last non-trivial " + file);
                    break;
                }
                break;
            }
            else {
                if (this.verbose > 2) {
                    Utils.log.fine("Removing trivial " + file);
                }
                listIterator.remove();
            }
        }
        if (this.verbose > 0) {
            Utils.log.info("Transmitting " + this.files.size() + " files, including per-file data for " + this.getClassStubs().size() + " classes out of " + this.classes.size());
        }
    }
    
    void buildGlobalConstantPool(final Set<ConstantPool.Entry> set) {
        if (this.verbose > 1) {
            Utils.log.fine("Checking for unused CP entries");
        }
        set.add(getRefString(""));
        this.visitRefs(1, set);
        ConstantPool.completeReferencesIn(set, false);
        if (this.verbose > 1) {
            Utils.log.fine("Sorting CP entries");
        }
        final ConstantPool.Index[] partitionByTag = ConstantPool.partitionByTag(ConstantPool.makeIndex("unsorted", set));
        for (int i = 0; i < ConstantPool.TAGS_IN_ORDER.length; ++i) {
            final byte b = ConstantPool.TAGS_IN_ORDER[i];
            final ConstantPool.Index index = partitionByTag[b];
            if (index != null) {
                ConstantPool.sort(index);
                this.cp.initIndexByTag(b, index);
                partitionByTag[b] = null;
            }
        }
        for (int j = 0; j < partitionByTag.length; ++j) {
            final ConstantPool.Index index2 = partitionByTag[j];
            assert index2 == null;
        }
        for (int k = 0; k < ConstantPool.TAGS_IN_ORDER.length; ++k) {
            final ConstantPool.Index indexByTag = this.cp.getIndexByTag(ConstantPool.TAGS_IN_ORDER[k]);
            assert indexByTag.assertIsSorted();
            if (this.verbose > 2) {
                Utils.log.fine(indexByTag.dumpString());
            }
        }
    }
    
    void ensureAllClassFiles() {
        final HashSet set = new HashSet((Collection<? extends E>)this.files);
        for (final Class class1 : this.classes) {
            if (!set.contains(class1.file)) {
                this.files.add(class1.file);
            }
        }
    }
    
    static {
        final HashMap hashMap = new HashMap(3);
        attrCodeEmpty = Attribute.define(hashMap, 2, "Code", "").layout();
        attrBootstrapMethodsEmpty = Attribute.define(hashMap, 0, "BootstrapMethods", "").layout();
        attrInnerClassesEmpty = Attribute.define(hashMap, 0, "InnerClasses", "").layout();
        attrSourceFileSpecial = Attribute.define(hashMap, 0, "SourceFile", "RUNH").layout();
        attrDefs = Collections.unmodifiableMap((Map<?, ?>)hashMap);
        assert lastIndexOf(0, 45, "x$$y$", 4) == 2;
        assert lastIndexOf(46, 47, "x//y/", 4) == 2;
        noObjects = Arrays.asList(new Object[0]);
        noFields = Arrays.asList(new Class.Field[0]);
        noMethods = Arrays.asList(new Class.Method[0]);
        noInnerClasses = Arrays.asList(new InnerClass[0]);
    }
    
    public final class Class extends Attribute.Holder implements Comparable<Class>
    {
        File file;
        int magic;
        Version version;
        ConstantPool.Entry[] cpMap;
        ConstantPool.ClassEntry thisClass;
        ConstantPool.ClassEntry superClass;
        ConstantPool.ClassEntry[] interfaces;
        ArrayList<Field> fields;
        ArrayList<Method> methods;
        ArrayList<InnerClass> innerClasses;
        ArrayList<ConstantPool.BootstrapMethodEntry> bootstrapMethods;
        
        public Package getPackage() {
            return Package.this;
        }
        
        Class(final int flags, final ConstantPool.ClassEntry thisClass, final ConstantPool.ClassEntry superClass, final ConstantPool.ClassEntry[] interfaces) {
            this.magic = -889275714;
            this.version = Package.this.defaultClassVersion;
            this.flags = flags;
            this.thisClass = thisClass;
            this.superClass = superClass;
            this.interfaces = interfaces;
            final boolean add = Package.this.classes.add(this);
            assert add;
        }
        
        Class(final String s) {
            this.initFile(Package.this.newStub(s));
        }
        
        List<Field> getFields() {
            return (this.fields == null) ? Package.noFields : this.fields;
        }
        
        List<Method> getMethods() {
            return (this.methods == null) ? Package.noMethods : this.methods;
        }
        
        public String getName() {
            return this.thisClass.stringValue();
        }
        
        Version getVersion() {
            return this.version;
        }
        
        @Override
        public int compareTo(final Class class1) {
            return this.getName().compareTo(class1.getName());
        }
        
        String getObviousSourceFile() {
            return Package.getObviousSourceFile(this.getName());
        }
        
        private void transformSourceFile(final boolean b) {
            final Attribute attribute = this.getAttribute(Package.attrSourceFileSpecial);
            if (attribute == null) {
                return;
            }
            final String obviousSourceFile = this.getObviousSourceFile();
            final ArrayList list = new ArrayList(1);
            attribute.visitRefs(this, 1, list);
            final ConstantPool.Utf8Entry utf8Entry = (ConstantPool.Utf8Entry)list.get(0);
            Attribute attribute2 = attribute;
            if (utf8Entry == null) {
                if (b) {
                    attribute2 = Attribute.find(0, "SourceFile", "H").addContent(new byte[2]);
                }
                else {
                    final byte[] array = new byte[2];
                    attribute2 = Package.attrSourceFileSpecial.addContent(array, Fixups.addRefWithBytes(null, array, Package.getRefString(obviousSourceFile)));
                }
            }
            else if (obviousSourceFile.equals(utf8Entry.stringValue())) {
                if (b) {
                    attribute2 = Package.attrSourceFileSpecial.addContent(new byte[2]);
                }
                else {
                    assert false;
                }
            }
            if (attribute2 != attribute) {
                if (Package.this.verbose > 2) {
                    Utils.log.fine("recoding obvious SourceFile=" + obviousSourceFile);
                }
                final ArrayList attributes = new ArrayList(this.getAttributes());
                attributes.set(attributes.indexOf(attribute), attribute2);
                this.setAttributes(attributes);
            }
        }
        
        void minimizeSourceFile() {
            this.transformSourceFile(true);
        }
        
        void expandSourceFile() {
            this.transformSourceFile(false);
        }
        
        @Override
        protected ConstantPool.Entry[] getCPMap() {
            return this.cpMap;
        }
        
        protected void setCPMap(final ConstantPool.Entry[] cpMap) {
            this.cpMap = cpMap;
        }
        
        boolean hasBootstrapMethods() {
            return this.bootstrapMethods != null && !this.bootstrapMethods.isEmpty();
        }
        
        List<ConstantPool.BootstrapMethodEntry> getBootstrapMethods() {
            return this.bootstrapMethods;
        }
        
        ConstantPool.BootstrapMethodEntry[] getBootstrapMethodMap() {
            return (ConstantPool.BootstrapMethodEntry[])(this.hasBootstrapMethods() ? ((ConstantPool.BootstrapMethodEntry[])this.bootstrapMethods.toArray(new ConstantPool.BootstrapMethodEntry[this.bootstrapMethods.size()])) : null);
        }
        
        void setBootstrapMethods(final Collection<ConstantPool.BootstrapMethodEntry> collection) {
            assert this.bootstrapMethods == null;
            this.bootstrapMethods = new ArrayList<ConstantPool.BootstrapMethodEntry>(collection);
        }
        
        boolean hasInnerClasses() {
            return this.innerClasses != null;
        }
        
        List<InnerClass> getInnerClasses() {
            return this.innerClasses;
        }
        
        public void setInnerClasses(final Collection<InnerClass> collection) {
            this.innerClasses = ((collection == null) ? null : new ArrayList<InnerClass>(collection));
            final Attribute attribute = this.getAttribute(Package.attrInnerClassesEmpty);
            if (this.innerClasses != null && attribute == null) {
                this.addAttribute(Package.attrInnerClassesEmpty.canonicalInstance());
            }
            else if (this.innerClasses == null && attribute != null) {
                this.removeAttribute(attribute);
            }
        }
        
        public List<InnerClass> computeGloballyImpliedICs() {
            final HashSet set = new HashSet();
            final ArrayList<InnerClass> innerClasses = this.innerClasses;
            this.innerClasses = null;
            this.visitRefs(0, set);
            this.innerClasses = innerClasses;
            ConstantPool.completeReferencesIn(set, true);
            final HashSet set2 = new HashSet();
            for (ConstantPool.Entry outerClass : set) {
                if (!(outerClass instanceof ConstantPool.ClassEntry)) {
                    continue;
                }
                while (outerClass != null) {
                    final InnerClass globalInnerClass = Package.this.getGlobalInnerClass(outerClass);
                    if (globalInnerClass == null) {
                        break;
                    }
                    if (!set2.add(outerClass)) {
                        break;
                    }
                    outerClass = globalInnerClass.outerClass;
                }
            }
            final ArrayList<InnerClass> list = new ArrayList<InnerClass>();
            for (final InnerClass innerClass : Package.this.allInnerClasses) {
                if (set2.contains(innerClass.thisClass) || innerClass.outerClass == this.thisClass) {
                    if (Package.this.verbose > 1) {
                        Utils.log.fine("Relevant IC: " + innerClass);
                    }
                    list.add(innerClass);
                }
            }
            return list;
        }
        
        private List<InnerClass> computeICdiff() {
            final List<InnerClass> computeGloballyImpliedICs = this.computeGloballyImpliedICs();
            Object o = this.getInnerClasses();
            if (o == null) {
                o = Collections.emptyList();
            }
            if (((List)o).isEmpty()) {
                return computeGloballyImpliedICs;
            }
            if (computeGloballyImpliedICs.isEmpty()) {
                return (List<InnerClass>)o;
            }
            final HashSet set = new HashSet((Collection)o);
            set.retainAll(new HashSet(computeGloballyImpliedICs));
            computeGloballyImpliedICs.addAll((Collection)o);
            computeGloballyImpliedICs.removeAll(set);
            return computeGloballyImpliedICs;
        }
        
        void minimizeLocalICs() {
            final List<InnerClass> computeICdiff = this.computeICdiff();
            final ArrayList<InnerClass> innerClasses = this.innerClasses;
            Object emptyList;
            if (computeICdiff.isEmpty()) {
                emptyList = null;
                if (innerClasses != null && innerClasses.isEmpty() && Package.this.verbose > 0) {
                    Utils.log.info("Warning: Dropping empty InnerClasses attribute from " + this);
                }
            }
            else if (innerClasses == null) {
                emptyList = Collections.emptyList();
            }
            else {
                emptyList = computeICdiff;
            }
            this.setInnerClasses((Collection<InnerClass>)emptyList);
            if (Package.this.verbose > 1 && emptyList != null) {
                Utils.log.fine("keeping local ICs in " + this + ": " + emptyList);
            }
        }
        
        int expandLocalICs() {
            final ArrayList<InnerClass> innerClasses = this.innerClasses;
            List<InnerClass> computeICdiff;
            int n;
            if (innerClasses == null) {
                final List<InnerClass> computeGloballyImpliedICs = this.computeGloballyImpliedICs();
                if (computeGloballyImpliedICs.isEmpty()) {
                    computeICdiff = null;
                    n = 0;
                }
                else {
                    computeICdiff = computeGloballyImpliedICs;
                    n = 1;
                }
            }
            else if (innerClasses.isEmpty()) {
                computeICdiff = null;
                n = 0;
            }
            else {
                computeICdiff = this.computeICdiff();
                n = (computeICdiff.containsAll(innerClasses) ? 1 : -1);
            }
            this.setInnerClasses(computeICdiff);
            return n;
        }
        
        @Override
        public void trimToSize() {
            super.trimToSize();
            for (int i = 0; i <= 1; ++i) {
                final List<E> list = (i == 0) ? this.fields : this.methods;
                if (list != null) {
                    ((ArrayList)list).trimToSize();
                    final Iterator<Object> iterator = (Iterator<Object>)((ArrayList<Member>)list).iterator();
                    while (iterator.hasNext()) {
                        iterator.next().trimToSize();
                    }
                }
            }
            if (this.innerClasses != null) {
                this.innerClasses.trimToSize();
            }
        }
        
        @Override
        public void strip(final String s) {
            if ("InnerClass".equals(s)) {
                this.innerClasses = null;
            }
            for (int i = 0; i <= 1; ++i) {
                final List<E> list = (i == 0) ? this.fields : this.methods;
                if (list != null) {
                    final Iterator<Object> iterator = (Iterator<Object>)((ArrayList<Member>)list).iterator();
                    while (iterator.hasNext()) {
                        iterator.next().strip(s);
                    }
                }
            }
            super.strip(s);
        }
        
        @Override
        protected void visitRefs(final int n, final Collection<ConstantPool.Entry> collection) {
            if (Package.this.verbose > 2) {
                Utils.log.fine("visitRefs " + this);
            }
            collection.add(this.thisClass);
            collection.add(this.superClass);
            collection.addAll(Arrays.asList(this.interfaces));
            for (int i = 0; i <= 1; ++i) {
                final List<E> list = (i == 0) ? this.fields : this.methods;
                if (list != null) {
                    for (final Member member : list) {
                        boolean b = false;
                        try {
                            member.visitRefs(n, collection);
                            b = true;
                        }
                        finally {
                            if (!b) {
                                Utils.log.warning("Error scanning " + member);
                            }
                        }
                    }
                }
            }
            this.visitInnerClassRefs(n, collection);
            super.visitRefs(n, collection);
        }
        
        protected void visitInnerClassRefs(final int n, final Collection<ConstantPool.Entry> collection) {
            visitInnerClassRefs(this.innerClasses, n, collection);
        }
        
        void finishReading() {
            this.trimToSize();
            this.maybeChooseFileName();
        }
        
        public void initFile(File stub) {
            assert this.file == null;
            if (stub == null) {
                stub = Package.this.newStub(this.canonicalFileName());
            }
            this.file = stub;
            assert stub.isClassStub();
            (stub.stubClass = this).maybeChooseFileName();
        }
        
        public void maybeChooseFileName() {
            if (this.thisClass == null) {
                return;
            }
            final String canonicalFileName = this.canonicalFileName();
            if (this.file.nameString.equals("")) {
                this.file.nameString = canonicalFileName;
            }
            if (this.file.nameString.equals(canonicalFileName)) {
                this.file.name = Package.getRefString("");
                return;
            }
            if (this.file.name == null) {
                this.file.name = Package.getRefString(this.file.nameString);
            }
        }
        
        public String canonicalFileName() {
            if (this.thisClass == null) {
                return null;
            }
            return this.thisClass.stringValue() + ".class";
        }
        
        public java.io.File getFileName(final java.io.File file) {
            String s = this.file.name.stringValue();
            if (s.equals("")) {
                s = this.canonicalFileName();
            }
            return new java.io.File(file, s.replace('/', java.io.File.separatorChar));
        }
        
        public java.io.File getFileName() {
            return this.getFileName(null);
        }
        
        @Override
        public String toString() {
            return this.thisClass.stringValue();
        }
        
        public abstract class Member extends Attribute.Holder implements Comparable<Member>
        {
            ConstantPool.DescriptorEntry descriptor;
            
            protected Member(final int flags, final ConstantPool.DescriptorEntry descriptor) {
                this.flags = flags;
                this.descriptor = descriptor;
            }
            
            public Class thisClass() {
                return Class.this;
            }
            
            public ConstantPool.DescriptorEntry getDescriptor() {
                return this.descriptor;
            }
            
            public String getName() {
                return this.descriptor.nameRef.stringValue();
            }
            
            public String getType() {
                return this.descriptor.typeRef.stringValue();
            }
            
            @Override
            protected ConstantPool.Entry[] getCPMap() {
                return Class.this.cpMap;
            }
            
            @Override
            protected void visitRefs(final int n, final Collection<ConstantPool.Entry> collection) {
                if (Package.this.verbose > 2) {
                    Utils.log.fine("visitRefs " + this);
                }
                if (n == 0) {
                    collection.add(this.descriptor.nameRef);
                    collection.add(this.descriptor.typeRef);
                }
                else {
                    collection.add(this.descriptor);
                }
                super.visitRefs(n, collection);
            }
            
            @Override
            public String toString() {
                return Class.this + "." + this.descriptor.prettyString();
            }
        }
        
        public class Field extends Member
        {
            int order;
            
            public Field(final int n, final ConstantPool.DescriptorEntry descriptorEntry) {
                super(n, descriptorEntry);
                assert !descriptorEntry.isMethod();
                if (Class.this.fields == null) {
                    Class.this.fields = new ArrayList<Field>();
                }
                final boolean add = Class.this.fields.add(this);
                assert add;
                this.order = Class.this.fields.size();
            }
            
            public byte getLiteralTag() {
                return this.descriptor.getLiteralTag();
            }
            
            @Override
            public int compareTo(final Member member) {
                return this.order - ((Field)member).order;
            }
        }
        
        public class Method extends Member
        {
            Code code;
            
            public Method(final int n, final ConstantPool.DescriptorEntry descriptorEntry) {
                super(n, descriptorEntry);
                assert descriptorEntry.isMethod();
                if (Class.this.methods == null) {
                    Class.this.methods = new ArrayList<Method>();
                }
                final boolean add = Class.this.methods.add(this);
                assert add;
            }
            
            @Override
            public void trimToSize() {
                super.trimToSize();
                if (this.code != null) {
                    this.code.trimToSize();
                }
            }
            
            public int getArgumentSize() {
                return (Modifier.isStatic(this.flags) ? 0 : 1) + this.descriptor.typeRef.computeSize(true);
            }
            
            @Override
            public int compareTo(final Member member) {
                return this.getDescriptor().compareTo(member.getDescriptor());
            }
            
            @Override
            public void strip(final String s) {
                if ("Code".equals(s)) {
                    this.code = null;
                }
                if (this.code != null) {
                    this.code.strip(s);
                }
                super.strip(s);
            }
            
            @Override
            protected void visitRefs(final int n, final Collection<ConstantPool.Entry> collection) {
                super.visitRefs(n, collection);
                if (this.code != null) {
                    if (n == 0) {
                        collection.add(Package.getRefString("Code"));
                    }
                    this.code.visitRefs(n, collection);
                }
            }
        }
    }
    
    public final class File implements Comparable<File>
    {
        String nameString;
        ConstantPool.Utf8Entry name;
        int modtime;
        int options;
        Class stubClass;
        ArrayList<byte[]> prepend;
        ByteArrayOutputStream append;
        
        File(final ConstantPool.Utf8Entry name) {
            this.modtime = 0;
            this.options = 0;
            this.prepend = new ArrayList<byte[]>();
            this.append = new ByteArrayOutputStream();
            this.name = name;
            this.nameString = name.stringValue();
        }
        
        File(String access$100) {
            this.modtime = 0;
            this.options = 0;
            this.prepend = new ArrayList<byte[]>();
            this.append = new ByteArrayOutputStream();
            access$100 = fixupFileName(access$100);
            this.name = Package.getRefString(access$100);
            this.nameString = this.name.stringValue();
        }
        
        public boolean isDirectory() {
            return this.nameString.endsWith("/");
        }
        
        public boolean isClassStub() {
            return (this.options & 0x2) != 0x0;
        }
        
        public Class getStubClass() {
            assert this.isClassStub();
            assert this.stubClass != null;
            return this.stubClass;
        }
        
        public boolean isTrivialClassStub() {
            return this.isClassStub() && this.name.stringValue().equals("") && (this.modtime == 0 || this.modtime == Package.this.default_modtime) && (this.options & 0xFFFFFFFD) == 0x0;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o.getClass() == File.class && ((File)o).nameString.equals(this.nameString);
        }
        
        @Override
        public int hashCode() {
            return this.nameString.hashCode();
        }
        
        @Override
        public int compareTo(final File file) {
            return this.nameString.compareTo(file.nameString);
        }
        
        @Override
        public String toString() {
            return this.nameString + "{" + (this.isClassStub() ? "*" : "") + (BandStructure.testBit(this.options, 1) ? "@" : "") + ((this.modtime == 0) ? "" : ("M" + this.modtime)) + ((this.getFileLength() == 0L) ? "" : ("[" + this.getFileLength() + "]")) + "}";
        }
        
        public java.io.File getFileName() {
            return this.getFileName(null);
        }
        
        public java.io.File getFileName(final java.io.File file) {
            return new java.io.File(file, this.nameString.replace('/', java.io.File.separatorChar));
        }
        
        public void addBytes(final byte[] array) {
            this.addBytes(array, 0, array.length);
        }
        
        public void addBytes(final byte[] array, final int n, final int n2) {
            if ((this.append.size() | n2) << 2 < 0) {
                this.prepend.add(this.append.toByteArray());
                this.append.reset();
            }
            this.append.write(array, n, n2);
        }
        
        public long getFileLength() {
            long n = 0L;
            if (this.prepend == null || this.append == null) {
                return 0L;
            }
            final Iterator<byte[]> iterator = this.prepend.iterator();
            while (iterator.hasNext()) {
                n += iterator.next().length;
            }
            return n + this.append.size();
        }
        
        public void writeTo(final OutputStream outputStream) throws IOException {
            if (this.prepend == null || this.append == null) {
                return;
            }
            final Iterator<byte[]> iterator = this.prepend.iterator();
            while (iterator.hasNext()) {
                outputStream.write(iterator.next());
            }
            this.append.writeTo(outputStream);
        }
        
        public void readFrom(final InputStream inputStream) throws IOException {
            final byte[] array = new byte[65536];
            int read;
            while ((read = inputStream.read(array)) > 0) {
                this.addBytes(array, 0, read);
            }
        }
        
        public InputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.append.toByteArray());
            if (this.prepend.isEmpty()) {
                return byteArrayInputStream;
            }
            final ArrayList list = new ArrayList(this.prepend.size() + 1);
            final Iterator<byte[]> iterator = this.prepend.iterator();
            while (iterator.hasNext()) {
                list.add(new ByteArrayInputStream(iterator.next()));
            }
            list.add(byteArrayInputStream);
            return new SequenceInputStream((Enumeration<? extends InputStream>)Collections.enumeration((Collection<Object>)list));
        }
        
        protected void visitRefs(final int n, final Collection<ConstantPool.Entry> collection) {
            assert this.name != null;
            collection.add(this.name);
        }
    }
    
    static class InnerClass implements Comparable<InnerClass>
    {
        final ConstantPool.ClassEntry thisClass;
        final ConstantPool.ClassEntry outerClass;
        final ConstantPool.Utf8Entry name;
        final int flags;
        final boolean predictable;
        
        InnerClass(final ConstantPool.ClassEntry thisClass, final ConstantPool.ClassEntry outerClass, final ConstantPool.Utf8Entry name, final int flags) {
            this.thisClass = thisClass;
            this.outerClass = outerClass;
            this.name = name;
            this.flags = flags;
            this.predictable = this.computePredictable();
        }
        
        private boolean computePredictable() {
            final String[] innerClassName = Package.parseInnerClassName(this.thisClass.stringValue());
            if (innerClassName == null) {
                return false;
            }
            final String s = innerClassName[0];
            final String s2 = innerClassName[2];
            final String s3 = (this.name == null) ? null : this.name.stringValue();
            final String s4 = (this.outerClass == null) ? null : this.outerClass.stringValue();
            return s2 == s3 && s == s4;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null || o.getClass() != InnerClass.class) {
                return false;
            }
            final InnerClass innerClass = (InnerClass)o;
            return eq(this.thisClass, innerClass.thisClass) && eq(this.outerClass, innerClass.outerClass) && eq(this.name, innerClass.name) && this.flags == innerClass.flags;
        }
        
        private static boolean eq(final Object o, final Object o2) {
            return (o == null) ? (o2 == null) : o.equals(o2);
        }
        
        @Override
        public int hashCode() {
            return this.thisClass.hashCode();
        }
        
        @Override
        public int compareTo(final InnerClass innerClass) {
            return this.thisClass.compareTo(innerClass.thisClass);
        }
        
        protected void visitRefs(final int n, final Collection<ConstantPool.Entry> collection) {
            collection.add(this.thisClass);
            if (n == 0 || !this.predictable) {
                collection.add(this.outerClass);
                collection.add(this.name);
            }
        }
        
        @Override
        public String toString() {
            return this.thisClass.stringValue();
        }
    }
    
    protected static final class Version
    {
        public final short major;
        public final short minor;
        
        private Version(final short major, final short minor) {
            this.major = major;
            this.minor = minor;
        }
        
        @Override
        public String toString() {
            return this.major + "." + this.minor;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Version && this.major == ((Version)o).major && this.minor == ((Version)o).minor;
        }
        
        public int intValue() {
            return (this.major << 16) + this.minor;
        }
        
        @Override
        public int hashCode() {
            return (this.major << 16) + 7 + this.minor;
        }
        
        public static Version of(final int n, final int n2) {
            return new Version((short)n, (short)n2);
        }
        
        public static Version of(final byte[] array) {
            return new Version((short)((array[2] & 0xFF) << 8 | (array[3] & 0xFF)), (short)((array[0] & 0xFF) << 8 | (array[1] & 0xFF)));
        }
        
        public static Version of(final int n) {
            return new Version((short)(n >>> 16), (short)n);
        }
        
        public static Version makeVersion(final PropMap propMap, final String s) {
            final int integer = propMap.getInteger("com.sun.java.util.jar.pack." + s + ".minver", -1);
            final int integer2 = propMap.getInteger("com.sun.java.util.jar.pack." + s + ".majver", -1);
            return (integer >= 0 && integer2 >= 0) ? of(integer2, integer) : null;
        }
        
        public byte[] asBytes() {
            return new byte[] { (byte)(this.minor >> 8), (byte)this.minor, (byte)(this.major >> 8), (byte)this.major };
        }
        
        public int compareTo(final Version version) {
            return this.intValue() - version.intValue();
        }
        
        public boolean lessThan(final Version version) {
            return this.compareTo(version) < 0;
        }
        
        public boolean greaterThan(final Version version) {
            return this.compareTo(version) > 0;
        }
    }
}
