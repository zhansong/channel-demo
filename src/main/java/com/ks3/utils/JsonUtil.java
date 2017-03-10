package com.ks3.utils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Slf4j
public class JsonUtil {
    static ObjectMapper objectMapper = new ObjectMapper();
    static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(DateTime.class, new DateTimeSerializer());
        module.addDeserializer(DateTime.class, new DatetimeDeserializer());
        objectMapper.registerModule(module);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T json2object(String _json, Class cls) {
        try {
            if (_json == null || _json.equals(""))
                _json = "{}";
            return (T) objectMapper.readValue(_json, cls);
        } catch (IOException e) {
            log.warn("json2object error", e);
        }
        return null;
    }

    public static String object2json(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("object2json error", e);
        }
        return null;
    }

    public static String object2jsonWithoutNull(Object obj) {
        String json = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
    //序列化的时候可以将datetime序列化为字符串，更容易读
    private static class DateTimeSerializer extends JsonSerializer<DateTime> {
        @Override
        public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(value.toString(dateFormatter));
        }
    }
    //返序列化将字符串转化为datetime
    private static class DatetimeDeserializer extends JsonDeserializer<DateTime> {

        @Override
        public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            String s = node.asText();
            DateTime parse = DateTime.parse(s, dateFormatter);
            return parse;
        }
    }
}
