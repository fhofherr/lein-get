#!/bin/bash -e

## Make checkout dependencies for lein-get.

CURDIR=$PWD

if [ ! -e "$CURDIR/make_checkouts.sh" ]
then
    echo "make_checkouts.sh must be invoked from the project root directory"
    exit 1
fi

cd $CURDIR/lein-get-core
lein install

cd $CURDIR/lein-get
mkdir -p checkouts
ln -s $CURDIR/lein-get-core checkouts/lein-get-core
lein install
