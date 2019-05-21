#import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib as mpl
import seaborn as sn

data = pd.read_csv("pp.csv")

sn.set_context("talk")

#x = [-750000, -500000, -250000, 0, 250000, 500000, 750000]
#x_tick = ['-750 mil', '-500 mil', '-250 mil', "0", '250 mil', '500 mil', '750 mil']


plt.figure(figsize=(8, 5))
sn.set_style("whitegrid")

font = {'family' : 'Times New Roman',
        #'weight' : 'bold',
        'size'   : 13}

plt.rc('font',**font)

plt.plot(data.GRASP,data.Probabilidade,color='#FF6347',linewidth=1, label="GRASP")
plt.plot(data.TS,data.Probabilidade,color='#4169E1',linewidth=1, label="TS")
plt.plot(data.GA,data.Probabilidade,color='#3CB371',linewidth=1, label="GA")
plt.plot(data.Model1,data.Probabilidade,color='#FFD700',linewidth=1, label="Model1")
plt.plot(data.Model2,data.Probabilidade,color='#4B0082',linewidth=1, label="Model2")

plt.ylabel('Probabilidade (%)')
#plt.xlabel('Tempo para o sub-ótimo (s)')
sn.despine(left=True, bottom=True)
plt.grid(False, axis='x')
#plt.yticks(x,x_tick)
plt.legend()
plt.savefig('pp.eps', dpi=1500, transparent=True, bbox_inches='tight')
plt.show()
