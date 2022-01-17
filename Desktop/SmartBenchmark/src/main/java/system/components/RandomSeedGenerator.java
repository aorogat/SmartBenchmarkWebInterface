package system.components;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.Database;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.model.Predicate;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class RandomSeedGenerator 
{
    static ArrayList<Branch> branchs = new ArrayList<>();
    static ArrayList<Predicate> availablePredicates = new ArrayList<>();
    
    public static void generateSeedList()
    {
        //Get subject types avialable in Lexicon
        availablePredicates = Database.getAvailablePredicates();
        System.out.println("We have " + availablePredicates.size() + " types");
        System.out.println("==============================================");
        int count = 0;
        for (Predicate p : availablePredicates) {
            count += 20; if(count>=2000) break;
            //Get some examples
            Branch branch = SPARQL.getBranchOfType_SType_connectTo_OType(KG_Settings.explorer, p.getPredicateContext().getSubjectType(),
                    p.getPredicateContext().getObjectType(), p.getPredicateURI(), 0);
            System.out.println(
                    p.getPredicate() + "\t" + 
                    p.getPredicateContext().getSubjectType()+ "\t" + branch.s + "\t" + 
                    p.getPredicateContext().getObjectType() + "\t" + branch.o + "\t");
            branchs.add(branch);
        }
    }

    public static void main(String[] args) {
        generateSeedList();
    }
}
