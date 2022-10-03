package org.dom4j.swing;

import javax.swing.tree.TreeNode;
import org.dom4j.Branch;
import org.dom4j.Document;
import javax.swing.tree.DefaultTreeModel;

public class DocumentTreeModel extends DefaultTreeModel
{
    protected Document document;
    
    public DocumentTreeModel(final Document document) {
        super(new BranchTreeNode(document));
        this.document = document;
    }
    
    public Document getDocument() {
        return this.document;
    }
    
    public void setDocument(final Document document) {
        this.document = document;
        this.setRoot(new BranchTreeNode(document));
    }
}
