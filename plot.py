import matplotlib.pyplot as plt
import numpy as np
import json

print(plt.style.available)
plt.style.use("ggplot")

"""
Very initial version of the script that will be used for plotting.
"""

FIG_SIZE = (18, 10)
IMAGES_FOLDER = "images"

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

def get_ordered_results(filename, algorithms, sort = False):
    # returns a tuple containing
    # 1. list containing file names
    # 2. list containing the number of nodes of each file (graph)
    # 3. a dictionary, containing the times for each algorithm
    #    each algorithm in algorithms get its own list
    # 4. Standard errors
    # 5. the actual results
    # if sort = True, the results are ordered in ascending order wrt number of nodes
    results = simpleReadResults(filename)
    graph_names = np.array([g["graphName"] for g in results])
    ns = np.array([g["nodesCount"] for g in results])
    algo_times, ses = {}, {}
    for i, algo in enumerate(algorithms):
        algo_times[algo] = np.array([tmp["avgExecutionTime"] for graphRes in results
                            for tmp in graphRes["results"] if tmp["algorithm"] == algo])
        ses[algo] = np.array([tmp["seExecutionTime"] for graphRes in results
                            for tmp in graphRes["results"] if tmp["algorithm"] == algo])


    if sort == True:
        order = np.argsort(np.array(ns))
        ns = ns[order]
        graph_names = graph_names[order]
        for algorithm in algorithms:
            algo_times[algorithm] = algo_times[algorithm][order]
            ses[algorithm] = ses[algorithm][order]

    return graph_names, ns, algo_times, ses, results

def plotBarChart(filename, algorithms, figname = None):
    fig, ax = plt.subplots(figsize = FIG_SIZE)
    bar_width = .10

    graph_names, _, all_algo_times, all_ses, results = get_ordered_results(filename, algorithms, sort = False)
    x = np.arange(len(results))
    algorithmCount = len(algorithms)
    for i, algorithm in enumerate(algorithms):
        algo_times = all_algo_times[algorithm]
        ses = all_ses[algorithm]
        ax.bar(x + bar_width * (i - (algorithmCount - 1) / 2), algo_times, bar_width, label = algorithm, yerr = ses)
    ax.set_xticks(x)
    ax.set_xticklabels(graph_names)
    box = ax.get_position()
    ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
    plt.legend(loc='center left', bbox_to_anchor=(1, 0.5))
    plt.ylabel("Execution time")
    plt.yscale("log")
    # plt.tight_layout()
    if figname is None:
        plt.show()
    else:
        plt.savefig(f"{IMAGES_FOLDER}/{figname}")

def plot_wrt_n(filename, algorithms, figname = None):
    results = simpleReadResults(filename)
    graph_names, ns, all_algo_times, all_ses, results = get_ordered_results(filename, algorithms, sort = True)
    print(ns)
    print(all_algo_times["Edge iterator"])


    unique_n = np.unique(ns)
    new_n, new_algo_times = np.empty((unique_n.shape[0])), {algo: np.empty((unique_n.shape[0])) for algo in algorithms}
    new_sds = {algo: np.empty((unique_n.shape[0])) for algo in algorithms}
    for i, n in enumerate(np.unique(ns)):
        new_n[i] = n
        for algo in new_algo_times:
            new_algo_times[algo][i] = np.mean(all_algo_times[algo][ns == n])
            new_sds[algo][i] = np.std(all_algo_times[algo][ns == n])
    ns = new_n
    all_algo_times = new_algo_times
    print(all_algo_times["Edge iterator"])

    fig, ax = plt.subplots(figsize = FIG_SIZE)
    for algorithm in algorithms:
        ax.errorbar(ns, all_algo_times[algorithm], label = algorithm)#, yerr = new_sds[algorithm])
    plt.legend()
    if figname is None:
        plt.show()
    else:
        plt.savefig(f"{IMAGES_FOLDER}/{figname}")


if __name__ == "__main__":
    # nodes, results = readGraph("results.json")
    # nodes, results = orderArrays(nodes, results)
    # plotResults(nodes, results)
    # plotTimesByGraph("results.json")
    # algs = readResults("results.json")
    # plotAverageTimes(algs, ["barabasi", "kronecker", "lattice"])

    # one
    plotBarChart("results.json", ["Edge iterator", "Forward algorithm",
                "Compact Forward algorithm", "Cycle counting",
                "Neighbour pairs - single", "Node iterator",
                "Sparse adjacency matrix search 1"])
    # plot_wrt_n("results.json", ["Edge iterator",
    #             "Compact Forward algorithm", "Cycle counting", "Node iterator",
    #             "Neighbour pairs - single", "Sparse adjacency matrix search 1"])
