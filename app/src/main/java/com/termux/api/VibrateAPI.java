package com.termux.api;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.provider.Settings;

import com.termux.api.util.ResultReturner;

import java.io.DataOutputStream;
import java.io.IOException;

public class VibrateAPI {

//
//
//    private final static String COMMAND_AIRPLANE_ON = "settings put global airplane_mode_on 1 \n " +
//            "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true\n ";
//
//    private final static String COMMAND_AIRPLANE_OFF = "settings put global airplane_mode_on 0 \n" +
//            " am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false\n ";
//
//    private final static String COMMAND_SU = "su";

    private static final String TAG = "AutoAirplaneModeService";
    private static final String COMMAND_FLIGHT_MODE_1 = "settings put global airplane_mode_on ";
    private static final String COMMAND_FLIGHT_MODE_2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state ";




    static void onReceive(TermuxApiReceiver apiReceiver, Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        int milliseconds = intent.getIntExtra("duration_ms", 3000);
        boolean force = intent.getBooleanExtra("force", false);


        // set On
        //writeCmd(COMMAND_AIRPLANE_ON);
        toggleAirplaneMode(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleAirplaneMode(false);
            }
        }, 200);



//        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        if (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT && !force) {
//            // Not vibrating since in silent mode and -f/--force option not used.
//        } else {
//            vibrator.vibrate(milliseconds);
//        }

        // set Off
        //writeCmd(COMMAND_AIRPLANE_OFF);



        ResultReturner.noteDone(apiReceiver, intent);
    }



    /**
     * Change the system settings of Airplane Mode.
     *
     * @param id the id
     * @return true is airplane mode should be enabled
     */
    public static void toggleAirplaneMode(boolean on) {
        String v = on ? "1" : "0";
        String command = COMMAND_FLIGHT_MODE_1 + v;
        executeCommandWithoutWait(command);
        String command2 = COMMAND_FLIGHT_MODE_2 + on;
        executeCommandWithoutWait(command2);
//        Settings.Global.putInt(getApplicationContext().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, on ? 1 : 0);
    }

    /**
     * Execute a command with root user.
     *
     * @param command the command to execute
     */
    private static void executeCommandWithoutWait(String command) {
        Log.d(TAG, "executeCommandWithoutWait");
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            Log.d(TAG, command);
        } catch (IOException e) {
            Log.e(TAG, "su command has failed due to: " + e.fillInStackTrace());
        }
    }
/*
    public static void writeCmd(String command){
        try {
            Process su = Runtime.getRuntime().exec(COMMAND_SU);
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes(command);
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            Log.e( "AutoAirplaneModeService" , "su command has failed due to: " + e.fillInStackTrace());
        }
    }*/

}
