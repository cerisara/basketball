package fr.xtof.basketball;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

// import ca.uol.aig.fftpack.RealDoubleFFT;

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

	private static short[] byte2shortSlow(byte[] buffer) {
		ByteBuffer bytes = ByteBuffer.wrap(buffer);
		ShortBuffer shorts = bytes.asShortBuffer();
		return shorts.array();
	}

	private static short[] byte2short(byte[] buf) {
		short[] res = new short[buf.length/2];
		for (int i=0;i<res.length;i++)
			res[i]=(short)(((buf[i+i+1] & 0xFF) << 8) | (buf[i+i] & 0xFF));
		return res;
	}

  public static void getFFT(byte[] buf0) {
	final int nFFT = 512;
	BasketTracker.main.msg("befconv");
	short[] buf=byte2short(buf0);
	BasketTracker.main.msg("aftconv");
	try {

		if (true) {
			PrintWriter ff = new PrintWriter(new FileWriter("/mnt/sdcard/tt.txt"));
			for (int i=0;i<buf.length;i++) ff.println(Short.toString(buf[i]));
			ff.close();
		}
		BasketTracker.main.msg("txtsaved");
		//RealDoubleFFT fft = new RealDoubleFFT(nFFT);


		// split into chunks of 10ms
		// we have 8000 short per second, so 10ms = 0.01s = 80 short
		// so we shift 80 short, but we compute the FFT over nFFT short
		double[] x = new double[nFFT];
		double[] y;
		PrintWriter f = new PrintWriter(new FileWriter("/mnt/sdcard/fft.txt"));
		int l=0;
		while (l+x.length<=buf.length) {
			for (int i=0;i<x.length;i++) {
				x[i]=(double)buf[l+i];
			}
			l+=80; // toutes les 20ms à 8kHz
			normaliza(x);
			if (true) {
				x = aplicaHamming(x);
				y = FFTMary.computePowerSpectrum(x);
				// fft.ft(x);
				y = normaliza(y);
				
				String s="";
				for (int i=0;i<y.length;i++) s+=Double.toString(y[i])+" ";
				f.println(s);
			}
		}
		f.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
  }

  int n, m;

  // Lookup tables. Only need to recompute when size of FFT changes.
  double[] cos;
  double[] sin;

  public FFT(int n) {
      this.n = n;
      this.m = (int) (Math.log(n) / Math.log(2));

      // Make sure n is a power of 2
      if (n != (1 << m))
          throw new RuntimeException("FFT length must be power of 2");

      // precompute tables
      cos = new double[n / 2];
      sin = new double[n / 2];

      for (int i = 0; i < n / 2; i++) {
          cos[i] = Math.cos(-2 * Math.PI * i / n);
          sin[i] = Math.sin(-2 * Math.PI * i / n);
      }
  }

  public void fft(double[] x, double[] y) {
      int i, j, k, n1, n2, a;
      double c, s, t1, t2;

      // Bit-reverse
      j = 0;
      n2 = n / 2;
      for (i = 1; i < n - 1; i++) {
          n1 = n2;
          while (j >= n1) {
              j = j - n1;
              n1 = n1 / 2;
          }
          j = j + n1;

          if (i < j) {
              t1 = x[i];
              x[i] = x[j];
              x[j] = t1;
              t1 = y[i];
              y[i] = y[j];
              y[j] = t1;
          }
      }

      // FFT
      n1 = 0;
      n2 = 1;

      for (i = 0; i < m; i++) {
          n1 = n2;
          n2 = n2 + n2;
          a = 0;

          for (j = 0; j < n1; j++) {
              c = cos[a];
              s = sin[a];
              a += 1 << (m - i - 1);

              for (k = j; k < n; k = k + n2) {
                  t1 = c * x[k + n1] - s * y[k + n1];
                  t2 = s * x[k + n1] + c * y[k + n1];
                  x[k + n1] = x[k] - t1;
                  y[k + n1] = y[k] - t2;
                  x[k] = x[k] + t1;
                  y[k] = y[k] + t2;
              }
          }
      }
  }

  public static void properWAV(byte[] clipData, int newRecordingID){
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
        os = new FileOutputStream(new File("/mnt/sdcard/a"+ newRecordingID+".wav"));
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

    } catch (IOException e) {
        e.printStackTrace();
    }

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


