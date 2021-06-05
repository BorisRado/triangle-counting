import matplotlib.pyplot as plt
import numpy as np
import json

"""
Very initial version of the script that will be used for plotting.
"""
class Result:
    def __init__(self, result):
        self.algorithm = result["algorithm"]
        self.triangleCount = result["triangleCount"]
        self.executionTime = result["executionTime"]
        self.nodesCount = -1
        self.edgesCount = -1

    def addInfo(self, nodes, edges):
        self.nodesCount = nodes
        self.edgesCount = edges

class Algorithm:
    def __init__(self, name):
        self.name = name
        self.data = {}

    def addResult(self, graphName: str, numNodes: int, numEdges: int, result: Result):
        if graphName not in self.data:
            self.data[graphName] = {}
        if numNodes not in self.data[graphName]:
            self.data[graphName][numNodes] = []
        result.addInfo(numNodes, numEdges)
        self.data[graphName][numNodes].append(result)

    def getAverageTimes(self, graph):
        xs = sorted(i for i in self.data[graph])
        ys = []
        for x in xs:
            times = [res.executionTime for res in self.data[graph][x]]
            ys.append(round(sum(times) / len(times)))
        return xs, ys


def readResults(filename, algorithms=None):
    with open(filename, "r") as f:
        s = f.read()
        s = s.replace("\t", "")
        s = s.replace("\n", "")
        s = s.replace(",}", "}")
        s = s.replace(",]", "]")
        data = json.loads(s)

    if algorithms is None:
        algorithms = {}
    for graphRes in data:
        graphName = graphRes["graphName"].split("_")[0]
        nodesCount = graphRes["nodesCount"]
        edgesCount = graphRes["edgesCount"]
        for result in graphRes["results"]:
            alg = result["algorithm"]
            if alg not in algorithms:
                algorithms[alg] = Algorithm(alg)
            algorithms[alg].addResult(graphName, nodesCount, edgesCount, Result(result))
    return algorithms

def plotAverageTimes(algorithms, graphNames):
    for graphName in graphNames:
        fig, ax = plt.subplots()
        for alg in algorithms:
            xs, ys = algorithms[alg].getAverageTimes(graphName)
            ax.plot(xs, ys, label=alg)
        ax.set_xscale("symlog")
        ax.set_yscale("symlog")
        plt.legend()
        plt.title(f"Average times for {graphName} graph.")
        plt.xlabel("Number of nodes in $\log$ scale")
        plt.ylabel("Time in ms in $\log$ scale")
        plt.show()

def simpleReadResults(filename):
    with open(filename, "r") as f:
        s = f.read()
        s = s.replace("\t", "")
        s = s.replace("\n", "")
        s = s.replace(",}", "}")
        s = s.replace(",]", "]")
        data = json.loads(s)
    return data
    # nodes, rezs = [], {}
    # for graphRes in data:
    #     nodes.append(graphRes["nodesCount"])
    #     for result in graphRes["results"]:
    #         algorithm = result["algorithm"]
    #         if algorithm not in rezs:
    #             rezs[algorithm] = []
    #
    #         rezs[algorithm].append(result["executionTime"])
    # return nodes, rezs

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

def plotBarChart(filename, algorithms):
    plt.style.use("ggplot")
    fig, ax = plt.subplots(figsize = (18,10))
    bar_width = .10

    results = simpleReadResults(filename)
    x = np.arange(len(results))
    algorithmCount = len(algorithms)

    graphs = [g["graphName"] for g in results]
    for i, algo in enumerate(algorithms):
        algo_times = [tmp["avgExecutionTime"] for graphRes in results for tmp in graphRes["results"] if tmp["algorithm"] == algo]
        ses = [tmp["seExecutionTime"] for graphRes in results for tmp in graphRes["results"] if tmp["algorithm"] == algo]
        ax.bar(x + bar_width * (i - (algorithmCount - 1) / 2), algo_times, bar_width, label = algo, yerr = ses)
    ax.set_xticks(x)
    ax.set_xticklabels(graphs)
    box = ax.get_position()
    ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
    plt.legend(loc='center left', bbox_to_anchor=(1, 0.5))
    plt.ylabel("Execution time")
    # plt.tight_layout()
    plt.savefig("test.png", bbox_inches="tight")

if __name__ == "__main__":
    # nodes, results = readGraph("results.json")
    # nodes, results = orderArrays(nodes, results)
    # plotResults(nodes, results)
    # plotTimesByGraph("results.json")
    # algs = readResults("results.json")
    # plotAverageTimes(algs, ["barabasi", "kronecker", "lattice"])
    plotBarChart("results.json", ["Edge iterator", "Forward algorithm",
                "Compact Forward algorithm", "Cycle counting", "Sparse + Set Algorithm",
                "Neighbour pairs - single",
                "Sparse adjacency matrix search 1"])
