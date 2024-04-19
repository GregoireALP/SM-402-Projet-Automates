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
        int maxStateWidth = "Etat".length();  // Start with header width
        int maxTypeWidth = "Type".length();  // Start with header width
        Map<String, Integer> maxTransitionWidths = new HashMap<>();

        // Initialize maxTransitionWidths with alphabet headers, and adjust for contents
        for (String letter : this.alphabet) {
            maxTransitionWidths.put(letter, letter.length());
        }

        // Calculate max widths for states, types, and transitions
        for (Etat etat : this.etats) {
            maxStateWidth = Math.max(maxStateWidth, etat.getName().length());
            ArrayList<Transition> transitions = this.getTransitionsByProvenance(etat);
            for (Transition transition : transitions) {
                String symbol = transition.getLibelle();
                String destName = transition.getDestination().getName();
                maxTransitionWidths.put(symbol, Math.max(maxTransitionWidths.get(symbol), destName.length()));
            }
        }

        // Build the header format string
        StringBuilder headerBuilder = new StringBuilder("╔");
        headerBuilder.append("═".repeat(maxTypeWidth + 2));
        headerBuilder.append("╦");
        headerBuilder.append("═".repeat(maxStateWidth + 2));
        for (String letter : this.alphabet) {
            headerBuilder.append("╦");
            headerBuilder.append("═".repeat(maxTransitionWidths.get(letter) + 2));
        }
        headerBuilder.append("╗");
        System.out.println(headerBuilder);

        // Print header
        String headerFormat = "║ %-"+maxTypeWidth+"s ║ %-"+maxStateWidth+"s ║ ";
        System.out.print(String.format(headerFormat, "Type", "Etat"));
        for (String letter : this.alphabet) {
            System.out.print(String.format("%-"+maxTransitionWidths.get(letter)+"s ║ ", letter));
        }
        System.out.println();

        // Print each state row
        for (Etat etat : this.etats) {
            String type = this.etatsInitiaux.contains(etat) ? "E" : " ";
            type = this.etatsTerminaux.contains(etat) ? type + "S" : type;

            StringBuilder rowBuilder = new StringBuilder("╠");
            rowBuilder.append("═".repeat(maxTypeWidth + 2));
            rowBuilder.append("╬");
            rowBuilder.append("═".repeat(maxStateWidth + 2));

            for (String letter : this.alphabet) {
                rowBuilder.append("╬");
                rowBuilder.append("═".repeat(maxTransitionWidths.get(letter) + 2));
            }
            rowBuilder.append("╣");
            System.out.println(rowBuilder.toString());

            System.out.print(String.format(headerFormat, type.trim(), etat.getName()));
            for (String letter : this.alphabet) {
                boolean found = false;
                for (Transition transition : this.getTransitionsByProvenance(etat)) {
                    if (transition.getLibelle().equals(letter)) {
                        System.out.print(String.format("%-"+maxTransitionWidths.get(letter)+"s ║ ", transition.getDestination().getName()));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.print(String.format("%-"+maxTransitionWidths.get(letter)+"s ║ ", "-"));
                }
            }
            System.out.println();
        }

        // Closing border
        StringBuilder footerBuilder = new StringBuilder("╚");
        footerBuilder.append("═".repeat(maxTypeWidth + 2));
        footerBuilder.append("╩");
        footerBuilder.append("═".repeat(maxStateWidth + 2));
        for (String letter : this.alphabet) {
            footerBuilder.append("╩");
            footerBuilder.append("═".repeat(maxTransitionWidths.get(letter) + 2));
        }
        footerBuilder.append("╝");
        System.out.println(footerBuilder);
    }

    private Set<Etat> getNextStates(Etat state, String symbol) {
        Set<Etat> result = new HashSet<>();
        for (Transition transition : this.transitions) {
            if (transition.getProvenance().equals(state) && transition.getLibelle().equals(symbol)) {
                result.add(transition.getDestination());
            }
        }
        return result;
    }

    private String stateSetToString(Set<Etat> states) {
        return states.stream().map(Etat::getName).sorted().collect(Collectors.joining("."));
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

    public Transition getTransition(Etat etat, String symbole) {
        for(Transition transition: this.getTransitionsByProvenance(etat)) {
            if(transition.getLibelle().equals(symbole)) {
                return transition;
            }
        }

        return null;
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

    public boolean isDeterminise() {
        // Check every state to ensure only one transition per symbol in the alphabet
        for (Etat etat : etats) {
            ArrayList<Transition> transitionsFromState = getTransitionsByProvenance(etat);

            // Use a set to track symbols for which transitions exist
            HashSet<String> seenSymbols = new HashSet<>();

            for (Transition transition : transitionsFromState) {
                String symbol = transition.getLibelle();

                // If we see the same symbol twice for this state, it's not deterministic
                if (seenSymbols.contains(symbol)) {
                    return false;  // Not deterministic
                }
                seenSymbols.add(symbol);
            }
        }

        // If we've checked all states and found no conflicts, the automaton is deterministic
        return true;
    }


    public void determinise() {
        // Map pour suivre les nouveaux états (clé : nom combiné, valeur : ensemble d'états)
        Map<String, Set<Etat>> newEtatsMap = new HashMap<>();
        // Liste des transitions pour l'automate déterministe
        List<Transition> newTransitions = new ArrayList<>();
        // Queue pour gérer les nouveaux états à traiter
        Queue<Set<Etat>> queue = new LinkedList<>();

        // Créer l'état initial du déterministe
        Set<Etat> initialSet = new HashSet<>(this.etatsInitiaux);
        String initialName = initialSet.stream().map(Etat::getName).sorted().collect(Collectors.joining());
        newEtatsMap.put(initialName, initialSet);
        queue.add(initialSet);

        while (!queue.isEmpty()) {
            Set<Etat> currentSet = queue.poll();
            String currentName = currentSet.stream().map(Etat::getName).sorted().collect(Collectors.joining());

            // Pour chaque symbole de l'alphabet
            for (String symbol : this.alphabet) {
                Set<Etat> nextSet = new HashSet<>();
                for (Etat state : currentSet) {
                    nextSet.addAll(getNextStates(state, symbol));
                }
                if (!nextSet.isEmpty()) {
                    String nextName = nextSet.stream().map(Etat::getName).sorted().collect(Collectors.joining());
                    if (!newEtatsMap.containsKey(nextName)) {
                        newEtatsMap.put(nextName, nextSet);
                        queue.add(nextSet);
                    }
                    newTransitions.add(new Transition(new Etat(currentName), new Etat(nextName), symbol));
                }
            }
        }

        // Réinitialiser les états et transitions de l'automate pour utiliser les nouveaux
        this.etats.clear();
        this.transitions.clear();
        newEtatsMap.forEach((name, set) -> {
            Etat newState = new Etat(name);
            this.etats.add(newState);
            if (set.stream().anyMatch(Etat::isFinal)) {
                this.etatsTerminaux.add(newState);
            }
            if (name.equals(initialName)) {
                this.etatsInitiaux.clear();
                this.etatsInitiaux.add(newState);
            }
        });
        this.transitions.addAll(newTransitions);
    }

    /******************************************************************/
    /*                       COMPLEMENTARITY                          */
    /******************************************************************/

    /**
     * Complémentarise l'automate : transforme l'automate pour qu'il accepte le langage complémentaire.
     * L'automate doit être déterministe et complet pour que cette opération soit valide.
     */
    public void complementarize() {
        if (this.isDeterminise() && this.isComplete()) {
            ArrayList<Etat> oldEtatsInitiaux = this.getEtatsInitiaux();
            this.setEtatsInitiaux(this.getEtatsInitiaux());
            this.setEtatsTerminaux(oldEtatsInitiaux);
        } else {
            System.out.println("[*] L'automate n'est pas complet et deterministe");
        }
    }


    /******************************************************************/
    /*                       WORDS RECOGNIZATION                      */
    /******************************************************************/


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

