package irctc;

import irctc.entities.Train;
import irctc.entities.User;
import irctc.service.UserBookingService;
import irctc.util.UserServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");

        try (Scanner scanner = new Scanner(System.in)) {
            UserBookingService userBookingService = null;

            try {
                userBookingService = new UserBookingService();
            } catch (IOException ex) {
                System.out.println("Error initializing the service: " + ex.getMessage());
                return;
            }

            int option = 0;
            Train trainSelectedForBooking = null;

            while (option != 7) {
                System.out.println("Choose option:");
                System.out.println("1. Sign up");
                System.out.println("2. Login");
                System.out.println("3. Fetch Bookings");
                System.out.println("4. Search Trains");
                System.out.println("5. Book a Seat");
                System.out.println("6. Cancel my Booking");
                System.out.println("7. Exit the App");

                if (scanner.hasNextInt()) {
                    option = scanner.nextInt();
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // Clear the invalid input
                    continue;
                }

                switch (option) {
                    case 1:
                        System.out.println("Enter the username to sign up:");
                        String nameToSignUp = scanner.next();
                        System.out.println("Enter the password to sign up:");
                        String passwordToSignUp = scanner.next();
                        User userToSignup = new User(nameToSignUp, passwordToSignUp, UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(), UUID.randomUUID().toString());
                        userBookingService.signUp(userToSignup);
                        break;

                    case 2:
                        System.out.println("Enter the username to login:");
                        String nameToLogin = scanner.next();
                        System.out.println("Enter the password to login:");
                        String passwordToLogin = scanner.next();
                        User userToLogin = new User(nameToLogin, passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin), new ArrayList<>(), UUID.randomUUID().toString());
                        try {
                            userBookingService = new UserBookingService(userToLogin);
                        } catch (IOException ex) {
                            System.out.println("Error logging in: " + ex.getMessage());
                        }
                        break;

                    case 3:
                        System.out.println("Fetching your bookings...");
                        userBookingService.fetchBookings();
                        break;

                    case 4:
                        System.out.println("Type your source station:");
                        String source = scanner.next();
                        System.out.println("Type your destination station:");
                        String dest = scanner.next();
                        List<Train> trains = userBookingService.getTrains(source, dest);
                        if (trains.isEmpty()) {
                            System.out.println("No trains found.");
                            break;
                        }
                        int index = 1;
                        for (Train t : trains) {
                            System.out.println(index + " Train id: " + t.getTrainId());
                            for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                                System.out.println("Station " + entry.getKey() + " time: " + entry.getValue());
                            }
                            index++;
                        }
                        System.out.println("Select a train by typing the number:");
                        int trainIndex = scanner.nextInt();
                        if (trainIndex > 0 && trainIndex <= trains.size()) {
                            trainSelectedForBooking = trains.get(trainIndex - 1);
                        } else {
                            System.out.println("Invalid train selection.");
                        }
                        break;

                    case 5:
                        if (trainSelectedForBooking == null) {
                            System.out.println("No train selected. Please search for trains first.");
                            break;
                        }
                        System.out.println("Select a seat out of these seats:");
                        List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                        if (seats.isEmpty()) {
                            System.out.println("No seats available.");
                            break;
                        }
                        for (List<Integer> row : seats) {
                            for (Integer val : row) {
                                System.out.print(val + " ");
                            }
                            System.out.println();
                        }
                        System.out.println("Select the seat by typing the row and column:");
                        System.out.println("Enter the row:");
                        int row = scanner.nextInt();
                        System.out.println("Enter the column:");
                        int col = scanner.nextInt();
                        System.out.println("Booking your seat...");
                        boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);
                        if (booked) {
                            System.out.println("Booked! Enjoy your journey.");
                        } else {
                            System.out.println("Can't book this seat.");
                        }
                        break;

                    case 6:
                        System.out.println("Cancel booking functionality is not implemented yet.");
                        break;

                    case 7:
                        System.out.println("Exiting the app.");
                        break;

                    default:
                        System.out.println("Invalid option. Please choose a valid number.");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
