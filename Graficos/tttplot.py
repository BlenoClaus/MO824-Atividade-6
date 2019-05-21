#import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib as mpl
import seaborn as sn

data = pd.read_csv("tttplot.csv")

sn.set_context("talk")

#x = [-750000, -500000, -250000, 0, 250000, 500000, 750000]
#x_tick = ['-750 mil', '-500 mil', '-250 mil', "0", '250 mil', '500 mil', '750 mil']


plt.figure(figsize=(8, 5))
sn.set_style("whitegrid")

font = {'family' : 'Times New Roman',
        #'weight' : 'bold',
        'size'   : 13}

plt.rc('font',**font)

#plt.plot(data.GRASPT1,data.x,linewidth=0.7, label="GRASP")
#plt.plot(data.TST1,data.x,linewidth=0.7, label="TS")
#plt.plot(data.GAT1,data.x,linewidth=0.7, label="GA")
#plt.plot(data.GRASPT2,data.x,linewidth=0.7, label="GRASP")
#plt.plot(data.TST2,data.x,linewidth=0.7, label="TS")
#plt.plot(data.GAT2,data.x,linewidth=0.7, label="GA")
#plt.plot(data.GRASPT3,data.x,linewidth=0.7, label="GRASP")
#plt.plot(data.TST3,data.x,linewidth=0.7, label="TS")
#plt.plot(data.GAT3,data.x,linewidth=0.7, label="GA")

plt.scatter(data.GRASPT3,data.x,color='#FF6347',marker='_',linewidth=0.7, label="GRASP")
plt.scatter(data.TST3,data.x,color='#4169E1',marker='1',linewidth=0.7, label="TS")
plt.scatter(data.GAT3,data.x,color='#3CB371',marker='+',linewidth=0.7, label="GA")


plt.ylabel('Probabilidade (%)')
plt.xlabel('Tempo para o sub-ótimo (s)')
sn.despine(left=True, bottom=True)
plt.grid(False, axis='x')
#plt.yticks(x,x_tick)
plt.legend()
plt.savefig('ttt.eps', dpi=1500, transparent=True, bbox_inches='tight')
plt.show()
