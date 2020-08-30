package com.stuffbox.webscraper.downloader;


import static com.stuffbox.webscraper.downloader.Sample.UriSample.ACTION_VIEW_LIST;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.UUID;

/* package */public abstract class Sample {
    public static final String EXTENSION_EXTRA = "extension";
    public static final String IS_LIVE_EXTRA = "is_live";
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String SPHERICAL_STEREO_MODE_EXTRA = "spherical_stereo_mode";
    public static final String URI_EXTRA = "uri";
    public static final String DRM_SCHEME_EXTRA = "drm_scheme";
    public static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES_EXTRA = "drm_key_request_properties";
    public static final String DRM_MULTI_SESSION_EXTRA = "drm_multi_session";
    public static final String PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders";
    public static final String TUNNELING_EXTRA = "tunneling";
    public static final String SUBTITLE_URI_EXTRA = "subtitle_uri";
    public static final String SUBTITLE_MIME_TYPE_EXTRA = "subtitle_mime_type";
    public static final String SUBTITLE_LANGUAGE_EXTRA = "subtitle_language";
    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";

    public static final class UriSample extends Sample {
        public static final String ACTION_VIEW_LIST =
                "com.google.android.exoplayer.demo.action.VIEW_LIST";
        public static UriSample createFromIntent(Uri uri, Intent intent, String extrasKeySuffix) {
            String extension = intent.getStringExtra(EXTENSION_EXTRA + extrasKeySuffix);
            String adsTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA + extrasKeySuffix);
            boolean isLive =
                    intent.getBooleanExtra(IS_LIVE_EXTRA + extrasKeySuffix, /* defaultValue= */ false);
            Uri adTagUri = adsTagUriString != null ? Uri.parse(adsTagUriString) : null;
            return new UriSample(
                    /* name= */ null,
                    uri,
                    extension,
                    isLive,
                    DrmInfo.createFromIntent(intent, extrasKeySuffix),
                    adTagUri,
                    /* sphericalStereoMode= */ null,
                    SubtitleInfo.createFromIntent(intent, extrasKeySuffix));
        }

        public final Uri uri;
        public final String extension;
        public final boolean isLive;
        public final DrmInfo drmInfo;
        public final Uri adTagUri;
        @Nullable public final String sphericalStereoMode;
        @Nullable SubtitleInfo subtitleInfo;

        public UriSample(
                String name,
                Uri uri,
                String extension,
                boolean isLive,
                DrmInfo drmInfo,
                Uri adTagUri,
                @Nullable String sphericalStereoMode,
                @Nullable SubtitleInfo subtitleInfo) {
            super(name);
            this.uri = uri;
            this.extension = extension;
            this.isLive = isLive;
            this.drmInfo = drmInfo;
            this.adTagUri = adTagUri;
            this.sphericalStereoMode = sphericalStereoMode;
            this.subtitleInfo = subtitleInfo;
        }

        @Override
        public void addToIntent(Intent intent) {
            intent.setAction(ACTION_VIEW).setData(uri);
            intent.putExtra(Sample.IS_LIVE_EXTRA, isLive);
            intent.putExtra(SPHERICAL_STEREO_MODE_EXTRA, sphericalStereoMode);
            addPlayerConfigToIntent(intent, /* extrasKeySuffix= */ "");
        }

        public void addToPlaylistIntent(Intent intent, String extrasKeySuffix) {
            intent.putExtra(URI_EXTRA + extrasKeySuffix, uri.toString());
            intent.putExtra(IS_LIVE_EXTRA + extrasKeySuffix, isLive);
            addPlayerConfigToIntent(intent, extrasKeySuffix);
        }

        private void addPlayerConfigToIntent(Intent intent, String extrasKeySuffix) {
            intent
                    .putExtra(EXTENSION_EXTRA + extrasKeySuffix, extension)
                    .putExtra(
                            AD_TAG_URI_EXTRA + extrasKeySuffix, adTagUri != null ? adTagUri.toString() : null);
            if (drmInfo != null) {
                drmInfo.addToIntent(intent, extrasKeySuffix);
            }
            if (subtitleInfo != null) {
                subtitleInfo.addToIntent(intent, extrasKeySuffix);
            }
        }
    }

    public static final class PlaylistSample extends Sample {

        public final UriSample[] children;

        public PlaylistSample(String name, UriSample... children) {
            super(name);
            this.children = children;
        }

        @Override
        public void addToIntent(Intent intent) {
            intent.setAction(ACTION_VIEW_LIST);
            for (int i = 0; i < children.length; i++) {
                children[i].addToPlaylistIntent(intent, /* extrasKeySuffix= */ "_" + i);
            }
        }
    }

    public static final class DrmInfo {

        public static DrmInfo createFromIntent(Intent intent, String extrasKeySuffix) {
            String schemeKey = DRM_SCHEME_EXTRA + extrasKeySuffix;
            String schemeUuidKey = DRM_SCHEME_UUID_EXTRA + extrasKeySuffix;
            if (!intent.hasExtra(schemeKey) && !intent.hasExtra(schemeUuidKey)) {
                return null;
            }
            String drmSchemeExtra =
                    intent.hasExtra(schemeKey)
                            ? intent.getStringExtra(schemeKey)
                            : intent.getStringExtra(schemeUuidKey);
            UUID drmScheme = Util.getDrmUuid(drmSchemeExtra);
            String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL_EXTRA + extrasKeySuffix);
            String[] keyRequestPropertiesArray =
                    intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA + extrasKeySuffix);
            boolean drmMultiSession =
                    intent.getBooleanExtra(DRM_MULTI_SESSION_EXTRA + extrasKeySuffix, false);
            return new DrmInfo(drmScheme, drmLicenseUrl, keyRequestPropertiesArray, drmMultiSession);
        }

        public final UUID drmScheme;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;
        public final boolean drmMultiSession;

        public DrmInfo(
                UUID drmScheme,
                String drmLicenseUrl,
                String[] drmKeyRequestProperties,
                boolean drmMultiSession) {
            this.drmScheme = drmScheme;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.drmMultiSession = drmMultiSession;
        }

        public void addToIntent(Intent intent, String extrasKeySuffix) {
            Assertions.checkNotNull(intent);
            intent.putExtra(DRM_SCHEME_EXTRA + extrasKeySuffix, drmScheme.toString());
            intent.putExtra(DRM_LICENSE_URL_EXTRA + extrasKeySuffix, drmLicenseUrl);
            intent.putExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA + extrasKeySuffix, drmKeyRequestProperties);
            intent.putExtra(DRM_MULTI_SESSION_EXTRA + extrasKeySuffix, drmMultiSession);
        }
    }

    public static final class SubtitleInfo {

        @Nullable
        public static SubtitleInfo createFromIntent(Intent intent, String extrasKeySuffix) {
            if (!intent.hasExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix)) {
                return null;
            }
            return new SubtitleInfo(
                    Uri.parse(intent.getStringExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix)),
                    intent.getStringExtra(SUBTITLE_MIME_TYPE_EXTRA + extrasKeySuffix),
                    intent.getStringExtra(SUBTITLE_LANGUAGE_EXTRA + extrasKeySuffix));
        }

        public final Uri uri;
        public final String mimeType;
        @Nullable public final String language;

        public SubtitleInfo(Uri uri, String mimeType, @Nullable String language) {
            this.uri = Assertions.checkNotNull(uri);
            this.mimeType = Assertions.checkNotNull(mimeType);
            this.language = language;
        }

        public void addToIntent(Intent intent, String extrasKeySuffix) {
            intent.putExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix, uri.toString());
            intent.putExtra(SUBTITLE_MIME_TYPE_EXTRA + extrasKeySuffix, mimeType);
            intent.putExtra(SUBTITLE_LANGUAGE_EXTRA + extrasKeySuffix, language);
        }
    }

    public static Sample createFromIntent(Intent intent) {
        if (ACTION_VIEW_LIST.equals(intent.getAction())) {
            ArrayList<String> intentUris = new ArrayList<>();
            int index = 0;
            while (intent.hasExtra(URI_EXTRA + "_" + index)) {
                intentUris.add(intent.getStringExtra(URI_EXTRA + "_" + index));
                index++;
            }
            UriSample[] children = new UriSample[intentUris.size()];
            for (int i = 0; i < children.length; i++) {
                Uri uri = Uri.parse(intentUris.get(i));
                children[i] = UriSample.createFromIntent(uri, intent, /* extrasKeySuffix= */ "_" + i);
            }
            return new PlaylistSample(/* name= */ null, children);
        } else {
            return UriSample.createFromIntent(intent.getData(), intent, /* extrasKeySuffix= */ "");
        }
    }

    @Nullable public final String name;

    public Sample(String name) {
        this.name = name;
    }

    public abstract void addToIntent(Intent intent);
}