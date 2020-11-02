import java.io.FileNotFoundException;
import java.util.Random;

public class qrnn {
	static int hiddenNodes_inputnode=16;
	static int hiddenNodesPerLayer=15;
	static int timeSteps=10;
	static quaternions[][][] weights_ = new quaternions[timeSteps+1][hiddenNodes_inputnode][hiddenNodes_inputnode];
	static quaternions[][][] lastDelta = new quaternions[timeSteps+1][hiddenNodes_inputnode][hiddenNodes_inputnode];
	static rawInput[] rawInputs = new rawInput[300];
	static quaternions[] inputs = null;
	static trainingSet[] trainingSets = new trainingSet[2*16];
	static int n = 0;
	static int iterations = 1;
	static int sampleSize=8;
	static int testBatches=2;
	static float p = (float) 1;
	static Random random = new Random(1);
	static int batches = (trainingSets.length/sampleSize);
	static float lambda = (float) 0.01;
	static float momentum = (float) 0.9;
	static int epochs = 100000;
	static hiddenNode[][][] hidden_ = new hiddenNode[sampleSize][timeSteps][hiddenNodesPerLayer];
	static bias[][] biases_ = new bias[timeSteps][hiddenNodesPerLayer];
	//0.2744665 0.07375506 0.11645821 0.09116442  at h=8     
	//0.26142198 0.05593174 0.3133991 0.086672045 at h=13
	//0.11847838 0.07375505 0.015118597 0.11495207 at h=8 with 10 timesteps samplesize = 4
	//0.61910313 0.12979972 0.22228256 0.042854786 at h=12 with 7 timesteps samplesize = 2
	
	// Training error at epoch 1100
	//0.3102204 0.079500794 0.0777867 0.2006539
	//Testing error at epoch 1100
	//1.0714083 2.1537697 0.3938006 0.1761409
	
	//Training error at epoch 1000
	//0.02012876 0.03700051 0.042249523 0.05986277
	//Testing error at epoch 1000
	//0.59413725 0.19241688 0.277022 0.24258934
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		initialise.initWeights();
		createBiases();
		initialise.readCsv();
		initialise.normaliseInputs();

		trainingSet.createSets();
		errorGradient.hidden = hidden_;
		errorGradient.weights = weights_;
		for(int batch=0;batch<batches;batch++){
			for(int s=0;s<sampleSize;s++){
				trainingSets[batch*sampleSize+s].print(false);
			}
		}
		for(int e=0;e<epochs;e++){
			for(int batch=0;batch<batches-testBatches;batch++){
				for(int iter=0;iter<iterations;iter++){
					outputNode output[][] = new outputNode[sampleSize][timeSteps];
					for(int i=batch*sampleSize;i<batch*sampleSize+sampleSize;i++){
						output[i-(batch*sampleSize)] = forwardPropagation.forwardProp(i, batch, weights_, biases_, hidden_,false);
					}
					quaternions[] totalError = new quaternions[timeSteps];
					for(int i=0;i<timeSteps;i++){
						totalError[i] = new quaternions(0,0,0,0);
						for(int j=0;j<output.length;j++){
							quaternions error = quaternions.subSquare(output[j][i].q, trainingSets[j+(batch*sampleSize)].q[i+1]);
							error = quaternions.sqrt(error);
							//error = new quaternions(0, 0, 0,error.w);
							totalError[i] = quaternions.add(totalError[i], error);
						}
						totalError[i] = quaternions.divide(totalError[i], sampleSize);
						
					}
					
					backprop(totalError, output, batch, weights_, biases_, hidden_);
				}
			}
			if(e % 10==0){
				testTraining(e);
				testTesting(e);
			}

		
			
		}
		
	}
	static void testTraining(int e){
		quaternions q = new quaternions(0,0,0,0);
		for(int i=0;i<batches-testBatches;i++){
			for(int j=0;j<sampleSize;j++){
			//System.out.println("********f******* " +i+" "+j);
			outputNode output[] = new outputNode[timeSteps];
			output = forwardPropagation.forwardProp(i*sampleSize+j, i, weights_, biases_, hidden_,true);

			//System.out.println("errors are");
			for(int h=0;h<timeSteps;h++){
				
				//output[h].q.print();
				//trainingSets[(i*sampleSize)+j].q[h+1].print();
				quaternions error = quaternions.subSquare(output[h].q, trainingSets[(i*sampleSize)+j].q[h+1]);
				error = quaternions.sqrt(error);
				//error.print();
				if(h==timeSteps-1){
					q = quaternions.add(q, error);
				}
			}
			}
		}
		q = quaternions.divide(q, (batches-testBatches)*sampleSize);
		System.out.println("Training error at epoch "+e);
		q.print();
	}
	static void testTesting(int e){
		quaternions q = new quaternions(0,0,0,0);
		int[] correct= new int[]{0,0,0};
		for(int i=batches-testBatches;i<batches;i++){
			for(int j=0;j<sampleSize;j++){
			//System.out.println("********f******* " +i+" "+j);
			outputNode output[] = new outputNode[timeSteps];
			output = forwardPropagation.forwardProp(i*sampleSize+j, i, weights_, biases_, hidden_, true);

			//System.out.println("errors are");
			for(int h=0;h<timeSteps;h++){
				
				//output[h].q.print();
				//trainingSets[i*sampleSize].q[h+1].print();
				quaternions error = quaternions.subSquare(output[h].q, trainingSets[(i*sampleSize)+j].q[h+1]);
				error = quaternions.sqrt(error);
				//error.print();
				if(h==timeSteps-1){
					for(int t=1;t<4;t++){
						if(Math.abs(quaternions.sub(output[h].q, trainingSets[(i*sampleSize)+j].q[h+1]).w)<(0.1*t)){
							correct[t-1]++;
						}
					}
					q = quaternions.add(q, error);
				}
			}
			}
		}
		q = quaternions.divide(q, testBatches*sampleSize);
		System.out.println("Testing error at epoch "+e);
		q.print();
		System.out.println("Testing accuracy at epoch "+" "+((double)correct[0]/(double)(testBatches*sampleSize))+" "+" "+((double)correct[1]/(double)(testBatches*sampleSize))+" "+" "+((double)correct[2]/(double)(testBatches*sampleSize))+" ");
	}
	static quaternions clipping(quaternions mse){
		quaternions q = new quaternions(mse.x,mse.y,mse.z,mse.w);
		if(q.x<-0.99){
			q.x=(float) -0.99;
		}
		if(q.y<-0.99){
			q.y=(float)-0.99;
		}
		if(q.z<-0.99){
			q.z=(float)-0.99;
		}
		if(q.w<-0.99){
			q.w=(float)-0.99;
		}
		if(q.x>0.99){
			q.x=(float)0.99;
		}
		if(q.y>0.99){
			q.y=(float)0.99;
		}
		if(q.z>0.99){
			q.z=(float)0.99;
		}
		if(q.w>0.99){
			q.w=(float)0.99;
		}
		return q;
		
	}
	static quaternions clipGrad(quaternions mse){
		quaternions q = new quaternions(mse.x,mse.y,mse.z,mse.w);
		while(Math.abs(q.x)>1||Math.abs(q.y)>1||Math.abs(q.z)>1||Math.abs(q.w)>1){
			q.x/=10;
			q.y/=10;
			q.z/=10;
			q.w/=10;
		}
		
		return q;
		
	}
	private static void backprop(quaternions[] mse, outputNode[][] output, int batch, quaternions[][][]weights, bias[][] biases, hiddenNode[][][] hidden) {
		// TODO Auto-generated method stub
		//System.out.println("BACKPROPAGATION ");
		//outer weights

		for(int sample=0;sample<sampleSize;sample++){
			//OUTER LAYER CHANGES
			// delta = lambda(error * hiddenconjugate)
			for(int t=0;t<timeSteps;t++){//0,1,2
				for(int i=0;i<hiddenNodesPerLayer;i++){//0,1
					quaternions delta = new quaternions(0,0,0,0);
					quaternions conjugate = quaternions.conj(hidden[sample][t][i].q);
					quaternions error = quaternions.sub(output[sample][t].q, trainingSets[sample+(batch*sampleSize)].q[t+1]);//mse[t];//
					//error = new quaternions(0, 0, 0,error.w);
					delta = quaternions.multi(error, conjugate
							);
					delta = quaternions.simpleMult(delta, lambda);
					if(t!=timeSteps-1){
						weights[t+1][i+1][hiddenNodesPerLayer] = quaternions.sub(weights[t+1][i+1][hiddenNodesPerLayer], delta);
					}else{
						weights[t+1][i][0] = quaternions.sub(weights[t+1][i][0], delta);
					}
				}
			}

			//error rates
			//error rate at T    =    Wy* x (p-y) x tanh'(y)
			for(int t=0;t<timeSteps;t++){//0,1,2
				
				for(int i=0;i<hiddenNodesPerLayer;i++){//0,1
					quaternions total = new quaternions(0,0,0,0);
					quaternions conjugate = null;
					if(t!=timeSteps-1){
						conjugate = quaternions.conj(weights[t+1][i+1][hiddenNodesPerLayer]);
					}else{
						conjugate = quaternions.conj(weights[t+1][i][0]);
					}
					
					conjugate = quaternions.multi(quaternions.multideriv(mse[t], qtanhderiv(output[sample][t].q)), conjugate);
					//conjugate = quaternions.multideriv(conjugate, qtanhderiv(output[sample][t].q));
					total = quaternions.add(total, conjugate);
					hidden[sample][t][i].errorT = total;
				}
				
				//System.out.println("errorT "+t);
				//total.print();
			}













			//find input deltas
			for(int t=0;t<timeSteps;t++){//each input timestep    0 1 2
				//System.out.println("££££££££££££££££");
				for(int r=0;r<weights[t][0].length;r++){//each input weight
					quaternions total = new quaternions(0,0,0,0);
					int m = 0;
					m=t;
					if(t>5){
						//m=t-6;
					}
					for(;m<t+1;m++){//summmation from 0 to t
						quaternions product = errorGradient.product(sample, t, m,1, r);//product from m to t
						product = quaternions.multi(product,quaternions.conj(trainingSets[sample+(batch*sampleSize)].q[t]));
						//System.out.println(t);
						//product.print();
						product = clipGrad(product);
						total = quaternions.add(total, product);
					}
					
					total = quaternions.simpleMult(total, lambda);
					//total = clipGrad(total);
					if(lastDelta[t][0][r]!=null){
					total = quaternions.add(total, quaternions.simpleMult(lastDelta[t][0][r],momentum));
					}
					//total = clipGrad(total);
					weights[t][0][r] = quaternions.sub(weights[t][0][r], total);
					lastDelta[t][0][r] = total;
				}

				//wi =
			}



			//System.out.println("%%%%%%%%%%%%%5");


			//HIDDEN BACKPROP
			for(int t=0;t<timeSteps-1;t++){//each timestep    0,1
				//System.out.println("$$$$$$$$$$$$$$ "+t);
				for(int e=0;e<hidden[sample][t].length;e++){//hidden node number  0,1
					
					quaternions total = new quaternions(0,0,0,0);
					int m = 0;
					m=t;
					if(t>5){
						//m=t-6;
					}
					for(;m<t+1;m++){//summmation from 0 to t

						quaternions product = errorGradient.product(sample, t, m, 0, e);//product from m to t
						product = quaternions.multi(product,quaternions.conj(hidden[sample][t][e].q));
						product = clipGrad(product);
						total = quaternions.add(total, product);
					}
					total = quaternions.simpleMult(total, lambda);
					//total = clipGrad(total);
					for(int r=0;r<weights[t+1][e+1].length-1;r++){//hidden node weight
						if(lastDelta[t+1][e+1][r]!=null){
						total = quaternions.add(total, quaternions.simpleMult(lastDelta[t+1][e+1][r],momentum));
						}
						//total = clipGrad(total);
						weights[t+1][e+1][r] = quaternions.sub(weights[t+1][e+1][r], total);
						lastDelta[t+1][e+1][r] = total;
					}
				}
				//wi =
			}



			//System.out.println("dfddfdfdfdfdf");












			//find bias deltas
			for(int t=0;t<timeSteps-1;t++){//each timestep    0,1
				for(int e=0;e<hidden[sample][t].length;e++){//hidden node number  0,1
					quaternions total = new quaternions(0,0,0,0);
					int m = 0;
					m=t;
					if(t>5){
						//m=t-6;
					}
					for(;m<t+1;m++){//t+1;m++){//summmation from 0 to t

						quaternions product = errorGradient.product(sample, t, m, 0, e);//product from m to t
						product = clipGrad(product);
						total = quaternions.add(total, product);
					}
					total = quaternions.simpleMult(total, lambda);
					//total = clipGrad(total);
					//System.out.println(t+" "+e+" "+r);
					biases[t][e].q = quaternions.sub(biases[t][e].q, total);
				}
				//wi =
			}


















			//System.out.println("***************");
		}
	}
	



	
	 static quaternions qrelu(quaternions total) {
		// TODO Auto-generated method stub
		quaternions q = new quaternions(relu(total.x),relu(total.y),relu(total.z),relu(total.w));
		return q;
	}

	 static float relu(float x) {
		// TODO Auto-generated method stub
		if(x<0){
			return 0;
		}
		return x;
	}

	 static quaternions qtanhderiv(quaternions newQ) {
		// TODO Auto-generated method stub

		quaternions q = new quaternions(tanhderiv(newQ.x),tanhderiv(newQ.y),tanhderiv(newQ.z),tanhderiv(newQ.w));
		return q;
	}
	 static float tanhderiv(float x) {
		// TODO Auto-generated method stub
		float z = (float) (1 - Math.pow(Math.tanh(x), 2)); 
		return z;
	}
	 static quaternions qtanh(quaternions newQ) {
		// TODO Auto-generated method stub

		quaternions q = new quaternions(tanh(newQ.x),tanh(newQ.y),tanh(newQ.z),tanh(newQ.w));
		return q;
	}
	 static float tanh(float x) {
		// TODO Auto-generated method stub
		float z = (float) Math.tanh(x);
		return z;
	}




	 static void createBiases() {
		// TODO Auto-generated method stub
		for(int i=0;i<biases_.length;i++){
			for(int j=0;j<biases_[0].length;j++){
				biases_[i][j] = new bias();
			}
		}
	}




}
