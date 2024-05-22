package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/kinosystem?user=root&password=27dave07";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Willkommen zum Kino-Buchungs-Management-System");

        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
            DatabaseManager.createTables(connectionSource);

            while (true) {
                System.out.println("1. Benutzer erstellen");
                System.out.println("2. Login");
                System.out.println("3. Beenden");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        createUser(connectionSource);
                        break;
                    case 2:
                        login(connectionSource);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Ungültige Auswahl. Bitte erneut versuchen.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createUser(ConnectionSource connectionSource) {
        try {
            Dao<User, Integer> userDao = DaoManager.createDao(connectionSource, User.class);
            System.out.print("Benutzername: ");
            String username = scanner.nextLine();
            System.out.print("Passwort: ");
            String password = scanner.nextLine();
            System.out.print("Ist Admin? (true/false): ");
            boolean isAdmin = scanner.nextBoolean();
            scanner.nextLine();

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setAdmin(isAdmin);

            userDao.create(user);
            System.out.println("Benutzer erfolgreich erstellt!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void login(ConnectionSource connectionSource) throws Exception {
        System.out.print("Benutzername: ");
        String username = scanner.nextLine();
        System.out.print("Passwort: ");
        String password = scanner.nextLine();

        User user = Auth.authenticate(username, password, connectionSource);
        if (user != null) {
            System.out.println("Erfolgreich eingeloggt!");
            if (user.isAdmin()) {
                adminMenu(connectionSource);
            } else {
                userMenu(connectionSource, user);
            }
        } else {
            System.out.println("Ungültiger Benutzername oder Passwort.");
        }
    }

    private static void adminMenu(ConnectionSource connectionSource) throws Exception {
        Dao<Film, Integer> filmDao = DaoManager.createDao(connectionSource, Film.class);
        Dao<Booking, Integer> bookingDao = DaoManager.createDao(connectionSource, Booking.class);

        while (true) {
            System.out.println("Admin-Menü:");
            System.out.println("1. Film hinzufügen");
            System.out.println("2. Film löschen");
            System.out.println("3. Buchung löschen");
            System.out.println("4. Sitzplatzanzeige");
            System.out.println("5. Beenden");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addFilm(filmDao);
                    break;
                case 2:
                    listFilms(filmDao);
                    deleteFilm(filmDao);
                    break;
                case 3:
                    listBookings(bookingDao);
                    deleteBooking(bookingDao);
                    break;
                case 4:
                    listFilms(filmDao);
                    System.out.print("Film-ID: ");
                    int filmId = scanner.nextInt();
                    showSeating(connectionSource, filmId);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Ungültige Auswahl.");
            }
        }
    }

    private static void userMenu(ConnectionSource connectionSource, User user) throws Exception {
        Dao<Film, Integer> filmDao = DaoManager.createDao(connectionSource, Film.class);
        Dao<Booking, Integer> bookingDao = DaoManager.createDao(connectionSource, Booking.class);

        while (true) {
            System.out.println("Benutzer-Menü:");
            System.out.println("1. Film buchen");
            System.out.println("2. Sitzplatzanzeige");
            System.out.println("3. Beenden");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    listFilms(filmDao);
                    createBooking(bookingDao, filmDao, user);
                    break;
                case 2:
                    listFilms(filmDao);
                    System.out.print("Film-ID: ");
                    int filmId = scanner.nextInt();
                    showSeating(connectionSource, filmId);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Ungültige Auswahl.");
            }
        }
    }

    private static void addFilm(Dao<Film, Integer> filmDao) throws Exception {
        System.out.print("Filmtitel: ");
        String title = scanner.nextLine();
        System.out.print("Beschreibung: ");
        String description = scanner.nextLine();
        Film film = new Film();
        film.setTitle(title);
        film.setDescription(description);
        filmDao.create(film);
        System.out.println("Film erfolgreich hinzugefügt!");
    }

    private static void deleteFilm(Dao<Film, Integer> filmDao) throws Exception {
        System.out.print("Film-ID: ");
        int filmId = scanner.nextInt();
        Film film = filmDao.queryForId(filmId);
        if (film != null) {
            filmDao.delete(film);
            System.out.println("Film erfolgreich gelöscht!");
        } else {
            System.out.println("Film nicht gefunden!");
        }
    }

    private static void createBooking(Dao<Booking, Integer> bookingDao, Dao<Film, Integer> filmDao, User user) throws
            Exception {
        System.out.print("Film-ID: ");
        int filmId = scanner.nextInt();
        System.out.print("Sitzplatznummer: ");
        int seatNumber = scanner.nextInt();

        List<Booking> existingBookings = bookingDao.queryForEq("film_id", filmId);
        boolean seatAlreadyBooked = existingBookings.stream()
                .anyMatch(booking -> booking.getSeatNumber() == seatNumber);

        if (seatAlreadyBooked) {
            System.out.println("Der Sitzplatz ist bereits gebucht. Bitte wählen Sie einen anderen Sitzplatz.");
        } else {
            Film film = filmDao.queryForId(filmId);
            if (film != null) {
                Booking booking = new Booking();
                booking.setUser(user);
                booking.setFilm(film);
                booking.setSeatNumber(seatNumber);
                bookingDao.create(booking);
                System.out.println("Buchung erfolgreich erstellt!");
            } else {
                System.out.println("Film nicht gefunden!");
            }
        }
    }

    private static void deleteBooking(Dao<Booking, Integer> bookingDao) throws Exception {
        System.out.print("Buchung-ID: ");
        int bookingId = scanner.nextInt();
        Booking booking = bookingDao.queryForId(bookingId);
        if (booking != null) {
            bookingDao.delete(booking);
            System.out.println("Buchung erfolgreich gelöscht!");
        } else {
            System.out.println("Buchung nicht gefunden!");
        }
    }

    private static void listFilms(Dao<Film, Integer> filmDao) throws Exception {
        List<Film> films = filmDao.queryForAll();
        System.out.println("Verfügbare Filme:");
        for (Film film : films) {
            System.out.println("ID: " + film.getId() + ", Titel: " + film.getTitle() + ", Beschreibung: " +
                    film.getDescription());
        }
    }

    private static void listBookings(Dao<Booking, Integer> bookingDao) throws Exception {
        List<Booking> bookings = bookingDao.queryForAll();
        System.out.println("Bestehende Buchungen:");
        for (Booking booking : bookings) {
            System.out.println("Buchung-ID: " + booking.getId() + ", Benutzer-ID: " + booking.getUser().getId() +
                    ", Film-ID: " + booking.getFilm().getId() + ", Sitzplatznummer: " + booking.getSeatNumber());
        }
    }

    private static void showSeating(ConnectionSource connectionSource, int filmId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CinemaHall cinemaHall = objectMapper.readValue(new File("cinema_hall.json"), CinemaHall.class);

            Dao<Booking, Integer> bookingDao = DaoManager.createDao(connectionSource, Booking.class);
            List<Booking> bookings = bookingDao.queryForEq("film_id", filmId);

            char[][] seats = new char[cinemaHall.getRows()][cinemaHall.getCols()];
            for (char[] row : seats) {
                java.util.Arrays.fill(row, 'O');
            }

            for (Booking booking : bookings) {
                int seatNumber = booking.getSeatNumber();
                int row = seatNumber / cinemaHall.getCols();
                int col = seatNumber % cinemaHall.getCols();
                seats[row][col] = 'X';
            }

            System.out.println("Sitzplatzanzeige für Film-ID: " + filmId);
            for (char[] row : seats) {
                for (char seat : row) {
                    System.out.print(seat + " ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
