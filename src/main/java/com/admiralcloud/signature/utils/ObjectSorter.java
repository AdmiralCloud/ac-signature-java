package com.admiralcloud.signature.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import java.util.*;

public class ObjectSorter {
    public static JsonNode deepSortJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode sortedObject = JsonNodeFactory.instance.objectNode();
            List<String> fieldNames = new ArrayList<>();
            node.fieldNames().forEachRemaining(fieldNames::add);
            Collections.sort(fieldNames);
            
            for (String fieldName : fieldNames) {
                sortedObject.set(fieldName, deepSortJsonNode(node.get(fieldName)));
            }
            return sortedObject;
        } 
        else if (node.isArray()) {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            for (JsonNode element : node) {
                arrayNode.add(deepSortJsonNode(element));
            }
            return arrayNode;
        }
        return node;
    }
}