package ru.rzn.gmyasoedov.collage.collagelogic;

import ru.rzn.gmyasoedov.collage.InstagramImage;

import java.util.List;

/**
 * Collage factory
 */
public class CollageFactory {

    /**
     * get collage by images count in images list
     * @param images list of image
     * @return SimpleCollage instanse for create collage
     */
    public static SimpleCollage getCollage(List<InstagramImage> images) {
        switch (images.size()) {
            case 1: return new SimpleCollage(images);
            case 2: return new TwoImageCollage(images);
            case 3: return new ThreeImageCollage(images);
            default: return new MoreImageCollage(images);
        }
    }
}
