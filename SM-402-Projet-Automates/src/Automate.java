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


    /******************************************************************/
    /*                          GET/SET                               */
    /******************************************************************/

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

    public ArrayList<Etat> getEtats() {
        return etats;
    }

    /******************************************************************/
    /*                            UTILS                               */
    /******************************************************************/

    public Etat getEtatByName(String name) {
        for (Etat etat : this.etats) {
            if (Objects.equals(etat.getName(), name)) {
                return etat;
            }
        }
        return null;
    }

    public boolean hasATransitionOn(Etat etat, String libelle) {

        boolean hasOne = false;
        for(Transition transition: this.getTransitionsByProvenance(etat)) {
            if(Objects.equals(transition.getLibelle(), libelle)) {
                hasOne = true;
            }
        }

        return hasOne;

    }

    public void afficherAutomate() {

        System.out.print("| Type | Etat | ");
        for (String lettre : this.alphabet) {
            System.out.print(lettre + " | ");
        }

        System.out.println("");

        for(Etat etat : this.etats) {

            if(this.etatsTerminaux.contains(etat) && this.etatsInitiaux.contains(etat)) {
                System.out.print("|  E/S |  ");

            } else if(this.etatsTerminaux.contains(etat)) {
                System.out.print("|  S   |  ");

            } else if(this.etatsInitiaux.contains(etat)) {
                System.out.print("|  E   |  ");

            } else {
                System.out.print("|      |  ");
            }

            System.out.print(etat.getName() + "   | ");

            ArrayList<Transition> transitionsArrayList = this.getTransitionsByProvenance(etat);
            for(String lettre: this.alphabet) {

                boolean isEmpty = true;
                for(Transition transition: transitionsArrayList) {
                    if(Objects.equals(transition.getLibelle(), lettre)) {
                        System.out.print(transition.getDestination().getName());
                        isEmpty = false;
                    }
                }

                if(isEmpty) {
                    System.out.print("-");
                }

                System.out.print(" | ");
            }

            System.out.println("");
        }
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

    public ArrayList<Transition> getTransitionsbyDestination(Etat etat) {
        ArrayList<Transition> transitions = new ArrayList<Transition>();
        for(Transition transition : this.transitions) {
            if(Objects.equals(transition.getDestination().getName(), etat.getName())) {
                transitions.add((transition));
            }
        }

        return transitions;
    }

    /******************************************************************/
    /*                       COMPLETION                               */
    /******************************************************************/

    public boolean isComplete() {
        for(Etat etat : this.etats) {
            int nombreTransition = this.getTransitionsByProvenance(etat).size();
            if(nombreTransition != this.getAlphabet().size()) {
                return false;


            }
        }

        return true;
    }

    public void complete() {

        if(!this.isComplete()) {

            // Création de l'état poubelle
            Etat poubelle = new Etat("P");

            for(Etat etat: this.etats) {

                ArrayList<Transition> transitionFromState = this.getTransitionsByProvenance(etat);

                // Il manque des transitions pour cette état
                if(transitionFromState.size() < this.getAlphabet().size()) {

                    for(int i = 0; i < this.getAlphabet().size(); i++) {
                        if(!this.hasATransitionOn(etat, this.getAlphabet().get(i))) {
                            Transition newTransition = new Transition(etat, poubelle, this.getAlphabet().get(i));
                            this.transitions.add(newTransition);
                        }

                    }

                }
            }

            // Ajout des transitions vers l'état poubelle
            for(String lettre: this.alphabet) {
                Transition transitionPoubelle = new Transition(poubelle, poubelle, lettre);
                this.transitions.add(transitionPoubelle);
            }
            this.etats.add(poubelle);

        } else {
            System.out.println("[!] Cette automate est déjà complet");
        }
    }

}
