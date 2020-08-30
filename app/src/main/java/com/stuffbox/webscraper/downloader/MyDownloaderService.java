package com.stuffbox.webscraper.downloader;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import com.stuffbox.webscraper.R;
import com.stuffbox.webscraper.application.AnimeWatcherApplication;

import java.util.List;

public class MyDownloaderService extends DownloadService {
    private static final int JOB_ID = 1;
    public static final String DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel";

    private static final int FOREGROUND_NOTIFICATION_ID = 1;
    public MyDownloaderService() {
        super(
                FOREGROUND_NOTIFICATION_ID,
                DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                DOWNLOAD_NOTIFICATION_CHANNEL_ID,
                R.string.exo_download_notification_channel_name,
                /* channelDescriptionResourceId= */ 0);
    }

    @Override
    protected DownloadManager getDownloadManager() {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        AnimeWatcherApplication application = (AnimeWatcherApplication) getApplication();
        DownloadManager downloadManager = application.getDownloadManager();
        DownloadNotificationHelper downloadNotificationHelper =
                application.getDownloadNotificationHelper();
        downloadManager.addListener(
                new TerminalStateNotificationHelper(
                        this, downloadNotificationHelper, FOREGROUND_NOTIFICATION_ID + 1));
        return downloadManager;
    }
    @Override
    protected PlatformScheduler getScheduler() {
        return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
    }
    @Override
    protected Notification getForegroundNotification(List<Download> downloads) {
        return ((AnimeWatcherApplication) getApplication())
                .getDownloadNotificationHelper()
                .buildProgressNotification(
                        R.drawable.ic_download, /* contentIntent= */ null, /* message= */ null, downloads);
    }
    private static final class TerminalStateNotificationHelper implements DownloadManager.Listener {

        private final Context context;
        private final DownloadNotificationHelper notificationHelper;

        private int nextNotificationId;

        public TerminalStateNotificationHelper(
                Context context, DownloadNotificationHelper notificationHelper, int firstNotificationId) {
            this.context = context.getApplicationContext();
            this.notificationHelper = notificationHelper;
            nextNotificationId = firstNotificationId;
        }

        @Override
        public void onDownloadChanged(DownloadManager manager, Download download) {
            Notification notification;
            if (download.state == Download.STATE_COMPLETED) {
                notification =
                        notificationHelper.buildDownloadCompletedNotification(
                                R.drawable.ic_download_done,
                                /* contentIntent= */ null,
                                Util.fromUtf8Bytes(download.request.data));
            } else if (download.state == Download.STATE_FAILED) {
                notification =
                        notificationHelper.buildDownloadFailedNotification(
                                R.drawable.ic_download_done,
                                /* contentIntent= */ null,
                                Util.fromUtf8Bytes(download.request.data));
            } else {
                return;
            }
            NotificationUtil.setNotification(context, nextNotificationId++, notification);
        }
    }
}
