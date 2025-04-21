package com.example.sickcare_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoriDao {

    private SQLiteDatabase db;
    private FavoriDatabaseHelper dbHelper;

    public FavoriDao(Context context) {
        dbHelper = new FavoriDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Ajouter une recette aux favoris
    public void addFavori(int recetteId) {
        ContentValues values = new ContentValues();
        values.put("id_recette", recetteId);
        db.insert("favoris", null, values);
    }

    // Supprimer une recette des favoris
    public void removeFavori(int recetteId) {
        db.delete("favoris", "id_recette = ?", new String[]{String.valueOf(recetteId)});
    }

    // Vérifier si une recette est un favori
    public boolean isFavori(int recetteId) {
        Cursor cursor = db.query("favoris", new String[]{"id_recette"},
                "id_recette = ?", new String[]{String.valueOf(recetteId)},
                null, null, null);

        boolean isFavori = cursor.getCount() > 0;
        cursor.close();
        return isFavori;
    }

    // Récupérer tous les ID des recettes mises en favoris
    public List<Integer> getAllFavoris() {
        List<Integer> favoris = new ArrayList<>();
        Cursor cursor = db.query("favoris", new String[]{"id_recette"},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idRecette = cursor.getInt(cursor.getColumnIndexOrThrow("id_recette"));
                favoris.add(idRecette);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoris;
    }
}
