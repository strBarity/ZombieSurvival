package main.timerhandler;

import main.Main;
import main.gamehandler.GameHandler;

public class WaveTimer implements Runnable {
    private static long waveCountdownSec = 0;
    private static long waveCountdownMin = 5;

    public static void resetWaveCountdown() {
        waveCountdownSec = 30;
        waveCountdownMin = 1;
    }

    public static void firstWaveCountdown() {
        waveCountdownSec = 30;
        waveCountdownMin = 0;
    }

    public static long getWaveCountdownMin() {
        return waveCountdownMin;
    }

    public static long getWaveCountdownSec() {
        return waveCountdownSec;
    }


    @Override
    public void run() {
        try {
            if (GameHandler.gameStarted) {
                if (waveCountdownSec > 0) waveCountdownSec--;
                if (waveCountdownSec <= 0 && GameHandler.wave > 0) {
                    if (waveCountdownMin > 0) {
                        waveCountdownSec = 59;
                        waveCountdownMin--;
                    } else if (GameHandler.wave < 100) {
                        GameHandler.nextWave();
                    }
                }
            }
        } catch (Exception e) {
            Main.printException(e);
        }
    }
}
