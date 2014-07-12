package ru.rzn.gmyasoedov.collage;

/**
 * image bean
 */
public class InstagramImage {
    String normalImage;
    String lowResolutionImage;
    String thumbnailImage;
    int likes;

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getNormalImage() {
        return normalImage;
    }

    public void setNormalImage(String normalImage) {
        this.normalImage = normalImage;
    }

    public String getLowResolutionImage() {
        return lowResolutionImage;
    }

    public void setLowResolutionImage(String lowResolutionImage) {
        this.lowResolutionImage = lowResolutionImage;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }
}
