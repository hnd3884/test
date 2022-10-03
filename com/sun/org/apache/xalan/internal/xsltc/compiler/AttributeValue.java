package com.sun.org.apache.xalan.internal.xsltc.compiler;

abstract class AttributeValue extends Expression
{
    public static final AttributeValue create(final SyntaxTreeNode parent, final String text, final Parser parser) {
        AttributeValue result;
        if (text.indexOf(123) != -1) {
            result = new AttributeValueTemplate(text, parser, parent);
        }
        else if (text.indexOf(125) != -1) {
            result = new AttributeValueTemplate(text, parser, parent);
        }
        else {
            result = new SimpleAttributeValue(text);
            result.setParser(parser);
            result.setParent(parent);
        }
        return result;
    }
}
