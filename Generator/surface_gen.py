#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Module for generating a problem

min x
min y
min z
s.t.
-ln(x)-1 >= 0
x + z - 0.5 >= 0
x + y + z - 0.8 >= 0

@author: Igor Kupczynski
"""

import sys

def problem_description(xw, yw, zw):
    """
    Prepare problem description in mod format
    """
    out = []
    for var in "xyz":
        out.append("var[-2.0, 2.0] %s;" % var)
    out.append("")
    out.append("min f1: x;")
    out.append("min f2: y;")
    out.append("min f3: z;")
    out.append("")
    out.append("c1: -ln(x) - 1 >= 0;")
    out.append("c2: x + z - 0.5 >= 0;")
    out.append("c3: x + y + z - 0.8 >= 0;")
    out.append("l1a: x >= 0;")
    out.append("l2a: y >= 0;")
    out.append("l3a: z >= 0;")
    out.append("l1b: x <= 1;")
    out.append("l2b: y <= 1;")
    out.append("l3b: z <= 1;")
    out.append("")
    out.append("!dec: %f * f1 + %f * f2 * + %f * f3;" % (xw, yw, zw))
    return "\n".join(out)


def main():
    usage = """
    python %s <x-weight> <y-weight> <z-weight>
    python %s 1.0 2.0 1.0
    """ % (sys.argv[0], sys.argv[0])

    try:
        xw, yw, zw = float(sys.argv[1]), float(sys.argv[2]), float(sys.argv[3])
    except:
        print usage
        sys.exit(1)

    print problem_description(xw, yw, zw)

    
if __name__ == '__main__':
    main()
