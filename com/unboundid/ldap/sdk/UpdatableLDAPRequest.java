package com.unboundid.ldap.sdk;

import com.unboundid.util.Validator;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public abstract class UpdatableLDAPRequest extends LDAPRequest
{
    private static final long serialVersionUID = 2487230102594573848L;
    
    protected UpdatableLDAPRequest(final Control[] controls) {
        super(controls);
    }
    
    public final void setControls(final Control... controls) {
        if (controls == null) {
            this.setControlsInternal(UpdatableLDAPRequest.NO_CONTROLS);
        }
        else {
            this.setControlsInternal(controls);
        }
    }
    
    public final void setControls(final List<Control> controls) {
        if (controls == null || controls.isEmpty()) {
            this.setControlsInternal(UpdatableLDAPRequest.NO_CONTROLS);
        }
        else {
            final Control[] controlArray = new Control[controls.size()];
            this.setControlsInternal(controls.toArray(controlArray));
        }
    }
    
    public final void clearControls() {
        this.setControlsInternal(UpdatableLDAPRequest.NO_CONTROLS);
    }
    
    public final void addControl(final Control control) {
        Validator.ensureNotNull(control);
        final Control[] controls = this.getControls();
        final Control[] newControls = new Control[controls.length + 1];
        System.arraycopy(controls, 0, newControls, 0, controls.length);
        newControls[controls.length] = control;
        this.setControlsInternal(newControls);
    }
    
    public final void addControls(final Control... controls) {
        if (controls == null || controls.length == 0) {
            return;
        }
        final Control[] currentControls = this.getControls();
        final Control[] newControls = new Control[currentControls.length + controls.length];
        System.arraycopy(currentControls, 0, newControls, 0, currentControls.length);
        System.arraycopy(controls, 0, newControls, currentControls.length, controls.length);
        this.setControlsInternal(newControls);
    }
    
    public final Control removeControl(final String oid) {
        Validator.ensureNotNull(oid);
        final Control[] controls = this.getControls();
        int pos = -1;
        Control c = null;
        for (int i = 0; i < controls.length; ++i) {
            if (controls[i].getOID().equals(oid)) {
                c = controls[i];
                pos = i;
                break;
            }
        }
        if (pos < 0) {
            return null;
        }
        if (controls.length == 1) {
            this.setControlsInternal(UpdatableLDAPRequest.NO_CONTROLS);
        }
        else {
            final Control[] newControls = new Control[controls.length - 1];
            int j = 0;
            int k = 0;
            while (j < controls.length) {
                if (j != pos) {
                    newControls[k++] = controls[j];
                }
                ++j;
            }
            this.setControlsInternal(newControls);
        }
        return c;
    }
    
    public final boolean removeControl(final Control control) {
        Validator.ensureNotNull(control);
        final Control[] controls = this.getControls();
        int pos = -1;
        for (int i = 0; i < controls.length; ++i) {
            if (controls[i].equals(control)) {
                pos = i;
                break;
            }
        }
        if (pos < 0) {
            return false;
        }
        if (controls.length == 1) {
            this.setControlsInternal(UpdatableLDAPRequest.NO_CONTROLS);
        }
        else {
            final Control[] newControls = new Control[controls.length - 1];
            int j = 0;
            int k = 0;
            while (j < controls.length) {
                if (j != pos) {
                    newControls[k++] = controls[j];
                }
                ++j;
            }
            this.setControlsInternal(newControls);
        }
        return true;
    }
    
    public final Control replaceControl(final Control control) {
        Validator.ensureNotNull(control);
        return this.replaceControl(control.getOID(), control);
    }
    
    public final Control replaceControl(final String oid, final Control control) {
        Validator.ensureNotNull(oid);
        if (control == null) {
            return this.removeControl(oid);
        }
        final Control[] controls = this.getControls();
        for (int i = 0; i < controls.length; ++i) {
            if (controls[i].getOID().equals(oid)) {
                final Control c = controls[i];
                controls[i] = control;
                this.setControlsInternal(controls);
                return c;
            }
        }
        final Control[] newControls = new Control[controls.length + 1];
        System.arraycopy(controls, 0, newControls, 0, controls.length);
        newControls[controls.length] = control;
        this.setControlsInternal(newControls);
        return null;
    }
}
