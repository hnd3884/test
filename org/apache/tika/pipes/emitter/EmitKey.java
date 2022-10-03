package org.apache.tika.pipes.emitter;

import java.util.Objects;
import java.io.Serializable;

public class EmitKey implements Serializable
{
    private static final long serialVersionUID = -3861669115439125268L;
    private String emitterName;
    private String emitKey;
    
    public EmitKey() {
    }
    
    public EmitKey(final String emitterName, final String emitKey) {
        this.emitterName = emitterName;
        this.emitKey = emitKey;
    }
    
    public String getEmitterName() {
        return this.emitterName;
    }
    
    public String getEmitKey() {
        return this.emitKey;
    }
    
    @Override
    public String toString() {
        return "EmitterKey{emitterName='" + this.emitterName + '\'' + ", emitterKey='" + this.emitKey + '\'' + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final EmitKey emitKey1 = (EmitKey)o;
        return Objects.equals(this.emitterName, emitKey1.emitterName) && Objects.equals(this.emitKey, emitKey1.emitKey);
    }
    
    @Override
    public int hashCode() {
        int result = (this.emitterName != null) ? this.emitterName.hashCode() : 0;
        result = 31 * result + ((this.emitKey != null) ? this.emitKey.hashCode() : 0);
        return result;
    }
}
