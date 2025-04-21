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

public class RechercheActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ToggleButton toggleButtonFilter;
    private ToggleButton toggleButtonFavoris;
    private RecetteAdapter adapter;
    private List<Recette> allRecettes = new ArrayList<>();
    private List<String> alimentsAssocies = new ArrayList<>();
    private List<Integer> favoris = new ArrayList<>();
    private String token = "";
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);

        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerView);
        toggleButtonFilter = findViewById(R.id.toggleButtonFilter);
        toggleButtonFavoris = findViewById(R.id.toggleButtonFavoris);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("auth_token", "");

        adapter = new RecetteAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        toggleButtonFilter.setChecked(false);
        toggleButtonFavoris.setChecked(false);

        toggleButtonFilter.setOnClickListener(v -> applyFilter());
        toggleButtonFavoris.setOnClickListener(v -> applyFilter());

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

        fetchRecettes();
    }

    private void fetchRecettes() {
        String url = "http://10.0.2.2/SickCare/public/api/details";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject userData = json.getJSONObject("data").getJSONObject("user_data");

                        alimentsAssocies.clear();
                        JSONArray maladies = userData.getJSONArray("maladies");
                        for (int i = 0; i < maladies.length(); i++) {
                            JSONObject maladie = maladies.getJSONObject(i);
                            if (maladie.has("aliments")) {
                                JSONArray aliments = maladie.getJSONArray("aliments");
                                for (int j = 0; j < aliments.length(); j++) {
                                    String alimentNom = aliments.getJSONObject(j).getString("nom_aliment");
                                    alimentsAssocies.add(alimentNom);
                                }
                            }
                        }

                        JSONArray recettesArray = json.getJSONObject("data").getJSONArray("recettes");
                        allRecettes.clear();
                        favoris = new FavoriDao(this).getAllFavoris();

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
                                    alimentsRecette
                            );
                            allRecettes.add(recette);
                        }

                        applyFilter();
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", e.getMessage());
                        Toast.makeText(this, "Erreur JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void applyFilter() {
        String searchQuery = searchView.getQuery().toString().toLowerCase();
        List<Recette> filtered = new ArrayList<>();

        for (Recette r : allRecettes) {
            boolean interdit = false;
            for (String aliment : r.getAliments()) {
                if (alimentsAssocies.contains(aliment)) {
                    interdit = true;
                    break;
                }
            }

            if (interdit && toggleButtonFilter.isChecked()) continue;

            boolean isFavori = favoris.contains(r.getIdRecette());
            if (toggleButtonFavoris.isChecked() && !isFavori) continue;

            boolean matchFound = r.getNomRecette().toLowerCase().contains(searchQuery);
            for (String aliment : r.getAliments()) {
                if (aliment.toLowerCase().contains(searchQuery)) {
                    matchFound = true;
                    break;
                }
            }

            if (matchFound) {
                filtered.add(r);
            }
        }

        adapter.setRecettes(filtered);

        String message = toggleButtonFilter.isChecked() ? "Filtre activé" : "Filtre désactivé";
        if (toggleButtonFavoris.isChecked()) {
            message += " et favoris activé";
        }
        Toast.makeText(RechercheActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    public void refreshFavoris() {
        favoris = new FavoriDao(this).getAllFavoris();
        applyFilter();
    }
}
