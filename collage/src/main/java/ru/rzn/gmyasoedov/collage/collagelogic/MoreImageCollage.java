package ru.rzn.gmyasoedov.collage.collagelogic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.nostra13.universalimageloader.core.ImageLoader;
import ru.rzn.gmyasoedov.collage.InstagramImage;
import ru.rzn.gmyasoedov.collage.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * collage from more pictures > 3
 */
public class MoreImageCollage extends SimpleCollage {

    public MoreImageCollage(List<InstagramImage> images) {
        super(images);
    }

    public File createCollage() throws IOException {
        if (images.isEmpty()) {
            throw new IllegalArgumentException();
        }
        int column = 1;
        int row;
        while (column * column < images.size()) {
            column++;
        }
        row = (int) Math.ceil(images.size() * 1d / column);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        //create result bitmap
        Bitmap partBitmap = BitmapFactory.decodeFile(ImageLoader.getInstance().getDiskCache()
                .get(Utils.getImageTypeForCollage(images.size(), images.get(0))).getAbsolutePath(), options);
        Bitmap collage = Bitmap.createBitmap(partBitmap.getWidth() * column, partBitmap.getHeight() * row,
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(collage);

        //add image to result bitmap
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                int currentImageNumber = i * column + j;
                if (currentImageNumber < images.size()) {
                    Bitmap currentBitmap = BitmapFactory.decodeFile(ImageLoader.getInstance().getDiskCache()
                            .get(Utils.getImageTypeForCollage(images.size(), images.get(currentImageNumber)))
                            .getAbsolutePath(), options);
                    c.drawBitmap(currentBitmap, j * partBitmap.getWidth(), i * partBitmap.getHeight(), new Paint());
                }
            }
        }

        //save result bitmap to file
        ImageLoader.getInstance().getDiskCache().save(COLLAGE, collage);
        File cacheFile = ImageLoader.getInstance().getDiskCache().get(COLLAGE);
        return renameFile(cacheFile);
    }
}
