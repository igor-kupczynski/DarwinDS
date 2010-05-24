#!/usr/bin/env python
# -*- encoding: utf-8 -*-
"""
Grabs outer.csv from subdirectories and presents aggregated summary of
it.

@author: Igor Kupczynski
"""

import csv
import os
import sys

def parse_outer(f, test_name):
    """
    Return list of dicts constructed from given outer file
    """
    result = []
    reader = csv.DictReader(f)
    for item in reader:
        parsed = {
            'test': test_name,
            'outer': int(item['outer']),
            'min': float(item['utility_min']),
            'max': float(item['utility_max']),
            'last': float(item['utility_last'])
            }
        result.append(parsed)                  
    return result

def add_try_index(items, key):
    """
    Add try index to all items. Filter out attributes other then outer
    and last.
    """
    result = []
    size = len(items)
    for idx in xrange(size):
        item = items[idx]
        r = {'test': item['test'],
             'try': idx,
             'outer': item['outer'],
             'value': item[key]
             }
        result.append(r)
    return result

def merge_lists(lists, key):
    """
    Merge lists of item into list of items.
    """
    max_len = 0
    for l in lists:
        if len(l) > max_len:
            max_len = len(l)
    result = []
    for idx in xrange(max_len):
        row = []
        for l in lists:
            if idx < len(l):
                row.append(l[idx])
        result.extend(add_try_index(row, key))
    return result

def get_files_for_one_run(dirname):
    result = []
    testname = dirname.split('/')[-1]
    for sub in os.listdir(dirname):
        subdir = os.path.join(dirname, sub)
        if os.path.isdir(subdir):
            outer = os.path.join(subdir, 'reports', 'outer.csv')
            if os.path.isfile(outer):
                result.append((outer, testname))
    return result

def process_directory(dirname):
    files = get_files_for_one_run(dirname)
    lists = []
    for fname, testname in files:
        with open(fname) as f:
            l = parse_outer(f, testname)
            lists.append(l)
    merged = merge_lists(lists, 'last')
    with open(os.path.join(dirname, "summary.csv"), 'w') as out:
        writer = csv.DictWriter(out, ['test', 'try', 'outer', 'value'])
        writer.writerow({'test': 'test', 'try': 'try', 'outer': 'outer', 'value': 'value'})
        writer.writerows(merged)
    return merged
                

def main():
    if len(sys.argv) != 2:
        sys.stderr.write("Usage: %s <directory>\n" % sys.argv[0])
        sys.exit(-1)
    dirname = sys.argv[1]
    lists = []
    for sub in os.listdir(dirname):
        subdir = os.path.join(dirname, sub)
        if os.path.isdir(subdir):
            l = process_directory(subdir)
            lists.extend(l)
    with open(os.path.join(dirname, "summary.csv"), 'w') as out:
        writer = csv.DictWriter(out, ['test', 'try', 'outer', 'value'])
        writer.writerow({'test': 'test', 'try': 'try', 'outer': 'outer', 'value': 'value'})
        writer.writerows(lists)
        

if __name__ == "__main__":
    main()
