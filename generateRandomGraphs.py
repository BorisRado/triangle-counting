from randomGraphGenerators import *

folder = "generated_graphs"
seeds = [i+1 for i in range(5)]

for seed in seeds:
    print("Seed:", seed)
    print(" Kronecker:")
    for scale in range(3, 18):
        print("  Scale:", scale)
        G = kroneckerGenerator(scale, 16, seed)
        saveKronecker(G, scale, f"kronecker_{scale}_16_{seed}", folder)
    print(" Barabasi-Albert")
    for c in [5, 10, 20]:
        print("  Avg degree:", 2*c)
        for p in range(2, 6):
            print("   Power", p)
            n = 10**p
            G = barabasiAlbert(n, c)
            saveGraph(G, f"barabasi_albert_{n}_{c}_{seed}", folder)
    print(" Regular lattice")
    for k in [1, 2, 3, 4]:
        print("  Param k:", k)
        for p in range(2, 6):
            print("   Power", p)
            n = 10**p
            G = regularLattice(n, k)
            saveGraph(G, f"lattice_{n}_{k}_{seed}", folder)