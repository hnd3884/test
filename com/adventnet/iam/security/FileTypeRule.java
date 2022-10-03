package com.adventnet.iam.security;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import java.util.ArrayList;
import com.zoho.security.util.TikaUtil;
import java.util.Iterator;
import java.util.logging.Level;
import org.w3c.dom.Element;
import java.util.List;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class FileTypeRule
{
    private static final Logger LOGGER;
    private static final Pattern TIKA_SUPPORTED_TYPES_PATTERN;
    private final String name;
    private List<String> fileTypes;
    private List<String> types;
    private List<FileTypeRule> fileTypeRules;
    private List<String> fileTypeNames;
    
    protected FileTypeRule(final Element fileTypeElement) throws RuntimeException {
        this.name = fileTypeElement.getAttribute("name");
        try {
            for (final Element typeElement : RuleSetParser.getChildNodesByTagName(fileTypeElement, "type")) {
                this.addFileType(typeElement.getTextContent());
            }
        }
        catch (final Exception e) {
            FileTypeRule.LOGGER.log(Level.SEVERE, "Invalid configuration at the <file-type> rule. Name: {0}", new Object[] { this.name });
            throw e;
        }
        for (final Element nameElement : RuleSetParser.getChildNodesByTagName(fileTypeElement, "name")) {
            this.addFileTypeName(nameElement.getTextContent());
        }
    }
    
    private void addFileType(final String type) throws RuntimeException {
        if (!SecurityUtil.isValid(type)) {
            return;
        }
        if (type.contains("/")) {
            try {
                final MimeType mimeType = TikaUtil.getMimeTypeDetector().getRegisteredMimeType(type);
                if (mimeType == null) {
                    throw new RuntimeException("The type '" + type + "' is not supported.");
                }
                (this.fileTypes = ((this.fileTypes == null) ? new ArrayList<String>() : this.fileTypes)).add(mimeType.getType().toString());
                return;
            }
            catch (final MimeTypeException e) {
                throw new RuntimeException("Invalid characters present in the type '" + type + "'.");
            }
        }
        if (!FileTypeRule.TIKA_SUPPORTED_TYPES_PATTERN.matcher(type).matches()) {
            throw new RuntimeException("The type '" + type + "' is not supported.");
        }
        (this.types = ((this.types == null) ? new ArrayList<String>() : this.types)).add(type);
    }
    
    private void addFileTypeName(final String filetTypeRuleName) {
        if (!SecurityUtil.isValid(filetTypeRuleName)) {
            return;
        }
        (this.fileTypeNames = ((this.fileTypeNames == null) ? new ArrayList<String>() : this.fileTypeNames)).add(filetTypeRuleName);
    }
    
    protected void addFileTypeRule(final FileTypeRule fileTypeRule) {
        if (fileTypeRule == null) {
            return;
        }
        (this.fileTypeRules = ((this.fileTypeRules == null) ? new ArrayList<FileTypeRule>() : this.fileTypeRules)).add(fileTypeRule);
    }
    
    public boolean contains(final String fileType) {
        if (!SecurityUtil.isValid(fileType)) {
            return false;
        }
        if (this.fileTypes != null && this.fileTypes.contains(fileType)) {
            return true;
        }
        if (this.types != null) {
            for (final String mainType : this.types) {
                if (fileType.startsWith(mainType)) {
                    return true;
                }
            }
        }
        if (this.fileTypeRules != null) {
            for (final FileTypeRule fileTypeRule : this.fileTypeRules) {
                if (fileTypeRule.contains(fileType)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getFileTypes() {
        return this.fileTypes;
    }
    
    public List<String> getTypes() {
        return this.types;
    }
    
    public List<FileTypeRule> getFileTypeRules() {
        return this.fileTypeRules;
    }
    
    protected List<String> getFileTypeNames() {
        return this.fileTypeNames;
    }
    
    static {
        LOGGER = Logger.getLogger(FileTypeRule.class.getName());
        TIKA_SUPPORTED_TYPES_PATTERN = Pattern.compile("application|text|image|audio|video|model|chemical|message|multipart|x-conference");
    }
}
