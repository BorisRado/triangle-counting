from random import randrange, random, choice
from math import comb, floor, sqrt

# Create Erdos-Renyi random graph G(n,m) without multilinks
def Gnm(n,m):
    H = set()
    G = [[] for _ in range(n)]
    links = 0
    stop = comb(n, 2)
    while links < m:
        h = randrange(stop)
        if h not in H:
            H.add(h)
            i = 1 + floor(-0.5 + sqrt(0.25 + 2*h))
            j = h - comb(i, 2)
            G[i].append(j)
            G[j].append(i)
            links += 1
    return G

def price(n, c, a):
    G = [[i for i in range(c) if i != j] for j in range(c)]
    Q = [node for node in range(len(G)) for _ in range(len(G[node]))]
    for i in range(c, n):
        G.append([])
        for _ in range(c):
            if random() < c/(c+a):
                j = choice(Q)
            else:
                j = randrange(i)
            Q.append(j)
            G[i].append(j)
            G[j].append(i)
    return G

def barabasi_albert(n, c):
    return price(n, c, c)

def saveGraph(G, filename):
    with open(filename, "w") as f:
        f.write(f"*vertices {len(G)}\n")
        for i in range(len(G)):
            f.write(f'{i+1} "{i+1}"\n')
        m = 0
        for i in range(len(G)):
            for j in G[i]:
                if j>i:
                    m += 1
        f.write(f"*edges {m}\n")
        for i in range(len(G)):
            for j in G[i]:
                if j>i:
                    f.write(f"{i+1} {j+1}\n")