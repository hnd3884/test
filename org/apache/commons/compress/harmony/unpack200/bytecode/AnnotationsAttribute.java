package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.DataOutputStream;

public abstract class AnnotationsAttribute extends Attribute
{
    public AnnotationsAttribute(final CPUTF8 attributeName) {
        super(attributeName);
    }
    
    public static class Annotation
    {
        private final int num_pairs;
        private final CPUTF8[] element_names;
        private final ElementValue[] element_values;
        private final CPUTF8 type;
        private int type_index;
        private int[] name_indexes;
        
        public Annotation(final int num_pairs, final CPUTF8 type, final CPUTF8[] element_names, final ElementValue[] element_values) {
            this.num_pairs = num_pairs;
            this.type = type;
            this.element_names = element_names;
            this.element_values = element_values;
        }
        
        public int getLength() {
            int length = 4;
            for (int i = 0; i < this.num_pairs; ++i) {
                length += 2;
                length += this.element_values[i].getLength();
            }
            return length;
        }
        
        public void resolve(final ClassConstantPool pool) {
            this.type.resolve(pool);
            this.type_index = pool.indexOf(this.type);
            this.name_indexes = new int[this.num_pairs];
            for (int i = 0; i < this.element_names.length; ++i) {
                this.element_names[i].resolve(pool);
                this.name_indexes[i] = pool.indexOf(this.element_names[i]);
                this.element_values[i].resolve(pool);
            }
        }
        
        public void writeBody(final DataOutputStream dos) throws IOException {
            dos.writeShort(this.type_index);
            dos.writeShort(this.num_pairs);
            for (int i = 0; i < this.num_pairs; ++i) {
                dos.writeShort(this.name_indexes[i]);
                this.element_values[i].writeBody(dos);
            }
        }
        
        public List getClassFileEntries() {
            final List entries = new ArrayList();
            for (int i = 0; i < this.element_names.length; ++i) {
                entries.add(this.element_names[i]);
                entries.addAll(this.element_values[i].getClassFileEntries());
            }
            entries.add(this.type);
            return entries;
        }
    }
    
    public static class ElementValue
    {
        private final Object value;
        private final int tag;
        private int constant_value_index;
        
        public ElementValue(final int tag, final Object value) {
            this.constant_value_index = -1;
            this.tag = tag;
            this.value = value;
        }
        
        public List getClassFileEntries() {
            final List entries = new ArrayList(1);
            if (this.value instanceof CPNameAndType) {
                entries.add(((CPNameAndType)this.value).name);
                entries.add(((CPNameAndType)this.value).descriptor);
            }
            else if (this.value instanceof ClassFileEntry) {
                entries.add(this.value);
            }
            else if (this.value instanceof ElementValue[]) {
                final ElementValue[] values = (ElementValue[])this.value;
                for (int i = 0; i < values.length; ++i) {
                    entries.addAll(values[i].getClassFileEntries());
                }
            }
            else if (this.value instanceof Annotation) {
                entries.addAll(((Annotation)this.value).getClassFileEntries());
            }
            return entries;
        }
        
        public void resolve(final ClassConstantPool pool) {
            if (this.value instanceof CPConstant) {
                ((CPConstant)this.value).resolve(pool);
                this.constant_value_index = pool.indexOf((ClassFileEntry)this.value);
            }
            else if (this.value instanceof CPClass) {
                ((CPClass)this.value).resolve(pool);
                this.constant_value_index = pool.indexOf((ClassFileEntry)this.value);
            }
            else if (this.value instanceof CPUTF8) {
                ((CPUTF8)this.value).resolve(pool);
                this.constant_value_index = pool.indexOf((ClassFileEntry)this.value);
            }
            else if (this.value instanceof CPNameAndType) {
                ((CPNameAndType)this.value).resolve(pool);
            }
            else if (this.value instanceof Annotation) {
                ((Annotation)this.value).resolve(pool);
            }
            else if (this.value instanceof ElementValue[]) {
                final ElementValue[] nestedValues = (ElementValue[])this.value;
                for (int i = 0; i < nestedValues.length; ++i) {
                    nestedValues[i].resolve(pool);
                }
            }
        }
        
        public void writeBody(final DataOutputStream dos) throws IOException {
            dos.writeByte(this.tag);
            if (this.constant_value_index != -1) {
                dos.writeShort(this.constant_value_index);
            }
            else if (this.value instanceof CPNameAndType) {
                ((CPNameAndType)this.value).writeBody(dos);
            }
            else if (this.value instanceof Annotation) {
                ((Annotation)this.value).writeBody(dos);
            }
            else {
                if (!(this.value instanceof ElementValue[])) {
                    throw new Error("");
                }
                final ElementValue[] nestedValues = (ElementValue[])this.value;
                dos.writeShort(nestedValues.length);
                for (int i = 0; i < nestedValues.length; ++i) {
                    nestedValues[i].writeBody(dos);
                }
            }
        }
        
        public int getLength() {
            switch (this.tag) {
                case 66:
                case 67:
                case 68:
                case 70:
                case 73:
                case 74:
                case 83:
                case 90:
                case 99:
                case 115: {
                    return 3;
                }
                case 101: {
                    return 5;
                }
                case 91: {
                    int length = 3;
                    final ElementValue[] nestedValues = (ElementValue[])this.value;
                    for (int i = 0; i < nestedValues.length; ++i) {
                        length += nestedValues[i].getLength();
                    }
                    return length;
                }
                case 64: {
                    return 1 + ((Annotation)this.value).getLength();
                }
                default: {
                    return 0;
                }
            }
        }
    }
}
