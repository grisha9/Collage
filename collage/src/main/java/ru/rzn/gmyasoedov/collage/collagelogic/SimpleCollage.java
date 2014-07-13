package ru.rzn.gmyasoedov.collage.collagelogic;

import com.nostra13.universalimageloader.core.ImageLoader;
import ru.rzn.gmyasoedov.collage.InstagramImage;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * collage from one picture
 */
public class SimpleCollage {
    protected static final String COLLAGE = "collage";
    protected static final String FILE_EXT = ".png";
    protected List<InstagramImage> images;

    public SimpleCollage(InstagramImage image) {
        images.add(image);
    }

    public SimpleCollage(List<InstagramImage> images) {
        this.images = images;
        Collections.reverse(this.images);
    }

    public File createCollage() throws IOException {
        return renameFile(ImageLoader.getInstance().getDiskCache().get(images.get(0).getNormalImage()));
    }

    protected File renameFile(File file) {
        File cache = ImageLoader.getInstance().getDiskCache().getDirectory();
        File from = new File(cache, file.getName());
        File to = new File(cache, COLLAGE + FILE_EXT);
        from.renameTo(to);
        from.delete();
        return to;
    }
}
