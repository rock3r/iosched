package com.conferenceengineer.android.iosched.io.model.users;

import com.conferenceengineer.android.iosched.io.model.users.events.Badge;
import com.conferenceengineer.android.iosched.io.model.users.events.Registration;
import com.conferenceengineer.android.iosched.io.model.users.events.Sessions;
import com.conferenceengineer.android.iosched.io.model.users.events.Todos;

/**
 * Users.Events as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Events
{
    public Events()
    {
    }

    public Badge badge()
    {
        return new Badge();
    }

    public Registration registration()
    {
        return new Registration();
    }

    public Sessions sessions()
    {
        return new Sessions();
    }

    public Todos todos()
    {
        return new Todos();
    }



}
