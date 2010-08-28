#!/usr/bin/env python
# -*- encoding: utf-8 -*-

import math
import sys

def prep_g(xm):
    """e.g. xm = [5, 6, 7]"""
    inner = []
    for xi in xm:
        inner.append("(x%(xi)s - 0.5)*(x%(xi)s - 0.5) - cos(20 * %(pi)s * (x%(xi)s - 0.5))" % {'xi': xi, 'pi': math.pi})
    inner_sum = "sum( " + ", ".join(inner) + ")"
    return "100 * ( %d + %s )" % (len(xm), inner_sum)
    
def prep_f(idx, imax, xm):
    """f_idx = x_1 * x_2 * ...  * x_(imax-1) * (x_max + g(xm))"""
    inner = ["[i%d: 0.3, 0.7]" % idx]
    for i in xrange(1, imax):
        inner.append("x%d" % i)
    if idx != 1:
        inner.append("(1 - x%d)" % imax)
    inner.append("(1 + (%s))" % prep_g(xm))
    return "min f%d: %s;" % (idx, " * ".join(inner))

def main(args):
    m = int(args[1])
    k = int(args[2])
    result = []
    for i in xrange(1, m+k):
        result.append("var[0,1] x%d;" % i)
    result.append("")
    xm = range(m, m+k)
    for idx in xrange(1, m+1):
        result.append(prep_f(idx, m-idx+1, xm))
    result.append("")

    for i in xrange(1, m+k):
        result.append("nn%d: x%d >= 0;" % (i, i))
        result.append("ng%d: x%d <= 1;" % (i, i))
    result.append("")
    
    dec = ["(-%d * <f%d, 0.25>)" % (m-idx+1, idx) for idx in xrange(1, m+1)]
    result.append("!dec: %s;" % " + ".join(dec))
    
    print "\n".join(result)
        

if __name__ == '__main__':
    main(sys.argv)
