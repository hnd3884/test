package org.apache.commons.compress.harmony.unpack200;

import java.util.ArrayList;

public class IcTuple
{
    private final int cIndex;
    private final int c2Index;
    private final int nIndex;
    private final int tIndex;
    public static final int NESTED_CLASS_FLAG = 65536;
    protected String C;
    protected int F;
    protected String C2;
    protected String N;
    private boolean predictSimple;
    private boolean predictOuter;
    private String cachedOuterClassString;
    private String cachedSimpleClassName;
    private boolean initialized;
    private boolean anonymous;
    private boolean outerIsAnonymous;
    private boolean member;
    private int cachedOuterClassIndex;
    private int cachedSimpleClassNameIndex;
    private boolean hashcodeComputed;
    private int cachedHashCode;
    
    public IcTuple(final String C, final int F, final String C2, final String N, final int cIndex, final int c2Index, final int nIndex, final int tIndex) {
        this.member = true;
        this.cachedOuterClassIndex = -1;
        this.cachedSimpleClassNameIndex = -1;
        this.C = C;
        this.F = F;
        this.C2 = C2;
        this.N = N;
        this.cIndex = cIndex;
        this.c2Index = c2Index;
        this.nIndex = nIndex;
        this.tIndex = tIndex;
        if (null == N) {
            this.predictSimple = true;
        }
        if (null == C2) {
            this.predictOuter = true;
        }
        this.initializeClassStrings();
    }
    
    public boolean predicted() {
        return this.predictOuter || this.predictSimple;
    }
    
    public boolean nestedExplicitFlagSet() {
        return (this.F & 0x10000) == 0x10000;
    }
    
    public String[] innerBreakAtDollar(final String className) {
        final ArrayList resultList = new ArrayList();
        int start = 0;
        int index = 0;
        while (index < className.length()) {
            if (className.charAt(index) <= '$') {
                resultList.add(className.substring(start, index));
                start = index + 1;
            }
            if (++index >= className.length()) {
                resultList.add(className.substring(start));
            }
        }
        final String[] result = new String[resultList.size()];
        for (int i = 0; i < resultList.size(); ++i) {
            result[i] = resultList.get(i);
        }
        return result;
    }
    
    public String outerClassString() {
        return this.cachedOuterClassString;
    }
    
    public String simpleClassName() {
        return this.cachedSimpleClassName;
    }
    
    public String thisClassString() {
        if (this.predicted()) {
            return this.C;
        }
        return this.C2 + "$" + this.N;
    }
    
    public boolean isMember() {
        return this.member;
    }
    
    public boolean isAnonymous() {
        return this.anonymous;
    }
    
    public boolean outerIsAnonymous() {
        return this.outerIsAnonymous;
    }
    
    private boolean computeOuterIsAnonymous() {
        final String[] result = this.innerBreakAtDollar(this.cachedOuterClassString);
        if (result.length == 0) {
            throw new Error("Should have an outer before checking if it's anonymous");
        }
        for (int index = 0; index < result.length; ++index) {
            if (this.isAllDigits(result[index])) {
                return true;
            }
        }
        return false;
    }
    
    private void initializeClassStrings() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        if (!this.predictSimple) {
            this.cachedSimpleClassName = this.N;
        }
        if (!this.predictOuter) {
            this.cachedOuterClassString = this.C2;
        }
        final String[] nameComponents = this.innerBreakAtDollar(this.C);
        if (nameComponents.length == 0) {}
        if (nameComponents.length == 1) {}
        if (nameComponents.length < 2) {
            return;
        }
        final int lastPosition = nameComponents.length - 1;
        this.cachedSimpleClassName = nameComponents[lastPosition];
        this.cachedOuterClassString = "";
        for (int index = 0; index < lastPosition; ++index) {
            this.cachedOuterClassString += nameComponents[index];
            if (this.isAllDigits(nameComponents[index])) {
                this.member = false;
            }
            if (index + 1 != lastPosition) {
                this.cachedOuterClassString += '$';
            }
        }
        if (!this.predictSimple) {
            this.cachedSimpleClassName = this.N;
            this.cachedSimpleClassNameIndex = this.nIndex;
        }
        if (!this.predictOuter) {
            this.cachedOuterClassString = this.C2;
            this.cachedOuterClassIndex = this.c2Index;
        }
        if (this.isAllDigits(this.cachedSimpleClassName)) {
            this.anonymous = true;
            this.member = false;
            if (this.nestedExplicitFlagSet()) {
                this.member = true;
            }
        }
        this.outerIsAnonymous = this.computeOuterIsAnonymous();
    }
    
    private boolean isAllDigits(final String nameString) {
        if (null == nameString) {
            return false;
        }
        for (int index = 0; index < nameString.length(); ++index) {
            if (!Character.isDigit(nameString.charAt(index))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer();
        result.append("IcTuple ");
        result.append('(');
        result.append(this.simpleClassName());
        result.append(" in ");
        result.append(this.outerClassString());
        result.append(')');
        return result.toString();
    }
    
    public boolean nullSafeEquals(final String stringOne, final String stringTwo) {
        if (null == stringOne) {
            return null == stringTwo;
        }
        return stringOne.equals(stringTwo);
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        final IcTuple compareTuple = (IcTuple)object;
        return this.nullSafeEquals(this.C, compareTuple.C) && this.nullSafeEquals(this.C2, compareTuple.C2) && this.nullSafeEquals(this.N, compareTuple.N);
    }
    
    private void generateHashCode() {
        this.hashcodeComputed = true;
        this.cachedHashCode = 17;
        if (this.C != null) {
            this.cachedHashCode = this.C.hashCode();
        }
        if (this.C2 != null) {
            this.cachedHashCode = this.C2.hashCode();
        }
        if (this.N != null) {
            this.cachedHashCode = this.N.hashCode();
        }
    }
    
    @Override
    public int hashCode() {
        if (!this.hashcodeComputed) {
            this.generateHashCode();
        }
        return this.cachedHashCode;
    }
    
    public String getC() {
        return this.C;
    }
    
    public int getF() {
        return this.F;
    }
    
    public String getC2() {
        return this.C2;
    }
    
    public String getN() {
        return this.N;
    }
    
    public int getTupleIndex() {
        return this.tIndex;
    }
    
    public int thisClassIndex() {
        if (this.predicted()) {
            return this.cIndex;
        }
        return -1;
    }
    
    public int outerClassIndex() {
        return this.cachedOuterClassIndex;
    }
    
    public int simpleClassNameIndex() {
        return this.cachedSimpleClassNameIndex;
    }
}
