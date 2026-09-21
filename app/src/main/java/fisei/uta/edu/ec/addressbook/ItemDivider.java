// ItemDivider.java
// RecyclerView.ItemDecoration subclass for drawing dividers between
// RecyclerView items
package fisei.uta.edu.ec.addressbook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemDivider extends RecyclerView.ItemDecoration {
    private final Drawable divider;

    // gets the device's default list item divider from the current theme
    public ItemDivider(Context context) {
        int[] attrs = {android.R.attr.listDivider};
        divider = context.obtainStyledAttributes(attrs).getDrawable(0);
    }

    // draws the divider lines between the RecyclerView's items
    @Override
    public void onDraw(Canvas canvas, RecyclerView parent,
        RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View item = parent.getChildAt(i);
            RecyclerView.LayoutParams params =
                (RecyclerView.LayoutParams) item.getLayoutParams();
            int top = item.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }
}
