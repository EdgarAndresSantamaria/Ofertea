package com.example.proyecto_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.mindorks.placeholderview.Animation;
import com.mindorks.placeholderview.annotations.Animate;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Animate(Animation.CARD_LEFT_IN_DESC)
@NonReusable
@Layout(R.layout.gallery_item)
public class GalleryItem {

    @View(R.id.imageView)
    private ImageView imageView;

    private Bitmap mDrawable;

    public GalleryItem(Context ctx, Bitmap drawable) {
        mDrawable = drawable;
        imageView = new ImageView(ctx);
        imageView.setImageBitmap(drawable);
    }

    @Resolve
    private void onResolved() {
        imageView.setImageBitmap(mDrawable);
    }
}