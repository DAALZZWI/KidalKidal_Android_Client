package com.daalzzwi.kidalkidal.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ModelToggle implements Serializable {

    @PrimaryKey( autoGenerate = true )
    private int togglePk;
    private int toggleValue;

    public ModelToggle() {

        this.togglePk = 0;
        this.toggleValue = 0;
    }

    public int getTogglePk() { return togglePk; }

    public void setTogglePk(int pk) { this.togglePk = pk; }

    public int getToggleValue() { return toggleValue; }

    public void setToggleValue( int toggleValue ) { this.toggleValue = toggleValue; }
}
