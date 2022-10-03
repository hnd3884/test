package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

public class MixedContentModel implements ContentModelValidator
{
    private int fCount;
    private QName[] fChildren;
    private int[] fChildrenType;
    private boolean fOrdered;
    
    public MixedContentModel(final QName[] children, final int[] type, final int offset, final int length, final boolean ordered) {
        this.fCount = length;
        this.fChildren = new QName[this.fCount];
        this.fChildrenType = new int[this.fCount];
        for (int i = 0; i < this.fCount; ++i) {
            this.fChildren[i] = new QName(children[offset + i]);
            this.fChildrenType[i] = type[offset + i];
        }
        this.fOrdered = ordered;
    }
    
    @Override
    public int validate(final QName[] children, final int offset, final int length) {
        if (this.fOrdered) {
            int inIndex = 0;
            for (int outIndex = 0; outIndex < length; ++outIndex) {
                final QName curChild = children[offset + outIndex];
                if (curChild.localpart != null) {
                    final int type = this.fChildrenType[inIndex];
                    if (type == 0) {
                        if (this.fChildren[inIndex].rawname != children[offset + outIndex].rawname) {
                            return outIndex;
                        }
                    }
                    else if (type == 6) {
                        final String uri = this.fChildren[inIndex].uri;
                        if (uri != null && uri != children[outIndex].uri) {
                            return outIndex;
                        }
                    }
                    else if (type == 8) {
                        if (children[outIndex].uri != null) {
                            return outIndex;
                        }
                    }
                    else if (type == 7 && this.fChildren[inIndex].uri == children[outIndex].uri) {
                        return outIndex;
                    }
                    ++inIndex;
                }
            }
        }
        else {
            for (int outIndex2 = 0; outIndex2 < length; ++outIndex2) {
                final QName curChild2 = children[offset + outIndex2];
                if (curChild2.localpart != null) {
                    int inIndex2;
                    for (inIndex2 = 0; inIndex2 < this.fCount; ++inIndex2) {
                        final int type = this.fChildrenType[inIndex2];
                        if (type == 0) {
                            if (curChild2.rawname == this.fChildren[inIndex2].rawname) {
                                break;
                            }
                        }
                        else if (type == 6) {
                            final String uri = this.fChildren[inIndex2].uri;
                            if (uri == null) {
                                break;
                            }
                            if (uri == children[outIndex2].uri) {
                                break;
                            }
                        }
                        else if (type == 8) {
                            if (children[outIndex2].uri == null) {
                                break;
                            }
                        }
                        else if (type == 7 && this.fChildren[inIndex2].uri != children[outIndex2].uri) {
                            break;
                        }
                    }
                    if (inIndex2 == this.fCount) {
                        return outIndex2;
                    }
                }
            }
        }
        return -1;
    }
}
