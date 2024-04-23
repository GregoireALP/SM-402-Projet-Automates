import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Automate {

    private ArrayList<Etat> etats = new ArrayList<Etat>();
    public static ArrayList<Etat> etatsInitiaux= new ArrayList<Etat>();
    public static ArrayList<Etat> etatsTerminaux= new ArrayList<Etat>();

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

    public void setEtatsTerminaux(ArrayList<Etat> etatsTerminaux) {
        this.etatsTerminaux = etatsTerminaux;
    }

    public void setEtatsInitiaux(ArrayList<Etat> etatsInitiaux) {
        this.etatsInitiaux = etatsInitiaux;
    }

    /******************************************************************/
    /*                            UTILS                               */
    /******************************************************************/

    // Fonction pour obtenir un état par son nom
    public Etat getEtatByName(String name) {
        for (Etat etat : this.etats) {
            if (Objects.equals(etat.getName(), name)) {
                return etat;
            }
        }
        return null;
    }

    public void afficherAutomate() {
        int maxStateWidth = "Etat".length();  // Largeur initiale basée sur le mot 'Etat'
        int maxTypeWidth = "Type".length();  // Largeur initiale basée sur le mot 'Type'
        Map<String, Integer> maxTransitionWidths = new HashMap<>();

        // Initialiser avec les largeurs des entêtes des symboles de l'alphabet
        for (String letter : this.alphabet) {
            maxTransitionWidths.put(letter, letter.length());
        }

        // Calculer les largeurs maximales pour les états, types et transitions
        for (Etat etat : this.etats) {
            maxStateWidth = Math.max(maxStateWidth, etat.getName().length());
            ArrayList<Transition> transitions = this.getTransitionsByProvenance(etat);
            for (Transition transition : transitions) {
                String symbol = transition.getLibelle();
                String destNames = transitions.stream()
                        .filter(t -> t.getProvenance().equals(etat) && t.getLibelle().equals(symbol))
                        .map(t -> t.getDestination().getName())
                        .distinct()
                        .collect(Collectors.joining(", "));
                maxTransitionWidths.put(symbol, Math.max(maxTransitionWidths.get(symbol), destNames.length()));
            }
        }

        // Construction de l'en-tête
        System.out.print("╔");
        System.out.print("═".repeat(maxTypeWidth + 2));
        System.out.print("╦");
        System.out.print("═".repeat(maxStateWidth + 2));
        for (String letter : this.alphabet) {
            System.out.print("╦");
            System.out.print("═".repeat(maxTransitionWidths.get(letter) + 2));
        }
        System.out.println("╗");

        // Affichage des entêtes
        System.out.print("║ " + "Type" + " ".repeat(maxTypeWidth - 4 + 1));
        System.out.print("║ " + "Etat" + " ".repeat(maxStateWidth - 4 + 1));
        for (String letter : this.alphabet) {
            System.out.print("║ " + letter + " ".repeat(maxTransitionWidths.get(letter) - letter.length() + 1));
        }
        System.out.println("║");

        // Affichage des lignes pour chaque état
        for (Etat etat : this.etats) {
            String type = this.etatsInitiaux.contains(etat) ? "E" : " ";
            type = this.etatsTerminaux.contains(etat) ? type + "S" : type;

            System.out.print("╠");
            System.out.print("═".repeat(maxTypeWidth + 2));
            System.out.print("╬");
            System.out.print("═".repeat(maxStateWidth + 2));
            for (String letter : this.alphabet) {
                System.out.print("╬");
                System.out.print("═".repeat(maxTransitionWidths.get(letter) + 2));
            }
            System.out.println("╣");

            System.out.print("║ " + type + " ".repeat(maxTypeWidth - type.length() + 1));
            System.out.print("║ " + etat.getName() + " ".repeat(maxStateWidth - etat.getName().length() + 1));
            for (String letter : this.alphabet) {
                ArrayList<Transition> transitions = getTransitionsByProvenance(etat);
                String destNames = transitions.stream()
                        .filter(t -> t.getProvenance().equals(etat) && t.getLibelle().equals(letter))
                        .map(t -> t.getDestination().getName())
                        .distinct()
                        .collect(Collectors.joining(", "));
                System.out.print("║ " + (destNames.isEmpty() ? "" : destNames) + " ".repeat(maxTransitionWidths.get(letter) - destNames.length() + 1));
            }
            System.out.println("║");
        }

        // Pied de page
        System.out.print("╚");
        System.out.print("═".repeat(maxTypeWidth + 2));
        System.out.print("╩");
        System.out.print("═".repeat(maxStateWidth + 2));
        for (String letter : this.alphabet) {
            System.out.print("╩");
            System.out.print("═".repeat(maxTransitionWidths.get(letter) + 2));
        }
        System.out.println("╝");
    }

    // Fonction pour obtenir toutes les transitions d'un état donné
    public ArrayList<Transition> getTransitionsByProvenance(Etat etat) {
        ArrayList<Transition> transitions = new ArrayList<Transition>();
        for (Transition transition : this.transitions) {
            if (Objects.equals(transition.getProvenance().getName(), etat.getName())) {
                transitions.add(transition);
            }
        }
        return transitions;
    }

    // Fonction pour obtenir une transition spécifique d'un état donné pour un symbole spécifique
    public Transition getTransition(Etat etat, String symbole) {
        for(Transition transition: this.getTransitionsByProvenance(etat)) {
            if(transition.getLibelle().equals(symbole)) {
                return transition;
            }
        }

        return null;
    }

    // Fonction pour obtenir les états suivants d'un état donné pour un symbole spécifique
    private Set<Etat> getNextStates(Etat state, String symbol) {
        Set<Etat> nextStates = new HashSet<>();
        for (Transition trans : transitions) {
            if (trans.getProvenance().equals(state) && trans.getLibelle().equals(symbol)) {
                nextStates.add(trans.getDestination());
            }
        }
        return nextStates;
    }

    // Fonction pour convertir un ensemble d'états en une chaîne représentant l'état DFA
    private String stateSetToString(Set<Etat> states) {
        return states.stream().map(Etat::getName).sorted().collect(Collectors.joining("."));
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

    /**
     * Complète l'automate en ajoutant un état poubelle.
     * Cet état est utilisé pour toutes les transitions non définies dans l'automate initial.
     */
    public void complete() {
        if(!this.isComplete()) {
            Etat etatPoubelle = new Etat("P"); // Créer un nouvel état poubelle
            this.etats.add(etatPoubelle); // Ajouter l'état poubelle à la liste des états

            // Vérifier chaque état pour chaque symbole de l'alphabet
            for (Etat etat : this.etats) {
                for (String symbole : this.alphabet) {
                    if (getTransition(etat, symbole) == null) {
                        // Si aucune transition n'existe pour un symbole donné, ajouter une transition vers l'état poubelle
                        this.transitions.add(new Transition(etat, etatPoubelle, symbole));
                    }
                }
            }
        } else {
            System.out.println("[!] Cet automate est déjà complet");
        }
    }

    /******************************************************************/
    /*                       STANDARDISATION                          */
    /******************************************************************/

    public boolean isStandard() {

        boolean isStandard = true;

        // Il doit y avoir 1 seule entré
        if(this.getEtatsInitiaux().size() != 1) {
            isStandard =  false;

        } else {

            // Aucune transition ne doit revenir sur l'état initiale
            for(Transition transition: this.getTransitions()) {
                if(transition.getDestination() == this.getEtatsInitiaux().get(0)) {
                    isStandard = false;
                }
            }
        }

        return isStandard;
    }

    public void standardise() {


        if(!this.isStandard()) {

            // Creation du nouvelle etat I
            Etat initiale = new Etat("I");

            // Pour chaque état initiale
            for(Etat etat: this.getEtatsInitiaux()) {

                // On récupère les transitions de ces états
                ArrayList<Transition> transitions = getTransitionsByProvenance(etat);

                // Pour chacune de ces transitions
                for(Transition transition: transitions) {

                    // On créer une nouvelle transition avec I en provenance
                    Transition newTransition = new Transition(initiale, transition.getDestination(), transition.getLibelle());

                    // On ajoute cette nouvelle transition à l'automate
                    this.getTransitions().add(newTransition);
                }
            }

            // TODO: Reconnaissance du mot vide

            // On supprime les états initiaux et on ajoute I à l'automate
            this.getEtatsInitiaux().clear();
            this.getEtats().add(initiale);
            this.getEtatsInitiaux().add(initiale);

        } else {
            System.out.println("[!] Cette automate est déjà standard");
        }
    }

    /******************************************************************/
    /*                       DETERMINISATION                          */
    /******************************************************************/

    public boolean isDeterministe() {
        // Pour chaque état dans l'automate
        for (Etat etat : this.etats) {
            // Créer un ensemble pour suivre les symboles déjà traités pour cet état
            HashSet<String> seenSymbols = new HashSet<>();

            // Obtenir toutes les transitions à partir de cet état
            ArrayList<Transition> transitionsFromState = getTransitionsByProvenance(etat);

            // Examiner chaque transition
            for (Transition transition : transitionsFromState) {
                String symbol = transition.getLibelle();

                // Vérifier si une transition pour ce symbole a déjà été vue
                if (seenSymbols.contains(symbol)) {
                    // Si oui, alors il y a plus d'une transition pour un symbole dans cet état, non déterministe
                    return false;
                }
                // Ajouter ce symbole à l'ensemble des symboles vus
                seenSymbols.add(symbol);
            }

            // Vérifier si tous les symboles de l'alphabet sont couverts
            if (seenSymbols.size() != this.alphabet.size()) {
                // Si tous les symboles ne sont pas couverts, l'automate n'est pas complet, donc pas déterministe
                // Note: cette partie est optionnelle en fonction de votre définition de déterminisme
                // Si votre définition de DFA inclut qu'il doit être complet, décommentez la ligne suivante
                // return false;
            }
        }

        // Si aucune condition de non-déterminisme n'a été rencontrée, l'automate est déterministe
        return true;
    }

    public void determinise() {

        if(!this.isDeterministe()) {
            Map<Set<Etat>, Etat> etatsDFA = new HashMap<>(); // Carte des ensembles d'états aux nouveaux états DFA
            Queue<Set<Etat>> queue = new LinkedList<>(); // File pour les ensembles d'états à traiter
            List<Transition> newTransitions = new ArrayList<>(); // Nouvelles transitions pour le DFA

            // Créer l'état initial du DFA
            Set<Etat> initialSet = new HashSet<>(etatsInitiaux);
            Etat initialState = new Etat(stateSetToString(initialSet));
            etatsDFA.put(initialSet, initialState);
            queue.add(initialSet);

            while (!queue.isEmpty()) {
                Set<Etat> currentSet = queue.poll();
                Etat currentDFAState = etatsDFA.get(currentSet);

                // Ajouter l'état DFA aux états finaux si nécessaire
                if (currentSet.stream().anyMatch(etatsTerminaux::contains)) {
                    etatsTerminaux.add(currentDFAState);
                }

                for (String sym : alphabet) {
                    Set<Etat> nextStateSet = new HashSet<>();
                    for (Etat etat : currentSet) {
                        nextStateSet.addAll(getNextStates(etat, sym));
                    }

                    if (!nextStateSet.isEmpty()) {
                        Etat nextState;
                        if (!etatsDFA.containsKey(nextStateSet)) {
                            nextState = new Etat(stateSetToString(nextStateSet));
                            etatsDFA.put(nextStateSet, nextState);
                            queue.add(nextStateSet);
                        } else {
                            nextState = etatsDFA.get(nextStateSet);
                        }

                        newTransitions.add(new Transition(currentDFAState, nextState, sym));
                    }
                }
            }

            // Réinitialiser et mettre à jour l'automate avec les nouvelles structures DFA
            etats.clear();
            etats.addAll(etatsDFA.values());
            transitions = (ArrayList<Transition>) newTransitions;
            etatsInitiaux.clear();
            etatsInitiaux.add(initialState);
        } else {
            System.out.println("[!] Cet automate est déjà déterministe");
        }
    }

    /******************************************************************/
    /*                       COMPLEMENTARITY                          */
    /******************************************************************/

    /**
     * Complémentarise l'automate : transforme l'automate pour qu'il accepte le langage complémentaire.
     * L'automate doit être déterministe et complet pour que cette opération soit valide.
     */
    public void complementarize() {
        if (this.isDeterministe() && this.isComplete()) {


            ArrayList<Etat> newFinalStates = new ArrayList<>();

            for(Etat etat: this.getEtats()) {
                if(!this.getEtatsTerminaux().contains(etat)) {
                    newFinalStates.add(etat);
                }
            }

            this.setEtatsTerminaux(newFinalStates);

        } else {
            System.out.println("[*] L'automate n'est pas complet et deterministe");
        }
    }


    /******************************************************************/
    /*                       WORDS RECOGNIZATION                      */
    /******************************************************************/

    /**
     * Recognize a word using the current automaton.
     * @param word The word to recognize
     * @return True if the word is accepted by the automaton, false otherwise
     */
    public boolean recognize(String word) {
        Set<Etat> currentStates = new HashSet<>(this.etatsInitiaux); // Start from all initial states

        // Process each character in the word
        for (int i = 0; i < word.length(); i++) {
            char symbol = word.charAt(i);
            Set<Etat> nextStates = new HashSet<>();

            // Move to next states based on the current symbol
            for (Etat state : currentStates) {
                for (Transition transition : getTransitionsByProvenance(state)) {
                    if (transition.getLibelle().equals(String.valueOf(symbol))) {
                        nextStates.add(transition.getDestination());
                    }
                }
            }

            // Update current states with the next states
            currentStates = nextStates;

            // If no states are reachable, break early
            if (currentStates.isEmpty()) {
                return false;
            }
        }

        // Check if any of the current states is a final state
        for (Etat state : currentStates) {
            if (this.etatsTerminaux.contains(state)) {
                return true; // Word is accepted if any final state is reached
            }
        }

        return false; // Word is not accepted if no final state is reached
    }

}

