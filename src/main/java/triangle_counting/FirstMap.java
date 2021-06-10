package triangle_counting;

public class FirstMap {

    int a;
    int b;
    int c;
    int u;
    int v;

    FirstMap(int a, int b, int c, int u, int v) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.u = u;
        this.v = v;

    }

    public String getRepr() {
        return a + "S" + b+ "S"+ c;
    }
}
