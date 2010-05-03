#!/usr/bin/env python
# -*- encoding: utf-8 -*-
"""
Quick and dirty script for running test plan.

@author: Igor Kupczynski

Testplan file format:
>test name/repetitions
patches to base config

eg.

>60gen/3
[main]
generationCount = 60
>45gen/3
[main]
generationCount = 45


means two test - first one with 60 generations, second one with 45
gens. Both repeated 3 times.

To invoke the script do:
./runTestPlan.py darwin-dir problem-file base-config test-plan
"""
import datetime
import os
import subprocess
import sys
import StringIO


def get_args():
    try:
        return (sys.argv[1], sys.argv[2], sys.argv[3], open(sys.argv[4], 'r'))
    except:
        print >> sys.stderr, \
            "Usage: %s darwin-dir problem-file base-config test-plan" % \
            sys.argv[0]
        sys.exit(-1)
        
def parse_testplan(lines):
    result = []
    item = None
    for line in lines:
        if line.startswith('>'):
            if item is not None:
                result.append(item)
            item = {}
            name, reps = line.split('/')
            item['name'] = name[1:]
            item['reps'] = int(reps)
            item['config-patch'] = StringIO.StringIO()
        else:
            item['config-patch'].write(line)
    return result

    
def main():
    (darwin_dir, problem_file, base_config, test_plan) = get_args()
    plan = parse_testplan(test_plan)
    now = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
    os.mkdir(now)
    for item in plan:
        tmp = open("tmp.ini", "w")
        p = subprocess.Popen(['python', 'updateConfig.py', base_config], stdin=subprocess.PIPE, stdout=tmp)
        p.communicate(item['config-patch'].getvalue())
        tmp.close()
        for idx in xrange(item['reps']):
            p = subprocess.Popen(['bash', 'runOnce.sh', darwin_dir, 'tmp.ini',
                                 problem_file, "%s/%s_%d" % (now, item['name'], idx)]
                                 )
            p.communicate()
        os.remove("tmp.ini")
    


if __name__ == "__main__":
    main()
