//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {


        Automate automate = new Automate("C:\\Users\\grego\\Documents\\GitHub\\SM-402-Projet-Automates\\SM-402-Projet-Automates\\src\\automate.txt");

        automate.afficherAutomate();
        System.out.println(automate.isStandard());

        automate.standardise();
        automate.afficherAutomate();
        System.out.println(automate.isStandard());



    }
}