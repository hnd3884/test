package com.adventnet.sym.server.mdm.security.safetynet;

public class SafetyNetConstants
{
    public static final String ATTESTATION_API_KEY = "ApiKey";
    public static final String ATTESTATION_NONCE = "Nonce";
    public static final String ATTESTATION_SAFETYNET_ID = "SafetyNetId";
    public static final String ATTESTATION_RESPONSE = "AttestationResponse";
    public static final String ERROR_CODE = "ErrorCode";
    public static final String ERROR_MSG = "ErrorMsg";
    public static final String ATTESTATION_DECODE_FAILURE = "DecodeFailure";
    public static final String ATTESTION_DECODE_STATUS = "AttestationDecodeStatus";
    public static final String ATTESTATION_CERT_ISSUER = "attest.android.com";
    public static final String ATTESTATION_ADVICE = "advice";
    public static final String QUEUE_TASK_WIPE_ALL_DEVICES = "WipeAllDevices";
    public static final String QUEUE_TASK_WIPE_DEVICE = "WipeDevice";
    public static final String WIPE_DEVICE_RES_ID = "DeviceWipeResId";
    public static final String WIPE_DEVICE_REASON = "WipeReason";
    public static final String WIPE_DEVICE_I18 = "WipeReasonI18";
    public static final String WIPE_DEVICE_FB = "WipeReasonFeedBack";
    public static final int ATTESTATION_DECODE_SUCCESS = 1;
    public static final int ATTESTATION_DECODE_FAILED = 0;
    public static final int ATTESTATION_RESPONSE_DECODE_FAILED = 13000;
    public static final String ATTESTATION_RESPONSE_DECODE_FAILED_STR = "Cannot decode the response";
}
