package ru.rzn.gmyasoedov.collage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import ru.rzn.gmyasoedov.collage.fragment.FragmentLogin;


public class CollageActivity extends Activity {
    private static final String TAG = CollageActivity.class.getSimpleName();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentLogin())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hideDialog() {
        dialog.dismiss();
    }

    public void showDialog() {
        dialog = ProgressDialog.show(this, null, getString(R.string.connecting));
        dialog.setCancelable(true);
    }
}
