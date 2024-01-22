package com.example.micontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CircularImageView extends AppCompatImageView {

    private Paint paint;
    private BitmapShader shader;
    private Bitmap imageBitmap;

    public CircularImageView(Context context) {
        super(context);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (imageBitmap == null) {
            imageBitmap = getBitmapFromDrawable(getDrawable());
            if (imageBitmap != null) {
                shader = new BitmapShader(imageBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                paint.setShader(shader); // Set the BitmapShader to the paint
            }
        }

        if (shader != null) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int minEdge = Math.min(viewWidth, viewHeight);

            // Calculate the radius for the circular image
            float radius = minEdge / 2f;

            // Calculate the scaling factor to fit the entire image inside the circle
            float scaleFactor = Math.max(viewWidth / (float) imageBitmap.getWidth(), viewHeight / (float) imageBitmap.getHeight());

            // Scale and center the image inside the circle
            Matrix matrix = new Matrix();
            matrix.setScale(scaleFactor, scaleFactor);
            matrix.postTranslate((viewWidth - imageBitmap.getWidth() * scaleFactor) / 2f, (viewHeight - imageBitmap.getHeight() * scaleFactor) / 2f);
            shader.setLocalMatrix(matrix);

            canvas.drawCircle(viewWidth / 2f, viewHeight / 2f, radius, paint);
        }
    }


    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
