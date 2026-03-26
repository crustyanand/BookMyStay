import java.io.*;
import java.util.*;

/**
 * Use Case 12: Data Persistence & System Recovery
 * Demonstrates saving and restoring system state using serialization.
 *
 * @author Anurag
 * @version 12.0
 */
public class BookMyStay {

    /**
     * Reservation class (Serializable)
     */
    static class Reservation implements Serializable {
        private static final long serialVersionUID = 1L;

        private String reservationId;
        private String roomType;
        private String guestName;

        public Reservation(String reservationId, String roomType, String guestName) {
            this.reservationId = reservationId;
            this.roomType = roomType;
            this.guestName = guestName;
        }

        public String getReservationId() {
            return reservationId;
        }

        public String getRoomType() {
            return roomType;
        }

        public String getGuestName() {
            return guestName;
        }

        @Override
        public String toString() {
            return "Reservation ID: " + reservationId +
                    ", Guest: " + guestName +
                    ", Room Type: " + roomType;
        }
    }

    /**
     * Inventory class (Serializable)
     */
    static class RoomInventory implements Serializable {
        private static final long serialVersionUID = 1L;

        private HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
        }

        public void addRoomType(String type, int count) {
            inventory.put(type, count);
        }

        public HashMap<String, Integer> getInventory() {
            return inventory;
        }

        public void displayInventory() {
            System.out.println("Current Inventory:");
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
        }
    }

    /**
     * Persistence Service
     */
    static class PersistenceService {

        private static final String FILE_NAME = "hotel_data.ser";

        public static void saveData(List<Reservation> reservations, RoomInventory inventory) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                oos.writeObject(reservations);
                oos.writeObject(inventory);
                System.out.println("Data successfully saved to file.");
            } catch (IOException e) {
                System.out.println("Error saving data: " + e.getMessage());
            }
        }

        public static Object[] loadData() {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                List<Reservation> reservations = (List<Reservation>) ois.readObject();
                RoomInventory inventory = (RoomInventory) ois.readObject();
                System.out.println("Data successfully loaded from file.");
                return new Object[]{reservations, inventory};
            } catch (FileNotFoundException e) {
                System.out.println("No previous data found. Starting fresh.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading data: " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Main Application
     */
    public static void main(String[] args) {

        List<Reservation> reservations;
        RoomInventory inventory;

        // Step 1: Try loading existing data
        Object[] data = PersistenceService.loadData();

        if (data != null) {
            reservations = (List<Reservation>) data[0];
            inventory = (RoomInventory) data[1];
        } else {
            // Fresh start
            reservations = new ArrayList<>();
            inventory = new RoomInventory();

            inventory.addRoomType("Single", 5);
            inventory.addRoomType("Double", 3);
            inventory.addRoomType("Suite", 2);

            reservations.add(new Reservation("R101", "Single", "Anurag"));
            reservations.add(new Reservation("R102", "Double", "Rahul"));
        }

        // Step 2: Display restored data
        System.out.println("\n--- SYSTEM STATE AFTER RECOVERY ---");
        inventory.displayInventory();

        System.out.println("\nBooking History:");
        for (Reservation r : reservations) {
            System.out.println(r);
        }

        // Step 3: Simulate new booking
        System.out.println("\nAdding new reservation...");
        Reservation newRes = new Reservation("R103", "Suite", "Priya");
        reservations.add(newRes);

        inventory.getInventory().put("Suite",
                inventory.getInventory().get("Suite") - 1);

        // Step 4: Save updated state
        PersistenceService.saveData(reservations, inventory);

        System.out.println("\nSystem shutdown complete.");
    }
}