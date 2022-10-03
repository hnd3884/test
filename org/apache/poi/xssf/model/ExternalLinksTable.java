package org.apache.poi.xssf.model;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedName;
import org.apache.poi.ss.usermodel.Name;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetName;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import java.io.OutputStream;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.ExternalLinkDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalLink;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class ExternalLinksTable extends POIXMLDocumentPart
{
    private CTExternalLink link;
    
    public ExternalLinksTable() {
        (this.link = CTExternalLink.Factory.newInstance()).addNewExternalBook();
    }
    
    public ExternalLinksTable(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            final ExternalLinkDocument doc = ExternalLinkDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.link = doc.getExternalLink();
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        final ExternalLinkDocument doc = ExternalLinkDocument.Factory.newInstance();
        doc.setExternalLink(this.link);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.writeTo(out);
        out.close();
    }
    
    public CTExternalLink getCTExternalLink() {
        return this.link;
    }
    
    public String getLinkedFileName() {
        final String rId = this.link.getExternalBook().getId();
        final PackageRelationship rel = this.getPackagePart().getRelationship(rId);
        if (rel != null && rel.getTargetMode() == TargetMode.EXTERNAL) {
            return rel.getTargetURI().toString();
        }
        return null;
    }
    
    public void setLinkedFileName(final String target) {
        final String rId = this.link.getExternalBook().getId();
        if (rId != null) {
            if (!rId.isEmpty()) {
                this.getPackagePart().removeRelationship(rId);
            }
        }
        final PackageRelationship newRel = this.getPackagePart().addExternalRelationship(target, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/externalLinkPath");
        this.link.getExternalBook().setId(newRel.getId());
    }
    
    public List<String> getSheetNames() {
        final CTExternalSheetName[] sheetNames = this.link.getExternalBook().getSheetNames().getSheetNameArray();
        final List<String> names = new ArrayList<String>(sheetNames.length);
        for (final CTExternalSheetName name : sheetNames) {
            names.add(name.getVal());
        }
        return names;
    }
    
    public List<Name> getDefinedNames() {
        final CTExternalDefinedName[] extNames = this.link.getExternalBook().getDefinedNames().getDefinedNameArray();
        final List<Name> names = new ArrayList<Name>(extNames.length);
        for (final CTExternalDefinedName extName : extNames) {
            names.add((Name)new ExternalName(extName));
        }
        return names;
    }
    
    protected class ExternalName implements Name
    {
        private CTExternalDefinedName name;
        
        protected ExternalName(final CTExternalDefinedName name) {
            this.name = name;
        }
        
        public String getNameName() {
            return this.name.getName();
        }
        
        public void setNameName(final String name) {
            this.name.setName(name);
        }
        
        public String getSheetName() {
            final int sheetId = this.getSheetIndex();
            if (sheetId >= 0) {
                return ExternalLinksTable.this.getSheetNames().get(sheetId);
            }
            return null;
        }
        
        public int getSheetIndex() {
            if (this.name.isSetSheetId()) {
                return (int)this.name.getSheetId();
            }
            return -1;
        }
        
        public void setSheetIndex(final int sheetId) {
            this.name.setSheetId((long)sheetId);
        }
        
        public String getRefersToFormula() {
            return this.name.getRefersTo().substring(1);
        }
        
        public void setRefersToFormula(final String formulaText) {
            this.name.setRefersTo('=' + formulaText);
        }
        
        public boolean isFunctionName() {
            return false;
        }
        
        public boolean isDeleted() {
            return false;
        }
        
        public String getComment() {
            return null;
        }
        
        public void setComment(final String comment) {
            throw new IllegalStateException("Not Supported");
        }
        
        public void setFunction(final boolean value) {
            throw new IllegalStateException("Not Supported");
        }
    }
}
