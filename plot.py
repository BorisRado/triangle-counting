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

def plotTimesByGraph(filename):
    '''Works for graphs created by `generateRandomGraphs`.'''
    with open(filename, "r") as f:
        s = f.read()
        s = s.replace("\t", "")
        s = s.replace("\n", "")
        s = s.replace(",}", "}")
        s = s.replace(",]", "]")
        data = json.loads(s)

        # nodes, rezs = [], {}
        graphs = ["barabasi", "kronecker", "lattice"]
        for graph in graphs:
            rezs = {}
            divisors = {}
            for graphRes in data:
                if graphRes["graphName"].split("_")[0] != graph: continue
                if graphRes["nodesCount"] not in rezs:
                    rezs[graphRes["nodesCount"]] = {}
                    divisors[graphRes["nodesCount"]] = 0
                rezi = rezs[graphRes["nodesCount"]]
                divisors[graphRes["nodesCount"]] += 1
                for result in graphRes["results"]:
                    algo = result["algorithm"]
                    if algo not in rezi:
                        rezi[algo] = 0
                    rezi[algo] += result["executionTime"]
            for rez in rezs:
                for algo in rezs[rez]:
                    rezs[rez][algo] /= divisors[rez]
            
            fig, ax = plt.subplots()
            key = list(rezs.keys())[0]
            algs = {alg for alg in rezs[key].keys()}
            xs = [n for n in rezs]
            order = np.array(xs).argsort()
            xs = np.array(xs)[order]
            for alg in algs:
                ys = np.array([rezs[n][alg] for n in rezs])[order]
                ax.plot(xs, ys, label=alg)
            ax.set_xscale("log")
            ax.set_yscale("log")
            plt.legend()
            plt.title(f"Average times for {graph} graph.")
            plt.xlabel("Number of nodes in $\log$ scale")
            plt.ylabel("Time in ms")
            plt.show()

if __name__ == "__main__":
    nodes, results = readGraph("results.json")
    nodes, results = orderArrays(nodes, results)
    plotResults(nodes, results)
    # plotTimesByGraph("results.json")