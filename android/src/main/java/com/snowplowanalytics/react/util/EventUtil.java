package com.snowplowanalytics.react.util;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.snowplowanalytics.snowplow.tracker.events.SelfDescribing;
import com.snowplowanalytics.snowplow.tracker.events.Structured;
import com.snowplowanalytics.snowplow.tracker.payload.SelfDescribingJson;

import java.util.ArrayList;
import java.util.List;

public class EventUtil {
    public static List<SelfDescribingJson> getContexts(ReadableArray contexts) {
        ArrayList<Object> reactContexts = contexts.toArrayList();
        ArrayList<SelfDescribingJson> nativeContexts = new ArrayList<>();
        for (Object context : reactContexts) {
            if (context instanceof ReadableMap) {
                SelfDescribingJson json = getSelfDescribingJson((ReadableMap) context);
                if (json != null) {
                    nativeContexts.add(json);
                } else {
                    // log errors
                }
            }
        }
        return nativeContexts;
    }

    public static SelfDescribingJson getSelfDescribingJson(ReadableMap json) {
        String schema = json.getString("schema");
        ReadableMap dataMap = json.getMap("data");
        if (schema != null && dataMap != null) {
            return new SelfDescribingJson(schema, dataMap.toHashMap());
        } else {
            // log error
        }
        return null;
    }

    public static SelfDescribing getSelfDescribingEvent(ReadableMap event, ReadableArray contexts) {
        SelfDescribingJson data = EventUtil.getSelfDescribingJson(event);
        List<SelfDescribingJson> nativeContexts = EventUtil.getContexts(contexts);
        SelfDescribing.Builder eventBuilder = SelfDescribing.builder();
        if (data == null) return null;
        eventBuilder.eventData(data);
        if (nativeContexts != null) {
            eventBuilder.customContext(nativeContexts);
        }
        return eventBuilder.build();
    }

    public static Structured getStructuredEvent(String category, String action, String label,
            String property, Number value, ReadableArray contexts) {
        Structured.Builder eventBuilder = Structured.builder()
                .action(action)
                .category(category)
                .value(value.doubleValue())
                .property(property)
                .label(label);
        List<SelfDescribingJson> nativeContexts = EventUtil.getContexts(contexts);
        if (nativeContexts != null) {
            eventBuilder.customContext(nativeContexts);
        }
        return eventBuilder.build();
    }
}