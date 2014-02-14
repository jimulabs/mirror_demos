package com.jimulabs.mirrorlib.receive;

import com.jimulabs.mirrorlib.model.ResourceDirModel;

/**
 * Created by lintonye on 2013-06-29.
 */
public interface ResourceReceiver {
    public void start();

    public void stop();

    public void setReceivingCallback(Callback callback);

    /**
     * Force the receiver to refresh the resources. Probably only useful when the
     * receiver is in manual refresh only mode.
     *
     * @throws java.io.FileNotFoundException If the
     */
    public void forceRefresh() throws MissingResourcesException;

    /**
     * Set whether the resource receiver can only be refreshed manually, or whether
     * the refresh can be triggered automatically (for example, by a change on the
     * file system). Probably only useful for testing and debugging.
     *
     * @param manualOnly
     */
    public void setManualRefreshOnly(boolean manualOnly);

    public interface Callback {

        void onAllFilesReady(ResourceDirModel rm);

        void onError(String message, Throwable e);

        void onSdCardAlmostFull();
    }

    public class MissingResourcesException extends Exception {
        public MissingResourcesException() {
        }

        public MissingResourcesException(Exception e) {
            super(e);
        }
    }
}
