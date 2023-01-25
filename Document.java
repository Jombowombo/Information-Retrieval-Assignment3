/**
 * This is a helper class for Asn3Java.java that holds document data 
 * 	and functions to return said data
 * 
 * @author Evan Brown
 *
 */
public class Document {
    String title;
    String body;

    public Document(){
        this.title = "";
        this.body = "";
    }

    public Document(String title, String body){
        this.title = title;
        this.body = body;
    }

    public double termFreqTitle(String term) {
    	double freq = 0;
    	String[] split = this.title.split("\\s+");
    	for (int i = 0; i < split.length; i++) {
    		if (term.equals(split[i])) {
    			freq++;
    		}
    	}
    	
    	return (freq / split.length);
    }
    
    public double termFreqBody(String term) {
    	double freq = 0;
    	String[] split = this.body.split("\\s+");
    	for (int i = 0; i < split.length; i++) {
    		if (term.equals(split[i])) {
    			freq++;
    		}
    	}
    	
    	return (freq / split.length);
    }
    
    public boolean hasTermTitle(String term) {
    	String[] split = this.title.split("\\s+");
    	for (int i = 0; i < split.length; i++) {
    		if (term.equals(split[i])) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean hasTermBody(String term) {
    	String[] split = this.body.split("\\s+");
    	for (int i = 0; i < split.length; i++) {
    		if (term.equals(split[i])) {
    			return true;
    		}
    	}
    	return false;
    }
    

    public void setTitle(String title){
        this.title = title;
    }

    public void setBody(String body){
        this.body = body;
    }
    
    public void updateTitle(String title) {
    	this.title = this.title.concat(title + " ");
    }

    public void updateBody(String body){
        this.body = this.body.concat(body + " ");
    }

    public String getTitle(){
        return this.title;
    }

    public String getBody(){
        return this.body;
    }

    @Override 
    public String toString() {
    	return "Title: " + this.title +
    			"\nBody: " + this.body;
    }
}
