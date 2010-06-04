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


def generate_item(correlation, cri, max_value):
    """
    Generate tuple (v, w) for single item
    """
    assert correlation in CORRELATIONS
    values = []
    values.append(random.random() * max_value)
    if correlation == NO_CORRELATION:
        for _ in xrange(cri):
            values.append(random.random() * max_value)
        values.append(random.random() * max_value)
    return values
    

def generate(n, cri, correlation, max_value):
    """
    Generate list of items
    """
    result = []
    for _ in xrange(n):
        result.append(generate_item(correlation, cri, max_value))
    return result


def problem_decription_darwin(items, cri, coefficient, ut_weights):
    """
    Prepare problem description in mod format
    """
    out = []
    sum = 0
    for idx in xrange(len(items)):
        out.append("var[(B) 0.0, 2.0] x%d;" % idx)
    out.append("")
    values = []
    for vIdx in xrange(cri):
        values.append("max value%d: sum(" % vIdx)
    weight = "weight: sum("
    for idx, item in enumerate(items):
        for vIdx in xrange(cri):
            values[vIdx] += ("%f * x%d, "  % (item[vIdx], idx))
        weight += "%f * x%d, " % (item[vIdx+1], idx)
        sum += item[vIdx+1]
    for vIdx in xrange(cri):
        values[vIdx] = values[vIdx].rstrip(" ,") + ");"
    weight = weight.rstrip(" ,") + ") <= %f;" % (coefficient * sum)
    for vIdx in xrange(cri):
        out.append(values[vIdx])
    out.append(weight)
    out.append("")
    last_line = "!dec: "
    for vIdx in xrange(cri):
        last_line += "%f * value%d + " % (ut_weights[vIdx], vIdx)
    last_line = last_line.rstrip(" +")
    last_line += ";"
    out.append(last_line)
    return "\n".join(out)


def problem_decription_glpk(items, cri, coefficient, ut_weights):
    """
    Prepare problem description in mod format
    """
    out = []
    sum = 0
    for idx in xrange(len(items)):
        out.append("var x%d binary;" % idx)
    out.append("")
    values = []
    for vIdx in xrange(cri):
        values.append("(")
    weight = "s.t. Weight : ("
    for idx, item in enumerate(items):
        for vIdx in xrange(cri):
            values.append("(")
            values[vIdx] +=  "%f * x%d + "  % (item[vIdx], idx)
        weight += "%f * x%d + " % (item[vIdx+1], idx)
        sum += item[vIdx+1]
    for vIdx in xrange(cri):
        values[vIdx] = values[vIdx].rstrip(" +") + ")"
    goal = "maximize z: "
    for vIdx in xrange(cri):
        goal += "%f * %s + " % (ut_weights[vIdx], values[vIdx])
    goal = goal.rstrip(" +") + ";\n"
    weight = weight.rstrip(" +") + ") <= %f;" % (coefficient * sum)

    out.append(goal)
    out.append(weight)
    out.append("end;\n")
    return "\n".join(out)


def main():
    usage = ("python %s <out-file> <item-no> <criteria-no> " +
             "<weight-correlation> <max-value>" + 
    "<knapsack-constraint> <ut-fun-weight-for-value> " + 
    "<ut-veight-for-weight>\n" + 
    "python %s kanpsack10 10 2 0 100.0 0.25 1.0 -1.0") % (sys.argv[0], sys.argv[0])

    try:
        fname = sys.argv[1]
        n = int(sys.argv[2])
        cri = int(sys.argv[3])
        correlation = int(sys.argv[4])
        max_val = float(sys.argv[5])
        constraint = float(sys.argv[6])
        ut_weights = []
        for idx in xrange(7, 7 + cri):
            ut_weights.append(float(sys.argv[idx]))
    except Exception, e:
        print e
        print usage
        sys.exit(1)

    items = generate(n, cri, correlation, max_val)
    desc = problem_decription_darwin(items, cri, constraint, ut_weights)
    with open(fname + "_darwin.mod", "w") as f:
        f.write(desc)
    desc = problem_decription_glpk(items, cri, constraint, ut_weights)
    with open(fname + "_glpk.mod", "w") as f:
        f.write(desc)
        
if __name__ == '__main__':
    main()
