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
}
