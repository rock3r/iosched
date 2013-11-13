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

import com.conferenceengineer.android.iosched.util.ParserUtils;

import java.util.TimeZone;

public class Config {
    // Log tag
    public static final String LOG_TAG = "DCNL2013";

    // General configuration
    public static final int CONFERENCE_YEAR = 2013;

    // Conference times
    public static final TimeZone CONFERENCE_TIME_ZONE = TimeZone.getTimeZone("Europe/Amsterdam");

    public static final long CONFERENCE_START_MILLIS = ParserUtils.parseTime(
            "2013-11-25T08:30:00.000+01:00");
    public static final long CONFERENCE_END_MILLIS = ParserUtils.parseTime(
            "2013-11-26T16:15:00.000+01:00");

    // OAuth 2.0 related config
    public static final String APP_NAME = "DroidconNL2013-Android";

    // Conference API-specific config
    public static final String EVENT_ID = "droidconnl2013";
    public static final String CONFERENCE_IMAGE_PREFIX_URL = "http://www.droidcon.nl/wp-content/uploads/";

    // Conference public WiFi AP parameters
    public static final String WIFI_SSID = "Droidcon";
    public static final String WIFI_PASSPHRASE = "DroidconW1F1";

    // Conference hashtag
    public static final String CONFERENCE_HASHTAG = "#droidconnl";

    // GCM config
    // TODO: Add your GCM information here.
    public static final String GCM_SERVER_URL = "https://YOUR_GCM_APP_ID_HERE.appspot.com";
    public static final String GCM_SENDER_ID = "YOUR_GCM_SENDER_ID_HERE";
    public static final String GCM_API_KEY = "YOUR_GCM_API_KEY_HERE";
}
