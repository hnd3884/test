package org.apache.xerces.impl.dtd.models;

import org.apache.xerces.xni.QName;

public class SimpleContentModel implements ContentModelValidator
{
    public static final short CHOICE = -1;
    public static final short SEQUENCE = -1;
    private final QName fFirstChild;
    private final QName fSecondChild;
    private final int fOperator;
    
    public SimpleContentModel(final short fOperator, final QName values, final QName values2) {
        this.fFirstChild = new QName();
        this.fSecondChild = new QName();
        this.fFirstChild.setValues(values);
        if (values2 != null) {
            this.fSecondChild.setValues(values2);
        }
        else {
            this.fSecondChild.clear();
        }
        this.fOperator = fOperator;
    }
    
    public int validate(final QName[] array, final int n, final int n2) {
        switch (this.fOperator) {
            case 0: {
                if (n2 == 0) {
                    return 0;
                }
                if (array[n].rawname != this.fFirstChild.rawname) {
                    return 0;
                }
                if (n2 > 1) {
                    return 1;
                }
                break;
            }
            case 1: {
                if (n2 == 1 && array[n].rawname != this.fFirstChild.rawname) {
                    return 0;
                }
                if (n2 > 1) {
                    return 1;
                }
                break;
            }
            case 2: {
                if (n2 > 0) {
                    for (int i = 0; i < n2; ++i) {
                        if (array[n + i].rawname != this.fFirstChild.rawname) {
                            return i;
                        }
                    }
                    break;
                }
                break;
            }
            case 3: {
                if (n2 == 0) {
                    return 0;
                }
                for (int j = 0; j < n2; ++j) {
                    if (array[n + j].rawname != this.fFirstChild.rawname) {
                        return j;
                    }
                }
                break;
            }
            case 4: {
                if (n2 == 0) {
                    return 0;
                }
                if (array[n].rawname != this.fFirstChild.rawname && array[n].rawname != this.fSecondChild.rawname) {
                    return 0;
                }
                if (n2 > 1) {
                    return 1;
                }
                break;
            }
            case 5: {
                if (n2 == 2) {
                    if (array[n].rawname != this.fFirstChild.rawname) {
                        return 0;
                    }
                    if (array[n + 1].rawname != this.fSecondChild.rawname) {
                        return 1;
                    }
                    break;
                }
                else {
                    if (n2 > 2) {
                        return 2;
                    }
                    return n2;
                }
                break;
            }
            default: {
                throw new RuntimeException("ImplementationMessages.VAL_CST");
            }
        }
        return -1;
    }
}
