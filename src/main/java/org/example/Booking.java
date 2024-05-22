package org.example;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "bookings")
public class Booking {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, canBeNull = false, columnName = "user_id")
    private User user;

    @DatabaseField(foreign = true, canBeNull = false, columnName = "film_id")
    private Film film;

    @DatabaseField(canBeNull = false)
    private int seatNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}
