import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class initialise {
	static void readCsv() throws FileNotFoundException{
		// TODO Auto-generated method stub
		int counter=0;
		try (Scanner scanner = new Scanner(new File("marketData.csv"));) {
			while (scanner.hasNextLine()) {
				//System.out.println(scanner.nextLine());
				String[] s = scanner.nextLine().split(",");
				if(s.length==6){
					//System.out.println(s[0]+" "+s[1]+" "+s[3]+" "+s[5]);
					qrnn.rawInputs[counter] = new rawInput(s[1], s[3], s[5]);
					//System.out.println(counter+" "+rawInputs[counter].sentiment+" "+rawInputs[counter].price+" "+rawInputs[counter].vix);
					counter++;
				}
				//records.add(getRecordFromLine(scanner.nextLine()));
			}
		}
		qrnn.n = counter;
		rawInput[] temp = new rawInput[qrnn.n];
		temp = Arrays.copyOfRange(qrnn.rawInputs, 0, qrnn.n);
		qrnn.rawInputs = temp;
	}
	static void initWeights() {
		// TODO Auto-generated method stub
		Random random = new Random(2);
		float sigma = (float) (1/Math.sqrt(4));

		System.out.println(sigma);


		for(int t=0;t<qrnn.timeSteps+1;t++){
			if(t==0){
				//qrnn.lastDelta_[t] = new quaternions[1][qrnn.hiddenNodes_inputnode-1];
				qrnn.weights_[t] = new quaternions[1][qrnn.hiddenNodes_inputnode-1];
			}
			else if(t==qrnn.timeSteps){
				//qrnn.lastDelta_[t] = new quaternions[qrnn.hiddenNodes_inputnode-1][1];
				qrnn.weights_[t] = new quaternions[qrnn.hiddenNodes_inputnode-1][1];
			}
			for(int i=0;i<qrnn.weights_[t].length;i++){
				if((t!=0 && t!=qrnn.timeSteps) && i==0){
					qrnn.weights_[t][i] = new quaternions[qrnn.hiddenNodes_inputnode-1];
				}
				for(int j=0;j<qrnn.weights_[t][i].length;j++){
					float theta = (float) ((random.nextFloat()-0.5)*2 * Math.PI);
					float thi = (float) ((random.nextFloat()-0.5)*2 * sigma);
					float[] xyz = new float[]{random.nextFloat(), random.nextFloat(), random.nextFloat()};
					float qMag = (float) Math.sqrt(Math.pow(xyz[0],2)+Math.pow(xyz[1],2)+Math.pow(xyz[2],2));
					xyz[0] /= qMag;
					xyz[1] /= qMag;
					xyz[2] /= qMag;
					//System.out.println(t+" "+i+" "+j+" "+xyz[0]+" "+ xyz[1]+" "+ xyz[2]+" "+thi+" "+theta);
					float w = (float) (thi * Math.cos(theta));
					xyz[0] = (float) (thi * Math.sin(theta) * xyz[0]);
					xyz[1] = (float) (thi * Math.sin(theta) * xyz[1]);
					xyz[2] = (float) (thi * Math.sin(theta) * xyz[2]);
					//System.out.println(j);
					//qrnn.lastDelta_[t][i][j] = new quaternions(0,0,0,0);
					qrnn.weights_[t][i][j] = new quaternions(xyz[0], xyz[1], xyz[2], w);

				}
			}
		}
	}
	
	static void normaliseInputs() {
		// TODO Auto-generated method stub
		System.out.println("there are "+qrnn.n+" numbers");
		
		qrnn.inputs = new quaternions[qrnn.n];
		for(int i=0;i<qrnn.n;i++){
			qrnn.inputs[i] = new quaternions(
					qrnn.rawInputs[i].sentiment,
					qrnn.rawInputs[i].price,
					qrnn.rawInputs[i].vix,
					0);
		}
	}
}
