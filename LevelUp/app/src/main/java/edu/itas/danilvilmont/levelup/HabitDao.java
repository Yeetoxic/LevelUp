package edu.itas.danilvilmont.levelup;

import androidx.room.*;

import java.util.List;

@Dao
public interface HabitDao {
    @Insert
    void insert(Habit habit);

    @Update
    void update(Habit habit);

    @Delete
    void delete(Habit habit);

    @Query("SELECT * FROM habits ORDER BY id DESC")
    List<Habit> getAllHabits();

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    Habit getHabitById(int habitId);
}