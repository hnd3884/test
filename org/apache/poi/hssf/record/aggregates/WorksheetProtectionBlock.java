package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.ScenarioProtectRecord;
import org.apache.poi.hssf.record.ObjectProtectRecord;
import org.apache.poi.hssf.record.ProtectRecord;

public final class WorksheetProtectionBlock extends RecordAggregate
{
    private ProtectRecord _protectRecord;
    private ObjectProtectRecord _objectProtectRecord;
    private ScenarioProtectRecord _scenarioProtectRecord;
    private PasswordRecord _passwordRecord;
    
    public static boolean isComponentRecord(final int sid) {
        switch (sid) {
            case 18:
            case 19:
            case 99:
            case 221: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean readARecord(final RecordStream rs) {
        switch (rs.peekNextSid()) {
            case 18: {
                this.checkNotPresent(this._protectRecord);
                this._protectRecord = (ProtectRecord)rs.getNext();
                break;
            }
            case 99: {
                this.checkNotPresent(this._objectProtectRecord);
                this._objectProtectRecord = (ObjectProtectRecord)rs.getNext();
                break;
            }
            case 221: {
                this.checkNotPresent(this._scenarioProtectRecord);
                this._scenarioProtectRecord = (ScenarioProtectRecord)rs.getNext();
                break;
            }
            case 19: {
                this.checkNotPresent(this._passwordRecord);
                this._passwordRecord = (PasswordRecord)rs.getNext();
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }
    
    private void checkNotPresent(final Record rec) {
        if (rec != null) {
            throw new RecordFormatException("Duplicate PageSettingsBlock record (sid=0x" + Integer.toHexString(rec.getSid()) + ")");
        }
    }
    
    @Override
    public void visitContainedRecords(final RecordVisitor rv) {
        visitIfPresent(this._protectRecord, rv);
        visitIfPresent(this._objectProtectRecord, rv);
        visitIfPresent(this._scenarioProtectRecord, rv);
        visitIfPresent(this._passwordRecord, rv);
    }
    
    private static void visitIfPresent(final Record r, final RecordVisitor rv) {
        if (r != null) {
            rv.visitRecord(r);
        }
    }
    
    public PasswordRecord getPasswordRecord() {
        return this._passwordRecord;
    }
    
    public ScenarioProtectRecord getHCenter() {
        return this._scenarioProtectRecord;
    }
    
    public void addRecords(final RecordStream rs) {
        while (this.readARecord(rs)) {}
    }
    
    private ProtectRecord getProtect() {
        if (this._protectRecord == null) {
            this._protectRecord = new ProtectRecord(false);
        }
        return this._protectRecord;
    }
    
    private PasswordRecord getPassword() {
        if (this._passwordRecord == null) {
            this._passwordRecord = createPassword();
        }
        return this._passwordRecord;
    }
    
    public void protectSheet(final String password, final boolean shouldProtectObjects, final boolean shouldProtectScenarios) {
        if (password == null) {
            this._passwordRecord = null;
            this._protectRecord = null;
            this._objectProtectRecord = null;
            this._scenarioProtectRecord = null;
            return;
        }
        final ProtectRecord prec = this.getProtect();
        final PasswordRecord pass = this.getPassword();
        prec.setProtect(true);
        pass.setPassword((short)CryptoFunctions.createXorVerifier1(password));
        if (this._objectProtectRecord == null && shouldProtectObjects) {
            final ObjectProtectRecord rec = createObjectProtect();
            rec.setProtect(true);
            this._objectProtectRecord = rec;
        }
        if (this._scenarioProtectRecord == null && shouldProtectScenarios) {
            final ScenarioProtectRecord srec = createScenarioProtect();
            srec.setProtect(true);
            this._scenarioProtectRecord = srec;
        }
    }
    
    public boolean isSheetProtected() {
        return this._protectRecord != null && this._protectRecord.getProtect();
    }
    
    public boolean isObjectProtected() {
        return this._objectProtectRecord != null && this._objectProtectRecord.getProtect();
    }
    
    public boolean isScenarioProtected() {
        return this._scenarioProtectRecord != null && this._scenarioProtectRecord.getProtect();
    }
    
    private static ObjectProtectRecord createObjectProtect() {
        final ObjectProtectRecord retval = new ObjectProtectRecord();
        retval.setProtect(false);
        return retval;
    }
    
    private static ScenarioProtectRecord createScenarioProtect() {
        final ScenarioProtectRecord retval = new ScenarioProtectRecord();
        retval.setProtect(false);
        return retval;
    }
    
    private static PasswordRecord createPassword() {
        return new PasswordRecord(0);
    }
    
    public int getPasswordHash() {
        if (this._passwordRecord == null) {
            return 0;
        }
        return this._passwordRecord.getPassword();
    }
}
