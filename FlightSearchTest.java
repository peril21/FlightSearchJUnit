package flight;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

public class FlightSearchTest {

    private FlightSearch fs;

    @BeforeEach
    void setUp() {
        fs = new FlightSearch();
    }

    // Helper method to check if attributes are null/default (not initialized)
    private boolean attributesAreUninitialized() throws Exception {
        Field departureDate = FlightSearch.class.getDeclaredField("departureDate");
        departureDate.setAccessible(true);
        return departureDate.get(fs) == null;
    }

    // Helper method to verify all attributes are correctly set
    private void verifyAttributes(String expectedDepartureDate, String expectedDepartureAirport, 
                                   boolean expectedEmergencyRow, String expectedReturnDate,
                                   String expectedDestinationAirport, String expectedSeatingClass,
                                   int expectedAdults, int expectedChildren, int expectedInfants) throws Exception {
        Field departureDate = FlightSearch.class.getDeclaredField("departureDate");
        Field departureAirportCode = FlightSearch.class.getDeclaredField("departureAirportCode");
        Field emergencyRowSeating = FlightSearch.class.getDeclaredField("emergencyRowSeating");
        Field returnDate = FlightSearch.class.getDeclaredField("returnDate");
        Field destinationAirportCode = FlightSearch.class.getDeclaredField("destinationAirportCode");
        Field seatingClass = FlightSearch.class.getDeclaredField("seatingClass");
        Field adultPassengerCount = FlightSearch.class.getDeclaredField("adultPassengerCount");
        Field childPassengerCount = FlightSearch.class.getDeclaredField("childPassengerCount");
        Field infantPassengerCount = FlightSearch.class.getDeclaredField("infantPassengerCount");

        departureDate.setAccessible(true);
        departureAirportCode.setAccessible(true);
        emergencyRowSeating.setAccessible(true);
        returnDate.setAccessible(true);
        destinationAirportCode.setAccessible(true);
        seatingClass.setAccessible(true);
        adultPassengerCount.setAccessible(true);
        childPassengerCount.setAccessible(true);
        infantPassengerCount.setAccessible(true);

        Assertions.assertEquals(expectedDepartureDate, departureDate.get(fs));
        Assertions.assertEquals(expectedDepartureAirport, departureAirportCode.get(fs));
        Assertions.assertEquals(expectedEmergencyRow, emergencyRowSeating.get(fs));
        Assertions.assertEquals(expectedReturnDate, returnDate.get(fs));
        Assertions.assertEquals(expectedDestinationAirport, destinationAirportCode.get(fs));
        Assertions.assertEquals(expectedSeatingClass, seatingClass.get(fs));
        Assertions.assertEquals(expectedAdults, adultPassengerCount.get(fs));
        Assertions.assertEquals(expectedChildren, childPassengerCount.get(fs));
        Assertions.assertEquals(expectedInfants, infantPassengerCount.get(fs));
    }

    @Test
    void testValidBooking() throws Exception {
        // Test with valid inputs - should return true and initialize attributes
        boolean result = fs.runFlightSearch("20/12/2025", "syd", false,
                "25/12/2025", "mel", "economy",
                2, 1, 0);
        Assertions.assertTrue(result, "Valid booking should return true");
        
        // Verify attributes are correctly initialized
        verifyAttributes("20/12/2025", "syd", false, "25/12/2025", "mel", "economy", 2, 1, 0);
    }

    @Test
    void testTooManyPassengers() throws Exception {
        // Boundary test: exactly 10 passengers (exceeds limit of 9)
        boolean result = fs.runFlightSearch("20/12/2025", "syd", false,
                "25/12/2025", "mel", "economy",
                5, 5, 0);
        Assertions.assertFalse(result, "More than 9 passengers should return false");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testChildrenInEmergencyRow() throws Exception {
        // Test: children cannot be in emergency row
        boolean result = fs.runFlightSearch("20/12/2025", "syd", true,
                "25/12/2025", "mel", "economy",
                1, 1, 0);
        Assertions.assertFalse(result, "Children cannot sit in emergency row");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testInfantsInBusinessClass() throws Exception {
        // Test: infants cannot be in business class
        boolean result = fs.runFlightSearch("20/12/2025", "syd", false,
                "25/12/2025", "mel", "business",
                1, 0, 1);
        Assertions.assertFalse(result, "Infants not allowed in business class");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testReturnBeforeDeparture() throws Exception {
        // Test: return date before departure date
        boolean result = fs.runFlightSearch("25/12/2025", "syd", false,
                "20/12/2025", "mel", "economy",
                2, 0, 0);
        Assertions.assertFalse(result, "Return before departure should fail");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testSameAirport() throws Exception {
        // Test: departure and destination cannot be same
        boolean result = fs.runFlightSearch("20/12/2025", "syd", false,
                "25/12/2025", "syd", "economy",
                2, 0, 0);
        Assertions.assertFalse(result, "Departure and destination airports cannot be same");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testEmergencyRowOnlyEconomy() throws Exception {
        // Test: emergency row only available in economy
        boolean result = fs.runFlightSearch("20/12/2025", "syd", true,
                "25/12/2025", "mel", "business",
                2, 0, 0);
        Assertions.assertFalse(result, "Emergency row only available in economy");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testInvalidDateFormat() throws Exception {
        // Test: invalid date format (YYYY/MM/DD instead of DD/MM/YYYY)
        boolean result = fs.runFlightSearch("2025/12/20", "syd", false,
                "2025/12/25", "mel", "economy",
                2, 0, 0);
        Assertions.assertFalse(result, "Invalid date format should fail");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testDepartureInPast() throws Exception {
        // Test: departure date in the past
        boolean result = fs.runFlightSearch("01/01/2020", "syd", false,
                "10/01/2020", "mel", "economy",
                1, 0, 0);
        Assertions.assertFalse(result, "Past departure date should fail");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testInvalidDepartureAirport() throws Exception {
        // Test: invalid departure airport code
        boolean result = fs.runFlightSearch("20/12/2025", "xyz", false,
                "25/12/2025", "mel", "economy",
                1, 0, 0);
        Assertions.assertFalse(result, "Invalid departure airport code should fail");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testInvalidDestinationAirport() throws Exception {
        // Test: invalid destination airport code
        boolean result = fs.runFlightSearch("20/12/2025", "syd", false,
                "25/12/2025", "zzz", "economy",
                1, 0, 0);
        Assertions.assertFalse(result, "Invalid destination airport code should fail");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }

    @Test
    void testZeroPassengers() throws Exception {
        // Boundary test: 0 passengers (below minimum of 1)
        boolean result = fs.runFlightSearch("20/12/2025", "syd", false,
                "25/12/2025", "mel", "economy",
                0, 0, 0);
        Assertions.assertFalse(result, "Zero passengers should not be allowed");
        
        // Verify attributes are NOT initialized
        Assertions.assertTrue(attributesAreUninitialized(), "Attributes should not be initialized on failure");
    }
}