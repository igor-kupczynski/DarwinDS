#!/usr/bin/env python
# -*- encoding: utf-8 -*-

import sys

def f(j, n, M):
    factor = int(n // M)
    inner = []
    lb = (j-1) * factor + 1
    ub = j * factor
    for i in xrange(lb, ub+1):
        inner.append("x%d" % i)
    f_ = float(1) / factor
    return "%s * sum(%s)" % (f_, ", ".join(inner))

def prep_f(j, n, M):
    return "min f%d: %s + [i%d: 0, %d];" % (j, f(j, n, M), j, 2*(M+1-j))

    

def prep_g1(j, n, M):
    return "g%d: (%s) + 4 *(%s) -1 >= 0;" % (j, f(M, n, M), f(j, n, M))
    

def prep_g2(n, M):
    inner = []
    for i in xrange(1, M):
        for j in xrange(1, M):
            if i != j:
                inner.append("%s + %s" % (f(i, n, M), f(j, n, M)))
    return "g%d: 2 * (%s) + min(%s) -1 >= 0;" % (M, f(M, n, M), ", ".join(inner))


def main(args):
    n = int(args[1])
    M = int(args[2])
    result = []
    for i in xrange(1, n+1):
        result.append("var[0,1] x%d;" % i)
    result.append("")
    
    for i in xrange(1, M+1):
        result.append(prep_f(i, n, M))
    result.append("")

    for i in xrange(1, M):
        result.append(prep_g1(i, n, M))
    result.append(prep_g2(n, M))
    for i in xrange(1, n+1):
        result.append("nn%d: x%d >= 0;" % (i, i))
        result.append("ng%d: x%d <= 1;" % (i, i))
    result.append("")
    
    dec = ["(-%d * <f%d, 0.60>)" % (M-idx+1, idx) for idx in xrange(1, M+1)]
    dec.extend(["(-%d * <f%d, 0.30>)" % (2*(M-idx+1), idx) for idx in xrange(1, M+1)])
    result.append("!dec: %s;" % " + ".join(dec))
    result.append("")

    print "\n".join(result)
    
if __name__ == '__main__':
    main(sys.argv)
