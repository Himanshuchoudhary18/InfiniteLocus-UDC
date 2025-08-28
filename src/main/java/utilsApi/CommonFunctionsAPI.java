package utilsApi;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.Assert;
import utilities.Base;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class CommonFunctionsAPI {

    private static final double EARTH_RADIUS_KM = 6371.0;
    public static double centralLatitude = 28.50201; // Replace with your central latitude
    public static double centralLongitude = 77.18184; // Replace with your central longitude
    public static double centralLatitudeAirport1 = 28.55521570825102; // Replace with your central latitude
    public static double centralLongitudeAirport1 = 77.08436930893012; // Replace with your central longitude
    public static double centralLatitudeAirport2 = 13.199076013728474; // Replace with your central latitude
    public static double centralLongitudeAirport2 = 77.70682382387231; // Replace with your central longitude
    public static double centralLatitudeUae = 25.2048493;
    public static double maxRadiusKm = 1.0;
    public static String receiptsNumber = genrateReceiptsNumber();
    private static double centralLongitudeUae = 55.2707828;

    public static String getKeyFromResponseJson(Response response, String key) {
        JsonPath jsonPath = new JsonPath(response.asString());
        return jsonPath.getString(key);
    }

    public static ArrayList<Integer> getIntegerKeyFromResponseJson(Response response, String key) {
        JsonPath jsonPath = new JsonPath(response.asString());
        return jsonPath.get(key);
    }


    public static ArrayList<String> getStringKeyListFromResponseJson(Response response, String key) {
        JsonPath jsonPath = new JsonPath(response.asString());
        return jsonPath.get(key);
    }

    public static ArrayList<Object> getObjectKeyListFromResponseJson(Response response, String key) {
        JsonPath jsonPath = new JsonPath(response.asString());
        return jsonPath.get(key);
    }

    public static <T> String convertDtoToJson(T obj) {
        return new Gson().toJson(obj);
    }

    public static Long getTime(int addMinutes) {
        Date date1 = new Date();
        Calendar c = Calendar.getInstance();
        // Set timezone based on country code
        if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("AE")) {
            c.setTimeZone(TimeZone.getTimeZone("Asia/Dubai"));
        } else {
            c.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        }
        c.setTime(date1);
        c.add(Calendar.MINUTE, addMinutes);
        return c.getTime().getTime();
    }

    public static double generateRandomCoordinate(double min, double max) {
        return min + (max - min) * Math.random();
    }

    public static double getRandomLatitude() {
        if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("IN")) {
            double latInRadians = Math.toRadians(centralLatitude);
            double latOffset = maxRadiusKm / 111.32; // 1 degree of latitude is approximately 111.32 km
            double minLat = centralLatitude - latOffset;
            double maxLat = centralLatitude + latOffset;
            return generateRandomCoordinate(minLat, maxLat);
        } else {
            double latInRadians = Math.toRadians(centralLatitudeUae);
            double latOffset = maxRadiusKm / 111.32; // 1 degree of latitude is approximately 111.32 km
            double minLat = centralLatitudeUae - latOffset;
            double maxLat = centralLatitudeUae + latOffset;
            return generateRandomCoordinate(minLat, maxLat);
        }
    }

    public static double getRandomLatitude(int range) {
        if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("IN")) {
            double latOffset = range / 111.32; // 1 degree of latitude is approximately 111.32 km
            double minLat = centralLatitude - latOffset;
            double maxLat = centralLatitude + latOffset;
            return generateRandomCoordinate(minLat, maxLat);
        } else {
            double latOffset = maxRadiusKm / 111.32; // 1 degree of latitude is approximately 111.32 km
            double minLat = centralLatitudeUae - latOffset;
            double maxLat = centralLatitudeUae + latOffset;
            return generateRandomCoordinate(minLat, maxLat);
        }
    }

    public static double[] getRandomCoordinates(double minDistanceKm, double pickupLat, double pickupLng) {
        double dropLat, dropLng;

        while (true) {
            dropLat = getRandomLatitude(13);
            dropLng = getRandomLongitude(13);

            // Check if the drop point meets the minimum distance requirement from the pickup
            double distanceToPickup = haversineDistance(pickupLat, pickupLng, dropLat, dropLng);
            log.info(String.format("Pickup = %f,%f Drop=%f,%f Distance=%f", pickupLat, pickupLng, dropLat, dropLng, distanceToPickup));
            if (distanceToPickup > minDistanceKm) {
                break;
            }
        }
        return new double[]{dropLat, dropLng};
    }

    private static double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public static double getRandomLatitudeAirport(int zoneId, boolean ifAirportLocation) {
        double latOffset = maxRadiusKm / 111.32;
        double minLat = centralLatitudeAirport1 - latOffset;
        double maxLat = centralLatitudeAirport1 - latOffset;
        if (zoneId == 1) {
            if (ifAirportLocation) {
                double latInRadians = Math.toRadians(centralLatitudeAirport1);
                double latOffsets = maxRadiusKm / 111.32; // 1 degree of latitude is approximately 111.32 km
                double minLats = centralLatitudeAirport1 - latOffset;
                double maxLats = centralLatitudeAirport1 + latOffset;
                return generateRandomCoordinate(minLat, maxLat);

            }
        } else if (zoneId == 2) {
            if (ifAirportLocation) {

            }
        } else if (zoneId == 3) {
            if (ifAirportLocation) {


            }
        } else
            Assert.fail("Check zoneId [Expected 1,2,3] Actual : " + zoneId);
        return generateRandomCoordinate(minLat, maxLat);
    }

    public static double getRandomLongitudeAirport(int zoneId, boolean ifAirportLocation) {
        double latOffset = maxRadiusKm / 111.32;
        double minLat = centralLongitudeAirport1 - latOffset;
        double maxLat = centralLongitudeAirport1 - latOffset;
        if (zoneId == 1) {
            if (ifAirportLocation) {
                double latInRadians = Math.toRadians(centralLongitudeAirport1);
                double latOffsets = maxRadiusKm / 111.32; // 1 degree of latitude is approximately 111.32 km
                double minLats = centralLongitudeAirport1 - latOffset;
                double maxLats = centralLongitudeAirport1 + latOffset;
                return generateRandomCoordinate(minLat, maxLat);

            }
        } else if (zoneId == 2) {
            if (ifAirportLocation) {

            }
        } else if (zoneId == 3) {
            if (ifAirportLocation) {


            }
        } else
            Assert.fail("Check zoneId [Expected 1,2,3] Actual : " + zoneId);
        return generateRandomCoordinate(minLat, maxLat);
    }

    public static double getRandomLatitude(String countryCode) {
        if (countryCode.equalsIgnoreCase("IN")) {
            double latInRadians = Math.toRadians(centralLatitude);
            double latOffset = maxRadiusKm / 111.32; // 1 degree of latitude is approximately 111.32 km
            double minLat = centralLatitude - latOffset;
            double maxLat = centralLatitude + latOffset;
            return generateRandomCoordinate(minLat, maxLat);
        } else {
            double latInRadians = Math.toRadians(centralLatitudeUae);
            double latOffset = maxRadiusKm / 111.32; // 1 degree of latitude is approximately 111.32 km
            double minLat = centralLatitudeUae - latOffset;
            double maxLat = centralLatitudeUae + latOffset;
            return generateRandomCoordinate(minLat, maxLat);
        }
    }

    public static double getRandomLongitude() {
        if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("IN")) {
            double lonInRadians = Math.toRadians(centralLongitude);
            double lonOffset = maxRadiusKm / (111.32 * Math.cos(centralLongitude));
            double minLon = centralLongitude - lonOffset;
            double maxLon = centralLongitude + lonOffset;
            return generateRandomCoordinate(minLon, maxLon);
        } else {
            double lonInRadians = Math.toRadians(centralLongitudeUae);
            double lonOffset = maxRadiusKm / (111.32 * Math.cos(centralLongitudeUae));
            double minLon = centralLongitudeUae - lonOffset;
            double maxLon = centralLongitudeUae + lonOffset;
            return generateRandomCoordinate(minLon, maxLon);
        }
    }

    public static double getRandomLongitude(String countryCode) {
        if (countryCode.equalsIgnoreCase("IN")) {
            double lonInRadians = Math.toRadians(centralLongitude);
            double lonOffset = maxRadiusKm / (111.32 * Math.cos(centralLongitude));
            double minLon = centralLongitude - lonOffset;
            double maxLon = centralLongitude + lonOffset;
            return generateRandomCoordinate(minLon, maxLon);
        } else {
            double lonInRadians = Math.toRadians(centralLongitudeUae);
            double lonOffset = maxRadiusKm / (111.32 * Math.cos(centralLongitudeUae));
            double minLon = centralLongitudeUae - lonOffset;
            double maxLon = centralLongitudeUae + lonOffset;
            return generateRandomCoordinate(minLon, maxLon);
        }
    }

    public static double getRandomLongitude(int range) {
        if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("IN")) {
            double lonOffset = range / (111.32 * Math.cos(centralLongitude));
            double minLon = centralLongitude - lonOffset;
            double maxLon = centralLongitude + lonOffset;
            return generateRandomCoordinate(minLon, maxLon);
        } else {
            double lonOffset = maxRadiusKm / (111.32 * Math.cos(centralLongitudeUae));
            double minLon = centralLongitudeUae - lonOffset;
            double maxLon = centralLongitudeUae + lonOffset;
            return generateRandomCoordinate(minLon, maxLon);
        }
    }

    public static double getTraceLat() {
        if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("IN")) {
            double latInRadians = Math.toRadians(centralLatitude);
            double latOffset = maxRadiusKm / 111.32; // 1 degree of latitude is approximately 111.32 km
            double minLat = centralLatitude - latOffset;
            double maxLat = centralLatitude + latOffset;
            return generateRandomCoordinate(minLat, maxLat);
        } else {
            double latInRadians = Math.toRadians(centralLatitudeUae);
            double latOffset = maxRadiusKm / 111.32; // 1 degree of latitude is approximately 111.32 km
            double minLat = centralLatitudeUae - latOffset;
            double maxLat = centralLatitudeUae + latOffset;
            return generateRandomCoordinate(minLat, maxLat);
        }
    }

    public static double getTraceLong() {
        if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("IN")) {
            double lonInRadians = Math.toRadians(centralLongitude);
            double lonOffset = maxRadiusKm / (111.32 * Math.cos(centralLongitude));
            double minLon = centralLongitude - lonOffset;
            double maxLon = centralLongitude + lonOffset;
            return generateRandomCoordinate(minLon, maxLon);
        } else {
            double lonInRadians = Math.toRadians(centralLongitudeUae);
            double lonOffset = maxRadiusKm / (111.32 * Math.cos(centralLongitude));
            double minLon = centralLongitudeUae - lonOffset;
            double maxLon = centralLongitudeUae + lonOffset;
            return generateRandomCoordinate(minLon, maxLon);
        }
    }

//    public static double getLat(double centralLon, double centralLat, double maxRadius) {
//        double lonInRadians = Math.toRadians(centralLon);
//        double lonOffset = maxRadius / (111.32 * Math.cos(centralLat));
//        double minLon = centralLon - lonOffset;
//        double maxLon = centralLon + lonOffset;
//        return generateRandomCoordinate(minLon, maxLon);
//    }
//
//    public static double getPickUpLat() {
//        Faker faker = new Faker();
//        double randomLat = getRandomLatitude();
//        return faker.number().randomDouble(6, (long) 28.3905, (long) 28.5300);
//    }
//
//    public static double getPickUpLong() {
//        Faker faker = new Faker();
//        return faker.number().randomDouble(6, (long) 76.9400, (long) 77.0471);
//    }
//
//    public static double getDropLat() {
//        Faker faker = new Faker();
//        return faker.number().randomDouble(6, (long) 28.3905, (long) 28.5300);
//    }
//
//    public static double getDropLong() {
//        Faker faker = new Faker();
//        return faker.number().randomDouble(6, (long) 76.9400, (long) 77.0471);
//    }

    public static long getTimeStampInMiliSeconds(int hours) {
        // Get the current epoch timestamp in milliseconds
        TimeZone timeZone = null;
        if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("AE")) {
            timeZone = TimeZone.getTimeZone("Asia/Dubai"); // United Arab Emirates time zone
        } else if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("IN")) {
            timeZone = TimeZone.getTimeZone("Asia/Kolkata"); // Indian time zone
        } else {
            Assert.fail("Invalid Input, Only taken AE and IN as inputs");
        }
        // Create a Calendar object for the specified time zone
        Calendar calendar = Calendar.getInstance(timeZone);

        // Get the current time in milliseconds and convert it to seconds (epoch timestamp)
        long currentTimestampMillis = calendar.getTimeInMillis();
//        long currentTimestampMillis = System.currentTimeMillis();
        // Convert to Instant and add 6 hours
        Instant currentInstant = Instant.ofEpochMilli(currentTimestampMillis);
        if (hours > 0) {
            Instant newInstant = currentInstant.plus(hours, ChronoUnit.HOURS);
            // Get the new epoch timestamp in milliseconds
            long newTimestampMillis = newInstant.toEpochMilli();
            return newTimestampMillis;
        } else {
            return currentTimestampMillis;
        }
    }

    public static long getTimeStampInMiliSeconds(float hours) {
        // Get the current epoch timestamp in milliseconds
        TimeZone timeZone = null;
        if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("AE")) {
            timeZone = TimeZone.getTimeZone("Asia/Dubai"); // United Arab Emirates time zone
        } else if (Base.getProperty().getProperty("countryCode").equalsIgnoreCase("IN")) {
            timeZone = TimeZone.getTimeZone("Asia/Kolkata"); // Indian time zone
        } else {
            Assert.fail("Invalid Input, Only taken AE and IN as inputs");
        }
        // Create a Calendar object for the specified time zone
        Calendar calendar = Calendar.getInstance(timeZone);

        // Get the current time in milliseconds and convert it to seconds (epoch timestamp)
        long currentTimestampMillis = calendar.getTimeInMillis();
//        long currentTimestampMillis = System.currentTimeMillis();
        // Convert to Instant and add 6 hours
        Instant currentInstant = Instant.ofEpochMilli(currentTimestampMillis);
        if (hours > 0) {
            Instant newInstant = currentInstant.plus((long) (hours * 60), ChronoUnit.MINUTES);
            // Get the new epoch timestamp in milliseconds
            return newInstant.toEpochMilli();
        } else {
            return currentTimestampMillis;
        }
    }

    public static String getRandomVehicleNumber() {
        final String[] STATE_CODES = {"DL", "MH", "UP", "TN", "KA"};
        Faker faker = new Faker();
        String stateCode = STATE_CODES[faker.random().nextInt(STATE_CODES.length)];
        String pdigits = faker.regexify("\\d{2}");
        String letters = faker.regexify("[A-Z]{2}");
        String sdigits = faker.regexify("\\d{4}");

        return stateCode + pdigits + letters + sdigits;
    }

    //Want 22DM static discussed the same with dev
    public static String getRandomVehicleNumberSheet() {
        final String[] STATE_CODES = {"DL", "MH", "UP", "TN", "KA"};
        Faker faker = new Faker();
        String stateCode = STATE_CODES[faker.random().nextInt(STATE_CODES.length)];
        String sdigits = faker.regexify("\\d{4}");
        return stateCode + "22DM" + sdigits;
    }

    // Generatimg chassis number in the format "MZZCBZKCZRV0t9204"
    public static String getRandomVehicleNumberChassis() {
        Faker faker = new Faker();
        String sdigits = faker.regexify("[A-Z]{11}");
        String pdigit = faker.regexify("[0-9]{1}");
        String ldigit = faker.regexify("[a-z]{1}");
        String ndigit = faker.regexify("[0-9]{4}");
        return sdigits + pdigit + ldigit + ndigit;
    }


    public static void getUpdateDataInSheetUpload(Sheet sheet, int rawIndex, int cellIndex, String cellValue) {
        Row row = sheet.getRow(rawIndex);
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(cellValue);
    }

    public static String getTimeStampForLeaseUpload(long timeStampInMiliSeconds) {
        String timestamp = String.valueOf(timeStampInMiliSeconds);
        if (timestamp.length() > 12)
            timestamp = timestamp.substring(0, timestamp.length() - 5) + "00000";
        return timestamp;
    }

    public static String getFutureDateInRequiredFormat(int minutesToAdd, String format) {
        LocalDateTime currentDateTime = LocalDateTime.now().plusMinutes(60);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return currentDateTime.format(formatter);
    }

    private static String generateStartTimestampLocal() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(15);
        String startTime = localDateTime.format(dateTimeFormatter).toString();
        return startTime;
    }

    private static String generateEndTimestampLocal() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(16);
        String endTime = localDateTime.format(dateTimeFormatter).toString();
        return endTime;
    }

    private static String genrateReceiptsNumber() {
        Faker fake = new Faker();
        String receipts = fake.regexify("[A-Z]{8}");
        return receipts;
    }

    public static Long getDateTimeForTheStartOfTheDay() {
        DateTime dateTime = DateTime.now();
        dateTime = dateTime.withHourOfDay(0);
        dateTime = dateTime.withMinuteOfHour(0);
        dateTime = dateTime.withSecondOfMinute(0);
        dateTime = dateTime.withMillisOfSecond(0);
        return dateTime.getMillis();
    }

    public static DateTime getDateTimeNowBasedOnRegion() {
        return DateTime.now().withZone(DateTimeZone.forID(Base.getProperty().getProperty("timeZone")));
    }

}

