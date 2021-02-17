package com.ujwal.locate2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.ujwal.locate2.models.Faculty;
import com.ujwal.locate2.viewmodel.repository.FacultyRepository;

import java.util.List;

import java9.util.concurrent.CompletableFuture;

public class FacultyViewModel extends AndroidViewModel {
    private FacultyRepository facultyRepository;

    public FacultyViewModel(@NonNull Application application) {
        super(application);
        facultyRepository=new FacultyRepository();
    }

    public LiveData<List<DataSnapshot>> getAllFaculties(){
        return facultyRepository.getAllFaculties();
    }

    public void addFaculty(Faculty faculty, String key){
        facultyRepository.addFaculty(faculty,key);
    }

    public void deleteFaculty(String key){
        facultyRepository.deleteFaculty(key);
    }

    public CompletableFuture<List<DataSnapshot>> getAllInstantFacultiesList() {
        return facultyRepository.getAllStudentsListInstant();
    }



    public void addFacultyToFacultiesList(String id,String node) {
        facultyRepository.addFacultyInfoToFacultiesList(id,node);
    }

    public void deleteFacultyFromFacultiesList(String key) {
        facultyRepository.deleteFacultyInfoFromFacultiesList(key);
    }
}
