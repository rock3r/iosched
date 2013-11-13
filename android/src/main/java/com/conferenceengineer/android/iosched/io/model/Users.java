package com.conferenceengineer.android.iosched.io.model;

import com.conferenceengineer.android.iosched.io.model.users.Events;

/**
 * Users as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Users
{
    public Users()
    {
    }

    public Events events()
    {
        return new Events();
    }

}
