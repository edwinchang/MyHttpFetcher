package com.tools.music;

import java.io.FileInputStream;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * Created by edwinchang on 2016-9-18.
 */
public class MusicUtil {
    private static FileInputStream file;
    private static AudioStream as;

    public static void playit(String path){
        //path如："e:\\Temp\\通达信预警音乐\\645.wav"
        try {
            file=new FileInputStream(path);
            as=new AudioStream(file);
            AudioPlayer.player.start(as);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopit(AudioStream as){
        try {
            AudioPlayer.player.stop(as);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {
        String fileURL = "e:\\Temp\\通达信预警音乐\\645.wav";

        playit(fileURL);

        try {
            Thread.sleep(5000);    //线程休眠5s
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopit(as);
    }
}
