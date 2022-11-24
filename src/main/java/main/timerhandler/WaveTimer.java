package main.timerhandler;

import main.gamehandler.GameHandler;

public class WaveTimer implements Runnable {
    private static long waveCountdownSec = 0;
    private static long waveCountdownMin = 5;

    public static void resetWaveCountdown() {
        waveCountdownSec = 0;
        waveCountdownMin = 5;
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
        if (GameHandler.gameStarted) {
            waveCountdownSec--;
            if (waveCountdownSec <= 0 && GameHandler.wave > 0) {
                if (waveCountdownMin > 0) {
                    waveCountdownSec = 59;
                    waveCountdownMin--;
                } else {
                    GameHandler.nextWave();
                }
            }
        }
    }
}
