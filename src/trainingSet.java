import java.util.Random;

public class trainingSet {
	quaternions[] q = null;
	int startIndex=0;
	public static void createSets() {
		// TODO Auto-generated method stub
		Random random = new Random(2);
		for(int batch=0;batch<qrnn.batches;batch++){
			
				int start = (int) batch *(qrnn.timeSteps*qrnn.sampleSize); //(random.nextDouble()*(qrnn.inputs.length-(qrnn.timeSteps*qrnn.sampleSize)));
				if(batch == qrnn.batches-1){
					start = 236-(qrnn.sampleSize*qrnn.timeSteps);
				}
				if(batch == qrnn.batches-2){
					start = 236-(qrnn.sampleSize*qrnn.timeSteps)-1;
				}
				for(int i=0;i<qrnn.sampleSize;i++){
					
					qrnn.trainingSets[batch*qrnn.sampleSize+i] = new trainingSet();
					qrnn.trainingSets[batch*qrnn.sampleSize+i].q = new quaternions[qrnn.timeSteps+1];
			//System.out.println("****");
			
					qrnn.trainingSets[batch*qrnn.sampleSize+i].startIndex = start + (i*qrnn.timeSteps);
				for(int j=0;j<qrnn.trainingSets[batch*qrnn.sampleSize+i].q.length;j++){
					qrnn.trainingSets[batch*qrnn.sampleSize+i].q[j] = qrnn.inputs[start+ (i*qrnn.timeSteps)+j];
				}
			}
		}
		for(int i=0;i<qrnn.batches;i++){
			float totalprice = 0;
			float totalsent = 0;
			float totalvix = 0;
			for(int j=0;j<qrnn.sampleSize;j++){
				for(int r=0;r<qrnn.timeSteps+1;r++){
					totalsent +=qrnn.trainingSets[i*qrnn.sampleSize+j].q[r].x;
					totalprice+=qrnn.trainingSets[i*qrnn.sampleSize+j].q[r].y;
					totalvix  +=qrnn.trainingSets[i*qrnn.sampleSize+j].q[r].z;
				}
				//for(quaternions r : qrnn.trainingSets[j+(i*qrnn.sampleSize)].q){
					//r.print();
				//}
			}
			float meanprice = totalprice/(qrnn.sampleSize*(qrnn.timeSteps+1));
			float meansent  = totalsent/(qrnn.sampleSize*(qrnn.timeSteps+1));
			float meanvix   = totalvix/(qrnn.sampleSize*(qrnn.timeSteps+1));
			//System.out.println(meanprice);
			float totalx_mean[] = new float[]{0,0,0};
			for(int j=0;j<qrnn.sampleSize;j++){
				for(int r=0;r<qrnn.timeSteps+1;r++){
					totalx_mean[0]  += Math.pow(qrnn.trainingSets[i*qrnn.sampleSize+j].q[r].x - meansent, 2);
					totalx_mean[1]  += Math.pow(qrnn.trainingSets[i*qrnn.sampleSize+j].q[r].y - meanprice, 2);
					totalx_mean[2]  += Math.pow(qrnn.trainingSets[i*qrnn.sampleSize+j].q[r].z - meanvix, 2);
					//System.out.println(" "+r.sentiment+" "+r.price+" "+r.vix);
				}
			}
			float var[] = new float[3];
			var[0] = totalx_mean[0]/(qrnn.sampleSize*(qrnn.timeSteps+1));
			var[1] = totalx_mean[1]/(qrnn.sampleSize*(qrnn.timeSteps+1));
			var[2] = totalx_mean[2]/(qrnn.sampleSize*(qrnn.timeSteps+1));


			for(int j=0;j<qrnn.sampleSize;j++){
				for(int k=0;k<qrnn.timeSteps+1;k++){
					qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k] = new quaternions(
							(qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k].x - meansent) / (float)Math.sqrt(var[0]),
							0,
							(qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k].z - meanvix)  / (float)Math.sqrt(var[2]),
							(qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k].y - meanprice)/ (float)Math.sqrt(var[1]));
					//qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k].x*=0.5;
					//qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k].y*=0.5;
					//qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k].z*=0.5;
					//qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k].w*=0.5;
					
					qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k] = qrnn.clipping(qrnn.trainingSets[(i*qrnn.sampleSize)+j].q[k]);
				}
			}
		}


	}

	void print(boolean showoutputs){
		int i=0;
		if(showoutputs==false){
			System.out.println("index start at "+startIndex);
		}else{
			i=1;
		}
		for(; i<this.q.length;i++){
			//q[i].print();
		}

	}
}
