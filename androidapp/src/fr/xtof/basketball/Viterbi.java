package fr.xtof.basketball;

public class Viterbi {
	private final static float scnull = -9999;
	private final static float cins = 1.1f;

	private static float dist(float a, float b) {
		float d=a-b;
		return d*d;
	}
	public static float viterbi(float[] x, float[] y) {
		float[][] sc = new float[2][y.length+1]; // le 0 correspond à l'etat initial, avant les obs
		for (int i=1;i<sc[0].length;i++) sc[0][i]=scnull;
		sc[0][0]=0;
		int cur = 1;
		for (int t=1;t<=x.length;t++) {
			sc[cur][0] = sc[1-cur][0] + cins;
			for (int j=1;j<=y.length;j++) {
				// allowed transitions = all previous states
				// the current cost is dist(x[t-1],y[j])
				// trans costs = insertion cost * (j-1-k)
				// try jump from 0 (skip all previous y states)
				sc[cur][j] = sc[1-cur][0] + (j-1-0)*cins;
				for (int k=1;k<j;k++) {
					if (sc[1-cur][k]==scnull) break;
					float v = sc[1-cur][k] + (j-1-k)*cins;
					if (v<sc[cur][j]) sc[cur][j]=v;
				}
				sc[cur][j] += dist(x[t-1],y[j-1]);
			}
			/*
			for (int k=0;k<sc[cur].length;k++)
				System.out.print(Float.toString(sc[cur][k])+" ");
			System.out.println();
			*/
			cur=1-cur;
		}
		return sc[1-cur][y.length];
	}

	public static void main(String args[]) {
		float[] a = {1f,2f,1f};
		float[] b = {2f,1f};
		float[] c = {2f,3f,3f};

		System.out.println(viterbi(a,b));
		System.out.println(viterbi(a,c));
	}
}

