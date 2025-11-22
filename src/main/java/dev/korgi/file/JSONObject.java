package dev.korgi.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONObject {

    private final JsonNode root;

    public JSONObject(JsonNode root) {
        this.root = root;
    }

    public static JSONObject parse(Path path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(path.toFile());
            return new JSONObject(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + path, e);
        }
    }

    public String getString(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isTextual()) {
            return node.asText();
        }
        throw new IllegalArgumentException("Key not found or not a string: " + key);
    }

    public int getInt(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isInt()) {
            return node.asInt();
        }
        throw new IllegalArgumentException("Key not found or not an integer: " + key);
    }

    public boolean getBoolean(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isBoolean()) {
            return node.asBoolean();
        }
        throw new IllegalArgumentException("Key not found or not a boolean: " + key);
    }

    public double getDouble(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isDouble()) {
            return node.asDouble();
        }
        throw new IllegalArgumentException("Key not found or not a double: " + key);
    }

    public JSONObject getObject(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isObject()) {
            return new JSONObject(node);
        }
        throw new IllegalArgumentException("Key not found or not an object: " + key);
    }

    public List<String> getStringList(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode element : node) {
                if (element.isTextual()) {
                    list.add(element.asText());
                }
            }
            return list;
        }
        throw new IllegalArgumentException("Key not found or not an array: " + key);
    }

    public List<Integer> getIntList(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isArray()) {
            List<Integer> list = new ArrayList<>();
            for (JsonNode element : node) {
                if (element.isInt()) {
                    list.add(element.asInt());
                }
            }
            return list;
        }
        throw new IllegalArgumentException("Key not found or not an array: " + key);
    }

    public List<Boolean> getBoolList(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isArray()) {
            List<Boolean> list = new ArrayList<>();
            for (JsonNode element : node) {
                if (element.booleanValue()) {
                    list.add(element.booleanValue());
                }
            }
            return list;
        }
        throw new IllegalArgumentException("Key not found or not an array: " + key);
    }

    public List<JSONObject> getObjectList(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isArray()) {
            List<JSONObject> list = new ArrayList<>();
            for (JsonNode element : node) {
                if (element.isObject()) {
                    list.add(new JSONObject(element));
                }
            }
            return list;
        }
        throw new IllegalArgumentException("Key not found or not an array: " + key);
    }

    public List<Double> getDoubleList(String key) {
        JsonNode node = root.get(key);
        if (node != null && node.isArray()) {
            List<Double> list = new ArrayList<>();
            for (JsonNode element : node) {
                if (element.isDouble()) {
                    list.add(element.asDouble());
                }
            }
            return list;
        }
        throw new IllegalArgumentException("Key not found or not an array: " + key);
    }

    public boolean isNull(String key) {
        return root.has(key) && root.get(key).isNull();
    }

    public boolean hasKey(String key) {
        return root.has(key);
    }

    public void addString(String key, String value) {
        if (root instanceof ObjectNode objectNode) {
            objectNode.put(key, value);
        } else {
            throw new UnsupportedOperationException("Cannot add key-value pair to non-object JSON node");
        }
    }

    public void addInt(String key, int value) {
        if (root instanceof ObjectNode objectNode) {
            objectNode.put(key, value);
        } else {
            throw new UnsupportedOperationException("Cannot add key-value pair to non-object JSON node");
        }
    }

    public void addBoolean(String key, boolean value) {
        if (root instanceof ObjectNode objectNode) {
            objectNode.put(key, value);
        } else {
            throw new UnsupportedOperationException("Cannot add key-value pair to non-object JSON node");
        }
    }

    public void addDouble(String key, double value) {
        if (root instanceof ObjectNode objectNode) {
            objectNode.put(key, value);
        } else {
            throw new UnsupportedOperationException("Cannot add key-value pair to non-object JSON node");
        }
    }

    public void addObject(String key, JSONObject value) {
        if (root instanceof ObjectNode objectNode) {
            objectNode.set(key, value.root);
        } else {
            throw new UnsupportedOperationException("Cannot add key-value pair to non-object JSON node");
        }
    }

    public void addList(String key, List<Object> values) {
        if (root instanceof ObjectNode objectNode) {
            ObjectMapper mapper = new ObjectMapper();
            objectNode.set(key, mapper.valueToTree(values));
        } else {
            throw new UnsupportedOperationException("Cannot add key-value pair to non-object JSON node");
        }
    }

}
