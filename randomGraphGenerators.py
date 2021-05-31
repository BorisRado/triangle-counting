from random import Random
from math import comb, floor, sqrt, log
from os import path

# Create Erdos-Renyi random graph G(n,m) without multilinks
def Gnm(n,m,seed=0):
    rand = Random(seed)
    H = set()
    G = [[] for _ in range(n)]
    links = 0
    stop = comb(n, 2)
    while links < m:
        h = rand.randrange(stop)
        if h not in H:
            H.add(h)
            i = 1 + floor(-0.5 + sqrt(0.25 + 2*h))
            j = h - comb(i, 2)
            G[i].append(j)
            G[j].append(i)
            links += 1
    return G

def price(n, c, a, seed=0):
    rand = Random(seed)
    G = [[i for i in range(c) if i != j] for j in range(c)]
    Q = [node for node in range(len(G)) for _ in range(len(G[node]))]
    for i in range(c, n):
        G.append([])
        for _ in range(c):
            if rand.random() < c/(c+a):
                j = rand.choice(Q)
            else:
                j = rand.randrange(i)
            Q.append(j)
            G[i].append(j)
            G[j].append(i)
    return G

def barabasiAlbert(n, c, seed=0):
    return price(n, c, c, seed)

def regularLattice(n, k=2):
    G = [None] * n
    for i in range(n):
        G[i] = [(i+j)%n for j in [-(l+1) for l in range(k)] + [l+1 for l in range(k)]]
    return G

def kroneckerGenerator(scale, edgefactor, seed=0):
    '''Generate an edgelist according to Graph500 parameters.'''
    rand = Random(seed)
    N = 2**scale # number of vertices
    M = edgefactor * N # number of edges

    # Set initial probabilities
    A = 0.57; B = 0.19; C = 0.19

    # Index arrays
    # ij = [[1,1] for _ in range(M)]
    ij = [[1,1] for _ in range(M)]
    
    # Loop over each order of bit
    ab = A+B
    c_norm = C / (1-ab)
    a_norm = A/ab

    for ib in range(1,scale+1):
        # Compare with probabilities and set bits of indices
        for i in range(M):
            ii_bit = rand.random() > ab
            jj_bit = rand.random() > (c_norm * ii_bit + a_norm * (not ii_bit))
            f = 2**(ib-1)
            ij[i] = [ij[i][0] + f*ii_bit, ij[i][1] + f*jj_bit]
    
    # Permute vertex labels
    Ns = [i for i in range(N)]
    rand.shuffle(Ns)
    for idx in range(M):
        i,j = ij[idx]
        ij[idx] = [Ns[i-1], Ns[j-1]]

    # Permute the edge list
    rand.shuffle(ij)

    return ij

def simplify(G):
    '''Simplify graph G ... remove self-loops and multi-links.'''
    G1 = [set(i) for i in G]
    for i in range(len(G1)):
        if i in G1[i]:
            G1[i].remove(i)
    return G1

def simplifyKronecker(G, scale):
    '''Simplify  Kronecker graph G (edgelist) ... remove self-loops and multi-links.'''
    G1 = [set() for _ in range(2**scale)]
    for i,j in G:
        if i != j:
            G1[i].add(j)
            G1[j].add(i)
    return G1

def saveKronecker(G, scale, filename, folder="data", toSimplify=True):
    if toSimplify:
        G = simplifyKronecker(G, scale)
    with open(path.join(folder, f"{filename}.net"), "w") as f:
        f.write(f"*vertices {len(G)}\n")
        for i in range(len(G)):
            f.write(f'{i+1} "{i+1}"\n')
        f.write(f"*edges {sum([len(i) for i in G]) // 2}\n")
        for i in range(len(G)):
            for j in G[i]:
                if j>i: f.write(f"{i+1} {j+1}\n")

def saveGraph(G, filename, folder="data", toSimplify=True):
    '''Saves a simple graph G.'''
    if toSimplify:
        G = simplify(G)
    with open(path.join(folder, f"{filename}.net"), "w") as f:
        f.write(f"*vertices {len(G)}\n")
        for i in range(len(G)):
            f.write(f'{i+1} "{i+1}"\n')
        f.write(f"*edges {sum(len(i) for i in G) // 2}\n")
        for i in range(len(G)):
            for j in G[i]:
                if j>i:
                    f.write(f"{i+1} {j+1}\n")