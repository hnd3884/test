package org.apache.xmlbeans.impl.common;

import org.apache.xmlbeans.XmlIDREF;
import org.apache.xmlbeans.XmlIDREFS;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlbeans.XmlID;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.xmlbeans.XmlObject;
import javax.xml.stream.Location;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaType;
import java.util.Collection;

public class IdentityConstraint
{
    private ConstraintState _constraintStack;
    private ElementState _elementStack;
    private Collection _errorListener;
    private boolean _invalid;
    private boolean _trackIdrefs;
    
    public IdentityConstraint(final Collection errorListener, final boolean trackIdrefs) {
        this._errorListener = errorListener;
        this._trackIdrefs = trackIdrefs;
    }
    
    public void element(final ValidatorListener.Event e, final SchemaType st, final SchemaIdentityConstraint[] ics) {
        this.newState();
        for (ConstraintState cs = this._constraintStack; cs != null; cs = cs._next) {
            cs.element(e, st);
        }
        for (int i = 0; ics != null && i < ics.length; ++i) {
            this.newConstraintState(ics[i], e, st);
        }
    }
    
    public void endElement(final ValidatorListener.Event e) {
        if (this._elementStack._hasConstraints) {
            for (ConstraintState cs = this._constraintStack; cs != null && cs != this._elementStack._savePoint; cs = cs._next) {
                cs.remove(e);
            }
            this._constraintStack = this._elementStack._savePoint;
        }
        this._elementStack = this._elementStack._next;
        for (ConstraintState cs = this._constraintStack; cs != null; cs = cs._next) {
            cs.endElement(e);
        }
    }
    
    public void attr(final ValidatorListener.Event e, final QName name, final SchemaType st, final String value) {
        for (ConstraintState cs = this._constraintStack; cs != null; cs = cs._next) {
            cs.attr(e, name, st, value);
        }
    }
    
    public void text(final ValidatorListener.Event e, final SchemaType st, final String value, final boolean emptyContent) {
        for (ConstraintState cs = this._constraintStack; cs != null; cs = cs._next) {
            cs.text(e, st, value, emptyContent);
        }
    }
    
    public boolean isValid() {
        return !this._invalid;
    }
    
    private void newConstraintState(final SchemaIdentityConstraint ic, final ValidatorListener.Event e, final SchemaType st) {
        if (ic.getConstraintCategory() == 2) {
            new KeyrefState(ic, e, st);
        }
        else {
            new SelectorState(ic, e, st);
        }
    }
    
    private void buildIdStates() {
        final IdState ids = new IdState();
        if (this._trackIdrefs) {
            new IdRefState(ids);
        }
    }
    
    private void newState() {
        final boolean firstTime = this._elementStack == null;
        final ElementState st = new ElementState();
        st._next = this._elementStack;
        this._elementStack = st;
        if (firstTime) {
            this.buildIdStates();
        }
    }
    
    private void emitError(final ValidatorListener.Event event, final String code, final Object[] args) {
        this._invalid = true;
        if (this._errorListener != null) {
            assert event != null;
            this._errorListener.add(errorForEvent(code, args, 0, event));
        }
    }
    
    public static XmlError errorForEvent(final String code, final Object[] args, final int severity, final ValidatorListener.Event event) {
        final XmlCursor loc = event.getLocationAsCursor();
        XmlError error;
        if (loc != null) {
            error = XmlError.forCursor(code, args, severity, loc);
        }
        else {
            final Location location = event.getLocation();
            if (location != null) {
                error = XmlError.forLocation(code, args, severity, location.getSystemId(), location.getLineNumber(), location.getColumnNumber(), location.getCharacterOffset());
            }
            else {
                error = XmlError.forMessage(code, args, severity);
            }
        }
        return error;
    }
    
    private void emitError(final ValidatorListener.Event event, final String msg) {
        this._invalid = true;
        if (this._errorListener != null) {
            assert event != null;
            this._errorListener.add(errorForEvent(msg, 0, event));
        }
    }
    
    public static XmlError errorForEvent(final String msg, final int severity, final ValidatorListener.Event event) {
        final XmlCursor loc = event.getLocationAsCursor();
        XmlError error;
        if (loc != null) {
            error = XmlError.forCursor(msg, severity, loc);
        }
        else {
            final Location location = event.getLocation();
            if (location != null) {
                error = XmlError.forLocation(msg, severity, location.getSystemId(), location.getLineNumber(), location.getColumnNumber(), location.getCharacterOffset());
            }
            else {
                error = XmlError.forMessage(msg, severity);
            }
        }
        return error;
    }
    
    private void setSavePoint(final ConstraintState cs) {
        if (!this._elementStack._hasConstraints) {
            this._elementStack._savePoint = cs;
        }
        this._elementStack._hasConstraints = true;
    }
    
    private static XmlObject newValue(final SchemaType st, final String value) {
        try {
            return st.newValue(value);
        }
        catch (final IllegalArgumentException e) {
            return null;
        }
    }
    
    static SchemaType getSimpleType(SchemaType st) {
        assert st.getContentType() == 2 : st + " does not have simple content.";
        while (!st.isSimpleType()) {
            st = st.getBaseType();
        }
        return st;
    }
    
    static boolean hasSimpleContent(final SchemaType st) {
        return st.isSimpleType() || st.getContentType() == 2;
    }
    
    public abstract class ConstraintState
    {
        ConstraintState _next;
        
        ConstraintState() {
            IdentityConstraint.this.setSavePoint(IdentityConstraint.this._constraintStack);
            this._next = IdentityConstraint.this._constraintStack;
            IdentityConstraint.this._constraintStack = this;
        }
        
        abstract void element(final ValidatorListener.Event p0, final SchemaType p1);
        
        abstract void endElement(final ValidatorListener.Event p0);
        
        abstract void attr(final ValidatorListener.Event p0, final QName p1, final SchemaType p2, final String p3);
        
        abstract void text(final ValidatorListener.Event p0, final SchemaType p1, final String p2, final boolean p3);
        
        abstract void remove(final ValidatorListener.Event p0);
    }
    
    public class SelectorState extends ConstraintState
    {
        SchemaIdentityConstraint _constraint;
        Set _values;
        XPath.ExecutionContext _context;
        
        SelectorState(final SchemaIdentityConstraint constraint, final ValidatorListener.Event e, final SchemaType st) {
            this._values = new LinkedHashSet();
            this._constraint = constraint;
            (this._context = new XPath.ExecutionContext()).init((XPath)this._constraint.getSelectorPath());
            if ((this._context.start() & 0x1) != 0x0) {
                this.createFieldState(e, st);
            }
        }
        
        void addFields(final XmlObjectList fields, final ValidatorListener.Event e) {
            if (this._constraint.getConstraintCategory() == 2) {
                this._values.add(fields);
            }
            else if (this._values.contains(fields)) {
                if (this._constraint.getConstraintCategory() == 3) {
                    IdentityConstraint.this.emitError(e, "cvc-identity-constraint.4.1", new Object[] { fields, QNameHelper.pretty(this._constraint.getName()) });
                }
                else {
                    IdentityConstraint.this.emitError(e, "cvc-identity-constraint.4.2.2", new Object[] { fields, QNameHelper.pretty(this._constraint.getName()) });
                }
            }
            else {
                this._values.add(fields);
            }
        }
        
        @Override
        void element(final ValidatorListener.Event e, final SchemaType st) {
            if ((this._context.element(e.getName()) & 0x1) != 0x0) {
                this.createFieldState(e, st);
            }
        }
        
        @Override
        void endElement(final ValidatorListener.Event e) {
            this._context.end();
        }
        
        void createFieldState(final ValidatorListener.Event e, final SchemaType st) {
            new FieldState(this, e, st);
        }
        
        @Override
        void remove(final ValidatorListener.Event e) {
            for (ConstraintState cs = this._next; cs != null; cs = cs._next) {
                if (cs instanceof KeyrefState) {
                    final KeyrefState kr = (KeyrefState)cs;
                    if (kr._constraint.getReferencedKey() == this._constraint) {
                        kr.addKeyValues(this._values, true);
                    }
                }
            }
        }
        
        @Override
        void attr(final ValidatorListener.Event e, final QName name, final SchemaType st, final String value) {
        }
        
        @Override
        void text(final ValidatorListener.Event e, final SchemaType st, final String value, final boolean emptyContent) {
        }
    }
    
    public class KeyrefState extends SelectorState
    {
        Map _keyValues;
        private Object CHILD_ADDED;
        private Object CHILD_REMOVED;
        private Object SELF_ADDED;
        
        KeyrefState(final SchemaIdentityConstraint constraint, final ValidatorListener.Event e, final SchemaType st) {
            super(constraint, e, st);
            this._keyValues = new HashMap();
            this.CHILD_ADDED = new Object();
            this.CHILD_REMOVED = new Object();
            this.SELF_ADDED = new Object();
        }
        
        void addKeyValues(final Set values, final boolean child) {
            for (final Object key : values) {
                final Object value = this._keyValues.get(key);
                if (value == null) {
                    this._keyValues.put(key, child ? this.CHILD_ADDED : this.SELF_ADDED);
                }
                else if (value == this.CHILD_ADDED) {
                    if (child) {
                        this._keyValues.put(key, this.CHILD_REMOVED);
                    }
                    else {
                        this._keyValues.put(key, this.SELF_ADDED);
                    }
                }
                else {
                    if (value != this.CHILD_REMOVED || child) {
                        continue;
                    }
                    this._keyValues.put(key, this.SELF_ADDED);
                }
            }
        }
        
        private boolean hasKeyValue(final Object key) {
            final Object value = this._keyValues.get(key);
            return value != null && value != this.CHILD_REMOVED;
        }
        
        @Override
        void remove(final ValidatorListener.Event e) {
            for (ConstraintState cs = this._next; cs != null && cs != IdentityConstraint.this._elementStack._savePoint; cs = cs._next) {
                if (cs instanceof SelectorState) {
                    final SelectorState sel = (SelectorState)cs;
                    if (sel._constraint == this._constraint.getReferencedKey()) {
                        this.addKeyValues(sel._values, false);
                    }
                }
            }
            for (final XmlObjectList fields : this._values) {
                if (fields.unfilled() < 0 && !this.hasKeyValue(fields)) {
                    IdentityConstraint.this.emitError(e, "cvc-identity-constraint.4.3", new Object[] { fields, QNameHelper.pretty(this._constraint.getName()) });
                }
            }
        }
    }
    
    public class FieldState extends ConstraintState
    {
        SelectorState _selector;
        XPath.ExecutionContext[] _contexts;
        boolean[] _needsValue;
        XmlObjectList _value;
        
        FieldState(final SelectorState selector, final ValidatorListener.Event e, final SchemaType st) {
            this._selector = selector;
            final SchemaIdentityConstraint ic = selector._constraint;
            final int fieldCount = ic.getFields().length;
            this._contexts = new XPath.ExecutionContext[fieldCount];
            this._needsValue = new boolean[fieldCount];
            this._value = new XmlObjectList(fieldCount);
            for (int i = 0; i < fieldCount; ++i) {
                (this._contexts[i] = new XPath.ExecutionContext()).init((XPath)ic.getFieldPath(i));
                if ((this._contexts[i].start() & 0x1) != 0x0) {
                    if (!IdentityConstraint.hasSimpleContent(st)) {
                        IdentityConstraint.this.emitError(e, "Identity constraint field must have simple content");
                    }
                    else {
                        this._needsValue[i] = true;
                    }
                }
            }
        }
        
        @Override
        void element(final ValidatorListener.Event e, final SchemaType st) {
            for (int i = 0; i < this._contexts.length; ++i) {
                if (this._needsValue[i]) {
                    IdentityConstraint.this.emitError(e, "Identity constraint field must have simple content");
                    this._needsValue[i] = false;
                }
            }
            for (int i = 0; i < this._contexts.length; ++i) {
                if ((this._contexts[i].element(e.getName()) & 0x1) != 0x0) {
                    if (!IdentityConstraint.hasSimpleContent(st)) {
                        IdentityConstraint.this.emitError(e, "Identity constraint field must have simple content");
                    }
                    else {
                        this._needsValue[i] = true;
                    }
                }
            }
        }
        
        @Override
        void attr(final ValidatorListener.Event e, final QName name, final SchemaType st, final String value) {
            if (value == null) {
                return;
            }
            for (int i = 0; i < this._contexts.length; ++i) {
                if (this._contexts[i].attr(name)) {
                    final XmlObject o = newValue(st, value);
                    if (o == null) {
                        return;
                    }
                    final boolean set = this._value.set(o, i);
                    if (!set) {
                        IdentityConstraint.this.emitError(e, "Multiple instances of field with xpath: '" + this._selector._constraint.getFields()[i] + "' for a selector");
                    }
                }
            }
        }
        
        @Override
        void text(final ValidatorListener.Event e, final SchemaType st, final String value, final boolean emptyContent) {
            if (value == null && !emptyContent) {
                return;
            }
            for (int i = 0; i < this._contexts.length; ++i) {
                if (this._needsValue[i]) {
                    if (emptyContent || !IdentityConstraint.hasSimpleContent(st)) {
                        IdentityConstraint.this.emitError(e, "Identity constraint field must have simple content");
                        return;
                    }
                    final SchemaType simpleType = IdentityConstraint.getSimpleType(st);
                    final XmlObject o = newValue(simpleType, value);
                    if (o == null) {
                        return;
                    }
                    final boolean set = this._value.set(o, i);
                    if (!set) {
                        IdentityConstraint.this.emitError(e, "Multiple instances of field with xpath: '" + this._selector._constraint.getFields()[i] + "' for a selector");
                    }
                }
            }
        }
        
        @Override
        void endElement(final ValidatorListener.Event e) {
            for (int i = 0; i < this._needsValue.length; ++i) {
                this._contexts[i].end();
                this._needsValue[i] = false;
            }
        }
        
        @Override
        void remove(final ValidatorListener.Event e) {
            if (this._selector._constraint.getConstraintCategory() == 1 && this._value.unfilled() >= 0) {
                IdentityConstraint.this.emitError(e, "Key " + QNameHelper.pretty(this._selector._constraint.getName()) + " is missing field with xpath: '" + this._selector._constraint.getFields()[this._value.unfilled()] + "'");
            }
            else {
                this._selector.addFields(this._value, e);
            }
        }
    }
    
    public class IdState extends ConstraintState
    {
        Set _values;
        
        IdState() {
            this._values = new LinkedHashSet();
        }
        
        @Override
        void attr(final ValidatorListener.Event e, final QName name, final SchemaType st, final String value) {
            this.handleValue(e, st, value);
        }
        
        @Override
        void text(final ValidatorListener.Event e, final SchemaType st, final String value, final boolean emptyContent) {
            if (emptyContent) {
                return;
            }
            this.handleValue(e, st, value);
        }
        
        private void handleValue(final ValidatorListener.Event e, final SchemaType st, final String value) {
            if (value == null) {
                return;
            }
            if (st == null || st.isNoType()) {
                return;
            }
            if (XmlID.type.isAssignableFrom(st)) {
                final XmlObjectList xmlValue = new XmlObjectList(1);
                final XmlObject o = newValue(XmlID.type, value);
                if (o == null) {
                    return;
                }
                xmlValue.set(o, 0);
                if (this._values.contains(xmlValue)) {
                    IdentityConstraint.this.emitError(e, "cvc-id.2", new Object[] { value });
                }
                else {
                    this._values.add(xmlValue);
                }
            }
        }
        
        @Override
        void element(final ValidatorListener.Event e, final SchemaType st) {
        }
        
        @Override
        void endElement(final ValidatorListener.Event e) {
        }
        
        @Override
        void remove(final ValidatorListener.Event e) {
        }
    }
    
    public class IdRefState extends ConstraintState
    {
        IdState _ids;
        List _values;
        
        IdRefState(final IdState ids) {
            this._ids = ids;
            this._values = new ArrayList();
        }
        
        private void handleValue(final ValidatorListener.Event e, final SchemaType st, final String value) {
            if (value == null) {
                return;
            }
            if (st == null || st.isNoType()) {
                return;
            }
            if (XmlIDREFS.type.isAssignableFrom(st)) {
                final XmlIDREFS lv = (XmlIDREFS)newValue(XmlIDREFS.type, value);
                if (lv == null) {
                    return;
                }
                final List l = lv.xgetListValue();
                for (int i = 0; i < l.size(); ++i) {
                    final XmlObjectList xmlValue = new XmlObjectList(1);
                    final XmlIDREF idref = l.get(i);
                    xmlValue.set(idref, 0);
                    this._values.add(xmlValue);
                }
            }
            else if (XmlIDREF.type.isAssignableFrom(st)) {
                final XmlObjectList xmlValue2 = new XmlObjectList(1);
                final XmlIDREF idref2 = (XmlIDREF)st.newValue(value);
                if (idref2 == null) {
                    return;
                }
                xmlValue2.set(idref2, 0);
                this._values.add(xmlValue2);
            }
        }
        
        @Override
        void attr(final ValidatorListener.Event e, final QName name, final SchemaType st, final String value) {
            this.handleValue(e, st, value);
        }
        
        @Override
        void text(final ValidatorListener.Event e, final SchemaType st, final String value, final boolean emptyContent) {
            if (emptyContent) {
                return;
            }
            this.handleValue(e, st, value);
        }
        
        @Override
        void remove(final ValidatorListener.Event e) {
            for (final Object o : this._values) {
                if (!this._ids._values.contains(o)) {
                    IdentityConstraint.this.emitError(e, "ID not found for IDRef value '" + o + "'");
                }
            }
        }
        
        @Override
        void element(final ValidatorListener.Event e, final SchemaType st) {
        }
        
        @Override
        void endElement(final ValidatorListener.Event e) {
        }
    }
    
    private static class ElementState
    {
        ElementState _next;
        boolean _hasConstraints;
        ConstraintState _savePoint;
    }
}
