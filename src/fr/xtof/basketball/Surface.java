package fr.xtof.basketball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class Surface extends View {
  public Surface(Context c) {
    super(c);
  }
  public Surface(Context c, AttributeSet attribs) {
    super(c,attribs);
  }
  public Surface(Context c, AttributeSet attribs, int defStyle) {
    super(c,attribs,defStyle);
  }
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Paint textPaint = new Paint();
    textPaint.setColor(Color.WHITE);
    // canvas.drawLine(0,0,canvas.getWidth(), canvas.getHeight(), textPaint);
  }
}

