/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.conferenceengineer.android.iosched;

import android.net.Uri;
import com.conferenceengineer.android.iosched.util.ParserUtils;

import java.util.TimeZone;

public class Config {
    // Log tag
    public static final String LOG_TAG = "DCNL2013";

    // General configuration
    public static final int CONFERENCE_YEAR = 2013;

    // Conference times
    public static final TimeZone CONFERENCE_TIME_ZONE = TimeZone.getTimeZone("Europe/Amsterdam");

    public static final long CONFERENCE_START_MILLIS = ParserUtils.parseTime("2014-02-06T08:30:00.000+01:00");  // TODO: confirm this date
    public static final long CONFERENCE_END_MILLIS = ParserUtils.parseTime("2014-02-09T16:30:00.000+01:00");    // TODO: confirm this date

    // Used in {@link UIUtils#tryTranslateHttpIntent(android.app.Activity)}.
    public static final Uri SESSION_DETAIL_WEB_URL_PREFIX = Uri.parse("http://it.droidcon.com/2014/prelineup/");

    // Feedback URL
    public static final String FEEDBACK_URL = "http://it.droidcon.com/2014/contact/";

    // OAuth 2.0 related config
    public static final String APP_NAME = "DroidconIT2014-Android";

    // Conference API-specific config
    public static final String EVENT_ID = "434";
    public static final String CONFERENCE_IMAGE_PREFIX_URL = "http://www.droidcon.it/wp-content/uploads/"; // TODO: confirm this URL

    // Conference public WiFi AP parameters
    public static final String WIFI_SSID = "droidconIT2014";           // TODO: confirm this data
    public static final String WIFI_PASSPHRASE = "it's a-me, Mario!";  // TODO: confirm this data

    // Conference hashtag
    public static final String CONFERENCE_HASHTAG = "#droidconit";

    // GCM config
    // TODO: Add your GCM information here.
    public static final String GCM_SERVER_URL = "https://YOUR_GCM_APP_ID_HERE.appspot.com";
    public static final String GCM_SENDER_ID = "YOUR_GCM_SENDER_ID_HERE";
    public static final String GCM_API_KEY = "YOUR_GCM_API_KEY_HERE";
}
