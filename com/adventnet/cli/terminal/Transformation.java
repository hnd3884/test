package com.adventnet.cli.terminal;

import java.util.Vector;
import java.util.Hashtable;

class Transformation
{
    Hashtable transformationTable;
    byte[] unTransformed;
    byte[] totalUnTransformed;
    boolean transformation;
    Vector codeSequence;
    Vector trSequence;
    byte[] codeS;
    byte[] trS;
    byte[] returnBytes;
    int returnByteCount;
    int matchIndex;
    
    Transformation() {
        this.transformationTable = null;
        this.unTransformed = null;
        this.totalUnTransformed = null;
        this.transformation = false;
        this.codeSequence = null;
        this.trSequence = null;
        this.codeS = null;
        this.trS = null;
        this.returnBytes = new byte[1024];
        this.returnByteCount = 0;
        this.matchIndex = 0;
    }
    
    void setTransformationTable(final Hashtable transformationTable) {
        this.transformationTable = transformationTable;
    }
    
    byte[] transform(final byte[] totalUnTransformed) {
        this.returnByteCount = 0;
        if (this.unTransformed == null) {
            this.totalUnTransformed = totalUnTransformed;
        }
        else {
            this.copyArray(this.totalUnTransformed = new byte[this.unTransformed.length + totalUnTransformed.length], 0, this.unTransformed, 0, this.unTransformed.length);
            this.copyArray(this.totalUnTransformed, this.unTransformed.length, totalUnTransformed, 0, totalUnTransformed.length);
            this.unTransformed = null;
        }
        int i = 0;
        while (i < this.totalUnTransformed.length) {
            if (!this.transformation) {
                final Vector[] array = this.transformationTable.get(new Byte(this.totalUnTransformed[i]));
                if (array == null) {
                    try {
                        this.returnBytes[this.returnByteCount] = this.totalUnTransformed[i];
                    }
                    catch (final ArrayIndexOutOfBoundsException ex) {
                        this.increaseCapacity(this.returnBytes);
                        this.returnBytes[this.returnByteCount] = this.totalUnTransformed[i];
                    }
                    ++this.returnByteCount;
                    ++i;
                    continue;
                }
                this.codeSequence = array[0];
                this.trSequence = array[1];
                this.transformation = true;
            }
            if (this.totalUnTransformed.length - i >= ((byte[])this.codeSequence.lastElement()).length) {
                final int checkForMatch = this.checkForMatch(this.totalUnTransformed, i, this.codeSequence);
                if (checkForMatch < 0) {
                    this.transformation = false;
                    this.returnBytes[this.returnByteCount] = this.totalUnTransformed[i];
                    ++this.returnByteCount;
                    ++i;
                }
                else {
                    this.trS = (byte[])this.trSequence.elementAt(checkForMatch);
                    this.copyArray(this.returnBytes, this.returnByteCount, this.trS, 0, this.trS.length);
                    this.returnByteCount += this.trS.length;
                    this.codeS = (byte[])this.codeSequence.elementAt(checkForMatch);
                    i += this.codeS.length;
                    this.transformation = false;
                }
            }
            else {
                if (this.returnByteCount > 0) {
                    final byte[] array2 = new byte[this.returnByteCount];
                    this.copyArray(array2, 0, this.returnBytes, 0, this.returnByteCount);
                    if (i < this.totalUnTransformed.length) {
                        this.copyArray(this.unTransformed = new byte[this.totalUnTransformed.length - i], 0, this.totalUnTransformed, 0, this.unTransformed.length);
                    }
                    else {
                        this.unTransformed = null;
                    }
                    this.returnByteCount = 0;
                    return array2;
                }
                this.unTransformed = this.totalUnTransformed;
                return null;
            }
        }
        if (this.returnByteCount > 0) {
            final byte[] array3 = new byte[this.returnByteCount];
            this.copyArray(array3, 0, this.returnBytes, 0, this.returnByteCount);
            return array3;
        }
        return null;
    }
    
    int checkForMatch(final byte[] array, final int n, final Vector vector) {
        int n2 = -1;
        for (int i = 0; i < vector.size(); ++i) {
            final byte[] array2 = vector.elementAt(i);
            boolean b = false;
            for (int j = 0; j < array2.length; ++j) {
                if (array[n + j] != array2[j]) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                n2 = i;
            }
        }
        return n2;
    }
    
    private void copyArray(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) {
        for (int i = 0; i < n3; ++i) {
            array[n + i] = array2[n2 + i];
        }
    }
    
    private void increaseCapacity(final byte[] array) {
        this.copyArray(this.returnBytes = new byte[array.length * 2], 0, array, 0, array.length);
    }
}
