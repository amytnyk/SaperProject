package alexm.saperproject;

/**
 * Created by alexm on 08.02.2018.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by alexm on 14.01.2018.
 */

public class SettingsView extends SurfaceView implements SurfaceHolder.Callback{
    private SettingsThread thread;
    private boolean thread_started = false;
    private int screenWidth = 720;
    private int screenHeight = 1200;
    private int time = 3;
    private int bombs_count = 4;
    private int size = 4;
    private boolean developer_mode = false;
    private int start_i = 3;
    private int padding = 50;
    private final int fields_position_y = screenHeight / 6;
    Bitmap background;
    Bitmap go_back;

    public SettingsView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new SettingsThread(getHolder(), this);
        setFocusable(true);
        background = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.night_sky));
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, true);
        go_back = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.go_back));
        go_back = Bitmap.createScaledBitmap(go_back, screenWidth / 4, screenHeight / 16, true);
        LoadSettings();

    }
    public void LoadSettings(){
        SharedPreferences prefs = getContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int s = prefs.getInt("size", 0);
        int b = prefs.getInt("bombs_count", 0);
        int t = prefs.getInt("time", 0) * 20;
        boolean d = prefs.getBoolean("developer_mode", false);
        if (s != 0 && b != 0 && t != 0){
            size = s;
            bombs_count = b;
            time = t;
            developer_mode = d;
        }
    }
    public void Save(){
        SharedPreferences prefs = getContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("size", size);
        editor.putInt("bombs_count", bombs_count);
        editor.putInt("time", time);
        editor.putBoolean("developer_mode", developer_mode);
        editor.commit();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if(x > screenWidth / 4 && x < screenWidth / 4 + screenWidth / 10 * (10 - start_i) && y > screenHeight / 3 && y < screenHeight / 3  + screenHeight / 10){
                    x -= screenWidth / 4;
                    int w = (int) (x / (screenWidth / 10));
                    size = w + start_i;
                } else if(x > screenWidth / 4 && x < screenWidth / 4 + screenWidth / 10 * (10 - start_i) && y > screenHeight / 3 + screenWidth / 10 + padding && y <  screenHeight / 3 + screenWidth / 10 + padding + screenWidth / 10){
                    x -= screenWidth / 4;
                    int w = (int) (x / (screenWidth / 10));
                    bombs_count = w + start_i;
                } else if(x > screenWidth / 4 + screenWidth / 10 && x < screenWidth / 4 + screenWidth / 10 * 6 && y > screenHeight / 3 + screenWidth / 10 + padding + padding + screenWidth / 10 && y < screenHeight / 3 + screenWidth / 10 + padding + padding + screenWidth / 10 + screenWidth / 10){
                    x -= screenWidth / 4 + screenWidth / 10;
                    int w = (int) (x / (screenWidth / 10));
                    time = w + 1;
                }else if(x > screenWidth / 2 - screenWidth / 8 && x < screenWidth / 2 + screenWidth / 8 && y > screenHeight - screenHeight / 16){
                    Save();
                    Intent intent = new Intent(getContext(), MainMenu.class);
                    getContext().startActivity(intent);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }



    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawBitmap(background, 0, 0,  null);
        Paint paint5 = new Paint();
        paint5.setColor(Color.GRAY);
        Paint paint6 = new Paint();
        paint6.setColor(Color.GREEN);
        paint6.setTextSize(20);
        Paint paint7 = new Paint();
        paint7.setColor(Color.BLACK);
        Paint paint9 = new Paint();
        paint9.setStrokeWidth(5);
        paint9.setColor(Color.RED);
        Paint paint4 = new Paint();
        paint4.setColor(Color.GREEN);
        paint4.setTextSize(screenHeight / 40);
        canvas.drawText("Size: ", 0, (float) (screenHeight / 3 + screenHeight / 25), paint4);
        canvas.drawText("Bomb: ", 0, (float) (screenHeight / 3 + screenHeight / 25 + screenWidth / 10 + padding), paint4);
        canvas.drawText("Time: ", 0, (float) (screenHeight / 3 + screenHeight / 25 + screenWidth / 10 + padding + padding + screenWidth / 10), paint4);
        canvas.drawText("Developer mode: ", 0, (float) (screenHeight / 3 + screenHeight / 25 + screenWidth / 10 + padding + padding + screenWidth / 10 + padding + screenWidth / 10), paint4);
        for (int i = start_i;i < 10;i++) {
            int pos_x = screenWidth / 4 + screenWidth / 10 * (i - start_i);
            int pos_y = screenHeight / 3;
            canvas.drawRect(pos_x, pos_y, pos_x + screenWidth / 10, pos_y + screenWidth / 10, paint5);
            canvas.drawText(Integer.toString(i), pos_x + screenWidth / 25, pos_y + screenHeight / 30, paint6);
            if(i == size || i - 1 == size) {
                canvas.drawLine(pos_x, pos_y, pos_x, pos_y + screenWidth / 10, paint9);
            }
            else{
                canvas.drawLine(pos_x, pos_y, pos_x, pos_y + screenWidth / 10, paint7);
            }
        }
        for (int i = start_i;i < 10;i++) {
            int pos_x = screenWidth / 4 + screenWidth / 10 * (i - start_i);
            int pos_y = screenHeight / 3 + screenWidth / 10 + padding;
            canvas.drawRect(pos_x, pos_y, pos_x + screenWidth / 10, pos_y + screenWidth / 10, paint5);
            canvas.drawText(Integer.toString(i), pos_x + screenWidth / 25, pos_y + screenHeight / 30, paint6);
            if(i == bombs_count || i - 1 == bombs_count) {
                canvas.drawLine(pos_x, pos_y, pos_x, pos_y + screenWidth / 10, paint9);
            }
            else{
                canvas.drawLine(pos_x, pos_y, pos_x, pos_y + screenWidth / 10, paint7);
            }
        }
        for (int i = 1;i < 6;i++) {
            int pos_x = screenWidth / 4 + screenWidth / 10 * i;
            int pos_y = screenHeight / 3 + screenWidth / 10 + padding + padding + screenWidth / 10;
            canvas.drawRect(pos_x, pos_y, pos_x + screenWidth / 10, pos_y + screenWidth / 10, paint5);
            canvas.drawText(Integer.toString(i * 20), pos_x + screenWidth / 25, pos_y + screenHeight / 30, paint6);
            if(i == time || i - 1 == time) {
                canvas.drawLine(pos_x, pos_y, pos_x, pos_y + screenWidth / 10, paint9);
            }
            else{
                canvas.drawLine(pos_x, pos_y, pos_x, pos_y + screenWidth / 10, paint7);
            }
        }

        canvas.drawBitmap(go_back, screenWidth / 2 - screenWidth / 8, screenHeight - screenHeight / 16,  null);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (thread_started) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            thread_started = true;
            thread.setRunning(true);
            thread.start();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }
}
