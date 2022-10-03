package org.apache.commons.compress.harmony.unpack200;

import org.apache.commons.compress.harmony.unpack200.bytecode.RuntimeVisibleorInvisibleParameterAnnotationsAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.RuntimeVisibleorInvisibleAnnotationsAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.AnnotationDefaultAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.AnnotationsAttribute;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPLong;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPFloat;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPDouble;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInteger;
import java.util.List;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;

public class MetadataBandGroup
{
    private final String type;
    private final CpBands cpBands;
    private static CPUTF8 rvaUTF8;
    private static CPUTF8 riaUTF8;
    private static CPUTF8 rvpaUTF8;
    private static CPUTF8 ripaUTF8;
    private List attributes;
    public int[] param_NB;
    public int[] anno_N;
    public CPUTF8[][] type_RS;
    public int[][] pair_N;
    public CPUTF8[] name_RU;
    public int[] T;
    public CPInteger[] caseI_KI;
    public CPDouble[] caseD_KD;
    public CPFloat[] caseF_KF;
    public CPLong[] caseJ_KJ;
    public CPUTF8[] casec_RS;
    public String[] caseet_RS;
    public String[] caseec_RU;
    public CPUTF8[] cases_RU;
    public int[] casearray_N;
    public CPUTF8[] nesttype_RS;
    public int[] nestpair_N;
    public CPUTF8[] nestname_RU;
    private int caseI_KI_Index;
    private int caseD_KD_Index;
    private int caseF_KF_Index;
    private int caseJ_KJ_Index;
    private int casec_RS_Index;
    private int caseet_RS_Index;
    private int caseec_RU_Index;
    private int cases_RU_Index;
    private int casearray_N_Index;
    private int T_index;
    private int nesttype_RS_Index;
    private int nestpair_N_Index;
    private Iterator nestname_RU_Iterator;
    private int anno_N_Index;
    private int pair_N_Index;
    
    public static void setRvaAttributeName(final CPUTF8 cpUTF8Value) {
        MetadataBandGroup.rvaUTF8 = cpUTF8Value;
    }
    
    public static void setRiaAttributeName(final CPUTF8 cpUTF8Value) {
        MetadataBandGroup.riaUTF8 = cpUTF8Value;
    }
    
    public static void setRvpaAttributeName(final CPUTF8 cpUTF8Value) {
        MetadataBandGroup.rvpaUTF8 = cpUTF8Value;
    }
    
    public static void setRipaAttributeName(final CPUTF8 cpUTF8Value) {
        MetadataBandGroup.ripaUTF8 = cpUTF8Value;
    }
    
    public MetadataBandGroup(final String type, final CpBands cpBands) {
        this.type = type;
        this.cpBands = cpBands;
    }
    
    public List getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ArrayList();
            if (this.name_RU != null) {
                final Iterator name_RU_Iterator = Arrays.asList(this.name_RU).iterator();
                if (!this.type.equals("AD")) {
                    this.T_index = 0;
                }
                this.caseI_KI_Index = 0;
                this.caseD_KD_Index = 0;
                this.caseF_KF_Index = 0;
                this.caseJ_KJ_Index = 0;
                this.casec_RS_Index = 0;
                this.caseet_RS_Index = 0;
                this.caseec_RU_Index = 0;
                this.cases_RU_Index = 0;
                this.casearray_N_Index = 0;
                this.nesttype_RS_Index = 0;
                this.nestpair_N_Index = 0;
                this.nestname_RU_Iterator = Arrays.asList(this.nestname_RU).iterator();
                if (this.type.equals("RVA") || this.type.equals("RIA")) {
                    for (int i = 0; i < this.anno_N.length; ++i) {
                        this.attributes.add(this.getAttribute(this.anno_N[i], this.type_RS[i], this.pair_N[i], name_RU_Iterator));
                    }
                }
                else if (this.type.equals("RVPA") || this.type.equals("RIPA")) {
                    this.anno_N_Index = 0;
                    this.pair_N_Index = 0;
                    for (int i = 0; i < this.param_NB.length; ++i) {
                        this.attributes.add(this.getParameterAttribute(this.param_NB[i], name_RU_Iterator));
                    }
                }
            }
            else if (this.type.equals("AD")) {
                for (int j = 0; j < this.T.length; ++j) {
                    this.attributes.add(new AnnotationDefaultAttribute(new AnnotationsAttribute.ElementValue(this.T[j], this.getNextValue(this.T[j]))));
                }
            }
        }
        return this.attributes;
    }
    
    private Attribute getAttribute(final int numAnnotations, final CPUTF8[] types, final int[] pairCounts, final Iterator namesIterator) {
        final AnnotationsAttribute.Annotation[] annotations = new AnnotationsAttribute.Annotation[numAnnotations];
        for (int i = 0; i < numAnnotations; ++i) {
            annotations[i] = this.getAnnotation(types[i], pairCounts[i], namesIterator);
        }
        return new RuntimeVisibleorInvisibleAnnotationsAttribute(this.type.equals("RVA") ? MetadataBandGroup.rvaUTF8 : MetadataBandGroup.riaUTF8, annotations);
    }
    
    private Attribute getParameterAttribute(final int numParameters, final Iterator namesIterator) {
        final RuntimeVisibleorInvisibleParameterAnnotationsAttribute.ParameterAnnotation[] parameter_annotations = new RuntimeVisibleorInvisibleParameterAnnotationsAttribute.ParameterAnnotation[numParameters];
        for (int i = 0; i < numParameters; ++i) {
            final int numAnnotations = this.anno_N[this.anno_N_Index++];
            final int[] pairCounts = this.pair_N[this.pair_N_Index++];
            final AnnotationsAttribute.Annotation[] annotations = new AnnotationsAttribute.Annotation[numAnnotations];
            for (int j = 0; j < annotations.length; ++j) {
                annotations[j] = this.getAnnotation(this.type_RS[this.anno_N_Index - 1][j], pairCounts[j], namesIterator);
            }
            parameter_annotations[i] = new RuntimeVisibleorInvisibleParameterAnnotationsAttribute.ParameterAnnotation(annotations);
        }
        return new RuntimeVisibleorInvisibleParameterAnnotationsAttribute(this.type.equals("RVPA") ? MetadataBandGroup.rvpaUTF8 : MetadataBandGroup.ripaUTF8, parameter_annotations);
    }
    
    private AnnotationsAttribute.Annotation getAnnotation(final CPUTF8 type, final int pairCount, final Iterator namesIterator) {
        final CPUTF8[] elementNames = new CPUTF8[pairCount];
        final AnnotationsAttribute.ElementValue[] elementValues = new AnnotationsAttribute.ElementValue[pairCount];
        for (int j = 0; j < elementNames.length; ++j) {
            elementNames[j] = namesIterator.next();
            final int t = this.T[this.T_index++];
            elementValues[j] = new AnnotationsAttribute.ElementValue(t, this.getNextValue(t));
        }
        return new AnnotationsAttribute.Annotation(pairCount, type, elementNames, elementValues);
    }
    
    private Object getNextValue(final int t) {
        switch (t) {
            case 66:
            case 67:
            case 73:
            case 83:
            case 90: {
                return this.caseI_KI[this.caseI_KI_Index++];
            }
            case 68: {
                return this.caseD_KD[this.caseD_KD_Index++];
            }
            case 70: {
                return this.caseF_KF[this.caseF_KF_Index++];
            }
            case 74: {
                return this.caseJ_KJ[this.caseJ_KJ_Index++];
            }
            case 99: {
                return this.casec_RS[this.casec_RS_Index++];
            }
            case 101: {
                final String enumString = this.caseet_RS[this.caseet_RS_Index++] + ":" + this.caseec_RU[this.caseec_RU_Index++];
                return this.cpBands.cpNameAndTypeValue(enumString);
            }
            case 115: {
                return this.cases_RU[this.cases_RU_Index++];
            }
            case 91: {
                final int arraySize = this.casearray_N[this.casearray_N_Index++];
                final AnnotationsAttribute.ElementValue[] nestedArray = new AnnotationsAttribute.ElementValue[arraySize];
                for (int i = 0; i < arraySize; ++i) {
                    final int nextT = this.T[this.T_index++];
                    nestedArray[i] = new AnnotationsAttribute.ElementValue(nextT, this.getNextValue(nextT));
                }
                return nestedArray;
            }
            case 64: {
                final CPUTF8 type = this.nesttype_RS[this.nesttype_RS_Index++];
                final int numPairs = this.nestpair_N[this.nestpair_N_Index++];
                return this.getAnnotation(type, numPairs, this.nestname_RU_Iterator);
            }
            default: {
                return null;
            }
        }
    }
}
