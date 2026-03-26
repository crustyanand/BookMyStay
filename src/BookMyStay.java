import java.util.*;

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoom(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }
}

class BookingService {

    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Set<String>> roomAllocations = new HashMap<>();
    private int idCounter = 1;

    public void processBookings(BookingRequestQueue queue, RoomInventory inventory) {

        while (!queue.isEmpty()) {

            Reservation r = queue.getNextRequest();
            String type = r.getRoomType();

            if (inventory.getAvailability(type) > 0) {

                String roomId = generateRoomId(type);

                allocatedRoomIds.add(roomId);

                roomAllocations.putIfAbsent(type, new HashSet<>());
                roomAllocations.get(type).add(roomId);

                inventory.decrement(type);

                System.out.println("Booking Confirmed for " + r.getGuestName() +
                        " | Room Type: " + type +
                        " | Room ID: " + roomId);
            } else {
                System.out.println("Booking Failed for " + r.getGuestName() +
                        " | Room Type: " + type + " (Not Available)");
            }
        }
    }

    private String generateRoomId(String type) {
        String roomId = type.substring(0, 2).toUpperCase() + idCounter++;
        while (allocatedRoomIds.contains(roomId)) {
            roomId = type.substring(0, 2).toUpperCase() + idCounter++;
        }
        return roomId;
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Anurag", "Single Room"));
        queue.addRequest(new Reservation("Rahul", "Double Room"));
        queue.addRequest(new Reservation("Sneha", "Suite Room"));
        queue.addRequest(new Reservation("Kiran", "Single Room"));

        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 1);
        inventory.addRoom("Double Room", 1);
        inventory.addRoom("Suite Room", 0);

        BookingService service = new BookingService();
        service.processBookings(queue, inventory);
    }
}