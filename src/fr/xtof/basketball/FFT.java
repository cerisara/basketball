package fr.xtof.basketball;

import java.io.*;

public class FFT {

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

  public static void properWAV(byte[] clipData, float newRecordingID){
    try {
        long mySubChunk1Size = 16;
        int myBitsPerSample= 16;
        int myFormat = 1;
        long myChannels = 1;
        long mySampleRate = 16000;
        long myByteRate = mySampleRate * myChannels * myBitsPerSample/8;
        int myBlockAlign = (int) (myChannels * myBitsPerSample/8);

        long myDataSize = clipData.length;
        long myChunk2Size =  myDataSize * myChannels * myBitsPerSample/8;
        long myChunkSize = 36 + myChunk2Size;

        OutputStream os;        
        os = new FileOutputStream(new File("/mnt/sdcard/OneFileAudio"+ newRecordingID+".wav"));
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


