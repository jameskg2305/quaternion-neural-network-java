
public class errorGradient {
	static quaternions[][][]weights = null;
	static hiddenNode[][][] hidden = null;
	static quaternions product(int sample, int time, int m, int inputs, int r){
		quaternions product = new quaternions(0,0,0,0);//time = 0, 1
		//System.out.println(time+" jjjj "+m);
		if(time==m){
			product = hidden[sample][time+(1-inputs)][r].errorT;
			
			return product;
		}
		int t = time;
		t+=(1-inputs);
		for(;t>m;t--){
			//System.out.println(t);
			quaternions total = new quaternions(0,0,0,0);
			for(int i=1;i<weights[t].length;i++){//nodes 1,2
				quaternions q = new quaternions(0,0,0,0);
				for(int j=0;j<weights[t][i].length-1;j++){//0,1
					
					quaternions conjugate = quaternions.conj(weights[t][i][j]);
					if(t==(time)+(1-inputs)){
						conjugate = quaternions.multi(quaternions.multideriv(hidden[sample][t][j].errorT, qrnn.qtanhderiv(hidden[sample][t-1][i-1].q)),conjugate);
					}else{
						conjugate = quaternions.multi(quaternions.multideriv(hidden[sample][t][j].error, qrnn.qtanhderiv(hidden[sample][t-1][i-1].q)),conjugate);
					}
					
					q = quaternions.add(q, conjugate);
					
				}
				hidden[sample][t-1][i-1].error = q;
			}
			
		}
		product = hidden[sample][m][r].error;
		
		return product;
	}
}
