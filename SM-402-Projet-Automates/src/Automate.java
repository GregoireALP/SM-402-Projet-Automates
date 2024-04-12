import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

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
        return states.stream().map(Etat::getName).sorted().collect(Collectors.joining(""));
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
        HashMap<String, Etat> newStates = new HashMap<>();
        Queue<Set<Etat>> toProcess = new LinkedList<>();
        Set<Etat> initialStateSet = new HashSet<>(this.etatsInitiaux);
        Set<Etat> newFinalStates = new HashSet<>();

        newStates.put(stateSetToString(initialStateSet), new Etat(stateSetToString(initialStateSet)));
        if (!Collections.disjoint(initialStateSet, this.etatsTerminaux)) {
            newFinalStates.add(newStates.get(stateSetToString(initialStateSet)));
        }
        toProcess.add(initialStateSet);

        ArrayList<Transition> newTransitions = new ArrayList<>();

        while (!toProcess.isEmpty()) {
            Set<Etat> currentStateSet = toProcess.poll();
            String currentStateString = stateSetToString(currentStateSet);

            for (String symbol : this.alphabet) {
                Set<Etat> nextStateSet = new HashSet<>();
                for (Etat state : currentStateSet) {
                    nextStateSet.addAll(getNextStates(state, symbol));
                }

                String nextStateString = stateSetToString(nextStateSet);
                if (!newStates.containsKey(nextStateString)) {
                    newStates.put(nextStateString, new Etat(nextStateString));
                    toProcess.add(nextStateSet);
                    if (!Collections.disjoint(nextStateSet, this.etatsTerminaux)) {
                        newFinalStates.add(newStates.get(nextStateString));
                    }
                }

                newTransitions.add(new Transition(newStates.get(currentStateString), newStates.get(nextStateString), symbol));
            }
        }

        // Update the automaton's states and transitions
        this.etats.clear();
        this.etats.addAll(newStates.values());
        this.etatsInitiaux.clear();
        this.etatsInitiaux.add(newStates.get(stateSetToString(initialStateSet)));
        this.etatsTerminaux.clear();
        this.etatsTerminaux.addAll(newFinalStates);
        this.transitions.clear();
        this.transitions.addAll(newTransitions);
    }

    /******************************************************************/
    /*                       COMPLEMENTARITY                          */
    /******************************************************************/

    public void complementarize() {


        if(this.isDeterminise() && this.isComplete()) {

            ArrayList<Etat> oldEtatsInitiaux = this.getEtatsInitiaux();

            this.setEtatsInitiaux(this.getEtatsInitiaux());
            this.setEtatsTerminaux(oldEtatsInitiaux);

        } else {
            System.out.println("[*] L'automate n'est pas complet et deterministe");
        }
    }

}

