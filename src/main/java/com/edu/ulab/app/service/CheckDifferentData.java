package com.edu.ulab.app.service;

public interface CheckDifferentData {
    public default int checkValidHumanAge(int someAge){
        int age = 0;
        if (someAge > 0 && someAge < 125)
            age = someAge;
        return age;
    }
}
