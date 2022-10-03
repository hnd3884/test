package org.apache.poi.openxml4j.opc.internal.unmarshallers;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.OPCPackage;

public final class UnmarshallContext
{
    private OPCPackage _package;
    private PackagePartName partName;
    private ZipArchiveEntry zipEntry;
    
    public UnmarshallContext(final OPCPackage targetPackage, final PackagePartName partName) {
        this._package = targetPackage;
        this.partName = partName;
    }
    
    OPCPackage getPackage() {
        return this._package;
    }
    
    public void setPackage(final OPCPackage container) {
        this._package = container;
    }
    
    PackagePartName getPartName() {
        return this.partName;
    }
    
    public void setPartName(final PackagePartName partName) {
        this.partName = partName;
    }
    
    ZipArchiveEntry getZipEntry() {
        return this.zipEntry;
    }
    
    public void setZipEntry(final ZipArchiveEntry zipEntry) {
        this.zipEntry = zipEntry;
    }
}
