package layoutCustomized;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by pyronaid on 05/03/2017.
 */
public class DynamicImageView extends ImageView {

    public DynamicImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();

        if (d != null) {
            //int height = MeasureSpec.getSize(heightMeasureSpec);
            int height;
            int width = MeasureSpec.getSize(widthMeasureSpec);

            height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
            //Log.i("misure",height+"");
            //Log.i("misure",width+"");
            //Log.i("misure",d.getIntrinsicHeight()+"");
            //Log.i("misure",d.getIntrinsicWidth()+"");

            //if(width >= height)
            //    height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
            //else
            //    width = (int) Math.ceil(height * (float) d.getIntrinsicWidth() / d.getIntrinsicHeight());

            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}