package org.apache.poi.sl.usermodel;

import org.apache.poi.hpsf.ClassIDPredefined;
import org.apache.poi.hpsf.ClassID;

public interface ObjectMetaData
{
    String getObjectName();
    
    String getProgId();
    
    ClassID getClassID();
    
    String getOleEntry();
    
    public enum Application
    {
        EXCEL_V8("Worksheet", "Excel.Sheet.8", "Package", ClassIDPredefined.EXCEL_V8), 
        EXCEL_V12("Worksheet", "Excel.Sheet.12", "Package", ClassIDPredefined.EXCEL_V12), 
        WORD_V8("Document", "Word.Document.8", "Package", ClassIDPredefined.WORD_V8), 
        WORD_V12("Document", "Word.Document.12", "Package", ClassIDPredefined.WORD_V12), 
        PDF("PDF", "AcroExch.Document", "Contents", ClassIDPredefined.PDF), 
        CUSTOM((String)null, (String)null, (String)null, (ClassIDPredefined)null);
        
        String objectName;
        String progId;
        String oleEntry;
        ClassID classId;
        
        private Application(final String objectName, final String progId, final String oleEntry, final ClassIDPredefined classId) {
            this.objectName = objectName;
            this.progId = progId;
            this.classId = ((classId == null) ? null : classId.getClassID());
            this.oleEntry = oleEntry;
        }
        
        public static Application lookup(final String progId) {
            for (final Application a : values()) {
                if (a.progId != null && a.progId.equals(progId)) {
                    return a;
                }
            }
            return null;
        }
        
        public ObjectMetaData getMetaData() {
            return new ObjectMetaData() {
                @Override
                public String getObjectName() {
                    return Application.this.objectName;
                }
                
                @Override
                public String getProgId() {
                    return Application.this.progId;
                }
                
                @Override
                public String getOleEntry() {
                    return Application.this.oleEntry;
                }
                
                @Override
                public ClassID getClassID() {
                    return Application.this.classId;
                }
            };
        }
    }
}
