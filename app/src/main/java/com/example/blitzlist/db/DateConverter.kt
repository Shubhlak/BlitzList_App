package com.example.blitzlist.db

import androidx.room.TypeConverter

import java.util.Date

class DateConverter {

    // this is 3. important things in the data base creation

    // we need to converters here one to push data into the database and another to get data form data base and display it

    // 1. first converter is To convert date to Long -->  TO push data into the db

    @TypeConverter
    fun formDateToLong(date: Date):Long{
        return date.time
    }


    // 2. second converter is to convert Long to date --> To read data form db

    @TypeConverter
    fun fromLongToDate(timestamp:Long):Date{
        return Date(timestamp)
    }

}






// below code is just for the sake of lerning to build the custom data class and typeconverter
// type converters are requird because the room data base connot store data types other then some of the specified ones so we need typeconverters
// these type conveters help us to convert the data form our usesable format to the format accepeted by the data base and back to usable format
// the below is the example of one


class StudentConverter{
    @TypeConverter
    fun formStudentToString(student: Student):String{
        return "${student.name} ${student.age} ${student.rollno} ${student.phoneno}" // this line takes the data in form of student and returns the data in string format , in spaced manner
    }



    @TypeConverter
    fun formStringToStudent(str:String):Student{
        val studentData =str.split("")
        // the split function takes a string and splits it into substrings baised on the type of delimter passed her it is space
        // bacically be are getting an list of strings for example -> " ABC 22 210 9898232133" --> ["ABC","22","210","9898232133"]

        return Student(
            name = studentData[0],
            age = studentData[1].toInt(),
            rollno = studentData[2].toInt(),
            phoneno = studentData[3]
        )
    }
}



// this is the costum data type that we need
data class Student(
    val name:String,
    val age:Int,
    val rollno:Int,
    val phoneno:String
)