package com.ujwal.locate2.di;

import com.ujwal.locate2.utils.authentication.LoginHandler;
import com.ujwal.locate2.utils.authentication.SignupHandler;
import com.ujwal.locate2.view.employees.FacultyLogin;
import com.ujwal.locate2.view.admins.StudentMainActivity;
import com.ujwal.locate2.viewmodel.repository.FacultyRepository;
import com.ujwal.locate2.viewmodel.repository.StudentRepository;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {ApplicationContextModule.class, FirebaseAuthModule.class})
public interface Component {

    void inject(StudentMainActivity studentMainActivity);

    void inject(LoginHandler loginHandler);

    void inject(StudentRepository studentRepository);

    void inject(FacultyRepository facultyRepository);

    void inject(FacultyLogin facultyLogin);

    void inject(SignupHandler signupHandler);
}
