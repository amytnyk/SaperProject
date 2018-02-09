package alexm.saperproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.sin;

/**
 * Created by alexm on 14.01.2018.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    private MainThread thread;
    public Timer timer;
    private boolean thread_started = false;
    private List<List<Field>> fields;
    private int bombs_count = 3;
    private int size = 5;
    private int new_size = 5;
    private int new_bombs_count = 3;
    private int screenWidth = 720;
    private int screenHeight = 1200;
    private final int start_i = 3;
    private final int fields_position_y = screenHeight / 6;
    private List<Pair<Integer, Integer>> bombs;
    private List<List<Integer>> numbers;
    public int count = 0;
    public int turns = 0;
    private int max_time = 60;
    private Step step;
    boolean developer_mode = false;
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
        fields = new ArrayList<>();
        bombs = new ArrayList<>();
        step = Step.NotStarted;
        LoadSettings();
        Generate();
    }
    public void LoadSettings(){
        SharedPreferences prefs = getContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int s = prefs.getInt("size", 0);
        int b = prefs.getInt("bombs_count", 0);
        int t = prefs.getInt("time", 0) * 20;
        boolean d = prefs.getBoolean("developer_mode", false);
        if (s != 0 && b != 0 && t != 0){
            new_size = s;
            new_bombs_count = b;
            max_time = t;
            developer_mode = d;
        }
    }
    public void StartGame(){
        step = Step.Game;
        LoadSettings();
        Generate();
    }
    public void Generate(){
        size = new_size;
        bombs_count = new_bombs_count;
        if (step != Step.NotStarted) {
            timer.cancel();
        }
        fields.clear();
        bombs.clear();
        numbers = new ArrayList<>();
        for (int i = 0;i < size;i++){
            fields.add(new ArrayList<Field>());
            numbers.add(new ArrayList<Integer>());
            for (int j = 0;j < size;j++) {
                if (developer_mode == true) {
                    fields.get(i).add(Field.discovered);
                }
                else{
                    fields.get(i).add(Field.empty);
                }
                numbers.get(i).add(0);
            }
        }
        for(int i = 0;i < bombs_count;i++){
            Random r = new Random();
            int x = Math.abs(r.nextInt() % size);
            int y = Math.abs(r.nextInt() % size);
            if (bombs.contains(Pair.create(x, y))){
                i--;
                continue;
            }
            bombs.add(new Pair(x, y));
            if (developer_mode == true) {
                fields.get(x).set(y, Field.bomb);
            }
            if (x != 0){
                numbers.get(x - 1).set(y, numbers.get(x - 1).get(y) + 1);
            }
            if (y != 0){
                numbers.get(x).set(y - 1, numbers.get(x).get(y - 1) + 1);
            }
            if (x != size - 1){
                numbers.get(x + 1).set(y, numbers.get(x + 1).get(y) + 1);
            }
            if (y != size - 1){
                numbers.get(x).set(y + 1, numbers.get(x).get(y + 1) + 1);
            }
            if (x != 0 && y != 0){
                numbers.get(x - 1).set(y - 1, numbers.get(x - 1).get(y - 1) + 1);
            }
            if (y != 0 && x != size - 1){
                numbers.get(x + 1).set(y - 1, numbers.get(x + 1).get(y - 1) + 1);
            }
            if (x != size - 1 && y != size - 1){
                numbers.get(x + 1).set(y + 1, numbers.get(x + 1).get(y + 1) + 1);
            }
            if (y != size - 1 && x != 0){
                numbers.get(x - 1).set(y + 1, numbers.get(x - 1).get(y + 1) + 1);
            }
        }
        count = 0;
        turns = 0;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (count > max_time && step == Step.Game){
                    AlotOfTime();
                }
                if (step == Step.Game) {
                    count++;
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }
    public void AlotOfTime(){
        step = Step.Time;
    }
    public void BombActivated() {
        step = Step.Bomb;
    }
    public void OpenAll(int w, int h){
        if (w != 0 && fields.get(w - 1).get(h) != Field.discovered) {
            fields.get(w - 1).set(h, Field.discovered);
            turns++;
            if (numbers.get(w - 1).get(h) == 0 && fields.get(w - 1).get(h) != Field.bomb){
                OpenAll(w - 1, h);
            }
        }
        if (w != size - 1 && fields.get(w + 1).get(h) != Field.discovered) {
            fields.get(w + 1).set(h, Field.discovered);
            turns++;
            if (numbers.get(w + 1).get(h) == 0 && fields.get(w + 1).get(h) != Field.bomb){
                OpenAll(w + 1, h);
            }
        }
        if (h != 0 && fields.get(w).get(h - 1) != Field.discovered) {
            fields.get(w).set(h - 1, Field.discovered);
            turns++;
            if (numbers.get(w).get(h - 1) == 0 && fields.get(w).get(h - 1) != Field.bomb){
                OpenAll(w, h - 1);
            }
        }
        if (h != size - 1 && fields.get(w).get(h + 1) != Field.discovered) {
            fields.get(w).set(h + 1, Field.discovered);
            turns++;
            if (numbers.get(w).get(h + 1) == 0 && fields.get(w).get(h + 1) != Field.bomb){
                OpenAll(w, h + 1);
            }
        }
        if (w != size - 1 && h != size - 1 && fields.get(w + 1).get(h + 1) != Field.discovered) {
            fields.get(w + 1).set(h + 1, Field.discovered);
            turns++;
            if (numbers.get(w + 1).get(h + 1) == 0 && fields.get(w + 1).get(h + 1) != Field.bomb){
                OpenAll(w + 1, h + 1);
            }
        }
        if (w != 0 && h != size - 1 && fields.get(w - 1).get(h + 1) != Field.discovered) {
            fields.get(w - 1).set(h + 1, Field.discovered);
            turns++;
            if (numbers.get(w - 1).get(h + 1) == 0 && fields.get(w - 1).get(h + 1) != Field.bomb){
                OpenAll(w - 1, h + 1);
            }
        }
        if (w != 0 && h != 0 && fields.get(w - 1).get(h - 1) != Field.discovered) {
            fields.get(w - 1).set(h - 1, Field.discovered);
            turns++;
            if (numbers.get(w - 1).get(h - 1) == 0 && fields.get(w - 1).get(h - 1) != Field.bomb){
                OpenAll(w - 1, h - 1);
            }
        }
        if (w != size - 1 && h != 0 && fields.get(w + 1).get(h - 1) != Field.discovered) {
            fields.get(w + 1).set(h - 1, Field.discovered);
            turns++;
            if (numbers.get(w + 1).get(h - 1) == 0 && fields.get(w + 1).get(h - 1) != Field.bomb){
                OpenAll(w + 1, h - 1);
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (x > screenWidth / 2 - screenWidth / 6 && x < Resources.getSystem().getDisplayMetrics().widthPixels / 2 + screenWidth / 6 && y < screenHeight / 6 / 2){
                    step = Step.Game;
                    StartGame();
                } else if(x < (int)(screenWidth / 3.5) && y < screenHeight && y > screenHeight - screenHeight / 15){
                    Intent intent = new Intent(getContext(), MainMenu.class);
                    getContext().startActivity(intent);
                }
                y -= fields_position_y;
                // On fields
                if (y <  screenHeight / 2 && y > 0 && step == Step.Game){
                    int w = (int) x / (screenWidth / size);
                    int h = (int) y / (screenHeight / 2 / size);
                    if (fields.get(w).get(h) != Field.discovered){
                        fields.get(w).set(h, Field.discovered);
                        turns++;
                        if (bombs.contains(Pair.create(w, h)) == false){
                            if (turns + bombs_count == size * size){
                                step = Step.Win;
                            }
                            if (numbers.get(w).get(h) == 0){
                                OpenAll(w, h);
                            }
                            fields.get(w).set(h, Field.discovered);
                        }
                        else{
                            fields.get(w).set(h, Field.bomb);
                            BombActivated();
                        }
                    }
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
        Bitmap background = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.night_sky));
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, true);
        canvas.drawBitmap(background, 0, 0,  null);
        Bitmap start_button;
        if (step == Step.NotStarted) {
            start_button = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.start));
        }
        else {
            start_button = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.restart));
        }
        start_button = Bitmap.createScaledBitmap(start_button, screenWidth / 3, screenHeight / 6 / 2, true);
        canvas.drawBitmap(start_button, screenWidth / 2 - Resources.getSystem().getDisplayMetrics().widthPixels / 6, 0, null);

        Paint paint = new Paint();

        //Draw squares
        for (int j = 0;j < size;j++) {
            for (int i = 0;i < size;i++) {
                switch (fields.get(i).get(j)){
                    case bomb:
                        paint.setColor(Color.RED);
                        break;
                    case discovered:
                        paint.setColor(Color.GRAY);
                        break;
                    case empty:
                        paint.setColor(Color.LTGRAY);
                        break;
                }
                int pos_x = screenWidth / size * i;
                int pos_y = fields_position_y + screenHeight / 2 / size * j;
                canvas.drawRect(pos_x, pos_y, pos_x + screenWidth / size, pos_y + screenHeight / 2 / size,  paint);
            }
        }
        //Draw grid
        paint.setColor(Color.BLACK);
        for (int i = 1;i < size;i++) {
            canvas.drawLine(screenWidth / size * i, fields_position_y, screenWidth / size * i, fields_position_y + screenHeight / 2, paint);
        }
        for (int i = 1;i < size;i++) {
            canvas.drawLine(0, fields_position_y + screenHeight / 2 / size * i, screenWidth, fields_position_y + screenHeight / 2 / size * i, paint);
        }
        if (step != Step.NotStarted) {
            //Draw timer
            Paint paint2 = new Paint();
            paint2.setColor(Color.GREEN);
            paint2.setTextSize(screenHeight / 12);
            canvas.drawText(Integer.toString(count), screenWidth / 10, screenHeight / 12, paint2);
            //Draw turns
            canvas.drawText(Integer.toString(turns), screenWidth - screenWidth / 6, screenHeight / 12, paint2);
            //Draw numbers
            Paint paint3 = new Paint();
            paint3.setColor(Color.GREEN);
            paint3.setTextSize(screenHeight / 2 / size / 2);
            for (int j = 0; j < size; j++) {
                for (int i = 0; i < size; i++) {
                    if (fields.get(i).get(j) == Field.discovered ) {
                        canvas.drawText(Integer.toString(numbers.get(i).get(j)), screenWidth / size * i + (float) (screenWidth / size / 2.5), (float) (fields_position_y + screenHeight / 2 / size * (j + 0.5)), paint3);
                    }
                }
            }
        }
        Paint paint4 = new Paint();
        paint4.setColor(Color.GREEN);
        paint4.setTextSize(screenHeight / 30);
        String text = "";
        if (step == Step.Win){
            text = "You win!";
        }
        if (step == Step.Time){
            text = "Time is out!";
        }
        if (step == Step.Bomb){
            text = "Oh no, this is bomb!";
        }
        canvas.drawText(text, 0, screenHeight / 6 / 2 + screenHeight / 30,paint4);
        Bitmap mainMenu = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.settings));
        mainMenu = Bitmap.createScaledBitmap(mainMenu, (int)(screenWidth / 3.5), screenHeight / 15, true);
        canvas.drawBitmap(mainMenu, 0, screenHeight -  screenHeight / 15,null);
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
