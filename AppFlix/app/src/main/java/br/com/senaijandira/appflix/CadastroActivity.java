package br.com.senaijandira.appflix;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class CadastroActivity extends AppCompatActivity {
    private final int COD_GALERIA = 1;
    private RatingBar rtAvaliacao;
    private EditText edtSinopse, edtLink, edtTitulo;
    private ImageView imgFilme;
    private LinearLayout linearCadastro;
    private ProgressBar progressBar;
    private Bitmap foto;
    private int filmeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rtAvaliacao = findViewById(R.id.rt_avaliacao);
        edtSinopse = findViewById(R.id.edt_sinopse);
        edtLink = findViewById(R.id.edt_link);
        edtTitulo = findViewById(R.id.edt_titulo);
        imgFilme = findViewById(R.id.img_filme);
        linearCadastro = findViewById(R.id.linear_cadastro);
        progressBar = findViewById(R.id.progress_bar_cadastro);

        filmeId = getIntent().getIntExtra("id", 0);
        if (filmeId != 0) {
            getSupportActionBar().setTitle("Editar filme");
            new ApiSelecionarUm().execute();
        }
    }

    public void salvarFilme(View view) {
        String sinopse = edtSinopse.getText().toString();
        String link = edtLink.getText().toString();
        String titulo = edtTitulo.getText().toString();
        if (sinopse.isEmpty()) {
            edtSinopse.setError("Preencha uma sinopse");
        } else if (link.isEmpty()) {
            edtLink.setError("Preencha um link");
        } else if (titulo.isEmpty()) {
            edtTitulo.setError("Preencha um título");
        } else {
            String imagem = foto == null ? "" : Base64.encodeToString(ImageHelper.bitmapToByteArray(foto), Base64.DEFAULT);
            if (filmeId == 0) {
                new ApiInserir().execute(sinopse, link, titulo, String.valueOf(rtAvaliacao.getRating()), imagem);
            } else {
                new ApiAtualizar().execute(sinopse, link, titulo, String.valueOf(rtAvaliacao.getRating()), imagem);
            }
        }
    }

    public void abrirGaleria(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, COD_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_GALERIA && resultCode == Activity.RESULT_OK) {
            try {
                InputStream stream = getContentResolver().openInputStream(data.getData());
                foto = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                if (foto.getHeight() > 512) {
                    int newHeight = (int) (foto.getHeight() * (512.0 / foto.getWidth()));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(foto, 512, newHeight, true);
                    imgFilme.setImageBitmap(scaledBitmap);
                } else {
                    imgFilme.setImageBitmap(foto);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class ApiSelecionarUm extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            linearCadastro.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            return HttpConnection.get("http://10.0.2.2/inf3t20181/TurmaB/apifilmes/selecionar.php?id=" + filmeId);
        }

        @Override
        protected void onPostExecute(String json) {
            progressBar.setVisibility(View.GONE);
            linearCadastro.setVisibility(View.VISIBLE);
            if (json != null) {
                try {
                    JSONObject object = new JSONObject(json);
                    edtSinopse.setText(object.getString("sinopse"));
                    edtLink.setText(object.getString("link"));
                    edtTitulo.setText(object.getString("titulo"));
                    rtAvaliacao.setRating((float) object.getDouble("avaliacao"));
                    Picasso.with(CadastroActivity.this).load("http://10.0.2.2/inf3t20181/TurmaB/apifilmes/imagens/" + object.getString("imagem")).into(imgFilme);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class ApiInserir extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> dados = new HashMap<>();
            dados.put("sinopse", strings[0]);
            dados.put("link", strings[1]);
            dados.put("titulo", strings[2]);
            dados.put("avaliacao", strings[3]);
            dados.put("imagem", strings[4]);
            return HttpConnection.post("http://10.0.2.2/inf3t20181/TurmaB/apifilmes/inserir.php", dados);
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getBoolean("sucesso")) {
                        Toast.makeText(getApplicationContext(), "Filme cadastrado com sucesso.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro ao cadastrar o filme. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class ApiAtualizar extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> dados = new HashMap<>();
            dados.put("id", String.valueOf(filmeId));
            dados.put("sinopse", strings[0]);
            dados.put("link", strings[1]);
            dados.put("titulo", strings[2]);
            dados.put("avaliacao", strings[3]);
            dados.put("imagem", strings[4]);
            return HttpConnection.post("http://10.0.2.2/inf3t20181/TurmaB/apifilmes/atualizar.php", dados);
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getBoolean("sucesso")) {
                        Toast.makeText(getApplicationContext(), "Filme atualizado com sucesso.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro ao atualizar o filme. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}