package javapns.notification;

import org.slf4j.LoggerFactory;
import javapns.notification.exceptions.PayloadMaxSizeProbablyExceededException;
import javapns.notification.exceptions.PayloadMaxSizeExceededException;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

public abstract class Payload
{
    static final Logger logger;
    private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
    private final JSONObject payload;
    private String characterEncoding;
    private int expiry;
    private boolean payloadSizeEstimatedWhenAdding;
    private int preSendConfiguration;
    
    protected Payload() {
        this.characterEncoding = "UTF-8";
        this.expiry = 86400;
        this.payloadSizeEstimatedWhenAdding = false;
        this.preSendConfiguration = 0;
        this.payload = new JSONObject();
    }
    
    Payload(final String rawJSON) throws JSONException {
        this.characterEncoding = "UTF-8";
        this.expiry = 86400;
        this.payloadSizeEstimatedWhenAdding = false;
        this.preSendConfiguration = 0;
        this.payload = new JSONObject(rawJSON);
    }
    
    public JSONObject getPayload() {
        return this.payload;
    }
    
    public void addCustomDictionary(final String name, final String value) throws JSONException {
        Payload.logger.debug("Adding custom Dictionary [" + name + "] = [" + value + "]");
        this.put(name, value, this.payload, false);
    }
    
    public void addCustomDictionary(final String name, final int value) throws JSONException {
        Payload.logger.debug("Adding custom Dictionary [" + name + "] = [" + value + "]");
        this.put(name, value, this.payload, false);
    }
    
    public void addCustomDictionary(final String name, final List values) throws JSONException {
        Payload.logger.debug("Adding custom Dictionary [" + name + "] = (list)");
        this.put(name, values, this.payload, false);
    }
    
    public void addCustomDictionary(final String name, final Object value) throws JSONException {
        Payload.logger.debug("Adding custom Dictionary [" + name + "] = [" + value + "]");
        this.put(name, value, this.payload, false);
    }
    
    @Override
    public String toString() {
        return this.payload.toString();
    }
    
    void verifyPayloadIsNotEmpty() {
        if (this.getPreSendConfiguration() != 0) {
            return;
        }
        if (this.toString().equals("{}")) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }
    }
    
    public byte[] getPayloadAsBytes() throws Exception {
        final byte[] payload = this.getPayloadAsBytesUnchecked();
        this.validateMaximumPayloadSize(payload.length);
        return payload;
    }
    
    private byte[] getPayloadAsBytesUnchecked() throws Exception {
        byte[] bytes;
        try {
            bytes = this.toString().getBytes(this.characterEncoding);
        }
        catch (final Exception ex) {
            bytes = this.toString().getBytes();
        }
        return bytes;
    }
    
    public int getPayloadSize() throws Exception {
        return this.getPayloadAsBytesUnchecked().length;
    }
    
    private boolean isPayloadTooLong() {
        try {
            final byte[] bytes = this.getPayloadAsBytesUnchecked();
            if (bytes.length > this.getMaximumPayloadSize()) {
                return true;
            }
        }
        catch (final Exception ex) {}
        return false;
    }
    
    private int estimatePayloadSizeAfterAdding(final String propertyName, final Object propertyValue) {
        try {
            int estimatedSize = this.getPayloadAsBytesUnchecked().length;
            if (propertyName != null && propertyValue != null) {
                estimatedSize += 5;
                estimatedSize += propertyName.getBytes(this.getCharacterEncoding()).length;
                int estimatedValueSize = 0;
                if (propertyValue instanceof String || propertyValue instanceof Number) {
                    estimatedValueSize = propertyValue.toString().getBytes(this.getCharacterEncoding()).length;
                }
                estimatedSize += estimatedValueSize;
            }
            return estimatedSize;
        }
        catch (final Exception e) {
            try {
                return this.getPayloadSize();
            }
            catch (final Exception e2) {
                return 0;
            }
        }
    }
    
    public boolean isEstimatedPayloadSizeAllowedAfterAdding(final String propertyName, final Object propertyValue) {
        final int maximumPayloadSize = this.getMaximumPayloadSize();
        final int estimatedPayloadSize = this.estimatePayloadSizeAfterAdding(propertyName, propertyValue);
        return estimatedPayloadSize <= maximumPayloadSize;
    }
    
    private void validateMaximumPayloadSize(final int currentPayloadSize) throws PayloadMaxSizeExceededException {
        final int maximumPayloadSize = this.getMaximumPayloadSize();
        if (currentPayloadSize > maximumPayloadSize) {
            throw new PayloadMaxSizeExceededException(maximumPayloadSize, currentPayloadSize);
        }
    }
    
    void put(final String propertyName, final Object propertyValue, final JSONObject object, final boolean opt) throws JSONException {
        try {
            if (this.isPayloadSizeEstimatedWhenAdding()) {
                final int maximumPayloadSize = this.getMaximumPayloadSize();
                final int estimatedPayloadSize = this.estimatePayloadSizeAfterAdding(propertyName, propertyValue);
                final boolean estimatedToExceed = estimatedPayloadSize > maximumPayloadSize;
                if (estimatedToExceed) {
                    throw new PayloadMaxSizeProbablyExceededException(maximumPayloadSize, estimatedPayloadSize);
                }
            }
        }
        catch (final PayloadMaxSizeProbablyExceededException e) {
            throw e;
        }
        catch (final Exception ex) {}
        if (opt) {
            object.putOpt(propertyName, propertyValue);
        }
        else {
            object.put(propertyName, propertyValue);
        }
    }
    
    Object remove(final String propertyName, final JSONObject object) {
        return object.remove(propertyName);
    }
    
    public boolean isPayloadSizeEstimatedWhenAdding() {
        return this.payloadSizeEstimatedWhenAdding;
    }
    
    public void setPayloadSizeEstimatedWhenAdding(final boolean checked) {
        this.payloadSizeEstimatedWhenAdding = checked;
    }
    
    int getMaximumPayloadSize() {
        return Integer.MAX_VALUE;
    }
    
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }
    
    public void setCharacterEncoding(final String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }
    
    public int getExpiry() {
        return this.expiry;
    }
    
    public void setExpiry(final int seconds) {
        this.expiry = seconds;
    }
    
    public Payload asSimulationOnly() {
        this.setExpiry(919191);
        return this;
    }
    
    int getPreSendConfiguration() {
        return this.preSendConfiguration;
    }
    
    void setPreSendConfiguration(final int preSendConfiguration) {
        this.preSendConfiguration = preSendConfiguration;
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)Payload.class);
    }
}
