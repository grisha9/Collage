package ru.rzn.gmyasoedov.collage.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.rzn.gmyasoedov.collage.*;
import ru.rzn.gmyasoedov.collage.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Images fragment
 */
public class FragmentImages extends Fragment {
    private static final String TAG = FragmentImages.class.getSimpleName();
    private List<InstagramImage> images;
    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        int userId = getArguments().getInt(Utils.USER_ID);
        Requestor.getMediaByUserId(userId, new ImagesJsonHttpResponseHandler());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(getArguments().getString(Utils.USER_LOGIN));
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        if (images != null) {
            gridView.setAdapter(new ImageAdapter(getActivity(), images));
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private class ImagesJsonHttpResponseHandler extends BaseJsonHttpResponseHandler<List<InstagramImage>> {
        @Override
        public void onStart() {
            super.onStart();
            try {
                ((CollageActivity) getActivity()).showDialog();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onFinish() {
            try {
                ((CollageActivity) getActivity()).hideDialog();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        @Override
        public void onSuccess(int i, Header[] headers, String s, List<InstagramImage> instagramImages) {
            try {
                images = instagramImages;
                Toast.makeText(getActivity(), getString(R.string.images) + images.size(), Toast.LENGTH_LONG).show();
                gridView.setAdapter(new ImageAdapter(getActivity(), images));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, List<InstagramImage> instagramImages) {
            try {
                Toast.makeText(getActivity(), getString(R.string.error_connection),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, throwable.toString());
            }
        }

        @Override
        protected List<InstagramImage> parseResponse(String s, boolean b) throws Throwable {
            List<InstagramImage> instagramImages = new ArrayList<InstagramImage>();
            JSONArray images = new JSONObject(s).getJSONArray(Utils.JSON_DATA);
            for (int i = 0; i < images.length(); i++) {
                InstagramImage image = new InstagramImage();
                JSONObject imageJSON  = images.getJSONObject(i).getJSONObject(Utils.JSON_IMAGES);
                image.setLowResolutionImage(imageJSON.getJSONObject(Utils.JSON_LOW_RESOLUTION)
                        .getString(Utils.JSON_URL));
                image.setNormalImage(imageJSON.getJSONObject(Utils.JSON_NORMAL)
                        .getString(Utils.JSON_URL));
                image.setThumbnailImage(imageJSON.getJSONObject(Utils.JSON_THUMBNAIL)
                        .getString(Utils.JSON_URL));
                image.setLikes(images.getJSONObject(i).getJSONObject(Utils.JSON_LIKES)
                        .getInt(Utils.JSON_COUNT));
                instagramImages.add(image);
            }
            Collections.sort(instagramImages, new Comparator<InstagramImage>() {
                @Override
                public int compare(InstagramImage instagramImage, InstagramImage instagramImage2) {
                    return instagramImage2.getLikes() - instagramImage.getLikes();
                }
            });
            return instagramImages;
        }
    }
}
