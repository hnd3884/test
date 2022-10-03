package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class VerificationTypeInfo
{
    public static final int ITEM_TOP = 0;
    public static final int ITEM_INTEGER = 1;
    public static final int ITEM_FLOAT = 2;
    public static final int ITEM_DOUBLE = 3;
    public static final int ITEM_LONG = 4;
    public static final int ITEM_NULL = 5;
    public static final int ITEM_UNINITIALIZED_THIS = 6;
    public static final int ITEM_OBJECT = 7;
    public static final int ITEM_UNINITIALIZED = 8;
    public int tag;
    private int id;
    private char[] constantPoolName;
    public int offset;
    
    private VerificationTypeInfo() {
    }
    
    public VerificationTypeInfo(final int id, final char[] constantPoolName) {
        this(id, 7, constantPoolName);
    }
    
    public VerificationTypeInfo(final int id, final int tag, final char[] constantPoolName) {
        this.id = id;
        this.tag = tag;
        this.constantPoolName = constantPoolName;
    }
    
    public VerificationTypeInfo(final int tag, final TypeBinding binding) {
        this(binding);
        this.tag = tag;
    }
    
    public VerificationTypeInfo(final TypeBinding binding) {
        this.id = binding.id;
        switch (binding.id) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 10: {
                this.tag = 1;
                break;
            }
            case 9: {
                this.tag = 2;
                break;
            }
            case 7: {
                this.tag = 4;
                break;
            }
            case 8: {
                this.tag = 3;
                break;
            }
            case 12: {
                this.tag = 5;
                break;
            }
            default: {
                this.tag = 7;
                this.constantPoolName = binding.constantPoolName();
                break;
            }
        }
    }
    
    public void setBinding(final TypeBinding binding) {
        this.constantPoolName = binding.constantPoolName();
        final int typeBindingId = binding.id;
        switch (this.id = typeBindingId) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 10: {
                this.tag = 1;
                break;
            }
            case 9: {
                this.tag = 2;
                break;
            }
            case 7: {
                this.tag = 4;
                break;
            }
            case 8: {
                this.tag = 3;
                break;
            }
            case 12: {
                this.tag = 5;
                break;
            }
            default: {
                this.tag = 7;
                break;
            }
        }
    }
    
    public int id() {
        return this.id;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        switch (this.tag) {
            case 6: {
                buffer.append("uninitialized_this(").append(this.readableName()).append(")");
                break;
            }
            case 8: {
                buffer.append("uninitialized(").append(this.readableName()).append(")");
                break;
            }
            case 7: {
                buffer.append(this.readableName());
                break;
            }
            case 3: {
                buffer.append('D');
                break;
            }
            case 2: {
                buffer.append('F');
                break;
            }
            case 1: {
                buffer.append('I');
                break;
            }
            case 4: {
                buffer.append('J');
                break;
            }
            case 5: {
                buffer.append("null");
                break;
            }
            case 0: {
                buffer.append("top");
                break;
            }
        }
        return String.valueOf(buffer);
    }
    
    public VerificationTypeInfo duplicate() {
        final VerificationTypeInfo verificationTypeInfo = new VerificationTypeInfo();
        verificationTypeInfo.id = this.id;
        verificationTypeInfo.tag = this.tag;
        verificationTypeInfo.constantPoolName = this.constantPoolName;
        verificationTypeInfo.offset = this.offset;
        return verificationTypeInfo;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof VerificationTypeInfo) {
            final VerificationTypeInfo info1 = (VerificationTypeInfo)obj;
            return info1.tag == this.tag && CharOperation.equals(info1.constantPoolName(), this.constantPoolName());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.tag + this.id + this.constantPoolName.length + this.offset;
    }
    
    public char[] constantPoolName() {
        return this.constantPoolName;
    }
    
    public char[] readableName() {
        return this.constantPoolName;
    }
    
    public void replaceWithElementType() {
        if (this.constantPoolName[1] == 'L') {
            this.constantPoolName = CharOperation.subarray(this.constantPoolName, 2, this.constantPoolName.length - 1);
        }
        else {
            this.constantPoolName = CharOperation.subarray(this.constantPoolName, 1, this.constantPoolName.length);
            if (this.constantPoolName.length == 1) {
                switch (this.constantPoolName[0]) {
                    case 'I': {
                        this.id = 10;
                        break;
                    }
                    case 'B': {
                        this.id = 3;
                        break;
                    }
                    case 'S': {
                        this.id = 4;
                        break;
                    }
                    case 'C': {
                        this.id = 2;
                        break;
                    }
                    case 'J': {
                        this.id = 7;
                        break;
                    }
                    case 'F': {
                        this.id = 9;
                        break;
                    }
                    case 'D': {
                        this.id = 8;
                        break;
                    }
                    case 'Z': {
                        this.id = 5;
                        break;
                    }
                    case 'N': {
                        this.id = 12;
                        break;
                    }
                    case 'V': {
                        this.id = 6;
                        break;
                    }
                }
            }
        }
    }
}
