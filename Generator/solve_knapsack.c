#include <stdio.h>
#include <stdlib.h>
#include <math.h>

double *values;
double *weights;

double uv, uw;
int items;

double weightedSum(double* values, int length, long mask) {
  double result = 0;
  int i;
  for (i = 0; i < length; i++) {
    result += (values[i] * (mask & 1));
    mask = mask >> 1;
  }
  return result;
}

int main(void) {
  scanf("%d", &items);
  scanf("%lf %lf", &uv, &uw);
  values = (double *) malloc(sizeof(double) * items);
  weights = (double *) malloc(sizeof(double) * items);
  int i;
  for (i = 0; i < items; i++) {
    scanf("%lf %lf", values+i, weights+i);
  }

  long max_val = pow(2, items);
  printf("2^%d = %ld\n", items, max_val);
  long solution;
  double v, w, u, bestv = 0, bestw = 0, bestu = 0;
  for (solution = max_val-1; solution >= 0; solution--) {
    v = weightedSum(values, items, solution);
    w = weightedSum(weights, items, solution);
    u = uv * v + uw * w;
    if (u > bestu) {
      bestu = u;
      bestv = v;
      bestw = w;
      printf("%ld::best for (%lf, %lf) = %lf\n", solution, v, w, u);
      fflush(stdout);
      fflush(stdout);
    }
  }
  printf("--- END OF RUN\nbest for (%lf, %lf) = %lf\n", bestv, bestw, bestu);

  return 0;
}
