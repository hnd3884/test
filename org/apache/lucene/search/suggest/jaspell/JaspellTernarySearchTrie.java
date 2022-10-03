package org.apache.lucene.search.suggest.jaspell;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import java.util.Vector;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStream;
import org.apache.lucene.util.IOUtils;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import org.apache.lucene.util.Accountable;

@Deprecated
public class JaspellTernarySearchTrie implements Accountable
{
    private int defaultNumReturnValues;
    private int matchAlmostDiff;
    private TSTNode rootNode;
    private final Locale locale;
    
    private static int compareCharsAlphabetically(final char cCompare2, final char cRef) {
        return Character.toLowerCase(cCompare2) - Character.toLowerCase(cRef);
    }
    
    public JaspellTernarySearchTrie() {
        this(Locale.ROOT);
    }
    
    public JaspellTernarySearchTrie(final Locale locale) {
        this.defaultNumReturnValues = -1;
        this.locale = locale;
    }
    
    void setRoot(final TSTNode newRoot) {
        this.rootNode = newRoot;
    }
    
    TSTNode getRoot() {
        return this.rootNode;
    }
    
    public JaspellTernarySearchTrie(final Path file) throws IOException {
        this(file, false);
    }
    
    public JaspellTernarySearchTrie(final Path file, final boolean compression) throws IOException {
        this();
        BufferedReader in;
        if (compression) {
            in = new BufferedReader(IOUtils.getDecodingReader((InputStream)new GZIPInputStream(Files.newInputStream(file, new OpenOption[0])), StandardCharsets.UTF_8));
        }
        else {
            in = Files.newBufferedReader(file, StandardCharsets.UTF_8);
        }
        final Float one = new Float(1.0f);
        String word;
        while ((word = in.readLine()) != null) {
            final int pos = word.indexOf("\t");
            Float occur = one;
            if (pos != -1) {
                occur = Float.parseFloat(word.substring(pos + 1).trim());
                word = word.substring(0, pos);
            }
            final String key = word.toLowerCase(this.locale);
            if (this.rootNode == null) {
                this.rootNode = new TSTNode(key.charAt(0), null);
            }
            TSTNode node = null;
            if (key.length() > 0 && this.rootNode != null) {
                TSTNode currentNode = this.rootNode;
                int charIndex = 0;
                while (true) {
                    while (currentNode != null) {
                        final int charComp = compareCharsAlphabetically(key.charAt(charIndex), currentNode.splitchar);
                        if (charComp == 0) {
                            if (++charIndex == key.length()) {
                                node = currentNode;
                                Float occur2 = null;
                                if (node != null) {
                                    occur2 = (Float)node.data;
                                }
                                if (occur2 != null) {
                                    occur += occur2;
                                }
                                currentNode = this.getOrCreateNode(word.trim().toLowerCase(this.locale));
                                currentNode.data = occur;
                                continue Label_0339;
                            }
                            currentNode = currentNode.relatives[2];
                        }
                        else if (charComp < 0) {
                            currentNode = currentNode.relatives[1];
                        }
                        else {
                            currentNode = currentNode.relatives[3];
                        }
                    }
                    continue;
                }
            }
            continue;
            Label_0339:;
        }
        in.close();
    }
    
    private void deleteNode(TSTNode nodeToDelete) {
        if (nodeToDelete == null) {
            return;
        }
        nodeToDelete.data = null;
        while (nodeToDelete != null) {
            nodeToDelete = this.deleteNodeRecursion(nodeToDelete);
        }
    }
    
    private TSTNode deleteNodeRecursion(final TSTNode currentNode) {
        if (currentNode == null) {
            return null;
        }
        if (currentNode.relatives[2] != null || currentNode.data != null) {
            return null;
        }
        final TSTNode currentParent = currentNode.relatives[0];
        final boolean lokidNull = currentNode.relatives[1] == null;
        final boolean hikidNull = currentNode.relatives[3] == null;
        int childType;
        if (currentParent.relatives[1] == currentNode) {
            childType = 1;
        }
        else if (currentParent.relatives[2] == currentNode) {
            childType = 2;
        }
        else {
            if (currentParent.relatives[3] != currentNode) {
                return this.rootNode = null;
            }
            childType = 3;
        }
        if (lokidNull && hikidNull) {
            currentParent.relatives[childType] = null;
            return currentParent;
        }
        if (lokidNull) {
            currentParent.relatives[childType] = currentNode.relatives[3];
            return currentNode.relatives[3].relatives[0] = currentParent;
        }
        if (hikidNull) {
            currentParent.relatives[childType] = currentNode.relatives[1];
            return currentNode.relatives[1].relatives[0] = currentParent;
        }
        int deltaHi = currentNode.relatives[3].splitchar - currentNode.splitchar;
        int deltaLo = currentNode.splitchar - currentNode.relatives[1].splitchar;
        if (deltaHi == deltaLo) {
            if (Math.random() < 0.5) {
                ++deltaHi;
            }
            else {
                ++deltaLo;
            }
        }
        int movingKid;
        TSTNode targetNode;
        if (deltaHi > deltaLo) {
            movingKid = 3;
            targetNode = currentNode.relatives[1];
        }
        else {
            movingKid = 1;
            targetNode = currentNode.relatives[3];
        }
        while (targetNode.relatives[movingKid] != null) {
            targetNode = targetNode.relatives[movingKid];
        }
        targetNode.relatives[movingKid] = currentNode.relatives[movingKid];
        currentParent.relatives[childType] = targetNode;
        targetNode.relatives[0] = currentParent;
        if (!lokidNull) {
            currentNode.relatives[1] = null;
        }
        if (!hikidNull) {
            currentNode.relatives[3] = null;
        }
        return currentParent;
    }
    
    public Object get(final CharSequence key) {
        final TSTNode node = this.getNode(key);
        if (node == null) {
            return null;
        }
        return node.data;
    }
    
    public Float getAndIncrement(final String key) {
        final String key2 = key.trim().toLowerCase(this.locale);
        final TSTNode node = this.getNode(key2);
        if (node == null) {
            return null;
        }
        Float aux = (Float)node.data;
        if (aux == null) {
            aux = new Float(1.0f);
        }
        else {
            aux = new Float((float)(aux.intValue() + 1));
        }
        this.put(key2, aux);
        return aux;
    }
    
    protected String getKey(final TSTNode node) {
        final StringBuilder getKeyBuffer = new StringBuilder();
        getKeyBuffer.setLength(0);
        getKeyBuffer.append("" + node.splitchar);
        TSTNode currentNode = node.relatives[0];
        TSTNode lastNode = node;
        while (currentNode != null) {
            if (currentNode.relatives[2] == lastNode) {
                getKeyBuffer.append("" + currentNode.splitchar);
            }
            lastNode = currentNode;
            currentNode = currentNode.relatives[0];
        }
        getKeyBuffer.reverse();
        return getKeyBuffer.toString();
    }
    
    public TSTNode getNode(final CharSequence key) {
        return this.getNode(key, this.rootNode);
    }
    
    protected TSTNode getNode(final CharSequence key, final TSTNode startNode) {
        if (key == null || startNode == null || key.length() == 0) {
            return null;
        }
        TSTNode currentNode = startNode;
        int charIndex = 0;
        while (currentNode != null) {
            final int charComp = compareCharsAlphabetically(key.charAt(charIndex), currentNode.splitchar);
            if (charComp == 0) {
                if (++charIndex == key.length()) {
                    return currentNode;
                }
                currentNode = currentNode.relatives[2];
            }
            else if (charComp < 0) {
                currentNode = currentNode.relatives[1];
            }
            else {
                currentNode = currentNode.relatives[3];
            }
        }
        return null;
    }
    
    protected TSTNode getOrCreateNode(final CharSequence key) throws NullPointerException, IllegalArgumentException {
        if (key == null) {
            throw new NullPointerException("attempt to get or create node with null key");
        }
        if (key.length() == 0) {
            throw new IllegalArgumentException("attempt to get or create node with key of zero length");
        }
        if (this.rootNode == null) {
            this.rootNode = new TSTNode(key.charAt(0), null);
        }
        TSTNode currentNode = this.rootNode;
        int charIndex = 0;
        while (true) {
            final int charComp = compareCharsAlphabetically(key.charAt(charIndex), currentNode.splitchar);
            if (charComp == 0) {
                if (++charIndex == key.length()) {
                    break;
                }
                if (currentNode.relatives[2] == null) {
                    currentNode.relatives[2] = new TSTNode(key.charAt(charIndex), currentNode);
                }
                currentNode = currentNode.relatives[2];
            }
            else if (charComp < 0) {
                if (currentNode.relatives[1] == null) {
                    currentNode.relatives[1] = new TSTNode(key.charAt(charIndex), currentNode);
                }
                currentNode = currentNode.relatives[1];
            }
            else {
                if (currentNode.relatives[3] == null) {
                    currentNode.relatives[3] = new TSTNode(key.charAt(charIndex), currentNode);
                }
                currentNode = currentNode.relatives[3];
            }
        }
        return currentNode;
    }
    
    public List<String> matchAlmost(final String key) {
        return this.matchAlmost(key, this.defaultNumReturnValues);
    }
    
    public List<String> matchAlmost(final CharSequence key, final int numReturnValues) {
        return this.matchAlmostRecursion(this.rootNode, 0, this.matchAlmostDiff, key, (numReturnValues < 0) ? -1 : numReturnValues, new Vector<String>(), false);
    }
    
    private List<String> matchAlmostRecursion(final TSTNode currentNode, final int charIndex, final int d, final CharSequence matchAlmostKey, final int matchAlmostNumReturnValues, final List<String> matchAlmostResult2, final boolean upTo) {
        if (currentNode == null || (matchAlmostNumReturnValues != -1 && matchAlmostResult2.size() >= matchAlmostNumReturnValues) || d < 0 || charIndex >= matchAlmostKey.length()) {
            return matchAlmostResult2;
        }
        final int charComp = compareCharsAlphabetically(matchAlmostKey.charAt(charIndex), currentNode.splitchar);
        List<String> matchAlmostResult3 = matchAlmostResult2;
        if (d > 0 || charComp < 0) {
            matchAlmostResult3 = this.matchAlmostRecursion(currentNode.relatives[1], charIndex, d, matchAlmostKey, matchAlmostNumReturnValues, matchAlmostResult3, upTo);
        }
        final int nextD = (charComp == 0) ? d : (d - 1);
        final boolean cond = upTo ? (nextD >= 0) : (nextD == 0);
        if (matchAlmostKey.length() == charIndex + 1 && cond && currentNode.data != null) {
            matchAlmostResult3.add(this.getKey(currentNode));
        }
        matchAlmostResult3 = this.matchAlmostRecursion(currentNode.relatives[2], charIndex + 1, nextD, matchAlmostKey, matchAlmostNumReturnValues, matchAlmostResult3, upTo);
        if (d > 0 || charComp > 0) {
            matchAlmostResult3 = this.matchAlmostRecursion(currentNode.relatives[3], charIndex, d, matchAlmostKey, matchAlmostNumReturnValues, matchAlmostResult3, upTo);
        }
        return matchAlmostResult3;
    }
    
    public List<String> matchPrefix(final String prefix) {
        return this.matchPrefix(prefix, this.defaultNumReturnValues);
    }
    
    public List<String> matchPrefix(final CharSequence prefix, final int numReturnValues) {
        final Vector<String> sortKeysResult = new Vector<String>();
        final TSTNode startNode = this.getNode(prefix);
        if (startNode == null) {
            return sortKeysResult;
        }
        if (startNode.data != null) {
            sortKeysResult.addElement(this.getKey(startNode));
        }
        return this.sortKeysRecursion(startNode.relatives[2], (numReturnValues < 0) ? -1 : numReturnValues, sortKeysResult);
    }
    
    public int numDataNodes() {
        return this.numDataNodes(this.rootNode);
    }
    
    protected int numDataNodes(final TSTNode startingNode) {
        return this.recursiveNodeCalculator(startingNode, true, 0);
    }
    
    public int numNodes() {
        return this.numNodes(this.rootNode);
    }
    
    protected int numNodes(final TSTNode startingNode) {
        return this.recursiveNodeCalculator(startingNode, false, 0);
    }
    
    public void put(final CharSequence key, final Object value) {
        this.getOrCreateNode(key).data = value;
    }
    
    private int recursiveNodeCalculator(final TSTNode currentNode, final boolean checkData, final int numNodes2) {
        if (currentNode == null) {
            return numNodes2;
        }
        int numNodes3 = this.recursiveNodeCalculator(currentNode.relatives[1], checkData, numNodes2);
        numNodes3 = this.recursiveNodeCalculator(currentNode.relatives[2], checkData, numNodes3);
        numNodes3 = this.recursiveNodeCalculator(currentNode.relatives[3], checkData, numNodes3);
        if (checkData) {
            if (currentNode.data != null) {
                ++numNodes3;
            }
        }
        else {
            ++numNodes3;
        }
        return numNodes3;
    }
    
    public void remove(final String key) {
        this.deleteNode(this.getNode(key.trim().toLowerCase(this.locale)));
    }
    
    public void setMatchAlmostDiff(final int diff) {
        if (diff < 0) {
            this.matchAlmostDiff = 0;
        }
        else if (diff > 3) {
            this.matchAlmostDiff = 3;
        }
        else {
            this.matchAlmostDiff = diff;
        }
    }
    
    public void setNumReturnValues(final int num) {
        this.defaultNumReturnValues = ((num < 0) ? -1 : num);
    }
    
    protected List<String> sortKeys(final TSTNode startNode, final int numReturnValues) {
        return this.sortKeysRecursion(startNode, (numReturnValues < 0) ? -1 : numReturnValues, new Vector<String>());
    }
    
    private List<String> sortKeysRecursion(final TSTNode currentNode, final int sortKeysNumReturnValues, final List<String> sortKeysResult2) {
        if (currentNode == null) {
            return sortKeysResult2;
        }
        List<String> sortKeysResult3 = this.sortKeysRecursion(currentNode.relatives[1], sortKeysNumReturnValues, sortKeysResult2);
        if (sortKeysNumReturnValues != -1 && sortKeysResult3.size() >= sortKeysNumReturnValues) {
            return sortKeysResult3;
        }
        if (currentNode.data != null) {
            sortKeysResult3.add(this.getKey(currentNode));
        }
        sortKeysResult3 = this.sortKeysRecursion(currentNode.relatives[2], sortKeysNumReturnValues, sortKeysResult3);
        return this.sortKeysRecursion(currentNode.relatives[3], sortKeysNumReturnValues, sortKeysResult3);
    }
    
    public long ramBytesUsed() {
        long mem = RamUsageEstimator.shallowSizeOf((Object)this);
        final TSTNode root = this.getRoot();
        if (root != null) {
            mem += root.ramBytesUsed();
        }
        return mem;
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    protected final class TSTNode implements Accountable
    {
        protected static final int PARENT = 0;
        protected static final int LOKID = 1;
        protected static final int EQKID = 2;
        protected static final int HIKID = 3;
        protected Object data;
        protected final TSTNode[] relatives;
        protected char splitchar;
        
        protected TSTNode(final char splitchar, final TSTNode parent) {
            this.relatives = new TSTNode[4];
            this.splitchar = splitchar;
            this.relatives[0] = parent;
        }
        
        public long ramBytesUsed() {
            long mem = RamUsageEstimator.shallowSizeOf((Object)this) + RamUsageEstimator.shallowSizeOf((Object[])this.relatives);
            for (int i = 1; i < 4; ++i) {
                final TSTNode node = this.relatives[i];
                if (node != null) {
                    mem += node.ramBytesUsed();
                }
            }
            return mem;
        }
        
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
    }
}
