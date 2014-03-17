package nl.frankkie.nowwatch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Draw a XKCD Style clock
 *
 * @author FrankkieNL
 */
public class NowClockActivity extends Activity {

    //settings
    //nowclock_earth; nowclock_all; nowclock_no_continent
    int sEarthImageId = R.drawable.nowclock_no_continent;
    boolean sDrawImage = true;
    boolean sDrawInnerCircle = true;
    boolean sDrawOuterCircleYellow = true;
    boolean sDrawOuterCircleGray = true;
    boolean sDrawHourLines = true;
    boolean sDrawText = true;

    MyClockView clockView;
    int yellowRingSize = 12; //bigger number is smaller ring
    Paint paint = new Paint();
    RectF outerCircle = null;
    RectF innerCircle = null;
    Bitmap clockImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSettings();
        initUI();
    }

    public void initSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sDrawImage = prefs.getBoolean("sDrawImage", sDrawImage);
        sDrawInnerCircle = prefs.getBoolean("sDrawInnerCircle", sDrawInnerCircle);
        sDrawOuterCircleYellow = prefs.getBoolean("sDrawOuterCircleYellow", sDrawOuterCircleYellow);
        sDrawOuterCircleGray = prefs.getBoolean("sDrawOuterCircleGray", sDrawOuterCircleGray);
        sDrawHourLines = prefs.getBoolean("sDrawHourLines", sDrawHourLines);
        sDrawText = prefs.getBoolean("sDrawText", sDrawText);
        sEarthImageId = prefs.getInt("sEarthImageId", sEarthImageId);
    }

    public void initUI() {
        //FullScreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        clockView = new MyClockView(this);
        setContentView(clockView);
    }

    public class MyClockView extends View {

        public MyClockView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //Get maximum square drawing size, my using smallest dimension
            //Truesmart watch should have a square screen.
            int size = Math.min(canvas.getHeight(), canvas.getWidth());
            int center = size / 2;

            canvas.drawColor(Color.WHITE);
            //Lets paint a clock                        
            paint.setAntiAlias(true);

            //draw outer circle
            if (sDrawOuterCircleYellow) {
                paint.setColor(Color.YELLOW);
                paint.setStyle(Paint.Style.FILL);
                if (outerCircle == null) {
                    outerCircle = new RectF(0, 0, size, size);
                }
                canvas.drawOval(outerCircle, paint);
            }

            if (sDrawOuterCircleGray) {
                paint.setColor(Color.LTGRAY);
                paint.setStyle(Paint.Style.FILL);
                if (outerCircle == null) {
                    outerCircle = new RectF(0, 0, size, size);
                }
                canvas.drawArc(outerCircle, 0, 180, true, paint);
            }

            //Draw hour-lines    
            if (sDrawHourLines) {
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                for (int i = 0; i < 24; i++) {
                    float x = (float) Math.cos(Math.toRadians((360.0 / 24.0) * i)) * center + center;
                    float y = (float) Math.sin(Math.toRadians((360.0 / 24.0) * i)) * center + center;
                    canvas.drawLine(center, center, x, y, paint);
                }
            }

            //draw inner circle
            if (sDrawInnerCircle) {
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                if (innerCircle == null) {
                    innerCircle = new RectF(size / yellowRingSize, size / yellowRingSize, size - size / yellowRingSize, size - size / yellowRingSize);
                }
                canvas.drawOval(innerCircle, paint);
            }

            //draw earth    
            if (sDrawImage) {
                if (clockImage == null) {
                    Resources res = getResources();
                    try {
                        clockImage = BitmapFactory.decodeResource(res, sEarthImageId);
                    } catch (Exception e) {
                        //Probaly a wrong id, try default
                        clockImage = BitmapFactory.decodeResource(res, R.drawable.nowclock_no_continent);
                    }
                }
                float timeDegrees = 0;
                Calendar c = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                int hour = c.get(Calendar.HOUR_OF_DAY); //24
                int min = c.get(Calendar.MINUTE); //60
                timeDegrees += (360.0 / 24.0) * (hour + 12); //do make sure we count from noon, not from midnight
                timeDegrees += ((360.0 / 24.0) / 60.0) * min;
                canvas.rotate(timeDegrees, center, center);
                int imageResize = -2;
                canvas.drawBitmap(clockImage, null, new Rect(size / (yellowRingSize + imageResize), size / (yellowRingSize + imageResize), size - size / (yellowRingSize + imageResize), size - size / (yellowRingSize + imageResize)), paint);
                canvas.rotate(-timeDegrees, center, center);
            }

            if (sDrawText) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                paint.setTextSize((float) (size / 40.0));
                paint.setFakeBoldText(true);
                int vijftien = 15;
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("6AM", vijftien, size / 2, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("6PM", size - vijftien, size / 2, paint);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Noon", size / 2, vijftien, paint);
                canvas.drawText("Midnight", size / 2, size - vijftien, paint);
            }
        }
    }
}
