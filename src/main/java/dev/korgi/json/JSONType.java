package dev.korgi.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class JSONType<T> {

    public static final JSONType<Integer> INTEGER = new JSONType<>(JSONObject::getInt);
    public static final JSONType<Boolean> BOOLEAN = new JSONType<>(JSONObject::getBoolean);
    public static final JSONType<JSONObject> OBJECT = new JSONType<>(JSONObject::getObject);
    public static final JSONType<String> STRING = new JSONType<>(JSONObject::getString);
    public static final JSONType<Double> DOUBLE = new JSONType<>(JSONObject::getDouble);
    public static final JSONType<List<JSONObject>> OBJECT_LIST = new JSONType<>(JSONObject::getObjectList);
    public static final JSONType<List<String>> STRING_LIST = new JSONType<>(JSONObject::getStringList);
    public static final JSONType<List<Integer>> INTEGER_LIST = new JSONType<>(JSONObject::getIntList);
    public static final JSONType<List<Double>> DOUBLE_LIST = new JSONType<>(JSONObject::getDoubleList);
    public static final JSONType<List<Boolean>> BOOLEAN_LIST = new JSONType<>(JSONObject::getBoolList);

    private final BiFunction<JSONObject, String, T> extractor;
    private final List<Map<JSONObject, String>> keys = new ArrayList<>();

    private JSONType(BiFunction<JSONObject, String, T> extractor) {
        this.extractor = extractor;
    }

    public BiFunction<JSONObject, String, T> getExtractor() {
        return extractor;
    }

    public JSONType<T> append(Map<JSONObject, String> key) {
        keys.add(key);
        return this;
    }

    public T get() {
        Map<JSONObject, String> keyMap = keys.get(0);
        JSONObject jsonObject = keyMap.keySet().iterator().next();
        return extractor.apply(jsonObject, keyMap.get(jsonObject));
    }

    public T get(int index) {
        Map<JSONObject, String> keyMap = keys.get(index);
        JSONObject jsonObject = keyMap.keySet().iterator().next();
        return extractor.apply(jsonObject, keyMap.get(jsonObject));
    }

    public T get(JSONObject jsonObject, String key) {
        return extractor.apply(jsonObject, key);
    }
}
