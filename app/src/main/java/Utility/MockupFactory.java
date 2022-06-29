package Utility;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MockupFactory {
    private final HashMap<Integer, MockupStation> idMockupNameMap;
    private static String language = Locale.getDefault().getLanguage();
    private final String
            TYPE_MEETING = language.equals("sv") ? "Mötesstation" : "Meeting station",
            TYPE_CYCLE = language.equals("sv") ? "Cykelstation" : "Cycle station",
            TYPE_SCOOTER = language.equals("sv") ? "Scooterstation" : "Scooter station";

    private static final Map<String, String> unit;

    static {
        unit = new HashMap<>();
        unit.put("PM2.5", "μg/m3");
        unit.put("PM10", "μg/m3");
        unit.put("Light", "Lux");
        unit.put("Temperature", "°C");
    }

    private static final Map<String, String> swedishTranslation;

    static {
        swedishTranslation = new HashMap<>();
        swedishTranslation.put("PM2.5", "Luftburna partiklar (PM2.5)");
        swedishTranslation.put("PM10", "Luftburna partiklar (PM10)");
        swedishTranslation.put("Light", "Belysningsstyrka");
        swedishTranslation.put("Temperature", "Temperatur");
    }

    private static final Map<String, String> englishTranslation;

    static {
        englishTranslation = new HashMap<>();
        englishTranslation.put("PM2.5", "Particle pollution (PM2.5)");
        englishTranslation.put("PM10", "Particle pollution (PM10)");
        englishTranslation.put("Light", "Illuminance");
        englishTranslation.put("Temperature", "Temperature");
    }

    public MockupFactory() {
        idMockupNameMap = new HashMap<>();
        initMap();
    }

    private void initMap() {
        idMockupNameMap.put(1041270, new MockupStation("Linus", TYPE_MEETING));
        idMockupNameMap.put(1041625, new MockupStation("Markus", TYPE_CYCLE));
        idMockupNameMap.put(1046533, new MockupStation("Greta", TYPE_MEETING));
        idMockupNameMap.put(1206486, new MockupStation("Tova", TYPE_SCOOTER));
    }

    public String getName(int id) {
        return Objects.requireNonNull(idMockupNameMap.get(id)).getName();
    }

    public String getType(int id) {
        return Objects.requireNonNull(idMockupNameMap.get(id)).getType();
    }

    public static String getUnit(String dataType) {
        return unit.get(dataType);
    }

    public static String getDataDescription(String dataType) {
        language = Locale.getDefault().getLanguage();
        return language.equals("sv") ? swedishTranslation.get(dataType) : englishTranslation.get(dataType);
    }

    private static class MockupStation {
        private final String name;
        private final String type;

        public MockupStation(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }

}
