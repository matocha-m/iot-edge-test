#!/usr/bin/python

import json
import sys

with open('module.json', 'r+') as f:
    data = json.load(f)
    data['image']['tag']['version'] = sys.argv[1] # <--- add `id` value.
    f.seek(0)        # <--- should reset file position to the beginning.
    json.dump(data, f, indent=2)
    f.truncate() 