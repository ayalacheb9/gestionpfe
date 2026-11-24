package pfe;

/** Règles métier : titre obligatoire, 5 à 100 caractères, sans balises HTML. */
public final class RapportValidator {
    public static final int TITRE_MIN = 5;
    public static final int TITRE_MAX = 100;

    private RapportValidator() {}

    public static boolean titreValide(String titre) {
        return verifierTitre(titre) == null;
    }

    /** Retourne un message d'erreur, ou null si le titre est valide. */
    public static String verifierTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) return "Le titre est obligatoire.";
        int n = titre.trim().length();
        if (n < TITRE_MIN) return "Le titre doit contenir au moins " + TITRE_MIN + " caractères.";
        if (n > TITRE_MAX) return "Le titre ne doit pas dépasser " + TITRE_MAX + " caractères.";
        if (titre.contains("<") || titre.contains(">")) {
            return "Le titre ne doit pas contenir les caractères < ou >.";
        }
        return null;
    }
}