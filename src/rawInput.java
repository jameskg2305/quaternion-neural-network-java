
public class rawInput {
	float sentiment = 0;
	float price = 0;
	float vix = 0;
	
	public rawInput(String sentiment, String price, String vix){
		this.sentiment = 	Float.parseFloat(sentiment);
		this.price 	   = 	Float.parseFloat(price);
		this.vix       = 	Float.parseFloat(vix);
	}
	
}
