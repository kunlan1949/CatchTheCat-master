package com.kunlan.surroundcrazycat.View;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;


import com.kunlan.surroundcrazycat.bgm.MusicService;
import com.surroundcrazycat.R;

public class GameView extends SurfaceView implements OnTouchListener {

    // 行数
    private static final int ROW = 9;
    // 列数
    private static final int COL = 9;
    // 障碍的数量
    private static final int BOCKS = COL * ROW / 5;
    // 屏幕宽度
    private int SCREEN_WIDTH;
    // 每个通道的宽度
    private int WIDTH;
    // 奇数行和偶数行通道间的位置偏差量
    private int DISTANCE;
    // 屏幕顶端和通道最顶端间的距离
    private int OFFSET;
    // 整个通道与屏幕两端间的距离
    private int length;
    // 做成神经猫动态图效果的单张图片
    private Drawable cat_drawable;
    // 背景图
    private Drawable background;
    // 神经猫动态图的索引
    private int index = 0;

    private Point[][] matrix;

    private Point cat;

    private Timer timer;

    private TimerTask timerttask;

    private Context context;

    //行走的步数
    private int steps;

    private boolean canMove = true;

    private int[] images = {R.drawable.cat1, R.drawable.cat2, R.drawable.cat3,
            R.drawable.cat4, R.drawable.cat5, R.drawable.cat6, R.drawable.cat7,
            R.drawable.cat8, R.drawable.cat9, R.drawable.cat10,
            R.drawable.cat11, R.drawable.cat12, R.drawable.cat13,
            R.drawable.cat14, R.drawable.cat15, R.drawable.cat16};

    private static SoundPool soundPool;
    private static final int[] musicId = {R.raw.game_over};
    private static Map<Integer, Integer> soundMap; //音效资源id与加载过后的音源id的映射关系表
    private static MediaPlayer music;
    private static boolean musicSt = true; //音乐开关
    private static boolean soundSt = true; //音效开关
    private int r;



    public  GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        matrix = new Point[ROW][COL];

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);

        soundMap = new HashMap<Integer, Integer>();
        soundMap.put(R.raw.game_over, soundPool.load(context, R.raw.game_over,1));
        soundMap.put(R.raw.put_step, soundPool.load(context, R.raw.put_step, 1));

        int r = new Random().nextInt(musicId.length);
        music = MediaPlayer.create(context, musicId[r]);
        music.setLooping(true);


        if (Build.VERSION.SDK_INT < 21) {
            cat_drawable = getResources().getDrawable(images[index]);
            background = getResources().getDrawable(R.drawable.bg);
        } else {
            cat_drawable = getResources().getDrawable(images[index], null);
            background = getResources().getDrawable(R.drawable.bg, null);
        }
        this.context = context;
        initGame();
        getHolder().addCallback(callback);
        setOnTouchListener(this);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
    }




    public static void playSound(int resId) {
        if (!soundSt)
            return;
        Integer soundId = soundMap.get(resId);
        if (soundId != null)
            soundPool.play(soundId, 1, 1, 1, 0, 1);
    }

    /**
     * 暂停音乐
     */
    public static void pauseMusic() {
        if (music.isPlaying())
            music.pause();
    }

    /**
     * 播放音乐
     */
    public static void startMusic() {
        if (musicSt)
            music.start();
    }



    // 初始化游戏
    private void initGame() {
        steps = 0;
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j] = new Point(j, i);
            }
        }
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j].setStatus(Point.STATUS.STATUS_OFF);
            }
        }
        cat = new Point(COL / 2 - 1, ROW / 2 - 1);
        getDot(cat.getX(), cat.getY()).setStatus(Point.STATUS.STATUS_IN);
        for (int i = 0; i < BOCKS; ) {
            int x = (int) ((Math.random() * 100) % COL);
            int y = (int) ((Math.random() * 100) % ROW);
            if (getDot(x, y).getStatus() == Point.STATUS.STATUS_OFF) {
                getDot(x, y).setStatus(Point.STATUS.STATUS_ON);
                i++;
            }
        }
    }

    // 绘图
    private void redraw() {
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.rgb(0, 0x8c, 0xd7));
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                DISTANCE = 0;
                if (i % 2 != 0) {
                    DISTANCE = WIDTH / 2;
                }
                Point dot = getDot(j, i);
                switch (dot.getStatus()) {
                    case STATUS_IN:
                        paint.setColor(0XFFEEEEEE);
                        break;
                    case STATUS_ON:
                        paint.setColor(0XFFFFAA00);
                        break;
                    case STATUS_OFF:
                        paint.setColor(0X74000000);
                        break;
                    default:
                        break;
                }
                canvas.drawOval(new RectF(dot.getX() * WIDTH + DISTANCE
                        + length, dot.getY() * WIDTH + OFFSET, (dot.getX() + 1)
                        * WIDTH + DISTANCE + length, (dot.getY() + 1) * WIDTH
                        + OFFSET), paint);
            }
        }
        int left;
        int top;
        if (cat.getY() % 2 == 0) {
            left = cat.getX() * WIDTH;
            top = cat.getY() * WIDTH;
        } else {
            left = (WIDTH / 2) + cat.getX() * WIDTH;
            top = cat.getY() * WIDTH;
        }
        // 此处神经猫图片的位置是根据效果图来调整的
        cat_drawable.setBounds(left - WIDTH / 6 + length, top - WIDTH / 2
                + OFFSET, left + WIDTH + length, top + WIDTH + OFFSET);
        cat_drawable.draw(canvas);
        background.setBounds(0, 0, SCREEN_WIDTH, OFFSET);
        background.draw(canvas);
        getHolder().unlockCanvasAndPost(canvas);
    }

    Callback callback = new Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();

            startTimer();
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            WIDTH = width / (COL + 1);
            OFFSET = height - WIDTH * ROW - 2 * WIDTH;
            length = WIDTH / 3;
            SCREEN_WIDTH = width;
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            stopTimer();
        }
    };

    // 开启定时任务
    private void startTimer() {
        timer = new Timer();
        timerttask = new TimerTask() {
            public void run() {
                gifImage();
            }
        };
        timer.schedule(timerttask, 50, 65);
    }

    // 停止定时任务
    public void stopTimer() {
        timer.cancel();
        timer.purge();
    }

    // 动态图
    private void gifImage() {
        index++;
        if (index > images.length - 1) {
            index = 0;
        }
        if (Build.VERSION.SDK_INT < 21) {
            cat_drawable = getResources().getDrawable(images[index]);
        } else {
            cat_drawable = getResources().getDrawable(images[index], null);
        }
        redraw();
    }

    // 获取通道对象
    private Point getDot(int x, int y) {
        return matrix[y][x];
    }

    // 判断神经猫是否处于边界
    private boolean inEdge(Point dot) {
        if (dot.getX() * dot.getY() == 0 || dot.getX() + 1 == COL
                || dot.getY() + 1 == ROW) {
            return true;
        }
        return false;
    }

    // 移动cat至指定点
    private void moveTo(Point dot) {
        dot.setStatus(Point.STATUS.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Point.STATUS.STATUS_OFF);
        cat.setXY(dot.getX(), dot.getY());
    }

    // 获取one在方向dir上的可移动距离
    private int getDistance(Point one, int dir) {
        int distance = 0;
        if (inEdge(one)) {
            return 1;
        }
        Point ori = one;
        Point next;
        while (true) {
            next = getNeighbour(ori, dir);
            if (next.getStatus() == Point.STATUS.STATUS_ON) {
                return distance * -1;
            }
            if (inEdge(next)) {
                distance++;
                return distance;
            }
            distance++;
            ori = next;
        }
    }

    // 获取dot的相邻点，返回其对象
    private Point getNeighbour(Point dot, int dir) {
        switch (dir) {
            case 1:
                return getDot(dot.getX() - 1, dot.getY());
            case 2:
                if (dot.getY() % 2 == 0) {
                    return getDot(dot.getX() - 1, dot.getY() - 1);
                } else {
                    return getDot(dot.getX(), dot.getY() - 1);
                }
            case 3:
                if (dot.getY() % 2 == 0) {
                    return getDot(dot.getX(), dot.getY() - 1);
                } else {
                    return getDot(dot.getX() + 1, dot.getY() - 1);
                }
            case 4:
                return getDot(dot.getX() + 1, dot.getY());
            case 5:
                if (dot.getY() % 2 == 0) {
                    return getDot(dot.getX(), dot.getY() + 1);
                } else {
                    return getDot(dot.getX() + 1, dot.getY() + 1);
                }
            case 6:
                if (dot.getY() % 2 == 0) {
                    return getDot(dot.getX() - 1, dot.getY() + 1);
                } else {
                    return getDot(dot.getX(), dot.getY() + 1);
                }
        }
        return null;
    }

    // cat的移动算法
    private void move() {
        if (inEdge(cat)) {
            failure();
            return;
        }
        Vector<Point> available = new Vector<>();
        Vector<Point> direct = new Vector<>();
        HashMap<Point, Integer> hash = new HashMap<>();
        for (int i = 1; i < 7; i++) {
            Point n = getNeighbour(cat, i);
            if (n.getStatus() == Point.STATUS.STATUS_OFF) {
                available.add(n);
                hash.put(n, i);
                if (getDistance(n, i) > 0) {
                    direct.add(n);
                }
            }
        }
        if (available.size() == 0) {
            win();
            canMove = false;
        } else if (available.size() == 1) {
            moveTo(available.get(0));
        } else {
            Point best = null;
            if (direct.size() != 0) {
                int min = 20;
                for (int i = 0; i < direct.size(); i++) {
                    if (inEdge(direct.get(i))) {
                        best = direct.get(i);
                        break;
                    } else {
                        int t = getDistance(direct.get(i),
                                hash.get(direct.get(i)));
                        if (t < min) {
                            min = t;
                            best = direct.get(i);
                        }
                    }
                }
            } else {
                int max = 1;
                for (int i = 0; i < available.size(); i++) {
                    int k = getDistance(available.get(i),
                            hash.get(available.get(i)));
                    if (k < max) {
                        max = k;
                        best = available.get(i);
                    }
                }
            }
            moveTo(best);
        }
        if (inEdge(cat)) {
            failure();
        }
    }

    // 通关失败
    private void failure() {
        Intent intent = new Intent(context, MusicService.class);
        context.stopService(intent);
        playSound(R.raw.game_over);
        Builder dialog = new Builder(context);
        dialog.setTitle("失败");
        dialog.setMessage("你让猫逃出啦(ˉ▽ˉ；)...");
        dialog.setCancelable(false);
        dialog.setNegativeButton("再玩一次", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                initGame();
                Intent intent = new Intent(context, MusicService.class);
                context.startService(intent);
                canMove = true;
            }
        });
//        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
        dialog.show();
    }

    // 通关成功
    private void win() {

        Builder dialog = new Builder(context);
        dialog.setTitle("成功");
        dialog.setMessage("你用" + (steps + 1) + "步捕捉到了猫耶( •̀ ω •́ )y");
        dialog.setCancelable(false);
        dialog.setNegativeButton("再玩一次", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                initGame();
                canMove = true;
            }
        });
//        dialog.setPositiveButton("取消", null);
        dialog.show();
    }

    // 触屏事件
    public boolean onTouch(View v, MotionEvent event) {

        int x, y;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getY() <= OFFSET) {
                return true;
            }
            y = (int) ((event.getY() - OFFSET) / WIDTH);
            if (y % 2 == 0) {
                if (event.getX() <= length
                        || event.getX() >= length + WIDTH * COL) {
                    return true;
                }
                x = (int) ((event.getX() - length) / WIDTH);
            } else {
                if (event.getX() <= (length + WIDTH / 2)
                        || event.getX() > (length + WIDTH / 2 + WIDTH * COL)) {
                    return true;
                }
                x = (int) ((event.getX() - WIDTH / 2 - length) / WIDTH);
            }
            if (x + 1 > COL || y + 1 > ROW) {
                return true;
            } else if (inEdge(cat) || !canMove) {
                initGame();
                canMove = true;
                return true;
            } else if (getDot(x, y).getStatus() == Point.STATUS.STATUS_OFF) {
                getDot(x, y).setStatus(Point.STATUS.STATUS_ON);
                move();
                steps++;
            }
        }
        return true;
    }

    // 按键事件
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopTimer();
        }
        return super.onKeyDown(keyCode, event);
    }


}
