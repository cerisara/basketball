import matplotlib
import matplotlib.pyplot as plt
import numpy as np

fig, ax = plt.subplots()
with open("fft.txt","r") as f:
    for l in f:
        x = [float(w) for w in l.split()]
        t=np.arange(0,len(x),1)

        ax.plot(t,x)
        plt.show()

        prin("waiting...")
        plt.waitforbuttonpress(-1)

