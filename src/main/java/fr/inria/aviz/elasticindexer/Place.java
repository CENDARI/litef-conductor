package fr.inria.aviz.elasticindexer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.inria.aviz.elasticindexer.utils.PlaceParser;


/**
 * Description for a Place
 */
@JsonInclude(Include.NON_NULL)
public class Place {
    /** Full text name */
    public String name;
    /** Location as in elasticsearch */
    public String location;
    
    /**
     * Default constructor.
     */
    public Place() { }
    /**
     * Creates a Place with only a name.
     * @param name the name
     */
    public Place(String name) {
        this.name = name;
    }
    
    /**
     * Creates a Place with a name and a location.
     * @param name the name
     * @param location the location
     */
    public Place(String name, String location) {
        this.name = name;
        this.location = location;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object that) {
        if ( this == that ) return true;
        if ( !(that instanceof Place) ) return false;
        Place p = (Place)that;
        return ((this.name == p.name) || (this.name != null && this.name.equals(p.name))) &&
                ((this.location == p.location) || (this.location != null && this.location.equals(p.location)));
    }
    
    /**
     * Resolve the place location according to the place name
     * @return the resolved place or null
     */
    public String resolve()  {
        if (name == null) return null;
        if (location != null) return location;
        String loc = PlaceParser.resolvePlace(name);
        if (loc != null)
            location = loc;
        return location;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31*result + (name !=null ? name.hashCode() : 0);
        result = 31*result + (location  !=null ? location.hashCode() : 0);
       
        return result;
    }
}
