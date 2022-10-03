package jdk.jfr.internal;

import jdk.jfr.Enabled;
import jdk.jfr.StackTrace;
import jdk.jfr.Threshold;
import jdk.jfr.Period;
import jdk.jfr.Category;
import jdk.jfr.TransitionTo;
import jdk.jfr.TransitionFrom;
import jdk.jfr.Description;
import jdk.jfr.Experimental;
import jdk.jfr.Label;
import java.util.Collection;
import jdk.jfr.Unsigned;
import java.util.Objects;
import java.util.Iterator;
import java.util.Collections;
import jdk.jfr.Relational;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import jdk.internal.util.xml.impl.SAXParserImpl;
import java.lang.annotation.Annotation;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.Attributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import jdk.jfr.AnnotationElement;
import java.util.List;
import java.util.Map;
import jdk.internal.org.xml.sax.EntityResolver;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

final class MetadataHandler extends DefaultHandler implements EntityResolver
{
    final Map<String, TypeElement> types;
    final Map<String, XmlType> xmlTypes;
    final Map<String, List<AnnotationElement>> xmlContentTypes;
    final List<String> relations;
    long eventTypeId;
    long structTypeId;
    FieldElement currentField;
    TypeElement currentType;
    
    MetadataHandler() {
        this.types = new LinkedHashMap<String, TypeElement>(200);
        this.xmlTypes = new HashMap<String, XmlType>(20);
        this.xmlContentTypes = new HashMap<String, List<AnnotationElement>>(20);
        this.relations = new ArrayList<String>();
        this.eventTypeId = 255L;
        this.structTypeId = 33L;
    }
    
    @Override
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        switch (s3) {
            case "XmlType": {
                final XmlType xmlType = new XmlType();
                xmlType.name = attributes.getValue("name");
                xmlType.javaType = attributes.getValue("javaType");
                xmlType.contentType = attributes.getValue("contentType");
                xmlType.unsigned = Boolean.valueOf(attributes.getValue("unsigned"));
                this.xmlTypes.put(xmlType.name, xmlType);
                break;
            }
            case "Type":
            case "Event": {
                this.currentType = new TypeElement();
                this.currentType.name = attributes.getValue("name");
                this.currentType.label = attributes.getValue("label");
                this.currentType.description = attributes.getValue("description");
                this.currentType.category = attributes.getValue("category");
                this.currentType.thread = this.getBoolean(attributes, "thread", false);
                this.currentType.stackTrace = this.getBoolean(attributes, "stackTrace", false);
                this.currentType.startTime = this.getBoolean(attributes, "startTime", true);
                this.currentType.period = attributes.getValue("period");
                this.currentType.cutoff = this.getBoolean(attributes, "cutoff", false);
                this.currentType.experimental = this.getBoolean(attributes, "experimental", false);
                this.currentType.isEvent = s3.equals("Event");
                break;
            }
            case "Field": {
                this.currentField = new FieldElement();
                this.currentField.struct = this.getBoolean(attributes, "struct", false);
                this.currentField.array = this.getBoolean(attributes, "array", false);
                this.currentField.name = attributes.getValue("name");
                this.currentField.label = attributes.getValue("label");
                this.currentField.typeName = attributes.getValue("type");
                this.currentField.description = attributes.getValue("description");
                this.currentField.experimental = this.getBoolean(attributes, "experimental", false);
                this.currentField.contentType = attributes.getValue("contentType");
                this.currentField.relation = attributes.getValue("relation");
                this.currentField.transition = attributes.getValue("transition");
                break;
            }
            case "XmlContentType": {
                this.xmlContentTypes.put(attributes.getValue("name"), this.createAnnotationElements(attributes.getValue("annotation")));
                break;
            }
            case "Relation": {
                this.relations.add(attributes.getValue("name"));
                break;
            }
        }
    }
    
    private List<AnnotationElement> createAnnotationElements(final String s) throws InternalError {
        final String[] split = s.split(",");
        final ArrayList list = new ArrayList();
        final String[] array = split;
        for (int length = array.length, i = 0; i < length; ++i) {
            final String trim = array[i].trim();
            final int index = trim.indexOf("(");
            if (index == -1) {
                list.add(new AnnotationElement(this.createAnnotationClass(trim)));
            }
            else {
                final int lastIndex = trim.lastIndexOf(")");
                if (lastIndex == -1) {
                    throw new InternalError("Expected closing parenthesis for 'XMLContentType'");
                }
                list.add(new AnnotationElement(this.createAnnotationClass(trim.substring(0, index)), trim.substring(index + 1, lastIndex)));
            }
        }
        return list;
    }
    
    private Class<? extends Annotation> createAnnotationClass(final String s) {
        try {
            if (!s.startsWith("jdk.jfr.")) {
                throw new IllegalStateException("Incorrect type " + s + ". Annotation class must be located in jdk.jfr package.");
            }
            return (Class<? extends Annotation>)Class.forName(s, true, null);
        }
        catch (final ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    private boolean getBoolean(final Attributes attributes, final String s, final boolean b) {
        final String value = attributes.getValue(s);
        return (value == null) ? b : Boolean.valueOf(value);
    }
    
    @Override
    public void endElement(final String s, final String s2, final String s3) {
        switch (s3) {
            case "Type":
            case "Event": {
                this.types.put(this.currentType.name, this.currentType);
                this.currentType = null;
                break;
            }
            case "Field": {
                this.currentType.fields.add(this.currentField);
                this.currentField = null;
                break;
            }
        }
    }
    
    public static List<Type> createTypes() throws IOException {
        final SAXParserImpl saxParserImpl = new SAXParserImpl();
        final MetadataHandler metadataHandler = new MetadataHandler();
        try (final BufferedInputStream bufferedInputStream = new BufferedInputStream(SecuritySupport.getResourceAsStream("/jdk/jfr/internal/types/metadata.xml"))) {
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.DEBUG, () -> "Parsing metadata.xml");
            try {
                saxParserImpl.parse(bufferedInputStream, metadataHandler);
                return metadataHandler.buildTypes();
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                throw new IOException(ex);
            }
        }
    }
    
    private List<Type> buildTypes() {
        this.removeXMLConvenience();
        final Map<String, Type> buildTypeMap = this.buildTypeMap();
        this.addFields(buildTypeMap, this.buildRelationMap(buildTypeMap));
        return this.trimTypes(buildTypeMap);
    }
    
    private Map<String, AnnotationElement> buildRelationMap(final Map<String, Type> map) {
        final HashMap hashMap = new HashMap();
        for (final String s : this.relations) {
            final Type type = new Type("jdk.types." + s, Type.SUPER_TYPE_ANNOTATION, this.eventTypeId++);
            type.setAnnotations(Collections.singletonList(new AnnotationElement((Class<? extends Annotation>)Relational.class)));
            hashMap.put(s, PrivateAccess.getInstance().newAnnotation(type, Collections.emptyList(), true));
            map.put(type.getName(), type);
        }
        return hashMap;
    }
    
    private List<Type> trimTypes(final Map<String, Type> map) {
        final ArrayList list = new ArrayList(map.size());
        for (final Type type : map.values()) {
            type.trimFields();
            list.add(type);
        }
        return list;
    }
    
    private void addFields(final Map<String, Type> map, final Map<String, AnnotationElement> map2) {
        for (final TypeElement typeElement : this.types.values()) {
            final Type type = map.get(typeElement.name);
            if (typeElement.isEvent) {
                final boolean b = typeElement.period != null;
                TypeLibrary.addImplicitFields(type, b, typeElement.startTime && !b, typeElement.thread, typeElement.stackTrace && !b, typeElement.cutoff);
            }
            for (final FieldElement fieldElement : typeElement.fields) {
                Type knownType = Type.getKnownType(fieldElement.typeName);
                if (knownType == null) {
                    knownType = Objects.requireNonNull(map.get(fieldElement.referenceType.name));
                }
                final ArrayList list = new ArrayList();
                if (fieldElement.unsigned) {
                    list.add(new AnnotationElement(Unsigned.class));
                }
                if (fieldElement.contentType != null) {
                    list.addAll(Objects.requireNonNull(this.xmlContentTypes.get(fieldElement.contentType)));
                }
                if (fieldElement.relation != null) {
                    list.add(Objects.requireNonNull(map2.get(fieldElement.relation)));
                }
                if (fieldElement.label != null) {
                    list.add(new AnnotationElement((Class<? extends Annotation>)Label.class, fieldElement.label));
                }
                if (fieldElement.experimental) {
                    list.add(new AnnotationElement(Experimental.class));
                }
                if (fieldElement.description != null) {
                    list.add(new AnnotationElement((Class<? extends Annotation>)Description.class, fieldElement.description));
                }
                if ("from".equals(fieldElement.transition)) {
                    list.add(new AnnotationElement(TransitionFrom.class));
                }
                if ("to".equals(fieldElement.transition)) {
                    list.add(new AnnotationElement(TransitionTo.class));
                }
                type.add(PrivateAccess.getInstance().newValueDescriptor(fieldElement.name, knownType, list, (int)(fieldElement.array ? 1 : 0), !fieldElement.struct && fieldElement.referenceType != null, null));
            }
        }
    }
    
    private Map<String, Type> buildTypeMap() {
        final HashMap hashMap = new HashMap();
        for (final Type type : Type.getKnownTypes()) {
            hashMap.put(type.getName(), type);
        }
        for (final TypeElement typeElement : this.types.values()) {
            final ArrayList annotations = new ArrayList();
            if (typeElement.category != null) {
                annotations.add(new AnnotationElement((Class<? extends Annotation>)Category.class, this.buildCategoryArray(typeElement.category)));
            }
            if (typeElement.label != null) {
                annotations.add(new AnnotationElement((Class<? extends Annotation>)Label.class, typeElement.label));
            }
            if (typeElement.description != null) {
                annotations.add(new AnnotationElement((Class<? extends Annotation>)Description.class, typeElement.description));
            }
            if (typeElement.isEvent) {
                if (typeElement.period != null) {
                    annotations.add(new AnnotationElement(Period.class, typeElement.period));
                }
                else {
                    if (typeElement.startTime) {
                        annotations.add(new AnnotationElement(Threshold.class, "0 ns"));
                    }
                    if (typeElement.stackTrace) {
                        annotations.add(new AnnotationElement((Class<? extends Annotation>)StackTrace.class, true));
                    }
                }
                if (typeElement.cutoff) {
                    annotations.add(new AnnotationElement(Cutoff.class, "infinity"));
                }
            }
            if (typeElement.experimental) {
                annotations.add(new AnnotationElement(Experimental.class));
            }
            Type type2;
            if (typeElement.isEvent) {
                annotations.add(new AnnotationElement((Class<? extends Annotation>)Enabled.class, false));
                type2 = new PlatformEventType(typeElement.name, this.eventTypeId++, false, true);
            }
            else {
                final boolean b = typeElement.name.endsWith("StackFrame") || typeElement.valueType;
                final String name = typeElement.name;
                final String s = null;
                long n;
                if (b) {
                    this.eventTypeId = (n = this.eventTypeId) + 1L;
                }
                else {
                    n = this.nextTypeId(typeElement.name);
                }
                type2 = new Type(name, s, n, false);
            }
            type2.setAnnotations(annotations);
            hashMap.put(typeElement.name, type2);
        }
        return hashMap;
    }
    
    private long nextTypeId(final String s) {
        if (Type.THREAD.getName().equals(s)) {
            return Type.THREAD.getId();
        }
        if (Type.STRING.getName().equals(s)) {
            return Type.STRING.getId();
        }
        if (Type.CLASS.getName().equals(s)) {
            return Type.CLASS.getId();
        }
        for (final Type type : Type.getKnownTypes()) {
            if (type.getName().equals(s)) {
                return type.getId();
            }
        }
        return this.structTypeId++;
    }
    
    private String[] buildCategoryArray(final String s) {
        final ArrayList list = new ArrayList();
        final StringBuilder sb = new StringBuilder();
        for (final char c : s.toCharArray()) {
            if (c == ',') {
                list.add(sb.toString().trim());
                sb.setLength(0);
            }
            else {
                sb.append(c);
            }
        }
        list.add(sb.toString().trim());
        return (String[])list.toArray(new String[0]);
    }
    
    private void removeXMLConvenience() {
        for (final TypeElement typeElement : this.types.values()) {
            final XmlType xmlType = this.xmlTypes.get(typeElement.name);
            if (xmlType != null && xmlType.javaType != null) {
                typeElement.name = xmlType.javaType;
            }
            else if (typeElement.isEvent) {
                typeElement.name = "jdk." + typeElement.name;
            }
            else {
                typeElement.name = "jdk.types." + typeElement.name;
            }
        }
        final Iterator<TypeElement> iterator2 = this.types.values().iterator();
        while (iterator2.hasNext()) {
            for (final FieldElement fieldElement : iterator2.next().fields) {
                fieldElement.referenceType = this.types.get(fieldElement.typeName);
                final XmlType xmlType2 = this.xmlTypes.get(fieldElement.typeName);
                if (xmlType2 != null) {
                    if (xmlType2.javaType != null) {
                        fieldElement.typeName = xmlType2.javaType;
                    }
                    if (xmlType2.contentType != null) {
                        fieldElement.contentType = xmlType2.contentType;
                    }
                    if (xmlType2.unsigned) {
                        fieldElement.unsigned = true;
                    }
                }
                if (fieldElement.struct && fieldElement.referenceType != null) {
                    fieldElement.referenceType.valueType = true;
                }
            }
        }
    }
    
    static class TypeElement
    {
        List<FieldElement> fields;
        String name;
        String label;
        String description;
        String category;
        String superType;
        String period;
        boolean thread;
        boolean startTime;
        boolean stackTrace;
        boolean cutoff;
        boolean isEvent;
        boolean experimental;
        boolean valueType;
        
        TypeElement() {
            this.fields = new ArrayList<FieldElement>();
        }
    }
    
    static class FieldElement
    {
        TypeElement referenceType;
        String name;
        String label;
        String description;
        String contentType;
        String typeName;
        String transition;
        String relation;
        boolean struct;
        boolean array;
        boolean experimental;
        boolean unsigned;
    }
    
    static class XmlType
    {
        String name;
        String javaType;
        String contentType;
        boolean unsigned;
    }
}
