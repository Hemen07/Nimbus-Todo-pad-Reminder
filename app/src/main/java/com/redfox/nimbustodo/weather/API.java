package com.redfox.nimbustodo.weather;

public class API {

    public static final String API_HOME = "http://api.openweathermap.org";
    public static final String API_KEY = "2b76aea4c23404097d7a4a8dccca90c1";
    public static final String API = API_HOME + "/data/2.5/weather?q=" + "NAME" + "&type=accurate&appid=" + API_KEY +
            "&units=metric";
    public static final String API_IMAGE = API_HOME + "/img/w/03d.png";

    //1000 24H:
   // public static final String API_PLACE = "AIzaSyD5VyNEu-GZv-pE3unctw69XuyOl68dXrQ";
}


