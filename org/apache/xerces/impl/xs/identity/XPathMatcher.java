package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.IntStack;
import org.apache.xerces.impl.xpath.XPath;

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
    private final XPath.LocationPath[] fLocationPaths;
    private final int[] fMatched;
    protected Object fMatchedString;
    protected String fXpathDefaultNamespace;
    private final IntStack[] fStepIndexes;
    private final int[] fCurrentStep;
    private final int[] fNoMatchDepth;
    final QName fQName;
    
    public XPathMatcher(final XPath xPath) {
        this.fQName = new QName();
        this.fLocationPaths = xPath.getLocationPaths();
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
    
    protected void handleContent(final XSTypeDefinition xsTypeDefinition, final boolean b, final Object o, final short n, final ShortList list) {
    }
    
    protected void matched(final Object o, final short n, final ShortList list, final boolean b) {
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
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes) {
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            final int n = this.fCurrentStep[i];
            this.fStepIndexes[i].push(n);
            if ((this.fMatched[i] & 0x5) == 0x1 || this.fNoMatchDepth[i] > 0) {
                final int[] fNoMatchDepth = this.fNoMatchDepth;
                final int n2 = i;
                ++fNoMatchDepth[n2];
            }
            else {
                if ((this.fMatched[i] & 0x5) == 0x5) {
                    this.fMatched[i] = 13;
                }
                final XPath.Step[] steps = this.fLocationPaths[i].steps;
                while (this.fCurrentStep[i] < steps.length && steps[this.fCurrentStep[i]].axis.type == 3) {
                    final int[] fCurrentStep = this.fCurrentStep;
                    final int n3 = i;
                    ++fCurrentStep[n3];
                }
                if (this.fCurrentStep[i] == steps.length) {
                    this.fMatched[i] = 1;
                }
                else {
                    final int n4 = this.fCurrentStep[i];
                    while (this.fCurrentStep[i] < steps.length && steps[this.fCurrentStep[i]].axis.type == 4) {
                        final int[] fCurrentStep2 = this.fCurrentStep;
                        final int n5 = i;
                        ++fCurrentStep2[n5];
                    }
                    final boolean b = this.fCurrentStep[i] > n4;
                    if (this.fCurrentStep[i] == steps.length) {
                        final int[] fNoMatchDepth2 = this.fNoMatchDepth;
                        final int n6 = i;
                        ++fNoMatchDepth2[n6];
                    }
                    else {
                        if ((this.fCurrentStep[i] == n || this.fCurrentStep[i] > n4) && steps[this.fCurrentStep[i]].axis.type == 1) {
                            final XPath.NodeTest nodeTest = steps[this.fCurrentStep[i]].nodeTest;
                            if (nodeTest.name.uri == null) {
                                nodeTest.name.uri = this.fXpathDefaultNamespace;
                            }
                            if (!matches(nodeTest, qName)) {
                                if (this.fCurrentStep[i] > n4) {
                                    this.fCurrentStep[i] = n4;
                                    continue;
                                }
                                final int[] fNoMatchDepth3 = this.fNoMatchDepth;
                                final int n7 = i;
                                ++fNoMatchDepth3[n7];
                                continue;
                            }
                            else {
                                final int[] fCurrentStep3 = this.fCurrentStep;
                                final int n8 = i;
                                ++fCurrentStep3[n8];
                            }
                        }
                        if (this.fCurrentStep[i] == steps.length) {
                            if (b) {
                                this.fCurrentStep[i] = n4;
                                this.fMatched[i] = 5;
                            }
                            else {
                                this.fMatched[i] = 1;
                            }
                        }
                        else if (this.fCurrentStep[i] < steps.length && steps[this.fCurrentStep[i]].axis.type == 2) {
                            final int length = xmlAttributes.getLength();
                            if (length > 0) {
                                final XPath.NodeTest nodeTest2 = steps[this.fCurrentStep[i]].nodeTest;
                                if (nodeTest2.name.uri == null) {
                                    nodeTest2.name.uri = this.fXpathDefaultNamespace;
                                }
                                int j = 0;
                                while (j < length) {
                                    xmlAttributes.getName(j, this.fQName);
                                    if (matches(nodeTest2, this.fQName)) {
                                        final int[] fCurrentStep4 = this.fCurrentStep;
                                        final int n9 = i;
                                        ++fCurrentStep4[n9];
                                        if (this.fCurrentStep[i] != steps.length) {
                                            break;
                                        }
                                        this.fMatched[i] = 3;
                                        int n10;
                                        for (n10 = 0; n10 < i && (this.fMatched[n10] & 0x1) != 0x1; ++n10) {}
                                        if (n10 == i) {
                                            final AttributePSVI attributePSVI = (AttributePSVI)xmlAttributes.getAugmentations(j).getItem("ATTRIBUTE_PSVI");
                                            this.matched(this.fMatchedString = attributePSVI.getActualNormalizedValue(), attributePSVI.getActualNormalizedValueType(), attributePSVI.getItemValueTypes(), false);
                                            break;
                                        }
                                        break;
                                    }
                                    else {
                                        ++j;
                                    }
                                }
                            }
                            if ((this.fMatched[i] & 0x1) != 0x1) {
                                if (this.fCurrentStep[i] > n4) {
                                    this.fCurrentStep[i] = n4;
                                }
                                else {
                                    final int[] fNoMatchDepth4 = this.fNoMatchDepth;
                                    final int n11 = i;
                                    ++fNoMatchDepth4[n11];
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void endElement(final QName qName, final XSTypeDefinition xsTypeDefinition, final boolean b, final Object o, final short n, final ShortList list) {
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            this.fCurrentStep[i] = this.fStepIndexes[i].pop();
            if (this.fNoMatchDepth[i] > 0) {
                final int[] fNoMatchDepth = this.fNoMatchDepth;
                final int n2 = i;
                --fNoMatchDepth[n2];
            }
            else {
                int n3;
                for (n3 = 0; n3 < i && (this.fMatched[n3] & 0x1) != 0x1; ++n3) {}
                if (n3 >= i) {
                    if (this.fMatched[n3] != 0) {
                        if ((this.fMatched[n3] & 0x3) == 0x3) {
                            this.fMatched[i] = 0;
                        }
                        else {
                            this.handleContent(xsTypeDefinition, b, o, n, list);
                            this.fMatched[i] = 0;
                        }
                    }
                }
            }
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        String s = super.toString();
        final int lastIndex = s.lastIndexOf(46);
        if (lastIndex != -1) {
            s = s.substring(lastIndex + 1);
        }
        sb.append(s);
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            sb.append('[');
            final XPath.Step[] steps = this.fLocationPaths[i].steps;
            for (int j = 0; j < steps.length; ++j) {
                if (j == this.fCurrentStep[i]) {
                    sb.append('^');
                }
                sb.append(steps[j].toString());
                if (j < steps.length - 1) {
                    sb.append('/');
                }
            }
            if (this.fCurrentStep[i] == steps.length) {
                sb.append('^');
            }
            sb.append(']');
            sb.append(',');
        }
        return sb.toString();
    }
    
    public void setXPathDefaultNamespace(final String fXpathDefaultNamespace) {
        this.fXpathDefaultNamespace = fXpathDefaultNamespace;
    }
    
    private String normalize(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                case 10: {
                    sb.append("\\n");
                    break;
                }
                default: {
                    sb.append(char1);
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    private static boolean matches(final XPath.NodeTest nodeTest, final QName qName) {
        if (nodeTest.type == 1) {
            return nodeTest.name.equals(qName);
        }
        return nodeTest.type != 4 || nodeTest.name.uri == qName.uri;
    }
}
