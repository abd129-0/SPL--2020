package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.passiveObjects.JsonInputReader;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
    public static CountDownLatch downLatch =  new CountDownLatch(4);;
    public static Diary diary = Diary.getDiaryInstance();

    public static void main(String[] args) {
        //Reading input
        Input input = null;
        try {
            input = JsonInputReader.getInputFromJson(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // initialize diary
        Diary diary = Diary.getDiaryInstance();
        // initialize Ewoks
        Ewoks ewoks = Ewoks.getInstance();
        ewoks.setEwoksArray(input.getEwoks());
        // initialize threads
        Thread C3PO = new Thread(new C3POMicroservice());
        Thread HanSolo = new Thread(new HanSoloMicroservice());
        Thread R2S2 = new Thread(new R2D2Microservice(input.getR2D2()));
        Thread Lando = new Thread(new LandoMicroservice(input.getLando()));
        Thread Leia = new Thread(new LeiaMicroservice(input.getAttacks()));
        C3PO.start();
        HanSolo.start();
        R2S2.start();
        Lando.start();
        try{
            downLatch.await();
        } catch (Exception e){}
        Leia.start();
        try {
            C3PO.join();
            HanSolo.join();
            R2S2.join();
            Lando.join();
            Leia.join();
        } catch (InterruptedException e) {e.printStackTrace();}
        // write the output json
        Gson gsonOutput = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter fileWriter = new FileWriter(args[1]);
            gsonOutput.toJson(diary, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
