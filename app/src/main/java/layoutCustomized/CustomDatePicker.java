package layoutCustomized;

/**
 * Created by pyronaid on 01/04/2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;

/**
 * Custom implementation of {@link DatePicker} to fix issue with {@link android.widget.ScrollView}.
 * Disallows touch event for parent when touched on picker.
 *
 * Creator: <lukasz.izmajlowicz@mydriver.com>
 * Date: 9/19/13
 *
 */
public class CustomDatePicker extends DatePicker {

    public CustomDatePicker(Context context) {
        super(context);
    }

    public CustomDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ViewParent parentView = getParent();

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (parentView != null) {
                parentView.requestDisallowInterceptTouchEvent(true);
            }
        }

        return false;
    }
}