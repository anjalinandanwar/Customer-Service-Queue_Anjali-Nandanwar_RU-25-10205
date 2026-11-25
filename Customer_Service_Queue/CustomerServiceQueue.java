
import java.io.*;
import java.util.Scanner;

public class CustomerServiceQueue {

    static final String FILE_NAME = "customers.txt";
    static final int FIRST_TOKEN = 101;

    // Customer class
    static class Customer {
        String token;
        String name;
        String service;

        Customer(String token, String name, String service) {
            this.token = token;
            this.name = name;
            this.service = service;
        }
    }

    // Node class
    static class Node {
        Customer data;
        Node next;

        Node(Customer data) {
            this.data = data;
            this.next = null;
        }
    }

    // Queue class using Linked List
    static class Queue {
        Node front;
        Node rear;
        int size = 0;
        int nextToken = FIRST_TOKEN;

        boolean isEmpty() {
            return front == null;
        }

        // Generate token
        String generateToken() {
            return "Q" + nextToken++;
        }

        // Add customer to rear
        void addCustomer(Customer customer) {
            Node newNode = new Node(customer);

            if (rear == null) {
                front = rear = newNode;
            } else {
                rear.next = newNode;
                rear = newNode;
            }

            size++;
        }

        // Join queue
        void join(String name, String service) {
            String token = generateToken();

            Customer customer =
                new Customer(token, name, service);

            addCustomer(customer);

            System.out.println("Token issued.");
            System.out.println(
                token + " | " + name + " | " + service
                + " | People ahead: " + (size - 1)
            );
        }

        // Call next customer
        void callNext() {
            if (isEmpty()) {
                System.out.println("Queue is empty.");
                return;
            }

            Customer customer = front.data;

            front = front.next;
            size--;

            if (front == null) {
                rear = null;
            }

            System.out.println(
                "Now serving: " + customer.token
                + " | " + customer.name
            );

            if (size == 0) {
                System.out.println("Queue is now empty.");
            } else {
                System.out.println(
                    "Remaining in queue: " + size
                );
            }
        }

        // Display queue
        void display() {
            if (isEmpty()) {
                System.out.println("Queue is empty.");
                return;
            }

            Node current = front;
            int position = 1;

            while (current != null) {
                Customer customer = current.data;

                System.out.println(
                    position + ". "
                    + customer.token + " | "
                    + customer.name + " | "
                    + customer.service
                );

                current = current.next;
                position++;
            }
        }

        // Remove customer by token
        void leave(String token) {
            Node current = front;
            Node previous = null;

            while (current != null) {

                if (current.data.token.equalsIgnoreCase(token)) {

                    if (previous == null) {
                        front = current.next;
                    } else {
                        previous.next = current.next;
                    }

                    if (current == rear) {
                        rear = previous;
                    }

                    size--;

                    System.out.println(
                        "Customer with token "
                        + current.data.token
                        + " left the queue."
                    );

                    return;
                }

                previous = current;
                current = current.next;
            }

            System.out.println(
                "Token " + token + " not found."
            );
        }

        // Load queue from file
        void loadFromFile() {
            File file = new File(FILE_NAME);

            if (!file.exists()) {
                return;
            }

            int maxToken = FIRST_TOKEN - 1;

            try (
                BufferedReader br =
                    new BufferedReader(new FileReader(file))
            ) {
                String line;

                while ((line = br.readLine()) != null) {

                    String[] parts = line.split("\\t", -1);

                    if (parts.length >= 3) {

                        Customer customer = new Customer(
                            parts[0], parts[1], parts[2]
                        );

                        addCustomer(customer);

                        try {
                            int number = Integer.parseInt(
                                parts[0].substring(1)
                            );

                            if (number > maxToken) {
                                maxToken = number;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore invalid token number
                        }
                    }
                }

                nextToken = Math.max(
                    FIRST_TOKEN, maxToken + 1
                );

                System.out.println(
                    "Loaded " + size
                    + " customer(s) from " + FILE_NAME
                );

            } catch (IOException e) {
                System.out.println(
                    "Error loading file: " + e.getMessage()
                );
            }
        }

        // Save queue to file
        void saveToFile() {
            try (
                PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                        new FileWriter(FILE_NAME)
                    )
                )
            ) {
                Node current = front;

                while (current != null) {
                    Customer customer = current.data;

                    out.println(
                        customer.token + "\t"
                        + customer.name.replace("\t", " ")
                        + "\t"
                        + customer.service.replace("\t", " ")
                    );

                    current = current.next;
                }

            } catch (IOException e) {
                System.out.println(
                    "Error saving file: " + e.getMessage()
                );
            }
        }
    }

    // Display menu
    static void displayMenu() {
        System.out.println(
            "\n===== CUSTOMER SERVICE QUEUE ====="
        );

        System.out.println("1. Join Queue");
        System.out.println("2. Call Next Customer");
        System.out.println("3. Display Queue");
        System.out.println("4. Leave Queue (by Token)");
        System.out.println("5. Exit");

        System.out.print("Enter your choice: ");
    }

    // Main method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Queue queue = new Queue();

        queue.loadFromFile();

        boolean running = true;

        while (running) {

            displayMenu();

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    System.out.print("Name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Service: ");
                    String service = sc.nextLine().trim();

                    if (name.isEmpty() || service.isEmpty()) {
                        System.out.println(
                            "Name and service cannot be empty."
                        );
                    } else {
                        queue.join(name, service);
                    }
                    break;

                case "2":
                    queue.callNext();
                    break;

                case "3":
                    queue.display();
                    break;

                case "4":
                    System.out.print("Token: ");
                    String token = sc.nextLine().trim();

                    queue.leave(token);
                    break;

                case "5":
                    queue.saveToFile();

                    System.out.println(
                        "Queue saved to " + FILE_NAME
                    );

                    System.out.println("Goodbye!");

                    running = false;
                    break;

                default:
                    System.out.println(
                        "Invalid option. Choose 1 to 5."
                    );
            }
        }

        sc.close();
    }
}
