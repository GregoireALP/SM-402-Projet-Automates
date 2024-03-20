public class Transition {

    private Etat provenance;
    private Etat destination;
    private String libelle;

    public Transition(Etat provenance, Etat destination, String libelle) {

        this.provenance = provenance;
        this.destination = destination;
        this.libelle = libelle;
    }

    public void afficherTransition() {
        System.out.println(this.provenance.getName() + "--" + this.libelle + "->" + this.destination.getName());
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
