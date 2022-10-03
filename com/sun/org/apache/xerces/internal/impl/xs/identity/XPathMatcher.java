package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.util.IntStack;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

public class XPathMatcher
{
    protected static final boolean DEBUG_ALL = false;
    protected static final boolean DEBUG_METHODS = false;
    protected static final boolean DEBUG_METHODS2 = false;
    protected static final boolean DEBUG_METHODS3 = false;
    protected static final boolean DEBUG_MATCH = false;
    protected static final boolean DEBUG_STACK = false;
    protected static final boolean DEBUG_ANY = false;
    protected static final int MATCHED = 1;
    protected static final int MATCHED_ATTRIBUTE = 3;
    protected static final int MATCHED_DESCENDANT = 5;
    protected static final int MATCHED_DESCENDANT_PREVIOUS = 13;
    private XPath.LocationPath[] fLocationPaths;
    private int[] fMatched;
    protected Object fMatchedString;
    private IntStack[] fStepIndexes;
    private int[] fCurrentStep;
    private int[] fNoMatchDepth;
    final QName fQName;
    
    public XPathMatcher(final XPath xpath) {
        this.fQName = new QName();
        this.fLocationPaths = xpath.getLocationPaths();
        this.fStepIndexes = new IntStack[this.fLocationPaths.length];
        for (int i = 0; i < this.fStepIndexes.length; ++i) {
            this.fStepIndexes[i] = new IntStack();
        }
        this.fCurrentStep = new int[this.fLocationPaths.length];
        this.fNoMatchDepth = new int[this.fLocationPaths.length];
        this.fMatched = new int[this.fLocationPaths.length];
    }
    
    public boolean isMatched() {
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            if ((this.fMatched[i] & 0x1) == 0x1 && (this.fMatched[i] & 0xD) != 0xD && (this.fNoMatchDepth[i] == 0 || (this.fMatched[i] & 0x5) == 0x5)) {
                return true;
            }
        }
        return false;
    }
    
    protected void handleContent(final XSTypeDefinition type, final boolean nillable, final Object value, final short valueType, final ShortList itemValueType) {
    }
    
    protected void matched(final Object actualValue, final short valueType, final ShortList itemValueType, final boolean isNil) {
    }
    
    public void startDocumentFragment() {
        this.fMatchedString = null;
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            this.fStepIndexes[i].clear();
            this.fCurrentStep[i] = 0;
            this.fNoMatchDepth[i] = 0;
            this.fMatched[i] = 0;
        }
    }
    
    public void startElement(final QName element, final XMLAttributes attributes) {
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            final int startStep = this.fCurrentStep[i];
            this.fStepIndexes[i].push(startStep);
            if ((this.fMatched[i] & 0x5) == 0x1 || this.fNoMatchDepth[i] > 0) {
                final int[] fNoMatchDepth = this.fNoMatchDepth;
                final int n = i;
                ++fNoMatchDepth[n];
            }
            else {
                if ((this.fMatched[i] & 0x5) == 0x5) {
                    this.fMatched[i] = 13;
                }
                final XPath.Step[] steps = this.fLocationPaths[i].steps;
                while (this.fCurrentStep[i] < steps.length && steps[this.fCurrentStep[i]].axis.type == 3) {
                    final int[] fCurrentStep = this.fCurrentStep;
                    final int n2 = i;
                    ++fCurrentStep[n2];
                }
                if (this.fCurrentStep[i] == steps.length) {
                    this.fMatched[i] = 1;
                }
                else {
                    final int descendantStep = this.fCurrentStep[i];
                    while (this.fCurrentStep[i] < steps.length && steps[this.fCurrentStep[i]].axis.type == 4) {
                        final int[] fCurrentStep2 = this.fCurrentStep;
                        final int n3 = i;
                        ++fCurrentStep2[n3];
                    }
                    final boolean sawDescendant = this.fCurrentStep[i] > descendantStep;
                    if (this.fCurrentStep[i] == steps.length) {
                        final int[] fNoMatchDepth2 = this.fNoMatchDepth;
                        final int n4 = i;
                        ++fNoMatchDepth2[n4];
                    }
                    else {
                        if ((this.fCurrentStep[i] == startStep || this.fCurrentStep[i] > descendantStep) && steps[this.fCurrentStep[i]].axis.type == 1) {
                            final XPath.Step step = steps[this.fCurrentStep[i]];
                            final XPath.NodeTest nodeTest = step.nodeTest;
                            if (nodeTest.type == 1 && !nodeTest.name.equals(element)) {
                                if (this.fCurrentStep[i] > descendantStep) {
                                    this.fCurrentStep[i] = descendantStep;
                                    continue;
                                }
                                final int[] fNoMatchDepth3 = this.fNoMatchDepth;
                                final int n5 = i;
                                ++fNoMatchDepth3[n5];
                                continue;
                            }
                            else {
                                final int[] fCurrentStep3 = this.fCurrentStep;
                                final int n6 = i;
                                ++fCurrentStep3[n6];
                            }
                        }
                        if (this.fCurrentStep[i] == steps.length) {
                            if (sawDescendant) {
                                this.fCurrentStep[i] = descendantStep;
                                this.fMatched[i] = 5;
                            }
                            else {
                                this.fMatched[i] = 1;
                            }
                        }
                        else if (this.fCurrentStep[i] < steps.length && steps[this.fCurrentStep[i]].axis.type == 2) {
                            final int attrCount = attributes.getLength();
                            if (attrCount > 0) {
                                final XPath.NodeTest nodeTest = steps[this.fCurrentStep[i]].nodeTest;
                                int aIndex = 0;
                                while (aIndex < attrCount) {
                                    attributes.getName(aIndex, this.fQName);
                                    if (nodeTest.type != 1 || nodeTest.name.equals(this.fQName)) {
                                        final int[] fCurrentStep4 = this.fCurrentStep;
                                        final int n7 = i;
                                        ++fCurrentStep4[n7];
                                        if (this.fCurrentStep[i] == steps.length) {
                                            this.fMatched[i] = 3;
                                            int j;
                                            for (j = 0; j < i && (this.fMatched[j] & 0x1) != 0x1; ++j) {}
                                            if (j == i) {
                                                final AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(aIndex).getItem("ATTRIBUTE_PSVI");
                                                this.matched(this.fMatchedString = attrPSVI.getActualNormalizedValue(), attrPSVI.getActualNormalizedValueType(), attrPSVI.getItemValueTypes(), false);
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                    else {
                                        ++aIndex;
                                    }
                                }
                            }
                            if ((this.fMatched[i] & 0x1) != 0x1) {
                                if (this.fCurrentStep[i] > descendantStep) {
                                    this.fCurrentStep[i] = descendantStep;
                                }
                                else {
                                    final int[] fNoMatchDepth4 = this.fNoMatchDepth;
                                    final int n8 = i;
                                    ++fNoMatchDepth4[n8];
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void endElement(final QName element, final XSTypeDefinition type, final boolean nillable, final Object value, final short valueType, final ShortList itemValueType) {
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            this.fCurrentStep[i] = this.fStepIndexes[i].pop();
            if (this.fNoMatchDepth[i] > 0) {
                final int[] fNoMatchDepth = this.fNoMatchDepth;
                final int n = i;
                --fNoMatchDepth[n];
            }
            else {
                int j;
                for (j = 0; j < i && (this.fMatched[j] & 0x1) != 0x1; ++j) {}
                if (j >= i && this.fMatched[j] != 0) {
                    if ((this.fMatched[j] & 0x3) != 0x3) {
                        this.handleContent(type, nillable, value, valueType, itemValueType);
                        this.fMatched[i] = 0;
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer();
        String s = super.toString();
        final int index2 = s.lastIndexOf(46);
        if (index2 != -1) {
            s = s.substring(index2 + 1);
        }
        str.append(s);
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            str.append('[');
            final XPath.Step[] steps = this.fLocationPaths[i].steps;
            for (int j = 0; j < steps.length; ++j) {
                if (j == this.fCurrentStep[i]) {
                    str.append('^');
                }
                str.append(steps[j].toString());
                if (j < steps.length - 1) {
                    str.append('/');
                }
            }
            if (this.fCurrentStep[i] == steps.length) {
                str.append('^');
            }
            str.append(']');
            str.append(',');
        }
        return str.toString();
    }
    
    private String normalize(final String s) {
        final StringBuffer str = new StringBuffer();
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char c = s.charAt(i);
            switch (c) {
                case '\n': {
                    str.append("\\n");
                    break;
                }
                default: {
                    str.append(c);
                    break;
                }
            }
        }
        return str.toString();
    }
}
