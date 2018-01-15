package tiquant.com.newsapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import adapters.MyListAdapter;
import constants.ServerConstants;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private ListView lvNews;
    private ProgressBar pb;
    private TextView tvError;


    private static final String TAG = ListActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        lvNews = findViewById(R.id.lv_news);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);

        tvError = findViewById(R.id.tv_error);

        if (isConnected(getApplicationContext())) {
            getLoaderManager().initLoader(0, null, this).forceLoad();
        } else {
            pb.setVisibility(View.GONE);
            showToast(getApplicationContext(), "Please connect to the internet");

        }
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        return new FetchData(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
        pb.setVisibility(View.GONE);
        try {
            if (data != null) {
                Log.i(TAG, data.toString());
                lvNews.setAdapter(new MyListAdapter(getApplicationContext(), data.getJSONObject("response")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent preferenceIntent = new Intent(getApplicationContext(), PreferenceActivity.class);
            startActivity(preferenceIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @description checks if Internet is ON or not
     * @param context
     * @return boolean (determining the state of the internet)
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
                if (info.getState() == NetworkInfo.State.CONNECTED || info.getState() == NetworkInfo.State.CONNECTING) {
                    return true;
                }
        }
        return false;

    }


    /**
     * @description generic method for showing Toast messages
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    private static class FetchData extends AsyncTaskLoader<JSONObject> {
        public FetchData(Context context) {
            super(context);
        }

        @Override
        public JSONObject loadInBackground() {
            JSONObject jsonObject = null;

            HttpURLConnection httpURLConnection = null;
            URL url;
            BufferedReader bufferedReader = null;
            int responseCode;

            try {
                url = new URL(ServerConstants.SERVER_URL + "search?show-tags=contributor");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("api-key", ServerConstants.API_KEY);

                InputStream inputStream = httpURLConnection.getInputStream();

                if (inputStream == null) return null;

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                responseCode = httpURLConnection.getResponseCode();

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                jsonObject = new JSONObject(sb.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                httpURLConnection.disconnect();

                try {
                    if (bufferedReader != null)
                        bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return jsonObject;
        }
    }
}
