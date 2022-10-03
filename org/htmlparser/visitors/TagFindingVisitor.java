package org.htmlparser.visitors;

import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

public class TagFindingVisitor extends NodeVisitor
{
    private String[] tagsToBeFound;
    private int[] count;
    private int[] endTagCount;
    private NodeList[] tags;
    private NodeList[] endTags;
    private boolean endTagCheck;
    
    public TagFindingVisitor(final String[] tagsToBeFound) {
        this(tagsToBeFound, false);
    }
    
    public TagFindingVisitor(final String[] tagsToBeFound, final boolean endTagCheck) {
        this.tagsToBeFound = tagsToBeFound;
        this.tags = new NodeList[tagsToBeFound.length];
        if (endTagCheck) {
            this.endTags = new NodeList[tagsToBeFound.length];
            this.endTagCount = new int[tagsToBeFound.length];
        }
        for (int i = 0; i < tagsToBeFound.length; ++i) {
            this.tags[i] = new NodeList();
            if (endTagCheck) {
                this.endTags[i] = new NodeList();
            }
        }
        this.count = new int[tagsToBeFound.length];
        this.endTagCheck = endTagCheck;
    }
    
    public int getTagCount(final int index) {
        return this.count[index];
    }
    
    public void visitTag(final Tag tag) {
        for (int i = 0; i < this.tagsToBeFound.length; ++i) {
            if (tag.getTagName().equalsIgnoreCase(this.tagsToBeFound[i])) {
                final int[] count = this.count;
                final int n = i;
                ++count[n];
                this.tags[i].add(tag);
            }
        }
    }
    
    public void visitEndTag(final Tag tag) {
        if (!this.endTagCheck) {
            return;
        }
        for (int i = 0; i < this.tagsToBeFound.length; ++i) {
            if (tag.getTagName().equalsIgnoreCase(this.tagsToBeFound[i])) {
                final int[] endTagCount = this.endTagCount;
                final int n = i;
                ++endTagCount[n];
                this.endTags[i].add(tag);
            }
        }
    }
    
    public Node[] getTags(final int index) {
        return this.tags[index].toNodeArray();
    }
    
    public int getEndTagCount(final int index) {
        return this.endTagCount[index];
    }
}
