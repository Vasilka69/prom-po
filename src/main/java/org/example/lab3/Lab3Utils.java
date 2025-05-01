package org.example.lab3;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Lab3Utils {

    private static final Gson gson = new Gson();

    public static Map<String, Long> parseMapFromString(String jsonString) {
        Type mapType = new TypeToken<Map<String, Long>>(){}.getType();
        return gson.fromJson(jsonString, mapType);
    }

    public static String objectToJsonString(Object object) {
        return gson.toJson(object);
    }
}
