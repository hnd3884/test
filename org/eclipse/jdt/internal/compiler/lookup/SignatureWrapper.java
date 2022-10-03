package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

public class SignatureWrapper
{
    public char[] signature;
    public int start;
    public int end;
    public int bracket;
    private boolean use15specifics;
    private boolean useExternalAnnotations;
    
    public SignatureWrapper(final char[] signature, final boolean use15specifics) {
        this.signature = signature;
        this.start = 0;
        final int n = -1;
        this.bracket = n;
        this.end = n;
        if (!(this.use15specifics = use15specifics)) {
            this.removeTypeArguments();
        }
    }
    
    public SignatureWrapper(final char[] signature, final boolean use15specifics, final boolean useExternalAnnotations) {
        this.signature = signature;
        this.start = 0;
        final int n = -1;
        this.bracket = n;
        this.end = n;
        this.use15specifics = use15specifics;
        this.useExternalAnnotations = useExternalAnnotations;
        if (!use15specifics) {
            this.removeTypeArguments();
        }
    }
    
    public SignatureWrapper(final char[] signature) {
        this(signature, true);
    }
    
    public boolean atEnd() {
        return this.start < 0 || this.start >= this.signature.length;
    }
    
    public boolean isParameterized() {
        return this.bracket == this.end;
    }
    
    public int computeEnd() {
        int index = this.start;
        Label_0094: {
            if (this.useExternalAnnotations) {
                while (true) {
                    switch (this.signature[index]) {
                        case '0':
                        case '1':
                        case '@': {
                            if (index == this.start) {
                                break Label_0094;
                            }
                        }
                        case '[': {
                            ++index;
                            continue;
                        }
                        default: {
                            break Label_0094;
                        }
                    }
                }
            }
            else {
                while (this.signature[index] == '[') {
                    ++index;
                }
            }
        }
        switch (this.signature[index]) {
            case 'L':
            case 'T': {
                this.end = CharOperation.indexOf(';', this.signature, this.start);
                if (this.bracket <= this.start) {
                    this.bracket = CharOperation.indexOf('<', this.signature, this.start);
                }
                if (this.bracket > this.start && this.bracket < this.end) {
                    this.end = this.bracket;
                    break;
                }
                if (this.end == -1) {
                    this.end = this.signature.length + 1;
                    break;
                }
                break;
            }
            default: {
                this.end = index;
                break;
            }
        }
        if (this.use15specifics || this.end != this.bracket) {
            this.start = this.end + 1;
        }
        else {
            this.start = this.skipAngleContents(this.end) + 1;
            this.bracket = -1;
        }
        return this.end;
    }
    
    private void removeTypeArguments() {
        final StringBuilder buffer = new StringBuilder();
        int offset = 0;
        int index = this.start;
        if (this.signature[0] == '<') {
            ++index;
        }
        while (index < this.signature.length) {
            if (this.signature[index] == '<') {
                buffer.append(this.signature, offset, index - offset);
                offset = (index = this.skipAngleContents(index));
            }
            ++index;
        }
        buffer.append(this.signature, offset, index - offset);
        this.signature = new char[buffer.length()];
        buffer.getChars(0, this.signature.length, this.signature, 0);
    }
    
    public int skipAngleContents(int i) {
        if (this.signature[i] != '<') {
            return i;
        }
        int depth = 0;
        final int length = this.signature.length;
        ++i;
        while (i < length) {
            switch (this.signature[i]) {
                case '<': {
                    ++depth;
                    break;
                }
                case '>': {
                    if (--depth < 0) {
                        return i + 1;
                    }
                    break;
                }
            }
            ++i;
        }
        return i;
    }
    
    public char[] nextWord() {
        this.end = CharOperation.indexOf(';', this.signature, this.start);
        if (this.bracket <= this.start) {
            this.bracket = CharOperation.indexOf('<', this.signature, this.start);
        }
        final int dot = CharOperation.indexOf('.', this.signature, this.start);
        if (this.bracket > this.start && this.bracket < this.end) {
            this.end = this.bracket;
        }
        if (dot > this.start && dot < this.end) {
            this.end = dot;
        }
        final char[] signature = this.signature;
        final int start = this.start;
        final int end = this.end;
        this.start = end;
        return CharOperation.subarray(signature, start, end);
    }
    
    public char[] nextName() {
        this.end = CharOperation.indexOf(';', this.signature, this.start);
        if (this.bracket <= this.start) {
            this.bracket = CharOperation.indexOf('<', this.signature, this.start);
        }
        if (this.bracket > this.start && this.bracket < this.end) {
            this.end = this.bracket;
        }
        final char[] signature = this.signature;
        final int start = this.start;
        final int end = this.end;
        this.start = end;
        return CharOperation.subarray(signature, start, end);
    }
    
    public char[] peekFullType() {
        final int s = this.start;
        final int b = this.bracket;
        final int e = this.end;
        final int peekEnd = this.skipAngleContents(this.computeEnd());
        this.start = s;
        this.bracket = b;
        this.end = e;
        return CharOperation.subarray(this.signature, s, peekEnd + 1);
    }
    
    public char[] getFrom(final int s) {
        if (this.end == this.bracket) {
            this.end = this.skipAngleContents(this.bracket);
            this.start = this.end + 1;
        }
        return CharOperation.subarray(this.signature, s, this.end + 1);
    }
    
    public char[] tail() {
        return CharOperation.subarray(this.signature, this.start, this.signature.length);
    }
    
    @Override
    public String toString() {
        return String.valueOf(new String(this.signature)) + " @ " + this.start;
    }
}
