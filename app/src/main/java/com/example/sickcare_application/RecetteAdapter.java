package com.example.sickcare_application;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecetteAdapter extends RecyclerView.Adapter<RecetteAdapter.RecetteViewHolder> {

    private Context context;
    private List<Recette> recettes;
    private FavoriDao favoriDao;

    public RecetteAdapter(Context context, List<Recette> recettes) {
        this.context = context;
        this.recettes = recettes;
        this.favoriDao = new FavoriDao(context);
    }

    @Override
    public RecetteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recette, parent, false);
        return new RecetteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecetteViewHolder holder, int position) {
        Recette recette = recettes.get(position);
        holder.nom.setText(recette.getNomRecette());

        // Gestion du bouton Favori
        if (favoriDao.isFavori(recette.getIdRecette())) {
            holder.btnFavori.setImageResource(R.drawable.baseline_star_24);
        } else {
            holder.btnFavori.setImageResource(R.drawable.baseline_star_border_24);
        }

        // Ajouter ou retirer des favoris
        holder.btnFavori.setOnClickListener(v -> {
            if (favoriDao.isFavori(recette.getIdRecette())) {
                favoriDao.removeFavori(recette.getIdRecette());
                holder.btnFavori.setImageResource(R.drawable.baseline_star_border_24);
            } else {
                favoriDao.addFavori(recette.getIdRecette());
                holder.btnFavori.setImageResource(R.drawable.baseline_star_24);
            }

            // Rafraîchir la liste des favoris
            if (context instanceof RechercheActivity) {
                ((RechercheActivity) context).refreshFavoris();
            }
        });

        // Gestion du clic sur le bouton "Voir"
        holder.btnVoir.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecetteActivity.class);
            intent.putExtra("nom", recette.getNomRecette());
            intent.putExtra("description", recette.getDescriptionRecette());
            intent.putExtra("etapes", recette.getEtapeRecette());
            intent.putExtra("image", recette.getImageRecette());
            intent.putStringArrayListExtra("aliments", new ArrayList<>(recette.getAliments()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recettes.size();
    }

    public static class RecetteViewHolder extends RecyclerView.ViewHolder {
        TextView nom;
        ImageButton btnFavori;
        Button btnVoir; // Ajout de la référence au bouton "Voir"

        public RecetteViewHolder(View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.recette_nom);
            btnFavori = itemView.findViewById(R.id.btnFavori);
            btnVoir = itemView.findViewById(R.id.btnVoir); // Initialisation du bouton "Voir"
        }
    }

    public void setRecettes(List<Recette> recettes) {
        this.recettes = recettes;
        notifyDataSetChanged();
    }
}
