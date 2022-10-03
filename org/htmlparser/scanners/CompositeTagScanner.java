package org.htmlparser.scanners;

import org.htmlparser.lexer.Page;
import org.htmlparser.util.ParserException;
import org.htmlparser.Attribute;
import java.util.Vector;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.Tag;

public class CompositeTagScanner extends TagScanner
{
    private static final boolean mUseJVMStack = false;
    private static final boolean mLeaveEnds = false;
    
    public Tag scan(final Tag tag, final Lexer lexer, final NodeList stack) throws ParserException {
        Tag ret = tag;
        if (ret.isEmptyXmlTag()) {
            ret.setEndTag(ret);
        }
        else {
            Node node;
            do {
                node = lexer.nextNode(false);
                if (null != node) {
                    if (node instanceof Tag) {
                        final Tag next = (Tag)node;
                        final String name = next.getTagName();
                        if (next.isEndTag() && name.equals(ret.getTagName())) {
                            ret.setEndTag(next);
                            node = null;
                        }
                        else if (this.isTagToBeEndedFor(ret, next)) {
                            lexer.setPosition(next.getStartPosition());
                            node = null;
                        }
                        else if (!next.isEndTag()) {
                            final Scanner scanner = next.getThisScanner();
                            if (null != scanner) {
                                if (scanner == this) {
                                    if (next.isEmptyXmlTag()) {
                                        next.setEndTag(next);
                                        this.finishTag(next, lexer);
                                        this.addChild(ret, next);
                                    }
                                    else {
                                        stack.add(ret);
                                        ret = next;
                                    }
                                }
                                else {
                                    node = scanner.scan(next, lexer, stack);
                                    this.addChild(ret, node);
                                }
                            }
                            else {
                                this.addChild(ret, next);
                            }
                        }
                        else {
                            final Vector attributes = new Vector();
                            attributes.addElement(new Attribute(name, null));
                            final Tag opener = lexer.getNodeFactory().createTagNode(lexer.getPage(), next.getStartPosition(), next.getEndPosition(), attributes);
                            final Scanner scanner = opener.getThisScanner();
                            if (null != scanner && scanner == this) {
                                int index = -1;
                                for (int i = stack.size() - 1; -1 == index && i >= 0; --i) {
                                    final Tag boffo = (Tag)stack.elementAt(i);
                                    if (name.equals(boffo.getTagName())) {
                                        index = i;
                                    }
                                    else if (this.isTagToBeEndedFor(boffo, next)) {
                                        index = i;
                                    }
                                }
                                if (-1 != index) {
                                    this.finishTag(ret, lexer);
                                    this.addChild((Tag)stack.elementAt(stack.size() - 1), ret);
                                    for (int i = stack.size() - 1; i > index; --i) {
                                        final Tag fred = (Tag)stack.remove(i);
                                        this.finishTag(fred, lexer);
                                        this.addChild((Tag)stack.elementAt(i - 1), fred);
                                    }
                                    ret = (Tag)stack.remove(index);
                                    node = null;
                                }
                                else {
                                    this.addChild(ret, next);
                                }
                            }
                            else {
                                this.addChild(ret, next);
                            }
                        }
                    }
                    else {
                        this.addChild(ret, node);
                        node.doSemanticAction();
                    }
                }
                if (null == node) {
                    final int depth = stack.size();
                    if (0 == depth) {
                        continue;
                    }
                    node = stack.elementAt(depth - 1);
                    if (node instanceof Tag) {
                        final Tag precursor = (Tag)node;
                        final Scanner scanner = precursor.getThisScanner();
                        if (scanner == this) {
                            stack.remove(depth - 1);
                            this.finishTag(ret, lexer);
                            this.addChild(precursor, ret);
                            ret = precursor;
                        }
                        else {
                            node = null;
                        }
                    }
                    else {
                        node = null;
                    }
                }
            } while (null != node);
        }
        this.finishTag(ret, lexer);
        return ret;
    }
    
    protected void addChild(final Tag parent, final Node child) {
        if (null == parent.getChildren()) {
            parent.setChildren(new NodeList());
        }
        child.setParent(parent);
        parent.getChildren().add(child);
    }
    
    protected void finishTag(final Tag tag, final Lexer lexer) throws ParserException {
        if (null == tag.getEndTag()) {
            tag.setEndTag(this.createVirtualEndTag(tag, lexer, lexer.getPage(), lexer.getCursor().getPosition()));
        }
        tag.getEndTag().setParent(tag);
        tag.doSemanticAction();
    }
    
    protected Tag createVirtualEndTag(final Tag tag, final Lexer lexer, final Page page, final int position) throws ParserException {
        final String name = "/" + tag.getRawTagName();
        final Vector attributes = new Vector();
        attributes.addElement(new Attribute(name, null));
        final Tag ret = lexer.getNodeFactory().createTagNode(page, position, position, attributes);
        return ret;
    }
    
    public final boolean isTagToBeEndedFor(final Tag current, final Tag tag) {
        boolean ret = false;
        final String name = tag.getTagName();
        String[] ends;
        if (tag.isEndTag()) {
            ends = current.getEndTagEnders();
        }
        else {
            ends = current.getEnders();
        }
        for (int i = 0; i < ends.length; ++i) {
            if (name.equalsIgnoreCase(ends[i])) {
                ret = true;
                break;
            }
        }
        return ret;
    }
}
