package system.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import offLine.kg_explorer.explorer.Database;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.model.Predicate;
import settings.KG_Settings;

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

    public static void generateSeedList() {

        //Get subject types avialable in Lexicon
        availablePredicates = Database.getAvailablePredicates();
        System.out.println("We have " + availablePredicates.size() + " types");
        System.out.println("==============================================");

        int typesSize = availablePredicates.size();

        for (Predicate p : availablePredicates) {
            if (p.getPredicateContext().getObjectType().equals(KG_Settings.Number)) {
                branches_with_number.add(p);
            } else if (p.getPredicateContext().getObjectType().equals(KG_Settings.Date)) {
                branches_with_date.add(p);
            } else {
                branches_with_entity.add(p);
            }
        }
        
        Random random = new Random();

        //numbers
        int count = 0;
        
        for (int i = random.nextInt(8)+2; i < branches_with_number.size(); i = (int) (i * 5.2 + 1)) { //make it 1.2
            Predicate p = branches_with_number.get(i);
            count += 1;
//            if (count >= 1) {
//                break;
//            }
            //Get some examples
            count += 1;
            if (count >= 20) {
                break;
            }
            addBranchs(p);
        }

        //dates
        count = 0;
        for (int i = random.nextInt(8)+2; i < branches_with_date.size(); i = (int) (i * 5.2 + 1)) { //make it 1.2
            Predicate p = branches_with_date.get(i);
            if (p.getPredicateURI().contains("populationAsOf")) {
                continue;
            }
            count += 1;
            if (count >= 20) {
                break;
            }
            addBranchs(p);
        }

        //entities
        count = 0;
        double m = branches_with_entity.size()/10;
        if(m>50)
            m = 50;
        int step = (int) (branches_with_entity.size()/m);
        for (int i = random.nextInt(8)+2; i < branches_with_entity.size(); i = i+step) { 
            Predicate p = branches_with_entity.get(i);
            m = (0.2 * i) + 1;
            addBranchs(p);
        }
        
        
        branches_with_entity = new ArrayList<>(new HashSet<>(branches_with_entity));
        branches_with_number= new ArrayList<>(new HashSet<>(branches_with_number));
        branches_with_date = new ArrayList<>(new HashSet<>(branches_with_date));

        System.out.println("Numbers type list size: " + branches_with_number.size());
        System.out.println("Dates type list size: " + branches_with_date.size());
        System.out.println("URIs type list size: " + branches_with_entity.size());
    }

    private static void addBranchs(Predicate p) {
        if (p == null) {
            return;
        }
        for (int j = 0; j <= 100; j = (int) ((j * 15.2) + 1)) {
            Branch branch = SPARQL.getBranchOfType_SType_connectTo_OType(KG_Settings.explorer, p.getPredicateContext().getSubjectType(),
                    p.getPredicateContext().getObjectType(), p.getPredicateURI(), j);
            if (branch == null) {
                return;
            }
            System.out.println(
                    p.getPredicate() + "\t"
                    + branch.s + "["+KG_Settings.explorer.removePrefix(p.getPredicateContext().getSubjectType())+"]\t"
                    + branch.o + "["+KG_Settings.explorer.removePrefix(p.getPredicateContext().getObjectType())+ "]");
            branchs.add(branch);
        }
    }

    public static void main(String[] args) {
        generateSeedList();
    }
}
