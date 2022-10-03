package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishObjects;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFileRecoveryPr;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagTypes;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCaches;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomWorkbookViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleSize;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedNames;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalReferences;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFunctionGroups;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheets;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFileSharing;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFileVersion;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTWorkbookImpl extends XmlComplexContentImpl implements CTWorkbook
{
    private static final long serialVersionUID = 1L;
    private static final QName FILEVERSION$0;
    private static final QName FILESHARING$2;
    private static final QName WORKBOOKPR$4;
    private static final QName WORKBOOKPROTECTION$6;
    private static final QName BOOKVIEWS$8;
    private static final QName SHEETS$10;
    private static final QName FUNCTIONGROUPS$12;
    private static final QName EXTERNALREFERENCES$14;
    private static final QName DEFINEDNAMES$16;
    private static final QName CALCPR$18;
    private static final QName OLESIZE$20;
    private static final QName CUSTOMWORKBOOKVIEWS$22;
    private static final QName PIVOTCACHES$24;
    private static final QName SMARTTAGPR$26;
    private static final QName SMARTTAGTYPES$28;
    private static final QName WEBPUBLISHING$30;
    private static final QName FILERECOVERYPR$32;
    private static final QName WEBPUBLISHOBJECTS$34;
    private static final QName EXTLST$36;
    
    public CTWorkbookImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTFileVersion getFileVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFileVersion ctFileVersion = (CTFileVersion)this.get_store().find_element_user(CTWorkbookImpl.FILEVERSION$0, 0);
            if (ctFileVersion == null) {
                return null;
            }
            return ctFileVersion;
        }
    }
    
    public boolean isSetFileVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.FILEVERSION$0) != 0;
        }
    }
    
    public void setFileVersion(final CTFileVersion ctFileVersion) {
        this.generatedSetterHelperImpl((XmlObject)ctFileVersion, CTWorkbookImpl.FILEVERSION$0, 0, (short)1);
    }
    
    public CTFileVersion addNewFileVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFileVersion)this.get_store().add_element_user(CTWorkbookImpl.FILEVERSION$0);
        }
    }
    
    public void unsetFileVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.FILEVERSION$0, 0);
        }
    }
    
    public CTFileSharing getFileSharing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFileSharing ctFileSharing = (CTFileSharing)this.get_store().find_element_user(CTWorkbookImpl.FILESHARING$2, 0);
            if (ctFileSharing == null) {
                return null;
            }
            return ctFileSharing;
        }
    }
    
    public boolean isSetFileSharing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.FILESHARING$2) != 0;
        }
    }
    
    public void setFileSharing(final CTFileSharing ctFileSharing) {
        this.generatedSetterHelperImpl((XmlObject)ctFileSharing, CTWorkbookImpl.FILESHARING$2, 0, (short)1);
    }
    
    public CTFileSharing addNewFileSharing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFileSharing)this.get_store().add_element_user(CTWorkbookImpl.FILESHARING$2);
        }
    }
    
    public void unsetFileSharing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.FILESHARING$2, 0);
        }
    }
    
    public CTWorkbookPr getWorkbookPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWorkbookPr ctWorkbookPr = (CTWorkbookPr)this.get_store().find_element_user(CTWorkbookImpl.WORKBOOKPR$4, 0);
            if (ctWorkbookPr == null) {
                return null;
            }
            return ctWorkbookPr;
        }
    }
    
    public boolean isSetWorkbookPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.WORKBOOKPR$4) != 0;
        }
    }
    
    public void setWorkbookPr(final CTWorkbookPr ctWorkbookPr) {
        this.generatedSetterHelperImpl((XmlObject)ctWorkbookPr, CTWorkbookImpl.WORKBOOKPR$4, 0, (short)1);
    }
    
    public CTWorkbookPr addNewWorkbookPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWorkbookPr)this.get_store().add_element_user(CTWorkbookImpl.WORKBOOKPR$4);
        }
    }
    
    public void unsetWorkbookPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.WORKBOOKPR$4, 0);
        }
    }
    
    public CTWorkbookProtection getWorkbookProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWorkbookProtection ctWorkbookProtection = (CTWorkbookProtection)this.get_store().find_element_user(CTWorkbookImpl.WORKBOOKPROTECTION$6, 0);
            if (ctWorkbookProtection == null) {
                return null;
            }
            return ctWorkbookProtection;
        }
    }
    
    public boolean isSetWorkbookProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.WORKBOOKPROTECTION$6) != 0;
        }
    }
    
    public void setWorkbookProtection(final CTWorkbookProtection ctWorkbookProtection) {
        this.generatedSetterHelperImpl((XmlObject)ctWorkbookProtection, CTWorkbookImpl.WORKBOOKPROTECTION$6, 0, (short)1);
    }
    
    public CTWorkbookProtection addNewWorkbookProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWorkbookProtection)this.get_store().add_element_user(CTWorkbookImpl.WORKBOOKPROTECTION$6);
        }
    }
    
    public void unsetWorkbookProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.WORKBOOKPROTECTION$6, 0);
        }
    }
    
    public CTBookViews getBookViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookViews ctBookViews = (CTBookViews)this.get_store().find_element_user(CTWorkbookImpl.BOOKVIEWS$8, 0);
            if (ctBookViews == null) {
                return null;
            }
            return ctBookViews;
        }
    }
    
    public boolean isSetBookViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.BOOKVIEWS$8) != 0;
        }
    }
    
    public void setBookViews(final CTBookViews ctBookViews) {
        this.generatedSetterHelperImpl((XmlObject)ctBookViews, CTWorkbookImpl.BOOKVIEWS$8, 0, (short)1);
    }
    
    public CTBookViews addNewBookViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookViews)this.get_store().add_element_user(CTWorkbookImpl.BOOKVIEWS$8);
        }
    }
    
    public void unsetBookViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.BOOKVIEWS$8, 0);
        }
    }
    
    public CTSheets getSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheets ctSheets = (CTSheets)this.get_store().find_element_user(CTWorkbookImpl.SHEETS$10, 0);
            if (ctSheets == null) {
                return null;
            }
            return ctSheets;
        }
    }
    
    public void setSheets(final CTSheets ctSheets) {
        this.generatedSetterHelperImpl((XmlObject)ctSheets, CTWorkbookImpl.SHEETS$10, 0, (short)1);
    }
    
    public CTSheets addNewSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheets)this.get_store().add_element_user(CTWorkbookImpl.SHEETS$10);
        }
    }
    
    public CTFunctionGroups getFunctionGroups() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFunctionGroups ctFunctionGroups = (CTFunctionGroups)this.get_store().find_element_user(CTWorkbookImpl.FUNCTIONGROUPS$12, 0);
            if (ctFunctionGroups == null) {
                return null;
            }
            return ctFunctionGroups;
        }
    }
    
    public boolean isSetFunctionGroups() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.FUNCTIONGROUPS$12) != 0;
        }
    }
    
    public void setFunctionGroups(final CTFunctionGroups ctFunctionGroups) {
        this.generatedSetterHelperImpl((XmlObject)ctFunctionGroups, CTWorkbookImpl.FUNCTIONGROUPS$12, 0, (short)1);
    }
    
    public CTFunctionGroups addNewFunctionGroups() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFunctionGroups)this.get_store().add_element_user(CTWorkbookImpl.FUNCTIONGROUPS$12);
        }
    }
    
    public void unsetFunctionGroups() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.FUNCTIONGROUPS$12, 0);
        }
    }
    
    public CTExternalReferences getExternalReferences() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalReferences ctExternalReferences = (CTExternalReferences)this.get_store().find_element_user(CTWorkbookImpl.EXTERNALREFERENCES$14, 0);
            if (ctExternalReferences == null) {
                return null;
            }
            return ctExternalReferences;
        }
    }
    
    public boolean isSetExternalReferences() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.EXTERNALREFERENCES$14) != 0;
        }
    }
    
    public void setExternalReferences(final CTExternalReferences ctExternalReferences) {
        this.generatedSetterHelperImpl((XmlObject)ctExternalReferences, CTWorkbookImpl.EXTERNALREFERENCES$14, 0, (short)1);
    }
    
    public CTExternalReferences addNewExternalReferences() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalReferences)this.get_store().add_element_user(CTWorkbookImpl.EXTERNALREFERENCES$14);
        }
    }
    
    public void unsetExternalReferences() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.EXTERNALREFERENCES$14, 0);
        }
    }
    
    public CTDefinedNames getDefinedNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDefinedNames ctDefinedNames = (CTDefinedNames)this.get_store().find_element_user(CTWorkbookImpl.DEFINEDNAMES$16, 0);
            if (ctDefinedNames == null) {
                return null;
            }
            return ctDefinedNames;
        }
    }
    
    public boolean isSetDefinedNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.DEFINEDNAMES$16) != 0;
        }
    }
    
    public void setDefinedNames(final CTDefinedNames ctDefinedNames) {
        this.generatedSetterHelperImpl((XmlObject)ctDefinedNames, CTWorkbookImpl.DEFINEDNAMES$16, 0, (short)1);
    }
    
    public CTDefinedNames addNewDefinedNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDefinedNames)this.get_store().add_element_user(CTWorkbookImpl.DEFINEDNAMES$16);
        }
    }
    
    public void unsetDefinedNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.DEFINEDNAMES$16, 0);
        }
    }
    
    public CTCalcPr getCalcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCalcPr ctCalcPr = (CTCalcPr)this.get_store().find_element_user(CTWorkbookImpl.CALCPR$18, 0);
            if (ctCalcPr == null) {
                return null;
            }
            return ctCalcPr;
        }
    }
    
    public boolean isSetCalcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.CALCPR$18) != 0;
        }
    }
    
    public void setCalcPr(final CTCalcPr ctCalcPr) {
        this.generatedSetterHelperImpl((XmlObject)ctCalcPr, CTWorkbookImpl.CALCPR$18, 0, (short)1);
    }
    
    public CTCalcPr addNewCalcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCalcPr)this.get_store().add_element_user(CTWorkbookImpl.CALCPR$18);
        }
    }
    
    public void unsetCalcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.CALCPR$18, 0);
        }
    }
    
    public CTOleSize getOleSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOleSize ctOleSize = (CTOleSize)this.get_store().find_element_user(CTWorkbookImpl.OLESIZE$20, 0);
            if (ctOleSize == null) {
                return null;
            }
            return ctOleSize;
        }
    }
    
    public boolean isSetOleSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.OLESIZE$20) != 0;
        }
    }
    
    public void setOleSize(final CTOleSize ctOleSize) {
        this.generatedSetterHelperImpl((XmlObject)ctOleSize, CTWorkbookImpl.OLESIZE$20, 0, (short)1);
    }
    
    public CTOleSize addNewOleSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOleSize)this.get_store().add_element_user(CTWorkbookImpl.OLESIZE$20);
        }
    }
    
    public void unsetOleSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.OLESIZE$20, 0);
        }
    }
    
    public CTCustomWorkbookViews getCustomWorkbookViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomWorkbookViews ctCustomWorkbookViews = (CTCustomWorkbookViews)this.get_store().find_element_user(CTWorkbookImpl.CUSTOMWORKBOOKVIEWS$22, 0);
            if (ctCustomWorkbookViews == null) {
                return null;
            }
            return ctCustomWorkbookViews;
        }
    }
    
    public boolean isSetCustomWorkbookViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.CUSTOMWORKBOOKVIEWS$22) != 0;
        }
    }
    
    public void setCustomWorkbookViews(final CTCustomWorkbookViews ctCustomWorkbookViews) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomWorkbookViews, CTWorkbookImpl.CUSTOMWORKBOOKVIEWS$22, 0, (short)1);
    }
    
    public CTCustomWorkbookViews addNewCustomWorkbookViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomWorkbookViews)this.get_store().add_element_user(CTWorkbookImpl.CUSTOMWORKBOOKVIEWS$22);
        }
    }
    
    public void unsetCustomWorkbookViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.CUSTOMWORKBOOKVIEWS$22, 0);
        }
    }
    
    public CTPivotCaches getPivotCaches() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotCaches ctPivotCaches = (CTPivotCaches)this.get_store().find_element_user(CTWorkbookImpl.PIVOTCACHES$24, 0);
            if (ctPivotCaches == null) {
                return null;
            }
            return ctPivotCaches;
        }
    }
    
    public boolean isSetPivotCaches() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.PIVOTCACHES$24) != 0;
        }
    }
    
    public void setPivotCaches(final CTPivotCaches ctPivotCaches) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotCaches, CTWorkbookImpl.PIVOTCACHES$24, 0, (short)1);
    }
    
    public CTPivotCaches addNewPivotCaches() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotCaches)this.get_store().add_element_user(CTWorkbookImpl.PIVOTCACHES$24);
        }
    }
    
    public void unsetPivotCaches() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.PIVOTCACHES$24, 0);
        }
    }
    
    public CTSmartTagPr getSmartTagPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTagPr ctSmartTagPr = (CTSmartTagPr)this.get_store().find_element_user(CTWorkbookImpl.SMARTTAGPR$26, 0);
            if (ctSmartTagPr == null) {
                return null;
            }
            return ctSmartTagPr;
        }
    }
    
    public boolean isSetSmartTagPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.SMARTTAGPR$26) != 0;
        }
    }
    
    public void setSmartTagPr(final CTSmartTagPr ctSmartTagPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTagPr, CTWorkbookImpl.SMARTTAGPR$26, 0, (short)1);
    }
    
    public CTSmartTagPr addNewSmartTagPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagPr)this.get_store().add_element_user(CTWorkbookImpl.SMARTTAGPR$26);
        }
    }
    
    public void unsetSmartTagPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.SMARTTAGPR$26, 0);
        }
    }
    
    public CTSmartTagTypes getSmartTagTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTagTypes ctSmartTagTypes = (CTSmartTagTypes)this.get_store().find_element_user(CTWorkbookImpl.SMARTTAGTYPES$28, 0);
            if (ctSmartTagTypes == null) {
                return null;
            }
            return ctSmartTagTypes;
        }
    }
    
    public boolean isSetSmartTagTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.SMARTTAGTYPES$28) != 0;
        }
    }
    
    public void setSmartTagTypes(final CTSmartTagTypes ctSmartTagTypes) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTagTypes, CTWorkbookImpl.SMARTTAGTYPES$28, 0, (short)1);
    }
    
    public CTSmartTagTypes addNewSmartTagTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagTypes)this.get_store().add_element_user(CTWorkbookImpl.SMARTTAGTYPES$28);
        }
    }
    
    public void unsetSmartTagTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.SMARTTAGTYPES$28, 0);
        }
    }
    
    public CTWebPublishing getWebPublishing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWebPublishing ctWebPublishing = (CTWebPublishing)this.get_store().find_element_user(CTWorkbookImpl.WEBPUBLISHING$30, 0);
            if (ctWebPublishing == null) {
                return null;
            }
            return ctWebPublishing;
        }
    }
    
    public boolean isSetWebPublishing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.WEBPUBLISHING$30) != 0;
        }
    }
    
    public void setWebPublishing(final CTWebPublishing ctWebPublishing) {
        this.generatedSetterHelperImpl((XmlObject)ctWebPublishing, CTWorkbookImpl.WEBPUBLISHING$30, 0, (short)1);
    }
    
    public CTWebPublishing addNewWebPublishing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWebPublishing)this.get_store().add_element_user(CTWorkbookImpl.WEBPUBLISHING$30);
        }
    }
    
    public void unsetWebPublishing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.WEBPUBLISHING$30, 0);
        }
    }
    
    public List<CTFileRecoveryPr> getFileRecoveryPrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FileRecoveryPrList extends AbstractList<CTFileRecoveryPr>
            {
                @Override
                public CTFileRecoveryPr get(final int n) {
                    return CTWorkbookImpl.this.getFileRecoveryPrArray(n);
                }
                
                @Override
                public CTFileRecoveryPr set(final int n, final CTFileRecoveryPr ctFileRecoveryPr) {
                    final CTFileRecoveryPr fileRecoveryPrArray = CTWorkbookImpl.this.getFileRecoveryPrArray(n);
                    CTWorkbookImpl.this.setFileRecoveryPrArray(n, ctFileRecoveryPr);
                    return fileRecoveryPrArray;
                }
                
                @Override
                public void add(final int n, final CTFileRecoveryPr ctFileRecoveryPr) {
                    CTWorkbookImpl.this.insertNewFileRecoveryPr(n).set((XmlObject)ctFileRecoveryPr);
                }
                
                @Override
                public CTFileRecoveryPr remove(final int n) {
                    final CTFileRecoveryPr fileRecoveryPrArray = CTWorkbookImpl.this.getFileRecoveryPrArray(n);
                    CTWorkbookImpl.this.removeFileRecoveryPr(n);
                    return fileRecoveryPrArray;
                }
                
                @Override
                public int size() {
                    return CTWorkbookImpl.this.sizeOfFileRecoveryPrArray();
                }
            }
            return new FileRecoveryPrList();
        }
    }
    
    @Deprecated
    public CTFileRecoveryPr[] getFileRecoveryPrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTWorkbookImpl.FILERECOVERYPR$32, (List)list);
            final CTFileRecoveryPr[] array = new CTFileRecoveryPr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFileRecoveryPr getFileRecoveryPrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFileRecoveryPr ctFileRecoveryPr = (CTFileRecoveryPr)this.get_store().find_element_user(CTWorkbookImpl.FILERECOVERYPR$32, n);
            if (ctFileRecoveryPr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFileRecoveryPr;
        }
    }
    
    public int sizeOfFileRecoveryPrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.FILERECOVERYPR$32);
        }
    }
    
    public void setFileRecoveryPrArray(final CTFileRecoveryPr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTWorkbookImpl.FILERECOVERYPR$32);
    }
    
    public void setFileRecoveryPrArray(final int n, final CTFileRecoveryPr ctFileRecoveryPr) {
        this.generatedSetterHelperImpl((XmlObject)ctFileRecoveryPr, CTWorkbookImpl.FILERECOVERYPR$32, n, (short)2);
    }
    
    public CTFileRecoveryPr insertNewFileRecoveryPr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFileRecoveryPr)this.get_store().insert_element_user(CTWorkbookImpl.FILERECOVERYPR$32, n);
        }
    }
    
    public CTFileRecoveryPr addNewFileRecoveryPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFileRecoveryPr)this.get_store().add_element_user(CTWorkbookImpl.FILERECOVERYPR$32);
        }
    }
    
    public void removeFileRecoveryPr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.FILERECOVERYPR$32, n);
        }
    }
    
    public CTWebPublishObjects getWebPublishObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWebPublishObjects ctWebPublishObjects = (CTWebPublishObjects)this.get_store().find_element_user(CTWorkbookImpl.WEBPUBLISHOBJECTS$34, 0);
            if (ctWebPublishObjects == null) {
                return null;
            }
            return ctWebPublishObjects;
        }
    }
    
    public boolean isSetWebPublishObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.WEBPUBLISHOBJECTS$34) != 0;
        }
    }
    
    public void setWebPublishObjects(final CTWebPublishObjects ctWebPublishObjects) {
        this.generatedSetterHelperImpl((XmlObject)ctWebPublishObjects, CTWorkbookImpl.WEBPUBLISHOBJECTS$34, 0, (short)1);
    }
    
    public CTWebPublishObjects addNewWebPublishObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWebPublishObjects)this.get_store().add_element_user(CTWorkbookImpl.WEBPUBLISHOBJECTS$34);
        }
    }
    
    public void unsetWebPublishObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.WEBPUBLISHOBJECTS$34, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTWorkbookImpl.EXTLST$36, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorkbookImpl.EXTLST$36) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTWorkbookImpl.EXTLST$36, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTWorkbookImpl.EXTLST$36);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorkbookImpl.EXTLST$36, 0);
        }
    }
    
    static {
        FILEVERSION$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fileVersion");
        FILESHARING$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fileSharing");
        WORKBOOKPR$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "workbookPr");
        WORKBOOKPROTECTION$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "workbookProtection");
        BOOKVIEWS$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "bookViews");
        SHEETS$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheets");
        FUNCTIONGROUPS$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "functionGroups");
        EXTERNALREFERENCES$14 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "externalReferences");
        DEFINEDNAMES$16 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "definedNames");
        CALCPR$18 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "calcPr");
        OLESIZE$20 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "oleSize");
        CUSTOMWORKBOOKVIEWS$22 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customWorkbookViews");
        PIVOTCACHES$24 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pivotCaches");
        SMARTTAGPR$26 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "smartTagPr");
        SMARTTAGTYPES$28 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "smartTagTypes");
        WEBPUBLISHING$30 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "webPublishing");
        FILERECOVERYPR$32 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fileRecoveryPr");
        WEBPUBLISHOBJECTS$34 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "webPublishObjects");
        EXTLST$36 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
