package com.example.quanlylichhoc.storage;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class AddSubjectViewModel extends ViewModel {
    private final SavedStateHandle state;

    public AddSubjectViewModel(SavedStateHandle savedStateHandle) {
        this.state = savedStateHandle;
    }

    public void setSubjectName(String name) { state.set("subject_name", name); }
    public String getSubjectName() { return state.get("subject_name"); }

    public void setRoom(String room) { state.set("room", room); }
    public String getRoom() { return state.get("room"); }

    public void setTime(String time) { state.set("time", time); }
    public String getTime() { return state.get("time"); }

    public void setClassCode(String code) { state.set("class_code", code); }
    public String getClassCode() { return state.get("class_code"); }
    
    public void setDayPos(int pos) { state.set("day_pos", pos); }
    public Integer getDayPos() { return state.get("day_pos"); }
}
