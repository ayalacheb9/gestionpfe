package pfe;

public class RapportDTO {
    private String identifiant;
    private String titre;
    private String statut;
    private double note;
    private boolean valide;

    public RapportDTO() {}

    public RapportDTO(Rapport r) {
        this.identifiant = r.getIdentifiant();
        this.titre = r.getTitre();
        this.statut = r.getStatut();
        this.note = r.getNote();
        this.valide = r.estValide();
    }

    public String getIdentifiant() { return identifiant; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public double getNote() { return note; }
    public void setNote(double note) { this.note = note; }
    public boolean isValide() { return valide; }
    public void setValide(boolean valide) { this.valide = valide; }
}