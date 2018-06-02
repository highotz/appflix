package br.com.senaijandira.appflix;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FilmeAdapter extends ArrayAdapter<Filme> {
    public FilmeAdapter(Context context) {
        super(context, 0, new ArrayList<Filme>());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_filme, null);
        }

        Filme filme = getItem(position);
        ImageView imgFilme = view.findViewById(R.id.img_item);
        TextView txtTitulo = view.findViewById(R.id.txt_titulo_item);

        txtTitulo.setText(filme.getTitulo());
        Picasso.with(getContext()).load("http://10.0.2.2/inf3t20181/TurmaB/apifilmes/imagens/" + filme.getImagem()).into(imgFilme);

        return view;
    }
}