package com.example.sickcare_application;

import java.util.List;

public class Recette {
    private int id_recette;
    private String nom_recette;
    private String description_recette;
    private String etape_recette;
    private List<String> aliments;
    private String image_recette;

    public Recette(int id_recette, String nom_recette, String description_recette, String etape_recette, List<String> aliments, String image_recette) {
        this.id_recette = id_recette;
        this.nom_recette = nom_recette;
        this.description_recette = description_recette;
        this.etape_recette = etape_recette;
        this.aliments = aliments;
        this.image_recette = image_recette;
    }

    public int getIdRecette() {
        return id_recette;
    }

    public String getNomRecette() {
        return nom_recette;
    }

    public String getDescriptionRecette() {
        return description_recette;
    }

    public String getEtapeRecette() {
        return etape_recette;
    }

    public List<String> getAliments() {
        return aliments;
    }

    public void setAliments(List<String> aliments) {
        this.aliments = aliments;
    }

    public String getImageRecette() {
        return image_recette;
    }

    public void setImageRecette(String image_recette) {
        this.image_recette = image_recette;
    }
}
