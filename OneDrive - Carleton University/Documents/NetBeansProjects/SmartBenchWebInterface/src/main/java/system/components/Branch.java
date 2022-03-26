package system.components;

/**
 *
 * @author aorogat
 */
public class Branch {
    String s;
    String o;
    String p;
    String s_type;
    String o_type; 

    public Branch(String s, String o, String p, String s_type, String o_type) {
        this.s = s;
        this.o = o;
        this.p = p;
        this.s_type = s_type;
        this.o_type = o_type;
    }
    
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(obj instanceof Branch)
        {
            Branch temp = (Branch) obj;
            if(this.s.equals(temp.s) && 
                    this.p.equals(temp.p) && 
                    this.o.equals(temp.o) && 
                    this.s_type.equals(temp.s_type) && 
                    this.o_type.equals(temp.o_type)
                    )
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        
        return (this.s.hashCode() + this.p.hashCode() + this.o.hashCode() + this.s_type.hashCode() + this.o_type.hashCode());        
    }
    
}
