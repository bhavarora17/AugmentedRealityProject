package app.com.augmentedrealitytraining.training.ar;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import app.com.augmentedrealitytraining.R;

/**
 * Created by Abhijit on 11/14/2015.
 */




public class NoteRelativeLayout extends RelativeLayout {

    private Context context;
    private int _xDelta;
    private int _yDelta;
    private float scale = 1f;
    private ScaleGestureDetector SGD;

    public NoteRelativeLayout(Context context) {
        super(context);
        this.context=context;
        SGD = new ScaleGestureDetector(context, new ScaleListener(this));
        setWillNotDraw(false);

    }

    public NoteRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        SGD = new ScaleGestureDetector(context, new ScaleListener(this));
        setWillNotDraw(false);
    }

    public NoteRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        SGD = new ScaleGestureDetector(context, new ScaleListener(this));
        setWillNotDraw(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NoteRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        SGD = new ScaleGestureDetector(context, new ScaleListener(this));
        setWillNotDraw(false);
    }

    private class ScaleListener extends ScaleGestureDetector.

            SimpleOnScaleGestureListener {

        private ViewGroup viewGroup;

        public ScaleListener(ViewGroup viewGroup) {
            super();
            this.viewGroup = viewGroup;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(1f, Math.min(scale, 1.5f));

            viewGroup.invalidate();
            return true;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.scale(scale, scale);
        super.onDraw(canvas);

    }


    @Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent event) {
     //   context.setOn
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) getLayoutParams();
        int h1=layoutParams.topMargin;


        int rightDiff = 0;
        final View notes = ((TrainingActivity)context).findViewById(R.id.notes);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                DrawerLayout.LayoutParams lParams = (DrawerLayout.LayoutParams) getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                rightDiff=lParams.rightMargin-X;
                break;
            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;

*/            case MotionEvent.ACTION_MOVE:



                ClipData.Item item = new ClipData.Item((CharSequence) notes.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

//                ClipData dragData = new ClipData(notes.getTag().toString(), mimeTypes, item);
                ClipData dragData = ClipData.newPlainText("", "");
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(notes);

                notes.startDrag(dragData, myShadow, notes, 0);
                return true;/*DrawerLayout.LayoutParams lParams = (DrawerLayout.LayoutParams) getLayoutParams();

                /*final int X1 = (int) event.getRawX();
                final int Y1 = (int) event.getRawY();
                int right=X1+rightDiff;
                layoutParams = (DrawerLayout.LayoutParams)
                        getLayoutParams();


                final Display defaultDisplay = ((TrainingActivity) context).getWindowManager().getDefaultDisplay();


                if((X - _xDelta) > 0 && right > 0 ) {
                    layoutParams.leftMargin = X - _xDelta;
                }
                if((Y - _yDelta) > 0 && (Y - _yDelta) < defaultDisplay.getHeight()) {
                    layoutParams.topMargin = Y - _yDelta;
                }
*/




           /*

                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                */
                /*int h=getRootView().getHeight();
                if((X - _xDelta) < defaultDisplay.getWidth() ) {
                    int w=getRootView().getWidth();
                    layoutParams.rightMargin = X - _xDelta;
                    layoutParams.width= w;
                }//layoutParams.rightMargin = -250;
//                int h1=layoutParams.topMargin;

                int width=getRootView().getMeasuredWidth();
                if(_yDelta> defaultDisplay.getHeight()) {
                    //int h=getRootView().getHeight();
                    h1=layoutParams.topMargin;
                    layoutParams.bottomMargin = Y - _yDelta;
                    //layoutParams.height=h;
                }
                layoutParams.topMargin=h1;
  //              layoutParams.height=h;
                //layoutParams.bottomMargin = -250;*/
           /* break;*/
        }


        notes.setOnDragListener(new View.OnDragListener() {
            public boolean onDrag(View v, DragEvent event) {
                LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();

                //layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        //    Log.d(msg, "Action is DragEvent.ACTION_DRAG_STARTED");

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        //  Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        //Log.d(msg, "Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        //Log.d(msg, "Action is DragEvent.ACTION_DRAG_LOCATION");


                    case DragEvent.ACTION_DRAG_ENDED:
                        //Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENDED");

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DROP:
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        v.setLayoutParams(layoutParams);

                        break;
//  Log.d(msg, "ACTION_DROP event");

                        // Do nothing

                    default:
                        break;
                }
                return true;
            }
        });

        SGD.onTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }


}
