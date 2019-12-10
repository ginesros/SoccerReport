package es.umu.soccerreport;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedList;

public class Converters {
    @TypeConverter
    public static LinkedList<Incidencia> storedStringToMyObjects(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return null;
        }
        Type listType = new TypeToken<LinkedList<Incidencia>>(){}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String myObjectsToStoredString(LinkedList<Incidencia> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }
}

