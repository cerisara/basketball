import matplotlib
import matplotlib.pyplot as plt
import numpy as np

fig, ax = plt.subplots()
plt.ion()
plt.show()

with open("fft.txt","r") as f:
    for l in f:
        x = [float(w) for w in l.split()]
        print(x[0:10])
        t=np.arange(0,len(x),1)

        ax.plot(t,x)
        plt.draw()
        plt.pause(0.001)

        plt.waitforbuttonpress(-1)
        plt.cla()
