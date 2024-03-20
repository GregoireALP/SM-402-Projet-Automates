import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Automate {

    private ArrayList<Etat> etats = new ArrayList<Etat>();
    private ArrayList<Etat> etatsInitiaux= new ArrayList<Etat>();
    private ArrayList<Etat> etatsTerminaux= new ArrayList<Etat>();

    private ArrayList<String> alphabet = new ArrayList<String>();

    private ArrayList<Transition> transitions = new ArrayList<Transition>();

    public Automate(String path) {

        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            int lineCounter = 0;

            while(scanner.hasNextLine()) {

                switch (lineCounter) {

                    case 0:
                        scanner.nextLine();
                        break;

                    // Enregistrement des etats
                    case 1:
                        String line = scanner.nextLine();
                        int nombreEtats = Integer.parseInt(line);

                        // On enleve le premiere element qui correspond seulement au nombre d'etat
                        String[] etatsInitiauxAsTab = scanner.nextLine().substring(1).split(" ");
                        String[] etatsTerminauxAsTab = scanner.nextLine().substring(1).split(" ");

                        // On les transforme en liste pour mieux les manipuler
                        List<String> etatsInitiauxAsList = Arrays.asList(etatsInitiauxAsTab);
                        List<String> etatsTerminauxAsList = Arrays.asList(etatsTerminauxAsTab);

                        for(int i = 0; i < nombreEtats; i++) {

                            Etat etat = new Etat(Integer.toString(i));

                            // On ajoute l'etats a la liste
                            this.etats.add(etat);

                            // Si il est dans la liste des etats intitiaux
                            if(etatsInitiauxAsList.contains(Integer.toString(i))) {
                                this.etatsInitiaux.add(etat);
                            }

                            // Si il est dans la liste des etats finaux
                            if(etatsTerminauxAsList.contains(Integer.toString(i))) {
                                this.etatsTerminaux.add(etat);
                            }
                        }
                        break;

                    case 4:
                        int nombreTransition = Integer.parseInt(scanner.nextLine());
                        for(int i = 0; i < nombreTransition; i++) {

                            String[] transitonAsString = scanner.nextLine().split("");
                            String provenance = transitonAsString[0];
                            String libelle = transitonAsString[1];
                            String destination = transitonAsString[2];

                            // Regarder si le libelle n'est pas dans l'alphabet
                            if(!this.alphabet.contains(libelle)) {
                                this.alphabet.add(libelle);
                            }

                            Transition transition = new Transition(this.getEtatByName(provenance), this.getEtatByName(destination), libelle);
                            this.transitions.add(transition);
                        }
                }


                lineCounter++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("[!] Fichier non trouvé");
        }
    }

    public ArrayList<Etat> getEtatsInitiaux() {
        return etatsInitiaux;
    }

    public ArrayList<Etat> getEtatsTerminaux() {
        return etatsTerminaux;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public ArrayList<String> getAlphabet() {
        return alphabet;
    }

    public Etat getEtatByName(String name) {
        for (Etat etat : this.etats) {
            if (Objects.equals(etat.getName(), name)) {
                return etat;
            }
        }
        return null;
    }

    public ArrayList<Transition> getTransitionsByProvenance(Etat etat) {
        ArrayList<Transition> transitions = new ArrayList<Transition>();
        for (Transition transition : this.transitions) {
            if (Objects.equals(transition.getProvenance().getName(), etat.getName())) {
                transitions.add(transition);
            }
        }
        return transitions;
    }

    public boolean isComplete() {
        for(Etat etat : this.etats) {
            int nombreTransition = this.getTransitionsByProvenance(etat).size();
            if(nombreTransition != 2) {
                return false;
            }
        }

        return true;
    }


}
