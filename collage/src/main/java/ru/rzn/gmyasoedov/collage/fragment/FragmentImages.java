package ru.rzn.gmyasoedov.collage.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.rzn.gmyasoedov.collage.*;
import ru.rzn.gmyasoedov.collage.adapter.ImageAdapter;
import ru.rzn.gmyasoedov.collage.listener.InstagramImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Images fragment
 */
public class FragmentImages extends Fragment {
    private static final String TAG = FragmentImages.class.getSimpleName();
    private static final String FILE_PREFIX = "file://";
    private static final int IMAGE_PREVIEW_HEIGHT = 200;
    private static final int IMAGE_PREVIEW_WIDTH = 200;
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.collage, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_collage:
                List<InstagramImage> list = new ArrayList<InstagramImage>();
                SparseBooleanArray positions = gridView.getCheckedItemPositions();
                for (int i = 0; i < positions.size(); i++) {
                    if (positions.valueAt(i)) {
                        list.add(images.get(positions.keyAt(i)));
                    }
                }
                if (list.isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.error_no_image), Toast.LENGTH_LONG).show();
                } else {
                    new InstagramImageLoader(list, new CollageCreatingListener()).startLoading();
                }
                return true;
            case R.id.action_reset:
                gridView.clearChoices();
                ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * class for creating collage image
     */
    private class CollageCreatingListener implements ImageLoadingListener {
        @Override
        public void onLoadingStarted(String s, View view) {
            try {
                ((CollageActivity) getActivity()).showDialog();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onLoadingFailed(String s, View view, FailReason failReason) {
            try {
                ((CollageActivity) getActivity()).hideDialog();
                Toast.makeText(getActivity(), failReason.getCause().getMessage(),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            Log.e(TAG, failReason.getCause().toString());
        }

        @Override
        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            try {
                ((CollageActivity) getActivity()).hideDialog();
                shareImageDialog(s);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onLoadingCancelled(String s, View view) {
            try {
                ((CollageActivity) getActivity()).hideDialog();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        private void shareImageDialog(final String filename) {
            ImageView imageView = new ImageView(getActivity());
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, IMAGE_PREVIEW_HEIGHT,
                    displayMetrics);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, IMAGE_PREVIEW_WIDTH,
                    displayMetrics);
            String url = FILE_PREFIX + filename;
            MemoryCacheUtils.removeFromCache(url, ImageLoader.getInstance().getMemoryCache());
            DiskCacheUtils.removeFromCache(url, ImageLoader.getInstance().getDiskCache());
            ImageLoader.getInstance().displayImage(url, imageView);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.share)
                    .setView(imageView)
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/*");
                            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filename)));
                            getActivity().startActivity(Intent.createChooser(share,
                                    getActivity().getResources().getString(R.string.share)));
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * class for handle response with image from instagram
     */
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
                JSONObject imageJSON = images.getJSONObject(i).getJSONObject(Utils.JSON_IMAGES);
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
