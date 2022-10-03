package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import java.util.StringTokenizer;
import java.util.HashMap;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import java.util.Map;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public class KeyIndex extends DTMAxisIteratorBase
{
    private Map<String, IntegerArray> _index;
    private int _currentDocumentNode;
    private Map<Integer, Map> _rootToIndexMap;
    private IntegerArray _nodes;
    private DOM _dom;
    private DOMEnhancedForDTM _enhancedDOM;
    private int _markedPosition;
    private static final IntegerArray EMPTY_NODES;
    
    public KeyIndex(final int dummy) {
        this._currentDocumentNode = -1;
        this._rootToIndexMap = new HashMap<Integer, Map>();
        this._nodes = null;
        this._markedPosition = 0;
    }
    
    @Override
    public void setRestartable(final boolean flag) {
    }
    
    public void add(final String value, final int node, final int rootNode) {
        if (this._currentDocumentNode != rootNode) {
            this._currentDocumentNode = rootNode;
            this._index = new HashMap<String, IntegerArray>();
            this._rootToIndexMap.put(rootNode, this._index);
        }
        IntegerArray nodes = this._index.get(value);
        if (nodes == null) {
            nodes = new IntegerArray();
            this._index.put(value, nodes);
            nodes.add(node);
        }
        else if (node != nodes.at(nodes.cardinality() - 1)) {
            nodes.add(node);
        }
    }
    
    @Deprecated
    public void merge(final KeyIndex other) {
        if (other == null) {
            return;
        }
        if (other._nodes != null) {
            if (this._nodes == null) {
                this._nodes = (IntegerArray)other._nodes.clone();
            }
            else {
                this._nodes.merge(other._nodes);
            }
        }
    }
    
    @Deprecated
    public void lookupId(final Object value) {
        this._nodes = null;
        final StringTokenizer values = new StringTokenizer((String)value, " \n\t");
        while (values.hasMoreElements()) {
            final String token = (String)values.nextElement();
            IntegerArray nodes = this._index.get(token);
            if (nodes == null && this._enhancedDOM != null && this._enhancedDOM.hasDOMSource()) {
                nodes = this.getDOMNodeById(token);
            }
            if (nodes == null) {
                continue;
            }
            if (this._nodes == null) {
                nodes = (IntegerArray)nodes.clone();
                this._nodes = nodes;
            }
            else {
                this._nodes.merge(nodes);
            }
        }
    }
    
    public IntegerArray getDOMNodeById(final String id) {
        IntegerArray nodes = null;
        if (this._enhancedDOM != null) {
            final int ident = this._enhancedDOM.getElementById(id);
            if (ident != -1) {
                final Integer root = new Integer(this._enhancedDOM.getDocument());
                Map<String, IntegerArray> index = this._rootToIndexMap.get(root);
                if (index == null) {
                    index = new HashMap<String, IntegerArray>();
                    this._rootToIndexMap.put(root, index);
                }
                else {
                    nodes = index.get(id);
                }
                if (nodes == null) {
                    nodes = new IntegerArray();
                    index.put(id, nodes);
                }
                nodes.add(this._enhancedDOM.getNodeHandle(ident));
            }
        }
        return nodes;
    }
    
    @Deprecated
    public void lookupKey(final Object value) {
        final IntegerArray nodes = this._index.get(value);
        this._nodes = ((nodes != null) ? ((IntegerArray)nodes.clone()) : null);
        this._position = 0;
    }
    
    @Override
    @Deprecated
    public int next() {
        if (this._nodes == null) {
            return -1;
        }
        return (this._position < this._nodes.cardinality()) ? this._dom.getNodeHandle(this._nodes.at(this._position++)) : -1;
    }
    
    public int containsID(final int node, final Object value) {
        final String string = (String)value;
        final int rootHandle = this._dom.getAxisIterator(19).setStartNode(node).next();
        final Map<String, IntegerArray> index = this._rootToIndexMap.get(rootHandle);
        final StringTokenizer values = new StringTokenizer(string, " \n\t");
        while (values.hasMoreElements()) {
            final String token = (String)values.nextElement();
            IntegerArray nodes = null;
            if (index != null) {
                nodes = index.get(token);
            }
            if (nodes == null && this._enhancedDOM != null && this._enhancedDOM.hasDOMSource()) {
                nodes = this.getDOMNodeById(token);
            }
            if (nodes != null && nodes.indexOf(node) >= 0) {
                return 1;
            }
        }
        return 0;
    }
    
    public int containsKey(final int node, final Object value) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex._dom:Lcom/sun/org/apache/xalan/internal/xsltc/DOM;
        //     4: bipush          19
        //     6: invokeinterface com/sun/org/apache/xalan/internal/xsltc/DOM.getAxisIterator:(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;
        //    11: iload_1         /* node */
        //    12: invokeinterface com/sun/org/apache/xml/internal/dtm/DTMAxisIterator.setStartNode:(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;
        //    17: invokeinterface com/sun/org/apache/xml/internal/dtm/DTMAxisIterator.next:()I
        //    22: istore_3        /* rootHandle */
        //    23: aload_0         /* this */
        //    24: getfield        com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex._rootToIndexMap:Ljava/util/Map;
        //    27: new             Ljava/lang/Integer;
        //    30: dup            
        //    31: iload_3         /* rootHandle */
        //    32: invokespecial   java/lang/Integer.<init>:(I)V
        //    35: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    40: checkcast       Ljava/util/Map;
        //    43: astore          index
        //    45: aload           index
        //    47: ifnull          83
        //    50: aload           index
        //    52: aload_2         /* value */
        //    53: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    58: checkcast       Lcom/sun/org/apache/xalan/internal/xsltc/util/IntegerArray;
        //    61: astore          nodes
        //    63: aload           nodes
        //    65: ifnull          81
        //    68: aload           nodes
        //    70: iload_1         /* node */
        //    71: invokevirtual   com/sun/org/apache/xalan/internal/xsltc/util/IntegerArray.indexOf:(I)I
        //    74: iflt            81
        //    77: iconst_1       
        //    78: goto            82
        //    81: iconst_0       
        //    82: ireturn        
        //    83: iconst_0       
        //    84: ireturn        
        //    StackMapTable: 00 03 FE 00 51 01 07 00 79 07 00 62 40 01 FA 00 00
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.MetadataHelper.substituteGenericArguments(MetadataHelper.java:1205)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2696)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1510)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:892)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:593)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:405)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    @Deprecated
    public DTMAxisIterator reset() {
        this._position = 0;
        return this;
    }
    
    @Override
    @Deprecated
    public int getLast() {
        return (this._nodes == null) ? 0 : this._nodes.cardinality();
    }
    
    @Override
    @Deprecated
    public int getPosition() {
        return this._position;
    }
    
    @Override
    @Deprecated
    public void setMark() {
        this._markedPosition = this._position;
    }
    
    @Override
    @Deprecated
    public void gotoMark() {
        this._position = this._markedPosition;
    }
    
    @Override
    @Deprecated
    public DTMAxisIterator setStartNode(final int start) {
        if (start == -1) {
            this._nodes = null;
        }
        else if (this._nodes != null) {
            this._position = 0;
        }
        return this;
    }
    
    @Override
    @Deprecated
    public int getStartNode() {
        return 0;
    }
    
    @Override
    @Deprecated
    public boolean isReverse() {
        return false;
    }
    
    @Override
    @Deprecated
    public DTMAxisIterator cloneIterator() {
        final KeyIndex other = new KeyIndex(0);
        other._index = this._index;
        other._rootToIndexMap = this._rootToIndexMap;
        other._nodes = this._nodes;
        other._position = this._position;
        return other;
    }
    
    public void setDom(DOM dom, final int node) {
        this._dom = dom;
        if (dom instanceof MultiDOM) {
            dom = ((MultiDOM)dom).getDTM(node);
        }
        if (dom instanceof DOMEnhancedForDTM) {
            this._enhancedDOM = (DOMEnhancedForDTM)dom;
        }
        else if (dom instanceof DOMAdapter) {
            final DOM idom = ((DOMAdapter)dom).getDOMImpl();
            if (idom instanceof DOMEnhancedForDTM) {
                this._enhancedDOM = (DOMEnhancedForDTM)idom;
            }
        }
    }
    
    public KeyIndexIterator getKeyIndexIterator(final Object keyValue, final boolean isKeyCall) {
        if (keyValue instanceof DTMAxisIterator) {
            return this.getKeyIndexIterator((DTMAxisIterator)keyValue, isKeyCall);
        }
        return this.getKeyIndexIterator(BasisLibrary.stringF(keyValue, this._dom), isKeyCall);
    }
    
    public KeyIndexIterator getKeyIndexIterator(final String keyValue, final boolean isKeyCall) {
        return new KeyIndexIterator(keyValue, isKeyCall);
    }
    
    public KeyIndexIterator getKeyIndexIterator(final DTMAxisIterator keyValue, final boolean isKeyCall) {
        return new KeyIndexIterator(keyValue, isKeyCall);
    }
    
    static {
        EMPTY_NODES = new IntegerArray(0);
    }
    
    public class KeyIndexIterator extends MultiValuedNodeHeapIterator
    {
        private IntegerArray _nodes;
        private DTMAxisIterator _keyValueIterator;
        private String _keyValue;
        private boolean _isKeyIterator;
        
        KeyIndexIterator(final String keyValue, final boolean isKeyIterator) {
            this._isKeyIterator = isKeyIterator;
            this._keyValue = keyValue;
        }
        
        KeyIndexIterator(final DTMAxisIterator keyValues, final boolean isKeyIterator) {
            this._keyValueIterator = keyValues;
            this._isKeyIterator = isKeyIterator;
        }
        
        protected IntegerArray lookupNodes(final int root, final String keyValue) {
            IntegerArray result = null;
            final Map<String, IntegerArray> index = KeyIndex.this._rootToIndexMap.get(root);
            if (!this._isKeyIterator) {
                final StringTokenizer values = new StringTokenizer(keyValue, " \n\t");
                while (values.hasMoreElements()) {
                    final String token = (String)values.nextElement();
                    IntegerArray nodes = null;
                    if (index != null) {
                        nodes = index.get(token);
                    }
                    if (nodes == null && KeyIndex.this._enhancedDOM != null && KeyIndex.this._enhancedDOM.hasDOMSource()) {
                        nodes = KeyIndex.this.getDOMNodeById(token);
                    }
                    if (nodes != null) {
                        if (result == null) {
                            result = (IntegerArray)nodes.clone();
                        }
                        else {
                            result.merge(nodes);
                        }
                    }
                }
            }
            else if (index != null) {
                result = index.get(keyValue);
            }
            return result;
        }
        
        @Override
        public DTMAxisIterator setStartNode(final int node) {
            this._startNode = node;
            if (this._keyValueIterator != null) {
                this._keyValueIterator = this._keyValueIterator.setStartNode(node);
            }
            this.init();
            return super.setStartNode(node);
        }
        
        @Override
        public int next() {
            int nodeHandle;
            if (this._nodes != null) {
                if (this._position < this._nodes.cardinality()) {
                    nodeHandle = this.returnNode(this._nodes.at(this._position));
                }
                else {
                    nodeHandle = -1;
                }
            }
            else {
                nodeHandle = super.next();
            }
            return nodeHandle;
        }
        
        @Override
        public DTMAxisIterator reset() {
            if (this._nodes == null) {
                this.init();
            }
            else {
                super.reset();
            }
            return this.resetPosition();
        }
        
        @Override
        protected void init() {
            super.init();
            this._position = 0;
            final int rootHandle = KeyIndex.this._dom.getAxisIterator(19).setStartNode(this._startNode).next();
            if (this._keyValueIterator == null) {
                this._nodes = this.lookupNodes(rootHandle, this._keyValue);
                if (this._nodes == null) {
                    this._nodes = KeyIndex.EMPTY_NODES;
                }
            }
            else {
                final DTMAxisIterator keyValues = this._keyValueIterator.reset();
                final int retrievedKeyValueIdx = 0;
                boolean foundNodes = false;
                this._nodes = null;
                for (int keyValueNode = keyValues.next(); keyValueNode != -1; keyValueNode = keyValues.next()) {
                    final String keyValue = BasisLibrary.stringF(keyValueNode, KeyIndex.this._dom);
                    final IntegerArray nodes = this.lookupNodes(rootHandle, keyValue);
                    if (nodes != null) {
                        if (!foundNodes) {
                            this._nodes = nodes;
                            foundNodes = true;
                        }
                        else {
                            if (this._nodes != null) {
                                this.addHeapNode(new KeyIndexHeapNode(this._nodes));
                                this._nodes = null;
                            }
                            this.addHeapNode(new KeyIndexHeapNode(nodes));
                        }
                    }
                }
                if (!foundNodes) {
                    this._nodes = KeyIndex.EMPTY_NODES;
                }
            }
        }
        
        @Override
        public int getLast() {
            return (this._nodes != null) ? this._nodes.cardinality() : super.getLast();
        }
        
        @Override
        public int getNodeByPosition(final int position) {
            int node = -1;
            if (this._nodes != null) {
                if (position > 0) {
                    if (position <= this._nodes.cardinality()) {
                        this._position = position;
                        node = this._nodes.at(position - 1);
                    }
                    else {
                        this._position = this._nodes.cardinality();
                    }
                }
            }
            else {
                node = super.getNodeByPosition(position);
            }
            return node;
        }
        
        protected class KeyIndexHeapNode extends HeapNode
        {
            private IntegerArray _nodes;
            private int _position;
            private int _markPosition;
            
            KeyIndexHeapNode(final IntegerArray nodes) {
                this._position = 0;
                this._markPosition = -1;
                this._nodes = nodes;
            }
            
            @Override
            public int step() {
                if (this._position < this._nodes.cardinality()) {
                    this._node = this._nodes.at(this._position);
                    ++this._position;
                }
                else {
                    this._node = -1;
                }
                return this._node;
            }
            
            @Override
            public HeapNode cloneHeapNode() {
                final KeyIndexHeapNode clone = (KeyIndexHeapNode)super.cloneHeapNode();
                clone._nodes = this._nodes;
                clone._position = this._position;
                clone._markPosition = this._markPosition;
                return clone;
            }
            
            @Override
            public void setMark() {
                this._markPosition = this._position;
            }
            
            @Override
            public void gotoMark() {
                this._position = this._markPosition;
            }
            
            @Override
            public boolean isLessThan(final HeapNode heapNode) {
                return this._node < heapNode._node;
            }
            
            @Override
            public HeapNode setStartNode(final int node) {
                return this;
            }
            
            @Override
            public HeapNode reset() {
                this._position = 0;
                return this;
            }
        }
    }
}
