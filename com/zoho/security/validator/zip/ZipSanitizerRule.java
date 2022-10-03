package com.zoho.security.validator.zip;

import java.util.ArrayList;
import org.w3c.dom.Element;
import java.util.regex.Pattern;
import java.util.List;

public class ZipSanitizerRule
{
    private String name;
    private String action;
    private long max_extraction_size;
    private int max_level;
    private int max_files_count;
    private List<String> allowedExtensions;
    private List<String> blockedExtensions;
    private Pattern allowedContentTypes;
    private Pattern blockedContentTypes;
    
    public ZipSanitizerRule() {
        this.name = null;
        this.action = null;
        this.max_extraction_size = -1L;
        this.max_level = -1;
        this.max_files_count = -1;
        this.allowedExtensions = null;
        this.blockedExtensions = null;
        this.allowedContentTypes = null;
        this.blockedContentTypes = null;
        this.action = "error";
        this.max_extraction_size = 2147483648L;
        this.max_level = 10;
        this.max_files_count = 3000;
        this.blockedExtensions = this.getArrayList("ade|adp|app|asa|asp|bas|bat|cer|chm|cmd|com|cpl|crt|csh|dll|exe|fxp|hlp|hta|htr|inf|ins|isp|its|js|jse|ksh|lnk|mad|maf|mag|mam|maq|mar|mas|mat|mau|mav|maw|mda|mdb|mde|mdt|mdw|mdz|mht|msc|msi|msp|mst|ocx|ops|pcd|pif|prf|prg|reg|scf|scr|sct|shb|shs|tmp|url|vb|vbe|vbs|vbx|vsmacros|vss|vst|vsw|ws|wsc|wsf|wsh|xsl|docm|dotm|xlsm|xltm|xlam|pptm|potm|ppam|ppsm|sldm", "\\|");
        this.blockedContentTypes = this.getPattern("(application/x-msdownload; format=.*)|application/x-bat|application/vnd.ms-htmlhelp|application/x-msdownload|application/x-matlab-data|application/x-msaccess|multipart/related|audio/adpcm|text/asp|text/x-basic|application/pkix-cert|application/x-x509-ca-cert|application/x-csh|application/x-dosexec|application/winhlp|application/javascript|application/vnd.ecowin.chart|application/x-ms-installer|application/x-ms-installer|application/x-ms-installer|application/pics-rules|text/x-vbdotnet|text/x-vbscript|application/vnd.visio|application/vnd.ms-word.document.macroenabled.12|application/vnd.ms-word.template.macroenabled.12|application/vnd.ms-excel.sheet.macroenabled.12|application/vnd.ms-excel.template.macroenabled.12|application/vnd.ms-excel.addin.macroenabled.12|application/vnd.ms-powerpoint.presentation.macroenabled.12|application/vnd.ms-powerpoint.template.macroenabled.12|application/vnd.ms-powerpoint.addin.macroenabled.12|application/vnd.ms-powerpoint.slideshow.macroenabled.12|application/vnd.ms-powerpoint.slide.macroenabled.12");
    }
    
    public ZipSanitizerRule(final String name, final String action, final long max_size_KB, final int max_level, final int max_files_count, final List<String> allowedExtensions, final List<String> blockedExtensions, final Pattern allowedContentTypes, final Pattern blockedContentTypes) {
        this.name = null;
        this.action = null;
        this.max_extraction_size = -1L;
        this.max_level = -1;
        this.max_files_count = -1;
        this.allowedExtensions = null;
        this.blockedExtensions = null;
        this.allowedContentTypes = null;
        this.blockedContentTypes = null;
        this.name = name;
        this.action = (isValid(action) ? action : "error");
        this.max_extraction_size = ((max_size_KB != -1L) ? (max_size_KB * 1024L) : 2147483648L);
        this.max_level = ((max_level != -1) ? max_level : 10);
        this.max_files_count = ((max_files_count != -1) ? max_files_count : 3000);
        this.allowedExtensions = allowedExtensions;
        this.blockedExtensions = blockedExtensions;
        this.allowedContentTypes = allowedContentTypes;
        this.blockedContentTypes = blockedContentTypes;
        this.validateConfiguration();
    }
    
    public ZipSanitizerRule(final Element element) {
        this.name = null;
        this.action = null;
        this.max_extraction_size = -1L;
        this.max_level = -1;
        this.max_files_count = -1;
        this.allowedExtensions = null;
        this.blockedExtensions = null;
        this.allowedContentTypes = null;
        this.blockedContentTypes = null;
        this.name = element.getAttribute("name");
        final String actionStr = element.getAttribute("action").toLowerCase();
        this.action = (isValid(actionStr) ? actionStr : "error");
        final String max_size = element.getAttribute("max-extraction-size");
        this.max_extraction_size = (isValid(max_size) ? (Long.parseLong(max_size) * 1024L) : 2147483648L);
        final String max_l = element.getAttribute("max-level");
        this.max_level = (isValid(max_l) ? Integer.parseInt(max_l) : 10);
        final String max_fc = element.getAttribute("max-files-count");
        this.max_files_count = (isValid(max_fc) ? Integer.parseInt(max_fc) : 3000);
        final String allowedextns = element.getAttribute("allowed-extensions");
        this.allowedExtensions = (isValid(allowedextns) ? this.getArrayList(allowedextns.toLowerCase(), "\\|") : null);
        final String blockedextns = element.getAttribute("blocked-extensions");
        this.blockedExtensions = (isValid(blockedextns) ? this.getArrayList(blockedextns.toLowerCase(), "\\|") : null);
        final String allowedtypes = element.getAttribute("allowed-content-types");
        this.allowedContentTypes = (isValid(allowedtypes) ? this.getPattern(allowedtypes) : null);
        final String blockedtypes = element.getAttribute("blocked-content-types");
        this.blockedContentTypes = (isValid(blockedtypes) ? this.getPattern(blockedtypes) : null);
        this.validateConfiguration();
    }
    
    private void validateConfiguration() {
        if (!isValid(this.action)) {
            throw new RuntimeException("\"action\" attribute is mandatory configuration for zip-extractor rule \"+name+\" ");
        }
        if (this.allowedExtensions != null && this.blockedExtensions != null) {
            throw new RuntimeException("Both \"allowed-extensions\" and \"blocked-extensions\" attributes are not allowed for zip-extractor rule \"+name+\" ");
        }
        if (this.allowedContentTypes != null && this.blockedContentTypes != null) {
            throw new RuntimeException("Both \"allowed-content-types\" and \"blocked-content-types\" attributes are not allowed for zip-extractor rule \"+name+\" ");
        }
    }
    
    private Pattern getPattern(final String contentTypes) {
        return Pattern.compile(contentTypes);
    }
    
    private List<String> getArrayList(final String input, final String splitByChar) {
        final List<String> inputList = new ArrayList<String>();
        final String[] inputArray = input.split(splitByChar);
        for (int i = 0; i < inputArray.length; ++i) {
            inputList.add(inputArray[i]);
        }
        return inputList;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getAction() {
        return this.action;
    }
    
    public long getMax_extraction_size() {
        return this.max_extraction_size;
    }
    
    public int getMax_level() {
        return this.max_level;
    }
    
    public int getMax_files_count() {
        return this.max_files_count;
    }
    
    public List<String> getAllowedExtensions() {
        return this.allowedExtensions;
    }
    
    public List<String> getBlockedExtensions() {
        return this.blockedExtensions;
    }
    
    public Pattern getAllowedContentTypes() {
        return this.allowedContentTypes;
    }
    
    public Pattern getBlockedContentTypes() {
        return this.blockedContentTypes;
    }
    
    public static boolean isValid(final Object value) {
        return value != null && !value.equals("null") && !value.equals("");
    }
}
