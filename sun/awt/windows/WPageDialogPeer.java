package sun.awt.windows;

final class WPageDialogPeer extends WPrintDialogPeer
{
    WPageDialogPeer(final WPageDialog wPageDialog) {
        super(wPageDialog);
    }
    
    private native boolean _show();
    
    @Override
    public void show() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ((WPrintDialog)WPageDialogPeer.this.target).setRetVal(WPageDialogPeer.this._show());
                }
                catch (final Exception ex) {}
                ((WPrintDialog)WPageDialogPeer.this.target).setVisible(false);
            }
        }).start();
    }
}
