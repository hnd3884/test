package java.awt;

import sun.awt.AppContext;
import sun.awt.ModalExclude;

abstract class ModalEventFilter implements EventFilter
{
    protected Dialog modalDialog;
    protected boolean disabled;
    
    protected ModalEventFilter(final Dialog modalDialog) {
        this.modalDialog = modalDialog;
        this.disabled = false;
    }
    
    Dialog getModalDialog() {
        return this.modalDialog;
    }
    
    @Override
    public FilterAction acceptEvent(final AWTEvent awtEvent) {
        if (this.disabled || !this.modalDialog.isVisible()) {
            return FilterAction.ACCEPT;
        }
        final int id = awtEvent.getID();
        if ((id >= 500 && id <= 507) || (id >= 1001 && id <= 1001) || id == 201) {
            final Object source = awtEvent.getSource();
            if (!(source instanceof ModalExclude)) {
                if (source instanceof Component) {
                    Component parent_NoClientCode;
                    for (parent_NoClientCode = (Component)source; parent_NoClientCode != null && !(parent_NoClientCode instanceof Window); parent_NoClientCode = parent_NoClientCode.getParent_NoClientCode()) {}
                    if (parent_NoClientCode != null) {
                        return this.acceptWindow((Window)parent_NoClientCode);
                    }
                }
            }
        }
        return FilterAction.ACCEPT;
    }
    
    protected abstract FilterAction acceptWindow(final Window p0);
    
    void disable() {
        this.disabled = true;
    }
    
    int compareTo(final ModalEventFilter modalEventFilter) {
        final Dialog modalDialog = modalEventFilter.getModalDialog();
        for (Container container = this.modalDialog; container != null; container = container.getParent_NoClientCode()) {
            if (container == modalDialog) {
                return 1;
            }
        }
        for (Container parent_NoClientCode = modalDialog; parent_NoClientCode != null; parent_NoClientCode = parent_NoClientCode.getParent_NoClientCode()) {
            if (parent_NoClientCode == this.modalDialog) {
                return -1;
            }
        }
        for (Dialog dialog = this.modalDialog.getModalBlocker(); dialog != null; dialog = dialog.getModalBlocker()) {
            if (dialog == modalDialog) {
                return -1;
            }
        }
        for (Dialog dialog2 = modalDialog.getModalBlocker(); dialog2 != null; dialog2 = dialog2.getModalBlocker()) {
            if (dialog2 == this.modalDialog) {
                return 1;
            }
        }
        return this.modalDialog.getModalityType().compareTo(modalDialog.getModalityType());
    }
    
    static ModalEventFilter createFilterForDialog(final Dialog dialog) {
        switch (dialog.getModalityType()) {
            case DOCUMENT_MODAL: {
                return new DocumentModalEventFilter(dialog);
            }
            case APPLICATION_MODAL: {
                return new ApplicationModalEventFilter(dialog);
            }
            case TOOLKIT_MODAL: {
                return new ToolkitModalEventFilter(dialog);
            }
            default: {
                return null;
            }
        }
    }
    
    private static class ToolkitModalEventFilter extends ModalEventFilter
    {
        private AppContext appContext;
        
        ToolkitModalEventFilter(final Dialog dialog) {
            super(dialog);
            this.appContext = dialog.appContext;
        }
        
        @Override
        protected FilterAction acceptWindow(Window owner) {
            if (owner.isModalExcluded(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE)) {
                return FilterAction.ACCEPT;
            }
            if (owner.appContext != this.appContext) {
                return FilterAction.REJECT;
            }
            while (owner != null) {
                if (owner == this.modalDialog) {
                    return FilterAction.ACCEPT_IMMEDIATELY;
                }
                owner = owner.getOwner();
            }
            return FilterAction.REJECT;
        }
    }
    
    private static class ApplicationModalEventFilter extends ModalEventFilter
    {
        private AppContext appContext;
        
        ApplicationModalEventFilter(final Dialog dialog) {
            super(dialog);
            this.appContext = dialog.appContext;
        }
        
        @Override
        protected FilterAction acceptWindow(Window owner) {
            if (owner.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
                return FilterAction.ACCEPT;
            }
            if (owner.appContext == this.appContext) {
                while (owner != null) {
                    if (owner == this.modalDialog) {
                        return FilterAction.ACCEPT_IMMEDIATELY;
                    }
                    owner = owner.getOwner();
                }
                return FilterAction.REJECT;
            }
            return FilterAction.ACCEPT;
        }
    }
    
    private static class DocumentModalEventFilter extends ModalEventFilter
    {
        private Window documentRoot;
        
        DocumentModalEventFilter(final Dialog dialog) {
            super(dialog);
            this.documentRoot = dialog.getDocumentRoot();
        }
        
        @Override
        protected FilterAction acceptWindow(Window owner) {
            if (owner.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
                for (Window window = this.modalDialog.getOwner(); window != null; window = window.getOwner()) {
                    if (window == owner) {
                        return FilterAction.REJECT;
                    }
                }
                return FilterAction.ACCEPT;
            }
            while (owner != null) {
                if (owner == this.modalDialog) {
                    return FilterAction.ACCEPT_IMMEDIATELY;
                }
                if (owner == this.documentRoot) {
                    return FilterAction.REJECT;
                }
                owner = owner.getOwner();
            }
            return FilterAction.ACCEPT;
        }
    }
}
