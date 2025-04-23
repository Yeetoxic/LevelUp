package edu.itas.danilvilmont.levelup;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "habits")
public class Habit {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public int xp = 0;
    public int streak = 0;

    public int winCount = 0;
    public int slipCount = 0;

    public long lastLogged = 0; // Timestamp of last log (ms)

    public Habit(String name) {
        this.name = name;
    }
}

