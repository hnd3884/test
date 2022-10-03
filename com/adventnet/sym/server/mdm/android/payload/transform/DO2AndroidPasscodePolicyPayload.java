package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPasscodePolicyPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public class DO2AndroidPasscodePolicyPayload implements DO2AndroidPayload
{
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidPasscodePolicyPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("AndroidPasscodePolicy");
            while (iterator.hasNext()) {
                payload = new AndroidPasscodePolicyPayload("1.0", "com.mdm.mobiledevice.passcode", "Passcode Policy");
                final Row row = iterator.next();
                final Long configdataItem = (Long)row.get("CONFIG_DATA_ITEM_ID");
                final Integer passcodeType = (Integer)row.get("PASSCODE_TYPE");
                final Integer minLength = (Integer)row.get("MIN_PASSCODE_LENGTH");
                final Integer maxInactivity = (Integer)row.get("AUTO_LOCK_IDLE_FOR");
                final Integer maxFailedAttempts = (Integer)row.get("MAX_FAILED_ATTEMPTS");
                final Integer maxAge = (Integer)row.get("MAX_PASSCODE_AGE");
                final Integer passcodeHistory = (Integer)row.get("NO_OF_PASSCODE_MAINTAINED");
                final Integer minLowerLength = (Integer)row.get("MIN_LOWER_CASE_LENGTH");
                final Integer minNonLength = (Integer)row.get("MIN_NON_LETTER_LENGTH");
                final Integer minNumLength = (Integer)row.get("MIN_NUMERIC_LENGTH");
                final Integer minUpperLength = (Integer)row.get("MIN_UPPER_CASE_LENGTH");
                final Integer minSymbolLength = (Integer)row.get("MIN_SYMBOL_LENGTH");
                final Integer minLetterLength = (Integer)row.get("MIN_LETTER_LENGTH");
                final Integer maxChars = (Integer)row.get("MAX_CHARS");
                final Integer maxNumSeq = (Integer)row.get("MAX_NUMERIC_SEQUENCE");
                final Integer gracePeriod = (Integer)row.get("GRACE_PERIOD");
                final Integer fingerPrint = (Integer)row.get("ALLOW_FINGERPRINT");
                final Boolean irisScan = (Boolean)row.get("ALLOW_IRIS_SCAN");
                final Boolean faceUnlock = (Boolean)row.get("ALLOW_FACE_UNLOCK");
                final Boolean oneLock = (Boolean)row.get("ALLOW_ONE_LOCK");
                final Integer passcodeScope = (Integer)row.get("SCOPE_FOR_PASSCODE");
                final Boolean setDefaultPasscode = (Boolean)row.get("SET_DEFAULT_PASSCODE");
                final Boolean allowChnageDefaultPasscode = (Boolean)row.get("ALLOW_CHANGE_DEFAULT_PASSCODE");
                final Integer trustAgent = (Integer)row.get("ALLOW_TRUST_AGENTS");
                final Integer passcodeComplexity = (Integer)row.get("PASSCODE_COMPLEXITY");
                final Integer strongAuthTimeout = (Integer)row.get("STRONG_AUTH_TIMEOUT");
                if (passcodeType != -1) {
                    payload.setPasscodeType(passcodeType);
                }
                payload.setPasscodeScope(passcodeScope);
                if (minLength != -1 || passcodeType == 1) {
                    payload.setMinLength((passcodeType == 1) ? 0 : ((int)minLength));
                }
                if (maxInactivity != 0) {
                    payload.setMaxInactivity(maxInactivity);
                }
                payload.setMaxFailedAttempts(maxFailedAttempts);
                payload.setMaxAge((maxAge != -1) ? ((int)maxAge) : 0);
                if (passcodeHistory != 0) {
                    payload.setPinHistory(passcodeHistory);
                }
                if (minLowerLength != -1) {
                    payload.setMinLowerLength(minLowerLength);
                }
                if (minNonLength != -1) {
                    payload.setMinNonLength(minNonLength);
                }
                if (minNumLength != -1) {
                    payload.setMinNumLength(minNumLength);
                }
                if (minUpperLength != -1) {
                    payload.setMinUpperLength(minUpperLength);
                }
                if (minSymbolLength != -1) {
                    payload.setMinSymbolLength(minSymbolLength);
                }
                if (minLetterLength != -1) {
                    payload.setMinLetterLength(minLetterLength);
                }
                if (maxChars != -1) {
                    payload.setMaximumCharacters(maxChars);
                }
                if (maxNumSeq != -1) {
                    payload.setMaximumNumericSequence(maxNumSeq);
                }
                if (fingerPrint != -1) {
                    payload.setAllowFingerprint(fingerPrint);
                }
                payload.setAllowIrisScan(irisScan);
                payload.setFaceUnlock(faceUnlock);
                payload.setOneLock(oneLock);
                payload.setMaximumGracePeriod((gracePeriod != -1) ? ((int)gracePeriod) : 60);
                payload.setAllowTrustAgents(trustAgent);
                payload.setDefaultPasscode(setDefaultPasscode);
                payload.allowChnageDefaultPasscode(allowChnageDefaultPasscode);
                String newDefaultPasscode = "";
                if (row.get("NEW_DEFAULT_PASSCODE_ID") != null) {
                    final Long newDefaultPasscodeId = (Long)row.get("NEW_DEFAULT_PASSCODE_ID");
                    newDefaultPasscode = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(newDefaultPasscodeId.toString());
                }
                payload.setNewDefaultPasscode(newDefaultPasscode);
                payload.setPayloadUUID(configdataItem);
                payload.setPasscodeComplexity(passcodeComplexity);
                payload.setStrongAuthTimeout(strongAuthTimeout);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DO2AndroidPasscodePolicyPayload.class.getName()).log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
}
