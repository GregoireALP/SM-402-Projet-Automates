import java.util.Objects;

public class Transition {

    private Etat provenance;
    private Etat destination;
    private String libelle;

    public Transition(Etat provenance, Etat destination, String libelle) {

        this.provenance = provenance;
        this.destination = destination;
        this.libelle = libelle;
    }

    @Override
    public boolean equals(Object obj) {

        boolean isEqual = false;

        if(obj == null || obj.getClass() != this.getClass()) {
            return isEqual;
        } else {

            final Transition transition = (Transition) obj;

            if(Objects.equals(transition.getLibelle(), this.libelle) && Objects.equals(transition.getDestination().getName(), this.destination.getName()) && Objects.equals(transition.getProvenance().getName(), this.provenance.getName())) {
                isEqual = true;
            }

            return isEqual;
        }

    }

    public Etat getDestination() {
        return destination;
    }

    public Etat getProvenance() {
        return provenance;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setDestination(Etat destination) {
        this.destination = destination;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setProvenance(Etat provenance) {
        this.provenance = provenance;
    }
}
