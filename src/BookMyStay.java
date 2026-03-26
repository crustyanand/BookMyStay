import java.util.*;

/**
 * Use Case 10: Booking Cancellation & Inventory Rollback
 *
 * Goal:
 * Safely cancel bookings and restore system state using rollback logic.
 *
 * Concepts:
 * - Stack (LIFO rollback)
 * - State reversal
 * - Validation before mutation
 * - Inventory consistency
 *
 * @author Anurag
 * @version 10.0
 */

/**
 * Reservation Model
 */
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isActive;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isActive = true;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void cancel() {
        this.isActive = false;
    }

    public void display() {
        System.out.println("Reservation ID: " + reservationId +
                " | Guest: " + guestName +
                " | Room Type: " + roomType +
                " | Room ID: " + roomId +
                " | Status: " + (isActive ? "CONFIRMED" : "CANCELLED"));
    }
}

/**
 * Inventory Service
 */
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 1);
        inventory.put("Double", 1);
        inventory.put("Suite", 1);
    }

    public void incrementRoom(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    public void decrementRoom(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + ": " + inventory.get(type));
        }
    }
}

/**
 * Booking History (acts as storage)
 */
class BookingHistory {
    private Map<String, Reservation> reservations;

    public BookingHistory() {
        reservations = new HashMap<>();
    }

    public void addReservation(Reservation r) {
        reservations.put(r.getReservationId(), r);
    }

    public Reservation getReservation(String id) {
        return reservations.get(id);
    }

    public void displayAll() {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : reservations.values()) {
            r.display();
        }
    }
}

/**
 * Cancellation Service with rollback using Stack
 */
class CancellationService {

    private RoomInventory inventory;
    private BookingHistory history;
    private Stack<String> rollbackStack; // Stores released room IDs

    public CancellationService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
        this.rollbackStack = new Stack<>();
    }

    public void cancelBooking(String reservationId) {

        Reservation r = history.getReservation(reservationId);

        // Validation
        if (r == null) {
            System.out.println("Cancellation failed: Reservation does not exist.");
            return;
        }

        if (!r.isActive()) {
            System.out.println("Cancellation failed: Reservation already cancelled.");
            return;
        }

        // Step 1: Push room ID to rollback stack
        rollbackStack.push(r.getRoomId());

        // Step 2: Restore inventory
        inventory.incrementRoom(r.getRoomType());

        // Step 3: Mark reservation as cancelled
        r.cancel();

        System.out.println("Cancellation successful for Reservation ID: " + reservationId);
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack (Recently Freed Room IDs): " + rollbackStack);
    }
}

/**
 * Main Class
 */
public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();

        // Simulating confirmed bookings (like UC6)
        Reservation r1 = new Reservation("R001", "Anurag", "Single", "S101");
        Reservation r2 = new Reservation("R002", "Rahul", "Double", "D201");

        // Decrement inventory (simulate allocation)
        inventory.decrementRoom("Single");
        inventory.decrementRoom("Double");

        history.addReservation(r1);
        history.addReservation(r2);

        CancellationService cancellationService = new CancellationService(inventory, history);

        // Initial state
        history.displayAll();
        inventory.displayInventory();

        System.out.println("\n--- Cancellation Attempts ---");

        // Valid cancellation
        cancellationService.cancelBooking("R001");

        // Duplicate cancellation
        cancellationService.cancelBooking("R001");

        // Invalid reservation
        cancellationService.cancelBooking("R999");

        // Final state
        history.displayAll();
        inventory.displayInventory();
        cancellationService.showRollbackStack();
    }
}