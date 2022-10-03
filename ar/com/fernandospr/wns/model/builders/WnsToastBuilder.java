package ar.com.fernandospr.wns.model.builders;

import ar.com.fernandospr.wns.model.WnsToastCommands;
import ar.com.fernandospr.wns.model.WnsAudio;
import ar.com.fernandospr.wns.model.WnsBinding;
import ar.com.fernandospr.wns.model.WnsVisual;
import ar.com.fernandospr.wns.model.WnsToast;

public class WnsToastBuilder extends WnsAbstractBuilder<WnsToastBuilder>
{
    private WnsToast toast;
    
    public WnsToastBuilder() {
        this.toast = new WnsToast();
    }
    
    public WnsToastBuilder getThis() {
        return this;
    }
    
    @Override
    protected WnsVisual getVisual() {
        if (this.toast.visual == null) {
            this.toast.visual = new WnsVisual();
        }
        return this.toast.visual;
    }
    
    @Override
    protected WnsBinding getBinding() {
        if (this.getVisual().binding == null) {
            this.getVisual().binding = new WnsBinding();
        }
        return this.toast.visual.binding;
    }
    
    protected WnsAudio getAudio() {
        if (this.toast.audio == null) {
            this.toast.audio = new WnsAudio();
        }
        return this.toast.audio;
    }
    
    public WnsToastBuilder launch(final String launch) {
        this.toast.launch = launch;
        return this;
    }
    
    public WnsToastBuilder duration(final String duration) {
        this.toast.duration = duration;
        return this;
    }
    
    public WnsToastBuilder bindingTemplateToastText01(final String textField1) {
        return this.bindingTemplate("ToastText01").setBindingTextFields(textField1);
    }
    
    public WnsToastBuilder bindingTemplateToastText02(final String textField1, final String textField2) {
        return this.bindingTemplate("ToastText02").setBindingTextFields(textField1, textField2);
    }
    
    public WnsToastBuilder bindingTemplateToastText03(final String textField1, final String textField2) {
        return this.bindingTemplate("ToastText03").setBindingTextFields(textField1, textField2);
    }
    
    public WnsToastBuilder bindingTemplateToastText04(final String textField1, final String textField2, final String textField3) {
        return this.bindingTemplate("ToastText04").setBindingTextFields(textField1, textField2, textField3);
    }
    
    public WnsToastBuilder bindingTemplateToastImageAndText01(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("ToastImageAndText01").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsToastBuilder bindingTemplateToastImageAndText02(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("ToastImageAndText02").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsToastBuilder bindingTemplateToastImageAndText03(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("ToastImageAndText03").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsToastBuilder bindingTemplateToastImageAndText04(final String imgSrc1, final String textField1, final String textField2, final String textField3) {
        return this.bindingTemplate("ToastImageAndText04").setBindingTextFields(textField1, textField2, textField3).setBindingImages(imgSrc1);
    }
    
    public WnsToastBuilder bindingTemplateToastGeneric(final String textField1, final String textField2) {
        return this.bindingTemplate("ToastGeneric").setBindingTextFields(textField1, textField2);
    }
    
    public WnsToastBuilder audioSrc(final String audioSrc) {
        this.getAudio().src = audioSrc;
        return this;
    }
    
    public WnsToastBuilder audioLoop(final Boolean loop) {
        this.getAudio().loop = loop;
        return this;
    }
    
    public WnsToastBuilder audioSilent(final Boolean silent) {
        this.getAudio().silent = silent;
        return this;
    }
    
    protected WnsToastCommands getCommands(final String scenario) {
        if (this.toast.commands == null || !this.toast.commands.scenario.equals(scenario)) {
            this.toast.commands = new WnsToastCommands(scenario);
        }
        return this.toast.commands;
    }
    
    public WnsToastBuilder addAlarmCommand(final String id, final String arguments) {
        this.getCommands("alarm").addCommand(id, arguments);
        return this;
    }
    
    public WnsToastBuilder addIncomingCallCommand(final String id, final String arguments) {
        this.getCommands("incomingCall").addCommand(id, arguments);
        return this;
    }
    
    public WnsToast build() {
        return this.toast;
    }
}
