package com.xnt.sglog.prettylog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;

public class PlainFormatStrategy implements FormatStrategy {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_LINE_REPLACEMENT = " <br> ";
    private static final String SEPARATOR = ",";

    @NonNull private final LogStrategy logStrategy;
    @Nullable private final String tag;

    private PlainFormatStrategy(@NonNull PlainFormatStrategy.Builder builder) {

        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    @NonNull public static PlainFormatStrategy.Builder newBuilder() {
        return new PlainFormatStrategy.Builder();
    }

    @Override
    public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {

        logStrategy.log(priority, onceOnlyTag,message);
    }


    public static final class Builder {
        private static final int MAX_BYTES = 500 * 1024; // 500K averages to a 4000 lines per file

        LogStrategy logStrategy;
        String tag = "PRETTY_LOGGER";

        private Builder() {
        }


        @NonNull public PlainFormatStrategy.Builder logStrategy(@Nullable LogStrategy val) {
            logStrategy = val;
            return this;
        }

        @NonNull public PlainFormatStrategy.Builder tag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        @NonNull public PlainFormatStrategy build() {
            if (logStrategy == null) {
                throw new NullPointerException("logStrategy is null");
            }
            return new PlainFormatStrategy(this);
        }
    }
}
