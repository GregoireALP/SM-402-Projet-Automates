import java.util.Set;
import java.util.stream.Collectors;

public class Etat {

    private String name;

    public Etat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFinal() {
        return Automate.etatsTerminaux.contains(this);
    }

    private Set<Etat> sousEtats;  // Les sous-états pour les états combinés dans la déterminisation

    public String toString() {
        if (this.sousEtats != null) {
            return this.sousEtats.stream().map(Etat::getName).collect(Collectors.joining("."));
        }
        return this.name; // name devrait être un attribut de la classe
    }
}
