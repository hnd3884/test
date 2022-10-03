package jdk.jfr.internal;

import jdk.jfr.ValueDescriptor;
import jdk.jfr.SettingDescriptor;
import jdk.jfr.AnnotationElement;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.DataInput;

final class MetadataReader
{
    private final DataInput input;
    private final List<String> pool;
    private final MetadataDescriptor descriptor;
    private final Map<Long, Type> types;
    
    public MetadataReader(final DataInput input) throws IOException {
        this.types = new HashMap<Long, Type>();
        this.input = input;
        final int int1 = input.readInt();
        this.pool = new ArrayList<String>(int1);
        for (int i = 0; i < int1; ++i) {
            this.pool.add(input.readUTF());
        }
        this.descriptor = new MetadataDescriptor();
        final MetadataDescriptor.Element element = this.createElement();
        final MetadataDescriptor.Element element2 = element.elements("metadata").get(0);
        this.declareTypes(element2);
        this.defineTypes(element2);
        this.annotateTypes(element2);
        this.buildEvenTypes();
        final MetadataDescriptor.Element element3 = element.elements("region").get(0);
        this.descriptor.gmtOffset = element3.attribute("gmtOffset", 1L);
        this.descriptor.locale = element3.attribute("locale", "");
        this.descriptor.root = element;
        if (Logger.shouldLog(LogTag.JFR_SYSTEM_PARSER, LogLevel.TRACE)) {
            final ArrayList list = new ArrayList((Collection<? extends E>)this.types.values());
            Collections.sort((List<Object>)list, (type, type2) -> type.getName().compareTo(type2.getName()));
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                ((Type)iterator.next()).log("Found", LogTag.JFR_SYSTEM_PARSER, LogLevel.TRACE);
            }
        }
    }
    
    private String readString() throws IOException {
        return this.pool.get(this.readInt());
    }
    
    private int readInt() throws IOException {
        return this.input.readInt();
    }
    
    private MetadataDescriptor.Element createElement() throws IOException {
        final MetadataDescriptor.Element element = new MetadataDescriptor.Element(this.readString());
        for (int int1 = this.readInt(), i = 0; i < int1; ++i) {
            element.addAttribute(this.readString(), this.readString());
        }
        for (int int2 = this.readInt(), j = 0; j < int2; ++j) {
            element.add(this.createElement());
        }
        return element;
    }
    
    private void annotateTypes(final MetadataDescriptor.Element element) throws IOException {
        for (final MetadataDescriptor.Element element2 : element.elements("class")) {
            final Type type = this.getType("id", element2);
            final ArrayList<AnnotationElement> annotations = new ArrayList<AnnotationElement>();
            final Iterator<MetadataDescriptor.Element> iterator2 = element2.elements("annotation").iterator();
            while (iterator2.hasNext()) {
                annotations.add(this.makeAnnotation(iterator2.next()));
            }
            annotations.trimToSize();
            type.setAnnotations(annotations);
            int n = 0;
            if (type instanceof PlatformEventType) {
                final List<SettingDescriptor> allSettings = ((PlatformEventType)type).getAllSettings();
                for (final MetadataDescriptor.Element element3 : element2.elements("setting")) {
                    final ArrayList<AnnotationElement> list = new ArrayList<AnnotationElement>();
                    final Iterator<MetadataDescriptor.Element> iterator4 = element3.elements("annotation").iterator();
                    while (iterator4.hasNext()) {
                        list.add(this.makeAnnotation(iterator4.next()));
                    }
                    list.trimToSize();
                    PrivateAccess.getInstance().setAnnotations(allSettings.get(n), list);
                    ++n;
                }
            }
            int n2 = 0;
            final List<ValueDescriptor> fields = type.getFields();
            for (final MetadataDescriptor.Element element4 : element2.elements("field")) {
                final ArrayList<AnnotationElement> list2 = new ArrayList<AnnotationElement>();
                final Iterator<MetadataDescriptor.Element> iterator6 = element4.elements("annotation").iterator();
                while (iterator6.hasNext()) {
                    list2.add(this.makeAnnotation(iterator6.next()));
                }
                list2.trimToSize();
                PrivateAccess.getInstance().setAnnotations(fields.get(n2), list2);
                ++n2;
            }
        }
    }
    
    private AnnotationElement makeAnnotation(final MetadataDescriptor.Element element) throws IOException {
        final Type type = this.getType("class", element);
        final ArrayList list = new ArrayList();
        for (final ValueDescriptor valueDescriptor : type.getFields()) {
            if (valueDescriptor.isArray()) {
                final ArrayList list2 = new ArrayList();
                int n = 0;
                while (true) {
                    final String attribute = element.attribute(valueDescriptor.getName() + "-" + n);
                    if (attribute == null) {
                        break;
                    }
                    list2.add(this.objectify(valueDescriptor.getTypeName(), attribute));
                    ++n;
                }
                final Object primitiveArray = Utils.makePrimitiveArray(valueDescriptor.getTypeName(), list2);
                if (primitiveArray == null) {
                    throw new IOException("Unsupported type " + list2 + " in array");
                }
                list.add(primitiveArray);
            }
            else {
                list.add(this.objectify(valueDescriptor.getTypeName(), element.attribute(valueDescriptor.getName())));
            }
        }
        return PrivateAccess.getInstance().newAnnotation(type, list, false);
    }
    
    private Object objectify(final String s, final String s2) throws IOException {
        try {
            switch (s) {
                case "int": {
                    return Integer.valueOf(s2);
                }
                case "long": {
                    return Long.valueOf(s2);
                }
                case "double": {
                    return Double.valueOf(s2);
                }
                case "float": {
                    return Float.valueOf(s2);
                }
                case "short": {
                    return Short.valueOf(s2);
                }
                case "char": {
                    if (s2.length() != 1) {
                        throw new IOException("Unexpected size of char");
                    }
                    return s2.charAt(0);
                }
                case "byte": {
                    return Byte.valueOf(s2);
                }
                case "boolean": {
                    return Boolean.valueOf(s2);
                }
                case "java.lang.String": {
                    return s2;
                }
            }
        }
        catch (final IllegalArgumentException ex) {
            throw new IOException("Could not parse text representation of " + s);
        }
        throw new IOException("Unsupported type for annotation " + s);
    }
    
    private Type getType(final String s, final MetadataDescriptor.Element element) {
        final long longValue = element.longValue(s);
        final Type type = this.types.get(longValue);
        if (type == null) {
            throw new IllegalStateException("Type '" + longValue + "' is not defined for " + element.attribute("type"));
        }
        return type;
    }
    
    private void buildEvenTypes() {
        for (final Type type : this.descriptor.types) {
            if (type instanceof PlatformEventType) {
                this.descriptor.eventTypes.add(PrivateAccess.getInstance().newEventType((PlatformEventType)type));
            }
        }
    }
    
    private void defineTypes(final MetadataDescriptor.Element element) {
        for (final MetadataDescriptor.Element element2 : element.elements("class")) {
            final Type type = this.types.get(element2.attribute("id", -1L));
            for (final MetadataDescriptor.Element element3 : element2.elements("setting")) {
                ((PlatformEventType)type).add(PrivateAccess.getInstance().newSettingDescriptor(this.getType("class", element3), element3.attribute("name"), element3.attribute("name"), new ArrayList<AnnotationElement>(2)));
            }
            for (final MetadataDescriptor.Element element4 : element2.elements("field")) {
                type.add(PrivateAccess.getInstance().newValueDescriptor(element4.attribute("name"), this.getType("class", element4), new ArrayList<AnnotationElement>(), (int)element4.attribute("dimension", 0L), element4.attribute("constantPool") != null, null));
            }
            type.trimFields();
        }
    }
    
    private void declareTypes(final MetadataDescriptor.Element element) {
        for (final MetadataDescriptor.Element element2 : element.elements("class")) {
            final String attribute = element2.attribute("name");
            final String attribute2 = element2.attribute("superType");
            final boolean b = element2.attribute("simpleType") != null;
            final long attribute3 = element2.attribute("id", -1L);
            Type type;
            if (Type.SUPER_TYPE_EVENT.equals(attribute2)) {
                type = new PlatformEventType(attribute, attribute3, false, false);
            }
            else {
                type = new Type(attribute, attribute2, attribute3, false, b);
            }
            this.types.put(attribute3, type);
            this.descriptor.types.add(type);
        }
    }
    
    public MetadataDescriptor getDescriptor() {
        return this.descriptor;
    }
}
