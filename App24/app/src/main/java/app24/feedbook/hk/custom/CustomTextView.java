package app24.feedbook.hk.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by amritpal on 4/11/15.
 */
public class CustomTextView extends TextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        //if (!isInEditMode()) {
//        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/STHeiti-Light.ttc");
//        setTypeface(typeFace, Typeface.NORMAL);
        //}
    }
}
