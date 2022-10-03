package org.apache.lucene.search.suggest.tst;

import java.util.Stack;
import java.util.ArrayList;

public class TSTAutocomplete
{
    TSTAutocomplete() {
    }
    
    public void balancedTree(final Object[] tokens, final Object[] vals, final int lo, final int hi, TernaryTreeNode root) {
        if (lo > hi) {
            return;
        }
        final int mid = (lo + hi) / 2;
        root = this.insert(root, (CharSequence)tokens[mid], vals[mid], 0);
        this.balancedTree(tokens, vals, lo, mid - 1, root);
        this.balancedTree(tokens, vals, mid + 1, hi, root);
    }
    
    public TernaryTreeNode insert(TernaryTreeNode currentNode, final CharSequence s, final Object val, final int x) {
        if (s == null || s.length() <= x) {
            return currentNode;
        }
        if (currentNode == null) {
            final TernaryTreeNode newNode = new TernaryTreeNode();
            newNode.splitchar = s.charAt(x);
            currentNode = newNode;
            if (x >= s.length() - 1) {
                currentNode.token = s.toString();
                currentNode.val = val;
                return currentNode;
            }
            currentNode.eqKid = this.insert(currentNode.eqKid, s, val, x + 1);
        }
        else if (currentNode.splitchar > s.charAt(x)) {
            currentNode.loKid = this.insert(currentNode.loKid, s, val, x);
        }
        else if (currentNode.splitchar == s.charAt(x)) {
            if (x >= s.length() - 1) {
                currentNode.token = s.toString();
                currentNode.val = val;
                return currentNode;
            }
            currentNode.eqKid = this.insert(currentNode.eqKid, s, val, x + 1);
        }
        else {
            currentNode.hiKid = this.insert(currentNode.hiKid, s, val, x);
        }
        return currentNode;
    }
    
    public ArrayList<TernaryTreeNode> prefixCompletion(final TernaryTreeNode root, final CharSequence s, int x) {
        TernaryTreeNode p = root;
        final ArrayList<TernaryTreeNode> suggest = new ArrayList<TernaryTreeNode>();
        while (p != null) {
            if (s.charAt(x) < p.splitchar) {
                p = p.loKid;
            }
            else if (s.charAt(x) == p.splitchar) {
                if (x == s.length() - 1) {
                    break;
                }
                ++x;
                p = p.eqKid;
            }
            else {
                p = p.hiKid;
            }
        }
        if (p == null) {
            return suggest;
        }
        if (p.eqKid == null && p.token == null) {
            return suggest;
        }
        if (p.eqKid == null && p.token != null) {
            suggest.add(p);
            return suggest;
        }
        if (p.token != null) {
            suggest.add(p);
        }
        p = p.eqKid;
        final Stack<TernaryTreeNode> st = new Stack<TernaryTreeNode>();
        st.push(p);
        while (!st.empty()) {
            final TernaryTreeNode top = st.peek();
            st.pop();
            if (top.token != null) {
                suggest.add(top);
            }
            if (top.eqKid != null) {
                st.push(top.eqKid);
            }
            if (top.loKid != null) {
                st.push(top.loKid);
            }
            if (top.hiKid != null) {
                st.push(top.hiKid);
            }
        }
        return suggest;
    }
}
