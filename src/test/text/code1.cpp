#include <vector>
#include <cstdio>

using namespace std;

#define max(a,b) ((a)>(b)?(a):(b))

double dp(int n) {
    vector<double> f(max(3,n+1));
    f[0] = 0.0;
    f[1] = 1.0;
    f[2] = 1.0;
    for (int i = 3; i <= n; ++i) {
        double p = 0.0;
        for (int j = 0; j <= i-3; ++j) {
            p += (1.0 + f[j] + f[i - 3 - j]);
        }
        p += 2.0 * (1 + f[i - 2]);
        f[i] = p/(double)i;
    }
    return f[n];
}

int main()
{
    while( 1 == scanf("%d", &n) ) {
        printf("%lf\n", dp(n));
    }
}
