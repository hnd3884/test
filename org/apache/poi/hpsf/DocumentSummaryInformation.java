package org.apache.poi.hpsf;

import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;

public class DocumentSummaryInformation extends PropertySet
{
    public static final String DEFAULT_STREAM_NAME = "\u0005DocumentSummaryInformation";
    public static final ClassID[] FORMAT_ID;
    
    @Override
    public PropertyIDMap getPropertySetIDMap() {
        return PropertyIDMap.getDocumentSummaryInformationProperties();
    }
    
    public DocumentSummaryInformation() {
        this.getFirstSection().setFormatID(ClassIDPredefined.DOC_SUMMARY.getClassID());
    }
    
    public DocumentSummaryInformation(final PropertySet ps) throws UnexpectedPropertySetTypeException {
        super(ps);
        if (!this.isDocumentSummaryInformation()) {
            throw new UnexpectedPropertySetTypeException("Not a " + this.getClass().getName());
        }
    }
    
    public DocumentSummaryInformation(final InputStream stream) throws NoPropertySetStreamException, IOException {
        super(stream);
    }
    
    public String getCategory() {
        return this.getPropertyStringValue(2);
    }
    
    public void setCategory(final String category) {
        this.getFirstSection().setProperty(2, category);
    }
    
    public void removeCategory() {
        this.remove1stProperty(2L);
    }
    
    public String getPresentationFormat() {
        return this.getPropertyStringValue(3);
    }
    
    public void setPresentationFormat(final String presentationFormat) {
        this.getFirstSection().setProperty(3, presentationFormat);
    }
    
    public void removePresentationFormat() {
        this.remove1stProperty(3L);
    }
    
    public int getByteCount() {
        return this.getPropertyIntValue(4);
    }
    
    public void setByteCount(final int byteCount) {
        this.set1stProperty(4L, byteCount);
    }
    
    public void removeByteCount() {
        this.remove1stProperty(4L);
    }
    
    public int getLineCount() {
        return this.getPropertyIntValue(5);
    }
    
    public void setLineCount(final int lineCount) {
        this.set1stProperty(5L, lineCount);
    }
    
    public void removeLineCount() {
        this.remove1stProperty(5L);
    }
    
    public int getParCount() {
        return this.getPropertyIntValue(6);
    }
    
    public void setParCount(final int parCount) {
        this.set1stProperty(6L, parCount);
    }
    
    public void removeParCount() {
        this.remove1stProperty(6L);
    }
    
    public int getSlideCount() {
        return this.getPropertyIntValue(7);
    }
    
    public void setSlideCount(final int slideCount) {
        this.set1stProperty(7L, slideCount);
    }
    
    public void removeSlideCount() {
        this.remove1stProperty(7L);
    }
    
    public int getNoteCount() {
        return this.getPropertyIntValue(8);
    }
    
    public void setNoteCount(final int noteCount) {
        this.set1stProperty(8L, noteCount);
    }
    
    public void removeNoteCount() {
        this.remove1stProperty(8L);
    }
    
    public int getHiddenCount() {
        return this.getPropertyIntValue(9);
    }
    
    public void setHiddenCount(final int hiddenCount) {
        this.set1stProperty(9L, hiddenCount);
    }
    
    public void removeHiddenCount() {
        this.remove1stProperty(9L);
    }
    
    public int getMMClipCount() {
        return this.getPropertyIntValue(10);
    }
    
    public void setMMClipCount(final int mmClipCount) {
        this.set1stProperty(10L, mmClipCount);
    }
    
    public void removeMMClipCount() {
        this.remove1stProperty(10L);
    }
    
    public boolean getScale() {
        return this.getPropertyBooleanValue(11);
    }
    
    public void setScale(final boolean scale) {
        this.set1stProperty(11L, scale);
    }
    
    public void removeScale() {
        this.remove1stProperty(11L);
    }
    
    public byte[] getHeadingPair() {
        this.notYetImplemented("Reading byte arrays ");
        return (byte[])this.getProperty(12);
    }
    
    public void setHeadingPair(final byte[] headingPair) {
        this.notYetImplemented("Writing byte arrays ");
    }
    
    public void removeHeadingPair() {
        this.remove1stProperty(12L);
    }
    
    public byte[] getDocparts() {
        this.notYetImplemented("Reading byte arrays");
        return (byte[])this.getProperty(13);
    }
    
    public void setDocparts(final byte[] docparts) {
        this.notYetImplemented("Writing byte arrays");
    }
    
    public void removeDocparts() {
        this.remove1stProperty(13L);
    }
    
    public String getManager() {
        return this.getPropertyStringValue(14);
    }
    
    public void setManager(final String manager) {
        this.set1stProperty(14L, manager);
    }
    
    public void removeManager() {
        this.remove1stProperty(14L);
    }
    
    public String getCompany() {
        return this.getPropertyStringValue(15);
    }
    
    public void setCompany(final String company) {
        this.set1stProperty(15L, company);
    }
    
    public void removeCompany() {
        this.remove1stProperty(15L);
    }
    
    public boolean getLinksDirty() {
        return this.getPropertyBooleanValue(16);
    }
    
    public void setLinksDirty(final boolean linksDirty) {
        this.set1stProperty(16L, linksDirty);
    }
    
    public void removeLinksDirty() {
        this.remove1stProperty(16L);
    }
    
    public int getCharCountWithSpaces() {
        return this.getPropertyIntValue(17);
    }
    
    public void setCharCountWithSpaces(final int count) {
        this.set1stProperty(17L, count);
    }
    
    public void removeCharCountWithSpaces() {
        this.remove1stProperty(17L);
    }
    
    public boolean getHyperlinksChanged() {
        return this.getPropertyBooleanValue(22);
    }
    
    public void setHyperlinksChanged(final boolean changed) {
        this.set1stProperty(22L, changed);
    }
    
    public void removeHyperlinksChanged() {
        this.remove1stProperty(22L);
    }
    
    public int getApplicationVersion() {
        return this.getPropertyIntValue(23);
    }
    
    public void setApplicationVersion(final int version) {
        this.set1stProperty(23L, version);
    }
    
    public void removeApplicationVersion() {
        this.remove1stProperty(23L);
    }
    
    public byte[] getVBADigitalSignature() {
        final Object value = this.getProperty(24);
        return (value instanceof byte[]) ? ((byte[])value) : null;
    }
    
    public void setVBADigitalSignature(final byte[] signature) {
        this.set1stProperty(24L, signature);
    }
    
    public void removeVBADigitalSignature() {
        this.remove1stProperty(24L);
    }
    
    public String getContentType() {
        return this.getPropertyStringValue(26);
    }
    
    public void setContentType(final String type) {
        this.set1stProperty(26L, type);
    }
    
    public void removeContentType() {
        this.remove1stProperty(26L);
    }
    
    public String getContentStatus() {
        return this.getPropertyStringValue(27);
    }
    
    public void setContentStatus(final String status) {
        this.set1stProperty(27L, status);
    }
    
    public void removeContentStatus() {
        this.remove1stProperty(27L);
    }
    
    public String getLanguage() {
        return this.getPropertyStringValue(28);
    }
    
    public void setLanguage(final String language) {
        this.set1stProperty(28L, language);
    }
    
    public void removeLanguage() {
        this.remove1stProperty(28L);
    }
    
    public String getDocumentVersion() {
        return this.getPropertyStringValue(29);
    }
    
    public void setDocumentVersion(final String version) {
        this.set1stProperty(29L, version);
    }
    
    public void removeDocumentVersion() {
        this.remove1stProperty(29L);
    }
    
    public CustomProperties getCustomProperties() {
        CustomProperties cps = null;
        if (this.getSectionCount() >= 2) {
            cps = new CustomProperties();
            final Section section = this.getSections().get(1);
            final Map<Long, String> dictionary = section.getDictionary();
            final Property[] properties = section.getProperties();
            int propertyCount = 0;
            for (final Property p : properties) {
                final long id = p.getID();
                if (id == 1L) {
                    cps.setCodepage((int)p.getValue());
                }
                else if (id > 1L) {
                    ++propertyCount;
                    final CustomProperty cp = new CustomProperty(p, dictionary.get(id));
                    cps.put(cp.getName(), cp);
                }
            }
            if (cps.size() != propertyCount) {
                cps.setPure(false);
            }
        }
        return cps;
    }
    
    public void setCustomProperties(final CustomProperties customProperties) {
        this.ensureSection2();
        final Section section = this.getSections().get(1);
        final Map<Long, String> dictionary = customProperties.getDictionary();
        int cpCodepage = customProperties.getCodepage();
        if (cpCodepage < 0) {
            cpCodepage = section.getCodepage();
        }
        if (cpCodepage < 0) {
            cpCodepage = 1252;
        }
        customProperties.setCodepage(cpCodepage);
        section.setCodepage(cpCodepage);
        section.setDictionary(dictionary);
        for (final CustomProperty p : customProperties.properties()) {
            section.setProperty(p);
        }
    }
    
    private void ensureSection2() {
        if (this.getSectionCount() < 2) {
            final Section s2 = new Section();
            s2.setFormatID(ClassIDPredefined.USER_PROPERTIES.getClassID());
            this.addSection(s2);
        }
    }
    
    public void removeCustomProperties() {
        if (this.getSectionCount() < 2) {
            throw new HPSFRuntimeException("Illegal internal format of Document SummaryInformation stream: second section is missing.");
        }
        final List<Section> l = new LinkedList<Section>(this.getSections());
        this.clearSections();
        int idx = 0;
        for (final Section s : l) {
            if (idx++ != 1) {
                this.addSection(s);
            }
        }
    }
    
    private void notYetImplemented(final String msg) {
        throw new UnsupportedOperationException(msg + " is not yet implemented.");
    }
    
    static {
        FORMAT_ID = new ClassID[] { ClassIDPredefined.DOC_SUMMARY.getClassID(), ClassIDPredefined.USER_PROPERTIES.getClassID() };
    }
}
