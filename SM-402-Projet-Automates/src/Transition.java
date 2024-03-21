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


    @Override
    public boolean equals(Object obj) {

        boolean isEqual = false;

        if(obj == null || obj.getClass() != this.getClass()) {
            return isEqual;
        } else {

            final Transition transition = (Transition) obj;

            if(transition.getLibelle() == this.libelle && transition.getDestination().getName() == this.destination.getName() && transition.getProvenance().getName() == this.provenance.getName()) {
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
