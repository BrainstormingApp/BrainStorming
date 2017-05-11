package animationStuff;

import android.support.design.widget.FloatingActionButton;
import android.view.animation.Animation;

/**
 * Created by pyronaid on 11/05/2017.
 */

public class FloatingActionAnimation {
    private FloatingActionButton floatingActionButton;
    private Animation animation_show;
    private Animation animation_hide;
    private double width;
    private double height;

    public FloatingActionAnimation(FloatingActionButton floatingActionButton, Animation animation_show, Animation animation_hide, double width, double height){
        this.floatingActionButton = floatingActionButton;
        this.animation_hide = animation_hide;
        this.animation_show = animation_show;
        this.width = width;
        this.height = height;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    public void setFloatingActionButton(FloatingActionButton floatingActionButton) {
        this.floatingActionButton = floatingActionButton;
    }

    public Animation getAnimation_show() {
        return animation_show;
    }

    public void setAnimation_show(Animation animation_show) {
        this.animation_show = animation_show;
    }

    public Animation getAnimation_hide() {
        return animation_hide;
    }

    public void setAnimation_hide(Animation animation_hide) {
        this.animation_hide = animation_hide;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
