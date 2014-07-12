package ru.rzn.gmyasoedov.collage;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Http client
 */
public class Requestor {
    private static final String USER_ID_TAG = "<user-id>";
    private static final String BASE_USER_ID_BY_LOGIN_URL = "http://jelled.com/ajax/instagram";
    private static final String BASE_INSTAGRAM_MEDIA_URL = "https://api.instagram.com/v1/users/" + USER_ID_TAG + "/media/recent/";
    private static final String PARAM_DO = "do";
    private static final String PARAM_DO_VALUE = "username";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_FORMAT_VALUE = "json";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_CLIENT_ID_VALUE = "74d20b8b9adc4dd48ebf0fdb2a85a0fd";
    private static final String PARAM_COUNT = "count";
    private static final String PARAM_COUNT_VALUE = "500";
    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * get instagram user id by instagram login
     * @param login instagram login
     * @param responseHandler handler for response
     */
    public static void getUserIdByLogin(String login, ResponseHandlerInterface responseHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(PARAM_DO, PARAM_DO_VALUE);
        requestParams.add(PARAM_USERNAME, login);
        requestParams.add(PARAM_FORMAT, PARAM_FORMAT_VALUE);
        client.get(BASE_USER_ID_BY_LOGIN_URL, requestParams, responseHandler);
    }

    /**
     * get user media by user id
     * @param id user id
     * @param responseHandler handler for response
     */
    public static void getMediaByUserId(int id, ResponseHandlerInterface responseHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(PARAM_CLIENT_ID, PARAM_CLIENT_ID_VALUE);
        requestParams.add(PARAM_COUNT, PARAM_COUNT_VALUE);
        client.get(BASE_INSTAGRAM_MEDIA_URL.replace(USER_ID_TAG, String.valueOf(id)), requestParams, responseHandler);
    }
}
