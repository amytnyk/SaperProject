package alexm.saperproject;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by alexm on 08.02.2018.
 */

public class SettingsThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private SettingsView settingsView;
    private boolean running;
    public static Canvas canvas;
    private int targetFPS = 30;
    private double averageFPS;

    public SettingsThread(SurfaceHolder surfaceHolder, SettingsView settingsView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.settingsView = settingsView;

    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / targetFPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.settingsView.draw(canvas);
                }
            } catch (Exception e) {
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try {
                this.sleep(waitTime);
            } catch (Exception e) {
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == targetFPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }
        }

    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }
}
