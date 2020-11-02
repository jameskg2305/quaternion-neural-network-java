
public class quaternions {
	float x=0;
	float y=0;
	float z=0;
	float w=0;
	
	public quaternions(float x, float y, float z, float w){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public static quaternions multi(quaternions q, quaternions v){
		quaternions n = new quaternions(0,0,0,0);
		float qx = q.x;
		float qy = q.y;
		float qz = q.z;
		float qw = q.w;

		float vx = v.x;
		float vy = v.y;
		float vz = v.z;
		float vw = v.w;
		
		n.w = qw*vw - qx*vx - qy*vy - qz*vz;
		
		n.x = qw*vx + qx*vw + qy*vz - qz*vy;
		
		n.y = qw*vy - qx*vz + qy*vw + qz*vx;
		
		n.z = qw*vz + qx*vy - qy*vx + qz*vw;
		
		
		return n;
		
	}
	
public static quaternions conj(quaternions q){
	quaternions n = new quaternions(0,0,0,0);
	n.w = q.w;
	n.x = -q.x;
	n.y = -q.y;
	n.z = -q.z;
	
	return n;
	
}
	
public static quaternions add(quaternions q, quaternions v){
		quaternions n = new quaternions(0,0,0,0);
		float qx = q.x;
		float qy = q.y;
		float qz = q.z;
		float qw = q.w;

		float vx = v.x;
		float vy = v.y;
		float vz = v.z;
		float vw = v.w;
		
		n.w = qw+vw;
		
		n.x = qx+vx;
		
		n.y = qy+vy;
		
		n.z = qz+vz;
		return n;
		
		
	}
	public void print(){
		System.out.println(this.x+" "+this.y+" "+this.z+" "+this.w);
	}
	
public static quaternions sub(quaternions q, quaternions v){
		quaternions n = new quaternions(0,0,0,0);
		float qx = q.x;
		float qy = q.y;
		float qz = q.z;
		float qw = q.w;

		float vx = v.x;
		float vy = v.y;
		float vz = v.z;
		float vw = v.w;
		
		n.w = (qw-vw);
		
		n.x = (qx-vx);
		
		n.y = (qy-vy);
		
		n.z = (qz-vz);
		
		return n;
		
	}
public static quaternions subSquare(quaternions q, quaternions v){
		quaternions n = new quaternions(0,0,0,0);
		float qx = q.x;
		float qy = q.y;
		float qz = q.z;
		float qw = q.w;

		float vx = v.x;
		float vy = v.y;
		float vz = v.z;
		float vw = v.w;
		
		n.w = (float)Math.pow(qw-vw,2);
		
		n.x = (float)Math.pow(qx-vx,2);
		
		n.y = (float)Math.pow(qy-vy,2);
		
		n.z = (float)Math.pow(qz-vz,2);
		
		return n;
		
	}
public static quaternions sqrt(quaternions q){
	quaternions n = new quaternions(0,0,0,0);
	float qx = q.x;
	float qy = q.y;
	float qz = q.z;
	float qw = q.w;

	
	n.w = (float)Math.sqrt(qw);
	
	n.x = (float)Math.sqrt(qx);
	
	n.y = (float)Math.sqrt(qy);
	
	n.z = (float)Math.sqrt(qz);
	
	return n;
	
}
public static quaternions divide(quaternions q, int x){
	
	quaternions n = new quaternions(0,0,0,0);
	n.w=q.w/x;
	n.x=q.x/x;
	n.y=q.y/x;
	n.z=q.z/x;
	return n;
}

public static quaternions simpleMult(quaternions delta, float lambda) {
	// TODO Auto-generated method stub
	quaternions n = new quaternions(0,0,0,0);
	n.w = delta.w*lambda;
	n.x = delta.x*lambda;
	n.y = delta.y*lambda;
	n.z = delta.z*lambda;
	
	return n;
}

public static quaternions multideriv(quaternions q, quaternions deriv) {
	// TODO Auto-generated method stub
	quaternions n = new quaternions(0,0,0,0);
	//n.w = q.w*deriv.w;
	//n.x = q.x*deriv.x;
	//n.y = q.y*deriv.y;
	//n.z = q.z*deriv.z;
	
	n.w = q.w*deriv.w - q.x*deriv.x - q.y*deriv.y - q.z*deriv.z;
	
	n.x = q.w*deriv.x + q.x*deriv.w + q.y*deriv.z + q.z*deriv.y;
	
	n.y = q.w*deriv.y + q.x*deriv.z + q.y*deriv.w + q.z*deriv.x;
	
	n.z = q.w*deriv.z + q.x*deriv.y + q.y*deriv.x + q.z*deriv.w;
	
	
	return n;
}

}
