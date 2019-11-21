package fr.xtof.basketball;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class FFT {

	private static double[] normaliza(double[] datos) {
		double maximo = 0;
		for (int k = 0; k < datos.length; k++) {
			if (Math.abs(datos[k]) > maximo) {
				maximo = Math.abs(datos[k]);
			}
		}
		for (int k = 0; k < datos.length; k++) {
			datos[k] = datos[k] / maximo;
		}
		return datos;
	}

	private static double[] aplicaHamming(double[] datos) {
		final double A0 = 0.53836;
		final double A1 = 1. - A0;
		int Nbf = datos.length;
		for (int k = 0; k < Nbf; k++) {
			datos[k] = datos[k] * (A0 - A1 * Math.cos(2 * Math.PI * k / (Nbf - 1)));
		}
		return datos;
	}

	private static short[] byte2short(byte[] buf) {
		short[] res = new short[buf.length/2];
		for (int i=0;i<res.length;i++)
			res[i]=(short)(((buf[i+i+1] & 0xFF) << 8) | (buf[i+i] & 0xFF));
		return res;
	}

	// method that used to correctly computes a full FFT, but it's a bit slow on smartphones.
	// So I'm trying to replace it with simple ZCR
	// TODO: ajouter une courbe d'amplitude normalisee au ZCR pour ameliorer la qualite de reco ?
	public static float[] getFFT(byte[] buf0) {
		short[] x=byte2short(buf0);
		BasketTracker.main.setText("short ok");
		// TODO : Mean removal
		byte[] cross = new byte[x.length];
		int lastSign = 0; // pos
		for (int i=0;i<x.length;i++) {
			if (lastSign==0 && x[i] < 0) {
				cross[i] = 1;
				lastSign=1;
			} else if (lastSign==1 && x[i] > 0) {
				cross[i] = 1;
				lastSign=0;
			} else cross[i] = 0;
		}
		BasketTracker.main.setText("sign ok");
		int winlen = 160; // 20 ms pour calculer un ZCR
		int winshift = 80; // 10 ms between 2 frames
		float[] zcr = new float[(x.length-winlen+1)/winshift];
		for (int i=0;i<zcr.length;i++) {
			zcr[i]=0;
			for (int j=0;j<winlen;j++) zcr[i]+=(float)cross[j+i*winshift];
		}
		for (int i=0;i<zcr.length;i++) zcr[i]/=(float)winlen;
		BasketTracker.main.setText("counts ok");

		if (true) {
			try {
				// because we use a running estimate of ZCR, we don't need to split into chunks
				PrintWriter f = new PrintWriter(new FileWriter("/mnt/sdcard/zcr.txt"));
				for (int i=0;i<zcr.length;i++) f.println(Float.toString(zcr[i]));
				f.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return zcr;
	}

	public static String properWAV(byte[] clipData, int newRecordingID){
		final String fich = "/mnt/sdcard/a"+newRecordingID+".wav";
		try {
			long mySubChunk1Size = 16;
			int myBitsPerSample= 16;
			int myFormat = 1;
			long myChannels = 1;
			long mySampleRate = 8000;
			long myByteRate = mySampleRate * myChannels * myBitsPerSample/8;
			int myBlockAlign = (int) (myChannels * myBitsPerSample/8);

			long myDataSize = clipData.length;
			long myChunk2Size =  myDataSize * myChannels * myBitsPerSample/8;
			long myChunkSize = 36 + myChunk2Size;

			OutputStream os;
			os = new FileOutputStream(new File(fich));
			BufferedOutputStream bos = new BufferedOutputStream(os);
			DataOutputStream outFile = new DataOutputStream(bos);

			outFile.writeBytes("RIFF");                                 // 00 - RIFF
			outFile.write(intToByteArray((int)myChunkSize), 0, 4);      // 04 - how big is the rest of this file?
			outFile.writeBytes("WAVE");                                 // 08 - WAVE
			outFile.writeBytes("fmt ");                                 // 12 - fmt 
			outFile.write(intToByteArray((int)mySubChunk1Size), 0, 4);  // 16 - size of this chunk
			outFile.write(shortToByteArray((short)myFormat), 0, 2);     // 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
			outFile.write(shortToByteArray((short)myChannels), 0, 2);   // 22 - mono or stereo? 1 or 2?  (or 5 or ???)
			outFile.write(intToByteArray((int)mySampleRate), 0, 4);     // 24 - samples per second (numbers per second)
			outFile.write(intToByteArray((int)myByteRate), 0, 4);       // 28 - bytes per second
			outFile.write(shortToByteArray((short)myBlockAlign), 0, 2); // 32 - # of bytes in one sample, for all channels
			outFile.write(shortToByteArray((short)myBitsPerSample), 0, 2);  // 34 - how many bits in a sample(number)?  usually 16 or 24
			outFile.writeBytes("data");                                 // 36 - data
			outFile.write(intToByteArray((int)myDataSize), 0, 4);       // 40 - how big is this data chunk
			outFile.write(clipData);                                    // 44 - the actual data itself - just a long string of numbers

			outFile.flush();
			outFile.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return fich;

	}


	private static byte[] intToByteArray(int i)
	{
		byte[] b = new byte[4];
		b[0] = (byte) (i & 0x00FF);
		b[1] = (byte) ((i >> 8) & 0x000000FF);
		b[2] = (byte) ((i >> 16) & 0x000000FF);
		b[3] = (byte) ((i >> 24) & 0x000000FF);
		return b;
	}

	// convert a short to a byte array
	public static byte[] shortToByteArray(short data)
	{
		/*
		 * NB have also tried:
		 * return new byte[]{(byte)(data & 0xff),(byte)((data >> 8) & 0xff)};
		 * 
		 */

		return new byte[]{(byte)(data & 0xff),(byte)((data >>> 8) & 0xff)};
	}

}


