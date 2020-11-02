
public class hiddenNode {
	quaternions q = null;
	quaternions pre = null;
	quaternions error = null;
	quaternions errorT = null;
	public hiddenNode(quaternions q, quaternions pre){
		this.q = q;
		this.pre = pre;
	}
}
