import java.util.*;

/**
 * Use Case 8: Booking History & Reporting
 *
 * Goal:
 * Maintain a history of confirmed bookings and generate reports.
 *
 * Concepts:
 * - List for ordered storage
 * - Separation of storage and reporting
 * - Read-only reporting
 *
 * @author Anurag
 * @version 8.0
 */

class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void display() {
        System.out.println("Reservation ID: " + reservationId +
                " | Guest: " + guestName +
                " | Room Type: " + roomType +
                " | Room ID: " + roomId);
    }
}

/**
 * Booking History - stores confirmed reservations
 */
class BookingHistory {
    private List<Reservation> history;

    public BookingHistory() {
        history = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return history;
    }
}

/**
 * Reporting Service - generates reports from history
 */
class BookingReportService {

    public void displayAllBookings(List<Reservation> reservations) {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : reservations) {
            r.display();
        }
    }

    public void generateSummary(List<Reservation> reservations) {
        System.out.println("\n=== Booking Summary ===");

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation r : reservations) {
            roomTypeCount.put(
                    r.getRoomType(),
                    roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        for (String type : roomTypeCount.keySet()) {
            System.out.println(type + " Bookings: " + roomTypeCount.get(type));
        }

        System.out.println("Total Bookings: " + reservations.size());
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        // Simulating confirmed bookings (from UC6)
        Reservation r1 = new Reservation("R001", "Anurag", "Single", "S101");
        Reservation r2 = new Reservation("R002", "Rahul", "Double", "D201");
        Reservation r3 = new Reservation("R003", "Sneha", "Suite", "SU301");
        Reservation r4 = new Reservation("R004", "Priya", "Single", "S102");

        // Add to history
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);
        history.addReservation(r4);

        // Display full history
        reportService.displayAllBookings(history.getAllReservations());

        // Generate summary report
        reportService.generateSummary(history.getAllReservations());
    }
}