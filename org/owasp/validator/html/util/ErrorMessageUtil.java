package org.owasp.validator.html.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class ErrorMessageUtil
{
    public static final String ERROR_TAG_NOT_IN_POLICY = "error.tag.notfound";
    public static final String ERROR_TAG_DISALLOWED = "error.tag.removed";
    public static final String ERROR_TAG_FILTERED = "error.tag.filtered";
    public static final String ERROR_TAG_ENCODED = "error.tag.encoded";
    public static final String ERROR_TAG_EMPTY = "error.tag.empty";
    public static final String ERROR_CDATA_FOUND = "error.cdata.found";
    public static final String ERROR_PI_FOUND = "error.pi.found";
    public static final String ERROR_ATTRIBUTE_CAUSE_FILTER = "error.attribute.invalid.filtered";
    public static final String ERROR_ATTRIBUTE_CAUSE_ENCODE = "error.attribute.invalid.encoded";
    public static final String ERROR_ATTRIBUTE_INVALID_FILTERED = "error.attribute.invalid.filtered";
    public static final String ERROR_ATTRIBUTE_INVALID_REMOVED = "error.attribute.invalid.removed";
    public static final String ERROR_ATTRIBUTE_NOT_IN_POLICY = "error.attribute.notfound";
    public static final String ERROR_ATTRIBUTE_INVALID = "error.attribute.invalid";
    public static final String ERROR_COMMENT_REMOVED = "error.comment.removed";
    public static final String ERROR_INPUT_SIZE = "error.size.toolarge";
    public static final String ERROR_CSS_ATTRIBUTE_MALFORMED = "error.css.attribute.malformed";
    public static final String ERROR_CSS_TAG_MALFORMED = "error.css.tag.malformed";
    public static final String ERROR_STYLESHEET_NOT_ALLOWED = "error.css.disallowed";
    public static final String ERROR_CSS_IMPORT_DISABLED = "error.css.import.disabled";
    public static final String ERROR_CSS_IMPORT_EXCEEDED = "error.css.import.exceeded";
    public static final String ERROR_CSS_IMPORT_FAILURE = "error.css.import.failure";
    public static final String ERROR_CSS_IMPORT_INPUT_SIZE = "error.css.import.toolarge";
    public static final String ERROR_CSS_IMPORT_URL_INVALID = "error.css.import.url.invalid";
    public static final String ERROR_STYLESHEET_RELATIVE = "error.css.stylesheet.relative";
    public static final String ERROR_CSS_TAG_RELATIVE = "error.css.tag.relative";
    public static final String ERROR_STYLESHEET_RULE_NOTFOUND = "error.css.stylesheet.rule.notfound";
    public static final String ERROR_CSS_TAG_RULE_NOTFOUND = "error.css.tag.rule.notfound";
    public static final String ERROR_STYLESHEET_SELECTOR_NOTFOUND = "error.css.stylesheet.selector.notfound";
    public static final String ERROR_CSS_TAG_SELECTOR_NOTFOUND = "error.css.tag.selector.notfound";
    public static final String ERROR_STYLESHEET_SELECTOR_DISALLOWED = "error.css.stylesheet.selector.disallowed";
    public static final String ERROR_CSS_TAG_SELECTOR_DISALLOWED = "error.css.tag.selector.disallowed";
    public static final String ERROR_STYLESHEET_PROPERTY_INVALID = "error.css.stylesheet.property.invalid";
    public static final String ERROR_CSS_TAG_PROPERTY_INVALID = "error.css.tag.property.invalid";
    public static final String ERROR_CSS_CHARSET_ENCODING_INVALID = "error.css.charset.encoding.invalid";
    public static final String ERROR_CSS_MEDIATYPE_INVALID = "error.css.media.type.invalid";
    public static final String ERROR_CSS_AT_MEDIA_SELECTOR_INVALID = "error.css.atmedia.selector.invalid";
    public static final String ERROR_CSS_PAGE_SELECTOR_INVALID = "error.css.page.selector.invalid";
    public static final String ERROR_CSS_UNKNOWNRULE = "error.css.unknown.rule";
    public static final String ERROR_CSS_KEYFRAME_PROPERTY_INVALID = "error.css.keyframe.property.invalid";
    
    private ErrorMessageUtil() {
    }
    
    public static String getMessage(final ResourceBundle messages, final String msgKey, final Object[] arguments) {
        return MessageFormat.format(messages.getString(msgKey), arguments);
    }
}
