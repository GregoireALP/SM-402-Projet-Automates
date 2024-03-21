//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {


        Automate automate = new Automate("C:\\Users\\grego\\Documents\\GitHub\\SM-402-Projet-Automates\\SM-402-Projet-Automates\\src\\automate.txt");

        System.out.println(automate.isComplete());
        automate.complete();
        automate.afficherAutomate();
        System.out.println(automate.isComplete());


    }
}