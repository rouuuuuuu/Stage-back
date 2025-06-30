package com.example.StageDIP.dto;

public class MatchWeightsDTO {
    private double poidsPrix = 0.4;    // default 40% weight on price
    private double poidsDelai = 0.3;   // default 30% weight on delivery delay
    private double poidsNotation = 0.3; // default 30% weight on rating

    public MatchWeightsDTO() {}

    public MatchWeightsDTO(double poidsPrix, double poidsDelai, double poidsNotation) {
        this.poidsPrix = poidsPrix;
        this.poidsDelai = poidsDelai;
        this.poidsNotation = poidsNotation;
    }

    public double getPoidsPrix() { return poidsPrix; }
    public void setPoidsPrix(double poidsPrix) { this.poidsPrix = poidsPrix; }

    public double getPoidsDelai() { return poidsDelai; }
    public void setPoidsDelai(double poidsDelai) { this.poidsDelai = poidsDelai; }

    public double getPoidsNotation() { return poidsNotation; }
    public void setPoidsNotation(double poidsNotation) { this.poidsNotation = poidsNotation; }
}
