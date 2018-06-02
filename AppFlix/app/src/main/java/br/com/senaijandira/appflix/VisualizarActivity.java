package br.com.senaijandira.appflix;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class VisualizarActivity extends AppCompatActivity {
    private Filme filme;
    private ImageView imgFilme;
    private TextView txtSinopse;
    private RatingBar rtAvaliacao;
    private LinearLayout linearVisualizar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgFilme = findViewById(R.id.img_visualizar_filme);
        txtSinopse = findViewById(R.id.txt_visualizar_sinopse);
        rtAvaliacao = findViewById(R.id.rt_visulizar_avaliacao);
        linearVisualizar = findViewById(R.id.linear_visualizar);
        progressBar = findViewById(R.id.progress_bar_visualizar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ApiSelecionarUm().execute(String.valueOf(getIntent().getIntExtra("id", 0)));
    }

    public void assistirProducao(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(filme.getLink())));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Não foi possível abrir o link: " + filme.getLink(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visualizar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_editar) {
            Intent intent = new Intent(this, CadastroActivity.class);
            intent.putExtra("id", filme.getId());
            startActivity(intent);

        } else if (item.getItemId() == R.id.item_excluir) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Excluir");
            builder.setMessage("Deseja realmente excluir este filme ?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new ApiExcluir().execute(String.valueOf(filme.getId()));
                }
            });

            builder.setNegativeButton("Não", null);
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void atualizarCampos() {
        getSupportActionBar().setTitle(filme.getTitulo());
        txtSinopse.setText(filme.getSinopse());
        rtAvaliacao.setRating((float) filme.getAvaliacao());
        Picasso.with(VisualizarActivity.this).load("http://10.0.2.2/inf3t20181/TurmaB/apifilmes/imagens/" + filme.getImagem()).into(imgFilme);
    }

    private class ApiSelecionarUm extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            linearVisualizar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            return HttpConnection.get("http://10.0.2.2/inf3t20181/TurmaB/apifilmes/selecionar.php?id=" + strings[0]);
        }

        @Override
        protected void onPostExecute(String json) {
            progressBar.setVisibility(View.GONE);
            linearVisualizar.setVisibility(View.VISIBLE);
            if (json != null) {
                try {
                    JSONObject object = new JSONObject(json);
                    filme = new Filme();
                    filme.setId(object.getInt("id"));
                    filme.setSinopse(object.getString("sinopse"));
                    filme.setLink(object.getString("link"));
                    filme.setTitulo(object.getString("titulo"));
                    filme.setAvaliacao(object.getDouble("avaliacao"));
                    filme.setImagem(object.getString("imagem"));
                    atualizarCampos();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class ApiExcluir extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return HttpConnection.get("http://10.0.2.2/inf3t20181/TurmaB/apifilmes/deletar.php?id=" + strings[0]);
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getBoolean("sucesso")) {
                        Toast.makeText(getApplicationContext(), "Filme excluido com sucesso.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro ao excluir o filme. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}