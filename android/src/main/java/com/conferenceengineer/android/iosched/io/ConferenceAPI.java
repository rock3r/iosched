package com.conferenceengineer.android.iosched.io;

import com.conferenceengineer.android.iosched.io.model.CheckIns;
import com.conferenceengineer.android.iosched.io.model.Events;
import com.conferenceengineer.android.iosched.io.model.Users;

/**
 * API for interacting with the server stored data.
 *
 * @author Al Sutton, Funky Android Ltd. (http://funkyandroid.com/)
 */
public class ConferenceAPI {

    public ConferenceAPI()
    {
    }

    public CheckIns checkIns()
    {
        return new CheckIns();
    }

    public Events events()
    {
        return new Events();
    }

    public Users users()
    {
        return new Users();
    }
}