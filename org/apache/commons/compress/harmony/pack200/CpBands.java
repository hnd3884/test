package org.apache.commons.compress.harmony.pack200;

import org.objectweb.asm.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CpBands extends BandSet
{
    private final Set defaultAttributeNames;
    private final Set cp_Utf8;
    private final Set cp_Int;
    private final Set cp_Float;
    private final Set cp_Long;
    private final Set cp_Double;
    private final Set cp_String;
    private final Set cp_Class;
    private final Set cp_Signature;
    private final Set cp_Descr;
    private final Set cp_Field;
    private final Set cp_Method;
    private final Set cp_Imethod;
    private final Map stringsToCpUtf8;
    private final Map stringsToCpNameAndType;
    private final Map stringsToCpClass;
    private final Map stringsToCpSignature;
    private final Map stringsToCpMethod;
    private final Map stringsToCpField;
    private final Map stringsToCpIMethod;
    private final Map objectsToCPConstant;
    private final Segment segment;
    
    public CpBands(final Segment segment, final int effort) {
        super(effort, segment.getSegmentHeader());
        this.defaultAttributeNames = new HashSet();
        this.cp_Utf8 = new TreeSet();
        this.cp_Int = new TreeSet();
        this.cp_Float = new TreeSet();
        this.cp_Long = new TreeSet();
        this.cp_Double = new TreeSet();
        this.cp_String = new TreeSet();
        this.cp_Class = new TreeSet();
        this.cp_Signature = new TreeSet();
        this.cp_Descr = new TreeSet();
        this.cp_Field = new TreeSet();
        this.cp_Method = new TreeSet();
        this.cp_Imethod = new TreeSet();
        this.stringsToCpUtf8 = new HashMap();
        this.stringsToCpNameAndType = new HashMap();
        this.stringsToCpClass = new HashMap();
        this.stringsToCpSignature = new HashMap();
        this.stringsToCpMethod = new HashMap();
        this.stringsToCpField = new HashMap();
        this.stringsToCpIMethod = new HashMap();
        this.objectsToCPConstant = new HashMap();
        this.segment = segment;
        this.defaultAttributeNames.add("AnnotationDefault");
        this.defaultAttributeNames.add("RuntimeVisibleAnnotations");
        this.defaultAttributeNames.add("RuntimeInvisibleAnnotations");
        this.defaultAttributeNames.add("RuntimeVisibleParameterAnnotations");
        this.defaultAttributeNames.add("RuntimeInvisibleParameterAnnotations");
        this.defaultAttributeNames.add("Code");
        this.defaultAttributeNames.add("LineNumberTable");
        this.defaultAttributeNames.add("LocalVariableTable");
        this.defaultAttributeNames.add("LocalVariableTypeTable");
        this.defaultAttributeNames.add("ConstantValue");
        this.defaultAttributeNames.add("Deprecated");
        this.defaultAttributeNames.add("EnclosingMethod");
        this.defaultAttributeNames.add("Exceptions");
        this.defaultAttributeNames.add("InnerClasses");
        this.defaultAttributeNames.add("Signature");
        this.defaultAttributeNames.add("SourceFile");
    }
    
    @Override
    public void pack(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing constant pool bands...");
        this.writeCpUtf8(out);
        this.writeCpInt(out);
        this.writeCpFloat(out);
        this.writeCpLong(out);
        this.writeCpDouble(out);
        this.writeCpString(out);
        this.writeCpClass(out);
        this.writeCpSignature(out);
        this.writeCpDescr(out);
        this.writeCpMethodOrField(this.cp_Field, out, "cp_Field");
        this.writeCpMethodOrField(this.cp_Method, out, "cp_Method");
        this.writeCpMethodOrField(this.cp_Imethod, out, "cp_Imethod");
    }
    
    private void writeCpUtf8(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + this.cp_Utf8.size() + " UTF8 entries...");
        final int[] cpUtf8Prefix = new int[this.cp_Utf8.size() - 2];
        final int[] cpUtf8Suffix = new int[this.cp_Utf8.size() - 1];
        final List chars = new ArrayList();
        final List bigSuffix = new ArrayList();
        final List bigChars = new ArrayList();
        final Object[] cpUtf8Array = this.cp_Utf8.toArray();
        final String first = ((CPUTF8)cpUtf8Array[1]).getUnderlyingString();
        cpUtf8Suffix[0] = first.length();
        this.addCharacters(chars, first.toCharArray());
        for (int i = 2; i < cpUtf8Array.length; ++i) {
            final char[] previous = ((CPUTF8)cpUtf8Array[i - 1]).getUnderlyingString().toCharArray();
            String currentStr = ((CPUTF8)cpUtf8Array[i]).getUnderlyingString();
            final char[] current = currentStr.toCharArray();
            int prefix = 0;
            for (int j = 0; j < previous.length && previous[j] == current[j]; ++j) {
                ++prefix;
            }
            cpUtf8Prefix[i - 2] = prefix;
            currentStr = currentStr.substring(prefix);
            final char[] suffix = currentStr.toCharArray();
            if (suffix.length > 1000) {
                cpUtf8Suffix[i - 1] = 0;
                bigSuffix.add(suffix.length);
                this.addCharacters(bigChars, suffix);
            }
            else {
                cpUtf8Suffix[i - 1] = suffix.length;
                this.addCharacters(chars, suffix);
            }
        }
        final int[] cpUtf8Chars = new int[chars.size()];
        final int[] cpUtf8BigSuffix = new int[bigSuffix.size()];
        final int[][] cpUtf8BigChars = new int[bigSuffix.size()][];
        for (int k = 0; k < cpUtf8Chars.length; ++k) {
            cpUtf8Chars[k] = chars.get(k);
        }
        for (int k = 0; k < cpUtf8BigSuffix.length; ++k) {
            final int numBigChars = bigSuffix.get(k);
            cpUtf8BigSuffix[k] = numBigChars;
            cpUtf8BigChars[k] = new int[numBigChars];
            for (int j = 0; j < numBigChars; ++j) {
                cpUtf8BigChars[k][j] = bigChars.remove(0);
            }
        }
        byte[] encodedBand = this.encodeBandInt("cpUtf8Prefix", cpUtf8Prefix, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8Prefix[" + cpUtf8Prefix.length + "]");
        encodedBand = this.encodeBandInt("cpUtf8Suffix", cpUtf8Suffix, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8Suffix[" + cpUtf8Suffix.length + "]");
        encodedBand = this.encodeBandInt("cpUtf8Chars", cpUtf8Chars, Codec.CHAR3);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8Chars[" + cpUtf8Chars.length + "]");
        encodedBand = this.encodeBandInt("cpUtf8BigSuffix", cpUtf8BigSuffix, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8BigSuffix[" + cpUtf8BigSuffix.length + "]");
        for (int l = 0; l < cpUtf8BigChars.length; ++l) {
            encodedBand = this.encodeBandInt("cpUtf8BigChars " + l, cpUtf8BigChars[l], Codec.DELTA5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpUtf8BigChars" + l + "[" + cpUtf8BigChars[l].length + "]");
        }
    }
    
    private void addCharacters(final List chars, final char[] charArray) {
        for (int i = 0; i < charArray.length; ++i) {
            chars.add(charArray[i]);
        }
    }
    
    private void writeCpInt(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + this.cp_Int.size() + " Integer entries...");
        final int[] cpInt = new int[this.cp_Int.size()];
        int i = 0;
        for (final CPInt integer : this.cp_Int) {
            cpInt[i] = integer.getInt();
            ++i;
        }
        final byte[] encodedBand = this.encodeBandInt("cp_Int", cpInt, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Int[" + cpInt.length + "]");
    }
    
    private void writeCpFloat(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + this.cp_Float.size() + " Float entries...");
        final int[] cpFloat = new int[this.cp_Float.size()];
        int i = 0;
        for (final CPFloat fl : this.cp_Float) {
            cpFloat[i] = Float.floatToIntBits(fl.getFloat());
            ++i;
        }
        final byte[] encodedBand = this.encodeBandInt("cp_Float", cpFloat, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Float[" + cpFloat.length + "]");
    }
    
    private void writeCpLong(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + this.cp_Long.size() + " Long entries...");
        final int[] highBits = new int[this.cp_Long.size()];
        final int[] loBits = new int[this.cp_Long.size()];
        int i = 0;
        for (final CPLong lng : this.cp_Long) {
            final long l = lng.getLong();
            highBits[i] = (int)(l >> 32);
            loBits[i] = (int)l;
            ++i;
        }
        byte[] encodedBand = this.encodeBandInt("cp_Long_hi", highBits, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Long_hi[" + highBits.length + "]");
        encodedBand = this.encodeBandInt("cp_Long_lo", loBits, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Long_lo[" + loBits.length + "]");
    }
    
    private void writeCpDouble(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + this.cp_Double.size() + " Double entries...");
        final int[] highBits = new int[this.cp_Double.size()];
        final int[] loBits = new int[this.cp_Double.size()];
        int i = 0;
        for (final CPDouble dbl : this.cp_Double) {
            final long l = Double.doubleToLongBits(dbl.getDouble());
            highBits[i] = (int)(l >> 32);
            loBits[i] = (int)l;
            ++i;
        }
        byte[] encodedBand = this.encodeBandInt("cp_Double_hi", highBits, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Double_hi[" + highBits.length + "]");
        encodedBand = this.encodeBandInt("cp_Double_lo", loBits, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Double_lo[" + loBits.length + "]");
    }
    
    private void writeCpString(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + this.cp_String.size() + " String entries...");
        final int[] cpString = new int[this.cp_String.size()];
        int i = 0;
        for (final CPString cpStr : this.cp_String) {
            cpString[i] = cpStr.getIndexInCpUtf8();
            ++i;
        }
        final byte[] encodedBand = this.encodeBandInt("cpString", cpString, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpString[" + cpString.length + "]");
    }
    
    private void writeCpClass(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + this.cp_Class.size() + " Class entries...");
        final int[] cpClass = new int[this.cp_Class.size()];
        int i = 0;
        for (final CPClass cpCl : this.cp_Class) {
            cpClass[i] = cpCl.getIndexInCpUtf8();
            ++i;
        }
        final byte[] encodedBand = this.encodeBandInt("cpClass", cpClass, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpClass[" + cpClass.length + "]");
    }
    
    private void writeCpSignature(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + this.cp_Signature.size() + " Signature entries...");
        final int[] cpSignatureForm = new int[this.cp_Signature.size()];
        final List classes = new ArrayList();
        int i = 0;
        for (final CPSignature cpS : this.cp_Signature) {
            classes.addAll(cpS.getClasses());
            cpSignatureForm[i] = cpS.getIndexInCpUtf8();
            ++i;
        }
        final int[] cpSignatureClasses = new int[classes.size()];
        for (int j = 0; j < cpSignatureClasses.length; ++j) {
            cpSignatureClasses[j] = classes.get(j).getIndex();
        }
        byte[] encodedBand = this.encodeBandInt("cpSignatureForm", cpSignatureForm, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpSignatureForm[" + cpSignatureForm.length + "]");
        encodedBand = this.encodeBandInt("cpSignatureClasses", cpSignatureClasses, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cpSignatureClasses[" + cpSignatureClasses.length + "]");
    }
    
    private void writeCpDescr(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + this.cp_Descr.size() + " Descriptor entries...");
        final int[] cpDescrName = new int[this.cp_Descr.size()];
        final int[] cpDescrType = new int[this.cp_Descr.size()];
        int i = 0;
        for (final CPNameAndType nameAndType : this.cp_Descr) {
            cpDescrName[i] = nameAndType.getNameIndex();
            cpDescrType[i] = nameAndType.getTypeIndex();
            ++i;
        }
        byte[] encodedBand = this.encodeBandInt("cp_Descr_Name", cpDescrName, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Descr_Name[" + cpDescrName.length + "]");
        encodedBand = this.encodeBandInt("cp_Descr_Type", cpDescrType, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from cp_Descr_Type[" + cpDescrType.length + "]");
    }
    
    private void writeCpMethodOrField(final Set cp, final OutputStream out, final String name) throws IOException, Pack200Exception {
        PackingUtils.log("Writing " + cp.size() + " Method and Field entries...");
        final int[] cp_methodOrField_class = new int[cp.size()];
        final int[] cp_methodOrField_desc = new int[cp.size()];
        int i = 0;
        for (final CPMethodOrField mOrF : cp) {
            cp_methodOrField_class[i] = mOrF.getClassIndex();
            cp_methodOrField_desc[i] = mOrF.getDescIndex();
            ++i;
        }
        byte[] encodedBand = this.encodeBandInt(name + "_class", cp_methodOrField_class, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + name + "_class[" + cp_methodOrField_class.length + "]");
        encodedBand = this.encodeBandInt(name + "_desc", cp_methodOrField_desc, Codec.UDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + name + "_desc[" + cp_methodOrField_desc.length + "]");
    }
    
    public void finaliseBands() {
        this.addCPUtf8("");
        this.removeSignaturesFromCpUTF8();
        this.addIndices();
        this.segmentHeader.setCp_Utf8_count(this.cp_Utf8.size());
        this.segmentHeader.setCp_Int_count(this.cp_Int.size());
        this.segmentHeader.setCp_Float_count(this.cp_Float.size());
        this.segmentHeader.setCp_Long_count(this.cp_Long.size());
        this.segmentHeader.setCp_Double_count(this.cp_Double.size());
        this.segmentHeader.setCp_String_count(this.cp_String.size());
        this.segmentHeader.setCp_Class_count(this.cp_Class.size());
        this.segmentHeader.setCp_Signature_count(this.cp_Signature.size());
        this.segmentHeader.setCp_Descr_count(this.cp_Descr.size());
        this.segmentHeader.setCp_Field_count(this.cp_Field.size());
        this.segmentHeader.setCp_Method_count(this.cp_Method.size());
        this.segmentHeader.setCp_Imethod_count(this.cp_Imethod.size());
    }
    
    private void removeSignaturesFromCpUTF8() {
        for (final CPSignature signature : this.cp_Signature) {
            final String sigStr = signature.getUnderlyingString();
            final CPUTF8 utf8 = signature.getSignatureForm();
            final String form = utf8.getUnderlyingString();
            if (!sigStr.equals(form)) {
                this.removeCpUtf8(sigStr);
            }
        }
    }
    
    private void addIndices() {
        final Set[] sets = { this.cp_Utf8, this.cp_Int, this.cp_Float, this.cp_Long, this.cp_Double, this.cp_String, this.cp_Class, this.cp_Signature, this.cp_Descr, this.cp_Field, this.cp_Method, this.cp_Imethod };
        for (int i = 0; i < sets.length; ++i) {
            int j = 0;
            for (final ConstantPoolEntry entry : sets[i]) {
                entry.setIndex(j);
                ++j;
            }
        }
        final Map classNameToIndex = new HashMap();
        for (final CPMethodOrField mOrF : this.cp_Field) {
            final CPClass className = mOrF.getClassName();
            final Integer index = classNameToIndex.get(className);
            if (index == null) {
                classNameToIndex.put(className, 1);
                mOrF.setIndexInClass(0);
            }
            else {
                final int theIndex = index;
                mOrF.setIndexInClass(theIndex);
                classNameToIndex.put(className, theIndex + 1);
            }
        }
        classNameToIndex.clear();
        final Map classNameToConstructorIndex = new HashMap();
        for (final CPMethodOrField mOrF2 : this.cp_Method) {
            final CPClass className2 = mOrF2.getClassName();
            final Integer index2 = classNameToIndex.get(className2);
            if (index2 == null) {
                classNameToIndex.put(className2, 1);
                mOrF2.setIndexInClass(0);
            }
            else {
                final int theIndex2 = index2;
                mOrF2.setIndexInClass(theIndex2);
                classNameToIndex.put(className2, theIndex2 + 1);
            }
            if (mOrF2.getDesc().getName().equals("<init>")) {
                final Integer constructorIndex = classNameToConstructorIndex.get(className2);
                if (constructorIndex == null) {
                    classNameToConstructorIndex.put(className2, 1);
                    mOrF2.setIndexInClassForConstructor(0);
                }
                else {
                    final int theIndex3 = constructorIndex;
                    mOrF2.setIndexInClassForConstructor(theIndex3);
                    classNameToConstructorIndex.put(className2, theIndex3 + 1);
                }
            }
        }
    }
    
    private void removeCpUtf8(final String string) {
        final CPUTF8 utf8 = this.stringsToCpUtf8.get(string);
        if (utf8 != null && this.stringsToCpClass.get(string) == null) {
            this.stringsToCpUtf8.remove(string);
            this.cp_Utf8.remove(utf8);
        }
    }
    
    void addCPUtf8(final String utf8) {
        this.getCPUtf8(utf8);
    }
    
    public CPUTF8 getCPUtf8(final String utf8) {
        if (utf8 == null) {
            return null;
        }
        CPUTF8 cpUtf8 = this.stringsToCpUtf8.get(utf8);
        if (cpUtf8 == null) {
            cpUtf8 = new CPUTF8(utf8);
            this.cp_Utf8.add(cpUtf8);
            this.stringsToCpUtf8.put(utf8, cpUtf8);
        }
        return cpUtf8;
    }
    
    public CPSignature getCPSignature(final String signature) {
        if (signature == null) {
            return null;
        }
        CPSignature cpS = this.stringsToCpSignature.get(signature);
        if (cpS == null) {
            final List cpClasses = new ArrayList();
            CPUTF8 signatureUTF8;
            if (signature.length() > 1 && signature.indexOf(76) != -1) {
                final List classes = new ArrayList();
                final char[] chars = signature.toCharArray();
                final StringBuffer signatureString = new StringBuffer();
                for (int i = 0; i < chars.length; ++i) {
                    signatureString.append(chars[i]);
                    if (chars[i] == 'L') {
                        final StringBuffer className = new StringBuffer();
                        for (int j = i + 1; j < chars.length; ++j) {
                            final char c = chars[j];
                            if (!Character.isLetter(c) && !Character.isDigit(c) && c != '/' && c != '$' && c != '_') {
                                classes.add(className.toString());
                                i = j - 1;
                                break;
                            }
                            className.append(c);
                        }
                    }
                }
                this.removeCpUtf8(signature);
                for (String className2 : classes) {
                    CPClass cpClass = null;
                    if (className2 != null) {
                        className2 = className2.replace('.', '/');
                        cpClass = this.stringsToCpClass.get(className2);
                        if (cpClass == null) {
                            final CPUTF8 cpUtf8 = this.getCPUtf8(className2);
                            cpClass = new CPClass(cpUtf8);
                            this.cp_Class.add(cpClass);
                            this.stringsToCpClass.put(className2, cpClass);
                        }
                    }
                    cpClasses.add(cpClass);
                }
                signatureUTF8 = this.getCPUtf8(signatureString.toString());
            }
            else {
                signatureUTF8 = this.getCPUtf8(signature);
            }
            cpS = new CPSignature(signature, signatureUTF8, cpClasses);
            this.cp_Signature.add(cpS);
            this.stringsToCpSignature.put(signature, cpS);
        }
        return cpS;
    }
    
    public CPClass getCPClass(String className) {
        if (className == null) {
            return null;
        }
        className = className.replace('.', '/');
        CPClass cpClass = this.stringsToCpClass.get(className);
        if (cpClass == null) {
            final CPUTF8 cpUtf8 = this.getCPUtf8(className);
            cpClass = new CPClass(cpUtf8);
            this.cp_Class.add(cpClass);
            this.stringsToCpClass.put(className, cpClass);
        }
        if (cpClass.isInnerClass()) {
            this.segment.getClassBands().currentClassReferencesInnerClass(cpClass);
        }
        return cpClass;
    }
    
    public void addCPClass(final String className) {
        this.getCPClass(className);
    }
    
    public CPNameAndType getCPNameAndType(final String name, final String signature) {
        final String descr = name + ":" + signature;
        CPNameAndType nameAndType = this.stringsToCpNameAndType.get(descr);
        if (nameAndType == null) {
            nameAndType = new CPNameAndType(this.getCPUtf8(name), this.getCPSignature(signature));
            this.stringsToCpNameAndType.put(descr, nameAndType);
            this.cp_Descr.add(nameAndType);
        }
        return nameAndType;
    }
    
    public CPMethodOrField getCPField(final CPClass cpClass, final String name, final String desc) {
        final String key = cpClass.toString() + ":" + name + ":" + desc;
        CPMethodOrField cpF = this.stringsToCpField.get(key);
        if (cpF == null) {
            final CPNameAndType nAndT = this.getCPNameAndType(name, desc);
            cpF = new CPMethodOrField(cpClass, nAndT);
            this.cp_Field.add(cpF);
            this.stringsToCpField.put(key, cpF);
        }
        return cpF;
    }
    
    public CPConstant getConstant(final Object value) {
        CPConstant constant = this.objectsToCPConstant.get(value);
        if (constant == null) {
            if (value instanceof Integer) {
                constant = new CPInt((int)value);
                this.cp_Int.add(constant);
            }
            else if (value instanceof Long) {
                constant = new CPLong((long)value);
                this.cp_Long.add(constant);
            }
            else if (value instanceof Float) {
                constant = new CPFloat((float)value);
                this.cp_Float.add(constant);
            }
            else if (value instanceof Double) {
                constant = new CPDouble((double)value);
                this.cp_Double.add(constant);
            }
            else if (value instanceof String) {
                constant = new CPString(this.getCPUtf8((String)value));
                this.cp_String.add(constant);
            }
            else if (value instanceof Type) {
                String className = ((Type)value).getClassName();
                if (className.endsWith("[]")) {
                    for (className = "[L" + className.substring(0, className.length() - 2); className.endsWith("[]"); className = "[" + className.substring(0, className.length() - 2)) {}
                    className += ";";
                }
                constant = this.getCPClass(className);
            }
            this.objectsToCPConstant.put(value, constant);
        }
        return constant;
    }
    
    public CPMethodOrField getCPMethod(final CPClass cpClass, final String name, final String desc) {
        final String key = cpClass.toString() + ":" + name + ":" + desc;
        CPMethodOrField cpM = this.stringsToCpMethod.get(key);
        if (cpM == null) {
            final CPNameAndType nAndT = this.getCPNameAndType(name, desc);
            cpM = new CPMethodOrField(cpClass, nAndT);
            this.cp_Method.add(cpM);
            this.stringsToCpMethod.put(key, cpM);
        }
        return cpM;
    }
    
    public CPMethodOrField getCPIMethod(final CPClass cpClass, final String name, final String desc) {
        final String key = cpClass.toString() + ":" + name + ":" + desc;
        CPMethodOrField cpIM = this.stringsToCpIMethod.get(key);
        if (cpIM == null) {
            final CPNameAndType nAndT = this.getCPNameAndType(name, desc);
            cpIM = new CPMethodOrField(cpClass, nAndT);
            this.cp_Imethod.add(cpIM);
            this.stringsToCpIMethod.put(key, cpIM);
        }
        return cpIM;
    }
    
    public CPMethodOrField getCPField(final String owner, final String name, final String desc) {
        return this.getCPField(this.getCPClass(owner), name, desc);
    }
    
    public CPMethodOrField getCPMethod(final String owner, final String name, final String desc) {
        return this.getCPMethod(this.getCPClass(owner), name, desc);
    }
    
    public CPMethodOrField getCPIMethod(final String owner, final String name, final String desc) {
        return this.getCPIMethod(this.getCPClass(owner), name, desc);
    }
    
    public boolean existsCpClass(final String className) {
        final CPClass cpClass = this.stringsToCpClass.get(className);
        return cpClass != null;
    }
}
