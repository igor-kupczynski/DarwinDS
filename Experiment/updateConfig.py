#!/usr/bin/env python
# -*- encoding: utf-8 -*-
"""
Simple script for updating ini config file. It accepts base config
file and a "patch" file - list of changes that should be applied to
base config.

Outputs merged config file.
"""
import ConfigParser
import sys


def merge(base, patches):
    result = ConfigParser.RawConfigParser()
    for sec in base.sections():
        result.add_section(sec)
        for item, value in base.items(sec):
            if patches.has_option(sec, item):
                value = patches.get(sec, item)
                if not patches.remove_option(sec, item):
                    raise Exception("Not an option: %s -> %s" % (sec, item))
            result.set(sec, item, value)
    for sec in patches.sections():
        for item, value in patches.items(sec):
            raise Exception("Unused options %s -> %s" % (sec, item))
    return result

    
def main():
    usage = "%s <base-config>" % (sys.argv[0])
    if (len(sys.argv) != 2):
        print >> sys.stderr, usage
        sys.exit(-1)
    base = ConfigParser.RawConfigParser()
    base.read(sys.argv[1])
    patches = ConfigParser.RawConfigParser()
    patches.readfp(sys.stdin)
    result = merge(base, patches)
    result.write(sys.stdout)
        

if __name__ == "__main__":
    main()
