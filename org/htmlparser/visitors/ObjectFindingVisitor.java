package org.htmlparser.visitors;

import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

public class ObjectFindingVisitor extends NodeVisitor
{
    private Class classTypeToFind;
    private NodeList tags;
    
    public ObjectFindingVisitor(final Class classTypeToFind) {
        this(classTypeToFind, true);
    }
    
    public ObjectFindingVisitor(final Class classTypeToFind, final boolean recurse) {
        super(recurse, true);
        this.classTypeToFind = classTypeToFind;
        this.tags = new NodeList();
    }
    
    public int getCount() {
        return this.tags.size();
    }
    
    public void visitTag(final Tag tag) {
        if (tag.getClass().equals(this.classTypeToFind)) {
            this.tags.add(tag);
        }
    }
    
    public Node[] getTags() {
        return this.tags.toNodeArray();
    }
}
