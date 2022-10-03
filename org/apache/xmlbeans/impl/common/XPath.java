package org.apache.xmlbeans.impl.common;

import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import java.util.Map;

public class XPath
{
    public static final String _NS_BOUNDARY = "$xmlbeans!ns_boundary";
    public static final String _DEFAULT_ELT_NS = "$xmlbeans!default_uri";
    private final Selector _selector;
    private final boolean _sawDeepDot;
    
    public static XPath compileXPath(final String xpath) throws XPathCompileException {
        return compileXPath(xpath, "$this", null);
    }
    
    public static XPath compileXPath(final String xpath, final String currentNodeVar) throws XPathCompileException {
        return compileXPath(xpath, currentNodeVar, null);
    }
    
    public static XPath compileXPath(final String xpath, final Map namespaces) throws XPathCompileException {
        return compileXPath(xpath, "$this", namespaces);
    }
    
    public static XPath compileXPath(final String xpath, final String currentNodeVar, final Map namespaces) throws XPathCompileException {
        return new CompilationContext(namespaces, currentNodeVar).compile(xpath);
    }
    
    private XPath(final Selector selector, final boolean sawDeepDot) {
        this._selector = selector;
        this._sawDeepDot = sawDeepDot;
    }
    
    public boolean sawDeepDot() {
        return this._sawDeepDot;
    }
    
    public static class XPathCompileException extends XmlException
    {
        XPathCompileException(final XmlError err) {
            super(err.toString(), null, err);
        }
    }
    
    public static class ExecutionContext
    {
        public static final int HIT = 1;
        public static final int DESCEND = 2;
        public static final int ATTRS = 4;
        private XPath _xpath;
        private ArrayList _stack;
        private PathContext[] _paths;
        
        public ExecutionContext() {
            this._stack = new ArrayList();
        }
        
        public final void init(final XPath xpath) {
            if (this._xpath != xpath) {
                this._xpath = xpath;
                this._paths = new PathContext[xpath._selector._paths.length];
                for (int i = 0; i < this._paths.length; ++i) {
                    this._paths[i] = new PathContext();
                }
            }
            this._stack.clear();
            for (int i = 0; i < this._paths.length; ++i) {
                this._paths[i].init(xpath._selector._paths[i]);
            }
        }
        
        public final int start() {
            int result = 0;
            for (int i = 0; i < this._paths.length; ++i) {
                result |= this._paths[i].start();
            }
            return result;
        }
        
        public final int element(final QName name) {
            assert name != null;
            this._stack.add(name);
            int result = 0;
            for (int i = 0; i < this._paths.length; ++i) {
                result |= this._paths[i].element(name);
            }
            return result;
        }
        
        public final boolean attr(final QName name) {
            boolean hit = false;
            for (int i = 0; i < this._paths.length; ++i) {
                hit |= this._paths[i].attr(name);
            }
            return hit;
        }
        
        public final void end() {
            this._stack.remove(this._stack.size() - 1);
            for (int i = 0; i < this._paths.length; ++i) {
                this._paths[i].end();
            }
        }
        
        private final class PathContext
        {
            private Step _curr;
            private List _prev;
            
            PathContext() {
                this._prev = new ArrayList();
            }
            
            void init(final Step steps) {
                this._curr = steps;
                this._prev.clear();
            }
            
            private QName top(final int i) {
                return ExecutionContext.this._stack.get(ExecutionContext.this._stack.size() - 1 - i);
            }
            
            private void backtrack() {
                assert this._curr != null;
                if (this._curr._hasBacktrack) {
                    this._curr = this._curr._backtrack;
                    return;
                }
                assert !this._curr._deep;
                this._curr = this._curr._prev;
            Label_0078:
                while (!this._curr._deep) {
                    int t = 0;
                    for (Step s = this._curr; !s._deep; s = s._prev) {
                        if (!s.match(this.top(t++))) {
                            this._curr = this._curr._prev;
                            continue Label_0078;
                        }
                    }
                    break;
                }
            }
            
            int start() {
                assert this._curr != null;
                assert this._curr._prev == null;
                if (this._curr._name != null) {
                    return this._curr._flags;
                }
                this._curr = null;
                return 1;
            }
            
            int element(final QName name) {
                this._prev.add(this._curr);
                if (this._curr == null) {
                    return 0;
                }
                assert this._curr._name != null;
                if (this._curr._attr || !this._curr.match(name)) {
                    do {
                        this.backtrack();
                        if (this._curr == null) {
                            return 0;
                        }
                        if (this._curr.match(name)) {
                            this._curr = this._curr._next;
                            break;
                        }
                    } while (!this._curr._deep);
                    return this._curr._flags;
                }
                final Step next = this._curr._next;
                this._curr = next;
                if (next._name != null) {
                    return this._curr._flags;
                }
                this.backtrack();
                return (this._curr == null) ? 1 : (0x1 | this._curr._flags);
            }
            
            boolean attr(final QName name) {
                return this._curr != null && this._curr._attr && this._curr.match(name);
            }
            
            void end() {
                this._curr = this._prev.remove(this._prev.size() - 1);
            }
        }
    }
    
    private static class CompilationContext
    {
        private String _expr;
        private boolean _sawDeepDot;
        private boolean _lastDeepDot;
        private String _currentNodeVar;
        protected Map _namespaces;
        private Map _externalNamespaces;
        private int _offset;
        private int _line;
        private int _column;
        
        CompilationContext(final Map namespaces, final String currentNodeVar) {
            assert !(!this._currentNodeVar.startsWith("$"));
            if (currentNodeVar == null) {
                this._currentNodeVar = "$this";
            }
            else {
                this._currentNodeVar = currentNodeVar;
            }
            this._namespaces = new HashMap();
            this._externalNamespaces = ((namespaces == null) ? new HashMap() : namespaces);
        }
        
        XPath compile(final String expr) throws XPathCompileException {
            this._offset = 0;
            this._line = 1;
            this._column = 1;
            this._expr = expr;
            return this.tokenizeXPath();
        }
        
        int currChar() {
            return this.currChar(0);
        }
        
        int currChar(final int offset) {
            return (this._offset + offset >= this._expr.length()) ? -1 : this._expr.charAt(this._offset + offset);
        }
        
        void advance() {
            if (this._offset < this._expr.length()) {
                final char ch = this._expr.charAt(this._offset);
                ++this._offset;
                ++this._column;
                if (ch == '\r' || ch == '\n') {
                    ++this._line;
                    this._column = 1;
                    if (this._offset + 1 < this._expr.length()) {
                        final char nextCh = this._expr.charAt(this._offset + 1);
                        if ((nextCh == '\r' || nextCh == '\n') && ch != nextCh) {
                            ++this._offset;
                        }
                    }
                }
            }
        }
        
        void advance(int count) {
            assert count >= 0;
            while (count-- > 0) {
                this.advance();
            }
        }
        
        boolean isWhitespace() {
            return this.isWhitespace(0);
        }
        
        boolean isWhitespace(final int offset) {
            final int ch = this.currChar(offset);
            return ch == 32 || ch == 9 || ch == 10 || ch == 13;
        }
        
        boolean isNCNameStart() {
            return this.currChar() != -1 && XMLChar.isNCNameStart(this.currChar());
        }
        
        boolean isNCName() {
            return this.currChar() != -1 && XMLChar.isNCName(this.currChar());
        }
        
        boolean startsWith(final String s) {
            return this.startsWith(s, 0);
        }
        
        boolean startsWith(final String s, final int offset) {
            return this._offset + offset < this._expr.length() && this._expr.startsWith(s, this._offset + offset);
        }
        
        private XPathCompileException newError(final String msg) {
            final XmlError err = XmlError.forLocation(msg, 0, null, this._line, this._column, this._offset);
            return new XPathCompileException(err);
        }
        
        String lookupPrefix(final String prefix) throws XPathCompileException {
            if (this._namespaces.containsKey(prefix)) {
                return this._namespaces.get(prefix);
            }
            if (this._externalNamespaces.containsKey(prefix)) {
                return this._externalNamespaces.get(prefix);
            }
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (prefix.equals("xs")) {
                return "http://www.w3.org/2001/XMLSchema";
            }
            if (prefix.equals("xsi")) {
                return "http://www.w3.org/2001/XMLSchema-instance";
            }
            if (prefix.equals("fn")) {
                return "http://www.w3.org/2002/11/xquery-functions";
            }
            if (prefix.equals("xdt")) {
                return "http://www.w3.org/2003/11/xpath-datatypes";
            }
            if (prefix.equals("local")) {
                return "http://www.w3.org/2003/11/xquery-local-functions";
            }
            throw this.newError("Undefined prefix: " + prefix);
        }
        
        private boolean parseWhitespace() throws XPathCompileException {
            boolean sawSpace = false;
            while (this.isWhitespace()) {
                this.advance();
                sawSpace = true;
            }
            return sawSpace;
        }
        
        private boolean tokenize(final String s) {
            assert s.length() > 0;
            int offset;
            for (offset = 0; this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s, offset)) {
                return false;
            }
            offset += s.length();
            this.advance(offset);
            return true;
        }
        
        private boolean tokenize(final String s1, final String s2) {
            assert s1.length() > 0;
            assert s2.length() > 0;
            int offset;
            for (offset = 0; this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s1, offset)) {
                return false;
            }
            for (offset += s1.length(); this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s2, offset)) {
                return false;
            }
            offset += s2.length();
            this.advance(offset);
            return true;
        }
        
        private boolean tokenize(final String s1, final String s2, final String s3) {
            assert s1.length() > 0;
            assert s2.length() > 0;
            assert s3.length() > 0;
            int offset;
            for (offset = 0; this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s1, offset)) {
                return false;
            }
            for (offset += s1.length(); this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s2, offset)) {
                return false;
            }
            for (offset += s2.length(); this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s3, offset)) {
                return false;
            }
            for (offset += s3.length(); this.isWhitespace(offset); ++offset) {}
            this.advance(offset);
            return true;
        }
        
        private boolean tokenize(final String s1, final String s2, final String s3, final String s4) {
            assert s1.length() > 0;
            assert s2.length() > 0;
            assert s3.length() > 0;
            assert s4.length() > 0;
            int offset;
            for (offset = 0; this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s1, offset)) {
                return false;
            }
            for (offset += s1.length(); this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s2, offset)) {
                return false;
            }
            for (offset += s2.length(); this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s3, offset)) {
                return false;
            }
            for (offset += s3.length(); this.isWhitespace(offset); ++offset) {}
            if (!this.startsWith(s4, offset)) {
                return false;
            }
            offset += s4.length();
            this.advance(offset);
            return true;
        }
        
        private String tokenizeNCName() throws XPathCompileException {
            this.parseWhitespace();
            if (!this.isNCNameStart()) {
                throw this.newError("Expected non-colonized name");
            }
            final StringBuffer sb = new StringBuffer();
            sb.append((char)this.currChar());
            this.advance();
            while (this.isNCName()) {
                sb.append((char)this.currChar());
                this.advance();
            }
            return sb.toString();
        }
        
        private QName getAnyQName() {
            return new QName("", "");
        }
        
        private QName tokenizeQName() throws XPathCompileException {
            if (this.tokenize("*")) {
                return this.getAnyQName();
            }
            final String ncName = this.tokenizeNCName();
            if (!this.tokenize(":")) {
                return new QName(this.lookupPrefix(""), ncName);
            }
            return new QName(this.lookupPrefix(ncName), this.tokenize("*") ? "" : this.tokenizeNCName());
        }
        
        private String tokenizeQuotedUri() throws XPathCompileException {
            char quote;
            if (this.tokenize("\"")) {
                quote = '\"';
            }
            else {
                if (!this.tokenize("'")) {
                    throw this.newError("Expected quote (\" or ')");
                }
                quote = '\'';
            }
            final StringBuffer sb = new StringBuffer();
            while (this.currChar() != -1) {
                if (this.currChar() == quote) {
                    this.advance();
                    if (this.currChar() != quote) {
                        return sb.toString();
                    }
                }
                sb.append((char)this.currChar());
                this.advance();
            }
            throw this.newError("Path terminated in URI literal");
        }
        
        private Step addStep(final boolean deep, final boolean attr, final QName name, Step steps) {
            final Step step = new Step(deep, attr, name);
            if (steps == null) {
                return step;
            }
            final Step s = steps;
            while (steps._next != null) {
                steps = steps._next;
            }
            steps._next = step;
            step._prev = steps;
            return s;
        }
        
        private Step tokenizeSteps() throws XPathCompileException {
            if (this.tokenize("/")) {
                throw this.newError("Absolute paths unsupported");
            }
            boolean deep;
            if (this.tokenize("$", this._currentNodeVar, "//") || this.tokenize(".", "//")) {
                deep = true;
            }
            else if (this.tokenize("$", this._currentNodeVar, "/") || this.tokenize(".", "/")) {
                deep = false;
            }
            else {
                if (this.tokenize("$", this._currentNodeVar) || this.tokenize(".")) {
                    return this.addStep(false, false, null, null);
                }
                deep = false;
            }
            Step steps = null;
            boolean deepDot = false;
            while (true) {
                while (!this.tokenize("attribute", "::") && !this.tokenize("@")) {
                    if (this.tokenize(".")) {
                        deepDot = (deepDot || deep);
                    }
                    else {
                        this.tokenize("child", "::");
                        final QName name;
                        if ((name = this.tokenizeQName()) != null) {
                            steps = this.addStep(deep, false, name, steps);
                            deep = false;
                        }
                    }
                    if (this.tokenize("//")) {
                        deep = true;
                        deepDot = false;
                    }
                    else {
                        if (!this.tokenize("/")) {
                            final boolean lastDeepDot = deepDot;
                            this._lastDeepDot = lastDeepDot;
                            if (lastDeepDot) {
                                this._lastDeepDot = true;
                                steps = this.addStep(true, false, this.getAnyQName(), steps);
                            }
                            return this.addStep(false, false, null, steps);
                        }
                        if (!deepDot) {
                            continue;
                        }
                        deep = true;
                    }
                }
                steps = this.addStep(deep, true, this.tokenizeQName(), steps);
                continue;
            }
        }
        
        private void computeBacktrack(final Step steps) throws XPathCompileException {
            Step t;
            for (Step s = steps; s != null; s = t) {
                for (t = s._next; t != null && !t._deep; t = t._next) {}
                if (!s._deep) {
                    for (Step u = s; u != t; u = u._next) {
                        u._hasBacktrack = true;
                    }
                }
                else {
                    int n = 0;
                    Step u2;
                    for (u2 = s; u2 != t && u2._name != null && !u2.isWild() && !u2._attr; u2 = u2._next) {
                        ++n;
                    }
                    final QName[] pattern = new QName[n + 1];
                    final int[] kmp = new int[n + 1];
                    Step v = s;
                    for (int i = 0; i < n; ++i) {
                        pattern[i] = v._name;
                        v = v._next;
                    }
                    pattern[n] = this.getAnyQName();
                    int i = 0;
                    final int[] array = kmp;
                    final int n2 = 0;
                    final int n3 = -1;
                    array[n2] = n3;
                    int j = n3;
                    while (i < n) {
                        while (j > -1 && !pattern[i].equals(pattern[j])) {
                            j = kmp[j];
                        }
                        if (pattern[++i].equals(pattern[++j])) {
                            kmp[i] = kmp[j];
                        }
                        else {
                            kmp[i] = j;
                        }
                    }
                    i = 0;
                    for (v = s; v != u2; v = v._next) {
                        v._hasBacktrack = true;
                        v._backtrack = s;
                        for (j = kmp[i]; j > 0; --j) {
                            v._backtrack = v._backtrack._next;
                        }
                        ++i;
                    }
                    v = s;
                    if (n > 1) {
                        for (j = kmp[n - 1]; j > 0; --j) {
                            v = v._next;
                        }
                    }
                    if (u2 != t && u2._attr) {
                        u2._hasBacktrack = true;
                        u2._backtrack = v;
                        u2 = u2._next;
                    }
                    if (u2 != t && u2._name == null) {
                        u2._hasBacktrack = true;
                        u2._backtrack = v;
                    }
                    assert s._deep;
                    s._hasBacktrack = true;
                    s._backtrack = s;
                }
            }
        }
        
        private void tokenizePath(final ArrayList paths) throws XPathCompileException {
            this._lastDeepDot = false;
            final Step steps = this.tokenizeSteps();
            this.computeBacktrack(steps);
            paths.add(steps);
            if (this._lastDeepDot) {
                this._sawDeepDot = true;
                Step s = null;
                for (Step t = steps; t != null; t = t._next) {
                    if (t._next != null && t._next._next == null) {
                        s = this.addStep(t._deep, true, t._name, s);
                    }
                    else {
                        s = this.addStep(t._deep, t._attr, t._name, s);
                    }
                }
                this.computeBacktrack(s);
                paths.add(s);
            }
        }
        
        private Selector tokenizeSelector() throws XPathCompileException {
            final ArrayList paths = new ArrayList();
            this.tokenizePath(paths);
            while (this.tokenize("|")) {
                this.tokenizePath(paths);
            }
            return new Selector(paths.toArray(new Step[0]));
        }
        
        private XPath tokenizeXPath() throws XPathCompileException {
            while (true) {
                if (this.tokenize("declare", "namespace")) {
                    if (!this.parseWhitespace()) {
                        throw this.newError("Expected prefix after 'declare namespace'");
                    }
                    final String prefix = this.tokenizeNCName();
                    if (!this.tokenize("=")) {
                        throw this.newError("Expected '='");
                    }
                    final String uri = this.tokenizeQuotedUri();
                    if (this._namespaces.containsKey(prefix)) {
                        throw this.newError("Redefinition of namespace prefix: " + prefix);
                    }
                    this._namespaces.put(prefix, uri);
                    if (this._externalNamespaces.containsKey(prefix)) {
                        throw this.newError("Redefinition of namespace prefix: " + prefix);
                    }
                    this._externalNamespaces.put(prefix, uri);
                    if (!this.tokenize(";")) {}
                    this._externalNamespaces.put("$xmlbeans!ns_boundary", new Integer(this._offset));
                }
                else if (this.tokenize("declare", "default", "element", "namespace")) {
                    final String uri2 = this.tokenizeQuotedUri();
                    if (this._namespaces.containsKey("")) {
                        throw this.newError("Redefinition of default element namespace");
                    }
                    this._namespaces.put("", uri2);
                    if (this._externalNamespaces.containsKey("$xmlbeans!default_uri")) {
                        throw this.newError("Redefinition of default element namespace : ");
                    }
                    this._externalNamespaces.put("$xmlbeans!default_uri", uri2);
                    if (!this.tokenize(";")) {
                        throw this.newError("Default Namespace declaration must end with ;");
                    }
                    this._externalNamespaces.put("$xmlbeans!ns_boundary", new Integer(this._offset));
                }
                else {
                    if (!this._namespaces.containsKey("")) {
                        this._namespaces.put("", "");
                    }
                    final Selector selector = this.tokenizeSelector();
                    this.parseWhitespace();
                    if (this.currChar() != -1) {
                        throw this.newError("Unexpected char '" + (char)this.currChar() + "'");
                    }
                    return new XPath(selector, this._sawDeepDot, null);
                }
            }
        }
        
        private void processNonXpathDecls() {
        }
    }
    
    private static final class Step
    {
        final boolean _attr;
        final boolean _deep;
        int _flags;
        final QName _name;
        Step _next;
        Step _prev;
        boolean _hasBacktrack;
        Step _backtrack;
        
        Step(final boolean deep, final boolean attr, final QName name) {
            this._name = name;
            this._deep = deep;
            this._attr = attr;
            int flags = 0;
            if (this._deep || !this._attr) {
                flags |= 0x2;
            }
            if (this._attr) {
                flags |= 0x4;
            }
            this._flags = flags;
        }
        
        boolean isWild() {
            return this._name.getLocalPart().length() == 0;
        }
        
        boolean match(final QName name) {
            final String local = this._name.getLocalPart();
            final String nameLocal = name.getLocalPart();
            final int localLength = local.length();
            if (localLength == 0) {
                final String uri = this._name.getNamespaceURI();
                final int uriLength = uri.length();
                return uriLength == 0 || uri.equals(name.getNamespaceURI());
            }
            if (localLength != nameLocal.length()) {
                return false;
            }
            final String uri = this._name.getNamespaceURI();
            final String nameUri = name.getNamespaceURI();
            return uri.length() == nameUri.length() && local.equals(nameLocal) && uri.equals(nameUri);
        }
    }
    
    private static final class Selector
    {
        final Step[] _paths;
        
        Selector(final Step[] paths) {
            this._paths = paths;
        }
    }
}
