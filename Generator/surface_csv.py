#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Module for generating a chart for a problem:

min x
min y
min z
s.t.
-ln(x)-1 >= 0
x + z - 0.5 >= 0
x + y + z - 0.8 >= 0


R script to see the chart:
dd <- read.csv("out.csv")
library(tcltk)
library(TeachingDemos)
rotate.cloud(dd$z ~ dd$x * dd$y)

@author: Igor Kupczynski
"""

from math import log

def test(f, x, y, z):
    """f(x,y,z) >= 0.0"""
    return f(x,y,z) >= 0

def c1(x, y, z):
    """ln(x) - 1"""
    try:
        res = -log(x) - 1
    except ValueError:
        return 1.0
    return res

def c2(x, y, z):
    """x + z - 0.5"""
    res = x + y - 0.5
    return res

def c3(x, y, z):
    """x + y + z - 0.8"""
    res = x + y + z - 0.8
    return res

def test_all(x, y, z):
    for f in (c1, c2, c3):
        if not test(f, x, y, z):
            return False
    return True

def main():
    res = {}
    for x in xrange(0, 101):
        for y in xrange(0, 101):
            item = None
            for z in xrange(0, 101):
                a, b, c = float(x)/100, float(y)/100, float(z)/100
                if test_all(a, b, c):
                    item = c
                    break
            if item != None:
                res[(a,b)] = c
    items = []
    for (x,y), z in res.iteritems():
        items.append(((x,y), z))
    items.sort()
    with open("/home/puszczyk/tmp/out.csv", "w") as f:
        f.write("x,y,z\n")
        for (x,y), z in items:
            f.write("%f,%f,%f\n" % (x, y, z))

if __name__ == "__main__":
    main()
                            
