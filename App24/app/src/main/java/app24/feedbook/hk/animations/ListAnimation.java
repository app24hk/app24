package app24.feedbook.hk.animations;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by amritpal on 9/11/15.
 */
public class ListAnimation extends Activity {
    static TranslateAnimation animation;

    /***
     * Method Name : settranslationofArrow()
     *
     * @param start
     * @param end
     * @param bottomToTop
     * @param topToBottom
     * @param duration
     * @param view
     * @return : None
     * <p/>
     * Description : Used to set sliding animation.
     */
    public static void settranslationofArrow(float start, float end,
                                             float bottomToTop, float topToBottom, int duration, View view) {

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, // fromXType
                start, // fromXValue
                Animation.RELATIVE_TO_SELF, // toXType
                end, // toXValue
                Animation.RELATIVE_TO_SELF, // fromYType
                bottomToTop, // fromYValue
                Animation.RELATIVE_TO_SELF, // toYType
                topToBottom); // toYValue
        animation.setDuration(duration);
        animation.setFillAfter(false);

        view.startAnimation(animation);
        // view.clearAnimation();

    }

}