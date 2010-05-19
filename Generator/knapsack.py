#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Simple script for generating bicriteria knapsack problem instances.

One can specify number of items, correlation type, max value and
knapsack constraint. Also weights for supposed utility function.

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
    v1 = random.random() * max_value
    if correlation == NO_CORRELATION:
        v2 = random.random() * max_value
        w = random.random() * max_value
    return (v1, v2, w)
    

def generate(n, correlation, max_value):
    """
    Generate list of items
    """
    result = []
    for _ in xrange(n):
        result.append(generate_item(correlation, max_value))
    return result


def problem_decription_darwin(items, coefficient, ut_weights):
    """
    Prepare problem description in mod format
    """
    out = []
    sum = 0
    for idx in xrange(len(items)):
        out.append("var[(B) 0.0, 2.0] x%d;" % idx)
    out.append("")
    value1 = "max value1: sum("
    value2 = "max value2: sum("
    weight = "weight: sum("
    for idx, item in enumerate(items):
        value1 += "%f * x%d, "  % (item[0], idx)
        value2 += "%f * x%d, "  % (item[1], idx)
        weight += "%f * x%d, " % (item[2], idx)
        sum += item[2]
    value1 = value1.rstrip(" ,") + ");"
    value2 = value2.rstrip(" ,") + ");"
    weight = weight.rstrip(" ,") + ") <= %f;" % (coefficient * sum)
    out.append(value1)
    out.append(value2)
    out.append(weight)
    out.append("")
    assert(len(ut_weights) == 2)
    out.append("!dec: %f * value1 + %f * value2;" % ut_weights)
    return "\n".join(out)


def problem_decription_glpk(items, coefficient, ut_weights):
    """
    Prepare problem description in mod format
    """
    out = []
    sum = 0
    for idx in xrange(len(items)):
        out.append("var x%d binary;" % idx)
    out.append("")
    value1 = "("
    value2 = "("
    weight = "s.t. Weight : ("
    for idx, item in enumerate(items):
        value1 += "%f * x%d + "  % (item[0], idx)
        value2 += "%f * x%d + "  % (item[1], idx)
        weight += "%f * x%d + " % (item[2], idx)
        sum += item[2]
    value1 = value1.rstrip(" +") + ")"
    value2 = value2.rstrip(" +") + ")"
    goal = "maximize z: %f * %s + %f * %s;\n" \
        % (ut_weights[0], value1, ut_weights[1], value2)
    
    weight = weight.rstrip(" +") + ") <= %f;" % (coefficient * sum)

    out.append(goal)
    out.append(weight)
    out.append("end;\n")
    return "\n".join(out)


def main():
    usage = ("python %s <item-no> <weight-correlation> <max-value>" + 
    "<knapsack-constraint> <ut-fun-weight-for-value> " + 
    "<ut-veight-for-weight> <out-file>\n" + 
    "python %s 10 0 100.0 0.25 1.0 -1.0 kanpsack10") % (sys.argv[0], sys.argv[0])

    try:
        n = int(sys.argv[1])
        correlation = int(sys.argv[2])
        max_val = float(sys.argv[3])
        constraint = float(sys.argv[4])
        ut_weights = (float(sys.argv[5]), float(sys.argv[6]))
        fname = sys.argv[7]
    except Exception, e:
        print usage
        sys.exit(1)

    items = generate(n, correlation, max_val)
    desc = problem_decription_darwin(items, constraint, ut_weights)
    with open(fname + "_darwin.mod", "w") as f:
        f.write(desc)
    desc = problem_decription_glpk(items, constraint, ut_weights)
    with open(fname + "_glpk.mod", "w") as f:
        f.write(desc)
        
if __name__ == '__main__':
    main()
