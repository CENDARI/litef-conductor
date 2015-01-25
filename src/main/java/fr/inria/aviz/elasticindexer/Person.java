package fr.inria.aviz.elasticindexer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * Description for a Person
 */
@JsonInclude(Include.NON_NULL)
public class Person {
    /** full text name */
    public String name;
    /** optional email */
    public String email;
    
    /**
     * Default constructor. 
     */
    public Person() {
    }

    /**
     * Creates a Person with only a name.
     * @param name the name
     */
    public Person(String name) { 
        this.name = name; 
    }
    
    /**
     * Creates a Person.
     * @param name the name
     * @param email the email
     */
    public Person(String name, String email) { 
        this.name = name;
        this.email = email;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object that) {
        if ( this == that ) return true;
        if ( !(that instanceof Person) ) return false;
        Person p = (Person)that;
        return ((this.name == p.name) || (this.name != null && this.name.equals(p.name))) &&
                ((this.email == p.email) || (this.email!= null && this.email.equals(p.email)));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31*result + (name !=null ? name.hashCode() : 0);
        result = 31*result + (email !=null ? email.hashCode() : 0);
       
        return result;
    }
}