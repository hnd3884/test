package jdk.jfr.internal;

import java.util.List;
import jdk.jfr.AnnotationElement;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.SettingDescriptor;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.DataOutput;

final class MetadataWriter
{
    private final MetadataDescriptor.Element metadata;
    private final MetadataDescriptor.Element root;
    
    public MetadataWriter(final MetadataDescriptor metadataDescriptor) {
        this.metadata = new MetadataDescriptor.Element("metadata");
        this.root = new MetadataDescriptor.Element("root");
        metadataDescriptor.getTypes().forEach(type -> this.makeTypeElement(this.metadata, type));
        this.root.add(this.metadata);
        final MetadataDescriptor.Element element = new MetadataDescriptor.Element("region");
        element.addAttribute("locale", metadataDescriptor.locale);
        element.addAttribute("gmtOffset", metadataDescriptor.gmtOffset);
        this.root.add(element);
    }
    
    public void writeBinary(final DataOutput dataOutput) throws IOException {
        final HashSet set = new HashSet(1000);
        this.buildStringPool(this.root, set);
        final LinkedHashMap linkedHashMap = new LinkedHashMap(set.size());
        int n = 0;
        this.writeInt(dataOutput, set.size());
        for (final String s : set) {
            linkedHashMap.put((Object)s, (Object)n);
            this.writeString(dataOutput, s);
            ++n;
        }
        this.write(dataOutput, this.root, (HashMap<String, Integer>)linkedHashMap);
    }
    
    private void writeString(final DataOutput dataOutput, final String s) throws IOException {
        if (s == null) {
            dataOutput.writeByte(0);
            return;
        }
        dataOutput.writeByte(4);
        final int length = s.length();
        this.writeInt(dataOutput, length);
        for (int i = 0; i < length; ++i) {
            this.writeInt(dataOutput, s.charAt(i));
        }
    }
    
    private void writeInt(final DataOutput dataOutput, final int n) throws IOException {
        final long n2 = (long)n & 0xFFFFFFFFL;
        if (n2 < 128L) {
            dataOutput.write((byte)n2);
            return;
        }
        dataOutput.write((byte)(n2 | 0x80L));
        final long n3 = n2 >> 7;
        if (n3 < 128L) {
            dataOutput.write((byte)n3);
            return;
        }
        dataOutput.write((byte)(n3 | 0x80L));
        final long n4 = n3 >> 7;
        if (n4 < 128L) {
            dataOutput.write((byte)n4);
            return;
        }
        dataOutput.write((byte)(n4 | 0x80L));
        final long n5 = n4 >> 7;
        if (n5 < 128L) {
            dataOutput.write((byte)n5);
            return;
        }
        dataOutput.write((byte)(n5 >> 7));
    }
    
    private void buildStringPool(final MetadataDescriptor.Element element, final Set<String> set) {
        set.add(element.name);
        for (final MetadataDescriptor.Attribute attribute : element.attributes) {
            set.add(attribute.name);
            set.add(attribute.value);
        }
        final Iterator<MetadataDescriptor.Element> iterator2 = element.elements.iterator();
        while (iterator2.hasNext()) {
            this.buildStringPool(iterator2.next(), set);
        }
    }
    
    private void write(final DataOutput dataOutput, final MetadataDescriptor.Element element, final HashMap<String, Integer> hashMap) throws IOException {
        this.writeInt(dataOutput, hashMap.get(element.name));
        this.writeInt(dataOutput, element.attributes.size());
        for (final MetadataDescriptor.Attribute attribute : element.attributes) {
            this.writeInt(dataOutput, hashMap.get(attribute.name));
            this.writeInt(dataOutput, hashMap.get(attribute.value));
        }
        this.writeInt(dataOutput, element.elements.size());
        final Iterator<MetadataDescriptor.Element> iterator2 = element.elements.iterator();
        while (iterator2.hasNext()) {
            this.write(dataOutput, iterator2.next(), hashMap);
        }
    }
    
    private void makeTypeElement(final MetadataDescriptor.Element element, final Type type) {
        final MetadataDescriptor.Element child = element.newChild("class");
        child.addAttribute("name", type.getName());
        final String superType = type.getSuperType();
        if (superType != null) {
            child.addAttribute("superType", superType);
        }
        if (type.isSimpleType()) {
            child.addAttribute("simpleType", true);
        }
        child.addAttribute("id", type.getId());
        if (type instanceof PlatformEventType) {
            final Iterator<SettingDescriptor> iterator = ((PlatformEventType)type).getSettings().iterator();
            while (iterator.hasNext()) {
                this.makeSettingElement(child, iterator.next());
            }
        }
        final Iterator<ValueDescriptor> iterator2 = type.getFields().iterator();
        while (iterator2.hasNext()) {
            this.makeFieldElement(child, iterator2.next());
        }
        final Iterator<AnnotationElement> iterator3 = type.getAnnotationElements().iterator();
        while (iterator3.hasNext()) {
            this.makeAnnotation(child, iterator3.next());
        }
    }
    
    private void makeSettingElement(final MetadataDescriptor.Element element, final SettingDescriptor settingDescriptor) {
        final MetadataDescriptor.Element child = element.newChild("setting");
        child.addAttribute("name", settingDescriptor.getName());
        child.addAttribute("class", settingDescriptor.getTypeId());
        child.addAttribute("defaultValue", settingDescriptor.getDefaultValue());
        final Iterator<AnnotationElement> iterator = settingDescriptor.getAnnotationElements().iterator();
        while (iterator.hasNext()) {
            this.makeAnnotation(child, iterator.next());
        }
    }
    
    private void makeFieldElement(final MetadataDescriptor.Element element, final ValueDescriptor valueDescriptor) {
        final MetadataDescriptor.Element child = element.newChild("field");
        child.addAttribute("name", valueDescriptor.getName());
        child.addAttribute("class", valueDescriptor.getTypeId());
        if (valueDescriptor.isArray()) {
            child.addAttribute("dimension", 1);
        }
        if (PrivateAccess.getInstance().isConstantPool(valueDescriptor)) {
            child.addAttribute("constantPool", true);
        }
        final Iterator<AnnotationElement> iterator = valueDescriptor.getAnnotationElements().iterator();
        while (iterator.hasNext()) {
            this.makeAnnotation(child, iterator.next());
        }
    }
    
    private void makeAnnotation(final MetadataDescriptor.Element element, final AnnotationElement annotationElement) {
        final MetadataDescriptor.Element child = element.newChild("annotation");
        child.addAttribute("class", annotationElement.getTypeId());
        final List<Object> values = annotationElement.getValues();
        int n = 0;
        for (final ValueDescriptor valueDescriptor : annotationElement.getValueDescriptors()) {
            final Object value = values.get(n++);
            if (valueDescriptor.isArray()) {
                child.addArrayAttribute(child, valueDescriptor.getName(), value);
            }
            else {
                child.addAttribute(valueDescriptor.getName(), value);
            }
        }
    }
}
