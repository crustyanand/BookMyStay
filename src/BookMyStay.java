import java.util.*;

/**
 * Use Case 9: Error Handling & Validation
 *
 * Goal:
 * Introduce validation and structured error handling to ensure system reliability.
 *
 * Concepts:
 * - Input validation
 * - Custom exceptions
 * - Fail-fast design
 * - Graceful error handling
 *
 * @author Anurag
 * @version 9.0
 */

/**
 * Custom Exception for Invalid Booking Scenarios
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * Inventory Service with validation safeguards
 */
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 2);
        inventory.put("Double", 1);
        inventory.put("Suite", 0); // intentionally 0 for testing
    }

    public boolean isRoomTypeValid(String roomType) {
        return inventory.containsKey(roomType);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrementRoom(String roomType) throws InvalidBookingException {
        int available = getAvailability(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("No available rooms for type: " + roomType);
        }

        inventory.put(roomType, available - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + ": " + inventory.get(type));
        }
    }
}

/**
 * Validator class (Fail-Fast Design)
 */
class BookingValidator {

    public static void validate(String guestName, String roomType, RoomInventory inventory)
            throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (!inventory.isRoomTypeValid(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }

        if (inventory.getAvailability(roomType) <= 0) {
            throw new InvalidBookingException("Room not available for type: " + roomType);
        }
    }
}

/**
 * Booking Service
 */
class BookingService {

    private RoomInventory inventory;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void bookRoom(String guestName, String roomType) {
        try {
            // Step 1: Validate input (Fail Fast)
            BookingValidator.validate(guestName, roomType, inventory);

            // Step 2: Perform allocation safely
            inventory.decrementRoom(roomType);

            // Step 3: Confirm booking
            System.out.println("Booking successful for " + guestName +
                    " | Room Type: " + roomType);

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("Booking failed: " + e.getMessage());
        }
    }
}

/**
 * Main Class
 */
public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Initial inventory
        inventory.displayInventory();

        System.out.println("\n--- Booking Attempts ---");

        // Valid booking
        bookingService.bookRoom("Anurag", "Single");

        // Invalid room type
        bookingService.bookRoom("Rahul", "Deluxe");

        // Empty guest name
        bookingService.bookRoom("", "Double");

        // Valid booking
        bookingService.bookRoom("Sneha", "Double");

        // Exceeding availability (Suite = 0)
        bookingService.bookRoom("Priya", "Suite");

        // Final inventory
        inventory.displayInventory();
    }
}