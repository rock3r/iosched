package com.google.samples.apps.iosched.droidconitaly15;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.samples.apps.iosched.BuildConfig;
import com.google.samples.apps.iosched.Config;
import com.google.samples.apps.iosched.droidconitaly15.model.DI15Session;
import com.google.samples.apps.iosched.droidconitaly15.model.DI15Speaker;
import com.google.samples.apps.iosched.io.model.Block;
import com.google.samples.apps.iosched.io.model.Room;
import com.google.samples.apps.iosched.io.model.Session;
import com.google.samples.apps.iosched.io.model.Speaker;
import com.google.samples.apps.iosched.io.model.Tag;
import com.google.samples.apps.iosched.provider.ScheduleContract;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_BLOCKS;
import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_ROOMS;
import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_SESSIONS;
import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_SPEAKERS;
import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_TAGS;

public final class JsonTransformer {

    private static final Set<String> BLOCK_SESSIONS;
    private static final Set<String> SPECIAL_SESSIONS;
    private static final HashMap<String, String> ROOMS_TO_COLORS;
    private static final String DEFAULT_ROOM_COLOR;

    static {
        BLOCK_SESSIONS = new HashSet<>(2);
        BLOCK_SESSIONS.add("http://it.droidcon.com/2015/sessions/welcome-2/");
        BLOCK_SESSIONS.add("http://it.droidcon.com/2015/sessions/chiusura/");

        SPECIAL_SESSIONS = new HashSet<>(3);
        SPECIAL_SESSIONS.add("http://it.droidcon.com/2015/sessions/keynote-2/");
        SPECIAL_SESSIONS.add("http://it.droidcon.com/2015/sessions/barcamp-organization-2/");
        SPECIAL_SESSIONS.add("http://it.droidcon.com/2015/sessions/droidcon-party/");

        ROOMS_TO_COLORS = new HashMap<>(8);
        ROOMS_TO_COLORS.put("sala 500", "#8e24aa");
        ROOMS_TO_COLORS.put("sala lisbona", "#2a56c6");
        ROOMS_TO_COLORS.put("sala londra", "#558b2f");
        ROOMS_TO_COLORS.put("sala madrid", "#607d8b");
        ROOMS_TO_COLORS.put("sala parigi", "#0d904f");
        ROOMS_TO_COLORS.put("sala instabul", "#3367d6");
        ROOMS_TO_COLORS.put("sala copenaghen", "#0288d1");
        ROOMS_TO_COLORS.put("caffè del progresso", "#00897b");
        DEFAULT_ROOM_COLOR = "#3f51b5";
    }

    private static final ThreadLocal<DateFormat> DATE_PARSER = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            // "9 April 2015 11:00"
            final String format = "d MMMM yyyy kk:mm";
            final DateFormat dateParser = new SimpleDateFormat(format, Locale.US);
            dateParser.setTimeZone(TimeZone.getTimeZone(BuildConfig.CONFERENCE_TIMEZONE));
            return dateParser;
        }
    };

    private static final ThreadLocal<DateFormat> DATE_FORMATTER_SESSIONS = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            // "2014-06-26T00:00:00Z"
            final String format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            final DateFormat dateFormatter = new SimpleDateFormat(format, Locale.US);
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormatter;
        }
    };

    private static final ThreadLocal<DateFormat> DATE_FORMATTER_BLOCKS = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            // "2014-06-26T00:00:00.000Z"
            final String format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            final DateFormat dateFormatter = new SimpleDateFormat(format, Locale.US);
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormatter;
        }
    };

    private JsonTransformer() {
    }

    @Nullable
    public static String[] transformJson(@Nullable String[] dataBodies) {

        if (dataBodies != null) {
            final Gson gson = new GsonBuilder().create();
            for (int i = 0, end = dataBodies.length; i < end; i++) {
                dataBodies[i] = transformJson(dataBodies[i], gson);
            }
        }
        return dataBodies;
    }

    @Nullable
    private static String transformJson(@Nullable String dataBody, @NonNull Gson gson) {

        if (dataBody == null) return null;

        final Map<String, Speaker> speakers = new HashMap<>();
        final Map<String, Room> rooms = new HashMap<>();
        final Map<String, Tag> tags = new HashMap<>();
        final List<Block> blocks = new ArrayList<>();
        final List<Session> sessions = new ArrayList<>();

        // read all the sessions incrementally and aggregate the data in the new format
        final JsonReader reader = new JsonReader(new StringReader(dataBody));
        try {
            reader.setLenient(true); // To err is human

            // the whole file is a single JSON object
            reader.beginObject();
            while (reader.hasNext()) {
                if ("sessions".equals(reader.nextName())) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        final DI15Session di15Session = gson.fromJson(reader, DI15Session.class);
                        if (di15Session != null) {
                            transformJson(di15Session, speakers, rooms, tags, blocks, sessions);
                        }
                    }
                    break;
                } else {
                    reader.skipValue();
                }
            }

            addBlocksManually(blocks);

        } catch (JsonParseException | ParseException | IOException e) {
            if (BuildConfig.DEBUG) {
                Log.w("JsonTransformer", "Error while transforming Json.", e);
            }
            return dataBody; // don't transform if it's not what we expect
        } finally {
            try {
                reader.close();
            } catch (IOException ignore) {
            }
        }

        final Map<String, Object> transformed = new HashMap<>();
        transformed.put(DATA_KEY_SPEAKERS, speakers.values());
        transformed.put(DATA_KEY_ROOMS, rooms.values());
        transformed.put(DATA_KEY_BLOCKS, blocks);
        transformed.put(DATA_KEY_SESSIONS, sessions);
        transformed.put(DATA_KEY_TAGS, tags.values());
        return gson.toJson(transformed);
    }

    private static void transformJson(
            @NonNull DI15Session di15Session,
            @NonNull Map<String, Speaker> speakers,
            @NonNull Map<String, Room> rooms,
            @NonNull Map<String, Tag> tags,
            @NonNull List<Block> blocks,
            @NonNull List<Session> sessions)
            throws ParseException {

        // *** BLOCKS ***

        final String sessionUrl =
                di15Session.url == null ? null : di15Session.url.toLowerCase(Locale.US);
        final String roomName = escapeHtml(di15Session.location);
        if (sessionUrl != null && BLOCK_SESSIONS.contains(sessionUrl)) {
            addBlock(di15Session.post_title,
                    roomName,
                    di15Session.date,
                    di15Session.time,
                    di15Session.end_time,
                    false,
                    DATE_PARSER.get(),
                    DATE_FORMATTER_BLOCKS.get(),
                    blocks);
            return;
        }

        // *** SPEAKERS ***

        final List<DI15Speaker> di15speakers = di15Session.speakers;
        if (di15speakers != null) {
            for (DI15Speaker di15Speaker : di15speakers) {
                if (!speakers.containsKey(di15Speaker.speaker_id)) {
                    final Speaker speaker = new Speaker();
                    speaker.id = di15Speaker.speaker_id;
                    speaker.name = escapeHtml(di15Speaker.post_title);
                    speaker.company = escapeHtml(di15Speaker.header);
                    speaker.bio = escapeHtml(di15Speaker.bio);
                    speaker.thumbnailUrl = di15Speaker.post_image;
                    speaker.plusoneUrl = di15Speaker.url;
                    speakers.put(speaker.id, speaker);
                }
            }
        }

        // *** ROOMS ***

        if (roomName != null) {
            if (!rooms.containsKey(roomName)) {
                final Room room = new Room();
                room.id = roomName;
                room.name = roomName;
                rooms.put(roomName, room);
            }
        }

        // *** TAGS ***

        final List<String> di15Tags =
                di15Session.track != null ? di15Session.track : new ArrayList<String>();
        if (sessionUrl != null && SPECIAL_SESSIONS.contains(sessionUrl)) {
            di15Tags.add(Config.Tags.SPECIAL_KEYNOTE);
        }
        for (String di15tag : di15Tags) {
            if (!tags.containsKey(di15tag)) {
                final Tag tag = new Tag();
                tag.category = "TOPIC";
                tag.name = di15tag;
                tag.tag = di15tag;
            }
        }

        // *** SESSIONS ***

        final Session session = new Session();
        session.id = sessionUrl;
        session.title = escapeHtml(di15Session.post_title);
        session.description = escapeHtml(di15Session.content);
        session.url = sessionUrl;
        session.room = roomName;
        session.color = getColorFrom(roomName);
        session.tags = di15Tags.toArray(new String[di15Tags.size()]);

        // speakers
        final int size = di15speakers == null ? 0 : di15speakers.size();
        final String[] speakerIds = new String[size];
        for (int i = 0; i < size; i++) {
            speakerIds[i] = di15speakers.get(i).speaker_id;
        }
        session.speakers = speakerIds;

        // timestamps
        final DateFormat dateParser = DATE_PARSER.get();
        final DateFormat dateFormatterSessions = DATE_FORMATTER_SESSIONS.get();
        session.startTimestamp =
                convertDateTime(di15Session.date, di15Session.time,
                        dateParser, dateFormatterSessions);
        session.endTimestamp =
                convertDateTime(di15Session.date, di15Session.end_time,
                        dateParser, dateFormatterSessions);

        sessions.add(session);
    }

    /**
     * MOAR COLORZ PLS!
     */
    private static String getColorFrom(String roomName) {
        if (roomName == null) {
            return DEFAULT_ROOM_COLOR;
        }

        String roomColor = ROOMS_TO_COLORS.get(roomName.trim().toLowerCase(Locale.US));

        if (roomColor == null) {
            roomColor = DEFAULT_ROOM_COLOR;
        }

        return roomColor;
    }

    private static void addBlocksManually(@NonNull Collection<Block> blocks)
            throws ParseException {

        final DateFormat dateParser = DATE_PARSER.get();
        final DateFormat dateFormatterBlocks = DATE_FORMATTER_BLOCKS.get();
        String day;

        day = "9 April 2015";

        addBlock("", "", day, "11:00", "12:00", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "12:00", "12:50", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "12:50", "13:30", true,
                dateParser, dateFormatterBlocks, blocks);

        addBlock("Lunch", "", day, "13:30", "14:30", false,
                dateParser, dateFormatterBlocks, blocks);

        addBlock("", "", day, "14:30", "15:10", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "15:10", "16:10", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "16:10", "17:10", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "17:10", "18:00", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "18:00", "19:00", true,
                dateParser, dateFormatterBlocks, blocks);

        day = "10 April 2015";

        addBlock("", "", day, "09:00", "09:50", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "09:50", "10:30", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "10:30", "11:40", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "11:40", "12:20", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "12:20", "13:00", true,
                dateParser, dateFormatterBlocks, blocks);

        addBlock("Lunch", "", day, "13:00", "14:00", false,
                dateParser, dateFormatterBlocks, blocks);

        addBlock("", "", day, "14:00", "14:50", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "14:50", "15:40", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "15:40", "16:40", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "16:40", "17:30", true,
                dateParser, dateFormatterBlocks, blocks);
        addBlock("", "", day, "17:30", "18:20", true,
                dateParser, dateFormatterBlocks, blocks);
    }

    private static void addBlock(
            @NonNull String title,
            @NonNull String subtitle,
            @NonNull String date,
            @NonNull String startTime,
            @NonNull String endTime,
            boolean freeOrBreak,
            @NonNull DateFormat dateParser,
            @NonNull DateFormat dateFormatter,
            @NonNull Collection<Block> blocks)
            throws ParseException {

        final Block block = new Block();
        block.start = convertDateTime(date, startTime, dateParser, dateFormatter);
        block.end = convertDateTime(date, endTime, dateParser, dateFormatter);
        block.title = title;
        block.subtitle = subtitle;
        block.type = freeOrBreak
                ? ScheduleContract.Blocks.BLOCK_TYPE_FREE
                : ScheduleContract.Blocks.BLOCK_TYPE_BREAK;
        blocks.add(block);
    }

    @Nullable
    private static String convertDateTime(
            @NonNull String date,
            @NonNull String time,
            @NonNull DateFormat dateParser,
            @NonNull DateFormat dateFormatter)
            throws ParseException {

        time = time.replace('.', ':');
        return dateFormatter.format(dateParser.parse(date + " " + time));
    }

    /**
     * From html to plain text, yo.
     */
    @Nullable
    private static String escapeHtml(@Nullable String html) {
        return html == null ? null : Html.fromHtml(html).toString(); // THE HORROR
    }
}
