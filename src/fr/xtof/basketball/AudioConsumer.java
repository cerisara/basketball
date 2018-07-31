package fr.xtof.basketball;

public interface AudioConsumer {
  /**
   * Data that has been recorded in the most recent sample and is ready for consumption.
   *
   * @param data      Buffer of audio data in raw form.
   * @param amplitude Amplitude from sample.
   * @param volume    Volume from sample.
   */
  void consume(byte[] data, double amplitude, double volume);
}


