package org.apache.commons.compress.harmony.pack200;

import java.util.Iterator;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.objectweb.asm.Attribute;
import java.util.List;

public class AttributeDefinitionBands extends BandSet
{
    public static final int CONTEXT_CLASS = 0;
    public static final int CONTEXT_CODE = 3;
    public static final int CONTEXT_FIELD = 1;
    public static final int CONTEXT_METHOD = 2;
    private final List classAttributeLayouts;
    private final List methodAttributeLayouts;
    private final List fieldAttributeLayouts;
    private final List codeAttributeLayouts;
    private final List attributeDefinitions;
    private final CpBands cpBands;
    private final Segment segment;
    
    public AttributeDefinitionBands(final Segment segment, final int effort, final Attribute[] attributePrototypes) {
        super(effort, segment.getSegmentHeader());
        this.classAttributeLayouts = new ArrayList();
        this.methodAttributeLayouts = new ArrayList();
        this.fieldAttributeLayouts = new ArrayList();
        this.codeAttributeLayouts = new ArrayList();
        this.attributeDefinitions = new ArrayList();
        this.cpBands = segment.getCpBands();
        this.segment = segment;
        final Map classLayouts = new HashMap();
        final Map methodLayouts = new HashMap();
        final Map fieldLayouts = new HashMap();
        final Map codeLayouts = new HashMap();
        for (int i = 0; i < attributePrototypes.length; ++i) {
            final NewAttribute newAttribute = (NewAttribute)attributePrototypes[i];
            if (!(newAttribute instanceof NewAttribute.ErrorAttribute) && !(newAttribute instanceof NewAttribute.PassAttribute) && !(newAttribute instanceof NewAttribute.StripAttribute)) {
                if (newAttribute.isContextClass()) {
                    classLayouts.put(newAttribute.type, newAttribute.getLayout());
                }
                if (newAttribute.isContextMethod()) {
                    methodLayouts.put(newAttribute.type, newAttribute.getLayout());
                }
                if (newAttribute.isContextField()) {
                    fieldLayouts.put(newAttribute.type, newAttribute.getLayout());
                }
                if (newAttribute.isContextCode()) {
                    codeLayouts.put(newAttribute.type, newAttribute.getLayout());
                }
            }
        }
        if (classLayouts.size() > 7) {
            this.segmentHeader.setHave_class_flags_hi(true);
        }
        if (methodLayouts.size() > 6) {
            this.segmentHeader.setHave_method_flags_hi(true);
        }
        if (fieldLayouts.size() > 10) {
            this.segmentHeader.setHave_field_flags_hi(true);
        }
        if (codeLayouts.size() > 15) {
            this.segmentHeader.setHave_code_flags_hi(true);
        }
        int[] availableClassIndices = { 25, 26, 27, 28, 29, 30, 31 };
        if (classLayouts.size() > 7) {
            availableClassIndices = this.addHighIndices(availableClassIndices);
        }
        this.addAttributeDefinitions(classLayouts, availableClassIndices, 0);
        int[] availableMethodIndices = { 26, 27, 28, 29, 30, 31 };
        if (this.methodAttributeLayouts.size() > 6) {
            availableMethodIndices = this.addHighIndices(availableMethodIndices);
        }
        this.addAttributeDefinitions(methodLayouts, availableMethodIndices, 2);
        int[] availableFieldIndices = { 18, 23, 24, 25, 26, 27, 28, 29, 30, 31 };
        if (this.fieldAttributeLayouts.size() > 10) {
            availableFieldIndices = this.addHighIndices(availableFieldIndices);
        }
        this.addAttributeDefinitions(fieldLayouts, availableFieldIndices, 1);
        int[] availableCodeIndices = { 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 };
        if (this.codeAttributeLayouts.size() > 15) {
            availableCodeIndices = this.addHighIndices(availableCodeIndices);
        }
        this.addAttributeDefinitions(codeLayouts, availableCodeIndices, 3);
    }
    
    public void finaliseBands() {
        this.addSyntheticDefinitions();
        this.segmentHeader.setAttribute_definition_count(this.attributeDefinitions.size());
    }
    
    @Override
    public void pack(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing attribute definition bands...");
        final int[] attributeDefinitionHeader = new int[this.attributeDefinitions.size()];
        final int[] attributeDefinitionName = new int[this.attributeDefinitions.size()];
        final int[] attributeDefinitionLayout = new int[this.attributeDefinitions.size()];
        for (int i = 0; i < attributeDefinitionLayout.length; ++i) {
            final AttributeDefinition def = this.attributeDefinitions.get(i);
            attributeDefinitionHeader[i] = (def.contextType | def.index + 1 << 2);
            attributeDefinitionName[i] = def.name.getIndex();
            attributeDefinitionLayout[i] = def.layout.getIndex();
        }
        byte[] encodedBand = this.encodeBandInt("attributeDefinitionHeader", attributeDefinitionHeader, Codec.BYTE1);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from attributeDefinitionHeader[" + attributeDefinitionHeader.length + "]");
        encodedBand = this.encodeBandInt("attributeDefinitionName", attributeDefinitionName, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from attributeDefinitionName[" + attributeDefinitionName.length + "]");
        encodedBand = this.encodeBandInt("attributeDefinitionLayout", attributeDefinitionLayout, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from attributeDefinitionLayout[" + attributeDefinitionLayout.length + "]");
    }
    
    private void addSyntheticDefinitions() {
        final boolean anySytheticClasses = this.segment.getClassBands().isAnySyntheticClasses();
        final boolean anySyntheticMethods = this.segment.getClassBands().isAnySyntheticMethods();
        final boolean anySyntheticFields = this.segment.getClassBands().isAnySyntheticFields();
        if (anySytheticClasses || anySyntheticMethods || anySyntheticFields) {
            final CPUTF8 syntheticUTF = this.cpBands.getCPUtf8("Synthetic");
            final CPUTF8 emptyUTF = this.cpBands.getCPUtf8("");
            if (anySytheticClasses) {
                this.attributeDefinitions.add(new AttributeDefinition(12, 0, syntheticUTF, emptyUTF));
            }
            if (anySyntheticMethods) {
                this.attributeDefinitions.add(new AttributeDefinition(12, 2, syntheticUTF, emptyUTF));
            }
            if (anySyntheticFields) {
                this.attributeDefinitions.add(new AttributeDefinition(12, 1, syntheticUTF, emptyUTF));
            }
        }
    }
    
    private int[] addHighIndices(final int[] availableIndices) {
        final int[] temp = new int[availableIndices.length + 32];
        for (int i = 0; i < availableIndices.length; ++i) {
            temp[i] = availableIndices[i];
        }
        int j = 32;
        for (int k = availableIndices.length; k < temp.length; ++k) {
            temp[k] = j;
            ++j;
        }
        return temp;
    }
    
    private void addAttributeDefinitions(final Map layouts, final int[] availableIndices, final int contextType) {
        final int i = 0;
        for (final String name : layouts.keySet()) {
            final String layout = layouts.get(name);
            final int index = availableIndices[0];
            final AttributeDefinition definition = new AttributeDefinition(index, contextType, this.cpBands.getCPUtf8(name), this.cpBands.getCPUtf8(layout));
            this.attributeDefinitions.add(definition);
            switch (contextType) {
                case 0: {
                    this.classAttributeLayouts.add(definition);
                    continue;
                }
                case 2: {
                    this.methodAttributeLayouts.add(definition);
                    continue;
                }
                case 1: {
                    this.fieldAttributeLayouts.add(definition);
                    continue;
                }
                case 3: {
                    this.codeAttributeLayouts.add(definition);
                    continue;
                }
            }
        }
    }
    
    public List getClassAttributeLayouts() {
        return this.classAttributeLayouts;
    }
    
    public List getMethodAttributeLayouts() {
        return this.methodAttributeLayouts;
    }
    
    public List getFieldAttributeLayouts() {
        return this.fieldAttributeLayouts;
    }
    
    public List getCodeAttributeLayouts() {
        return this.codeAttributeLayouts;
    }
    
    public static class AttributeDefinition
    {
        public int index;
        public int contextType;
        public CPUTF8 name;
        public CPUTF8 layout;
        
        public AttributeDefinition(final int index, final int contextType, final CPUTF8 name, final CPUTF8 layout) {
            this.index = index;
            this.contextType = contextType;
            this.name = name;
            this.layout = layout;
        }
    }
}
