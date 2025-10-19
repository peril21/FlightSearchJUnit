package flight;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class FlightSearch {

    private String departureDate;
    private String departureAirportCode;
    private boolean emergencyRowSeating;
    private String returnDate;
    private String destinationAirportCode;
    private String seatingClass;
    private int adultPassengerCount;
    private int childPassengerCount;
    private int infantPassengerCount;

    /**
     * Validates all input parameters based on given conditions.
     * If all are valid, initializes class attributes and returns true.
     * Otherwise, returns false without changing any attributes.
     */
    public boolean runFlightSearch(String departureDate, String departureAirportCode, boolean emergencyRowSeating,
                                   String returnDate, String destinationAirportCode, String seatingClass,
                                   int adultPassengerCount, int childPassengerCount, int infantPassengerCount) {

        // Valid airport codes
        String[] validAirports = {"syd", "mel", "lax", "cdg", "del", "pvg", "doh"};
        // Valid seating classes
        String[] validClasses = {"economy", "premium economy", "business", "first"};

        // 1️⃣ Condition 1 - Total passengers must be between 1 and 9
        int totalPassengers = adultPassengerCount + childPassengerCount + infantPassengerCount;
        if (totalPassengers < 1 || totalPassengers > 9) return false;

        // 2️⃣ Condition 2 - Children cannot be in emergency row or first class
        if ((childPassengerCount > 0) && (emergencyRowSeating || seatingClass.equals("first")))
            return false;

        // 3️⃣ Condition 3 - Infants cannot be in emergency row or business class
        if ((infantPassengerCount > 0) && (emergencyRowSeating || seatingClass.equals("business")))
            return false;

        // 4️⃣ Condition 4 - Up to 2 children per adult
        if (childPassengerCount > adultPassengerCount * 2)
            return false;

        // 5️⃣ Condition 5 - One infant per adult
        if (infantPassengerCount > adultPassengerCount)
            return false;

        // 6️⃣ Condition 6 - Departure date cannot be in the past
        Date today = new Date();
        Date depDate = parseDateStrict(departureDate);
        if (depDate == null || depDate.before(removeTime(today)))
            return false;

        // 7️⃣ Condition 7 - Strict date format and validation
        Date retDate = parseDateStrict(returnDate);
        if (retDate == null)
            return false;

        // 8️⃣ Condition 8 - Return date cannot be before departure
        if (retDate.before(depDate))
            return false;

        // 9️⃣ Condition 9 - Valid seating class
        if (!Arrays.asList(validClasses).contains(seatingClass))
            return false;

        // 🔟 Condition 10 - Only economy class can have emergency row
        if (emergencyRowSeating && !seatingClass.equals("economy"))
            return false;

        // 11️⃣ Condition 11 - Valid airports and not the same
        if (!Arrays.asList(validAirports).contains(departureAirportCode) ||
            !Arrays.asList(validAirports).contains(destinationAirportCode) ||
            departureAirportCode.equals(destinationAirportCode))
            return false;

        // ✅ If all conditions are met, initialize attributes
        this.departureDate = departureDate;
        this.departureAirportCode = departureAirportCode;
        this.emergencyRowSeating = emergencyRowSeating;
        this.returnDate = returnDate;
        this.destinationAirportCode = destinationAirportCode;
        this.seatingClass = seatingClass;
        this.adultPassengerCount = adultPassengerCount;
        this.childPassengerCount = childPassengerCount;
        this.infantPassengerCount = infantPassengerCount;

        return true;
    }

    // Helper method to strictly parse DD/MM/YYYY
    private Date parseDateStrict(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    // Helper to remove time (keep only date)
    private Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}