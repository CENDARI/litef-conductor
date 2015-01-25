package fr.inria.aviz.elasticindexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.inria.aviz.elasticindexer.utils.DateParser;
import fr.inria.aviz.elasticindexer.utils.LangCleaner;



/**
 * Class DocumentInfo
 * 
 * @author Jean-Daniel Fekete
 */
public class DocumentInfo extends HashMap<String , Object> {
//    private String application;
//    private String[] artifact;
//    private Person[] contributor;
//    private Person[] creator;
//    private String[] date;
//    private String[] event;
//    private String format;
//    private String[] language;
//    private String[] org;
//    private Person[] person;
//    private Place[] place;
//    private String[] publisher;
//    private String[] ref;
//    private String[] tag;
//    private String text;
//    private String[] title;
//    private String uri;
//    private String[] groups_allowed;
//    private String[] users_allowed;
    /** All the field names */
    @JsonIgnore
    public final static String[] FIELDS = {
        "application",
        "artifact",
        "contributor",
        "creator",
        "date",
        "event",
        "format",
        "language",
        "org",
        "person",
        "place",
        "publisher",
        "ref",
        "tag",
        "text",
        "title",
        "uri",
        "groups_allowed",
        "users_allowed",
    };
    /** Set of fields */
    @JsonIgnore
    public final static Set<String> FIELDS_SET = new HashSet<>(Arrays.asList(FIELDS));
    
    /**
     * @return the application
     */
    public String getApplication() {
        return (String)this.get("application");
    }
    /**
     * @param application the application to set
     */
    public void setApplication(String application) {
        this.put("application", application);
    }
    /**
     * @return the artifact
     */
    public String[] getArtifact() {
        return (String[])this.get("artifact");
    }
    /**
     * @param artifact the artifact to set
     */
    public void setArtifact(String... artifact) {
        this.put("artifact", artifact);
    }
    /**
     * @return the contributor
     */
    public Person[] getContributor() {
        return (Person[])this.get("contributor");
    }
    /**
     * @param name the contributor names to set
     */
    public void setContributorName(String... name) {
        Person[] person = new Person[name.length];
        for (int i = 0; i < name.length; i++) {
            person[i] = new Person(name[i]);
        }
        setContributor(person);
    }

    /**
     * @param contributor the contributor to set
     */
    public void setContributor(Person... contributor) {
        this.put("contributor", contributor);
    }
    /**
     * @return the creator
     */
    public Person[] getCreator() {
        return (Person[])this.get("creator");
    }
    /**
     * @param creator the creator to set
     */
    public void setCreator(Person... creator) {
        this.put("creator", creator);
    }
    
    /**
     * @param name the creator names to set
     */
    public void setCreatorName(String... name) {
        Person[] person = new Person[name.length];
        for (int i = 0; i < name.length; i++) {
            person[i] = new Person(name[i]);
        }
        setCreator(person);
    }
    
    /**
     * @return the date
     */
    public String[] getDate() {
        return (String[])this.get("date");
    }
    
    
    /**
     * Check date format and fix or set to null
     * @param dates
     * @return a table of dates
     */
    public String[] checkDates(String[] dates) {
        int nullCnt = 0;
        for (int i = 0; i < dates.length; i++) {
            String d = DateParser.checkDate(dates[i]);
            if (d == null) {
                nullCnt++;
            }
            dates[i] = d;
        }
        if (nullCnt!= 0) {
            nullCnt = dates.length - nullCnt;
            if (nullCnt == 0)
                return null;
            String[] ret = new String[nullCnt];
            int j = 0;
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null)
                    ret[j++] = dates[i];
            }
            return ret;
        }
        return dates;
    }
    /**
     * @param date the date to set
     */
    public void setDate(String... date) {
        date = checkDates(date);
        this.put("date", date);
    }
    
    /**
     * Adds dates
     * @param date
     */
    public void addDate(String... date) {
        if (this.get("date") == null) {
            setDate(date);
        }
        else {   
            ArrayList<String> buf = new ArrayList<>(
                    Arrays.asList((String[])this.get("date")));
            buf.addAll(Arrays.asList(date));
            String[] newdate = new String[buf.size()];
            buf.toArray(newdate);
            setDate(newdate);
        }
    }
    
    /**
     * @param date the date to set
     */
    public void setDateJavaDate(Date... date) {
        String[] res = new String[date.length];
        for (int i = 0; i < date.length; i++) {
            res[i] = DateParser.format(date[i]);
        }
        setDate(res);
    }
    
    /**
     * @param date the date to set
     */
    public void setDateJodaDate(DateTime... date) {
        String[] res = new String[date.length];
        for (int i = 0; i < date.length; i++) {
            res[i] = DateParser.format(date[i]);
        }
        setDate(res);
    }


    /**
     * @return the event
     */
    public String[] getEvent() {
        return (String[])this.get("event");
    }
    /**
     * @param event the event to set
     */
    public void setEvent(String... event) {
        this.put("event", event);
    }
    /**
     * @return the format
     */
    public String getFormat() {
        return (String)this.get("format");
    }
    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.put("format", format);
    }
    /**
     * @return the language
     */
    public String[] getLanguage() {
        return (String[])this.get("language");
    }
    
    /**
     * Check language formats and fix or set to null
     * @param langs the languages
     * @return a table of valid languages
     */
    public String[] checkLanguages(String[] langs) {
        int nullCnt = 0;
        for (int i = 0; i < langs.length; i++) {
            String d = LangCleaner.cleanLanguage(langs[i]);
            if (d == null) {
                nullCnt++;
            }
            langs[i] = d;
        }
        if (nullCnt!= 0) {
            nullCnt = langs.length - nullCnt;
            if (nullCnt == 0)
                return null;
            String[] ret = new String[nullCnt];
            int j = 0;
            for (int i = 0; i < langs.length; i++) {
                if (langs[i] != null)
                    ret[j++] = langs[i];
            }
            return ret;
        }
        return langs;
    }
    
    /**
     * @param language the language to set
     */
    public void setLanguage(String... language) {
        this.put("language", checkLanguages(language));
    }
    /**
     * @return the org
     */
    public String[] getOrg() {
        return (String[])this.get("org");
    }
    /**
     * @param org the org to set
     */
    public void setOrg(String... org) {
        this.put("org", org);
    }
    /**
     * @return the person
     */
    public Person[] getPerson() {
        return (Person[])this.get("person");
    }
    /**
     * @param person the person to set
     */
    public void setPerson(Person ... person) {
        this.put("person", person);
    }
    
    /**
     * @param name the person names to set
     */
    public void setPersonName(String... name) {
        Person[] person = new Person[name.length];
        for (int i = 0; i < name.length; i++) {
            person[i] = new Person(name[i]);
        }
        setPerson(person);
    }
    /**
     * @return the place
     */
    public Place[] getPlace() {
        return (Place[]) this.get("place");
    }
    
    /**
     * @param name the place names to set
     */
    public void setPlaceName(String ... name) {
        Place[] place= new Place[name.length];
        for (int i = 0; i < name.length; i++) {
            place[i] = new Place(name[i]);
        }
        setPlace(place);
    }
    /**
     * @param place the place to set
     */
    public void setPlace(Place ... place) {
        this.put("place", place);
    }
    /**
     * @return the publisher
     */
    public String[] getPublisher() {
        return (String[]) this.get("publisher");
    }
    /**
     * @param publisher the publisher to set
     */
    public void setPublisher(String... publisher) {
        this.put("publisher", publisher);
    }
    /**
     * @return the ref
     */
    public String[] getRef() {
        return (String[])this.get("ref");
    }
    /**
     * @param ref the ref to set
     */
    public void setRef(String ... ref) {
        this.put("ref", ref);
    }
    /**
     * @return the tag
     */
    public String[] getTag() {
        return (String[])this.get("tag");
    }
    /**
     * @param tag the tag to set
     */
    public void setTag(String ... tag) {
        this.put("tag", tag);
    }
    /**
     * @return the text
     */
    public String getText() {
        return (String)this.get("text");
    }
    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.put("text", text);
    }
    /**
     * @return the title
     */
    public String[] getTitle() {
        return (String[])this.get("title");
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String... title) {
        this.put("title", title);
    }
    /**
     * @return the uri
     */
    public String getUri() {
        return (String)this.get("uri");
    }
    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.put("uri", uri);
    }
    /**
     * @return the groups_allowed
     */
    public String[] getGroups_allowed() {
        return (String[])this.get("groups_allowed");
    }
    /**
     * @param groups_allowed the groups_allowed to set
     */
    public void setGroups_allowed(String... groups_allowed) {
        this.put("groups_allowed", groups_allowed);
    }
    /**
     * @return the users_allowed
     */
    public String[] getUsers_allowed() {
        return (String[])this.get("users_allowed");
    }
    /**
     * @param users_allowed the users_allowed to set
     */
    public void setUsers_allowed(String... users_allowed) {
        this.put("users_allowed", users_allowed);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        return super.get(key);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object put(String key, Object value) {
        return super.put(key, value);
    }

    /**
     * Creates a DocumentInfo from its JSON representation.
     * @param json the json string
     * @param mapper the ObjectMapper
     * @return a DocumentInfo 
     * @throws JsonParseException if the json is invalid
     * @throws JsonMappingException if the mapping does not work
     * @throws IOException 
     */
    public static DocumentInfo fromJSON(String json, ObjectMapper mapper) 
            throws JsonParseException, JsonMappingException, IOException {
        //mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        DocumentInfo info = mapper.readValue(json, DocumentInfo.class);
        return info;
    }
    
    /**
     * Converts a DocumentInfo to JSON representation.
     * @param mapper the ObjectMapper
     * @return a JSON string
     * @throws JsonProcessingException if conversion fails
     */
    public String toJSON(ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object that) {
        if ( this == that) return true;
        if ( !(that instanceof DocumentInfo) ) return false;
        return super.equals(that);
    }
}
