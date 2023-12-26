package com.chenmeng.project.common.utils;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Json工具类
 *
 * @author 沉梦听雨
 **/
@Slf4j
public class JsonUtil {

    public static <T> String toJson(T value) {
        try {
            return getInstance().writeValueAsString(value);
        } catch (Exception var2) {
            log.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static boolean canSerialize(@Nullable Object value) {
        return value == null ? true : getInstance().canSerialize(value.getClass());
    }

    public static ObjectMapper getInstance() {
        return JsonUtil.JacksonHolder.INSTANCE;
    }

    private static class JacksonHolder {
        private static final ObjectMapper INSTANCE = new JacksonObjectMapper();

        private JacksonHolder() {
        }
    }

    private static class JacksonObjectMapper extends ObjectMapper {
        private static final long serialVersionUID = 4288193147502386170L;
        private static final Locale CHINA;

        public JacksonObjectMapper(ObjectMapper src) {
            super(src);
        }

        public JacksonObjectMapper() {
            super.setLocale(CHINA);
            super.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            super.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            super.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA));
            super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            super.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
            super.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
            super.findAndRegisterModules();
            super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            super.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
            super.getDeserializationConfig().withoutFeatures(new DeserializationFeature[]{DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES});
            super.findAndRegisterModules();
        }

        @Override
        public ObjectMapper copy() {
            return new JacksonObjectMapper(this);
        }

        static {
            CHINA = Locale.CHINA;
        }
    }
}
