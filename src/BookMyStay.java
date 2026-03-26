import java.util.*;

/**
 * Use Case 11: Concurrent Booking Simulation
 *
 * Goal:
 * Demonstrate thread-safe booking under concurrent access.
 *
 * Concepts:
 * - Race conditions
 * - Thread safety
 * - Synchronized critical sections
 *
 * @author Anurag
 * @version 11.0
 */

/**
 * Reservation Request
 */
class BookingRequest {
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

/**
 * Thread-safe Inventory
 */
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 2);
        inventory.put("Double", 1);
    }

    // Critical Section
    public synchronized boolean allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + ": " + inventory.get(type));
        }
    }
}

/**
 * Shared Booking Queue (Thread-safe access)
 */
class BookingQueue {
    private Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest request) {
        queue.offer(request);
    }

    public synchronized BookingRequest getRequest() {
        return queue.poll();
    }
}

/**
 * Booking Processor (Thread)
 */
class BookingProcessor extends Thread {

    private RoomInventory inventory;
    private BookingQueue queue;

    public BookingProcessor(RoomInventory inventory, BookingQueue queue) {
        this.inventory = inventory;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest request;

            // synchronized fetch
            synchronized (queue) {
                request = queue.getRequest();
            }

            if (request == null) {
                break;
            }

            // Critical Section for allocation
            boolean success = inventory.allocateRoom(request.roomType);

            if (success) {
                System.out.println(Thread.currentThread().getName() +
                        " SUCCESS → " + request.guestName +
                        " booked " + request.roomType);
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " FAILED → " + request.guestName +
                        " (No " + request.roomType + " rooms available)");
            }

            try {
                Thread.sleep(100); // simulate processing delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * Main Class
 */
public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Simulate concurrent booking requests
        queue.addRequest(new BookingRequest("Anurag", "Single"));
        queue.addRequest(new BookingRequest("Rahul", "Single"));
        queue.addRequest(new BookingRequest("Sneha", "Single")); // should fail
        queue.addRequest(new BookingRequest("Priya", "Double"));
        queue.addRequest(new BookingRequest("Amit", "Double"));  // should fail

        // Multiple threads processing same queue
        BookingProcessor t1 = new BookingProcessor(inventory, queue);
        BookingProcessor t2 = new BookingProcessor(inventory, queue);
        BookingProcessor t3 = new BookingProcessor(inventory, queue);

        t1.setName("Thread-1");
        t2.setName("Thread-2");
        t3.setName("Thread-3");

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inventory.displayInventory();
    }
}