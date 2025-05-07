package com.example.sickcare_application;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Activité pour la recherche de recettes
public class RechercheActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // Liste pour afficher les recettes
    private ToggleButton toggleButtonFilter; // Bouton pour filtrer selon les aliments interdits
    private ToggleButton toggleButtonFavoris; // Bouton pour filtrer les favoris
    private RecetteAdapter adapter; // Adaptateur pour la RecyclerView
    private List<Recette> allRecettes = new ArrayList<>(); // Liste de toutes les recettes récupérées
    private List<String> alimentsAssocies = new ArrayList<>(); // Liste des aliments interdits selon les maladies de l'utilisateur
    private List<Integer> favoris = new ArrayList<>(); // Liste des ID de recettes en favori
    private String token = ""; // Token d'authentification pour l'API
    private SearchView searchView; // Barre de recherche

    // Méthode appelée à la création de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);

        // Bouton pour se déconnecter et revenir à l'écran précédent
        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerView);
        toggleButtonFilter = findViewById(R.id.toggleButtonFilter);
        toggleButtonFavoris = findViewById(R.id.toggleButtonFavoris);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Organisation verticale
        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("auth_token", ""); // Récupération du token

        adapter = new RecetteAdapter(this, new ArrayList<>()); // Initialisation de l'adaptateur vide
        recyclerView.setAdapter(adapter);

        toggleButtonFilter.setChecked(false); // Désactive le filtre par défaut
        toggleButtonFavoris.setChecked(false); // Désactive les favoris par défaut

        // Applique les filtres lors du clic sur les boutons
        toggleButtonFilter.setOnClickListener(v -> applyFilter());
        toggleButtonFavoris.setOnClickListener(v -> applyFilter());

        // Applique les filtres lorsqu'on tape dans la barre de recherche
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilter();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilter();
                return false;
            }
        });

        fetchRecettes(); // Lance la récupération des recettes à l'ouverture
    }

    // Méthode pour récupérer les recettes et aliments associés via l'API
    private void fetchRecettes() {
        String url = "http://10.0.2.2/~fevzican.karamercan/SickCare/public/api/details"; // URL de l'API locale
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject userData = json.getJSONObject("data").getJSONObject("user_data");

                        alimentsAssocies.clear(); // Vide les anciens aliments associés
                        JSONArray maladies = userData.getJSONArray("maladies");
                        for (int i = 0; i < maladies.length(); i++) {
                            JSONObject maladie = maladies.getJSONObject(i);
                            if (maladie.has("aliments")) {
                                JSONArray aliments = maladie.getJSONArray("aliments");
                                for (int j = 0; j < aliments.length(); j++) {
                                    String alimentNom = aliments.getJSONObject(j).getString("nom_aliment");
                                    alimentsAssocies.add(alimentNom); // Ajoute les aliments interdits
                                }
                            }
                        }

                        JSONArray recettesArray = json.getJSONObject("data").getJSONArray("recettes");
                        allRecettes.clear(); // Vide les anciennes recettes
                        favoris = new FavoriDao(this).getAllFavoris(); // Charge les favoris depuis la base locale

                        // Boucle pour traiter chaque recette reçue
                        for (int i = 0; i < recettesArray.length(); i++) {
                            JSONObject obj = recettesArray.getJSONObject(i);
                            List<String> alimentsRecette = new ArrayList<>();

                            if (obj.has("aliments")) {
                                JSONArray alimentsArray = obj.getJSONArray("aliments");

                                for (int j = 0; j < alimentsArray.length(); j++) {
                                    String alimentNom = alimentsArray.getJSONObject(j).getString("nom_aliment");
                                    alimentsRecette.add(alimentNom);
                                }

                            }

                            Recette recette = new Recette(
                                    obj.getInt("id_recette"),
                                    obj.getString("nom_recette"),
                                    obj.optString("description_recette"),
                                    obj.optString("etape_recette"),
                                    alimentsRecette,
                                    obj.optString("image_recette", "")
                            );

                            allRecettes.add(recette); // Ajoute la recette à la liste
                        }

                        applyFilter(); // Applique le filtre après chargement
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", e.getMessage());
                        Toast.makeText(this, "Erreur JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                }) {
            // Ajoute le header Authorization avec le token
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(request); // Ajoute la requête à la file d'attente Volley
    }

    // Méthode pour appliquer les filtres sur les recettes
    private void applyFilter() {
        String searchQuery = searchView.getQuery().toString().toLowerCase(); // Texte de recherche
        List<Recette> filtered = new ArrayList<>(); // Liste des recettes filtrées

        // Parcourt toutes les recettes
        for (Recette r : allRecettes) {
            boolean interdit = false;
            for (String aliment : r.getAliments()) {
                if (alimentsAssocies.contains(aliment)) {
                    interdit = true; // Recette interdite si elle contient un aliment interdit
                    break;
                }
            }

            // Si le filtre est activé et qu'un aliment interdit est trouvé, on saute cette recette
            if (interdit && toggleButtonFilter.isChecked()) continue;

            boolean isFavori = favoris.contains(r.getIdRecette());
            if (toggleButtonFavoris.isChecked() && !isFavori) continue; // Filtrer les non-favoris si demandé

            // Vérifie si la recherche correspond au nom de la recette ou à ses aliments
            boolean matchFound = r.getNomRecette().toLowerCase().contains(searchQuery);
            for (String aliment : r.getAliments()) {
                if (aliment.toLowerCase().contains(searchQuery)) {
                    matchFound = true;
                    break;
                }
            }

            if (matchFound) {
                filtered.add(r); // Ajoute la recette au résultat filtré
            }
        }

        adapter.setRecettes(filtered); // Met à jour l'affichage avec les recettes filtrées

        // Message d'information sur l'état des filtres
        String message = toggleButtonFilter.isChecked() ? "Filtre activé" : "Filtre désactivé";
        if (toggleButtonFavoris.isChecked()) {
            message += " et favoris activé";
        }
        Toast.makeText(RechercheActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    // Méthode pour rafraîchir la liste des favoris
    public void refreshFavoris() {
        favoris = new FavoriDao(this).getAllFavoris();
        applyFilter();
    }
}
