package ru.rzn.gmyasoedov.collage.collagelogic;

import ru.rzn.gmyasoedov.collage.FileManager;
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
    protected List<InstagramImage> images;

    public SimpleCollage(InstagramImage image) {
        images.add(image);
    }

    public SimpleCollage(List<InstagramImage> images) {
        this.images = images;
        Collections.reverse(this.images);
    }

    public File createCollage() throws IOException {
        return FileManager.getInstance().getFile("0");
    }

}
