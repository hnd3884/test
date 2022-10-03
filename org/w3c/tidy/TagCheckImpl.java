package org.w3c.tidy;

public final class TagCheckImpl
{
    public static final TagCheck HTML;
    public static final TagCheck SCRIPT;
    public static final TagCheck TABLE;
    public static final TagCheck CAPTION;
    public static final TagCheck IMG;
    public static final TagCheck AREA;
    public static final TagCheck ANCHOR;
    public static final TagCheck MAP;
    public static final TagCheck STYLE;
    public static final TagCheck TABLECELL;
    public static final TagCheck LINK;
    public static final TagCheck HR;
    public static final TagCheck FORM;
    public static final TagCheck META;
    
    private TagCheckImpl() {
    }
    
    static {
        HTML = new CheckHTML();
        SCRIPT = new CheckSCRIPT();
        TABLE = new CheckTABLE();
        CAPTION = new CheckCaption();
        IMG = new CheckIMG();
        AREA = new CheckAREA();
        ANCHOR = new CheckAnchor();
        MAP = new CheckMap();
        STYLE = new CheckSTYLE();
        TABLECELL = new CheckTableCell();
        LINK = new CheckLINK();
        HR = new CheckHR();
        FORM = new CheckForm();
        META = new CheckMeta();
    }
    
    public static class CheckAREA implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            boolean b = false;
            boolean b2 = false;
            for (AttVal attVal = node.attributes; attVal != null; attVal = attVal.next) {
                final Attribute checkAttribute = attVal.checkAttribute(lexer, node);
                if (checkAttribute == AttributeTable.attrAlt) {
                    b = true;
                }
                else if (checkAttribute == AttributeTable.attrHref) {
                    b2 = true;
                }
            }
            if (!b) {
                lexer.badAccess |= 0x2;
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "alt", ""), (short)49);
            }
            if (!b2) {
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "href", ""), (short)49);
            }
        }
    }
    
    public static class CheckAnchor implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            node.checkAttributes(lexer);
            lexer.fixId(node);
        }
    }
    
    public static class CheckCaption implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            String value = null;
            node.checkAttributes(lexer);
            AttVal attVal;
            for (attVal = node.attributes; attVal != null; attVal = attVal.next) {
                if ("align".equalsIgnoreCase(attVal.attribute)) {
                    value = attVal.value;
                    break;
                }
            }
            if (value != null) {
                if ("left".equalsIgnoreCase(value) || "right".equalsIgnoreCase(value)) {
                    lexer.constrainVersion(8);
                }
                else if ("top".equalsIgnoreCase(value) || "bottom".equalsIgnoreCase(value)) {
                    lexer.constrainVersion(-4);
                }
                else {
                    lexer.report.attrError(lexer, node, attVal, (short)51);
                }
            }
        }
    }
    
    public static class CheckForm implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            final AttVal attrByName = node.getAttrByName("action");
            node.checkAttributes(lexer);
            if (attrByName == null) {
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "action", ""), (short)49);
            }
        }
    }
    
    public static class CheckHR implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            final AttVal attrByName = node.getAttrByName("src");
            node.checkAttributes(lexer);
            if (attrByName != null) {
                lexer.report.attrError(lexer, node, attrByName, (short)54);
            }
        }
    }
    
    public static class CheckHTML implements TagCheck
    {
        private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
        
        public void check(final Lexer lexer, final Node node) {
            final AttVal attrByName = node.getAttrByName("xmlns");
            if (attrByName != null && "http://www.w3.org/1999/xhtml".equals(attrByName.value)) {
                lexer.isvoyager = true;
                if (!lexer.configuration.htmlOut) {
                    lexer.configuration.xHTML = true;
                }
                lexer.configuration.xmlOut = true;
                lexer.configuration.upperCaseTags = false;
                lexer.configuration.upperCaseAttrs = false;
            }
            for (AttVal attVal = node.attributes; attVal != null; attVal = attVal.next) {
                attVal.checkAttribute(lexer, node);
            }
        }
    }
    
    public static class CheckIMG implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            boolean b = false;
            boolean b2 = false;
            boolean b3 = false;
            boolean b4 = false;
            boolean b5 = false;
            for (AttVal attVal = node.attributes; attVal != null; attVal = attVal.next) {
                final Attribute checkAttribute = attVal.checkAttribute(lexer, node);
                if (checkAttribute == AttributeTable.attrAlt) {
                    b = true;
                }
                else if (checkAttribute == AttributeTable.attrSrc) {
                    b2 = true;
                }
                else if (checkAttribute == AttributeTable.attrUsemap) {
                    b3 = true;
                }
                else if (checkAttribute == AttributeTable.attrIsmap) {
                    b4 = true;
                }
                else if (checkAttribute == AttributeTable.attrDatafld) {
                    b5 = true;
                }
                else if (checkAttribute == AttributeTable.attrWidth || checkAttribute == AttributeTable.attrHeight) {
                    lexer.constrainVersion(-2);
                }
            }
            if (!b) {
                lexer.badAccess |= 0x1;
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "alt", ""), (short)49);
                if (lexer.configuration.altText != null) {
                    node.addAttribute("alt", lexer.configuration.altText);
                }
            }
            if (!b2 && !b5) {
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "src", ""), (short)49);
            }
            if (b4 && !b3) {
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "ismap", ""), (short)56);
            }
        }
    }
    
    public static class CheckLINK implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            final AttVal attrByName = node.getAttrByName("rel");
            node.checkAttributes(lexer);
            if (attrByName != null && attrByName.value != null && attrByName.value.equals("stylesheet") && node.getAttrByName("type") == null) {
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "type", ""), (short)49);
                node.addAttribute("type", "text/css");
            }
        }
    }
    
    public static class CheckMap implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            node.checkAttributes(lexer);
            lexer.fixId(node);
        }
    }
    
    public static class CheckMeta implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            final AttVal attrByName = node.getAttrByName("content");
            node.checkAttributes(lexer);
            if (attrByName == null) {
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "content", ""), (short)49);
            }
        }
    }
    
    public static class CheckSCRIPT implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            node.checkAttributes(lexer);
            final AttVal attrByName = node.getAttrByName("language");
            if (node.getAttrByName("type") == null) {
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "type", ""), (short)49);
                if (attrByName != null) {
                    final String value = attrByName.value;
                    if ("javascript".equalsIgnoreCase(value) || "jscript".equalsIgnoreCase(value)) {
                        node.addAttribute("type", "text/javascript");
                    }
                    else if ("vbscript".equalsIgnoreCase(value)) {
                        node.addAttribute("type", "text/vbscript");
                    }
                }
                else {
                    node.addAttribute("type", "text/javascript");
                }
            }
        }
    }
    
    public static class CheckSTYLE implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            final AttVal attrByName = node.getAttrByName("type");
            node.checkAttributes(lexer);
            if (attrByName == null) {
                lexer.report.attrError(lexer, node, new AttVal(null, null, 34, "type", ""), (short)49);
                node.addAttribute("type", "text/css");
            }
        }
    }
    
    public static class CheckTABLE implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            boolean b = false;
            for (AttVal attVal = node.attributes; attVal != null; attVal = attVal.next) {
                if (attVal.checkAttribute(lexer, node) == AttributeTable.attrSummary) {
                    b = true;
                }
            }
            if (!b && lexer.doctype != 1 && lexer.doctype != 2) {
                lexer.badAccess |= 0x4;
            }
            if (lexer.configuration.xmlOut) {
                final AttVal attrByName = node.getAttrByName("border");
                if (attrByName != null && attrByName.value == null) {
                    attrByName.value = "1";
                }
            }
            final AttVal attrByName2;
            if ((attrByName2 = node.getAttrByName("height")) != null) {
                lexer.report.attrError(lexer, node, attrByName2, (short)53);
                lexer.versions &= 0x1C0;
            }
        }
    }
    
    public static class CheckTableCell implements TagCheck
    {
        public void check(final Lexer lexer, final Node node) {
            node.checkAttributes(lexer);
            if (node.getAttrByName("width") != null || node.getAttrByName("height") != null) {
                lexer.constrainVersion(-5);
            }
        }
    }
}
