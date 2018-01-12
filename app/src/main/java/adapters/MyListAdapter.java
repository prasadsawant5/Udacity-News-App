package adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import constants.ServerConstants;
import tiquant.com.newsapp.R;

/**
 * Created by prasadsawant on 1/11/18.
 */

public class MyListAdapter extends BaseAdapter {

    private Context context;
    private JSONArray jsonArray;
    private JSONObject jsonObject;

    private TextView tvHeadline, tvEditor, tvDate, tvSection;

    private static final String TAG = MyListAdapter.class.getName();

    public MyListAdapter(Context context, JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.context = context;

        try {
            this.jsonArray = jsonObject.getJSONArray(ServerConstants.JSON_RESULTS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater;
        View rootView = null;

        try {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rootView = layoutInflater.inflate(R.layout.layout_row, parent, false);

            tvHeadline = rootView.findViewById(R.id.tv_headline);
            tvEditor = rootView.findViewById(R.id.tv_editor);
            tvDate = rootView.findViewById(R.id.tv_date);
            tvSection = rootView.findViewById(R.id.tv_section);

            String headline;
            String editor;

            final JSONObject jsonObject = jsonArray.getJSONObject(position);

            if (jsonObject.has(ServerConstants.JSON_WEB_TITLE)) {
                headline = jsonObject.getString(ServerConstants.JSON_WEB_TITLE);

                if (headline.contains("|")) {
                    editor = headline.substring(headline.indexOf("|") + 2, headline.length());
                    headline = headline.substring(0, headline.indexOf("|"));
                    tvEditor.setText(editor);
                }

                tvHeadline.setText(headline);
            }

            if (jsonObject.has(ServerConstants.JSON_WEB_DATE)) {
                tvDate.setText(jsonObject.getString(ServerConstants.JSON_WEB_DATE));
            }

            if (jsonObject.has(ServerConstants.JSON_SECTION_NAME)) {
                tvSection.setText(jsonObject.getString(ServerConstants.JSON_SECTION_NAME));
            }

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String uri = jsonObject.getString(ServerConstants.JSON_WEB_URL);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(uri));

                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rootView;
    }

}
