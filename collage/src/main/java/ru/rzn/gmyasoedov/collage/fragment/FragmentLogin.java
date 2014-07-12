package ru.rzn.gmyasoedov.collage.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.rzn.gmyasoedov.collage.CollageActivity;
import ru.rzn.gmyasoedov.collage.R;
import ru.rzn.gmyasoedov.collage.Requestor;
import ru.rzn.gmyasoedov.collage.Utils;

/**
 * Fragment for loginEditText
 */
public class FragmentLogin extends Fragment {
    private static final String TAG = FragmentLogin.class.getSimpleName();
    private EditText loginEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_name, container, false);
        loginEditText = (EditText) rootView.findViewById(R.id.login);
        getActivity().getActionBar().setTitle(getString(R.string.app_name));
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.collage, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_collage:
                //hide key board
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                String login = loginEditText.getText().toString();
                if (!TextUtils.isEmpty(login)) {
                    Requestor.getUserIdByLogin(login.toString(), new LoginJsonHttpResponseHandler(login));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_empty_login), Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoginJsonHttpResponseHandler extends BaseJsonHttpResponseHandler<Integer> {
        private String login;

        private LoginJsonHttpResponseHandler(String login) {
            this.login = login;
        }

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
        public void onSuccess(int i, Header[] headers, String s, Integer id) {
            try {
                if (id != null) {
                    Fragment fragment = new FragmentImages();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Utils.USER_ID ,id);
                    bundle.putString(Utils.USER_LOGIN, login);
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.container, fragment)
                            .addToBackStack(null).commit();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_invalid_login),
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Integer id) {
            try {
                Toast.makeText(getActivity(), getString(R.string.error_connection),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, throwable.toString());
            }
        }

        @Override
        protected Integer parseResponse(String s, boolean b) throws Throwable {
            Integer result = null;
            JSONArray users = new JSONObject(s).getJSONArray(Utils.JSON_DATA);
            for (int i = 0; i < users.length(); i++) {
                if (login.equals(users.getJSONObject(i).getString(Utils.JSON_USERNAME))) {
                    result = users.getJSONObject(i).getInt(Utils.JSON_ID);
                    break;
                }
            }
            return result;
        }
    }
}
