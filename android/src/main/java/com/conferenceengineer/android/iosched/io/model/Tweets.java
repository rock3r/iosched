package com.conferenceengineer.android.iosched.io.model;

import com.conferenceengineer.android.iosched.io.ServerRequest;

import java.io.IOException;

/**
 * Class to interact with the tweets stored on the server.
 */
public class Tweets {

    public List list(String eventId)
            throws IOException
    {
        return new List(eventId);
    }


    public class List extends ServerRequest<TweetsResponse>
    {
        private String eventId;

        protected List(String eventId)
        {
            super("GET", "events/{eventId}/tweets/current", null, TweetsResponse.class);
            this.eventId = eventId;
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }
    }
}
