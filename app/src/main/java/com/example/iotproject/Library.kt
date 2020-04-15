package com.example.iotproject

class Library {

//    fun validateFanSpeed(fanSpeed:Int):Int{
//        return when {
//            fanSpeed<0 -> {
//                0;
//            }
//            fanSpeed>5 -> {
//                5;
//            }
//            else -> {
//                fanSpeed;
//            }
//        }
//    }
//    fun validateLight(lightSpeed:Int):Int{
//        return when {
//            lightSpeed<0 -> {
//                0;
//            }
//            lightSpeed>5 -> {
//                5;
//            }
//            else -> {
//                lightSpeed;
//            }
//        }
//    }
    fun autoLight(lightIntensity:Int):Int {
        when{
            lightIntensity>400 ->{
                return 0
            }
            lightIntensity>300 ->{
                return 51
            }
            lightIntensity>200 ->{
                return 102
            }
            lightIntensity>100 ->{
                return 153
            }
            lightIntensity>50 ->{
                return 204
            }
            else ->{
                return 255
            }
        }
    }
    fun lightPower(index:Int):Int {
        return when (index) {
            0 -> 0
            1 -> 51
            2 -> 102
            3 -> 153
            4 -> 204
            5 -> 255
            else -> 0
        }
    }
    fun lightPowerCon(power:Int):Int {
        return when (power) {
            0 -> 0
            51 -> 1
            102 -> 2
            153 -> 3
            204 -> 4
            255 -> 5
            else -> 0
        }
    }
    fun autoFan(temp:Int):Int{
        when {
            temp<=24 -> {
                return 0
            }
            temp<27 -> {
                return 1
            }
            temp<30 -> {
                return 2
            }
            temp<33 -> {
                return 3
            }
            temp<36 -> {
                return 4
            }
            else -> {
                return 5
            }
        }
    }
}