package org.htmlparser.scanners;

import org.htmlparser.util.ParserException;
import org.htmlparser.Node;
import java.util.Vector;
import org.htmlparser.Attribute;
import org.htmlparser.util.NodeList;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.Tag;

public class StyleScanner extends CompositeTagScanner
{
    public Tag scan(final Tag tag, final Lexer lexer, final NodeList stack) throws ParserException {
        final Node content = lexer.parseCDATA();
        final int position = lexer.getPosition();
        Node node = lexer.nextNode(false);
        if (null != node && (!(node instanceof Tag) || !((Tag)node).isEndTag() || !((Tag)node).getTagName().equals(tag.getIds()[0]))) {
            lexer.setPosition(position);
            node = null;
        }
        if (null == node) {
            final Attribute attribute = new Attribute("/style", null);
            final Vector vector = new Vector();
            vector.addElement(attribute);
            node = lexer.getNodeFactory().createTagNode(lexer.getPage(), position, position, vector);
        }
        tag.setEndTag((Tag)node);
        if (null != content) {
            tag.setChildren(new NodeList(content));
            content.setParent(tag);
        }
        node.setParent(tag);
        tag.doSemanticAction();
        return tag;
    }
}
