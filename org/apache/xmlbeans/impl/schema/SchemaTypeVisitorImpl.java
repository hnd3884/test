package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaLocalElement;
import java.util.Arrays;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.impl.values.TypeStoreVisitor;

public class SchemaTypeVisitorImpl implements TypeStoreVisitor
{
    static final boolean PROBE_VALIDITY = true;
    static final boolean CHECK_VALIDITY = false;
    private VisitorState[] _stack;
    private VisitorState[] _rollback;
    int _stackSize;
    int _rollbackSize;
    private boolean _isValid;
    private SchemaParticle _matchedParticle;
    private VisitorState _top;
    private int _rollbackIndex;
    
    public SchemaTypeVisitorImpl(final SchemaParticle part) {
        this.init(part);
    }
    
    public SchemaTypeVisitorImpl() {
    }
    
    public void init(final SchemaParticle part) {
        if (this._stack == null) {
            this._stack = this.expand(null);
        }
        if (this._rollback == null) {
            this._rollback = this.expand(null);
        }
        this._stackSize = 0;
        this._rollbackSize = 0;
        if (part != null) {
            this.push(part);
            this._rollbackIndex = 1;
        }
    }
    
    public VisitorState[] expand(final VisitorState[] orig) {
        final int newsize = (orig == null) ? 4 : (orig.length * 2);
        final VisitorState[] result = new VisitorState[newsize];
        if (orig != null) {
            System.arraycopy(orig, 0, result, 0, orig.length);
        }
        for (int i = (orig == null) ? 0 : orig.length; i < newsize; ++i) {
            result[i] = new VisitorState();
        }
        return result;
    }
    
    VisitorState topRef() {
        return this._stack[this._stackSize - 1];
    }
    
    void saveCopy(final VisitorState ref) {
        if (this._rollback.length == this._rollbackSize) {
            this._rollback = this.expand(this._rollback);
        }
        this._rollback[this._rollbackSize].copy(ref);
        ++this._rollbackSize;
    }
    
    void addParticle(final SchemaParticle part) {
        if (this._stack.length == this._stackSize) {
            this._stack = this.expand(this._stack);
        }
        this._stack[this._stackSize].init(part);
        ++this._stackSize;
    }
    
    boolean prepare() {
        if (this._rollbackIndex == 0) {
            this._top = null;
            return false;
        }
        this.saveCopy(this._top = this.topRef());
        this._rollbackIndex = this._stackSize - 1;
        return true;
    }
    
    void push(final SchemaParticle part) {
        this.addParticle(part);
        this._top = this.topRef();
    }
    
    boolean pop() {
        --this._stackSize;
        if (this._stackSize <= this._rollbackIndex) {
            return this.prepare();
        }
        this._top = this.topRef();
        return true;
    }
    
    void commit() {
        this._top = null;
        this._rollbackIndex = this._stackSize;
        this._rollbackSize = 0;
    }
    
    void rollback() {
        while (this._rollbackSize > 0) {
            --this._rollbackSize;
            final VisitorState temp = this._stack[this._rollbackIndex];
            this._stack[this._rollbackIndex] = this._rollback[this._rollbackSize];
            this._rollback[this._rollbackSize] = temp;
            ++this._rollbackIndex;
        }
        this._stackSize = this._rollbackIndex;
        this._top = null;
    }
    
    boolean notValid() {
        this._isValid = false;
        this._matchedParticle = null;
        this.rollback();
        return false;
    }
    
    boolean ok(final SchemaParticle part, final boolean testValidity) {
        if (!testValidity) {
            this._matchedParticle = part;
            this.commit();
        }
        else {
            this.rollback();
        }
        return true;
    }
    
    @Override
    public boolean visit(final QName eltName) {
        return this.visit(eltName, false);
    }
    
    public boolean visit(final QName eltName, final boolean testValidity) {
        if (!this.prepare()) {
            return this.notValid();
        }
        int lastAtProcessedChildCount = -2;
        int lastAtStackSize = -2;
    Label_0743:
        while (true) {
            if (this._top._curCount > this._top._curMin && lastAtProcessedChildCount == this._top._processedChildCount && lastAtStackSize == this._stackSize) {
                this._top._curCount = this._top._curMax;
            }
            lastAtProcessedChildCount = this._top._processedChildCount;
            lastAtStackSize = this._stackSize;
            while (this._top._curCount >= this._top._curMax) {
                if (!this.pop()) {
                    break Label_0743;
                }
            }
            Label_0733: {
                switch (this._top._curPart.getParticleType()) {
                    default: {
                        assert false;
                    }
                    case 5: {
                        if (this._top._curPart.canStartWithElement(eltName)) {
                            final VisitorState top = this._top;
                            ++top._curCount;
                            return this.ok(this._top._curPart, testValidity);
                        }
                        if (this._top._curCount < this._top._curMin) {
                            return this.notValid();
                        }
                        break;
                    }
                    case 4: {
                        if (this._top._curPart.canStartWithElement(eltName)) {
                            final VisitorState top2 = this._top;
                            ++top2._curCount;
                            return this.ok(this._top._curPart, testValidity);
                        }
                        if (this._top._curCount < this._top._curMin) {
                            return this.notValid();
                        }
                        break;
                    }
                    case 3: {
                        int i = this._top._processedChildCount;
                        while (i < this._top._childCount) {
                            final SchemaParticle candidate = this._top._curPart.getParticleChild(i);
                            if (candidate.canStartWithElement(eltName)) {
                                this._top._processedChildCount = i + 1;
                                this.push(candidate);
                                continue Label_0743;
                            }
                            if (!candidate.isSkippable()) {
                                if (this._top._processedChildCount != 0 || this._top._curCount < this._top._curMin) {
                                    return this.notValid();
                                }
                                break Label_0733;
                            }
                            else {
                                ++i;
                            }
                        }
                        final VisitorState top3 = this._top;
                        ++top3._curCount;
                        this._top._processedChildCount = 0;
                        continue;
                    }
                    case 2: {
                        for (int i = 0; i < this._top._childCount; ++i) {
                            final SchemaParticle candidate = this._top._curPart.getParticleChild(i);
                            if (candidate.canStartWithElement(eltName)) {
                                final VisitorState top4 = this._top;
                                ++top4._curCount;
                                this.push(candidate);
                                continue Label_0743;
                            }
                        }
                        if (this._top._curCount < this._top._curMin && !this._top._curPart.isSkippable()) {
                            return this.notValid();
                        }
                        break;
                    }
                    case 1: {
                        int skipped = this._top._processedChildCount;
                        for (int j = 0; j < this._top._childCount; ++j) {
                            if (!this._top._seen[j]) {
                                final SchemaParticle candidate2 = this._top._curPart.getParticleChild(j);
                                if (candidate2.canStartWithElement(eltName)) {
                                    final VisitorState top5 = this._top;
                                    ++top5._processedChildCount;
                                    this._top._seen[j] = true;
                                    this.push(candidate2);
                                    continue Label_0743;
                                }
                                if (candidate2.isSkippable()) {
                                    ++skipped;
                                }
                            }
                        }
                        if (skipped >= this._top._childCount) {
                            final VisitorState top6 = this._top;
                            ++top6._curCount;
                            this._top._processedChildCount = 0;
                            Arrays.fill(this._top._seen, false);
                            continue;
                        }
                        if (this._top._curCount < this._top._curMin) {
                            return this.notValid();
                        }
                        break;
                    }
                }
            }
            if (!this.pop()) {
                break;
            }
        }
        if (eltName == null) {
            return this.ok(null, testValidity);
        }
        return this.notValid();
    }
    
    public boolean testValid(final QName eltName) {
        return this.visit(eltName, true);
    }
    
    @Override
    public int get_elementflags() {
        if (this.currentParticle() == null || this.currentParticle().getParticleType() != 4) {
            return 0;
        }
        final SchemaLocalElement elt = (SchemaLocalElement)this.currentParticle();
        return (elt.isNillable() ? 1 : 0) | (elt.isDefault() ? 2 : 0) | (elt.isFixed() ? 4 : 0);
    }
    
    @Override
    public String get_default_text() {
        if (this.currentParticle() == null || this.currentParticle().getParticleType() != 4) {
            return null;
        }
        return ((SchemaLocalElement)this.currentParticle()).getDefaultText();
    }
    
    @Override
    public SchemaField get_schema_field() {
        if (this.currentParticle() instanceof SchemaField) {
            return (SchemaField)this.currentParticle();
        }
        return null;
    }
    
    public SchemaParticle currentParticle() {
        return this._matchedParticle;
    }
    
    public boolean isAllValid() {
        return this._isValid;
    }
    
    private static class VisitorState
    {
        SchemaParticle _curPart;
        int _curCount;
        int _curMax;
        int _curMin;
        int _processedChildCount;
        int _childCount;
        boolean[] _seen;
        
        public void copy(final VisitorState orig) {
            this._curPart = orig._curPart;
            this._curCount = orig._curCount;
            this._curMin = orig._curMin;
            this._curMax = orig._curMax;
            this._processedChildCount = orig._processedChildCount;
            this._childCount = orig._childCount;
            if (orig._seen != null) {
                this._seen = new boolean[orig._seen.length];
                System.arraycopy(orig._seen, 0, this._seen, 0, orig._seen.length);
            }
        }
        
        public void init(final SchemaParticle part) {
            this._curPart = part;
            this._curMin = part.getIntMinOccurs();
            this._curMax = part.getIntMaxOccurs();
            this._curCount = 0;
            this._processedChildCount = 0;
            this._childCount = part.countOfParticleChild();
            this._seen = (boolean[])((part.getParticleType() == 1) ? new boolean[this._childCount] : null);
        }
    }
}
