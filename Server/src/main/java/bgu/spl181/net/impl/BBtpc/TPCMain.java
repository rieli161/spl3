package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.*;
import bgu.spl181.net.srv.Server;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;

public class TPCMain {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        System.out.println(port);
        String USERS_JSON_PATH = "DataBase/Users.json";
        String MOVIES_JSON_PATH = "DataBase/Movies.json";

        UsersData users;
        MoviesData movies;

        try (FileReader usersFileReader = new FileReader(USERS_JSON_PATH);
             FileReader moviesFileReader = new FileReader(MOVIES_JSON_PATH)) {
            users = new Gson().fromJson(usersFileReader, UsersData.class);
            movies = new Gson().fromJson(moviesFileReader, MoviesData.class);
        } catch (IOException e) {
            System.out.println("Didn't find json files");
            return;
        }
        TBProtocolSharedData TBProtoSharedData = new TBProtocolSharedData();
        BBProtocolSharedData BBProtoSharedData = new BBProtocolSharedData(users, movies);
        Server.<String>threadPerClient(port,
                () -> new BlockbusterProtocol(BBProtoSharedData, TBProtoSharedData),
                () -> new MessageEncoderDecoderImpl()).serve();
    }
}
