package br.com.senaijandira.appflix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ProgressBar progressBar;
    private FilmeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress_bar);
        listView = findViewById(R.id.lv_filmes);
        adapter = new FilmeAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Filme filme = adapter.getItem(i);
                Intent intent = new Intent(MainActivity.this, VisualizarActivity.class);
                intent.putExtra("id", filme.getId());
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CadastroActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        new ApiSelecionar().execute();
    }

    private class ApiSelecionar extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return HttpConnection.get("http://10.0.2.2/inf3t20181/TurmaB/apifilmes/selecionar.php");
        }

        @Override
        protected void onPostExecute(String json) {
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            if (json != null) {
                try {
                    JSONArray array = new JSONArray(json);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Filme filme = new Filme();
                        filme.setId(object.getInt("id"));
                        filme.setSinopse(object.getString("sinopse"));
                        filme.setLink(object.getString("link"));
                        filme.setTitulo(object.getString("titulo"));
                        filme.setAvaliacao(object.getDouble("avaliacao"));
                        filme.setImagem(object.getString("imagem"));
                        adapter.add(filme);
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}