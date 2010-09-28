# -*- coding: utf-8 -*-
import fnmatch, os, string
import sys
from csv import DictReader

def walk( root, recurse=False, pattern='*', return_folders=False ):
    # initialize
    result = []

    # must have at least root folder
    try:
        names = os.listdir(root)
    except os.error:
        return result

    # expand pattern
    pattern = pattern or '*'
    pat_list = string.splitfields( pattern , ';' )
    
    # check each file
    for name in names:
        fullname = os.path.normpath(os.path.join(root, name))

        # grab if it matches our pattern and entry type
        for pat in pat_list:
            if fnmatch.fnmatch(name, pat):
                if os.path.isfile(fullname) or \
                       (return_folders and os.path.isdir(fullname)):
                    result.append(fullname)
                continue
                
        # recursively scan other folders, appending results
        if recurse:
            if os.path.isdir(fullname) and not os.path.islink(fullname):
                result = result + walk( fullname, recurse, pattern,
                                        return_folders )
            
    return result


def max_utility(items):
    return max([float(item['utility_last']) for item in items])

def all_reports(base_dir):
    return walk(base_dir, True, 'outer.csv', False)

def best_utility(base_dir):
    files = all_reports(base_dir)
    best = (-999999.0, '-- ERROR --')
    for name in files:
        with open(name, 'r') as f:
            v = max_utility(DictReader(f))
            if v > best[0]:
                best = (v, name)
    return best
        

if __name__=='__main__':
    print "--- BEST RUN: %s\n%s" % best_utility(sys.argv[1])
