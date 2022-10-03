package org.htmlparser.scanners;

import org.htmlparser.util.ParserException;
import org.htmlparser.Node;
import java.util.Vector;
import org.htmlparser.Attribute;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.Tag;

public class ScriptScanner extends CompositeTagScanner
{
    public static boolean STRICT;
    
    public Tag scan(final Tag tag, final Lexer lexer, final NodeList stack) throws ParserException {
        if (tag instanceof ScriptTag) {
            final String language = ((ScriptTag)tag).getLanguage();
            if (null != language && (language.equalsIgnoreCase("JScript.Encode") || language.equalsIgnoreCase("VBScript.Encode"))) {
                final String code = ScriptDecoder.Decode(lexer.getPage(), lexer.getCursor());
                ((ScriptTag)tag).setScriptCode(code);
            }
        }
        final Node content = lexer.parseCDATA(!ScriptScanner.STRICT);
        final int position = lexer.getPosition();
        Node node = lexer.nextNode(false);
        if (null != node && (!(node instanceof Tag) || !((Tag)node).isEndTag() || !((Tag)node).getTagName().equals(tag.getIds()[0]))) {
            lexer.setPosition(position);
            node = null;
        }
        if (null == node) {
            final Attribute attribute = new Attribute("/script", null);
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
    
    static {
        ScriptScanner.STRICT = true;
    }
}
