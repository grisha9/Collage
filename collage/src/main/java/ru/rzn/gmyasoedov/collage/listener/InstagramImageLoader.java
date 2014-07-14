package ru.rzn.gmyasoedov.collage.listener;

import android.graphics.Bitmap;
import android.view.View;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import ru.rzn.gmyasoedov.collage.InstagramImage;
import ru.rzn.gmyasoedov.collage.Utils;
import ru.rzn.gmyasoedov.collage.collagelogic.CollageFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by g on 13.07.14.
 */
public class InstagramImageLoader implements ImageLoadingListener {
    private static final String TAG = InstagramImageLoader.class.getSimpleName();
    private List<InstagramImage> images;
    private int imageCount;
    private ImageLoadingListener listener;

    public InstagramImageLoader(List<InstagramImage> images, ImageLoadingListener listener) {
        this.images = images;
        this.listener = listener;
    }

    public void startLoading() {
        imageCount = images.size();
        listener.onLoadingStarted(null, null);
        onLoadingComplete(null, null, null);
    }

    @Override
    public void onLoadingStarted(String s, View view) {
    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {
        listener.onLoadingFailed(s, view, failReason);
    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        if (bitmap != null) {
            try {
                ImageLoader.getInstance().getDiskCache().save(s, bitmap);
            } catch (IOException e) {
                onLoadingFailed(s, view, new FailReason(FailReason.FailType.IO_ERROR, e));
                return;
            }
        }
        imageCount--;
        if (imageCount >= 0) {
            ImageLoader.getInstance().loadImage(
                    Utils.getImageTypeForCollage(images.size(), images.get(imageCount)), this);
        } else {
            try {
                File file= CollageFactory.getCollage(images).createCollage();
                listener.onLoadingComplete(file.getAbsolutePath(), null, null);
            } catch (IOException e) {
                onLoadingFailed(s, view, new FailReason(FailReason.FailType.IO_ERROR, e));
            }
        }
    }

    @Override
    public void onLoadingCancelled(String s, View view) {
        listener.onLoadingCancelled(s, view);
    }

}
