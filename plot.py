import matplotlib.pyplot as plt
import numpy as np
import json

plt.style.use("ggplot")

"""
Script for plotting.
"""

FIG_SIZE = (18, 10)
IMAGES_FOLDER = "images"

def simpleReadResults(filename):
    with open(filename, "r") as f:
        s = f.read()
        s = s.replace("\t", "")
        s = s.replace("\n", "")
        s = s.replace(",}", "}")
        s = s.replace(",]", "]")
        data = json.loads(s)
    return data

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
        print(algo)
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
    bar_width = .09

    graph_names, _, all_algo_times, all_ses, results = get_ordered_results(filename, algorithms, sort = False)
    x = np.arange(len(results))
    algorithmCount = len(algorithms)
    for i, algorithm in enumerate(algorithms):
        algo_times = all_algo_times[algorithm]
        print(algorithm, " ", algo_times)
        ses = all_ses[algorithm]
        if i+ 1 == len(algorithms):
            ax.bar(x + bar_width * (i - (algorithmCount - 1) / 2), algo_times, bar_width, label = algorithm,
                        yerr = ses, color = "green")
        elif i+ 2 == len(algorithms):
            ax.bar(x + bar_width * (i - (algorithmCount - 1) / 2), algo_times, bar_width, label = algorithm,
                        yerr = ses, color = "purple")
        else:
            ax.bar(x + bar_width * (i - (algorithmCount - 1) / 2), algo_times, bar_width, label = algorithm, yerr = ses)
    ax.set_xticks(x)
    ax.set_xticklabels(graph_names)
    box = ax.get_position()
    ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
    plt.legend(fontsize = 17)
    ax.tick_params(labelsize = 16)
    plt.ylabel("Execution time", fontsize = 16)
    plt.yscale("log")
    plt.tight_layout()
    if figname is None:
        plt.show()
    else:
        plt.savefig(f"{IMAGES_FOLDER}/{figname}")

def plot_wrt_n(filename, algorithms, figname = None):
    results = simpleReadResults(filename)
    graph_names, ns, all_algo_times, all_ses, results = get_ordered_results(filename, algorithms, sort = True)

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

    fig, ax = plt.subplots(figsize = FIG_SIZE)
    for i, algorithm in enumerate(algorithms):
        if i + 1 == len(algorithms):
            ax.errorbar(ns, all_algo_times[algorithm], label = algorithm, color = "green")
        else:
            ax.errorbar(ns, all_algo_times[algorithm], label = algorithm)#, yerr = new_sds[algorithm])
    ax.tick_params(labelsize = 16)
    plt.ylabel("Execution time", fontsize = 16)
    plt.xlabel("Nodes count", fontsize = 16)

    plt.legend(fontsize = 17)
    plt.tight_layout()
    if figname is None:
        plt.show()
    else:
        plt.savefig(f"{IMAGES_FOLDER}/{figname}")

if __name__ == "__main__":
    algos = ["Edge-iterator", "Forward algorithm",
                "Compact Forward algorithm",
                "DLDV", "DLDV+", "Node iterator",
                "Sparse matrix with Hadamard product", "Sparse and Set Algorithm"]
    # plotBarChart("results.json", algos)
    plot_wrt_n(".results.json", algos)
