
public class forwardPropagation {
	 static outputNode[] forwardProp(int sample, int batch, quaternions[][][]weights, bias[][] biases, hiddenNode[][][] hidden, boolean test) {
		// TODO Auto-generated method stub
		//System.out.println("FORWARD PROP");
		outputNode[] output = new outputNode[qrnn.timeSteps];
		for(int t=0;t<qrnn.timeSteps+1;t++){//0,1,2,3
			//System.out.println("input = "+t+" ");
			//inputs[t].print();
			if(t==0){
				quaternions newQ = new quaternions(0,0,0,0);
				for(int i=0;i<qrnn.hiddenNodesPerLayer;i++){
					newQ = quaternions.multi(weights[0][0][i], qrnn.trainingSets[sample].q[t]);
					
					newQ = quaternions.add(newQ, biases[0][i].q);
					hidden[sample-(batch*qrnn.sampleSize)][0][i] = new hiddenNode(qrnn.qtanh(newQ), newQ);
					if(test==true){
						//hidden[sample-(batch*qrnn.sampleSize)][0][i].q = quaternions.simpleMult(hidden[sample-(batch*qrnn.sampleSize)][0][i].q, qrnn.p);
					}
				}
			}else if(t==qrnn.timeSteps){
				quaternions total = new quaternions(0,0,0,0);
				for(int i=0;i<qrnn.hiddenNodesPerLayer;i++){

					quaternions wi    = new quaternions(0,0,0,0);
					wi = quaternions.multi(weights[t][i][0], hidden[sample-(batch*qrnn.sampleSize)][t-1][i].q);
					
					/*raining error at epoch 140
					0.6992576 0.56960267 0.7649722 0.3707408
					Testing error at epoch 140
					0.7862931 0.5887856 0.664657 0.32365805
					Testing accuracy at epoch 140 0.5 8*/
					
					total = quaternions.add(total, wi);
				}
				output[qrnn.timeSteps-1] = new outputNode(qrnn.qtanh(total));
				if(test==true){
					//output[qrnn.timeSteps-1].q = quaternions.simpleMult(output[qrnn.timeSteps-1].q, (float)qrnn.p);
				}
				return output;
			}
			else{//1,2
				for(int i=0;i<qrnn.hiddenNodesPerLayer;i++){//0
					quaternions total = new quaternions(0,0,0,0);
					quaternions wi    = new quaternions(0,0,0,0);
					wi = quaternions.multi(weights[t][0][i], qrnn.trainingSets[sample].q[t]);
					total = quaternions.add(total, wi);
					//total.print();

					for(int j=1;j<qrnn.hiddenNodes_inputnode;j++){//1
						quaternions newQ = new quaternions(0,0,0,0);
						
							newQ = quaternions.multi(weights[t][j][i], hidden[sample-(batch*qrnn.sampleSize)][t-1][i].q);
						
							//newQ = quaternions.multi(quaternions.simpleMult(weights[t][j][i], (float)qrnn.p), hidden[sample-(batch*qrnn.sampleSize)][t-1][i].q);
						
						total = quaternions.add(total, newQ);

					}

					total = quaternions.add(total, biases[t][i].q);
					hidden[sample-(batch*qrnn.sampleSize)][t][i] = new hiddenNode(qrnn.qtanh(total), total);
					if(test==true){
						hidden[sample-(batch*qrnn.sampleSize)][t][i].q = quaternions.simpleMult(hidden[sample-(batch*qrnn.sampleSize)][t][i].q, (float)qrnn.p);
					}
					if(test==false && qrnn.random.nextFloat()>qrnn.p){
						hidden[sample-(batch*qrnn.sampleSize)][t][i] = new hiddenNode(new quaternions(0,0,0,0), new quaternions(0,0,0,0));
					}//else{
					//	hidden[sample-(batch*qrnn.sampleSize)][t][i].q = quaternions.simpleMult(hidden[sample-(batch*qrnn.sampleSize)][t][i].q, (float)qrnn.p);
					//}
					
				}
				quaternions total = new quaternions(0,0,0,0);
				for(int i=1;i<qrnn.hiddenNodes_inputnode;i++){//1
					quaternions wi    = new quaternions(0,0,0,0);
					wi = quaternions.multi(weights[t][i][qrnn.hiddenNodesPerLayer], hidden[sample-(batch*qrnn.sampleSize)][t-1][i-1].q);
					if(test==true){
						//wi = quaternions.multi(quaternions.simpleMult(weights[t][i][qrnn.hiddenNodesPerLayer], (float)qrnn.p), hidden[sample-(batch*qrnn.sampleSize)][t-1][i-1].q);
					}
					total = quaternions.add(total, wi);
				}
				//total = quaternions.add(total, biases[3][0].q);
				output[t-1] = new outputNode(qrnn.qtanh(total));
				if(test==true){
					//output[t-1].q = quaternions.simpleMult(output[t-1].q, (float)qrnn.p);
				}
			}
		}
		return null;
	}
}
