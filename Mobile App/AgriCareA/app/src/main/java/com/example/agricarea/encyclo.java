package com.example.agricarea;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class encyclo extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private ArrayList<String> diseaseList;
    private ArrayList<JSONObject> diseaseDetailsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclo);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);

        diseaseList = new ArrayList<>();
        diseaseDetailsList = new ArrayList<>();
        loadDiseaseData();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, diseaseList);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDisease = adapter.getItem(position);
                showDiseaseDetails(selectedDisease);
            }
        });
        Intent intent = getIntent();
        String query = intent.getStringExtra("dis");
        if (query != null && !query.isEmpty()) {
            searchView.setQuery(query, true);
        }

    }

    private void loadDiseaseData() {
        try {
            InputStream is = getAssets().open("diseases.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("diseases");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                diseaseList.add(obj.getString("name"));
                diseaseDetailsList.add(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDiseaseDetails(String diseaseName) {
        try {
            for (JSONObject obj : diseaseDetailsList) {
                if (obj.getString("name").equals(diseaseName)) {
                    Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.dialog_disease_details);
                    TextView tvName = dialog.findViewById(R.id.tvName);
                    ImageView tvPicture = dialog.findViewById(R.id.tvPicture);
                    TextView tvDefinition = dialog.findViewById(R.id.tvDefinition);
                    TextView tvHistory = dialog.findViewById(R.id.tvHistory);
                    TextView tvSymptoms = dialog.findViewById(R.id.tvSymptoms);
                    TextView tvCauses = dialog.findViewById(R.id.tvCauses);
                    TextView tvSolutions = dialog.findViewById(R.id.tvSolutions);
                    tvName.setText(obj.getString("name"));

                    int drawableResourceId = getResources().getIdentifier(obj.getString("link"), "drawable", getPackageName());
                    tvPicture.setImageResource(drawableResourceId);
                    tvDefinition.setText(obj.getString("definition"));
                    tvHistory.setText(obj.getString("history"));
                    tvSymptoms.setText(obj.getString("symptoms"));
                    tvCauses.setText(obj.getString("causes"));
                    tvSolutions.setText(obj.getString("solutions"));

                    dialog.show();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(imageUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                // Handle case where bitmap is null, show placeholder or error message
                imageView.setImageResource(R.drawable.leaf); // Placeholder image resource
            }
        }
    }


}
