package org.apache.xerces.impl.dtd.models;

import org.apache.xerces.xni.QName;

public class MixedContentModel implements ContentModelValidator
{
    private final int fCount;
    private final QName[] fChildren;
    private final int[] fChildrenType;
    private final boolean fOrdered;
    
    public MixedContentModel(final QName[] array, final int[] array2, final int n, final int fCount, final boolean fOrdered) {
        this.fCount = fCount;
        this.fChildren = new QName[this.fCount];
        this.fChildrenType = new int[this.fCount];
        for (int i = 0; i < this.fCount; ++i) {
            this.fChildren[i] = new QName(array[n + i]);
            this.fChildrenType[i] = array2[n + i];
        }
        this.fOrdered = fOrdered;
    }
    
    public int validate(final QName[] array, final int n, final int n2) {
        if (this.fOrdered) {
            int n3 = 0;
            for (int i = 0; i < n2; ++i) {
                if (array[n + i].localpart != null) {
                    final int n4 = this.fChildrenType[n3];
                    if (n4 == 0) {
                        if (this.fChildren[n3].rawname != array[n + i].rawname) {
                            return i;
                        }
                    }
                    else if (n4 == 6) {
                        final String uri = this.fChildren[n3].uri;
                        if (uri != null && uri != array[i].uri) {
                            return i;
                        }
                    }
                    else if (n4 == 8) {
                        if (array[i].uri != null) {
                            return i;
                        }
                    }
                    else if (n4 == 7 && this.fChildren[n3].uri == array[i].uri) {
                        return i;
                    }
                    ++n3;
                }
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                final QName qName = array[n + j];
                if (qName.localpart != null) {
                    int k;
                    for (k = 0; k < this.fCount; ++k) {
                        final int n5 = this.fChildrenType[k];
                        if (n5 == 0) {
                            if (qName.rawname == this.fChildren[k].rawname) {
                                break;
                            }
                        }
                        else if (n5 == 6) {
                            final String uri2 = this.fChildren[k].uri;
                            if (uri2 == null) {
                                break;
                            }
                            if (uri2 == array[j].uri) {
                                break;
                            }
                        }
                        else if (n5 == 8) {
                            if (array[j].uri == null) {
                                break;
                            }
                        }
                        else if (n5 == 7 && this.fChildren[k].uri != array[j].uri) {
                            break;
                        }
                    }
                    if (k == this.fCount) {
                        return j;
                    }
                }
            }
        }
        return -1;
    }
}
