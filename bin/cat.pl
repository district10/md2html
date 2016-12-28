#!/usr/bin/perl

# Usage:
#       $ perl cat.pl INPUT_FILE > OUTPUT_FILE

use 5.010;
use strict;
use File::Basename;
use Cwd 'abs_path';

sub inList {
    my $needle = shift;
    my @haystack = @_;
    foreach my $hay (@haystack) {
        if ( $needle eq $hay ) { return 1; }
    }
    return 0;
}

sub unfold {
    my($PADDING)  = shift @_;
    my($FILENAME) = shift @_;
    my($FULLNAME) = abs_path($FILENAME);
    if (&inList($FULLNAME, @_) == 1) {
        print STDERR "ERROR! Loop detected: "."[$FULLNAME]-->"."{@_}"."\n";
        print      "\nERROR! Loop detected: "."[$FULLNAME]-->"."{@_}"."\n\n";
        return;
    }
    unshift(@_, $FULLNAME);

    open my $INPUT, '<', $FILENAME;
    while(<$INPUT>) {
        if (/^(\s*)\@include <-=(.*)=/) {
            &unfold($PADDING.$1, dirname($FILENAME)."/".$2, @_);
        } else {
            print $PADDING.$_;
        }
    }
}

&unfold("", $ARGV[0]);
