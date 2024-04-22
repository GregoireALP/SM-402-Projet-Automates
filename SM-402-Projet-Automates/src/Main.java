import java.io.*;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;


        /******************************/
        /* REMPLACER PAR SON CHEMIN */
        String path = "C:\\Users\\grego\\Documents\\GitHub\\SM-402-Projet-Automates\\SM-402-Projet-Automates\\src\\core\\E2-";
        /******************************/

        String menu = """
                                _                        _      \s
                     /\\        | |                      | |     \s
                    /  \\  _   _| |_ ___  _ __ ___   __ _| |_ ___\s
                   / /\\ \\| | | | __/ _ \\| '_ ` _ \\ / _` | __/ _ \\
                  / ____ \\ |_| | || (_) | | | | | | (_| | ||  __/
                 /_/    \\_\\__,_|\\__\\___/|_| |_| |_|\\__,_|\\__\\___|
                                                                \s
                                                                \s
                                                                
                 1) Choisir un automate.
                 2) Afficher les automates.
                 3) Quitter
                 
                 
                 
                 
                 Choix:
                """;

        while(isRunning) {

            System.out.print(menu);
            String choice = scanner.nextLine();

            if(choice != "") {
                switch (Integer.parseInt(choice)) {

                    case 1:
                        System.out.print("Entrer le numéro de l'automate a tester:");
                        int res = scanner.nextInt();

                        try {
                            File file = new File(path + res + ".txt");
                            if(!file.exists()) {
                                System.out.println("[!] Automate introuvable");
                                continue;
                            }
                        } catch (Exception e) {
                            System.out.println("[!] Automate introuvable");
                            break;
                        }

                        Automate automate = new Automate(path + res + ".txt");
                        boolean isAutomateRunning = true;

                        String automateMenu = """
                                
                                Automate: %s
                                Choisissez une commande à faire
                                
                                1) Afficher automate
                                2) Compléter automate
                                3) Déterminiser automate
                                4) Standardiser automate
                                5) Complément automate
                                6) Test de reconnaissance de mot
                                7) Quitter cette automate
                                
                                Choix:
                                """.formatted(res);

                        while (isAutomateRunning) {

                            System.out.print(automateMenu);
                            int automateRes = scanner.nextInt();


                            switch (automateRes) {

                                case 1:
                                    automate.afficherAutomate();
                                    // Idling
                                    scanner.nextLine();
                                    break;

                                case 2:
                                    automate.complete();
                                    automate.afficherAutomate();
                                    // Idling
                                    scanner.nextLine();
                                    break;

                                case 3:
                                    automate.determinise();
                                    automate.afficherAutomate();
                                    // Idling
                                    scanner.nextLine();
                                    break;

                                case 4:
                                    automate.standardise();
                                    automate.afficherAutomate();
                                    // Idling
                                    scanner.nextLine();
                                    break;

                                case 5:
                                    automate.complementarize();
                                    automate.afficherAutomate();
                                    // Idling
                                    scanner.nextLine();
                                    break;

                                case 6:
                                    System.out.print("Entrer le mot à tester:");
                                    String word = scanner.next();

                                    boolean canRecognize = automate.recognize(word);
                                    System.out.println(canRecognize ? "Le mot est reconnu par l'automate" : "Le mot n'est pas reconnu par l'automate");


                                    // Idling
                                    scanner.nextLine();
                                    break;

                                case 7:
                                    isAutomateRunning = false;
                                    break;

                                default:
                                    System.out.println("[*] Choix invalide !");
                                    scanner.nextLine();
                                    break;
                            }
                        }


                        break;


                    case 2:
                        File folder = new File(path);
                        File[] files = folder.listFiles();

                        if(files != null) {
                            for(int i = 0; i < files.length; i++) {
                                System.out.println(i + 1 + ") " + files[i].getName());
                            }
                        } else {
                            System.out.println("[!] Dossier vide.");
                        }
                        break;


                    case 3:
                        isRunning = false;
                        break;
                }

                // Idling
                scanner.nextLine();
            } else {

                System.out.println("Aucun choix séléctionné");
                // Idling
                scanner.nextLine();
            }
        }
    }
}