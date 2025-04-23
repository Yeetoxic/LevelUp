package edu.itas.danilvilmont.levelup;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Habit.class}, version = 1)
public abstract class HabitDatabase extends RoomDatabase {
    private static HabitDatabase INSTANCE;

    public abstract HabitDao habitDao();

    public static synchronized HabitDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HabitDatabase.class, "habit_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // remove this later!
                    .build();
        }
        return INSTANCE;
    }
}
