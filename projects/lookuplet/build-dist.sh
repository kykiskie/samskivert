#!/bin/sh
#
# $Id: build-dist.sh,v 1.1 2001/08/16 21:03:49 mdb Exp $
#
# Builds a distribution archive. This should be run from the top-level
# project directory and it will place the distribution archive into the
# directory identified by $DISTDIR.

USAGE="Usage: $0 distname (eg. lookuplet-1.4)"
DISTDIR=.

if [ -z "$1" ]; then
    echo $USAGE
    exit -1
else
    TARGET=$1
fi

# build our excludes file
cat > .excludes <<EOF
build-dist.sh
CVS
macros
.excludes
.cvsignore
*.tgz
*~
EOF

# create our distribution directory
mkdir /tmp/$TARGET

# temporarily move the distribution files into temp so that we can put
# them into a properly named directory
tar --exclude-from=.excludes -cf - * | tar -C /tmp/$TARGET -xf -

# now build the actual archive file
tar -C /tmp -czf $DISTDIR/$TARGET.tgz $TARGET

# and clean up after ourselves
rm -rf /tmp/$TARGET
rm .excludes
