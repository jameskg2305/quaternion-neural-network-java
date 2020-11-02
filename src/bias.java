import java.util.Random;

public class bias {
	quaternions q;
	public bias(){
		Random r = new Random(1);
		this.q = new quaternions(
				(float)(r.nextFloat()-0.5)/(float)1,
				(float)(r.nextFloat()-0.5)/(float)1,
				(float)(r.nextFloat()-0.5)/(float)1,
				(float)(r.nextFloat()-0.5)/(float)1);
	}
}
