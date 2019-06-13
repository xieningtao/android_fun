package com.xnt.sglog.prettylog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.DEBUG;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;


public class TxtFormatStrategy implements FormatStrategy {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_LINE_REPLACEMENT = " <br> ";
    private static final String SEPARATOR = ",";

    @NonNull
    private final Date date;
    @NonNull
    private final SimpleDateFormat dateFormat;
    @NonNull
    private final LogStrategy logStrategy;
    @Nullable
    private final String tag;

    private TxtFormatStrategy(@NonNull TxtFormatStrategy.Builder builder) {

        date = builder.date;
        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    @NonNull
    public static TxtFormatStrategy.Builder newBuilder() {
        return new TxtFormatStrategy.Builder();
    }

    @Override
    public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {

        String tag = formatTag(onceOnlyTag);

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

        // machine-readable date/time
        builder.append(Long.toString(date.getTime()));

        // human-readable date/time
        builder.append(SEPARATOR);
        builder.append(dateFormat.format(date));

        // level
        builder.append(SEPARATOR);
        builder.append(logLevel(priority));

        // tag
        builder.append(SEPARATOR);
        builder.append(tag);

        // message
        if (message.contains(NEW_LINE)) {
            // a new line would break the CSV format, so we replace it here
            message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
        }
        builder.append(SEPARATOR);
        builder.append(message);

        // new line
        builder.append(NEW_LINE);

        logStrategy.log(priority, tag, builder.toString());
    }

    @Nullable
    private String formatTag(@Nullable String tag) {
        if (!TextUtils.isEmpty(tag) && !tag.equals(this.tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static final class Builder {
        private static final int MAX_BYTES = 500 * 1024; // 500K averages to a 4000 lines per file

        String rootLogPath;
        Date date;
        SimpleDateFormat dateFormat;
        LogStrategy logStrategy;
        String tag = "PRETTY_LOGGER";

        private Builder() {
        }

        @NonNull
        public TxtFormatStrategy.Builder date(@Nullable Date val) {
            date = val;
            return this;
        }

        @NonNull
        public TxtFormatStrategy.Builder dateFormat(@Nullable SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        @NonNull
        public TxtFormatStrategy.Builder logStrategy(@Nullable LogStrategy val) {
            logStrategy = val;
            return this;
        }

        public TxtFormatStrategy.Builder rootLogPath(String rootLogPath) {
            this.rootLogPath = rootLogPath;
            return this;
        }

        @NonNull
        public TxtFormatStrategy.Builder tag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        @NonNull
        public TxtFormatStrategy build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.UK);
            }
            if (logStrategy == null) {
                logStrategy = HTDiskLogStrategy.createInstance(rootLogPath, MAX_BYTES);
            }
            return new TxtFormatStrategy(this);
        }
    }

    static String logLevel(int value) {
        switch (value) {
            case VERBOSE:
                return "VERBOSE";
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARN:
                return "WARN";
            case ERROR:
                return "ERROR";
            case ASSERT:
                return "ASSERT";
            default:
                return "UNKNOWN";
        }
    }
}
