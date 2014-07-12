package ru.rzn.gmyasoedov.collage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import ru.rzn.gmyasoedov.collage.InstagramImage;
import ru.rzn.gmyasoedov.collage.R;

import java.util.List;

/**
 * adapter for forecast
 */
public class ImageAdapter extends ArrayAdapter<InstagramImage> {
    private List<InstagramImage> images;
    private LayoutInflater inflater;

    public ImageAdapter(Context context, List<InstagramImage> images) {
        super(context, -1, images);
        this.images = images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.image_item, null);
            holder = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ImageView) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(images.get(position).getThumbnailImage(), holder);
        return convertView;
    }
}
