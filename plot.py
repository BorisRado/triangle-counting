import matplotlib.pyplot as plt
import numpy as np
import json

"""
Very initial version of the script that will be used for plotting.
"""

def readGraph(filename):
    with open(filename, "r") as f:
        s = f.read()
        s = s.replace("\t", "")
        s = s.replace("\n", "")
        s = s.replace(",}", "}")
        s = s.replace(",]", "]")
        data = json.loads(s)

    nodes, rezs = [], {}
    for graphRes in data:
        nodes.append(graphRes["nodesCount"])
        for result in graphRes["results"]:
            algorithm = result["algorithm"]
            if algorithm not in rezs:
                rezs[algorithm] = []

            rezs[algorithm].append(result["executionTime"])
    return nodes, rezs

def orderArrays(nodes, results):
    order = np.array(nodes).argsort()
    nodes = np.array(nodes)[order]

    for algo in results:
        results[algo] = np.array(results[algo])[order]
    return nodes, results

def plotResults(nodes, results):
    fig, ax = plt.subplots()
    for algo, times in results.items():
        ax.plot(nodes, times, label = algo)
    plt.legend()
    plt.show()

if __name__ == "__main__":
    nodes, results = readGraph("results.json")
    nodes, results = orderArrays(nodes, results)
    plotResults(nodes, results)
