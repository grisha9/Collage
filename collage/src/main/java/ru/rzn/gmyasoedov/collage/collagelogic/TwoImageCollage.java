package ru.rzn.gmyasoedov.collage.collagelogic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import ru.rzn.gmyasoedov.collage.FileManager;
import ru.rzn.gmyasoedov.collage.InstagramImage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * collage from two pictures
 */
public class TwoImageCollage extends SimpleCollage{

    public TwoImageCollage(List<InstagramImage> images) {
        super(images);
    }

    public File createCollage() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap firstBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getFile("0")
            .getAbsolutePath(), options);
        Bitmap secondBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getFile("1")
                .getAbsolutePath(), options);
        Bitmap collage = Bitmap.createBitmap(firstBitmap.getWidth() + secondBitmap.getWidth(), firstBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(collage);
        c.drawBitmap(firstBitmap, 0, 0, new Paint());
        c.drawBitmap(secondBitmap, firstBitmap.getWidth(), 0, new Paint());
        firstBitmap.recycle();
        secondBitmap.recycle();
        return FileManager.getInstance().saveBitmapToFile(collage, COLLAGE);
    }
}
