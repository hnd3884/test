package java.lang.invoke;

import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;

final class LambdaFormBuffer
{
    private int arity;
    private int length;
    private LambdaForm.Name[] names;
    private LambdaForm.Name[] originalNames;
    private byte flags;
    private int firstChange;
    private LambdaForm.Name resultName;
    private String debugName;
    private ArrayList<LambdaForm.Name> dups;
    private static final int F_TRANS = 16;
    private static final int F_OWNED = 3;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    LambdaFormBuffer(final LambdaForm lambdaForm) {
        this.arity = lambdaForm.arity;
        this.setNames(lambdaForm.names);
        int result = lambdaForm.result;
        if (result == -2) {
            result = this.length - 1;
        }
        if (result >= 0 && lambdaForm.names[result].type != LambdaForm.BasicType.V_TYPE) {
            this.resultName = lambdaForm.names[result];
        }
        this.debugName = lambdaForm.debugName;
        assert lambdaForm.nameRefsAreLegal();
    }
    
    private LambdaForm lambdaForm() {
        assert !this.inTrans();
        return new LambdaForm(this.debugName, this.arity, this.nameArray(), this.resultIndex());
    }
    
    LambdaForm.Name name(final int n) {
        assert n < this.length;
        return this.names[n];
    }
    
    LambdaForm.Name[] nameArray() {
        return Arrays.copyOf(this.names, this.length);
    }
    
    int resultIndex() {
        if (this.resultName == null) {
            return -1;
        }
        final int index = indexOf(this.resultName, this.names);
        assert index >= 0;
        return index;
    }
    
    void setNames(final LambdaForm.Name[] array) {
        this.originalNames = array;
        this.names = array;
        this.length = array.length;
        this.flags = 0;
    }
    
    private boolean verifyArity() {
        for (int n = 0; n < this.arity && n < this.firstChange; ++n) {
            assert this.names[n].isParam() : "#" + n + "=" + this.names[n];
        }
        for (int i = this.arity; i < this.length; ++i) {
            assert !this.names[i].isParam() : "#" + i + "=" + this.names[i];
        }
        for (int j = this.length; j < this.names.length; ++j) {
            assert this.names[j] == null : "#" + j + "=" + this.names[j];
        }
        if (this.resultName != null) {
            final int index = indexOf(this.resultName, this.names);
            assert index >= 0 : "not found: " + this.resultName.exprString() + Arrays.asList(this.names);
            assert this.names[index] == this.resultName;
        }
        return true;
    }
    
    private boolean verifyFirstChange() {
        assert this.inTrans();
        int i = 0;
        while (i < this.length) {
            if (this.names[i] != this.originalNames[i]) {
                assert this.firstChange == i : Arrays.asList(this.firstChange, i, this.originalNames[i].exprString(), Arrays.asList(this.names));
                return true;
            }
            else {
                ++i;
            }
        }
        assert this.firstChange == this.length : Arrays.asList(this.firstChange, Arrays.asList(this.names));
        return true;
    }
    
    private static int indexOf(final LambdaForm.NamedFunction namedFunction, final LambdaForm.NamedFunction[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == namedFunction) {
                return i;
            }
        }
        return -1;
    }
    
    private static int indexOf(final LambdaForm.Name name, final LambdaForm.Name[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == name) {
                return i;
            }
        }
        return -1;
    }
    
    boolean inTrans() {
        return (this.flags & 0x10) != 0x0;
    }
    
    int ownedCount() {
        return this.flags & 0x3;
    }
    
    void growNames(final int n, final int n2) {
        final int length = this.length;
        final int length2 = length + n2;
        int ownedCount = this.ownedCount();
        if (ownedCount == 0 || length2 > this.names.length) {
            this.names = Arrays.copyOf(this.names, (this.names.length + n2) * 5 / 4);
            if (ownedCount == 0) {
                ++this.flags;
                ++ownedCount;
                assert this.ownedCount() == ownedCount;
            }
        }
        if (this.originalNames != null && this.originalNames.length < this.names.length) {
            this.originalNames = Arrays.copyOf(this.originalNames, this.names.length);
            if (ownedCount == 1) {
                ++this.flags;
                ++ownedCount;
                assert this.ownedCount() == ownedCount;
            }
        }
        if (n2 == 0) {
            return;
        }
        final int n3 = n + n2;
        final int n4 = length - n;
        System.arraycopy(this.names, n, this.names, n3, n4);
        Arrays.fill(this.names, n, n3, null);
        if (this.originalNames != null) {
            System.arraycopy(this.originalNames, n, this.originalNames, n3, n4);
            Arrays.fill(this.originalNames, n, n3, null);
        }
        this.length = length2;
        if (this.firstChange >= n) {
            this.firstChange += n2;
        }
    }
    
    int lastIndexOf(final LambdaForm.Name name) {
        int n = -1;
        for (int i = 0; i < this.length; ++i) {
            if (this.names[i] == name) {
                n = i;
            }
        }
        return n;
    }
    
    private void noteDuplicate(final int n, final int n2) {
        final LambdaForm.Name name = this.names[n];
        assert name == this.names[n2];
        assert this.originalNames[n] != null;
        assert this.originalNames[n2] == name;
        if (this.dups == null) {
            this.dups = new ArrayList<LambdaForm.Name>();
        }
        this.dups.add(name);
    }
    
    private void clearDuplicatesAndNulls() {
        if (this.dups != null) {
            assert this.ownedCount() >= 1;
            for (final LambdaForm.Name name : this.dups) {
                int i = this.firstChange;
                while (i < this.length) {
                    if (this.names[i] == name && this.originalNames[i] != name) {
                        this.names[i] = null;
                        assert Arrays.asList(this.names).contains(name);
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
            this.dups.clear();
        }
        final int length = this.length;
        for (int j = this.firstChange; j < this.length; ++j) {
            if (this.names[j] == null) {
                System.arraycopy(this.names, j + 1, this.names, j, --this.length - j);
                --j;
            }
        }
        if (this.length < length) {
            Arrays.fill(this.names, this.length, length, null);
        }
        assert !Arrays.asList(this.names).subList(0, this.length).contains(null);
    }
    
    void startEdit() {
        assert this.verifyArity();
        final int ownedCount = this.ownedCount();
        assert !this.inTrans();
        this.flags |= 0x10;
        final LambdaForm.Name[] names = this.names;
        final LambdaForm.Name[] array = (LambdaForm.Name[])((ownedCount == 2) ? this.originalNames : null);
        assert array != names;
        if (array != null && array.length >= this.length) {
            this.names = this.copyNamesInto(array);
        }
        else {
            this.names = Arrays.copyOf(names, Math.max(this.length + 2, names.length));
            if (ownedCount < 2) {
                ++this.flags;
            }
            assert this.ownedCount() == ownedCount + 1;
        }
        this.originalNames = names;
        assert this.originalNames != this.names;
        this.firstChange = this.length;
        assert this.inTrans();
    }
    
    private void changeName(final int firstChange, final LambdaForm.Name resultName) {
        assert this.inTrans();
        assert firstChange < this.length;
        final LambdaForm.Name name = this.names[firstChange];
        assert name == this.originalNames[firstChange];
        assert this.verifyFirstChange();
        if (this.ownedCount() == 0) {
            this.growNames(0, 0);
        }
        this.names[firstChange] = resultName;
        if (this.firstChange > firstChange) {
            this.firstChange = firstChange;
        }
        if (this.resultName != null && this.resultName == name) {
            this.resultName = resultName;
        }
    }
    
    void setResult(final LambdaForm.Name resultName) {
        assert this.lastIndexOf(resultName) >= 0;
        this.resultName = resultName;
    }
    
    LambdaForm endEdit() {
        assert this.verifyFirstChange();
        for (int i = Math.max(this.firstChange, this.arity); i < this.length; ++i) {
            final LambdaForm.Name name = this.names[i];
            if (name != null) {
                final LambdaForm.Name replaceNames = name.replaceNames(this.originalNames, this.names, this.firstChange, i);
                if (replaceNames != name) {
                    this.names[i] = replaceNames;
                    if (this.resultName == name) {
                        this.resultName = replaceNames;
                    }
                }
            }
        }
        assert this.inTrans();
        this.flags &= 0xFFFFFFEF;
        this.clearDuplicatesAndNulls();
        this.originalNames = null;
        if (this.firstChange < this.arity) {
            final LambdaForm.Name[] array = new LambdaForm.Name[this.arity - this.firstChange];
            int firstChange = this.firstChange;
            int n = 0;
            for (int j = this.firstChange; j < this.arity; ++j) {
                final LambdaForm.Name name2 = this.names[j];
                if (name2.isParam()) {
                    this.names[firstChange++] = name2;
                }
                else {
                    array[n++] = name2;
                }
            }
            assert n == this.arity - firstChange;
            System.arraycopy(array, 0, this.names, firstChange, n);
            this.arity -= n;
        }
        assert this.verifyArity();
        return this.lambdaForm();
    }
    
    private LambdaForm.Name[] copyNamesInto(final LambdaForm.Name[] array) {
        System.arraycopy(this.names, 0, array, 0, this.length);
        Arrays.fill(array, this.length, array.length, null);
        return array;
    }
    
    LambdaFormBuffer replaceFunctions(final LambdaForm.NamedFunction[] array, final LambdaForm.NamedFunction[] array2, final Object... array3) {
        assert this.inTrans();
        if (array.length == 0) {
            return this;
        }
        for (int i = this.arity; i < this.length; ++i) {
            final LambdaForm.Name name = this.names[i];
            final int index = indexOf(name.function, array);
            if (index >= 0 && Arrays.equals(name.arguments, array3)) {
                this.changeName(i, new LambdaForm.Name(array2[index], name.arguments));
            }
        }
        return this;
    }
    
    private void replaceName(final int n, final LambdaForm.Name name) {
        assert this.inTrans();
        assert this.verifyArity();
        assert n < this.arity;
        final LambdaForm.Name name2 = this.names[n];
        assert name2.isParam();
        assert name2.type == name.type;
        this.changeName(n, name);
    }
    
    LambdaFormBuffer renameParameter(final int n, final LambdaForm.Name name) {
        assert name.isParam();
        this.replaceName(n, name);
        return this;
    }
    
    LambdaFormBuffer replaceParameterByNewExpression(final int n, final LambdaForm.Name name) {
        assert !name.isParam();
        assert this.lastIndexOf(name) < 0;
        this.replaceName(n, name);
        return this;
    }
    
    LambdaFormBuffer replaceParameterByCopy(final int n, final int n2) {
        assert n != n2;
        this.replaceName(n, this.names[n2]);
        this.noteDuplicate(n, n2);
        return this;
    }
    
    private void insertName(final int n, final LambdaForm.Name name, final boolean b) {
        assert this.inTrans();
        assert this.verifyArity();
        Label_0079: {
            if (!LambdaFormBuffer.$assertionsDisabled) {
                if (b) {
                    if (n <= this.arity) {
                        break Label_0079;
                    }
                }
                else if (n >= this.arity) {
                    break Label_0079;
                }
                throw new AssertionError();
            }
        }
        this.growNames(n, 1);
        if (b) {
            ++this.arity;
        }
        this.changeName(n, name);
    }
    
    LambdaFormBuffer insertExpression(final int n, final LambdaForm.Name name) {
        assert !name.isParam();
        this.insertName(n, name, false);
        return this;
    }
    
    LambdaFormBuffer insertParameter(final int n, final LambdaForm.Name name) {
        assert name.isParam();
        this.insertName(n, name, true);
        return this;
    }
}
