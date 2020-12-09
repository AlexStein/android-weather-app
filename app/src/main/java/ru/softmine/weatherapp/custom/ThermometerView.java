package ru.softmine.weatherapp.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.R;

public class ThermometerView extends View {

    private static final String TAG = "ThermometerView";

    // Цвет фона, подложки
    private int backColor = Color.GRAY;

    // Цвет индикатора для положительных температур
    private int positiveColor = Color.RED;

    // Цвет индикатора для положительных температур
    private int negativeColor = Color.BLUE;


    // Изображение батареи
    private RectF mainRectangle = new RectF();

    // Изображение температуры
    private RectF levelBar = new RectF();

    // Изображение пузырька
    private RectF headCircle = new RectF();
    private RectF headInnerCircle = new RectF();

    // Метка нуля
    private Rect zeroMark = new Rect();

    // Краска основы
    private Paint backPaint;
    // Краска уровня
    private Paint levelPaint;

    private float width = 0;
    private float height = 0;
    private float padding;
    private float level_zero;

    // Температрура
    private int level = 0;

    public ThermometerView(Context context) {
        super(context);
        init();
    }

    public ThermometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    // Инициализация атрибутов пользовательского элемента из xml
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView, 0, 0);

        backColor = typedArray.getColor(R.styleable.ThermometerView_back_color, Color.BLACK);
        positiveColor = typedArray.getColor(R.styleable.ThermometerView_level_positive_color, Color.RED);
        negativeColor = typedArray.getColor(R.styleable.ThermometerView_level_negative_color, Color.BLUE);

        level = typedArray.getInteger(R.styleable.ThermometerView_level_degrees, 0);

        typedArray.recycle();
    }

    // Начальная инициализация полей класса
    private void init() {
        backPaint = new Paint();
        backPaint.setColor(backColor);
        backPaint.setStyle(Paint.Style.FILL);

        int levelColor = positiveColor;
        if (level < 0) {
            levelColor = negativeColor;
        }

        levelPaint = new Paint();
        levelPaint.setColor(levelColor);
        levelPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");

        // Получить реальные ширину и высоту
        // Ширина элемента
        width = w - getPaddingLeft() - getPaddingRight();
        // Высота элемента
        height = h - getPaddingTop() - getPaddingBottom();

        padding = width / 5;
        level_zero = (height - width / 2) / 2 + padding;

        float level_top = level_zero - (height - padding - width) / 100 * level;

        if (Logger.VERBOSE) {
            Log.v(TAG, String.format("level: %d", level));
            Log.v(TAG, String.format("width: %.2f", width));
            Log.v(TAG, String.format("height: %.2f", height));
            Log.v(TAG, String.format("level_top: %.2f", level_top));
        }

        // Отрисовка градусника
        mainRectangle.set(padding,
                0,
                width - padding,
                height - width / 2);

        // Отрисовка нижней колбы
        headCircle.set(0,
                height - width,
                width,
                height);

        // Метка нуля
        zeroMark.set((int) (width - padding),
                (int) level_zero - 2,
                (int) width,
                (int) level_zero + 2);

        // Наполенение колбы
        headInnerCircle.set(padding,
                height - width + padding,
                width - padding,
                height - padding);

        // Уровень
        updateBarRect();
    }

    private void updateBarRect() {
        levelBar.set(2 * padding,
                levelTopPosition(),
                width - 2 * padding,
                height - width / 2);
    }

    private float levelTopPosition() {
        return level_zero - (height - width / 2) / 100 * level;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.d(TAG, "draw");

        float round = mainRectangle.width() / 2;

        int levelColor = positiveColor;
        if (this.level < 0) {
            levelColor = negativeColor;
        }

        levelPaint.setColor(levelColor);

        canvas.drawRoundRect(mainRectangle, round, round, backPaint);

        canvas.drawRect(zeroMark, backPaint);

        round = headCircle.width() / 2;
        canvas.drawRoundRect(headCircle, round, round, backPaint);

        round = headInnerCircle.width() / 2;
        canvas.drawRoundRect(headInnerCircle, round, round, levelPaint);

        round = levelBar.width() / 2;

        updateBarRect();
        canvas.drawRoundRect(levelBar, round, round, levelPaint);
    }

    public void setLevel(int level) {
        this.level = level;
        invalidate();
    }
}
