import java.util.*;

abstract class Room {
    private String type;
    private double price;

    public Room(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 2000);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 3500);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 6000);
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
}

class SearchService {

    public static void searchRooms(List<Room> rooms, RoomInventory inventory) {
        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getType());
            if (available > 0) {
                System.out.println(room.getType() + " - Price: " + room.getPrice() + " - Available: " + available);
            }
        }
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 5);
        inventory.addRoom("Double Room", 0);
        inventory.addRoom("Suite Room", 2);

        SearchService.searchRooms(rooms, inventory);
    }
}