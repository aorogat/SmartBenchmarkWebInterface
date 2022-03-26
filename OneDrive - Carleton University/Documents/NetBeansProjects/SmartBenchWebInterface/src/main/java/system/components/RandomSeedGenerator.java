package system.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import database.Database;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.model.Predicate;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class RandomSeedGenerator {

    static ArrayList<Branch> branchs = new ArrayList<>();
    static ArrayList<Predicate> availablePredicates = new ArrayList<>();

    static ArrayList<Predicate> branches_with_number = new ArrayList<>();
    static ArrayList<Predicate> branches_with_date = new ArrayList<>();
    static ArrayList<Predicate> branches_with_entity = new ArrayList<>();
    static ArrayList<Predicate> branches_with_Literal = new ArrayList<>();

    public static void generateSeedList() {

        //Get subject types avialable in Lexicon
        availablePredicates = Database.getAvailablePredicates();
        System.out.println("We have " + availablePredicates.size() + " types");
        System.out.println("==============================================");

        int typesSize = availablePredicates.size();

        for (Predicate p : availablePredicates) {
            if (p.getPredicateContext().getObjectType().equals(Settings.Number)) {
                branches_with_number.add(p);
            } else if (p.getPredicateContext().getObjectType().equals(Settings.Date)) {
                branches_with_date.add(p);
            } else if (p.getPredicateContext().getObjectType().equals(Settings.Literal)) {
                branches_with_Literal.add(p);
            } else {
                branches_with_entity.add(p);
            }
        }

        Random random = new Random();

        //numbers
        int count = 0;

        for (int i = 0;//random.nextInt(8)+2; 
                i < branches_with_number.size(); i = i + 1//(int) (i * 1.2 + 1)
                ) { //make it 1.2
            Predicate p = branches_with_number.get(i);
            count += 1;
            if (count >= 2) {
                break;
            }
            //Get some examples
//            count += 1;
//            if (count >= 20) {
//                break;
//            }
            addBranchs(p);
        }

        //dates
        count = 0;
        for (int i = 0;//random.nextInt(3)+2; 
                i < branches_with_date.size(); i = i + 1//(int) (i * 1.2 + 1)
                ) {
            Predicate p = branches_with_date.get(i);
            if (p.getPredicateURI().contains("populationAsOf")) {
                continue;
            }
            count += 1;
            if (count >= 3) {
                break;
            }
            addBranchs(p);
        }

        //literals
        count = 0;
        for (int i = 0; i < branches_with_Literal.size(); i = i + 1//(int) (i * 1.2 + 1)
                ) {
            Predicate p = branches_with_Literal.get(i);
            if (p.getPredicateURI().contains("populationAsOf")) {
                continue;
            }
            count += 1;
            if (count >= 3) {
                break;
            }
            addBranchs(p);
        }

        //entities
        count = 0;
//        double m = branches_with_entity.size()/10;
//        if(m>100)
//            m = 100;
//        int step = (int) (branches_with_entity.size()/m);
        for (int i = 0;//random.nextInt(8)+2; 
                i < branches_with_entity.size(); i = i + 1//(int) (i * 1.2 + 1)
                ) {
            Predicate p = branches_with_entity.get(i);
//            m = (0.2 * i) + 1;
            count += 1;
            if (count >= 3) {
                break;
            }
            addBranchs(p);
        }

        branches_with_entity = new ArrayList<>(new HashSet<>(branches_with_entity));
        branches_with_number = new ArrayList<>(new HashSet<>(branches_with_number));
        branches_with_date = new ArrayList<>(new HashSet<>(branches_with_date));
        branches_with_Literal = new ArrayList<>(new HashSet<>(branches_with_Literal));

        System.out.println("Numbers type list size: " + branches_with_number.size());
        System.out.println("Dates type list size: " + branches_with_date.size());
        System.out.println("URIs type list size: " + branches_with_entity.size());
        System.out.println("Literals type list size: " + branches_with_Literal.size());
    }

    private static void addBranchs(Predicate p) {
        if (p == null) {
            return;
        }
        Random random = new Random();
        int c = random.nextInt(10);
        int count = 0;
        for (int j = c;
                j <= c + 2; j++) { //=  (int) ((j * 3.2) + 1)) {
            Branch branch = SPARQL.getBranchOfType_SType_connectTo_OType(Settings.explorer, p.getPredicateContext().getSubjectType(),
                    p.getPredicateContext().getObjectType(), p.getPredicateURI(), j);
            if (branch == null) {
                return;
            }
            System.out.println(p.getPredicate() + "\t"
                    + branch.s + "[" + Settings.explorer.removePrefix(p.getPredicateContext().getSubjectType()) + "]\t"
                    + branch.o + "[" + Settings.explorer.removePrefix(p.getPredicateContext().getObjectType()) + "]");
            branchs.add(branch);
            count += 1;
            if (count >= 2) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        generateSeedList();
    }
}
