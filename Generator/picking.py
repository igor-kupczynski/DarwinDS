#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Simple script for generating bicriteria picking problem instances.

The criteria are sum of values (max) and sum of weights (min). It
outputs the problem in mod format used by darwin method.

One can specify number of items, correlation type and max value. Also
weights for supposed utility function.

@author: Igor Kupczynski
"""

import random
import sys

NO_CORRELATION = 0
CORRELATIONS = (NO_CORRELATION, )


def generate_item(correlation, max_value):
    """
    Generate tuple (v, w) for single item
    """
    assert correlation in CORRELATIONS
    v = random.random() * max_value
    if correlation == NO_CORRELATION:
        w = random.random() * max_value
    return (v, w)
    

def generate(n, correlation, max_value):
    """
    Generate list of items
    """
    result = []
    for _ in xrange(n):
        result.append(generate_item(correlation, max_value))
    return result


def problem_decription(items, ut_weights):
    """
    Prepare problem description in mod format
    """
    out = []
    for idx in xrange(len(items)):
        out.append("var[(B) 0.0, 2.0] x%d;" % idx)
    out.append("")
    value = "max value: sum("
    weight = "min weight: sum("
    for idx, item in enumerate(items):
        value += "%f * x%d, "  % (item[0], idx)
        weight += "%f * x%d, " % (item[1], idx)
    value = value.rstrip(" ,") + ");"
    weight = weight.rstrip(" ,") + ");"
    out.append(value)
    out.append(weight)
    out.append("")
    assert(len(ut_weights) == 2)
    out.append("!dec: %f * value + %f * weight;" % ut_weights)
    return "\n".join(out)
    

def solve(items, ut_weights):
    """
    Finds optimum utility for DM
    """
    best = 0.0
    for item in items:
        u = item[0] * ut_weights[0] + item[1] * ut_weights[1]
        if (u > 0):
            best += u
    return best

def main():
    usage = """
    python %s <item-no> <weight-correlation> <max-value> <ut-fun-weight-for-value> <ut-veight-for-weight>
    python %s 10 0 100.0 1.0 -1.0
    """ % (sys.argv[0], sys.argv[0])

    try:
        n = int(sys.argv[1])
        correlation = int(sys.argv[2])
        max_val = float(sys.argv[3])
        ut_weights = (float(sys.argv[4]), float(sys.argv[5]))
    except Exception, e:
        print usage
        sys.exit(1)

    items = generate(n, correlation, max_val)
    print problem_decription(items, ut_weights)
    with open('best.txt', 'w') as f:
        f.write("best = %f" % solve(items, ut_weights))


if __name__ == '__main__':
    main()
