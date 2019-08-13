package filedstrength.jingxun.com.meter.Widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import filedstrength.jingxun.com.meter.Constant.Constant;
import filedstrength.jingxun.com.meter.R;
import filedstrength.jingxun.com.meter.Utils.SharePreferenceUtils;

@SuppressLint("HandlerLeak")
public class ChartView extends View {

    private int[] data_screen;
    private int[] data_power;
    private int[] data_total;
    private int flag;
    private int XPoint = (int) (60 * Constant.SCREEN_WIDTH / 480);
    private int YPoint = (int) (300 * Constant.SCREEN_WIDTH / 480);
    private int XScale = (int) (8 * Constant.SCREEN_WIDTH / 480); // �̶ȳ���
    private int YScale = (int) (40 * Constant.SCREEN_WIDTH / 480);
    private int XLength = (int) (380 * Constant.SCREEN_WIDTH / 480);
    private int YLength = (int) (240 * Constant.SCREEN_WIDTH / 480);
    private int dotRadius = (int) (4 * Constant.SCREEN_WIDTH / 480);

    private String pow;
    private ArrayList<String> plist = new ArrayList<String>();
    private Paint paint;
    private Paint paintC1;
    private Paint paintDot;
    private int layoutWidth;
    private int layoutHeight;
    private Paint paint_scale;
    private boolean IsRecPow;
    private float maxCircleRadius;
    private float circleX;
    private float circleY;
    private float firstPow_scale;
    private float power_prior;
    private float power_next;
    private BigDecimal decimalTemp;
    private int MaxDataSize = 300;
    private int mXDirection;
    private float maxFieldStrength = 128;
    private int display_Tag;
    private String disPlayTag = "DisPlayTag";
    private Context context;

    private List<Float> data = new ArrayList<Float>();

    private List<Float> mXDirectionData = new ArrayList<Float>();

    private String[] YLabel = new String[YLength / YScale];

    private String[] Y_Label = new String[YLength / YScale];

    private String[] Default = {"0", "-26.0", "-52.0", "-78.0", "-104.0", "-130.0"};

    private StateThread sateThread = new StateThread();

    private class StateThread extends Thread {
        @Override
        public void run() {
            while (IsRecPow) {
                if (data.size() >= MaxDataSize) {
                    data.remove(0);
                }
                if (mXDirectionData.size() >= MaxDataSize) {
                    mXDirectionData.remove(0);
                }
                System.out.println("----rec-pow:" + pow);
                if (pow != null && pow != "" && mXDirection != 0) {
//                    if (display_Tag == 1) {
//                        data.add(-(float) Integer.parseInt(pow));
//                    } else {
                        data.add((float) Integer.parseInt(pow));
//                    }
                    mXDirectionData.add((float) mXDirection);
                }


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0x1234);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x1234) {
                ChartView.this.invalidate();
                Constant.POWTAG = 3;
            }
        }

        ;
    };
    private float firstPow;

    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();
        IsRecPow = false;
    }


    public ChartView(Context context, int flag, int layoutWidth, int layoutHeight) {
        super(context);

        this.context = context;
        this.layoutWidth = layoutWidth;
        this.layoutHeight = layoutHeight;
        if (layoutWidth >= layoutHeight) {
            maxCircleRadius = layoutHeight / 2 - YScale;
        } else {
            maxCircleRadius = layoutWidth / 2 - YScale;
        }
        circleX = Constant.SCREEN_WIDTH / 2;
        circleY = Constant.SCREEN_HEIGHT / 2 - layoutHeight / 2-50;

        IsRecPow = true;
        sateThread.start();
        this.flag = flag;
        if (flag == 1) {
            this.MaxDataSize = XLength/XScale;
        }else{
            this.MaxDataSize = 300;
        }

        data_screen = new int[]{90, 65, 80, 115};
        data_power = new int[]{110, 75, 50, 70};
        data_total = new int[4];
        for (int i = 0; i < 4; i++)
            data_total[i] = data_screen[i] + data_power[i];
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(6);
        paint.setTextSize(32);
        paintC1 = new Paint();
        paintC1.setDither(true);
        paintC1.setAntiAlias(true);
        paintC1.setStyle(Paint.Style.STROKE);
        paintC1.setColor(Color.WHITE);

        paint_scale = new Paint();
        paint_scale.setAntiAlias(true);
        paint_scale.setColor(Color.WHITE);
        paint_scale.setStrokeWidth(1);

        paintDot = new Paint();
        paintDot.setDither(true);
        paintDot.setAntiAlias(true);
        paintDot.setColor(Color.RED);
    }


    public void drawAxis(Canvas canvas) {

        if (flag == 1) {
            paint_scale.setColor(Color.WHITE);
            if (TextUtils.isEmpty(pow)) {
                firstPow = 0;
                firstPow_scale = 50;
            } else {
                firstPow = Float.parseFloat(pow);
//				firstPow_scale = (firstPow * 2) / (5 * 10);

//                display_Tag = SharePreferenceUtils.getIntValue(context, disPlayTag, 1);
//                if (display_Tag == 1) {
//                    firstPow_scale = -26;
//                } else {
                    firstPow_scale = 50;
//                }
                BigDecimal bgDecimal = new BigDecimal(firstPow_scale);
                firstPow_scale = bgDecimal
                        .setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

                Log.i("enr", "firstPow_scale: " + firstPow_scale);
            }
//            for (int i = 0; i < YLabel.length; i++) {
//                YLabel[i] = Default[i];
//            }
//            if (display_Tag == 1 || firstPow == 0) {
//                for (int i = 0; i < YLabel.length; i++) {
//                    YLabel[i] = Default[i];
//                }
//            } else {
                for (int i = 0; i < YLabel.length; i++) {
                    BigDecimal yLabel_Decimal = new BigDecimal(i * firstPow_scale);
                    float value = yLabel_Decimal.setScale(1,
                            BigDecimal.ROUND_HALF_UP).floatValue();
                    YLabel[i] = value + "";
                }
//            }

            canvas.drawLine(XPoint + (YLabel[YLabel.length - 1].length() * 5), YPoint - YLength,
                    XPoint + (YLabel[YLabel.length - 1].length() * 5), YPoint, paint);
            canvas.drawLine(XPoint + (YLabel[YLabel.length - 1].length() * 5), YPoint - YLength,
                    XPoint + (YLabel[YLabel.length - 1].length() * 5) - 10, YPoint
                            - YLength + 20, paint);
            canvas.drawLine(XPoint + (YLabel[YLabel.length - 1].length() * 5), YPoint - YLength,
                    XPoint + (YLabel[YLabel.length - 1].length() * 5) + 10, YPoint
                            - YLength + 20, paint);

//            canvas.drawText(display_Tag == 1 ? "/dBm" : "", XPoint + (YLabel[YLabel.length - 1].length() * 5) + 15, YPoint - YLength + 30, paint);
            canvas.drawText("", XPoint + (YLabel[YLabel.length - 1].length() * 5) + 15, YPoint - YLength + 30, paint);
            canvas.drawText("/t", XPoint + (YLabel[YLabel.length - 1].length() * 5) + XLength - 30,
                    YPoint + 40, paint);

            for (int i = 0; i < YLabel.length; i++) {
                canvas.drawLine(XPoint + (YLabel[YLabel.length - 1].length() * 5), YPoint - i * YScale,
                        XPoint + (YLabel[YLabel.length - 1].length() * 5) + XLength,
                        YPoint - i * YScale, paint_scale);

                canvas.drawText(YLabel[i], XPoint - (30 + YLabel[i].length() * 8), YPoint - i * YScale,
                        paint);//
            }
            canvas.drawLine(XPoint + (YLabel[YLabel.length - 1].length() * 5), YPoint,
                    XPoint + (YLabel[YLabel.length - 1].length() * 5) + XLength, YPoint, paint);
            canvas.drawLine(XPoint + (YLabel[YLabel.length - 1].length() * 5) + XLength - 20, YPoint - 10,
                    XPoint + (YLabel[YLabel.length - 1].length() * 5) + XLength, YPoint, paint);
            canvas.drawLine(XPoint + (YLabel[YLabel.length - 1].length() * 5) + XLength - 20, YPoint + 10,
                    XPoint + (YLabel[YLabel.length - 1].length() * 5) + XLength, YPoint, paint);
        } else if (flag == 2) {
            canvas.drawCircle(circleX, circleY, maxCircleRadius, paintC1);
            canvas.drawCircle(circleX, circleY, maxCircleRadius / 4, paintC1);
            canvas.drawCircle(circleX, circleY, 2 * maxCircleRadius / 4, paintC1);
            canvas.drawCircle(circleX, circleY, 3 * maxCircleRadius / 4, paintC1);

            canvas.drawLine(circleX, circleY-maxCircleRadius, circleX, circleY + maxCircleRadius, paint);
            canvas.drawLine(circleX-maxCircleRadius, circleY, circleX+maxCircleRadius, circleY, paint);
            canvas.drawText("北", circleX-15, circleY-maxCircleRadius - 20, paint);
            canvas.drawText("南", circleX-15,circleY+maxCircleRadius+40, paint);
            canvas.drawText("西", circleX-maxCircleRadius-40, circleY+15, paint);
            canvas.drawText("东", circleX+maxCircleRadius+20, circleY+15, paint);

        }
    }

    public void drawChart(Canvas canvas) {
        if (flag == 1) {
            paint_scale.setColor(Color.MAGENTA);
            System.out.println("Data.size = " + data.size());
            if (data.size() > 1) {
                for (int i = 1; i < data.size(); i++) {
                    if (firstPow_scale > 0) {
                        decimalTemp = new BigDecimal(data.get(i - 1) / firstPow_scale);
                        power_prior = decimalTemp.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

                        Log.i("power", "power_prior: " + power_prior);

                        decimalTemp = new BigDecimal(data.get(i) / firstPow_scale);
                        power_next = decimalTemp.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

                        Log.i("power", "power_next: " + power_next);
                    } else {

                        decimalTemp = new BigDecimal(data.get(i - 1) / firstPow_scale);
                        power_prior = decimalTemp.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

                        Log.i("power", "power_prior: " + power_prior);

                        decimalTemp = new BigDecimal(data.get(i) / firstPow_scale);
                        power_next = decimalTemp.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

                        Log.i("power", "power_next: " + power_next);

//                        power_prior = 0.0f;
//                        power_next = 0.0f;
                    }

                    canvas.drawLine(XPoint + (YLabel[YLabel.length - 1].length() * 5) + (i - 1) * XScale,
                            YPoint - Math.abs(power_prior) * YScale,
                            XPoint + (YLabel[YLabel.length - 1].length() * 5) + i * XScale,
                            YPoint - Math.abs(power_next) * YScale, paint_scale);
                }
            }
        } else if (flag == 2) {
            System.out.println("Data.size = " + data.size());
//            display_Tag = SharePreferenceUtils.getIntValue(context, disPlayTag, 1);
//            System.out.println("display_Tag = " + display_Tag);
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {


                    double dataAbs = ((255.0 - Math.abs(data.get(i))) / 2);
                    Log.i("ChartView", "--dataAbs: " + dataAbs);
                    double percent = dataAbs / maxFieldStrength;
                    if (data.get(i) < 25) {
                        percent = 1;
                    }
                    if(data.get(i) > 215){
                        percent = 0;
                    }
                    Log.i("ChartView", "--percent: " + percent);
                    Log.i("ChartView", "--mXDirection: " + mXDirectionData.get(i));
                    double radians = Math.toRadians(mXDirectionData.get(i));
                    float dotX = (float) (circleX + maxCircleRadius * Math.sin(radians) * percent);
                    float dotY = (float) (circleY - maxCircleRadius * Math.cos(radians) * percent);
                    canvas.drawCircle(dotX, dotY, dotRadius, paintDot);
//                    }

                }
            }

        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(R.drawable.welcomebg);
        drawAxis(canvas);
        drawChart(canvas);
    }

    public void setFieldStrength(String pow, int mXDirection) {
        this.pow = pow;
        this.mXDirection = mXDirection;
        Log.i("setFieldStrength", "--pow: " + pow);
        Log.i("setFieldStrength", "--mXDirection: " + mXDirection);
    }

}
