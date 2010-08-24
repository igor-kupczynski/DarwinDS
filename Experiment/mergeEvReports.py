#!/usr/bin/env python
# -*- coding: utf-8 -*-

import csv
import sys

outs = set()
gens = set()
ranks = set()

def one_report_from_file(filename):
    global outs
    global gens
    global ranks
    r = csv.DictReader(open(filename))
    result = {}
    for item in r:
        out, gen, rank = int(item['outer']), int(item['generation']), int(item['rank'])
        outs.add(out)
        gens.add(gen)
        ranks.add(rank)
        if not out in result:
            result[out] = {}
        if not gen in result[out]:
            result[out][gen] = {}
        result[out][gen][rank] = float(item['utility'])
    return result


def merge_reports(args):
    result = {}
    for out in outs:
        result[out] = {}
        for gen in gens:
            result[out][gen] = {}
            for rank in ranks:
                result[out][gen][rank] = []
                for arg in args:
                    result[out][gen][rank].append(arg[out][gen][rank])
    return result


def aggregate(data, f):
    result = []
    for out in outs:
        for gen in gens:
            for rank in ranks:
                result.append((out, gen, rank, f(data[out][gen][rank])))
    return result

    

def main(args):
    foo = merge_reports([one_report_from_file(item) for item in args[1:-1]])
    bar = aggregate(foo, lambda x: float(sum(x)) / len(x))
    w = csv.writer(open(args[-1], 'w'))
    w.writerow(("outer","generation","rank","utility"))
    w.writerows(bar)

if __name__ == "__main__":
    main(sys.argv)
